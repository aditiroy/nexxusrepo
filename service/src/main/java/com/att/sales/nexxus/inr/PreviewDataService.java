package com.att.sales.nexxus.inr;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.constant.CommonConstants;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;


/*
 * CDIR data format
{
    "rootTag": ["tag1",
        "tag2"],
    "header": [{
            "key": "value",
            "key1": "value1"
        }
    ],
    "mainSheet": [{
            "key": "value",
            "key1": "value1"
        }
    ],
    "falloutSheet": [{
            "key": "value",
            "key1": "value1"
        }
    ]
}
 */
@Component
public class PreviewDataService {
	private static Logger logger = LoggerFactory.getLogger(PreviewDataService.class);

	@Autowired
	private InrFactory inrFactory;

	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Autowired
	private NxDesignAuditRepository nxDesignAuditRepository;
	
	@Autowired
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	@Autowired
	private ObjectMapper mapper;

	private List<String> idPaths;
	private DateTimeFormatter dtfMonthNum = DateTimeFormatter.ofPattern("MMyyyy");
	private DateTimeFormatter dtfFullMonthName = DateTimeFormatter.ofPattern("MMMM yyyy");

	public JsonNode generateCdirData(NxOutputFileModel nxOutputFileModel) throws IOException {
		long startTime = System.currentTimeMillis();
		long st = startTime;
		long et = startTime;
		ObjectNode res = mapper.createObjectNode();
		Set<String> rootSet = new HashSet<>();
		long nexxusRequestId = nxOutputFileModel.getNxRequestDetails().getNxReqId();
		long nexxusSolutionId = nxOutputFileModel.getNxRequestDetails().getNxSolutionDetail().getNxSolutionId();
		JsonNode manageBillingPriceJsonNode = mapper.createObjectNode();
		if (nxOutputFileModel.getNxRequestDetails().getManageBillingPriceJson() != null) {
			manageBillingPriceJsonNode = mapper.readTree(nxOutputFileModel.getNxRequestDetails().getManageBillingPriceJson());
		}
		ObjectNode excelHeaderNode = createExcelHeaderNode(manageBillingPriceJsonNode, nexxusSolutionId, nexxusRequestId);
		if (nxOutputFileModel.getInventoryJson() != null) {
			JsonNode inventoryJson = mapper.readTree(nxOutputFileModel.getInventoryJson());
			String rootName = inventoryJson.fieldNames().next();
			processInventory(nxOutputFileModel, res, rootSet, excelHeaderNode);
			et = System.currentTimeMillis();
			logger.info("time used to flatten inventory json in ms: {}", et - st);
			st = et;
			if (!(rootName != null && rootName.endsWith("Usrp")) && nxOutputFileModel.getFallOutData() != null) {
				processFalloutXlsm(nxOutputFileModel, res, rootSet);
			}
			et = System.currentTimeMillis();
			logger.info("time used to adding fallout data in ms: {}", et - st);
			st = et;
		}
		for (String rootTag : rootSet) {
			res.withArray("rootTag").add(rootTag);
		}
		et = System.currentTimeMillis();
		logger.info("total time to get cdir data ready for nx_output_file id {} in ms: {}", nxOutputFileModel.getId(), et - startTime);
		return res;
	}
	
