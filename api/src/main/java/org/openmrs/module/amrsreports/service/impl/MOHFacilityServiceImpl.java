package org.openmrs.module.amrsreports.service.impl;

import org.openmrs.Location;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.AmrsReportsConstants;
import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.amrsreports.db.MOHFacilityDAO;
import org.openmrs.module.amrsreports.service.MOHFacilityService;
import org.openmrs.module.amrsreports.service.MohCoreService;
import org.openmrs.util.MetadataComparator;

import java.util.Collections;
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
		for (MOHFacility facility: facilities) {
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
		for (MOHFacility facility: facilities) {
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
		for (PatientIdentifier pi: patientIdentifiers) {
			cccMap.put(pi.getPatient().getPatientId(), pi);
		}

		return cccMap;
	}
}
