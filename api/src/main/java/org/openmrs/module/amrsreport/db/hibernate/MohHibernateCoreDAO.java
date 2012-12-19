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
package org.openmrs.module.amrsreport.db.hibernate;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.OpenmrsObject;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.User;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.amrsreport.UserLocation;
import org.openmrs.module.amrsreport.UserReport;
import org.openmrs.module.amrsreport.cache.MohCacheUtils;
import org.openmrs.module.amrsreport.db.MohCoreDAO;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreport.util.MohFetchOrdering;
import org.openmrs.module.amrsreport.util.MohFetchRestriction;
import org.openmrs.util.OpenmrsUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class MohHibernateCoreDAO implements MohCoreDAO {

	private Log log = LogFactory.getLog(MohHibernateCoreDAO.class);
	private SessionFactory sessionFactory;

	/**
	 * Method that will be called by Spring to inject the Hibernate's
	 * SessionFactory.
	 *
	 * @param sessionFactory the session factory to be injected
	 */
	public void setSessionFactory(final SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * @see MohCoreDAO#getPatientObservations(Integer, java.util.Map,
	 *      org.openmrs.module.amrsreport.util.MohFetchRestriction)
	 */
	@Override
	public List<Obs> getPatientObservations(final Integer patientId, final Map<String, Collection<OpenmrsObject>> restrictions,
	                                        final MohFetchRestriction mohFetchRestriction) throws DAOException {
		// build the criteria
		Criteria criteria = buildPatientObservationsCriteria(patientId, restrictions, mohFetchRestriction);

		// process the observations
		List<Obs> observations = processObs(criteria, mohFetchRestriction);

		return observations;
	}

	/**
	 * @see MohCoreDAO#getPatientObservationsWithEncounterRestrictions(Integer, java.util.Map, java.util.Map, org.openmrs.module.amrsreport.util.MohFetchRestriction)
	 */
	@Override
	public List<Obs> getPatientObservationsWithEncounterRestrictions(final Integer patientId,
	                                                                 final Map<String, Collection<OpenmrsObject>> obsRestrictions,
	                                                                 final Map<String, Collection<OpenmrsObject>> encounterRestrictions,
	                                                                 final MohFetchRestriction mohFetchRestriction) {

		// build the criteria
		Criteria criteria = buildPatientObservationsCriteria(patientId, obsRestrictions, mohFetchRestriction);

		// add encounter criteria subquery
		Encounter encounter = new Encounter();
		Criteria encounterCriteria = criteria.createCriteria("encounter");
		for (String property : encounterRestrictions.keySet()) {
			Collection<OpenmrsObject> propertyValues = encounterRestrictions.get(property);
			if (CollectionUtils.isNotEmpty(propertyValues) && PropertyUtils.isReadable(encounter, property)) {
				encounterCriteria.add(Restrictions.in(property, propertyValues));
				encounterCriteria.addOrder(Order.asc(property));
			}
		}

		// process the observations
		List<Obs> observations = processObs(criteria, mohFetchRestriction);

		return observations;
	}

	/**
	 * post-process a list of observations from a criteria; duplicate data FTL
	 *
	 * @param criteria the object with data in it
	 * @param mohFetchRestriction information for limiting fetch, specifically the size
	 * @return a list of processed (cleaned) observations
	 */
	private List<Obs> processObs(Criteria criteria, MohFetchRestriction mohFetchRestriction) {
		List<Obs> observations = new ArrayList<Obs>();

		// TODO: further optimization would be adding start date and end date parameter in the obs restrictions
		ScrollableResults scrollableResults = criteria.scroll(ScrollMode.FORWARD_ONLY);

		Integer size = mohFetchRestriction.getSize();

		// scroll the results
		Obs processedObs = null;
		while (scrollableResults.next() && OpenmrsUtil.compareWithNullAsGreatest(observations.size(), size) == -1) {
			Obs obs = (Obs) scrollableResults.get(0);
			// TODO: thanks to Ampath for the duplicate data, we need to sanitize the query results here
			if (processedObs != null && !obs.isObsGrouping()) {
				if (DateUtils.isSameDay(processedObs.getObsDatetime(), obs.getObsDatetime())
						&& OpenmrsUtil.nullSafeEquals(processedObs.getConcept(), obs.getConcept())
						&& OpenmrsUtil.nullSafeEquals(processedObs.getValueCoded(), obs.getValueCoded())
						&& (OpenmrsUtil.nullSafeEquals(processedObs.getValueNumeric(), obs.getValueNumeric()))) {
					continue;
				}
			}
			processedObs = obs;
			observations.add(obs);
		}
		scrollableResults.close();
		return observations;
	}

	/**
	 * creates a Criteria from restrictions provided
	 *
	 * @param patientId the patient connected to the desired observations
	 * @param restrictions a map of restrictions on the observations
	 * @param mohFetchRestriction a map of fetch restrictions
	 * @return a Criteria for use in evaluation
	 */
	private Criteria buildPatientObservationsCriteria(final Integer patientId,
	                                                  final Map<String, Collection<OpenmrsObject>> restrictions,
	                                                  final MohFetchRestriction mohFetchRestriction) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Obs.class);
		criteria.add(Restrictions.eq("personId", patientId));

		criteria.setFirstResult(0);
		if (mohFetchRestriction.getStart() != null) {
			criteria.setFirstResult(mohFetchRestriction.getStart());
		}

		if (mohFetchRestriction.getSize() != null && mohFetchRestriction.getSize() == 1) {
			criteria.setMaxResults(mohFetchRestriction.getSize());
		}

		Order order = Order.desc("obsDatetime");
		if (OpenmrsUtil.nullSafeEquals(MohFetchOrdering.ORDER_ASCENDING, mohFetchRestriction.getFetchOrdering())) {
			order = Order.asc("obsDatetime");
		}

		criteria.addOrder(order);

		Obs observation = new Obs();
		for (String property : restrictions.keySet()) {
			Collection<OpenmrsObject> propertyValues = restrictions.get(property);
			if (CollectionUtils.isNotEmpty(propertyValues) && PropertyUtils.isReadable(observation, property)) {
				criteria.add(Restrictions.in(property, propertyValues));
				criteria.addOrder(Order.asc(property));
			}
		}

		criteria.add(Restrictions.eq("voided", Boolean.FALSE));

		return criteria;
	}

	/**
	 * @see MohCoreDAO#getPatientEncounters(Integer, java.util.Map,
	 *      org.openmrs.module.amrsreport.util.MohFetchRestriction)
	 */
	@Override
	public List<Encounter> getPatientEncounters(final Integer patientId, final Map<String, Collection<OpenmrsObject>> restrictions,
	                                            final MohFetchRestriction mohFetchRestriction) throws DAOException {
		// create a hibernate criteria on the encounter object
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Encounter.class);
		// restrict the encounter that will be returned to specific patient only
		criteria.add(Restrictions.eq("patientId", patientId));

		// default ordering for the returned encounter is descending on encounter datetime
		Order order = Order.desc("encounterDatetime");
		// change the default ordering if the user pass the ascending ordering in the parameters
		if (OpenmrsUtil.nullSafeEquals(MohFetchOrdering.ORDER_ASCENDING, mohFetchRestriction.getFetchOrdering())) {
			order = Order.asc("encounterDatetime");
		}
		// add the ordering object to the criteria
		criteria.addOrder(order);

		// always get from the first result
		criteria.setFirstResult(0);
		// set the first result to a number if the user pass any value on this parameter (currently not being used)
		if (mohFetchRestriction.getStart() != null) {
			criteria.setFirstResult(mohFetchRestriction.getStart());
		}

		// specify how many records should be returned
		if (mohFetchRestriction.getSize() != null) {
			criteria.setMaxResults(mohFetchRestriction.getSize());
		}

		// create a dummy encounter object
		Encounter encounter = new Encounter();
		// iterate over each property in the restriction map
		for (String property : restrictions.keySet()) {
			// get the actual object that will restrict the encounter. this will contains the list of encounter type or list of location
			// or list of provider (currently not being used) passed from caller
			Collection<OpenmrsObject> propertyValues = restrictions.get(property);
			// check to make sure the list is not empty and the property is readable. example of the property for encounter are
			// encounterType or location of the encounter
			if (CollectionUtils.isNotEmpty(propertyValues) && PropertyUtils.isReadable(encounter, property)) {
				// create a restriction on the property with the above list as the value
				criteria.add(Restrictions.in(property, propertyValues));
				// add ordering on that property to prevent slowness when ordering only on encounter datetime (1.6.x only)
				criteria.addOrder(Order.asc(property));
			}
		}

		// exclude all voided encounters
		criteria.add(Restrictions.eq("voided", Boolean.FALSE));

		List<Encounter> encounters = new ArrayList<Encounter>();

		// scroll the results and add them to the above list of encounter
		Integer counter = 0;
		ScrollableResults scrollableResults = criteria.scroll(ScrollMode.FORWARD_ONLY);
		while (scrollableResults.next()) {
			encounters.add((Encounter) scrollableResults.get(0));
		}
		scrollableResults.close();
		return encounters;
	}

	/**
	 * @see MohCoreDAO#getDateCreatedCohort(org.openmrs.Location,
	 *      java.util.Date, java.util.Date)
	 */
	@Override
	public Cohort getDateCreatedCohort(final Location location, final Date startDate, final Date endDate) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Obs.class);

		if (location != null) {
			criteria.add(Restrictions.eq("location", location));
		} else {
			criteria.add(Restrictions.isNull("location"));
		}

		if (startDate != null) {
			criteria.add(Restrictions.ge("dateCreated", startDate));
		}

		if (endDate != null) {
			criteria.add(Restrictions.le("dateCreated", endDate));
		}

		criteria.setProjection(Projections.property("person.personId"));
		criteria.add(Restrictions.eq("voided", Boolean.FALSE));
		return new Cohort(criteria.list());
	}

	/**
	 * @see MohCoreDAO#getDateCreatedCohort(org.openmrs.Location,
	 *      java.util.Date, java.util.Date)
	 */
	@Override
	public Cohort getReturnDateCohort(final Location location, final Date startDate, final Date endDate) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Obs.class);
		criteria.add(Restrictions.eq("concept", MohCacheUtils.getConcept(MohEvaluableNameConstants.RETURN_VISIT_DATE)));

		if (location != null) {
			criteria.add(Restrictions.eq("location", location));
		} else {
			criteria.add(Restrictions.isNull("location"));
		}

		if (startDate != null) {
			criteria.add(Restrictions.ge("valueDatetime", startDate));
		}

		if (endDate != null) {
			criteria.add(Restrictions.le("valueDatetime", endDate));
		}

		criteria.setProjection(Projections.property("person.personId"));
		criteria.add(Restrictions.eq("voided", Boolean.FALSE));
		return new Cohort(criteria.list());
	}

	/**
	 * @see MohCoreDAO#getObservationCohort(java.util.List, java.util.Date,
	 *      java.util.Date)
	 */
	@Override
	public Cohort getObservationCohort(final List<Concept> concepts, final Date startDate, final Date endDate) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Obs.class);
		criteria.add(Restrictions.in("concept", concepts));

		if (startDate != null) {
			criteria.add(Restrictions.ge("dateCreated", startDate));
		}

		if (endDate != null) {
			criteria.add(Restrictions.le("dateCreated", endDate));
		}

		criteria.setProjection(Projections.property("person.personId"));
		criteria.add(Restrictions.eq("voided", Boolean.FALSE));
		return new Cohort(criteria.list());
	}

	@Override
	public List<PatientIdentifier> getAllPatientIdenifiers(Patient p) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PatientIdentifier.class);
		criteria.add(Restrictions.eq("patient", p));
		return criteria.list();
	}

	/*  Save report user
	 */
	@Override
	public UserReport saveUserReport(UserReport userReport) {
		sessionFactory.getCurrentSession().saveOrUpdate(userReport);

		return userReport;
	}

	@Override
	public UserReport getUserReport(Integer userReportId) {
		return (UserReport) sessionFactory.getCurrentSession().get(UserReport.class, userReportId);
	}

	@Override
	public UserReport getUserReportByUuid(String uuid) {
		return (UserReport) sessionFactory.getCurrentSession().
				createCriteria(UserReport.class)
				.add(Expression.eq("uuid", uuid))
				.uniqueResult();
	}

	@Override
	public void purgeUserReport(UserReport userReport) {
		sessionFactory.getCurrentSession().delete(userReport);
	}

	/**
	 * UserLocation methods
	 */
	@Override
	public UserLocation saveUserLocation(UserLocation userlocation) {
		sessionFactory.getCurrentSession().saveOrUpdate(userlocation);
		return userlocation;
	}

	@Override
	public UserLocation getUserLocation(Integer userlocationId) {
		return (UserLocation) sessionFactory.getCurrentSession().get(UserLocation.class, userlocationId);
	}

	@Override
	public List<UserLocation> getAllUserLocationPrivileges() {
		Criteria crt = sessionFactory.getCurrentSession().createCriteria(UserLocation.class);
		return crt.list();
	}

	@Override
	public Boolean hasLocationPrivilege(User user, Location location) {
		return (Integer) sessionFactory.getCurrentSession().createCriteria(UserLocation.class)
				.add(Restrictions.eq("sysUser", user))
				.add(Restrictions.eq("userLoc", location))
				.setProjection(Projections.rowCount())
				.uniqueResult()
				>= 1;
	}

	@Override
	public void deleteUserLocation(UserLocation userLocation) {
		sessionFactory.getCurrentSession().delete(userLocation);
	}

	@Override
	public List<UserLocation> getUserLocationsForUser(User user) {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(UserLocation.class)
				.add(Restrictions.eq("sysUser", user));
		return (List<UserLocation>) crit.list();
	}

	@Override
	public List<UserReport> getUserReportsForUser(User user) {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(UserReport.class)
				.add(Restrictions.eq("amrsReportsUser", user));
		return (List<UserReport>) crit.list();
	}

}
