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

import org.apache.commons.lang.StringUtils;
import org.openmrs.PersonAddress;
import org.openmrs.module.amrsreports.util.MOHReportUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.util.ArrayList;
import java.util.List;

public class PersonAddressConverter implements DataConverter {

	/**
	 * Convert the address into a readable format.  We are using the built-in Kenya template format, but doing it by hand.
	 *
	 * Village: {cityVillage}
	 * Sublocation: {address5}, Location: {address6}
	 * Division: {address4}, District: {countyDistrict}
	 * Province: {stateProvince}, Postal Code: {postalCode}
	 * GPS: {latitude}, {longitude}
	 * Country: {country}
	 *
	 * @should fill in an address in the Kenyan format
	 */
	@Override
	public Object convert(Object original) {
		if (original == null)
			return null;

		String nullValue = "___";
		
		PersonAddress pa = (PersonAddress) original;
		List<String> lines = new ArrayList<String>();

		lines.add("Village: " + ObjectUtil.nvlStr(pa.getCityVillage(), nullValue));
		lines.add("Sublocation: " + ObjectUtil.nvlStr(pa.getAddress5(), nullValue) +
				", Location: " + ObjectUtil.nvlStr(pa.getAddress6(), nullValue));
		lines.add("Division: " + ObjectUtil.nvlStr(pa.getAddress4(), nullValue) +
				", District: " + ObjectUtil.nvlStr(pa.getCountyDistrict(), nullValue));
		lines.add("Province: " + ObjectUtil.nvlStr(pa.getStateProvince(), nullValue) +
				", Postal Code: " + ObjectUtil.nvlStr(pa.getPostalCode(), nullValue));
		lines.add("GPS: " + ObjectUtil.nvlStr(pa.getLatitude(), nullValue) + ", " + ObjectUtil.nvlStr(pa.getLongitude(), nullValue));
		lines.add("Country: " + ObjectUtil.nvlStr(pa.getCountry(), nullValue));

		List<String> out = new ArrayList<String>();

		for (String line : lines) {
			if (StringUtils.isNotBlank(line)) {
				out.add(line.trim());
			}
		}

		return MOHReportUtil.joinAsSingleCell(out);
	}

	@Override
	public Class<?> getInputDataType() {
		return PersonAddress.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}
}
