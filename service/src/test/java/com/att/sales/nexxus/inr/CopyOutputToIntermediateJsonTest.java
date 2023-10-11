package com.att.sales.nexxus.inr;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.framework.exception.SalesBusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
@ExtendWith(MockitoExtension.class)
public class CopyOutputToIntermediateJsonTest {
	
	@Spy
	@InjectMocks
	private CopyOutputToIntermediateJson copyOutputToIntermediateJson;
	
	@Mock
	private ObjectNode objectNode;
	
	@Mock
	private ArrayNode arrayNode;
	
	@Mock
	private JsonPath path;
	
	private ObjectMapper mapper = new ObjectMapper();
	
	@Test
	public void testCopyNxSiteId() throws SalesBusinessException {
		doNothing().when(copyOutputToIntermediateJson).copyNxSiteIdHelper(any(), any(), any());
		copyOutputToIntermediateJson.copyNxSiteId();
	}
	
	@Test
	public void testCopyNxSiteIdHelper() {
		when(objectNode.getNodeType()).thenReturn(JsonNodeType.ARRAY, JsonNodeType.OBJECT, JsonNodeType.STRING);
		doNothing().when(copyOutputToIntermediateJson).processArrayNode(any(), any(), any());
		doNothing().when(copyOutputToIntermediateJson).processObjectNode(any(), any(), any());
		copyOutputToIntermediateJson.copyNxSiteIdHelper(objectNode, null, objectNode);
		copyOutputToIntermediateJson.copyNxSiteIdHelper(objectNode, null, objectNode);
	}
	
	@Test
	public void testProcessArrayNode() {
		doNothing().when(copyOutputToIntermediateJson).copyNxSiteIdHelper(any(), any(), any());
		ArrayNode node = mapper.createArrayNode();
		node.add(1);
		copyOutputToIntermediateJson.processArrayNode(node, null, node);
	}
	
	@Test
	public void testProcessObjectNode() {
		doNothing().when(copyOutputToIntermediateJson).copyNxSiteIdHelper(any(), any(), any());
		ObjectNode nodes = mapper.createObjectNode();
		ObjectNode node1 = mapper.createObjectNode();
		node1.put("nxSiteId", 1);
		node1.put("nxSiteIdZ", 2);
		node1.put("endPointType", "A");
		nodes.set("site", node1);
		copyOutputToIntermediateJson.processObjectNode(nodes, path, nodes);
		copyOutputToIntermediateJson.processObjectNode(node1, path, node1);
	}

}
