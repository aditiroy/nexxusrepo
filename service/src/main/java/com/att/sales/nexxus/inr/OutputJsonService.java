package com.att.sales.nexxus.inr;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.repository.InrXmlToJsonRuleDao;
import com.att.sales.nexxus.dao.repository.InrXmlToJsonRuleDao.InrXmlToJsonRuleDaoResult;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * The Class OutputJsonService.
 */
@Component
public class OutputJsonService {

	/** The log. */
	private static Logger log = LoggerFactory.getLogger(OutputJsonService.class);

	/** The Constant LPTN_PBIS. */
	private static final List<String> LPTN_PBIS = Arrays.asList("00095891", "00095887", "00073019", "00070447",
			"00073017", "00070451", "00073018", "00073020", "00097269", "00070450", "00073011", "00097254", "00070441");

	/** The Constant MODULE_CARD_PBIS. */
	private static final List<String> MODULE_CARD_PBIS = Arrays.asList("00070436", "00079022", "00079023");

	/** The Constant BILLED_MINS_PBI_DESCS. */
	private static final List<String> BILLED_MINS_PBI_DESCS = Arrays.asList("VOAVPN INT L OFFNET",
			"VOAVPN INTERNATIONAL OFF-NET", "VDNASB INTERNATIONAL OFF-NET", "VOIP INTERNATIONAL OFF-NET",
			"VOAVPN US OFF-NET LD", "VDNASB US OFF-NET LD", "VDNASB US ON-NET LD", "VOAVPN US OFFNET LD",
			"IPTF INTERSTATE USAGE", "IPTF INTRASTATE USAGE", "IPTF INTRALATA USAGE", "IPTF CANADA USAGE",
			"VOAVPN IPTF INTERSTATE USAGE", "VOAVPN IPTF INTRASTATE USAGE", "VOAVPN IPTF INTRALATA USAGE",
			"VOAVPN IPTF CANADA USAGE", "VOAVPN IPTF MOW USAGE");

	/** The Constant BILLED_MINS_PBI. */
	private static final List<String> BILLED_MINS_PBI = Arrays.asList("00070467", "00092978", "00079044", "00082410",
			"00093908");

	/** The Constant BILLED_MINS_PBI_DESC. */
	private static final String BILLED_MINS_PBI_DESC = "VoIP US Off-Net LD";

	/** The Constant LPTN_TYPE. */
	public static final String LPTN_TYPE = "lptnType";

	/** The Constant INR_QTY. */
	public static final String INR_QTY = "inrQty";
	
	/** The Constant JURISDICTION. */
	public static final String JURISDICTION = "jurisdiction";

	/** The Constant JURISDICTION_COL. */
	public static final String JURISDICTION_COL = "jurisdictionCol";

	/** The Constant MODULE_CARD_QTY. */
	public static final String MODULE_CARD_QTY = "moduleCardQty";

	/** The Constant CONCURRENT_CALL_QTY. */
	public static final String CONCURRENT_CALL_QTY = "concurrentCallQty";

	/** The Constant CONCURRENT_CALL_TYPE. */
	public static final String CONCURRENT_CALL_TYPE = "concurrentCallType";

	/** The Constant PBI_CODE. */
	public static final String PBI_CODE = "PBICode";

	/** The Constant BILLED_MINUTES_QTY. */
	public static final String BILLED_MINUTES_QTY = "billedMinutesQty";

	/** The Constant FREE_MINUTES_QTY. */
	public static final String FREE_MINUTES_QTY = "freeMinutesQty";

	/** The Constant GENERIC_QUANTITY. */
	public static final String GENERIC_QUANTITY = "GenericQuantity";

	/** The Constant CURRENT_MRC. */
	public static final String CURRENT_MRC = "currentMRC";

	/** The Constant DISCOUNT. */
	public static final String DISCOUNT = "discount";

