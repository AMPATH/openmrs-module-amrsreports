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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.amrsreports.util.MOHReportUtil;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DrugStartStopDataEvaluatorTest {

	private Set<Date> startDates;
	private Set<Date> stopDates;

	private Date evaluationDate;

	private DrugStartStopDataEvaluator evaluator;

	@Before
	public void setup() {

		// initialize the date lists
		startDates = new TreeSet<Date>();
		stopDates = new TreeSet<Date>();

		// set evaluation date
		evaluationDate = makeDate("2013-01-01");

		// create a drug evaluator
		evaluator = new TestDrugStartStopDateEvaluator();
	}

	/**
	 * generates a date from a string
	 */
	private Date makeDate(String date) {
		try {
			return new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH).parse(date);
		} catch (Exception e) {
			// pass
		}
		return new Date();
	}

	/**
	 * adds a start date
	 */
	private void addStartDate(String date) {
		startDates.add(makeDate(date));
	}

	/**
	 * adds a stop date
	 */
	private void addStopDate(String date) {
		stopDates.add(makeDate(date));
	}


	/**
	 * @verifies return blank result for no dates found
	 * @see DrugStartStopDataEvaluator#buildRangeInformation(java.util.Set, java.util.Set, java.util.Date)
	 */
	@Test
	public void buildRangeInformation_shouldReturnBlankResultForNoDatesFound() throws Exception {
		String expected = "";
		assertThat(evaluator.buildRangeInformation(startDates, stopDates, evaluationDate), is(expected));
	}

	/**
	 * @verifies properly format a single start date
	 * @see DrugStartStopDataEvaluator#buildRangeInformation(java.util.Set, java.util.Set, java.util.Date)
	 */
	@Test
	public void buildRangeInformation_shouldProperlyFormatASingleStartDate() throws Exception {
		addStartDate("16 Oct 1975");
		String expected = "16/10/1975 - ";
		assertThat(evaluator.buildRangeInformation(startDates, stopDates, evaluationDate), is(expected));
	}

	/**
	 * @verifies properly format a single stop date
	 * @see DrugStartStopDataEvaluator#buildRangeInformation(java.util.Set, java.util.Set, java.util.Date)
	 */
	@Test
	public void buildRangeInformation_shouldProperlyFormatASingleStopDate() throws Exception {
		addStopDate("16 Oct 1975");
		String expected = "Unknown - 16/10/1975";
		assertThat(evaluator.buildRangeInformation(startDates, stopDates, evaluationDate), is(expected));
	}

	/**
	 * @verifies properly format a start and stop date
	 * @see DrugStartStopDataEvaluator#buildRangeInformation(java.util.Set, java.util.Set, java.util.Date)
	 */
	@Test
	public void buildRangeInformation_shouldProperlyFormatAStartAndStopDate() throws Exception {
		addStartDate("12 Oct 1975");
		addStopDate("16 Oct 1975");
		String expected = "12/10/1975 - 16/10/1975";
		assertThat(evaluator.buildRangeInformation(startDates, stopDates, evaluationDate), is(expected));
	}

	/**
	 * @verifies properly format two starts followed by one stop
	 * @see DrugStartStopDataEvaluator#buildRangeInformation(java.util.Set, java.util.Set, java.util.Date)
	 */
	@Test
	public void buildRangeInformation_shouldProperlyFormatTwoStartsFollowedByOneStop() throws Exception {
		addStartDate("12 Oct 1975");
		addStartDate("14 Oct 1975");
		addStopDate("16 Oct 1975");

		String expected = MOHReportUtil.joinAsSingleCell(
				"12/10/1975 - 16/10/1975");

		assertThat(evaluator.buildRangeInformation(startDates, stopDates, evaluationDate), is(expected));

		// now test skipping the 14th
		addStartDate("15 Oct 1975");

		assertThat(evaluator.buildRangeInformation(startDates, stopDates, evaluationDate), is(expected));
	}

	/**
	 * @verifies properly format one start followed by two stops
	 * @see DrugStartStopDataEvaluator#buildRangeInformation(java.util.Set, java.util.Set, java.util.Date)
	 */
	@Test
	public void buildRangeInformation_shouldProperlyFormatOneStartFollowedByTwoStops() throws Exception {
		addStartDate("12 Oct 1975");
		addStopDate("14 Oct 1975");
		addStopDate("16 Oct 1975");

		String expected = MOHReportUtil.joinAsSingleCell(
				"12/10/1975 - 14/10/1975",
				"Unknown - 16/10/1975");

		assertThat(evaluator.buildRangeInformation(startDates, stopDates, evaluationDate), is(expected));
	}

	/**
	 * @verifies properly format two start and stop periods
	 * @see DrugStartStopDataEvaluator#buildRangeInformation(java.util.Set, java.util.Set, java.util.Date)
	 */
	@Test
	public void buildRangeInformation_shouldProperlyFormatTwoStartAndStopPeriods() throws Exception {
		addStartDate("12 Oct 1975");
		addStopDate("14 Oct 1975");
		addStartDate("16 Oct 1975");
		addStopDate("18 Oct 1975");

		String expected = MOHReportUtil.joinAsSingleCell(
				"12/10/1975 - 14/10/1975",
				"16/10/1975 - 18/10/1975");

		assertThat(evaluator.buildRangeInformation(startDates, stopDates, evaluationDate), is(expected));
	}

	/**
	 * @verifies ignore same date in both start and stop dates
	 * @see DrugStartStopDataEvaluator#buildRangeInformation(java.util.Set, java.util.Set, java.util.Date)
	 */
	@Test
	public void buildRangeInformation_shouldIgnoreSameDateInBothStartAndStopDates() throws Exception {
		addStartDate("12 Oct 1975");
		addStopDate("14 Oct 1975");
		addStartDate("14 Oct 1975");
		addStopDate("18 Oct 1975");

		String expected = MOHReportUtil.joinAsSingleCell(
				"12/10/1975 - 18/10/1975");

		assertThat(evaluator.buildRangeInformation(startDates, stopDates, evaluationDate), is(expected));
	}

	private class TestDrugStartStopDateEvaluator extends DrugStartStopDataEvaluator {

		@Override
		public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
			return null;
		}
	}

}
