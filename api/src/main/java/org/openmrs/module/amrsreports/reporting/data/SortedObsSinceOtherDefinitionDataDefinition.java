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
import org.openmrs.module.amrsreports.model.SortedObsFromDate;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.MappedData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

import java.util.ArrayList;
import java.util.List;

/**
 * Instead of providing just a List, we guarantee it is a SortedSet
 */
@Caching(strategy = ConfigurationPropertyCachingStrategy.class)
public class SortedObsSinceOtherDefinitionDataDefinition extends BaseDataDefinition implements PersonDataDefinition {

	@ConfigurationProperty(required = false)
	private MappedData<? extends PersonDataDefinition> effectiveDateDefinition;

	@ConfigurationProperty(required = true)
	private List<Concept> questions;

	public SortedObsSinceOtherDefinitionDataDefinition() {
		super();
	}

	public SortedObsSinceOtherDefinitionDataDefinition(String name) {
		super(name);
	}

	/**
	 * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		return SortedObsFromDate.class;
	}

	/**
	 * @return the effectiveDateDefinition
	 */
	public MappedData<? extends PersonDataDefinition> getEffectiveDateDefinition() {
		return effectiveDateDefinition;
	}

	/**
	 * @param effectiveDateDefinition the effectiveDateDefinition to set
	 */
	public void setEffectiveDateDefinition(MappedData<? extends PersonDataDefinition> effectiveDateDefinition) {
		this.effectiveDateDefinition = effectiveDateDefinition;
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
}
