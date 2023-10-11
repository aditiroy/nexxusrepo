package com.att.sales.nexxus.myprice.transaction.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxInrDesign;
import com.att.sales.nexxus.dao.model.NxInrDesignDetails;
import com.att.sales.nexxus.dao.repository.NxInrDesignRepository;
import com.att.sales.nexxus.handlers.ConfigureDesignWSHandlerInr;
import com.att.sales.nexxus.handlers.ConfigureSolnAndProductWSHandlerInr;
import com.att.sales.nexxus.myprice.transaction.model.CreateTransactionResponse;
import com.att.sales.nexxus.util.JacksonUtil;

@Component
public class ConfigAndUpdateProcessingInrService {
	
	private static Logger logger = LoggerFactory.getLogger(ConfigAndUpdateProcessingInrService.class);

	@Autowired
	private ConfigureDesignWSHandlerInr configureDesignWSHandlerInr;
	
	@Autowired
	private ConfigureSolnAndProductWSHandlerInr configureSolnAndProductWSHandlerInr;
	
	@Autowired
	private UpdateTransactionPricingInrServiceImpl updateTransactionPricingInrService;
	
	@Autowired
	private MyPriceTransactionUtil myPriceTransactionUtil;
	
	@Autowired
	private NxInrDesignRepository nxInrDesignRepository;
	
	@Autowired
	private ConfigAndUpdatePricingUtilInr configAndUpdatePricingUtilInr;
	
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> configAndUpdatePricing(CreateTransactionResponse response, NxInrDesign nxInrDesign, Map<String, Object> paramMap ) {
		Map<String, Object> requestMap = new HashMap<String, Object>();
		Map<String, Object> responseMap = new HashMap<String, Object>();
		boolean result = false;
		
		//List<NxInrDesignDetails> nxInrDesignDetails = nxInrDesignDetailsRepository.findByNxInrDesignAndActiveYN(nxInrDesign, StringConstants.CONSTANT_Y);
	//	List<NxInrDesignDetails> nxInrDesignDetails = nxInrDesign.getNxInrDesignDetails();
		if(CollectionUtils.isNotEmpty(nxInrDesign.getNxInrDesignDetails())) {
			Map<String,List<String>> productInfoMap=
					configAndUpdatePricingUtilInr.getConfigProdutMapFromLookup(MyPriceConstants.INR_MP_PRODUCT_DATASET);
			requestMap.put(MyPriceConstants.INR_MP_PRODUCT_INFO_DATA_MAP, productInfoMap);
			requestMap.put(MyPriceConstants.MP_TRANSACTION_ID, paramMap.get(MyPriceConstants.MP_TRANSACTION_ID));
			requestMap.put(MyPriceConstants.NX_TRANSACTION_ID, paramMap.get(MyPriceConstants.NX_TRANSACTION_ID));
			requestMap.put(MyPriceConstants.NX_DESIGN_ID, nxInrDesign.getNxInrDesignId());
			requestMap.put(MyPriceConstants.NX_DESIGN, nxInrDesign);
			requestMap.put(MyPriceConstants.SOURCE, MyPriceConstants.SOURCE_INR);
			requestMap.put(MyPriceConstants.CONFIG_DESIGN_RESPONSE, paramMap.get(MyPriceConstants.CONFIG_DESIGN_RESPONSE));
			requestMap.put(MyPriceConstants.CONFIG_RESPONSE_PROCESSING_DATA, paramMap.get(MyPriceConstants.CONFIG_RESPONSE_PROCESSING_DATA));
			requestMap.put(MyPriceConstants.IS_LAST_DESIGN, paramMap.get(MyPriceConstants.IS_LAST_DESIGN));
			responseMap=this.processConfigSolAndProduct(nxInrDesign.getNxInrDesignDetails(), requestMap);
			result = responseMap.containsKey(MyPriceConstants.RESPONSE_STATUS) ?
					(boolean) responseMap.get(MyPriceConstants.RESPONSE_STATUS) : false;
			if(!result) {
				nxInrDesign.setFailureData(responseMap.toString());
				paramMap.put(MyPriceConstants.CONFIG_SOLN_DESIGN_RESPONSE, false);
			}
			responseMap.put(MyPriceConstants.TRANSACTION_TYPE, MyPriceConstants.AUDIT_CONFIG_SOLN);
			responseMap = this.processConfigDesignAndUpdate(nxInrDesign.getNxInrDesignDetails(), requestMap,responseMap);
			if(responseMap.containsKey(MyPriceConstants.CONFIG_SOLN_DESIGN_RESPONSE)) {
				paramMap.put(MyPriceConstants.CONFIG_SOLN_DESIGN_RESPONSE, responseMap.get(MyPriceConstants.CONFIG_SOLN_DESIGN_RESPONSE));
			}
			//nxInrDesign.setNxInrDesignDetails(nxInrDesignDetails);
			paramMap.put(MyPriceConstants.CURRENT_RESULT, result);
		}
		if(paramMap.containsKey(MyPriceConstants.DESIGN_DATA)) {
			boolean isLastdesign = (boolean) paramMap.get(MyPriceConstants.IS_LAST_DESIGN);
			List<NxInrDesign> designs = (List<NxInrDesign>) paramMap.get(MyPriceConstants.DESIGN_DATA);
			this.saveConfigAndUpdateStatus(result, nxInrDesign);
			designs.add(nxInrDesign);
			if(designs.size() == 20 || isLastdesign) {
				nxInrDesignRepository.saveAll(designs);
				designs.clear();
			}else {
				paramMap.put(MyPriceConstants.DESIGN_DATA, designs);
			}
		}else {
			this.saveConfigAndUpdateStatus(result, nxInrDesign);
			paramMap.put(MyPriceConstants.DESIGN_DATA, new ArrayList<NxInrDesign>(){{
				add(nxInrDesign);
			}});
		}
		paramMap.replace(MyPriceConstants.CONFIG_DESIGN_RESPONSE, requestMap.get(MyPriceConstants.CONFIG_DESIGN_RESPONSE));
		paramMap.replace(MyPriceConstants.CONFIG_RESPONSE_PROCESSING_DATA, requestMap.get(MyPriceConstants.CONFIG_RESPONSE_PROCESSING_DATA));
		return responseMap;
		
	}
	
