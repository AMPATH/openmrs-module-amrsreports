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
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.module.amrsreports.model.RegimenChange;
import org.openmrs.module.amrsreports.reporting.data.RegimenHistoryDataDefinition;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.evaluation.context.PersonEvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class RegimenHistoryDataEvaluatorTest extends BaseModuleContextSensitiveTest {

	private Date evaluationDate;
	private PersonEvaluationContext evaluationContext;
	private RegimenHistoryDataEvaluator evaluator;
	private RegimenHistoryDataDefinition definition;

	@Before
	public void setUp() throws Exception {

		executeDataSet("datasets/concepts-drugeventbuilder.xml");
		executeDataSet("datasets/regimen-history.xml");

		Cohort c = new Cohort("1,2");

		evaluationDate = new Date();

		evaluationContext = new PersonEvaluationContext(evaluationDate);
		evaluationContext.setBaseCohort(c);

		definition = new RegimenHistoryDataDefinition();
		evaluator = new RegimenHistoryDataEvaluator();
	}

	/**
	 * @verifies find a regimen if it exists
	 * @see RegimenHistoryDataEvaluator#evaluate(org.openmrs.module.reporting.data.person.definition.PersonDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	@Test
	@Ignore
	public void evaluate_shouldFindARegimenIfItExists() throws Exception {
		EvaluatedPersonData actual = evaluator.evaluate(definition, evaluationContext);
		Map<Integer, Object> data = actual.getData();
		assertNotNull(data);

		List<RegimenChange> rcList = (List<RegimenChange>) data.get(2);
		assertNotNull(rcList);
		assertEquals(1, rcList.size());

		RegimenChange rc = rcList.get(0);
		assertEquals("Regimen A", rc.getRegimen().getName());
	}

	/**
	 * @verifies return nothing if DrugSnapshots do not indicate a Regimen
	 * @see RegimenHistoryDataEvaluator#evaluate(org.openmrs.module.reporting.data.person.definition.PersonDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	@Test
	@Ignore
	public void evaluate_shouldReturnNothingIfDrugSnapshotsDoNotIndicateARegimen() throws Exception {
		EvaluatedPersonData actual = evaluator.evaluate(definition, evaluationContext);
		Map<Integer, Object> data = actual.getData();
		assertNotNull(data);

		List<RegimenChange> rcList = (List<RegimenChange>) data.get(1);
		assertNull(rcList);
	}

	/**
	 * @verifies only return a reason from the same encounter as the DrugSnapshot used to indicate a Regimen
	 * @see RegimenHistoryDataEvaluator#evaluate(org.openmrs.module.reporting.data.person.definition.PersonDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	@Test
	@Ignore
	public void evaluate_shouldOnlyReturnAReasonFromTheSameEncounterAsTheDrugSnapshotUsedToIndicateARegimen() throws Exception {
		EvaluatedPersonData actual = evaluator.evaluate(definition, evaluationContext);
		Map<Integer, Object> data = actual.getData();
		assertNotNull(data);

		List<RegimenChange> rcList = (List<RegimenChange>) data.get(2);
		assertNotNull(rcList);
		assertEquals(1, rcList.size());

		RegimenChange rc = rcList.get(0);
		assertEquals("DRUG TOXICITY", rc.getReason());
	}
}
