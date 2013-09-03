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
import org.openmrs.annotation.Handler;
import org.openmrs.module.amrsreports.reporting.data.CtxStartStopDataDefinition;
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
@Handler(supports = CtxStartStopDataDefinition.class, order = 50)
public class CtxStartStopDataEvaluator extends DrugStartStopDataEvaluator {

	private Log log = LogFactory.getLog(getClass());

	/**
	 * @should start on PCP_PROPHYLAXIS_STARTED with not null answer
	 * @should not start on PCP_PROPHYLAXIS_STARTED with null answer
	 * @should stop on REASON_PCP_PROPHYLAXIS_STOPPED with not null answer
	 * @should not stop on REASON_PCP_PROPHYLAXIS_STOPPED with null answer
	 * @should start on CURRENT_MEDICATIONS equal to TRIMETHOPRIM_AND_SULFAMETHOXAZOLE
	 * @should not start on CURRENT_MEDICATIONS equal to something other than TRIMETHOPRIM_AND_SULFAMETHOXAZOLE
	 * @should start on PATIENT_REPORTED_CURRENT_PCP_PROPHYLAXIS equal to TRIMETHOPRIM_AND_SULFAMETHOXAZOLE
	 * @should not start on PATIENT_REPORTED_CURRENT_PCP_PROPHYLAXIS equal to something other than TRIMETHOPRIM_AND_SULFAMETHOXAZOLE
	 */
	@Override
	public EvaluatedPersonData evaluate(final PersonDataDefinition definition, final EvaluationContext context) throws EvaluationException {
		EvaluatedPersonData data = new EvaluatedPersonData(definition, context);

		if (context.getBaseCohort().isEmpty())
			return data;

		Map<String, Object> m = new HashMap<String, Object>();
		m.put("personIds", context.getBaseCohort());

		String sql = "select person_id, obs_datetime" +
				"  from obs" +
				"  where" +
				"    person_id in (:personIds)" +
				"    and (" +
				"      (concept_id = 1263 and value_coded is not null) " +
				"      or (concept_id in (1193, 1109) and value_coded = 916) " +
				"    )" +
				"    and voided = 0";

		ListMap<Integer, Date> mappedStartDates = makeDatesMapFromSQL(sql, m);

		sql = "select person_id, obs_datetime" +
				"  from obs" +
				"  where" +
				"    person_id in (:personIds)" +
				"    and (" +
				"      (concept_id in (1262, 1925) and value_coded is not null) " +
				"      or (concept_id = 1261 and value_coded = 1260) " +
				"    )" +
				"    and voided = 0";

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
