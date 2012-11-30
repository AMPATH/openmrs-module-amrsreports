package org.openmrs.module.amrsreport.rule.medication;

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
import org.openmrs.module.amrsreport.service.MohCoreService;
import org.openmrs.module.amrsreport.util.MohFetchOrdering;
import org.openmrs.module.amrsreport.util.MohFetchRestriction;
import org.openmrs.util.OpenmrsUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MohFluconazoleStartStopDateRule extends DrugStartStopDateRule {

	public static final String TOKEN = "MOH Fluconazole Start-Stop Date";

	private static final Concept StartDrugs = MohCacheUtils.getConcept(MohEvaluableNameConstants.START_DRUGS);
	private static final Concept FluconazoleDrug = MohCacheUtils.getConcept(MohEvaluableNameConstants.FLUCONAZOLE);
	private static final Concept StopDrugs = MohCacheUtils.getConcept(MohEvaluableNameConstants.STOP_ALL);

	private static final List<OpenmrsObject> questionConcepts = Arrays.<OpenmrsObject>asList(
			new Concept[]{
					MohCacheUtils.getConcept(MohEvaluableNameConstants.CRYPTOCOCCAL_TREATMENT_PLAN),
					MohCacheUtils.getConcept(MohEvaluableNameConstants.CRYPTOCOSSUS_TREATMENT_STARTED)
			});

	/**
	 * MOH Fluconazole Start-Stop Date
	 * <p/>
	 * For Fluconazole startdate: Use the observation date when CRYPTOCOCCAL TREATMENT PLAN (1277)=START DRUGS (1256) or
	 * CRYPTOCOSSUS TREATMENT STARTED (1278)= FLUCONAZOLE (747) were captured.
	 * <p/>
	 * For Fluconazole stopdate: Use the observation date when CRYPTOCOCCAL TREATMENT PLAN (1277)=STOP ALL (1260) was captured
	 */
	@Override
	protected Result evaluate(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		// set up query for observations in order by ascending date
		Map<String, Collection<OpenmrsObject>> restrictions = new HashMap<String, Collection<OpenmrsObject>>();
		restrictions.put("concept", questionConcepts);
		MohFetchRestriction fetchRestriction = new MohFetchRestriction();
		fetchRestriction.setFetchOrdering(MohFetchOrdering.ORDER_ASCENDING);

		// get the start observations
		List<Obs> observations = Context.getService(MohCoreService.class).getPatientObservations(patientId, restrictions, fetchRestriction);

		List<Obs> startObs = new ArrayList<Obs>();
		List<Obs> stopObs = new ArrayList<Obs>();

		// find start and stop observations
		for (Obs obs : observations) {
			if (OpenmrsUtil.nullSafeEquals(StartDrugs, obs.getValueCoded()) || OpenmrsUtil.nullSafeEquals(FluconazoleDrug, obs.getValueCoded())) {
				startObs.add(obs);
			} else if (OpenmrsUtil.nullSafeEquals(StopDrugs, obs.getValueCoded())) {
				stopObs.add(obs);
			}
		}
		return buildResultFromObservations(startObs, stopObs);
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