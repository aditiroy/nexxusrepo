package com.att.sales.nexxus.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.att.sales.nexxus.dao.model.NxFmoMPOutputJsonMapping;

public interface NxFmoMPOutputJsonMappingRepo extends JpaRepository<NxFmoMPOutputJsonMapping, Long>{
	
	@Query(value = "from NxFmoMPOutputJsonMapping  where active = 'Y'")
	List<NxFmoMPOutputJsonMapping> findActiveRecord();

}
