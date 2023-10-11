package com.att.sales.nexxus.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxAccessPricingData;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.NxRequestGroup;
import com.att.sales.nexxus.dao.model.NxSolutionSite;
import com.att.sales.nexxus.dao.model.NxValidationRules;
import com.att.sales.nexxus.dao.repository.NxAccessPricingDataRepository;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxOutputFileRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxRequestGroupRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionSiteRepository;
import com.att.sales.nexxus.dao.repository.NxValidationRulesRepository;
import com.att.sales.nexxus.fallout.service.FalloutDetailsImpl;
import com.att.sales.nexxus.inr.InrIntermediateJsonGenerator;
import com.att.sales.nexxus.model.CircuitSiteDetails;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.TypeRef;

@Component
public class InrBetaGenerateNxsiteId {
	
	@Autowired
	private NxDesignAuditRepository nxDesignAuditRepository;
	
	@Autowired
	private NxSolutionSiteRepository nxSolutionSiteRepository;
	
	@Autowired
	private NexxusJsonUtility nexxusJsonUtility;
	
	@Autowired
	private NxRequestDetailsRepository nxRequestDetailsRepository;
	
	@Autowired
	private NxOutputFileRepository nxOutputFileRepository;
	
	@Autowired
	private NxRequestGroupRepository nxRequestGroupRepository;
	
	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Autowired
	private NxValidationRulesRepository nxValidationRulesRepository;
	
	@Autowired
	private JsonPathUtil jsonPathUtil;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private FalloutDetailsImpl falloutDetailsImpl;
	
	@Autowired
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;

	@Autowired
	private NxAccessPricingDataRepository nxaccessPricingDataRepo;
	
	
	@Autowired
	private InrQualifyService inrQualifyService;


	@Value("${p8.local.destPath}")
	private String p8dLocalPath;
	
	private static Logger logger = LoggerFactory.getLogger(InrBetaGenerateNxsiteId.class);
	private static final String tdmNonQualiFiedPortWWCircuitPath = "$..design[?(@.accessProductName=='TDM' && @.qualifyCircuit=='N')].[?(@.priceDetails..typeOfCharge contains 'P')].accesscktid";
//	private static final  String tdmNonQualifiedAccessCircuitPath = "$..design[?(@.accessProductName=='TDM' && @.qualifyCircuit=='N')].[?(@.priceDetails..typeOfCharge contains 'A')].circuitId";
	private static final  String tdmCktId="$..design[?(@.accessProductName=='TDM')].circuitId";
	private static final String tdmQualifiedWWCircuitPath = "$..design[?(@.accessProductName=='TDM' && @.qualifyCircuit=='Y')].accesscktid";

