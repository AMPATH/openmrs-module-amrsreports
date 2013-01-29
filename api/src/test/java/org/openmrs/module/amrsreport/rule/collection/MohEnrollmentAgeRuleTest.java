package org.openmrs.module.amrsreport.rule.collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A test file for MohLostToFollowUpRule class.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class MohEnrollmentAgeRuleTest {

    private String ENCOUNTER_TYPE_ADULT_INITIAL = "ADULTINITIAL";
    private String ENCOUNTER_TYPE_ADULT_RETURN = "ADULTRETURN";
    public String ENCOUNTER_TYPE_PEDIATRIC_INITIAL = "PEDSINITIAL";
    private static final int PATIENT_ID = 11;
    private Patient patient;
    private PatientService patientService;
    private MohCoreService mohCoreService;
    private EncounterService encounterService;

    private MohEnrollmentAgeRule rule;
    private List<Encounter> encounters;


    @Before
    public void setup() {

        encounters = new ArrayList<Encounter>();
        // build the patient
        patient = new Patient();

        // build the mock patient service
        patientService = Mockito.mock(PatientService.class);
        Mockito.when(patientService.getPatient(PATIENT_ID)).thenReturn(patient);


        // build the mock services
        encounterService = Mockito.mock(EncounterService.class);

        Mockito.when(encounterService.getEncounterType(this.ENCOUNTER_TYPE_ADULT_INITIAL)).thenReturn(new EncounterType(0));
        Mockito.when(encounterService.getEncounterType(this.ENCOUNTER_TYPE_ADULT_RETURN)).thenReturn(new EncounterType(1));
        Mockito.when(encounterService.getEncounterType(this.ENCOUNTER_TYPE_PEDIATRIC_INITIAL)).thenReturn(new EncounterType(2));


        // build the MOH Core service
        mohCoreService = Mockito.mock(MohCoreService.class);
        Mockito.when(mohCoreService.getPatientEncounters(Mockito.eq(PATIENT_ID),
                Mockito.anyMap(), Mockito.any(MohFetchRestriction.class))).thenReturn(encounters);

        // set up Context
        PowerMockito.mockStatic(Context.class);
        Mockito.when(Context.getService(MohCoreService.class)).thenReturn(mohCoreService);
        Mockito.when(Context.getPatientService()).thenReturn(patientService);
        Mockito.when(Context.getEncounterService()).thenReturn(encounterService);

        // create a rule instance
        rule = new MohEnrollmentAgeRule();


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
     * adds an encounter with the given type and date as the encounter datetime
     *
     * @param encounterType
     * @param date
     */
    private void addEncounter(String encounterType, String date) {
        Encounter encounter = new Encounter();
        encounter.setEncounterType(encounterService.getEncounterType(encounterType));
        encounter.setEncounterDatetime(makeDate(date));
        encounters.add(encounter);

    }


    /**
     * @verifies return enrollment age for an adult
     * @see MohEnrollmentAgeRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
     */
    @Test
    public void evaluate_shouldReturnEnrollmentAgeForAnAdult() throws Exception {
        patient.setBirthdate(makeDate("16 Oct 1980"));
        addEncounter(this.ENCOUNTER_TYPE_ADULT_INITIAL,"16 Oct 2010");
        Assert.assertEquals("Test for adult tested false",new Result("29y"),rule.evaluate(null,PATIENT_ID,null));

    }

    /**
     * @verifies return enrollment age for a child
     * @see MohEnrollmentAgeRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
     */
    @Test
    public void evaluate_shouldReturnEnrollmentAgeForAChild() throws Exception {
        patient.setBirthdate(makeDate("16 Feb 2012"));
        addEncounter(this.ENCOUNTER_TYPE_PEDIATRIC_INITIAL,"20 Dec 2012");

        Assert.assertEquals("Test for child tested false",new Result("10m"),rule.evaluate(null,PATIENT_ID,null));
    }

    /**
     * @verifies return UNKNOWN result
     * @see MohEnrollmentAgeRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
     */
    @Test
    public void evaluate_shouldReturnUNKNOWNResult() throws Exception {

        addEncounter(this.ENCOUNTER_TYPE_PEDIATRIC_INITIAL,"20 Dec 2012");

        Assert.assertEquals("Test with a null birthdate has tested false",new Result("Unknown"),rule.evaluate(null,PATIENT_ID,null));
    }


}