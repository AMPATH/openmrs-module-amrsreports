package org.openmrs.module.amrsreports.service;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.UserLocation;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

import java.util.List;

/**
 * Test file for MohCoreService methods
 */
public class MohCoreServiceTest extends BaseModuleContextSensitiveTest {

	/**
	 * @see org.openmrs.module.amrsreports.service.MohCoreService#getUserLocation(Integer)
	 * @Verifies(value = "should get a UserLocation by its Id", method =
	 * "getUserLocation(Integer)")
	 * @should get a UserLocation by its Id
	 */
	@Test
	public void getUserLocation_shouldGetAUserLocationByItsId()
		throws Exception {

		MohCoreService cservice = Context.getService(MohCoreService.class);
		User sysUser = new User();
		sysUser.setUserId(501);

		Location userLoc = new Location();
		userLoc.setLocationId(1);

		UserLocation userlocation = new UserLocation();
		userlocation.setSysUser(sysUser);
		userlocation.setUserLoc(userLoc);

		UserLocation returnedUserLocation = cservice.saveUserLocation(userlocation);

		Assert.assertNotNull(cservice.getUserLocation(returnedUserLocation.getUserLocationId()));
	}

	/**
	 * @see org.openmrs.module.amrsreports.service.MohCoreService#purgeUserLocation(org.openmrs.module.amrsreports.UserLocation)
	 * @verifies(value = "should Purge a UserLocation", method =
	 * "purgeUserLocation(UserLocation)")
	 * @should purge a UserLocation
	 */
	@Test
	public void purgeUserLocation_shouldPurgeAUserLocation() throws Exception {

		MohCoreService cservice = Context.getService(MohCoreService.class);

		User sysUser = new User();
		sysUser.setUserId(501);

		Location userLoc = new Location();
		userLoc.setLocationId(1);

		UserLocation userlocation = new UserLocation();
		userlocation.setSysUser(sysUser);
		userlocation.setUserLoc(userLoc);

		UserLocation uloc = cservice.saveUserLocation(userlocation);

		Integer userlocationid = uloc.getUserLocationId();
		Assert.assertNotNull(userlocationid);

		cservice.purgeUserLocation(uloc);

		Assert.assertNull(cservice.getUserLocation(userlocationid));
	}

	/**
	 * @see org.openmrs.module.amrsreports.service.MohCoreService#saveUserLocation(org.openmrs.module.amrsreports.UserLocation)
	 * @verifies(value = "should save a UserLocation", method =
	 * "saveUserLocation(UserLocation)")
	 * @should save a UserLocation
	 */
	@Test
	public void saveUserLocation_shouldSaveAUserLocation() throws Exception {

		MohCoreService cservice = Context.getService(MohCoreService.class);

		User sysUser = new User();
		sysUser.setUserId(501);

		Location userLoc = new Location();
		userLoc.setLocationId(1);

		UserLocation userlocation = new UserLocation();
		userlocation.setSysUser(sysUser);
		userlocation.setUserLoc(userLoc);

		Assert.assertNull(userlocation.getUserLocationId());

		UserLocation uloc = cservice.saveUserLocation(userlocation);

		Assert.assertNotNull(uloc.getUserLocationId());
	}

	/**
	 * @see org.openmrs.module.amrsreports.service.MohCoreService#getAllowedLocationsForUser(org.openmrs.User)
	 */
	@Test
	@Verifies(value = "should only get specified locations for user", method ="getAllowedLocationsForUser(User)")
	public void getAllowedLocationsForUser_shouldOnlyGetSpecifiedLocationsForUser() throws Exception {
		MohCoreService cservice = Context.getService(MohCoreService.class);

		User sysUser = new User();
		sysUser.setUserId(501);

		Location userLoc = new Location();
		userLoc.setLocationId(1);

		// create a userlocation
		UserLocation userlocation = new UserLocation();
		userlocation.setSysUser(sysUser);
		userlocation.setUserLoc(userLoc);

		Assert.assertNull(userlocation.getUserLocationId());

		cservice.saveUserLocation(userlocation);

		List<Location> actual = cservice.getAllowedLocationsForUser(sysUser);
		Assert.assertEquals(1, actual.size());
		Assert.assertEquals(1, actual.get(0).getLocationId().intValue());
	}

	/**
	 * @see org.openmrs.module.amrsreports.service.MohCoreService#getAllowedLocationsForUser(org.openmrs.User)
	 */
	@Test
	@Verifies(value = "should return empty list if none assigned", method ="getAllowedLocationsForUser(User)")
	public void getAllowedLocationsForUser_shouldReturnEmptyListIfNoneAssigned() throws Exception {
		MohCoreService cservice = Context.getService(MohCoreService.class);

		User sysUser = new User();
		sysUser.setUserId(501);

		List<Location> actual = cservice.getAllowedLocationsForUser(sysUser);
		Assert.assertNotNull(actual);
		Assert.assertEquals(0, actual.size());
	}

}
