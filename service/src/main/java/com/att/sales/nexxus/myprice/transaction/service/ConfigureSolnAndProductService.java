package com.att.sales.nexxus.myprice.transaction.service;

import java.util.Map;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.myprice.transaction.model.ConfigureSolnAndProductRequest;
import com.att.sales.nexxus.myprice.transaction.model.ConfigureSolnAndProductResponse;
import com.att.sales.nexxus.reteriveicb.model.RetreiveICBPSPRequest;

/**
 * @author KumariMuktta
 *
 */
public interface ConfigureSolnAndProductService {

	public ConfigureSolnAndProductResponse configureSolnAndProduct(Map<String, Object> designMap) throws SalesBusinessException;

}
