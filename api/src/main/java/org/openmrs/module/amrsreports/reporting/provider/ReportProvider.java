package org.openmrs.module.amrsreports.reporting.provider;

import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;

/**
 * Defines what it means to be a report provider
 */
public interface ReportProvider {

	public String getName();

	public ReportDefinition getReportDefinition();

	public CohortDefinition getCohortDefinition();
}
