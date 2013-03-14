package org.openmrs.module.amrsreports.rule.medication;

import org.openmrs.Obs;
import org.openmrs.OpenmrsObject;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.result.Result;
import org.openmrs.module.amrsreports.cache.MohCacheUtils;
import org.openmrs.module.amrsreports.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreports.rule.util.MohRuleUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Map;


/**
 * rule for calculating CTX start and stop dates
 */
public class MOHCTXStartStopDateRule extends DrugStartStopDateRule {

	public static final String TOKEN = "MOH CTX Start-Stop Date";

	public MOHCTXStartStopDateRule() {
		this.startConcepts = Collections.singletonList((OpenmrsObject) MohCacheUtils.getConcept(MohEvaluableNameConstants.PCP_PROPHYLAXIS_STARTED));

		this.stopConcepts = Arrays.<OpenmrsObject>asList(
				MohCacheUtils.getConcept(MohEvaluableNameConstants.REASON_PCP_PROPHYLAXIS_STOPPED),
				MohCacheUtils.getConcept(MohEvaluableNameConstants.REASON_PCP_PROPHYLAXIS_STOPPED_DETAILED)
		);
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

		// get evaluation date from logic context
		Date evaluationDate = context.getIndexDate();

		return this.getResult(patientId, evaluationDate);
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

	@Override
	protected String getEvaluableToken() {
		return TOKEN;
	}
}
