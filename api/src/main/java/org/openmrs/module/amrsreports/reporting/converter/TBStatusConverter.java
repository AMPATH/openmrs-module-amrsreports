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
import org.openmrs.module.amrsreports.cache.MohCacheUtils;
import org.openmrs.module.amrsreports.model.SortedObsFromDate;
import org.openmrs.module.amrsreports.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreports.util.MOHReportUtil;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TBStatusConverter implements DataConverter {

	//Define string constants for status codes
	public static final String NO_SIGNS_AND_SYMPTOMS = "1-No Signs and symptoms";
	public static final String TB_SUSPECT = "2-TB Suspect";
	public static final String ON_TREATMENT = "3-On TB Treatment";
	public static final String SCREENING_NOT_DONE = "4-TB Screening Not Done";
	private int interval = 0;

	public TBStatusConverter(int interval) {
		this.setInterval(interval);
	}

	/**
	 * Finds a value nearest the specified interval (in months) from the reference date
	 *
	 * @should find an observation on the correct date
	 * @should find an observation before the correct date
	 * @should find an observation after the correct date
	 * @should find the observation closest to the correct date
	 * @should not find an observation if not within 2 weeks of the correct date
	 * @should find observations 48 month intervals after specified interval
	 * @should pick one of multiple observations on the same day
	 */
	@Override
	public Object convert(Object original) {
		SortedObsFromDate o = (SortedObsFromDate) original;

		if (o == null || o.getData().size() == 0)
			return null;

		List<String> results = new ArrayList<String>();

		// get the target and range dates
		Calendar target = Calendar.getInstance();
		Calendar lower = Calendar.getInstance();
		Calendar upper = Calendar.getInstance();

		// find the closest obs on either side of the date
		Obs before = null, after = null;
		Boolean found = false;
		Integer thisInterval = this.getInterval();
		initializeDates(o.getReferenceDate(), target, lower, upper, thisInterval);

		// loop from beginning to end of all observations
		for (Obs current : o.getData()) {

			// avoid the obvious possible issues
			if (current != null && current.getObsDatetime() != null) {

				Date thisDate = current.getObsDatetime();

				// if the current date is after the range, advance to the next range
				if (found || thisDate.after(upper.getTime())) {

					// update results
					updateResults(results, target, before, after, found, thisInterval);

					// reset variables and move forward 24 months
					do {
						thisInterval += 24;
						initializeDates(o.getReferenceDate(), target, lower, upper, thisInterval);
						before = null;
						after = null;
						found = false;
					} while (thisDate.after(upper.getTime()));
				}

				// check to be sure it is in the right range
				if (thisDate.after(lower.getTime()) && thisDate.before(upper.getTime())) {
					if (thisDate.before(target.getTime())) {
						before = current;
					} else {
						after = current;
						found = true;
					}
				}
			}
		}

		// at this time, if anything remains in the data, we should add it
		updateResults(results, target, before, after, found, thisInterval);

		return MOHReportUtil.joinAsSingleCell(results);
	}

	private void updateResults(List<String> results, Calendar target, Obs before, Obs after, Boolean found, Integer interval) {
		if (before == null && found) {
			nullSafeAdd(results, format(after, interval));
		} else if (after == null) {
			nullSafeAdd(results, format(before, interval));
		} else if ((target.getTimeInMillis() - before.getObsDatetime().getTime()) <=
				(after.getObsDatetime().getTime() - target.getTimeInMillis())) {
			nullSafeAdd(results, format(before, interval));
		} else {
			nullSafeAdd(results, format(after, interval));
		}
	}

	private void initializeDates(Date referenceDate, Calendar target, Calendar lower, Calendar upper, Integer thisInterval) {
		target.setTime(referenceDate);
		target.add(Calendar.MONTH, thisInterval);
		lower.setTime(target.getTime());
		lower.add(Calendar.WEEK_OF_MONTH, -2);
		upper.setTime(target.getTime());
		upper.add(Calendar.WEEK_OF_MONTH, 2);
	}

	private void nullSafeAdd(List<String> list, String s) {
		if (s != null) {
			list.add(s);
		}
	}

	private String format(Obs obs, Integer interval) {
		if (obs == null)
			return null;

		String statusCode = getTBStatusCode(obs);

		if (statusCode.equals(null))
			return null;

		return String.format("Mth %d) %s", interval, statusCode);
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

	protected String getTBStatusCode(Obs obs) {

		Integer conceptId = obs.getConcept().getConceptId();
		Integer conceptAnswer = obs.getValueCoded().getConceptId();

		//check if answer indicates a patient is on tb treatment
		if (conceptAnswer.equals(MohCacheUtils.getConceptId(MohEvaluableNameConstants.STOP_ALL_MEDICATIONS))) {
			return ON_TREATMENT;
		}

        //Check if concept suggests TB screening not done
        if (conceptId.equals(MohCacheUtils.getConceptId(MohEvaluableNameConstants.SPUTUM_FOR_AFB))) {
            if (conceptAnswer.equals(MohCacheUtils.getConceptId(MohEvaluableNameConstants.NOT_DONE))) {
                return SCREENING_NOT_DONE;
            }
        }

		//check for no signs and symptoms
		if (conceptId.equals(MohCacheUtils.getConceptId(MohEvaluableNameConstants.PATIENT_REPORTED_X_RAY_CHEST))) {
			if (conceptAnswer.equals(MohCacheUtils.getConceptId(MohEvaluableNameConstants.NORMAL))) {
				return NO_SIGNS_AND_SYMPTOMS;
			}
		}

		if (conceptId.equals(MohCacheUtils.getConceptId(MohEvaluableNameConstants.SPUTUM_FOR_AFB))) {
			if (conceptAnswer.equals(MohCacheUtils.getConceptId(MohEvaluableNameConstants.NEGATIVE))) {
				return NO_SIGNS_AND_SYMPTOMS;
			}
		}

		//check if it is TB suspect cases
		if (conceptId.equals(MohCacheUtils.getConceptId(MohEvaluableNameConstants.COUGH_DURATION_CODED))) {
			if (conceptAnswer.equals(MohCacheUtils.getConceptId(MohEvaluableNameConstants.WEEKS)) || conceptAnswer.equals(MohCacheUtils.getConceptId(MohEvaluableNameConstants.MONTHS)) || conceptAnswer.equals(MohCacheUtils.getConceptId(MohEvaluableNameConstants.CONTINUOUS))) {
				return TB_SUSPECT;
			}
		}

		if (conceptId.equals(MohCacheUtils.getConceptId(MohEvaluableNameConstants.SPUTUM_FOR_AFB))) {
			if (!conceptAnswer.equals(MohCacheUtils.getConceptId(MohEvaluableNameConstants.NEGATIVE))) {
				return TB_SUSPECT;
			}
		}

		if (conceptId.equals(MohCacheUtils.getConceptId(MohEvaluableNameConstants.REVIEW_OF_SYSTEMS_GENERAL))) {
			if (conceptAnswer.equals(MohCacheUtils.getConceptId(MohEvaluableNameConstants.COUGH_FOR_MORE_THAN_TWO_WEEKS))) {
				return TB_SUSPECT;
			}
		}

		if (conceptId.equals(MohCacheUtils.getConceptId(MohEvaluableNameConstants.PATIENT_REPORTED_X_RAY_CHEST))) {
			if (!conceptAnswer.equals(MohCacheUtils.getConceptId(MohEvaluableNameConstants.NORMAL))) {
				return TB_SUSPECT;
			}
		}

		//check for On Treatment
		if (conceptId.equals(MohCacheUtils.getConceptId(MohEvaluableNameConstants.TUBERCULOSIS_TREATMENT_STARTED))) {
			if (!conceptAnswer.equals(null)) {
				return ON_TREATMENT;
			}
		}

		if (conceptId.equals(MohCacheUtils.getConceptId(MohEvaluableNameConstants.TUBERCULOSIS_TREATMENT_PLAN))) {
			if (!conceptAnswer.equals(MohCacheUtils.getConceptId(MohEvaluableNameConstants.NONE))) {
				return ON_TREATMENT;
			}
		}



		return null;
	}
}
