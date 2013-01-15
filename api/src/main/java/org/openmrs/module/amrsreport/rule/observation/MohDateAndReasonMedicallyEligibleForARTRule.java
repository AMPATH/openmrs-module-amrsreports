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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.OpenmrsObject;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.amrsreport.cache.MohCacheUtils;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreport.rule.MohEvaluableRule;
import org.openmrs.module.amrsreport.rule.util.MohRuleUtils;
import org.openmrs.module.amrsreport.service.MohCoreService;
import org.openmrs.module.amrsreport.util.MohFetchRestriction;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * determine the date and reason for medical eligibility for ART
 */
public class MohDateAndReasonMedicallyEligibleForARTRule extends MohEvaluableRule {

	private static final Log log = LogFactory.getLog(MohDateAndReasonMedicallyEligibleForARTRule.class);

	public static final String TOKEN = "MOH Date and Reason Medically Eligible For ART";

	private static final List<OpenmrsObject> questionConcepts = Arrays.<OpenmrsObject>asList(new Concept[]{
			MohCacheUtils.getConcept(MohEvaluableNameConstants.CD4_BY_FACS),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.CD4_PERCENT),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_DNA_PCR),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.WHO_STAGE_PEDS),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.WHO_STAGE_ADULT)
	});

	private MohCoreService mohCoreService = Context.getService(MohCoreService.class);

	/**
	 * @should get the date and reason for ART eligibility
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, org.openmrs.Patient,
	 *      java.util.Map)
	 */
	@Override
	public Result evaluate(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		Patient patient = Context.getPatientService().getPatient(patientId);

		try {
			ARVPatientSnapshot flags = new ARVPatientSnapshot();

			//pull relevant observations then loop while checking concepts
			Map<String, Collection<OpenmrsObject>> obsRestrictions = new HashMap<String, Collection<OpenmrsObject>>();
			obsRestrictions.put("concept", questionConcepts);
			MohFetchRestriction mohFetchRestriction = new MohFetchRestriction();
			List<Obs> observations = mohCoreService.getPatientObservations(patientId, obsRestrictions, mohFetchRestriction);

			// iterate through observations, flip flags and evaluate them as we go
			for (Obs o : observations) {
				// flip a flag, if possible
				if (flags.consume(o)) // if a flag was flipped, check eligibility
				{
					flags.setAgeGroup(MohRuleUtils.getAgeGroupAtDate(patient.getBirthdate(), o.getObsDatetime()));
					if (flags.eligible()) // this obs marks the first eligible date; return it
					{
						return formatResult(o.getObsDatetime(), (String) flags.getProperty("reason"));
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

	private Result formatResult(Date date, String reason) {
		return new Result(MohRuleUtils.formatdates(date) + " - " + reason);
	}
}