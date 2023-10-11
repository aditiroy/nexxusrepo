/**
 * 
 */
package com.att.sales.nexxus.dao.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.dao.model.NxSiteidProductMapping;

/**
 * @author sj0546
 *
 */
@Repository
@Transactional
public interface NxSiteidProductMappingRepo extends JpaRepository<NxSiteidProductMapping, Long>{
	
	List<NxSiteidProductMapping> findByNxSolutionId(@Param("nxSolutionId") Long nxSolutionId);
	
	

}
