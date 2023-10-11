package com.att.sales.nexxus.myprice.transaction.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxMpConfigMapping;
import com.att.sales.nexxus.dao.repository.NxLineItemProcessingDao;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;

@ExtendWith(MockitoExtension.class)

public class ConfigAndUpdatePricingUtilFmoTest {
	
	@Spy
	@InjectMocks
	private ConfigAndUpdatePricingUtilFmo configAndUpdatePricingUtilFmo;
	
	
	private NxMpConfigMapping mappingData;
	
	private String inputDesignDetails;
	
	private Map<String, Object> requestMap;
	
	@Mock
	private NexxusJsonUtility nexxusJsonUtility;
	
	@Mock
	private JsonPathUtil jsonPathUtil;
	
	@Mock
	private NxLineItemProcessingDao lineItemProcessingDao;
	
	@Mock
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	@BeforeEach
	public void init() {
		mappingData = new NxMpConfigMapping();
		inputDesignDetails = "{\"site\":[{\"country\":\"US\",\"nxSiteId\":10204,\"zipCode\":\"95136\",\"address\":null,\"referenceOfferId\":4,\"isLineItemPicked\":\"Y\",\"city\":\"san jose\",\"address2\":null,\"address1\":"
				+ "\"20 shenado pl\",\"postalCode\":null,\"siteName\":\"test\",\"siteNpanxx\":\"408225\",\"regionCode\":null,\"swcClli\":\"SNJSCA13\",\"design\":[{\"portProtocol\":\"PPP\","
				+ "\"accessSpeedUdfAttrId\":30549,\"accessDetails\":{\"supplierName\":null,\"npanxx\":null,\"nrcListRate\":null,\"serialNumber\":null,\"tokenId\":null,\"mrcListRate\":null,"
				+ "\"respAccessInterconnect\":null,\"dqid\":null,\"physicalInterface\":null,\"portId\":null,\"speed\":null,\"quoteId\":null,\"iglooMaxMrcDiscount\":null,\"respSpeed\":null,\"popClli\":null,"
				+ "\"respSupplierName\":null,\"currencyCode\":null,\"respPopClli\":null},\"sitePopCilli\":null,\"portId\":9257029,\"interface\":null,\"referenceSiteId\":9958922,\"lac\":null,\"accessSpeed\":"
				+ "\"DS3\",\"accessType\":\"Private Line\",\"accessArchitecture\":null,\"accessTypeUdfAttrId\":30155,\"categoryLocalAccess\":null,\"portSpeed\":\"45M\",\"priceDetails\":[{\"country\":\"US\","
				+ "\"componentType\":\"Port\",\"priceScenarioId\":99999925473,\"quantity\":\"1\",\"priceType\":\"accessRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":0.0,\"frequency\":\"MRC\","
				+ "\"rdsPriceType\":\"ACCESS\",\"beid\":null,\"componentParentId\":9958922,\"productRateId\":2929,\"reqPriceType\":null,\"term\":60,\"localListPrice\":16852.78,\"referencePortId\":9257029},"
				+ "{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999925473,\"secondaryKeys\":\"#FCC#MPLS Port#Flat Rate#45M#T3#FR, ATM, IP#VPN Transport Connection#per port#18013#18030#United States#US#USA\","
				+ "\"quantity\":\"1\",\"priceType\":\"portRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":55.56,\"frequency\":\"MRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18013\",\"nrcBeid\":\"18030\",\"componentParentId"
				+ "\":9958922,\"productRateId\":906,\"reqPriceType\":null,\"term\":60,\"elementType\":\"Port\",\"uniqueId\":\"#MPLS Port#MPLS Port - 45M#FR, ATM, IP#45 Mbps#T3#VPN Transport#Connection#Each\",\"localListPrice\":1990.0,"
				+ "\"referencePortId\":9257029},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999925473,\"quantity\":\"1\",\"priceType\":\"portNRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":88.88,"
				+ "\"frequency\":\"NRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18030\",\"componentParentId\":9958922,\"productRateId\":930,\"reqPriceType\":\"Fixed\",\"term\":60,\"localListPrice\":1000.0,\"referencePortId\":9257029},"
				+ "{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999925473,\"secondaryKeys\":\"#FCC#CoS Package#Multimedia Standard Svc#45M#N\\/A#N\\/A#VPN Transport COS Package#per port#18292#18425#United States#US#USA\","
				+ "\"quantity\":\"1\",\"priceType\":\"cosRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":55.56,\"frequency\":\"MRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18292\",\"nrcBeid\":\"18425\",\"componentParentId"
				+ "\":9958922,\"productRateId\":1330,\"reqPriceType\":null,\"term\":60,\"elementType\":\"PortFeature\",\"uniqueId\":\"#CoS Package Multimedia Standard#CoS Package Multimedia Standard Svc - 45M#FR,"
				+ " ATM, IP#45 Mbps#T3#VPN Transport#COS Package#Port\",\"localListPrice\":0.0,\"referencePortId\":9257029},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999925473,\"quantity"
				+ "\":\"1\",\"priceType\":\"cosNRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":88.88,\"frequency\":\"NRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18425\",\"componentParentId\":9958922,\"productRateId\":"
				+ "1498,\"reqPriceType\":null,\"term\":60,\"localListPrice\":0.0,\"referencePortId\":9257029}],\"accessTailTechnology\":null,\"mileage\":null,\"siteType\":null}],\"siteId\":9958922,\"state\":\"CA\","
				+ "\"customerLocationClli\":null},{\"country\":\"US\",\"nxSiteId\":10204,\"zipCode\":\"95136\",\"address\":null,\"referenceOfferId\":4,\"isLineItemPicked\":\"Y\",\"city\":\"san jose\",\"address2\":null,"
				+ "\"address1\":\"20 shenado pl\",\"postalCode\":null,\"siteName\":\"test\",\"siteNpanxx\":\"408225\",\"regionCode\":null,\"swcClli\":\"SNJSCA13\",\"design\":[{\"portProtocol\":\"PPP\","
				+ "\"accessSpeedUdfAttrId\":30549,\"accessDetails\":{\"supplierName\":null,\"npanxx\":null,\"nrcListRate\":null,\"serialNumber\":null,\"tokenId\":null,\"mrcListRate\":null,"
				+ "\"respAccessInterconnect\":null,\"dqid\":null,\"physicalInterface\":null,\"portId\":null,\"speed\":null,\"quoteId\":null,\"iglooMaxMrcDiscount\":null,\"respSpeed\""
				+ ":null,\"popClli\":null,\"respSupplierName\":null,\"currencyCode\":null,\"respPopClli\":null},\"sitePopCilli\":null,\"portId\":9257029,\"interface\":null,\"referenceSiteId\":9958922,"
				+ "\"lac\":null,\"accessSpeed\":\"DS3\",\"accessType\":\"Private Line\",\"accessArchitecture\":null,\"accessTypeUdfAttrId\":30155,\"categoryLocalAccess\":null,\"portSpeed\":"
				+ "\"45M\",\"priceDetails\":[{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999925473,\"quantity\":\"1\",\"priceType\":\"accessRCRateId\","
				+ "\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":0.0,\"frequency\":\"MRC\",\"rdsPriceType\":\"ACCESS\",\"beid\":null,\"componentParentId\":9958922,\"productRateId\":2929,"
				+ "\"reqPriceType\":null,\"term\":60,\"localListPrice\":16852.78,\"referencePortId\":9257029},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999925473,"
				+ "\"secondaryKeys\":\"#FCC#MPLS Port#Flat Rate#45M#T3#FR, ATM, IP#VPN Transport Connection#per port#18013#18030#United States#US#USA\",\"quantity\":\"1\",\"priceType\":\"portRCRateId\","
				+ "\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":50.56,\"frequency\":\"MRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18013\",\"nrcBeid\":\"18030\",\"componentParentId\":9958922,"
				+ "\"productRateId\":906,\"reqPriceType\":null,\"term\":60,\"elementType\":\"Port\",\"uniqueId\":\"#MPLS Port#MPLS Port - 45M#FR, ATM, IP#45 Mbps#T3#VPN Transport#Connection#Each\","
				+ "\"localListPrice\":2000.0,\"referencePortId\":9257029},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999925473,\"quantity\":\"1\",\"priceType\":"
				+ "\"portNRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":88.88,\"frequency\":\"NRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18030\",\"componentParentId\":9958922,"
				+ "\"productRateId\":930,\"reqPriceType\":\"Fixed\",\"term\":60,\"localListPrice\":1000.0,\"referencePortId\":9257029},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999925473,"
				+ "\"secondaryKeys\":\"#FCC#CoS Package#Multimedia Standard Svc#45M#N\\/A#N\\/A#VPN Transport COS Package#per port#18292#18425#United States#US#USA\",\"quantity\":\"1\",\"priceType\":"
				+ "\"cosRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":50.56,\"frequency\":\"MRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18292\",\"nrcBeid\":\"18425\",\"componentParentId\":9958922,"
				+ "\"productRateId\":1330,\"reqPriceType\":null,\"term\":60,\"elementType\":\"PortFeature\",\"uniqueId\":\"#CoS Package Multimedia Standard#CoS Package Multimedia Standard Svc - 45M#FR, ATM, IP#45 Mbps#T3#VPN Transport#COS Package#Port\","
				+ "\"localListPrice\":20.0,\"referencePortId\":9257029},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999925473,\"quantity\":\"1\",\"priceType\":\"cosNRCRateId\",\"localCurrency\":\"USD\","
				+ "\"icbDesiredDiscPerc\":88.88,\"frequency\":\"NRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18425\",\"componentParentId\":9958922,\"productRateId\":1498,\"reqPriceType\":null,\"term\":60,\"localListPrice\":0.0,"
				+ "\"referencePortId\":9257029}],\"accessTailTechnology\":null,\"mileage\":null,\"siteType\":null}],\"siteId\":9958923,\"state\":\"CA\",\"customerLocationClli\":null}]}";
		requestMap = new HashMap<String, Object>();
	}
	
	
	@Test
	public void getDataTest() {mappingData.setType(MyPriceConstants.IS_DEFAULT);
	
		mappingData.setType(MyPriceConstants.IS_DEFAULT);
		mappingData.setDefaultValue("defaultValue");
		configAndUpdatePricingUtilFmo.getData(mappingData, inputDesignDetails, requestMap);
		
		
	
		//assertThat(configAndUpdatePricingUtilFmo.getData(mappingData, inputDesignDetails, requestMap)).as(" equal to ").isEqualTo("defaultValue");
		
		mappingData.setType("Custome");
		mappingData.setVariableName(MyPriceConstants.SPEED_LOCAL_ACCESS_PF);
		mappingData.setOffer("Ethernet");
		mappingData.setPath("$..siteName");
		Mockito.when(nexxusJsonUtility.getValue(anyString(), anyString())).thenReturn("10 mbps");
		//assertThat(configAndUpdatePricingUtilFmo.getData(mappingData, inputDesignDetails, requestMap)).as(" equal to ").isEqualTo("10 mbps");
		
		mappingData.setType("List");
		List<String> str = new ArrayList<String>();
		str.add("data1");
		str.add("data2");
		Mockito.when(jsonPathUtil.search(anyString(), anyString(), any())).thenReturn(str);
		configAndUpdatePricingUtilFmo.getData(mappingData, inputDesignDetails, requestMap);
		
		
		List<Object> dataLst=new ArrayList<Object>();
		dataLst.add("gdhd");
		when(jsonPathUtil.search(any(),any(),any())).thenReturn(dataLst);
	
		mappingData.setType("Count");
		mappingData.setPath("$..site##$..deal");
		configAndUpdatePricingUtilFmo.getData(mappingData, inputDesignDetails, requestMap);
	
		mappingData.setType("Count");
		mappingData.setPath("$..site");
		configAndUpdatePricingUtilFmo.getData(mappingData, inputDesignDetails, requestMap);
		
		mappingData.setType("Custome");
		mappingData.setVariableName("abc");
		requestMap.put("abc", "A");
		configAndUpdatePricingUtilFmo.getData(mappingData, inputDesignDetails, requestMap);
		
		mappingData.setType("Custome");
		mappingData.setVariableName("abc");
		mappingData.setDefaultValue("gfh");
		configAndUpdatePricingUtilFmo.getData(mappingData, inputDesignDetails, requestMap);
	
		
		mappingData.setType(null);
		mappingData.setPath("$..zipCode||PLACEHOLDER");
		configAndUpdatePricingUtilFmo.getData(mappingData, inputDesignDetails, requestMap);
		
		mappingData.setPath("$..address##$..address1,$..address12");
		mappingData.setDelimiter("$");
		configAndUpdatePricingUtilFmo.getData(mappingData, inputDesignDetails, requestMap);
		
		
		mappingData.setDataSetName("SALES_MS_PRODCOMP_UDF_ATTR_VAL");
		mappingData.setPath("$..address##$..address1");
		configAndUpdatePricingUtilFmo.getData(mappingData, inputDesignDetails, requestMap);
		
		mappingData.setPath("$..swcClli");
		mappingData.setDataSetName("NX_LOOKUP_DATA|mp_ethernet_accessArchitecture");
		Map<String,NxLookupData> resultMap = new HashMap<String,NxLookupData>();
		NxLookupData lookup = new NxLookupData();
		lookup.setDescription("SWITCHED");
		resultMap.put("SWITCHED", lookup);
		Mockito.when(nexxusJsonUtility.getValue(anyString(), anyString())).thenReturn("SWITCHED");
		Mockito.when(nxMyPriceRepositoryServce.getLookupDataByItemId(anyString())).thenReturn(resultMap);
		configAndUpdatePricingUtilFmo.getData(mappingData, inputDesignDetails, requestMap);
	
	}
	
