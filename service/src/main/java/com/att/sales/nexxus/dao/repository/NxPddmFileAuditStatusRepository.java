package com.att.sales.nexxus.dao.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.att.sales.pddm.dao.NxUDFDetailsFileAuditStatus;

/**
 * The Interface NxPddmFileAuditStatusRepository.
 */
@Repository
public interface NxPddmFileAuditStatusRepository extends JpaRepository<NxUDFDetailsFileAuditStatus, Long> {
	
	/**
	 * Find by file id.
	 *
	 * @param fileId the file id
	 * @return the nx UDF details file audit status
	 */
	public NxUDFDetailsFileAuditStatus findByFileId(@Param("fileId") BigDecimal fileId); 

}
