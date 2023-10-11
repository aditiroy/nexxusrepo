package com.att.sales.nexxus.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.att.sales.nexxus.dao.model.NxValidationRules;

public interface NxValidationRulesRepository extends JpaRepository<NxValidationRules, Long>{
	
	List<NxValidationRules> findByValidationGroupAndOfferAndActiveAndFlowType(String validationGroup, String offer, String active, String flowType);
	
	List<NxValidationRules> findByValidationGroupAndOfferAndActiveAndFlowTypeAndName(String validationGroup, String offer, String active, String flowType, String name);

	List<NxValidationRules> findByValidationGroupAndActiveAndFlowType(String validationGroup, String active, String flowType);

	List<NxValidationRules> findByValidationGroupAndActive(String validationGroup, String active);

}