	public Map<String, Object> generateNxsiteidInrBeta(Long nxReqId, boolean isGenerateNxSiteId, Map<String, Object> map){
		Map<String, Object> result = new HashMap<String, Object>();
		Long currentTime = System.currentTimeMillis();
		Long startTime=System.currentTimeMillis() - currentTime;
		try {
			NxRequestDetails reqidDetail = nxRequestDetailsRepository.findByNxReqIdAndActiveYn(nxReqId,
					StringConstants.CONSTANT_Y);
			if (CollectionUtils.isEmpty(reqidDetail.getNxOutputFiles()) || reqidDetail.getNxOutputFiles().get(0).getMpOutputJson() == null ) {
				logger.info("InrBetaQualifycheck is not done as output json is not populated for the requests OR json generation is failed for requestid:: {}", nxReqId);
			}else {
				inrQualifyService.inrBetaQualifycheck(nxReqId);
				Long qualifycheckendTime=System.currentTimeMillis() - currentTime;
				logger.info("InrBetaQualifycheck for reqid {} took  total duration {} millisec",nxReqId, (qualifycheckendTime - startTime));
			
				inrBetaFractionalTDM(nxReqId);
				Long fractionalPortcheckendTime=System.currentTimeMillis() - currentTime;
				logger.info("InrBetaFractionalTDM for reqid {} took  total duration {} millisec",nxReqId, (fractionalPortcheckendTime - startTime));

			}
			
			NxRequestDetails nxReqGrpId =  nxRequestDetailsRepository.findByNxReqIdAndActiveYn(nxReqId, StringConstants.CONSTANT_Y);
			logger.info("Start InrBetaGenerateNxsiteId for group id {}", nxReqGrpId.getNxRequestGroupId());
			List<NxRequestDetails> allNxRequestDetails = nxRequestDetailsRepository.findByNxSolutionDetailAndActiveYn(nxReqGrpId.getNxSolutionDetail(), StringConstants.CONSTANT_Y);
			List<NxRequestDetails> nxRequestDetails = nxRequestDetailsRepository.findByNxRequestGroupIdAndActiveYn(nxReqGrpId.getNxRequestGroupId(), StringConstants.CONSTANT_Y);
			NxRequestGroup nxRequestGroup = nxRequestGroupRepository.findByNxRequestGroupIdAndActiveYn(nxReqGrpId.getNxRequestGroupId(), StringConstants.CONSTANT_Y);
			List<Long> nxRequests = new ArrayList<>();
			boolean inProgress = false;
			for (NxRequestDetails nrd : nxRequestDetails) {
				if(nrd.getStatus() == 10) {
					inProgress = true;
					break;
				}
				if (nrd.getNxOutputFiles().size() == 0 || nrd.getNxOutputFiles().get(0).getMpOutputJson() == null 
						|| com.att.sales.nexxus.constant.CommonConstants.FAIL.equalsIgnoreCase(nrd.getNxOutputFiles().get(0).getStatus())) {
					nxRequests.add(nrd.getNxReqId());
				}
			}
			
			if(inProgress) {
				logger.info("InrBetaGenerateNxsiteId is not done as inr processing is in progress");
				return result;
			}else {
				if(nxRequests.size() > 0) {
					logger.info("InrBetaGenerateNxsiteId is not done as output json is not populated for these requests OR json generation is failed :: {}", nxRequests.toString());
					if(!inProgress) {
						falloutDetailsImpl.setNxGroupStatus(nxRequestDetails, nxRequestGroup);
					}
				}
			}

			Map<String, List<CircuitSiteDetails>> cktSiteMap = new HashMap<String, List<CircuitSiteDetails>>();
			AtomicInteger nxSiteIdCounter = null;
			NxDesignAudit nxDesignAudit = nxDesignAuditRepository.findByNxRefIdAndTransaction(
					nxReqGrpId.getNxSolutionDetail().getNxSolutionId(), MyPriceConstants.REQUEST_SITE_ID_REF);
			if (map!=null && map.containsKey("maxNxsiteid")) {
				nxSiteIdCounter = new AtomicInteger(Integer.parseInt(String.valueOf(map.get("maxNxsiteid"))));
			} else {
				if (nxDesignAudit != null) {
					nxSiteIdCounter = new AtomicInteger(Integer.parseInt(nxDesignAudit.getData()));
				} else {
					nxSiteIdCounter = new AtomicInteger(1);
				}
			}
			prepareIglooCktSiteMap(cktSiteMap, nxReqId);
			
			//This call captures existing nxsiteid
			logger.info("Preparing map of circuit and site details for solutionId {}", nxReqGrpId.getNxSolutionDetail().getNxSolutionId());
			List<Long> requestIds =  allNxRequestDetails.stream().filter(req ->  req.getNxOutputFiles() != null && req.getNxOutputFiles().size() > 0 
					&& (StringConstants.CONSTANT_Y.equalsIgnoreCase(req.getNxOutputFiles().get(0).getNxSiteIdInd())
							|| com.att.sales.nexxus.constant.CommonConstants.REGENERATE_NXSITEID.equalsIgnoreCase(req.getNxOutputFiles().get(0).getNxSiteIdInd()))).map(n->n.getNxReqId()).collect(Collectors.toList());
			if(CollectionUtils.isNotEmpty(requestIds)) {
				List<Object[]> cktNxsiteIdData =nxOutputFileRepository.fetchCircuitIdAndNxsiteId(requestIds);
				for(Object[] o : cktNxsiteIdData) {
					String cktId = (String) o[0];
					List<CircuitSiteDetails> circuits = cktId != null ? cktSiteMap.entrySet().stream().filter(c -> c.getKey().trim().equalsIgnoreCase(cktId.trim())).map(Map.Entry::getValue).findAny().orElse(null) : null;
					List<CircuitSiteDetails> circuitSiteDetailsList = null;	
					if(CollectionUtils.isEmpty(circuits)) {
						circuitSiteDetailsList = new ArrayList<CircuitSiteDetails>();
						CircuitSiteDetails circuitSiteDetails = new CircuitSiteDetails();
						circuitSiteDetails.setId(((BigDecimal) o[1]).longValue());
						circuitSiteDetailsList.add(circuitSiteDetails);
						//to put values for nxsiteidZ
						if(o.length>2 && null!=o[2]) {
							CircuitSiteDetails circuitSiteDetailsZ = new CircuitSiteDetails();
							circuitSiteDetailsZ.setId(((BigDecimal) o[2]).longValue());
							circuitSiteDetailsZ.setEndType("Z");
							circuitSiteDetailsList.add(circuitSiteDetailsZ);
						}
					}
							
					if(CollectionUtils.isNotEmpty(circuitSiteDetailsList) && cktId != null)
						cktSiteMap.put(cktId, circuitSiteDetailsList);
				}
			}
			List<NxRequestDetails> requests =  allNxRequestDetails.stream().filter(req -> req.getNxOutputFiles() != null && req.getNxOutputFiles().size() > 0 
					&& (StringConstants.CONSTANT_N.equalsIgnoreCase(req.getNxOutputFiles().get(0).getNxSiteIdInd())
							|| com.att.sales.nexxus.constant.CommonConstants.REGENERATE_NXSITEID.equalsIgnoreCase(req.getNxOutputFiles().get(0).getNxSiteIdInd()))).collect(Collectors.toList());
			if(CollectionUtils.isNotEmpty(requests)) {
				logger.info("Calling generateNxSiteId to generate nxsiteid for solutionId {}", nxReqGrpId.getNxSolutionDetail().getNxSolutionId());
				generateNxSiteId(requests, cktSiteMap, null, MyPriceConstants.SOURCE_INR, isGenerateNxSiteId, nxSiteIdCounter);
			}
			
			if(nxDesignAudit != null) {
				nxDesignAudit.setData(String.valueOf(nxSiteIdCounter));
				nxDesignAudit.setModifedDate(new Date());
			}else {
				nxDesignAudit = new NxDesignAudit();
				nxDesignAudit.setData(String.valueOf(nxSiteIdCounter));
				nxDesignAudit.setTransaction(MyPriceConstants.REQUEST_SITE_ID_REF);
				nxDesignAudit.setNxRefId(nxReqGrpId.getNxSolutionDetail().getNxSolutionId());
				nxDesignAudit.setCreatedDate(new Date());
			}
			nxDesignAuditRepository.saveAndFlush(nxDesignAudit);
			
			Long endTime=System.currentTimeMillis() - currentTime;
			logger.info("End : InrBetaGenerateNxsiteId for req id {} took {} millisec", nxReqId,(endTime - startTime));
			
		
			
		}catch(Exception e) {
			logger.info("Exception occured in Quality check for the request {}",nxReqId +" error :: "+ e.getMessage());
			e.printStackTrace();
		}
		return result;
	} 
	
	public List<Object> getCircuits(Object request, String path) {
		if(request != null) {
			TypeRef<List<Object>> mapType = new TypeRef<List<Object>>() {};
			List<Object> results = jsonPathUtil.search(request, path, mapType);
			if(!CollectionUtils.isEmpty(results)) {
				return results;
			}
		}
		return null;
	}
	
