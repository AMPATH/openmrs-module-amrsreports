package org.openmrs.module.amrsreports.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.amrsreports.util.HIVCareEnrollmentBuilder;
import org.openmrs.scheduler.tasks.AbstractTask;

import java.util.Date;

/**
 * Updates HIV Care HIVCareEnrollment table with latest enrollment information
 */
public class UpdateHIVCareEnrollmentTask extends AbstractTask {

	private static final Log log = LogFactory.getLog(UpdateHIVCareEnrollmentTask.class);

	@Override
	public void execute() {
		HIVCareEnrollmentBuilder.execute();
	}
}
