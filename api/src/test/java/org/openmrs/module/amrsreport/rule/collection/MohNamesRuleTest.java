package org.openmrs.module.amrsreport.rule.collection;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.module.amrsreport.rule.collection.MohNamesRule;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.api.context.Context;

import org.openmrs.logic.result.Result;
import java.lang.String;

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

        Patient patient = Context.getPatientService().getPatient(8);
        Assert.assertNotNull("The patient was not found",patient);

        MohNamesRule mohNamesRule = new MohNamesRule();
        Result expected =new Result("Anet Test Oloo");
        Result actual = mohNamesRule.evaluate(null,patient.getId(),null);

        Assert.assertNotNull("No name was found for the patient",actual);

        Assert.assertEquals("A different name was returned for the patient",expected,actual);




    }
}
