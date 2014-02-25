package org.openmrs.module.amrsreports.reporting.data.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.amrsreports.reporting.data.ARTTransferStatusDataDefinition;
import org.openmrs.module.amrsreports.service.MohCoreService;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handler for enrollment date column
 */
@Handler(supports = ARTTransferStatusDataDefinition.class, order = 50)
public class ARTTransferStatusDataEvaluator implements PersonDataEvaluator {

	@Override
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
		EvaluatedPersonData ret = new EvaluatedPersonData(definition, context);

		if (context.getBaseCohort().isEmpty())
			return ret;

		// find the facility number
		MOHFacility facility = (MOHFacility) context.getParameterValue("facility");

		if (facility == null) {
			return ret;
		}

		String sql = "select patient_id " +
				"from amrsreports_hiv_care_enrollment " +
				"where first_arv_date <= :reportDate " +
				"and first_arv_location_id in (:locationList)";

		Map<String, Object> m = new HashMap<String, Object>();
		m.put("reportDate", context.getEvaluationDate());
		m.put("locationList", facility.getLocations());

		List<Object> queryResult = Context.getService(MohCoreService.class).executeSqlQuery(sql, m);

		for (Integer patientId : context.getBaseCohort().getMemberIds()) {
			ret.addData(patientId, !queryResult.contains(patientId));
		}

		return ret;
	}
}
