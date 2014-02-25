package org.openmrs.module.amrsreports.reporting.data.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.reporting.data.AgeAtEvaluationDateDataDefinition;
import org.openmrs.module.reporting.data.converter.BirthdateToAgeConverter;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.Map;

/**
 * Returns the age of each person in the cohort, according to the evaluation date from the context
 */
@Handler(supports = AgeAtEvaluationDateDataDefinition.class, order = 50)
public class AgeAtEvaluationDateDataEvaluator implements PersonDataEvaluator {
	/**
	 * @see PersonDataEvaluator#evaluate(PersonDataDefinition, EvaluationContext)
	 */
	@Override
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {

		EvaluatedPersonData c = Context.getService(PersonDataService.class).evaluate(new BirthdateDataDefinition(), context);
		AgeAtEvaluationDateDataDefinition add = (AgeAtEvaluationDateDataDefinition) definition;

		BirthdateToAgeConverter converter = new BirthdateToAgeConverter(context.getEvaluationDate());
		EvaluatedPersonData ret = new EvaluatedPersonData(add, context);

		for (Map.Entry<Integer, Object> e : c.getData().entrySet()) {
			ret.addData(e.getKey(), converter.convert(e.getValue()));
		}
		return ret;
	}
}
