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


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;

import java.util.HashMap;
import java.util.Map;

/**
 * Lightweight concept caching based on the concept name.
 */
public class MohCacheInstance<T> {

	private static final Log log = LogFactory.getLog(MohCacheInstance.class);

	private final Map<String, T> ourMap;

	/**
	 *
	 */
	MohCacheInstance() {
		ourMap = new HashMap<String, T>();
	}

	/**
	 * Add an element to the cache system
	 *
	 * @param name
	 * @param object
	 */
	synchronized void add(String name, T object) {
		ourMap.put(name, object);
	}

	/**
	 * Clear content of the cache
	 */
	synchronized void clear() {
		ourMap.clear();
	}

	/**
	 * Internal implementation of getting something from the cache
	 *
	 * @param name
	 * @return
	 */
	T get(String name) {
		return ourMap.get(name);
	}

}
