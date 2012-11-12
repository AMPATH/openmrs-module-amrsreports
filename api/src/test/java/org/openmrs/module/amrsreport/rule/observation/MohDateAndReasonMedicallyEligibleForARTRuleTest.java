package org.openmrs.module.amrsreport.rule.observation;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientSetService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.module.amrsreport.rule.observation.MohDateAndReasonMedicallyEligibleForARTRule;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import java.util.Date;
import java.text.SimpleDateFormat;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptName;
import org.openmrs.Concept;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;

/**
 * Created with IntelliJ IDEA.
 * User: oliver
 * Date: 11/12/12
 * Time: 11:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class MohDateAndReasonMedicallyEligibleForARTRuleTest extends BaseModuleContextSensitiveTest {

    ConceptService conceptService;
    /**
     * @verifies get the date and reason for ART eligibility
     * @see MohDateAndReasonMedicallyEligibleForARTRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
     */
    @Test
    public void evaluate_shouldGetTheDateAndReasonForARTEligibility() throws Exception {

        this.createQuestion(MohEvaluableNameConstants.WHO_STAGE_ADULT, new String[]{
                MohEvaluableNameConstants.WHO_STAGE_1_ADULT,
                MohEvaluableNameConstants.WHO_STAGE_2_ADULT,
                MohEvaluableNameConstants.WHO_STAGE_3_ADULT,
                MohEvaluableNameConstants.WHO_STAGE_4_ADULT
        });
        this.createQuestion(MohEvaluableNameConstants.WHO_STAGE_PEDS, new String[]{
                MohEvaluableNameConstants.WHO_STAGE_1_PEDS,
                MohEvaluableNameConstants.WHO_STAGE_2_PEDS,
                MohEvaluableNameConstants.WHO_STAGE_3_PEDS,
                MohEvaluableNameConstants.WHO_STAGE_4_PEDS
        });
        this.createQuestion(MohEvaluableNameConstants.HIV_DNA_PCR, new String[]{
                MohEvaluableNameConstants.POSITIVE
        });

        Patient samplePatient = new Patient(501);

        Date birthdate =     new SimpleDateFormat("yyyy-MM-dd").parse("1975-01-01");

        samplePatient.setBirthdate(birthdate) ;

        EncounterService service = Context.getEncounterService();;
        EncounterType et = new EncounterType();
        et.setName("ADULTRETURN");

        Encounter sampleEncounter = new Encounter() ;
        Date encounterDate = new Date() ;
        sampleEncounter.setEncounterId(22);
        sampleEncounter.setEncounterDatetime(encounterDate);
        sampleEncounter.setPatient(samplePatient);
        sampleEncounter.setEncounterType(et);
        //ObsService os = Context.getObsService();
        Obs obs = new Obs();

        obs.setConcept(conceptService.getConceptByName("CURRENT WHO HIV STAGE"));
        obs.setValueCoded(conceptService.getConceptByName("WHO STAGE 2 ADULT"));

        obs.setEncounter(sampleEncounter);

        MohDateAndReasonMedicallyEligibleForARTRule sampleRule = new MohDateAndReasonMedicallyEligibleForARTRule();
        //assert "True";


    }

    /**
     * create a question concept with associated set of answers from strings
     *
     * @param question
     * @param answers
     */
    private void createQuestion(String question, String[] answers) {
        // create a new question concept
        Concept q = new Concept();
        q.addName(new ConceptName(question, Context.getLocale()));
        q.setConceptClass(conceptService.getConceptClassByName("Question"));

        // loop over answers and add them one by one to the question
        for (String answer: answers) {
            // create a new concept for the answer
            Concept a = new Concept();
            a.addName(new ConceptName(answer, Context.getLocale()));
            conceptService.saveConcept(a);
            // create a ConceptAnswer and add it to the question
            ConceptAnswer ca = new ConceptAnswer();
            ca.setAnswerConcept(a);
            q.addAnswer(ca);
        }

        // save the question
        conceptService.saveConcept(q);
    }
}
