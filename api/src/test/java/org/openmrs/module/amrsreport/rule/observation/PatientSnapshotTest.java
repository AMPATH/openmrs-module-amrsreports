package org.openmrs.module.amrsreport.rule.observation;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptName;
import org.openmrs.Obs;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test class for PatientSnapshot
 */
public class PatientSnapshotTest extends BaseModuleContextSensitiveTest {

	@Autowired
	ConceptService conceptService;

	/**
	 * @verifies recognize and set WHO stage from an obs or specify peds WHO
	 * @see PatientSnapshot#consume(org.openmrs.Obs)
	 */
	@Test
	public void consume_shouldRecognizeAndSetWHOStageFromAnObsOrSpecifyPedsWHO() throws Exception {
		// create mock data for reference in this test
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

		/*Set concepts for adults*/
		Obs obs = new Obs();
		obs.setConcept(conceptService.getConceptByName(MohEvaluableNameConstants.WHO_STAGE_ADULT));
		obs.setValueCoded(conceptService.getConceptByName(MohEvaluableNameConstants.WHO_STAGE_2_ADULT));

		/*Set concepts for peds*/
		Obs obsPeds = new Obs();
		obsPeds.setConcept(conceptService.getConceptByName(MohEvaluableNameConstants.WHO_STAGE_PEDS));
		obsPeds.setValueCoded(conceptService.getConceptByName(MohEvaluableNameConstants.WHO_STAGE_2_PEDS));

		/*Set concepts for hiv dna pcr*/
		Obs obsHiv = new Obs();
		obsHiv.setConcept(conceptService.getConceptByName(MohEvaluableNameConstants.HIV_DNA_PCR));
		obsHiv.setValueCoded(conceptService.getConceptByName(MohEvaluableNameConstants.POSITIVE));

		ARVPatientSnapshot arvPatientSnapshot = new ARVPatientSnapshot();

		/*Test result for adults*/
		arvPatientSnapshot.consume(obs);
		Assert.assertEquals(2, arvPatientSnapshot.getProperty("adultWHOStage"));

		/*Test results for peds*/
		arvPatientSnapshot.consume(obsPeds);
		Assert.assertEquals(2, arvPatientSnapshot.getProperty("pedsWHOStage"));

		/*Test results for HIV_DNA_PCR*/
		arvPatientSnapshot.consume(obsHiv);
		Assert.assertTrue((Boolean) arvPatientSnapshot.getProperty("HIVDNAPCRPositive"));
	}

	/**
	 * @verifies determine eligibility based on age group and flags
	 * @see PatientSnapshot#eligible()
	 */
	@Test
	public void eligible_shouldDetermineEligibilityBasedOnAgeGroupAndFlags() throws Exception {

		ARVPatientSnapshot arvPatientSnapshot = new ARVPatientSnapshot();
		arvPatientSnapshot.setAgeGroup(MohEvaluableNameConstants.AgeGroup.EIGHTEEN_MONTHS_TO_FIVE_YEARS);
		arvPatientSnapshot.setProperty("pedsWHOStage", 4);
		Assert.assertTrue(arvPatientSnapshot.eligible());

		String expectedReason = (String) arvPatientSnapshot.getProperty("reason");
		Assert.assertTrue("They are not equal", expectedReason.equals("Clinical Only"));

		arvPatientSnapshot.setProperty("reason", "Clinical Only");
		Assert.assertEquals("That is pedsWHOStage", 4, arvPatientSnapshot.getProperty("pedsWHOStage"));
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
