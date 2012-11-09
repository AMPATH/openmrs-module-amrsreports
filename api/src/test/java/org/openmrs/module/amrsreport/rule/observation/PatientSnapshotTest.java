package org.openmrs.module.amrsreport.rule.observation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.amrsreport.rule.observation.ARVPatientSnapshot;
import org.openmrs.module.amrsreport.rule.observation.PatientSnapshot;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.lang.AssertionError;

/**
 * A test class for set and get property methods in PatientSnapshot class
 */
public class PatientSnapshotTest extends BaseModuleContextSensitiveTest {

    PatientSnapshot ps;
    @Test
    public void testGetProperty() throws Exception {
        ps = new ARVPatientSnapshot();
        ps.setProperty("reason","Clinical Only");

        Assert.assertTrue(ps.getProperty("reason").equals("Clinical Only"));

    }
    @Test
    public void testSetProperty() throws Exception {
        ps = new ARVPatientSnapshot();
        ps.setProperty("reason","Clinical Only");

        Assert.assertTrue(ps.getProperty("reason").equals("Clinical Only"));

    }
}
