package com.att.sales.nexxus.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxFmoMPOutputJsonMapping;
import com.att.sales.nexxus.dao.model.NxLineItemLookUpDataModel;
import com.att.sales.nexxus.dao.model.NxLineItemLookUpFieldModel;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

@Component
public class FmoMpOutputJsonHelper {
	

	@Autowired
	private NexxusJsonUtility nexusJsonUtility;
	
	@Autowired
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	public void processMPoutputJson(List<NxLineItemLookUpDataModel> inpuLineItemDataLst,JSONObject siteObj,
			NxLineItemLookUpFieldModel fieldLookup,Map<String,Object> inputMap) {
		if(CollectionUtils.isNotEmpty(inpuLineItemDataLst)) {
			String siteJsonString=JacksonUtil.toString(siteObj);
			for(NxLineItemLookUpDataModel lineItemdata:inpuLineItemDataLst) {
				String lineItemLookupJsonString=JacksonUtil.toString(lineItemdata);
				Set<NxFmoMPOutputJsonMapping> mappingData=fieldLookup.getMpJsonMapping();
				 for(NxFmoMPOutputJsonMapping data:mappingData) {
					 if(StringUtils.isNotEmpty(data.getActive()) && "Y".equalsIgnoreCase(data.getActive())) {
						 Object inputData=this.getDataForInsert(lineItemLookupJsonString,siteJsonString,data,inputMap);
						 if(null!=inputData) {
							this.handleSetProcess(data,inputData,siteJsonString,lineItemLookupJsonString, inputMap,siteObj);
						 }
					 }
				 }
			}
		}
	}
	
	
	
	protected Object getDataForInsert(String lineItemLookupJsonString,String siteJsonString,NxFmoMPOutputJsonMapping mappingData,Map<String,Object> inputMap){
		Object result=null;
		if(StringUtils.isNotEmpty(mappingData.getGetType()) && 
				mappingData.getGetType().equals(MyPriceConstants.IS_DEFAULT)) {
			return mappingData.getDefaultValue();
		}else if(StringUtils.isNotEmpty(mappingData.getGetType()) && 
			mappingData.getGetType().equals("Custome")) {
			result=this.customeCodeProcessingForGetData(mappingData,lineItemLookupJsonString,inputMap);		
		}else {
			String getPath=this.creategGetPath(mappingData.getGetPath(),mappingData, siteJsonString,lineItemLookupJsonString);
			if(StringUtils.isNotEmpty(mappingData.getGetType()) && 
					mappingData.getGetType().equals(MyPriceConstants.REQUEST_SITE_DATA_SOURCE)) {
				//getting data from input request Site Json
				result=nexusJsonUtility.getValue(siteJsonString, getPath);		
			}else {
				//getting data from input lineItem lookup  
				result=nexusJsonUtility.getValue(lineItemLookupJsonString, getPath);
			}
			
			if(StringUtils.isNotEmpty(mappingData.getDataSetName())) {
				result= this.processDataSetName(result, mappingData);
			}
			
		}
		if(null==result) {
			result=mappingData.getDefaultValue();
		}
		return result;
		
	}
	
