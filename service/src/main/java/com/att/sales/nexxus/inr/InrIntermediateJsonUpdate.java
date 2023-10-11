package com.att.sales.nexxus.inr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.CommonConstants;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author sj0546
 *
 */
public class InrIntermediateJsonUpdate {
	private Map<String, JsonNode> nodeMap = new HashMap<>();
	private JsonNode outputJson;
	private LinkedHashMap<String, Object> criteriaMap;
	private LinkedHashMap<String, Object> dataMap;
	private String action;
	private String product;
	private Set<String> cktIdforAugmentation;
	private String flowType;
	public static final String EXCLUDE_LINE_ITEMS_FALLOUT_REASON = "Excluded line items per user request" ;

	/**
	 * Constructor to create object
	 * 
	 * @param outputJson
	 * @param criteriaMap
	 * @param dataMap
	 * @param action
	 */
	public InrIntermediateJsonUpdate(JsonNode outputJson,  LinkedHashMap<String, Object> criteriaMap,  LinkedHashMap<String, Object> dataMap, String action, String product, Set<String> cktIdforAugmentation, String flowType) {
		this.outputJson = outputJson;
		this.criteriaMap = criteriaMap;
		this.dataMap = dataMap;   
		this.action = action;
		this.product = product;
		this.cktIdforAugmentation = cktIdforAugmentation;
		this.flowType = flowType;
	}

	/**
	 * Initiate the inr json traversing
	 * 
	 * @param excludedCkts
	 * @param cdirInput
	 * @throws SalesBusinessException
	 */
	public void inredits(Set<String> excludedCkts, Map<String, LinkedHashMap<String, Object>> cdirInput, Set<String> cktUpdate,Set<String> inrBetaexcludeckts) throws SalesBusinessException {
		JsonPath rootPath = JsonPath.getRootPath();
		inreditsHelper(outputJson, rootPath, excludedCkts, cdirInput, cktUpdate,inrBetaexcludeckts);
	}

	/**
	 * Helper method to traverse the node
	 * 
	 * @param node
	 * @param path
	 * @param criteriaMap
	 * @param dataMap
	 * @param action
	 * @param excludedCkts
	 * @param cdirInput
	 */
	protected void inreditsHelper(JsonNode node, JsonPath path, Set<String> excludedCkts, Map<String, LinkedHashMap<String, Object>> cdirInput, Set<String> cktUpdate,Set<String> inrBetaexcludeckts) {
		switch (node.getNodeType()) {
		case ARRAY:
			processArrayNode(node, path, excludedCkts, cdirInput, cktUpdate,inrBetaexcludeckts);
			break;
		case OBJECT:
			processObjectNode(node, path, excludedCkts, cdirInput, cktUpdate,inrBetaexcludeckts);
			break;
		default:
			break;
		}
	}
	
