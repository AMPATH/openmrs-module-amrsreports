package org.openmrs.module.amrsreports;

import org.openmrs.BaseOpenmrsObject;

import java.util.Date;

/**
 * All information required to generate a report
 */
public class QueuedReport extends BaseOpenmrsObject {

	private Integer queuedReportId;
	private String reportName;
	private MOHFacility facility;
	private Date evaluationDate;
	private Date dateScheduled;
	private String status;
    private Integer repeatInterval;
	private String csvFilename;
	private String xlsFilename;

	public static final String STATUS_NEW = "NEW";
	public static final String STATUS_ERROR = "ERROR";
	public static final String STATUS_RUNNING = "RUNNING";
	public static final String STATUS_COMPLETE = "COMPLETE";

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

	public MOHFacility getFacility() {
		return facility;
	}

	public void setFacility(MOHFacility facility) {
		this.facility = facility;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public Integer getId() {
		return getQueuedReportId();
	}

	@Override
	public void setId(Integer id) {
		setQueuedReportId(id);
	}

	public String getCsvFilename() {
		return csvFilename;
	}

	public void setCsvFilename(String csvFilename) {
		this.csvFilename = csvFilename;
	}

	public String getXlsFilename() {
		return xlsFilename;
	}

	public void setXlsFilename(String xlsFilename) {
		this.xlsFilename = xlsFilename;
	}

    public Integer getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(Integer repeatInterval) {
        this.repeatInterval = repeatInterval;
    }
}
