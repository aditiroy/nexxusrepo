package com.att.sales.nexxus.rest.handlers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.CustomJsonConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdateRestUtilFmo;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdateRestUtilInr;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdateRestUtilPd;
import com.att.sales.nexxus.util.JacksonUtil;

@Component("configDesignUpdateRestHandler")
public class ConfigDesignUpdateRestHandler extends ConfigRestHandler{

	private static final Logger log = LoggerFactory.getLogger(ConfigDesignUpdateRestHandler.class);
	
	private static final String PART_FIELD3_PATH="$..items.*.[?(@._part_custom_field3!='' && @._part_custom_field3!=null )]._part_custom_field3";
	private static final String SITE_CONFIG_ERROR_PATH="$.configData.siteConfigurationError_pf";
	
	@Autowired
	private ConfigAndUpdateRestUtilInr configAndUpdateRestUtilInr;
	
	@Autowired
	private ConfigAndUpdateRestUtilPd configAndUpdateRestUtilPd;
	
	@Autowired
	private ConfigAndUpdateRestUtilFmo configAndUpdateRestUtilFmo;

	@PostConstruct
	protected void init() {
		setWsName(CustomJsonConstants.CONFIG_DESIGN_UPDATE);
	}
	@Override
	public Map<String, Object> process(Map<String, Object> requestMap, String inputDesign){
		log.info("Inside process for ConfigDesignUpdateRestHandler","");
		Map<String, Object> responseMap =new HashMap<String, Object>();
		responseMap.put(MyPriceConstants.RESPONSE_STATUS, false);
		try {
			if(StringUtils.isNotEmpty(inputDesign)) {
				requestMap.put(CustomJsonConstants.RULE_TYPE, CustomJsonConstants.CONFIG_DESIGN_UPDATE);
				this.processConfigReqDataFromCustomRules(requestMap, inputDesign);
				String request=getCustomJsonProcessingUtil().createJsonString(requestMap, inputDesign);
				triggerRestClient(request, getUrl(requestMap),CommonConstants.POST_METHOD, requestMap,responseMap);
				processConfigDesignUpdateResponse(requestMap, responseMap, inputDesign);
			}
		}catch(SalesBusinessException se) {
			log.error("Exception during REST call: {}", se.getMessage());
		}catch(Exception e) {
			log.error("Exception during REST call: {}", e.getMessage());
		}
		return responseMap;
	}
	
	
	protected void processConfigReqDataFromCustomRules(Map<String, Object> requestMap, String inputDesign) {
		String productType=requestMap.get(MyPriceConstants.PRODUCT_TYPE)!=null?requestMap.get(MyPriceConstants.PRODUCT_TYPE).toString():null;
		if(MyPriceConstants.SOURCE_INR.equals(productType) || MyPriceConstants.SOURCE_USRP.equals(productType)) {
			configAndUpdateRestUtilInr.processConfigDataFromCustomeRules(requestMap, inputDesign);
		}else if(MyPriceConstants.SOURCE_PD.equals(productType)) {
			configAndUpdateRestUtilPd.processConfigDataFromCustomeRules(requestMap, inputDesign);
		}else if(MyPriceConstants.SOURCE_FMO.equals(productType)) {
			configAndUpdateRestUtilFmo.processConfigDataFromCustomeRules(requestMap, inputDesign);
			configAndUpdateRestUtilFmo.processCustomeFieldsUsingNxLookupData(requestMap, inputDesign);
		}
		
	}
	
	
	
