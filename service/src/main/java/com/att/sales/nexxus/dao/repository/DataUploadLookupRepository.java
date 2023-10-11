package com.att.sales.nexxus.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.att.sales.nexxus.dao.model.LookupDataMapping;

/**
 * The Interface DataUploadLookupRepository.
 *
 * @author vt393d
 */
public interface DataUploadLookupRepository extends JpaRepository<LookupDataMapping, Long>{

	/**
	 * Find by little id.
	 *
	 * @param littelId the littel id
	 * @return the list
	 */
	@Query(value="from LookupDataMapping ldm where ldm.littelId = :littelId") 
	List<LookupDataMapping> findByLittleId( @Param("littelId")Long littelId);
}
