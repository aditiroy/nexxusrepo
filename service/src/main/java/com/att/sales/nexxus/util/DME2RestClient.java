package com.att.sales.nexxus.util;

import java.io.IOException;

/**
 * SalesRestClient
 */

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
import com.att.sales.nexxus.accesspricing.model.AccessPricingResponseWrapper;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.MessageConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.edf.model.ManageBillDataInv;
import com.att.sales.nexxus.edf.model.ManageBillingPriceInventoryDataResponse;
import com.att.sales.nexxus.edf.model.ValidateAccountDataRequest;
import com.att.sales.nexxus.edf.model.ValidateAccountDataResponse;
import com.att.sales.nexxus.model.MailResponse;
import com.att.sales.nexxus.model.QuoteRequest;
import com.att.sales.nexxus.reteriveicb.model.RetreiveICBPSPRequest;
import com.att.sales.nexxus.reteriveicb.model.RetreiveICBPSPResponse;
import com.att.sales.nexxus.serviceValidation.model.AddressValidationServiceQualificationRequest;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

// TODO: Auto-generated Javadoc
/**
 * The Class DME2RestClient.
 */
@Component
public class DME2RestClient {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(DME2RestClient.class);
	
	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;

	/** The wrapper. */
	//@Autowired
	//private DME2Utility wrapper;
	
	/** The env. */
	@Autowired
	private Environment env;
	
	@Autowired
	private HttpRestClient httpRestClient;
	
	@Value("${azure.proxy.enabled}")
	private String isProxyEnabled;

	/**
	 * Invoke individual MS.
	 *
	 * @param uri            the uri
	 * @param subContext     the sub context
	 * @param requestPayLoad the request pay load
	 * @param aftLatitude    the aft latitude
	 * @param aftLongitude   the aft longitude
	 * @param aftEnvironment the aft environment
	 * @param userName       the user name
	 * @param password       the password
	 * @param requestHeaders the request headers
	 * @return the string
	 * @throws SalesBusinessException the sales business exception
	 */
	public String callDme2Client(String uri, String subContext, String requestPayLoad, String aftLatitude,
			String aftLongitude, String aftEnvironment, String userName, String password,
			Map<String, String> requestHeaders) throws SalesBusinessException {
		String response = null;
		try {
			logger.info(uri);
			logger.info(subContext);
			logger.info(aftEnvironment);
			logger.info(aftLatitude);
			logger.info(aftLongitude);
			logger.info(userName);
			logger.info(password);
			
			logger.info(org.apache.commons.lang3.StringUtils.normalizeSpace(requestPayLoad));
			String method = CommonConstants.POST_METHOD;

			requestHeaders.put("Content-Type", "application/json");
			Map<String, String> queryParameters = new HashMap<>();

			//response = wrapper.processRequest(aftEnvironment, aftLatitude, aftLongitude, uri, subContext, method,
				//	queryParameters, requestHeaders, requestPayLoad, userName, password, false);
		} catch (Exception e) {
			logger.error("Exception while Calling Child mS: ", e);
			throw new SalesBusinessException(MessageConstants.HTTP_URL_EXCEPTION);
		}
		logger.info(org.apache.commons.lang3.StringUtils.normalizeSpace(response));
		return response;
	}

	/**
	 * Invoke individual MS.
	 *
	 * @param uri            the uri
	 * @param subContext     the sub context
	 * @param requestPayLoad the request pay load
	 * @param aftLatitude    the aft latitude
	 * @param aftLongitude   the aft longitude
	 * @param aftEnvironment the aft environment
	 * @param userName       the user name
	 * @param password       the password
	 * @param requestHeaders the request headers
	 * @param method the method
	 * @return the string
	 * @throws SalesBusinessException the sales business exception
	 */
	public String callDme2Client(String uri, String subContext, String requestPayLoad, String aftLatitude,
			String aftLongitude, String aftEnvironment, String userName, String password,
			Map<String, String> requestHeaders, String method, Map<String, String> queryParameters) throws SalesBusinessException {
		String response = null;
		try {
			logger.info(uri);
			logger.info(org.apache.commons.lang3.StringUtils.normalizeSpace(subContext));
			logger.info(aftEnvironment);
			logger.info(aftLatitude);
			logger.info(aftLongitude);
			logger.info(userName);
			logger.info(password);
			logger.info(requestPayLoad);
			logger.info(method);
			
			requestHeaders.put("Content-Type", "application/json");
			//Map<String, String> queryParameters = new HashMap<>();

			//response = wrapper.processRequest(aftEnvironment, aftLatitude, aftLongitude, uri, subContext, method,
				//	queryParameters, requestHeaders, requestPayLoad, userName, password, false);
		} catch (Exception e) {
			logger.error("Exception while Calling Child mS: ", e);
			throw new SalesBusinessException(MessageConstants.HTTP_URL_EXCEPTION);
		}
		logger.info(org.apache.commons.lang3.StringUtils.normalizeSpace(response));
		return response;
	}
	