	public void generateNxSiteId(List<NxRequestDetails> nxRequestDetails, Map<String, List<CircuitSiteDetails>> cktSiteMap, Map<String, List<Object>> cktLocations, String flowType,
			boolean isGenerateNxSiteId, AtomicInteger nxSiteIdCounter) {
		List<NxOutputFileModel> nxOutputFileModelList = new ArrayList<NxOutputFileModel>();
		JsonNode request = null;
		boolean isStatusChange = false;
		List<NxLookupData> copyStatusLookup = nxMyPriceRepositoryServce.getItemDescFromLookup(com.att.sales.nexxus.constant.CommonConstants.NEXXUS_COPY_STATUS, StringConstants.CONSTANT_Y);
		for(NxRequestDetails nxDetails : nxRequestDetails) {
			List<NxOutputFileModel> nxOutputFileModels = nxOutputFileRepository.findByNxReqId(nxDetails.getNxReqId());
			NxOutputFileModel nxOutputFileModel = nxOutputFileModels.get(0);
		
			//	List<NxValidationRules> validationRules = nxValidationRulesRepository.findByValidationGroupAndOfferAndActiveAndFlowType("SITE_ID_PATH",  nxDetails.getProduct(), StringConstants.CONSTANT_Y, flowType);
			List<NxValidationRules> validationRules = nxValidationRulesRepository.findByValidationGroupAndOfferAndActiveAndFlowType("SITE_ID_PATH",  nxDetails.getProduct(), StringConstants.CONSTANT_Y, nxDetails.getFlowType());
			try {
				request = mapper.readTree(nxOutputFileModel.getMpOutputJson());
			} catch (Exception e) {
				logger.info("Exception occured in Quality check : while converting to jsonnode {}",e.getMessage());
			}
			if(isGenerateNxSiteId) {
				for(NxValidationRules rules : validationRules) {
					if(rules.getDataPath().contains(",")) {
						process(request.at(rules.getDataPath().split(",")[0]), rules, cktSiteMap, cktLocations, nxSiteIdCounter);
					}else {
						process(request, rules, cktSiteMap, cktLocations, nxSiteIdCounter);
					}
				}
			}

			try {
				if(isGenerateNxSiteId) {
					nxOutputFileModel.setMpOutputJson(mapper.writeValueAsString(request));
					nxOutputFileModel.setNxSiteIdInd(StringConstants.CONSTANT_Y);
					nxOutputFileModel.setModifiedDate(new Timestamp(System.currentTimeMillis()));
					updateNxSiteIdToCdir(nxOutputFileModel, request);
					nxOutputFileModelList.add(nxOutputFileModel);
				}
				// persist site details
				if(cktLocations == null) {
					List<Object> siteAddress = new ArrayList<>();
					Set<String> siteIds = new HashSet<String>();
					//prepareSiteData(nxDetails.getProduct(), siteAddress, nxOutputFileModel.getMpOutputJson(), siteIds, flowType);
					prepareSiteData(nxDetails.getProduct(), siteAddress, nxOutputFileModel.getMpOutputJson(), siteIds, nxDetails.getFlowType());
					saveNxSolutionSite(siteAddress, nxDetails); 
				}
			} catch (Exception e) {
				logger.info("Exception occured in Quality check : while converting to jsonnode {}",e.getMessage());
			}
			
			// Update request status for copied solutions
			isStatusChange = updateCopyStatus(copyStatusLookup, nxDetails);
			request = null;
		
		}
		if(isStatusChange) {
			nxRequestDetailsRepository.saveAll(nxRequestDetails);
		}
		if(CollectionUtils.isNotEmpty(nxOutputFileModelList)) {
			nxOutputFileRepository.saveAll(nxOutputFileModelList);
			nxOutputFileRepository.flush();
		}
	}
	
	protected void updateNxSiteIdToCdir(NxOutputFileModel nxOutputFileModel, JsonNode request) {
		//generate NXSITEMATCHINGID to nxSiteId map
		Map<Integer, JsonNode> nxSiteMatchingMap = new HashMap<>();
		updateNxSiteIdToCdirHelper(request, nxSiteMatchingMap);
		//using NXSITEMATCHINGID to nxSiteId map to update cdir data
		try {
			JsonNode cdir = mapper.readTree(nxOutputFileModel.getCdirData());
			for (JsonNode row : cdir.path("mainSheet")) {
				if (row.has(InrIntermediateJsonGenerator.NXSITEMATCHINGID) && nxSiteMatchingMap.containsKey(row.path(InrIntermediateJsonGenerator.NXSITEMATCHINGID).asInt())) {
					ObjectNode rowObj = (ObjectNode) row;
					Iterator<Entry<String, JsonNode>> fields = nxSiteMatchingMap.get(row.path(InrIntermediateJsonGenerator.NXSITEMATCHINGID).asInt()).fields();
					fields.forEachRemaining(entry -> {
						rowObj.set(entry.getKey(), entry.getValue());
					});
				}
			}
			nxOutputFileModel.setCdirData(cdir.toString());
		} catch (IOException e) {
			logger.error("Error in parsing cdir data", e);
		}
	}

	protected void updateNxSiteIdToCdirHelper(JsonNode root, Map<Integer, JsonNode> nxSiteMatchingMap) {
		if (root.has("nxSiteId")) {
			ObjectNode nxSiteInfo = mapper.createObjectNode();
			nxSiteInfo.set("nxSiteId", root.get("nxSiteId"));
			if (root.has("nxSiteIdZ")) {
				nxSiteInfo.set("nxSiteIdZ", root.get("nxSiteIdZ"));
			}
			if (root.has("endPointType")) {
				nxSiteInfo.set("endPointType", root.get("endPointType"));
			}
			nxSiteMatchingMap.put(root.path(InrIntermediateJsonGenerator.NXSITEMATCHINGID).asInt(), nxSiteInfo);
			return;
		}
		for (JsonNode n : root) {
			updateNxSiteIdToCdirHelper(n, nxSiteMatchingMap);
		}
	}

