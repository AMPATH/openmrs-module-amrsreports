package org.openmrs.module.amrsreports.db.hibernate;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.amrsreports.QueuedReport;
import org.openmrs.module.amrsreports.db.QueuedReportDAO;

import java.util.Date;
import java.util.List;

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
				.addOrder(Order.asc("dateScheduled"))
				.setFetchSize(1);

		return (QueuedReport) c.uniqueResult();
	}

	@Override
	public void purgeQueuedReport(QueuedReport queuedReport) {
		sessionFactory.getCurrentSession().delete(queuedReport);
	}

	@Override
	public List<QueuedReport> getAllQueuedReports() {
		return sessionFactory.getCurrentSession().createCriteria(QueuedReport.class).list();
	}
}
