package org.openmrs.module.amrsreports.db;

import org.openmrs.module.amrsreports.QueuedReport;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * DAO for QueuedReport objects
 */
public interface QueuedReportDAO {

	@Transactional
	public QueuedReport saveQueuedReport(QueuedReport queuedReport);

	@Transactional
	public QueuedReport getNextQueuedReport(Date date);

	@Transactional
	public void purgeQueuedReport(QueuedReport queuedReport);

	@Transactional
	public List<QueuedReport> getAllQueuedReports();
}
