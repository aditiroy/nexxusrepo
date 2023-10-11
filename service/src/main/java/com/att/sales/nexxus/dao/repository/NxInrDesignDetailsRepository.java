package com.att.sales.nexxus.dao.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.dao.model.NxInrDesign;
import com.att.sales.nexxus.dao.model.NxInrDesignDetails;

@Repository
@Transactional
public interface NxInrDesignDetailsRepository extends JpaRepository<NxInrDesignDetails, Long>{
	
	List<NxInrDesignDetails> findByNxInrDesignAndActiveYN(NxInrDesign nxInrDesign, String activeYN);

	@Query(value = "select nddetail from NxInrDesignDetails nddetail where nddetail.nxInrDesign.nxInrDesignId in (select nd.nxInrDesignId from NxInrDesign nd  where nd.nxSolutionId = :nxSolutionId)"
			+ " and nddetail.status='RN' and nddetail.usageCategory=:usageCategory and nddetail.product=:product")
	List<NxInrDesignDetails> findByNxSolutionIdAndUsagecategory(@Param("nxSolutionId") Long nxSolutionId, @Param("usageCategory")String usageCategory,
			@Param("product")String product);

}
