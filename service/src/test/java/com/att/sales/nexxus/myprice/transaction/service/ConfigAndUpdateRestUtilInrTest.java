package com.att.sales.nexxus.myprice.transaction.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.CustomJsonConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxMpConfigJsonMapping;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.util.NexxusJsonUtility;

@SuppressWarnings({ "rawtypes", "unchecked" })
@ExtendWith(MockitoExtension.class)

public class ConfigAndUpdateRestUtilInrTest {
	@Spy
	@InjectMocks
	private ConfigAndUpdateRestUtilInr configAndUpdateRestUtilInr;

	@Mock
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Mock
	private NexxusJsonUtility nexxusJsonUtility;
	
	@Mock
	private RestCommonUtil restCommonUtil;

	
	@Test
	public void processConfigDataFromCustomeRulesTest() throws SalesBusinessException {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		requestMap.put(MyPriceConstants.OFFER_NAME, "AVPN");
		requestMap.put(StringConstants.PRICE_SCENARIO_ID, "99999910531");
		List<NxLookupData> rulesData=new ArrayList<NxLookupData>();
		NxLookupData d=new NxLookupData();
		d.setId("ASE");
		d.setDescription("PD");
		d.setCriteria("$..priceAttributes.[?((@.priceScenarioId == %priceScenarioId%)  && @.rateGroup in [\"OEM_Port\",\"ICO_MEETPOINT\",\"MEET_POINT\","
				+ "\"ALTSWITCH\",\"OEM_Interactive\",\"OEM_MMH\",\"OEM_MMS\",\"OEM_BusinessH_OOR\",\"OEM_EFC\"])]##{\"_settelcoBillingElements_pf/items\": "
				+ "{\"uSOC_pf\": \"usocId\",\"newQty_telcoBillingElementArray_pf\": \"New\",\"existingQty_telcoBillingElementArray_pf\":\"Existing\","
				+ "\"migrationQty_telcoBillingElementArray_pf\":\"Migration\",\"_index\": \"index\"},\"telcoBillingElementArrayController_pf\": \"size\"}");
		rulesData.add(d);
		when(nxLookupDataRepository.findByDatasetNameAndItemIdAndDescription(any(),any(),any())).thenReturn(rulesData);
		LinkedHashMap<String, String> pirceData=new LinkedHashMap<String, String>();
		pirceData.put("l", "g");
		NexxusJsonUtility nj=new NexxusJsonUtility();
		List<Object>  designDatalst=new ArrayList<Object>();
		designDatalst.add(pirceData);
		when(nexxusJsonUtility.getValueLst(any(),any())).thenReturn(designDatalst);
		LinkedHashMap criteriaMap=(LinkedHashMap<String, Object>) nj.convertStringJsonToMap("{\"_settelcoBillingElements_pf/items\": "
				+ "{\"uSOC_pf\": \"usocId\",\"newQty_telcoBillingElementArray_pf\": \"New\",\"existingQty_telcoBillingElementArray_pf\":\"Existing\","
				+ "\"migrationQty_telcoBillingElementArray_pf\":\"Migration\",\"_index\": \"index\"},\"telcoBillingElementArrayController_pf\": \"size\"}");
		when(nexxusJsonUtility.convertStringJsonToMap(Mockito.anyString())).thenReturn(criteriaMap);
		Integer grpId=1;
		when(restCommonUtil.generateGroupId(any(),any(),any(),any(),any())).thenReturn(grpId);
		configAndUpdateRestUtilInr.processConfigDataFromCustomeRules(requestMap, "{}");
	}
	
	@Test
	public void collectReqElementTypeTest() {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		LinkedHashMap<String, String> pirceData=new LinkedHashMap<String, String>();
		pirceData.put("elementType", "g");
		configAndUpdateRestUtilInr.collectReqElementType(requestMap, pirceData);
	}
	
	@Test
	public void processCustomFieldsTest() {
		NxMpConfigJsonMapping mappingData=new NxMpConfigJsonMapping();
		mappingData.setOffer(MyPriceConstants.ETHERNET);
		mappingData.setKey(MyPriceConstants.SPEED_LOCAL_ACCESS_PF);
		Map<String, Object> requestMap=new HashMap<String, Object>();
		String inputData="1 GBPS";
		when(restCommonUtil.getItemValueUsingJsonPath(any(),any(),any())).thenReturn(inputData);
		configAndUpdateRestUtilInr.processCustomFields(mappingData, "{}", requestMap, String.class);
	}
	
