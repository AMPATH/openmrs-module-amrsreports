package org.openmrs.module.amrsreports.reporting.data.evaluator;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.cache.MohCacheUtils;
import org.openmrs.module.amrsreports.reporting.data.EligibilityForARTDataDefinition;
import org.openmrs.module.amrsreports.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreports.snapshot.ARVPatientSnapshot;
import org.openmrs.module.amrsreports.rule.util.MohRuleUtils;
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
@Handler(supports=EligibilityForARTDataDefinition.class, order=50)
public class EligibilityForARTDataEvaluator implements PersonDataEvaluator {

	@Override
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {

		List<Concept> questionConcepts = Arrays.asList(new Concept[]{
				MohCacheUtils.getConcept(MohEvaluableNameConstants.CD4_BY_FACS),
				MohCacheUtils.getConcept(MohEvaluableNameConstants.CD4_PERCENT),
				MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_DNA_PCR),
				MohCacheUtils.getConcept(MohEvaluableNameConstants.WHO_STAGE_PEDS),
				MohCacheUtils.getConcept(MohEvaluableNameConstants.WHO_STAGE_ADULT)
		});

		EligibilityForARTDataDefinition def = (EligibilityForARTDataDefinition) definition;
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
			Patient p = Context.getPatientService().getPatient(pId);
			ARVPatientSnapshot snapshot = new ARVPatientSnapshot();
			Iterator<Obs> i = obsForPatients.get(pId).iterator();
			while (!done && i.hasNext()) {
				Obs o = i.next();
				if (snapshot.consume(o)) {
					snapshot.setAgeGroup(MohRuleUtils.getAgeGroupAtDate(p.getBirthdate(), o.getObsDatetime()));
					if (snapshot.eligible()) {
						done = true;
					}
				}
			}
			c.addData(pId, snapshot);
		}

		return c;
	}
}
