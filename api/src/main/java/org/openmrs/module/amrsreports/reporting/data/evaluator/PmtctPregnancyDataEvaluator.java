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
import org.openmrs.module.amrsreports.AmrsReportsConstants;
import org.openmrs.module.amrsreports.reporting.data.PmtctPregnancyDataDefinition;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 */
@Handler(supports = PmtctPregnancyDataDefinition.class, order = 50)
public class PmtctPregnancyDataEvaluator implements PersonDataEvaluator {

	@Override
	public EvaluatedPersonData evaluate(final PersonDataDefinition definition, final EvaluationContext context) throws EvaluationException {
		EvaluatedPersonData data = new EvaluatedPersonData(definition, context);

		if (context.getBaseCohort().isEmpty())
			return data;

		String sql = "select" +
				"	ap.person_id," +
				"	ap.episode," +
				"	ap.due_date" +
				" from (" +
				"	select person_id, episode, max(pregnancy_id) as p_id" +
				"	from amrsreports_pregnancy ap" +
				"		inner join (" +
				"			select patient_id from cohort_member cm" +
				"				inner join cohort c" +
				"					on cm.cohort_id = c.cohort_id" +
				"					where c.uuid = ':cohortUuid'" +
				"		) cms on ap.person_id = cms.patient_id" +
				"   where" +
				"     pregnancy_date < ':reportDate'" +
				"	group by person_id, episode " +
				"	having episode > 0" +
				"	order by person_id asc" +
				" ) ordered" +
				"	left join amrsreports_pregnancy ap" +
				"		on ap.pregnancy_id = ordered.p_id";

		String reportDate = new SimpleDateFormat("yyyy-MM-dd").format(context.getEvaluationDate());

		ListMap<Integer, Date> dateMap = makeDateMapFromSQL(sql, AmrsReportsConstants.SAVED_COHORT_UUID, reportDate);

		for (Integer memberId : context.getBaseCohort().getMemberIds()) {

			Set<Date> dueDates = safeFind(dateMap, memberId);
			data.addData(memberId, dueDates);
		}

		return data;
	}

	private Set<Date> safeFind(final ListMap<Integer, Date> map, final Integer key) {
		Set<Date> dateSet = new LinkedHashSet<Date>();
		if (map.containsKey(key))
			dateSet.addAll(map.get(key));
		return dateSet;
	}

	/**
	 * replaces reportDate and personIds with data from private variables before generating a date map
	 */
	private ListMap<Integer, Date> makeDateMapFromSQL(String sql, String cohortUuid, String reportDate) {
		List<List<Object>> data = Context.getAdministrationService().executeSQL(
				sql.replaceAll(":reportDate", reportDate).replaceAll(":cohortUuid", cohortUuid),
				true);

		return makeDateMap(data);
	}

	/**
	 * generates a map of integers to dates, assuming this is the kind of response expected from the SQL
	 */
	private ListMap<Integer, Date> makeDateMap(List<List<Object>> data) {
		ListMap<Integer, Date> dateListMap = new ListMap<Integer, Date>();
		for (List<Object> row : data) {
			dateListMap.putInList((Integer) row.get(0), (Date) row.get(2));
		}

		return dateListMap;
	}
}
