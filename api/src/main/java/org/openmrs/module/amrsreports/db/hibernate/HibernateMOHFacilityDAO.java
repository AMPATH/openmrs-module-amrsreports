package org.openmrs.module.amrsreports.db.hibernate;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.amrsreports.db.MOHFacilityDAO;

import java.util.List;

/**
 * Hibernate implementation of MOHFacilityDAO
 */
public class HibernateMOHFacilityDAO implements MOHFacilityDAO {

	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public List<MOHFacility> getAllFacilities(Boolean includeRetired) {
		Criteria c = sessionFactory.getCurrentSession().createCriteria(MOHFacility.class);
		c.addOrder(Order.asc("name"));
		if (!includeRetired)
			c.add(Restrictions.eq("retired", false));
		return c.list();
	}

	@Override
	public MOHFacility getFacility(Integer facilityId) {
		return (MOHFacility) sessionFactory.getCurrentSession().get(MOHFacility.class, facilityId);
	}

	@Override
	public MOHFacility saveFacility(MOHFacility facility) {
		sessionFactory.getCurrentSession().saveOrUpdate(facility);
		return facility;
	}

	@Override
	public void purgeFacility(MOHFacility facility) {
		sessionFactory.getCurrentSession().delete(facility);
	}
}
