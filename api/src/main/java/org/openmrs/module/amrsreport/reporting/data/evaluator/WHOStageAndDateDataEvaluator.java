package org.openmrs.module.amrsreport.reporting.data.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreport.model.WHOStageAndDate;
import org.openmrs.module.amrsreport.reporting.data.EnrollmentDateDataDefinition;
import org.openmrs.module.amrsreport.reporting.data.WHOStageAndDateDataDefinition;
import org.openmrs.module.amrsreport.service.MohCoreService;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.Date;
import java.util.Map;

/**
 * Evaluator for WHO Stage and Date columns
 */
@Handler(supports=WHOStageAndDateDataDefinition.class, order=50)
public class WHOStageAndDateDataEvaluator implements PersonDataEvaluator {

	@Override
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
		MohCoreService service = Context.getService(MohCoreService.class);
		EvaluatedPersonData ret = new EvaluatedPersonData(definition, context);

		// pull the enrollment date map from the system
		Map<Integer, WHOStageAndDate> whoStageAndDateMap = service.getWHOStageAndDateMap(context.getBaseCohort().getMemberIds());
		for (Integer personId: context.getBaseCohort().getMemberIds()) {
			ret.addData(personId, whoStageAndDateMap.containsKey(personId) ? whoStageAndDateMap.get(personId) : null);
		}

		return ret;
	}
}
