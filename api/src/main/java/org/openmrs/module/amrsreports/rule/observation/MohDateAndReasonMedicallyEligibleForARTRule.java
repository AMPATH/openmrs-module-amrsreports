package org.openmrs.module.amrsreports.rule.observation;

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
import org.openmrs.module.amrsreports.cache.MohCacheUtils;
import org.openmrs.module.amrsreports.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreports.rule.MohEvaluableRule;
import org.openmrs.module.amrsreports.rule.medication.MohDateArtStartedRule;
import org.openmrs.module.amrsreports.rule.util.MohRuleUtils;
import org.openmrs.module.amrsreports.service.MohCoreService;
import org.openmrs.module.amrsreports.util.MOHReportUtil;
import org.openmrs.module.amrsreports.util.MohFetchRestriction;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
	 * @should return Clinical and WHO Stage if under 12 and PEDS WHO Stage is 4
	 * @should return CD4 and WHO Stage and CD4 values if under 12 and PEDS WHO Stage is 3 and CD4 is under 500 and CD4 percentage is under 25
	 * @should return CD4 and HIV DNA PCR and WHO Stage and CD4 and HIV DNA PCR values if under 18 months and PEDS WHO Stage is 2 and CD4 is under 500 and HIV DNA PCR is positive
	 * @should return HIV DNA PCR and WHO Stage and HIV DNA PCR value if under 18 months and PEDS WHO Stage is 1 and HIV DNA PCR is positive
	 * @should return CD4 and WHO Stage and CD4 percentage values if between 18 months and 5 years and PEDS WHO Stage is 1 or 2 and CD4 percentage is under 20
	 * @should return CD4 and WHO Stage and CD4 percentage values if between 5 years and 12 years and PEDS WHO Stage is 1 or 2 and CD4 percentage is under 25
	 * @should return Clinical and WHO Stage if over 12 and ADULT WHO Stage is 3 or 4
	 * @should return CD4 and WHO Stage and CD4 value if over 12 and ADULT or PEDS WHO Stage is 1 or 2 and CD4 is under 350
	 * @should return reason only when ART started before eligibility date
	 *
	 * @see {@link MohEvaluableRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)}
	 */
	@Override
	public Result evaluate(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {

		// get evaluation date from logic context
		Date evaluationDate = context.getIndexDate();

		Patient patient = Context.getPatientService().getPatient(patientId);

		try {
			ARVPatientSnapshot flags = new ARVPatientSnapshot();

			//pull relevant observations then loop while checking concepts
			Map<String, Collection<OpenmrsObject>> obsRestrictions = new HashMap<String, Collection<OpenmrsObject>>();
			obsRestrictions.put("concept", questionConcepts);
			MohFetchRestriction mohFetchRestriction = new MohFetchRestriction();
			List<Obs> observations = mohCoreService.getPatientObservations(patientId, obsRestrictions, mohFetchRestriction, evaluationDate);

			// iterate through observations, flip flags and evaluate them as we go
			for (Obs o : observations) {
				// flip a flag, if possible
				if (flags.consume(o)) // if a flag was flipped, check eligibility
				{
					flags.setAgeGroup(MohRuleUtils.getAgeGroupAtDate(patient.getBirthdate(), o.getObsDatetime()));
					if (flags.eligible()) // this obs marks the first eligible date; return it
					{
						Date obsDatetime = o.getObsDatetime();
						Date artStarted = getArtStarted(context, patientId, parameters);
						if (artStarted != null && obsDatetime.after(artStarted))
							return formatResult(flags);
						else
							return formatResult(o.getObsDatetime(), flags);
					}
				}
			}

		} catch (Exception e) {
			log.warn("could not evaluate patient for ART eligibility date.", e);
			throw new LogicException("could not evaluate patient for ART eligibility date.", e);
		}
		return new Result();
	}

	private Date getArtStarted(final LogicContext context, final Integer patientId, Map<String, Object> parameters) {
		DateFormat dateFormat = new SimpleDateFormat(MohRuleUtils.DATE_FORMAT);

		MohDateArtStartedRule mohDateArtStartedRule = new MohDateArtStartedRule();
		Result result = mohDateArtStartedRule.eval(context, patientId, parameters);
		Date artStarted = null;
		try {
			artStarted = dateFormat.parse(result.toString());
		} catch (ParseException e) {
			log.info("Ignoring non-date value!");
		}
		return artStarted;
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

	private Result formatResult(PatientSnapshot flags) {
		return formatResult(null, flags);
	}

	private Result formatResult(Date date, PatientSnapshot flags) {
		List<String> results = new ArrayList<String>();

		if (date != null)
			results.add(MohRuleUtils.formatdates(date));

		results.add((String) flags.get("reason"));
		if (flags.hasProperty("extras"))
			results.addAll((List<String>) flags.get("extras"));

		return new Result(MOHReportUtil.joinAsSingleCell(results));
	}
}