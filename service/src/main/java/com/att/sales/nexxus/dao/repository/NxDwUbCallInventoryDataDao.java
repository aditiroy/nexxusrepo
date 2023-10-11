package com.att.sales.nexxus.dao.repository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Queue;
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
public class NxDwUbCallInventoryDataDao {

	private static Logger logger = LoggerFactory.getLogger(NxDwInventoryDao.class);
	private NamedParameterJdbcTemplate namedjdbcTemplate;
	private static String table = "nx_dw_ub_call_inventory_data";

	@PersistenceContext
	private EntityManager em;

	@Autowired
	public void setDataSource(@Qualifier("primaryDS") DataSource primaryDS) {
		this.namedjdbcTemplate = new NamedParameterJdbcTemplate(primaryDS);
	}

	public long getTotalCountBySearchCriteria(Object obj, String product) {
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
						queryBuilder.append(" mcn_base IN (:mcn)");
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
						queryBuilder.append(" parent_acct_id IN (:mainAcctNum)");
						queryParamMap.put("mainAcctNum", mainAcctNumSet);
					}
				}

			}
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

		}
		dynamicQueryMap.put("querybuilderString", queryBuilder.toString());
		dynamicQueryMap.put("queryParamMap", queryParamMap);
		logger.info("dynamicQueryMap is {}", queryParamMap);
		return dynamicQueryMap;
	}

	public String getAccountNameCountBySearchCriteria(Object obj, String product) {
		Map<String, Object> dynamicQueryMap = getDynamicQueryparamBasedParamAndProduct(obj, product

		);
		String query = (String) dynamicQueryMap.get("querybuilderString");
		@SuppressWarnings("unchecked")
		Map<String, Object> queryParam = (Map<String, Object>) dynamicQueryMap.get("queryParamMap");
		String sql = String.format(
				"select acct_name from %s where %s and acct_name is not null and rownum=1 order by id desc", table,
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
		Queue<Map<String, Object>> res = new LinkedList<>();
		Map<String, Object> dynamicQueryMap = getDynamicQueryparamBasedParamAndProduct(searchCriteria, product);
		String queryConditions = (String) dynamicQueryMap.get("querybuilderString");
		@SuppressWarnings("unchecked")
		Map<String, Object> queryParam = (Map<String, Object>) dynamicQueryMap.get("queryParamMap");
		queryParam.put("chunkSize", chunkSize);
		String probeMaxMinIdNumberSqlFirst = String.format(
				"select min(id) as MINVAL, max(id) as MAXVAL from (select id from %s where %s order by id) where rownum <= :chunkSize",
				table, queryConditions);
		String probeMaxMinIdSqlSubsequent = String.format(
				"select min(id) as MINVAL, max(id) as MAXVAL from (select id  from %s where %s and id> :MAXVAL order by id) where rownum <= :chunkSize",
				table, queryConditions);
			String baseQuery = String.format("select ID,PRODUCT, ACCT_NAME, ACCT_ID,PARENT_ACCT_ID,DUNS_NUM,MCN_BASE,MIL_BND_IND,BL_TRANS_DESC,CALL_CARD_IND,L3,L4_ACCT_ID,L5_ACCT_ID,OFFER_ID,DIR_IND,CONTRACT_EFF_DT,CONTRACT_END_DT,TERM_AGREEMENT,HEIR_ID,SLC_OFF_CODE,GRC,ORIG_ST_CTRY_NAME,TERM_ST_CTRY_NAME,RATE_PERIOD,JURIS_DW_VAL,ABN_MOB_IND,PORT_NUM,IVC_PDT_TYPE_DESC,IVC_PDT_FLY_DESC,BILL_ELE_CODE,UNIT_RATE_AMT,TOTAL_DISCOUNT_AMT,TOTAL_PRE_DISCOUNT_AMT,ORIG_ST_CTRY_NAME,TERM_ST_CTRY_NAME, ABN_MOB_IND,PBI,RATE_PERIOD,ATT_CON_IND,UOM_CD,INT_PRD_RATE,INT_PRD_CNT,ADD_PRD_DEF,ADD_PRD_CNT,ADD_PRD_RATE,SL_ADDR_LINE2,SL_COUNTRY,SL_ADDR_LINE1,SL_CITY,SL_STATE,SL_ZIP,INT_PRD_DEF,JURIS_DW_VAL,JUR_CODE,PBI_DSC,TOTAL_MONTHLY_SEC,BL_ADDR_LINE1,BL_ADDR_LINE2,BL_COUNTRY,BL_STATE,BL_CITY,BL_ZIP,CUR_CODE,TOTAL_USG_MSG_CNT from %s where %s and ID >= :MINVAL and ID <= :MAXVAL", table, queryConditions);
			String finalQuery = String.format("(%s)", baseQuery);
			do {
				if (!queryParam.containsKey("MINVAL")) {
					List<Map<String, Object>> probeRes = namedjdbcTemplate.queryForList(probeMaxMinIdNumberSqlFirst, queryParam);
					queryParam.putAll(probeRes.get(0));
				} else {
					List<Map<String, Object>> probeRes = namedjdbcTemplate.queryForList(probeMaxMinIdSqlSubsequent, queryParam);
					queryParam.putAll(probeRes.get(0));
				}
				List<Map<String, Object>> queryRes = namedjdbcTemplate.queryForList(finalQuery, queryParam);
				res.addAll(queryRes);
			} while (queryParam.get("MINVAL") != null);
			return res;
		}
}