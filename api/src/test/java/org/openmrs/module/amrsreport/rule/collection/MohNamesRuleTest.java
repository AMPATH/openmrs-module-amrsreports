package org.openmrs.module.amrsreport.rule.collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openmrs.Patient;
import org.openmrs.PersonName;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.result.Result;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Test file for MohNamesRule class
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class MohNamesRuleTest {

	private static final int PATIENT_ID = 5;

	private Patient patient = new Patient();

	private PatientService patientService;

	private MohNamesRule rule;

	@Before
	public void setup() {

		// build the patient service
		patientService = Mockito.mock(PatientService.class);
		Mockito.when(patientService.getPatient(PATIENT_ID)).thenReturn(patient);

		// set up Context
		PowerMockito.mockStatic(Context.class);
		Mockito.when(Context.getPatientService()).thenReturn(patientService);

		// create a rule instance
		rule = new MohNamesRule();
	}

	/**
	 * @verifies return the full name of a patient
	 * @see MohNamesRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldReturnTheFullNameOfAPatient() throws Exception {
		PersonName personName = new PersonName("Frederick", "Robert", "Banks");
		patient.addName(personName);
		assertThat(rule.evaluate(null, PATIENT_ID, null), is(new Result("Frederick Robert Banks")));
	}

}
