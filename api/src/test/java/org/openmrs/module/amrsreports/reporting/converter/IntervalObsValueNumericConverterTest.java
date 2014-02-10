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

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNumeric;
import org.openmrs.Obs;
import org.openmrs.module.amrsreports.MohTestUtils;
import org.openmrs.module.amrsreports.model.SortedObsFromDate;
import org.openmrs.module.amrsreports.reporting.common.ObsDatetimeComparator;
import org.openmrs.module.amrsreports.util.MOHReportUtil;

import java.util.Date;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

public class IntervalObsValueNumericConverterTest {
	/**
	 * @verifies find an observation on the correct date
	 * @see IntervalObsValueNumericConverter#convert(Object)
	 */
	@Test
	public void convert_shouldFindAnObservationOnTheCorrectDate() throws Exception {
		SortedObsFromDate original = new SortedObsFromDate();
		original.setReferenceDate(MohTestUtils.makeDate("01 Jan 2010"));

		ConceptNumeric c = makeConceptNumeric("Weight (KG)", "kg");

		SortedSet<Obs> data = new TreeSet<Obs>(new ObsDatetimeComparator());
		data.add(makeObs(c, 5.0d, MohTestUtils.makeDate("01 Feb 2010")));
		original.setData(data);

		IntervalObsValueNumericConverter converter = new IntervalObsValueNumericConverter(2, 1);

//		Assert.assertEquals("01/02/2010 - 5.00 kg", converter.convert(original));
		Assert.assertEquals("Mth 1) 5.00 kg", converter.convert(original));
	}

	/**
	 * @verifies find an observation before the correct date
	 * @see IntervalObsValueNumericConverter#convert(Object)
	 */
	@Test
	public void convert_shouldFindAnObservationBeforeTheCorrectDate() throws Exception {
		SortedObsFromDate original = new SortedObsFromDate();
		original.setReferenceDate(MohTestUtils.makeDate("01 Jan 2010"));

		ConceptNumeric c = makeConceptNumeric("Weight (KG)", "kg");

		SortedSet<Obs> data = new TreeSet<Obs>(new ObsDatetimeComparator());
		data.add(makeObs(c, 4.2d, MohTestUtils.makeDate("25 Jan 2010")));
		original.setData(data);

		IntervalObsValueNumericConverter converter = new IntervalObsValueNumericConverter(2, 1);

//		Assert.assertEquals("25/01/2010 - 4.20 kg", converter.convert(original));
		Assert.assertEquals("Mth 1) 4.20 kg", converter.convert(original));
	}

	/**
	 * @verifies find an observation after the correct date
	 * @see IntervalObsValueNumericConverter#convert(Object)
	 */
	@Test
	public void convert_shouldFindAnObservationAfterTheCorrectDate() throws Exception {
		SortedObsFromDate original = new SortedObsFromDate();
		original.setReferenceDate(MohTestUtils.makeDate("01 Jan 2010"));

		ConceptNumeric c = makeConceptNumeric("Weight (KG)", "kg");

		SortedSet<Obs> data = new TreeSet<Obs>(new ObsDatetimeComparator());
		data.add(makeObs(c, 3.912d, MohTestUtils.makeDate("05 Feb 2010")));
		original.setData(data);

		IntervalObsValueNumericConverter converter = new IntervalObsValueNumericConverter(2, 1);

//		Assert.assertEquals("05/02/2010 - 3.91 kg", converter.convert(original));
		Assert.assertEquals("Mth 1) 3.91 kg", converter.convert(original));
	}

	/**
	 * @verifies find the observation closest to the correct date
	 * @see IntervalObsValueNumericConverter#convert(Object)
	 */
	@Test
	public void convert_shouldFindTheObservationClosestToTheCorrectDate() throws Exception {
		SortedObsFromDate original = new SortedObsFromDate();
		original.setReferenceDate(MohTestUtils.makeDate("01 Jan 2010"));

		ConceptNumeric c = makeConceptNumeric("Weight (KG)", "kg");

		SortedSet<Obs> data = new TreeSet<Obs>(new ObsDatetimeComparator());
		data.add(makeObs(c, 4.2d, MohTestUtils.makeDate("25 Jan 2010")));
		data.add(makeObs(c, 3.912d, MohTestUtils.makeDate("02 Feb 2010")));
		original.setData(data);

		IntervalObsValueNumericConverter converter = new IntervalObsValueNumericConverter(2, 1);

//		Assert.assertEquals("02/02/2010 - 3.91 kg", converter.convert(original));
		Assert.assertEquals("Mth 1) 3.91 kg", converter.convert(original));
	}

