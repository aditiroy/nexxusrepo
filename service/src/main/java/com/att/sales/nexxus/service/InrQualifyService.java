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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
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
import com.att.sales.nexxus.dao.model.NxTDMKeyIdMapping;
import com.att.sales.nexxus.dao.model.NxValidationRules;
import com.att.sales.nexxus.dao.repository.NxAccessPricingDataRepository;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxOutputFileRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxRequestGroupRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionSiteRepository;
import com.att.sales.nexxus.dao.repository.NxTDMKeyIdMappingRepo;
import com.att.sales.nexxus.dao.repository.NxValidationRulesRepository;
import com.att.sales.nexxus.fallout.service.FalloutDetailsImpl;
import com.att.sales.nexxus.inr.InrIntermediateJsonGenerator;
import com.att.sales.nexxus.model.CircuitSiteDetails;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.TypeRef;

@Component
public class InrQualifyService {
	
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
	private NxTDMKeyIdMappingRepo nxTDMKeyIdMappingRepo;
	

	@Value("${p8.local.destPath}")
	private String p8dLocalPath;
	
	private static Logger logger = LoggerFactory.getLogger(InrQualifyService.class);

	private static final String accessCircuitPath = "$..[?(@.priceDetails..typeOfCharge contains 'A')].circuitId";
	private static final String portCircuitPath = "$..[?(@.priceDetails..typeOfCharge contains 'P')].circuitId";
	private static final String cktIdPath="$..circuitId";


	public Map<String, Object> inrQualifyCheck(Long nxReqId, boolean isGenerateNxSiteId, Map<String, Object> map){
		Map<String, Object> result = new HashMap<String, Object>();
		Long currentTime = System.currentTimeMillis();
		Long startTime=System.currentTimeMillis() - currentTime;
		try {
			NxRequestDetails nxReqGrpId =  nxRequestDetailsRepository.findByNxReqIdAndActiveYn(nxReqId, StringConstants.CONSTANT_Y);
			logger.info("Start inrQualifyCheck for group id {}", nxReqGrpId.getNxRequestGroupId());
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
				logger.info("InrQualifyCheck is not done as inr processing is in progress");
				return result;
			}else {
				if(nxRequests.size() > 0) {
					logger.info("InrQualifyCheck is not done as output json is not populated for these requests OR json generation is failed :: {}", nxRequests.toString());
					if(!inProgress) {
						falloutDetailsImpl.setNxGroupStatus(nxRequestDetails, nxRequestGroup);
					}
				}
			}
			
			List<NxRequestDetails> accessRequest = nxRequestDetails.stream().
					filter(n -> MyPriceConstants.ACCESS_GROUP.equalsIgnoreCase(n.getNxRequestGroupName())).collect(Collectors.toList());
			List<NxRequestDetails> serviceRequest = nxRequestDetails.stream().filter(n -> 
			(MyPriceConstants.SERVICE_GROUP.equalsIgnoreCase(n.getNxRequestGroupName()) || 
					MyPriceConstants.SERVICE_ACCESS_GROUP.equalsIgnoreCase(n.getNxRequestGroupName()))).collect(Collectors.toList());
			
			
			Map<String, List<CircuitSiteDetails>> cktSiteMap = new HashMap<String, List<CircuitSiteDetails>>();
			String requestGroupName = nxLookupDataRepository.findDatasetNameByItemIdAndDatasetName(String.valueOf(nxRequestGroup.getGroupId()), MyPriceConstants.NX_REQ_GROUP_NAMES);
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
			Set<String> tdmRangeCkts = new HashSet<String>();
			Map<String,NxTDMKeyIdMapping> tdmCktKeyIdMap= new HashMap<String,NxTDMKeyIdMapping>();
			prepareIglooCktSiteMap(cktSiteMap, nxReqId);
			
			//This call captures existing nxsiteid
			logger.info("Calling generateNxSiteId to collect circuit and site details for solutionId {}", nxReqGrpId.getNxSolutionDetail().getNxSolutionId());
			List<NxRequestDetails> requests =  allNxRequestDetails.stream().filter(req -> (MyPriceConstants.SERVICE_GROUP.equalsIgnoreCase(req.getNxRequestGroupName()) || 
					MyPriceConstants.SERVICE_ACCESS_GROUP.equalsIgnoreCase(req.getNxRequestGroupName()))
					&& req.getNxOutputFiles() != null && req.getNxOutputFiles().size() > 0 
					&& (StringConstants.CONSTANT_Y.equalsIgnoreCase(req.getNxOutputFiles().get(0).getNxSiteIdInd())
							|| com.att.sales.nexxus.constant.CommonConstants.REGENERATE_NXSITEID.equalsIgnoreCase(req.getNxOutputFiles().get(0).getNxSiteIdInd()))).collect(Collectors.toList());
			if(CollectionUtils.isNotEmpty(requests)) {
				generateNxSiteId(requests, cktSiteMap, true, "SERVICE", null, MyPriceConstants.SOURCE_INR, isGenerateNxSiteId, nxSiteIdCounter, null,null, null);
			}
			requests =  allNxRequestDetails.stream().filter(req -> MyPriceConstants.ACCESS_GROUP.equalsIgnoreCase(req.getNxRequestGroupName())
					&& req.getNxOutputFiles() != null && req.getNxOutputFiles().size() > 0 
					&& (StringConstants.CONSTANT_Y.equalsIgnoreCase(req.getNxOutputFiles().get(0).getNxSiteIdInd())
							|| com.att.sales.nexxus.constant.CommonConstants.REGENERATE_NXSITEID.equalsIgnoreCase(req.getNxOutputFiles().get(0).getNxSiteIdInd()))).collect(Collectors.toList());
			if(CollectionUtils.isNotEmpty(requests)) {
				generateNxSiteId(requests, cktSiteMap, true, "ACCESS", null, MyPriceConstants.SOURCE_INR, isGenerateNxSiteId, nxSiteIdCounter, null,null, null);
			}
			logger.info("End Calling generateNxSiteId to collect circuit and site details for solutionId {}", nxReqGrpId.getNxSolutionDetail().getNxSolutionId());
			// end collecting existing nxsiteid
			Map<String, String> ddaMrc = new HashMap<String, String>();
			if(MyPriceConstants.ACCESS_GROUP.equalsIgnoreCase(requestGroupName)
					|| MyPriceConstants.SERVICE_GROUP.equalsIgnoreCase(requestGroupName)
					|| (accessRequest.size() == 0 && serviceRequest.size() >= 1)){
				logger.info("INR qualified check is not required for the group ::{}", requestGroupName); 
				List<NxRequestDetails>  requestWithOutNxSiteId =  nxRequestDetails.stream().filter(req -> 
						(com.att.sales.nexxus.constant.CommonConstants.INR_REQUEST_STATUS.contains(req.getStatus().longValue()))
						&&  req.getNxOutputFiles() != null && req.getNxOutputFiles().size() > 0 
						&& (StringConstants.CONSTANT_N.equalsIgnoreCase(req.getNxOutputFiles().get(0).getNxSiteIdInd())
							|| com.att.sales.nexxus.constant.CommonConstants.REGENERATE_NXSITEID.equalsIgnoreCase(req.getNxOutputFiles().get(0).getNxSiteIdInd()))).collect(Collectors.toList());
				if(MyPriceConstants.ACCESS_GROUP.equalsIgnoreCase(requestGroupName)) {
					logger.info("Calling generateNxSiteId to generate site id for solutionId {}", nxReqGrpId.getNxSolutionDetail().getNxSolutionId());
					generateNxSiteId(requestWithOutNxSiteId, cktSiteMap, false, "ACCESS", null, MyPriceConstants.SOURCE_INR, isGenerateNxSiteId, nxSiteIdCounter, tdmRangeCkts,tdmCktKeyIdMap, ddaMrc);
				}else {
					logger.info("Calling generateNxSiteId to generate site id for solutionId {}", nxReqGrpId.getNxSolutionDetail().getNxSolutionId());
					generateNxSiteId(requestWithOutNxSiteId, cktSiteMap, false, "SERVICE", null, MyPriceConstants.SOURCE_INR, isGenerateNxSiteId, nxSiteIdCounter, null,null, null);
				}
			}else {
				// if req is combination of access and service
				// retrieve ckt id from access req
				//applicable for INR flow
				if(InrConstants.INR.equalsIgnoreCase(nxReqGrpId.getFlowType())) {
					if(nxRequests.size() == 0 && !inProgress) {
						qualifyCheck(accessRequest, serviceRequest, nxReqGrpId.getNxSolutionDetail().getNxSolutionId(), nxRequestGroup);
						updateNxOutputFileCdirData(nxRequestDetails);
					}
				}
				
				// This call generates the nxSiteid			
				logger.info("Calling generateNxSiteId to generate site id for solutionId {}", nxReqGrpId.getNxSolutionDetail().getNxSolutionId());
				requests =  serviceRequest.stream().filter(req -> (com.att.sales.nexxus.constant.CommonConstants.INR_REQUEST_STATUS.contains(req.getStatus().longValue())) 
						&& req.getNxOutputFiles() != null && req.getNxOutputFiles().size() > 0 
						&& (StringConstants.CONSTANT_N.equalsIgnoreCase(req.getNxOutputFiles().get(0).getNxSiteIdInd())
								|| com.att.sales.nexxus.constant.CommonConstants.REGENERATE_NXSITEID.equalsIgnoreCase(req.getNxOutputFiles().get(0).getNxSiteIdInd()))).collect(Collectors.toList());
				generateNxSiteId(requests, cktSiteMap, false, "SERVICE", null, MyPriceConstants.SOURCE_INR, isGenerateNxSiteId, nxSiteIdCounter, null,null,null);
				// calling DDA group with all req :: on service req retrigger if service gets same ckt as DDA then override DDA nxSiteId
				requests =  accessRequest.stream().filter(req -> (com.att.sales.nexxus.constant.CommonConstants.INR_REQUEST_STATUS.contains(req.getStatus().longValue()))
						&& req.getNxOutputFiles() != null && req.getNxOutputFiles().size() > 0).collect(Collectors.toList());
				generateNxSiteId(requests, cktSiteMap, false, "ACCESS", null, MyPriceConstants.SOURCE_INR, isGenerateNxSiteId, nxSiteIdCounter, tdmRangeCkts,tdmCktKeyIdMap, ddaMrc);
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
			
			if(CollectionUtils.isNotEmpty(tdmRangeCkts)) {
				NxDesignAudit designAudit = nxDesignAuditRepository.findByNxRefIdAndTransaction(nxReqGrpId.getNxRequestGroupId(), MyPriceConstants.TDM_NXT1_CIRCUIT_ID);
				if(designAudit != null) {
					designAudit.setData(tdmRangeCkts.stream().collect(Collectors.joining("##")));
					designAudit.setModifedDate(new Date());
				}else {
					designAudit = new NxDesignAudit();
					designAudit.setData(tdmRangeCkts.stream().collect(Collectors.joining("##")));
					designAudit.setTransaction(MyPriceConstants.TDM_NXT1_CIRCUIT_ID);
					designAudit.setNxRefId(nxReqGrpId.getNxRequestGroupId());
					designAudit.setCreatedDate(new Date());
				}
				nxDesignAuditRepository.saveAndFlush(designAudit);
			}
			if(MapUtils.isNotEmpty(tdmCktKeyIdMap)) {
				List<NxTDMKeyIdMapping> nxTdmKeyList= new ArrayList<>();
	    		for (Map.Entry<String,NxTDMKeyIdMapping>  tdmCktMap : tdmCktKeyIdMap.entrySet()) {
	    			List<NxTDMKeyIdMapping> nxTDMKeyIdMappings = nxTDMKeyIdMappingRepo.findByNxGrpIdAndTdmNxKeyId(nxReqGrpId.getNxRequestGroupId(), tdmCktMap.getKey());
	    			NxTDMKeyIdMapping nxTDMKeyIdMapping = null;
	    			if(CollectionUtils.isNotEmpty(nxTDMKeyIdMappings)) {
	    				nxTDMKeyIdMapping = nxTDMKeyIdMappings.get(0);
	    			}else {
	    				nxTDMKeyIdMapping = new NxTDMKeyIdMapping();
	    			}
	    			NxTDMKeyIdMapping nxTdm=tdmCktMap.getValue();
	    			nxTDMKeyIdMapping.setNxGrpId(nxReqGrpId.getNxRequestGroupId());
	    			nxTDMKeyIdMapping.setNxKeyId(nxTdm.getNxKeyId());
	    			nxTDMKeyIdMapping.setTdmNxKeyId(nxTdm.getTdmNxKeyId());
	    			nxTDMKeyIdMapping.setNewNxKeyId(nxTdm.getNewNxKeyId());
	    			nxTDMKeyIdMapping.setQuantity(nxTdm.getQuantity());
	    			nxTDMKeyIdMapping.setNxt1CktId(nxTdm.getNxt1CktId());
	    			nxTdmKeyList.add(nxTDMKeyIdMapping);
				}
	    		nxTDMKeyIdMappingRepo.saveAll(nxTdmKeyList);
			}
			
			Long endTime=System.currentTimeMillis() - currentTime;
			logger.info("End : inrQualifyCheck and total duration {}, {}", (endTime - startTime), nxReqGrpId.getNxRequestGroupId());
			
		
			
		}catch(Exception e) {
			logger.info("Exception occured in Quality check for the request {}",nxReqId +" error :: "+ e.getMessage());
			e.printStackTrace();
		}
		return result;
	} 
	
