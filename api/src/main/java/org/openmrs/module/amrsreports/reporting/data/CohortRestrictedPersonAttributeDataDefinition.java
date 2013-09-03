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

import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

/**
 * Person Attribute Column
 */
@Localized("reporting.PersonAttributeDataDefinition")
public class CohortRestrictedPersonAttributeDataDefinition extends BaseDataDefinition implements PersonDataDefinition {

	public static final long serialVersionUID = 1L;

	//***** PROPERTIES *****

	@ConfigurationProperty
	private PersonAttributeType personAttributeType;

	//****** CONSTRUCTORS ******

	/**
	 * Default Constructor
	 */
	public CohortRestrictedPersonAttributeDataDefinition() {
		super();
	}

	/**
	 * Constructor to populate name and personAttributeType
	 */
	public CohortRestrictedPersonAttributeDataDefinition(String name, PersonAttributeType personAttributeType) {
		super(name);
		this.personAttributeType = personAttributeType;
	}

	/**
	 * Constructor to populate type only
	 */
	public CohortRestrictedPersonAttributeDataDefinition(PersonAttributeType personAttributeType) {
		this(null, personAttributeType);
	}

	//***** INSTANCE METHODS *****

	/**
	 * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		return PersonAttribute.class;
	}
	
	//***** PROPERTY ACCESS *****

	/**
	 * @return the personAttributeType
	 */
	public PersonAttributeType getPersonAttributeType() {
		return personAttributeType;
	}

	/**
	 * @param personAttributeType the personAttributeType to set
	 */
	public void setPersonAttributeType(PersonAttributeType personAttributeType) {
		this.personAttributeType = personAttributeType;
	}
}