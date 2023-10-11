package com.att.sales.nexxus.serviceValidation.service;

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
import com.att.sales.framework.model.Status;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.serviceValidation.model.UpdateTransSitesServiceUpdateRequest;
import com.att.sales.nexxus.serviceValidation.model.UpdateTransSitesServiceUpdateResponse;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.RestClientUtil;

@Service("UpdateTransactionSitesServiceUpdateImpl")
public class UpdateTransactionSitesServiceUpdateImpl extends BaseServiceImpl
		implements UpdateTransactionSitesServiceUpdate {

	private static Logger logger = LoggerFactory.getLogger(UpdateTransactionSitesServiceUpdateImpl.class);

	@Autowired
	private Environment env;

	@Value("${myprice.username}")
	private String userName;

	@Value("${myprice.password}")
	private String password;

	@Autowired
	private RestClientUtil restClient;
	
	@Autowired
	private HttpRestClient httpRestClient;
	
	@Value("${myprice.proxy.enabled}")
	private String isProxyEnabled;

	@Override
	public UpdateTransSitesServiceUpdateResponse sitesServiceUpdate(UpdateTransSitesServiceUpdateRequest request,
			Long myPriceTransId, Map<String, Object> paramMap) {
		logger.info("Entering updateTransSiteStatusUpdate() method");
		Map<String, Object> requestParams = (Map<String, Object>) paramMap.get("requestMetaDataMap");
		UpdateTransSitesServiceUpdateResponse response = null;
		String transResponse = null;
		try {
			String uri = env.getProperty("myPrice.updateTransactionSiteServiceUpdate");
			uri = uri.replace("{transactionId}", String.valueOf(myPriceTransId));
			String requestString = JacksonUtil.toString(request);
			Map<String, String> requestHeaders = new HashMap<String, String>();
			requestHeaders.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.MYPRICE_AUTHORIZATION));
			String proxy = null;
			if(StringConstants.CONSTANT_Y.equalsIgnoreCase(isProxyEnabled)) {
				proxy = env.getProperty(CommonConstants.CDP_HTTP_PROXY);
			}
			transResponse = httpRestClient.callHttpRestClient(uri, HttpMethod.POST, null, requestString, 
					requestHeaders, proxy);
			
		} catch (SalesBusinessException e) {
			response = new UpdateTransSitesServiceUpdateResponse();
			Status status = new Status();
			status.setCode("M00003");
			response.setStatus(status);
			logger.error("Exception occured in Myprice updateTransSiteServiceUpdate call", e);
		}

		if (null != transResponse) {
			response = (UpdateTransSitesServiceUpdateResponse) restClient.processResult(transResponse,
					UpdateTransSitesServiceUpdateResponse.class);
			setSuccessResponse(response);
		}

		logger.info("Exiting updateTransSiteStatusUpdate() method");
		return response;
	}
}
