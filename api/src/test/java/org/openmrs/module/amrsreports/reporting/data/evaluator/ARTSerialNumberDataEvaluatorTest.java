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

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.AmrsReportsConstants;
import org.openmrs.module.amrsreports.reporting.data.ARTSerialNumberDataDefinition;
import org.openmrs.module.amrsreports.service.MOHFacilityService;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.evaluation.context.PersonEvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ARTSerialNumberDataEvaluatorTest extends BaseModuleContextSensitiveTest {

	private Date evaluationDate;
	private PersonEvaluationContext evaluationContext;
	private ARTSerialNumberDataEvaluator evaluator;
	private ARTSerialNumberDataDefinition definition;

	@Before
	public void setUp() throws Exception {

		executeDataSet("datasets/art-serial-number.xml");

		Cohort c = new Cohort("6,7,8,9,501,502");

		evaluationDate = new Date();

		evaluationContext = new PersonEvaluationContext(evaluationDate);
		evaluationContext.setBaseCohort(c);

		Map<String, Object> m = new HashMap<String, Object>();
		m.put("facility", Context.getService(MOHFacilityService.class).getFacility(1));

		evaluationContext.setParameterValues(m);

		definition = new ARTSerialNumberDataDefinition();
		evaluator = new ARTSerialNumberDataEvaluator();
	}

	/**
	 * @verifies give out serial numbers in ascending order by date
	 * @see ARTSerialNumberDataEvaluator#evaluate(org.openmrs.module.reporting.data.person.definition.PersonDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	@Test
	public void evaluate_shouldGiveOutSerialNumbersInAscendingOrderByDate() throws Exception {
		EvaluatedPersonData actual = evaluator.evaluate(definition, evaluationContext);
		Map<Integer, Object> data = actual.getData();
		assertNotNull(data);
		assertEquals("1", data.get(6).toString());
		assertEquals("2", data.get(7).toString());
	}

	/**
	 * @verifies reset serial number upon switching to a new year and month combination
	 * @see ARTSerialNumberDataEvaluator#evaluate(org.openmrs.module.reporting.data.person.definition.PersonDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	@Test
	public void evaluate_shouldResetSerialNumberUponSwitchingToANewYearAndMonthCombination() throws Exception {
		EvaluatedPersonData actual = evaluator.evaluate(definition, evaluationContext);
		Map<Integer, Object> data = actual.getData();
		assertNotNull(data);
		assertEquals("1", data.get(6).toString());
		assertEquals("2", data.get(7).toString());
		assertEquals("1", data.get(8).toString());
		assertEquals("1", data.get(9).toString());
	}

	/**
	 * @verifies place transfer ins at the bottom of each month
	 * @see ARTSerialNumberDataEvaluator#evaluate(org.openmrs.module.reporting.data.person.definition.PersonDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	@Test
	public void evaluate_shouldPlaceTransferInsAtTheBottomOfEachMonth() throws Exception {
		EvaluatedPersonData actual = evaluator.evaluate(definition, evaluationContext);
		Map<Integer, Object> data = actual.getData();
		assertNotNull(data);
		assertEquals("1", data.get(9).toString());
		assertEquals("2", data.get(502).toString());
		assertEquals(AmrsReportsConstants.TRANSFER_IN, data.get(501).toString());
	}
}
