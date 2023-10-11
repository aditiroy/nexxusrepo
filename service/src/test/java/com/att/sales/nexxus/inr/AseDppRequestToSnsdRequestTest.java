package com.att.sales.nexxus.inr;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.io.IOException;
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
import com.att.sales.nexxus.dao.repository.SalesMsDao;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
@ExtendWith(MockitoExtension.class)
public class AseDppRequestToSnsdRequestTest {
	@Spy
	@InjectMocks
	private AseDppRequestToSnsdRequest aseDppRequestToSnsdRequest;
	@Mock
	private ObjectMapper mapper;
	@Mock
	private InrXmlToJsonRuleDao inrXmlToJsonRuleDao;
	@Mock
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	@Mock
	private JsonNode dppRequest;
	@Mock
	private SalesMsDao salesMsDao;
	@Mock
	private Map<String, JsonNode> nodeMap;
	@Mock
	private ObjectNode objectNode;
	@Mock
	private ArrayNode arrayNode;
	@Mock
	private Map<String, InrXmlToJsonRule> inrXmlToJsonRuleMap;

	private ObjectMapper realMapper = new ObjectMapper();

	@Test
	public void generateTest() throws SalesBusinessException {
		doNothing().when(aseDppRequestToSnsdRequest).init();
		ReflectionTestUtils.setField(aseDppRequestToSnsdRequest, "nodeMap", nodeMap);
		when(nodeMap.get(any())).thenReturn(objectNode);
		doNothing().when(aseDppRequestToSnsdRequest).generateHelper(any(), any());
		assertSame(objectNode, aseDppRequestToSnsdRequest.generate());
	}

	@Test
	public void generateHelperTest() {
		doNothing().when(aseDppRequestToSnsdRequest).processArrayNode(any(), any());
		doNothing().when(aseDppRequestToSnsdRequest).processObjectNode(any(), any());
		doNothing().when(aseDppRequestToSnsdRequest).processNonContainerNode(any(), any());
		when(objectNode.getNodeType()).thenReturn(JsonNodeType.ARRAY, JsonNodeType.OBJECT, JsonNodeType.STRING);
		aseDppRequestToSnsdRequest.generateHelper(objectNode, null);
		aseDppRequestToSnsdRequest.generateHelper(objectNode, null);
		aseDppRequestToSnsdRequest.generateHelper(objectNode, null);
	}

	@Test
	public void processArrayNodeTest() {
		ArrayNode arrayNode = realMapper.createArrayNode();
		arrayNode.add(objectNode);
		doNothing().when(aseDppRequestToSnsdRequest).generateHelper(any(), any());
		aseDppRequestToSnsdRequest.processArrayNode(arrayNode, null);
	}

