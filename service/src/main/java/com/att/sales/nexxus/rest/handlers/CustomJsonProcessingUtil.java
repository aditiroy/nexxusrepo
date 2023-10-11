package com.att.sales.nexxus.rest.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.aft.dme2.internal.gson.Gson;
import com.att.aft.dme2.internal.gson.GsonBuilder;
import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.CustomJsonConstants;
import com.att.sales.nexxus.constant.MessageConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxMpConfigJsonMapping;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdateRestUtilInr;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdateRestUtilPd;
import com.att.sales.nexxus.myprice.transaction.service.RestCommonUtil;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.jayway.jsonpath.TypeRef;

import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicInteger;

@Component
public class CustomJsonProcessingUtil {

	private static final Logger log = LoggerFactory.getLogger(CustomJsonProcessingUtil.class);
	@Autowired
	private JsonPathUtil jsonPathUtil;
	
	@Autowired
	private NexxusJsonUtility nexxusJsonUtility;
	
	@Autowired
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	@Autowired
	private ConfigAndUpdateRestUtilPd configAndUpdateRestUtilPd;
	
	@Autowired
	private ConfigAndUpdateRestUtilInr configAndUpdateRestUtilInr;
	
	@Autowired
	private RestCommonUtil restCommonUtil;
	
	public String createJsonString(Map<String, Object> inputParamMap,Object inputDesign) {
		LinkedHashMap<String,Object> customJsonMap=createJsonObject(inputParamMap, inputDesign);
		//Gson gson = new Gson();
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        return gson.toJson(customJsonMap, LinkedHashMap.class);
	}
	
