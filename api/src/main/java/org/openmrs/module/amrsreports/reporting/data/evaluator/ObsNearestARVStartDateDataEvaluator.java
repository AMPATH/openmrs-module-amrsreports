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
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.reporting.data.DateARTStartedDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.ObsNearestARVStartDateDataDefinition;
import org.openmrs.module.reporting.common.Age;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.data.MappedData;
import org.openmrs.module.reporting.data.converter.DateConverter;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.AgeAtDateOfOtherDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
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

		Cohort cohort = new Cohort(context.getBaseCohort().getMemberIds());

		// give up early if the cohort is empty
		if (cohort == null || cohort.isEmpty()) {
			return c;
		}

		if (def.getAgeLimit() != null) {

			// create a mapped Date ART Started definition
			DateARTStartedDataDefinition artStarted = new DateARTStartedDataDefinition();
			MappedData<DateARTStartedDataDefinition> mappedDef = new MappedData<DateARTStartedDataDefinition>();
			mappedDef.setParameterizable(artStarted);
			mappedDef.addConverter(new DateConverter());

			// create the Age definition
			AgeAtDateOfOtherDataDefinition startAge = new AgeAtDateOfOtherDataDefinition();
			startAge.setEffectiveDateDefinition(mappedDef);

			// evaluate it
			EvaluatedPersonData startAges = Context.getService(PersonDataService.class).evaluate(startAge, context);

			// get the evaluated data
			Map<Integer, Object> artStartAges = startAges.getData();

			// loop and remove from base cohort if over the age limit
			for (Integer personId : artStartAges.keySet()) {
				Age age = (Age) artStartAges.get(personId);
				if (age.getFullYears() > def.getAgeLimit())
					cohort.removeMember(personId);
			}
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
		m.put("patientIds", cohort);
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

		// fill response data by taking first in list (if it's in there)
		for (Integer pId : obsListMap.keySet()) {
			c.addData(pId, obsListMap.get(pId).get(0));
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