	public void getDDAMrcData(JsonNode mpOutputJsonNode, Long nxSolutioId, Map<String, String> map) {
		NxDesignAudit designAudit = nxDesignAuditRepository.findByNxRefIdAndTransaction(nxSolutioId,
				MyPriceConstants.DDA_CKTS_MRC);
		if (designAudit == null) {
			designAudit = new NxDesignAudit();
			designAudit.setCreatedDate(new Date());
			designAudit.setData(map.toString());
		} else {
			Map<String, String> existingDataMap = Arrays.stream(designAudit.getData().substring(1, designAudit.getData().length() - 1).split(",")).map(s -> s.split("="))
					.collect(Collectors.toMap(s -> s[0].trim(), s -> s[1].trim()));
			map.putAll(existingDataMap);
			designAudit.setData(map.toString());
		}
		designAudit.setTransaction(MyPriceConstants.DDA_CKTS_MRC);
		designAudit.setNxRefId(nxSolutioId);
		designAudit.setModifedDate(new Date());
		nxDesignAuditRepository.saveAndFlush(designAudit);

	}
	
	public Map<String, Set<String>> getData(Object mpOutputJson, Set<String> data, Set<String> qualifiedData, NxRequestDetails nxRequestDetails) {
		Map<String, Set<String>> serviceCkts = new HashMap<String, Set<String>>();
			
		List<NxValidationRules> autoQualifyRules = nxValidationRulesRepository.findByValidationGroupAndOfferAndActiveAndFlowType("IGNORE_QUALIFY",  nxRequestDetails.getProduct(), StringConstants.CONSTANT_Y, MyPriceConstants.SOURCE_INR);
		
		List<NxValidationRules> qualifyData = nxValidationRulesRepository.findByValidationGroupAndOfferAndActiveAndFlowType("QUALIFY",  nxRequestDetails.getProduct(), StringConstants.CONSTANT_Y, MyPriceConstants.SOURCE_INR);
		
		for(NxValidationRules nxValidationRules : autoQualifyRules) {
			getValuesFromRequest(mpOutputJson, nxValidationRules.getDataPath(), qualifiedData);
		}
		
		for(NxValidationRules nxValidationRules : qualifyData) {
			Set<String> ckts = new HashSet<String>();
			getValuesFromRequest(mpOutputJson, nxValidationRules.getDataPath(), ckts);
			data.addAll(ckts);
			if(nxValidationRules.getDescription() != null && CollectionUtils.isNotEmpty(ckts)) {
				serviceCkts.put(nxValidationRules.getDescription(), ckts);
			}
		}
		return serviceCkts;
	}

