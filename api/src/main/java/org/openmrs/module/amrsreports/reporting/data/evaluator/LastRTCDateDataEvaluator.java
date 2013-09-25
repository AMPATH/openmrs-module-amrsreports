package org.openmrs.module.amrsreports.reporting.data.evaluator;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.amrsreports.reporting.common.ObsRepresentation;
import org.openmrs.module.amrsreports.reporting.common.ObsRepresentationDatetimeComparator;
import org.openmrs.module.amrsreports.reporting.data.LastRTCDateDataDefinition;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

/**
 * Evaluator for WHO Stage and Date columns
 */
@Handler(supports = LastRTCDateDataDefinition.class, order = 50)
public class LastRTCDateDataEvaluator extends BatchedExecutionDataEvaluator<ObsRepresentation> {

	private LastRTCDateDataDefinition definition;

	@Override
	protected ObsRepresentation renderSingleResult(Map<String, Object> m) {
		return new ObsRepresentation(m);
	}

	@Override
	protected Comparator<ObsRepresentation> getResultsComparator() {
		return new ObsRepresentationDatetimeComparator();
	}

	@Override
	protected PersonDataDefinition setDefinition(PersonDataDefinition def) {
		definition = (LastRTCDateDataDefinition) def;
		return definition;
	}

	@Override
	protected Object doExecute(Integer pId, SortedSet<ObsRepresentation> o, EvaluationContext context) {
		return o.last();
	}

	@Override
	protected boolean doBefore(EvaluationContext context, EvaluatedPersonData c, Cohort cohort) {
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
				"		valueDatetime as valueDatetime," +
				"		obsDatetime as obsDatetime)" +
				"	from Obs " +
				"	where voided = false " +
				"		and personId in (:personIds) " +
				"		and concept.id in (1502, 5096)" +
				"		and obsDatetime <= :reportDate" +
				"		and encounter.encounterType.id in (1, 2, 3, 4, 13, 14, 15, 17, 18, 19, 20, 21, 22, 23, 26)";
	}

	@Override
	protected Map<String, Object> getSubstitutions(EvaluationContext context) {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("reportDate", context.getEvaluationDate());
		return m;
	}
}
