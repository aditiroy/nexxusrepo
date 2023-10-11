package com.att.sales.nexxus.myprice.transaction.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.FmoConstants;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxDesign;
import com.att.sales.nexxus.dao.model.NxDesignDetails;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.handlers.ConfigureDesignWSHandlerFmo;
import com.att.sales.nexxus.handlers.ConfigureSolnAndProductWSHandlerFmo;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class ConfigAndUpdateProcessingFmoService {
	private static final Logger log = LoggerFactory.getLogger(ConfigAndUpdateProcessingFmoService.class);
	@Autowired
	private MyPriceTransactionUtil myPriceTransactionUtil;
	
	@Autowired
	private NxMpDealRepository nxMpDealRepository;
	
	@Autowired
	private ConfigureSolnAndProductWSHandlerFmo configSolutionHandler;
	
	@Autowired
	private ConfigureDesignWSHandlerFmo configureDesignHandler;
	
	
	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Autowired
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	
	

	@SuppressWarnings("unchecked")
	public Map<String, Object> callConfigSolutionAndDesign(NxDesign nxDesign,
			Map<String, Object> paramMap ) {
		Map<String, Object> requestMap = new HashMap<String, Object>();
		Map<String, Object> responseMap = new HashMap<String, Object>();
		boolean result = false;
		List<NxDesignDetails> nxDesignDetails=nxDesign.getNxDesignDetails();
		if(CollectionUtils.isNotEmpty(nxDesignDetails)) {
			requestMap.clear();
			if(paramMap.containsKey(InrConstants.REQUEST_META_DATA_KEY)) {
				requestMap.put(InrConstants.REQUEST_META_DATA_KEY, paramMap.get(InrConstants.REQUEST_META_DATA_KEY));
			}
			requestMap.put(MyPriceConstants.MP_TRANSACTION_ID,  paramMap.get(MyPriceConstants.MP_TRANSACTION_ID));
			requestMap.put(MyPriceConstants.NX_TRANSACTION_ID,  paramMap.get(MyPriceConstants.NX_TRANSACTION_ID));
			requestMap.put(MyPriceConstants.CONFIG_DESIGN_RESPONSE, paramMap.get(MyPriceConstants.CONFIG_DESIGN_RESPONSE));
			requestMap.put(MyPriceConstants.CONFIG_RESPONSE_PROCESSING_DATA, paramMap.get(MyPriceConstants.CONFIG_RESPONSE_PROCESSING_DATA));
			requestMap.put(MyPriceConstants.OFFER_NAME, nxDesign.getBundleCd());
			requestMap.put(MyPriceConstants.NX_DESIGN, nxDesign);
			requestMap.put(MyPriceConstants.NX_DESIGN_ID, nxDesign.getNxDesignId());
			requestMap.put(MyPriceConstants.SOURCE, MyPriceConstants.SOURCE_FMO);
			requestMap.put(MyPriceConstants.IS_LAST_DESIGN, paramMap.get(MyPriceConstants.IS_LAST_DESIGN));
			//setting productModel_pm for configSolution and product request
			String productModel=getProductModelNameForCongSolution(nxDesignDetails);
			Integer noOfSearchSite=getNoOfSearchSite(productModel);
			requestMap.put(MyPriceConstants.PRODUCT_MODEL,productModel);
			if(null!=noOfSearchSite) {
				requestMap.put(MyPriceConstants.NO_OF_SITE_SEARCH,noOfSearchSite);
				requestMap.put(MyPriceConstants.NO_OF_PRODUCTS,noOfSearchSite);
			}
			
			if(null!=nxDesignDetails.get(0) && StringUtils.isNotEmpty(nxDesignDetails.get(0).getDesignData())) {
				requestMap.put(MyPriceConstants.OFFER_NAME, getOfferNameForConfigSol(nxDesign.getBundleCd()));
				responseMap = this.callConfigureSolutionProduct(nxDesignDetails.get(0).getDesignData(),requestMap);
				result = responseMap.containsKey(MyPriceConstants.RESPONSE_STATUS) ? 
						(boolean) responseMap.get(MyPriceConstants.RESPONSE_STATUS) : false;
			}
			responseMap.put(MyPriceConstants.TRANSACTION_TYPE, MyPriceConstants.AUDIT_CONFIG_SOLN);
			for(NxDesignDetails obj:nxDesignDetails) {
				//creating product list for access scenario to call configDesign
				List<String> products=getProductsNameListForConfigDesign(obj,requestMap);
				for(String product:products) {
					if(result) {
						requestMap.put(MyPriceConstants.OFFER_NAME, product);
						requestMap.put(StringConstants.PRICE_SCENARIO_ID, paramMap.get(StringConstants.PRICE_SCENARIO_ID));
						requestMap.put(FmoConstants.PORT_ID, obj.getComponentId());
						responseMap = this.callConfigureDesign(obj.getDesignData(),requestMap);
						responseMap.put(MyPriceConstants.TRANSACTION_TYPE, MyPriceConstants.AUDIT_CONFIG_DESIGN);
					}
					result = responseMap.containsKey(MyPriceConstants.RESPONSE_STATUS) ? 
							(boolean) responseMap.get(MyPriceConstants.RESPONSE_STATUS) : false;
				}
				
/*				if (result) {
					Map<String, Object> requestPriceMap = new HashMap<String, Object>();
					if(requestMap.containsKey(InrConstants.REQUEST_META_DATA_KEY)) {
						requestPriceMap.put(InrConstants.REQUEST_META_DATA_KEY, requestMap.get(InrConstants.REQUEST_META_DATA_KEY));
					}
					requestPriceMap.put(MyPriceConstants.SOURCE, MyPriceConstants.SOURCE_FMO);
					requestPriceMap.put(MyPriceConstants.MP_TRANSACTION_ID, nxMpDeal.getTransactionId());
					requestPriceMap.put(MyPriceConstants.NX_TRANSACTION_ID, nxMpDeal.getNxTxnId());
					requestPriceMap.put(StringConstants.PRICE_SCENARIO_ID, nxMpDeal.getPriceScenarioId());
					requestPriceMap.put(MyPriceConstants.NX_DESIGN, nxDesign);
					requestPriceMap.put(MyPriceConstants.NX_DESIGN_ID, nxDesign.getNxDesignId());
					requestPriceMap.put(MyPriceConstants.OFFER_TYPE, obj.getProductName());
					String erateInd = paramMap.containsKey(FmoConstants.ERATE_IND) ? paramMap.get(FmoConstants.ERATE_IND).toString() : null;
					requestPriceMap.put(FmoConstants.PORT_ID, obj.getComponentId());
					requestPriceMap.put(FmoConstants.ERATE_IND, erateInd);
					responseMap = myPriceTransactionUtil.callUpdatePricing(requestPriceMap);
					responseMap.put(MyPriceConstants.TRANSACTION_TYPE, MyPriceConstants.AUDIT_UPDATE_PR);
				}*/
			}
			
			paramMap.replace(MyPriceConstants.CONFIG_DESIGN_RESPONSE, requestMap.get(MyPriceConstants.CONFIG_DESIGN_RESPONSE));
			paramMap.replace(MyPriceConstants.CONFIG_RESPONSE_PROCESSING_DATA, requestMap.get(MyPriceConstants.CONFIG_RESPONSE_PROCESSING_DATA));
			
		}
		return responseMap;
	}
	
	protected String getOfferNameForConfigSol(String offerName) {
		if(StringUtils.isNotEmpty(offerName) && offerName.contains(MyPriceConstants.BVoIP)) {
			return MyPriceConstants.BVoIP;
		}
		return offerName;
	}
	
	
	public NxMpDeal getDealBySolutionId(Long nxSolutionId) {
		List<NxMpDeal> deals = nxMpDealRepository.findBySolutionIdAndActiveYN(nxSolutionId, StringConstants.CONSTANT_Y);
		if(CollectionUtils.isNotEmpty(deals)) {
			return deals.get(0);
		}
		return null;
	}
	
	protected String getProductModelNameForCongSolution(List<NxDesignDetails> nxDesignDetails) {
		//setting productModel_pm for configSolution and product request
		StringBuilder sb = new StringBuilder();
		for(NxDesignDetails design:nxDesignDetails) {
			if(sb.length()>0) {
				 sb.append(MyPriceConstants.MP_DELIM);
			}
			sb.append(design.getProductName());
			if(StringUtils.isNotEmpty(design.getType()) && MyPriceConstants.FMO_CONFIG_ACCESS_PRODUCT.containsKey(design.getType())) {
				sb.append(MyPriceConstants.MP_DELIM);
				sb.append(MyPriceConstants.FMO_CONFIG_ACCESS_PRODUCT.get(design.getType()));
			}
			
		}
		return sb.toString();
	}
	
	
	protected Integer getNoOfSearchSite(String input) {
		if(StringUtils.isNotEmpty(input)) {
			List<String> lst= new ArrayList<String>(Arrays.asList(input.split(
					Pattern.quote(MyPriceConstants.MP_DELIM))));
			
			return lst.size();
		}
		return null;
	}
	
	public Map<String, Object> callConfigureSolutionProduct(String inputJson,Map<String, Object> requestMap) {
		log.info("Start -- callConfigureSolutionProduct FMO");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			Boolean isSuccessful = configSolutionHandler.initiateConfigSolnAndProdWebService(requestMap,inputJson);
			if(!isSuccessful) {
				myPriceTransactionUtil.prepareResponseMap(requestMap, responseMap);
				responseMap.put(MyPriceConstants.RESPONSE_STATUS, false);
				return responseMap;
			}
			
		} catch (SalesBusinessException e) {
			log.info("Error during callConfigureSolutionProduct FMO{}", e.getMessage());
			responseMap.put(MyPriceConstants.RESPONSE_STATUS, false);
			responseMap.put(MyPriceConstants.RESPONSE_MSG, e.getMessage());
			responseMap.put(MyPriceConstants.RESPONSE_CODE, e.getHttpErrorCode());
			return responseMap;
		}
		responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
		log.info("End -- callConfigureSolutionProduct FMO");
		return responseMap;
	}
	
	public Map<String, Object> callConfigureDesign(String inputJson,Map<String, Object> requestMap) {
		log.info("Start -- callConfigureDesign FMO");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			Boolean isSuccessful = configureDesignHandler.initiateConfigDesignWebService(requestMap,inputJson);
			if(!isSuccessful) {
				myPriceTransactionUtil.prepareResponseMap(requestMap, responseMap);
				responseMap.put(MyPriceConstants.RESPONSE_STATUS, false);
				return responseMap;
			}
		} catch (SalesBusinessException e) {
			log.info("Error during callConfigureDesign FMO {}", e.getMessage());
			responseMap.put(MyPriceConstants.RESPONSE_STATUS, false);
			responseMap.put(MyPriceConstants.RESPONSE_MSG, e.getMessage());
			responseMap.put(MyPriceConstants.RESPONSE_CODE, e.getHttpErrorCode());
			return responseMap;
		}
		responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
		log.info("End -- callConfigureDesign FMO");
		return responseMap;
	}
	
	protected List<String> getProductsNameListForConfigDesign(NxDesignDetails obj,Map<String, Object> requestMap ){
		List<String> result=new ArrayList<String>();
		result.add(obj.getProductName());
		if(StringUtils.isNotEmpty(obj.getType())) {
			result.add(obj.getType());
		}
		return result;
	}
	
	
	public String getAccessType(JsonNode design,String offerName,Map<String, Object> paramMap) {
		String country=StringUtils.isNotEmpty(design.at("/design/0/country").asText())?design.at("/design/0/country").asText():design.at("/country").asText();
		if(StringUtils.isNotEmpty(country) && ("US".equalsIgnoreCase(country) || "USA".equalsIgnoreCase(country))) {
			String accessTypeUdfAttrId=design.at("/design/0/accessTypeUdfAttrId").asText();
			String accessType=getDataFromNxLookUp(accessTypeUdfAttrId, MyPriceConstants.FMO_DOMESTIC_ACCESS_TYPE_DATASET);
			  if(StringUtils.isNotEmpty(accessType) && isValidAccessPricing(design,offerName,accessType)) {
				  return this.createProductNameForAccess(new ArrayList<String>(Arrays.asList(offerName,accessType)),paramMap); 
			  }
		}else {
			if(MyPriceConstants.AVPN.equals(offerName)) {
				return this.createProductNameForAccess(new ArrayList<String>(Arrays.asList(offerName,"InternationalAccess")),paramMap);
				
			}
		}
		return null;
		
	}
	
	protected boolean isValidAccessPricing(JsonNode design,String offer,String accessType) {
		
		if(MyPriceConstants.ADI.equals(offer) && MyPriceConstants.TDM.equals(accessType)) {
			String speed =design.at("/design/0/accessSpeedUdfAttrId").asText();
			if(StringUtils.isNotEmpty(speed) && !"null".equals(speed) ) {
				return true;
			}
			return false;
		}else if(MyPriceConstants.AVPN.equals(offer) || MyPriceConstants.ADI.equals(offer)  ) {
			String speed =design.at("/design/0/accessDetails/speed").asText();
			String popClli  =design.at("/design/0/accessDetails/respPopClli").asText();
			if(StringUtils.isNotEmpty(speed) && !"null".equals(speed) 
					&& StringUtils.isNotEmpty(popClli) && !"null".equals(popClli) ) {
				return true;
			}
			return false;
		}
		return true;
	}
	
	
	@SuppressWarnings("unchecked")
	public String createProductNameForAccess(List<String> input,Map<String, Object> paramMap) {
		Map<String,List<String>> productInfoMap=
				paramMap.get(MyPriceConstants.FMO_ACCESS_PRODUCT_NAME_DATA_MAP)!=null?
						(Map<String,List<String>>)paramMap.get(MyPriceConstants.FMO_ACCESS_PRODUCT_NAME_DATA_MAP):null;
		if(CollectionUtils.isNotEmpty(input)) {
			for (Map.Entry<String,List<String>>  criteriaMap : productInfoMap.entrySet()) {
				List<String> criteria=criteriaMap.getValue();
				if(listEqualsIgnoreOrder(criteria,input)) {
					return criteriaMap.getKey();
				}
			}
			return input.stream()
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
	
	
	public Map<String,List<String>> getDataMapFromLookup(String datasetName){
		Map<String,List<String>> result=new HashMap<String, List<String>>();
		List<NxLookupData> nxLookupLst=nxLookupDataRepository.findByDatasetName(datasetName);
		 Optional.ofNullable(nxLookupLst).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull).
		    forEach( data -> {
		    	if(StringUtils.isNotEmpty(data.getCriteria()) && StringUtils.isNotEmpty(data.getItemId())) {
		    		List<String> range=new ArrayList<String>(Arrays.asList(data.getCriteria().split(Pattern.quote(","))));
		    		result.put(data.getItemId(), range);
		    	}
		    	
		 });
		return result;
	}
	
	/*protected String getDataFromNxLookUp(String input, String looupDataSet) {
		if(StringUtils.isNotEmpty(looupDataSet) && StringUtils.isNotEmpty(input)) {
			NxLookupData nxLookup=nxLookupDataRepository.findTopByDatasetNameAndItemId(looupDataSet, input);
			if(null!=nxLookup) {
				return nxLookup.getDescription();
			}
		}
		return null;
	}*/
	
	protected String getDataFromNxLookUp(String input, String looupDataSet) {
		if(StringUtils.isNotEmpty(looupDataSet) && StringUtils.isNotEmpty(input)) {
			Map<String,NxLookupData> resultMap=nxMyPriceRepositoryServce.getLookupDataByItemId(looupDataSet);
			if(null!=resultMap && resultMap.containsKey(input) && null!= resultMap.get(input) ) {
				NxLookupData data=resultMap.get(input);
				return data.getDescription();
			}
		}
		return null;
	}
	
	
	public String getOfferNameForNxDesign(List<String> requestProduts) {
		Map<String,List<String>> productInfoMap=MyPriceConstants.FMO_OFFER_NAME;
		if(CollectionUtils.isNotEmpty(requestProduts)) {
			for (Map.Entry<String,List<String>>  criteriaMap : productInfoMap.entrySet()) {
				List<String> criteria=criteriaMap.getValue();
				if(listEqualsIgnoreOrder(criteria,requestProduts)) {
					return criteriaMap.getKey();
				}
			}
			return requestProduts.stream()
	                .collect(Collectors.joining("/"));
		}
		return "";
	}
	
	public String getBvoipOfferName(String inputOfferName) {
		if("BVOIP".equalsIgnoreCase(inputOfferName) || "BVoIP".equalsIgnoreCase(inputOfferName)) {
			return MyPriceConstants.BVoIP;
		}
		return inputOfferName;
	}
	
	
}
