package com.att.sales.nexxus.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.dao.model.NxUserFeatureMapping;

@Repository
public interface NxUserFeatureMappingRepository extends JpaRepository<NxUserFeatureMapping, Long> {
	
	List<NxUserFeatureMapping> findByEnabled(String enabled);
	List<NxUserFeatureMapping> findByNxUser_UserAttIdAndEnabled(String userAttId,String enabled);
	
	@Query(value="select nf.feature_name from nx_user_feature_mapping nmp, nx_feature nf where nmp.feature_id= nf.feature_id and nmp.user_att_id= :userAttId and nmp.enabled='Y' and nf.active='Y'",nativeQuery=true)
	List<String> findByNxUserUserAttIdAndEnabled(@Param("userAttId") String userAttId);

}
