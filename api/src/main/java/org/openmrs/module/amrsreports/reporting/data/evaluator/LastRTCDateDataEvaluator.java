package org.openmrs.module.amrsreports.reporting.data.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.reporting.common.ObsRepresentation;
import org.openmrs.module.amrsreports.reporting.common.ObsRepresentationDatetimeComparator;
import org.openmrs.module.amrsreports.reporting.common.SortedSetMap;
import org.openmrs.module.amrsreports.reporting.data.LastRTCDateDataDefinition;
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
import java.util.SortedSet;

/**
 * Evaluator for WHO Stage and Date columns
 */
@Handler(supports = LastRTCDateDataDefinition.class, order = 50)
public class LastRTCDateDataEvaluator implements PersonDataEvaluator {

	@Override
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
		LastRTCDateDataDefinition def = (LastRTCDateDataDefinition) definition;
		EvaluatedPersonData c = new EvaluatedPersonData(def, context);

		if (context.getBaseCohort() == null || context.getBaseCohort().isEmpty()) {
			return c;
		}

		DataSetQueryService qs = Context.getService(DataSetQueryService.class);


		String hql = "select new map(" +
				"		personId as personId, " +
				"		valueDatetime as valueDatetime," +
				"		obsDatetime as obsDatetime)" +
				"	from Obs " +
				"	where voided = false " +
				"		and personId in (:personIds) " +
				"		and concept.id in (1502, 5096)" +
				"		and obsDatetime <= :reportDate" +
				"		and encounter.encounterType.id in (1, 2, 3, 4, 13, 14, 15, 17, 18, 19, 20, 21, 22, 23, 26)";

		Map<String, Object> m = new HashMap<String, Object>();
		m.put("personIds", context.getBaseCohort());
		m.put("reportDate", context.getEvaluationDate());

		List<Object> queryResult = qs.executeHqlQuery(hql, m);

		SortedSetMap<Integer, ObsRepresentation> obsForPatients = new SortedSetMap<Integer, ObsRepresentation>();
		obsForPatients.setSetComparator(new ObsRepresentationDatetimeComparator());

		for (Object o : queryResult) {
			ObsRepresentation or = new ObsRepresentation((Map<String, Object>) o);
			obsForPatients.putInList(or.getPersonId(), or);
		}

		for (Integer pId : obsForPatients.keySet()) {
			SortedSet<ObsRepresentation> l = obsForPatients.get(pId);
			c.addData(pId, l.last());
		}

		return c;
	}
}
