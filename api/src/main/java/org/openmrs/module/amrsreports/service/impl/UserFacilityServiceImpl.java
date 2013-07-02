package org.openmrs.module.amrsreports.service.impl;

import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.amrsreports.UserFacility;
import org.openmrs.module.amrsreports.db.UserFacilityDAO;
import org.openmrs.module.amrsreports.service.MOHFacilityService;
import org.openmrs.module.amrsreports.service.UserFacilityService;
import org.openmrs.util.RoleConstants;

import java.util.Collections;
import java.util.List;

/**
 * Implementation of {@link UserFacilityService}
 */
public class UserFacilityServiceImpl implements UserFacilityService {

	private UserFacilityDAO dao;

	public void setDao(UserFacilityDAO dao) {
		this.dao = dao;
	}

	@Override
	public UserFacility saveUserFacility(UserFacility userFacility) {
		return dao.saveUserFacility(userFacility);
	}

	@Override
	public UserFacility getUserFacility(Integer userFacilityId) {
		return dao.getUserFacility(userFacilityId);
	}

	@Override
	public List<UserFacility> getAllUserFacilities() {
		return dao.getAllUserFacilities();
	}

	@Override
	public void purgeUserFacility(UserFacility userFacility) {
		dao.purgeUserFacility(userFacility);
	}

	@Override
	public List<MOHFacility> getAllowedFacilitiesForUser(User user) {
		if (user == null)
			return Collections.emptyList();

		if (user.hasRole(RoleConstants.SUPERUSER))
			return Context.getService(MOHFacilityService.class).getAllFacilities();

		return dao.getAllowedFacilitiesForUser(user);
	}

	@Override
	public Boolean hasFacilityPrivilege(User user, MOHFacility facility) {
		if (user.hasRole(RoleConstants.SUPERUSER))
			return true;

		UserFacility uf = dao.getUserFacilityFor(user, facility);
		return uf != null;
	}
}
