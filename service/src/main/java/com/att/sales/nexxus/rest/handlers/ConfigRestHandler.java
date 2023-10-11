package com.att.sales.nexxus.rest.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.CustomJsonConstants;
import com.att.sales.nexxus.constant.MessageConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.myprice.transaction.service.MyPriceTransactionUtil;
import com.att.sales.nexxus.myprice.transaction.service.RestCommonUtil;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.att.sales.nexxus.util.RestClientUtil;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public abstract class ConfigRestHandler {
	
	private static final Logger log = LoggerFactory.getLogger(ConfigRestHandler.class);
	
	@Value("${myPrice.base.url}")
	private String mpBaseUrl;
	
	@Value("${myPrice.configSolution.rest.url}")
	private String configSolutionUrl;

	@Value("${myPrice.configSystem.rest.url}")
	private String configSystemUrl;
	
	@Value("${myPrice.configDesignUpdate.rest.url}")
	private String configDesignupdate;
	
	@Value("${myPrice.configAddTransaction.rest.url}")
	private String configAddTxnUrl;
	
	@Value("${rest.log.enabled:N}")
	private String logEnabled;
	
	@Value("${restWS.retrigger.enabled:N}")
	private String retriggerFlag;
	
	@Value("${restWS.retrigger.max.count:0}")
	private int maxRetriggerCount;
	
	@Value("${restWS.retrigger.mp.errorMsg:N}")
	private String mpRetriggerErrorMsg;
	
	private String wsName;
	
	@Autowired
	private RestClientUtil restClient;
	
	@Autowired
	private MyPriceTransactionUtil myPriceTransactionUtil;
	
	@Autowired
	private CustomJsonProcessingUtil customJsonProcessingUtil;
	
	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Autowired
	private NexxusJsonUtility nexxusJsonUtility;
	
	@Autowired
	private RestCommonUtil restCommonUtil;
	
	@Autowired
	private Environment env;
	
	@Autowired
	private HttpRestClient httpRestClient;
	
	@Value("${myprice.proxy.enabled}")
	private String isProxyEnabled;
	
	private  static final String CACHE_INSTANCE_ID_PATH="$..cacheInstanceId";
	private  static final String RESPONSE_ERROR_PATH="$..o:errorDetails.*.title";
	
	public abstract Map<String, Object> process(Map<String, Object> requestMap,String inputDesign) ;
	
	public void  triggerRestClient(String request, String url,String method,Map<String, Object> requestMap,Map<String, Object> responseMap)
			throws SalesBusinessException{
		Long currentTime = System.currentTimeMillis();
		Long startTime=System.currentTimeMillis() - currentTime;
		boolean printStartEndLogger=false;
		try {
			if(StringUtils.isNotEmpty(request)) {
				//log.info("Rest request for : {}",wsName+" "+request);
				if(StringConstants.CONSTANT_Y.equalsIgnoreCase(logEnabled)) {
					printLogs(request, null, wsName);
				}
				Map<String, String> headers  = new HashMap<>();
				headers.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.MYPRICE_AUTHORIZATION));
				headers.put(StringConstants.REQUEST_CONTENT_TYPE, "application/json");
				
				Map<String, Object> result = new HashMap<String, Object>();
				String proxy = null;
				if(StringConstants.CONSTANT_Y.equalsIgnoreCase(isProxyEnabled)) {
					proxy = env.getProperty(CommonConstants.CDP_HTTP_PROXY);
				}
				String response = httpRestClient.callHttpRestClient(url,  HttpMethod.resolve(method), null, request, 
						headers,proxy);
				result.put(MyPriceConstants.RESPONSE_DATA, response);
				result.put(MyPriceConstants.RESPONSE_CODE, 200);
				result.put(MyPriceConstants.RESPONSE_MSG,"OK");
				responseMap.putAll(result);
//				responseMap.putAll(restClient.initiateWebService(request,url,method,
//						headers, queryParameters));
				this.printStartEndLogs(printStartEndLogger,currentTime,startTime, requestMap, null);
				String responseMsg=(responseMap.containsKey(MyPriceConstants.RESPONSE_MSG) 
						&& responseMap.get(MyPriceConstants.RESPONSE_MSG) != null) ? (String) responseMap.get(MyPriceConstants.RESPONSE_MSG) : "";
				if(this.isRetriggered(responseMsg, requestMap, wsName)) {
					triggerRestClient(request, url, method, requestMap, responseMap);
				}else {
					processResponse(requestMap, responseMap);
				}
				
			}else {
				log.error("Error while creating custom request for  REST Api for :{}",wsName);
				String defaultError=requestMap.containsKey(CustomJsonConstants.DEFAULT_CONFIG_ERROR_MSG)?
						(String) requestMap.get(CustomJsonConstants.DEFAULT_CONFIG_ERROR_MSG):CustomJsonConstants.DEFAULT_ERROR_MSG;
				responseMap.put(CustomJsonConstants.REST_RESPONSE_ERROR,new HashSet<String>(Arrays.asList(defaultError)));
				responseMap.put(MyPriceConstants.RESPONSE_STATUS, false);
			}
			
		}catch(SalesBusinessException se) {
			log.error("Exception during REST call: {}", se.getMessage());
			this.printStartEndLogs(printStartEndLogger,currentTime,startTime, requestMap, se.getMessage());
			responseMap.put(MyPriceConstants.RESPONSE_STATUS, false);
			throw new SalesBusinessException(se.getMessage());
		}catch(Exception e) {
			log.error("Exception during REST call: {}", e.getMessage());
			this.printStartEndLogs(printStartEndLogger,currentTime,startTime, requestMap, e.getMessage());
			responseMap.put(MyPriceConstants.RESPONSE_STATUS, false);
			throw new SalesBusinessException(MessageConstants.PROCESS_ERROR_CODE);
		}
	}
	
	@SuppressWarnings("unchecked")
	public String getUrl(Map<String, Object> requestMap) {
		if(StringUtils.isNotEmpty(wsName)) {
			String offerName = requestMap.get(MyPriceConstants.OFFER_NAME) != null? (String) requestMap.get(MyPriceConstants.OFFER_NAME): "";
			String subOfferName = requestMap.get(MyPriceConstants.SUB_OFFER_NAME) != null? (String) requestMap.get(MyPriceConstants.SUB_OFFER_NAME): "";			
			if("LocA".equalsIgnoreCase(subOfferName) || "LocZ".equalsIgnoreCase(subOfferName)) {
			    subOfferName = "" ;
			}
			Map<String,String> restUrlProducts=null!=requestMap.get(CustomJsonConstants.REST_URL_PRODUCTS)?
					(Map<String,String>)requestMap.get(CustomJsonConstants.REST_URL_PRODUCTS):new HashMap<String, String>();
			String urlProduct=restUrlProducts.getOrDefault(StringUtils.isNotEmpty(subOfferName)?subOfferName:offerName,"");
			if(CustomJsonConstants.CONFIG_SOLUTION.equals(wsName)) {
				return configSolutionUrl.replace("{mpBaseUrl}", mpBaseUrl);
			}else if (CustomJsonConstants.CONFIG_SYSTEM.equals(wsName)) {
				return  configSystemUrl.replace("{mpBaseUrl}", mpBaseUrl).replace("{urlProduct}", urlProduct);
			}else if(CustomJsonConstants.CONFIG_DESIGN_UPDATE.equals(wsName)) {
				return  configDesignupdate.replace("{mpBaseUrl}", mpBaseUrl).replace("{urlProduct}", urlProduct);
			}else if(CustomJsonConstants.CONFIG_ADD_TRANSACTION.equals(wsName)) {
				return  configAddTxnUrl.replace("{mpBaseUrl}", mpBaseUrl).replace("{urlProduct}", urlProduct);
			}
		}
		return null;
	}
	
	public String getCacheInstanceId(String response) {
		Object cacheInstaceId=nexxusJsonUtility.getValue(response, CACHE_INSTANCE_ID_PATH);
		if(null!=cacheInstaceId) {
			return cacheInstaceId.toString();
		}
		return null;
	}
	
	protected Set<String> getResponseErrorMsg(String response,Map<String, Object> requestMap) {
		Set<String> errorMsgs=restCommonUtil.geDataInSetString(response, RESPONSE_ERROR_PATH);
		if(CollectionUtils.isNotEmpty(errorMsgs)) {
			return errorMsgs;
		}
		String defaultError=requestMap.containsKey(CustomJsonConstants.DEFAULT_CONFIG_ERROR_MSG)?
				(String) requestMap.get(CustomJsonConstants.DEFAULT_CONFIG_ERROR_MSG):CustomJsonConstants.DEFAULT_ERROR_MSG;
		return new HashSet<String>(Arrays.asList(defaultError));
	}
	
	public void processResponse(Map<String, Object> requestMap, Map<String, Object> responseMap) {
		if(responseMap.containsKey("code") && null!= responseMap.get("code")) {
			int code = (int) responseMap.get("code");
			String responseData = (responseMap.containsKey(MyPriceConstants.RESPONSE_DATA) 
						&& responseMap.get(MyPriceConstants.RESPONSE_DATA) != null) ? (String) responseMap.get(MyPriceConstants.RESPONSE_DATA) : null;
			if(StringUtils.isNotEmpty(responseData)) {
				if (code == CommonConstants.SUCCESS_CODE) {
					responseMap.put(CustomJsonConstants.CACHE_INSTANCE_ID, getCacheInstanceId(responseData));
					responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
					//log.info("Rest response for : {}",wsName+" "+responseData);			
					if(StringConstants.CONSTANT_Y.equalsIgnoreCase(logEnabled)) {
						printLogs(null, responseData, wsName);
					}			
				} else {
					Set<String> errorMsg=this.getResponseErrorMsg(responseData,requestMap);
					String logMessage = wsName+" "+errorMsg;
					log.info("Error in Rest response from MP  for : {} ", org.apache.commons.lang3.StringUtils.normalizeSpace(logMessage));
					responseMap.put(CustomJsonConstants.REST_RESPONSE_ERROR,errorMsg);
					responseMap.put(MyPriceConstants.RESPONSE_STATUS, false);
				}
			}
		}
	}
	
	protected void printStartEndLogs(boolean printStartEndLogger,Long currentTime,Long startTime, 
			Map<String, Object> methodParamMap,String error) {
		Long endTime=0l;
		if(!printStartEndLogger) {
			endTime=System.currentTimeMillis() - currentTime;
			methodParamMap.put("restWs_ProcessTime_"+methodParamMap.get(""), endTime - startTime);
			printStartEndLogger=true;
			String totalDuration="";
			if(StringUtils.isNotBlank(error)) {
				totalDuration=new StringBuilder().append("InitiateRestService->> Error : "+error+" >>> "+ wsName + " method took").append(" for Id "+methodParamMap.get(MyPriceConstants.MP_TRANSACTION_ID)+" is : ")
						.append((endTime - startTime)).append(" ").append(MyPriceConstants.MILLISEC).toString();
			}else {
				totalDuration=new StringBuilder().append("InitiateRestService->>Success >>> "+ wsName + " method took").append(" for Id "+methodParamMap.get(MyPriceConstants.MP_TRANSACTION_ID)+" is : ")
						.append((endTime - startTime)).append(" ").append(MyPriceConstants.MILLISEC).toString();
			}
			
			StringBuffer logTotalDuration = new StringBuffer(totalDuration);
			log.info(" totalDuration :::::: {}", org.apache.commons.lang3.StringUtils.normalizeSpace(logTotalDuration.toString()));
		}
	}
	
	
	protected void printLogs(Object request,Object response, String wsName) {
		if(null!=request) {
			log.info("Rest request for : {}",wsName+" "+ request);
		}
		if(null!=response) {
			log.info("Rest response for : {}",wsName+" "+ response);
		}
	}
	
	
	protected boolean isRetriggered(String responseErrorMsg,Map<String, Object> methodParamMap,String wsName) {
		try {
			if(StringUtils.isNotEmpty(wsName) && "Y".equalsIgnoreCase(retriggerFlag)){
				List<String> mpRetriggerErrorMsgLst=new ArrayList<String>(Arrays.asList(mpRetriggerErrorMsg.split(Pattern.quote("##"))));
				if(StringUtils.isNotEmpty(responseErrorMsg) && CollectionUtils.isNotEmpty(mpRetriggerErrorMsgLst)
						&& mpRetriggerErrorMsgLst.contains(responseErrorMsg)) {
					if(!methodParamMap.containsKey(MyPriceConstants.REST_WS_RETRIGGER_COUNT) 
							|| null==methodParamMap.get(MyPriceConstants.REST_WS_RETRIGGER_COUNT) ){
						methodParamMap.put(MyPriceConstants.REST_WS_RETRIGGER_COUNT, 1);
					}
					int count=(int)methodParamMap.get(MyPriceConstants.REST_WS_RETRIGGER_COUNT);
					if(count<=maxRetriggerCount) {
						log.info("Rest API retrigger call for : {}", wsName);
						log.info("Rest API retrigger call: {}",count+" "+responseErrorMsg);
						count++;
						methodParamMap.put(MyPriceConstants.REST_WS_RETRIGGER_COUNT, count);
						return true;
					}
				}
			}	
		}catch(Exception e) {
			log.error("Exception during isRetriggered method: {}", e);
			return false;
		}
		
		return false;
		
	}
	
	
}
