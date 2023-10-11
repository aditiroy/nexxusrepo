package com.att.sales.nexxus.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.att.sales.nexxus.dao.model.SubmitToAnotherDeal;

/**
 * @author rc9330
 *
 */
public interface SubmitToAnotherDealRepository extends JpaRepository<SubmitToAnotherDeal, String> {
	
	@Query("select data from SubmitToAnotherDeal data where data.dealId =:dealId")
	SubmitToAnotherDeal findBydealId(@Param("dealId") String dealId);

}
