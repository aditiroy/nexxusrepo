package com.att.sales.nexxus.myprice.transaction.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.constant.CustomJsonConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxMpConfigJsonMapping;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxLineItemProcessingDao;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.reteriveicb.model.ComponentDetails;
import com.att.sales.nexxus.reteriveicb.model.PriceAttributes;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;

@SuppressWarnings({ "rawtypes", "unchecked" })
@ExtendWith(MockitoExtension.class)

public class ConfigAndUpdateRestUtilPdTest {

	@Spy
	@InjectMocks
	private ConfigAndUpdateRestUtilPd configAndUpdateRestUtilPd;
	
	@Mock
	private NxDesignAuditRepository nxDesignAuditRepository;
	
	@Mock
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Mock
	private NexxusJsonUtility nexxusJsonUtility;
	
	@Mock
	private JsonPathUtil jsonPathUtil;
	
	@Mock
	private NxLineItemProcessingDao lineItemProcessingDao;
	
	@Mock
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	
	@Mock
	private ConfigAndUpdatePricingUtil configAndUpdatePricingUtil;
	
	@Mock
	private RestCommonUtil restCommonUtil;
	
	
	@Test
	public void processInputDesignTest() {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		requestMap.put(MyPriceConstants.AUTOMATION_IND, true);
		JSONObject designDetails=new JSONObject();
		designDetails.put("siteId", 12);
		NxDesignAudit nxDesignAudit=new NxDesignAudit();
		String json="{\"solution\":{\"solutionDeterminants\":{\"icbInd\":null,\"solutionType\":\"NS\",\"mvlInd\":null},\"bulkInd\":\"N\","
				+ "\"bundleCode\":\"ADE\",\"offers\":[{\"site\":[],\"offerId\":\"120\"}],\"leadDesignID\":6495653,\"marketStrata\":\"Wholesale\",\"cancellationReason\":null,"
				+ "\"pricerDSolutionId\":6495653,\"automationInd\":\"Y\",\"erateInd\":\"Y\",\"layer\":\"SLED\",\"solutionStatus\":\"N\"},"
				+ "\"actionDeterminants\":[{\"component\":[\"Design\",\"Price\",\"ADE\"],\"activity\":\"UpdateDesign\"}]}";
		when(jsonPathUtil.set(any(),any(),any(),any())).thenReturn(json);
		nxDesignAudit.setData(json);
		when(nxDesignAuditRepository.findByNxRefIdAndTransaction(any(),any())).thenReturn(nxDesignAudit);
		Object marketStrataObj="retails";
		when(nexxusJsonUtility.getValue(any(),any())).thenReturn(marketStrataObj);
		NxLookupData nxLookup=new NxLookupData();
		nxLookup.setDescription("abc");
		when(nxLookupDataRepository.findTopByDatasetNameAndItemId(any(),any())).thenReturn(nxLookup);
		NxLookupData prodSubTypeLookupData=new NxLookupData();
		prodSubTypeLookupData.setDescription("hgt");
		when(nxLookupDataRepository.findTopByDatasetNameAndItemId(any(),any())).thenReturn(prodSubTypeLookupData);
		
		configAndUpdateRestUtilPd.processInputDesign(designDetails, MyPriceConstants.ASE_OFFER_NAME, requestMap);
	}
	

	@Test
	public void processInputDesignTest2() {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		JSONObject designDetails=new JSONObject();
		designDetails.put("siteId", 12);
		NxDesignAudit nxDesignAudit=new NxDesignAudit();
		nxDesignAudit.setData("{\"solution\":{\"solutionDeterminants\":{\"icbInd\":null,\"solutionType\":\"NS\",\"mvlInd\":null},\"bulkInd\":\"N\","
				+ "\"bundleCode\":\"ADE\",\"offers\":[{\"site\":[],\"offerId\":\"120\"}],\"leadDesignID\":6495653,\"marketStrata\":\"Wholesale\",\"cancellationReason\":null,"
				+ "\"pricerDSolutionId\":6495653,\"automationInd\":\"Y\",\"erateInd\":\"Y\",\"layer\":\"SLED\",\"solutionStatus\":\"N\"},"
				+ "\"actionDeterminants\":[{\"component\":[\"Design\",\"Price\",\"ADE\"],\"activity\":\"UpdateDesign\"}]}");
		when(nxDesignAuditRepository.findByNxRefIdAndTransaction(any(),any())).thenReturn(nxDesignAudit);
		Object marketStrataObj="retails";
		when(nexxusJsonUtility.getValue(any(),any())).thenReturn(marketStrataObj);
		NxLookupData nxLookup=new NxLookupData();
		nxLookup.setDescription("abc");
		when(nxLookupDataRepository.findTopByDatasetNameAndItemId(any(),any())).thenReturn(nxLookup);
		NxLookupData prodSubTypeLookupData=new NxLookupData();
		prodSubTypeLookupData.setDescription("hgt");
		when(nxLookupDataRepository.findTopByDatasetNameAndItemId(any(),any())).thenReturn(prodSubTypeLookupData);
		configAndUpdateRestUtilPd.processInputDesign(designDetails, MyPriceConstants.ADE_OFFER_NAME, requestMap);
	}
	
