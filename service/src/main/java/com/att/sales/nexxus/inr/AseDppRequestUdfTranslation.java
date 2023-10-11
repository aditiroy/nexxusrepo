package com.att.sales.nexxus.inr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.InrXmlToJsonRule;
import com.att.sales.nexxus.dao.model.NxUdfMapping;
import com.att.sales.nexxus.dao.model.SalesMsProdcompUdfAttrVal;
import com.att.sales.nexxus.dao.repository.InrXmlToJsonRuleDao;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxUdfMappingDao;
import com.att.sales.nexxus.dao.repository.SalesMsProdcompUdfAttrValRepository;
import com.att.sales.nexxus.util.JacksonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * @author xy3208
 * Translate udf values in designDetails
 * Put translated values in /solution/offers/site and /solution/offers/circuit/component
 * For ADE request, transform the request to associate site with circuit
 * ADE model after udf translation:
 * offers: [
 * 	 {
 * 		site: [] //deleted
 * 		circuit: [
 * 			{
 * 				component: [] //one element for circuit
 * 				endpoint: [
 * 					{
 * 						site: {} //corresponding site block
 * 					}
 * 				] //two element for endpoint a and z
 * 			}
 * 		]
 * 	 }
 * ]
 * 
 */

public class AseDppRequestUdfTranslation {
	private static Logger logger = LoggerFactory.getLogger(AseDppRequestUdfTranslation.class);
	private ObjectMapper mapper;
	private InrXmlToJsonRuleDao inrXmlToJsonRuleDao;
	private Map<String, JsonNode> nodeMap = new HashMap<>();
	private Map<String, InrXmlToJsonRule> inrXmlToJsonRuleMap;
	private JsonNode dppRequest;
	private NxUdfMappingDao nxUdfMappingDao;
	private NxLookupDataRepository nxLookupDataRepository;
	private SalesMsProdcompUdfAttrValRepository salesMsProdcompUdfAttrValRepository;
	private long offerId;
	private long componentCodeId;
	private Map<String, Map<Long, NxUdfMapping>> nxUdfMappingCache = new HashMap<>();

	public AseDppRequestUdfTranslation(ObjectMapper mapper, InrXmlToJsonRuleDao inrXmlToJsonRuleDao,
			JsonNode dppRequest, NxUdfMappingDao nxUdfMappingDao, NxLookupDataRepository nxLookupDataRepository,
			SalesMsProdcompUdfAttrValRepository salesMsProdcompUdfAttrValRepository) {
		super();
		this.mapper = mapper;
		this.inrXmlToJsonRuleDao = inrXmlToJsonRuleDao;
		this.dppRequest = dppRequest;
		this.nxUdfMappingDao = nxUdfMappingDao;
		this.nxLookupDataRepository = nxLookupDataRepository;
		this.salesMsProdcompUdfAttrValRepository = salesMsProdcompUdfAttrValRepository;
	}

	protected void init() throws SalesBusinessException {
		initializeRuleMap();
		if (inrXmlToJsonRuleMap == null || inrXmlToJsonRuleMap.isEmpty()) {
			throw new SalesBusinessException(InrConstants.EMPTY_XML_JSON_RULE_EXCEPTION);
		}
	}

	protected void initializeRuleMap() {
		inrXmlToJsonRuleMap = inrXmlToJsonRuleDao.getInrXmlToJsonRuleMap("aseDppRequestUdfTranslate");
	}

	public void udfTranslate() throws SalesBusinessException {
		init();
		JsonPath rootPath = JsonPath.getRootPath();
		udfTranslateHelper(dppRequest, rootPath);
	}

