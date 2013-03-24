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
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.reporting.data.FluconazoleStartStopDataDefinition;
import org.openmrs.module.amrsreports.rule.util.MohRuleUtils;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.util.OpenmrsUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 */
@Handler(supports = FluconazoleStartStopDataDefinition.class, order = 50)
public class PmtctPregnancyDataEvaluator extends DrugStartStopDataEvaluator {

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
				" select person_id, value_datetime " +
						" from obs " +
						" where (concept_id = 6743 or concept_id = 5596) " +
						"     and person_id in (:personIds) " +
						"     and obs_datetime <= ':reportDate'" +
						"     and voided = 0 " +
						" order by person_id asc, obs_datetime asc";
		Map<Integer, Set<String>> mappedValueDates = makeDatesMapFromSQL(startQuery, substitutions);

		for (Integer memberId : memberIds) {
			Set<String> dates = safeFind(mappedValueDates, memberId);
			data.addData(memberId, StringUtils.join(dates, ";"));
		}

		return data;
	}

	private Set<String> safeFind(final Map<Integer, Set<String>> map, final Integer key) {
		if (map.containsKey(key))
			return map.get(key);
		return new LinkedHashSet<String>();
	}


	/**
	 * replaces reportDate and personIds with data from private variables before generating a date map
	 */
	private Map<Integer, Set<String>> makeDatesMapFromSQL(final String query, final Map<String, String> substitutions) {
		String sql = query;
		for (String key : substitutions.keySet()) {
			String replacement = substitutions.get(key);
			if (StringUtils.isNotEmpty(replacement))
				sql = sql.replaceAll(key, replacement);
		}
		List<List<Object>> data = Context.getAdministrationService().executeSQL(sql, false);

		Map<Integer, Set<String>> mappedDates = new HashMap<Integer, Set<String>>();
		for (List<Object> objects : data) {
			// there will be two objects per list
			Integer personId = (Integer) objects.get(0);
			Date valueDate = (Date) objects.get(1);
			Set<String> dates = mappedDates.get(personId);
			if (dates == null) {
				dates = new LinkedHashSet<String>();
				mappedDates.put(personId, dates);
			}
			dates.add(MohRuleUtils.formatdates(valueDate) + " | PMTCT");
		}

		return mappedDates;
	}
}