	private static final String[] newFieldNames = { "priceType", "beid", "quantity", "localListPrice", "actualPrice" };
	private static final String[][] portAccessPVCDetailsNames = {
			{ "PORTBEID", "PORTBEID", "PORTQUANTITY", "PORTGROSSCHARGE", "PORTSPEEDNETCHARGE" },
			{ "UNILINKBEID", "UNILINKBEID", "UNILINKQUANTITY", "UNILINKGROSSCHARGE", "UNILINKNETCHARGE" },
			{ "DIVERSITYBEID", "DIVERSITYBEID", "DIVERSITYQUANTITY", "DIVERSITYGROSSCHARGE", "DIVERSITYNETCHARGE" },
			{ "ACCESSBEID", "ACCESSBEID", "ACCESSQUANTITY", "ACCESSGROSSCHARGE", "ACCESSNETCHARGE" },
			{ "PORTOVERAGEBEID", "PORTOVERAGEBEID", "PORTOVERAGEQUANTITY", "PORTOVERAGEGROSSCHARGE",
					"PORTOVERAGENETCHARGE" } };
	private static final String[][] pvcChargesNames = {
			{ "COSBEID", "COSBEID", "COSQUANTITY", "COSGROSSCHARGE", "COSNETCHARGE" },
			{ "INTERNETVLANBEID", "INTERNETVLANBEID", "INTERNETVLANQUANTITY", "INTERNETVLANGROSSCHARGE",
					"INTERNETVLANNETCHARGE" },
			{ "DNSZONEBEID", "DNSZONEBEID", "DNSZONEQUANTITY", "DNSZONEGROSSCHARGE", "DNSZONENETCHARGE" },
			{ "MVLBEID", "MVLBEID", "MVLQUANTITY", "MVLGROSSCHARGE", "MVLNETCHARGE" } };
	private static final String[][] managedDetailsNames = {
			{ "ROUTER1BEID", "ROUTER1BEID", "ROUTER1QUANTITY", "ROUTER1GROSSCHARGE", "ROUTER1NETCHARGE" },
			{ "CSUBEID", "CSUBEID", "CSUQUANTITY", "CSUGROSSCHARGE", "CSUNETCHARGE" },
			{ "ROUTER2BEID", "ROUTER2BEID", "ROUTER2QUANTITY", "ROUTER2GROSSCHARGE", "ROUTER2NETCHARGE" },
			{ "ENHANCEDRPTBEID", "ENHANCEDRPTBEID", "ENHANCEDREPORTQUANTITY", "ENHANCEDREPORTGROSSCHARGE",
					"ENHANCEDREPORTNETCHARGE" } };
	private static final String[][] managedFeatureDetailsNames = { { "MANAGEDFEATUREBEID", "MANAGEDFEATUREBEID",
			"MANAGEDFEATUREQUANTITY", "MANAGEDFEATUREGROSSCHARGE", "MANAGEDFEATURENETCHARGE" } };

	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;

	/** The inr factory. */
	@Autowired
	private InrFactory inrFactory;

	@Autowired
	private InrXmlToJsonRuleDao inrXmlToJsonRuleDao;
	
	@Autowired
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;

	/**
	 * Gets the output data.
	 *
	 * @param intermediateJson the intermediate json
	 * @return the output data
	 * @throws SalesBusinessException the sales business exception
	 */
	public OutputJsonFallOutData getOutputData(JsonNode intermediateJson, String flowType) throws SalesBusinessException {
		OutputJsonGenerator outputJsonGenerator = inrFactory.getOutputJsonGenerator(intermediateJson, flowType);
		OutputJsonFallOutData outputJsonFallOutData = outputJsonGenerator.generate();
		return outputJsonFallOutData;
	}