	private boolean updateCopyStatus(List<NxLookupData> copyStatusLookup, NxRequestDetails nxRequestDetails) {
		for(NxLookupData copyStatus : copyStatusLookup) {
			if(Long.valueOf(copyStatus.getItemId()).longValue() == nxRequestDetails.getStatus().longValue()) {
				nxRequestDetails.setStatus(Long.valueOf(copyStatus.getDescription()));
				return true;
			}
		}
		return false;
	}
	
	public void prepareSiteData(String productName, List<Object> siteAddress, Object outputJson, Set<String> processedSites, String flowType) {
		logger.info("Start prepareSiteData");
		List<NxValidationRules> siteDataRules = nxValidationRulesRepository.findByValidationGroupAndOfferAndActiveAndFlowType("MP_CLEAN_SAVE", productName, StringConstants.CONSTANT_Y, flowType);
		if(CollectionUtils.isNotEmpty(siteDataRules)) {
			for(NxValidationRules nxValidationRule : siteDataRules) {
				//EPLSWAN
				if ("USRP".equals(nxValidationRule.getFlowType()) && "EPLSWAN".equals(nxValidationRule.getOffer())) {
					List<Object> circuits = getCircuits(outputJson, nxValidationRule.getDataPath());
					if (circuits != null) {
						for (Object ckt : circuits) {
							JsonNode cktData = mapper.valueToTree(ckt);
							Map<Object, Object> map = new HashMap<>();
							if (MyPriceConstants.SOURCE_FMO.equalsIgnoreCase(flowType)) {
								map.put("siteInfoSource", MyPriceConstants.SOURCE_ADOPT);
							} else if (MyPriceConstants.SOURCE_USRP.equalsIgnoreCase(flowType)) {
								map.put("siteInfoSource", MyPriceConstants.SOURCE_INR.toLowerCase());
							} else {
								map.put("siteInfoSource", flowType.toLowerCase());
							}
							map.put("validationStatus", CommonConstants.VALID);
							if (nxValidationRule.getSubDataPath() != null) {
								List<Object> locations = new ArrayList<Object>();
								try {
									locations = getCircuits(mapper.writeValueAsString(ckt),
											nxValidationRule.getSubDataPath());
								} catch (JsonProcessingException e) {
									logger.info("exception while processing {}", e.getMessage());
								}
								if (CollectionUtils.isNotEmpty(locations)) {
									for (Object loc : locations) {
										Map<String, String> locaMapA = new HashMap<>();
										Map<String, String> locaMapZ = new HashMap<>();
										if (nxValidationRule.getSubData() != null) {
											LinkedHashMap<String, String> rulesMap = (LinkedHashMap<String, String>) nexxusJsonUtility.convertStringJsonToMap(nxValidationRule.getSubData());
											for(Entry<String, String> ruleMap : rulesMap.entrySet()) {
												boolean isLocA=false;
		                                        boolean isLocZ=false;
												JsonNode locData = mapper.valueToTree(loc);
												String[] values = null;
												if (("LocA").equals(ruleMap.getKey())) {
													values = ruleMap.getValue().split(";");
													isLocA = true;
												}
												if (("LocZ").equals(ruleMap.getKey())) {
													values = ruleMap.getValue().split(";");
													isLocZ = true;
												}
												for (String val : values) {
													String[] data = val.split(":");
													String value = "";
													if(data[1].contains("#")) {
														for(String tag : data[1].split("#")) {
															if(data.length>3) {
																if(!locData.path(tag).isMissingNode()) {
																	if(value.isEmpty()) {
																		value = locData.path(tag).asText();
																	}else {
																		value += ("SPACE".equalsIgnoreCase(data[3]) ? " " : data[3]) + (locData.path(tag).asText().isEmpty() ? cktData.path(tag).asText() : locData.path(tag).asText());
																	}
																}else if(tag.startsWith("$.")) {
																	if(value.isEmpty()) {
																		value = nexxusJsonUtility.getValue(locData, tag) != null ? String.valueOf(nexxusJsonUtility.getValue(locData, tag)) : "";
																	}else {
																		value += ("SPACE".equalsIgnoreCase(data[3]) ? " " : data[3]) + nexxusJsonUtility.getValue(locData, tag) != null ? String.valueOf(nexxusJsonUtility.getValue(locData, tag)) : "";
																	}
																}else if(!cktData.path(tag).isMissingNode()){
																	if(value.isEmpty()) {
																		value = cktData.path(tag).asText();
																	}else {
																		value += ("SPACE".equalsIgnoreCase(data[3]) ? " " : data[3]) + cktData.path(tag).asText();
																	}
																}
															}
														}
													}else {
														if(data[1].startsWith("$.")) {
															value = nexxusJsonUtility.getValue(locData, data[1]) != null ? String.valueOf(nexxusJsonUtility.getValue(locData, data[1])) : "";
														}else {
															value = locData.path(data[1]).asText();
														}
													}
													if(data.length > 2 && value.isEmpty()) {
														if(data[2].contains("-")) {
															if(isLocA)
																value = data[2].split("-")[0] + "-" + (locaMapA.get(data[2].split("-")[1]) != null ? locaMapA.get(data[2].split("-")[1]) : map.get(data[2].split("-")[1]));
															if(isLocZ)
																value = data[2].split("-")[0] + "-" + (locaMapZ.get(data[2].split("-")[1]) != null ? locaMapZ.get(data[2].split("-")[1]) : map.get(data[2].split("-")[1]));
																
														}else{
															value = data[2];
														}
													}
													if(isLocA)
														locaMapA.put(data[0], value);
													if(isLocZ)
														locaMapZ.put(data[0], value);
												}
												if(isLocA && !locaMapA.containsKey("address") ) {
													addressMethod(locaMapA, true);
												} 
												if(isLocZ && !locaMapZ.containsKey("address") ) {
													addressMethod(locaMapZ, false);
												} 
												if(isLocA)
													siteAddress.add(locaMapA);
												if(isLocZ)
													siteAddress.add(locaMapZ);
											} 
												
										  } 
											
										}
									}
								}
								cktData = null;
							}
							
						}
					} 
				else {
				List<Object> circuits = getCircuits(outputJson, nxValidationRule.getDataPath());
				if(circuits != null) {
					for(Object ckt : circuits) {
						JsonNode cktData = mapper.valueToTree(ckt);
						// process ckt level data nxSiteId:nxSiteId;country:CktCountryCd
						Map<Object, Object> map = new HashMap<>();
						if(MyPriceConstants.SOURCE_FMO.equalsIgnoreCase(flowType)) {
							map.put("siteInfoSource", MyPriceConstants.SOURCE_ADOPT);
						}else if(MyPriceConstants.SOURCE_USRP.equalsIgnoreCase(flowType)) {
							map.put("siteInfoSource", MyPriceConstants.SOURCE_INR.toLowerCase());
						}else {
							map.put("siteInfoSource", flowType.toLowerCase());
						}
						map.put("validationStatus", CommonConstants.VALID);
						if(nxValidationRule.getValue() != null) {
							String[] values = nxValidationRule.getValue().split(";");
							for(String val : values) {
								String[] data = val.split(":");
								String value = "";
								if(data[1].contains("#")) {
									for(String tag : data[1].split("#")) {
										if(!cktData.path(tag).isMissingNode()) {
											if(data.length>3) {
												if(value.isEmpty()) {
													value = cktData.path(tag).asText();
												}else {
													value += ("SPACE".equalsIgnoreCase(data[3]) ? " " : data[3])  + cktData.path(tag).asText();
												}
											}
										}
									}
								}else {
									value = cktData.path(data[1]).asText();
								}
								if(data.length > 2 && value.isEmpty()) {
									if(data[2].contains("-")) {
										value = data[2].split("-")[0] + "-" + map.get(data[2].split("-")[1]);
									}else {
										value = data[2];
									}
								}
								map.put(data[0], value);
							}
						}
						// populate location info level
						if(nxValidationRule.getSubDataPath() != null) {
							List<Object> locations = new ArrayList<Object>();
							try {
								locations = getCircuits(mapper.writeValueAsString(ckt), nxValidationRule.getSubDataPath());
							} catch (JsonProcessingException e) {
								logger.info("exception while processing {}" ,e.getMessage());
							}
							if(CollectionUtils.isNotEmpty(locations)) {
								for(Object loc : locations) {
									Map<Object, Object> map1 = new HashMap<>();
									Map<Object, Object> map2 = new HashMap<>();
									Map<String, String> clli = new HashMap<String, String>();
									if(nxValidationRule.getSubData() != null) {
										JsonNode locData = mapper.valueToTree(loc);
										String[] values = nxValidationRule.getSubData().split(";");
										for(String val : values) {
											String[] data = val.split(":");
											String value = "";
											if(data[1].contains("#")) {
												for(String tag : data[1].split("#")) {
													if(data.length>3) {
														if(!locData.path(tag).isMissingNode()) {
															if(value.isEmpty()) {
																value = locData.path(tag).asText();
															}else {
																value += ("SPACE".equalsIgnoreCase(data[3]) ? " " : data[3]) + (locData.path(tag).asText().isEmpty() ? cktData.path(tag).asText() : locData.path(tag).asText());
															}
														}else if(tag.startsWith("$.")) {
															if(value.isEmpty()) {
																value = nexxusJsonUtility.getValue(locData, tag) != null ? String.valueOf(nexxusJsonUtility.getValue(locData, tag)) : "";
															}else {
																value += ("SPACE".equalsIgnoreCase(data[3]) ? " " : data[3]) + nexxusJsonUtility.getValue(locData, tag) != null ? String.valueOf(nexxusJsonUtility.getValue(locData, tag)) : "";
															}
														}else if(!cktData.path(tag).isMissingNode()){
															if(value.isEmpty()) {
																value = cktData.path(tag).asText();
															}else {
																value += ("SPACE".equalsIgnoreCase(data[3]) ? " " : data[3]) + cktData.path(tag).asText();
															}
														}
													}
												}
											}else {
												if(data[1].startsWith("$.")) {
													value = nexxusJsonUtility.getValue(locData, data[1]) != null ? String.valueOf(nexxusJsonUtility.getValue(locData, data[1])) : "";
												}else {
													value = locData.path(data[1]).asText();
												}
											}
											if(data.length > 2 && value.isEmpty()) {
												if(data[2].contains("-")) {
													value = data[2].split("-")[0] + "-" + (map1.get(data[2].split("-")[1]) != null ? map1.get(data[2].split("-")[1]) : map.get(data[2].split("-")[1]));
												}else{
													value = data[2];
												}
											}
											map1.put(data[0], value);

											if(data[1].equalsIgnoreCase("CLLIAEnd")) {
												clli.put("CLLIAEnd", value);
											}else if(data[1].equalsIgnoreCase("CLLIZEnd")) {
												clli.put("CLLIZEnd", value);
											}
											
										}
										
									}
									map2.putAll(map);
									map2.putAll(map1);
									if(!map2.containsKey("address")) {
										StringBuffer address = new StringBuffer();
										if(map2.get("addressLine") != null && !map2.get("addressLine").toString().isEmpty()) {
											address.append(map2.get("addressLine").toString());
										}
										if(map2.get("city") != null && !map2.get("city").toString().isEmpty()) {
											if(address.length() != 0) {
												address.append("," + map2.get("city").toString());
											}else {
												address.append(map2.get("city").toString());
											}
										}
										if(map2.get("state") != null && !map2.get("state").toString().isEmpty()) {
											if(address.length() != 0) {
												address.append("," + map2.get("state").toString());
											}else {
												address.append(map2.get("state").toString());
											}
										}
										if(map2.get("postalCode") != null && !map2.get("postalCode").toString().isEmpty()) {
											if(address.length() != 0) {
												address.append("," + map2.get("postalCode").toString());
											}else {
												address.append(map2.get("postalCode").toString());
											}
										}
										if(map2.get("country") != null && !map2.get("country").toString().isEmpty()) {
											if(address.length() != 0) {
												address.append("," + map2.get("country").toString());
											}else {
												address.append(map2.get("country").toString());
											}
										}
										map2.put("address", address);
										address = null;
									}
									Set<String> siteIds = new HashSet<String>(){{
										if(map2.get("nxSiteId") != null)
											add(String.valueOf(map2.get("nxSiteId")));
										if(map2.get("nxSiteIdZ") != null)
											add(String.valueOf(map2.get("nxSiteIdZ")));
									}};
									if(!processedSites.containsAll(siteIds)){
										if(!clli.isEmpty()) {
											map2.remove("cllizend");
											Map<Object, Object> serviceMap = new HashMap<Object, Object>();
											serviceMap.putAll(map2);
											serviceMap.remove("nxSiteIdZ");
											serviceMap.put("popClli", clli.get("CLLIAEnd"));
											siteAddress.add(serviceMap);
											if(clli.get("CLLIZEnd") != null) {
												Map<Object, Object> serviceMap1 = new HashMap<Object, Object>();
												serviceMap1.putAll(map2);
												//serviceMap1.remove("nxSiteId");
												serviceMap1.put("nxSiteId", serviceMap1.get("nxSiteIdZ"));
												serviceMap1.put("popClli", clli.get("CLLIZEnd"));
												serviceMap1.put("name", "TBD-" + serviceMap1.get("nxSiteIdZ"));
												serviceMap1.remove("nxSiteIdZ");
												siteAddress.add(serviceMap1);
											}
										}else {
											siteAddress.add(map2);
										}
										processedSites.addAll(siteIds);
									}
								}
							}
							
						}else {
							siteAddress.add(map);
						}
						cktData = null;
					}
				 }
			  }
			}
		}
		logger.info("End prepareSiteData");
	}

