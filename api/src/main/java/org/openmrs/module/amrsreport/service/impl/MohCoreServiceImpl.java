/**
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.amrsreport.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.OpenmrsObject;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.amrsreport.db.MohCoreDAO;
import org.openmrs.module.amrsreport.service.MohCoreService;
import org.openmrs.module.amrsreport.UserLocation;
import org.openmrs.module.amrsreport.util.MohFetchRestriction;
import org.openmrs.module.amrsreport.UserReport;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;

/**
 * Actual implementation of the core service contract
 */
public class MohCoreServiceImpl extends BaseOpenmrsService implements MohCoreService {

	private static final Log log = LogFactory.getLog(MohCoreServiceImpl.class);
	private MohCoreDAO mohCoreDAO;

	/**
	 * Setter for the DAO interface reference that will be called by Spring
	 * to inject the actual implementation of the DAO layer
	 *
	 * @param mohCoreDAO the coreDAO to be injected
	 */
	public void setCoreDAO(final MohCoreDAO mohCoreDAO) {
		if (log.isDebugEnabled()) {
			log.debug("Wiring up CoreDAO with CoreService ...");
		}

		this.mohCoreDAO = mohCoreDAO;
	}

	/**
	 * @see MohCoreService#getDateCreatedCohort(org.openmrs.Location,
	 * java.util.Date, java.util.Date)
	 */
	@Override
	public Cohort getDateCreatedCohort(final Location location, final Date startDate, final Date endDate) throws APIException {
		return mohCoreDAO.getDateCreatedCohort(location, startDate, endDate);
	}

	/**
	 * @see MohCoreService#getReturnDateCohort(org.openmrs.Location,
	 * java.util.Date, java.util.Date)
	 */
	@Override
	public Cohort getReturnDateCohort(final Location location, final Date startDate, final Date endDate) throws APIException {
		return mohCoreDAO.getReturnDateCohort(location, startDate, endDate);
	}

	/**
	 * @see MohCoreService#getObservationCohort(java.util.List,
	 * java.util.Date, java.util.Date)
	 */
	@Override
	public Cohort getObservationCohort(final List<Concept> concepts, final Date startDate, final Date endDate) throws APIException {
		return mohCoreDAO.getObservationCohort(concepts, startDate, endDate);
	}

	/**
	 * @see MohCoreService#getPatientEncounters(Integer, java.util.Map,
	 * org.openmrs.module.amrsreport.util.MohFetchRestriction)
	 */
	@Override
	public List<Encounter> getPatientEncounters(final Integer patientId, final Map<String, Collection<OpenmrsObject>> restrictions,
		final MohFetchRestriction mohFetchRestriction) throws APIException {
		return mohCoreDAO.getPatientEncounters(patientId, restrictions, mohFetchRestriction);
	}

	/**
	 * @see MohCoreService#getPatientObservations(Integer, java.util.Map,
	 * org.openmrs.module.amrsreport.util.MohFetchRestriction)
	 */
	@Override
	public List<Obs> getPatientObservations(final Integer patientId, final Map<String, Collection<OpenmrsObject>> restrictions,
		final MohFetchRestriction mohFetchRestriction) throws APIException {
		return mohCoreDAO.getPatientObservations(patientId, restrictions, mohFetchRestriction);
	}

	@Override
	public List<PatientIdentifier> getAllPatientIdenifiers(Patient p) {

		return mohCoreDAO.getAllPatientIdenifiers(p);
	}

	@Override
	public UserReport saveUserReport(UserReport userReport) {
		return mohCoreDAO.saveUserReport(userReport);
	}

	@Override
	public void purgeUserReport(UserReport userReport) {
		mohCoreDAO.purgeUserReport(userReport);
	}

	@Override
	public UserReport getUserReportByUuid(String uuid) {
		return mohCoreDAO.getUserReportByUuid(uuid);

	}

	public UserLocation saveUserLocation(UserLocation userlocation) {
		return mohCoreDAO.saveUserLocation(userlocation);
	}

	public UserLocation getUserLocation(Integer userlocationId) {
		return mohCoreDAO.getUserLocation(userlocationId);
	}

	public List<UserLocation> getAllUserLocationPrivileges() {

		return mohCoreDAO.getAllUserLocationPrivileges();
	}

	public void purgeUserLocation(UserLocation userlocation) {
		mohCoreDAO.deleteUserLocation(userlocation);
	}

	@Override
	public UserReport getUserReport(Integer userReportId) {
		return mohCoreDAO.getUserReport(userReportId);
	}

	@Override
	public List<Location> getAllowedLocationsForUser(User user) {
		List<Location> locations = new ArrayList<Location>();
		List<UserLocation> userLocations = mohCoreDAO.getUserLocationsForUser(user);
		for (UserLocation ul : userLocations) {
			locations.add(ul.getUserLoc());
		}
		return locations;
	}

	@Override
	public List<ReportDefinition> getAllowedReportDefinitionsForUser(User user) {
		ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
		List<ReportDefinition> definitions = new ArrayList<ReportDefinition>();
		List<UserReport> userReports = mohCoreDAO.getUserReportsForUser(user);
		for (UserReport ur : userReports) {
			String uuid = ur.getReportDefinitionUuid();
			definitions.add(rds.getDefinitionByUuid(uuid));
		}
		return definitions;
	}
}
