package com.att.sales.nexxus.dao.repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.dao.model.NxDesign;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.nxPEDstatus.model.SolutionDetails;

/**
 * The Interface NxDesignRepository.
 */
@Repository
@Transactional
public interface NxDesignRepository extends JpaRepository<NxDesign, Long> {

	/**
	 * Gets the design by asr item id.
	 *
	 * @param asrItemId the asr item id
	 * @return the design by asr item id
	 */
	@Query(value = "from NxDesign nd where nd.asrItemId = :asrItemId")
	public List<NxDesign> getDesignByAsrItemId(@Param("asrItemId") String asrItemId);


	@Query(value = "select designStatus from NxDesignDetails where nxDesign.nxDesignId in :nxDesignIds")
	public List<SolutionDetails> findDesignStatusByDesignId(@Param("nxDesignIds") List<Long> nxDesignIds);
	
	@Query(value = "select nxDesignId, bundleCd from NxDesign where nxSolutionDetail.nxSolutionId = :nxSolutionId")
	public List<Object[]> findDesignIdsNbundleCdByNxSolutionId(@Param("nxSolutionId") long nxSolutionId);
		
	@Query(value = "select nd from NxDesign nd where nd.nxSolutionDetail.nxSolutionId = :nxSolutionId and nd.status <> 'D' and nd.asrItemId <> :asrItemId")
	public List<NxDesign> findByNxSolutionIdAsr(@Param("nxSolutionId") long nxSolutionId, @Param("asrItemId") String asrItemId);
	
	public NxDesign findByNxDesignId(@Param("nxDesignId") Long nxDesignId);

	@Query(value="SELECT np FROM NxDesign np WHERE np.nxDesignId IN (:nxDesignId)")
	List<NxDesign> findByMultipleNxDesignIds(@Param("nxDesignId") Set<Long> nxDesignId);
	
	
	@Query(value = "select nxDesignId from NxDesign nd where nd.asrItemId = :asrItemId and nxSolutionDetail.nxSolutionId = :nxSolutionId")
	public Long findByAsrItemIdAndNxSolutionId(@Param("asrItemId") String asrItemId, @Param("nxSolutionId") long nxSolutionId);

	List<NxDesign> findByNxSolutionDetail(@Param("nxSolutionDetail") NxSolutionDetail nxSolutionDetail);
	
	NxDesign findByAsrItemIdAndNxSolutionDetail(@Param("asrItemId") String asrItemId, @Param("nxSolutionDetail") NxSolutionDetail nxSolutionDetail);
	
	NxDesign findByAsrItemIdAndNxSolutionDetailAndBundleCd(@Param("asrItemId") String asrItemId, @Param("nxSolutionDetail") NxSolutionDetail nxSolutionDetail,@Param("bundleCd") String bundleCd );
	
	NxDesign findBySiteIdAndNxSolutionDetailAndBundleCdAndCircuitId(@Param("siteId") Long siteId, @Param("nxSolutionDetail") NxSolutionDetail nxSolutionDetail, @Param("bundleCd") String bundleCd,@Param("circuitId") String circuitId);
	
	NxDesign findBySiteIdAndNxSolutionDetail(@Param("siteId") Long siteId, @Param("nxSolutionDetail") NxSolutionDetail nxSolutionDetail);
	
	@Query(value = "select nd from NxDesign nd where nxSolutionDetail.nxSolutionId = :nxSolutionId and nd.asrItemId in :asrItemId")
	List<NxDesign> findByNxSolutionIdAndAsrItemId(@Param("nxSolutionId") long nxSolutionId, @Param("asrItemId") List<Object> asrItemIds);
	
	@Query(value = "select nd.nxDesignId from NxDesign nd where nxSolutionDetail.nxSolutionId = :nxSolutionId and nd.asrItemId in :asrItemId")
	List<Long> findByNxSolutionIdAndMultipleAsrItemId(@Param("nxSolutionId") long nxSolutionId, @Param("asrItemId") List<String> asrItemIds);

	@Query(value = "select count(*) from NxDesign nd where nxSolutionDetail.nxSolutionId = :nxSolutionId")
	public int numberOfAsrForSolution(@Param("nxSolutionId")long nxSolutionId);
	
	@Query(value = "select asrItemId from NxDesign nd where nxSolutionDetail.nxSolutionId = :nxSolutionId and nd.status=:status")
	public List<String> fetchAsrCompletedForSolution(@Param("nxSolutionId")long nxSolutionId,@Param("status") String status);
	
	@Query(value = "select distinct dd.MP_SOLUTION_ID,nd.BUNDLE_CODE  from nx_design nd join nx_mp_design_document dd on dd.nx_design_id = nd.nx_design_id and dd.active_yn = 'Y'"
			+ " where nd.nx_solution_id = :nxSolutionId and nd.asr_item_id in :asrItemId and dd.nx_txn_id = :nxTxnId", nativeQuery = true)
	public List<Object[]> fetchMpsolutionIdForSolution(@Param("nxSolutionId") long nxSolutionId, @Param("asrItemId") List<Object> asrItemId, @Param("nxTxnId") long nxTxnId);
	
	@Query(value = "select distinct dd.MP_SOLUTION_ID,nd.BUNDLE_CODE from nx_design nd join nx_mp_design_document dd on dd.nx_design_id = nd.nx_design_id and dd.active_yn = 'Y'"
			+ " where nd.nx_solution_id = :nxSolutionId and nd.site_Id in :siteId and dd.nx_txn_id = :nxTxnId", nativeQuery = true)
	public List<Object[]> fetchMpsolutionIdForSolutionBySiteId(@Param("nxSolutionId") long nxSolutionId, @Param("siteId") List<Long> siteId, @Param("nxTxnId") long nxTxnId);
	
