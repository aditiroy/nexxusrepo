package com.att.sales.nexxus.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.att.sales.nexxus.dao.model.NxDwToJsonRules;

public interface NxDwToJsonRulesRepository extends JpaRepository<NxDwToJsonRules, Long> {
	
	List<NxDwToJsonRules> findByOfferAndRuleNameAndActive(String offer, String ruleName, String active);
}
