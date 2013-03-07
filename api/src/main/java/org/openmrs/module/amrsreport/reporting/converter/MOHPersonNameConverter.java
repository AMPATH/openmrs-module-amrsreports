package org.openmrs.module.amrsreport.reporting.converter;

import org.openmrs.PersonName;
import org.openmrs.module.reporting.data.converter.DataConverter;

/**
 * This converter formats a PersonName to the required format in MOH reports
 */
public class MOHPersonNameConverter implements DataConverter {
	@Override
	public Object convert(Object original) {
		PersonName personName = (PersonName) original;
		if (personName == null)
			return "";

		return String.format("%s;%s", personName.getGivenName(), personName.getFamilyName());
	}

	@Override
	public Class<?> getInputDataType() {
		return PersonName.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}
}
