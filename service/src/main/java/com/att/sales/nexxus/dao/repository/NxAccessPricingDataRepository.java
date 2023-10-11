package com.att.sales.nexxus.dao.repository;

import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

/*
 * Author chandan
 */

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.dao.model.NxAccessPricingData;
/**
 * The Interface NxAccessPricingDataRepository.
 */
@Repository
@Transactional
public interface NxAccessPricingDataRepository extends JpaRepository<NxAccessPricingData, Long>{
	 
	//List<NxAccessPricingData> findByNxSolutionId( @Param("nxSolutionId")Long nxSolutionId);
	
	/*@Query(value="select * from NX_ACCESS_PRICING_DATA ac where ac.TOKEN_ID = :ethToken and ac.NX_SOLUTION_ID = :nxSolutionId",nativeQuery = true)
	List<NxAccessPricingData> getNxPricingData(@Param("ethToken")String ethToken, @Param("nxSolutionId")Long nxSolutionId);*/
	
	/**
	 * Find by nx solution id.
	 *
	 * @param nxSolutionId the nx solution id
	 * @return the list
	 */
	@Query(value="select * from NX_ACCESS_PRICING_DATA nxt where nxt.NX_SOLUTION_ID = :nxSolutionId order by nxt.NX_ACCESS_PRICE_ID desc",nativeQuery = true) 
	List<NxAccessPricingData> findByNxSolutionId( @Param("nxSolutionId")Long nxSolutionId);
	
	@Query(value="select * from NX_ACCESS_PRICING_DATA nxt where nxt.NX_SOLUTION_ID = :nxSolutionId  and (nxt.has_required_fields='Y' OR nxt.has_required_fields is null) order by nxt.NX_ACCESS_PRICE_ID desc",nativeQuery = true) 
	List<NxAccessPricingData> findByHasrequiredfeilds( @Param("nxSolutionId")Long nxSolutionId);
	

	/**
	 * Gets the nx access pricing data.
	 *
	 * @param apId the ap id
	 * @return the nx access pricing data
	 */
	@Query(value="select * from NX_ACCESS_PRICING_DATA ac where ac.NX_ACCESS_PRICE_ID = :apId" ,nativeQuery = true)
	NxAccessPricingData getNxAccessPricingData(@Param("apId")Long apId);
	
	/**
	 * Find by eth token and nx solution id.
	 *
	 * @param ethToken the eth token
	 * @param nxSolutionId the nx solution id
	 * @return the list
	 */
	List<NxAccessPricingData> findByEthTokenAndNxSolutionId(@Param("ethToken")String ethToken, @Param("nxSolutionId")Long nxSolutionId);
	
	/**
	 * Find by nx sol id and include ind.
	 *
	 * @param nxSolutionId the nx solution id
	 * @return the list
	 */
	@Query(value="from NxAccessPricingData nxt where nxt.nxSolutionId = :nxSolutionId and includeYn='Y' ") 
	List<NxAccessPricingData> findByNxSolIdAndIncludeInd( @Param("nxSolutionId")Long nxSolutionId);
    
    @Query(value = "SELECT COUNT(case when (include_yn = 'Y' and (MP_STATUS is null or MP_STATUS = 'RF')) then 1 end) as ap_selected_count, count(*) as ap_count "
    		+ "FROM nx_access_pricing_data WHERE nx_solution_id = :nxSolutionId and (has_required_fields='Y' OR has_required_fields is null)", 
			nativeQuery = true)
	List<Object[]> findByNxSolId(@Param("nxSolutionId") Long nxSolutionId);
	
//	@Query(value="select * from NX_ACCESS_PRICING_DATA nxt where nxt.NX_SOLUTION_ID = :nxSolutionId and (MP_STATUS is null OR nxt.has_required_fields='N' OR nxt.has_required_fields is null)",nativeQuery = true) 
//	List<NxAccessPricingData> findByNxSolnId( @Param("nxSolutionId") Long nxSolutionId);
	
