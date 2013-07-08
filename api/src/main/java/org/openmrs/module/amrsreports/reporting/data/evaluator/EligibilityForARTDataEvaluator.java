package org.openmrs.module.amrsreports.reporting.data.evaluator;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.cache.MohCacheUtils;
import org.openmrs.module.amrsreports.reporting.data.DateARTStartedDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.EligibilityForARTDataDefinition;
import org.openmrs.module.amrsreports.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreports.snapshot.ARVPatientSnapshot;
import org.openmrs.module.amrsreports.util.MOHReportUtil;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Evaluator for ART Eligibility
 */
@Handler(supports = EligibilityForARTDataDefinition.class, order = 50)
public class EligibilityForARTDataEvaluator implements PersonDataEvaluator {

	/**
	 * @should return Clinical and WHO Stage if under 12 and PEDS WHO Stage is 4
	 * @should return CD4 and WHO Stage and CD4 values if under 12 and PEDS WHO Stage is 3 and CD4 is under 500 and CD4 percentage is under 25
	 * @should return CD4 and HIV DNA PCR and WHO Stage and CD4 and HIV DNA PCR values if under 18 months and PEDS WHO Stage is 2 and CD4 is under 500 and HIV DNA PCR is positive
	 * @should return HIV DNA PCR and WHO Stage and HIV DNA PCR value if under 18 months and PEDS WHO Stage is 1 and HIV DNA PCR is positive
	 * @should return CD4 and WHO Stage and CD4 percentage values if between 18 months and 5 years and PEDS WHO Stage is 1 or 2 and CD4 percentage is under 20
	 * @should return CD4 and WHO Stage and CD4 percentage values if between 5 years and 12 years and PEDS WHO Stage is 1 or 2 and CD4 percentage is under 25
	 * @should return Clinical and WHO Stage if over 12 and ADULT WHO Stage is 3 or 4
	 * @should return CD4 and WHO Stage and CD4 value if over 12 and ADULT or PEDS WHO Stage is 1 or 2 and CD4 is under 350
	 * @should return reason only when ART started before eligibility date
	 *
	 * @see PersonDataEvaluator#evaluate(org.openmrs.module.reporting.data.person.definition.PersonDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
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

		EvaluatedPersonData otherColumn = Context.getService(PersonDataService.class).evaluate(new DateARTStartedDataDefinition(), context);
		Map<Integer, Object> artStartDates = otherColumn.getData();

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
			Obs obs = (Obs) o;
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
					snapshot.setAgeGroup(MOHReportUtil.getAgeGroupAtDate(p.getBirthdate(), o.getObsDatetime()));
					if (snapshot.eligible()) {
						done = true;

						Date lastDate = (Date) snapshot.get("lastDate");
						Date artStartDate = (Date) artStartDates.get(pId);

						if (artStartDate != null && lastDate.after(artStartDate)) {
							snapshot.set("lastDate", null);
						}
					}
				}
			}

			c.addData(pId, snapshot);
		}

		return c;
	}
}