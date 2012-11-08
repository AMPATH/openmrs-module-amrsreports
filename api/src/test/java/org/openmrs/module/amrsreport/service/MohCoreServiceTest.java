package org.openmrs.module.amrsreport.service;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreport.UserLocation;
import org.openmrs.module.amrsreport.UserReport;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreport.rule.observation.ARVPatientSnapshot;
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
	 * @verifies save AmrsReportUser
	 * @see
	 * org.openmrs.module.amrsreport.service.MohCoreService#saveUserReport(org.openmrs.module.amrsreport.UserReport)
	 */
	@Test
	public void saveUserReport_shouldSaveAmrsReportUser() throws Exception {
		//TODO auto-generated
		MohCoreService service = Context.getService(MohCoreService.class);

		//Create a sample User
		User systemUser = new User();
		systemUser.setUserId(501);

		//sample user report instance
		UserReport userrpt = new UserReport();
		userrpt.setAmrsReportsUser(systemUser);
		userrpt.setReportDefinitionUuid("testuuid");
		Assert.assertNotNull(service.saveUserReport(userrpt));
	}

	/**
	 * @verifies get userreport by id
	 * @see org.openmrs.module.amrsreport.service.MohCoreService#getUserReportByUserId(Integer)
	 */
	@Test
	public void getUserReportByUserId_shouldGetUserreportById() throws Exception {
		//TODO auto-generated
		//Create an instance of the service object

		MohCoreService service = Context.getService(MohCoreService.class);

		//Create a sample User
		User systemUser = new User();
		systemUser.setUserId(501);

		//sample user report instance
		UserReport userrpt = new UserReport();
		userrpt.setAmrsReportsUser(systemUser);
		userrpt.setReportDefinitionUuid("testuuid");
		userrpt = service.saveUserReport(userrpt);

		Context.flushSession();

		Assert.assertNotNull(service.getUserReport(userrpt.getId()));
	}

	/**
	 * @verifies delete user report based on user report uuid
	 * @see
	 * org.openmrs.module.amrsreport.service.MohCoreService#purgeUserReport(org.openmrs.module.amrsreport.UserReport)
	 */
	@Test
	public void getUserReportByUserId_shouldPurgeUserReport() throws Exception {
		//TODO auto-generated
		//Create an instance of the service object

		MohCoreService service = Context.getService(MohCoreService.class);

		//Create a sample User
		User systemUser = new User();
		systemUser.setUserId(501);

		//sample user report instance
		UserReport userrpt = new UserReport();
		userrpt.setAmrsReportsUser(systemUser);
		userrpt.setReportDefinitionUuid("testuuid");
		UserReport userreport = service.saveUserReport(userrpt);
		service.purgeUserReport(userreport);
		Assert.assertNull(service.getUserReportByUuid(userreport.getUuid()));
	}

	/**
	 * @see org.openmrs.module.amrsreport.service.MohCoreService#getUserLocation(Integer)
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
	 * @see org.openmrs.module.amrsreport.service.MohCoreService#purgeUserLocation(org.openmrs.module.amrsreport.UserLocation)
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
	 * @see org.openmrs.module.amrsreport.service.MohCoreService#saveUserLocation(org.openmrs.module.amrsreport.UserLocation)
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
	 * @see org.openmrs.module.amrsreport.service.MohCoreService#getAllowedLocationsForUser(org.openmrs.User)
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
	 * @see org.openmrs.module.amrsreport.service.MohCoreService#getAllowedLocationsForUser(org.openmrs.User)
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

	/**
	 * @see org.openmrs.module.amrsreport.service.MohCoreService#getAllowedReportDefinitionsForUser(org.openmrs.User)
	 */
	@Test
	@Verifies(value = "should only get specified report definitions for user", method ="getAllowedReportDefinitionsForUser(User)")
	public void getAllowedReportDefinitionsForUser_shouldOnlyGetSpecifiedReportDefinitionsForUser() throws Exception {
		MohCoreService cservice = Context.getService(MohCoreService.class);

		User sysUser = new User();
		sysUser.setUserId(501);

		// create a report definition
		ReportDefinition rd = new ReportDefinition();
		rd.setName("foo");
		Context.getService(ReportDefinitionService.class).saveDefinition(rd);
		String expectedUuid = rd.getUuid();
		Assert.assertNotNull(expectedUuid);

		// create a userreport
		UserReport userreport = new UserReport();
		userreport.setAmrsReportsUser(sysUser);
		userreport.setReportDefinitionUuid(expectedUuid);
		cservice.saveUserReport(userreport);

		List<ReportDefinition> actual = cservice.getAllowedReportDefinitionsForUser(sysUser);
		Assert.assertEquals(1, actual.size());
		Assert.assertEquals(expectedUuid, actual.get(0).getUuid());
	}

	/**
	 * @see org.openmrs.module.amrsreport.service.MohCoreService#getAllowedReportDefinitionsForUser(org.openmrs.User)
	 */
	@Test
	@Verifies(value = "should return empty list if none assigned", method ="getAllowedReportDefinitionsForUser(User)")
	public void getAllowedReportDefinitionsForUser_shouldReturnEmptyListIfNoneAssigned() throws Exception {
		MohCoreService cservice = Context.getService(MohCoreService.class);

		User sysUser = new User();
		sysUser.setUserId(501);

		List<ReportDefinition> actual = cservice.getAllowedReportDefinitionsForUser(sysUser);
		Assert.assertNotNull(actual);
		Assert.assertEquals(0, actual.size());
	}

}
