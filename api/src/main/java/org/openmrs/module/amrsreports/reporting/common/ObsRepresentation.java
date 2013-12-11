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

package org.openmrs.module.amrsreports.reporting.common;

import java.util.Date;
import java.util.Map;

public class ObsRepresentation extends DataRepresentation {

	private Integer conceptId;
	private Integer valueCodedId;
	private Double valueNumeric;
	private Date valueDatetime;
	private Date obsDatetime;
	private Integer order;

	public ObsRepresentation() {
		super();
	}

	public ObsRepresentation(Map<String, Object> m) {

		super(m);

		conceptId = m.containsKey("conceptId") ? (Integer) m.get("conceptId") : null;
		valueCodedId = m.containsKey("valueCodedId") ? (Integer) m.get("valueCodedId") : null;
		valueNumeric = m.containsKey("valueNumeric") ? (Double) m.get("valueNumeric") : null;
		valueDatetime = m.containsKey("valueDatetime") ? (Date) m.get("valueDatetime") : null;
		obsDatetime = m.containsKey("obsDatetime") ? (Date) m.get("obsDatetime") : null;
		order = m.containsKey("order") ? (Integer) m.get("order") : null;
	}

	public Integer getConceptId() {
		return conceptId;
	}

	public void setConceptId(Integer conceptId) {
		this.conceptId = conceptId;
	}

	public Integer getValueCodedId() {
		return valueCodedId;
	}

	public void setValueCodedId(Integer valueCodedId) {
		this.valueCodedId = valueCodedId;
	}

	public Double getValueNumeric() {
		return valueNumeric;
	}

	public void setValueNumeric(Double valueNumeric) {
		this.valueNumeric = valueNumeric;
	}

	public Date getValueDatetime() {
		return valueDatetime;
	}

	public void setValueDatetime(Date valueDatetime) {
		this.valueDatetime = valueDatetime;
	}

	public Date getObsDatetime() {
		return obsDatetime;
	}

	public void setObsDatetime(Date obsDatetime) {
		this.obsDatetime = obsDatetime;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}
}
