package org.openmrs.module.amrsreports.reporting.data.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.AmrsReportsConstants;
import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.amrsreports.reporting.data.FirstEncounterAtFacilityDataDefinition;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handler for last encounter at facility
 */
@Handler(supports = FirstEncounterAtFacilityDataDefinition.class, order = 50)
public class FirstEncounterAtFacilityDataEvaluator implements PersonDataEvaluator {

	private final Log log = LogFactory.getLog(getClass());

	@Override
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {

		FirstEncounterAtFacilityDataDefinition def = (FirstEncounterAtFacilityDataDefinition) definition;
		EvaluatedPersonData c = new EvaluatedPersonData(def, context);

		if (context.getBaseCohort() == null || context.getBaseCohort().isEmpty()) {
			return c;
		}

		// find the facility number
		MOHFacility facility = (MOHFacility) context.getParameterValue("facility");

		// fail quickly if the facility does not exist
		if (facility == null) {
			log.warn("No facility provided; returning empty data.");
			return c;
		}

		// use HQL to do our bidding
		String hql = "from Encounter" +
				" where voided=false" +
				" and patientId in (:patientIds)" +
				" and location in (:locationList)" +
				" and encounterDatetime <= :onOrBefore" +
				" order by encounterDatetime asc";

		Map<String, Object> m = new HashMap<String, Object>();
		m.put("patientIds", context.getBaseCohort());
		m.put("locationList", facility.getLocations());
		m.put("onOrBefore", context.getEvaluationDate());

		DataSetQueryService qs = Context.getService(DataSetQueryService.class);
		List<Object> queryResult = qs.executeHqlQuery(hql, m);

		ListMap<Integer, Encounter> encForPatients = new ListMap<Integer, Encounter>();
		for (Object o : queryResult) {
			Encounter enc = (Encounter) o;
			encForPatients.putInList(enc.getPatientId(), enc);
		}

		for (Integer pId : encForPatients.keySet()) {
			List<Encounter> l = encForPatients.get(pId);
			c.addData(pId, l.get(0));
		}

		return c;
	}
}
