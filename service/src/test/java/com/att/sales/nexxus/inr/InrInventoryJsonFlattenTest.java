package com.att.sales.nexxus.inr;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@ExtendWith(MockitoExtension.class)
public class InrInventoryJsonFlattenTest {
	
	@Spy
	@InjectMocks
	private InrInventoryJsonFlatten inrInventoryJsonFlatten;
	
	private ObjectMapper realMapper = new ObjectMapper();
	
	@Test
	public void generateTest() {
		ReflectionTestUtils.setField(inrInventoryJsonFlatten, "mapper", realMapper);
		ArrayNode arrayNode = realMapper.createArrayNode();
		doNothing().when(inrInventoryJsonFlatten).flatten(any(), any(), any());
		doReturn(arrayNode).when(inrInventoryJsonFlatten).postFlatten(any());
		
		ArrayNode res = inrInventoryJsonFlatten.generate();
		assertSame(arrayNode, res);
	}
	
	@Test
	public void postFlattenTest() {
		ObjectNode node = realMapper.createObjectNode();
		ArrayNode data = realMapper.createArrayNode();
		node.set("data", data);
		ObjectNode dataElement = realMapper.createObjectNode();
		dataElement.put("endWithFALLOUTMATCHINGID", "1");
		data.add(dataElement).add(dataElement);
		
		ArrayNode res = inrInventoryJsonFlatten.postFlatten(node);
		assertSame(data, res);
		
		ReflectionTestUtils.setField(inrInventoryJsonFlatten, "mapper", realMapper);
		node.remove("data");
		node.put("endWithFALLOUTMATCHINGID", "1");
		inrInventoryJsonFlatten.postFlatten(node);
	}
	
	@Test
	public void flattenTest() throws IOException {
		ReflectionTestUtils.setField(inrInventoryJsonFlatten, "mapper", realMapper);
		String json = "{\r\n" + 
				"    \"parent\": {\r\n" + 
				"        \"data\": {\r\n" + 
				"            \"\": {\r\n" + 
				"                \"root\": {\r\n" + 
				"                    \"site\": {\r\n" + 
				"                        \"address\": {\r\n" + 
				"                            \"room\": \"room\",\r\n" + 
				"                            \"floor\": \"floor\"\r\n" + 
				"                        },\r\n" + 
				"                        \"desgin\": [{\r\n" + 
				"                                \"speed\": \"speed\",\r\n" + 
				"                                \"feature\": [{\r\n" + 
				"                                        \"a\": \"a\"\r\n" + 
				"                                    }, {\r\n" + 
				"                                        \"a\": \"a\"\r\n" + 
				"                                    }\r\n" + 
				"                                ]\r\n" + 
				"                            }, {\r\n" + 
				"                                \"speed\": \"speed\",\r\n" + 
				"                                \"feature\": [{\r\n" + 
				"                                        \"a\": \"a\"\r\n" + 
				"                                    }, {\r\n" + 
				"                                        \"a\": \"a\"\r\n" + 
				"                                    }\r\n" + 
				"                                ]\r\n" + 
				"                            }\r\n" + 
				"                        ]\r\n" + 
				"                    }\r\n" + 
				"                }\r\n" + 
				"            }\r\n" + 
				"        }\r\n" + 
				"    }\r\n" + 
				"}";
		JsonNode grandParent = realMapper.readTree(json);
		inrInventoryJsonFlatten.flatten(grandParent, grandParent.path("parent"), "");
	}
}
