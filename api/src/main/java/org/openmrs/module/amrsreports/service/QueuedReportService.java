package org.openmrs.module.amrsreports.service;

import org.openmrs.module.amrsreports.QueuedReport;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.io.IOException;
import java.util.List;

/**
 * Service for dealing with queued reports
 */
public interface QueuedReportService {

	public QueuedReport getNextQueuedReport();

	void processQueuedReport(QueuedReport queuedReport) throws EvaluationException, IOException;

	public QueuedReport saveQueuedReport(QueuedReport queuedReport);

	public void purgeQueuedReport(QueuedReport queuedReport);

	public List<QueuedReport> getAllQueuedReports();
}
