package com.att.sales.nexxus.dao.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.dao.model.NxFeature;

@Repository
public interface NxFeatureRepository extends JpaRepository<NxFeature, String> {
	
	NxFeature findByFeatureNameAndActive(String featureName, String active);

}
