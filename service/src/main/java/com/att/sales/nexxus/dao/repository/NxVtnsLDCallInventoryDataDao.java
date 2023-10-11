package com.att.sales.nexxus.dao.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.assertj.core.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxDwInventory;
import com.att.sales.nexxus.util.JacksonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;


@Repository
public class NxVtnsLDCallInventoryDataDao {
private static Logger logger = LoggerFactory.getLogger(NxVtnsLDCallInventoryDataDao.class);
	private NamedParameterJdbcTemplate namedjdbcTemplate;
	private static String VTNS_USAGE_TABLE = "NX_VTNS_USAGE_INVENTORY";
	private static String VTNS_FEATURE_TABLE = "NX_VTNS_FEATURE_INVENTORY";

	@PersistenceContext
	private EntityManager em;

	@Autowired
	public void setDataSource(@Qualifier("primaryDS") DataSource primaryDS) {
		this.namedjdbcTemplate = new NamedParameterJdbcTemplate(primaryDS);
	}

	public long getTotalCountBySearchCriteria(Object obj, String product) {
		String table = getTableName(product);
		Map<String, Object> dynamicQueryMap = getDynamicQueryparamBasedParamAndProduct(obj, product);
		String query = (String) dynamicQueryMap.get("querybuilderString");
		@SuppressWarnings("unchecked")
		Map<String, Object> queryParam = (Map<String, Object>) dynamicQueryMap.get("queryParamMap");
		String sql = String.format("select count(*) as count from %s where %s", table, query);
		logger.info("query for count is {}", sql);
		Map<String, Object> resultMap = namedjdbcTemplate.queryForMap(sql, queryParam);
		return Long.valueOf(resultMap.get("count").toString());
	}

	public Map<String, Object> getDynamicQueryparamBasedParamAndProduct(Object obj, String product) {
		Set<String> mcnSet = new HashSet<>();
		Set<String> svidSet = new HashSet<>();
		Set<String> mainAcctNumSet = new HashSet<>();
		String billMonth = "";
		String beginBillMonth = "";
		StringBuilder queryBuilder = new StringBuilder();
		Map<String, Object> queryParamMap = new HashMap<>();
		Map<String, Object> dynamicQueryMap = new HashMap<>();

		if (product != null) {
			if (queryBuilder.length() > 0) {
				queryBuilder.append(" AND ");
			}
			queryBuilder.append("product=:product");
			queryParamMap.put("product", product);
		}

		if (obj != null) {
			String searchCriteriaJson = obj.toString();
			JsonNode searchCriteriaNode = JacksonUtil.toJsonNode(searchCriteriaJson);
			if (searchCriteriaNode.has("searchCriteria")
					&& !Strings.isNullOrEmpty(searchCriteriaNode.get("searchCriteria").asText())) {
				String searchcriteria = searchCriteriaNode.get("searchCriteria").asText();
				if ("MCN".equalsIgnoreCase(searchcriteria)) {
					if (searchCriteriaNode.has("mcn")
							&& !Strings.isNullOrEmpty(searchCriteriaNode.get("mcn").asText())) {
						mcnSet = Arrays.stream(searchCriteriaNode.get("mcn").asText().split(","))
								.collect(Collectors.toSet());
						if (queryBuilder.length() > 0) {
							queryBuilder.append(" AND ");
						}
						queryBuilder.append(" mcn IN (:mcn)");
						queryParamMap.put("mcn", mcnSet);
					}
				}

				if ("SVID".equalsIgnoreCase(searchcriteria)) {
					if (searchCriteriaNode.has("svid")
							&& !Strings.isNullOrEmpty(searchCriteriaNode.get("svid").asText())) {
						svidSet = Arrays.stream(searchCriteriaNode.get("svid").asText().split(","))
								.collect(Collectors.toSet());
						if (queryBuilder.length() > 0) {
							queryBuilder.append(" AND ");
						}
						queryBuilder.append(" svid IN (:svid)");
						queryParamMap.put("svid", svidSet);
					}
				}

				if ("MainAccountNumber".equalsIgnoreCase(searchcriteria)) {
					if (searchCriteriaNode.has("mainAccountNumber")
							&& !Strings.isNullOrEmpty(searchCriteriaNode.get("mainAccountNumber").asText())) {
						mainAcctNumSet = Arrays.stream(searchCriteriaNode.get("mainAccountNumber").asText().split(","))
								.collect(Collectors.toSet());
						if (queryBuilder.length() > 0) {
							queryBuilder.append(" AND ");
						}
						queryBuilder.append(" MASTER_ACCOUNTID IN (:mainAcctNum)");
						queryParamMap.put("mainAcctNum", mainAcctNumSet);
					}
				}

			}
			if(MyPriceConstants.VTNS_LD.equals(product)) {
				if (searchCriteriaNode.has("beginBillMonth")
						&& !Strings.isNullOrEmpty(searchCriteriaNode.get("beginBillMonth").asText())) {
					beginBillMonth = searchCriteriaNode.get("beginBillMonth").asText();
					if (queryBuilder.length() > 0) {
						queryBuilder.append(" AND to_number(bill_month)");
	
					}
					queryBuilder.append(" Between");
					queryBuilder.append(" :reqBeginBillDate");
					queryParamMap.put("reqBeginBillDate", beginBillMonth);
	
				}
				if (searchCriteriaNode.has("billMonth")
						&& !Strings.isNullOrEmpty(searchCriteriaNode.get("billMonth").asText())) {
					billMonth = searchCriteriaNode.get("billMonth").asText();
					if (queryBuilder.length() > 0) {
						queryBuilder.append(" AND ");
					}
					queryBuilder.append(" :reqBillDate");
					queryParamMap.put("reqBillDate", billMonth);
				}
			}else {
				if (searchCriteriaNode.has("billMonth")
						&& !Strings.isNullOrEmpty(searchCriteriaNode.get("billMonth").asText())) {
					billMonth = searchCriteriaNode.get("billMonth").asText();
					if (queryBuilder.length() > 0) {
						queryBuilder.append(" AND ");
					}
					queryBuilder.append(" bill_month=:reqBillDate");
					queryParamMap.put("reqBillDate", billMonth);
				}
			}

		}
		dynamicQueryMap.put("querybuilderString", queryBuilder.toString());
		dynamicQueryMap.put("queryParamMap", queryParamMap);
		logger.info("dynamicQueryMap is {}", queryParamMap);
		return dynamicQueryMap;
	}

