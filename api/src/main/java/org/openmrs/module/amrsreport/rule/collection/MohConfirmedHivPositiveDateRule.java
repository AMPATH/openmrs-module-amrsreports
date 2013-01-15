package org.openmrs.module.amrsreport.rule.collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
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
import org.openmrs.module.amrsreport.service.MohCoreService;
import org.openmrs.module.amrsreport.util.MohFetchOrdering;
import org.openmrs.module.amrsreport.util.MohFetchRestriction;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author jmwogi
 */
public class MohConfirmedHivPositiveDateRule extends MohEvaluableRule {

	private static final Log log = LogFactory.getLog(MohConfirmedHivPositiveDateRule.class);

	public static final String TOKEN = "MOH Confirmed HIV Positive Date";

	private static final Concept CONCEPT_ENZYME = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_ENZYME_IMMUNOASSAY_QUALITATIVE);
	private static final Concept CONCEPT_RAPID = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_RAPID_TEST_QUALITATIVE);
	private static final Concept CONCEPT_POSITIVE = MohCacheUtils.getConcept(MohEvaluableNameConstants.POSITIVE);

	private static final MohCoreService mohCoreService = Context.getService(MohCoreService.class);

	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, org.openmrs.Patient,
	 *      java.util.Map)
	 */
	public Result evaluate(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {

		//pull relevant observations then loop while checking concepts
		Map<String, Collection<OpenmrsObject>> obsRestrictions = new HashMap<String, Collection<OpenmrsObject>>();
		obsRestrictions.put("concept", Arrays.<OpenmrsObject>asList(new Concept[]{CONCEPT_ENZYME, CONCEPT_RAPID}));
		obsRestrictions.put("valueCoded", Arrays.<OpenmrsObject>asList(new Concept[]{CONCEPT_POSITIVE}));

		MohFetchRestriction mohFetchRestriction = new MohFetchRestriction();

		List<Obs> observations = mohCoreService.getPatientObservations(patientId, obsRestrictions, mohFetchRestriction);

		if (!observations.isEmpty()) {
			Obs obs = observations.get(0);
			return new Result(obs.getObsDatetime());
		}

		// return the first encounter date if nothing else is found
		Date firstEncounterDate = null;

		// set up query for encounters in order by ascending date
		Map<String, Collection<OpenmrsObject>> restrictions = new HashMap<String, Collection<OpenmrsObject>>();
		mohFetchRestriction = new MohFetchRestriction();
		mohFetchRestriction.setFetchOrdering(MohFetchOrdering.ORDER_ASCENDING);

		// get the encounters
		List<Encounter> encounters = mohCoreService.getPatientEncounters(patientId, restrictions, mohFetchRestriction);

		// pull the first encounter date
		if (!encounters.isEmpty()) {
			firstEncounterDate = encounters.get(0).getEncounterDatetime();
		}

		return new Result(firstEncounterDate);
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

	private static class SortByDateComparator implements Comparator<Encounter> {

		@Override
		public int compare(Encounter a, Encounter b) {
			Encounter ao = (Encounter) a;
			Encounter bo = (Encounter) b;
			return ao.getDateCreated().compareTo(bo.getDateCreated());
		}
	}

}