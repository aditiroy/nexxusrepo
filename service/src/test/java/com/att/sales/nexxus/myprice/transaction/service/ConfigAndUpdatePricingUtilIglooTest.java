package com.att.sales.nexxus.myprice.transaction.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxMpConfigMapping;
import com.att.sales.nexxus.dao.repository.NxLineItemProcessingDao;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;

@ExtendWith(MockitoExtension.class)

public class ConfigAndUpdatePricingUtilIglooTest {
	
	@InjectMocks
	private ConfigAndUpdatePricingUtilIgloo configAndUpdatePricingUtilIgloo;
	
	private NxMpConfigMapping mappingData;
	
	private String inputDesignDetails;
	
	private Map<String, Object> requestMap;
	
	@Mock
	private NexxusJsonUtility nexxusJsonUtility;
	
	@Mock
	private JsonPathUtil jsonPathUtill;
	
	@Mock
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	@Mock
	private NxLineItemProcessingDao nxLineItemProcessingDao;
	
	@Mock
	private NxLookupDataRepository nxLookupDataRepository;
	
	@BeforeEach
	public void init() {
		mappingData = new NxMpConfigMapping();
		inputDesignDetails = "{\"address\":\"8665 CYPRESS WATERS BLVD\",\"city\":\"IRVING\",\"country\":\"United States\",\"reqVendor\":\"SBC\",\"attEthPop\":\"DLLSTXTL\",\"service\":\"AVPN-ETH\",\"bandwidth\":\"20\",\"accessArch\":\"Switched\",\"physicalInterface\":\"100BaseTX Electrical\",\"baseMonthlyRecurringPrice\":\"906.00\",\"flowType\":\"IGL\"}";
		requestMap = new HashMap<String, Object>();
	}
	
	@Test
	public void testGetData() {
		mappingData.setType(MyPriceConstants.IS_DEFAULT);
		mappingData.setDefaultValue("defaultValue");
		assertThat(configAndUpdatePricingUtilIgloo.getData(mappingData, inputDesignDetails, requestMap)).as(" equal to ").isEqualTo("defaultValue");
		
		mappingData.setPath("$..address##$..city##$..state##$..zipCode##$..country");
		mappingData.setType("List");
		List<String> str = new ArrayList<String>();
		str.add("data1");
		str.add("data2");
		Mockito.when(jsonPathUtill.search(anyString(), anyString(), any())).thenReturn(str);
		configAndUpdatePricingUtilIgloo.getData(mappingData, inputDesignDetails, requestMap);
		

		mappingData.setType("Count");
		configAndUpdatePricingUtilIgloo.getData(mappingData, inputDesignDetails, requestMap);
		
		mappingData.setType(null);
		mappingData.setPath("$..reqState||$..custState");
		configAndUpdatePricingUtilIgloo.getData(mappingData, inputDesignDetails, requestMap);
		
		mappingData.setPath("$..address##$..city##$..state##$..zipCode##$..country");
		configAndUpdatePricingUtilIgloo.getData(mappingData, inputDesignDetails, requestMap);
		
		mappingData.setPath("$..physicalInterface");
		mappingData.setDataSetName("NX_LOOKUP_DATA|mp_physicalInterface_ethernet");
		Map<String,NxLookupData> resultMap = new HashMap<String,NxLookupData>();
		NxLookupData lookup = new NxLookupData();
		lookup.setDescription("100 Mbps");
		resultMap.put("100 Mbps", lookup);
		Mockito.when(nexxusJsonUtility.getValue(anyString(), anyString())).thenReturn("100 Mbps");
		Mockito.when(nxMyPriceRepositoryServce.getLookupDataByItemId(anyString())).thenReturn(resultMap);
		configAndUpdatePricingUtilIgloo.getData(mappingData, inputDesignDetails, requestMap);
		mappingData.setDataSetName(null);
	}
	
	@Test
	public void testGetResultCount() {
		mappingData.setPath("$..address##$..city##$..state##$..zipCode##$..country");
		List<String> str = new ArrayList<String>();
		str.add("data1");
		str.add("data2");
		Mockito.when(jsonPathUtill.search(anyString(), anyString(), any())).thenReturn(str);
		configAndUpdatePricingUtilIgloo.getResultCount(mappingData, inputDesignDetails);
	}
	
