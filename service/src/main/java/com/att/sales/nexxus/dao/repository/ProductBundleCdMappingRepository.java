package com.att.sales.nexxus.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.att.sales.nexxus.dao.model.ProductBundleCdMapping;

/**
 * The Interface ProductBundleCdMappingRepository.
 */
public interface ProductBundleCdMappingRepository  extends JpaRepository<ProductBundleCdMapping, Long>{
	
	/**
	 * Gets the product maaping by offer id.
	 *
	 * @param bundleOfferId the bundle offer id
	 * @return the product maaping by offer id
	 */
	@Query(value = "from ProductBundleCdMapping WHERE bundleOfferId = :bundleOfferId")
	public List<ProductBundleCdMapping> getProductMaapingByOfferId(@Param("bundleOfferId") Long bundleOfferId);

}
