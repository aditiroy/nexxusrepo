package com.att.sales.nexxus.rest.handlers;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.constant.CustomJsonConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxMpConfigJsonMapping;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdateRestUtilInr;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdateRestUtilPd;
import com.att.sales.nexxus.myprice.transaction.service.RestCommonUtil;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;

@ExtendWith(MockitoExtension.class)
public class CustomJsonProcessingUtilTest {
	
	@Spy
	@InjectMocks
	private CustomJsonProcessingUtil customJsonProcessingUtil;
	
	@Mock
	private JsonPathUtil jsonPathUtil;
	
	@Mock
	private NexxusJsonUtility nexxusJsonUtility;
	
	@Mock
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	@Mock
	private ConfigAndUpdateRestUtilPd configAndUpdateRestUtilPd;
	
	@Mock
	private ConfigAndUpdateRestUtilInr configAndUpdateRestUtilInr;
	
	@Mock
	private RestCommonUtil restCommonUtil;
	
	@Test
	public void createJsonStringTest() {
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		inputParamMap.put(MyPriceConstants.PRODUCT_TYPE, MyPriceConstants.SOURCE_PD);
		Object inputDesign="{}";
		List<NxMpConfigJsonMapping> f =new ArrayList<NxMpConfigJsonMapping>();
		NxMpConfigJsonMapping nf=new NxMpConfigJsonMapping();
		nf.setKey("config");
		nf.setFieldName("config");
		nf.setFieldParent("root");
		NxMpConfigJsonMapping nf2=new NxMpConfigJsonMapping();
		nf2.setKey("item");
		nf2.setFieldName("item");
		nf2.setFieldParent("root");
		nf2.setFieldType("Array");
		nf2.setArrayElementName("item/element");
		nf2.setType(MyPriceConstants.IS_DEFAULT);
		nf2.setDefaultValue("[{ \"_index\": 0,\"int_productModel_pm\": \"ADE\",\"modelPath_pm\": \"wireline:telco:aDE\"}]");
		
		NxMpConfigJsonMapping nf3=new NxMpConfigJsonMapping();
		nf3.setKey("value");
		nf3.setFieldName("value");
		nf3.setFieldParent("root");
		nf3.setFieldType("Object");
		f.add(nf);
		f.add(nf2);
		f.add(nf3);
		when(nxMyPriceRepositoryServce.findByOfferAndProductTypeAndRuleNameForJson(any(),any(),any())).thenReturn(f);
		customJsonProcessingUtil.createJsonString(inputParamMap, inputDesign);
	}
	
	@Test
	public void createJsonStringTest2() {
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		inputParamMap.put(MyPriceConstants.PRODUCT_TYPE, MyPriceConstants.SOURCE_PD);
		Object inputDesign="{}";
		List<NxMpConfigJsonMapping> f =new ArrayList<NxMpConfigJsonMapping>();
		NxMpConfigJsonMapping nf=new NxMpConfigJsonMapping();
		nf.setKey("config");
		nf.setFieldName("config");
		nf.setFieldParent("root");
		NxMpConfigJsonMapping nf2=new NxMpConfigJsonMapping();
		nf2.setKey("item");
		nf2.setFieldName("item");
		nf2.setFieldParent("root");
		nf2.setFieldType("Array");
		nf2.setType(MyPriceConstants.IS_DEFAULT);
		nf2.setDefaultValue("[\"ABC\",\"BCD\"]");
		
		NxMpConfigJsonMapping nf3=new NxMpConfigJsonMapping();
		nf3.setKey("value");
		nf3.setFieldName("value");
		nf3.setFieldParent("root");
		nf3.setFieldType("Object");
		nf3.setInputPath("$..");
		f.add(nf);
		f.add(nf2);
		f.add(nf3);
		when(nxMyPriceRepositoryServce.findByOfferAndProductTypeAndRuleNameForJson(any(),any(),any())).thenReturn(f);
		customJsonProcessingUtil.createJsonString(inputParamMap, inputDesign);
	}
	
	@Test
	public void createJsonStringTestException() {
		Object inputDesign="{}";
		customJsonProcessingUtil.createJsonString(null, inputDesign);
	}
	
	@Test
	public void setArrayIndexTest() {
		LinkedHashMap<String, String> f=new LinkedHashMap<String, String>();
		customJsonProcessingUtil.setArrayIndex(0, f);
	}
	
