package com.att.sales.nexxus.inr;

import java.util.Iterator;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.dao.model.InrXmlToJsonRule;
import com.att.sales.nexxus.dao.repository.InrXmlToJsonRuleDao;
import com.att.sales.nexxus.dao.repository.SalesMsDao;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.RegExUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AseDppRequestToSnsdRequest extends InrIntermediateJsonGenerator {
	private static Logger log = LoggerFactory.getLogger(AseDppRequestToSnsdRequest.class);

	private JsonNode dppRequest;
	private int offerId;
	private SalesMsDao salesMsDao;
	private String automationInd;
	private String standardPricingInd;

	public AseDppRequestToSnsdRequest(ObjectMapper mapper, InrXmlToJsonRuleDao inrXmlToJsonRuleDao,
			NxMyPriceRepositoryServce nxMyPriceRepositoryServce, JsonNode dppRequest, SalesMsDao salesMsDao) {
		super(mapper, inrXmlToJsonRuleDao, nxMyPriceRepositoryServce);
		this.dppRequest = dppRequest;
		this.salesMsDao = salesMsDao;
	}

	@Override
	public JsonNode generate() throws SalesBusinessException {
		init();
		JsonPath rootPath = JsonPath.getRootPath();
		ObjectNode snsdRequest = (ObjectNode) nodeMap.get(InrConstants.ROOT_JSON_MAP_KEY);

		ObjectNode salesUserNode = mapper.createObjectNode();
		snsdRequest.set("salesUser", salesUserNode);
		nodeMap.put("salesUser", salesUserNode);

		generateHelper(dppRequest, rootPath);
		snsdRequest.put("submittingUser", "PRICER-D");
		return snsdRequest;
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
			log.info("processing object node path {}", path.getPath());
			InrXmlToJsonRule inrXmlToJsonRule = inrXmlToJsonRuleMap.get(path.getPath());
			ObjectNode newNode = mapper.createObjectNode();
			nodeMap.put(inrXmlToJsonRule.getArrayElementName(), newNode);

			if (path.getPath().equals("/solution/offers/site")) {
				ObjectNode designDetailsNode = mapper.createObjectNode();
				newNode.set("designDetails", designDetailsNode);
				nodeMap.put("designDetails", designDetailsNode);

				ObjectNode siteDetailsElementNode = mapper.createObjectNode();
				designDetailsNode.withArray("siteDetails").add(siteDetailsElementNode);
				nodeMap.put("siteDetailsElement", siteDetailsElementNode);

				ObjectNode addressNode = mapper.createObjectNode();
				siteDetailsElementNode.set("address", addressNode);
				nodeMap.put("address", addressNode);

				// ase default
				siteDetailsElementNode.put("newBuildingIndicator", "false");
				siteDetailsElementNode.put("siteIdentifier", "A");
				designDetailsNode.put("multiGigeIndicator", "false");
				designDetailsNode.put("quantity", 1);

				// designDetails/product
				String product = salesMsDao.getOfferNameByOfferId(offerId);
				if ("ASENoD".equalsIgnoreCase(product)) {
					product = "ASE NETWORK ON DEMAND";
				}
				designDetailsNode.put("product", product);

				// designDetails/imsProductNumber
				JsonNode imsProductNumberNode = dppRequest.at("/solution/imsProductNumber");
				if (!imsProductNumberNode.isNull() && !imsProductNumberNode.isMissingNode()) {
					designDetailsNode.put("imsProductNumber", imsProductNumberNode.asInt());
				}

				// designDetails/siteDetails/address/addressLine:[]
				String address1 = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(node, "/address1");
				String address2 = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(node, "/address2");
				if (!address1.isEmpty()) {
					addressNode.withArray("addressLine").add(address1);
				}
				/*
				 * if (!address2.isEmpty()) {
				 * addressNode.withArray("addressLine").add(address2); }
				 */

				// designDetails/siteDetails/address/subAddress
				String room = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(node, "/room");
				String floor = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(node, "/floor");
				String building = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(node, "/building");
				String subAddress = null;
				if (!address2.isEmpty()) {
					subAddress = String.format("%s %s %s %s", address2, room, floor, building);
				} else {
					subAddress = String.format("%s %s %s", room, floor, building);
				}

				if (!subAddress.trim().isEmpty()) {
					addressNode.put("subAddress", subAddress);
				}

				// designDetails/siteDetails/localContactDetails, alternateLocalContactDetails,
				// buildingContactDetails
				JsonNode lconNode = node.at("/lconDetails");
				if (!lconNode.isNull() && !lconNode.isMissingNode()) {
					ArrayNode lconArray = (ArrayNode) lconNode;
					for (JsonNode lconArrayElement : lconArray) {
						String lconType = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(lconArrayElement,
								"/lconType");
						lconType = lconType.isEmpty() ? "Primary" : lconType;
						String lconFirstName = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(lconArrayElement,
								"/lconFirstName");
						String lconLastName = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(lconArrayElement,
								"/lconLastName");
						String name = String.format("%s %s", lconFirstName, lconLastName).trim();
						String lconPhone = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(lconArrayElement,
								"/lconPhone");
						String lconEmail = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(lconArrayElement,
								"/lconEmail");
						ObjectNode localContactDetails = mapper.createObjectNode();
						JacksonUtil.objectNodePutStringValueIgnoreNullAndEmpty(localContactDetails, "contactName",
								name);
						JacksonUtil.objectNodePutStringValueIgnoreNullAndEmpty(localContactDetails,
								"contactPhoneNumber", lconPhone);
						JacksonUtil.objectNodePutStringValueIgnoreNullAndEmpty(localContactDetails,
								"contactEmailAddress", lconEmail);
						if (localContactDetails.size() > 0) {
							if ("Primary".equalsIgnoreCase(lconType)) {
								siteDetailsElementNode.set("localContactDetails", localContactDetails);
							} else if ("Alternate".equalsIgnoreCase(lconType)) {
								siteDetailsElementNode.set("alternateLocalContactDetails", localContactDetails);
							} else if ("Building".equalsIgnoreCase(lconType)) {
								siteDetailsElementNode.set("buildingContactDetails", localContactDetails);
							} else {
								log.error("Unknown lconType:{}", lconType);
							}
						}
					}
				}

				// designDetails/siteDetails/endCustomerName
				String endCustomerName = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(dppRequest,
						"/solution/endCustomerName");
				JacksonUtil.objectNodePutStringValueIgnoreNullAndEmpty(siteDetailsElementNode, "endCustomerName",
						endCustomerName);

				// designDetails/macdActivity
				JsonNode macdActivityNode = node.at("/macdActivity");
				if (macdActivityNode.isArray()) {
					for (JsonNode n : macdActivityNode) {
						String macdActivity = convertData("snsd_ase_macdActivity",
								JacksonUtil.nodeAsTextNullToEmptyString(n));
						if (!macdActivity.isEmpty()) {
							designDetailsNode.withArray("macdActivity").add(macdActivity);
						}
					}
				} else {
					String macdActivity = convertData("snsd_ase_macdActivity",
							JacksonUtil.nodeAtPointerAsTextNullToEmptyString(node, "/macdActivity"));
					if (!macdActivity.isEmpty()) {
						designDetailsNode.withArray("macdActivity").add(macdActivity);
					}
				}

				// /customerRequestedDueDate
				JsonNode crddNode = node.at("/crdd");
				if (!crddNode.isNull() && !crddNode.isMissingNode()) {
					//JacksonUtil.objectNodePutStringValueIgnoreNullAndEmpty(nodeMap.get(InrConstants.DESIGNELEMENT_JSON_MAP_KEY),
						//	"customerRequestedDueDate", RegExUtil.firstMatchYYYY_MM_DD(crddNode.asText()));
					designDetailsNode.put("customerRequestedDueDate", RegExUtil.firstMatchYYYY_MM_DD(crddNode.asText()));
				}

				// designDetails/nssEngagementIndicator
				String macdType = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(node, "/macdType");
				String designCertification = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(node,
						"/designCertification");
//				if ("Y".equalsIgnoreCase(standardPricingInd)) {
//					designDetailsNode.put("nssEngagementIndicator", "false");
//				} else 
				if ("Y".equalsIgnoreCase(automationInd)) {
					designDetailsNode.put("nssEngagementIndicator",
							convertData("Y_N_true_false", JacksonUtil.nodeAtPointerAsText(node, "/nSSReviewRequired")));
				} else if ("Add".equals(macdType)) {
					if (!"Certified".equals(designCertification)) {
						designDetailsNode.put("nssEngagementIndicator", "true");
					} else {
						designDetailsNode.put("nssEngagementIndicator", "false");
					}
				} else {
					if (!"Certified".equals(designCertification)) {
						designDetailsNode.put("nssEngagementIndicator", "true");
					} else {
						String macdTypeOfChange = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(node,
								"/macdTypeOfChange");
						if ("SIMPLE_MACD".equals(macdTypeOfChange)) {
							designDetailsNode.put("nssEngagementIndicator", convertData("Y_N_true_false",
									JacksonUtil.nodeAtPointerAsText(node, "/nSSReviewRequired")));
						} else if ("ASE NETWORK ON DEMAND".equals(product) && "CHANGE".equalsIgnoreCase(macdType)) {
							designDetailsNode.put("nssEngagementIndicator", convertData("Y_N_true_false",
									JacksonUtil.nodeAtPointerAsText(node, "/nssEngagement")));
							log.info("Setting nssEngagementIndicator to False for ASENOD");
						} else {
							designDetailsNode.put("nssEngagementIndicator", "true");
						}

					}
				}

				// siteDetails/portConnectionType
				String portConnectionType = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(node,
						"/portConnectionType");
				String portInterfaceType = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(node, "/portInterfaceType");
				if ("1024".equalsIgnoreCase(portConnectionType)) {
					if ("04LN9.1CT".equalsIgnoreCase(portInterfaceType)
							|| "08LN9.1GE.1G".equalsIgnoreCase(portInterfaceType)) {
						siteDetailsElementNode.put("portConnectionType", "10/100/1000");
					} else {
						siteDetailsElementNode.put("portConnectionType", "1 G");
					}
				} else if ("100".equalsIgnoreCase(portConnectionType)) {
					siteDetailsElementNode.put("portConnectionType", "10/100");
				} else if ("10240".equalsIgnoreCase(portConnectionType)) {
					siteDetailsElementNode.put("portConnectionType", "10 G");
				} else if ("102400".equalsIgnoreCase(portConnectionType)) {
					siteDetailsElementNode.put("portConnectionType", "100 G");
				} else {
					siteDetailsElementNode.put("portConnectionType", portConnectionType);
				}

			} else if (path.getPath().equals("/solution/offers/circuit")) {
				ObjectNode designDetailsNode = mapper.createObjectNode();
				newNode.set("designDetails", designDetailsNode);
				nodeMap.put("designDetails", designDetailsNode);
				
//				if ("Y".equalsIgnoreCase(standardPricingInd)) {
//					designDetailsNode.put("nssEngagementIndicator", "false");
//				}
				// designDetails/product
				String product = salesMsDao.getOfferNameByOfferId(offerId);
				designDetailsNode.put("product", product);

				// designDetails/dateSoldToCustomer
				String dateSoldToCustomer = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(dppRequest,
						"/solution/dateSoldToCustomer");
				JacksonUtil.objectNodePutStringValueIgnoreNullAndEmpty(designDetailsNode, "dateSoldToCustomer",
						RegExUtil.convertDateToYYYY_MM_DD(dateSoldToCustomer));

				// designDetails/imsProductNumber
				JsonNode imsProductNumberNode = dppRequest.at("/solution/imsProductNumber");
				if (!imsProductNumberNode.isNull() && !imsProductNumberNode.isMissingNode()) {
					designDetailsNode.put("imsProductNumber", imsProductNumberNode.asInt());
				}

				// designDetails/annualRevenue
				JsonNode annualRevenueNode = dppRequest.at("/solution/annualRevenue");
				if (!annualRevenueNode.isNull() && !annualRevenueNode.isMissingNode()) {
					designDetailsNode.put("annualRevenue", annualRevenueNode.asDouble());
				}

				// designDetails/monthlyRevenue
				JsonNode monthlyRevenueNode = dppRequest.at("/solution/monthlyRevenue");
				if (!monthlyRevenueNode.isNull() && !monthlyRevenueNode.isMissingNode()) {
					designDetailsNode.put("monthlyRevenue", monthlyRevenueNode.asDouble());
				}

				// designDetails/macdActivity
				JsonNode macdActivityNode = node.at("/component/0/macdActivity");
				if (macdActivityNode.isArray()) {
					for (JsonNode n : macdActivityNode) {
						String macdActivity = convertData("snsd_macdActivity",
								JacksonUtil.nodeAsTextNullToEmptyString(n));
						if (!macdActivity.isEmpty()) {
							designDetailsNode.withArray("macdActivity").add(macdActivity);
						}
					}
				} else {
					String macdActivity = convertData("snsd_macdActivity",
							JacksonUtil.nodeAtPointerAsTextNullToEmptyString(node, "/component/0/macdActivity"));
					if (!macdActivity.isEmpty()) {
						designDetailsNode.withArray("macdActivity").add(macdActivity);
					}
				}
			} else if (path.getPath().equals("/solution/offers/circuit/endpoint")) {
				ObjectNode addressNode = mapper.createObjectNode();
				newNode.set("address", addressNode);
				nodeMap.put("address", addressNode);

				// designDetails/siteDetails/siteIdentifier
				String aEndpoint = JacksonUtil.nodeAtPointerAsText(node, "/aEndpoint");
				String zEndpoint = JacksonUtil.nodeAtPointerAsText(node, "/zEndpoint");
				if (aEndpoint != null) {
					newNode.put("siteIdentifier", "A");
				} else if (zEndpoint != null) {
					newNode.put("siteIdentifier", "Z");
				}

				// designDetails/siteDetails/address/addressLine:[]
				String address1 = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(node, "/site/address1");
				String address2 = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(node, "/site/address2");
				if (!address1.isEmpty()) {
					addressNode.withArray("addressLine").add(address1);
				}
				if (!address2.isEmpty()) {
					addressNode.withArray("addressLine").add(address2);
				}

				// designDetails/siteDetails/address/subAddress
				String room = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(node, "/site/room");
				String floor = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(node, "/site/floor");
				String building = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(node, "/site/building");
				String subAddress = String.format("%s %s %s", room, floor, building);

				if (!subAddress.trim().isEmpty()) {
					addressNode.put("subAddress", subAddress);
				}

				// designDetails/siteDetails/localContactDetails, alternateLocalContactDetails,
				// buildingContactDetails
				JsonNode lconNode = node.at("/site/lconDetails");
				if (!lconNode.isNull() && !lconNode.isMissingNode()) {
					ArrayNode lconArray = (ArrayNode) lconNode;
					for (JsonNode lconArrayElement : lconArray) {
						String lconType = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(lconArrayElement,
								"/lconType");
						lconType = lconType.isEmpty() ? "Primary" : lconType;
						String lconFirstName = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(lconArrayElement,
								"/lconFirstName");
						String lconLastName = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(lconArrayElement,
								"/lconLastName");
						String name = String.format("%s %s", lconFirstName, lconLastName).trim();
						String lconPhone = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(lconArrayElement,
								"/lconPhone");
						String lconEmail = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(lconArrayElement,
								"/lconEmail");
						ObjectNode localContactDetails = mapper.createObjectNode();
						JacksonUtil.objectNodePutStringValueIgnoreNullAndEmpty(localContactDetails, "contactName",
								name);
						JacksonUtil.objectNodePutStringValueIgnoreNullAndEmpty(localContactDetails,
								"contactPhoneNumber", lconPhone);
						JacksonUtil.objectNodePutStringValueIgnoreNullAndEmpty(localContactDetails,
								"contactEmailAddress", lconEmail);
						if (localContactDetails.size() > 0) {
							if ("Primary".equalsIgnoreCase(lconType)) {
								((ObjectNode) nodeMap.get("siteDetailsElement")).set("localContactDetails",
										localContactDetails);
							} else if ("Alternate".equalsIgnoreCase(lconType)) {
								((ObjectNode) nodeMap.get("siteDetailsElement")).set("alternateLocalContactDetails",
										localContactDetails);
							} else if ("Building".equalsIgnoreCase(lconType)) {
								((ObjectNode) nodeMap.get("siteDetailsElement")).set("buildingContactDetails",
										localContactDetails);
							} else {
								log.error("Unknown lconType:{}", lconType);
							}
						}
					}
				}

				// designDetails/siteDetails/endCustomerName
				String endCustomerName = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(dppRequest,
						"/solution/endCustomerName");
				JacksonUtil.objectNodePutStringValueIgnoreNullAndEmpty(nodeMap.get("siteDetailsElement"),
						"endCustomerName", endCustomerName);

				// /customerRequestedDueDate
				JsonNode crddNode = node.at("/site/crdd");
				if (!crddNode.isNull() && !crddNode.isMissingNode()) {
					JacksonUtil.objectNodePutStringValueIgnoreNullAndEmpty(nodeMap.get(InrConstants.ROOT_JSON_MAP_KEY),
							"customerRequestedDueDate", RegExUtil.firstMatchYYYY_MM_DD(crddNode.asText()));
				}

				// designDetails/siteDetails/newBuildingIndicator
				String newBuildingIndicator = JacksonUtil.nodeAtPointerAsText(node, "/newBuildingIndicator");
				if (newBuildingIndicator == null) {
					newNode.put("newBuildingIndicator", "false");
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
		if (path.getFieldName().equals("offerId")) {
			offerId = node.asInt();
			log.info("offerId updated to {}", offerId);
		}
		if (path.getFieldName().equals("automationInd") && !node.isNull() && !node.isMissingNode()) {
			automationInd = node.asText();
		}
		if (path.getFieldName().equals("standardPricingInd") && !node.isNull() && !node.isMissingNode()) {
			standardPricingInd = node.asText();
		}
		if (!node.isNull() && !node.asText().trim().isEmpty() && inrXmlToJsonRuleMap.containsKey(path.getPath())) {
			log.info("processing non container path {}", path.getPath());
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

	@Override
	protected void initializeRuleMap() throws SalesBusinessException {
		inrXmlToJsonRuleMap = inrXmlToJsonRuleDao.getInrXmlToJsonRuleMap("aseDppRequestToSnsd");
	}

}
