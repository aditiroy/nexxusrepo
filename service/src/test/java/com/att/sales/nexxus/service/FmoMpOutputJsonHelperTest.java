package com.att.sales.nexxus.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxFmoMPOutputJsonMapping;
import com.att.sales.nexxus.dao.model.NxLineItemLookUpDataModel;
import com.att.sales.nexxus.dao.model.NxLineItemLookUpFieldModel;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.jayway.jsonpath.internal.Path;
@SuppressWarnings({ "rawtypes", "unchecked" })
@ExtendWith(MockitoExtension.class)
public class FmoMpOutputJsonHelperTest {
	
	@Spy
	@InjectMocks
	private FmoMpOutputJsonHelper fmoMpOutputJsonHelper;
	
	private String inputDesignDetails;
	
	private JSONObject data=null;
	
	private Map<String, Object> requestMap;
	
	@Mock
	private NexxusJsonUtility nexusJsonUtility;
	
	@Mock
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	@Mock
	private Path path;
	
	private JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;

	@BeforeEach
	public void init() {
		
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
		data=JacksonUtil.toJsonObject(inputDesignDetails);
		requestMap = new HashMap<String, Object>();
	}
	
	@Test
	public void processMPoutputJsonTest() throws IOException {
		List<NxLineItemLookUpDataModel> inpuLineItemDataLst=new ArrayList<NxLineItemLookUpDataModel>();
		NxLineItemLookUpDataModel np=new NxLineItemLookUpDataModel();
		Set<NxFmoMPOutputJsonMapping> dd=new HashSet<NxFmoMPOutputJsonMapping>();
		NxFmoMPOutputJsonMapping fm=new NxFmoMPOutputJsonMapping();
		fm.setActive("Y");
		fm.setDataSetName("");
		fm.setDefaultValue("");
		fm.setFieldName("nrcBeid");
		fm.setFiledType("Field");
		fm.setGetPath("$..field2Value");
		fm.setGetPathCriteria("");
		fm.setId(1l);
		fm.setOffer("AVPN");
		//fm.setSetPath("$..priceDetails.[?(@.beid==:mrcBeid)].nrcBeid");
		fm.setSetPathCriteria("{\"mrcBeid\":\"LINE_ITEM_LOOKUP_DATA|$..field1Value\"}");
		fm.setSetType("");
		dd.add(fm);
		inpuLineItemDataLst.add(np);
		NxLineItemLookUpFieldModel fieldLookup=new NxLineItemLookUpFieldModel();
		fieldLookup.setMpJsonMapping(dd);
		when(nexusJsonUtility.getValue(any(),any())).thenReturn("SWITCHED");
		fmoMpOutputJsonHelper.processMPoutputJson(inpuLineItemDataLst, data, fieldLookup, requestMap);
	}
	
	@Test
	public void getDataForInsertTest() {
		NxFmoMPOutputJsonMapping fm=new NxFmoMPOutputJsonMapping();
		fm.setActive("Y");
		fm.setDefaultValue("ABC");
		fm.setGetType(MyPriceConstants.IS_DEFAULT);
		String lineItemLookupJsonString="{}";
		fmoMpOutputJsonHelper.getDataForInsert(lineItemLookupJsonString, inputDesignDetails, fm, requestMap);
	}
	
	@Test
	public void getDataForInsertTest2() {
		NxFmoMPOutputJsonMapping fm=new NxFmoMPOutputJsonMapping();
		fm.setActive("Y");
		fm.setGetType("Custome");
		String lineItemLookupJsonString="{}";
		fmoMpOutputJsonHelper.getDataForInsert(lineItemLookupJsonString, inputDesignDetails, fm, requestMap);
	}
	
