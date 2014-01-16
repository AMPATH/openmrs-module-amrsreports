package org.openmrs.module.amrsreports.reporting.data;

import org.openmrs.module.amrsreports.reporting.common.ObsRepresentation;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

import java.util.Date;

/**
 * finds the last RTC date
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class INHStartDateDataDefinition extends BaseDataDefinition implements PersonDataDefinition {

	@Override
	public Class<?> getDataType() {
		return String.class;
	}

}