	@Test
	public void processCustomFieldsTest1() {
		NxMpConfigJsonMapping mappingData=new NxMpConfigJsonMapping();
		mappingData.setOffer(MyPriceConstants.ETHERNET);
		mappingData.setKey(MyPriceConstants.SPEED_LOCAL_ACCESS_PF);
		Map<String, Object> requestMap=new HashMap<String, Object>();
		String inputData="100 GBPS";
		when(restCommonUtil.getItemValueUsingJsonPath(any(),any(),any())).thenReturn(inputData);
		configAndUpdateRestUtilInr.processCustomFields(mappingData, "{}", requestMap, String.class);
	}
	
	@Test
	public void processCustomFieldsTest2() {
		NxMpConfigJsonMapping mappingData=new NxMpConfigJsonMapping();
		mappingData.setOffer(MyPriceConstants.ETHERNET);
		mappingData.setKey(MyPriceConstants.INT_SPEED_LOCAL_ACCESS_PF);
		Map<String, Object> requestMap=new HashMap<String, Object>();
		String inputData="1 MBPS";
		when(restCommonUtil.getItemValueUsingJsonPath(any(),any(),any())).thenReturn(inputData);
		configAndUpdateRestUtilInr.processCustomFields(mappingData, "{}", requestMap, String.class);
	}
	
	@Test
	public void processCustomFieldsTest3() {
		NxMpConfigJsonMapping mappingData=new NxMpConfigJsonMapping();
		mappingData.setOffer(MyPriceConstants.ETHERNET);
		mappingData.setKey(MyPriceConstants.INT_SPEED_LOCAL_ACCESS_PF);
		Map<String, Object> requestMap=new HashMap<String, Object>();
		String inputData="1 KBPS";
		when(restCommonUtil.getItemValueUsingJsonPath(any(),any(),any())).thenReturn(inputData);
		configAndUpdateRestUtilInr.processCustomFields(mappingData, "{}", requestMap, String.class);
	}
	
	@Test
	public void processCustomFieldsTest4() {
		NxMpConfigJsonMapping mappingData=new NxMpConfigJsonMapping();
		mappingData.setOffer(MyPriceConstants.ETHERNET);
		mappingData.setKey(MyPriceConstants.INT_SPEED_LOCAL_ACCESS_PF);
		Map<String, Object> requestMap=new HashMap<String, Object>();
		String inputData="1 g";
		when(restCommonUtil.getItemValueUsingJsonPath(any(),any(),any())).thenReturn(inputData);
		configAndUpdateRestUtilInr.processCustomFields(mappingData, "{}", requestMap, String.class);
	}
	
	@Test
	public void processCustomFieldsTest5() {
		NxMpConfigJsonMapping mappingData=new NxMpConfigJsonMapping();
		mappingData.setOffer(MyPriceConstants.ETHERNET);
		mappingData.setKey(MyPriceConstants.INT_SPEED_LOCAL_ACCESS_PF);
		Map<String, Object> requestMap=new HashMap<String, Object>();
		configAndUpdateRestUtilInr.processCustomFields(mappingData, "{}", requestMap, String.class);
	}
	
	@Test
	public void processCustomFieldsTest6() {
		NxMpConfigJsonMapping mappingData=new NxMpConfigJsonMapping();
		mappingData.setOffer(MyPriceConstants.TDM);
		mappingData.setKey(MyPriceConstants.INT_SPEED_LOCAL_ACCESS_PF);
		mappingData.setDatasetName("hj");
		Map<String, Object> requestMap=new HashMap<String, Object>();
		String inputData="1 mb";
		when(restCommonUtil.getItemValueUsingJsonPath(any(),any(),any())).thenReturn(inputData);
		configAndUpdateRestUtilInr.processCustomFields(mappingData, "{}", requestMap, String.class);
	}
	
	@Test
	public void processCustomFieldsTest7() {
		NxMpConfigJsonMapping mappingData=new NxMpConfigJsonMapping();
		mappingData.setOffer(MyPriceConstants.TDM);
		mappingData.setKey(MyPriceConstants.INT_SPEED_LOCAL_ACCESS_PF);
		mappingData.setDatasetName("hj");
		Map<String, Object> requestMap=new HashMap<String, Object>();
		configAndUpdateRestUtilInr.processCustomFields(mappingData, "{}", requestMap, String.class);
	}
	