	/**
	 * Gets the pricing access.
	 *
	 * @param quoteRequest the quote request
	 * @return the pricing access
	 * @throws SalesBusinessException the sales business exception
	 * @throws JsonParseException the json parse exception
	 * @throws JsonMappingException the json mapping exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public AccessPricingResponseWrapper getPricingAccess(QuoteRequest quoteRequest)
			throws SalesBusinessException, JsonParseException, JsonMappingException, IOException {

		/*String uri = env.getProperty("pricing.ms.uri");		
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		String uri = env.getProperty("pricing.ms.uri");
		String subContext = env.getProperty("pricing.ms.subContext");
		String aftLatitude = env.getProperty("dme2.latitude");
		String aftLongitude = env.getProperty("dme2.longitude");
		String aftEnvironment = env.getProperty("dme2.aftenv");
		String userName = env.getProperty("dme2.authid");
		String password = env.getProperty("dme2.authpassword");

		Map<String, String> requestHeaders = new HashMap<>();
		requestHeaders.put("Offer", "AVPN");
		requestHeaders.put(StringConstants.REQUEST_AUTHORIZATION, authKeyy);
		requestHeaders.put(StringConstants.REQUEST_CONTENT_TYPE, MediaType.APPLICATION_JSON);
		com.fasterxml.jackson.databind.ObjectMapper thisMapper = new com.fasterxml.jackson.databind.ObjectMapper();
		thisMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
		String requestPayLoad = mapper.writeValueAsString(quoteRequest);
		logger.info("The request is {}", requestPayLoad);
		logger.info("The uri is {}", uri);
		
		String response =  restClientUtil.callMPRestClient(requestPayLoad, uri, "POST",requestHeaders, queryParameters);

		String response = callDme2Client(uri, subContext, requestPayLoad, aftLatitude, aftLongitude, aftEnvironment,
				userName, password, requestHeaders);

		AccessPricingResponse responseObj = thisMapper.readValue(response, AccessPricingResponse.class);
		
		JsonNode tree = mapper.readTree(response);
		return new AccessPricingResponseWrapper(responseObj, tree);*/
		return null;

	}

	/**
	 * Gets the billing price inventry uri.
	 *
	 * @param inventryRequest the inventry request
	 * @return the billing price inventry uri
	 * @throws Exception the exception
	 */
	public ManageBillingPriceInventoryDataResponse getBillingPriceInventryUri(ManageBillDataInv inventryRequest)
			throws Exception {

		String uri = env.getProperty("edf.url");

		Map<String, String> requestHeaders = new HashMap<>();
		requestHeaders.put("SourceSystem", env.getProperty("edf.header.SourceSystem"));
		requestHeaders.put("Accept", "application/json");
		requestHeaders.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.AZURE_MS_AUTHORIZATION));
		
		com.fasterxml.jackson.databind.ObjectMapper thisMapper = new com.fasterxml.jackson.databind.ObjectMapper();
		thisMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
		thisMapper.setSerializationInclusion(Include.NON_NULL);
		thisMapper.setSerializationInclusion(Include.NON_EMPTY); 
		
		String requestPayload = thisMapper.writeValueAsString(inventryRequest);
		logger.info("The request now is {}", requestPayload);
		String proxy = null;
		if(StringConstants.CONSTANT_Y.equals(isProxyEnabled)) {
			proxy = env.getProperty(CommonConstants.AZURE_HTTP_PROXY);
		}
		
		String response = httpRestClient.callHttpRestClient(uri,  HttpMethod.POST, null, requestPayload, 
				requestHeaders, proxy);

		ManageBillingPriceInventoryDataResponse resp = thisMapper.readValue(response,
				ManageBillingPriceInventoryDataResponse.class);
		
		return resp;
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

		/*String uri = env.getProperty("edf.bulkupload.dmeurl");
		String subContext = env.getProperty("edf.bulkupload.ms.subContext");
		String aftLatitude = env.getProperty("edf.latitude");
		String aftLongitude = env.getProperty("edf.longitude");
		String aftEnvironment = env.getProperty("edf.aftenv");
		String userName = env.getProperty("edf.authid");
		String password = env.getProperty("edf.authpassword");

		Map<String, String> requestHeaders = new HashMap<>();
		requestHeaders.put("SourceSystem", env.getProperty("edf.header.SourceSystem"));
		requestHeaders.put("Accept", "application/json");

		com.fasterxml.jackson.databind.ObjectMapper thisMapper = new com.fasterxml.jackson.databind.ObjectMapper();
		thisMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
		thisMapper.setSerializationInclusion(Include.NON_NULL);
		thisMapper.setSerializationInclusion(Include.NON_EMPTY);

		String requestPayLoad = thisMapper.writeValueAsString(validateAccDataRequest);
		logger.info("The request now is {}", requestPayLoad);

		String response = null;
		ValidateAccountDataResponse responseObj = null;
		try {
			response = callDme2Client(uri, subContext, requestPayLoad, aftLatitude, aftLongitude, aftEnvironment,
					userName, password, requestHeaders);
			logger.info("The response is {}", response);
			responseObj = thisMapper.readValue(response, ValidateAccountDataResponse.class);
			logger.info("The responseObj is {}", responseObj);
		} catch (Exception e) {
			logger.error("exception occured while processing dme2 call" + e);
			logger.error("SERVICE LOG :: Error while calling mS " + e.getMessage(), e);
			throw new SalesBusinessException(MessageConstants.PROCESS_ERROR_CODE);
		}
		logger.info("The responseObj is {}", responseObj);
		return responseObj;*/
		return null;
	}

	
	/**
	 * Call for MailNotification.
	 *
	 * @param jsonPayLoad the json pay load
	 * @return the mail response
	 * @throws SalesBusinessException the sales business exception
	 */

     public MailResponse callMailNotificationDME2(String jsonPayLoad) throws SalesBusinessException {
		String uri = env.getProperty("mail.ms.uri");
		com.fasterxml.jackson.databind.ObjectMapper thisMapper = new com.fasterxml.jackson.databind.ObjectMapper();
		thisMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
		Map<String, String> requestHeaders = new HashMap<>();
		requestHeaders.put(StringConstants.KEY_OFFER, StringConstants.REQ_OFFER);
		requestHeaders.put(StringConstants.SERVICE, StringConstants.MAIL);
		requestHeaders.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.AZURE_MS_AUTHORIZATION));
		String response = null;
		MailResponse responseObj = null;
		try {
			String proxy = null;
			if(StringConstants.CONSTANT_Y.equals(isProxyEnabled)) {
				proxy = env.getProperty(CommonConstants.AZURE_HTTP_PROXY);
			}
			response = httpRestClient.callHttpRestClient(uri,  HttpMethod.POST, null, jsonPayLoad, 
					requestHeaders, proxy);
			responseObj = thisMapper.readValue(response, MailResponse.class);
		} catch (Exception e) {
			logger.error("exception occured while processing dme2 call" + e);
			logger.error("SERVICE LOG :: Error while calling mS " + e.getMessage(), e);
			throw new SalesBusinessException(MessageConstants.ADDRESS_EXCEPTION);
		}

		return responseObj;
	}
	
	public RetreiveICBPSPResponse callOrchCustomPricingOrderFlow(RetreiveICBPSPRequest request,String offerName)
			throws SalesBusinessException{
		RetreiveICBPSPResponse response=null;
		String uri = env.getProperty("dppOrch.url");
		Map<String, String> requestHeaders = new HashMap<>();
		requestHeaders.put("Offer", offerName);
		requestHeaders.put("X-ATT-ClientId","ADOPT");
		requestHeaders.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.AZURE_MS_AUTHORIZATION));
		try {
			com.fasterxml.jackson.databind.ObjectMapper thisMapper = new com.fasterxml.jackson.databind.ObjectMapper();
			thisMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
			String requestPayload = mapper.writeValueAsString(request);
			String proxy = null;
			if(StringConstants.CONSTANT_Y.equals(isProxyEnabled)) {
				proxy = env.getProperty(CommonConstants.AZURE_HTTP_PROXY);
			}
			String respString = httpRestClient.callHttpRestClient(uri,  HttpMethod.POST, null, requestPayload, 
					requestHeaders, proxy);
			response = thisMapper.readValue(respString, RetreiveICBPSPResponse.class);
			logger.info("The response is {}", response);
			
		} catch (Exception e) {
			logger.error("exception occured while processing dme2 call" + e);
			logger.error("SERVICE LOG :: Error while calling mS " + e.getMessage(), e);
			throw new SalesBusinessException(MessageConstants.ADDRESS_EXCEPTION);
		}
		
		return response;
	}
	
	public RetreiveICBPSPResponse callOrchCustomPricingOrderFlow(org.json.simple.JSONObject request,String offerName)
			throws SalesBusinessException{
		RetreiveICBPSPResponse response=null;
		String uri = env.getProperty("dppOrch.url");	
		Map<String, String> requestHeaders = new HashMap<>();
		requestHeaders.put("Offer", offerName);
		requestHeaders.put("X-ATT-ClientId","ADOPT");
		requestHeaders.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.AZURE_MS_AUTHORIZATION));
		try {
			com.fasterxml.jackson.databind.ObjectMapper thisMapper = new com.fasterxml.jackson.databind.ObjectMapper();
			thisMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
			String proxy = null;
			if(StringConstants.CONSTANT_Y.equals(isProxyEnabled)) {
				proxy = env.getProperty(CommonConstants.AZURE_HTTP_PROXY);
			}
			String respString = httpRestClient.callHttpRestClient(uri,  HttpMethod.POST, null, request.toJSONString(), 
					requestHeaders, proxy); 
			response = thisMapper.readValue(respString, RetreiveICBPSPResponse.class);
			
		} catch (Exception e) {
			logger.error("exception occured while processing dme2 call" + e);
			logger.error("SERVICE LOG :: Error while calling mS " + e.getMessage(), e);
			throw new SalesBusinessException(MessageConstants.ADDRESS_EXCEPTION);
		}
		
		return response;
	}
	

	
	public String callAVSQRequest(AddressValidationServiceQualificationRequest request, Map<String, Object> paramMap)
			throws SalesBusinessException{
		logger.info("Inside callAVSQRequest : RestClient");
		ObjectMapper avsqMapper = new ObjectMapper();
		String uri = env.getProperty("avsq.url");
		Map<String, String> requestHeaders = new HashMap<>();
		requestHeaders.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.AZURE_MS_AUTHORIZATION));
		requestHeaders.put("X-ATT-TimeToLive", env.getProperty("avsq.timeout"));
		requestHeaders.put("X-ATT-MessageId", env.getProperty("avsq.messageId"));
		requestHeaders.put("X-ATT-Version", env.getProperty("avsq.version"));
		requestHeaders.put("X-ATT-ConversationId", request.getQualConversationId());
		String respString = null;
		try {
			avsqMapper.setSerializationInclusion(Include.NON_NULL);
			avsqMapper.setSerializationInclusion(Include.NON_EMPTY);
			SimpleFilterProvider filterProvider = new SimpleFilterProvider();
			filterProvider.addFilter("avsqLocationFilter", SimpleBeanPropertyFilter.filterOutAllExcept("LocationOptions", "ValidationOptions"));
			avsqMapper.setFilterProvider(filterProvider);
			String requestPayload = avsqMapper.writeValueAsString(request);
			String proxy = null;
			if(StringConstants.CONSTANT_Y.equals(isProxyEnabled)) {
				proxy = env.getProperty(CommonConstants.AZURE_HTTP_PROXY);
			}
			respString = httpRestClient.callHttpRestClient(uri, HttpMethod.POST, null, requestPayload, 
					requestHeaders, proxy); 

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("exception occured while processing rest call" + e);
			logger.error("SERVICE LOG :: Error while calling mS " + e.getMessage(), e);
		}
		return respString;
	}
	
	public String callIpeGetQualification(Map<String, Object> queryParameters)	throws SalesBusinessException{
		/*logger.info("Calling IP&E for Get Qualification .");
	public String callIpeGetQualification(Map<String, String> queryParameters)	throws SalesBusinessException{
		logger.info("Calling IP&E for Get Qualification .");
		String uri = env.getProperty("ipe.dmeurl");
		String subContext = env.getProperty("ipe.ms.subContext");
		String userName = env.getProperty("ipe.authid");
		String password = env.getProperty("ipe.authpassword");
		uri=uri.concat(subContext);
		Map<String, String> headers  = new HashMap<String, String>();
		String encoded= Base64.getEncoder().encodeToString((userName + ":" + password).getBytes());
		headers.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+encoded);
		headers.put(StringConstants.REQUEST_CONTENT_TYPE, "application/json");

		String respString = null;
		try {
			com.fasterxml.jackson.databind.ObjectMapper thisMapper = new com.fasterxml.jackson.databind.ObjectMapper();
			thisMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

			String requestPayLoad = thisMapper.writeValueAsString("{}");
			logger.info("The request is {}", requestPayLoad);
			logger.info("IPE lgw request uri {}", uri);
			
			respString=restClientUtil.callMPRestClient(null, uri, CommonConstants.GET_METHOD, headers, queryParameters);
			
		} catch (Exception e) {
			logger.error("exception occured while processing dme2 call" + e);
			logger.error("SERVICE LOG :: Error while calling mS " + e.getMessage(), e);
			throw new SalesBusinessException(MessageConstants.ADDRESS_EXCEPTION);
		}
		
		return respString;*/
		return null;
	}
	
	
}

