package org.openmrs.module.amrsreports.reporting.converter;

import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.module.reporting.data.converter.DataConverter;

/**
 * Converter to pull just the location out of an encounter
 */
public class EncounterLocationConverter implements DataConverter {
	@Override
	public Object convert(Object original) {
		Encounter e = (Encounter) original;

		if (e == null)
			return null;

		return e.getLocation();
	}

	@Override
	public Class<?> getInputDataType() {
		return Encounter.class;
	}

	@Override
	public Class<?> getDataType() {
		return Location.class;
	}
}
