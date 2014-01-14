package org.openmrs.module.amrsreports.reporting.converter;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

/**
 * test class for ARTMonthZeroConverter class
 */
public class ARTMonthZeroConverterTest {
    @Test
    public void shouldReturnDecember() {
        Date dateOne = new Date("05 Dec 2012");

        ARTMonthZeroConverter artMonthZeroConverter = new ARTMonthZeroConverter();

        Assert.assertEquals("December", artMonthZeroConverter.convert(dateOne));

    }

}
