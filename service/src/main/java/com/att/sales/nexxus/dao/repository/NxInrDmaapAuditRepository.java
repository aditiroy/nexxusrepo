package com.att.sales.nexxus.dao.repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.dao.model.NxInrDmaapAudit;

@Repository
@Transactional
public interface NxInrDmaapAuditRepository extends JpaRepository<NxInrDmaapAudit, Long> {

	@Query(value = "SELECT * FROM NX_INR_DMAAP_AUDIT WHERE NX_PROCESS_STATUS = :processStatus AND NX_TRANSACTION_TYPE = :transactionType ORDER BY NX_DMAAP_AUDIT_ID ASC", nativeQuery = true)
	List<NxInrDmaapAudit> getNewDmaapDetails(@Param("processStatus") String processStatus, @Param("transactionType") String transactionType);
	
	@Modifying(clearAutomatically = true)
	@Query(value="UPDATE NX_INR_DMAAP_AUDIT SET NX_PROCESS_STATUS = :processStatus, MODIFIED_TIME = SYSDATE WHERE NX_DMAAP_AUDIT_ID = :auditId", nativeQuery=true)
	int updateDmaapProcessStatus(@Param("processStatus") String processStatus, @Param("auditId") Long auditId);
	
	@Query(value = "select * from nx_inr_dmaap_audit where Nx_Solution_Id =:nxSolutionId and NX_PROCESS_STATUS in (:status) and NX_TRANSACTION_TYPE =:transactionType and NX_POD_NAME is not null and rownum =1", nativeQuery=true)
	NxInrDmaapAudit findByNxSolutionIdAndStatusAndTransactionType(@Param("nxSolutionId") Long nxSolutionId, @Param("status") List<String> status, @Param("transactionType") String transactionType);
	
	@Query(value = "SELECT * FROM NX_INR_DMAAP_AUDIT WHERE NX_PROCESS_STATUS = :processStatus AND NX_TRANSACTION_TYPE = :transactionType and NX_POD_NAME = :nxPodName and rownum < 3 ORDER BY NX_DMAAP_AUDIT_ID ASC", nativeQuery = true)
	List<NxInrDmaapAudit> getNewDmaapDetailsByPodName(@Param("processStatus") String processStatus, @Param("transactionType") String transactionType, @Param("nxPodName") String nxPodName);
	
	@Modifying(clearAutomatically = true)
	@Query(value="UPDATE NX_INR_DMAAP_AUDIT SET NX_POD_NAME = NULL, MODIFIED_TIME = SYSDATE WHERE NX_POD_NAME = :podName and NX_PROCESS_STATUS ='N'", nativeQuery=true)
	int updatePodName(@Param("podName") String podName);
	
	@Modifying(clearAutomatically = true)
	@Query(value="UPDATE NX_INR_DMAAP_AUDIT SET NX_POD_NAME = :podName, MODIFIED_TIME = SYSDATE WHERE NX_POD_NAME is null and NX_PROCESS_STATUS ='N'", nativeQuery=true)
	int updatePodNames(@Param("podName") String podName);
	
	@Query(value = "SELECT * FROM NX_INR_DMAAP_AUDIT WHERE NX_TRANSACTION_TYPE = :transactionType and NX_POD_NAME is null and NX_PROCESS_STATUS in ('N') ORDER BY NX_DMAAP_AUDIT_ID ASC", nativeQuery = true)
	public List<NxInrDmaapAudit> findByTransactionType(@Param("transactionType") String transactionType);
	
	@Query(value = "SELECT * FROM NX_INR_DMAAP_AUDIT WHERE NX_TRANSACTION_TYPE = :transactionType and NX_PROCESS_STATUS= :processStatus and MODIFIED_TIME < :dateThreshold ORDER BY NX_DMAAP_AUDIT_ID ASC", nativeQuery = true)
	public List<NxInrDmaapAudit> findByTransactionTypeAndProcessStatus(@Param("transactionType") String transactionType, @Param("processStatus") String processStatus, @Param("dateThreshold") Date dateThreshold);
	
	@Query(value = "SELECT * FROM NX_INR_DMAAP_AUDIT WHERE NX_TRANSACTION_TYPE = :transactionType and NX_PROCESS_STATUS in (:processStatus) and MODIFIED_TIME < :dateThreshold and NX_POD_NAME NOT IN (:podName) ORDER BY NX_DMAAP_AUDIT_ID ASC", nativeQuery = true)
	public List<NxInrDmaapAudit> findByTransactionTypeAndProcessStatusAndPodName(@Param("transactionType") String transactionType, @Param("processStatus") List<String> status, @Param("dateThreshold") Date dateThreshold, @Param("podName") Set<String> podName);
	
	@Query(value = "SELECT * FROM NX_INR_DMAAP_AUDIT WHERE NX_TRANSACTION_TYPE = :transactionType and NX_PROCESS_STATUS='N' and CREATED_TIME < :dateThreshold and (NX_POD_NAME NOT IN (:podName) or NX_POD_NAME is null)"
			+ "ORDER BY NX_DMAAP_AUDIT_ID ASC", nativeQuery = true)
	public List<NxInrDmaapAudit> getPendingNewDmaapDetails(@Param("transactionType") String transactionType, @Param("dateThreshold") Date dateThreshold, @Param("podName") Set<String> podName);

	@Transactional
	@Modifying
	@Query(value="delete from NX_INR_DMAAP_AUDIT where nx_correlation_id=:nxReqId", nativeQuery=true)
	int	 deleteByNxReqId(@Param("nxReqId") String nxReqId);

}
