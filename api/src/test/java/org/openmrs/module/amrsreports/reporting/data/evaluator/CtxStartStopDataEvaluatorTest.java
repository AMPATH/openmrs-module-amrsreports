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

import org.junit.Test;
import org.openmrs.module.amrsreports.MohTestUtils;
import org.openmrs.module.amrsreports.rule.MohEvaluableNameConstants;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class CtxStartStopDataEvaluatorTest extends DrugDataEvaluatorTestHelper {

	@Override
	protected String getDataSetName() {
		return "datasets/concepts-ctx.xml";
	}

	/**
	 * @verifies start on PCP_PROPHYLAXIS_STARTED with not null answer
	 * @see CtxStartStopDataEvaluator#evaluate(org.openmrs.module.reporting.data.person.definition.PersonDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	@Test
	public void evaluate_shouldStartOnPCP_PROPHYLAXIS_STARTEDWithNotNullAnswer() throws Exception {
		MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.PCP_PROPHYLAXIS_STARTED,
				MohEvaluableNameConstants.NONE, "16 Oct 1975");
		assertEvaluatesTo("16/10/1975 - ");
	}

	/**
	 * @verifies not start on PCP_PROPHYLAXIS_STARTED with null answer
	 * @see CtxStartStopDataEvaluator#evaluate(org.openmrs.module.reporting.data.person.definition.PersonDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	@Test
	public void evaluate_shouldNotStartOnPCP_PROPHYLAXIS_STARTEDWithNullAnswer() throws Exception {
		MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.PCP_PROPHYLAXIS_STARTED,
				null, "17 Oct 1975");
		assertEvaluatesTo("");
	}

	/**
	 * @verifies stop on REASON_PCP_PROPHYLAXIS_STOPPED with not null answer
	 * @see CtxStartStopDataEvaluator#evaluate(org.openmrs.module.reporting.data.person.definition.PersonDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	@Test
	public void evaluate_shouldStopOnREASON_PCP_PROPHYLAXIS_STOPPEDWithNotNullAnswer() throws Exception {
		MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.REASON_PCP_PROPHYLAXIS_STOPPED,
				MohEvaluableNameConstants.NONE, "18 Oct 1975");
		assertEvaluatesTo("Unknown - 18/10/1975");
	}

	/**
	 * @verifies not stop on REASON_PCP_PROPHYLAXIS_STOPPED with null answer
	 * @see CtxStartStopDataEvaluator#evaluate(org.openmrs.module.reporting.data.person.definition.PersonDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	@Test
	public void evaluate_shouldNotStopOnREASON_PCP_PROPHYLAXIS_STOPPEDWithNullAnswer() throws Exception {
		MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.REASON_PCP_PROPHYLAXIS_STOPPED,
				null, "19 Oct 1975");
		assertEvaluatesTo("");
	}

	/**
	 * @verifies start on CURRENT_MEDICATIONS equal to TRIMETHOPRIM_AND_SULFAMETHOXAZOLE
	 * @see CtxStartStopDataEvaluator#evaluate(org.openmrs.module.reporting.data.person.definition.PersonDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	@Test
	public void evaluate_shouldStartOnCURRENT_MEDICATIONSEqualToTRIMETHOPRIM_AND_SULFAMETHOXAZOLE() throws Exception {
		MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.CURRENT_MEDICATIONS,
				MohEvaluableNameConstants.TRIMETHOPRIM_AND_SULFAMETHOXAZOLE, "20 Oct 1975");
		assertEvaluatesTo("20/10/1975 - ");
	}

	/**
	 * @verifies not start on CURRENT_MEDICATIONS equal to something other than TRIMETHOPRIM_AND_SULFAMETHOXAZOLE
	 * @see CtxStartStopDataEvaluator#evaluate(org.openmrs.module.reporting.data.person.definition.PersonDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	@Test
	public void evaluate_shouldNotStartOnCURRENT_MEDICATIONSEqualToSomethingOtherThanTRIMETHOPRIM_AND_SULFAMETHOXAZOLE() throws Exception {
		MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.CURRENT_MEDICATIONS,
				MohEvaluableNameConstants.NONE, "21 Oct 1975");
		assertEvaluatesTo("");
	}

	/**
	 * @verifies start on PATIENT_REPORTED_CURRENT_PCP_PROPHYLAXIS equal to TRIMETHOPRIM_AND_SULFAMETHOXAZOLE
	 * @see CtxStartStopDataEvaluator#evaluate(org.openmrs.module.reporting.data.person.definition.PersonDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	@Test
	public void evaluate_shouldStartOnPATIENT_REPORTED_CURRENT_PCP_PROPHYLAXISEqualToTRIMETHOPRIM_AND_SULFAMETHOXAZOLE() throws Exception {
		MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.PATIENT_REPORTED_CURRENT_PCP_PROPHYLAXIS,
				MohEvaluableNameConstants.TRIMETHOPRIM_AND_SULFAMETHOXAZOLE, "22 Oct 1975");
		assertEvaluatesTo("22/10/1975 - ");
	}

	/**
	 * @verifies not start on PATIENT_REPORTED_CURRENT_PCP_PROPHYLAXIS equal to something other than TRIMETHOPRIM_AND_SULFAMETHOXAZOLE
	 * @see CtxStartStopDataEvaluator#evaluate(org.openmrs.module.reporting.data.person.definition.PersonDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	@Test
	public void evaluate_shouldNotStartOnPATIENT_REPORTED_CURRENT_PCP_PROPHYLAXISEqualToSomethingOtherThanTRIMETHOPRIM_AND_SULFAMETHOXAZOLE() throws Exception {
		MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.PATIENT_REPORTED_CURRENT_PCP_PROPHYLAXIS,
				MohEvaluableNameConstants.NONE, "23 Oct 1975");
		assertEvaluatesTo("");
	}

}
