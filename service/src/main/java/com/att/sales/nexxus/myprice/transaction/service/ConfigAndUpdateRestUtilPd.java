package com.att.sales.nexxus.myprice.transaction.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.constant.CommonConstants;
import com.att.sales.nexxus.constant.CustomJsonConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxMpConfigJsonMapping;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxLineItemProcessingDao;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.helper.GroupingEnitity;
import com.att.sales.nexxus.reteriveicb.model.ComponentDetails;
import com.att.sales.nexxus.reteriveicb.model.PriceAttributes;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;

import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicInteger;

@Component
public class ConfigAndUpdateRestUtilPd {
	
	@Autowired
	private NxDesignAuditRepository nxDesignAuditRepository;
	
	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Autowired
	private NexxusJsonUtility nexxusJsonUtility;
	
	@Autowired
	private JsonPathUtil jsonPathUtil;
	
	@Autowired
	private NxLineItemProcessingDao lineItemProcessingDao;
	
	@Autowired
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	
	private static final String STATE_PATH="$..state";
	
	@Autowired
	private ConfigAndUpdatePricingUtil configAndUpdatePricingUtil;
	
	@Autowired
	private RestCommonUtil restCommonUtil;
	
	private  static final String PORT_IND_PATH="$..component.[?(@.componentCodeId==30)].designDetails.[?(@.udfId==732567771)].udfAttributeId.[*]";
	private  static final String LNS_TXT_PATH="$..component.[?(@.componentCodeId==30)].designDetails.[?(@.udfId==22563)].udfAttributeText.[*]";
	/**
	 * Gets the input design details.
	 *
	 * @param nxDesign the nx design
	 * @param offerName the offer name
	 * @return the input design details
	 */
	protected String processInputDesign(JSONObject designDetails,String offerName,Map<String, Object> requestMap) {
		if(null!=designDetails) {
			this.setNexxusProductSubType(designDetails, offerName, requestMap);
			if(offerName.equals(MyPriceConstants.ASE_OFFER_NAME) ||MyPriceConstants.ASENOD_OFFER_NAME.equalsIgnoreCase(offerName)) {
				process3PAUcoIdFromLookup(requestMap);
				JSONObject result= this.mergeSolutionAndDesignDataASE(designDetails,requestMap);
				return null!=result?result.toJSONString():new JSONObject().toJSONString();
			}else if(offerName.equals(MyPriceConstants.ADE_OFFER_NAME)) {
				JSONObject result= this.mergeSolutionAndDesignDataADE(designDetails,requestMap);
				//merge circuit block on endpoint level to prepare rest request for MP for ADE
				result=this.mergeCircuitBlockOnEndpointLevelADE(result);
				return  null!=result?result.toJSONString():new JSONObject().toJSONString();
			}else {
				return designDetails.toJSONString();
			}
		}
		return null;
	}
	
	
	
	/**
	 * Merge solution and design data ASE.
	 *
	 * @param nxDesign the nx design
	 * @param designDetails the design details
	 * @return the JSON object
	 */
	protected JSONObject mergeSolutionAndDesignDataASE(JSONObject designDetails,Map<String, Object> requestMap) {
		JSONObject solutionData=getSolutionData(requestMap);
		if(null!=solutionData) {
			if (StringConstants.VERSION_2.equals(requestMap.get(StringConstants.REST_VERSION))) {
				String path1 = "$..solution.offers.*.site";
				String json = jsonPathUtil.set(solutionData, path1, designDetails.get("site"), true);
				return JacksonUtil.toJsonObject(json);
			} else {
				String path1 = "$..solution.offers.*.site[0]";
				String json = jsonPathUtil.set(solutionData, path1,designDetails, true);
				return JacksonUtil.toJsonObject(json);
			}
		}
		return designDetails;
	}
	
	
	protected JSONObject mergeSolutionAndDesignDataADE(JSONObject designDetails,Map<String, Object> requestMap) {
		JSONObject solutionData=getSolutionData(requestMap);
		if(null!=solutionData) {
			//Replace Site array with Circuit array
			String modifiedSolutionData = jsonPathUtil.delete(solutionData, "$..solution.offers.*.site",true);
			modifiedSolutionData = jsonPathUtil.put(JacksonUtil.toJsonObject(modifiedSolutionData), 
					"$..solution.offers.*","circuit",new JSONArray(), true);
			//Merge Design data with solution data
			modifiedSolutionData = jsonPathUtil.set(JacksonUtil.toJsonObject(modifiedSolutionData), 
					"$..solution.offers.*.circuit[0]",designDetails, true);
			return JacksonUtil.toJsonObject(modifiedSolutionData);
		}
		return designDetails;
	}
	
