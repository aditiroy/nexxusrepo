package com.att.sales.nexxus.inr;

import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.InrXmlToJsonRule;
import com.att.sales.nexxus.dao.model.SalesMsProdcompUdfAttrVal;
import com.att.sales.nexxus.dao.repository.InrXmlToJsonRuleDao;
import com.att.sales.nexxus.dao.repository.SalesMsDao;
import com.att.sales.nexxus.dao.repository.SalesMsProdcompUdfAttrValRepository;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.RegExUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AseDppRequestToSnsdSoldCancelRequest extends InrIntermediateJsonGenerator {
	private static Logger log = LoggerFactory.getLogger(AseDppRequestToSnsdSoldCancelRequest.class);

	private SalesMsDao salesMsDao;
	private SalesMsProdcompUdfAttrValRepository salesMsProdcompUdfAttrValRepository;
	private JsonNode dppRequest;

	public AseDppRequestToSnsdSoldCancelRequest(ObjectMapper mapper, InrXmlToJsonRuleDao inrXmlToJsonRuleDao,
			NxMyPriceRepositoryServce nxMyPriceRepositoryServce, JsonNode dppRequest,SalesMsDao salesMsDao,
			SalesMsProdcompUdfAttrValRepository salesMsProdcompUdfAttrValRepository) {
		super(mapper, inrXmlToJsonRuleDao, nxMyPriceRepositoryServce);
		this.dppRequest = dppRequest;
		this.salesMsDao= salesMsDao;
		this.salesMsProdcompUdfAttrValRepository= salesMsProdcompUdfAttrValRepository;
	}

	@Override
	public JsonNode generate() throws SalesBusinessException {
		init();
		JsonPath rootPath = JsonPath.getRootPath();
		ObjectNode snsdSoldCancelRequest = (ObjectNode) nodeMap.get(InrConstants.ROOT_JSON_MAP_KEY);
		generateValueForRoutePerKmzInd(dppRequest);
		generateHelper(dppRequest, rootPath);
		snsdSoldCancelRequest.put("submittingUser", "PRICER-D");
		return snsdSoldCancelRequest;
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

	protected void processArrayNode(JsonNode node, JsonPath path) {
		for (JsonNode arrayElement : node) {
			this.generateHelper(arrayElement, path);
		}

	}

	protected void processObjectNode(JsonNode node, JsonPath path) {
		if (inrXmlToJsonRuleMap.containsKey(path.getPath())) {
			InrXmlToJsonRule inrXmlToJsonRule = inrXmlToJsonRuleMap.get(path.getPath());
			ObjectNode newNode = mapper.createObjectNode();
			nodeMap.put(inrXmlToJsonRule.getArrayElementName(), newNode);

			if (path.getPath().equals("/solution/offers/site")) {
				ObjectNode endpointElementNode = mapper.createObjectNode();
				newNode.withArray("endpoint").add(endpointElementNode);
				nodeMap.put("endpointElement", endpointElementNode);

				ObjectNode customerAddressNode = mapper.createObjectNode();
				endpointElementNode.set("customerAddress", customerAddressNode);
				endpointElementNode.put("terminatingEnd", "A");
				nodeMap.put("customerAddress", customerAddressNode);

				// customerAddress/addressLine:[]
				String address1 = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(node, "/address1");
				String address2 = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(node, "/address2");
				if (!address1.isEmpty()) {
					customerAddressNode.withArray("addressLine").add(address1);
				}
				/*if (!address2.isEmpty()) {
					customerAddressNode.withArray("addressLine").add(address2);
				}*/

				// customerAddress/subAddress
				String room = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(node, "/room");
				String floor = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(node, "/floor");
				String building = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(node, "/building");
				String subAddress=null;
				if (!address2.isEmpty()) {
					subAddress = String.format("%s %s %s %s",address2, room, floor, building);
				}else {
					subAddress = String.format("%s %s %s", room, floor, building);
				}
				
				if (!subAddress.trim().isEmpty()) {
					customerAddressNode.put("subAddress", subAddress);
				}

				// endpoint/customerContactName, customerPhoneNumber,
				// customerEmailAddress
				JsonNode lconNode = node.at("/lconDetails");
				if (!lconNode.isNull() && !lconNode.isMissingNode()) {
					for (JsonNode lconArrayElement : lconNode) {
						String lconType = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(lconArrayElement,
								"/lconType");
						lconType = lconType.isEmpty() ? "Primary" : lconType;
						if ("Primary".equalsIgnoreCase(lconType)) {
							String lconFirstName = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(lconArrayElement,
									"/lconFirstName");
							String lconLastName = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(lconArrayElement,
									"/lconLastName");
							String name = String.format("%s %s", lconFirstName, lconLastName).trim();
							String lconPhone = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(lconArrayElement,
									"/lconPhone");
							String lconEmail = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(lconArrayElement,
									"/lconEmail");
							JacksonUtil.objectNodePutStringValueIgnoreNullAndEmpty(endpointElementNode, "customerContactName", name);
							JacksonUtil.objectNodePutStringValueIgnoreNullAndEmpty(endpointElementNode, "customerPhoneNumber",
									lconPhone);
							JacksonUtil.objectNodePutStringValueIgnoreNullAndEmpty(endpointElementNode, "customerEmailAddress",
									lconEmail);
							break;
						}
					}
				}
				
				// /customerRequestedDueDate
				JsonNode crddNode = node.at("/crdd");
				if (!crddNode.isNull() && !crddNode.isMissingNode()) {
					JacksonUtil.objectNodePutStringValueIgnoreNullAndEmpty(nodeMap.get(InrConstants.CIRCUITSDETAILS_JSON_MAP_KEY),
							"customerRequestedDueDate", RegExUtil.firstMatchYYYY_MM_DD(crddNode.asText()));
				}
			} else if (path.getPath().equals("/solution/offers/circuit/endpoint")) {
				ObjectNode customerAddressNode = mapper.createObjectNode();
				newNode.set("customerAddress", customerAddressNode);
				nodeMap.put("customerAddress", customerAddressNode);
				
				//terminatingEnd
				String aEndpoint = JacksonUtil.nodeAtPointerAsText(node, "/aEndpoint");
				String zEndpoint = JacksonUtil.nodeAtPointerAsText(node, "/zEndpoint");
				if (aEndpoint != null) {
					newNode.put("terminatingEnd", "A");
				} else if (zEndpoint != null) {
					newNode.put("terminatingEnd", "Z");
				}
				
				// customerAddress/addressLine:[]
				String address1 = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(node, "/site/address1");
				String address2 = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(node, "/site/address2");
				if (!address1.isEmpty()) {
					customerAddressNode.withArray("addressLine").add(address1);
				}
				if (!address2.isEmpty()) {
					customerAddressNode.withArray("addressLine").add(address2);
				}

				// customerAddress/subAddress
				String room = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(node, "/site/room");
				String floor = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(node, "/site/floor");
				String building = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(node, "/site/building");
				String subAddress = String.format("%s %s %s", room, floor, building);
				if (!subAddress.trim().isEmpty()) {
					customerAddressNode.put("subAddress", subAddress);
				}
				
				// endpoint/customerContactName, customerPhoneNumber,
				// customerEmailAddress
				JsonNode lconNode = node.at("/site/lconDetails");
				if (!lconNode.isNull() && !lconNode.isMissingNode()) {
					for (JsonNode lconArrayElement : lconNode) {
						String lconType = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(lconArrayElement,
								"/lconType");
						lconType = lconType.isEmpty() ? "Primary" : lconType;
						if ("Primary".equalsIgnoreCase(lconType)) {
							String lconFirstName = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(lconArrayElement,
									"/lconFirstName");
							String lconLastName = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(lconArrayElement,
									"/lconLastName");
							String name = String.format("%s %s", lconFirstName, lconLastName).trim();
							String lconPhone = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(lconArrayElement,
									"/lconPhone");
							String lconEmail = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(lconArrayElement,
									"/lconEmail");
							JacksonUtil.objectNodePutStringValueIgnoreNullAndEmpty(newNode, "customerContactName", name);
							JacksonUtil.objectNodePutStringValueIgnoreNullAndEmpty(newNode, "customerPhoneNumber",
									lconPhone);
							JacksonUtil.objectNodePutStringValueIgnoreNullAndEmpty(newNode, "customerEmailAddress",
									lconEmail);
							break;
						}
					}
				}
				
				// /customerRequestedDueDate
				JsonNode crddNode = node.at("/site/crdd");
				if (!crddNode.isNull() && !crddNode.isMissingNode()) {
					JacksonUtil.objectNodePutStringValueIgnoreNullAndEmpty(nodeMap.get(InrConstants.ROOT_JSON_MAP_KEY),
							"customerRequestedDueDate", RegExUtil.firstMatchYYYY_MM_DD(crddNode.asText()));
				}
			}

			doObjectNode(node, path);

			if (isNodeValid(inrXmlToJsonRule, newNode)) {
				((ObjectNode) nodeMap.get(inrXmlToJsonRule.getArrayParent())).withArray(inrXmlToJsonRule.getArrayName())
						.add(newNode);
			}
		} else {
			doObjectNode(node, path);
		}
	}

	protected void doObjectNode(JsonNode node, JsonPath path) {
		for (Iterator<Entry<String, JsonNode>> i = node.fields(); i.hasNext();) {
			Entry<String, JsonNode> entry = i.next();
			JsonNode childNode = entry.getValue();
			if (childNode.isContainerNode()) {
				continue;
			}
			String childName = entry.getKey();
			this.generateHelper(childNode, path.resolveContainerNode(childName));
		}
		for (Iterator<Entry<String, JsonNode>> i = node.fields(); i.hasNext();) {
			Entry<String, JsonNode> entry = i.next();
			JsonNode childNode = entry.getValue();
			if (!childNode.isContainerNode()) {
				continue;
			}
			String childName = entry.getKey();
			this.generateHelper(childNode, path.resolveContainerNode(childName));
		}
	}

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
			} else if (inrXmlToJsonRule.getJsonType().contains(InrConstants.JSON_TYPE_FIELD_DATE)) {
				((ObjectNode) nodeMap.get(jsonParent)).put(jsonKey, RegExUtil.convertDateToYYYY_MM_DD(node.asText()));
			}
		}
	}

	@Override
	protected void initializeRuleMap() throws SalesBusinessException {
		inrXmlToJsonRuleMap = inrXmlToJsonRuleDao.getInrXmlToJsonRuleMap("aseDppRequestToSnsdSoldCancel");
	}
	
	protected void generateValueForRoutePerKmzInd(JsonNode dppRequest){
		JsonNode offers = dppRequest.at("/solution/offers");
		String offerName = "";
		for (JsonNode offerElement : offers) {
			String offerId = offerElement.path("offerId").asText();
			if (StringUtils.isNotEmpty(offerId)) {
				int id = Integer.parseInt(offerId);
				offerName = salesMsDao.getOfferNameByOfferId(id);
				 if (StringConstants.OFFERNAME_ADE.equals(offerName)) {
						JsonNode circuit = offerElement.path("circuit");
						for (JsonNode circuitElement : circuit) {
							JsonNode component = circuitElement.path("component");
							for (JsonNode componentElement : component) {
								long componentCodeId= componentElement.path("componentCodeId").asLong();
								boolean isRoutePerKmzIndMissing = componentElement.path("routePerKmzInd").isMissingNode();
								if(isRoutePerKmzIndMissing) {
									JsonNode designDetails = componentElement.path("designDetails");
									for(JsonNode designDetailsElement: designDetails) {
										if("22212".equals(designDetailsElement.path("udfId").asText())
												|| "22211".equals(designDetailsElement.path("udfId").asText()) ||
												"22213".equals(designDetailsElement.path("udfId").asText())) {
											JsonNode udfAttributeIdNode = designDetailsElement.path("udfAttributeId");
											long udfId = designDetailsElement.path("udfId").asLong();
											long udfAttributeId = udfAttributeIdNode.path(0).asLong();
											String udfAttributeValue = translateUdfAttributeId(udfId, udfAttributeId,Long.parseLong(offerId),
													componentCodeId);
											((ObjectNode)componentElement).put("routePerKmzInd", udfAttributeValue);
											break;
										}
									}
								}
							}
						}
				 }
			}
		}
		log.info("dpp request for sold cancel {}",dppRequest);
	}
	
	protected String translateUdfAttributeId(long udfId, long udfAttributeId,long offerId,long componentCodeId) {
		SalesMsProdcompUdfAttrVal salesMsProdcompUdfAttrVal = salesMsProdcompUdfAttrValRepository
				.findTopByOfferIdAndComponentIdAndUdfIdAndUdfAttributeIdAndActive(offerId, componentCodeId, udfId,
						udfAttributeId, "Active");
		if (salesMsProdcompUdfAttrVal != null) {
			return salesMsProdcompUdfAttrVal.getUdfAttributeValue();
		}

		return null;
	}
}
