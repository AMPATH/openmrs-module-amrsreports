package org.openmrs.module.amrsreport.enrollment;

/**
 * Interface for enrollment providers; registers requiring specific enrollment criteria implement this.
 */
public interface EnrollmentProfile {

	/**
	 * perform enrollment for this profile
	 */
	public void enroll();

}
