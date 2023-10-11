package com.att.sales.nexxus.myprice.transaction.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;

@ExtendWith(MockitoExtension.class)

public class ConfigAndUpdateRestUtilFmoTest {
	
	@InjectMocks
	ConfigAndUpdateRestUtilFmo configAndUpdateRestUtilFmo;
	
	@Mock
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Mock
	private NexxusJsonUtility nexxusJsonUtility;
	
	@Mock
	private RestCommonUtil restCommonUtil;
	
	@Mock
	private JsonPathUtil jsonPathUtil;
	
	private Map<String, Object> requestMap;
	
	private String inputDesign;
	
	@BeforeEach
	public void init() {
		requestMap = new HashMap<String, Object>();
		requestMap.put(MyPriceConstants.OFFER_NAME, "AVPN");
		requestMap.put(MyPriceConstants.PRODUCT_TYPE, "FMO");
		inputDesign = "[ { \"zipCode\": \"560 002\", \"country\": \"IN\", \"nxSiteId\": 449573, \"address\": null, \"referenceOfferId\": 4, \"isLineItemPicked\": \"Y\", \"address2\": null, \"city\": \"Bangalore\", \"address1\": \"No.3, Tp Lane, Sjp Rd Cross\", \"postalCode\": null, \"siteNpanxx\": \"91\", \"siteName\": \"IN BANGALORE\", \"regionCode\": null, \"swcClli\": null, \"siteId\": 10216903, \"state\": null, \"customerLocationClli\": null, \"design\": [ { \"portProtocol\": \"MLPPP\", \"accessSpeedUdfAttrId\": 30146, \"accessDetails\": { \"supplierName\": null, \"npanxx\": null, \"nrcListRate\": 199.6885, \"serialNumber\": \"100141402.001\", \"tokenId\": null, \"mrcListRate\": 47.9252, \"respAccessInterconnect\": 1, \"physicalInterface\": null, \"dqid\": null, \"portId\": 9274743, \"quoteId\": null, \"speed\": null, \"iglooMaxMrcDiscount\": null, \"respSpeed\": 2048, \"popClli\": null, \"respSupplierName\": \"ATT\", \"currencyCode\": \"USD\", \"respPopClli\": \"BNGRIIBB\" }, \"sitePopCilli\": null, \"portId\": 9274743, \"interface\": null, \"physicalInterfaceDesc\": null, \"referenceSiteId\": 10216903, \"lac\": null, \"accessSpeed\": \"4.096\", \"accessType\": \"Private Line\", \"accessTypeUdfAttrId\": 30155, \"portTypeUdfAttr\": 30155, \"accessArchitecture\": null, \"physicalInterfaceUdfAttr\": null, \"categoryLocalAccess\": null, \"portSpeed\": \"4.096M\", \"priceDetails\": [ { \"country\": \"IN\", \"componentType\": \"Port\", \"priceScenarioId\": 99999906168, \"secondaryKeys\": \"#FCC#CoS Package#Multimedia Standard Svc#4.096M#N/A#N/A#VPN Transport COS Package#per port#18270#18425#United States#US#USA\", \"quantity\": \"1\", \"priceType\": \"cosRCRateId\", \"localCurrency\": \"USD\", \"icbDesiredDiscPerc\": 50.0, \"rdsPriceType\": \"PORT\", \"frequency\": \"MRC\", \"beid\": \"18270\", \"nrcBeid\": \"18425\", \"componentParentId\": 10216903, \"reqPriceType\": null, \"productRateId\": 1305, \"term\": 36, \"elementType\": \"PortFeature\", \"uniqueId\": \"#CoS Package Multimedia Standard#CoS Package Multimedia Standard Svc - 4.096M#ATM#4.096Mbps#NxE1#VPN Transport#COS Package#Port\", \"localListPrice\": 0.0, \"referencePortId\": 9274743 } ], \"accessTailTechnology\": \"Frame/ATM (Gateway Interconnect/Leased Line)\", \"mileage\": null, \"siteType\": null } ] } ]";
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testProcessConfigDataFromCustomeRules() {
		List<NxLookupData> rulesData = new ArrayList<NxLookupData>();
		NxLookupData nxLookupData = new NxLookupData();
		nxLookupData.setCriteria("$..priceDetails.[?(@.elementType=='PortFeature')]%{\"countfilter\": {\"uniqueId\":\"$..uniqueId\"},\"_setaVPNPortFearuresArray_pf/items\": {\"UniqueId_PortFeatures_pf\": \"uniqueId\",\"New_PortFeatures_pf\":\"quantity\",\"_index\": \"index\"},\"AVPNPortFeatures_ArrayController_pf\": \"size\"}");
		rulesData.add(nxLookupData);
		Mockito.when(nxLookupDataRepository.findByDatasetNameAndItemIdAndDescription(anyString(), anyString(), anyString())).thenReturn(rulesData);
		LinkedHashMap<String, String> pirceData=new LinkedHashMap<String, String>();
		pirceData.put("l", "g");
		NexxusJsonUtility nj=new NexxusJsonUtility();
		List<Object>  designDatalst=new ArrayList<Object>();
		designDatalst.add(pirceData);
		Mockito.when(nexxusJsonUtility.getValueLst(any(),any())).thenReturn(designDatalst);
		LinkedHashMap criteriaMap=(LinkedHashMap<String, Object>) nj.convertStringJsonToMap("{\"_setaVPNPortFearuresArray_pf/items\": {\"UniqueId_PortFeatures_pf\": \"uniqueId\",\"New_PortFeatures_pf\":\"quantity\",\"_index\": \"index\"},\"AVPNPortFeatures_ArrayController_pf\": \"size\"}");
		when(nexxusJsonUtility.convertStringJsonToMap(Mockito.anyString())).thenReturn(criteriaMap);
		Integer grpId=1;
		when(restCommonUtil.generateGroupId(any(),any(),any(),any(),any())).thenReturn(grpId);
		configAndUpdateRestUtilFmo.processConfigDataFromCustomeRules(requestMap, inputDesign);
	}
	
	@Test
	public void testProcessCustomeFieldsUsingNxLookupData() {
		requestMap.put(MyPriceConstants.OFFER_NAME, "ADI/TDM");
		List<String> dataLst = new ArrayList<>();
		dataLst.add("30467");
		Mockito.when(jsonPathUtil.search(anyString(), anyString(), any())).thenReturn(dataLst);
		NxLookupData rulesData = new NxLookupData();
		rulesData.setCriteria("{\"int_Speed_LocalAccess_pf\":\"DS3\",\"int_Type_LocalAccess_pf\":\"Unprotected\",\"int_category_LocalAccess_pf\":\"LD DS3 OCx Access\",\"int_productType_LocalAccess_pf\":\"DS3 Schedule B/Regional\"}");
		Mockito.when(nxLookupDataRepository.findTopByDatasetNameAndItemId(anyString(), anyString())).thenReturn(rulesData);
		configAndUpdateRestUtilFmo.processCustomeFieldsUsingNxLookupData(requestMap, inputDesign);
	}
	
	@Test
	public void processBvoipTest() {
		inputDesign="{\"nxSiteId\":450576,\"zipCode\":\"60143\",\"country\":\"US\",\"fromCountryRegion\":\"US\",\"referenceOfferId\":7,\"isLineItemPicked\":\"Y\",\"city\":\"Itasca\",\"address1\":\"700 Hilltop Dr.\",\"siteNpanxx\":\"630773\",\"siteName\":\"ILLINOIS HQ\",\"nxKeyId\":\"US\",\"swcClli\":\"BNSVILBV\",\"design\":[{\"21303\":\"6\",\"21302\":\"6\",\"country\":\"US\",\"priceDetails\":[{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999908709,\"secondaryKeys\":\"#FCC#IP Flex Reach#VoIP Module Card#MIS T1 VoMIS12 - 12 or less CC\",\"quantity\":\"6\",\"lineItemId\":9585721,\"jurisdiction\":\"US\",\"nxItemId\":935541,\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":45.0,\"rdsPriceType\":\"Voice_Over_Ip\",\"frequency\":\"MRC\",\"component\":\"IP Flex Reach\",\"mpQuantity\":\"6\",\"componentParentId\":10120718,\"productRateId\":2405,\"term\":24,\"category\":\"Module Cards\",\"productType\":\"Module Cards\",\"uniqueId\":\"#FCC#Module Cards#IP Flex Reach#VoIP Module Card#MIS T1 VoMIS12 - 12 or less CC#0#Per Concurrent Call#US Nationwide\",\"localListPrice\":15.0,\"referencePortId\":9270377},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999908709,\"quantity\":\"6\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":85.0,\"rdsPriceType\":\"Voice_Over_Ip\",\"frequency\":\"MRC\",\"mpQuantity\":\"6\",\"componentParentId\":10120718,\"productRateId\":3897,\"term\":24,\"localListPrice\":2.0,\"referencePortId\":9270377},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999908709,\"secondaryKeys\":\"#FCC#IP Flex Reach#Calling Plan Setup#Setup fee#N\\/A#per site\",\"quantity\":\"1\",\"lineItemId\":9588592,\"jurisdiction\":\"US\",\"nxItemId\":908002,\"localCurrency\":\"USD\",\"type\":\"Setup fee\",\"icbDesiredDiscPerc\":65.0,\"rdsPriceType\":\"Voice_Over_Ip\",\"frequency\":\"NRC\",\"component\":\"IP Flex Reach\",\"mpQuantity\":\"1\",\"componentParentId\":10120718,\"reqPriceType\":\"Fixed\",\"productRateId\":2408,\"term\":24,\"category\":\"Product Services\",\"uniqueId\":\"#FCC#Product Services#IP Flex Reach#Calling Plan Setup#Setup fee#N\\/A#per site#US Nationwide\",\"productType\":\"Features\",\"localListPrice\":250.0,\"referencePortId\":9270377},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999908709,\"secondaryKeys\":\"#FCC#IP Flex Reach - AVPN#Calling Plan (Domestic)#Plan B#United States#per concurrent call\",\"quantity\":\"6\",\"lineItemId\":9588615,\"jurisdiction\":\"US\",\"priceType\":\"N\\/A\",\"nxItemId\":1981462,\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":55.0,\"rdsPriceType\":\"Voice_Over_Ip\",\"frequency\":\"MRC\",\"component\":\"IP Flex Reach - AVPN\",\"mpQuantity\":\"6\",\"componentParentId\":10120718,\"reqPriceType\":\"Fixed\",\"productRateId\":2409,\"term\":24,\"category\":\"Product Services\",\"uniqueId\":\"#FCC#Product Services#IP Flex Reach - AVPN#Calling Plan (Domestic)#Plan B#N\\/A#per concurrent call#US Nationwide\",\"productType\":\"Product Services\",\"localListPrice\":30.0,\"referencePortId\":9270377},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999908709,\"secondaryKeys\":\"#FCC#IP Flex Reach#Telephone Numbers#Standard numbers#N\\/A#per number\",\"quantity\":\"6\",\"lineItemId\":9588596,\"jurisdiction\":\"US\",\"nxItemId\":908006,\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":75.0,\"rdsPriceType\":\"Voice_Over_Ip\",\"frequency\":\"MRC\",\"component\":\"IP Flex Reach\",\"mpQuantity\":\"6\",\"componentParentId\":10120718,\"reqPriceType\":\"Fixed\",\"productRateId\":2410,\"term\":24,\"category\":\"Product Services\",\"uniqueId\":\"#FCC#Product Services#IP Flex Reach#Telephone Numbers#Standard numbers#N\\/A#per number#US Nationwide\",\"productType\":\"Features\",\"localListPrice\":0.3,\"referencePortId\":9270377}],\"concurrentCallType\":\"VoMIS12\",\"portId\":9270377,\"concurrentCall\":\"6\",\"referenceSiteId\":10120718}],\"siteId\":10120718,\"state\":\"IL\"}";
		requestMap.put(MyPriceConstants.OFFER_NAME, "BVoIP");
		List<NxLookupData> rulesData = new ArrayList<NxLookupData>();
		NxLookupData nxLookupData = new NxLookupData();
		nxLookupData.setCriteria("$..priceDetails.[?(@.productType=='Product Services' && @.jurisdiction=='US')]%{\"countfilter\": {\"uniqueId\":\"$..uniqueId\"}, \"_setbVoIPProductServicesArray/items\":{\"_index\":\"index\", \"bVoIPProductServicesUniqueID_pf\":\"uniqueId\",\"bVoIPProductServicesNew_pf\":\"mpQuantity\", \"bVoIPProductServicesSiteID_pf\":\"nxSiteIdList\"},\"bVoIPProductServicesArrayController_pf\":\"size\"}");
		rulesData.add(nxLookupData);
		Mockito.when(nxLookupDataRepository.findByDatasetNameAndItemIdAndDescription(anyString(), anyString(), anyString())).thenReturn(rulesData);
		LinkedHashMap<String, String> pirceData=new LinkedHashMap<String, String>();
		pirceData.put("l", "g");
		NexxusJsonUtility nj=new NexxusJsonUtility();
		List<Object>  designDatalst=new ArrayList<Object>();
		designDatalst.add(pirceData);
		Mockito.when(nexxusJsonUtility.getValueLst(any(),any())).thenReturn(designDatalst);
		LinkedHashMap criteriaMap=(LinkedHashMap<String, Object>) nj.convertStringJsonToMap("{\"_setbVoIPProductServicesArray/items\": {\"bVoIPProductServicesUniqueID_pf\": \"uniqueId\",\"bVoIPProductServicesNew_pf\":\"mpQuantity\",\"bVoIPProductServicesSiteID_pf\":\"nxSiteIdList\",\"_index\": \"index\"},\"bVoIPProductServicesArrayController_pf\": \"size\"}");
		when(nexxusJsonUtility.convertStringJsonToMap(Mockito.anyString())).thenReturn(criteriaMap);
		Integer grpId=1;
		when(restCommonUtil.generateGroupId(any(),any(),any(),any(),any())).thenReturn(grpId);
		configAndUpdateRestUtilFmo.processBvoip(requestMap, inputDesign, rulesData);;
	}
	
	@Test
	public void createSiteIdDataTest() {
		inputDesign="{\"nxSiteId\":450576,\"zipCode\":\"60143\",\"country\":\"US\",\"fromCountryRegion\":\"US\",\"referenceOfferId\":7,\"isLineItemPicked\":\"Y\",\"city\":\"Itasca\",\"address1\":\"700 Hilltop Dr.\",\"siteNpanxx\":\"630773\",\"siteName\":\"ILLINOIS HQ\",\"nxKeyId\":\"US\",\"swcClli\":\"BNSVILBV\",\"design\":[{\"21303\":\"6\",\"21302\":\"6\",\"country\":\"US\",\"priceDetails\":[{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999908709,\"secondaryKeys\":\"#FCC#IP Flex Reach#VoIP Module Card#MIS T1 VoMIS12 - 12 or less CC\",\"quantity\":\"6\",\"lineItemId\":9585721,\"jurisdiction\":\"US\",\"nxItemId\":935541,\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":45.0,\"rdsPriceType\":\"Voice_Over_Ip\",\"frequency\":\"MRC\",\"component\":\"IP Flex Reach\",\"mpQuantity\":\"6\",\"componentParentId\":10120718,\"productRateId\":2405,\"term\":24,\"category\":\"Module Cards\",\"productType\":\"Module Cards\",\"uniqueId\":\"#FCC#Module Cards#IP Flex Reach#VoIP Module Card#MIS T1 VoMIS12 - 12 or less CC#0#Per Concurrent Call#US Nationwide\",\"localListPrice\":15.0,\"referencePortId\":9270377},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999908709,\"quantity\":\"6\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":85.0,\"rdsPriceType\":\"Voice_Over_Ip\",\"frequency\":\"MRC\",\"mpQuantity\":\"6\",\"componentParentId\":10120718,\"productRateId\":3897,\"term\":24,\"localListPrice\":2.0,\"referencePortId\":9270377},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999908709,\"secondaryKeys\":\"#FCC#IP Flex Reach#Calling Plan Setup#Setup fee#N\\/A#per site\",\"quantity\":\"1\",\"lineItemId\":9588592,\"jurisdiction\":\"US\",\"nxItemId\":908002,\"localCurrency\":\"USD\",\"type\":\"Setup fee\",\"icbDesiredDiscPerc\":65.0,\"rdsPriceType\":\"Voice_Over_Ip\",\"frequency\":\"NRC\",\"component\":\"IP Flex Reach\",\"mpQuantity\":\"1\",\"componentParentId\":10120718,\"reqPriceType\":\"Fixed\",\"productRateId\":2408,\"term\":24,\"category\":\"Product Services\",\"uniqueId\":\"#FCC#Product Services#IP Flex Reach#Calling Plan Setup#Setup fee#N\\/A#per site#US Nationwide\",\"productType\":\"Features\",\"localListPrice\":250.0,\"referencePortId\":9270377},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999908709,\"secondaryKeys\":\"#FCC#IP Flex Reach - AVPN#Calling Plan (Domestic)#Plan B#United States#per concurrent call\",\"quantity\":\"6\",\"lineItemId\":9588615,\"jurisdiction\":\"US\",\"priceType\":\"N\\/A\",\"nxItemId\":1981462,\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":55.0,\"rdsPriceType\":\"Voice_Over_Ip\",\"frequency\":\"MRC\",\"component\":\"IP Flex Reach - AVPN\",\"mpQuantity\":\"6\",\"componentParentId\":10120718,\"reqPriceType\":\"Fixed\",\"productRateId\":2409,\"term\":24,\"category\":\"Product Services\",\"uniqueId\":\"#FCC#Product Services#IP Flex Reach - AVPN#Calling Plan (Domestic)#Plan B#N\\/A#per concurrent call#US Nationwide\",\"productType\":\"Product Services\",\"localListPrice\":30.0,\"referencePortId\":9270377},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999908709,\"secondaryKeys\":\"#FCC#IP Flex Reach#Telephone Numbers#Standard numbers#N\\/A#per number\",\"quantity\":\"6\",\"lineItemId\":9588596,\"jurisdiction\":\"US\",\"nxItemId\":908006,\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":75.0,\"rdsPriceType\":\"Voice_Over_Ip\",\"frequency\":\"MRC\",\"component\":\"IP Flex Reach\",\"mpQuantity\":\"6\",\"componentParentId\":10120718,\"reqPriceType\":\"Fixed\",\"productRateId\":2410,\"term\":24,\"category\":\"Product Services\",\"uniqueId\":\"#FCC#Product Services#IP Flex Reach#Telephone Numbers#Standard numbers#N\\/A#per number#US Nationwide\",\"productType\":\"Features\",\"localListPrice\":0.3,\"referencePortId\":9270377}],\"concurrentCallType\":\"VoMIS12\",\"portId\":9270377,\"concurrentCall\":\"6\",\"referenceSiteId\":10120718}],\"siteId\":10120718,\"state\":\"IL\"}";
		StringBuilder sb=new StringBuilder();
		sb.append("gh");
		requestMap.put("abc", sb);
		Mockito.when(nexxusJsonUtility.getValue(any(),any())).thenReturn("hjj");
		configAndUpdateRestUtilFmo.createSiteIdData(requestMap, "abc", "10120718", inputDesign);
	}
	
	@Test
	public void collectReqElementTypeTest() {
		LinkedHashMap<String, String> pirceData=new LinkedHashMap<String, String>();
		pirceData.put("elementType", "g");
		configAndUpdateRestUtilFmo.collectReqElementType(requestMap, pirceData);
	}

}
