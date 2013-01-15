package org.openmrs.module.amrsreport.rule.collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreport.cache.MohCacheUtils;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreport.rule.util.MohTestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Unit tests for LostToFollowUpPatientSnapshot
 */
public class LostToFollowUpPatientSnapshotTest extends BaseModuleContextSensitiveTest {

	public static final String CONCEPT_TRANSFER_CARE_TO_OTHER_CENTER = "TRANSFER CARE TO OTHER CENTER";
	public static final String CONCEPT_AMPATH = "AMPATH";

	@Before
	public void setup() {
		EncounterType encounterType = new EncounterType();
		encounterType.setName(MohEvaluableNameConstants.ENCOUNTER_TYPE_DEATH_REPORT);
		encounterType.setDescription("foo");
		Context.getEncounterService().saveEncounterType(encounterType);
		Context.flushSession();
		Assert.assertNotNull(Context.getEncounterService().getEncounterType(MohEvaluableNameConstants.ENCOUNTER_TYPE_DEATH_REPORT));
	}

	/**
	 * @verifies find out if a particular Obs is consumed
	 * @see LostToFollowUpPatientSnapshot#consume(org.openmrs.Obs)
	 */
	@Test
	@Ignore
	public void consume_shouldFindOutIfAParticularObsIsConsumed() throws Exception {

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

		Patient patient2 = Context.getPatientService().getPatient(8);

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
		sampleEncounter.setEncounterType(MohCacheUtils.getEncounterType("ADULTINITIAL"));
		sampleEncounter.setLocation(new Location(1));
		sampleEncounter.setProvider(new Person(1));

		Obs obs = new Obs();
		obs.setConcept(conceptService.getConceptByName(CONCEPT_TRANSFER_CARE_TO_OTHER_CENTER));
		obs.setValueCoded(conceptService.getConceptByName(CONCEPT_AMPATH));
		obs.setObsDatetime(new Date());
		sampleEncounter.addObs(obs);

		Encounter resEncounter = service.saveEncounter(sampleEncounter);

		Assert.assertNotNull("The encounter was not saved", resEncounter.getUuid() != null);

		Assert.assertNotNull("No Obs was saved", resEncounter.getObs().size() > 0);

		Assert.assertTrue("The patient is dead", patient2.getDeathDate() == null);

		LostToFollowUpPatientSnapshot samplePatientSnapshot = new LostToFollowUpPatientSnapshot();
		samplePatientSnapshot.consume(obs);

		String transferResult = samplePatientSnapshot.getProperty("reason").toString();

		String expectedTransferRes = "TO | (Ampath) " + sdf.format(obs.getObsDatetime());

		Assert.assertTrue(transferResult, transferResult.equals(expectedTransferRes));
	}

	/**
	 * @verifies test if a given encounter is consumed
	 * @see LostToFollowUpPatientSnapshot#consume(org.openmrs.Encounter)
	 */
	@Test
	@Ignore
	public void consume_shouldTestIfAGivenEncounterIsConsumed() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

		Patient patient2 = Context.getPatientService().getPatient(8);

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
		sampleEncounter.setEncounterType(Context.getEncounterService().getEncounterType(MohEvaluableNameConstants.ENCOUNTER_TYPE_DEATH_REPORT));
		sampleEncounter.setLocation(new Location(1));
		sampleEncounter.setProvider(new Person(1));

		Obs obs = new Obs();
		obs.setConcept(conceptService.getConceptByName(CONCEPT_TRANSFER_CARE_TO_OTHER_CENTER));
		obs.setValueCoded(conceptService.getConceptByName(CONCEPT_AMPATH));
		obs.setObsDatetime(new Date());

		sampleEncounter.addObs(obs);

		Encounter resEncounter = service.saveEncounter(sampleEncounter);

		Assert.assertNotNull("The encounter was not saved", resEncounter.getUuid() != null);

		Assert.assertNotNull("No Obs was saved", resEncounter.getObs().size() > 0);

		Assert.assertTrue("The patient is dead", patient2.getDeathDate() == null);

		LostToFollowUpPatientSnapshot samplePatientSnapshot = new LostToFollowUpPatientSnapshot();
		samplePatientSnapshot.consume(sampleEncounter);

		Assert.assertNotNull(samplePatientSnapshot.getProperty("reason"));

		String transferResult = samplePatientSnapshot.getProperty("reason").toString();

		String expectedTransferRes = "DEAD | " + sdf.format(sampleEncounter.getEncounterDatetime());

		Assert.assertEquals(expectedTransferRes, transferResult);
	}
}
