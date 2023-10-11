package com.att.sales.nexxus.inr;

import com.att.sales.framework.service.BaseServiceImpl;

/*
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.dao.model.NxDwPriceDetails;
import com.att.sales.nexxus.dao.model.NxDwToJsonRules;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.repository.NxDwPriceDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxDwToJsonRulesRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.UsrpDao;
import com.att.sales.nexxus.service.InrProcessingService;
import com.att.sales.nexxus.service.InrQualifyService;
import com.att.sales.nexxus.service.MessageConsumptionServiceImpl;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
//xy3208 to be deleted
//@Service
*/ 

public class UsrpDesignServiceImpl extends BaseServiceImpl {
	/*
	private static Logger logger = LoggerFactory.getLogger(UsrpDesignServiceImpl.class);
	private static String RULE_NAME = "DESIGN_JSON";
	private static String Y = "Y";
	private static String USRP_QUERY_RULE = "USRP_QUERY_RULE";

	@Autowired
	private NxDwToJsonRulesRepository nxDwToJsonRulesRepository;

	@Autowired
	private NxDwPriceDetailsRepository nxDwPriceDetailsRepository;

	@Autowired
	private NxRequestDetailsRepository nxRequestDetailsRepository;

	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;

	@Autowired
	private UsrpDao usrpDao;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	@Autowired
	private InrProcessingService inrProcessingService;
	
	@Autowired
	private InrQualifyService inrQualifyService;
	
	@Autowired
	private MessageConsumptionServiceImpl messageConsumptionServiceImpl;

	public ServiceResponse usrpDesign(UsrpDesignRequest request) throws SalesBusinessException {
		logger.info("enter usrpDesign method");
		ServiceResponse response = new ServiceResponse();
		if (true) {
			return response;
		}
		NxDwPriceDetails nxDwPriceDetails = nxDwPriceDetailsRepository.findOne(request.getId());
		NxRequestDetails nxRequestDetails = nxRequestDetailsRepository.findOne(nxDwPriceDetails.getNxReqId());
		List<NxDwToJsonRules> rules = nxDwToJsonRulesRepository
				.findByOfferAndRuleNameAndActive(nxRequestDetails.getProduct(), RULE_NAME, Y);
		Map<String, NxDwToJsonRules> jsonBuildRuleMap = rules.stream()
				.collect(Collectors.toMap(NxDwToJsonRules::getFieldName, Function.identity()));
		NxLookupData nxLookupData = nxLookupDataRepository.findTopByDatasetNameAndItemId(USRP_QUERY_RULE,
				nxRequestDetails.getProduct());
		UsrpQueryRule queryRule;
		JsonNode priceJson;
		try {
			queryRule = mapper.readValue(nxLookupData.getCriteria(), UsrpQueryRule.class);
			priceJson = mapper.readTree(nxDwPriceDetails.getPriceJson());
		} catch (IOException e) {
			logger.info("Exception:", e);
			throw new SalesBusinessException();
		}
		updatePriceJson(priceJson, queryRule, jsonBuildRuleMap);
		((ObjectNode) priceJson).put(InrConstants.SOURCE, InrConstants.USRP);
		ObjectNode inventoryJson = mapper.createObjectNode();
		inventoryJson.set(queryRule.getRootTagName(), priceJson);
		NxOutputFileModel nxOutputFileModel = inrProcessingService.createInrNexusOutput(nxRequestDetails, inventoryJson);
		nxRequestDetails.addNxOutputFiles(nxOutputFileModel);
		nxRequestDetailsRepository.saveAndFlush(nxRequestDetails);
		inrQualifyService.inrQualifyCheck(nxRequestDetails.getNxReqId(), true, null);
		messageConsumptionServiceImpl.sendMailNotification(nxRequestDetails);
		return response;
	}

	protected void updatePriceJson(JsonNode priceJson, UsrpQueryRule queryRule,
			Map<String, NxDwToJsonRules> jsonBuildRuleMap) {
		JsonPath rootPath = JsonPath.getRootPath();
		Map<String, JsonNode> nodeMap = new HashMap<>();
		nodeMap.put(InrConstants.ROOT_JSON_MAP_KEY, priceJson);
		traverseHelper(priceJson, rootPath, queryRule, jsonBuildRuleMap, nodeMap);
		updateAfterTraverse(priceJson);
	}

	protected void updateAfterTraverse(JsonNode priceJson) {
		Map<String, String> nxt1SecondaryCircuitIdToPrimaryCircuitId = new HashMap<>();
		List<JsonNode> designs = priceJson.findValues("design");
		for (JsonNode designArray : designs) {
			for (JsonNode design : designArray) {
				boolean nxt1QuantityUpdateFlag = false;
				int qty = 0;
				boolean ethernetNoFalloutFlag = false;
				boolean tdmNoAccessSpeedFlag = false;
				//update NxT1 quantity, populating possible NxT1 secondary circuitId map
				if ("1.544 mb".equals(design.path("AccessSpeed").asText())) {
					int portSpeed = Integer.parseInt(design.path("portSpeed").asText());
					qty = (int) Math.round(portSpeed / 1544.0);
					if (qty > 1) {
						nxt1QuantityUpdateFlag = true;
						String PrimaryCircuitId = design.path("circuitId").asText();
						String SecondaryCircuitId = PrimaryCircuitId;
						for (int i = 0; i < qty - 1; i++) {
							SecondaryCircuitId = circuitIdPlusOne(SecondaryCircuitId);
							nxt1SecondaryCircuitIdToPrimaryCircuitId.put(SecondaryCircuitId, PrimaryCircuitId);
						}
					}
				}
				//ethernet should not have line item lookup fallout
				if ("Ethernet".equals(design.path("accessType").asText())) {
					ethernetNoFalloutFlag = true;
				}
				//adding tdm no access speed fallout, but not overwrite usrp has no records fallout
				if (!"Ethernet".equals(design.path("accessType").asText())
						&& !"Foreign".equals(design.path("accessType").asText()) && !design.has("AccessSpeed")
						&& !design.has(InrConstants.NEXXUS_FALLOUT)) {
					tdmNoAccessSpeedFlag = true;
				}
				JsonNode priceDetailsArray = design.path("priceDetails");
				for (JsonNode priceDetails : priceDetailsArray) {
					//defaut fallout reason
					if ("Y".equals(priceDetails.path(InrConstants.NEXXUS_FALLOUT).asText())) {
						((ObjectNode) priceDetails).put(InrConstants.NEXXUS_FALLOUT_REASON, "USRP has no records");
					}
					if ("A".equals(priceDetails.path("typeOfCharge").asText())) {
						if (nxt1QuantityUpdateFlag) {
							((ObjectNode) priceDetails).put("quantity", String.valueOf(qty));
						}
						if (ethernetNoFalloutFlag) {
							((ObjectNode) priceDetails).put(InrConstants.NEXXUS_FALLOUT_IGNORE, "Y");
						}
						if (tdmNoAccessSpeedFlag) {
							((ObjectNode) priceDetails).put(InrConstants.NEXXUS_FALLOUT, "Y");
							((ObjectNode) priceDetails).put(InrConstants.NEXXUS_FALLOUT_REASON, "Access Speed missing in TDM");
						}
					}
				}
			}
		}
		
		//second round to check secondary nxt1 circuitId and update fallout reason
		for (JsonNode designArray : designs) {
			for (JsonNode design : designArray) {
				String circuitId = design.path("circuitId").asText();
				if (design.has(InrConstants.NEXXUS_FALLOUT)
						&& nxt1SecondaryCircuitIdToPrimaryCircuitId.containsKey(circuitId)) {
					// update priceDetails fallout reason
					JsonNode priceDetailsArray = design.path("priceDetails");
					for (JsonNode priceDetails : priceDetailsArray) {
						((ObjectNode) priceDetails).put(InrConstants.NEXXUS_FALLOUT_REASON,
								"This circuit may be the secondary circuit of "
										+ nxt1SecondaryCircuitIdToPrimaryCircuitId.get(circuitId));
					}
				}
			}
		}
	}
	
//	 	sample output
//		abc801ati:abc802ati
//		abc809ati:abc810ati
//		abc899ati:abc900ati
//		abc9ati:1abc0ati
//		abc999ati:1abc000ati
		//carry to letter has issue, but should not be seen in real circuitId

	protected String circuitIdPlusOne(String id) {
		StringBuilder sb = new StringBuilder(id);
		circuitIdPlusOneHelper(sb, -1);
		return sb.toString();
	}
	
	protected void circuitIdPlusOneHelper(StringBuilder sb, int indexFromEnd) {
		int index = sb.length() + indexFromEnd;
		if (index < 0) {
			sb.insert(0, '1');
			return;
		} else if (sb.charAt(index) >= '0' && sb.charAt(index) <= '8') {
			sb.setCharAt(index, (char) (sb.charAt(index) + 1));
			return;
		} else if (sb.charAt(index) == '9') {
			sb.setCharAt(index, '0');
			circuitIdPlusOneHelper(sb, indexFromEnd - 1);
		} else {
			circuitIdPlusOneHelper(sb, indexFromEnd - 1);
		}
	}

	protected void traverseHelper(JsonNode node, JsonPath path, UsrpQueryRule queryRule,
			Map<String, NxDwToJsonRules> jsonBuildRuleMap, Map<String, JsonNode> nodeMap) {
		if (node.getNodeType() == JsonNodeType.ARRAY) {
			for (int i = 0; i < node.size(); i++) {
				traverseHelper(node.get(i), path, queryRule, jsonBuildRuleMap, nodeMap);
			}
		} else if (node.getNodeType() == JsonNodeType.OBJECT) {
			nodeMap.put(path.toString(), node);
			updateNodeMapIdentifierKey(node, path, jsonBuildRuleMap, nodeMap);
			queryUsrp(node, path, queryRule, jsonBuildRuleMap, nodeMap);

			List<String> childName = new ArrayList<>();
			List<JsonNode> childNode = new ArrayList<>();
			Iterator<Entry<String, JsonNode>> iterator = node.fields();
			iterator.forEachRemaining(entry -> {
				childName.add(entry.getKey());
				childNode.add(entry.getValue());
			});
			for (int i = 0; i < childName.size(); i++) {
				traverseHelper(childNode.get(i), path.resolveContainerNode(childName.get(i)), queryRule,
						jsonBuildRuleMap, nodeMap);
			}
			if (queryRule.getRootTagName().equals("avpnUsrp") && path.toString().equals("/accountDetails")) {
				((ObjectNode) node).remove("design");
			}
			nodeMap.remove(path.toString());
		}
	}

	protected void queryUsrp(JsonNode node, JsonPath path, UsrpQueryRule queryRule,
			Map<String, NxDwToJsonRules> jsonBuildRuleMap, Map<String, JsonNode> nodeMap) {
		if (queryRule.getQueryPath().equals(path.toString())) {
			String[] queryParams = queryRule.getQueryParams().split(",");
			String sql = queryRule.getQueryString();
			Object[] sqlArgs = new Object[queryParams.length];
			for (int i = 0; i < queryParams.length; i++) {
				sqlArgs[i] = getQueryParam(queryParams[i], nodeMap);
			}
			List<Map<String, Object>> usrpDesigns = usrpDao.query(sql, sqlArgs);
			if (usrpDesigns.isEmpty()) {
				Map<String, Object> fallout = new HashMap<>();
				fallout.put(InrConstants.NEXXUS_FALLOUT, "Y");
				fallout.put("siteid", InrConstants.NEXXUS_FALLOUT);
				fallout.put("circuitid", nodeMap.get("/accountDetails/design").path("circuitId").asText());
				fallout.put("icore_site_id", nodeMap.get("/accountDetails/design").path("portNumber").asText());
				usrpDesigns.add(fallout);
			}
			for (Map<String, Object> usrpDesign : usrpDesigns) {
				for (NxDwToJsonRules jsonBuildRule : jsonBuildRuleMap.values()) {
					if (isjsonBuildRuleForValueNode(jsonBuildRule)) {
						if (null != jsonBuildRule.getDwKey() && null == usrpDesign.get(jsonBuildRule.getDwKey())) {
							continue;
						}
						ObjectNode parentNode = getParentNode(jsonBuildRule, usrpDesign, jsonBuildRuleMap, nodeMap);
						putValueToNode(jsonBuildRule.getFieldType(),
								convertData(jsonBuildRule, usrpDesign.get(jsonBuildRule.getDwKey()), usrpDesign, jsonBuildRuleMap), parentNode,
								jsonBuildRule.getFieldName(), jsonBuildRule, nodeMap);
					}
				}
			}
		}
	}

	protected Object convertData(NxDwToJsonRules jsonBuildRule, Object data, Map<String, Object> usrpDesign, Map<String, NxDwToJsonRules> jsonBuildRuleMap) {
		if ("CUSTOM".equals(jsonBuildRule.getType())) {
			if ("AVPN".equals(jsonBuildRule.getOffer()) && "technology".equals(jsonBuildRule.getFieldName())) {
				String interconnectType = String.valueOf(convertData(jsonBuildRuleMap.get("interconnectType"),
						usrpDesign.get("interconnect_type"), usrpDesign, jsonBuildRuleMap));
				String accessMethodType = String.valueOf(convertData(jsonBuildRuleMap.get("accessMethodType"),
						usrpDesign.get("access_method_type"), usrpDesign, jsonBuildRuleMap));
				if ("null".equals(interconnectType) || "null".equals(accessMethodType)) {
					return null;
				}
				String key = interconnectType + "_" + accessMethodType;
				NxLookupData nxLookupData = nxMyPriceRepositoryServce.getLookupDataByItemId("USRP_TECHNOLOGY").get(key);
				if (nxLookupData != null) {
					return nxLookupData.getDescription();
				}
				return null;
			} else if ("AVPN".equals(jsonBuildRule.getOffer()) && "AccessSpeed".equals(jsonBuildRule.getFieldName())) {
				String accessType = String.valueOf(convertData(jsonBuildRuleMap.get("accessType"),
						usrpDesign.get("access_type"), usrpDesign, jsonBuildRuleMap));
				String portSpeed = String.valueOf(usrpDesign.get("port_speed"));
				if ("Ethernet".equals(accessType)) {
					//ethernet
					return usrpDesign.get("ethernet_access_bandwidth");
				} else {
					String key = accessType + "_" + portSpeed;
					NxLookupData nxLookupData = nxMyPriceRepositoryServce.getLookupDataByItemId("USRP_ACCESS_SPEED").get(key);
					if (nxLookupData != null) {
						return nxLookupData.getDescription();
					}
					return null;
				}
			} else if ("AVPN".equals(jsonBuildRule.getOffer()) && "accessProductName".equals(jsonBuildRule.getFieldName())) {
				String accessType = String.valueOf(usrpDesign.get("access_type"));
				if ("33".equals(accessType)) {
					return "Ethernet";
				} else if ("8".equals(accessType)) {
					return "International";
				} else {
					return "TDM";
				}
			} else if ("AVPN".equals(jsonBuildRule.getOffer()) && "AccessArchitecture".equals(jsonBuildRule.getFieldName())) {
				String interconnectType = String.valueOf(usrpDesign.get("interconnect_type"));
				String accessMethodType = String.valueOf(usrpDesign.get("access_method_type"));
				if (("1".equals(interconnectType) && "8".equals(accessMethodType)) || ("2".equals(interconnectType)
						&& ("6".equals(accessMethodType) || "7".equals(accessMethodType) || "8".equals(accessMethodType)
								|| "11".equals(accessMethodType)))) {
					return "SWITCHED";
				} else {
					return "DEDICATED";
				}
			}
		}
		if (jsonBuildRule.getType() != null && jsonBuildRule.getType().contains("TRIM_CIRCUITID")) {
			return String.valueOf(data).replaceAll("\\s", "").replaceAll("\\.", "");
		}
		if (jsonBuildRule.getDefaultValue() != null) {
			return jsonBuildRule.getDefaultValue();
		}
		if (jsonBuildRule.getLookupDatasetName() == null) {
			return data;
		}
		if (jsonBuildRule.getLookupDatasetName() != null) {
			NxLookupData nxLookupData = nxMyPriceRepositoryServce
					.getLookupDataByItemId(jsonBuildRule.getLookupDatasetName()).get(String.valueOf(data));
			if (nxLookupData != null) {
				return nxLookupData.getDescription();
			} 
		}
		return data;
	}

	protected void putValueToNode(String type, Object value, ObjectNode parentNode, String tagName, NxDwToJsonRules jsonBuildRule, Map<String, JsonNode> nodeMap) {
		if ("AVPN".equals(jsonBuildRule.getOffer()) && "COPY".equals(jsonBuildRule.getType())) {
			JsonNode priceDetailsArray = nodeMap.get(jsonBuildRule.getDefaultValue()).path("priceDetails");
			for (JsonNode priceDetailsElement : priceDetailsArray) {
				parentNode.withArray("priceDetails").add(priceDetailsElement);
				if (parentNode.has(InrConstants.NEXXUS_FALLOUT)) {
					((ObjectNode) priceDetailsElement).set(InrConstants.NEXXUS_FALLOUT, parentNode.get(InrConstants.NEXXUS_FALLOUT));
				}
			}
			return;
		}
		if (value == null) {
			return;
		}
		if (type.equals("String")) {
			parentNode.put(tagName, String.valueOf(value).trim());
		} else if (type.equals("int")) {
			parentNode.put(tagName, Integer.parseInt(String.valueOf(value).trim()));
		} else if (type.equals("long")) {
			parentNode.put(tagName, Long.parseLong(String.valueOf(value).trim()));
		} else if (type.equals("double")) {
			parentNode.put(tagName, Double.parseDouble(String.valueOf(value).trim()));
		}
	}

	protected String getQueryParam(String path, Map<String, JsonNode> nodeMap) {
		JsonPath jsonPath = new JsonPath(path);
		String res = nodeMap.get(jsonPath.parent().toString()).path(jsonPath.getFieldName()).asText();
		if (path.toLowerCase().contains("circuitid")) {
			res = res.replaceAll("\\s", "");
			if (res.length() == 13) {
				res = String.format(".%s.%s..%s.", res.substring(0, 4), res.substring(4, 10),
						res.substring(10, 13));
			} else if (res.length() == 16 ) {
				res = String.format(".%s.%s.%s.%s.", res.substring(0, 4), res.substring(4, 10),
						res.substring(10, 13), res.subSequence(13, 16));
			} else if (res.length() == 12) {
				res = String.format(".%s.%s..%s.", res.substring(0, 4), res.substring(4, 9),
						res.substring(9, 12));
			}
		}
		return res;
	}

	protected ObjectNode getParentNode(NxDwToJsonRules jsonBuildRule, Map<String, Object> usrpDesignRow,
			Map<String, NxDwToJsonRules> jsonBuildRuleMap, Map<String, JsonNode> nodeMap) {
		NxDwToJsonRules parentRule = jsonBuildRuleMap.get(jsonBuildRule.getFieldParent());
		String identifierKey = getIdentifierKey(parentRule, jsonBuildRuleMap, nodeMap, usrpDesignRow);
		if (nodeMap.containsKey(identifierKey)) {
			return (ObjectNode) nodeMap.get(identifierKey);
		}
		ObjectNode grandParentNode = getParentNode(parentRule, usrpDesignRow, jsonBuildRuleMap, nodeMap);
		ObjectNode parentNode = createdParentNode(parentRule, usrpDesignRow, nodeMap, jsonBuildRuleMap);
		if (parentRule.getFieldType().equals("list")) {
			grandParentNode.withArray(parentRule.getFieldName()).add(parentNode);
		} else {
			grandParentNode.set(parentRule.getFieldName(), parentNode);
		}
		if (grandParentNode.has(InrConstants.NEXXUS_FALLOUT)) {
			parentNode.set(InrConstants.NEXXUS_FALLOUT, grandParentNode.get(InrConstants.NEXXUS_FALLOUT));
		}
		nodeMap.put(identifierKey, parentNode);
		return parentNode;
	}

	protected ObjectNode createdParentNode(NxDwToJsonRules jsonBuildRule, Map<String, Object> usrpDesignRow,
			Map<String, JsonNode> nodeMap, Map<String, NxDwToJsonRules> jsonBuildRuleMap) {
		ObjectNode res = mapper.createObjectNode();
		String[] keys = jsonBuildRule.getIdentifierKey().split(",");
		String[] types = jsonBuildRule.getIdentifierType().split(",");
		String[] usrpDesignKeys = jsonBuildRule.getDwKey().split(",");
		for (int i = 0; i < keys.length; i++) {
			putValueToNode(types[i], convertData(jsonBuildRule, usrpDesignRow.get(usrpDesignKeys[i]), usrpDesignRow, jsonBuildRuleMap), res, keys[i], jsonBuildRule, nodeMap);
		}
		return res;
	}

	protected boolean isjsonBuildRuleForValueNode(NxDwToJsonRules jsonBuildRule) {
		return !jsonBuildRule.getFieldType().equals("list") && !jsonBuildRule.getFieldType().equals("object");
	}

	protected void updateNodeMapIdentifierKey(JsonNode node, JsonPath path,
			Map<String, NxDwToJsonRules> jsonBuildRuleMap, Map<String, JsonNode> nodeMap) {
		if (jsonBuildRuleMap.containsKey(path.getFieldName())) {
			NxDwToJsonRules jsonBuildRule = jsonBuildRuleMap.get(path.getFieldName());
			if (jsonBuildRule.getIdentifierKey() != null && jsonBuildRule.getIdentifierKey().startsWith("/")) {
				String identifierKey = getIdentifierKey(jsonBuildRule, jsonBuildRuleMap, nodeMap, null);
				nodeMap.put(identifierKey, node);
			}
		}

	}

	protected String getIdentifierKey(NxDwToJsonRules jsonBuildRule, Map<String, NxDwToJsonRules> jsonBuildRuleMap,
			Map<String, JsonNode> nodeMap, Map<String, Object> usrpDesignRow) {
		StringBuilder sb = new StringBuilder();
		getIdentifierKeyHelper(sb, jsonBuildRule, jsonBuildRuleMap, nodeMap, usrpDesignRow);
		return sb.toString();
	}

	protected void getIdentifierKeyHelper(StringBuilder sb, NxDwToJsonRules jsonBuildRule,
			Map<String, NxDwToJsonRules> jsonBuildRuleMap, Map<String, JsonNode> nodeMap,
			Map<String, Object> usrpDesignRow) {
		if (jsonBuildRule == null) {
			return;
		}
		getIdentifierKeyHelper(sb, jsonBuildRuleMap.get(jsonBuildRule.getFieldParent()), jsonBuildRuleMap, nodeMap,
				usrpDesignRow);
		String[] identifierKeys = jsonBuildRule.getIdentifierKey().split(",");
		String[] usrpDesignKeys = {};
		if (jsonBuildRule.getDwKey() != null) {
			usrpDesignKeys = jsonBuildRule.getDwKey().split(",");
		}
		for (int i = 0; i < identifierKeys.length; i++) {
			String key = identifierKeys[i];
			if (key.startsWith("/")) {
				JsonPath jsonPath = new JsonPath(key);
				String value = nodeMap.get(jsonPath.parent().toString()).path(jsonPath.getFieldName()).asText();
				if (sb.length() > 0) {
					sb.append("_");
				}
				sb.append(value);
			} else {
				String value = String.valueOf(usrpDesignRow.get(usrpDesignKeys[i]));
				if (sb.length() > 0) {
					sb.append("_");
				}
				sb.append(value);
			}
		}
	}
*/
}
