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

import org.openmrs.Obs;

import java.util.Date;
import java.util.SortedSet;

public class SortedObsFromDate {

	private Date referenceDate;
	private SortedSet<Obs> data;

	public Date getReferenceDate() {
		return referenceDate;
	}

	public void setReferenceDate(Date referenceDate) {
		this.referenceDate = referenceDate;
	}

	public SortedSet<Obs> getData() {
		return data;
	}

	public void setData(SortedSet<Obs> data) {
		this.data = data;
	}
}
