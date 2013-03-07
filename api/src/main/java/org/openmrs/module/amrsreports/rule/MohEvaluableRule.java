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
package org.openmrs.module.amrsreports.rule;



import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.rule.AbstractRule;

public abstract class MohEvaluableRule extends AbstractRule {

	private static final Log log = LogFactory.getLog(MohEvaluableRule.class);

	/**
	 * Get parameters that's declared by the class as the evaluable parameters for the EvaluableRule
	 *
	 * @param parameters
	 * @return
	 */
	
	private Map<String, Object> getEffectiveParameters(Map<String, Object> parameters) {
		Map<String, Object> effectiveParameters = new HashMap<String, Object>();
		for (MohEvaluableParameter parameter : getEvaluationParameters()) {
			Object o = parameters.get(parameter.getName());
			if (o != null)
				effectiveParameters.put(parameter.getName(), o);
		}
		return effectiveParameters;
	}

	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Override()
	public Result eval(final LogicContext context, final Integer patientId, final Map<String, Object> parameters) throws LogicException {
		validateParameters(parameters);

		Result results;
		if (!cacheable())
			results = evaluate(context, patientId, parameters);
		else {
			String token = getEvaluableToken();
			Map<String, Object> effectiveParameters = getEffectiveParameters(parameters);

			MohResultCacheInstance cacheInstance = MohResultCacheInstance.getInstance();
			MohResultCacheKey cacheKey = new MohResultCacheKey(patientId, token, effectiveParameters);
			results = cacheInstance.getResult(cacheKey);

			if (log.isDebugEnabled()) {
				log.debug("Searching cache for token: " + token + " and effective cache parameters: " + effectiveParameters);
				log.debug("Cache searching output: " + results);
			}

			if (results == null) {
				results = evaluate(context, patientId, parameters);
				cacheResults(cacheKey, results);
			}
		}

		return results;
	}

	private void cacheResults(final MohResultCacheKey cacheKey, final Result results) {
		MohResultCacheInstance cacheInstance = MohResultCacheInstance.getInstance();
		// always cache the initial results
		cacheInstance.addResult(cacheKey, results);

		Map<String, Object> parameters = cacheKey.getParameters();
		//  see if we need to create derived cache entries based on the obs size or encounter size
		String sizeParameter = MohEvaluableConstants.OBS_FETCH_SIZE;
		if (parameters.containsKey(MohEvaluableConstants.ENCOUNTER_FETCH_SIZE))
			sizeParameter = MohEvaluableConstants.ENCOUNTER_FETCH_SIZE;

		Object object = parameters.get(sizeParameter);
		// if we have more than one results then we need to slice em and cache them
		Integer size = results.size();
		if (object != null)
			size = NumberUtils.toInt(object.toString());

		Integer counter = 0;
		// see the first two lines of this method body
		// we just need to create cache for (size - 1) because we already save the cache for the (size)
		do {
			// create a copy of the effective parameters on the key
			Map<String, Object> effectiveParameters = new HashMap<String, Object>();
			for (String parameterKey : parameters.keySet())
				effectiveParameters.put(parameterKey, parameters.get(parameterKey));
			effectiveParameters.put(sizeParameter, ++counter);

			Result derivedResults = new Result();

			Integer resultCounter = 0;
			while (resultCounter < results.size() && resultCounter < counter)
				derivedResults.add(results.get(resultCounter++));

			if (log.isDebugEnabled()) {
				log.debug("Creating cache element for token: " + cacheKey.getToken() + " and parameters: " + effectiveParameters);
				log.debug("Cache created with results: " + derivedResults);
			}

			MohResultCacheKey derivedCacheKey = new MohResultCacheKey(cacheKey.getPatientId(), cacheKey.getToken(), effectiveParameters);
			cacheInstance.addResult(derivedCacheKey, derivedResults);
		} while (counter < size);
	}

	/**
	 * Get the definition of each parameter that should be passed to this rule execution
	 *
	 * @return all parameter that applicable for each rule execution
	 */
	public Set<MohEvaluableParameter> getEvaluationParameters() {
		return new HashSet<MohEvaluableParameter>();
	}

	/**
	 * Whether the results of the rule should be cached or not
	 *
	 * @return true if the system should put the results into the caching system
	 */
	protected Boolean cacheable() {
		return Boolean.FALSE;
	}

	/**
	 * Check every parameters to ensure rule executed correctly
	 *
	 * @param parameters the parameters for rule execution
	 * @throws LogicException when any of the parameter definition is not satisfied by the parameters
	 * @see MohEvaluableParameter
	 */
	protected void validateParameters(final Map<String, Object> parameters) throws LogicException{
		for (MohEvaluableParameter mohEvaluableParameter : getEvaluationParameters()) {
			
			// if object for the parameter is not found and the parameter is required, then throw missing parameter exception
			Object o = parameters.get(mohEvaluableParameter.getName());
				if (o == null && mohEvaluableParameter.isRequired())
					throw new LogicException("Insufficient parameter to execute rule. Missing parameter: " + mohEvaluableParameter.getName());
				// if the object is found but the type is different, then throw invalid parameter type exception
				if (o != null && !ClassUtils.isAssignable(o.getClass(), mohEvaluableParameter.getParameterClass()))
					throw new LogicException("Invalid parameter type to execute rule. " +
							"Expecting " + mohEvaluableParameter.getParameterClass().getName() + " for " + mohEvaluableParameter.getName() +
							", but getting " + o.getClass().getName() + " instead.");
		}
	}

	/**
	 * @param context
	 * @param patientId
	 * @param parameters
	 * @return
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	protected abstract Result evaluate(final LogicContext context, final Integer patientId, final Map<String, Object> parameters);

	/**
	 * Get the token name of the rule that can be used to reference the rule from LogicService
	 *
	 * @return the token name
	 */
	protected abstract String getEvaluableToken();

}