	private void addressMethod(Map<String, String> locaMap, boolean isLocA) {
		final String city = isLocA ? "cityAEnd" : "cityZEnd";
		final String state = isLocA ? "stateAEnd" : "stateZEnd";
		final String postal = isLocA ? "postalcodeAEnd" : "postalcodeZEnd";
		final String country = isLocA ? "cityAEnd" : "cityZEnd";
		final String location = isLocA ? "addressAEnd": "addressZEnd";

		StringBuffer address = new StringBuffer();
		if(locaMap.get("addressLine") != null && !locaMap.get("addressLine").toString().isEmpty()) {
			address.append(locaMap.get("addressLine").toString());
		}
		if(locaMap.get(city) != null && !locaMap.get(city).toString().isEmpty()) {
			if(address.length() != 0) {
				address.append("," + locaMap.get(city).toString());
			}else {
				address.append(locaMap.get(city).toString());
			}
		}
		if(locaMap.get(state) != null && !locaMap.get(state).toString().isEmpty()) {
			if(address.length() != 0) {
				address.append("," + locaMap.get(state).toString());
			}else {
				address.append(locaMap.get(state).toString());
			}
		}
		if(locaMap.get(postal) != null && !locaMap.get(postal).toString().isEmpty()) {
			if(address.length() != 0) {
				address.append("," + locaMap.get(postal).toString());
			}else {
				address.append(locaMap.get(postal).toString());
			}
		}
		if(locaMap.get(country) != null && !locaMap.get(country).toString().isEmpty()) {
			if(address.length() != 0) {
				address.append("," + locaMap.get(country).toString());
			}else {
				address.append(locaMap.get(country).toString());
			}
		}
		locaMap.put(location, address.toString());
		address = null;
	}
	
