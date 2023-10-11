package com.att.sales.nexxus.myprice.transaction.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
import com.att.sales.nexxus.util.RestClientUtil;

@Service("updateTransactionApproveRateLetterServiceImpl")
public class UpdateTransactionApproveRateLetterServiceImpl extends BaseServiceImpl{

	@Autowired
	private RestClientUtil restClient;

	@Autowired
	private Environment env;
	
	@Autowired
	private HttpRestClient httpRestClient;
	
	@Value("${myprice.proxy.enabled}")
	private String isProxyEnabled;

	private static Logger logger = LoggerFactory.getLogger(UpdateTransactionAssignAccesptServiceImpl.class);

	
	public ServiceResponse updateTransactionApproveRateLetterService(LinkedHashMap<String, Object> requestMap)
			throws SalesBusinessException {
		logger.info("Entering updateTransactionApproveRateLetterService() method");
		ServiceResponse response = new ServiceResponse();
		String qualifyServiceResponse = null;
		try {
			String myPriceTransId = (String) requestMap.get(StringConstants.MY_PRICE_TRANS_ID);
			String emptyRequest = new JSONObject().toString();
			String uri = env.getProperty("myprice.updateTransactionApproveRateLetter").replace("{transactionId}", myPriceTransId);
			Map<String, String> headers = new HashMap<String, String>();
			headers.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.MYPRICE_AUTHORIZATION));
			headers.put(StringConstants.REQUEST_CONTENT_TYPE, "application/json");
			String proxy = null;
			if(StringConstants.CONSTANT_Y.equalsIgnoreCase(isProxyEnabled)) {
				proxy = env.getProperty(CommonConstants.CDP_HTTP_PROXY);
			}
			qualifyServiceResponse = httpRestClient.callHttpRestClient(uri, HttpMethod.POST, null, emptyRequest, 
					headers, proxy);
		} catch (SalesBusinessException e) {
			logger.error("Exception occured while calling update transaction qualify service {}", e);
		}
		if (null != qualifyServiceResponse) {
			response = (ServiceResponse) restClient.processResult(qualifyServiceResponse, ServiceResponse.class);
			setSuccessResponse(response);
		}
		logger.info("Exiting updateTransactionPreviewWirelineService() method");
		return response;
	}
}
