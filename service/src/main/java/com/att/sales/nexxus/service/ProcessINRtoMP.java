package com.att.sales.nexxus.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.CustomJsonConstants;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxAccessPricingData;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxInrDesign;
import com.att.sales.nexxus.dao.model.NxInrDesignDetails;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.NxSiteidProductMapping;
import com.att.sales.nexxus.dao.model.NxTDMKeyIdMapping;
import com.att.sales.nexxus.dao.model.NxValidationRules;
import com.att.sales.nexxus.dao.model.SubmitToAnotherDeal;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxAccessPricingDataRepository;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxInrDesignRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxOutputFileRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxSiteidProductMappingRepo;
import com.att.sales.nexxus.dao.repository.NxTDMKeyIdMappingRepo;
import com.att.sales.nexxus.dao.repository.NxValidationRulesRepository;
import com.att.sales.nexxus.dao.repository.SubmitToAnotherDealRepository;
import com.att.sales.nexxus.fallout.service.FalloutDetailsImpl;
import com.att.sales.nexxus.inr.OutputJsonFallOutData;
import com.att.sales.nexxus.inr.OutputJsonService;
import com.att.sales.nexxus.model.RestErrors;
import com.att.sales.nexxus.model.UsageRule;
import com.att.sales.nexxus.model.UsageRuleObj;
import com.att.sales.nexxus.myprice.transaction.model.CreateTransactionResponse;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdatePricingUtilInr;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdateProcessingIglooService;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdateProcessingInrService;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdateRestProcessingService;
import com.att.sales.nexxus.myprice.transaction.service.ConfigDesignHelperService;
import com.att.sales.nexxus.myprice.transaction.service.MyPriceTransactionUtil;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.ConfigureResponse;

import groovy.util.Eval;

@Component
public class ProcessINRtoMP {

	@Autowired
	private SubmitToMyPriceService submitToMyPriceService;

	@Autowired
	private NexxusJsonUtility nexxusJsonUtility;

	@Autowired
	private NxMpDealRepository nxMpDealRepository;

	@Autowired
	private NxRequestDetailsRepository nxRequestDetailsRepository;

	@Autowired
	private NxDesignAuditRepository nxDesignAuditRepository;

	@Autowired
	private MyPriceTransactionUtil myPriceTransactionUtil;

	@Autowired
	private NxValidationRulesRepository nxValidationRulesRepository;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private NxInrDesignRepository nxInrDesignRepository;

	@Autowired
	private ConfigAndUpdateProcessingInrService configAndUpdateProcessingInrService;

	@Autowired
	private InrQualifyService inrQualifyService;

	@Autowired
	private ConfigAndUpdateProcessingIglooService configAndUpdateProcessingIglooService;

	@Autowired
	private ConfigDesignHelperService configDesignHelperService;

	@Autowired
	private MailServiceImpl mailServiceImpl;

	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;

	@Autowired
	private NexxusService nexxusService;

	@Autowired
	private FalloutDetailsImpl falloutDetailsImpl;

	@Autowired
	private ConfigAndUpdateRestProcessingService configAndUpdateRestProcessingService;

	@Autowired
	private NxAccessPricingDataRepository nxAccessPricingDataRepository;

	@Autowired
	private ConfigAndUpdatePricingUtilInr configAndUpdatePricingUtilInr;

	@Autowired
	private NxSiteidProductMappingRepo nxSiteidProductMappingRepo;

	@Autowired
	private NxTDMKeyIdMappingRepo nxTDMKeyIdMappingRepo;

	@Autowired
	private JsonPathUtil jsonPathUtil;
	
	@Autowired
	private NxOutputFileRepository nxOutputFileRepository;
	
	@Autowired
	private OutputJsonService outputJsonService;
	
	@Autowired
	private SubmitToAnotherDealRepository submitToAnotherDealRepository;

	
	private static Logger logger = LoggerFactory.getLogger(ProcessINRtoMP.class);

	@Value("${myprice.restField.documentId:0l}")
	private String documentId;
	
	public List<Long> sortGrpsOnPrecedence(List<Long> nxRequestGrpId){
		if(CollectionUtils.isNotEmpty(nxRequestGrpId)) {
			List<Object> data = nxRequestDetailsRepository.sortNxreqGrpId(nxRequestGrpId);
			return data.stream().map(m -> Long.parseLong(String.valueOf(m))).collect(Collectors.toList());
		}
		return new ArrayList<Long>();
	}

