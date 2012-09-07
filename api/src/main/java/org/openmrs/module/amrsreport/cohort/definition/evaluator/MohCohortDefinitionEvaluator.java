/**
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.amrsreport.cohort.definition.evaluator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.OpenmrsObject;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientSetService;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreport.rule.MohEvaluableConstants;
import org.openmrs.module.amrsreport.cohort.definition.MohCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PersonAttributeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

@Handler(supports = {MohCohortDefinition.class})
public class MohCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	private static final Log log = LogFactory.getLog(MohCohortDefinitionEvaluator.class);
	public static final String ENCOUNTER_TYPE_ADULT_RETURN = "ADULTRETURN";
	public static final String ENCOUNTER_TYPE_ADULT_INITIAL = "ADULTINITIAL";
	public static final String FIRST_HIV_RAPID_TEST_QUALITATIVE_CONCEPT = "HIV RAPID TEST, QUALITATIVE";
	public static final String SECOND_HIV_RAPID_TEST_QUALITATIVE_CONCEPT = "HIV RAPID TEST 2, QUALITATIVE";
	public static final String POSITIVE_CONCEPT = "POSITIVE";
	public static final String HIV_ENZYME_IMMUNOASSAY_QUALITATIVE_CONCEPT = "HIV ENZYME IMMUNOASSAY, QUALITATIVE";

	public EvaluatedCohort evaluate(final CohortDefinition cohortDefinition, final EvaluationContext evaluationContext) throws EvaluationException {

		MohCohortDefinition mohCohortDefinition = (MohCohortDefinition) cohortDefinition;

		EncounterService service = Context.getEncounterService();
		ConceptService conceptService = Context.getConceptService();
		CohortDefinitionService definitionService = Context.getService(CohortDefinitionService.class);

		// limit to people with adult initial or return encounters
		EncounterCohortDefinition encounterCohortDefinition = new EncounterCohortDefinition();
		encounterCohortDefinition.addEncounterType(service.getEncounterType(ENCOUNTER_TYPE_ADULT_INITIAL));
		encounterCohortDefinition.addEncounterType(service.getEncounterType(ENCOUNTER_TYPE_ADULT_RETURN));
		encounterCohortDefinition.setLocationList(mohCohortDefinition.getLocationList());
		
		// find people who had adult encounters at this location
		Cohort encounterCohort = definitionService.evaluate(encounterCohortDefinition, evaluationContext);

		// TODO set these with GPs and a settings page
		Concept firstRapidConcept = conceptService.getConcept(FIRST_HIV_RAPID_TEST_QUALITATIVE_CONCEPT);
		Concept secondRapidConcept = conceptService.getConcept(SECOND_HIV_RAPID_TEST_QUALITATIVE_CONCEPT);
		Concept positiveConcept = conceptService.getConcept(POSITIVE_CONCEPT);

		// define search for all people who have rapid test positive results
		CodedObsCohortDefinition firstRapidCohortDefinition = new CodedObsCohortDefinition();
		firstRapidCohortDefinition.setTimeModifier(PatientSetService.TimeModifier.ANY);
		firstRapidCohortDefinition.setLocationList(mohCohortDefinition.getLocationList());
		firstRapidCohortDefinition.setQuestion(firstRapidConcept);
		firstRapidCohortDefinition.setOperator(SetComparator.IN);
		firstRapidCohortDefinition.setValueList(Arrays.asList(positiveConcept));

		// define search all people who have rapid test 2 positive results
		CodedObsCohortDefinition secondRapidCohortDefinition = new CodedObsCohortDefinition();
		secondRapidCohortDefinition.setTimeModifier(PatientSetService.TimeModifier.ANY);
		secondRapidCohortDefinition.setLocationList(mohCohortDefinition.getLocationList());
		secondRapidCohortDefinition.setQuestion(secondRapidConcept);
		secondRapidCohortDefinition.setOperator(SetComparator.IN);
		secondRapidCohortDefinition.setValueList(Arrays.asList(positiveConcept));

		// combine rapid test definitions
		CompositionCohortDefinition rapidCompositionCohortDefinition = new CompositionCohortDefinition();
		rapidCompositionCohortDefinition.addSearch("PositiveFirstRapid", firstRapidCohortDefinition, null);
		rapidCompositionCohortDefinition.addSearch("PositiveSecondRapid", secondRapidCohortDefinition, null);
		rapidCompositionCohortDefinition.setCompositionString("PositiveFirstRapid OR PositiveSecondRapid");
		Cohort rapidCompositionCohort = definitionService.evaluate(rapidCompositionCohortDefinition, evaluationContext);

		// set age limits
		AgeCohortDefinition ageCohortDefinition = new AgeCohortDefinition();
		ageCohortDefinition.setMinAge(18);
		ageCohortDefinition.setMinAgeUnit(DurationUnit.MONTHS);
		ageCohortDefinition.setMaxAge(14);
		ageCohortDefinition.setMaxAgeUnit(DurationUnit.YEARS);

		// TODO set this concept with a GP and settings page
		Concept elisaConcept = conceptService.getConcept(HIV_ENZYME_IMMUNOASSAY_QUALITATIVE_CONCEPT);

		// define search for all people with a positive elisa evaluation
		CodedObsCohortDefinition elisaCohortDefinition = new CodedObsCohortDefinition();
		elisaCohortDefinition.setTimeModifier(PatientSetService.TimeModifier.ANY);
		elisaCohortDefinition.setLocationList(mohCohortDefinition.getLocationList());
		elisaCohortDefinition.setQuestion(elisaConcept);
		elisaCohortDefinition.setOperator(SetComparator.IN);
		elisaCohortDefinition.setValueList(Arrays.asList(positiveConcept));

		// find patients within age limits who had elisa positive results
		CompositionCohortDefinition elisaCompositionCohortDefinition = new CompositionCohortDefinition();
		elisaCompositionCohortDefinition.addSearch("PaediatricAge", ageCohortDefinition, null);
		elisaCompositionCohortDefinition.addSearch("PositiveElisa", elisaCohortDefinition, null);
		elisaCompositionCohortDefinition.setCompositionString("PaediatricAge AND PositiveElisa");
		Cohort elisaCompositionCohort = definitionService.evaluate(elisaCompositionCohortDefinition, evaluationContext);

		// Check for the elisa to make sure the elisa happened after 18 months
		PersonAttributeCohortDefinition personAttributeCohortDefinition = new PersonAttributeCohortDefinition();
		personAttributeCohortDefinition.setAttributeType(Context.getPersonService().getPersonAttributeTypeByName("Health Center"));
		personAttributeCohortDefinition.setValueLocations(mohCohortDefinition.getLocationList());

		// TODO use GPs and a settings page to configure these concepts
		Concept transferConcept = conceptService.getConcept("TRANSFER CARE TO OTHER CENTER");
		Concept withinConcept = conceptService.getConcept("AMPATH");
		Concept missedVisitConcept = conceptService.getConcept("REASON FOR MISSED VISIT");
		Concept transferVisitConcept = conceptService.getConcept("AMPATH CLINIC TRANSFER");

		// define transfer specifications
		CodedObsCohortDefinition transferCohortDefinition = new CodedObsCohortDefinition();
		transferCohortDefinition.setTimeModifier(PatientSetService.TimeModifier.ANY);
		transferCohortDefinition.setLocationList(mohCohortDefinition.getLocationList());
		transferCohortDefinition.setQuestion(transferConcept);
		transferCohortDefinition.setOperator(SetComparator.IN);
		transferCohortDefinition.setValueList(Arrays.asList(withinConcept));

		// find all people with proper health center location and a transfer
		CompositionCohortDefinition transferCompositionCohortDefinition = new CompositionCohortDefinition();
		transferCompositionCohortDefinition.addSearch("HealthCenterAttribute", personAttributeCohortDefinition, null);
		transferCompositionCohortDefinition.addSearch("TransferWithinAmpath", transferCohortDefinition, null);
		transferCompositionCohortDefinition.setCompositionString("HealthCenterAttribute AND TransferWithinAmpath");
		Cohort transferCompositionCohort = definitionService.evaluate(transferCompositionCohortDefinition, evaluationContext);
		
		// define missed visits at this location
		CodedObsCohortDefinition missedVisitCohortDefinition = new CodedObsCohortDefinition();
		missedVisitCohortDefinition.setTimeModifier(PatientSetService.TimeModifier.ANY);
		missedVisitCohortDefinition.setLocationList(mohCohortDefinition.getLocationList());
		missedVisitCohortDefinition.setQuestion(missedVisitConcept);
		missedVisitCohortDefinition.setOperator(SetComparator.IN);
		missedVisitCohortDefinition.setValueList(Arrays.asList(transferVisitConcept));

		// find all people with defined health center and a missed visit
		CompositionCohortDefinition missedVisitCompositionCohortDefinition = new CompositionCohortDefinition();
		missedVisitCompositionCohortDefinition.addSearch("HealthCenterAttribute", personAttributeCohortDefinition, null);
		missedVisitCompositionCohortDefinition.addSearch("MissedVisitTransfer", missedVisitCohortDefinition, null);
		missedVisitCompositionCohortDefinition.setCompositionString("HealthCenterAttribute AND MissedVisitTransfer");
		Cohort missedVisitCompositionCohort = definitionService.evaluate(missedVisitCompositionCohortDefinition, evaluationContext);

		// build the patientIds by combining all found patients
		Set<Integer> patientIds = new HashSet<Integer>();
		patientIds.addAll(encounterCohort.getMemberIds());
		patientIds.addAll(rapidCompositionCohort.getMemberIds());
		patientIds.addAll(elisaCompositionCohort.getMemberIds());
		patientIds.addAll(transferCompositionCohort.getMemberIds());
		patientIds.addAll(missedVisitCompositionCohort.getMemberIds());

		// find fake patients
		PersonAttributeCohortDefinition fakePatientCohortDefinition = new PersonAttributeCohortDefinition();
		fakePatientCohortDefinition.setAttributeType(Context.getPersonService().getPersonAttributeType(28));
		fakePatientCohortDefinition.setValues(Collections.singletonList("true"));
		Cohort fakePatientCohort = definitionService.evaluate(fakePatientCohortDefinition, evaluationContext);

		// remove fake patients from the list                
		patientIds.removeAll(fakePatientCohort.getMemberIds());

		// build the cohort from the resulting list of patient ids
		return new EvaluatedCohort(new Cohort(patientIds), cohortDefinition, evaluationContext);
	}
}