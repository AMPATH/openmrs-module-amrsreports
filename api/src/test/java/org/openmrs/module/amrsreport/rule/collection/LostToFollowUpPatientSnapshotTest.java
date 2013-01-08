package org.openmrs.module.amrsreport.rule.collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.result.Result;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A test file for LostToFollowUpPatientSnapshot class. It contains methods that check if a given
 * Obs is consumed and finally evaluates eligibility for processing
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class LostToFollowUpPatientSnapshotTest {

    private static final List<String> initConcepts = Arrays.asList(
            LostToFollowUpPatientSnapshot.CONCEPT_DECEASED,
            LostToFollowUpPatientSnapshot.CONCEPT_TRANSFER_CARE_TO_OTHER_CENTER,
            LostToFollowUpPatientSnapshot.CONCEPT_RETURN_VISIT_DATE_EXP_CARE_NURSE,
            LostToFollowUpPatientSnapshot.CONCEPT_AMPATH

    );

    private static final int PATIENT_ID = 5;
    private Patient patient;
    private String ENCOUNTER_TYPE_ADULT_INITIAL="ADULTINITIAL";
    private String ENCOUNTER_TYPE_ADULT_RETURN="ADULTRETURN";
    private String ENCOUNTER_TYPE_DEATH_REPORT="DEATHREPORT";

    private ConceptService conceptService;
    private EncounterService encounterService;
    private PatientService patientService;
    private LostToFollowUpPatientSnapshot rule;

    private Obs currentObs;
    private Encounter encounter;


    @Before
    public void setup() {

        // build the patient
        patient = new Patient();

        // build the mock patient service
        patientService = Mockito.mock(PatientService.class);
        Mockito.when(patientService.getPatient(PATIENT_ID)).thenReturn(patient);

        // build the mock  services
        encounterService = Mockito.mock(EncounterService.class);
        conceptService = Mockito.mock(ConceptService.class);

        Mockito.when(encounterService.getEncounterType(this.ENCOUNTER_TYPE_ADULT_INITIAL)).thenReturn(new EncounterType(0));
        Mockito.when(encounterService.getEncounterType(this.ENCOUNTER_TYPE_ADULT_RETURN)).thenReturn(new EncounterType(1));
        Mockito.when(encounterService.getEncounterType(this.ENCOUNTER_TYPE_DEATH_REPORT)).thenReturn(new EncounterType(2));

        // set up Context
        PowerMockito.mockStatic(Context.class);
        Mockito.when(Context.getConceptService()).thenReturn(conceptService);
        Mockito.when(Context.getEncounterService()).thenReturn(encounterService);

        rule = new LostToFollowUpPatientSnapshot();

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
        currentObs = new Obs();
        currentObs.setConcept(conceptService.getConcept(concept));
        currentObs.setValueCoded(conceptService.getConcept(answer));
        currentObs.setObsDatetime(makeDate(date));
        currentObs.setEncounter(encounter);
       // currentObs.add(obs);
    }

    /**
     * adds an observation with the given date as the obs datetime
     *
     * @param concept
     * @param date
     */
   /* private void addObs(String concept, Boolean answer, String date) {
        Obs obs = new Obs();
        obs.setConcept(conceptService.getConcept(concept));
        patient.setDead(answer);
        obs.setPerson(patient);
        obs.setObsDatetime(makeDate(date));
        obs.setEncounter(encounter);
        currentObs.add(obs);
    }*/

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

    /**
     * @verifies find out if a particular Obs is consumed
     * @see LostToFollowUpPatientSnapshot#consume(org.openmrs.Obs)
     */

    @Test
    public void consume_shouldProperlyDetermineTOfromAnObservation() throws Exception {
        addEncounter(this.ENCOUNTER_TYPE_ADULT_INITIAL,"16 Oct 1975" );
        addObs(LostToFollowUpPatientSnapshot.CONCEPT_TRANSFER_CARE_TO_OTHER_CENTER, LostToFollowUpPatientSnapshot.CONCEPT_AMPATH, "16 Oct 1975") ;
        Assert.assertNotNull("A null encounter was encountered", encounter);
        Assert.assertNotNull("A null Obs was encountered", currentObs);
        System.out.println("This is what is happening "+currentObs.getEncounter().getEncounterType().toString());
        rule.consume(currentObs);
        rule.getProperty("reason");

    }

}
