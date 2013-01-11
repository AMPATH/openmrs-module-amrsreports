package org.openmrs.module.amrsreport.rule.collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openmrs.*;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.result.Result;
import org.openmrs.module.amrsreport.service.MohCoreService;
import org.openmrs.module.amrsreport.util.MohFetchRestriction;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A test file for MohLostToFollowUpRule class.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class MohLostToFollowUpRuleTest {

    private static final List<String> initConcepts = Arrays.asList(
            LostToFollowUpPatientSnapshot.CONCEPT_DATE_OF_DEATH,
            LostToFollowUpPatientSnapshot.CONCEPT_DECEASED,
            LostToFollowUpPatientSnapshot.CONCEPT_TRANSFER_CARE_TO_OTHER_CENTER,
            LostToFollowUpPatientSnapshot.CONCEPT_RETURN_VISIT_DATE_EXP_CARE_NURSE,
            LostToFollowUpPatientSnapshot.CONCEPT_AMPATH

    );
    private String ENCOUNTER_TYPE_ADULT_INITIAL="ADULTINITIAL";
    private String ENCOUNTER_TYPE_ADULT_RETURN="ADULTRETURN";
    private String ENCOUNTER_TYPE_DEATH_REPORT="DEATHREPORT";


    private static final int PATIENT_ID = 5;
    private Patient patient;
    private PatientService patientService;
    private ConceptService conceptService;
    private MohCoreService mohCoreService;
    private EncounterService encounterService;
    private MohLostToFollowUpRule rule;
    private List<Obs> currentObs;
    private Encounter encounter;

    @Before
    public void setup() {

        currentObs = new ArrayList<Obs>();
        // build the patient
        patient = new Patient();

        // build the mock patient service
        patientService = Mockito.mock(PatientService.class);
        Mockito.when(patientService.getPatient(PATIENT_ID)).thenReturn(patient);

        // build the concept service
        int i = 0;
        conceptService = Mockito.mock(ConceptService.class);
        for (String conceptName : initConcepts) {
            Mockito.when(conceptService.getConcept(conceptName)).thenReturn(new Concept(i++));
        }

        // build the mock  services
        encounterService = Mockito.mock(EncounterService.class);

        Mockito.when(encounterService.getEncounterType(this.ENCOUNTER_TYPE_ADULT_INITIAL)).thenReturn(new EncounterType(0));
        Mockito.when(encounterService.getEncounterType(this.ENCOUNTER_TYPE_ADULT_RETURN)).thenReturn(new EncounterType(1));
        Mockito.when(encounterService.getEncounterType(this.ENCOUNTER_TYPE_DEATH_REPORT)).thenReturn(new EncounterType(2));

        // build the MOH Core service
        mohCoreService = Mockito.mock(MohCoreService.class);
        Mockito.when(mohCoreService.getPatientObservations(Mockito.eq(PATIENT_ID),
            Mockito.anyMap(), Mockito.any(MohFetchRestriction.class))).thenReturn(currentObs);

        // set up Context
        PowerMockito.mockStatic(Context.class);
        Mockito.when(Context.getConceptService()).thenReturn(conceptService);
        Mockito.when(Context.getService(MohCoreService.class)).thenReturn(mohCoreService);
        Mockito.when(Context.getPatientService()).thenReturn(patientService);
        Mockito.when(Context.getEncounterService()).thenReturn(encounterService);

        // create a rule instance
        rule = new MohLostToFollowUpRule();


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
        obs.setEncounter(encounter);
        currentObs.add(obs);
    }

    /**
     * adds a stop observation with the given date as the obs datetime
     *
     * @param conceptName
     * @param date
     */
    private void addObsValueDateTime(String concept,String valueDatetime,String date) {
        Obs obs = new Obs();
        obs.setConcept(conceptService.getConcept(concept));
        obs.setValueDatetime(makeDate(valueDatetime));
        obs.setObsDatetime(makeDate(date));
        currentObs.add(obs);
    }

    /**
     * adds an encounter with the given type and date as the encounter datetime
     *
     * @param encounterType
     * @param date
     */
    private void addEncounter(String encounterType, String date) {
        encounter  = new Encounter();
        encounter.setEncounterType(encounterService.getEncounterType(encounterType));
        encounter.setEncounterDatetime(makeDate(date));

    }


    @Test
    public void consume_shouldProperlyDetermineDEADfromAnEncounter() throws Exception {
        Calendar calendar= Calendar.getInstance();
        calendar.set(2012,Calendar.DECEMBER, 20);

        Date deathDate = calendar.getTime();
        patient.setDead(true);
        patient.setDeathDate(deathDate);

        Assert.assertTrue("The Patient is alive",patient.getDead());

        Result expectedResult = new Result("DEAD | 20-Dec-2012");

        Assert.assertEquals("Result for DEAD not correct",expectedResult, rule.evaluate(null,PATIENT_ID, null));



    }



    @Test
    public void consume_shouldProperlyDetermineTOfromAnObservation() throws Exception {
        addEncounter(this.ENCOUNTER_TYPE_ADULT_INITIAL,"16 Oct 1975" );
        addObs(LostToFollowUpPatientSnapshot.CONCEPT_TRANSFER_CARE_TO_OTHER_CENTER, LostToFollowUpPatientSnapshot.CONCEPT_AMPATH, "16 Oct 1975") ;

        Assert.assertNotNull("A null encounter was encountered", encounter);
        Assert.assertNotNull("A null Obs was encountered", currentObs);

        Assert.assertEquals("Test for TO is wrong ",new Result("TO | (Ampath) 16-Oct-1975"),rule.evaluate(null,PATIENT_ID,null));
    }

    @Test
    public void consume_shouldProperlyDetermineLTFUfromAnObs() throws Exception {

        addObsValueDateTime(LostToFollowUpPatientSnapshot.CONCEPT_RETURN_VISIT_DATE_EXP_CARE_NURSE,"25 Aug 2012","16 Aug 2012");

        Assert.assertNotNull("A null encounter was encountered", currentObs);

        Result expectedRes = new Result("LTFU | 25-Aug-2012");

        Assert.assertEquals("Result for LFTU has tested negative",expectedRes,rule.evaluate(null,PATIENT_ID, null));

    }

}
