package org.openmrs.module.amrsreports.db.hibernate;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.User;
import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.amrsreports.UserFacility;
import org.openmrs.module.amrsreports.db.UserFacilityDAO;

import java.util.List;

/**
 * Hibernate implementation of UserFacilityDAO
 */
public class HibernateUserFacilityDAO implements UserFacilityDAO {

	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}


	@Override
	public UserFacility saveUserFacility(UserFacility userFacility) {
		sessionFactory.getCurrentSession().saveOrUpdate(userFacility);
		return userFacility;
	}

	@Override
	public UserFacility getUserFacility(Integer userFacilityId) {
		return (UserFacility) sessionFactory.getCurrentSession().get(UserFacility.class, userFacilityId);
	}

	@Override
	public List<UserFacility> getAllUserFacilities() {
		Criteria c = sessionFactory.getCurrentSession().createCriteria(UserFacility.class);
		return c.list();
	}

	@Override
	public void purgeUserFacility(UserFacility userFacility) {
		sessionFactory.getCurrentSession().delete(userFacility);
	}

	@Override
	public List<MOHFacility> getAllowedFacilitiesForUser(User user) {
		Criteria c = sessionFactory.getCurrentSession().createCriteria(UserFacility.class)
				.add(Restrictions.eq("user", user))
				.setProjection(Projections.property("facility"));

		return c.list();
	}

	@Override
	public UserFacility getUserFacilityFor(User user, MOHFacility facility) {
		Criteria c = sessionFactory.getCurrentSession().createCriteria(UserFacility.class)
				.add(Restrictions.eq("user", user))
				.add(Restrictions.eq("facility", facility));

		return (UserFacility) c.uniqueResult();
	}
}
