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
import org.openmrs.module.amrsreports.AmrsReportsConstants;
import org.openmrs.module.amrsreports.reporting.data.CtxStartDataDefinition;
import org.openmrs.module.amrsreports.service.MohCoreService;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * evaluator for CtxStartDataDefinition
 */
@Handler(supports = CtxStartDataDefinition.class, order = 50)
public class CtxStartDataEvaluator extends DrugStartStopDataEvaluator {

	@Override
	public EvaluatedPersonData evaluate(final PersonDataDefinition definition, final EvaluationContext context) throws EvaluationException {
		EvaluatedPersonData data = new EvaluatedPersonData(definition, context);

		if (context.getBaseCohort().isEmpty())
			return data;

		String hql = "select personId, obsDatetime" +
				"	from Obs" +
				" 	where voided = false" +
				"   	and personId in (:patientIds)" +
				"   	and concept.id = 1263" +
				"   	and valueCoded.id = 916";

		Map<String, Object> m = new HashMap<String, Object>();
		m.put("patientIds", context.getBaseCohort());

		ListMap<Integer, Date> mappedStartDates = makeDatesMapFromHQL(hql, m);

		for (Integer memberId : context.getBaseCohort().getMemberIds()) {

			Set<Date> startDates = safeFind(mappedStartDates, memberId);
			data.addData(memberId, startDates);
		}

		return data;
	}

	/**
	 * replaces reportDate and personIds with data from private variables before generating a date map
	 */
	private ListMap<Integer, Date> makeDatesMapFromHQL(final String query, final Map<String, Object> substitutions) {
		MohCoreService mcs = Context.getService(MohCoreService.class);
		List<Object> queryResult = mcs.executeScrollingHqlQuery(query, substitutions);

		ListMap<Integer, Date> dateListMap = new ListMap<Integer, Date>();
		for (Object o : queryResult) {
			Object[] parts = (Object[]) o;
			if (parts.length == 2) {
				Integer pId = (Integer) parts[0];
				Date date = (Date) parts[1];
				dateListMap.putInList(pId, date);
			}
		}

		return dateListMap;
	}
}
