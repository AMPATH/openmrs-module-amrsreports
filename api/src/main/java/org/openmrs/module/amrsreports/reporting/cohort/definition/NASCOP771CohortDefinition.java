package org.openmrs.module.amrsreports.reporting.cohort.definition;

import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * NASCOP 771 Register cohort definition
 */
@Caching(strategy = ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.NASCOP771CohortDefinition")
public class NASCOP771CohortDefinition extends AMRSReportsCohortDefinition {

}
