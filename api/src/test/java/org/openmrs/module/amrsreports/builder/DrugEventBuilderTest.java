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

package org.openmrs.module.amrsreports.builder;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.MohTestUtils;
import org.openmrs.module.amrsreports.cache.MohCacheUtils;
import org.openmrs.module.drughistory.DrugEvent;
import org.openmrs.module.drughistory.DrugEventType;
import org.openmrs.module.drughistory.api.DrugEventService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsUtil;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class DrugEventBuilderTest extends BaseModuleContextSensitiveTest {

	private Patient patient;

	@Before
	public void setUp() throws Exception {
		executeDataSet("datasets/concepts-drugeventbuilder.xml");
		patient = MohTestUtils.createTestPatient();
	}

	/**
	 * @verifies find occurrences of ABACAVIR
	 * @see DrugEventBuilder#execute()
	 */
	@Test
	public void execute_shouldFindOccurrencesOfABACAVIR() throws Exception {
		runAgainstAllQuestions(DrugEventBuilder.DRUG_ABACAVIR);
	}

	/**
	 * @verifies find occurrences of ATAZANAVIR
	 * @see DrugEventBuilder#execute()
	 */
	@Test
	public void execute_shouldFindOccurrencesOfATAZANAVIR() throws Exception {
		runAgainstAllQuestions(DrugEventBuilder.DRUG_ATAZANAVIR);
	}

	/**
	 * @verifies find occurrences of DARUNAVIR
	 * @see DrugEventBuilder#execute()
	 */
	@Test
	public void execute_shouldFindOccurrencesOfDARUNAVIR() throws Exception {
		runAgainstAllQuestions(DrugEventBuilder.DRUG_DARUNAVIR);
	}

	/**
	 * @verifies find occurrences of DIDANOSINE
	 * @see DrugEventBuilder#execute()
	 */
	@Test
	public void execute_shouldFindOccurrencesOfDIDANOSINE() throws Exception {
		runAgainstAllQuestions(DrugEventBuilder.DRUG_DIDANOSINE);
	}

	/**
	 * @verifies find occurrences of EFAVIRENZ
	 * @see DrugEventBuilder#execute()
	 */
	@Test
	public void execute_shouldFindOccurrencesOfEFAVIRENZ() throws Exception {
		runAgainstAllQuestions(DrugEventBuilder.DRUG_EFAVIRENZ);
	}

	/**
	 * @verifies find occurrences of EMTRICITABINE
	 * @see DrugEventBuilder#execute()
	 */
	@Test
	public void execute_shouldFindOccurrencesOfEMTRICITABINE() throws Exception {
		runAgainstAllQuestions(DrugEventBuilder.DRUG_EMTRICITABINE);
	}

	/**
	 * @verifies find occurrences of ETRAVIRINE
	 * @see DrugEventBuilder#execute()
	 */
	@Test
	public void execute_shouldFindOccurrencesOfETRAVIRINE() throws Exception {
		runAgainstAllQuestions(DrugEventBuilder.DRUG_ETRAVIRINE);
	}

	/**
	 * @verifies find occurrences of INDINAVIR
	 * @see DrugEventBuilder#execute()
	 */
	@Test
	public void execute_shouldFindOccurrencesOfINDINAVIR() throws Exception {
		runAgainstAllQuestions(DrugEventBuilder.DRUG_INDINAVIR);
	}

	/**
	 * @verifies find occurrences of LAMIVUDINE
	 * @see DrugEventBuilder#execute()
	 */
	@Test
	public void execute_shouldFindOccurrencesOfLAMIVUDINE() throws Exception {
		runAgainstAllQuestions(DrugEventBuilder.DRUG_LAMIVUDINE);
	}

	/**
	 * @verifies find occurrences of LOPINAVIR
	 * @see DrugEventBuilder#execute()
	 */
	@Test
	public void execute_shouldFindOccurrencesOfLOPINAVIR() throws Exception {
		runAgainstAllQuestions(DrugEventBuilder.DRUG_LOPINAVIR);
	}

	/**
	 * @verifies find occurrences of NELFINAVIR
	 * @see DrugEventBuilder#execute()
	 */
	@Test
	public void execute_shouldFindOccurrencesOfNELFINAVIR() throws Exception {
		runAgainstAllQuestions(DrugEventBuilder.DRUG_NELFINAVIR);
	}

	/**
	 * @verifies find occurrences of NEVIRAPINE
	 * @see DrugEventBuilder#execute()
	 */
	@Test
	public void execute_shouldFindOccurrencesOfNEVIRAPINE() throws Exception {
		runAgainstAllQuestions(DrugEventBuilder.DRUG_NEVIRAPINE);
	}

	/**
	 * @verifies find occurrences of RALTEGRAVIR
	 * @see DrugEventBuilder#execute()
	 */
	@Test
	public void execute_shouldFindOccurrencesOfRALTEGRAVIR() throws Exception {
		runAgainstAllQuestions(DrugEventBuilder.DRUG_RALTEGRAVIR);
	}

	/**
	 * @verifies find occurrences of RITONAVIR
	 * @see DrugEventBuilder#execute()
	 */
	@Test
	public void execute_shouldFindOccurrencesOfRITONAVIR() throws Exception {
		runAgainstAllQuestions(DrugEventBuilder.DRUG_RITONAVIR);
	}

	/**
	 * @verifies find occurrences of STAVUDINE
	 * @see DrugEventBuilder#execute()
	 */
	@Test
	public void execute_shouldFindOccurrencesOfSTAVUDINE() throws Exception {
		runAgainstAllQuestions(DrugEventBuilder.DRUG_STAVUDINE);
	}

	/**
	 * @verifies find occurrences of TENOFOVIR
	 * @see DrugEventBuilder#execute()
	 */
	@Test
	public void execute_shouldFindOccurrencesOfTENOFOVIR() throws Exception {
		runAgainstAllQuestions(DrugEventBuilder.DRUG_TENOFOVIR);
	}

	/**
	 * @verifies find occurrences of ZIDOVUDINE
	 * @see DrugEventBuilder#execute()
	 */
	@Test
	public void execute_shouldFindOccurrencesOfZIDOVUDINE() throws Exception {
		runAgainstAllQuestions(DrugEventBuilder.DRUG_ZIDOVUDINE);
	}

	/**
	 * @verifies find occurrences of UNKNOWN
	 * @see DrugEventBuilder#execute()
	 */
	@Test
	public void execute_shouldFindOccurrencesOfUNKNOWN() throws Exception {
		runAgainstAllQuestions(DrugEventBuilder.DRUG_UNKNOWN);
	}

	/**
	 * @verifies find occurrences of OTHER
	 * @see DrugEventBuilder#execute()
	 */
	@Test
	public void execute_shouldFindOccurrencesOfOTHER() throws Exception {
		runAgainstAllQuestions(DrugEventBuilder.DRUG_OTHER);
	}

	private void runAgainstAllQuestions(String c) throws Exception {
		Concept drugConcept = MohCacheUtils.getConcept(c);

		for (Concept answer : DrugEventBuilder.getInstance().drugAnswers.get(c)) {
			for (Concept question : DrugEventBuilder.getInstance().QUESTIONS_START) {
				if (!runSpecificScenario(question, answer, drugConcept, DrugEventType.START))
					fail("failed on q: " + question + ", a: " + answer + ", type: START");
			}
			for (Concept question : DrugEventBuilder.getInstance().QUESTIONS_CONTINUE) {
				if (!runSpecificScenario(question, answer, drugConcept, DrugEventType.CONTINUE))
					fail("failed on q: " + question + ", a: " + answer + ", type: CONTINUE");
			}
			for (Concept question : DrugEventBuilder.getInstance().QUESTIONS_STOP) {
				if (!runSpecificScenario(question, answer, drugConcept, DrugEventType.STOP))
					fail("failed on q: " + question + ", a: " + answer + ", type: STOP");
			}
		}
	}

	private boolean runSpecificScenario(Concept question, Concept answer, Concept drugConcept, DrugEventType drugEventType) throws Exception {
		// add the observation
		MohTestUtils.addCodedObs(patient, question, answer, "16 Oct 1975");

		// run the builder
		DrugEventBuilder.getInstance().execute();

		// get the results
		List<DrugEvent> actual = Context.getService(DrugEventService.class).getAllDrugEvents(null);

		// check for accuracy
		assertFalse(actual.isEmpty());

		boolean found = false;
		for (DrugEvent de : actual) {
			if (OpenmrsUtil.nullSafeEquals(de.getConcept(), drugConcept) &&
					de.getEventType() == drugEventType &&
					OpenmrsUtil.nullSafeEquals(de.getPerson(), patient) &&
					de.getDateOccurred().compareTo(MohTestUtils.makeDate("16 Oct 1975")) == 0)
				found = true;
		}

		// clear things out for the next run
		Context.getService(DrugEventService.class).purgeAllDrugEvents();
		Context.flushSession();
		Context.clearSession();

		return found;
	}
}
