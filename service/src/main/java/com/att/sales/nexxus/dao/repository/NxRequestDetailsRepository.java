package com.att.sales.nexxus.dao.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;

/**
 * The Interface NxRequestDetailsRepository.
 */
@Repository
@Transactional
public interface NxRequestDetailsRepository extends JpaRepository<NxRequestDetails, Long>{
	
	
	/**
	 * Find by nx req id.
	 *
	 * @param nxReqId the nx req id
	 * @return the nx request details
	 */
	NxRequestDetails findByNxReqId(@Param("nxReqId") Long nxReqId);
	
	/* (non-Javadoc)
	 * @see org.springframework.data.jpa.repository.JpaRepository#findAll()
	 */
	public List<NxRequestDetails> findAll();

	/**
	 * Update edf ack.
	 *
	 * @param nxSolutionId the nx solution id
	 * @param requestId the request id
	 */
	@Query(value="update n.EDF_ACK_ID from NxRequestDetails n where n.NX_SOLUTION_ID in :nxSolutionId and n.NX_REQ_ID in :requestId",nativeQuery = true)
    void updateEdfAck(@Param("nxSolutionId") String nxSolutionId, @Param("requestId") String requestId);
	
	@Transactional
	@Modifying(clearAutomatically = true)	  
	@Query(value="update nx_request_details set active_yn = 'N' where nx_Req_Id = :nxReqId",nativeQuery=true)
	void inactiveReqDetails(@Param("nxReqId") Long nxReqId);

	/**
	 * Gets the nx requests.
	 *
	 * @param status the status
	 * @param dateValue the date value
	 * @return the nx requests
	 */
	@Query(value = "select * from NX_REQUEST_DETAILS WHERE status = :inProgressStatus AND CREATED_DATE < :dateThreshold",nativeQuery=true)
	public List<NxRequestDetails> getNxRequests(@Param("inProgressStatus") Long status, @Param("dateThreshold") Date dateValue);
	
	/**
	 * Find by nx req id in.
	 *
	 * @param nxReqIds the nx req ids
	 * @return the list
	 */
	List<NxRequestDetails> findByNxReqIdIn(List<Long> nxReqIds);
	
	@Query(value = "select lookup.DESCRIPTION as currentReqStatus, nx.NX_REQ_ID as nxReqId, nx.NX_SOLUTION_ID\r\n" + 
			"as nxSolutionId, nx.status as currentStatus, case when nx.status = :status then 'N' else 'Y' end as statusChanged ,"
			+ "lookup1.description as groupStatus from NX_REQUEST_DETAILS nx left join nx_request_group gp on gp.nx_request_group_id = nx.nx_request_group_id"
			+ " and gp.ACTIVE_YN = 'Y' LEFT join nx_lookup_data lookup1 on lookup1.ITEM_ID = GP.STATUS and lookup1.DATASET_NAME = 'REQUEST_GROUP_STATUS'"
			+ " join nx_lookup_data lookup on lookup.ITEM_ID = nx.STATUS and lookup.DATASET_NAME = 'SOLUTION_REQUEST_STATUS' where nx.NX_REQ_ID = :nxReqId", nativeQuery = true)
	List<Object[]> findByStatusAndNxReqId(@Param("status") Long status, @Param("nxReqId") Long nxReqId);
	
	@Query(value = "select data from NxRequestDetails data where data.nxRequestGroupId = :nxRequestGroupId and data.activeYn = :activeYn")
	public List<NxRequestDetails> findRequestsByGroupId(@Param("nxRequestGroupId") Long nxRequestGroupId,  @Param("activeYn") String activeYn);

	@Query("select data from NxRequestDetails data where data.nxSolutionDetail =:nxSolutionDetail and activeYn = :activeYn and data.nxRequestGroupId in (:nxRequestGroupId)")
	List<NxRequestDetails> findbyNSolutionIdAndActiveYnAndNxRequestGrpIds(@Param("nxSolutionDetail") NxSolutionDetail nxSolutionDetail, @Param("activeYn") String activeYn, @Param("nxRequestGroupId") List<Long> nxRequestGroupId);
	