	@Test
	public void processInputDesignTest3() {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		JSONObject designDetails=new JSONObject();
		designDetails.put("siteId", 12);
		configAndUpdateRestUtilPd.processInputDesign(designDetails,"other", requestMap);
	}
	
	@Test
	public void processInputDesignTest4() {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		configAndUpdateRestUtilPd.processInputDesign(null,"other", requestMap);
	}
	
	@Test
	public void mergeSolutionAndDesignDataASETest() {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		requestMap.put(MyPriceConstants.AUTOMATION_IND, true);
		JSONObject designDetails=new JSONObject();
		designDetails.put("siteId", 12);
		NxDesignAudit nxDesignAudit=new NxDesignAudit();
		String json="{\"solution\":{\"solutionDeterminants\":{\"icbInd\":null,\"solutionType\":\"NS\",\"mvlInd\":null},\"bulkInd\":\"N\","
				+ "\"bundleCode\":\"ADE\",\"offers\":[{\"site\":[],\"offerId\":\"120\"}],\"leadDesignID\":6495653,\"marketStrata\":\"Wholesale\",\"cancellationReason\":null,"
				+ "\"pricerDSolutionId\":6495653,\"automationInd\":\"Y\",\"erateInd\":\"Y\",\"layer\":\"SLED\",\"solutionStatus\":\"N\"},"
				+ "\"actionDeterminants\":[{\"component\":[\"Design\",\"Price\",\"ADE\"],\"activity\":\"UpdateDesign\"}]}";
		when(jsonPathUtil.set(any(),any(),any(),any())).thenReturn(json);
		nxDesignAudit.setData(json);
		when(nxDesignAuditRepository.findByNxRefIdAndTransaction(any(),any())).thenReturn(nxDesignAudit);
		configAndUpdateRestUtilPd.mergeSolutionAndDesignDataASE(designDetails, requestMap);
	}
	
	@Test
	public void mergeSolutionAndDesignDataADETest() {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		requestMap.put(MyPriceConstants.AUTOMATION_IND, true);
		JSONObject designDetails=new JSONObject();
		designDetails.put("siteId", 12);
		NxDesignAudit nxDesignAudit=new NxDesignAudit();
		String json="{\"solution\":{\"solutionDeterminants\":{\"icbInd\":null,\"solutionType\":\"NS\",\"mvlInd\":null},\"bulkInd\":\"N\","
				+ "\"bundleCode\":\"ADE\",\"offers\":[{\"site\":[],\"offerId\":\"120\"}],\"leadDesignID\":6495653,\"marketStrata\":\"Wholesale\",\"cancellationReason\":null,"
				+ "\"pricerDSolutionId\":6495653,\"automationInd\":\"Y\",\"erateInd\":\"Y\",\"layer\":\"SLED\",\"solutionStatus\":\"N\"},"
				+ "\"actionDeterminants\":[{\"component\":[\"Design\",\"Price\",\"ADE\"],\"activity\":\"UpdateDesign\"}]}";
		when(jsonPathUtil.set(any(),any(),any(),any())).thenReturn(json);
		nxDesignAudit.setData(json);
		when(nxDesignAuditRepository.findByNxRefIdAndTransaction(any(),any())).thenReturn(nxDesignAudit);
		configAndUpdateRestUtilPd.mergeSolutionAndDesignDataADE(designDetails, requestMap);
	}
	
	@Test
	public void handleMarketStrataValueTest() {
		JSONObject designDetails=new JSONObject();
		designDetails.put("siteId", 12);
		Object erateInd="Y";
		when(nexxusJsonUtility.getValue(any(),any())).thenReturn(erateInd);
		configAndUpdateRestUtilPd.handleMarketStrataValue(designDetails);
	}
	
	@Test
	public void processCustomFieldsTest() {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		NxMpConfigJsonMapping mappingData=new NxMpConfigJsonMapping();
		mappingData.setKey(MyPriceConstants.DIVERSITY_SERVICE);
		when(nexxusJsonUtility.isExists(any(),any())).thenReturn(true);
		configAndUpdateRestUtilPd.processCustomFields(mappingData, "{}", requestMap, String.class);
	}
	
