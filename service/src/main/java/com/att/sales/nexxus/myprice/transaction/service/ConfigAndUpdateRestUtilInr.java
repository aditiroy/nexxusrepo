package com.att.sales.nexxus.myprice.transaction.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.constant.CustomJsonConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxMpConfigJsonMapping;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.helper.GroupingEnitity;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.att.aft.dme2.internal.gson.Gson;
import com.att.aft.dme2.internal.gson.GsonBuilder;

import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicInteger;
import groovy.util.Eval;

@Component
public class ConfigAndUpdateRestUtilInr {
	
	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Autowired
	private NexxusJsonUtility nexxusJsonUtility;
	
	@Autowired
	private RestCommonUtil restCommonUtil;
	
	@SuppressWarnings("unchecked")
	public void processConfigDataFromCustomeRules(Map<String, Object> requestMap,Object inputDesign) {
		String offerName = requestMap.get(MyPriceConstants.OFFER_NAME) != null? (String) requestMap.get(MyPriceConstants.OFFER_NAME): "";
		String productType=requestMap.get(MyPriceConstants.PRODUCT_TYPE)!=null?(String) requestMap.get(MyPriceConstants.PRODUCT_TYPE):"";
		List<NxLookupData> rulesData=nxLookupDataRepository.
				findByDatasetNameAndItemIdAndDescription(CustomJsonConstants.CUSTOM_CONFIG_RULES_REST,offerName, productType);
		List<NxLookupData> rulesDataString=nxLookupDataRepository.
				findByDatasetNameAndItemIdAndDescription(CustomJsonConstants.CUSTOM_CONFIG_RULES_REST_STRING,offerName, productType);
		if(CollectionUtils.isNotEmpty(rulesDataString)) {
			if (	MyPriceConstants.RESTUSAGE.contains(offerName)) {
				this.processConfigDataFromCustomeRulesUsageProduct(requestMap, rulesDataString, inputDesign,offerName,true);
			}
		}
		if(CollectionUtils.isNotEmpty(rulesData)) {
			if(MyPriceConstants.SOURCE_INR.equalsIgnoreCase(productType) && MyPriceConstants.ANIRA.equals(offerName)) {
				this.processConfigDataFromCustomeRulesAnira(requestMap, rulesData, inputDesign);
			}else if(MyPriceConstants.SOURCE_USRP.equalsIgnoreCase(productType) &&(MyPriceConstants.AVTS.equals(offerName) ||
					MyPriceConstants.ANIRA.equals(offerName))) {
				this.processConfigDataFromCustomeRulesAVTS(requestMap, rulesData, inputDesign);
			}else if (	MyPriceConstants.RESTUSAGE.contains(offerName)) {
				//boolean parameter is to determine whether the request should be in string format or not
				this.processConfigDataFromCustomeRulesUsageProduct(requestMap, rulesData, inputDesign,offerName,false);
			}else if (MyPriceConstants.BVoIP_NON_USAGE.equalsIgnoreCase(offerName)) {
				this.processConfigDataFromCustomeRulesBVoIPNonUsage(requestMap, rulesData, inputDesign,offerName);
			}else if (	MyPriceConstants.ONENET_FEATURE.contains(offerName) || MyPriceConstants.VTNS_FEATURE.contains(offerName)) {
				this.processConfigDataFromCustomeRulesOneNetFeatureProduct(requestMap, rulesData, inputDesign,offerName,false);
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
												}else if(t.getValue().equals("count")) {
													restCommonUtil.incrementValue(innerData, t.getKey());
												}else {
													innerData.put(t.getKey(), pirceData.get(t.getValue()));
												}
											}
											restCommonUtil.fillAndSetList(grpId, innerData, lst);
											index++;
										}else {
											//if its from same group then we r incrementing count
											restCommonUtil.incrementValue(innerData, restCommonUtil.getKeysByValue(mapData, "count"));
											restCommonUtil.fillAndSetList(grpId, innerData, lst);
										}
									}else if(x.getValue().equals("size")) {
										requestMap.put(x.getKey(), index);
									}else {
										requestMap.put(x.getKey(), pirceData.get(x.getValue()));
									}
									
								}
								
							}
							
						}
					}
				}
			}
			
		}
	}
	
	private void processConfigDataFromCustomeRulesAVTS(Map<String, Object> requestMap, List<NxLookupData> rulesData,
			Object inputDesign) {
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
										}else if(t.getValue().equals("count")) {
											restCommonUtil.incrementValue(innerData, t.getKey());
										}else {
											innerData.put(t.getKey(), pirceData.get(t.getValue()));
										}
									}
									restCommonUtil.fillAndSetList(grpId, innerData, lst);
									index++;
								}else {
									String quantityKey=restCommonUtil.getKeysByValue(mapData, "quantity");
									if(StringUtils.isNotEmpty(quantityKey)){
										restCommonUtil.incrementQuantity(innerData,quantityKey,pirceData.get("quantity"));
										restCommonUtil.fillAndSetList(grpId, innerData, lst);
									}
									
									String countKey=restCommonUtil.getKeysByValue(mapData, "count");
									if(StringUtils.isNotEmpty(countKey)){
										//if its from same group then we r incrementing count
										restCommonUtil.incrementValue(innerData, countKey);
										restCommonUtil.fillAndSetList(grpId, innerData, lst);
									}
									
									String actualPriceKey=restCommonUtil.getKeysByValue(mapData, "actualPrice");
									if(StringUtils.isNotEmpty(actualPriceKey)){
										//if its from same group then we r passing the min value
										restCommonUtil.minValue(innerData, actualPriceKey,pirceData.get("actualPrice"));
										restCommonUtil.fillAndSetList(grpId, innerData, lst);
									}

								
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

	@SuppressWarnings("unchecked") 
	public void processConfigDataFromCustomeRulesOneNetFeatureProduct(Map<String, Object> requestMap,
			List<NxLookupData> rulesData, Object inputDesign, String offerName, boolean isStringRequest) {
		boolean isDataProcessed=false;
		int index=0;
		AtomicInteger cnt=new  AtomicInteger(0);
		List<NxLookupData> defaultData=null;
				//nxLookupDataRepository.findByDatasetNameAndItemIdAndDescription(MyPriceConstants.CUSTOM_CONFIG_RULES, offerName, MyPriceConstants.DEFAULT_VALUES);
		List<String> innerDataKeys = new ArrayList<>();
		for(NxLookupData ruleObj:rulesData) {
			if(StringUtils.isNotEmpty(ruleObj.getCriteria()) && (ruleObj.getCriteria().contains("%"))) 
			{
				String[] rule = ruleObj.getCriteria().split("%");
				String jsonPath=0<rule.length && rule[0]!=null?rule[0]:"";
				String criteria=1<rule.length && rule[1]!=null?rule[1]:"";
				List<Object>  designDatalst=nexxusJsonUtility.getValueLst(inputDesign,jsonPath);
						
				
				if(CollectionUtils.isNotEmpty(designDatalst)) {
					isDataProcessed=true;
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
						
						LinkedHashMap<String, Object> pirceData=(LinkedHashMap<String, Object>)d;
						
						for (Map.Entry<String,Object> x : criteriaMap.entrySet()) {
							if(x.getValue() instanceof Map<?, ?>) {
								if(!requestMap.containsKey(x.getKey())) {
									requestMap.put(x.getKey(), new ArrayList<Map<String,Object>>());
									index=0;
									innerDataKeys.add(x.getKey());
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
										}else if(t.getValue().contains("evaluate")) {
											evalAndUpdateInnerData(t, defaultData, pirceData, innerData);
										}else {
											innerData.put(t.getKey(), pirceData.get(t.getValue()));
										}
									}
									restCommonUtil.fillAndSetList(grpId, innerData, lst);
									index++;
								}else {
									for (Map.Entry<String,String> t : mapData.entrySet()) {
										if(t.getValue().contains("evaluate")) {
											evalAndUpdateInnerData(t, defaultData, pirceData, innerData);
										}
									}
								}
							}else if(x.getValue().equals("size")) {
								requestMap.put(x.getKey(), index);
							}
							
						}
						
					}
					
				}
			}
		}
		//consolidate minutes and mrc
		if (isDataProcessed) {
			for (String innerDataKey : innerDataKeys) {
				List<Map<String, Object>> lst = (List<Map<String, Object>>) requestMap.get(innerDataKey);
				for (Map<String, Object> innerData : lst) {
					if (innerData == null) {
						continue;
					}
						List<String> minuteList = (ArrayList<String>) innerData.get("minutes");
						double totalMinute = 0; 
						totalMinute = minuteList.stream().map(s -> s == null ? 0 : Double.valueOf(s))
								.collect(Collectors.summingDouble(Double::doubleValue));
						int consolidatedexistingMinute = (int) getRoundValue(totalMinute, 0);
						consolidatedexistingMinute = (consolidatedexistingMinute == 0) ? 1 : consolidatedexistingMinute;
						String minuteKey = (String) innerData.get("minutes_key");
						innerData.put(minuteKey, consolidatedexistingMinute);
						
						List<String> mrcList = (ArrayList<String>) innerData.get("mrc");
						
						int mrcListcount = mrcList.size();
						double totalMrc=mrcList.stream().map(s -> s == null ? 0 : Double.valueOf(s))
								.collect(Collectors.summingDouble(Double::doubleValue));
						double totalnetUsageRate =totalMrc / mrcListcount;
						String mrcKey = (String) innerData.get("mrc_key");
						innerData.put(mrcKey, totalnetUsageRate);
						innerData.remove("mrc_key");
						innerData.remove("minutes_key");
						innerData.remove("mrc");
						innerData.remove("minutes");
					
				}
			}

		}
		
	}


	@SuppressWarnings("unchecked")
	protected void processConfigDataFromCustomeRulesAnira(Map<String, Object> requestMap,List<NxLookupData> rulesData,Object inputDesign) {
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
										}else if(t.getValue().equals("count")) {
											restCommonUtil.incrementValue(innerData, t.getKey());
										}else {
											innerData.put(t.getKey(), pirceData.get(t.getValue()));
										}
									}
									restCommonUtil.fillAndSetList(grpId, innerData, lst);
									index++;
								}else {
									String sohoQuantityKey=restCommonUtil.getKeysByValue(mapData, "sohoQuantity");
									if(StringUtils.isNotEmpty(sohoQuantityKey)){
										restCommonUtil.incrementQuantity(innerData,sohoQuantityKey,pirceData.get("sohoQuantity"));
										restCommonUtil.fillAndSetList(grpId, innerData, lst);
									}
									
									String countKey=restCommonUtil.getKeysByValue(mapData, "count");
									if(StringUtils.isNotEmpty(countKey)){
										//if its from same group then we r incrementing count
										restCommonUtil.incrementValue(innerData, countKey);
										restCommonUtil.fillAndSetList(grpId, innerData, lst);
									}
								
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

	@SuppressWarnings("unchecked")
	protected void processConfigDataFromCustomeRulesUsageProduct(Map<String, Object> requestMap,List<NxLookupData> rulesData,Object inputDesign,String offerName,
			boolean isStringRequest) {
		boolean isDataProcessed=false;
		int index=0;
		AtomicInteger cnt=new  AtomicInteger(0);
		List<NxLookupData> defaultData=nxLookupDataRepository.
				findByDatasetNameAndItemIdAndDescription(MyPriceConstants.CUSTOM_CONFIG_RULES, offerName, MyPriceConstants.DEFAULT_VALUES);
		List<String> innerDataKeys = new ArrayList<>();
		for(NxLookupData ruleObj:rulesData) {
			if(StringUtils.isNotEmpty(ruleObj.getCriteria()) && (ruleObj.getCriteria().contains("%"))) 
			{
				String[] rule = ruleObj.getCriteria().split("%");
				String jsonPath=0<rule.length && rule[0]!=null?rule[0]:"";
				String criteria=1<rule.length && rule[1]!=null?rule[1]:"";
				
				List<Object>  designDatalst=nexxusJsonUtility.getValueLst(inputDesign,jsonPath);
				if(CollectionUtils.isNotEmpty(designDatalst)) {
					isDataProcessed=true;
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
						
						LinkedHashMap<String, Object> pirceData=(LinkedHashMap<String, Object>)d;
						
						//collectReqElementType(requestMap, pirceData); //xy3208 VTNS does not have elementType
						
						for (Map.Entry<String,Object> x : criteriaMap.entrySet()) {
							if(x.getValue() instanceof Map<?, ?>) {
								if(!requestMap.containsKey(x.getKey())) {
									requestMap.put(x.getKey(), new ArrayList<Map<String,Object>>());
									index=0;
									innerDataKeys.add(x.getKey());
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
										}else if(t.getValue().contains("evaluate")) {
											evalAndUpdateInnerData(t, defaultData, pirceData, innerData);
										}else {
											innerData.put(t.getKey(), pirceData.get(t.getValue()));
										}
									}
									restCommonUtil.fillAndSetList(grpId, innerData, lst);
									index++;
								}else {
									for (Map.Entry<String,String> t : mapData.entrySet()) {
										if(t.getValue().contains("evaluate")) {
											evalAndUpdateInnerData(t, defaultData, pirceData, innerData);
										}
									}
								}
							}else if(x.getValue().equals("size")) {
								requestMap.put(x.getKey(), index);
							}
							
						}
						
					}
					
				}
			}
		}
		//consolidate minutes and mrc
		if (isDataProcessed) {
			for (String innerDataKey : innerDataKeys) {
				List<Map<String, Object>> lst = (List<Map<String, Object>>) requestMap.get(innerDataKey);
				for (Map<String, Object> innerData : lst) {
					if (innerData == null) {
						continue;
					}
					List<String> minuteList = (ArrayList<String>) innerData.get("minutes");
					List<String> mrcList = (ArrayList<String>) innerData.get("mrc");
					double totalMinute = 0;
					totalMinute = minuteList.stream().map(s -> s == null ? 0 : Double.valueOf(s))
							.collect(Collectors.summingDouble(Double::doubleValue));
					int consolidatedexistingMinute = (int) getRoundValue(totalMinute, 0);
					consolidatedexistingMinute = (consolidatedexistingMinute == 0) ? 1 : consolidatedexistingMinute;
					String minuteKey = (String) innerData.get("minutes_key");
					innerData.put(minuteKey, consolidatedexistingMinute);

					double totalnetUsageRate = 0;
					double totaldicountedRevenue = 0;
					totalMinute = (totalMinute == 0) ? 1 : totalMinute;
					int mrcListcount = 0;
					for (String data : minuteList) {
						double dicountedRevenue = Double.valueOf(data) * Double.valueOf(mrcList.get(mrcListcount));
						totaldicountedRevenue += dicountedRevenue;
						mrcListcount++;
					}
					totaldicountedRevenue = getRoundValue(totaldicountedRevenue, 5);
					totalnetUsageRate = getRoundValue(totaldicountedRevenue / totalMinute, 4);
					String mrcKey = (String) innerData.get("mrc_key");
					innerData.put(mrcKey, totalnetUsageRate);
					innerData.remove("mrc_key");
					innerData.remove("minutes_key");
					innerData.remove("mrc");
					innerData.remove("minutes");
				}
			}

			// converting the inner data to string
			if (isStringRequest) {
				for (String innerDataKey : innerDataKeys) {
					List<Map<String, Object>> lst = (List<Map<String, Object>>) requestMap.get(innerDataKey);
					if (CollectionUtils.isNotEmpty(lst)) {
						if (lst.contains(null)) {
							lst.removeAll(Collections.singleton(null));
						}
						//Gson gson = new Gson();
						Gson gson = new GsonBuilder().disableHtmlEscaping().create();
						String result = gson.toJson(lst, List.class);
						requestMap.put(innerDataKey, result);
					}
				}
			}
		}
}


	@SuppressWarnings("unchecked")
	protected void evalAndUpdateInnerData(Map.Entry<String, String> mapDataEntry, List<NxLookupData> defaultData,
			LinkedHashMap<String, Object> pirceData, Map<String, Object> innerData) {
		String values[] = mapDataEntry.getValue().split("##");
		String[] data = values[2].split(",");
		String formulaExp = values[0];
		String formulaSteps[] = formulaExp.split("&&");
		Map<String, String> formulaStepMap = new HashMap<String, String>();
		String type = null;
		if (values.length == 4) {
			type = values[3];
		}
		if (!innerData.containsKey(type)) {
			innerData.put(type, new ArrayList<String>());
			innerData.put(type + "_key", mapDataEntry.getKey());
		}
		for (String formulaStepdata : formulaSteps) {
			String formulaStepvalue[] = formulaStepdata.split("::");
			String formula = formulaStepvalue[1];
			String roundParse[] = formulaStepvalue[2].split(":");
			int roundPrecision = Integer.valueOf(roundParse[1]);
			boolean isEval = true;
			if (formula.contains("step1")) {
				if (formulaStepMap.containsKey("step1")) {
					formula = formula.replace("step1", formulaStepMap.get("step1"));
				} else {
					isEval = false;
				}
			}
			for (String str : data) {
				String jsonValue = pirceData.get(str) != null
						? String.valueOf(pirceData.get(str))
						: null;
				if (jsonValue == null) {
					jsonValue = getDefaultValue(str, defaultData);
					if (jsonValue == null) {
						isEval = false;
						break;
					}
				}
				String value = String.valueOf(jsonValue);
				formula = formula.replace(str, value);
			}
			if (isEval) {
				try {
					Object result = Eval.me(formula);
					Double res = Double.parseDouble(String.valueOf(result));
					formulaStepMap.put(formulaStepvalue[0],
							roundPrecision != -1
									? String.valueOf(
											getRoundValue(res, roundPrecision))
									: String.valueOf(res));
				} catch (ArithmeticException e) {
					Double res = 0.0;
					formulaStepMap.put(formulaStepvalue[0], String.valueOf(res));
				}
			}
		}
		if(formulaStepMap.containsKey("result")) {
			((ArrayList<String>)innerData.get(type)).add(String.valueOf(formulaStepMap.get("result")));
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
	protected void collectReqElementType(Map<String, Object> requestMap, LinkedHashMap<String, String> pirceData) {
		if(null!=pirceData.get("elementType")) {
			if(!requestMap.containsKey(CustomJsonConstants.ELEMENT_TYPE)) {
				requestMap.put(CustomJsonConstants.ELEMENT_TYPE, new HashSet<String>());
			}
			 ((HashSet<String>) requestMap.get(CustomJsonConstants.ELEMENT_TYPE)).add(pirceData.get("elementType"));
		}
	}
	
	
	public <T> T processCustomFields(NxMpConfigJsonMapping mappingData,Object input,Map<String, Object> requestMap,Class<T> clazz) {
		T result=null;
		if ((MyPriceConstants.ETHERNET.equals(mappingData.getOffer())
				|| MyPriceConstants.TDM.equals(mappingData.getOffer())
				|| MyPriceConstants.EPLSWAN_Ethernet.equals(mappingData.getOffer()))
				&& (mappingData.getKey().equalsIgnoreCase(MyPriceConstants.SPEED_LOCAL_ACCESS_PF)
						|| mappingData.getKey().equalsIgnoreCase(MyPriceConstants.INT_SPEED_LOCAL_ACCESS_PF))) {
			String inputData=restCommonUtil.getItemValueUsingJsonPath(mappingData.getInputPath(), input, String.class);
			if(clazz == String.class) {
				return clazz.cast(convertAccessSpeedForEthernetTdm(inputData,mappingData));
			}
			
		}else if(requestMap.containsKey(mappingData.getKey())&& null!=requestMap.get(mappingData.getKey())) {
			Object o=requestMap.get(mappingData.getKey());
			result=restCommonUtil.handleCast(o, clazz);
		    requestMap.remove(mappingData.getKey());
		}
		return result;
	}
	
	
	protected String convertAccessSpeedForEthernetTdm(String input,NxMpConfigJsonMapping mappingData) {
		if(MyPriceConstants.ETHERNET.equals(mappingData.getOffer()) ||  MyPriceConstants.EPLSWAN_Ethernet.equals(mappingData.getOffer())) {
			if(MyPriceConstants.SOURCE_USRP.equalsIgnoreCase(mappingData.getProductType())) {
				if(StringUtils.isNotEmpty(input)) {
					long speed = Long.parseLong(input);
					if (speed >= 10_000_000) {
						return String.valueOf((speed/1_000_000)).concat(" ").concat("Gbps");
					}
					return String.valueOf((speed/1000)).concat(" ").concat("Mbps");
				}
				return input;
			}else {
				if(StringUtils.isNotEmpty(input)) {
					if(input.contains("GBPS")) {
						String data= input.substring(0,input.indexOf("GBPS")).concat("Gbps");
						if(StringUtils.isNotEmpty(data) && data.equalsIgnoreCase("1 Gbps")) {
							return "1000 Mbps";
						}
						return data;
					}else if(input.contains("MBPS")) {
						return input.substring(0,input.indexOf("MBPS")).concat("Mbps");
					}else if(input.contains("KBPS")) {
						return input.substring(0,input.indexOf("KBPS")).concat("Kbps");
					}else if(input.contains("K")) {
						return input.substring(0,input.indexOf("K")).concat(" Kbps");
					}else if(input.contains("M")) {
						return input.substring(0,input.indexOf("M")).concat(" Mbps");
					}else if(input.contains("G")) {
						String data= input.substring(0,input.indexOf("G")).concat(" Gbps");
						if(StringUtils.isNotEmpty(data) && data.equalsIgnoreCase("1 Gbps")) {
							return "1000 Mbps";
						}
						return data;
					}else {
						return input;
					}
				}
				return input;
			}
		}else if(MyPriceConstants.TDM.equals(mappingData.getOffer())) {
			if(StringUtils.isNotEmpty(input)) {
				 for(Map.Entry<String, String> entry : MyPriceConstants.TDM_ACCESS_SPEED_CONVERSION.entrySet()) {
					 String [] x=input.split("\\s+");
					 if(Arrays.asList(x).contains(entry.getKey())) {
						 input=input.replace(entry.getKey(), entry.getValue());
					 }
				 }
				 if(StringUtils.isNotEmpty(mappingData.getDatasetName())) {
					 return restCommonUtil.processDataSetName(input, mappingData.getDatasetName(), String.class);
					
				 }
			}
			
			return input;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	protected void processConfigDataFromCustomeRulesBVoIPNonUsage(Map<String, Object> requestMap,List<NxLookupData> rulesData,
			Object inputDesign,String offerName) {
		boolean isDataProcessed=false;
		int index=0;
		AtomicInteger cnt=new  AtomicInteger(0);
		List<NxLookupData> defaultData=nxLookupDataRepository.
				findByDatasetNameAndItemIdAndDescription(MyPriceConstants.CUSTOM_CONFIG_RULES, offerName, MyPriceConstants.DEFAULT_VALUES);
		List<String> innerDataKeys = new ArrayList<>();
		for(NxLookupData ruleObj:rulesData) {
			if(StringUtils.isNotEmpty(ruleObj.getCriteria()) && (ruleObj.getCriteria().contains("%"))) 
			{
				String[] rule = ruleObj.getCriteria().split("%");
				String jsonPath=0<rule.length && rule[0]!=null?rule[0]:"";
				String criteria=1<rule.length && rule[1]!=null?rule[1]:"";
				
				List<Object>  designDatalst=nexxusJsonUtility.getValueLst(inputDesign,jsonPath);
				if(CollectionUtils.isNotEmpty(designDatalst)) {
					isDataProcessed=true;
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
						LinkedHashMap<String, Object> pirceData=(LinkedHashMap<String, Object>)d;
						for (Map.Entry<String,Object> x : criteriaMap.entrySet()) {
							if(x.getValue() instanceof Map<?, ?>) {
								if(!requestMap.containsKey(x.getKey())) {
									requestMap.put(x.getKey(), new ArrayList<Map<String,Object>>());
									index=0;
									innerDataKeys.add(x.getKey());
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
										}
										else if(t.getValue().contains("type")){
											 updateInnerDataForBvoipNonUsage(t, defaultData, pirceData, innerData);
										}
										else {
											innerData.put(t.getKey(), pirceData.get(t.getValue()));
										}
									}
									restCommonUtil.fillAndSetList(grpId, innerData, lst);
									index++;
								}else {
									for (Map.Entry<String,String> t : mapData.entrySet()) {
										if(t.getValue().contains("type")) {
											 updateInnerDataForBvoipNonUsage(t, defaultData, pirceData, innerData);
										}
									}
								}
							}else if(x.getValue().equals("size")) {
								requestMap.put(x.getKey(), index);
							}
							
						}
						
					}
					
				}
			}
		}
		//consolidate minutes and mrc
		if (isDataProcessed) {
			for (String innerDataKey : innerDataKeys) {
				List<Map<String, Object>> lst = (List<Map<String, Object>>) requestMap.get(innerDataKey);
				for (Map<String, Object> innerData : lst) {
					if (innerData == null) {
						continue;
					}
					List<String> minuteList = (ArrayList<String>) innerData.get("minutes");
					List<String> mrcList = (ArrayList<String>) innerData.get("mrc");
					List<String> totalMinuteList = (ArrayList<String>) innerData.get("totalMinutes");

					double totalGenericQuantity = minuteList.stream().map(s -> s == null ? 0 : 
						Double.valueOf(s)).collect(Collectors.summingDouble(Double::doubleValue));
					String minuteKey = (String) innerData.get("minutes_key");
					innerData.put(minuteKey, (int) getRoundValue(totalGenericQuantity, 0));

					double totalCount = 0;
					totalCount = totalMinuteList.stream().map(s -> s == null ? 0 : Double.valueOf(s))
							.collect(Collectors.summingDouble(Double::doubleValue));
					double totalNetAmount = mrcList.stream().map(s -> s == null ? 0 : Double.valueOf(s))
							.collect(Collectors.summingDouble(Double::doubleValue));
					totalCount = (totalCount != 0) ? totalCount : 1;
					double netMrc=getRoundValue(totalNetAmount / totalCount, 2);
					String mrcKey = (String) innerData.get("mrc_key");
					innerData.put(mrcKey,netMrc);
					
					innerData.remove("mrc_key");
					innerData.remove("minutes_key");
					innerData.remove("totalMinutes_key");
					innerData.remove("mrc");
					innerData.remove("minutes");
					innerData.remove("totalMinutes");

				}
			}
		}
	}
	
	protected void updateInnerDataForBvoipNonUsage(Map.Entry<String, String> mapDataEntry,
			List<NxLookupData> defaultData, LinkedHashMap<String, Object> pirceData, Map<String, Object> innerData) {
		String values[] = mapDataEntry.getValue().split("##");
		String type = 2<values.length && values[2]!=null?values[2]:"";
		Object val = pirceData.get(values[0]);
		String data = val != null ? val.toString() : null;

		if (!innerData.containsKey(type)) {
			innerData.put(type, new ArrayList<String>());
			innerData.put(type + "_key", mapDataEntry.getKey());
		}
		((ArrayList<String>) innerData.get(type)).add(data);
	}

}
