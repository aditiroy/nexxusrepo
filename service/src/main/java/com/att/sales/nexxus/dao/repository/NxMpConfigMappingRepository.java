package com.att.sales.nexxus.dao.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.att.sales.nexxus.dao.model.NxMpConfigMapping;

public interface NxMpConfigMappingRepository extends JpaRepository<NxMpConfigMapping, Long>{
	
	@Query(value="from NxMpConfigMapping nmp where nmp.offer=:offer and nmp.ruleName=:ruleName and nmp.activeYN='Y' ") 
	List<NxMpConfigMapping> findByOfferAndRuleName(@Param("offer") String offer, @Param("ruleName") String ruleName);
	
	@Query(value="from NxMpConfigMapping nmp where nmp.offer=:offer and nmp.productType=:productType and nmp.ruleName=:ruleName and nmp.activeYN='Y' ") 
	List<NxMpConfigMapping> findByOfferAndProductTypeAndRuleName(@Param("offer") String offer,@Param("productType") String productType,@Param("ruleName") String ruleName);
	
	@Query(value="from NxMpConfigMapping nmp where nmp.offer IN (:offer) and nmp.ruleName=:ruleName and nmp.activeYN='Y' ") 
	List<NxMpConfigMapping> findByMultipleOffersAndRuleName(@Param("offer") Set<String> offers, @Param("ruleName") String ruleName);
	
	

}