	@Test
	public void processCustomFieldsTest8() {
		NxMpConfigJsonMapping mappingData=new NxMpConfigJsonMapping();
		mappingData.setKey("A");
		Map<String, Object> requestMap=new HashMap<String, Object>();
		requestMap.put("A", "k");
		configAndUpdateRestUtilInr.processCustomFields(mappingData, "{}", requestMap, String.class);
	}
	
	@Test
	public void processConfigDataFromCustomeRulesAniraTest() throws SalesBusinessException {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		requestMap.put(MyPriceConstants.OFFER_NAME, "ANIRA");
		requestMap.put(StringConstants.PRICE_SCENARIO_ID, "99999910531");
		List<NxLookupData> rulesData=new ArrayList<NxLookupData>();
		NxLookupData d=new NxLookupData();
		d.setId("ASE");
		d.setDescription("PD");
		d.setCriteria("$..priceAttributes.[?((@.priceScenarioId == %priceScenarioId%)  && @.rateGroup in [\"OEM_Port\",\"ICO_MEETPOINT\",\"MEET_POINT\","
				+ "\"ALTSWITCH\",\"OEM_Interactive\",\"OEM_MMH\",\"OEM_MMS\",\"OEM_BusinessH_OOR\",\"OEM_EFC\"])]##{\"_settelcoBillingElements_pf/items\": "
				+ "{\"uSOC_pf\": \"usocId\",\"newQty_telcoBillingElementArray_pf\": \"New\",\"existingQty_telcoBillingElementArray_pf\":\"Existing\","
				+ "\"migrationQty_telcoBillingElementArray_pf\":\"Migration\",\"_index\": \"index\"},\"telcoBillingElementArrayController_pf\": \"size\"}");
		rulesData.add(d);
		when(nxLookupDataRepository.findByDatasetNameAndItemIdAndDescription(any(),any(),any())).thenReturn(rulesData);
		LinkedHashMap<String, String> pirceData=new LinkedHashMap<String, String>();
		pirceData.put("l", "g");
		NexxusJsonUtility nj=new NexxusJsonUtility();
		List<Object>  designDatalst=new ArrayList<Object>();
		designDatalst.add(pirceData);
		when(nexxusJsonUtility.getValueLst(any(),any())).thenReturn(designDatalst);
		LinkedHashMap criteriaMap=(LinkedHashMap<String, Object>) nj.convertStringJsonToMap("{\"countfilter\": {\"uniqueId\":\"$..uniqueId\"},\"_setANIRAFeaturesArray_pf/items\": "
				+ "{\"aNIRAFeaturesUniqueID_pf\": \"uniqueId\",\"existingMRC_ANIRAFeatures_pf\": \"sohoMrc\",\"ANIRACategoryFeatures_Existing_pf\":"
				+ "\"count\",\"_index\": \"index\"},\"ANIRAFeaturesArrayController_pf\": \"size\"}");
		when(nexxusJsonUtility.convertStringJsonToMap(Mockito.anyString())).thenReturn(criteriaMap);
		Integer grpId=1;
		when(restCommonUtil.generateGroupId(any(),any(),any(),any(),any())).thenReturn(grpId);
		configAndUpdateRestUtilInr.processConfigDataFromCustomeRules(requestMap, "{}");
	}
	
	
	@Test
	public void processConfigDataFromCustomeRulesUsageTestString() throws SalesBusinessException {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		requestMap.put(MyPriceConstants.OFFER_NAME, "OneNet");
		List<NxLookupData> rulesData=new ArrayList<NxLookupData>();
		NxLookupData d=new NxLookupData();
		d.setId("OneNet");
		d.setCriteria(
				"$..usageDetails.[?(@.nxGeoCode=='OneNet Interstate Voice') ]%{ \"countfilter\": { \"uniqueId\": \"$..uniqueId\" }, \"_setoneNetInterstateArraySet_pf/items\": { \"OneNet_InterstateVoice_Existing_pf\": \"result::minutesCount/12::round:2##evaluate##minutesCount##minutes\", \"uniqueID_INT_OneNet_InterState_pf\": \"uniqueId\", \"existingMRC_OneNet_InterstateVoice_pf\": \"step1::((60/addlPeriodDefn)*addlPeriodRate)::round:4&&result::step1*((1-sdnOneNetNetDiscount)*(1-sdnOneNetSuppDiscount))::round:4##evaluate##addlPeriodDefn,addlPeriodRate,sdnOneNetNetDiscount,sdnOneNetSuppDiscount##mrc\", "
						+ "\"_index\": \"index\" }, \"oneNetInterstateVoiceArrayController_pf\": \"size\" }");
		d.setDescription("INR");
		when(nxLookupDataRepository.findByDatasetNameAndItemIdAndDescription(eq(CustomJsonConstants.CUSTOM_CONFIG_RULES_REST_STRING),any(),any())).thenReturn(rulesData);
		LinkedHashMap<String, String> pirceData=new LinkedHashMap<String, String>();
		pirceData.put("uniqueId", "uniqueId");
		pirceData.put("minutesCount", "20");
		pirceData.put("addlPeriodDefn", "6");
		pirceData.put("addlPeriodRate", "0.04");
		pirceData.put("sdnOneNetNetDiscount","0.45");
		pirceData.put("sdnOneNetSuppDiscount","0");
				NexxusJsonUtility nj=new NexxusJsonUtility();
		List<Object>  designDatalst=new ArrayList<Object>();
		designDatalst.add(pirceData);
		when(nexxusJsonUtility.getValueLst(any(),any())).thenReturn(designDatalst);
		LinkedHashMap criteriaMap = (LinkedHashMap<String, Object>) nj.convertStringJsonToMap(
				"{ \"countfilter\": { \"uniqueId\": \"$..uniqueId\" }, \"_setoneNetInterstateArraySet_pf/items\": { \"OneNet_InterstateVoice_Existing_pf\": \"result::minutesCount/12::round:2##evaluate##minutesCount##minutes\", \"uniqueID_INT_OneNet_InterState_pf\": \"uniqueId\", \"existingMRC_OneNet_InterstateVoice_pf\": \"step1::((60/addlPeriodDefn)*addlPeriodRate)::round:4&&result::step1*((1-sdnOneNetNetDiscount)*(1-sdnOneNetSuppDiscount))::round:4##evaluate##addlPeriodDefn,addlPeriodRate,sdnOneNetNetDiscount,sdnOneNetSuppDiscount##mrc\", \"_index\": \"index\" }, \"oneNetInterstateVoiceArrayController_pf\": \"size\" }");
		when(nexxusJsonUtility.convertStringJsonToMap(Mockito.anyString())).thenReturn(criteriaMap);
		Integer grpId=1;
		when(restCommonUtil.generateGroupId(any(),any(),any(),any(),any())).thenReturn(grpId);
		
		rulesData.add(d);
		configAndUpdateRestUtilInr.processConfigDataFromCustomeRules(requestMap, "{}");
	}
	
