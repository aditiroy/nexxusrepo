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
public class NxDwInventoryDao {

	private static Logger logger = LoggerFactory.getLogger(NxDwInventoryDao.class);
    private NamedParameterJdbcTemplate namedjdbcTemplate;
	private int chunkSize = 1000;

	private static String table="nx_dw_inventory";
	
	@PersistenceContext
	private EntityManager em;

	@Autowired 
    public void setDataSource(@Qualifier("primaryDS") DataSource primaryDS) {
        this.namedjdbcTemplate= new NamedParameterJdbcTemplate(primaryDS);
    }
			public Map<String,Object> getNxInventoryBySearchCriteriaChunkandId(Object obj,int chunkSize,long lastId,String product) {
			Map<String,Object> result= new HashMap<>();
			Map<String,Object> dynamicQueryMap=getDynamicQueryparamBasedParamAndProduct(obj,product);
			String query=(String)dynamicQueryMap.get("querybuilderString");
			@SuppressWarnings("unchecked")
			Map<String,Object> queryParam=(Map<String,Object> )dynamicQueryMap.get("queryParamMap");

			String sql = String.format("select * from %s where %s and rownum <=:chunkSize order by id asc",table,query);
			String subSql = String.format("select * from %s where %s and id>:id and rownum <=:chunkSize order by id asc",table,query);
	
			List<Map<String, Object>> queryForList =null;
			if(lastId==0) {
				logger.info("query for first chunk is {}",sql);
				queryParam.put("chunkSize", chunkSize);
				queryForList=namedjdbcTemplate.queryForList(sql,queryParam);
			}else{
				logger.info("query for other chunk is  with last id {}",subSql,lastId);
				queryParam.put("chunkSize", chunkSize);
				queryParam.put("id", lastId);
				queryForList=namedjdbcTemplate.queryForList(subSql,queryParam);
			}
			Map<String, Object> lastRow=queryForList.get(queryForList.size()-1);
			long lastIdval=Long.parseLong(String.valueOf(lastRow.get("ID")));

			result.put("data", queryForList);
			result.put("lastId",lastIdval);
			return result;
	}
		
		public long getTotalCountBySearchCriteria(Object obj,String product) {
			Map<String,Object> dynamicQueryMap=getDynamicQueryparamBasedParamAndProduct(obj,product);
			String query=(String)dynamicQueryMap.get("querybuilderString");
			@SuppressWarnings("unchecked")
			Map<String,Object> queryParam=(Map<String,Object> )dynamicQueryMap.get("queryParamMap");
			String sql = String.format("select count(*) as count from %s where %s",table,query);
			logger.info("query for count is {}",sql);
			Map<String,Object> resultMap = namedjdbcTemplate.queryForMap(sql, queryParam);
			return Long.valueOf(resultMap.get("count").toString());
		}
		
