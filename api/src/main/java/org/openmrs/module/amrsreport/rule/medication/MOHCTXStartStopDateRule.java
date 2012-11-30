package org.openmrs.module.amrsreport.rule.medication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.result.Result;
import org.openmrs.module.amrsreport.cache.MohCacheUtils;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;

import java.util.Map;


/**
 * Author ningosi
 */
public class MOHCTXStartStopDateRule extends DrugStartStopDateRule {

	private static final Log log = LogFactory.getLog(MOHCTXStartStopDateRule.class);

	public MOHCTXStartStopDateRule() {
		this.startConcept = MohCacheUtils.getConcept(MohEvaluableNameConstants.PCP_PROPHYLAXIS_STARTED);
		this.stopConcept = MohCacheUtils.getConcept(MohEvaluableNameConstants.REASON_PCP_PROPHYLAXIS_STOPPED);
		this.TOKEN = "MOH CTX Start-Stop Date";
	}

	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	public Result evaluate(final LogicContext context, final Integer patientId, final Map<String, Object> parameters) throws LogicException {
		return this.getResult(patientId);
	}
}
