package com.att.sales.nexxus.dao.repository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.assertj.core.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.util.JacksonUtil;
import com.fasterxml.jackson.databind.JsonNode;

@Repository
public class NxOneNetInventoryDataDao {
	private static Logger logger = LoggerFactory.getLogger(NxOneNetInventoryDataDao.class);
	private static final String ONENET_FEATURE_TABLE = "NX_ONENET_FEATURE_INVENTORY";
	private static final String ONENET_USAGE_TABLE = "NX_ONENET_USAGE_INVENTORY";
	private NamedParameterJdbcTemplate namedjdbcTemplate;

	@PersistenceContext
	private EntityManager em;

	@Autowired
	public void setDataSource(@Qualifier("primaryDS") DataSource primaryDS) {
		this.namedjdbcTemplate = new NamedParameterJdbcTemplate(primaryDS);
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
						queryBuilder.append(" MCN IN (:mcn)");
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
						queryBuilder.append(" SVID IN (:svid)");
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
						queryBuilder.append(" ONENET_ACCOUNT IN (:mainAcctNum)");
						queryParamMap.put("mainAcctNum", mainAcctNumSet);
					}
				}

			}
			if (MyPriceConstants.ONENET_USAGE_PRODUCT_CD.equals(product)) {
				if (searchCriteriaNode.has("beginBillMonth")
						&& !Strings.isNullOrEmpty(searchCriteriaNode.get("beginBillMonth").asText())) {
					beginBillMonth = searchCriteriaNode.get("beginBillMonth").asText();
					if (queryBuilder.length() > 0) {
						queryBuilder.append(" AND to_number(BILL_MONTH)");

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
			} else {
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

	public String getTableName(String product) {
		if (MyPriceConstants.ONENET_USAGE_PRODUCT_CD.equals(product)) {
			return ONENET_USAGE_TABLE;
		}
		return ONENET_FEATURE_TABLE;
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

	public String getAccountNameCountBySearchCriteria(Object obj, String product) {
		String table = getTableName(product);
		Map<String, Object> dynamicQueryMap = getDynamicQueryparamBasedParamAndProduct(obj, product

		);
		String query = (String) dynamicQueryMap.get("querybuilderString");
		@SuppressWarnings("unchecked")
		Map<String, Object> queryParam = (Map<String, Object>) dynamicQueryMap.get("queryParamMap");
		String sql = String.format(
				"select CUSTOMER_NAME from %s where %s and CUSTOMER_NAME is not null and rownum=1 order by id desc",
				table, query);
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
		if (MyPriceConstants.ONENET_USAGE_PRODUCT_CD.equals(product)) {
			finalQuery = String.format("select min(ID) as ID, MCN, SVID, DUNS_NUMBER, MASTER_ACCOUNTID, ACCOUNTID, SUBACCOUNTID, ONENET_ACCOUNT, CUSTOMER_NAME, avg(APPLICABLE_NET_DISCOUNT) as APPLICABLE_NET_DISCOUNT, avg(APPLICABLE_SUPPLEMENTAL_NET_DISCOUNT) as APPLICABLE_SUPPLEMENTAL_NET_DISCOUNT, RATED_STATE_COUNTRY_DESCRIPTION, RATED_STATE_COUNTRY_CODE, INTL_TERMINATING_COUNTRY_DESCRIPTION, INTL_TERMINATING_COUNTRY_CODE, JURISDICTION, LATA_PAIR_PRICING_INDICATOR, OUTBOUND_RATE_SCHEDULE_INDICATOR, RATE_PERIOD_INDICATOR, SERVICE_TYPE_DESCRIPTION, ACCESS_TYPE_DESCRIPTION, CALL_TYPE_DESCRIPTION, MILEAGE_BAND, MOBILE_TERMINATING_INDICATOR, sum(MINUTES_COUNT) * MONTHLY_FACTOR as MINUTES_COUNT, sum(CALLS_COUNT) * MONTHLY_FACTOR as CALLS_COUNT, sum(COUNT_OF_INITIAL_PERIODS) * MONTHLY_FACTOR as COUNT_OF_INITIAL_PERIODS, sum(COUNT_OF_ADDITIONAL_PERIODS) * MONTHLY_FACTOR as COUNT_OF_ADDITIONAL_PERIODS, INITIAL_PERIOD_DEFINITION, ADDITIONAL_PERIOD_DEFINITION, INITIAL_PERIOD_RATE, ADDITIONAL_PERIOD_RATE, sum(THIRD_PARTY_RATED_DOLLAR_VALUE) * MONTHLY_FACTOR as THIRD_PARTY_RATED_DOLLAR_VALUE, sum(SURCHARGE_AMOUNT) * MONTHLY_FACTOR as SURCHARGE_AMOUNT, CONNECTED_INDICATOR, MONTHLY_FACTOR from (select inventory.*, 12 / (select (TO_NUMBER(SUBSTR(:reqBillDate,0,4))*12 + To_Number(Substr(:reqBillDate,5,2))) - (TO_NUMBER(SUBSTR(:reqBeginBillDate,0,4))*12 + TO_NUMBER(SUBSTR(:reqBeginBillDate,5,2))) + 1 from dual) as MONTHLY_FACTOR from NX_ONENET_USAGE_INVENTORY INVENTORY where %s ) group by MCN, SVID, DUNS_NUMBER, MASTER_ACCOUNTID, ACCOUNTID, SUBACCOUNTID, ONENET_ACCOUNT, CUSTOMER_NAME, RATED_STATE_COUNTRY_DESCRIPTION, RATED_STATE_COUNTRY_CODE, INTL_TERMINATING_COUNTRY_DESCRIPTION, INTL_TERMINATING_COUNTRY_CODE, JURISDICTION, LATA_PAIR_PRICING_INDICATOR, OUTBOUND_RATE_SCHEDULE_INDICATOR, RATE_PERIOD_INDICATOR, SERVICE_TYPE_DESCRIPTION, ACCESS_TYPE_DESCRIPTION, CALL_TYPE_DESCRIPTION, MILEAGE_BAND, MOBILE_TERMINATING_INDICATOR, INITIAL_PERIOD_DEFINITION, ADDITIONAL_PERIOD_DEFINITION, INITIAL_PERIOD_RATE, ADDITIONAL_PERIOD_RATE, CONNECTED_INDICATOR, MONTHLY_FACTOR", queryConditions);
		} else {
			finalQuery = String.format("select min(ID) as ID, MCN, SVID, DUNS_NUMBER, MASTER_ACCOUNTID, ACCOUNTID, SUBACCOUNTID, ONENET_ACCOUNT, CUSTOMER_NAME, FEATURE_USOC, USOC_SUB_FEATURE_ID, USOC_DESCRIPTION, PER_INSTANCE_RATE, UNIT_OF_MEASURE, sum(FEATURE_COUNT) * 12 as FEATURE_COUNT, 12 as MONTHLY_FACTOR from NX_ONENET_FEATURE_INVENTORY where %s group by MCN, SVID, DUNS_NUMBER, MASTER_ACCOUNTID, ACCOUNTID, SUBACCOUNTID, ONENET_ACCOUNT, CUSTOMER_NAME, FEATURE_USOC, USOC_SUB_FEATURE_ID, USOC_DESCRIPTION, PER_INSTANCE_RATE, UNIT_OF_MEASURE", queryConditions);
		}
		List<Map<String, Object>> queryRes = namedjdbcTemplate.queryForList(finalQuery, queryParam);
		res.addAll(queryRes);
		return res;
	}
}
