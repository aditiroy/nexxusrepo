package com.att.sales.nexxus.dao.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.dao.model.NxDesign;
import com.att.sales.nexxus.dao.model.NxDesignDetails;

@Repository
@Transactional
public interface NxDesignDetailsRepository extends JpaRepository<NxDesignDetails, Long> {

	List<NxDesignDetails> findByNxDesign(@Param("nxDesign") NxDesign nxDesign);
	
	@Query(value = "select designDetails from NxDesignDetails designDetails where designDetails.nxDesign.nxDesignId IN (select design.nxDesignId from NxDesign design where design.nxSolutionDetail.nxSolutionId = :nxSolutionId)")
	List<NxDesignDetails> findDesignDetailsaByNxSolutionId(@Param("nxSolutionId") Long nxSolutionId);
	
	@Query(value = "select designDetails from NxDesignDetails designDetails where designDetails.nxDesign.nxDesignId IN :nxDesignIds")
	List<NxDesignDetails> findByNxDesignIdIn(@Param("nxDesignIds") List<Long> nxDesignIds);
	
	@Query(value="select  distinct ndd.CONSOLIDATION_CRITERIA  from nx_design nd join NX_DESIGN_DETAILS ndd on nd.nx_design_id= ndd.nx_design_id "
			+ "where nd.nx_solution_id=:nxSolutionId", nativeQuery = true)
	public List<String> findConsolidationCriteriaByNxSolutionId(@Param("nxSolutionId")long nxSolutionId);

	@Query(value="select  distinct ndd.CONSOLIDATION_CRITERIA,ndd.TYPE,nd.BUNDLE_CODE from nx_design nd join NX_DESIGN_DETAILS ndd on nd.nx_design_id= ndd.nx_design_id "
			+ "where nd.nx_solution_id=:nxSolutionId and ndd.CONSOLIDATION_CRITERIA is not null", nativeQuery = true) 
	public List<Object[]> findConsolidationCriteriaByNxSolutionIdAndType(@Param("nxSolutionId")long nxSolutionId);

	@Query(value="select  distinct ndd.CONSOLIDATION_CRITERIA  from nx_design nd join NX_DESIGN_DETAILS ndd on nd.nx_design_id= ndd.nx_design_id "
			+ "and nd.nx_solution_id=:nxSolutionId and nd.asr_item_id in(:asrItemId)", nativeQuery = true)
	public List<String> findConsolidationCriteriaByAsrItemId(@Param("nxSolutionId")long nxSolutionId,@Param("asrItemId") List<String> asrItemId);

	@Query(value="select designDetails from NxDesignDetails designDetails where designDetails.nxDesign.nxDesignId IN (select design.nxDesignId from NxDesign design where design.nxSolutionDetail.nxSolutionId = :nxSolutionId) "
			+ "and consolidation_criteria=:consolidationCriteria ")
	public List<NxDesignDetails> findDesignDetailsByConsolidationCriteria(@Param("nxSolutionId")long nxSolutionId,@Param("consolidationCriteria") String consolidationCriteria);
	
	@Query(value="select designDetails from NxDesignDetails designDetails where designDetails.nxDesign.nxDesignId IN (select design.nxDesignId from NxDesign design where design.nxSolutionDetail.nxSolutionId = :nxSolutionId and design.bundleCd = :bundleCd ) "
			+ "and consolidation_criteria=:consolidationCriteria  and type=:type")
	public List<NxDesignDetails> findDesignDetailsByConsolidationCriteriaAndTypeAndBundleCd(@Param("nxSolutionId")long nxSolutionId,@Param("consolidationCriteria") String consolidationCriteria,@Param("type") String type,@Param("bundleCd") String bundleCd);
	
	@Query(value="select  distinct ndd.CONSOLIDATION_CRITERIA,ndd.TYPE,nd.BUNDLE_CODE from nx_design nd join NX_DESIGN_DETAILS ndd on nd.nx_design_id= ndd.nx_design_id "
			+ "where nd.nx_solution_id=:nxSolutionId and nd.asr_item_id in :asrItemId", nativeQuery = true)
	public List<Object[]> findConsolidationCriteriaByNxSolutionIdAndAsr(@Param("nxSolutionId")long nxSolutionId, @Param("asrItemId") List<Object> asrItemId);

	@Query(value="select  distinct ndd.CONSOLIDATION_CRITERIA,ndd.TYPE,nd.BUNDLE_CODE from nx_design nd join NX_DESIGN_DETAILS ndd on nd.nx_design_id= ndd.nx_design_id "
			+ "where nd.nx_solution_id=:nxSolutionId and ndd.CONSOLIDATION_CRITERIA is not null and nd.site_id in(:siteId)", nativeQuery = true) 
	public List<Object[]> findConsolidationCriteriaByNxSolutionIdAndSiteId(@Param("nxSolutionId")long nxSolutionId,@Param("siteId") List<Long> siteId);
}
