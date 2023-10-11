package com.att.sales.nexxus.dao.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.dao.model.LittleProductDataEntity;
import com.att.sales.nexxus.dao.model.TopProductDataEntity;

/**
 * The Interface LittleProductRepo.
 *
 * @author RudreshWaladaunki
 */
@Repository
@Transactional
public interface LittleProductRepo extends JpaRepository<LittleProductDataEntity, Long> {
	
	/**
	 * Find by top product data.
	 *
	 * @param topProductData the top product data
	 * @return the list
	 */
	List<LittleProductDataEntity> findByTopProductData(@Param("topProductData") TopProductDataEntity topProductData);

	/**
	 * Find by little product id.
	 *
	 * @param littleProductId the little product id
	 * @return the list
	 */
	List<LittleProductDataEntity> findByLittleProductId(Long littleProductId);

	LittleProductDataEntity findByLittleId(Long littleId);
}
