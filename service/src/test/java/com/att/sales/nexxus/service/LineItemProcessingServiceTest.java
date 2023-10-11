package com.att.sales.nexxus.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.constant.FmoConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.OutputFileConstants;
import com.att.sales.nexxus.dao.model.NxKeyFieldPathModel;
import com.att.sales.nexxus.dao.model.NxLineItemLookUpDataModel;
import com.att.sales.nexxus.dao.model.NxLineItemLookUpFieldModel;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxOutputProductMappingModel;
import com.att.sales.nexxus.myprice.transaction.dao.service.NxMpRepositoryService;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdateProcessingFmoService;
import com.att.sales.nexxus.output.entity.NxAvpnOutputBean;
import com.att.sales.nexxus.output.entity.NxOutputBean;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;


/**
 * The Class LineItemProcessingServiceTest.
 */
@ExtendWith(MockitoExtension.class)
public class LineItemProcessingServiceTest {

	@Spy
	@InjectMocks
	LineItemProcessingService lineItemProcessingService;
	@Mock
	 Map<String,Set<String>> falloutDataMap=new HashMap<>();
	@Mock
	StringUtils stringutil;
	@Mock
	private FmoProcessingRepoService repositoryService;
	@Mock
	CollectionUtils utils;
	@Mock
	private FmoMpOutputJsonHelper fmoMpOutputJsonHelper;
	
	@Mock
	private NxMpRepositoryService nxMpRepositoryService;
	
	@Mock
	private ConfigAndUpdateProcessingFmoService configAndUpdateProcessingFmoService;
	@Mock
	JSONObject inputObj;
	
	@Mock
	private NexxusJsonUtility nexusJsonUtility;
	
	Map<String, Object> paramMap=new HashMap<String, Object>();
	
	private JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;

