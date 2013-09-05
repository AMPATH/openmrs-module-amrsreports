package org.openmrs.module.amrsreports.reporting.data;

import org.openmrs.Encounter;
import org.openmrs.module.amrsreports.reporting.common.EncounterRepresentation;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * The last HIV encounter before the report date
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class LastHIVEncounterDataDefinition extends BaseDataDefinition implements PersonDataDefinition {

	@Override
	public Class<?> getDataType() {
		return EncounterRepresentation.class;
	}
}