	@Test
	public void processObjectNodeTest() throws IOException {
		String dpp = "{\r\n" + "    \"solution\": {\r\n" + "        \"imsProductNumber\": 1,\r\n"
				+ "		\"annualRevenue\": 1.1,\r\n" + "		\"monthlyRevenue\": 1.1\r\n" + "    }\r\n" + "}";
		JsonNode dppTree = realMapper.readTree(dpp);
		JsonPath path = new JsonPath("/solution/offers/site");
		ObjectNode node = realMapper.createObjectNode();
		doNothing().when(aseDppRequestToSnsdRequest).doObjectNode(any(), any());
		ReflectionTestUtils.setField(aseDppRequestToSnsdRequest, "mapper", realMapper);
		ReflectionTestUtils.setField(aseDppRequestToSnsdRequest, "inrXmlToJsonRuleMap", inrXmlToJsonRuleMap);
		ReflectionTestUtils.setField(aseDppRequestToSnsdRequest, "nodeMap", nodeMap);
		ReflectionTestUtils.setField(aseDppRequestToSnsdRequest, "dppRequest", dppTree);
		when(inrXmlToJsonRuleMap.containsKey(any())).thenReturn(true);
		InrXmlToJsonRule inrXmlToJsonRule = new InrXmlToJsonRule();
		when(inrXmlToJsonRuleMap.get(any())).thenReturn(inrXmlToJsonRule);
		when(salesMsDao.getOfferNameByOfferId(anyInt())).thenReturn("ASENoD");

		node.put("address1", "address1");
		node.put("address2", "address2");
		node.put("room", "room");
		node.put("floor", "floor");
		node.put("building", "building");
		ArrayNode lconArray = realMapper.createArrayNode();
		node.set("lconDetails", lconArray);
		ObjectNode lconArrayElement = realMapper.createObjectNode();
		lconArray.add(lconArrayElement);
		lconArrayElement.put("lconType", "Primary");
		lconArrayElement.put("lconFirstName", "lconFirstName");
		lconArrayElement.put("lconLastName", "lconLastName");
		lconArrayElement.put("lconPhone", "lconPhone");
		lconArrayElement.put("lconEmail", "lconEmail");
		node.set("macdActivity", lconArray);
		doReturn("macdActivity").when(aseDppRequestToSnsdRequest).convertData(anyString(), anyString());
		node.put("crdd", "crdd");
		ReflectionTestUtils.setField(aseDppRequestToSnsdRequest, "automationInd", "Y");
		node.put("portConnectionType", "1024");
		node.put("portInterfaceType", "04LN9.1CT");
		when(nodeMap.get(any())).thenReturn(objectNode);
		when(objectNode.withArray(any())).thenReturn(arrayNode);
		aseDppRequestToSnsdRequest.processObjectNode(node, path);

		lconArrayElement.put("lconType", "Alternate");
		ObjectNode macdActivityNodeObj = realMapper.createObjectNode();
		node.set("macdActivity", macdActivityNodeObj);
		ReflectionTestUtils.setField(aseDppRequestToSnsdRequest, "automationInd", "N");
		node.put("macdType", "Add");
		node.put("designCertification", "notCertified");
		node.put("portInterfaceType", "1");
		aseDppRequestToSnsdRequest.processObjectNode(node, path);

		lconArrayElement.put("lconType", "Building");
		node.put("designCertification", "Certified");
		node.put("portConnectionType", "100");
		aseDppRequestToSnsdRequest.processObjectNode(node, path);

		lconArrayElement.put("lconType", "unknown");
		node.put("macdType", "Change");
		node.put("designCertification", "notCertified");
		node.put("portConnectionType", "10240");
		aseDppRequestToSnsdRequest.processObjectNode(node, path);

		node.put("designCertification", "Certified");
		node.put("macdTypeOfChange", "SIMPLE_MACD");
		node.put("portConnectionType", "102400");
		aseDppRequestToSnsdRequest.processObjectNode(node, path);

		node.put("macdTypeOfChange", "other");
		node.put("portConnectionType", "other");
		aseDppRequestToSnsdRequest.processObjectNode(node, path);

		path = new JsonPath("/solution/offers/circuit");
		ObjectNode componentObj = realMapper.createObjectNode();
		node.withArray("component").add(componentObj);
		componentObj.set("macdActivity", lconArray);
		aseDppRequestToSnsdRequest.processObjectNode(node, path);

		componentObj.set("macdActivity", macdActivityNodeObj);
		aseDppRequestToSnsdRequest.processObjectNode(node, path);

		path = new JsonPath("/solution/offers/circuit/endpoint");
		node.put("aEndpoint", "aEndpoint");
		ObjectNode siteObj = realMapper.createObjectNode();
		node.set("site", siteObj);
		siteObj.put("address1", "address1");
		siteObj.put("address2", "address2");
		siteObj.put("room", "room");
		siteObj.put("floor", "floor");
		siteObj.put("building", "building");
		siteObj.set("lconDetails", lconArray);
		lconArrayElement.put("lconType", "Primary");
		siteObj.put("crdd", "crdd");
		aseDppRequestToSnsdRequest.processObjectNode(node, path);

		lconArrayElement.put("lconType", "Alternate");
		node.remove("aEndpoint");
		node.put("zEndpoint", "zEndpoint");
		aseDppRequestToSnsdRequest.processObjectNode(node, path);

		lconArrayElement.put("lconType", "Building");
		aseDppRequestToSnsdRequest.processObjectNode(node, path);

		lconArrayElement.put("lconType", "unKnown");
		aseDppRequestToSnsdRequest.processObjectNode(node, path);

		when(inrXmlToJsonRuleMap.containsKey(any())).thenReturn(false);
		aseDppRequestToSnsdRequest.processObjectNode(node, path);
	}

