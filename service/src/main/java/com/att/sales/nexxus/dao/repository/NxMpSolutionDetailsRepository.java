package com.att.sales.nexxus.dao.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.dao.model.NxMpSolutionDetails;

/**
 * @author KumariMuktta
 *
 */
@Repository
@Transactional
public interface NxMpSolutionDetailsRepository extends JpaRepository<NxMpSolutionDetails, Long> {
	@Query("select data from NxMpSolutionDetails data where data.activeYN = 'Y' and data.nxTxnId =:nxTxnId")
	NxMpSolutionDetails findByNxTxnId(@Param("nxTxnId") Long nxTxnId);

}
