package org.openmrs.module.amrsreports.reporting.data;

import org.openmrs.Encounter;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;

/**
 * The last Encounter at a facility
 */
public class FirstEncounterAtFacilityDataDefinition extends BaseDataDefinition implements PersonDataDefinition {

	@Override
	public Class<?> getDataType() {
		return Encounter.class;
	}
}
