package com.att.sales.nexxus.myprice.transaction.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxMpConfigMapping;
import com.att.sales.nexxus.dao.repository.NxLineItemProcessingDao;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.jayway.jsonpath.TypeRef;

@Component
public class ConfigAndUpdatePricingUtilFmo {
	
	


	@Autowired
	private NexxusJsonUtility nexxusJsonUtility;
	
	@Autowired
	private JsonPathUtil jsonPathUtil;
	
	@Autowired
	private NxLineItemProcessingDao lineItemProcessingDao;
	
	@Autowired
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String getData(NxMpConfigMapping mappingData,String inputDesignDetails,Map<String, Object> requestMap) {
		Long offerId=MyPriceConstants.OFFER_NAME_OFFER_ID_MAP.get(mappingData.getOffer());
		String result=null;
		if(StringUtils.isNotEmpty(mappingData.getType()) && 
				mappingData.getType().equals(MyPriceConstants.IS_DEFAULT)) {
			result= mappingData.getDefaultValue();
		}else if(StringUtils.isNotEmpty(mappingData.getType()) && 
				mappingData.getType().equals("Custome")) {
			result= this.customeCodeProcessing(mappingData, inputDesignDetails,requestMap);
		}else if(StringUtils.isNotEmpty(mappingData.getPath())){
			if(StringUtils.isNotEmpty(mappingData.getType()) && mappingData.getType().equals("List")) {
				result= this.processListResult(mappingData, inputDesignDetails, offerId);
			}else if(StringUtils.isNotEmpty(mappingData.getType()) && mappingData.getType().equals("Count")) {
				result= this.getResultCount(mappingData, inputDesignDetails);
			}else if(mappingData.getPath().contains(MyPriceConstants.OR_CONDITION_SEPERATOR)){
				List<String> pathList= new ArrayList(Arrays.asList(mappingData.getPath().split(
						Pattern.quote(MyPriceConstants.OR_CONDITION_SEPERATOR))));
				result= processOrCondition(mappingData, inputDesignDetails, offerId, pathList);
			}else if(mappingData.getPath().contains(MyPriceConstants.MULTI_JSON_PATH_SEPERATOR)){
				List<String> pathList= new ArrayList(Arrays.asList(mappingData.getPath().split(MyPriceConstants.MULTI_JSON_PATH_SEPERATOR)));
				result= processMultipleJsonPath(mappingData,inputDesignDetails,offerId,pathList,mappingData.getDelimiter());
			}else {
				result=this.getItemValueUsingJsonPath(mappingData.getPath(), inputDesignDetails);
				if(StringUtils.isNotEmpty(mappingData.getDataSetName())) {
					result= this.processDataSetName(result, mappingData, offerId);
				}
			}
		}
		if(StringUtils.isEmpty(result)) {
			result=mappingData.getDefaultValue();
		}
		return result;
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
	 * Handle code manually.
	 *
	 * @param mappingData the mapping data
	 * @param inputDesignDetails the input design details
	 * @return the string
	 */
	protected String customeCodeProcessing(NxMpConfigMapping mappingData,String inputDesignDetails,
			Map<String, Object> requestMap) {
		String result=null;
		if(StringUtils.isNotEmpty(mappingData.getVariableName())){
			if(requestMap.containsKey(mappingData.getVariableName())
					&& null!=requestMap.get(mappingData.getVariableName())) {
			   result=requestMap.get(mappingData.getVariableName()).toString();
			   requestMap.remove(mappingData.getVariableName());
			}
		}
		return result;
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
	protected String processMultipleJsonPath(NxMpConfigMapping mappingData, String inputDesignDetails, Long offerId,
			List<String> pathList,String delim) {
		StringBuilder sb = new StringBuilder();
		for(String path:pathList) {
			if(path.contains(",")) {
				List<String> subConditionPathLst= new ArrayList(Arrays.asList(path.split(MyPriceConstants.COMMA_SEPERATOR)));
				if(sb.length()>0 && StringUtils.isNotEmpty(delim)) {
					 sb.append(delim);
				}
				String result=processMultipleJsonPath(mappingData, inputDesignDetails, offerId, 
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
				String result=this.processOrCondition(mappingData, inputDesignDetails, offerId, subConditionPathLst);
				if(StringUtils.isNotEmpty(result)) {
					sb.append(result);
				}
			}else {
				String itemValue=this.getItemValueUsingJsonPath(path,inputDesignDetails);
				if(StringUtils.isNotEmpty(itemValue)) {
					if(StringUtils.isNotEmpty(mappingData.getDataSetName())) {
						itemValue=this.processDataSetName(itemValue, mappingData, offerId);
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
	protected String processOrCondition(NxMpConfigMapping mappingData,String inputDesignDetails,Long offerId,
			List<String> pathList) {
		for(String path:pathList) {
			String itemValue=null;
			if(path.contains(MyPriceConstants.MULTI_JSON_PATH_SEPERATOR)){
				List<String> subPathList= new ArrayList<String>(Arrays.asList(path.split(MyPriceConstants.MULTI_JSON_PATH_SEPERATOR)));
				itemValue= processMultipleJsonPath(mappingData,inputDesignDetails,offerId,subPathList,mappingData.getDelimiter());
			}else {
				itemValue=this.getItemValueUsingJsonPath(path,inputDesignDetails);
			}
			if(StringUtils.isNotEmpty(itemValue)) {
				if(StringUtils.isNotEmpty(mappingData.getDataSetName())) {
					itemValue=this.processDataSetName(itemValue,mappingData,offerId);
				}
				if(StringUtils.isNotEmpty(itemValue)) {
					return itemValue;
				}
			}
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
	protected String processListResult(NxMpConfigMapping mappingData,String inputDesignDetails, Long offerId) {
		TypeRef<List<String>> ref = new TypeRef<List<String>>() {};
		List<String> dataLst=jsonPathUtil.search(inputDesignDetails, mappingData.getPath(), ref);
		StringBuilder sb = new StringBuilder();
		if(CollectionUtils.isNotEmpty(dataLst)) {
			for(String itemValue:dataLst) {
				if(StringUtils.isNotEmpty(mappingData.getDataSetName())) {
					itemValue=this.processDataSetName(itemValue,mappingData,offerId);
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
	protected String processDataSetName(String input,NxMpConfigMapping mappingData,Long offerId) {
		String dataSourceName=mappingData.getDataSetName();
		if(dataSourceName.equals(MyPriceConstants.SALES_LOOKUP_SOURCE)) {
			input = getDataFromSalesLookUp(input,offerId,mappingData.getUdfId(),mappingData.getComponentId());
		}else if(dataSourceName.contains(MyPriceConstants.NX_LOOKUP_SOURCE)) {
			String looupDataSet = dataSourceName.substring(dataSourceName.indexOf('|')+1, dataSourceName.length()); 
			input = getDataFromNxLookUp(input, looupDataSet);
		}
		return input;
	}
	
	/**
	 * Gets the item value using json path.
	 *
	 * @param jsonPath the json path
	 * @param inputDesignDetails the input design details
	 * @return the item value using json path
	 */
	protected String getItemValueUsingJsonPath(String jsonPath,String inputDesignDetails) {
		Object result=nexxusJsonUtility.getValue(inputDesignDetails, jsonPath);
		if(null!=result) {
			return String.valueOf(result);
		}
		return null;
	}
	
	
	/**
	 * Gets the data from nx look up.
	 *
	 * @param input the input
	 * @param looupDataSet the looup data set
	 * @return the data from nx look up
	 */
	/*public String getDataFromNxLookUp(String input, String looupDataSet) {
		if(StringUtils.isNotEmpty(looupDataSet)) {
			NxLookupData nxLookup=nxLookupDataRepository.findTopByDatasetNameAndItemId(looupDataSet, input);
			if(null!=nxLookup) {
				input=nxLookup.getDescription();
			}
		}
		return input;
	}*/
	
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
	
	public Boolean isProductLineIdMatchForConfigDesign(Map<String,Object> methodParam,String respParentId,String offerName) {
		
		String reqProductLineId=methodParam.get(MyPriceConstants.MP_PRODUCT_LINE_ID)!=null?
				(String)methodParam.get(MyPriceConstants.MP_PRODUCT_LINE_ID):"";
		
		if(offerName.equals(MyPriceConstants.AVPN) || offerName.equals(MyPriceConstants.ADI) 
				|| MyPriceConstants.AVPN_ETHERNET.equalsIgnoreCase(offerName) || MyPriceConstants.AVPN_TDM.equalsIgnoreCase(offerName) 
				|| MyPriceConstants.AVPN_INTL_ACCESS.equalsIgnoreCase(offerName) || MyPriceConstants.ADI_ETHERNET.equalsIgnoreCase(offerName) 
				|| MyPriceConstants.ADI_TDM.equalsIgnoreCase(offerName) || MyPriceConstants.BVoIP.equalsIgnoreCase(offerName)) {
			if(StringUtils.isNotEmpty(respParentId) && 
					reqProductLineId.equals(respParentId)) {
				return true;		
			}	
			return false;
		}
		return true;
		
	}
	
	
	
	
	
}