	@Test
	public void doObjectNodeTest() {
		doNothing().when(aseDppRequestToSnsdRequest).generateHelper(any(), any());
		ObjectNode node = realMapper.createObjectNode();
		node.put("field", "value");
		ObjectNode objNode = realMapper.createObjectNode();
		node.set("objNode", objNode);
		JsonPath rootPath = JsonPath.getRootPath();
		aseDppRequestToSnsdRequest.doObjectNode(node, rootPath);
	}

	@Test
	public void processNonContainerNodeTest() {
		JsonPath path = new JsonPath("/offerId");
		ObjectNode node = realMapper.createObjectNode();
		node.put("offerId", 1);
		ReflectionTestUtils.setField(aseDppRequestToSnsdRequest, "inrXmlToJsonRuleMap", inrXmlToJsonRuleMap);
		when(inrXmlToJsonRuleMap.containsKey(any())).thenReturn(true);
		InrXmlToJsonRule inrXmlToJsonRule = new InrXmlToJsonRule();
		when(inrXmlToJsonRuleMap.get(any())).thenReturn(inrXmlToJsonRule);
		ReflectionTestUtils.setField(aseDppRequestToSnsdRequest, "nodeMap", nodeMap);
		when(nodeMap.get(any())).thenReturn(objectNode);
		doReturn("macdActivity").when(aseDppRequestToSnsdRequest).convertData(any(InrXmlToJsonRule.class), anyString());
		inrXmlToJsonRule.setJsonType(InrConstants.JSON_TYPE_FIELD_TAG);

		aseDppRequestToSnsdRequest.processNonContainerNode(node.path("offerId"), path);

		node.put("automationInd", "Y");
		path = new JsonPath("/automationInd");
		aseDppRequestToSnsdRequest.processNonContainerNode(node.path("automationInd"), path);

		path = new JsonPath("/offerId");
		inrXmlToJsonRule.setJsonType(InrConstants.JSON_TYPE_FIELD_TAG);
		aseDppRequestToSnsdRequest.processNonContainerNode(node.path("offerId"), path);

		inrXmlToJsonRule.setJsonType(InrConstants.JSON_TYPE_FIELD_STR);
		aseDppRequestToSnsdRequest.processNonContainerNode(node.path("offerId"), path);

		inrXmlToJsonRule.setJsonType(InrConstants.JSON_TYPE_FIELD_DOUBLE);
		aseDppRequestToSnsdRequest.processNonContainerNode(node.path("offerId"), path);

		inrXmlToJsonRule.setJsonType(InrConstants.JSON_TYPE_FIELD_INT);
		aseDppRequestToSnsdRequest.processNonContainerNode(node.path("offerId"), path);

		inrXmlToJsonRule.setJsonType(InrConstants.JSON_TYPE_FIELD_LONG);
		aseDppRequestToSnsdRequest.processNonContainerNode(node.path("offerId"), path);
	}

	@Test
	public void initializeRuleMapTest() throws SalesBusinessException {
		when(inrXmlToJsonRuleDao.getInrXmlToJsonRuleMap(any())).thenReturn(inrXmlToJsonRuleMap);
		aseDppRequestToSnsdRequest.initializeRuleMap();
	}
}