	@Test
	public void getDataForInsertTest3() {
		NxFmoMPOutputJsonMapping fm=new NxFmoMPOutputJsonMapping();
		fm.setActive("Y");
		fm.setDataSetName("");
		fm.setDefaultValue("");
		fm.setFieldName("nrcBeid");
		fm.setFiledType("Field");
		fm.setGetPath("$..field2Value");
		fm.setGetPathCriteria("");
		fm.setId(1l);
		fm.setOffer("AVPN");
		String lineItemLookupJsonString="{}";
		fmoMpOutputJsonHelper.getDataForInsert(lineItemLookupJsonString, inputDesignDetails, fm, requestMap);
	}
	
	@Test
	public void getDataForInsertTest4() {
		NxFmoMPOutputJsonMapping fm=new NxFmoMPOutputJsonMapping();
		fm.setActive("Y");
		fm.setDataSetName(MyPriceConstants.NX_LOOKUP_SOURCE);
		fm.setDefaultValue("");
		fm.setFieldName("nrcBeid");
		fm.setFiledType("Field");
		fm.setGetPath("$..field2Value");
		fm.setGetPathCriteria("");
		fm.setId(1l);
		fm.setOffer("AVPN");
		fm.setGetType(MyPriceConstants.REQUEST_SITE_DATA_SOURCE);
		String lineItemLookupJsonString="{}";
		when(nexusJsonUtility.getValue(any(),any())).thenReturn("SWITCHED");
		fmoMpOutputJsonHelper.getDataForInsert(lineItemLookupJsonString, inputDesignDetails, fm, requestMap);
	}
	
	@Test
	public void getDataForInsertTest5() {
		NxFmoMPOutputJsonMapping fm=new NxFmoMPOutputJsonMapping();
		fm.setActive("Y");
		fm.setDataSetName(MyPriceConstants.NX_LOOKUP_SOURCE);
		fm.setDefaultValue("");
		fm.setFieldName("nrcBeid");
		fm.setFiledType("Field");
		fm.setGetPath("$..field2Value");
		fm.setGetPathCriteria("");
		fm.setId(1l);
		fm.setOffer("AVPN");
		fm.setGetType(MyPriceConstants.REQUEST_SITE_DATA_SOURCE);
		String lineItemLookupJsonString="{}";
		when(nexusJsonUtility.getValue(any(),any())).thenReturn("SWITCHED");
		Map<String,NxLookupData> resultMap=new HashMap<String, NxLookupData>();
		resultMap.put("SWITCHED", new NxLookupData());
		when(nxMyPriceRepositoryServce.getLookupDataByItemId(any())).thenReturn(resultMap);
		fmoMpOutputJsonHelper.getDataForInsert(lineItemLookupJsonString, inputDesignDetails, fm, requestMap);
	}
	
	@Test
	public void handleSetProcessTest() {
		NxFmoMPOutputJsonMapping fm=new NxFmoMPOutputJsonMapping();
		fm.setActive("Y");
		fm.setGetType("Custome");
		String lineItemLookupJsonString="{}";
		Object s="";
		fmoMpOutputJsonHelper.handleSetProcess(fm, s, inputDesignDetails, lineItemLookupJsonString, requestMap, data);
	}
	
	
	@Test
	public void handleSetProcessTest1() {
		NxFmoMPOutputJsonMapping fm=new NxFmoMPOutputJsonMapping();
		fm.setActive("Y");
		fm.setSetPath("$..site##$$..deal");
		fm.setSetPathCriteria("abc");
		String lineItemLookupJsonString="{}";
		Object s="";
		 Map  setPathCriteria=new HashMap();
		 setPathCriteria.put("s", "s");
		 when(nexusJsonUtility.convertStringJsonToMap(any())).thenReturn(setPathCriteria);
		fmoMpOutputJsonHelper.handleSetProcess(fm, s, inputDesignDetails, lineItemLookupJsonString, requestMap, data);
	}
	
	@Test
	public void getPath() {
		fmoMpOutputJsonHelper.getPath("$..site", "site");
	}
	
	@Test
	public void getPath1() {
		fmoMpOutputJsonHelper.getPath("$.site", "site");
	}
	
