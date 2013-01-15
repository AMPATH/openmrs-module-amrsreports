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

package org.openmrs.module.amrsreport.rule.collection;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.result.Result;
import org.openmrs.module.amrsreport.cache.MohCacheUtils;
import org.openmrs.module.amrsreport.rule.MohEvaluableRule;
import org.openmrs.module.amrsreport.service.MohCoreService;
import org.openmrs.module.amrsreport.util.MohFetchOrdering;
import org.openmrs.module.amrsreport.util.MohFetchRestriction;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MohPregnancyPMTCReferralRule extends MohEvaluableRule {

	private static final Log log = LogFactory.getLog(MohPregnancyPMTCReferralRule.class);

	public static final String TOKEN = "MOH Pregnancy PMTC Referral";

	public static final String ESTIMATED_DATE_OF_CONFINEMENT = "ESTIMATED DATE OF CONFINEMENT";
	public static final String ESTIMATED_DATE_OF_CONFINEMENT_ULTRASOUND = "ESTIMATED DATE OF CONFINEMENT, ULTRASOUND";
	public static final String CURRENT_PREGNANT = "CURRENT PREGNANT";
	public static final String NO_OF_WEEK_OF_PREGNANCY = "NO OF WEEK OF PREGNANCY";
	public static final String FUNDAL_LENGTH = "FUNDAL LENGTH";
	public static final String PREGNANCY_URINE_TEST = "PREGNANCY URINE TEST";
	public static final String URGENT_MEDICAL_ISSUES = "URGENT MEDICAL ISSUES";
	public static final String PROBLEM_ADDED = "PROBLEM ADDED";
	public static final String FOETAL_MOVEMENT = "FOETAL MOVEMENT";
	public static final String REASON_FOR_CURRENT_VISIT = "REASON FOR CURRENT VISIT";
	public static final String REASON_FOR_NEXT_VISIT = "REASON FOR NEXT VISIT";
	public static final String YES = "YES";
	public static final String MONTH_OF_CURRENT_GESTATION = "MONTH OF CURRENT GESTATION";
	public static final String POSITIVE = "POSITIVE";
	public static final String PREGNANCY = "PREGNANCY";
	public static final String PREGNANCY_ECTOPIC = "PREGNANCY, ECTOPIC";
	public static final String ANTENATAL_CARE = "ANTENATAL CARE";

	private static final Collection<OpenmrsObject> questionConcepts = Arrays.<OpenmrsObject>asList(new Concept[]{
			MohCacheUtils.getConcept(ESTIMATED_DATE_OF_CONFINEMENT),
			MohCacheUtils.getConcept(ESTIMATED_DATE_OF_CONFINEMENT_ULTRASOUND),
			MohCacheUtils.getConcept(CURRENT_PREGNANT),
			MohCacheUtils.getConcept(NO_OF_WEEK_OF_PREGNANCY),
			MohCacheUtils.getConcept(MONTH_OF_CURRENT_GESTATION),
			MohCacheUtils.getConcept(FUNDAL_LENGTH),
			MohCacheUtils.getConcept(PREGNANCY_URINE_TEST),
			MohCacheUtils.getConcept(URGENT_MEDICAL_ISSUES),
			MohCacheUtils.getConcept(PROBLEM_ADDED),
			MohCacheUtils.getConcept(FOETAL_MOVEMENT),
			MohCacheUtils.getConcept(REASON_FOR_CURRENT_VISIT),
			MohCacheUtils.getConcept(REASON_FOR_NEXT_VISIT)
	});

	private static final MohCoreService mohCoreService = Context.getService(MohCoreService.class);

	/**
	 * @param context
	 * @param patientId
	 * @param parameters
	 * @return
	 * @should return due date for patient with one pregnancy
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Override
	protected Result evaluate(final LogicContext context, final Integer patientId, final Map<String, Object> parameters) {

		// set up fetching of observations
		Map<String, Collection<OpenmrsObject>> restrictions = new HashMap<String, Collection<OpenmrsObject>>();
		restrictions.put("concept", questionConcepts);
		MohFetchRestriction fetchRestriction = new MohFetchRestriction();
		fetchRestriction.setFetchOrdering(MohFetchOrdering.ORDER_ASCENDING);

		// get the observations
		List<Obs> observations = mohCoreService.getPatientObservations(patientId, restrictions, fetchRestriction);

		// instantiate a patient snapshot
		MohPregnancyPMTCReferralRuleSnapshot snapshot = new MohPregnancyPMTCReferralRuleSnapshot();

		// set up the result
		Result result = new Result();

		for (Obs observation : observations) {
			Date dueDate = null;

			if (observation.getConcept().equals(MohCacheUtils.getConcept(ESTIMATED_DATE_OF_CONFINEMENT)))
				dueDate = observation.getValueDatetime();
			else if (observation.getConcept().equals(MohCacheUtils.getConcept(ESTIMATED_DATE_OF_CONFINEMENT_ULTRASOUND)))
				dueDate = observation.getValueDatetime();

			if (snapshot.consume(observation))
				result.add(new Result(new Date(), Result.Datatype.DATETIME, Boolean.TRUE, null, dueDate, null, "PMTCT", null));
			else
				result.add(new Result(new Date(), Result.Datatype.DATETIME, Boolean.FALSE, null, null, null, StringUtils.EMPTY, null));
		}

		return result;
	}

	/**
	 * Get the token name of the rule that can be used to reference the rule from LogicService
	 *
	 * @return the token name
	 */
	@Override
	protected String getEvaluableToken() {
		return TOKEN;
	}
}
