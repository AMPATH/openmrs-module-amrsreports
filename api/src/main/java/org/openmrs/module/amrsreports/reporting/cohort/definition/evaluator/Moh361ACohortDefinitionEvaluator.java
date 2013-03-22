package org.openmrs.module.amrsreports.reporting.cohort.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.reporting.cohort.definition.Moh361ACohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Evaluator for MOH 361A Cohort Definition
 */
@Handler(supports = {Moh361ACohortDefinition.class})
public class Moh361ACohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private final Log log = LogFactory.getLog(this.getClass());

	@Override
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

		Moh361ACohortDefinition definition = (Moh361ACohortDefinition) cohortDefinition;

		if (definition == null)
			return null;

		String reportDate = sdf.format(context.getEvaluationDate());

		String sql =
				"select person_id" +
						" from amrsreports_hiv_care_enrollment " +
						" where " +
						"  enrollment_date is not NULL" +
						"  and enrollment_date <= ':reportDate'" +
						"  and enrollment_location_id in ( :locationList )";

        List<Location> locationList = (List<Location>) context.getParameterValue("locationList");
        for (Location location : locationList) {
            String personAttributeQuery =
                    " union " +
                            " select person_attribute.person_id" +
                            " from person_attribute join amrsreports_hiv_care_enrollment" +
                            "   on person_attribute.person_id = amrsreports_hiv_care_enrollment.person_id" +
                            " where (person_attribute.voided = 0" +
                            "        or (person_attribute.voided = 1 and person_attribute.void_reason like 'New value: %'))" +
                            "   and person_attribute.person_attribute_type_id = 7" +
                            "   and person_attribute.value = '" + location.getLocationId() + "'" +
                            "   and amrsreports_hiv_care_enrollment.enrollment_date is not null";
            sql = sql + personAttributeQuery;
        }

		SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition(sql.replaceAll(":reportDate", reportDate));
		Cohort results = Context.getService(CohortDefinitionService.class).evaluate(sqlCohortDefinition, context);

		return new EvaluatedCohort(results, sqlCohortDefinition, context);
	}
}
