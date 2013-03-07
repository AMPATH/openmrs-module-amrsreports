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

import java.util.List;

public interface MohEvaluableConstants {

	String ENCOUNTER_TYPE = "encounterType";

	String ENCOUNTER_LOCATION = "location";

	String ENCOUNTER_PROVIDER = "provider";

	String ENCOUNTER_FETCH_SIZE = "encounter.size";

	String ENCOUNTER_FETCH_ORDER = "encounter.order";

	String OBS_ENCOUNTER = "encounter";

	String OBS_LOCATION = "location";

	String OBS_CONCEPT = "concept";

	String OBS_VALUE_CODED = "valueCoded";

	String OBS_FETCH_SIZE = "obs.size";

	String OBS_FETCH_ORDER = "obs.order";

	MohEvaluableParameter REQUIRED_ENCOUNTER_TYPE_PARAMETER_DEFINITION = new MohEvaluableParameter(ENCOUNTER_TYPE, List.class, Boolean.TRUE);

	MohEvaluableParameter REQUIRED_ENCOUNTER_FETCH_ORDER_PARAMETER_DEFINITION = new MohEvaluableParameter(ENCOUNTER_FETCH_ORDER, String.class, Boolean.TRUE);

	MohEvaluableParameter REQUIRED_ENCOUNTER_FETCH_SIZE_PARAMETER_DEFINITION = new MohEvaluableParameter(ENCOUNTER_FETCH_SIZE, Integer.class, Boolean.TRUE);

	MohEvaluableParameter REQUIRED_OBS_CONCEPT_PARAMETER_DEFINITION = new MohEvaluableParameter(OBS_CONCEPT, List.class, Boolean.TRUE);

	MohEvaluableParameter REQUIRED_OBS_FETCH_ORDER_PARAMETER_DEFINITION = new MohEvaluableParameter(OBS_FETCH_ORDER, String.class, Boolean.TRUE);

	MohEvaluableParameter REQUIRED_OBS_FETCH_SIZE_PARAMETER_DEFINITION = new MohEvaluableParameter(OBS_FETCH_SIZE, Integer.class, Boolean.TRUE);

	MohEvaluableParameter REQUIRED_OBS_VALUE_CODED_PARAMETER_DEFINITION = new MohEvaluableParameter(OBS_VALUE_CODED, List.class, Boolean.TRUE);

	MohEvaluableParameter OPTIONAL_ENCOUNTER_TYPE_PARAMETER_DEFINITION = new MohEvaluableParameter(ENCOUNTER_TYPE, List.class, Boolean.FALSE);

	MohEvaluableParameter OPTIONAL_ENCOUNTER_FETCH_ORDER_PARAMETER_DEFINITION = new MohEvaluableParameter(ENCOUNTER_FETCH_ORDER, String.class, Boolean.FALSE);

	MohEvaluableParameter OPTIONAL_ENCOUNTER_FETCH_SIZE_PARAMETER_DEFINITION = new MohEvaluableParameter(ENCOUNTER_FETCH_SIZE, Integer.class, Boolean.FALSE);

	MohEvaluableParameter OPTIONAL_OBS_CONCEPT_PARAMETER_DEFINITION = new MohEvaluableParameter(OBS_CONCEPT, List.class, Boolean.FALSE);

	MohEvaluableParameter OPTIONAL_OBS_ENCOUNTER_PARAMETER_DEFINITION = new MohEvaluableParameter(OBS_ENCOUNTER, List.class, Boolean.FALSE);

	MohEvaluableParameter OPTIONAL_OBS_FETCH_ORDER_PARAMETER_DEFINITION = new MohEvaluableParameter(OBS_FETCH_ORDER, String.class, Boolean.FALSE);

	MohEvaluableParameter OPTIONAL_OBS_FETCH_SIZE_PARAMETER_DEFINITION = new MohEvaluableParameter(OBS_FETCH_SIZE, Integer.class, Boolean.FALSE);

	MohEvaluableParameter OPTIONAL_OBS_VALUE_CODED_PARAMETER_DEFINITION = new MohEvaluableParameter(OBS_VALUE_CODED, List.class, Boolean.FALSE);
}
