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

import java.util.Map;

class MohResultCacheKey {

	private static final Log log = LogFactory.getLog(MohResultCacheKey.class);

	private Integer patientId;

	private String token;

	private Map<String, Object> parameters;

	/**
	 * @param patientId
	 * @param token
	 * @param parameters
	 */
	MohResultCacheKey(final Integer patientId, final String token, final Map<String, Object> parameters) {
		this.patientId = patientId;
		this.token = token;
		this.parameters = parameters;
	}

	/**
	 * @return
	 */
	public Integer getPatientId() {
		return patientId;
	}

	/**
	 * @return
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @return
	 */
	public Map<String, Object> getParameters() {
		return parameters;
	}

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final MohResultCacheKey that = (MohResultCacheKey) o;

		if (!parameters.equals(that.parameters)) return false;
		if (!patientId.equals(that.patientId)) return false;
		if (!token.equals(that.token)) return false;

		return true;
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = patientId.hashCode();
		result = 31 * result + token.hashCode();
		result = 31 * result + parameters.hashCode();
		return result;
	}

	/**
	 * Returns a string representation of the object. In general, the
	 * <code>toString</code> method returns a string that
	 * "textually represents" this object. The result should
	 * be a concise but informative representation that is easy for a
	 * person to read.
	 * It is recommended that all subclasses override this method.
	 * <p/>
	 * The <code>toString</code> method for class <code>Object</code>
	 * returns a string consisting of the name of the class of which the
	 * object is an instance, the at-sign character `<code>@</code>', and
	 * the unsigned hexadecimal representation of the hash code of the
	 * object. In other words, this method returns a string equal to the
	 * value of:
	 * <blockquote>
	 * <pre>
	 * getClass().getName() + '@' + Integer.toHexString(hashCode())
	 * </pre></blockquote>
	 *
	 * @return a string representation of the object.
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		builder.append("patientId: ").append(patientId).append(", ");
		builder.append("token: ").append(token).append(", ");
		builder.append("{");
		for (String key : parameters.keySet()) {
			builder.append(" " + key + ": ").append(parameters.get(key));
		}
		builder.append(" }");
		builder.append("]");

		return builder.toString();
	}
}
