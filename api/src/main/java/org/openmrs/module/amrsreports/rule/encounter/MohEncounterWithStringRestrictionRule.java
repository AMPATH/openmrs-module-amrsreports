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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.result.Result;
import org.openmrs.module.amrsreports.cache.MohCacheUtils;
import org.openmrs.module.amrsreports.rule.MohEvaluableConstants;
import org.openmrs.module.amrsreports.rule.MohEvaluableParameter;
import org.openmrs.module.amrsreports.rule.util.MohRuleUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @see MohEncounterWithObjectRestrictionRule
 */
public class MohEncounterWithStringRestrictionRule extends MohEncounterWithRestrictionRule {

	private static final Log log = LogFactory.getLog(MohEncounterWithStringRestrictionRule.class);

	public static final String TOKEN = "MOH Encounter String Restrictions";

	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Override
	protected Result evaluate(final LogicContext context, final Integer patientId, final Map<String, Object> parameters) throws LogicException {

		// this rule can process the following parameters:
		// - list of encounter type names (map key: EvaluableConstants.ENCOUNTER_TYPE)
		// - list of location names (map key: EvaluableConstants.LOCATION)

		// list of actual encounter types after we convert the encounter type name into the actual encounter type object.
		List<EncounterType> encounterTypeList = new ArrayList<EncounterType>();
		// get the encounter type names from the rule execution parameters
		Object paramTypeObjects = parameters.get(MohEvaluableConstants.ENCOUNTER_TYPE);
		// validate the above object to make sure it's a collection object
		if (MohRuleUtils.isValidCollectionObject(paramTypeObjects)) {
			// the object is a valid collection object, cast them into a collection object
			Collection typeObjects = (Collection) paramTypeObjects;
			// iterate over the encounter type names
			for (Object typeObject : typeObjects) {
				// get the actual encounter type and put them in the list
				EncounterType encounterType = MohCacheUtils.getEncounterType(String.valueOf(typeObject));
				if (encounterType != null)
					encounterTypeList.add(encounterType);
			}
			// this check will prevent user from passing random string in the parameters
			if (CollectionUtils.isEmpty(encounterTypeList))
				throw new LogicException("Passing invalid list of encounter types attempted. Processed parameters are " + typeObjects);
			// replace the parameter from list of encounter type names with list of encounter types
			parameters.put(MohEvaluableConstants.ENCOUNTER_TYPE, encounterTypeList);
		}

		// list of actual location after we convert the location name into the actual location object.
		List<Location> locationList = new ArrayList<Location>();
		// get the location names from the rule execution parameters
		Object paramLocationObjects = parameters.get(MohEvaluableConstants.ENCOUNTER_LOCATION);
		// validate the above object to make sure it's a collection object
		if (MohRuleUtils.isValidCollectionObject(paramLocationObjects)) {
			// the object is a valid collection object, cast them into a collection object
			Collection locationObjects = (Collection) paramLocationObjects;
			// iterate over the location names
			for (Object locationObject : locationObjects) {
				// get the actual location and put them in the list
				Location location = Context.getLocationService().getLocation(String.valueOf(locationObject));
				if (location != null)
					locationList.add(location);
			}
			// this check will prevent user from passing random string in the parameters
			if (CollectionUtils.isEmpty(locationObjects))
				throw new LogicException("Passing invalid list of locations attempted. Processed parameters are " + locationObjects);
			// replace the parameter from list of location names with list of location
			parameters.put(MohEvaluableConstants.ENCOUNTER_LOCATION, locationList);
		}

		// at this point, the parameters contains:
		// - list of encounter type (map key: EvaluableConstants.ENCOUNTER_TYPE)
		// - list of location (map key: EvaluableConstants.LOCATION)
		// next, pass the processing to the EncounterWithObjectRestrictionRule
		MohEncounterWithRestrictionRule mohEncounterWithRestrictionRule = new MohEncounterWithObjectRestrictionRule();
		return mohEncounterWithRestrictionRule.eval(context, patientId, parameters);
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
	 * Whether the result of the rule should be cached or not
	 *
	 * @return true if the system should put the result into the caching system
	 */
	@Override
	protected Boolean cacheable() {
		return Boolean.TRUE;
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
