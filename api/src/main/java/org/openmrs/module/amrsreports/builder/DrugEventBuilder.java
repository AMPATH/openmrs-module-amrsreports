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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.cache.MohCacheUtils;
import org.openmrs.module.drughistory.DrugEventTrigger;
import org.openmrs.module.drughistory.DrugEventType;
import org.openmrs.module.drughistory.api.DrugEventService;
import org.openmrs.module.drughistory.api.DrugSnapshotService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DrugEventBuilder {

	protected final Log log = LogFactory.getLog(this.getClass());

	public static final String DRUG_ABACAVIR = "ABACAVIR";
	public static final String DRUG_ATAZANAVIR = "ATAZANAVIR";
	public static final String DRUG_DARUNAVIR = "DARUNAVIR";
	public static final String DRUG_DIDANOSINE = "DIDANOSINE";
	public static final String DRUG_EFAVIRENZ = "EFAVIRENZ";
	public static final String DRUG_EMTRICITABINE = "EMTRICITABINE";
	public static final String DRUG_ETRAVIRINE = "ETRAVIRINE";
	public static final String DRUG_INDINAVIR = "INDINAVIR";
	public static final String DRUG_LAMIVUDINE = "LAMIVUDINE";
	public static final String DRUG_LOPINAVIR = "LOPINAVIR AND RITONAVIR";
	public static final String DRUG_NELFINAVIR = "NELFINAVIR";
	public static final String DRUG_NEVIRAPINE = "NEVIRAPINE";
	public static final String DRUG_RALTEGRAVIR = "RALTEGRAVIR";
	public static final String DRUG_RITONAVIR = "RITONAVIR";
	public static final String DRUG_STAVUDINE = "STAVUDINE";
	public static final String DRUG_TENOFOVIR = "TENOFOVIR";
	public static final String DRUG_ZIDOVUDINE = "ZIDOVUDINE";
	public static final String DRUG_UNKNOWN = "UNKNOWN";
	public static final String DRUG_OTHER = "OTHER NON-CODED";

	public static Set<Concept> ARV_DRUGS, QUESTIONS_START, QUESTIONS_CONTINUE, QUESTIONS_STOP;

	public static Map<String, Set<Concept>> drugAnswers;

	private DrugEventService drugEventService;
	private ConceptService conceptService;

	private static DrugEventBuilder instance;
	private DrugSnapshotService drugSnapshotService;

	static {

		ARV_DRUGS = new HashSet<Concept>(Arrays.asList(
				MohCacheUtils.getConcept(DRUG_ABACAVIR),
				MohCacheUtils.getConcept(DRUG_ATAZANAVIR),
				MohCacheUtils.getConcept(DRUG_DARUNAVIR),
				MohCacheUtils.getConcept(DRUG_DIDANOSINE),
				MohCacheUtils.getConcept(DRUG_EFAVIRENZ),
				MohCacheUtils.getConcept(DRUG_EMTRICITABINE),
				MohCacheUtils.getConcept(DRUG_ETRAVIRINE),
				MohCacheUtils.getConcept(DRUG_INDINAVIR),
				MohCacheUtils.getConcept(DRUG_LAMIVUDINE),
				MohCacheUtils.getConcept(DRUG_LOPINAVIR),
				MohCacheUtils.getConcept(DRUG_NELFINAVIR),
				MohCacheUtils.getConcept(DRUG_NEVIRAPINE),
				MohCacheUtils.getConcept(DRUG_RALTEGRAVIR),
				MohCacheUtils.getConcept(DRUG_RITONAVIR),
				MohCacheUtils.getConcept(DRUG_STAVUDINE),
				MohCacheUtils.getConcept(DRUG_TENOFOVIR),
				MohCacheUtils.getConcept(DRUG_ZIDOVUDINE),
				MohCacheUtils.getConcept(DRUG_UNKNOWN),
				MohCacheUtils.getConcept(DRUG_OTHER)
		));

		QUESTIONS_START = new HashSet<Concept>(Arrays.asList(
				MohCacheUtils.getConcept(1250),
				MohCacheUtils.getConcept(1895)
		));

		QUESTIONS_CONTINUE = new HashSet<Concept>(Arrays.asList(
				MohCacheUtils.getConcept(966),
				MohCacheUtils.getConcept(1088),
				MohCacheUtils.getConcept(2154)
		));

		QUESTIONS_STOP = new HashSet<Concept>(Arrays.asList(
				MohCacheUtils.getConcept(1086),
				MohCacheUtils.getConcept(1087),
				MohCacheUtils.getConcept(2157)
		));

		drugAnswers = new HashMap<String, Set<Concept>>();
		drugAnswers.put(DRUG_ABACAVIR, MohCacheUtils.getConcepts(814, 817, 6679));
		drugAnswers.put(DRUG_ATAZANAVIR, MohCacheUtils.getConcepts(6159, 6160));
		drugAnswers.put(DRUG_DARUNAVIR, MohCacheUtils.getConcepts(6157));
		drugAnswers.put(DRUG_DIDANOSINE, MohCacheUtils.getConcepts(796));
		drugAnswers.put(DRUG_EFAVIRENZ, MohCacheUtils.getConcepts(633, 6964));
		drugAnswers.put(DRUG_EMTRICITABINE, MohCacheUtils.getConcepts(791, 6180));
		drugAnswers.put(DRUG_ETRAVIRINE, MohCacheUtils.getConcepts(6158));
		drugAnswers.put(DRUG_INDINAVIR, MohCacheUtils.getConcepts(749));
		drugAnswers.put(DRUG_LAMIVUDINE, MohCacheUtils.getConcepts(628, 630, 792, 817, 1400, 6467, 6679, 6964, 6965));
		drugAnswers.put(DRUG_LOPINAVIR, MohCacheUtils.getConcepts(794));
		drugAnswers.put(DRUG_NELFINAVIR, MohCacheUtils.getConcepts(635));
		drugAnswers.put(DRUG_NEVIRAPINE, MohCacheUtils.getConcepts(631, 792, 6467));
		drugAnswers.put(DRUG_RALTEGRAVIR, MohCacheUtils.getConcepts(6156));
		drugAnswers.put(DRUG_RITONAVIR, MohCacheUtils.getConcepts(794, 795, 6160));
		drugAnswers.put(DRUG_STAVUDINE, MohCacheUtils.getConcepts(625, 792, 6965));
		drugAnswers.put(DRUG_TENOFOVIR, MohCacheUtils.getConcepts(802, 1400, 6180, 6964));
		drugAnswers.put(DRUG_ZIDOVUDINE, MohCacheUtils.getConcepts(630, 797, 817, 6467));
		drugAnswers.put(DRUG_UNKNOWN, MohCacheUtils.getConcepts(5811));
		drugAnswers.put(DRUG_OTHER, MohCacheUtils.getConcepts(5424));
	}

	public static DrugEventBuilder getInstance() {
		if (instance == null)
			instance = new DrugEventBuilder();
		return instance;
	}

	/**
	 * generates DrugEvents for all patients based on ARV criteria
	 *
	 * @should find occurrences of ABACAVIR
	 * @should find occurrences of ATAZANAVIR
	 * @should find occurrences of DARUNAVIR
	 * @should find occurrences of DIDANOSINE
	 * @should find occurrences of EFAVIRENZ
	 * @should find occurrences of EMTRICITABINE
	 * @should find occurrences of ETRAVIRINE
	 * @should find occurrences of INDINAVIR
	 * @should find occurrences of LAMIVUDINE
	 * @should find occurrences of LOPINAVIR
	 * @should find occurrences of NELFINAVIR
	 * @should find occurrences of NEVIRAPINE
	 * @should find occurrences of RALTEGRAVIR
	 * @should find occurrences of RITONAVIR
	 * @should find occurrences of STAVUDINE
	 * @should find occurrences of TENOFOVIR
	 * @should find occurrences of ZIDOVUDINE
	 * @should find occurrences of UNKNOWN
	 * @should find occurrences of OTHER
	 */
	public void execute() {
		// update all of the ARVs
		for (String drug : drugAnswers.keySet()) {
			updateARVs(drug);
		}
	}

	/**
	 * generates drug events based on the given drug
	 */
	private void updateARVs(String drug) {

		log.info("Generating DrugEvents for " + drug);

		// do nothing if we have no drug to work with
		if (StringUtils.isBlank(drug))
			return;

		// set up the trigger
		DrugEventTrigger t = new DrugEventTrigger();
		t.setEventConcept(MohCacheUtils.getConcept(drug));
		t.setAnswers(drugAnswers.get(drug));

		// run query for start questions
		t.setQuestions(QUESTIONS_START);
		t.setEventType(DrugEventType.START);
		getDrugEventService().generateDrugEventsFromTrigger(t, null);

		// run query for continue questions
		t.setQuestions(QUESTIONS_CONTINUE);
		t.setEventType(DrugEventType.CONTINUE);
		getDrugEventService().generateDrugEventsFromTrigger(t, null);

		// run query for stop questions
		t.setQuestions(QUESTIONS_STOP);
		t.setEventType(DrugEventType.STOP);
		getDrugEventService().generateDrugEventsFromTrigger(t, null);

		// clean stuff up
		Context.flushSession();
	}

	public DrugEventService getDrugEventService() {
		if (drugEventService == null)
			drugEventService = Context.getService(DrugEventService.class);
		return drugEventService;
	}

	public ConceptService getConceptService() {
		if (conceptService == null)
			conceptService = Context.getConceptService();
		return conceptService;
	}

	public DrugSnapshotService getDrugSnapshotService() {
		if (drugSnapshotService == null)
			drugSnapshotService = Context.getService(DrugSnapshotService.class);
		return drugSnapshotService;
	}
}
