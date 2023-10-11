package com.att.sales.nexxus.rest.handlers;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.CustomJsonConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;

@Component("configAddTransactionRestHandler")
public class ConfigAddTransactionRestHandler extends ConfigRestHandler{
	
	private static final Logger log = LoggerFactory.getLogger(ConfigAddTransactionRestHandler.class);

	@PostConstruct
	protected void init() {
		setWsName(CustomJsonConstants.CONFIG_ADD_TRANSACTION);
	}

	@Override
	public Map<String, Object>  process(Map<String, Object> requestMap, String inputDesign){
		log.info("Inside process for ConfigAddTransactionRestHandler","");
		Map<String, Object> responseMap =new HashMap<String, Object>();
		responseMap.put(MyPriceConstants.RESPONSE_STATUS, false);
		try {
			if(StringUtils.isNotEmpty(inputDesign)) {
				Map<String, Object> inputMap=new HashMap<String, Object>();
				inputMap.put(MyPriceConstants.PRODUCT_TYPE, "*");
				inputMap.put(MyPriceConstants.OFFER_NAME, "*");
				inputMap.put(MyPriceConstants.SUB_OFFER_NAME, "");
				inputMap.put(CustomJsonConstants.RULE_TYPE, CustomJsonConstants.CONFIG_ADD_TRANSACTION);
				inputMap.put(CustomJsonConstants.CACHE_INSTANCE_ID,requestMap.get(CustomJsonConstants.CACHE_INSTANCE_ID));
				String request=getCustomJsonProcessingUtil().createJsonString(inputMap, inputDesign);
				triggerRestClient(request, getUrl(requestMap),CommonConstants.POST_METHOD, requestMap,responseMap);
			}
			
		}catch(SalesBusinessException se) {
			log.error("Exception during REST call: {}", se.getMessage());
		}catch(Exception e) {
			log.error("Exception during REST call: {}", e.getMessage());
		}
		return responseMap;
	}

}
