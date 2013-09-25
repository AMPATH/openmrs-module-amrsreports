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

import org.openmrs.PersonAttribute;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.AmrsReportsConstants;
import org.openmrs.module.amrsreports.reporting.data.CohortRestrictedPersonAttributeDataDefinition;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Evaluates a PersonAttributeDataDefinition to produce a PatientData
 */
@Handler(supports = CohortRestrictedPersonAttributeDataDefinition.class, order = 50)
public class CohortRestrictedPersonAttributeDataEvaluator implements PersonDataEvaluator {

	/**
	 * @should return the person attribute of the passed type for each person in the passed context
	 * @see org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator#evaluate(org.openmrs.module.reporting.data.person.definition.PersonDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {

		CohortRestrictedPersonAttributeDataDefinition def = (CohortRestrictedPersonAttributeDataDefinition) definition;
		EvaluatedPersonData c = new EvaluatedPersonData(def, context);

		if ((context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) || def.getPersonAttributeType() == null) {
			return c;
		}

		DataSetQueryService qs = Context.getService(DataSetQueryService.class);

		Map<String, Object> m = new HashMap<String, Object>();
		StringBuilder hql = new StringBuilder();

		hql.append("select 		pa.person.personId, pa ");
		hql.append("from 		PersonAttribute as pa ");
		hql.append("where 		voided = false ");

		hql.append("and 		person.personId in (" +
				"	select elements(c.memberIds) from Cohort as c" +
				"	where c.uuid = :cohortUuid" +
				") ");
		m.put("cohortUuid", AmrsReportsConstants.SAVED_COHORT_UUID);

		hql.append("and 		attributeType.personAttributeTypeId = :idType ");
		m.put("idType", def.getPersonAttributeType().getPersonAttributeTypeId());

		List<Object> queryResult = qs.executeHqlQuery(hql.toString(), m);
		for (Object o : queryResult) {
			Object[] parts = (Object[]) o;
			if (parts.length == 2) {
				Integer pId = (Integer) parts[0];
				PersonAttribute pa = (PersonAttribute) parts[1];
				c.addData(pId, pa);
			}
		}
		return c;
	}
}
