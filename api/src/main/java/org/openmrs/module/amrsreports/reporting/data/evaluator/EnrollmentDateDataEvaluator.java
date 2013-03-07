package org.openmrs.module.amrsreports.reporting.data.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.reporting.data.EnrollmentDateDataDefinition;
import org.openmrs.module.amrsreports.service.MohCoreService;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.Date;
import java.util.Map;

/**
 * Handler for enrollment date column
 */
@Handler(supports=EnrollmentDateDataDefinition.class, order=50)
public class EnrollmentDateDataEvaluator implements PersonDataEvaluator {

	@Override
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
		MohCoreService service = Context.getService(MohCoreService.class);
		EvaluatedPersonData ret = new EvaluatedPersonData(definition, context);

		// pull the enrollment date map from the system
		Map<Integer, Date> enrollmentDateMap = service.getEnrollmentDateMap(context.getBaseCohort().getMemberIds());
		for (Integer personId: context.getBaseCohort().getMemberIds()) {
			ret.addData(personId, enrollmentDateMap.containsKey(personId) ? enrollmentDateMap.get(personId) : null);
		}

		return ret;
	}
}
