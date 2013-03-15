package org.openmrs.module.amrsreports.web.controller;

import org.openmrs.Location;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.amrsreports.service.MOHFacilityService;
import org.openmrs.module.amrsreports.web.propertyeditor.LocationNameEditor;
import org.openmrs.web.WebConstants;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Controller for Facility form page
 */
@Controller
@RequestMapping("module/amrsreports/facility.form")
@SessionAttributes("facility")
public class FacilityFormController {

	private static final String SUCCESS_VIEW = "redirect:facility.list";
	private static final String EDIT_VIEW = "module/amrsreports/facilityForm";

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Location.class, new LocationNameEditor());
	}

	@RequestMapping(method = RequestMethod.GET)
	public String editFacility(
			@RequestParam(value = "facilityId", required = false) Integer facilityId,
			@RequestParam(value = "locationId", required = false) Integer locationId,
			ModelMap modelMap) {
		MOHFacility facility = null;

		if (facilityId != null)
			facility = Context.getService(MOHFacilityService.class).getFacility(facilityId);

		if (facility == null)
			facility = new MOHFacility();

		if (facilityId == null && locationId != null) {
			Location location = Context.getLocationService().getLocation(locationId);
			facility.addLocation(location);
		}

		modelMap.put("facility", facility);

		Set<Location> unallocated = Context.getService(MOHFacilityService.class).getUnallocatedLocations();
		unallocated.addAll(facility.getLocations());
		modelMap.put("locations", unallocated);

		return EDIT_VIEW;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String saveFacility(
			@ModelAttribute("facility") MOHFacility facility,
			BindingResult errors,
			HttpServletRequest request) {

		MOHFacilityService service = Context.getService(MOHFacilityService.class);
		HttpSession httpSession = request.getSession();
		String view = null;

		if (request.getParameter("save") != null) {
			service.saveFacility(facility);
			view = SUCCESS_VIEW;
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Facility saved");
		}

		// if the user is retiring out the EncounterType
		else if (request.getParameter("retire") != null) {
			String retireReason = request.getParameter("retireReason");
			if (facility.getFacilityId() != null && !(StringUtils.hasText(retireReason))) {
				errors.reject("retireReason", "general.retiredReason.empty");
				return EDIT_VIEW;
			}

			service.retireFacility(facility, retireReason);
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Facility successfully retired");

			view = SUCCESS_VIEW;
		}

		// if the user is purging the encounterType
		else if (request.getParameter("purge") != null) {

			try {
				service.purgeFacility(facility);
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Facility successfully purged");
				view = SUCCESS_VIEW;
			}
			catch (DataIntegrityViolationException e) {
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "error.object.inuse.cannot.purge");
				view = EDIT_VIEW;
			}
			catch (APIException e) {
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "error.general: " + e.getLocalizedMessage());
				view = EDIT_VIEW;
			}
		}

		return view;
	}
}
