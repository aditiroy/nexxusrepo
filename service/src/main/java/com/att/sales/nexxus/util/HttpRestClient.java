/**
 * 
 */
package com.att.sales.nexxus.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.util.HttpsUtility;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.MessageConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.edf.model.ValidateAccountDataRequest;
import com.att.sales.nexxus.edf.model.ValidateAccountDataResponse;
import com.att.sales.nexxus.service.WebServiceErrorAlertService;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.MapperFeature;

/**
 * @author sj0546
 *
 */
@Component
public class HttpRestClient {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(HttpRestClient.class);
	
	@Autowired
	private HttpsUtility httpsUtility;
	
	@Autowired
	private Environment env;
	
	@Value("${azure.proxy.enabled}")
	private String isProxyEnabled;
	
	@Autowired
	private WebServiceErrorAlertService webServiceErrorAlertService;
	
	/**
	 * To call rest api
	 * @param uri
	 * @param method
	 * @param queryParameters
	 * @param requestPayload
	 * @param requestHeaders
	 * @param proxy
	 * @return
	 * @throws SalesBusinessException
	 */
	public String callHttpRestClient(String uri,  HttpMethod method, Map<String, String> queryParameters, String requestPayload, 
			Map<String, String> requestHeaders, String proxy) throws SalesBusinessException {
		String response = null;
		try {
			logger.info(org.apache.commons.lang3.StringUtils.normalizeSpace(uri));
			logger.info(org.apache.commons.lang3.StringUtils.normalizeSpace(requestPayload));
			response = httpsUtility.processRequest(uri, method, queryParameters, requestHeaders,
					 requestPayload, proxy);
		} catch (Exception e) {
			logger.error("Exception while Calling Child mS: ", e);
			if (queryParameters != null) {
				Map<String, Object> qp = new HashMap<>();
				queryParameters.forEach((k, v) -> qp.put(k, v));
				webServiceErrorAlertService.serviceErrorAlert(requestPayload, uri, response, requestHeaders, qp, e);
			} else {
				webServiceErrorAlertService.serviceErrorAlert(requestPayload, uri, response, requestHeaders, null, e);
			}
			throw new SalesBusinessException(MessageConstants.HTTP_URL_EXCEPTION);
		}
		
		logger.info(StringUtils.normalizeSpace(response));
		return response;
	}
	
	/**
	 * getValidateAccontDataUri.
	 * 
	 * @param validateAccDataRequest
	 * @return
	 * @throws Exception
	 */
	public ValidateAccountDataResponse getValidateAccontDataUri(ValidateAccountDataRequest validateAccDataRequest)
			throws Exception {

		String uri = env.getProperty("edf.bulkupload.url");
		Map<String, String> requestHeaders = new HashMap<>();
		requestHeaders.put("SourceSystem", env.getProperty("edf.header.SourceSystem"));
		requestHeaders.put("Accept", "application/json");
		requestHeaders.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.AZURE_MS_AUTHORIZATION));

		com.fasterxml.jackson.databind.ObjectMapper thisMapper = new com.fasterxml.jackson.databind.ObjectMapper();
		thisMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
		thisMapper.setSerializationInclusion(Include.NON_NULL);
		thisMapper.setSerializationInclusion(Include.NON_EMPTY);

		String requestPayload = thisMapper.writeValueAsString(validateAccDataRequest);
		String response = null;
		ValidateAccountDataResponse responseObj = null;
		try {
			String proxy = null;
			if(StringConstants.CONSTANT_Y.equals(isProxyEnabled)) {
				proxy = env.getProperty(CommonConstants.AZURE_HTTP_PROXY);
			}
			response = callHttpRestClient(uri,  HttpMethod.POST, null, requestPayload, 
					requestHeaders, proxy);
			responseObj = thisMapper.readValue(response, ValidateAccountDataResponse.class);
		} catch (Exception e) {
			logger.error("exception occured while processing dme2 call" + e);
			logger.error("SERVICE LOG :: Error while calling mS " + e.getMessage(), e);
			throw new SalesBusinessException(MessageConstants.PROCESS_ERROR_CODE);
		}
		return responseObj;
	}

}