	/**
	 * Preprocess inventory json.
	 *
	 * @param inventoryJson the inventory json
	 */
	public void preprocessInventoryJson(JsonNode inventoryJson) {
		if (inventoryJson.has("BVOIPPricingInventory")) { // bvoip usage
			List<JsonNode> nodes = inventoryJson.findValues("UsageDetails");
			for (JsonNode node : nodes) {
				if (node.isObject()) {
					JsonNode objNode = node;
					node = mapper.createArrayNode().add(objNode);
				}
				for (JsonNode element : node) {
					ObjectNode site = (ObjectNode) element;

					// preview [CDT Jurisdiction] column
					String jurisdiction = site.path("Jurisdiction").asText();
					if (jurisdiction.equalsIgnoreCase("Intrastate InterLATA")
							|| jurisdiction.equalsIgnoreCase("Intrastate IntraLATA")) {
						site.put(JURISDICTION_COL, "Intrastate");
					} else if (jurisdiction.equalsIgnoreCase("Interstate InterLATA")
							|| jurisdiction.equalsIgnoreCase("Interstate IntraLATA")) {
						site.put(JURISDICTION_COL, "Interstate");
					} else {
						site.put(JURISDICTION_COL, "N/A");
					}

					// preview billed quantity and free quantity column
					if (!site.path("TotalQuantityAnnual").asText().isEmpty()) {
						String totalQuantityAnnual = site.path("TotalQuantityAnnual").asText();
						if (("Outbound".equalsIgnoreCase(site.path("CallDirection").asText())
								&& "USAGE".equalsIgnoreCase(site.path("BillingElementCode").asText()))
								&& ((isFreeMinsPbiDesc(site.path("PBIDescription").asText()))
										|| (BILLED_MINS_PBI_DESC.equalsIgnoreCase(site.path("PBIDescription").asText())
												&& BILLED_MINS_PBI.contains(site.path(PBI_CODE).asText())))) {
							site.put(BILLED_MINUTES_QTY, totalQuantityAnnual);
						} else {
							site.put(FREE_MINUTES_QTY, totalQuantityAnnual);
						}
					}

					// preview discount column
					String grossDiscount = site.path("GrossDiscount").asText();
					String grossCharge = site.path("GrossCharge").asText();
					double discount = 0;
					try {
						double gd = Double.parseDouble(grossDiscount);
						double gc = Double.parseDouble(grossCharge);
						if (gc != 0) {
							discount = gd / gc * 100;
						}
					} catch (NumberFormatException e) {
						log.info("Exception", e);
					}
					site.put(DISCOUNT, String.format("%.0f", discount));
					
					site.put(LPTN_TYPE, "*");
					
					bvoipModuleCards(site);
					
					if (!site.path(GENERIC_QUANTITY).asText().isEmpty()) {
						site.put(INR_QTY, site.path(GENERIC_QUANTITY).asText());
					}
					
					if (!site.path("UnitRate").asText().isEmpty()) {
						site.put(CURRENT_MRC, site.path("UnitRate").asText());
					}
					
					String pbiCode = site.path(PBI_CODE).asText();
					if (!"Intrastate".equals(site.path(JURISDICTION_COL).asText())
							|| !"Interstate".equals(site.path(JURISDICTION_COL).asText())) {
						double myBilledMinuttes = 0;
						discount = 0;
						if (!site.path(GENERIC_QUANTITY).isMissingNode() && !site.path(GENERIC_QUANTITY).isNull()) {
							try {
								myBilledMinuttes = Double.parseDouble(site.path(GENERIC_QUANTITY).asText());
							} catch (NumberFormatException e) {
								log.info("Exception", e);
							}
						}
						if (!site.path("GrossDiscount").isMissingNode() && !site.path("GrossDiscount").isNull()) {
							try {
								discount = Double.parseDouble(site.path("GrossDiscount").asText());
							} catch (NumberFormatException e) {
								log.info("Exception", e);
							}
						}
						if (myBilledMinuttes > 0 && discount <= 0 && !"00070467".equals(pbiCode)) {
							site.put("cdtRegion", "US");
						}
					}
					String originatingStateCountryName = site.path("OriginatingStateCountryName").asText();
					if (("00079044".equals(pbiCode) || "00092978".equals(pbiCode) || "00093908".equals(pbiCode))
							&& !originatingStateCountryName.isEmpty()) {
						NxLookupData nxLookupData = nxMyPriceRepositoryServce
								.getLookupDataByItemId("INR_BVOIP_CDT_REGION")
								.get(originatingStateCountryName);
						if (nxLookupData != null) {
							site.put("cdtRegion", nxLookupData.getDescription());
						}
					}
				}
			}
		} else if (inventoryJson.has("BVOIPFeaturesPricingInventory")) { // bvoip non-usage
			List<JsonNode> nodes = inventoryJson.findValues("Site");
			for (JsonNode node : nodes) {
				if (node.isObject()) {
					JsonNode objNode = node;
					node = mapper.createArrayNode().add(objNode);
				}
				for (JsonNode element : node) {
					ObjectNode site = (ObjectNode) element;
					List<JsonNode> featureDetails = site.findValues("FeatureDetails");
					for (JsonNode featureDetail : featureDetails) {
						if (featureDetail.isObject()) {
							site.remove("FeatureDetails");
							featureDetail = site.withArray("FeatureDetails").add(featureDetail);
						}
						ArrayNode featureArray = (ArrayNode) featureDetail;

						for (int j = 0; j < featureArray.size(); j++) {
							ObjectNode feature = (ObjectNode) featureArray.get(j);
							// preview discount column
							String grossDiscount = feature.path("DiscountAmount").asText();
							String grossCharge = feature.path("GrossAmount").asText();
							double discount = 0;
							try {
								double gd = Double.parseDouble(grossDiscount);
								double gc = Double.parseDouble(grossCharge);
								if (gc != 0) {
									discount = gd / gc * 100;
								}
							} catch (NumberFormatException e) {
								log.info("Exception", e);
							}
							feature.put(DISCOUNT, String.format("%.0f", discount));
						}

						// preview CDT LP TN Type
						int standardCount = 0; 
						int virtualCount = 0; 
						try { 
							standardCount = Integer.parseInt(site.path("StandardTNCount").asText()); 
						} catch (NumberFormatException e) { 
							log.info("NumberFormatException in parsing StandardTNCount"); 
						} 
						try { 
							virtualCount = Integer.parseInt(site.path("VirtualTNCount").asText()); 
						} catch (NumberFormatException e) { 
							log.info("NumberFormatException in parsing VirtualTNCount"); 
						}
 
						for (int j = featureArray.size() - 1; j >= 0; j--) { 
							ObjectNode feature = (ObjectNode) featureArray.get(j); 
							String pBICode = feature.path(PBI_CODE).asText(); 
							if (LPTN_PBIS.contains(pBICode)) { 
								if (standardCount > 0 && virtualCount > 0) { 
									ObjectNode deepCopy = feature.deepCopy(); 
									deepCopy.put(LPTN_TYPE, "Standard"); 
									deepCopy.put(INR_QTY, site.path("StandardTNCount").asText()); 
									featureArray.insert(j, deepCopy); 
									feature.put(LPTN_TYPE, "Virtual"); 
									feature.put(INR_QTY, site.path("VirtualTNCount").asText()); 
								} else if (standardCount > 0) { 
									feature.put(LPTN_TYPE, "Standard"); 
									feature.put(INR_QTY, site.path("StandardTNCount").asText()); 
								} else if (virtualCount > 0) { 
									feature.put(LPTN_TYPE, "Virtual"); 
									feature.put(INR_QTY, site.path("VirtualTNCount").asText()); 
								} 
							} else { 
								int genericCount = 0; 
								try { 
									genericCount = Integer.parseInt(feature.path("GenericQuantity").asText()); 
								} catch (NumberFormatException e) { 
									log.info("NumberFormatException in parsing VirtualTNCount"); 
								} 
								if(genericCount > 0) { 
									feature.put(INR_QTY, feature.path("GenericQuantity").asText()); 
								} 
							} 
						}
						for (int j = 0; j < featureArray.size(); j++) {
							ObjectNode feature = (ObjectNode) featureArray.get(j);
							// preview CDT Concurrent Call Range
							bvoipModuleCards(feature);
							if (!feature.path("NetAmount").asText().isEmpty()
									&& !feature.path(GENERIC_QUANTITY).asText().isEmpty()) {
								String netAmount = feature.path("NetAmount").asText();
								String genericQuantity = feature.path(GENERIC_QUANTITY).asText();
								try {
									double netAmt = Double.parseDouble(netAmount);
									double genericQty = Double.parseDouble(genericQuantity);
									if (genericQty != 0) {
										double curretMrc = netAmt / genericQty;
										feature.put(CURRENT_MRC, String.format("%.2f", curretMrc));
									}
								} catch (NumberFormatException e) {
									log.info("Exception", e);
								}
							}
						}
					}
				}
			}
		} else if (inventoryJson.has("SDNOneNetLDVoiceUsage")) { // SDN/ONENET LD VOICE USAGE
			List<JsonNode> nodes = inventoryJson.findValues("UsageDetails");
			String falloutReason = "OutBoundScheduleRate Not Available";
			Set<String> outboundRateSchduleValues = nxMyPriceRepositoryServce.getLookupDataByItemId("OneNet_OutboundRateSchdule").keySet();
			for (JsonNode node : nodes) {
				if (node.isObject()) {
					JsonNode objNode = node;
					node = mapper.createArrayNode().add(objNode);
				}
				for (JsonNode element : node) {
					ObjectNode usageDetails = (ObjectNode) element;
					String outboundRateSchedule = usageDetails.path("OutboundRateSchedule").asText();
					usageDetails.put("OriginalOutBoundRateSchedule", outboundRateSchedule);
					if (!outboundRateSchduleValues.contains(outboundRateSchedule)) {
						usageDetails.put(InrConstants.FALLOUT_REASON, falloutReason);
						if (outboundRateSchedule.isEmpty()) {
							usageDetails.put("OutboundRateSchedule", usageDetails.path("AccessTypeDescription").asText());
						} else if (outboundRateSchedule.endsWith("A")) {
							usageDetails.put("OutboundRateSchedule", "A");
						}
					}
				}
			}
		} else if (inventoryJson.has("DDAResponse")) { // DOMESTIC DEDICATED ACCESS
			List<JsonNode> nestedNodesDomesticEthernetAccessInventory = inventoryJson.findValues("DomesticEthernetAccessInventory");
			for (JsonNode nodesDomesticEthernetAccessInventory : nestedNodesDomesticEthernetAccessInventory) {
				if (nodesDomesticEthernetAccessInventory.isObject()) {
					JsonNode nodesDomesticEthernetAccessInventoryCopy = nodesDomesticEthernetAccessInventory;
					nodesDomesticEthernetAccessInventory = mapper.createArrayNode().add(nodesDomesticEthernetAccessInventoryCopy);
				}
//				for (JsonNode nodeDomesticEthernetAccessInventory : nodesDomesticEthernetAccessInventory) {
				for (int i = 0; i < nodesDomesticEthernetAccessInventory.size(); i++) {
					JsonNode nodeDomesticEthernetAccessInventory = nodesDomesticEthernetAccessInventory.get(i);
					List<JsonNode> nestedNodesCustomerLocationInfo = nodeDomesticEthernetAccessInventory.findValues("CustomerLocationInfo");
					for (JsonNode nodesCustomerLocationInfo : nestedNodesCustomerLocationInfo) {
						if (nodesCustomerLocationInfo.isObject()) {
							JsonNode nodesCustomerLocationInfoCopy = nodesCustomerLocationInfo;
							nodesCustomerLocationInfo = mapper.createArrayNode().add(nodesCustomerLocationInfoCopy);
						}
						for (JsonNode nodeCustomerLocationInfo : nodesCustomerLocationInfo) {
							// for all the CustomerLocationInfo node under DomesticEthernetAccessInventory
							JsonNode physicalInterface = nodeCustomerLocationInfo.path("PhysicalInterface");
							JsonNode cdtPhysicalInterface = nodeCustomerLocationInfo.path("CDTPhysicalInterface");
							if ((cdtPhysicalInterface.isMissingNode() || cdtPhysicalInterface.isNull())
									&& (!physicalInterface.isMissingNode() && !physicalInterface.isNull())) {
								NxLookupData nxLookupData = nxMyPriceRepositoryServce
										.getLookupDataByItemId("INR_CDT_PHYSICAL_INTERFACE")
										.get(physicalInterface.asText());
								if (nxLookupData != null) {
									ObjectNode nodeCustomerLocationInfoObj = (ObjectNode) nodeCustomerLocationInfo; 
									nodeCustomerLocationInfoObj.put("CDTPhysicalInterface", nxLookupData.getDescription());
								}
							}
						}
					}
				}
			}
		} else if (inventoryJson.has("ANIRAPricingInventory")) { // ANIRA
			List<JsonNode> accountDetailsList = inventoryJson.findValues("AccountDetails");
			for (JsonNode node : accountDetailsList) {
				if (node.isObject()) {
					JsonNode objNode = node;
					node = mapper.createArrayNode().add(objNode);
				}
				for (JsonNode element : node) {
					ObjectNode accountDetails = (ObjectNode) element;
					if (accountDetails.has("ANIRAService")) { // move ANIRAService tag to the end
						JsonNode backup = accountDetails.get("ANIRAService");
						accountDetails.remove("ANIRAService");
						accountDetails.set("ANIRAService", backup);
					}
				}
			}
			List<JsonNode> siteDetailsList = inventoryJson.findValues("SiteDetails");
			for (JsonNode node : siteDetailsList) {
				if (node.isObject()) {
					JsonNode objNode = node;
					node = mapper.createArrayNode().add(objNode);
				}
				for (JsonNode element : node) {
					ObjectNode siteDetails = (ObjectNode) element;
					if (siteDetails.has("ANIRAAccessList")) { // move ANIRAAccessList tag to the end
						JsonNode backup = siteDetails.get("ANIRAAccessList");
						siteDetails.remove("ANIRAAccessList");
						siteDetails.set("ANIRAAccessList", backup);
					}
				}
			}
		} else if (inventoryJson.has("InrMISPNTResponse")) { // MIS/ADI
			List<JsonNode> sapidDetailsList = inventoryJson.findValues("SAPIDDetails");
			for (JsonNode node : sapidDetailsList) {
				if  (node.isObject()) {
					JsonNode objNode = node;
					node = mapper.createArrayNode().add(objNode);
				}
				for (JsonNode element : node) {
					ObjectNode sapidDetails = (ObjectNode) element;
					if (!sapidDetails.has("PortRateSchedule")) {
						sapidDetails.put("PortRateSchedule", 3);
					}
				}
			}
		} else if (inventoryJson.has("SDNOneNetLDVoiceFeatures")) { // OneNet feature
			List<JsonNode> featuresDetailsList = inventoryJson.findValues("FeaturesDetails");
			for (JsonNode node : featuresDetailsList) {
				if (node.isObject()) {
					JsonNode objNode = node;
					node = mapper.createArrayNode().add(objNode);
				}
				for (JsonNode element : node) {
					ObjectNode featuresDetails = (ObjectNode) element;
					if (!featuresDetails.path("PerInstanceRate").isMissingNode() && !featuresDetails.path("PerInstanceRate").isNull()) {
						String value = featuresDetails.path("PerInstanceRate").asText();
						if (value.contains(".") && (value.charAt(value.length() - 1) == '.' || value.charAt(value.length() - 1) == '0')) {
							StringBuilder sb = new StringBuilder(value);
							while (sb.charAt(sb.length() - 1) == '0') {
								sb.deleteCharAt(sb.length() - 1);
							}
							if (sb.charAt(sb.length() - 1) == '.') {
								sb.deleteCharAt(sb.length() - 1);
							}
							featuresDetails.put("PerInstanceRate", sb.toString());
						}
					}
				}
			}
		}
	}

