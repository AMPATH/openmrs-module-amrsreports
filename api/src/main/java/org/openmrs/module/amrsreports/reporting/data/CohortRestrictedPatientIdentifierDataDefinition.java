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

import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

import java.util.ArrayList;
import java.util.List;

@Localized("reporting.CohortRestrictedPatientIdentifierDataDefinition")
public class CohortRestrictedPatientIdentifierDataDefinition  extends BaseDataDefinition implements PatientDataDefinition {

	//***** PROPERTIES *****

	@ConfigurationProperty(required=true)
	private List<PatientIdentifierType> types;

	@ConfigurationProperty
	private Boolean includeFirstNonNullOnly = Boolean.FALSE;

	//****** CONSTRUCTORS ******

	/**
	 * Default Constructor
	 */
	public CohortRestrictedPatientIdentifierDataDefinition() {
		super();
	}

	/**
	 * Constructor to populate name and type only
	 */
	public CohortRestrictedPatientIdentifierDataDefinition(String name, PatientIdentifierType... types) {
		super(name);
		if (types != null) {
			for (PatientIdentifierType type : types) {
				addType(type);
			}
		}
	}

	//***** INSTANCE METHODS *****

	/**
	 * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		if (getIncludeFirstNonNullOnly() == Boolean.TRUE) {
			return PatientIdentifier.class;
		}
		return List.class;
	}

	//****** PROPERTY ACCESS ******

	/**
	 * @return the types
	 */
	public List<PatientIdentifierType> getTypes() {
		return types;
	}

	/**
	 * @param types the types to set
	 */
	public void setTypes(List<PatientIdentifierType> types) {
		this.types = types;
	}

	/**
	 * @param type the type to add
	 */
	public void addType(PatientIdentifierType type) {
		if (types == null) {
			types = new ArrayList<PatientIdentifierType>();
		}
		types.add(type);
	}

	/**
	 * @return whether or not to include only the first null value for each patient
	 */
	public Boolean getIncludeFirstNonNullOnly() {
		return includeFirstNonNullOnly;
	}

	/**
	 * @param includeFirstNonNullOnly whether or not to include only the first null value for each patient
	 */
	public void setIncludeFirstNonNullOnly(Boolean includeFirstNonNullOnly) {
		this.includeFirstNonNullOnly = includeFirstNonNullOnly;
	}
}