	@Test
	public void processCustomFieldsTest2() {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		NxMpConfigJsonMapping mappingData=new NxMpConfigJsonMapping();
		mappingData.setKey(CustomJsonConstants.INT_JURISDICTION);
		mappingData.setOffer("ASE");
		String input="dd"; 
		when(restCommonUtil.getItemValueUsingJsonPath(any(),any(),any())).thenReturn(input);
		List<Object> result=new ArrayList<Object>();
		result.add("13STATES");
		when(lineItemProcessingDao.getDataFromSalesLookUpTbl(any(),any(),any(),any())).thenReturn(result);
		String jurisdictionItemValue="FCC"; 
		when(restCommonUtil.getItemValueUsingJsonPath(any(),any(),any())).thenReturn(jurisdictionItemValue);
		configAndUpdateRestUtilPd.processCustomFields(mappingData, "{}", requestMap, String.class);
	}
	
	@Test
	public void processCustomFieldsTest3() {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		NxMpConfigJsonMapping mappingData=new NxMpConfigJsonMapping();
		mappingData.setKey(CustomJsonConstants.INT_JURISDICTION);
		mappingData.setOffer("ASE");
		String input="dd"; 
		when(restCommonUtil.getItemValueUsingJsonPath(any(),any(),any())).thenReturn(input);
		List<Object> result=new ArrayList<Object>();
		result.add("9STATES");
		when(lineItemProcessingDao.getDataFromSalesLookUpTbl(any(),any(),any(),any())).thenReturn(result);
		String jurisdictionItemValue="Interstate (FCC) Access (Interstate)"; 
		when(restCommonUtil.getItemValueUsingJsonPath(any(),any(),any())).thenReturn(jurisdictionItemValue);
		configAndUpdateRestUtilPd.processCustomFields(mappingData, "{}", requestMap, String.class);
	}
	
	@Test
	public void processCustomFieldsTes4() {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		NxMpConfigJsonMapping mappingData=new NxMpConfigJsonMapping();
		mappingData.setKey(CustomJsonConstants.INT_JURISDICTION);
		mappingData.setOffer("ASE");
		String input="dd"; 
		when(restCommonUtil.getItemValueUsingJsonPath(any(),any(),any())).thenReturn(input);
		List<Object> result=new ArrayList<Object>();
		result.add("N");
		when(lineItemProcessingDao.getDataFromSalesLookUpTbl(any(),any(),any(),any())).thenReturn(result);
		String jurisdictionItemValue="Interstate (FCC) Access (Interstate)"; 
		when(restCommonUtil.getItemValueUsingJsonPath(any(),any(),any())).thenReturn(jurisdictionItemValue);
		configAndUpdateRestUtilPd.processCustomFields(mappingData, "{}", requestMap, String.class);
	}
	
	@Test
	public void processCustomFieldsTes5() {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		NxMpConfigJsonMapping mappingData=new NxMpConfigJsonMapping();
		mappingData.setKey(CustomJsonConstants.INT_JURISDICTION);
		mappingData.setOffer("ASE");
		String input="dd"; 
		when(restCommonUtil.getItemValueUsingJsonPath(any(),any(),any())).thenReturn(input);
		String jurisdictionItemValue="State Access"; 
		when(restCommonUtil.getItemValueUsingJsonPath(any(),any(),any())).thenReturn(jurisdictionItemValue);
		configAndUpdateRestUtilPd.processCustomFields(mappingData, "{}", requestMap, String.class);
	}
	
	@Test
	public void processCustomFieldsTes6() {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		NxMpConfigJsonMapping mappingData=new NxMpConfigJsonMapping();
		mappingData.setKey(CustomJsonConstants.INT_JURISDICTION);
		mappingData.setOffer("ASE");
		String input="FL"; 
		when(restCommonUtil.getItemValueUsingJsonPath(any(),any(),any())).thenReturn(input);
		List<Object> result=new ArrayList<Object>();
		result.add("IF");
		when(lineItemProcessingDao.getDataFromSalesLookUpTbl(any(),any(),any(),any())).thenReturn(result);
		String jurisdictionItemValue="State Access"; 
		when(restCommonUtil.getItemValueUsingJsonPath(null,"{}",String.class)).thenReturn(jurisdictionItemValue);
		configAndUpdateRestUtilPd.processCustomFields(mappingData, "{}", requestMap, String.class);
	}
	

	@Test
	public void processCustomFieldsTes7() {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		NxMpConfigJsonMapping mappingData=new NxMpConfigJsonMapping();
		mappingData.setKey(CustomJsonConstants.INT_JURISDICTION);
		mappingData.setOffer("ASE");
		String input="FL"; 
		when(restCommonUtil.getItemValueUsingJsonPath(any(),any(),any())).thenReturn(input);
		List<Object> result=new ArrayList<Object>();
		result.add("OOF");
		when(lineItemProcessingDao.getDataFromSalesLookUpTbl(any(),any(),any(),any())).thenReturn(result);
		String jurisdictionItemValue="State Access"; 
		when(restCommonUtil.getItemValueUsingJsonPath(null,"{}",String.class)).thenReturn(jurisdictionItemValue);
		configAndUpdateRestUtilPd.processCustomFields(mappingData, "{}", requestMap, String.class);
	}
	
