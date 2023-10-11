package com.att.sales.nexxus.dao.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.dao.model.NxInrActivePods;

@Repository
@Transactional
public interface NxInrActivePodsRepository  extends JpaRepository<NxInrActivePods, String>{
	
	public NxInrActivePods findByPodName(@Param("podName") String podName);
	
	@Query(value = "select * from NX_REQUEST_DETAILS WHERE LAST_HEARTBEAT >= :dateThreshold",nativeQuery=true)
	public List<NxInrActivePods> getNxInrActivePods(@Param("dateThreshold") Date dateThreshold);
	
	@Query(value = "SELECT pod.pod_name, count(DMAAP.NX_POD_NAME) FROM NX_INR_ACTIVE_PODS POD LEFT JOIN nx_inr_dmaap_audit DMAAP ON DMAAP.NX_POD_NAME = POD.POD_NAME "
			+ "and dmaap.nx_process_status IN ('N') where pod.last_heartbeat >= :dateThreshold and pod.pod_type = :transactionType group by pod.pod_name", nativeQuery = true)
	public List<Object[]> getPods(@Param("dateThreshold") Date dateThreshold, @Param("transactionType") String transactionType);

	@Query(value = "select pod.pod_name FROM NX_INR_ACTIVE_PODS POD where pod.last_heartbeat >= :dateThreshold and pod.pod_type = :podType and rownum =1 order "
			+ "by pod.last_heartbeat desc",nativeQuery=true)
	public List<Object[]> getActivePodByPodType(@Param("dateThreshold") Date dateThreshold, @Param("podType") String podType);


}
