package org.openmrs.module.amrsreports.reporting.converter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonAttribute;
import org.openmrs.module.amrsreports.AmrsReportsConstants;
import org.openmrs.module.amrsreports.cache.MohCacheUtils;
import org.openmrs.module.amrsreports.rule.MohEvaluableNameConstants;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Converts the Entry Point person attribute into the appropriate acronym
 */
public class MultiplePatientIdentifierConverter implements DataConverter {

	private final Log log = LogFactory.getLog(this.getClass());

	/**
	 * returns all patient identifiers split by the common inter-cell split character
	 */
	@Override
	public Object convert(Object original) {
		if (original == null)
			return "";

		List<PatientIdentifier> piList = (List<PatientIdentifier>) original;

		return StringUtils.join(piList, AmrsReportsConstants.INTER_CELL_SEPARATOR);
	}

	@Override
	public Class<?> getInputDataType() {
		return List.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}

}