	@Test
	public void processCustomFieldsTes8() {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		NxMpConfigJsonMapping mappingData=new NxMpConfigJsonMapping();
		mappingData.setKey(CustomJsonConstants.INT_JURISDICTION);
		mappingData.setOffer("ASENoD");
		configAndUpdateRestUtilPd.processCustomFields(mappingData, "{}", requestMap, String.class);
	}
	
	@Test
	public void processCustomFieldsTest9() {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		NxMpConfigJsonMapping mappingData=new NxMpConfigJsonMapping();
		mappingData.setKey(CustomJsonConstants.INT_JURISDICTION);
		mappingData.setOffer("ADE");
		String input="dd"; 
		when(restCommonUtil.getItemValueUsingJsonPath(any(),any(),any())).thenReturn(input);
		List<Object> result=new ArrayList<Object>();
		result.add("IF");
		when(lineItemProcessingDao.getDataFromSalesLookUpTbl(any(),any(),any(),any())).thenReturn(result);
		String jurisdictionItemValue="Interstate (FCC) Access (Interstate)"; 
		when(restCommonUtil.getItemValueUsingJsonPath(any(),any(),any())).thenReturn(jurisdictionItemValue);
		configAndUpdateRestUtilPd.processCustomFields(mappingData, "{}", requestMap, String.class);
	}
	
	@Test
	public void processCustomFieldsTest10() {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		NxMpConfigJsonMapping mappingData=new NxMpConfigJsonMapping();
		mappingData.setKey(CustomJsonConstants.INT_JURISDICTION);
		mappingData.setOffer("ADE");
		String input="dd"; 
		when(restCommonUtil.getItemValueUsingJsonPath(any(),any(),any())).thenReturn(input);
		List<Object> result=new ArrayList<Object>();
		result.add("OOF");
		when(lineItemProcessingDao.getDataFromSalesLookUpTbl(any(),any(),any(),any())).thenReturn(result);
		String jurisdictionItemValue="Interstate (FCC) Access (Interstate)"; 
		when(restCommonUtil.getItemValueUsingJsonPath(any(),any(),any())).thenReturn(jurisdictionItemValue);
		configAndUpdateRestUtilPd.processCustomFields(mappingData, "{}", requestMap, String.class);
	}
	
	@Test
	public void processCustomFieldsTest11() {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		NxMpConfigJsonMapping mappingData=new NxMpConfigJsonMapping();
		mappingData.setKey(CustomJsonConstants.INT_JURISDICTION);
		mappingData.setOffer("ADE");
		String input="dd"; 
		when(restCommonUtil.getItemValueUsingJsonPath(any(),any(),any())).thenReturn(input);
		List<Object> result=new ArrayList<Object>();
		result.add("IF");
		when(lineItemProcessingDao.getDataFromSalesLookUpTbl(any(),any(),any(),any())).thenReturn(result);
		String jurisdictionItemValue="Intrastate Access (Interlata/Intrastate)"; 
		when(restCommonUtil.getItemValueUsingJsonPath(any(),any(),any())).thenReturn(jurisdictionItemValue);
		configAndUpdateRestUtilPd.processCustomFields(mappingData, "{}", requestMap, String.class);
	}
	

	@Test
	public void processCustomFieldsTest12() {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		NxMpConfigJsonMapping mappingData=new NxMpConfigJsonMapping();
		mappingData.setKey(CustomJsonConstants.INT_JURISDICTION);
		mappingData.setOffer("ADE");
		String input="dd"; 
		when(restCommonUtil.getItemValueUsingJsonPath(any(),any(),any())).thenReturn(input);
		List<Object> result=new ArrayList<Object>();
		result.add("OOF");
		when(lineItemProcessingDao.getDataFromSalesLookUpTbl(any(),any(),any(),any())).thenReturn(result);
		String jurisdictionItemValue="Intrastate Access (Interlata/Intrastate)"; 
		when(restCommonUtil.getItemValueUsingJsonPath(any(),any(),any())).thenReturn(jurisdictionItemValue);
		configAndUpdateRestUtilPd.processCustomFields(mappingData, "{}", requestMap, String.class);
	}
	
