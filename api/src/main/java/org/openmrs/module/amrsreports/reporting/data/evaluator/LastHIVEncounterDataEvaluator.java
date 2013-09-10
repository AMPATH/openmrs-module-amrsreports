package org.openmrs.module.amrsreports.reporting.data.evaluator;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.amrsreports.reporting.common.EncounterRepresentation;
import org.openmrs.module.amrsreports.reporting.common.EncounterRepresentationDatetimeComparator;
import org.openmrs.module.amrsreports.reporting.data.LastHIVEncounterDataDefinition;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

/**
 * Handler for last HIV encounter data
 */
@Handler(supports = LastHIVEncounterDataDefinition.class, order = 50)
public class LastHIVEncounterDataEvaluator extends BatchedExecutionDataEvaluator<EncounterRepresentation> {

	private LastHIVEncounterDataDefinition definition;

	@Override
	protected EncounterRepresentation renderSingleResult(Map<String, Object> m) {
		return new EncounterRepresentation(m);
	}

	@Override
	protected Comparator<EncounterRepresentation> getResultsComparator() {
		return new EncounterRepresentationDatetimeComparator();
	}

	@Override
	protected PersonDataDefinition setDefinition(PersonDataDefinition def) {
		definition = (LastHIVEncounterDataDefinition) def;
		return definition;
	}

	@Override
	protected Object doExecute(Integer pId, SortedSet<EncounterRepresentation> o, EvaluationContext context) {
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
				"   e.patientId as personId," +
				"   e.encounterDatetime as encounterDatetime," +
				"	e.location.name as locationName" +
				" )" +
				" from Encounter e" +
				" where e.voided = false" +
				"   and e.patientId in (:personIds) " +
				"   and e.encounterType.id in (:encounterTypeIds)" +
				"   and e.encounterDatetime <= :onOrBefore";
	}

	@Override
	protected Map<String, Object> getSubstitutions(EvaluationContext context) {

		Map<String, Object> m = new HashMap<String, Object>();
		m.put("encounterTypeIds", Arrays.asList(1, 2, 3, 4, 13));
		m.put("onOrBefore", context.getEvaluationDate());

		return m;
	}
}
