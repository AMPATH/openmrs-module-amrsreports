package org.openmrs.module.amrsreport.enrollment;

import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreport.Enrollment;
import org.openmrs.module.amrsreport.service.MohCoreService;

import java.util.List;

/**
 * Provides a mechanism for enrolling patients to a given register based on criteria from registered
 * EnrollmentProviders.
 */
public class EnrollmentManager {

	private static EnrollmentManager instance;

	/**
	 * Singleton instance getter
	 */
	public static EnrollmentManager getInstance() {
		if (instance == null)
			instance = new EnrollmentManager();
		return instance;
	}

	public void rebuildAll() {

		// loop through all EnrollmentProfiles and build them
		// ...
		// or just do what we know about right now
		EnrollmentProfile profile = new MOH361AEnrollmentProfile();
		profile.enroll();
	}
}
