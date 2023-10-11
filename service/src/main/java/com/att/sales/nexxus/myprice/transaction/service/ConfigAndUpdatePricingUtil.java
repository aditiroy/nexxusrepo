package com.att.sales.nexxus.myprice.transaction.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.constant.CommonConstants;
import com.att.sales.nexxus.constant.FmoConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxDesign;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxDesignDetails;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxMpConfigMapping;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.reteriveicb.model.ComponentDetails;
import com.att.sales.nexxus.reteriveicb.model.PriceAttributes;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.jayway.jsonpath.TypeRef;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.SimpleAttributesType;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.SimpleDocumentType;

import groovy.util.Eval;

@Component
public class ConfigAndUpdatePricingUtil {
	
	@Autowired
	private NexxusJsonUtility nexxusJsonUtility;

	@Autowired
	private JsonPathUtil jsonPathUtil;

	@Autowired
	private NxDesignAuditRepository nxDesignAuditRepository;

	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Autowired
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	

	public JSONObject getInputDesignDetails(NxDesign nxDesign, String offerName) {
		JSONObject designDetails = JacksonUtil.toJsonObject(nxDesign.getNxDesignDetails().get(0).getDesignData());
		if (null != designDetails) {
			if (offerName.equals(MyPriceConstants.ASE_OFFER_NAME)) {
				return this.mergeSolutionAndDesignDataASE(nxDesign, designDetails);
			} else if (offerName.equals(MyPriceConstants.ADE_OFFER_NAME)) {
				return this.mergeSolutionAndDesignDataADE(nxDesign, designDetails);
			} else {
				return designDetails;
			}
		}
		return null;
	}

	private JSONObject mergeSolutionAndDesignDataASE(NxDesign nxDesign, JSONObject designDetails) {
		JSONObject solutionData = getSolutionData(nxDesign);
		if (null != solutionData) {
			String path1 = "$..solution.offers.*.site[0]";
			String json = jsonPathUtil.set(solutionData, path1, designDetails, true);
			return JacksonUtil.toJsonObject(json);
		}
		return designDetails;
	}

	protected JSONObject mergeSolutionAndDesignDataADE(NxDesign nxDesign, JSONObject designDetails) {
		JSONObject solutionData = getSolutionData(nxDesign);
		if (null != solutionData) {
			// Replace Site array with Circuit array
			String modifiedSolutionData = jsonPathUtil.delete(solutionData, "$..solution.offers.*.site", true);
			modifiedSolutionData = jsonPathUtil.put(JacksonUtil.toJsonObject(modifiedSolutionData),
					"$..solution.offers.*", "circuit", new JSONArray(), true);
			// Merge Design data with solution data
			modifiedSolutionData = jsonPathUtil.set(JacksonUtil.toJsonObject(modifiedSolutionData),
					"$..solution.offers.*.circuit[0]", designDetails, true);
			return JacksonUtil.toJsonObject(modifiedSolutionData);
		}
		return designDetails;
	}

	protected JSONObject getSolutionData(NxDesign nxDesign) {
		JSONObject solutionData = null;
		NxDesignAudit nxDesignAudit = nxDesignAuditRepository.findByNxRefIdAndTransaction(
				nxDesign.getNxSolutionDetail().getNxSolutionId(),
				com.att.sales.nexxus.constant.CommonConstants.SOLUTION_DATA);
		if (null != nxDesignAudit) {
			solutionData = JacksonUtil.toJsonObject(nxDesignAudit.getData());
			if (null != solutionData) {
				// convert dpp marketStrata value to Nexxus format
				solutionData = this.handleMarketStrataValue(solutionData);
			}
		}
		return solutionData;
	}

	protected JSONObject handleMarketStrataValue(JSONObject input) {
		Object marketStrataObj = nexxusJsonUtility.getValue(input, "$..marketStrata");
		if (null != marketStrataObj) {
			String dppValue = String.valueOf(marketStrataObj);
			if (StringUtils.isNotEmpty(dppValue)) {
				NxLookupData nxLookup = nxLookupDataRepository.findTopByDatasetNameAndItemId("mp_marketStrata",
						dppValue);
				if (null != nxLookup && StringUtils.isNotEmpty(nxLookup.getDescription())) {
					String json = jsonPathUtil.set(input, "$..marketStrata", nxLookup.getDescription(), true);
					return JacksonUtil.toJsonObject(json);
				}
			}
		}
		return input;
	}

