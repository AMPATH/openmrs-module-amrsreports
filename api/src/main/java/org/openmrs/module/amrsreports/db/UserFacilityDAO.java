package org.openmrs.module.amrsreports.db;

import org.openmrs.User;
import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.amrsreports.UserFacility;

import java.util.List;

/**
 * DAO for manipulating UserFacility objects
 */
public interface UserFacilityDAO {

	public UserFacility saveUserFacility(UserFacility userFacility);

	public UserFacility getUserFacility(Integer userFacilityId);

	public List<UserFacility> getAllUserFacilities();

	public void purgeUserFacility(UserFacility userFacility);

	public List<MOHFacility> getAllowedFacilitiesForUser(User user);

	public UserFacility getUserFacilityFor(User user, MOHFacility facility);
}
