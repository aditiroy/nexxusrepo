package com.att.sales.nexxus.inr;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.Map;
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
import com.att.sales.nexxus.dao.repository.InrXmlToJsonRuleDao;
import com.att.sales.nexxus.dao.repository.InrXmlToJsonRuleDao.InrXmlToJsonRuleDaoResult;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

@ExtendWith(MockitoExtension.class)
public class InrInventoryJsonToIntermediateJsonTest {
	
	@Spy
	@InjectMocks
	private InrInventoryJsonToIntermediateJson inrInventoryJsonToIntermediateJson;
	@Mock
	private ObjectMapper mapper;
	@Mock
	private InrXmlToJsonRuleDao inrXmlToJsonRuleDao;
	@Mock
	private Map<String, JsonNode> nodeMap;
	@Mock
	private Map<String, InrXmlToJsonRule> inrXmlToJsonRuleMap;
	@Mock
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	@Mock
	private JsonNode inventoryJson;
	@Mock
	private InrXmlToJsonRuleDaoResult inrXmlToJsonRuleDaoResult;
	@Mock
	private ObjectNode objectNode;
	@Mock
	private ArrayNode arrayNode;
	private ObjectMapper realMapper = new ObjectMapper();
	
	@Test
	public void generateTest() throws SalesBusinessException {
		doNothing().when(inrInventoryJsonToIntermediateJson).init();
		doNothing().when(inrInventoryJsonToIntermediateJson).generateHelper(any(), any());
		ReflectionTestUtils.setField(inrInventoryJsonToIntermediateJson, "nodeMap", nodeMap);
		when(nodeMap.get(any())).thenReturn(objectNode);
		
		JsonNode res = inrInventoryJsonToIntermediateJson.generate();
		assertSame(objectNode, res);
	}
	
	@Test
	public void generateHelperTest() {
		when(objectNode.getNodeType()).thenReturn(JsonNodeType.ARRAY, JsonNodeType.OBJECT, JsonNodeType.STRING);
		doNothing().when(inrInventoryJsonToIntermediateJson).processArrayNode(any(), any());
		doNothing().when(inrInventoryJsonToIntermediateJson).processObjectNode(any(), any());
		doNothing().when(inrInventoryJsonToIntermediateJson).processNonContainerNode(any(), any());
		inrInventoryJsonToIntermediateJson.generateHelper(objectNode, null);
		inrInventoryJsonToIntermediateJson.generateHelper(objectNode, null);
		inrInventoryJsonToIntermediateJson.generateHelper(objectNode, null);
	}
	
	@Test
	public void processNonContainerNodeTest() {
		ReflectionTestUtils.setField(inrInventoryJsonToIntermediateJson, "inrXmlToJsonRuleMap", inrXmlToJsonRuleMap);
		ReflectionTestUtils.setField(inrInventoryJsonToIntermediateJson, "nodeMap", nodeMap);
		when(inrXmlToJsonRuleMap.containsKey(any())).thenReturn(true);
		when(nodeMap.get(any())).thenReturn(objectNode);
		InrXmlToJsonRule inrXmlToJsonRule = new InrXmlToJsonRule();
		when(inrXmlToJsonRuleMap.get(any())).thenReturn(inrXmlToJsonRule);
		inrXmlToJsonRule.setFieldNullYn("Y");
		inrXmlToJsonRule.setFieldName("fieldName");
		inrXmlToJsonRule.setFieldParent("fieldParent");
		inrXmlToJsonRule.setJsonType(InrConstants.JSON_TYPE_FIELD_TAG);
		
		ObjectNode node = realMapper.createObjectNode();
		node.put("field", "field");
		JsonPath path = new JsonPath("/field");
		inrInventoryJsonToIntermediateJson.processNonContainerNode(node.path("field"), path);
		
		inrXmlToJsonRule.setJsonType(InrConstants.JSON_TYPE_FIELD_STR);
		inrInventoryJsonToIntermediateJson.processNonContainerNode(node.path("field"), path);
		
		inrXmlToJsonRule.setJsonType(InrConstants.JSON_TYPE_FIELD_DOUBLE);
		node.put("field", 1.1);
		inrInventoryJsonToIntermediateJson.processNonContainerNode(node.path("field"), path);
		
		inrXmlToJsonRule.setJsonType(InrConstants.JSON_TYPE_FIELD_INT);
		node.put("field", 1);
		inrInventoryJsonToIntermediateJson.processNonContainerNode(node.path("field"), path);
		
		inrXmlToJsonRule.setJsonType(InrConstants.JSON_TYPE_FIELD_LONG);
		inrInventoryJsonToIntermediateJson.processNonContainerNode(node.path("field"), path);
	}
	
	@Test
	public void processObjectNodeTest() {
		ReflectionTestUtils.setField(inrInventoryJsonToIntermediateJson, "inrXmlToJsonRuleMap", inrXmlToJsonRuleMap);
		ReflectionTestUtils.setField(inrInventoryJsonToIntermediateJson, "nodeMap", nodeMap);
		when(inrXmlToJsonRuleMap.containsKey(any())).thenReturn(true);
		when(nodeMap.get(any())).thenReturn(objectNode);
		InrXmlToJsonRule inrXmlToJsonRule = new InrXmlToJsonRule();
		when(inrXmlToJsonRuleMap.get(any())).thenReturn(inrXmlToJsonRule);
		inrXmlToJsonRule.setJsonType(InrConstants.JSON_TYPE_OBJECT);
		ReflectionTestUtils.setField(inrInventoryJsonToIntermediateJson, "mapper", realMapper);
		doNothing().when(inrInventoryJsonToIntermediateJson).generateHelper(any(), any());
		doReturn(true).when(inrInventoryJsonToIntermediateJson).isNodeValid(any(), any());
		ObjectNode node = realMapper.createObjectNode();
		node.put("field", "field");
		JsonPath path = JsonPath.getRootPath();
		
		inrInventoryJsonToIntermediateJson.processObjectNode(node, path);
		
		inrXmlToJsonRule.setJsonType(InrConstants.JSON_TYPE_ARRAY);
		when(objectNode.withArray(any())).thenReturn(arrayNode);
		inrInventoryJsonToIntermediateJson.processObjectNode(node, path);
		
		when(inrXmlToJsonRuleMap.containsKey(any())).thenReturn(false);
		inrInventoryJsonToIntermediateJson.processObjectNode(node, path);
	}
	
	@Test
	public void processArrayNodeTest() {
		doNothing().when(inrInventoryJsonToIntermediateJson).generateHelper(any(), any());
		ArrayNode node = realMapper.createArrayNode();
		node.add(1);
		inrInventoryJsonToIntermediateJson.processArrayNode(node, null);
	}
	
	@Test
	public void initializeRuleMapTest() throws SalesBusinessException {
		when(inrXmlToJsonRuleDaoResult.getInrXmlToJsonRuleMap()).thenReturn(inrXmlToJsonRuleMap);
		inrInventoryJsonToIntermediateJson.initializeRuleMap();
	}
}
