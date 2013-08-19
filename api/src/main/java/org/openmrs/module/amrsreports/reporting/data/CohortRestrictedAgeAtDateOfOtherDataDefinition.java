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

import org.openmrs.module.reporting.common.Age;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.MappedData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

public class CohortRestrictedAgeAtDateOfOtherDataDefinition extends BaseDataDefinition implements PersonDataDefinition {

	public static final long serialVersionUID = 1L;

	//****** PROPERTIES ******

	@ConfigurationProperty(required=false)
	private MappedData<? extends PersonDataDefinition> effectiveDateDefinition;

	/**
	 * Default Constructor
	 */
	public CohortRestrictedAgeAtDateOfOtherDataDefinition() {
		super();
	}

	/**
	 * Constructor to populate name only
	 */
	public CohortRestrictedAgeAtDateOfOtherDataDefinition(String name) {
		super(name);
	}

	//***** INSTANCE METHODS *****

	/**
	 * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		return Age.class;
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
}