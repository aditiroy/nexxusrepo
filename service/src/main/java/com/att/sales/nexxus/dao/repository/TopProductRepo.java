package com.att.sales.nexxus.dao.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.dao.model.TopProductDataEntity;

/**
 * The Interface TopProductRepo.
 *
 * @author RudreshWaladaunki
 */
@Repository
@Transactional
public interface TopProductRepo extends JpaRepository<TopProductDataEntity, Long> {

	/**
	 * Find by top product id.
	 *
	 * @param topProductId the top product id
	 * @return the list
	 */
	public List<TopProductDataEntity> findByTopProductId(Long topProductId);
}
