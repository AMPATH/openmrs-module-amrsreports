package org.openmrs.module.amrsreports.reporting.converter;

import org.openmrs.Encounter;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.util.Date;

/**
 * Converter to get the encounter date from an encounter
 */
public class EncounterDatetimeConverter implements DataConverter {
	@Override
	public Object convert(Object original) {
		Encounter e = (Encounter) original;

		if (e == null)
			return null;

		return e.getEncounterDatetime();
	}

	@Override
	public Class<?> getInputDataType() {
		return Encounter.class;
	}

	@Override
	public Class<?> getDataType() {
		return Date.class;
	}
}
