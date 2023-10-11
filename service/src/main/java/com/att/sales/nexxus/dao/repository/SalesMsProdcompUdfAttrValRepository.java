package com.att.sales.nexxus.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.att.sales.nexxus.dao.model.SalesMsProdcompUdfAttrVal;

public interface SalesMsProdcompUdfAttrValRepository extends JpaRepository<SalesMsProdcompUdfAttrVal, Long> {

	SalesMsProdcompUdfAttrVal findTopByOfferIdAndComponentIdAndUdfIdAndUdfAttributeIdAndActive(Long offerId, Long componentId,
			Long udfId, Long udfAttributeId, String active);
}
