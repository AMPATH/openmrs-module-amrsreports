/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.amrsreports.reporting.converter;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 *  formats and returns a list of dates as a string with each date formatted according to a given format string
 */
public class DateListCustomConverterTest extends TestCase {
    public void testConvert() throws Exception {
        String format ="MM/yyyy";

        List<Date> dateList = Arrays.asList(new Date("05 Dec 2012"), new Date("02 Apr 2013"), new Date("25 May 2013"));

        Assert.assertTrue("The test has failed",new DateListCustomConverter().convert(format,dateList).toString().equals("12/2012\n" +
                "04/2013\n" +
                "05/2013\n"));

    }
}