	public JsonNode updateAuditCheck(NxOutputFileModel nxOutputFileModel) throws IOException {
		long st = System.currentTimeMillis();
		long et = st;
		ObjectNode res = (ObjectNode) mapper.readTree(nxOutputFileModel.getCdirData());
		JsonNode inventoryJson = mapper.readTree(nxOutputFileModel.getInventoryJson());
		String rootName = inventoryJson.fieldNames().next();
		Map<String, List<String>> falloutMessageMap = falloutMessageMap(nxOutputFileModel.getNxRequestDetails());
		if (rootName != null && rootName.endsWith("Usrp")) {
			JsonNode tree = mapper.readTree(nxOutputFileModel.getMpOutputJson());
			if (!falloutMessageMap.isEmpty()) {
				addAuditCheckToTree(falloutMessageMap, tree);
			}
			ObjectNode treeView = mapper.createObjectNode();
			treeView.put("rootTag", rootName);
			if ("avtsUsrp".equals(rootName) || "aniraUsrp".equals(rootName)) {
				treeView.put("excelWritePath", String.format("/%s/accountDetails/design/priceDetails", rootName));
			}else if("bvoipnonusageUsrp".equals(rootName)) {
				treeView.put("excelWritePath", String.format("/%s/accountDetails/site/featureDetails", rootName));
			}else if("bvoipUsrp".equals(rootName)) {
				treeView.put("excelWritePath", String.format("/%s/accountDetails/site", rootName));
			}else if("abnldvoiceUsrp".equals(rootName)) {
				treeView.put("excelWritePath", String.format("/%s/subAccountFeaturesUsage/usageDetails", rootName));
			}else if("vtnsldvoiceusageUsrp".equals(rootName) || "sdnonenetldvoiceusageUsrp".equals(rootName)) {
				treeView.put("excelWritePath", String.format("/%s/accountDetails/usageDetails", rootName));
			} else if ("vtnsldvoicefeaturesUsrp".equals(rootName) || "sdnonenetldvoicefeaturesUsrp".equals(rootName)) {
				treeView.put("excelWritePath", String.format("/%s/accountDetails/featuresDetails", rootName));
			}else if ("eplswanUsrp".equals(rootName)) {
				treeView.put("excelWritePath", String.format("/%s/accountDetails/design", rootName));
			}
			else {
				treeView.put("excelWritePath", String.format("/%s/accountDetails/site/design/priceDetails", rootName));
			}
			treeView.set("data", tree);
			res.set("treeView", treeView);
		} else {
			Set<String> rootSet = new HashSet<>();
			Set<String> rootSetBefore = new HashSet<>();
			JsonNode rootTagArray = res.path("rootTag");
			for (JsonNode n : rootTagArray) {
				rootSet.add(n.asText());
				rootSetBefore.add(n.asText());
			}
			rootSet = addAuditCheck(falloutMessageMap, res, rootSet);
			for (String rootTag : rootSet) {
				if (!rootSetBefore.contains(rootTag)) {
					res.withArray("rootTag").add(rootTag);
				}
			}
		}
		logger.info("time used to adding audit data in ms: {}", et - st);
		return res;
	}
	
	protected void addAuditCheckToTree(Map<String, List<String>> falloutMessageMap, JsonNode node) {
		if (node.getNodeType() == JsonNodeType.ARRAY) {
			for (JsonNode element : node) {
				addAuditCheckToTree(falloutMessageMap, element);
			}
		} else if (node.getNodeType() == JsonNodeType.OBJECT) {
			if (node.hasNonNull("circuitId")) {
				String id = node.path("circuitId").asText().replaceAll("\\W", "");
				if (falloutMessageMap.containsKey(id)) {
					String falloutReason = String.join(" ", falloutMessageMap.get(id));
					JsonNode priceDetailsArray = node.path("priceDetails");
					for (JsonNode priceDetails : priceDetailsArray) {
						String existingFalloutReason = priceDetails.path(InrConstants.NEXXUS_FALLOUT_REASON).asText();
						String newFalloutReason = existingFalloutReason.isEmpty() ? falloutReason
								: existingFalloutReason + " and " + falloutReason;
						((ObjectNode) priceDetails).put(InrConstants.NEXXUS_FALLOUT_REASON, newFalloutReason);
					}
				}
				//return;
			}
			if (node.hasNonNull("FALLOUTMATCHINGID")) {
				String FALLOUTMATCHINGID = node.path("FALLOUTMATCHINGID").asText();
				if (falloutMessageMap.containsKey(FALLOUTMATCHINGID)) {
					String falloutReason = String.join(" ", falloutMessageMap.get(FALLOUTMATCHINGID));
					String existingFalloutReason = node.path(InrConstants.NEXXUS_FALLOUT_REASON).asText();
					String newFalloutReason = existingFalloutReason.isEmpty() ? falloutReason
							: existingFalloutReason + " and " + falloutReason;
					((ObjectNode) node).put(InrConstants.NEXXUS_FALLOUT_REASON, newFalloutReason);
				}
			}
			if (node.hasNonNull("NXSITEMATCHINGID")) {
				String NXSITEMATCHINGID = node.path("NXSITEMATCHINGID").asText();
				if (falloutMessageMap.containsKey(NXSITEMATCHINGID)) {
					String falloutReason = String.join(" ", falloutMessageMap.get(NXSITEMATCHINGID));
					String existingFalloutReason = node.path(InrConstants.NEXXUS_FALLOUT_REASON).asText();
					String newFalloutReason = existingFalloutReason.isEmpty() ? falloutReason
							: existingFalloutReason + " and " + falloutReason;
					((ObjectNode) node).put(InrConstants.NEXXUS_FALLOUT_REASON, newFalloutReason);
				}
			}

			for (Iterator<Entry<String, JsonNode>> i = node.fields(); i.hasNext();) {
				JsonNode child = i.next().getValue();
				addAuditCheckToTree(falloutMessageMap, child);
			}
		}
	}