	// @Transactional
	public void process(NxSolutionDetail nxSolutionDetail, List<Long> nxRequestGrpId,
			Map<String, Object> requestMetaDataMap, Map<String, Object> configUpdateResMap, String source,
			CreateTransactionResponse createTransactionResponse, String prodName, boolean isReconfigure) {
		try {
			String nxMpStatusInd = requestMetaDataMap.containsKey("NX_MP_STATUS_IND")
					? requestMetaDataMap.get("NX_MP_STATUS_IND").toString()
					: null;
			String nxMpStatus = requestMetaDataMap.containsKey("CURRENT_DEALSTATUS")
					? requestMetaDataMap.get("CURRENT_DEALSTATUS").toString()
					: null;
			requestMetaDataMap.remove("CURRENT_DEALSTATUS");
			Long nxSolutionId = nxSolutionDetail.getNxSolutionId();
			StringBuffer printLog = new StringBuffer("Processing soluton to MP nxSolutionId " + nxSolutionId
					+ " nxTxnId : " + createTransactionResponse.getNxTransacId() + " myPriceTxnId : "
					+ createTransactionResponse.getMyPriceTransacId());
			logger.info("printLog {}", org.apache.commons.lang3.StringUtils.normalizeSpace(printLog.toString()));
			boolean iglooResult = true, inrResult = true, iglooRestResult = true, iglooSuccessResult = false,
					inrSuccessResult = false, isAddrEditSubmission = true;
			List<Long> nxReqIds = new ArrayList<Long>();
			if (nxRequestGrpId != null && !nxRequestGrpId.isEmpty()) {
				List<NxLookupData> lookUp = nxLookupDataRepository
						.findByDatasetName(MyPriceConstants.MP_REST_DESIGN_LIMIT_DATASET);
				int mpRestDesignLimit = Integer.parseInt(lookUp.get(0).getItemId());
				Map<Long, String> restRequests = new HashMap<Long, String>();
				Set<Long> usrpReq = new HashSet<Long>();
				Map<Long,String> reqProductMap = new HashMap<Long,String>();
				Map<String, NxInrDesign> designMap = new HashMap<String, NxInrDesign>();
				Map<Long, Set<Long>> reqWithGrps = getServiceAccessGroupId(nxSolutionId);
				Set<String> ackIds = new HashSet<String>();
				List<String> processedAckId = nxRequestDetailsRepository.findEdfAckIdByNxSolutionId(nxSolutionId);
				if (CollectionUtils.isNotEmpty(processedAckId)) {
					ackIds.addAll(processedAckId);
				}
				// get qualified ckts
				List<String> grps = nxRequestGrpId.stream().map(n -> String.valueOf(n)).collect(Collectors.toList());
				List<NxDesignAudit> designAudits = nxDesignAuditRepository.findByNxSubRefIdAndTransaction(grps,
						MyPriceConstants.QUALIFIED_CIRCUITS);
				grps = null;
				Map<Long, Map<String, Set<String>>> qualifiedCkts = new HashMap<Long, Map<String, Set<String>>>();
				if (CollectionUtils.isNotEmpty(designAudits)) {
					for (Long grpId : nxRequestGrpId) {
						Map<String, Set<String>> ckts = new HashMap<String, Set<String>>();
						List<NxDesignAudit> nxDesignAudit = designAudits.stream()
								.filter(n -> n.getNxSubRefId().equalsIgnoreCase(String.valueOf(grpId)))
								.collect(Collectors.toList());
						ckts = nxDesignAudit.stream()
								.collect(Collectors.toMap(x -> x.getStatus(),
										x -> new HashSet<String>(Arrays.asList(
												x.getData().substring(1, x.getData().length() - 1).split("\\s*,\\s*"))),
										((d1, d2) -> d1.addAll(d2) ? d1 : d2)));
						qualifiedCkts.put(grpId, ckts);
					}
				}
				Map<String, List<String>> productSiteIdMap = prepareProductSiteIdMap(
						nxSolutionDetail.getNxSolutionId());
				for (Long grpId : sortGrpsOnPrecedence(nxRequestGrpId)) {
					List<NxRequestDetails> nxRequestDetailList = nxRequestDetailsRepository
							.findbyNxSolutionDetailAndNxRequestGroupIdAndActiveYn(nxSolutionDetail, grpId,
									StringConstants.CONSTANT_Y);
					if (CollectionUtils.isNotEmpty(nxRequestDetailList)) {
						isAddrEditSubmission = false;
						findInrRestRequests(nxRequestDetailList, restRequests);
						usrpReq.addAll(nxRequestDetailList.stream().filter(n->MyPriceConstants.SOURCE_USRP.equalsIgnoreCase(n.getFlowType())).map(n->n.getNxReqId()).collect(Collectors.toSet()));
						List<String> prodNames = getDataSet(MyPriceConstants.APPEND_LOCATIONS_IN_SERVICE_DATASET);
						if (prodNames != null) {
							List<NxRequestDetails> accessRequest = nxRequestDetailList.stream().filter(
									n -> MyPriceConstants.ACCESS_GROUP.equalsIgnoreCase(n.getNxRequestGroupName()))
									.collect(Collectors.toList());
							long prodCount = nxRequestDetailList.stream()
									.filter(n -> prodNames.contains(n.getProduct())).count();
							if (accessRequest.size() > 0 && prodCount > 0) {
								if (StringConstants.CONSTANT_N
										.equalsIgnoreCase(restRequests.get(accessRequest.get(0).getNxReqId()))) {
									logger.info("Calling populateLocationsInService for solution {}",
											nxSolutionDetail.getNxSolutionId());
									List<NxRequestDetails> serviceRequest = nxRequestDetailList.stream()
											.filter(n -> MyPriceConstants.SERVICE_ACCESS_GROUP
													.equalsIgnoreCase(n.getNxRequestGroupName()))
											.collect(Collectors.toList());
									inrQualifyService.populateLocationsInService(accessRequest, serviceRequest, true);
								}
							}
						}
						NxDesignAudit tdmNxt1DesignAudit = nxDesignAuditRepository.findByNxRefIdAndTransaction(grpId,
								MyPriceConstants.TDM_NXT1_CIRCUIT_ID);
						Set<String> tdmNxt1Ckts = null;
						if (tdmNxt1DesignAudit != null) {
							tdmNxt1Ckts = new HashSet<String>(Arrays.asList(tdmNxt1DesignAudit.getData().split("##")));
						}
						Map<Long, List<Map<String, List<Long>>>> nxKeyIdNxSiteIds = new LinkedHashMap<Long, List<Map<String, List<Long>>>>();
						for (NxRequestDetails nxRequest : nxRequestDetailList) {
							reqProductMap.put(nxRequest.getNxReqId(),nxRequest.getProduct());
							List<NxOutputFileModel> nxOutputFileModels = nxRequest.getNxOutputFiles();
							// persist data in design and design_details from output_json
							Map<String, Object> paramMap = new HashMap<String, Object>();
							paramMap.put(MyPriceConstants.NX_GROUP_ID, grpId);
							paramMap.put(MyPriceConstants.NX_GROUP_IDS, reqWithGrps.get(nxRequest.getNxReqId()));
							paramMap.put("createTransactionRes", createTransactionResponse);
							paramMap.put(MyPriceConstants.NX_REQ_ID, nxRequest.getNxReqId());
							paramMap.put(MyPriceConstants.NX_TRANSACTION_ID,
									createTransactionResponse.getNxTransacId());
							paramMap.put(MyPriceConstants.OFFER_NAME, nxRequest.getProduct());
							paramMap.put(MyPriceConstants.MP_REST_DESIGN_LIMIT, mpRestDesignLimit);
							paramMap.put(MyPriceConstants.FLOWTYPE,nxRequest.getFlowType());
							if (isReconfigure) {
								paramMap.put("IS_RECONFIGURE", "inrReconfigure");
								paramMap.put("NX_MP_STATUS_IND", nxMpStatusInd);
							}
							List<NxValidationRules> nxValidationRules = null;

							if (StringConstants.CONSTANT_Y.equalsIgnoreCase(restRequests.get(nxRequest.getNxReqId()))) {
								nxValidationRules = nxValidationRulesRepository
										.findByValidationGroupAndOfferAndActiveAndFlowType("DESIGN_BLOCK",
												nxRequest.getProduct(), StringConstants.CONSTANT_Y, nxRequest.getFlowType());
							} else {
								nxValidationRules = nxValidationRulesRepository
										.findByValidationGroupAndOfferAndActiveAndFlowType("CIRCUIT_BLOCK",
												nxRequest.getProduct(), StringConstants.CONSTANT_Y, nxRequest.getFlowType());
							}

							paramMap.put("restMap", restRequests);
							if (CollectionUtils.isNotEmpty(nxValidationRules)) {
								for (NxValidationRules nxValidationRule : nxValidationRules) {

									List<NxValidationRules> skipCktRules = nxValidationRulesRepository
											.findByValidationGroupAndOfferAndActiveAndFlowTypeAndName(
													"MANDATORY_DESIGN_DATA", nxRequest.getProduct(),
													StringConstants.CONSTANT_Y, nxRequest.getFlowType(), nxValidationRule.getName());

									if ("SOLUTION_CATEGORY".equalsIgnoreCase(nxValidationRule.getDescription())) {
										submitToMyPriceService.persistDesignBasedSolutionCategory(nxValidationRule,
												paramMap, nxOutputFileModels, nxSolutionDetail, skipCktRules);
									} else if ("DESIGN_BLOCK".equalsIgnoreCase(nxValidationRule.getValidationGroup())) {
										List<Long> ids = nxRequestDetailsRepository
												.findNxRequestGroupIdByEdfAckIdAndActiveYn(nxRequest.getEdfAckId(),
														StringConstants.CONSTANT_Y);
										List<Entry<Long, Map<String, Set<String>>>> ckts = qualifiedCkts.entrySet()
												.stream().filter(n -> ids.contains(n.getKey()))
												.collect(Collectors.toList());
										Map<String, Set<String>> ckt = ckts.stream()
												.flatMap(m -> m.getValue().entrySet().stream())
												.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
										persistDesign(nxValidationRule, paramMap, nxOutputFileModels.get(0),
												nxSolutionDetail, skipCktRules, ckt, nxKeyIdNxSiteIds, tdmNxt1Ckts,
												productSiteIdMap);
										ckts = null;
										ckt = null;
									} else {

										List<Object> circuits = inrQualifyService.getCircuits(
												nxOutputFileModels.get(0).getMpOutputJson(),
												nxValidationRule.getDataPath());
										if (circuits != null) {
											paramMap.put(MyPriceConstants.PRODUCT_NAME, nxValidationRule.getName());
											paramMap.put(MyPriceConstants.SUB_DATA, nxValidationRule.getSubData());
											String values[] = nxValidationRule.getValue().split(":");
											for (int i = 0; i < circuits.size(); i++) {
												JsonNode cktData = mapper.valueToTree(circuits.get(i));
												ObjectNode cktObj = (ObjectNode) cktData;
												cktObj.put("product", nxValidationRule.getName());
												Object obj = nexxusJsonUtility.getValue(cktObj, values[0]);
												if (obj == null && values.length > 1) {
													obj = nexxusJsonUtility.getValue(cktObj, values[1]);
												}
												String cktId = String.valueOf(obj);
												paramMap.put(MyPriceConstants.CIRCUIT_ID, cktId);
												if (nxValidationRule.getSubDataPath() != null) {
													boolean designDataMissing = submitToMyPriceService
															.skipCktProcessing(skipCktRules, cktData, paramMap);
													if (!designDataMissing) {
														List<Object> locations = new ArrayList<Object>();
														try {
															String data = mapper.writeValueAsString(circuits.get(i));
															locations = inrQualifyService.getCircuits(data,
																	nxValidationRule.getSubDataPath());
														} catch (JsonProcessingException e) {
															e.printStackTrace();
														}
														if (CollectionUtils.isNotEmpty(locations)) {
															for (Object loc : locations) {
																JsonNode locNode = mapper.valueToTree(loc);
																if (MyPriceConstants.AVPN_OFFER_NAME.equalsIgnoreCase(
																		nxValidationRule.getOffer())) {
																	if (paramMap.containsKey(
																			MyPriceConstants.SUB_PRODUCT)) {
																		paramMap.remove(MyPriceConstants.SUB_PRODUCT);
																	}
																	obj = nexxusJsonUtility.getValue(locNode,
																			values[0]);
																	if (obj == null && values.length > 1) {
																		obj = nexxusJsonUtility.getValue(locNode,
																				values[1]);
																	}
																	cktId = String.valueOf(obj);
																	paramMap.put(MyPriceConstants.CIRCUIT_ID, cktId);
																	if (!locNode.path("accessSecondaryKey")
																			.isMissingNode()) {
																		paramMap.put(MyPriceConstants.SUB_PRODUCT,
																				MyPriceConstants.AVPN_LOCALACCESS);
																	}
																	List<JsonNode> nodes = new ArrayList<JsonNode>() {
																		{
																			add(locNode);
																		}
																	};
																	JsonNodeFactory nf = JsonNodeFactory.instance;
																	ArrayNode arrayNodes = new ArrayNode(nf, nodes);

																	cktObj.set(nxValidationRule.getSubData(),
																			arrayNodes);
																} else {
																	cktObj.set(nxValidationRule.getSubData(), locNode);
																}
																submitToMyPriceService.saveNxInrDesign(nxSolutionDetail,
																		cktData, paramMap, designMap);
															}
														}
													}
													if (!designMap.isEmpty()
															&& (designMap.size() == 20 || circuits.size() == (i + 1))) {
														nxInrDesignRepository.saveAll(designMap.values());
														designMap.clear();
													}
												} else {
													boolean designDataMissing = submitToMyPriceService
															.skipCktProcessing(skipCktRules, cktData, paramMap);
													if (!designDataMissing) {
														submitToMyPriceService.saveNxInrDesign(nxSolutionDetail,
																cktData, paramMap, designMap);
													}
													if (!designMap.isEmpty()
															&& (designMap.size() == 20 || circuits.size() == (i + 1))) {
														nxInrDesignRepository.saveAll(designMap.values());
														designMap.clear();
													}
												}

											}
										}
									}
								}
								ackIds.add(nxRequest.getEdfAckId());
							}

							if (paramMap.containsKey("skipCircuits") && paramMap.get("skipCircuits") != null) {
								NxDesignAudit nxDesignAudit = nxDesignAuditRepository.findByNxRefIdAndTransaction(
										nxRequest.getNxReqId(), MyPriceConstants.MISSING_MANDATORY_DESIGN_DATA);
								if (nxDesignAudit == null) {
									nxDesignAudit = new NxDesignAudit();
									nxDesignAudit.setTransaction(MyPriceConstants.MISSING_MANDATORY_DESIGN_DATA);
									nxDesignAudit.setStatus(MyPriceConstants.AUDIT_FALLOUT_STATUS);
									nxDesignAudit.setNxRefId(nxRequest.getNxReqId());
								}
								nxDesignAudit.setData(paramMap.get("skipCircuits").toString());
								nxDesignAuditRepository.save(nxDesignAudit);
							}
							nxReqIds.add(nxRequest.getNxReqId());
							paramMap = null;
						}
					}
				}
				persistProductSiteMap(nxSolutionId, productSiteIdMap);
				logger.info("Insert into nxInrDesign is completed for the solution {}", nxSolutionId);
				for (Long grpId : nxRequestGrpId) {
					try {
						nxRequestDetailsRepository.updateRequestStatus(grpId, 90L, 30L, new Date());
						nxRequestDetailsRepository.updateRequestStatus(grpId, 100L, 80L, new Date());
						nexxusService.updateNxSolution(nxSolutionId);
					} catch (Exception e) {
						logger.info("Exception during request status update {}", e.getMessage());
					}
				}
				logger.info("Request Status updated for the solution id {}", nxSolutionId);
				List<Long> restRequestIds = restRequests.entrySet().stream()
						.filter(n -> n.getValue().equalsIgnoreCase(StringConstants.CONSTANT_Y)).map(n -> n.getKey())
						.collect(Collectors.toList());
				List<Long> soapRequestIds = restRequests.entrySet().stream()
						.filter(n -> n.getValue().equalsIgnoreCase(StringConstants.CONSTANT_N)).map(n -> n.getKey())
						.collect(Collectors.toList());
				// soap req processing
				if (CollectionUtils.isNotEmpty(soapRequestIds)) {
					Map<String, Object> paramMap = new HashMap<String, Object>();
					paramMap.put(InrConstants.REQUEST_META_DATA_KEY, requestMetaDataMap);
					Map<String, Object> configRespMap = new HashMap<String, Object>();
					List<Object[]> nxInrDesigns = nxInrDesignRepository.findDesignByNxSolutionId(nxSolutionId,
							MyPriceConstants.MP_API_CONST, StringConstants.CONSTANT_Y, soapRequestIds);
					paramMap.put(MyPriceConstants.CONFIG_DESIGN_RESPONSE, new ConfigureResponse());
					paramMap.put(MyPriceConstants.CONFIG_RESPONSE_PROCESSING_DATA,
							new HashMap<String, Map<String, Object>>());
					paramMap.put(MyPriceConstants.MP_TRANSACTION_ID, createTransactionResponse.getMyPriceTransacId());
					paramMap.put(MyPriceConstants.NX_TRANSACTION_ID, createTransactionResponse.getNxTransacId());
					paramMap.put(MyPriceConstants.FLOW_TYPE, MyPriceConstants.SOURCE_INR);
					logger.info("nxInrDesigns size {}", nxInrDesigns.size());
					if (nxInrDesigns.size() == 0) {
						logger.info("No valid designs to submit to MP for the solution id {}", nxSolutionId);
						inrResult = false;
					} else {
						int i = 0;
						for (Object[] design : nxInrDesigns) {
							NxInrDesign nxInrDesign = nxInrDesignRepository
									.findByNxSolutionIdAndNxInrDesignIdAndActiveYN(nxSolutionId,
											Long.parseLong(String.valueOf(design[0])), StringConstants.CONSTANT_Y);
							Boolean isLastDesign = myPriceTransactionUtil.isLastDesign(i++, nxInrDesigns);
							paramMap.put(MyPriceConstants.IS_LAST_DESIGN, isLastDesign);
							configRespMap = configAndUpdateProcessingInrService
									.configAndUpdatePricing(createTransactionResponse, nxInrDesign, paramMap);
							if (!inrSuccessResult) {
								inrSuccessResult = paramMap.containsKey(MyPriceConstants.CURRENT_RESULT)
										? (boolean) paramMap.get(MyPriceConstants.CURRENT_RESULT)
										: false;
							}
						}

						inrResult = paramMap.containsKey(MyPriceConstants.CONFIG_SOLN_DESIGN_RESPONSE)
								? (boolean) paramMap.get(MyPriceConstants.CONFIG_SOLN_DESIGN_RESPONSE)
								: true;
						paramMap.remove(MyPriceConstants.CONFIG_SOLN_DESIGN_RESPONSE);
						logger.info("Start Processing config design {}", nxSolutionId);
						// saving configDesign response for all sites
						Map<String, Object> respMap = configDesignHelperService.processConfigDesignResponse(paramMap);
						if (respMap.containsKey(MyPriceConstants.RESPONSE_STATUS) && inrResult) {
							inrResult = (boolean) respMap.get(MyPriceConstants.CONFIG_SOLN_DESIGN_RESPONSE);
						}
						logger.info("End Processing config design {}", nxSolutionId);
						// adding failed ckts in in NxDesignAudit
						List<Object[]> failedDesigns = nxInrDesignRepository.findCktByNxSolutionId(nxSolutionId,
								nxReqIds);
						Map<Long, Set<String>> failedCktsByReqIdMap = new HashMap<>();
						for (Object[] design : failedDesigns) {
							if (failedCktsByReqIdMap.containsKey(Long.parseLong(String.valueOf(design[2])))) {
								Set<String> failedCkts = failedCktsByReqIdMap
										.get(Long.parseLong(String.valueOf(design[2])));
								failedCkts.add(String.valueOf(design[0]));
								failedCktsByReqIdMap.put(Long.parseLong(String.valueOf(design[2])), failedCkts);
							} else {
								Set<String> failedCkts = new HashSet<>();
								failedCkts.add(String.valueOf(design[0]));
								failedCktsByReqIdMap.put(Long.parseLong(String.valueOf(design[2])), failedCkts);
							}
						}
						nxReqIds = null;

						failedCktsByReqIdMap.forEach((reqId, ckts) -> {
							NxDesignAudit nxDesignAudit = nxDesignAuditRepository.findByNxRefIdAndTransaction(reqId,
									MyPriceConstants.MYPRICE_FAILED_CIRCUITS);
							if (nxDesignAudit == null) {
								nxDesignAudit = new NxDesignAudit();
								nxDesignAudit.setTransaction(MyPriceConstants.MYPRICE_FAILED_CIRCUITS);
								nxDesignAudit.setNxRefId(reqId);
							}
							nxDesignAudit.setData(ckts.toString());
							nxDesignAuditRepository.save(nxDesignAudit);
						});
					}
				}
				// rest request processing
				if (CollectionUtils.isNotEmpty(restRequestIds)) {
					logger.info("Start processing solution with rest api {}", nxSolutionId);
					List<NxValidationRules> restErrRules = nxValidationRulesRepository
							.findByValidationGroupAndActive("REST_ERROR", StringConstants.CONSTANT_Y);
					List<NxLookupData> usageNonusageProductList = nxLookupDataRepository
							.findByDatasetName(MyPriceConstants.USAGE_NON_USAGE_PRODUCT_NAME);
					List<Object[]> nxInrDesigns = nxInrDesignRepository.findDesignByNxSolutionId(nxSolutionId,
							MyPriceConstants.REST_MP_API_CONST, StringConstants.CONSTANT_Y, restRequestIds);
					logger.info("nxInrDesigns size {}", nxInrDesigns.size());
					if (nxInrDesigns.size() == 0) {
						logger.info("No valid designs to submit to MP for the solution id {}", nxSolutionId);
						inrResult = false;
					} else {
						Map<String, List<RestErrors>> designErr = new LinkedHashMap<String, List<RestErrors>>();
						// code to read existing error
						List<NxDesignAudit> nxDesignAudits = nxDesignAuditRepository
								.findByTransactionAndNxRefId(MyPriceConstants.REST_CONFIG_FAILURE_DATA, nxSolutionId);
						if (CollectionUtils.isNotEmpty(nxDesignAudits)) {
							for (NxDesignAudit nxDesignAudit : nxDesignAudits) {
								designErr.put(nxDesignAudit.getStatus(),
										mapper.readerFor(new TypeReference<List<RestErrors>>() {
										}).withRootName("restErrors").readValue(nxDesignAudit.getData()));
							}
						}
						boolean restInrResult = true;
						List<NxInrDesign> status = new ArrayList<NxInrDesign>();
						for (int i = 0; i < nxInrDesigns.size(); i++) {
							Object[] design = nxInrDesigns.get(i);
							NxInrDesign nxInrDesign = nxInrDesignRepository
									.findByNxSolutionIdAndNxInrDesignIdAndActiveYN(nxSolutionId,
											Long.parseLong(String.valueOf(design[0])), StringConstants.CONSTANT_Y);
							if (nxInrDesign !=null && CollectionUtils.isNotEmpty(nxInrDesign.getNxInrDesignDetails())) {
								for (NxInrDesignDetails nxInrDesignDetails : nxInrDesign.getNxInrDesignDetails()) {
									if (MyPriceConstants.REST_API_NOT_INVOKED
											.equalsIgnoreCase(nxInrDesignDetails.getStatus())) {
										Map<String, Object> requestMap = new HashMap<String, Object>();
										requestMap.put(MyPriceConstants.OFFER_NAME,
												this.getProduct(nxInrDesignDetails.getProduct()));
										requestMap.put(InrConstants.REQUEST_META_DATA_KEY, requestMetaDataMap);
										if(usrpReq.contains(nxInrDesignDetails.getNxReqId())) {
											requestMap.put(MyPriceConstants.PRODUCT_TYPE, MyPriceConstants.SOURCE_USRP);
											requestMap.put(MyPriceConstants.PRODUCT_TYPE_REST_ERROR, MyPriceConstants.SOURCE_USRP);
										}else {
											requestMap.put(MyPriceConstants.PRODUCT_TYPE, MyPriceConstants.SOURCE_INR);
											requestMap.put(MyPriceConstants.PRODUCT_TYPE_REST_ERROR, null);
										}
										requestMap.put(CustomJsonConstants.BS_ID,
												createTransactionResponse.getMyPriceTransacId() != null
														? Long.valueOf(createTransactionResponse.getMyPriceTransacId())
														: 0l);
										requestMap.put(MyPriceConstants.MP_TRANSACTION_ID,
												createTransactionResponse.getMyPriceTransacId() != null
														? Long.valueOf(createTransactionResponse.getMyPriceTransacId())
														: 0l);
										requestMap.put(CustomJsonConstants.DOCUMENT_ID, Long.valueOf(documentId));
										configAndUpdateRestProcessingService.callMpConfigAndUpdate(requestMap,
												nxInrDesignDetails.getDesignData());
										boolean result = requestMap.containsKey(MyPriceConstants.RESPONSE_STATUS)
												? (boolean) requestMap.get(MyPriceConstants.RESPONSE_STATUS)
												: true;
										if (nxInrDesignDetails.getSubProduct() != null && (!nxInrDesignDetails
												.getSubProduct().startsWith(MyPriceConstants.MULTIPLE_CONFIG))
												&& result) {
											// requestMap.put(CustomJsonConstants.SUB_OFFER_NAME,
											// nxInrDesignDetails.getSubProduct());
											requestMap = new HashMap<String, Object>();
											requestMap.put(MyPriceConstants.OFFER_NAME,
													this.getProduct(nxInrDesignDetails.getSubProduct()));
											requestMap.put(InrConstants.REQUEST_META_DATA_KEY, requestMetaDataMap);
											requestMap.put(MyPriceConstants.PRODUCT_TYPE, MyPriceConstants.SOURCE_INR);
											requestMap.put(CustomJsonConstants.BS_ID,
													createTransactionResponse.getMyPriceTransacId() != null
															? Long.valueOf(
																	createTransactionResponse.getMyPriceTransacId())
															: 0l);
											requestMap.put(MyPriceConstants.MP_TRANSACTION_ID,
													createTransactionResponse.getMyPriceTransacId() != null
															? Long.valueOf(
																	createTransactionResponse.getMyPriceTransacId())
															: 0l); // to print in logs
											requestMap.put(CustomJsonConstants.DOCUMENT_ID, Long.valueOf(documentId));
											configAndUpdateRestProcessingService.callMpConfigAndUpdate(requestMap,
													nxInrDesignDetails.getDesignData());
										}

										if ((requestMap.containsKey(MyPriceConstants.RESPONSE_STATUS)
												&& !(Boolean) requestMap.get(MyPriceConstants.RESPONSE_STATUS))
												|| (requestMap.containsKey(CustomJsonConstants.CONFIG_BOM_ERROR)
														&& requestMap.get(CustomJsonConstants.CONFIG_BOM_ERROR) != null
														&& (Boolean) requestMap
																.get(CustomJsonConstants.CONFIG_BOM_ERROR))
												|| (requestMap.containsKey(CustomJsonConstants.SITE_CONFIG_ERROR)
														&& requestMap.get(CustomJsonConstants.SITE_CONFIG_ERROR) != null
														&& (Boolean) requestMap
																.get(CustomJsonConstants.SITE_CONFIG_ERROR))) {
											StringBuffer logRequestMap = new StringBuffer(
													requestMap.toString() + " keyId " + nxInrDesign.getCircuitId());
											logger.info("Error from MP for rest api {}",
													org.apache.commons.lang3.StringUtils.normalizeSpace(logRequestMap.toString()));
											nxInrDesignDetails.setStatus(MyPriceConstants.REST_API_FAILED);
											nxInrDesignDetails.setFailureData(getErrorDetails(requestMap).toString());
											nxInrDesign.setStatus(MyPriceConstants.REST_API_FAILED);
											restInrResult = false;
											String requestProductCd=reqProductMap.containsKey(nxInrDesignDetails.getNxReqId())?
													reqProductMap.get(nxInrDesignDetails.getNxReqId()):null;
											prepareFailedCkt(requestMap, nxInrDesignDetails.getProduct(),
													nxInrDesignDetails.getDesignData(), restErrRules, designErr,
													usageNonusageProductList,requestProductCd);
											requestMap.put(MyPriceConstants.CURRENT_RESULT, false);
										} else {
											nxInrDesignDetails.setStatus(MyPriceConstants.REST_API_SUCCEED);
											nxInrDesign.setStatus(MyPriceConstants.REST_API_SUCCEED);
											requestMap.put(MyPriceConstants.CURRENT_RESULT, true);
										}

										if (!inrSuccessResult) {
											inrSuccessResult = requestMap.containsKey(MyPriceConstants.CURRENT_RESULT)
													? (boolean) requestMap.get(MyPriceConstants.CURRENT_RESULT)
													: false;
										}
										requestMap = null;
									}
								}
								status.add(nxInrDesign);
								if (status.size() == 10 || (i + 1) == nxInrDesigns.size()) {
									nxInrDesignRepository.saveAll(status);
									status.clear();
								}
							}
						}
						logger.info("End processing solution with rest api {}", nxSolutionId);
						if (!designErr.isEmpty() && !restInrResult) {
							designErr.forEach((product, error) -> {
								NxDesignAudit nxDesignAudit = nxDesignAuditRepository
										.findByTransactionAndStatusAndNxRefId(MyPriceConstants.REST_CONFIG_FAILURE_DATA,
												product, nxSolutionId);
								if (nxDesignAudit == null) {
									nxDesignAudit = new NxDesignAudit();
								}
								nxDesignAudit.setStatus(product);
								nxDesignAudit.setTransaction(MyPriceConstants.REST_CONFIG_FAILURE_DATA);
								nxDesignAudit.setNxRefId(nxSolutionId);
								try {
									nxDesignAudit.setData(
											mapper.writer().withRootName("restErrors").writeValueAsString(error));
								} catch (JsonProcessingException e) {
									logger.info("Erro at persisting rest error in DB");
								}
								nxDesignAuditRepository.save(nxDesignAudit);
							});
						}
						if (restInrResult) {
							inrResult = Boolean.parseBoolean(nxInrDesignRepository.findStatusByFailed(nxSolutionId));
						} else {
							inrResult = restInrResult;
						}
					}
					restErrRules=null;
				}
			}
			if (configUpdateResMap.containsKey("iglooCount")
					&& Long.parseLong(configUpdateResMap.get("iglooCount").toString()) != 0) {
				try {
					TimeUnit.SECONDS.sleep(10);
				} catch (InterruptedException e) {
					logger.info(e.getMessage());
				}
			}
			configUpdateResMap.remove("iglooCount");
			List<NxAccessPricingData> nxAccessPricingDatas = nxAccessPricingDataRepository
					.findByNxSolIdAndIncludeIndAndMpStatus(nxSolutionDetail.getNxSolutionId());
			if (CollectionUtils.isNotEmpty(nxAccessPricingDatas)) {
				isAddrEditSubmission = false;
				Map<String, Object> paramMap = new HashMap<String, Object>();
				Map<String, Object> configRespMap = new HashMap<String, Object>();
				paramMap.put(MyPriceConstants.PRODUCT_NAME, prodName);
				paramMap.put(InrConstants.REQUEST_META_DATA_KEY, requestMetaDataMap);
				paramMap.put(MyPriceConstants.CONFIG_DESIGN_RESPONSE, new ConfigureResponse());
				paramMap.put(MyPriceConstants.CONFIG_RESPONSE_PROCESSING_DATA,
						new HashMap<String, Map<String, Object>>());
				paramMap.put(MyPriceConstants.MP_TRANSACTION_ID, createTransactionResponse.getMyPriceTransacId());
				paramMap.put(MyPriceConstants.NX_TRANSACTION_ID, createTransactionResponse.getNxTransacId());
				paramMap.put(MyPriceConstants.FLOW_TYPE, MyPriceConstants.SOURCE_IGLOO);
				logger.info("nxAccessPricingDatas size : " + nxAccessPricingDatas.size());
				// soap enabled
				if (!isRESTEnabled(paramMap)) {
					int i = 0;
					for (NxAccessPricingData accessPricingData : nxAccessPricingDatas) {
						Boolean isLastDesign = myPriceTransactionUtil.isLastDesign(i++, nxAccessPricingDatas);
						paramMap.put(MyPriceConstants.IS_LAST_DESIGN, isLastDesign);
						configRespMap = configAndUpdateProcessingIglooService
								.callConfigSolutionAndDesign(createTransactionResponse, accessPricingData, paramMap);
						if (!iglooSuccessResult) {
							iglooSuccessResult = paramMap.containsKey(MyPriceConstants.CURRENT_RESULT)
									? (boolean) paramMap.get(MyPriceConstants.CURRENT_RESULT)
									: false;
						}
					}

					iglooResult = paramMap.containsKey(MyPriceConstants.CONFIG_SOLN_DESIGN_RESPONSE)
							? (boolean) paramMap.get(MyPriceConstants.CONFIG_SOLN_DESIGN_RESPONSE)
							: true;
					paramMap.remove(MyPriceConstants.CONFIG_SOLN_DESIGN_RESPONSE);
					// saving configDesign response for all sites
					Map<String, Object> respMap = configDesignHelperService.processConfigDesignResponse(paramMap);
					if (respMap.containsKey(MyPriceConstants.RESPONSE_STATUS) && iglooResult) {
						iglooResult = (boolean) respMap.get(MyPriceConstants.CONFIG_SOLN_DESIGN_RESPONSE);
					}
				} else {
					Long currentTime = System.currentTimeMillis();
					Long createIglooRestStartTime = System.currentTimeMillis() - currentTime;
					// REST Enabled
					Map<String, List<Map<String, List<Object>>>> iglooMap = createIglooData(nxAccessPricingDatas);
					printTotalDuration(currentTime, createIglooRestStartTime, StringConstants.IGLOO_QUOTE_CONSOLIDATION,
							nxSolutionId);
					for (Map.Entry<String, List<Map<String, List<Object>>>> iglloMapData : iglooMap.entrySet()) {
						List<Map<String, List<Object>>> nxAccessMpLimitRequest = iglloMapData.getValue();
						for (int i = 0; i < nxAccessMpLimitRequest.size(); i++) {
							if (nxAccessMpLimitRequest.get(i).containsKey("jsonList")
									&& nxAccessMpLimitRequest.get(i).containsKey("nxAccessPricId")) {
								String iglooConsolidateJson = mapper
										.writeValueAsString(nxAccessMpLimitRequest.get(i).get("jsonList"));
								logger.info("NxAccessPricingDataID ::"
										+ nxAccessMpLimitRequest.get(i).get("nxAccessPricId").toString()
										+ " :: consolidateIglooData value::" + iglooConsolidateJson);
								JSONObject data = (JSONObject) nxAccessMpLimitRequest.get(i).get("jsonList").get(0);
								String countryIsoCode = data.get("country") != null ? data.get("country").toString()
										: null;
								Map<String, Object> requestMap = new HashMap<>();
								requestMap.put(MyPriceConstants.PRODUCT_TYPE, MyPriceConstants.SOURCE_IGLOO);
								requestMap.put(CustomJsonConstants.BS_ID,
										createTransactionResponse.getMyPriceTransacId() != null
												? Long.valueOf(createTransactionResponse.getMyPriceTransacId())
												: 0l);
								requestMap.put(MyPriceConstants.MP_TRANSACTION_ID,
										createTransactionResponse.getMyPriceTransacId() != null
												? Long.valueOf(createTransactionResponse.getMyPriceTransacId())
												: 0l); // to print in logs
								requestMap.put(MyPriceConstants.DOCUMENT_ID, Long.valueOf(documentId));
								requestMap.put(InrConstants.REQUEST_META_DATA_KEY, requestMetaDataMap);
								if (null != countryIsoCode && countryIsoCode.trim().equalsIgnoreCase("US")) {
									requestMap.put(MyPriceConstants.OFFER_NAME, MyPriceConstants.ETHERNET);
									configAndUpdateRestProcessingService.callMpConfigAndUpdate(requestMap,
											iglooConsolidateJson);
								} else {
									requestMap.put(MyPriceConstants.OFFER_NAME,
											MyPriceConstants.SOURCE_INTERNATIONAL_ACCESS);
									configAndUpdateRestProcessingService.callMpConfigAndUpdate(requestMap,
											iglooConsolidateJson);

								}
								List<Object> nxAccessPricIdList = nxAccessMpLimitRequest.get(i).get("nxAccessPricId");
								if (CollectionUtils.isNotEmpty(nxAccessPricIdList)) {
									for (Object id : nxAccessPricIdList) {
										Long idVal = (Long) id;
										NxAccessPricingData nxAccessPricingDataObj = nxAccessPricingDataRepository
												.getNxAccessPricingData(idVal);
										if ((requestMap.containsKey(MyPriceConstants.RESPONSE_STATUS)
												&& !(Boolean) requestMap.get(MyPriceConstants.RESPONSE_STATUS))
												|| (requestMap.containsKey(CustomJsonConstants.CONFIG_BOM_ERROR)
														&& requestMap.get(CustomJsonConstants.CONFIG_BOM_ERROR) != null
														&& (Boolean) requestMap
																.get(CustomJsonConstants.CONFIG_BOM_ERROR))
												|| (requestMap.containsKey(CustomJsonConstants.SITE_CONFIG_ERROR)
														&& requestMap.get(CustomJsonConstants.SITE_CONFIG_ERROR) != null
														&& (Boolean) requestMap
																.get(CustomJsonConstants.SITE_CONFIG_ERROR))) {
											iglooRestResult = false;
											nxAccessPricingDataObj.setMpStatus(MyPriceConstants.REST_API_FAILED);
											nxAccessPricingDataObj
													.setRestResponseError(getErrorDetails(requestMap).toString());
											requestMap.put(MyPriceConstants.CURRENT_RESULT, false);
										} else {
											nxAccessPricingDataObj.setMpStatus(MyPriceConstants.REST_API_SUCCEED);
											requestMap.put(MyPriceConstants.CURRENT_RESULT, true);
										}
										nxAccessPricingDataRepository.saveAndFlush(nxAccessPricingDataObj);
										if (!iglooSuccessResult) {
											iglooSuccessResult = requestMap.containsKey(MyPriceConstants.CURRENT_RESULT)
													? (boolean) requestMap.get(MyPriceConstants.CURRENT_RESULT)
													: false;
										}
									}
								}
							}
						}
					}
					printTotalDuration(currentTime, createIglooRestStartTime, StringConstants.IGLOO_QUOTE_MP_PROESS,
							nxSolutionId);
				}
			}
			NxMpDeal nxMpDeal = nxMpDealRepository.findByNxTxnId(createTransactionResponse.getNxTransacId());
			logger.info("iglooResult : " + iglooResult + " inrResult : " + inrResult + " iglooRestResult : "
					+ iglooRestResult + " nxTxnId :" + createTransactionResponse.getNxTransacId());
			if (Optional.ofNullable(nxMpDeal).isPresent()) {
				if (isAddrEditSubmission) {
					nxMpDeal.setDealStatus(nxMpStatus);
				} else {
					if (!iglooResult || !inrResult || !iglooRestResult) {
						if (inrSuccessResult || iglooSuccessResult) {
							nxMpDeal.setDealStatus(CommonConstants.PARTIAL);
						} else {
							nxMpDeal.setDealStatus(CommonConstants.FAILED);
						}
					} else {
						nxMpDeal.setDealStatus(CommonConstants.SUBMITTED);
					}
				}

				nxMpDeal.setModifiedDate(new Date());
				nxMpDealRepository.saveAndFlush(nxMpDeal);
				String dealId = requestMetaDataMap.containsKey("DEAL_ID")
						? requestMetaDataMap.get("DEAL_ID").toString()
						: null;
				SubmitToAnotherDeal submitToAnotherDealId = submitToAnotherDealRepository.findBydealId(dealId);
				if (submitToAnotherDealId != null) {
					submitToAnotherDealRepository.delete(submitToAnotherDealId);
				}
				
			}
			mailServiceImpl.prepareMyPriceDealSubmissionRequest(nxMpDeal);
		} catch (Exception e) {
			String dealId = requestMetaDataMap.containsKey("DEAL_ID")
					? requestMetaDataMap.get("DEAL_ID").toString()
					: null;
			SubmitToAnotherDeal submitToAnotherDealId = submitToAnotherDealRepository.findBydealId(dealId);
			if (submitToAnotherDealId != null) {
				submitToAnotherDealRepository.delete(submitToAnotherDealId);
			}
			logger.info("Exception occured at ProcessINRtoMP", e);
		}
	}

