package org.openmrs.module.amrsreports.db.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Patient;
import org.openmrs.module.amrsreports.HIVCareEnrollment;
import org.openmrs.module.amrsreports.db.HIVCareEnrollmentDAO;

/**
 * Hibernate implementation of HIVCareEnrollmentDAO
 */
public class HibernateHIVCareEnrollmentDAO implements HIVCareEnrollmentDAO {

	private final Log log = LogFactory.getLog(getClass());

	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public HIVCareEnrollment getHIVCareEnrollmentForPatient(Patient patient) {
		return (HIVCareEnrollment) sessionFactory.getCurrentSession()
				.createCriteria(HIVCareEnrollment.class)
				.add(Restrictions.eq("patient", patient))
				.uniqueResult();
	}

}
