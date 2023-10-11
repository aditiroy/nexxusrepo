package com.att.sales.nexxus.myprice.transaction.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.util.HttpRestClient;

@Service("updateTransactionPrintDocServiceImpl")
public class UpdateTransactionPrintDocServiceImpl extends BaseServiceImpl{

	@Autowired
	private Environment env;
	
	@Autowired
	private HttpRestClient httpRestClient;
	
	@Value("${myprice.proxy.enabled}")
	private String isProxyEnabled;

	private static Logger logger = LoggerFactory.getLogger(UpdateTransactionAssignAccesptServiceImpl.class);

	
	public ServiceResponse updateTransactionPrintDocService(LinkedHashMap<String, Object> requestMap)
			throws SalesBusinessException {
		logger.info("Entering updateTransactionPrintDocService() method");
		ServiceResponse response = new ServiceResponse();
		String qualifyServiceResponse = null;
		String request = null;
		try {
			request = new JSONObject("{\"actionVariableName\": \"rl_wlAutoApprovalRateLetter_q\",\"saveToQuote\": true}").toString();
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			String myPriceTransId = (String) requestMap.get(StringConstants.MY_PRICE_TRANS_ID);
			Map<String, String> headers = new HashMap<String, String>();
			headers.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.MYPRICE_AUTHORIZATION));
			headers.put(StringConstants.REQUEST_CONTENT_TYPE, "application/json");
			String proxy = null;
			if(StringConstants.CONSTANT_Y.equalsIgnoreCase(isProxyEnabled)) {
				proxy = env.getProperty(CommonConstants.CDP_HTTP_PROXY);
			}
			qualifyServiceResponse = httpRestClient.callHttpRestClient(env.getProperty("myprice.updateTransactionPrintDocument")
					.replace("{transactionId}", myPriceTransId), HttpMethod.POST, null, request, 
					headers, proxy);
		} catch (SalesBusinessException e) {
			logger.error("Exception occured while calling update transaction print document service {}", e);
			throw new SalesBusinessException();
		}
		/*
		 * if (null != qualifyServiceResponse) { response = (ServiceResponse)
		 * restClient.processResult(qualifyServiceResponse, ServiceResponse.class);
		 * setSuccessResponse(response); }
		 */
		if (null == qualifyServiceResponse) {
			throw new SalesBusinessException();
		}
		logger.info("Exiting updateTransactionPrintDocService() method");
		return response;
	}
}
