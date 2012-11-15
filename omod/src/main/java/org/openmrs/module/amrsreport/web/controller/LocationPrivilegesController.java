package org.openmrs.module.amrsreport.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.User;
import org.openmrs.api.LocationService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreport.UserLocation;
import org.openmrs.module.amrsreport.service.MohCoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * controller for View AMRS Reports page
 */
@Controller
public class LocationPrivilegesController {

	private static final Log log = LogFactory.getLog(LocationPrivilegesController.class);

	@Autowired
	LocationService locationService;

	@Autowired
	UserService userService;

	@RequestMapping(value = "module/amrsreport/locationPrivileges.form", method = RequestMethod.GET)
	public void populateModel(ModelMap model) {
		List<Location> locations = locationService.getAllLocations(true);

		model.addAttribute("locationlist", locations);

		List<User> users = userService.getAllUsers();

		model.addAttribute("userlist", users);

		MohCoreService mohCoreService = Context.getService(MohCoreService.class);
		List<UserLocation> allUserLocPrivileges = mohCoreService.getAllUserLocationPrivileges();

		model.addAttribute("userlocpriv", allUserLocPrivileges);
	}
}