	@Test
	public void processCustomFieldsTest13() {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		NxMpConfigJsonMapping mappingData=new NxMpConfigJsonMapping();
		mappingData.setKey(CustomJsonConstants.INT_JURISDICTION);
		mappingData.setOffer("ADE");
		String jurisdictionItemValue="others"; 
		when(restCommonUtil.getItemValueUsingJsonPath(any(),any(),any())).thenReturn(jurisdictionItemValue);
		Map<String,NxLookupData> resultMap=new HashMap<String, NxLookupData>();
		resultMap.put("others", new NxLookupData());
		when(nxMyPriceRepositoryServce.getLookupDataByItemId(any())).thenReturn(resultMap);
		configAndUpdateRestUtilPd.processCustomFields(mappingData, "{}", requestMap, String.class);
	}
	
	@Test
	public void processCustomFieldsTest14() {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		NxMpConfigJsonMapping mappingData=new NxMpConfigJsonMapping();
		mappingData.setKey(CustomJsonConstants.INT_JURISDICTION);
		mappingData.setOffer("ADE");
		String jurisdictionItemValue=null; 
		when(restCommonUtil.getItemValueUsingJsonPath(any(),any(),any())).thenReturn(jurisdictionItemValue);
		configAndUpdateRestUtilPd.processCustomFields(mappingData, "{}", requestMap, String.class);
	}
	
	@Test
	public void processCustomFieldsTest15() {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		requestMap.put("A", "asd");
		NxMpConfigJsonMapping mappingData=new NxMpConfigJsonMapping();
		mappingData.setKey("A");
		mappingData.setOffer("ADE");
		configAndUpdateRestUtilPd.processCustomFields(mappingData, "{}", requestMap, String.class);
	}
	
	

	@Test
	public void processConfigDataFromCustomeRulesTest() {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		requestMap.put(MyPriceConstants.OFFER_NAME, "ASE");
		requestMap.put(StringConstants.PRICE_SCENARIO_ID, "99999910531");
		JSONObject designDetails=new JSONObject();
		designDetails.put("siteId", 12);
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
		LinkedHashMap  criteriaMap=new LinkedHashMap();
		when(nexxusJsonUtility.convertStringJsonToMap(Mockito.anyString())).thenReturn(criteriaMap);
		List<PriceAttributes> priceAttributesLst=new ArrayList<PriceAttributes>();
		PriceAttributes p=new PriceAttributes();
		p.setRateGroup("abc");
		p.setPriceType("bcd");
		p.setPriceGroup("jkl");
		p.setBeid("bht");
		priceAttributesLst.add(p);
		when(configAndUpdatePricingUtil.getPriceAttributes(any(),any())).thenReturn(priceAttributesLst);
		String macdType="gh";
		Mockito.when(restCommonUtil.getDataInString(any(),any())).thenReturn(macdType);
		List<NxLookupData> rules=new ArrayList<NxLookupData>();
		NxLookupData r=new NxLookupData();
		r.setDescription("New");
		Mockito.when(nxLookupDataRepository.getNewExistingMigrationRules(any(),any())).thenReturn(rules);
		Map  ruleMap=new HashMap<String, List<String>>();
		ruleMap.put("rateGroup", new HashSet<String>(Arrays.asList("Port")));
		//Mockito.when(nexxusJsonUtility.convertStringJsonToMap(any())).thenReturn(ruleMap);
		configAndUpdateRestUtilPd.processConfigDataFromCustomeRules(requestMap, designDetails.toJSONString());
	}
	
	@Test
	public void processConfigDataFromCustomeRulesTest2() {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		requestMap.put(MyPriceConstants.OFFER_NAME, "ADE");
		requestMap.put(StringConstants.PRICE_SCENARIO_ID, "99999910531");
		JSONObject designDetails=new JSONObject();
		designDetails.put("siteId", 12);
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
		LinkedHashMap  criteriaMap=new LinkedHashMap();
		when(nexxusJsonUtility.convertStringJsonToMap(Mockito.anyString())).thenReturn(criteriaMap);
		List<PriceAttributes> priceAttributesLst=new ArrayList<PriceAttributes>();
		PriceAttributes p=new PriceAttributes();
		p.setRateGroup("abc");
		p.setPriceType("bcd");
		p.setPriceGroup("jkl");
		p.setBeid("bht");
		priceAttributesLst.add(p);
		when(configAndUpdatePricingUtil.getPriceAttributes(any(),any())).thenReturn(priceAttributesLst);
		String macdType="gh";
		Mockito.when(restCommonUtil.getDataInString(any(),any())).thenReturn(macdType);
		List<NxLookupData> rules=new ArrayList<NxLookupData>();
		NxLookupData r=new NxLookupData();
		r.setDescription("New");
		Mockito.when(nxLookupDataRepository.getNewExistingMigrationRules(any(),any())).thenReturn(rules);
		Map  ruleMap=new HashMap<String, List<String>>();
		ruleMap.put("rateGroup", new HashSet<String>(Arrays.asList("Port")));
		//Mockito.when(nexxusJsonUtility.convertStringJsonToMap(any())).thenReturn(ruleMap);
		configAndUpdateRestUtilPd.processConfigDataFromCustomeRules(requestMap, designDetails.toJSONString());
	}
	