	protected Object customeCodeProcessingForGetData(NxFmoMPOutputJsonMapping mappingData,String inpuLineItemData,
			Map<String, Object> requestMap) {
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
	protected Object processDataSetName(Object input,NxFmoMPOutputJsonMapping mappingData) {
		String dataSourceName=mappingData.getDataSetName();
		if(null!=input && dataSourceName.contains(MyPriceConstants.NX_LOOKUP_SOURCE)) {
			String looupDataSet = dataSourceName.substring(dataSourceName.indexOf('|')+1, dataSourceName.length()); 
			input = getDataFromNxLookUp(input.toString(), looupDataSet);
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

	
	
	protected  void  handleSetProcess(NxFmoMPOutputJsonMapping mappingData,Object inputData,String siteJsonString,
			String lineItemLookupJsonString,Map<String,Object> inputMap,JSONObject siteObj) {
		if(StringUtils.isNotEmpty(mappingData.getGetType()) && 
				mappingData.getGetType().equals("Custome")) {
			this.customeCodeProcessingForSetData(mappingData, inputData, siteJsonString, lineItemLookupJsonString, inputMap);
		}else if(StringUtils.isNotEmpty(mappingData.getSetPath())){
			String setPath=null;
			if(mappingData.getSetPath().contains(MyPriceConstants.MULTI_JSON_PATH_SEPERATOR)){
				List<String> pathList= new ArrayList<String>(Arrays.asList(mappingData.getSetPath().split(
						MyPriceConstants.MULTI_JSON_PATH_SEPERATOR)));
				for(String path:pathList) {
					setPath=createSetPath(path,mappingData,siteJsonString,lineItemLookupJsonString);
					 if(StringUtils.isNotEmpty(setPath)) {
						 this.insertOrUpdateData(siteObj,setPath,inputData,mappingData.getFiledType(),mappingData.getFieldName());
					 }
				}
			}else {
				setPath=this.createSetPath(mappingData.getSetPath(),mappingData,siteJsonString,lineItemLookupJsonString);
				 if(StringUtils.isNotEmpty(setPath)) {
					 this.insertOrUpdateData(siteObj,setPath,inputData,mappingData.getFiledType(),mappingData.getFieldName());
				 }
			}		
		}
	}
	
	protected void customeCodeProcessingForSetData(NxFmoMPOutputJsonMapping mappingData,Object inputData,String siteData,
			String lineItemLookupData,Map<String,Object> inputMap) {
		
	}
	
	@SuppressWarnings("unchecked")
	protected  void insertOrUpdateData(JSONObject siteObj, String setPath,Object data,String type,String fieldName) {
        Configuration conf = Configuration.defaultConfiguration().addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL).
        		addOptions(Option.SUPPRESS_EXCEPTIONS);  
        List<Object> result=nexusJsonUtility.getValueLst(siteObj,setPath);
        DocumentContext documentContext = JsonPath.using(conf).parse(siteObj);
        if("Array".equals(type)) {
        	if(CollectionUtils.isNotEmpty(result)) {
	        	documentContext.add(JsonPath.compile(setPath), data);
	        }else {
	        	JSONArray array=new JSONArray();
	        	array.add(data);
	        	documentContext.put(JsonPath.compile(this.getPath(setPath, fieldName)), fieldName, array);
	        }
	       
        }else {
        	if(CollectionUtils.isNotEmpty(result)) {
	        	//documentContext.set(JsonPath.compile(this.getPath(path,fieldName)), data);
        		documentContext.put(JsonPath.compile(this.getPath(setPath, fieldName)), fieldName, data);
	        }else {
	        	if("Object".equals(type)) {
	        		 List<Object> x=nexusJsonUtility.getValueLst(siteObj, this.getPath(setPath, fieldName));
	        		 if(CollectionUtils.isNotEmpty(x)) {
	        			 documentContext.put(JsonPath.compile(this.getPath(setPath, fieldName)), fieldName, data);
	        			 
	        		 }else {
	        			 JSONObject newObj=new JSONObject();
	        			 newObj.put(fieldName, data);
	        			 setPath=this.getPath(setPath,fieldName);
	        			 String objectName=setPath.substring(setPath.lastIndexOf(".")+1, setPath.length());
	        			 documentContext.put(JsonPath.compile(this.getPath(setPath,objectName)), objectName, newObj);
	        		 }
	        	}else {
	        		documentContext.put(JsonPath.compile(this.getPath(setPath, fieldName)), fieldName, data);
	        	}
	        	
	        }
        }
        siteObj= JacksonUtil.toJsonObject(documentContext.jsonString());
	}
	
	
	 protected String getPath(String inputPath,String fieldName) {
		 if(inputPath.contains(".."+fieldName)) {
			 return inputPath.replaceAll(".."+fieldName, "");
		 }else if(inputPath.contains("."+fieldName)) {
				return inputPath.replaceAll("."+fieldName, "");
		 }
		 return inputPath;
	 }
	
	 
	 @SuppressWarnings("unchecked")
	protected String createSetPath(String path,NxFmoMPOutputJsonMapping mappingData,String siteJsonString,
			 String lineItemLookupJsonString ) {
		 if(StringUtils.isNotEmpty(mappingData.getSetPathCriteria())) {
			 Map<String,String>  setPathCriteria=(Map<String,String>) 
						nexusJsonUtility.convertStringJsonToMap(mappingData.getSetPathCriteria());
			 if(MapUtils.isNotEmpty(setPathCriteria)) {
				 for(Map.Entry<String, String> entry : setPathCriteria.entrySet()) {
					 String val= getDataForPath(entry.getValue(),siteJsonString,lineItemLookupJsonString);
					 if(StringUtils.isNotEmpty(val)) {
						 path=path.replace(":"+entry.getKey(),val);
					 }else {
						 return null;
					 }
			     }
			 }
			 
		 }
		 return path;
	 }
	 
	 @SuppressWarnings("unchecked")
		protected String creategGetPath(String path,NxFmoMPOutputJsonMapping mappingData,String siteJsonString,
				 String lineItemLookupJsonString ) {
			 if(StringUtils.isNotEmpty(mappingData.getSetPathCriteria())) {
				 Map<String,String>  getPathCriteria=(Map<String,String>) 
							nexusJsonUtility.convertStringJsonToMap(mappingData.getGetPathCriteria());
				 if(MapUtils.isNotEmpty(getPathCriteria)) {
					 for(Map.Entry<String, String> entry : getPathCriteria.entrySet()) {
						 String val= getDataForPath(entry.getValue(),siteJsonString,lineItemLookupJsonString);
						 if(StringUtils.isNotEmpty(val)) {
							 path=path.replace(":"+entry.getKey(),val);
						 }else {
							 return null;
						 }
				     }
				 }
				 
			 }
			 return path;
		 }
	 
	
	 protected String getDataForPath(String criteria,String siteJsonString,String lineItemLookupJsonString ) {
		 if(StringUtils.isNotEmpty(criteria)) {
			 String path=criteria.substring(criteria.indexOf('|')+1, criteria.length()); 
			 Object result=null;
			 if(criteria.contains(MyPriceConstants.LINE_ITEM_DATA_SOURCE)) {
				 result=nexusJsonUtility.getValue(lineItemLookupJsonString,path);
			 }else if(criteria.contains(MyPriceConstants.REQUEST_SITE_DATA_SOURCE)) {
				 result=nexusJsonUtility.getValue(siteJsonString, path);
			 }else {
				 result=nexusJsonUtility.getValue(lineItemLookupJsonString,path);
			 }
			return result!=null?result.toString():null;
		 }
		 return null;
	 }	
}
