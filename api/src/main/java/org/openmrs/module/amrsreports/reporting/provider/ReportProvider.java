package org.openmrs.module.amrsreports.reporting.provider;

import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;

/**
 * Defines what it means to be a report provider
 */
public abstract class ReportProvider {

	protected String name;
	protected Boolean visible = false;
	protected ReportDefinition reportDefinition;
	protected CohortDefinition cohortDefinition;
	protected ReportDesign reportDesign;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public ReportDefinition getReportDefinition() {
		return reportDefinition;
	}

	public void setReportDefinition(ReportDefinition reportDefinition) {
		this.reportDefinition = reportDefinition;
	}

	public CohortDefinition getCohortDefinition() {
		return cohortDefinition;
	}

	public void setCohortDefinition(CohortDefinition cohortDefinition) {
		this.cohortDefinition = cohortDefinition;
	}

	public ReportDesign getReportDesign() {
		return reportDesign;
	}

	public void setReportDesign(ReportDesign reportDesign) {
		this.reportDesign = reportDesign;
	}
}
