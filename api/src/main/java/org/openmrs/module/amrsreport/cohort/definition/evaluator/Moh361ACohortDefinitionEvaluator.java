package org.openmrs.module.amrsreport.cohort.definition.evaluator;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreport.cache.MohCacheUtils;
import org.openmrs.module.amrsreport.cohort.definition.Moh361ACohortDefinition;
import org.openmrs.module.amrsreport.rule.MohEvaluableConstants;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PersonAttributeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.Collections;

/**
 * Evaluator for MOH 361A Cohort Definition
 */
@Handler(supports={Moh361ACohortDefinition.class})
public class Moh361ACohortDefinitionEvaluator implements CohortDefinitionEvaluator {


	@Override
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

		Moh361ACohortDefinition definition = (Moh361ACohortDefinition) cohortDefinition;
		if (definition == null)
			return null;

		// STEP 1: add Encounter Types 1,2,3,4,13
		EncounterCohortDefinition encounterCohortDefinition = new EncounterCohortDefinition();
		encounterCohortDefinition.addEncounterType(MohCacheUtils.getEncounterType(MohEvaluableNameConstants.ENCOUNTER_TYPE_ADULT_INITIAL));
		encounterCohortDefinition.addEncounterType(MohCacheUtils.getEncounterType(MohEvaluableNameConstants.ENCOUNTER_TYPE_ADULT_RETURN));
		encounterCohortDefinition.addEncounterType(MohCacheUtils.getEncounterType(MohEvaluableNameConstants.ENCOUNTER_TYPE_PEDIATRIC_INITIAL));
		encounterCohortDefinition.addEncounterType(MohCacheUtils.getEncounterType(MohEvaluableNameConstants.ENCOUNTER_TYPE_PEDIATRIC_RETURN));
		encounterCohortDefinition.addEncounterType(MohCacheUtils.getEncounterType(MohEvaluableNameConstants.ENCOUNTER_TYPE_BASELINE_INVESTIGATION));
		encounterCohortDefinition.setLocationList(definition.getLocationList());

		// STEP 2: find fake patients
		PersonAttributeCohortDefinition fakePatientCohortDefinition = new PersonAttributeCohortDefinition();
		fakePatientCohortDefinition.setAttributeType(Context.getPersonService().getPersonAttributeType(28));
		fakePatientCohortDefinition.setValues(Collections.singletonList("true"));

		// STEP 3: compose the query
		CompositionCohortDefinition combined = new CompositionCohortDefinition();
		combined.addSearch("eligible", encounterCohortDefinition, null);
		combined.addSearch("fake", fakePatientCohortDefinition, null);
		combined.setCompositionString("eligible AND NOT fake");

		Cohort results = Context.getService(CohortDefinitionService.class).evaluate(combined, context);
		return new EvaluatedCohort(results, cohortDefinition, context);
	}
}
