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

import org.openmrs.Obs;
import org.openmrs.module.amrsreports.model.SortedObsFromDate;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

public class IntervalObsValueNumericConverter extends ObsValueNumericConverter {

	private int interval = 0;

	public IntervalObsValueNumericConverter(int precision, int interval) {
		this.setPrecision(precision);
		this.setInterval(interval);
	}

	/**
	 * Finds a value nearest the specified interval (in months) from the reference date
	 * @should find an observation on the correct date
	 * @should find an observation before the correct date
	 * @should find an observation after the correct date
	 * @should find the observation closest to the correct date
	 * @should not find an observation if not within 2 weeks of the correct date
	 */
	@Override
	public Object convert(Object original) {
		SortedObsFromDate o = (SortedObsFromDate) original;

		if (o == null || o.getData().size() == 0)
			return null;

		// get the reference date
		Calendar c = Calendar.getInstance();
		c.setTime(o.getReferenceDate());
		c.add(Calendar.MONTH, this.getInterval());

		// get bounding range for possible values
		Calendar lower = Calendar.getInstance();
		lower.setTime(c.getTime());
		lower.add(Calendar.WEEK_OF_MONTH, -2);

		Calendar upper = Calendar.getInstance();
		upper.setTime(c.getTime());
		upper.add(Calendar.WEEK_OF_MONTH, 2);

		// find the closest obs on either side of the date
		Iterator<Obs> io = o.getData().iterator();
		Boolean found = false;
		Obs before = null;
		Obs after = null;

		while (io.hasNext() && !found) {
			Obs current = io.next();

			// avoid the obvious possible issues
			if (current != null && current.getObsDatetime() != null) {

				// check to be sure it is in the right range
				Date thisDate = current.getObsDatetime();
				if (thisDate.after(lower.getTime()) && thisDate.before(upper.getTime())) {
					if (thisDate.before(c.getTime())) {
						before = current;
					} else {
						after = current;
						found = true;
					}
				}
			}
		}

		if (before == null) {
			return super.convert(after);
		}

		if (after == null) {
			return super.convert(before);
		}

		if ((c.getTimeInMillis() - before.getObsDatetime().getTime()) <= (after.getObsDatetime().getTime() - c.getTimeInMillis())) {
			return super.convert(before);
		}

		return super.convert(after);
	}

	@Override
	public Class<?> getInputDataType() {
		return SortedObsFromDate.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}
}