	/**
	 * Traverse the Array node and update the data
	 * 
	 * @param node
	 * @param path
	 * @param criteriaParentMap
	 * @param dataMap
	 * @param action
	 * @param excludedCkts
	 * @param cdirInput
	 */
	protected void processArrayNode(JsonNode node, JsonPath path, Set<String> excludedCkts, Map<String, LinkedHashMap<String, Object>> cdirInput, Set<String> cktUpdate,Set<String> inrBetaexcludeckts) {
		ArrayNode arrayNode = (ArrayNode) node;
		for (int i = arrayNode.size() - 1; i >= 0; i--) {
			JsonNode element = arrayNode.get(i);
			boolean pathfound = false;
			for (Map.Entry<String, Object> c : criteriaMap.entrySet()) {
				if (c.getValue() instanceof Map<?, ?>) {
					Map<String, Object> criteriaJson = (Map<String, Object>) c.getValue();
					if (path.getPath().equals(criteriaJson.get(CommonConstants.WHERE_CLAUSE_PATH))) {
						pathfound = true;
						nodeMap.put(path.getPath(), node);
						JsonNode whereClauseAttriValue = element.findValue(criteriaJson.get(CommonConstants.WHERE_CLAUSE_ATTRINAME).toString());
						if (whereClauseAttriValue != null) {
							boolean dataMatch = findDataMatch(whereClauseAttriValue, String.valueOf(dataMap.get(criteriaJson.get(CommonConstants.WHERE_CLAUSE_EXCEL_COLNAME))),
									criteriaJson.get(CommonConstants.WHERE_CLAUSE_DATATYPE).toString());
							if(dataMatch) {
								if (criteriaJson.get(CommonConstants.DATA_KEY) instanceof Map<?, ?>) {
									Map<String, Object> dataUpdateMap = (Map<String, Object>) criteriaJson.get(CommonConstants.DATA_KEY) ;
									if(CommonConstants.DATA_UPDATE.equalsIgnoreCase(action) || CommonConstants.CIRCUITE_ID_AUGMENTATION.equalsIgnoreCase(action)) {
										for (Map.Entry<String, Object> x : dataUpdateMap.entrySet()) {
											if(dataMap.containsKey(x.getKey()) || CommonConstants.REMOVENXSITEID.equalsIgnoreCase(x.getKey())) {
												if (x.getValue() instanceof Map<?, ?>) {
													Map<String, String> mapData = (Map<String, String>) x.getValue();
													if(x.getKey().equalsIgnoreCase(CommonConstants.REMOVENXSITEID)) {
														if(dataMap.get("Circuit ID") != null && cktIdforAugmentation !=null && cktIdforAugmentation.contains(dataMap.get("Circuit ID").toString().replaceAll("\\s", "").replaceAll("\\.", ""))) {
															if(mapData.get(CommonConstants.PATH_KEY).equalsIgnoreCase(path.getPath())) {
																removeSiteAttributes(mapData.get(CommonConstants.JSON_ATTRINAME).toString(), element); 
																cktUpdate.add(StringConstants.CONSTANT_Y);
															}else {
																JsonNode obj = element.findPath(mapData.get(CommonConstants.PATH_KEY));
																if(!obj.isMissingNode() && !obj.isNull()) {
																	if(obj.isArray()) {
																		for(JsonNode j : obj) {
																			removeSiteAttributes(mapData.get(CommonConstants.JSON_ATTRINAME).toString(), j); 
																			cktUpdate.add(StringConstants.CONSTANT_Y);
																		}
																	}else {
																		removeSiteAttributes(mapData.get(CommonConstants.JSON_ATTRINAME).toString(), obj); 
																		cktUpdate.add(StringConstants.CONSTANT_Y);
																	}
																}
																
															}
														}
													}else {
														String data = null;
														if(mapData.containsKey(CommonConstants.OPERATION)) {
															if(CommonConstants.REPLACE.equalsIgnoreCase(mapData.get(CommonConstants.OPERATION))) {
																data = dataMap.get(x.getKey()).toString().replaceAll("\\W", "");
															}else if(CommonConstants.UPPERCASE.equalsIgnoreCase(mapData.get(CommonConstants.OPERATION))) {
																data = dataMap.get(x.getKey()).toString().toUpperCase();
															}
														}else {
															data = dataMap.get(x.getKey()).toString();
														}
														if(mapData.get(CommonConstants.PATH_KEY).equalsIgnoreCase(path.getPath())) {
															((ObjectNode) element).put(mapData.get(CommonConstants.JSON_ATTRINAME), data);
															 cktUpdate.add(StringConstants.CONSTANT_Y);
														}else {
															JsonNode obj = element.findPath(mapData.get(CommonConstants.PATH_KEY));
															if(!obj.isMissingNode() && !obj.isNull()) {
																if(obj.isArray()) {
																	for(JsonNode j : obj) {
																		JsonNode subCondition = j.findValue(criteriaJson.get(CommonConstants.WHERE_CLAUSE_ATTRINAME).toString());
																		boolean subDataMatch = findDataMatch(subCondition, String.valueOf(dataMap.get(criteriaJson.get(CommonConstants.WHERE_CLAUSE_EXCEL_COLNAME))),
																				criteriaJson.get(CommonConstants.WHERE_CLAUSE_DATATYPE).toString());
																		if(subDataMatch) {
																			((ObjectNode) j).put(mapData.get(CommonConstants.JSON_ATTRINAME), data);
																			 cktUpdate.add(StringConstants.CONSTANT_Y);
																		}
																	}
																}else {
																	((ObjectNode) obj).put(mapData.get(CommonConstants.JSON_ATTRINAME), data);
																	 cktUpdate.add(StringConstants.CONSTANT_Y);
																}
															}
															
														}
														prepareCdirInput(element, criteriaJson.get(CommonConstants.CDIR_UPDATE_KEY).toString(),  cdirInput);
													}
												}
											}
										}
										((ObjectNode) element).put("dataUpdated", "Y");
									} 
								} else if(CommonConstants.EXCLUDE_FROM_MP.equalsIgnoreCase(action)){
									if(MyPriceConstants.SOURCE_INR.equalsIgnoreCase(flowType)) {
									JsonNode cktNode = element.findValue(criteriaJson.get(CommonConstants.CKTID_ATTRINAME).toString());
									if(criteriaJson.containsKey(CommonConstants.CKTID_CHECK) && StringConstants.CONSTANT_Y.equalsIgnoreCase(criteriaJson.get(CommonConstants.CKTID_CHECK).toString())) {
										if(cktNode != null) {
											// nxt1 circuits can have same nxsiteid so this logic is to remove only the ckt request by user
											if(cktNode.asText().equalsIgnoreCase(dataMap.get(criteriaJson.get(CommonConstants.CKTID_EXCEL_NAME)).toString().replaceAll("\\W", ""))) {
												if(!arrayNode.remove(i).isNull()) {
													excludedCkts.add(cktNode.asText());
												}
											}
										}
									}else {
										if(!arrayNode.remove(i).isNull()) {
											excludedCkts.add(cktNode.asText());
										}
									}
									} else if (MyPriceConstants.SOURCE_USRP.equalsIgnoreCase(flowType)) {
												((ObjectNode) element).put(InrConstants.NEXXUS_FALLOUT, "Y");
												JsonNode priceDetailsArray = element.findPath("priceDetails");
												for (JsonNode priceDetails : priceDetailsArray) {
													((ObjectNode) priceDetails).put(InrConstants.NEXXUS_FALLOUT, "Y");
													((ObjectNode) priceDetails).put(InrConstants.NEXXUS_FALLOUT_REASON, "Excluded line items per user request");
															
												}
												JsonNode cktNode = element.findValue(criteriaJson.get(CommonConstants.CKTID_ATTRINAME).toString());
												inrBetaexcludeckts.add(cktNode.asText());
									}
								}
							}
						} 
					}
				}
			}
			if(!pathfound) {
				this.inreditsHelper(element, path, excludedCkts, cdirInput, cktUpdate,inrBetaexcludeckts);
			}
		}	
		
	}

