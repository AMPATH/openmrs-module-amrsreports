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
package org.openmrs.module.amrsreports.util;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 */
public class MohFetchRestriction {

	private static final Log log = LogFactory.getLog(MohFetchRestriction.class);

	private Integer start;

	private Integer size;

	private MohFetchOrdering mohFetchOrdering;

	/**
	 */
	public MohFetchRestriction() {
	}

	/**
	 * @param size
	 * @param mohFetchOrdering
	 */
	public MohFetchRestriction(final Integer size, final MohFetchOrdering mohFetchOrdering) {
		setSize(size);
		setFetchOrdering(mohFetchOrdering);
	}

	/**
	 * @param size
	 * @param ordering
	 */
	public MohFetchRestriction(final Integer size, final String ordering) {
		setSize(size);
		setFetchOrdering(ordering);
	}

	/**
	 * @return
	 */
	public Integer getStart() {
		return start;
	}

	/**
	 * @param start
	 */
	public void setStart(final Integer start) {
		this.start = start;
	}

	/**
	 * @return
	 */
	public Integer getSize() {
		return size;
	}

	/**
	 * @param size
	 */
	public void setSize(final Integer size) {
		this.size = size;
	}

	/**
	 * @return
	 */
	public MohFetchOrdering getFetchOrdering() {
		return mohFetchOrdering;
	}

	/**
	 * @param mohFetchOrdering
	 */
	public void setFetchOrdering(final MohFetchOrdering mohFetchOrdering) {
		this.mohFetchOrdering = mohFetchOrdering;
	}

	/**
	 * @param ordering
	 */
	public void setFetchOrdering(final String ordering) {
		for (MohFetchOrdering mohFetchOrdering : MohFetchOrdering.values()) {
			if (StringUtils.equalsIgnoreCase(ordering, mohFetchOrdering.getValue()))
				this.mohFetchOrdering = mohFetchOrdering;
		}
	}
}
