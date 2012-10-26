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

    @RequestMapping(value = "module/amrsreport/locationPrivileges.form", method = RequestMethod.GET)
    public void onloadLocationPrivileges(ModelMap model) {
        LocationService locationservinstance =Context.getService(LocationService.class);
        List<Location> locationlist=locationservinstance.getAllLocations(true);

        model.addAttribute("locationlist", locationlist);

        UserService userservinstance =Context.getService(UserService.class);
        List<User> userslist=userservinstance.getAllUsers();

        model.addAttribute("userlist", userslist);

        MohCoreService getallUserlocpriv = Context.getService(MohCoreService.class);
        List<UserLocation> allUserLocPrivileges =getallUserlocpriv.getAllUserLocationPrivileges();

        model.addAttribute("userlocpriv", allUserLocPrivileges);

    }
}
