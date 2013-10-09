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

import org.openmrs.module.amrsreports.reporting.common.ObsRepresentation;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.util.List;

public class IntervalObsValueNumericConverter extends ObsRepresentationValueNumericConverter {

	private int interval = 0;

	public IntervalObsValueNumericConverter(int precision, int interval) {
		this.setPrecision(precision);
		this.setInterval(interval);
	}

	@Override
	public Object convert(Object original) {
		List<ObsRepresentation> o = (List<ObsRepresentation>) original;
		if (o == null || o.size() == 0)
			return null;

		// pick the first one ...
		ObsRepresentation or = o.get(0);

		return super.convert(or);
	}

	@Override
	public Class<?> getInputDataType() {
		return List.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}
}
