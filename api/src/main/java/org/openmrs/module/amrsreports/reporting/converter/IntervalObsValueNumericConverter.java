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
import org.openmrs.module.amrsreports.util.MOHReportUtil;

import java.util.ArrayList;
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
	 * @should find observations 48 month intervals after specified interval
	 */
	@Override
	public Object convert(Object original) {
		SortedObsFromDate o = (SortedObsFromDate) original;

		if (o == null || o.getData().size() == 0)
			return null;

		// loop through the following logic in 48 month increments until nothing is found
		// TODO do this more efficiently so we only crawl through the list once
		Integer thisInterval = null;
		Boolean found = true;
		List<String> results = new ArrayList<String>();

		while (found) {
			// initialize thisInterval or increase by 48 months
			thisInterval = thisInterval == null ? this.getInterval() : thisInterval + 48;

			// get the reference date
			Calendar c = Calendar.getInstance();
			c.setTime(o.getReferenceDate());
			c.add(Calendar.MONTH, thisInterval);

			// get bounding range for possible values
			Calendar lower = Calendar.getInstance();
			lower.setTime(c.getTime());
			lower.add(Calendar.WEEK_OF_MONTH, -2);

			Calendar upper = Calendar.getInstance();
			upper.setTime(c.getTime());
			upper.add(Calendar.WEEK_OF_MONTH, 2);

			// find the closest obs on either side of the date
			Obs before = null;
			Obs after = null;
			found = false;

			// keep looking through the list unless we found the best obs
			Iterator<Obs> io = o.getData().iterator();
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

			// determine which one (before or after) to show
			if (before == null && found) {
				nullSafeAdd(results, format(after));
			} else if (after == null) {
				nullSafeAdd(results, format(before));
			} else if ((c.getTimeInMillis() - before.getObsDatetime().getTime()) <=
					(after.getObsDatetime().getTime() - c.getTimeInMillis())) {
				nullSafeAdd(results, format(before));
			} else {
				nullSafeAdd(results, format(after));
			}

			// set found flag if we actually found an obs before
			if (before != null) {
				found = true;
			}
		}

		return MOHReportUtil.joinAsSingleCell(results);
	}

	private void nullSafeAdd(List<String> list, String s) {
		if (s != null) {
			list.add(s);
		}
	}

	private String format(Obs after) {
		if (after == null)
			return null;

		Object o = super.convert(after);

		if (o == null)
			return null;

		return String.format("%s - %s", MOHReportUtil.formatdates(after.getObsDatetime()), o);
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