	public String getSiteAddressJson(List<Object> siteAddress) {
		ObjectMapper obj = new ObjectMapper();
		//String siteAddressBlock = null;
		try {
			return obj.writerWithDefaultPrettyPrinter().writeValueAsString(siteAddress);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			logger.info(e.getMessage());
		}
		return null;
	}
	
	
	public void process(JsonNode node, NxValidationRules validationRule,
			Map<String, List<CircuitSiteDetails>> cktSiteMap, Map<String, List<Object>> cktLocations, AtomicInteger nxSiteIdCounter) {
		String path = validationRule.getDataPath().contains(",") ? validationRule.getDataPath().split(",")[1] : validationRule.getDataPath();
		if(!node.at(path).isMissingNode()) { 
			generateNxsiteId(node.path(path).elements(), validationRule, cktSiteMap, cktLocations, nxSiteIdCounter);
		}else {
			Iterator<JsonNode> elements = node.elements();
			while(elements.hasNext()) {
				JsonNode element = elements.next();
				JsonNode data = element.at(path);
				if(!data.isMissingNode()) {				
					generateNxsiteId(data.elements(), validationRule, cktSiteMap, cktLocations, nxSiteIdCounter);
				}else if(element.isArray()) {
					process(element, validationRule, cktSiteMap, cktLocations, nxSiteIdCounter);
				}else if(element.isObject()) {
					process(element, validationRule, cktSiteMap, cktLocations, nxSiteIdCounter);
				}
			}
		}
	}
	
