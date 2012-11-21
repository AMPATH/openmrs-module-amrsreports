package org.openmrs.module.amrsreport.rule.observation;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;

import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.amrsreport.cache.MohCacheUtils;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants.AgeGroup;
import org.openmrs.module.amrsreport.rule.MohEvaluableRule;
import org.openmrs.module.amrsreport.rule.util.MohRuleUtils;
import org.openmrs.util.OpenmrsUtil;

/**
 * determine the date and reason for medical eligibility for ART
 */
public class MohDateAndReasonMedicallyEligibleForARTRule extends MohEvaluableRule {

	private static final Log log = LogFactory.getLog(MohDateAndReasonMedicallyEligibleForARTRule.class);

	public static final String TOKEN = "MOH Date and Reason Medically Eligible For ART";
	public static final String REASON_CLINICAL = "Clinical Only";
	public static final String REASON_CLINICAL_CD4 = "Clinical + CD4";
	public static final String REASON_CLINICAL_CD4_HIV_DNA_PCR = "Clinical + CD4 + HIV DNA PCR";
	public static final String REASON_CLINICAL_HIV_DNA_PCR = "Clinical + HIV DNA PCR";

	/**
	 * comparator for sorting observations
	 */
	private static class SortByDateComparator implements Comparator {

		@Override
		public int compare(Object a, Object b) {
			Obs ao = (Obs) a;
			Obs bo = (Obs) b;
			return ao.getObsDatetime().compareTo(bo.getObsDatetime());
		}
	}

	/**
     * @should get the date and reason for ART eligibility
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, org.openmrs.Patient,
	 *      java.util.Map)
	 */
	@Override
	public Result evaluate(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {

		try {
			Patient patient = Context.getPatientService().getPatient(patientId);
			ARVPatientSnapshot flags = new ARVPatientSnapshot();


			// get relevant observations
			List<Obs> obs = Context.getObsService().getObservations(
					Arrays.asList(new Person[]{patient}), null, getQuestionConcepts(),
					null, null, null, null, null, null, null, null, false);

			// order the observations by date
			// TODO may not be necessary -- check getObservations() code
			Collections.sort(obs, new SortByDateComparator());

			// iterate through observations, flip flags and evaluate them as we go
			for (Obs o : obs) {
				// flip a flag, if possible
				if (flags.consume(o)) // if a flag was flipped, check eligibility
				{
                    flags.setAgeGroup(MohRuleUtils.getAgeGroupAtDate(patient.getBirthdate(), o.getObsDatetime()));
					if (flags.eligible()) // this obs marks the first eligible date; return it
					{
						return formatResult(o.getObsDatetime(), (String)flags.getProperty("reason"));
					}
				}
			}

		} catch (Exception e) {
			log.warn("could not evaluate patient for ART eligibility date.", e);
			throw new LogicException("could not evaluate patient for ART eligibility date.", e);
		}
		return new Result();
	}

	@Override
	protected String getEvaluableToken() {
		return TOKEN;
	}

	/**
	 * @see org.openmrs.logic.Rule#getParameterList()
	 */

	public Set<RuleParameterInfo> getParameterList() {
		return null;
	}

	/**
	 * @see org.openmrs.logic.Rule#getDependencies()
	 */

	public String[] getDependencies() {
		return new String[]{};
	}

	/**
	 * @see org.openmrs.logic.Rule#getTTL()
	 */

	public int getTTL() {
		return 60 * 60 * 24; // 1 day
	}

	/**
	 * @see org.openmrs.logic.Rule#getDefaultDatatype()
	 */

	public Datatype getDefaultDatatype() {
		return Datatype.TEXT;
	}

	/**
	 * obtain the list of questions for observations we are interested in to determine ART medical eligibility
	 *
	 * @return a list of concepts for use in finding relevant observations
	 */
	private List<Concept> getQuestionConcepts() {
		List<Concept> questions = new ArrayList<Concept>();
			questions.add(MohCacheUtils.getConcept(MohEvaluableNameConstants.CD4_BY_FACS));
			questions.add(MohCacheUtils.getConcept(MohEvaluableNameConstants.CD4_PERCENT));
			questions.add(MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_DNA_PCR));
			questions.add(MohCacheUtils.getConcept(MohEvaluableNameConstants.WHO_STAGE_PEDS));
			questions.add(MohCacheUtils.getConcept(MohEvaluableNameConstants.WHO_STAGE_ADULT));
		return questions;
	}

	private Result formatResult(Date date, String reason) {
		return new Result(OpenmrsUtil.getDateFormat(Context.getLocale()).format(date) + " - " + reason);
	}
}