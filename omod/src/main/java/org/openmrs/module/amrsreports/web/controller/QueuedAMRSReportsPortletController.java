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

import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.amrsreports.QueuedReport;
import org.openmrs.module.amrsreports.service.QueuedReportService;
import org.openmrs.module.amrsreports.service.UserFacilityService;
import org.openmrs.util.RoleConstants;
import org.openmrs.web.controller.PortletController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

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
             //Find list of super users
            Role superUserRole = Context.getUserService().getRole(RoleConstants.SUPERUSER);
            List<User> superUsers=Context.getUserService().getUsers(null, Collections.singletonList(superUserRole), false);

            //Find current user
            User currentUser=Context.getAuthenticatedUser();

            if( superUsers.contains(currentUser)) {
            //  display all reports if the user is a super user
			for (QueuedReport thisReport : queuedReports) {

				MOHFacility thisMohFacility = thisReport.getFacility();

				if (!queuedReportsMap.containsKey(thisMohFacility))
					queuedReportsMap.put(thisMohFacility, new ArrayList<QueuedReport>());

				queuedReportsMap.get(thisMohFacility).add(thisReport);
			}

            } else{
                //filter reports based on current user as per facility
                   List<MOHFacility> allowedFacilities  =Context.getService(UserFacilityService.class).getAllowedFacilitiesForUser(currentUser);
                  for (QueuedReport thisReport : queuedReports) {

                    MOHFacility thisMohFacility = thisReport.getFacility();

                    if(allowedFacilities.contains(thisMohFacility)){
                            if (!queuedReportsMap.containsKey(thisMohFacility))
                                queuedReportsMap.put(thisMohFacility, new ArrayList<QueuedReport>());

                            queuedReportsMap.get(thisMohFacility).add(thisReport);


                    }

                }

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