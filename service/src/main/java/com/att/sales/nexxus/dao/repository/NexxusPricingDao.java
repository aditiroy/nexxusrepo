package com.att.sales.nexxus.dao.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Component;

@Component
public class NexxusPricingDao {

	@PersistenceContext
	private EntityManager em;
	
	public String getUdfValue(long udfId) {
		String queryString = "SELECT udf_abbr FROM sales_ms_udf WHERE udf_id = :udfId";
		Query query = em.createNativeQuery(queryString);
		query.setParameter("udfId", udfId);
		return (String) query.getSingleResult();
	}
	
	public String getAttributeValue(long udfAttributeId) {
		String queryString = "SELECT attribute_value FROM sales_ms_attribute WHERE attribute_id = :attributeId";
		Query query = em.createNativeQuery(queryString);
		query.setParameter("attributeId", udfAttributeId);
		return (String) query.getSingleResult();
	}
}
