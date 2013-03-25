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

import org.apache.commons.lang.StringUtils;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.reporting.data.CtxStartStopDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.FluconazoleStartStopDataDefinition;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 */
@Handler(supports = CtxStartStopDataDefinition.class, order = 50)
public class CtxStartStopDataEvaluator extends DrugStartStopDataEvaluator {

	@Override
	public EvaluatedPersonData evaluate(final PersonDataDefinition definition, final EvaluationContext context) throws EvaluationException {
		EvaluatedPersonData data = new EvaluatedPersonData(definition, context);

		Map<String, String> substitutions = new HashMap<String, String>();
		Set<Integer> memberIds = context.getBaseCohort().getMemberIds();
		String personIds = StringUtils.join(memberIds, ",");
		substitutions.put(":personIds", personIds);
		String reportDate = new SimpleDateFormat("yyyy-MM-dd").format(context.getEvaluationDate());
		substitutions.put(":reportDate", reportDate);

		String startQuery =
				" select person_id, obs_datetime " +
						" from obs " +
						" where ((concept_id = 1263 and value_coded is not null) " +
						"     or (concept_id = 1193 and value_coded = 916) " +
						"     or (concept_id = 1109 and value_coded = 916)) " +
						"     and person_id in (:personIds) " +
						"     and obs_datetime <= ':reportDate'" +
						"     and obs_datetime >= '2001-01-01'" +
						"     and voided = 0 " +
						" order by person_id asc, obs_datetime asc";
		Map<Integer, Set<Date>> mappedStartDates = makeDatesMapFromSQL(startQuery, substitutions);

		String stopQuery =
				" select person_id, obs_datetime " +
						" from obs " +
						" where (concept_id = 1262 and value_coded is not null) " +
						"     and person_id in (:personIds) " +
						"     and obs_datetime <= ':reportDate'" +
						"     and obs_datetime >= '2001-01-01'" +
						"     and voided = 0 " +
						" order by person_id asc, obs_datetime asc";
		Map<Integer, Set<Date>> mappedStopDates = makeDatesMapFromSQL(stopQuery, substitutions);

		for (Integer memberId : memberIds) {
			Set<Date> stopDates = safeFind(mappedStopDates, memberId);
			Set<Date> startDates = safeFind(mappedStartDates, memberId);
			String rangeInformation = buildRangeInformation(startDates, stopDates);
			data.addData(memberId, rangeInformation);
		}

		return data;
	}

	private Set<Date> safeFind(final Map<Integer, Set<Date>> map, final Integer key) {
		if (map.containsKey(key))
			return map.get(key);
		return new LinkedHashSet<Date>();
	}


	/**
	 * replaces reportDate and personIds with data from private variables before generating a date map
	 */
	private Map<Integer, Set<Date>> makeDatesMapFromSQL(final String query, final Map<String, String> substitutions) {
		String sql = query;
		for (String key : substitutions.keySet()) {
			String replacement = substitutions.get(key);
			if (StringUtils.isNotEmpty(replacement))
				sql = sql.replaceAll(key, replacement);
		}
		List<List<Object>> data = Context.getAdministrationService().executeSQL(sql, false);

		Map<Integer, Set<Date>> mappedDates = new HashMap<Integer, Set<Date>>();
		for (List<Object> objects : data) {
			// there will be two objects per list
			Integer personId = (Integer) objects.get(0);
			Date dateValue = (Date) objects.get(1);
			Set<Date> dates = mappedDates.get(personId);
			if (dates == null) {
				dates = new LinkedHashSet<Date>();
				mappedDates.put(personId, dates);
			}
			dates.add(dateValue);
		}

		return mappedDates;
	}
}
