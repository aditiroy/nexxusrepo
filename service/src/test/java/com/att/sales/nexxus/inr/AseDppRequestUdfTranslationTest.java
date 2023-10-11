package com.att.sales.nexxus.inr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.dao.model.InrXmlToJsonRule;
import com.att.sales.nexxus.dao.model.NxUdfMapping;
import com.att.sales.nexxus.dao.model.SalesMsProdcompUdfAttrVal;
import com.att.sales.nexxus.dao.repository.InrXmlToJsonRuleDao;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxUdfMappingDao;
import com.att.sales.nexxus.dao.repository.SalesMsProdcompUdfAttrValRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
@ExtendWith(MockitoExtension.class)
public class AseDppRequestUdfTranslationTest {
	@Spy
	@InjectMocks
	private AseDppRequestUdfTranslation aseDppRequestUdfTranslation;
	@Mock
	private ObjectMapper mapper;
	@Mock
	private InrXmlToJsonRuleDao inrXmlToJsonRuleDao;
	@Mock
	private Map<String, JsonNode> nodeMap;
	@Mock
	private Map<String, InrXmlToJsonRule> inrXmlToJsonRuleMap;
	@Mock
	private JsonNode dppRequest;
	@Mock
	private NxUdfMappingDao nxUdfMappingDao;
	@Mock
	private NxLookupDataRepository nxLookupDataRepository;
	@Mock
	private SalesMsProdcompUdfAttrValRepository salesMsProdcompUdfAttrValRepository;
	@Mock
	private Map<String, Map<Long, NxUdfMapping>> nxUdfMappingCache;
	@Mock
	private ObjectNode objectNode;
	
	private ObjectMapper realMapper = new ObjectMapper();
	
	@Test
	public void initTestException() throws SalesBusinessException {
		doNothing().when(aseDppRequestUdfTranslation).initializeRuleMap();
		aseDppRequestUdfTranslation.init();
	}
	
	@Test
	public void initTest() throws SalesBusinessException {
		ReflectionTestUtils.setField(aseDppRequestUdfTranslation, "inrXmlToJsonRuleMap", inrXmlToJsonRuleMap);
		doNothing().when(aseDppRequestUdfTranslation).initializeRuleMap();
		aseDppRequestUdfTranslation.init();
	}
	
	@Test
	public void initializeRuleMapTest() {
		when(inrXmlToJsonRuleDao.getInrXmlToJsonRuleMap(any())).thenReturn(inrXmlToJsonRuleMap);
		aseDppRequestUdfTranslation.initializeRuleMap();
	}
	
	@Test
	public void udfTranslateTest() throws SalesBusinessException {
		doNothing().when(aseDppRequestUdfTranslation).init();
		doNothing().when(aseDppRequestUdfTranslation).udfTranslateHelper(any(), any());
		aseDppRequestUdfTranslation.udfTranslate();
	}
	
	@Test
	public void udfTranslateHelperTest() {
		doNothing().when(aseDppRequestUdfTranslation).processArrayNode(any(), any());
		doNothing().when(aseDppRequestUdfTranslation).processObjectNode(any(), any());
		doNothing().when(aseDppRequestUdfTranslation).processNonContainerNode(any(), any());
		when(objectNode.getNodeType()).thenReturn(JsonNodeType.ARRAY, JsonNodeType.OBJECT, JsonNodeType.STRING);
		aseDppRequestUdfTranslation.udfTranslateHelper(objectNode, null);
		aseDppRequestUdfTranslation.udfTranslateHelper(objectNode, null);
		aseDppRequestUdfTranslation.udfTranslateHelper(objectNode, null);
	}
	
