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
package org.openmrs.module.amrsreports.reporting.data.evaluator;

import org.openmrs.Obs;
import org.openmrs.logic.result.Result;
import org.openmrs.module.amrsreports.rule.util.MohRuleUtils;
import org.openmrs.module.amrsreports.util.MOHReportUtil;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 */
public abstract class DrugStartStopDataEvaluator implements PersonDataEvaluator {

	private Date safeNext(final Iterator<Date> dateInterator) {
		Date returnValue = null;
		if (dateInterator.hasNext())
			returnValue = dateInterator.next();
		return returnValue;
	}

	protected String buildRangeInformation(final Set<Date> startDates, final Set<Date> stopDates) {
		Iterator<Date> startDateIterator = startDates.iterator();
		Iterator<Date> stopDateIterator = stopDates.iterator();

		List<Date[]> ranges = new ArrayList<Date[]>();

		Date stopDate = safeNext(stopDateIterator);
		Date startDate = safeNext(startDateIterator);

		// TODO: for tomorrow, we will display the first date only!
		if (startDate != null || stopDate != null)
			ranges.add(new Date[]{ startDate, stopDate });

		/*
		do {
			if (stopDate != null) {
				// stop is before start, range is: Unknown - stop date
				if (stopDate.before(startDate)) {
					ranges.add(new Date[]{null, stopDate});
					stopDate = safeNext(stopDateIterator);
				} else {
					// see if the following start date is still before stop date
					Date nextStartDate = safeNext(startDateIterator);
					if (nextStartDate != null && stopDate.after(nextStartDate)) {
						ranges.add(new Date[]{startDate, null});
						startDate = nextStartDate;
					} else {
						ranges.add(new Date[]{startDate, stopDate});
						startDate = nextStartDate;
						stopDate = safeNext(stopDateIterator);
					}
				}
			} else {
				if (startDate != null) {
					// stop date is null and start date is not null, range is: start date - unknown
					ranges.add(new Date[]{startDate, null});
					startDate = safeNext(startDateIterator);
				}
			}
		} while(startDateIterator.hasNext() || stopDateIterator.hasNext());
		*/

		// build the response

		List<String> results = new ArrayList<String>();
		for (Date[] range : ranges) {
			results.add(MohRuleUtils.formatdates(range[0]) + " - " + MohRuleUtils.formatdates(range[1]));
		}

		return MOHReportUtil.joinAsSingleCell(results);
	}
}
