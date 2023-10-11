package com.att.sales.nexxus.inr;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxDwPriceDetails;
import com.att.sales.nexxus.dao.model.NxDwToJsonRules;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.repository.NxDwPriceDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxDwToJsonRulesRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.UsrpDao;
import com.att.sales.nexxus.service.InrBetaGenerateNxsiteId;
import com.att.sales.nexxus.service.InrProcessingService;
import com.att.sales.nexxus.service.InrQualifyService;
import com.att.sales.nexxus.service.MessageConsumptionServiceImpl;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@ExtendWith(MockitoExtension.class)
public class InrJsonServiceImplTest {
	@Spy
	@InjectMocks
	private InrJsonServiceImpl inrJsonServiceImpl;
	
	@Mock
	private NxDwToJsonRulesRepository nxDwToJsonRulesRepository;

	@Mock
	private NxDwPriceDetailsRepository nxDwPriceDetailsRepository;

	@Mock
	private NxRequestDetailsRepository nxRequestDetailsRepository;

	@Mock
	private UsrpDao usrpDao;

	@Spy
	private ObjectMapper mapper;

	@Mock
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	@Mock
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Mock
	private InrProcessingService inrProcessingService;
	
	@Mock
	private InrQualifyService inrQualifyService;
	
	@Mock
	private InrBetaGenerateNxsiteId inrBetaGenerateNxsiteId;
	
	@Mock
	private MessageConsumptionServiceImpl messageConsumptionServiceImpl;
	
	private ObjectMapper realMapper = new ObjectMapper();
	

	@Test
	public void inrJsonProcessTest() throws SalesBusinessException {
		InrJsonServiceRequest request = new InrJsonServiceRequest();
		request.setId(1L);
		Optional<NxDwPriceDetails> nxDwPriceDetail = Optional.empty();
		NxDwPriceDetails nxDwPriceDetails = new NxDwPriceDetails();
		nxDwPriceDetails.setNxReqId(1L);
		when(nxDwPriceDetailsRepository.findById(anyLong())).thenReturn(nxDwPriceDetail);
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		when(nxRequestDetailsRepository.findByNxReqId(anyLong())).thenReturn(nxRequestDetails);
		nxDwPriceDetails.setPriceJson("{}");
		nxRequestDetails.setProduct("product");
		InrJsonServiceImpl.Args args = new InrJsonServiceImpl.Args();
		doReturn(args).when(inrJsonServiceImpl).initializeArgs(any(),any());
		doNothing().when(inrJsonServiceImpl).updatePriceJson(any(), any());
		Map<String, Object> result = new HashMap<String, Object>();
		doReturn(result).when(inrBetaGenerateNxsiteId).generateNxsiteidInrBeta(anyLong(),anyBoolean(),anyMap());
		NxOutputFileModel nxOutputFileModel = new NxOutputFileModel();
		when(inrProcessingService.createInrNexusOutput(any(), any())).thenReturn(nxOutputFileModel);
		inrJsonServiceImpl.inrJsonProcess(request);
	}
	
	@Test
	public void initializeArgsTest() throws SalesBusinessException {
		NxDwToJsonRules nxDwToJsonRules = new NxDwToJsonRules();
		nxDwToJsonRules.setFieldName("field1");
		List<NxDwToJsonRules> rules = Arrays.asList(nxDwToJsonRules);
		when(nxDwToJsonRulesRepository
				.findByOfferAndRuleNameAndActive(any(), any(), any())).thenReturn(rules);
		NxLookupData nxLookupData = new NxLookupData();
		nxLookupData.setCriteria("{}");
		when(nxLookupDataRepository.findTopByDatasetNameAndItemId(any(), any())).thenReturn(nxLookupData);
		inrJsonServiceImpl.initializeArgs("MIS/PNT",1L);
		inrJsonServiceImpl.initializeArgs("GMIS",2L);
	}
	
	@Test
	public void updatePriceJsonTest() {
		ObjectNode node = realMapper.createObjectNode();
		InrJsonServiceImpl.Args args = new InrJsonServiceImpl.Args();
		doNothing().when(inrJsonServiceImpl).traverseHelper(any(), any(), any());
		doNothing().when(inrJsonServiceImpl).processUnattachedUsrpData(any(), any());
		doNothing().when(inrJsonServiceImpl).updateAfterTraverse(any(), any());
		inrJsonServiceImpl.updatePriceJson(node, args);
	}
	