	@Test
	public void processArrayNodeTest() throws IOException {
		doNothing().when(aseDppRequestUdfTranslation).udfTranslateHelper(any(), any());
		ArrayNode arrayNode = realMapper.createArrayNode();
		ObjectNode element1 = realMapper.createObjectNode();
		ObjectNode element2 = realMapper.createObjectNode();
		arrayNode.add(element1).add(element2);
		element2.put("asrItemId", "asrItemId");
		JsonPath path = new JsonPath("/solution/offers/site");
		
		aseDppRequestUdfTranslation.processArrayNode(arrayNode, path);
		
		String componentJson = "{\r\n" + 
				"    \"component\": [{\r\n" + 
				"            \"componentCodeId\": 1210,\r\n" + 
				"            \"designDetails\": [{\r\n" + 
				"                    \"udfAttributeId\": [\r\n" + 
				"                        301777\r\n" + 
				"                    ],\r\n" + 
				"                    \"udfId\": 200164\r\n" + 
				"                }\r\n" + 
				"            ]\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
		JsonNode element3 = realMapper.readTree(componentJson);
		arrayNode.add(element3);
		path = new JsonPath("/solution/offers/circuit");
		aseDppRequestUdfTranslation.processArrayNode(arrayNode, path);
		
		path = JsonPath.getRootPath();
		aseDppRequestUdfTranslation.processArrayNode(arrayNode, path);
	}
	
	@Test
	public void processObjectNodeTest() throws IOException {
		ReflectionTestUtils.setField(aseDppRequestUdfTranslation, "inrXmlToJsonRuleMap", inrXmlToJsonRuleMap);
		ReflectionTestUtils.setField(aseDppRequestUdfTranslation, "nodeMap", nodeMap);
		when(inrXmlToJsonRuleMap.containsKey(any())).thenReturn(true);
		InrXmlToJsonRule inrXmlToJsonRule = new InrXmlToJsonRule();
		when(inrXmlToJsonRuleMap.get(any())).thenReturn(inrXmlToJsonRule);
		inrXmlToJsonRule.setUdfRuleSet("udfRuleSet");
		Map<Long, NxUdfMapping> nxUdfMappingMap = new HashMap<>();
		doReturn(nxUdfMappingMap).when(aseDppRequestUdfTranslation).getNxUdfMappingMap(any(), anyLong()	, anyLong());
		ObjectNode node = realMapper.createObjectNode();
		ObjectNode parentNode = realMapper.createObjectNode();
		when(nodeMap.get(any())).thenReturn(parentNode);
		node.put("udfId", 1);
		NxUdfMapping nxUdfMapping = new NxUdfMapping();
		nxUdfMappingMap.put(1L, nxUdfMapping);
		nxUdfMapping.setUdfAttributeDatasetName("Text");
		ArrayNode udfAttributeTextArray = realMapper.createArrayNode();
		udfAttributeTextArray.add("text1");
		node.set("udfAttributeText", udfAttributeTextArray);
		JsonPath path = JsonPath.getRootPath();
		
		aseDppRequestUdfTranslation.processObjectNode(node, path);
		
		udfAttributeTextArray.add("text2");
		aseDppRequestUdfTranslation.processObjectNode(node, path);
		
		nxUdfMapping.setUdfAttributeDatasetName("notText");
		udfAttributeTextArray.removeAll();
		udfAttributeTextArray.add(1);
		node.set("udfAttributeId", udfAttributeTextArray);
		doReturn("").when(aseDppRequestUdfTranslation).translateUdfAttributeId(anyLong(), anyLong());
		aseDppRequestUdfTranslation.processObjectNode(node, path);
		
		udfAttributeTextArray.add(2);
		aseDppRequestUdfTranslation.processObjectNode(node, path);
		
		String nodeJson = "{\r\n" + 
				"    \"offerId\": 120,\r\n" + 
				"    \"circuit\": [{\r\n" + 
				"            \"component\": [{\r\n" + 
				"                    \"componentCodeId\": 1210,\r\n" + 
				"                    \"channelizedIndicator\": \"Y\"\r\n" + 
				"                }, {\r\n" + 
				"                    \"componentCodeId\": 1220,\r\n" + 
				"                    \"references\": [{\r\n" + 
				"                            \"referenceId\": 1\r\n" + 
				"                        }\r\n" + 
				"                    ]\r\n" + 
				"                }\r\n" + 
				"            ]\r\n" + 
				"        }\r\n" + 
				"    ],\r\n" + 
				"    \"site\": [{\r\n" + 
				"            \"siteId\": 1\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
		node = (ObjectNode) realMapper.readTree(nodeJson);
		when(inrXmlToJsonRuleMap.containsKey(any())).thenReturn(false);
		doNothing().when(aseDppRequestUdfTranslation).udfTranslateHelper(any(), any());
		doNothing().when(aseDppRequestUdfTranslation).updatePhoneNumber(any(), any());
		path = new JsonPath("/solution/offers");
		aseDppRequestUdfTranslation.processObjectNode(node, path);
	}
	
