package org.openmrs.module.amrsreports.reporting.data.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.AmrsReportsConstants;
import org.openmrs.module.amrsreports.HIVCareEnrollment;
import org.openmrs.module.amrsreports.reporting.data.DateARTStartedDataDefinition;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Evaluator for ART Eligibility
 */
@Handler(supports = DateARTStartedDataDefinition.class, order = 50)
public class DateARTStartedDataEvaluator implements PersonDataEvaluator {

	@Override
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {

		DateARTStartedDataDefinition def = (DateARTStartedDataDefinition) definition;
		EvaluatedPersonData c = new EvaluatedPersonData(def, context);

		if (context.getBaseCohort() == null || context.getBaseCohort().isEmpty()) {
			return c;
		}

		DataSetQueryService qs = Context.getService(DataSetQueryService.class);

		StringBuilder hql = new StringBuilder();
		Map<String, Object> m = new HashMap<String, Object>();

		hql.append("select hce.patient.patientId, hce.firstARVDate");
		hql.append(" from HIVCareEnrollment as hce");
		hql.append(" where");

		hql.append(" hce.patient.patientId in (" +
				"	SELECT elements(c.memberIds) from Cohort as c" +
				"	where c.uuid = :cohortUuid" +
				") ");
		m.put("cohortUuid", AmrsReportsConstants.SAVED_COHORT_UUID);

		hql.append("and hce.firstARVDate <= :onOrBefore ");
		m.put("onOrBefore", context.getEvaluationDate());

		List<Object> queryResult = qs.executeHqlQuery(hql.toString(), m);

		for (Object o : queryResult) {
			Object[] parts = (Object[]) o;
			if (parts.length == 2) {
				Integer pId = (Integer) parts[0];
				Date firstARVDate = (Date) parts[1];
				c.addData(pId, firstARVDate);
			}
		}

		return c;
	}
}
