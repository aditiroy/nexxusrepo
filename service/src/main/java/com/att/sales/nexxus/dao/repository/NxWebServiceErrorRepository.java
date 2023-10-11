package com.att.sales.nexxus.dao.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.dao.model.NxWebServiceError;

@Repository
public interface NxWebServiceErrorRepository extends JpaRepository<NxWebServiceError, Long> {
	
	@Query(value = "select count(t) from NxWebServiceError t where serviceName = :serviceName and requestTime > :requestTime and not exists (select x from NxWebServiceError x where serviceName = :serviceName and alertSentTime > :alertSentTime)")
	long countError(@Param("serviceName") String serviceName, @Param("requestTime") Date requestTime, @Param("alertSentTime") Date alertSentTime);
	
	long countByServiceName(String serviceName);
	
	long countByServiceNameAndRequestTimeAfter(String serviceName, Date requestTime);
}
