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

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

public class ConfigAndUpdatePricingUtilInrTest {
	
	@InjectMocks
	private ConfigAndUpdatePricingUtilInr configAndUpdatePricingUtilInr;
	
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
		inputDesignDetails = "{\"CktId\":\"MLEC762924ATI\",\"CustomerLocationInfo\":{\"ServiceState\":\"TN\",\"USOCInfo\":[{\"USOC\":\"1LNET\",\"NetRate\":\"451.20\",\"AccessSpeed\":\"10 MBPS BASIC SVC\"}],\"PhysicalInterface\":\"100 Mbps\",\"ServiceZip\":\"37211\",\"ServiceCity\":\"NASHVILLE\",\"ServiceProvider\":\"ATT/BELL SOUTH (TN)\",\"AttCtrOffcCLLICd\":\"NSVLTNMT\",\"ServiceAddress2\":\"FLR 1 RM SUITE 211\",\"ServiceAddress1\":\"475  METROPLEX DR\",\"AccessArchitecture\":\"SWITCHED\",\"CustSrvgWireCtrCLLICd\":\"NSVLTNCH\"},\"product\":\"Ethernet\"}";
		requestMap = new HashMap<String, Object>();
	}
	
	@Test
	public void testGetData() {
		mappingData.setType(MyPriceConstants.IS_DEFAULT);
		mappingData.setDefaultValue("defaultValue");
		assertThat(configAndUpdatePricingUtilInr.getData(mappingData, inputDesignDetails, requestMap)).as(" equal to ").isEqualTo("defaultValue");
		
		mappingData.setType("Custome");
		mappingData.setVariableName(MyPriceConstants.SPEED_LOCAL_ACCESS_PF);
		mappingData.setOffer("Ethernet");
		mappingData.setPath("$..CustomerLocationInfo.USOCInfo.*.AccessSpeed");
		Mockito.when(nexxusJsonUtility.getValue(anyString(), anyString())).thenReturn("10 mbps");
		assertThat(configAndUpdatePricingUtilInr.getData(mappingData, inputDesignDetails, requestMap)).as(" equal to ").isEqualTo("10 mbps");
		
		mappingData.setType("List");
		List<String> str = new ArrayList<String>();
		str.add("data1");
		str.add("data2");
		Mockito.when(jsonPathUtill.search(anyString(), anyString(), any())).thenReturn(str);
		configAndUpdatePricingUtilInr.getData(mappingData, inputDesignDetails, requestMap);
		

		mappingData.setType("Count");
		configAndUpdatePricingUtilInr.getData(mappingData, inputDesignDetails, requestMap);
		
		mappingData.setType(null);
		mappingData.setPath("$..CustomerLocationInfo.ServiceCity||PLACEHOLDER");
		configAndUpdatePricingUtilInr.getData(mappingData, inputDesignDetails, requestMap);
		
		mappingData.setPath("$..CustomerLocationInfo.ServiceAddress1##$..CustomerLocationInfo.ServiceAddress2");
		configAndUpdatePricingUtilInr.getData(mappingData, inputDesignDetails, requestMap);
		
		mappingData.setPath("$..CustomerLocationInfo.AccessArchitecture");
		mappingData.setDataSetName("NX_LOOKUP_DATA|mp_ethernet_accessArchitecture");
		Map<String,NxLookupData> resultMap = new HashMap<String,NxLookupData>();
		NxLookupData lookup = new NxLookupData();
		lookup.setDescription("SWITCHED");
		resultMap.put("SWITCHED", lookup);
		Mockito.when(nexxusJsonUtility.getValue(anyString(), anyString())).thenReturn("SWITCHED");
		Mockito.when(nxMyPriceRepositoryServce.getLookupDataByItemId(anyString())).thenReturn(resultMap);
		configAndUpdatePricingUtilInr.getData(mappingData, inputDesignDetails, requestMap);
		mappingData.setDataSetName(null);
	}
	
	@Test
	public void testCustomeCodeProcessing() {
		mappingData.setDefaultValue("defaultValue");
		mappingData.setVariableName(MyPriceConstants.SPEED_LOCAL_ACCESS_PF);
		requestMap.put(MyPriceConstants.NX_INR_DESIGN_DETAILS_COUNT, 2);
		mappingData.setVariableName("SiteCity_pf");
		mappingData.setDelimiter("#");
		configAndUpdatePricingUtilInr.customeCodeProcessing(mappingData, inputDesignDetails, requestMap);
		
		mappingData.setDefaultValue(null);
		mappingData.setDelimiter(null);
		requestMap.remove(MyPriceConstants.NX_INR_DESIGN_DETAILS_COUNT, 2);
		requestMap.put("SiteCity_pf", "NY");
		configAndUpdatePricingUtilInr.customeCodeProcessing(mappingData, inputDesignDetails, requestMap);
		
		mappingData.setType("Custome");
		mappingData.setVariableName(MyPriceConstants.INT_SPEED_LOCAL_ACCESS_PF);
		mappingData.setOffer("TDM");
		mappingData.setPath("$..CustomerLocationInfo.USOCInfo.*.AccessSpeed##$..CustomerLocationInfo.USOCInfo.*.AccessSpeed");
		mappingData.setDelimiter("$");
		Mockito.when(nexxusJsonUtility.getValue(anyString(), anyString())).thenReturn("10 mbps");
		configAndUpdatePricingUtilInr.customeCodeProcessing(mappingData, inputDesignDetails, requestMap);
		mappingData.setDelimiter(null);
	}
	
	@Test
	public void testConverAccessSpeedEthernet() {
		NxMpConfigMapping mappingData = new NxMpConfigMapping();
		assertThat(configAndUpdatePricingUtilInr.converAccessSpeedEthernet("10 MBPS", mappingData)).as(" equal to ").isEqualTo("10 Mbps");
		
		assertThat(configAndUpdatePricingUtilInr.converAccessSpeedEthernet("10 KBPS", mappingData)).as(" equal to ").isEqualTo("10 Kbps");
		
		assertThat(configAndUpdatePricingUtilInr.converAccessSpeedEthernet("1 GBPS", mappingData)).as(" equal to ").isEqualTo("1000 Mbps");
	}

	@Test
	public void testAppendCharAt() {
		configAndUpdatePricingUtilInr.appendCharAt("Test", "Start", "S");
		configAndUpdatePricingUtilInr.appendCharAt("Test", "Start", "E");
		configAndUpdatePricingUtilInr.appendCharAt("Test", "Start", null);	
	}
	
	@Test
	public void testGetResultCount() {
		mappingData.setPath("$..CustomerLocationInfo.ServiceAddress1##$..CustomerLocationInfo.ServiceAddress2");
		List<String> str = new ArrayList<String>();
		str.add("data1");
		str.add("data2");
		Mockito.when(jsonPathUtill.search(anyString(), anyString(), any())).thenReturn(str);
		configAndUpdatePricingUtilInr.getResultCount(mappingData, inputDesignDetails);
	}
	
	@Test
	public void testProcessOrCondition() {
		mappingData.setPath("$..CustomerLocationInfo.ServiceCity##$..CustomerLocationInfo.ServiceCity||PLACEHOLDER");
		List<String> pathList= new ArrayList<String>(Arrays.asList(mappingData.getPath().split(
				Pattern.quote(MyPriceConstants.OR_CONDITION_SEPERATOR))));
		configAndUpdatePricingUtilInr.processOrCondition(mappingData, inputDesignDetails, pathList);
		
	}
	
	@Test
	public void testProcessMultipleJsonPath() {
		List<String> pathList= new ArrayList<String>();
		pathList.add("$..CustomerLocationInfo.ServiceCity||$..CustomerLocationInfo.ServiceCity,$..CustomerLocationInfo.ServiceCity");
		configAndUpdatePricingUtilInr.processMultipleJsonPath(mappingData, inputDesignDetails,
				pathList, ",");
	}
	
	@Test
	public void testProcessAppendedCharWithJsonPath() {
		Mockito.when(nexxusJsonUtility.getValue(anyString(), anyString())).thenReturn("10001");
		configAndUpdatePricingUtilInr.processAppendedCharWithJsonPath("TBD-{$..nxSiteId}", inputDesignDetails);
		
		configAndUpdatePricingUtilInr.processAppendedCharWithJsonPath("{$..nxSiteId}-TBD", inputDesignDetails);
		
		configAndUpdatePricingUtilInr.processAppendedCharWithJsonPath("{$..nxSiteId}", inputDesignDetails);
		
		Mockito.when(nexxusJsonUtility.getValue(anyString(), anyString())).thenThrow(Exception.class);
		configAndUpdatePricingUtilInr.processAppendedCharWithJsonPath("TBD-{$..nxSiteId}", inputDesignDetails);
	}
	
	@Test
	public void testGetDataFromSalesLookUp() {
		List<Object> result= new ArrayList<>();
		result.add("Interstate");
		Mockito.when(nxLineItemProcessingDao.getDataFromSalesLookUpTbl(any(), anyLong(), anyLong(), anyLong())).thenReturn(result);
		configAndUpdatePricingUtilInr.getDataFromSalesLookUp("Interstate", 120l, 200176l, 1210l); 
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
		configAndUpdatePricingUtilInr.getConfigProdutMapFromLookup("mp_ethernet_accessArchitecture");
	}
	
	@Test
	public void testGetConfigProdutFromLookup() {
		List<NxLookupData> nxLookupLst= new ArrayList<>();
		NxLookupData lookup = new NxLookupData();
		lookup.setCriteria("ABN");
		lookup.setItemId("ABN");
		nxLookupLst.add(lookup);
		Mockito.when(nxLookupDataRepository.findByDatasetName(anyString())).thenReturn(nxLookupLst);
		configAndUpdatePricingUtilInr.getConfigProdutFromLookup("INR_CONFIG_SOL_PRODUCT_SKIP");
	}
}
