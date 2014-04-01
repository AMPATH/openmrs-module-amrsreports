package org.openmrs.module.amrsreports.reporting.data.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.module.amrsreports.MohTestUtils;
import org.openmrs.module.amrsreports.cache.MohCacheUtils;
import org.openmrs.module.amrsreports.model.SortedItemsFromDate;
import org.openmrs.module.amrsreports.reporting.common.ObsRepresentation;
import org.openmrs.module.amrsreports.reporting.data.DateARTStartedDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.TBStatusDataDefinition;
import org.openmrs.module.amrsreports.rule.MohEvaluableNameConstants;
import org.openmrs.module.reporting.data.MappedData;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.PersonEvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Test class for TBStatusDataEvaluator class
 */
public class TBStatusDataEvaluatorTest extends BaseModuleContextSensitiveTest {

	protected Patient patient;
	private Date evaluationDate;
	private PersonEvaluationContext evaluationContext;
	private TBStatusDataEvaluator evaluator;
	private TBStatusDataDefinition definition;

	@Before
	public void setUp() throws Exception {

		executeDataSet("datasets/concepts-tb-status.xml");

		patient = MohTestUtils.createTestPatient();

		Cohort c = new Cohort(Collections.singleton(patient.getId()));

		evaluationDate = new Date();
		evaluationContext = new PersonEvaluationContext(evaluationDate);
		evaluationContext.setBaseCohort(c);

		definition = new TBStatusDataDefinition();

		MappedData<DateARTStartedDataDefinition> artDateMap = new MappedData<DateARTStartedDataDefinition>();
		artDateMap.setParameterizable(new DateARTStartedDataDefinition());
		definition.setEffectiveDateDefinition(artDateMap);

		evaluator = new TBStatusDataEvaluator();

	}

	/**
	 * @throws Exception
	 * @should return  NORMAL for PATIENT_REPORTED_X_RAY_CHEST
	 */
	@Test
	public void shouldReturnNORMALForPATIENT_REPORTED_X_RAY_CHEST() throws Exception {
		testFinding(MohEvaluableNameConstants.PATIENT_REPORTED_X_RAY_CHEST, MohEvaluableNameConstants.NORMAL);
	}

	/**
	 * @throws Exception
	 * @should return NEGATIVE for SPUTUM FOR AFB
	 */
	@Test
	public void shouldReturnNEGATIVEForSPUTUM_FOR_AFB() throws Exception {
		testFinding(MohEvaluableNameConstants.SPUTUM_FOR_AFB, MohEvaluableNameConstants.NEGATIVE);
	}

	/**
	 * @throws Exception
	 * @should return WEEKS COUGH DURATION, CODED with WEEKS result
	 */
	@Test
	public void shouldReturnWEEKSForCOUGH_DURATION_CODED() throws Exception {
		testFinding(MohEvaluableNameConstants.COUGH_DURATION_CODED, MohEvaluableNameConstants.WEEKS);
	}

	/**
	 * @throws Exception
	 * @should return value_datetime for COUGH DURATION, CODED with MONTHS result
	 */
	@Test
	public void shouldReturnMONTHSForCOUGH_DURATION_CODED() throws Exception {
		testFinding(MohEvaluableNameConstants.COUGH_DURATION_CODED, MohEvaluableNameConstants.MONTHS);
	}

	/**
	 * @throws Exception
	 * @should return CONTINUOUS for COUGH DURATION, CODED
	 */
	@Test
	public void shouldReturnCONTINUOUSForCOUGH_DURATION_CODED() throws Exception {
		testFinding(MohEvaluableNameConstants.COUGH_DURATION_CODED, MohEvaluableNameConstants.CONTINUOUS);
	}

	/**
	 * @throws Exception
	 * @should return NEGATIVE  for Spatum for AFB result
	 */
	@Test
	public void shouldTestForNegativeSpatumForAFB() throws Exception {
		testFinding(MohEvaluableNameConstants.SPUTUM_FOR_AFB, MohEvaluableNameConstants.NEGATIVE);
	}

	/**
	 * @throws Exception
	 * @should test if REVIEW OF SYSTEMS, GENERAL (1069) = COUGH FOR MORE THAN TWO WEEKS (6171)
	 */
	@Test
	public void shouldTestForREVIEW_OF_SYSTEMS_GENERAL() throws Exception {
		testFinding(MohEvaluableNameConstants.REVIEW_OF_SYSTEMS_GENERAL, MohEvaluableNameConstants.COUGH_FOR_MORE_THAN_TWO_WEEKS);
	}

	/**
	 * @throws Exception
	 * @should test if PATIENT REPORTED X-RAY, CHEST (7178) <> NORMAL (1115)
	 */
	@Test
	public void shouldTestifPATIENT_REPORTED_X_RAY_CHEST_isNormal() throws Exception {
		testFinding(MohEvaluableNameConstants.PATIENT_REPORTED_X_RAY_CHEST, MohEvaluableNameConstants.NORMAL);
	}

	/**
	 * @throws Exception
	 * @should test if TUBERCULOSIS TREATMENT PLAN (1268) <> NONE (1107)
	 */
	@Test
	public void shouldTestifTUBERCULOSIS_TREATMENT_PLAN_isNotNONE() throws Exception {
		testFinding(MohEvaluableNameConstants.TUBERCULOSIS_TREATMENT_PLAN, MohEvaluableNameConstants.START_DRUGS);
	}

	/**
	 * @throws Exception
	 * @should return NOT DONE  for Spatum for AFB result
	 */
	@Test
	public void shouldReturnNotDoneForSpatumForAFB() throws Exception {
		testFinding(MohEvaluableNameConstants.SPUTUM_FOR_AFB, MohEvaluableNameConstants.NOT_DONE);
	}

	private void testFinding(String question, String answer) throws Exception {
		MohTestUtils.addCodedObs(patient, question, answer, "16 Oct 1990");

		SortedItemsFromDate<ObsRepresentation> actual = getActualResult();
		ArrayList<ObsRepresentation> observations = new ArrayList<ObsRepresentation>(actual.getData());

		assertThat(observations.size(), is(1));

		Integer valueCodedId = observations.get(0).getValueCodedId();
		Integer questionId = observations.get(0).getConceptId();

		assertEquals(valueCodedId, MohCacheUtils.getConceptId(answer));
		assertEquals(questionId, MohCacheUtils.getConceptId(question));
	}

	private SortedItemsFromDate<ObsRepresentation> getActualResult() throws EvaluationException {
		EvaluatedPersonData actual = evaluator.evaluate(definition, evaluationContext);
		Map<Integer, Object> data = actual.getData();
		return (SortedItemsFromDate<ObsRepresentation>) data.get(patient.getId());
	}
}
