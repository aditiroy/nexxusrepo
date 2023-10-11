package com.att.sales.nexxus.dao.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.dao.model.NxUdfMapping;

@Component
public class NxUdfMappingDao {

	private static Logger log = LoggerFactory.getLogger(NxUdfMappingDao.class);
	@PersistenceContext
	private EntityManager em;

	public Map<Long, NxUdfMapping> getNxUdfMappingMap(String ruleSet, long offerId, long componentId) {
		String queryString = "FROM NxUdfMapping WHERE ruleSet = :ruleSet AND offerId = :offerId AND componentId = :componentId";
		TypedQuery<NxUdfMapping> query = em.createQuery(queryString, NxUdfMapping.class);
		query.setParameter("ruleSet", ruleSet);
		query.setParameter("offerId", offerId);
		query.setParameter("componentId", componentId);
		List<NxUdfMapping> resultList = query.getResultList();
		Map<Long, NxUdfMapping> result = resultList.stream()
				.collect(Collectors.toMap(NxUdfMapping::getUdfId, Function.identity()));
		log.info("ruleSet:{} offerId:{} componentId:{} has udfIdSet:{}", ruleSet, offerId, componentId,
				result.keySet());
		return result;
	}
	
	
	public List<NxUdfMapping>  getNxUdfDataByOfferIdAndRule(String ruleSet, long offerId){
		Map<Long,Map<String,NxUdfMapping>> result=new HashMap<>();
		String queryString = "FROM NxUdfMapping WHERE ruleSet = :ruleSet AND offerId = :offerId ";
		TypedQuery<NxUdfMapping> query = em.createQuery(queryString, NxUdfMapping.class);
		query.setParameter("ruleSet", ruleSet);
		query.setParameter("offerId", offerId);
		List<NxUdfMapping> resultList = query.getResultList();
		Optional.ofNullable(resultList).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull).
		forEach( data -> {
			if(!result.containsKey(data.getComponentId())) {
				result.put(data.getComponentId(), new HashMap<String,NxUdfMapping>());
			}
			result.get(data.getComponentId()).put(data.getUdfAbbr(), data);
		});
		return resultList;
	}
	
}
