package org.openmrs.module.amrsreport.service;

import junit.framework.Assert;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreport.userlocation.UserLocation;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class MohCoreServiceTest extends BaseModuleContextSensitiveTest {

	/**
	 * @see MohCoreService#getUserLocation(Integer)
	 * @Verifies(value = "should get a UserLocation by its Id", method = "getUserLocation(Integer)")
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
	 * @see MohCoreService#purgeUserLocation(UserLocation)
	 * @verifies(value = "should Purge a UserLocation", method = "purgeUserLocation(UserLocation)")
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
	 * @see MohCoreService#saveUserLocation(UserLocation)
	 * @verifies(value = "should save a UserLocation", method = "saveUserLocation(UserLocation)")
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
}