	@Query(value="select * from NX_ACCESS_PRICING_DATA nxt where nxt.NX_SOLUTION_ID = :nxSolutionId and ((MP_STATUS is null or MP_STATUS = 'RF') and (nxt.has_required_fields='Y' OR nxt.has_required_fields is null)) and INCLUDE_YN='Y'",nativeQuery = true) 
	List<NxAccessPricingData> findByNxSolIdAndIncludeIndAndMpStatus( @Param("nxSolutionId") Long nxSolutionId);
	
	@Query(value="select count(*) from NX_ACCESS_PRICING_DATA nxt where nxt.NX_SOLUTION_ID = :nxSolutionId and ((MP_STATUS is null or MP_STATUS = 'RF') and (nxt.has_required_fields='Y' OR nxt.has_required_fields is null)) and INCLUDE_YN='Y'",nativeQuery = true) 
	Long getCountByNxSolIdAndIncludeIndAndMpStatus( @Param("nxSolutionId") Long nxSolutionId);
	
	@Query(value = "select nx from NxAccessPricingData nx where nx.ethToken in (:ethTokens) and nx.nxSolutionId = :nxSolutionId")
	List<NxAccessPricingData> getTokens(@Param("ethTokens") Set<String> ethTokens, @Param("nxSolutionId") Long nxSolutionId);
	
	@Query(value="select * from NX_ACCESS_PRICING_DATA nxt where nxt.NX_SOLUTION_ID = :nxSolutionId and ((MP_STATUS is null or MP_STATUS IN ('RS', 'RF')) and( nxt.has_required_fields='Y' OR nxt.has_required_fields is null) ) and INCLUDE_YN='Y'",nativeQuery = true) 
	List<NxAccessPricingData> findByNxSolIdAndMpStatusAndIncludeInd( @Param("nxSolutionId") Long nxSolutionId);

	@Query(value="select * from NX_ACCESS_PRICING_DATA nxt where nxt.NX_SOLUTION_ID = :nxSolutionId and ( nxt.MP_STATUS = :mpStatus OR nxt.has_required_fields='N')",nativeQuery = true) 
	List<NxAccessPricingData> findByNxSolIdAndMpStatus( @Param("nxSolutionId") Long nxSolutionId,@Param("mpStatus") String mpStatus);

	@Query(value = "select nx from NxAccessPricingData nx where nx.nxSolutionId = :nxSolutionId and (nx.ethToken in (:tokens) or nx.iglooQuoteId in (:tokens))")
	List<NxAccessPricingData> findByTokensAndQuotes(@Param("tokens") Set<String> ethTokens, @Param("nxSolutionId") Long nxSolutionId);
	
	List<NxAccessPricingData> findByIglooQuoteIdAndNxSolutionId(@Param("iglooQuoteId")String ethToken, @Param("nxSolutionId")Long nxSolutionId);

	@Query(value = "select circuitId from NxAccessPricingData where nxSolutionId = :nxSolutionId and locationYn = 'Migration'")
	List<String> fetchCktByLocationYnAndNxSolutionId(@Param("nxSolutionId") Long nxSolutionId);
	
	@Query(value = "select * from NX_ACCESS_PRICING_DATA nxt where nxt.NX_SOLUTION_ID = :nxSolutionId and nxt.LOCATION_YN = 'Existing' and nxt.CIRCUIT_ID IS NOT NULL",nativeQuery = true)
	List<NxAccessPricingData> fetchByByLocationYnAndNxSolutionId(@Param("nxSolutionId") Long nxSolutionId);

	@Query(value="select * from NX_ACCESS_PRICING_DATA nxt where nxt.NX_SOLUTION_ID = :nxSolutionId and (MP_STATUS is null or MP_STATUS NOT IN ('RS') OR nxt.has_required_fields='N' OR nxt.has_required_fields is null  ) and INCLUDE_YN='Y'",nativeQuery = true) 
	List<NxAccessPricingData> findByNxSolIdAndMpStatusNdIncludeInd( @Param("nxSolutionId") Long nxSolutionId);

	NxAccessPricingData findByNxAccessPriceId(Long nxAccessPriceId);

}
