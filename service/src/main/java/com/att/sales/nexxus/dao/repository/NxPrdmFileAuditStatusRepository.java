package com.att.sales.nexxus.dao.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.att.sales.prdm.dao.NxRatePlanFileAuditData;

/**
 * The Interface NxPrdmFileAuditStatusRepository.
 */
@Repository
public interface NxPrdmFileAuditStatusRepository extends JpaRepository<NxRatePlanFileAuditData, Long>{

	
	/**
	 * Find by file id.
	 *
	 * @param fileId the file id
	 * @return the nx rate plan file audit data
	 */
	public NxRatePlanFileAuditData findByFileId(@Param("fileId") BigDecimal fileId); 
	
}
