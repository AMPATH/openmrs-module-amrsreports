package org.openmrs.module.amrsreports.task;

import ca.uhn.hl7v2.HL7Exception;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.ReportQueueProcessor;
import org.openmrs.scheduler.tasks.AbstractTask;

/**
 * Scheduled task for running queued reports
 */
public class RunQueuedReportsTask extends AbstractTask {

	// Logger
	private static Log log = LogFactory.getLog(RunQueuedReportsTask.class);

	// Instance of hl7 processor
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
	public void execute() {
		Context.openSession();
		try {
			log.debug("Processing AMRS reports queue ... ");
			processor.processQueuedReports();
		} catch (HL7Exception e) {
			log.error("Error running AMRS reports queue task", e);
			throw new APIException("Error running AMRS reports queue task", e);
		} finally {
			Context.closeSession();
		}
	}

}
