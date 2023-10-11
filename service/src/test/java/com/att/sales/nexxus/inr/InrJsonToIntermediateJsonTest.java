package com.att.sales.nexxus.inr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.dao.model.InrXmlToJsonRule;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.repository.InrXmlToJsonRuleDao;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

@ExtendWith(MockitoExtension.class)
public class InrJsonToIntermediateJsonTest {
	@Mock
	private JsonNode rawJson;
	@Mock
	private ObjectMapper mapper;
	@Mock
	private InrXmlToJsonRuleDao inrXmlToJsonRuleDao;
	@Mock
	private Map<String, JsonNode> nodeMap;
	@Mock
	private Map<String, InrXmlToJsonRule> inrXmlToJsonRuleMap;
	@Spy
	@InjectMocks
	private InrJsonToIntermediateJson inrJsonToIntermediateJson;
	@Mock
	private ObjectNode objectNode;
	@Mock
	private ArrayNode arrayNode;
	@Mock
	private Iterator<JsonNode> iterator;
	@Mock
	private Iterator<Entry<String, JsonNode>> iteratorEntry;
	@Mock
	private Entry<String, JsonNode> entry;
	@Mock
	private JsonNode simpleNode;
	@Mock
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;

	@Test
	public void initTest() throws SalesBusinessException {
		doNothing().when(inrJsonToIntermediateJson).initializeRuleMap();
		when(inrXmlToJsonRuleMap.isEmpty()).thenReturn(false);
		ReflectionTestUtils.setField(inrJsonToIntermediateJson, "inrXmlToJsonRuleMap", inrXmlToJsonRuleMap);
		inrJsonToIntermediateJson.init();
	}

	@Test
	public void initTestException() throws SalesBusinessException {
		doNothing().when(inrJsonToIntermediateJson).initializeRuleMap();
		inrJsonToIntermediateJson.init();
	}

	@Test
	public void generateTest() throws SalesBusinessException {
		doNothing().when(inrJsonToIntermediateJson).init();
		doNothing().when(inrJsonToIntermediateJson).generateHelper(any(), any());
		ReflectionTestUtils.setField(inrJsonToIntermediateJson, "nodeMap", nodeMap);
		when(nodeMap.get(any())).thenReturn(objectNode);
		JsonNode root = inrJsonToIntermediateJson.generate();
		assertSame(objectNode, root);
	}

	@Test
	public void generateHelperTest() {
		doNothing().when(inrJsonToIntermediateJson).processArrayNode(any(), any());
		doNothing().when(inrJsonToIntermediateJson).processNonContainerNode(any(), any());
		doNothing().when(inrJsonToIntermediateJson).processObjectNode(any(), any());
		when(rawJson.getNodeType()).thenReturn(JsonNodeType.ARRAY);
		inrJsonToIntermediateJson.generateHelper(rawJson, JsonPath.getRootPath());
		when(rawJson.getNodeType()).thenReturn(JsonNodeType.OBJECT);
		inrJsonToIntermediateJson.generateHelper(rawJson, JsonPath.getRootPath());
		when(rawJson.getNodeType()).thenReturn(JsonNodeType.STRING);
		inrJsonToIntermediateJson.generateHelper(rawJson, JsonPath.getRootPath());
	}

	@Test
	public void processArrayNodeTest() {
		ReflectionTestUtils.setField(inrJsonToIntermediateJson, "inrXmlToJsonRuleMap", inrXmlToJsonRuleMap);
		ReflectionTestUtils.setField(inrJsonToIntermediateJson, "nodeMap", nodeMap);
		doNothing().when(inrJsonToIntermediateJson).generateHelper(any(), any());
		when(arrayNode.iterator()).thenReturn(iterator);

		// case 1
		when(inrXmlToJsonRuleMap.containsKey(any())).thenReturn(true);
		InrXmlToJsonRule inrXmlToJsonRule = new InrXmlToJsonRule();
		inrXmlToJsonRule.setArrayElementName("arrayElementName");
		inrXmlToJsonRule.setArrayParent("arrayParent");
		inrXmlToJsonRule.setArrayName("arrayName");
		when(inrXmlToJsonRuleMap.get(any())).thenReturn(inrXmlToJsonRule);
		when(iterator.hasNext()).thenReturn(true, false);
		when(iterator.next()).thenReturn(arrayNode);
		when(nodeMap.get(any())).thenReturn(objectNode);
		when(objectNode.withArray(any())).thenReturn(arrayNode);
		doReturn(true).when(inrJsonToIntermediateJson).isNodeValid(any(), any());
		inrJsonToIntermediateJson.processArrayNode(arrayNode, JsonPath.getRootPath());

		// case 2
		when(inrXmlToJsonRuleMap.containsKey(any())).thenReturn(false);
		when(iterator.hasNext()).thenReturn(true, false);
		when(iterator.next()).thenReturn(arrayNode);
		inrJsonToIntermediateJson.processArrayNode(arrayNode, JsonPath.getRootPath());

	}