		public Map<String,Object> getDynamicQueryparamBasedParamAndProduct(Object obj,String product) {
			Set<String> mcnSet = new HashSet<>();
			Set<String> circuitIdSet = new HashSet<>();
			Set<String> svidSet = new HashSet<>();
			Set<String> mainAcctNumSet = new HashSet<>();
			String billMonth="";
			String beginBillMonth="";
			StringBuilder queryBuilder=new  StringBuilder();
			Map<String,Object> queryParamMap=new HashMap<>();
			Map<String,Object> dynamicQueryMap=new HashMap<>();

			if(product!=null) {
				if ("MIS/PNT".equals(product)) {
					product = "ADI";
				} else if ("GMIS".equals(product)) {
					product = "ADIG";
				} 
				/*else if("EPLSWAN".equals(product)) {
					product = "PLS";
				}*/
				if(queryBuilder.length()>0) {
					queryBuilder.append(" AND ");
				}
				queryBuilder.append("product=:product");
				queryParamMap.put("product", product);
			}
			
			if(obj != null) {
				String searchCriteriaJson=obj.toString();
				JsonNode searchCriteriaNode = JacksonUtil.toJsonNode(searchCriteriaJson);
				if ( searchCriteriaNode.has("searchCriteria")
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
				if ("CIRCUITID".equalsIgnoreCase(searchcriteria)) {
					if (searchCriteriaNode.has("circuitid")
							&& !Strings.isNullOrEmpty(searchCriteriaNode.get("circuitid").asText())) {
						circuitIdSet = Arrays.stream(searchCriteriaNode.get("circuitid").asText().split(","))
								.collect(Collectors.toSet());
						if (queryBuilder.length() > 0) {
							queryBuilder.append(" AND ");
						}
						queryBuilder.append(" clean_circuit_number IN (:circuitid)");
						queryParamMap.put("circuitid", circuitIdSet);
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
				if (searchCriteriaNode.has("billMonth")
						&& !Strings.isNullOrEmpty(searchCriteriaNode.get("billMonth").asText())) {
					billMonth=searchCriteriaNode.get("billMonth").asText();
					if(queryBuilder.length()>0) {
						queryBuilder.append(" AND ");
					}
					queryBuilder.append(" bill_month=:reqBillDate");
					queryParamMap.put("reqBillDate", billMonth);
				}
				
			/*	if (searchCriteriaNode.has("beginBillMonth")
						&& !Strings.isNullOrEmpty(searchCriteriaNode.get("beginBillMonth").asText())) {
					beginBillMonth=searchCriteriaNode.get("beginBillMonth").asText();
					if(queryBuilder.length()>0) {
						queryBuilder.append(" AND ");
					}
					queryBuilder.append(" bill_month=:reqBillDate");
					queryParamMap.put("reqBillDate", beginBillMonth);

				}*/
		}
			dynamicQueryMap.put("querybuilderString", queryBuilder.toString());
			dynamicQueryMap.put("queryParamMap", queryParamMap);
			logger.info("dynamicQueryMap is {}",queryParamMap);
			return dynamicQueryMap;
	}
	
	public Queue<Map<String, Object>> getNxInventoryBySearchCriteriaWithSize(Object searchCriteria, int chunkSize,
			String product) {
		Queue<Map<String, Object>> res = new LinkedList<>();
		Map<String, Object> dynamicQueryMap = getDynamicQueryparamBasedParamAndProduct(searchCriteria, product);
		String queryConditions = (String) dynamicQueryMap.get("querybuilderString");
		@SuppressWarnings("unchecked")
		Map<String, Object> queryParam = (Map<String, Object>) dynamicQueryMap.get("queryParamMap");
		queryParam.put("chunkSize", chunkSize);
		if (MyPriceConstants.BVoIP_NON_USAGE.equals(product)) {
			/*String probeMaxMinIdNumberSqlFirst = String.format(
					"select min(id) as MINVAL, max(id) as MAXVAL from (select id from %s where %s order by id) where rownum <= :chunkSize",
					table, queryConditions);
			String probeMaxMinIdSqlSubsequent = String.format(
					"select min(id) as MINVAL, max(id) as MAXVAL from (select id  from %s where %s and id> :MAXVAL order by id) where rownum <= :chunkSize",
					table, queryConditions);
			String groupBy="group by PRODUCT, MCN_BASE,  PORT_NUMBER, ACCOUNT_NAME, BILLING_ELEMENT_ID, CURRENCY_CODE,COUNTRY_CODE,  ACCT_ID, PARENT_ACCT_ID,PBI,L3,COMPONENT_DESCRIPTION, DUNS_NUM,L5_ACC_ID,L4_ACC_ID,OFFER_ID,CONTRACT_EFF_DT,CONTRACT_END_DT,TERM_AGREEMENT,BL_ZIP,BL_STATE,BL_COUNTRY,BL_CITY,BL_ADDR_LINE_1,BL_ADDR_LINE_2,SL_ADDR_LINE_2,SL_ADDR_LINE_1,SL_CITY,SL_STATE,SL_ZIP,BILLING_ELEMENT_CODE,PROD_BILLING_ID_DESC,BL_TRANS_DESC,UOM_CD,UNIT_RATE_AMOUNT,WORK_WITH_CKT_NB";
			String subQuerySelect ="select PRODUCT, MCN_BASE,  PORT_NUMBER, ACCOUNT_NAME, BILLING_ELEMENT_ID, CURRENCY_CODE,  sum(POST_DISCOUNT_AMT) as POST_DISCOUNT_AMT, sum(PRE_DISCOUNT_AMT) as PRE_DISCOUNT_AMT, round(sum(BIT_CT),0) as BIT_CT, COUNTRY_CODE,  ACCT_ID, PARENT_ACCT_ID,PBI,L3,COMPONENT_DESCRIPTION, DUNS_NUM,L5_ACC_ID,L4_ACC_ID,OFFER_ID,CONTRACT_EFF_DT,CONTRACT_END_DT,TERM_AGREEMENT,BL_ZIP,BL_STATE,BL_COUNTRY,BL_CITY,BL_ADDR_LINE_1,BL_ADDR_LINE_2,SL_ADDR_LINE_2,SL_ADDR_LINE_1,SL_CITY,SL_STATE,SL_ZIP,BILLING_ELEMENT_CODE,PROD_BILLING_ID_DESC,BL_TRANS_DESC,UOM_CD,UNIT_RATE_AMOUNT, "
					+ "sum(DISCOUNT_AMOUNT) as DISCOUNT_AMOUNT , WORK_WITH_CKT_NB";
			String baseQuery = String.format("select ID,PRODUCT, MCN_BASE,  PORT_NUMBER, ACCOUNT_NAME, BILLING_ELEMENT_ID, CURRENCY_CODE,  case when POST_DISCOUNT_AMT is null then 0 else to_number(POST_DISCOUNT_AMT) end as POST_DISCOUNT_AMT, case when PRE_DISCOUNT_AMT is null then 0 else to_number(PRE_DISCOUNT_AMT) end as PRE_DISCOUNT_AMT,case when BIT_CT is null then 0 else to_number(BIT_CT) end as BIT_CT, COUNTRY_CODE,  ACCT_ID, PARENT_ACCT_ID,FINAL_PROD_BLNG_ID as PBI,L3,COMPONENT_DESCRIPTION, DUNS_NUM,L5_ACC_ID,L4_ACC_ID,OFFER_ID,CONTRACT_EFF_DT,CONTRACT_END_DT,TERM_AGREEMENT,BL_ZIP,BL_STATE,BL_COUNTRY,BL_CITY,BL_ADDR_LINE_1,BL_ADDR_LINE_2,SL_ADDR_LINE_2,SL_ADDR_LINE_1,SL_CITY,SL_STATE,SL_ZIP,BILLING_ELEMENT_CODE,PROD_BILLING_ID_DESC,BL_TRANS_DESC,UOM_CD,case when UNIT_RATE_AMOUNT is null then 0 else to_number(UNIT_RATE_AMOUNT) end as UNIT_RATE_AMOUNT, case when DISCOUNT_AMOUNT is null then 0 else to_number(DISCOUNT_AMOUNT)"
							+ "    end as DISCOUNT_AMOUNT,WORK_WITH_CKT_NB,REVENUE_TYPE_CD  from %s where %s and BILLING_ELEMENT_CODE='MNTHLYSVC' and REVENUE_TYPE_CD='R' and ID >= :MINVAL and ID <= :MAXVAL",
					table, queryConditions);
			String finalQuery = String.format("%s from (%s) %s", subQuerySelect, baseQuery,groupBy);
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
			} while (queryParam.get("MINVAL") != null);*/
			
			String commonGroupBy=" group by PRODUCT, MCN_BASE,SVID,PORT_NUMBER, ACCOUNT_NAME, BILLING_ELEMENT_ID, CURRENCY_CODE,POST_DISCOUNT_AMT, PRE_DISCOUNT_AMT,  BIT_CT,PBI,COUNTRY_CODE,ACCT_ID,PARENT_ACCT_ID,L3,COMPONENT_DESCRIPTION, DUNS_NUM,L5_ACC_ID,L4_ACC_ID,OFFER_ID,CONTRACT_EFF_DT,CONTRACT_END_DT,TERM_AGREEMENT,BL_ZIP,BL_STATE,BL_COUNTRY,BL_CITY,BL_ADDR_LINE_1,BL_ADDR_LINE_2,SL_ADDR_LINE_2,SL_ADDR_LINE_1,SL_CITY,SL_STATE,SL_ZIP,BILLING_ELEMENT_CODE,PROD_BILLING_ID_DESC,BL_TRANS_DESC,UOM_CD,UNIT_RATE_AMOUNT,DISCOUNT_AMOUNT,WORK_WITH_CKT_NB , REVENUE_TYPE_CD";
			String outerQuerySelect="select PRODUCT, MCN_BASE,SVID,PORT_NUMBER, ACCOUNT_NAME, BILLING_ELEMENT_ID, CURRENCY_CODE,POST_DISCOUNT_AMT, PRE_DISCOUNT_AMT, round(BIT_CT,0) as BIT_CT,DISCOUNT_AMOUNT,PBI,COUNTRY_CODE,ACCT_ID,PARENT_ACCT_ID,L3,COMPONENT_DESCRIPTION, DUNS_NUM,L5_ACC_ID,L4_ACC_ID,OFFER_ID,CONTRACT_EFF_DT,CONTRACT_END_DT,TERM_AGREEMENT,BL_ZIP,BL_STATE,BL_COUNTRY,BL_CITY,BL_ADDR_LINE_1,BL_ADDR_LINE_2,SL_ADDR_LINE_2,SL_ADDR_LINE_1,SL_CITY,SL_STATE,SL_ZIP,BILLING_ELEMENT_CODE,PROD_BILLING_ID_DESC,BL_TRANS_DESC,UOM_CD,UNIT_RATE_AMOUNT,WORK_WITH_CKT_NB,REVENUE_TYPE_CD,BILLING_ELEMENT_CODE";
			String innerQuery1=String.format("select ID,PRODUCT, MCN_BASE,SVID, PORT_NUMBER, ACCOUNT_NAME, BILLING_ELEMENT_ID, CURRENCY_CODE,COUNTRY_CODE,ACCT_ID, PARENT_ACCT_ID,FINAL_PROD_BLNG_ID,L3,COMPONENT_DESCRIPTION, DUNS_NUM,L5_ACC_ID,L4_ACC_ID,OFFER_ID,CONTRACT_EFF_DT,CONTRACT_END_DT,TERM_AGREEMENT,BL_ZIP,BL_STATE,BL_COUNTRY,BL_CITY,BL_ADDR_LINE_1,BL_ADDR_LINE_2,SL_ADDR_LINE_2,SL_ADDR_LINE_1,SL_CITY,SL_STATE,SL_ZIP,BILLING_ELEMENT_CODE,PROD_BILLING_ID_DESC,BL_TRANS_DESC,UOM_CD,case when UNIT_RATE_AMOUNT is null then 0 else to_number(UNIT_RATE_AMOUNT) end as UNIT_RATE_AMOUNT, WORK_WITH_CKT_NB,REVENUE_TYPE_CD from %s where %s and REVENUE_TYPE_CD='R' and BILLING_ELEMENT_CODE='MTHLYSVC'",table, queryConditions);
			String aggregationGroupBy="group by ACCT_ID,PARENT_ACCT_ID,FINAL_PROD_BLNG_ID";
			String innerQuery2=String.format("select  ACCT_ID,PARENT_ACCT_ID,FINAL_PROD_BLNG_ID, sum(POST_DISCOUNT_AMT) as POST_DISCOUNT_AMT, sum(PRE_DISCOUNT_AMT) as PRE_DISCOUNT_AMT, sum(BIT_CT) as BIT_CT, sum(DISCOUNT_AMOUNT) as DISCOUNT_AMOUNT from"
					+ "(select ACCT_ID,PARENT_ACCT_ID,FINAL_PROD_BLNG_ID,case when POST_DISCOUNT_AMT is null then 0 else to_number(POST_DISCOUNT_AMT) end as POST_DISCOUNT_AMT, case when PRE_DISCOUNT_AMT is null then 0 else to_number(PRE_DISCOUNT_AMT) end as PRE_DISCOUNT_AMT,case when BIT_CT is null then 0 else to_number(BIT_CT) end as BIT_CT, case when DISCOUNT_AMOUNT is null then 0 else to_number(DISCOUNT_AMOUNT) end as DISCOUNT_AMOUNT\r\n" + 
					"	 from %s where %s and REVENUE_TYPE_CD='R' and BILLING_ELEMENT_CODE='MTHLYSVC') %s",table, queryConditions,aggregationGroupBy);
			String innerJoinQuery=String.format("select a.*,a.FINAL_PROD_BLNG_ID  as PBI, b.POST_DISCOUNT_AMT,b.PRE_DISCOUNT_AMT,b.DISCOUNT_AMOUNT,b.BIT_CT from(%s) a inner join(%s) b on a.PARENT_ACCT_ID=b.PARENT_ACCT_ID and a.ACCT_ID=b.ACCT_ID and a.FINAL_PROD_BLNG_ID=b.FINAL_PROD_BLNG_ID ",innerQuery1,innerQuery2);
			String finalQuery=String .format("%s from (%s) %s", outerQuerySelect,innerJoinQuery,commonGroupBy);
			List<Map<String, Object>> queryRes = namedjdbcTemplate.queryForList(finalQuery, queryParam);
			res.addAll(queryRes);
			return res;
		} else {
			String probeMaxMinCircuitNumberSqlFirst="";
			String probeMaxMinCircuitNumberSqlSubsequent="";
			String subQueryDCS="";
			String probeMaxMinPortNumberSqlFirst="";
			String probeMaxMinPortNumberSqlSubsequent="";
			String subQueryUb="";
			if(MyPriceConstants.EPLSWAN.equals(product)) {
				final String commonGroupBy = "group by PRODUCT, MCN_BASE,SVID, GRC, SALES_OFFICE_CODE, CIRCUIT_NUMBER, PORT_NUMBER, ACCOUNT_NAME,CURRENCY_CODE, TYPE_OF_CHARGE,  COUNTRY_CODE, ROUTER_NUMBER, ACCT_ID, PARENT_ACCT_ID, BILLER_NAME,SUB_PRODUCT,PBI,COMPONENT_DESCRIPTION,L3,L4_ACC_ID,L5_ACC_ID,DUNS_NUM,OFFER_ID,TERM_AGREEMENT";
				final String commonSubQuerySelect = "select PRODUCT, MCN_BASE,SVID, GRC, SALES_OFFICE_CODE, CIRCUIT_NUMBER, PORT_NUMBER, ACCOUNT_NAME,CURRENCY_CODE, TYPE_OF_CHARGE,  COUNTRY_CODE, ROUTER_NUMBER, ACCT_ID, PARENT_ACCT_ID, BILLER_NAME,SUB_PRODUCT,PBI,L3,L4_ACC_ID,L5_ACC_ID,DUNS_NUM,OFFER_ID,TERM_AGREEMENT,COMPONENT_DESCRIPTION,BIT_CT, round((POST_DISCOUNT_AMT / BIT_CT),2) as ACTUALPRICE, PRE_DISCOUNT_AMT / BIT_CT as LOCALLISTPRICE,POST_DISCOUNT_AMT,PRE_DISCOUNT_AMT";
				probeMaxMinCircuitNumberSqlFirst = String.format("select min(clean_circuit_number) as MINVAL, max(clean_circuit_number) as MAXVAL from (select nvl(clean_circuit_number, ' ') as clean_circuit_number from %s where %s and TYPE_OF_CHARGE is not NULL order by nvl(clean_circuit_number, ' ')) where rownum <= :chunkSize", table, queryConditions);
				probeMaxMinCircuitNumberSqlSubsequent = String.format("select min(clean_circuit_number) as MINVAL, max(clean_circuit_number) as MAXVAL from (select nvl(clean_circuit_number, ' ') as clean_circuit_number from %s where %s and TYPE_OF_CHARGE is not NULL and nvl(clean_circuit_number, ' ') > :MAXVAL order by nvl(clean_circuit_number, ' ')) where rownum <= :chunkSize", table, queryConditions);
				probeMaxMinPortNumberSqlFirst = String.format("select min(port_number) as MINVAL, max(port_number) as MAXVAL from (select nvl(port_number, ' ') as port_number from %s where %s and TYPE_OF_CHARGE is not NULL order by nvl(port_number, ' ')) where rownum <= :chunkSize", table, queryConditions);
				probeMaxMinPortNumberSqlSubsequent = String.format("select min(port_number) as MINVAL, max(port_number) as MAXVAL from (select nvl(port_number, ' ') as port_number from %s where %s and TYPE_OF_CHARGE is not NULL and nvl(port_number, ' ') > :MAXVAL order by nvl(port_number, ' ')) where rownum <= :chunkSize", table, queryConditions);
				String baseQueryPageByCircuitNumber = String.format("select PRODUCT, MCN_BASE,SVID, GRC, SALES_OFFICE_CODE, CLEAN_CIRCUIT_NUMBER as CIRCUIT_NUMBER, PORT_NUMBER, ACCOUNT_NAME, CURRENCY_CODE, TYPE_OF_CHARGE, case when POST_DISCOUNT_AMT < 0 then 0 else to_number(POST_DISCOUNT_AMT) end as POST_DISCOUNT_AMT, case when PRE_DISCOUNT_AMT < 0 then 0 else to_number(PRE_DISCOUNT_AMT) end as PRE_DISCOUNT_AMT, CHARGE_AMOUNT, case when to_number(BIT_CT) = 0 then null else to_number(BIT_CT) end as BIT_CT,  COUNTRY_CODE, ROUTER_NUMBER, ACCT_ID, PARENT_ACCT_ID, BILLER_NAME,SUB_PRODUCT,PBI,L3,L4_ACC_ID,L5_ACC_ID,DUNS_NUM,OFFER_ID,TERM_AGREEMENT,COMPONENT_DESCRIPTION from %s where %s and TYPE_OF_CHARGE is not NULL and nvl(CLEAN_CIRCUIT_NUMBER, ' ') >= :MINVAL and nvl(CLEAN_CIRCUIT_NUMBER, ' ') <= :MAXVAL", table, queryConditions);
				String baseQueryPageByPortNumber = String.format("select PRODUCT, MCN_BASE,SVID,GRC, SALES_OFFICE_CODE, CLEAN_CIRCUIT_NUMBER as CIRCUIT_NUMBER, PORT_NUMBER, ACCOUNT_NAME, CURRENCY_CODE, TYPE_OF_CHARGE, case when POST_DISCOUNT_AMT < 0 then 0 else to_number(POST_DISCOUNT_AMT) end as POST_DISCOUNT_AMT, case when PRE_DISCOUNT_AMT < 0 then 0 else to_number(PRE_DISCOUNT_AMT) end as PRE_DISCOUNT_AMT, CHARGE_AMOUNT, case when to_number(BIT_CT) = 0 then null else to_number(BIT_CT) end as BIT_CT,  COUNTRY_CODE, ROUTER_NUMBER, ACCT_ID, PARENT_ACCT_ID, BILLER_NAME,SUB_PRODUCT,PBI,L3,L4_ACC_ID,L5_ACC_ID,DUNS_NUM,OFFER_ID,TERM_AGREEMENT,COMPONENT_DESCRIPTION from %s where %s and TYPE_OF_CHARGE is not NULL and nvl(PORT_NUMBER, ' ') >= :MINVAL and nvl(PORT_NUMBER, ' ') <= :MAXVAL", table, queryConditions);
				String queryUbStep1 = String.format("%s %s", baseQueryPageByPortNumber.replace("from", ",BIT_START_DATE,BIT_END_DATE from"), "and FILE_TYPE in ('UB', 'UB_MOW')");
				String queryUbStep2 = String.format("select a.* from ((%s) a left outer join (%s) b on nvl(a.PRODUCT, 'null') = nvl(b.PRODUCT, 'null') and nvl(a.MCN_BASE, 'null') = nvl(b.MCN_BASE, 'null') and (nvl(a.CIRCUIT_NUMBER, 'null') = nvl(b.CIRCUIT_NUMBER, 'null') or a.CIRCUIT_NUMBER is null or b.CIRCUIT_NUMBER is null) and nvl(a.PORT_NUMBER, 'null') = nvl(b.PORT_NUMBER, 'null') and nvl(a.ACCOUNT_NAME, 'null') = nvl(b.ACCOUNT_NAME, 'null') and nvl(a.TYPE_OF_CHARGE, 'null') = nvl(b.TYPE_OF_CHARGE, 'null') and nvl(a.COUNTRY_CODE, 'null') = nvl(b.COUNTRY_CODE, 'null') and nvl(a.ROUTER_NUMBER, 'null') = nvl(b.ROUTER_NUMBER, 'null') and nvl(a.ACCT_ID, 'null') = nvl(b.ACCT_ID, 'null') and nvl(a.PARENT_ACCT_ID, 'null') = nvl(b.PARENT_ACCT_ID, 'null') and nvl(a.BILLER_NAME, 'null') = nvl(b.BILLER_NAME, 'null') and nvl(a.SUB_PRODUCT, 'null') = nvl(b.SUB_PRODUCT, 'null') and nvl(a.PBI, 'null') = nvl(b.PBI, 'null') and nvl(a.L3, 'null') = nvl(b.L3, 'null') and nvl(a.L4_ACC_ID, 'null') = nvl(b.L4_ACC_ID, 'null') and nvl(a.L5_ACC_ID, 'null') = nvl(b.L5_ACC_ID, 'null') and nvl(a.DUNS_NUM, 'null') = nvl(b.DUNS_NUM, 'null') and nvl(a.OFFER_ID, 'null') = nvl(b.OFFER_ID, 'null') and nvl(a.TERM_AGREEMENT, 'null') = nvl(b.TERM_AGREEMENT, 'null') and nvl(a.COMPONENT_DESCRIPTION, 'null') = nvl(b.COMPONENT_DESCRIPTION, 'null') and a.BIT_START_DATE < b.BIT_START_DATE and a.BIT_END_DATE < b.BIT_END_DATE) where b.product is null", queryUbStep1, queryUbStep1);
				subQueryUb = String.format("%s from (%s)", commonSubQuerySelect, queryUbStep2);
				String subQueryDCS66 = String.format("select PRODUCT, MCN_BASE,SVID, GRC, SALES_OFFICE_CODE, CIRCUIT_NUMBER, PORT_NUMBER, ACCOUNT_NAME, CURRENCY_CODE, TYPE_OF_CHARGE,  COUNTRY_CODE, ROUTER_NUMBER, ACCT_ID, PARENT_ACCT_ID, BILLER_NAME,SUB_PRODUCT,PBI,L3,L4_ACC_ID,L5_ACC_ID,DUNS_NUM,OFFER_ID,TERM_AGREEMENT,COMPONENT_DESCRIPTION, 0 as POST_DISCOUNT_AMT, sum(CHARGE_AMOUNT) as PRE_DISCOUNT_AMT, sum(BIT_CT) as BIT_CT from (%s %s) %s", baseQueryPageByCircuitNumber, "and file_type in ('DCS') and FINANCIAL_REV_TYPE_CODE = 'S' and TYPE_OF_CHARGE = 'A' and CHARGE_TYPE_CODE in ('66')", commonGroupBy);
				String subQueryDCS66And13 = String.format("select PRODUCT, MCN_BASE,SVID, GRC, SALES_OFFICE_CODE, CIRCUIT_NUMBER, PORT_NUMBER, ACCOUNT_NAME, CURRENCY_CODE, TYPE_OF_CHARGE,  COUNTRY_CODE, ROUTER_NUMBER, ACCT_ID, PARENT_ACCT_ID, BILLER_NAME,SUB_PRODUCT,PBI,L3,L4_ACC_ID,L5_ACC_ID,DUNS_NUM,OFFER_ID,TERM_AGREEMENT,COMPONENT_DESCRIPTION, sum(CHARGE_AMOUNT) as POST_DISCOUNT_AMT, 0 as PRE_DISCOUNT_AMT, sum(BIT_CT) as BIT_CT from (%s %s) %s", baseQueryPageByCircuitNumber, "and file_type in ('DCS') and FINANCIAL_REV_TYPE_CODE = 'S' and TYPE_OF_CHARGE = 'A' and CHARGE_TYPE_CODE in ('66', '13')", commonGroupBy);
				String subQueryDCSAggregated = String.format("select PRODUCT, MCN_BASE,SVID, GRC, SALES_OFFICE_CODE, CIRCUIT_NUMBER, PORT_NUMBER, ACCOUNT_NAME, CURRENCY_CODE, TYPE_OF_CHARGE,  COUNTRY_CODE, ROUTER_NUMBER, ACCT_ID, PARENT_ACCT_ID, BILLER_NAME,SUB_PRODUCT,PBI,L3,L4_ACC_ID,L5_ACC_ID,DUNS_NUM,OFFER_ID,TERM_AGREEMENT,COMPONENT_DESCRIPTION, sum(POST_DISCOUNT_AMT) as POST_DISCOUNT_AMT, sum(PRE_DISCOUNT_AMT) as PRE_DISCOUNT_AMT, case when avg(BIT_CT) = 0 then null else avg(BIT_CT) end as BIT_CT from (%s union all %s) %s having sum(BIT_CT) > 0", subQueryDCS66, subQueryDCS66And13, commonGroupBy);
				subQueryDCS = String.format("%s from (%s)", commonSubQuerySelect, subQueryDCSAggregated);
			} else {
				final String commonGroupBy = "group by PRODUCT, MCN_BASE,SVID, GRC, SALES_OFFICE_CODE,CONTRACT_EFF_DT, CIRCUIT_NUMBER, PORT_NUMBER, ACCOUNT_NAME, BILLING_ELEMENT_ID, CURRENCY_CODE, TYPE_OF_CHARGE, RATE_ID, COUNTRY_CODE, ROUTER_NUMBER, ACCT_ID, PARENT_ACCT_ID, BILLER_NAME,SUB_PRODUCT,PBI,COMPONENT_DESCRIPTION,L3,L4_ACC_ID,L5_ACC_ID,DUNS_NUM,OFFER_ID,TERM_AGREEMENT,SL_ADDR_LINE_1,SL_ADDR_LINE_2,SL_CITY,SL_STATE,SL_ZIP,SL_COUNTRY";
				final String commonSubQuerySelect = "select PRODUCT, MCN_BASE,SVID, GRC, SALES_OFFICE_CODE,CONTRACT_EFF_DT, CIRCUIT_NUMBER, PORT_NUMBER, ACCOUNT_NAME, BILLING_ELEMENT_ID, CURRENCY_CODE, TYPE_OF_CHARGE, RATE_ID, COUNTRY_CODE, ROUTER_NUMBER, ACCT_ID, PARENT_ACCT_ID, BILLER_NAME,SUB_PRODUCT,PBI,L3,L4_ACC_ID,L5_ACC_ID,DUNS_NUM,OFFER_ID,TERM_AGREEMENT,COMPONENT_DESCRIPTION,SL_ADDR_LINE_1,SL_ADDR_LINE_2,SL_CITY,SL_STATE,SL_ZIP,SL_COUNTRY,BIT_CT, round((POST_DISCOUNT_AMT / BIT_CT),2) as ACTUALPRICE, PRE_DISCOUNT_AMT / BIT_CT as LOCALLISTPRICE,POST_DISCOUNT_AMT,PRE_DISCOUNT_AMT";
				probeMaxMinCircuitNumberSqlFirst = String.format("select min(clean_circuit_number) as MINVAL, max(clean_circuit_number) as MAXVAL from (select nvl(clean_circuit_number, ' ') as clean_circuit_number from %s where %s and TYPE_OF_CHARGE is not NULL order by nvl(clean_circuit_number, ' ')) where rownum <= :chunkSize", table, queryConditions);
				probeMaxMinCircuitNumberSqlSubsequent = String.format("select min(clean_circuit_number) as MINVAL, max(clean_circuit_number) as MAXVAL from (select nvl(clean_circuit_number, ' ') as clean_circuit_number from %s where %s and TYPE_OF_CHARGE is not NULL and nvl(clean_circuit_number, ' ') > :MAXVAL order by nvl(clean_circuit_number, ' ')) where rownum <= :chunkSize", table, queryConditions);
				probeMaxMinPortNumberSqlFirst = String.format("select min(port_number) as MINVAL, max(port_number) as MAXVAL from (select nvl(port_number, ' ') as port_number from %s where %s and TYPE_OF_CHARGE is not NULL order by nvl(port_number, ' ')) where rownum <= :chunkSize", table, queryConditions);
				probeMaxMinPortNumberSqlSubsequent = String.format("select min(port_number) as MINVAL, max(port_number) as MAXVAL from (select nvl(port_number, ' ') as port_number from %s where %s and TYPE_OF_CHARGE is not NULL and nvl(port_number, ' ') > :MAXVAL order by nvl(port_number, ' ')) where rownum <= :chunkSize", table, queryConditions);
				String baseQueryPageByCircuitNumber = String.format("select PRODUCT, MCN_BASE, GRC,SVID, SALES_OFFICE_CODE,CONTRACT_EFF_DT, CLEAN_CIRCUIT_NUMBER as CIRCUIT_NUMBER, PORT_NUMBER, ACCOUNT_NAME, BILLING_ELEMENT_ID, CURRENCY_CODE, TYPE_OF_CHARGE, case when POST_DISCOUNT_AMT < 0 then 0 else to_number(POST_DISCOUNT_AMT) end as POST_DISCOUNT_AMT, case when PRE_DISCOUNT_AMT < 0 then 0 else to_number(PRE_DISCOUNT_AMT) end as PRE_DISCOUNT_AMT, CHARGE_AMOUNT, case when to_number(BIT_CT) = 0 then null else to_number(BIT_CT) end as BIT_CT, RATE_ID, COUNTRY_CODE, ROUTER_NUMBER, ACCT_ID, PARENT_ACCT_ID, BILLER_NAME,SUB_PRODUCT,PBI,L3,L4_ACC_ID,L5_ACC_ID,DUNS_NUM,OFFER_ID,TERM_AGREEMENT,COMPONENT_DESCRIPTION,SL_ADDR_LINE_1,SL_ADDR_LINE_2,SL_CITY,SL_STATE,SL_ZIP,SL_COUNTRY from %s where %s and TYPE_OF_CHARGE is not NULL and nvl(CLEAN_CIRCUIT_NUMBER, ' ') >= :MINVAL and nvl(CLEAN_CIRCUIT_NUMBER, ' ') <= :MAXVAL", table, queryConditions);
				String baseQueryPageByPortNumber = String.format("select PRODUCT, MCN_BASE, GRC,SVID, SALES_OFFICE_CODE,CONTRACT_EFF_DT, CLEAN_CIRCUIT_NUMBER as CIRCUIT_NUMBER, PORT_NUMBER, ACCOUNT_NAME, BILLING_ELEMENT_ID, CURRENCY_CODE, TYPE_OF_CHARGE, case when POST_DISCOUNT_AMT < 0 then 0 else to_number(POST_DISCOUNT_AMT) end as POST_DISCOUNT_AMT, case when PRE_DISCOUNT_AMT < 0 then 0 else to_number(PRE_DISCOUNT_AMT) end as PRE_DISCOUNT_AMT, CHARGE_AMOUNT, case when to_number(BIT_CT) = 0 then null else to_number(BIT_CT) end as BIT_CT, RATE_ID, COUNTRY_CODE, ROUTER_NUMBER, ACCT_ID, PARENT_ACCT_ID, BILLER_NAME,SUB_PRODUCT,PBI,L3,L4_ACC_ID,L5_ACC_ID,DUNS_NUM,OFFER_ID,TERM_AGREEMENT,COMPONENT_DESCRIPTION,SL_ADDR_LINE_1,SL_ADDR_LINE_2,SL_CITY,SL_STATE,SL_ZIP,SL_COUNTRY from %s where %s and TYPE_OF_CHARGE is not NULL and nvl(PORT_NUMBER, ' ') >= :MINVAL and nvl(PORT_NUMBER, ' ') <= :MAXVAL", table, queryConditions);
				String queryUbStep1 = String.format("%s %s", baseQueryPageByPortNumber.replace("from", ",BIT_START_DATE,BIT_END_DATE from"), "and FILE_TYPE in ('UB', 'UB_MOW')");
				String queryUbStep2 = String.format("select a.* from ((%s) a left outer join (%s) b on nvl(a.PRODUCT, 'null') = nvl(b.PRODUCT, 'null') and nvl(a.MCN_BASE, 'null') = nvl(b.MCN_BASE, 'null') and (nvl(a.CIRCUIT_NUMBER, 'null') = nvl(b.CIRCUIT_NUMBER, 'null') or a.CIRCUIT_NUMBER is null or b.CIRCUIT_NUMBER is null) and nvl(a.PORT_NUMBER, 'null') = nvl(b.PORT_NUMBER, 'null') and nvl(a.ACCOUNT_NAME, 'null') = nvl(b.ACCOUNT_NAME, 'null') and nvl(a.BILLING_ELEMENT_ID, 'null') = nvl(b.BILLING_ELEMENT_ID, 'null') and nvl(a.TYPE_OF_CHARGE, 'null') = nvl(b.TYPE_OF_CHARGE, 'null') and nvl(a.COUNTRY_CODE, 'null') = nvl(b.COUNTRY_CODE, 'null') and nvl(a.ROUTER_NUMBER, 'null') = nvl(b.ROUTER_NUMBER, 'null') and nvl(a.ACCT_ID, 'null') = nvl(b.ACCT_ID, 'null') and nvl(a.PARENT_ACCT_ID, 'null') = nvl(b.PARENT_ACCT_ID, 'null') and nvl(a.BILLER_NAME, 'null') = nvl(b.BILLER_NAME, 'null') and nvl(a.SUB_PRODUCT, 'null') = nvl(b.SUB_PRODUCT, 'null') and nvl(a.PBI, 'null') = nvl(b.PBI, 'null') and nvl(a.L3, 'null') = nvl(b.L3, 'null') and nvl(a.L4_ACC_ID, 'null') = nvl(b.L4_ACC_ID, 'null') and nvl(a.L5_ACC_ID, 'null') = nvl(b.L5_ACC_ID, 'null') and nvl(a.DUNS_NUM, 'null') = nvl(b.DUNS_NUM, 'null') and nvl(a.OFFER_ID, 'null') = nvl(b.OFFER_ID, 'null') and nvl(a.TERM_AGREEMENT, 'null') = nvl(b.TERM_AGREEMENT, 'null') and nvl(a.COMPONENT_DESCRIPTION, 'null') = nvl(b.COMPONENT_DESCRIPTION, 'null') and a.BIT_START_DATE < b.BIT_START_DATE and a.BIT_END_DATE < b.BIT_END_DATE) where b.product is null", queryUbStep1, queryUbStep1);
				subQueryUb = String.format("%s from (%s)", commonSubQuerySelect, queryUbStep2);
				if (MyPriceConstants.ANIRA.equals(product) || MyPriceConstants.AVTS.equals(product)) {
					subQueryUb = String.format("%s from (%s) where BIT_CT > 0", commonSubQuerySelect, queryUbStep2);
				}
				String subQueryDCS66 = String.format("select PRODUCT, MCN_BASE, GRC,SVID, SALES_OFFICE_CODE,CONTRACT_EFF_DT, CIRCUIT_NUMBER, PORT_NUMBER, ACCOUNT_NAME, BILLING_ELEMENT_ID, CURRENCY_CODE, TYPE_OF_CHARGE, RATE_ID, COUNTRY_CODE, ROUTER_NUMBER, ACCT_ID, PARENT_ACCT_ID, BILLER_NAME,SUB_PRODUCT,PBI,L3,L4_ACC_ID,L5_ACC_ID,DUNS_NUM,OFFER_ID,TERM_AGREEMENT,COMPONENT_DESCRIPTION,SL_ADDR_LINE_1,SL_ADDR_LINE_2,SL_CITY,SL_STATE,SL_ZIP,SL_COUNTRY, 0 as POST_DISCOUNT_AMT, sum(CHARGE_AMOUNT) as PRE_DISCOUNT_AMT, sum(BIT_CT) as BIT_CT from (%s %s) %s", baseQueryPageByCircuitNumber, "and file_type in ('DCS') and FINANCIAL_REV_TYPE_CODE = 'S' and TYPE_OF_CHARGE = 'A' and CHARGE_TYPE_CODE in ('66')", commonGroupBy);
				String subQueryDCS66And13 = String.format("select PRODUCT, MCN_BASE, GRC,SVID, SALES_OFFICE_CODE,CONTRACT_EFF_DT, CIRCUIT_NUMBER, PORT_NUMBER, ACCOUNT_NAME, BILLING_ELEMENT_ID, CURRENCY_CODE, TYPE_OF_CHARGE, RATE_ID, COUNTRY_CODE, ROUTER_NUMBER, ACCT_ID, PARENT_ACCT_ID, BILLER_NAME,SUB_PRODUCT,PBI,L3,L4_ACC_ID,L5_ACC_ID,DUNS_NUM,OFFER_ID,TERM_AGREEMENT,COMPONENT_DESCRIPTION,SL_ADDR_LINE_1,SL_ADDR_LINE_2,SL_CITY,SL_STATE,SL_ZIP,SL_COUNTRY, sum(CHARGE_AMOUNT) as POST_DISCOUNT_AMT, 0 as PRE_DISCOUNT_AMT, sum(BIT_CT) as BIT_CT from (%s %s) %s", baseQueryPageByCircuitNumber, "and file_type in ('DCS') and FINANCIAL_REV_TYPE_CODE = 'S' and TYPE_OF_CHARGE = 'A' and CHARGE_TYPE_CODE in ('66', '13')", commonGroupBy);
				String subQueryDCSAggregated = String.format("select PRODUCT, MCN_BASE, GRC,SVID, SALES_OFFICE_CODE,CONTRACT_EFF_DT, CIRCUIT_NUMBER, PORT_NUMBER, ACCOUNT_NAME, BILLING_ELEMENT_ID, CURRENCY_CODE, TYPE_OF_CHARGE, RATE_ID, COUNTRY_CODE, ROUTER_NUMBER, ACCT_ID, PARENT_ACCT_ID, BILLER_NAME,SUB_PRODUCT,PBI,L3,L4_ACC_ID,L5_ACC_ID,DUNS_NUM,OFFER_ID,TERM_AGREEMENT,COMPONENT_DESCRIPTION,SL_ADDR_LINE_1,SL_ADDR_LINE_2,SL_CITY,SL_STATE,SL_ZIP,SL_COUNTRY, sum(POST_DISCOUNT_AMT) as POST_DISCOUNT_AMT, sum(PRE_DISCOUNT_AMT) as PRE_DISCOUNT_AMT, case when avg(BIT_CT) = 0 then null else avg(BIT_CT) end as BIT_CT from (%s union all %s) %s having sum(POST_DISCOUNT_AMT) > 0 and sum(BIT_CT) > 0", subQueryDCS66, subQueryDCS66And13, commonGroupBy);
				subQueryDCS = String.format("%s from (%s)", commonSubQuerySelect, subQueryDCSAggregated);
			}
		do {
			if (!queryParam.containsKey("MINVAL")) {
				List<Map<String, Object>> probeRes = namedjdbcTemplate.queryForList(probeMaxMinCircuitNumberSqlFirst, queryParam);
				queryParam.putAll(probeRes.get(0));
			} else {
				List<Map<String, Object>> probeRes = namedjdbcTemplate.queryForList(probeMaxMinCircuitNumberSqlSubsequent, queryParam);
				queryParam.putAll(probeRes.get(0));
			}
			List<Map<String, Object>> queryRes = namedjdbcTemplate.queryForList(subQueryDCS, queryParam);
			res.addAll(queryRes);
		} while (queryParam.get("MINVAL") != null);
		queryParam.remove("MINVAL");
		queryParam.remove("MAXVAL");
		do {
			if (!queryParam.containsKey("MINVAL")) {
				List<Map<String, Object>> probeRes = namedjdbcTemplate.queryForList(probeMaxMinPortNumberSqlFirst, queryParam);
				queryParam.putAll(probeRes.get(0));
			} else {
				List<Map<String, Object>> probeRes = namedjdbcTemplate.queryForList(probeMaxMinPortNumberSqlSubsequent, queryParam);
				queryParam.putAll(probeRes.get(0));
			}
			List<Map<String, Object>> queryRes = namedjdbcTemplate.queryForList(subQueryUb, queryParam);
			res.addAll(queryRes);
		} while (queryParam.get("MINVAL") != null);
		return res;
	}
}

			public List<NxDwInventory>  findBySearchCriteriaAndProduct(Object obj,String product){
			Map<String,Object> dynamicQueryMap=getDynamicQueryparamBasedParamAndProduct(obj,product);
			String querybuiledrString=(String)dynamicQueryMap.get("querybuilderString");
			@SuppressWarnings("unchecked")
			Map<String,Object> queryParam=(Map<String,Object> )dynamicQueryMap.get("queryParamMap");
			String queryString = String.format("select *  FROM NX_DW_INVENTORY nxdw where %s  order by nxdw.id",querybuiledrString);
			Query query = em.createNativeQuery(queryString, NxDwInventory.class);
			for (Entry<String,Object> entry : queryParam.entrySet()) {
				query.setParameter(entry.getKey(), entry.getValue());
			}
			List<NxDwInventory> resultList = query.getResultList();

			return resultList;
		}
		
		
		public List<Object[]>  findPortNumberDetailsBySearchCriteriaAndProduct(Object obj,String product){
			Map<String,Object> dynamicQueryMap=getDynamicQueryparamBasedParamAndProduct(obj,product);
			String querybuiledrString=(String)dynamicQueryMap.get("querybuilderString");
			@SuppressWarnings("unchecked")
			Map<String,Object> queryParam=(Map<String,Object> )dynamicQueryMap.get("queryParamMap");
			String queryString = String.format("select PORT_NUMBER,PARENT_ACCT_ID,ACCT_ID,L3 from nx_dw_inventory where %s and file_type in ('UB','UB_MOW') and port_number is not null",querybuiledrString);
			
			Query query = em.createNativeQuery(queryString);
			for (Entry<String,Object> entry : queryParam.entrySet()) {
				query.setParameter(entry.getKey(), entry.getValue());
			}
			List<Object[]> resultList = query.getResultList();

			return resultList;
		}

		public List<Object[]>  findCircuitToAdiAccessBySearchCriteriaAndProduct(Object obj,String product,
				List<String> circuitNumberList){
			 String querybuilderString = MyPriceConstants.USRP_AVPN_ADI_NXDWINV_CRITERIA;	
			Map<String,Object> queryParam =  new HashMap<>() ;	
			if(null !=obj) {
			String searchCriteriaJson=obj.toString();
			JsonNode searchCriteriaNode = JacksonUtil.toJsonNode(searchCriteriaJson);
			if (searchCriteriaNode.has("billMonth")
					&& !Strings.isNullOrEmpty(searchCriteriaNode.get("billMonth").asText())) {
				queryParam.put("reqBillDate",searchCriteriaNode.get("billMonth").asText());
				queryParam.put("product", "PLS");
				}
			}
			//ends here
			
			/*Map<String,Object> dynamicQueryMap=getDynamicQueryparamBasedParamAndProduct(obj,product);
			String querybuilderString=(String)dynamicQueryMap.get("querybuilderString");
			@SuppressWarnings("unchecked")
			Map<String,Object> queryParam=(Map<String,Object> )dynamicQueryMap.get("queryParamMap");
			queryParam.put("product", "PLS");*/
			/*In query has limitation of 1000 entries*/
			List<List<String>> smallerCircuitNumberList = Lists.partition(circuitNumberList, chunkSize);	
			List<Object[]> result = new ArrayList<>();

			String queryString= String.format("select clean_circuit_number, product,id,type_of_charge from nx_dw_inventory where %s and clean_circuit_number in(:circuitList)",querybuilderString);
			for(List<String> l: smallerCircuitNumberList) {
				Query query = em.createNativeQuery(queryString);
				for (Entry<String,Object> entry : queryParam.entrySet()) {
					query.setParameter(entry.getKey(), entry.getValue());
				}
				query.setParameter("circuitList", l);
				List<Object[]> resultList = query.getResultList();
				result.addAll(resultList);
			}
			return result;
		}
		
		@Transactional
		@Modifying
		public void  updateBasedOnId(List<Long> idList,String product,String typeOfCharge){
			String queryString= String.format("update nx_dw_inventory set product=:product,"
					+ " type_of_charge=:typeOfCharge where id IN (:ID)");
			List<List<Long>> smallerList = Lists.partition(idList, chunkSize);	
			for(List<Long> l: smallerList) {
				Query query = em.createNativeQuery(queryString);
				query.setParameter("ID", l);
				query.setParameter("product", product);
				query.setParameter("typeOfCharge", typeOfCharge);
				query.executeUpdate();
			}

		}
		
		public List<Object[]>  findCircuitToAvpnAccessBySearchCriteriaAndProduct(Object obj,String product,
				List<String> circuitNumberList){
			//271022: commenting as AVPN nxdwinv table will be queried with cktID+prod+bill month- the user search criteria will not be consumed
			//starts here
			/*Map<String,Object> dynamicQueryMap=getDynamicQueryparamBasedParamAndProduct(obj,product);
			String qb=(String)dynamicQueryMap.get("querybuilderString");
			String querybuilderString=qb.replace("product=:product", "product IN (:product)");
			@SuppressWarnings("unchecked")
			Map<String,Object> queryParam=(Map<String,Object> )dynamicQueryMap.get("queryParamMap");*/
			String querybuilderString = MyPriceConstants.USRP_AVPN_ADI_NXDWINV_CRITERIA;	
			Map<String,Object> queryParam =  new HashMap<>() ;	
			if(null !=obj) {
			String searchCriteriaJson=obj.toString();
			JsonNode searchCriteriaNode = JacksonUtil.toJsonNode(searchCriteriaJson);
			if (searchCriteriaNode.has("billMonth")
					&& !Strings.isNullOrEmpty(searchCriteriaNode.get("billMonth").asText())) {
				queryParam.put("reqBillDate",searchCriteriaNode.get("billMonth").asText());}
			}
			//ends here
			Set<String> productSet = new HashSet<>();
			productSet.add("AVPN");
			productSet.add("PLS");
			queryParam.put("product", productSet);
			/*In query has limitation of 1000 entries*/
			List<List<String>> smallerCircuitNumberrList = Lists.partition(circuitNumberList, chunkSize);	
			List<Object[]> result = new ArrayList<>();

			String queryString= String.format("select clean_circuit_number, product,id,type_of_charge from nx_dw_inventory \r\n" + 
					"where %s and  clean_circuit_number in(:circuitList) and  type_of_charge!='P'",querybuilderString);
			
			for(List<String> l: smallerCircuitNumberrList) {
				Query query = em.createNativeQuery(queryString);
				for (Entry<String,Object> entry : queryParam.entrySet()) {
					query.setParameter(entry.getKey(), entry.getValue());
				}
				query.setParameter("circuitList", l);
				List<Object[]> resultList = query.getResultList();
				result.addAll(resultList);
			}
			return result;
		}

		public List<Object[]> findQualifiedADIPort(Object obj,String product){
			Map<String,Object> dynamicQueryMap=getDynamicQueryparamBasedParamAndProduct(obj,product);
			String querybuilderString=(String)dynamicQueryMap.get("querybuilderString");
			@SuppressWarnings("unchecked")
			Map<String,Object> queryParam=(Map<String,Object> )dynamicQueryMap.get("queryParamMap");
			queryParam.put("product", product);
			String queryString=String.format("select nxdw.id,nxdw.product,nxdw.mcn_base,nxdw.type_of_charge,nxdw.is_qualify,nxdw.clean_circuit_number \r\n" + 
					"from nx_dw_inventory nxdw \r\n" + 
					"where %s and type_of_charge='P' and clean_circuit_number \r\n" + 
					"in (select clean_circuit_number from nx_dw_inventory where  %s and type_of_charge='A') ",querybuilderString,querybuilderString);
			Query query = em.createNativeQuery(queryString);
			for (Entry<String,Object> entry : queryParam.entrySet()) {
				query.setParameter(entry.getKey(), entry.getValue());
			}
			List<Object[]> resultList = query.getResultList();
			return resultList;
			
		}
		
		public String getAccountNameCountBySearchCriteria(Object obj,String product) {
			Map<String, Object> dynamicQueryMap = getDynamicQueryparamBasedParamAndProduct(obj, product);
			String query = (String) dynamicQueryMap.get("querybuilderString");
			@SuppressWarnings("unchecked")
			Map<String, Object> queryParam = (Map<String, Object>) dynamicQueryMap.get("queryParamMap");
			String sql = String.format(
					"select account_name from %s where %s and account_name is not null and  rownum=1 order by id desc",
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
		
		
	public List<String>  findADIPortCircuitToAccess(Object obj){
		Map<String,Object> dynamicQueryMap=getDynamicQueryparamBasedParamAndProduct(obj,"ADI");
		String querybuiledrString=(String)dynamicQueryMap.get("querybuilderString");
		@SuppressWarnings("unchecked")
		Map<String,Object> queryParam=(Map<String,Object> )dynamicQueryMap.get("queryParamMap");
		String queryString = String.format("select unique clean_circuit_number from %s where %s " + 
				"and type_of_charge='P' and clean_circuit_number not in(select clean_circuit_number from %s where "
				+ "%s and type_of_charge='A' and file_type='UB')",table,querybuiledrString,table,querybuiledrString);
		Query query = em.createNativeQuery(queryString);
		for (Entry<String,Object> entry : queryParam.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		List<String> resultList = query.getResultList();

		return resultList;
	}
	

	
	@Transactional
	@Modifying
	public void  updateDcsFileDetailBasedOncircuitId(Map<String, Map<String, Object>> circuitIdDetail,Object obj,String product){
		Map<String,Object> dynamicQueryMap=getDynamicQueryparamBasedParamAndProduct(obj,product);
		String querybuiledrString=(String)dynamicQueryMap.get("querybuilderString");
		@SuppressWarnings("unchecked")
		Map<String,Object> queryParam=(Map<String,Object> )dynamicQueryMap.get("queryParamMap");
		String queryString= String.format("update nx_dw_inventory set PARENT_ACCT_ID=:PARENT_ACCT_ID,"
				+ " ACCT_ID=:ACCT_ID,L3=:L3  where %s and file_type in ('DCS') and CLEAN_CIRCUIT_NUMBER=:CLEAN_CIRCUIT_NUMBER",querybuiledrString);
		Query query = em.createNativeQuery(queryString);

		for (Entry<String,Object> entry : queryParam.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}

		  for (Map.Entry<String, Map<String, Object>> entry : circuitIdDetail.entrySet()) {
				query.setParameter("PARENT_ACCT_ID", entry.getValue().get("PARENT_ACCT_ID"));
				query.setParameter("ACCT_ID", entry.getValue().get("ACCT_ID"));
				query.setParameter("CLEAN_CIRCUIT_NUMBER", entry.getKey());
				query.setParameter("L3", entry.getValue().get("L3"));
				query.executeUpdate();

	    }

	}

}
