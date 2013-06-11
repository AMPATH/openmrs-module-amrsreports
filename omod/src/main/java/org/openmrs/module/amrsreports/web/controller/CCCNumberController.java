package org.openmrs.module.amrsreports.web.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.amrsreports.service.MOHFacilityService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

/**
 * Controller for facility management pages
 */
@Controller
@RequestMapping("module/amrsreports/cccNumbers.list")
public class CCCNumberController {

	@ModelAttribute("facilities")
	public List<MOHFacility> getAllFacilities() {
		return Context.getService(MOHFacilityService.class).getAllFacilities(true);
	}

	@ModelAttribute("serials")
	public Map<Integer, Integer> getLatestSerialNumbers() {
		return Context.getService(MOHFacilityService.class).getFacilityIdToLatestSerialNumberMap();
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showFacilities() {
		return "module/amrsreports/cccNumbers";
	}

}
