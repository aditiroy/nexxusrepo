package com.att.sales.nexxus.inr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.att.sales.framework.exception.SalesBusinessException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author sj0546
 *
 */
public class CopyOutputToIntermediateJson {
	private JsonNode outputJson;
	private JsonNode intermediateJson;

	/**
	 * Constructor to create object
	 * 
	 * @param outputJson
	 * @param intermediateJson
	 */
	public CopyOutputToIntermediateJson(JsonNode outputJson,  JsonNode intermediateJson) {
		this.outputJson = outputJson;
		this.intermediateJson = intermediateJson;
	}

	/**
	 * Initiate the inr json traversing
	 * 
	 * @throws SalesBusinessException
	 */
	public void copyNxSiteId() throws SalesBusinessException {
		JsonPath rootPath = JsonPath.getRootPath();
		copyNxSiteIdHelper(outputJson, rootPath, intermediateJson);
	}

	/**
	 * Helper method to traverse the node
	 * 
	 * @param src
	 * @param path
	 * @param dest
	 */
	protected void copyNxSiteIdHelper(JsonNode src, JsonPath path, JsonNode dest) {
		switch (src.getNodeType()) {
		case ARRAY:
			processArrayNode(src, path, dest);
			break;
		case OBJECT:
			processObjectNode(src, path, dest);
			break;
		default:
			break;
		}
	}
	
	/**
	 * Traverse the array node
	 * 
	 * @param outputJsonNode
	 * @param path
	 * @param intermediateJsonNode
	 */
	protected void processArrayNode(JsonNode outputJsonNode, JsonPath path, JsonNode intermediateJsonNode) {
		for (int i = 0; i < outputJsonNode.size(); i++) {
			copyNxSiteIdHelper(outputJsonNode.get(i), path, intermediateJsonNode.get(i));
		}
		
	}

	/**
	 * Traverse the object node
	 * 
	 * @param sourceNode
	 * @param path
	 * @param destNode
	 */
	protected void processObjectNode(JsonNode sourceNode, JsonPath path, JsonNode destNode) {
		if (sourceNode.has("nxSiteId")) {
			((ObjectNode) destNode).put("nxSiteId", ((ObjectNode) sourceNode).get("nxSiteId").asInt());
			
			if (sourceNode.has("endPointType")) {
				((ObjectNode) destNode).put("endPointType", ((ObjectNode) sourceNode).get("endPointType").asText());
			}
			if (sourceNode.has("nxSiteIdZ")) {
				((ObjectNode) destNode).put("nxSiteIdZ", ((ObjectNode) sourceNode).get("nxSiteIdZ").asInt());
			}
		}else{
			// iterate in list rather than using iterator to avoid
			// java.util.ConcurrentModificationException
			List<String> srcNodeChildName = new ArrayList<>();
			List<JsonNode> srcChildNode = new ArrayList<>();
			Iterator<Entry<String, JsonNode>> srcNodeIterator = sourceNode.fields();
			srcNodeIterator.forEachRemaining(entry -> {
				if (entry.getValue().getNodeType() == JsonNodeType.OBJECT || entry.getValue().getNodeType() == JsonNodeType.ARRAY) {
					srcNodeChildName.add(entry.getKey());
					srcChildNode.add(entry.getValue());
				}
			});
			
			
			List<String> destNodeChildName = new ArrayList<>();
			List<JsonNode> destChildNode = new ArrayList<>();
			Iterator<Entry<String, JsonNode>> destNodeiterator = destNode.fields();
			destNodeiterator.forEachRemaining(entry -> {
				if (entry.getValue().getNodeType() == JsonNodeType.OBJECT || entry.getValue().getNodeType() == JsonNodeType.ARRAY) {
					destNodeChildName.add(entry.getKey());
					destChildNode.add(entry.getValue());
				}
			});
			for (int i = 0; i < srcNodeChildName.size(); i++) {
				this.copyNxSiteIdHelper(srcChildNode.get(i), path.resolveContainerNode(srcNodeChildName.get(i)), destChildNode.get(i));
			}
		}
	}
}
