package org.openmrs.module.amrsreport.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreport.util.HIVCareEnrollmentBuilder;
import org.openmrs.scheduler.tasks.AbstractTask;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Updates HIV Care HIVCareEnrollment table with latest enrollment information
 */
public class UpdateHIVCareEnrollmentTask extends AbstractTask {

	private static final Log log = LogFactory.getLog(UpdateHIVCareEnrollmentTask.class);

	@Override
	public void execute() {
		HIVCareEnrollmentBuilder.execute(new Date());
	}
}
