package org.openmrs.module.amrsreports.reporting.data;

import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

import java.util.Date;

/**
 * Transfer In Date column
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class TransferStatusDataDefinition extends BaseDataDefinition implements PersonDataDefinition {

	@Override
	public Class<?> getDataType() {
		return Boolean.class;
	}
}
