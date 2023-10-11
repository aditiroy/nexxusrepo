package com.att.sales.nexxus.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.att.sales.nexxus.dao.model.SalesMsProdcompUdfAttrVal;

/**
 * The Interface SalesMsProdcompRepository.
 */
public interface SalesMsProdcompRepository extends JpaRepository<SalesMsProdcompUdfAttrVal, Long> {

	
	/**
	 * Gets the udf attr id from sales tbl.
	 *
	 * @param offerId the offer id
	 * @param componentId the component id
	 * @param udfId the udf id
	 * @param udfAttrValue the udf attr value
	 * @return the udf attr id from sales tbl
	 */
	@Query(value="select udfAttributeId from SalesMsProdcompUdfAttrVal sp where sp.offerId = :offerId and sp.componentId=:componentId "
			+ "and sp.udfId=:udfId and sp.udfAttributeValue=:udfAttributeValue") 
	public List<Long> getUdfAttrIdFromSalesTbl(@Param("offerId")Long offerId,@Param("componentId")Long componentId,
			@Param("udfId")Long udfId,@Param("udfAttributeValue")String udfAttributeValue);
}