	/**
	 * Traverse the object node
	 * 
	 * @param node
	 * @param path
	 * @param criteriaMap
	 * @param dataMap
	 * @param action
	 * @param excludedCkts
	 * @param cdirInput
	 */
	protected void processObjectNode(JsonNode node, JsonPath path, Set<String> excludedCkts, Map<String, LinkedHashMap<String, Object>> cdirInput, Set<String> cktUpdate,Set<String> inrBetaexcludeckts) {
		nodeMap.put(path.getPath(), node);
		
		if (path.getPath().equals(criteriaMap.get(CommonConstants.WHERE_CLAUSE_PATH))) {
			
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
				this.inreditsHelper(childNode.get(i), path.resolveContainerNode(childName.get(i)), excludedCkts, cdirInput, cktUpdate,inrBetaexcludeckts);
			}
		}
	}
	
	/**
	 * Find the json block to be updated
	 * 
	 * @param whereClauseAttriValue
	 * @param whereClauseName
	 * @param dataType
	 * @return
	 */
	protected boolean findDataMatch(JsonNode whereClauseAttriValue, String whereClauseData, String dataType) {
		boolean dataMatch = false;
		if(CommonConstants.STRING_LONG.equalsIgnoreCase(dataType)) {
			long longData = Long.parseLong(whereClauseData);
			if(longData == whereClauseAttriValue.asLong()) {
				dataMatch = true;
			}
		}else if(CommonConstants.STRING_INTEGER.equalsIgnoreCase(dataType)) {
			int intData = Integer.parseInt(whereClauseData);
			if(intData == whereClauseAttriValue.asInt()) {
				dataMatch = true;
			}
		}else {
			if(whereClauseData.equalsIgnoreCase(whereClauseAttriValue.asText())) {
				dataMatch = true;
			}
		}
		return dataMatch;
	}
	
	/**
	 * Removes nxSiteId, nxSiteIdZ and endPointType
	 * 
	 * @param jsonAttriName
	 * @param element
	 */
	protected void removeSiteAttributes(String jsonAttriName, JsonNode element) {
		String[] siteAttributes = jsonAttriName.toString().split(",");
		for(String siteAttribute : siteAttributes) {
			if (element.has(siteAttribute)) {
				((ObjectNode) element).remove(siteAttribute);
			}
		}
	}
	
	/**
	 * Prepares input object for cdir update
	 * @param element
	 * @param key
	 * @param cdirInput
	 */
	protected void prepareCdirInput(JsonNode element, String key, Map<String, LinkedHashMap<String, Object>> cdirInput) {
		List<JsonNode> nodes; 
		// custom logic to send site specific access architecture
		if(CommonConstants.DDA_PRODUCT_NAME.equalsIgnoreCase(product) && dataMap.containsKey(CommonConstants.DDA_ACCESS_ARCHITECTURE)) {
			nodes = element.findParents(key);
			for(JsonNode node : nodes) {
				Long nxSiteId = Long.parseLong(dataMap.get(CommonConstants.EXCEL_NX_SITE_ID_COLNAME).toString());
				if(nxSiteId.longValue() == node.findValue(CommonConstants.JSON_NXSITEID_NAME).asLong()) {
					cdirInput.put(node.findValue(key).asText(), dataMap);
				}else {
					LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>(dataMap);
					map.remove(CommonConstants.DDA_ACCESS_ARCHITECTURE);
					cdirInput.put(node.findValue(key).asText(), map);
					map = null;
				}
			}
		}else {
			nodes = element.findValues(key);
			for(JsonNode cdir : nodes) {
				cdirInput.put(cdir.asText(), dataMap);
			}
		}
	}
}
