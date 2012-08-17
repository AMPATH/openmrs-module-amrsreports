/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.amrsreport.db;


import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.OpenmrsObject;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.amrsreport.util.MohFetchRestriction;

/**
 */
public interface MohCoreDAO {

	Cohort getDateCreatedCohort(final Location location, final Date startDate, Date endDate) throws DAOException;

	Cohort getReturnDateCohort(final Location location, final Date startDate, final Date endDate) throws DAOException;

	Cohort getObservationCohort(List<Concept> concepts, Date startDate, Date endDate) throws DAOException;

	List<Encounter> getPatientEncounters(final Integer patientId, final Map<String, Collection<OpenmrsObject>> restrictions,
	                                     final MohFetchRestriction mohFetchRestriction) throws DAOException;

	List<Obs> getPatientObservations(final Integer patientId, final Map<String, Collection<OpenmrsObject>> restrictions,
	                                 final MohFetchRestriction mohFetchRestriction) throws DAOException;
	
	List<PatientIdentifier> getAllPatientIdenifiers(Patient p);
}
