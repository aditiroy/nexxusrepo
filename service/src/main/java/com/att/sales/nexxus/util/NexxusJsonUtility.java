package com.att.sales.nexxus.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.FmoConstants;
import com.att.sales.nexxus.constant.MessageConstants;
import com.att.sales.nexxus.service.FmoProcessingRepoService;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.jsonpath.TypeRef;

/**
 * The Class NexxusJsonUtility.
 *
 * @author vt393d
 */
@Component
public class NexxusJsonUtility {
	
/** The logger. */
private static Logger logger = LoggerFactory.getLogger(NexxusJsonUtility.class);
	
	/** The json path util. */
	@Autowired
	private JsonPathUtil jsonPathUtil;
	
	@Autowired
	private FmoProcessingRepoService repositoryService;
	
	/**
	 * Gets the objectmapper.
	 *
	 * @return the objectmapper
	 */
	protected  ObjectMapper getObjectmapper() {
		return new ObjectMapper();
	}


	/**
	 * Convert list to csv with quote.
	 *
	 * @param inputList the input list
	 * @return the string
	 */
	public String convertListToCsvWithQuote(List<?> inputList) {
		String inputValue=null;
		if(CollectionUtils.isNotEmpty(inputList)) {
			inputValue = inputList.toString().replace("[", "'").replace("]", "'")
		            .replace(", ", "','");
		}
		return inputValue;
	}
	
	
	/**
	 * Convert json to map.
	 *
	 * @param data the data
	 * @return the map
	 */
	@SuppressWarnings("unchecked")
	public Map<String,String> convertJsonToMap(String data){
		Map<String,String> dataMap=new HashMap<>();
		if(StringUtils.isNotEmpty(data)) {
			try {
				dataMap=getObjectmapper().readValue(data, Map.class);
			} catch (IOException e) {
				logger.error("Exceptionwhile while converting Json to Map "
						+ "in convertJsonToMap  {}", e);
				return dataMap;
			}
		}
		
		return dataMap;
	}
	
	
	/**
	 * Convert string json to map.
	 *
	 * @param data the data
	 * @return the map
	 */
	public Map<?,?> convertStringJsonToMap(String data){
		Map<?,?> dataMap=new HashMap<>();
		if(StringUtils.isNotEmpty(data)) {
			try {
				dataMap=getObjectmapper().readValue(data, Map.class);
			} catch (IOException e) {
				logger.error("Exceptionwhile while converting Json to Map "
						+ "in convertJsonToMap  {}", e);
				return dataMap;
			}
		}
		
		return dataMap;
	}
	
	
	
	
	/**
	 * Gets the value.
	 *
	 * @param jsonObject the json object
	 * @param path the path
	 * @return the value
	 */
	public Object getValue(Object jsonObject,String path) {
		if(null!=jsonObject && StringUtils.isNotEmpty(path)) {
			TypeRef<List<Object>> mapType = new TypeRef<List<Object>>() {};
			List<Object> data=jsonPathUtil.search(jsonObject, path,mapType);
			if(CollectionUtils.isNotEmpty(data)) {
				return data.get(0);
			}
		}
		
		return null;
	}
	
	
	/**
	 * Gets the value lst.
	 *
	 * @param jsonObject the json object
	 * @param path the path
	 * @return the value lst
	 */
	public List<Object> getValueLst(Object jsonObject,String path) {
		if(null!=jsonObject && StringUtils.isNotEmpty(path)) {
			TypeRef<List<Object>> mapType = new TypeRef<List<Object>>() {};
			List<Object> data=jsonPathUtil.search(jsonObject, path,mapType);
			if(CollectionUtils.isNotEmpty(data)) {
				return data;
			}
		}
		
		return new ArrayList<>();
	}
	
	
	/**
	 * Gets the json path.
	 *
	 * @param baseJsonPath the base json path
	 * @param parameters the parameters
	 * @return the json path
	 */
	public String getJsonPath(String baseJsonPath,Object... parameters) {
		String path=baseJsonPath;
		if(StringUtils.isNotEmpty(baseJsonPath) && null!=parameters && parameters.length>0) {
			for (int i = 0; i < parameters.length; i++) { 
				if(null!=parameters[i]){
					String param=this.appendQuotes(parameters[i]);
					path=baseJsonPath.replaceFirst(":"+i, param);
				}
	        } 
		}
		return path;
	}
	
	
	/**
	 * Convert java to json.
	 *
	 * @param inputObj the input obj
	 * @return the string
	 * @throws SalesBusinessException the sales business exception
	 */
	public String convertJavaToJson(Object inputObj) throws  SalesBusinessException {
		String jsonString="";
		try {
			jsonString = getObjectmapper().writeValueAsString(inputObj);
		} catch (JsonGenerationException e) {
			logger.error("Json generation error {}", e);
			throw new SalesBusinessException(MessageConstants.PROCESS_JSON_ERROR_CODE);
		
		} catch (IOException e) {
			logger.error("IO Exception {}", e);
			throw new SalesBusinessException(MessageConstants.PROCESS_ERROR_CODE);
		}
		return jsonString;
	}
	
