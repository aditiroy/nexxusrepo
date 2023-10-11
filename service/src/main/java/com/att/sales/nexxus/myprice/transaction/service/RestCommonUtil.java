package com.att.sales.nexxus.myprice.transaction.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.constant.CustomJsonConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.helper.GroupingEnitity;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.jayway.jsonpath.TypeRef;

import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicInteger;

@Component
public class RestCommonUtil {
	
	private static final Logger log = LoggerFactory.getLogger(RestCommonUtil.class);
	
	@Autowired
	private JsonPathUtil jsonPathUtil;
	
	@Autowired
	private NexxusJsonUtility nexxusJsonUtility;
	
	@Autowired
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	
	public <T> void fillAndSetList(Integer index,T object,List<T> list) {
		if(index!=null) {
			if(index>(list.size()-1)) {
				for(int i=list.size();i<index;i++) {
					list.add(null);
				}
				list.add(object);
			}else {
				list.set(index, object);
			}
		}else {
			list.add(object);
		}
		
	}
	
	public <T> boolean hasIndex(int index,List<T> list) {
		if(index < list.size()) {
			return true;
		}
		return false;
	}
	
	public  void incrementValue(Map<String,Object> map,String key)
	{
		if(StringUtils.isNotEmpty(key)) {
			Integer count = (Integer) map.get(key);
			if (count == null) {
				count = 0;
			}
			map.put(key, count + 1);
		}
	
	}
	
	public  void incrementQuantity(Map<String,Object> map,String key,String newValue)
	{
		Integer x=StringUtils.isNotEmpty(newValue)?Integer.parseInt(newValue):0;
		Integer data=map.get(key)!=null?Integer.parseInt(map.get(key).toString()):0;
		map.put(key, data + x);
	}

	public  void minValue(Map<String,Object> map,String key,String newValue)
	{
		Double x=StringUtils.isNotEmpty(newValue)?Double.parseDouble(newValue):0;
		Double data=map.get(key)!=null?Double.parseDouble(map.get(key).toString()):0;
		Double res=x<data?x:data;
		map.put(key,res);
	}

	
	public  <T,E> T getKeysByValue(Map<T, E> map, E value) {
	    return map.entrySet()
	              .stream()
	              .filter(entry -> Objects.equals(entry.getValue(), value))
	              .map(Map.Entry::getKey)
	              .findFirst()
	        	  .orElse(null);
	}
	
	
	public  Integer generateGroupId(Object o,Map<Integer,List<GroupingEnitity>> groupMap,
			LinkedHashMap<String,String>  filterRulesMap,AtomicInteger d,Map<String, Object> paramMap) {
		if(null!=filterRulesMap){
			GroupingEnitity e=getEntity(o,filterRulesMap,paramMap);
			for (Map.Entry<Integer, List<GroupingEnitity>> x : groupMap.entrySet()) {
				List<GroupingEnitity> mapDataLst = x.getValue();
				if(! new HashSet<GroupingEnitity>(mapDataLst).add(e)) {
					return x.getKey();
				}
			}
			Integer k=d.getAndIncrement();
			groupMap.put(k, new ArrayList<GroupingEnitity>(Arrays.asList(e)));
			return k;
		}
		return null;
	
	}
	
	protected  GroupingEnitity getEntity(Object o,LinkedHashMap<String,String>  keyRulesMap,Map<String, Object> paramMap) {
		GroupingEnitity e=new GroupingEnitity();
		for (Map.Entry<String,String> x : keyRulesMap.entrySet()) {
			Set<String> data=geDataByJsonPath(o, x.getValue());
			if(data!=null && data.size()>0) {
				e.setValue(x.getKey(), data);
			}
		}
		return e;
	}
	
	
	protected   Set<String> geDataByJsonPath(Object jsonObject,String path){
		if(StringUtils.isNotEmpty(path) && path.contains("$")) {
			if(null!=jsonObject) {
				TypeRef<Set<String>> mapType = new TypeRef<Set<String>>() {};
				return jsonPathUtil.search(jsonObject, path,mapType);
			}
			return new HashSet<String>();
		}else {
			return new HashSet<String>(Arrays.asList(path));
		}
	}
	
	
	/**
	 * Gets the item value using json path.
	 *
	 * @param jsonPath the json path
	 * @param inputDesignDetails the input design details
	 * @return the item value using json path
	 */
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
	
	

	public String processAppendedCharWithJsonPath(String jsonPath,Object inputJson) {
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
	
	public String appendCharAt(String input,String charToAppend, String position) {
		
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
	Object value=null;
	public  <T> T getSingleObject(Object inputJson,String path,Class<T> clazz) {
		TypeRef<List<Object>> mapType = new TypeRef<List<Object>>() {};
		List<Object> lst=jsonPathUtil.search(inputJson, path,mapType);
		if(CollectionUtils.isNotEmpty(lst) && null!=lst.get(0)) {
			return handleCast(lst.get(0), clazz);
		}
		return null;
	}
	
	public <T,I> T handleCast(I input,Class<T> clazz) {
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
	
	public  <T,I> void handleCastForList(List<I> list,Class<T> clazz) {
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
	
	
	
	public <T> T processDataSetName(T input,String dataSourceName,Class<T> clazz) {
		if(dataSourceName.contains(MyPriceConstants.NX_LOOKUP_SOURCE)) {
			String[] rule = dataSourceName.split(Pattern.quote("|"));
			String looupDataSet=1<rule.length && rule[1]!=null?rule[1]:"";
			String defaultValue=2<rule.length && rule[2]!=null?rule[2]:"";
			
			String data = null;
			if(null!=input) {
				data = getDataFromNxLookUp(input.toString(),looupDataSet);
			}
			if(StringUtils.isNotEmpty(data)) {
				return this.handleCast(data,clazz);
			}else if(StringUtils.isNotEmpty(defaultValue)) {
				if("null".equals(defaultValue)) return null;
				else return this.handleCast(defaultValue,clazz);
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
		return null;
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
	
	public String getDataInString(JSONObject inputDesignDetails,String path) {
		TypeRef<List<String>> ref = new TypeRef<List<String>>() {};
		List<String> dataLst=jsonPathUtil.search(inputDesignDetails,path, ref);
		if(CollectionUtils.isNotEmpty(dataLst)) {
			return dataLst.get(0);
		}
		return null;
	}
	
	
	public  boolean listEqualsIgnoreOrder(Set<String> list1, Set<String> list2) {
	    if (list1.size() != list2.size()) {
	        return false;
	    }
	    List<String> temp = new ArrayList<String>(list2);
	    for (String element : list1) {
	        if (!temp.remove(element)) {
	            return false;
	        }
	    }
	    return temp.isEmpty();
	}
	

	public  Class<?> determineTypeUsingInput(String val) {
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
	
	public  Object convertStringNumber(String val) {
		if(StringUtils.isNotEmpty(val)) {
			 try (Scanner scanner = new Scanner(val)) {
			        if (scanner.hasNextInt()) return Integer.parseInt(val);
			        if (scanner.hasNextLong()) return Long.parseLong(val);
			        if (scanner.hasNextDouble()) return Double.parseDouble(val);
			        return val;
			  }
		}
		 return val;
	}

	
	
}
