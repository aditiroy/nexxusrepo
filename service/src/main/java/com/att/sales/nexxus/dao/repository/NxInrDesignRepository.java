package com.att.sales.nexxus.dao.repository;

import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.dao.model.NxInrDesign;

@Repository
@Transactional
public interface NxInrDesignRepository extends JpaRepository<NxInrDesign, Long>{
	
	NxInrDesign findByNxSolutionIdAndCircuitIdAndActiveYN(Long nxSolutionId, String circuitId, String activeYN);
	
	List<NxInrDesign> findByNxSolutionIdAndStatusAndActiveYN(Long nxSolutionId, String status, String activeYN);
	
	List<NxInrDesign> findByNxSolutionId(Long nxSolutionId);
	
	@Modifying(clearAutomatically = true)
	@Query(value="update NX_INR_DESIGN set STATUS = :successStatus where STATUS = :updateStatus and NX_SOLUTION_ID = :nxSolutionId",nativeQuery=true)
	void updateStatusByNxSolutionId(@Param("successStatus") String status,@Param("updateStatus") String updateStatus,@Param("nxSolutionId") Long nxSolutionId);
	
	@Query(value = "select nd from NxInrDesign nd JOIN nd.nxInrDesignDetails ndd where nd.nxSolutionId = :nxSolutionId and ndd.status = :status and nd.activeYN = :activeInd")
	public List<NxInrDesign> findStatusByNxDesignDetails(@Param("nxSolutionId") long nxSolutionId, @Param("status") String status, @Param("activeInd") String activeInd);
	
	@Query(value = "select distinct nd.NX_INR_DESIGN_ID, nd.CIRCUIT_ID from Nx_Inr_Design nd join nx_inr_design_details ndd on ndd.NX_INR_DESIGN_ID = nd.NX_INR_DESIGN_ID"
			+ " and ndd.active_yn ='Y' where nd.NX_SOLUTION_ID = :nxSolutionId and nd.status in (:status) and nd.ACTIVE_YN = :activeInd and ndd.nx_req_id in (:nxReqId)", nativeQuery = true)
	public List<Object[]> findDesignByNxSolutionId(@Param("nxSolutionId") long nxSolutionId, @Param("status") List<String> status, 
			@Param("activeInd") String activeInd, @Param("nxReqId") List<Long> nxReqId);
	
	@Query(value = "from NxInrDesign where nxSolutionId = :nxSolutionId and status in (:status) and activeYN = :activeInd")
	public List<NxInrDesign> findDesignByNxSolutionIdAndStatusAndActiveYN(@Param("nxSolutionId") long nxSolutionId, @Param("status") List<String> status, @Param("activeInd") String activeInd);
	
	NxInrDesign findByNxSolutionIdAndNxInrDesignIdAndActiveYN(Long nxSolutionId, Long nxInrDesignId, String activeYN);
	
	@Query(value = "select nid.circuit_id, nid.status as solution_status, nidd.nx_req_id, nidd.status as design_status from nx_inr_design nid, "
			+ "nx_inr_design_details nidd where nidd.nx_inr_design_id = nid.nx_inr_design_id and (nid.status = 'F' or nidd.status = 'F') "
			+ "and nid.ACTIVE_YN = 'Y' AND nidd.ACTIVE_YN = 'Y' and nid.nx_solution_id = :nxSolutionId and nidd.nx_req_id in (:nxReqIds)", nativeQuery = true)
	public List<Object[]> findCktByNxSolutionId(@Param("nxSolutionId") long nxSolutionId, @Param("nxReqIds") List<Long> nxReqIds);
	
	@Query(value = "from NxInrDesign where nxSolutionId = :nxSolutionId and circuitId = :circuitId and activeYN = :activeYN and nxRequestGroupId in (:nxRequestGroupId)")
	NxInrDesign findByNxSolutionIdAndCircuitIdAndActiveYNAndNxRequestGroupId(@Param("nxSolutionId") Long nxSolutionId, @Param("circuitId") String circuitId, 
			@Param("activeYN") String activeYN, @Param("nxRequestGroupId") Set<Long> nxRequestGroupId);
	
	@Query(value = "FROM NxInrDesign nd left JOIN nd.nxInrDesignDetails ndd with ndd.nxCountThreshold is null and ndd.activeYN ='Y'"
			+ " WHERE nd.nxSolutionId = :nxsolutionid AND nd.activeYN = 'Y' and nd.circuitId = :circuitId")
	NxInrDesign findDesignsByNxSolutionIdAndCircuitId(@Param("nxsolutionid") Long nxSolutionId, @Param("circuitId") String circuitId);
	
	@Query(value = "Select case when exists(SELECT * FROM nx_inr_design nid join nx_inr_design_details nidd on nidd.nx_inr_design_id = nid.nx_inr_design_id WHERE nidd.status ='RF' and nid.active_yn = 'Y' AND nidd.active_yn = 'Y' AND nid.nx_solution_id = :nxsolutionid) then 'false' else 'true' end from dual", nativeQuery = true)
	public String findStatusByFailed(@Param("nxsolutionid") Long nxSolutionId); 

}