	protected void processFalloutXlsm(NxOutputFileModel nxOutputFileModel, ObjectNode res, Set<String> rootSet)
			throws IOException, JsonParseException, JsonMappingException {
		InrFallOutData fallout = mapper.readValue(nxOutputFileModel.getFallOutData(), InrFallOutData.class);
		List<String> rootSetList = new ArrayList<>();
		String r = null;
		String r1 = null;
		for (Iterator<String> itr = rootSet.iterator(); itr.hasNext();) {
			String next = itr.next();
			if (!next.startsWith("fo")) {
				if (next.endsWith("1")) {
					r1 = next;
				} else {
					r = next;
				} 
			}
		}
		rootSetList.add(r);
		if (r1 != null) {
			rootSetList.add(r1);
		}
		if (fallout.getInventoryJsonLookups() != null) {
			List<InventoryJsonLookup> inventoryJsonLookups = fallout.getInventoryJsonLookups();
			for (Iterator<InventoryJsonLookup> itr = inventoryJsonLookups.iterator(); itr.hasNext(); ) {
				InventoryJsonLookup lookup = itr.next();
				addNewFalloutRow(res, rootSetList, lookup, OutputJsonGenerator.LINEITEM_NOT_FOUND_DESC, rootSet);
			}
		}
	}

//	protected void processFallout(NxOutputFileModel nxOutputFileModel, ObjectNode res, Set<String> rootSet)
//			throws IOException, JsonParseException, JsonMappingException {
//		InrFallOutData fallout = mapper.readValue(nxOutputFileModel.getFallOutData(), InrFallOutData.class);
//		if (fallout.getInventoryJsonLookups() != null) {
//			List<InventoryJsonLookup> inventoryJsonLookups = fallout.getInventoryJsonLookups();
//			JsonNode mainSheet = res.path("mainSheet");
//			JsonNode pilotNode = mainSheet.get(0);
//			if (pilotNode.has(InrIntermediateJsonGenerator.FALLOUTMATCHINGID)) {
//				Map<String, List<InventoryJsonLookup>> lookMap = new HashMap<>();
//				for (InventoryJsonLookup lookup : inventoryJsonLookups) {
//					Map<String, String> longForm = lookup.getLongForm();
//					for (Entry<String, String> entry : longForm.entrySet()) {
//						if (entry.getKey().endsWith(InrIntermediateJsonGenerator.FALLOUTMATCHINGID)) {
//							String key = entry.getValue();
//							if (!lookMap.containsKey(key)) {
//								lookMap.put(key, new LinkedList<>());
//							}
//							lookMap.get(key).add(lookup);
//							break;
//						}
//					}
//				}
//				for (JsonNode n : mainSheet) {
//					String key = n.path(InrIntermediateJsonGenerator.FALLOUTMATCHINGID).asText();
//					if (lookMap.containsKey(key)) {
//						List<InventoryJsonLookup> lookups = lookMap.get(key);
//						if (lookups.isEmpty()) {
//							lookMap.remove(key);
//						}
//						for (Iterator<InventoryJsonLookup> itr = lookups.iterator(); itr.hasNext(); ) {
//							InventoryJsonLookup lookup = itr.next();
//							if (isFalloutMatch(n, lookup)) {
//								addNewFalloutRow(res, n, lookup, "Unable to find a line item for your data. Please contact IT team to fix the issue.", rootSet);
//								itr.remove();
//							}
//						}
//					}
//				}
//			} else {
//				for (JsonNode n : mainSheet) {
//					for (Iterator<InventoryJsonLookup> itr = inventoryJsonLookups.iterator(); itr.hasNext(); ) {
//						InventoryJsonLookup lookup = itr.next();
//						if (isFalloutMatch(n, lookup)) {
//							addNewFalloutRow(res, n, lookup, "Unable to find a line item for your data. Please contact IT team to fix the issue.", rootSet);
//						}
//					}
//				}
//			}
//		}
//	}

