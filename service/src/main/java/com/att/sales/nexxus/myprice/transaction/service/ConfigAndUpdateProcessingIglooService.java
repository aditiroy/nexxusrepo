package com.att.sales.nexxus.myprice.transaction.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxAccessPricingData;
import com.att.sales.nexxus.dao.model.NxInrDesign;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.repository.NxAccessPricingDataRepository;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.handlers.ConfigureDesignWSHandlerIgloo;
import com.att.sales.nexxus.handlers.ConfigureSolnAndProductWSHandlerIgloo;
import com.att.sales.nexxus.myprice.transaction.model.CreateTransactionResponse;
import com.att.sales.nexxus.util.JacksonUtil;

@Component
public class ConfigAndUpdateProcessingIglooService {
	
	private static Logger logger = LoggerFactory.getLogger(ConfigAndUpdateProcessingIglooService.class);

	@Autowired
	private ConfigureDesignWSHandlerIgloo configureDesignWSHandlerIgloo;
	
	@Autowired
	private ConfigureSolnAndProductWSHandlerIgloo configureSolnAndProductWSHandlerIgloo;
	
	@Autowired
	private MyPriceTransactionUtil myPriceTransactionUtil;
	
	@Autowired
	private NxMpDealRepository nxMpDealRepository;
	
