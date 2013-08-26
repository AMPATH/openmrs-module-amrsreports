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
import org.openmrs.module.amrsreports.reporting.data.FluconazoleStartStopDataDefinition;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 */
@Handler(supports = FluconazoleStartStopDataDefinition.class, order = 50)
public class FluconazoleStartStopDataEvaluator extends DrugStartStopDataEvaluator {

	@Override
	public EvaluatedPersonData evaluate(final PersonDataDefinition definition, final EvaluationContext context) throws EvaluationException {
		EvaluatedPersonData data = new EvaluatedPersonData(definition, context);

		if (context.getBaseCohort().isEmpty())
			return data;

		Map<String, Object> m = new HashMap<String, Object>();
		m.put("personIds", context.getBaseCohort());

		String sql = "select person_id, obs_datetime" +
				" 	from obs" +
				" 	where" +
				"		person_id in (:personIds)" +
				"   	and (" +
				"     		(concept_id = 1277 and value_coded = 1256) " +
				"     		or (concept_id = 1278 and value_coded = 747) " +
				"   	)" +
				"		and voided = 0";

		ListMap<Integer, Date> mappedStartDates = makeDatesMapFromSQL(sql, m);

		sql = "select person_id, obs_datetime" +
				" 	from obs" +
				" 	where" +
				"		person_id in (:personIds)" +
				"   	and concept_id = 1277" +
				"   	and value_coded = 1260" +
				"		and voided = 0";

		ListMap<Integer, Date> mappedStopDates = makeDatesMapFromSQL(sql, m);

		for (Integer memberId : context.getBaseCohort().getMemberIds()) {
			Set<Date> stopDates = safeFind(mappedStopDates, memberId);
			Set<Date> startDates = safeFind(mappedStartDates, memberId);
			String rangeInformation = buildRangeInformation(startDates, stopDates, context.getEvaluationDate());
			data.addData(memberId, rangeInformation);
		}

		return data;
	}

}
