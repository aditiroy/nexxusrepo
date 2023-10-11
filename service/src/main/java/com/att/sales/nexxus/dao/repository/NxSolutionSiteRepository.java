package com.att.sales.nexxus.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.att.sales.nexxus.dao.model.NxSolutionSite;

public interface NxSolutionSiteRepository extends JpaRepository<NxSolutionSite, Long>{
	
	@Query(value = "select s.* from nx_solution_site s " + 
			"join nx_request_details n on n.nx_req_id = s.nx_req_id " + 
			"where s.nx_solution_id=:nxSolutionId and n.nx_solution_id=:nxSolutionId " + 
			"and s.nx_request_group_id =:nxRequestGroupId and s.active_yn=:activeYN order by n.flow_type desc", nativeQuery=true)
	List<NxSolutionSite> findByNxSolutionIdAndNxRequestGroupIdAndActiveYN(@Param("nxSolutionId") Long nxSolutionId, @Param("nxRequestGroupId") Long nxRequestGroupId, @Param("activeYN") String activeYN);
	
	NxSolutionSite findByNxSolutionIdAndActiveYN(Long nxSolutionId, String activeYN);
	
	NxSolutionSite findByNxSolutionIdAndNxRequestGroupIdAndActiveYNAndNxReqId(Long nxSolutionId, Long nxRequestGroupId, String activeYN, Long nxReqId);
}
