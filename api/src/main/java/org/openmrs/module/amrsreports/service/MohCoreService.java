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
package org.openmrs.module.amrsreports.service;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.OpenmrsObject;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.amrsreports.model.WHOStageAndDate;
import org.openmrs.module.amrsreports.util.MohFetchRestriction;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service contract for the core layer of OpenMRS
 */
@Transactional
public interface MohCoreService extends OpenmrsService {

	/**
	 * Get all patient id attached to a certain location on their
	 * observations where the observations are created between the start
	 * date and end date
	 *
	 * @param location  location of the patient observation
	 * @param startDate min date of the observation created date
	 * @param endDate   max date of the observation created date
	 * @return cohort of patient with observation in from a certain location
	 *         that fall between the date range. Return empty cohort when no patient
	 *         id match the criteria.
	 * @throws APIException
	 * @should return all patient id with certain location on their
	 * observations between certain date
	 * @should return empty cohort when no patient match the criteria
	 */
	@Transactional(readOnly = true)
	@Authorized({PrivilegeConstants.VIEW_LOCATIONS, PrivilegeConstants.VIEW_PATIENTS})
	Cohort getDateCreatedCohort(final Location location, final Date startDate, final Date endDate) throws APIException;

	/**
	 * Get all patient id attached to a certain location on their
	 * observations where the observations are created between the start
	 * date and end date
	 *
	 * @param location  location of the patient observation
	 * @param startDate min date of the observation created date
	 * @param endDate   max date of the observation created date
	 * @return cohort of patient with observation in from a certain location
	 *         that fall between the date range. Return empty cohort when no patient
	 *         id match the criteria.
	 * @throws APIException
	 * @should return all patient id with certain location on their
	 * observations between certain date
	 * @should return empty cohort when no patient match the criteria
	 */
	@Transactional(readOnly = true)
	Cohort getReturnDateCohort(final Location location, final Date startDate, final Date endDate) throws APIException;

	/**
	 * Get all patient id attached to a certain concepts on their
	 * observations where the observations are created between the start
	 * date and end date
	 *
	 * @param concepts  concepts in question of the patient observation
	 * @param startDate min date of the observation created date
	 * @param endDate   max date of the observation created date
	 * @return cohort of patient with observation from list of concepts that
	 *         fall between the date range. Return empty cohort when no patient id
	 *         match the criteria.
	 * @throws APIException
	 * @should return all patient id with certain location on their
	 * observations between certain date
	 * @should return empty cohort when no patient match the criteria
	 */
	@Transactional(readOnly = true)
	Cohort getObservationCohort(List<Concept> concepts, Date startDate, Date endDate) throws APIException;

	/**
	 * Get all patient encounters that match the encounter types, locations
	 * and providers criteria
	 *
	 * @param patientId           the patient
	 * @param restrictions        list of all possible restrictions for encounters
	 * @param mohFetchRestriction additional parameters for fetching
	 *                            encounters
	 * @return all encounters that match the criteria or empty list when no
	 *         encounters match the criteria
	 * @throws APIException
	 * @should return all encounters that match the search criteria
	 * @should return empty list when no encounter match the criteria
	 */
	@Transactional(readOnly = true)
	@Authorized({PrivilegeConstants.VIEW_ENCOUNTERS})
	List<Encounter> getPatientEncounters(
			final Integer patientId,
			final Map<String, Collection<OpenmrsObject>> restrictions,
			final MohFetchRestriction mohFetchRestriction,
			final Date evaluationDate) throws APIException;

	/**
	 * Get all patient observations that match the encounter , locations ,
	 * concept and value coded criteria
	 *
	 * @param patientId           the patient
	 * @param restrictions        list of all possible restrictions for
	 *                            observations
	 * @param mohFetchRestriction additional parameters for fetching
	 *                            observations
	 * @return all observations that match the criteria or empty list when
	 *         no observations match the criteria
	 * @throws APIException
	 * @should return all observations that match the search criteria
	 * @should return empty list when no observation match the criteria
	 */
	@Transactional(readOnly = true)
	@Authorized({PrivilegeConstants.VIEW_OBS})
	List<Obs> getPatientObservations(
			final Integer patientId,
			final Map<String, Collection<OpenmrsObject>> restrictions,
			final MohFetchRestriction mohFetchRestriction,
			final Date evaluationDate) throws APIException;

	@Transactional(readOnly = true)
	List<Obs> getPatientObservationsWithEncounterRestrictions(
			final Integer patientId,
			final Map<String, Collection<OpenmrsObject>> obsRestrictions,
			final Map<String, Collection<OpenmrsObject>> encounterRestrictions,
			final MohFetchRestriction mohFetchRestriction,
			final Date evaluationDate) throws APIException;

	@Transactional(readOnly = true)
	public Map<Integer, Date> getEnrollmentDateMap(Set<Integer> cohort);

	@Transactional(readOnly = true)
	public PatientIdentifierType getCCCNumberIdentifierType();

    @Transactional(readOnly = true)
    public PersonAttributeType getTBRegAttributeType();

	@Transactional(readOnly = true)
	public List<Object> executeScrollingHqlQuery(String query, Map<String, Object> substitutions);

	@Transactional(readOnly = true)
	public List<Object> executeSqlQuery(String query, Map<String, Object> substitutions);

	@Transactional(readOnly = true)
	public List<Object> executeHqlQuery(String query, Map<String, Object> substitutions);
}