package org.openmrs.module.amrsreports.db;

import org.openmrs.Patient;
import org.openmrs.module.amrsreports.HIVCareEnrollment;

/**
 * DAO for HIVCareEnrollments
 */
public interface HIVCareEnrollmentDAO {

	public HIVCareEnrollment getHIVCareEnrollmentForPatient(Patient patient);

}
