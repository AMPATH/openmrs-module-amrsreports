package org.openmrs.module.amrsreport.service;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreport.UserReport;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.User;

import org.openmrs.Location;
import org.openmrs.module.amrsreport.userlocation.UserLocation;


/**
 * Created with IntelliJ IDEA.
 * User: alfayo
 * Date: 9/25/12
 * Time: 12:14 PM
 *
 */
public class MohCoreServiceTest   extends BaseModuleContextSensitiveTest{
   /**
     * @verifies save AmrsReportUser
     * @see MohCoreService#saveUserReport(org.openmrs.module.amrsreport.UserReport)
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
        userrpt.setReporDefinitionUuid("testuuid");
        Assert.assertNotNull( service.saveUserReport(userrpt));
    }

    /**
     * @verifies get userreport by id
     * @see MohCoreService#getUserReportByUserId(Integer)
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
        userrpt.setReporDefinitionUuid("testuuid");
        UserReport userreport=service.saveUserReport(userrpt);
       Assert.assertNotNull( service.getUserReportByUserId(userreport.getId()));
    }

  /**
     * @verifies  delete user report  based on user report uuid
     * @see MohCoreService#purgeUserReport(org.openmrs.module.amrsreport.UserReport)
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
        userrpt.setReporDefinitionUuid("testuuid");
        UserReport userreport=service.saveUserReport(userrpt);
        service.purgeUserReport(userreport);
       Assert.assertNull( service.getUserReportByUuid(userreport.getUuid()));
    }

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