	@Test
	public void processConfigDataFromCustomeRulesBVoIPNonUsageTest() throws SalesBusinessException {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		requestMap.put(MyPriceConstants.OFFER_NAME, "BVoIP Non-Usage");
		List<NxLookupData> rulesData=new ArrayList<NxLookupData>();
		NxLookupData d=new NxLookupData();
		d.setId("BVoIP Non-Usage");
		d.setCriteria(
				"$..featureDetails.[?(@.productType=='Product Services' && @.jurisdiction=='MOW' && @.elementType == "
				+ "'IP Flex Reach - AVPN')]%{ \"countfilter\": { \"uniqueId\": \"$..uniqueId\" },\"_setbVoIPProductServicesArray/items\":{\"bVoIPProductServicesUniqueID_pf\":\"uniqueId\",\"bVoIPProductServicesExisting_pf\": \"inrQty##type##minutes\",\"bVoIPProductServicesExistingIntermediate_pf\": \"genericQuantity##type##totalMinutes\",\"existingMRC_bVoIPProductServices_pf\":\"netAmount##type##mrc\",\"_index\":\"index\"},\"bVoIPProductServicesArrayController_pf\":\"size\"}");
		d.setDescription("INR");
		when(nxLookupDataRepository.findByDatasetNameAndItemIdAndDescription(eq(CustomJsonConstants.CUSTOM_CONFIG_RULES_REST),any(),any())).thenReturn(rulesData);
		LinkedHashMap<String, String> pirceData=new LinkedHashMap<String, String>();
		pirceData.put("uniqueId", "uniqueId");
		pirceData.put("genericQuantity","2");
		pirceData.put("inrQty","1");
		pirceData.put("netAmount","10");
		
		NexxusJsonUtility nj=new NexxusJsonUtility();
		List<Object>  designDatalst=new ArrayList<Object>();
		designDatalst.add(pirceData);
		when(nexxusJsonUtility.getValueLst(any(),any())).thenReturn(designDatalst);
		LinkedHashMap criteriaMap = (LinkedHashMap<String, Object>) nj.convertStringJsonToMap(
				"{ \"countfilter\": { \"uniqueId\": \"$..uniqueId\" },\"_setbVoIPProductServicesArray/items\":{\"bVoIPProductServicesUniqueID_pf\":\"uniqueId\",\"bVoIPProductServicesExisting_pf\": \"inrQty##type##minutes\",\"bVoIPProductServicesExistingIntermediate_pf\": \"genericQuantity##type##totalMinutes\",\"existingMRC_bVoIPProductServices_pf\":\"netAmount##type##mrc\",\"_index\":\"index\"},"
				+ "\"bVoIPProductServicesArrayController_pf\":\"size\"}");
		when(nexxusJsonUtility.convertStringJsonToMap(Mockito.anyString())).thenReturn(criteriaMap);
		Integer grpId=1;
		when(restCommonUtil.generateGroupId(any(),any(),any(),any(),any())).thenReturn(grpId);
		
		rulesData.add(d);
		configAndUpdateRestUtilInr.processConfigDataFromCustomeRules(requestMap, "{}");
	}
	@Test
	public void processConfigDataFromCustomeRulesFeatureTest() throws SalesBusinessException {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		requestMap.put(MyPriceConstants.OFFER_NAME, "OneNet Feature");
		List<NxLookupData> rulesData=new ArrayList<NxLookupData>();
		NxLookupData d=new NxLookupData();
		d.setId("OneNet Feature");
		d.setCriteria(
				"$..featureDetails.[*]%{ \"countfilter\": { \"nxKeyId\": \"$..nxKeyId\" },\"_setoneNetFeaturesArraySet/items\":{\"OneNetFeaturesUniqueID_pf\":\"uniqueId\",\"OneNetFeatures_existingMinutes_pf\": \"result::featureCount/12::round:2##evaluate##featureCount##minutes\",\"OneNetFeaturesExistingMRC_pf\":\"result::perInstanceRate::round:2##evaluate##perInstanceRate##mrc\",\"_index\":\"index\"},\"oneNetFeaturesArrayController\":\"size\"}");
		d.setDescription("INR");
		when(nxLookupDataRepository.findByDatasetNameAndItemIdAndDescription(eq(CustomJsonConstants.CUSTOM_CONFIG_RULES_REST),any(),any())).thenReturn(rulesData);
		LinkedHashMap<String, String> pirceData=new LinkedHashMap<String, String>();
		pirceData.put("nxKeyId", "nxKeyId");
		pirceData.put("genericQuantity","2");
		pirceData.put("inrQty","1");
		pirceData.put("netAmount","10");
		
		NexxusJsonUtility nj=new NexxusJsonUtility();
		List<Object>  designDatalst=new ArrayList<Object>();
		designDatalst.add(pirceData);
		when(nexxusJsonUtility.getValueLst(any(),any())).thenReturn(designDatalst);
		LinkedHashMap criteriaMap = (LinkedHashMap<String, Object>) nj.convertStringJsonToMap(
				"{ \"countfilter\": { \"nxKeyId\": \"$..nxKeyId\" },\"_setoneNetFeaturesArraySet/items\":{\"OneNetFeaturesUniqueID_pf\":\"uniqueId\",\"OneNetFeatures_existingMinutes_pf\": \"result::featureCount/12::round:2##evaluate##featureCount##minutes\",\"OneNetFeaturesExistingMRC_pf\": \"result::perInstanceRate::round:2##evaluate##perInstanceRate##mrc\",\"_index\":\"index\"},"
				+ "\"oneNetFeaturesArrayController\":\"size\"}");
		when(nexxusJsonUtility.convertStringJsonToMap(Mockito.anyString())).thenReturn(criteriaMap);
		Integer grpId=1;
		when(restCommonUtil.generateGroupId(any(),any(),any(),any(),any())).thenReturn(grpId);
		
		rulesData.add(d);
		configAndUpdateRestUtilInr.processConfigDataFromCustomeRulesOneNetFeatureProduct(requestMap,rulesData, "{}","OneNet Feature", false);
	}
	@Test
	public void processConfigDataFromCustomeRulesUsageProductBvoip() {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		List<NxLookupData> rulesData=new ArrayList<NxLookupData>();
		NxLookupData d=new NxLookupData();
		d.setId("BVoIP");
		d.setCriteria("$..[?(@.productType=='Usage' && @.jurisdiction=='US' && @.category=='BVoIP Intl IPTF Inbound' )]%{\"countfilter\":{\"uniqueId\":\"$..uniqueId\"},"
				+ "\"_setinternationalIPTollFreeInboundUsageArraySet/items\":{\"uniqueID_INT_IntlIPTollFreeUsage_pf\":\"uniqueId\",\"existingMRC_IntlIPTollFreeUsage_pf\":"
				+ " \"step1::((60/initialPeriodDefinition)*initialPeriodRate)::round:4&&result::step1*(1-(discount*0.01))::round:4##evaluate##initialPeriodDefinition,initialPeriodRate,"
				+ "discount##mrc\",\"BVoIP_Existing_InternationalIPTollFreeUsage_pf\": \"result::totalQuantityAnnual/60/12::round:-1##evaluate##totalQuantityAnnual##minutes\","
				+ "\"_index\":\"index\"},\"internationalIPTollFreeInboundUsageArrayController_pf\":\"size\"}");
		d.setDescription("INR");
		rulesData.add(d);
		when(nxLookupDataRepository.findByDatasetNameAndItemIdAndDescription(eq(CustomJsonConstants.CUSTOM_CONFIG_RULES_REST),any(),any())).thenReturn(rulesData);
		LinkedHashMap<String, String> pirceData=new LinkedHashMap<String, String>();
		pirceData.put("uniqueId", "uniqueId");
		pirceData.put("genericQuantity","2");
		pirceData.put("inrQty","1");
		pirceData.put("netAmount","10");
		
		NexxusJsonUtility nj=new NexxusJsonUtility();
		List<Object>  designDatalst=new ArrayList<Object>();
		designDatalst.add(pirceData);
		when(nexxusJsonUtility.getValueLst(any(),any())).thenReturn(designDatalst);
		requestMap.put(MyPriceConstants.OFFER_NAME, "BVoIP");
		LinkedHashMap criteriaMap = (LinkedHashMap<String, Object>) nj.convertStringJsonToMap("{\"countfilter\":{\"uniqueId\":\"$..uniqueId\"},"
				+ "\"_setinternationalIPTollFreeInboundUsageArraySet/items\":{\"uniqueID_INT_IntlIPTollFreeUsage_pf\":\"uniqueId\",\"existingMRC_IntlIPTollFreeUsage_pf\":"
				+ " \"step1::((60/initialPeriodDefinition)*initialPeriodRate)::round:4&&result::step1*(1-(discount*0.01))::round:4##evaluate##initialPeriodDefinition,initialPeriodRate,"
				+ "discount##mrc\",\"BVoIP_Existing_InternationalIPTollFreeUsage_pf\": \"result::totalQuantityAnnual/60/12::round:-1##evaluate##totalQuantityAnnual##minutes\","
				+ "\"_index\":\"index\"},\"internationalIPTollFreeInboundUsageArrayController_pf\":\"size\"}");
		when(nexxusJsonUtility.convertStringJsonToMap(Mockito.anyString())).thenReturn(criteriaMap);
		//when(restCommonUtil.fillAndSetList(Mockito.anyString())).thenReturn(criteriaMap);
		Map<String,Object> innerData=new HashMap<String, Object>();
		innerData.put("uniqueID_INT_IntlIPTollFreeUsage_pf", "123");
		List<Map<String,Object>> lst=new ArrayList<Map<String,Object>>();
		lst.add(innerData);
		configAndUpdateRestUtilInr.processConfigDataFromCustomeRulesUsageProduct(requestMap, rulesData, "{}", "BVoIP", false);
	}
	