	protected void processInventory(NxOutputFileModel nxOutputFileModel, ObjectNode res, Set<String> rootSet,
			ObjectNode excelHeaderNode) throws IOException {
		JsonNode inventoryJson = mapper.readTree(nxOutputFileModel.getInventoryJson());
		String rootName = inventoryJson.fieldNames().next();
		if ("InrDomCktResponse".equals(rootName)) {
			ObjectNode bodyNode = (ObjectNode) inventoryJson.at("/InrDomCktResponse/Body");
			if (!bodyNode.has("DomesticEthernetIOCInventory") && bodyNode.has("DomesticIOCInventory")) {
				addSingleSheet(res, rootSet, excelHeaderNode, inventoryJson, rootName + "1");
			} else if (bodyNode.has("DomesticEthernetIOCInventory") && bodyNode.has("DomesticIOCInventory")) {
				JsonNode inventoryJson1 = inventoryJson.deepCopy();
				ObjectNode excelHeaderNode1 = excelHeaderNode.deepCopy();
				bodyNode.remove("DomesticIOCInventory");
				InrInventoryJsonFlatten inrInventoryJsonFlatten = inrFactory.getInrInventoryJsonFlatten(inventoryJson, rootName);
				ArrayNode flattened = inrInventoryJsonFlatten.generate();
				rootSet.add(rootName);
				excelHeaderNode.put(InrConstants.FLATTEN_ROOT_TAG, rootName);
				res.withArray("header").add(excelHeaderNode);
				
				ObjectNode bodyNode1 = (ObjectNode) inventoryJson1.at("/InrDomCktResponse/Body");
				bodyNode1.remove("DomesticEthernetIOCInventory");
				InrInventoryJsonFlatten inrInventoryJsonFlatten1 = inrFactory.getInrInventoryJsonFlatten(inventoryJson1, rootName + "1");
				ArrayNode flattened1 = inrInventoryJsonFlatten1.generate();
				rootSet.add(rootName + "1");
				excelHeaderNode1.put(InrConstants.FLATTEN_ROOT_TAG, rootName + "1");
				res.withArray("header").add(excelHeaderNode1);
				
				ArrayNode combined = mapper.createArrayNode();
				for (JsonNode n : flattened) {
					combined.add(n);
				}
				for (JsonNode n : flattened1) {
					combined.add(n);
				}
				res.set("mainSheet", combined);
			} else {
				addSingleSheet(res, rootSet, excelHeaderNode, inventoryJson, rootName);
			}
		} else if ("DDAResponse".equals(rootName)) {
			ObjectNode bodyNode = (ObjectNode) inventoryJson.at("/DDAResponse/Body");
			ArrayNode combined = mapper.createArrayNode();
			if (bodyNode.has("DomesticEthernetAccessInventory")) {
				JsonNode inventoryJson1 = inventoryJson.deepCopy();
				ObjectNode excelHeaderNode1 = excelHeaderNode.deepCopy();
				ObjectNode bodyNode1 = (ObjectNode) inventoryJson1.at("/DDAResponse/Body");
				bodyNode1.remove("DomesticDSODS1AccessInventory");
				bodyNode1.remove("DomesticDS3OCXAccessInventory");
				InrInventoryJsonFlatten inrInventoryJsonFlatten1 = inrFactory.getInrInventoryJsonFlatten(inventoryJson1, rootName);
				ArrayNode flattened1 = inrInventoryJsonFlatten1.generate();
				rootSet.add(rootName);
				excelHeaderNode1.put(InrConstants.FLATTEN_ROOT_TAG, rootName);
				res.withArray("header").add(excelHeaderNode1);
				for (JsonNode n : flattened1) {
					combined.add(n);
				}
			}
			int ds0ds1Size = 0;
			if (bodyNode.has("DomesticDSODS1AccessInventory")) {
				JsonNode inventoryJson1 = inventoryJson.deepCopy();
				ObjectNode excelHeaderNode1 = excelHeaderNode.deepCopy();
				ObjectNode bodyNode1 = (ObjectNode) inventoryJson1.at("/DDAResponse/Body");
				bodyNode1.remove("DomesticEthernetAccessInventory");
				bodyNode1.remove("DomesticDS3OCXAccessInventory");
				String rootName1 = rootName + "1";
				InrInventoryJsonFlatten inrInventoryJsonFlatten1 = inrFactory.getInrInventoryJsonFlatten(inventoryJson1, rootName1);
				ArrayNode flattened1 = inrInventoryJsonFlatten1.generate();
				ds0ds1Size = flattened1.size();
				rootSet.add(rootName1);
				excelHeaderNode1.put(InrConstants.FLATTEN_ROOT_TAG, rootName1);
				res.withArray("header").add(excelHeaderNode1);
				for (JsonNode n : flattened1) {
					combined.add(n);
				}
			}
			if (bodyNode.has("DomesticDS3OCXAccessInventory")) {
				JsonNode inventoryJson1 = inventoryJson.deepCopy();
				ObjectNode excelHeaderNode1 = excelHeaderNode.deepCopy();
				ObjectNode bodyNode1 = (ObjectNode) inventoryJson1.at("/DDAResponse/Body");
				bodyNode1.remove("DomesticEthernetAccessInventory");
				bodyNode1.remove("DomesticDSODS1AccessInventory");
				String rootName1 = rootName + "1";
				InrInventoryJsonFlatten inrInventoryJsonFlatten1 = inrFactory.getInrInventoryJsonFlatten(inventoryJson1, rootName1);
				ArrayNode flattened1 = inrInventoryJsonFlatten1.generate();
				rootSet.add(rootName1);
				excelHeaderNode1.put(InrConstants.FLATTEN_ROOT_TAG, rootName1);
				res.withArray("header").add(excelHeaderNode1);
				for (JsonNode n : flattened1) {
					if (ds0ds1Size > 0) {
						ObjectNode nObj = (ObjectNode) n;
						nObj.put("sequence", nObj.get("sequence").asInt() + ds0ds1Size);
					}
					combined.add(n);
				}
			}
			res.set("mainSheet", combined);
		} else {
			addSingleSheet(res, rootSet, excelHeaderNode, inventoryJson, rootName);
		}
	}