	@SuppressWarnings("unchecked")
	public LinkedHashMap<String,Object> createJsonObject(Map<String, Object> inputParamMap,Object inputDesign) {
		try {
			List<NxMpConfigJsonMapping> jsonRules=getNxConfigMapping(inputParamMap);
			if(CollectionUtils.isNotEmpty(jsonRules)) {
				Map<String, Object> jsonMap=new HashMap<String, Object>();
				jsonMap.put(CustomJsonConstants.JSON_ROOT_NODE, new LinkedHashMap<String,Object>());
				createJson(jsonRules, jsonMap, inputDesign, CustomJsonConstants.JSON_ROOT_NODE, inputParamMap);
				LinkedHashMap<String,Object> resultJson= (LinkedHashMap<String,Object>)jsonMap.get(CustomJsonConstants.JSON_ROOT_NODE);
				JacksonUtil.cleanJSON(resultJson);
				return resultJson;
			}
		}catch(Exception e) {
			log.error("Exception during creating custom json: {}",e);
		}
		
		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	public  void createJson(List<NxMpConfigJsonMapping> jsonRules,Map<String, Object> jsonMap,Object input,
			String nodeName,Map<String, Object> inputParamMap) throws SalesBusinessException {
		try {
			if(jsonMap.containsKey(nodeName) && null!=jsonMap.get(nodeName) && jsonMap.get(nodeName) instanceof LinkedHashMap) {
				LinkedHashMap<String,Object>  parentJsonObject=(LinkedHashMap<String, Object>) jsonMap.get(nodeName);

				LinkedHashMap<String,NxMpConfigJsonMapping> childMap= jsonRules.stream().filter(p->p.getFieldParent().equals(nodeName)).
						 collect(Collectors.toMap(NxMpConfigJsonMapping::getKey,Function.identity(),(v1,v2)->v1,LinkedHashMap::new));
					for (Map.Entry<String,NxMpConfigJsonMapping> x : childMap.entrySet()) {
						NxMpConfigJsonMapping jsonRule=x.getValue();
						Class<?> type=CustomJsonConstants.TYPE_MAP.getOrDefault(jsonRule.getFieldType(), Object.class);
						if(CustomJsonConstants.JSON_TYPE_ARRAY.equals(jsonRule.getFieldType())) {
							List<Object> dataForArray=getListDataForArray(jsonRule, input, inputParamMap,Object.class);
							List<Object> array=new ArrayList<Object>();
							parentJsonObject.put(jsonRule.getFieldName(),array);
							jsonMap.put(jsonRule.getKey(), array);
							boolean isObjetArray=StringUtils.isNotEmpty(jsonRule.getArrayElementName())?true:false;
							if(isObjetArray) {
								AtomicInteger i=new AtomicInteger(0);
								Optional.ofNullable(dataForArray).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull).
							    forEach( inputObj -> {
							    	setArrayIndex(i.getAndIncrement(), inputObj);
							    	LinkedHashMap<String,Object>  arrayElement=new LinkedHashMap<String,Object>();
									jsonMap.put(jsonRule.getArrayElementName(), arrayElement);
									try {
										createJson(jsonRules,jsonMap, inputObj, jsonRule.getArrayElementName(),inputParamMap);
									} catch (SalesBusinessException e) {
										log.error("Exception during  custom Array creation for node : {}",jsonRule.getArrayElementName());
									}
									array.add(arrayElement);
							    });
								
							}else {
								array.addAll(dataForArray);
							}
							
						}else if(CustomJsonConstants.JSON_TYPE_OBJECT.equals(jsonRule.getFieldType())) {
							if(StringUtils.isNotEmpty(jsonRule.getInputPath())) {
								input=restCommonUtil.getSingleObject(input,jsonRule.getInputPath(), Object.class);
							}
							LinkedHashMap<String,Object> newObj=createNewJsonObject(jsonRule,input,inputParamMap);
							parentJsonObject.put(jsonRule.getFieldName(),newObj);
							jsonMap.put(jsonRule.getKey(), newObj);
							createJson(jsonRules, jsonMap, input, jsonRule.getKey(),inputParamMap);
						}else {
							if(!parentJsonObject.containsKey(jsonRule.getFieldName())) {
								parentJsonObject.put(jsonRule.getFieldName(),getData(jsonRule, input,inputParamMap,type));
							}
						}
					}
			}
		}catch(Exception e) {
			log.error("Exception during  custom json for node : {}",nodeName);
			throw new SalesBusinessException(MessageConstants.CUSTOM_JSON_CREATION_ERROR);
		}
		
	}
	
	
	@SuppressWarnings("unchecked")
	protected void setArrayIndex(int i, Object inputObj) {
		if(inputObj instanceof LinkedHashMap) {
			LinkedHashMap<String, Object> d=(LinkedHashMap<String, Object>)inputObj;
			d.put("arrayIndex", i);
		}else if(inputObj instanceof JSONObject) {
			JSONObject d=(JSONObject)inputObj;
			d.put("arrayIndex", i);
		}
	}
	

	
	protected <T> T getData(NxMpConfigJsonMapping jsonRule,Object input,Map<String, Object> inputParamMap,Class<T> clazz) {
		T t=null;
		if(MyPriceConstants.IS_DEFAULT.equals(jsonRule.getType())) {
			return restCommonUtil.handleCast(jsonRule.getDefaultValue(),clazz);
		}else if(CustomJsonConstants.CUSTOM_CODE.equals(jsonRule.getType())) {
			t=customProcessing(jsonRule,input,inputParamMap,clazz);
		}else if(CustomJsonConstants.CUSTOM_JSON_STRING.equals(jsonRule.getType())) {
			return customJsonStringProcessing(jsonRule,input,inputParamMap,clazz);
		}else if("Input".equals(jsonRule.getType())) {
			t=restCommonUtil.handleCast(input,clazz);
		}else if(StringUtils.isNotEmpty(jsonRule.getInputPath())) {
			if(jsonRule.getInputPath().contains(MyPriceConstants.MULTI_JSON_PATH_SEPERATOR)){
				List<String> pathList= new ArrayList<String>(Arrays.asList(jsonRule.getInputPath().split(
						Pattern.quote(MyPriceConstants.MULTI_JSON_PATH_SEPERATOR))));
				if(clazz == String.class) {
					t=clazz.cast(processMultipleJsonPath(jsonRule, input, pathList, jsonRule.getDelim()));
				}
				
			}else if(jsonRule.getInputPath().contains(MyPriceConstants.OR_CONDITION_SEPERATOR)){
				List<String> pathList= new ArrayList<String>(Arrays.asList(jsonRule.getInputPath().split(
						Pattern.quote(MyPriceConstants.OR_CONDITION_SEPERATOR))));
				t = processOrCondition(jsonRule, input,pathList,clazz);
			}else {
				t = restCommonUtil.getItemValueUsingJsonPath(jsonRule.getInputPath(),input, clazz);
			}
			
		}
		
		if(StringUtils.isNotEmpty(jsonRule.getDatasetName())) {
			t= restCommonUtil.processDataSetName(t,jsonRule.getDatasetName(),clazz);
		}
		if(t==null) {
			return processDefaultValue(jsonRule, clazz);
		}
		return t;	
	}
	
