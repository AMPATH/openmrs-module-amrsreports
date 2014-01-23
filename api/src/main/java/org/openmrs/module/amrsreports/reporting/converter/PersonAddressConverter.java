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
	 * <p/>
	 * <pre>
	 * <lineByLineFormat>
	 *     <string>address1</string>
	 *     <string>address2</string>
	 *     <string>address3 cityVillage</string>
	 *     <string>address5 address6</string>
	 *     <string>address4 countyDistrict</string>
	 *     <string>stateProvince postalCode</string>
	 *     <string>latitude longitude</string>
	 *     <string>country</string>
	 * </lineByLineFormat>
	 * </pre>
	 *
	 * @should not include blank lines if data does not exist for that line
	 * @should not have lines with leading or trailing spaces
	 * @should return null for a null input
	 * @should fill in an address in the Kenyan format
	 */
	@Override
	public Object convert(Object original) {
		if (original == null)
			return null;

		PersonAddress pa = (PersonAddress) original;
		List<String> lines = new ArrayList<String>();

		lines.add(ObjectUtil.nvlStr(pa.getAddress1(), ""));
		lines.add(ObjectUtil.nvlStr(pa.getAddress2(), ""));
		lines.add(ObjectUtil.nvlStr(pa.getAddress3(), "") + " " + ObjectUtil.nvlStr(pa.getCityVillage(), ""));
		lines.add(ObjectUtil.nvlStr(pa.getAddress5(), "") + " " + ObjectUtil.nvlStr(pa.getAddress6(), ""));
		lines.add(ObjectUtil.nvlStr(pa.getAddress4(), "") + " " + ObjectUtil.nvlStr(pa.getCountyDistrict(), ""));
		lines.add(ObjectUtil.nvlStr(pa.getStateProvince(), "") + " " + ObjectUtil.nvlStr(pa.getPostalCode(), ""));
		lines.add(ObjectUtil.nvlStr(pa.getLatitude(), "") + " " + ObjectUtil.nvlStr(pa.getLongitude(), ""));
		lines.add(ObjectUtil.nvlStr(pa.getCountry(), ""));

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
