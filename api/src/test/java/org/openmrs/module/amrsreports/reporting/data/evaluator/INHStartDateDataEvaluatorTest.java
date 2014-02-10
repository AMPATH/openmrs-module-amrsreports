package org.openmrs.module.amrsreports.reporting.data.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.module.amrsreports.AmrsReportsConstants;
import org.openmrs.module.amrsreports.MohTestUtils;
import org.openmrs.module.amrsreports.reporting.data.INHStartDateDataDefinition;
import org.openmrs.module.amrsreports.rule.MohEvaluableNameConstants;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.PersonEvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * test class for INH start date
 */
public class INHStartDateDataEvaluatorTest extends BaseModuleContextSensitiveTest {

	protected Patient patient;
	private PersonEvaluationContext evaluationContext;
	private INHStartDateDataEvaluator evaluator;
	private INHStartDateDataDefinition definition;

	@Before
	public void setUp() throws Exception {
		executeDataSet("datasets/concepts-inh.xml");

		patient = MohTestUtils.createTestPatient();
		Cohort c = new Cohort(Collections.singleton(patient.getId()));

		evaluationContext = new PersonEvaluationContext(new Date());

		evaluationContext.setBaseCohort(c);

		definition = new INHStartDateDataDefinition();
		evaluator = new INHStartDateDataEvaluator();
	}

	/**
	 * @should test for TUBERCULOSIS TREATMENT STARTED date
	 * @should return obs datetime
	 */
	@Test
	public void shouldTestForTUBERCULOSIS_TREATMENT_STARTED() throws Exception {

		MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.TUBERCULOSIS_TREATMENT_STARTED,
				MohEvaluableNameConstants.ISONIAZID, "16 Oct 1975");