	@Test
	public void processConfigDataFromCustomeRulesUsageProductBvoip2() {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		List<NxLookupData> rulesData=new ArrayList<NxLookupData>();
		NxLookupData d=new NxLookupData();
		d.setId("BVoIP");
		d.setCriteria("$..[?(@.productType=='Usage' && @.jurisdiction=='US' && @.category=='BVoIP Intl IPTF Inbound' )]%{\"countfilter\":{\"uniqueId\":\"$..uniqueId\"},"
				+ "\"_setinternationalIPTollFreeInboundUsageArraySet/items\":{\"uniqueID_INT_IntlIPTollFreeUsage_pf\":\"uniqueId\",\"existingMRC_IntlIPTollFreeUsage_pf\":"
				+ " \"step1::((60/initialPeriodDefinition)*initialPeriodRate)::round:4&&result::step1*(1-(discount*0.01))::round:4##evaluate##initialPeriodDefinition,initialPeriodRate,"
				+ "discount##mrc\",\"BVoIP_Existing_InternationalIPTollFreeUsage_pf\": \"result::totalQuantityAnnual/60/12::round:-1##evaluate##totalQuantityAnnual##minutes\","
				+ "\"_index\":\"index\"},\"internationalIPTollFreeInboundUsageArrayController_pf\":\"size\"}");
		d.setDescription("INR");
		rulesData.add(d);
		when(nxLookupDataRepository.findByDatasetNameAndItemIdAndDescription(eq(CustomJsonConstants.CUSTOM_CONFIG_RULES_REST),any(),any())).thenReturn(rulesData);
		LinkedHashMap<String, String> pirceData=new LinkedHashMap<String, String>();
		pirceData.put("uniqueId", "uniqueId");
		pirceData.put("genericQuantity","2");
		pirceData.put("inrQty","1");
		pirceData.put("netAmount","10");
		
		NexxusJsonUtility nj=new NexxusJsonUtility();
		List<Object>  designDatalst=new ArrayList<Object>();
		designDatalst.add(pirceData);
		when(nexxusJsonUtility.getValueLst(any(),any())).thenReturn(designDatalst);
		requestMap.put(MyPriceConstants.OFFER_NAME, "BVoIP");
		LinkedHashMap criteriaMap = (LinkedHashMap<String, Object>) nj.convertStringJsonToMap("{\"countfilter\":{\"uniqueId\":\"$..uniqueId\"},"
				+ "\"_setinternationalIPTollFreeInboundUsageArraySet/items\":{\"uniqueID_INT_IntlIPTollFreeUsage_pf\":\"uniqueId\",\"existingMRC_IntlIPTollFreeUsage_pf\":"
				+ " \"step1::((60/initialPeriodDefinition)*initialPeriodRate)::round:4&&result::step1*(1-(discount*0.01))::round:4##evaluate##initialPeriodDefinition,initialPeriodRate,"
				+ "discount##mrc\",\"BVoIP_Existing_InternationalIPTollFreeUsage_pf\": \"result::totalQuantityAnnual/60/12::round:-1##evaluate##totalQuantityAnnual##minutes\","
				+ "\"_index\":\"index\"},\"internationalIPTollFreeInboundUsageArrayController_pf\":\"size\"}");
		when(nexxusJsonUtility.convertStringJsonToMap(Mockito.anyString())).thenReturn(criteriaMap);
		//when(restCommonUtil.fillAndSetList(Mockito.anyString())).thenReturn(criteriaMap);
		Map<String,Object> innerData=new HashMap<String, Object>();
		innerData.put("uniqueID_INT_IntlIPTollFreeUsage_pf", "123");
		List<Map<String,Object>> lst=new ArrayList<Map<String,Object>>(Collections.nCopies(1, null));
		lst.set(0,innerData);
		requestMap.put("_setinternationalIPTollFreeInboundUsageArraySet/items", lst);
		when(restCommonUtil.hasIndex(anyInt(),anyList())).thenReturn(true);
		configAndUpdateRestUtilInr.processConfigDataFromCustomeRulesUsageProduct(requestMap, rulesData, "{}", "BVoIP", false);
	}
	
	@Test
	public void handleDesignForCustomCode() {
		configAndUpdateRestUtilInr.handleDesignForCustomCode("[{}]");
	}
	
}
