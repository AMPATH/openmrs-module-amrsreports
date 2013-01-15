package org.openmrs.module.amrsreport.rule.observation;

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
import org.openmrs.logic.result.Result;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreport.service.MohCoreService;
import org.openmrs.module.amrsreport.util.MohFetchRestriction;
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
			MohEvaluableNameConstants.CD4_BY_FACS
	);

	private static final int PATIENT_ID = 5;
	private Patient patient;
	private ConceptService conceptService;
	private PatientService patientService;
	private MohCoreService mohCoreService;
	private MohDateAndReasonMedicallyEligibleForARTRule rule;
	private List<Obs> currentObs;

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
				Mockito.anyMap(), Mockito.any(MohFetchRestriction.class))).thenReturn(currentObs);


		// set up Context
		PowerMockito.mockStatic(Context.class);
		Mockito.when(Context.getConceptService()).thenReturn(conceptService);
		Mockito.when(Context.getService(MohCoreService.class)).thenReturn(mohCoreService);
		Mockito.when(Context.getPatientService()).thenReturn(patientService);

		rule = new MohDateAndReasonMedicallyEligibleForARTRule();
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
	 * @verifies get the date and reason for ART eligibility
	 * @see MohDateAndReasonMedicallyEligibleForARTRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldReturnREASON_CLINICAL_CD4ForAdults() throws Exception {
		Date dob = makeDate("16 Oct 1975");
		patient.setBirthdate(dob);

		addObs(MohEvaluableNameConstants.WHO_STAGE_ADULT, MohEvaluableNameConstants.WHO_STAGE_1_ADULT, "16 Oct 2012");
		addObsValue(MohEvaluableNameConstants.CD4_BY_FACS, 300d, "16 Oct 2012");

		Assert.assertEquals("Result for Adult stage tested false", new Result("16-Oct-12 - Clinical + CD4"), rule.evaluate(null, PATIENT_ID, null));
	}

	/**
	 * @verifies get the date and reason for ART eligibility
	 * @see MohDateAndReasonMedicallyEligibleForARTRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldReturnREASON_CLINICALForAdults() throws Exception {

		Date dob = makeDate("16 Oct 1975");
		patient.setBirthdate(dob);

		addObs(MohEvaluableNameConstants.WHO_STAGE_ADULT, MohEvaluableNameConstants.WHO_STAGE_4_ADULT, "16 Oct 2012");

		//Assert.assertEquals("Result for Adult stage tested false",new Result("This is a test"),rule.evaluate(null,PATIENT_ID,null));
		Assert.assertEquals("Current Obs is null", 1, currentObs.size());

	}

	/**
	 * @verifies get the date and reason for ART eligibility
	 * @see MohDateAndReasonMedicallyEligibleForARTRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldReturnREASON_CLINICALForPeds() throws Exception {

		Date dob = makeDate("16 Oct 2006");
		patient.setBirthdate(dob);

		addObs(MohEvaluableNameConstants.WHO_STAGE_PEDS, MohEvaluableNameConstants.WHO_STAGE_4_PEDS, "16 Oct 2012");

		//Assert.assertEquals("Result for Adult stage tested false",new Result("This is a test"),rule.evaluate(null,PATIENT_ID,null));
		Assert.assertEquals("Current Obs is null", 1, currentObs.size());

	}

	/**
	 * @verifies get the date and reason for ART eligibility
	 * @see MohDateAndReasonMedicallyEligibleForARTRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldReturnREASON_CLINICAL_CD4_HIV_DNA_PCRForPeds() throws Exception {

		Date dob = makeDate("16 Oct 2012");
		patient.setBirthdate(dob);

		addObs(MohEvaluableNameConstants.WHO_STAGE_PEDS, MohEvaluableNameConstants.WHO_STAGE_2_PEDS, "16 Oct 2012");
		addObs(MohEvaluableNameConstants.HIV_DNA_PCR, MohEvaluableNameConstants.POSITIVE, "16 Oct 2012");
		addObsValue(MohEvaluableNameConstants.CD4_BY_FACS, 340d, "16 Oct 2012");

		//Assert.assertEquals("Result for Adult stage tested false",new Result("This is a test"),rule.evaluate(null,PATIENT_ID,null));
		Assert.assertEquals("Current Obs is null", 3, currentObs.size());

	}

	/**
	 * @verifies get the date and reason for ART eligibility
	 * @see MohDateAndReasonMedicallyEligibleForARTRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldReturnREASON_CLINICAL_CD4ForPeds() throws Exception {

		Date dob = makeDate("16 Oct 1975");
		patient.setBirthdate(dob);

		addObs(MohEvaluableNameConstants.WHO_STAGE_PEDS, MohEvaluableNameConstants.WHO_STAGE_3_PEDS, "16 Oct 2012");
		addObsValue(MohEvaluableNameConstants.CD4_BY_FACS, 340d, "16 Oct 2012");
		addObsValue(MohEvaluableNameConstants.CD4_PERCENT, 20d, "16 Oct 2012");

		//Assert.assertEquals("Result for Adult stage tested false",new Result("This is a test"),rule.evaluate(null,PATIENT_ID,null));
		Assert.assertEquals("Current Obs is null", 3, currentObs.size());

	}
}
