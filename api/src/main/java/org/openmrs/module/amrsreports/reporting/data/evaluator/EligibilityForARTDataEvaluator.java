package org.openmrs.module.amrsreports.reporting.data.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.cache.MohCacheUtils;
import org.openmrs.module.amrsreports.reporting.common.ObsRepresentation;
import org.openmrs.module.amrsreports.reporting.common.ObsRepresentationDatetimeComparator;
import org.openmrs.module.amrsreports.reporting.data.DateARTStartedDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.EligibilityForARTDataDefinition;
import org.openmrs.module.amrsreports.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreports.snapshot.ARVPatientSnapshot;
import org.openmrs.module.amrsreports.util.MOHReportUtil;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

/**
 * Evaluator for ART Eligibility
 */
@Handler(supports = EligibilityForARTDataDefinition.class, order = 50)
public class EligibilityForARTDataEvaluator extends BatchedExecutionDataEvaluator<ObsRepresentation> {

	private Log log = LogFactory.getLog(getClass());

	private Map<Integer, Object> artStartDates;

	private EligibilityForARTDataDefinition definition;

	@Override
	protected ObsRepresentation renderSingleResult(Map<String, Object> m) {
		return new ObsRepresentation(m);
	}

	@Override
	protected Comparator<ObsRepresentation> getResultsComparator() {
		return new ObsRepresentationDatetimeComparator();
	}

	@Override
	protected PersonDataDefinition setDefinition(PersonDataDefinition def) {
		definition = (EligibilityForARTDataDefinition) def;
		return definition;
	}

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
	 */
	@Override
	protected Object doExecute(Integer pId, SortedSet<ObsRepresentation> data, EvaluationContext context) {
		Patient p = Context.getPatientService().getPatient(pId);

		ARVPatientSnapshot snapshot = new ARVPatientSnapshot();
		snapshot.setEvaluationDate(context.getEvaluationDate());

		Iterator<ObsRepresentation> i = data.iterator();
		while (i.hasNext()) {
			ObsRepresentation o = i.next();
			if (snapshot.consume(o)) {
				snapshot.setAgeGroup(MOHReportUtil.getAgeGroupAtDate(p.getBirthdate(), o.getObsDatetime()));
				if (snapshot.eligible()) {
					Date lastDate = (Date) snapshot.get("lastDate");
					Date artStartDate = (Date) artStartDates.get(pId);

					if (artStartDate != null && lastDate.after(artStartDate)) {
						snapshot.set("lastDate", null);
					}

					return snapshot;
				}
			}
		}

		return snapshot;
	}

	@Override
	protected boolean doBefore(EvaluationContext context, EvaluatedPersonData c, Cohort cohort) {
		EvaluatedPersonData otherColumn;

		try {
			otherColumn = Context.getService(PersonDataService.class).evaluate(new DateARTStartedDataDefinition(), context);
		} catch (EvaluationException e) {
			log.error("could not evaluate Date ART Started", e);
			return false;
		}
		artStartDates = otherColumn.getData();

		return true;
	}

	@Override
	protected void doAfter(EvaluationContext context, EvaluatedPersonData c) {
		// pass
	}

	@Override
	protected String getHQL() {
		StringBuilder hql = new StringBuilder();

		hql.append("select new map(");
		hql.append("    personId as personId,");
		hql.append("    concept.id as conceptId,");
		hql.append("    valueCoded.id as valueCodedId,");
		hql.append("    valueNumeric as valueNumeric,");
		hql.append("    obsDatetime as obsDatetime)");
		hql.append(" from Obs ");
		hql.append(" where voided = false ");
		hql.append("    and personId in (:personIds)");
		hql.append("    and concept.id in (:questionList) ");

		return hql.toString();
	}

	@Override
	protected Map<String, Object> getSubstitutions(EvaluationContext context) {
		List<Integer> questionConcepts = Arrays.asList(
				MohCacheUtils.getConceptId(MohEvaluableNameConstants.CD4_BY_FACS),
				MohCacheUtils.getConceptId(MohEvaluableNameConstants.CD4_PERCENT),
				MohCacheUtils.getConceptId(MohEvaluableNameConstants.HIV_DNA_PCR),
				MohCacheUtils.getConceptId(MohEvaluableNameConstants.WHO_STAGE_PEDS),
				MohCacheUtils.getConceptId(MohEvaluableNameConstants.WHO_STAGE_ADULT)
		);

		Map<String, Object> m = new HashMap<String, Object>();
		m.put("questionList", questionConcepts);
		return m;
	}
}