	@Test
	public void updateAfterTraverseTest() throws JsonMappingException, JsonProcessingException {
		String n1 = "{\r\n"
				+ "    \"service\": \"AVPN\",\r\n"
				+ "    \"accountDetails\": [{\r\n"
				+ "            \"custName\": \"Merck & Co. Inc\",\r\n"
				+ "            \"MCN\": \"B06106\",\r\n"
				+ "            \"design\": [{\r\n"
				+ "                    \"portNumber\": \"3436043\",\r\n"
				+ "                    \"AccessSpeed\": \"1.544 mb\",\r\n"
				+ "                    \"portSpeed\": \"4000\",\r\n"
				+ "                    \"circuitId\": \"abc899ati\",\r\n"
				+ "                    \"accessType\": \"Ethernet\",\r\n"
				+ "                    \"priceDetails\": [{\r\n"
				+ "                            \"beid\": \"18009\",\r\n"
				+ "                            \"typeOfCharge\": \"P\",\r\n"
				+ "                            \"actualPrice\": \"857.14\",\r\n"
				+ "                            \"billingRateId\": \"60000409\",\r\n"
				+ "                            \"localListPrice\": \"2597.38\",\r\n"
				+ "                            \"priceType\": \"PORTBEID\",\r\n"
				+ "                            \"quantity\": \"1\",\r\n"
				+ "                            \"pbi\": \"00097054\",\r\n"
				+ "                            \"componentDescription\": \"AVPN Port\"\r\n"
				+ "                        }, {\r\n"
				+ "                            \"beid\": \"19773\",\r\n"
				+ "                            \"typeOfCharge\": \"A\",\r\n"
				+ "                            \"actualPrice\": \"1446.57\",\r\n"
				+ "                            \"localListPrice\": \"1446.57\",\r\n"
				+ "                            \"priceType\": \"ACCESSBEID\",\r\n"
				+ "                            \"quantity\": \"1\",\r\n"
				+ "                            \"pbi\": \"00123873\",\r\n"
				+ "                            \"componentDescription\": \"AVPN MOW Access - 50M\"\r\n"
				+ "                        }\r\n"
				+ "                    ]\r\n"
				+ "                }, {\r\n"
				+ "                    \"portNumber\": \"3436043\",\r\n"
				+ "                    \"portSpeed\": \"4000\",\r\n"
				+ "                    \"circuitId\": \"abc001ati\",\r\n"
				+ "                    \"accessType\": \"TDM\",\r\n"
				+ "                    \"priceDetails\": [{\r\n"
				+ "                            \"beid\": \"18009\",\r\n"
				+ "                            \"typeOfCharge\": \"P\",\r\n"
				+ "                            \"actualPrice\": \"857.14\",\r\n"
				+ "                            \"billingRateId\": \"60000409\",\r\n"
				+ "                            \"localListPrice\": \"2597.38\",\r\n"
				+ "                            \"priceType\": \"PORTBEID\",\r\n"
				+ "                            \"quantity\": \"1\",\r\n"
				+ "                            \"pbi\": \"00097054\",\r\n"
				+ "                            \"componentDescription\": \"AVPN Port\"\r\n"
				+ "                        }, {\r\n"
				+ "                            \"beid\": \"19773\",\r\n"
				+ "                            \"typeOfCharge\": \"A\",\r\n"
				+ "                            \"actualPrice\": \"1446.57\",\r\n"
				+ "                            \"localListPrice\": \"1446.57\",\r\n"
				+ "                            \"priceType\": \"ACCESSBEID\",\r\n"
				+ "                            \"quantity\": \"1\",\r\n"
				+ "                            \"pbi\": \"00123873\",\r\n"
				+ "                            \"componentDescription\": \"AVPN MOW Access - 50M\"\r\n"
				+ "                        }\r\n"
				+ "                    ]\r\n"
				+ "                }, {\r\n"
				+ "                    \"portNumber\": \"3436043\",\r\n"
				+ "                    \"portSpeed\": \"4000\",\r\n"
				+ "                    \"circuitId\": \"abc900ati\",\r\n"
				+ "                    \"accessType\": \"TDM\",\r\n"
				+ "                    \"nexxusFallout\": \"Y\",\r\n"
				+ "                    \"priceDetails\": [{\r\n"
				+ "                            \"beid\": \"18009\",\r\n"
				+ "                            \"typeOfCharge\": \"P\",\r\n"
				+ "                            \"actualPrice\": \"857.14\",\r\n"
				+ "                            \"billingRateId\": \"60000409\",\r\n"
				+ "                            \"localListPrice\": \"2597.38\",\r\n"
				+ "                            \"priceType\": \"PORTBEID\",\r\n"
				+ "                            \"quantity\": \"1\",\r\n"
				+ "                            \"pbi\": \"00097054\",\r\n"
				+ "                            \"componentDescription\": \"AVPN Port\"\r\n"
				+ "                        }, {\r\n"
				+ "                            \"beid\": \"19773\",\r\n"
				+ "                            \"typeOfCharge\": \"A\",\r\n"
				+ "                            \"actualPrice\": \"1446.57\",\r\n"
				+ "                            \"localListPrice\": \"1446.57\",\r\n"
				+ "                            \"priceType\": \"ACCESSBEID\",\r\n"
				+ "                            \"quantity\": \"1\",\r\n"
				+ "                            \"pbi\": \"00123873\",\r\n"
				+ "                            \"componentDescription\": \"AVPN MOW Access - 50M\"\r\n"
				+ "                        }\r\n"
				+ "                    ]\r\n"
				+ "                }\r\n"
				+ "            ]\r\n"
				+ "        }\r\n"
				+ "    ]\r\n"
				+ "}";
		JsonNode node = realMapper.readTree(n1);
		InrJsonServiceImpl.Args args = new InrJsonServiceImpl.Args();
		args.product = "AVPN";
		inrJsonServiceImpl.updateAfterTraverse(node, args);
	}
	
