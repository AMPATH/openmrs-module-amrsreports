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

package org.openmrs.module.amrsreports.reporting.common;

import org.openmrs.Location;

import java.util.Date;
import java.util.Map;

public class EncounterRepresentation extends DataRepresentation {

	private Date encounterDatetime;
	private String locationName;

	public EncounterRepresentation(Map<String, Object> m) {
		super(m);

		encounterDatetime = m.containsKey("encounterDatetime") ? (Date) m.get("encounterDatetime") : null;
		locationName = m.containsKey("locationName") ? (String) m.get("locationName") : null;
	}

	public Date getEncounterDatetime() {
		return encounterDatetime;
	}

	public void setEncounterDatetime(Date encounterDatetime) {
		this.encounterDatetime = encounterDatetime;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
}
