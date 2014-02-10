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

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.PersonAddress;
import org.openmrs.module.amrsreports.util.MOHReportUtil;
import org.openmrs.module.reporting.common.ObjectUtil;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class PersonAddressConverterTest {
	/**
	 * @verifies not include blank lines if data does not exist for that line
	 * @see PersonAddressConverter#convert(Object)
	 */
	@Test
	public void convert_shouldNotIncludeBlankLinesIfDataDoesNotExistForThatLine() throws Exception {
		PersonAddress pa = new PersonAddress();
		pa.setAddress1("Foo");
		pa.setAddress2("Bar");

		String expected = MOHReportUtil.joinAsSingleCell("Foo", "Bar");
		String actual = (String) new PersonAddressConverter().convert(pa);

		assertThat(actual, is(expected));
	}

	/**
	 * @verifies not have lines with leading or trailing spaces
	 * @see PersonAddressConverter#convert(Object)
	 */
	@Test
	public void convert_shouldNotHaveLinesWithLeadingOrTrailingSpaces() throws Exception {
		PersonAddress pa = new PersonAddress();
		pa.setAddress3("Foo");
		pa.setAddress6("Bar");

		String expected = MOHReportUtil.joinAsSingleCell("Foo", "Bar");
		String actual = (String) new PersonAddressConverter().convert(pa);

		assertThat(actual, is(expected));
	}

	/**
	 * @verifies return null for a null input
	 * @see PersonAddressConverter#convert(Object)
	 */
	@Test
	public void convert_shouldReturnNullForANullInput() throws Exception {
		String expected = null;
		String actual = (String) new PersonAddressConverter().convert(null);
		assertThat(actual, is(expected));
	}

	/**
	 * @verifies fill in an address in the Kenyan format
	 * @see PersonAddressConverter#convert(Object)
	 */
	@Test
	public void convert_shouldFillInAnAddressInTheKenyanFormat() throws Exception {
		PersonAddress pa = new PersonAddress();
		pa.setAddress1("A");
		pa.setAddress2("B");
		pa.setAddress3("C");
		pa.setCityVillage("D");
		pa.setAddress5("E");
		pa.setAddress6("F");
		pa.setAddress4("G");
		pa.setCountyDistrict("H");
		pa.setStateProvince("I");
		pa.setPostalCode("J");
		pa.setLatitude("K");
		pa.setLongitude("L");
		pa.setCountry("M");

		String expected = MOHReportUtil.joinAsSingleCell(
				"A",
				"B",
				"C D",
				"E F",
				"G H",
				"I J",
				"K L",
				"M"
		);

		String actual = (String) new PersonAddressConverter().convert(pa);

		assertThat(actual, is(expected));
	}
}
