package org.openmrs.module.amrsreport.cohort.definition.evaluator;

import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreport.cohort.definition.Moh361ACohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PersonAttributeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

import java.util.Collections;
import java.util.Date;

/**
 * Evaluator for MOH 361A Cohort Definition
 */
@Handler(supports = {Moh361ACohortDefinition.class})
public class Moh361ACohortDefinitionEvaluator implements CohortDefinitionEvaluator {


	@Override
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

		Moh361ACohortDefinition definition = (Moh361ACohortDefinition) cohortDefinition;
		if (definition == null)
			return null;

//		EncounterCohortDefinition encounterCohortDefinition = new EncounterCohortDefinition();
//		encounterCohortDefinition.addEncounterType(MohCacheUtils.getEncounterType(MohEvaluableNameConstants.ENCOUNTER_TYPE_ADULT_INITIAL));
//		encounterCohortDefinition.addEncounterType(MohCacheUtils.getEncounterType(MohEvaluableNameConstants.ENCOUNTER_TYPE_ADULT_RETURN));
//		encounterCohortDefinition.addEncounterType(MohCacheUtils.getEncounterType(MohEvaluableNameConstants.ENCOUNTER_TYPE_PEDIATRIC_INITIAL));
//		encounterCohortDefinition.addEncounterType(MohCacheUtils.getEncounterType(MohEvaluableNameConstants.ENCOUNTER_TYPE_PEDIATRIC_RETURN));
//		encounterCohortDefinition.addEncounterType(MohCacheUtils.getEncounterType(MohEvaluableNameConstants.ENCOUNTER_TYPE_BASELINE_INVESTIGATION));
//		encounterCohortDefinition.setLocationList(definition.getLocationList());

		// STEP 1: find all patients >= 2yo at first HIV encounter
		String encounterQuery = "Select person_id from(" +
				"    (Select " +
				"        p.person_id, " +
				"        fir.encounter_datetime," +
				"        p.birthdate, " +
				"        floor(datediff(fir.encounter_datetime,p.birthdate) /365.25) as age " +
				"    from" +
				"        (Select " +
				"            patient_id, " +
				"            encounter_datetime, " +
				"            location_id," +
				"            encounter_type " +
				"        from encounter" +
				"        where " +
				"            voided=0 " +
				"            and encounter_type in(1,2,3,4,13)" +
				"            group by patient_id" +
				"            order by patient_id, encounter_datetime ) fir" +
				"        join person p" +
				"            on p.person_id=fir.patient_id" +
				"    where " +
				"        location_id = :location " +
				"        and p.voided=0)" +
				"     as al)" +
				"where " +
				"    al.age>=2 " +
				"    and al.encounter_datetime<=:endDate";
		SqlCohortDefinition eligibleByEncounterAndAge = new SqlCohortDefinition(encounterQuery);
		eligibleByEncounterAndAge.addParameter(new Parameter("endDate", "End Date", Date.class));
		eligibleByEncounterAndAge.addParameter(new Parameter("location", "Location", Location.class));

		// STEP 2: find all patients with HIV+ observations, regardless of age
		String positiveQuery = "Select person_id from(" +
				"    select o.person_id ,o.obs_datetime" +
				"        from obs o" +
				"        where (" +
				"            o.concept_id in(1040,1030,1042) " +
				"            and o.value_coded=703) " +
				"            and o.obs_datetime<=:endDate " +
				"            and location_id=:location" +
				"        group by person_id " +
				"        order by person_id, obs_datetime) pos" +
				"    join encounter e" +
				"        on e.patient_id = pos.person_id" +
				"    where " +
				"        e.encounter_type in(1,2,3,4,13) " +
				"        and e.voided=0" +
				"    group by person_id " +
				"    order by person_id, obs_datetime";
		SqlCohortDefinition positiveByObservation = new SqlCohortDefinition(positiveQuery);
		positiveByObservation.addParameter(new Parameter("endDate", "End Date", Date.class));
		positiveByObservation.addParameter(new Parameter("location", "Location", Location.class));

		// STEP 3: exclude fake patients
		PersonAttributeCohortDefinition fakePatientCohortDefinition = new PersonAttributeCohortDefinition();
		fakePatientCohortDefinition.setAttributeType(Context.getPersonService().getPersonAttributeType(28));
		fakePatientCohortDefinition.setValues(Collections.singletonList("true"));

		// STEP 4: build mapped cohorts for use in composition
		Mapped<CohortDefinition> mappedEligible = new Mapped<CohortDefinition>();
		mappedEligible.setParameterizable(eligibleByEncounterAndAge);
		mappedEligible.addParameterMapping("endDate", "${endDate}");
		mappedEligible.addParameterMapping("location", "${location}");

		Mapped<CohortDefinition> mappedPositive = new Mapped<CohortDefinition>();
		mappedPositive.setParameterizable(positiveByObservation);
		mappedPositive.addParameterMapping("endDate", "${endDate}");
		mappedPositive.addParameterMapping("location", "${location}");

		// STEP 4: compose the query
		CompositionCohortDefinition combined = new CompositionCohortDefinition();
		combined.addSearch("eligible", mappedEligible);
		combined.addSearch("positive", mappedPositive);
		combined.addSearch("fake", fakePatientCohortDefinition, null);
		combined.setCompositionString("(eligible OR positive) AND NOT fake");

		// STEP 5: link parameters required in sub cohorts
		combined.addParameter(new Parameter("endDate", "End Date", Date.class));
		combined.addParameter(new Parameter("location", "Location", Location.class));

		Cohort results = Context.getService(CohortDefinitionService.class).evaluate(combined, context);
		return new EvaluatedCohort(results, cohortDefinition, context);
	}
}
