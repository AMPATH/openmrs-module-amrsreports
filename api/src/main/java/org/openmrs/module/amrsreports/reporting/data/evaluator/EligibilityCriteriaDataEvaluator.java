package org.openmrs.module.amrsreports.reporting.data.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.cache.MohCacheUtils;
import org.openmrs.module.amrsreports.reporting.common.ObsRepresentation;
import org.openmrs.module.amrsreports.reporting.common.ObsRepresentationDatetimeComparator;
import org.openmrs.module.amrsreports.reporting.data.ARTTransferStatusDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.DateARTStartedDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.EligibilityCriteriaDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.EligibilityForARTDataDefinition;
import org.openmrs.module.amrsreports.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreports.snapshot.ARVPatientSnapshot;
import org.openmrs.module.amrsreports.util.MOHReportUtil;
import org.openmrs.module.reporting.common.Birthdate;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

/**
 * Evaluator class for Eligibility for ART and Pre-ART
 */
@Handler(supports = EligibilityCriteriaDataDefinition.class, order = 50)
public class EligibilityCriteriaDataEvaluator extends BatchedExecutionDataEvaluator<ObsRepresentation> {

	private Log log = LogFactory.getLog(getClass());

	private Map<Integer, Object> artTransferStatus;
	private Map<Integer, Object> eligibilityForART;

	private EligibilityCriteriaDataDefinition definition;

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
		definition = (EligibilityCriteriaDataDefinition) def;
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

		ARVPatientSnapshot snapshot = new ARVPatientSnapshot();
		snapshot.setEvaluationDate(context.getEvaluationDate());

        Boolean artTransferInfo = (Boolean) artTransferStatus.get(pId);

        if(artTransferInfo != null && artTransferInfo){
            snapshot.set("transferStatus","Transfer In");
        }
        else {
            ARVPatientSnapshot eligibilityCriteria = (ARVPatientSnapshot) eligibilityForART.get(pId);
            if(eligibilityCriteria != null && eligibilityCriteria.get("reason") != null){
                snapshot.set("transferStatus",eligibilityCriteria.get("reason"));
            }
        }
		return snapshot;
	}

	@Override
	protected boolean doBefore(EvaluationContext context, EvaluatedPersonData c, Cohort cohort) {

		try {
			EvaluatedPersonData otherColumn = Context.getService(PersonDataService.class).evaluate(new ARTTransferStatusDataDefinition(), context);
			artTransferStatus = otherColumn.getData();
		} catch (EvaluationException e) {
			log.error("could not evaluate ART Transfer Status", e);
			return false;
		}

		try {
			EvaluatedPersonData eligibilityForARTData = Context.getService(PersonDataService.class).evaluate(new EligibilityForARTDataDefinition(), context);
			eligibilityForART = eligibilityForARTData.getData();
		} catch (EvaluationException e) {
			log.error("could not evaluate Eligibility Criteria for ART ", e);
			return false;
		}

		return true;
	}

	@Override
	protected void doAfter(EvaluationContext context, EvaluatedPersonData c) {
		// pass
	}

	@Override
	protected String getHQL() {
		return null;
	}

	@Override
	protected Map<String, Object> getSubstitutions(EvaluationContext context) {
        return new HashMap<String, Object>();
    }
}