	/**
	 * Bvoip module cards.
	 *
	 * @param node the node
	 */
	protected void bvoipModuleCards(ObjectNode node) {
		String pBICode = node.path(PBI_CODE).asText();
		if (MODULE_CARD_PBIS.contains(pBICode) && !node.path(GENERIC_QUANTITY).asText().isEmpty()) {
			String genericQuantity = node.path(GENERIC_QUANTITY).asText();
			node.put(CONCURRENT_CALL_QTY, genericQuantity);
			node.put(MODULE_CARD_QTY, "1");
			try {
				Double genericQty = Double.parseDouble(genericQuantity);
				if (genericQty <= 12) {
					node.put(CONCURRENT_CALL_TYPE, "VoMIS12");
				} else if (genericQty <= 24) {
					node.put(CONCURRENT_CALL_TYPE, "VoMIS24");
				} else if (genericQty <= 48) {
					node.put(CONCURRENT_CALL_TYPE, "VoMIS48");
				} else {
					node.put(CONCURRENT_CALL_TYPE, "T3");
				}
			} catch (NumberFormatException e) {
				log.info("Exception", e);
			}
		}
	}

	/**
	 * Checks if is free mins pbi desc.
	 *
	 * @param pbiDesc the pbi desc
	 * @return true, if is free mins pbi desc
	 */
	protected boolean isFreeMinsPbiDesc(String pbiDesc) {
		for (String s : BILLED_MINS_PBI_DESCS) {
			if (s.equalsIgnoreCase(pbiDesc)) {
				return true;
			}
		}
		return false;
	}

