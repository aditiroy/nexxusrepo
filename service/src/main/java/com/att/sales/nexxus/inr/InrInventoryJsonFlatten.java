package com.att.sales.nexxus.inr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.sales.nexxus.constant.InrConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class InrInventoryJsonFlatten {
	private static Logger log = LoggerFactory.getLogger(InrInventoryJsonFlatten.class);
	private ObjectMapper mapper;
	private JsonNode inventoryJson;
	private String rootTagValue;
	
	public InrInventoryJsonFlatten(ObjectMapper mapper, JsonNode inventoryJson, String rootTagValue) {
		this.mapper = mapper;
		this.inventoryJson = inventoryJson;
		this.rootTagValue = rootTagValue;
	}
	
	public ArrayNode generate() {
		log.debug("inventoryJson: {}", inventoryJson );
		ObjectNode data = mapper.createObjectNode();
		data.set("", inventoryJson);
		data.put(InrConstants.FLATTEN_ROOT_TAG, rootTagValue);
		ObjectNode parent = mapper.createObjectNode();
		parent.set("data", data);
		ObjectNode grantParent = mapper.createObjectNode();
		grantParent.set("parent", parent);
		flatten(grantParent, parent, "");
		ArrayNode res = postFlatten(parent);
		log.debug("flattened data for generating excel output: {}", res);
		return res;
	}
	
	protected ArrayNode postFlatten(JsonNode node) {
		int i = 1;
		JsonNode data = node.path("data");
		if (data.getNodeType() == JsonNodeType.ARRAY) {
			String falloutMatchingKey = null;
			String nxSiteMatchingKey = null;
			outer:
			for (int j = 0; j < data.size(); j++) {
				for (Iterator<String> itr = data.get(j).fieldNames(); itr.hasNext(); ) {
					String key = itr.next();
					if (falloutMatchingKey == null && key.endsWith(InrIntermediateJsonGenerator.FALLOUTMATCHINGID)) {
						falloutMatchingKey = key;
					}
					if (nxSiteMatchingKey == null && key.endsWith(InrIntermediateJsonGenerator.NXSITEMATCHINGID)) {
						nxSiteMatchingKey = key;
					}
					if (falloutMatchingKey != null && nxSiteMatchingKey != null) {
						break outer;
					}
				}
			}
			for (JsonNode n : data) {
				((ObjectNode) n).put("sequence", i++);
				if (falloutMatchingKey != null && !n.path(falloutMatchingKey).isMissingNode()) {
					((ObjectNode) n).put(InrIntermediateJsonGenerator.FALLOUTMATCHINGID, n.path(falloutMatchingKey).asText());
				}
				if (nxSiteMatchingKey != null && !n.path(nxSiteMatchingKey).isMissingNode()) {
					((ObjectNode) n).put(InrIntermediateJsonGenerator.NXSITEMATCHINGID, n.path(nxSiteMatchingKey).asInt());
				}
			}
			return (ArrayNode) data;
		} else {
			ObjectNode objCase = mapper.createObjectNode();
			node.fields().forEachRemaining(e -> {
				objCase.set(e.getKey().substring(5), e.getValue());
			});
			boolean falloutMatchingIdFound = false;
			boolean nxSiteMatchingIdFound = false;
			for (Iterator<String> itr = node.fieldNames(); itr.hasNext(); ) {
				String key = itr.next();
				if (key.endsWith(InrIntermediateJsonGenerator.FALLOUTMATCHINGID)) {
					objCase.put(InrIntermediateJsonGenerator.FALLOUTMATCHINGID, node.path(key).asText());
					falloutMatchingIdFound = true;
				} else if (key.endsWith(InrIntermediateJsonGenerator.NXSITEMATCHINGID)) {
					objCase.put(InrIntermediateJsonGenerator.NXSITEMATCHINGID, node.path(key).asInt());
					nxSiteMatchingIdFound = true;
				}
				if (falloutMatchingIdFound && nxSiteMatchingIdFound) {
					break;
				}
			}
			ArrayNode res = mapper.createArrayNode();
			objCase.put("sequence", i++);
			res.add(objCase);
			return res;
		}
	}
	
	protected void flatten(JsonNode parent, JsonNode node, String nodeName) {
		// recursive down
		if (node.getNodeType() == JsonNodeType.OBJECT) {
			List<String> childName = new ArrayList<>();
			List<JsonNode> childNode = new ArrayList<>();
			Iterator<Entry<String, JsonNode>> iterator = node.fields();
			iterator.forEachRemaining(entry -> {
				childName.add(entry.getKey());
				childNode.add(entry.getValue());
			});
			for (int i = 0; i < childName.size(); i++) {
				flatten(node, childNode.get(i), childName.get(i));
			}
		} else if (node.getNodeType() == JsonNodeType.ARRAY) {
			for (int i = 0; i < node.size(); i++) {
				flatten(node, node.get(i), "");
			}
		}
		// reduce current node
		if (node.getNodeType() == JsonNodeType.OBJECT && parent.getNodeType() == JsonNodeType.OBJECT) {
			List<JsonNodeWithName> fields = new ArrayList<>();
			List<JsonNodeWithName> arrays = new ArrayList<>();
			Iterator<Entry<String, JsonNode>> iterator = node.fields();
			iterator.forEachRemaining(entry -> {
				String childName = entry.getKey();
				JsonNode childNode = entry.getValue();
				if (childNode.getNodeType() == JsonNodeType.ARRAY) {
					arrays.add(new JsonNodeWithName(childName, childNode));
				} else {
					fields.add(new JsonNodeWithName(childName, childNode));
				}
			});
			ObjectNode parentObj = (ObjectNode) parent;
			if (arrays.isEmpty()) {
				for (JsonNodeWithName jnwn : fields) {
					parentObj.set(getCombinedNodeName(nodeName, jnwn.getName()), jnwn.getNode());
				}
				parentObj.remove(nodeName);
			} else {
				ArrayNode substitudeArrayNode = mapper.createArrayNode();
				int maxArrayLength = arrays.stream().map(jnwn -> jnwn.getNode()).mapToInt(jsonNode -> jsonNode.size()).max().orElse(0);
				for (int i = 0; i < maxArrayLength; i++) {
					ObjectNode objNode = mapper.createObjectNode();
					fields.forEach(jnwn -> {
						objNode.set(jnwn.getName(), jnwn.getNode());
					});
					int currentIndex = i;
					arrays.forEach(jnwn -> {
						String arrayName = jnwn.getName();
						JsonNode arrayNode = jnwn.getNode();
						if (currentIndex < arrayNode.size()) {
							JsonNode element = arrayNode.get(currentIndex);
							for (Iterator<Entry<String, JsonNode>> itr = element.fields(); itr.hasNext(); ) {
								Entry<String, JsonNode> elementField = itr.next();
								objNode.set(getCombinedNodeName(arrayName, elementField.getKey()), elementField.getValue());
							}
						}
					});
					substitudeArrayNode.add(objNode);
				}
				parentObj.remove(nodeName);
				parentObj.set(nodeName, substitudeArrayNode);
			}
		} else if (node.getNodeType() == JsonNodeType.ARRAY) {
			ArrayNode arrayNode = (ArrayNode) node;
			final int initialSize = arrayNode.size();
			for (int i = 0; i < initialSize; i++) {
				JsonNode element = arrayNode.get(i);
				List<JsonNodeWithName> fields = new ArrayList<>();
				List<JsonNodeWithName> arrays = new ArrayList<>();
				for (Iterator<Entry<String, JsonNode>> itr = element.fields(); itr.hasNext(); ) {
					Entry<String, JsonNode> elementField = itr.next();
					String childName = elementField.getKey();
					JsonNode childNode = elementField.getValue();
					if (childNode.getNodeType() == JsonNodeType.ARRAY) {
						arrays.add(new JsonNodeWithName(childName, childNode));
					} else {
						fields.add(new JsonNodeWithName(childName, childNode));
					}
				}
				if (arrays.isEmpty()) {
					arrayNode.add(element);
				} else {
					int maxArrayLength = arrays.stream().map(jnwn -> jnwn.getNode()).mapToInt(jsonNode -> jsonNode.size()).max().orElse(0);
					for (int j = 0; j < maxArrayLength; j++) {
						ObjectNode objNode = mapper.createObjectNode();
						fields.forEach(jnwn -> {
							objNode.set(jnwn.getName(), jnwn.getNode());
						});
						int currentIndex = j;
						arrays.forEach(jnwn -> {
							String arrayName = jnwn.getName();
							JsonNode innerArrayNode = jnwn.getNode();
							if (currentIndex < innerArrayNode.size()) {
								JsonNode innerElement = innerArrayNode.get(currentIndex);
								for (Iterator<Entry<String, JsonNode>> itr = innerElement.fields(); itr.hasNext(); ) {
									Entry<String, JsonNode> elementField = itr.next();
									objNode.set(getCombinedNodeName(arrayName, elementField.getKey()), elementField.getValue());
								}
							}
						});
						arrayNode.add(objNode);
					}
				}
			}
			for (int i = 0; i < initialSize; i++) {
				arrayNode.remove(0);
			}
		}
	}
	
	protected String getCombinedNodeName(String nodeName, String childName) {
		return nodeName + "/" + childName;
	}
}
