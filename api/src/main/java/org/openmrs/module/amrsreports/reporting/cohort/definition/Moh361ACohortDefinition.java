package org.openmrs.module.amrsreports.reporting.cohort.definition;

import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * MOH 361A Register cohort definition
 */
@Caching(strategy = ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.MOH361ACohortDefinition")
public class Moh361ACohortDefinition extends BaseCohortDefinition {

	@ConfigurationProperty
	private MOHFacility facility;

	public Moh361ACohortDefinition() {
		super();
	}

	public Moh361ACohortDefinition(MOHFacility facility) {
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
