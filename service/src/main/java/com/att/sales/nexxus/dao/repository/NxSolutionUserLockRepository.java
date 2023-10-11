package com.att.sales.nexxus.dao.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.att.sales.nexxus.dao.model.NxUserLockDetails;

@Repository
@Transactional
public interface NxSolutionUserLockRepository extends JpaRepository<NxUserLockDetails, String> {

	/**
	 * Find by nx solution id.
	 *
	 * @param nxSolutionId the nx solution id
	 * @return the list
	 */
	List<NxUserLockDetails> findByNxSolutionId(@Param("nxSolutionId") long nxSolutionId);

	@Query(value = "select * from NX_USER_LOCK_DETAILS WHERE MODIFIED_DATE < :modifiedDate and IS_LOCKED = :isLocked ",nativeQuery=true)
	List<NxUserLockDetails> findByModifiedDateAndIsLocked(@Param("modifiedDate") Date modifiedDate,
			@Param("isLocked") String isLocked);

	List<NxUserLockDetails> findByNxSolutionIdAndIsLocked(@Param("nxSolutionId") long nxSolutionId,
			@Param("isLocked") String isLocked);

	@Transactional
	@Modifying
	@Query("update NxUserLockDetails set isLocked = :isLocked, modifiedDate = :modifiedDate where nxSolutionId = :nxSolutionId and isLocked='Y'")
	int updateLockStatusIndBySolutionId(@Param("isLocked") String isLocked, @Param("modifiedDate") Date modifiedDate,
			@Param("nxSolutionId") Long nxSolutionId);

	@Transactional
	@Modifying
	@Query("update NxUserLockDetails set isLocked = :isLocked, modifiedDate = :modifiedDate where isLocked='Y' and nxSolutionId IN (:nxSolutionIds)")
	int updateLockStatusIndBySolutionIds(@Param("isLocked") String isLocked, @Param("modifiedDate") Date modifiedDate,
			@Param("nxSolutionIds") List<Long> nxSolutionIds);
}
