package org.openmrs.module.amrsreports.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.builder.ARVTableBuilder;
import org.openmrs.module.amrsreports.builder.DrugEventBuilder;
import org.openmrs.module.amrsreports.builder.HIVCareEnrollmentBuilder;
import org.openmrs.module.amrsreports.builder.PregnancyTableBuilder;
import org.openmrs.module.drughistory.api.DrugSnapshotService;

/**
 * Updates HIV Care HIVCareEnrollment table with latest enrollment information
 */
public class UpdateHIVCareEnrollmentTask extends AMRSReportsTask {

	private static final Log log = LogFactory.getLog(UpdateHIVCareEnrollmentTask.class);

	@Override
	public void doExecute() {
		// build the ARV Tables
		ARVTableBuilder.getInstance().execute();

//		// build drug history
//		DrugEventBuilder.getInstance().execute();
//
//		// tell the drug event service to regenerate snapshots
//		Context.getService(DrugSnapshotService.class).generateDrugSnapshots(null);

		// build the pregnancy table
		PregnancyTableBuilder.getInstance().execute();

		// build the enrollment table
		HIVCareEnrollmentBuilder.getInstance().execute();
	}
}
