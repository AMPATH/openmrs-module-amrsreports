package org.openmrs.module.amrsreports.db.hibernate;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Location;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.amrsreports.db.MOHFacilityDAO;
import org.openmrs.module.amrsreports.service.MohCoreService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hibernate implementation of MOHFacilityDAO
 */
public class HibernateMOHFacilityDAO implements MOHFacilityDAO {

	private final Log log = LogFactory.getLog(getClass());

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

	public Map<Integer, String> getSerialNumberMapForFacility(MOHFacility facility) {
		PatientIdentifierType pit = Context.getService(MohCoreService.class).getCCCNumberIdentifierType();

		String hql = "select pi.patient.patientId, substring(pi.identifier,7,5)" +
				"	from PatientIdentifier as pi" +
				"	where" +
				"		pi.voided = false" +
				"		and pi.identifierType.id = :identifierTypeId" +
				"		and substring(pi.identifier,1,5) = :facilityCode";

		Query q = sessionFactory.getCurrentSession().createQuery(hql);
		q.setInteger("identifierTypeId", pit.getId());
		q.setString("facilityCode", facility.getCode());

		List<Object> res = q.list();
		Map<Integer, String> m = new HashMap<Integer, String>();
		for (Object r : res) {
			Object[] a = (Object[]) r;
			m.put((Integer) a[0], (String) a[1]);
		}

		return m;
	}

	@Override
	public List<PatientIdentifier> getCCCNumbersForFacility(MOHFacility facility) {
		PatientIdentifierType pit = Context.getService(MohCoreService.class).getCCCNumberIdentifierType();

		// fail silently if no facility or patient identifier type is found
		if (facility == null) {
			log.warn("No facility provided; returning empty data.");
			return new ArrayList<PatientIdentifier>();
		}

		if (pit == null) {
			log.warn("No CCC patient identifier type found; returning empty data.");
			return new ArrayList<PatientIdentifier>();
		}

		Criteria c = sessionFactory.getCurrentSession().createCriteria(PatientIdentifier.class)
				.add(Restrictions.eq("voided", false))
				.add(Restrictions.eq("identifierType", pit))
				.add(Restrictions.like("identifier", facility.getCode(), MatchMode.START));

		return c.list();
	}

	@Override
	public Map<String, Integer> getFacilityCodeToLatestSerialNumberMap() {
		PatientIdentifierType pit = Context.getService(MohCoreService.class).getCCCNumberIdentifierType();

		String hql = "select max(pi.identifier)" +
				" from PatientIdentifier as pi" +
				" where pi.voided = false" +
				"   and pi.identifierType = :identifierType" +
				" group by substring(pi.identifier, 1, 5)";

		Query q = sessionFactory.getCurrentSession().createQuery(hql);
		q.setParameter("identifierType", pit);

		Map<String, Integer> m = new HashMap<String, Integer>();

		for (Object o : q.list()) {
			String id = (String) o;
			String[] s = id.split("-");
			m.put(s[0], Integer.parseInt(s[1]));
		}

		return m;
	}

	@Override
	public List<Integer> getPatientsInCohortMissingCCCNumbers(List<Integer> c) {
		PatientIdentifierType pit = Context.getService(MohCoreService.class).getCCCNumberIdentifierType();

		if (c == null || c.isEmpty())
			return new ArrayList<Integer>();

		String sql = "select p.person_id" +
				" from person p left join patient_identifier pi" +
				"   on pi.patient_id = p.person_id" +
				"     and pi.identifier_type = " + pit.getPatientIdentifierTypeId() +
				"     and pi.voided = 0" +
				" where" +
				"   p.person_id in (" + StringUtils.join(c, ",") +
				")" +
				"	and pi.uuid is null";

		SQLQuery q = sessionFactory.getCurrentSession().createSQLQuery(sql);

		return (List<Integer>) q.list();
	}

	@Override
	public Integer getLatestSerialNumberForFacility(MOHFacility facility) {
		if (facility == null)
			return -1;

		PatientIdentifierType pit = Context.getService(MohCoreService.class).getCCCNumberIdentifierType();

		String hql = "select max(substring(pi.identifier, 7, 5))" +
				" from PatientIdentifier as pi" +
				" where pi.voided = false" +
				"   and pi.identifierType = :identifierType" +
				"   and substring(pi.identifier, 1, 5) = :code";

		Query q = sessionFactory.getCurrentSession().createQuery(hql);
		q.setParameter("identifierType", pit);
		q.setParameter("code", facility.getCode());

		String serial = (String) q.uniqueResult();
		return Integer.parseInt(serial);
	}

	@Override
	public List<Integer> getEnrolledPatientsForFacility(MOHFacility facility) {
		String hql = "select e.patient.patientId from HIVCareEnrollment e" +
				" where e.enrollmentLocation in (:locationList)" +
				"  and e.enrollmentDate is not null" +
				"  and e.transferredInDate is null" +
				" order by e.enrollmentDate asc";

		Query q = sessionFactory.getCurrentSession().createQuery(hql);
		q.setParameterList("locationList", new ArrayList<Location>(facility.getLocations()));

		return (List<Integer>) q.list();
	}

}
