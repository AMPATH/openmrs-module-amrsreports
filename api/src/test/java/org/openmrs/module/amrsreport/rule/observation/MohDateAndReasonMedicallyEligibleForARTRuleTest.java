package org.openmrs.module.amrsreport.rule.observation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.result.Result;
import org.openmrs.module.amrsreport.rule.MohEvaluableConstants;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreport.rule.observation.MohDateAndReasonMedicallyEligibleForARTRule;
import org.openmrs.module.amrsreport.util.MohFetchRestriction;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Test file for MohDateAndReasonMedicallyEligibleForARTRule.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class MohDateAndReasonMedicallyEligibleForARTRuleTest{

    private static final List<String> initConcepts = Arrays.asList(
            MohDateAndReasonMedicallyEligibleForARTRule.REASON_CLINICAL,
            MohDateAndReasonMedicallyEligibleForARTRule.REASON_CLINICAL_CD4,
            MohDateAndReasonMedicallyEligibleForARTRule.REASON_CLINICAL_CD4_HIV_DNA_PCR,
            MohDateAndReasonMedicallyEligibleForARTRule.REASON_CLINICAL_HIV_DNA_PCR,
            MohEvaluableNameConstants.WHO_STAGE_1_ADULT,
            MohEvaluableNameConstants.WHO_STAGE_2_ADULT,
            MohEvaluableNameConstants.WHO_STAGE_3_ADULT,
            MohEvaluableNameConstants.WHO_STAGE_1_PEDS,
            MohEvaluableNameConstants.WHO_STAGE_3_PEDS,
            MohEvaluableNameConstants.WHO_STAGE_ADULT,
            MohEvaluableNameConstants.WHO_STAGE_PEDS,
            MohEvaluableNameConstants.HIV_DNA_PCR
    );

    private static final int PATIENT_ID = 5;
    private Patient patient;
    private ConceptService conceptService;
    private PatientService patientService;
    private ObsService obsService;
    private MohDateAndReasonMedicallyEligibleForARTRule rule;
    private List<Obs> currentObs;

    @Before
    public void setup() {

        // initialize the current obs
        currentObs = new ArrayList<Obs>();

        patient = new Patient();
        // build the concept service
        int i = 0;
        conceptService = Mockito.mock(ConceptService.class);
        patientService = Mockito.mock(PatientService.class);


        for (String conceptName : initConcepts) {
            Mockito.when(conceptService.getConcept(conceptName)).thenReturn(new Concept(i++));
        }
        Mockito.when(conceptService.getConcept((String) null)).thenReturn(null);
        Mockito.when(patientService.getPatient(PATIENT_ID)).thenReturn(patient);

        obsService = Mockito.mock(ObsService.class);
       /* List<Obs> obs = Context.getObsService().getObservations(
                Arrays.asList(new Person[]{patient}), null, getQuestionConcepts(),
                null, null, null, null, null, null, null, null, false);*/

        Mockito.when(obsService.getObservations(
                Arrays.asList(patient),
                Mockito.anyList(),
                Mockito.anyList(),
                Mockito.anyList(),
                Mockito.anyList(),
                Mockito.anyList(),
                Mockito.anyList(),
                Mockito.anyInt(),
                Mockito.anyInt(),
                Mockito.any(Date.class),
                Mockito.any(Date.class),
                Mockito.anyBoolean()
                )).thenReturn(currentObs);


        // set up Context
        PowerMockito.mockStatic(Context.class);
        Mockito.when(Context.getConceptService()).thenReturn(conceptService);
        Mockito.when(Context.getObsService()).thenReturn(obsService);
        Mockito.when(Context.getPatientService()).thenReturn(patientService);

        rule = new MohDateAndReasonMedicallyEligibleForARTRule();
    }


	/**
	 * @verifies get the date and reason for ART eligibility
	 * @see MohDateAndReasonMedicallyEligibleForARTRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldGetTheDateAndReasonForARTEligibility() throws Exception {

		/*MohTestUtils.createQuestion(MohEvaluableNameConstants.WHO_STAGE_ADULT, new String[]{
				MohEvaluableNameConstants.WHO_STAGE_1_ADULT,
				MohEvaluableNameConstants.WHO_STAGE_2_ADULT,
				MohEvaluableNameConstants.WHO_STAGE_3_ADULT,
				MohEvaluableNameConstants.WHO_STAGE_4_ADULT
		});
		MohTestUtils.createQuestion(MohEvaluableNameConstants.WHO_STAGE_PEDS, new String[]{
				MohEvaluableNameConstants.WHO_STAGE_1_PEDS,
				MohEvaluableNameConstants.WHO_STAGE_2_PEDS,
				MohEvaluableNameConstants.WHO_STAGE_3_PEDS,
				MohEvaluableNameConstants.WHO_STAGE_4_PEDS
		});
		MohTestUtils.createQuestion(MohEvaluableNameConstants.HIV_DNA_PCR, new String[]{
				MohEvaluableNameConstants.POSITIVE
		});
		Patient patient = new Patient();
		patient.setPersonId(2);

		PatientIdentifierType pit = new PatientIdentifierType();
		pit.setPatientIdentifierTypeId(1);

		PatientIdentifier pi = new PatientIdentifier("23452", pit, null);
		pi.setPatient(patient);

		Date birthdate = new Date(1975, 01, 01);
		patient.setBirthdate(birthdate);

		EncounterService service = Context.getEncounterService();

		Encounter sampleEncounter = new Encounter();
		Date encounterDate = new Date();
		sampleEncounter.setEncounterDatetime(encounterDate);
		sampleEncounter.setPatient(patient);
		sampleEncounter.setEncounterType(service.getEncounterType("ADULTINITIAL"));

		*//*ObsService obsService = Context.getObsService();*//*

		Obs obs = new Obs();
		obs.setConcept(conceptService.getConceptByName(MohEvaluableNameConstants.WHO_STAGE_ADULT));
		obs.setValueCoded(conceptService.getConceptByName(MohEvaluableNameConstants.WHO_STAGE_1_ADULT));
		obs.setObsDatetime(new Date());
		obs.setEncounter(sampleEncounter);

		//sampleEncounter.setObs(allObs);

		Encounter resEncounter = service.saveEncounter(sampleEncounter);
		Integer patientID = resEncounter.getPatient().getPersonId();

		*//*Checks if the Encounter has been saved*//*
		Assert.assertNotNull("Encounter is Null", resEncounter);

		*//*Checks to find id patient Id is not null*//*
		Assert.assertNotNull("PatientID is Null", patientID);


		Assert.assertNotNull("Encounter is Null", resEncounter);


		MohDateAndReasonMedicallyEligibleForARTRule sampleRule = new MohDateAndReasonMedicallyEligibleForARTRule();

		Result evalResult = sampleRule.evaluate(null, patientID, null);

		Assert.assertNotNull("Evaluate Method returns Null", evalResult);

	}
*/
    }
}
