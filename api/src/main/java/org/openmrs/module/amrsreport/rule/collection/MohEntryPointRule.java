package org.openmrs.module.amrsreport.rule.collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreport.rule.MohEvaluableRule;

import java.util.Map;
import java.util.Set;

/**
 * Author jmwogi
 */
public class MohEntryPointRule extends MohEvaluableRule {

	private static final Log log = LogFactory.getLog(MohEntryPointRule.class);

	public static final String TOKEN = "MOH Point Of Entry";

	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, org.openmrs.Patient,
	 *      java.util.Map)
	 */
	public Result evaluate(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		try {
			Patient patient = Context.getPatientService().getPatient(patientId);
			Integer intPoint = Context.getPersonService().getPersonAttributeTypeByName(MohEvaluableNameConstants.POINT_OF_HIV_TESTING).getId();
			int entryPointConcept = Integer.parseInt(patient.getAttribute(intPoint).getValue());
			String entryPoint = Context.getConceptService().getConcept(entryPointConcept).getName().toString();

			if (entryPoint.equals(MohEvaluableNameConstants.MOBILE_VOLUNTARY_COUNSELING_AND_TESTING))
				return new Result("MVCT");
			if (entryPoint.equals(MohEvaluableNameConstants.MATERNAL_CHILD_HEALTH_PROGRAM))
				return new Result("MCH");
			if (entryPoint.equals(MohEvaluableNameConstants.PREVENTION_OF_MOTHER_TO_CHILD_TRANSMISSION_OF_HIV))
				return new Result("PMTCT");
			if (entryPoint.equals(MohEvaluableNameConstants.VOLUNTARY_COUNSELING_AND_TESTING_CENTER))
				return new Result("VCT");
			if (entryPoint.equals(MohEvaluableNameConstants.TUBERCULOSIS))
				return new Result("TB");
			if (entryPoint.equals(MohEvaluableNameConstants.HOME_BASED_TESTING_PROGRAM))
				return new Result("HCT");
			if (entryPoint.equals(MohEvaluableNameConstants.OTHER_NON_CODED))
				return new Result("Other");
			if (entryPoint.equals(MohEvaluableNameConstants.INPATIENT_CARE_OR_HOSPITALIZATION))
				return new Result("IPD");
			if (entryPoint.equals(MohEvaluableNameConstants.PROVIDER_INITIATED_TESTING_AND_COUNSELING))
				return new Result("PITC");
			if (entryPoint.equals(MohEvaluableNameConstants.PEDIATRIC_OUTPATIENT_CLINIC))
				return new Result("POC");
		} catch (Exception e) {
		}
		return new Result("Other");
	}

	protected String getEvaluableToken() {
		return TOKEN;
	}

	/**
	 * @see org.openmrs.logic.Rule#getDependencies()
	 */
	@Override
	public String[] getDependencies() {
		return new String[]{};
	}

	/**
	 * Get the definition of each parameter that should be passed to this rule execution
	 *
	 * @return all parameter that applicable for each rule execution
	 */

	@Override
	public Datatype getDefaultDatatype() {
		return Datatype.TEXT;
	}

	public Set<RuleParameterInfo> getParameterList() {
		return null;
	}

	@Override
	public int getTTL() {
		return 0;
	}

}