package org.openmrs.module.amrsreports.reporting.cohort.definition;

import org.openmrs.Location;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * NASCOP 771 Register cohort definition
 */
@Localized("reporting.NASCOP771CohortDefinition")
public class NASCOP771CohortDefinition extends BaseCohortDefinition {

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
