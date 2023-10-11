package com.att.sales.nexxus.myprice.transaction.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.common.MessageConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.myprice.transaction.model.Aseod3PAPortReqRatesResponse;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.RestClientUtil;

@Component("aseod3PAPortReqRatesServiceImpl")
public class Aseod3PAPortReqRatesServiceImpl extends BaseServiceImpl implements Asenod3PAService<Aseod3PAPortReqRatesResponse>{

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(Aseod3PAPortReqRatesServiceImpl.class);

	/** The env. */
	@Autowired
	private Environment env;

	@Autowired
	private RestClientUtil restClient;
	
	@Autowired
	private MyPriceTransactionUtil myPriceTransactionUtil;
	
	@Autowired
	private HttpRestClient httpRestClient;
	
	@Value("${myprice.proxy.enabled}")
	private String isProxyEnabled;

	@Override
	public Aseod3PAPortReqRatesResponse process(String transactionId) throws SalesBusinessException {
		logger.info("Entering processPortReqRates method {}", transactionId);
		Aseod3PAPortReqRatesResponse response=null;
		try {
			String uri = env.getProperty("myprice.portReqRateArrContainer");
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
				response = (Aseod3PAPortReqRatesResponse) restClient.processResult(transResponse,
						Aseod3PAPortReqRatesResponse.class);
				setSuccessResponse(response);
			}
			
		}catch(Exception se) {
			throw new SalesBusinessException(MessageConstants.PROCESS_ERROR_CODE);
		}
		return response;
	}
}