	@Test
	public void processObjectNodeTest() {
		ReflectionTestUtils.setField(inrJsonToIntermediateJson, "inrXmlToJsonRuleMap", inrXmlToJsonRuleMap);
		ReflectionTestUtils.setField(inrJsonToIntermediateJson, "nodeMap", nodeMap);
		doNothing().when(inrJsonToIntermediateJson).generateHelper(any(), any());
		when(objectNode.fields()).thenReturn(iteratorEntry);

		// case 1
		when(inrXmlToJsonRuleMap.containsKey(any())).thenReturn(true);
		InrXmlToJsonRule inrXmlToJsonRule = new InrXmlToJsonRule();
		inrXmlToJsonRule.setObjectName("objectName");
		inrXmlToJsonRule.setObjectParent("objectParent");
		when(inrXmlToJsonRuleMap.get(any())).thenReturn(inrXmlToJsonRule);
		when(iteratorEntry.hasNext()).thenReturn(true, false);
		when(iteratorEntry.next()).thenReturn(entry);
		doReturn(true).when(inrJsonToIntermediateJson).isNodeValid(any(), any());
		when(nodeMap.get(any())).thenReturn(objectNode);
		inrJsonToIntermediateJson.processObjectNode(objectNode, JsonPath.getRootPath());

		// case 2
		when(inrXmlToJsonRuleMap.containsKey(any())).thenReturn(false);
		when(iteratorEntry.hasNext()).thenReturn(true, false);
		when(iteratorEntry.next()).thenReturn(entry);
		inrJsonToIntermediateJson.processObjectNode(objectNode, JsonPath.getRootPath());
	}

	@Test
	public void processNonContainerNodeTest() {
		ReflectionTestUtils.setField(inrJsonToIntermediateJson, "inrXmlToJsonRuleMap", inrXmlToJsonRuleMap);
		ReflectionTestUtils.setField(inrJsonToIntermediateJson, "nodeMap", nodeMap);
		when(inrXmlToJsonRuleMap.containsKey(any())).thenReturn(true);
		when(nodeMap.get(any())).thenReturn(objectNode);

		when(simpleNode.asText()).thenReturn("value");
		when(simpleNode.asDouble()).thenReturn(1.0);
		when(simpleNode.asInt()).thenReturn(1);
		when(simpleNode.asLong()).thenReturn(1L);
		InrXmlToJsonRule inrXmlToJsonRule = new InrXmlToJsonRule();
		inrXmlToJsonRule.setFieldName("fieldName");
		inrXmlToJsonRule.setFieldParent("fieldParent");
		inrXmlToJsonRule.setFieldNameForTag("fieldNameForTag");
		when(inrXmlToJsonRuleMap.get(any())).thenReturn(inrXmlToJsonRule);

		// case 1
		inrXmlToJsonRule.setJsonType(InrConstants.JSON_TYPE_FIELD_TAG);
		inrJsonToIntermediateJson.processNonContainerNode(simpleNode, JsonPath.getRootPath());

		// case 2
		inrXmlToJsonRule.setJsonType(InrConstants.JSON_TYPE_FIELD_STR);
		inrJsonToIntermediateJson.processNonContainerNode(simpleNode, JsonPath.getRootPath());

		// case 3
		inrXmlToJsonRule.setJsonType(InrConstants.JSON_TYPE_FIELD_DOUBLE);
		inrJsonToIntermediateJson.processNonContainerNode(simpleNode, JsonPath.getRootPath());

		// case 4
		inrXmlToJsonRule.setJsonType(InrConstants.JSON_TYPE_FIELD_INT);
		inrJsonToIntermediateJson.processNonContainerNode(simpleNode, JsonPath.getRootPath());

		// case 5
		inrXmlToJsonRule.setJsonType(InrConstants.JSON_TYPE_FIELD_LONG);
		inrJsonToIntermediateJson.processNonContainerNode(simpleNode, JsonPath.getRootPath());
	}

	@Test
	public void initializeRuleMapTest() {
		inrJsonToIntermediateJson.initializeRuleMap();
	}

	@Disabled
	@Test
	public void isNodeValidTest() {
		// case 1
		InrXmlToJsonRule inrXmlToJsonRule = new InrXmlToJsonRule();
		assertTrue(inrJsonToIntermediateJson.isNodeValid(inrXmlToJsonRule, null));
		// case 2
		inrXmlToJsonRule.setMinSize(1L);
		assertFalse(inrJsonToIntermediateJson.isNodeValid(inrXmlToJsonRule, objectNode));

		// case 3
		inrXmlToJsonRule.setMinSize(null);
		inrXmlToJsonRule.setRequiredFields("field1, field2");
		when(objectNode.path(any())).thenReturn(objectNode);
		when(objectNode.isMissingNode()).thenReturn(true);
		assertFalse(inrJsonToIntermediateJson.isNodeValid(inrXmlToJsonRule, objectNode));
	}
	
	@Test
	public void convertDataTest() {
		NxLookupData nxLookupData = new NxLookupData();
		nxLookupData.setDescription("description");
		InrXmlToJsonRule inrXmlToJsonRule = new InrXmlToJsonRule();
		String data = "data";
		Map<String,NxLookupData> resultMap= new HashMap<>();
		resultMap.put(data, nxLookupData);
		Map<String,NxLookupData> resultMap1= new HashMap<>();
		
		//case 1
		assertEquals(data, inrJsonToIntermediateJson.convertData(inrXmlToJsonRule, data));
		
		//case 2
		inrXmlToJsonRule.setLookupDatasetName("lookupDatasetName");
		when(nxMyPriceRepositoryServce.getLookupDataByItemId(any())).thenReturn(resultMap);
		assertEquals(nxLookupData.getDescription(), inrJsonToIntermediateJson.convertData(inrXmlToJsonRule, data));
		
		//case 3
		when(nxMyPriceRepositoryServce.getLookupDataByItemId(any())).thenReturn(resultMap1);
		assertEquals(data, inrJsonToIntermediateJson.convertData(inrXmlToJsonRule, data));
		
		//case 4
		inrXmlToJsonRule.setOperations(InrIntermediateJsonGenerator.TRIM + "," + InrIntermediateJsonGenerator.REPLACE);
		inrJsonToIntermediateJson.convertData(inrXmlToJsonRule, data);
	}
}
