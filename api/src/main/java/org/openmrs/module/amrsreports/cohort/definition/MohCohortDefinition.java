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

package org.openmrs.module.amrsreports.cohort.definition;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

@Localized("reporting.MOHCohortDefinition")
public class MohCohortDefinition extends BaseCohortDefinition {

	private static final Log log = LogFactory.getLog(MohCohortDefinition.class);

	@ConfigurationProperty(group = "otherGroup")
	private List<Location> locationList;

	public List<Location> getLocationList() {
		return locationList;
	}

	public void setLocationList(final List<Location> locationList) {
		this.locationList = locationList;
	}

    public void addLocation(Location location) {
    	if (locationList == null) {
    		locationList = new ArrayList<Location>();
    	}
    	locationList.add(location);
    }
}
