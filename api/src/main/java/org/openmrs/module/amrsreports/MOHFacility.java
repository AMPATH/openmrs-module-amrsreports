package org.openmrs.module.amrsreports;

import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.Location;

import java.util.HashSet;
import java.util.Set;

/**
 * Assignment of an MOH facility code to one or more locations
 */
public class MOHFacility extends BaseOpenmrsMetadata {

	private Integer facilityId;
	private String code;
	private Set<Location> locations;

	@Override
	public Integer getId() {
		return facilityId;
	}

	@Override
	public void setId(Integer id) {
		facilityId = id;
	}

	public Integer getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(Integer facilityId) {
		this.facilityId = facilityId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Set<Location> getLocations() {
		if (locations == null)
			locations = new HashSet<Location>();
		return locations;
	}

	public void setLocations(Set<Location> locations) {
		this.locations = locations;
	}

	public void addLocation(Location location) {
		this.getLocations().add(location);
	}

	@Override
	public String toString() {
		return String.format("%s - %s", this.getCode(), this.getName());
	}
}
