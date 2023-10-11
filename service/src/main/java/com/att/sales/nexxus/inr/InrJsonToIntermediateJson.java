package com.att.sales.nexxus.inr;

import java.util.Iterator;
import java.util.Map.Entry;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.dao.model.InrXmlToJsonRule;
import com.att.sales.nexxus.dao.repository.InrXmlToJsonRuleDao;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * The Class InrJsonToIntermediateJson.
 */
public class InrJsonToIntermediateJson extends InrIntermediateJsonGenerator {
	
	/** The raw json. */
	private JsonNode rawJson;

	/**
	 * Instantiates a new inr json to intermediate json.
	 *
	 * @param mapper the mapper
	 * @param inrXmlToJsonRuleDao the inr xml to json rule dao
	 * @param nxLookupDataRepository the nx lookup data repository
	 * @param rawJson the raw json
	 */
	public InrJsonToIntermediateJson(ObjectMapper mapper, InrXmlToJsonRuleDao inrXmlToJsonRuleDao,
			NxMyPriceRepositoryServce nxMyPriceRepositoryServce, JsonNode rawJson) {
		super(mapper, inrXmlToJsonRuleDao, nxMyPriceRepositoryServce);
		this.rawJson = rawJson;
	}

	/* (non-Javadoc)
	 * @see com.att.sales.nexxus.inr.InrIntermediateJsonGenerator#generate()
	 */
	@Override
	public JsonNode generate() throws SalesBusinessException {
		init();
		JsonPath rootPath = JsonPath.getRootPath();
		generateHelper(rawJson, rootPath);
		JsonNode res = nodeMap.get(InrConstants.ROOT_JSON_MAP_KEY);
		((ObjectNode) res).put(InrConstants.FLOW_TYPE, InrConstants.IGL);
		return res;
	}

	/**
	 * Generate helper.
	 *
	 * @param node the node
	 * @param path the path
	 */
	protected void generateHelper(JsonNode node, JsonPath path) {
		switch (node.getNodeType()) {
		case ARRAY:
			processArrayNode(node, path);
			break;
		case OBJECT:
			processObjectNode(node, path);
			break;
		default:
			processNonContainerNode(node, path);
			break;
		}
	}

	/**
	 * Process array node.
	 *
	 * @param node the node
	 * @param path the path
	 */
	protected void processArrayNode(JsonNode node, JsonPath path) {
		if (inrXmlToJsonRuleMap.containsKey(path.getPath())) {
			InrXmlToJsonRule inrXmlToJsonRule = inrXmlToJsonRuleMap.get(path.getPath());
			for (JsonNode arrayElement : node) {
				ObjectNode newNode = mapper.createObjectNode();
				nodeMap.put(inrXmlToJsonRule.getArrayElementName(), newNode);
				this.generateHelper(arrayElement, path);
				if (this.isNodeValid(inrXmlToJsonRule, newNode)) {
					((ObjectNode) nodeMap.get(inrXmlToJsonRule.getArrayParent()))
							.withArray(inrXmlToJsonRule.getArrayName()).add(newNode);
				}
			}
		} else {
			for (JsonNode arrayElement : node) {
				this.generateHelper(arrayElement, path);
			}
		}
	}

	/**
	 * Process object node.
	 *
	 * @param node the node
	 * @param path the path
	 */
	protected void processObjectNode(JsonNode node, JsonPath path) {
		if (inrXmlToJsonRuleMap.containsKey(path.getPath())) {
			InrXmlToJsonRule inrXmlToJsonRule = inrXmlToJsonRuleMap.get(path.getPath());
			ObjectNode newNode = mapper.createObjectNode();
			nodeMap.put(inrXmlToJsonRule.getObjectName(), newNode);
			for (Iterator<Entry<String, JsonNode>> i = node.fields(); i.hasNext();) {
				Entry<String, JsonNode> entry = i.next();
				JsonNode childNode = entry.getValue();
				String childName = entry.getKey();
				this.generateHelper(childNode, path.resolveContainerNode(childName));
			}
			if (this.isNodeValid(inrXmlToJsonRule, newNode)) {
				String jsonKey = inrXmlToJsonRule.getObjectName();
				((ObjectNode) nodeMap.get(inrXmlToJsonRule.getObjectParent())).set(jsonKey, newNode);
			}
		} else {
			for (Iterator<Entry<String, JsonNode>> i = node.fields(); i.hasNext();) {
				Entry<String, JsonNode> entry = i.next();
				JsonNode childNode = entry.getValue();
				String childName = entry.getKey();
				this.generateHelper(childNode, path.resolveContainerNode(childName));
			}
		}
	}

	/**
	 * Process non container node.
	 *
	 * @param node the node
	 * @param path the path
	 */
	protected void processNonContainerNode(JsonNode node, JsonPath path) {
		if (!node.isNull() && !node.asText().trim().isEmpty() && inrXmlToJsonRuleMap.containsKey(path.getPath())) {
			InrXmlToJsonRule inrXmlToJsonRule = inrXmlToJsonRuleMap.get(path.getPath());
			String jsonKey = inrXmlToJsonRule.getFieldName();
			String jsonParent = inrXmlToJsonRule.getFieldParent();
			if (inrXmlToJsonRule.getJsonType().contains(InrConstants.JSON_TYPE_FIELD_TAG)) {
				((ObjectNode) nodeMap.get(jsonParent)).put(inrXmlToJsonRule.getFieldNameForTag(), path.getFieldName());
			}
			if (inrXmlToJsonRule.getJsonType().contains(InrConstants.JSON_TYPE_FIELD_STR)) {
				((ObjectNode) nodeMap.get(jsonParent)).put(jsonKey, convertData(inrXmlToJsonRule, node.asText()));
			} else if (inrXmlToJsonRule.getJsonType().contains(InrConstants.JSON_TYPE_FIELD_DOUBLE)) {
				((ObjectNode) nodeMap.get(jsonParent)).put(jsonKey, node.asDouble());
			} else if (inrXmlToJsonRule.getJsonType().contains(InrConstants.JSON_TYPE_FIELD_INT)) {
				((ObjectNode) nodeMap.get(jsonParent)).put(jsonKey, node.asInt());
			} else if (inrXmlToJsonRule.getJsonType().contains(InrConstants.JSON_TYPE_FIELD_LONG)) {
				((ObjectNode) nodeMap.get(jsonParent)).put(jsonKey, node.asLong());
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.att.sales.nexxus.inr.InrIntermediateJsonGenerator#initializeRuleMap()
	 */
	@Override
	protected void initializeRuleMap() {
		inrXmlToJsonRuleMap = inrXmlToJsonRuleDao.getInrXmlToJsonRuleMap("accessPricingJson");
	}
}
