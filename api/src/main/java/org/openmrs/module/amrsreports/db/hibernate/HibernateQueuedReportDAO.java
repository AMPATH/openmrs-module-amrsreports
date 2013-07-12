package org.openmrs.module.amrsreports.db.hibernate;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Location;
import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.amrsreports.QueuedReport;
import org.openmrs.module.amrsreports.db.QueuedReportDAO;

import java.util.*;

/**
 * Hibernate implementation of the QueuedReportDAO
 */
public class HibernateQueuedReportDAO implements QueuedReportDAO {

	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public QueuedReport saveQueuedReport(QueuedReport queuedReport) {
		sessionFactory.getCurrentSession().saveOrUpdate(queuedReport);
		return queuedReport;
	}

	@Override
	public QueuedReport getNextQueuedReport(Date date) {

		Criteria c = sessionFactory.getCurrentSession().createCriteria(QueuedReport.class)
				.add(Restrictions.le("dateScheduled", date))
				.add(Restrictions.in("status", new String[]{ QueuedReport.STATUS_RUNNING, QueuedReport.STATUS_NEW }))
				.addOrder(Order.asc("dateScheduled"))
				.setMaxResults(1);

		return (QueuedReport) c.uniqueResult();
	}

	@Override
	public void purgeQueuedReport(QueuedReport queuedReport) {
		sessionFactory.getCurrentSession().delete(queuedReport);
	}

	@Override
	public List<QueuedReport> getAllQueuedReports() {
		return sessionFactory.getCurrentSession().createCriteria(QueuedReport.class)
				.addOrder(Order.asc("dateScheduled"))
				.list();
	}

	@Override
	public List<QueuedReport> getQueuedReportsWithStatus(String status) {
		return sessionFactory.getCurrentSession().createCriteria(QueuedReport.class)
				.add(Restrictions.eq("status", status))
				.addOrder(Order.asc("dateScheduled"))
				.list();
	}

	@Override
	public QueuedReport getQueuedReport(Integer reportId) {
		return (QueuedReport) sessionFactory.getCurrentSession().get(QueuedReport.class, reportId);
	}

    public Map<MOHFacility,List<QueuedReport>> completedReports(){

        Map<MOHFacility,List<QueuedReport>> finalMap = new HashMap<MOHFacility, List<QueuedReport>>();
        List<QueuedReport> completeReports = getQueuedReportsWithStatus(QueuedReport.STATUS_COMPLETE);
        Set<MOHFacility> requiredFacilities = new HashSet<MOHFacility>();
        for(QueuedReport thisReport:completeReports){
            MOHFacility thisMohFacility = thisReport.getFacility();
            requiredFacilities.add(thisMohFacility);

        }

        for(MOHFacility facilityLoop:requiredFacilities){
            List<QueuedReport> relevantReports = new ArrayList<QueuedReport>();
            for(QueuedReport queuedReportLoop:completeReports){
                if(facilityLoop.equals(queuedReportLoop.getFacility())){
                   relevantReports.add(queuedReportLoop);
                }

            }

            finalMap.put(facilityLoop,relevantReports);

        }

      return finalMap;

    }
}