	protected void udfTranslateHelper(JsonNode node, JsonPath path) {
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
		ArrayNode arrayNode = (ArrayNode) node;
		for (int i = arrayNode.size() - 1; i >= 0; i--) {
			JsonNode element = arrayNode.get(i);
			if (path.getPath().equals("/solution/offers/site")) {
				JsonNode asrItemId = element.path("asrItemId");
				JsonNode endPointSiteIdentifier = element.path("endPointSiteIdentifier");
				if (((asrItemId.isMissingNode() || asrItemId.isNull())
						&& (endPointSiteIdentifier.isMissingNode() || endPointSiteIdentifier.isNull()))
						|| "Y".equalsIgnoreCase(element.path("thirdPartyInd").asText())) {
					arrayNode.remove(i);
				} else {
					this.udfTranslateHelper(element, path);
				}
			} else if (path.getPath().equals("/solution/offers/circuit")) {
				Long attributeId = JacksonUtil.findUdfAttributeIdFromComponentNode(JacksonUtil.findComponentNode(element, 1210), 200164);
				if (attributeId != null && Long.compare(attributeId, 301777L) == 0) {
					arrayNode.remove(i);
				} else {
					this.udfTranslateHelper(element, path);
				}
			} else {
				this.udfTranslateHelper(element, path);
			}
		}
	}

