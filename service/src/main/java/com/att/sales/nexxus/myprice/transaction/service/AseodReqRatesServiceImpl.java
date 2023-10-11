package com.att.sales.nexxus.myprice.transaction.service;

import java.util.HashMap;
import java.util.Map;

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
import com.att.sales.nexxus.myprice.transaction.model.AseodReqRatesResponse;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.RestClientUtil;

@Service("aseodReqRatesServiceImpl")
public class AseodReqRatesServiceImpl extends BaseServiceImpl {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(AseodReqRatesServiceImpl.class);

	/** The env. */
	@Autowired
	private Environment env;

	@Autowired
	private RestClientUtil restClient;
	
	@Autowired
	private HttpRestClient httpRestClient;
	
	@Value("${myprice.proxy.enabled}")
	private String isProxyEnabled;
	
	public ServiceResponse aseodReqRates(String transactionId) throws SalesBusinessException {
		logger.info("Entering aseodReqRates() method {}", transactionId);
		AseodReqRatesResponse response = null;
		try {
			//myPriceTransactionUtil.setSystemProperties();
			String uri = env.getProperty("myprice.aseodReqRatesArrContainer");
			uri = uri.replace("{transactionId}", transactionId);			
			Map<String, String> headers  = new HashMap<>();
			headers.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.MYPRICE_AUTHORIZATION));
			headers.put(StringConstants.REQUEST_CONTENT_TYPE, "application/json");
			String proxy = null;
			if(StringConstants.CONSTANT_Y.equalsIgnoreCase(isProxyEnabled)) {
				proxy = env.getProperty(CommonConstants.CDP_HTTP_PROXY);
			}
			String transResponse = httpRestClient.callHttpRestClient(uri, HttpMethod.GET, null, null, 
					headers, proxy);
			if (null != transResponse) {
				response = (AseodReqRatesResponse) restClient.processResult(transResponse,
						AseodReqRatesResponse.class);
				logger.info("aseodReqRates Response : {}", JacksonUtil.toString(response));
				setSuccessResponse(response);
			}
		} catch (SalesBusinessException e) {
			logger.error("exception occured in Myprice aseodReqRates call {}", e.getMessage());
		}
		logger.info("Existing aseodReqRates() method {}", transactionId);
		return response;
	}

}