	protected <T> T processDefaultValue(NxMpConfigJsonMapping jsonRule,Class<T> clazz){
		String defaultValue=jsonRule.getDefaultValue();
		if(StringUtils.isNotEmpty(defaultValue)) {
			if(StringUtils.isNotEmpty(jsonRule.getFieldType()) && CustomJsonConstants.NUMBER.equals(jsonRule.getFieldType())) {
				Object inputNumber=restCommonUtil.convertStringNumber(defaultValue);
				return restCommonUtil.handleCast(inputNumber,clazz);
			}
			return restCommonUtil.handleCast(defaultValue,clazz);
		}
		return null;
	}
	
	
	
	
	protected <T> T customProcessing(NxMpConfigJsonMapping jsonRule,Object input,Map<String, Object> requestMap,Class<T> clazz) {
		T t=null;
		if(MyPriceConstants.SOURCE_PD.equals(jsonRule.getProductType())) {
			return configAndUpdateRestUtilPd.processCustomFields(jsonRule, input, requestMap,clazz);
		}else if (MyPriceConstants.SOURCE_INR.equals(jsonRule.getProductType()) || MyPriceConstants.SOURCE_USRP.equalsIgnoreCase(jsonRule.getProductType())) {
			return configAndUpdateRestUtilInr.processCustomFields(jsonRule, input, requestMap,clazz);
		}else if(requestMap.containsKey(jsonRule.getKey())&& null!=requestMap.get(jsonRule.getKey())) {
			Object o=requestMap.get(jsonRule.getKey());
			t= restCommonUtil.handleCast(o, clazz);
		   requestMap.remove(jsonRule.getKey());
		}
		return t;
	}
	
	
	protected <T> T customJsonStringProcessing(NxMpConfigJsonMapping jsonRule,Object input,Map<String, Object> inputParamMap,Class<T> clazz) {
		T t=null;
		if(StringUtils.isNotEmpty(jsonRule.getDatasetName())) {
			String[] rule = jsonRule.getDatasetName().split(Pattern.quote("#"));
			String ruleType=0<rule.length && rule[0]!=null?rule[0]:"";
			String offerName=1<rule.length && rule[1]!=null?rule[1]:"";
			String productType=2<rule.length && rule[2]!=null?rule[2]:"";
			String subOfferName=3<rule.length && rule[3]!=null?rule[3]:"";
			if(StringUtils.isNotEmpty(jsonRule.getInputPath())) {
				input=nexxusJsonUtility.getValueLst(input, jsonRule.getInputPath());
			}
			Map<String, Object> requestParamMap=new HashMap<String, Object>();
			requestParamMap.put(CustomJsonConstants.RULE_TYPE,ruleType);
			requestParamMap.put(MyPriceConstants.OFFER_NAME,offerName);
			requestParamMap.put(MyPriceConstants.SUB_OFFER_NAME,subOfferName);
			requestParamMap.put(MyPriceConstants.PRODUCT_TYPE,productType);
			LinkedHashMap<String,Object> json= this.createJsonObject(requestParamMap, input);
			if(null!=json && clazz == String.class && null!=json.get(jsonRule.getKey())) {
				Object o=json.get(jsonRule.getKey());
				//Gson gson = new Gson();
				Gson gson = new GsonBuilder().disableHtmlEscaping().create();
				String result=null;
				if(o instanceof List) {
					result=gson.toJson(o,List.class);
				}else {
					result=gson.toJson(o,LinkedHashMap.class);
				}
		      
				 t= clazz.cast(result);
			}
		}
		if(t==null) {
			return restCommonUtil.handleCast(jsonRule.getDefaultValue(),clazz);
		}
		return t;
	}
	
	
	
