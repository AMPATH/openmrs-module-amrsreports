package org.openmrs.module.amrsreports.reporting.cohort.definition.evaluator;

import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.reporting.cohort.definition.Moh361ACohortDefinition;
import org.openmrs.module.amrsreports.reporting.cohort.definition.NASCOP771CohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Evaluator for NASCOP 771 Cohort Definition
 */
@Handler(supports = {NASCOP771CohortDefinition.class})
public class NASCOP771CohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

		Moh361ACohortDefinition definition = (Moh361ACohortDefinition) cohortDefinition;

		if (definition == null)
			return null;

		if (definition.getFacility() == null)
			return null;

		String reportDate = sdf.format(context.getEvaluationDate());
		List<Location> locationList = new ArrayList<Location>();
		locationList.addAll(definition.getFacility().getLocations());
		context.addParameterValue("locationList", locationList);

		String sql =
				"select person_id" +
						" from amrsreport_hiv_care_enrollment " +
						" where " +
						"  enrollment_date is not NULL" +
						"  and enrollment_date <= ':reportDate'" +
						"  and transferred_in_date is NULL" +
						"  and (" +
						"    last_discontinue_date is NULL" +
						"    or last_hiv_encounter_date > last_discontinue_date" +
						"  )" +
						"  and enrollment_location_id in (:locationList)";

		SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition(sql.replaceAll(":reportDate", reportDate));

		Cohort results = Context.getService(CohortDefinitionService.class).evaluate(sqlCohortDefinition, context);
		return new EvaluatedCohort(results, sqlCohortDefinition, context);
	}
}