	/**
	 * @verifies not find an observation if not within 2 weeks of the correct date
	 * @see IntervalObsValueNumericConverter#convert(Object)
	 */
	@Test
	public void convert_shouldNotFindAnObservationIfNotWithin2WeeksOfTheCorrectDate() throws Exception {
		SortedObsFromDate original = new SortedObsFromDate();
		original.setReferenceDate(MohTestUtils.makeDate("01 Jan 2010"));

		ConceptNumeric c = makeConceptNumeric("Weight (KG)", "kg");

		SortedSet<Obs> data = new TreeSet<Obs>(new ObsDatetimeComparator());
		data.add(makeObs(c, 4.2d, MohTestUtils.makeDate("05 Jan 2010")));
		data.add(makeObs(c, 3.912d, MohTestUtils.makeDate("20 Feb 2010")));
		original.setData(data);

		IntervalObsValueNumericConverter converter = new IntervalObsValueNumericConverter(2, 1);

		Assert.assertEquals("", converter.convert(original));
	}

	private ConceptNumeric makeConceptNumeric(String name, String units) {
		ConceptNumeric cn = new ConceptNumeric();
		cn.addName(new ConceptName(name, Locale.US));
		cn.setUnits(units);
		return cn;
	}

	private Obs makeObs(Concept c, Double valueNumeric, Date date) {
		Obs o = new Obs();
		o.setConcept(c);
		o.setValueNumeric(valueNumeric);
		o.setObsDatetime(date);
		return o;
	}

	/**
	 * @verifies find observations 48 month intervals after specified interval
	 * @see IntervalObsValueNumericConverter#convert(Object)
	 */
	@Test
	public void convert_shouldFindObservations48MonthIntervalsAfterSpecifiedInterval() throws Exception {
		SortedObsFromDate original = new SortedObsFromDate();
		original.setReferenceDate(MohTestUtils.makeDate("01 Jan 2010"));

		ConceptNumeric c = makeConceptNumeric("Weight (KG)", "kg");

		SortedSet<Obs> data = new TreeSet<Obs>(new ObsDatetimeComparator());
		data.add(makeObs(c, 4.2d, MohTestUtils.makeDate("25 Jan 2010")));
		data.add(makeObs(c, 3.912d, MohTestUtils.makeDate("02 Feb 2010")));
		data.add(makeObs(c, 5.221d, MohTestUtils.makeDate("27 Jan 2014")));
		original.setData(data);

		IntervalObsValueNumericConverter converter = new IntervalObsValueNumericConverter(2, 1);

//		Assert.assertEquals(MOHReportUtil.joinAsSingleCell("02/02/2010 - 3.91 kg", "27/01/2014 - 5.22 kg"),
//				converter.convert(original));
		Assert.assertEquals(MOHReportUtil.joinAsSingleCell("Mth 1) 3.91 kg", "Mth 49) 5.22 kg"),
				converter.convert(original));
	}

	/**
	 * @verifies pick one of multiple observations on the same day
	 * @see IntervalObsValueNumericConverter#convert(Object)
	 */
	@Test
	public void convert_shouldPickOneOfMultipleObservationsOnTheSameDay() throws Exception {
		SortedObsFromDate original = new SortedObsFromDate();
		original.setReferenceDate(MohTestUtils.makeDate("01 Jan 2010"));

		ConceptNumeric c = makeConceptNumeric("CD4, BY FACS", "cells/µL");
		ConceptNumeric d = makeConceptNumeric("CD4%, BY FACS", "%");

		SortedSet<Obs> data = new TreeSet<Obs>(new ObsDatetimeComparator());
		data.add(makeObs(c, 109.0d, MohTestUtils.makeDate("25 Jan 2010")));
		data.add(makeObs(d, 9.0d, MohTestUtils.makeDate("25 Jan 2010")));
		original.setData(data);

		IntervalObsValueNumericConverter converter = new IntervalObsValueNumericConverter(1, 1);

//		Assert.assertEquals("25/01/2010 - 109.0 cells/µL", converter.convert(original));
		Assert.assertEquals("Mth 1) 109.0 cells/µL", converter.convert(original));
	}
}