	@Test
	public void circuitIdPlusOneTest() {
		inrJsonServiceImpl.circuitIdPlusOne("abc9ati");
	}
	
	@Test
	public void traverseHelperTest() throws JsonMappingException, JsonProcessingException {
		String n1 = "{\r\n"
				+ "    \"service\": \"AVPN\",\r\n"
				+ "    \"accountDetails\": [{\r\n"
				+ "            \"custName\": \"Merck & Co. Inc\",\r\n"
				+ "            \"MCN\": \"B06106\",\r\n"
				+ "            \"design\": [{}\r\n"
				+ "            ]\r\n"
				+ "        }\r\n"
				+ "    ]\r\n"
				+ "}";
		JsonNode node = realMapper.readTree(n1);
		JsonPath rootPath = JsonPath.getRootPath();
		InrJsonServiceImpl.Args args = new InrJsonServiceImpl.Args();
		args.queryPath = "/accountDetails";
		doNothing().when(inrJsonServiceImpl).updateNodeMapIdentifierKey(any(), any(), any());
		doNothing().when(inrJsonServiceImpl).queryUsrp(any(), any(), any());
		doNothing().when(inrJsonServiceImpl).attachUsrpData(any(), any(), any());
		inrJsonServiceImpl.traverseHelper(node, rootPath, args);
	}
	
	@Test
	public void processUnattachedUsrpDataTest() throws JsonMappingException, JsonProcessingException {
		String n1 = "{\r\n"
				+ "    \"service\": \"AVPN\",\r\n"
				+ "    \"accountDetails\": [{\r\n"
				+ "            \"custName\": \"Merck & Co. Inc\",\r\n"
				+ "            \"MCN\": \"B06106\",\r\n"
				+ "            \"design\": [{}\r\n"
				+ "            ]\r\n"
				+ "        }\r\n"
				+ "    ]\r\n"
				+ "}";
		JsonNode node = realMapper.readTree(n1);
		InrJsonServiceImpl.Args args = new InrJsonServiceImpl.Args();
		Map<String, Object> row = new HashMap<>();
		row.put("MCN", "mcn_value");
		args.usrpDesigns.add(row);
		doNothing().when(inrJsonServiceImpl).updateNodeMapIdentifierKey(any(), any(), any());
		doNothing().when(inrJsonServiceImpl).processUsrpData(any(), any());
		inrJsonServiceImpl.processUnattachedUsrpData(node, args);
	}
	
	@Test
	public void attachUsrpDataTest() {
		ObjectNode node = realMapper.createObjectNode();
		JsonPath path = new JsonPath("/accountDetails/design");
		InrJsonServiceImpl.Args args = new InrJsonServiceImpl.Args();
		args.attachUsrpDataPath = "/accountDetails/design";
		doReturn(null).when(inrJsonServiceImpl).getUsrpDesign(any(), any());
		doNothing().when(inrJsonServiceImpl).processUsrpData(any(), any());
		inrJsonServiceImpl.attachUsrpData(node, path, args);
	}
	
