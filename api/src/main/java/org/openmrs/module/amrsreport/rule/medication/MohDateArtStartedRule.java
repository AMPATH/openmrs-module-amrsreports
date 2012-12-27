package org.openmrs.module.amrsreport.rule.medication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.OpenmrsObject;
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
import org.openmrs.module.amrsreport.util.MohFetchOrdering;
import org.openmrs.module.amrsreport.util.MohFetchRestriction;
import org.openmrs.util.OpenmrsUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MohDateArtStartedRule extends MohEvaluableRule {

	private static final Log log = LogFactory.getLog(MohDateArtStartedRule.class);

	public static final String TOKEN = "MOH Date ART Started";

	protected static final List<OpenmrsObject> questionConcepts = Arrays.<OpenmrsObject>asList(
			MohCacheUtils.getConcept(MohEvaluableNameConstants.ANTIRETROVIRAL_DRUG_TREATMENT_START_DATE),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.ANTIRETROVIRAL_PLAN)
	);

	protected static final List<OpenmrsObject> excludeQuestions = Arrays.<OpenmrsObject>asList(
			MohCacheUtils.getConcept(MohEvaluableNameConstants.REASON_ANTIRETROVIRALS_STARTED),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.PATIENT_REPORTED_REASON_FOR_CURRENT_ANTIRETROVIRALS_STARTED),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.NEWBORN_ANTIRETROVIRAL_USE),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.NEWBORN_PROPHYLACTIC_ANTIRETROVIRAL_USE)
	);

	private static final List<Concept> excludedReasons = Arrays.<Concept>asList(
			MohCacheUtils.getConcept(MohEvaluableNameConstants.TOTAL_MATERNAL_TO_CHILD_TRANSMISSION_PROPHYLAXIS),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.PREVENTION_OF_MOTHER_TO_CHILD_TRANSMISSION_OF_HIV)
	);

	private static final List<Concept> excludedNewbornARVs = Arrays.<Concept>asList(
			MohCacheUtils.getConcept(MohEvaluableNameConstants.STAVUDINE),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.LAMIVUDINE),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.NEVIRAPINE),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.NELFINAVIR),
			MohCacheUtils.getConcept("LOPINAVIR AND RITONAVIR"),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.ZIDOVUDINE),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.OTHER_NON_CODED)
	);

	private static final List<Concept> allowedAnswers = Arrays.<Concept>asList(
			MohCacheUtils.getConcept(MohEvaluableNameConstants.START_DRUGS),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.CONTINUE_REGIMEN),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.CHANGE_FORMULATION),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.CHANGE_REGIMEN),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.REFILLED),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.NOT_REFILLED),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.DRUG_SUBSTITUTION),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.DRUG_RESTART),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.DOSING_CHANGE)
	);

	/**
	 * @see {@link MohEvaluableRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)}
	 * @should return first obs date for ANTIRETROVIRAL_PLAN in allowed answers
	 * @should return first value date for ANTIRETROVIRAL_DRUG_TREATMENT_START_DATE
	 * @should return first date for mixed valid observations
	 * @should exclude if REASON_ANTIRETROVIRALS_STARTED is in excluded reasons
	 * @should exclude if PATIENT_REPORTED_REASON_FOR_CURRENT_ANTIRETROVIRALS_STARTED is PREVENTION_OF_MOTHER_TO_CHILD_TRANSMISSION_OF_HIV
	 * @should exclude if NEWBORN_PROPHYLACTIC_ANTIRETROVIRAL_USE is TRUE
	 * @should exclude if NEWBORN_ANTIRETROVIRAL_USE is in excluded newborn ARVs
	 */
	@Override
	public Result evaluate(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {

		// ensure the patient qualifies for evaluation
		Map<String, Collection<OpenmrsObject>> restrictions = new HashMap<String, Collection<OpenmrsObject>>();
		restrictions.put("concept", excludeQuestions);

		MohFetchRestriction fetchRestriction = new MohFetchRestriction();
		fetchRestriction.setFetchOrdering(MohFetchOrdering.ORDER_ASCENDING);

		// get the observations
		List<Obs> observations = Context.getService(MohCoreService.class).getPatientObservations(patientId, restrictions, fetchRestriction);

		// check them for reasons to exclude
		for (Obs obs : observations) {

			if (MohRuleUtils.compareConceptToName(obs.getConcept(), MohEvaluableNameConstants.REASON_ANTIRETROVIRALS_STARTED)) {
				if (OpenmrsUtil.isConceptInList(obs.getValueCoded(), excludedReasons))
					return excludePatient();
			}

			if (MohRuleUtils.compareConceptToName(obs.getConcept(), MohEvaluableNameConstants.PATIENT_REPORTED_REASON_FOR_CURRENT_ANTIRETROVIRALS_STARTED)) {
				if (MohRuleUtils.compareConceptToName(obs.getValueCoded(), MohEvaluableNameConstants.PREVENTION_OF_MOTHER_TO_CHILD_TRANSMISSION_OF_HIV))
					return excludePatient();
			}

			if (MohRuleUtils.compareConceptToName(obs.getConcept(), MohEvaluableNameConstants.NEWBORN_PROPHYLACTIC_ANTIRETROVIRAL_USE)) {
				if (MohRuleUtils.compareConceptToName(obs.getValueCoded(), "TRUE"))
					return excludePatient();
			}

			if (MohRuleUtils.compareConceptToName(obs.getConcept(), MohEvaluableNameConstants.NEWBORN_ANTIRETROVIRAL_USE)) {
				if (OpenmrsUtil.isConceptInList(obs.getValueCoded(), excludedNewbornARVs))
					return excludePatient();
			}
		}

		// set up for positive results
		restrictions.clear();
		restrictions.put("concept", questionConcepts);

		// get the observations
		observations = Context.getService(MohCoreService.class).getPatientObservations(patientId, restrictions, fetchRestriction);

		// find the first qualifying observation
		for (Obs obs : observations) {
			if (MohRuleUtils.compareConceptToName(obs.getConcept(), MohEvaluableNameConstants.ANTIRETROVIRAL_DRUG_TREATMENT_START_DATE))
				return new Result(MohRuleUtils.formatdates(obs.getValueDatetime()));

			if (OpenmrsUtil.isConceptInList(obs.getValueCoded(), allowedAnswers))
				return new Result(MohRuleUtils.formatdates(obs.getObsDatetime()));
		}

		// give up
		return new Result(MohEvaluableNameConstants.UNKNOWN);
	}

	/**
	 * generates a common response for excluded patients
	 */
	private Result excludePatient() {
		return new Result("Excluded");
	}

	protected String getEvaluableToken() {
		return TOKEN;
	}

	/**
	 * @see org.openmrs.logic.Rule#getDependencies()
	 */
	@Override
	public String[] getDependencies() {
		return new String[]{};
	}

	/**
	 * Get the definition of each parameter that should be passed to this rule execution
	 *
	 * @return all parameter that applicable for each rule execution
	 */
	@Override
	public Datatype getDefaultDatatype() {
		return Datatype.DATETIME;
	}

	public Set<RuleParameterInfo> getParameterList() {
		return null;
	}

	@Override
	public int getTTL() {
		return 0;
	}
}