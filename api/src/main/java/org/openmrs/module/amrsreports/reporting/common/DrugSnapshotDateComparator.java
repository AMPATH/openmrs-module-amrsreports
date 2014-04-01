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

import org.openmrs.module.drughistory.DrugSnapshot;

public class DrugSnapshotDateComparator implements java.util.Comparator<DrugSnapshot> {
	@Override
	public int compare(DrugSnapshot ds1, DrugSnapshot ds2) {

		// null situations

		if (ds1 == null) {
			if (ds2 == null) {
				return 0;
			}
			return 1;
		}

		if (ds2 == null) {
			return -1;
		}

		// date null situations

		if (ds1.getDateTaken() == null) {
			if (ds2.getDateTaken() == null) {
				return 0;
			}
			return 1;
		}

		if (ds2.getDateTaken() == null) {
			return -1;
		}

		return ds1.getDateTaken().compareTo(ds2.getDateTaken());
	}
}
