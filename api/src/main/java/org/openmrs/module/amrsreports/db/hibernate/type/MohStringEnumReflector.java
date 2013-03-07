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
package org.openmrs.module.amrsreports.db.hibernate.type;

import org.apache.commons.lang.StringUtils;

/**
 * Reflector class to get the value of StringEnum
 */
public class MohStringEnumReflector {
	
	private MohStringEnumReflector() {
	}
	
	/**
	 * All enum constants (instances) declared in the specified class.
	 * 
	 * @param enumClass Class to reflect
	 * @return Array of all declared EnumConstants (instances).
	 */
	private static <T extends Enum<T>> T[] getValues(final Class<T> enumClass) {
		return enumClass.getEnumConstants();
	}
	
	/**
	 * Name of the enum instance which hold the specified string value. If value has duplicate enum
	 * instances than returns the first occurrence.
	 * 
	 * @param enumClass Class to inspect.
	 * @param value String.
	 * @return name of the enum instance.
	 */
	public static <T extends Enum<T> & MohStringEnum> String getNameFromValue(final Class<T> enumClass, final String value) {
		T[] values = getValues(enumClass);
		for (T v : values)
			if (v.getValue().equals(value))
				return v.name();
		return StringUtils.EMPTY;
	}
	
}
