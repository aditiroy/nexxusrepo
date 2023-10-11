package com.att.sales.nexxus.dao.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.att.sales.nexxus.dao.model.NxDesignAudit;

/**
 * The Interface NxDesignAuditRepository.
 */
public interface NxDesignAuditRepository extends JpaRepository<NxDesignAudit, Long>{
	
	public NxDesignAudit findByNxRefIdAndTransaction(@Param("nxRefId")Long nxRefId, @Param("transaction")String transaction);
	
	public NxDesignAudit findTopByNxRefIdOrderByNxAuditIdDesc(@Param("nxRefId")Long nxRefId);
	
	public List<NxDesignAudit> findByNxRefIdAndTransactionAndStatus(@Param("nxRefId")Long nxRefId, @Param("transaction")String transaction, @Param("status")String status);
	
	public NxDesignAudit findByNxRefIdAndNxSubRefId(@Param("nxRefId")Long nxRefId, @Param("nxSubRefId")String nxSubRefId);
	
	public NxDesignAudit findByNxRefIdAndNxSubRefIdAndTransaction(@Param("nxRefId")Long nxRefId, @Param("nxSubRefId")String nxSubRefId,@Param("transaction")String transaction);

	public List<NxDesignAudit> findByNxRefId(@Param("nxRefId")Long nxRefId);
	
	@Transactional
	@Modifying
	@Query("update NxDesignAudit set status = :status, transaction = :transaction, modifedDate = :modifiedDate where nxAuditId = :nxAuditId")
	int updateStatusByNxAuditId(@Param("status") String status, @Param("transaction") String transaction, @Param("modifiedDate") Date modifiedDate, @Param("nxAuditId") Long nxAuditId);

	public List<NxDesignAudit> findFailedTokensByTransactionAndNxRefId(@Param("transaction")String transaction, @Param("nxRefId")Long nxRefId);

	@Query(value = "select count(*) from NX_DESIGN_AUDIT where NX_REF_ID=:nxSolutionId and TRANSACTION='Ethernet Token Bulkupload'", nativeQuery = true)
	int noOfFaildtokensByNxSolutionId(@Param("nxSolutionId")Long nxSolutionId);
	
	public NxDesignAudit findByTransactionAndStatusAndNxRefId(@Param("transaction")String transaction, @Param("status")String status, @Param("nxRefId")Long nxRefId);
	
	public List<NxDesignAudit> findByTransactionAndNxRefId(@Param("transaction")String transaction, @Param("nxRefId")Long nxRefId);
	
	@Query(value = "from NxDesignAudit where nxRefId in(:nxRefId) and transaction in (:transaction)")
	public List<NxDesignAudit> findByNxRefIdAndTransactions(@Param("nxRefId")List<Long> nxRefId, @Param("transaction") List<String> transaction);
	
	public List<NxDesignAudit> findByNxSubRefIdAndTransaction(@Param("nxSubRefId") String nxSubRefId, @Param("transaction") String transaction);
	
	@Query(value = "from NxDesignAudit where status is not null and nxSubRefId in (:nxSubRefId) and transaction = :transaction")
	public List<NxDesignAudit> findByNxSubRefIdAndTransaction(@Param("nxSubRefId") List<String> nxSubRefId, @Param("transaction") String transaction);

	@Query(value = "select * from Nx_Design_Audit where NX_REF_ID = :nxRefId and status is not null and transaction ='QUALIFIED_CIRCUITS'",nativeQuery = true)
	public List<NxDesignAudit> findByNxRefIdAndStausNotNull(@Param("nxRefId")long nxRefId);
	
	@Query(value = "select distinct nxSubRefId from NxDesignAudit where data is not null and transaction = 'DCC_LEGACY_FILE' and status = 'SUCCESS'")
	public List<String> findDccLodedFiles();
	
	@Transactional
	@Modifying
	@Query(value = "update NxDesignAudit set data = null where data is not null and transaction = 'DCC_LEGACY_FILE' and status = 'SUCCESS'")
	public int resetDccLodedFiles();

	public NxDesignAudit findByNxAuditId(Long nxAuditId);
}

