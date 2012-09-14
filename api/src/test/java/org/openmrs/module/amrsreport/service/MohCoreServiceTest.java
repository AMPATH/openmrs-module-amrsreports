package org.openmrs.module.amrsreport.service;

import junit.framework.Assert;

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
		//Create an instance of the service object
		
		MohCoreService cservice = Context.getService(MohCoreService.class);
		
		//Create a sample User 
		User sysUser = new User();
		sysUser.setUserId(501);
		
		//Create a sample Location -- using New Location as an example
		Location userLoc = new Location();
		userLoc.setLocationId(1);
		
		//create an instance of UserLocation class
		UserLocation userlocation = new UserLocation();
		userlocation.setSysUser(sysUser);
		userlocation.setUserLoc(userLoc);
		//setting returnedUserLocation to hold the returned UserLocation object after save process
		UserLocation returnedUserLocation=cservice.saveUserLocation(userlocation);
		
		//returnedUserLocation.getUserLocationId() will hold the id of the saved object
		//cservice.getUserLocation(returnedUserLocation.getUserLocationId()) should return an object with the specidied ID
		//The test should not return null object
		Assert.assertNotNull(cservice.getUserLocation(returnedUserLocation.getUserLocationId()));
		
	}

	/**
	 * @see MohCoreService#purgeUserLocation(UserLocation)
	 * @verifies purge a UserLocation
	 */
	@Test
	public void purgeUserLocation_shouldPurgeAUserLocation() throws Exception {
		
		//Create an instance of the service object
		
		MohCoreService cservice = Context.getService(MohCoreService.class);
		
		//Create a sample User 
		User sysUser = new User();
		sysUser.setUserId(501);
		
		//Create a sample Location -- using New Location
		Location userLoc = new Location();
		userLoc.setLocationId(1);
		
		//create an instance of UserLocation class
		UserLocation userlocation = new UserLocation();
		userlocation.setSysUser(sysUser);
		userlocation.setUserLoc(userLoc);
		//sets up uloc variable to hold the saved UserLocation object
		UserLocation uloc = cservice.saveUserLocation(userlocation);
		
		//uloc.getUserLocationId() should return the id of the saved object in the database
		//Assert.assertNotNull(uloc.getUserLocationId()) should never be null if the object was saved successfully
		Integer userlocationid=uloc.getUserLocationId();
		Assert.assertNotNull(userlocationid);
		
		//If Assert.assertNotNull(uloc.getUserLocationId()); passes, we purge the uloc object from the database
		cservice.purgeUserLocation(uloc);
		
		/*cservice.getUserLocation(userlocationid);*/
		//Checks to see that the object has been purged from the database
		Assert.assertNull(cservice.getUserLocation(userlocationid));
		
		
		
		
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
		sysUser.setUserId(501);
		
		//Create a sample Location -- using New Location
		Location userLoc = new Location();
		userLoc.setLocationId(1);
		
		//create an instance of UserLocation class
		UserLocation userlocation = new UserLocation();
		userlocation.setSysUser(sysUser);
		userlocation.setUserLoc(userLoc);
		
		
		//Checks to see that getUserLocationId() returns null before save
		Assert.assertNull(userlocation.getUserLocationId());
		
		//sets up uloc variable to hold the returned object after save operation
		UserLocation uloc=cservice.saveUserLocation(userlocation);
		
		//Checks to ascertain that getUserLocationId() of the returned object is never null
		Assert.assertNotNull(uloc.getUserLocationId());
		
		
		
	}

}
