package com.att.sales.nexxus.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxLineItemLookUpDataModel;

/**
 * The Interface NxLineItemLookupDataRepository.
 */
public interface NxLineItemLookupDataRepository extends JpaRepository<NxLineItemLookUpDataModel, Long> {
	
	/**
	 * Find by item id.
	 *
	 * @param nxItemId the nx item id
	 * @return the list
	 */
	@Query(value="from NxLineItemLookUpDataModel nxl where nxl.nxItemId = :nxItemId  ") 
	List<NxLineItemLookUpDataModel> findByItemId( @Param("nxItemId")Long nxItemId);
	
	/**
	 * Find by little prod id and top prod id.
	 *
	 * @param littleProdId the little prod id
	 * @param topProdId the top prod id
	 * @return the list
	 */
	@Query(value="from NxLineItemLookUpDataModel nxl where nxl.littleProdId = :littleProdId  and nxl.topProdId=:topProdId ") 
	List<NxLineItemLookUpDataModel> findByLittleProdIdAndTopProdId( @Param("littleProdId")Long littleProdId ,@Param("topProdId") Long topProdId);
	
	/**
	 * Find by secondary key little prod id and top prod id.
	 *
	 * @param littleProdId the little prod id
	 * @param topProdId the top prod id
	 * @param secondaryKey the secondary key
	 * @param flowType the flow type
	 * @return the list
	 */
	@Query(value="from NxLineItemLookUpDataModel nxl where nxl.littleProdId = :littleProdId  and nxl.topProdId=:topProdId "
			+ "and nxl.secondaryKey=:secondaryKey and flowType=:flowType") 
	List<NxLineItemLookUpDataModel> findBySecondaryKeyLittleProdIdAndTopProdId( @Param("littleProdId")Long littleProdId,
			@Param("topProdId") Long topProdId,@Param("secondaryKey") String secondaryKey,@Param("flowType")String flowType);
	
	/**
	 * Gets the data count by line item id sec key little prod id and top prod id.
	 *
	 * @param lineItemId the line item id
	 * @param secondaryKey the secondary key
	 * @param littleProdId the little prod id
	 * @param topProdId the top prod id
	 * @param flowType the flow type
	 * @return the data count by line item id sec key little prod id and top prod id
	 */
	@Query(value="select count(*) from NxLineItemLookUpDataModel nxl where nxl.lineItemId=:lineItemId and nxl.secondaryKey=:secondaryKey "
			+ "and nxl.littleProdId = :littleProdId  and nxl.topProdId=:topProdId  and flowType=:flowType") 
	Long getDataCountByLineItemIdSecKeyLittleProdIdAndTopProdId(@Param("lineItemId")Long lineItemId,@Param("secondaryKey") String secondaryKey,
			@Param("littleProdId")Long littleProdId,@Param("topProdId") Long topProdId,@Param("flowType")String flowType);

	@Query(value = "select distinct source_id from PC_PRICE_ELEMENT_DETAILS where price_element_id in (select price_element_id from PC_PRICE_CATALOG where beid in (select field1_value from nx_line_item_lookup_data where field20_value=:uniqueid))", nativeQuery = true)
	String findSourceIdByUniqeId(@Param("uniqueid") String uniqueid);
	
	@Query(value = "select field1_value from nx_line_item_lookup_data where field20_value=:uniqueid and flow_type=:flowType", nativeQuery = true)
	String findSourceIdByUniqeIdAndFlowType(@Param("uniqueid") String uniqueid, @Param("flowType") String flowType);

	@Query(value = "from NxLineItemLookUpDataModel nxl where nxl.field20Value=:uniqueid and nxl.flowType=:flowType and active = 'Y'")
	List<NxLineItemLookUpDataModel> findByUniqueIdAndFlowType(@Param("uniqueid") String uniqueid, @Param("flowType") String flowType);	
	
	@Query(value = "select distinct source_id from PC_PRICE_ELEMENT_DETAILS where price_element_id in (select price_element_id from PC_PRICE_CATALOG where beid =:beid)", nativeQuery = true)
	String findSourceIdByBid(@Param("beid") String beid);
	
	@Query(value = "select distinct rate_type from PC_PRICE_ELEMENT_DETAILS where source_id =:sourceId", nativeQuery = true)
	List<Object> findRateTypeByProdRateId(@Param("sourceId") String sourceId);
	
	@Query(value = "select distinct rate_type from PC_PRICE_ELEMENT_DETAILS where source_id =:sourceId", nativeQuery = true)
	List<String> findRateTypesByProdRateId(@Param("sourceId") String sourceId);
	
	@Query(value = "select distinct beid from pc_price_catalog where price_element_id in (select price_element_id from pc_price_element_details where source_id =:sourceId)", nativeQuery = true)
	String findBeIdByProdRateId(@Param("sourceId") String sourceId);
	
	@Query(value = "select distinct source_id from PC_PRICE_ELEMENT_DETAILS where price_element_id in (select price_element_id from PC_PRICE_CATALOG where beid =:bid)", nativeQuery = true)
	String findBidByUniqueIdAndFlowType(@Param("bid") String bid);
}
