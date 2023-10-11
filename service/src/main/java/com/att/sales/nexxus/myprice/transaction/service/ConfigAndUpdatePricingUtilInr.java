package com.att.sales.nexxus.myprice.transaction.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxMpConfigMapping;
import com.att.sales.nexxus.dao.repository.NxLineItemProcessingDao;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.jayway.jsonpath.TypeRef;

@Component
public class ConfigAndUpdatePricingUtilInr {
	
	private static final Logger log = LoggerFactory.getLogger(ConfigAndUpdatePricingUtilInr.class);
	
	
	@Autowired
	private NexxusJsonUtility nexxusJsonUtility;
	
	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Autowired
	private JsonPathUtil jsonPathUtil;
	
	@Autowired
	private NxLineItemProcessingDao lineItemProcessingDao;
	
	@Autowired
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	/**
	 * Gets the data.
	 *
	 * @param mappingData the mapping data
	 * @param inputDesignDetails the input design details
	 * @return the data
	 */
	public String getData(NxMpConfigMapping mappingData,String inputDesignDetails,Map<String, Object> requestMap) {
		String result=null;
		if(StringUtils.isNotEmpty(mappingData.getType()) && 
				mappingData.getType().equals(MyPriceConstants.IS_DEFAULT)) {
			result= mappingData.getDefaultValue();
		}else if(StringUtils.isNotEmpty(mappingData.getType()) && 
				mappingData.getType().equals("Custome")) {
			result= this.customeCodeProcessing(mappingData, inputDesignDetails,requestMap);
		}else if(StringUtils.isNotEmpty(mappingData.getPath())){
			if(StringUtils.isNotEmpty(mappingData.getType()) && mappingData.getType().equals("List")) {
				result= this.processListResult(mappingData, inputDesignDetails);
			}if(StringUtils.isNotEmpty(mappingData.getType()) && mappingData.getType().equals("Count")) {
				result= this.getResultCount(mappingData, inputDesignDetails);
			}else if(mappingData.getPath().contains(MyPriceConstants.OR_CONDITION_SEPERATOR)){
				List<String> pathList= new ArrayList<String>(Arrays.asList(mappingData.getPath().split(
						Pattern.quote(MyPriceConstants.OR_CONDITION_SEPERATOR))));
				result= processOrCondition(mappingData, inputDesignDetails,pathList);
			}else if(mappingData.getPath().contains(MyPriceConstants.MULTI_JSON_PATH_SEPERATOR)){
				List<String> pathList= new ArrayList<String>(Arrays.asList(mappingData.getPath().split(
						MyPriceConstants.MULTI_JSON_PATH_SEPERATOR)));
				result= processMultipleJsonPath(mappingData,inputDesignDetails,pathList,mappingData.getDelimiter());
			}else {
				result=this.getItemValueUsingJsonPath(mappingData.getPath(), inputDesignDetails);
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
	
	protected String customeCodeProcessing(NxMpConfigMapping mappingData,String inputDesignDetails,
			Map<String, Object> requestMap) {
		if(StringUtils.isNotEmpty(mappingData.getVariableName())){
			if((MyPriceConstants.ETHERNET.equals(mappingData.getOffer()) || 
					MyPriceConstants.TDM.equals(mappingData.getOffer())) && 
					(mappingData.getVariableName().equalsIgnoreCase(MyPriceConstants.SPEED_LOCAL_ACCESS_PF)
							|| mappingData.getVariableName().equalsIgnoreCase(MyPriceConstants.INT_SPEED_LOCAL_ACCESS_PF))) {
				if(mappingData.getPath().contains(MyPriceConstants.MULTI_JSON_PATH_SEPERATOR)){
					StringBuilder sb = new StringBuilder();
					List<String> pathList= new ArrayList<String>(
							Arrays.asList(mappingData.getPath().split(MyPriceConstants.MULTI_JSON_PATH_SEPERATOR)));
					for(String path:pathList) {
						String result =convertAccessSpeed(this.getItemValueUsingJsonPath(path,inputDesignDetails),
								mappingData); 
						if(StringUtils.isNotEmpty(result)) {
							if(sb.length()>0 && StringUtils.isNotEmpty(mappingData.getDelimiter())) {
								sb.append(mappingData.getDelimiter());
							}
							if(StringUtils.isNotEmpty(result)) {
								sb.append(result);
							}
						}
					}
					return sb.toString();
				}else {
					return convertAccessSpeed(this.getItemValueUsingJsonPath(mappingData.getPath(),inputDesignDetails),
							mappingData); 
				}
			}else if(mappingData.getDelimiter()!=null && mappingData.getDefaultValue()!=null && 
					requestMap.containsKey(MyPriceConstants.NX_INR_DESIGN_DETAILS_COUNT)) {
				int count = (int) requestMap.get(MyPriceConstants.NX_INR_DESIGN_DETAILS_COUNT);
				StringBuilder sb=new StringBuilder();
				for(int j=0;j<count;j++) {
					if(sb.length()>0) {
						sb.append(mappingData.getDelimiter());
					}
					sb.append(mappingData.getDefaultValue());
				}
				return sb.toString();
			}
			else if(requestMap.containsKey(mappingData.getVariableName()) && null != requestMap.get(mappingData.getVariableName())) {
				String result= requestMap.get(mappingData.getVariableName()).toString();
				requestMap.remove(mappingData.getVariableName());
				return result;
			}
		}
		return "";
		
	}
	
	protected String convertAccessSpeed(String input,NxMpConfigMapping mappingData) {
		if(MyPriceConstants.ETHERNET.equals(mappingData.getOffer())) {
			return converAccessSpeedEthernet(input,mappingData);
		}else if(MyPriceConstants.TDM.equals(mappingData.getOffer())) {
			return converAccessSpeedTDM(input,mappingData);
		}
		return null;
	}
	
	protected String converAccessSpeedEthernet(String input,NxMpConfigMapping mappingData) {
		if(StringUtils.isNotEmpty(input)) {
			if(input.contains("GBPS")) {
				String data= input.substring(0,input.indexOf("GBPS")).concat("Gbps");
				if(StringUtils.isNotEmpty(data) && data.equalsIgnoreCase("1 Gbps")) {
					return "1000 Mbps";
				}
				return data;
			}else if(input.contains("MBPS")) {
				return input.substring(0,input.indexOf("MBPS")).concat("Mbps");
			}if(input.contains("KBPS")) {
				return input.substring(0,input.indexOf("KBPS")).concat("Kbps");
			}else {
				return input;
			}
		}
		return input;
	}
	
	protected String converAccessSpeedTDM(String input,NxMpConfigMapping mappingData) {
		if(StringUtils.isNotEmpty(input)) {
			 for(Map.Entry<String, String> entry : MyPriceConstants.TDM_ACCESS_SPEED_CONVERSION.entrySet()) {
				 String [] x=input.split("\\s+");
				 if(Arrays.asList(x).contains(entry.getKey())) {
					 input=input.replace(entry.getKey(), entry.getValue());
				 }
			 }
			 if(StringUtils.isNotEmpty(mappingData.getDataSetName())) {
					return this.processDataSetName(input,mappingData);
			 }
		}
		
		return input;
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
	
	
	
	/**
	 * Process list result.
	 *
	 * @param mappingData the mapping data
	 * @param inputDesignDetails the input design details
	 * @param offerId the offer id
	 * @return the string
	 */
	protected String processListResult(NxMpConfigMapping mappingData,String inputDesignDetails) {
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
	
	
	protected String getResultCount(NxMpConfigMapping mappingData,String inputDesignDetails) {
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
	
	/**
	 * Process or condition.
	 *
	 * @param mappingData the mapping data
	 * @param inputDesignDetails the input design details
	 * @param offerId the offer id
	 * @param pathList the path list
	 * @return the string
	 */
	protected String processOrCondition(NxMpConfigMapping mappingData,String inputDesignDetails,
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
	
	/**
	 * Process multiple json path.
	 *
	 * @param mappingData the mapping data
	 * @param inputDesignDetails the input design details
	 * @param offerId the offer id
	 * @param pathList the path list
	 * @param delim the delim
	 * @return the string
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected String processMultipleJsonPath(NxMpConfigMapping mappingData, String inputDesignDetails,
			List<String> pathList,String delim) {
		StringBuilder sb = new StringBuilder();
		for(String path:pathList) {
			if(path.contains(",")) {
				List<String> subConditionPathLst= new ArrayList(Arrays.asList(path.split(MyPriceConstants.COMMA_SEPERATOR)));
				if(sb.length()>0 && StringUtils.isNotEmpty(delim)) {
					 sb.append(delim);
				}
				String result=processMultipleJsonPath(mappingData, inputDesignDetails, 
						subConditionPathLst, MyPriceConstants.COMMA_SEPERATOR);
				if(StringUtils.isNotEmpty(result)) {
					sb.append(result);
				}
			}else if (path.contains(MyPriceConstants.OR_CONDITION_SEPERATOR)){
				List<String> subConditionPathLst= new ArrayList(Arrays.asList(path.split(Pattern.quote
						(MyPriceConstants.OR_CONDITION_SEPERATOR))));
				if(sb.length()>0 && StringUtils.isNotEmpty(delim)) {
					 sb.append(delim);
				}
				String result=this.processOrCondition(mappingData, inputDesignDetails,subConditionPathLst);
				if(StringUtils.isNotEmpty(result)) {
					sb.append(result);
				}
			}else {
				String itemValue=this.getItemValueUsingJsonPath(path,inputDesignDetails);
				if(StringUtils.isNotEmpty(itemValue)) {
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
	 * Gets the item value using json path.
	 *
	 * @param jsonPath the json path
	 * @param inputDesignDetails the input design details
	 * @return the item value using json path
	 */
	protected String getItemValueUsingJsonPath(String jsonPath,String inputDesignDetails) {
		if(jsonPath.contains("$")) {
			if(jsonPath.contains("{")  && jsonPath.contains("}") ) { 
				//This method is used to append static value before or after jsonPath result
				return this.processAppendedCharWithJsonPath(jsonPath, inputDesignDetails);
			}else {
				Object result=nexxusJsonUtility.getValue(inputDesignDetails, jsonPath);
				if(null!=result) {
					return String.valueOf(result);
				}
			}
			return null;
		}
		return jsonPath; 
	}
	
	protected String processAppendedCharWithJsonPath(String jsonPath,String inputDesignDetails) {
		try {
			int open=jsonPath.indexOf("{");
			int close=jsonPath.indexOf("}");
			if(open!=-1 && close!=1){
				String path=jsonPath.substring(open+1, close);
				String charBefore=jsonPath.substring(0,open);
				String chatAfter=jsonPath.substring(close+1,jsonPath.length());
				Object result=nexxusJsonUtility.getValue(inputDesignDetails, path);
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
	
	/**
	 * Gets the data from sales look up.
	 *
	 * @param input the input
	 * @param mappingData the mapping data
	 * @param offerId the offer id
	 * @return the data from sales look up
	 */
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
	
	public Map<String,List<String>> getConfigProdutMapFromLookup(String datasetName){
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

	public Map<String, String> getConfigProdutFromLookup(String datasetName){
		Map<String,String> result=new HashMap<String,String>();
		List<NxLookupData> nxLookupLst=nxLookupDataRepository.findByDatasetName(datasetName);
		 Optional.ofNullable(nxLookupLst).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull).
		    forEach( data -> {
		    	if(StringUtils.isNotEmpty(data.getCriteria())) {
		    		result.put(data.getItemId(), data.getCriteria());
		    	}
		 });
		return result;
	}

}
