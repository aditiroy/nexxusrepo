package com.att.sales.nexxus.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.att.sales.nexxus.dao.model.NxMpMyloginMapping;

public interface NxMpMyloginMappingRepository extends JpaRepository<NxMpMyloginMapping, Long> {
	
	@Query(value = "select * from NX_MP_MYLOGIN_MAPPING where profile_name = :profileName and application = :application and active ='Y' order by order_seq asc", nativeQuery= true)
	List<NxMpMyloginMapping> findByProfileName(@Param("profileName") String profileName, @Param("application") String application);

}
