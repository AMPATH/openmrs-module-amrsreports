package org.openmrs.module.amrsreports.reporting.converter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Obs;
import org.openmrs.module.amrsreports.MohTestUtils;
import org.openmrs.module.amrsreports.cache.MohCacheUtils;
import org.openmrs.module.amrsreports.model.SortedObsFromDate;
import org.openmrs.module.amrsreports.reporting.common.ObsDatetimeComparator;
import org.openmrs.module.amrsreports.rule.MohEvaluableNameConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Test class for TBStatusConverter
 */
public class TBStatusConverterTest extends BaseModuleContextSensitiveTest {

	@Before
	public void setUp() throws Exception {
		executeDataSet("datasets/concepts-tb-status.xml");
	}

	/**
	 * @verifies not find an observation if not within 2 weeks of the correct date
	 * @see IntervalObsValueNumericConverter#convert(Object)
	 */
	@Test
	public void convert_shouldNotFindAnObservationIfNotWithin2WeeksOfTheCorrectDate() throws Exception {
		SortedObsFromDate original = new SortedObsFromDate();
		original.setReferenceDate(MohTestUtils.makeDate("01 Jan 2010"));

		SortedSet<Obs> data = new TreeSet<Obs>(new ObsDatetimeComparator());
		data.add(makeObs(MohEvaluableNameConstants.PATIENT_REPORTED_X_RAY_CHEST, MohEvaluableNameConstants.NORMAL, MohTestUtils.makeDate("05 Jan 2010")));
		original.setData(data);

		TBStatusConverter converter = new TBStatusConverter(1);

		Assert.assertEquals("", converter.convert(original));
	}

	/**
	 * @verifies find an observation on the correct date
	 * @see IntervalObsValueNumericConverter#convert(Object)
	 */
	@Test
	public void convert_shouldFindAnObservationOnTheCorrectDate() throws Exception {
		SortedObsFromDate original = new SortedObsFromDate();
		original.setReferenceDate(MohTestUtils.makeDate("01 Jan 2010"));

		SortedSet<Obs> data = new TreeSet<Obs>(new ObsDatetimeComparator());
		data.add(makeObs(MohEvaluableNameConstants.PATIENT_REPORTED_X_RAY_CHEST, MohEvaluableNameConstants.NORMAL, MohTestUtils.makeDate("01 Feb 2010")));
		original.setData(data);
		TBStatusConverter converter = new TBStatusConverter(1);

		Assert.assertEquals("Mth 1) 1-No Signs and symptoms", converter.convert(original));
	}

	/**
	 * @verifies find an observation before the correct date
	 * @see IntervalObsValueNumericConverter#convert(Object)
	 */
	@Test
	public void convert_shouldFindAnObservationBeforeTheCorrectDate() throws Exception {
		SortedObsFromDate original = new SortedObsFromDate();
		original.setReferenceDate(MohTestUtils.makeDate("01 Jan 2010"));

		SortedSet<Obs> data = new TreeSet<Obs>(new ObsDatetimeComparator());
		data.add(makeObs(MohEvaluableNameConstants.PATIENT_REPORTED_X_RAY_CHEST, MohEvaluableNameConstants.NORMAL, MohTestUtils.makeDate("25 Jan 2010")));
		original.setData(data);
		TBStatusConverter converter = new TBStatusConverter(1);

		Assert.assertEquals("Mth 1) 1-No Signs and symptoms", converter.convert(original));
	}

	/**
	 * @verifies find an observation after the correct date
	 * @see IntervalObsValueNumericConverter#convert(Object)
	 */
	@Test
	public void convert_shouldFindAnObservationAfterTheCorrectDate() throws Exception {
		SortedObsFromDate original = new SortedObsFromDate();
		original.setReferenceDate(MohTestUtils.makeDate("01 Jan 2010"));

		SortedSet<Obs> data = new TreeSet<Obs>(new ObsDatetimeComparator());
		data.add(makeObs(MohEvaluableNameConstants.PATIENT_REPORTED_X_RAY_CHEST, MohEvaluableNameConstants.NORMAL, MohTestUtils.makeDate("05 Feb 2010")));
		original.setData(data);
		TBStatusConverter converter = new TBStatusConverter(1);

		Assert.assertEquals("Mth 1) 1-No Signs and symptoms", converter.convert(original));
	}

	/**
	 * @verifies find the observation closest to the correct date
	 * @see IntervalObsValueNumericConverter#convert(Object)
	 */
	@Test
	public void convert_shouldFindTheObservationClosestToTheCorrectDate() throws Exception {
		SortedObsFromDate original = new SortedObsFromDate();
		original.setReferenceDate(MohTestUtils.makeDate("01 Jan 2010"));

		SortedSet<Obs> data = new TreeSet<Obs>(new ObsDatetimeComparator());
		data.add(makeObs(MohEvaluableNameConstants.COUGH_DURATION_CODED, MohEvaluableNameConstants.WEEKS, MohTestUtils.makeDate("25 Jan 2010")));
		data.add(makeObs(MohEvaluableNameConstants.PATIENT_REPORTED_X_RAY_CHEST, MohEvaluableNameConstants.NORMAL, MohTestUtils.makeDate("02 Feb 2011")));
		original.setData(data);
		TBStatusConverter converter = new TBStatusConverter(1);

		Assert.assertEquals("Mth 1) 2-TB Suspect", converter.convert(original));
	}

	private Obs makeObs(String conceptName, String conceptAnswer, Date date) {
		Obs o = new Obs();
		o.setConcept(MohCacheUtils.getConcept(conceptName));
		o.setValueCoded(MohCacheUtils.getConcept(conceptAnswer));
		o.setObsDatetime(date);
		return o;
	}
}