	public List<String> getDataSet(String datasetName) {
		List<NxLookupData> nxLookupLst = nxLookupDataRepository.findByDatasetName(datasetName);
		if (!nxLookupLst.isEmpty())
			return new ArrayList<String>(Arrays.asList(nxLookupLst.get(0).getCriteria().split(Pattern.quote(","))));
		else
			return null;
	}

	public Map<Long, Set<Long>> getServiceAccessGroupId(Long nxSolutionId) {
		List<NxRequestDetails> nxRequestDetailList = nxRequestDetailsRepository.findByNxSolutionId(nxSolutionId);
		Map<Long, Set<Long>> reqWithGrps = new HashMap<Long, Set<Long>>();
		Set<Long> groups = nxRequestDetailList.stream().map(g -> g.getNxRequestGroupId()).collect(Collectors.toSet());
		for (Long group : groups) {
			List<NxRequestDetails> nrs = nxRequestDetailList.stream()
					.filter(n -> n.getNxRequestGroupId().longValue() == group.longValue()).collect(Collectors.toList());
			boolean isServiceAccessGroup = falloutDetailsImpl.isServiceAccessGroup(nrs);
			if (isServiceAccessGroup) {
				List<NxRequestDetails> accessProducts = nxRequestDetailList.stream()
						.filter(n -> MyPriceConstants.ACCESS_GROUP.equalsIgnoreCase(n.getNxRequestGroupName()))
						.collect(Collectors.toList());
				List<NxRequestDetails> grps = nxRequestDetailList.stream().flatMap(
						ap -> accessProducts.stream().filter(n -> n.getEdfAckId().equalsIgnoreCase(ap.getEdfAckId())))
						.collect(Collectors.toList());
				Set<Long> groupIds = grps.stream().map(g -> g.getNxRequestGroupId()).collect(Collectors.toSet());
				nrs.forEach(n -> {
					reqWithGrps.put(n.getNxReqId(), groupIds);
				});
			} else {
				nrs.forEach(n -> {
					reqWithGrps.put(n.getNxReqId(), new HashSet<Long>() {
						{
							add(n.getNxRequestGroupId());
						}
					});
				});
			}
		}
		return reqWithGrps;
	}