	@Test
	public void processUsrpDataTest() {
		Map<String, Object> usrpDesign = new HashMap<>();
		usrpDesign.put("dwKey", "value");
		InrJsonServiceImpl.Args args = new InrJsonServiceImpl.Args();
		NxDwToJsonRules rule1 = new NxDwToJsonRules();
		rule1.setDwKey("dwKeyNotInUsrp");
		rule1.setFieldName("field1");
		rule1.setFieldType("string");
		Map<String, NxDwToJsonRules> jsonBuildRuleMap = new HashMap<>();
		jsonBuildRuleMap.put("field1", rule1);
		NxDwToJsonRules rule2 = new NxDwToJsonRules();
		rule2.setDwKey("dwKey");
		rule2.setFieldName("field2");
		rule2.setFieldType("string");
		jsonBuildRuleMap.put("field2", rule2);
		args.jsonBuildRuleMap = jsonBuildRuleMap;
		doReturn(null).when(inrJsonServiceImpl).getParentNode(any(), any(), any());
		doReturn(null).when(inrJsonServiceImpl).convertData(any(), any(), any(), any());
		doNothing().when(inrJsonServiceImpl).putValueToNode(any(), any(), any(), any(), any(), any());
		inrJsonServiceImpl.processUsrpData(usrpDesign, args);
	}
	
	@Test
	public void getParentNodeTest() {
		NxDwToJsonRules jsonBuildRule = new NxDwToJsonRules();
		Map<String, Object> usrpDesign = new HashMap<>();
		usrpDesign.put("key0", "value0");
		usrpDesign.put("key1", "value1");
		InrJsonServiceImpl.Args args = new InrJsonServiceImpl.Args();
		Map<String, NxDwToJsonRules> jsonBuildRuleMap = new HashMap<>();
		NxDwToJsonRules rule0 = new NxDwToJsonRules();
		rule0.setFieldName("field0");
		rule0.setFieldType("string");
		rule0.setFieldParent("field1");
		rule0.setIdentifierKey("key0");
		rule0.setIdentifierKey("key0");
		jsonBuildRuleMap.put("field0", rule0);
		NxDwToJsonRules rule1 = new NxDwToJsonRules();
		rule1.setDwKey("dwKeyNotInUsrp");
		rule1.setFieldName("field1");
		rule1.setFieldType("string");
		rule1.setFieldParent("field2");
		rule1.setIdentifierKey("key1");
		rule1.setDwKey("key1");
		jsonBuildRuleMap.put("field1", rule1);
		NxDwToJsonRules rule2 = new NxDwToJsonRules();
		rule2.setDwKey("dwKey");
		rule2.setFieldName("field2");
		rule2.setFieldType("string");
		rule2.setFieldParent("root");
		rule2.setIdentifierKey("/accountDetails/key2");
		jsonBuildRuleMap.put("field2", rule2);
		args.jsonBuildRuleMap = jsonBuildRuleMap;
		ObjectNode grandParentNode = realMapper.createObjectNode();
		grandParentNode.put("key2", "value2");
		args.nodeMap.put("value2", grandParentNode);
		args.nodeMap.put("/accountDetails", grandParentNode);
		ObjectNode parentNode = realMapper.createObjectNode();
		doReturn(parentNode).when(inrJsonServiceImpl).createdParentNode(any(), any(), any());
		usrpDesign.put(InrConstants.NEXXUS_FALLOUT, InrConstants.NEXXUS_FALLOUT);
		usrpDesign.put(InrConstants.NEXXUS_FALLOUT_REASON, InrConstants.NEXXUS_FALLOUT_REASON);
		inrJsonServiceImpl.getParentNode(rule0, usrpDesign, args);
		
		ObjectNode grandParentNode1 = realMapper.createObjectNode();
		grandParentNode1.put("key2", "value2");
		rule1.setFieldType("list");
		args.nodeMap.clear();
		args.nodeMap.put("value2", grandParentNode1);
		args.nodeMap.put("/accountDetails", grandParentNode1);
		inrJsonServiceImpl.getParentNode(rule0, usrpDesign, args);
	}
	
