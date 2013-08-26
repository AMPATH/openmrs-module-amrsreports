package org.openmrs.module.amrsreports.reporting.data.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.amrsreports.reporting.common.ObsRepresentation;
import org.openmrs.module.amrsreports.reporting.data.EligibilityForARTDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.FirstWHOStageDataDefinition;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

/**
 * Evaluator for WHO Stage and Date columns
 */
@Handler(supports = FirstWHOStageDataDefinition.class, order = 50)
public class FirstWHOStageDataEvaluator extends BatchedExecutionDataEvaluator {

	private Log log = LogFactory.getLog(getClass());

	private FirstWHOStageDataDefinition definition;

	@Override
	protected PersonDataDefinition setDefinition(PersonDataDefinition def) {
		definition = (FirstWHOStageDataDefinition) def;
		return definition;
	}

	@Override
	protected Object doExecute(Integer pId, SortedSet<ObsRepresentation> o, EvaluationContext context) {
		ObsRepresentation or = o.first();
		if (!or.getObsDatetime().after(context.getEvaluationDate()))
			return or;
		return null;
	}

	@Override
	protected boolean doBefore(EvaluationContext context, EvaluatedPersonData c) {
		// pass
		return true;
	}

	@Override
	protected void doAfter(EvaluationContext context, EvaluatedPersonData c) {
		// pass
	}

	@Override
	protected String getHQL() {
		return "select new map(" +
				"		personId as personId, " +
				"		valueCoded.id as valueCodedId," +
				"		obsDatetime as obsDatetime)" +
				"	from Obs " +
				"	where voided = false " +
				"		and personId in (:personIds) " +
				"		and concept.id in (1224, 5356)";
	}

	@Override
	protected Map<String, Object> getSubstitutions() {
		return new HashMap<String, Object>();
	}
}
