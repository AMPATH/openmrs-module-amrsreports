package org.openmrs.module.amrsreport.rule.observation;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.*;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptName;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.User;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientSetService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.result.Result;
import org.openmrs.module.amrsreport.rule.observation.MohDateAndReasonMedicallyEligibleForARTRule;
import org.openmrs.module.amrsreport.rule.util.MohTestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.lang.Integer;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashSet;

import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;

/**
 * Created with IntelliJ IDEA.
 * User: oliver
 * Date: 11/12/12
 * Time: 11:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class MohDateAndReasonMedicallyEligibleForARTRuleTest extends BaseModuleContextSensitiveTest {

	ConceptService conceptService = Context.getConceptService();


	/**
	 * @verifies get the date and reason for ART eligibility
	 * @see MohDateAndReasonMedicallyEligibleForARTRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
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

		Date birthdate = new Date(1975, 01, 01);
		patient.setBirthdate(birthdate);

		EncounterService service = Context.getEncounterService();

		Encounter sampleEncounter = new Encounter();
		Date encounterDate = new Date();
		sampleEncounter.setEncounterDatetime(encounterDate);
		sampleEncounter.setPatient(patient);
		sampleEncounter.setEncounterType(service.getEncounterType("ADULTINITIAL"));

		/*ObsService obsService = Context.getObsService();*/

		Obs obs = new Obs();
		obs.setConcept(conceptService.getConceptByName(MohEvaluableNameConstants.WHO_STAGE_ADULT));
		obs.setValueCoded(conceptService.getConceptByName(MohEvaluableNameConstants.WHO_STAGE_1_ADULT));
		obs.setObsDatetime(new Date());
		obs.setEncounter(sampleEncounter);

		//sampleEncounter.setObs(allObs);

		Encounter resEncounter = service.saveEncounter(sampleEncounter);
		Integer patientID = resEncounter.getPatient().getPersonId();

		/*Checks if the Encounter has been saved*/
		Assert.assertNotNull("Encounter is Null", resEncounter);

		/*Checks to find id patient Id is not null*/
		Assert.assertNotNull("PatientID is Null", patientID);


		Assert.assertNotNull("Encounter is Null", resEncounter);


		MohDateAndReasonMedicallyEligibleForARTRule sampleRule = new MohDateAndReasonMedicallyEligibleForARTRule();

		Result evalResult = sampleRule.evaluate(null, patientID, null);

		Assert.assertNotNull("Evaluate Method returns Null", evalResult);

	}


}
