package org.openmrs.module.amrsreport.reporting.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.PatientIdentifier;
import org.openmrs.module.reporting.data.converter.DataConverter;

/**
 * Converter for generating a serial number from a CCC identifier
 */
public class MOHSerialNumberConverter implements DataConverter {
	@Override
	public Object convert(Object original) {
		PatientIdentifier identifier = (PatientIdentifier) original;
		if (identifier == null)
			return "";

		if (StringUtils.isBlank(identifier.getIdentifier()))
			return "";

		Integer dashLocation = identifier.getIdentifier().indexOf("-");
		if (dashLocation == -1)
			return identifier.getIdentifier();

		return identifier.getIdentifier().substring(dashLocation);
	}

	@Override
	public Class<?> getInputDataType() {
		return PatientIdentifier.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}
}
