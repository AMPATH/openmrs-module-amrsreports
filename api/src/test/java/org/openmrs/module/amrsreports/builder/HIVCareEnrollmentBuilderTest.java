/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.amrsreports.builder;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.HIVCareEnrollment;
import org.openmrs.module.amrsreports.MohTestUtils;
import org.openmrs.module.amrsreports.service.HIVCareEnrollmentService;
import org.openmrs.module.drughistory.api.DrugSnapshotService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class HIVCareEnrollmentBuilderTest extends BaseModuleContextSensitiveTest {

	private Patient patient;

	@Before
	public void setUp() throws Exception {
		executeDataSet("datasets/concepts-drugeventbuilder.xml");
		patient = MohTestUtils.createTestPatient();
	}

	/**
	 * @verifies create an HIVCareEnrollment with proper firstARVDate for matching snapshot
	 * @see HIVCareEnrollmentBuilder#updateFirstARVDates()
	 */
	@Test
	public void updateFirstARVDates_shouldCreateAnHIVCareEnrollmentWithProperFirstARVDateForMatchingSnapshot() throws Exception {
		// verify preconditions
		HIVCareEnrollmentService hceService = Context.getService(HIVCareEnrollmentService.class);
		HIVCareEnrollment actual = hceService.getHIVCareEnrollmentForPatient(patient);
		assertNull(actual);

		// add the observations
		MohTestUtils.addCodedObs(patient, "ANTIRETROVIRALS STARTED", DrugEventBuilder.DRUG_ABACAVIR, "16 Oct 1975");
		MohTestUtils.addCodedObs(patient, "ANTIRETROVIRALS STARTED", DrugEventBuilder.DRUG_DARUNAVIR, "16 Oct 1975");
		MohTestUtils.addCodedObs(patient, "ANTIRETROVIRALS STARTED", DrugEventBuilder.DRUG_EFAVIRENZ, "16 Oct 1975");

		// run the DrugEventBuilder
		DrugEventBuilder.getInstance().execute();

		// build snapshots
		Context.getService(DrugSnapshotService.class).generateDrugSnapshots(null);

		// update first ARV date
		HIVCareEnrollmentBuilder.getInstance().updateFirstARVDates();

		// check for accuracy
		actual = hceService.getHIVCareEnrollmentForPatient(patient);
		assertNotNull(actual);
		assertEquals(MohTestUtils.makeDate("16 Oct 1975"), actual.getFirstARVDate());
	}

	/**
	 * @verifies not create an HIVCareEnrollment if no snapshots match the criteria
	 * @see HIVCareEnrollmentBuilder#updateFirstARVDates()
	 */
	@Test
	public void updateFirstARVDates_shouldNotCreateAnHIVCareEnrollmentIfNoSnapshotsMatchTheCriteria() throws Exception {
		// verify preconditions
		HIVCareEnrollmentService hceService = Context.getService(HIVCareEnrollmentService.class);
		HIVCareEnrollment actual = hceService.getHIVCareEnrollmentForPatient(patient);
		assertNull(actual);

		// add the observations
		MohTestUtils.addCodedObs(patient, "ANTIRETROVIRALS STARTED", DrugEventBuilder.DRUG_ABACAVIR, "16 Oct 1975");
		MohTestUtils.addCodedObs(patient, "ANTIRETROVIRALS STARTED", DrugEventBuilder.DRUG_DARUNAVIR, "16 Oct 1975");

		// run the DrugEventBuilder
		DrugEventBuilder.getInstance().execute();

		// build snapshots
		Context.getService(DrugSnapshotService.class).generateDrugSnapshots(null);

		// update first ARV date
		HIVCareEnrollmentBuilder.getInstance().updateFirstARVDates();

		// check for accuracy
		actual = hceService.getHIVCareEnrollmentForPatient(patient);
		assertNull(actual);
	}
}
