package com.att.sales.nexxus.dao.repository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.dao.model.InrXmlToJsonRule;
import com.att.sales.nexxus.inr.InrIntermediateJsonGenerator;

/**
 * The Class InrXmlToJsonRuleDao.
 *
 * @author xy3208
 */
@Component
public class InrXmlToJsonRuleDao {

	/** The log. */
	private static Logger log = LoggerFactory.getLogger(InrXmlToJsonRuleDao.class);

	/** The em. */
	@PersistenceContext
	private EntityManager em;

	/**
	 * involved table: INR_XML_TO_JSON_RULES.
	 *
	 * @param rootTag the root tag
	 * @return InrXmlToJsonRuleMap, key: xmlStartTag, value: InrXmlToJsonRule
	 */
	public Map<String, InrXmlToJsonRule> getInrXmlToJsonRuleMap(String rootTag) {
		String queryString = "FROM InrXmlToJsonRule WHERE rootTag = :rootTag";
		TypedQuery<InrXmlToJsonRule> query = em.createQuery(queryString, InrXmlToJsonRule.class);
		query.setParameter("rootTag", rootTag);
		// empty query result will return an empty List
		List<InrXmlToJsonRule> resultList = query.getResultList();
		Map<String, InrXmlToJsonRule> result = resultList.stream()
				.collect(Collectors.toMap(InrXmlToJsonRule::getXmlStartTag, Function.identity()));
		log.info("InrXmlToJsonRuleMap has key {}", result.keySet());
		return result;
	}

	public InrXmlToJsonRuleDaoResult getInrXmlToJsonRuleDaoResult(String rootTag) {
		String queryString = "FROM InrXmlToJsonRule WHERE rootTag = :rootTag";
		TypedQuery<InrXmlToJsonRule> query = em.createQuery(queryString, InrXmlToJsonRule.class);
		query.setParameter("rootTag", rootTag);
		// empty query result will return an empty List
		List<InrXmlToJsonRule> resultList = query.getResultList();
		Map<String, InrXmlToJsonRule> inrXmlToJsonRuleMap = resultList.stream()
				.collect(Collectors.toMap(InrXmlToJsonRule::getXmlStartTag, Function.identity()));
		log.info("InrXmlToJsonRuleMap has key {}", inrXmlToJsonRuleMap.keySet());
		List<String> fieldNullTags = resultList.stream().filter(row -> row.getFieldNullYn() != null)
				.map(row -> row.getXmlStartTag()).collect(Collectors.toList());
		List<String> falloutMatchingTags = resultList.stream()
				.filter(row -> row.getOperations() != null
						&& row.getOperations().contains(InrIntermediateJsonGenerator.FALLOUTMATCHINGID))
				.map(row -> row.getXmlStartTag()).collect(Collectors.toList());
		List<String> nxSiteMatchingTags = resultList.stream()
				.filter(row -> row.getOperations() != null
						&& row.getOperations().contains(InrIntermediateJsonGenerator.NXSITEMATCHINGID))
				.map(row -> row.getXmlStartTag()).collect(Collectors.toList());
		return new InrXmlToJsonRuleDaoResult(inrXmlToJsonRuleMap, fieldNullTags, falloutMatchingTags, nxSiteMatchingTags);
	}

	public static class InrXmlToJsonRuleDaoResult {
		private Map<String, InrXmlToJsonRule> inrXmlToJsonRuleMap;
		private List<String> fieldNullTags;
		private List<String> falloutMatchingTags;
		private List<String> nxSiteMatchingTags;

		public InrXmlToJsonRuleDaoResult(Map<String, InrXmlToJsonRule> inrXmlToJsonRuleMap,
				List<String> fieldNullTags, List<String> falloutMatchingTags, List<String> nxSiteMatchingTags) {
			super();
			this.inrXmlToJsonRuleMap = inrXmlToJsonRuleMap;
			this.fieldNullTags = fieldNullTags;
			this.falloutMatchingTags = falloutMatchingTags;
			this.nxSiteMatchingTags = nxSiteMatchingTags;
		}

		public Map<String, InrXmlToJsonRule> getInrXmlToJsonRuleMap() {
			return inrXmlToJsonRuleMap;
		}

		public List<String> getFieldNullTags() {
			return fieldNullTags;
		}

		public List<String> getFalloutMatchingTags() {
			return falloutMatchingTags;
		}
		
		public List<String> getNxSiteMatchingTags() {
			return nxSiteMatchingTags;
		}
	}
}