	public String getAccountNameCountBySearchCriteria(Object obj, String product) {
		String table = getTableName(product);
		Map<String, Object> dynamicQueryMap = getDynamicQueryparamBasedParamAndProduct(obj, product);
		String query = (String) dynamicQueryMap.get("querybuilderString");
		@SuppressWarnings("unchecked")
		Map<String, Object> queryParam = (Map<String, Object>) dynamicQueryMap.get("queryParamMap");
		String sql = String.format(
				"select CUSTOMER_NAME from %s where %s and CUSTOMER_NAME is not null and rownum=1 order by id desc", table,
				query);
		logger.info("query for getAccountNameBySearchCriteria is {}", sql);
		Query q = em.createNativeQuery(sql);
		for (Entry<String, Object> entry : queryParam.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}
		List<String> resultList = q.getResultList();
		if (resultList != null && CollectionUtils.isNotEmpty(resultList)) {
			return resultList.get(0);
		}
		return product;
	}
	
	public Queue<Map<String, Object>> getNxInventoryBySearchCriteriaWithSize(Object searchCriteria, int chunkSize,
			String product) {
		String table = getTableName(product);
		Queue<Map<String, Object>> res = new LinkedList<>();
		Map<String, Object> dynamicQueryMap = getDynamicQueryparamBasedParamAndProduct(searchCriteria, product);
		String queryConditions = (String) dynamicQueryMap.get("querybuilderString");
		@SuppressWarnings("unchecked")
		Map<String, Object> queryParam = (Map<String, Object>) dynamicQueryMap.get("queryParamMap");
		queryParam.put("chunkSize", chunkSize);
		String finalQuery;
		if (MyPriceConstants.VTNS_LD.equals(product)) {
			finalQuery = String.format("select min(ID) as ID,CUSTOMER_NAME,SVID,DUNS_NUMBER,MASTER_ACCOUNTID,ACCOUNTID,SUBACCOUNTID,RATE_PERIOD,RATE_SCHEDULE_CODE,STATE_COUNTRY_DESCR,STATE_COUNTRY_CODE,INTERNATIONAL_TERMINATING_COUNTRY_DESCRIPTION,INTERNATIONAL_TERMINATING_COUNTRY_CODE,MOBILE_RATE_TERMINATION_INDICATOR,SURCHARGE_TOTAL_AMOUNT,SURCHARGE_TOTAL_AMOUNT,DISCOUNT_TYPE,DISCOUNT_BASIS_INDICATOR,DISCOUNT_PERCENTAGE,DISCOUNT_PRICING_PLAN_SCOPE,DISCOUNT_DESCRIPTION,sum(MINUTES_COUNT) AS MINUTES_COUNT,sum(CALL_COUNT) AS CALL_COUNT,TARIFF_12_INDICATOR,sum(COUNT_OF_INITIAL_PERIODS) AS COUNT_OF_INITIAL_PERIODS,sum(COUNT_OF_ADDITIONAL_PERIODS) AS COUNT_OF_ADDITIONAL_PERIODS,ADDITIONAL_PERIOD_DEFINITION,INITIAL_PERIOD_DEFINITION,INITIAL_PERIOD_RATE,ADDITIONAL_PERIOD_RATE,THIRD_PARTY_RATED_DOLLAR_VALUE,MCN,PRODUCT,USAGE_MODEL_FACTOR,JURISDICTION,ORIGINAL_RATE_SCHEDULE_CODE,CONTRACT_NUMBER,CALLING_CARD_TYPE from %s where %s "
					+ "group by CUSTOMER_NAME,SVID,DUNS_NUMBER,MASTER_ACCOUNTID,ACCOUNTID,SUBACCOUNTID,RATE_PERIOD,RATE_SCHEDULE_CODE,STATE_COUNTRY_DESCR,STATE_COUNTRY_CODE,INTERNATIONAL_TERMINATING_COUNTRY_DESCRIPTION,INTERNATIONAL_TERMINATING_COUNTRY_CODE,MOBILE_RATE_TERMINATION_INDICATOR,SURCHARGE_TOTAL_AMOUNT,SURCHARGE_TOTAL_AMOUNT,DISCOUNT_TYPE,DISCOUNT_BASIS_INDICATOR,DISCOUNT_PERCENTAGE,DISCOUNT_PRICING_PLAN_SCOPE,DISCOUNT_DESCRIPTION,TARIFF_12_INDICATOR,ADDITIONAL_PERIOD_DEFINITION,INITIAL_PERIOD_DEFINITION,INITIAL_PERIOD_RATE,ADDITIONAL_PERIOD_RATE,THIRD_PARTY_RATED_DOLLAR_VALUE,MCN,PRODUCT,USAGE_MODEL_FACTOR,JURISDICTION,ORIGINAL_RATE_SCHEDULE_CODE,CONTRACT_NUMBER,CALLING_CARD_TYPE", table, queryConditions);
		} else {
			finalQuery = String.format("select ID,SVID,DUNS_NUMBER,MASTER_ACCOUNTID,ACCOUNTID,SUBACCOUNTID,BILL_MONTH,FEATURE_NAME,RATE_FOR_FEATURE_USAGE,COUNT_OF_FEATURE_USAGE,UNIT_OF_MEASURE,CUSTOMER_NAME,MCN,PRODUCT,BILL_GROUP_CODE,SALES_OFFICE,BILLING_TELEPHONE_NUM,VTNS_SITE_ID,VTNS_WHOLESALE_BC_INDICATOR,DISCOUNT_TYPE,DISCOUNT_DESCRIPTION,DISCOUNT_PERCENTAGE from %s where %s ", table, queryConditions);
		}
		List<Map<String, Object>> queryRes = namedjdbcTemplate.queryForList(finalQuery, queryParam);
		res.addAll(queryRes);
		return res;
	}
				
		
	public String getTableName(String product) {
		if (MyPriceConstants.VTNS_LD.equals(product)) {
			return VTNS_USAGE_TABLE;
		}
		return VTNS_FEATURE_TABLE;
	}


}
