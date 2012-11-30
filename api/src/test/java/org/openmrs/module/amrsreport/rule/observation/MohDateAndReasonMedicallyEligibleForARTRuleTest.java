package org.openmrs.module.amrsreport.rule.observation;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.result.Result;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreport.rule.util.MohTestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class MohDateAndReasonMedicallyEligibleForARTRuleTest extends BaseModuleContextSensitiveTest {

	@Autowired
	ConceptService conceptService;

	/**
	 * @verifies get the date and reason for ART eligibility
	 * @see MohDateAndReasonMedicallyEligibleForARTRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	@Ignore
	public void evaluate_shouldGetTheDateAndReasonForARTEligibility() throws Exception {

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
		Patient patient = new Patient();
		patient.setPersonId(2);

		PatientIdentifierType pit = new PatientIdentifierType();
		pit.setPatientIdentifierTypeId(1);

		PatientIdentifier pi = new PatientIdentifier("23452", pit, null);
		pi.setPatient(patient);
		Date birthdate = new Date(1975, 1, 1);
		patient.setBirthdate(birthdate);

		EncounterService service = Context.getEncounterService();

		Encounter sampleEncounter = new Encounter();
		Date encounterDate = new Date();
		sampleEncounter.setEncounterDatetime(encounterDate);
		sampleEncounter.setPatient(patient);
		sampleEncounter.setEncounterType(service.getEncounterType("ADULTINITIAL"));

		Obs obs = new Obs();
		obs.setConcept(conceptService.getConceptByName(MohEvaluableNameConstants.WHO_STAGE_ADULT));
		obs.setValueCoded(conceptService.getConceptByName(MohEvaluableNameConstants.WHO_STAGE_1_ADULT));
		obs.setObsDatetime(new Date());
		sampleEncounter.addObs(obs);

		Encounter resEncounter = service.saveEncounter(sampleEncounter);
		Integer patientID = resEncounter.getPatient().getPersonId();

		/*Checks if the Encounter has been saved*/
		Assert.assertNotNull("Encounter is Null", resEncounter);
		/*Checks to find if patient Id is not null*/
		Assert.assertNotNull("PatientID is Null", patientID);

		Assert.assertNotNull("Encounter is Null", resEncounter);

		MohDateAndReasonMedicallyEligibleForARTRule sampleRule = new MohDateAndReasonMedicallyEligibleForARTRule();

		Result evalResult = sampleRule.evaluate(null, patientID, null);

		Assert.assertNotNull("Evaluate Method returns Null", evalResult);
	}
}
