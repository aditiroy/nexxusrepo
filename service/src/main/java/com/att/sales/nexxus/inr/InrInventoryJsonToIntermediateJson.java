package com.att.sales.nexxus.inr;

import java.util.Iterator;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.dao.model.InrXmlToJsonRule;
import com.att.sales.nexxus.dao.repository.InrXmlToJsonRuleDao;
import com.att.sales.nexxus.dao.repository.InrXmlToJsonRuleDao.InrXmlToJsonRuleDaoResult;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class InrInventoryJsonToIntermediateJson extends InrIntermediateJsonGenerator {
	private static Logger log = LoggerFactory.getLogger(InrInventoryJsonToIntermediateJson.class);
	private JsonNode inventoryJson;
	private InrXmlToJsonRuleDaoResult inrXmlToJsonRuleDaoResult;

	protected InrInventoryJsonToIntermediateJson(ObjectMapper mapper, InrXmlToJsonRuleDao inrXmlToJsonRuleDao,
			NxMyPriceRepositoryServce nxMyPriceRepositoryServce, JsonNode inventoryJson, InrXmlToJsonRuleDaoResult inrXmlToJsonRuleDaoResult) {
		super(mapper, inrXmlToJsonRuleDao, nxMyPriceRepositoryServce);
		this.inventoryJson = inventoryJson;
		this.inrXmlToJsonRuleDaoResult = inrXmlToJsonRuleDaoResult;
	}

	@Override
	public JsonNode generate() throws SalesBusinessException {
		init();
		JsonPath rootPath = JsonPath.getRootPath();
		generateHelper(inventoryJson, rootPath);
		JsonNode res = nodeMap.get(InrConstants.ROOT_JSON_MAP_KEY);
		((ObjectNode) res).put(InrConstants.FLOW_TYPE, InrConstants.INR);
		return res;
	}

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
	
	

	protected void processNonContainerNode(JsonNode node, JsonPath path) {
		if (inrXmlToJsonRuleMap.containsKey(path.getPath())) {
			InrXmlToJsonRule inrXmlToJsonRule = inrXmlToJsonRuleMap.get(path.getPath());
			if ("Y".equals(inrXmlToJsonRule.getFieldNullYn())) {
				String jsonKey = inrXmlToJsonRule.getFieldName();
				String jsonParent = inrXmlToJsonRule.getFieldParent();
				((ObjectNode) nodeMap.get(jsonParent)).set(jsonKey, null);
			}
			if (!node.asText().isEmpty()) {
				String jsonKey = inrXmlToJsonRule.getFieldName();
				String jsonParent = inrXmlToJsonRule.getFieldParent();
				String data = node.asText();
				if (inrXmlToJsonRule.getJsonType().contains(InrConstants.JSON_TYPE_FIELD_TAG)) {
					((ObjectNode) nodeMap.get(jsonParent)).put(inrXmlToJsonRule.getFieldNameForTag(),
							path.getFieldName());
				}
				if (inrXmlToJsonRule.getJsonType().contains(InrConstants.JSON_TYPE_FIELD_STR)) {
					((ObjectNode) nodeMap.get(jsonParent)).put(jsonKey, convertData(inrXmlToJsonRule, data));
				} else if (inrXmlToJsonRule.getJsonType().contains(InrConstants.JSON_TYPE_FIELD_DOUBLE)) {
					((ObjectNode) nodeMap.get(jsonParent)).put(jsonKey, Double.valueOf(data));
				} else if (inrXmlToJsonRule.getJsonType().contains(InrConstants.JSON_TYPE_FIELD_INT)) {
					((ObjectNode) nodeMap.get(jsonParent)).put(jsonKey, Integer.valueOf(data));
				} else if (inrXmlToJsonRule.getJsonType().contains(InrConstants.JSON_TYPE_FIELD_LONG)) {
					((ObjectNode) nodeMap.get(jsonParent)).put(jsonKey, Long.valueOf(data));
				}
			}
		}
	}

	protected void processObjectNode(JsonNode node, JsonPath path) {
		if (inrXmlToJsonRuleMap.containsKey(path.getPath())) {
			InrXmlToJsonRule inrXmlToJsonRule = inrXmlToJsonRuleMap.get(path.getPath());
			if (inrXmlToJsonRule.getJsonType().contains(InrConstants.JSON_TYPE_OBJECT)) {
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
			} else if (inrXmlToJsonRule.getJsonType().contains(InrConstants.JSON_TYPE_ARRAY)) {
				ObjectNode newNode = mapper.createObjectNode();
				nodeMap.put(inrXmlToJsonRule.getArrayElementName(), newNode);
				for (Iterator<Entry<String, JsonNode>> i = node.fields(); i.hasNext();) {
					Entry<String, JsonNode> entry = i.next();
					JsonNode childNode = entry.getValue();
					String childName = entry.getKey();
					this.generateHelper(childNode, path.resolveContainerNode(childName));
				}
				if (isNodeValid(inrXmlToJsonRule, newNode)) {
					((ObjectNode) nodeMap.get(inrXmlToJsonRule.getArrayParent()))
							.withArray(inrXmlToJsonRule.getArrayName()).add(newNode);
				}
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

	protected void processArrayNode(JsonNode node, JsonPath path) {
		for (JsonNode arrayElement : node) {
			this.generateHelper(arrayElement, path);
		}
	}

	@Override
	protected void initializeRuleMap() throws SalesBusinessException {
		inrXmlToJsonRuleMap = inrXmlToJsonRuleDaoResult.getInrXmlToJsonRuleMap();
	}

}
