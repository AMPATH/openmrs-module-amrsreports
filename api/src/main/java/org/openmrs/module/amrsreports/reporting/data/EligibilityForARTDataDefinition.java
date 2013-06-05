package org.openmrs.module.amrsreports.reporting.data;

import org.openmrs.module.amrsreports.snapshot.ARVPatientSnapshot;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * determines ARV patient snapshots for patients
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class EligibilityForARTDataDefinition extends BaseDataDefinition implements PersonDataDefinition {

	@Override
	public Class<?> getDataType() {
		return ARVPatientSnapshot.class;
	}

}