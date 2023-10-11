package com.att.sales.nexxus.dao.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.model.solution.NxUiAudit;

@Repository
@Transactional
public interface NxUiAuditRepository extends JpaRepository<NxUiAudit, Long>{
	
	/**
	 * Find by nx solution id.
	 *
	 * @param nxSolutionId the nx solution id
	 * @return the list
	 */
	@Query(value = "from NxUiAudit where nxSolutionId = :nxSolutionId  and actionType= :actionType order by createdDate desc")
	List<NxUiAudit> findByNxSolutionIdandActionType(@Param("nxSolutionId") long nxSolutionId, @Param("actionType") String actionType);
}
