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
package org.openmrs.module.amrsreport.cache;


import org.openmrs.PatientIdentifierType;

/**
 * Lightweight concept caching based on the Identifier Type name.
 */
class MohPatientIdentifierTypeCacheInstance extends MohCacheInstance<PatientIdentifierType> {

	private static MohPatientIdentifierTypeCacheInstance ourInstance = new MohPatientIdentifierTypeCacheInstance();

	private MohPatientIdentifierTypeCacheInstance() {
		super();
	}

	public static MohPatientIdentifierTypeCacheInstance getInstance() {
		return ourInstance;
	}

}
