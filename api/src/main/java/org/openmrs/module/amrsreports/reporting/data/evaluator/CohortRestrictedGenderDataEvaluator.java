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
import org.openmrs.module.amrsreports.reporting.data.CohortRestrictedGenderDataDefinition;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Handler(supports = CohortRestrictedGenderDataDefinition.class, order = 50)
public class CohortRestrictedGenderDataEvaluator implements PersonDataEvaluator {

	/**
	 * @should return all birth dates for all persons
	 * @see org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator#evaluate(org.openmrs.module.reporting.data.person.definition.PersonDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
		EvaluatedPersonData c = new EvaluatedPersonData(definition, context);

		if (context != null && context.getBaseCohort() != null && !context.getBaseCohort().isEmpty()) {
			DataSetQueryService qs = Context.getService(DataSetQueryService.class);

			String hql = "select personId, gender" +
					" from Person" +
					" where voided = false " +
					" and personId in (" +
					" select elements(c.memberIds) from Cohort as c" +
					"	where c.uuid = :cohortUuid" +
					" )";

			Map<String, Object> m = new HashMap<String, Object>();
			m.put("cohortUuid", AmrsReportsConstants.SAVED_COHORT_UUID);

			List<Object> queryResult = qs.executeHqlQuery(hql, m);
			for (Object o : queryResult) {
				Object[] parts = (Object[]) o;
				if (parts.length == 2) {
					Integer pId = (Integer) parts[0];
					String gender = (String) parts[1];
					c.addData(pId, gender);
				}
			}
		}

		return c;
	}
}
