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
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.cache.MohCacheUtils;
import org.openmrs.module.drughistory.DrugEventTrigger;
import org.openmrs.module.drughistory.DrugEventType;
import org.openmrs.module.drughistory.api.DrugEventService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DrugEventBuilder {

	public static final String DRUG_ABACAVIR = "ABACAVIR";
	public static final String DRUG_ATAZANAVIR = "ATAZANAVIR";
	public static final String DRUG_DARUNAVIR = "DARUNAVIR";
	public static final String DRUG_DIDANOSINE = "DIDANOSINE";
	public static final String DRUG_EFAVIRENZ = "EFAVIRENZ";
	public static final String DRUG_EMTRICITABINE = "EMTRICITABINE";
	public static final String DRUG_ETRAVIRINE = "ETRAVIRINE";
	public static final String DRUG_INDINAVIR = "INDINAVIR";
	public static final String DRUG_LAMIVUDINE = "LAMIVUDINE";
	// TODO find a single concept for Lopinavir ... LOPINAVIR AND RITONAVIR perhaps
	// public static final String DRUG_LOPINAVIR = "LOPINAVIR";
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

	public static DrugEventBuilder getInstance() {
		if (instance == null)
			instance = new DrugEventBuilder();
		return instance;
	}

	private DrugEventBuilder() {

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
//				MohCacheUtils.getConcept(DRUG_LOPINAVIR),
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
				getConcept(1250),
				getConcept(1895)
		));

		QUESTIONS_CONTINUE = new HashSet<Concept>(Arrays.asList(
				getConcept(966),
				getConcept(1088),
				getConcept(2154)
		));

		QUESTIONS_STOP = new HashSet<Concept>(Arrays.asList(
				getConcept(1086),
				getConcept(1087),
				getConcept(2157)
		));

		drugAnswers = new HashMap<String, Set<Concept>>();
		drugAnswers.put(DRUG_ABACAVIR, getConcepts(814, 817, 6679));
		drugAnswers.put(DRUG_ATAZANAVIR, getConcepts(6159, 6160));
		drugAnswers.put(DRUG_DARUNAVIR, getConcepts(6157));
		drugAnswers.put(DRUG_DIDANOSINE, getConcepts(796));
		drugAnswers.put(DRUG_EFAVIRENZ, getConcepts(633, 6964));
		drugAnswers.put(DRUG_EMTRICITABINE, getConcepts(791, 6180));
		drugAnswers.put(DRUG_ETRAVIRINE, getConcepts(6158));
		drugAnswers.put(DRUG_INDINAVIR, getConcepts(749));
		drugAnswers.put(DRUG_LAMIVUDINE, getConcepts(628, 630, 792, 817, 1400, 6467, 6679, 6964, 6965));
//		drugAnswers.put(DRUG_LOPINAVIR, getConcepts(794));
		drugAnswers.put(DRUG_NELFINAVIR, getConcepts(635));
		drugAnswers.put(DRUG_NEVIRAPINE, getConcepts(631, 792, 6467));
		drugAnswers.put(DRUG_RALTEGRAVIR, getConcepts(6156));
		drugAnswers.put(DRUG_RITONAVIR, getConcepts(794, 795, 6160));
		drugAnswers.put(DRUG_STAVUDINE, getConcepts(625, 792, 6965));
		drugAnswers.put(DRUG_TENOFOVIR, getConcepts(802, 1400, 6180, 6964));
		drugAnswers.put(DRUG_ZIDOVUDINE, getConcepts(630, 797, 817, 6467));
		drugAnswers.put(DRUG_UNKNOWN, getConcepts(5811));
		drugAnswers.put(DRUG_OTHER, getConcepts(5424));
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

		// tell the drug event service to regenerate snapshots
		// getDrugEventService().regenerateSnapshots();

		// find first ART dates by looking for regimens ... ?
		// TODO
	}

	/**
	 * generates drug events based on the given drug
	 */
	private void updateARVs(String drug) {

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

	private Concept getConcept(int conceptId) {
		return getConceptService().getConcept(conceptId);
	}

	private Set<Concept> getConcepts(Integer... conceptIds) {
		Set<Concept> c = new HashSet<Concept>();
		for (Integer conceptId : conceptIds) {
			c.add(getConcept(conceptId));
		}
		return c;
	}
}
