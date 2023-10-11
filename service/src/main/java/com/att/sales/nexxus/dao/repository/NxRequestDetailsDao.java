package com.att.sales.nexxus.dao.repository;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.springframework.stereotype.Component;

/**
 * The Class NxRequestDetailsDao.
 */
@Component
public class NxRequestDetailsDao {

	/** The em. */
	@PersistenceContext
	private EntityManager em;

	/**
	 * Update no dmaap notification status.
	 *
	 * @param noDmaapStatus the no dmaap status
	 * @param inProgressStatus the in progress status
	 * @param dateThreshold the date threshold
	 * @return the int
	 */
	public int updateNoDmaapNotificationStatus(long noDmaapStatus, long inProgressStatus, Date dateThreshold) {
		Query query = em.createQuery("UPDATE NxRequestDetails SET status = :noDmaapStatus "
				+ "WHERE status = :inProgressStatus AND createdDate < :dateThreshold");
		query.setParameter("noDmaapStatus", noDmaapStatus);
		query.setParameter("inProgressStatus", inProgressStatus);
		query.setParameter("dateThreshold", dateThreshold, TemporalType.DATE);
		return query.executeUpdate();
	}
}
