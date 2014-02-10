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

import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.model.SortedObsFromDate;
import org.openmrs.module.amrsreports.reporting.common.ObsDatetimeComparator;
import org.openmrs.module.amrsreports.reporting.common.SortedSetMap;
import org.openmrs.module.amrsreports.reporting.data.SortedObsSinceOtherDefinitionDataDefinition;
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
@Handler(supports = SortedObsSinceOtherDefinitionDataDefinition.class, order = 50)
public class SortedObsSinceOtherDefinitionDataEvaluator implements PersonDataEvaluator {

	/**
	 * @should return the obs that match the passed definition configuration
	 * @see PersonDataEvaluator#evaluate(PersonDataDefinition, EvaluationContext)
	 */
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {

		SortedObsSinceOtherDefinitionDataDefinition def = (SortedObsSinceOtherDefinitionDataDefinition) definition;
		EvaluatedPersonData c = new EvaluatedPersonData(def, context);

		if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
			return c;
		}

		EvaluatedPersonData effectiveDates = Context.getService(PersonDataService.class).evaluate(def.getEffectiveDateDefinition(), context);

		DataSetQueryService qs = Context.getService(DataSetQueryService.class);

		StringBuilder hql = new StringBuilder();
		Map<String, Object> m = new HashMap<String, Object>();

		hql.append("from 		Obs ");
		hql.append("where 		voided = false ");

		if (context.getBaseCohort() != null) {
			hql.append("and 		personId in (:patientIds) ");
			m.put("patientIds", context.getBaseCohort());
		}

		hql.append("and 		concept in (:questionList) ");
		m.put("questionList", def.getQuestions());

		List<Object> queryResult = qs.executeHqlQuery(hql.toString(), m);

		SortedSetMap<Integer, Obs> obsForPatients = new SortedSetMap<Integer, Obs>();
		obsForPatients.setSetComparator(new ObsDatetimeComparator());

		for (Object o : queryResult) {
			Obs obs = (Obs) o;
			obsForPatients.putInList(obs.getPersonId(), obs);
		}

		for (Integer pId : obsForPatients.keySet()) {
			SortedObsFromDate sofd = new SortedObsFromDate();

			Object effectiveDate = effectiveDates.getData().get(pId);
			sofd.setReferenceDate(effectiveDate == null ? null : (Date) effectiveDate);
			sofd.setData(obsForPatients.get(pId));

			c.addData(pId, sofd);
		}

		return c;
	}
}
