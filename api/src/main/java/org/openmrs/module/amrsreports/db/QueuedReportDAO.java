package org.openmrs.module.amrsreports.db;

import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.amrsreports.QueuedReport;

import java.util.Date;
import java.util.List;

/**
 * DAO for QueuedReport objects
 */
public interface QueuedReportDAO {

	public QueuedReport saveQueuedReport(QueuedReport queuedReport);

	public QueuedReport getNextQueuedReport(Date date);

	public void purgeQueuedReport(QueuedReport queuedReport);

	public List<QueuedReport> getAllQueuedReports();

	public List<QueuedReport> getQueuedReportsWithStatus(String status);

	public QueuedReport getQueuedReport(Integer reportId);

	public List<QueuedReport> getQueuedReportsByFacilities(List<MOHFacility> allowedFacilities, String status);

}
