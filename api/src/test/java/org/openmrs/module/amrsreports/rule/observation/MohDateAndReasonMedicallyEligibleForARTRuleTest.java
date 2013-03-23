package org.openmrs.module.amrsreports.rule.observation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.result.Result;
import org.openmrs.module.amrsreports.cache.MohCacheInstance;
import org.openmrs.module.amrsreports.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreports.rule.MohResultCacheInstance;
import org.openmrs.module.amrsreports.service.MohCoreService;
import org.openmrs.module.amrsreports.util.MOHReportUtil;
import org.openmrs.module.amrsreports.util.MohFetchRestriction;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Test file for MohDateAndReasonMedicallyEligibleForARTRule.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class MohDateAndReasonMedicallyEligibleForARTRuleTest {

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
	private MohCoreService mohCoreService;
	private MohDateAndReasonMedicallyEligibleForARTRule rule;
	private List<Obs> currentObs;
	private LogicContext logicContext;

	@Before
	public void setup() {

		// initialize the current obs
		currentObs = new ArrayList<Obs>();

		patient = new Patient();
		// build the concept service
		int i = 0;
		conceptService = Mockito.mock(ConceptService.class);
		patientService = Mockito.mock(PatientService.class);


		for (String conceptName : initConcepts) {
			Mockito.when(conceptService.getConcept(conceptName)).thenReturn(new Concept(i++));
		}
		Mockito.when(conceptService.getConcept((String) null)).thenReturn(null);

		Mockito.when(patientService.getPatient(PATIENT_ID)).thenReturn(patient);

		mohCoreService = Mockito.mock(MohCoreService.class);

		Mockito.when(mohCoreService.getPatientObservations(Mockito.eq(PATIENT_ID),
				Mockito.anyMap(), Mockito.any(MohFetchRestriction.class), Mockito.any(Date.class))).thenReturn(currentObs);


		// set up Context
		PowerMockito.mockStatic(Context.class);
		Mockito.when(Context.getConceptService()).thenReturn(conceptService);
		Mockito.when(Context.getService(MohCoreService.class)).thenReturn(mohCoreService);
		Mockito.when(Context.getPatientService()).thenReturn(patientService);

		rule = new MohDateAndReasonMedicallyEligibleForARTRule();

		// initialize logic context
		logicContext = Mockito.mock(LogicContext.class);
		Mockito.when(logicContext.getIndexDate()).thenReturn(makeDate("2013-01-01"));
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
		currentObs.add(obs);
	}

	/**
	 * clears the currentObs
	 */
	private void clearObs() {
		currentObs.clear();
	}

	/**
	 * @verifies return Clinical and WHO Stage if under 12 and PEDS WHO Stage is 4
	 * @see MohDateAndReasonMedicallyEligibleForARTRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldReturnClinicalAndWHOStageIfUnder12AndPEDSWHOStageIs4() throws Exception {
		patient.setBirthdate(makeDate("16 Oct 2012"));

		addObs(MohEvaluableNameConstants.ANTIRETROVIRAL_PLAN, MohEvaluableNameConstants.START_DRUGS, "23 Mar 2013");
		addObs(MohEvaluableNameConstants.WHO_STAGE_PEDS, MohEvaluableNameConstants.WHO_STAGE_4_PEDS, "16 Nov 2012");

		String expected = MOHReportUtil.joinAsSingleCell(
				"16/11/2012",
				ARVPatientSnapshot.REASON_CLINICAL,
				"WHO Stage 4"
		);

		Assert.assertEquals(new Result(expected), rule.evaluate(logicContext, PATIENT_ID, null));
	}

	/**
	 * @verifies return CD4 and WHO Stage and CD4 values if under 12 and PEDS WHO Stage is 3 and CD4 is under 500 and CD4
	 * percentage is under 25
	 * @see MohDateAndReasonMedicallyEligibleForARTRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldReturnCD4AndWHOStageAndCD4ValuesIfUnder12AndPEDSWHOStageIs3AndCD4IsUnder500AndCD4PercentageIsUnder25() throws Exception {
		patient.setBirthdate(makeDate("16 Oct 2010"));

		addObs(MohEvaluableNameConstants.ANTIRETROVIRAL_PLAN, MohEvaluableNameConstants.START_DRUGS, "23 Mar 2013");
		addObs(MohEvaluableNameConstants.WHO_STAGE_PEDS, MohEvaluableNameConstants.WHO_STAGE_3_PEDS, "16 Oct 2012");
		addObsValue(MohEvaluableNameConstants.CD4_BY_FACS, 340d, "16 Oct 2012");
		addObsValue(MohEvaluableNameConstants.CD4_PERCENT, 20d, "16 Oct 2012");

		String expected = MOHReportUtil.joinAsSingleCell(
				"16/10/2012",
				ARVPatientSnapshot.REASON_CLINICAL_CD4,
				"WHO Stage 3",
				"CD4 Count: 340",
				"CD4 %: 20"
		);

		Assert.assertEquals(new Result(expected), rule.evaluate(logicContext, PATIENT_ID, null));
	}

	/**
	 * @verifies return CD4 and HIV DNA PCR and WHO Stage and CD4 and HIV DNA PCR values if under 18 months and PEDS WHO
	 * Stage is 2 and CD4 is under 500 and HIV DNA PCR is positive
	 * @see MohDateAndReasonMedicallyEligibleForARTRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldReturnCD4AndHIVDNAPCRAndWHOStageAndCD4AndHIVDNAPCRValuesIfUnder18MonthsAndPEDSWHOStageIs2AndCD4IsUnder500AndHIVDNAPCRIsPositive() throws Exception {
		patient.setBirthdate(makeDate("16 Oct 2012"));

		addObs(MohEvaluableNameConstants.ANTIRETROVIRAL_PLAN, MohEvaluableNameConstants.START_DRUGS, "23 Mar 2013");
		addObs(MohEvaluableNameConstants.WHO_STAGE_PEDS, MohEvaluableNameConstants.WHO_STAGE_2_PEDS, "16 Oct 2012");
		addObs(MohEvaluableNameConstants.HIV_DNA_PCR, MohEvaluableNameConstants.POSITIVE, "16 Oct 2012");
		addObsValue(MohEvaluableNameConstants.CD4_BY_FACS, 340d, "16 Oct 2012");

		String expected = MOHReportUtil.joinAsSingleCell(
				"16/10/2012",
				ARVPatientSnapshot.REASON_CLINICAL_CD4_HIV_DNA_PCR,
				"WHO Stage 2",
				"CD4 Count: 340",
				"HIV DNA PCR: Positive"
		);

		Assert.assertEquals(new Result(expected), rule.evaluate(logicContext, PATIENT_ID, null));
	}

	/**
	 * @verifies return HIV DNA PCR and WHO Stage and HIV DNA PCR value if under 18 months and PEDS WHO Stage is 1 and HIV
	 * DNA PCR is positive
	 * @see MohDateAndReasonMedicallyEligibleForARTRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldReturnHIVDNAPCRAndWHOStageAndHIVDNAPCRValueIfUnder18MonthsAndPEDSWHOStageIs1AndHIVDNAPCRIsPositive() throws Exception {
		patient.setBirthdate(makeDate("16 Oct 2012"));

		addObs(MohEvaluableNameConstants.ANTIRETROVIRAL_PLAN, MohEvaluableNameConstants.START_DRUGS, "23 Mar 2013");
		addObs(MohEvaluableNameConstants.WHO_STAGE_PEDS, MohEvaluableNameConstants.WHO_STAGE_1_PEDS, "17 Oct 2012");
		addObs(MohEvaluableNameConstants.HIV_DNA_PCR, MohEvaluableNameConstants.POSITIVE, "18 Oct 2012");

		String expected = MOHReportUtil.joinAsSingleCell(
				"18/10/2012",
				ARVPatientSnapshot.REASON_CLINICAL_HIV_DNA_PCR,
				"WHO Stage 1",
				"HIV DNA PCR: Positive"
		);

		Assert.assertEquals(new Result(expected), rule.evaluate(logicContext, PATIENT_ID, null));
	}

	/**
	 * @verifies return CD4 and WHO Stage and CD4 percentage values if between 18 months and 5 years and PEDS WHO Stage is
	 * 1 or 2 and CD4 percentage is under 20
	 * @see MohDateAndReasonMedicallyEligibleForARTRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldReturnCD4AndWHOStageAndCD4PercentageValuesIfBetween18MonthsAnd5YearsAndPEDSWHOStageIs1Or2AndCD4PercentageIsUnder20() throws Exception {
		patient.setBirthdate(makeDate("16 Oct 2010"));

		addObs(MohEvaluableNameConstants.ANTIRETROVIRAL_PLAN, MohEvaluableNameConstants.START_DRUGS, "23 Mar 2013");
		addObs(MohEvaluableNameConstants.WHO_STAGE_PEDS, MohEvaluableNameConstants.WHO_STAGE_1_PEDS, "16 Oct 2012");
		addObsValue(MohEvaluableNameConstants.CD4_PERCENT, 19d, "18 Oct 2012");

		String expected = MOHReportUtil.joinAsSingleCell(
				"18/10/2012",
				ARVPatientSnapshot.REASON_CLINICAL_CD4,
				"WHO Stage 1",
				"CD4 %: 19"
		);

		Assert.assertEquals(new Result(expected), rule.evaluate(logicContext, PATIENT_ID, null));

		clearObs();

		addObs(MohEvaluableNameConstants.ANTIRETROVIRAL_PLAN, MohEvaluableNameConstants.START_DRUGS, "23 Mar 2013");
		addObs(MohEvaluableNameConstants.WHO_STAGE_PEDS, MohEvaluableNameConstants.WHO_STAGE_2_PEDS, "16 Oct 2012");
		addObsValue(MohEvaluableNameConstants.CD4_PERCENT, 19d, "19 Oct 2012");

		expected = MOHReportUtil.joinAsSingleCell(
				"19/10/2012",
				ARVPatientSnapshot.REASON_CLINICAL_CD4,
				"WHO Stage 2",
				"CD4 %: 19"
		);

		Assert.assertEquals(new Result(expected), rule.evaluate(logicContext, PATIENT_ID, null));
	}

	/**
	 * @verifies return CD4 and WHO Stage and CD4 percentage values if between 5 years and 12 years and PEDS WHO Stage is 1
	 * or 2 and CD4 percentage is under 25
	 * @see MohDateAndReasonMedicallyEligibleForARTRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldReturnCD4AndWHOStageAndCD4PercentageValuesIfBetween5YearsAnd12YearsAndPEDSWHOStageIs1Or2AndCD4PercentageIsUnder25() throws Exception {
		patient.setBirthdate(makeDate("16 Oct 2003"));

		addObs(MohEvaluableNameConstants.ANTIRETROVIRAL_PLAN, MohEvaluableNameConstants.START_DRUGS, "23 Mar 2013");
		addObs(MohEvaluableNameConstants.WHO_STAGE_PEDS, MohEvaluableNameConstants.WHO_STAGE_1_PEDS, "16 Oct 2012");
		addObsValue(MohEvaluableNameConstants.CD4_PERCENT, 24d, "18 Oct 2012");

		String expected = MOHReportUtil.joinAsSingleCell(
				"18/10/2012",
				ARVPatientSnapshot.REASON_CLINICAL_CD4,
				"WHO Stage 1",
				"CD4 %: 24"
		);

		Assert.assertEquals(new Result(expected), rule.evaluate(logicContext, PATIENT_ID, null));

		clearObs();

		addObs(MohEvaluableNameConstants.ANTIRETROVIRAL_PLAN, MohEvaluableNameConstants.START_DRUGS, "23 Mar 2013");
		addObs(MohEvaluableNameConstants.WHO_STAGE_PEDS, MohEvaluableNameConstants.WHO_STAGE_2_PEDS, "16 Oct 2012");
		addObsValue(MohEvaluableNameConstants.CD4_PERCENT, 24d, "19 Oct 2012");

		expected = MOHReportUtil.joinAsSingleCell(
				"19/10/2012",
				ARVPatientSnapshot.REASON_CLINICAL_CD4,
				"WHO Stage 2",
				"CD4 %: 24"
		);

		Assert.assertEquals(new Result(expected), rule.evaluate(logicContext, PATIENT_ID, null));
	}

	/**
	 * @verifies return Clinical and WHO Stage if over 12 and ADULT WHO Stage is 3 or 4
	 * @see MohDateAndReasonMedicallyEligibleForARTRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldReturnClinicalAndWHOStageIfOver12AndADULTWHOStageIs3Or4() throws Exception {
		patient.setBirthdate(makeDate("16 Oct 1975"));

		addObs(MohEvaluableNameConstants.ANTIRETROVIRAL_PLAN, MohEvaluableNameConstants.START_DRUGS, "23 Mar 2013");
		addObs(MohEvaluableNameConstants.WHO_STAGE_ADULT, MohEvaluableNameConstants.WHO_STAGE_3_ADULT, "16 Oct 2012");

		String expected = MOHReportUtil.joinAsSingleCell(
				"16/10/2012",
				ARVPatientSnapshot.REASON_CLINICAL,
				"WHO Stage 3"
		);

		Assert.assertEquals(new Result(expected), rule.evaluate(logicContext, PATIENT_ID, null));

		clearObs();

		addObs(MohEvaluableNameConstants.ANTIRETROVIRAL_PLAN, MohEvaluableNameConstants.START_DRUGS, "23 Mar 2013");
		addObs(MohEvaluableNameConstants.WHO_STAGE_ADULT, MohEvaluableNameConstants.WHO_STAGE_4_ADULT, "17 Oct 2012");

		expected = MOHReportUtil.joinAsSingleCell(
				"17/10/2012",
				ARVPatientSnapshot.REASON_CLINICAL,
				"WHO Stage 4"
		);

		Assert.assertEquals(new Result(expected), rule.evaluate(logicContext, PATIENT_ID, null));
	}

	/**
	 * @verifies return CD4 and WHO Stage and CD4 value if over 12 and ADULT or PEDS WHO Stage is 1 or 2 and CD4 is under
	 * 350
	 * @see MohDateAndReasonMedicallyEligibleForARTRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldReturnCD4AndWHOStageAndCD4ValueIfOver12AndADULTOrPEDSWHOStageIs1Or2AndCD4IsUnder350() throws Exception {
		patient.setBirthdate(makeDate("16 Oct 1975"));

		addObs(MohEvaluableNameConstants.ANTIRETROVIRAL_PLAN, MohEvaluableNameConstants.START_DRUGS, "23 Mar 2013");
		addObs(MohEvaluableNameConstants.WHO_STAGE_ADULT, MohEvaluableNameConstants.WHO_STAGE_1_ADULT, "16 Oct 2012");
		addObsValue(MohEvaluableNameConstants.CD4_BY_FACS, 300d, "17 Oct 2012");

		String expected = MOHReportUtil.joinAsSingleCell(
				"17/10/2012",
				ARVPatientSnapshot.REASON_CLINICAL_CD4,
				"WHO Stage 1",
				"CD4 Count: 300"
		);

		Assert.assertEquals(new Result(expected), rule.evaluate(logicContext, PATIENT_ID, null));

		clearObs();

		addObs(MohEvaluableNameConstants.ANTIRETROVIRAL_PLAN, MohEvaluableNameConstants.START_DRUGS, "23 Mar 2013");
		addObs(MohEvaluableNameConstants.WHO_STAGE_ADULT, MohEvaluableNameConstants.WHO_STAGE_2_ADULT, "16 Oct 2012");
		addObsValue(MohEvaluableNameConstants.CD4_BY_FACS, 300d, "18 Oct 2012");

		expected = MOHReportUtil.joinAsSingleCell(
				"18/10/2012",
				ARVPatientSnapshot.REASON_CLINICAL_CD4,
				"WHO Stage 2",
				"CD4 Count: 300"
		);

		Assert.assertEquals(new Result(expected), rule.evaluate(logicContext, PATIENT_ID, null));

		clearObs();

		addObs(MohEvaluableNameConstants.ANTIRETROVIRAL_PLAN, MohEvaluableNameConstants.START_DRUGS, "23 Mar 2013");
		addObs(MohEvaluableNameConstants.WHO_STAGE_PEDS, MohEvaluableNameConstants.WHO_STAGE_1_PEDS, "16 Oct 2012");
		addObsValue(MohEvaluableNameConstants.CD4_BY_FACS, 300d, "19 Oct 2012");

		expected = MOHReportUtil.joinAsSingleCell(
				"19/10/2012",
				ARVPatientSnapshot.REASON_CLINICAL_CD4,
				"WHO Stage 1",
				"CD4 Count: 300"
		);

		Assert.assertEquals(new Result(expected), rule.evaluate(logicContext, PATIENT_ID, null));

		clearObs();

		addObs(MohEvaluableNameConstants.ANTIRETROVIRAL_PLAN, MohEvaluableNameConstants.START_DRUGS, "23 Mar 2013");
		addObs(MohEvaluableNameConstants.WHO_STAGE_PEDS, MohEvaluableNameConstants.WHO_STAGE_2_PEDS, "16 Oct 2012");
		addObsValue(MohEvaluableNameConstants.CD4_BY_FACS, 300d, "20 Oct 2012");

		expected = MOHReportUtil.joinAsSingleCell(
				"20/10/2012",
				ARVPatientSnapshot.REASON_CLINICAL_CD4,
				"WHO Stage 2",
				"CD4 Count: 300"
		);

		Assert.assertEquals(new Result(expected), rule.evaluate(logicContext, PATIENT_ID, null));
	}

	/**
	 * @verifies return reason only when ART started before eligibility date
	 * @see MohDateAndReasonMedicallyEligibleForARTRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldReturnReasonOnlyWhenARTStartedBeforeEligibilityDate() throws Exception {
		patient.setBirthdate(makeDate("16 Oct 2012"));

		addObs(MohEvaluableNameConstants.ANTIRETROVIRAL_PLAN, MohEvaluableNameConstants.START_DRUGS, "23 Mar 2010");
		addObs(MohEvaluableNameConstants.WHO_STAGE_PEDS, MohEvaluableNameConstants.WHO_STAGE_1_PEDS, "17 Oct 2012");
		addObs(MohEvaluableNameConstants.HIV_DNA_PCR, MohEvaluableNameConstants.POSITIVE, "18 Oct 2012");

		String expected = MOHReportUtil.joinAsSingleCell(
				ARVPatientSnapshot.REASON_CLINICAL_HIV_DNA_PCR,
				"WHO Stage 1",
				"HIV DNA PCR: Positive"
		);
		Assert.assertEquals(new Result(expected), rule.evaluate(logicContext, PATIENT_ID, null));
	}
}
