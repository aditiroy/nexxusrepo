package com.att.sales.nexxus.inr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.OutputFileConstants;
import com.att.sales.nexxus.dao.model.NxKeyFieldPathModel;
import com.att.sales.nexxus.dao.model.NxLineItemLookUpDataModel;
import com.att.sales.nexxus.dao.model.NxLineItemLookUpFieldModel;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.output.entity.NxAvpnIntAccessOutputBean;
import com.att.sales.nexxus.output.entity.NxAvpnOutputBean;
import com.att.sales.nexxus.output.entity.NxBaseOutputBean;
import com.att.sales.nexxus.output.entity.NxBvoipOutputBean;
import com.att.sales.nexxus.output.entity.NxEthernetAccessOutputBean;
import com.att.sales.nexxus.output.entity.NxMisBean;
import com.att.sales.nexxus.output.entity.NxOutputBean;
import com.att.sales.nexxus.service.NexxusAIService;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.att.sales.nexxus.util.OutputBeanUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * The Class OutputJsonGenerator.
 */
public class OutputJsonGenerator {

	/** The log. */
	private static Logger log = LoggerFactory.getLogger(OutputJsonGenerator.class);

	/** The comma seperator. */
	private static String COMMA_SEPERATOR = ",";

	private ObjectMapper mapper;

	/** The em. */
	private EntityManager em;

	/** The intermediate json. */
	private JsonNode intermediateJson;

	/** The nexxus AI service. */
	private NexxusAIService nexxusAIService;

	/** The nx output bean. */
	private NxOutputBean nxOutputBean = new NxOutputBean();

	/** The json entry. */
	private Map<String, String> jsonEntry = new LinkedHashMap<>();

	/** The lookups. */
	private List<QueryLookup> lookups;

	/** The fall out. */
	private InrFallOutData fallOut = new InrFallOutData();

	/** The line item cache. */
	private Map<Map<String, String>, List<NxLineItemLookUpDataModel>> lineItemCache = new HashMap<>();

	private Map<String, ObjectNode> objectNodeMap = new HashMap<>();

	private boolean isBeanOutput = false;

	private boolean hasValue = false;

	private boolean isLineItemFound = false;
	
	private boolean mpLineItemNotActive = false;
	
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	private Map<String,NxLookupData> nxKeyIdRules;
	
	private List<Map<String, String>> queryParams = new ArrayList<>();
	private List<InventoryJsonLookup> inventoryJsonLookups = new ArrayList<>();
	
	private String flowType;
	
	public static final String LINEITEM_NOT_FOUND_DESC = "Unable to find a line item for your data. Create USH ticket or add manually.";
	public static final String KEY_MISSING_DESC = "One or more keys missing in the inventory data. Create USH ticket or add manually.";
	public static final String MP_LINE_ITEM_INACTIVE = "Line item is not supported by ICB Pricing.";
 
	/**
	 * Instantiates a new output json generator.
	 *
	 * @param em               the em
	 * @param intermediateJson the intermediate json
	 * @param nexxusAIService  the nexxus AI service
	 * @param nxMyPriceRepositoryServce 
	 */
	protected OutputJsonGenerator(ObjectMapper mapper, EntityManager em, JsonNode intermediateJson,
			NexxusAIService nexxusAIService, NxMyPriceRepositoryServce nxMyPriceRepositoryServce, String flowType) {
		super();
		this.mapper = mapper;
		this.em = em;
		this.intermediateJson = intermediateJson;
		this.nexxusAIService = nexxusAIService;
		this.nxMyPriceRepositoryServce = nxMyPriceRepositoryServce;
		this.flowType = flowType;
	}

	/**
	 * Inits the.
	 *
	 * @throws SalesBusinessException the sales business exception
	 */
	protected void init() throws SalesBusinessException {
		initializeLookups();
		if (lookups == null || lookups.isEmpty()) {
			throw new SalesBusinessException(InrConstants.EMPTY_LOOKUP_RULE_EXCEPTION);
		}
	}

	/**
	 * Generate.
	 *
	 * @return the nx output bean
	 * @throws SalesBusinessException the sales business exception
	 */
	public OutputJsonFallOutData generate() throws SalesBusinessException {
		init();
		JsonPath rootPath = JsonPath.getRootPath();
		generateHelper(intermediateJson, rootPath);

		String fallOutData = null;
		try {
			if (fallOut.hasValue()) {
				fallOutData = mapper.writeValueAsString(fallOut);
			}
		} catch (JsonProcessingException e) {
			log.info("Exception in generate", e);
			throw new SalesBusinessException(InrConstants.JSON_PROCESSING_EXCEPTION);
		}
		return new OutputJsonFallOutData(nxOutputBean, fallOutData, intermediateJson, isBeanOutput, hasValue);
	}