	@Test
	public void updatePhoneNumberTest() {
		doNothing().when(aseDppRequestUdfTranslation).updatePhoneNode(any(), any());
		JsonPath path = new JsonPath("/solution/offers/site/lconDetails");
		aseDppRequestUdfTranslation.updatePhoneNumber(null, path);
		
		path = new JsonPath("/solution/contact");
		aseDppRequestUdfTranslation.updatePhoneNumber(null, path);
		
		path = JsonPath.getRootPath();
		aseDppRequestUdfTranslation.updatePhoneNumber(null, path);
	}
	
	@Test
	public void updatePhoneNodeTest() {
		ObjectNode node = realMapper.createObjectNode();
		String field = "field";
		node.put(field, "1234567890a");
		aseDppRequestUdfTranslation.updatePhoneNode(node, field);
		assertEquals("1234567890", node.path(field).asText());
		
		node.put(field, "aa");
		aseDppRequestUdfTranslation.updatePhoneNode(node, field);
		assertFalse(node.has(field));
	}
	
	@Test
	public void translateUdfAttributeIdTest() {
		String res = aseDppRequestUdfTranslation.translateUdfAttributeId(1l, 1l);
		assertNull(res);
		
		String udfAttributeValue = "value";
		SalesMsProdcompUdfAttrVal salesMsProdcompUdfAttrVal = new SalesMsProdcompUdfAttrVal();
		salesMsProdcompUdfAttrVal.setUdfAttributeValue(udfAttributeValue);
		when(salesMsProdcompUdfAttrValRepository.findTopByOfferIdAndComponentIdAndUdfIdAndUdfAttributeIdAndActive(any(), any(), any(), any(), any())).thenReturn(salesMsProdcompUdfAttrVal);
		res = aseDppRequestUdfTranslation.translateUdfAttributeId(1l, 1l);
		assertEquals(udfAttributeValue, res);
	}
	
	@Test
	public void processNonContainerNodeTest() {
		ObjectNode node = realMapper.createObjectNode();
		node.put("offerId", 1);
		node.put("componentCodeId", 2);
		JsonPath path = new JsonPath("/offerId");
		aseDppRequestUdfTranslation.processNonContainerNode(node.path("offerId"), path);
		assertEquals("1", ReflectionTestUtils.getField(aseDppRequestUdfTranslation, "offerId").toString());
		
		path = new JsonPath("/componentCodeId");
		aseDppRequestUdfTranslation.processNonContainerNode(node.path("componentCodeId"), path);
		assertEquals("2", ReflectionTestUtils.getField(aseDppRequestUdfTranslation, "componentCodeId").toString());
	}
	
	@Test
	public void generateNxUdfMappingCacheKeyTest() {
		String res = aseDppRequestUdfTranslation.generateNxUdfMappingCacheKey("1", 2L, 3L);
		assertEquals("1_2_3", res);
	}
	
	@Test
	public void getNxUdfMappingMapTest() {
		aseDppRequestUdfTranslation.getNxUdfMappingMap("1", 2L, 3L);
	}
}
