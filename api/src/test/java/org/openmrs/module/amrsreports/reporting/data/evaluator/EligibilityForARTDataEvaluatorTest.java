package org.openmrs.module.amrsreports.reporting.data.evaluator;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.reporting.data.DateARTStartedDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.EligibilityForARTDataDefinition;
import org.openmrs.module.amrsreports.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreports.snapshot.ARVPatientSnapshot;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.PersonEvaluationContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@link EligibilityForARTDataEvaluator}
 */
@Ignore
@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class EligibilityForARTDataEvaluatorTest {

	private static final List<String> initConcepts = Arrays.asList(
			ARVPatientSnapshot.REASON_CLINICAL,
			ARVPatientSnapshot.REASON_CLINICAL_CD4,
			ARVPatientSnapshot.REASON_CLINICAL_CD4_HIV_DNA_PCR,
			ARVPatientSnapshot.REASON_CLINICAL_HIV_DNA_PCR,

			MohEvaluableNameConstants.ANTIRETROVIRAL_PLAN,
			MohEvaluableNameConstants.START_DRUGS,

			MohEvaluableNameConstants.WHO_STAGE_1_ADULT,
			MohEvaluableNameConstants.WHO_STAGE_2_ADULT,
			MohEvaluableNameConstants.WHO_STAGE_3_ADULT,
			MohEvaluableNameConstants.WHO_STAGE_4_ADULT,
			MohEvaluableNameConstants.WHO_STAGE_1_PEDS,
			MohEvaluableNameConstants.WHO_STAGE_2_PEDS,
			MohEvaluableNameConstants.WHO_STAGE_3_PEDS,
			MohEvaluableNameConstants.WHO_STAGE_4_PEDS,
			MohEvaluableNameConstants.WHO_STAGE_ADULT,
			MohEvaluableNameConstants.WHO_STAGE_PEDS,
			MohEvaluableNameConstants.HIV_DNA_PCR,
			MohEvaluableNameConstants.POSITIVE,
			MohEvaluableNameConstants.CD4_BY_FACS,
			MohEvaluableNameConstants.CD4_PERCENT
	);

	private static final int PATIENT_ID = 5;
	private Patient patient;
	private ConceptService conceptService;
	private PatientService patientService;
	private DataSetQueryService dataSetQueryService;
	private PersonDataService personDataService;
	private List<Object> currentObs;
	private EvaluatedPersonData currentARTStartDates;
	private Date evaluationDate;
	private PersonEvaluationContext evaluationContext;
	private EligibilityForARTDataEvaluator evaluator;
	private PersonDataDefinition definition;

	@Before
	public void setup() throws EvaluationException {

		// initialize the current obs
		currentObs = new ArrayList<Object>();

		// initialize art start dates
		currentARTStartDates = new EvaluatedPersonData();

		// set up the patient
		patient = new Patient();
		patient.setPersonId(PATIENT_ID);
		patient.setPatientId(PATIENT_ID);

		// build the concept service
		conceptService = Mockito.mock(ConceptService.class);

		int i = 0;
		for (String conceptName : initConcepts) {
			Mockito.when(conceptService.getConcept(conceptName)).thenReturn(new Concept(i++));
		}
		Mockito.when(conceptService.getConcept((String) null)).thenReturn(null);

		// build the patient service
		patientService = Mockito.mock(PatientService.class);
		Mockito.when(patientService.getPatient(PATIENT_ID)).thenReturn(patient);

		// build the data set query service
		dataSetQueryService = Mockito.mock(DataSetQueryService.class);
		Mockito.when(dataSetQueryService.executeHqlQuery(Mockito.anyString(), Mockito.anyMap())).thenReturn(currentObs);

		// build the person data service
		personDataService = Mockito.mock(PersonDataService.class);
		Mockito.when(personDataService.evaluate(Mockito.any(DateARTStartedDataDefinition.class), Mockito.any(EvaluationContext.class)))
				.thenReturn(currentARTStartDates);

		// set up Context
		PowerMockito.mockStatic(Context.class);
		Mockito.when(Context.getConceptService()).thenReturn(conceptService);
		Mockito.when(Context.getPatientService()).thenReturn(patientService);
		Mockito.when(Context.getService(DataSetQueryService.class)).thenReturn(dataSetQueryService);
		Mockito.when(Context.getService(PersonDataService.class)).thenReturn(personDataService);

		// set evaluation date
		evaluationDate = makeDate("2013-01-01");

		// set up evaluation context
		evaluationContext = new PersonEvaluationContext(evaluationDate);
		evaluationContext.setBaseCohort(new Cohort(Arrays.asList(PATIENT_ID)));

		// last few reporting required variables
		definition = new EligibilityForARTDataDefinition();
		evaluator = new EligibilityForARTDataEvaluator();
	}

	/**
	 * generate a date from a string
	 *
	 * @param date
	 * @return
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
	 * adds an observation with the given date as the obs datetime
	 *
	 * @param concept
	 * @param date
	 */
	private void addObs(String concept, String answer, String date) {
		Obs obs = new Obs();
		obs.setConcept(conceptService.getConcept(concept));
		obs.setValueCoded(conceptService.getConcept(answer));
		obs.setObsDatetime(makeDate(date));
		obs.setPerson(patient);
		currentObs.add(obs);
	}

	/**
	 * adds an observation with the given date as the obs datetime
	 *
	 * @param concept
	 * @param date
	 */
	private void addObsValue(String concept, Double answer, String date) {
		Obs obs = new Obs();
		obs.setConcept(conceptService.getConcept(concept));
		obs.setValueNumeric(answer);
		obs.setObsDatetime(makeDate(date));
		obs.setPerson(patient);
		currentObs.add(obs);
	}

	/**
	 * clears the currentObs
	 */
	private void clearObs() {
		currentObs.clear();
	}

	/**
	 * adds an ART start date
	 */
	private void addARTStartDate(Date date) {
		currentARTStartDates.addData(PATIENT_ID, date);
	}

	/**
	 * @verifies return Clinical and WHO Stage if under 12 and PEDS WHO Stage is 4
	 * @see EligibilityForARTDataEvaluator#evaluate(org.openmrs.module.reporting.data.person.definition.PersonDataDefinition,
	 *      org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	@Test
	public void evaluate_shouldReturnClinicalAndWHOStageIfUnder12AndPEDSWHOStageIs4() throws Exception {
		patient.setBirthdate(makeDate("16 Oct 2012"));

		addObs(MohEvaluableNameConstants.WHO_STAGE_PEDS, MohEvaluableNameConstants.WHO_STAGE_4_PEDS, "16 Nov 2012");

		List<String> extras = Arrays.asList("WHO Stage 4");

		EvaluatedPersonData results = evaluator.evaluate(definition, evaluationContext);
		ARVPatientSnapshot actual = (ARVPatientSnapshot) results.getData().get(PATIENT_ID);

		assertThat((Date) actual.get("lastDate"), is(makeDate("16 Nov 2012")));
		assertThat((String) actual.get("reason"), is(ARVPatientSnapshot.REASON_CLINICAL));
		assertThat((List<String>) actual.get("extras"), is(extras));
	}

	/**
	 * @verifies return CD4 and WHO Stage and CD4 values if under 12 and PEDS WHO Stage is 3 and CD4 is under 500 and CD4
	 * percentage is under 25
	 * @see EligibilityForARTDataEvaluator#evaluate(org.openmrs.module.reporting.data.person.definition.PersonDataDefinition,
	 *      org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	@Test
	public void evaluate_shouldReturnCD4AndWHOStageAndCD4ValuesIfUnder12AndPEDSWHOStageIs3AndCD4IsUnder500AndCD4PercentageIsUnder25() throws Exception {
		patient.setBirthdate(makeDate("16 Oct 2010"));

		addObs(MohEvaluableNameConstants.WHO_STAGE_PEDS, MohEvaluableNameConstants.WHO_STAGE_3_PEDS, "16 Oct 2012");
		addObsValue(MohEvaluableNameConstants.CD4_BY_FACS, 340d, "17 Oct 2012");
		addObsValue(MohEvaluableNameConstants.CD4_PERCENT, 20d, "18 Oct 2012");

		List<String> extras = Arrays.asList(
				"WHO Stage 3",
				"CD4 Count: 340",
				"CD4 %: 20"
		);

		EvaluatedPersonData results = evaluator.evaluate(definition, evaluationContext);
		ARVPatientSnapshot actual = (ARVPatientSnapshot) results.getData().get(PATIENT_ID);

		assertThat((Date) actual.get("lastDate"), is(makeDate("18 Oct 2012")));
		assertThat((String) actual.get("reason"), is(ARVPatientSnapshot.REASON_CLINICAL_CD4));
		assertThat((List<String>) actual.get("extras"), is(extras));
	}

	/**
	 * @verifies return CD4 and HIV DNA PCR and WHO Stage and CD4 and HIV DNA PCR values if under 18 months and PEDS WHO
	 * Stage is 2 and CD4 is under 500 and HIV DNA PCR is positive
	 * @see EligibilityForARTDataEvaluator#evaluate(org.openmrs.module.reporting.data.person.definition.PersonDataDefinition,
	 *      org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	@Test
	public void evaluate_shouldReturnCD4AndHIVDNAPCRAndWHOStageAndCD4AndHIVDNAPCRValuesIfUnder18MonthsAndPEDSWHOStageIs2AndCD4IsUnder500AndHIVDNAPCRIsPositive() throws Exception {
		patient.setBirthdate(makeDate("16 Oct 2012"));

		addObs(MohEvaluableNameConstants.WHO_STAGE_PEDS, MohEvaluableNameConstants.WHO_STAGE_2_PEDS, "16 Oct 2012");
		addObs(MohEvaluableNameConstants.HIV_DNA_PCR, MohEvaluableNameConstants.POSITIVE, "17 Oct 2012");
		addObsValue(MohEvaluableNameConstants.CD4_BY_FACS, 340d, "18 Oct 2012");

		List<String> extras = Arrays.asList(
				"WHO Stage 2",
				"CD4 Count: 340",
				"HIV DNA PCR: Positive"
		);

		EvaluatedPersonData results = evaluator.evaluate(definition, evaluationContext);
		ARVPatientSnapshot actual = (ARVPatientSnapshot) results.getData().get(PATIENT_ID);

		assertThat((Date) actual.get("lastDate"), is(makeDate("18 Oct 2012")));
		assertThat((String) actual.get("reason"), is(ARVPatientSnapshot.REASON_CLINICAL_CD4_HIV_DNA_PCR));
		assertThat((List<String>) actual.get("extras"), is(extras));
	}

	/**
	 * @verifies return HIV DNA PCR and WHO Stage and HIV DNA PCR value if under 18 months and PEDS WHO Stage is 1 and HIV
	 * DNA PCR is positive
	 * @see EligibilityForARTDataEvaluator#evaluate(org.openmrs.module.reporting.data.person.definition.PersonDataDefinition,
	 *      org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	@Test
	public void evaluate_shouldReturnHIVDNAPCRAndWHOStageAndHIVDNAPCRValueIfUnder18MonthsAndPEDSWHOStageIs1AndHIVDNAPCRIsPositive() throws Exception {
		patient.setBirthdate(makeDate("16 Oct 2012"));

		addObs(MohEvaluableNameConstants.WHO_STAGE_PEDS, MohEvaluableNameConstants.WHO_STAGE_1_PEDS, "17 Oct 2012");
		addObs(MohEvaluableNameConstants.HIV_DNA_PCR, MohEvaluableNameConstants.POSITIVE, "18 Oct 2012");

		List<String> extras = Arrays.asList(
				"WHO Stage 1",
				"HIV DNA PCR: Positive"
		);

		EvaluatedPersonData results = evaluator.evaluate(definition, evaluationContext);
		ARVPatientSnapshot actual = (ARVPatientSnapshot) results.getData().get(PATIENT_ID);

		assertThat((Date) actual.get("lastDate"), is(makeDate("18 Oct 2012")));
		assertThat((String) actual.get("reason"), is(ARVPatientSnapshot.REASON_CLINICAL_HIV_DNA_PCR));
		assertThat((List<String>) actual.get("extras"), is(extras));
	}

	/**
	 * @verifies return CD4 and WHO Stage and CD4 percentage values if between 18 months and 5 years and PEDS WHO Stage is
	 * 1 or 2 and CD4 percentage is under 20
	 * @see EligibilityForARTDataEvaluator#evaluate(org.openmrs.module.reporting.data.person.definition.PersonDataDefinition,
	 *      org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	@Test
	public void evaluate_shouldReturnCD4AndWHOStageAndCD4PercentageValuesIfBetween18MonthsAnd5YearsAndPEDSWHOStageIs1Or2AndCD4PercentageIsUnder20() throws Exception {
		patient.setBirthdate(makeDate("16 Oct 2010"));

		addObs(MohEvaluableNameConstants.WHO_STAGE_PEDS, MohEvaluableNameConstants.WHO_STAGE_1_PEDS, "16 Oct 2012");
		addObsValue(MohEvaluableNameConstants.CD4_PERCENT, 19d, "18 Oct 2012");

		List<String> extras = Arrays.asList(
				"WHO Stage 1",
				"CD4 %: 19"
		);

		EvaluatedPersonData results = evaluator.evaluate(definition, evaluationContext);
		ARVPatientSnapshot actual = (ARVPatientSnapshot) results.getData().get(PATIENT_ID);

		assertThat((Date) actual.get("lastDate"), is(makeDate("18 Oct 2012")));
		assertThat((String) actual.get("reason"), is(ARVPatientSnapshot.REASON_CLINICAL_CD4));
		assertThat((List<String>) actual.get("extras"), is(extras));

		clearObs();

		addObs(MohEvaluableNameConstants.WHO_STAGE_PEDS, MohEvaluableNameConstants.WHO_STAGE_2_PEDS, "16 Oct 2012");
		addObsValue(MohEvaluableNameConstants.CD4_PERCENT, 19d, "19 Oct 2012");

		extras = Arrays.asList(
				"WHO Stage 2",
				"CD4 %: 19"
		);

		results = evaluator.evaluate(definition, evaluationContext);
		actual = (ARVPatientSnapshot) results.getData().get(PATIENT_ID);

		assertThat((Date) actual.get("lastDate"), is(makeDate("19 Oct 2012")));
		assertThat((String) actual.get("reason"), is(ARVPatientSnapshot.REASON_CLINICAL_CD4));
		assertThat((List<String>) actual.get("extras"), is(extras));
	}

	/**
	 * @verifies return CD4 and WHO Stage and CD4 percentage values if between 5 years and 12 years and PEDS WHO Stage is 1
	 * or 2 and CD4 percentage is under 25
	 * @see EligibilityForARTDataEvaluator#evaluate(org.openmrs.module.reporting.data.person.definition.PersonDataDefinition,
	 *      org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	@Test
	public void evaluate_shouldReturnCD4AndWHOStageAndCD4PercentageValuesIfBetween5YearsAnd12YearsAndPEDSWHOStageIs1Or2AndCD4PercentageIsUnder25() throws Exception {
		patient.setBirthdate(makeDate("16 Oct 2003"));

		addObs(MohEvaluableNameConstants.WHO_STAGE_PEDS, MohEvaluableNameConstants.WHO_STAGE_1_PEDS, "16 Oct 2012");
		addObsValue(MohEvaluableNameConstants.CD4_PERCENT, 24d, "18 Oct 2012");

		List<String> extras = Arrays.asList(
				"WHO Stage 1",
				"CD4 %: 24"
		);

		EvaluatedPersonData results = evaluator.evaluate(definition, evaluationContext);
		ARVPatientSnapshot actual = (ARVPatientSnapshot) results.getData().get(PATIENT_ID);

		assertThat((Date) actual.get("lastDate"), is(makeDate("18 Oct 2012")));
		assertThat((String) actual.get("reason"), is(ARVPatientSnapshot.REASON_CLINICAL_CD4));
		assertThat((List<String>) actual.get("extras"), is(extras));

		clearObs();

		addObs(MohEvaluableNameConstants.WHO_STAGE_PEDS, MohEvaluableNameConstants.WHO_STAGE_2_PEDS, "16 Oct 2012");
		addObsValue(MohEvaluableNameConstants.CD4_PERCENT, 24d, "19 Oct 2012");

		extras = Arrays.asList(
				"WHO Stage 2",
				"CD4 %: 24"
		);

		results = evaluator.evaluate(definition, evaluationContext);
		actual = (ARVPatientSnapshot) results.getData().get(PATIENT_ID);

		assertThat((Date) actual.get("lastDate"), is(makeDate("19 Oct 2012")));
		assertThat((String) actual.get("reason"), is(ARVPatientSnapshot.REASON_CLINICAL_CD4));
		assertThat((List<String>) actual.get("extras"), is(extras));
	}

	/**
	 * @verifies return Clinical and WHO Stage if over 12 and ADULT WHO Stage is 3 or 4
	 * @see EligibilityForARTDataEvaluator#evaluate(org.openmrs.module.reporting.data.person.definition.PersonDataDefinition,
	 *      org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	@Test
	public void evaluate_shouldReturnClinicalAndWHOStageIfOver12AndADULTWHOStageIs3Or4() throws Exception {
		patient.setBirthdate(makeDate("16 Oct 1975"));

		addObs(MohEvaluableNameConstants.WHO_STAGE_ADULT, MohEvaluableNameConstants.WHO_STAGE_3_ADULT, "16 Oct 2012");

		List<String> extras = Arrays.asList("WHO Stage 3");

		EvaluatedPersonData results = evaluator.evaluate(definition, evaluationContext);
		ARVPatientSnapshot actual = (ARVPatientSnapshot) results.getData().get(PATIENT_ID);

		assertThat((Date) actual.get("lastDate"), is(makeDate("16 Oct 2012")));
		assertThat((String) actual.get("reason"), is(ARVPatientSnapshot.REASON_CLINICAL));
		assertThat((List<String>) actual.get("extras"), is(extras));

		clearObs();

		addObs(MohEvaluableNameConstants.WHO_STAGE_ADULT, MohEvaluableNameConstants.WHO_STAGE_4_ADULT, "17 Oct 2012");

		extras = Arrays.asList("WHO Stage 4");

		results = evaluator.evaluate(definition, evaluationContext);
		actual = (ARVPatientSnapshot) results.getData().get(PATIENT_ID);

		assertThat((Date) actual.get("lastDate"), is(makeDate("17 Oct 2012")));
		assertThat((String) actual.get("reason"), is(ARVPatientSnapshot.REASON_CLINICAL));
		assertThat((List<String>) actual.get("extras"), is(extras));
	}

	/**
	 * @verifies return CD4 and WHO Stage and CD4 value if over 12 and ADULT or PEDS WHO Stage is 1 or 2 and CD4 is under
	 * 350
	 * @see EligibilityForARTDataEvaluator#evaluate(org.openmrs.module.reporting.data.person.definition.PersonDataDefinition,
	 *      org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	@Test
	public void evaluate_shouldReturnCD4AndWHOStageAndCD4ValueIfOver12AndADULTOrPEDSWHOStageIs1Or2AndCD4IsUnder350() throws Exception {
		patient.setBirthdate(makeDate("16 Oct 1975"));

		addObs(MohEvaluableNameConstants.WHO_STAGE_ADULT, MohEvaluableNameConstants.WHO_STAGE_1_ADULT, "16 Oct 2012");
		addObsValue(MohEvaluableNameConstants.CD4_BY_FACS, 300d, "17 Oct 2012");

		List<String> extras = Arrays.asList(
				"WHO Stage 1",
				"CD4 Count: 300"
		);

		EvaluatedPersonData results = evaluator.evaluate(definition, evaluationContext);
		ARVPatientSnapshot actual = (ARVPatientSnapshot) results.getData().get(PATIENT_ID);

		assertThat((Date) actual.get("lastDate"), is(makeDate("17 Oct 2012")));
		assertThat((String) actual.get("reason"), is(ARVPatientSnapshot.REASON_CLINICAL_CD4));
		assertThat((List<String>) actual.get("extras"), is(extras));

		clearObs();

		addObs(MohEvaluableNameConstants.WHO_STAGE_ADULT, MohEvaluableNameConstants.WHO_STAGE_2_ADULT, "16 Oct 2012");
		addObsValue(MohEvaluableNameConstants.CD4_BY_FACS, 300d, "18 Oct 2012");

		extras = Arrays.asList(
				"WHO Stage 2",
				"CD4 Count: 300"
		);

		results = evaluator.evaluate(definition, evaluationContext);
		actual = (ARVPatientSnapshot) results.getData().get(PATIENT_ID);

		assertThat((Date) actual.get("lastDate"), is(makeDate("18 Oct 2012")));
		assertThat((String) actual.get("reason"), is(ARVPatientSnapshot.REASON_CLINICAL_CD4));
		assertThat((List<String>) actual.get("extras"), is(extras));

		clearObs();

		addObs(MohEvaluableNameConstants.WHO_STAGE_PEDS, MohEvaluableNameConstants.WHO_STAGE_1_PEDS, "16 Oct 2012");
		addObsValue(MohEvaluableNameConstants.CD4_BY_FACS, 300d, "19 Oct 2012");

		extras = Arrays.asList(
				"WHO Stage 1",
				"CD4 Count: 300"
		);

		results = evaluator.evaluate(definition, evaluationContext);
		actual = (ARVPatientSnapshot) results.getData().get(PATIENT_ID);

		assertThat((Date) actual.get("lastDate"), is(makeDate("19 Oct 2012")));
		assertThat((String) actual.get("reason"), is(ARVPatientSnapshot.REASON_CLINICAL_CD4));
		assertThat((List<String>) actual.get("extras"), is(extras));

		clearObs();

		addObs(MohEvaluableNameConstants.WHO_STAGE_PEDS, MohEvaluableNameConstants.WHO_STAGE_2_PEDS, "16 Oct 2012");
		addObsValue(MohEvaluableNameConstants.CD4_BY_FACS, 300d, "20 Oct 2012");

		extras = Arrays.asList(
				"WHO Stage 2",
				"CD4 Count: 300"
		);

		results = evaluator.evaluate(definition, evaluationContext);
		actual = (ARVPatientSnapshot) results.getData().get(PATIENT_ID);

		assertThat((Date) actual.get("lastDate"), is(makeDate("20 Oct 2012")));
		assertThat((String) actual.get("reason"), is(ARVPatientSnapshot.REASON_CLINICAL_CD4));
		assertThat((List<String>) actual.get("extras"), is(extras));
	}

	/**
	 * @verifies return reason only when ART started before eligibility date
	 * @see EligibilityForARTDataEvaluator#evaluate(org.openmrs.module.reporting.data.person.definition.PersonDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	@Test
	public void evaluate_shouldReturnReasonOnlyWhenARTStartedBeforeEligibilityDate() throws Exception {
		patient.setBirthdate(makeDate("16 Oct 2012"));

		addObs(MohEvaluableNameConstants.WHO_STAGE_PEDS, MohEvaluableNameConstants.WHO_STAGE_4_PEDS, "16 Nov 2012");
		addARTStartDate(makeDate("15 Nov 2012"));

		List<String> extras = Arrays.asList("WHO Stage 4");

		EvaluatedPersonData results = evaluator.evaluate(definition, evaluationContext);
		ARVPatientSnapshot actual = (ARVPatientSnapshot) results.getData().get(PATIENT_ID);

		assertThat((Date) actual.get("lastDate"), is((Date) null));
		assertThat((String) actual.get("reason"), is(ARVPatientSnapshot.REASON_CLINICAL));
		assertThat((List<String>) actual.get("extras"), is(extras));
	}
}
