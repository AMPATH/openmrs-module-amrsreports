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


/**
 * rule for calculating CTX start and stop dates
 */
public class MOHCTXStartStopDateRule extends DrugStartStopDateRule {

	public MOHCTXStartStopDateRule() {
		this.startConcepts = Collections.singletonList((OpenmrsObject) MohCacheUtils.getConcept(MohEvaluableNameConstants.PCP_PROPHYLAXIS_STARTED));

		this.stopConcepts = Arrays.<OpenmrsObject>asList(
				MohCacheUtils.getConcept(MohEvaluableNameConstants.REASON_PCP_PROPHYLAXIS_STOPPED),
				MohCacheUtils.getConcept(MohEvaluableNameConstants.REASON_PCP_PROPHYLAXIS_STOPPED_DETAILED)
		);

		this.TOKEN = "MOH CTX Start-Stop Date";
	}

	/**
	 * @should start on PCP_PROPHYLAXIS_STARTED with not null answer
	 * @should not start on PCP_PROPHYLAXIS_STARTED with null answer
	 * @should stop on REASON_PCP_PROPHYLAXIS_STOPPED with not null answer
	 * @should not stop on REASON_PCP_PROPHYLAXIS_STOPPED with null answer
	 * @should stop on REASON_PCP_PROPHYLAXIS_STOPPED_DETAILED with not null answer
	 * @should not stop on REASON_PCP_PROPHYLAXIS_STOPPED_DETAILED with null answer
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	public Result evaluate(final LogicContext context, final Integer patientId, final Map<String, Object> parameters) throws LogicException {
		return this.getResult(patientId);
	}

	@Override
	protected boolean validateStartObs(Obs obs) {
		return MohRuleUtils.compareConceptToName(obs.getConcept(), MohEvaluableNameConstants.PCP_PROPHYLAXIS_STARTED) &&
				obs.getValueCoded() != null;
	}

	@Override
	protected boolean validateStopObs(Obs obs) {
		return (obs.getValueCoded() != null) && (
				MohRuleUtils.compareConceptToName(obs.getConcept(), MohEvaluableNameConstants.REASON_PCP_PROPHYLAXIS_STOPPED)
						||
						MohRuleUtils.compareConceptToName(obs.getConcept(), MohEvaluableNameConstants.REASON_PCP_PROPHYLAXIS_STOPPED_DETAILED)
		);
	}
}
