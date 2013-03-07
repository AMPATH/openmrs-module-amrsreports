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

package org.openmrs.module.amrsreports.rule.observation;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.result.Result;
import org.openmrs.module.amrsreports.rule.MohEvaluableConstants;
import org.openmrs.module.amrsreports.rule.MohEvaluableParameter;
import org.openmrs.module.amrsreports.rule.util.MohRuleUtils;
import org.openmrs.module.amrsreports.service.MohCoreService;
import org.openmrs.module.amrsreports.util.MohFetchRestriction;

/**
 * This rule will pull encounter for a patient based on the restrictions parameters. Available restrictions are: <ul> <li>encounter: to restrict
 * returned observations based on the encounter</li> <li>location: to restrict returned observations based on the location</li> <li>concept: to
 * restrict returned observations based on the concept</li> <li>valueCoded: to restrict returned observations based on the coded values</li> </ul>
 * <p/>
 * Both encounter restriction rule and observation restriction rule supports size and order to limit the returned list size and how it is ordered.
 */
public class MohObsWithObjectRestrictionRule extends MohObsWithRestrictionRule {

	private static final Log log = LogFactory.getLog(MohObsWithObjectRestrictionRule.class);

	public static final String TOKEN = "MOH Obs Object Restrictions";

	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected Result evaluate(final LogicContext context, final Integer patientId, final Map<String, Object> parameters) throws LogicException {

		Result result = new Result();

		Map<String, Collection<OpenmrsObject>> restrictions = new HashMap<String, Collection<OpenmrsObject>>();

		Object encounterObjects = parameters.get(MohEvaluableConstants.OBS_ENCOUNTER);
		if (MohRuleUtils.isValidCollectionObject(encounterObjects))
			restrictions.put(MohEvaluableConstants.OBS_ENCOUNTER, (Collection<OpenmrsObject>) encounterObjects);

		Object conceptObjects = parameters.get(MohEvaluableConstants.OBS_CONCEPT);
		if (MohRuleUtils.isValidCollectionObject(conceptObjects))
			restrictions.put(MohEvaluableConstants.OBS_CONCEPT, (Collection<OpenmrsObject>) conceptObjects);

		Object valueCodedObjects = parameters.get(MohEvaluableConstants.OBS_VALUE_CODED);
		if (MohRuleUtils.isValidCollectionObject(valueCodedObjects))
			restrictions.put(MohEvaluableConstants.OBS_VALUE_CODED, (Collection<OpenmrsObject>) valueCodedObjects);

		MohFetchRestriction mohFetchRestriction = new MohFetchRestriction();

		Object sizeObject = parameters.get(MohEvaluableConstants.OBS_FETCH_SIZE);
		if (MohRuleUtils.isValidSizeObject(sizeObject))
			mohFetchRestriction.setSize(NumberUtils.toInt(sizeObject.toString()));

		Object orderObject = parameters.get(MohEvaluableConstants.OBS_FETCH_ORDER);
		if (MohRuleUtils.isValidOrderObject(orderObject))
			mohFetchRestriction.setFetchOrdering(orderObject.toString());

		MohCoreService mohCoreService = Context.getService(MohCoreService.class);
		List<Obs> observations = mohCoreService.getPatientObservations(patientId, restrictions, mohFetchRestriction);

		for (Obs observation : observations)
			result.add(new Result(observation));

		return result;
	}

	/**
	 * Get the definition of each parameter that should be passed to this rule execution
	 *
	 * @return all parameter that applicable for each rule execution
	 */
	@Override
	public Set<MohEvaluableParameter> getEvaluationParameters() {
		Set<MohEvaluableParameter> mohEvaluableParameters = new HashSet<MohEvaluableParameter>();
		mohEvaluableParameters.add(MohEvaluableConstants.OPTIONAL_OBS_CONCEPT_PARAMETER_DEFINITION);
		mohEvaluableParameters.add(MohEvaluableConstants.OPTIONAL_OBS_ENCOUNTER_PARAMETER_DEFINITION);
		mohEvaluableParameters.add(MohEvaluableConstants.OPTIONAL_OBS_VALUE_CODED_PARAMETER_DEFINITION);
		mohEvaluableParameters.add(MohEvaluableConstants.OPTIONAL_OBS_FETCH_ORDER_PARAMETER_DEFINITION);
		mohEvaluableParameters.add(MohEvaluableConstants.OPTIONAL_OBS_FETCH_SIZE_PARAMETER_DEFINITION);
		// TODO: in the future we might need to add the location here. but for now, there's no need to do that yet
		return mohEvaluableParameters;
	}

	/**
	 * Get the token name of the rule that can be used to reference the rule from LogicService
	 *
	 * @return the token name
	 */
	@Override
	protected String getEvaluableToken() {
		return TOKEN;
	}
}