		assertEvaluatesTo("1975-10-16 00:00:00.0");
	}

	/**
	 * @should test for CURRENT MEDICATIONS date
	 * @should return obs datetime
	 */
	@Test
	public void shouldTestForCURRENT_MEDICATIONS() throws Exception {

		MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.CURRENT_MEDICATIONS,
				MohEvaluableNameConstants.ISONIAZID, "17 Oct 1975");

		assertEvaluatesTo("1975-10-17 00:00:00.0");
	}

	/**
	 * @should test for PATIENT REPORTED CURRENT TUBERCULOSIS PROPHYLAXIS date
	 * @should return obs datetime
	 */
	@Test
	public void shouldTestForPATIENT_REPORTED_CURRENT_TUBERCULOSIS_PROPHYLAXIS() throws Exception {

		MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.PATIENT_REPORTED_CURRENT_TUBERCULOSIS_PROPHYLAXIS,
				MohEvaluableNameConstants.ISONIAZID, "18 Oct 1975");

		assertEvaluatesTo("1975-10-18 00:00:00.0");
	}

	/**
	 * @should test for PREVIOUS MEDICATIONS USED PAST THREE MONTHS date
	 * @should return obs datetime
	 */
	@Test
	public void shouldTestForPREVIOUS_MEDICATIONS_USED_PAST_THREE_MONTHS() throws Exception {

		MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.PREVIOUS_MEDICATIONS_USED_PAST_THREE_MONTHS,
				MohEvaluableNameConstants.ISONIAZID, "19 Oct 1975");

		assertEvaluatesTo("1975-10-19 00:00:00.0");
	}

	/**
	 * @should test for PPATIENT REPORTED CURRENT TUBERCULOSIS TREATMENT date
	 * @should return obs datetime
	 */
	@Test
	public void shouldTestForPATIENT_REPORTED_CURRENT_TUBERCULOSIS_TREATMENT() throws Exception {

		MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.PATIENT_REPORTED_CURRENT_TUBERCULOSIS_TREATMENT,
				MohEvaluableNameConstants.ISONIAZID, "18 Oct 1976");

		assertEvaluatesTo("1976-10-18 00:00:00.0");
	}

	/**
	 * @should test for PATIENT REPORTED OPPORTUNISTIC INFECTION PROPHYLAXIS date
	 * @should return obs datetime
	 */
	@Test
	public void shouldTestForPATIENT_REPORTED_OPPORTUNISTIC_INFECTION_PROPHYLAXIS() throws Exception {

		MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.PATIENT_REPORTED_OPPORTUNISTIC_INFECTION_PROPHYLAXIS,
				MohEvaluableNameConstants.ISONIAZID, "18 Oct 1979");

		assertEvaluatesTo("1979-10-18 00:00:00.0");
	}

	/**
	 * @should test for TUBERCULOSIS PROPHYLAXIS STARTED date
	 * @should return obs datetime
	 */
	@Test
	public void shouldTestForTUBERCULOSIS_PROPHYLAXIS_STARTED() throws Exception {

		MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.TUBERCULOSIS_PROPHYLAXIS_STARTED,
				MohEvaluableNameConstants.ISONIAZID, "18 Oct 1990");

		assertEvaluatesTo("1990-10-18 00:00:00.0");
	}

	/**
	 * @should test for TUTUBERCULOSIS DRUG TREATMENT START DATE date
	 * @should return obs datetime
	 */
	@Test
	public void shouldTestForTUBERCULOSIS_DRUG_TREATMENT_START_DATE() throws Exception {

		MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.TUBERCULOSIS_DRUG_TREATMENT_START_DATE,
				MohEvaluableNameConstants.ISONIAZID, "18 Oct 1994");

		assertEvaluatesTo("1994-10-18 00:00:00.0");
	}

	/**
	 * @should tests combination of TUTUBERCULOSIS DRUG TREATMENT START DATE and TUBERCULOSIS_TREATMENT_STARTED  date
	 * @should return obs datetime
	 */
	@Test
	public void shouldTestForTUBERCULOSIS_DRUG_TREATMENT_START_DATEagainstTUBERCULOSIS_TREATMENT_STARTED() throws Exception {

		MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.TUBERCULOSIS_DRUG_TREATMENT_START_DATE,
				MohEvaluableNameConstants.ISONIAZID, "18 Oct 1994");

		MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.TUBERCULOSIS_TREATMENT_STARTED,
				MohEvaluableNameConstants.ISONIAZID, "16 Oct 1975");

        EvaluatedPersonData actual = evaluator.evaluate(definition, evaluationContext);
        Map<Integer, Object> data = actual.getData();
        Set<Date> dates = (Set<Date>) data.get(patient.getId());
        ArrayList<Date> patientDates = new ArrayList<Date>(dates);


        assertThat(data.size(), is(1));
        assertThat(patientDates.get(0).toString(), is("1975-10-16 00:00:00.0"));
        assertThat(patientDates.get(1).toString(), is("1994-10-18 00:00:00.0"));
	}


	/**
	 * @should tests combination of PATIENT_REPORTED_OPPORTUNISTIC_INFECTION_PROPHYLAXIS and TUBERCULOSIS_PROPHYLAXIS_STARTED  date
	 * @should return obs datetime
	 *//*
	@Test
	public void shouldTestForPATIENT_REPORTED_OPPORTUNISTIC_INFECTION_PROPHYLAXISagainstTUBERCULOSIS_PROPHYLAXIS_STARTED() throws Exception {

		MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.PATIENT_REPORTED_OPPORTUNISTIC_INFECTION_PROPHYLAXIS,
				MohEvaluableNameConstants.ISONIAZID, "18 Oct 1979");

		MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.TUBERCULOSIS_PROPHYLAXIS_STARTED,
				MohEvaluableNameConstants.ISONIAZID, "18 Oct 1990");

		assertEvaluatesTo("1979-10-18 00:00:00.0");
	}

	*//**
	 * @should tests combination of several conditions  date
	 * @should return obs datetime
	 *//*
	@Test
	public void shouldReturnFirstDateFromAsetOfDates() throws Exception {

		MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.PATIENT_REPORTED_OPPORTUNISTIC_INFECTION_PROPHYLAXIS,
				MohEvaluableNameConstants.ISONIAZID, "18 Oct 1979");

		MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.TUBERCULOSIS_PROPHYLAXIS_STARTED,
				MohEvaluableNameConstants.ISONIAZID, "18 Oct 1990");

		MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.CURRENT_MEDICATIONS,
				MohEvaluableNameConstants.ISONIAZID, "17 Oct 1975");

		MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.TUBERCULOSIS_TREATMENT_STARTED,
				MohEvaluableNameConstants.ISONIAZID, "16 Oct 1975");

		assertEvaluatesTo("1975-10-16 00:00:00.0");
	}
*/
	protected void assertEvaluatesTo(String expected) throws EvaluationException {
		EvaluatedPersonData actual = evaluator.evaluate(definition, evaluationContext);
		Map<Integer, Object> data = actual.getData();
        Set<Date> dates = (Set<Date>) data.get(patient.getId());
		ArrayList<Date> finalVal = new ArrayList<Date>(dates);
		assertThat(data.size(), is(1));
		assertThat(finalVal.get(0).toString(), is(expected));
	}

}