	JSONObject siteObj=JacksonUtil.toJsonObject("{\"country\":\"US\",\"nxSiteId\":10204,\"zipCode\":\"95136\",\"address\":null,\"referenceOfferId\":4,"
			+ "\"isLineItemPicked\":\"Y\",\"city\":\"san jose\",\"address2\":null,\"address1\":\"20 shenado pl\",\"postalCode\":null,\"siteName\":\"test\","
			+ "\"siteNpanxx\":\"408225\",\"regionCode\":null,\"swcClli\":\"SNJSCA13\",\"design\":[{\"portProtocol\":\"PPP\",\"accessSpeedUdfAttrId\":30549,"
			+ "\"accessDetails\":{\"supplierName\":null,\"npanxx\":null,\"nrcListRate\":null,\"serialNumber\":null,\"tokenId\":null,\"mrcListRate\":null,"
			+ "\"respAccessInterconnect\":null,\"dqid\":null,\"physicalInterface\":null,\"portId\":null,\"speed\":null,\"quoteId\":null,\"iglooMaxMrcDiscount\":null,"
			+ "\"respSpeed\":null,\"popClli\":null,\"respSupplierName\":null,\"currencyCode\":null,\"respPopClli\":null},\"sitePopCilli\":null,\"portId\":9257029,"
			+ "\"interface\":null,\"referenceSiteId\":9958922,\"lac\":null,\"accessSpeed\":\"DS3\",\"accessType\":\"Private Line\",\"accessArchitecture\":null,"
			+ "\"accessTypeUdfAttrId\":30155,\"categoryLocalAccess\":null,\"portSpeed\":\"45M\",\"priceDetails\":[{\"country\":\"US\",\"componentType\":\"Port\","
			+ "\"priceScenarioId\":99999925473,\"quantity\":\"1\",\"priceType\":\"accessRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":0.0,\"frequency\":"
			+ "\"MRC\",\"rdsPriceType\":\"ACCESS\",\"beid\":null,\"componentParentId\":9958922,\"productRateId\":2929,\"reqPriceType\":null,\"term\":60,"
			+ "\"localListPrice\":16852.78,\"referencePortId\":9257029},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999925473,\"secondaryKeys\":"
			+ "\"#FCC#MPLS Port#Flat Rate#45M#T3#FR, ATM, IP#VPN Transport Connection#per port#18013#18030#United States#US#USA\",\"quantity\":\"1\",\"priceType\":"
			+ "\"portRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":55.56,\"frequency\":\"MRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18013\",\"nrcBeid\":"
			+ "\"18030\",\"componentParentId\":9958922,\"productRateId\":906,\"reqPriceType\":null,\"term\":60,\"elementType\":\"Port\",\"uniqueId\":"
			+ "\"#MPLS Port#MPLS Port - 45M#FR, ATM, IP#45 Mbps#T3#VPN Transport#Connection#Each\",\"localListPrice\":1990.0,\"referencePortId\":9257029},"
			+ "{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999925473,\"quantity\":\"1\",\"priceType\":\"portNRCRateId\",\"localCurrency\":\"USD\","
			+ "\"icbDesiredDiscPerc\":88.88,\"frequency\":\"NRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18030\",\"componentParentId\":9958922,\"productRateId\":930,"
			+ "\"reqPriceType\":\"Fixed\",\"term\":60,\"localListPrice\":1000.0,\"referencePortId\":9257029},{\"country\":\"US\",\"componentType\":\"Port\","
			+ "\"priceScenarioId\":99999925473,\"secondaryKeys\":\"#FCC#CoS Package#Multimedia Standard Svc#45M#N\\/A#N\\/A#VPN Transport COS Package#per port#18292#18425#United States#US#USA\","
			+ "\"quantity\":\"1\",\"priceType\":\"cosRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":55.56,\"frequency\":\"MRC\",\"rdsPriceType\":\"PORT\","
			+ "\"beid\":\"18292\",\"nrcBeid\":\"18425\",\"componentParentId\":9958922,\"productRateId\":1330,\"reqPriceType\":null,\"term\":60,\"elementType\":"
			+ "\"PortFeature\",\"uniqueId\":\"#CoS Package Multimedia Standard#CoS Package Multimedia Standard Svc - 45M#FR, ATM, IP#45 Mbps#T3#VPN Transport#COS Package#Port\","
			+ "\"localListPrice\":0.0,\"referencePortId\":9257029},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999925473,\"quantity\":\"1\","
			+ "\"priceType\":\"cosNRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":88.88,\"frequency\":\"NRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18425\","
			+ "\"componentParentId\":9958922,\"productRateId\":1498,\"reqPriceType\":null,\"term\":60,\"localListPrice\":0.0,\"referencePortId\":9257029}],\"accessTailTechnology\":null,"
			+ "\"mileage\":null,\"siteType\":null}],\"siteId\":9958922,\"state\":\"CA\",\"customerLocationClli\":null}");
	
	
	String mpFmoFlow="N";
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getLineItemDataTest1() throws NoSuchMethodException, SecurityException, IllegalAccessException,
	IllegalArgumentException, InvocationTargetException {
		Map<String,String> keyPathMap=new HashMap<>();
		keyPathMap.put("sol", "$..is");
		Method method = lineItemProcessingService.getClass().getDeclaredMethod("getKeyPathDataMap");
		method.setAccessible(true);
		keyPathMap=(Map) method.invoke(lineItemProcessingService);
		JSONObject jsonObject=new JSONObject();
		
		JSONArray siteArray=new JSONArray();
		JSONObject siterObj=new JSONObject();
		siterObj.put(FmoConstants.COUNTRY_CD, "US");
		siteArray.add(siterObj);
		
		JSONArray offerArray=new JSONArray();
		JSONObject offerObj=new JSONObject();
		offerObj.put(FmoConstants.OFFER_ID, 1l);
		offerObj.put(FmoConstants.SITE_ATR, siteArray);
		offerArray.add(offerObj);
		
		JSONObject solutionObj=new JSONObject();
		solutionObj.put(FmoConstants.OFFER_ATR, offerArray);
		jsonObject.put(FmoConstants.SOLUTION_ATR, solutionObj);
		List<NxLineItemLookUpFieldModel> resultLst=new ArrayList<>();
		NxLineItemLookUpFieldModel nl=new NxLineItemLookUpFieldModel();
		nl.setKeyFieldName(FmoConstants.BEID_ID_COUNTRY_CRITERIA);
		nl.setOfferName("AVPN");
		resultLst.add(nl);
		doReturn(mpFmoFlow).when(lineItemProcessingService).getMpFmoFlow();
		String offerName="AVPN";
		when(nxMpRepositoryService.getOfferNameByOfferId(anyInt())).thenReturn(offerName);
		when(repositoryService.getNxLineItemFieldDataByOfferId(any(),any())).thenReturn(resultLst);
		List<NxLineItemLookUpDataModel> resultLst1= new ArrayList<>();
		NxLineItemLookUpDataModel  nx=new NxLineItemLookUpDataModel();
		nx.setOfferId(1l);
		resultLst1.add(nx);
		Map<String, Object> paramMap=new HashMap<String, Object>();
	//	when(repositoryService.getListItemDataByBeId(any(),any(),any(),any())).thenReturn(resultLst1);
		lineItemProcessingService.getLineItemData(jsonObject,paramMap);
	}
	
	
	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	@Test
	public void getLineItemDataTest2() throws NoSuchMethodException, SecurityException, IllegalAccessException,
	IllegalArgumentException, InvocationTargetException {
		Map<String,String> keyPathMap=new HashMap<>();
		keyPathMap.put("sol", "$..is");
		Method method = lineItemProcessingService.getClass().getDeclaredMethod("getKeyPathDataMap");
		method.setAccessible(true);
		keyPathMap=(Map) method.invoke(lineItemProcessingService);
		JSONObject jsonObject=new JSONObject();
		
		JSONArray siteArray=new JSONArray();
		JSONObject siterObj=new JSONObject();
		siterObj.put(FmoConstants.COUNTRY_CD, "US");
		siteArray.add(siterObj);
		
		JSONArray offerArray=new JSONArray();
		JSONObject offerObj=new JSONObject();
		offerObj.put(FmoConstants.OFFER_ID, 1l);
		offerObj.put(FmoConstants.SITE_ATR, siteArray);
		offerArray.add(offerObj);
		
		JSONObject solutionObj=new JSONObject();
		solutionObj.put(FmoConstants.OFFER_ATR, offerArray);
		jsonObject.put(FmoConstants.SOLUTION_ATR, solutionObj);
		List<NxLineItemLookUpFieldModel> resultLst=new ArrayList<>();
		NxLineItemLookUpFieldModel nl=new NxLineItemLookUpFieldModel();
		nl.setKeyFieldName(FmoConstants.STATE_COUNTRY_CRITERIA);
		nl.setOfferName("AVPN");
		resultLst.add(nl);
		doReturn(mpFmoFlow).when(lineItemProcessingService).getMpFmoFlow();
		String offerName="AVPN";
		when(nxMpRepositoryService.getOfferNameByOfferId(anyInt())).thenReturn(offerName);
		when(repositoryService.getNxLineItemFieldDataByOfferId(any(),any())).thenReturn(resultLst);
		Map<String, Object> paramMap=new HashMap<String, Object>();
		lineItemProcessingService.getLineItemData(jsonObject,paramMap);
	}
	
	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	@Test
	public void getLineItemDataTestElse() throws NoSuchMethodException, SecurityException, IllegalAccessException,
	IllegalArgumentException, InvocationTargetException {
		Map<String,String> keyPathMap=new HashMap<>();
		keyPathMap.put("sol", "$..is");
		Method method = lineItemProcessingService.getClass().getDeclaredMethod("getKeyPathDataMap");
		method.setAccessible(true);
		keyPathMap=(Map) method.invoke(lineItemProcessingService);
		JSONObject jsonObject=new JSONObject();
		
		JSONArray siteArray=new JSONArray();
		JSONObject siterObj=new JSONObject();
		siterObj.put(FmoConstants.COUNTRY_CD, "UK");
		siteArray.add(siterObj);
		
		JSONArray offerArray=new JSONArray();
		JSONObject offerObj=new JSONObject();
		offerObj.put(FmoConstants.OFFER_ID, 1l);
		offerObj.put(FmoConstants.SITE_ATR, siteArray);
		offerArray.add(offerObj);
		
		JSONObject solutionObj=new JSONObject();
		solutionObj.put(FmoConstants.OFFER_ATR, offerArray);
		jsonObject.put(FmoConstants.SOLUTION_ATR, solutionObj);
		List<NxLineItemLookUpFieldModel> resultLst=new ArrayList<>();
		NxLineItemLookUpFieldModel nl=new NxLineItemLookUpFieldModel();
		nl.setKeyFieldName("RCRATEID##COUNTRY_CD");
		nl.setOfferName("AVPN");
		resultLst.add(nl);
		doReturn(mpFmoFlow).when(lineItemProcessingService).getMpFmoFlow();
		String offerName="AVPN";
		when(nxMpRepositoryService.getOfferNameByOfferId(anyInt())).thenReturn(offerName);
		when(repositoryService.getNxLineItemFieldDataByOfferId(any(),any())).thenReturn(resultLst);
		Map<String, Object> paramMap=new HashMap<String, Object>();
		lineItemProcessingService.getLineItemData(jsonObject,paramMap);
	}
	
	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	@Test
	public void getLineItemDataTestElse1() throws NoSuchMethodException, SecurityException, IllegalAccessException,
	IllegalArgumentException, InvocationTargetException {
		Map<String,String> keyPathMap=new HashMap<>();
		keyPathMap.put("sol", "$..is");
		Method method = lineItemProcessingService.getClass().getDeclaredMethod("getKeyPathDataMap");
		method.setAccessible(true);
		keyPathMap=(Map) method.invoke(lineItemProcessingService);
		JSONObject jsonObject=new JSONObject();
		
		JSONArray siteArray=new JSONArray();
		JSONObject siterObj=new JSONObject();
		siterObj.put(FmoConstants.COUNTRY_CD, "UK");
		siteArray.add(siterObj);
		
		JSONArray offerArray=new JSONArray();
		JSONObject offerObj=new JSONObject();
		offerObj.put(FmoConstants.OFFER_ID, 1l);
		offerObj.put(FmoConstants.SITE_ATR, siteArray);
		offerArray.add(offerObj);
		
		JSONObject solutionObj=new JSONObject();
		solutionObj.put(FmoConstants.OFFER_ATR, offerArray);
		jsonObject.put(FmoConstants.SOLUTION_ATR, solutionObj);
		List<NxLineItemLookUpFieldModel> resultLst=new ArrayList<>();
		NxLineItemLookUpFieldModel nl=new NxLineItemLookUpFieldModel();
		nl.setKeyFieldName("SiteCountry##Product##Currency##Technology");
		nl.setOfferName("AVPN");
		resultLst.add(nl);
		doReturn(mpFmoFlow).when(lineItemProcessingService).getMpFmoFlow();
		String offerName="AVPN";
		when(nxMpRepositoryService.getOfferNameByOfferId(anyInt())).thenReturn(offerName);
		when(repositoryService.getNxLineItemFieldDataByOfferId(any(),any())).thenReturn(resultLst);
		Map<String, Object> paramMap=new HashMap<String, Object>();
		lineItemProcessingService.getLineItemData(jsonObject,paramMap);
	}
	
	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	@Test
	public void getLineItemDataTestElse2() throws NoSuchMethodException, SecurityException, IllegalAccessException,
	IllegalArgumentException, InvocationTargetException {
		Map<String,String> keyPathMap=new HashMap<>();
		keyPathMap.put("sol", "$..is");
		Method method = lineItemProcessingService.getClass().getDeclaredMethod("getKeyPathDataMap");
		method.setAccessible(true);
		keyPathMap=(Map) method.invoke(lineItemProcessingService);
		JSONObject jsonObject=new JSONObject();
		
		JSONArray siteArray=new JSONArray();
		JSONObject siterObj=new JSONObject();
		siterObj.put(FmoConstants.COUNTRY_CD, "UK");
		siteArray.add(siterObj);
		
		JSONArray offerArray=new JSONArray();
		JSONObject offerObj=new JSONObject();
		offerObj.put(FmoConstants.OFFER_ID, 1l);
		offerObj.put(FmoConstants.SITE_ATR, siteArray);
		offerArray.add(offerObj);
		
		JSONObject solutionObj=new JSONObject();
		solutionObj.put(FmoConstants.OFFER_ATR, offerArray);
		jsonObject.put(FmoConstants.SOLUTION_ATR, solutionObj);
		List<NxLineItemLookUpFieldModel> resultLst=new ArrayList<>();
		NxLineItemLookUpFieldModel nl=new NxLineItemLookUpFieldModel();
		nl.setKeyFieldName("COUNTRY_CD");
		nl.setOfferName("AVPN");
		resultLst.add(nl);
		doReturn(mpFmoFlow).when(lineItemProcessingService).getMpFmoFlow();
		String offerName="AVPN";
		when(nxMpRepositoryService.getOfferNameByOfferId(anyInt())).thenReturn(offerName);
		when(repositoryService.getNxLineItemFieldDataByOfferId(any(),any())).thenReturn(resultLst);
		Map<String, Object> paramMap=new HashMap<String, Object>();
		lineItemProcessingService.getLineItemData(jsonObject,paramMap);
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getLineItemDataTest5() throws NoSuchMethodException, SecurityException, IllegalAccessException,
	IllegalArgumentException, InvocationTargetException {
		Map<String,String> keyPathMap=new HashMap<>();
		keyPathMap.put("sol", "$..is");
		Method method = lineItemProcessingService.getClass().getDeclaredMethod("getKeyPathDataMap");
		method.setAccessible(true);
		keyPathMap=(Map) method.invoke(lineItemProcessingService);
		JSONObject jsonObject=new JSONObject();
		
		JSONArray siteArray=new JSONArray();
		JSONObject siterObj=new JSONObject();
		siterObj.put(FmoConstants.COUNTRY_CD, "US");
		siteArray.add(siterObj);
		
		JSONArray offerArray=new JSONArray();
		JSONObject offerObj=new JSONObject();
		offerObj.put(FmoConstants.OFFER_ID, 1l);
		offerObj.put(FmoConstants.SITE_ATR, siteArray);
		offerArray.add(offerObj);
		
		JSONObject solutionObj=new JSONObject();
		solutionObj.put(FmoConstants.OFFER_ATR, offerArray);
		jsonObject.put(FmoConstants.SOLUTION_ATR, solutionObj);
		List<NxLineItemLookUpFieldModel> resultLst=new ArrayList<>();
		NxLineItemLookUpFieldModel nl=new NxLineItemLookUpFieldModel();
		nl.setKeyFieldName(FmoConstants.ETHERNET_ACCESS_CRITERIA);
		nl.setOfferName("AVPN");
		resultLst.add(nl);
		doReturn(mpFmoFlow).when(lineItemProcessingService).getMpFmoFlow();
		String offerName="AVPN";
		when(nxMpRepositoryService.getOfferNameByOfferId(anyInt())).thenReturn(offerName);
		when(repositoryService.getNxLineItemFieldDataByOfferId(any(),any())).thenReturn(resultLst);
		Map<String, Object> paramMap=new HashMap<String, Object>();
		lineItemProcessingService.getLineItemData(jsonObject,paramMap);
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getLineItemDataTest4() throws NoSuchMethodException, SecurityException, IllegalAccessException,
	IllegalArgumentException, InvocationTargetException {
		Map<String,String> keyPathMap=new HashMap<>();
		keyPathMap.put("sol", "$..is");
		Method method = lineItemProcessingService.getClass().getDeclaredMethod("getKeyPathDataMap");
		method.setAccessible(true);
		keyPathMap=(Map) method.invoke(lineItemProcessingService);
		JSONObject jsonObject=new JSONObject();
		
		JSONArray siteArray=new JSONArray();
		JSONObject siterObj=new JSONObject();
		siteArray.add(siterObj);
		
		JSONArray offerArray=new JSONArray();
		JSONObject offerObj=new JSONObject();
		offerObj.put(FmoConstants.OFFER_ID, 1l);
		offerObj.put(FmoConstants.SITE_ATR, siteArray);
		offerArray.add(offerObj);
		
		JSONObject solutionObj=new JSONObject();
		solutionObj.put(FmoConstants.OFFER_ATR, offerArray);
		jsonObject.put(FmoConstants.SOLUTION_ATR, solutionObj);
		List<NxLineItemLookUpFieldModel> resultLst=new ArrayList<>();
		NxLineItemLookUpFieldModel nl=new NxLineItemLookUpFieldModel();
		nl.setKeyFieldName(FmoConstants.RATE_ID_COUNTRY_CRITERIA);
		nl.setOfferName("AVPN");
		resultLst.add(nl);
		doReturn(mpFmoFlow).when(lineItemProcessingService).getMpFmoFlow();
		String offerName="AVPN";
		when(nxMpRepositoryService.getOfferNameByOfferId(anyInt())).thenReturn(offerName);
		when(repositoryService.getNxLineItemFieldDataByOfferId(any(),any())).thenReturn(resultLst);
		Map<String, Object> paramMap=new HashMap<String, Object>();
		lineItemProcessingService.getLineItemData(jsonObject,paramMap);
	}
	
	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	@Test
	public void getLineItemDataTest3() throws NoSuchMethodException, SecurityException, IllegalAccessException,
	IllegalArgumentException, InvocationTargetException {
		Map<String,String> keyPathMap=new HashMap<>();
		keyPathMap.put("sol", "$..is");
		Method method = lineItemProcessingService.getClass().getDeclaredMethod("getKeyPathDataMap");
		method.setAccessible(true);
		keyPathMap=(Map) method.invoke(lineItemProcessingService);
		JSONObject jsonObject=new JSONObject();
		
		JSONArray siteArray=new JSONArray();
		JSONObject siterObj=new JSONObject();
		siteArray.add(siterObj);
		
		JSONArray offerArray=new JSONArray();
		JSONObject offerObj=new JSONObject();
		offerObj.put(FmoConstants.OFFER_ID, 1l);
		offerObj.put(FmoConstants.SITE_ATR, siteArray);
		offerArray.add(offerObj);
		
		JSONObject solutionObj=new JSONObject();
		solutionObj.put(FmoConstants.OFFER_ATR, offerArray);
		jsonObject.put(FmoConstants.SOLUTION_ATR, solutionObj);
		List<NxLineItemLookUpFieldModel> resultLst=new ArrayList<>();
		NxLineItemLookUpFieldModel nl=new NxLineItemLookUpFieldModel();
		nl.setKeyFieldName(FmoConstants.BEID);
		nl.setOfferName("ADI");
		resultLst.add(nl);
		doReturn(mpFmoFlow).when(lineItemProcessingService).getMpFmoFlow();
		String offerName="AVPN";
		when(nxMpRepositoryService.getOfferNameByOfferId(anyInt())).thenReturn(offerName);
		when(repositoryService.getNxLineItemFieldDataByOfferId(any(),any())).thenReturn(resultLst);
		Map<String, Object> paramMap=new HashMap<String, Object>();
		lineItemProcessingService.getLineItemData(jsonObject,paramMap);
	}
	
	@Test
	public void getLineItemDattest3() {
		Map<String, Object> paramMap=new HashMap<String, Object>();
		lineItemProcessingService.getLineItemData(null,paramMap);
	}
	
	@Test
	public void getKeyPathDataMapTest() {
		List<NxKeyFieldPathModel> resultLst=new ArrayList<>();
		NxKeyFieldPathModel nf1 =new NxKeyFieldPathModel();
		nf1.setJsonPath("$..");
		nf1.setFieldName("GG");
		resultLst.add(nf1);
		when(repositoryService.loadNexxusKeyPathData()).thenReturn(resultLst);
		lineItemProcessingService.getKeyPathDataMap();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void fetchLineItemIdOnBeIdTest1() {
		NxLineItemLookUpFieldModel data=new NxLineItemLookUpFieldModel();
		Map<String, Set<String>> d=new HashMap<String, Set<String>>();
		paramMap.put(FmoConstants.FALLOUT_MAP,d);
		Set<NxKeyFieldPathModel> k=new HashSet<NxKeyFieldPathModel>();
		NxKeyFieldPathModel nk=new NxKeyFieldPathModel();
		nk.setId(1l);
		nk.setJsonPath("$.site");
		k.add(nk);
		data.setKeyFieldMapping(k);
		data.setOfferId(1l);
		data.setKeyFieldMapping(k);
		Map<String,String> keyPath=new HashMap<>();
		keyPath.put("sol", "$..is");
		NxOutputBean nxOutputBean=new NxOutputBean();
		JSONObject siteObj=new JSONObject();
		JSONArray designLst=new JSONArray();
		siteObj.put(FmoConstants.DESIGN_ATR, designLst);
		List<NxLineItemLookUpDataModel> resultLst=new ArrayList<>();
		NxLineItemLookUpDataModel nl=new NxLineItemLookUpDataModel();
		NxOutputProductMappingModel nexusOutputMapping=new NxOutputProductMappingModel();
		nexusOutputMapping.setTabName(OutputFileConstants.AVPN_TAB);
		nl.setNexusOutputMapping(nexusOutputMapping);
		resultLst.add(nl);
		//Mockito.when(siteObj.get(FmoConstants.DESIGN_ATR)).thenReturn(designLst);
		when(repositoryService.getLineItemData(any(),any(),any(),any())).thenReturn(resultLst);
		String inputValue="AS";
		when(nexusJsonUtility.convertListToCsvWithQuote(any())).thenReturn(inputValue);
		lineItemProcessingService.fetchLineItemIdOnBeId(data, keyPath, nxOutputBean, siteObj,paramMap);
	}
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void fetchLineItemIdOnBeIdTest2() {
		NxLineItemLookUpFieldModel data=new NxLineItemLookUpFieldModel();
		Map<String, Set<String>> d=new HashMap<String, Set<String>>();
		paramMap.put(FmoConstants.FALLOUT_MAP,d);
		Set<NxKeyFieldPathModel> k=new HashSet<NxKeyFieldPathModel>();
		NxKeyFieldPathModel nk=new NxKeyFieldPathModel();
		nk.setId(1l);
		nk.setJsonPath("$.site");
		k.add(nk);
		data.setKeyFieldMapping(k);
		data.setOfferId(1l);
		Map<String,String> keyPath=new HashMap<>();
		keyPath.put("sol", "$..is");
		NxOutputBean nxOutputBean=new NxOutputBean();
		JSONObject siteObj=new JSONObject();
		JSONArray designLst=new JSONArray();
		siteObj.put(FmoConstants.DESIGN_ATR, designLst);
		List<NxLineItemLookUpDataModel> resultLst=new ArrayList<>();
		NxLineItemLookUpDataModel nl=new NxLineItemLookUpDataModel();
		NxOutputProductMappingModel nexusOutputMapping=new NxOutputProductMappingModel();
		nexusOutputMapping.setTabName(OutputFileConstants.AVPN_INTERNATIONAL_TAB);
		nl.setNexusOutputMapping(nexusOutputMapping);
		resultLst.add(nl);
		when(repositoryService.getLineItemData(any(),any(),any(),any())).thenReturn(resultLst);
		String inputValue="AS";
		when(nexusJsonUtility.convertListToCsvWithQuote(any())).thenReturn(inputValue);
		lineItemProcessingService.fetchLineItemIdOnBeId(data, keyPath, nxOutputBean, siteObj,paramMap);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void fetchLineItemIdOnBeIdTest3() {
		NxLineItemLookUpFieldModel data=new NxLineItemLookUpFieldModel();
		Map<String, Set<String>> d=new HashMap<String, Set<String>>();
		paramMap.put(FmoConstants.FALLOUT_MAP,d);
		Set<NxKeyFieldPathModel> k=new HashSet<NxKeyFieldPathModel>();
		NxKeyFieldPathModel nk=new NxKeyFieldPathModel();
		nk.setId(1l);
		nk.setJsonPath("$.site");
		k.add(nk);
		data.setKeyFieldMapping(k);
		data.setOfferId(1l);
		Map<String,String> keyPath=new HashMap<>();
		keyPath.put("sol", "$..is");
		NxOutputBean nxOutputBean=new NxOutputBean();
		JSONObject siteObj=new JSONObject();
		JSONArray designLst=new JSONArray();
		siteObj.put(FmoConstants.DESIGN_ATR, designLst);
		List<NxLineItemLookUpDataModel> resultLst=new ArrayList<>();
		NxLineItemLookUpDataModel nl=new NxLineItemLookUpDataModel();
		NxOutputProductMappingModel nexusOutputMapping=new NxOutputProductMappingModel();
		nexusOutputMapping.setTabName(OutputFileConstants.ETHERNET_ACCESS_TAB);
		nl.setNexusOutputMapping(nexusOutputMapping);
		resultLst.add(nl);
		when(repositoryService.getLineItemData(any(),any(),any(),any())).thenReturn(resultLst);
		String inputValue="AS";
		when(nexusJsonUtility.convertListToCsvWithQuote(any())).thenReturn(inputValue);
		lineItemProcessingService.fetchLineItemIdOnBeId(data, keyPath, nxOutputBean, siteObj,paramMap);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void fetchLineItemIdOnBeIdTest4() {
		NxLineItemLookUpFieldModel data=new NxLineItemLookUpFieldModel();
		Map<String, Set<String>> d=new HashMap<String, Set<String>>();
		paramMap.put(FmoConstants.FALLOUT_MAP,d);
		Set<NxKeyFieldPathModel> k=new HashSet<NxKeyFieldPathModel>();
		NxKeyFieldPathModel nk=new NxKeyFieldPathModel();
		nk.setId(1l);
		nk.setJsonPath("$.site");
		k.add(nk);
		data.setKeyFieldMapping(k);
		data.setOfferId(1l);
		Map<String,String> keyPath=new HashMap<>();
		keyPath.put("sol", "$..is");
		NxOutputBean nxOutputBean=new NxOutputBean();
		JSONObject siteObj=new JSONObject();
		JSONArray designLst=new JSONArray();
		siteObj.put(FmoConstants.DESIGN_ATR, designLst);
		List<NxLineItemLookUpDataModel> resultLst=new ArrayList<>();
		NxLineItemLookUpDataModel nl=new NxLineItemLookUpDataModel();
		NxOutputProductMappingModel nexusOutputMapping=new NxOutputProductMappingModel();
		nexusOutputMapping.setTabName(OutputFileConstants.DS3_ACCESS_TAB);
		nl.setNexusOutputMapping(nexusOutputMapping);
		resultLst.add(nl);
		when(repositoryService.getLineItemData(any(),any(),any(),any())).thenReturn(resultLst);
		String inputValue="AS";
		when(nexusJsonUtility.convertListToCsvWithQuote(any())).thenReturn(inputValue);
		lineItemProcessingService.fetchLineItemIdOnBeId(data, keyPath, nxOutputBean, siteObj,paramMap);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void fetchLineItemIdOnBeIdTest5() {
		NxLineItemLookUpFieldModel data=new NxLineItemLookUpFieldModel();
		Map<String, Set<String>> d=new HashMap<String, Set<String>>();
		paramMap.put(FmoConstants.FALLOUT_MAP,d);
		Set<NxKeyFieldPathModel> k=new HashSet<NxKeyFieldPathModel>();
		NxKeyFieldPathModel nk=new NxKeyFieldPathModel();
		nk.setId(1l);
		nk.setJsonPath("$.site");
		k.add(nk);
		data.setKeyFieldMapping(k);
		data.setOfferId(1l);
		Map<String,String> keyPath=new HashMap<>();
		keyPath.put("sol", "$..is");
		NxOutputBean nxOutputBean=new NxOutputBean();
		JSONObject siteObj=new JSONObject();
		JSONArray designLst=new JSONArray();
		siteObj.put(FmoConstants.DESIGN_ATR, designLst);
		List<NxLineItemLookUpDataModel> resultLst=new ArrayList<>();
		NxLineItemLookUpDataModel nl=new NxLineItemLookUpDataModel();
		NxOutputProductMappingModel nexusOutputMapping=new NxOutputProductMappingModel();
		nexusOutputMapping.setTabName(OutputFileConstants.MIS_DS1_ACCESS_TAB);
		nl.setNexusOutputMapping(nexusOutputMapping);
		resultLst.add(nl);
		when(repositoryService.getLineItemData(any(),any(),any(),any())).thenReturn(resultLst);
		String inputValue="AS";
		when(nexusJsonUtility.convertListToCsvWithQuote(any())).thenReturn(inputValue);
		lineItemProcessingService.fetchLineItemIdOnBeId(data, keyPath, nxOutputBean, siteObj,paramMap);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void fetchLineItemIdOnBeIdTest6() {
		NxLineItemLookUpFieldModel data=new NxLineItemLookUpFieldModel();
		Map<String, Set<String>> d=new HashMap<String, Set<String>>();
		paramMap.put(FmoConstants.FALLOUT_MAP,d);
		Set<NxKeyFieldPathModel> k=new HashSet<NxKeyFieldPathModel>();
		NxKeyFieldPathModel nk=new NxKeyFieldPathModel();
		nk.setId(1l);
		nk.setJsonPath("$.site");
		k.add(nk);
		data.setKeyFieldMapping(k);
		data.setOfferId(1l);
		Map<String,String> keyPath=new HashMap<>();
		keyPath.put("sol", "$..is");
		NxOutputBean nxOutputBean=new NxOutputBean();
		JSONObject siteObj=new JSONObject();
		JSONArray designLst=new JSONArray();
		siteObj.put(FmoConstants.DESIGN_ATR, designLst);
		List<NxLineItemLookUpDataModel> resultLst=new ArrayList<>();
		NxLineItemLookUpDataModel nl=new NxLineItemLookUpDataModel();
		NxOutputProductMappingModel nexusOutputMapping=new NxOutputProductMappingModel();
		nexusOutputMapping.setTabName(OutputFileConstants.MIS_TAB);
		nl.setNexusOutputMapping(nexusOutputMapping);
		resultLst.add(nl);
		when(repositoryService.getLineItemData(any(),any(),any(),any())).thenReturn(resultLst);
		String inputValue="AS";
		when(nexusJsonUtility.convertListToCsvWithQuote(any())).thenReturn(inputValue);
		lineItemProcessingService.fetchLineItemIdOnBeId(data, keyPath, nxOutputBean, siteObj,paramMap);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void fetchLineItemOnStateAndCountrytest() {
		NxLineItemLookUpFieldModel data=new NxLineItemLookUpFieldModel();
		Map<String, Set<String>> d=new HashMap<String, Set<String>>();
		paramMap.put(FmoConstants.FALLOUT_MAP,d);
		Set<NxKeyFieldPathModel> k=new HashSet<NxKeyFieldPathModel>();
		NxKeyFieldPathModel nk=new NxKeyFieldPathModel();
		nk.setId(1l);
		nk.setJsonPath("$.site");
		k.add(nk);
		data.setKeyFieldMapping(k);
		data.setOfferId(1l);
		Map<String,String> keyPath=new HashMap<>();
		keyPath.put("sol", "$..is");
		NxOutputBean nxOutputBean=new NxOutputBean();
		JSONObject siteObj=new JSONObject();
		JSONArray designLst=new JSONArray();
		siteObj.put(FmoConstants.DESIGN_ATR, designLst);
		List<NxLineItemLookUpDataModel> resultLst=new ArrayList<>();
		NxLineItemLookUpDataModel nl=new NxLineItemLookUpDataModel();
		NxOutputProductMappingModel nexusOutputMapping=new NxOutputProductMappingModel();
		nexusOutputMapping.setTabName(OutputFileConstants.AVPN_TAB);
		nl.setNexusOutputMapping(nexusOutputMapping);
		
		resultLst.add(nl);
		when(repositoryService.getLineItemData(any(),any())).thenReturn(resultLst);
		lineItemProcessingService.fetchLineItemOnStateAndCountry(data, keyPath, nxOutputBean, siteObj,paramMap);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void fetchLineItemOnStateAndCountrytestElseIf1() {
		Map<String, Set<String>> d=new HashMap<String, Set<String>>();
		paramMap.put(FmoConstants.FALLOUT_MAP,d);
		Set<NxKeyFieldPathModel> k=new HashSet<NxKeyFieldPathModel>();
		NxKeyFieldPathModel nk=new NxKeyFieldPathModel();
		nk.setId(1l);
		nk.setJsonPath("$.site");
		k.add(nk);
		NxLineItemLookUpFieldModel data=new NxLineItemLookUpFieldModel();
		data.setOfferId(1l);
		Map<String,String> keyPath=new HashMap<>();
		keyPath.put("sol", "$..is");
		NxOutputBean nxOutputBean=new NxOutputBean();
		JSONObject siteObj=new JSONObject();
		JSONArray designLst=new JSONArray();
		siteObj.put(FmoConstants.DESIGN_ATR, designLst);
		List<NxLineItemLookUpDataModel> resultLst=new ArrayList<>();
		NxLineItemLookUpDataModel nl=new NxLineItemLookUpDataModel();
		NxOutputProductMappingModel nexusOutputMapping=new NxOutputProductMappingModel();
		nexusOutputMapping.setTabName("AVPN_Intl_Access");
		nl.setNexusOutputMapping(nexusOutputMapping);
		
		resultLst.add(nl);
		when(repositoryService.getLineItemData(any(),any())).thenReturn(resultLst);
		lineItemProcessingService.fetchLineItemOnStateAndCountry(data, keyPath, nxOutputBean, siteObj,paramMap);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void fetchLineItemOnStateAndCountrytestElseIf2() {
		Map<String, Set<String>> d=new HashMap<String, Set<String>>();
		paramMap.put(FmoConstants.FALLOUT_MAP,d);
		Set<NxKeyFieldPathModel> k=new HashSet<NxKeyFieldPathModel>();
		NxKeyFieldPathModel nk=new NxKeyFieldPathModel();
		nk.setId(1l);
		nk.setJsonPath("$.site");
		k.add(nk);
		NxLineItemLookUpFieldModel data=new NxLineItemLookUpFieldModel();
		data.setOfferId(1l);
		Map<String,String> keyPath=new HashMap<>();
		keyPath.put("sol", "$..is");
		NxOutputBean nxOutputBean=new NxOutputBean();
		JSONObject siteObj=new JSONObject();
		JSONArray designLst=new JSONArray();
		siteObj.put(FmoConstants.DESIGN_ATR, designLst);
		List<NxLineItemLookUpDataModel> resultLst=new ArrayList<>();
		NxLineItemLookUpDataModel nl=new NxLineItemLookUpDataModel();
		NxOutputProductMappingModel nexusOutputMapping=new NxOutputProductMappingModel();
		nexusOutputMapping.setTabName("MIS_DS1_Access");
		nl.setNexusOutputMapping(nexusOutputMapping);
		
		resultLst.add(nl);
		when(repositoryService.getLineItemData(any(),any())).thenReturn(resultLst);
		lineItemProcessingService.fetchLineItemOnStateAndCountry(data, keyPath, nxOutputBean, siteObj,paramMap);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void fetchLineItemOnStateAndCountrytestElseIf3() {
		Map<String, Set<String>> d=new HashMap<String, Set<String>>();
		paramMap.put(FmoConstants.FALLOUT_MAP,d);
		Set<NxKeyFieldPathModel> k=new HashSet<NxKeyFieldPathModel>();
		NxKeyFieldPathModel nk=new NxKeyFieldPathModel();
		nk.setId(1l);
		nk.setJsonPath("$.site");
		k.add(nk);
		NxLineItemLookUpFieldModel data=new NxLineItemLookUpFieldModel();
		data.setOfferId(1l);
		Map<String,String> keyPath=new HashMap<>();
		keyPath.put("sol", "$..is");
		NxOutputBean nxOutputBean=new NxOutputBean();
		JSONObject siteObj=new JSONObject();
		JSONArray designLst=new JSONArray();
		siteObj.put(FmoConstants.DESIGN_ATR, designLst);
		List<NxLineItemLookUpDataModel> resultLst=new ArrayList<>();
		NxLineItemLookUpDataModel nl=new NxLineItemLookUpDataModel();
		NxOutputProductMappingModel nexusOutputMapping=new NxOutputProductMappingModel();
		nexusOutputMapping.setTabName("AVPN_DS1_Access");
		nl.setNexusOutputMapping(nexusOutputMapping);
		
		resultLst.add(nl);
		when(repositoryService.getLineItemData(any(),any())).thenReturn(resultLst);
		lineItemProcessingService.fetchLineItemOnStateAndCountry(data, keyPath, nxOutputBean, siteObj,paramMap);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void fetchLineItemOnStateAndCountrytestElseIf4() {
		Map<String, Set<String>> d=new HashMap<String, Set<String>>();
		paramMap.put(FmoConstants.FALLOUT_MAP,d);
		Set<NxKeyFieldPathModel> k=new HashSet<NxKeyFieldPathModel>();
		NxKeyFieldPathModel nk=new NxKeyFieldPathModel();
		nk.setId(1l);
		nk.setJsonPath("$.site");
		k.add(nk);
		NxLineItemLookUpFieldModel data=new NxLineItemLookUpFieldModel();
		data.setOfferId(1l);
		Map<String,String> keyPath=new HashMap<>();
		keyPath.put("sol", "$..is");
		NxOutputBean nxOutputBean=new NxOutputBean();
		JSONObject siteObj=new JSONObject();
		JSONArray designLst=new JSONArray();
		siteObj.put(FmoConstants.DESIGN_ATR, designLst);
		List<NxLineItemLookUpDataModel> resultLst=new ArrayList<>();
		NxLineItemLookUpDataModel nl=new NxLineItemLookUpDataModel();
		NxOutputProductMappingModel nexusOutputMapping=new NxOutputProductMappingModel();
		nexusOutputMapping.setTabName("AVPN_DS1_Flat_Rate_Access");
		nl.setNexusOutputMapping(nexusOutputMapping);
		
		resultLst.add(nl);
		when(repositoryService.getLineItemData(any(),any())).thenReturn(resultLst);
		lineItemProcessingService.fetchLineItemOnStateAndCountry(data, keyPath, nxOutputBean, siteObj,paramMap);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void fetchLineItemOnStateAndCountrytestElseIf5() {
		NxLineItemLookUpFieldModel data=new NxLineItemLookUpFieldModel();
		Map<String, Set<String>> d=new HashMap<String, Set<String>>();
		paramMap.put(FmoConstants.FALLOUT_MAP,d);
		Set<NxKeyFieldPathModel> k=new HashSet<NxKeyFieldPathModel>();
		NxKeyFieldPathModel nk=new NxKeyFieldPathModel();
		nk.setId(1l);
		nk.setJsonPath("$.site");
		k.add(nk);
		data.setKeyFieldMapping(k);
		data.setOfferId(1l);
		Map<String,String> keyPath=new HashMap<>();
		keyPath.put("sol", "$..is");
		NxOutputBean nxOutputBean=new NxOutputBean();
		JSONObject siteObj=new JSONObject();
		JSONArray designLst=new JSONArray();
		siteObj.put(FmoConstants.DESIGN_ATR, designLst);
		List<NxLineItemLookUpDataModel> resultLst=new ArrayList<>();
		NxLineItemLookUpDataModel nl=new NxLineItemLookUpDataModel();
		NxOutputProductMappingModel nexusOutputMapping=new NxOutputProductMappingModel();
		nexusOutputMapping.setTabName("AVPN_DS0DS1_Access");
		nl.setNexusOutputMapping(nexusOutputMapping);
		
		resultLst.add(nl);
		when(repositoryService.getLineItemData(any(),any())).thenReturn(resultLst);
		lineItemProcessingService.fetchLineItemOnStateAndCountry(data, keyPath, nxOutputBean, siteObj,paramMap);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void fetchLineItemOnStateAndCountrytestElseIf6() {
		NxLineItemLookUpFieldModel data=new NxLineItemLookUpFieldModel();
		Map<String, Set<String>> d=new HashMap<String, Set<String>>();
		paramMap.put(FmoConstants.FALLOUT_MAP,d);
		Set<NxKeyFieldPathModel> k=new HashSet<NxKeyFieldPathModel>();
		NxKeyFieldPathModel nk=new NxKeyFieldPathModel();
		nk.setId(1l);
		nk.setJsonPath("$.site");
		k.add(nk);
		data.setKeyFieldMapping(k);
		data.setOfferId(1l);
		Map<String,String> keyPath=new HashMap<>();
		keyPath.put("sol", "$..is");
		NxOutputBean nxOutputBean=new NxOutputBean();
		JSONObject siteObj=new JSONObject();
		JSONArray designLst=new JSONArray();
		siteObj.put(FmoConstants.DESIGN_ATR, designLst);
		List<NxLineItemLookUpDataModel> resultLst=new ArrayList<>();
		NxLineItemLookUpDataModel nl=new NxLineItemLookUpDataModel();
		NxOutputProductMappingModel nexusOutputMapping=new NxOutputProductMappingModel();
		nexusOutputMapping.setTabName("DS3_Access");
		nl.setNexusOutputMapping(nexusOutputMapping);
		
		resultLst.add(nl);
		when(repositoryService.getLineItemData(any(),any())).thenReturn(resultLst);
		lineItemProcessingService.fetchLineItemOnStateAndCountry(data, keyPath, nxOutputBean, siteObj,paramMap);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void fetchLineItemOnStateAndCountrytestElseIf7() {
		NxLineItemLookUpFieldModel data=new NxLineItemLookUpFieldModel();
		Map<String, Set<String>> d=new HashMap<String, Set<String>>();
		paramMap.put(FmoConstants.FALLOUT_MAP,d);
		Set<NxKeyFieldPathModel> k=new HashSet<NxKeyFieldPathModel>();
		NxKeyFieldPathModel nk=new NxKeyFieldPathModel();
		nk.setId(1l);
		nk.setJsonPath("$.site");
		k.add(nk);
		data.setKeyFieldMapping(k);
		data.setOfferId(1l);
		Map<String,String> keyPath=new HashMap<>();
		keyPath.put("sol", "$..is");
		NxOutputBean nxOutputBean=new NxOutputBean();
		JSONObject siteObj=new JSONObject();
		JSONArray designLst=new JSONArray();
		siteObj.put(FmoConstants.DESIGN_ATR, designLst);
		List<NxLineItemLookUpDataModel> resultLst=new ArrayList<>();
		NxLineItemLookUpDataModel nl=new NxLineItemLookUpDataModel();
		NxOutputProductMappingModel nexusOutputMapping=new NxOutputProductMappingModel();
		nexusOutputMapping.setTabName("EthernetAccess");
		nl.setNexusOutputMapping(nexusOutputMapping);
		
		resultLst.add(nl);
		when(repositoryService.getLineItemData(any(),any())).thenReturn(resultLst);
		lineItemProcessingService.fetchLineItemOnStateAndCountry(data, keyPath, nxOutputBean, siteObj,paramMap);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void fetchLineItemOnStateAndCountrytestElseIf8() {
		NxLineItemLookUpFieldModel data=new NxLineItemLookUpFieldModel();
		Map<String, Set<String>> d=new HashMap<String, Set<String>>();
		paramMap.put(FmoConstants.FALLOUT_MAP,d);
		Set<NxKeyFieldPathModel> k=new HashSet<NxKeyFieldPathModel>();
		NxKeyFieldPathModel nk=new NxKeyFieldPathModel();
		nk.setId(1l);
		nk.setJsonPath("$.site");
		k.add(nk);
		data.setKeyFieldMapping(k);
		data.setOfferId(1l);
		Map<String,String> keyPath=new HashMap<>();
		keyPath.put("sol", "$..is");
		NxOutputBean nxOutputBean=new NxOutputBean();
		JSONObject siteObj=new JSONObject();
		JSONArray designLst=new JSONArray();
		siteObj.put(FmoConstants.DESIGN_ATR, designLst);
		List<NxLineItemLookUpDataModel> resultLst=new ArrayList<>();
		NxLineItemLookUpDataModel nl=new NxLineItemLookUpDataModel();
		NxOutputProductMappingModel nexusOutputMapping=new NxOutputProductMappingModel();
		nexusOutputMapping.setTabName("MIS");
		nl.setNexusOutputMapping(nexusOutputMapping);
		
		resultLst.add(nl);
		when(repositoryService.getLineItemData(any(),any())).thenReturn(resultLst);
		lineItemProcessingService.fetchLineItemOnStateAndCountry(data, keyPath, nxOutputBean, siteObj,paramMap);
	}
	
	@Test
	public void getMrcNrcPriceDetailsTest1() {
		Map<String,String> keyPath=new HashMap<>();
		keyPath.put("sol", "$..is");
		NxLineItemLookUpDataModel lookupData=new NxLineItemLookUpDataModel();
		lookupData.setIdetityField1("123");
		lookupData.setIdetityField2("345");
		List<Object> priceDetails =new ArrayList<>();
		LinkedHashMap<String, String> data=new LinkedHashMap<>();
		data.put(FmoConstants.FREQUENCY, FmoConstants.NRC);
		priceDetails.add(data);
		when(nexusJsonUtility.getValueLst(any(),any())).thenReturn(priceDetails);
		List<String> beIdLst=new ArrayList<>();
		beIdLst.add("123");
		lineItemProcessingService.getMrcNrcPriceDetails(priceDetails);
	}
	
	@Test
	public void getMrcNrcPriceDetailsTest2() {
		Map<String,String> keyPath=new HashMap<>();
		keyPath.put("sol", "$..is");
		NxLineItemLookUpDataModel lookupData=new NxLineItemLookUpDataModel();
		lookupData.setIdetityField1("123");
		lookupData.setIdetityField2("345");
		List<Object> priceDetails =new ArrayList<>();
		LinkedHashMap<String, String> data=new LinkedHashMap<>();
		data.put(FmoConstants.FREQUENCY, FmoConstants.MRC);
		priceDetails.add(data);
		when(nexusJsonUtility.getValueLst(any(),any())).thenReturn(priceDetails);
		List<String> beIdLst=new ArrayList<>();
		beIdLst.add("123");
		lineItemProcessingService.getMrcNrcPriceDetails(priceDetails);
	}
	
	@Test
	public void constructQueryTest() {
		Set<NxKeyFieldPathModel> keyFieldMapping=new HashSet<>();
		NxKeyFieldPathModel nf1 =new NxKeyFieldPathModel();
		nf1.setJsonPath("$..");
		nf1.setFieldName("GG");
		keyFieldMapping.add(nf1);
		NxKeyFieldPathModel nf2 =new NxKeyFieldPathModel();
		nf2.setJsonPath("$..");
		nf2.setFieldName("AA");
		keyFieldMapping.add(nf2);
		JSONObject siteObj=new JSONObject();
		String inputValue="AS";
		when(nexusJsonUtility.convertListToCsvWithQuote(any())).thenReturn(inputValue);
		List<String> beIdLst=new ArrayList<>();
		beIdLst.add("123");
		String filterCriteria="{\"LITTLE_PRODUCT_ID\":[\"6005\"]}";
		lineItemProcessingService.constructQuery(siteObj,keyFieldMapping,filterCriteria);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void createDSAccessOutputBeanTest() {
		Map<String,String> priceDetailsMap=new HashMap<>();
		JSONArray designLst=new JSONArray();
		Map<String,String> keyPath=new HashMap<>();
		keyPath.put("sol", "$..is");
		JSONObject siteObj=new JSONObject();
		siteObj.put(FmoConstants.STATE, "TX");
		lineItemProcessingService.createAvpnDS0DS1AccessOutputBean(siteObj,designLst, keyPath,1l);
	}
	
	
	@Test
	public void getRateIdsListTest() {
		NxLineItemLookUpDataModel lookupdata=new NxLineItemLookUpDataModel();
		Map<String,Map<String,String>> nrcMrcSrcMap=new HashMap<String, Map<String,String>>();
		JSONArray designLst=new JSONArray();
		Map<String,String> keyPath=new HashMap<String, String>();
		lookupdata.setIdetityField1("abc");
		lookupdata.setIdetityField2("abc");
		lineItemProcessingService.getRateIdsList(lookupdata, nrcMrcSrcMap, designLst, keyPath);
	}
	
	@Test
	public void getRateIdsListTest2() {
		NxLineItemLookUpDataModel lookupdata=new NxLineItemLookUpDataModel();
		Map<String,String> m=new HashMap<String, String>();
		m.put(FmoConstants.LOOKUP, "AS");
		Map<String,Map<String,String>> nrcMrcSrcMap=new HashMap<String, Map<String,String>>();
		nrcMrcSrcMap.put(FmoConstants.MRC_RATEID_SOURCE, m);
		nrcMrcSrcMap.put(FmoConstants.NRC_RATEID_SOURCE, m);
		JSONArray designLst=new JSONArray();
		Map<String,String> keyPath=new HashMap<String, String>();
		lineItemProcessingService.getRateIdsList(lookupdata, nrcMrcSrcMap, designLst, keyPath);
	}
	
	
	
	@Test
	public void getBeIdListTest1() {
		Map<String,Map<String,String>> nrcMrcSrcMap=new HashMap<>();
		Map<String,String> keyPath=new HashMap<>();
		keyPath.put("sol", "$..is");
		NxLineItemLookUpDataModel  lookupdata=new NxLineItemLookUpDataModel();
		lookupdata.setOfferId(1l);
		lookupdata.setIdetityField1("ABC");
		lookupdata.setIdetityField2("BGH");
		JSONArray designLst=new JSONArray();
		lineItemProcessingService.getBeIdList(lookupdata, nrcMrcSrcMap, designLst, keyPath);
	}
	@Test
	public void getBeIdListTest2() {
		Map<String,Map<String,String>> nrcMrcSrcMap=new HashMap<>();
		Map<String,String> m1=new HashMap<>();
		m1.put(FmoConstants.LOOKUP, "A");
		nrcMrcSrcMap.put(FmoConstants.MRC_BEID_SOURCE, m1);
		Map<String,String> m2=new HashMap<>();
		m2.put(FmoConstants.LOOKUP, "B");
		nrcMrcSrcMap.put(FmoConstants.NRC_BEID_SOURCE, m1);
		Map<String,String> keyPath=new HashMap<>();
		keyPath.put("sol", "$..is");
		NxLineItemLookUpDataModel  lookupdata=new NxLineItemLookUpDataModel();
		lookupdata.setOfferId(1l);
		JSONArray designLst=new JSONArray();
		lineItemProcessingService.getBeIdList(lookupdata, nrcMrcSrcMap, designLst, keyPath);
	}
	
	
	@Test
	public void getNxOutputBeanTest() {
		NxOutputBean nxOutputBean=new NxOutputBean();
		NxAvpnOutputBean avpn=new NxAvpnOutputBean();
		avpn.setCity("NJ");
		nxOutputBean.getNxAvpnOutput().add(avpn);
		lineItemProcessingService.getNxOutputBean(nxOutputBean);
	}

	
	
	@Test
	public void getMrcNrcColumnTest() {
		Map<String,Map<String,String>> nrcMrcSrcMap=new HashMap<>();
		Map<String,String> m1=new HashMap<>();
		m1.put(FmoConstants.COLUMN, "FILED1_VALUE");
		nrcMrcSrcMap.put(FmoConstants.MRC_BEID_SOURCE, m1);
		lineItemProcessingService.getColumnNameFromMap(nrcMrcSrcMap, FmoConstants.MRC_BEID_SOURCE);
	}
	
	@Test
	public void outputFileGenerationBvoipFlowTest() {
		Map<String, Set<String>> d=new HashMap<String, Set<String>>();
		paramMap.put(FmoConstants.FALLOUT_MAP,d);
		Set<NxKeyFieldPathModel> k=new HashSet<NxKeyFieldPathModel>();
		NxKeyFieldPathModel nk=new NxKeyFieldPathModel();
		nk.setId(1l);
		nk.setJsonPath("$.site");
		k.add(nk);
		NxOutputBean nxOutputBean=new NxOutputBean();
		Map<String, String> keyPathMap=new HashMap<String, String>();
		NxLineItemLookUpFieldModel data=new NxLineItemLookUpFieldModel();
		data.setKeyFieldName(FmoConstants.RCRATEID_CON_CALLTYPE_CRITERIA);
		data.setKeyFieldMapping(k);
		
		paramMap.put(FmoConstants.COUNTRY_CD, "US");
		String inputValue="AS";
		when(nexusJsonUtility.convertListToCsvWithQuote(any())).thenReturn(inputValue);
		List<NxLineItemLookUpDataModel> resultLst=new ArrayList<>();
		NxLineItemLookUpDataModel nl=new NxLineItemLookUpDataModel();
		NxOutputProductMappingModel nexusOutputMapping=new NxOutputProductMappingModel();
		nexusOutputMapping.setTabName(OutputFileConstants.MIS_DS1_ACCESS_TAB);
		nl.setNexusOutputMapping(nexusOutputMapping);
		resultLst.add(nl);
		when(repositoryService.getLineItemData(any(),any(),any(),any())).thenReturn(resultLst);
		lineItemProcessingService.outputFileGenerationBvoipFlow(nxOutputBean, keyPathMap, siteObj, data, paramMap);
	}
	
	@Test
	public void outputFileGenerationBvoipFlowTest2() {
		Map<String, Set<String>> d=new HashMap<String, Set<String>>();
		paramMap.put(FmoConstants.FALLOUT_MAP,d);
		Set<NxKeyFieldPathModel> k=new HashSet<NxKeyFieldPathModel>();
		NxKeyFieldPathModel nk=new NxKeyFieldPathModel();
		nk.setId(1l);
		nk.setJsonPath("$.site");
		k.add(nk);
		NxOutputBean nxOutputBean=new NxOutputBean();
		Map<String, String> keyPathMap=new HashMap<String, String>();
		NxLineItemLookUpFieldModel data=new NxLineItemLookUpFieldModel();
		data.setKeyFieldName("ss");
		data.setKeyFieldMapping(k);
		paramMap.put(FmoConstants.COUNTRY_CD, "US");
		String inputValue="AS";
		when(nexusJsonUtility.convertListToCsvWithQuote(any())).thenReturn(inputValue);
		List<NxLineItemLookUpDataModel> resultLst=new ArrayList<>();
		NxLineItemLookUpDataModel nl=new NxLineItemLookUpDataModel();
		NxOutputProductMappingModel nexusOutputMapping=new NxOutputProductMappingModel();
		nexusOutputMapping.setTabName(OutputFileConstants.MIS_DS1_ACCESS_TAB);
		nl.setNexusOutputMapping(nexusOutputMapping);
		resultLst.add(nl);
		when(repositoryService.getLineItemData(any(),any(),any(),any())).thenReturn(resultLst);
		lineItemProcessingService.outputFileGenerationBvoipFlow(nxOutputBean, keyPathMap, siteObj, data, paramMap);
	}
	
	@Test
	public void fetchLineItemOnStateAndCountryNew() {
		
		NxLineItemLookUpFieldModel data=new NxLineItemLookUpFieldModel();
		Map<String, Set<String>> d=new HashMap<String, Set<String>>();
		paramMap.put(FmoConstants.FALLOUT_MAP,d);
		Set<NxKeyFieldPathModel> k=new HashSet<NxKeyFieldPathModel>();
		NxKeyFieldPathModel nk=new NxKeyFieldPathModel();
		nk.setId(1l);
		nk.setJsonPath("$.site");
		k.add(nk);
		data.setKeyFieldName("ss");
		data.setKeyFieldMapping(k);
		Map<String, String> keyPath=new HashMap<String, String>();
		NxOutputBean nxOutputBean=new NxOutputBean();
		data.setKeyFieldName("ss");
		data.setKeyFieldMapping(k);
		paramMap.put(FmoConstants.COUNTRY_CD, "US");
		String inputValue="AS";
		when(nexusJsonUtility.convertListToCsvWithQuote(any())).thenReturn(inputValue);
		List<NxLineItemLookUpDataModel> resultLst=new ArrayList<>();
		NxLineItemLookUpDataModel nl=new NxLineItemLookUpDataModel();
		NxOutputProductMappingModel nexusOutputMapping=new NxOutputProductMappingModel();
		nexusOutputMapping.setTabName(OutputFileConstants.MIS_DS1_ACCESS_TAB);
		nl.setNexusOutputMapping(nexusOutputMapping);
		resultLst.add(nl);
		when(repositoryService.getLineItemData(any(),any())).thenReturn(resultLst);
		lineItemProcessingService.fetchLineItemOnStateAndCountry(data, keyPath, nxOutputBean, siteObj, paramMap);
	}
	

	@SuppressWarnings("unchecked")
	//@Test 
	public void fetchLineItemIdOnRateIdTest() {
		NxLineItemLookUpFieldModel data=new NxLineItemLookUpFieldModel();
		data.setOfferId(1l);
		Map<String,String> keyPath=new HashMap<>();
		keyPath.put("sol", "$..is");
		NxOutputBean nxOutputBean=new NxOutputBean();
		JSONObject siteObj=new JSONObject();
		JSONArray designLst=new JSONArray();
		siteObj.put(FmoConstants.DESIGN_ATR, designLst);
		List<NxLineItemLookUpDataModel> resultLst=new ArrayList<>();
		NxLineItemLookUpDataModel nl=new NxLineItemLookUpDataModel();
		NxOutputProductMappingModel nexusOutputMapping=new NxOutputProductMappingModel();
		nexusOutputMapping.setTabName(OutputFileConstants.AVPN_TAB);
		nl.setNexusOutputMapping(nexusOutputMapping);
		resultLst.add(nl);
		when(repositoryService.getLineItemData(any(),any(),any(),any())).thenReturn(resultLst);
		//lineItemProcessingService.fetchLineItemIdOnRateId(data, keyPath, nxOutputBean, siteObj);
	}
	
	/*@Test
	public void testGetFalloutDataMap() {
		lineItemProcessingService.getFalloutDataMap();	
	}
	@Test
	public void testsetFalloutDataMap() {
		String key="";
		Set<String> value=new HashSet<>();
		String s1="set";
		value.add(s1);
		Mockito.when(falloutDataMap.containsKey(Mockito.any())).thenReturn(true);
		//Mockito.when(falloutDataMap.replace(Mockito.anyString(), Mockito.anySet())).thenReturn(value);
		lineItemProcessingService.setFalloutDataMap(key, value);
	}
	@Test
	public void testsetFalloutDataMapIf() {
		String key="";
		Set<String> value=new HashSet<>();
		String s1="set";
		value.add(s1);
		//Mockito.when(falloutDataMap.containsKey(Mockito.any())).thenReturn(true);
		Mockito.when(falloutDataMap.replace(Mockito.anyString(), Mockito.anyObject())).thenReturn(value);
		lineItemProcessingService.setFalloutDataMap(key, value);
	} 
	@Test
	public void testsetFalloutDataMapElse() {
		String key="";
		Set<String> value=new HashSet<>();
		Mockito.when(falloutDataMap.containsKey(Mockito.any())).thenReturn(true);
		//Mockito.when(falloutDataMap.replace(Mockito.anyString(), Mockito.anySet())).thenReturn(value);
		lineItemProcessingService.setFalloutDataMap(key, value);
	}*/
	
	
	@Test
	public void testcreateBvoipOutput() {
		String value = "{\"country\":\"country\"}";
		JSONObject siteObj = JacksonUtil.toJsonObject(value);
		JSONArray designLst= JacksonUtil.toJsonArray("[{\"site\":\"data\"}]");
		Map<String,String> mrcPriceObj=new HashMap<>();
		mrcPriceObj.put(FmoConstants.QUANTITY,"2");
		mrcPriceObj.put(FmoConstants.LOCAL_LST_PRICE,"2");
		mrcPriceObj.put(FmoConstants.ICB_DESIRED_DISCOUNT,"2");
		mrcPriceObj.put(FmoConstants.TERM,"2");
		
		Map<String,String> nrcPriceObj= new HashMap<>();
		nrcPriceObj.put(FmoConstants.QUANTITY, "2");
		nrcPriceObj.put(FmoConstants.ICB_DESIRED_DISCOUNT, "2");
		nrcPriceObj.put(FmoConstants.TERM, "2");
		
		Map<String,String> keyPath=new HashMap<>();
		when(nexusJsonUtility.getValue(any(),anyString())).thenReturn("designLevelConCallType");
		//case1
		lineItemProcessingService.createBvoipOutput(siteObj, designLst, mrcPriceObj, null, keyPath);
		
		//case2
		lineItemProcessingService.createBvoipOutput(siteObj, designLst, null, nrcPriceObj, keyPath);

		//case3
		siteObj.remove("country");
		Map<String,String> mrcPriceObj1=new HashMap<>();
		mrcPriceObj1.put("test", "test");
		lineItemProcessingService.createBvoipOutput(siteObj, null,mrcPriceObj1, null, keyPath);

		//case4
		Map<String,String> nrcPriceObj1=new HashMap<>();
		nrcPriceObj1.put("test", "test");
		lineItemProcessingService.createBvoipOutput(siteObj, designLst,null, nrcPriceObj1, keyPath);
	}
	
	@Test
	public void testfetchLineItemOnDesignLevel() {
		NxLineItemLookUpFieldModel data = new NxLineItemLookUpFieldModel();
		Set<NxKeyFieldPathModel> nxKeyFieldPathModelset= new HashSet<>();  
		NxKeyFieldPathModel nxKeyFieldPathModel = new NxKeyFieldPathModel();
		nxKeyFieldPathModel.setJsonPath("jsonPath");
		nxKeyFieldPathModelset.add(nxKeyFieldPathModel);
		data.setKeyFieldMapping(nxKeyFieldPathModelset);
		data.setKeyFieldCondition("and");
		String value = "{\"siteId\":\"57657\", \"country\":\"US\" ,"
				+ "\"design\":[{ \"siteName\":\"2973294\"}]}";
		JSONObject siteObj = JacksonUtil.toJsonObject(value);
		NxLookupData nxLookupData = new NxLookupData();
		nxLookupData.setDescription("US");
		when(repositoryService.getLookupDataById(anyString(),anyString())).thenReturn(nxLookupData);
		when(nexusJsonUtility.convertListToCsvWithQuote(anyList())).thenReturn("resultdata");
		Map<String,String> keyPath=new HashMap<>();
		NxOutputBean nxOutputBean=new NxOutputBean();
		Map<String, Object> paramMap=new HashMap<>();
		paramMap.put(FmoConstants.FALLOUT_MAP,new HashMap<String, Set<String>> ());
		List<NxLineItemLookUpDataModel> result = new ArrayList<>();
		NxLineItemLookUpDataModel nxLineItemLookUpDataModel = new NxLineItemLookUpDataModel();
		NxOutputProductMappingModel nexusOutputMapping = new NxOutputProductMappingModel();
		nexusOutputMapping.setTabName("tableName");
		nexusOutputMapping.setTopProdId(1L);
		nxLineItemLookUpDataModel.setNexusOutputMapping(nexusOutputMapping);
		result.add(nxLineItemLookUpDataModel);
		when(repositoryService.getLineItemData(anyString(),anyString())).thenReturn(result);
		lineItemProcessingService.fetchLineItemOnDesignLevel(data,
				keyPath, nxOutputBean, siteObj, paramMap);
	}
	
	@Test
	public void testfetchLineItemIdOnBeIdForMp() {
		NxLineItemLookUpFieldModel data= new NxLineItemLookUpFieldModel();
		Set<NxKeyFieldPathModel> nxKeyFieldPathModelset= new HashSet<>();  
		NxKeyFieldPathModel nxKeyFieldPathModel = new NxKeyFieldPathModel();
		nxKeyFieldPathModel.setJsonPath("jsonPath");
		nxKeyFieldPathModelset.add(nxKeyFieldPathModel);
		data.setKeyFieldMapping(nxKeyFieldPathModelset);
		data.setKeyFieldCondition("and");
		data.setOfferName("BVOIP");
		data.setMrcNrcSourceMap("mrcNrcSourceMap");
		Map<String,String> keyPath= new HashMap<>();
		NxOutputBean nxOutputBean= new NxOutputBean();
		String value = "{\"siteId\":\"57657\", \"country\":\"US\" ,"
				+ "\"design\":[{ \"siteName\":\"2973294\"}]}";
		JSONObject siteObj = JacksonUtil.toJsonObject(value);
		Map<String, Object> paramMap= new HashMap<>();
		Map nrcMrcSrcMap = new HashMap();
		when(nexusJsonUtility.convertStringJsonToMap(anyString())).thenReturn( nrcMrcSrcMap);
		when(nexusJsonUtility.convertListToCsvWithQuote(anyList())).thenReturn("resultdata");
		paramMap.put(FmoConstants.FALLOUT_MAP,new HashMap<String, Set<String>> ());
		List<NxLineItemLookUpDataModel> result = new ArrayList<>();
		NxLineItemLookUpDataModel nxLineItemLookUpDataModel = new NxLineItemLookUpDataModel();
		NxOutputProductMappingModel nexusOutputMapping = new NxOutputProductMappingModel();
		nexusOutputMapping.setTabName("tableName");
		nexusOutputMapping.setTopProdId(1L);
		nxLineItemLookUpDataModel.setNexusOutputMapping(nexusOutputMapping);
		result.add(nxLineItemLookUpDataModel);
		when(repositoryService.getLineItemData(anyString(),anyString(),anyString(),anyString())).thenReturn(result);
		lineItemProcessingService.fetchLineItemIdOnBeIdForMp(data,keyPath,nxOutputBean,siteObj,paramMap);
	}
	
	@Test
	public void testsetNxKey() {
		Long offerId=1l;
		String value = "{\"siteId\":\"57657\", \"country\":\"US\" ,"
				+ "\"design\":[{ \"siteName\":\"2973294\"}]}";
		JSONObject siteObj = JacksonUtil.toJsonObject(value);
		Map<String, ArrayList<String>> portwiseKeyMap= new HashMap<>();
		ArrayList<String> designLevelKeyList=new ArrayList<>();
		designLevelKeyList.add("key"); 
		portwiseKeyMap.put("portId", designLevelKeyList);
		Map<String, Object> paramMap= new HashMap<>();

		when(nxMpRepositoryService.getOfferNameByOfferId(anyInt())).thenReturn("BVoIP");
		when(configAndUpdateProcessingFmoService.getBvoipOfferName(anyString())).thenReturn(MyPriceConstants.BVoIP);
		lineItemProcessingService.setNxKey(offerId,siteObj,portwiseKeyMap,paramMap);
	
		when(configAndUpdateProcessingFmoService.getBvoipOfferName(anyString())).thenReturn(MyPriceConstants.AVPN);
		when(nexusJsonUtility.getValue(any(),anyString())).thenReturn("designByPortId");
		when(configAndUpdateProcessingFmoService.getAccessType(any(),anyString(),anyMap()))
		.thenReturn("InternationalAccess");
		lineItemProcessingService.setNxKey(offerId,siteObj,portwiseKeyMap,paramMap);
	}
	
	@Test
	public void testcollectFalloutBeid() {
		Set<String> allMrcBeids=new HashSet<>();
		allMrcBeids.add("ORLX");
		allMrcBeids.add("beid");
		LinkedHashMap<String,String> mrcPriceDtlsBlock= new LinkedHashMap<String, String>();
		mrcPriceDtlsBlock.put(FmoConstants.BEID, "beid");
		lineItemProcessingService.collectFalloutBeid(allMrcBeids,mrcPriceDtlsBlock);
	}
	
	@Test
	public void testfilterCriteria() {
		Map resultMap = new HashMap();
		List<String> stringList= new ArrayList<>();
		stringList.add("data");
		resultMap.put("data", stringList);
		when(nexusJsonUtility.convertStringJsonToMap(anyString())).thenReturn( resultMap);
		when(nexusJsonUtility.convertListToCsvWithQuote(anyList())).thenReturn("resultdata");
		lineItemProcessingService.filterCriteria("filterCriteriaMapping");
	}
	
	@Test
	public void testgenerateDesignLevelNxKey() {
		List<NxLineItemLookUpDataModel> lineItemLst=new ArrayList<>();
		NxLineItemLookUpDataModel nxLineItemLookUpDataModel = new NxLineItemLookUpDataModel();
		nxLineItemLookUpDataModel.setNxItemId(1L);
		lineItemLst.add(nxLineItemLookUpDataModel);
		
		Map<String,ArrayList<String>> portwiseMap=new HashMap<>();
		ArrayList<String> portIdList= new ArrayList();
		portIdList.add("portId");
		portwiseMap.put("portId", portIdList);
		
		String value = "{\"siteId\":\"57657\", \"country\":\"US\" ,"
				+ "\"design\":[{ \"siteName\":\"2973294\"}]}";
		JSONObject siteObj = JacksonUtil.toJsonObject(value);
		ArrayList<Object> portIdListresult= new ArrayList();
		portIdListresult.add("portId");
		
		when(nexusJsonUtility.getValueLst(any(),anyString())).thenReturn(portIdListresult);
		lineItemProcessingService.generateDesignLevelNxKey(lineItemLst,portwiseMap,siteObj);
	}
}
