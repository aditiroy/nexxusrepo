package com.att.sales.nexxus.dao.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;

/**
 * The Class NamedQueryHolder.
 *
 * @author sw088d
 */
@NamedNativeQueries({

		@NamedNativeQuery(name = "getNexxusSolutionsByTxnNDealId", query = "SELECT a.nx_solution_id,a.nxs_description,a.opty_id,a.duns_number,a.gu_duns_number,a.l3_value,a.l4_value,a.customer_name,a.inr_status_ind, a.igloo_status_ind,a.created_user,a.created_date,a.modified_date,a.flow_type, a.archived_sol_ind,(SELECT Count(*) FROM nx_access_pricing_data WHERE nx_solution_id = a.nx_solution_id) AS AP_COUNT,(SELECT Count(*) FROM nx_access_pricing_data WHERE nx_solution_id = a.nx_solution_id AND include_yn = 'Y'  and (HAS_REQUIRED_FIELDS='Y' OR HAS_REQUIRED_FIELDS is null)) AS AP_SELECTED_COUNT, (SELECT nx_lookup_data.description FROM nx_lookup_data WHERE nx_lookup_data.dataset_name = 'SOLUTION_REQUEST_STATUS' AND nx_lookup_data.item_id = b.status) AS REQ_STATUS,b.nx_req_id,b.product_cd,b.cpni_approver,b.status,b.nx_req_desc,b.modified_date AS edfReqModifiedDate,"
				+"b.DMAAP_BULK_STATUS,(SELECT nx_lookup_data.description FROM nx_lookup_data WHERE nx_lookup_data.dataset_name ='DMAAP_BULK_STATUS' AND nx_lookup_data.item_id = b.dmaap_bulk_status) AS DMAPP_BULK_STATUS_DESC,b.created_date AS edfReqCreateDate,b.BULK_REQ_YN ,b.SOURCE_SOL_ID "
				+ "FROM (SELECT * FROM (SELECT rownum rnum, i.* FROM (SELECT * FROM nx_solution_details WHERE Nvl(nx_solution_id, -9) = To_number((select nvl(nx_solution_id, -9 )  from nx_mp_deal where my_price_txn_id = :transactionId AND deal_id = :dealId )) AND archived_sol_ind=:archivedSolInd ORDER BY modified_date DESC) i WHERE rownum <= ( :ROW_FETCH_COUNT * :fetchBatchIndex )) WHERE rnum > ( ( :ROW_FETCH_COUNT * :fetchBatchIndex ) - :ROW_FETCH_COUNT )) a LEFT JOIN nx_request_details b ON a.nx_solution_id = b.nx_solution_id AND a.archived_sol_ind =:archivedSolInd ORDER BY a.modified_date DESC, b.created_date DESC", resultSetMapping = "sqlToNexxusSolutionDetailUIModelMapping"),

		@NamedNativeQuery(name = "getNexxusSolutionsByUserId", query = "select  a.NX_SOLUTION_ID, a.NXS_DESCRIPTION, a.OPTY_ID, a.DUNS_NUMBER, a.GU_DUNS_NUMBER, a.L3_VALUE, "
				+ "a.L4_VALUE, a.CUSTOMER_NAME, a.inr_status_ind, a.igloo_status_ind, a.CREATED_USER, a.CREATED_DATE, a.MODIFIED_DATE, a.FLOW_TYPE, a.archived_sol_ind, "
				+ "(select count(*) from NX_ACCESS_PRICING_DATA where NX_SOLUTION_ID = a.NX_SOLUTION_ID and (HAS_REQUIRED_FIELDS='Y' OR HAS_REQUIRED_FIELDS is null)) as AP_COUNT, "
				+ "(select count(*) from NX_ACCESS_PRICING_DATA where NX_SOLUTION_ID = a.NX_SOLUTION_ID and INCLUDE_YN='Y' and (HAS_REQUIRED_FIELDS='Y' OR HAS_REQUIRED_FIELDS is null)) as AP_SELECTED_COUNT, "
				+ "(select NX_LOOKUP_DATA.DESCRIPTION from NX_LOOKUP_DATA where NX_LOOKUP_DATA.DATASET_NAME = 'SOLUTION_REQUEST_STATUS' and NX_LOOKUP_DATA.ITEM_ID = b.STATUS) as REQ_STATUS, "
				+ "b.NX_REQ_ID, b.PRODUCT_CD, b.CPNI_APPROVER, b.STATUS, b.NX_REQ_DESC, b.MODIFIED_DATE as edfReqModifiedDate, b.CREATED_DATE as edfReqCreateDate,"
				+ "b.DMAAP_BULK_STATUS,(SELECT nx_lookup_data.description FROM nx_lookup_data WHERE nx_lookup_data.dataset_name ='DMAAP_BULK_STATUS' AND nx_lookup_data.item_id = b.dmaap_bulk_status) AS DMAPP_BULK_STATUS_DESC, b.BULK_REQ_YN ,b.SOURCE_SOL_ID "
				+ "from (select * from (select rownum rnum, i.* from (select * from NX_SOLUTION_DETAILS "
				+ "where upper(created_user)=upper(:userId) and archived_sol_ind =:archivedSolInd  and NVL(OPTY_ID,'X') = NVL(to_char(:optyId), NVL(OPTY_ID,'X')) and NVL(NX_SOLUTION_ID,-9) = NVL(to_number(:nxId), NVL(NX_SOLUTION_ID, -9))"
				+ "and NVL(FLOW_TYPE,'X') in ('INR', 'FMO') "
				+ "and  NVL(EXTERNAL_KEY,-9) = NVL(to_number(:externalId), NVL(EXTERNAL_KEY, -9)) "
				+ "order by MODIFIED_DATE desc) "
				+ " i where rownum <=(:ROW_FETCH_COUNT * :fetchBatchIndex)) where rnum > ( (:ROW_FETCH_COUNT * :fetchBatchIndex) - :ROW_FETCH_COUNT) ) a "
				+ "left join NX_REQUEST_DETAILS b " + "on a.NX_SOLUTION_ID = b.NX_SOLUTION_ID "
				+ "order by a.MODIFIED_DATE desc, b.CREATED_DATE desc", resultSetMapping = "sqlToNexxusSolutionDetailUIModelMapping"),

		@NamedNativeQuery(name = "getNexxusSolutionsBySearchCriteria", query = "select a.NX_SOLUTION_ID, a.NXS_DESCRIPTION, a.OPTY_ID, a.DUNS_NUMBER, a.GU_DUNS_NUMBER, a.L3_VALUE, a.L4_VALUE, "
				+ "a.CUSTOMER_NAME, a.inr_status_ind, a.igloo_status_ind, a.CREATED_USER, a.CREATED_DATE, a.MODIFIED_DATE, a.FLOW_TYPE, a.archived_sol_ind, (select count(*) from NX_ACCESS_PRICING_DATA where NX_SOLUTION_ID = a.NX_SOLUTION_ID and (HAS_REQUIRED_FIELDS='Y' OR HAS_REQUIRED_FIELDS is null)) as AP_COUNT, "
				+ "(select count(*) from NX_ACCESS_PRICING_DATA where NX_SOLUTION_ID = a.NX_SOLUTION_ID and INCLUDE_YN='Y'  and (HAS_REQUIRED_FIELDS='Y' OR HAS_REQUIRED_FIELDS is null)) as AP_SELECTED_COUNT, "
				+ "(select NX_LOOKUP_DATA.DESCRIPTION from NX_LOOKUP_DATA where NX_LOOKUP_DATA.DATASET_NAME = 'SOLUTION_REQUEST_STATUS' and NX_LOOKUP_DATA.ITEM_ID = b.STATUS) as REQ_STATUS, "
				+ "b.NX_REQ_ID, b.PRODUCT_CD, b.CPNI_APPROVER, b.STATUS, b.NX_REQ_DESC, b.MODIFIED_DATE as edfReqModifiedDate, b.CREATED_DATE as edfReqCreateDate,"
				+"b.DMAAP_BULK_STATUS,(SELECT nx_lookup_data.description FROM nx_lookup_data WHERE nx_lookup_data.dataset_name ='DMAAP_BULK_STATUS' AND nx_lookup_data.item_id = b.dmaap_bulk_status) AS DMAPP_BULK_STATUS_DESC,b.BULK_REQ_YN, b.SOURCE_SOL_ID   " 
				+"from NX_SOLUTION_DETAILS a left join NX_REQUEST_DETAILS b on a.NX_SOLUTION_ID = b.NX_SOLUTION_ID "
				+ "where a.NX_SOLUTION_ID in "
				+ "(select NX_SOLUTION_ID from ( select rownum rnum, i.NX_SOLUTION_ID from ( "
				+ "select distinct s.NX_SOLUTION_ID, s.MODIFIED_DATE from NX_SOLUTION_DETAILS s left join NX_REQUEST_DETAILS r on s.NX_SOLUTION_ID = r.NX_SOLUTION_ID  "
				+ "	where ( "
				+ "		upper(s.CREATED_USER)=upper(:userId)  OR s.NX_SOLUTION_ID in (select t.NX_SOLUTION_ID from NX_TEAM t where upper(t.ATTUID)=upper(:userId))) "
				+ "		and (to_char(s.NX_SOLUTION_ID) like '%'||upper(:filter)||'%' or nvl(upper(s.NXS_DESCRIPTION), ' ') like '%'||upper(:filter)||'%' or nvl(upper(s.CUSTOMER_NAME), ' ') like '%'||upper(:filter)||'%' or nvl(upper(s.CREATED_USER), ' ') like '%'||upper(:filter)||'%') "
				+ "and NVL(s.FLOW_TYPE,'X') in ('INR', 'FMO') "
				+ "		order by s.MODIFIED_DATE desc ) i where ROWNUM <= (:ROW_FETCH_COUNT * :fetchBatchIndex)) "
				+ "  where rnum > ( (:ROW_FETCH_COUNT * :fetchBatchIndex) - :ROW_FETCH_COUNT) ) AND a.archived_sol_ind =:archivedSolInd	"
				+ "order by a.MODIFIED_DATE desc, b.CREATED_DATE desc", resultSetMapping = "sqlToNexxusSolutionDetailUIModelMapping"),
		
		@NamedNativeQuery(name = "getNexxusSolutionsByTxnNDealIdByGroup", query = "SELECT a.nx_solution_id, a.nxs_description, a.opty_id, a.duns_number, a.gu_duns_number, a.l3_value, a.l4_value, a.customer_name, a.created_user, a.inr_status_ind, a.igloo_status_ind, a.created_date, a.modified_date, a.flow_type,(select count(*) from NX_DESIGN_AUDIT where NX_REF_ID=a.nx_solution_id and TRANSACTION='Ethernet Token Bulkupload') AS FAILED_TOKENS_COUNT, ( SELECT COUNT(*) FROM nx_access_pricing_data WHERE nx_solution_id = a.nx_solution_id and (HAS_REQUIRED_FIELDS='Y' OR HAS_REQUIRED_FIELDS is null)) AS ap_count, ( SELECT COUNT(*) FROM nx_access_pricing_data WHERE nx_solution_id = a.nx_solution_id AND include_yn = 'Y' and (HAS_REQUIRED_FIELDS='Y' OR HAS_REQUIRED_FIELDS is null) ) AS ap_selected_count,( SELECT COUNT(*) FROM nx_access_pricing_data WHERE nx_solution_id = a.nx_solution_id AND HAS_REQUIRED_FIELDS = 'N' ) AS MISSING_FIELD_COUNT, ( SELECT nx_lookup_data.description FROM nx_lookup_data WHERE nx_lookup_data.dataset_name = 'SOLUTION_REQUEST_STATUS' AND nx_lookup_data.item_id = b.status ) AS req_status,"
				+ "d.deal_status  AS deal_status, d.deal_id  AS deal_id,d.revision AS deal_revision,d.version AS deal_version,d.my_price_txn_id AS my_price_txn_id, b.nx_req_id, b.product_cd, b.cpni_approver, b.status, b.nx_req_desc, b.nx_request_group_id,b.nx_request_group_name,"
				+ "c.description AS nx_request_group_description,c.group_id AS nx_lookup_group_groupid,( SELECT nx_lookup_data.dataset_name FROM nx_lookup_data WHERE dataset_name IN ('SERVICE_ACCESS_GROUP', 'SERVICE_ACCESS_GROUP', 'ACCESS_GROUP') AND nx_lookup_data.item_id =c.status) AS group_name,(SELECT nx_lookup_data.description FROM nx_lookup_data WHERE dataset_name in ('REQUEST_GROUP_STATUS') AND nx_lookup_data.item_id = c.status) AS nx_request_group_status,c.status as nx_request_group_status_id, b.modified_date  AS edfreqmodifieddate, b.created_date AS edfreqcreatedate ,"
				+ "b.DMAAP_BULK_STATUS,(SELECT nx_lookup_data.description FROM nx_lookup_data WHERE nx_lookup_data.dataset_name ='DMAAP_BULK_STATUS' AND nx_lookup_data.item_id = b.dmaap_bulk_status) AS DMAPP_BULK_STATUS_DESC,b.BULK_REQ_YN,b.SOURCE_SOL_ID,a.is_locked, case when  a.is_locked = 'Y' and usr.first_name is not null then usr.first_name || ', ' || usr.last_name end AS locked_by_user_name,b.flow_type as req_flow_type "
				+ "FROM ( SELECT * FROM ( SELECT ROWNUM rnum, i.* FROM ( SELECT * FROM nx_solution_details WHERE nvl(nx_solution_id,-9) = to_number( ( SELECT nvl(nx_solution_id,-9) FROM nx_mp_deal WHERE my_price_txn_id =:transactionId AND deal_id =:dealId AND nvl(flow_type,'X') IN (:flowType) ) ) and archived_sol_ind=:archivedSolInd ORDER BY modified_date DESC ) i WHERE ROWNUM <= (:ROW_FETCH_COUNT *:fetchBatchIndex ) ) WHERE rnum > ( (:ROW_FETCH_COUNT *:fetchBatchIndex ) -:ROW_FETCH_COUNT ) ) "
				+ "a left JOIN nx_user usr ON usr.user_att_id = a.locked_by_user LEFT JOIN nx_request_details b ON a.nx_solution_id = b.nx_solution_id  AND b.active_yn = 'Y' LEFT JOIN nx_request_group c ON c.nx_request_group_id = b.nx_request_group_id LEFT JOIN (select deal_status, deal_id , revision, version, my_price_txn_id, nx_solution_id,row_number() OVER(PARTITION BY nx_solution_id ORDER BY my_price_txn_id desc) row_num from nx_mp_deal where active_yn = 'Y') d on d.nx_solution_id = a.nx_solution_id and d.row_num = 1 AND a.archived_sol_ind =:archivedSolInd ORDER BY a.modified_date DESC, b.modified_date DESC,c.modified_date DESC", resultSetMapping = "sqlToNexxusSolutionDetailUIModelMapping"),
		
		@NamedNativeQuery(name = "getNexxusSolutionsByUserIdByGroup", query = "SELECT a.nx_solution_id, a.nxs_description, a.opty_id, a.duns_number, a.gu_duns_number, a.l3_value, a.l4_value, a.customer_name, a.inr_status_ind, a.igloo_status_ind, a.created_user, a.created_date, a.modified_date, a.flow_type,a.archived_sol_ind,(select count(*) from NX_DESIGN_AUDIT where NX_REF_ID=a.nx_solution_id and TRANSACTION='Ethernet Token Bulkupload') AS FAILED_TOKENS_COUNT, ( SELECT COUNT(*) FROM nx_access_pricing_data WHERE nx_solution_id = a.nx_solution_id  and (HAS_REQUIRED_FIELDS='Y' OR HAS_REQUIRED_FIELDS is null) ) AS ap_count, ( SELECT COUNT(*) FROM nx_access_pricing_data WHERE nx_solution_id = a.nx_solution_id AND include_yn = 'Y'  and (HAS_REQUIRED_FIELDS='Y' OR HAS_REQUIRED_FIELDS is null)) AS ap_selected_count,( SELECT COUNT(*) FROM nx_access_pricing_data WHERE nx_solution_id = a.nx_solution_id AND HAS_REQUIRED_FIELDS = 'N' ) AS MISSING_FIELD_COUNT, ( SELECT nx_lookup_data.description FROM nx_lookup_data WHERE nx_lookup_data.dataset_name = 'SOLUTION_REQUEST_STATUS' AND nx_lookup_data.item_id = b.status ) AS req_status,"
				+ "d.deal_status AS deal_status, d.deal_id AS deal_id,d.revision AS deal_revision,d.version AS deal_version,d.my_price_txn_id AS my_price_txn_id,"
				+ " b.nx_req_id, b.product_cd, b.cpni_approver, b.status, b.nx_req_desc, b.nx_request_group_id,b.nx_request_group_name, b.SOURCE_SOL_ID,"
				+ "c.description AS nx_request_group_description,c.group_id AS nx_lookup_group_groupid,"
				+ "( SELECT nx_lookup_data.dataset_name FROM nx_lookup_data WHERE dataset_name IN ('SERVICE_ACCESS_GROUP', 'SERVICE_ACCESS_GROUP', 'ACCESS_GROUP') "
				+ "AND nx_lookup_data.item_id = c.status) AS group_name,( SELECT nx_lookup_data.description FROM nx_lookup_data WHERE dataset_name in ('REQUEST_GROUP_STATUS') AND nx_lookup_data.item_id = c.status) AS nx_request_group_status, "
				+ "c.status as nx_request_group_status_id, b.modified_date   AS edfreqmodifieddate, b.created_date AS edfreqcreatedate , b.DMAAP_BULK_STATUS,(SELECT nx_lookup_data.description FROM nx_lookup_data WHERE nx_lookup_data.dataset_name ='DMAAP_BULK_STATUS' AND nx_lookup_data.item_id = b.dmaap_bulk_status) AS DMAPP_BULK_STATUS_DESC,b.BULK_REQ_YN,a.is_locked, case when  a.is_locked = 'Y' and usr.first_name is not null then usr.first_name || ', ' || usr.last_name end AS locked_by_user_name,b.flow_type as req_flow_type  "
				+ "FROM ( SELECT * FROM ( SELECT ROWNUM rnum, i.* FROM ( SELECT * FROM nx_solution_details WHERE nx_solution_id in (select nx_solution_id from nx_team where upper(attuid)=upper(:userId)) and archived_sol_ind=:archivedSolInd AND nvl(opty_id,'X') = nvl(TO_CHAR(:optyId),nvl(opty_id,'X') ) AND nvl(nx_solution_id,-9) = nvl(to_number(:nxId),nvl(nx_solution_id,-9) ) AND nvl(flow_type,'X') IN ( :flowType ) AND nvl(external_key,-9) = nvl(to_number(:externalId),nvl(external_key,-9) ) ORDER BY modified_date DESC ) i WHERE ROWNUM <= (:ROW_FETCH_COUNT *:fetchBatchIndex ) ) WHERE rnum > ( (:ROW_FETCH_COUNT *:fetchBatchIndex ) -:ROW_FETCH_COUNT ) ) a left JOIN nx_user usr ON usr.user_att_id = a.locked_by_user LEFT JOIN nx_request_details b ON a.nx_solution_id = b.nx_solution_id  AND b.active_yn = 'Y' LEFT JOIN "
				+ "nx_request_group c ON c.nx_request_group_id = b.nx_request_group_id LEFT JOIN (select deal_status, deal_id , revision, version, my_price_txn_id, nx_solution_id,row_number() OVER(PARTITION BY nx_solution_id ORDER BY my_price_txn_id desc) row_num from nx_mp_deal where active_yn = 'Y') d on d.nx_solution_id = a.nx_solution_id and d.row_num = 1 ORDER BY a.modified_date DESC, b.modified_date DESC,c.modified_date DESC", resultSetMapping = "sqlToNexxusSolutionDetailUIModelMapping"),

		
		@NamedNativeQuery(name = "getNexxusSolutionsBySearchCriteriaByGroup", query = "SELECT a.nx_solution_id,a.nxs_description,a.opty_id,a.duns_number,   a.gu_duns_number,   a.l3_value,   a.l4_value,   a.customer_name,  a.inr_status_ind, a.igloo_status_ind, a.created_user,   a.created_date,   a.modified_date,   a.flow_type, a.archived_sol_ind,   (SELECT COUNT(*) FROM NX_DESIGN_AUDIT WHERE NX_REF_ID=a.nx_solution_id  AND TRANSACTION='Ethernet Token Bulkupload') AS FAILED_TOKENS_COUNT,   (SELECT COUNT(*)   FROM nx_access_pricing_data   WHERE nx_solution_id = a.nx_solution_id   and (HAS_REQUIRED_FIELDS='Y' OR HAS_REQUIRED_FIELDS is null)) AS ap_count,(SELECT COUNT(*)   FROM nx_access_pricing_data   WHERE nx_solution_id = a.nx_solution_id   AND include_yn = 'Y' and (HAS_REQUIRED_FIELDS='Y' OR HAS_REQUIRED_FIELDS is null)  ) AS ap_selected_count, ( SELECT COUNT(*) FROM nx_access_pricing_data WHERE nx_solution_id = a.nx_solution_id AND HAS_REQUIRED_FIELDS = 'N' ) AS MISSING_FIELD_COUNT,  (SELECT nx_lookup_data.description   FROM nx_lookup_data   WHERE nx_lookup_data.dataset_name = 'SOLUTION_REQUEST_STATUS'   AND nx_lookup_data.item_id        = b.status   ) AS req_status, "
				+ "d.deal_status AS deal_status, d.deal_id AS deal_id, d.revision AS deal_revision,d.version AS deal_version, d.my_price_txn_id AS my_price_txn_id,   b.nx_req_id,   b.product_cd,   b.cpni_approver,   b.status,   b.nx_req_desc,   b.nx_request_group_id,   b.nx_request_group_name, b.SOURCE_SOL_ID, "
				+ "c.description AS nx_request_group_description,c.group_id AS nx_lookup_group_groupid, "
				+ "(SELECT nx_lookup_data.dataset_name   FROM nx_lookup_data   WHERE dataset_name  IN ('SERVICE_ACCESS_GROUP', 'SERVICE_GROUP', 'ACCESS_GROUP')   AND nx_lookup_data.item_id =c.status) AS group_name, (SELECT nx_lookup_data.description   FROM nx_lookup_data   WHERE dataset_name IN ('REQUEST_GROUP_STATUS')   AND nx_lookup_data.item_id = c.status) AS nx_request_group_status,"
				+ "c.status AS nx_request_group_status_id,b.modified_date AS edfreqmodifieddate,   b.created_date  AS edfreqcreatedate ,"
				+ "b.DMAAP_BULK_STATUS,(SELECT nx_lookup_data.description FROM nx_lookup_data WHERE nx_lookup_data.dataset_name ='DMAAP_BULK_STATUS' AND nx_lookup_data.item_id = b.dmaap_bulk_status) AS DMAPP_BULK_STATUS_DESC,b.BULK_REQ_YN, a.is_locked, case when  a.is_locked = 'Y' and usr.first_name is not null then usr.first_name || ', ' || usr.last_name end AS locked_by_user_name,b.flow_type as req_flow_type "
				+ "FROM nx_solution_details a left JOIN nx_user usr ON usr.user_att_id = a.locked_by_user LEFT JOIN nx_request_details b ON a.nx_solution_id = b.nx_solution_id AND b.active_yn='Y' LEFT JOIN nx_request_group c "
				+ "ON c.nx_request_group_id = b.nx_request_group_id LEFT JOIN (select deal_status, deal_id , revision, version, my_price_txn_id, nx_solution_id,row_number() "
				+ "OVER(PARTITION BY nx_solution_id ORDER BY my_price_txn_id desc) row_num from nx_mp_deal where active_yn = 'Y') d on d.nx_solution_id = a.nx_solution_id and d.row_num = 1 "
				+ "WHERE a.archived_sol_ind=:archivedSolInd and a.nx_solution_id IN   (SELECT nx_solution_id   FROM     (SELECT ROWNUM rnum,  i.nx_solution_id     FROM   ( SELECT DISTINCT  s.nx_solution_id,  s.modified_date FROM nx_solution_details   s "
				+ "LEFT JOIN nx_request_details    r ON s.nx_solution_id = r.nx_solution_id  AND r.active_yn = 'Y' LEFT JOIN nx_request_group      t ON t.nx_request_group_id = r.nx_request_group_id LEFT JOIN nx_mp_deal "
				+ "nd on nd.nx_solution_id = s.nx_solution_id and nd.active_yn = 'Y' Left join (SELECT COUNT(*) as ap_count, nx_solution_id FROM nx_access_pricing_data group by nx_solution_id) q on q.nx_solution_id = s.nx_solution_id"
				+ "  WHERE ( upper(s.created_user) = upper(:userId) OR s.nx_solution_id IN ( SELECT t.nx_solution_id FROM nx_team t WHERE upper(t.attuid) = upper(:userId) ) )  "
				+ "AND (  to_char(s.nx_solution_id) LIKE '%' || upper(:filter) || '%' OR nvl(upper(s.nxs_description), ' ') LIKE '%' || upper(:filter) || '%' OR nvl(upper(s.customer_name), ' ') LIKE '%' || upper(:filter) || '%' OR nvl(upper(s.created_user), ' ') LIKE '%' || upper(:filter) || '%' OR ( upper('Submitted for InR Retrieval') LIKE '%' || upper(:filter) || '%' AND s.flow_type IN ( 'iglooQuote', 'INR' ) AND nd.deal_status IS NULL AND ( 0 != ( SELECT COUNT(*) FROM nx_request_details WHERE nx_solution_id = s.nx_solution_id AND ( status = 10 OR status IS NULL ) ) ) ) OR ( upper('Ready for review') LIKE '%' || upper(:filter) || '%' AND ( ( s.flow_type = 'iglooQuote' AND q.ap_count != 0 AND nd.deal_status IS NULL ) OR ( s.flow_type = 'INR' AND nd.deal_status IS NULL AND ( 0 != ( SELECT COUNT(*) FROM nx_request_details WHERE nx_solution_id = s.nx_solution_id AND ( status != 10 OR status IS NULL ) ) ) ) OR ( s.flow_type = 'FMO' AND nd.deal_status = 'CREATED' AND r.status = 20 ) ) ) OR ( upper('N/A') LIKE '%' || upper(:filter) || '%' AND ( ( s.flow_type = 'iglooQuote' AND q.ap_count = 0 AND nd.deal_status IS NULL ) OR ( s.flow_type = 'FMO' AND nd.deal_status IS NULL ) ) ) OR ( upper('Submitted to myPrice') LIKE '%' || upper(:filter) || '%' AND ((nd.deal_status = 'SUBMITTED' AND  s.flow_type in ('INR', 'iglooQuote')) OR (s.flow_type = 'FMO' AND nd.deal_status = 'CREATED' and r.status != 20))) OR ( upper('Submission to myPrice Failed') LIKE '%' || upper(:filter) || '%' AND nd.deal_status = 'FAILED' ) OR ( upper('Partially submitted to myPrice') LIKE '%' || upper(:filter) || '%' AND nd.deal_status = 'PARTIAL' ) OR ( upper('Upload to myPrice in progress') LIKE '%' || upper(:filter) || '%' AND (nd.deal_status = 'CREATED' AND s.flow_type in ('INR', 'iglooQuote'))) "
				+ "OR ( upper('Deal Approved in myPrice') LIKE '%' || upper(:filter) || '%' AND ( nd.deal_status = 'APPROVED' OR nd.deal_status = 'Approved' ) ) )"
				+ " AND nvl(s.flow_type, 'X') IN ( :flowType ) ORDER BY s.modified_date DESC) i  WHERE ROWNUM <= (:ROW_FETCH_COUNT *:fetchBatchIndex ))   WHERE rnum > ( (:ROW_FETCH_COUNT *:fetchBatchIndex ) -:ROW_FETCH_COUNT )) ORDER BY a.modified_date DESC, b.modified_date DESC,c.modified_date DESC" , resultSetMapping = "sqlToNexxusSolutionDetailUIModelMapping"),
		

		@NamedNativeQuery(name = "getNexxusSolutionsBySearchCriteriaByGroupAdminUser", query = "SELECT a.nx_solution_id, a.nxs_description, a.opty_id, a.duns_number, a.gu_duns_number, a.l3_value, a.l4_value, a.customer_name, a.inr_status_ind, a.igloo_status_ind, a.created_user, a.created_date, a.modified_date, a.flow_type, a.archived_sol_ind, ( SELECT COUNT(*) FROM nx_design_audit WHERE nx_ref_id = a.nx_solution_id AND transaction = 'Ethernet Token Bulkupload' ) AS failed_tokens_count, ( SELECT COUNT(*) FROM nx_access_pricing_data WHERE nx_solution_id = a.nx_solution_id and (HAS_REQUIRED_FIELDS='Y' OR HAS_REQUIRED_FIELDS is null) ) AS ap_count, ( SELECT COUNT(*) FROM nx_access_pricing_data WHERE nx_solution_id = a.nx_solution_id AND include_yn = 'Y'  and (HAS_REQUIRED_FIELDS='Y' OR HAS_REQUIRED_FIELDS is null)) AS ap_selected_count, ( SELECT nx_lookup_data.description FROM nx_lookup_data WHERE nx_lookup_data.dataset_name = 'SOLUTION_REQUEST_STATUS' AND nx_lookup_data.item_id = b.status ) AS req_status, d.deal_status       AS deal_status, d.deal_id           AS deal_id, d.revision          AS deal_revision, d.version           AS deal_version, d.my_price_txn_id   AS my_price_txn_id, b.nx_req_id, b.product_cd, b.cpni_approver, b.status, b.nx_req_desc, b.nx_request_group_id, b.nx_request_group_name, c.description       AS nx_request_group_description, c.group_id          AS nx_lookup_group_groupid, ( SELECT nx_lookup_data.dataset_name FROM nx_lookup_data WHERE dataset_name IN ( 'SERVICE_ACCESS_GROUP', 'SERVICE_GROUP', 'ACCESS_GROUP' ) AND nx_lookup_data.item_id = c.status ) AS group_name, ( SELECT COUNT(*) FROM nx_access_pricing_data WHERE nx_solution_id = a.nx_solution_id AND HAS_REQUIRED_FIELDS = 'N' ) AS MISSING_FIELD_COUNT, ( SELECT nx_lookup_data.description FROM nx_lookup_data WHERE dataset_name IN ( 'REQUEST_GROUP_STATUS' ) AND nx_lookup_data.item_id = c.status ) AS nx_request_group_status, c.status            AS nx_request_group_status_id, b.modified_date     AS edfreqmodifieddate, b.created_date      AS edfreqcreatedate, b.dmaap_bulk_status, ( SELECT nx_lookup_data.description FROM nx_lookup_data WHERE nx_lookup_data.dataset_name = 'DMAAP_BULK_STATUS' AND nx_lookup_data.item_id = b.dmaap_bulk_status ) AS dmapp_bulk_status_desc, b.bulk_req_yn, b.source_sol_id, a.is_locked, case when  a.is_locked = 'Y' and usr.first_name is not null then usr.first_name || ', ' || usr.last_name end AS locked_by_user_name,b.flow_type as req_flow_type FROM nx_solution_details     a left JOIN nx_user usr ON usr.user_att_id = a.locked_by_user LEFT JOIN nx_request_details   b ON a.nx_solution_id = b.nx_solution_id AND b.active_yn = 'Y' LEFT JOIN nx_request_group  c ON c.nx_request_group_id = b.nx_request_group_id LEFT JOIN ( SELECT deal_status, deal_id, revision, version, my_price_txn_id, nx_solution_id, ROW_NUMBER() OVER( PARTITION BY nx_solution_id ORDER BY my_price_txn_id DESC ) row_num FROM nx_mp_deal WHERE active_yn = 'Y' ) d ON d.nx_solution_id = a.nx_solution_id AND d.row_num = 1 WHERE nvl(a.flow_type, 'X') IN ( :flowType ) AND a.nx_solution_id IN ( SELECT nx_solution_id FROM ( SELECT ROWNUM rnum, i.nx_solution_id FROM ( SELECT DISTINCT s.nx_solution_id, s.modified_date FROM nx_solution_details    s LEFT JOIN nx_request_details   r ON s.nx_solution_id = r.nx_solution_id AND r.active_yn = 'Y' LEFT JOIN nx_request_group  t ON t.nx_request_group_id = r.nx_request_group_id LEFT JOIN nx_mp_deal  nd ON nd.nx_solution_id = s.nx_solution_id AND nd.active_yn = 'Y' LEFT JOIN ( SELECT COUNT(*) AS ap_count, nx_solution_id FROM nx_access_pricing_data GROUP BY nx_solution_id ) q ON q.nx_solution_id = s.nx_solution_id WHERE nvl(s.flow_type, 'X') IN ( :flowType ) AND ( to_char(s.nx_solution_id) LIKE '%' || upper(:filter) || '%' OR nvl(upper(s.nxs_description), ' ') LIKE '%' || upper(:filter) || '%' OR nvl(upper(s.customer_name), ' ') LIKE '%' || upper(:filter) || '%' OR nvl(upper(s.created_user), ' ') LIKE '%' || upper(:filter) || '%' OR ( upper('Submitted for InR Retrieval') LIKE '%' || upper(:filter) || '%' AND s.flow_type IN ( 'iglooQuote', 'INR' ) AND nd.deal_status IS NULL AND ( 0 != ( SELECT COUNT(*) FROM nx_request_details WHERE nx_solution_id = s.nx_solution_id AND ( status = 10 OR status IS NULL ) ) ) ) OR ( upper('Ready for review') LIKE '%' || upper(:filter) || '%' AND ( ( s.flow_type = 'iglooQuote' AND q.ap_count != 0 AND nd.deal_status IS NULL ) OR ( s.flow_type = 'INR' AND nd.deal_status IS NULL AND ( 0 != ( SELECT COUNT(*) FROM nx_request_details WHERE nx_solution_id = s.nx_solution_id AND ( status != 10 OR status IS NULL ) ) ) ) OR ( s.flow_type = 'FMO' AND nd.deal_status = 'CREATED' AND r.status = 20 ) ) ) OR ( upper('N/A') LIKE '%' || upper(:filter) || '%' AND ( ( s.flow_type = 'iglooQuote' AND q.ap_count = 0 AND nd.deal_status IS NULL ) OR ( s.flow_type = 'FMO' AND nd.deal_status IS NULL ) ) ) OR ( upper('Submitted to myPrice') LIKE '%' || upper(:filter) || '%' AND ((nd.deal_status = 'SUBMITTED' AND  s.flow_type in ('INR', 'iglooQuote')) OR (s.flow_type = 'FMO' AND nd.deal_status = 'CREATED' and r.status != 20))) OR ( upper('Submission to myPrice Failed') LIKE '%' || upper(:filter) || '%' AND nd.deal_status = 'FAILED' ) OR ( upper('Partially submitted to myPrice') LIKE '%' || upper(:filter) || '%' AND nd.deal_status = 'PARTIAL' ) OR ( upper('Upload to myPrice in progress') LIKE '%' || upper(:filter) || '%' AND (nd.deal_status = 'CREATED' AND s.flow_type in ('INR', 'iglooQuote'))) OR ( upper('Deal Approved in myPrice') LIKE '%' || upper(:filter) || '%' AND ( nd.deal_status = 'APPROVED' OR nd.deal_status = 'Approved' ) ) ) ORDER BY s.modified_date DESC ) i WHERE ROWNUM <= ( :ROW_FETCH_COUNT * :fetchBatchIndex ) ) WHERE rnum > ( ( :ROW_FETCH_COUNT * :fetchBatchIndex ) - :ROW_FETCH_COUNT ) ) AND a.archived_sol_ind =:archivedSolInd ORDER BY a.modified_date DESC, b.modified_date DESC, c.modified_date DESC" , resultSetMapping = "sqlToNexxusSolutionDetailUIModelMapping")




})