	@Test
	public void getDataTest() {
		NxMpConfigJsonMapping jsonRule=new NxMpConfigJsonMapping();
		jsonRule.setType(MyPriceConstants.IS_DEFAULT);
		Object input="{}";
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		customJsonProcessingUtil.getData(jsonRule, input, inputParamMap, String.class);
	}
	
	@Test
	public void getDataTest1() {
		NxMpConfigJsonMapping jsonRule=new NxMpConfigJsonMapping();
		jsonRule.setType(CustomJsonConstants.CUSTOM_CODE);
		Object input="{}";
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		customJsonProcessingUtil.getData(jsonRule, input, inputParamMap, String.class);
	}
	
	@Test
	public void getDataTest2() {
		NxMpConfigJsonMapping jsonRule=new NxMpConfigJsonMapping();
		jsonRule.setType(CustomJsonConstants.CUSTOM_JSON_STRING);
		Object input="{}";
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		customJsonProcessingUtil.getData(jsonRule, input, inputParamMap, String.class);
	}
	
	@Test
	public void getDataTest3() {
		NxMpConfigJsonMapping jsonRule=new NxMpConfigJsonMapping();
		jsonRule.setType("Input");
		Object input="{}";
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		customJsonProcessingUtil.getData(jsonRule, input, inputParamMap, String.class);
	}
	
	@Test
	public void getDataTest4() {
		NxMpConfigJsonMapping jsonRule=new NxMpConfigJsonMapping();
		jsonRule.setInputPath("$..siteId");
		Object input="{}";
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		customJsonProcessingUtil.getData(jsonRule, input, inputParamMap, String.class);
	}
	
	@Test
	public void getDataTest5() {
		NxMpConfigJsonMapping jsonRule=new NxMpConfigJsonMapping();
		jsonRule.setInputPath("$..siteId##$..siteId1");
		jsonRule.setDefaultValue("0");
		Object input="{}";
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		customJsonProcessingUtil.getData(jsonRule, input, inputParamMap, String.class);
	}
	
	@Test
	public void getDataTest6() {
		NxMpConfigJsonMapping jsonRule=new NxMpConfigJsonMapping();
		jsonRule.setInputPath("$..siteId||$..siteId1");
		jsonRule.setDatasetName("abc");
		jsonRule.setDefaultValue("A");
		Object input="{}";
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		customJsonProcessingUtil.getData(jsonRule, input, inputParamMap, String.class);
	}
	
	@Test
	public void getDataTest7() {
		NxMpConfigJsonMapping jsonRule=new NxMpConfigJsonMapping();
		jsonRule.setFieldType("Number");
		jsonRule.setDefaultValue("0");
		Object input="{}";
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		customJsonProcessingUtil.getData(jsonRule, input, inputParamMap, String.class);
	}
	
	@Test
	public void customProcessing1() {
		NxMpConfigJsonMapping jsonRule=new NxMpConfigJsonMapping();
		jsonRule.setProductType(MyPriceConstants.SOURCE_PD);
		Object input="{}";
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		customJsonProcessingUtil.customProcessing(jsonRule, input, inputParamMap, String.class);
	}
	
	@Test
	public void customProcessing2() {
		NxMpConfigJsonMapping jsonRule=new NxMpConfigJsonMapping();
		jsonRule.setProductType(MyPriceConstants.SOURCE_INR);
		Object input="{}";
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		customJsonProcessingUtil.customProcessing(jsonRule, input, inputParamMap, String.class);
	}
	
	@Test
	public void customProcessing3() {
		NxMpConfigJsonMapping jsonRule=new NxMpConfigJsonMapping();
		jsonRule.setKey("A");
		Object input="{}";
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		inputParamMap.put("A", "B");
		customJsonProcessingUtil.customProcessing(jsonRule, input, inputParamMap, String.class);
	}
	
	@Test
	public void customJsonStringProcessingTest() {
		NxMpConfigJsonMapping jsonRule=new NxMpConfigJsonMapping();
		jsonRule.setDatasetName("ABC");
		jsonRule.setInputPath("$..");
		jsonRule.setKey("A");
		Object input="{}";
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		inputParamMap.put("A", "B");
		customJsonProcessingUtil.customJsonStringProcessing(jsonRule, input, inputParamMap, String.class);
	}
	
