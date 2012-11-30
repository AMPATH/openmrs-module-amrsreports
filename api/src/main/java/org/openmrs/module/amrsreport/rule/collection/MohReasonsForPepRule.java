package org.openmrs.module.amrsreport.rule.collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.amrsreport.cache.MohCacheUtils;
import org.openmrs.module.amrsreport.rule.MohEvaluableRule;
import org.openmrs.module.amrsreport.service.MohCoreService;
import org.openmrs.module.amrsreport.util.MohFetchRestriction;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MohReasonsForPepRule extends MohEvaluableRule {

	private static final Log log = LogFactory.getLog(MohReasonsForPepRule.class);

	public static final String TOKEN = "MOH Reasons For PEP";

	public static final String METHOD_OF_HIV_EXPOSURE = "METHOD OF HIV EXPOSURE";
	public static final String SEXUAL_ASSAULT = "SEXUAL ASSAULT";
	public static final String SPOUSES_PARTNER_SUSPECTED_HIV = "SPOUSES PARTNER SUSPECTED HIV+";
	public static final String OCCUPATIONAL_EXPOSURE = "OCCUPATIONAL EXPOSURE";
	public static final String PHYSICAL_ASSAULT = "PHYSICAL ASSAULT";
	public static final String SUSPECTED_PAST_HIV_EXPOSURE = "SUSPECTED PAST HIV EXPOSURE";
	public static final String CONTAMINATED_NEEDLE_STICK = "CONTAMINATED NEEDLE STICK";
	public static final String BLOOD_TRANSFUSION = "BLOOD TRANSFUSION";
	public static final String INTRAVENOUS_DRUG_USE = "INTRAVENOUS DRUG USE";
	public static final String OTHER_NON_CODED = "OTHER NON-CODED";
	public static final String UNKNOWN = "UNKNOWN";
	public static final String POST_EXPOSURE_INITIAL_FORM = "PEPINITIAL";
	public static final String POST_EXPOSURE_RETURN_FORM = "PEPRETURN";

	private static final List<OpenmrsObject> questionConcepts = Collections.<OpenmrsObject>singletonList(MohCacheUtils.getConcept(METHOD_OF_HIV_EXPOSURE));

	private static final List<OpenmrsObject> encounterTypes = Arrays.<OpenmrsObject>asList(new EncounterType[]{
			MohCacheUtils.getEncounterType(POST_EXPOSURE_INITIAL_FORM),
			MohCacheUtils.getEncounterType(POST_EXPOSURE_RETURN_FORM)
	});

	@Autowired
	MohCoreService mohCoreService;

	public Result evaluate(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		List<String> pepReasons = new ArrayList<String>();

		//pull relevant observations then loop while checking concepts
		Map<String, Collection<OpenmrsObject>> obsRestrictions = new HashMap<String, Collection<OpenmrsObject>>();
		obsRestrictions.put("concept", questionConcepts);
		obsRestrictions.put("encounter.encounterType", encounterTypes);
		MohFetchRestriction mohFetchRestriction = new MohFetchRestriction();
		List<Obs> observations = mohCoreService.getPatientObservations(patientId, obsRestrictions, mohFetchRestriction);

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
				} else if (value.equals(MohCacheUtils.getConcept(SUSPECTED_PAST_HIV_EXPOSURE))) {
					pepReasons.add(SUSPECTED_PAST_HIV_EXPOSURE);
				} else if (value.equals(MohCacheUtils.getConcept(CONTAMINATED_NEEDLE_STICK))) {
					pepReasons.add(CONTAMINATED_NEEDLE_STICK);
				} else if (value.equals(MohCacheUtils.getConcept(BLOOD_TRANSFUSION))) {
					pepReasons.add(BLOOD_TRANSFUSION);
				} else if (value.equals(MohCacheUtils.getConcept(INTRAVENOUS_DRUG_USE))) {
					pepReasons.add(INTRAVENOUS_DRUG_USE);
				} else if (obs.getValueCoded().equals(MohCacheUtils.getConcept(OTHER_NON_CODED))) {
					pepReasons.add(OTHER_NON_CODED);
				} else if (obs.getValueCoded().equals(MohCacheUtils.getConcept(UNKNOWN))) {
					pepReasons.add(UNKNOWN);
				}
			}
		}

		boolean first = true;
		StringBuilder sb = new StringBuilder();
		for (String reason : pepReasons) {
			if (!first) {
				sb.append(";");
			} else {
				first = false;
			}
			sb.append(reason);
		}

		return new Result(sb.toString());
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
