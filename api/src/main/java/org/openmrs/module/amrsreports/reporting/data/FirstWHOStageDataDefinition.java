package org.openmrs.module.amrsreports.reporting.data;

import org.openmrs.module.amrsreports.reporting.common.ObsRepresentation;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * finds the current WHO Stage and date determined for anyone in the cohort
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class FirstWHOStageDataDefinition extends BaseDataDefinition implements PersonDataDefinition {

	@Override
	public Class<?> getDataType() {
		return ObsRepresentation.class;
	}

}