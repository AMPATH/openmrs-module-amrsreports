package org.openmrs.module.amrsreports.reporting.data.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.amrsreports.reporting.data.TransferStatusDataDefinition;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handler for enrollment date column
 */
@Handler(supports = TransferStatusDataDefinition.class, order = 50)
public class TransferStatusDataEvaluator implements PersonDataEvaluator {

	/**
	 * Returns a true or false value based on whether a person has transferred to the facility for this report
	 *
	 * @param definition the definition to be evaluated
	 * @param context    the context for the definition's evaluation
	 * @return true or false whether a person has transferred into the given facility
	 * @throws EvaluationException
	 * @should return true if the person was enrolled at a different facility
	 * @should return false if the person was enrolled at this facility
	 */
	@Override
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
		EvaluatedPersonData ret = new EvaluatedPersonData(definition, context);

		if (context.getBaseCohort().isEmpty())
			return ret;

		// get the facility
		MOHFacility facility = (MOHFacility) context.getParameterValue("facility");

		if (facility == null) {
			return ret;
		}

		// find people who enrolled at this facility
		String hql = "select patient.id from HIVCareEnrollment" +
				" where enrollmentLocation in (:locationList)" +
				" and enrollmentDate <= :reportDate";

		Map<String, Object> m = new HashMap<String, Object>();
		m.put("locationList", facility.getLocations());
		m.put("reportDate", context.getEvaluationDate());

		DataSetQueryService qs = Context.getService(DataSetQueryService.class);
		List<Object> queryResult = qs.executeHqlQuery(hql, m);

		// convert returned data into ids
		List<Integer> locals = new ArrayList<Integer>();
		for (Object o : queryResult) {
			locals.add((Integer) o);
		}

		// set value to opposite of whether the person is enrolled in this facility
		for (Integer personId : context.getBaseCohort().getMemberIds()) {
			ret.addData(personId, !locals.contains(personId));
		}

		return ret;
	}
}
