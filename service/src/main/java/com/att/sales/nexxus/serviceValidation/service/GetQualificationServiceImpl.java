package com.att.sales.nexxus.serviceValidation.service;

import java.util.HashMap;
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
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.MessageConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxMpSiteDictionary;
import com.att.sales.nexxus.util.HttpRestClient;
import com.fasterxml.jackson.databind.MapperFeature;

@Service
public class GetQualificationServiceImpl {
	
	private static Logger logger = LoggerFactory.getLogger(GetQualificationServiceImpl.class);
	
	@Autowired
	private AVSQUtil avsqUtil;
	
	/** The env. */
	@Autowired
	private Environment env;
	
	@Autowired
	private HttpRestClient httpRestClient;
	
	@Value("${azure.proxy.enabled}")
	private String isProxyEnabled;
	
	public Map<String, Object> getQualification(String sourceSystem, Integer motsId, String uniqueId, Long nxTxnId,NxMpDeal cloneNxMpDeal) {
		logger.info("Start getQualification");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Boolean status = true;
		Map<String, String> queryParameters = new HashMap<String, String>();
		queryParameters.put("sourceSystem", sourceSystem);
		queryParameters.put("motsId", String.valueOf(motsId));
		queryParameters.put("uniqueId", uniqueId);
		Map<String, String> headers  = new HashMap<String, String>();

		headers.put(StringConstants.REQUEST_CONTENT_TYPE, "application/json");
		
		try {
			String ipeResponse = null;//dme2RestClient.callIpeGetQualification(queryParameters);
			
			String uri = env.getProperty("ipe.url");
			Map<String, String> requestHeaders  = new HashMap<String, String>();
			requestHeaders.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.AZURE_MS_AUTHORIZATION));
			try {
				com.fasterxml.jackson.databind.ObjectMapper thisMapper = new com.fasterxml.jackson.databind.ObjectMapper();
				thisMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

				String requestPayload = thisMapper.writeValueAsString("{}");
				String proxy = null;
				if(StringConstants.CONSTANT_Y.equals(isProxyEnabled)) {
					proxy = env.getProperty(CommonConstants.AZURE_HTTP_PROXY);
				}
				ipeResponse = httpRestClient.callHttpRestClient(uri, HttpMethod.GET, queryParameters, requestPayload, 
						requestHeaders, proxy);
			} catch (Exception e) {
				logger.error("exception occured while processing rest api call" + e);
				logger.error("SERVICE LOG :: Error while calling mS " + e.getMessage(), e);
				throw new SalesBusinessException(MessageConstants.ADDRESS_EXCEPTION);
			}
			
			logger.info("Response from IP&E ::"+ipeResponse);
			try {
				// '\n' is embedded \n in ipeResponse, causing MP siteUpdate error
				ipeResponse = ipeResponse.replaceAll("\\\\n", " ");
				JSONObject result = new JSONObject(ipeResponse);
				int statusCode = (int) result.get("statusCodeValue");
				if(statusCode == 200) {
					if(result.get("body") != null) {
						JSONObject json = (JSONObject) result.get("body");
						if("NOT_FOUND".equalsIgnoreCase(json.get("status").toString())) {
							status = false;
						}else {
							NxMpSiteDictionary nxMpSiteDictionary = avsqUtil.populateSiteJsonforQualificationUsingJsonNode(nxTxnId, json.toString(),cloneNxMpDeal);
							resultMap.put("siteData", nxMpSiteDictionary);
						}
					}
					
				}else {
					status = false;
					logger.info("Call to IP&E ms is failed with status code :: "+ statusCode);
				}
			}catch(org.json.JSONException e) {
				e.printStackTrace();
			}
		} catch (SalesBusinessException e) {
			status = false;
			logger.info("Exception occured while calling IP&E for getQualification");
			e.printStackTrace();
		}
		resultMap.put("status", status);
		return resultMap;
	}

}
