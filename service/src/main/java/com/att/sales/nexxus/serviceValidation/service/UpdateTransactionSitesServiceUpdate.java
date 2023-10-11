package com.att.sales.nexxus.serviceValidation.service;

import java.util.Map;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.serviceValidation.model.UpdateTransSitesServiceUpdateRequest;
import com.att.sales.nexxus.serviceValidation.model.UpdateTransSitesServiceUpdateResponse;

public interface UpdateTransactionSitesServiceUpdate {

	public UpdateTransSitesServiceUpdateResponse sitesServiceUpdate(
			UpdateTransSitesServiceUpdateRequest request, Long myPriceTransId, Map<String, Object> paramMap) throws SalesBusinessException;

}
