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

package org.openmrs.module.amrsreports.reporting.data;

import org.openmrs.Concept;
import org.openmrs.module.amrsreports.reporting.common.ObsRepresentation;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Caching(strategy = ConfigurationPropertyCachingStrategy.class)
public class ObsNearestARVStartDateDataDefinition extends BaseDataDefinition implements PersonDataDefinition {

	@ConfigurationProperty(required = true)
	private List<Concept> questions;

	@ConfigurationProperty(required = false)
	private Integer ageLimit = null;

	public ObsNearestARVStartDateDataDefinition() {
		super();
	}

	public ObsNearestARVStartDateDataDefinition(String name) {
		super(name);
	}

	public ObsNearestARVStartDateDataDefinition(String name, Concept... questions) {
		super(name);
		this.questions = new ArrayList<Concept>(Arrays.asList(questions));
	}

	@Override
	public Class<?> getDataType() {
		return ObsRepresentation.class;
	}

	public List<Concept> getQuestions() {
		if (questions == null)
			questions = new ArrayList<Concept>();
		return questions;
	}

	public void setQuestions(List<Concept> questions) {
		this.questions = questions;
	}

	public void addQuestion(Concept question) {
		this.getQuestions().add(question);
	}

	public Integer getAgeLimit() {
		return ageLimit;
	}

	public void setAgeLimit(Integer ageLimit) {
		this.ageLimit = ageLimit;
	}
}
