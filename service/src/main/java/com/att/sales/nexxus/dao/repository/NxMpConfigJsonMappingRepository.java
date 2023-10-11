package com.att.sales.nexxus.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.att.sales.nexxus.dao.model.NxMpConfigJsonMapping;

public interface NxMpConfigJsonMappingRepository extends JpaRepository<NxMpConfigJsonMapping, Long> {
	
	@Query(value="from NxMpConfigJsonMapping nmp where nmp.offer=:offer and nmp.ruleName=:ruleName and nmp.activeYN='Y' order by orderSeq") 
	List<NxMpConfigJsonMapping> findByOfferAndRuleName(@Param("offer") String offer, @Param("ruleName") String ruleName);
	
	@Query(value="from NxMpConfigJsonMapping nmp where nmp.offer=:offer and nmp.productType=:productType and nmp.ruleName=:ruleName and nmp.activeYN='Y' order by orderSeq") 
	List<NxMpConfigJsonMapping> findByOfferAndProductTypeAndRuleName(@Param("offer") String offer,@Param("productType") String productType,@Param("ruleName") String ruleName);
	
	@Query(value="from NxMpConfigJsonMapping nmp where nmp.offer=:offer and nmp.subOffer=:subOffer and nmp.productType=:productType and nmp.ruleName=:ruleName and nmp.activeYN='Y' order by orderSeq") 
	List<NxMpConfigJsonMapping> findByOfferAndSubOfferAndProductTypeAndRuleName(@Param("offer") String offer,@Param("subOffer") String subOffer,@Param("productType") String productType,@Param("ruleName") String ruleName);

}
