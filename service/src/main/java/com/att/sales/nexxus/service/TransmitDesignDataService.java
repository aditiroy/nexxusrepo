package com.att.sales.nexxus.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.constant.CommonConstants;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.constant.MessageConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.constant.TDDConstants;
import com.att.sales.nexxus.dao.model.NxDesign;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxDesignDetails;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxUdfMapping;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxDesignRepository;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.dmaap.mr.util.DmaapPublishEventsServiceImpl;
import com.att.sales.nexxus.model.MailResponse;
import com.att.sales.nexxus.myprice.transaction.service.AutomationFlowHelperService;
import com.att.sales.nexxus.nxPEDstatus.model.SolutionDetails;
import com.att.sales.nexxus.ped.dmaap.model.EndPointDetails;
import com.att.sales.nexxus.ped.dmaap.model.NxPEDStatusDMaap;
import com.att.sales.nexxus.reteriveicb.model.ActionDeterminants;
import com.att.sales.nexxus.reteriveicb.model.Component;
import com.att.sales.nexxus.reteriveicb.model.DesignDetailsTDD;
import com.att.sales.nexxus.reteriveicb.model.NexxusTDDMessage;
import com.att.sales.nexxus.reteriveicb.model.Offer;
import com.att.sales.nexxus.reteriveicb.model.Port;
import com.att.sales.nexxus.reteriveicb.model.RetreiveICBPSPRequest;
import com.att.sales.nexxus.reteriveicb.model.RetreiveICBPSPResponse;
import com.att.sales.nexxus.reteriveicb.model.Site;
import com.att.sales.nexxus.reteriveicb.model.Solution;
import com.att.sales.nexxus.reteriveicb.model.UDFBaseData;
import com.att.sales.nexxus.transmitdesigndata.model.CircuitDetails;
import com.att.sales.nexxus.transmitdesigndata.model.EndpointDetails;
import com.att.sales.nexxus.transmitdesigndata.model.PedToUdfMapper;
import com.att.sales.nexxus.transmitdesigndata.model.PortDetails;
import com.att.sales.nexxus.transmitdesigndata.model.RequestDetails;
import com.att.sales.nexxus.transmitdesigndata.model.SolutionStatus;
import com.att.sales.nexxus.transmitdesigndata.model.TransmitDesignDataRequest;
import com.att.sales.nexxus.transmitdesigndata.model.TransmitDesignDataResponse;
import com.att.sales.nexxus.util.DME2RestClient;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.JsonPathExpressionBuilder;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.att.sales.nexxus.util.ThreadMetaDataUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.TypeRef;

/**
 * The Class TransmitDesignDataService.
 */
@Service
public class TransmitDesignDataService extends BaseServiceImpl {

	private static Logger logger = LoggerFactory.getLogger(TransmitDesignDataService.class);

	@Autowired
	private JsonPathUtil jsonPathUtil;

	@Autowired
	private DmaapPublishEventsServiceImpl dmaapPublishEventsServiceImpl;

	@Autowired
	private MailServiceImpl mailService;

	@Autowired
	private DME2RestClient dME2RestClient;

	@Autowired
	private TransmitDesignDataRepoService tddRepositoryService;
	private Set<String> errorCd = null;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private NxDesignAuditRepository nxDesignAuditRepository;

	@Autowired
	private NxMpDealRepository nxMpDealRepository;

	@Autowired
	private NexxusJsonUtility nexxusJsonUtility;

	@Autowired
	private AutomationFlowHelperService automationFlowHelperService;

	public Boolean isDesignChange = false;

	@Value("${tdd.dpp.trigger.ADE:N}")
	private String isDppTriggerADE;

	@Value("${tdd.dpp.trigger:N}")
	private String isDppTrigger;

	@Autowired
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;

	@Autowired
	private NxDesignRepository nxDesignRepository;

	@Value("${tdd.dpp.trigger.ASE:N}")
	private String isDppTriggerASE;

	/**
	 * Execute.
	 *
	 * @param request the request
	 * @return the transmit design data response
	 */
	@Transactional(rollbackOn = Exception.class)
	public ServiceResponse execute(TransmitDesignDataRequest request) {
		logger.info("Inside execute method for transmitDesignData", "");
		logger.info("TransmitDesignDataRequest is: {}", JacksonUtil.toString(request));
		TransmitDesignDataResponse response = new TransmitDesignDataResponse();
		errorCd = new HashSet<>();
		Map<String, Object> inputMap = new HashMap<>();
		Map<String, Object> requestMetaDataMap = new HashMap<>();
		if (ServiceMetaData.getRequestMetaData() != null) {
			ServiceMetaData.getRequestMetaData().forEach((key, value) -> requestMetaDataMap.put(key, value));
			inputMap.put(InrConstants.REQUEST_META_DATA_KEY, requestMetaDataMap);
		}
		// getting all PED status from lookup table
		Map<String, NxLookupData> pedStatusMap = tddRepositoryService.getPedStatusMap();
		inputMap.put(TDDConstants.PED_STATUS_MAP, pedStatusMap);

		Optional.ofNullable(request.getSolutionStatus()).map(List::stream).orElse(Stream.empty())
				.filter(Objects::nonNull).forEach(pedSolutionStatus -> {
					boolean dataNotfound = false;
					logger.info("Inside execute method for transmitDesignData for solutionId {}",
							pedSolutionStatus.getSolutionId());
					NxSolutionDetail solutionDtls = null;
					String ipeInd = null;
					String flowType = null;
					String slcIndicator = null;
					String atxCallInd = null;
					List<Object[]> nxSol = nxSolutionDetailsRepository
							.findNxSolutionByExternalKey(Long.valueOf(pedSolutionStatus.getSolutionId()));
					for (Object[] row : nxSol) {
						solutionDtls = new NxSolutionDetail();
						solutionDtls.setNxSolutionId((Long) row[0]);
						solutionDtls.setAutomationFlowInd((String) row[1]);
						solutionDtls.setCreatedUser((String) row[2]);
						flowType = (String) row[3];
						slcIndicator = (String) row[4];
						atxCallInd = (String) row[5];
						break;
					}
				
					if (StringConstants.SALES_IPNE.equalsIgnoreCase(flowType)) {
						ipeInd = StringConstants.CONSTANT_Y;	
					}else {
						ipeInd = nxSolutionDetailsRepository
								.findStandardPricingIndByExternalKey(Long.valueOf(pedSolutionStatus.getSolutionId()));
					}

					inputMap.put(TDDConstants.IPE_INDICATOR, ipeInd);
					inputMap.put(TDDConstants.FLOW_TYPE, flowType);
					inputMap.put(TDDConstants.SOL_COST_IND,slcIndicator);

					if (solutionDtls != null) {
						List<Object> asrItemIds = nexxusJsonUtility.getValueLst(pedSolutionStatus, "$..accessServiceRequestItemId");
						if (CollectionUtils.isNotEmpty(asrItemIds)) {
							List<NxDesign> nxDesigns = nxDesignRepository
									.findByNxSolutionIdAndAsrItemId(solutionDtls.getNxSolutionId(), asrItemIds);
							if (CollectionUtils.isNotEmpty(nxDesigns)) {
								solutionDtls.setNxDesign(nxDesigns);
								solutionDtls.setExternalKey(Long.valueOf(pedSolutionStatus.getSolutionId()));

								NxPEDStatusDMaap pedDmaapReq = this.createRequestForDmaap(pedSolutionStatus);
								inputMap.put(TDDConstants.SOLUTION_ID, solutionDtls.getExternalKey());
								inputMap.put(TDDConstants.NX_SOLUTION_ID, solutionDtls.getNxSolutionId());
								inputMap.put(TDDConstants.ATT_ID, solutionDtls.getCreatedUser());
								if (CollectionUtils.isNotEmpty(pedSolutionStatus.getPortDetails())) {
									processASEData(pedSolutionStatus, solutionDtls, pedDmaapReq, inputMap, response);
								}
								if (CollectionUtils.isNotEmpty(pedSolutionStatus.getCircuitDetails())) {
									processADEData(pedSolutionStatus, solutionDtls, pedDmaapReq, inputMap, response);
								}
								tddRepositoryService.saveNxDesings(solutionDtls.getNxDesign());
								nxDesigns = null;
								saveTddRequestResponse(pedSolutionStatus.getSolutionId(), request, response);
								solutionLevelDMaap(pedDmaapReq,
										(CircuitDetails) inputMap.get(TDDConstants.circuitDetails), inputMap,
										(NxDesign) inputMap.get(TDDConstants.nxDesign), pedSolutionStatus);
                                Long nxreqDesign = solutionDtls.getNxDesign().get(0).getNxDesignId().longValue();
								inputMap.put("req_status", nxreqDesign);
								// calling automation flow
								NxSolutionDetail sol = solutionDtls;
								String autoCall = "called";
								
								logger.info("ATX current status {}",solutionDtls.getNxDesign().get(0).getStatus());

								if("D".equalsIgnoreCase(solutionDtls.getNxDesign().get(0).getStatus()) && !StringConstants.SALES_IPNE.equalsIgnoreCase(flowType) 
										&& ("N".equalsIgnoreCase(slcIndicator) ||  slcIndicator == null)) {
					           		
									if(atxCallInd == null && atxCallInd != autoCall) {
	
									CompletableFuture.runAsync(() -> {
										try {
											ThreadMetaDataUtil.initThreadMetaData(requestMetaDataMap);
											nxSolutionDetailsRepository.updateAutoCallBySolutionId(autoCall,sol.getExternalKey());
											this.callAutomationFlow(sol, inputMap);
										} catch (Exception e) {
											logger.info("Exception", e);
											throw e;
										} finally {
											ThreadMetaDataUtil.destroyThreadMetaData();
										}
									    });
								    }else {
									       logger.info("ATX flow is Already called for solution id {} with status {}",solutionDtls.getNxSolutionId(), solutionDtls.getNxDesign().get(0).getStatus());
								    }
								}else {
									logger.info("ATX flow is not called for solution id {} with status {}",solutionDtls.getNxSolutionId(), solutionDtls.getNxDesign().get(0).getStatus());
								}

							} else {
								dataNotfound = true;
							}
						} else {
							dataNotfound = true;
						}
					} else {
						dataNotfound = true;
					}

					/*
					 * if(null!=solutionDtls &&
					 * CollectionUtils.isNotEmpty(solutionDtls.getNxDesign())) { NxPEDStatusDMaap
					 * pedDmaapReq=this.createRequestForDmaap(pedSolutionStatus);
					 * inputMap.put(TDDConstants.SOLUTION_ID, solutionDtls.getExternalKey());
					 * inputMap.put(TDDConstants.NX_SOLUTION_ID, solutionDtls.getNxSolutionId());
					 * inputMap.put(TDDConstants.ATT_ID, solutionDtls.getCreatedUser());
					 * if(CollectionUtils.isNotEmpty(pedSolutionStatus.getPortDetails())) {
					 * processASEData(pedSolutionStatus,solutionDtls,pedDmaapReq,inputMap,response);
					 * } if(CollectionUtils.isNotEmpty(pedSolutionStatus.getCircuitDetails())) {
					 * processADEData(pedSolutionStatus, solutionDtls,pedDmaapReq, inputMap,
					 * response); } tddRepositoryService.saveSolutionDetails(solutionDtls);
					 * saveTddRequestResponse(pedSolutionStatus.getSolutionId(), request, response);
					 * //calling automation flow CompletableFuture.runAsync(() ->
					 * this.callAutomationFlow(solutionDtls,inputMap)); }else {
					 * errorCd.add(MessageConstants.TDDR_DATA_NOT_FOUND);
					 * saveTddRequestResponse(pedSolutionStatus.getSolutionId(), request,null); }
					 */

					if (dataNotfound) {
						errorCd.add(MessageConstants.TDDR_DATA_NOT_FOUND);
						saveTddRequestResponse(pedSolutionStatus.getSolutionId(), request, null);
					}
				});
		logger.info("TDD response :{}", JacksonUtil.toString(response));
		this.processResponse(response);
		return response;
	}