	@Query("select data from NxRequestDetails data where data.nxSolutionDetail =:nxSolutionDetail and data.nxRequestGroupId = :nxRequestGroupId and activeYn = :activeYn and data.status not in (90, 100) and data.submitReqAddrEditInd is null ORDER BY data.flowType desc")
	List<NxRequestDetails> findbyNxSolutionDetailAndNxRequestGroupIdAndActiveYn(@Param("nxSolutionDetail") NxSolutionDetail nxSolutionDetail, @Param("nxRequestGroupId") Long nxRequestGroupId, @Param("activeYn") String activeYn);
	
	NxRequestDetails findByNxReqIdAndActiveYn(Long nxReqId, String activeYn);
	
	NxRequestDetails findByEdfAckId(String edfAckId);
	
	List<NxRequestDetails> findByNxRequestGroupIdAndActiveYn(Long nxRequestGroupId, String activeYn);
	
	List<NxRequestDetails> findByEdfAckIdAndActiveYn(String edfAckId, String activeYn);
	
	List<NxRequestDetails> findByNxSolutionDetailAndActiveYn(NxSolutionDetail nxSolutionDetail, String activeYn);
	
	@Query("select data from NxRequestDetails data where data.nxSolutionDetail =:nxSolutionDetail and data.activeYn = :activeYn and flowType in (:flowType)")
	List<NxRequestDetails> findByNxSolutionDetailAndActiveYnAndFlowType(@Param("nxSolutionDetail") NxSolutionDetail nxSolutionDetail, @Param("activeYn") String activeYn, 
			@Param("flowType") List<String> flowType);
	
	@Query("select data from NxRequestDetails data where data.nxSolutionDetail =:nxSolutionDetail and data.nxRequestGroupId = :nxRequestGroupId and data.activeYn = :activeYn and data.status in (:status)")
	List<NxRequestDetails> findbyNxSolutionDetailAndNxRequestGroupIdAndActiveYnAndStatus(@Param("nxSolutionDetail") NxSolutionDetail nxSolutionDetail, @Param("nxRequestGroupId") Long nxRequestGroupId, 
			@Param("activeYn") String activeYn, @Param("status") List<Long> status);
	
	@Modifying(clearAutomatically = true)	  
	@Query(value="update nx_request_details set status = :newStatus, MODIFIED_DATE=:modifiedDate where nx_request_group_id = :groupId and active_yn = 'Y' and status = :oldStatus",nativeQuery=true)
	int updateRequestStatus(@Param("groupId") Long groupId, @Param("newStatus") Long newStatus, @Param("oldStatus") Long oldStatus, @Param("modifiedDate") Date modifiedDate);
	
	List<NxRequestDetails> findByEdfAckIdAndActiveYnAndNxRequestGroupId(String edfAckId, String activeYn, Long nxRequestGroupId);
	
	@Query("select data from NxRequestDetails data where data.nxSolutionDetail =:nxSolutionDetail and activeYn = :activeYn and data.status in (:status)")
	List<NxRequestDetails> findbyNxSolutionIdAndActiveYnAndStatus(@Param("nxSolutionDetail") NxSolutionDetail nxSolutionDetail, @Param("activeYn") String activeYn, @Param("status") List<Long> status);

	List<NxRequestDetails> findByEdfAckIdAndActiveYnAndNxSolutionDetailAndNxRequestGroupId(String edfAckId, String activeYn, NxSolutionDetail solutionDetail, Long requestGroupId);

	List<NxRequestDetails> findByEdfAckIdAndActiveYnAndBulkReqYn(String edfAckId, String activeYn,String bulkReqYn);
	
	@Query(value="select * from NX_REQUEST_DETAILS nxr where nxr.NX_SOLUTION_ID = :nxSolutionId and nxr.ACTIVE_YN = 'Y'",nativeQuery = true) 
	List<NxRequestDetails> findByNxSolutionId( @Param("nxSolutionId")Long nxSolutionId);
	
