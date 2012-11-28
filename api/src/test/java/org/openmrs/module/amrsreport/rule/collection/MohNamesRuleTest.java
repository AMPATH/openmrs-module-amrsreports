package org.openmrs.module.amrsreport.rule.collection;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.*;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.module.amrsreport.rule.collection.MohNamesRule;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.api.context.Context;

import org.openmrs.logic.result.Result;
import java.lang.String;
import java.util.Date;

/**
 *Test file for MohNamesRule class
 */
public class MohNamesRuleTest extends BaseModuleContextSensitiveTest {
    /**
     * @should return full names of a patient
     * @verifies that full name of a patient is returned
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

    /**
     * @should return two names for the patient
     * @verifies that only two names are returned
     * @see MohNamesRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
     */
    @Test
    public void evaluate_shouldReturnTwoNames() throws Exception {

        Patient patient = new Patient();
        patient.setPersonId(501);

        Assert.assertNotNull("The patient is null",patient);

        PatientIdentifierType pit = new PatientIdentifierType(2);

        Location location = new Location(2);
        PatientIdentifier pi = new PatientIdentifier("234", pit, location);


        pi.setPatient(patient);
        PatientService ps = Context.getPatientService();
        Patient savedPatient =ps.savePatient(patient);



    }
}
