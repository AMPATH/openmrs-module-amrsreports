package org.openmrs.module.amrsreports.reporting.data.evaluator;

import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.reporting.data.LastRTCDateDataDefinition;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Evaluator for WHO Stage and Date columns
 */
@Handler(supports = LastRTCDateDataDefinition.class, order = 50)
public class LastRTCDateDataEvaluator implements PersonDataEvaluator {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
		LastRTCDateDataDefinition def = (LastRTCDateDataDefinition) definition;
		EvaluatedPersonData c = new EvaluatedPersonData(def, context);

		if (context.getBaseCohort() == null || context.getBaseCohort().isEmpty()) {
			return c;
		}

		DataSetQueryService qs = Context.getService(DataSetQueryService.class);

		StringBuilder hql = new StringBuilder();
		Map<String, Object> m = new HashMap<String, Object>();

		hql.append("from Obs ");
		hql.append("where voided = false ");

		if (context.getBaseCohort() != null) {
			hql.append("and personId in (:patientIds) ");
			m.put("patientIds", context.getBaseCohort());
		}

		hql.append("and encounter.encounterType.id in (1, 2, 3, 4, 13, 14, 15, 17, 18, 19, 20, 21, 22, 23, 26)");
		hql.append("and concept.id in (1502, 5096)  ");
		hql.append("and obsDatetime <= :onOrBefore ");
		m.put("onOrBefore", context.getEvaluationDate());

		hql.append("order by obsDatetime desc");

		List<Object> queryResult = qs.executeHqlQuery(hql.toString(), m);

		ListMap<Integer, Obs> obsForPatients = new ListMap<Integer, Obs>();
		for (Object o : queryResult) {
			Obs obs = (Obs) o;
			obsForPatients.putInList(obs.getPersonId(), obs);
		}

		for (Integer pId : obsForPatients.keySet()) {
			List<Obs> l = obsForPatients.get(pId);
			c.addData(pId, l.get(0));
		}

		return c;
	}
}