	@Test
	public void processConfigDataFromCustomeRulesTest3() {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		requestMap.put(MyPriceConstants.OFFER_NAME, "ASENoD");
		requestMap.put(MyPriceConstants.SUB_OFFER_NAME, "asenod_3PA");
		requestMap.put(StringConstants.PRICE_SCENARIO_ID, "99999910531");
		JSONObject designDetails=new JSONObject();
		designDetails.put("siteId", 12);
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
		LinkedHashMap  criteriaMap=new LinkedHashMap();
		when(nexxusJsonUtility.convertStringJsonToMap(Mockito.anyString())).thenReturn(criteriaMap);
		List<PriceAttributes> priceAttributesLst=new ArrayList<PriceAttributes>();
		PriceAttributes p=new PriceAttributes();
		p.setRateGroup("abc");
		p.setPriceType("bcd");
		p.setPriceGroup("jkl");
		p.setBeid("bht");
		priceAttributesLst.add(p);
		when(configAndUpdatePricingUtil.getPriceAttributes(any(),any())).thenReturn(priceAttributesLst);
		String macdType="gh";
		Mockito.when(restCommonUtil.getDataInString(any(),any())).thenReturn(macdType);
		List<NxLookupData> rules=new ArrayList<NxLookupData>();
		NxLookupData r=new NxLookupData();
		r.setDescription("New");
		Mockito.when(nxLookupDataRepository.getNewExistingMigrationRules(any(),any())).thenReturn(rules);
		Map  ruleMap=new HashMap<String, List<String>>();
		ruleMap.put("rateGroup", new ArrayList<String>(Arrays.asList("Port")));
		//Mockito.when(nexxusJsonUtility.convertStringJsonToMap(any())).thenReturn(ruleMap);
		configAndUpdateRestUtilPd.processConfigDataFromCustomeRules(requestMap, designDetails.toJSONString());
	}
	
	@Test
	public void processConfigDataASETest() {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		Object inputDesign="{}";
		String offerName="ASE";
		JSONObject designDetails=new JSONObject();
		designDetails.put("siteId", 12);
		String jsonPath="$..";
		NexxusJsonUtility nj=new NexxusJsonUtility();
		LinkedHashMap<String, Object>  criteriaMap=(LinkedHashMap<String, Object>) nj.convertStringJsonToMap("{\"_settelcoBillingElements_pf/items\": "
				+ "{\"uSOC_pf\": \"usocId\",\"newQty_telcoBillingElementArray_pf\": \"New\",\"existingQty_telcoBillingElementArray_pf\":\"Existing\","
				+ "\"migrationQty_telcoBillingElementArray_pf\":\"Migration\",\"_index\": \"index\"},\"telcoBillingElementArrayController_pf\": \"size\"}");
		List<PriceAttributes> priceAttributesLst=new ArrayList<PriceAttributes>();
		PriceAttributes p=new PriceAttributes();
		p.setRateGroup("abc");
		p.setPriceType("bcd");
		p.setPriceGroup("jkl");
		p.setBeid("bht");
		priceAttributesLst.add(p);
		when(configAndUpdatePricingUtil.getPriceAttributes(any(),any())).thenReturn(priceAttributesLst);
		String macdType="gh";
		Mockito.when(restCommonUtil.getDataInString(any(),any())).thenReturn(macdType);
		List<NxLookupData> rules=new ArrayList<NxLookupData>();
		NxLookupData r=new NxLookupData();
		r.setDescription("New");
		rules.add(r);
		Mockito.when(nxLookupDataRepository.getNewExistingMigrationRules(any(),any())).thenReturn(rules);
		Map  ruleMap=new HashMap<String, List<String>>();
		ruleMap.put("rateGroup", new ArrayList<String>(Arrays.asList("abc")));
		Mockito.when(nexxusJsonUtility.convertStringJsonToMap(any())).thenReturn(ruleMap);
		String usocIdCategor="New";
		Mockito.when(configAndUpdatePricingUtil.getUsocIdCategory(any(),any(),any(),any())).thenReturn(usocIdCategor);
		configAndUpdateRestUtilPd.processConfigDataASE(requestMap, inputDesign, offerName, designDetails, jsonPath, criteriaMap);
	}
	
