package com.att.sales.nexxus.myprice.transaction.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.constant.CustomJsonConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.helper.GroupingEnitity;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.jayway.jsonpath.TypeRef;

import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicInteger;

@Component
public class ConfigAndUpdateRestUtilFmo {
	
	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Autowired
	private NexxusJsonUtility nexxusJsonUtility;
	
	@Autowired
	private RestCommonUtil restCommonUtil;
	
	@Autowired
	private JsonPathUtil jsonPathUtil;

	@SuppressWarnings("unchecked")
	public void processConfigDataFromCustomeRules(Map<String, Object> requestMap,Object inputDesign) {
		String offerName = requestMap.get(MyPriceConstants.OFFER_NAME) != null? (String) requestMap.get(MyPriceConstants.OFFER_NAME): "";
		String productType=requestMap.get(MyPriceConstants.PRODUCT_TYPE)!=null?(String) requestMap.get(MyPriceConstants.PRODUCT_TYPE):"";
		List<NxLookupData> rulesData=nxLookupDataRepository.
				findByDatasetNameAndItemIdAndDescription(CustomJsonConstants.CUSTOM_CONFIG_RULES_REST,offerName, productType);
		if(CollectionUtils.isNotEmpty(rulesData)) {
			if(MyPriceConstants.BVoIP.equals(offerName) || MyPriceConstants.BVoIP_INTERNATIONAL.equals(offerName)) {
				processBvoip(requestMap, inputDesign, rulesData);
			}else {
				Object updateDesign=this.handleDesignForCustomCode(inputDesign);
				int index=0;
				AtomicInteger cnt=new  AtomicInteger(0);
				for(NxLookupData ruleObj:rulesData) {
					if(StringUtils.isNotEmpty(ruleObj.getCriteria()) && (ruleObj.getCriteria().contains("%"))) 
					{
						String[] rule = ruleObj.getCriteria().split("%");
						String jsonPath=0<rule.length && rule[0]!=null?rule[0]:"";
						String criteria=1<rule.length && rule[1]!=null?rule[1]:"";
						
						List<Object>  designDatalst=nexxusJsonUtility.getValueLst(updateDesign,jsonPath);
						if(CollectionUtils.isNotEmpty(designDatalst)) {
							LinkedHashMap<String, Object>  criteriaMap=(LinkedHashMap<String, Object>) nexxusJsonUtility.convertStringJsonToMap(criteria);
							
							//get rules to calculate the count on basis of filter
							LinkedHashMap<String, String>  filterRuleMap=criteriaMap.get(CustomJsonConstants.COUNT_FILTER)!=null?
									(LinkedHashMap<String, String>)criteriaMap.get(CustomJsonConstants.COUNT_FILTER):null;
							//remove counterFilter from main criteria map		
							criteriaMap.remove(CustomJsonConstants.COUNT_FILTER);
							Map<Integer, List<GroupingEnitity>> groupMap=new HashMap<Integer, List<GroupingEnitity>>();
							for(Object d:designDatalst) {
								//Group id on basis of filter rules
								//This grpId used to calculate count on basis of filter criteria
								Integer grpId=restCommonUtil.generateGroupId(d,groupMap,filterRuleMap,cnt,requestMap);
								
								LinkedHashMap<String, String> pirceData=(LinkedHashMap<String, String>)d;
								
								collectReqElementType(requestMap, pirceData);
								
								for (Map.Entry<String,Object> x : criteriaMap.entrySet()) {
									if(x.getValue() instanceof Map<?, ?>) {
										if(!requestMap.containsKey(x.getKey())) {
											requestMap.put(x.getKey(), new ArrayList<Map<String,Object>>());
											index=0;
										}
										List<Map<String,Object>> lst=(List<Map<String, Object>>) requestMap.get(x.getKey());
										Map<String,Object> innerData=CollectionUtils.isNotEmpty(lst) && null!=grpId && restCommonUtil.hasIndex(grpId, lst)?lst.get(grpId):null;
										Map<String,String> mapData= (Map<String, String>) x.getValue();
										if(innerData==null) {
											//for new record
											innerData=new HashMap<String, Object>();
											for (Map.Entry<String,String> t : mapData.entrySet()) {
												if(t.getValue().equals("index")) {
													innerData.put(t.getKey(),index);
												}/*else if(t.getValue().equals("count")) {
													restCommonUtil.incrementValue(innerData, t.getKey());
												}*/else {
													innerData.put(t.getKey(), pirceData.get(t.getValue()));
												}
											}
											restCommonUtil.fillAndSetList(grpId, innerData, lst);
											index++;
										}else {
											//if its from same group then we r incrementing count
										//	restCommonUtil.incrementValue(innerData, restCommonUtil.getKeysByValue(mapData, "count"));
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
			
		}
	}


	@SuppressWarnings("unchecked")
	public void processBvoip(Map<String, Object> requestMap, Object inputDesign, List<NxLookupData> rulesData) {
		int index=0;
		AtomicInteger cnt=new  AtomicInteger(0);
		for(NxLookupData ruleObj:rulesData) {
			if(StringUtils.isNotEmpty(ruleObj.getCriteria()) && (ruleObj.getCriteria().contains("%"))) 
			{
				String[] rule = ruleObj.getCriteria().split("%");
				String jsonPath=0<rule.length && rule[0]!=null?rule[0]:"";
				String criteria=1<rule.length && rule[1]!=null?rule[1]:"";
				
				List<Object>  designDatalst=nexxusJsonUtility.getValueLst(inputDesign,jsonPath);
				if(CollectionUtils.isNotEmpty(designDatalst)) {
					LinkedHashMap<String, Object>  criteriaMap=(LinkedHashMap<String, Object>) nexxusJsonUtility.convertStringJsonToMap(criteria);
					
					//get rules to calculate the count on basis of filter
					LinkedHashMap<String, String>  filterRuleMap=criteriaMap.get(CustomJsonConstants.COUNT_FILTER)!=null?
							(LinkedHashMap<String, String>)criteriaMap.get(CustomJsonConstants.COUNT_FILTER):null;
					//remove counterFilter from main criteria map		
					criteriaMap.remove(CustomJsonConstants.COUNT_FILTER);
					Map<Integer, List<GroupingEnitity>> groupMap=new HashMap<Integer, List<GroupingEnitity>>();
					for(Object d:designDatalst) {
						//Group id on basis of filter rules
						//This grpId used to calculate count on basis of filter criteria
						Integer grpId=restCommonUtil.generateGroupId(d,groupMap,filterRuleMap,cnt,requestMap);
						
						LinkedHashMap<String, String> pirceData=(LinkedHashMap<String, String>)d;
						
						//collectReqElementType(requestMap, pirceData);
						
						for (Map.Entry<String,Object> x : criteriaMap.entrySet()) {
							if(x.getValue() instanceof Map<?, ?>) {
								if(!requestMap.containsKey(x.getKey())) {
									requestMap.put(x.getKey(), new ArrayList<Map<String,Object>>());
									index=0;
								}
								List<Map<String,Object>> lst=(List<Map<String, Object>>) requestMap.get(x.getKey());
								Map<String,Object> innerData=CollectionUtils.isNotEmpty(lst) && null!=grpId && restCommonUtil.hasIndex(grpId, lst)?lst.get(grpId):null;
								Map<String,String> mapData= (Map<String, String>) x.getValue();
								if(innerData==null) {
									//for new record
									innerData=new HashMap<String, Object>();
									for (Map.Entry<String,String> t : mapData.entrySet()) {
										if(t.getValue().equals("index")) {
											innerData.put(t.getKey(),index);
										}else if(t.getValue().equals("nxSiteIdList")) {
											this.createSiteIdData(innerData, t.getKey(), pirceData.get("componentParentId"), inputDesign);
										}else {
											innerData.put(t.getKey(), pirceData.get(t.getValue()));
										}
									}
									restCommonUtil.fillAndSetList(grpId, innerData, lst);
									index++;
								}else {
									String key=restCommonUtil.getKeysByValue(mapData, "nxSiteIdList");
									this.createSiteIdData(innerData, key, pirceData.get("componentParentId"), inputDesign);
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
	
	
	protected void createSiteIdData(Map<String, Object> innerData,String key,Object siteId,Object inputDesign) {
		String path="$..*.[?(@.siteId == "+siteId+")].nxSiteId";
		Object data=nexxusJsonUtility.getValue(inputDesign, path);
		if (!innerData.containsKey(key)) {
			innerData.put(key, new StringBuilder());
		}
		 StringBuilder sb=(StringBuilder)innerData.get(key);
		if(sb.length()>0) {
			sb.append(MyPriceConstants.COMMA_SEPERATOR);
		}
		if(data != null)
			sb.append(data);
		innerData.put(key,sb);
	}

	@SuppressWarnings("unchecked")
	protected void collectReqElementType(Map<String, Object> requestMap, LinkedHashMap<String, String> pirceData) {
		if(null!=pirceData.get("elementType")) {
			if(!requestMap.containsKey(CustomJsonConstants.ELEMENT_TYPE)) {
				requestMap.put(CustomJsonConstants.ELEMENT_TYPE, new HashSet<String>());
			}
			 ((HashSet<String>) requestMap.get(CustomJsonConstants.ELEMENT_TYPE)).add(pirceData.get("elementType"));
		}
	}
	
	@SuppressWarnings("unchecked")
	protected Object handleDesignForCustomCode(Object inputDesign) {
		if(null!=inputDesign) {
			if (inputDesign.toString().startsWith("[")) {
				//if json array then pick 0th element
				JSONArray j=JacksonUtil.toJsonArray(inputDesign.toString());
				JSONArray newJArray=new JSONArray();
				newJArray.add(j.get(0));
				return newJArray;
			}
		}
		return inputDesign;
	}
	
	@SuppressWarnings("unchecked")
	public void processCustomeFieldsUsingNxLookupData(Map<String,Object> requestMap, Object inputDesign) {
		String offerName=requestMap.get(MyPriceConstants.OFFER_NAME) != null ? (String)
					 requestMap.get(MyPriceConstants.OFFER_NAME) : "";
		if(MyPriceConstants.ADI_TDM.equals(offerName))	{
			String itemId = getDataInString(inputDesign, MyPriceConstants.FMO_TDM_SPEED_FIELDS_PATH);	
			if(StringUtils.isNotEmpty(itemId)) {
				NxLookupData rulesData = nxLookupDataRepository.
						findTopByDatasetNameAndItemId(MyPriceConstants.MP_TDM_CUSTOME_FIELDS_REST_DATASET, itemId);
				if(null!=rulesData && StringUtils.isNotEmpty(rulesData.getCriteria())) {
					Map<String,String> fieldMap = (Map<String,String>) 
							nexxusJsonUtility.convertStringJsonToMap(rulesData.getCriteria());
					if(null != fieldMap) {
						requestMap.putAll(fieldMap);
					}
				}
			}
		}
	}
	
	protected String getDataInString(Object inputDesignDetails, String path) {
		TypeRef<List<String>> ref = new TypeRef<List<String>>() {};
		List<String> dataLst = jsonPathUtil.search(inputDesignDetails, path, ref);
		if(CollectionUtils.isNotEmpty(dataLst)) {
			return dataLst.get(0);
		}
		return null;
	}
	
}
