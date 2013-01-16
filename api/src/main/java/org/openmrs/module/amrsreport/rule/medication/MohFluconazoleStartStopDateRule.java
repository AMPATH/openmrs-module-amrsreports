package org.openmrs.module.amrsreport.rule.medication;

import org.openmrs.Obs;
import org.openmrs.OpenmrsObject;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.result.Result;
import org.openmrs.module.amrsreport.cache.MohCacheUtils;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreport.rule.util.MohRuleUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class MohFluconazoleStartStopDateRule extends DrugStartStopDateRule {

	public MohFluconazoleStartStopDateRule() {

		this.startConcepts = Arrays.<OpenmrsObject>asList(
				MohCacheUtils.getConcept(MohEvaluableNameConstants.CRYPTOCOCCAL_TREATMENT_PLAN),
				MohCacheUtils.getConcept(MohEvaluableNameConstants.CRYPTOCOSSUS_TREATMENT_STARTED)
		);

		this.stopConcepts = Collections.singletonList((OpenmrsObject) MohCacheUtils.getConcept(MohEvaluableNameConstants.CRYPTOCOCCAL_TREATMENT_PLAN));

		this.TOKEN = "MOH Fluconazole Start-Stop Date";
	}

	/**
	 * MOH Fluconazole Start-Stop Date
	 * <p/>
	 * For Fluconazole startdate: Use the observation date when CRYPTOCOCCAL TREATMENT PLAN (1277)=START DRUGS (1256) or
	 * CRYPTOCOSSUS TREATMENT STARTED (1278)= FLUCONAZOLE (747) were captured.
	 * <p/>
	 * For Fluconazole stopdate: Use the observation date when CRYPTOCOCCAL TREATMENT PLAN (1277)=STOP ALL (1260) was captured
	 *
	 * @should start on CRYPTOCOCCAL_TREATMENT_PLAN is START_DRUGS
	 * @should start on CRYPTOCOSSUS_TREATMENT_STARTED is FLUCONAZOLE
	 * @should stop on CRYPTOCOCCAL_TREATMENT_PLAN is STOP_ALL
	 * @should start and stop on CRYPTOCOCCAL_TREATMENT_PLAN with correct values
	 */
	@Override
	public Result evaluate(final LogicContext context, final Integer patientId, final Map<String, Object> parameters) throws LogicException {
		return this.getResult(patientId);
	}

	@Override
	protected boolean validateStartObs(Obs obs) {
		return (MohRuleUtils.compareConceptToName(obs.getConcept(), MohEvaluableNameConstants.CRYPTOCOCCAL_TREATMENT_PLAN) &&
				MohRuleUtils.compareConceptToName(obs.getValueCoded(), MohEvaluableNameConstants.START_DRUGS))
				||
				(MohRuleUtils.compareConceptToName(obs.getConcept(), MohEvaluableNameConstants.CRYPTOCOSSUS_TREATMENT_STARTED) &&
						MohRuleUtils.compareConceptToName(obs.getValueCoded(), MohEvaluableNameConstants.FLUCONAZOLE));
	}

	@Override
	protected boolean validateStopObs(Obs obs) {
		return MohRuleUtils.compareConceptToName(obs.getConcept(), MohEvaluableNameConstants.CRYPTOCOCCAL_TREATMENT_PLAN) &&
				MohRuleUtils.compareConceptToName(obs.getValueCoded(), MohEvaluableNameConstants.STOP_ALL);
	}

}