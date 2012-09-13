package org.openmrs.module.amrsreport.service;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreport.userlocation.UserLocation;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.User;
import org.openmrs.Location;

import src.test.java.org.openmrs.module.dataintegrity.DataIntegrityService;

public class MohCoreServiceTest extends BaseModuleContextSensitiveTest{

	public MohCoreServiceTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	/**
	 * Sample test just to have something here
	 */
	@Test
	public void testFoo() {
		// pass
	}

	/**
	 * @see MohCoreService#getUserLocation(Integer)
	 * @verifies get a UserLocation by its Id
	 */
	@Test
	public void getUserLocation_shouldGetAUserLocationByItsId()
			throws Exception {
		//TODO auto-generated
		Assert.fail("Not yet implemented");
	}

	/**
	 * @see MohCoreService#purgeUserLocation(UserLocation)
	 * @verifies purge a UserLocation
	 */
	@Test
	public void purgeUserLocation_shouldPurgeAUserLocation() throws Exception {
		//TODO auto-generated
		Assert.fail("Not yet implemented");
	}

	/**
	 * @see MohCoreService#saveUserLocation(UserLocation)
	 * @verifies save a UserLocation
	 */
	@Test
	public void saveUserLocation_shouldSaveAUserLocation() throws Exception {
		
		//Create an instance of the service object
		
		MohCoreService cservice = Context.getService(MohCoreService.class);
		
		//Create a sample User 
		User sysUser = new User();
		sysUser.setUserId(2);
		
		//Create a sample Location
		Location userLoc = new Location();
		userLoc.setLocationId(3);
		
		//create an instance of UserLocation class
		UserLocation userlocation = new UserLocation();
		userlocation.setUserLocationId(3);
		userlocation.setSysUser(sysUser);
		userlocation.setUserLoc(userLoc);
		
		cservice.saveUserLocation(userlocation);
		
		
	}

}