	public String getData(NxMpConfigMapping mappingData, JSONObject site) {
		String result=null;
		if(StringUtils.isNotEmpty(mappingData.getType()) && 
				mappingData.getType().equals(MyPriceConstants.IS_DEFAULT)) {
			result= mappingData.getDefaultValue();
		}else if (StringUtils.isNotEmpty(mappingData.getPath())) {
			if(StringUtils.isNotEmpty(mappingData.getType()) && mappingData.getType().equals("List")) {
				result= this.processListResult(mappingData, site);
			}else if(StringUtils.isNotEmpty(mappingData.getType()) && mappingData.getType().equals("Count")) {
				result= this.getResultCount(mappingData, site);
			}else if (mappingData.getPath().contains(",#")) {
				List<String> pathList = new ArrayList<>(Arrays.asList(mappingData.getPath().split(",#")));
				result= processMultipleJsonPath(mappingData, site,pathList, mappingData.getDelimiter());
			}else if(mappingData.getPath().contains(MyPriceConstants.OR_CONDITION_SEPERATOR)){
				List<String> pathList= new ArrayList<String>(Arrays.asList(mappingData.getPath().split(
						Pattern.quote(MyPriceConstants.OR_CONDITION_SEPERATOR))));
				result= processOrCondition(mappingData, site,pathList);
			} else if (mappingData.getPath().contains(MyPriceConstants.MULTI_JSON_PATH_SEPERATOR)) {
				List<String> pathList = new ArrayList<>(
						Arrays.asList(mappingData.getPath().split(MyPriceConstants.MULTI_JSON_PATH_SEPERATOR)));
				result=processMultipleJsonPath(mappingData, site,pathList, mappingData.getDelimiter());
			} else {
				result = this.getItemValueUsingJsonPath(mappingData.getPath(), site);
				if(StringUtils.isNotEmpty(mappingData.getDataSetName())) {
					result= this.processDataSetName(result, mappingData);
				}
			}
		}
		if(StringUtils.isEmpty(result)) {
			result=mappingData.getDefaultValue();
		}
		return result;
	}
	
	

	
	/**
	 * Process list result.
	 *
	 * @param mappingData the mapping data
	 * @param inputDesignDetails the input design details
	 * @param offerId the offer id
	 * @return the string
	 */
	protected String processListResult(NxMpConfigMapping mappingData,JSONObject inputDesignDetails) {
		TypeRef<List<String>> ref = new TypeRef<List<String>>() {};
		List<String> dataLst=jsonPathUtil.search(inputDesignDetails, mappingData.getPath(), ref);
		StringBuilder sb = new StringBuilder();
		if(CollectionUtils.isNotEmpty(dataLst)) {
			for(String itemValue:dataLst) {
				if(StringUtils.isNotEmpty(mappingData.getDataSetName())) {
					itemValue=this.processDataSetName(itemValue,mappingData);
				}
				
				if(sb.length()>0 && StringUtils.isNotEmpty(mappingData.getDelimiter())) {
					 sb.append(mappingData.getDelimiter());
				}
				if(StringUtils.isNotEmpty(itemValue)) {
					sb.append(itemValue);
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * Process data set name.
	 *
	 * @param input the input
	 * @param mappingData the mapping data
	 * @param offerId the offer id
	 * @return the string
	 */
	protected String processDataSetName(String input,NxMpConfigMapping mappingData) {
		String dataSourceName=mappingData.getDataSetName();
		if(dataSourceName.contains(MyPriceConstants.NX_LOOKUP_SOURCE)) {
			String looupDataSet = dataSourceName.substring(dataSourceName.indexOf('|')+1, dataSourceName.length()); 
			input = getDataFromNxLookUp(input, looupDataSet);
		}
		return input;
	}
	
	/**
	 * Gets the data from nx look up.
	 *
	 * @param input the input
	 * @param looupDataSet the looup data set
	 * @return the data from nx look up
	 */
	/*protected String getDataFromNxLookUp(String input, String looupDataSet) {
		if(StringUtils.isNotEmpty(looupDataSet)) {
			NxLookupData nxLookup=nxLookupDataRepository.findTopByDatasetNameAndItemId(looupDataSet, input);
			if(null!=nxLookup) {
				input=nxLookup.getDescription();
			}
		}
		return input;
	}*/
	
	protected String getDataFromNxLookUp(String input, String looupDataSet) {
		if(StringUtils.isNotEmpty(looupDataSet) && StringUtils.isNotEmpty(input)) {
			Map<String,NxLookupData> resultMap=nxMyPriceRepositoryServce.getLookupDataByItemId(looupDataSet);
			if(null!=resultMap && resultMap.containsKey(input) && null!= resultMap.get(input) ) {
				NxLookupData data=resultMap.get(input);
				return data.getDescription();
			}
		}
		return input;
	}
	
	protected String getResultCount(NxMpConfigMapping mappingData,JSONObject inputDesignDetails) {
		TypeRef<List<Object>> ref = new TypeRef<List<Object>>() {};
		if(mappingData.getPath().contains(MyPriceConstants.MULTI_JSON_PATH_SEPERATOR)){
			int i=0;
			List<String> pathList= new ArrayList<String>(Arrays.asList(mappingData.getPath().split(
					MyPriceConstants.MULTI_JSON_PATH_SEPERATOR)));
			for(String path:pathList) {
				List<Object> dataLst=jsonPathUtil.search(inputDesignDetails,path,ref);
				if(CollectionUtils.isNotEmpty(dataLst)) {
					i=dataLst.size();
				}else {
					continue;
				}
			}
			return i>0?String.valueOf(i):null;
		}else {
			List<Object> dataLst=jsonPathUtil.search(inputDesignDetails, mappingData.getPath(), ref);
			if(CollectionUtils.isNotEmpty(dataLst)) {
				return String.valueOf(dataLst.size());
			}
		}
		
		
		return null;
	}
	
	

	protected String processMultipleJsonPath(NxMpConfigMapping mappingData, JSONObject site,
			List<String> pathList, String delim) {
		StringBuilder sb = new StringBuilder();
		for (String path : pathList) {
			if (path.contains(MyPriceConstants.COMMA_SEPERATOR)) {
				List<String> subConditionPathLst = new ArrayList<>(
						Arrays.asList(path.split(MyPriceConstants.COMMA_SEPERATOR)));
				if (sb.length() > 0 && StringUtils.isNotEmpty(delim)) {
					sb.append(delim);
				}
				String result = processMultipleJsonPath(mappingData, site,subConditionPathLst,
						MyPriceConstants.COMMA_SEPERATOR);
				if (StringUtils.isNotEmpty(result)) {
					sb.append(result);
				}
			}else if (path.contains(MyPriceConstants.OR_CONDITION_SEPERATOR)){
				List<String> subConditionPathLst= new ArrayList<String>(Arrays.asList(path.split(Pattern.quote
						(MyPriceConstants.OR_CONDITION_SEPERATOR))));
				if(sb.length()>0 && StringUtils.isNotEmpty(delim)) {
					 sb.append(delim);
				}
				String result=this.processOrCondition(mappingData, site,subConditionPathLst);
				if(StringUtils.isNotEmpty(result)) {
					sb.append(result);
				}
			} else {
				String itemValue = this.getItemValueUsingJsonPath(path, site);
				if (StringUtils.isNotEmpty(itemValue)) {
					if(StringUtils.isNotEmpty(mappingData.getDataSetName())) {
						itemValue=this.processDataSetName(itemValue, mappingData);
					}
					if(sb.length()>0 && StringUtils.isNotEmpty(delim)) {
						sb.append(delim);
					}
					if(StringUtils.isNotEmpty(itemValue)) {
						sb.append(itemValue);
					}
					
				}
			}
		}
		return sb.toString();
	}
	
	
	
	/**
	 * Process or condition.
	 *
	 * @param mappingData the mapping data
	 * @param inputDesignDetails the input design details
	 * @param offerId the offer id
	 * @param pathList the path list
	 * @return the string
	 */
	protected String processOrCondition(NxMpConfigMapping mappingData,JSONObject inputDesignDetails,
			List<String> pathList) {
		for(String path:pathList) {
			String itemValue=null;
			if(path.contains(MyPriceConstants.MULTI_JSON_PATH_SEPERATOR)){
				List<String> subPathList= new ArrayList<String>(Arrays.asList(path.split(MyPriceConstants.MULTI_JSON_PATH_SEPERATOR)));
				itemValue= processMultipleJsonPath(mappingData,inputDesignDetails,subPathList,mappingData.getDelimiter());
			}else {
				itemValue=this.getItemValueUsingJsonPath(path,inputDesignDetails);
			}
			if(StringUtils.isNotEmpty(itemValue)) {
				if(StringUtils.isNotEmpty(mappingData.getDataSetName())) {
					itemValue=this.processDataSetName(itemValue,mappingData);
				}
				if(StringUtils.isNotEmpty(itemValue)) {
					return itemValue;
				}
			}
		}
		return null;
	}

	protected String getItemValueUsingJsonPath(String jsonPath, JSONObject site) {
		Object result = nexxusJsonUtility.getValue(site, jsonPath);
		if (null != result) {
			return String.valueOf(result);
		}
		return null;
	}
	
	/**
	 * Checks if is product line itd match for config design.
	 *
	 * @param methodParam the method param
	 * @param respParentId the resp parent id
	 * @param offerName the offer name
	 * @return the boolean
	 */
	public Boolean isProductLineIdMatchForConfigDesign(Map<String,Object> methodParam,String respParentId,String offerName) {
		
		String reqProductLineId=methodParam.get(MyPriceConstants.MP_PRODUCT_LINE_ID)!=null?
				(String)methodParam.get(MyPriceConstants.MP_PRODUCT_LINE_ID):"";
		
		if(offerName.equals(MyPriceConstants.ASE_OFFER_NAME) || offerName.equals(MyPriceConstants.ADE_OFFER_NAME) 
				|| StringConstants.OFFERNAME_ASENOD.equalsIgnoreCase(offerName) || MyPriceConstants.AVPN.equalsIgnoreCase(offerName) 
				|| MyPriceConstants.ADI.equalsIgnoreCase(offerName)) {
			if(StringUtils.isNotEmpty(respParentId) && 
					reqProductLineId.equals(respParentId)) {
				return true;		
			}	
			return false;
		}
		return true;
		
	}
	
	
	/**
	 * Collect circuit details for ade.
	 *
	 * @param inputDesignDetails the input design details
	 * @param offerName the offer name
	 * @param methodParam the method param
	 */
	public void collectCircuitDetailsForAde(JSONObject inputDesignDetails,String offerName,Map<String,Object> methodParam) {
		if(offerName.equals(MyPriceConstants.ADE_OFFER_NAME)) {
			String priceScenarioId=methodParam.get(StringConstants.PRICE_SCENARIO_ID)!=null?
					String.valueOf(methodParam.get(StringConstants.PRICE_SCENARIO_ID)):null;
			//collect all ADE mandatory data from DPP for response processing
			methodParam.put(MyPriceConstants.ENDPOINT_A_NX_SITE_ID, 
					this.getDataInString(inputDesignDetails,MyPriceConstants.ENDPOINT_A_NX_SITE_ID_PATH));
			methodParam.put(MyPriceConstants.ENDPOINT_Z_NX_SITE_ID, 
					this.getDataInString(inputDesignDetails,MyPriceConstants.ENDPOINT_Z_NX_SITE_ID_PATH));
			methodParam.put(MyPriceConstants.ENDPOINT_A_COMPONENT_ID,
					this.getDataInString(inputDesignDetails, MyPriceConstants.ENDPOINT_A_COMPONENT_ID_PATH));
			methodParam.put(MyPriceConstants.ENDPOINT_Z_COMPONENT_ID, 
					this.getDataInString(inputDesignDetails,MyPriceConstants.ENDPOINT_Z_COMPONENT_ID_PATH));
			methodParam.put(MyPriceConstants.CIRCUIT_COMPONENT_ID,
					this.getDataInString(inputDesignDetails,MyPriceConstants.CIRCUIT_COMPONENT_ID_PATH));
			methodParam.put(MyPriceConstants.ENDPOINT_A_BEID, this.getBeIdListByComponentId(inputDesignDetails,
					methodParam.get(MyPriceConstants.ENDPOINT_A_COMPONENT_ID),priceScenarioId));
			methodParam.put(MyPriceConstants.ENDPOINT_Z_BEID, this.getBeIdListByComponentId(inputDesignDetails,
					methodParam.get(MyPriceConstants.ENDPOINT_Z_COMPONENT_ID),priceScenarioId));
			methodParam.put(MyPriceConstants.CIRCUIT_BEID, this.getBeIdListByComponentId(inputDesignDetails,
					methodParam.get(MyPriceConstants.CIRCUIT_COMPONENT_ID),priceScenarioId));
		}
	}
	
	/**
	 * Gets the be id list by component id.
	 *
	 * @param inputDesignDetails the input design details
	 * @param componentId the component id
	 * @return the be id list by component id
	 */
	protected List<String> getBeIdListByComponentId(JSONObject inputDesignDetails,Object componentId,String priceScenarioId){
		String path="$..circuit.*.priceDetails.componentDetails.[?(@.componentId=="+componentId+")]"
				+ ".priceAttributes[?(@.priceScenarioId=="+priceScenarioId+")].beid";
		return this.getDataListInString(inputDesignDetails, path);
	}
	
	public String getDataInString(JSONObject inputDesignDetails,String path) {
		TypeRef<List<String>> ref = new TypeRef<List<String>>() {};
		List<String> dataLst=jsonPathUtil.search(inputDesignDetails,path, ref);
		if(CollectionUtils.isNotEmpty(dataLst)) {
			return dataLst.get(0);
		}
		return null;
	}
	
	public List<String> getDataListInString(JSONObject inputDesignDetails,String path) {
		TypeRef<List<String>> ref = new TypeRef<List<String>>() {};
		List<String> dataLst=jsonPathUtil.search(inputDesignDetails,path, ref);
		if(CollectionUtils.isNotEmpty(dataLst)) {
			return dataLst;
		}
		return new ArrayList<>();
	}
	
	public List<PriceAttributes> getPriceAttributes(Object jsonObject,String path){
		if(null!=jsonObject && StringUtils.isNotEmpty(path)) {
			TypeRef<List<PriceAttributes>> mapType = new TypeRef<List<PriceAttributes>>() {};
			return jsonPathUtil.search(jsonObject, path,mapType);
		}
		return new ArrayList<>();
		
	}
	
	public List<ComponentDetails> getComponentList(Object jsonObject,String path){
		if(null!=jsonObject && StringUtils.isNotEmpty(path)) {
			TypeRef<List<ComponentDetails>> mapType = new TypeRef<List<ComponentDetails>>() {};
			return jsonPathUtil.search(jsonObject, path,mapType);
		}
		return new ArrayList<>();
		
	}
	
	public List<NxMpConfigMapping>  getNxConfigMapping(Map<String, Object> requestMap,String offerName,String ruleType){
		
		String productType=requestMap.get(MyPriceConstants.PRODUCT_TYPE)!=null?requestMap.get(MyPriceConstants.PRODUCT_TYPE).toString():null;
		if(StringUtils.isNotEmpty(productType)) {
			return nxMyPriceRepositoryServce.findByOfferAndProductTypeAndRuleName(offerName, productType,ruleType);
		}else {
			return nxMyPriceRepositoryServce.findByOfferAndRuleName(offerName,ruleType);
		}
	}
	
	public void setProductTypeForInrFmo(Map<String, Object> requestMap) {
		if(requestMap.containsKey(MyPriceConstants.SOURCE) && null!=requestMap.get(MyPriceConstants.SOURCE)) {
			if(MyPriceConstants.SOURCE_FMO.equals(requestMap.get(MyPriceConstants.SOURCE).toString())){
				requestMap.put(MyPriceConstants.PRODUCT_TYPE, MyPriceConstants.SOURCE_FMO);
			}else if(MyPriceConstants.SOURCE_INR.equals(requestMap.get(MyPriceConstants.SOURCE).toString())){
				requestMap.put(MyPriceConstants.PRODUCT_TYPE, MyPriceConstants.SOURCE_INR);
			}/*else if(MyPriceConstants.SOURCE_IGLOO.equals(requestMap.get(MyPriceConstants.SOURCE).toString())){
				requestMap.put(MyPriceConstants.PRODUCT_TYPE, MyPriceConstants.PRODUCT_TYPE_ETH);
			}else if(MyPriceConstants.SOURCE_INTERNATIONAL_ACCESS.equals(requestMap.get(MyPriceConstants.SOURCE).toString())){
				requestMap.put(MyPriceConstants.PRODUCT_TYPE, MyPriceConstants.SOURCE_INTERNATIONAL_ACCESS);
			}*/
			
			
			
		}
	}
	
	
	public String getSourceName(Map<String, Object> requestMap) {
		if(requestMap.containsKey(MyPriceConstants.SOURCE) && null!=requestMap.get(MyPriceConstants.SOURCE)) {
			return requestMap.get(MyPriceConstants.SOURCE).toString();
		}
		return "";
	}
	
	@SuppressWarnings("unchecked")
	public void processConfigDataFromCustomeRules(Map<String, Object> requestMap,JSONObject inputDesign) {
		
		
		  String offerName=requestMap.get(MyPriceConstants.OFFER_NAME)!=null?(String)
		   requestMap.get(MyPriceConstants.OFFER_NAME):""; 
		  String sourceName=getSourceName(requestMap);
		 
		List<NxLookupData> rulesData=nxLookupDataRepository.
				findByDatasetNameAndItemIdAndDescription(MyPriceConstants.CUSTOM_CONFIG_RULES,offerName, sourceName);
		
		List<NxLookupData> defaultData=nxLookupDataRepository.
				findByDatasetNameAndItemIdAndDescription(MyPriceConstants.CUSTOM_CONFIG_RULES,offerName, MyPriceConstants.DEFAULT_VALUES);
		
		if(CollectionUtils.isNotEmpty(rulesData)) {
			for(NxLookupData ruleObj:rulesData) {
				if(StringUtils.isNotEmpty(ruleObj.getCriteria()) && (ruleObj.getCriteria().contains("%"))) 
				{
					String[] rule = ruleObj.getCriteria().split("%");
					String jsonPath=rule[0]!=null?rule[0]:"";
					String criteria=rule[1]!=null?rule[1]:"";
					List<Object>  datalst=nexxusJsonUtility.getValueLst(inputDesign,jsonPath);
					if(CollectionUtils.isNotEmpty(datalst)) {
						Map<String,String>  criteriaMap=(Map<String,String>) 
								nexxusJsonUtility.convertStringJsonToMap(criteria);
						if(null!=criteriaMap) {
							LinkedHashMap<String, Long> resMap = new LinkedHashMap<String, Long>();
							List<String> mrcVal = new ArrayList<String>();
							LinkedHashMap<String, Double> minuteData = new LinkedHashMap<String,Double>();
							String customeVals = null;
							LinkedHashMap<String, LinkedHashMap<String,Map<String,LinkedList<String>>>> uniqueIdExistingMinutesMrcMap = new LinkedHashMap<String, LinkedHashMap<String,Map<String,LinkedList<String>>>>();
							LinkedHashMap<String, String> uniqueIdArrayControllerMap = new LinkedHashMap<String,String>();
							for(Object x:datalst) {
								LinkedHashMap<String, String> pirceData=(LinkedHashMap<String, String>)x;
								 for(Map.Entry<String, String> entry : criteriaMap.entrySet()) {
									 
									 if(entry.getValue().contains("custome")) {
										 customeVals = entry.getValue();
										
										 if(resMap.containsKey(pirceData.get("uniqueId"))) {
											 resMap.put(pirceData.get("uniqueId"), (resMap.get(pirceData.get("uniqueId")) +1));
											 
										 }else {
											 resMap.put(pirceData.get("uniqueId"), 1L);
											 mrcVal.add(pirceData.get("actualPrice"));
										 }
									 }else if(entry.getValue().contains("calculate")) {
										 String values[] = entry.getValue().split("##");
										 String[] data = values[2].split(",");
										 String formula = values[0];
										 boolean isEval = true;
										 for(String str : data) {
											 String jsonValue=pirceData.get(str)!=null?String.valueOf(pirceData.get(str)):null;
											 if(jsonValue == null) {
												 jsonValue= getDefaultValue(str,defaultData);
												 if(jsonValue==null) {
													 isEval = false;
													 break;
												 }
											 }
											 
											 String value = String.valueOf(jsonValue);
											 formula = formula.replace(str, value);
										 }
										 if(isEval) {
											 Double res=0.0;
											 try {
											 Object result = Eval.me(formula);
											 res = Double.parseDouble(String.valueOf(result));
											 }catch(ArithmeticException e) {
												 res = 0.0;
											 }
											 String[] parse  = values[3].split("::");
											 String type=null;
											 if(values.length==5) {
												 type =values[4];
											 }
											 if("round".equalsIgnoreCase(parse[0])) {
												 if(MyPriceConstants.CP4PRODUCTS.contains(offerName) && "INR".equalsIgnoreCase(sourceName)) {
													 createUniqueIdMinuteMrcData(requestMap,entry.getKey(),String.valueOf(res),uniqueIdExistingMinutesMrcMap,pirceData,type);
												 }else { 
												 createData(requestMap,entry.getKey(),String.valueOf(Math.round(res)),MyPriceConstants.MP_DELIM);
												 }
											 }else {
												 // this is for precision
												 DecimalFormat df = new DecimalFormat(parse[1]);
												 if(MyPriceConstants.CP4PRODUCTS.contains(offerName) && "INR".equalsIgnoreCase(sourceName)) {
													 createUniqueIdMinuteMrcData(requestMap,entry.getKey(),String.valueOf(res),uniqueIdExistingMinutesMrcMap,pirceData,type); 
												 }else{
													 createData(requestMap,entry.getKey(),String.valueOf(df.format(res)),MyPriceConstants.MP_DELIM);
												 }
											 }
										 }
									 }else if(entry.getValue().equals("size")) {
										 Integer count=requestMap.get(entry.getKey())!=null?(Integer)requestMap.get(entry.getKey()):0;
										 requestMap.put(entry.getKey(), count+1);
									 }else if (entry.getValue().equals("quantity")) {
										 createData(requestMap,entry.getKey(),"1",MyPriceConstants.MP_DELIM);
									 }else if(entry.getValue().equals("collect")){
										 createBeIdRefData(requestMap, pirceData);
									  }else if((offerName != null && offerName.equalsIgnoreCase("MIS/PNT"))
											  && (entry.getValue().contains(","))){
										  createMrcData(requestMap, pirceData, entry.getKey(), entry.getValue(),MyPriceConstants.MP_DELIM);
									}else if(entry.getValue().contains("evaluate")) {
										 String values[] = entry.getValue().split("##");
										 String[] data = values[2].split(",");
										 String formulaExp = values[0];
										 String formulaSteps[] = formulaExp.split("&&");
										 Map<String ,String> formulaStepMap = new HashMap<String,String>();
										 String type=null;
										 if(values.length==4) {
											 type =values[3];
										 }
										 for(String formulaStepdata : formulaSteps) {
											 String formulaStepvalue[]= formulaStepdata.split("::");
											 String formula =formulaStepvalue[1];
											 String roundParse[] = formulaStepvalue[2].split(":");
											 int roundPrecision=Integer.valueOf(roundParse[1]);
											 boolean isEval = true;
											 if(formula.contains("step1")) { 
												 if(formulaStepMap.containsKey("step1")) { 
												  formula=formula.replace("step1", formulaStepMap.get("step1"));
												 }else {
													 isEval=false; 
												 }
											 }
											 for(String str : data) {
												 String jsonValue=pirceData.get(str)!=null?String.valueOf(pirceData.get(str)):null;
												 if(jsonValue == null) {
													 jsonValue= getDefaultValue(str,defaultData);
													 if(jsonValue==null) {
														 isEval = false;
														 break;
													 }
												 }
												 String value = String.valueOf(jsonValue);
												 formula = formula.replace(str, value);
											 }
											 if(isEval) {
												 try {
													 Object result = Eval.me(formula);
													 Double res = Double.parseDouble(String.valueOf(result));
													 formulaStepMap.put(formulaStepvalue[0], roundPrecision != -1 ? String.valueOf(getRoundValue(res,roundPrecision)) : String.valueOf(res));
												 }catch(ArithmeticException e) {
													 Double res = 0.0;
													 formulaStepMap.put(formulaStepvalue[0],String.valueOf(res));
												 }
											 }
											 
										 }
										 if(formulaStepMap.containsKey("result")) {
											 if(MyPriceConstants.CP4PRODUCTS.contains(offerName) && "INR".equalsIgnoreCase(sourceName)) {
												 createUniqueIdMinuteMrcData(requestMap,entry.getKey(),String.valueOf(formulaStepMap.get("result")),uniqueIdExistingMinutesMrcMap,pirceData,type);
											 }
										 }else{
											 createData(requestMap,entry.getKey(),String.valueOf(formulaStepMap.get("result")),MyPriceConstants.MP_DELIM);
										 }
									}
									 /*
										 * else if((offerName != null &&
										 * (offerName.equalsIgnoreCase(MyPriceConstants.BVoIP) ||
										 * offerName.equalsIgnoreCase(MyPriceConstants.BVoIP_NON_USAGE))) &&
										 * entry.getKey().equalsIgnoreCase(MyPriceConstants.
										 * BVOIP_DOMESTIC_USAGE_EXISTING_PF) ) {
										 * 
										 * String callDirection =
										 * pirceData.containsKey("callDirection")?pirceData.get("callDirection"):null;
										 * String billingElementCode =
										 * pirceData.containsKey("billingElementCode")?pirceData.get(
										 * "billingElementCode"):null; String pBIDescription =
										 * pirceData.containsKey("pBIDescription")?pirceData.get("pBIDescription"):null;
										 * String pBICode
										 * =pirceData.containsKey("pBICode")?pirceData.get("pBICode"):null;
										 * 
										 * if("OUTBOUND".equalsIgnoreCase(callDirection) &&
										 * "USAGE".equalsIgnoreCase(billingElementCode)) {
										 * if("VoIP US Off-Net LD".equalsIgnoreCase(pBIDescription) &&
										 * ("00070467".equalsIgnoreCase(pBICode) || "00092978".equalsIgnoreCase(pBICode)
										 * || "00079044".equalsIgnoreCase(pBICode) ||
										 * "00082410".equalsIgnoreCase(pBICode))) {
										 * if(pirceData.containsKey("totalQuantityAnnual")) {
										 * if(minuteData.containsKey("billedMinutes")) {
										 * minuteData.put("billedMinutes",(Double.valueOf(pirceData.get(
										 * "totalQuantityAnnual")) + minuteData.get("billedMinutes"))); }else {
										 * minuteData.put("billedMinutes",Double.valueOf(pirceData.get(
										 * "totalQuantityAnnual"))); } } }else
										 * if("VOAVPN INT L OFFNET".equalsIgnoreCase(pBIDescription) ||
										 * "VOAVPN INTERNATIONAL OFF-NET".equalsIgnoreCase(pBIDescription) ||
										 * "VDNASB INTERNATIONAL OFF-NET".equalsIgnoreCase(pBIDescription) ||
										 * "VOIP INTERNATIONAL OFF-NET".equalsIgnoreCase(pBIDescription) ||
										 * "VOAVPN US OFF-NET LD".equalsIgnoreCase(pBIDescription)||
										 * "VDNASB US OFF-NET LD".equalsIgnoreCase(pBIDescription) ||
										 * "VDNASB US ON-NET LD".equalsIgnoreCase(pBIDescription) ||
										 * "VOAVPN US OFFNET LD".equalsIgnoreCase(pBIDescription) ||
										 * "IPTF INTERSTATE USAGE".equalsIgnoreCase(pBIDescription) ||
										 * "IPTF INTRASTATE USAGE".equalsIgnoreCase(pBIDescription) ||
										 * "IPTF INTRALATA USAGE".equalsIgnoreCase(pBIDescription) ||
										 * "IPTF CANADA USAGE".equalsIgnoreCase(pBIDescription) ||
										 * "VOAVPN IPTF INTERSTATE USAGE".equalsIgnoreCase(pBIDescription) ||
										 * "VOAVPN IPTF INTRASTATE USAGE".equalsIgnoreCase(pBIDescription) ||
										 * "VOAVPN IPTF INTRALATA USAGE".equalsIgnoreCase(pBIDescription) ||
										 * "VOAVPN IPTF CANADA USAGE".equalsIgnoreCase(pBIDescription) ||
										 * "VOAVPN IPTF MOW USAGE".equalsIgnoreCase(pBIDescription)) {
										 * if(pirceData.containsKey("totalQuantityAnnual")) {
										 * if(minuteData.containsKey("billedMinutes")) {
										 * minuteData.put("billedMinutes",(Double.valueOf(pirceData.get(
										 * "totalQuantityAnnual")) + minuteData.get("billedMinutes"))); }else {
										 * minuteData.put("billedMinutes",Double.valueOf(pirceData.get(
										 * "totalQuantityAnnual"))); } } }else {
										 * if(pirceData.containsKey("totalQuantityAnnual")) {
										 * if(minuteData.containsKey("freeMinutes")) {
										 * minuteData.put("freeMinutes",(Double.valueOf(pirceData.get(
										 * "totalQuantityAnnual")) + minuteData.get("freeMinutes"))); }else {
										 * minuteData.put("freeMinutes",Double.valueOf(pirceData.get(
										 * "totalQuantityAnnual"))); } } } } }
										 */
									else if(entry.getValue().contains("type")){
										 String values[] = entry.getValue().split("##");
										 String type=null;
									    if(values.length==3) {
											type=values[2]; 
										 }
										 Object val=pirceData.get(values[0]);
										 String data=val!=null?val.toString():null;
									     createUniqueIdMinuteMrcData(requestMap,entry.getKey(),data,uniqueIdExistingMinutesMrcMap,pirceData,type);
									}else if(entry.getValue().contains("uniqueIdCount")){
										 String values[] = entry.getValue().split("##");
										 uniqueIdArrayControllerMap.put(values[1], entry.getKey());
									}
									else {
										 Object val=pirceData.get(entry.getValue());
										 String data=val!=null?val.toString():null;
										 if(MyPriceConstants.CP4PRODUCTS.contains(offerName) && "INR".equalsIgnoreCase(sourceName)) {
											 if("uniqueId".equalsIgnoreCase(entry.getValue())) {
												 createUniqueIdMinuteMrcData(requestMap,entry.getKey(),data,uniqueIdExistingMinutesMrcMap,pirceData,"uniqueId");
											 }
										 }else {
											 createData(requestMap,entry.getKey(),data,MyPriceConstants.MP_DELIM);
										 }
									 }
								 }
							}
							
							 createCP4ConsolidationData(offerName,requestMap,uniqueIdExistingMinutesMrcMap,sourceName);
							 createBVoIP_Non_Usage_ConsolidationData(offerName,requestMap,uniqueIdExistingMinutesMrcMap,sourceName,uniqueIdArrayControllerMap);

							// setMinuteData(requestMap,minuteData);
							if(!resMap.isEmpty()) {
								String count = resMap.values().stream().map(Object::toString).collect(Collectors.joining("$,$"));
								String uniqueIds = resMap.keySet().stream().map(Object::toString).collect(Collectors.joining("$,$"));
								String mrc = mrcVal.stream().map(Object::toString).collect(Collectors.joining("$,$"));
								int size = resMap.keySet().size();
								if(customeVals != null) {
									String keys[] = customeVals.split("#:");
									for(String key : keys[1].split("#")) {
										String k[] = key.split("::");
										if("uniqueId".equalsIgnoreCase(k[1])) {
											 requestMap.put(k[0], uniqueIds);
										}else if("count".equalsIgnoreCase(k[1])) {
											 requestMap.put(k[0], count);
										}else if("mrc".equalsIgnoreCase(k[1])) {
											requestMap.put(k[0], mrc);
										}else if("size".equalsIgnoreCase(k[1])) {
											requestMap.put(k[0], size);
										}
										
									}
									
								}
							}
							
						}
					}
					
				}
			}
		}
	}
	
	

	private void createMrcData(Map<String, Object> requestMap, LinkedHashMap<String, String> pirceData,String key, String values,String delim) {
		if(!requestMap.containsKey(key)) {
			requestMap.put(key, new StringBuilder());
		}
		StringBuilder sb=(StringBuilder)requestMap.get(key);
		for(String value : values.split(",")) {
			if(null != pirceData.get(value)) {
				sb.append(pirceData.get(value));
			}
		}
		requestMap.put(key,sb);
	}
	@SuppressWarnings({ "unchecked","rawtypes", "serial"})
	private void createBeIdRefData(Map<String, Object> requestMap,LinkedHashMap<String, String> pirceData) {
		 //creating dataMap for uniqueIds and  beIds
		  String offerName=requestMap.get(MyPriceConstants.OFFER_NAME)!=null?(String)
					 requestMap.get(MyPriceConstants.OFFER_NAME):""; 
		if((MyPriceConstants.ADI).equals(offerName) || (MyPriceConstants.BVoIP).equals(offerName))	{
			Object data=pirceData.get("productRateId");
			String productRateId=data!=null?data.toString():null;
			if(StringUtils.isNotEmpty(productRateId) 
					 && StringUtils.isNotEmpty(pirceData.get("uniqueId"))) {
				 if(!requestMap.containsKey(MyPriceConstants.UNIQUEID_BEID_MAP)) {
						requestMap.put(MyPriceConstants.UNIQUEID_BEID_MAP, new HashMap<String, String>());
				 }
				 ((HashMap) requestMap.get(MyPriceConstants.UNIQUEID_BEID_MAP)).
				 putAll(new HashMap<String, String>(){{put(pirceData.get("uniqueId").replace("\\/", "/"),productRateId);}});
			 }
		}else {
			if(StringUtils.isNotEmpty(pirceData.get("beid")) 
					 && StringUtils.isNotEmpty(pirceData.get("uniqueId"))) {
				 if(!requestMap.containsKey(MyPriceConstants.UNIQUEID_BEID_MAP)) {
						requestMap.put(MyPriceConstants.UNIQUEID_BEID_MAP, new HashMap<String, String>());
				 }
				 ((HashMap) requestMap.get(MyPriceConstants.UNIQUEID_BEID_MAP)).
				 putAll(new HashMap<String, String>(){{put(pirceData.get("uniqueId").replace("\\/", "/"),pirceData.get("beid"));}});
			 }
			else if(StringUtils.isNotEmpty(pirceData.get("aniraSohoBeid")) 
					 && StringUtils.isNotEmpty(pirceData.get("uniqueId"))) {
				 if(!requestMap.containsKey(MyPriceConstants.UNIQUEID_BEID_MAP)) {
						requestMap.put(MyPriceConstants.UNIQUEID_BEID_MAP, new HashMap<String, String>());
				 }
				 ((HashMap) requestMap.get(MyPriceConstants.UNIQUEID_BEID_MAP)).
				 putAll(new HashMap<String, String>(){{put(pirceData.get("uniqueId").replace("\\/", "/"),pirceData.get("aniraSohoBeid"));}});
			 }
		}
		
 	}
 


	private void createData(Map<String, Object> requestMap,String key, String data,String delim) {
		if (!requestMap.containsKey(key)) {
			 requestMap.put(key, new StringBuilder());
		}
		 StringBuilder sb=(StringBuilder)requestMap.get(key);
		if(sb.length()>0) {
			sb.append(delim);
		}
		if(data != null)
			sb.append(data.replace("\\/", "/"));
		requestMap.put(key,sb);
	}
	
	public String getProductLine(Map<String, Object> requestMap) {
		String offerName=requestMap.get(MyPriceConstants.OFFER_NAME)!=null?(String)
				requestMap.get(MyPriceConstants.OFFER_NAME):"";
		if(MyPriceConstants.LD_PRODUCT_LINE_PRODUCTS.contains(offerName)) {
			return MyPriceConstants.LD_PRODUCT_LINE;
		}
		return MyPriceConstants.TELCO_PRODUCT_LINE;
	}
	

	public String getUsocIdFromRequestFmo(String uiniqId,JSONObject designDetails) {
		String path="$..priceDetails.[?(@.uniqueId=="+uiniqId+")].beid";
		return getDataInString(designDetails, path);
	}

	public NxDesignDetails getDesignDetailsByPortId(Map<String, Object> requestMap,List<NxDesignDetails> nxDesignDetailslst) {
		String portId=requestMap.containsKey(FmoConstants.PORT_ID) 
				&& requestMap.get(FmoConstants.PORT_ID)!=null?
						requestMap.get(FmoConstants.PORT_ID).toString():"";
		String sourceName=getSourceName(requestMap);
		if(MyPriceConstants.SOURCE_FMO.equals(sourceName)) {
			return nxDesignDetailslst.stream().filter(x-> StringUtils.isNotEmpty(x.getComponentId())
					&& portId.equals(x.getComponentId())).findAny().orElse(null);
		}else {
			return nxDesignDetailslst.get(0);
		}
		
	} 
	
		 public String getCountryFromRequest(String inputDesignDetails) {
		TypeRef<List<String>> ref = new TypeRef<List<String>>() {};
		List<String> dataLst=jsonPathUtil.search(inputDesignDetails,MyPriceConstants.COUNTRY_JSON_PATH, ref);
		if(CollectionUtils.isNotEmpty(dataLst)) {
			return dataLst.get(0);
		}
		return "US";
	}
	 
	/*
	 * private void setMinuteData(Map<String, Object> requestMap ,
	 * LinkedHashMap<String, Double>minuteData) {
	 * if(minuteData.containsKey("billedMinutes") ||
	 * minuteData.containsKey("freeMinutes")){ StringBuilder sb =new
	 * StringBuilder(); if(minuteData.size()>1) { for(Map.Entry<String, Double>
	 * entry : minuteData.entrySet()) { String
	 * result=String.valueOf(Math.round(entry.getValue()/12)); if(sb.length()>0) {
	 * sb.append(MyPriceConstants.MP_DELIM); } sb.append(result); } }else {
	 * if(minuteData.containsKey("billedMinutes")) {
	 * sb.append(String.valueOf(Math.round((minuteData.get("billedMinutes")/12))));
	 * }else if(minuteData.containsKey("freeMinutes")) {
	 * sb.append(String.valueOf(Math.round(minuteData.get("freeMinutes")/12))); } }
	 * requestMap.put(MyPriceConstants.BVOIP_DOMESTIC_USAGE_EXISTING_PF,sb); } }
	 */
	
	@SuppressWarnings("unchecked")
	public void processCustomeFieldsUsingNxLookupData(Map<String,Object> requestMap,String inputDesign) {
		String offerName=requestMap.get(MyPriceConstants.OFFER_NAME)!=null?(String)
					 requestMap.get(MyPriceConstants.OFFER_NAME):"";
		String sourceName=getSourceName(requestMap);				 
		if(MyPriceConstants.ADI_TDM.equals(offerName) && MyPriceConstants.SOURCE_FMO.equals(sourceName))	{
			String itemId=getDataInString(inputDesign,MyPriceConstants.FMO_TDM_SPEED_FIELDS_PATH);	
			if(StringUtils.isNotEmpty(itemId)) {
				NxLookupData rulesData=nxLookupDataRepository.
						findTopByDatasetNameAndItemId(MyPriceConstants.MP_TDM_CUSTOME_FIELDS_DATASET,itemId);
				if(null!=rulesData && StringUtils.isNotEmpty(rulesData.getCriteria())) {
					Map<String,String>  fieldMap=(Map<String,String>) 
							nexxusJsonUtility.convertStringJsonToMap(rulesData.getCriteria());
					if(null!=fieldMap) {
						requestMap.putAll(fieldMap);
					}
				}
			}
				
		}
		
	}
	
	public String getDataInString(String inputDesignDetails,String path) {
		TypeRef<List<String>> ref = new TypeRef<List<String>>() {};
		List<String> dataLst=jsonPathUtil.search(inputDesignDetails,path, ref);
		if(CollectionUtils.isNotEmpty(dataLst)) {
			return dataLst.get(0);
		}
		return null;
	}

	public SimpleDocumentType getSolutionFilterDocument() {
		SimpleDocumentType filterDocument = new SimpleDocumentType();
		SimpleAttributesType filterAttributes = new SimpleAttributesType();
		filterAttributes.getAttribute().add("_line_bom_id");
		filterAttributes.getAttribute().add("_line_bom_parent_id");
		filterAttributes.getAttribute().add("_model_product_line_id");
		filterAttributes.getAttribute().add("_parent_line_item");
		filterAttributes.getAttribute().add("_document_number");
		filterAttributes.getAttribute().add("_parent_doc_number");
		filterAttributes.getAttribute().add("_model_name");
		filterAttributes.getAttribute().add("_model_variable_name");
		
		filterDocument.setVarName("transactionLine");
		filterDocument.setAttributes(filterAttributes);
		
		return filterDocument;
	}
	
	public SimpleDocumentType getDesignFilterDocument() {
		SimpleDocumentType filterDocument = new SimpleDocumentType();
		SimpleAttributesType filterAttributes = new SimpleAttributesType();
		filterAttributes.getAttribute().add("_document_number");
		filterAttributes.getAttribute().add("_line_bom_id");
		filterAttributes.getAttribute().add("_line_bom_parent_id");
		filterAttributes.getAttribute().add("_parent_doc_number");
		filterAttributes.getAttribute().add("_parent_line_item");
		filterAttributes.getAttribute().add("wi_uniqueID_ql");
		filterAttributes.getAttribute().add("lii_uSOC_ql");
		filterAttributes.getAttribute().add("_line_bom_part_number");
		filterAttributes.getAttribute().add("lii_nxSiteId_ql");
		filterAttributes.getAttribute().add("wl_int_ade_site_reln");
		
		filterDocument.setVarName("transactionLine");
		filterDocument.setAttributes(filterAttributes);
		
		return filterDocument;
	}
	
	private void createUniqueIdMinuteMrcData(Map<String, Object> requestMap,String key, String data,LinkedHashMap<String, LinkedHashMap<String,Map<String,LinkedList<String>>>> uniqueIdExistingMinutesMrcMap,
			LinkedHashMap<String, String> usageData,String type) {
		LinkedHashMap<String,Map<String,LinkedList<String>>> existingMinuteMrcMap=null;
		
		if(uniqueIdExistingMinutesMrcMap.containsKey(usageData.get("uniqueId"))) {
			existingMinuteMrcMap=uniqueIdExistingMinutesMrcMap.get(usageData.get("uniqueId"));
		}else {
			uniqueIdExistingMinutesMrcMap.put(usageData.get("uniqueId"), new LinkedHashMap<String, Map<String,LinkedList<String>>>());
			existingMinuteMrcMap=uniqueIdExistingMinutesMrcMap.get(usageData.get("uniqueId"));
		}
		LinkedList<String> existingMinuteMrcList = null;
		Map<String,LinkedList<String>> minuteMrcTypeMap = null;
			
		StringBuilder existingMinuteMrcMapKey = new StringBuilder();
		existingMinuteMrcMapKey.append(type);
			
		if(existingMinuteMrcMap.containsKey(existingMinuteMrcMapKey.toString())) {
			if("mrc".equalsIgnoreCase(type) || "minutes".equalsIgnoreCase(type) || "totalMinutes".equalsIgnoreCase(type)) {
				minuteMrcTypeMap =existingMinuteMrcMap.get(existingMinuteMrcMapKey.toString());
				existingMinuteMrcList = minuteMrcTypeMap.get(key);
				existingMinuteMrcList.add(data);
			}
		}else {
			minuteMrcTypeMap = new LinkedHashMap<String,LinkedList<String>>();
			existingMinuteMrcList = new LinkedList<String>();
			existingMinuteMrcList.add(data);
			minuteMrcTypeMap.put(key, existingMinuteMrcList);
			existingMinuteMrcMap.put(existingMinuteMrcMapKey.toString(), minuteMrcTypeMap);
		}
	}
	@SuppressWarnings("unchecked")
	private String getDefaultValue(String jsonField,List<NxLookupData> defaultData) {
		String data=null;
		if(CollectionUtils.isNotEmpty(defaultData)) {
			for(NxLookupData defaultDataObj : defaultData) {
				Map<String,String>  criteriaMap=(Map<String,String>) 
						nexxusJsonUtility.convertStringJsonToMap( defaultDataObj.getCriteria());
				if(criteriaMap.containsKey(jsonField)) {
					data=criteriaMap.get(jsonField);
				}
			}
		}
		
		return data;
	}
	
	@SuppressWarnings({ "unchecked"})
	public void collectDataByProductIdForResponseProcessing(Map<String, Object> requestMap,String mpProductId) {
		if(requestMap.containsKey(MyPriceConstants.CONFIG_RESPONSE_PROCESSING_DATA) && 
				null!=requestMap.get(MyPriceConstants.CONFIG_RESPONSE_PROCESSING_DATA)) {
			Map<String,Map<String,Object>> configResponseProcessingData=(HashMap<String,Map<String,Object>>) requestMap.get(MyPriceConstants.CONFIG_RESPONSE_PROCESSING_DATA);
			if(!configResponseProcessingData.containsKey(mpProductId)) {
				configResponseProcessingData.put(mpProductId, new HashMap<String, Object>());
			}
			if(requestMap.containsKey(MyPriceConstants.UNIQUEID_BEID_MAP)) {
				configResponseProcessingData.get(mpProductId).put(MyPriceConstants.UNIQUEID_BEID_MAP, requestMap.get(MyPriceConstants.UNIQUEID_BEID_MAP));
			}
			if(requestMap.containsKey(MyPriceConstants.NX_DESIGN_ID)) {
				configResponseProcessingData.get(mpProductId).put(MyPriceConstants.NX_DESIGN_ID, requestMap.get(MyPriceConstants.NX_DESIGN_ID));
			}
			//portId required for FMO
			if(requestMap.containsKey(FmoConstants.PORT_ID)) {
				configResponseProcessingData.get(mpProductId).put(FmoConstants.PORT_ID, requestMap.get(FmoConstants.PORT_ID));
			}
			
			//NX_ACCESS_PRICE_ID required for IGLOO
			if(requestMap.containsKey(MyPriceConstants.NX_ACCESS_PRICE_ID)) {
				configResponseProcessingData.get(mpProductId).put(MyPriceConstants.NX_ACCESS_PRICE_ID, requestMap.get(MyPriceConstants.NX_ACCESS_PRICE_ID));
			}
		}
		
		
	}	

	private double getRoundValue(double value, int places) {
	    double result = 0 ;
		if (places > 0) {
			BigDecimal bd = new BigDecimal(Double.toString(value));
			bd = bd.setScale(places, RoundingMode.HALF_UP);
			result= bd.doubleValue();
	    }else if(places==0) {
	    	result=Math.round(value);
	    }
		return result;
	}
	
	
	private void createCP4ConsolidationData(String offerName,Map<String, Object> requestMap,LinkedHashMap<String, LinkedHashMap<String,Map<String,LinkedList<String>>>>
	uniqueIdExistingMinutesMrcMap,String source) {
		if(MyPriceConstants.CP4PRODUCTS.contains(offerName) && "INR".equalsIgnoreCase(source) && MapUtils.isNotEmpty(uniqueIdExistingMinutesMrcMap)) {
			 for(Entry<String, LinkedHashMap<String, Map<String, LinkedList<String>>>> uidmapset : uniqueIdExistingMinutesMrcMap.entrySet()) {
				 LinkedHashMap<String, Map<String, LinkedList<String>>> minutemrcType =  uidmapset.getValue();
			  if(minutemrcType!=null && !minutemrcType.isEmpty()) {
				 Map<String, LinkedList<String>> minuteMap =  minutemrcType.get("minutes");
				 Map<String, LinkedList<String>> mrcMap =minutemrcType.get("mrc");
				 Map<String, LinkedList<String>> uniqueIdMap =minutemrcType.get("uniqueId");
				 double totalMinute=0;
				 LinkedList<String> minuteList=null;
				 LinkedList<String> mrcList=null;
				 String minuteKey="";
				 String mrcKey="";
				 String uniqueIdKey="";
				 String uniqueIdValue="";
				 if(uniqueIdMap!=null && mrcMap!=null && minuteMap!=null) {
				 for(Map.Entry<String, LinkedList<String>> uniqueIdMapList: uniqueIdMap.entrySet()) {
					 uniqueIdKey=uniqueIdMapList.getKey();
					 uniqueIdValue=uniqueIdMapList.getValue().get(0);
				 }
				 
				 createData(requestMap,uniqueIdKey,uniqueIdValue,MyPriceConstants.MP_DELIM);

				 for(Map.Entry<String, LinkedList<String>> minuteMapList: minuteMap.entrySet()) {
					 totalMinute = minuteMapList.getValue().stream().map(s -> s == null ? 0 : Double.valueOf(s)).collect(Collectors.summingDouble(Double::doubleValue));
					 minuteList=minuteMapList.getValue();
					 minuteKey=minuteMapList.getKey();
				 }
				// totalMinute= ((totalMinute >= 0)&&(totalMinute <1)) ? 1 :totalMinute;
				 int consolidatedexistingMinute = (int)getRoundValue(totalMinute,0);
				 consolidatedexistingMinute = (consolidatedexistingMinute==0)?1:consolidatedexistingMinute;
				 createData(requestMap,minuteKey,String.valueOf(consolidatedexistingMinute),MyPriceConstants.MP_DELIM);
				 double totalnetUsageRate =0;
				 double totaldicountedRevenue =0;

			
				 for(Map.Entry<String, LinkedList<String>> mrcMapList : mrcMap.entrySet()) {
					 mrcKey=mrcMapList.getKey(); 
					 mrcList=mrcMapList.getValue();
				 }
				 int mrcListcount=0;
				 
				 totalMinute= (totalMinute == 0) ? 1 : totalMinute;
						 
				 for(String data : minuteList) {
					 double dicountedRevenue =Double.valueOf(data) * Double.valueOf(mrcList.get(mrcListcount));
					 totaldicountedRevenue+=dicountedRevenue;
					 
					 mrcListcount++;
				 }
				 totaldicountedRevenue=getRoundValue(totaldicountedRevenue,5);
				 totalnetUsageRate=getRoundValue(totaldicountedRevenue/totalMinute,4);
				 createData(requestMap,mrcKey, String.valueOf((totalnetUsageRate)),MyPriceConstants.MP_DELIM);
			 }
			}
     	 }
	  }
	}
	
	public String getHideInTransactionResponseValue(Map<String, Object> requestMap) {
		if(requestMap.containsKey(MyPriceConstants.IS_LAST_DESIGN) && 
				(Boolean)requestMap.get(MyPriceConstants.IS_LAST_DESIGN)) {
			return CommonConstants.FALSE;
		}
		return CommonConstants.TRUE;
	}
	
	private void createBVoIP_Non_Usage_ConsolidationData(String offerName, Map<String, Object> requestMap,
			LinkedHashMap<String, LinkedHashMap<String, Map<String, LinkedList<String>>>> uniqueIdExistingMinutesMrcMap,
			String source,LinkedHashMap<String, String> uniqueIdArrayControllerMap){
		if (MyPriceConstants.BVoIP_NON_USAGE.equalsIgnoreCase(offerName) && "INR".equalsIgnoreCase(source)
				&& MapUtils.isNotEmpty(uniqueIdExistingMinutesMrcMap)) {
			for (Entry<String, LinkedHashMap<String, Map<String, LinkedList<String>>>> uidmapset : uniqueIdExistingMinutesMrcMap
					.entrySet()) {
				LinkedHashMap<String, Map<String, LinkedList<String>>> minutemrcType = uidmapset.getValue();
				if (MapUtils.isNotEmpty(minutemrcType)) {
					Map<String, LinkedList<String>> minuteMap = minutemrcType.get("minutes");
					Map<String, LinkedList<String>> totalMinuteMap = minutemrcType.get("totalMinutes");
					Map<String, LinkedList<String>> mrcMap = minutemrcType.get("mrc");
					Map<String, LinkedList<String>> uniqueIdMap = minutemrcType.get("uniqueId");
					double totalGenericQuantity = 0;
					double totalCount = 0;
					String minuteKey = "";
					String mrcKey = "";
					String uniqueIdKey = "";
					String uniqueIdValue = "";
					double totalNetAmount = 0;
					double netMrc=0;
					if (MapUtils.isNotEmpty(uniqueIdMap) && MapUtils.isNotEmpty(mrcMap) &&  MapUtils.isNotEmpty(minuteMap)) {
						for (Map.Entry<String, LinkedList<String>> uniqueIdMapList : uniqueIdMap.entrySet()) {
							uniqueIdKey = uniqueIdMapList.getKey();
							uniqueIdValue = uniqueIdMapList.getValue().get(0);
						}
						createData(requestMap, uniqueIdKey, uniqueIdValue, MyPriceConstants.MP_DELIM);

						for (Map.Entry<String, LinkedList<String>> minuteMapList : minuteMap.entrySet()) {
							totalGenericQuantity = minuteMapList.getValue().stream().map(s -> s == null ? 0 : Double.valueOf(s)).collect(Collectors.summingDouble(Double::doubleValue));
							minuteKey=minuteMapList.getKey();
						}
						createData(requestMap, minuteKey, String.valueOf((int) getRoundValue(totalGenericQuantity, 0)),
								MyPriceConstants.MP_DELIM);
						
						for (Map.Entry<String, LinkedList<String>> minuteMapList : totalMinuteMap.entrySet()) {
							totalCount = minuteMapList.getValue().stream().map(s -> s == null ? 0 : Double.valueOf(s)).collect(Collectors.summingDouble(Double::doubleValue));
						}
						
						for (Map.Entry<String, LinkedList<String>> mrcMapList : mrcMap.entrySet()) {
							totalNetAmount = mrcMapList.getValue().stream().map(s -> s == null ? 0 : Double.valueOf(s)).collect(Collectors.summingDouble(Double::doubleValue));
							mrcKey=mrcMapList.getKey();
						}
						totalCount = (totalCount != 0) ? totalCount : 1;
						netMrc=getRoundValue(totalNetAmount / totalCount, 2);
						createData(requestMap, mrcKey, String.valueOf((netMrc)), MyPriceConstants.MP_DELIM);
					}
				}
			}
			if(MapUtils.isNotEmpty(uniqueIdArrayControllerMap)) {
				for (Map.Entry<String,String> uniqueIdArrayControllerKey : uniqueIdArrayControllerMap.entrySet()) {
					String uiniqueIdValue=requestMap.get(uniqueIdArrayControllerKey.getKey()).toString();
					String arrayControllerAttribute=uniqueIdArrayControllerKey.getValue();
					Integer uniqueIdCount= uiniqueIdValue.split("\\$,\\$").length;
					requestMap.put(arrayControllerAttribute, uniqueIdCount);
				}
			}
			
		}
	}
	
	
	/**
	 * Gets the usoc id category.
	 *
	 * @param inputDesignDetails the input design details
	 * @param offerName the offer name
	 * @param usocIputDataMap the usoc iput data map
	 * @return the usoc id category
	 */
	@SuppressWarnings("unchecked")
	public String getUsocIdCategory(JSONObject inputDesignDetails,String offerName,Map<String,String> usocFieldsDataMap,Map<String, Object> requestMap) {
		List<NxLookupData> rules=this.getRulesForNewExistingMigration(inputDesignDetails, offerName,requestMap);
		if(CollectionUtils.isNotEmpty(rules)) {
			for(NxLookupData rule:rules) {
				boolean isRulesMatch=false;
		    	Map<String,List<String>>  ruleMap=(Map<String,List<String>>) 
		    			nexxusJsonUtility.convertStringJsonToMap(rule.getCriteria());
		    	if(MapUtils.isNotEmpty(ruleMap)) {
		    		for (Map.Entry<String,List<String>>  ruleEntry : ruleMap.entrySet()) {
			    		String ruleFieldName=ruleEntry.getKey();
			    		List<String> ruleFieldValue=ruleEntry.getValue();
			    		if(CollectionUtils.isNotEmpty(ruleFieldValue)) {
			    			String inputUsocFieldValue=usocFieldsDataMap.get(ruleFieldName);
			    			if(StringUtils.isNotEmpty(inputUsocFieldValue) && ruleFieldValue.contains(inputUsocFieldValue)) {
			    				isRulesMatch=true;
			    			}else {
			    				isRulesMatch=false;
			    				break;
			    			}
				    	}else {
				    		isRulesMatch=true;
				    	}
			    	}
		    	}else {
		    		isRulesMatch=true;
		    	}
		    	if(isRulesMatch) {
		    		return rule.getDescription();
		    	}
		    }
		}
		
		return null;
	}
	
	/**
	 * Gets the rules for new existing migration.
	 *
	 * @param inputDesignDetails the input design details
	 * @param offerName the offer name
	 * @return the rules for new existing migration
	 */
	public List<NxLookupData> getRulesForNewExistingMigration(JSONObject inputDesignDetails,String offerName,Map<String, Object> requestMap){
		List<NxLookupData> rules=null;
		String macdActivity="";
		String dataSetName="";
		if(offerName.equals(MyPriceConstants.ASE_OFFER_NAME)) {
			String reqMcadType=(String)requestMap.get(MyPriceConstants.REQ_MCAD_TYPE_VALUE);
			if(StringUtils.isNotEmpty(reqMcadType) && MyPriceConstants.MCAD_TYPE_CHANGE.equalsIgnoreCase(reqMcadType)) {
				macdActivity=this.getMacdActivityUdfAttrIdFromMultipleMcadASE(requestMap);
				dataSetName=MyPriceConstants.ASE_RULES_DATASET_MCAD;
			}else {
				macdActivity=this.getDataInString(inputDesignDetails,MyPriceConstants.ASE_MCAD_ACTIVITY_PATH);
				dataSetName=MyPriceConstants.ASE_RULES_DATASET;
			}
			
		}else if(StringConstants.OFFERNAME_ASENOD.equalsIgnoreCase(offerName)) {
			macdActivity=this.getDataInString(inputDesignDetails,MyPriceConstants.ASE_MCAD_ACTIVITY_PATH);
			dataSetName=MyPriceConstants.ASE_RULES_DATASET;
		}else if(offerName.equals(MyPriceConstants.ADE_OFFER_NAME)) {
			macdActivity=this.getDataInString(inputDesignDetails,MyPriceConstants.ADE_MCAD_ACTIVITY_PATH);
			dataSetName=MyPriceConstants.ADE_RULES_DATASET;
		}
		if(StringUtils.isNotEmpty(macdActivity)) {
			rules=nxLookupDataRepository.getNewExistingMigrationRules(dataSetName, macdActivity);
		}
		return rules;
	}
	
	
	@SuppressWarnings("unchecked")
	public String  getPortQtyPf(Map<String, Object> requestMap) {
		List<String> reqMacdActivityUdfAttr=(List<String>) requestMap.get(MyPriceConstants.REQ_MCAD_ACTIVITY_UDFATTR);
		String reqMcadType=(String)requestMap.get(MyPriceConstants.REQ_MCAD_TYPE_VALUE);
		if(StringUtils.isNotEmpty(reqMcadType) && MyPriceConstants.MCAD_TYPE_CHANGE.equalsIgnoreCase(reqMcadType)) {
			if(CollectionUtils.isNotEmpty(reqMacdActivityUdfAttr)) {
				LinkedHashMap<String,String> portQtyPfLookupData=nxMyPriceRepositoryServce.getDataFromLookup("ASE_PORT_QTY");
				if(reqMacdActivityUdfAttr.size()>1) {
					List<NxLookupData> mcadOrder=(List<NxLookupData>) requestMap.get(MyPriceConstants.ASE_MCAD_ORDER_DATA);
					for(NxLookupData o:mcadOrder) {
						for(String macdActivityUdfAttrId:reqMacdActivityUdfAttr) {
							String lookupData=portQtyPfLookupData.get(macdActivityUdfAttrId);
							if(o.getItemId().equals(lookupData)) {
								return lookupData;
							}
						}
					}
				}else {
					return portQtyPfLookupData.get(reqMacdActivityUdfAttr.get(0));
				}
			}
		}else {
			return MyPriceConstants.NEW;
		}
		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	public String  getCIRQtyPf(Map<String, Object> requestMap) {
		List<String> reqMacdActivityUdfAttr=(List<String>) requestMap.get(MyPriceConstants.REQ_MCAD_ACTIVITY_UDFATTR);
		String reqMcadType=(String)requestMap.get(MyPriceConstants.REQ_MCAD_TYPE_VALUE);
		if(StringUtils.isNotEmpty(reqMcadType) && MyPriceConstants.MCAD_TYPE_CHANGE.equalsIgnoreCase(reqMcadType)) {
			if(CollectionUtils.isNotEmpty(reqMacdActivityUdfAttr)) {
				LinkedHashMap<String,String> portQtyPfLookupData=nxMyPriceRepositoryServce.getDataFromLookup("ASE_CIR_QTY");
				if(reqMacdActivityUdfAttr.size()>1) {
					List<NxLookupData> mcadOrder=(List<NxLookupData>) requestMap.get(MyPriceConstants.ASE_MCAD_ORDER_DATA);
					for(NxLookupData o:mcadOrder) {
						for(String macdActivityUdfAttrId:reqMacdActivityUdfAttr) {
							String lookupData=portQtyPfLookupData.get(macdActivityUdfAttrId);
							if(o.getItemId().equals(lookupData)) {
								return lookupData;
							}
						}
					}
				}else {
					return portQtyPfLookupData.get(reqMacdActivityUdfAttr.get(0));
				}
			}
		}else {
			return MyPriceConstants.NEW;
		}
		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	public String  getMacdActivityUdfAttrIdFromMultipleMcadASE(Map<String, Object> requestMap) {
		List<String> reqMacdActivityUdfAttr=(List<String>) requestMap.get(MyPriceConstants.REQ_MCAD_ACTIVITY_UDFATTR);
		if(CollectionUtils.isNotEmpty(reqMacdActivityUdfAttr)) {
			if(reqMacdActivityUdfAttr.size()>1) {
				LinkedHashMap<String,String> usocLookupDataAse=nxMyPriceRepositoryServce.getDataFromLookup("ASE_USOC_CATEGORY");
				List<NxLookupData> mcadOrder=(List<NxLookupData>) requestMap.get(MyPriceConstants.ASE_MCAD_ORDER_DATA);
				for(NxLookupData o:mcadOrder) {
					for(String macdActivityUdfAttrId:reqMacdActivityUdfAttr) {
						String lookupData=usocLookupDataAse.get(macdActivityUdfAttrId);
						if(o.getItemId().equals(lookupData)) {
							return macdActivityUdfAttrId;
						}
					}	
				}
			}else {
				return reqMacdActivityUdfAttr.get(0);
			}
		}
		return null;
	}
	
	public String getRequestMacdTypeForASE(Object inputDesignDetails ,Map<String, Object> requestMap) {
		Object reqMacdTypeUdfAttr=nexxusJsonUtility.getValue(inputDesignDetails,MyPriceConstants.MCAD_TYPE_PATH_ASE);
		if(null!=reqMacdTypeUdfAttr) {
			NxLookupData macdTypeLookup=nxLookupDataRepository.
					findTopByDatasetNameAndItemId("ASE_MCAD_TYPE", reqMacdTypeUdfAttr.toString());
			if(null!=macdTypeLookup) {
				return macdTypeLookup.getDescription();
			}
		}
		return null;
	}
	
}
