package org.openmrs.module.amrsreports.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.amrsreports.UserFacility;
import org.openmrs.module.amrsreports.service.MOHFacilityService;
import org.openmrs.module.amrsreports.service.UserFacilityService;
import org.openmrs.util.RoleConstants;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

/**
 * controller for facility privileges page
 */
@Controller
@RequestMapping(value = "module/amrsreports/facilityPrivileges.form")
public class FacilityPrivilegesController {

	private Log log = LogFactory.getLog(this.getClass());

	private static final String VIEW = "module/amrsreports/facilityPrivileges";

	@ModelAttribute("userFacilities")
	public List<UserFacility> getAllUserFacilities() {
		return Context.getService(UserFacilityService.class).getAllUserFacilities();
	}

	@ModelAttribute("facilities")
	public List<MOHFacility> getAllFacilities() {
		return Context.getService(MOHFacilityService.class).getAllFacilities();
	}

	@ModelAttribute("superusers")
	public List<User> getSuperUsers() {
		Role su = Context.getUserService().getRole(RoleConstants.SUPERUSER);
		return Context.getUserService().getUsers(null, Collections.singletonList(su), false);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showPage() {
		return VIEW;
	}

	@RequestMapping(method = RequestMethod.POST, params = "action=delete")
	public String submitForm(
			@RequestParam(value = "userFacilityIds", required = true) List<Integer> userFacilityIds,
			HttpServletRequest request) {

		Integer count = 0;
		for (Integer id : userFacilityIds) {
			try {
				UserFacility uf = Context.getService(UserFacilityService.class).getUserFacility(id);
				if (uf != null) {
					Context.getService(UserFacilityService.class).purgeUserFacility(uf);
					count++;
				}
			} catch (Exception ex) {
				log.warn("Could not purge UserFacility #" + id);
			}
		}

		request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR,
				String.format("Deleted %d privilege%s.", count, count > 1 ? "s" : ""));

		return "redirect:facilityPrivileges.form";
	}

	@RequestMapping(method = RequestMethod.POST, params = "action=assign")
	public String savePermission(
			@RequestParam(value = "userId", required = true) Integer userId,
			@RequestParam(value = "facilityId", required = true) Integer facilityId,
			HttpServletRequest request) {

		if (userId == null) {
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "You must select a user.");
			return VIEW;
		}

		if (facilityId == null) {
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "You must select a facility.");
			return VIEW;
		}

		User user = Context.getUserService().getUser(userId);

		if (user == null) {
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Invalid user id: " + userId + ".");
			return VIEW;
		}

		MOHFacility facility = Context.getService(MOHFacilityService.class).getFacility(facilityId);

		if (facility == null) {
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Invalid facility id: " + facilityId + ".");
			return VIEW;
		}

		if (user.hasRole(RoleConstants.SUPERUSER)) {
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
					String.format("User %s already has Super User role and can see any facility.", user));
			return VIEW;
		}

		if (Context.getService(UserFacilityService.class).hasFacilityPrivilege(user, facility)) {
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
					String.format("User %s already has permission for %s.", user, facility.getName()));
			return VIEW;
		}

		UserFacility uf = new UserFacility();
		uf.setUser(user);
		uf.setFacility(facility);
		Context.getService(UserFacilityService.class).saveUserFacility(uf);

		request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR,
				String.format("User %s given access to %s.", user, facility));

		return "redirect:facilityPrivileges.form";
	}

}
