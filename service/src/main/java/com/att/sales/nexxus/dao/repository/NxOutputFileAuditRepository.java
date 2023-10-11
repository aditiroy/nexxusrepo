package com.att.sales.nexxus.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.dao.model.NxOutputFileAuditModel;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;

/**
 * The Interface NxOutputFileAuditRepository.
 */
@Repository
public interface NxOutputFileAuditRepository extends JpaRepository<NxOutputFileAuditModel, Long> {
	
	/**
	 * Find by nx output file id and nx solution detail.
	 *
	 * @param nxOutputFileId the nx output file id
	 * @param nxSolutionDetail the nx solution detail
	 * @return the list
	 */
	List<NxOutputFileAuditModel> findByNxOutputFileIdAndNxSolutionDetail(@Param("nxOutputFileId") Long nxOutputFileId, @Param("nxSolutionDetail") NxSolutionDetail nxSolutionDetail);
	
}
