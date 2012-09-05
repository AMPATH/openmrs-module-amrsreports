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
package org.openmrs.module.amrsreport.userlocation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.openmrs.User;
import org.openmrs.Location;
import org.openmrs.BaseOpenmrsObject;
//import javax.xml.stream.Location;

/**
 * It is a model class. It should extend either {@link BaseOpenmrsObject} or {@link BaseOpenmrsMetadata}.
 */
public class UserLocation extends BaseOpenmrsObject implements java.io.Serializable{

	//private static final long serialVersionUID = 1L;
	
	
	private Integer userLocationId;
	private User sysUser;
	private List<Location> userLoc;
	private String uuid = null;
	
	public Integer getId() {
		return userLocationId;
	}
	
	
	public void setId(Integer id) {
		
		this.userLocationId=id;
	}

	public Integer getUserLocationId() {
		
		return this.getId();
		
	}

	public void setUserLocationId(Integer userLocationId) {
		this.setId(userLocationId);
		
	}

	public User getSysUser() {
		return sysUser;
	}

	public void setSysUser(User sysUser) {
		this.sysUser = sysUser;
	}

	public List<Location> getUserLoc() {
		return userLoc;
	}

	public void setUserLoc(List<Location> userLoc) {
		this.userLoc = userLoc;
	}


	public String getUuid() {
		return uuid;
	}


	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	
	
}