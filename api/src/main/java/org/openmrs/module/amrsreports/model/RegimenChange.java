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

package org.openmrs.module.amrsreports.model;

import org.openmrs.Concept;
import org.openmrs.module.drughistory.Regimen;

import java.util.Date;

public class RegimenChange {
	Regimen regimen;
	Concept reason;
	Date dateOccurred;

	public Regimen getRegimen() {
		return regimen;
	}

	public void setRegimen(Regimen regimen) {
		this.regimen = regimen;
	}

	public Concept getReason() {
		return reason;
	}

	public void setReason(Concept reason) {
		this.reason = reason;
	}

	public Date getDateOccurred() {
		return dateOccurred;
	}

	public void setDateOccurred(Date dateOccurred) {
		this.dateOccurred = dateOccurred;
	}
}