	protected boolean isRESTEnabled(Map<String, Object> requestParam) {
		boolean isRestEnabled = false;
		List<NxLookupData> restProductDetails = nxLookupDataRepository.findByDatasetNameAndItemId(
				MyPriceConstants.REST_PRODUCTS, (String) requestParam.get(MyPriceConstants.FLOW_TYPE));
		if (CollectionUtils.isNotEmpty(restProductDetails)) {
			String criteria = restProductDetails.get(0).getCriteria();
			if ("Y".equalsIgnoreCase(criteria)) {
				isRestEnabled = true;
			} else if ("N".equalsIgnoreCase(criteria)) {
				isRestEnabled = false;
			}
		}
		return isRestEnabled;
	}

	protected void persistDesign(NxValidationRules nxValidationRule, Map<String, Object> paramMap,
			NxOutputFileModel nxOutputFileModel, NxSolutionDetail nxSolutionDetail,
			List<NxValidationRules> skipCktRules, Map<String, Set<String>> qualifiedCkts,
			Map<Long, List<Map<String, List<Long>>>> nxKeyIdNxSiteIds, Set<String> tdmNxt1Ckts,
			Map<String, List<String>> productSiteIdMap) throws JsonParseException, JsonMappingException, IOException {
		List<String> cktids = nxAccessPricingDataRepository 
				.fetchCktByLocationYnAndNxSolutionId(nxSolutionDetail.getNxSolutionId()).stream().filter(c -> c!=null).map(n-> n.replaceAll("\\s", "").replaceAll("\\.", "")).collect(Collectors.toList());
		List<NxAccessPricingData> nxAccessPricingDatas = nxAccessPricingDataRepository
				.fetchByByLocationYnAndNxSolutionId(nxSolutionDetail.getNxSolutionId());
		Long groupId = paramMap.get(MyPriceConstants.NX_GROUP_ID) != null
				? (Long) paramMap.get(MyPriceConstants.NX_GROUP_ID)
				: null;
		Map<String, NxInrDesign> designMap = new HashMap<String, NxInrDesign>();
		JsonNodeFactory nf = JsonNodeFactory.instance;
		UsageRuleObj usageRuleObj = mapper.readValue(nxValidationRule.getDataPath(), UsageRuleObj.class);
		UsageRule usageRule = usageRuleObj.getUsageRule().get(0);
		/**
		 * String jsonArrayName = usageRule.getJsonarray(); // array key name String
		 * jsonDataPath = usageRule.getJsonDataPath(); // path with replace indicator
		 * String jsonValidationPath = usageRule.getJsonValidationPath(); // to retrieve
		 * unique key id String jsonValidationKey = usageRule.getJsonValidationKey(); //
		 * replace indicator String jsonParentDataPath =
		 * usageRule.getJsonParentDataPath(); // parent path with replace
		 */
		List<String> accessProvidedSkipMp= getDataSet(MyPriceConstants.ACCESS_PROVIDED_SKIP_MP);
		String flowtype=paramMap.containsKey(MyPriceConstants.FLOWTYPE)?
				(String)paramMap.get(MyPriceConstants.FLOWTYPE):null;
		boolean isUsrp=false;
		boolean isEplsUsrp=false;
		if (MyPriceConstants.USRP_PORT_OFFER.contains(nxValidationRule.getName()) && MyPriceConstants.SOURCE_USRP.equalsIgnoreCase(flowtype)) {
			isUsrp=true;
			paramMap.put(MyPriceConstants.FLOWTYPE, MyPriceConstants.SOURCE_USRP);
			/**
			 * skip rule for usrp with port block
			 */
			skipCktRules=nxValidationRulesRepository.findByValidationGroupAndOfferAndActiveAndFlowTypeAndName("MANDATORY_DESIGN_DATA", nxValidationRule.getName(),
					StringConstants.CONSTANT_Y,MyPriceConstants.SOURCE_USRP,nxValidationRule.getName());
		}
		if("EPLSWAN".equalsIgnoreCase(nxValidationRule.getName()) && MyPriceConstants.SOURCE_USRP.equalsIgnoreCase(flowtype))
		{
			
			isEplsUsrp = true; 
			skipCktRules=nxValidationRulesRepository.findByValidationGroupAndOfferAndActiveAndFlowTypeAndName("MANDATORY_DESIGN_DATA", nxValidationRule.getName(),
					StringConstants.CONSTANT_Y,MyPriceConstants.SOURCE_USRP,nxValidationRule.getName());
		}
		
		paramMap.put(MyPriceConstants.PRODUCT_NAME, nxValidationRule.getName());
		boolean isUSRPportProcessesed=false;
		if (usageRule.getJsonParentDataPath() != null) {
			List<Object> parentNodes = inrQualifyService.getCircuits(nxOutputFileModel.getMpOutputJson(),
					usageRule.getJsonParentDataPath());
			if (parentNodes != null) {
				paramMap.put(MyPriceConstants.SUB_DATA, usageRule.getJsonDataPath());
				paramMap.put(MyPriceConstants.ARRAY_NAME, usageRule.getJsonarray());
				String values[] = nxValidationRule.getValue().split(":");
				for (int i = 0; i < parentNodes.size(); i++) {
					isUSRPportProcessesed=false;
					JsonNode parentNode = mapper.valueToTree(parentNodes.get(i));
						ObjectNode parentObj = (ObjectNode) parentNode;
						parentObj.put("product", nxValidationRule.getName());
						if (usageRule.getJsonDataPath() != null) {
							boolean designDataMissing = submitToMyPriceService.skipCktProcessing(skipCktRules, parentNode,
									paramMap);
							if(isUsrp && MyPriceConstants.USRP_PORT_OFFER.contains(nxValidationRule.getOffer())
									&& !designDataMissing) {
							List<Object> subDataNodes = inrQualifyService.getCircuits(parentNode,
										"$..priceDetails.[?(@.typeOfCharge=='P')]");
								if(subDataNodes==null) {
									/** if design array has only Access block then mark the designdata missing as true
									* so that it can be processesed in the next block
									**/
									designDataMissing=true;
								}
							}
							if (!designDataMissing) {
								List<Object> locations = new ArrayList<Object>();
								try {
									String data = mapper.writeValueAsString(parentNode);
									locations = inrQualifyService.getCircuits(data, usageRule.getJsonDataPath());
								} catch (JsonProcessingException e) {
									e.printStackTrace();
								}
								if (CollectionUtils.isNotEmpty(locations)) {
									locLoop: for (Object loc : locations) {
										List<Object> portObjectList=null;
										paramMap.put(MyPriceConstants.PRODUCT_NAME, nxValidationRule.getName());
										JsonNode locNode = mapper.valueToTree(loc);
										String nxKeyId = String.valueOf(
												nexxusJsonUtility.getValue(locNode, usageRule.getJsonValidationPath()));
										
										Object obj = nexxusJsonUtility.getValue(locNode, values[0]);
										if (obj == null && values.length > 1) {
											obj = nexxusJsonUtility.getValue(locNode, values[1]);
										}
										String tdmcktId = (parentNode.path("CktId").isMissingNode()
												|| parentNode.path("CktId").isNull()) ? null
														: parentNode.path("CktId").asText();
										if (MyPriceConstants.DOMESTIC_DEDICATED_ACCESS.equalsIgnoreCase(nxValidationRule.getOffer())
												&& cktids.contains(tdmcktId.replaceAll("\\W", ""))) {
										} else if(obj != null) {
											
											Long nxSiteId = Long.parseLong(String.valueOf(obj));
											String nxT1Key = "";
											boolean isNxT1Ckt = false;
											if ("TDM".equalsIgnoreCase(nxValidationRule.getSubOffer())
													&& tdmNxt1Ckts != null) {
												String cktId = (parentNode.path("CktId").isMissingNode()
														|| parentNode.path("CktId").isNull()) ? null
																: parentNode.path("CktId").asText();
												String siteNpaNxx = (locNode.path("SiteNPANXX").isMissingNode()
														|| locNode.path("SiteNPANXX").isNull()) ? null
																: locNode.path("SiteNPANXX").asText();
												String custSrvgWireCtrCLLICd = (locNode.path("CustSrvgWireCtrCLLICd")
														.isMissingNode() || locNode.path("CustSrvgWireCtrCLLICd").isNull())
																? null
																: locNode.path("CustSrvgWireCtrCLLICd").asText();
												nxT1Key = String.valueOf(obj) + "$"
														+ (cktId.trim().length() >= 10 ? (cktId.trim().substring(0, 10))
																: cktId.trim())
														+ "$" + siteNpaNxx + "$" + custSrvgWireCtrCLLICd;
												if (tdmNxt1Ckts.contains(nxT1Key)) {
													List<NxTDMKeyIdMapping> nxTDMKeyIdMappings = nxTDMKeyIdMappingRepo
															.findByNxGrpIdAndTdmNxKeyId(groupId, nxT1Key);
													if (!nxTDMKeyIdMappings.isEmpty()) {
														nxKeyId = nxTDMKeyIdMappings.get(0).getNewNxKeyId();
													} else {
														nxKeyId = nxT1Key;
													}
													isNxT1Ckt = true;
												}
											}

											if (isDesignProcessed(productSiteIdMap, nxValidationRule.getOffer(),
															nxSiteId)) {
												List<Object> ports = new ArrayList<>();
												if (MyPriceConstants.GMIS.equalsIgnoreCase(nxValidationRule.getOffer()) && (!isUsrp)) {
													ports = inrQualifyService.getCircuits(loc,
															usageRule.getMultiConfigCountPath());
												}
												if (MyPriceConstants.AVPN_OFFER_NAME
														.equalsIgnoreCase(nxValidationRule.getOffer()) &&  (!isUsrp)) {
														if (!locNode.path("accessSecondaryKey").isMissingNode() 
																&& !locNode.path("circuitId").isMissingNode()
																&& !cktids.contains(locNode.path("circuitId").asText())) {
															paramMap.put(MyPriceConstants.SUB_PRODUCT,
																	MyPriceConstants.AVPN_LOCALACCESS);
														}
														mrcnrc:
														for(NxAccessPricingData nxAccessPricingData: nxAccessPricingDatas) {
															if (nxAccessPricingData.getCircuitId() != null && nxAccessPricingData.getCircuitId().replaceAll("\\W", "").equalsIgnoreCase(locNode.path("circuitId").asText())) {
																ObjectNode node = (ObjectNode)locNode;
																node.put("igloolistMRC",nxAccessPricingData.getMrc());
																node.put("igloolistNRC",nxAccessPricingData.getNrc());
																break mrcnrc;
															}
														}
														parentObj.set(usageRule.getJsonarray(),
																new ArrayNode(nf, 0).add(locNode));
												}else if(isUsrp && MyPriceConstants.USRP_PORT_OFFER.contains(nxValidationRule.getOffer())) {
													/*for usrp  , save when port data and access 
													 * save only port data
													 */
													ObjectNode locNodeCopy = (ObjectNode) locNode.deepCopy();
														// need to pick node with pridetails with typeOfCharge==P  always 
														locNodeCopy.remove("priceDetails");
														portObjectList= inrQualifyService.getCircuits(locNode,
																"$..priceDetails.[?(@.typeOfCharge=='P' && @.nexxusFallout!='Y')]");
														List<JsonNode> a=new ArrayList<>(); 
														if(portObjectList!=null) {
															for (Object o : portObjectList) {
																a.add(mapper.valueToTree(o));
															}
															locNodeCopy.set("priceDetails", new ArrayNode(nf, 0).addAll(a));
															parentObj.set(usageRule.getJsonarray(),
																	new ArrayNode(nf, 0).add(locNodeCopy));
														}

												}
												else if (MyPriceConstants.GMIS.equalsIgnoreCase(
														nxValidationRule.getOffer()) && ports.size() > 0 && (!isUsrp)) {
													paramMap.put(MyPriceConstants.NX_KEY_ID, nxKeyId);
													List<Object> designObj = inrQualifyService.getCircuits(loc,
															usageRule.getMultiConfigDataPath());
													ObjectNode locObj = (ObjectNode) locNode;
													ObjectNode json = nf.objectNode();
	
													ArrayNode a = mapper.createArrayNode();
													for (Object o : designObj) {
														a.add(mapper.valueToTree(o));
													}
													json.putArray("dataElementDetails")
															.add(mapper.valueToTree(ports.get(0))).addAll(a);
													locObj.set("dataElementList", json);
													parentObj.set(usageRule.getJsonarray(), locNode);
													saveNxInrDesign(nxSolutionDetail, parentObj, paramMap, designMap);
													for (int p = 1; p < ports.size(); p++) {
														ObjectNode portJson = nf.objectNode();
														portJson.putArray("dataElementDetails")
																.add(mapper.valueToTree(ports.get(p)));
														locObj.set("dataElementList", portJson);
														parentObj.set(usageRule.getJsonarray(), locNode);
														paramMap.put(MyPriceConstants.SUB_PRODUCT,
																MyPriceConstants.MULTIPLE_CONFIG + "_"
																		+ nxValidationRule.getName());
														saveNxInrDesign(nxSolutionDetail, parentObj, paramMap, designMap);
														portJson = null;
													}
													json = null;
													paramMap.remove(MyPriceConstants.SUB_PRODUCT);
													paramMap.remove(MyPriceConstants.NX_KEY_ID);
													continue locLoop;
												} else {
													if (nxValidationRule.getSubData() != null && !qualifiedCkts.isEmpty()) {
														Object ckt = nexxusJsonUtility.getValue(parentObj,
																nxValidationRule.getSubData());
														if (obj != null && !qualifiedCkts.isEmpty()) {
															qualifiedCkts.forEach((prod, ckts) -> {
																if (ckts.contains(String.valueOf(ckt))) {
																	parentObj.put(nxValidationRule.getSubDataPath(), prod);
																}
															});
														}
													}
													if ("TDM".equalsIgnoreCase(nxValidationRule.getSubOffer()) && isNxT1Ckt
															&& StringUtils.isNotBlank(nxT1Key)) {
														List<NxTDMKeyIdMapping> nxTDMKeyIdMappings = nxTDMKeyIdMappingRepo
																.findByNxGrpIdAndTdmNxKeyId(groupId, nxT1Key);
														if (!nxTDMKeyIdMappings.isEmpty()) {
															String quantity = nxTDMKeyIdMappings.get(0).getQuantity();
															parentObj.put("nxT1Quantity", quantity);
														} else {
															parentObj.put("nxT1Quantity", 1);
														}
														paramMap.put(MyPriceConstants.NXT1_CKT, isNxT1Ckt);
														paramMap.put(MyPriceConstants.SUB_OFFER,
																nxValidationRule.getSubOffer());
													}
	
													parentObj.set(usageRule.getJsonarray(), locNode);
												}
	                                            if (isUsrp && MyPriceConstants.USRP_PORT_OFFER.contains(nxValidationRule.getOffer())) {
	                                            	if(portObjectList!=null) {
														paramMap.put(MyPriceConstants.NX_KEY_ID, nxKeyId);
														saveNxInrDesign(nxSolutionDetail, parentObj, paramMap, designMap);
														paramMap.remove(MyPriceConstants.SUB_PRODUCT);
														paramMap.remove(MyPriceConstants.NX_KEY_ID);
														paramMap.remove(MyPriceConstants.NXT1_CKT);
														paramMap.remove(MyPriceConstants.SUB_OFFER);
														paramMap.remove(MyPriceConstants.PRODUCT_NAME);
	                                            	}
	                                            }
	                                            else if (isEplsUsrp) {
												try {
													parentObj.set(usageRule.getJsonarray(),
															new ArrayNode(nf, 0).add(locNode));// this is for epls
													paramMap.put(MyPriceConstants.NX_KEY_ID, nxKeyId);
													saveNxInrDesign(nxSolutionDetail, parentObj, paramMap, designMap);
													paramMap.remove(MyPriceConstants.SUB_PRODUCT);
													paramMap.remove(MyPriceConstants.NX_KEY_ID);
													paramMap.remove(MyPriceConstants.NXT1_CKT);
													paramMap.remove(MyPriceConstants.SUB_OFFER);
													paramMap.remove(MyPriceConstants.PRODUCT_NAME);
													//processing eplswan/ethernet object
													// skip access when it is in igloo migration ckt
													if(!cktids.contains(locNode.path("circuitId").asText())) {
													List<NxValidationRules> skipRules = nxValidationRulesRepository
															.findByValidationGroupAndOfferAndActiveAndFlowTypeAndName(
																	"MANDATORY_DESIGN_DATA", "EPLSWAN",
																	StringConstants.CONSTANT_Y, MyPriceConstants.SOURCE_USRP, "EPLSWAN/Ethernet");
													
													boolean designDataMissingAccess = submitToMyPriceService.skipCktProcessing(skipRules, locNode,
															paramMap);
													
													
													if(!designDataMissingAccess ) {
													
													String nxKeyIdA, nxKeyIdZ;
													nxKeyIdA = !locNode.path("nxKeyIdA").isMissingNode()
															? locNode.path("nxKeyIdA").asText()
															: null;
													nxKeyIdZ = !locNode.path("nxKeyIdZ").isMissingNode()
															? locNode.path("nxKeyIdZ").asText()
															: null;
													List<NxLookupData> lookUp = nxLookupDataRepository
															.findByDatasetName(MyPriceConstants.USRP_EPLS_ETHERNET);
													String excludeCriteria = lookUp.get(0).getCriteria().toString();
													//NexxusJsonUtility nexxusJsonUtility = new NexxusJsonUtility();
													@SuppressWarnings("unchecked")
													LinkedHashMap<String, Object> ruleMap = (LinkedHashMap<String, Object>) nexxusJsonUtility
															.convertStringJsonToMap(excludeCriteria);
													if (nxKeyIdA != null) {
														ObjectNode locNodeCopyForEthernet = (ObjectNode) locNode
																.deepCopy();
														if (ruleMap.containsKey("LocZTags")) {
															List<String> excludeList= new ArrayList();
															excludeList= Arrays.asList(ruleMap.get("LocZTags").toString().split(","));
															locNodeCopyForEthernet.remove(excludeList);
															// now remove Z end data from base parent obj and prepare
															// the right Erhernet parentObj
															paramMap.put(MyPriceConstants.PRODUCT_NAME,
																	"EPLSWAN/Ethernet");
															parentObj.set(usageRule.getJsonarray(),
																	new ArrayNode(nf, 0).add(locNodeCopyForEthernet));
															paramMap.put(MyPriceConstants.NX_KEY_ID, nxKeyIdA);// pass A end key 
															saveNxInrDesign(nxSolutionDetail, parentObj, paramMap,
																	designMap);
															paramMap.remove(MyPriceConstants.NX_KEY_ID);
															paramMap.remove(MyPriceConstants.SUB_PRODUCT);
															paramMap.remove(MyPriceConstants.NX_KEY_ID);
															paramMap.remove(MyPriceConstants.NXT1_CKT);
															paramMap.remove(MyPriceConstants.SUB_OFFER);
															paramMap.remove(MyPriceConstants.PRODUCT_NAME);

														}

													}

													if (nxKeyIdZ != null) {
														ObjectNode locNodeCopyForEthernet = (ObjectNode) locNode
																.deepCopy();//copy the deisgn block to new node to create ethernet

														if (ruleMap.containsKey("LocATags")) {
															List<String> excludeList= new ArrayList();
															excludeList= Arrays.asList(ruleMap.get("LocATags").toString().split(","));
															// now remove A end data from base parent obj and prepare the right Erhernet parentObj
															locNodeCopyForEthernet.remove(excludeList);
															paramMap.put(MyPriceConstants.PRODUCT_NAME,
																	"EPLSWAN/Ethernet");
															parentObj.set(usageRule.getJsonarray(),
																	new ArrayNode(nf, 0).add(locNodeCopyForEthernet));
															paramMap.put(MyPriceConstants.NX_KEY_ID, nxKeyIdZ);// passZ end key
															saveNxInrDesign(nxSolutionDetail, parentObj, paramMap,
																	designMap);
															paramMap.remove(MyPriceConstants.NX_KEY_ID);
															paramMap.remove(MyPriceConstants.SUB_PRODUCT);
															paramMap.remove(MyPriceConstants.NX_KEY_ID);
															paramMap.remove(MyPriceConstants.NXT1_CKT);
															paramMap.remove(MyPriceConstants.SUB_OFFER);
															paramMap.remove(MyPriceConstants.PRODUCT_NAME);

														}

													}
													}
												} 
												}
											catch (Exception e) {
													e.printStackTrace();
												}
												}

											
	                                            else {
													paramMap.put(MyPriceConstants.NX_KEY_ID, nxKeyId);
													saveNxInrDesign(nxSolutionDetail, parentObj, paramMap, designMap);
													paramMap.remove(MyPriceConstants.SUB_PRODUCT);
													paramMap.remove(MyPriceConstants.NX_KEY_ID);
													paramMap.remove(MyPriceConstants.NXT1_CKT);
													paramMap.remove(MyPriceConstants.SUB_OFFER);
													paramMap.remove(MyPriceConstants.PRODUCT_NAME);
												}
												// to process usrp avpn access products(port+access)
	                                            if (isUsrp && MyPriceConstants.USRP_PORT_OFFER.contains(nxValidationRule.getOffer())) {
	                                                isUSRPportProcessesed = true;
	                                            }
	                                            //always send access even if only typeOfCharge is P
												// to process usrp  access products
	                                            //if circuit contains the A block
	                                            //qualify circuit is there for AVPN,ADI,ADIG
												if (isUsrp && MyPriceConstants.USRP_PORT_OFFER.contains(nxValidationRule.getOffer())) {
													if (!locNode.path("qualifyCircuit").isMissingNode() && !locNode.path("qualifyCircuit").isNull()
															&& !locNode.path("nxKeyIdAccess").isMissingNode() && !locNode.path("nxKeyIdAccess").isNull()
															// skip access when it is in igloo migration ckt
															&& !cktids.contains(locNode.path("circuitId").asText())) {
														String product = locNode.path("accessProductName").asText();
														String nxKeyIdAccess = locNode.path("nxKeyIdAccess").asText();
														List<NxValidationRules> skipRules = nxValidationRulesRepository
																	.findByValidationGroupAndOfferAndActiveAndFlowTypeAndName(
																			"MANDATORY_DESIGN_DATA", nxValidationRule.getName(),
																			StringConstants.CONSTANT_Y, MyPriceConstants.SOURCE_USRP, product);
															
															boolean designDataMissingAccess = submitToMyPriceService.skipCktProcessing(skipRules, locNode,
																	paramMap);
															if (!designDataMissingAccess &&  
																	hasAccess(locNode) && isDesignProcessed(productSiteIdMap, MyPriceConstants.USRP_OFFER_NAME_MAP.containsKey(product) ? MyPriceConstants.USRP_OFFER_NAME_MAP.get(product) : product,
																			nxSiteId)) {
																
																/*for usrp  , circuit has the access block  
																 * save only access data
																 */
																ObjectNode locNodeCopy = (ObjectNode) locNode.deepCopy();
																locNodeCopy.remove("priceDetails");
																parentObj.remove(usageRule.getJsonarray());
																List<Object> accesObjectList = inrQualifyService.getCircuits(locNode,
																		"$..priceDetails.[?(@.typeOfCharge=='A' && @.nexxusFallout!='Y')]");
																List<JsonNode> a=new ArrayList<>(); 
																if(accesObjectList!=null) {
																	for (Object o : accesObjectList) {
																		a.add(mapper.valueToTree(o));
																	}
																	locNodeCopy.set("priceDetails", new ArrayNode(nf, 0).addAll(a));
																	parentObj.set(usageRule.getJsonarray(),
																			new ArrayNode(nf, 0).add(locNodeCopy));
	
																	paramMap.put(MyPriceConstants.NX_KEY_ID, nxKeyIdAccess);
																	paramMap.put(MyPriceConstants.PRODUCT_NAME, product);
																	saveNxInrDesign(nxSolutionDetail, parentObj, paramMap, designMap);
																	paramMap.remove(MyPriceConstants.PRODUCT_NAME);
																	paramMap.remove(MyPriceConstants.NX_KEY_ID);
																}
															  
															}else if(!designDataMissingAccess && ("N").equalsIgnoreCase(locNode.path("qualifyCircuit").asText()) && hasPort(locNode)) {
																/*handled when non qualified port is there
																 * if circuit is not qualified , then also save the  design related data for access product name along
																*with the port,here port price details will be save,to myprice only design related data will be send 
																*skip sending to mp if it belongs to accessProvidedSkipMp
																*/
																List<Object> nonFallPortObjectList= inrQualifyService.getCircuits(locNode,
																		"$..priceDetails.[?(@.typeOfCharge=='P' && @.nexxusFallout!='Y')]");
																boolean isSkipSaveMp = false;
																if(nonFallPortObjectList!=null) {
																	if(MyPriceConstants.AVPN.equalsIgnoreCase(nxValidationRule.getOffer()) || MyPriceConstants.MISPNT.equalsIgnoreCase(nxValidationRule.getOffer())) {
																		String howAccessProvided = !locNode.path("howAccessProvided").isMissingNode() && !locNode.path("howAccessProvided").isNull()?locNode.path("howAccessProvided").asText():null;
																		if(accessProvidedSkipMp!=null && howAccessProvided!=null && accessProvidedSkipMp.contains(howAccessProvided)) {
																			isSkipSaveMp=true;
																		}
																	}
																	
																if(MyPriceConstants.AVPN.equalsIgnoreCase(nxValidationRule.getOffer()) && !locNode.path("fractionalTDM").isMissingNode()
																		&& !locNode.path("fractionalTDM").isNull() && ("Y").equalsIgnoreCase(locNode.path("fractionalTDM").asText())) {
																	isSkipSaveMp=true;

																}	
															      if (!isSkipSaveMp && isDesignProcessed(productSiteIdMap,
																	MyPriceConstants.USRP_OFFER_NAME_MAP
																			.containsKey(product)
																					? MyPriceConstants.USRP_OFFER_NAME_MAP
																							.get(product)
																					: product,
																	nxSiteId)) {
															    	/*Quantity logic for TDM , when only port is there and its configured as access in MP
															    	*to add logic for TDM lookup for accessbeid lookup, hence put pricetype and typeofcharge as accessbeid
															    	*and added quantity
																	*remove all port data
															    	* */
																	String AccessSpeed = !locNode.path("AccessSpeed").isMissingNode() && !locNode.path("AccessSpeed").isNull()?locNode.path("AccessSpeed").asText():null;
																	int portSpeed=!locNode.path("portSpeed").isMissingNode() && !locNode.path("portSpeed").isNull()?Integer.parseInt(locNode.path("portSpeed").asText()):0;
																	if ((MyPriceConstants.AVPN.equalsIgnoreCase(nxValidationRule.getOffer()) || MyPriceConstants.MISPNT.equalsIgnoreCase(nxValidationRule.getOffer()))
																			&& ("TDM".equals(product))) {
																		ObjectNode locNodeCopy = (ObjectNode) locNode.deepCopy();
																		locNodeCopy.remove("priceDetails");
																		ObjectNode priceDetailNode = mapper.createObjectNode();
																		priceDetailNode.put("priceType", "ACCESSBEID");
																		priceDetailNode.put("typeOfCharge", "A");
																		JsonNode priceDetailsArray = locNode.path("priceDetails");
																		String portQuantity= String.valueOf(priceDetailsArray.get(0).path("quantity").asText());
																		String portFalloutMatchingId=String.valueOf(priceDetailsArray.get(0).path("FALLOUTMATCHINGID").asText());
																		priceDetailNode.put("quantity", portQuantity);
																		priceDetailNode.put("FALLOUTMATCHINGID", portFalloutMatchingId);
																		if("1.544 mb".equals(AccessSpeed)) {
																			int qty= (int) Math.round(portSpeed / 1544.0);
																			if (qty > 1) {
																				priceDetailNode.put("quantity", String.valueOf(qty));
																			}		
																		}
																		locNodeCopy.set("priceDetails", new ArrayNode(nf, 0).add(priceDetailNode));
																		/*service value will be offername for lineitemlookup
																		 * for only tdm lookup its TDM_ACCESS, i.e read from service in json
																		 */
																		ObjectNode designNode = mapper.createObjectNode();
																		designNode.set("design", new ArrayNode(nf, 0).add(locNodeCopy));
																		designNode.put("service","TDM_ACCESS");
																		try {
																			OutputJsonFallOutData outputJsonFallOutData=outputJsonService.getOutputData(designNode, InrConstants.INR_BETA);
																			designNode=(ObjectNode) outputJsonFallOutData.getMpOutputJson();
																			locNode=designNode.path("design").get(0);
																		} catch (SalesBusinessException e) {
																			logger.error("Exception in generating the lineitem lookup while saving nxInrDesigndetail data for TDM", e);
																		}
																		nxKeyIdAccess = locNode.path("nxKeyIdAccess").asText();
																	}
																	paramMap.put(MyPriceConstants.NX_KEY_ID, nxKeyIdAccess);
																	paramMap.put(MyPriceConstants.PRODUCT_NAME, product);
																	parentObj.set(usageRule.getJsonarray(), new ArrayNode(nf, 0).add(locNode));
																	saveNxInrDesign(nxSolutionDetail, parentObj, paramMap, designMap);
																	paramMap.remove(MyPriceConstants.PRODUCT_NAME);
																	paramMap.remove(MyPriceConstants.NX_KEY_ID);
																}
															}
													}
													}
												} 
											}
										}
									}
								}
							}
						//when usrp design blocks has only access
						if (MyPriceConstants.USRP_PORT_OFFER.contains(nxValidationRule.getOffer()) &&
								!isUSRPportProcessesed) {
							List<Object> locations = new ArrayList<Object>();
							try {
								String data = mapper.writeValueAsString(parentNode);
								locations = inrQualifyService.getCircuits(data, usageRule.getJsonDataPath());
							} catch (JsonProcessingException e) {
								e.printStackTrace();
							}
							if (CollectionUtils.isNotEmpty(locations)) {
								for (Object loc : locations) {
									JsonNode locNode = mapper.valueToTree(loc);
									Object obj = nexxusJsonUtility.getValue(locNode, values[0]);
									if (obj == null && values.length > 1) {
										obj = nexxusJsonUtility.getValue(locNode, values[1]);
									}
									 if(obj != null) {
									Long nxSiteId = Long.parseLong(String.valueOf(obj));
			
									if (!locNode.path("qualifyCircuit").isMissingNode() && !locNode.path("qualifyCircuit").isNull() && 
											!locNode.path("nxKeyIdAccess").isMissingNode() && !locNode.path("nxKeyIdAccess").isNull()
											&& hasAccess(locNode)
											// skip access when it is in igloo migration ckt
											&& !cktids.contains(locNode.path("circuitId").asText())) {
										String product = locNode.path("accessProductName").asText();
										String nxKeyIdAccess = locNode.path("nxKeyIdAccess").asText();
										
											List<NxValidationRules> skipRules = nxValidationRulesRepository
													.findByValidationGroupAndOfferAndActiveAndFlowTypeAndName(
															"MANDATORY_DESIGN_DATA", nxValidationRule.getName(),
															StringConstants.CONSTANT_Y, MyPriceConstants.SOURCE_USRP, product);
											
											boolean designDataMissingAccess = submitToMyPriceService.skipCktProcessing(skipRules, locNode,
													paramMap);
											if (!designDataMissingAccess && isDesignProcessed(productSiteIdMap, MyPriceConstants.USRP_OFFER_NAME_MAP.containsKey(product) ? MyPriceConstants.USRP_OFFER_NAME_MAP.get(product) : product,
														nxSiteId)) {
												/*save access data without nexxus fallout
												 */
												ObjectNode locNodeCopy = (ObjectNode) locNode.deepCopy();
												locNodeCopy.remove("priceDetails");
												parentObj.remove(usageRule.getJsonarray());
												List<Object> accesObjectList = inrQualifyService.getCircuits(locNode,
														"$..priceDetails.[?(@.typeOfCharge=='A' && @.nexxusFallout!='Y')]");
												List<JsonNode> a=new ArrayList<>(); 
												if(accesObjectList!=null) {
													for (Object o : accesObjectList) {
														a.add(mapper.valueToTree(o));
													}
													locNodeCopy.set("priceDetails", new ArrayNode(nf, 0).addAll(a));
													parentObj.set(usageRule.getJsonarray(),
															new ArrayNode(nf, 0).add(locNodeCopy));

													paramMap.put(MyPriceConstants.NX_KEY_ID, nxKeyIdAccess);
													paramMap.put(MyPriceConstants.PRODUCT_NAME, product);
													saveNxInrDesign(nxSolutionDetail, parentObj, paramMap, designMap);
													paramMap.remove(MyPriceConstants.PRODUCT_NAME);
													paramMap.remove(MyPriceConstants.NX_KEY_ID);
												}
											
										}
										
									}
								  }
								}
							}
						} 
						
					}else {
							boolean designDataMissing = submitToMyPriceService.skipCktProcessing(skipCktRules, parentNode,
									paramMap);
							if (!designDataMissing) {
								String nxKeyId = String
										.valueOf(nexxusJsonUtility.getValue(parentNode, usageRule.getJsonValidationPath()));
								Object obj = nexxusJsonUtility.getValue(parentNode, values[0]);
								if (obj == null && values.length > 1) {
									obj = nexxusJsonUtility.getValue(parentNode, values[1]);
								}
								Long nxSiteId = Long.parseLong(String.valueOf(obj));
								if (isDesignProcessed(productSiteIdMap, nxValidationRule.getOffer(), nxSiteId)) {
									paramMap.put(MyPriceConstants.NX_KEY_ID, nxKeyId);
									saveNxInrDesign(nxSolutionDetail, parentNode, paramMap, designMap);
									paramMap.remove(MyPriceConstants.NX_KEY_ID);
								}
							}
						}
					
					if (!designMap.isEmpty() && (designMap.size() == 10 || parentNodes.size() == (i + 1))) {
						nxInrDesignRepository.saveAll(designMap.values());
						designMap.clear();
					}
				}
				nxKeyIdNxSiteIds = null;
			}
		} else {
			paramMap.put(MyPriceConstants.NX_KEY_ID,
					nxValidationRule.getOffer() + "_" + nxOutputFileModel.getNxRequestDetails().getNxReqId());
			paramMap.put(MyPriceConstants.SAVE_MP_OUTPUT_JSON_OBJECT_NODE, StringConstants.CONSTANT_Y);
			JsonNode parentNode = mapper.readTree(nxOutputFileModel.getMpOutputJson());
			saveNxInrDesign(nxSolutionDetail, parentNode, paramMap, designMap);
			paramMap.remove(MyPriceConstants.NX_KEY_ID);
			paramMap.remove(MyPriceConstants.SAVE_MP_OUTPUT_JSON_OBJECT_NODE);
			nxInrDesignRepository.saveAll(designMap.values());
		}
	}

