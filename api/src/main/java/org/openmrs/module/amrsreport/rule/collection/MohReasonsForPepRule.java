package org.openmrs.module.amrsreport.rule.collection;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.amrsreport.cache.MohCacheUtils;
import org.openmrs.module.amrsreport.rule.MohEvaluableRule;
import org.openmrs.module.amrsreport.service.MohCoreService;
import org.openmrs.module.amrsreport.util.MohFetchOrdering;
import org.openmrs.module.amrsreport.util.MohFetchRestriction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Reasons for PEP Rule
 * <p/>
 * METHOD OF HIV EXPOSURE (2054) =
 * <ul>
 *     <li>SEXUAL ASSAULT (165) OR</li>
 *     <li>SPOUSES PARTNER SUSPECTED HIV+ (5564) OR</li>
 *     <li>PHYSICAL ASSAULT (1789) OR</li>
 *     <li>OCCUPATIONAL EXPOSURE (7094) OR</li>
 *     <li>OTHER (5622)</li>
 * </ul>
 */
public class MohReasonsForPepRule extends MohEvaluableRule {

	private static final Log log = LogFactory.getLog(MohReasonsForPepRule.class);

	public static final String TOKEN = "MOH Reasons For PEP";

	// concepts
	public static final String METHOD_OF_HIV_EXPOSURE = "METHOD OF HIV EXPOSURE";
	public static final String SEXUAL_ASSAULT = "SEXUAL ASSAULT";
	public static final String SPOUSES_PARTNER_SUSPECTED_HIV = "SPOUSES PARTNER SUSPECTED HIV+";
	public static final String PHYSICAL_ASSAULT = "PHYSICAL ASSAULT";
	public static final String OCCUPATIONAL_EXPOSURE = "OCCUPATIONAL EXPOSURE";
	public static final String OTHER_NON_CODED = "OTHER NON-CODED";

	// encounters
	public static final String POST_EXPOSURE_INITIAL_FORM = "PEPINITIAL";
	public static final String POST_EXPOSURE_RETURN_FORM = "PEPRETURN";

	private static final List<OpenmrsObject> questionConcepts = Collections.<OpenmrsObject>singletonList(MohCacheUtils.getConcept(METHOD_OF_HIV_EXPOSURE));

	private static final List<OpenmrsObject> encounterTypes = Arrays.<OpenmrsObject>asList(new EncounterType[]{
			MohCacheUtils.getEncounterType(POST_EXPOSURE_INITIAL_FORM),
			MohCacheUtils.getEncounterType(POST_EXPOSURE_RETURN_FORM)
	});

	private MohCoreService mohCoreService = Context.getService(MohCoreService.class);

	/**
	 * @should recognize SEXUAL_ASSAULT
	 * @should recognize SPOUSES_PARTNER_SUSPECTED_HIV
	 * @should recognize PHYSICAL_ASSAULT
	 * @should recognize OCCUPATIONAL_EXPOSURE
	 * @should recognize OTHER_NON_CODED
	 * @should recognize multiple reasons
	 * @should not recognize other reasons
	 * @see {@link MohEvaluableRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)}
	 */
	public Result evaluate(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {

		// pull relevant observations then loop while checking concepts
		Map<String, Collection<OpenmrsObject>> obsRestrictions = new HashMap<String, Collection<OpenmrsObject>>();
		obsRestrictions.put("concept", questionConcepts);
		Map<String, Collection<OpenmrsObject>> encounterRestrictions = new HashMap<String, Collection<OpenmrsObject>>();
		obsRestrictions.put("encounterType", encounterTypes);

		// set order of observations
		MohFetchRestriction mohFetchRestriction = new MohFetchRestriction();
		mohFetchRestriction.setFetchOrdering(MohFetchOrdering.ORDER_ASCENDING);

		// get the observations
		List<Obs> observations = mohCoreService.getPatientObservationsWithEncounterRestrictions(
				patientId, obsRestrictions, encounterRestrictions, mohFetchRestriction);

		// create the list of reasons
		List<String> pepReasons = new ArrayList<String>();

		for (Obs obs : observations) {
			Concept value = obs.getValueCoded();
			if (value != null) {
				if (value.equals(MohCacheUtils.getConcept(SEXUAL_ASSAULT))) {
					pepReasons.add(SEXUAL_ASSAULT);
				} else if (value.equals(MohCacheUtils.getConcept(SPOUSES_PARTNER_SUSPECTED_HIV))) {
					pepReasons.add(SPOUSES_PARTNER_SUSPECTED_HIV);
				} else if (value.equals(MohCacheUtils.getConcept(OCCUPATIONAL_EXPOSURE))) {
					pepReasons.add(OCCUPATIONAL_EXPOSURE);
				} else if (value.equals(MohCacheUtils.getConcept(PHYSICAL_ASSAULT))) {
					pepReasons.add(PHYSICAL_ASSAULT);
				} else if (obs.getValueCoded().equals(MohCacheUtils.getConcept(OTHER_NON_CODED))) {
					pepReasons.add(OTHER_NON_CODED);
				}
			}
		}

		// make a result from all reasons joined by a semicolon
		return new Result(StringUtils.join(pepReasons, ";"));
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
	 * @see org.openmrs.module.amrsreport.rule.MohEvaluableRule#getDefaultDatatype()
	 */
	@Override
	public Result.Datatype getDefaultDatatype() {
		return Result.Datatype.TEXT;
	}

	public Set<RuleParameterInfo> getParameterList() {
		return null;
	}

	@Override
	public int getTTL() {
		return 0;
	}
}