@SqlResultSetMappings({ @SqlResultSetMapping(name = "sqlToNexxusSolutionDetailUIModelMapping", classes = {
		@ConstructorResult(targetClass = NexxusSolutionDetailUIModel.class, columns = {
				@ColumnResult(name = "NX_SOLUTION_ID", type = Long.class),
				@ColumnResult(name = "deal_status", type = String.class),
				@ColumnResult(name = "deal_id", type = String.class),
				@ColumnResult(name = "deal_revision", type = String.class),
				@ColumnResult(name = "deal_version", type = String.class),
				@ColumnResult(name = "my_price_txn_id", type = String.class),
				@ColumnResult(name = "NXS_DESCRIPTION", type = String.class),
				@ColumnResult(name = "OPTY_ID", type = String.class),
				@ColumnResult(name = "DUNS_NUMBER", type = String.class),
				@ColumnResult(name = "GU_DUNS_NUMBER", type = String.class),
				@ColumnResult(name = "L3_VALUE", type = String.class),
				@ColumnResult(name = "L4_VALUE", type = String.class),
				@ColumnResult(name = "CUSTOMER_NAME", type = String.class),
				@ColumnResult(name = "CREATED_USER", type = String.class),
				@ColumnResult(name = "CREATED_DATE", type = String.class),
				@ColumnResult(name = "MODIFIED_DATE", type = Date.class),
				@ColumnResult(name = "FLOW_TYPE", type = String.class),
				@ColumnResult(name = "ARCHIVED_SOL_IND", type = String.class),
				@ColumnResult(name = "FAILED_TOKENS_COUNT", type = String.class),
				@ColumnResult(name = "AP_COUNT", type = String.class),
				@ColumnResult(name = "AP_SELECTED_COUNT", type = String.class),
				@ColumnResult(name = "NX_REQ_ID", type = Long.class),
				@ColumnResult(name = "PRODUCT_CD", type = String.class),
				@ColumnResult(name = "CPNI_APPROVER", type = String.class),
				@ColumnResult(name = "STATUS", type = String.class),
				@ColumnResult(name = "NX_REQ_DESC", type = String.class),
				@ColumnResult(name = "nx_request_group_id", type = Long.class),
				@ColumnResult(name = "nx_request_group_name", type = String.class),
				@ColumnResult(name = "nx_lookup_group_groupid", type = Long.class),
				@ColumnResult(name = "group_name", type = String.class),
				@ColumnResult(name = "nx_request_group_status", type = String.class),
				@ColumnResult(name = "nx_request_group_status_id", type = String.class),
				@ColumnResult(name = "edfReqModifiedDate", type = Date.class),
				@ColumnResult(name = "edfReqCreateDate", type = Date.class),
				@ColumnResult(name = "REQ_STATUS", type = String.class),
				@ColumnResult(name = "nx_request_group_description", type = String.class),
				@ColumnResult(name = "DMAAP_BULK_STATUS", type = String.class),
				@ColumnResult(name = "DMAPP_BULK_STATUS_DESC",type = String.class),
				@ColumnResult(name = "BULK_REQ_YN",type = String.class),
				@ColumnResult(name = "INR_STATUS_IND",type = String.class),
				@ColumnResult(name = "IGLOO_STATUS_IND",type = String.class),
				@ColumnResult(name = "SOURCE_SOL_ID",type = Long.class),
				@ColumnResult(name = "IS_LOCKED",type = String.class),
				@ColumnResult(name = "LOCKED_BY_USER_NAME",type = String.class),
				@ColumnResult(name = "REQ_FLOW_TYPE",type = String.class),
				@ColumnResult(name = "MISSING_FIELD_COUNT",type = String.class)
			}) }) })
@Entity
public class NamedQueryHolder implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The id. */
	@Id
	private Integer id;
}
