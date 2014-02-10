package org.openmrs.module.amrsreports.web.controller;

import org.openmrs.Location;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.amrsreports.service.MOHFacilityService;
import org.openmrs.module.amrsreports.service.UserFacilityService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Set;

/**
 * Controller for facility management pages
 */
@Controller
@RequestMapping("module/amrsreports/facility.list")
public class FacilityListController {

	@ModelAttribute("facilities")
	public List<MOHFacility> getAllFacilities() {
        User currentUser = Context.getAuthenticatedUser();
        List<MOHFacility> relevantFacilities = Context.getService(UserFacilityService.class).getAllowedFacilitiesForUser(currentUser);

        return relevantFacilities;
	}

	@ModelAttribute("unallocatedLocations")
	public Set<Location> getUnallocatedLocations() {
		return Context.getService(MOHFacilityService.class).getUnallocatedLocations();
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showFacilities() {
		return "module/amrsreports/facilityList";
	}

}