	public InrXmlToJsonRuleDaoResult trimInventoryJson(JsonNode inventoryJson) {
		String rootTag = inventoryJson.fieldNames().next();
		InrXmlToJsonRuleDaoResult inrXmlToJsonRuleDaoResult = inrXmlToJsonRuleDao.getInrXmlToJsonRuleDaoResult(rootTag);
		JsonPath rootPath = JsonPath.getRootPath();
		IdGenerator idGen = new IdGenerator();
		trimJsonHelper(inventoryJson, rootPath, inrXmlToJsonRuleDaoResult, idGen);
		return inrXmlToJsonRuleDaoResult;
	}

	private void trimJsonHelper(JsonNode node, JsonPath path, InrXmlToJsonRuleDaoResult inrXmlToJsonRuleDaoResult,
			IdGenerator idGen) {
		switch (node.getNodeType()) {
		case ARRAY:
			trimArrayNode(node, path, inrXmlToJsonRuleDaoResult, idGen);
			break;
		case OBJECT:
			trimObjectNode(node, path, inrXmlToJsonRuleDaoResult, idGen);
			break;
		default:
			break;
		}
	}

	private void trimObjectNode(JsonNode node, JsonPath path, InrXmlToJsonRuleDaoResult inrXmlToJsonRuleDaoResult,
			IdGenerator idGen) {
		ObjectNode objNode = (ObjectNode) node;
		List<String> keysToBeRemoved = new LinkedList<>();
		Iterator<Entry<String, JsonNode>> fields = objNode.fields();
		while (fields.hasNext()) {
			Entry<String, JsonNode> next = fields.next();
			JsonNode value = next.getValue();
			trimJsonHelper(value, path.resolveContainerNode(next.getKey()), inrXmlToJsonRuleDaoResult, idGen);
			if (value.getNodeType() == JsonNodeType.OBJECT || value.getNodeType() == JsonNodeType.ARRAY) {
				if (value.size() == 0
						&& !inrXmlToJsonRuleDaoResult.getFieldNullTags().contains(path.resolveField(next.getKey()))) {
					keysToBeRemoved.add(next.getKey());
				}
			} else {
				if ((value.equals(NullNode.getInstance()) || value.asText().isEmpty())
						&& !inrXmlToJsonRuleDaoResult.getFieldNullTags().contains(path.resolveField(next.getKey()))) {
					keysToBeRemoved.add(next.getKey());
				}
			}
		}
		keysToBeRemoved.forEach(objNode::remove);
		if (inrXmlToJsonRuleDaoResult.getFalloutMatchingTags().contains(path.getPath())) {
			objNode.put(InrIntermediateJsonGenerator.FALLOUTMATCHINGID, String.format("%010d", idGen.getNext())
					+ path.toString() + "/" + InrIntermediateJsonGenerator.FALLOUTMATCHINGID);
		}
		if (inrXmlToJsonRuleDaoResult.getNxSiteMatchingTags().contains(path.getPath())) {
			objNode.put(InrIntermediateJsonGenerator.NXSITEMATCHINGID, idGen.getNext());
		}
	}

