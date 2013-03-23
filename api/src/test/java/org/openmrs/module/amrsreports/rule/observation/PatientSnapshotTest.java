package org.openmrs.module.amrsreports.rule.observation;

import org.junit.Assert;
import org.junit.Test;

/**
 * A test class for set and get property methods in PatientSnapshot class
 */
public class PatientSnapshotTest {

    PatientSnapshot ps;
    @Test
    public void testGetProperty() throws Exception {
        ps = new ARVPatientSnapshot();
        ps.set("reason", "Clinical Only");

        Assert.assertTrue(ps.get("reason").equals("Clinical Only"));

    }
    @Test
    public void testSetProperty() throws Exception {
        ps = new ARVPatientSnapshot();
        ps.set("reason", "Clinical Only");

        Assert.assertTrue(ps.get("reason").equals("Clinical Only"));

    }
}