	@Query(value = "select nd from NxDesign nd join nd.nxDesignDetails ndd where nd.nxSolutionDetail.nxSolutionId = :nxSolutionId and nd.status not in :status "
			+ "and ndd.consolidationCriteria = :consolidationCriteria")
	public List<NxDesign> fetchNxDesignForSolution(@Param("nxSolutionId") long nxSolutionId, @Param("status") List<String> status,
			@Param("consolidationCriteria") String consolidationCriteria);

	@Query(value = "select nd from NxDesign nd join nd.nxDesignDetails ndd where nd.nxSolutionDetail.nxSolutionId = :nxSolutionId and nd.status not in :status "
			+ "and ndd.consolidationCriteria = :consolidationCriteria and ndd.type = :type and nd.bundleCd = :bundleCd ")
	public List<NxDesign> fetchNxDesignForSolutionByTypeAndBundleCd(@Param("nxSolutionId") long nxSolutionId, @Param("status") List<String> status,
			@Param("consolidationCriteria") String consolidationCriteria, @Param("type") String type,@Param("bundleCd") String bundleCd);
	
	@Query(value = "select distinct dd.MP_SOLUTION_ID,nd.BUNDLE_CODE,nd.nx_design_id  from nx_design nd join nx_mp_design_document dd on dd.nx_design_id = nd.nx_design_id and dd.active_yn = 'Y'"
			+ " where nd.nx_solution_id = :nxSolutionId and nd.asr_item_id not in :asrItemId and dd.nx_txn_id = :nxTxnId and nd.bundle_code = :bundleCd and nd.submit_to_mp = :submitToMp ", nativeQuery = true)
	public List<Object[]> fetchMpsolutionIdByAsrAndProduct(@Param("nxSolutionId") long nxSolutionId, @Param("asrItemId") List<Object> asrItemId, @Param("nxTxnId") long nxTxnId,@Param("bundleCd") String bundleCd,@Param("submitToMp") String submitToMp);

	@Query(value = "select distinct dd.MP_SOLUTION_ID,nd.BUNDLE_CODE,nd.nx_design_id from nx_design nd join nx_mp_design_document dd on dd.nx_design_id = nd.nx_design_id and dd.active_yn = 'Y'"
			+ " where nd.nx_solution_id = :nxSolutionId and nd.nx_design_Id not in :nxDesignId and dd.nx_txn_id = :nxTxnId and nd.bundle_code in :bundleCd ", nativeQuery = true)
	public List<Object[]> fetchMpsolutionIdByDesignId(@Param("nxSolutionId") long nxSolutionId, @Param("nxDesignId") List<Long> nxDesignId, @Param("nxTxnId") long nxTxnId, @Param("bundleCd") List<String> bundleCd);
	
	
	@Query(value = "select distinct nd.nx_design_id from nx_design nd  where nd.nx_solution_id = :nxSolutionId and nd.site_Id  in :siteId  and nd.bundle_code in :bundleCd and nd.circuit_td in :circuitId", nativeQuery = true)
	public List<Long> fetchDesignIdBySiteIdAndProductAndCircuitId(@Param("nxSolutionId") long nxSolutionId, @Param("siteId") List<Long> siteId, @Param("bundleCd") List<String> bundleCd ,@Param("circuitId") List<String> circuitId);
	
	@Query(value = "select distinct dd.MP_SOLUTION_ID,nd.BUNDLE_CODE,nd.nx_design_id from nx_design nd join nx_mp_design_document dd on dd.nx_design_id = nd.nx_design_id and dd.active_yn = 'Y'"
			+ " where nd.nx_solution_id = :nxSolutionId  and dd.nx_txn_id = :nxTxnId and nd.bundle_code in :bundleCd ", nativeQuery = true)
	public List<Object[]> fetchMpsolutionIdBySolutionId(@Param("nxSolutionId") long nxSolutionId, @Param("nxTxnId") long nxTxnId, @Param("bundleCd") List<String> bundleCd);
	
	@Query(value = "select distinct dd.MP_SOLUTION_ID,nd.BUNDLE_CODE,nd.nx_design_id  from nx_design nd join nx_mp_design_document dd on dd.nx_design_id = nd.nx_design_id and dd.active_yn = 'Y'"
			+ " where nd.nx_solution_id = :nxSolutionId and dd.nx_txn_id = :nxTxnId and nd.bundle_code = :bundleCd and nd.submit_to_mp = :submitToMp ", nativeQuery = true)
	public List<Object[]> fetchMpsolutionIdBySolutionIdAndSubmitToMp(@Param("nxSolutionId") long nxSolutionId, @Param("nxTxnId") long nxTxnId,@Param("bundleCd") String bundleCd,@Param("submitToMp") String submitToMp);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "update NxDesign set status =:status,modified_date=:modifiedDate " + "where nx_solution_id = :nxSolutionId and nx_design_id in :nxDesignId")
	int updateDesignBySolIdAndDesignId(@Param("status")String status,@Param("modifiedDate") Date modifiedDate,
			@Param("nxSolutionId") Long nxSolutionId,@Param("nxDesignId") List<String> nxDesignId);
	
}
