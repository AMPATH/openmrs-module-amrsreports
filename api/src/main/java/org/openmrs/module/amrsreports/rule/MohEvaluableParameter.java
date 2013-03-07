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


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class that will hold the definition of each parameters that need to be passed to a rule
 */
public class MohEvaluableParameter {

	private static final Log log = LogFactory.getLog(MohEvaluableParameter.class);

	private String name;

	private Class parameterClass;

	private Boolean required;

	private Object defaultValue;

	public MohEvaluableParameter(final String name, final Class parameterClass, final Boolean required) {
		this.name = name;
		this.parameterClass = parameterClass;
		this.required = required;
		this.defaultValue = 1;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public Class getParameterClass() {
		return parameterClass;
	}

	/**
	 * @return
	 */
	public Boolean isRequired() {
		return required;
	}

	/**
	 * @return
	 */
	public Object getDefaultValue() {
		return defaultValue;
	}

}