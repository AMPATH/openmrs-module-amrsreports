package org.openmrs.module.amrsreport.rule.collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.amrsreport.cache.MohCacheUtils;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreport.rule.MohEvaluableRule;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Author jmwogi
 */
public class MohEntryPointRule extends MohEvaluableRule {

	private static final Log log = LogFactory.getLog(MohEntryPointRule.class);

	public static final String TOKEN = "MOH Point Of Entry";

	private static Map<String, String> locationMap;

	static {
		locationMap = new HashMap<String, String>();
		locationMap.put(getConceptId(MohEvaluableNameConstants.MOBILE_VOLUNTARY_COUNSELING_AND_TESTING), "MVCT");
		locationMap.put(getConceptId(MohEvaluableNameConstants.MATERNAL_CHILD_HEALTH_PROGRAM), "MCH");
		locationMap.put(getConceptId(MohEvaluableNameConstants.PREVENTION_OF_MOTHER_TO_CHILD_TRANSMISSION_OF_HIV), "PMTCT");
		locationMap.put(getConceptId(MohEvaluableNameConstants.VOLUNTARY_COUNSELING_AND_TESTING_CENTER), "VCT");
		locationMap.put(getConceptId(MohEvaluableNameConstants.TUBERCULOSIS), "TB");
		locationMap.put(getConceptId(MohEvaluableNameConstants.HOME_BASED_TESTING_PROGRAM), "HCT");
		locationMap.put(getConceptId(MohEvaluableNameConstants.INPATIENT_CARE_OR_HOSPITALIZATION), "IPD");
		locationMap.put(getConceptId(MohEvaluableNameConstants.PROVIDER_INITIATED_TESTING_AND_COUNSELING), "PITC");
		locationMap.put(getConceptId(MohEvaluableNameConstants.PEDIATRIC_OUTPATIENT_CLINIC), "POC");
	}

	private static String getConceptId(String conceptName) {
		return MohCacheUtils.getConcept(conceptName).getConceptId().toString();
	}

	/**
	 * returns the value of the entry point location, based on the point of HIV testing person attribute
	 *
	 * @should return MVCT for Mobile Voluntary Counseling and Testing
	 * @should return MCH for Maternal Child Health Program
	 * @should return PMTCT for Prevention of Mother to Child Transmission of HIV
	 * @should return VCT for Voluntary Counseling and Testing Center
	 * @should return TB for Tuberculosis
	 * @should return HCT for Home Based Testing Program
	 * @should return IPD for Inpatient Care or Hospitalization
	 * @should return PITC for Provider Initiated Testing and Counseling
	 * @should return POC for Pediatric Outpatient Clinic
	 * @should return Other for Other Non Coded
	 * @should return Other if no point of HIV testing exists
	 * @should return Other if point of HIV testing is not recognized
	 */
	public Result evaluate(LogicContext context, Integer patientId, Map<String, Object> parameters) {
		Patient patient = Context.getPatientService().getPatient(patientId);
		PersonAttribute pa = patient.getAttribute(MohEvaluableNameConstants.POINT_OF_HIV_TESTING);

		if (log.isDebugEnabled())
			log.debug("pa value is " + (pa != null ? pa.getValue() : "null (pa is null)"));

		if (pa == null)
			return new Result("Other");

		String entryPoint = locationMap.get(pa.getValue());

		if (log.isDebugEnabled())
			log.debug("entryPoint is " + entryPoint);

		if (entryPoint != null)
			return new Result(entryPoint);

		return new Result("Other");
	}

	/**
	 * @see org.openmrs.module.amrsreport.rule.MohEvaluableRule#getEvaluableToken()
	 */
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