	protected void processObjectNode(JsonNode node, JsonPath path) {
		nodeMap.put(path.getPath(), node);
		if (inrXmlToJsonRuleMap.containsKey(path.getPath())) {
			InrXmlToJsonRule inrXmlToJsonRule = inrXmlToJsonRuleMap.get(path.getPath());
			if (inrXmlToJsonRule.getUdfRuleSet() != null) {
				String udfRuleSet = inrXmlToJsonRule.getUdfRuleSet();
				ObjectNode parentNode = (ObjectNode) nodeMap.get(inrXmlToJsonRule.getFieldParent());
				Map<Long, NxUdfMapping> nxUdfMappingMap = getNxUdfMappingMap(udfRuleSet, offerId, componentCodeId);
				long udfId = node.path("udfId").asLong();
				if (nxUdfMappingMap.containsKey(udfId)) {
					NxUdfMapping nxUdfMapping = nxUdfMappingMap.get(udfId);
					if ("Text".equalsIgnoreCase(nxUdfMapping.getUdfAttributeDatasetName())) {
						JsonNode udfAttributeTextNode = node.path("udfAttributeText");
						if (udfAttributeTextNode.size() == 1) {
							String udfAttributeText = udfAttributeTextNode.path(0).asText();
							if(udfId==StringConstants.SPECIAL_ROUTING_UDF_ID) {
								if(udfAttributeText!=null && (!(udfAttributeText.equalsIgnoreCase("null") || 
										udfAttributeText.equalsIgnoreCase("N/A")))) {
									parentNode.put(nxUdfMapping.getUdfAbbr(), udfAttributeText);
								}
							}else {
								parentNode.put(nxUdfMapping.getUdfAbbr(), udfAttributeText);
							}
							//logger.info("put key:value={}:{}", nxUdfMapping.getUdfAbbr(), udfAttributeText);
						} else if (udfAttributeTextNode.size() > 1) {
							if (!parentNode.path(nxUdfMapping.getUdfAbbr()).isArray()) {
								parentNode.remove(nxUdfMapping.getUdfAbbr());
							}
							for (JsonNode e : udfAttributeTextNode) {
								String udfAttributeText = e.asText();
								parentNode.withArray(nxUdfMapping.getUdfAbbr()).add(udfAttributeText);
								//logger.info("put key:value={}:{}", nxUdfMapping.getUdfAbbr(), udfAttributeText);
							}
						}
					} else {
						JsonNode udfAttributeIdNode = node.path("udfAttributeId");
						if (udfAttributeIdNode.size() == 1) {
							long udfAttributeId = udfAttributeIdNode.path(0).asLong();
							String udfAttributeValue = translateUdfAttributeId(udfId, udfAttributeId);
							parentNode.put(nxUdfMapping.getUdfAbbr(), udfAttributeValue);
							//logger.info("put key:value={}:{}", nxUdfMapping.getUdfAbbr(), udfAttributeValue);
						} else if (udfAttributeIdNode.size() > 1) {
							if (!parentNode.path(nxUdfMapping.getUdfAbbr()).isArray()) {
								parentNode.remove(nxUdfMapping.getUdfAbbr());
							}
							for (JsonNode e : udfAttributeIdNode) {
								long udfAttributeId = e.asLong();
								String udfAttributeValue = translateUdfAttributeId(udfId, udfAttributeId);
								
								parentNode.withArray(nxUdfMapping.getUdfAbbr()).add(udfAttributeValue);
								//logger.info("put key:value={}:{}", nxUdfMapping.getUdfAbbr(), udfAttributeValue);
							}
						}

					}
				}
			}
		} else {
			// iterate in list rather than using iterator to avoid
			// java.util.ConcurrentModificationException
			List<String> childName = new ArrayList<>();
			List<JsonNode> childNode = new ArrayList<>();
			Iterator<Entry<String, JsonNode>> iterator = node.fields();
			iterator.forEachRemaining(entry -> {
				childName.add(entry.getKey());
				childNode.add(entry.getValue());
			});
			for (int i = 0; i < childName.size(); i++) {
				this.udfTranslateHelper(childNode.get(i), path.resolveContainerNode(childName.get(i)));
			}
			
			// change phoneNumber to 10 digit
			// all phoneNumber are not in udf block, safe to put in this else branch
			updatePhoneNumber(node, path);

			// before exit offer block in ADE case, associate site block with circuit block
			// and remove site block
			if (path.getPath().equals("/solution/offers")) {
				JsonNode offerIdNode = node.path("offerId");
				JsonNode circuit = node.path("circuit");
				JsonNode siteUnderOffer = node.path("site");
				if (!circuit.isMissingNode() && !circuit.isNull()
						&& StringConstants.OFFERID_ADE.equals(offerIdNode.asText())) {
					JsonNode site = node.path("site");
					Map<String, JsonNode> siteMap = new HashMap<>();
					for (JsonNode siteElement : site) {
						String siteId = siteElement.path("siteId").asText();
						if (StringUtils.isNotEmpty(siteId)) {
							siteMap.put(siteId, siteElement);
						}
					}
					for (JsonNode circuitElement : circuit) {
						JsonNode component = circuitElement.path("component");
						if (!component.isMissingNode() && !component.isNull()) {
							ArrayNode componentArray = (ArrayNode) component;
							String channelizedIndicator = null;
							for (int i = componentArray.size() - 1; i >= 0; i--) {
								JsonNode componentElement = componentArray.get(i);
								JsonNode componentCodeIdNode = componentElement.path("componentCodeId");
								if (!StringConstants.COMPONENTID_ENDPOINT.equals(componentCodeIdNode.asText())) {
									channelizedIndicator = JacksonUtil.nodeAtPointerAsTextNullToEmptyString(
											componentElement, "/channelizedIndicator");
									break;
								}
							}
							for (int i = componentArray.size() - 1; i >= 0; i--) {
								JsonNode componentElement = componentArray.get(i);
								JsonNode componentCodeIdNode = componentElement.path("componentCodeId");
								if (!StringConstants.COMPONENTID_ENDPOINT.equals(componentCodeIdNode.asText())) {
									continue;
								}
								JacksonUtil.objectNodePutStringValueIgnoreNullAndEmpty(componentElement,
										"channelizedIndicator", channelizedIndicator);
								((ObjectNode) circuitElement).withArray("endpoint").add(componentElement);
								componentArray.remove(i);
								JsonNode referenceId = componentElement.at("/references/0/referenceId");
								if (!referenceId.isMissingNode() && !referenceId.isNull()) {
									JsonNode siteNode = siteMap.get(referenceId.asText());
									if (siteNode != null) {
										((ObjectNode) componentElement).set("site", siteNode);
									}
								}
							}
						}
					}
					((ObjectNode) node).remove("site");
				} else if (!siteUnderOffer.isMissingNode() && !siteUnderOffer.isNull()
						&& StringConstants.OFFERID_ASE.equals(offerIdNode.asText())) {
					ArrayNode site = (ArrayNode) siteUnderOffer;
					for (int i = site.size() - 1; i >= 0; i--) {
						JsonNode siteElement = site.get(i);
						if ("Change".equalsIgnoreCase(siteElement.path("macdType").asText())) {
							JsonNode macdActivity = siteElement.path("macdActivity");
							if (macdActivity.isArray()) {
								ArrayNode macdActivityArray = (ArrayNode) macdActivity;
								for (int j = macdActivityArray.size() - 1; j >= 0; j--) {
									if ("516".equals(macdActivityArray.get(j).asText())) {
										macdActivityArray.remove(j);
									}
								}
								if (macdActivityArray.size() == 0) {
									site.remove(i);
								}
							} else if ("516".equals(macdActivity.asText())) {
								site.remove(i);
							}
						}
					}
				}
			}
		}
	}