	@Test
	public void customJsonStringProcessingTest1() {
		NxMpConfigJsonMapping jsonRule=new NxMpConfigJsonMapping();
		jsonRule.setDatasetName("ABC#A#A");
		jsonRule.setInputPath("$..");
		jsonRule.setKey("config");
		Object input="{}";
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		inputParamMap.put(MyPriceConstants.PRODUCT_TYPE, MyPriceConstants.SOURCE_PD);
		inputParamMap.put("A", "B");
		List<NxMpConfigJsonMapping> f =new ArrayList<NxMpConfigJsonMapping>();
		NxMpConfigJsonMapping nf=new NxMpConfigJsonMapping();
		nf.setKey("config");
		nf.setFieldName("config");
		nf.setFieldType("Object");
		nf.setFieldParent("root");
		NxMpConfigJsonMapping nf2=new NxMpConfigJsonMapping();
		nf2.setKey("item");
		nf2.setFieldName("item");
		nf2.setFieldParent("config");
		nf2.setFieldType("String");
		nf2.setType(MyPriceConstants.IS_DEFAULT);
		nf2.setDefaultValue("ABC");
		f.add(nf);
		f.add(nf2);
		when(nxMyPriceRepositoryServce.findByOfferAndProductTypeAndRuleNameForJson(any(),any(),any())).thenReturn(f);
		String s="ABC";
		when(restCommonUtil.handleCast(any(),any())).thenReturn(s);
		customJsonProcessingUtil.customJsonStringProcessing(jsonRule, input, inputParamMap, String.class);
	}
	
	@Test
	public void getListDataForArrayTest() {
		NxMpConfigJsonMapping jsonRule=new NxMpConfigJsonMapping();
		jsonRule.setDatasetName("ABC");
		jsonRule.setInputPath("$..");
		jsonRule.setKey("A");
		jsonRule.setType(CustomJsonConstants.CUSTOM_CODE);
		Object input="{}";
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		inputParamMap.put("A", Arrays.asList("h"));
		customJsonProcessingUtil.getListDataForArray(jsonRule, input, inputParamMap, String.class);
	}
	

	@Test
	public void getListDataForArrayTest1() {
		NxMpConfigJsonMapping jsonRule=new NxMpConfigJsonMapping();
		jsonRule.setDatasetName("ABC");
		jsonRule.setInputPath("$..");
		jsonRule.setKey("A");
		Object input="{}";
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		inputParamMap.put("A", Arrays.asList("h"));
		customJsonProcessingUtil.getListDataForArray(jsonRule, input, inputParamMap, String.class);
	}
	
	@Test
	public void getListDataForArrayTest2() {
		NxMpConfigJsonMapping jsonRule=new NxMpConfigJsonMapping();
		jsonRule.setInputPath("$..");
		jsonRule.setKey("A");
		jsonRule.setDefaultValue("[\"H\"]");
		Object input="{}";
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		inputParamMap.put("A", Arrays.asList("h"));
		customJsonProcessingUtil.getListDataForArray(jsonRule, input, inputParamMap, String.class);
	}
	
	@Test
	public void createNewJsonObjectTest3() {
		NxMpConfigJsonMapping jsonRule=new NxMpConfigJsonMapping();
		jsonRule.setInputPath("$..");
		jsonRule.setKey("A");
		jsonRule.setDefaultValue("[\"H\"]");
		jsonRule.setType(CustomJsonConstants.CUSTOM_CODE);
		Object input="{}";
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		inputParamMap.put("A", new LinkedHashMap<Integer, Object>());
		customJsonProcessingUtil.createNewJsonObject(jsonRule, input, inputParamMap);
	}
	
	@Test
	public void getChildJsonRulesByParentTest() {
		List<NxMpConfigJsonMapping>  allJsonRule=new ArrayList<NxMpConfigJsonMapping>();
		NxMpConfigJsonMapping jsonRule=new NxMpConfigJsonMapping();
		jsonRule.setFieldParent("abc");
		allJsonRule.add(jsonRule);
		customJsonProcessingUtil.getChildJsonRulesByParent(allJsonRule, "abc");
	}
	
