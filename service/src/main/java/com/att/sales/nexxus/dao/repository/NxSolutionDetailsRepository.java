package com.att.sales.nexxus.dao.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;



/**
 * The Interface NxSolutionDetailsRepository.
 */
@Repository
@Transactional
public interface NxSolutionDetailsRepository extends JpaRepository<NxSolutionDetail, Long>{
	
	/**
	 * Find by nx solution id.
	 *
	 * @param nxSolutionId the nx solution id
	 * @return the list
	 */
	NxSolutionDetail findByNxSolutionId(@Param("nxSolutionId") long nxSolutionId);
	
	/**
	 * Find by external key.
	 *
	 * @param externalKey the external key
	 * @return the list
	 */
	List<NxSolutionDetail> findByExternalKey(Long externalKey);
	
	/**
	 * find solution id by external key
	 * 
	 * @param externalKey
	 * 
	 * @return nx solution id
	 */
	@Query(value= "select nxSolutionId from NxSolutionDetail where externalKey = :externalKey")
	Long findSolutionByExternalKey(@Param("externalKey") Long externalKey);
	
	@Query(value= "select standardPricingInd from NxSolutionDetail where externalKey = :externalKey")
	String findStandardPricingIndByExternalKey(@Param("externalKey") Long externalKey);
	
	@Query(value="select standardPricingInd from NxSolutionDetail where externalKey = :externalKey and flowType = :flowType")
	String findIpneIndExternalKey(@Param("externalKey") Long externalKey,@Param("flowType") String flowType);
	 
	
	List<NxSolutionDetail> findByNxSolutionIdAndActiveYn(@Param("nxSolutionId") long nxSolutionId, @Param("activeYn") String activeYn);
	
	@Query(value = "select nsd.* from nx_solution_details nsd, nx_mp_deal nmd where nmd.deal_id = :dealId and nsd.nx_solution_id = nmd.nx_solution_id and nsd.flow_type = 'AUTO' and nsd.external_key is not null and ROWNUM = 1", nativeQuery = true)
	NxSolutionDetail getSolutionByDealId(@Param("dealId") String dealId);
	
	@Query(value= "select nxSolutionId, automationFlowInd, createdUser ,flowType ,slcInd ,atxCall from NxSolutionDetail where externalKey = :externalKey")
	List<Object[]> findNxSolutionByExternalKey(@Param("externalKey") Long externalKey);
	
//	@Query(value= "select nsd.* from Nx_Solution_Details nsd where nsd.externalKey = :externalKey")
//	NxSolutionDetail findNxSolutionDetailsByExternalKey(@Param("externalKey") Long externalKey);
	
	@Transactional
	@Modifying
	@Query("update NxSolutionDetail set modifiedDate = :modifiedDate where nxSolutionId = :nxSolutionId")
	int updateModifiedDateBySolutionId(@Param("modifiedDate") Date modifiedDate, @Param("nxSolutionId") Long nxSolutionId);

	@Transactional
	@Modifying
	@Query("update NxSolutionDetail set customerName=:customerName where nxSolutionId = :nxSolutionId")
	int updateCustomerNameSolutionId(@Param("customerName")String customerName, @Param("nxSolutionId") Long nxSolutionId);
	
	@Transactional
	@Modifying
	@Query("update NxSolutionDetail set archivedSolInd=:archivedSolInd where nxSolutionId = :nxSolutionId")
	int updateArchivedSolutionId(@Param("archivedSolInd")String archivedSolInd, @Param("nxSolutionId") Long nxSolutionId);

	@Transactional
	@Modifying
	@Query("update NxSolutionDetail set pdStatusInd = :pdStatusInd, modifiedDate = :modifiedDate where nxSolutionId = :nxSolutionId")
	int updatePdStatusIndBySolutionId(@Param("pdStatusInd") String pdStatusInd, @Param("modifiedDate") Date modifiedDate, @Param("nxSolutionId") Long nxSolutionId);

	@Transactional
	@Modifying
	@Query("update NxSolutionDetail set isLocked = :isLocked, lockedByUser = :lockedByUser where nxSolutionId = :nxSolutionId")
	int updateLockStatusIndBySolutionId(@Param("isLocked") String isLocked, @Param("lockedByUser") String lockedByUser, @Param("nxSolutionId") Long nxSolutionId);
	
	@Transactional
	@Modifying
	@Query("update NxSolutionDetail set isLocked = :isLocked where nxSolutionId IN (:nxSolutionIds)")
	int updateLockStatusIndBySolutionIdIn(@Param("isLocked") String isLocked, @Param("nxSolutionIds") List<Long> nxSolutionIds);

	@Transactional
	@Modifying
	@Query("update NxSolutionDetail set slcInd = :slcIndicator where externalKey = :externalKey") 
	int updateSlcIndBySolutionId(@Param("slcIndicator") String slcIndicator, @Param("externalKey") Long externalKey);
	
	
	@Transactional
	@Modifying
	@Query("update NxSolutionDetail set atxCall = :autoCall  where externalKey = :externalKey") 
	int updateAutoCallBySolutionId(@Param("autoCall") String slcIndicator, @Param("externalKey") Long externalKey);
	 
    @Transactional
	@Modifying
	@Query("delete from NxSolutionDetail where nxSolutionId IN (:nxSolutionId)")
	int deleteNxSolutions(@Param("nxSolutionId") Long nxSolutionId);
}


