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
import org.openmrs.PersonAttributeType;
import org.openmrs.annotation.Handler;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.AmrsReportsConstants;
import org.openmrs.module.amrsreports.model.PatientTBTreatmentData;
import org.openmrs.module.amrsreports.reporting.data.TbTreatmentStartDateDataDefinition;
import org.openmrs.module.amrsreports.service.MohCoreService;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonAttributeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 */
@Handler(supports = TbTreatmentStartDateDataDefinition.class, order = 50)
public class TbTreatmentStartDateDataEvaluator implements PersonDataEvaluator {

	/**
	 * @param definition
	 * @param context
	 * @return
	 * @throws EvaluationException
	 * @should return value_datetime for TUBERCULOSIS DRUG TREATMENT START DATE
	 * @should return obs_datetime when drug TUBERCULOSIS TREATMENT PLAN is START DRUGS
	 */
	@Override
	public EvaluatedPersonData evaluate(final PersonDataDefinition definition, final EvaluationContext context) throws EvaluationException {
		EvaluatedPersonData data = new EvaluatedPersonData(definition, context);

		if (context.getBaseCohort().isEmpty())
			return data;

		Map<String, Object> m = new HashMap<String, Object>();
		m.put("personIds", context.getBaseCohort());

		String sql = "select person_id, " +
				" CASE " +
				"     WHEN (concept_id=1113) THEN value_datetime" +
				"     WHEN (concept_id=1268 and value_coded=1256)  THEN obs_datetime " +
				" END" +
				" start_date" +
				" from obs  " +
				" 	  where" +
				"		person_id in (:personIds)" +
				"		and concept_id in (1113,1268)" +
				"		and voided = 0";

		ListMap<Integer, Date> mappedStartDates = makeDatesMapFromSQL(sql, m);

        /*get tb registration number for patients*/
		String typeId = Context.getAdministrationService().getGlobalProperty(AmrsReportsConstants.TB_REGISTRATION_NO_ATTRIBUTE_TYPE);
		PersonAttributeType pat;

		try {
			pat = Context.getPersonService().getPersonAttributeType(Integer.valueOf(typeId));
		} catch (NumberFormatException e) {
			pat = Context.getPersonService().getPersonAttributeType(AmrsReportsConstants.TB_REGISTRATION_NO_ATTRIBUTE_TYPE_DEFAULT);
		}

		if (pat == null) {
			throw new APIException("Could not find TB Registration Number Person Attribute.");
		}

		PersonAttributeDataDefinition patientTBRegistrationDetails = new PersonAttributeDataDefinition(pat);

		EvaluatedPersonData tbRegData = Context.getService(PersonDataService.class).evaluate(patientTBRegistrationDetails, context);

		Map<Integer, Object> regDetails = null;
		Set<Date> startDates = null;

		if (tbRegData.getData() != null)
			regDetails = tbRegData.getData();

		for (Integer memberId : context.getBaseCohort().getMemberIds()) {

			PatientTBTreatmentData details = new PatientTBTreatmentData();

			if (!mappedStartDates.isEmpty()) {
				startDates = safeFind(mappedStartDates, memberId);
			}

			String tbRegistrationNo = null;
			if (regDetails != null) {
				Object regNoObj = regDetails.get(memberId);
				if (regNoObj != null) {
					PersonAttribute pa = (PersonAttribute) regNoObj;
					tbRegistrationNo = pa.getValue();
				}
			}
			details.setTbRegNO(tbRegistrationNo);

			if (startDates.size() > 0)
				details.setEvaluationDates(startDates);

            /*Add findings to the list*/

			data.addData(memberId, details);
		}

		return data;
	}

	protected Set<Date> safeFind(final ListMap<Integer, Date> map, final Integer key) {
		Set<Date> dateSet = new TreeSet<Date>();
		if (map.size() > 0 && map.containsKey(key))
			dateSet.addAll(map.get(key));
		return dateSet;
	}

	/**
	 * executes sql query and generates a ListMap<Integer, Date>
	 */
	protected ListMap<Integer, Date> makeDatesMapFromSQL(String sql, Map<String, Object> substitutions) {
		List<Object> data = Context.getService(MohCoreService.class).executeSqlQuery(sql, substitutions);
		return makeDatesMap(data);
	}

	/**
	 * generates a map of integers to lists of dates, assuming this is the kind of response expected from the SQL
	 */
	protected ListMap<Integer, Date> makeDatesMap(List<Object> data) {
		ListMap<Integer, Date> dateListMap = new ListMap<Integer, Date>();
		for (Object o : data) {
			Object[] parts = (Object[]) o;
			if (parts.length == 2) {
				Integer pId = (Integer) parts[0];
				Date date = (Date) parts[1];
				if (pId != null && date != null) {
					dateListMap.putInList(pId, date);
				}
			}
		}

		return dateListMap;
	}

}