	private void trimArrayNode(JsonNode node, JsonPath path, InrXmlToJsonRuleDaoResult inrXmlToJsonRuleDaoResult,
			IdGenerator idGen) {
		ArrayNode arrayNode = (ArrayNode) node;
		for (int i = arrayNode.size() - 1; i >= 0; i--) {
			JsonNode arrayElement = arrayNode.get(i);
			trimJsonHelper(arrayElement, path, inrXmlToJsonRuleDaoResult, idGen);
			if (arrayElement.getNodeType() == JsonNodeType.OBJECT || arrayElement.getNodeType() == JsonNodeType.ARRAY) {
				if (arrayElement.size() == 0
						&& !inrXmlToJsonRuleDaoResult.getFieldNullTags().contains(path.getPath())) {
					arrayNode.remove(i);
				}
			} else {
				if ((arrayElement.equals(NullNode.getInstance()) || arrayElement.asText().isEmpty())
						&& !inrXmlToJsonRuleDaoResult.getFieldNullTags().contains(path.getPath())) {
					arrayNode.remove(i);
				}
			}
		}
	}

	public void modifyInventoryJsonForIntermediateJsonGeneration(JsonNode inventoryJson) {
		if (inventoryJson.has("AVPNPricingInventory")) {
			List<JsonNode> siteDetailsList = inventoryJson.findValues("SiteDetails");
			for (JsonNode siteDetails : siteDetailsList) {
				if (siteDetails.getNodeType() == JsonNodeType.ARRAY) {
					for (JsonNode element : siteDetails) {
						modifyAvpnSiteDetails(element);
					}
				} else if (siteDetails.getNodeType() == JsonNodeType.OBJECT) {
					modifyAvpnSiteDetails(siteDetails);
				}
			}

		}
	}

