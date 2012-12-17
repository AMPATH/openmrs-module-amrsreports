package org.openmrs.module.amrsreport.rule.collection;

import org.junit.Test;
import org.junit.Assert;
import org.openmrs.logic.result.Result;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreport.rule.collection.MohConfirmedHivPositiveDateRule;
import org.openmrs.module.amrsreport.rule.util.MohTestUtils;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptName;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.api.context.Context;

import java.lang.System;
import java.util.Calendar;
import java.util.Date;

/**
 * test file for MohConfirmedHivPositiveDateRule class
 */
public class MohConfirmedHivPositiveDateRuleTest extends BaseModuleContextSensitiveTest {

    ConceptService conceptService = Context.getConceptService();
    /**
     * @verifies return the the first date a patient was confirmed HIV positive
     * @see MohConfirmedHivPositiveDateRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
     */
    @Test
    public void evaluate_shouldReturnTheTheFirstDateAPatientWasConfirmedHIVPositive() throws Exception {

        Patient patient = new Patient();
        patient.setPersonId(2);

        PatientIdentifierType pit = new PatientIdentifierType();
        pit.setPatientIdentifierTypeId(1);

        PatientIdentifier pi = new PatientIdentifier("23452", pit, null);
        pi.setPatient(patient);

        Calendar cal = Calendar.getInstance();
        cal.set(1975,Calendar.JANUARY,1);


        Date birthdate = cal.getTime();
        patient.setBirthdate(birthdate);

        EncounterService service = Context.getEncounterService();

       // System.out.println(birthdate.toString());
        Encounter sampleEncounter = new Encounter();

        Calendar encDate = Calendar.getInstance();
        encDate.set(2012,Calendar.DECEMBER, 10);
        encDate.set(Calendar.HOUR_OF_DAY, 0);
        encDate.set(Calendar.MINUTE,0);
        encDate.set(Calendar.SECOND, 0);


        Date encounterDate = encDate.getTime();



        sampleEncounter.setEncounterDatetime(encounterDate);
        sampleEncounter.setPatient(patient);
        sampleEncounter.setEncounterType(service.getEncounterType("ADULTINITIAL"));

        /*ObsService obsService = Context.getObsService();*/

        Obs obs = new Obs();
        obs.setConcept(conceptService.getConceptByName(MohEvaluableNameConstants.HIV_ENZYME_IMMUNOASSAY_QUALITATIVE));
        obs.setValueCoded(conceptService.getConceptByName(MohEvaluableNameConstants.POSITIVE));

        Calendar obsDate = Calendar.getInstance();
        obsDate.set(2012,Calendar.DECEMBER, 10);
        obsDate.set(Calendar.HOUR_OF_DAY, 0);
        obsDate.set(Calendar.MINUTE,0);
        obsDate.set(Calendar.SECOND, 0);

        Date obDate = obsDate.getTime();
        obs.setObsDatetime(obDate);
        obs.setEncounter(sampleEncounter);

        //sampleEncounter.setObs(allObs);
        Encounter resEncounter = service.saveEncounter(sampleEncounter);

        MohConfirmedHivPositiveDateRule mohConfirmedHivPositiveDateRule = new MohConfirmedHivPositiveDateRule();
        Result result = mohConfirmedHivPositiveDateRule.evaluate(null,patient.getId(), null);


        System.out.println("Obs date is "+obDate.toString());
        System.out.println("Found result is "+ result.toString());

        Result expectedResult = new Result("10/12/2012");

        Assert.assertEquals("Obs date comparison was not successful",expectedResult.toString(), result.toString());

    }

    @Test
    public void evaluate_shouldReturnTheTheFirstDateAPatientWasConfirmedHIVPositiveUsingHIV_RAPID_TEST_QUALITATIVE() throws Exception {


        Patient patient = new Patient();
        patient.setPersonId(2);

        PatientIdentifierType pit = new PatientIdentifierType();
        pit.setPatientIdentifierTypeId(1);

        PatientIdentifier pi = new PatientIdentifier("23452", pit, null);
        pi.setPatient(patient);

        Calendar cal = Calendar.getInstance();
        cal.set(1975,Calendar.JANUARY,1);


        Date birthdate = cal.getTime();
        patient.setBirthdate(birthdate);

        EncounterService service = Context.getEncounterService();

        Encounter sampleEncounter = new Encounter();

        Calendar encDate = Calendar.getInstance();
        encDate.set(2012,Calendar.DECEMBER, 10);
        encDate.set(Calendar.HOUR_OF_DAY, 0);
        encDate.set(Calendar.MINUTE,0);
        encDate.set(Calendar.SECOND, 0);


        Date encounterDate = encDate.getTime();

        sampleEncounter.setEncounterDatetime(encounterDate);
        sampleEncounter.setPatient(patient);
        sampleEncounter.setEncounterType(service.getEncounterType("ADULTINITIAL"));

        /*ObsService obsService = Context.getObsService();*/

        Obs obs = new Obs();
        obs.setConcept(conceptService.getConceptByName(MohEvaluableNameConstants.HIV_RAPID_TEST_QUALITATIVE));
        obs.setValueCoded(conceptService.getConceptByName(MohEvaluableNameConstants.POSITIVE));

        Calendar obsDate = Calendar.getInstance();
        obsDate.set(2012,Calendar.DECEMBER, 10);
        obsDate.set(Calendar.HOUR_OF_DAY, 0);
        obsDate.set(Calendar.MINUTE,0);
        obsDate.set(Calendar.SECOND, 0);

        Date obDate = obsDate.getTime();
        obs.setObsDatetime(obDate);

        obs.setEncounter(sampleEncounter);

        //sampleEncounter.setObs(allObs);
        Encounter resEncounter = service.saveEncounter(sampleEncounter);
        Context.flushSession();


        MohConfirmedHivPositiveDateRule mohConfirmedHivPositiveDateRule = new MohConfirmedHivPositiveDateRule();
        Result result = mohConfirmedHivPositiveDateRule.evaluate(null,patient.getId(), null);
        Result expectedResult = new Result("10/12/2012");

        Assert.assertEquals("The date fetched is incorrect",expectedResult.toString(), result.toString());

    }
}
