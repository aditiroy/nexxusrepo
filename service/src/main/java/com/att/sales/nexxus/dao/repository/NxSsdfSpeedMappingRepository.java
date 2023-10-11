package com.att.sales.nexxus.dao.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.dao.model.NxSsdfSpeedMapping;;
/**
 * The Interface NxSsdfSpeedMappingRepository.
 */
@Repository
@Transactional
public interface NxSsdfSpeedMappingRepository extends JpaRepository<NxSsdfSpeedMapping, Long>{
	
	@Query(value= "select * from NX_SSDF_SPEED_MAPPING n where n.ACTIVE = 'Y' and n.COMPONENT= :component and n.OFFER= :offer",nativeQuery=true)
	List<NxSsdfSpeedMapping> getSpeedData(@Param("component") String component, @Param("offer") String offer);
	
	

}