	protected void modifyAvpnSiteDetails(JsonNode siteDetails) {
		JsonNode portAccessPVCDetails = siteDetails.path("PortAccessPVCDetails");
		if (portAccessPVCDetails.getNodeType() == JsonNodeType.OBJECT) {
			ArrayNode nexxusDesign = mapper.createArrayNode();
			modifyAvpnByAddingDesign(nexxusDesign, portAccessPVCDetailsNames, portAccessPVCDetails);
			modifyAvpnProcessingDesign(portAccessPVCDetails, nexxusDesign, "PVCCharges", pvcChargesNames);
			modifyAvpnProcessingDesign(siteDetails, nexxusDesign, "ManagedDetails", managedDetailsNames);
			modifyAvpnProcessingDesign(siteDetails, nexxusDesign, "ManagedFeatureDetails", managedFeatureDetailsNames);
			if (nexxusDesign.size() > 0) {
				((ObjectNode) portAccessPVCDetails).set("nexxusDesign", nexxusDesign);
			}
		} else if (portAccessPVCDetails.getNodeType() == JsonNodeType.ARRAY) {
			for (int i = 0; i < portAccessPVCDetails.size() - 1; i++) {
				ArrayNode nexxusDesign = mapper.createArrayNode();
				JsonNode element = portAccessPVCDetails.get(i);
				modifyAvpnByAddingDesign(nexxusDesign, portAccessPVCDetailsNames, element);
				modifyAvpnProcessingDesign(element, nexxusDesign, "PVCCharges", pvcChargesNames);
				if (nexxusDesign.size() > 0) {
					((ObjectNode) element).set("nexxusDesign", nexxusDesign);
				}
			}
			ArrayNode nexxusDesign = mapper.createArrayNode();
			JsonNode element = portAccessPVCDetails.get(portAccessPVCDetails.size() - 1);
			modifyAvpnByAddingDesign(nexxusDesign, portAccessPVCDetailsNames, element);
			modifyAvpnProcessingDesign(element, nexxusDesign, "PVCCharges", pvcChargesNames);
			modifyAvpnProcessingDesign(siteDetails, nexxusDesign, "ManagedDetails", managedDetailsNames);
			modifyAvpnProcessingDesign(siteDetails, nexxusDesign, "ManagedFeatureDetails", managedFeatureDetailsNames);
			if (nexxusDesign.size() > 0) {
				((ObjectNode) element).set("nexxusDesign", nexxusDesign);
			}
		}
	}

