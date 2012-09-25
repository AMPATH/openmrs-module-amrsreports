package org.openmrs.module.amrsreport.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreport.userreport.UserReport;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.User;

/**
 * Created with IntelliJ IDEA.
 * User: alfayo
 * Date: 9/25/12
 * Time: 12:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class MohCoreServiceTest   extends BaseModuleContextSensitiveTest{
   /**
     * @verifies save AmrsReportUser
     * @see MohCoreService#saveUserReport(org.openmrs.module.amrsreport.userreport.UserReport)
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
     * @see MohCoreService#purgeUserReport(org.openmrs.module.amrsreport.userreport.UserReport)
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
}
