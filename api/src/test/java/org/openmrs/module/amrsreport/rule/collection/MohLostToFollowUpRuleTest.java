package org.openmrs.module.amrsreport.rule.collection;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreport.rule.util.MohTestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

/**
 * Test file for MohLostToFollowUpRule
 */
public class MohLostToFollowUpRuleTest extends BaseModuleContextSensitiveTest {

	public static final String CONCEPT_TRANSFER_CARE_TO_OTHER_CENTER = "TRANSFER CARE TO OTHER CENTER";
	public static final String CONCEPT_AMPATH = "AMPATH";


	/**
	 * @verifies get date and reason why a patient was lost
	 * @see MohLostToFollowUpRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldGetDateAndReasonWhyAPatientWasLost() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

		Patient patient = Context.getPatientService().getPatient(2);

		patient.setDead(true);

		patient.setDeathDate(new Date());

		Assert.assertNotNull(patient);
		Assert.assertTrue("The patient is not dead", patient.isDead());

		MohLostToFollowUpRule lostToFollowUpRule = new MohLostToFollowUpRule();
		String result = lostToFollowUpRule.evaluate(null, patient.getId(), null).toString();
		String expectedRes = "DEAD | " + sdf.format(new Date());

		Assert.assertTrue(result, result.equals(expectedRes));
	}

	@Test
	@Ignore
	public void evaluateUsingObs() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

		Patient patient2 = Context.getPatientService().getPatient(8);
		patient2.setDead(false);

		Assert.assertNotNull(patient2);
		Assert.assertTrue("The patient should be alive", !(patient2.isDead()));

		MohTestUtils.createQuestion(CONCEPT_TRANSFER_CARE_TO_OTHER_CENTER, new String[]{
				CONCEPT_AMPATH,
				"NON-AMPATH"
		});

		ConceptService conceptService = Context.getConceptService();

		EncounterService service = Context.getEncounterService();

		Encounter sampleEncounter = new Encounter();
		Date encounterDate = new Date();
		sampleEncounter.setEncounterDatetime(encounterDate);
		sampleEncounter.setPatient(patient2);
		sampleEncounter.setEncounterType(service.getEncounterType("ADULTINITIAL"));

		ObsService obsService = Context.getObsService();

		Obs obs = new Obs();
		obs.setConcept(conceptService.getConceptByName(CONCEPT_TRANSFER_CARE_TO_OTHER_CENTER));
		obs.setValueCoded(conceptService.getConceptByName(CONCEPT_AMPATH));
		obs.setObsDatetime(new Date());
		obs.setEncounter(sampleEncounter);

		//sampleEncounter.setObs(allObs);

		Encounter resEncounter = service.saveEncounter(sampleEncounter);

		Assert.assertNotNull("The encounter was not saved", resEncounter.getUuid() != null);

		Assert.assertNotNull("No Obs was saved", resEncounter.getObs().size() > 0);

		Assert.assertTrue("The patient is dead", patient2.getDeathDate() == null);

		MohLostToFollowUpRule testTransferService = new MohLostToFollowUpRule();

		Set<Obs> savedObs = resEncounter.getObs();

		String transferResult = testTransferService.evaluate(null, patient2.getId(), null).toString();

		String expectedTransferRes = "TO | (Ampath) " + sdf.format(obs.getObsDatetime());

		//Assert.assertTrue(transferResult, transferResult.equals(expectedTransferRes));
	}
}
