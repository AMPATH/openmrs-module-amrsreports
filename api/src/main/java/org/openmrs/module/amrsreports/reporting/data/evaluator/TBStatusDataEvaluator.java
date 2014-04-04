/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.amrsreports.reporting.data.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.model.SortedItemsFromDate;
import org.openmrs.module.amrsreports.reporting.common.ObsRepresentation;
import org.openmrs.module.amrsreports.reporting.common.ObsRepresentationDatetimeComparator;
import org.openmrs.module.amrsreports.reporting.common.SortedSetMap;
import org.openmrs.module.amrsreports.reporting.data.TBStatusDataDefinition;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Evaluates an ObsForPersonDataDefinition to produce a PersonData
 */
@Handler(supports = TBStatusDataDefinition.class, order = 50)
public class TBStatusDataEvaluator implements PersonDataEvaluator {

	public TBStatusDataEvaluator() {
		super();
	}

	/**
	 * @should return the obs that match the passed definition configuration
	 * @see org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator#evaluate(org.openmrs.module.reporting.data.person.definition.PersonDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {

		TBStatusDataDefinition def = (TBStatusDataDefinition) definition;

		EvaluatedPersonData c = new EvaluatedPersonData(def, context);

		if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
			return c;
		}

		EvaluatedPersonData effectiveDates = Context.getService(PersonDataService.class).evaluate(def.getEffectiveDateDefinition(), context);

		DataSetQueryService qs = Context.getService(DataSetQueryService.class);
		Map<String, Object> m = new HashMap<String, Object>();


		String hql = "select new map(" +
				"		personId as personId, " +
				"		concept.id as conceptId," +
				"		valueCoded.id as valueCodedId," +
				"		obsDatetime as obsDatetime)" +
				" from Obs" +
				" Where " +
				"   voided = false " +
				"   and person.id in (:patientIds) " +
				"   and (" +
				"     (concept.id in (307,2020,2021,2022,2028,6174,7178))  " +
				"     or (concept.id = 5959 and valueCoded.id in (1073,1074,1079))  " +
				"     or (concept.id = 1069 and valueCoded.id = 6171)  " +
				"     or (concept.id = 1111 and valueCoded in (1107,1267))  " +
				"     or (concept.id = 5965 and valueCoded in (1066,1267,1794)) " +
				"     or (concept.id in (2029,1159,1270) and valueCoded IS NOT NULL)  " +
				"     or (concept.id = 6981 and valueCoded != 1067)  " +
				"     or (concept.id = 1268 and (valueCoded != 1107 or valueCoded = 1260))  " +
				"   )";

		if (context.getBaseCohort() != null) {
			m.put("patientIds", context.getBaseCohort());
		}

		List<Object> queryResult = qs.executeHqlQuery(hql, m);

		SortedSetMap<Integer, ObsRepresentation> obsForPatients = new SortedSetMap<Integer, ObsRepresentation>();
		obsForPatients.setSetComparator(new ObsRepresentationDatetimeComparator());

		for (Object o : queryResult) {
			Map<String, Object> res = (Map<String, Object>) o;
			ObsRepresentation obsRepresentation = new ObsRepresentation(res);
			obsForPatients.putInList(obsRepresentation.getPersonId(), obsRepresentation);
		}

		for (Integer pId : obsForPatients.keySet()) {
			SortedItemsFromDate sofd = new SortedItemsFromDate();

			Object effectiveDate = effectiveDates.getData().get(pId);
			sofd.setReferenceDate(effectiveDate == null ? null : (Date) effectiveDate);
			sofd.setData(obsForPatients.get(pId));

			c.addData(pId, sofd);
		}

		return c;
	}
}