	protected void addSingleSheet(ObjectNode res, Set<String> rootSet, ObjectNode excelHeaderNode, JsonNode inventoryJson,
			String rootName) {
		if (!(rootName != null && rootName.endsWith("Usrp"))) {
			InrInventoryJsonFlatten inrInventoryJsonFlatten = inrFactory.getInrInventoryJsonFlatten(inventoryJson,
					rootName);
			ArrayNode flattened = inrInventoryJsonFlatten.generate();
			res.set("mainSheet", flattened);
		}
		rootSet.add(rootName);
		excelHeaderNode.put(InrConstants.FLATTEN_ROOT_TAG, rootName);
		res.withArray("header").add(excelHeaderNode);
	}
	
	protected ObjectNode createExcelHeaderNode(JsonNode manageBillingPriceJsonNode, long nxSolutionId, long nxRequestId) {
		ObjectNode res = mapper.createObjectNode();
		res.put("nexxusSolutionId", nxSolutionId);
		res.put("nexxusRequestId", nxRequestId);
		res.put("customerName", manageBillingPriceJsonNode.path("customerName").asText());
		String searchCriteria = manageBillingPriceJsonNode.path("searchCriteria").asText();
		res.put("searchCriteria", searchCriteria);
		res.put("searchValue", manageBillingPriceJsonNode.path("searchValue").asText());
		if ("duns".equalsIgnoreCase(searchCriteria)) {
			res.put("searchValue", manageBillingPriceJsonNode.path("dunsNumber").asText());
		} else if ("mcn".equalsIgnoreCase(searchCriteria)) {
			res.put("searchValue", manageBillingPriceJsonNode.path("mcn").asText());
		} else if ("svid".equalsIgnoreCase(searchCriteria)) {
			res.put("searchValue", manageBillingPriceJsonNode.path("svid").asText());
		}else if ("L3".equalsIgnoreCase(searchCriteria)) {
			res.put("searchValue", manageBillingPriceJsonNode.path("l3SubAcctId").asText());
		} else if ("MAN".equalsIgnoreCase(searchCriteria)) {
			res.put("searchValue", manageBillingPriceJsonNode.path("mainAcctNumber").asText());
		} else if ("MainAccountNumber".equalsIgnoreCase(searchCriteria)) {
			res.put("searchValue", manageBillingPriceJsonNode.path("mainAccountNumber").asText());
		} else if ("L5".equalsIgnoreCase(searchCriteria)) {
			res.put("searchValue", manageBillingPriceJsonNode.path("l5MasterAcctId").asText());
		}else if ("L4".equalsIgnoreCase(searchCriteria)) {
			res.put("searchValue", manageBillingPriceJsonNode.path("l4AcctId").asText());
		}
		res.put("opportunityID", manageBillingPriceJsonNode.path("opportunityID").asText());
		res.put("dunsNumber", manageBillingPriceJsonNode.path("dunsNumber").asText());
		res.put("mcn", manageBillingPriceJsonNode.path("mcn").asText());
		res.put("mainAcctNumber", manageBillingPriceJsonNode.path("mainAcctNumber").asText());
		res.put("mainAccountNumber", manageBillingPriceJsonNode.path("mainAccountNumber").asText());
		res.put("l5MasterAcctId", manageBillingPriceJsonNode.path("l5MasterAcctId").asText());
		res.put("l4AcctId", manageBillingPriceJsonNode.path("l4AcctId").asText());
		res.put("l3SubAcctId", manageBillingPriceJsonNode.path("l3SubAcctId").asText());
		res.put("svid", manageBillingPriceJsonNode.path("svid").asText());
		String billMonth = manageBillingPriceJsonNode.path("billMonth").asText();
		String beginBillMonth = manageBillingPriceJsonNode.path("beginBillMonth").asText();
		try {
			billMonth = YearMonth.parse(billMonth, dtfMonthNum).format(dtfFullMonthName);
			beginBillMonth = YearMonth.parse(beginBillMonth, dtfMonthNum).format(dtfFullMonthName);
		} catch (DateTimeException e) {
			
		}
		res.put("billMonth", billMonth);
		res.put("beginBillMonth", beginBillMonth);
		return res;
	}
	