	@Test
	public void createdParentNodeTest() {
		NxDwToJsonRules jsonBuildRule = new NxDwToJsonRules();
		jsonBuildRule.setIdentifierKey("key");
		jsonBuildRule.setIdentifierType("string");
		jsonBuildRule.setDwKey("dwkey");
		Map<String, Object> usrpDesignRow = new HashMap<>();
		InrJsonServiceImpl.Args args = new InrJsonServiceImpl.Args();
		doReturn(null).when(inrJsonServiceImpl).convertData(any(), any(), any(), any());
		doNothing().when(inrJsonServiceImpl).putValueToNode(any(), any(), any(), any(), any(), any());
		inrJsonServiceImpl.createdParentNode(jsonBuildRule, usrpDesignRow, args);
	}
	
	@Test
	public void putValueToNodeTest() {
		NxDwToJsonRules jsonBuildRule = new NxDwToJsonRules();
		jsonBuildRule.setType("COPY");
		jsonBuildRule.setFieldName("priceDetails");
		jsonBuildRule.setDefaultValue("/accountDetails/design");
		InrJsonServiceImpl.Args args = new InrJsonServiceImpl.Args();
		ObjectNode design = realMapper.createObjectNode();
		ObjectNode priceDetails = realMapper.createObjectNode();
		design.withArray("priceDetails").add(priceDetails);
		ObjectNode parentNode = realMapper.createObjectNode();
		parentNode.put(InrConstants.NEXXUS_FALLOUT, InrConstants.NEXXUS_FALLOUT);
		parentNode.put(InrConstants.NEXXUS_FALLOUT_REASON, InrConstants.NEXXUS_FALLOUT_REASON);
		inrJsonServiceImpl.putValueToNode("type", "value", parentNode, "tagName", jsonBuildRule, args);
		
		args.nodeMap.put("/accountDetails/design", design);
		inrJsonServiceImpl.putValueToNode("type", "value", parentNode, "tagName", jsonBuildRule, args);
		
		jsonBuildRule.setFieldName("subProductName");
		design.put("subProductName", "subProductName");
		inrJsonServiceImpl.putValueToNode("type", "value", parentNode, "tagName", jsonBuildRule, args);
		
		jsonBuildRule.setType(null);
		inrJsonServiceImpl.putValueToNode("type", null, parentNode, "tagName", jsonBuildRule, args);
		
		inrJsonServiceImpl.putValueToNode("String", "value", parentNode, "tagName", jsonBuildRule, args);
		inrJsonServiceImpl.putValueToNode("int", "1", parentNode, "tagName", jsonBuildRule, args);
		inrJsonServiceImpl.putValueToNode("long", "1", parentNode, "tagName", jsonBuildRule, args);
		inrJsonServiceImpl.putValueToNode("double", "1", parentNode, "tagName", jsonBuildRule, args);
	}
	
