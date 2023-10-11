package com.att.sales.nexxus.dao.repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.att.sales.nexxus.dao.model.NxMpDeal;

/**
 * @author ShruthiCJ
 *
 */
public interface NxMpDealRepository extends JpaRepository<NxMpDeal, Long> {

	List<NxMpDeal> findBySolutionId(Long solutionId);
	
	@Query(value = "select designDetails.designData,mpDeal.nxTxnId from NxDesignDetails designDetails,NxMpDeal mpDeal where designDetails.nxDesign.nxDesignId = (select nxDesignId from NxMpDesignDocument where nxTxnId = (select mpDeal.nxTxnId from mpDeal where mpDeal.transactionId = :myPriceTxnId)) AND mpDeal.nxTxnId = (select mpDeal.nxTxnId from mpDeal where mpDeal.transactionId = :myPriceTxnId)")
	List<Object[]> findDesignDataNtxnByMyPriceTxnId(@Param("myPriceTxnId") String myPriceTxnId);

	@Query("select data from NxMpDeal data where data.activeYN = 'Y' and data.transactionId =:transactionId")
	NxMpDeal findByTransactionId(@Param("transactionId") String transactionId);
	
	@Query("select data from NxMpDeal data where data.activeYN = 'Y' and data.transactionId =:transactionId")
	List<NxMpDeal> findByMpTransactionId(@Param("transactionId") String transactionId);
	
	@Query(value = "select dealStatus from NxMpDeal where transactionId = :myPriceTxnId")
	String findStatusByMyPriceTxnId(@Param("myPriceTxnId") String myPriceTxnId);
	
	List<NxMpDeal> findBySolutionIdAndActiveYN(Long solutionId, String activeYN);

	List<NxMpDeal> findBydealID(String dealId);
	
	List<NxMpDeal> findBySolutionIdAndActiveYNAndPriceScenarioId(Long solutionId, String activeYN,
			Long priceScenarioId);
	
	@Query(value = "select max(revision) from NxMpDeal where solutionId = :solutionId and dealID = :dealID and activeYN = :activeYN and version = :version")
	public int findMaxRevisionBySolutoinId(@Param("solutionId") Long solutionId, @Param("dealID") String dealID, @Param("activeYN") String activeYN, @Param("version") String version);
	
	@Query(value = "select max(version) from NxMpDeal where solutionId = :solutionId and activeYN = :activeYN")
	public int findMaxVersionBySolutoinId(@Param("solutionId") Long solutionId, @Param("activeYN") String activeYN);
	
	NxMpDeal findByNxTxnIdAndPriceScenarioIdAndActiveYN(Long nxTxnId, Long priceScenarioId, String activeYN);
	
	@Query(value = "select designDetails.designData from NxDesignDetails designDetails where designDetails.nxDesign.nxDesignId = :nxDesignId")
	String findDesignDataByDesignId(@Param("nxDesignId") Long nxDesignId);
	
	@Query(value = "select dealDetails from NxMpDeal dealDetails where dealDetails.dealID = :dealId and dealDetails.version = :versionNumber")
	List<NxMpDeal> findBydealIDVersnId(@Param("dealId") String dealId, @Param("versionNumber") String versionNumber);
	 
	@Query(value = "select dealDetails from NxMpDeal dealDetails where dealDetails.dealID = :dealId and dealDetails.version = :versionNumber and dealStatus in (:dealStatus)")
	List<NxMpDeal> findBydealIDVersnId(@Param("dealId") String dealId, @Param("versionNumber") String versionNumber, @Param("dealStatus") Set<String> dealStatus);
	
	@Query(value = "select dealDetails from NxMpDeal dealDetails where dealDetails.dealID = :dealId and dealDetails.version = :versionNumber and dealDetails.revision = :revisionNumber")
	List<NxMpDeal> findBydealIDVersnIdRevId(@Param("dealId") String dealId, @Param("versionNumber") String versionNumber,@Param("revisionNumber") String revisionNumber);
	
	@Query(value = "select dealDetails from NxMpDeal dealDetails where dealDetails.dealID = :dealId and dealDetails.version = :versionNumber and dealDetails.revision = :revisionNumber and dealStatus in (:dealStatus)")
	List<NxMpDeal> findBydealIDVersnIdRevId(@Param("dealId") String dealId, @Param("versionNumber") String versionNumber,@Param("revisionNumber") String revisionNumber, @Param("dealStatus") Set<String> dealStatus);

