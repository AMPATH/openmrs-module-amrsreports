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

package org.openmrs.module.amrsreports.reporting.converter;

import org.openmrs.module.amrsreports.model.RegimenChange;
import org.openmrs.module.drughistory.Regimen;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.util.OpenmrsUtil;

import java.util.Iterator;
import java.util.List;

public class RegimenHistoryConverter implements DataConverter {

	private String line = null;
	private Integer order = null;

	public RegimenHistoryConverter(String line, Integer order) {
		this.line = line;
		this.order = order;
	}

	@Override
	public Object convert(Object original) {
		if (original == null || !(original instanceof List))
			return null;

		List<RegimenChange> regimens = (List<RegimenChange>) original;
		Iterator<RegimenChange> rIter = regimens.iterator();
		int i = 0;
		while (rIter.hasNext()) {
			RegimenChange rc = rIter.next();
			// match on line or allow if not specified
			if (line == null || OpenmrsUtil.nullSafeEquals(rc.getRegimen().getLine(), this.line)) {
				// ensure at the right level
				if (order == null || order == i) {
					return rc.getRegimen().getName();
				}
			}
		}

		return null;
	}

	@Override
	public Class<?> getInputDataType() {
		return List.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}
}
