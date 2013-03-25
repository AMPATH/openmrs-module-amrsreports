package org.openmrs.module.amrsreports.reporting.data.evaluator;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.OpenmrsObject;
import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.cache.MohCacheUtils;
import org.openmrs.module.amrsreports.reporting.data.DateARTStartedDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.EligibilityForARTDataDefinition;
import org.openmrs.module.amrsreports.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreports.rule.util.MohRuleUtils;
import org.openmrs.module.amrsreports.snapshot.ARVPatientSnapshot;
import org.openmrs.module.amrsreports.snapshot.DateARTStartedPatientSnapshot;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Evaluator for ART Eligibility
 */
@Handler(supports=DateARTStartedDataDefinition.class, order=50)
public class DateARTStartedDataEvaluator implements PersonDataEvaluator {

	@Override
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {

		List<OpenmrsObject> questionConcepts = Arrays.<OpenmrsObject>asList(
				MohCacheUtils.getConcept(MohEvaluableNameConstants.ANTIRETROVIRAL_DRUG_TREATMENT_START_DATE),
				MohCacheUtils.getConcept(MohEvaluableNameConstants.ANTIRETROVIRAL_PLAN),
				MohCacheUtils.getConcept(MohEvaluableNameConstants.REASON_ANTIRETROVIRALS_STARTED),
				MohCacheUtils.getConcept(MohEvaluableNameConstants.PATIENT_REPORTED_REASON_FOR_CURRENT_ANTIRETROVIRALS_STARTED),
				MohCacheUtils.getConcept(MohEvaluableNameConstants.NEWBORN_ANTIRETROVIRAL_USE),
				MohCacheUtils.getConcept(MohEvaluableNameConstants.NEWBORN_PROPHYLACTIC_ANTIRETROVIRAL_USE)
		);


		DateARTStartedDataDefinition def = (DateARTStartedDataDefinition) definition;
		EvaluatedPersonData c = new EvaluatedPersonData(def, context);

		if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
			return c;
		}

		DataSetQueryService qs = Context.getService(DataSetQueryService.class);

		StringBuilder hql = new StringBuilder();
		Map<String, Object> m = new HashMap<String, Object>();

		hql.append("from Obs ");
		hql.append("where voided = false ");

		if (context.getBaseCohort() != null) {
			hql.append("and personId in (:patientIds) ");
			m.put("patientIds", context.getBaseCohort());
		}

		hql.append("and concept in (:questionList) ");
		m.put("questionList", questionConcepts);

		hql.append("and obsDatetime <= :onOrBefore ");
		m.put("onOrBefore", context.getEvaluationDate());

		hql.append("order by obsDatetime asc");

		List<Object> queryResult = qs.executeHqlQuery(hql.toString(), m);

		ListMap<Integer, Obs> obsForPatients = new ListMap<Integer, Obs>();
		for (Object o : queryResult) {
			Obs obs = (Obs)o;
			obsForPatients.putInList(obs.getPersonId(), obs);
		}

		for (Integer pId : obsForPatients.keySet()) {
			boolean done = false;
			DateARTStartedPatientSnapshot snapshot = new DateARTStartedPatientSnapshot();
			Iterator<Obs> i = obsForPatients.get(pId).iterator();
			while (!done && i.hasNext()) {
				Obs o = i.next();
				if (snapshot.consume(o)) {
					if (snapshot.eligible()) {
						done = true;
					}
				}
			}
			if (snapshot.hasProperty("result"))
				c.addData(pId, snapshot.get("result"));
			else
				c.addData(pId, "");
		}

		return c;
	}
}