	//@Query(value="select EDF_ACK_ID from NX_REQUEST_DETAILS nxr where nxr.NX_REQUEST_GROUP_ID in (:nxRequestGroupId) and nxr.NX_SOLUTION_ID = :nxSolutionId and nxr.ACTIVE_YN = 'Y' and nxr.STATUS in (90,100)",nativeQuery = true) 
	//List<String> findByNxRequestGroupIdAndNxSolutionId(@Param("nxRequestGroupId") List<Long> nxRequestGroupId, @Param("nxSolutionId")Long nxSolutionId);
	
	@Query(value="select nxRequestGroupId from NxRequestDetails where edfAckId = :edfAckId and activeYn = :activeYn")
	List<Long> findNxRequestGroupIdByEdfAckIdAndActiveYn(@Param("edfAckId") String edfAckId, @Param("activeYn") String activeYn);
	
	@Query(value="select * from NX_REQUEST_DETAILS nxr where nxr.EDF_ACK_ID = :edfAckId and nxr.ACTIVE_YN = :activeYn and rowNum = 1",nativeQuery = true) 
	NxRequestDetails findNxSolutionIdByEdfAckIdAndActiveYn(@Param("edfAckId") String edfAckId, @Param("activeYn") String activeYn);

	@Query(value="select distinct(product_cd) from NX_REQUEST_DETAILS where nx_solution_id = :nxSolutionId",nativeQuery = true) 
	List<String> findProductsByNxSolutionId(@Param("nxSolutionId")Long nxSolutionId);

	@Query(value="select * from NX_REQUEST_DETAILS nrd where nrd.nx_solution_id = :nxSolutionId and nrd.product_cd = :product and nrd.ACTIVE_YN = 'Y' ",nativeQuery = true) 
	List<NxRequestDetails> findRequestByNxSolutionIdAndProduct(@Param("nxSolutionId")Long nxSolutionId, @Param("product")String product);
	
	@Query("select data from NxRequestDetails data where data.nxReqId in (:nxReqId) and data.activeYn = :activeYn")
	List<NxRequestDetails> findByNxReqIdAndActiveYn(@Param("nxReqId") List<Long> nxReqId, @Param("activeYn") String activeYn);
	
	@Query(value="select nxReqId from NxRequestDetails n where n.nxSolutionDetail.nxSolutionId = :nxSolutionId")
	List<Long> findNxReqIdByNxSolutionId(@Param("nxSolutionId") Long nxSolutionId);
	
	@Query(value="select EDF_ACK_ID from NX_REQUEST_DETAILS nxr where nxr.NX_SOLUTION_ID = :nxSolutionId and nxr.ACTIVE_YN = 'Y' and nxr.STATUS in (90,100)",nativeQuery = true) 
	List<String> findEdfAckIdByNxSolutionId(@Param("nxSolutionId")Long nxSolutionId);
	
	@Query(value="select nxReqId from NxRequestDetails n where n.nxSolutionDetail.nxSolutionId = :nxSolutionId and product = :product and activeYn = 'Y'") 
	List<Long> findNxReqIdByNxSolutionIdAndProduct(@Param("nxSolutionId")Long nxSolutionId,  @Param("product")String product);
	
	@Query(value="select nx_Request_Group_Id from (select distinct nx_Request_Group_Id, flow_type from Nx_Request_Details where nx_Request_Group_Id in (:nxRequestGroupId) order by flow_Type desc)", nativeQuery=true)
	List<Object> sortNxreqGrpId(@Param("nxRequestGroupId") List<Long> nxRequestGroupId);

	@Transactional
	@Modifying
	@Query(value = "delete from NX_REQUEST_DETAILS nxr where nxr.NX_SOLUTION_ID = :nxSolutionId",nativeQuery = true)
	int deleteNxRequestDetails(@Param("nxSolutionId") Long nxSolutionId);
	
	@Query(value="select nxReqId from NxRequestDetails n where n.nxSolutionDetail.nxSolutionId = :nxSolutionId and product in (:product) and activeYn = 'Y' and flowType='USRP'") 
	List<Long> findUsrpNxReqIdByNxSolutionIdAndProduct(@Param("nxSolutionId")Long nxSolutionId,  @Param("product")List<String> product);


}