	@Query(value = "SELECT deal FROM NxMpDeal deal WHERE deal.transactionId = :transactionId and deal.activeYN = 'Y' order by deal.nxTxnId")
	List<NxMpDeal> getByTransactionIdAndNxTxnOrder(@Param("transactionId") String transactionId);

	NxMpDeal getByTransactionId(@Param("transactionId") String transactionId);
	
	NxMpDeal findByNxTxnId(Long nxTxnId);
	
	@Transactional
	@Modifying
	@Query("update NxMpDeal set nxMpStatusInd = :nxMpStatusInd, modifiedDate = :modifiedDate where nxTxnId = :nxTxnId")
	int updateNxMpStatusIndByNxTxnId(@Param("nxMpStatusInd") String nxMpStatusInd, @Param("modifiedDate") Date modifiedDate, @Param("nxTxnId") Long nxTxnId);
	
	@Transactional
	@Modifying
	@Query("update NxMpDeal set dealStatus = :dealStatus, modifiedDate = :modifiedDate where nxTxnId = :nxTxnId")
	int updateMpDealStatusByNxTxnId(@Param("dealStatus") String dealStatus, @Param("modifiedDate") Date modifiedDate, @Param("nxTxnId") Long nxTxnId);
	
	List<NxMpDeal> findBySolutionIdAndOfferIdAndActiveYN(Long solutionId, String offerId, String activeYN);
	
	@Query("select data from NxMpDeal data where data.activeYN = 'Y' and data.transactionId =:transactionId")
	List<NxMpDeal> findAllByTransactionId(@Param("transactionId") String transactionId);
	
	@Query(value = "select COUNTRY_ISO_CODE from NX_COUNTRY where upper(COUNTRY_CODE) = upper(:country)", nativeQuery = true)
	String getCountryCodeByCountryIsoCode(@Param("country")String country);
	
	@Query(value = "select CURRENCY from NX_COUNTRY where upper(COUNTRY_ISO_CODE) = upper(:country)", nativeQuery = true)
	String getCurrencyCodeByCountryIsoCode(@Param("country")String country);
	
	@Query(value = "select designDetails.designData from NxDesignDetails designDetails where designDetails.nxDesign.nxDesignId = :nxDesignId and designDetails.componentId = :componentId and designDetails.productName = :productName")
	String findDesignDataByDesignIdAndComponentId(@Param("nxDesignId") Long nxDesignId, @Param("componentId") String componentId, @Param("productName") String productName);
	
	@Query(value = "select nmd.* from nx_solution_details nsd, nx_mp_deal nmd where nmd.deal_id = :dealId and nmd.active_yn = 'Y' and nsd.nx_solution_id = nmd.nx_solution_id and nsd.flow_type = 'AUTO' and nsd.external_key is not null and ROWNUM = 1", nativeQuery = true)
	NxMpDeal getDealByDealId(@Param("dealId") String dealId);
	
	@Query(value = "select * from nx_mp_deal where nx_solution_id = :solutionId and active_yn = :activeYN and nvl(action, 'N') != 'PD_CLONE'", nativeQuery = true)
	List<NxMpDeal> getActivePricerDDeals(@Param("solutionId") Long solutionId, @Param("activeYN") String activeYN);
	
	@Query(value = "select * from NX_MP_DEAL deal where deal.nx_solution_id in (select sol.nx_solution_id from nx_solution_details sol where sol.ACTIVE_YN ='Y' "
			+ "AND SOL.FLOW_TYPE IN ('INR', 'iglooQuote')) AND deal.deal_status = 'CREATED' AND deal.CREATED_DATE < :dateThreshold and deal.ACTIVE_YN = 'Y'", 
			nativeQuery=true)
	public List<NxMpDeal> getNxMpDeals(@Param("dateThreshold") Date dateValue);
	
	@Query(value = "SELECT nmd.* FROM nx_solution_details nsd, nx_mp_deal nmd WHERE nmd.deal_id = :dealId AND nmd.active_yn = 'Y' AND nsd.nx_solution_id = nmd.nx_solution_id AND ROWNUM = 1", nativeQuery = true)
	NxMpDeal getMpDealByDealId(@Param("dealId") String dealId);
	
	@Query(value="select  distinct ndd.FLOW_TYPE  from nx_mp_deal nd join NX_SOLUTION_DETAILS ndd on nd.NX_SOLUTION_ID= ndd.NX_SOLUTION_ID "
			+ "and nd.DEAL_ID=:dealId", nativeQuery = true)
	public List<String> findFlowtypeByDealId(@Param("dealId")String dealId);

}
