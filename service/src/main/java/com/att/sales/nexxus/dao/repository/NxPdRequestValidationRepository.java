package com.att.sales.nexxus.dao.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.dao.model.NxPdRequestValidation;;
/**
 * The Interface NxSolutionDetailsRepository.
 */
@Repository
@Transactional
public interface NxPdRequestValidationRepository extends JpaRepository<NxPdRequestValidation, String>{
		/**
		 * method for fetching all the validation
		 * @param product req
		 * @return List<PDValidationDetailsBean> res
		 */
		@Query(value= "select * from NX_PD_REQUEST_VALIDATION n where n.ACTIVE = 'Y' and (n.product = 'All' or  n.product = :product) ORDER BY n.validation_order",nativeQuery=true)
		List<NxPdRequestValidation> fetchAllValidation(@Param("product") String product);
		
}
