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

		// 1AMPATH HIV positive patients 2yrs and above
		String eligibleAdultsByEncounter = "Select person_id from(" +
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
		SqlCohortDefinition eligibleAdults = new SqlCohortDefinition(eligibleAdultsByEncounter);
		eligibleAdults.addParameter(new Parameter("endDate", "End Date", Date.class));
		eligibleAdults.addParameter(new Parameter("location", "Location", Location.class));

		// 1AMPATH HIV exposed Infants(<2yrs) at first visit
		String eligibleChildrenByEncounter = "Select person_id from(" +
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
				"    al.age < 2 " +
				"    and al.encounter_datetime<=:endDate";
		SqlCohortDefinition eligibleChild = new SqlCohortDefinition(eligibleChildrenByEncounter);
		eligibleChild.addParameter(new Parameter("endDate", "End Date", Date.class));
		eligibleChild.addParameter(new Parameter("location", "Location", Location.class));

		// 1AMPATH patients with positive non conflicting results
		String positiveNonConflictingResults = "select ob.person_id " +
				"from (" +
				"	(select person_id, min(obs_datetime) as pos_date, location_id" +
				"		from obs o" +
				"		where" +
				"			(o.concept_id in (1040, 1030, 1042) and o.value_coded = 703)" +
				"			and o.obs_datetime <= :endDate" +
				"		group by person_id) ob" +
				"	left join (" +
				"		select person_id, max(obs_datetime) as neg_date" +
				"		from obs o" +
				"		where (" +
				"			o.concept_id in (1040, 1030, 1042)" +
				"			and o.value_coded = 664" +
				"			and o.obs_datetime <= :endDate)" +
				"		group by person_id) as ab" +
				"	on ob.person_id = ab.person_id) " +
				"where" +
				"	(neg_date is null or pos_date > neg_date)" +
				"	and ob.location_id = :location";
		SqlCohortDefinition positiveNonConflicting = new SqlCohortDefinition(positiveNonConflictingResults);
		positiveNonConflicting.addParameter(new Parameter("endDate", "End Date", Date.class));
		positiveNonConflicting.addParameter(new Parameter("location", "Location", Location.class));

		// 1AMPATH patients with Problem added=HIV Infected
		String positiveByProblemAdded = "SELECT why.person_id" +
				" FROM (" +
				"	SELECT o.person_id, o.location_id, o.obs_datetime" +
				"	FROM obs o" +
				"	LEFT JOIN encounter e ON e.encounter_id=o.encounter_id" +
				"	INNER JOIN person p ON p.person_id=o.person_id" +
				"	WHERE" +
				"		o.concept_id=6042" +
				"		AND o.value_coded=1169" +
				"		AND o.voided=0" +
				"		AND e.voided=0" +
				"		AND p.voided=0)" +
				"	AS why" +
				" WHERE" +
				"	why.location_id = :location" +
				"	and why.obs_datetime <= :endDate";
		SqlCohortDefinition positiveProblemAdded = new SqlCohortDefinition(positiveByProblemAdded);
		positiveProblemAdded.addParameter(new Parameter("endDate", "End Date", Date.class));
		positiveProblemAdded.addParameter(new Parameter("location", "Location", Location.class));

		// exclude fake patients
		PersonAttributeCohortDefinition fakePatientCohortDefinition = new PersonAttributeCohortDefinition();
		fakePatientCohortDefinition.setAttributeType(Context.getPersonService().getPersonAttributeType(28));
		fakePatientCohortDefinition.setValues(Collections.singletonList("true"));

		// build mapped cohorts for use in composition
		Mapped<CohortDefinition> mappedEligibleAdult = new Mapped<CohortDefinition>();
		mappedEligibleAdult.setParameterizable(eligibleAdults);
		mappedEligibleAdult.addParameterMapping("endDate", "${endDate}");
		mappedEligibleAdult.addParameterMapping("location", "${location}");

		Mapped<CohortDefinition> mappedEligibleChild = new Mapped<CohortDefinition>();
		mappedEligibleChild.setParameterizable(eligibleChild);
		mappedEligibleChild.addParameterMapping("endDate", "${endDate}");
		mappedEligibleChild.addParameterMapping("location", "${location}");

		Mapped<CohortDefinition> mappedPositiveNonConflicting = new Mapped<CohortDefinition>();
		mappedPositiveNonConflicting.setParameterizable(positiveNonConflicting);
		mappedPositiveNonConflicting.addParameterMapping("endDate", "${endDate}");
		mappedPositiveNonConflicting.addParameterMapping("location", "${location}");

		Mapped<CohortDefinition> mappedPositiveProblemAdded = new Mapped<CohortDefinition>();
		mappedPositiveProblemAdded.setParameterizable(positiveProblemAdded);
		mappedPositiveProblemAdded.addParameterMapping("endDate", "${endDate}");
		mappedPositiveProblemAdded.addParameterMapping("location", "${location}");

		// compose the query
		CompositionCohortDefinition combined = new CompositionCohortDefinition();
		combined.addSearch("eligibleAdult", mappedEligibleAdult);
		combined.addSearch("eligibleChild", mappedEligibleChild);
		combined.addSearch("positiveNonConflicting", mappedPositiveNonConflicting);
		combined.addSearch("positiveProblemAdded", mappedPositiveProblemAdded);
		combined.addSearch("fake", fakePatientCohortDefinition, null);
		combined.setCompositionString("(eligibleAdult OR (eligibleChild AND (positiveNonConflicting OR positiveProblemAdded))) AND NOT fake");

		// link parameters required in sub cohorts
		combined.addParameter(new Parameter("endDate", "End Date", Date.class));
		combined.addParameter(new Parameter("location", "Location", Location.class));

		Cohort results = Context.getService(CohortDefinitionService.class).evaluate(combined, context);
		return new EvaluatedCohort(results, cohortDefinition, context);
	}
}