	@Test
	public void processConfigDataASENoD3PATest() {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		Object inputDesign="{}";
		JSONObject designDetails=new JSONObject();
		designDetails.put("siteId", 12);
		String jsonPath="$..";
		NexxusJsonUtility nj=new NexxusJsonUtility();
		LinkedHashMap<String, Object>  criteriaMap=(LinkedHashMap<String, Object>) nj.convertStringJsonToMap("{\"_settelcoBillingElements_pf/items\": "
				+ "{\"uSOC_pf\": \"usocId\",\"newQty_telcoBillingElementArray_pf\": \"New\",\"existingQty_telcoBillingElementArray_pf\":\"Existing\","
				+ "\"migrationQty_telcoBillingElementArray_pf\":\"Migration\",\"_index\": \"index\"},\"telcoBillingElementArrayController_pf\": \"size\"}");
		List<PriceAttributes> priceAttributesLst=new ArrayList<PriceAttributes>();
		PriceAttributes p=new PriceAttributes();
		p.setRateGroup("abc");
		p.setPriceType("bcd");
		p.setPriceGroup("jkl");
		p.setBeid("bht");
		priceAttributesLst.add(p);
		when(configAndUpdatePricingUtil.getPriceAttributes(any(),any())).thenReturn(priceAttributesLst);
		String macdType="gh";
		Mockito.when(restCommonUtil.getDataInString(any(),any())).thenReturn(macdType);
		List<NxLookupData> rules=new ArrayList<NxLookupData>();
		NxLookupData r=new NxLookupData();
		r.setDescription("New");
		rules.add(r);
		Mockito.when(nxLookupDataRepository.getNewExistingMigrationRules(any(),any())).thenReturn(rules);
		Map  ruleMap=new HashMap<String, List<String>>();
		ruleMap.put("rateGroup", new ArrayList<String>(Arrays.asList("abc")));
		Mockito.when(nexxusJsonUtility.convertStringJsonToMap(any())).thenReturn(ruleMap);
		configAndUpdateRestUtilPd.processConfigDataASENoD3PA(requestMap, inputDesign, jsonPath, criteriaMap);
	}
	
	@Test
	public void processConfigDataADETest() {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		Object inputDesign="{}";
		JSONObject designDetails=new JSONObject();
		designDetails.put("siteId", 12);
		String jsonPath="$..";
		NexxusJsonUtility nj=new NexxusJsonUtility();
		LinkedHashMap<String, Object>  criteriaMap=(LinkedHashMap<String, Object>) nj.convertStringJsonToMap("{\"_settelcoBillingElements_pf/items\": "
				+ "{\"uSOC_pf\": \"usocId\",\"newQty_telcoBillingElementArray_pf\": \"New\",\"existingQty_telcoBillingElementArray_pf\":\"Existing\","
				+ "\"migrationQty_telcoBillingElementArray_pf\":\"Migration\",\"_index\": \"index\"},\"telcoBillingElementArrayController_pf\": \"size\"}");
		List<ComponentDetails> componentData=new ArrayList<ComponentDetails>();
		ComponentDetails cd=new ComponentDetails();
		componentData.add(cd);
		when(configAndUpdatePricingUtil.getComponentList(any(),any())).thenReturn(componentData);
		List<PriceAttributes> priceAttributesLst=new ArrayList<PriceAttributes>();
		PriceAttributes p=new PriceAttributes();
		p.setRateGroup("abc");
		p.setPriceType("bcd");
		p.setPriceGroup("jkl");
		p.setBeid("bht");
		priceAttributesLst.add(p);
		when(configAndUpdatePricingUtil.getPriceAttributes(any(),any())).thenReturn(priceAttributesLst);
		String macdType="gh";
		Mockito.when(restCommonUtil.getDataInString(any(),any())).thenReturn(macdType);
		List<NxLookupData> rules=new ArrayList<NxLookupData>();
		NxLookupData r=new NxLookupData();
		r.setDescription("New");
		rules.add(r);
		Mockito.when(nxLookupDataRepository.getNewExistingMigrationRules(any(),any())).thenReturn(rules);
		Map  ruleMap=new HashMap<String, List<String>>();
		ruleMap.put("rateGroup", new ArrayList<String>(Arrays.asList("abc")));
		Mockito.when(nexxusJsonUtility.convertStringJsonToMap(any())).thenReturn(ruleMap);
		String usocIdCategor="New";
		Mockito.when(configAndUpdatePricingUtil.getUsocIdCategory(any(),any(),any(),any())).thenReturn(usocIdCategor);
		configAndUpdateRestUtilPd.processConfigDataADE(requestMap, inputDesign, "ADE", designDetails, jsonPath, criteriaMap);
	}
	
