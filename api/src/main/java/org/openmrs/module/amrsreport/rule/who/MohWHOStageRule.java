package org.openmrs.module.amrsreport.rule.who;

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

/**
 * Determines the WHO stage upon enrollment, essentially the first observation of ADULT_WHO_CONDITION_QUERY or
 * PEDS_WHO_SPECIFIC_CONDITION_QUERY.
 */
public class MohWHOStageRule extends MohEvaluableRule {

	private static final Log log = LogFactory.getLog(MohWHOStageRule.class);

	public static final String TOKEN = "MOH WHO Stage";

	protected static final List<Concept> ADULT_WHO_STAGE_1_CONCEPTS = MohCacheUtils.getConcept(
			MohEvaluableNameConstants.WHO_STAGE_1_ADULT).getSetMembers();

	protected static final List<Concept> ADULT_WHO_STAGE_2_CONCEPTS = MohCacheUtils.getConcept(
			MohEvaluableNameConstants.WHO_STAGE_2_ADULT).getSetMembers();

	protected static final List<Concept> ADULT_WHO_STAGE_3_CONCEPTS = MohCacheUtils.getConcept(
			MohEvaluableNameConstants.WHO_STAGE_3_ADULT).getSetMembers();

	protected static final List<Concept> ADULT_WHO_STAGE_4_CONCEPTS = MohCacheUtils.getConcept(
			MohEvaluableNameConstants.WHO_STAGE_4_ADULT).getSetMembers();

	protected static final List<Concept> PEDS_WHO_STAGE_1_CONCEPTS = MohCacheUtils.getConcept(
			MohEvaluableNameConstants.WHO_STAGE_1_PEDS).getSetMembers();

	protected static final List<Concept> PEDS_WHO_STAGE_2_CONCEPTS = MohCacheUtils.getConcept(
			MohEvaluableNameConstants.WHO_STAGE_2_PEDS).getSetMembers();

	protected static final List<Concept> PEDS_WHO_STAGE_3_CONCEPTS = MohCacheUtils.getConcept(
			MohEvaluableNameConstants.WHO_STAGE_3_PEDS).getSetMembers();

	protected static final List<Concept> PEDS_WHO_STAGE_4_CONCEPTS = MohCacheUtils.getConcept(
			MohEvaluableNameConstants.WHO_STAGE_4_PEDS).getSetMembers();

	private static final List<OpenmrsObject> questionConcepts = Arrays.<OpenmrsObject>asList(new Concept[]{
			MohCacheUtils.getConcept(MohEvaluableNameConstants.ADULT_WHO_CONDITION_QUERY),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.PEDS_WHO_SPECIFIC_CONDITION_QUERY)
	});

	private MohCoreService mohCoreService = Context.getService(MohCoreService.class);

	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, org.openmrs.Patient, java.util.Map)
	 *
	 * @should recognize WHO_STAGE_1_ADULT
	 * @should recognize WHO_STAGE_2_ADULT
	 * @should recognize WHO_STAGE_3_ADULT
	 * @should recognize WHO_STAGE_4_ADULT
	 * @should recognize WHO_STAGE_1_PEDS
	 * @should recognize WHO_STAGE_2_PEDS
	 * @should recognize WHO_STAGE_3_PEDS
	 * @should recognize WHO_STAGE_4_PEDS
	 * @should return UNKNOWN if not found
	 */
	@Override
	protected Result evaluate(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {

		// pull relevant observations
		Map<String, Collection<OpenmrsObject>> obsRestrictions = new HashMap<String, Collection<OpenmrsObject>>();
		obsRestrictions.put("concept", questionConcepts);

		// we only need the first stage, "upon enrollment" ... so get the first one in ascending order
		MohFetchRestriction mohFetchRestriction = new MohFetchRestriction();
		mohFetchRestriction.setFetchOrdering(MohFetchOrdering.ORDER_ASCENDING);
		mohFetchRestriction.setSize(1);

		List<Obs> observations = mohCoreService.getPatientObservations(patientId, obsRestrictions, mohFetchRestriction);

		for (Obs WHOObs : observations) {
			Concept value = WHOObs.getValueCoded();

			if (OpenmrsUtil.isConceptInList(value, ADULT_WHO_STAGE_1_CONCEPTS)) {
				if (log.isDebugEnabled())
					log.debug("Entering stage 1 for determination adult");
				return new Result("WHO STAGE 1 ADULT - " + MohRuleUtils.formatdates(WHOObs.getObsDatetime()));

			} else if (OpenmrsUtil.isConceptInList(value, ADULT_WHO_STAGE_2_CONCEPTS)) {
				log.info("Entering stage 2 for determination adult");
				return new Result("WHO STAGE 2 ADULT - " + MohRuleUtils.formatdates(WHOObs.getObsDatetime()));

			} else if (OpenmrsUtil.isConceptInList(value, ADULT_WHO_STAGE_3_CONCEPTS)) {
				if (log.isDebugEnabled())
					log.debug("Entering stage 3 for determination adult");
				return new Result("WHO STAGE 3 ADULT - " + MohRuleUtils.formatdates(WHOObs.getObsDatetime()));

			} else if (OpenmrsUtil.isConceptInList(value, ADULT_WHO_STAGE_4_CONCEPTS)) {
				if (log.isDebugEnabled())
					log.debug("Entering stage 4 for determination adult");
				return new Result("WHO STAGE 4 ADULT - " + MohRuleUtils.formatdates(WHOObs.getObsDatetime()));

			} else if (OpenmrsUtil.isConceptInList(value, PEDS_WHO_STAGE_1_CONCEPTS)) {
				if (log.isDebugEnabled())
					log.debug("Entering stage 1 for determination peds");
				return new Result("WHO STAGE 1 PEDS - " + MohRuleUtils.formatdates(WHOObs.getObsDatetime()));

			} else if (OpenmrsUtil.isConceptInList(value, PEDS_WHO_STAGE_2_CONCEPTS)) {
				log.info("Entering stage 2 for determination peds");
				return new Result("WHO STAGE 2 PEDS - " + MohRuleUtils.formatdates(WHOObs.getObsDatetime()));

			} else if (OpenmrsUtil.isConceptInList(value, PEDS_WHO_STAGE_3_CONCEPTS)) {
				if (log.isDebugEnabled())
					log.debug("Entering stage 3 for determination peds");
				return new Result("WHO STAGE 3 PEDS - " + MohRuleUtils.formatdates(WHOObs.getObsDatetime()));

			} else if (OpenmrsUtil.isConceptInList(value, PEDS_WHO_STAGE_4_CONCEPTS)) {
				if (log.isDebugEnabled())
					log.debug("Entering stage 4 for determination peds");
				return new Result("WHO STAGE 4 PEDS - " + MohRuleUtils.formatdates(WHOObs.getObsDatetime()));
			}
		}

		return new Result(MohEvaluableNameConstants.UNKNOWN);
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