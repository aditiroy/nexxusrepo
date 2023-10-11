package com.att.sales.nexxus.dao.repository;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;

import com.att.sales.nexxus.dao.model.LegacyCoPercentage;
import com.att.sales.nexxus.dao.model.LegacyCoPercentage.Pk;

@Component
public class LegacyCoPercentageDao {
	@PersistenceContext
	private EntityManager em;
	
	public Set<Pk> findPkByPkIn(List<Pk> pks) {
		final int chunkSize = 1000;
		Set<Pk> result = new HashSet<>();
		for (int i = 0; i < pks.size(); i += chunkSize) {
			List<Pk> subList = pks.subList(i, Math.min(i + chunkSize, pks.size()));
			String queryString = "SELECT pk FROM LegacyCoPercentage WHERE pk in :pks";
			TypedQuery<Pk> query = em.createQuery(queryString, Pk.class);
			query.setParameter("pks", subList);
			List<Pk> resultList = query.getResultList();
			result.addAll(resultList);
		}
		return result;
	}
	
	public List<LegacyCoPercentage> findSyncRecords(String source) {
		String queryString = "FROM LegacyCoPercentage WHERE status = :added or status = :modified";
		TypedQuery<LegacyCoPercentage> query = em.createQuery(queryString, LegacyCoPercentage.class);
		query.setParameter("added", source + "_add");
		query.setParameter("modified", source + "_modify");
		return query.getResultList();
	}

	public Map<String, String> getOcnToCompanyNameMap() {
		Map<String, String> res = new HashMap<>();
		String queryString = "SELECT DISTINCT lcp.pk.ocn, lcp.companyName FROM LegacyCoPercentage lcp WHERE lcp.pk.ocn is not null";
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
		String queryString = "UPDATE LegacyCoPercentage SET companyName = :companyName, modifiedDate = :modifiedDate WHERE (companyName is null or companyName <> :companyName) and pk.ocn = :ocn and status = 'dcc_add'";
		Query query = em.createQuery(queryString);
		query.setParameter("companyName", companyName);
		query.setParameter("ocn", ocn);
		query.setParameter("modifiedDate", modifiedDate);
		result += query.executeUpdate();
		queryString = "UPDATE LegacyCoPercentage SET companyName = :companyName, modifiedDate = :modifiedDate, status = 'dcc_modify' WHERE (companyName is null or companyName <> :companyName) and pk.ocn = :ocn and (status <> 'dcc_add' or status is null)";
		query = em.createQuery(queryString);
		query.setParameter("companyName", companyName);
		query.setParameter("ocn", ocn);
		query.setParameter("modifiedDate", modifiedDate);
		result += query.executeUpdate();
		return result;
	}
	
	public int updateCompleteStatus(List<Pk> pks, String status) {
		final int chunkSize = 1000;
		int result = 0;
		for (int i = 0; i < pks.size(); i += chunkSize) {
			List<Pk> subList = pks.subList(i, Math.min(i + chunkSize, pks.size()));
			String queryString = "UPDATE LegacyCoPercentage SET status = :status WHERE pk in :pks";
			Query query = em.createQuery(queryString);
			query.setParameter("status", status);
			query.setParameter("pks", subList);
			result += query.executeUpdate();
		}
		return result;
	}

	public void bulkSave(List<LegacyCoPercentage> legacyCoPercentageSavedList) {
		for (LegacyCoPercentage legacyCoPercentage : legacyCoPercentageSavedList) {
			em.persist(legacyCoPercentage);
		}
		em.flush();
		em.clear();
	}
}
