package org.openmrs.module.amrsreports.reporting.converter;

import junit.framework.Assert;
import org.junit.Test;
import org.openmrs.module.amrsreports.snapshot.ARVPatientSnapshot;

/**
 * Please give a brief explanation of what the class does
 */
public class ARVPatientSnapshotReasonConverterTest {

    ARVPatientSnapshotReasonConverter converter = new ARVPatientSnapshotReasonConverter();

    @Test
    public void shouldTestWhetherReasonIsSet() throws Exception {

        ARVPatientSnapshot ps = new ARVPatientSnapshot();
        ps.set("reason","Clinical Only");
        Assert.assertNotNull(ps);
        Assert.assertEquals("Reason is not Clinical Only","Clinical Only",converter.convert(ps));
    }

    @Test
    public void shouldTestWhetherTransferPropertyIsSet() throws Exception {

        ARVPatientSnapshot ps = new ARVPatientSnapshot();
        ps.set("transfer",true);

        Assert.assertNotNull(ps);
        Assert.assertEquals("Not a Transfer In","Transfer In",converter.convert(ps));
    }
}