	protected Set<String> addAuditCheck(Map<String, List<String>> messageMap, ObjectNode res, Set<String> rootSet) {
		if (messageMap.isEmpty()) {
			return rootSet;
		}
		JsonNode mainSheet = res.path("mainSheet");
		for (JsonNode n : mainSheet) {
//			if (messageMap.isEmpty()) {
//				break;
//			}
			for (String idPath : retrieveIdPaths()) {
				if (n.path(idPath).asText().length() > 0) {
					String id = n.path(idPath).asText().replaceAll("\\W", "");
					if (messageMap.containsKey(id)) {
						addNewFalloutRow(res, n, null, String.join(" ", messageMap.get(id)), rootSet);
//						messageMap.remove(id);
						break;
					}
				}
			}
		}
		return rootSet;
	}

	protected Map<String, List<String>> falloutMessageMap(NxRequestDetails nxRequestDetails) {
		long refId = nxRequestDetails.getNxReqId();
		Map<String, NxLookupData> cdirDesignAuditMap = nxMyPriceRepositoryServce.getLookupDataByItemId("CDIR_DESIGN_AUDIT");
		List<NxLookupData> nxLookupdata = nxLookupDataRepository.findByDatasetName(MyPriceConstants.USAGE_NON_USAGE_OFFER);
		String[] usageOffer = nxLookupdata.get(0).getDescription().split("\\s*,\\s*");
		Set<String> usageNonUsageOffer=new HashSet<>(Arrays.asList(usageOffer));

		List<NxDesignAudit> audits = new ArrayList<>();
		for (String transaction : cdirDesignAuditMap.keySet()) {
			NxDesignAudit audit = nxDesignAuditRepository.findByNxRefIdAndTransaction(refId, transaction);
			if (audit != null && audit.getData() != null && audit.getData().length() > 2) {
				audits.add(audit);
			}
		}
		Map<String, List<String>> messageMap = new HashMap<>();
		for (NxDesignAudit audit : audits) {
			String[] data = audit.getData().substring(1, audit.getData().length() - 1).trim().split("\\s*,\\s*");
			for (String id : data) {
				if (!messageMap.containsKey(id)) {
					messageMap.put(id, new ArrayList<>());
				}
				messageMap.get(id).add(cdirDesignAuditMap.get(audit.getTransaction()).getDescription());
			}
		}
		Long nxRequestDetailsStatus = nxRequestDetails.getStatus();
		if (nxRequestDetailsStatus != null && (CommonConstants.STATUS_CONSTANTS.SUBMIT_MYPRICE_FALLOUT_IGNORED
				.getValue() == nxRequestDetailsStatus
				|| CommonConstants.STATUS_CONSTANTS.SUBMIT_MYPRICE_SUCCESS.getValue() == nxRequestDetailsStatus)) {
			audits = nxDesignAuditRepository.findByNxRefIdAndTransactionAndStatus(
					nxRequestDetails.getNxSolutionDetail().getNxSolutionId(), "REST_CONFIG_FAILURE_DATA",
					nxRequestDetails.getProduct());
			for (NxDesignAudit audit : audits) {
				if (audit != null && audit.getData() != null) {
					try {
						JsonNode auditDataNode = mapper.readTree(audit.getData());
						JsonNode restErrors = auditDataNode.path("restErrors");
						if (restErrors.isArray()) {
							for (JsonNode error : restErrors) {
								if(usageNonUsageOffer.contains(nxRequestDetails.getProduct())) {
									if (!error.path("nxsiteMatchingId").asText().isEmpty() && !error.path("messages").asText().isEmpty()) {
										String id = error.path("nxsiteMatchingId").asText();
										if (!messageMap.containsKey(id)) {
											messageMap.put(id, new ArrayList<>());
										}
										messageMap.get(id).add(cdirDesignAuditMap.get("MYPRICE_CONFIG_FAILURE").getDescription());
									}
								}else {
								if (!error.path("circuitId").asText().isEmpty() && !error.path("messages").asText().isEmpty()) {
									String id = error.path("circuitId").asText();
									if (!messageMap.containsKey(id)) {
										messageMap.put(id, new ArrayList<>());
									}
									//messageMap.get(id).add(error.path("messages").asText());
									/**msg description to be picked from db
									 */
									messageMap.get(id).add(cdirDesignAuditMap.get("MYPRICE_CONFIG_FAILURE").getDescription());
								}
							}
						}
					}
					} catch (IOException e) {
						logger.info("Exception in parsing NX_DESIGN_AUDIT DATA column to json node", e);
					}
				}
			}
		}
		return messageMap;
	}
	
