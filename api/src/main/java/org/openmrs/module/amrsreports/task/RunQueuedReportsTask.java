package org.openmrs.module.amrsreports.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.amrsreports.ReportQueueProcessor;

/**
 * Scheduled task for running queued reports
 */
public class RunQueuedReportsTask extends AMRSReportsTask {

	// Instance of processor
	private static ReportQueueProcessor processor = null;

	/**
	 * Default Constructor (Uses SchedulerConstants.username and SchedulerConstants.password
	 */
	public RunQueuedReportsTask() {
		if (processor == null) {
			processor = new ReportQueueProcessor();
		}
	}

	/**
	 * Process the next queued item
	 */
	public void doExecute() {
		processor.processQueuedReports();
	}

}