	public void generateNxsiteId(Iterator<JsonNode>  datas, NxValidationRules validationRule,
			Map<String, List<CircuitSiteDetails>> cktSiteMap, Map<String, List<Object>> cktLocations, AtomicInteger nxSiteIdCounter) {
		String[] siteAttriName = validationRule.getName() != null ? validationRule.getName().split(",") : null;
		String[] clli = validationRule.getSubData() != null ? validationRule.getSubData().split(",") : null;
		JsonNode cktNode = null;
		while(datas.hasNext()) {
			cktNode = datas.next();
			Object obj = nexxusJsonUtility.getValue(cktNode, validationRule.getValue());
			String cktId = obj != null ? String.valueOf(obj) : null;
			if(cktLocations != null) {
				ObjectNode objCkt = (ObjectNode) cktNode;
				if(cktId != null && cktLocations.containsKey(cktId.trim())) {
					objCkt.set(validationRule.getDescription(), mapper.valueToTree(cktLocations.get(cktId.trim())));
				}
			}else {
				List<CircuitSiteDetails> circuitSiteDetailsList = cktId != null ? cktSiteMap.entrySet().stream().filter(c -> c.getKey().trim().equalsIgnoreCase(cktId.trim())).map(Map.Entry::getValue).findAny().orElse(null) : null;
				if(CollectionUtils.isEmpty(circuitSiteDetailsList)) {
					circuitSiteDetailsList = new ArrayList<CircuitSiteDetails>();
					ObjectNode objCkt = (ObjectNode) cktNode;
					if(siteAttriName != null) {
						for(String attri : siteAttriName) {
							objCkt.put(attri, nxSiteIdCounter.incrementAndGet());
						}
					}
					CircuitSiteDetails cdA = new CircuitSiteDetails();
					cdA.setId(cktNode.path("nxSiteId").asLong());
					if(validationRule.getSubDataPath() != null) {
						JsonNode locationInfo = cktNode.at(validationRule.getSubDataPath());
						if(!locationInfo.isMissingNode()) {
							if(locationInfo.isArray()) {
								//String[] clli = validationRule.getSubData().split(",");
								for (JsonNode location : locationInfo) {
									if(clli != null) {
										cdA.setClli(location.path(clli[0]).isNull() ? null : location.path(clli[0]).asText());
									}
									cdA.setEndType("A");
								}
							}
						}
					}

					if(MyPriceConstants.EPLSWAN.equalsIgnoreCase(validationRule.getOffer()) &&
							MyPriceConstants.SOURCE_USRP.equalsIgnoreCase(validationRule.getFlowType())) {
						cdA.setEndType("A");
					}
					
					circuitSiteDetailsList.add(cdA);
					if(!cktNode.path("nxSiteIdZ").isMissingNode() && !cktNode.path("nxSiteIdZ").isNull()) {
						CircuitSiteDetails cdZ = new CircuitSiteDetails();
						cdZ.setId(cktNode.path("nxSiteIdZ").asLong());
						if(validationRule.getSubDataPath() != null) {
							JsonNode locationInfo = cktNode.at(validationRule.getSubDataPath());
							if(!locationInfo.isMissingNode()) {
								if(locationInfo.isArray()) {
									for (JsonNode location : locationInfo) {
										//String[] clli = validationRule.getSubData().split(",");
										if(clli != null) {
											cdZ.setClli(location.path(clli[1]).isNull() ? null : location.path(clli[1]).asText());
										}
										cdZ.setEndType("Z");
									}
								}
							}
						}
						circuitSiteDetailsList.add(cdZ);
						
						if(MyPriceConstants.EPLSWAN.equalsIgnoreCase(validationRule.getOffer()) &&
								MyPriceConstants.SOURCE_USRP.equalsIgnoreCase(validationRule.getFlowType())) {
							cdZ.setEndType("Z");
						}

					}
					if(cktId != null) {
						cktSiteMap.put(cktId, circuitSiteDetailsList);
					}
				}else {
					for(CircuitSiteDetails cktSite : circuitSiteDetailsList) {
						ObjectNode objCkt = (ObjectNode) cktNode;
						if("A".equalsIgnoreCase(cktSite.getEndType()) || cktSite.getEndType() == null) {
							objCkt.put("nxSiteId", cktSite.getId());
						}
						if("Z".equalsIgnoreCase(cktSite.getEndType())) {
							objCkt.put("nxSiteIdZ", cktSite.getId());
						}
					}
					
					if(!cktNode.has("nxSiteIdZ") && "DOMESTIC PL IOC".equalsIgnoreCase(validationRule.getOffer())) {
						ObjectNode objCkt = (ObjectNode) cktNode;
						objCkt.put("nxSiteIdZ", nxSiteIdCounter.incrementAndGet());
					}
					
					if(!cktNode.has("nxSiteIdZ") && MyPriceConstants.EPLS_WAN_OFFER_NAME.equalsIgnoreCase(validationRule.getOffer())
							&& MyPriceConstants.SOURCE_USRP.equalsIgnoreCase(validationRule.getFlowType())) {
						ObjectNode objCkt = (ObjectNode) cktNode;
						objCkt.put("nxSiteIdZ", nxSiteIdCounter.incrementAndGet());
					}

				}	
			}

		}
	}

	public void saveNxSolutionSite(List<Object> siteAddress, NxRequestDetails nxReqGrpId) {
		if(CollectionUtils.isNotEmpty(siteAddress)) {
			NxSolutionSite nxSolutionSite = nxSolutionSiteRepository.findByNxSolutionIdAndNxRequestGroupIdAndActiveYNAndNxReqId(nxReqGrpId.getNxSolutionDetail().getNxSolutionId(),
					nxReqGrpId.getNxRequestGroupId(), StringConstants.CONSTANT_Y, nxReqGrpId.getNxReqId());
			if(nxSolutionSite == null) {
				nxSolutionSite = new NxSolutionSite();
				nxSolutionSite.setNxSolutionId(nxReqGrpId.getNxSolutionDetail().getNxSolutionId());
				nxSolutionSite.setActiveYN(StringConstants.CONSTANT_Y);
				nxSolutionSite.setNxRequestGroupId(nxReqGrpId.getNxRequestGroupId());
				nxSolutionSite.setNxReqId(nxReqGrpId.getNxReqId());
			}
			nxSolutionSite.setSiteAddress(getSiteAddressJson(siteAddress));		
			nxSolutionSiteRepository.save(nxSolutionSite);
		}
	}