	protected  JSONObject mergeCircuitBlockOnEndpointLevelADE(JSONObject designDetails) {
		JsonPathUtil jsonPathUtil=new JsonPathUtil();
		//get circuit block from main design using path
		Object circuitBlock=nexxusJsonUtility.getValue(designDetails, "$..component.[?(@.componentCodeId== 1210)]");
		
		//set on Endpoint level in circuitObj object
		String updateDesignDetails=jsonPathUtil.put(designDetails, 
				"$..component.[?(@.componentCodeId== 1220)]","circuitObj",new JSONObject(), true);
		updateDesignDetails = jsonPathUtil.set(JacksonUtil.toJsonObject(updateDesignDetails), 
				"$..component.[?(@.componentCodeId== 1220)].circuitObj",circuitBlock, true);
		if(StringUtils.isNotEmpty(updateDesignDetails)) {
			return JacksonUtil.toJsonObject(updateDesignDetails);
		}
		return designDetails;
	}
	
	/**
	 * Gets the solution data.
	 *
	 * @param nxDesign the nx design
	 * @return the solution data
	 */
	protected JSONObject getSolutionData(Map<String, Object> requestMap) {
		JSONObject solutionData=null;
		Long solutionId=requestMap.containsKey(MyPriceConstants.NX_SOLIUTION_ID)?(Long)requestMap.get(MyPriceConstants.NX_SOLIUTION_ID):0l;
		NxDesignAudit nxDesignAudit=nxDesignAuditRepository.findByNxRefIdAndTransaction(
				solutionId,CommonConstants.SOLUTION_DATA);
		if(null!=nxDesignAudit) {
			solutionData=JacksonUtil.toJsonObject(nxDesignAudit.getData());
			if(null!=solutionData) {
				//convert dpp marketStrata value to Nexxus format
				solutionData=this.handleMarketStrataValue(solutionData);
			}
		}
		return solutionData;
	}
	
	/**
	 * Handle market strata value.
	 *
	 * @param input the input
	 * @return the JSON object
	 */
	protected JSONObject handleMarketStrataValue(JSONObject input) {
		Object erateInd=nexxusJsonUtility.getValue(input, "$..erateInd");
		if(null!=erateInd && "Y".equals(String.valueOf(erateInd))){
			//code for setting productSubType_pf value in configDesign request for ASE and ASENOD in Erate scenario
			String value="SLED";
			String json = jsonPathUtil.set(input, "$..marketStrata",value, true);
			return JacksonUtil.toJsonObject(json);
		}else {
			Object marketStrataObj=nexxusJsonUtility.getValue(input, "$..marketStrata");
			if(null!=marketStrataObj) {
				String dppValue=String.valueOf(marketStrataObj);
				if(StringUtils.isNotEmpty(dppValue)) {
					NxLookupData nxLookup=nxLookupDataRepository.findTopByDatasetNameAndItemId("mp_marketStrata", dppValue);
					if(null!=nxLookup && StringUtils.isNotEmpty(nxLookup.getDescription())) {
						String json = jsonPathUtil.set(input, "$..marketStrata",nxLookup.getDescription(), true);
						return JacksonUtil.toJsonObject(json);
					}
				}
			}
		}
		
		return input;
	}
	
	@SuppressWarnings("unchecked")
	protected void setNexxusProductSubType(JSONObject designDetails,String offerName,Map<String, Object> requestMap) {
		boolean automationInd=requestMap.get(MyPriceConstants.AUTOMATION_IND)!=null?
				(boolean)requestMap.get(MyPriceConstants.AUTOMATION_IND):false;
		String nxProdSubTypeVal=null;		
		if(automationInd) {
			//setting nxProductSubType for automation flow indicator
			NxLookupData prodSubTypeLookupData=nxLookupDataRepository.findTopByDatasetNameAndItemId
					(MyPriceConstants.AUTOMATION_PRODUCT_SUB_TYPE,offerName);
			if(null!=prodSubTypeLookupData && StringUtils.isNotEmpty(prodSubTypeLookupData.getDescription())) {
				nxProdSubTypeVal=prodSubTypeLookupData.getDescription();
			}
			if (StringConstants.VERSION_2.equals(requestMap.get(StringConstants.REST_VERSION))) {
				JSONArray siteArray = (JSONArray) designDetails.get("site");
				for (Object site : siteArray) {
					((JSONObject) site).put("nxProductSubType",nxProdSubTypeVal);
				}
			} else {
				designDetails.put("nxProductSubType",nxProdSubTypeVal);
			}
		}
		
	}
	
