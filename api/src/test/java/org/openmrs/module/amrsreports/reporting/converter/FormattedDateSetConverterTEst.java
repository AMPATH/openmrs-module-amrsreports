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

import org.junit.Test;
import org.openmrs.module.amrsreports.AmrsReportsConstants;
import org.openmrs.module.amrsreports.util.MOHReportUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.dbunit.dataset.DataSetUtils.assertEquals;
import static org.junit.Assert.assertEquals;

public class FormattedDateSetConverterTest {
	/**
	 * @verifies convert a list of dates
	 * @see FormattedDateSetConverter#convert(Object)
	 */
	@Test
	public void convert_shouldConvertAListOfDates() throws Exception {
		Set<Date> dateList = new LinkedHashSet<Date>(Arrays.asList(
				new Date("05 Dec 2012"),
				new Date("02 Apr 2013"),
				new Date("25 May 2013")));

		String expected =
				MOHReportUtil.formatdates(new Date("05 Dec 2012")) + " | PMTCT" +
						AmrsReportsConstants.INTER_CELL_SEPARATOR +
						MOHReportUtil.formatdates(new Date("02 Apr 2013")) + " | PMTCT" +
						AmrsReportsConstants.INTER_CELL_SEPARATOR +
						MOHReportUtil.formatdates(new Date("25 May 2013")) + " | PMTCT";

		String actual = (String) new FormattedDateSetConverter("%s | PMTCT").convert(dateList);

		assertEquals(actual, expected);
	}

	/**
	 * @verifies return a blank string if no dates exist
	 * @see FormattedDateSetConverter#convert(Object)
	 */
	@Test
	public void convert_shouldReturnABlankStringIfNoDatesExist() throws Exception {
		String expected = "";
		String actual = (String) new FormattedDateSetConverter().convert(Collections.emptySet());
		assertEquals(actual, expected);
	}

	/**
	 * @verifies return null if the parameter is null
	 * @see FormattedDateSetConverter#convert(Object)
	 */
	@Test
	public void convert_shouldReturnNullIfTheParameterIsNull() throws Exception {
		String expected = null;
		String actual = (String) new FormattedDateSetConverter().convert(null);
		assertEquals(actual, expected);
	}
}