	@Autowired
	private NxAccessPricingDataRepository accessPricingDataRepository;
	
	
	public Map<String, Object> callConfigSolutionAndDesign(CreateTransactionResponse response,NxAccessPricingData accessPricingData,Map<String, Object> paramMap  ) {
		JSONObject data = JacksonUtil.toJsonObject(accessPricingData.getIntermediateJson());
		String countryIsoCode = accessPricingData.getCustCountry();
		data.put("country", countryIsoCode);
		//Long nxSiteId = paramMap.containsKey(MyPriceConstants.NX_SITE_ID)? (Long)paramMap.get(MyPriceConstants.NX_SITE_ID): 0L;
		//data.put("nxSiteId", nxSiteId);
		String locationYn = accessPricingData.getLocationYn();
		if(Optional.ofNullable(locationYn).isPresent()) {
			data.put("portQtyPf", locationYn);
		} else {
			data.put("portQtyPf", "New");
		}
		if(null != countryIsoCode && countryIsoCode.trim().equalsIgnoreCase("US")) {
			data.put("accessArch", null!=data.get("accessArch")? data.get("accessArch").toString().toUpperCase():"");
			data.put("reqAccessBandwidth", null!=data.get("reqAccessBandwidth")?data.get("reqAccessBandwidth").toString()+" Mbps":"");
			return configSolutionAndDesignEthernet(response,data,paramMap,accessPricingData);
		} else {
			return configSolutionAndDesignInternationlAccess(response,data,paramMap,accessPricingData);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> configSolutionAndDesignEthernet(CreateTransactionResponse response,JSONObject data,Map<String, Object> paramMap,NxAccessPricingData accessPricingData ) {
		Map<String, Object> requestMap = new HashMap<String, Object>();
		Map<String, Object> responseMap = new HashMap<String, Object>();
		boolean result = false;
			requestMap.clear();
			requestMap.put(MyPriceConstants.MP_TRANSACTION_ID, response.getMyPriceTransacId());
			requestMap.put(MyPriceConstants.NX_TRANSACTION_ID, response.getNxTransacId());
			requestMap.put(MyPriceConstants.NX_ACCESS_PRICE_ID, accessPricingData.getNxAccessPriceId());
			requestMap.put(MyPriceConstants.NX_SOLIUTION_ID, accessPricingData.getNxSolutionId());
			requestMap.put(MyPriceConstants.SOURCE, MyPriceConstants.SOURCE_IGLOO);
			requestMap.put(MyPriceConstants.PRODUCT_TYPE, MyPriceConstants.PRODUCT_TYPE_ETH);
			requestMap.put(MyPriceConstants.OFFER_NAME, response.getOfferName());
			requestMap.put(MyPriceConstants.NX_ACCESS_PRICING_DATA, accessPricingData);
			responseMap = this.callConfigureSolutionProduct(data,requestMap);
			result = responseMap.containsKey(MyPriceConstants.RESPONSE_STATUS) ? 
					(boolean) responseMap.get(MyPriceConstants.RESPONSE_STATUS) : false;
			responseMap.put(MyPriceConstants.TRANSACTION_TYPE, MyPriceConstants.AUDIT_CONFIG_SOLN);
			if(result) {
				requestMap.clear();
				requestMap.put(MyPriceConstants.MP_TRANSACTION_ID, response.getMyPriceTransacId());
				requestMap.put(MyPriceConstants.NX_TRANSACTION_ID, response.getNxTransacId());
				requestMap.put(MyPriceConstants.NX_ACCESS_PRICE_ID, accessPricingData.getNxAccessPriceId());
				requestMap.put(MyPriceConstants.NX_SOLIUTION_ID, accessPricingData.getNxSolutionId());
				requestMap.put(MyPriceConstants.SOURCE, MyPriceConstants.SOURCE_IGLOO);
				requestMap.put(MyPriceConstants.PRODUCT_TYPE, MyPriceConstants.PRODUCT_TYPE_ETH);
				requestMap.put(MyPriceConstants.OFFER_NAME, response.getOfferName());
				requestMap.put(MyPriceConstants.NX_ACCESS_PRICING_DATA, accessPricingData);
				requestMap.put(MyPriceConstants.CONFIG_DESIGN_RESPONSE, paramMap.get(MyPriceConstants.CONFIG_DESIGN_RESPONSE));
				requestMap.put(MyPriceConstants.CONFIG_RESPONSE_PROCESSING_DATA, paramMap.get(MyPriceConstants.CONFIG_RESPONSE_PROCESSING_DATA));
				requestMap.put(MyPriceConstants.IS_LAST_DESIGN, paramMap.get(MyPriceConstants.IS_LAST_DESIGN));
				responseMap = this.callConfigureDesign(data,requestMap);
				responseMap.put(MyPriceConstants.TRANSACTION_TYPE, MyPriceConstants.AUDIT_CONFIG_DESIGN);
			}else {
				paramMap.put(MyPriceConstants.CONFIG_SOLN_DESIGN_RESPONSE, false);
			}
			result = responseMap.containsKey(MyPriceConstants.RESPONSE_STATUS) ? 
					(boolean) responseMap.get(MyPriceConstants.RESPONSE_STATUS) : false;
			if(!result) {
				paramMap.put(MyPriceConstants.CONFIG_SOLN_DESIGN_RESPONSE, false);
			}
			paramMap.replace(MyPriceConstants.CONFIG_DESIGN_RESPONSE,
					requestMap.get(MyPriceConstants.CONFIG_DESIGN_RESPONSE));
			paramMap.replace(MyPriceConstants.CONFIG_RESPONSE_PROCESSING_DATA,
					requestMap.get(MyPriceConstants.CONFIG_RESPONSE_PROCESSING_DATA));
			paramMap.put(MyPriceConstants.CURRENT_RESULT, result);
		return responseMap;
	}
		
	@SuppressWarnings("unchecked")
	public Map<String, Object> configSolutionAndDesignInternationlAccess(CreateTransactionResponse response,JSONObject data,Map<String, Object> paramMap,NxAccessPricingData accessPricingData  ) {
		Map<String, Object> requestMap = new HashMap<String, Object>();
		Map<String, Object> responseMap = new HashMap<String, Object>();
		boolean result = false;
			requestMap.clear();
			requestMap.put(MyPriceConstants.MP_TRANSACTION_ID, response.getMyPriceTransacId());
			requestMap.put(MyPriceConstants.NX_TRANSACTION_ID, response.getNxTransacId());
			requestMap.put(MyPriceConstants.NX_ACCESS_PRICE_ID, accessPricingData.getNxAccessPriceId());
			requestMap.put(MyPriceConstants.NX_SOLIUTION_ID, accessPricingData.getNxSolutionId());
			requestMap.put(MyPriceConstants.PRODUCT_TYPE, MyPriceConstants.SOURCE_INTERNATIONAL_ACCESS);
			requestMap.put(MyPriceConstants.SOURCE, MyPriceConstants.SOURCE_IGLOO);
			requestMap.put(MyPriceConstants.OFFER_NAME, response.getOfferName());
			requestMap.put(MyPriceConstants.NX_ACCESS_PRICING_DATA, accessPricingData);
			responseMap = this.callConfigureSolutionProduct(data,requestMap);
			result = responseMap.containsKey(MyPriceConstants.RESPONSE_STATUS) ? 
					(boolean) responseMap.get(MyPriceConstants.RESPONSE_STATUS) : false;
			responseMap.put(MyPriceConstants.TRANSACTION_TYPE, MyPriceConstants.AUDIT_CONFIG_SOLN);
			if(result) {
				requestMap.clear();
				requestMap.put(MyPriceConstants.MP_TRANSACTION_ID, response.getMyPriceTransacId());
				requestMap.put(MyPriceConstants.NX_TRANSACTION_ID, response.getNxTransacId());
				requestMap.put(MyPriceConstants.NX_ACCESS_PRICE_ID, accessPricingData.getNxAccessPriceId());
				requestMap.put(MyPriceConstants.NX_SOLIUTION_ID, accessPricingData.getNxSolutionId());
				requestMap.put(MyPriceConstants.PRODUCT_TYPE, MyPriceConstants.SOURCE_INTERNATIONAL_ACCESS);
				requestMap.put(MyPriceConstants.SOURCE, MyPriceConstants.SOURCE_IGLOO);
				requestMap.put(MyPriceConstants.OFFER_NAME, response.getOfferName());
				requestMap.put(MyPriceConstants.NX_ACCESS_PRICING_DATA, accessPricingData);
				requestMap.put(MyPriceConstants.CONFIG_DESIGN_RESPONSE, paramMap.get(MyPriceConstants.CONFIG_DESIGN_RESPONSE));
				requestMap.put(MyPriceConstants.CONFIG_RESPONSE_PROCESSING_DATA, paramMap.get(MyPriceConstants.CONFIG_RESPONSE_PROCESSING_DATA));
				requestMap.put(MyPriceConstants.IS_LAST_DESIGN, paramMap.get(MyPriceConstants.IS_LAST_DESIGN));
				responseMap = this.callConfigureDesign(data,requestMap);
				responseMap.put(MyPriceConstants.TRANSACTION_TYPE, MyPriceConstants.AUDIT_CONFIG_DESIGN);
			}else {
				paramMap.put(MyPriceConstants.CONFIG_SOLN_DESIGN_RESPONSE, false);
			}
			result = responseMap.containsKey(MyPriceConstants.RESPONSE_STATUS) ? 
					(boolean) responseMap.get(MyPriceConstants.RESPONSE_STATUS) : false;
			if(!result) {
				paramMap.put(MyPriceConstants.CONFIG_SOLN_DESIGN_RESPONSE, false);
			}
			paramMap.replace(MyPriceConstants.CONFIG_DESIGN_RESPONSE,
					requestMap.get(MyPriceConstants.CONFIG_DESIGN_RESPONSE));
			paramMap.replace(MyPriceConstants.CONFIG_RESPONSE_PROCESSING_DATA, 
					requestMap.get(MyPriceConstants.CONFIG_RESPONSE_PROCESSING_DATA));
			paramMap.put(MyPriceConstants.CURRENT_RESULT, result);
		return responseMap;
	}

	public Map<String, Object> callConfigureSolutionProduct(JSONObject inputJson,Map<String, Object> requestMap) {
		logger.info("Start -- callConfigureSolutionProduct");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			Boolean isSuccessful = configureSolnAndProductWSHandlerIgloo.initiateConfigSolnAndProdWebService(inputJson,requestMap);
			saveConfigAndUpdateStatus(isSuccessful,requestMap);
			logger.info("callConfigureSolutionProduct igloo isSuccessful : "+isSuccessful);
			if(!isSuccessful) {
				myPriceTransactionUtil.prepareResponseMap(requestMap, responseMap);
				responseMap.put(MyPriceConstants.RESPONSE_STATUS, false);
				return responseMap;
			}
		} catch (SalesBusinessException e) {
			logger.info("Error during callConfigureSolutionProduct {}", e.getMessage());
			responseMap.put(MyPriceConstants.RESPONSE_STATUS, false);
			responseMap.put(MyPriceConstants.RESPONSE_MSG, e.getMessage());
			responseMap.put(MyPriceConstants.RESPONSE_CODE, e.getHttpErrorCode());
			return responseMap;
		}
		responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
		logger.info("End -- callConfigureSolutionProduct");
		return responseMap;
	}
	
	
	public Map<String, Object> callConfigureDesign(JSONObject inputJson,Map<String, Object> requestMap) {
		logger.info("Start -- callConfigureDesign");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			Boolean isSuccessful = configureDesignWSHandlerIgloo.initiateConfigDesignWebService(inputJson,requestMap);
			saveConfigAndUpdateStatus(isSuccessful,requestMap);
			logger.info("callConfigureDesign igloo isSuccessful : "+isSuccessful);
			if(!isSuccessful) {
				myPriceTransactionUtil.prepareResponseMap(requestMap, responseMap);
				return responseMap;
			}
		} catch (SalesBusinessException e) {
			logger.info("Error during callConfigureDesign {}", e.getMessage());
			responseMap.put(MyPriceConstants.RESPONSE_STATUS, false);
			responseMap.put(MyPriceConstants.RESPONSE_MSG, e.getMessage());
			responseMap.put(MyPriceConstants.RESPONSE_CODE, e.getHttpErrorCode());
			return responseMap;
		}
		responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
		logger.info("End -- callConfigureDesign");
		return responseMap;
	}
	