	public <T> T processCustomFields(NxMpConfigJsonMapping mappingData,Object input,Map<String, Object> requestMap,Class<T> clazz) {
		T result=null;
		if(mappingData.getKey().contains(MyPriceConstants.DIVERSITY_SERVICE)) {
			if(clazz == String.class) {
				return clazz.cast(getDiversityValue(mappingData, input));
			}
		}else if(mappingData.getKey().equalsIgnoreCase(CustomJsonConstants.INT_JURISDICTION) &&
				(mappingData.getOffer().equals(MyPriceConstants.ASE_OFFER_NAME) || 
						MyPriceConstants.ASENOD_OFFER_NAME.equalsIgnoreCase(mappingData.getOffer())) ) {
			if(clazz == String.class) {
				if(MyPriceConstants.ASENOD_OFFER_NAME.equalsIgnoreCase(mappingData.getOffer())) {
					return clazz.cast(processJurisdictionASENoD(mappingData, input));
				}else {
					return clazz.cast(processJurisdictionASE(mappingData, input));
				}
				
			}
		}else if(mappingData.getKey().equalsIgnoreCase(CustomJsonConstants.INT_JURISDICTION) &&
				mappingData.getOffer().equals(MyPriceConstants.ADE_OFFER_NAME)) {
			if(clazz == String.class) {
				return clazz.cast(processJurisdictionADE(mappingData, input));
			}
		}else if(requestMap.containsKey(mappingData.getKey())&& null!=requestMap.get(mappingData.getKey())) {
			Object o=requestMap.get(mappingData.getKey());
			result=restCommonUtil.handleCast(o, clazz);
		    requestMap.remove(mappingData.getKey());
		}
		return result;
	}
	
	protected String getDiversityValue(NxMpConfigJsonMapping mappingData, Object inputDesignDetails) {
		String result=MyPriceConstants.NO;
		boolean isExists=nexxusJsonUtility.isExists(inputDesignDetails, mappingData.getInputPath());
		if(isExists) {
			result=MyPriceConstants.YES;
		}
		return result;
	}
	
	
	/**
	 * Process jurisdiction ASE.
	 *
	 * @param mappingData        the mapping data
	 * @param inputDesignDetails the input design details
	 * @param result             the result
	 * @return the string
	 */
	protected String processJurisdictionASE(NxMpConfigJsonMapping mappingData, Object inputDesignDetails) { 
		String regionIdItemValue=this.geValueByUdfIdAndCompId(inputDesignDetails, 
				MyPriceConstants.REGION_ID_UDF_ID, MyPriceConstants.PORT_COMPONENT_CD_ID,MyPriceConstants.ASE_OFFER_ID,"ASE"); 
		String ifOofIndItemValue=this.geValueByUdfIdAndCompId(inputDesignDetails, 
				MyPriceConstants.IF_OOF_IND_UDF_ID_ASE, MyPriceConstants.PORT_COMPONENT_CD_ID,MyPriceConstants.ASE_OFFER_ID,"ASE"); 
		String inRegionIndicator=this.geValueByUdfIdAndCompId(inputDesignDetails, 
				200059l, MyPriceConstants.PORT_COMPONENT_CD_ID,MyPriceConstants.ASE_OFFER_ID,"ASE"); 
		String jurisdictionItemValue=restCommonUtil.getItemValueUsingJsonPath(mappingData.getInputPath(),inputDesignDetails,String.class); 
		if(StringUtils.isNotEmpty(jurisdictionItemValue)) { 
			if((jurisdictionItemValue.equals("FCC") || 
					jurisdictionItemValue.equals("Interstate (FCC) Access (Interstate)")) && 
					"13STATES".equals(regionIdItemValue) && 
					!"N".equalsIgnoreCase(inRegionIndicator)) { 
				//If Jurisdiction is "FCC" or "Inerstate (FCC) Access (Interstate)" AND IF/OOF = IF AND 
				return "FCC - 12 States"; 
			}else if((jurisdictionItemValue.equals("FCC") || 
					jurisdictionItemValue.equals("Interstate (FCC) Access (Interstate)")) && 
					"9STATES".equals(regionIdItemValue) && 
					!"N".equalsIgnoreCase(inRegionIndicator)) { 
				//If Jurisdiction is "FCC" or "Inerstate (FCC) Access (Interstate)" AND IF/OOF = IF AND 
				return "FCC - 9 States"; 
			}else if((jurisdictionItemValue.equals("FCC") || 
					jurisdictionItemValue.equals("Interstate (FCC) Access (Interstate)")) && 
					("N".equalsIgnoreCase(inRegionIndicator))) { 
				//If Jurisdiction is "FCC" or "Inerstate (FCC) Access (Interstate)" AND IF/OOF = OOF 
				return "FCC - LNS-OOR"; 
			}else if(jurisdictionItemValue.equals("State Access")) { 
				//If Jurisdiction is "State Access" 
				String state=restCommonUtil.getItemValueUsingJsonPath(STATE_PATH,inputDesignDetails,String.class); 
				if(StringUtils.isNotEmpty(state)) { 
					if(!state.equals("FL")) { 
						return state; 
					}else if(("IF").equals(ifOofIndItemValue) || ("OOF").equals(ifOofIndItemValue)) { 
						return state; 
					}else { 
						return "FL - LNS-OOR"; 
					} 
				} 
			} 
		} 
		return null; 
	}
	
