package org.openmrs.module.amrsreports.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.HIVCareEnrollment;
import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.amrsreports.db.MOHFacilityDAO;
import org.openmrs.module.amrsreports.service.HIVCareEnrollmentService;
import org.openmrs.module.amrsreports.service.MOHFacilityService;
import org.openmrs.module.amrsreports.service.MohCoreService;
import org.openmrs.util.MetadataComparator;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Implementation of MOHFacilityService
 */
public class MOHFacilityServiceImpl implements MOHFacilityService {

	private MOHFacilityDAO dao;
	private final Log log = LogFactory.getLog(getClass());

	public void setDao(MOHFacilityDAO dao) {
		this.dao = dao;
	}

	@Override
	public List<MOHFacility> getAllFacilities() {
		return Context.getService(MOHFacilityService.class).getAllFacilities(false);
	}

	@Override
	public List<MOHFacility> getAllFacilities(Boolean includeRetired) {
		return dao.getAllFacilities(includeRetired);
	}

	@Override
	public MOHFacility getFacility(Integer facilityId) {
		return dao.getFacility(facilityId);
	}

	@Override
	public MOHFacility saveFacility(MOHFacility facility) {
		return dao.saveFacility(facility);
	}

	@Override
	public Set<Location> getUnallocatedLocations() {
		Set<Location> locations = new TreeSet<Location>(new MetadataComparator(Context.getLocale()));
		locations.addAll(Context.getLocationService().getAllLocations());
		List<MOHFacility> facilities = Context.getService(MOHFacilityService.class).getAllFacilities();
		for (MOHFacility facility : facilities) {
			locations.removeAll(facility.getLocations());
		}
		return locations;
	}

	@Override
	public Set<Location> getAllocatedLocations() {
		// TODO: consider using this:
		// select distinct l from Location l, Facility f where l member of f.locations
		Set<Location> locations = new TreeSet<Location>(new MetadataComparator(Context.getLocale()));
		List<MOHFacility> facilities = Context.getService(MOHFacilityService.class).getAllFacilities();
		for (MOHFacility facility : facilities) {
			locations.addAll(facility.getLocations());
		}
		return locations;
	}

	@Override
	public void retireFacility(MOHFacility facility, String retireReason) {
		if (retireReason == null || retireReason.length() < 1)
			throw new APIException("A reason is required when retiring a facility");

		facility.setRetired(true);
		facility.setRetiredBy(Context.getAuthenticatedUser());
		facility.setDateRetired(new Date());
		facility.setRetireReason(retireReason);

		dao.saveFacility(facility);
	}

	@Override
	public void purgeFacility(MOHFacility facility) {
		dao.purgeFacility(facility);
	}

	@Override
	public Map<Integer, PatientIdentifier> getCCCNumberMapForFacility(MOHFacility facility) {

		List<PatientIdentifier> patientIdentifiers = dao.getCCCNumbersForFacility(facility);

		Map<Integer, PatientIdentifier> cccMap = new HashMap<Integer, PatientIdentifier>();
		for (PatientIdentifier pi : patientIdentifiers) {
			cccMap.put(pi.getPatient().getPatientId(), pi);
		}

		return cccMap;
	}

	@Override
	public Map<Integer, Integer> getFacilityIdToLatestSerialNumberMap() {
		Map<String, Integer> codeSerialMap = dao.getFacilityCodeToLatestSerialNumberMap();
		List<MOHFacility> allFacilities = Context.getService(MOHFacilityService.class).getAllFacilities(true);

		Map<Integer, Integer> m = new HashMap<Integer, Integer>();
		for (MOHFacility facility : allFacilities) {
			m.put(facility.getFacilityId(), codeSerialMap.containsKey(facility.getCode()) ? codeSerialMap.get(facility.getCode()) : 0);
		}

		return m;
	}

	private List<Integer> getEnrolledPatientsForFacility(MOHFacility facility) {
		return dao.getEnrolledPatientsForFacility(facility);
	}

	@Override
	public Integer countPatientsInFacilityMissingCCCNumbers(MOHFacility facility) {
		return getPatientsInFacilityMissingCCCNumbers(facility).size();
	}

	@Override
	public List<Integer> getPatientsInFacilityMissingCCCNumbers(MOHFacility facility) {
		List<Integer> c = getEnrolledPatientsForFacility(facility);
		List<Integer> missing = dao.getPatientsInCohortMissingCCCNumbers(c);
		return missing;
	}

	@Override
	public Integer getLatestSerialNumberForFacility(MOHFacility facility) {
		return dao.getLatestSerialNumberForFacility(facility);
	}

	@Override
	public Map<Integer, String> getSerialNumberMapForFacility(MOHFacility facility) {
		return dao.getSerialNumberMapForFacility(facility);
	}


	@Override
	public Integer assignMissingIdentifiersForFacility(MOHFacility facility) {

		// fail quickly if the facility does not exist
		if (facility == null) {
			return -1;
		}

		// also fail quickly if there are no locations for the facility
		if (facility.getLocations().size() < 1) {
			return -1;
		}

		// get some required information
		PatientIdentifierType pit = Context.getService(MohCoreService.class).getCCCNumberIdentifierType();
		List<Integer> c = getEnrolledPatientsForFacility(facility);
		Integer serial = dao.getLatestSerialNumberForFacility(facility);

		// start a counter for our progress
		Integer count = 0;

		// loop over members of the filtered cohort
		List<Integer> missing = dao.getPatientsInCohortMissingCCCNumbers(c);

		for (Integer patientId : missing) {

			// get the patient
			Patient p = Context.getPatientService().getPatient(patientId);

			// only do something if the patient exists
			if (p != null) {

				// increase the serial number
				serial++;

				// get the enrollment information
				HIVCareEnrollment enrollment = Context.getService(HIVCareEnrollmentService.class)
						.getHIVCareEnrollmentForPatient(p);

				// set the location for the identifier
				Location location = enrollment.getEnrollmentLocation();

				// create the new identifier
				String identifier = String.format("%s-%05d", facility.getCode(), serial);
				PatientIdentifier pi = new PatientIdentifier(identifier, pit, location);

				// add it to the patient
				pi.setPatient(p);
				p.addIdentifier(pi);

				// save the patient
				Context.getPatientService().savePatient(p);

				// keep track of how many we did
				count++;
			}
		}

		return count;
	}

}