	@Transactional
	protected NxInrDesign saveNxInrDesign(NxSolutionDetail nxSolutionDetail, JsonNode design,
			Map<String, Object> paramMap, Map<String, NxInrDesign> designDataMap) throws IOException {
		Long nxReqId = paramMap.containsKey(MyPriceConstants.NX_REQ_ID)
				? (Long) paramMap.get(MyPriceConstants.NX_REQ_ID)
				: 0L;
		String productName = (String) paramMap.get(MyPriceConstants.PRODUCT_NAME);
		boolean saveMpOutputJsonObjectNode = StringConstants.CONSTANT_Y
				.equals(paramMap.get(MyPriceConstants.SAVE_MP_OUTPUT_JSON_OBJECT_NODE));
		String nxKeyId = paramMap.get(MyPriceConstants.NX_KEY_ID) != null
				? ((String) paramMap.get(MyPriceConstants.NX_KEY_ID)).trim()
				: null;
		int mpRestDesignLimit = (int) paramMap.get(MyPriceConstants.MP_REST_DESIGN_LIMIT);
		NxInrDesign nxInrDesign = null;
		if (!designDataMap.isEmpty() && designDataMap.containsKey(nxKeyId)) {
			nxInrDesign = designDataMap.get(nxKeyId);
		} else {
			nxInrDesign = nxInrDesignRepository.findByNxSolutionIdAndCircuitIdAndActiveYN(
					nxSolutionDetail.getNxSolutionId(), nxKeyId, StringConstants.CONSTANT_Y);
		}
		String isReconfiure = paramMap.get("IS_RECONFIGURE") != null ? (String) paramMap.get("IS_RECONFIGURE") : null;
		String nxMpStatusInd = paramMap.containsKey("NX_MP_STATUS_IND") ? paramMap.get("NX_MP_STATUS_IND").toString()
				: null;
		if (null != isReconfiure && isReconfiure.equalsIgnoreCase("inrReconfigure")
				&& StringConstants.CONSTANT_Y.equalsIgnoreCase(nxMpStatusInd)
				&& Optional.ofNullable(nxInrDesign).isPresent()
				&& !MyPriceConstants.REST_MP_API_CONST.contains(nxInrDesign.getStatus())) {
			nxInrDesign.setStatus(MyPriceConstants.REST_API_UPDATED);
			nxInrDesign.setNxInrDesignDetails(new ArrayList<NxInrDesignDetails>());
		}
		if (null == nxInrDesign) {
			nxInrDesign = new NxInrDesign();
			nxInrDesign.setStatus(MyPriceConstants.REST_API_NOT_INVOKED);
		}
		nxInrDesign.setNxSolutionId(nxSolutionDetail.getNxSolutionId());
		nxInrDesign.setCircuitId(nxKeyId);
		nxInrDesign.setActiveYN(StringConstants.CONSTANT_Y);
		// creating design details
		boolean newDesign = false;
		List<NxInrDesignDetails> details = null;
		if (nxInrDesign.getNxInrDesignDetails() != null && !nxInrDesign.getNxInrDesignDetails().isEmpty()) {
			if (paramMap.containsKey(MyPriceConstants.SUB_PRODUCT) && paramMap.get(MyPriceConstants.SUB_PRODUCT) != null
					&& (MyPriceConstants.MULTIPLE_CONFIG + "_" + productName)
							.equalsIgnoreCase((String) paramMap.get(MyPriceConstants.SUB_PRODUCT))) {
				details = nxInrDesign.getNxInrDesignDetails().stream()
						.filter(req -> req.getProduct().equalsIgnoreCase(productName)
								&& req.getNxCountThreshold() == null
								&& MyPriceConstants.MULTIPLE_CONFIG.equalsIgnoreCase(req.getSubProduct()))
						.collect(Collectors.toList());
			} else {
				details = nxInrDesign.getNxInrDesignDetails().stream().filter(
						req -> req.getProduct().equalsIgnoreCase(productName) && req.getNxCountThreshold() == null)
						.collect(Collectors.toList());
			}

			if (CollectionUtils.isNotEmpty(details)) {
				if (!saveMpOutputJsonObjectNode) {
					List<JsonNode> designs = mapper.readValue(details.get(0).getDesignData(),
							new TypeReference<List<JsonNode>>() {
							});
					
					designs.add(design);
					details.get(0).setDesignData(designs.toString());
					if (designs.size() == mpRestDesignLimit) {
						details.get(0).setNxCountThreshold(StringConstants.CONSTANT_Y);
						// paramMap.put("THRESHOLD_REACHED", nxKeyId);
					}
					designs = null;
				} else {
					String designString = design.toString();
					details.get(0).setDesignData(designString);
				}
			} else {
				newDesign = true;
			}

		} else {
			newDesign = true;
		}
		if (newDesign) {
			NxInrDesignDetails nxInrDesignDetail = new NxInrDesignDetails();
			List<JsonNode> designs;
			if (!saveMpOutputJsonObjectNode) {
				designs = new ArrayList<JsonNode>() {
					{
						add(design);
					}
				};
				nxInrDesignDetail.setDesignData(designs.toString());
			} else {
				String designString = design.toString();
				nxInrDesignDetail.setDesignData(designString);
			}
			nxInrDesignDetail.setProduct(productName);
			nxInrDesignDetail.setActiveYN(StringConstants.CONSTANT_Y);
			nxInrDesignDetail.setNxReqId(nxReqId);
			nxInrDesignDetail.setSubProduct(paramMap.containsKey(MyPriceConstants.SUB_PRODUCT)
					&& paramMap.get(MyPriceConstants.SUB_PRODUCT) != null
							? (String) paramMap.get(MyPriceConstants.SUB_PRODUCT)
							: null);
			nxInrDesignDetail.setStatus(MyPriceConstants.REST_API_NOT_INVOKED);
			nxInrDesign.addNxInrDesignDetails(nxInrDesignDetail);
			designs = null;
		}
		designDataMap.put(nxKeyId, nxInrDesign);
		return nxInrDesign;
	}