	@Test
	public void createSetPathTest() {
		String lineItemLookupJsonString="{}";
		NxFmoMPOutputJsonMapping fm=new NxFmoMPOutputJsonMapping();
		fm.setSetPathCriteria("s");
		fm.setGetPathCriteria("fg");
		 Map  setPathCriteria=new HashMap();
		 setPathCriteria.put("s", "s");
		 when(nexusJsonUtility.convertStringJsonToMap(any())).thenReturn(setPathCriteria);
		 when(nexusJsonUtility.getValue(any(),any())).thenReturn("SWITCHED");
		fmoMpOutputJsonHelper.creategGetPath("$..site", fm, inputDesignDetails, lineItemLookupJsonString);
	}
	
	@Test
	public void createSetPathTest2() {
		String lineItemLookupJsonString="{}";
		NxFmoMPOutputJsonMapping fm=new NxFmoMPOutputJsonMapping();
		fm.setSetPathCriteria("s");
		fm.setGetPathCriteria("fg");
		 Map  setPathCriteria=new HashMap();
		 setPathCriteria.put("s", null);
		 when(nexusJsonUtility.convertStringJsonToMap(any())).thenReturn(setPathCriteria);
		fmoMpOutputJsonHelper.creategGetPath("$..site", fm, inputDesignDetails, lineItemLookupJsonString);
	}
	
	@Test
	public void getDataForPathTest() {
		String lineItemLookupJsonString="{}";
		when(nexusJsonUtility.getValue(any(),any())).thenReturn("SWITCHED");
		fmoMpOutputJsonHelper.getDataForPath(MyPriceConstants.LINE_ITEM_DATA_SOURCE, inputDesignDetails, lineItemLookupJsonString);
	}
	
	@Test
	public void getDataForPathTest2() {
		String lineItemLookupJsonString="{}";
		when(nexusJsonUtility.getValue(any(),any())).thenReturn("SWITCHED");
		fmoMpOutputJsonHelper.getDataForPath(MyPriceConstants.REQUEST_SITE_DATA_SOURCE, inputDesignDetails, lineItemLookupJsonString);
	}
	
	@Test
	public void getDataForPathTest3() {
		String lineItemLookupJsonString="{}";
		fmoMpOutputJsonHelper.getDataForPath(null, inputDesignDetails, lineItemLookupJsonString);
	}

	@Test
	public void testinsertOrUpdateData() {
		//case1
		List<Object> result = new ArrayList<>();
		when(nexusJsonUtility.getValueLst(any(),anyString())).thenReturn(result);
		String value = "{\"site\":\"data\"}";
		JSONObject siteObj = JacksonUtil.toJsonObject(value);
		fmoMpOutputJsonHelper.insertOrUpdateData(siteObj,"setPath","data","Array","fieldName");
		
		//case 2
		result.add("data");
		when(nexusJsonUtility.getValueLst(any(),anyString())).thenReturn(result);
		fmoMpOutputJsonHelper.insertOrUpdateData(siteObj,"setPath","data","Array","fieldName");

		//case3
		fmoMpOutputJsonHelper.insertOrUpdateData(siteObj,"setPath","data","Object","fieldName");
		
		//case4
		result.clear();
		when(nexusJsonUtility.getValueLst(any(),anyString())).thenReturn(result);
		fmoMpOutputJsonHelper.insertOrUpdateData(siteObj,"setPath","data","Object","fieldName");

		//case 5
		fmoMpOutputJsonHelper.insertOrUpdateData(siteObj,"setPath","data","Field","fieldName");
	}
	

	@Test
	public void handleSetProcessTest2() {
		NxFmoMPOutputJsonMapping fm=new NxFmoMPOutputJsonMapping();
		fm.setActive("Y");
		fm.setSetPath("setPath");
		fm.setFiledType("field");
		fm.setFieldName("fieldName");
		String lineItemLookupJsonString="{}";
		String value = "{\"site\":\"data\"}";
		JSONObject siteObj = JacksonUtil.toJsonObject(value);
		fmoMpOutputJsonHelper.handleSetProcess(fm, siteObj, inputDesignDetails, lineItemLookupJsonString, requestMap, data);
	}
}
