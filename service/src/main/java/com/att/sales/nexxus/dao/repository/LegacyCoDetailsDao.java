package com.att.sales.nexxus.dao.repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;

import com.att.sales.nexxus.dao.model.LegacyCoDetails;

@Component
public class LegacyCoDetailsDao {
	@PersistenceContext
	private EntityManager em;
	
	public LegacyCoDetails findOne(String swcclli) {
		String queryString = "FROM LegacyCoDetails WHERE swcclli = :swcclli";
		TypedQuery<LegacyCoDetails> query = em.createQuery(queryString, LegacyCoDetails.class);
		query.setParameter("swcclli", swcclli);
		return query.getResultList().stream().findFirst().orElse(null);
	}
	
	public List<String> findAllSwcclliSorted() {
		String queryString = "SELECT lcd.swcclli FROM LegacyCoDetails lcd ORDER BY lcd.swcclli";
		TypedQuery<String> query = em.createQuery(queryString, String.class);
		return query.getResultList();
	}
	
	public List<LegacyCoDetails> findSyncRecords(String source) {
		String queryString = "FROM LegacyCoDetails WHERE status = :added or status = :modified";
		TypedQuery<LegacyCoDetails> query = em.createQuery(queryString, LegacyCoDetails.class);
		query.setParameter("added", source + "_add");
		query.setParameter("modified", source + "_modify");
		return query.getResultList();
	}
	
	public Map<String, String> getOcnToCompanyNameMap() {
		Map<String, String> res = new HashMap<>();
		String queryString = "SELECT DISTINCT lcd.ocn, lcd.operatingCompName FROM LegacyCoDetails lcd WHERE lcd.ocn is not null";
		Query query = em.createQuery(queryString);
		List<Object[]> resultList = query.getResultList();
		for (Object[] obj : resultList) {
			if (res.containsKey((String) obj[0])) {
				res.put((String) obj[0], null);
			} else {
				res.put((String) obj[0], (String) obj[1]);
			}
		}
		return res;
	}
	
	public Map<String, String> getOcnToCategoryMap() {
		Map<String, String> res = new HashMap<>();
		String queryString = "SELECT DISTINCT lcd.ocn, lcd.category FROM LegacyCoDetails lcd WHERE lcd.ocn is not null";
		Query query = em.createQuery(queryString);
		List<Object[]> resultList = query.getResultList();
		for (Object[] obj : resultList) {
			if (res.containsKey((String) obj[0])) {
				res.put((String) obj[0], null);
			} else {
				res.put((String) obj[0], (String) obj[1]);
			}
		}
		return res;
	}
	
	public int updateCompanyName(String companyName, String ocn, Date modifiedDate) {
		int result = 0;
		String queryString = "UPDATE LegacyCoDetails SET operatingCompName = :companyName, modifiedDate = :modifiedDate WHERE (operatingCompName is null or operatingCompName <> :companyName) and ocn = :ocn and status = 'dcc_add'";
		Query query = em.createQuery(queryString);
		query.setParameter("companyName", companyName);
		query.setParameter("ocn", ocn);
		query.setParameter("modifiedDate", modifiedDate);
		result += query.executeUpdate();
		queryString = "UPDATE LegacyCoDetails SET operatingCompName = :companyName, modifiedDate = :modifiedDate, status = 'dcc_modify' WHERE (operatingCompName is null or operatingCompName <> :companyName) and ocn = :ocn and (status <> 'dcc_add' or status is null)";
		query = em.createQuery(queryString);
		query.setParameter("companyName", companyName);
		query.setParameter("ocn", ocn);
		query.setParameter("modifiedDate", modifiedDate);
		result += query.executeUpdate();
		return result;
	}
	
	public int updateCategory(String category, String ocn, Date modifiedDate) {
		int result = 0;
		String queryString = "UPDATE LegacyCoDetails SET category = :category, modifiedDate = :modifiedDate WHERE (category is null or category <> :category) and ocn = :ocn and status = 'dcc_add'";
		Query query = em.createQuery(queryString);
		query.setParameter("category", category);
		query.setParameter("ocn", ocn);
		query.setParameter("modifiedDate", modifiedDate);
		result += query.executeUpdate();
		queryString = "UPDATE LegacyCoDetails SET category = :category, modifiedDate = :modifiedDate, status = 'dcc_modify' WHERE (category is null or category <> :category) and ocn = :ocn and (status <> 'dcc_add' or status is null)";
		query = em.createQuery(queryString);
		query.setParameter("category", category);
		query.setParameter("ocn", ocn);
		query.setParameter("modifiedDate", modifiedDate);
		result += query.executeUpdate();
		return result;
	}
	
	public int updateCompleteStatus(List<String> ids, String status) {
		final int chunkSize = 1000;
		int result = 0;
		for (int i = 0; i < ids.size(); i += chunkSize) {
			List<String> subList = ids.subList(i, Math.min(i + chunkSize, ids.size()));
			String queryString = "UPDATE LegacyCoDetails SET status = :status where swcclli in :ids";
			Query query = em.createQuery(queryString);
			query.setParameter("status", status);
			query.setParameter("ids", subList);
			result += query.executeUpdate();
		}
		return result;
	}
	
	public void bulkSave(List<LegacyCoDetails> legacyCoDetailsSavedList) {
		for (LegacyCoDetails legacyCoDetails : legacyCoDetailsSavedList) {
			em.persist(legacyCoDetails);
		}
		em.flush();
		em.clear();
	}
}