	protected void saveConfigAndUpdateStatus(boolean result, NxInrDesign nxInrDesign) {
		if(!result) {
			nxInrDesign.setStatus(MyPriceConstants.API_FAILED);
		}else {
			nxInrDesign.setStatus(MyPriceConstants.API_SUCCEED);
		}
	//	nxInrDesignRepository.save(nxInrDesign);
	}
	
	protected void saveConfigAndUpdateStatus(Map<String, Object> responseMap, NxInrDesignDetails nxInrDesignDetails) {
		boolean result = responseMap.containsKey(MyPriceConstants.RESPONSE_STATUS) ? 
				(boolean) responseMap.get(MyPriceConstants.RESPONSE_STATUS) : false;
		if(!result) {
			nxInrDesignDetails.setStatus(MyPriceConstants.API_FAILED);
			nxInrDesignDetails.setFailureData(responseMap.toString());	
		}else {
			nxInrDesignDetails.setStatus(MyPriceConstants.API_SUCCEED);
		}
	}
	
	@SuppressWarnings("unchecked")
	protected Map<String, Object>  processConfigSolAndProduct(List<NxInrDesignDetails> nxInrDesignDetails,
			Map<String, Object> requestMap){
		JSONObject combined = new JSONObject();
		JSONArray circuit = new JSONArray();
		List<String> requestProduts = new ArrayList<String>();
		int count = 0;
		for (NxInrDesignDetails design : nxInrDesignDetails) {
			JSONObject data = JacksonUtil.toJsonObject(design.getDesignData());
			circuit.add(data);
			requestProduts.add(this.getProduct(design.getProduct()));
			if(design.getSubProduct() != null)
				requestProduts.add(design.getSubProduct());
			count++;
		}
		combined.put("circuit", circuit);
		requestMap.put(MyPriceConstants.PRODUCT_NAME, getConfigSolutionProductName(requestProduts,requestMap,count));
		requestMap.put(MyPriceConstants.NX_INR_DESIGN_DETAILS_COUNT, count);
		return this.callConfigureSolutionProduct(combined, requestMap);
	}
	