	/**
	 * this method is used to prepare map of cktid and nxsiteid for igloo quotes
	 * @param cktSiteMap
	 * @param nxReqId
	 */
	public void prepareIglooCktSiteMap(Map<String, List<CircuitSiteDetails>> cktSiteMap, Long nxReqId) {
		NxRequestDetails nxRequestDetails = nxRequestDetailsRepository.findByNxReqId(nxReqId);
		Long solutionId=nxRequestDetails.getNxSolutionDetail().getNxSolutionId();
		List<NxRequestDetails> nxRequestDetailList = nxRequestDetailsRepository.findByNxSolutionId(solutionId);
		List<NxLookupData> nxLookupdataList = nxLookupDataRepository
				.findByDatasetNameAndActive("IGLOO_SERVICE_GROUP_PRODUCTS", StringConstants.CONSTANT_Y);
		List<String> iglooProductgrpList = Arrays.asList(nxLookupdataList.get(0).getCriteria().split(","));
		List<NxRequestDetails> nxRequestDetilToProcess = nxRequestDetailList.stream()
				.filter(n -> iglooProductgrpList.contains(n.getProduct())).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(nxRequestDetilToProcess)) {
			List<NxAccessPricingData> nxAccessPricingDataList = nxaccessPricingDataRepo
					.findByNxSolutionId(solutionId);
			if(!CollectionUtils.isEmpty(nxAccessPricingDataList)) {
				for(NxAccessPricingData nxAccessPricingData : nxAccessPricingDataList){
					if (nxAccessPricingData.getCircuitId() != null && 
							org.apache.commons.lang.StringUtils.isNotBlank(nxAccessPricingData.getCircuitId())) {
						List<CircuitSiteDetails> csdList = new ArrayList<>();
						CircuitSiteDetails csd = new CircuitSiteDetails();
						csd.setId(nxAccessPricingData.getNxSiteId());
						csdList.add(csd);
						String circuitId=nxAccessPricingData.getCircuitId().replaceAll("\\s", "").replaceAll("\\.", "");
						cktSiteMap.put(circuitId, csdList);
					}
					
				};
			}
		}

	}
	
	public void inrBetaFractionalTDM(Long nxReqId) {
		NxRequestDetails nxRequestDetails = nxRequestDetailsRepository.findByNxReqIdAndActiveYn(nxReqId,
				StringConstants.CONSTANT_Y);
		List<NxOutputFileModel> nxOutputFileModels = nxOutputFileRepository.findByNxReqId(nxReqId);
		NxOutputFileModel nxOutputFileModel = nxOutputFileModels.get(0);
		JsonNode mpJsonNode = null;
		try {
			mpJsonNode = mapper.readTree(nxOutputFileModel.getMpOutputJson());
		} catch (Exception e) {
			logger.info("Exception occured in inr beta Quality check : while converting to jsonnode {}", e.getMessage());
		}

		if (InrConstants.USRP.equals(nxRequestDetails.getFlowType()) && isProductApplicableInrBetaFraactionalTDM(mpJsonNode,nxRequestDetails.getProduct())) {
			List<Object> ckt1 = getCircuits(mpJsonNode,tdmNonQualiFiedPortWWCircuitPath);
			Set<String>	tdmNonQualiFiedPortWWCircuitset=new HashSet<>();
			if(!CollectionUtils.isEmpty(ckt1)) {
				tdmNonQualiFiedPortWWCircuitset.addAll(ckt1.stream().map(object -> Objects.toString(object)).collect(Collectors.toSet()));
			}
			
			List<Object> ckt2 = getCircuits(mpJsonNode,tdmQualifiedWWCircuitPath);
			Set<String>	tdmQualifiedWWCircuitSet=new HashSet<>();
			if(!CollectionUtils.isEmpty(ckt2)) {
				tdmQualifiedWWCircuitSet.addAll(ckt2.stream().map(object -> Objects.toString(object)).collect(Collectors.toSet()));
			}
			Set<String> tdmFractionalWWcktSet = tdmNonQualiFiedPortWWCircuitset.stream().filter(((Predicate<String>)tdmQualifiedWWCircuitSet::contains)).collect(Collectors.toSet());
			String tdmFractionalWWcktString = String.join(",", tdmFractionalWWcktSet);
			Set<String>	tdmFractionalPortCktset=new HashSet<>();
			String a = "$..design[?(@.accessProductName=='TDM' && @.qualifyCircuit=='N' && @.accesscktid in [REPLACE])].[?(@.priceDetails..typeOfCharge contains 'P')].circuitId";
			String b=a.replaceAll("REPLACE", tdmFractionalWWcktString);
			List<Object> tdmFractionalPortCkt= getCircuits(mpJsonNode,b);
			if(!CollectionUtils.isEmpty(tdmFractionalPortCkt)) {
				tdmFractionalPortCktset.addAll(tdmFractionalPortCkt.stream().map(object -> Objects.toString(object)).collect(Collectors.toSet()));
			}

			updateInrBetaMpOutPutJsonFractionalPortckt(mpJsonNode,tdmFractionalPortCktset);
			nxOutputFileModel.setMpOutputJson(mpJsonNode.toString());
			nxOutputFileRepository.saveAndFlush(nxOutputFileModel);
		}else {
				logger.info("INR beta Fractional TDM  is not required for the request Id ::{}", nxRequestDetails.getNxReqId());
			}
	}
	
	
	private boolean isProductApplicableInrBetaFraactionalTDM(JsonNode mpJsonNode,String value) {
		boolean flag=false;
		if(value.equalsIgnoreCase("AVPN")) {
			List<Object> tdmckt = getCircuits(mpJsonNode,tdmCktId);
			if(null!=tdmckt) {
				flag=true;
			}
		}
		return flag;
	}

	public void updateInrBetaMpOutPutJsonFractionalPortckt(JsonNode node, Set<String> fractionalPortckt) {
		if (node.getNodeType() == JsonNodeType.ARRAY) {
			for (JsonNode element : node) {
				updateInrBetaMpOutPutJsonFractionalPortckt(element, fractionalPortckt);
			}
		} else if (node.getNodeType() == JsonNodeType.OBJECT) {
			if (node.hasNonNull("circuitId")) {
				String cktid = node.path("circuitId").asText();
				if (fractionalPortckt.contains(cktid)) {
					((ObjectNode) node).put("fractionalTDM", "Y");
				} 
			}
			for (Iterator<Entry<String, JsonNode>> i = node.fields(); i.hasNext();) {
				JsonNode child = i.next().getValue();
				updateInrBetaMpOutPutJsonFractionalPortckt(child, fractionalPortckt);
			}
		}
		
	}

}

