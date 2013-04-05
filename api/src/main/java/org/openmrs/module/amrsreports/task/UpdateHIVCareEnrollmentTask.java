package org.openmrs.module.amrsreports.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.amrsreports.util.HIVCareEnrollmentBuilder;

/**
 * Updates HIV Care HIVCareEnrollment table with latest enrollment information
 */
public class UpdateHIVCareEnrollmentTask extends AMRSReportsTask {

	private static final Log log = LogFactory.getLog(UpdateHIVCareEnrollmentTask.class);

	@Override
	public void doExecute() {
		HIVCareEnrollmentBuilder.execute();
	}
}
