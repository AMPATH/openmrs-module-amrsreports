package org.openmrs.module.amrsreports.reporting.cohort.definition;

import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * NASCOP 771 Register cohort definition
 */
@Caching(strategy = ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.NASCOP771CohortDefinition")
public class NASCOP771CohortDefinition extends BaseCohortDefinition {

	@ConfigurationProperty
	private MOHFacility facility;

	public NASCOP771CohortDefinition() {
		super();
	}

	public NASCOP771CohortDefinition(MOHFacility facility) {
		super();
		this.facility = facility;
	}

	public MOHFacility getFacility() {
		return facility;
	}

	public void setFacility(MOHFacility facility) {
		this.facility = facility;
	}
}
