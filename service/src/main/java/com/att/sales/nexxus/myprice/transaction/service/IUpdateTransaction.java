package com.att.sales.nexxus.myprice.transaction.service;

import java.util.Map;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.myprice.transaction.model.CreateTransactionResponse;
import com.att.sales.nexxus.reteriveicb.model.RetreiveICBPSPRequest;

public interface IUpdateTransaction {

	public Map<String, Object> updateTransactionCleanSave(RetreiveICBPSPRequest retreiveICBPSPRequest,
			CreateTransactionResponse createTransactionResponse,Map<String, Object> paramMap) throws SalesBusinessException;

	
}
