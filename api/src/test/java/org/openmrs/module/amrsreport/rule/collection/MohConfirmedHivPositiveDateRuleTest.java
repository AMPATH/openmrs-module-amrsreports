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
import org.openmrs.module.amrsreport.cache.MohCacheUtils;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreport.service.MohCoreService;
import org.openmrs.module.amrsreport.util.MohFetchRestriction;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * test file for MohConfirmedHivPositiveDateRule class
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class MohConfirmedHivPositiveDateRuleTest {

    private static final List<String> initConcepts = Arrays.asList(
            MohEvaluableNameConstants.HIV_ENZYME_IMMUNOASSAY_QUALITATIVE,
            MohEvaluableNameConstants.HIV_RAPID_TEST_QUALITATIVE,
            MohEvaluableNameConstants.POSITIVE
            );

    private static final int PATIENT_ID = 5;
   // private Patient patient;
    private ConceptService conceptService;
    private PatientService patientService;
    private MohCoreService mohCoreService;
    private List<Obs> currentObs;
    private List<Encounter> currentEncounters;
    private MohConfirmedHivPositiveDateRule rule;


    @Before
    public void setup() {

        // initialize the current obs and Encounters
        currentObs = new ArrayList<Obs>();
        currentEncounters = new ArrayList<Encounter>();

       // patient = new Patient();

        //setup PatientService
       // patientService = Mockito.mock(PatientService.class);
        //Mockito.when(patientService.getPatient(PATIENT_ID)).thenReturn(patient);

        // build the concept service
        int i = 0;
        conceptService = Mockito.mock(ConceptService.class);

        for (String conceptName : initConcepts) {

            Mockito.when(conceptService.getConcept(conceptName)).thenReturn(new Concept(i++));
        }
        Mockito.when(conceptService.getConcept((String) null)).thenReturn(null);

        //set up MohCoreService
        mohCoreService = Mockito.mock(MohCoreService.class);

        //return current Observations

        Mockito.when(mohCoreService.getPatientObservations(Mockito.eq(PATIENT_ID),
                Mockito.anyMap(), Mockito.any(MohFetchRestriction.class))).thenReturn(currentObs);

        //return current encounters
        Mockito.when(mohCoreService.getPatientEncounters(Mockito.anyInt(),
                Mockito.anyMap(), Mockito.any(MohFetchRestriction.class))).thenReturn(currentEncounters);

        // set up Context
        PowerMockito.mockStatic(Context.class);
        Mockito.when(Context.getConceptService()).thenReturn(conceptService);
        Mockito.when(Context.getService(MohCoreService.class)).thenReturn(mohCoreService);
        Mockito.when(Context.getPatientService()).thenReturn(patientService);

        rule = new MohConfirmedHivPositiveDateRule();

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
        currentObs.add(obs);
    }

    private void addEncounter(String date){
        Encounter encounter = new Encounter();
        encounter.setDateCreated(makeDate(date));
        encounter.setEncounterDatetime(makeDate(date));

        currentEncounters.add(encounter);
    }


    /**
     * @verifies return the the first date a patient was confirmed HIV positive using HIV_ENZYME_IMMUNOASSAY_QUALITATIVE test
     * @see MohConfirmedHivPositiveDateRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
     */
    @Test
    public void evaluate_shouldReturnTheTheFirstDateAPatientWasConfirmedHIVPositiveUsingHIV_ENZYME_IMMUNOASSAY_QUALITATIVETest() throws Exception {
        addObs(MohEvaluableNameConstants.HIV_ENZYME_IMMUNOASSAY_QUALITATIVE, MohEvaluableNameConstants.POSITIVE, "16 Oct 2012");
        addObs(MohEvaluableNameConstants.HIV_RAPID_TEST_QUALITATIVE, MohEvaluableNameConstants.POSITIVE, "19 Oct 2012");
        Assert.assertEquals("The size of current Obs in HIV_ENZYME_IMMUNOASSAY_QUALITATIVE test is wrong!",2,currentObs.size());
        Assert.assertEquals("HIV_ENZYME_IMMUNOASSAY_QUALITATIVETest tested negative",new Result("16/10/2012"),rule.evaluate(null,PATIENT_ID,null));

    }

    /**
     * @verifies return the first date a patient was confirmed HIV Positive using HIV_RAPID_TEST_QUALITATIVE test
     * @see MohConfirmedHivPositiveDateRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
     */
    @Test
    public void evaluate_shouldReturnTheFirstDateAPatientWasConfirmedHIVPositiveUsingHIV_RAPID_TEST_QUALITATIVETest() throws Exception {
        addObs(MohEvaluableNameConstants.HIV_RAPID_TEST_QUALITATIVE, MohEvaluableNameConstants.POSITIVE, "16 Oct 2012");
        addObs(MohEvaluableNameConstants.HIV_ENZYME_IMMUNOASSAY_QUALITATIVE, MohEvaluableNameConstants.POSITIVE, "19 Oct 2012");
        Assert.assertEquals("The size of current Obs in HIV_RAPID_TEST_QUALITATIVE test is wrong!",2,currentObs.size());
        Assert.assertEquals("HIV_RAPID_TEST_QUALITATIVE tested negative",new Result("16/10/2012"),rule.evaluate(null,PATIENT_ID,null));

    }

    /**
     * @verifies return result for a patient who is HIV negative
     * @see MohConfirmedHivPositiveDateRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
     */
    @Test
    public void evaluate_shouldReturnResultForAPatientWhoIsHIVNegative() throws Exception {
        Assert.assertEquals("Test for HIV Negative patient has tested negative",new Result(),rule.evaluate(null,PATIENT_ID,null));

    }

    /**
     * @verifies return the date for the first encounter
     * @see MohConfirmedHivPositiveDateRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
     */
    @Test
    public void evaluate_shouldReturnTheDateForTheFirstEncounter() throws Exception {
       addEncounter("16 Oct 2012");
       addEncounter("17 Oct 2012");
       addEncounter("18 Oct 2012");

       Assert.assertEquals("Test for the size of encounters is wrong!",3,currentEncounters.size());

       Assert.assertEquals("Encounter test turned false",new Result("16/10/2012"),rule.evaluate(null,5,null));


    }
}
