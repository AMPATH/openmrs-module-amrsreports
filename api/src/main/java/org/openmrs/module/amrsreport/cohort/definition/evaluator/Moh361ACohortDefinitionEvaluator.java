package org.openmrs.module.amrsreport.cohort.definition.evaluator;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreport.cohort.definition.Moh361ACohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.text.SimpleDateFormat;

/**
 * Evaluator for MOH 361A Cohort Definition
 */
@Handler(supports = {Moh361ACohortDefinition.class})
public class Moh361ACohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

		Moh361ACohortDefinition definition = (Moh361ACohortDefinition) cohortDefinition;

		if (definition == null)
			return null;

		String reportDate = sdf.format(context.getEvaluationDate());

		String sql =
				"select person_id" +
						" from amrsreport_hiv_care_enrollment " +
						" where " +
						"  enrollment_reason is not NULL" +
						"  and transferred_in_date is NULL" +
						"  and (" +
						"    last_discontinue_date is NULL" +
						"    or last_hiv_encounter_date > last_discontinue_date" +
						"  )" +
						"  and report_date = ':reportDate'" +
						"  and enrollment_location_id in (:locationList)";

		SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition(sql.replaceAll(":reportDate", reportDate));

		Cohort results = Context.getService(CohortDefinitionService.class).evaluate(sqlCohortDefinition, context);
		return new EvaluatedCohort(results, sqlCohortDefinition, context);
	}
}
