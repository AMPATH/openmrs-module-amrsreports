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

package org.openmrs.module.amrsreports.service;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.amrsreports.service.impl.UserFacilityServiceImpl;

import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class UserFacilityServiceTest {
	/**
	 * @verifies return an empty list if the user is null
	 * @see UserFacilityService#getAllowedFacilitiesForUser(org.openmrs.User)
	 */
	@Test
	public void getAllowedFacilitiesForUser_shouldReturnAnEmptyListIfTheUserIsNull() throws Exception {
		UserFacilityService service = new UserFacilityServiceImpl();
		List<MOHFacility> actual = service.getAllowedFacilitiesForUser(null);

		assertNotNull(actual);
		assertTrue(actual.isEmpty());
	}
}
