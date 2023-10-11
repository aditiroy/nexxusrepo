package com.att.sales.nexxus.dao.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.att.sales.nexxus.dao.model.NxDwPriceDetails;

public interface NxDwPriceDetailsRepository extends JpaRepository<NxDwPriceDetails, Long> {

	Optional<NxDwPriceDetails> findById(Long id);
	
	@Transactional
	@Modifying
	@Query(value="delete from NX_DW_PRICE_DETAILS where nx_req_id=:nxReqId", nativeQuery=true)
	int deleteByNxReqId(@Param("nxReqId") Long nxReqId);

}

