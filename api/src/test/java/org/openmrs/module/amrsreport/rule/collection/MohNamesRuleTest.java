package org.openmrs.module.amrsreport.rule.collection;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.module.amrsreport.rule.collection.MohNamesRule;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.api.context.Context;
/**
 *Test file for MohNamesRule class
 */
public class MohNamesRuleTest extends BaseModuleContextSensitiveTest {
    /**
     * @verifies return the full name of a patient
     * @see MohNamesRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
     */
    @Test
    public void evaluate_shouldReturnTheFullNameOfAPatient() throws Exception {

        Person person = new Person();


        Patient patient = Context.getPatientService().getPatient(8);
        Assert.assertNotNull("The patient was not found",patient);

        MohNamesRule mohNamesRule = new MohNamesRule();
        String patientName = mohNamesRule.evaluate(null,patient.getId(),null).toString();

        Assert.assertEquals("A different name was returned for the patient","Anet Test Oloo",patientName);




    }
}