	// decide whether group is rest or soap
	protected Map<Long, String> findInrRestRequests(List<NxRequestDetails> nxRequestDetailList,
			Map<Long, String> restRequests) {
		Set<String> products = nxRequestDetailList.stream().map(a -> a.getProduct()).collect(Collectors.toSet());
		List<NxLookupData> nxLookupData = nxLookupDataRepository.findByItemIdsAndDatasetAndCriteriaAndDesc(products,
				MyPriceConstants.REST_PRODUCTS, StringConstants.CONSTANT_Y, MyPriceConstants.SOURCE_INR);
		if (products.size() == nxLookupData.size()) {
			for (NxRequestDetails nxRequest : nxRequestDetailList) {
				restRequests.put(nxRequest.getNxReqId(), StringConstants.CONSTANT_Y);
			}
		} else {
			for (NxRequestDetails nxRequest : nxRequestDetailList) {
				restRequests.put(nxRequest.getNxReqId(), StringConstants.CONSTANT_N);
			}
		}
		return restRequests;
	}

	protected Map<String, List<Map<String, List<Object>>>> createIglooData(
			List<NxAccessPricingData> nxAccessPricingDatas) {
		Map<String, List<Map<String, List<Object>>>> iglooConsolidatedMap = new HashMap<>();
		List<NxLookupData> lookUp = nxLookupDataRepository
				.findByDatasetName(MyPriceConstants.MP_REST_DESIGN_LIMIT_DATASET);
		int mpRestDesignLimit = Integer.parseInt(lookUp.get(0).getItemId());
		boolean quoteAdded = false;
		List<NxDesignAudit> nxDesignAudits = nxDesignAuditRepository.findByTransactionAndNxRefId(
				MyPriceConstants.DDA_CKTS_MRC, nxAccessPricingDatas.get(0).getNxSolutionId());
		Map<String, String> hMapData = new HashMap<>();
		if (CollectionUtils.isNotEmpty(nxDesignAudits)) {
			String cktdata = nxDesignAudits.get(0).getData();
			hMapData = Arrays.stream(cktdata.substring(1, cktdata.length() - 1).split(","))
					.map(s -> s.split("=")).collect(Collectors.toMap(s -> s[0].trim(), s -> s[1].trim()));
		}
		List<Long> reqIdList = nxRequestDetailsRepository.findNxReqIdByNxSolutionIdAndProduct(nxAccessPricingDatas.get(0).getNxSolutionId(), "AVPN");
		if (!reqIdList.isEmpty()) {
			for (Long reqId : reqIdList) {
				List<Object[]> avpnData =nxOutputFileRepository.fetchAVPNIntlData(reqId);
				for(Object[] o : avpnData) {
					hMapData.put((String) o[0], (String) o[1]);
				}
			}
		}
		
		List<Long> usrpAccessreqIdList=nxRequestDetailsRepository.findUsrpNxReqIdByNxSolutionIdAndProduct(nxAccessPricingDatas.get(0).getNxSolutionId(), MyPriceConstants.USRP_ACCESS_PRODUCT);	
		if (!usrpAccessreqIdList.isEmpty()) {
			for (Long reqId : usrpAccessreqIdList) {
				List<Object[]> avpnData =nxOutputFileRepository.fetchAceessData(reqId);
				for(Object[] o : avpnData) {
					hMapData.put(o[0].toString().replaceAll("\\W", ""), (String) o[1]);
				}
			}
		}

		
		for (NxAccessPricingData accessPricingData : nxAccessPricingDatas) {
				if (MyPriceConstants.Existing.equalsIgnoreCase(accessPricingData.getLocationYn()) && accessPricingData.getCircuitId() != null && hMapData != null && hMapData.keySet().contains(accessPricingData.getCircuitId().replaceAll("\\W", ""))) {
				} else {
					quoteAdded = false;
					String consolidationCriteriaVal = accessPricingData.getConsolidation_criteria();
						if (iglooConsolidatedMap.containsKey(consolidationCriteriaVal)) {
							List<Map<String, List<Object>>> nxAccessPricingDataExistList = iglooConsolidatedMap
									.get(consolidationCriteriaVal);
							for (int i = 0; i < nxAccessPricingDataExistList.size(); i++) {
								if (!quoteAdded) {
									// Limiting the number of quotes that has to be send to my price in one request
									if (nxAccessPricingDataExistList.get(i).get("jsonList").size() == mpRestDesignLimit) {
										if (i == nxAccessPricingDataExistList.size() - 1) {
											Map<String, List<Object>> nxAccessPriceMapNew = new HashMap<>();
											List<Object> nxAccessPricingJsonNewList = new ArrayList<>();
											JSONObject enhancedIntermediateJsonData = getAccessPricingDataEnhancedJson(
													accessPricingData, hMapData);
											nxAccessPricingJsonNewList.add(enhancedIntermediateJsonData);
											List<Object> nxAccessPricingIDNewList = new ArrayList<>();
											nxAccessPricingIDNewList.add(accessPricingData.getNxAccessPriceId());
											nxAccessPriceMapNew.put("jsonList", nxAccessPricingJsonNewList);
											nxAccessPriceMapNew.put("nxAccessPricId", nxAccessPricingIDNewList);
											nxAccessPricingDataExistList.add(nxAccessPriceMapNew);
											quoteAdded = true;
										}
								} else {
									Map<String, List<Object>> nxAccessPriceMapExisting = nxAccessPricingDataExistList
											.get(i);
									List<Object> nxAccessPricingJsonExistingList = nxAccessPriceMapExisting
											.get("jsonList");
									JSONObject enhancedIntermediateJsonData = getAccessPricingDataEnhancedJson(
											accessPricingData, hMapData);
									nxAccessPricingJsonExistingList.add(enhancedIntermediateJsonData);
									List<Object> nxAccessPricingIDExistingList = nxAccessPriceMapExisting
											.get("nxAccessPricId");
									nxAccessPricingIDExistingList.add(accessPricingData.getNxAccessPriceId());
									quoteAdded = true;
								}
							}
						}
					} else {
						List<Map<String, List<Object>>> nxAccessPricingDataNewList = new ArrayList<>();
						Map<String, List<Object>> nxAccessPriceMapNew = new HashMap<>();
						List<Object> nxAccessPricingJsonNewList = new ArrayList<>();
						JSONObject enhancedIntermediateJsonData = getAccessPricingDataEnhancedJson(accessPricingData,
								hMapData);
						nxAccessPricingJsonNewList.add(enhancedIntermediateJsonData);
						List<Object> nxAccessPricingIDNewList = new ArrayList<>();
						nxAccessPricingIDNewList.add(accessPricingData.getNxAccessPriceId());
						nxAccessPriceMapNew.put("jsonList", nxAccessPricingJsonNewList);
						nxAccessPriceMapNew.put("nxAccessPricId", nxAccessPricingIDNewList);
						nxAccessPricingDataNewList.add(nxAccessPriceMapNew);
						iglooConsolidatedMap.put(consolidationCriteriaVal, nxAccessPricingDataNewList);
					}
				}
			}
		
		return iglooConsolidatedMap;
	}

