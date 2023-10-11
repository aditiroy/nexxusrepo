/*package com.att.sales.nexxus.rest.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.CustomJsonConstants;
import com.att.sales.nexxus.constant.MessageConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxMpConfigJsonMapping;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdateRestUtilInr;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdateRestUtilPd;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.jayway.jsonpath.TypeRef;

import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicInteger;

@Component
public class CustomJsonProcessingOld {

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
	
	
	
	public JSONObject createJsonObject(Map<String, Object> inputParamMap,Object inputDesign) {
		try {
			List<NxMpConfigJsonMapping> jsonRules=getNxConfigMapping(inputParamMap);
			if(CollectionUtils.isNotEmpty(jsonRules)) {
				Map<String, Object> jsonMap=new HashMap<String, Object>();
				jsonMap.put(CustomJsonConstants.JSON_ROOT_NODE, new JSONObject());
				createJson(jsonRules, jsonMap, inputDesign, CustomJsonConstants.JSON_ROOT_NODE, inputParamMap);
				JSONObject resultJson= (JSONObject)jsonMap.get(CustomJsonConstants.JSON_ROOT_NODE);
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
			if(jsonMap.containsKey(nodeName) && null!=jsonMap.get(nodeName) && jsonMap.get(nodeName) instanceof JSONObject) {
				JSONObject parentJsonObject=(JSONObject) jsonMap.get(nodeName);
				Map<String,NxMpConfigJsonMapping> childMap= jsonRules.stream().filter(p->p.getFieldParent().equals(nodeName)).
						 collect(Collectors.toMap(NxMpConfigJsonMapping::getKey,Function.identity()));
					for (Map.Entry<String,NxMpConfigJsonMapping> x : childMap.entrySet()) {
						NxMpConfigJsonMapping jsonRule=x.getValue();
						Class<?> type=CustomJsonConstants.TYPE_MAP.getOrDefault(jsonRule.getFieldType(), Object.class);
						if(CustomJsonConstants.JSON_TYPE_ARRAY.equals(jsonRule.getFieldType())) {
							List<Object> dataForArray=getListDataForArray(jsonRule, input, inputParamMap,Object.class);
							JSONArray array=new JSONArray();
							parentJsonObject.put(jsonRule.getFieldName(),array);
							jsonMap.put(jsonRule.getKey(), array);
							boolean isObjetArray=StringUtils.isNotEmpty(jsonRule.getArrayElementName())?true:false;
							if(isObjetArray) {
								AtomicInteger i=new AtomicInteger(0);
								Optional.ofNullable(dataForArray).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull).
							    forEach( inputObj -> {
							    	setArrayIndex(i.getAndIncrement(), inputObj);
							    	JSONObject arrayElement=new JSONObject();
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
								input=getSingleObject(input,jsonRule.getInputPath(), Object.class);
							}
							JSONObject newObj=createNewJsonObject(jsonRule,input,inputParamMap);
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
			return handleCast(jsonRule.getDefaultValue(),clazz);
		}else if(CustomJsonConstants.CUSTOM_CODE.equals(jsonRule.getType())) {
			t=customProcessing(jsonRule,input,inputParamMap,clazz);
		}else if(CustomJsonConstants.CUSTOM_JSON_STRING.equals(jsonRule.getType())) {
			return customJsonStringProcessing(jsonRule,input,inputParamMap,clazz);
		}else if("Input".equals(jsonRule.getType())) {
			t=handleCast(input,clazz);
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
				t = getItemValueUsingJsonPath(jsonRule.getInputPath(),input, clazz);
			}
			
		}
		
		if(StringUtils.isNotEmpty(jsonRule.getDatasetName())) {
			t= this.processDataSetName(t,jsonRule,clazz);
		}
		if(t==null) {
			return handleCast(jsonRule.getDefaultValue(),clazz);
		}
		return t;	
	}
	
	
	public <T> T processDataSetName(T input,NxMpConfigJsonMapping jsonRule,Class<T> clazz) {
		String dataSourceName=jsonRule.getDatasetName();
		if(dataSourceName.contains(MyPriceConstants.NX_LOOKUP_SOURCE)) {
			String[] rule = dataSourceName.split(Pattern.quote("|"));
			String looupDataSet=1<rule.length && rule[1]!=null?rule[1]:"";
			String defaultValue=2<rule.length && rule[2]!=null?rule[2]:"";
			
			String data = null;
			if(null!=input) {
				data = getDataFromNxLookUp(input.toString(),looupDataSet);
			}
			if(StringUtils.isNotEmpty(data)) {
				return handleCast(data,clazz);
			}else if(StringUtils.isNotEmpty(defaultValue)) {
				if("null".equals(defaultValue)) return null;
				else return handleCast(defaultValue,clazz);
			}
		}
		return input;
	}
	
	
	public String getDataFromNxLookUp(String input, String looupDataSet) {
		if(StringUtils.isNotEmpty(looupDataSet) && StringUtils.isNotEmpty(input)) {
			Map<String,NxLookupData> resultMap=nxMyPriceRepositoryServce.getLookupDataByItemId(looupDataSet);
			if(null!=resultMap && resultMap.containsKey(input) && null!= resultMap.get(input) ) {
				NxLookupData data=resultMap.get(input);
				return data.getDescription();
			}
		}
		return input;
	}
	
	
	protected <T> T customProcessing(NxMpConfigJsonMapping jsonRule,Object input,Map<String, Object> requestMap,Class<T> clazz) {
		T t=null;
		if(MyPriceConstants.SOURCE_PD.equals(jsonRule.getProductType())) {
			return configAndUpdateRestUtilPd.processCustomFields(jsonRule, input, requestMap,clazz);
		}else if (MyPriceConstants.SOURCE_INR.equals(jsonRule.getProductType())) {
			return configAndUpdateRestUtilInr.processCustomFields(jsonRule, input, requestMap,clazz);
		}else if(requestMap.containsKey(jsonRule.getKey())&& null!=requestMap.get(jsonRule.getKey())) {
			Object o=requestMap.get(jsonRule.getKey());
			t= handleCast(o, clazz);
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
			JSONObject json= this.createJsonObject(requestParamMap, input);
			if(null!=json && clazz == String.class && null!=json.get(jsonRule.getKey())) {
				 t= clazz.cast(json.get(jsonRule.getKey()).toString());
			}
		}
		if(t==null) {
			return handleCast(jsonRule.getDefaultValue(),clazz);
		}
		return t;
	}
	
	
	
	@SuppressWarnings("unchecked")
	protected  <T> List<T> getListDataForArray(NxMpConfigJsonMapping jsonRule,Object input,Map<String, Object> inputParamMap,Class<T> clazz){
		 List<T> list = new ArrayList<>();
		if(MyPriceConstants.IS_DEFAULT.equals(jsonRule.getType())) {
			list=JacksonUtil.toJsonArray(jsonRule.getDefaultValue());
			handleCastForList(list, clazz);
			return list;
		}else if(CustomJsonConstants.CUSTOM_CODE.equals(jsonRule.getType())) {
			return customListProcessing(jsonRule,input,inputParamMap, clazz);
		}else if(StringUtils.isNotEmpty(jsonRule.getInputPath())){
			//if jsonObject as input
			if("*".equals(jsonRule.getInputPath())) {
				list.add(clazz.cast(input));
			}else{
				list=(List<T>)nexxusJsonUtility.getValueLst(input, jsonRule.getInputPath());
			}
			
			handleCastForList(list, clazz);
		}else if(StringUtils.isNotEmpty(jsonRule.getDatasetName())) {
			String itemId=getSingleObject(input, jsonRule.getInputPath(), String.class);
			String dataFromLookup=this.processDataSetName(itemId,jsonRule,String.class);
			list=JacksonUtil.toJsonArray(dataFromLookup);
			handleCastForList(list, clazz);
		}
		if(CollectionUtils.isEmpty(list) && StringUtils.isNotEmpty(jsonRule.getDefaultValue())) {
			list=JacksonUtil.toJsonArray(jsonRule.getDefaultValue());
			handleCastForList(list, clazz);
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
		handleCastForList(list, clazz);
		return list;
	}
	

	protected JSONObject createNewJsonObject(NxMpConfigJsonMapping jsonRule,Object input,Map<String, Object> inputParamMap) {
		JSONObject t=new JSONObject();
		if(CustomJsonConstants.CUSTOM_CODE.equals(jsonRule.getType())) {
			if(inputParamMap.containsKey(jsonRule.getKey())&& null!=inputParamMap.get(jsonRule.getKey())) {
				t=(JSONObject) inputParamMap.get(jsonRule.getKey());
			}
		}
		return  t;
	}
	
	
	protected List<NxMpConfigJsonMapping> getChildJsonRulesByParent(List<NxMpConfigJsonMapping>  allJsonRules,String parentName){
		return allJsonRules.stream().filter(p->p.getFieldParent().equals(parentName)).collect(Collectors.toList());
	}
	
	protected  <T> T getSingleObject(Object inputJson,String path,Class<T> clazz) {
		TypeRef<List<Object>> mapType = new TypeRef<List<Object>>() {};
		List<Object> lst=jsonPathUtil.search(inputJson, path,mapType);
		if(CollectionUtils.isNotEmpty(lst) && null!=lst.get(0)) {
			return handleCast(lst.get(0), clazz);
		}
		return null;
	}
	
	
	
	public <T> T getItemValueUsingJsonPath(String jsonPath,Object inputJson,Class<T> clazz) {
		if(jsonPath.contains("$")) {
			if(jsonPath.contains("{")  && jsonPath.contains("}") ) { 
				//This method is used to append static value before or after jsonPath result
				if(clazz == String.class) {
					return clazz.cast(this.processAppendedCharWithJsonPath(jsonPath, inputJson));
				}
				
			}else {
				return getSingleObject(inputJson, jsonPath, clazz);
			}
		}
		if(clazz == String.class) {
			return clazz.cast(jsonPath); 
		}
		return null;
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
				String itemValue=this.getItemValueUsingJsonPath(path, inputDesignJson, String.class);
				if(StringUtils.isNotEmpty(itemValue)) {
					if(StringUtils.isNotEmpty(jsonRule.getDatasetName())) {
						itemValue=this.processDataSetName(itemValue, jsonRule, String.class);
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
				itemValue=this.getItemValueUsingJsonPath(path, inputJson, clazz);
				if(null!=itemValue) {
					if(StringUtils.isNotEmpty(jsonRule.getDatasetName())) {
						itemValue=this.processDataSetName(itemValue, jsonRule, clazz);
					}
					if(null!=itemValue) {
						return itemValue;
					}
				}
			}
		}
		return null;
	}
	
	
	

	protected String processAppendedCharWithJsonPath(String jsonPath,Object inputJson) {
		try {
			int open=jsonPath.indexOf("{");
			int close=jsonPath.indexOf("}");
			if(open!=-1 && close!=1){
				String path=jsonPath.substring(open+1, close);
				String charBefore=jsonPath.substring(0,open);
				String chatAfter=jsonPath.substring(close+1,jsonPath.length());
				Object result=nexxusJsonUtility.getValue(inputJson, path);
				if(null!=result) {
					if(StringUtils.isNotEmpty(charBefore)) {
						return this.appendCharAt(result.toString(),charBefore, MyPriceConstants.START); 
					}
					if(StringUtils.isNotEmpty(chatAfter)) {
						return this.appendCharAt(result.toString(),chatAfter, MyPriceConstants.END); 
					}
					return String.valueOf(result);
				}
				return null;
			}
		
		}catch(Exception e) {
			log.error("Exception during processing static character with jsonPath result", e);
		}
	
		return null;
	}
	
	protected String appendCharAt(String input,String charToAppend, String position) {
		
		 if(StringUtils.isNotEmpty(input)) {
			 StringBuilder sb = new StringBuilder(input);
			 if(MyPriceConstants.START.equalsIgnoreCase(position)) {
				 sb.insert(0, charToAppend);
			 }else if(MyPriceConstants.END.equalsIgnoreCase(position)) {
				 sb.append(charToAppend);
			 }else {
				 return input;
			 }
			 return sb.toString();
			 
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
			handleCastForList(list, clazz);
			return list;
		}
		return null;
	}
	
	
	
   public List<NxMpConfigJsonMapping>  getNxConfigMapping(Map<String, Object> requestMap){
	   String offerName = requestMap.get(MyPriceConstants.OFFER_NAME) != null? (String) requestMap.get(MyPriceConstants.OFFER_NAME): "";
	   String subOfferName = requestMap.get(MyPriceConstants.SUB_OFFER_NAME) != null? (String) requestMap.get(MyPriceConstants.SUB_OFFER_NAME): "";
		String ruleType = requestMap.get(CustomJsonConstants.RULE_TYPE) != null? (String) requestMap.get(CustomJsonConstants.RULE_TYPE): "";
		String productType=requestMap.get(MyPriceConstants.PRODUCT_TYPE)!=null?requestMap.get(MyPriceConstants.PRODUCT_TYPE).toString():null;
		
		if(StringUtils.isNotEmpty(productType)) {
			if(StringUtils.isNotEmpty(subOfferName)) {
				return  nxMyPriceRepositoryServce.findByOfferAndSubOfferAndProductTypeAndRuleName(offerName,subOfferName,productType,ruleType);
			}else {
				return nxMyPriceRepositoryServce.findByOfferAndProductTypeAndRuleNameForJson(offerName, productType,ruleType);
			}
		}
		return null;
	}
   
  
	
	public static <T,I> T handleCast(I input,Class<T> clazz) {
		if(clazz == Boolean.class) {
			return clazz.cast(CustomJsonConstants.BOOELAN_CONVERTER.getOrDefault(input,false));
		}else if(null!=input){
			if(clazz == String.class) {
				return clazz.cast(input.toString());
			}else {
				return clazz.cast(input);
			}
			
		}
		return null;
	}
	
	
	protected static <T,I> void handleCastForList(List<I> list,Class<T> clazz) {
		if(CollectionUtils.isNotEmpty(list)){
			if(clazz == Boolean.class) {
				ListIterator<?> iterator=list.listIterator();
				while (iterator.hasNext()) {
					clazz.cast(CustomJsonConstants.BOOELAN_CONVERTER.getOrDefault(iterator.next(),false));
				}
			}else if(clazz == String.class) {
				ListIterator<?> iterator=list.listIterator();
				while (iterator.hasNext()) {
					clazz.cast(iterator.next().toString());
				}
			}else {
				list.forEach(clazz::cast);
			}
		}
		
	}
	
	
	protected static Class<?> determineTypeUsingInput(String val) {
		if(StringUtils.isNotEmpty(val)) {
			 try (Scanner scanner = new Scanner(val)) {
			        if (scanner.hasNextInt()) return Integer.class;
			        if (scanner.hasNextLong()) return Long.class;
			        if (scanner.hasNextDouble()) return Double.class;
			        return String.class;
			  }
		}
		 return String.class;
	}
	
	
	public Set<String> geDataInSetString(Object input,String path){
		List<Object> result=nexxusJsonUtility.getValueLst(input,path);
		return Optional.ofNullable(result).map(List::stream).orElse(Stream.empty())
		   .map(object -> Objects.toString(object, null))
		   .collect(Collectors.toSet());
	}
	
	public List<String> geDataInListString(Object input,String path){
		List<Object> result=nexxusJsonUtility.getValueLst(input,path);
		return Optional.ofNullable(result).map(List::stream).orElse(Stream.empty())
		   .map(object -> Objects.toString(object, null))
		   .collect(Collectors.toList());
	}
	
	

	
}
*/