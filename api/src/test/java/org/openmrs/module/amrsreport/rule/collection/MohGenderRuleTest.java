package org.openmrs.module.amrsreport.rule.collection;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Test file for MohGenderRuleTest
 */
public class MohGenderRuleTest extends BaseModuleContextSensitiveTest {
	/**
	 * @verifies get Gender of a patient
	 * @see MohGenderRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldGetGenderOfAPatient() throws Exception {

		Patient patient = Context.getPatientService().getPatient(8);
		Patient patient1 = Context.getPatientService().getPatient(2);

		Assert.assertNotNull("patient returned null", patient);
		Assert.assertNotNull("patient1 returned null", patient1);

		String expectedString = "F";
		String expectedString1 = "M";

		MohGenderRule mohGenderRule = new MohGenderRule();
		String foundString = mohGenderRule.evaluate(null, patient.getId(), null).toString();
		String foundString1 = mohGenderRule.evaluate(null, patient1.getId(), null).toString();

		Assert.assertEquals("Female was not found", expectedString, foundString);
		Assert.assertEquals("Male was not found", expectedString1, foundString1);
	}
}
