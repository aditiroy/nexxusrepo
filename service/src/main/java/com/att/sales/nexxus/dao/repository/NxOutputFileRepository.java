package com.att.sales.nexxus.dao.repository;

import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.dao.model.NxDwPriceDetails;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;

/**
 * The Interface NxOutputFileRepository.
 */
@Repository
@Transactional
public interface NxOutputFileRepository extends JpaRepository<NxOutputFileModel, Long> {
	
	/**
	 * Gets the nexxus output by ids.
	 *
	 * @param nxReqId the nx req id
	 * @return the nexxus output by ids
	 */
	@Query(value="SELECT no FROM NxRequestDetails nr JOIN nr.nxOutputFiles no WHERE nr.nxReqId IN (:nxReqId)")
	List<NxOutputFileModel> findByMultipleRequestIds(@Param("nxReqId") Set<Long> nxReqId);
	
	
	/**
	 * Find by request id.
	 *
	 * @param nxReqId the nx req id
	 * @return the list
	 */
	@Query(value="SELECT no FROM NxRequestDetails nr JOIN nr.nxOutputFiles no WHERE nr.nxReqId =:nxReqId")
	List<NxOutputFileModel> findByNxReqId(@Param("nxReqId") Long nxReqId);
	
	/**
	 * Delete by nx request details.
	 *
	 * @param nxRequestDetails the nx request details
	 */
	void deleteByNxRequestDetails(@Param("nxRequestDetails") NxRequestDetails nxRequestDetails);
	
	@Query(value="select DISTINCT circuitId,actualPrice from json_table((select MP_OUTPUT_JSON from nx_output_file where NX_REQ_ID =:nxReqId),'$' " + 
			"columns(NESTED PATH '$.accountDetails[*]' " + 
			"columns(NESTED PATH '$.site[*]' " + 
			"columns(NESTED PATH '$.design[*]' " + 
			"columns(circuitId varchar2(500) path '$.circuitId', " + 
			"NESTED PATH '$.priceDetails[*]' " + 
			"columns(actualPrice varchar2(500) path '$.actualPrice', " + 
			"    priceType VARCHAR2 ( 500 ) PATH '$.priceType' " + 
			")))))) where priceType ='ACCESSBEID' and circuitId is not null",nativeQuery = true) 
	List<Object[]> fetchAVPNIntlData( @Param("nxReqId")Long nxReqId);
	
	@Query(value="select nxt1key, nxKeyIdAccess|| '$'||count(nxt1key) as newnxkeyid , count(nxt1key) as qty from ( "+ 
			"select nxSiteId || '$' || case when LENGTH(CIRCUITID) <= 10 then circuitid else SUBSTR(circuitid, 0,10) end || '$' || SiteNPANXX " + 
			"|| '$' || CustSrvgWireCtrCLLICd as nxt1key , " + 
			"nxKeyIdAccess from json_table((select MP_OUTPUT_JSON from nx_output_file where NX_REQ_ID =:nxReqId),'$' " + 
			"			columns(NESTED PATH '$.accountDetails[*]' " + 
			"			columns(NESTED PATH '$.site[*]'  " + 
			"			columns(NESTED PATH '$.design[*]' " + 
			"			columns(circuitId varchar2(500) path '$.circuitId', " + 
			"            nxSiteId varchar2(500) path '$.nxSiteId', " + 
			"           accessProductName varchar2(500) path '$.accessProductName', " + 
			"            CustSrvgWireCtrCLLICd varchar2(500) path '$.CustSrvgWireCtrCLLICd', " + 
			"            SiteNPANXX varchar2(500) path '$.SiteNPANXX', " + 
			"            nxKeyIdAccess varchar2(1000) path '$.nxKeyIdAccess'			" + 
			"			))))) where (accessProductName ='TDM') and circuitId is not null) group by nxt1key, nxKeyIdAccess", nativeQuery = true)
	List<Object[]> fetchTdmNxt1(@Param("nxReqId")Long nxReqId);
	
