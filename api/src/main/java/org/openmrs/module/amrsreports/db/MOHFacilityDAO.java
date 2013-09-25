package org.openmrs.module.amrsreports.db;

import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.module.amrsreports.MOHFacility;

import java.util.List;
import java.util.Map;

/**
 * DAO for manipulating Facilities
 */
public interface MOHFacilityDAO {

	public List<MOHFacility> getAllFacilities(Boolean includeRetired);

	public MOHFacility getFacility(Integer facilityId);

	public MOHFacility saveFacility(MOHFacility facility);

	public void purgeFacility(MOHFacility facility);

	public List<PatientIdentifier> getCCCNumbersForFacility(MOHFacility facility);

	public Map<String, Integer> getFacilityCodeToLatestSerialNumberMap();

	public List<Integer> getPatientsInCohortMissingCCCNumbers(List<Integer> c);

	public Integer getLatestSerialNumberForFacility(MOHFacility facility);

	public List<Integer> getEnrolledPatientsForFacility(MOHFacility facility);

	public Map<Integer, String> getSerialNumberMapForFacility(MOHFacility facility);
}
