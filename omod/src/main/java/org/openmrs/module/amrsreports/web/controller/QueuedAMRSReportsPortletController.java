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

import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.AmrsReportsConstants;
import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.amrsreports.QueuedReport;
import org.openmrs.module.amrsreports.service.QueuedReportService;
import org.openmrs.module.amrsreports.util.MOHReportUtil;
import org.openmrs.web.controller.PortletController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("**/queuedAMRSReports.portlet")
public class QueuedAMRSReportsPortletController extends PortletController {

	/**
	 * @see org.openmrs.web.controller.PortletController#populateModel(javax.servlet.http.HttpServletRequest,
	 *      java.util.Map)
	 */
	@Override
	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {

		String status = (String) model.get("status");

		Map<MOHFacility, List<QueuedReport>> queuedReportsMap = new HashMap<MOHFacility, List<QueuedReport>>();

		if (Context.isAuthenticated() && status != null) {

			List<QueuedReport> queuedReports = Context.getService(QueuedReportService.class).getQueuedReportsWithStatus(status);

			for (QueuedReport thisReport : queuedReports) {

				MOHFacility thisMohFacility = thisReport.getFacility();

				if (!queuedReportsMap.containsKey(thisMohFacility))
					queuedReportsMap.put(thisMohFacility, new ArrayList<QueuedReport>());


				queuedReportsMap.get(thisMohFacility).add(thisReport);
			}
		}

		model.put("queuedReportsMap", queuedReportsMap);

		// date time format -- needs to come from here because we can make it locale-specific
		// TODO extract this to a utility if used more than once

		SimpleDateFormat sdf = Context.getDateFormat();
		String format = sdf.toPattern();
		format += " hh:mm a";

		model.put("datetimeFormat", format);
	}



}