package org.openmrs.module.amrsreports.db;

import org.openmrs.module.amrsreports.MOHFacility;

import java.util.List;

/**
 * DAO for manipulating Facilities
 */
public interface MOHFacilityDAO {

	public List<MOHFacility> getAllFacilities(Boolean includeRetired);

	public MOHFacility getFacility(Integer facilityId);

	public MOHFacility saveFacility(MOHFacility facility);

	public void purgeFacility(MOHFacility facility);
}
