package com.att.sales.nexxus.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.att.sales.nexxus.dao.model.NxRatePlanDetails;

/**
 * @author KRani
 *
 */
public interface NxRatePlanDetailsRepository extends JpaRepository<NxRatePlanDetails, Long> { 
	
	@Query(value="from NxRatePlanDetails nrpd where nrpd.socDate = :socDate  and nrpd.product=:product ") 
	List<NxRatePlanDetails> findBySocDateAndProduct( @Param("socDate") String socDate ,@Param("product") String product);

	@Query(value="from NxRatePlanDetails nrpd where nrpd.socDate = :socDate") 
	List<NxRatePlanDetails> findBySocDate(@Param("socDate") String socDate);
	
	NxRatePlanDetails findTopBySocDateAndProductAndActiveYn(String socDate, String product, String activeYn);
	
	NxRatePlanDetails findTopBySocDateContainingAndProductAndActiveYnOrderBySocDateDesc(String socDate, String product, String activeYn);
	
	NxRatePlanDetails findTopByProductAndErateIndicatorAndActiveYnOrderBySocDateDesc(String product, String erateIndicator, String activeYn);
}
