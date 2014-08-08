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
package org.openmrs.module.amrsreports.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.OpenmrsObject;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.amrsreports.AmrsReportsConstants;
import org.openmrs.module.amrsreports.db.MohCoreDAO;
import org.openmrs.module.amrsreports.service.MohCoreService;
import org.openmrs.module.amrsreports.util.MohFetchRestriction;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Actual implementation of the core service contract
 */
public class MohCoreServiceImpl extends BaseOpenmrsService implements MohCoreService {

	private static final Log log = LogFactory.getLog(MohCoreServiceImpl.class);
	private MohCoreDAO mohCoreDAO;

	/**
	 * Setter for the DAO interface reference that will be called by Spring to inject the actual implementation of the DAO
	 * layer
	 *
	 * @param mohCoreDAO the coreDAO to be injected
	 */
	public void setCoreDAO(final MohCoreDAO mohCoreDAO) {
		this.mohCoreDAO = mohCoreDAO;
	}

	/**
	 * @see MohCoreService#getDateCreatedCohort(org.openmrs.Location, java.util.Date, java.util.Date)
	 */
	@Override
	public Cohort getDateCreatedCohort(final Location location, final Date startDate, final Date endDate) throws APIException {
		return mohCoreDAO.getDateCreatedCohort(location, startDate, endDate);
	}

	/**
	 * @see MohCoreService#getReturnDateCohort(org.openmrs.Location, java.util.Date, java.util.Date)
	 */
	@Override
	public Cohort getReturnDateCohort(final Location location, final Date startDate, final Date endDate) throws APIException {
		return mohCoreDAO.getReturnDateCohort(location, startDate, endDate);
	}

	/**
	 * @see MohCoreService#getObservationCohort(java.util.List, java.util.Date, java.util.Date)
	 */
	@Override
	public Cohort getObservationCohort(final List<Concept> concepts, final Date startDate, final Date endDate) throws APIException {
		return mohCoreDAO.getObservationCohort(concepts, startDate, endDate);
	}

	/**
	 * @see MohCoreService#getPatientEncounters(Integer, java.util.Map, org.openmrs.module.amrsreports.util.MohFetchRestriction, java.util.Date)
	 */
	@Override
	public List<Encounter> getPatientEncounters(final Integer patientId,
												final Map<String, Collection<OpenmrsObject>> restrictions,
												final MohFetchRestriction mohFetchRestriction,
												final Date evaluationDate) throws APIException {
		return mohCoreDAO.getPatientEncounters(patientId, restrictions, mohFetchRestriction, evaluationDate);
	}

	/**
	 * @see MohCoreService#getPatientObservations(Integer, java.util.Map, org.openmrs.module.amrsreports.util.MohFetchRestriction, java.util.Date)
	 */
	@Override
	public List<Obs> getPatientObservations(final Integer patientId,
											final Map<String, Collection<OpenmrsObject>> restrictions,
											final MohFetchRestriction mohFetchRestriction,
											final Date evaluationDate) throws APIException {
		return mohCoreDAO.getPatientObservations(patientId, restrictions, mohFetchRestriction, evaluationDate);
	}

	@Override
	public List<Obs> getPatientObservationsWithEncounterRestrictions(
			Integer patientId,
			Map<String, Collection<OpenmrsObject>> obsRestrictions,
			Map<String, Collection<OpenmrsObject>> encounterRestrictions,
			MohFetchRestriction mohFetchRestriction,
			Date evaluationDate) throws APIException {
		return mohCoreDAO.getPatientObservationsWithEncounterRestrictions(
				patientId, obsRestrictions, encounterRestrictions, mohFetchRestriction, evaluationDate);
	}

	@Override
	public Map<Integer, Date> getEnrollmentDateMap(Set<Integer> cohort) {
		return mohCoreDAO.getEnrollmentDateMap(cohort);
	}

	@Override
	public PatientIdentifierType getCCCNumberIdentifierType() {
		String typeId = Context.getAdministrationService().getGlobalProperty(AmrsReportsConstants.GP_CCC_NUMBER_IDENTIFIER_TYPE);
        try{
		    return Context.getPatientService().getPatientIdentifierType(Integer.valueOf(typeId));
        }catch (NumberFormatException nfe){
            log.error(AmrsReportsConstants.GP_CCC_NUMBER_IDENTIFIER_TYPE+"is not defined or improperly defined!",nfe);
        }
        return null;
	}

    @Override
    public PersonAttributeType getTBRegAttributeType() {
        String typeId = Context.getAdministrationService().getGlobalProperty(AmrsReportsConstants.TB_REGISTRATION_NO_ATTRIBUTE_TYPE);
        return Context.getPersonService().getPersonAttributeType(Integer.valueOf(typeId));
    }

    @Override
	public List<Object> executeScrollingHqlQuery(String query, Map<String, Object> substitutions) {
		return mohCoreDAO.executeScrollingHqlQuery(query, substitutions);
	}

	@Override
	public List<Object> executeSqlQuery(String query, Map<String, Object> substitutions) {
		return mohCoreDAO.executeSqlQuery(query, substitutions);
	}

	@Override
	public List<Object> executeHqlQuery(String query, Map<String, Object> substitutions) {
		return mohCoreDAO.executeHqlQuery(query, substitutions);
	}
}