	@Test
	public void processMultipleJsonPathTest() {
		NxMpConfigJsonMapping jsonRule=new NxMpConfigJsonMapping();
		StringBuilder sb = new StringBuilder();
		sb.append("ss");
		jsonRule.setInputPath("$..");
		jsonRule.setKey("A");
		jsonRule.setDefaultValue("[\"H\"]");
		jsonRule.setType(CustomJsonConstants.CUSTOM_CODE);
		Object inputDesignJson="{}";
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		inputParamMap.put("A", new LinkedHashMap<Integer, Object>());
		List<String> pathList=new ArrayList<String>();
		pathList.add("$..offer,$..site");
		customJsonProcessingUtil.processMultipleJsonPath(jsonRule, inputDesignJson, pathList, ",");
	}
	
	@Test
	public void processMultipleJsonPathTest1() {
		StringBuilder sb = new StringBuilder();
		sb.append("ss");
		NxMpConfigJsonMapping jsonRule=new NxMpConfigJsonMapping();
		jsonRule.setInputPath("$..");
		jsonRule.setKey("A");
		jsonRule.setDefaultValue("[\"H\"]");
		jsonRule.setDatasetName("ss");
		jsonRule.setType(CustomJsonConstants.CUSTOM_CODE);
		Object inputDesignJson="{}";
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		inputParamMap.put("A", new LinkedHashMap<Integer, Object>());
		List<String> pathList=new ArrayList<String>();
		pathList.add("$..offer||$..site");
		String f="sss";
		when(restCommonUtil.getItemValueUsingJsonPath(any(),any(),any())).thenReturn(f);
		String f2="sss";
		when(restCommonUtil.processDataSetName(any(),any(),any())).thenReturn(f2);
		customJsonProcessingUtil.processMultipleJsonPath(jsonRule, inputDesignJson, pathList, "");
	}
	
	@Test
	public void processMultipleJsonPathTest3() {
		StringBuilder sb = new StringBuilder();
		sb.append("ss");
		NxMpConfigJsonMapping jsonRule=new NxMpConfigJsonMapping();
		jsonRule.setInputPath("$..");
		jsonRule.setKey("A");
		jsonRule.setDefaultValue("[\"H\"]");
		jsonRule.setType(CustomJsonConstants.CUSTOM_CODE);
		Object inputDesignJson="{}";
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		inputParamMap.put("A", new LinkedHashMap<Integer, Object>());
		List<String> pathList=new ArrayList<String>();
		pathList.add("$..offer");
		String f="sss";
		when(restCommonUtil.getItemValueUsingJsonPath(any(),any(),any())).thenReturn(f);
		customJsonProcessingUtil.processMultipleJsonPath(jsonRule, inputDesignJson, pathList, "");
	}
	
	@Test
	public void processMultipleJsonPathTest4() {
		StringBuilder sb = new StringBuilder();
		sb.append("ss");
		NxMpConfigJsonMapping jsonRule=new NxMpConfigJsonMapping();
		jsonRule.setInputPath("$..");
		jsonRule.setKey("A");
		jsonRule.setDatasetName("ss");
		jsonRule.setType(CustomJsonConstants.CUSTOM_CODE);
		Object inputDesignJson="{}";
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		inputParamMap.put("A", new LinkedHashMap<Integer, Object>());
		List<String> pathList=new ArrayList<String>();
		pathList.add("$..offer");
		String f="sss";
		when(restCommonUtil.getItemValueUsingJsonPath(any(),any(),any())).thenReturn(f);
		customJsonProcessingUtil.processMultipleJsonPath(jsonRule, inputDesignJson, pathList, "$");
	}
	
	@Test
	public void getListObjectTest() {
		Object inputDesignJson="{}";
		 List<String> l=new ArrayList<String>();
		 l.add("gh");
		when(jsonPathUtil.search(any(),any(),any())).thenReturn(l);
		customJsonProcessingUtil.getListObject(inputDesignJson, "$..", String.class);
	}
	
	@Test
	public void getNxConfigMappingTest() {
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		inputParamMap.put(MyPriceConstants.PRODUCT_TYPE, MyPriceConstants.SOURCE_PD);
		inputParamMap.put(MyPriceConstants.SUB_OFFER_NAME, "IR");
		customJsonProcessingUtil.getNxConfigMapping(inputParamMap);
	}
	
	

}
