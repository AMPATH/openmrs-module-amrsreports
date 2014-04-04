package org.openmrs.module.amrsreports.reporting.converter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.amrsreports.MohTestUtils;
import org.openmrs.module.amrsreports.cache.MohCacheUtils;
import org.openmrs.module.amrsreports.model.SortedItemsFromDate;
import org.openmrs.module.amrsreports.reporting.common.ObsRepresentation;
import org.openmrs.module.amrsreports.reporting.common.ObsRepresentationDatetimeComparator;
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
		SortedItemsFromDate<ObsRepresentation> original = makeItemsWith(
				MohEvaluableNameConstants.PATIENT_REPORTED_X_RAY_CHEST,
				MohEvaluableNameConstants.NORMAL,
				MohTestUtils.makeDate("05 Jan 2010"));
		TBStatusConverter converter = new TBStatusConverter(1);
		Assert.assertEquals("", converter.convert(original));
	}

	/**
	 * @verifies find an observation on the correct date
	 * @see IntervalObsValueNumericConverter#convert(Object)
	 */
	@Test
	public void convert_shouldFindAnObservationOnTheCorrectDate() throws Exception {
		SortedItemsFromDate<ObsRepresentation> original = makeItemsWith(
				MohEvaluableNameConstants.PATIENT_REPORTED_X_RAY_CHEST,
				MohEvaluableNameConstants.NORMAL,
				MohTestUtils.makeDate("01 Feb 2010"));
		TBStatusConverter converter = new TBStatusConverter(1);
		Assert.assertEquals("Mth 1) 1-No Signs and symptoms", converter.convert(original));
	}

	/**
	 * @verifies find an observation before the correct date
	 * @see IntervalObsValueNumericConverter#convert(Object)
	 */
	@Test
	public void convert_shouldFindAnObservationBeforeTheCorrectDate() throws Exception {
		SortedItemsFromDate<ObsRepresentation> original = makeItemsWith(
				MohEvaluableNameConstants.PATIENT_REPORTED_X_RAY_CHEST,
				MohEvaluableNameConstants.NORMAL,
				MohTestUtils.makeDate("25 Jan 2010"));
		TBStatusConverter converter = new TBStatusConverter(1);
		Assert.assertEquals("Mth 1) 1-No Signs and symptoms", converter.convert(original));
	}

	/**
	 * @verifies find an observation after the correct date
	 * @see IntervalObsValueNumericConverter#convert(Object)
	 */
	@Test
	public void convert_shouldFindAnObservationAfterTheCorrectDate() throws Exception {
		SortedItemsFromDate<ObsRepresentation> original = makeItemsWith(
				MohEvaluableNameConstants.PATIENT_REPORTED_X_RAY_CHEST,
				MohEvaluableNameConstants.NORMAL,
				MohTestUtils.makeDate("05 Feb 2010"));
		TBStatusConverter converter = new TBStatusConverter(1);
		Assert.assertEquals("Mth 1) 1-No Signs and symptoms", converter.convert(original));
	}

	/**
	 * @verifies find the observation closest to the correct date
	 * @see IntervalObsValueNumericConverter#convert(Object)
	 */
	@Test
	public void convert_shouldFindTheObservationClosestToTheCorrectDate() throws Exception {
		SortedItemsFromDate<ObsRepresentation> original = makeItemsWith(
				MohEvaluableNameConstants.COUGH_DURATION_CODED,
				MohEvaluableNameConstants.WEEKS,
				MohTestUtils.makeDate("25 Jan 2010"));
		original.getData().add(makeObsRepresentation(
				MohEvaluableNameConstants.PATIENT_REPORTED_X_RAY_CHEST,
				MohEvaluableNameConstants.NORMAL,
				MohTestUtils.makeDate("02 Feb 2011")));
		TBStatusConverter converter = new TBStatusConverter(1);

		Assert.assertEquals("Mth 1) 2-TB Suspect", converter.convert(original));
	}

	/**
	 * @verifies find the observation indicating that a patient is on treatment
	 * @see IntervalObsValueNumericConverter#convert(Object)
	 */
	@Test
	public void convert_shouldVerifyApatientOnTBTreatmentUsingTreatmentPlan() throws Exception {
		SortedItemsFromDate<ObsRepresentation> original = makeItemsWith(
				MohEvaluableNameConstants.TUBERCULOSIS_TREATMENT_PLAN,
				MohEvaluableNameConstants.START_DRUGS,
				MohTestUtils.makeDate("25 Jan 2010"));
		TBStatusConverter converter = new TBStatusConverter(1);
		Assert.assertEquals("Mth 1) 3-On TB Treatment", converter.convert(original));
	}

	/**
	 * @verifies a patient is on medication when valueCoded is STOP_ALL_MEDICATIONS
	 * @see IntervalObsValueNumericConverter#convert(Object)
	 */
	@Test
	public void convert_shouldVerifyApatientOnTBTreatmentUsingSTOP_ALL_MEDICATIONS() throws Exception {
		SortedItemsFromDate<ObsRepresentation> original = makeItemsWith(
				MohEvaluableNameConstants.TUBERCULOSIS_TREATMENT_PLAN,
				MohEvaluableNameConstants.STOP_ALL_MEDICATIONS,
				MohTestUtils.makeDate("25 Jan 2010"));
		TBStatusConverter converter = new TBStatusConverter(1);
		Assert.assertEquals("Mth 1) 3-On TB Treatment", converter.convert(original));
	}

	/**
	 * @verifies find the observation indicating that a patient is on treatment
	 * @see IntervalObsValueNumericConverter#convert(Object)
	 */
	@Test
	public void convert_shouldVerifyApatientOnTBTreatmentUsingTUBERCULOSIS_TREATMENT_STARTED() throws Exception {
		SortedItemsFromDate<ObsRepresentation> original = makeItemsWith(
				MohEvaluableNameConstants.TUBERCULOSIS_TREATMENT_STARTED,
				MohEvaluableNameConstants.ISONIAZID,
				MohTestUtils.makeDate("25 Jan 2010"));
		TBStatusConverter converter = new TBStatusConverter(1);
		Assert.assertEquals("Mth 1) 3-On TB Treatment", converter.convert(original));
	}

	/**
	 * @verifies find the observation indicating that a patient is a TB suspect
	 * @see IntervalObsValueNumericConverter#convert(Object)
	 */
	@Test
	public void convert_shouldReturnTBSuspectIfHOUSEHOLD_MEMBER_DIAGNOSED_WITH_TUBERCULOSISevaluatesToYES() throws Exception {
		SortedItemsFromDate<ObsRepresentation> original = makeItemsWith(
				MohEvaluableNameConstants.HOUSEHOLD_MEMBER_DIAGNOSED_WITH_TUBERCULOSIS,
				MohEvaluableNameConstants.YES,
				MohTestUtils.makeDate("25 Jan 2010"));
		TBStatusConverter converter = new TBStatusConverter(1);
		Assert.assertEquals("Mth 1) 2-TB Suspect", converter.convert(original));
	}

	/**
	 * @verifies find the observation indicating that a patient has no TB Signs and symptoms
	 * @see IntervalObsValueNumericConverter#convert(Object)
	 */
	@Test
	public void convert_shouldReturnNoSignsAndSymptomsIfHOUSEHOLD_MEMBER_DIAGNOSED_WITH_TUBERCULOSISevaluatesToNO() throws Exception {
		SortedItemsFromDate<ObsRepresentation> original = makeItemsWith(
				MohEvaluableNameConstants.HOUSEHOLD_MEMBER_DIAGNOSED_WITH_TUBERCULOSIS,
				MohEvaluableNameConstants.NO,
				MohTestUtils.makeDate("25 Jan 2010"));
		TBStatusConverter converter = new TBStatusConverter(1);
		Assert.assertEquals("Mth 1) 1-No Signs and symptoms", converter.convert(original));
	}

	/**
	 * @verifies find the observation indicating that a patient is a TB suspect
	 * @see IntervalObsValueNumericConverter#convert(Object)
	 */
	@Test
	public void convert_shouldReturnTBSuspectIfTUBERCULOSIS_DIAGNOSED_SINCE_LAST_VISITevaluatesToYES() throws Exception {
		SortedItemsFromDate<ObsRepresentation> original = makeItemsWith(
				MohEvaluableNameConstants.TUBERCULOSIS_DIAGNOSED_SINCE_LAST_VISIT,
				MohEvaluableNameConstants.YES,
				MohTestUtils.makeDate("25 Jan 2010"));
		TBStatusConverter converter = new TBStatusConverter(1);
		Assert.assertEquals("Mth 1) 2-TB Suspect", converter.convert(original));
	}

	/**
	 * @verifies find the observation indicating that a patient has no TB Signs and symptoms
	 * @see IntervalObsValueNumericConverter#convert(Object)
	 */
	@Test
	public void convert_shouldReturnNoSignsAndSymptomsIfTUBERCULOSIS_DIAGNOSED_THIS_VISITevaluatesToNO() throws Exception {
		SortedItemsFromDate<ObsRepresentation> original = makeItemsWith(
				MohEvaluableNameConstants.TUBERCULOSIS_DIAGNOSED_THIS_VISIT,
				MohEvaluableNameConstants.NO,
				MohTestUtils.makeDate("25 Jan 2010"));
		TBStatusConverter converter = new TBStatusConverter(1);
		Assert.assertEquals("Mth 1) 1-No Signs and symptoms", converter.convert(original));
	}

	/**
	 * @verifies find the observation indicating that a patient is a TB suspect
	 * @see IntervalObsValueNumericConverter#convert(Object)
	 */
	@Test
	public void convert_shouldReturnTBSuspectIfTUBERCULOSIS_DIAGNOSED_THIS_VISITevaluatesToYES() throws Exception {
		SortedItemsFromDate<ObsRepresentation> original = makeItemsWith(
				MohEvaluableNameConstants.TUBERCULOSIS_DIAGNOSED_THIS_VISIT,
				MohEvaluableNameConstants.YES,
				MohTestUtils.makeDate("25 Jan 2010"));
		TBStatusConverter converter = new TBStatusConverter(1);
		Assert.assertEquals("Mth 1) 2-TB Suspect", converter.convert(original));
	}


	/**
	 * @verifies find the observation indicating that a patient is on Treatment
	 * @see IntervalObsValueNumericConverter#convert(Object)
	 */
	@Test
	public void convert_shouldReturnOnTBTreatmentIfTUBERCULOSIS_DIAGNOSED_THIS_VISITevaluatesToTUBERCULOSIS_TREATMENT_DRUGS() throws Exception {
		SortedItemsFromDate<ObsRepresentation> original = makeItemsWith(
				MohEvaluableNameConstants.TUBERCULOSIS_DIAGNOSED_THIS_VISIT,
				MohEvaluableNameConstants.TUBERCULOSIS_TREATMENT_DRUGS,
				MohTestUtils.makeDate("25 Jan 2010"));
		TBStatusConverter converter = new TBStatusConverter(1);
		Assert.assertEquals("Mth 1) 3-On TB Treatment", converter.convert(original));
	}

	/**
	 * @verifies find the observation indicating that a patient has no signs and symptoms
	 * @see IntervalObsValueNumericConverter#convert(Object)
	 */
	@Test
	public void convert_shouldReturnNoSignsAndSymptomsIfTUBERCULOSIS_DIAGNOSED_SINCE_LAST_VISITevaluatesToNO() throws Exception {
		SortedItemsFromDate<ObsRepresentation> original = makeItemsWith(
				MohEvaluableNameConstants.TUBERCULOSIS_DIAGNOSED_SINCE_LAST_VISIT,
				MohEvaluableNameConstants.NO,
				MohTestUtils.makeDate("25 Jan 2010"));
		TBStatusConverter converter = new TBStatusConverter(1);
		Assert.assertEquals("Mth 1) 1-No Signs and symptoms", converter.convert(original));
	}

	/**
	 * @verifies find the observation indicating that a patient has no signs and symptoms
	 * @see IntervalObsValueNumericConverter#convert(Object)
	 */
	@Test
	public void convert_shouldReturnNoSignsAndSymptomsIfREVIEW_OF_TUBERCULOSIS_SCREENING_QUESTIONSevaluatesToNONE() throws Exception {
		SortedItemsFromDate<ObsRepresentation> original = makeItemsWith(
				MohEvaluableNameConstants.REVIEW_OF_TUBERCULOSIS_SCREENING_QUESTIONS,
				MohEvaluableNameConstants.NONE,
				MohTestUtils.makeDate("25 Jan 2010"));
		TBStatusConverter converter = new TBStatusConverter(1);
		Assert.assertEquals("Mth 1) 1-No Signs and symptoms", converter.convert(original));
	}

	/**
	 * @verifies that a patient had not undergone TB Screening
	 * @see IntervalObsValueNumericConverter#convert(Object)
	 */
	@Test
	public void convert_shouldVerifyNotDoneResult() throws Exception {
		SortedItemsFromDate<ObsRepresentation> original = makeItemsWith(
				MohEvaluableNameConstants.SPUTUM_FOR_AFB,
				MohEvaluableNameConstants.NOT_DONE,
				MohTestUtils.makeDate("25 Jan 2010"));
		TBStatusConverter converter = new TBStatusConverter(1);
		Assert.assertEquals("Mth 1) 4-TB Screening Not Done", converter.convert(original));
	}

	private ObsRepresentation makeObsRepresentation(String conceptName, String conceptAnswer, Date date) {
		ObsRepresentation or = new ObsRepresentation();
		or.setConceptId(MohCacheUtils.getConcept(conceptName).getId());
		or.setValueCodedId(MohCacheUtils.getConcept(conceptAnswer).getId());
		or.setObsDatetime(date);
		return or;
	}

	private SortedItemsFromDate<ObsRepresentation> makeItemsWith(String concept, String value, Date date) {
		SortedSet<ObsRepresentation> data = new TreeSet<ObsRepresentation>(new ObsRepresentationDatetimeComparator());
		data.add(makeObsRepresentation(concept, value, date));

		SortedItemsFromDate<ObsRepresentation> items = new SortedItemsFromDate<ObsRepresentation>();
		items.setReferenceDate(MohTestUtils.makeDate("01 Jan 2010"));
		items.setData(data);

		return items;
	}
}