	protected JSONObject getAccessPricingDataEnhancedJson(NxAccessPricingData nxAccessPricingData,
			Map<String, String> hMapData) {
		JSONObject data = JacksonUtil.toJsonObject(nxAccessPricingData.getIntermediateJson());
		String countryIsoCode = nxAccessPricingData.getCustCountry();
		data.put("country", countryIsoCode);
		String locationYn = nxAccessPricingData.getLocationYn();
		if (Optional.ofNullable(locationYn).isPresent()) {
			data.put("portQtyPf", locationYn);
		} else {
			data.put("portQtyPf", "New");
		}
		if (null != countryIsoCode && countryIsoCode.trim().equalsIgnoreCase("US")) {
			data.put("accessArch",
					null != data.get("accessArch") ? data.get("accessArch").toString().toUpperCase() : "");
			if (data.get("quoteType").equals("Custom Quote")) {

				try {
					data.put("splConstCost", nxAccessPricingData.getSplConstructionCostNRC());
					data.put("splConstPrice", nxAccessPricingData.getSplConstructionCharges());
					data.put("mrc", null != data.get("mrc") ? roundOff(data.get("mrc").toString(), 2) : "");
					data.put("nrc",
							null != data.get("nonRecurringCharge")
									? roundOff(data.get("nonRecurringCharge").toString(), 2)
									: "");
					if (null != data.get("bandwidth")) {
						if (nxAccessPricingData.getEthernetLcPopMonthlyRecurringCost() != null && java.util.Optional
								.ofNullable(nxAccessPricingData.getEthernetLcPopMonthlyRecurringCost()).isPresent()) {
							data.put("mrcCost",
									evalcalculation("LC_MRC", Double.valueOf(nxAccessPricingData.getAccessBandwidth()),
											Double.valueOf(nxAccessPricingData.getEthernetLcPopMonthlyRecurringCost()),
											Double.valueOf(nxAccessPricingData.getEthernetLcMonthlyRecurringCost())));
						}
						if (nxAccessPricingData.getEthernetLcPopNonRecurringCost() != null && java.util.Optional
								.ofNullable(nxAccessPricingData.getEthernetLcPopNonRecurringCost()).isPresent()) {
							data.put("nrcCost",
									evalcalculation("LC_NRC", Double.valueOf(nxAccessPricingData.getAccessBandwidth()),
											Double.valueOf(nxAccessPricingData.getEthernetLcPopNonRecurringCost()),
											Double.valueOf(nxAccessPricingData.getEthernetLcNonRecurringCost())));
						}
						logger.info("bandwidth is not null");
					} else if (null != data.get("reqAccessBandwidth")) {
						if (nxAccessPricingData.getEthernetLcPopMonthlyRecurringCost() != null && java.util.Optional
								.ofNullable(nxAccessPricingData.getEthernetLcPopMonthlyRecurringCost()).isPresent()) {
							data.put("mrcCost",
									evalcalculation("LC_MRC", Double.valueOf(nxAccessPricingData.getSpeed()),
											Double.valueOf(nxAccessPricingData.getEthernetLcPopMonthlyRecurringCost()),
											Double.valueOf(nxAccessPricingData.getEthernetLcMonthlyRecurringCost())));
						}
						if (nxAccessPricingData.getEthernetLcPopNonRecurringCost() != null && java.util.Optional
								.ofNullable(nxAccessPricingData.getEthernetLcPopNonRecurringCost()).isPresent()) {
							data.put("nrcCost",
									evalcalculation("LC_NRC", Double.valueOf(nxAccessPricingData.getSpeed()),
											Double.valueOf(nxAccessPricingData.getEthernetLcPopNonRecurringCost()),
											Double.valueOf(nxAccessPricingData.getEthernetLcNonRecurringCost())));
						}
						logger.info("speed is not null");
					}
				} catch (NullPointerException e) {
					logger.error("speed or bandwith is having null");
				}
			}
		}else {
			//for mow
			data.put("mrc", null != nxAccessPricingData.getMrc() ? roundOff(nxAccessPricingData.getMrc(), 2) : "");
			data.put("nrc",null != nxAccessPricingData.getNrc()? roundOff(nxAccessPricingData.getNrc(), 2) : "");

		}
		data.put("reqAccessBandwidth",
				null != data.get("reqAccessBandwidth") ? data.get("reqAccessBandwidth").toString() + " Mbps" : null);
		data.put("bandwidth", null != data.get("bandwidth") ? data.get("bandwidth").toString() + " Mbps" : null);

		data.put("nxAccessPriceId", nxAccessPricingData.getNxAccessPriceId());

		if ((MyPriceConstants.Migration.equalsIgnoreCase(locationYn) || MyPriceConstants.Growth.equalsIgnoreCase(locationYn))
				&& hMapData != null && !hMapData.isEmpty() && nxAccessPricingData.getCircuitId() != null) {
			String igloocircuitId=nxAccessPricingData.getCircuitId().replaceAll("\\W", "");
			if(hMapData.containsKey(igloocircuitId)) {
				data.put("existingDDAMrc", hMapData.get(igloocircuitId));

			}
			
		}
		return data;
	}

	protected String roundOff(String value, int scale) {
		try {
			if (java.util.Optional.ofNullable(value).isPresent() && !value.isEmpty()) {
				BigDecimal bd = new BigDecimal(value);
				bd = bd.setScale(scale, BigDecimal.ROUND_HALF_EVEN);
				return bd.toString();
			}
		} catch (Exception e) {
			logger.error("Error : While rounding off value : ", e);
		}
		return null;
	}

	protected String getProduct(String input) {
		Map<String, List<String>> productInfoMap = configAndUpdatePricingUtilInr
				.getConfigProdutMapFromLookup(MyPriceConstants.INR_REST_PRODUCTS);
		for (Map.Entry<String, List<String>> criteriaMap : productInfoMap.entrySet()) {
			List<String> criteria = criteriaMap.getValue();
			if (criteria.contains(input)) {
				return criteriaMap.getKey();
			}
		}
		return input;
	}

