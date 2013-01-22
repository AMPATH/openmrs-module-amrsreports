package org.openmrs.module.amrsreport.rule.collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Determines Confirmed HIV+ Date
 */
public class MohConfirmedHivPositiveDateRule extends MohEvaluableRule {

	private static final Log log = LogFactory.getLog(MohConfirmedHivPositiveDateRule.class);

	public static final String TOKEN = "MOH Confirmed HIV Positive Date";

	private static final Concept CONCEPT_ENZYME = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_ENZYME_IMMUNOASSAY_QUALITATIVE);
	private static final Concept CONCEPT_RAPID = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_RAPID_TEST_QUALITATIVE);
	private static final Concept CONCEPT_POSITIVE = MohCacheUtils.getConcept(MohEvaluableNameConstants.POSITIVE);

	private MohCoreService mohCoreService = Context.getService(MohCoreService.class);

	/**
	 * @should return the the first date a patient was confirmed HIV positive using HIV_ENZYME_IMMUNOASSAY_QUALITATIVE test
	 * @should return the first date a patient was confirmed HIV Positive using HIV_RAPID_TEST_QUALITATIVE test
	 * @should return result for a patient who is HIV negative
	 * @see {@link MohEvaluableRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)}
	 */
	public Result evaluate(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {

		//pull relevant observations then loop while checking concepts
		Map<String, Collection<OpenmrsObject>> obsRestrictions = new HashMap<String, Collection<OpenmrsObject>>();
		obsRestrictions.put("concept", Arrays.<OpenmrsObject>asList(new Concept[]{CONCEPT_ENZYME, CONCEPT_RAPID}));
		obsRestrictions.put("valueCoded", Arrays.<OpenmrsObject>asList(new Concept[]{CONCEPT_POSITIVE}));

		MohFetchRestriction mohFetchRestriction = new MohFetchRestriction();
		mohFetchRestriction.setFetchOrdering(MohFetchOrdering.ORDER_ASCENDING);
		mohFetchRestriction.setSize(1);

		List<Obs> observations = mohCoreService.getPatientObservations(patientId, obsRestrictions, mohFetchRestriction);

		// return the first observation found
		if (!observations.isEmpty()) {
			return new Result(MohRuleUtils.formatdates(observations.get(0).getObsDatetime()));
		}

		// return the first encounter date if nothing else is found
		Date firstEncounterDate = null;

		// set up query for encounters in order by ascending date
		Map<String, Collection<OpenmrsObject>> restrictions = new HashMap<String, Collection<OpenmrsObject>>();

		// get the encounters
		List<Encounter> encounters = mohCoreService.getPatientEncounters(patientId, restrictions, mohFetchRestriction);

		// pull the first encounter date
		if (!encounters.isEmpty()) {
			return new Result(MohRuleUtils.formatdates(encounters.get(0).getEncounterDatetime()));
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

	/**
	 * Get the definition of each parameter that should be passed to this rule execution
	 *
	 * @return all parameter that applicable for each rule execution
	 */
	@Override
	public Datatype getDefaultDatatype() {
		return Datatype.TEXT;
	}

	public Set<RuleParameterInfo> getParameterList() {
		return null;
	}

	@Override
	public int getTTL() {
		return 0;
	}

}