	@Query(value="SELECT circuitId FROM nx_output_file, JSON_TABLE(MP_OUTPUT_JSON, '$.accountDetails[*].site[*].design[*]' COLUMNS (circuitId varchar2(500) path '$.circuitId')) where nx_req_id in (select nx_req_id from nx_request_details where nx_solution_id= :nxSolutionId) and circuitId is not null union SELECT circuitId FROM nx_output_file, JSON_TABLE(MP_OUTPUT_JSON, '$.accountDetails[*].site[*]' COLUMNS (circuitId varchar2(500) path '$.circuitId')) where nx_req_id in (select nx_req_id from nx_request_details where nx_solution_id= :nxSolutionId) and circuitId is not null union SELECT circuitId FROM nx_output_file, JSON_TABLE(MP_OUTPUT_JSON, '$.DomesticIOCInventory.CustomerAccountInfo[*].CustomerSubAccountInfo.CustomerCircuitInfo[*]' COLUMNS (circuitId varchar2(500) path '$.CktId')) where nx_req_id in (select nx_req_id from nx_request_details where nx_solution_id= :nxSolutionId) and circuitId is not null union SELECT circuitId FROM nx_output_file, JSON_TABLE(MP_OUTPUT_JSON, '$.DomesticEthernetIOCInventory.CustomerAccountInfo[*].CustomerSubAccountInfo.CustomerCircuitInfo[*]' COLUMNS (circuitId varchar2(500) path '$.CktId')) where nx_req_id in (select nx_req_id from nx_request_details where nx_solution_id= :nxSolutionId) and circuitId is not null union SELECT circuitId FROM nx_output_file, JSON_TABLE(MP_OUTPUT_JSON, '$.DomesticDSODS1AccessInventory.CustomerAccountInfo[*].CustomerSubAccountInfo.CustomerCircuitInfo[*]' COLUMNS (circuitId varchar2(500) path '$.CktId')) where nx_req_id in (select nx_req_id from nx_request_details where nx_solution_id= :nxSolutionId) and circuitId is not null union SELECT circuitId FROM nx_output_file, JSON_TABLE(MP_OUTPUT_JSON, '$.DomesticEthernetAccessInventory.CustomerAccountInfo[*].CustomerSubAccountInfo.CustomerCircuitInfo[*]' COLUMNS (circuitId varchar2(500) path '$.CktId')) where nx_req_id in (select nx_req_id from nx_request_details where nx_solution_id= :nxSolutionId) and circuitId is not null union SELECT circuitId FROM nx_output_file, JSON_TABLE(MP_OUTPUT_JSON, '$.DomesticDS3OCXAccessInventory.CustomerAccountInfo[*].CustomerSubAccountInfo.CustomerCircuitInfo[*]' COLUMNS (circuitId varchar2(500) path '$.CktId')) where nx_req_id in (select nx_req_id from nx_request_details where nx_solution_id= :nxSolutionId) and circuitId is not null", nativeQuery = true)
	Set<String> fetchCircuitId(@Param("nxSolutionId") Long nxSolutionId);
	
	@Query(value="SELECT DISTINCT circuitid, nxsiteid, nxsiteidz FROM nx_output_file       nof JOIN nx_request_details   rq ON rq.nx_req_id = nof.nx_req_id AND rq.product_cd = 'AVPN', JSON_TABLE ( mp_output_json, '$.accountDetails[*].site[*].design[*]' COLUMNS ( circuitid VARCHAR2 ( 500 ) PATH '$.icoreSiteId', nxsiteid NUMBER PATH '$.nxSiteId', nxsiteidz NUMBER PATH '$.nxSiteIdZ' ) ) WHERE nof.nxsiteid_ind IN ( 'Y', 'R' ) AND nof.nx_req_id IN ( :nxReqId ) AND circuitid IS NOT NULL AND nxsiteid IS NOT NULL UNION SELECT DISTINCT circuitid, nxsiteid, nxsiteidz FROM nx_output_file       nof JOIN nx_request_details   rq ON rq.nx_req_id = nof.nx_req_id AND rq.product_cd IN ( 'GMIS', 'MIS/PNT' ), JSON_TABLE ( mp_output_json, '$.accountDetails[*].site[*].design[*]' COLUMNS ( circuitid VARCHAR2 ( 500 ) PATH '$.circuitId', nxsiteid NUMBER PATH '$.nxSiteId', nxsiteidz NUMBER PATH '$.nxSiteIdZ' ) ) WHERE nof.nxsiteid_ind IN ( 'Y', 'R' ) AND nof.nx_req_id IN ( :nxReqId ) AND circuitid IS NOT NULL AND nxsiteid IS NOT NULL UNION SELECT DISTINCT circuitid, nxsiteid, nxsiteidz FROM nx_output_file       nof JOIN nx_request_details   rq ON rq.nx_req_id = nof.nx_req_id AND rq.product_cd IN ( 'AVTS', 'ANIRA' ), JSON_TABLE ( mp_output_json, '$.accountDetails[*].design[*]' COLUMNS ( circuitid VARCHAR2 ( 500 ) PATH '$.siteId', nxsiteid NUMBER PATH '$.nxSiteId', nxsiteidz NUMBER PATH '$.nxSiteIdZ' ) ) WHERE nof.nxsiteid_ind IN ( 'Y', 'R' ) AND nof.nx_req_id IN ( :nxReqId ) AND circuitid IS NOT NULL AND nxsiteid IS NOT NULL UNION SELECT DISTINCT circuitid, nxsiteid, nxsiteidz FROM nx_output_file       nof JOIN nx_request_details   rq ON rq.nx_req_id = nof.nx_req_id AND rq.product_cd = 'EPLSWAN', JSON_TABLE ( mp_output_json, '$.accountDetails[*].design[*]' COLUMNS ( circuitid VARCHAR2 ( 500 ) PATH '$.circuitId', nxsiteid NUMBER PATH '$.nxSiteId', nxsiteidz NUMBER PATH '$.nxSiteIdZ' ) ) WHERE nof.nxsiteid_ind IN ( 'Y', 'R' ) AND nof.nx_req_id IN ( :nxReqId ) AND circuitid IS NOT NULL AND nxsiteid IS NOT NULL", nativeQuery = true)
	List<Object[]> fetchCircuitIdAndNxsiteId(@Param("nxReqId") List<Long> nxReqId);
	