	protected void addNewFalloutRow(ObjectNode res, List<String> rootSetList, InventoryJsonLookup inventoryJsonLookup, String reason, Set<String> rootSet) {
		ObjectNode newRow = mapper.createObjectNode();
		String falloutRootTag = rootSetList.get(0);
		if (inventoryJsonLookup != null) {
			inventoryJsonLookup.getShortForm().forEach((k, v) -> {
				newRow.put("/fallout/" + k, v);
			});
			for (Entry<String, String> entry : inventoryJsonLookup.getLongForm().entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				newRow.put(key, value);
				if (key.startsWith("/InrDomCktResponse/Body/DomesticIOCInventory")
						|| key.startsWith("/DDAResponse/Body/DomesticDSODS1AccessInventory")
						|| key.startsWith("/DDAResponse/Body/DomesticDS3OCXAccessInventory")) {
					falloutRootTag = rootSetList.get(1);
				}
			}
		}
		newRow.put("/fallout/reason", reason);
		falloutRootTag = "fo" + falloutRootTag;
		rootSet.add(falloutRootTag);
		newRow.put(InrConstants.FLATTEN_ROOT_TAG, falloutRootTag);
		if (inventoryJsonLookup != null && inventoryJsonLookup.getFallOutReason() != null) {
			newRow.put("/fallout/reason", inventoryJsonLookup.getFallOutReason());
		}
		res.withArray("falloutSheet").add(newRow);
	}