	protected void saveConfigAndUpdateStatus(boolean result,Map<String, Object> requestMap) {
		Long nxAccesspriceId = requestMap.containsKey(MyPriceConstants.NX_ACCESS_PRICE_ID) ? (Long)requestMap.get(MyPriceConstants.NX_ACCESS_PRICE_ID) : 0L;
		String status = (result) ? MyPriceConstants.API_SUCCEED : MyPriceConstants.API_FAILED;
		if(Optional.ofNullable(nxAccesspriceId).isPresent()) {
			NxAccessPricingData nxAccessPricingData = accessPricingDataRepository.findByNxAccessPriceId(nxAccesspriceId);
			nxAccessPricingData.setMpStatus(status);
			accessPricingDataRepository.saveAndFlush(nxAccessPricingData);
		}
	}
	
	public String getProductName(Map<String,List<String>> configProductInfoMap,String inputProductName) {
		if(MapUtils.isNotEmpty(configProductInfoMap) && StringUtils.isNotEmpty(inputProductName)) {
			for (Map.Entry<String,List<String>>  usocCriteria : configProductInfoMap.entrySet()) {
				List<String> criteria=usocCriteria.getValue();
				if(criteria.contains(inputProductName)) {
					return usocCriteria.getKey();
				}
			}
		}
		return "";
	}
	
	protected NxMpDeal getDealBySolutionId(Long nxSolutionId) {
		List<NxMpDeal> deals = nxMpDealRepository.findBySolutionIdAndActiveYN(nxSolutionId, StringConstants.CONSTANT_Y);
		if(CollectionUtils.isNotEmpty(deals)) {
			return deals.get(0);
		}
		return null;
	}
	
}