	/**
	 * Process jurisdiction ADE.
	 *
	 * @param mappingData the mapping data
	 * @param inputDesignDetails the input design details
	 * @param result the result
	 * @return the string
	 */
	protected String processJurisdictionADE(NxMpConfigJsonMapping mappingData, Object inputDesignDetails) {
		/*String ifOofIndItemValue=this.geValueByUdfIdAndCompId(inputDesignDetails, 
				MyPriceConstants.IF_OOF_IND_UDF_ID_ADE, MyPriceConstants.END_POINT_COMPONENT_CD_ID,MyPriceConstants.ADE_OFFER_ID);*/
		String jurisdictionAttr=restCommonUtil.getItemValueUsingJsonPath(mappingData.getInputPath(),inputDesignDetails,String.class);
		String jurisdictionItemValue=this.getDataFromSalesLookUp(jurisdictionAttr,MyPriceConstants.ADE_OFFER_ID,
				mappingData.getUdfId(),mappingData.getComponentId());
		String regionIdItemValue=this.geValueByUdfIdAndCompId(inputDesignDetails, 
				200216l, MyPriceConstants.END_POINT_COMPONENT_CD_ID,MyPriceConstants.ADE_OFFER_ID,"ADI"); 
		/*if(StringUtils.isNotEmpty(jurisdictionItemValue) && StringUtils.isNotEmpty(ifOofIndItemValue)) {
			if(jurisdictionItemValue.equals("Interstate (FCC) Access (Interstate)")&& 
					ifOofIndItemValue.equals("IF")) {
				//If "Jurisdiction" is 'Interstate (FCC) Access (Interstate)' AND  "IF/OOF Indicator" is 'IF'
				return "FCC - Interstate 21 states";
			}else if(jurisdictionItemValue.equals("Interstate (FCC) Access (Interstate)") && 
					ifOofIndItemValue.equals("OOF")) {
				//If "Jurisdiction" is 'Interstate (FCC) Access (Interstate)' AND "IF/OOF indicator" is 'OOF'
				return "FCC - Interstate LNS-OOR";
			}else if(jurisdictionItemValue.equals("Intrastate Access (Interlata/Intrastate)") && 
					ifOofIndItemValue.equals("IF")) {
				//If "Jurisdiction" is 'Intrastate Access (Interlata/Intrastate)' AND  "IF/OOF Indicator" is 'IF'
				return "Intrastate Access - 21 states";
			}else if(jurisdictionItemValue.equals("Intrastate Access (Interlata/Intrastate)") && 
					ifOofIndItemValue.equals("OOF")) {
				//If "Jurisdiction" is 'Intrastate Access (Interlata/Intrastate)' AND "IF/OOF indicator" is 'OOF'
				return "Intrastate Access - LNS-OOR";
			}else {
				String state=restCommonUtil.getItemValueUsingJsonPath(STATE_PATH,inputDesignDetails,String.class);
				if(StringUtils.isNotEmpty(state)) {
					return this.getDataFromNxLookUp(state, "mp_ade_jurisdiction_state");
				}
			}
		}*/
		
		if(StringUtils.isNotEmpty(jurisdictionItemValue)) {
			if(jurisdictionItemValue.equals("Interstate (FCC) Access (Interstate)")&& 
					"N".equalsIgnoreCase(regionIdItemValue)) {
				return "FCC - Interstate LNS-OOR";
			}else if(jurisdictionItemValue.equals("Interstate (FCC) Access (Interstate)")) {
				return "FCC - Interstate 21 states";
			}else if(jurisdictionItemValue.equals("Intrastate Access (Interlata/Intrastate)") && 
					"N".equalsIgnoreCase(regionIdItemValue)) {
				return "Intrastate Access - LNS-OOR";
			}else if(jurisdictionItemValue.equals("Intrastate Access (Interlata/Intrastate)")) {
				return "Intrastate Access - 21 states";
			}else {
				String state=restCommonUtil.getItemValueUsingJsonPath(STATE_PATH,inputDesignDetails,String.class);
				if(StringUtils.isNotEmpty(state)) {
					return this.getDataFromNxLookUp(state, "mp_ade_jurisdiction_state");
				}
			}
		}
		return null;
	}
	
	
	protected String geValueByUdfIdAndCompId(Object inputDesignDetails,Long udfId,Long componenetId,Long offerId, String offerName) {
		String path="$..component.[?(@.componentCodeId=="+componenetId+")]."
				+ "designDetails.[?(@.udfId=="+udfId+")].udfAttributeId.[*]";
		return this.getDataFromSalesLookUp(restCommonUtil.getItemValueUsingJsonPath(path,inputDesignDetails,String.class),
				offerId,udfId,componenetId);
	}

	
	protected String getDataFromSalesLookUp(String input, Long offerId,Long udfId,Long componentId) {
		if(StringUtils.isNotEmpty(input) && null!=offerId && null!=udfId && null!=componentId){
			List<Object> result= lineItemProcessingDao.getDataFromSalesLookUpTbl(input,offerId,udfId,
					componentId);
			if(CollectionUtils.isNotEmpty(result) && result.get(0)!=null) {
				input= String.valueOf(result.get(0));
			}
		}
		
		return input;
	}
	
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
	
	
	@SuppressWarnings("unchecked")
	public void processConfigDataFromCustomeRules(Map<String, Object> requestMap,Object inputDesign) {
		String productType=requestMap.get(MyPriceConstants.PRODUCT_TYPE)!=null?(String) requestMap.get(MyPriceConstants.PRODUCT_TYPE):"";
		String offerName = requestMap.get(MyPriceConstants.OFFER_NAME) != null? (String) requestMap.get(MyPriceConstants.OFFER_NAME): "";
		String subOfferName = requestMap.get(MyPriceConstants.SUB_OFFER_NAME) != null? (String) requestMap.get(MyPriceConstants.SUB_OFFER_NAME): "";
		this.processPortQtyAndCIRQtyASE(requestMap, inputDesign,offerName);
		JSONObject designDetails=JacksonUtil.toJsonObject(inputDesign.toString());
		List<NxLookupData> rulesData = getRuleData(productType, offerName,subOfferName);
		if(CollectionUtils.isNotEmpty(rulesData)) {
			for(NxLookupData ruleObj:rulesData) {
				if(StringUtils.isNotEmpty(ruleObj.getCriteria()) && (ruleObj.getCriteria().contains("##"))) {
					String[] rule = ruleObj.getCriteria().split("##");
					String jsonPath=rule[0]!=null?rule[0]:"";
					String criteria=rule[1]!=null?rule[1]:"";
					LinkedHashMap<String, Object>  criteriaMap=(LinkedHashMap<String, Object>) nexxusJsonUtility.convertStringJsonToMap(criteria);
					if(offerName.equals(MyPriceConstants.ASE_OFFER_NAME) ||	(offerName.equals(MyPriceConstants.ASENOD_OFFER_NAME) && MyPriceConstants.ASENOD_IR.equals(subOfferName))) {
						this.processConfigDataASE(requestMap, inputDesign, offerName, designDetails, jsonPath, criteriaMap);
					}else if(offerName.equals(MyPriceConstants.ASENOD_OFFER_NAME) && MyPriceConstants.ASENOD_3PA.equals(subOfferName)) {
						this.processConfigDataASENoD3PA(requestMap, inputDesign, jsonPath, criteriaMap);	
					}else if(offerName.equals(MyPriceConstants.ADE_OFFER_NAME)) {
						this.processConfigDataADE(requestMap, inputDesign, offerName, designDetails, jsonPath, criteriaMap);
					}
				}
			}
			
		}
	}
	

