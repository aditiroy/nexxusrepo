package com.att.sales.nexxus.service;

import java.io.IOException;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.reteriveicb.model.ContractInvResponseBean;
import com.att.sales.nexxus.reteriveicb.model.ContractInventoryRequestBean;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface ContractInventoryService {
	/**
	 * getContractInventory method
	 * @param request
	 * @return
	 * @throws SalesBusinessException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	ServiceResponse getContractInventory(ContractInventoryRequestBean request)throws  JsonParseException, JsonMappingException, IOException ;
}
