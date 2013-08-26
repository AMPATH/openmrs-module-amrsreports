package org.openmrs.module.amrsreports.reporting.data.evaluator;

import org.openmrs.Encounter;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.AmrsReportsConstants;
import org.openmrs.module.amrsreports.reporting.common.EncounterDatetimeComparator;
import org.openmrs.module.amrsreports.reporting.common.SortedSetMap;
import org.openmrs.module.amrsreports.reporting.data.LastHIVEncounterDataDefinition;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

/**
 * Handler for last HIV encounter data
 */
@Handler(supports = LastHIVEncounterDataDefinition.class, order = 50)
public class LastHIVEncounterDataEvaluator implements PersonDataEvaluator {

	@Override
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {

		LastHIVEncounterDataDefinition def = (LastHIVEncounterDataDefinition) definition;
		EvaluatedPersonData c = new EvaluatedPersonData(def, context);

		if (context.getBaseCohort() == null || context.getBaseCohort().isEmpty()) {
			return c;
		}

		DataSetQueryService qs = Context.getService(DataSetQueryService.class);

		// use HQL to do our bidding
		String hql = "from Encounter" +
				" where voided = false" +
				" and patientId in (:patientIds) " +
				" and encounterType.encounterTypeId in (:encounterTypeIds)" +
				" and encounterDatetime <= :onOrBefore";

		List<Integer> encounterTypeIds = Arrays.asList(1, 2, 3, 4, 13);

		Map<String, Object> m = new HashMap<String, Object>();
		m.put("patientIds", context.getBaseCohort());
		m.put("encounterTypeIds", encounterTypeIds);
		m.put("onOrBefore", context.getEvaluationDate());

		List<Object> queryResult = qs.executeHqlQuery(hql, m);

		SortedSetMap<Integer, Encounter> encForPatients = new SortedSetMap<Integer, Encounter>();
		encForPatients.setSetComparator(new EncounterDatetimeComparator());
		for (Object o : queryResult) {
			Encounter enc = (Encounter) o;
			encForPatients.putInList(enc.getPatientId(), enc);
		}

		for (Integer pId : encForPatients.keySet()) {
			SortedSet<Encounter> l = encForPatients.get(pId);
			c.addData(pId, l.last());
		}

		return c;
	}
}
