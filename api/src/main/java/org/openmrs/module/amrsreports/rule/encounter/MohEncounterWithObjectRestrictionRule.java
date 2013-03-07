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
package org.openmrs.module.amrsreports.rule.encounter;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
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

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This rule will pull encounter for a patient based on the restrictions parameters. Available restrictions are: <ul> <li>encounterType: to restrict
 * returned encounters based on the encounter type</li> <li>location: to restrict returned encounters based on the encounter location</li>
 * <li>provider: to restrict returned encounters based on the encounter provider</li> </ul>
 * <p/>
 * Both encounter restriction rule and observation restriction rule supports size and order to limit the returned list size and how it is ordered.
 */
public class MohEncounterWithObjectRestrictionRule extends MohEncounterWithRestrictionRule {

	private static final Log log = LogFactory.getLog(MohEncounterWithObjectRestrictionRule.class);

	public static final String TOKEN = "MOH Encounter Object Restrictions";

	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected Result evaluate(final LogicContext context, final Integer patientId, final Map<String, Object> parameters) throws LogicException {

		// this rule can process the following parameters:
		// - list of encounter type (map key: EvaluableConstants.ENCOUNTER_TYPE)
		// - list of location (map key: EvaluableConstants.LOCATION)
		// - number of encounter that should be returned (map key: EvaluableConstants.ENCOUNTER_FETCH_SIZE)
		// - ordering on the encounter datetime (map key: EvaluableConstants.ENCOUNTER_FETCH_ORDER)

		Result result = new Result();
		// we will pass this restrictions to core service to get the correct encounters
		Map<String, Collection<OpenmrsObject>> restrictions = new HashMap<String, Collection<OpenmrsObject>>();

		// get list of encounter types from the parameters
		Object encounterTypeObjects = parameters.get(MohEvaluableConstants.ENCOUNTER_TYPE);
		if (MohRuleUtils.isValidCollectionObject(encounterTypeObjects))
			restrictions.put(MohEvaluableConstants.ENCOUNTER_TYPE, (Collection<OpenmrsObject>) encounterTypeObjects);
		// get list of locations from the parameters
		Object locationObjects = parameters.get(MohEvaluableConstants.ENCOUNTER_LOCATION);
		if (MohRuleUtils.isValidCollectionObject(locationObjects))
			restrictions.put(MohEvaluableConstants.ENCOUNTER_LOCATION, (Collection<OpenmrsObject>) locationObjects);
		// get list of providers from the parameters (currently this is not supported)
		Object providerObjects = parameters.get(MohEvaluableConstants.ENCOUNTER_PROVIDER);
		if (MohRuleUtils.isValidCollectionObject(providerObjects))
			restrictions.put(MohEvaluableConstants.ENCOUNTER_PROVIDER, (Collection<OpenmrsObject>) providerObjects);

		// this fetch restriction will define how many records should be returned and how they will be ordered
		MohFetchRestriction mohFetchRestriction = new MohFetchRestriction();
		// specify how many records should be returned
		Object sizeObject = parameters.get(MohEvaluableConstants.ENCOUNTER_FETCH_SIZE);
		if (MohRuleUtils.isValidSizeObject(sizeObject))
			mohFetchRestriction.setSize(NumberUtils.toInt(sizeObject.toString()));
		// specify how the record will be ordered (ascending or descending by encounter datetime)
		Object orderObject = parameters.get(MohEvaluableConstants.ENCOUNTER_FETCH_ORDER);
		if (MohRuleUtils.isValidOrderObject(orderObject))
			mohFetchRestriction.setFetchOrdering(orderObject.toString());

		// call the core service to search for the matching encounters
		MohCoreService mohCoreService = Context.getService(MohCoreService.class);
		// pass the patient id, restrictions map and the fetch restriction in the service call
		List<Encounter> encounters = mohCoreService.getPatientEncounters(patientId, restrictions, mohFetchRestriction);

		if (log.isDebugEnabled())
			log.debug("Patient: " + patientId + ", encounters size:" + CollectionUtils.size(encounters));

		// iterate over the encounters and then wrap them into a logic's result object
		// check the result object implementation to see how to create result object and how to pull data from them
		for (Encounter encounter : encounters) {
			Date encounterDatetime = encounter.getEncounterDatetime();
			EncounterType type = encounter.getEncounterType();
			String encounterTypeName = StringUtils.EMPTY;
			if (type != null)
				encounterTypeName = type.getName();
			result.add(new Result(encounterDatetime, encounterTypeName, encounter));
		}

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
		mohEvaluableParameters.add(MohEvaluableConstants.OPTIONAL_ENCOUNTER_TYPE_PARAMETER_DEFINITION);
		mohEvaluableParameters.add(MohEvaluableConstants.OPTIONAL_ENCOUNTER_FETCH_SIZE_PARAMETER_DEFINITION);
		mohEvaluableParameters.add(MohEvaluableConstants.OPTIONAL_ENCOUNTER_FETCH_ORDER_PARAMETER_DEFINITION);
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