	@Test
	public void testGetDataFromSalesLookUp() {
		List<Object> result= new ArrayList<>();
		result.add("Interstate");
		Mockito.when(nxLineItemProcessingDao.getDataFromSalesLookUpTbl(any(), anyLong(), anyLong(), anyLong())).thenReturn(result);
		configAndUpdatePricingUtilIgloo.getDataFromSalesLookUp("Interstate", 120l, 200176l, 1210l); 
	}
	

	@Test
	public void testProcessMultipleJsonPath() {
		List<String> pathList= new ArrayList<String>();
		pathList.add("$..reqCity||$..reqCity,$..custCity");
		configAndUpdatePricingUtilIgloo.processMultipleJsonPath(mappingData, inputDesignDetails, pathList, ",");
	}
	
	@Test
	public void testProcessOrCondition() {
		mappingData.setPath("$..reqZipCode||$..custPostalcode||PLACEHOLDER");
		List<String> pathList= new ArrayList<String>(Arrays.asList(mappingData.getPath().split(
				Pattern.quote(MyPriceConstants.OR_CONDITION_SEPERATOR))));
		configAndUpdatePricingUtilIgloo.processOrCondition(mappingData, inputDesignDetails, pathList);
		
	}
	
	
	@Test
	public void testCustomeCodeProcessing() {
		mappingData.setOffer("TDM");
		mappingData.setVariableName(MyPriceConstants.SPEED_LOCAL_ACCESS_PF);
		mappingData.setPath("$..reqAccessBandwidth##$..reqAccessBandwidth");
		mappingData.setDelimiter("#");
		configAndUpdatePricingUtilIgloo.customeCodeProcessing(mappingData, inputDesignDetails, requestMap);
		
		mappingData.setDefaultValue(null);
		mappingData.setDelimiter(null);
		mappingData.setPath("$..reqAccessBandwidth");
		requestMap.put("reqAccessBandwidth", "100");
		configAndUpdatePricingUtilIgloo.customeCodeProcessing(mappingData, inputDesignDetails, requestMap);
		


	}
	
	@Test
	public void testAppendCharAt() {
		configAndUpdatePricingUtilIgloo.appendCharAt("Test", "Start", "S");
		configAndUpdatePricingUtilIgloo.appendCharAt("Test", "Start", "E");
		configAndUpdatePricingUtilIgloo.appendCharAt("Test", "Start", null);	
	}
	
	@Test
	public void testGetConfigProdutMapFromLookup() {
		List<NxLookupData> nxLookupLst= new ArrayList<>();
		NxLookupData lookup = new NxLookupData();
		lookup.setDescription("SWITCHED");
		lookup.setCriteria("LocalAccess,Local Access");
		lookup.setItemId("SWITCHED");
		nxLookupLst.add(lookup);
		Mockito.when(nxLookupDataRepository.findByDatasetName(anyString())).thenReturn(nxLookupLst);
		configAndUpdatePricingUtilIgloo.getConfigProdutMapFromLookup("mp_ethernet_accessArchitecture");
	}
	
	@Test
	public void testConverAccessSpeedEthernet() {
		configAndUpdatePricingUtilIgloo.converAccessSpeedEthernet("1 GBPS");
		configAndUpdatePricingUtilIgloo.converAccessSpeedEthernet("1 MBPS");
		configAndUpdatePricingUtilIgloo.converAccessSpeedEthernet("1 KBPS");
		
	}
	
	@Test
	public void testConverAccessSpeedTDM() {
		configAndUpdatePricingUtilIgloo.converAccessSpeedTDM("1 GBPS");
	}

	@Test
	public void testProcessAppendedCharWithJsonPath() {
		Mockito.when(nexxusJsonUtility.getValue(anyString(), anyString())).thenReturn("10001");
		configAndUpdatePricingUtilIgloo.processAppendedCharWithJsonPath("TBD-{$..nxSiteId}", inputDesignDetails);
		
		configAndUpdatePricingUtilIgloo.processAppendedCharWithJsonPath("{$..nxSiteId}-TBD", inputDesignDetails);
		
		configAndUpdatePricingUtilIgloo.processAppendedCharWithJsonPath("{$..nxSiteId}", inputDesignDetails);
		
		Mockito.when(nexxusJsonUtility.getValue(anyString(), anyString())).thenThrow(Exception.class);
		configAndUpdatePricingUtilIgloo.processAppendedCharWithJsonPath("TBD-{$..nxSiteId}", inputDesignDetails);
	}

}
