package com.att.sales.nexxus.myprice.transaction.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.constant.CustomJsonConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.rest.handlers.ConfigRestHandler;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.att.sales.nexxus.util.JacksonUtil;



@Component
public class ConfigAndUpdateRestProcessingService {
	
	private static final Logger log = LoggerFactory.getLogger(ConfigAndUpdateRestProcessingService.class);
	
	@Autowired
	@Qualifier("configSolutionRestHandler")
	private ConfigRestHandler configSolutionRestHandler;
	
	@Autowired
	@Qualifier("configDesignUpdateRestHandler")
	private ConfigRestHandler configDesignUpdateRestHandler;
	
	@Autowired
	@Qualifier("configInSystemRestHandler")
	private ConfigRestHandler configInSystemRestHandler;
	
	
	@Autowired
	@Qualifier("configAddTransactionRestHandler")
	private ConfigRestHandler configAddTransactionRestHandler;
	
	@Autowired
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	@Autowired
	private ConfigAndUpdateRestUtilPd configAndUpdateRestUtilPd;
	
	
	
	@SuppressWarnings("unchecked")
	public void callMpConfigAndUpdate(Map<String, Object> requestMap,String inputDesign){
		log.info("Inside  callMpConfigAndUpdate for input design : {}", org.apache.commons.lang3.StringUtils.normalizeSpace(String.valueOf(requestMap.get(CustomJsonConstants.BS_ID))));
		this.getErrorMsgFromLookUp(requestMap);
		this.getProductRestMpUrlFromLookup(requestMap);
		//process inputDesign PD flow
		inputDesign=this.processInputDesignForPDFlow(requestMap, inputDesign);
		Boolean configSolution=false;
		Boolean configInSystem=false;
		Boolean configDesignUpdate=false;
		
		
		configSolution=this.callConfigSolution(requestMap, inputDesign);
		
		if(configSolution) {
			configInSystem=this.callConfigInSystem(requestMap, inputDesign);
		}
		if(configInSystem) {
			configDesignUpdate=this.callConfigDesignAndUpdate(requestMap, inputDesign);
		}
		if(configDesignUpdate) {
			this.callConfigAddToTxn(requestMap, inputDesign);
		}
	}


	
	
	
	protected boolean callConfigSolution(Map<String, Object> requestMap,String inputDesign) {
		Map<String, Object> responseMap=configSolutionRestHandler.process(requestMap, inputDesign);
		if(responseMap.containsKey(MyPriceConstants.RESPONSE_STATUS) && (Boolean)responseMap.get(MyPriceConstants.RESPONSE_STATUS)) {
			requestMap.put(MyPriceConstants.RESPONSE_STATUS, true);
			requestMap.put(CustomJsonConstants.CACHE_INSTANCE_ID, responseMap.get(CustomJsonConstants.CACHE_INSTANCE_ID));
			return true;
		}else {
			setDataForError(requestMap, responseMap, CustomJsonConstants.CONFIG_SOLUTION);
			return false;
		}
	}
	
	protected boolean callConfigInSystem(Map<String, Object> requestMap,String inputDesign) {
		Map<String, Object> responseMap=configInSystemRestHandler.process(requestMap, inputDesign);
		if(responseMap.containsKey(MyPriceConstants.RESPONSE_STATUS) && (Boolean)responseMap.get(MyPriceConstants.RESPONSE_STATUS)) {
			requestMap.put(MyPriceConstants.RESPONSE_STATUS, true);
			requestMap.put(CustomJsonConstants.CACHE_INSTANCE_ID, responseMap.get(CustomJsonConstants.CACHE_INSTANCE_ID));
			return true;
		}else {
			setDataForError(requestMap, responseMap, CustomJsonConstants.CONFIG_SYSTEM);
			return false;
		}
	}
	
	
	protected boolean callConfigDesignAndUpdate(Map<String, Object> requestMap,String inputDesign) {
		
		Map<String, Object> responseMap=configDesignUpdateRestHandler.process(requestMap, inputDesign);
		
		//if site failed to configure in configDesign update with config bom error or site config error then we r not stopping addTxn API triggered
		if((responseMap.containsKey(CustomJsonConstants.CONFIG_BOM_ERROR) && 
				(Boolean)responseMap.get(CustomJsonConstants.CONFIG_BOM_ERROR)) || (responseMap.containsKey(CustomJsonConstants.SITE_CONFIG_ERROR) && 
						(Boolean)responseMap.get(CustomJsonConstants.SITE_CONFIG_ERROR))) {
			requestMap.put(CustomJsonConstants.CACHE_INSTANCE_ID, responseMap.get(CustomJsonConstants.CACHE_INSTANCE_ID));
			requestMap.put(MyPriceConstants.RESPONSE_STATUS, false);
			//setting true or false if we getting bom error in MP response
			requestMap.put(CustomJsonConstants.CONFIG_BOM_ERROR, responseMap.get(CustomJsonConstants.CONFIG_BOM_ERROR));
			//setting error in Set<String> if got error bom config  in MP response
			requestMap.put(CustomJsonConstants.CONFIG_BOM_ERROR_DATA, responseMap.get(CustomJsonConstants.CONFIG_BOM_ERROR_DATA));
			//setting true or false if we getting  error in siteConfigurationError_pf field in MP response
			requestMap.put(CustomJsonConstants.SITE_CONFIG_ERROR, responseMap.get(CustomJsonConstants.SITE_CONFIG_ERROR));
			//setting response  error details of siteConfigurationError_pf field from MP response
			requestMap.put(CustomJsonConstants.SITE_CONFIG_ERROR_MAP, responseMap.get(CustomJsonConstants.SITE_CONFIG_ERROR_MAP));
			requestMap.put(CustomJsonConstants.ERROR_WS_NAME, CustomJsonConstants.CONFIG_DESIGN_UPDATE);
			return true;
		}
		 
		if(responseMap.containsKey(MyPriceConstants.RESPONSE_STATUS) && (Boolean)responseMap.get(MyPriceConstants.RESPONSE_STATUS)) {
			requestMap.put(MyPriceConstants.RESPONSE_STATUS, true);
			requestMap.put(CustomJsonConstants.CACHE_INSTANCE_ID, responseMap.get(CustomJsonConstants.CACHE_INSTANCE_ID));
			return true;
		}else {
			setDataForError(requestMap, responseMap, CustomJsonConstants.CONFIG_DESIGN_UPDATE);
			return false;
		}
	}
	
