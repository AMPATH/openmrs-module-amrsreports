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
import org.openmrs.module.amrsreports.reporting.data.CohortRestrictedAgeAtDateOfOtherDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.CohortRestrictedBirthdateDataDefinition;
import org.openmrs.module.reporting.data.DataUtil;
import org.openmrs.module.reporting.data.converter.BirthdateToAgeConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.Date;
import java.util.List;

@Handler(supports = CohortRestrictedAgeAtDateOfOtherDataDefinition.class, order = 50)
public class CohortRestrictedAgeAtDateOfOtherDataEvaluator implements PersonDataEvaluator {

	/**
	 * @should return all ages on the date of the given definition
	 * @see PersonDataEvaluator#evaluate(org.openmrs.module.reporting.data.person.definition.PersonDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {

		CohortRestrictedAgeAtDateOfOtherDataDefinition add = (CohortRestrictedAgeAtDateOfOtherDataDefinition) definition;

		EvaluatedPersonData birthdates = Context.getService(PersonDataService.class).evaluate(new CohortRestrictedBirthdateDataDefinition(), context);
		EvaluatedPersonData effectiveDates = Context.getService(PersonDataService.class).evaluate(add.getEffectiveDateDefinition(), context);

		List<DataConverter> converters = add.getEffectiveDateDefinition().getConverters();
		if (converters != null && converters.size() > 0) {
			for (Integer pId : effectiveDates.getData().keySet()) {
				Object convertedValue = DataUtil.convertData(effectiveDates.getData().get(pId), converters);
				effectiveDates.addData(pId, convertedValue);
			}
		}

		EvaluatedPersonData ret = new EvaluatedPersonData(definition, context);
		BirthdateToAgeConverter converter = new BirthdateToAgeConverter();
		for (Integer personId : birthdates.getData().keySet()) {
			Object birthdate = birthdates.getData().get(personId);
			converter.setEffectiveDate((Date) effectiveDates.getData().get(personId));
			ret.addData(personId, converter.convert(birthdate));
		}

		return ret;
	}
}
