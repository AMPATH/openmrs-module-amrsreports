package org.openmrs.module.amrsreport.web.controller;

import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Controller for the cohortCount page
 */
@Controller
@RequestMapping(value = "module/amrsreport/cohortCounts")
public class CohortCountController {

	@ModelAttribute("locations")
	public List<Location> getLocations() {
		return Context.getLocationService().getAllLocations();
	}

	@RequestMapping(method = RequestMethod.GET)
	public void setup() {
		// do nothing
	}
}