	/**
	 * Convert json to java object.
	 *
	 * @param requestJson the request json
	 * @return the object
	 * @throws SalesBusinessException the sales business exception
	 */
	public Object convertJsonToJavaObject(String requestJson) throws SalesBusinessException{
		Object responceObject=null;
		try {
			responceObject =getObjectmapper().readValue(requestJson, Object.class);
		} catch (JsonGenerationException e) {
			logger.error("Json generation error {}", e);
			throw new SalesBusinessException(MessageConstants.PROCESS_JSON_ERROR_CODE);
		} catch (IOException e) {
			logger.error("IO Exception {}", e);
			throw new SalesBusinessException(MessageConstants.PROCESS_ERROR_CODE);
		}
		return responceObject;
	}
	
	
	/**
	 * Convert map to string json.
	 *
	 * @param inputmap the inputmap
	 * @return the string
	 * @throws SalesBusinessException the sales business exception
	 */
	public String convertMapToJson(Map<?,?> inputmap) throws SalesBusinessException {
		String resultJson=null;
		if(MapUtils.isNotEmpty(inputmap)) {
			try {
				resultJson = getObjectmapper().writeValueAsString(inputmap);
			} catch (JsonProcessingException e) {
				logger.error("Json generation error {}", e);
				throw new SalesBusinessException(MessageConstants.PROCESS_JSON_ERROR_CODE);
			} catch (IOException e) {
				logger.error("IO Exception {}", e);
				throw new SalesBusinessException(MessageConstants.PROCESS_ERROR_CODE);
			}
		}
		return resultJson;
	}
	
	/**
	 * Append quotes.
	 *
	 * @param input the input
	 * @return the string
	 */
	public String appendQuotes(Object input) {
		String inputValue=null;
		if(null!=input) {
			if(input instanceof List<?>) {
				StringBuilder sb = new StringBuilder();
				  Iterator<?> it = ((List<?>) input).iterator();
				  sb.append("[\"").append(it.next()).append('"'); // Not empty
				  if(it.hasNext()) {
					  while (it.hasNext()) {
						    sb.append(", \"").append(it.next()).append("\"]");
						  }
				  }else {
					  sb.append("]");
				  }
				  
				  inputValue = sb.toString();
			}else  {
				inputValue="\"" + input + "\"";
			}
		}
		
		
		return inputValue;
	}
	
	public Boolean isExists(Object jsonObject,String path) {
		if(null!=jsonObject && StringUtils.isNotEmpty(path)) {
			TypeRef<List<Object>> mapType = new TypeRef<List<Object>>() {};
			List<Object> data=jsonPathUtil.search(jsonObject, path,mapType);
			if(CollectionUtils.isNotEmpty(data)) {
				return true;
			}
		}
		return false;
	}
	
	public Object getDataFromSalesProdComp(String datasetName,Object inputData,Long offerId,Long udfId,Long componentId) {
		if(StringUtils.isNotEmpty(datasetName) &&  null!=inputData) {
			if(datasetName.equals(FmoConstants.SALES_UDF_LOOKUP_SOURCE)) {
				return repositoryService.getDataFromSalesLookUpTbl(inputData,offerId,udfId,componentId);
			}
		}	
		return inputData;
	}
	
}