	@SuppressWarnings("unchecked")
	protected  <T> List<T> getListDataForArray(NxMpConfigJsonMapping jsonRule,Object input,Map<String, Object> inputParamMap,Class<T> clazz){
		 List<T> list = new ArrayList<>();
		if(MyPriceConstants.IS_DEFAULT.equals(jsonRule.getType())) {
			list=JacksonUtil.toJsonArray(jsonRule.getDefaultValue());
			restCommonUtil.handleCastForList(list, clazz);
			return list;
		}else if(CustomJsonConstants.CUSTOM_CODE.equals(jsonRule.getType())) {
			return customListProcessing(jsonRule,input,inputParamMap, clazz);
		}else if(StringUtils.isNotEmpty(jsonRule.getInputPath())){
			 if(StringUtils.isNotEmpty(jsonRule.getDatasetName())) {
					String itemId=restCommonUtil.getSingleObject(input, jsonRule.getInputPath(), String.class);
					String dataFromLookup=restCommonUtil.processDataSetName(itemId,jsonRule.getDatasetName(),String.class);
					list=JacksonUtil.toJsonArray(dataFromLookup);
					restCommonUtil.handleCastForList(list, clazz);
			 }else {
					//if input complete jsonObject as input
					if("*".equals(jsonRule.getInputPath())) {
						list.add(clazz.cast(input));
					}else{
						list=(List<T>)nexxusJsonUtility.getValueLst(input, jsonRule.getInputPath());
					}
					restCommonUtil.handleCastForList(list, clazz);
			 }
		}
		if(CollectionUtils.isEmpty(list) && StringUtils.isNotEmpty(jsonRule.getDefaultValue())) {
			list=JacksonUtil.toJsonArray(jsonRule.getDefaultValue());
			restCommonUtil.handleCastForList(list, clazz);
		}
		return list;
	}
	
	
	
	@SuppressWarnings("unchecked")
	protected <T> List<T> customListProcessing(NxMpConfigJsonMapping jsonRule,Object input,Map<String, Object> inputParamMap,Class<T> clazz) {
		 List<T> list = new ArrayList<>();  
		if(inputParamMap.containsKey(jsonRule.getKey())&& null!=inputParamMap.get(jsonRule.getKey())) {
			list=(List<T>)inputParamMap.get(jsonRule.getKey());
		    inputParamMap.remove(jsonRule.getKey());
		}
		restCommonUtil.handleCastForList(list, clazz);
		return list;
	}
	

	@SuppressWarnings("unchecked")
	protected LinkedHashMap<String,Object> createNewJsonObject(NxMpConfigJsonMapping jsonRule,Object input,Map<String, Object> inputParamMap) {
		LinkedHashMap<String,Object>  t=new LinkedHashMap<String, Object>();
		if(CustomJsonConstants.CUSTOM_CODE.equals(jsonRule.getType())) {
			if(inputParamMap.containsKey(jsonRule.getKey())&& null!=inputParamMap.get(jsonRule.getKey()) 
					&& inputParamMap.get(jsonRule.getKey()) instanceof LinkedHashMap) {
				t=(LinkedHashMap<String, Object>) inputParamMap.get(jsonRule.getKey());
			}
		}
		return  t;
	}
	
	
	protected List<NxMpConfigJsonMapping> getChildJsonRulesByParent(List<NxMpConfigJsonMapping>  allJsonRules,String parentName){
		return allJsonRules.stream().filter(p->p.getFieldParent().equals(parentName)).collect(Collectors.toList());
	}
	