	// phoneNumber location:
	// /solution/offers/site/lconDetails/lconPhone
	// /solution/contact/workPhone
	// /solution/contact/cellPhoneNumber
	protected void updatePhoneNumber(JsonNode node, JsonPath path) {
		if (path.getPath().equals("/solution/offers/site/lconDetails")) {
			updatePhoneNode(node, "lconPhone");
		} else if (path.getPath().equals("/solution/contact")) {
			updatePhoneNode(node, "workPhone");
			updatePhoneNode(node, "cellPhoneNumber");
		}
	}

	protected void updatePhoneNode(JsonNode node, String field) {
		JsonNode phoneNumber = node.path(field);
		if (!phoneNumber.isNull() && !phoneNumber.isMissingNode()) {
			ObjectNode objNode = (ObjectNode) node;
			String allDigitPhoneNumber = phoneNumber.asText().replaceAll("[^\\d]", "" );
			if (allDigitPhoneNumber.length() == 10) {
				objNode.put(field, allDigitPhoneNumber);
			} else {
				objNode.remove(field);
			}
		}
	}
	
	

	protected String translateUdfAttributeId(long udfId, long udfAttributeId) {
		SalesMsProdcompUdfAttrVal salesMsProdcompUdfAttrVal = salesMsProdcompUdfAttrValRepository
				.findTopByOfferIdAndComponentIdAndUdfIdAndUdfAttributeIdAndActive(offerId, componentCodeId, udfId,
						udfAttributeId, "Active");
		if (salesMsProdcompUdfAttrVal != null) {
			return salesMsProdcompUdfAttrVal.getUdfAttributeValue();
		}

		return null;
	}

	protected void processNonContainerNode(JsonNode node, JsonPath path) {
		if (path.getFieldName().equals("offerId")) {
			offerId = node.asLong();
			logger.info("offerId updated to {}", offerId);
		} else if (path.getFieldName().equals("componentCodeId")) {
			componentCodeId = node.asLong();
			logger.info("componentCodeId updated to {}", componentCodeId);
		}
	}

	protected String generateNxUdfMappingCacheKey(String ruleSet, long offerId, long componentCodeId) {
		return String.format("%s_%d_%d", ruleSet, offerId, componentCodeId);
	}

	protected Map<Long, NxUdfMapping> getNxUdfMappingMap(String ruleSet, long offerId, long componentCodeId) {
		String key = generateNxUdfMappingCacheKey(ruleSet, offerId, componentCodeId);
		if (!nxUdfMappingCache.containsKey(key)) {
			nxUdfMappingCache.put(key, nxUdfMappingDao.getNxUdfMappingMap(ruleSet, offerId, componentCodeId));
		}
		return nxUdfMappingCache.get(key);
	}
}