	@Test
	public void processMultipleJsonPathTest() {
		List<String> str = new ArrayList<String>();
		str.add("data1");
		str.add("data2");
		Mockito.when(jsonPathUtil.search(anyString(), anyString(), any())).thenReturn(str);
		List<String> pathList=new ArrayList<String>();
		pathList.add("$..site");
		pathList.add("$..siteName||$..siteAdd");
		String s="fgh";
		when(nexxusJsonUtility.getValue(any(),any())).thenReturn(s);
		configAndUpdatePricingUtilFmo.processMultipleJsonPath(mappingData, inputDesignDetails, 1l, pathList, ",");
	}
	
	@Test
	public void processOrConditionTest() {
		List<String> str = new ArrayList<String>();
		str.add("data1");
		str.add("data2");
		Mockito.when(jsonPathUtil.search(anyString(), anyString(), any())).thenReturn(str);
		List<String> pathList=new ArrayList<String>();
		pathList.add("$..siteName##$..siteAdd");
		String s="fgh";
		when(nexxusJsonUtility.getValue(any(),any())).thenReturn(s);
		mappingData.setDataSetName("abc");
		configAndUpdatePricingUtilFmo.processOrCondition(mappingData, inputDesignDetails, 1l, pathList);
	}
	
	@Test
	public void processListResultTest() {
		List<String> str = new ArrayList<String>();
		str.add("data1");
		str.add("data2");
		Mockito.when(jsonPathUtil.search(anyString(), anyString(), any())).thenReturn(str);
		mappingData.setDataSetName("abc");
		mappingData.setDelimiter("$");
		configAndUpdatePricingUtilFmo.processListResult(mappingData, inputDesignDetails, 1l);
	}
	
	@Test
	public void getDataFromSalesLookUpTest() {
		List<Object> dataLst=new ArrayList<Object>();
		dataLst.add("gdhd");
		when(lineItemProcessingDao.getDataFromSalesLookUpTbl(any(),any(),any(),any())).thenReturn(dataLst);
		configAndUpdatePricingUtilFmo.getDataFromSalesLookUp("1", 1l, 23l, 2l);
	}
	
	@Test
	public void getProductNameTest() {
		Map<String,List<String>> configProductInfoMap=new HashMap<String, List<String>>();
		configProductInfoMap.put("avpn", Arrays.asList("avpn"));
		configAndUpdatePricingUtilFmo.getProductName(configProductInfoMap, "avpn");
	}
	
	@Test
	public void isProductLineIdMatchForConfigDesignTest() {
		requestMap.put(MyPriceConstants.MP_PRODUCT_LINE_ID,"345");
		configAndUpdatePricingUtilFmo.isProductLineIdMatchForConfigDesign(requestMap, "345", "AVPN");
	}

}
