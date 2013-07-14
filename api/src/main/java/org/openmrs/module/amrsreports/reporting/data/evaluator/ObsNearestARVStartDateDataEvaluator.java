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


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.reporting.data.ObsNearestARVStartDateDataDefinition;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handler for last HIV encounter data
 */
@Handler(supports = ObsNearestARVStartDateDataDefinition.class, order = 50)
public class ObsNearestARVStartDateDataEvaluator implements PersonDataEvaluator {

	private final Log log = LogFactory.getLog(getClass());

	@Override
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
		ObsNearestARVStartDateDataDefinition def = (ObsNearestARVStartDateDataDefinition) definition;
		EvaluatedPersonData c = new EvaluatedPersonData(def, context);

		// give up early if the cohort is empty
		if (context.getBaseCohort() == null || context.getBaseCohort().isEmpty()) {
			return c;
		}

		DataSetQueryService qs = Context.getService(DataSetQueryService.class);

		// define the HQL
		String hql = "FROM Obs AS o, HIVCareEnrollment AS hce" +
				" WHERE" +
				"	o.person.personId = hce.patient.personId" +
				"   AND o.voided = false" +
				"	AND o.personId in (:patientIds)" +
				"	AND o.concept.conceptId in (:conceptIds)" +
				"	AND o.obsDatetime <= :onOrBefore" +
				"	AND o.obsDatetime BETWEEN SUBDATE(hce.firstARVDate, 21) AND ADDDATE(hce.firstARVDate, 7)" +
				" ORDER BY" +
				"	o.person.personId asc, ABS(DATEDIFF(o.obsDatetime, hce.firstARVDate)) asc";

		// set the variables
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("patientIds", context.getBaseCohort());
		m.put("onOrBefore", context.getEvaluationDate());
		m.put("conceptIds", getListOfIds(def.getQuestions()));

		// execute the HQL
		List<Object> queryResult = qs.executeHqlQuery(hql.toString(), m);

		// create a listmap of the observations
		ListMap<Integer, Obs> obsListMap = new ListMap<Integer, Obs>();
		for (Object o : queryResult) {

			// each result is an array: [Obs, HIVCareEnrollment]
			Object[] arr = (Object[]) o;
			Obs obs = (Obs) arr[0];

			obsListMap.putInList(obs.getPersonId(), obs);
		}

		// get the first one
		for (Integer pId : obsListMap.keySet()) {
			List<Obs> l = obsListMap.get(pId);
			c.addData(pId, l.get(0));
		}

		return c;
	}

	private List<Integer> getListOfIds(List<Concept> questions) {
		List<Integer> l = new ArrayList<Integer>();
		for (Concept c : questions) {
			l.add(c.getId());
		}
		return l;
	}
}
