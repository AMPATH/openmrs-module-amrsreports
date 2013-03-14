package org.openmrs.module.amrsreports;

import org.openmrs.BaseOpenmrsObject;
import org.openmrs.Location;

import java.util.Date;

/**
 * All information required to generate a report
 */
public class QueuedReport extends BaseOpenmrsObject {

	private Integer queuedReportId;
	private String reportName;
	private Location location;
	private Date evaluationDate;
	private Date dateScheduled;

	public Integer getQueuedReportId() {
		return queuedReportId;
	}

	public void setQueuedReportId(Integer queuedReportId) {
		this.queuedReportId = queuedReportId;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Date getEvaluationDate() {
		return evaluationDate;
	}

	public void setEvaluationDate(Date evaluationDate) {
		this.evaluationDate = evaluationDate;
	}

	public Date getDateScheduled() {
		return dateScheduled;
	}

	public void setDateScheduled(Date dateScheduled) {
		this.dateScheduled = dateScheduled;
	}

	@Override
	public Integer getId() {
		return getQueuedReportId();
	}

	@Override
	public void setId(Integer id) {
		setQueuedReportId(id);
	}
}
