package com.att.sales.nexxus.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.reteriveicb.model.Solution;
import com.att.sales.nexxus.service.FmoProcessingRepoService;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * The Class FmoProcessingServiceTest.
 */
/**
 * @author vt393d
 *
 */
@ExtendWith(MockitoExtension.class)
public class NexxusJsonUtilityTest {

	@Spy
	@InjectMocks
	NexxusJsonUtility  nexxusJsonUtility;
	
	@Mock
	private JsonPathUtil jsonPathUtil;
	
	@Mock
	private ObjectMapper mapper;
	
	@Mock
	private FmoProcessingRepoService repositoryService;
	
	
	
	@Test
	public void convertListToCsvWithQuoteTest() {
		
		nexxusJsonUtility.convertListToCsvWithQuote(Arrays.asList("s","d"));
	}
	
	
	@Test
	public void convertJsonToMapTest() {
		when(nexxusJsonUtility.getObjectmapper()).thenReturn(mapper);
		nexxusJsonUtility.convertJsonToMap("{abc}");
	}
	
	@Test
	public void convertJsonToMapExceptionTest() throws JsonParseException, 
	JsonMappingException, IOException {
		
		when(nexxusJsonUtility.getObjectmapper()).thenReturn(mapper);
		doThrow(new IOException()).when(mapper).readValue("{abc}",Map.class);
		nexxusJsonUtility.convertJsonToMap("{abc}");
	}
	
	@Test
	public void getValueTest1() {
		List<Object> data=new ArrayList<>();
		data.add(new Object());
		when(jsonPathUtil.search(any(),any(),any())).thenReturn(data);
		nexxusJsonUtility.getValue(new Object(), "$.dd");
	}
	
	@Test
	public void getValueTest2() {
		nexxusJsonUtility.getValue(null, "$.dd");
	}
	
	@Test
	public void getValueLstTest1() {
		List<Object> data=new ArrayList<>();
		data.add(new Object());
		when(jsonPathUtil.search(any(),any(),any())).thenReturn(data);
		nexxusJsonUtility.getValueLst(new Object(), "$.dd");
	}
	
	@Test
	public void getValueLstTest2() {
		nexxusJsonUtility.getValueLst(null, "$.dd");
	}
	
	@Test
	public void getJsonPathTest() {
		nexxusJsonUtility.getJsonPath("{abc}", 1);
	}
	
	
	@Test
	public void TestConvertJavaToJson() throws SalesBusinessException {
		when(nexxusJsonUtility.getObjectmapper()).thenReturn(mapper);
		Object inputObj=new Object();
		nexxusJsonUtility.convertJavaToJson(inputObj);
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void TestConvertJavaToJsontExceptionScenario1() throws SalesBusinessException, JsonGenerationException, JsonMappingException, IOException {
		Object inputObj=new Object();
		when(nexxusJsonUtility.getObjectmapper()).thenReturn(mapper);
		doThrow(new JsonGenerationException("")).when(mapper).writeValueAsString(any());
		nexxusJsonUtility.convertJavaToJson(inputObj);
	}
	
	@Test
	public void TestConvertJavaToJsontExceptionScenario2() throws SalesBusinessException, JsonGenerationException, JsonMappingException, IOException {
		Object inputObj=new Object();
		when(nexxusJsonUtility.getObjectmapper()).thenReturn(mapper);
		doThrow(new IOException("")).when(mapper).writeValueAsString(any());
		nexxusJsonUtility.convertJavaToJson(inputObj);
	}
	
	@Test
	public void TestConvertJsonToJavaObject() throws SalesBusinessException {
		when(nexxusJsonUtility.getObjectmapper()).thenReturn(mapper);
		String requestjson="{\"id\":\"123\"}";
		nexxusJsonUtility.convertJsonToJavaObject(requestjson);
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void TestConvertJsonToJavaObjectExceptionScenario1() throws SalesBusinessException, JsonGenerationException, JsonMappingException, IOException {
		String inputJson="";
		when(nexxusJsonUtility.getObjectmapper()).thenReturn(mapper);
		doThrow(new JsonGenerationException("")).when(mapper).readValue("",Object.class);
		nexxusJsonUtility.convertJsonToJavaObject(inputJson);
	}
	
	@Test
	public void TestConvertJsonToJavaObjectScenario2() throws SalesBusinessException, JsonGenerationException, JsonMappingException, IOException {
		String inputJson="";
		when(nexxusJsonUtility.getObjectmapper()).thenReturn(mapper);
		doThrow(new IOException("")).when(mapper).readValue("",Object.class);
		nexxusJsonUtility.convertJsonToJavaObject(inputJson);
	}
	
	@Test
	public void convertStringJsonToMapTest() {
		String data = "{}";
		nexxusJsonUtility.convertStringJsonToMap(data);
		
		data = "{";
		nexxusJsonUtility.convertStringJsonToMap(data);
		
		data = null;
		nexxusJsonUtility.convertStringJsonToMap(data);
	}
	
	@Test
	public void convertMapToJsonTest() throws SalesBusinessException {
		Map<String, String> inputmap = new HashMap<>();
		nexxusJsonUtility.convertMapToJson(inputmap);
		
		inputmap.put("key", "value");
		nexxusJsonUtility.convertMapToJson(inputmap);
	}
	
	@Test
	public void convertMapToJsonExceptionTest() throws org.codehaus.jackson.JsonGenerationException, org.codehaus.jackson.map.JsonMappingException, IOException, SalesBusinessException {
		Map<String, String> inputmap = new HashMap<>();
		inputmap.put("key", "value");
		doReturn(mapper).when(nexxusJsonUtility).getObjectmapper();
		doThrow(JsonProcessingException.class).when(mapper).writeValueAsString(any());
		nexxusJsonUtility.convertMapToJson(inputmap);
	}
	
	@Test
	public void convertMapToJsonIOExceptionTest() throws org.codehaus.jackson.JsonGenerationException, org.codehaus.jackson.map.JsonMappingException, IOException, SalesBusinessException {
		Map<String, String> inputmap = new HashMap<>();
		inputmap.put("key", "value");
		doReturn(mapper).when(nexxusJsonUtility).getObjectmapper();
		doThrow(IOException.class).when(mapper).writeValueAsString(any());
		nexxusJsonUtility.convertMapToJson(inputmap);
	}
	
	@Test
	public void appendQuotesTest() {
		List<String> input = Arrays.asList("1", "2");
		nexxusJsonUtility.appendQuotes(input);
		
		input = Arrays.asList("1");
		nexxusJsonUtility.appendQuotes(input);
	}
	
	@Test
	public void isExistsTest() {
		List<Object> data = new ArrayList<>();
		when(jsonPathUtil.search(any(), any(), any())).thenReturn(data);
		nexxusJsonUtility.isExists("", "path");
		
		data.add("1");
		nexxusJsonUtility.isExists("", "path");
	}
	
	@Test
	public void getDataFromSalesProdCompTest() {
		String datasetName = "SALES_MS_PRODCOMP_UDF_ATTR_VAL";
		nexxusJsonUtility.getDataFromSalesProdComp(datasetName, "", null, null, null);
		nexxusJsonUtility.getDataFromSalesProdComp(datasetName, null, null, null, null);
	}
}
