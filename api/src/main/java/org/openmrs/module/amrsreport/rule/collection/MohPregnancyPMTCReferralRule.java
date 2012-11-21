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
import java.util.Calendar;
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

	private MohCoreService mohCoreService;

	public void setMohCoreService(MohCoreService mohCoreService) {
		this.mohCoreService = mohCoreService;
	}

	public MohPregnancyPMTCReferralRule() {
		setMohCoreService(Context.getService(MohCoreService.class));
	}

	/**
	 * @param context
	 * @param patientId
	 * @param parameters
	 * @return
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 * @should return due date for patient with one pregnancy
	 */
	@Override
	protected Result evaluate(final LogicContext context, final Integer patientId, final Map<String, Object> parameters) {

		// set up fetching of observations
		Map<String, Collection<OpenmrsObject>> restrictions = new HashMap<String, Collection<OpenmrsObject>>();
		restrictions.put("concept", getQuestionConcepts());
		MohFetchRestriction fetchRestriction = new MohFetchRestriction();
		fetchRestriction.setFetchOrdering(MohFetchOrdering.ORDER_ASCENDING);

		// get the observations
		List<Obs> observations = mohCoreService.getPatientObservations(patientId, restrictions, fetchRestriction);

		Result result = new Result();
		Date dueDate = null;

		// iterate over the observations
		for (Obs observation : observations) {

			if (observation.getConcept().equals(MohCacheUtils.getConcept(ESTIMATED_DATE_OF_CONFINEMENT))
					|| observation.getConcept().equals(MohCacheUtils.getConcept(ESTIMATED_DATE_OF_CONFINEMENT_ULTRASOUND)))
				dueDate = observation.getValueDatetime();

			if (isPregnant(observation))
				result = new Result(new Date(), Result.Datatype.DATETIME, Boolean.TRUE, null, dueDate, null, "PMTCT", null);
			else
				result = new Result(new Date(), Result.Datatype.DATETIME, Boolean.FALSE, null, null, null, StringUtils.EMPTY, null);
		}

		return result;
	}

	private Boolean isPregnant(Obs obs) {
		Concept concept = obs.getConcept();
		Date valueDatetime = obs.getValueDatetime();
		Double valueNumeric = obs.getValueNumeric();
		Concept valueCoded = obs.getValueCoded();

		// ESTIMATED DATE OF CONFINEMENT (5596)-OBS. DATE)<=42 OR
		if (concept.equals(MohCacheUtils.getConcept(ESTIMATED_DATE_OF_CONFINEMENT))) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(obs.getObsDatetime());
			calendar.add(Calendar.DATE, 42 * 7);
			if (calendar.getTime().after(valueDatetime))
				return true;
		}

		// ESTIMATED DATE OF CONFINEMENT, ULTRASOUND (6743)-OBS. DATE)<=294 OR
		if (concept.equals(MohCacheUtils.getConcept(ESTIMATED_DATE_OF_CONFINEMENT_ULTRASOUND))) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(obs.getObsDatetime());
			calendar.add(Calendar.DATE, 294);
			if (calendar.getTime().after(valueDatetime))
				return true;
		}

		// CURRENT PREGNANT (5272)=YES (1065) OR
		if (concept.equals(MohCacheUtils.getConcept(CURRENT_PREGNANT))) {
			if (valueCoded.equals(MohCacheUtils.getConcept(YES)))
				return true;
		}

		// NO OF WEEK OF PREGNANCY (1279)>0 OR
		if (concept.equals(MohCacheUtils.getConcept(NO_OF_WEEK_OF_PREGNANCY))) {
			if (valueNumeric > 0)
				return true;
		}

		// MONTH OF CURRENT GESTATION (5992)>0 OR
		if (concept.equals(MohCacheUtils.getConcept(MONTH_OF_CURRENT_GESTATION))) {
			if (valueNumeric > 0)
				return true;
		}

		// FUNDAL LENGTH (1855) >0 OR
		if (concept.equals(MohCacheUtils.getConcept(FUNDAL_LENGTH))) {
			if (valueNumeric > 0)
				return true;
		}

		// PREGNANCY URINE TEST (45)=POSITIVE (703) OR
		if (concept.equals(MohCacheUtils.getConcept(PREGNANCY_URINE_TEST))) {
			if (valueCoded.equals(MohCacheUtils.getConcept(POSITIVE)))
				return true;
		}

		// URGENT MEDICAL ISSUES (1790) = PREGNANCY (44) OR
		if (concept.equals(MohCacheUtils.getConcept(URGENT_MEDICAL_ISSUES))) {
			if (valueCoded.equals(MohCacheUtils.getConcept(PREGNANCY)))
				return true;
		}

		/*// PROBLEM ADDED (6042) = PREGNANCY, ECTOPIC (46) OR
					if (concept.equals(MohCacheUtils.getConcept(PROBLEM_ADDED))) {
							if (valueCoded.equals(MohCacheUtils.getConcept(PREGNANCY_ECTOPIC)))
									return true;
					}*/

		// FOETAL MOVEMENT (1856)=YES (1065) OR
		if (concept.equals(MohCacheUtils.getConcept(FOETAL_MOVEMENT))) {
			if (valueCoded.equals(MohCacheUtils.getConcept(YES)))
				return true;
		}

		// REASON FOR CURRENT VISIT (1834)=ANTENATAL CARE (1831) OR
		if (concept.equals(MohCacheUtils.getConcept(REASON_FOR_CURRENT_VISIT))) {
			if (valueCoded.equals(MohCacheUtils.getConcept(ANTENATAL_CARE)))
				return true;
		}

		// REASON FOR NEXT VISIT (1835)=ANTENATAL CARE (1831) OR
		if (concept.equals(MohCacheUtils.getConcept(REASON_FOR_NEXT_VISIT))) {
			if (valueCoded.equals(MohCacheUtils.getConcept(ANTENATAL_CARE)))
				return true;
		}

		return false;
	}

	private Collection<OpenmrsObject> getQuestionConcepts() {
		return Arrays.asList(
				new OpenmrsObject[]{
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
