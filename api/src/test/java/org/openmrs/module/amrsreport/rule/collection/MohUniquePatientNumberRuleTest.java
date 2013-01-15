package org.openmrs.module.amrsreport.rule.collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.result.Result;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


/**
 * Test file for MohUniquePatientNumberRule class. Should check and return CCC Number for a patient
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class MohUniquePatientNumberRuleTest {

	private static final int PATIENT_ID = 5;
	private Patient patient;
	private PatientIdentifierType cccIdentifierType;

	private static final String CCC_GLOBAL_PROPERTY = "cccgenerator.CCC";
	private static final String CCC_NUMBER = "CCC Number";

	private PatientService patientService;
	private AdministrationService administrationService;

	private MohUniquePatientNumberRule rule;

	@Before
	public void setup() {

		// build the patient
		patient = new Patient();

		// add an initial identifier
		PatientIdentifierType patientIdentifierType = new PatientIdentifierType(0);
		patientIdentifierType.setName("Foo");
		patient.addIdentifier(new PatientIdentifier("Ack", patientIdentifierType, new Location(1)));

		Assert.assertEquals(1, patient.getIdentifiers().size());

		// build the ccc identifier type
		cccIdentifierType = new PatientIdentifierType(1);
		cccIdentifierType.setName(CCC_NUMBER);

		// build the mock patient service
		patientService = Mockito.mock(PatientService.class);
		Mockito.when(patientService.getPatient(PATIENT_ID)).thenReturn(patient);
		Mockito.when(patientService.getPatientIdentifierTypeByName(CCC_NUMBER)).thenReturn(cccIdentifierType);

		// build the mock administration service
		administrationService = Mockito.mock(AdministrationService.class);
		Mockito.when(administrationService.getGlobalProperty(CCC_GLOBAL_PROPERTY)).thenReturn(CCC_NUMBER);

		// set up Context
		PowerMockito.mockStatic(Context.class);
		Mockito.when(Context.getPatientService()).thenReturn(patientService);
		Mockito.when(Context.getAdministrationService()).thenReturn(administrationService);

		// create a rule instance
		rule = new MohUniquePatientNumberRule();
	}

	private void addCCCNumber(String cccNumber) {
		PatientIdentifier pi = new PatientIdentifier(cccNumber, cccIdentifierType, new Location(1));
		patient.addIdentifier(pi);
	}

	/**
	 * @verifies return not found if no Unique Patient Number exists
	 * @see MohUniquePatientNumberRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldReturnNotFoundIfNoUniquePatientNumberExists() throws Exception {
		assertThat(rule.evaluate(null, PATIENT_ID, null), is(new Result("not found")));
	}

	/**
	 * @verifies return a Unique Patient Number
	 * @see MohUniquePatientNumberRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldReturnAUniquePatientNumber() throws Exception {
		addCCCNumber("11740-00001");
		assertThat(rule.evaluate(null, PATIENT_ID, null), is(new Result("11740-00001")));
	}
}
