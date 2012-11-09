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
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants.AgeGroup;
import org.openmrs.module.amrsreport.rule.MohEvaluableRule;
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

	private Map<String, Concept> cachedConcepts = null;
	private List<Concept> cachedQuestions = null;

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
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, org.openmrs.Patient,
	 *      java.util.Map)
	 */
	@Override
	public Result evaluate(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {

		try {
			Patient patient = Context.getPatientService().getPatient(patientId);
			ARVPatientSnapshot flags = new ARVPatientSnapshot();
            /*if (flags.eligible(getAgeGroupAtDate(patient, o.getObsDatetime())))*/

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
                    flags.setAgeGroup(this.getAgeGroupAtDate(patient, o.getObsDatetime()));
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
	@Override
	public Set<RuleParameterInfo> getParameterList() {
		return null;
	}

	/**
	 * @see org.openmrs.logic.Rule#getDependencies()
	 */
	@Override
	public String[] getDependencies() {
		return new String[]{};
	}

	/**
	 * @see org.openmrs.logic.Rule#getTTL()
	 */
	@Override
	public int getTTL() {
		return 60 * 60 * 24; // 1 day
	}

	/**
	 * @see org.openmrs.logic.Rule#getDefaultDatatype()
	 */
	@Override
	public Datatype getDefaultDatatype() {
		return Datatype.TEXT;
	}

	/**
	 * determine the age group for a patient at a given date
	 *
	 * @param patient the patient whose age is used in the calculations
	 * @param when the date upon which the age should be identified
	 * @return the appropriate age group
	 */
	private AgeGroup getAgeGroupAtDate(Patient patient, Date when) {
		Date birthdate = patient.getBirthdate();
		if (birthdate == null) {
			return null;
		}

		Calendar now = Calendar.getInstance();
		if (when != null) {
			now.setTime(when);
		}

		Calendar then = Calendar.getInstance();
		then.setTime(birthdate);

		int ageInMonths = 0;
		while (!then.after(now)) {
			then.add(Calendar.MONTH, 1);
			ageInMonths++;
		}
		ageInMonths--;

		if (ageInMonths < 18) {
			return AgeGroup.UNDER_EIGHTEEN_MONTHS;
		}

		if (ageInMonths < 60) {
			return AgeGroup.EIGHTEEN_MONTHS_TO_FIVE_YEARS;
		}

		if (ageInMonths < 144) {
			return AgeGroup.FIVE_YEARS_TO_TWELVE_YEARS;
		}

		return AgeGroup.ABOVE_TWELVE_YEARS;
	}

	/**
	 * maintains a cache of concepts and stores them by name
	 * 
	 * @param name the name of the cached concept to retrieve
	 * @return the concept matching the name
	 */
	private Concept getCachedConcept(String name) {
		if (cachedConcepts == null) {
			cachedConcepts = new HashMap<String, Concept>();
		}
		if (!cachedConcepts.containsKey(name)) {
			cachedConcepts.put(name, Context.getConceptService().getConcept(name));
		}
		return cachedConcepts.get(name);
	}

	/**
	 * obtain the list of questions for observations we are interested in to determine ART medical eligibility
	 *
	 * @return a list of concepts for use in finding relevant observations
	 */
	private List<Concept> getQuestionConcepts() {
		if (cachedQuestions == null) {
			cachedQuestions = new ArrayList<Concept>();
			cachedQuestions.add(getCachedConcept(MohEvaluableNameConstants.CD4_BY_FACS));
			cachedQuestions.add(getCachedConcept(MohEvaluableNameConstants.CD4_PERCENT));
			cachedQuestions.add(getCachedConcept(MohEvaluableNameConstants.HIV_DNA_PCR));
			cachedQuestions.add(getCachedConcept(MohEvaluableNameConstants.WHO_STAGE_PEDS));
			cachedQuestions.add(getCachedConcept(MohEvaluableNameConstants.WHO_STAGE_ADULT));
		}
		return cachedQuestions;
	}

	private Result formatResult(Date date, String reason) {
		return new Result(OpenmrsUtil.getDateFormat(Context.getLocale()).format(date) + " - " + reason);
	}
}