	protected void processResponse(TransmitDesignDataResponse response) {
		List<String> messageLst = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(errorCd)) {
			messageLst.addAll(errorCd);
		} else {
			messageLst.add(MessageConstants.REQUEST_COMPLETED_SUCCESSFULLY);
		}
		setSuccessResponse(response, messageLst);
	}

	/**
	 * Creates the request for dmaap.
	 *
	 * @param solutionData the solution data
	 * @return the nx PED status D maa P
	 */
	protected NxPEDStatusDMaap createRequestForDmaap(SolutionStatus solutionData) {
		NxPEDStatusDMaap pedDmaap = new NxPEDStatusDMaap();
		pedDmaap.setOpportunityId(solutionData.getOpportunityId());
		pedDmaap.setSolutionId(solutionData.getSolutionId());
		return pedDmaap;
	}

	/**
	 * Creates the request for orch.
	 *
	 * @param solutionData the solution data
	 * @return the retreive ICBPSP request
	 */
	protected RetreiveICBPSPRequest createRequestForOrch(SolutionStatus pedSolutionStatus,
			NxSolutionDetail solutionDtls) {
		RetreiveICBPSPRequest dppOrchReq = new RetreiveICBPSPRequest();
		Solution solution = new Solution();
		solution.setPricerDSolutionId(Long.valueOf(pedSolutionStatus.getSolutionId()));
		solution.setExternalKey(solutionDtls.getExternalKey());
		solution.setOptyId(pedSolutionStatus.getOpportunityId());
		solution.setUserId(solutionDtls.getCreatedUser());
		solution.setErateInd("N");
		solution.setBulkInd("N");
		solution.setDesignType("");
		solution.setCancellationReason(pedSolutionStatus.getSolutionCancellationReason());
		dppOrchReq.setSolution(solution);
		return dppOrchReq;

	}

	/**
	 * Creates the action determinants.
	 *
	 * @param activityType the activity type
	 * @param component    the component
	 * @return the action determinants
	 */
	protected ActionDeterminants createActionDeterminants(String activityType, List<String> component) {
		ActionDeterminants actionDeterminants = new ActionDeterminants();
		actionDeterminants.setActivity(activityType);
		actionDeterminants.setComponent(component);
		return actionDeterminants;
	}

	/**
	 * Gets the solution detail by id.
	 *
	 * @param solutionId the solution id
	 * @return the solution detail by id
	 */
	protected NxSolutionDetail getSolutionDetailById(Long solutionId) {
		List<NxSolutionDetail> nxSolutionDetail = tddRepositoryService.findByExternalKey(solutionId);
		if (CollectionUtils.isNotEmpty(nxSolutionDetail) && null != nxSolutionDetail.get(0)) {
			return nxSolutionDetail.get(0);
		}
		return null;
	}

	/**
	 * Gets the design details by asr id.
	 *
	 * @param nxDesigns the nx designs
	 * @param asrItemId the asr item id
	 * @return the design details by asr id
	 */
	protected NxDesign getDesignDetailsByAsrId(List<NxDesign> nxDesigns, String asrItemId) {
		return Optional.ofNullable(nxDesigns).map(List::stream).orElse(Stream.empty())
				.filter(x -> x.getAsrItemId().equalsIgnoreCase(asrItemId)).findFirst().orElse(new NxDesign());
	}

	/**
	 * Process ASE data.
	 *
	 * @param portDetails the port details
	 * @param nxDesignLst the nx design
	 * @param dppOrchReq  the dpp orch req
	 * @return the transmit design data response
	 */
	protected void processASEData(SolutionStatus pedSolutionStatus, NxSolutionDetail dbSolutionDtls,
			NxPEDStatusDMaap pedDmaapReq, Map<String, Object> inputMap, TransmitDesignDataResponse response) {
		Map<String, Object> requestMetaDataMap = new HashMap<>();
		if (ServiceMetaData.getRequestMetaData() != null) {
			ServiceMetaData.getRequestMetaData().forEach((key, value) -> requestMetaDataMap.put(key, value));
		}
		List<NxDesign> nxDesignLst = dbSolutionDtls.getNxDesign();
		Boolean[] dataFound = new Boolean[1];
		Arrays.fill(dataFound, Boolean.FALSE);
		List<RequestDetails> requestDetails = new ArrayList<>();
		List<PortDetails> pedPortDetails = pedSolutionStatus.getPortDetails();
		Optional.ofNullable(pedPortDetails).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull)
				.forEach(portData -> {
					if (CollectionUtils.isNotEmpty(nxDesignLst)) {
						ListIterator<NxDesign> iterator = nxDesignLst.listIterator();
						while (iterator.hasNext()) {
							isDesignChange = false;
							NxDesign nxDesign = iterator.next();
							if (nxDesign != null) {
								if (nxDesign.getAsrItemId().equalsIgnoreCase(portData.getAsrItemId())) {
									Arrays.fill(dataFound, Boolean.TRUE);
									nxDesign.setAttuId(portData.getNssManagerATTUID());
									nxDesign.setFisrtName(portData.getNssManagerFirstName());
									nxDesign.setLastName(portData.getNssManagerLastName());
									if (StringUtils.isNotEmpty(portData.getStatusCode())) {
										nxDesign.setStatus(portData.getStatusCode());
									}
									nxDesign.setModifedDate(new Date());
									String offerId = this.getOfferIdASEAndASENoD(nxDesign);
									Map<Long, Map<String, NxUdfMapping>> udfMappingData = tddRepositoryService
											.getUdfDataMap(TDDConstants.TRANSMIT_DESIGN_DATA, Long.valueOf(offerId));
									inputMap.put(TDDConstants.UDF_MAPPING, udfMappingData);
									inputMap.put(TDDConstants.OFFER_ID, offerId);
									if (CollectionUtils.isNotEmpty(nxDesign.getNxDesignDetails())
											&& null != nxDesign.getNxDesignDetails().get(0)) {
										// for ASE one site have one Design Details only
										NxDesignDetails nxDesignDetails = nxDesign.getNxDesignDetails().get(0);
										nxDesignDetails.setModifedDate(new Date());
										Site site = JacksonUtil.fromStringForCodeHaus(nxDesignDetails.getDesignData(),
												Site.class);
										this.updateSiteObjASE(site, portData, inputMap);
										nxDesignDetails.setDesignData(JacksonUtil.toString(site));
										Long siteId = site.getSiteId();
										inputMap.put(TDDConstants.SITE_ID, siteId);

										JSONObject dppRequest = this.createDppRequestForASE(dbSolutionDtls,
												nxDesignDetails, inputMap);
										// calling DMAAP and DPP Orch
										inputMap.put(TDDConstants.SOLUTION_DATA, dbSolutionDtls);
										CompletableFuture.runAsync(() -> {
											try {
												ThreadMetaDataUtil.initThreadMetaData(requestMetaDataMap);
												this.callExternalApiForAse(dppRequest, pedDmaapReq, portData, inputMap,
														nxDesign);
											} catch (Exception e) {
												logger.info("Exception", e);
												throw e;
											} finally {
												ThreadMetaDataUtil.destroyThreadMetaData();
											}
										});
										updateDesignStatus(offerId, nxDesign, nxDesignDetails, portData,
												pedSolutionStatus);
									}
									this.saveDataInAuditTbl(nxDesign.getNxDesignId(), portData.getStatusCode(),
											CommonConstants.AUDIT_TRANSACTION_CONSTANTS.TDD_TRIGGER.getValue());
									RequestDetails resDetails = new RequestDetails();
									resDetails.setAsrItemId(portData.getAsrItemId());
									resDetails.setOffer(TDDConstants.OFFER_ID_BUNDLE_CD_MAPPING.get(offerId));
									requestDetails.add(resDetails);
								}
							}
						}
					}
				});
		if (!dataFound[0]) {
			errorCd.add(MessageConstants.TDDR_DATA_NOT_FOUND);
		}
		response.setRequestDetails(requestDetails);
	}

	protected String getOfferIdASEAndASENoD(NxDesign nxDesign) {
		if (StringUtils.isNotEmpty(nxDesign.getBundleCd())
				&& nxDesign.getBundleCd().equalsIgnoreCase(StringConstants.OFFERNAME_ASENOD)) {
			return TDDConstants.ASENOD_OFFER_ID;
		}
		return TDDConstants.ASE_OFFER_ID;
	}

	protected JSONObject createDppRequestForASE(NxSolutionDetail dbSolutionDtls, NxDesignDetails nxDesignDetails,
			Map<String, Object> inputMap) {
		if ("Y".equals(isDppTriggerASE)) {
			NxDesignAudit nxDesignAudit = nxDesignAuditRepository
					.findByNxRefIdAndTransaction(dbSolutionDtls.getNxSolutionId(), CommonConstants.SOLUTION_DATA);
			if (null != nxDesignAudit) {
				JSONObject baseRequest = JacksonUtil.toJsonObject(nxDesignAudit.getData());
				if (null != baseRequest) {
					JSONObject inputSite = JacksonUtil.toJsonObject(nxDesignDetails.getDesignData());
					if (inputSite.containsKey("nxSiteId")) {
						inputSite.remove("nxSiteId");
					}
					baseRequest = this.updateSite(baseRequest, inputSite);
				}
				return baseRequest;
			}
		}
		return null;
	}

	/**
	 * Gets the updated site obj.
	 *
	 * @param inputSite the input site
	 * @param portData  the port data
	 * @return the updated site obj
	 */
	@SuppressWarnings("unchecked")
	protected void updateSiteObjASE(Site inputSite, PortDetails portData, Map<String, Object> inputMap) {
		Map<Long, Map<String, NxUdfMapping>> udfMappingData = null != inputMap.get(TDDConstants.UDF_MAPPING)
				? (HashMap<Long, Map<String, NxUdfMapping>>) inputMap.get(TDDConstants.UDF_MAPPING)
				: new HashMap<>();
		if (portData.getEstimatedInterval() != null) {
			PedToUdfMapper mappingBean = this.convertPedToUdfFormat(udfMappingData, 30l, "estimatedInterval",
					String.valueOf(portData.getEstimatedInterval()));
			setDesignDataByUdfIdForASE(String.valueOf(mappingBean.getComponentId()), mappingBean.getComponentType(),
					String.valueOf(mappingBean.getUdfId()), mappingBean.getUdfAttributeVal(), mappingBean.getType(),
					inputSite.getDesignSiteOfferPort());
		}
		if (StringUtils.isNotEmpty(portData.getNotes())) {
			PedToUdfMapper mappingBean = this.convertPedToUdfFormat(udfMappingData, 30l, "notes",
					String.valueOf(portData.getNotes()));
			setDesignDataByUdfIdForASE(String.valueOf(mappingBean.getComponentId()), mappingBean.getComponentType(),
					String.valueOf(mappingBean.getUdfId()), mappingBean.getUdfAttributeVal(), mappingBean.getType(),
					inputSite.getDesignSiteOfferPort());
		}
		if (StringUtils.isNotEmpty(portData.getStatusCode())) {
			setDesignStatus(portData.getStatusCode(), inputSite.getDesignSiteOfferPort());
		}
	}

	/**
	 * Sets the design data by udf id.
	 *
	 * @param componentCodeId      the component code id
	 * @param udfId                the udf id
	 * @param udfAttribute         the udf attribute
	 * @param setType              the set type
	 * @param designSiteOfferPorts the design site offer ports
	 */
	protected void setDesignDataByUdfIdForASE(String componentCodeId, String componentCodeType, String udfId,
			String udfAttribute, String setType, List<Port> designSiteOfferPorts) {

		if (CollectionUtils.isNotEmpty(designSiteOfferPorts) && StringUtils.isNotEmpty(udfAttribute)
				&& !udfId.equals("null")) {
			ListIterator<Port> iterator = designSiteOfferPorts.listIterator();
			while (iterator.hasNext()) {
				Port port = iterator.next();
				String path = new JsonPathExpressionBuilder().arraynode(CommonConstants.COMPONENT).select()
						.where(CommonConstants.COMPONENT_CODE_ID).is(componentCodeId)
						.arraynode(CommonConstants.DESIGN_DETAILS).select().where(CommonConstants.UDF_ID).is(udfId)
						.build();
				TypeRef<List<UDFBaseData>> typeRef = new TypeRef<List<UDFBaseData>>() {
				};
				List<UDFBaseData> designDetailList = jsonPathUtil.search(port, path, typeRef);
				String json = "";
				if (CollectionUtils.isNotEmpty(designDetailList)) {
					json = updateDesignByUdfId(udfId, udfAttribute, setType, port, path, designDetailList, json);
				} else {
					isDesignChange = true;
					json = addDesignByUdfId(componentCodeId, udfId, udfAttribute, setType, port, json);
				}
				this.setDesignChangeStatus(designDetailList, setType, udfAttribute);
				if (StringUtils.isNotEmpty(json)) {
					try {
						port = mapper.readValue(json, Port.class);
					} catch (IOException e) {
						logger.error("Exception from setDesignDataByUdfIdForASE>>", e);
					}
				}
				iterator.set(port);
			}
		}
	}

	/**
	 * Call external api for ase.
	 *
	 * @param dppOrchReq  the dpp orch req
	 * @param pedDmaapReq the ped dmaap req
	 * @param offerId     the offer id
	 * @param portData    the port data
	 */
	protected void callExternalApiForAse(JSONObject dppOrchReq, NxPEDStatusDMaap pedDmaapReq, PortDetails portData,
			Map<String, Object> inputMap, NxDesign nxDesign) {
		String offerId = (String) inputMap.get(TDDConstants.OFFER_ID);
		if (StringUtils.isNotEmpty(offerId) && offerId.equals(TDDConstants.ASE_OFFER_ID)) {
			if (isDesignChange) {

				if (null != dppOrchReq) {
					this.callOrchCustomPricingOrderFlow(dppOrchReq,
							TDDConstants.OFFER_ID_BUNDLE_CD_MAPPING.get(offerId), nxDesign);
				}
			}

			// this.callAutomationFlow(inputMap, portData.getStatusCode());

			// sending mail
			this.callMailService(inputMap, nxDesign);
		}

		// publishing dmaap
		this.createPedDmaapDesignForASE(pedDmaapReq, portData, inputMap);
		this.callMessageRouter(inputMap, pedDmaapReq, nxDesign);
	}

	/**
	 * Creates the ped dmaap design for ase.
	 *
	 * @param pedDmaapReq the ped dmaap req
	 * @param portData    the port data
	 * @param inputMap    the input map
	 */
	protected void createPedDmaapDesignForASE(NxPEDStatusDMaap pedDmaapReq, PortDetails portData,
			Map<String, Object> inputMap) {
		String offerId = inputMap.get(TDDConstants.OFFER_ID) != null ? (String) inputMap.get(TDDConstants.OFFER_ID)
				: "";
		Long siteId = inputMap.get(TDDConstants.SITE_ID) != null ? (Long) inputMap.get(TDDConstants.SITE_ID) : 0l;
		Long nxSolutionId = inputMap.get(TDDConstants.NX_SOLUTION_ID) != null
				? (Long) inputMap.get(TDDConstants.NX_SOLUTION_ID)
				: 0l;
		List<NxMpDeal> dealData = nxMpDealRepository.findBySolutionId(nxSolutionId);
		if (CollectionUtils.isNotEmpty(dealData) && null != dealData.get(0)) {
			pedDmaapReq.setDealId(dealData.get(0).getDealID());
			pedDmaapReq.setVersionNumber(dealData.get(0).getVersion());
		}
		pedDmaapReq.setOfferId(offerId);
		pedDmaapReq.setEventType(TDDConstants.CIRCUIT);

		if (StringUtils.isNotEmpty(portData.getFailureInd()) && "Y".equalsIgnoreCase(portData.getFailureInd())) {
			NexxusTDDMessage message = new NexxusTDDMessage();
			pedDmaapReq.setDesignStatus(TDDConstants.TDD_STATUS_FAILED);
			pedDmaapReq.setMessage(portData.getValidationIssues());
			List<DesignDetailsTDD> designDetails = new ArrayList<DesignDetailsTDD>();
			DesignDetailsTDD designDetail = new DesignDetailsTDD();
			designDetail.setAsrItemId(portData.getAsrItemId());
			designDetail.setId(String.valueOf(siteId));
			designDetail.setMessage(portData.getMessage());
			designDetail.setResult(portData.getResult());
			designDetail.setValidationIssues(portData.getValidationIssues());
			designDetails.add(designDetail);
			message.setDesignDetails(designDetails);
			pedDmaapReq.setMessage(message);
			pedDmaapReq.setAsrItemId(new ArrayList<>(Arrays.asList(portData.getAsrItemId())));
		} else {
			pedDmaapReq.setSuccessAsrItemId(new ArrayList<>(Arrays.asList(portData.getAsrItemId())));
			pedDmaapReq.setDesignStatus(portData.getStatusCode());
		}

		pedDmaapReq.setEstimateInterval(portData.getEstimatedInterval());
		pedDmaapReq.setPreliminaryServingPlanURL(portData.getPreliminaryServingPlanURL());
		pedDmaapReq.setNotes(portData.getNotes());
		pedDmaapReq.setNssManagerATTUID(portData.getNssManagerATTUID());
		pedDmaapReq.setNssManagerFirstName(portData.getNssManagerFirstName());
		pedDmaapReq.setNssManagerLastName(portData.getNssManagerLastName());
		EndPointDetails endPoint = new EndPointDetails();
		endPoint.setSiteId(siteId);
		endPoint.setEndpointType(TDDConstants.ENDPOINT_A);
		pedDmaapReq.setEndPointDetails(Arrays.asList(endPoint));
			
		if ("D".equalsIgnoreCase(portData.getStatusCode())) {
			
			if (portData.getKmzMapLink()!= null) {
				pedDmaapReq.setKmzMapLink(portData.getKmzMapLink());
			}
				
		}
		
	}

	/**
	 * This function is used to update the design status in nxDesignDetails for ASE
	 * 
	 * @param offerId
	 * @param nxDesign
	 * @param nxDesignDetails
	 * @param portData
	 * @param pedSolutionStatus
	 */
	public void updateDesignStatus(String offerId, NxDesign nxDesign, NxDesignDetails nxDesignDetails,
			PortDetails portData, SolutionStatus pedSolutionStatus) {
		Site site = JacksonUtil.fromStringForCodeHaus(nxDesignDetails.getDesignData(), Site.class);
		SolutionDetails solutionDetails = new SolutionDetails();
		solutionDetails.setSiteId(site.getSiteId());
		solutionDetails.setDesignId(String.valueOf(nxDesignDetails.getNxDesignId()));
		solutionDetails.setAsrItemId(nxDesign.getAsrItemId());
		solutionDetails.setStatusCode(nxDesign.getStatus());
		solutionDetails.setNotes(portData.getNotes());
		// solutionDetails.setCancellationReason(portDetails.getCircuitCancellationReason());
		// solutionDetails.setConfirmedInterval(portDetails.getConfirmedInterval());
		solutionDetails.setEstimatedInterval(portData.getEstimatedInterval());
		// solutionDetails.setPreliminaryServingPlanURL(portDetails.getPreliminaryServingPlanURL());
		solutionDetails.setNssManagerFirstName(portData.getNssManagerFirstName());
		solutionDetails.setNssManagerLastName(portData.getNssManagerLastName());
		solutionDetails.setNssManagerATTUID(portData.getNssManagerATTUID());
		List<com.att.sales.nexxus.nxPEDstatus.model.EndPointDetails> endPointDetailsList = new ArrayList<>();
		com.att.sales.nexxus.nxPEDstatus.model.EndPointDetails endPointDetails = new com.att.sales.nexxus.nxPEDstatus.model.EndPointDetails();
		endPointDetails.setEndpointType("A");
		endPointDetailsList.add(endPointDetails);
		solutionDetails.setEndPointDetails(endPointDetailsList);
		nxDesignDetails.setDesignStatus(solutionDetails);
	}

	/**
	 * This function is used to update the design status in nxDesignDetails for ADE
	 * 
	 * @param offerId
	 * @param nxDesign
	 * @param nxDesignDetails
	 * @param circuitDetails
	 * @param pedSolutionStatus
	 */
	public void updateDesignStatus(String offerId, NxDesign nxDesign, NxDesignDetails nxDesignDetails,
			CircuitDetails circuitDetails, SolutionStatus pedSolutionStatus) {
		Site site = JacksonUtil.fromStringForCodeHaus(nxDesignDetails.getDesignData(), Site.class);
		SolutionDetails solutionDetails = new SolutionDetails();
		solutionDetails.setSiteId(site.getSiteId());
		solutionDetails.setDesignId(String.valueOf(nxDesignDetails.getNxDesignId()));
		solutionDetails.setAsrItemId(nxDesign.getAsrItemId());
		solutionDetails.setStatusCode(nxDesign.getStatus());
		if (null != circuitDetails) {
			solutionDetails.setNotes(circuitDetails.getNotes());
			solutionDetails.setCancellationReason(circuitDetails.getCircuitCancellationReason());
			solutionDetails.setConfirmedInterval(circuitDetails.getConfirmedInterval());
			solutionDetails.setEstimatedInterval(circuitDetails.getEstimatedInterval());
			solutionDetails.setPreliminaryServingPlanURL(circuitDetails.getPreliminaryServingPlanURL());
			solutionDetails.setNssManagerFirstName(circuitDetails.getNssManagerFirstName());
			solutionDetails.setNssManagerLastName(circuitDetails.getNssManagerLastName());
			solutionDetails.setNssManagerATTUID(circuitDetails.getNssManagerATTUID());
			solutionDetails.setKmzMapLink(circuitDetails.getKmzMapLink());
			if (CollectionUtils.isNotEmpty(circuitDetails.getEndpointDetails())) {
				List<com.att.sales.nexxus.nxPEDstatus.model.EndPointDetails> endPointDetailsList = new ArrayList<>();
				circuitDetails.getEndpointDetails().parallelStream().forEach(circuitDetail -> {
					com.att.sales.nexxus.nxPEDstatus.model.EndPointDetails endPointDetails = new com.att.sales.nexxus.nxPEDstatus.model.EndPointDetails();
					endPointDetails.setEndpointType(circuitDetail.getEndpointType());
					endPointDetails.setEdgelessDesignIndicator(circuitDetail.getEdgelessDesignIndicator());
					endPointDetails.setLocationclli(circuitDetail.getLocationCLLI());
					endPointDetails.setAlternateSWCCLLI(circuitDetail.getAlternateSWCCLLI());
					endPointDetails.setCommonLanguageFacilityId(circuitDetail.getCommonLanguageFacilityId());
					endPointDetailsList.add(endPointDetails);
				});
				solutionDetails.setEndPointDetails(endPointDetailsList);
			}
		}
		nxDesignDetails.setDesignStatus(solutionDetails);
	}

	/**
	 * Process ADE data.
	 * 
	 * @param pedSolutionStatus the ped solution status
	 * @param nxDesignLst       the nx design lst
	 * @param dppOrchReq        the dpp orch req
	 * @param pedDmaapReq       the ped dmaap req
	 * @param inputMap          the input map
	 * @param response          the response
	 */
	protected void processADEData(SolutionStatus pedSolutionStatus, NxSolutionDetail dbSolutionDtls,
			NxPEDStatusDMaap pedDmaapReq, Map<String, Object> inputMap, TransmitDesignDataResponse response) {
		Map<String, Object> requestMetaDataMap = new HashMap<>();
		if (ServiceMetaData.getRequestMetaData() != null) {
			ServiceMetaData.getRequestMetaData().forEach((key, value) -> requestMetaDataMap.put(key, value));
		}
		List<NxDesign> nxDesignLst = dbSolutionDtls.getNxDesign();
		this.processStatusCdForADE(pedSolutionStatus, inputMap);
		Boolean[] dataFound = new Boolean[1];
		Arrays.fill(dataFound, Boolean.FALSE);
		String offerId = TDDConstants.ADE_OFFER_ID;

		Map<Long, Map<String, NxUdfMapping>> udfMappingData = tddRepositoryService
				.getUdfDataMap(TDDConstants.TRANSMIT_DESIGN_DATA, Long.valueOf(offerId));
		inputMap.put(TDDConstants.UDF_MAPPING, udfMappingData);
		inputMap.put(TDDConstants.OFFER_ID, offerId);
		inputMap.put(TDDConstants.RESPONSE_TYPE, pedSolutionStatus.getResponseType());
		inputMap.put(TDDConstants.TDD_STATUS, pedSolutionStatus.getStatusCode());
		List<RequestDetails> requestDetails = new ArrayList<>();
		String circuitStatus = (String) inputMap.get(TDDConstants.CIRCUIT_LEVEL_STATUS);
		List<CircuitDetails> circuitDetails = pedSolutionStatus.getCircuitDetails();
		Optional.ofNullable(circuitDetails).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull)
				.forEach(pedCircuitDetails -> {
					isDesignChange = false;
					this.processCancellationReason(pedSolutionStatus, pedCircuitDetails, inputMap);
					inputMap.put(TDDConstants.TDD_KMZ_MAP_LINK, pedCircuitDetails.getKmzMapLink());
					if (CollectionUtils.isNotEmpty(nxDesignLst)) {
						ListIterator<NxDesign> iterator = nxDesignLst.listIterator();
						while (iterator.hasNext()) {
							NxDesign nxDesign = iterator.next();
							if (nxDesign != null &&nxDesign.getBundleCd().equalsIgnoreCase("ADE")) {
								if (nxDesign.getAsrItemId().equalsIgnoreCase(pedCircuitDetails.getAsrItemId())) {
									Arrays.fill(dataFound, Boolean.TRUE);
									nxDesign.setAttuId(pedCircuitDetails.getNssManagerATTUID());
									nxDesign.setFisrtName(pedCircuitDetails.getNssManagerFirstName());
									nxDesign.setLastName(pedCircuitDetails.getNssManagerLastName());
									nxDesign.setStatus(circuitStatus);
									nxDesign.setModifedDate(new Date());
									if (CollectionUtils.isNotEmpty(nxDesign.getNxDesignDetails())
											&& null != nxDesign.getNxDesignDetails().get(0)) {
										NxDesignDetails nxDesignDetails = nxDesign.getNxDesignDetails().get(0);
										nxDesignDetails.setModifedDate(new Date());
										JSONObject circuitData = JacksonUtil
												.toJsonObject(nxDesignDetails.getDesignData());
										updateCircuitObjADE(circuitData, pedCircuitDetails, inputMap);
										nxDesignDetails.setDesignData(circuitData.toJSONString());
										Long endPointASiteId = getSiteIdFromEndPoint(circuitData,
												TDDConstants.ENDPOINT_UDF_MAPPING.get(TDDConstants.ENDPOINT_A), "1220");
										Long endPointZSiteId = getSiteIdFromEndPoint(circuitData,
												TDDConstants.ENDPOINT_UDF_MAPPING.get(TDDConstants.ENDPOINT_Z), "1220");
										inputMap.put(TDDConstants.ASR_ITEM_ID, nxDesign.getAsrItemId());
										inputMap.put(TDDConstants.A_SITE_ID, endPointASiteId);
										inputMap.put(TDDConstants.Z_SITE_ID, endPointZSiteId);
										JSONObject dppRequest = this.createDppRequestForADE(dbSolutionDtls,
												nxDesignDetails, inputMap);
										// calling DMAAP and DPP Orch
										inputMap.put(TDDConstants.SOLUTION_DATA, dbSolutionDtls);
										CompletableFuture.runAsync(() -> {
											try {
												ThreadMetaDataUtil.initThreadMetaData(requestMetaDataMap);
												this.callExternalApiForADE(dppRequest, pedDmaapReq, pedCircuitDetails,
														inputMap, nxDesign);
											} catch (Exception e) {
												logger.info("Exception", e);
												throw e;
											} finally {
												ThreadMetaDataUtil.destroyThreadMetaData();
											}
										});
										logger.info(" after publishing circuit level dmaap");
										inputMap.put(TDDConstants.circuitDetails, pedCircuitDetails);
										inputMap.put(TDDConstants.nxDesign, nxDesign);
										// solutionLevelDMaap(pedDmaapReq,pedCircuitDetails,inputMap,nxDesign,pedSolutionStatus);
										updateDesignStatus(offerId, nxDesign, nxDesignDetails, pedCircuitDetails,
												pedSolutionStatus);
									}
									this.saveDataInAuditTbl(nxDesign.getNxDesignId(), circuitStatus,
											CommonConstants.AUDIT_TRANSACTION_CONSTANTS.TDD_TRIGGER.getValue());
									RequestDetails resDetails = new RequestDetails();
									resDetails.setAsrItemId(pedCircuitDetails.getAsrItemId());
									resDetails.setOffer(TDDConstants.OFFER_ID_BUNDLE_CD_MAPPING.get(offerId));
									requestDetails.add(resDetails);
//								nxDesignRepository.updateDesingStatus(pedCircuitDetails.getAsrItemId(),pedSolutionStatus.getStatusCode(),pedSolutionStatus.getSolutionId());
								}
							}
						}
					}
				});

		if (!dataFound[0]) {
			errorCd.add(MessageConstants.TDDR_DATA_NOT_FOUND);
		}

		response.setRequestDetails(requestDetails);
	}

	private void solutionLevelDMaap(NxPEDStatusDMaap pedDmaapReq, CircuitDetails pedCircuitDetails,
			Map<String, Object> inputMap, NxDesign nxDesign, SolutionStatus pedSolutionStatus) {
		logger.info("inside solution level dmaap");
		// publishing dmaap
		String status = (String) inputMap.get(TDDConstants.TDD_STATUS);
		String ipeInd = (String) inputMap.get(TDDConstants.IPE_INDICATOR);
		String offerId = inputMap.get(TDDConstants.OFFER_ID) != null ? (String) inputMap.get(TDDConstants.OFFER_ID): "";	
		String flowType = inputMap.get(TDDConstants.FLOW_TYPE) != null ? (String) inputMap.get(TDDConstants.FLOW_TYPE) : "";
		
		if ("120".equals(offerId)|| "201".equals(offerId)) { 
			if ((TDDConstants.TDD_STATUS_COMPLETED.equalsIgnoreCase(status)
					&& StringConstants.CONSTANT_Y.equalsIgnoreCase(ipeInd))
					|| (TDDConstants.TDD_STATUS_SOLD.equalsIgnoreCase(status)
							&& StringConstants.CONSTANT_Y.equalsIgnoreCase(ipeInd))
					|| (TDDConstants.TDD_STATUS_CANCEL.equalsIgnoreCase(status)
							&& StringConstants.CONSTANT_Y.equalsIgnoreCase(ipeInd))) {
				NxSolutionDetail solutionDtls = getSolutionDetailById(Long.valueOf(pedSolutionStatus.getSolutionId()));

				Long solutionId = solutionDtls.getNxSolutionId();
				int numberOfASRForSolution = nxDesignRepository.numberOfAsrForSolution(solutionId);
				List<String> completedAsrItems = nxDesignRepository.fetchAsrCompletedForSolution(solutionId, status);
//				logger.info("number Of ASR For Solution : " + numberOfASRForSolution);
//				logger.info("total ASR Completed For Solution : " + completedAsrItems.size());

				if (numberOfASRForSolution == completedAsrItems.size()) {

					pedDmaapReq.setOfferId(offerId);
					pedDmaapReq.setSuccessAsrItemId(completedAsrItems);
					pedDmaapReq.setEventType(null);
					pedDmaapReq.setNxSolutionId(solutionId + "");
					pedDmaapReq.setDesignStatus(null);
					pedDmaapReq.setNxStatus(status);
					pedDmaapReq.setIpeIndicator(ipeInd);
					pedDmaapReq.setEstimateInterval(null);
					pedDmaapReq.setEndPointDetails(null);
					pedDmaapReq.setNssManagerFirstName(null);
					pedDmaapReq.setNssManagerLastName(null);
					pedDmaapReq.setNssManagerATTUID(null);
					
					if (StringConstants.SALES_IPNE.equalsIgnoreCase(flowType)) {
						pedDmaapReq.setKmzMapLink(null);
						}
					
					
					// publishing the DMaap
					this.callMessageRouter(inputMap, pedDmaapReq, nxDesign);

					// sending mail
					this.callMailService(inputMap, nxDesign);

				}

			}
		}
	}

	protected JSONObject createDppRequestForADE(NxSolutionDetail dbSolutionDtls, NxDesignDetails nxDesignDetails,
			Map<String, Object> inputMap) {
		if ("Y".equals(isDppTriggerADE)) {
			NxDesignAudit nxDesignAudit = nxDesignAuditRepository
					.findByNxRefIdAndTransaction(dbSolutionDtls.getNxSolutionId(), CommonConstants.SOLUTION_DATA);
			if (null != nxDesignAudit) {
				JSONObject baseRequest = JacksonUtil.toJsonObject(nxDesignAudit.getData());
				if (null != baseRequest) {
					baseRequest = this.updateSolutionStatus(baseRequest,
							(String) inputMap.get(TDDConstants.SOLUTION_LEVEL_STATUS));
					baseRequest = this.updateSolutionCancellationReason(baseRequest,
							(String) inputMap.get(TDDConstants.SOLUTION_LEVEL_CANCEL_REASON));
					JSONObject inputCircuitData = JacksonUtil.toJsonObject(nxDesignDetails.getDesignData());
					JSONObject siteObject = this.createSiteForADE(inputMap, inputCircuitData);
					baseRequest = this.updateSite(baseRequest, siteObject);
				}
				return baseRequest;
			}
		}
		return null;

	}

	@SuppressWarnings("unchecked")
	protected JSONObject createSiteForADE(Map<String, Object> inputMap, JSONObject reqCircuitData) {
		Long endPointASiteId = (Long) inputMap.get(TDDConstants.A_SITE_ID);
		Long endPointZSiteId = (Long) inputMap.get(TDDConstants.Z_SITE_ID);
		String asrItemId = (String) inputMap.get(TDDConstants.ASR_ITEM_ID);
		// JSONObject endPointAsite=getEndPointSiteBlock(reqCircuitData,
		// endPointASiteId);
		// JSONObject endPointZsite=getEndPointSiteBlock(reqCircuitData,
		// endPointZSiteId);
		JSONObject reqSite = new JSONObject();
		reqSite.put("pricerDSiteId", endPointZSiteId);
		reqSite.put("asrItemId", asrItemId);
		reqSite.put("designStatus", (String) inputMap.get(TDDConstants.CIRCUIT_LEVEL_STATUS));
		reqSite.put("cancellationReason", (String) inputMap.get(TDDConstants.CIRCUIT_LEVEL_CANCEL_REASON));
		JSONArray popSiteDetails = new JSONArray();
		JSONObject popSiteDetail = new JSONObject();
		popSiteDetail.put("pricerDSiteId", endPointASiteId);
		popSiteDetails.add(popSiteDetail);
		reqSite.put("popSiteDetails", popSiteDetails);
		reqSite.put("circuitDesign", reqCircuitData.get("component"));
		return reqSite;
	}

	@SuppressWarnings("unchecked")
	protected JSONObject getEndPointSiteBlock(JSONObject reqCircuitData, Long siteId) {
		String path = "$..site.[?(@.siteId== " + siteId + ")]";
		TypeRef<List<Object>> typeRef = new TypeRef<List<Object>>() {
		};
		List<Object> siteObj = jsonPathUtil.search(reqCircuitData, path, typeRef);
		if (CollectionUtils.isNotEmpty(siteObj) && siteObj.get(0) != null) {
			LinkedHashMap<Object, Object> dd = (LinkedHashMap<Object, Object>) siteObj.get(0);
			return new JSONObject(dd);
		}
		return null;
	}

	/**
	 * Gets the site id from end point.
	 *
	 * @param circuit         the circuit
	 * @param endPointUdfId   the end point udf id
	 * @param componentCodeId the component code id
	 * @return the site id from end point
	 */
	protected Long getSiteIdFromEndPoint(JSONObject circuit, String endPointUdfId, String componentCodeId) {
		String path = "$..[?(@.componentCodeId ==" + componentCodeId + " &&" + " " + endPointUdfId
				+ " in @.designDetails[*].udfId)].references[?(@.referenceType == 'Site')].referenceId";
		JSONArray componentList = (JSONArray) circuit.get("component");
		Object data = nexxusJsonUtility.getValue(componentList, path);
		if (data != null) {
			return Long.valueOf(data.toString());
		}
		return null;
	}

	/**
	 * Process status cd for ADE.
	 *
	 * @param pedSolutionStatus the ped solution status
	 * @param inputMap          the input map
	 */
	protected void processStatusCdForADE(SolutionStatus pedSolutionStatus, Map<String, Object> inputMap) {
		inputMap.put(TDDConstants.CIRCUIT_LEVEL_STATUS, "");
		inputMap.put(TDDConstants.SOLUTION_LEVEL_STATUS, "");
		if (StringUtils.isNotEmpty(pedSolutionStatus.getResponseType())) {
			if (pedSolutionStatus.getResponseType().equals("Circuit")) {
				inputMap.put(TDDConstants.CIRCUIT_LEVEL_STATUS, pedSolutionStatus.getStatusCode());
			} else {
				inputMap.put(TDDConstants.SOLUTION_LEVEL_STATUS, pedSolutionStatus.getStatusCode());
			}
		}
	}

	/**
	 * Process cancellation reason.
	 *
	 * @param pedSolutionStatus the ped solution status
	 * @param circuitDetails    the circuit details
	 * @param inputMap          the input map
	 */
	protected void processCancellationReason(SolutionStatus pedSolutionStatus, CircuitDetails circuitDetails,
			Map<String, Object> inputMap) {
		inputMap.put(TDDConstants.SOLUTION_LEVEL_CANCEL_REASON, "");
		inputMap.put(TDDConstants.CIRCUIT_LEVEL_CANCEL_REASON, "");
		if ((pedSolutionStatus.getStatusCode().equalsIgnoreCase("CL")
				|| pedSolutionStatus.getStatusCode().equalsIgnoreCase("SC"))
				&& pedSolutionStatus.getResponseType().equalsIgnoreCase("Solution")) {
			inputMap.put(TDDConstants.SOLUTION_LEVEL_CANCEL_REASON, pedSolutionStatus.getSolutionCancellationReason());
		} else if (pedSolutionStatus.getResponseType().equalsIgnoreCase("Circuit")) {
			inputMap.put(TDDConstants.CIRCUIT_LEVEL_CANCEL_REASON, circuitDetails.getCircuitCancellationReason());
		}
	}

	/**
	 * Update circuit obj ADE.
	 *
	 * @param circuit           the circuit
	 * @param pedCircuitDetails the ped circuit details
	 * @param inputMap          the input map
	 */
	@SuppressWarnings("unchecked")
	protected void updateCircuitObjADE(JSONObject circuit, CircuitDetails pedCircuitDetails,
			Map<String, Object> inputMap) {
		Map<Long, Map<String, NxUdfMapping>> udfMappingData = null != inputMap.get(TDDConstants.UDF_MAPPING)
				? (HashMap<Long, Map<String, NxUdfMapping>>) inputMap.get(TDDConstants.UDF_MAPPING)
				: new HashMap<>();
		JSONArray componentLst = (JSONArray) circuit.get("component");
		JSONArray siteLst = (JSONArray) circuit.get("site");
		List<Component> component = JacksonUtil.toList(componentLst.toJSONString(),
				new TypeReference<List<Component>>() {
				});
		if (pedCircuitDetails.getEstimatedInterval() != null) {
			PedToUdfMapper mappingBean = this.convertPedToUdfFormat(udfMappingData, 1210l, "estimatedInterval",
					String.valueOf(pedCircuitDetails.getEstimatedInterval()));
			setDesignDataByUdfIdForADE(String.valueOf(mappingBean.getComponentId()), mappingBean.getComponentType(),
					String.valueOf(mappingBean.getUdfId()), mappingBean.getUdfAttributeVal(), mappingBean.getType(),
					component, null);
		}
		if (pedCircuitDetails.getConfirmedInterval() != null) {
			PedToUdfMapper mappingBean = this.convertPedToUdfFormat(udfMappingData, 1210l, "confrimedInterval",
					String.valueOf(pedCircuitDetails.getConfirmedInterval()));
			setDesignDataByUdfIdForADE(String.valueOf(mappingBean.getComponentId()), mappingBean.getComponentType(),
					String.valueOf(mappingBean.getUdfId()), mappingBean.getUdfAttributeVal(), mappingBean.getType(),
					component, null);
		}
		if (StringUtils.isNotEmpty(pedCircuitDetails.getNotes())) {
			PedToUdfMapper mappingBean = this.convertPedToUdfFormat(udfMappingData, 1210l, "notes",
					String.valueOf(pedCircuitDetails.getNotes()));
			setDesignDataByUdfIdForADE(String.valueOf(mappingBean.getComponentId()), mappingBean.getComponentType(),
					String.valueOf(mappingBean.getUdfId()), mappingBean.getUdfAttributeVal(), mappingBean.getType(),
					component, null);
		}

		String statusCd = null;
		if (null != inputMap.get(TDDConstants.CIRCUIT_LEVEL_STATUS)) {
			statusCd = (String) inputMap.get(TDDConstants.CIRCUIT_LEVEL_STATUS);
		} else if (null != inputMap.get(TDDConstants.SOLUTION_LEVEL_STATUS)) {
			statusCd = (String) inputMap.get(TDDConstants.SOLUTION_LEVEL_STATUS);
		}
		if (statusCd != null && !statusCd.isEmpty() && "D".equals(statusCd)) {
			PedToUdfMapper mappingBean = this.convertPedToUdfFormat(udfMappingData, 1210l, "kmzMapLink",
					pedCircuitDetails.getKmzMapLink());
			setDesignDataByUdfIdForADE(String.valueOf(mappingBean.getComponentId()), mappingBean.getComponentType(),
					String.valueOf(mappingBean.getUdfId()), mappingBean.getUdfAttributeVal(), mappingBean.getType(),
					component, null);
		}

		List<EndpointDetails> endpointDetails = pedCircuitDetails.getEndpointDetails();
		Optional.ofNullable(endpointDetails).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull)
				.forEach(endpointData -> {
					String endpointUdfId = TDDConstants.ENDPOINT_UDF_MAPPING.get(endpointData.getEndpointType());
					Long siteId = getSiteIdFromEndPoint(circuit, endpointUdfId, "1220");
					if (StringUtils.isNotEmpty(endpointData.getLocationCLLI())) {
						PedToUdfMapper mappingBean = this.convertPedToUdfFormat(udfMappingData, 1220l, "locationCLLI",
								String.valueOf(endpointData.getLocationCLLI()));
						setDesignDataByUdfIdForADE(String.valueOf(mappingBean.getComponentId()),
								mappingBean.getComponentType(), String.valueOf(mappingBean.getUdfId()),
								mappingBean.getUdfAttributeVal(), mappingBean.getType(), component, endpointUdfId);
						this.updateSiteObjForADE(siteId, siteLst, "customerLocationClli",
								String.valueOf(endpointData.getLocationCLLI()));
					}
					if (StringUtils.isNotEmpty(endpointData.getAlternateSWCCLLI())) {
						PedToUdfMapper mappingBean = this.convertPedToUdfFormat(udfMappingData, 1220l,
								"alternateSwcCLLI", String.valueOf(endpointData.getAlternateSWCCLLI()));
						setDesignDataByUdfIdForADE(String.valueOf(mappingBean.getComponentId()),
								mappingBean.getComponentType(), String.valueOf(mappingBean.getUdfId()),
								mappingBean.getUdfAttributeVal(), mappingBean.getType(), component, endpointUdfId);
						this.updateSiteObjForADE(siteId, siteLst, "alternateSwcCLLI",
								String.valueOf(endpointData.getAlternateSWCCLLI()));
					}
					if (StringUtils.isNotEmpty(endpointData.getEdgelessDesignIndicator())) {
						PedToUdfMapper mappingBean = this.convertPedToUdfFormat(udfMappingData, 1220l,
								"edgelessDesignIndicator", String.valueOf(endpointData.getEdgelessDesignIndicator()));
						setDesignDataByUdfIdForADE(String.valueOf(mappingBean.getComponentId()),
								mappingBean.getComponentType(), String.valueOf(mappingBean.getUdfId()),
								mappingBean.getUdfAttributeVal(), mappingBean.getType(), component, endpointUdfId);
					}
				});
		circuit.replace("component", JacksonUtil.convertObjectToJsonArray(component));
		circuit.replace("site", siteLst);
	}

	@SuppressWarnings("unchecked")
	protected void updateSiteObjForADE(Long siteId, JSONArray siteLst, String field, String value) {

		Iterator<JSONObject> it = siteLst.iterator();
		while (it.hasNext()) {
			JSONObject site = it.next();
			if (site.get("siteId").equals(siteId)) {
				site.get(field);
				site.replace(field, value);
			}
		}

	}

	/**
	 * Convert ped to udf format.
	 *
	 * @param udfMappingData  the udf mapping data
	 * @param componentCodeId the component code id
	 * @param fieldName       the field name
	 * @param fieldValue      the field value
	 * @return the ped to udf mapper
	 */
	protected PedToUdfMapper convertPedToUdfFormat(Map<Long, Map<String, NxUdfMapping>> udfMappingData,
			Long componentCodeId, String fieldName, String fieldValue) {
		PedToUdfMapper mappingBean = new PedToUdfMapper();
		if (udfMappingData.containsKey(componentCodeId) && MapUtils.isNotEmpty(udfMappingData.get(componentCodeId))) {
			Map<String, NxUdfMapping> dataMap = udfMappingData.get(componentCodeId);
			if (dataMap.containsKey(fieldName) && null != dataMap.get(fieldName)) {
				NxUdfMapping udfData = dataMap.get(fieldName);
				mappingBean.setComponentId(udfData.getComponentId());
				mappingBean.setOfferId(udfData.getOfferId());
				mappingBean.setUdfId(udfData.getUdfId());
				mappingBean.setComponentType(udfData.getComponentType());
				if (StringUtils.isNotEmpty(udfData.getUdfAttributeDatasetName())) {
					if (udfData.getUdfAttributeDatasetName().equalsIgnoreCase(TDDConstants.TEXT)) {
						mappingBean.setType(TDDConstants.TEXT);
						mappingBean.setUdfAttributeVal(fieldValue);
					} else {
						mappingBean.setType(TDDConstants.ID);
						NxLookupData lookupdata = tddRepositoryService
								.findTopByDatasetNameAndDescription(udfData.getUdfAttributeDatasetName(), fieldValue);
						if (null != lookupdata) {
							mappingBean.setUdfAttributeVal(lookupdata.getItemId());
						} else {
							Long udfAttrData = tddRepositoryService.getUdfAttrIdFromSalesTbl(udfData.getOfferId(),
									udfData.getComponentId(), udfData.getUdfId(), fieldValue);
							if (null != udfAttrData) {
								mappingBean.setUdfAttributeVal(String.valueOf(udfAttrData));
							}

						}
					}
				}
			}
		}
		return mappingBean;
	}

	/**
	 * Sets the design data by udf id for ADE.
	 *
	 * @param componentCodeId     the component code id
	 * @param componentCodeType   the component code type
	 * @param udfId               the udf id
	 * @param udfAttribute        the udf attribute
	 * @param setType             the set type
	 * @param components          the components
	 * @param endPointfilterUdfId the end pointfilter udf id
	 */
	protected void setDesignDataByUdfIdForADE(String componentCodeId, String componentCodeType, String udfId,
			String udfAttribute, String setType, List<Component> components, String endPointfilterUdfId) {
		if (CollectionUtils.isNotEmpty(components) && !udfId.equals("null")) {
			ListIterator<Component> iterator = components.listIterator();
			while (iterator.hasNext()) {
				Component component = iterator.next();
				String path = null;
				if (StringUtils.isNotEmpty(endPointfilterUdfId)) {
					path = "$..[?(@.componentCodeId ==" + componentCodeId + " &&" + " " + endPointfilterUdfId
							+ " in @.designDetails[*].udfId)].designDetails[?(@.udfId == " + udfId + ")]";
				} else {
					path = "$..[?(@.componentCodeId == " + componentCodeId + ")].designDetails[?(@.udfId == " + udfId
							+ ")]";
				}

				TypeRef<List<UDFBaseData>> typeRef = new TypeRef<List<UDFBaseData>>() {
				};
				List<UDFBaseData> designDetailList = jsonPathUtil.search(component, path, typeRef);
				String json = "";
				if (CollectionUtils.isNotEmpty(designDetailList)) {
					if (udfAttribute == null || !StringUtils.isNotEmpty(udfAttribute)) {
						json = removeDesignByUdfId(udfId, udfAttribute, component, path, designDetailList, json);
					} else {
						json = updateDesignByUdfId(udfId, udfAttribute, setType, component, path, designDetailList,
								json);
					}
				} else {
					if (udfAttribute != null && StringUtils.isNotEmpty(udfAttribute)) {
						isDesignChange = true;
						json = addDesignByUdfId(componentCodeId, udfId, udfAttribute, setType, component, json);
					}
				}
				this.setDesignChangeStatus(designDetailList, setType, udfAttribute);
				if (StringUtils.isNotEmpty(json)) {
					try {
						component = mapper.readValue(json, Component.class);
					} catch (IOException e) {
						logger.error("Exception from setDesignDataByUdfIdForADE>>", e);
					}
				}
				iterator.set(component);
			}
		}
	}

	/**
	 * Call external api for ADE.
	 *
	 * @param dppOrchReq     the dpp orch req
	 * @param pedDmaapReq    the ped dmaap req
	 * @param circuitDetails the circuit details
	 * @param inputMap       the input map
	 * @param nxDesign       the nx design
	 */
	protected void callExternalApiForADE(JSONObject dppOrchReq, NxPEDStatusDMaap pedDmaapReq,
			CircuitDetails circuitDetails, Map<String, Object> inputMap, NxDesign nxDesign) {
		String statusCd = null;
		if (null != inputMap.get(TDDConstants.CIRCUIT_LEVEL_STATUS)) {
			statusCd = (String) inputMap.get(TDDConstants.CIRCUIT_LEVEL_STATUS);
		} else if (null != inputMap.get(TDDConstants.SOLUTION_LEVEL_STATUS)) {
			statusCd = (String) inputMap.get(TDDConstants.SOLUTION_LEVEL_STATUS);
		}

		if (isDesignChange) {
			String offerId = (String) inputMap.get(TDDConstants.OFFER_ID);
			if (null != dppOrchReq) {
				this.callOrchCustomPricingOrderFlow(dppOrchReq, TDDConstants.OFFER_ID_BUNDLE_CD_MAPPING.get(offerId),
						nxDesign);
			}

		}

		// publishing dmaap
		this.createPedDmaapDesignForADE(pedDmaapReq, circuitDetails, inputMap);
		this.callMessageRouter(inputMap, pedDmaapReq, nxDesign);

		// sending mail
		this.callMailService(inputMap, nxDesign);
	}

	/**
	 * Creates the ped dmaap design for ADE.
	 * 
	 * @param pedDmaapReq       the ped dmaap req
	 * @param pedCircuitDetails the ped circuit details
	 * @param inputMap          the input map
	 */
	protected void createPedDmaapDesignForADE(NxPEDStatusDMaap pedDmaapReq, CircuitDetails pedCircuitDetails,
			Map<String, Object> inputMap) {
		Long nxSolutionId = inputMap.get(TDDConstants.NX_SOLUTION_ID) != null
				? (Long) inputMap.get(TDDConstants.NX_SOLUTION_ID)
				: 0l;
		String offerId = inputMap.get(TDDConstants.OFFER_ID) != null ? (String) inputMap.get(TDDConstants.OFFER_ID)
				: "";
		
		String flowType = inputMap.get(TDDConstants.FLOW_TYPE) != null ? (String) inputMap.get(TDDConstants.FLOW_TYPE) : "";
		pedDmaapReq.setOfferId(offerId);
		pedDmaapReq.setSuccessAsrItemId(new ArrayList<>(Arrays.asList(pedCircuitDetails.getAsrItemId())));
		pedDmaapReq.setEventType((String) inputMap.get(TDDConstants.RESPONSE_TYPE));
		String nxStatus = (String) inputMap.get(TDDConstants.SOLUTION_LEVEL_STATUS);
		if (pedDmaapReq.getEventType().equalsIgnoreCase("Solution")) {
			pedDmaapReq.setNxStatus(nxStatus);
		} else {
			pedDmaapReq.setDesignStatus((String) inputMap.get(TDDConstants.CIRCUIT_LEVEL_STATUS));
			pedDmaapReq.setNxStatus((String) inputMap.get(TDDConstants.CIRCUIT_LEVEL_STATUS));
			if (pedDmaapReq.getDesignStatus().equalsIgnoreCase("C")) {
				pedDmaapReq.setCancellationReason(pedCircuitDetails.getCircuitCancellationReason());
			}
		}
		List<NxMpDeal> dealData = nxMpDealRepository.findBySolutionId(nxSolutionId);
		if (CollectionUtils.isNotEmpty(dealData) && null != dealData.get(0)) {
			pedDmaapReq.setDealId(dealData.get(0).getDealID());
			pedDmaapReq.setVersionNumber(dealData.get(0).getVersion());
		}
		pedDmaapReq.setEstimateInterval(pedCircuitDetails.getEstimatedInterval());
		pedDmaapReq.setConfirmedInterval(pedCircuitDetails.getConfirmedInterval());
		pedDmaapReq.setNotes(pedCircuitDetails.getNotes());
		pedDmaapReq.setNssManagerATTUID(pedCircuitDetails.getNssManagerATTUID());
		pedDmaapReq.setNssManagerFirstName(pedCircuitDetails.getNssManagerFirstName());
		pedDmaapReq.setNssManagerLastName(pedCircuitDetails.getNssManagerLastName());
		pedDmaapReq.setPreliminaryServingPlanURL(pedCircuitDetails.getPreliminaryServingPlanURL());
		List<EndPointDetails> dmaapEndPointDetails = new ArrayList<>();
		List<EndpointDetails> endpointDetails = pedCircuitDetails.getEndpointDetails();
		Optional.ofNullable(endpointDetails).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull)
				.forEach(pedEndpointData -> {
					EndPointDetails dmaapEndpoint = new EndPointDetails();
					if (StringUtils.isNotEmpty(pedEndpointData.getEndpointType())) {
						if (pedEndpointData.getEndpointType().equalsIgnoreCase("A")) {
							dmaapEndpoint.setSiteId((Long) inputMap.get(TDDConstants.A_SITE_ID));
						} else {
							dmaapEndpoint.setSiteId((Long) inputMap.get(TDDConstants.Z_SITE_ID));
						}
					}
					dmaapEndpoint.setLocationclli(pedEndpointData.getLocationCLLI());
					dmaapEndpoint.setEndpointType(pedEndpointData.getEndpointType());
					dmaapEndpoint.setEdgelessDesignIndicator(pedEndpointData.getEdgelessDesignIndicator());
					dmaapEndpoint.setCommonLanguageFacilityId(pedEndpointData.getCommonLanguageFacilityId());
					dmaapEndpoint.setAlternateSWCCLLI(pedEndpointData.getAlternateSWCCLLI());
					dmaapEndPointDetails.add(dmaapEndpoint);
				});
		pedDmaapReq.setEndPointDetails(dmaapEndPointDetails);

		String statusCd = null;
		if (null != inputMap.get(TDDConstants.CIRCUIT_LEVEL_STATUS)) {
			statusCd = (String) inputMap.get(TDDConstants.CIRCUIT_LEVEL_STATUS);
		} else if (null != inputMap.get(TDDConstants.SOLUTION_LEVEL_STATUS) && !StringConstants.SALES_IPNE.equalsIgnoreCase(flowType) ) {
			statusCd = (String) inputMap.get(TDDConstants.SOLUTION_LEVEL_STATUS);
		}
		if (statusCd != null && !statusCd.isEmpty() && "D".equals(statusCd)) {
			String kmzMapLink = inputMap.containsKey(TDDConstants.TDD_KMZ_MAP_LINK)
					? (String) inputMap.get(TDDConstants.TDD_KMZ_MAP_LINK)
					: null;
			if (kmzMapLink != null) {
				pedDmaapReq.setKmzMapLink(kmzMapLink);
			}
		}
	}

	/**
	 * Call orch custom pricing order flow.
	 *
	 * @param request   the request
	 * @param offerName the offer name
	 * @param nxDesign  the nx design
	 * @return the retreive ICBPSP response
	 */
	protected RetreiveICBPSPResponse callOrchCustomPricingOrderFlow(JSONObject request, String offerName,
			NxDesign nxDesign) {
		logger.info("Inside callOrchCustomPricingOrderFlow method for offer: {}", offerName);
		RetreiveICBPSPResponse response = null;
		String status = CommonConstants.SUCCESS_STATUS;
		try {
			response = dME2RestClient.callOrchCustomPricingOrderFlow(request, offerName);
		} catch (SalesBusinessException e) {
			status = CommonConstants.FAIL_STATUS;
			logger.error("Exception from dme2Client while calling DPP orchestration custom pricing Order flow>>", e);
		}
		/*
		 * if(null!=response && response.getStatus().getCode().equals("200")) {
		 * status=CommonConstants.SUCCESS_STATUS; }
		 */
		this.saveDataInAuditTbl(nxDesign.getNxDesignId(), status,
				CommonConstants.AUDIT_TRANSACTION_CONSTANTS.TDD_ORCH_UPDATE.getValue());
		return response;
	}

	/**
	 * Call message router.
	 *
	 * @param inputMap    the input map
	 * @param pedDmaapReq the ped dmaap req
	 * @param portData    the port data
	 * @param nxDesign    the nx design
	 */
	protected void callMessageRouter(Map<String, Object> inputMap, NxPEDStatusDMaap pedDmaapReq, NxDesign nxDesign) {
		inputMap.put(CommonConstants.AUDIT_ID, nxDesign.getNxDesignId());
		inputMap.put(CommonConstants.AUDIT_TRANSACTION,
				CommonConstants.AUDIT_TRANSACTION_CONSTANTS.TDD_DMAAP.getValue());
		String flowType = inputMap.get(TDDConstants.FLOW_TYPE) != null ? (String) inputMap.get(TDDConstants.FLOW_TYPE) : "";
		if (StringConstants.SALES_IPNE.equalsIgnoreCase(flowType)) {
			pedDmaapReq.setIpeIndicator(StringConstants.CONSTANT_Y);
			}
		
		dmaapPublishEventsServiceImpl.triggerDmaapEventForPEDRequest(pedDmaapReq, inputMap);
	}

	/**
	 * Call mail service.
	 *
	 * @param inputMap the input map
	 */
	protected void callMailService(Map<String, Object> inputMap, NxDesign nxDesign) {
		String status = CommonConstants.FAIL_STATUS;
		MailResponse mailResp = null;
		Long solutionId = inputMap.get(TDDConstants.SOLUTION_ID) != null ? (Long) inputMap.get(TDDConstants.SOLUTION_ID)
				: 0l;
		String attuId = inputMap.get(TDDConstants.ATT_ID) != null ? (String) inputMap.get(TDDConstants.ATT_ID) : "";
		try {
			mailResp = (MailResponse) mailService.prepareAndSendMailForPEDRequest(solutionId, attuId);
		} catch (SalesBusinessException e) {
			logger.error("Exception from callMailService>>", e);
		}
		if (null != mailResp) {
			status = CommonConstants.SUCCESS_STATUS;
		}
		this.saveDataInAuditTbl(nxDesign.getNxDesignId(), status,
				CommonConstants.AUDIT_TRANSACTION_CONSTANTS.TDD_MAIL.getValue());
	}

	/**
	 * Save data in audit tbl.
	 *
	 * @param nxDesign    the nx design
	 * @param status      the status
	 * @param transaction the transaction
	 */
	public void saveDataInAuditTbl(Long id, String status, String transaction) {
		NxDesignAudit nxDesignAudit = new NxDesignAudit();
		nxDesignAudit.setNxRefId(id);
		nxDesignAudit.setTransaction(transaction);
		nxDesignAudit.setStatus(status);
		nxDesignAudit.setCreatedDate(new Date());
		nxDesignAudit.setModifedDate(new Date());
		tddRepositoryService.saveAuditData(nxDesignAudit);
	}

	/**
	 * Creates the offer block.
	 *
	 * @param dppOrchReq the dpp orch req
	 * @param siteList   the site list
	 * @param offerId    the offer id
	 */
	protected void createOfferBlock(RetreiveICBPSPRequest dppOrchReq, String offerId) {
		Offer offer = new Offer();
		offer.setBundleCode(TDDConstants.OFFER_ID_BUNDLE_CD_MAPPING.get(offerId));
		offer.setOfferId(offerId);
		dppOrchReq.getSolution().setOffers(Arrays.asList(offer));
		dppOrchReq.getSolution().setBundleCode(TDDConstants.OFFER_ID_BUNDLE_CD_MAPPING.get(offerId));
	}

	/**
	 * Adds the design by udf id.
	 *
	 * @param componentCodeId the component code id
	 * @param udfId           the udf id
	 * @param udfAttribute    the udf attribute
	 * @param setType         the set type
	 * @param obj             the port
	 * @param json            the json
	 * @return the string
	 */
	protected <T> String addDesignByUdfId(String componentCodeId, String udfId, String udfAttribute, String setType,
			T obj, String json) {
		/*
		 * String path1 = new JsonPathExpressionBuilder()
		 * .arraynode(CommonConstants.COMPONENT).select()
		 * .where(CommonConstants.COMPONENT_CODE_ID).is(componentCodeId).key(
		 * CommonConstants.DESIGN_DETAILS).build();
		 */
		String path1 = "$..[?(@.componentCodeId == " + componentCodeId + ")].designDetails";
		UDFBaseData designDetail = new UDFBaseData();
		if (setType.equalsIgnoreCase(CommonConstants.TEXT)) {
			List<String> udfAttributeTextList = new ArrayList<>();
			designDetail.setUdfId(Integer.valueOf(udfId));
			udfAttributeTextList.add(udfAttribute);
			designDetail.setUdfAttributeText(udfAttributeTextList);
			json = jsonPathUtil.add(obj, path1, designDetail, true);
		} else if (setType.equalsIgnoreCase(CommonConstants.ID)) {
			List<Long> udfAttributeIdList = new ArrayList<>();
			designDetail.setUdfId(Integer.valueOf(udfId));
			udfAttributeIdList.add(Long.valueOf(udfAttribute));
			designDetail.setUdfAttributeId(udfAttributeIdList);
			json = jsonPathUtil.add(obj, path1, designDetail, true);
		}
		return json;
	}

	/**
	 * Update design by udf id.
	 *
	 * @param udfId            the udf id
	 * @param udfAttribute     the udf attribute
	 * @param setType          the set type
	 * @param obj              the port
	 * @param path             the path
	 * @param designDetailList the design detail list
	 * @param json             the json
	 * @return the string
	 */
	protected <T> String updateDesignByUdfId(String udfId, String udfAttribute, String setType, T obj, String path,
			List<UDFBaseData> designDetailList, String json) {
		if (setType.equalsIgnoreCase(CommonConstants.TEXT)) {
			List<String> udfAttributeTexts = designDetailList.get(0).getUdfAttributeText();
			if (null == udfAttributeTexts) {
				udfAttributeTexts = new ArrayList<String>();
			}
			udfAttributeTexts.clear();
			udfAttributeTexts.add(udfAttribute);
			UDFBaseData designDetail = new UDFBaseData();
			designDetail.setUdfId(Integer.valueOf(udfId));
			designDetail.setUdfAttributeText(udfAttributeTexts);
			json = jsonPathUtil.set(obj, path, designDetail, true);
		} else if (setType.equalsIgnoreCase(CommonConstants.ID)) {
			List<Long> udfAttributeIds = designDetailList.get(0).getUdfAttributeId();
			if (null == udfAttributeIds) {
				udfAttributeIds = new ArrayList<Long>();
			}
			udfAttributeIds.clear();
			udfAttributeIds.add(Long.valueOf(udfAttribute));
			UDFBaseData designDetail = new UDFBaseData();
			designDetail.setUdfId(Integer.valueOf(udfId));
			designDetail.setUdfAttributeId(udfAttributeIds);
			json = jsonPathUtil.set(obj, path, designDetail, true);
		}
		return json;
	}

	/**
	 * Adds the component block.
	 *
	 * @param components        the components
	 * @param componentCodeId   the component code id
	 * @param componentCodeType the component code type
	 */
	protected void addComponentBlock(List<Component> components, String componentCodeId, String componentCodeType) {
		// adding new Component block if not present
		if (CollectionUtils.isNotEmpty(components) && !isComponentPresent(components, componentCodeId)) {
			Component comObj = new Component();
			comObj.setComponentCodeId(Long.valueOf(componentCodeId));
			comObj.setComponentCodeType(componentCodeType);
			comObj.setDesignDetails(new ArrayList<UDFBaseData>());
			components.add(comObj);
		}
	}

	/**
	 * Checks if is component present.
	 *
	 * @param inputeCompLst   the inpute comp lst
	 * @param componentCodeId the component code id
	 * @return true, if is component present
	 */
	protected boolean isComponentPresent(List<Component> inputeCompLst, String componentCodeId) {
		String path = "$..[?(@['componentCodeId']==" + componentCodeId + ")]";
		TypeRef<List<Component>> mapType = new TypeRef<List<Component>>() {
		};
		List<Component> componentLst = jsonPathUtil.search(inputeCompLst, path, mapType);
		return CollectionUtils.isNotEmpty(componentLst) ? true : false;
	}

	/**
	 * Sets the design status.
	 *
	 * @param designStatus         the design status
	 * @param designSiteOfferPorts the design site offer ports
	 */
	protected void setDesignStatus(String designStatus, List<Port> designSiteOfferPorts) {
		if (CollectionUtils.isNotEmpty(designSiteOfferPorts)) {
			ListIterator<Port> iterator = designSiteOfferPorts.listIterator();
			while (iterator.hasNext()) {
				Port port = iterator.next();
				port.setDesignStatus(designStatus);
			}
		}
	}

	/**
	 * @param designDetailList
	 * @param setType
	 * @param inputudfvalue
	 */
	protected void setDesignChangeStatus(List<UDFBaseData> designDetailList, String setType, String inputudfvalue) {
		if (CollectionUtils.isNotEmpty(designDetailList) && designDetailList.get(0) != null
				&& StringUtils.isNotEmpty(inputudfvalue)) {
			UDFBaseData udfBase = designDetailList.get(0);
			if (setType.equalsIgnoreCase(CommonConstants.TEXT)
					&& CollectionUtils.isNotEmpty(udfBase.getUdfAttributeText())) {
				if (!udfBase.getUdfAttributeText().contains(inputudfvalue)) {
					isDesignChange = true;
				}
			} else if (setType.equalsIgnoreCase(CommonConstants.ID)
					&& CollectionUtils.isNotEmpty(udfBase.getUdfAttributeId())) {
				if (!udfBase.getUdfAttributeId().contains(Long.valueOf(inputudfvalue))) {
					isDesignChange = true;
				}
			}
		}
	}

	/**
	 * Gets the ped status discription.
	 *
	 * @param statusCd     the status cd
	 * @param pedStatusMap the ped status map
	 * @return the ped status discription
	 */
	protected String getPedStatusDiscription(String statusCd, Map<String, NxLookupData> pedStatusMap) {

		if (MapUtils.isNotEmpty(pedStatusMap) && null != pedStatusMap.get(statusCd)) {
			return pedStatusMap.get(statusCd).getDescription();
		}
		return null;
	}

	protected JSONObject updateSite(JSONObject inputOrchJson, JSONObject inputSite) {
		String path1 = "$..solution.offers.*.site[0]";
		String json = jsonPathUtil.set(inputOrchJson, path1, inputSite, true);
		return JacksonUtil.toJsonObject(json);
	}

	protected JSONObject updateActionDeterminants(JSONObject baseRequest) {
		String activityPath1 = "$..actionDeterminants.*.activity";
		String json = jsonPathUtil.set(baseRequest, activityPath1, TDDConstants.UPDATE_DESIGN_ACTIVITY, true);
		return JacksonUtil.toJsonObject(json);
	}

	protected JSONObject updateSolutionStatus(JSONObject baseRequest, String staus) {
		String path = "$..solution.solutionStatus";
		String json = jsonPathUtil.set(baseRequest, path, staus, true);
		return JacksonUtil.toJsonObject(json);
	}

	protected JSONObject updateSolutionCancellationReason(JSONObject baseRequest, String cancellationReason) {
		String path = "$..solution.cancellationReason";
		String json = jsonPathUtil.set(baseRequest, path, cancellationReason, true);
		return JacksonUtil.toJsonObject(json);
	}

	protected void callAutomationFlow(NxSolutionDetail solutionDtls, Map<String, Object> inputMap) {
		if (null != solutionDtls && StringUtils.isNotEmpty(solutionDtls.getAutomationFlowInd())
				&& solutionDtls.getAutomationFlowInd().equals("Y") && this.isStatusCompleted(solutionDtls,inputMap)) {
			inputMap.put(TDDConstants.SOLUTION_DATA, solutionDtls);
			automationFlowHelperService.process(new LinkedHashMap<String, Object>(inputMap));
		}

	}

	protected boolean isStatusCompleted(NxSolutionDetail solutionDtls, Map<String, Object> inputMap) {
		boolean result = false;
		List<NxDesign> nxDesignSol = nxDesignRepository.findByNxSolutionIdAsr(solutionDtls.getNxSolutionId(), solutionDtls.getNxDesign().get(0).getAsrItemId());
		if (CollectionUtils.isEmpty(nxDesignSol)) {
			logger.info("Atx flow - {} {}", solutionDtls.getNxSolutionId(), "true");
			return true;
		}
		logger.info("Atx flow - {} {}", solutionDtls.getNxSolutionId(), result);
		return result;
    }

	@SuppressWarnings("unchecked")
	protected void saveTddRequestResponse(Integer solutionId, TransmitDesignDataRequest request,
			TransmitDesignDataResponse response) {
		CompletableFuture.runAsync(() -> {
			JSONObject r = new JSONObject();
			try {
				if (null != response) {
					r.put("Response for  Solution Id: " + solutionId,
							JacksonUtil.toJsonObject(JacksonUtil.toString(response)));

				} else {
					r.put("Response for  Solution Id: " + solutionId, "TDD_DATA_NOT_FOUND");
				}
				r.put("Request for Solution Id: " + solutionId,
						JacksonUtil.toJsonObject(JacksonUtil.toString(request)));
			} catch (Exception e) {
				logger.error("Exception while saving TDD request response logs  in audit tbl>", e);
			}
			NxDesignAudit nxDesignAudit = new NxDesignAudit();
			nxDesignAudit.setNxRefId(Long.valueOf(solutionId));
			nxDesignAudit.setTransaction("TDD_REQUEST_RESPONSE");
			nxDesignAudit.setData(r.toJSONString());
			nxDesignAudit.setCreatedDate(new Date());
			nxDesignAudit.setModifedDate(new Date());
			tddRepositoryService.saveAuditData(nxDesignAudit);
		});
	}

	protected <T> String removeDesignByUdfId(String udfId, String udfAttribute, T obj, String path,
			List<UDFBaseData> designDetailList, String json) {
		json = jsonPathUtil.delete(obj, path, true);
		return json;
	}

}