	protected String processMultipleJsonPath(NxMpConfigJsonMapping jsonRule, Object inputDesignJson,
			List<String> pathList,String delim) {
		StringBuilder sb = new StringBuilder();
		for(String path:pathList) {
			if(path.contains(MyPriceConstants.COMMA_SEPERATOR)) {
				List<String> subConditionPathLst= new ArrayList<String>(Arrays.asList(path.split(MyPriceConstants.COMMA_SEPERATOR)));
				if(sb.length()>0 && StringUtils.isNotEmpty(delim)) {
					 sb.append(delim);
				}
				String result=processMultipleJsonPath(jsonRule,inputDesignJson,subConditionPathLst,MyPriceConstants.COMMA_SEPERATOR);
				if(StringUtils.isNotEmpty(result)) {
					sb.append(result);
				}
			}else if (path.contains(MyPriceConstants.OR_CONDITION_SEPERATOR)){
				List<String> subConditionPathLst= new ArrayList<String>(Arrays.asList(path.split(Pattern.quote
						(MyPriceConstants.OR_CONDITION_SEPERATOR))));
				if(sb.length()>0 && StringUtils.isNotEmpty(delim)) {
					 sb.append(delim);
				}
				String result=this.processOrCondition(jsonRule, inputDesignJson, subConditionPathLst, String.class);
				if(StringUtils.isNotEmpty(result)) {
					sb.append(result);
				}
			}else {
				String itemValue=restCommonUtil.getItemValueUsingJsonPath(path.trim(), inputDesignJson, String.class);
				if(StringUtils.isNotEmpty(itemValue)) {
					if(StringUtils.isNotEmpty(jsonRule.getDatasetName())) {
						itemValue=restCommonUtil.processDataSetName(itemValue, jsonRule.getDatasetName(), String.class);
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
	protected <T> T processOrCondition(NxMpConfigJsonMapping jsonRule,Object inputJson,List<String> pathList,Class<T> clazz) {
		for(String path:pathList) {
			T itemValue=null;
			if(clazz == String.class) {
				itemValue=restCommonUtil.getItemValueUsingJsonPath(path.trim(), inputJson, clazz);
				if(null!=itemValue) {
					if(StringUtils.isNotEmpty(jsonRule.getDatasetName())) {
						itemValue=restCommonUtil.processDataSetName(itemValue, jsonRule.getDatasetName(), clazz);
					}
					if(null!=itemValue) {
						return itemValue;
					}
				}
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	protected  <T> List<T> getListObject(Object inputJson,String path,Class<T> clazz){
		 List<T> list = new ArrayList<>();
		 List<?> l=null;
		try {
			TypeRef<List<Object>> mapType = new TypeRef<List<Object>>() {};
			l=jsonPathUtil.search(inputJson, path,mapType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(CollectionUtils.isNotEmpty(l)) {
			list= (List<T>)l;
			restCommonUtil.handleCastForList(list, clazz);
			return list;
		}
		return null;
	}
	
	
	
   public List<NxMpConfigJsonMapping>  getNxConfigMapping(Map<String, Object> requestMap){
	   String offerName = requestMap.get(MyPriceConstants.OFFER_NAME) != null? (String) requestMap.get(MyPriceConstants.OFFER_NAME): "";
	   String subOfferName = requestMap.get(MyPriceConstants.SUB_OFFER_NAME) != null? (String) requestMap.get(MyPriceConstants.SUB_OFFER_NAME): "";
		String ruleType = requestMap.get(CustomJsonConstants.RULE_TYPE) != null? (String) requestMap.get(CustomJsonConstants.RULE_TYPE): "";
		String productType=requestMap.get(MyPriceConstants.PRODUCT_TYPE)!=null?requestMap.get(MyPriceConstants.PRODUCT_TYPE).toString():null;
		if (null != requestMap.get(StringConstants.REST_VERSION)) {
			offerName = offerName + "_" + requestMap.get(StringConstants.REST_VERSION);
		}
		if(StringUtils.isNotEmpty(productType)) {
			if(StringUtils.isNotEmpty(subOfferName)) {
				return  nxMyPriceRepositoryServce.findByOfferAndSubOfferAndProductTypeAndRuleName(offerName,subOfferName,productType,ruleType);
			}else {
				return nxMyPriceRepositoryServce.findByOfferAndProductTypeAndRuleNameForJson(offerName, productType,ruleType);
			}
		}
		return null;
	}
	
	
	
	
}
