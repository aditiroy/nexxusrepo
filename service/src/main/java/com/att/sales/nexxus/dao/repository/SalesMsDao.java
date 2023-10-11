package com.att.sales.nexxus.dao.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Component;

@Component
public class SalesMsDao {
	@PersistenceContext
	private EntityManager em;
	
	@SuppressWarnings("rawtypes")
	public String getOfferNameByOfferId(int offerId) {
		String res = null;
		String queryString = "SELECT offernameabbr FROM SALES_MS_OFFER WHERE offer_id = :offerId AND active = 'Active'";
		Query query = em.createNativeQuery(queryString);
		query.setParameter("offerId", offerId);
		List resultList = query.getResultList();
		if (!resultList.isEmpty()) {
			res = resultList.get(0).toString();
		}
		return res;
	}
	
	@SuppressWarnings("rawtypes")
	public String getOfferIdByOfferName(String offernameabbr) {
		String res = null;
		String queryString = "SELECT offer_id FROM SALES_MS_OFFER WHERE offernameabbr = :offernameabbr AND active = 'Active'";
		Query query = em.createNativeQuery(queryString);
		query.setParameter("offernameabbr", offernameabbr);
		List resultList = query.getResultList();
		if (!resultList.isEmpty()) {
			res = resultList.get(0).toString();
		}
		return res;
	}
}