	@Test
	public void convertDataTest() {
		NxDwToJsonRules jsonBuildRule = new NxDwToJsonRules();
		InrJsonServiceImpl.Args args = new InrJsonServiceImpl.Args();
		Map<String, Object> usrpDesign = new HashMap<>();
		jsonBuildRule.setType("CUSTOM");
		jsonBuildRule.setFieldName("technology");
		args.product = MyPriceConstants.AVPN;
		NxDwToJsonRules jsonBuildRuleInMap = new NxDwToJsonRules();
		Map<String, NxDwToJsonRules> jsonBuildRuleMap = new HashMap<>();
		args.jsonBuildRuleMap = jsonBuildRuleMap;
		jsonBuildRuleMap.put("interconnectType", jsonBuildRuleInMap);
		jsonBuildRuleMap.put("accessMethodType", jsonBuildRuleInMap);
		inrJsonServiceImpl.convertData(jsonBuildRule, null, usrpDesign, args);
		
		usrpDesign.put("interconnect_type", "interconnect_type");
		usrpDesign.put("access_method_type", "access_method_type");
		inrJsonServiceImpl.convertData(jsonBuildRule, null, usrpDesign, args);
		
		NxLookupData nxLookupData = new NxLookupData();
		Map<String, NxLookupData> nxLookupDataMap = new HashMap<>();
		nxLookupDataMap.put("interconnect_type_access_method_type", nxLookupData);
		when(nxMyPriceRepositoryServce.getLookupDataByItemId(any())).thenReturn(nxLookupDataMap);
		inrJsonServiceImpl.convertData(jsonBuildRule, null, usrpDesign, args);
		
		jsonBuildRule.setFieldName("AccessSpeed");
		NxDwToJsonRules accessProductNameJsonBuildRule = new NxDwToJsonRules();
		jsonBuildRuleMap.put("accessType", jsonBuildRuleInMap);
		jsonBuildRuleMap.put("accessProductName", accessProductNameJsonBuildRule);
		accessProductNameJsonBuildRule.setType("CUSTOM");
		accessProductNameJsonBuildRule.setFieldName("accessProductName");
		usrpDesign.put("access_type", 33);
		inrJsonServiceImpl.convertData(jsonBuildRule, null, usrpDesign, args);
		
		usrpDesign.put("access_type", 1);
		inrJsonServiceImpl.convertData(jsonBuildRule, null, usrpDesign, args);
		
		usrpDesign.put("port_speed", 1);
		nxLookupDataMap.put("1_1", nxLookupData);
		inrJsonServiceImpl.convertData(jsonBuildRule, null, usrpDesign, args);
		
		usrpDesign.put("access_type", 8);
		inrJsonServiceImpl.convertData(jsonBuildRule, null, usrpDesign, args);
		
		jsonBuildRule.setFieldName("AccessArchitecture");
		usrpDesign.remove("interconnect_type");
		usrpDesign.remove("access_method_type");
		inrJsonServiceImpl.convertData(jsonBuildRule, null, usrpDesign, args);
		
		usrpDesign.put("interconnect_type", 1);
		usrpDesign.put("access_method_type", 8);
		inrJsonServiceImpl.convertData(jsonBuildRule, null, usrpDesign, args);
		
		usrpDesign.put("access_method_type", 1);
		inrJsonServiceImpl.convertData(jsonBuildRule, null, usrpDesign, args);
		
		args.product = MyPriceConstants.ADIG;
		jsonBuildRule.setFieldName("accessTechnology");
		usrpDesign.remove("interconnect_type");
		usrpDesign.remove("access_method_type");
		inrJsonServiceImpl.convertData(jsonBuildRule, null, usrpDesign, args);
		
		usrpDesign.put("interconnect_type", 1);
		usrpDesign.put("access_method_type", 8);
		inrJsonServiceImpl.convertData(jsonBuildRule, null, usrpDesign, args);
		
		nxLookupDataMap.put("1_8", nxLookupData);
		inrJsonServiceImpl.convertData(jsonBuildRule, null, usrpDesign, args);
		
		args.product = MyPriceConstants.ADI;
		jsonBuildRule.setFieldName("cosSpeed");
		inrJsonServiceImpl.convertData(jsonBuildRule, null, usrpDesign, args);
		
		usrpDesign.put("cos", "Y");
		inrJsonServiceImpl.convertData(jsonBuildRule, null, usrpDesign, args);
		
		args.product = "product";
		inrJsonServiceImpl.convertData(jsonBuildRule, null, usrpDesign, args);
		
		jsonBuildRule.setType(null);
		jsonBuildRule.setDefaultValue("defaultValue");
		inrJsonServiceImpl.convertData(jsonBuildRule, null, usrpDesign, args);
		
		jsonBuildRule.setDefaultValue(null);
		jsonBuildRule.setType("TRIM_CIRCUITID");
		inrJsonServiceImpl.convertData(jsonBuildRule, "1 2", usrpDesign, args);
		
		jsonBuildRule.setType(null);
		jsonBuildRule.setLookupDatasetName("lookupDatasetName");
		inrJsonServiceImpl.convertData(jsonBuildRule, "data", usrpDesign, args);
		
		nxLookupDataMap.put("data", nxLookupData);
		inrJsonServiceImpl.convertData(jsonBuildRule, "data", usrpDesign, args);
	}
	
	@Test
	public void stringHasValueTest() {
		inrJsonServiceImpl.stringHasValue(null);
		inrJsonServiceImpl.stringHasValue("");
	}
	