	protected String getProduct(String input) {
		/*if(MyPriceConstants.TDM_GROUP.contains(input)) {
			return MyPriceConstants.TDM;
		}
		else if(MyPriceConstants.MISPNT.equalsIgnoreCase(input)) {
			return MyPriceConstants.ADI;
		}
		else if(MyPriceConstants.GMIS.equalsIgnoreCase(input)) {
			return MyPriceConstants.ADIG;
		}else {*/
		Map<String,List<String>> productInfoMap=
		configAndUpdatePricingUtilInr.getConfigProdutMapFromLookup(MyPriceConstants.INR_PRODUCTS);
		for (Map.Entry<String,List<String>>  criteriaMap : productInfoMap.entrySet()) {
			List<String> criteria=criteriaMap.getValue();
			if(criteria.contains(input)) {
				return criteriaMap.getKey();
			}
		}

		// }
		return input;
	}

	protected String getConfigSolutionProductName(List<String> requestProduts,Map<String, Object> requestMap,int count) {
		Map<String, String> productMap = configAndUpdatePricingUtilInr
				.getConfigProdutFromLookup(MyPriceConstants.INR_CONFIG_SOL_PRODUCT_SKIP);
		if (null != productMap && CollectionUtils.isNotEmpty(requestProduts)) {
			requestMap.put(MyPriceConstants.NO_OF_PRODUCTS,count);
			for (Map.Entry<String, String> criteriaMap : productMap.entrySet()) {
				String criteria = criteriaMap.getValue();
				if (requestProduts.contains(criteria)) {
					return criteriaMap.getKey();
				}
			}
		}
		Map<String, List<String>> productInfoMap = configAndUpdatePricingUtilInr
				.getConfigProdutMapFromLookup(MyPriceConstants.INR_CONFIG_SOL_PRODUCT_DATASET);
		if (CollectionUtils.isNotEmpty(requestProduts)) {
			for (Map.Entry<String, List<String>> criteriaMap : productInfoMap.entrySet()) {
				List<String> criteria = criteriaMap.getValue();
				if (listEqualsIgnoreOrder(criteria, requestProduts)) {
					return criteriaMap.getKey();
				}
			}
			return requestProduts.stream()
	                .collect(Collectors.joining("/"));
		}
		return "";
	}
	
	
	public static boolean listEqualsIgnoreOrder(List<String> lsit1, List<String> list2) {
	    if (lsit1.size() != list2.size()) {
	        return false;
	    }
	    List<String> temp = new ArrayList<String>(list2);
	    for (String element : lsit1) {
	        if (!temp.remove(element)) {
	            return false;
	        }
	    }
	    return temp.isEmpty();
	}
	
	
	protected Map<String, Object> processConfigDesignAndUpdate(List<NxInrDesignDetails> nxInrDesignDetails,Map<String, Object> requestMap,
			Map<String, Object> responseMap) {
		  Boolean result = responseMap.containsKey(MyPriceConstants.RESPONSE_STATUS) ?
		         (boolean) responseMap.get(MyPriceConstants.RESPONSE_STATUS) : false;
		if(result) {

			for(NxInrDesignDetails design:nxInrDesignDetails) {
				requestMap.put(MyPriceConstants.OFFER_NAME, design.getProduct());
				requestMap.put(MyPriceConstants.PRODUCT_NAME, this.getProduct(design.getProduct()));
				JSONObject inputJson=JacksonUtil.toJsonObject(design.getDesignData());
				responseMap=this.callConfigureDesign(inputJson, requestMap);
				responseMap.put(MyPriceConstants.TRANSACTION_TYPE, MyPriceConstants.AUDIT_CONFIG_DESIGN);
				result = responseMap.containsKey(MyPriceConstants.RESPONSE_STATUS) ?
						(boolean) responseMap.get(MyPriceConstants.RESPONSE_STATUS) : false;
				if(result) {
					//responseMap = this.callUpdatePricing(inputJson, requestMap);
					if(design.getSubProduct() != null) {
						requestMap.put(MyPriceConstants.PRODUCT_NAME, this.getProduct(design.getSubProduct()));
						responseMap=this.callConfigureDesign(inputJson, requestMap);
						responseMap.put(MyPriceConstants.TRANSACTION_TYPE, MyPriceConstants.AUDIT_CONFIG_DESIGN);
						result = responseMap.containsKey(MyPriceConstants.RESPONSE_STATUS) ?
								(boolean) responseMap.get(MyPriceConstants.RESPONSE_STATUS) : false;
						if(!result) {
							//persist failure data
							this.saveConfigAndUpdateStatus(responseMap, design);
							responseMap.put(MyPriceConstants.CONFIG_SOLN_DESIGN_RESPONSE, false);
						}
					}
				
					responseMap.put(MyPriceConstants.TRANSACTION_TYPE, MyPriceConstants.AUDIT_UPDATE_PR);
				}else {
					responseMap.put(MyPriceConstants.CONFIG_SOLN_DESIGN_RESPONSE, false);
				}
				this.saveConfigAndUpdateStatus(responseMap, design);
			}
		}else {
			responseMap.put(MyPriceConstants.CONFIG_SOLN_DESIGN_RESPONSE, false);
		}
		return responseMap;
		
	}
	
	
	public Map<String, Object> callConfigureSolutionProduct(JSONObject inputJson,Map<String, Object> requestMap) {
		logger.info("Start -- callConfigureSolutionProduct");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			Boolean isSuccessful = configureSolnAndProductWSHandlerInr.initiateConfigSolnAndProdWebService(inputJson,requestMap);
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
			myPriceTransactionUtil.copyTime(requestMap, responseMap);
			return responseMap;
		}
		responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
		myPriceTransactionUtil.copyTime(requestMap, responseMap);
		logger.info("End -- callConfigureSolutionProduct");
		return responseMap;
	}
	
	
	public Map<String, Object> callConfigureDesign(JSONObject inputJson,Map<String, Object> requestMap) {
		logger.info("Start -- callConfigureDesign");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			Boolean isSuccessful = configureDesignWSHandlerInr.initiateConfigDesignWebService(inputJson,requestMap);
			if(!isSuccessful) {
				responseMap.put(MyPriceConstants.RESPONSE_STATUS, false);
				myPriceTransactionUtil.prepareResponseMap(requestMap, responseMap);
				return responseMap;
			}
		} catch (SalesBusinessException e) {
			logger.info("Error during callConfigureDesign {}", e.getMessage());
			responseMap.put(MyPriceConstants.RESPONSE_STATUS, false);
			responseMap.put(MyPriceConstants.RESPONSE_MSG, e.getMessage());
			responseMap.put(MyPriceConstants.RESPONSE_CODE, e.getHttpErrorCode());
			myPriceTransactionUtil.copyTime(requestMap, responseMap);
			return responseMap;
		}
		responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
		myPriceTransactionUtil.copyTime(requestMap, responseMap);
		logger.info("End -- callConfigureDesign");
		return responseMap;
	}
	
	
	public Map<String, Object> callUpdatePricing(JSONObject inputJson,Map<String, Object> requestMap) {
		logger.info("Start -- callUpdatePricing");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
		
			updateTransactionPricingInrService.updateTransactionPricingRequest(requestMap,inputJson);
			boolean status = (requestMap.containsKey(MyPriceConstants.RESPONSE_STATUS) &&
					requestMap.get(MyPriceConstants.RESPONSE_STATUS) != null) ? (boolean) requestMap.get(MyPriceConstants.RESPONSE_STATUS) : false;
					myPriceTransactionUtil.prepareResponseMap(requestMap, responseMap);
			if(status) {
				responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
			}else {
				responseMap.put(MyPriceConstants.RESPONSE_STATUS, false);
			}
		} catch (SalesBusinessException e) {
			logger.info("Error during callUpdatePricing {}", e.getMessage());
			responseMap.put(MyPriceConstants.RESPONSE_STATUS, false);
			responseMap.put(MyPriceConstants.RESPONSE_MSG, e.getMessage());
			responseMap.put(MyPriceConstants.RESPONSE_CODE, e.getHttpErrorCode());
			return responseMap;
		}
		logger.info("End -- callUpdatePricing");
		return responseMap;
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

}
