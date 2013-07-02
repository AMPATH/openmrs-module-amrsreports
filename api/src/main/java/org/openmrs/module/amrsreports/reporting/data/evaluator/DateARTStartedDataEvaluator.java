package org.openmrs.module.amrsreports.reporting.data.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.HIVCareEnrollment;
import org.openmrs.module.amrsreports.reporting.data.DateARTStartedDataDefinition;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

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

		hql.append("from HIVCareEnrollment ");
		hql.append(" where");

		hql.append(" patient.personId in (:personIds) ");
		m.put("personIds", context.getBaseCohort());

		hql.append("and firstARVDate <= :onOrBefore ");
		m.put("onOrBefore", context.getEvaluationDate());

		List<Object> queryResult = qs.executeHqlQuery(hql.toString(), m);

		for (Object o : queryResult) {
			HIVCareEnrollment enrollment = (HIVCareEnrollment) o;
			c.addData(enrollment.getPatient().getPersonId(), enrollment.getFirstARVDate());
		}

		return c;
	}
}