	@Test
	public void getUsrpDesignTest() {
		ObjectNode node = realMapper.createObjectNode();
		node.put("circuitId", "circuitId");
		node.put("portNumber", "portNumber");
		Map<String, Object> usrpDesign = new HashMap<>();
		Map<String, Map<String, Object>> usrpDesignMap = new HashMap<>();
		Map<String, Map<String, Map<String, Object>>> lookup = new HashMap<>();
		InrJsonServiceImpl.Args args = new InrJsonServiceImpl.Args();
		args.usrpDesignLookup = lookup;
		args.mcn = "mcn";
		lookup.put("mcn", usrpDesignMap);
		usrpDesignMap.put("circuitId", usrpDesign);
		inrJsonServiceImpl.getUsrpDesign(node, args);
		
		usrpDesignMap.remove("circuitId");
		usrpDesignMap.put("portNumber", usrpDesign);
		inrJsonServiceImpl.getUsrpDesign(node, args);
		
		usrpDesignMap.remove("portNumber");
		args.queryString = "select top 1 * from v_adig_inv where (circuitid = ? and cls_serial = ? and access_type = 8)";
		args.queryParams = "/accountDetails/design/circuitId";
		doReturn(".DHEC.963824..ATI.").when(inrJsonServiceImpl).getQueryParam(any(), any());
		Map<String, Object> row = new HashMap<>();
		row.put("disc_ind", "Y");
		List<Map<String, Object>> usrpDesigns = new ArrayList<>();
		usrpDesigns.add(row);
		doReturn(usrpDesigns).when(usrpDao).query(any(), any());
		inrJsonServiceImpl.getUsrpDesign(node, args);
		
		usrpDesigns.clear();
		inrJsonServiceImpl.getUsrpDesign(node, args);
	}
	
	@Test
	public void getClsSerialTest() {
		inrJsonServiceImpl.getClsSerial("123");
	}
	
	@Test
	public void getQueryParamTest() {
		String path = "/accountDetails/design/circuitId";
		InrJsonServiceImpl.Args args = new InrJsonServiceImpl.Args();
		ObjectNode node = realMapper.createObjectNode();
		node.put("circuitId", "1234567890ATI");
		args.nodeMap.put("/accountDetails/design", node);
		inrJsonServiceImpl.getQueryParam(path, args);
		
		node.put("circuitId", "1234567890123ATI");
		inrJsonServiceImpl.getQueryParam(path, args);
		
		node.put("circuitId", "123456789ATI");
		inrJsonServiceImpl.getQueryParam(path, args);
	}
	
	@Test
	public void queryUsrpTest() {
		InrJsonServiceImpl.Args args = new InrJsonServiceImpl.Args();
		args.queryPath = "/accountDetails";
		ObjectNode node = realMapper.createObjectNode();
		JsonPath path = new JsonPath("/accountDetails");
		node.put("mcn", "mcn");
		doNothing().when(inrJsonServiceImpl).updatingUsrpResultsToArgs(any(), any());
		inrJsonServiceImpl.queryUsrp(node, path, args);
	}
	
	@Test
	public void updatingUsrpResultsToArgsTest() {
		InrJsonServiceImpl.Args args = new InrJsonServiceImpl.Args();
		args.mcn = "mcn";
		Map<String, Object> row = new HashMap<>();
		row.put("disc_ind", "Y");
		row.put("circuitid", "circuitid");
		row.put("icore_site_id", "icore_site_id");
		Map<String, Object> row1 = new HashMap<>();
		row1.put("circuitid", "circuitid");
		row1.put("icore_site_id", "icore_site_id");
		List<Map<String, Object>> usrpDesigns = Arrays.asList(row, row1);
		inrJsonServiceImpl.updatingUsrpResultsToArgs(usrpDesigns, args);
	}
	
	@Test
	public void updateNodeMapIdentifierKeyTest() {
		JsonPath path = new JsonPath("/parent/field");
		InrJsonServiceImpl.Args args = new InrJsonServiceImpl.Args();
		NxDwToJsonRules nxDwToJsonRules = new NxDwToJsonRules();
		nxDwToJsonRules.setFieldName("field");
		Map<String, NxDwToJsonRules> jsonBuildRuleMap = new HashMap<>();
		args.jsonBuildRuleMap = jsonBuildRuleMap;
		jsonBuildRuleMap.put("field", nxDwToJsonRules);
		nxDwToJsonRules.setIdentifierKey("/identifierKey");
		doReturn("identifierKey").when(inrJsonServiceImpl).getIdentifierKey(any(), any(), any());
		inrJsonServiceImpl.updateNodeMapIdentifierKey(null, path, args);
	}
	
	@Test
	public void trimSpaceAndDotTest() {
		inrJsonServiceImpl.trimSpaceAndDot(null);
	}
}
