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

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.service.MohCoreService;
import org.openmrs.module.amrsreports.util.MOHReportUtil;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 */
public abstract class DrugStartStopDataEvaluator implements PersonDataEvaluator {

	private Date safeNext(final Iterator<Date> dateIterator, Date evaluationDate) {
		Date returnValue = null;
		while (dateIterator.hasNext() && (returnValue == null || returnValue.after(evaluationDate)))
			returnValue = dateIterator.next();
		return returnValue;
	}

	/**
	 * @should return blank result for no dates found
	 * @should properly format a single start date
	 * @should properly format a single stop date
	 * @should properly format a start and stop date
	 * @should properly format two starts followed by one stop
	 * @should properly format one start followed by two stops
	 * @should properly format two start and stop periods
	 * @should ignore same date in both start and stop dates
	 */
	protected String buildRangeInformation(final Set<Date> startDates, final Set<Date> stopDates, Date evaluationDate) {

		// make copies of the start and stop date sets
		Set<Date> starts = new LinkedHashSet<Date>(startDates);
		Set<Date> stops = new LinkedHashSet<Date>(stopDates);

		// slim down the start and stop date sets to include only unique dates
		starts.removeAll(stopDates);
		stops.removeAll(startDates);

		// use iterators to walk through the dates
		Iterator<Date> startDateIterator = starts.iterator();
		Iterator<Date> stopDateIterator = stops.iterator();

		// this will be used to create the final result
		List<Date[]> ranges = new ArrayList<Date[]>();

		// initialize first dates
		Date startDate = safeNext(startDateIterator, evaluationDate);
		Date stopDate = safeNext(stopDateIterator, evaluationDate);

		// loop through date sets looking for matches
		do {
			if (stopDate != null) {
				// stop is before start, range is: Unknown - stop date
				if (startDate != null) {
					if (stopDate.before(startDate)) {

						// skip stop dates until after the start date
						do {
							stopDate = safeNext(stopDateIterator, evaluationDate);
						} while (stopDate != null && stopDate.before(startDate));

						// stopDate is ready to rock, even if it is null
						ranges.add(new Date[]{startDate, stopDate});

						// get the next stopDate or null
						stopDate = safeNext(stopDateIterator, evaluationDate);

					} else {

						// advance forward in start dates until right before the next stop date
						Date nextStart;
						do {
							nextStart = safeNext(startDateIterator, evaluationDate);
						} while (nextStart != null && nextStart.before(stopDate));

						// at this point, nextStart is after stopDate, so we pair startDate with stopDate
						ranges.add(new Date[]{startDate, stopDate});

						// now update startDate
						startDate = nextStart;

						// ... and get the next stopDate
						stopDate = safeNext(stopDateIterator, evaluationDate);
					}
				} else {

					// no more startDates, so advance forward to final stopDate
					Date nextStop;
					do {
						nextStop = safeNext(stopDateIterator, evaluationDate);
						if (nextStop != null && evaluationDate.after(nextStop))
							stopDate = nextStop;
					} while (nextStop != null && evaluationDate.after(nextStop));

					ranges.add(new Date[]{null, stopDate});

					// flag that we are finished
					stopDate = null;
				}
			} else {
				if (startDate != null) {
					// stop date is null and start date is not null, range is: start date - [blank]
					ranges.add(new Date[]{startDate, null});

					// flag that we are finished
					startDate = null;
				}
			}
		} while (startDate != null || stopDate != null);

		// build the response
		List<String> results = new ArrayList<String>();
		for (Date[] range : ranges) {
			String startDateString = MOHReportUtil.formatdates(range[0]);
			String stopDateString = StringUtils.EMPTY;
			if (range[1] != null)
				stopDateString = MOHReportUtil.formatdates(range[1]);
			results.add(startDateString + " - " + stopDateString);
		}

		return MOHReportUtil.joinAsSingleCell(results);
	}

	protected Set<Date> safeFind(final ListMap<Integer, Date> map, final Integer key) {
		Set<Date> dateSet = new TreeSet<Date>();
		if (map.containsKey(key))
			dateSet.addAll(map.get(key));
		return dateSet;
	}

	/**
	 * executes sql query and generates a ListMap<Integer, Date>
	 */
	protected ListMap<Integer, Date> makeDatesMapFromSQL(String sql, Map<String, Object> substitutions) {
		List<Object> data = Context.getService(MohCoreService.class).executeSqlQuery(sql, substitutions);

		return makeDatesMap(data);
	}

	/**
	 * generates a map of integers to lists of dates, assuming this is the kind of response expected from the SQL
	 */
	protected ListMap<Integer, Date> makeDatesMap(List<Object> data) {
		ListMap<Integer, Date> dateListMap = new ListMap<Integer, Date>();
		for (Object o : data) {
			Object[] parts = (Object[]) o;
			if (parts.length == 2) {
				Integer pId = (Integer) parts[0];
				Date date = (Date) parts[1];
				dateListMap.putInList(pId, date);
			}
		}

		return dateListMap;
	}

}
