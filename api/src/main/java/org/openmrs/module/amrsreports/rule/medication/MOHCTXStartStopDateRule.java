package org.openmrs.module.amrsreports.rule.medication;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.OpenmrsObject;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.result.Result;
import org.openmrs.module.amrsreports.cache.MohCacheUtils;
import org.openmrs.module.amrsreports.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreports.rule.util.MohRuleUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * rule for calculating CTX start and stop dates
 */
public class MOHCTXStartStopDateRule extends DrugStartStopDateRule {

	public static final String TOKEN = "MOH CTX Start-Stop Date";

	public MOHCTXStartStopDateRule() {
		this.startConcepts = Arrays.<OpenmrsObject>asList(
				MohCacheUtils.getConcept(MohEvaluableNameConstants.PCP_PROPHYLAXIS_STARTED),
				MohCacheUtils.getConcept(MohEvaluableNameConstants.CURRENT_MEDICATIONS),
				MohCacheUtils.getConcept(MohEvaluableNameConstants.PATIENT_REPORTED_CURRENT_PCP_PROPHYLAXIS)
		);

		this.stopConcepts = Arrays.<OpenmrsObject>asList(
				MohCacheUtils.getConcept(MohEvaluableNameConstants.REASON_PCP_PROPHYLAXIS_STOPPED)
		);
	}

	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 *
	 * @should start on PCP_PROPHYLAXIS_STARTED with not null answer
	 * @should not start on PCP_PROPHYLAXIS_STARTED with null answer
	 * @should start on CURRENT_MEDICATIONS equal to TRIMETHOPRIM_AND_SULFAMETHOXAZOLE
	 * @should not start on CURRENT_MEDICATIONS equal to something other than TRIMETHOPRIM_AND_SULFAMETHOXAZOLE
	 * @should start on PATIENT_REPORTED_CURRENT_PCP_PROPHYLAXIS equal to TRIMETHOPRIM_AND_SULFAMETHOXAZOLE
	 * @should not start on PATIENT_REPORTED_CURRENT_PCP_PROPHYLAXIS equal to something other than TRIMETHOPRIM_AND_SULFAMETHOXAZOLE
	 * @should stop on REASON_PCP_PROPHYLAXIS_STOPPED with not null answer
	 * @should not stop on REASON_PCP_PROPHYLAXIS_STOPPED with null answer
	 */
	public Result evaluate(final LogicContext context, final Integer patientId, final Map<String, Object> parameters) throws LogicException {

		// get evaluation date from logic context
		Date evaluationDate = context.getIndexDate();

		return this.getResult(patientId, evaluationDate);
	}

	@Override
	protected boolean validateStartObs(Obs obs) {
		Concept c = obs.getConcept();
		Concept v = obs.getValueCoded();

		return (MohRuleUtils.compareConceptToName(c, MohEvaluableNameConstants.PCP_PROPHYLAXIS_STARTED) && v != null)
				|| (MohRuleUtils.compareConceptToName(c, MohEvaluableNameConstants.CURRENT_MEDICATIONS)
				&& MohRuleUtils.compareConceptToName(v, MohEvaluableNameConstants.TRIMETHOPRIM_AND_SULFAMETHOXAZOLE))
				|| (MohRuleUtils.compareConceptToName(c, MohEvaluableNameConstants.PATIENT_REPORTED_CURRENT_PCP_PROPHYLAXIS)
				&& MohRuleUtils.compareConceptToName(v, MohEvaluableNameConstants.TRIMETHOPRIM_AND_SULFAMETHOXAZOLE));
	}

	@Override
	protected boolean validateStopObs(Obs obs) {
		return MohRuleUtils.compareConceptToName(obs.getConcept(), MohEvaluableNameConstants.REASON_PCP_PROPHYLAXIS_STOPPED)
				&& obs.getValueCoded() != null;
	}

	@Override
	protected String getEvaluableToken() {
		return TOKEN;
	}
}
