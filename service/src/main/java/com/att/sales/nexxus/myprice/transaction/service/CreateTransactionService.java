/**
 * 
 */
package com.att.sales.nexxus.myprice.transaction.service;

import java.util.Map;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.reteriveicb.model.RetreiveICBPSPRequest;

/**
 * @author ShruthiCJ
 *
 */
public interface CreateTransactionService {

	public Map<String, Object> createTransaction(RetreiveICBPSPRequest retreiveICBPSPRequest, NxSolutionDetail nxSolutionDetail, Long nxTxnId, String flowType);
	
	public Map<String, Object> callCreateTrans() throws SalesBusinessException;
}