	protected boolean isDesignProcessed(Map<Long, List<Map<String, List<Long>>>> grpIdNxKeyIdNxSiteIds, String nxKeyId,
			Long nxSiteId, Long groupId) {
		if (grpIdNxKeyIdNxSiteIds.containsKey(groupId)) {
			List<Map<String, List<Long>>> nxKeyIdNxSiteIds = grpIdNxKeyIdNxSiteIds.get(groupId);
			if (nxKeyIdNxSiteIds.stream().filter(n -> n.containsKey(nxKeyId)).findFirst().isPresent()) {
				Map<String, List<Long>> nxKeyIdNxSiteId = nxKeyIdNxSiteIds.stream().filter(n -> n.containsKey(nxKeyId))
						.findFirst().get();
				if (nxKeyIdNxSiteId.get(nxKeyId).contains(nxSiteId)) {
					return false;
				} else {
					nxKeyIdNxSiteId.get(nxKeyId).add(nxSiteId);
					return true;
				}

			} else {
				Map<String, List<Long>> nxKeyIdNxSiteId = new HashMap<String, List<Long>>();
				List<Long> siteIds = new ArrayList<Long>();
				siteIds.add(nxSiteId);
				nxKeyIdNxSiteId.put(nxKeyId, siteIds);
				grpIdNxKeyIdNxSiteIds.get(groupId).add(nxKeyIdNxSiteId);
				return true;
			}
		} else {
			Map<String, List<Long>> nxKeyIdNxSiteId = new HashMap<String, List<Long>>();
			List<Long> siteIds = new ArrayList<Long>();
			siteIds.add(nxSiteId);
			nxKeyIdNxSiteId.put(nxKeyId, siteIds);
			grpIdNxKeyIdNxSiteIds.put(groupId, new ArrayList<Map<String, List<Long>>>() {
				{
					add(nxKeyIdNxSiteId);
				}
			});
			return true;
		}

	}

	protected boolean isDesignProcessed(Map<String, List<String>> productNxSiteIds, String product, Long nxSiteId) {
		if (productNxSiteIds.containsKey(product)) {
			List<String> nxSiteIds = productNxSiteIds.get(product);
			if (nxSiteIds.contains(String.valueOf(nxSiteId))) {
				return false;
			} else {
				productNxSiteIds.get(product).add(String.valueOf(nxSiteId));
				return true;
			}
		} else {
			List<String> siteIds = new ArrayList<String>();
			siteIds.add(String.valueOf(nxSiteId));
			productNxSiteIds.put(product, siteIds);
			return true;
		}

	}

	public Map<String, Object> getErrorDetails(Map<String, Object> requestMap) {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		responseMap.put(MyPriceConstants.RESPONSE_STATUS,
				(requestMap.containsKey(MyPriceConstants.RESPONSE_STATUS)
						&& requestMap.get(MyPriceConstants.RESPONSE_STATUS) != null)
								? (boolean) requestMap.get(MyPriceConstants.RESPONSE_STATUS)
								: false);
		responseMap.put(CustomJsonConstants.ERROR_WS_NAME,
				(requestMap.containsKey(CustomJsonConstants.ERROR_WS_NAME)
						&& requestMap.get(CustomJsonConstants.ERROR_WS_NAME) != null)
								? (String) requestMap.get(CustomJsonConstants.ERROR_WS_NAME)
								: null);
		if (requestMap.containsKey(CustomJsonConstants.REST_RESPONSE_ERROR)) {
			responseMap.put(CustomJsonConstants.REST_RESPONSE_ERROR,
					requestMap.get(CustomJsonConstants.REST_RESPONSE_ERROR));
		}
		if (requestMap.containsKey(CustomJsonConstants.CONFIG_BOM_ERROR_DATA)) {
			responseMap.put(CustomJsonConstants.CONFIG_BOM_ERROR_DATA,
					requestMap.get(CustomJsonConstants.CONFIG_BOM_ERROR_DATA));
		}
		if (requestMap.containsKey(CustomJsonConstants.SITE_CONFIG_ERROR_MAP)) {
			responseMap.put(CustomJsonConstants.SITE_CONFIG_ERROR_MAP,
					requestMap.get(CustomJsonConstants.SITE_CONFIG_ERROR_MAP));
		}
		return responseMap;
	}

	/**
	 * Prints total duration of the operation
	 * 
	 * @param currentTime
	 * @param startTime
	 * @param operation
	 */
	protected void printTotalDuration(Long currentTime, Long startTime, String operation, Long nxSolutionId) {
		Long endTime = System.currentTimeMillis() - currentTime;
		String totalDuration = new StringBuilder().append(operation + " for solution id :: ").append(nxSolutionId)
				.append(" took :: ").append((endTime - startTime)).append(" ").append(MyPriceConstants.MILLISEC)
				.toString();
		logger.info(totalDuration);
	}

	@SuppressWarnings("unchecked")
	protected void prepareFailedCkt(Map<String, Object> response, String product, String design,
			List<NxValidationRules> restErrRules, Map<String, List<RestErrors>> designErr,
			List<NxLookupData> usageNonusageProductList,String requestProductCd) {
		logger.info("Start prepareFailedCkt");
		/*NxValidationRules nxValidationRule = restErrRules.stream().filter(v -> v.getName().equalsIgnoreCase(product)
				&& ((response.get(MyPriceConstants.PRODUCT_TYPE_REST_ERROR) == null && v.getDescription() == null)
						|| (response.get(MyPriceConstants.PRODUCT_TYPE_REST_ERROR) != null && response
								.get(MyPriceConstants.PRODUCT_TYPE_REST_ERROR).toString().equals(v.getDescription()))))
				.findFirst().orElse(null);*/
		NxValidationRules nxValidationRule =null;
		if(requestProductCd !=null) {
			nxValidationRule=restErrRules.stream().filter(v -> v.getName().equalsIgnoreCase(product) &&
					(response.get(MyPriceConstants.PRODUCT_TYPE) != null) &&
					v.getFlowType().equalsIgnoreCase(response.get(MyPriceConstants.PRODUCT_TYPE).toString())
					&& v.getOffer().equalsIgnoreCase(requestProductCd)).findFirst().orElse(null);
		}
		if (nxValidationRule != null) {
			String[] keys = nxValidationRule.getValue().split(":");
			String[] path = nxValidationRule.getDataPath().split(":");
			List<Object> datas = nexxusJsonUtility.getValueLst(design, path[0]);
			if (!designErr.containsKey(nxValidationRule.getOffer())) {
				designErr.put(nxValidationRule.getOffer(), new ArrayList<RestErrors>());
			}
			if (nxValidationRule.getSubDataPath() != null) {
				List<Object> usrpData = new ArrayList<>();
				for (Object data: datas) {
					String[] subDataPath = nxValidationRule.getSubDataPath().split(":");
					List<Object> falloutMatchingIds = nexxusJsonUtility.getValueLst(data, subDataPath[0]);
					Object nxSiteId = nexxusJsonUtility.getValue(data, subDataPath[1]);
					for (int i = 0; i < falloutMatchingIds.size(); i++) {
						LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
						map.put(keys[0], falloutMatchingIds.get(i));
						if (i == 0) {
							map.put(keys[1], nxSiteId);
						}
						usrpData.add(map);
					}
				}
				datas = usrpData;
			}
			List<RestErrors> prodErrors = designErr.get(nxValidationRule.getOffer());
			for (Object data : datas) {
				LinkedHashMap<String, Object> map = null;
				if (data instanceof LinkedHashMap) {
					map = (LinkedHashMap<String, Object>) data;
				} else {
					map = new LinkedHashMap<String, Object>();
					map.put(keys[0], data);
				}
				//usrp uses FALLOUTMATCHINGID as cktId
				String cktId = String.valueOf(map.get(keys[0]));
				RestErrors restError=null;
				String[] usageProd = usageNonusageProductList.get(0).getDescription().split("\\s*,\\s*");
				Set<String> usageNonUsageProducts=new HashSet<>(Arrays.asList(usageProd));

				if(usageNonUsageProducts.contains(product)) {
					restError = prodErrors.stream().filter(r -> r.getNxsiteMatchingId().equalsIgnoreCase(cktId))
							.findFirst().orElse(null);
				}else {
					restError = prodErrors.stream().filter(r -> r.getCircuitId().equalsIgnoreCase(cktId))
							.findFirst().orElse(null);
				}
				if (restError == null) {
					restError = new RestErrors();
					prodErrors.add(restError);
				}
				if(usageNonUsageProducts.contains(product)) {
					restError.setNxsiteMatchingId(cktId);
				}else {	
					restError.setCircuitId(cktId);
				}
				Set<String> errMsg = restError.getMessages() != null
						? new HashSet<String>(Arrays.asList(restError.getMessages().split(".")))
						: new HashSet<String>();
				if (response.containsKey(CustomJsonConstants.REST_RESPONSE_ERROR)
						&& null != response.get(CustomJsonConstants.REST_RESPONSE_ERROR)) {
					errMsg.addAll((Set<String>) response.get(CustomJsonConstants.REST_RESPONSE_ERROR));
				}
				if (response.containsKey(CustomJsonConstants.CONFIG_BOM_ERROR_DATA)
						&& null != response.get(CustomJsonConstants.CONFIG_BOM_ERROR_DATA)) {
					errMsg.addAll((Set<String>) response.get(CustomJsonConstants.CONFIG_BOM_ERROR_DATA));
				}
				if (response.containsKey(CustomJsonConstants.SITE_CONFIG_ERROR_MAP)
						&& null != response.get(CustomJsonConstants.SITE_CONFIG_ERROR_MAP)) {
					Map<String, Set<String>> siteConfigErr = (Map<String, Set<String>>) response
							.get(CustomJsonConstants.SITE_CONFIG_ERROR_MAP);
					if (path.length > 1) {
						// dda processing
						Set<Object> uniqueIds = new HashSet<Object>(
								nexxusJsonUtility.getValueLst(design, path[1].replace("<REPLACE>", cktId)));
						for (Object site : uniqueIds) {
							String siteId = String.valueOf(site);
							if (siteConfigErr.containsKey(siteId)) {
								errMsg.addAll(siteConfigErr.get(siteId));
							}
						}
						uniqueIds = null;
					} else {
						if (map.get(keys[1]) != null && siteConfigErr.containsKey(String.valueOf(map.get(keys[1])))) {
							errMsg.addAll(siteConfigErr.get(String.valueOf(map.get(keys[1]))));
						}
					}
				}
				restError.setMessages(String.join(".", errMsg));
			}
			designErr.put(nxValidationRule.getOffer(), prodErrors);
		}
		logger.info("End prepareFailedCkt");
	}

	protected double evalcalculation(String dataSet, double speed, double val1, double val2) {
		double res = 0.0;
		List<NxLookupData> rulesData = nxLookupDataRepository.findByDatasetNameAndItemIdAndDescription(dataSet, "IGLOO",
				null);
		// String
		// mapDataEntry="speed>600?lcPopMrc:lcPopMrc*((speed/1000)*(1/0.7))::round:2##evaluate##speed,lcPopMrc";
		// String
		// mapDataEntry="speed>600?lcPopNrc:0.0::round:2##evaluate##speed,lcPopNrc";
		String calculationLogic = rulesData.get(0).getCriteria();
		String values[] = calculationLogic.split("##");
		String[] data = values[2].split(",");
		String formulaExp = values[0];

		String formulaStepvalue[] = formulaExp.split("::");
		String formula = formulaStepvalue[0];
		String roundParse[] = formulaStepvalue[1].split(":");
		int roundPrecision = Integer.valueOf(roundParse[1]);
		boolean isEval = true;

		for (String str : data) {
			if (str.equals("speed")) {
				formula = formula.replace(str, String.valueOf(speed));
			} else if (str.equals("lcMrc") || str.equals("lcNrc")) {
				formula = formula.replace(str, String.valueOf(val2));
			} else {
				formula = formula.replace(str, String.valueOf(val1));
			}
		}
		if (isEval) {
			try {
				Object result = Eval.me(formula);
				res = Double.parseDouble(String.valueOf(result));
				res = roundPrecision != 2 ? getRoundValue(res, roundPrecision) : res;
			} catch (ArithmeticException e) {
				res = 0.0;
			}
		}

		return res;
	}

	private double getRoundValue(double value, int places) {
		double result = 0;
		if (places > 0) {
			BigDecimal bd = new BigDecimal(Double.toString(value));
			bd = bd.setScale(places, RoundingMode.HALF_UP);
			result = bd.doubleValue();
		} else if (places == 0) {
			result = Math.round(value);
		}
		return result;
	}

	protected Map<String, List<String>> prepareProductSiteIdMap(Long nxSolutionId) {
		Map<String, List<String>> data = new LinkedHashMap<String, List<String>>();
		List<NxSiteidProductMapping> nxSiteidProductMappings = nxSiteidProductMappingRepo
				.findByNxSolutionId(nxSolutionId);
		for (NxSiteidProductMapping nxSiteidProductMapping : nxSiteidProductMappings) {
			data.put(nxSiteidProductMapping.getProduct(),
					new LinkedList<String>(Arrays.asList(nxSiteidProductMapping.getNxSiteId()
							.substring(1, nxSiteidProductMapping.getNxSiteId().length() - 1).trim()
							.split("\\s*,\\s*"))));
		}
		nxSiteidProductMappings = null;
		return data;
	}

	protected void persistProductSiteMap(Long nxSolutionId, Map<String, List<String>> data) {
		List<NxSiteidProductMapping> updatedMapping = new ArrayList<NxSiteidProductMapping>();
		List<NxSiteidProductMapping> nxSiteidProductMappings = nxSiteidProductMappingRepo
				.findByNxSolutionId(nxSolutionId);
		for (Map.Entry<String, List<String>> entry : data.entrySet()) {
			NxSiteidProductMapping nxSiteidProductMapping = nxSiteidProductMappings.stream()
					.filter(n -> n.getProduct().equalsIgnoreCase(entry.getKey())).findFirst().orElse(null);
			if (nxSiteidProductMapping == null) {
				nxSiteidProductMapping = new NxSiteidProductMapping();
				nxSiteidProductMapping.setNxSolutionId(nxSolutionId);
				nxSiteidProductMapping.setProduct(entry.getKey());
				nxSiteidProductMapping.setNxSiteId(entry.getValue().toString());
			} else {
				nxSiteidProductMapping.setNxSiteId(entry.getValue().toString());
			}
			updatedMapping.add(nxSiteidProductMapping);
		}
		nxSiteidProductMappingRepo.saveAll(updatedMapping);
		nxSiteidProductMappingRepo.flush();
		nxSiteidProductMappings = null;
		updatedMapping = null;
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
	protected boolean hasPort(JsonNode node) {
		List<JsonNode> priceTypes = node.findValues("priceType");
		for (JsonNode n : priceTypes) {
			if ("PORTBEID".equals(n.asText())) {
				return true;
			}
		}
		return false;
	}
}
