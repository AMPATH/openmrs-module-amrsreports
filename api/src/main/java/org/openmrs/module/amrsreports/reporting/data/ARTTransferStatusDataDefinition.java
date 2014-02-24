package org.openmrs.module.amrsreports.reporting.data;

import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

/**
 * Transfer In Date column
 */
@Caching(strategy = ConfigurationPropertyCachingStrategy.class)
public class ARTTransferStatusDataDefinition extends BaseDataDefinition implements PersonDataDefinition {

	public ARTTransferStatusDataDefinition() {
		Parameter facility = new Parameter();
		facility.setName("facility");
		facility.setType(MOHFacility.class);
		this.addParameter(facility);
	}

	@Override
	public Class<?> getDataType() {
		return Boolean.class;
	}
}