	/**
	 * Generate helper.
	 *
	 * @param node the node
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
			break;
		}
	}

	protected void processObjectNode(JsonNode node, JsonPath path) {
		boolean skipLineItemLookUp = false;
		if (node.has(InrIntermediateJsonGenerator.FALLOUTMATCHINGID)) {
			isLineItemFound = false;
			mpLineItemNotActive = false;
			queryParams.clear();
			inventoryJsonLookups.clear();
			if ("Y".equals(node.path(InrConstants.NEXXUS_FALLOUT).asText())) {
				skipLineItemLookUp = true;
			}
		}
		String fieldName = path.getFieldName();
		if (!skipLineItemLookUp) {
			jsonEntry.put(fieldName, null);
			objectNodeMap.put(fieldName, (ObjectNode) node);
			populateJsonFields(node);
			lineItemLookup(node, path);
			
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
				generateHelper(childNode.get(i), path.resolveContainerNode(childName.get(i)));
			}
		}
		
		if (node.has(InrIntermediateJsonGenerator.FALLOUTMATCHINGID) && !isLineItemFound) {
			if (getQueryParams().isEmpty()) { //case no line item lookup is executed
				InventoryJsonLookup inventoryJsonLookup = new InventoryJsonLookup();
				Map<String, String> longForm = new HashMap<>();
				Map<String, String> shortForm = new HashMap<>();
				longForm.put(node.path(InrIntermediateJsonGenerator.FALLOUTMATCHINGID).asText().substring(10),
						node.path(InrIntermediateJsonGenerator.FALLOUTMATCHINGID).asText());
				inventoryJsonLookup.setLongForm(longForm);
				inventoryJsonLookup.setShortForm(shortForm);
				inventoryJsonLookup.setFallOutReason(KEY_MISSING_DESC);
				fallOut.addInventoryJsonLookups(inventoryJsonLookup);
				/*
				if ("Y".equals(node.path(InrConstants.NEXXUS_FALLOUT).asText())) {
					if (node.has(InrConstants.NEXXUS_FALLOUT_REASON)) {
						inventoryJsonLookup.setFallOutReason(node.path(InrConstants.NEXXUS_FALLOUT_REASON).asText());
					} else {
						inventoryJsonLookup.setFallOutReason("USRP has no records");
					}
				} else {
					inventoryJsonLookup.setFallOutReason(LINEITEM_NOT_FOUND_DESC);
				}
				if (!node.has(InrConstants.NEXXUS_FALLOUT_IGNORE)) {
					fallOut.addInventoryJsonLookups(inventoryJsonLookup);
				}
				*/
				if (isFlowTypeInrBeta() && !skipLineItemLookUp && !node.has(InrConstants.NEXXUS_FALLOUT_IGNORE)
						&& !"ACCESSBEID".equals(jsonEntry.get("priceType"))) {
					ObjectNode objNode = (ObjectNode) node;
					objNode.put(InrConstants.NEXXUS_FALLOUT_REASON, KEY_MISSING_DESC);
					objNode.put(InrConstants.NEXXUS_FALLOUT, "Y");
				}
			} else { // case line item lookup executed, but no result
				for (Map<String, String> queryParam : getQueryParams()) {
					fallOut.add(queryParam);
				}
				for (InventoryJsonLookup ijl : getInventoryJsonLookups()) {
					Map<String, String> longForm = ijl.getLongForm();
					longForm.put(node.path(InrIntermediateJsonGenerator.FALLOUTMATCHINGID).asText().substring(10),
							node.path(InrIntermediateJsonGenerator.FALLOUTMATCHINGID).asText());
					fallOut.addInventoryJsonLookups(ijl);
				}
				if (isFlowTypeInrBeta() && !skipLineItemLookUp && !node.has(InrConstants.NEXXUS_FALLOUT_IGNORE)
						&& !"ACCESSBEID".equals(jsonEntry.get("priceType"))) {
					ObjectNode objNode = (ObjectNode) node;
					objNode.put(InrConstants.NEXXUS_FALLOUT, "Y");
					if (MyPriceConstants.ONENET_FEATURE_PRODUCT_CD.equalsIgnoreCase(findOfferName())
							|| MyPriceConstants.VTNS_LD_FEATURE.equalsIgnoreCase(findOfferName())
							|| mpLineItemNotActive) {
						objNode.put(InrConstants.NEXXUS_FALLOUT_REASON, MP_LINE_ITEM_INACTIVE);
					} else {
						objNode.put(InrConstants.NEXXUS_FALLOUT_REASON, LINEITEM_NOT_FOUND_DESC);
					}
				}
			}
		}
		
		if (nxKeyIdRules.containsKey(path.getPath())) {
			String seperator = "\\s*,\\s*";
			ArrayList<String> nxKeyIdValues = new ArrayList<>();
			JsonNode rules = mapper.createObjectNode();
			try {
				rules = mapper.readTree(nxKeyIdRules.get(path.getPath()).getCriteria());
			} catch (IOException e) {
				log.info("ObjecmMapper exception", e);
			}
			if (rules.path("nxItemId").asText().equals("Y")) {
				ArrayList<JsonNode> nxItemNodes = new ArrayList<>();
				aggregateNodeWithField(node, "nxItemId", nxItemNodes);
				nxItemNodes.sort((a, b) -> a.path("nxItemId").asText().compareTo(b.path("nxItemId").asText()));
				if (!rules.path("nxItemFields").asText().isEmpty()) {
					String[] fields = rules.path("nxItemFields").asText().split(seperator);
					nxItemNodes.forEach(n -> {
						ArrayList<String> nxItemFields = new ArrayList<>();
						nxItemFields.add(n.path("nxItemId").asText());
						for (String field : fields) {
							nxItemFields.add(n.path(field).asText());
						}
						nxKeyIdValues.add(String.join("_", nxItemFields));
					});
				} else {
					nxItemNodes.forEach(n -> {
						nxKeyIdValues.add(n.path("nxItemId").asText());
					});
				}
			}
			if (!rules.path("otherFields").asText().isEmpty()) {
				String[] fields = rules.path("otherFields").asText().split(seperator);
				for (String field : fields) {
					if (jsonEntry.containsKey(field)) {
						nxKeyIdValues.add(jsonEntry.get(field));
					} else {
						List<JsonNode> findNodes = node.findValues(field);
						List<String> findStringValues = findNodes.stream().map(n -> n.asText()).collect(Collectors.toList());
						Collections.sort(findStringValues);
						nxKeyIdValues.add(String.join("$", findStringValues));
					}
				}
			}
			((ObjectNode) node).put("nxKeyId", String.join("$", nxKeyIdValues));
			//			if (isFlowTypeInrBeta() && hasAccess(node)) 
			//accessBeid will come for both port and access
			if (isFlowTypeInrBeta()) {
				String accessType = jsonEntry.get("accessType");
				if (!"Foreign".equals(accessType) && !"Ethernet".equals(accessType)) {
					// String[] fields ="secondaryKey,ServiceIndicator".split(seperator);
					List<String> nxKeyIdValues1 = new ArrayList<>();
					List<String> secondaryKeyList = new ArrayList<>();
					for (JsonNode priceDetails : node.path("priceDetails")) {
					//	if ("ACCESSBEID".equals(priceDetails.path("priceType").asText())) {
							secondaryKeyList.add(priceDetails.path("secondaryKey").asText() + "_"
									+ priceDetails.path("quantity").asText());
					//	}
					}
					nxKeyIdValues1.add(String.join("$", secondaryKeyList));
					if (jsonEntry.containsKey("ServiceIndicator")) {
						nxKeyIdValues1.add(jsonEntry.get("ServiceIndicator"));
					}
					((ObjectNode) node).put("nxKeyIdAccess", String.join("$", nxKeyIdValues1));
				} else {
					//Added subProductName for AIA
					String[] fields = { "AccessSpeed", "AccessArchitecture", "PhysicalInterface", "ServiceIndicator","subProductName" };
					ArrayList<String> nxKeyIdValues1 = new ArrayList<>();
					for (String field : fields) {
						if (jsonEntry.containsKey(field)) {
							nxKeyIdValues1.add(jsonEntry.get(field));
						}
					}
					((ObjectNode) node).put("nxKeyIdAccess", String.join("$", nxKeyIdValues1));
				}
				if (MyPriceConstants.EPLSWAN.equals(findOfferName())) {
					List<NxLookupData> skipMpLookup = nxMyPriceRepositoryServce
							.getItemDescFromLookup(MyPriceConstants.ACCESS_PROVIDED_SKIP_MP, "Y");
					String skipMp = skipMpLookup.get(0).getCriteria();
					String accessServiceAEnd = jsonEntry.get("AccessServiceAEnd");
					if (accessServiceAEnd != null && !skipMp.contains(accessServiceAEnd)) {
						((ObjectNode) node).put("nxKeyIdA",
								jsonEntry.get("AccessSpeed") + "$" + jsonEntry.get("PhysicalInterfaceAEnd"));
					}
					String accessServiceZEnd = jsonEntry.get("AccessServiceZEnd");
					if (accessServiceZEnd != null && !skipMp.contains(accessServiceZEnd)) {
						((ObjectNode) node).put("nxKeyIdZ",
								jsonEntry.get("AccessSpeed") + "$" + jsonEntry.get("PhysicalInterfaceZEnd"));
					}
				}
			}
		}
		
		if (!skipLineItemLookUp) {
			unPopulateJsonFields(node);
			objectNodeMap.remove(fieldName);
			jsonEntry.remove(fieldName);
		}
	}

	protected boolean hasAccess(JsonNode node) {
		List<JsonNode> priceTypes = node.findValues("priceType");
		for (JsonNode n : priceTypes) {
			if ("ACCESSBEID".equals(n.asText())) {
				return true;
			}
		}
		return false;
	}

	protected void aggregateNodeWithField(JsonNode node, String fieldName, List<JsonNode> aggregate) {
		switch (node.getNodeType()) {
		case ARRAY:
			for (JsonNode arrayElement : node) {
				aggregateNodeWithField(arrayElement, fieldName, aggregate);
			}
			break;
		case OBJECT:
			if (node.has(fieldName)) {
				if (!isFlowTypeInrBeta() || !"ACCESSBEID".equals(node.path("priceType").asText())) {
					aggregate.add(node);
				}
			}
			Iterator<Entry<String, JsonNode>> iterator = node.fields();
			iterator.forEachRemaining(entry -> {
				aggregateNodeWithField(entry.getValue(), fieldName, aggregate);
			});
			break;
		default:
			break;
		}
		
	}

	protected void processArrayNode(JsonNode node, JsonPath path) {
		for (JsonNode arrayElement : node) {
			generateHelper(arrayElement, path);
		}
	}

	/**
	 * Line item lookup.
	 *
	 * @param node the node
	 */
	protected void lineItemLookup(JsonNode node, JsonPath path) {
		for (QueryLookup lookup : lookups) {
			if (isLookupApplicable(lookup, path)) {
				List<NxLineItemLookUpDataModel> lineItems = findLineItem(lookup, flowType);
				for (NxLineItemLookUpDataModel lineItem : lineItems) {
					createNexusOutput(lineItem, lookup);
				}
				if (lookup.getLookupEntity().getSortOrder() != null && !lineItems.isEmpty()) {
					break;
				}
			}
		}
	}

	/**
	 * Populate json fields.
	 *
	 * @param node the node
	 */
	protected void populateJsonFields(JsonNode node) {
		for (Iterator<Entry<String, JsonNode>> i = node.fields(); i.hasNext();) {
			Entry<String, JsonNode> next = i.next();
			if (!next.getValue().isContainerNode()) {
				JsonNode value = next.getValue();
				if (value.isNull()) {
					jsonEntry.put(next.getKey(), null);
				} else {
					jsonEntry.put(next.getKey(), value.asText());
				}
			}
		}
	}

	/**
	 * Un populate json fields.
	 *
	 * @param node the node
	 */
	protected void unPopulateJsonFields(JsonNode node) {
		for (Iterator<String> i = node.fieldNames(); i.hasNext();) {
			jsonEntry.remove(i.next());
		}
	}

	/**
	 * Initialize lookups.
	 */
	protected void initializeLookups() {
		lookups = findLookupByOfferNameAndInputType(findOfferName(), flowType);
		String offer = intermediateJson.path("service").asText().toUpperCase();
		nxKeyIdRules = nxMyPriceRepositoryServce.getLookupDataByItemId("NX_KEY_ID_" + offer);
	}

	/**
	 * example output: query lookups: [QueryLookup [requiredFields=[beid, country],
	 * queryMapping={field4Value=country, field1Value=beid}]].
	 *
	 * @param offerName the offer name
	 * @param inputType the input type
	 * @return the list
	 */
	protected List<QueryLookup> findLookupByOfferNameAndInputType(String offerName, String inputType) {
		List<QueryLookup> res = new ArrayList<>();
		String queryString = "FROM NxLineItemLookUpFieldModel WHERE upper(offerName) = upper(:offerName) AND inputType = :inputType order by sortOrder, keyFieldCondition";
		TypedQuery<NxLineItemLookUpFieldModel> query = em.createQuery(queryString, NxLineItemLookUpFieldModel.class);
		query.setParameter("offerName", offerName);
		query.setParameter("inputType", inputType);
		List<NxLineItemLookUpFieldModel> resultList = query.getResultList();
		for (NxLineItemLookUpFieldModel lookupEntity : resultList) {
			List<String> requiredFields = new ArrayList<>();
			Map<String, NxKeyFieldPathModel> queryMapping = new HashMap<>();
			Set<NxKeyFieldPathModel> keyFieldMapping = lookupEntity.getKeyFieldMapping();
			if (keyFieldMapping.isEmpty()) {
				continue;
			}
			keyFieldMapping.forEach(mapping -> {
				String keyFieldName = mapping.getKeyFieldName();
				String fieldName = mapping.getFieldName();
				if (keyFieldName != null) {
					requiredFields.add(keyFieldName);
				}
				if (fieldName != null) {
					queryMapping.put(this.getClassPropertyFromTableColumn(mapping.getFieldName()), mapping);
				}
			});
			res.add(new QueryLookup(requiredFields, queryMapping, lookupEntity));
		}
		log.info("query lookups: {}", res);
		return res;
	}

	/**
	 * Find offer name.
	 *
	 * @return the string
	 */
	protected String findOfferName() {
		String offerName = intermediateJson.path("service").asText();
		if (offerName.startsWith("AVPN")) {
			return "AVPN";
		} else if (offerName.startsWith("MIS")) {
			return "ADI";
		}
		return offerName;
	}

	/**
	 * convert table column name "FIELD1_VALUE" to java field name "field1Value".
	 *
	 * @param tableColumn the table column
	 * @return the class property from table column
	 */
	protected String getClassPropertyFromTableColumn(String tableColumn) {
		StringBuilder res = new StringBuilder(tableColumn.toLowerCase());
		for (int i = res.indexOf("_"); i >= 0; i = res.indexOf("_")) {
			res.deleteCharAt(i);
			if (i < res.length()) {
				res.setCharAt(i, Character.toUpperCase(res.charAt(i)));
			}
		}
		return res.toString();
	}

	/**
	 * Checks if is lookup applicable.
	 *
	 * @param lookup the lookup
	 * @return true, if is lookup applicable
	 */
	protected boolean isLookupApplicable(QueryLookup lookup, JsonPath path) {
		String keyFieldName = lookup.getLookupEntity().getKeyFieldName();
		String countryCd = lookup.getLookupEntity().getCountryCd();
		String keyFieldCondition = lookup.getLookupEntity().getKeyFieldCondition();
		if (!path.toString().equals(keyFieldCondition)) {
			return false;
		}
		if ("US".equals(countryCd) && !isCountryUS()) {
			return false;
		}
		if ("MOW".equals(countryCd) && isCountryUS()) {
			return false;
		}
		if ("BEID##COUNTRY_CD".equals(keyFieldName) && !isCountryUS()
				&& "ACCESSBEID".equals(jsonEntry.get("priceType"))) {
			return false;
		}
		if ("SiteCountry##Product##Currency##Technology@@INR".equals(keyFieldName)
				&& (!(isFlowTypeInr() || isFlowTypeInrBeta()) || !"PORTBEID".equals(jsonEntry.get("priceType")))) {
			return false;
		}
		if ("SiteCountry##Product##Currency##Technology@@IGL".equals(keyFieldName) && !isFlowTypeIgl()) {
			return false;
		}
		if (keyFieldName != null && keyFieldName.contains("PORTBEID")) {
			if (!"PORTBEID".equals(jsonEntry.get("priceType"))) {
				return false;
			}
		}
		if ("AccessSpeed##TDM".equals(keyFieldName)) {
			/*if (!"ACCESSBEID".equals(jsonEntry.get("priceType"))) {
				return false;
			} else {
				String speed = jsonEntry.get("AccessSpeed");
				if (speed == null || speed.startsWith("DS") || speed.startsWith("OC")) {
					return false;
				}
			}*/
			if (!"TDM".equals(jsonEntry.get("accessProductName"))) {
				return false;
			}
			String speed = jsonEntry.get("AccessSpeed");
			if (speed == null || speed.startsWith("DS") || speed.startsWith("OC")) {
				return false;
			}
		}
		if ("AccessSpeed##DS3".equals(keyFieldName)) {
		/*	if (!"ACCESSBEID".equals(jsonEntry.get("priceType"))) {
				return false;
			} else {
				String speed = jsonEntry.get("AccessSpeed");
				if (speed == null || (!speed.startsWith("DS") && !speed.startsWith("OC"))) {
					return false;
				}
			}*/
			if (!"TDM".equals(jsonEntry.get("accessProductName"))) {
				return false;
			}
			String speed = jsonEntry.get("AccessSpeed");
			if (speed == null || (!speed.startsWith("DS") && !speed.startsWith("OC"))) {
				return false;
			}
		}
		if (isFlowTypeInrBeta()
				&& !("BVoIP Non-Usage".equals(lookup.getLookupEntity().getOfferName()) 
						|| "BVoIP".equals(lookup.getLookupEntity().getOfferName())
						|| "ABN LD VOICE".equals(lookup.getLookupEntity().getOfferName())
						|| "SDN/ONENET LD VOICE FEATURES".equals(lookup.getLookupEntity().getOfferName())
						|| "SDN/ONENET LD VOICE USAGE".equals(lookup.getLookupEntity().getOfferName())
						|| "VTNS LD VOICE USAGE".equals(lookup.getLookupEntity().getOfferName()))
				&& !hasAllRequiredFields(lookup)) {
			return false;
		}
		if (isFlowTypeInrBeta() && lookup.getLookupEntity().getOfferName().equals("ADI")
				&& jsonEntry.get("componentDescription") != null
				&& jsonEntry.get("componentDescription").toLowerCase().contains("bib")
				&& hasAllRequiredFields(lookup)) {
			if ("BILLRATEID##5311@@PORTBEID".equals(keyFieldName)) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if is flow type igl.
	 *
	 * @return true, if is flow type igl
	 */
	protected boolean isFlowTypeIgl() {
		return InrConstants.IGL.equals(jsonEntry.get(InrConstants.FLOW_TYPE));
	}
	
	protected boolean isFlowTypeInrBeta() {
		return InrConstants.INR_BETA.equals(flowType);
	}

	/**
	 * Checks if is flow type inr.
	 *
	 * @return true, if is flow type inr
	 */
	protected boolean isFlowTypeInr() {
		return InrConstants.INR.equals(jsonEntry.get(InrConstants.FLOW_TYPE));
	}

	/**
	 * Checks if is country US.
	 *
	 * @return true, if is country US
	 */
	protected boolean isCountryUS() {
		String country = null;
		if (jsonEntry.get("country") != null) {
			country = jsonEntry.get("country");
		} else if (jsonEntry.get("siteCountry") != null) {
			country = jsonEntry.get("siteCountry");
		}
		return "US".equalsIgnoreCase(country) || "USA".equalsIgnoreCase(country)
				|| "United States".equalsIgnoreCase(country);
	}

	/**
	 * Checks if is queriable.
	 *
	 * @param lookup the lookup
	 * @param node   the node
	 * @return true, if is queriable
	 */
	/*
	// no longer used
	protected boolean isQueriable(QueryLookup lookup, JsonNode node) {
		List<String> requiredFields = lookup.getRequiredFields();
		return jsonEntry.keySet().containsAll(requiredFields) && isNodeHasAnyOfTheFields(node, requiredFields);
	}
	*/
	
	protected boolean hasAllRequiredFields(QueryLookup lookup) {
		List<String> requiredFields = lookup.getRequiredFields();
		return jsonEntry.keySet().containsAll(requiredFields);
	}
	
	/**
	 * Checks if is node has any of the fields.
	 *
	 * @param node           the node
	 * @param requiredFields the required fields
	 * @return true, if is node has any of the fields
	 */
	protected boolean isNodeHasAnyOfTheFields(JsonNode node, List<String> requiredFields) {
		for (String requiredField : requiredFields) {
			if (node.has(requiredField)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Find line item.
	 *
	 * @param lookup             the lookup
	 * @param flowType           the flow type
	 * @param flags
	 * @param queryParams
	 * @param longTagQueryParams
	 * @return the list
	 */
	protected List<NxLineItemLookUpDataModel> findLineItem(QueryLookup lookup, String flowType) {
		Map<String, NxKeyFieldPathModel> queryMapping = lookup.getQueryMapping();
		Map<String, String> cacheKey = new HashMap<>();
		InventoryJsonLookup inventoryJsonLookup = null;
		if (!inventoryJsonLookups.isEmpty()) {
			inventoryJsonLookup = inventoryJsonLookups.get(0);
		} else {
			inventoryJsonLookup = new InventoryJsonLookup();
		}
		Map<String, String> longForm = new HashMap<>();
		Map<String, String> shortForm = new HashMap<>();
		for (Entry<String, NxKeyFieldPathModel> entry : queryMapping.entrySet()) {
			NxKeyFieldPathModel nxKeyFieldPathModel = entry.getValue();
			cacheKey.put(entry.getKey(), getParameter(nxKeyFieldPathModel));
			if (nxKeyFieldPathModel.getLongKeyName() != null) {
				shortForm.put(nxKeyFieldPathModel.getKeyFieldName(), getParameter(nxKeyFieldPathModel));
				longForm.put(nxKeyFieldPathModel.getLongKeyName(), getParameter(nxKeyFieldPathModel));
			}
		}
		shortForm.put("productCd", cacheKey.get("productCd"));
		inventoryJsonLookup.addLongForm(longForm);
		inventoryJsonLookup.addShortForm(shortForm);
		List<NxLineItemLookUpDataModel> resultList = null;
		String queryString = "FROM NxLineItemLookUpDataModel WHERE flowType = :flowType AND active = 'Y'";
		if (lineItemCache.containsKey(cacheKey)) {
			resultList = lineItemCache.get(cacheKey);
		} else {
			StringBuilder queryBuilder = new StringBuilder(queryString);
			StringBuilder orderBy = new StringBuilder(" order by");
			for (String key : queryMapping.keySet()) {
				if (key.equals("littleProdId")) {
					queryBuilder.append(" AND ").append(key).append(" = :").append(key);
				} else {
					queryBuilder.append(" AND (UPPER(").append(key).append(") = UPPER(:").append(key).append(") OR (")
							.append(key).append(" IS NULL AND :").append(key).append(" IS NULL) OR ").append(key)
							.append(" = '*' OR ").append(key).append(" = 'N/A')");
					orderBy.append(" ").append(key).append(",");
				}
			}
			orderBy.deleteCharAt(orderBy.length() - 1).append(" desc");
			queryBuilder.append(orderBy);
			queryString = queryBuilder.toString();
			TypedQuery<NxLineItemLookUpDataModel> query = em.createQuery(queryString, NxLineItemLookUpDataModel.class); // NOSONAR
			query.setParameter("flowType", flowType);
			for (Entry<String, NxKeyFieldPathModel> entry : queryMapping.entrySet()) {
				query.setParameter(entry.getKey(), getParameterForJpqlQuery(entry.getValue()));
			}
			resultList = query.getResultList();
			lineItemCache.put(cacheKey, resultList);
		}
		if (resultList.size() > 1) {
			log.info("many records found in DB query, the result list size is {}", resultList.size());
			for (Entry<String, NxKeyFieldPathModel> entry : queryMapping.entrySet()) {
				log.info("key {}, value {}", entry.getKey(), getParameter(entry.getValue()));
			}
			// xy3208 only keep first result
			Collections.sort(resultList);
			resultList.subList(1, resultList.size()).clear();
		}
		if (resultList.isEmpty()) {
			List<String> headers = new ArrayList<>();
			List<String> values = new ArrayList<>();

			headers.add("product");
			values.add(lookup.getLookupEntity().getOfferName());

			for (NxKeyFieldPathModel nxKeyFieldPath : queryMapping.values()) {
				String jsonKey = nxKeyFieldPath.getKeyFieldName();
				if (jsonKey != null) {
					String value = getParameter(nxKeyFieldPath);
					headers.add(jsonKey);
					values.add(value);
				}
			}

			String aiRequest = String.format("%s\n%s", String.join(COMMA_SEPERATOR, headers),
					String.join(COMMA_SEPERATOR, values));
			Map<String, String> queryPram = new HashMap<>(cacheKey);
			queryPram.put("nxLookupId", String.valueOf(lookup.getLookupEntity().getLookUpId()));
			queryPram.put("queryString", queryString);
			queryPram.put("aiRequest", aiRequest);
			boolean aiResponse = nexxusAIService.getNxPredictions(aiRequest);
			queryPram.put("aiResponse", String.valueOf(aiResponse));
			queryParams.add(queryPram);
			if (inventoryJsonLookups.isEmpty()) {
				inventoryJsonLookups.add(inventoryJsonLookup);
			}
		} else {
			if ("N".equals(resultList.get(0).getMpActiveYn())) {
				mpLineItemNotActive = true;
				Map<String, String> queryPram = new HashMap<>(cacheKey);
				queryPram.put("nxLookupId", String.valueOf(lookup.getLookupEntity().getLookUpId()));
				queryPram.put("queryString", queryString);
				queryParams.add(queryPram);
				if (inventoryJsonLookups.isEmpty()) {
					inventoryJsonLookups.add(inventoryJsonLookup);
				}
				resultList.clear();
			} else {
				isLineItemFound = true;
			}
		}
		return resultList;
	}

	/**
	 * Gets the parameter.
	 *
	 * @param jsonKey the json key
	 * @return the parameter
	 */
	protected String getParameter(NxKeyFieldPathModel nxKeyFieldPath) {
		if (nxKeyFieldPath.getDefaultValue() != null) {
			if ("null".equals(nxKeyFieldPath.getDefaultValue())) {
				return null;
			}
			return nxKeyFieldPath.getDefaultValue();
		}
		String jsonKey = nxKeyFieldPath.getKeyFieldName();
		String value = jsonEntry.get(jsonKey);
		if ("service".equals(jsonKey) && value.startsWith("AVPN")) {
			return "AVPN";
		}
		return value;
	}
	
	protected Object getParameterForJpqlQuery(NxKeyFieldPathModel nxKeyFieldPath) {
		if ("LITTLE_PROD_ID".equals(nxKeyFieldPath.getFieldName())) {
			return Long.valueOf(getParameter(nxKeyFieldPath));
		}
		return getParameter(nxKeyFieldPath);
	}

	/**
	 * Creates the nexus output.
	 *
	 * @param lineItem the line item
	 * @param lookup
	 */
	protected void createNexusOutput(NxLineItemLookUpDataModel lineItem, QueryLookup lookup) {
		if (null != lineItem.getNexusOutputMapping()
				&& StringUtils.isNotEmpty(lineItem.getNexusOutputMapping().getTabName())) {
			isBeanOutput = true;
			if (lineItem.getNexusOutputMapping().getTabName().equals(OutputFileConstants.AVPN_TAB)) {
				NxAvpnOutputBean bean = createAvpnOutput();
				collectNxOutputData(nxOutputBean.getNxAvpnOutput(), bean, lineItem);
				hasValue = true;
			} else if (lineItem.getNexusOutputMapping().getTabName()
					.equals(OutputFileConstants.AVPN_INTERNATIONAL_TAB)) {
				NxAvpnIntAccessOutputBean bean = createAvpnIntlOutput();
				collectNxOutputData(nxOutputBean.getNxAvpnIntlOutputBean(), bean, lineItem);
				hasValue = true;
			} else if (lineItem.getNexusOutputMapping().getTabName().equals(OutputFileConstants.ETHERNET_ACCESS_TAB)) {
				NxEthernetAccessOutputBean bean = createEthernetAccOutputBean();
				collectNxOutputData(nxOutputBean.getNxEthernetAccOutputBean(), bean, lineItem);
				hasValue = true;
			} else if (lineItem.getNexusOutputMapping().getTabName().equals(OutputFileConstants.MIS_TAB)) {
				NxMisBean bean = createAdiMisOutput(lineItem);
				collectNxOutputData(nxOutputBean.getNxAdiMisBean(), bean, lineItem);
				hasValue = true;
			} else if (lineItem.getNexusOutputMapping().getTabName().equals(OutputFileConstants.BVOIP_TAB)) {
				NxBvoipOutputBean bean = createBvoipOutput();
				collectNxOutputData(nxOutputBean.getNxBvoipOutputBean(), bean, lineItem);
				hasValue = true;
			}
		}
		if (lookup.getLookupEntity().getOutputJsonMapping() != null) {
			try {
				OutputJsonMappings mappings = mapper.readValue(lookup.getLookupEntity().getOutputJsonMapping(),
						OutputJsonMappings.class);
				if (mappings.getMappings() != null) {
					JsonNode lineItemNode = mapper.valueToTree(lineItem);
					String arrayName = mappings.getArrayName();
					String destName = mappings.getDestName();
					ObjectNode objectNode = null;
					if (arrayName != null) {
						objectNode = mapper.createObjectNode();
					} else {
						objectNode = objectNodeMap.get(destName);
					}
					for (OutputJsonMapping mapping : mappings.getMappings()) {
						String source = mapping.getSource();
						JsonNode sourceNode = null;
						if (OutputJsonMapping.SOURCE_LINE_ITEM_QUERY.equalsIgnoreCase(source)) {
							sourceNode = lineItemNode;
						} else {
							sourceNode = objectNodeMap.get(source);
						}
						JsonNode sourceField = sourceNode.path(mapping.getSourceField());
						if (!sourceField.isNull() && !sourceField.isMissingNode()) {
							String destField = mapping.getDestField();
							if (mapping.getType().equals(InrConstants.JSON_TYPE_FIELD_STR)) {
								if (mapping.getDestName() == null) {
									objectNode.put(destField, sourceField.asText());
								} else {
									objectNodeMap.get(mapping.getDestName()).put(destField, sourceField.asText());
								}
								hasValue = true;
							} else if (mapping.getType().equals(InrConstants.JSON_TYPE_FIELD_DOUBLE)) {
								if (mapping.getDestName() == null) {
									objectNode.put(destField, sourceField.asDouble());
								} else {
									objectNodeMap.get(mapping.getDestName()).put(destField, sourceField.asDouble());
								}
								hasValue = true;
							} else if (mapping.getType().equals(InrConstants.JSON_TYPE_FIELD_INT)) {
								if (mapping.getDestName() == null) {
									objectNode.put(destField, sourceField.asInt());
								} else {
									objectNodeMap.get(mapping.getDestName()).put(destField, sourceField.asInt());
								}
								hasValue = true;
							} else if (mapping.getType().equals(InrConstants.JSON_TYPE_FIELD_LONG)) {
								if (mapping.getDestName() == null) {
									objectNode.put(destField, sourceField.asLong());
								} else {
									objectNodeMap.get(mapping.getDestName()).put(destField, sourceField.asLong());
								}
								hasValue = true;
							}
						}
					}
					if (arrayName != null) {
						objectNodeMap.get(destName).withArray(arrayName).add(objectNode);
					}
				}
			} catch (JsonParseException e) {
				log.error("ObjectMapper Error in createNexusOutput method", e);
			} catch (JsonMappingException e) {
				log.error("ObjectMapper Error in createNexusOutput method", e);
			} catch (IOException e) {
				log.error("ObjectMapper Error in createNexusOutput method", e);
			}
		}
	}

	/**
	 * Collect nx output data.
	 *
	 * @param <T>      the generic type
	 * @param inputLst the input lst
	 * @param data     the data
	 * @param lineItem the line item
	 */
	protected <T extends NxBaseOutputBean> void collectNxOutputData(List<T> inputLst, T data,
			NxLineItemLookUpDataModel lineItem) {
		data.setType(lineItem.getFlowType());
		data.setLineItemId(lineItem.getLineItemId() != null ? String.valueOf(lineItem.getLineItemId()) : null);
		data.setSecondaryKey(lineItem.getSecondaryKey() != null ? lineItem.getSecondaryKey() : null);
		data.setLittleProdId(lineItem.getLittleProdId() != null ? String.valueOf(lineItem.getLittleProdId()) : null);
		data.setTopProdId(lineItem.getTopProdId() != null ? String.valueOf(lineItem.getTopProdId()) : null);
		inputLst.add(data);
	}

	/**
	 * Creates the avpn intl output.
	 *
	 * @return the nx avpn int access output bean
	 */
	protected NxAvpnIntAccessOutputBean createAvpnIntlOutput() {
		NxAvpnIntAccessOutputBean bean = new NxAvpnIntAccessOutputBean();
		bean.setProductType(OutputFileConstants.AVPN_INTERNATIONAL_TAB);
		String siteRefID = jsonEntry.get("siteRefID");
		String streetAddress = jsonEntry.get("address");
		String city = jsonEntry.get("city");
		String siteId = findSiteName(siteRefID, streetAddress, city);
		bean.setSiteId(siteId);// siteRefId, defaulting logic
		bean.setCountry(jsonEntry.get("country"));
		String accessBandwidth = jsonEntry.get("accessBandwidth");
		String siteAliase = accessBandwidth + " " + city;
		bean.setSiteAliase(siteAliase);// Merge accessBandwidth + City, defaulting logic
		bean.setCity(city);
		bean.setAccessBandwidth(accessBandwidth);
		bean.setAccessPrimaryOrBackup("Primary");
		bean.setPostalCd(jsonEntry.get("custPostalcode"));
		bean.setAddress(streetAddress);
		bean.setIglooId(jsonEntry.get("serialNumber"));
		bean.setAccessPopCilli(jsonEntry.get("clli"));
		bean.setExistingTelcoCircuitId(null);
		bean.setExistingTelcoProvider(null);
		bean.setInrMrcQuantity("1");
		bean.setTerm(jsonEntry.get("contractTerm"));
		bean.setCurrency(jsonEntry.get("currency"));
		bean.setCurrentMrc(findNonNull(jsonEntry.get("mrc"), jsonEntry.get(InrConstants.ACTUAL_PRICE)));
		return bean;
	}

	/**
	 * Creates the avpn output.
	 *
	 * @return the nx avpn output bean
	 */
	protected NxAvpnOutputBean createAvpnOutput() {
		NxAvpnOutputBean bean = new NxAvpnOutputBean();
		bean.setProductType(OutputFileConstants.AVPN_TAB);
		bean.setSiteId(jsonEntry.get(InrConstants.SITE_ID));
		bean.setSiteName(jsonEntry.get(InrConstants.SITE_NAME));
		bean.setState(jsonEntry.get(InrConstants.STATE));
		bean.setCity(jsonEntry.get(InrConstants.CITY));
		bean.setCountry(jsonEntry.get(InrConstants.COUNTRY));
		bean.setCurrency(jsonEntry.get(InrConstants.CURRENCY));
		bean.setFmoMrcQuantity(null);
		bean.setTerm(null);
		bean.setCurrentMrc(jsonEntry.get(InrConstants.ACTUAL_PRICE));
		bean.setCurrentNrc(null);
		bean.setInrMrcQuantity(jsonEntry.get(InrConstants.QUANTITY));
		bean.setFmoNrcQuantity(null);
		bean.setMrcBeId(jsonEntry.get(InrConstants.BEID));
		bean.setNrcBeId(null);
		bean.setPostalCd(null); // xy add this later
		bean.setAddress(null); // xy add this later
		bean.setInrNrcQuantity(null);

		bean.setPortSpeed(jsonEntry.get(InrConstants.PORT_SPEED));
		bean.setPortProtocol(null);
		return bean;
	}

	/**
	 * Creates the ethernet acc output bean.
	 *
	 * @return the nx ethernet access output bean
	 */
	protected NxEthernetAccessOutputBean createEthernetAccOutputBean() {
		NxEthernetAccessOutputBean bean = new NxEthernetAccessOutputBean();
		bean.setProductType(OutputFileConstants.ETHERNET_ACCESS_TAB);
		String siteRefID = jsonEntry.get("siteRefID");
		String streetAddress = jsonEntry.get("address");
		String city = jsonEntry.get("city");
		String siteName = findSiteName(siteRefID, streetAddress, city);
		bean.setSiteName(siteName);
		bean.setAccessArchitecture(jsonEntry.get("accessArch"));// accessArch
		bean.setIlecSWCCilli(jsonEntry.get("swclli"));// swclli
		bean.setAccociatedService(jsonEntry.get("service"));// service
		bean.setSpeed(jsonEntry.get("bandwidth"));// bandwidth
		bean.setEthernetPopCilli(jsonEntry.get("attEthPop"));// attEthPop
		bean.setInterfaceType(jsonEntry.get("physicalInterface"));// physicalInterface
		bean.setAlternateProvider(jsonEntry.get("reqVendor"));// reqVendor
		bean.setPremisesCode(jsonEntry.get("clli"));// clli
		bean.setInrMrcQuantity("1");
		return bean;
	}

	/**
	 * Creates the adi mis output.
	 *
	 * @param lineItem the line item
	 * @return the nx mis bean
	 */
	protected NxMisBean createAdiMisOutput(NxLineItemLookUpDataModel lineItem) {
		NxMisBean bean = new NxMisBean();
		bean.setProductType(OutputFileConstants.MIS_TAB);
		bean.setState(jsonEntry.get("state"));
		bean.setInrMrcQuantity(jsonEntry.get("quantity"));
		bean.setLocation(OutputBeanUtil.getLocation(jsonEntry.get("state")));
		if (lineItem.getLittleProdId() == 6005L || lineItem.getLittleProdId() == 6171L) {
			bean.setCurrentMrc(jsonEntry.get("misPortRate"));
		} else if (lineItem.getLittleProdId() == 6006L) {
			bean.setCurrentMrc(jsonEntry.get("pNTUpliftRate"));
		} else if (lineItem.getLittleProdId() == 6007L) {
			bean.setCurrentMrc(jsonEntry.get("coSRate"));
		}
		return bean;
	}

	/**
	 * Creates the bvoip output.
	 *
	 * @return the nx bvoip output bean
	 */
	protected NxBvoipOutputBean createBvoipOutput() {
		NxBvoipOutputBean bean = new NxBvoipOutputBean();
		bean.setProductType(OutputFileConstants.BVOIP_TAB);
		bean.setCountry(jsonEntry.get(InrConstants.COUNTRY));
		bean.setPbi(jsonEntry.get("pBICode"));
		bean.setConcurrentCallType(jsonEntry.get(OutputJsonService.CONCURRENT_CALL_TYPE));
		bean.setConcurrentCallQty(jsonEntry.get(OutputJsonService.CONCURRENT_CALL_QTY));
		bean.setModuleCardQty(jsonEntry.get(OutputJsonService.MODULE_CARD_QTY));
		bean.setInitialPeriodDefinition(jsonEntry.get("initialPeriodDefinition"));
		bean.setAdditionalPeriodDefinition(jsonEntry.get("additionalPeriodDefinition"));
		bean.setInitialPeriodRate(jsonEntry.get("initialPeriodRate"));
		bean.setAdditionalPeriodRate(jsonEntry.get("additionalPeriodRate"));
		bean.setUnitRate(jsonEntry.get("unitRate"));
		bean.setDiscount(jsonEntry.get("discount"));
		bean.setTerminatingStateCountryName(jsonEntry.get("terminatingStateCountryName"));
		bean.setFreeMinsQty(jsonEntry.get(OutputJsonService.FREE_MINUTES_QTY));
		bean.setBilledMinsQty(jsonEntry.get(OutputJsonService.BILLED_MINUTES_QTY));
		bean.setLptnType(jsonEntry.get(OutputJsonService.LPTN_TYPE));
		bean.setInrMrcQuantity(jsonEntry.get(OutputJsonService.INR_QTY));
		bean.setCurrentMrc(jsonEntry.get(OutputJsonService.CURRENT_MRC));
		bean.setIobmtIndicator(jsonEntry.get("iOBMTIndicator"));
		return bean;
	}

	/**
	 * Find site name.
	 *
	 * @param siteRefID     the site ref ID
	 * @param streetAddress the street address
	 * @param city          the city
	 * @return the string
	 */
	protected String findSiteName(String siteRefID, String streetAddress, String city) {
		if (siteRefID != null) {
			return siteRefID;
		}
		if (streetAddress == null) {
			return city;
		}
		return streetAddress + " " + city;
	}

	/**
	 * Find non null.
	 *
	 * @param sList the s list
	 * @return the string
	 */
	protected String findNonNull(String... sList) {
		for (String s : sList) {
			if (s != null) {
				return s;
			}
		}
		return null;
	}

	/**
	 * Gets the fall out.
	 *
	 * @return the fall out
	 */
	public InrFallOutData getFallOut() {
		return fallOut;
	}

	public List<Map<String, String>> getQueryParams() {
		return queryParams;
	}

	public List<InventoryJsonLookup> getInventoryJsonLookups() {
		return inventoryJsonLookups;
	}
}