	@Query(value="select DISTINCT circuitId,actualPrice from json_table((select MP_OUTPUT_JSON from nx_output_file where NX_REQ_ID =:nxReqId),'$' " + 
			"columns(NESTED PATH '$.accountDetails[*]' " + 
			"columns(NESTED PATH '$.site[*]' " + 
			"columns(NESTED PATH '$.design[*]' " + 
			"columns(circuitId varchar2(500) path '$.circuitId', " + 
			"NESTED PATH '$.priceDetails[*]' " + 
			"columns(actualPrice varchar2(500) path '$.actualPrice', " + 
			"    priceType VARCHAR2 ( 500 ) PATH '$.priceType' " + 
			")))))) where priceType ='ACCESSBEID' and circuitId is not null",nativeQuery = true) 
	List<Object[]> fetchAceessData( @Param("nxReqId")Long nxReqId);
	
	@Query(value="SELECT DISTINCT circuitid, icoreSiteId FROM nx_output_file       nof JOIN nx_request_details   rq ON rq.nx_req_id = nof.nx_req_id AND rq.product_cd = 'AVPN' and rq.FLOW_TYPE='USRP',  JSON_TABLE ( mp_output_json, '$.accountDetails[*].site[*].design[*]' COLUMNS ( icoreSiteId VARCHAR2 ( 500 ) PATH '$.icoreSiteId', circuitId VARCHAR2 ( 500 ) PATH '$.circuitId' ) )  WHERE nof.nx_req_id IN ( :nxReqId ) and circuitid in (:cktId)", nativeQuery = true)
	List<Object[]> fetchIcoreSiteIdByCktId(@Param("nxReqId") List<Long> nxReqId, @Param("cktId") Set<String> cktId);
	
	@Query(value="SELECT DISTINCT circuitid, nxsiteid FROM nx_output_file       nof JOIN nx_request_details   rq ON rq.nx_req_id = nof.nx_req_id AND rq.product_cd = 'AVPN', JSON_TABLE ( mp_output_json, '$.accountDetails[*].site[*].design[*]' COLUMNS ( circuitid VARCHAR2 ( 500 ) PATH '$.circuitId', nxsiteid NUMBER PATH '$.nxSiteId' ) ) WHERE nof.nxsiteid_ind IN ( 'Y', 'R' ) AND nof.nx_req_id IN ( :nxReqId ) AND circuitid IS NOT NULL AND nxsiteid IS NOT NULL UNION SELECT DISTINCT circuitid, nxsiteid FROM nx_output_file       nof JOIN nx_request_details   rq ON rq.nx_req_id = nof.nx_req_id AND rq.product_cd IN ( 'GMIS', 'MIS/PNT' ), JSON_TABLE ( mp_output_json, '$.accountDetails[*].site[*].design[*]' COLUMNS ( circuitid VARCHAR2 ( 500 ) PATH '$.circuitId', nxsiteid NUMBER PATH '$.nxSiteId' ) ) WHERE nof.nxsiteid_ind IN ( 'Y', 'R' ) AND nof.nx_req_id IN ( :nxReqId ) AND circuitid IS NOT NULL AND nxsiteid IS NOT NULL UNION SELECT DISTINCT circuitid, nxsiteid FROM nx_output_file       nof JOIN nx_request_details   rq ON rq.nx_req_id = nof.nx_req_id AND rq.product_cd = 'AVTS', JSON_TABLE ( mp_output_json, '$.accountDetails[*].design[*]' COLUMNS ( circuitid VARCHAR2 ( 500 ) PATH '$.siteId', nxsiteid NUMBER PATH '$.nxSiteId' ) ) WHERE nof.nxsiteid_ind IN ( 'Y', 'R' ) AND nof.nx_req_id IN ( :nxReqId ) AND circuitid IS NOT NULL AND nxsiteid IS NOT NULL", nativeQuery = true)
	List<Object[]> fetchCircuitIdAndNxsiteIdByReqId(@Param("nxReqId") List<Long> nxReqId);
	

	@Query(value="select * from nx_output_file where nx_req_id in (select nx_req_id from nx_request_details where nx_request_group_id=:nxRequestGroupId)", nativeQuery=true)
	List<NxOutputFileModel> findByNxRequestGrpId(@Param("nxRequestGroupId") Long nxRequestGroupId);
	
	@Transactional
	@Modifying
	@Query(value="delete from nx_output_file where nx_req_id=:nxReqId", nativeQuery=true)
	int deleteByNxReqId(@Param("nxReqId") Long nxReqId);

}
