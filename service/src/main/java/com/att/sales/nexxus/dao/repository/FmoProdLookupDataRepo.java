package com.att.sales.nexxus.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.att.sales.nexxus.dao.model.FmoProdLookupData;

/**
 * The Interface FmoProdLookupDataRepo.
 *
 * @author vt393d
 */
public interface FmoProdLookupDataRepo extends JpaRepository<FmoProdLookupData, Long>{

	/* (non-Javadoc)
	 * @see org.springframework.data.jpa.repository.JpaRepository#findAll()
	 */
	public List<FmoProdLookupData> findAll();
	
	/**
	 * Find by rate type and ims 2 code.
	 *
	 * @param rateType the rate type
	 * @param ims2code the ims 2 code
	 * @return the fmo prod lookup data
	 */
	@Query(value = "from FmoProdLookupData  where rateType in(:rateType) and ims2Code in(:ims2code)")
	public List<FmoProdLookupData> findByRateTypeAndIms2code(@Param("rateType") List<String> rateType,
			@Param("ims2code") List<String> ims2code);
	
	
	@Query(value = "from FmoProdLookupData  where rateType in(:rateType)")
	public List<FmoProdLookupData> findDataRateType(@Param("rateType") List<String> rateType);
}