	@Test
	public void process3PAUcoIdFromLookupTest() {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		requestMap.put(MyPriceConstants.OFFER_NAME, "ASENoD");
		requestMap.put(MyPriceConstants.SUB_OFFER_NAME, "asenod_3PA");
		requestMap.put(StringConstants.PRICE_SCENARIO_ID, "99999910531");
		List<NxLookupData> rulesData=new ArrayList<NxLookupData>();
		NxLookupData d=new NxLookupData();
		d.setItemId("34455");
		d.setDescription("34455");
		d.setCriteria("23435-563636");
		rulesData.add(d);
		when(nxLookupDataRepository.findByDatasetName(any())).thenReturn(rulesData);
		configAndUpdateRestUtilPd.process3PAUcoIdFromLookup(requestMap);
	}
	
	@Test
	public void getConvertedUsocIdFor3PATest() {
		Map<String, Object> requestMap=new HashMap<String, Object>();
		requestMap.put(MyPriceConstants.OFFER_NAME, "ASENoD");
		requestMap.put(MyPriceConstants.SUB_OFFER_NAME, "asenod_3PA");
		Map<String,List<String>> usocCriteriaMap=new HashMap<String, List<String>>();
		usocCriteriaMap.put("t", Arrays.asList("t"));
		requestMap.put(MyPriceConstants.ASENOD_3PA_USOC_RANGE, usocCriteriaMap);
		configAndUpdateRestUtilPd.getConvertedUsocIdFor3PA(requestMap, "t");
	}
	
	@Test
	public void isAsenodWholesale() {
		JSONObject designDetails=new JSONObject();
		designDetails.put("siteId", 12);
		NxMpConfigJsonMapping mappingData=new NxMpConfigJsonMapping();
		mappingData.setOffer(MyPriceConstants.ASENOD_OFFER_NAME);
		mappingData.setSubOffer(MyPriceConstants.ASENOD_IR);
		boolean t=false;
		Mockito.when(nexxusJsonUtility.isExists(any(),any())).thenReturn(t);
		configAndUpdateRestUtilPd.isAsenodWholesale(mappingData, designDetails);
	}
	
	@Test
	public void isAsenodWholesale1() {
		JSONObject designDetails=new JSONObject();
		designDetails.put("siteId", 12);
		NxMpConfigJsonMapping mappingData=new NxMpConfigJsonMapping();
		boolean t=false;
		Mockito.when(nexxusJsonUtility.isExists(any(),any())).thenReturn(t);
		configAndUpdateRestUtilPd.isAsenodWholesale(mappingData, designDetails);
	}
	
	@Test
	public void processJurisdictionASENoDWholeSale() {
		String input="dd"; 
		when(restCommonUtil.getItemValueUsingJsonPath(any(),any(),any())).thenReturn(input);
		List<Object> result=new ArrayList<Object>();
		result.add("13STATES");
		when(lineItemProcessingDao.getDataFromSalesLookUpTbl(any(),any(),any(),any())).thenReturn(result);
		NxMpConfigJsonMapping mappingData=new NxMpConfigJsonMapping();
		JSONObject designDetails=new JSONObject();
		designDetails.put("siteId", 12);
		configAndUpdateRestUtilPd.processJurisdictionASENoDWholeSale(mappingData, designDetails);
	}

	
	@Test
	public void processJurisdictionASENoDWholeSale2() {
		String input="dd"; 
		when(restCommonUtil.getItemValueUsingJsonPath(any(),any(),any())).thenReturn(input);
		List<Object> result=new ArrayList<Object>();
		result.add("9 States");
		when(lineItemProcessingDao.getDataFromSalesLookUpTbl(any(),any(),any(),any())).thenReturn(result);
		NxMpConfigJsonMapping mappingData=new NxMpConfigJsonMapping();
		JSONObject designDetails=new JSONObject();
		designDetails.put("siteId", 12);
		configAndUpdateRestUtilPd.processJurisdictionASENoDWholeSale(mappingData, designDetails);
	}
	
	@Test
	public void processJurisdictionASENoD() {
		NxMpConfigJsonMapping mappingData=new NxMpConfigJsonMapping();
		mappingData.setSubOffer(MyPriceConstants.ASENOD_IR);
		JSONObject designDetails=new JSONObject();
		designDetails.put("siteId", 12);
		boolean t=true;
		Mockito.when(nexxusJsonUtility.isExists(any(),any())).thenReturn(t);
		configAndUpdateRestUtilPd.processJurisdictionASENoD(mappingData, designDetails);
	}
	
	@Test
	public void processJurisdictionASENoD2() {
		NxMpConfigJsonMapping mappingData=new NxMpConfigJsonMapping();
		mappingData.setSubOffer(MyPriceConstants.ASENOD_IR);
		JSONObject designDetails=new JSONObject();
		designDetails.put("siteId", 12);
		configAndUpdateRestUtilPd.processJurisdictionASENoD(mappingData, designDetails);
	}
}