	protected void modifyAvpnProcessingDesign(JsonNode siteDetails, ArrayNode nexxusDesign, String fieldName,
			String[][] names) {
		List<JsonNode> nodes = siteDetails.findValues(fieldName);
		for (JsonNode node : nodes) {
			if (node.getNodeType() == JsonNodeType.OBJECT) {
				modifyAvpnByAddingDesign(nexxusDesign, names, node);
			} else if (node.getNodeType() == JsonNodeType.ARRAY) {
				for (JsonNode e : node) {
					modifyAvpnByAddingDesign(nexxusDesign, names, e);
				}
			}
		}
	}

	protected void modifyAvpnByAddingDesign(ArrayNode nexxusDesign, String[][] names, JsonNode node) {
		for (int i = 0; i < names.length; i++) {
			ObjectNode element = mapper.createObjectNode();
			element.put(newFieldNames[0], names[i][0]);
			for (int j = 1; j < names[i].length; j++) {
				modifyAvpnAddingNewValue(element, node, names, i, j);
			}
			nexxusDesign.add(element);
		}
	}

	protected void modifyAvpnAddingNewValue(ObjectNode element, JsonNode node, String[][] names, int i, int j) {
		JsonNode path = node.path(names[i][j]);
		if (!path.isMissingNode() && !path.isNull()) {
			element.set(newFieldNames[j], node.get(names[i][j]));
		}
	}

	public static class IdGenerator {
		private int id = 1;

		public int getNext() {
			return id++;
		}
	}
}