	@SuppressWarnings("unchecked")
	protected void processConfigDataASE(Map<String, Object> requestMap, Object inputDesign, String offerName,
			JSONObject designDetails, String jsonPath, LinkedHashMap<String, Object> criteriaMap) {
		Map<String,Map<String,String>> usocDataMap=this.createDataMapByUsocId(JacksonUtil.toJsonObject(inputDesign.toString()),requestMap,jsonPath);
		if(MapUtils.isNotEmpty(usocDataMap)) {
			int index=0;
			for (Map.Entry<String,Map<String,String>>  usocEntry : usocDataMap.entrySet()) {
				String usocId=usocEntry.getKey();
				Map<String,String> usocFieldsDataMap=usocEntry.getValue();
				//derived the New/Existing/Migration category for usoc id using rule from nx_look_up table
				String usocIdCategory=configAndUpdatePricingUtil.getUsocIdCategory(designDetails, offerName, usocFieldsDataMap,requestMap);
				if(StringUtils.isNotEmpty(usocIdCategory)) {
					for (Map.Entry<String,Object> x : criteriaMap.entrySet()) {
						if(x.getValue() instanceof Map<?, ?>) {
							if(!requestMap.containsKey(x.getKey())) {
								requestMap.put(x.getKey(), new ArrayList<Map<String,Object>>());
							}
							Map<String,Object> innerData=new HashMap<String, Object>();
							Map<String,String> mapData= (Map<String, String>) x.getValue();
							for (Map.Entry<String,String> t : mapData.entrySet()) {
								if(t.getValue().equals("usocId")) {
									innerData.put(t.getKey(), usocId);
								}else if(t.getValue().equals("index")) {
									innerData.put(t.getKey(), index);
								}else if(t.getValue().equalsIgnoreCase(usocIdCategory)){
									innerData.put(t.getKey(), 1);
								}
							}
							index++;
							((ArrayList<Map<String,Object>>)requestMap.get(x.getKey())).add(innerData);
						}else if(x.getValue().equals("size")) {
							requestMap.put(x.getKey(), index);
						}
					
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void processConfigDataASENoD3PA(Map<String, Object> requestMap, Object inputDesign, String jsonPath,
			LinkedHashMap<String, Object> criteriaMap) {
		Map<String,Map<String,String>> usocDataMap=this.createDataMapByUsocId(JacksonUtil.toJsonObject(inputDesign.toString()),requestMap,jsonPath);
		if(MapUtils.isNotEmpty(usocDataMap)) {
			int index=0;
			for (Map.Entry<String,Map<String,String>>  usocEntry : usocDataMap.entrySet()) {
				String usocId=usocEntry.getKey();
				//convert request usocId using lookupdata range	
				String updatedUsocId=getConvertedUsocIdFor3PA(requestMap, usocId);
				for (Map.Entry<String,Object> x : criteriaMap.entrySet()) {
					if(x.getValue() instanceof Map<?, ?>) {
						if(!requestMap.containsKey(x.getKey())) {
							requestMap.put(x.getKey(), new ArrayList<Map<String,Object>>());
						}
						Map<String,Object> innerData=new HashMap<String, Object>();
						Map<String,String> mapData= (Map<String, String>) x.getValue();
						for (Map.Entry<String,String> t : mapData.entrySet()) {
							if(t.getValue().equals("usocId")) {
								innerData.put(t.getKey(), updatedUsocId);
							}else if(t.getValue().equals("index")) {
								innerData.put(t.getKey(), index);
							}
						}
						index++;
						((ArrayList<Map<String,Object>>)requestMap.get(x.getKey())).add(innerData);
					}else if(x.getValue().equals("size")) {
						requestMap.put(x.getKey(), index);
					}
				
				}
			}
			
		}
	}

	@SuppressWarnings("unchecked")
	protected void processConfigDataADE(Map<String, Object> requestMap, Object inputDesign, String offerName,
			JSONObject designDetails, String jsonPath, LinkedHashMap<String, Object> criteriaMap) {
		//map holding key as beid and value as list of field map from circuit,and Endpoint level
		Map<String,List<Map<String,String>>> usocDataMap=this.createDataMapByUsocIdForAde(JacksonUtil.toJsonObject(inputDesign.toString()),requestMap,jsonPath);
		AtomicInteger cnt=new  AtomicInteger(0);
		if(MapUtils.isNotEmpty(usocDataMap)) {
			int index=0;
			Map<Integer, List<GroupingEnitity>> groupMap=new HashMap<Integer, List<GroupingEnitity>>();
			for (Map.Entry<String,List<Map<String,String>>>  usocEntry : usocDataMap.entrySet()) {
				String usocId=usocEntry.getKey();
				List<Map<String,String>> data=usocEntry.getValue();
				for(Map<String,String> usocFieldsDataMap:data) {
					//derived the New/Existing/Migration category for usoc id using rule from nx_look_up table
					String usocIdCategory=configAndUpdatePricingUtil.getUsocIdCategory(designDetails, offerName, usocFieldsDataMap,requestMap);
					if(StringUtils.isNotEmpty(usocIdCategory)) {
						LinkedHashMap<String,String> filterRulesMap=new LinkedHashMap<String, String>();
						filterRulesMap.put("usocId", usocId);
						filterRulesMap.put("usocIdCategory", usocIdCategory);
						Integer grpId=restCommonUtil.generateGroupId(null, groupMap, filterRulesMap, cnt, requestMap);
						for (Map.Entry<String,Object> x : criteriaMap.entrySet()) {
							if(x.getValue() instanceof Map<?, ?>) {
								if(!requestMap.containsKey(x.getKey())) {
									requestMap.put(x.getKey(), new ArrayList<Map<String,Object>>());
								}
								List<Map<String,Object>> lst=(List<Map<String, Object>>) requestMap.get(x.getKey());
								Map<String,Object> innerData=CollectionUtils.isNotEmpty(lst) && null!=grpId && restCommonUtil.hasIndex(grpId, lst)?lst.get(grpId):null;
								Map<String,String> mapData= (Map<String, String>) x.getValue();
								if(innerData==null) {
									innerData=new HashMap<String, Object>();
									for (Map.Entry<String,String> t : mapData.entrySet()) {
										if(t.getValue().equals("usocId")) {
											innerData.put(t.getKey(), usocId);
										}else if(t.getValue().equals("index")) {
											innerData.put(t.getKey(), index);
										}else if(t.getValue().equalsIgnoreCase(usocIdCategory)){
											restCommonUtil.incrementValue(innerData, t.getKey());
										}
									}
									restCommonUtil.fillAndSetList(grpId, innerData, lst);
									index++;
								}else {
									//if its from same group then we r incrementing count
									restCommonUtil.incrementValue(innerData, restCommonUtil.getKeysByValue(mapData,usocIdCategory));
									restCommonUtil.fillAndSetList(grpId, innerData, lst);
								}
							}else if(x.getValue().equals("size")) {
								requestMap.put(x.getKey(), index);
							}	
						}
					}
				}
			}
		}
	}
	
	protected List<NxLookupData> getRuleData(String productType, String offerName,String subOffer) {
		if(StringUtils.isNotEmpty(subOffer)) {
			offerName=offerName.concat("/").concat(subOffer);
		}
		List<NxLookupData> rulesData=nxLookupDataRepository.
				findByDatasetNameAndItemIdAndDescription(CustomJsonConstants.CUSTOM_CONFIG_RULES_REST,offerName, productType);
		return rulesData;
	}
	
	
	
	
	public Map<String,Map<String,String>> createDataMapByUsocId(JSONObject inputDesignDetails,Map<String, Object> requestMap,String basePath){
		Map<String,Map<String,String>> result=new TreeMap<>();	
		String priceScenarioId=requestMap.get(StringConstants.PRICE_SCENARIO_ID)!=null?
				String.valueOf(requestMap.get(StringConstants.PRICE_SCENARIO_ID)):null;
		String updatedPath=basePath.replaceAll("%priceScenarioId%", priceScenarioId);
		//priceAttribute filter on base rateGroup for ASE and priceUnit for ADE
		List<PriceAttributes> priceAttributesLst=configAndUpdatePricingUtil.getPriceAttributes(inputDesignDetails,updatedPath);
		for(PriceAttributes obj:priceAttributesLst) {
			Map<String,String> filedDataMap=new HashMap<>();
			filedDataMap.put("rateGroup", obj.getRateGroup());
			filedDataMap.put("priceGroup", obj.getPriceGroup());
			filedDataMap.put("priceType", obj.getPriceType());
			result.put(obj.getBeid(), filedDataMap);
		}
		return result;
		
	}
	
	protected Map<String,List<Map<String,String>>>  createDataMapByUsocIdForAde(JSONObject inputDesignDetails,Map<String, Object> requestMap,String basePath){
		String priceScenarioId=requestMap.get(StringConstants.PRICE_SCENARIO_ID)!=null?
				String.valueOf(requestMap.get(StringConstants.PRICE_SCENARIO_ID)):null;
		List<ComponentDetails> componentData=configAndUpdatePricingUtil.getComponentList(inputDesignDetails, "$..priceDetails.componentDetails.*");		
		Map<String,List<Map<String,String>>> dataMap=new HashMap<String, List<Map<String,String>>>();
		if(CollectionUtils.isNotEmpty(componentData)) {
			for(ComponentDetails c:componentData) {
				String updatedPath=basePath.replaceAll("%priceScenarioId%", priceScenarioId);
				List<PriceAttributes> priceAttributesLst= configAndUpdatePricingUtil.getPriceAttributes(c,updatedPath);
				 Map<String,Map<String,String>> map=new HashMap<>();
				 if(CollectionUtils.isNotEmpty(priceAttributesLst)) {
					 for(PriceAttributes obj:priceAttributesLst) {
						 Map<String,String> filedDataMap=new HashMap<>();
						 filedDataMap.put("usocId", obj.getBeid());
						 filedDataMap.put("rateGroup", obj.getRateGroup());
						 filedDataMap.put("priceGroup", obj.getPriceGroup());
						 filedDataMap.put("priceType", obj.getPriceType());	
						 map.put(obj.getBeid(),filedDataMap);
					 }
					 for (Map.Entry<String,Map<String,String>>  usocEntry : map.entrySet()) {
						 String uscoId=usocEntry.getKey();
						 if(!dataMap.containsKey(uscoId)) {
							 dataMap.put(uscoId, new ArrayList<Map<String,String>>());
						 }
						 dataMap.get(uscoId).add(usocEntry.getValue());
					 }
				 }
			}
		}
		
		return dataMap;
		
	}
	

	
	
	public void process3PAUcoIdFromLookup(Map<String, Object> requestMap) {
		String offerName = requestMap.get(MyPriceConstants.OFFER_NAME) != null? (String) requestMap.get(MyPriceConstants.OFFER_NAME): "";
		String subOfferName = requestMap.get(MyPriceConstants.SUB_OFFER_NAME) != null? (String) requestMap.get(MyPriceConstants.SUB_OFFER_NAME): "";

		if(offerName.equals(MyPriceConstants.ASENOD_OFFER_NAME) && MyPriceConstants.ASENOD_3PA.equals(subOfferName)) {
			//get rule data from lookup data table for converting usocId to myPrice format
			Map<String,List<String>> result=new HashMap<String, List<String>>();
			List<NxLookupData> nxLookupLst=nxLookupDataRepository.findByDatasetName(MyPriceConstants.ASENOD_3PA_USOC_RANGE_DATASET);
			 Optional.ofNullable(nxLookupLst).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull).
			    forEach( data -> {
			    	if(StringUtils.isNotEmpty(data.getCriteria()) && StringUtils.isNotEmpty(data.getItemId())) {
			    		List<String> range=new ArrayList<String>(Arrays.asList(data.getCriteria().split(Pattern.quote(","))));
			    		result.put(data.getItemId(), range);
			    	}
			 });
			 requestMap.put(MyPriceConstants.ASENOD_3PA_USOC_RANGE, result);
		}
	}
	

	@SuppressWarnings("unchecked")
	public String getConvertedUsocIdFor3PA(Map<String, Object> requestMap,String inputUsocId) {
		Map<String,List<String>> usocCriteriaMap=requestMap.containsKey(MyPriceConstants.ASENOD_3PA_USOC_RANGE)?
				(Map<String,List<String>>)requestMap.get(MyPriceConstants.ASENOD_3PA_USOC_RANGE):null;
		//convert  usocId in myPrice format and append in string
		if(MapUtils.isNotEmpty(usocCriteriaMap)) {
			for (Map.Entry<String,List<String>>  usocCriteria : usocCriteriaMap.entrySet()) {
				List<String> criteria=usocCriteria.getValue();
				if(criteria.contains(inputUsocId)) {
					return usocCriteria.getKey();
				}
			}
		}
		return inputUsocId;
		
	}
	
	protected void processPortQtyAndCIRQtyASE(Map<String, Object> requestMap, Object inputDesign,String offerName) {
		if(offerName.equals(MyPriceConstants.ASE_OFFER_NAME)) {
			List<String> reqMacdActivityUdfAttr=restCommonUtil.geDataInListString(inputDesign, MyPriceConstants.ASE_MCAD_ACTIVITY_PATH);
			requestMap.put(MyPriceConstants.REQ_MCAD_ACTIVITY_UDFATTR, reqMacdActivityUdfAttr);
			String reqMcadType=configAndUpdatePricingUtil.getRequestMacdTypeForASE(inputDesign, requestMap);
			requestMap.put(MyPriceConstants.REQ_MCAD_TYPE_VALUE, reqMcadType);
			List<NxLookupData> macdOrder=nxLookupDataRepository.getOrderForMacd("ASE_MCAD_ORDER");
			requestMap.put(MyPriceConstants.ASE_MCAD_ORDER_DATA, macdOrder);
			String portQtyPf=configAndUpdatePricingUtil.getPortQtyPf(requestMap);
			requestMap.put(MyPriceConstants.REST_PORT_QTY_VAL, portQtyPf);
			requestMap.put(MyPriceConstants.REST_PORT_QTY_DISPLAY_VAL, portQtyPf);
			String cirQtyPf=configAndUpdatePricingUtil.getCIRQtyPf(requestMap);
			requestMap.put(MyPriceConstants.REST_CIR_QTY_VAL, cirQtyPf);
			requestMap.put(MyPriceConstants.REST_CIR_QTY_DISPLAY_VAL, cirQtyPf);
		}
	}
	
	protected String processJurisdictionASENoD(NxMpConfigJsonMapping mappingData, Object inputDesignDetails) { 
		if(MyPriceConstants.ASENOD_IR.equals(mappingData.getSubOffer())) {
			if(isAsenodWholesale(mappingData, inputDesignDetails) && !isLNSOORFeature(mappingData, inputDesignDetails)) {
				return processJurisdictionASENoDWholeSale(mappingData, inputDesignDetails);
			}else if(mappingData.getInputPath().contains(MyPriceConstants.OR_CONDITION_SEPERATOR)){
				List<String> pathList= new ArrayList<String>(Arrays.asList(mappingData.getInputPath().split(
						Pattern.quote(MyPriceConstants.OR_CONDITION_SEPERATOR))));
				for(String path:pathList) {
					String itemValue=null;
						itemValue=restCommonUtil.getItemValueUsingJsonPath(path.trim(), inputDesignDetails, String.class);
						if(null!=itemValue) {
							if(StringUtils.isNotEmpty(mappingData.getDatasetName())) {
								itemValue=restCommonUtil.processDataSetName(itemValue, mappingData.getDatasetName(), String.class);
							}
							if(null!=itemValue) {
								return itemValue;
							}
						}
				}
			}else {
				return restCommonUtil.getItemValueUsingJsonPath(mappingData.getInputPath(),inputDesignDetails,String.class); 
			}
		}
		return null;
	}
	
	protected boolean isAsenodWholesale(NxMpConfigJsonMapping mappingData, Object inputDesignDetails) {
		return nexxusJsonUtility.isExists(inputDesignDetails, PORT_IND_PATH);
	}
	
	protected boolean isLNSOORFeature(NxMpConfigJsonMapping mappingData, Object inputDesignDetails) {
		return nexxusJsonUtility.isExists(inputDesignDetails, LNS_TXT_PATH);
	}
	
	protected String processJurisdictionASENoDWholeSale(NxMpConfigJsonMapping mappingData, Object inputDesignDetails) { 
		String regionIdItemValue=this.geValueByUdfIdAndCompId(inputDesignDetails, 
				MyPriceConstants.REGION_ID_UDF_ID, MyPriceConstants.PORT_COMPONENT_CD_ID,MyPriceConstants.ASE_OFFER_ID, "ASE"); 
		if(StringUtils.isNotEmpty(regionIdItemValue) && "13STATES".equals(regionIdItemValue)) { 
			return "FCC - 12 States"; 
		} 
		return "FCC - 9 States"; 
	}
}
