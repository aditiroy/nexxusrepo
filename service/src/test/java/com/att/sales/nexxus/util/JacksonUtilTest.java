package com.att.sales.nexxus.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;


import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.reteriveicb.model.Solution;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JacksonUtilTest {
	private ObjectMapper mapper = new ObjectMapper();
	
	@Test
	public void constructorTest() {
		JacksonUtil jacksonUtil = new JacksonUtil();
		assertNotNull(jacksonUtil);
	}
	
	@Test
	public void fromStringTest() {
		String string = "{}";
		Solution solution = JacksonUtil.fromString(string, Solution.class);
		assertNotNull(solution);
		
		string = null;
		solution = JacksonUtil.fromString(null, Solution.class);
		assertNull(solution);
	}
	
	@Test
	public void fromStringExceptionTest() {
		String string = "{";
		JacksonUtil.fromString(string, Solution.class);
	}
	
	@Test
	public void fromStringForCodeHausTest() {
		String string = "{}";
		Solution solution = JacksonUtil.fromStringForCodeHaus(string, Solution.class);
		assertNotNull(solution);
		
		string = null;
		solution = JacksonUtil.fromStringForCodeHaus(null, Solution.class);
		assertNull(solution);
	}
	
	@Test
	public void fromStringForCodeHausExceptionTest() {
		String string = "{";
		JacksonUtil.fromStringForCodeHaus(string, Solution.class);
	}
	
	@Test
	public void toStringTest() {
		Solution solution = new Solution();
		String res = JacksonUtil.toString(solution);
		assertFalse(res.isEmpty());
		
		res = JacksonUtil.toString(null);
		assertNull(res);
	}
	
	@Test
	public void toStringForCodeHausTest() {
		Solution solution = new Solution();
		String res = JacksonUtil.toStringForCodeHaus(solution);
		assertFalse(res.isEmpty());
		
		res = JacksonUtil.toStringForCodeHaus(null);
		assertNull(res);
	}
	
	@Test
	public void toJsonNodeTest() {
		String value = "{}";
		JsonNode res = JacksonUtil.toJsonNode(value);
		assertNotNull(res);
	}
	
	@Test
	public void toJsonNodeExceptionTest() {
		String value = "{";
		JacksonUtil.toJsonNode(value);
	}
	
	@Test
	public void jsonNodeFromObjTest() {
		Solution solution = new Solution();
		JsonNode res = JacksonUtil.jsonNodeFromObj(solution);
		assertNotNull(res);
		
		res = JacksonUtil.jsonNodeFromObj(null);
		assertNull(res);
	}
	
	@Test
	public void cloneTest() {
		Solution solution = new Solution();
		Solution res = JacksonUtil.clone(solution);
		assertNotNull(res);
	}
	
	@Test
	public void trimJsonTest() {
		String json = "{\r\n" + 
				"    \"obj1\": {\r\n" + 
				"        \"f\": null\r\n" + 
				"    },\r\n" + 
				"    \"obj2\": {\r\n" + 
				"        \"f\": \"v\"\r\n" + 
				"    },\r\n" + 
				"    \"array1\": [{}, null],\r\n" + 
				"    \"array2\": [1]\r\n" + 
				"}";
		JsonNode jsonNode = JacksonUtil.toJsonNode(json);
		JacksonUtil.trimJson(jsonNode);
		assertFalse(jsonNode.has("obj1"));
		assertFalse(jsonNode.has("array1"));
	}
	
	@Test
	public void nodeAsTextNullToEmptyStringTest() {
		ObjectNode objectNode = mapper.createObjectNode();
		objectNode.put("f", "v");
		String res = JacksonUtil.nodeAsTextNullToEmptyString(objectNode.path("f"));
		assertEquals("v", res);
		
		res = JacksonUtil.nodeAsTextNullToEmptyString(null);
		assertEquals("", res);
	}
	
	@Test
	public void nodeAtPointerAsTextNullToEmptyStringTest() {
		ObjectNode objectNode = mapper.createObjectNode();
		objectNode.put("f", "v");
		String res = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(objectNode, "/f");
		assertEquals("v", res);

		res = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(objectNode, "/notExist");
		assertEquals("", res);
	}
	
	@Test
	public void nodeAtPointerAsTextTest() {
		ObjectNode objectNode = mapper.createObjectNode();
		objectNode.put("f", "v");
		String res = JacksonUtil.nodeAtPointerAsText(objectNode, "/f");
		assertEquals("v", res);

		res = JacksonUtil.nodeAtPointerAsText(objectNode, "/notExist");
		assertNull(res);
	}
	
	@Test
	public void objectNodePutStringValueIgnoreNullAndEmptyTest() {
		ObjectNode objectNode = mapper.createObjectNode();
		JacksonUtil.objectNodePutStringValueIgnoreNullAndEmpty(objectNode, "f", "v");
		assertEquals("v", objectNode.path("f").asText());
		
		JacksonUtil.objectNodePutStringValueIgnoreNullAndEmpty(null, "f", "v");
	}
	
	@Test
	public void toJsonObjectTest() {
		String value = "{}";
		JSONObject jsonObject = JacksonUtil.toJsonObject(value);
		assertNotNull(jsonObject);
		
		jsonObject = JacksonUtil.toJsonObject(null);
		assertNull(jsonObject);
	}
	
	@Test
	public void toJsonObjectExceptionTest() {
		String value = "{";
		JacksonUtil.toJsonObject(value);
	}
	
	@Test
	public void toListTest() {
		String value = "[{}]";
		List<Solution> list = JacksonUtil.toList(value, new TypeReference<List<Solution>>() {});
		assertFalse(list.isEmpty());
		
		list = JacksonUtil.toList(null, new TypeReference<List<Solution>>() {});
		assertNull(list);
	}
	
	@Test
	public void toListExceptionTest() {
		String value = "[{}";
		JacksonUtil.toList(value, new TypeReference<List<Solution>>() {});
	}
	
	@Test
	public void toJsonArrayTest() {
		String value = "[]";
		JSONArray jsonArray = JacksonUtil.toJsonArray(value);
		assertNotNull(jsonArray);
		
		jsonArray = JacksonUtil.toJsonArray(null);
		assertNull(jsonArray);
	}
	
	@Test
	public void toJsonArrayExceptionTest() {
		String value = "[";
		JacksonUtil.toJsonArray(value);
	}
	
	@Test
	public void convertObjectToJsonObjectTest() {
		Solution solution = new Solution();
		JSONObject res = JacksonUtil.convertObjectToJsonObject(solution);
		assertNotNull(res);
	}
	
	@Test
	public void convertObjectToJsonArrayTest() {
		Solution solution = new Solution();
		JSONArray res = JacksonUtil.convertObjectToJsonArray(Arrays.asList(solution));
		assertFalse(res.isEmpty());
	}
	
	@Test
	public void deepCopyTest() {
		String json = "{\r\n" + 
				"    \"obj\": {},\r\n" + 
				"    \"array1\": [{}],\r\n" + 
				"	\"array2\": [[]],\r\n" + 
				"	\"array3\": [\"v\"],\r\n" + 
				"    \"f\": \"v\"\r\n" + 
				"}";
		JSONObject jsonObject = JacksonUtil.toJsonObject(json);
		JSONObject deepCopy = JacksonUtil.deepCopy(jsonObject, Arrays.asList("obj", "array1", "array2", "array3", "f"));
		assertNotNull(deepCopy);
	}
	
	@Test
	public void toXmlStringTest() throws SalesBusinessException {
		Solution solution = new Solution();
		String res = JacksonUtil.toXmlString(solution);
		assertNotNull(res);
		
		res = JacksonUtil.toXmlString(null);
		assertNull(res);
	}
	
	@Test
	public void arrayNodeToListTest() {
		ArrayNode arrayNode = mapper.createArrayNode();
		arrayNode.add(1);
		List<JsonNode> res = JacksonUtil.arrayNodeToList(arrayNode);
		assertFalse(res.isEmpty());
	}
	
	@Test
	public void listToArrayNodeTest() {
		List<JsonNode> list = Arrays.asList((JsonNode) mapper.createObjectNode());
		JsonNode res = JacksonUtil.listToArrayNode(list);
		assertTrue(res.size() > 0);
	}
	
	@Test
	public void findComponentNodeTest() throws IOException {
		String json = "{\r\n" + 
				"    \"component\": [{\r\n" + 
				"            \"componentCodeId\": 10\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
		JsonNode node = mapper.readTree(json);
		JsonNode res = JacksonUtil.findComponentNode(node, 10);
		assertNotNull(res);
		
		res = JacksonUtil.findComponentNode(node.path("missingNode"), 10);
		assertNull(res);
	}
	
	@Test
	public void findFirstDesignDetailInComponentNodeTest() throws IOException {
		String json = "{\r\n" + 
				"    \"designDetails\": [{\r\n" + 
				"            \"udfId\": 10\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
		JsonNode node = mapper.readTree(json);
		Optional<JsonNode> res = JacksonUtil.findFirstDesignDetailInComponentNode(node, 10);
		assertTrue(res.isPresent());
		
		res = JacksonUtil.findFirstDesignDetailInComponentNode(node.path("missingNode"), 10);
		assertFalse(res.isPresent());
	}

	@Test
	public void findUdfAttributeIdFromComponentNodeTest() throws IOException {
		String json = "{\r\n" + 
				"    \"designDetails\": [{\r\n" + 
				"            \"udfId\": 10,\r\n" + 
				"            \"udfAttributeId\": [1, 2]\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
		JsonNode node = mapper.readTree(json);
		Long res = JacksonUtil.findUdfAttributeIdFromComponentNode(node, 10);
		assertEquals(1, res.longValue());
		
		res = JacksonUtil.findUdfAttributeIdFromComponentNode(node, 1);
		assertNull(res);
	}
	
	@Test
	public void findUdfAttributeTextFromComponentNodeTest() throws IOException {
		String json = "{\r\n" + 
				"    \"designDetails\": [{\r\n" + 
				"            \"udfId\": 10,\r\n" + 
				"            \"udfAttributeText\": [\"1\", \"2\"]\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
		JsonNode node = mapper.readTree(json);
		String res = JacksonUtil.findUdfAttributeTextFromComponentNode(node, 10);
		assertEquals("1", res);
		
		res = JacksonUtil.findUdfAttributeTextFromComponentNode(node, 1);
		assertNull(res);
	}
}
