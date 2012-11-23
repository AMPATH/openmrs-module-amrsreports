package org.openmrs.module.amrsreport.rule.collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.result.Result;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * test class for MohEntryPointRule
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class MohEntryPointRuleTest {

	private static final int PATIENT_ID = 5;

	private ConceptService conceptService;
	private PatientService patientService;
	private Patient patient;
	private MohEntryPointRule rule;
	private PersonAttributeType personAttributeType;

	@Before
	public void setup() {

		// build the patient
		patient = new Patient();

		// build the mock patient service
		patientService = Mockito.mock(PatientService.class);
		Mockito.when(patientService.getPatient(PATIENT_ID)).thenReturn(patient);

		// set up the person attribute type
		personAttributeType = new PersonAttributeType();
		personAttributeType.setPersonAttributeTypeId(1);
		personAttributeType.setFormat("Concept");
		personAttributeType.setName(MohEvaluableNameConstants.POINT_OF_HIV_TESTING);

		// build the concept service
		int i = 0;
		conceptService = Mockito.mock(ConceptService.class);
		Mockito.when(conceptService.getConcept(MohEvaluableNameConstants.MOBILE_VOLUNTARY_COUNSELING_AND_TESTING)).thenReturn(new Concept(i++));
		Mockito.when(conceptService.getConcept(MohEvaluableNameConstants.MATERNAL_CHILD_HEALTH_PROGRAM)).thenReturn(new Concept(i++));
		Mockito.when(conceptService.getConcept(MohEvaluableNameConstants.PREVENTION_OF_MOTHER_TO_CHILD_TRANSMISSION_OF_HIV)).thenReturn(new Concept(i++));
		Mockito.when(conceptService.getConcept(MohEvaluableNameConstants.VOLUNTARY_COUNSELING_AND_TESTING_CENTER)).thenReturn(new Concept(i++));
		Mockito.when(conceptService.getConcept(MohEvaluableNameConstants.TUBERCULOSIS)).thenReturn(new Concept(i++));
		Mockito.when(conceptService.getConcept(MohEvaluableNameConstants.HOME_BASED_TESTING_PROGRAM)).thenReturn(new Concept(i++));
		Mockito.when(conceptService.getConcept(MohEvaluableNameConstants.INPATIENT_CARE_OR_HOSPITALIZATION)).thenReturn(new Concept(i++));
		Mockito.when(conceptService.getConcept(MohEvaluableNameConstants.PROVIDER_INITIATED_TESTING_AND_COUNSELING)).thenReturn(new Concept(i++));
		Mockito.when(conceptService.getConcept(MohEvaluableNameConstants.PEDIATRIC_OUTPATIENT_CLINIC)).thenReturn(new Concept(i++));
		Mockito.when(conceptService.getConcept(MohEvaluableNameConstants.OTHER_NON_CODED)).thenReturn(new Concept(i++));
		Mockito.when(conceptService.getConcept("fake")).thenReturn(new Concept(i++));

		// set up Context
		PowerMockito.mockStatic(Context.class);
		Mockito.when(Context.getConceptService()).thenReturn(conceptService);

		// create a rule instance
		rule = new MohEntryPointRule();
		rule.setPatientService(patientService);
	}

	/**
	 * add the given entry point concept's id to the patient as the expected person attribute
	 *
	 * @param entryPoint
	 */
	private void setPatientEntryPoint(String entryPoint) {
		PersonAttribute personAttribute = new PersonAttribute();
		personAttribute.setAttributeType(personAttributeType);
		if (entryPoint == null)
			personAttribute.setValue(null);
		else
			personAttribute.setValue(conceptService.getConcept(entryPoint).getConceptId().toString());
		patient.addAttribute(personAttribute);
	}

	/**
	 * @verifies return MVCT for Mobile Voluntary Counseling and Testing
	 * @see MohEntryPointRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldReturnMVCTForMobileVoluntaryCounselingAndTesting() throws Exception {
		setPatientEntryPoint(MohEvaluableNameConstants.MOBILE_VOLUNTARY_COUNSELING_AND_TESTING);
		Assert.assertEquals(new Result("MVCT"), rule.evaluate(null, PATIENT_ID, null));
	}

	/**
	 * @verifies return MCH for Maternal Child Health Program
	 * @see MohEntryPointRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldReturnMCHForMaternalChildHealthProgram() throws Exception {
		setPatientEntryPoint(MohEvaluableNameConstants.MATERNAL_CHILD_HEALTH_PROGRAM);
		Assert.assertEquals(new Result("MCH"), rule.evaluate(null, PATIENT_ID, null));
	}

	/**
	 * @verifies return PMTCT for Prevention of Mother to Child Transmission of HIV
	 * @see MohEntryPointRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldReturnPMTCTForPreventionOfMotherToChildTransmissionOfHIV() throws Exception {
		setPatientEntryPoint(MohEvaluableNameConstants.PREVENTION_OF_MOTHER_TO_CHILD_TRANSMISSION_OF_HIV);
		Assert.assertEquals(new Result("PMTCT"), rule.evaluate(null, PATIENT_ID, null));
	}

	/**
	 * @verifies return VCT for Voluntary Counseling and Testing Center
	 * @see MohEntryPointRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldReturnVCTForVoluntaryCounselingAndTestingCenter() throws Exception {
		setPatientEntryPoint(MohEvaluableNameConstants.VOLUNTARY_COUNSELING_AND_TESTING_CENTER);
		Assert.assertEquals(new Result("VCT"), rule.evaluate(null, PATIENT_ID, null));
	}

	/**
	 * @verifies return TB for Tuberculosis
	 * @see MohEntryPointRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldReturnTBForTuberculosis() throws Exception {
		setPatientEntryPoint(MohEvaluableNameConstants.TUBERCULOSIS);
		Assert.assertEquals(new Result("TB"), rule.evaluate(null, PATIENT_ID, null));
	}

	/**
	 * @verifies return HCT for Home Based Testing Program
	 * @see MohEntryPointRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldReturnHCTForHomeBasedTestingProgram() throws Exception {
		setPatientEntryPoint(MohEvaluableNameConstants.HOME_BASED_TESTING_PROGRAM);
		Assert.assertEquals(new Result("HCT"), rule.evaluate(null, PATIENT_ID, null));
	}

	/**
	 * @verifies return IPD for Inpatient Care or Hospitalization
	 * @see MohEntryPointRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldReturnIPDForInpatientCareOrHospitalization() throws Exception {
		setPatientEntryPoint(MohEvaluableNameConstants.INPATIENT_CARE_OR_HOSPITALIZATION);
		Assert.assertEquals(new Result("IPD"), rule.evaluate(null, PATIENT_ID, null));
	}

	/**
	 * @verifies return PITC for Provider Initiated Testing and Counseling
	 * @see MohEntryPointRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldReturnPITCForProviderInitiatedTestingAndCounseling() throws Exception {
		setPatientEntryPoint(MohEvaluableNameConstants.PROVIDER_INITIATED_TESTING_AND_COUNSELING);
		Assert.assertEquals(new Result("PITC"), rule.evaluate(null, PATIENT_ID, null));
	}

	/**
	 * @verifies return POC for Pediatric Outpatient Clinic
	 * @see MohEntryPointRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldReturnPOCForPediatricOutpatientClinic() throws Exception {
		setPatientEntryPoint(MohEvaluableNameConstants.PEDIATRIC_OUTPATIENT_CLINIC);
		Assert.assertEquals(new Result("POC"), rule.evaluate(null, PATIENT_ID, null));
	}

	/**
	 * @verifies return Other for Other Non Coded
	 * @see MohEntryPointRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldReturnOtherForOtherNonCoded() throws Exception {
		setPatientEntryPoint(MohEvaluableNameConstants.OTHER_NON_CODED);
		Assert.assertEquals(new Result("Other"), rule.evaluate(null, PATIENT_ID, null));
	}

	/**
	 * @verifies return Other if no point of HIV testing exists
	 * @see MohEntryPointRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldReturnOtherIfNoPointOfHIVTestingExists() throws Exception {
		setPatientEntryPoint(null);
		Assert.assertEquals(new Result("Other"), rule.evaluate(null, PATIENT_ID, null));
	}

	/**
	 * @verifies return Other if point of HIV testing is not recognized
	 * @see MohEntryPointRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldReturnOtherIfPointOfHIVTestingIsNotRecognized() throws Exception {
		setPatientEntryPoint("fake");
		Assert.assertEquals(new Result("Other"), rule.evaluate(null, PATIENT_ID, null));
	}
}
