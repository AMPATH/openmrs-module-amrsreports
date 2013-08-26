package org.openmrs.module.amrsreports.snapshot;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Obs;
import org.openmrs.api.ConceptService;
import org.openmrs.module.amrsreports.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreports.MohTestUtils;

/**
 * Test class for PatientSnapshot
 */
public class ARVPatientSnapshotTest {

//	private static final ConceptService conceptService = Context.getConceptService();
	private static final ConceptService conceptService = null;

	/**
	 * @verifies recognize and set WHO stage from an obs or specify peds WHO
	 * @see org.openmrs.module.amrsreports.snapshot.PatientSnapshot#consume(org.openmrs.Obs)
	 */
	@Test
	@Ignore
	public void consume_shouldRecognizeAndSetWHOStageFromAnObsOrSpecifyPedsWHO() throws Exception {
		// create mock data for reference in this test
        MohTestUtils.createQuestion(MohEvaluableNameConstants.WHO_STAGE_ADULT, new String[]{
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
		Assert.assertEquals(2, arvPatientSnapshot.get("adultWHOStage"));

		/*Test results for peds*/
		arvPatientSnapshot.consume(obsPeds);
		Assert.assertEquals(2, arvPatientSnapshot.get("pedsWHOStage"));

		/*Test results for HIV_DNA_PCR*/
		arvPatientSnapshot.consume(obsHiv);
		Assert.assertTrue((Boolean) arvPatientSnapshot.get("HIVDNAPCRPositive"));
	}

	/**
	 * @verifies determine eligibility based on age group and flags
	 * @see org.openmrs.module.amrsreports.snapshot.PatientSnapshot#eligible()
	 */
	@Test
	@Ignore
	public void eligible_shouldDetermineEligibilityBasedOnAgeGroupAndFlags() throws Exception {

		ARVPatientSnapshot arvPatientSnapshot = new ARVPatientSnapshot();
		arvPatientSnapshot.setAgeGroup(MohEvaluableNameConstants.AgeGroup.EIGHTEEN_MONTHS_TO_FIVE_YEARS);
		arvPatientSnapshot.set("pedsWHOStage", 4);
		Assert.assertTrue(arvPatientSnapshot.eligible());

		String expectedReason = (String) arvPatientSnapshot.get("reason");
		Assert.assertTrue("They are not equal", expectedReason.equals("Clinical Only"));

		arvPatientSnapshot.set("reason", "Clinical Only");
		Assert.assertEquals("That is pedsWHOStage", 4, arvPatientSnapshot.get("pedsWHOStage"));
	}
}
