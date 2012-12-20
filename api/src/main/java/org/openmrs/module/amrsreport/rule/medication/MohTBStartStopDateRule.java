/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.amrsreport.rule.medication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.OpenmrsObject;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.result.Result;
import org.openmrs.module.amrsreport.cache.MohCacheUtils;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;

import java.util.Collections;
import java.util.Map;

/**
 * Logic Rule for finding start and stop dates for TB treatment
 */
public class MohTBStartStopDateRule extends DrugStartStopDateRule {

	private static final Log log = LogFactory.getLog(MohTBStartStopDateRule.class);

	public MohTBStartStopDateRule() {
		this.startConcepts = Collections.singletonList((OpenmrsObject) MohCacheUtils.getConcept(MohEvaluableNameConstants.TUBERCULOSIS_TREATMENT_STARTED));
		this.stopConcepts = Collections.singletonList((OpenmrsObject) MohCacheUtils.getConcept(MohEvaluableNameConstants.TUBERCULOSIS_TREATMENT_COMPLETED_DATE));
		this.TOKEN = "MOH TB Start-Stop Date";
	}

	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	public Result evaluate(final LogicContext context, final Integer patientId, final Map<String, Object> parameters) throws LogicException {
		return this.getResult(patientId);
	}

	@Override
	protected boolean validateStartObs(Obs obs) {
		return true;
	}

	@Override
	protected boolean validateStopObs(Obs obs) {
		return true;
	}
}