	public void getValuesFromRequest(Object request, String path, Set<String> result) {
		if(request != null) {
			TypeRef<List<Object>> mapType = new TypeRef<List<Object>>() {};
			List<Object> results = jsonPathUtil.search(request, path, mapType);
			if(!CollectionUtils.isEmpty(results)) {
				result.addAll(results.stream().map(object -> Objects.toString(object)).map(String::trim).collect(Collectors.toSet()));
			}
		}
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
	
	public void generateNxSiteId(List<NxRequestDetails> nxRequestDetails, Map<String, List<CircuitSiteDetails>> cktSiteMap, boolean prepareSiteMap
			, String productType, Map<String, List<Object>> cktLocations, String flowType,
			boolean isGenerateNxSiteId, AtomicInteger nxSiteIdCounter, Set<String> tdmRangeCkts,Map<String ,NxTDMKeyIdMapping> tdmCktKeyIdMap, 
			Map<String, String> ddaMrc) {
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
						process(request.at(rules.getDataPath().split(",")[0]), rules, prepareSiteMap, productType, cktSiteMap, cktLocations, nxSiteIdCounter, tdmRangeCkts,tdmCktKeyIdMap,ddaMrc);
					}else {
						process(request, rules, prepareSiteMap, productType, cktSiteMap, cktLocations, nxSiteIdCounter, tdmRangeCkts,tdmCktKeyIdMap,ddaMrc);
					}
				}
			}

			if(!prepareSiteMap) {
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
			}
			
			// Update request status for copied solutions
			isStatusChange = updateCopyStatus(copyStatusLookup, nxDetails);
			if(MyPriceConstants.DOMESTIC_DEDICATED_ACCESS.equalsIgnoreCase(nxDetails.getProduct()) && ddaMrc != null && !ddaMrc.isEmpty()) {
				getDDAMrcData(request, nxDetails.getNxSolutionDetail().getNxSolutionId(), ddaMrc);
			}
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
		logger.info("End prepareSiteData");
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
	
	
	public void process(JsonNode node, NxValidationRules validationRule, boolean prepareSiteMap, String productType,
			Map<String, List<CircuitSiteDetails>> cktSiteMap, Map<String, List<Object>> cktLocations, AtomicInteger nxSiteIdCounter, Set<String> tdmRangeCkts,
			Map<String ,NxTDMKeyIdMapping> tdmCktKeyIdMap, Map<String, String> ddaMrc) {
		String path = validationRule.getDataPath().contains(",") ? validationRule.getDataPath().split(",")[1] : validationRule.getDataPath();
		if(!node.at(path).isMissingNode()) { 
			generateNxsiteId(node.path(path).elements(), validationRule, prepareSiteMap, productType, cktSiteMap, cktLocations, nxSiteIdCounter, tdmRangeCkts,tdmCktKeyIdMap, ddaMrc);
		}else {
			Iterator<JsonNode> elements = node.elements();
			while(elements.hasNext()) {
				JsonNode element = elements.next();
				JsonNode data = element.at(path);
				if(!data.isMissingNode()) {				
					generateNxsiteId(data.elements(), validationRule, prepareSiteMap, productType, cktSiteMap, cktLocations, nxSiteIdCounter, tdmRangeCkts,tdmCktKeyIdMap, ddaMrc);
				}else if(element.isArray()) {
					process(element, validationRule, prepareSiteMap, productType, cktSiteMap, cktLocations, nxSiteIdCounter, tdmRangeCkts,tdmCktKeyIdMap, ddaMrc);
				}else if(element.isObject()) {
					process(element, validationRule, prepareSiteMap, productType, cktSiteMap, cktLocations, nxSiteIdCounter, tdmRangeCkts,tdmCktKeyIdMap, ddaMrc);
				}
			}
		}
	}
	
	public void generateNxsiteId(Iterator<JsonNode>  datas, NxValidationRules validationRule, boolean prepareSiteMap, String productType,
			Map<String, List<CircuitSiteDetails>> cktSiteMap, Map<String, List<Object>> cktLocations, AtomicInteger nxSiteIdCounter, Set<String> tdmRangeCkts,
			Map<String,NxTDMKeyIdMapping> tdmCktKeyIdMap, Map<String, String> ddaMrc) {
		String[] siteAttriName = validationRule.getName() != null ? validationRule.getName().split(",") : null;
		String[] clli = validationRule.getSubData() != null ? validationRule.getSubData().split(",") : null;
		JsonNode cktNode = null;
		while(datas.hasNext()) {
			cktNode = datas.next();
			Object obj = nexxusJsonUtility.getValue(cktNode, validationRule.getValue());
			String cktId = obj != null ? String.valueOf(obj) : null;
			boolean isNxT1 = false;
			if("ACCESS".equalsIgnoreCase(productType)) {
				JsonNode locationInfo = cktNode.at(validationRule.getSubDataPath());
			
				if(locationInfo.isArray()) {
					if(prepareSiteMap) {
						List<CircuitSiteDetails> circuits = cktId != null ? cktSiteMap.entrySet().stream().filter(c -> c.getKey().trim().equalsIgnoreCase(cktId.trim())).map(Map.Entry::getValue).findAny().orElse(null) : null;
						if(CollectionUtils.isEmpty(circuits)) {
							List<CircuitSiteDetails> circuitSiteDetailsList = new ArrayList<CircuitSiteDetails>();
							for (JsonNode location : locationInfo) {
								if(!location.path("nxSiteId").isMissingNode() && !location.path("nxSiteId").isNull()) {
									CircuitSiteDetails circuitSiteDetails = new CircuitSiteDetails();
									circuitSiteDetails.setId(location.path("nxSiteId").asLong());
									circuitSiteDetails.setClli((location.path(validationRule.getSubData()).isMissingNode() || location.path(validationRule.getSubData()).isNull())  ? null : location.path(validationRule.getSubData()).asText());
									circuitSiteDetails.setEndType((location.path("endPointType").isMissingNode() || location.path("endPointType").isNull())  ? null : location.path("endPointType").asText());
									if("TDM".equalsIgnoreCase(validationRule.getSubOffer())){
										circuitSiteDetails.setSiteNpanxx((location.path("SiteNPANXX").isMissingNode() || location.path("SiteNPANXX").isNull())  ? null : location.path("SiteNPANXX").asText());
										circuitSiteDetails.setCustSrvgWireCtrCLLICd((location.path("CustSrvgWireCtrCLLICd").isMissingNode() || location.path("CustSrvgWireCtrCLLICd").isNull())  ? null : location.path("CustSrvgWireCtrCLLICd").asText());
										circuitSiteDetails.setNxKeyId((location.path("nxKeyId").isMissingNode() || location.path("nxKeyId").isNull())  ? null : location.path("nxKeyId").asText());
										circuitSiteDetails.setNxT1Qty(1);
									}
									circuitSiteDetailsList.add(circuitSiteDetails);
								}
								
							}
							if(CollectionUtils.isNotEmpty(circuitSiteDetailsList) && cktId != null)
								cktSiteMap.put(cktId, circuitSiteDetailsList);
						}
					}else {
						List<CircuitSiteDetails> circuitSiteDetailsList = cktId != null ? cktSiteMap.entrySet().stream().filter(c -> c.getKey().trim().equalsIgnoreCase(cktId.trim())).map(Map.Entry::getValue).findAny().orElse(null) : null;
						if("TDM".equalsIgnoreCase(validationRule.getSubOffer())){// && CollectionUtils.isEmpty(circuitSiteDetailsList)) {
							if(CollectionUtils.isEmpty(circuitSiteDetailsList)){
								cktfound:
								for (Map.Entry<String, List<CircuitSiteDetails>> entry : cktSiteMap.entrySet()) {
									if(cktId != null && entry.getKey().length() >= 10 && cktId.trim().length() >= 10 &&
											(entry.getKey().trim().substring(0, 10)).equalsIgnoreCase(cktId.trim().substring(0, 10))) {
										for(CircuitSiteDetails csd : entry.getValue()) {
											for (JsonNode location : locationInfo) {
												if((csd.getSiteNpanxx() != null && csd.getSiteNpanxx().equalsIgnoreCase((location.path("SiteNPANXX").isMissingNode() 
														|| location.path("SiteNPANXX").isNull())  ? null : location.path("SiteNPANXX").asText()))
														&& (csd.getCustSrvgWireCtrCLLICd() != null && csd.getCustSrvgWireCtrCLLICd().equalsIgnoreCase((location.path("CustSrvgWireCtrCLLICd").isMissingNode()
																|| location.path("CustSrvgWireCtrCLLICd").isNull())  ? null : location.path("CustSrvgWireCtrCLLICd").asText()))) {
													circuitSiteDetailsList = new ArrayList<CircuitSiteDetails>();
													circuitSiteDetailsList.add(csd);
													isNxT1 = true;
													String tdmNxKeyId=csd.getId() + "$" + (entry.getKey().trim().substring(0, 10)) + "$" + csd.getSiteNpanxx() + "$" + csd.getCustSrvgWireCtrCLLICd();
													tdmRangeCkts.add(csd.getId() + "$" + (entry.getKey().trim().substring(0, 10)) + "$" + csd.getSiteNpanxx() + "$" + csd.getCustSrvgWireCtrCLLICd());
													if(CollectionUtils.isNotEmpty(circuitSiteDetailsList) && cktId != null)
														cktSiteMap.put(cktId, circuitSiteDetailsList);
													//here when ckt2 with same tdmNxKeyId comes, hence qty is added plus 1
													if(tdmCktKeyIdMap.get(tdmNxKeyId)==null) {
														String qty=String.valueOf(csd.getNxT1Qty()+1);
														NxTDMKeyIdMapping nxTDMKeyIdMapping= new NxTDMKeyIdMapping();
														nxTDMKeyIdMapping.setNxKeyId(csd.getNxKeyId());
														nxTDMKeyIdMapping.setTdmNxKeyId(tdmNxKeyId);
														nxTDMKeyIdMapping.setQuantity(qty);
														nxTDMKeyIdMapping.setNewNxKeyId(csd.getNxKeyId()+"$"+qty);
														Set<String> nxt1CktIds = new HashSet<String>();
														nxt1CktIds.add(entry.getKey().trim());
														nxt1CktIds.add(cktId.trim());
														nxTDMKeyIdMapping.setNxt1CktId(nxt1CktIds.toString());
														tdmCktKeyIdMap.put(tdmNxKeyId,nxTDMKeyIdMapping);
														nxt1CktIds = null;
													}else if(tdmCktKeyIdMap.containsKey(tdmNxKeyId) && tdmCktKeyIdMap.get(tdmNxKeyId)!=null){
														NxTDMKeyIdMapping existingTDMKeyIdMapping=tdmCktKeyIdMap.get(tdmNxKeyId);
														Set<String> nxt1CktIds = existingTDMKeyIdMapping.getNxt1CktId() != null ? Stream.of(existingTDMKeyIdMapping.getNxt1CktId().substring(1, existingTDMKeyIdMapping.getNxt1CktId().length()-1).split(",")).map(n -> n.trim()).collect(Collectors.toSet()) : new HashSet<String>();
														if(CollectionUtils.isEmpty(nxt1CktIds) || !nxt1CktIds.contains(cktId.trim())) {
															String qty=existingTDMKeyIdMapping.getQuantity();
															String newqty=String.valueOf(Integer.valueOf(qty)+1);
															existingTDMKeyIdMapping.setNxKeyId(csd.getNxKeyId());
															existingTDMKeyIdMapping.setTdmNxKeyId(tdmNxKeyId);
															existingTDMKeyIdMapping.setQuantity(newqty);
															existingTDMKeyIdMapping.setNewNxKeyId(csd.getNxKeyId()+"$"+newqty);
															nxt1CktIds.add(cktId.trim());
															existingTDMKeyIdMapping.setNxt1CktId(nxt1CktIds.toString());
															tdmCktKeyIdMap.put(tdmNxKeyId, existingTDMKeyIdMapping);
														}
														nxt1CktIds = null;
													}
													break cktfound;
												}
													
											}
										}
										
									}
								}
							}else if(CollectionUtils.isNotEmpty(circuitSiteDetailsList)){
								cktfound:
								for (Map.Entry<String, List<CircuitSiteDetails>> entry : cktSiteMap.entrySet()) {
									if(cktId != null && !entry.getKey().trim().equalsIgnoreCase(cktId.trim())
											&& entry.getKey().length() >= 10 && cktId.trim().length() >= 10 &&
											(entry.getKey().trim().substring(0, 10)).equalsIgnoreCase(cktId.trim().substring(0, 10))) {
										for(CircuitSiteDetails csd : entry.getValue()) {
											for (JsonNode location : locationInfo) {
												if((csd.getSiteNpanxx() != null && csd.getSiteNpanxx().equalsIgnoreCase((location.path("SiteNPANXX").isMissingNode() 
														|| location.path("SiteNPANXX").isNull())  ? null : location.path("SiteNPANXX").asText()))
														&& (csd.getCustSrvgWireCtrCLLICd() != null && csd.getCustSrvgWireCtrCLLICd().equalsIgnoreCase((location.path("CustSrvgWireCtrCLLICd").isMissingNode()
																|| location.path("CustSrvgWireCtrCLLICd").isNull())  ? null : location.path("CustSrvgWireCtrCLLICd").asText()))) {
													circuitSiteDetailsList = new ArrayList<CircuitSiteDetails>();
													circuitSiteDetailsList.add(csd);
													isNxT1 = true;
													String tdmNxKeyId=csd.getId() + "$" + (entry.getKey().trim().substring(0, 10)) + "$" + csd.getSiteNpanxx() + "$" + csd.getCustSrvgWireCtrCLLICd();
													tdmRangeCkts.add(csd.getId() + "$" + (entry.getKey().trim().substring(0, 10)) + "$" + csd.getSiteNpanxx() + "$" + csd.getCustSrvgWireCtrCLLICd());
													if(CollectionUtils.isNotEmpty(circuitSiteDetailsList) && cktId != null)
														cktSiteMap.put(cktId, circuitSiteDetailsList);
													//here when ckt2 with same tdmNxKeyId comes, hence qty is added plus 1
													if(tdmCktKeyIdMap.get(tdmNxKeyId)==null) {
														String qty=String.valueOf(csd.getNxT1Qty()+1);
														NxTDMKeyIdMapping nxTDMKeyIdMapping= new NxTDMKeyIdMapping();
														nxTDMKeyIdMapping.setNxKeyId(csd.getNxKeyId());
														nxTDMKeyIdMapping.setTdmNxKeyId(tdmNxKeyId);
														nxTDMKeyIdMapping.setQuantity(qty);
														nxTDMKeyIdMapping.setNewNxKeyId(csd.getNxKeyId()+"$"+qty);
														Set<String> nxt1CktIds = new HashSet<String>();
														nxt1CktIds.add(entry.getKey().trim());
														nxt1CktIds.add(cktId.trim());
														nxTDMKeyIdMapping.setNxt1CktId(nxt1CktIds.toString());
														tdmCktKeyIdMap.put(tdmNxKeyId,nxTDMKeyIdMapping);
														nxt1CktIds = null;
													}else if(tdmCktKeyIdMap.containsKey(tdmNxKeyId) && tdmCktKeyIdMap.get(tdmNxKeyId)!=null){
														NxTDMKeyIdMapping existingTDMKeyIdMapping=tdmCktKeyIdMap.get(tdmNxKeyId);
														Set<String> nxt1CktIds = existingTDMKeyIdMapping.getNxt1CktId() != null ? Stream.of(existingTDMKeyIdMapping.getNxt1CktId().substring(1, existingTDMKeyIdMapping.getNxt1CktId().length()-1).split(",")).map(n -> n.trim()).collect(Collectors.toSet()) : new HashSet<String>();
														if(CollectionUtils.isEmpty(nxt1CktIds) || !nxt1CktIds.contains(cktId.trim())) {
															String qty=existingTDMKeyIdMapping.getQuantity();
															String newqty=String.valueOf(Integer.valueOf(qty)+1);
															existingTDMKeyIdMapping.setNxKeyId(csd.getNxKeyId());
															existingTDMKeyIdMapping.setTdmNxKeyId(tdmNxKeyId);
															existingTDMKeyIdMapping.setQuantity(newqty);
															existingTDMKeyIdMapping.setNewNxKeyId(csd.getNxKeyId()+"$"+newqty);
															nxt1CktIds.add(cktId.trim());
															existingTDMKeyIdMapping.setNxt1CktId(nxt1CktIds.toString());
															tdmCktKeyIdMap.put(tdmNxKeyId, existingTDMKeyIdMapping);
														}
														nxt1CktIds = null;
													}
													break cktfound;
												}
													
											}
										}
										
									}
								}
							}
								
						}
						if(CollectionUtils.isEmpty(circuitSiteDetailsList)) {
							List<CircuitSiteDetails> circuitSiteDetails = new ArrayList<CircuitSiteDetails>();
							for (int i = 0; i< locationInfo.size();  i++) {
								JsonNode location = locationInfo.get(i); 
								CircuitSiteDetails csd = new CircuitSiteDetails();
								ObjectNode loc = (ObjectNode) location;
								long siteId = nxSiteIdCounter.incrementAndGet();
								loc.put("nxSiteId", siteId);
								loc.put("endPointType", MyPriceConstants.endType[i%2]);
								csd.setId(siteId);
								csd.setClli((location.path(validationRule.getSubData()).isMissingNode() || location.path(validationRule.getSubData()).isNull())  ? null : location.path(validationRule.getSubData()).asText());
								csd.setEndType(MyPriceConstants.endType[i%2]);
								if("TDM".equalsIgnoreCase(validationRule.getSubOffer())){
									csd.setSiteNpanxx((location.path("SiteNPANXX").isMissingNode() || location.path("SiteNPANXX").isNull())  ? null : location.path("SiteNPANXX").asText());
									csd.setCustSrvgWireCtrCLLICd((location.path("CustSrvgWireCtrCLLICd").isMissingNode() || location.path("CustSrvgWireCtrCLLICd").isNull())  ? null : location.path("CustSrvgWireCtrCLLICd").asText());
									csd.setNxKeyId((location.path("nxKeyId").isMissingNode() || location.path("nxKeyId").isNull())  ? null : location.path("nxKeyId").asText());
									csd.setNxT1Qty(1);
								}
								circuitSiteDetails.add(csd);
								ddaMrc.put(cktId, location.findValue("NetRate").asText());
							}
							if(CollectionUtils.isNotEmpty(circuitSiteDetails) && cktId != null)
								cktSiteMap.put(cktId, circuitSiteDetails);
							
						} else {
							loc:
							for (int i = 0; i< locationInfo.size(); i++) {
								JsonNode location = locationInfo.get(i); 
								ObjectNode loc = (ObjectNode) location;
								ddaMrc.put(cktId, loc.findValue("NetRate").asText());
								for(CircuitSiteDetails cktDetails : circuitSiteDetailsList) {  
									if(cktDetails.getClli() != null && cktDetails.getClli().equalsIgnoreCase(loc.get(validationRule.getSubData()).asText())){
										loc.put("endPointType", cktDetails.getEndType());
										loc.put("nxSiteId", cktDetails.getId());
										if("TDM".equalsIgnoreCase(validationRule.getSubOffer()) && cktDetails.getCustSrvgWireCtrCLLICd() == null){
											cktDetails.setCustSrvgWireCtrCLLICd((loc.path("CustSrvgWireCtrCLLICd").isMissingNode() || loc.path("CustSrvgWireCtrCLLICd").isNull())  ? null : loc.path("CustSrvgWireCtrCLLICd").asText());
										}
										if("TDM".equalsIgnoreCase(validationRule.getSubOffer()) && cktDetails.getSiteNpanxx() == null){
											cktDetails.setSiteNpanxx((loc.path("SiteNPANXX").isMissingNode() || loc.path("SiteNPANXX").isNull())  ? null : loc.path("SiteNPANXX").asText());
										}
										if("TDM".equalsIgnoreCase(validationRule.getSubOffer()) && cktDetails.getNxKeyId() == null){
											cktDetails.setNxKeyId((loc.path("nxKeyId").isMissingNode() || loc.path("nxKeyId").isNull())  ? null : loc.path("nxKeyId").asText());
										}
										if("TDM".equalsIgnoreCase(validationRule.getSubOffer())) {
											cktDetails.setNxT1Qty(1); 
										}
										 
										continue loc;
									}else if(cktDetails.getClli() == null) {
										loc.put("nxSiteId", cktDetails.getId());
										loc.put("endPointType", MyPriceConstants.endType[i%2]);
										if("TDM".equalsIgnoreCase(validationRule.getSubOffer()) && cktDetails.getCustSrvgWireCtrCLLICd() == null){
											cktDetails.setCustSrvgWireCtrCLLICd((loc.path("CustSrvgWireCtrCLLICd").isMissingNode() || loc.path("CustSrvgWireCtrCLLICd").isNull())  ? null : loc.path("CustSrvgWireCtrCLLICd").asText());
										}
										if("TDM".equalsIgnoreCase(validationRule.getSubOffer()) && cktDetails.getSiteNpanxx() == null){
											cktDetails.setSiteNpanxx((loc.path("SiteNPANXX").isMissingNode() || loc.path("SiteNPANXX").isNull())  ? null : loc.path("SiteNPANXX").asText());
										}
										if("TDM".equalsIgnoreCase(validationRule.getSubOffer()) && cktDetails.getNxKeyId() == null){
											cktDetails.setNxKeyId((loc.path("nxKeyId").isMissingNode() || loc.path("nxKeyId").isNull())  ? null : loc.path("nxKeyId").asText());
										}
										if("TDM".equalsIgnoreCase(validationRule.getSubOffer())) {
											cktDetails.setNxT1Qty(1); 
										}

										continue loc;
									}else if("TDM".equalsIgnoreCase(validationRule.getSubOffer()) && isNxT1){
										loc.put("nxSiteId", cktDetails.getId());
										loc.put("endPointType", MyPriceConstants.endType[i%2]);
										if(cktDetails.getCustSrvgWireCtrCLLICd() == null){
											cktDetails.setCustSrvgWireCtrCLLICd((loc.path("CustSrvgWireCtrCLLICd").isMissingNode() || loc.path("CustSrvgWireCtrCLLICd").isNull())  ? null : loc.path("CustSrvgWireCtrCLLICd").asText());
										}
										if(cktDetails.getSiteNpanxx() == null){
											cktDetails.setSiteNpanxx((loc.path("SiteNPANXX").isMissingNode() || loc.path("SiteNPANXX").isNull())  ? null : loc.path("SiteNPANXX").asText());
										}
										if(cktDetails.getNxKeyId() == null){
											cktDetails.setNxKeyId((loc.path("nxKeyId").isMissingNode() || loc.path("nxKeyId").isNull())  ? null : loc.path("nxKeyId").asText());
										}
										if("TDM".equalsIgnoreCase(validationRule.getSubOffer())) {
											cktDetails.setNxT1Qty(1); 
										}

										continue loc;
									}
								}
							}
						}
					}
				}
			}else if("SERVICE".equalsIgnoreCase(productType)) {
				if(cktLocations != null) {
					ObjectNode objCkt = (ObjectNode) cktNode;
					if(cktId != null && cktLocations.containsKey(cktId.trim())) {
						objCkt.set(validationRule.getDescription(), mapper.valueToTree(cktLocations.get(cktId.trim())));
					}
				}else {
					if(prepareSiteMap) {
						if(!MyPriceConstants.NO_UNIQUE_KEY.equalsIgnoreCase(validationRule.getValue())) {
							List<CircuitSiteDetails> circuits = cktId != null ? cktSiteMap.entrySet().stream().filter(c -> c.getKey().trim().equalsIgnoreCase(cktId.trim())).map(Map.Entry::getValue).findAny().orElse(null) : null;
							if(CollectionUtils.isEmpty(circuits)) {
								if(!cktNode.path("nxSiteId").isMissingNode() && !cktNode.path("nxSiteId").isNull()) {
									List<CircuitSiteDetails> circuitSiteDetailsList = new ArrayList<CircuitSiteDetails>();
									
									CircuitSiteDetails cdA = new CircuitSiteDetails();
									cdA.setId(cktNode.path("nxSiteId").asLong());
									
									CircuitSiteDetails cdZ = null;
									if(!cktNode.path("nxSiteIdZ").isMissingNode()) {
										cdZ = new CircuitSiteDetails();
										cdZ.setId(cktNode.path("nxSiteIdZ").asLong());
									}
										
									if(validationRule.getSubDataPath() != null && productType.equalsIgnoreCase("SERVICE")) {
										JsonNode locationInfo = cktNode.at(validationRule.getSubDataPath());
										if(!locationInfo.isMissingNode()) {
											if(locationInfo.isArray()) {
												//String[] clli = validationRule.getSubData().split(",");
												for (JsonNode location : locationInfo) {
													if(clli != null) {
														cdA.setClli(location.path(clli[0]).isNull() ? null : location.path(clli[0]).asText());
													}
													cdA.setEndType("A");
													if(!cktNode.path("nxSiteIdZ").isMissingNode() || cktNode.path("nxSiteIdZ").isNull()) {
														if(clli != null) {
															cdZ.setClli(location.path(clli[1]).isNull() ? null : location.path(clli[1]).asText());
														}													
														cdZ.setEndType("Z");
														circuitSiteDetailsList.add(cdZ);
													}
												}
											}
										}
									}
									circuitSiteDetailsList.add(cdA);
									if(cktId != null)
										cktSiteMap.put(cktId, circuitSiteDetailsList);
								}
							}
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
							if(validationRule.getSubDataPath() != null && productType.equalsIgnoreCase("SERVICE")) {
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
							
							circuitSiteDetailsList.add(cdA);
							if(!cktNode.path("nxSiteIdZ").isMissingNode() && !cktNode.path("nxSiteIdZ").isNull()) {
								CircuitSiteDetails cdZ = new CircuitSiteDetails();
								cdZ.setId(cktNode.path("nxSiteIdZ").asLong());
								if(validationRule.getSubDataPath() != null && productType.equalsIgnoreCase("SERVICE")) {
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
							}
							if(cktId != null) {
								cktSiteMap.put(cktId, circuitSiteDetailsList);
							}
						}else {
							for(CircuitSiteDetails cktSite : circuitSiteDetailsList) {
								ObjectNode objCkt = (ObjectNode) cktNode;
								if("A".equalsIgnoreCase(cktSite.getEndType()) || cktSite.getEndType() == null) {
									objCkt.put("nxSiteId", cktSite.getId());
								}else if("Z".equalsIgnoreCase(cktSite.getEndType())) {
									objCkt.put("nxSiteIdZ", cktSite.getId());
								}
							}
							
							if(!cktNode.has("nxSiteIdZ") && "DOMESTIC PL IOC".equalsIgnoreCase(validationRule.getOffer())) {
								ObjectNode objCkt = (ObjectNode) cktNode;
								objCkt.put("nxSiteIdZ", nxSiteIdCounter.incrementAndGet());
							}
						}	
					}
				}
			}
		}
	}
	
	public void deleteDisQualifiedCkts(List<Long> nxReqId) {
		List<NxDesignAudit> nxDesignAudit = nxDesignAuditRepository.findByNxRefIdAndTransactions(nxReqId, MyPriceConstants.CIRCUITS);
		if(nxDesignAudit != null) {
			nxDesignAuditRepository.deleteAll(nxDesignAudit);
			logger.info("Disqualified circuits and qualified circuits are deleted for the request {}", nxReqId);
		}
	}
	
	public void populateLocationsInService(List<NxRequestDetails> accessRequest, List<NxRequestDetails> serviceRequest, boolean isGenerateNxSiteId) {
		// populate locations in service block
		Map<String, List<Object>> cktLocations = new HashMap<String, List<Object>>();
		for(NxRequestDetails ar : accessRequest) {
			List<NxValidationRules> nxValidationRules = nxValidationRulesRepository.findByValidationGroupAndOfferAndActiveAndFlowType("CIRCUIT_BLOCK", ar.getProduct(), StringConstants.CONSTANT_Y, MyPriceConstants.SOURCE_INR);
			if(CollectionUtils.isNotEmpty(nxValidationRules)) {
				List<NxOutputFileModel> nxOutputFileModel = nxOutputFileRepository.findByNxReqId(ar.getNxReqId());
				for(NxValidationRules nxValidationRule : nxValidationRules) {
					List<Object> circuits = getCircuits(nxOutputFileModel.get(0).getMpOutputJson(), nxValidationRule.getDataPath());
					if(circuits != null) {
						for(Object ckt : circuits) {
							if(nxValidationRule.getSubDataPath() != null) {
								Object obj = nexxusJsonUtility.getValue((ObjectNode) mapper.valueToTree(ckt), nxValidationRule.getValue());
								//String cktId = String.valueOf(obj);
								List<Object> locations = new ArrayList<Object>();
								try {
									locations = getCircuits(mapper.writeValueAsString(ckt), nxValidationRule.getSubDataPath());
								} catch (JsonProcessingException e) {
									logger.info(e.getMessage());
								}
								if(obj != null)
									cktLocations.put(String.valueOf(obj).trim(), locations);
							}
						}
					}
				}
			}
		}
		// This call is to add location block in service product
		if(!cktLocations.isEmpty())
			generateNxSiteId(serviceRequest, null, false, "SERVICE", cktLocations, MyPriceConstants.SOURCE_INR, isGenerateNxSiteId, null, null,null, null);
	}
	
	public void qualifyCheck(List<NxRequestDetails> accessRequest, List<NxRequestDetails> serviceRequest, Long solutionId, NxRequestGroup nxRequestGroup) {
		Set<String> accessData = new HashSet<String>();
		for(NxRequestDetails ar : accessRequest) {
			List<NxOutputFileModel> nxOutputFileModel = nxOutputFileRepository.findByNxReqId(ar.getNxReqId());
			getData(nxOutputFileModel.get(0).getMpOutputJson(), accessData, null, ar);
			
		}
		Set<String> serviceData = new HashSet<String>();
		Set<String> serviceQualifiedData = new HashSet<String>();
		boolean grpQualified = true;
		Set<String> qualifiedCkts = new HashSet<String>();;
		for(NxRequestDetails sr : serviceRequest) {
			List<NxOutputFileModel> nxOutputFileModel = nxOutputFileRepository.findByNxReqId(sr.getNxReqId());
			Map<String, Set<String>> serviceCkts = getData(nxOutputFileModel.get(0).getMpOutputJson(), serviceData, serviceQualifiedData, sr);
			
			// if no circuit ids
			if(serviceData.size() == 0 && serviceQualifiedData.size() == 0) {
				logger.info("Circuit ids not present in the service product { }", sr.getProduct());
				grpQualified = false;
				break;
			}else if(serviceData.size() == serviceQualifiedData.size()) {
				logger.info("All Circuit ids are to be ignored as defualt data present for the service product { }", sr.getProduct());
				grpQualified = true;
			}else {
				Set<String> toBeQualified = serviceData.stream().filter(((Predicate<String>)serviceQualifiedData::contains).negate()).collect(Collectors.toSet());
				// collect the ckt id which are not part of access ckt
				Set<String> notQualified = toBeQualified.stream().filter(((Predicate<String>)accessData::contains).negate()).collect(Collectors.toSet());
				Set<String> qualifiedData = toBeQualified.stream().filter(((Predicate<String>)accessData::contains)).collect(Collectors.toSet());
				if(CollectionUtils.isNotEmpty(qualifiedData)) {
					serviceCkts.forEach((prod, ckts) ->{
						NxDesignAudit nxDesignAudit = nxDesignAuditRepository.findByTransactionAndStatusAndNxRefId(MyPriceConstants.QUALIFIED_CIRCUITS, prod, sr.getNxReqId());
						if(nxDesignAudit == null) {
							nxDesignAudit = new NxDesignAudit();
							nxDesignAudit.setNxRefId(sr.getNxReqId());
							nxDesignAudit.setTransaction(MyPriceConstants.QUALIFIED_CIRCUITS);
							nxDesignAudit.setStatus(prod);
							nxDesignAudit.setNxSubRefId(String.valueOf(nxRequestGroup.getNxRequestGroupId()));
						}
						nxDesignAudit.setData(ckts.stream().filter(((Predicate<String>)qualifiedData::contains)).collect(Collectors.toSet()).toString());
						nxDesignAuditRepository.save(nxDesignAudit);
						qualifiedCkts.addAll(ckts.stream().filter(((Predicate<String>)qualifiedData::contains)).collect(Collectors.toSet()));
					});
				}
				if(notQualified.size() > 0) {
					// persist in design_audit table
					NxDesignAudit nxDesignAudit = nxDesignAuditRepository.findByNxRefIdAndTransaction(sr.getNxReqId(), MyPriceConstants.DISQUALIFIED_CIRCUITS);
					if(nxDesignAudit == null) {
						nxDesignAudit = new NxDesignAudit();
						nxDesignAudit.setNxRefId(sr.getNxReqId());
						nxDesignAudit.setTransaction(MyPriceConstants.DISQUALIFIED_CIRCUITS);
					}
					
					nxDesignAudit.setData(notQualified.toString());
					nxDesignAuditRepository.save(nxDesignAudit);
					logger.info("Circuit ids not matched with any of the access product circuit id for the product { }", sr.getProduct());
					grpQualified = false;
					//break;
				}else {
					if(grpQualified)
						grpQualified = true;
				}
			}
			serviceCkts= null;
		}
		if(CollectionUtils.isNotEmpty(qualifiedCkts)) {
			for(NxRequestDetails ar : accessRequest) {
					List<NxDesignAudit> nxDesignAudits = nxDesignAuditRepository.
							findByTransactionAndNxRefId(MyPriceConstants.QUALIFIED_CIRCUITS, ar.getNxReqId());
					NxDesignAudit nxDesignAudit;
					if(nxDesignAudits.isEmpty()) {
						nxDesignAudit = new NxDesignAudit();
						nxDesignAudit.setNxRefId(ar.getNxReqId());
						nxDesignAudit.setTransaction(MyPriceConstants.QUALIFIED_CIRCUITS);
						nxDesignAudit.setNxSubRefId(String.valueOf(nxRequestGroup.getNxRequestGroupId()));
					}else {
						nxDesignAudit=nxDesignAudits.get(0);
					}
					nxDesignAudit.setData(qualifiedCkts.toString());
					nxDesignAuditRepository.save(nxDesignAudit);
			}
		}
		if(grpQualified) {
			logger.info("Group is qualified {}", solutionId);
			nxRequestGroup.setStatus(MyPriceConstants.QUALIFIED);
			nxRequestGroup.setModifiedDate(new Date());
			nxRequestGroupRepository.save(nxRequestGroup);
		
		}else {
			logger.info("Group is not qualified {}", solutionId);
			nxRequestGroup.setStatus(MyPriceConstants.NOT_QUALIFIED);
			nxRequestGroup.setModifiedDate(new Date());
			nxRequestGroupRepository.save(nxRequestGroup);
		}
	}
	
	/*private void genNxSiteId(AtomicInteger nxSiteIdCounter, NxOutputFileModel model) throws IOException {
		JsonGenerator gen = null;
		 JsonParser jp = null;
		// p8dLocalPath = "C:\\Users\\ShruthiCJ\\Desktop\\";
		 Path sourcePath = Paths.get(p8dLocalPath).resolve(FilenameUtils.getName(String.valueOf(model.getId())+".json"));
		 Path destPath = Paths.get(p8dLocalPath).resolve(FilenameUtils.getName(String.valueOf(model.getId())+"withId.json"));
		try {
			// write to file
			Files.write(sourcePath, model.getMpOutputJson().getBytes());

			JsonFactory f = new MappingJsonFactory();
	        jp = f.createParser(new File(sourcePath.toString()));
	        jp.setCodec(new ObjectMapper());
	        JsonFactory fac = new JsonFactory();
	        gen =  fac.createGenerator(new File(destPath.toString()), JsonEncoding.UTF8);
	        JsonToken current = jp.nextToken();
	        gen.setCodec(new ObjectMapper());
	        gen.writeStartObject();
	        if (current != JsonToken.START_OBJECT) {
	            return;
	        }
	        while (jp.nextToken() != JsonToken.END_OBJECT) {
	            String fieldName = jp.getCurrentName();
	            current = jp.nextToken();
	            if ("DomesticDSODS1AccessInventory".equalsIgnoreCase(fieldName) || "DomesticEthernetAccessInventory".equalsIgnoreCase(fieldName)
	            		|| "DomesticDS3OCXAccessInventory".equalsIgnoreCase(fieldName) || "DomesticIOCInventory".equalsIgnoreCase(fieldName)
	            		|| "DomesticEthernetIOCInventory".equalsIgnoreCase(fieldName)) {
	            	JsonNode node = jp.readValueAsTree();
	            	for(JsonNode parent : node.path("CustomerAccountInfo")) {
	            		for(JsonNode child : parent.at("/CustomerSubAccountInfo/CustomerCircuitInfo")) {
	            			ObjectNode design = (ObjectNode) child;
	            			design.put("nxSiteIdZ1", nxSiteIdCounter.getAndIncrement());
	            		}
	            	}
	            	gen.writeObjectField(jp.getCurrentName(), node);
	            	node = null;
	            } else {
	                gen.writeObjectField(jp.getCurrentName(), jp.readValueAsTree());
	                jp.skipChildren();
	            }
	        }
	        gen.writeEndObject();
	      
	        model.setMpOutputJson(new String(Files.readAllBytes(destPath)));
		}catch(IOException e) {
			logger.info(e.getMessage());
		}finally {
			if(gen != null) {
				gen.close();
			}
			if(jp != null) {
				jp.close();
			}
			 Files.deleteIfExists(sourcePath);
			 Files.deleteIfExists(destPath);
		}
		
	}**/
	
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

	public Map<String, List<CircuitSiteDetails>> prepareInrCktSiteMap(Long nxSolutionId) {
		Map<String, List<CircuitSiteDetails>> cktSiteMap = new HashMap<String, List<CircuitSiteDetails>>();
		List<NxRequestDetails> nxRequestDetailList = nxRequestDetailsRepository.findByNxSolutionId(nxSolutionId);
		List<NxLookupData> nxLookupdataList = nxLookupDataRepository
				.findByDatasetNameAndActive("IGLOO_SERVICE_GROUP_PRODUCTS", StringConstants.CONSTANT_Y);
		List<String> iglooProductgrpList =((nxLookupdataList!=null) && CollectionUtils.isNotEmpty(nxLookupdataList))?
				Arrays.asList(nxLookupdataList.get(0).getCriteria().split(",")):new ArrayList<>();
		boolean isGenerateNxSiteId = true;
		NxDesignAudit nxDesignAudit = nxDesignAuditRepository.findByNxRefIdAndTransaction(nxSolutionId,
				MyPriceConstants.REQUEST_SITE_ID_REF);
		AtomicInteger nxSiteIdCounter = null;
		if (nxDesignAudit != null) {
			nxSiteIdCounter = new AtomicInteger(Integer.parseInt(nxDesignAudit.getData()));
		} else {
			nxSiteIdCounter = new AtomicInteger(1);
		}
		List<NxRequestDetails> nxRequestDetilToProcess = nxRequestDetailList.stream()
				.filter(n -> iglooProductgrpList.contains(n.getProduct())).collect(Collectors.toList());
		if (nxRequestDetailList != null && CollectionUtils.isNotEmpty(nxRequestDetilToProcess)) {
			List<NxRequestDetails> accessRequest = nxRequestDetilToProcess.stream()
					.filter(n -> MyPriceConstants.ACCESS_GROUP.equalsIgnoreCase(n.getNxRequestGroupName()) && MyPriceConstants.SOURCE_INR.equalsIgnoreCase(n.getFlowType()))
					.collect(Collectors.toList());
			List<NxRequestDetails> serviceRequest = nxRequestDetilToProcess.stream()
					.filter(n -> (MyPriceConstants.SERVICE_GROUP.equalsIgnoreCase(n.getNxRequestGroupName())
							|| MyPriceConstants.SERVICE_ACCESS_GROUP.equalsIgnoreCase(n.getNxRequestGroupName())) && MyPriceConstants.SOURCE_INR.equalsIgnoreCase(n.getFlowType()))
					.collect(Collectors.toList());

			List<NxRequestDetails> requests = serviceRequest.stream().filter(req -> req.getNxOutputFiles() != null
					&& req.getNxOutputFiles().size() > 0
					&& (StringConstants.CONSTANT_Y.equalsIgnoreCase(req.getNxOutputFiles().get(0).getNxSiteIdInd())
							|| com.att.sales.nexxus.constant.CommonConstants.REGENERATE_NXSITEID
									.equalsIgnoreCase(req.getNxOutputFiles().get(0).getNxSiteIdInd())))
					.collect(Collectors.toList());
			generateNxSiteId(requests, cktSiteMap, true, "SERVICE", null, MyPriceConstants.SOURCE_INR,
					isGenerateNxSiteId, nxSiteIdCounter, null,null,null);
			requests = accessRequest.stream().filter(req -> req.getNxOutputFiles() != null
					&& req.getNxOutputFiles().size() > 0
					&& (StringConstants.CONSTANT_Y.equalsIgnoreCase(req.getNxOutputFiles().get(0).getNxSiteIdInd())
							|| com.att.sales.nexxus.constant.CommonConstants.REGENERATE_NXSITEID
									.equalsIgnoreCase(req.getNxOutputFiles().get(0).getNxSiteIdInd())))
					.collect(Collectors.toList());
			generateNxSiteId(requests, cktSiteMap, true, "ACCESS", null, MyPriceConstants.SOURCE_INR,
					isGenerateNxSiteId, nxSiteIdCounter, null,null,null);

			List<Long> requestIds =  nxRequestDetilToProcess.stream().filter(req -> MyPriceConstants.SOURCE_USRP.equalsIgnoreCase(req.getFlowType()) && req.getNxOutputFiles() != null 
					&& req.getNxOutputFiles().size() > 0 
					&& (StringConstants.CONSTANT_Y.equalsIgnoreCase(req.getNxOutputFiles().get(0).getNxSiteIdInd())
							|| com.att.sales.nexxus.constant.CommonConstants.REGENERATE_NXSITEID.equalsIgnoreCase(req.getNxOutputFiles().get(0).getNxSiteIdInd())))
					.map(n->n.getNxReqId()).collect(Collectors.toList());
			if(CollectionUtils.isNotEmpty(requestIds)) {
				List<Object[]> cktNxsiteIdData =nxOutputFileRepository.fetchCircuitIdAndNxsiteIdByReqId(requestIds);
				for(Object[] o : cktNxsiteIdData) {
					String cktId = (String) o[0];
					List<CircuitSiteDetails> circuits = cktId != null ? cktSiteMap.entrySet().stream().filter(c -> c.getKey().trim().equalsIgnoreCase(cktId.trim())).map(Map.Entry::getValue).findAny().orElse(null) : null;
					List<CircuitSiteDetails> circuitSiteDetailsList = null;	
					if(CollectionUtils.isEmpty(circuits)) {
						circuitSiteDetailsList = new ArrayList<CircuitSiteDetails>();
						CircuitSiteDetails circuitSiteDetails = new CircuitSiteDetails();
						circuitSiteDetails.setId(((BigDecimal) o[1]).longValue());
						circuitSiteDetailsList.add(circuitSiteDetails);
					}
							
					if(CollectionUtils.isNotEmpty(circuitSiteDetailsList) && cktId != null)
						cktSiteMap.put(cktId, circuitSiteDetailsList);
				}
			}
		}
		return cktSiteMap;
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
				nxAccessPricingDataList.stream().forEach(nxAccessPricingData -> {
					if (nxAccessPricingData.getCircuitId() != null && 
							org.apache.commons.lang.StringUtils.isNotBlank(nxAccessPricingData.getCircuitId())) {
						List<CircuitSiteDetails> csdList = new ArrayList<>();
						CircuitSiteDetails csd = new CircuitSiteDetails();
						csd.setId(nxAccessPricingData.getNxSiteId());
						csdList.add(csd);
						String circuitIdModified=nxAccessPricingData.getCircuitId().replaceAll("\\s", "").replaceAll("\\.", "");
						cktSiteMap.put(circuitIdModified, csdList);
					}
					
				});
			}
		}

	}
	
	public void updateNxOutputFileCdirData(List<NxRequestDetails> nxRequestDetailsList) {
		logger.info("Start Updating cdir data for qualify check");
		for(NxRequestDetails nxRequestDetails: nxRequestDetailsList) {
			if(isProductQualified(nxRequestDetails.getProduct())) {
				List<NxOutputFileModel> nxOutputFiles = nxRequestDetails.getNxOutputFiles();
				ObjectMapper mapper = new ObjectMapper();
				if (nxOutputFiles != null) {
					for (NxOutputFileModel nxOutputFile : nxOutputFiles) {
						try {
							String cdirData = nxOutputFile.getCdirData();
							JsonNode cdirDataNode = mapper.readTree(cdirData);
							List<NxDesignAudit> designAudit = nxDesignAuditRepository.findByTransactionAndNxRefId(MyPriceConstants.QUALIFIED_CIRCUITS,nxRequestDetails.getNxReqId());
							if (!designAudit.isEmpty()) {
								logger.info("qualified circuit for solution");
								List<String> ckts = Arrays.asList(designAudit.get(0).getData().substring(1, designAudit.get(0).getData().length() - 1)
										.trim().split("\\s*,\\s*"));
								if(!cdirDataNode.path("mainSheet").isMissingNode()) {
									ArrayNode arrayNode = (ArrayNode) cdirDataNode.path("mainSheet");
									next:
									for (int i = arrayNode.size() - 1; i >= 0; i--) {
										Iterator<Entry<String, JsonNode>> elements = arrayNode.get(i).fields();
										boolean cktFlag = false;
										String qualifyCheck = null ;
										while (elements.hasNext()) {
											Entry<String, JsonNode> e = elements.next();
											if (e.getValue() != null ) {
												if(e.getKey().contains("CktId") || e.getKey().toUpperCase().contains("CIRCUITID")) {
													if (ckts.contains(e.getValue().asText().replaceAll("\\W", ""))) {
														cktFlag = true;
														qualifyCheck="Y";
													}else {
														cktFlag = true;
														qualifyCheck="N";
													}
												}
												if (cktFlag) {
													JsonNode node = arrayNode.get(i);
													ObjectNode obj = (ObjectNode) node;
													obj.put("qualifyCheck", qualifyCheck);			
													continue next;							
												}
											}
										}
									}
								}
							}else {
								logger.info("No qualified circuit for solution");
								if(!cdirDataNode.path("mainSheet").isMissingNode()) {
									ArrayNode arrayNode = (ArrayNode) cdirDataNode.path("mainSheet");
									for (int i = arrayNode.size() - 1; i >= 0; i--) {
										JsonNode node = arrayNode.get(i);
										ObjectNode obj = (ObjectNode) node;
										obj.put("qualifyCheck", "N");			
									}
								}
							}
							nxOutputFile.setCdirData(cdirDataNode.toString());
						} catch (IOException e) {
							logger.info("exception in converting cdid_data to JsonNode", e);
						}	
						nxOutputFileRepository.saveAndFlush(nxOutputFile);
		            }
				}
	
			}
		}
	}
	
	private boolean isProductQualified(String value) {
		boolean flag=false;
		if(value.equalsIgnoreCase("DOMESTIC DEDICATED ACCESS")) {
			flag=true;
		}else if(value.equalsIgnoreCase("DOMESTIC PL IOC")){
			flag=true;
		}else if(value.equalsIgnoreCase("ADI")){
			flag=true;
		}else if(value.equalsIgnoreCase("AVPN")){
			flag=true;
		}else if(value.equalsIgnoreCase("MIS/PNT")){
			flag=true;
		}
		return flag;
	}
	
	public void inrBetaQualifycheck(Long nxReqId) {
		NxRequestDetails nxRequestDetails = nxRequestDetailsRepository.findByNxReqIdAndActiveYn(nxReqId,
				StringConstants.CONSTANT_Y);
		if (InrConstants.USRP.equals(nxRequestDetails.getFlowType()) && isProductApplicableInrBetaQualified(nxRequestDetails.getProduct())) {
			List<NxOutputFileModel> nxOutputFileModels = nxOutputFileRepository.findByNxReqId(nxReqId);
			NxOutputFileModel nxOutputFileModel = nxOutputFileModels.get(0);
			JsonNode mpJsonNode = null;
			try {
				mpJsonNode = mapper.readTree(nxOutputFileModel.getMpOutputJson());
			} catch (Exception e) {
				logger.info("Exception occured in inr beta Quality check : while converting to jsonnode {}", e.getMessage());
			}
			List<Object> ckts = getCircuits(mpJsonNode,cktIdPath);
			Set<String>	circuitSet=new HashSet<>();
			if(!CollectionUtils.isEmpty(ckts)) {
				circuitSet.addAll(ckts.stream().map(object -> Objects.toString(object)).collect(Collectors.toSet()));
			}
			
			List<Object> accessCircuits = getCircuits(mpJsonNode,accessCircuitPath);
			Set<String>	accessCircuitSet=new HashSet<>();
			if(!CollectionUtils.isEmpty(accessCircuits)) {
				accessCircuitSet.addAll(accessCircuits.stream().map(object -> Objects.toString(object)).collect(Collectors.toSet()));
			}
			List<Object> portCircuits = getCircuits(mpJsonNode,	portCircuitPath);
			Set<String>	portCircuitSet=new HashSet<>();
			if(!CollectionUtils.isEmpty(portCircuits)) {
				portCircuitSet.addAll(portCircuits.stream().map(object -> Objects.toString(object)).collect(Collectors.toSet()));
			}

			Set<String> qualifiedCircuits = circuitSet.stream().filter(((Predicate<String>)accessCircuitSet::contains)).filter(((Predicate<String>)portCircuitSet::contains)).collect(Collectors.toSet());
			Set<String> notQualifiedCircuits = circuitSet.stream().filter(((Predicate<String>)qualifiedCircuits::contains).negate()).collect(Collectors.toSet());

			updateInrBetaMpOutPutJsonQualifyCheck(mpJsonNode, qualifiedCircuits);
			nxOutputFileModel.setMpOutputJson(mpJsonNode.toString());
			nxOutputFileRepository.saveAndFlush(nxOutputFileModel);
		} else {
			logger.info("INR beta qualified check is not required for the product ::{}", nxRequestDetails.getProduct());
		}
	}
	
	public void updateInrBetaMpOutPutJsonQualifyCheck(JsonNode node, Set<String> qualifiedCircuits) {
		if (node.getNodeType() == JsonNodeType.ARRAY) {
			for (JsonNode element : node) {
				updateInrBetaMpOutPutJsonQualifyCheck(element, qualifiedCircuits);
			}
		} else if (node.getNodeType() == JsonNodeType.OBJECT) {
			if (node.hasNonNull("circuitId")) {
				String cktid = node.path("circuitId").asText();
				if (qualifiedCircuits.contains(cktid)) {
					((ObjectNode) node).put("qualifyCircuit", "Y");
				} else {
					((ObjectNode) node).put("qualifyCircuit", "N");
				}
				JsonNode priceDetailsArray = node.path("priceDetails");
				for (JsonNode priceDetails : priceDetailsArray) {
					if (qualifiedCircuits.contains(cktid)) {
						((ObjectNode) priceDetails).put("qualifyCheck", "Y");
					} else {
						((ObjectNode) priceDetails).put("qualifyCheck", "N");
					}
				}
			}
			for (Iterator<Entry<String, JsonNode>> i = node.fields(); i.hasNext();) {
				JsonNode child = i.next().getValue();
				updateInrBetaMpOutPutJsonQualifyCheck(child, qualifiedCircuits);
			}
		}
	}
	
	private boolean isProductApplicableInrBetaQualified(String value) {
		boolean flag=false;
		if(value.equalsIgnoreCase("AVPN")) {
			flag=true;
		}else if(value.equalsIgnoreCase("GMIS")){
			flag=true;
		}else if(value.equalsIgnoreCase("MIS/PNT")){
			flag=true;
		}
		return flag;
	}
}