	@SuppressWarnings("unchecked")
	public void processConfigDesignUpdateResponse(Map<String, Object> requestMap, Map<String, Object> responseMap,
			String inputDesign) {
		String offerName = requestMap.get(MyPriceConstants.OFFER_NAME) != null? (String) requestMap.get(MyPriceConstants.OFFER_NAME): "";
		String productType=requestMap.get(MyPriceConstants.PRODUCT_TYPE)!=null?(String) requestMap.get(MyPriceConstants.PRODUCT_TYPE):"";
		if(responseMap.containsKey(MyPriceConstants.RESPONSE_STATUS) && (Boolean)responseMap.get(MyPriceConstants.RESPONSE_STATUS)) {
			String restResponse = (responseMap.containsKey(MyPriceConstants.RESPONSE_DATA) 
					&& responseMap.get(MyPriceConstants.RESPONSE_DATA) != null) ? 
							(String) responseMap.get(MyPriceConstants.RESPONSE_DATA) : null;
			if(StringUtils.isNotEmpty(restResponse)) {
				if(MyPriceConstants.SOURCE_PD.equals(productType)) {
					//code required if needed
					
					this.processSiteConigErrorFromResponse(restResponse, responseMap);
				}else {
					Set<String> bomResponseErrors=geDataInString(restResponse,PART_FIELD3_PATH);
					LinkedHashMap<String,String> errorMessageMap=requestMap.containsKey(CustomJsonConstants.CONFIG_ERROR_MSG)?
							(LinkedHashMap<String,String>)requestMap.get(CustomJsonConstants.CONFIG_ERROR_MSG):new LinkedHashMap<String,String>() ;
					if(MyPriceConstants.AVPN.equals(offerName) || MyPriceConstants.ADI.equals(offerName) 
							|| MyPriceConstants.ANIRA.equals(offerName) || MyPriceConstants.ADIG.equals(offerName)) {
						Set<String> requestElementType=null!= requestMap.get(CustomJsonConstants.ELEMENT_TYPE)?
								(Set<String>) requestMap.get(CustomJsonConstants.ELEMENT_TYPE):new HashSet<String>();
						Set<String> resultitem=compareReqAndRespElementType(requestElementType, bomResponseErrors);
						if(CollectionUtils.isNotEmpty(resultitem)) {
							Set<String> errorMessages=errorMessageMap.entrySet() 
					        .stream()
					        .filter(entry -> resultitem.contains(entry.getKey()))
					        .map(Entry::getValue)
					        .collect(Collectors.toCollection(HashSet::new));
							log.error("Error during configDesignUpdate from Mp: {} " +errorMessages);
							responseMap.put(MyPriceConstants.RESPONSE_STATUS, false);
							responseMap.put(CustomJsonConstants.CONFIG_BOM_ERROR, true);
							responseMap.put(CustomJsonConstants.CONFIG_BOM_ERROR_DATA, errorMessages);
						}else {
							responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
						}
					}else if (MyPriceConstants.ONENET_FEATURE.equals(offerName)) {
					 	if (CollectionUtils.isNotEmpty(bomResponseErrors) && bomResponseErrors.contains("VoiceDataFeatures")) {
							 responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
						} else { 
							log.error("Error during configDesignUpdate from Mp: {} " + errorMessageMap.get(offerName)); 
							responseMap.put(MyPriceConstants.RESPONSE_STATUS, false); 
							responseMap.put(CustomJsonConstants.CONFIG_BOM_ERROR, true); 
							responseMap.put(CustomJsonConstants.CONFIG_BOM_ERROR_DATA, new HashSet<String>(Arrays.asList(errorMessageMap.get(offerName))));
						 }
					} else if (MyPriceConstants.VTNS.equals(offerName) || MyPriceConstants.ONENET.equals(offerName)
							|| MyPriceConstants.ABN.equals(offerName)) {
						if (CollectionUtils.isNotEmpty(bomResponseErrors) && bomResponseErrors.contains("VoiceData")) {
							responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
						} else {
							log.error("Error during configDesignUpdate from Mp: {} "
									+ errorMessageMap.get(offerName));
							responseMap.put(MyPriceConstants.RESPONSE_STATUS, false);
							responseMap.put(CustomJsonConstants.CONFIG_BOM_ERROR, true);
							responseMap.put(CustomJsonConstants.CONFIG_BOM_ERROR_DATA,
									new HashSet<String>(Arrays.asList(errorMessageMap.get(offerName))));
						}
					}else if(MyPriceConstants.SOURCE_INR.equals(productType) && MyPriceConstants.BVoIP.equals(offerName)){
						if (CollectionUtils.isNotEmpty(bomResponseErrors) && bomResponseErrors.contains("Usage")) {
							responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
						} else {
							log.error("Error during configDesignUpdate from Mp: {} "
									+ errorMessageMap.get(offerName));
							responseMap.put(MyPriceConstants.RESPONSE_STATUS, false);
							responseMap.put(CustomJsonConstants.CONFIG_BOM_ERROR, true);
							responseMap.put(CustomJsonConstants.CONFIG_BOM_ERROR_DATA,
									new HashSet<String>(Arrays.asList(errorMessageMap.get(offerName))));
						}
					}else {
				
						//for access product
						if(CollectionUtils.isNotEmpty(bomResponseErrors)) {
							responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
						}else {
							log.error("Error during configDesignUpdate from Mp: {} " + errorMessageMap.get("Others"));
							responseMap.put(MyPriceConstants.RESPONSE_STATUS, false);
							responseMap.put(CustomJsonConstants.CONFIG_BOM_ERROR, true);
							responseMap.put(CustomJsonConstants.CONFIG_BOM_ERROR_DATA,  
									new HashSet<String>(Arrays.asList(errorMessageMap.get("Others"))));
						}
					}
					
					this.processSiteConigErrorFromResponse(restResponse, responseMap);
				}
				
			}			
							
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void processSiteConigErrorFromResponse(String restResponse,Map<String, Object> responseMap) {
		Map<String,Set<String>> siteConfigErrorMap=new HashMap<String, Set<String>>();
		Object siteConfigError=getNexxusJsonUtility().getValue(restResponse, SITE_CONFIG_ERROR_PATH);
		if(null!=siteConfigError) {
			log.error("Site Configuration Error during configDesignUpdate  : {}",siteConfigError);
			JSONArray errors=JacksonUtil.toJsonArray(siteConfigError.toString());
			if(null!=errors  && !errors.isEmpty()) {
				Optional.ofNullable(errors).ifPresent(i -> i.forEach(e -> {
					JSONObject errorObj = (JSONObject) e;
					String siteId=null!=errorObj.get("nxSiteId")?(String) errorObj.get("nxSiteId"):null;
					String errorMsg=null!=errorObj.get("Error")?(String) errorObj.get("Error"):null;
					if(!siteConfigErrorMap.containsKey(siteId)) {
						siteConfigErrorMap.put(siteId, new HashSet<String>());
					}
					siteConfigErrorMap.get(siteId).add(errorMsg);
				}));
				responseMap.put(CustomJsonConstants.SITE_CONFIG_ERROR, true);
				responseMap.put(CustomJsonConstants.SITE_CONFIG_ERROR_MAP, siteConfigErrorMap);
				responseMap.put(MyPriceConstants.RESPONSE_STATUS, false);
			}
			
		}
		
	}
	
	protected Set<String> geDataInString(Object input,String path){
		List<Object> result=getNexxusJsonUtility().getValueLst(input,path);
		return Optional.ofNullable(result).map(List::stream).orElse(Stream.empty())
		   .map(object -> Objects.toString(object, null))
		   .collect(Collectors.toSet());
	}
	
	
	protected Set<String> compareReqAndRespElementType(Set<String> list1, Set<String> list2){
		if(getRestCommonUtil().listEqualsIgnoreOrder(list1, list2)) {
			return  null;
		}else {
			list1.removeIf(list2::contains);
			return list1;
		}
	}
	

}
