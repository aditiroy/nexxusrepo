package com.att.sales.nexxus.dao.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.dao.model.NxRequestGroup;

@Repository
@Transactional
public interface NxRequestGroupRepository extends JpaRepository<NxRequestGroup, Long>{
	
	NxRequestGroup findByNxRequestGroupIdAndActiveYn(Long nxRequestGroupId, String activeYn);
	
	@Query(value = "from NxRequestGroup where nxRequestGroupId in (:nxRequestGroupId) and activeYn = :activeYn")
	List<NxRequestGroup> findByNxRequestGroupIdAndActiveYn(@Param("nxRequestGroupId") List<Long> nxRequestGroupId, @Param("activeYn") String activeYn);
	
	List<NxRequestGroup> findByNxSolutionIdAndActiveYn(Long nxSolutionId, String activeYn);
	
	List<NxRequestGroup> findByNxSolutionIdAndGroupIdAndActiveYn(Long nxSolutionId, Long groupId, String activeYn);
	
	List<NxRequestGroup> findByNxSolutionIdAndStatusAndActiveYn(Long nxSolutionId, String status, String activeYn);

	@Query(value = "select lookup.DESCRIPTION as statusName, nx.NX_REQUEST_GROUP_ID as nxReqGrpId,nx.NX_SOLUTION_ID as nxSolutionId, "
			+ "nx.GROUP_ID as groupId, nx.STATUS as statusId, nx.DESCRIPTION as description from NX_REQUEST_GROUP nx join nx_lookup_data lookup on lookup.ITEM_ID = nx.STATUS"
			+ " where nx.NX_SOLUTION_ID = :nxSolutionId and lookup.DATASET_NAME = 'REQUEST_GROUP_STATUS' AND NX.ACTIVE_YN = :activeYn", 
			nativeQuery = true)
	List<Object[]> findByNxSolutionIdAndActive(@Param("nxSolutionId") Long nxSolutionId, @Param("activeYn") String activeYn);
	
	@Query(value = "select lookup.DESCRIPTION as statusName, nx.NX_REQUEST_GROUP_ID as nxReqGrpId,nx.NX_SOLUTION_ID as nxSolutionId, "
			+ "nx.GROUP_ID as groupId, nx.STATUS as statusId, nx.DESCRIPTION as description from NX_REQUEST_GROUP nx join nx_lookup_data lookup on lookup.ITEM_ID = nx.STATUS"
			+ " where nx.NX_SOLUTION_ID = :nxSolutionId and lookup.DATASET_NAME = 'REQUEST_GROUP_STATUS' AND nx.NX_REQUEST_GROUP_ID in (:nxRequestGroupId) AND NX.ACTIVE_YN = :activeYn", 
			nativeQuery = true)
	List<Object[]> findByNxSolutionIdAndNxRequestGroupIdAndActive(@Param("nxSolutionId") Long nxSolutionId, @Param("nxRequestGroupId") List<Long> nxRequestGroupId, @Param("activeYn") String activeYn);
	
	@Query(value = "select NX_REQUEST_GROUP_ID from NX_REQUEST_GROUP where NX_SOLUTION_ID = :nxSolutionId and ACTIVE_YN = :activeYn", nativeQuery = true)
	List<Object[]> findGroupIdByNxSolutionIdAndActiveYn(@Param("nxSolutionId") Long nxSolutionId, @Param("activeYn") String activeYn);
	
	@Query(value="select nxRequestGroupId from NxRequestGroup where nxSolutionId = :nxSolutionId and activeYn= :activeYn")
	List<Long> findNxGroupIdByNxSolutionIdAndActiveYn(@Param("nxSolutionId") Long nxSolutionId, @Param("activeYn") String activeYn);

	@Transactional
	@Modifying
	@Query(value = "delete from NX_REQUEST_GROUP nxr where nxr.NX_SOLUTION_ID = :nxSolutionId",nativeQuery = true)
	int deleteNxRequestGroup(@Param("nxSolutionId") Long nxSolutionId);


}

