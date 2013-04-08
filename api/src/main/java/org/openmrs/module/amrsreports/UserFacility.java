/**
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.amrsreports;

import org.openmrs.BaseOpenmrsObject;
import org.openmrs.User;

/**
 * Link between users and facilities, used for permissions when running or viewing reports
 */
public class UserFacility extends BaseOpenmrsObject implements java.io.Serializable {

	private Integer userFacilityId;
	private User user;
	private MOHFacility facility;

	public Integer getId() {
		return userFacilityId;
	}

	public void setId(Integer id) {
		this.userFacilityId = id;
	}

	public Integer getUserFacilityId() {
		return this.getId();
	}

	public void setUserFacilityId(Integer userFacilityId) {
		this.setId(userFacilityId);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public MOHFacility getFacility() {
		return facility;
	}

	public void setFacility(MOHFacility facility) {
		this.facility = facility;
	}
}