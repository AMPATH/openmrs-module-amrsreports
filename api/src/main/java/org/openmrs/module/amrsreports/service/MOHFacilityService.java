package org.openmrs.module.amrsreports.service;

import org.openmrs.Location;
import org.openmrs.PatientIdentifier;
import org.openmrs.module.amrsreports.MOHFacility;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service for dealing with facilities
 */
public interface MOHFacilityService {

	@Transactional(readOnly = true)
	public List<MOHFacility> getAllFacilities();

	@Transactional(readOnly = true)
	public List<MOHFacility> getAllFacilities(Boolean includeRetired);

	@Transactional(readOnly = true)
	public MOHFacility getFacility(Integer facilityId);

	@Transactional
	public MOHFacility saveFacility(MOHFacility facility);

	@Transactional(readOnly = true)
	public Set<Location> getUnallocatedLocations();

	@Transactional(readOnly = true)
	public Set<Location> getAllocatedLocations();

	@Transactional
	void retireFacility(MOHFacility facility, String retireReason);

	@Transactional
	void purgeFacility(MOHFacility facility);

	@Transactional(readOnly = true)
	public Map<Integer, PatientIdentifier> getCCCNumberMapForFacility(MOHFacility facility);

	@Transactional(readOnly = true)
	public Map<Integer, Integer> getFacilityIdToLatestSerialNumberMap();

	@Transactional(readOnly = true)
	public Integer countPatientsInFacilityMissingCCCNumbers(MOHFacility facility);

	@Transactional
	Integer assignMissingIdentifiersForFacility(MOHFacility facility);

	@Transactional(readOnly = true)
	List<Integer> getPatientsInFacilityMissingCCCNumbers(MOHFacility facility);

	@Transactional
	Integer getLatestSerialNumberForFacility(MOHFacility facility);

	@Transactional(readOnly = true)
	Map<Integer, String> getSerialNumberMapForFacility(MOHFacility facility);
}