	protected boolean callConfigAddToTxn(Map<String, Object> requestMap,String inputDesign) {
		Map<String, Object> responseMap=configAddTransactionRestHandler.process(requestMap, inputDesign);
		if(responseMap.containsKey(MyPriceConstants.RESPONSE_STATUS) && (Boolean)responseMap.get(MyPriceConstants.RESPONSE_STATUS)) {
			requestMap.put(MyPriceConstants.RESPONSE_STATUS, true);
			requestMap.put(CustomJsonConstants.CACHE_INSTANCE_ID, responseMap.get(CustomJsonConstants.CACHE_INSTANCE_ID));
			return true;
		}else {
			setDataForError(requestMap, responseMap, CustomJsonConstants.CONFIG_ADD_TRANSACTION);
			return false;
		}
	}
	
	
	
	protected void setDataForError(Map<String, Object> requestMap,Map<String, Object> responseMap,String wsName) {
		requestMap.put(MyPriceConstants.RESPONSE_STATUS, false);
		requestMap.put(CustomJsonConstants.ERROR_WS_NAME, wsName);
		if(responseMap.containsKey(CustomJsonConstants.REST_RESPONSE_ERROR)) {
			requestMap.put(CustomJsonConstants.REST_RESPONSE_ERROR, responseMap.get(CustomJsonConstants.REST_RESPONSE_ERROR));
		}else {
			log.error("Error while creating custom request for  REST Api for :{}",wsName);
			String defaultError=requestMap.containsKey(CustomJsonConstants.DEFAULT_CONFIG_ERROR_MSG)?
					(String) requestMap.get(CustomJsonConstants.DEFAULT_CONFIG_ERROR_MSG):CustomJsonConstants.DEFAULT_ERROR_MSG;
			requestMap.put(CustomJsonConstants.REST_RESPONSE_ERROR,new HashSet<String>(Arrays.asList(defaultError)));
		}
	}
	
	protected String  processInputDesignForPDFlow(Map<String, Object> requestMap,String inputDesign) {
		String productType=requestMap.get(MyPriceConstants.PRODUCT_TYPE)!=null?(String) requestMap.get(MyPriceConstants.PRODUCT_TYPE):"";
		String offerName = requestMap.get(MyPriceConstants.OFFER_NAME) != null? (String) requestMap.get(MyPriceConstants.OFFER_NAME): "";
		if(MyPriceConstants.SOURCE_PD.equals(productType)) {
			return configAndUpdateRestUtilPd.processInputDesign(JacksonUtil.toJsonObject(inputDesign), offerName, requestMap);
		}
		return inputDesign;
	}
	
	
	protected void getErrorMsgFromLookUp(Map<String, Object> requestMap) {
		LinkedHashMap<String,String> lookUpErrorMsg=nxMyPriceRepositoryServce.getDataFromLookup("CONFIG_ERROR_MSG");
		requestMap.put(CustomJsonConstants.CONFIG_ERROR_MSG, lookUpErrorMsg);
		requestMap.put(CustomJsonConstants.DEFAULT_CONFIG_ERROR_MSG, lookUpErrorMsg.containsKey("Default_Error")?
				lookUpErrorMsg.get("Default_Error"):"Nexxus and MyPrice had communication error");
	}
	
	protected void getProductRestMpUrlFromLookup(Map<String, Object> requestMap) {
		Map<String,String> lookupData=nxMyPriceRepositoryServce.getDescDataFromLookup("REST_URL_PRODUCT");
		requestMap.put(CustomJsonConstants.REST_URL_PRODUCTS, lookupData);
	}
	
}