	protected void addNewFalloutRow(ObjectNode res, JsonNode row, InventoryJsonLookup inventoryJsonLookup, String reason, Set<String> rootSet) {
		ObjectNode newRow = row.deepCopy();
		String rootTag = row.path(InrConstants.FLATTEN_ROOT_TAG).asText();
		String falloutRootTag = "fo" + rootTag;
		rootSet.add(falloutRootTag);
		newRow.put(InrConstants.FLATTEN_ROOT_TAG, falloutRootTag);
		if (inventoryJsonLookup != null) {
			inventoryJsonLookup.getShortForm().forEach((k, v) -> {
				newRow.put("/fallout/" + k, v);
			});
		}
		newRow.put("/fallout/reason", reason);
		if (inventoryJsonLookup != null && inventoryJsonLookup.getFallOutReason() != null) {
			newRow.put("/fallout/reason", inventoryJsonLookup.getFallOutReason());
		}
		res.withArray("falloutSheet").add(newRow);
	}

//	protected boolean isFalloutMatch(JsonNode dataElement, InventoryJsonLookup inventoryJsonLookup) {
//		Map<String, String> longForm = inventoryJsonLookup.getLongForm();
//		Map<String, String> shortForm = inventoryJsonLookup.getShortForm();
//		if (shortForm.containsKey("productCd")
//				&& dataElement.path(InrConstants.FLATTEN_ROOT_TAG).asText().equals("InrDomCktResponse")
//				&& !"EPLSWAN".equals(shortForm.get("productCd"))) {
//			return false;
//		}
//		if (shortForm.containsKey("productCd")
//				&& dataElement.path(InrConstants.FLATTEN_ROOT_TAG).asText().equals("InrDomCktResponse1")
//				&& !"Private Line".equals(shortForm.get("productCd"))) {
//			return false;
//		}
//		if (shortForm.containsKey("productCd")
//				&& dataElement.path(InrConstants.FLATTEN_ROOT_TAG).asText().equals("DDAResponse1")
//				&& !"TDM".equals(shortForm.get("productCd"))) {
//			return false;
//		}
//		
//		for (Entry<String, String> entry : longForm.entrySet()) {
//			String key = entry.getKey();
//			if (!key.contains(",")) {
//				if (!dataElement.path(key).asText().equals(entry.getValue())) {
//					return false;
//				}
//			} else {
//				String[] keys = key.split("\\s*,\\s*");
//				boolean matchAny = false;
//				for (String k : keys) {
//					if (dataElement.path(k).asText().equals(entry.getValue())) {
//						matchAny = true;
//						break;
//					}
//				}
//				if (!matchAny) {
//					return false;
//				}
//			}
//		}
//		return true;
//	}
	
	protected List<String> retrieveIdPaths() {
		if (idPaths == null) {
			List<NxLookupData> nxLookUpData = nxLookupDataRepository.findByDatasetName("INR_CDIR_IDS");
			List<String> res = nxLookUpData.stream().map(NxLookupData::getDescription).collect(Collectors.toList());
			idPaths = res;
		}
		return idPaths;
	}
}
