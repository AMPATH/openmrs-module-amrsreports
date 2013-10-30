package org.openmrs.module.amrsreports.reporting.converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.PersonAttribute;
import org.openmrs.module.amrsreports.cache.MohCacheUtils;
import org.openmrs.module.amrsreports.rule.MohEvaluableNameConstants;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.util.HashMap;
import java.util.Map;

/**
 * Converts the Entry Point person attribute into the appropriate acronym
 */
public class EntryPointConverter implements DataConverter {

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

	public static final String OTHER = "Other";

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
	@Override
	public Object convert(Object original) {
		if (original == null)
			return OTHER;

		if (!(original instanceof PersonAttribute))
			return "Invalid: " + original.toString();

		PersonAttribute pa = (PersonAttribute) original;

		String entryPoint = locationMap.get(pa.getValue());

		if (entryPoint != null)
			return entryPoint;

		return OTHER;
	}

	@Override
	public Class<?> getInputDataType() {
		return PersonAttribute.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}

	private static String getConceptId(String conceptName) {
		return MohCacheUtils.getConcept(conceptName).getConceptId().toString();
	}

}
