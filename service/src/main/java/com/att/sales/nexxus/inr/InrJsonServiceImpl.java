package com.att.sales.nexxus.inr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.att.aft.dme2.internal.google.common.base.Strings;
import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.MpPopVendorMapping;
import com.att.sales.nexxus.dao.model.NxDwPriceDetails;
import com.att.sales.nexxus.dao.model.NxDwToJsonRules;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.repository.MpPopVendorMappingRepository;
import com.att.sales.nexxus.dao.repository.NxDwPriceDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxDwToJsonRulesRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.UsrpDao;
import com.att.sales.nexxus.service.InrBetaGenerateNxsiteId;
import com.att.sales.nexxus.service.InrProcessingService;
import com.att.sales.nexxus.service.InrQualifyService;
import com.att.sales.nexxus.service.MessageConsumptionServiceImpl;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.att.sales.nexxus.serviceValidation.model.SiteDetails;
import com.att.sales.nexxus.serviceValidation.service.AVSQUtil;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.LogUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class InrJsonServiceImpl extends BaseServiceImpl {
	public static final String ORDERING_DETAILS_MISSING = "Ordering/Provisioning details are missing for the circuit. Create USH ticket or add manually.";
	public static final String BILL_IS_NOT_GENERATED = "New Circuit. Bill not available yet. User must add manually.";
	public static final String SECONDARY_CIRCUIT_OF = "This circuit may be the secondary circuit of ";
	public static final String TDM_ACCESS_SPEED_MISSING = "Access Speed missing in TDM.";
	public static final String CIRCUIT_DISCONNECTED = "circuit is disconnected.";
	public static final String OLD_CIRCUITS_NO_BILLING = "old/inactive circuits with no billing.";
	public static final String NOT_EPLSWAN_BITRATE = "Bitrate in design doesn't belong to EPLSWAN.";
	private static Logger logger = LoggerFactory.getLogger(InrJsonServiceImpl.class);
	private static String RULE_NAME = "DESIGN_JSON";
	private static String Y = "Y";
	private static final String POPCILLI_VENDOR_NAME_TO_SWCCLI_DATASET = "POPCILLI_VENDOR_NAME_TO_SWCCLI";

	
	@Autowired
	private NxDwToJsonRulesRepository nxDwToJsonRulesRepository;

	@Autowired
	private NxDwPriceDetailsRepository nxDwPriceDetailsRepository;

	@Autowired
	private NxRequestDetailsRepository nxRequestDetailsRepository;

	@Autowired
	private UsrpDao usrpDao;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Autowired
	private InrProcessingService inrProcessingService;
	
	@Autowired
	private InrQualifyService inrQualifyService;
	
	@Autowired
	private InrBetaGenerateNxsiteId inrBetaGenerateNxsiteId;
	
	@Autowired
	private MessageConsumptionServiceImpl messageConsumptionServiceImpl;
	
	@Autowired
	private AVSQUtil avsqUtil;
	
	@Autowired
	public MpPopVendorMappingRepository mpPopVendorMappingRepository;

	public ServiceResponse inrJsonProcess(InrJsonServiceRequest request) throws SalesBusinessException {
		logger.info("enter inrJsonProcess method for id {}",request.getId());
		long startTime = System.currentTimeMillis();
		ServiceResponse response = new ServiceResponse();
		NxDwPriceDetails nxDwPriceDetails = nxDwPriceDetailsRepository.findById(request.getId()).get();
		NxRequestDetails nxRequestDetails = nxRequestDetailsRepository.findByNxReqId(nxDwPriceDetails.getNxReqId());
		JsonNode priceJson;

		try {
			priceJson = mapper.readTree(nxDwPriceDetails.getPriceJson());
		} catch (JsonProcessingException e) {
			logger.info("Exception:", e);
			throw new SalesBusinessException();
		}
		long loadRulesAndDesignJsonGenerationStartTime = System.currentTimeMillis();
		Args args = initializeArgs(nxRequestDetails.getProduct(),nxRequestDetails.getNxReqId());

		if (!"Y".equals(args.skipAttachingDesignData)) {
			updatePriceJson(priceJson, args);
		}
		logger.info("After updatePriceJson for request id {}",request.getId());
		LogUtils.logExecutionDurationMs(logger, args.usrpQueryTime, 
				String.format("query usrp for nx req id %d", nxRequestDetails.getNxReqId()));
		LogUtils.logExecutionDurationMs(logger, args.avsqCallTime, 
				String.format("Total avsq call time to get Swccli value for nx req id %d", nxRequestDetails.getNxReqId()));
		LogUtils.logExecutionDurationMs(logger,
				String.format("load rules and generate design json for nx req id %d", nxRequestDetails.getNxReqId()),
				loadRulesAndDesignJsonGenerationStartTime);
		long inrProcessingStartTime = System.currentTimeMillis();
		((ObjectNode) priceJson).put(InrConstants.SOURCE, InrConstants.USRP);
		ObjectNode inventoryJson = mapper.createObjectNode();
		inventoryJson.set(args.rootTagName, priceJson);
		NxOutputFileModel nxOutputFileModel = inrProcessingService.createInrNexusOutput(nxRequestDetails, inventoryJson);
		nxRequestDetails.addNxOutputFiles(nxOutputFileModel);
		nxRequestDetailsRepository.saveAndFlush(nxRequestDetails);
		LogUtils.logExecutionDurationMs(logger,
				String.format("inr processing for nx req id %d", nxRequestDetails.getNxReqId()),
				inrProcessingStartTime);
		//to release memory
		priceJson = null;
		inventoryJson = null;
		logger.info("createInrNexusOutput for request id {}",request.getId());
		long inrbetaQualifyStartTime = System.currentTimeMillis();
		//call inr beta qualification check first so that post that request status is updated
		/*moving inrBetaQualifycheck call to generateNxsiteidInrBeta 
		 * inrQualifyService.inrBetaQualifycheck(nxRequestDetails.getNxReqId());
		LogUtils.logExecutionDurationMs(logger,
				String.format("inr beta qualify for nx req id %d", nxRequestDetails.getNxReqId()),
				inrbetaQualifyStartTime);*/
		long nxsiteidGenStartTime = System.currentTimeMillis();
		inrBetaGenerateNxsiteId.generateNxsiteidInrBeta(nxRequestDetails.getNxReqId(), true, null);
		LogUtils.logExecutionDurationMs(logger,
				String.format("Nxsiteid Generation for nx req id %d", nxRequestDetails.getNxReqId()),
				nxsiteidGenStartTime);
		messageConsumptionServiceImpl.sendMailNotification(nxRequestDetails);
		LogUtils.logExecutionDurationMs(logger,
				String.format("Total inr json processing for nx req id %d", nxRequestDetails.getNxReqId()),
				startTime);
		return response;
	}
	
	/*
	 * Args is used to pass value within this class
	 * only initializeArgs method change the fields of Args
	 * Args fields should be read only in other methods
	 */
	protected Args initializeArgs(String product,Long nxReqId) throws SalesBusinessException {
		if ("MIS/PNT".equals(product)) {
			product = MyPriceConstants.ADI;
		} else if ("GMIS".equals(product)) {
			product = MyPriceConstants.ADIG;
		}
		Args args = new Args();
		args.nxReqId = nxReqId;
		List<NxDwToJsonRules> rules = nxDwToJsonRulesRepository
				.findByOfferAndRuleNameAndActive(product, RULE_NAME, Y);
		Map<String, NxDwToJsonRules> jsonBuildRuleMap = rules.stream()
				.collect(Collectors.toMap(NxDwToJsonRules::getFieldName, Function.identity()));
		args.jsonBuildRuleMap = jsonBuildRuleMap;
		args.product = product;
		args.queryPath = "/accountDetails";
		args.attachUsrpDataPath = "/accountDetails/design";
		args.rootTagName = product.replaceAll("\\W", "").toLowerCase() + "Usrp";
		NxLookupData nxLookupData = nxLookupDataRepository.findTopByDatasetNameAndItemId("USRP_ARGS", product);
		try {
			JsonNode data = mapper.readTree(nxLookupData.getCriteria());
			args.queryString = data.path("queryString").asText();
			args.queryParams = data.path("queryParams").asText();
			args.viewName = data.path("viewName").asText();
			args.skipAttachingDesignData = data.path("skipAttachingDesignData").asText();
		} catch (JsonProcessingException e) {
			logger.info("Exception:", e);
			throw new SalesBusinessException();
		}
		Map<String, Object> requestParams = new HashMap<>();	
		String conversationId = String.format("NEXXUSAVSQCALL%d", nxReqId);
		String traceId = String.format("%d%d", nxReqId, System.currentTimeMillis());

		requestParams.put(ServiceMetaData.XTraceId, traceId);	
		requestParams.put(ServiceMetaData.XSpanId, traceId);	
		requestParams.put(ServiceMetaData.XCONVERSATIONID, conversationId);
		args.paramMap=requestParams;
		
		NxRequestDetails nxRequestDetails = nxRequestDetailsRepository.findByNxReqId(nxReqId);
		String searchCriteriaJson = nxRequestDetails.getManageBillingPriceJson();
		if(searchCriteriaJson != null) {
			JsonNode searchCriteriaNode = JacksonUtil.toJsonNode(searchCriteriaJson);
			if ( searchCriteriaNode.has("searchCriteria")
				&& !Strings.isNullOrEmpty(searchCriteriaNode.get("searchCriteria").asText())) {
			String searchcriteria = searchCriteriaNode.get("searchCriteria").asText();
			args.searchCriteria=searchcriteria;
			}
		}

		return args;
	}
	
	/*
	 * attaching usrp design to price json
	 */
	protected void updatePriceJson(JsonNode priceJson, Args args) {
		JsonPath rootPath = JsonPath.getRootPath();
		args.nodeMap.put(InrConstants.ROOT_JSON_MAP_KEY, priceJson);
		traverseHelper(priceJson, rootPath, args);
		if (!"MainAccountNumber".equalsIgnoreCase(args.searchCriteria)) {
			processUnattachedUsrpData(priceJson, args);
		}
		updateAfterTraverse(priceJson, args);
	}
	
	protected void updateAfterTraverse(JsonNode priceJson, Args args) {
		if (MyPriceConstants.AVPN.equalsIgnoreCase(args.product) || MyPriceConstants.ADI.equalsIgnoreCase(args.product)) {
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
						if ("A".equals(priceDetails.path("typeOfCharge").asText())) {
							if (nxt1QuantityUpdateFlag) {
								((ObjectNode) priceDetails).put("quantity", String.valueOf(qty));
							}
							if (ethernetNoFalloutFlag) {
								((ObjectNode) priceDetails).put(InrConstants.NEXXUS_FALLOUT_IGNORE, "Y");
							}
							if (tdmNoAccessSpeedFlag) {
								((ObjectNode) priceDetails).put(InrConstants.NEXXUS_FALLOUT, "Y");
								((ObjectNode) priceDetails).put(InrConstants.NEXXUS_FALLOUT_REASON,
										priceDetails.path(InrConstants.NEXXUS_FALLOUT_REASON).asText()
												+ TDM_ACCESS_SPEED_MISSING);
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
									priceDetails.path(InrConstants.NEXXUS_FALLOUT_REASON).asText()
											+ SECONDARY_CIRCUIT_OF
											+ nxt1SecondaryCircuitIdToPrimaryCircuitId.get(circuitId) + ".");
						}
					}
				}
			}
		}
	}

	/*
	 	sample output
		abc801ati:abc802ati
		abc809ati:abc810ati
		abc899ati:abc900ati
		abc9ati:1abc0ati
		abc999ati:1abc000ati
		//carry to letter has issue, but should not be seen in real circuitId
	*/
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

	protected void traverseHelper(JsonNode node, JsonPath path, Args args) {
		if (node.getNodeType() == JsonNodeType.ARRAY) {
			for (int i = 0; i < node.size(); i++) {
				traverseHelper(node.get(i), path, args);
			}
		} else if (node.getNodeType() == JsonNodeType.OBJECT) {
			args.nodeMap.put(path.toString(), node);
			updateNodeMapIdentifierKey(node, path, args);
			queryUsrp(node, path, args);
			attachUsrpData(node, path, args);
			
			List<String> childName = new ArrayList<>();
			List<JsonNode> childNode = new ArrayList<>();
			Iterator<Entry<String, JsonNode>> iterator = node.fields();
			iterator.forEachRemaining(entry -> {
				childName.add(entry.getKey());
				childNode.add(entry.getValue());
			});
			for (int i = 0; i < childName.size(); i++) {
				traverseHelper(childNode.get(i), path.resolveContainerNode(childName.get(i)), args);
			}
			if (args.queryPath.equals(path.toString()) && !MyPriceConstants.EPLSWAN.equals(args.product)) {
				((ObjectNode) node).remove("design");
			}
			args.nodeMap.remove(path.toString());
		}
	}
	
	protected void processUnattachedUsrpData(JsonNode priceJson, Args args) {
		if (!args.usrpDesigns.isEmpty()) {
			if (MyPriceConstants.EPLSWAN.equals(args.product)) {
				args.usrpDesigns.forEach(row -> {
					row.put("service_mcn", row.get("mcn"));
					row.put("service_customer_name", row.get("customer_name"));
				});
			}
			Map<String, List<Map<String, Object>>> mcnToUnAttachedUsrpData = args.usrpDesigns.stream().collect(Collectors.groupingBy(row -> String.valueOf(row.get("service_mcn")).trim()));
			ArrayNode accountDetails = (ArrayNode) priceJson.path("accountDetails");
			mcnToUnAttachedUsrpData.forEach((k, v) -> {
				boolean accountDetailsAddedToJson = false; 
				for (Map<String, Object> usrpDesign : v) {
					usrpDesign.put(InrConstants.NEXXUS_FALLOUT, "Y");
					if ("Y".equalsIgnoreCase(String.valueOf(usrpDesign.get("disc_ind")))) {
						// usrpDesign.put(InrConstants.NEXXUS_FALLOUT_REASON, OLD_CIRCUITS_NO_BILLING);
						// skip adding design to json
					} else {
						if (!NOT_EPLSWAN_BITRATE.equals(usrpDesign.get(InrConstants.NEXXUS_FALLOUT_REASON))) {
							usrpDesign.put(InrConstants.NEXXUS_FALLOUT_REASON, BILL_IS_NOT_GENERATED);
						}
						// when adding to json, only add accountDetails once
						if (!accountDetailsAddedToJson) {
							ObjectNode accountDetailsElement = mapper.createObjectNode();
							accountDetailsElement.put("MCN", k);
							accountDetailsElement.put("custName", String.valueOf(v.get(0).get("service_customer_name")) + " ");
							accountDetails.add(accountDetailsElement);
							args.nodeMap.put("/accountDetails", accountDetailsElement);
							updateNodeMapIdentifierKey(accountDetailsElement, new JsonPath("/accountDetails"), args);
							accountDetailsAddedToJson = true;
						}
						processUsrpData(usrpDesign, args);
					}
				}
				
			});
		}
	}

	protected void attachUsrpData(JsonNode node, JsonPath path, Args args) {
		if (args.attachUsrpDataPath.equals(path.toString())) {
			Map<String, Object> usrpDesign = getUsrpDesign(node, args);
			processUsrpData(usrpDesign, args);
		}
	}

	protected void processUsrpData(Map<String, Object> usrpDesign, Args args) {
		for (NxDwToJsonRules jsonBuildRule : args.jsonBuildRuleMap.values()) {
			if (isjsonBuildRuleForValueNode(jsonBuildRule)) {
				if (null != jsonBuildRule.getDwKey() && null == usrpDesign.get(jsonBuildRule.getDwKey())) {
					continue;
				}
				ObjectNode parentNode = getParentNode(jsonBuildRule, usrpDesign, args);
				putValueToNode(jsonBuildRule.getFieldType(),
						convertData(jsonBuildRule, usrpDesign.get(jsonBuildRule.getDwKey()), usrpDesign, args), parentNode,
						jsonBuildRule.getFieldName(), jsonBuildRule, args);
			}
		}
	}

	protected ObjectNode getParentNode(NxDwToJsonRules jsonBuildRule, Map<String, Object> usrpDesignRow, Args args) {
		if (MyPriceConstants.EPLSWAN.equals(args.product) && args.nodeMap.containsKey(args.attachUsrpDataPath)) {
			return (ObjectNode) args.nodeMap.get(args.attachUsrpDataPath);
		}
		NxDwToJsonRules parentRule = args.jsonBuildRuleMap.get(jsonBuildRule.getFieldParent());
		String identifierKey = getIdentifierKey(parentRule, args, usrpDesignRow);
		if (args.nodeMap.containsKey(identifierKey)) {
			return (ObjectNode) args.nodeMap.get(identifierKey);
		}
		ObjectNode grandParentNode = getParentNode(parentRule, usrpDesignRow, args);
		ObjectNode parentNode = createdParentNode(parentRule, usrpDesignRow, args);
		if (parentRule.getFieldType().equals("list")) {
			grandParentNode.withArray(parentRule.getFieldName()).add(parentNode);
		} else {
			grandParentNode.set(parentRule.getFieldName(), parentNode);
		}
		if (usrpDesignRow.containsKey(InrConstants.NEXXUS_FALLOUT)) {
			parentNode.put(InrConstants.NEXXUS_FALLOUT, String.valueOf(usrpDesignRow.get(InrConstants.NEXXUS_FALLOUT)));
		}
		if (usrpDesignRow.containsKey(InrConstants.NEXXUS_FALLOUT_REASON)) {
			parentNode.put(InrConstants.NEXXUS_FALLOUT_REASON, String.valueOf(usrpDesignRow.get(InrConstants.NEXXUS_FALLOUT_REASON)));
		}
		args.nodeMap.put(identifierKey, parentNode);
		return parentNode;
	}

	protected ObjectNode createdParentNode(NxDwToJsonRules jsonBuildRule, Map<String, Object> usrpDesignRow, Args args) {
		ObjectNode res = mapper.createObjectNode();
		String[] keys = jsonBuildRule.getIdentifierKey().split(",");
		String[] types = jsonBuildRule.getIdentifierType().split(",");
		String[] usrpDesignKeys = jsonBuildRule.getDwKey().split(",");
		for (int i = 0; i < keys.length; i++) {
			putValueToNode(types[i], convertData(jsonBuildRule, usrpDesignRow.get(usrpDesignKeys[i]), usrpDesignRow, args), res, keys[i], jsonBuildRule, args);
		}
		return res;
	}

	protected void putValueToNode(String type, Object value, ObjectNode parentNode, String tagName,
			NxDwToJsonRules jsonBuildRule, Args args) {
		// xy3208 avpn and adi use the same logic
		if ("COPY".equals(jsonBuildRule.getType())) {
		 if("priceDetails".equals(jsonBuildRule.getFieldName())) {
			if (args.nodeMap.containsKey(jsonBuildRule.getDefaultValue())) {
				// if at design level, copy the priceDetails
				JsonNode priceDetailsArray = args.nodeMap.get(jsonBuildRule.getDefaultValue()).path("priceDetails");
				for (JsonNode priceDetailsElement : priceDetailsArray) {
					parentNode.withArray("priceDetails").add(priceDetailsElement);
					if(priceDetailsElement.has(InrConstants.NEXXUS_FALLOUT_REASON) &&
							InrConstants.NRC_CHARGES_ARE_NOT_NEEDED.equals(priceDetailsElement.path(InrConstants.NEXXUS_FALLOUT_REASON).asText())) {
						continue;
					}
					if (parentNode.has(InrConstants.NEXXUS_FALLOUT)) {
						((ObjectNode) priceDetailsElement).set(InrConstants.NEXXUS_FALLOUT, parentNode.get(InrConstants.NEXXUS_FALLOUT));
					}
					if (parentNode.has(InrConstants.NEXXUS_FALLOUT_REASON)) {
						((ObjectNode) priceDetailsElement).set(InrConstants.NEXXUS_FALLOUT_REASON, parentNode.get(InrConstants.NEXXUS_FALLOUT_REASON));
					}
				}
			} else {
				// if not at design level, dw has no data, create empty priceDetails
				JsonNode priceDetailsElement = mapper.createObjectNode();
				if (parentNode.has(InrConstants.NEXXUS_FALLOUT)) {
					((ObjectNode) priceDetailsElement).set(InrConstants.NEXXUS_FALLOUT, parentNode.get(InrConstants.NEXXUS_FALLOUT));
				}
				if (parentNode.has(InrConstants.NEXXUS_FALLOUT_REASON)) {
					((ObjectNode) priceDetailsElement).set(InrConstants.NEXXUS_FALLOUT_REASON, parentNode.get(InrConstants.NEXXUS_FALLOUT_REASON));
				}
				parentNode.withArray("priceDetails").add(priceDetailsElement);
			}
			return;
		 }
		 if("subProductName".equals(jsonBuildRule.getFieldName())) {
			 //to add subProductName for AIA
			 if (args.nodeMap.containsKey(jsonBuildRule.getDefaultValue())) {
					// if at design level, copy the subProductName
					JsonNode subProductNameNode = args.nodeMap.get(jsonBuildRule.getDefaultValue()).path("subProductName");
					if (!subProductNameNode.isNull() && !subProductNameNode.isMissingNode()) {
						parentNode.put("subProductName",subProductNameNode.asText());
					}
				}
				return;
		 }
		}

		if ("COPY_TO_DESIGN".equals(jsonBuildRule.getType())) {
			 if (args.nodeMap.containsKey(jsonBuildRule.getDefaultValue())) {
					// if at design level, copy the subProductName
					JsonNode node = args.nodeMap.get(jsonBuildRule.getDefaultValue()).path(jsonBuildRule.getFieldName());
					if (!node.isNull() && !node.isMissingNode()) {
						parentNode.put(jsonBuildRule.getFieldName(),node.asText());
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

	protected Object convertData(NxDwToJsonRules jsonBuildRule, Object data, Map<String, Object> usrpDesign,
			Args args) {
		if ("CUSTOM".equals(jsonBuildRule.getType())) {
			if (MyPriceConstants.AVPN.equalsIgnoreCase(args.product) || MyPriceConstants.ADI.equalsIgnoreCase(args.product)
					|| MyPriceConstants.ADIG.equalsIgnoreCase(args.product)) {
				if ("technology".equals(jsonBuildRule.getFieldName())) {
					String interconnectType = String.valueOf(convertData(args.jsonBuildRuleMap.get("interconnectType"),
							usrpDesign.get("interconnect_type"), usrpDesign, args));
					String accessMethodType = String.valueOf(convertData(args.jsonBuildRuleMap.get("accessMethodType"),
							usrpDesign.get("access_method_type"), usrpDesign, args));
					if (!stringHasValue(interconnectType) || !stringHasValue(accessMethodType)) {
						return null;
					}
					String key = interconnectType + "_" + accessMethodType;
					NxLookupData nxLookupData = nxMyPriceRepositoryServce.getLookupDataByItemId("USRP_TECHNOLOGY").get(key);
					if (nxLookupData != null) {
						return nxLookupData.getDescription();
					}
					return null;
				} else if ("AccessSpeed".equals(jsonBuildRule.getFieldName())) {
					String accessType = String.valueOf(convertData(args.jsonBuildRuleMap.get("accessType"),
							usrpDesign.get("access_type"), usrpDesign, args));
					String portSpeed = String.valueOf(usrpDesign.get("port_speed"));
					//here Ethernet,TDM or international should be compared against accessProductName and not access type
					String accessProductName=String.valueOf(convertData(args.jsonBuildRuleMap.get("accessProductName"),
							null, usrpDesign, args));
					if ("Ethernet".equals(accessProductName)) {
						//ethernet
						return usrpDesign.get("ethernet_access_bandwidth");
					} else if("TDM".equals(accessProductName)){
						String key = accessType + "_" + portSpeed;
						NxLookupData nxLookupData = nxMyPriceRepositoryServce.getLookupDataByItemId("USRP_ACCESS_SPEED").get(key);
						if (nxLookupData != null) {
							return nxLookupData.getDescription();
						}
						return null;
					}else if("International".equals(accessProductName)) {
						if(stringHasValue(String.valueOf(usrpDesign.get("ethernet_access_bandwidth")))){
							return String.valueOf(usrpDesign.get("ethernet_access_bandwidth"));
						}else {
							return portSpeed;
						}
					}
				} else if ("accessProductName".equals(jsonBuildRule.getFieldName())) {
					String accessType = String.valueOf(usrpDesign.get("access_type"));
					if (!stringHasValue(accessType)) {
						return null;
					}
					if ("33".equals(accessType)) {
						return "Ethernet";
					} else if ("8".equals(accessType)) {
						return "International";
					} else {
						return "TDM";
					}
				} else if ("AccessArchitecture".equals(jsonBuildRule.getFieldName())) {
					String interconnectType = String.valueOf(usrpDesign.get("interconnect_type"));
					String accessMethodType = String.valueOf(usrpDesign.get("access_method_type"));
					if (!stringHasValue(interconnectType) && !stringHasValue(accessMethodType)) {
						return null;
					}
					if (("1".equals(interconnectType) && "8".equals(accessMethodType)) || ("2".equals(interconnectType)
							&& ("6".equals(accessMethodType) || "7".equals(accessMethodType) || "8".equals(accessMethodType)
									|| "11".equals(accessMethodType)))) {
						return "SWITCHED";
					} else {
						return "DEDICATED";
					}
				}
			}
			if (MyPriceConstants.ADIG.equalsIgnoreCase(args.product)) {
				if ("accessTechnology".equals(jsonBuildRule.getFieldName())) {
					String interconnectType = String.valueOf(convertData(args.jsonBuildRuleMap.get("interconnectType"),
							usrpDesign.get("interconnect_type"), usrpDesign, args));
					String accessMethodType = String.valueOf(convertData(args.jsonBuildRuleMap.get("accessMethodType"),
							usrpDesign.get("access_method_type"), usrpDesign, args));
					if (!stringHasValue(interconnectType) || !stringHasValue(accessMethodType)) {
						return null;
					}
					String key = interconnectType + "_" + accessMethodType;
					NxLookupData nxLookupData = nxMyPriceRepositoryServce.getLookupDataByItemId("USRP_TECHNOLOGY").get(key);
					if (nxLookupData != null) {
						return nxLookupData.getDescription();
					}
					return null;
				}
			} else if (MyPriceConstants.ADI.equalsIgnoreCase(args.product)) {
				if ("cosSpeed".equals(jsonBuildRule.getFieldName())) {
					String cosInd = String.valueOf(usrpDesign.get("cos"));
					if ("Y".equalsIgnoreCase(cosInd)) {
						String portSpeed = String.valueOf(usrpDesign.get("port_speed"));
						if (stringHasValue(portSpeed)) {
							return portSpeed;
						}
					}
					return null;
				}
			}	
				 if(MyPriceConstants.ADI.equalsIgnoreCase(args.product) || MyPriceConstants.AVPN.equalsIgnoreCase(args.product) ) {
					if("swccli".equals(jsonBuildRule.getFieldName())) {
						String usrpswccli = String.valueOf(usrpDesign.get("swc_clli"));
						if (!stringHasValue(usrpswccli)) {
							usrpswccli=null;
						}
						 if (usrpswccli==null && !usrpDesign.containsKey(InrConstants.NEXXUS_FALLOUT)) {
							String usrpcity=stringHasValue(String.valueOf(usrpDesign.get("loc_city")))?String.valueOf(usrpDesign.get("loc_city")).trim():null;
							String usrpState=stringHasValue(String.valueOf(usrpDesign.get("loc_state")))?String.valueOf(usrpDesign.get("loc_state")).trim():null;
							String usrpPostalCode=stringHasValue(String.valueOf(usrpDesign.get("loc_zip")))?String.valueOf(usrpDesign.get("loc_zip")).trim():null;
							String usrpAddressLine=stringHasValue(String.valueOf(usrpDesign.get("loc_str1")))?String.valueOf(usrpDesign.get("loc_str1")).trim():null;
							String usrpPopcli=stringHasValue(String.valueOf(usrpDesign.get("clli")))?String.valueOf(usrpDesign.get("clli")).trim():null;
							String usrpVendorName = String.valueOf(convertData(args.jsonBuildRuleMap.get("accessCarrier"),
									usrpDesign.get("access_vendor"), usrpDesign, args));
							String usrpCountryCode = String.valueOf(usrpDesign.get("loc_country"));
							 SiteDetails siteDetails=new SiteDetails();
							 siteDetails.setCity(usrpcity);
							 siteDetails.setState(usrpState);
							 siteDetails.setPostalCode(usrpPostalCode);
							 siteDetails.setAddressLine(usrpAddressLine);
							 siteDetails.setCountry(usrpCountryCode);
							 return getSwccliValue(siteDetails, usrpPopcli, usrpVendorName,args);
						 }else {
							 return usrpswccli;
						 }
						 
					 }
			} else if (MyPriceConstants.EPLSWAN.equalsIgnoreCase(args.product)) {
				if ("swcclliAEnd".equals(jsonBuildRule.getFieldName())) {
					String usrpswccli = String.valueOf(usrpDesign.get("a_end_swc_clli"));
					if (!stringHasValue(usrpswccli)) {
						usrpswccli = null;
					}
					if (usrpswccli == null && !usrpDesign.containsKey(InrConstants.NEXXUS_FALLOUT)) {
						String usrpcity = stringHasValue(String.valueOf(usrpDesign.get("a_end_loc_city")))
								? String.valueOf(usrpDesign.get("a_end_loc_city")).trim()
								: null;
						String usrpState = stringHasValue(String.valueOf(usrpDesign.get("a_end_loc_state")))
								? String.valueOf(usrpDesign.get("a_end_loc_state")).trim()
								: null;
						String usrpPostalCode = stringHasValue(String.valueOf(usrpDesign.get("a_end_loc_zip")))
								? String.valueOf(usrpDesign.get("a_end_loc_zip")).trim()
								: null;
						String usrpAddressLine = stringHasValue(String.valueOf(usrpDesign.get("a_end_loc_str1")))
								? String.valueOf(usrpDesign.get("a_end_loc_str1")).trim()
								: null;
						String usrpPopcli = stringHasValue(String.valueOf(usrpDesign.get("a_end_pop_clli")))
								? String.valueOf(usrpDesign.get("a_end_pop_clli")).trim()
								: null;
						String usrpVendorName = String
								.valueOf(convertData(args.jsonBuildRuleMap.get("AccessCarrierAEnd"),
										usrpDesign.get("a_end_access_vendor"), usrpDesign, args));
						String usrpCountryCode = String.valueOf(usrpDesign.get("a_end_loc_country"));
						SiteDetails siteDetails = new SiteDetails();
						siteDetails.setCity(usrpcity);
						siteDetails.setState(usrpState);
						siteDetails.setPostalCode(usrpPostalCode);
						siteDetails.setAddressLine(usrpAddressLine);
						siteDetails.setCountry(usrpCountryCode);
						return getSwccliValue(siteDetails, usrpPopcli, usrpVendorName, args);
					} else {
						return usrpswccli;
					}

				}
				if ("swcclliZEnd".equals(jsonBuildRule.getFieldName())) {
					String usrpswccli = String.valueOf(usrpDesign.get("z_end_swc_clli"));
					if (!stringHasValue(usrpswccli)) {
						usrpswccli = null;
					}
					if (usrpswccli == null && !usrpDesign.containsKey(InrConstants.NEXXUS_FALLOUT)) {
						String usrpcity = stringHasValue(String.valueOf(usrpDesign.get("z_end_loc_city")))
								? String.valueOf(usrpDesign.get("z_end_loc_city")).trim()
								: null;
						String usrpState = stringHasValue(String.valueOf(usrpDesign.get("z_end_loc_state")))
								? String.valueOf(usrpDesign.get("z_end_loc_state")).trim()
								: null;
						String usrpPostalCode = stringHasValue(String.valueOf(usrpDesign.get("z_end_loc_zip")))
								? String.valueOf(usrpDesign.get("z_end_loc_zip")).trim()
								: null;
						String usrpAddressLine = stringHasValue(String.valueOf(usrpDesign.get("z_end_loc_str1")))
								? String.valueOf(usrpDesign.get("z_end_loc_str1")).trim()
								: null;
						String usrpPopcli = stringHasValue(String.valueOf(usrpDesign.get("z_end_pop_clli")))
								? String.valueOf(usrpDesign.get("z_end_pop_clli")).trim()
								: null;
						String usrpVendorName = String
								.valueOf(convertData(args.jsonBuildRuleMap.get("AccessCarrierZEnd"),
										usrpDesign.get("z_end_access_vendor"), usrpDesign, args));
						String usrpCountryCode = String.valueOf(usrpDesign.get("z_end_loc_country"));
						SiteDetails siteDetails = new SiteDetails();
						siteDetails.setCity(usrpcity);
						siteDetails.setState(usrpState);
						siteDetails.setPostalCode(usrpPostalCode);
						siteDetails.setAddressLine(usrpAddressLine);
						siteDetails.setCountry(usrpCountryCode);
						return getSwccliValue(siteDetails, usrpPopcli, usrpVendorName, args);
					} else {
						return usrpswccli;
					}
				}
			}
			return null;
		}	
		
		if (jsonBuildRule.getDefaultValue() != null) {
			return jsonBuildRule.getDefaultValue();
		}
		if (!stringHasValue(String.valueOf(data))) {
			return null;
		}
		if (jsonBuildRule.getType() != null && jsonBuildRule.getType().contains("TRIM_CIRCUITID")) {
			return String.valueOf(data).replaceAll("\\s", "").replaceAll("\\.", "");
		}
		if (jsonBuildRule.getType() != null && jsonBuildRule.getType().contains("TRIM_NPANXX")) {
			return String.valueOf(data).replaceAll("\\-", "");
		}
		if (jsonBuildRule.getLookupDatasetName() == null) {
			return data;
		}
		if (jsonBuildRule.getLookupDatasetName() != null) {
			NxLookupData nxLookupData = nxMyPriceRepositoryServce
					.getLookupDataByItemIdIgnoreKeyCase(jsonBuildRule.getLookupDatasetName()).get(String.valueOf(data));
			if (nxLookupData != null) {
				return nxLookupData.getDescription();
			} 
		}
		return data;
	}
	
	protected boolean stringHasValue(String in) {
		if (in == null) {
			return false;
		}
		if (in.isEmpty()) {
			return false;
		}
		if ("null".equals(in)) {
			return false;
		}
		return true;
	}

	protected boolean isjsonBuildRuleForValueNode(NxDwToJsonRules jsonBuildRule) {
		return !jsonBuildRule.getFieldType().equals("list") && !jsonBuildRule.getFieldType().equals("object");
	}

	protected Map<String, Object> getUsrpDesign(JsonNode node, Args args) {
		String circuitId = trimSpaceAndDot(node.path("circuitId").asText());
		circuitId = circuitId.isEmpty() ? null : circuitId;
		String portNumber = node.path("portNumber").asText();
		portNumber = portNumber.isEmpty() ? null : portNumber;
		Map<String, Map<String, Object>> lookup = args.usrpDesignLookup.get(args.mcn);
		if (lookup != null && lookup.containsKey(circuitId)) {//null pointer eception
			Map<String, Object> res = lookup.get(circuitId);
			args.usrpDesigns.remove(res);
			return res;
		}
		if (lookup != null && lookup.containsKey(portNumber)) {
			Map<String, Object> res = lookup.get(portNumber);
			args.usrpDesigns.remove(res);
			return res;
		}
		// if not found by query mcn, query by circuitid or portNumber
		String[] queryParams = args.queryParams.split(",");
		String sql = args.queryString;
		Object[] queryParamValues = new Object[queryParams.length];
		Object[] passArgs = new Object[findNumberOfPlaceholder(sql)];
		for (int i = 0, j = 0; i < queryParams.length; i++) {
			queryParamValues[i] = getQueryParam(queryParams[i], args);
			passArgs[j++] = queryParamValues[i];
			// query placeholder cls_serial always follows circuitid
			if (queryParams[i].contains("circuitId")) {
				passArgs[j++] = getClsSerial(String.valueOf(queryParamValues[i]));
			}
		}
		logger.info("secondary query as mcn search is either not happened or mcn search result is not found"+circuitId+","+portNumber);
		long startTime = System.currentTimeMillis();
		List<Map<String, Object>> usrpDesigns = usrpDao.query(sql, passArgs);
		long endTime = System.currentTimeMillis();
		LogUtils.logExecutionDurationMs(logger, endTime - startTime, 
				String.format("secondary query usrp for nx req id %d", args.nxReqId));
		args.usrpQueryTime += endTime - startTime;
		if (!usrpDesigns.isEmpty()) {
			Map<String, Object> row = usrpDesigns.get(0);
			if (MyPriceConstants.EPLSWAN.equals(args.product)) {
				row = usrpDesigns.stream().collect(Collectors.maxBy(UsrpDao.EPLSWAN_CMP)).get();
			}
			if (eplswanUsrpRowFilterOut(row, args)) {
				row.put(InrConstants.NEXXUS_FALLOUT, "Y");
				row.put(InrConstants.NEXXUS_FALLOUT_REASON, NOT_EPLSWAN_BITRATE);
			} else if ("Y".equalsIgnoreCase(String.valueOf(row.get("disc_ind")))) {
				row.put(InrConstants.NEXXUS_FALLOUT, "Y");
				row.put(InrConstants.NEXXUS_FALLOUT_REASON, CIRCUIT_DISCONNECTED);
			}
			return row;
		}
		//fallout case
		Map<String, Object> fallout = new HashMap<>();
		fallout.put(InrConstants.NEXXUS_FALLOUT, "Y");
		fallout.put(InrConstants.NEXXUS_FALLOUT_REASON, ORDERING_DETAILS_MISSING);
		fallout.put("siteid", InrConstants.NEXXUS_FALLOUT);
		fallout.put("circuitid", circuitId);
		fallout.put("icore_site_id", portNumber);
		// xy3208 adi does not have icore_site_id, but use the same flow as avpn
		return fallout;
	}
	
	protected int findNumberOfPlaceholder(String sql) {
		int res = 0;
		for (int i = 0; i < sql.length(); i++) {
			if (sql.charAt(i) == '?') {
				res++;
			}
		}
		return res;
	}
	
	//circuitId: .DHEC.963824..ATI.
	//ClsSerial is between 2nd dot and 3rd dot
	protected String getClsSerial(String circuitId) {
		int start = circuitId.indexOf(".", 1);
		int end = circuitId.indexOf(".", start + 1);
		if (start > -1 && end > -1) {
			return circuitId.substring(start + 1, end);
		}
		return "";
	}
	
	protected String getQueryParam(String path, Args args) {
		JsonPath jsonPath = new JsonPath(path);
		String res = args.nodeMap.get(jsonPath.parent().toString()).path(jsonPath.getFieldName()).asText();
		if (path.toLowerCase().contains("circuitid")) {
			res = res.replaceAll("\\s", "");
			if (res.endsWith("ATI") || res.endsWith("ACR")) {
				if (res.length() == 13) {
					res = String.format(".%s.%s..%s.", res.substring(0, 4), res.substring(4, 10),
							res.substring(10, 13));
				} else if (res.length() == 16) {
					res = String.format(".%s.%s.%s.%s.", res.substring(0, 4), res.substring(4, 10),
							res.substring(10, 13), res.subSequence(13, 16));
				} else if (res.length() == 12) {
					res = String.format(".%s.%s..%s.", res.substring(0, 4), res.substring(4, 9), res.substring(9, 12));
				} 
				if (!MyPriceConstants.AVPN.equalsIgnoreCase(args.product)) {
					res = res.substring(1, res.length() - 1);
				}
			}
		}
		return res;
	}

	protected void queryUsrp(JsonNode node, JsonPath path, Args args) {
		if (args.queryPath.equals(path.toString())) {
			String mcn = node.path("MCN").asText();
			args.mcn = mcn;
			if (!args.usrpDesignLookup.containsKey(mcn)) {
				if(!node.path("isSkipAccessMCN").isMissingNode()
						&& !node.path("isSkipAccessMCN").isNull() &&
						("Y").equalsIgnoreCase(node.path("isSkipAccessMCN").asText())){				
					return;
					}			
				else
            	{
            	long startTime = System.currentTimeMillis();
				List<Map<String, Object>> usrpDesigns = usrpDao.queryWithSize(mcn, args.viewName, args.product);
				long endTime = System.currentTimeMillis();
				LogUtils.logExecutionDurationMs(logger, endTime - startTime, 
						String.format("first query usrp on mcn for nx req id %d", args.nxReqId));
				args.usrpQueryTime += endTime - startTime;
				updatingUsrpResultsToArgs(usrpDesigns, args);
			}
		}
		}
	}
	
	
	protected boolean eplswanUsrpRowFilterOut(Map<String, Object> row, Args args) {
		if (MyPriceConstants.EPLSWAN.equals(args.product)) {
			String bitrate = String.valueOf(row.get("bitrate"));
			Map<String, NxLookupData> whiteList = nxMyPriceRepositoryServce
					.getLookupDataByItemId("USRP_EPLSWAN_DISPLAYSPEED");
			if (!whiteList.keySet().contains(bitrate)) {
				return true;
			}
		}
		return false;
	}

	protected void updatingUsrpResultsToArgs(List<Map<String, Object>> usrpDesigns, Args args) {
		Map<String, Map<String, Object>> lookup = new HashMap<>();
		args.usrpDesignLookup.put(args.mcn, lookup);
		usrpDesigns.forEach(row -> {
			if (eplswanUsrpRowFilterOut(row, args)) {
				row.put(InrConstants.NEXXUS_FALLOUT, "Y");
				row.put(InrConstants.NEXXUS_FALLOUT_REASON, NOT_EPLSWAN_BITRATE);
			} else if ("Y".equalsIgnoreCase(String.valueOf(row.get("disc_ind")))) {
				row.put(InrConstants.NEXXUS_FALLOUT, "Y");
				row.put(InrConstants.NEXXUS_FALLOUT_REASON, CIRCUIT_DISCONNECTED);
			}
			args.usrpDesigns.add(row);
			String circuitId = null;
			String portNumber = null;
			if (MyPriceConstants.AVPN.equals(args.product)) {
				circuitId = trimSpaceAndDot(String.valueOf(row.get("circuitid")));
				portNumber = String.valueOf(row.get("icore_site_id"));
			} else if (MyPriceConstants.ADI.equals(args.product)) {
				circuitId = trimSpaceAndDot(String.valueOf(row.get("circuitid")));
			} else if (MyPriceConstants.ADIG.equals(args.product)) {
				portNumber = String.valueOf(row.get("piid"));
			} else if (MyPriceConstants.EPLSWAN.equals(args.product)) {
				circuitId = trimSpaceAndDot(String.valueOf(row.get("circuitid")));
			}
			if (stringHasValue(circuitId)) {
				lookup.put(circuitId, row);
			}
			if (stringHasValue(portNumber)) {
				lookup.put(portNumber, row);
			}
			// ADI, adding mapping from portNumber to USRP row
			if (MyPriceConstants.ADI.equals(args.product)) {
				portNumber = String.valueOf(row.get("billing_sap_id"));
				if (stringHasValue(portNumber)) {
					lookup.put(portNumber, row);
				}
				portNumber = String.valueOf(row.get("ipd_serv_acc_pt_id"));
				if (stringHasValue(portNumber)) {
					lookup.put(portNumber, row);
				}
				portNumber = String.valueOf(row.get("ml_ipd_serv_acc_pt_id"));
				if (stringHasValue(portNumber)) {
					lookup.put(portNumber, row);
				}
			}
		});
	}

	protected void updateNodeMapIdentifierKey(JsonNode node, JsonPath path, Args args) {
		if (args.jsonBuildRuleMap.containsKey(path.getFieldName())) {
			NxDwToJsonRules jsonBuildRule = args.jsonBuildRuleMap.get(path.getFieldName());
			if (jsonBuildRule.getIdentifierKey() != null && jsonBuildRule.getIdentifierKey().startsWith("/")) {
				String identifierKey = getIdentifierKey(jsonBuildRule, args, null);
				args.nodeMap.put(identifierKey, node);
			}
		}
	}
	
	protected String getIdentifierKey(NxDwToJsonRules jsonBuildRule, Args args, Map<String, Object> usrpDesignRow) {
		StringBuilder sb = new StringBuilder();
		getIdentifierKeyHelper(sb, jsonBuildRule, args, usrpDesignRow);
		return sb.toString();
	}
	
	protected void getIdentifierKeyHelper(StringBuilder sb, NxDwToJsonRules jsonBuildRule, Args args, Map<String, Object> usrpDesignRow) {
		if (jsonBuildRule == null) {
			return;
		}
		getIdentifierKeyHelper(sb, args.jsonBuildRuleMap.get(jsonBuildRule.getFieldParent()), args,
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
				String value = args.nodeMap.get(jsonPath.parent().toString()).path(jsonPath.getFieldName()).asText();
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
	
	protected String trimSpaceAndDot(String in) {
		if (in == null) {
			return null;
		}
		return in.replaceAll("\\s", "").replaceAll("\\.", "");
	}
	
	protected static class Args {
		Map<String, NxDwToJsonRules> jsonBuildRuleMap;
		String product;
		String queryPath;
		String attachUsrpDataPath;
		String rootTagName;
		String queryParams;
		String queryString;
		String viewName;
		String mcn;
		String skipAttachingDesignData;
		Map<String, JsonNode> nodeMap = new HashMap<>();
		Set<Map<String, Object>> usrpDesigns = new HashSet<>();
		Map<String, Map<String, Map<String, Object>>> usrpDesignLookup = new HashMap<>();
		long usrpQueryTime = 0L;
		long avsqCallTime = 0L;
		Map<String, Object> paramMap;
		String searchCriteria;
		Long nxReqId;
	}
	
	protected String getSwccliValue(SiteDetails siteDetails,String popcli,String vendorname,Args args){
		logger.info("getSwccliValue site country code is {}", siteDetails.getCountry());
		String result = null;
		if((!stringHasValue(siteDetails.getAddressLine()) && !stringHasValue(siteDetails.getCity()) && !stringHasValue(siteDetails.getState()) &&
				!stringHasValue(siteDetails.getPostalCode())) || !"1".equals(siteDetails.getCountry()))  { // skip avsq for non us country
			result=null;
		}else {
			siteDetails.setCountry(null);
			long startTime = System.currentTimeMillis();
			result = avsqUtil.getSwccliFromAVSQ(siteDetails,args.paramMap);
			long endTime = System.currentTimeMillis();
			args.avsqCallTime += endTime - startTime;

		}
		if (result == null && stringHasValue(popcli) && stringHasValue(vendorname)) {
			List<MpPopVendorMapping> mpPopVendorMappingList=mpPopVendorMappingRepository.findByPopclliAndVendorName(popcli, vendorname);
			if(CollectionUtils.isNotEmpty(mpPopVendorMappingList)) {
				String swccli=mpPopVendorMappingList.get(0).getSwccli();
				return swccli;
	
			}

		}
		return result;
	}

}
