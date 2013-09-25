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

package org.openmrs.module.amrsreports.web.controller;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.HIVCareEnrollment;
import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.amrsreports.service.HIVCareEnrollmentService;
import org.openmrs.module.amrsreports.service.MOHFacilityService;
import org.openmrs.module.amrsreports.service.MohCoreService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;

@Controller
public class CCCDownloadController {

	@RequestMapping("module/amrsreports/downloadCCCNumberSQL")
	public void downloadSQL(HttpServletResponse response,
							@RequestParam(required = true, value = "facilityId") Integer facilityId) throws IOException {

		if (facilityId == null) {
			// TODO say something ...
			return;
		}

		// get the facility
		MOHFacilityService fs = Context.getService(MOHFacilityService.class);
		MOHFacility facility = fs.getFacility(facilityId);

		if (facility == null) {
			// TODO say something ...
			return;
		}

		// get the Patient Identifier Type
		PatientIdentifierType pit = Context.getService(MohCoreService.class).getCCCNumberIdentifierType();

		if (pit == null)
			// TODO say something ...
			return;

		// get the patients
		List<Integer> c = fs.getPatientsInFacilityMissingCCCNumbers(facility);

		if (c.isEmpty())
			// TODO say something ...
			return;

		// get the starting serial number
		Integer serial = fs.getLatestSerialNumberForFacility(facility);

		// increase because the number we got was the last one used
		serial++;

		// get the currently logged in user
		User user = Context.getAuthenticatedUser();

		// use a template for the insert statements
		String template = "INSERT INTO patient_identifier" +
				" (patient_id, identifier, identifier_type, preferred, location_id, creator, date_created, voided, uuid)" +
				" VALUES (%d, '%s', %d, 0, %d, %d, NOW(), 0, UUID());";

		// build the SQL
		List<String> inserts = new ArrayList<String>();
		for (Integer patientId : c) {
			Patient p = Context.getPatientService().getPatient(patientId);
			HIVCareEnrollment hce = Context.getService(HIVCareEnrollmentService.class).getHIVCareEnrollmentForPatient(p);
			String identifier = String.format("%s-%05d", facility.getCode(), serial++);

			// only create an insert if the patient and HIV Care Enrollment exist
			if (p != null && hce != null) {
				inserts.add(
						String.format(template,
								patientId, identifier, pit.getPatientIdentifierTypeId(),
								hce.getEnrollmentLocation().getLocationId(), user.getUserId()));
			}
		}

		// join the individual inserts with newlines
		String result = StringUtils.join(inserts, "\n");

		// format the filename
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		String filename = String.format("ccc-inserts-facility-%d-%s.sql", facility.getFacilityId(), sdf.format(new Date()));

		// set the information
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment; filename=" + filename);
		response.setContentLength(result.length());

		// push the data to the stream
		response.getOutputStream().println(result);

		// close the stream
		response.getOutputStream().close();
	}

	@RequestMapping("module/amrsreports/downloadCCCNumberList")
	public void downloadList(HttpServletResponse response,
							@RequestParam(required = true, value = "facilityId") Integer facilityId,
							@RequestParam(required = true, value = "startingSerial") Integer startingSerial,
							@RequestParam(required = true, value = "count") Integer count) throws IOException {

		if (facilityId == null) {
			// TODO say something ...
			return;
		}

		// get the facility
		MOHFacilityService fs = Context.getService(MOHFacilityService.class);
		MOHFacility facility = fs.getFacility(facilityId);

		if (facility == null) {
			// TODO say something ...
			return;
		}

		Integer serial = startingSerial;

		// build the list of identifiers
		List<String> identifiers = new ArrayList<String>();
		for (int i = 0; i < count; i++) {
			identifiers.add(String.format("%s-%05d", facility.getCode(), serial++));
		}

		// join the individual identifiers with newlines
		String result = StringUtils.join(identifiers, "\n");

		// format the filename
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		String filename = String.format("ccc-identifiers-facility-%d-%s.txt", facility.getFacilityId(), sdf.format(new Date()));

		// set the information
		response.setContentType("text/plain");
		response.setHeader("Content-Disposition", "attachment; filename=" + filename);
		response.setContentLength(result.length());

		// push the data to the stream
		response.getOutputStream().println(result);

		// close the stream
		response.getOutputStream().close();
	}



}
