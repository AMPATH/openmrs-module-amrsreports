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
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.module.amrsreports.MohTestUtils;
import org.openmrs.module.amrsreports.reporting.data.CtxStartStopDataDefinition;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.PersonEvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public abstract class DrugDataEvaluatorTestHelper extends BaseModuleContextSensitiveTest {

	protected Patient patient;
	private Date evaluationDate;
	private PersonEvaluationContext evaluationContext;
	private CtxStartStopDataEvaluator evaluator;
	private CtxStartStopDataDefinition definition;

	@Before
	public void setUp() throws Exception {

		executeDataSet(getDataSetName());

		patient = MohTestUtils.createTestPatient();
		Cohort c = new Cohort(Collections.singleton(patient.getId()));

		evaluationDate = new Date();

		evaluationContext = new PersonEvaluationContext(evaluationDate);
		evaluationContext.setBaseCohort(c);

		definition = new CtxStartStopDataDefinition();
		evaluator = new CtxStartStopDataEvaluator();
	}

	protected abstract String getDataSetName();

	protected void assertEvaluatesTo(String expected) throws EvaluationException {
		EvaluatedPersonData actual = evaluator.evaluate(definition, evaluationContext);
		Map<Integer, Object> data = actual.getData();
		assertThat(data.size(), is(1));
		assertThat((String) data.get(patient.getId()), is(expected));
	}
}
