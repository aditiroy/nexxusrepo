package com.att.sales.nexxus.service;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.commons.lang.StringUtils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.inr.InrFactory;
import com.att.sales.nexxus.inr.InrPreviewGeneratorV1;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class InrEditCreateExcelService {
	private static Logger logger = LoggerFactory.getLogger(InrEditCreateExcelService.class);
	// match to root tag in template
	private static String DUMMY_ROOT_NAME = "dummyRoot";
	private static String TREE_NXSITEID_TAG = "nxSiteId";

	@Autowired
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private InrFactory inrFactory;

	@Autowired
	private NxRequestDetailsRepository nxRequestDetailsRepository;
	
	@Autowired
	private BulkUploadInrUpdateService bulkUploadInrUpdateService;
	
	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;

	public File generateInrAddressEditSheet(Long nxSolutionId) throws IOException {
		logger.info("generateInrAddressEditSheet for nxSolutionId:{}", nxSolutionId);
		List<NxRequestDetails> nxRequestDetailsList = nxRequestDetailsRepository.findByNxSolutionId(nxSolutionId);
		nxRequestDetailsList.sort((o1, o2) -> {
			if ("AVPN".equalsIgnoreCase(o1.getProduct())) {
				return -1;
			} else if ("AVPN".equalsIgnoreCase(o2.getProduct())) {
				return 1;
			}
			if (null == o1.getProduct()) {
				return 1;
			}
			if (null == o2.getProduct()) {
				return -1;
			}
			return o1.getProduct().compareTo(o2.getProduct());
			
		});
		Map<Integer, ObjectNode> rowMap = new LinkedHashMap<>();
		/*LinkedHashMap<String, String> dataConvertRuleMap = nxMyPriceRepositoryServce
				.getDataFromLookup("INR_EDIT_EXCEL_DOWNLOAD_MAPPING");*/
		for (NxRequestDetails nxRequestDetails : nxRequestDetailsList) {
			String datasetName = bulkUploadInrUpdateService.getAddressDatasetName(nxRequestDetails.getFlowType());
			List<NxLookupData> nxLookupLst=nxLookupDataRepository.findByDatasetNameAndItemIdAndActive(datasetName, nxRequestDetails.getProduct(), StringConstants.CONSTANT_Y);
			LinkedHashMap<String,String> dataConvertRuleMap=new LinkedHashMap<String,String>();
			 Optional.ofNullable(nxLookupLst).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull).
			    forEach( data -> {
			    	if(StringUtils.isNotEmpty(data.getCriteria())) {
			    		dataConvertRuleMap.put(data.getItemId(), data.getCriteria());
			    	}
			 });
			String offer = nxRequestDetails.getProduct();
			if(!offer.equalsIgnoreCase("VTNS LD Voice Features")) {
				if (nxRequestDetails.getNxOutputFiles() != null && !nxRequestDetails.getNxOutputFiles().isEmpty()
						&& nxRequestDetails.getNxOutputFiles().get(0) != null) {
					NxOutputFileModel nxOutputFile = nxRequestDetails.getNxOutputFiles().get(0);
					saveAddressToMap(rowMap, dataConvertRuleMap.get(offer), nxOutputFile.getMpOutputJson(), offer);
				}
			}
		}
		ArrayNode excelData = generateExcelData(rowMap);
		InrPreviewGeneratorV1 inrAddressEditGenerator = inrFactory.getInrAddressEditGenerator(excelData,
				nxSolutionId.hashCode());
		File file = inrAddressEditGenerator.generate();
		return file;
	}

	protected ArrayNode generateExcelData(Map<Integer, ObjectNode> rowMap) {
		ObjectNode sheet = mapper.createObjectNode();
		rowMap.values().forEach(objectNode -> sheet.withArray("mainSheet").add(objectNode));
		sheet.withArray("rootTag").add(DUMMY_ROOT_NAME);
		ArrayNode excelData = mapper.createArrayNode();
		excelData.add(sheet);
		return excelData;
	}

	protected void saveAddressToMap(Map<Integer, ObjectNode> rowMap, String dataConvertRule, String mpOutputJson,
			String offer) {
		if (mpOutputJson == null || dataConvertRule == null) {
			logger.info("mpOutputJson or dataConvertRule missing, skipping converting data for nxRequestDetails");
			return;
		}
		try {
			JsonNode mpOutputJsonNode = mapper.readTree(mpOutputJson);
			JsonNode dataConvertRuleNode = mapper.readTree(dataConvertRule);
			traverse(mpOutputJsonNode, dataConvertRuleNode, rowMap, offer);
		} catch (IOException e) {
			logger.error("mp_output_json parse error", e);
		}
	}

	protected void traverse(JsonNode mpOutputJsonNode, JsonNode dataConvertRuleNode, Map<Integer, ObjectNode> rowMap,
			String offer) {
		Map<String, ObjectNode> objNodeMap = new HashMap<>();
		// used to delay removal of nxSiteId objNode's children
		Set<String> objToBeRemovedFromObjNodeMap = new HashSet<>();
		Set<Integer> avpnDesignArrayFirstNxSiteId = new HashSet<>();
		traverseHelper(mpOutputJsonNode, "root", dataConvertRuleNode, rowMap, objNodeMap, objToBeRemovedFromObjNodeMap,
				offer, -1, avpnDesignArrayFirstNxSiteId);
	}

	protected void traverseHelper(JsonNode node, String currentObjTag, JsonNode dataConvertRuleNode,
			Map<Integer, ObjectNode> rowMap, Map<String, ObjectNode> objNodeMap,
			Set<String> objToBeRemovedFromObjNodeMap, String offer, int arrayIndex,
			Set<Integer> avpnDesignArrayFirstNxSiteId) {
		if (node.getNodeType() == JsonNodeType.ARRAY) {
			for (int i = 0; i < node.size(); i++) {
				traverseHelper(node.get(i), currentObjTag, dataConvertRuleNode, rowMap, objNodeMap,
						objToBeRemovedFromObjNodeMap, offer, i, avpnDesignArrayFirstNxSiteId);
			}
		} else if (node.getNodeType() == JsonNodeType.OBJECT) {
			objNodeMap.put(currentObjTag, (ObjectNode) node);
			List<String> childName = new ArrayList<>();
			List<JsonNode> childNode = new ArrayList<>();
			Iterator<Entry<String, JsonNode>> iterator = node.fields();
			iterator.forEachRemaining(entry -> {
				childName.add(entry.getKey());
				childNode.add(entry.getValue());
			});
			// delay removal of objMap for nxSiteId objNode's children
			if (node.has(TREE_NXSITEID_TAG) || !objToBeRemovedFromObjNodeMap.isEmpty()) {
				objToBeRemovedFromObjNodeMap.add(currentObjTag);
			}
			for (int i = 0; i < childName.size(); i++) {
				traverseHelper(childNode.get(i), childName.get(i), dataConvertRuleNode, rowMap, objNodeMap,
						objToBeRemovedFromObjNodeMap, offer, -1, avpnDesignArrayFirstNxSiteId);
			}
			// if current objNode has tag nxSiteId, save address to rowMap
			if (node.has(TREE_NXSITEID_TAG)) {
				for (JsonNode rule : dataConvertRuleNode) {
					ObjectNode row = null;
					int nxSiteId = 0;
					
					if(objNodeMap.get("site") != null && objNodeMap.get("site").has("nexxusFallout")) {
						if("Y".equals(objNodeMap.get("site").get("nexxusFallout").asText())) {
							continue;
						}
					}
					// first loop find the nxSiteId
					for (JsonNode column : rule) {
						if ("NX_Site_ID".equals(column.path("excelTag").asText())) {
							String nxSiteIdTreeTag = column.path("treeTag").asText();
							nxSiteId = node.path(nxSiteIdTreeTag).asInt();
							if (siteAddressHasManyNxSiteIds(offer, currentObjTag) && arrayIndex == 0) {
								avpnDesignArrayFirstNxSiteId.add(nxSiteId);
							}
							if (!rowMap.containsKey(nxSiteId)) {
								ObjectNode newRow = mapper.createObjectNode();
								newRow.put("rootTag", DUMMY_ROOT_NAME);
								rowMap.put(nxSiteId, newRow);
							}
							row = rowMap.get(nxSiteId);
							break;
						}
					}
					  
					for (JsonNode column : rule) {
						String objName = column.path("objName").asText();
						String excelTag = column.path("excelTag").asText();
						String treeTag = column.path("treeTag").asText();
					
						
						if (objNodeMap.get(objName) != null  && objNodeMap.get(objName).has(treeTag)) {
							if (siteAddressHasManyNxSiteIds(offer, currentObjTag) && arrayIndex > 0 && "site".equalsIgnoreCase(objName)
									&& !avpnDesignArrayFirstNxSiteId.contains(nxSiteId)) {
								
									row.put(excelTag, "*" + objNodeMap.get(objName).get(treeTag).asText());
							
							} else {
								if (row.path(excelTag).isMissingNode() || row.path(excelTag).isNull() || !row.path(excelTag).asText().startsWith("*")) {
									
										row.set(excelTag, objNodeMap.get(objName).get(treeTag));
								}
							}
						} 
					}
					
					if (row.path("Action").isMissingNode() || row.path("Action").isNull()) {
						row.put("Action", "No Change");
					}
				}
				objToBeRemovedFromObjNodeMap.forEach(objName -> objNodeMap.remove(objName));
				objToBeRemovedFromObjNodeMap.clear();
			} else if (objToBeRemovedFromObjNodeMap.isEmpty()) {
				objNodeMap.remove(currentObjTag);
			}
		}
	}
	
	protected boolean siteAddressHasManyNxSiteIds(String offer, String currentObjTag) {
		if (MyPriceConstants.AVPN.equalsIgnoreCase(offer)) {
			return true;
		} else if (MyPriceConstants.MISPNT.equalsIgnoreCase(offer) && "design".equals(currentObjTag)) {
			return true;
		}
		return false;
	}

	public void uploadExcelData(NxRequestDetails nxRequestDetails, String dataConvertRule,
			List<LinkedHashMap<String, Object>> cdirData) throws IOException {
		logger.info("uploadExcelData for nxReqId: {}", nxRequestDetails.getNxReqId());
		String offer = nxRequestDetails.getProduct();
		NxOutputFileModel nxOutputFileModel = nxRequestDetails.getNxOutputFiles().get(0);
		JsonNode mpOutputJsonNode = mapper.readTree(nxOutputFileModel.getMpOutputJson());
		JsonNode intermediateJsonNode = mapper.readTree(nxOutputFileModel.getIntermediateJson());
		JsonNode dataConvertRuleNode = mapper.readTree(dataConvertRule);
		Map<String, LinkedHashMap<String, Object>> uploadDataMap = new LinkedHashMap<>();
		Map<String, LinkedHashMap<String, Object>> avpnCdirAddedData = new LinkedHashMap<>();
		for (LinkedHashMap<String, Object> uploadData : cdirData) {
			uploadDataMap.put(String.valueOf(uploadData.get("NX_Site_ID")), uploadData);
		}
		doubleTraverse(mpOutputJsonNode, intermediateJsonNode, dataConvertRuleNode, uploadDataMap, offer,
				avpnCdirAddedData);
		nxOutputFileModel.setMpOutputJson(mpOutputJsonNode.toString());
		nxOutputFileModel.setIntermediateJson(intermediateJsonNode.toString());
		nxOutputFileModel.setModifiedDate(new Timestamp(System.currentTimeMillis()));
		if (!avpnCdirAddedData.isEmpty()) {
			avpnCdirAddedData.values().forEach(addedCdirData -> cdirData.add(addedCdirData));
		}
	}

	protected void doubleTraverse(JsonNode mpOutputJsonNode, JsonNode intermediateJsonNode,
			JsonNode dataConvertRuleNode, Map<String, LinkedHashMap<String, Object>> uploadDataMap, String offer,
			Map<String, LinkedHashMap<String, Object>> avpnCdirAddedData) {
		Map<String, ObjectNode> objNode1Map = new HashMap<>();
		Map<String, ObjectNode> objNode2Map = new HashMap<>();
		Set<String> objToBeRemovedFromObjNodeMap = new HashSet<>();
		Map<String, Object> paramMap = new HashMap<>();
		doubleTraverseHelper(mpOutputJsonNode, intermediateJsonNode, "root", dataConvertRuleNode, uploadDataMap,
				objNode1Map, objNode2Map, objToBeRemovedFromObjNodeMap, offer, avpnCdirAddedData, -1, paramMap);
	}

	protected void doubleTraverseHelper(JsonNode node1, JsonNode node2, String currentObjTag,
			JsonNode dataConvertRuleNode, Map<String, LinkedHashMap<String, Object>> uploadDataMap,
			Map<String, ObjectNode> objNode1Map, Map<String, ObjectNode> objNode2Map,
			Set<String> objToBeRemovedFromObjNodeMap, String offer,
			Map<String, LinkedHashMap<String, Object>> avpnCdirAddedData, int arrayIndex,
			Map<String, Object> paramMap) {
		if (node1.getNodeType() == JsonNodeType.ARRAY) {
			for (int i = 0; i < node1.size(); i++) {
				doubleTraverseHelper(node1.get(i), node2.get(i), currentObjTag, dataConvertRuleNode, uploadDataMap,
						objNode1Map, objNode2Map, objToBeRemovedFromObjNodeMap, offer, avpnCdirAddedData, i, paramMap);
			}
		} else if (node1.getNodeType() == JsonNodeType.OBJECT) {
			objNode1Map.put(currentObjTag, (ObjectNode) node1);
			objNode2Map.put(currentObjTag, (ObjectNode) node2);
			List<String> childName = new ArrayList<>();
			List<JsonNode> node1Child = new ArrayList<>();
			Iterator<Entry<String, JsonNode>> iterator1 = node1.fields();
			iterator1.forEachRemaining(entry -> {
				if (entry.getValue().getNodeType() == JsonNodeType.OBJECT
						|| entry.getValue().getNodeType() == JsonNodeType.ARRAY) {
					if (!("MIS/PNT".equals(offer) && "uniqueIds".equals(entry.getKey()))) { // mpOutputJson and
																							// inventoryJson not match
						childName.add(entry.getKey());
						node1Child.add(entry.getValue());
					}
				}
			});
			List<JsonNode> node2Child = new ArrayList<>();
			Iterator<Entry<String, JsonNode>> iterator2 = node2.fields();
			iterator2.forEachRemaining(entry -> {
				if (entry.getValue().getNodeType() == JsonNodeType.OBJECT
						|| entry.getValue().getNodeType() == JsonNodeType.ARRAY) {
					node2Child.add(entry.getValue());
				}
			});
			// delay removal of objMap for nxSiteId objNode's children
			if (node1.has(TREE_NXSITEID_TAG) || !objToBeRemovedFromObjNodeMap.isEmpty()) {
				objToBeRemovedFromObjNodeMap.add(currentObjTag);
			}
			for (int i = 0; i < childName.size(); i++) {
				doubleTraverseHelper(node1Child.get(i), node2Child.get(i), childName.get(i), dataConvertRuleNode,
						uploadDataMap, objNode1Map, objNode2Map, objToBeRemovedFromObjNodeMap, offer, avpnCdirAddedData,
						-1, paramMap);
			}
			// if current objNode has tag nxSiteId, check for updating
			if (node1.has(TREE_NXSITEID_TAG)) {
				for (JsonNode rule : dataConvertRuleNode) {
					LinkedHashMap<String, Object> uploadData = null;
					// first loop find the nxSiteId
					String nxSiteId = null;
					for (JsonNode column : rule) {
						if ("NX_Site_ID".equals(column.path("excelTag").asText())) {
							String nxSiteIdTreeTag = column.path("treeTag").asText();
							nxSiteId = node1.path(nxSiteIdTreeTag).asText();
							uploadData = uploadDataMap.get(nxSiteId);
							break;
						}
					}
					if (uploadData == null) {
						continue;
					}
					for (JsonNode column : rule) {
						String objName = column.path("objName").asText();
						String excelTag = column.path("excelTag").asText();
						String treeTag = column.path("treeTag").asText();
						if (!"NX_Site_ID".equals(excelTag) && uploadData.get(excelTag) != null) {
							// xy3208 part of this checking is moved to new method siteAddressHasManyNxSiteIds for inrbeta adi
							// further check if inrbeta adi need to be included in this logic
							// after checking, please remove this comment
							if (!("AVPN".equalsIgnoreCase(offer) && arrayIndex > 0
									&& "site".equalsIgnoreCase(objName))) {
								objNode1Map.get(objName).put(treeTag, uploadData.get(excelTag).toString());
								objNode2Map.get(objName).put(treeTag, uploadData.get(excelTag).toString());
							}
						}
					}
					// xy3208 part of this checking is moved to new method siteAddressHasManyNxSiteIds for inrbeta adi
					// further check if inrbeta adi need to be included in this logic
					// after checking, please remove this comment
					if ("AVPN".equalsIgnoreCase(offer)) {
						if (arrayIndex == 0) {
							paramMap.put("AVPN_ROOT_NX_Site_ID", nxSiteId);
						}
						List<String> avpnAddressUpdateAffectedNxSiteIds = objNode1Map.get("site")
								.findValuesAsText("nxSiteId");
						// set the address update for other site if root site is updated
						for (String affectedNxSiteId : avpnAddressUpdateAffectedNxSiteIds) {
							if (!uploadDataMap.containsKey(affectedNxSiteId)
									&& !avpnCdirAddedData.containsKey(affectedNxSiteId) && arrayIndex == 0) {
								LinkedHashMap<String, Object> copy = new LinkedHashMap<>(uploadData);
								copy.put("NX_Site_ID", affectedNxSiteId);
								copy.remove("ICORESITEID");
								avpnCdirAddedData.put(affectedNxSiteId, copy);
							}
						}

						long rootNxsiteId = paramMap.containsKey("AVPN_ROOT_NX_Site_ID")
								? Long.parseLong((String) paramMap.get("AVPN_ROOT_NX_Site_ID"))
								: 0l;
						long currentNxsiteId = Long.parseLong((String) uploadData.get("NX_Site_ID"));
						if (arrayIndex > 0 && (currentNxsiteId != rootNxsiteId)) {
							// non root site address details are updated only if root site is updated
							// else only icoresiteid is updated
							String non_rootSite_icoreSiteID = (String) uploadData.get("ICORESITEID");
							uploadData.clear();
							String avpnRotoNxSiteId = paramMap.containsKey("AVPN_ROOT_NX_Site_ID")
									? (String) paramMap.get("AVPN_ROOT_NX_Site_ID")
									: null;
							if (uploadDataMap.containsKey(avpnRotoNxSiteId)) {
								LinkedHashMap<String, Object> rootCopy = new LinkedHashMap<>(
										uploadDataMap.get(avpnRotoNxSiteId));
								rootCopy.remove("NX_Site_ID");
								rootCopy.remove("ICORESITEID");
								uploadData.putAll(rootCopy);
							}
							uploadData.put("NX_Site_ID", nxSiteId);
							uploadData.put("ICORESITEID", non_rootSite_icoreSiteID);
						}
					}
				}
				objToBeRemovedFromObjNodeMap.forEach(objName -> {
					objNode1Map.remove(objName);
					objNode2Map.remove(objName);
				});
				objToBeRemovedFromObjNodeMap.clear();
			} else if (objToBeRemovedFromObjNodeMap.isEmpty()) {
				objNode1Map.remove(currentObjTag);
				objNode2Map.remove(currentObjTag);
			}
		}
	}
}