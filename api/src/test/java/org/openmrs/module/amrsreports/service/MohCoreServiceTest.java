package org.openmrs.module.amrsreports.service;

import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Test file for MohCoreService methods
 */
public class MohCoreServiceTest extends BaseModuleContextSensitiveTest {

	@Test
	public void blankTest() {
		// pass
	}

//	/**
//	 * @see org.openmrs.module.amrsreports.service.MohCoreService#getUserLocation(Integer)
//	 * @Verifies(value = "should get a UserFacility by its Id", method =
//	 * "getUserLocation(Integer)")
//	 * @should get a UserLocation by its Id
//	 */
//	@Test
//	public void getUserLocation_shouldGetAUserLocationByItsId()
//		throws Exception {
//
//		MohCoreService cservice = Context.getService(MohCoreService.class);
//		User sysUser = new User();
//		sysUser.setUserId(501);
//
//		Location userLoc = new Location();
//		userLoc.setLocationId(1);
//
//		UserFacility userlocation = new UserFacility();
//		userlocation.setUser(sysUser);
//		userlocation.setFacility(userLoc);
//
//		UserFacility returnedUserFacility = cservice.saveUserLocation(userlocation);
//
//		Assert.assertNotNull(cservice.getUserLocation(returnedUserFacility.getUserFacilityId()));
//	}
//
//	/**
//	 * @see org.openmrs.module.amrsreports.service.MohCoreService#purgeUserLocation(org.openmrs.module.amrsreports.UserFacility)
//	 * @verifies(value = "should Purge a UserFacility", method =
//	 * "purgeUserLocation(UserFacility)")
//	 * @should purge a UserLocation
//	 */
//	@Test
//	public void purgeUserLocation_shouldPurgeAUserLocation() throws Exception {
//
//		MohCoreService cservice = Context.getService(MohCoreService.class);
//
//		User sysUser = new User();
//		sysUser.setUserId(501);
//
//		Location userLoc = new Location();
//		userLoc.setLocationId(1);
//
//		UserFacility userlocation = new UserFacility();
//		userlocation.setUser(sysUser);
//		userlocation.setFacility(userLoc);
//
//		UserFacility uloc = cservice.saveUserLocation(userlocation);
//
//		Integer userlocationid = uloc.getUserFacilityId();
//		Assert.assertNotNull(userlocationid);
//
//		cservice.purgeUserLocation(uloc);
//
//		Assert.assertNull(cservice.getUserLocation(userlocationid));
//	}
//
//	/**
//	 * @see org.openmrs.module.amrsreports.service.MohCoreService#saveUserLocation(org.openmrs.module.amrsreports.UserFacility)
//	 * @verifies(value = "should save a UserFacility", method =
//	 * "saveUserLocation(UserFacility)")
//	 * @should save a UserLocation
//	 */
//	@Test
//	public void saveUserLocation_shouldSaveAUserLocation() throws Exception {
//
//		MohCoreService cservice = Context.getService(MohCoreService.class);
//
//		User sysUser = new User();
//		sysUser.setUserId(501);
//
//		Location userLoc = new Location();
//		userLoc.setLocationId(1);
//
//		UserFacility userlocation = new UserFacility();
//		userlocation.setUser(sysUser);
//		userlocation.setFacility(userLoc);
//
//		Assert.assertNull(userlocation.getUserFacilityId());
//
//		UserFacility uloc = cservice.saveUserLocation(userlocation);
//
//		Assert.assertNotNull(uloc.getUserFacilityId());
//	}
//
//	/**
//	 * @see org.openmrs.module.amrsreports.service.MohCoreService#getAllowedLocationsForUser(org.openmrs.User)
//	 */
//	@Test
//	@Verifies(value = "should only get specified locations for user", method ="getAllowedLocationsForUser(User)")
//	public void getAllowedLocationsForUser_shouldOnlyGetSpecifiedLocationsForUser() throws Exception {
//		MohCoreService cservice = Context.getService(MohCoreService.class);
//
//		User sysUser = new User();
//		sysUser.setUserId(501);
//
//		Location userLoc = new Location();
//		userLoc.setLocationId(1);
//
//		// create a userlocation
//		UserFacility userlocation = new UserFacility();
//		userlocation.setUser(sysUser);
//		userlocation.setFacility(userLoc);
//
//		Assert.assertNull(userlocation.getUserFacilityId());
//
//		cservice.saveUserLocation(userlocation);
//
//		List<Location> actual = cservice.getAllowedLocationsForUser(sysUser);
//		Assert.assertEquals(1, actual.size());
//		Assert.assertEquals(1, actual.get(0).getLocationId().intValue());
//	}
//
//	/**
//	 * @see org.openmrs.module.amrsreports.service.MohCoreService#getAllowedLocationsForUser(org.openmrs.User)
//	 */
//	@Test
//	@Verifies(value = "should return empty list if none assigned", method ="getAllowedLocationsForUser(User)")
//	public void getAllowedLocationsForUser_shouldReturnEmptyListIfNoneAssigned() throws Exception {
//		MohCoreService cservice = Context.getService(MohCoreService.class);
//
//		User sysUser = new User();
//		sysUser.setUserId(501);
//
//		List<Location> actual = cservice.getAllowedLocationsForUser(sysUser);
//		Assert.assertNotNull(actual);
//		Assert.assertEquals(0, actual.size());
//	}

}
