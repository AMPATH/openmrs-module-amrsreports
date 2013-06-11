package org.openmrs.module.amrsreports.service;

import org.openmrs.Patient;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.amrsreports.HIVCareEnrollment;
import org.springframework.transaction.annotation.Transactional;

/**
 * service for operations on HIVCareEnrollments
 */
public interface HIVCareEnrollmentService extends OpenmrsService {

	@Transactional
	public HIVCareEnrollment getHIVCareEnrollmentForPatient(Patient patient);
}
