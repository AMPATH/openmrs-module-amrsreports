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
import org.openmrs.module.amrsreports.reporting.data.ARTSerialNumberDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.ARTTransferStatusDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.DateARTStartedDataDefinition;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.util.OpenmrsUtil;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Evaluates an ObsForPersonDataDefinition to produce a PersonData
 */
@Handler(supports = ARTSerialNumberDataDefinition.class, order = 50)
public class ARTSerialNumberDataEvaluator implements PersonDataEvaluator {

	private String currentMonth = null;
	private Integer currentSerial = 1;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");

	/**
	 * @see org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator#evaluate(org.openmrs.module.reporting.data.person.definition.PersonDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 * @should give out serial numbers in ascending order by date
	 * @should reset serial number upon switching to a new year and month combination
	 * @should place transfer ins at the bottom of each month
	 */
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {

		ARTSerialNumberDataDefinition def = (ARTSerialNumberDataDefinition) definition;
		EvaluatedPersonData c = new EvaluatedPersonData(def, context);

		if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
			return c;
		}

		PersonDataService pds = Context.getService(PersonDataService.class);

		// get ART start dates
		DateARTStartedDataDefinition artStartDateDefinition = new DateARTStartedDataDefinition();
		EvaluatedPersonData artStartDatesData = pds.evaluate(artStartDateDefinition, context);
		Map<Integer, Object> artStartDates = artStartDatesData.getData();

		// get Transfer status
		ARTTransferStatusDataDefinition artTransferDefinition = new ARTTransferStatusDataDefinition();
		EvaluatedPersonData artTransfersData = pds.evaluate(artTransferDefinition, context);
		Map<Integer, Object> artTransfers = artTransfersData.getData();

		// sort the people by ART start date
		Map<Integer, Object> sortedStartDates = sortByValue(artStartDates);

		for (Integer pId : sortedStartDates.keySet()) {
			// check to see if this person is a transfer
			if (artTransfers.containsKey(pId) && (Boolean) artTransfers.get(pId)) {
				c.addData(pId, AmrsReportsConstants.TRANSFER_IN);
			} else {
				c.addData(pId, nextSerial((Date) sortedStartDates.get(pId)));
			}
		}

		return c;
	}

	private Map sortByValue(Map map) {
		List list = new LinkedList(map.entrySet());
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue())
						.compareTo(((Map.Entry) (o2)).getValue());
			}
		});

		Map result = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext(); ) {
			Map.Entry entry = (Map.Entry) it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	private String nextSerial(Date date) {
		String thisMonth = sdf.format(date);
		if (!OpenmrsUtil.nullSafeEquals(thisMonth, currentMonth)) {
			currentMonth = thisMonth;
			currentSerial = 0;
		}
		return Integer.toString(++currentSerial);
	}

}
