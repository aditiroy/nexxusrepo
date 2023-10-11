package com.att.sales.nexxus.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.CommonConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dmaap.mr.util.DmaapPublishEventsServiceImpl;
import com.att.sales.nexxus.inr.AseDppRequestToSnsdRequest;
import com.att.sales.nexxus.inr.AseDppRequestToSnsdSoldCancelRequest;
import com.att.sales.nexxus.inr.AseDppRequestUdfTranslation;
import com.att.sales.nexxus.inr.InrFactory;
import com.att.sales.nexxus.ped.dmaap.model.NxPEDStatusDMaap;
import com.att.sales.nexxus.reteriveicb.model.Offer;
import com.att.sales.nexxus.reteriveicb.model.RetreiveICBPSPRequest;
import com.att.sales.nexxus.reteriveicb.model.Solution;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.NexxusListUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class PedSnsdService {
	private static Logger log = LoggerFactory.getLogger(PedSnsdService.class);
	private static final String FAILED = "F";
	private static final String OMITTED = "O";
	private static final int MAX_DESIGN_LENGTH = 175;
	private static final int MAX_CIRCUITSDETAILS_LENGTH = 100;
	private static final String NODE_NAME_DESIGN = "design";
	private static final String NODE_NAME_CIRCUITSDETAILS = "circuitsDetails";
	private static final String DMAAP_EVENT_TYPE = "Solution";
	private static final String PED_STATUS_IN_PROGRESS = "In-Progress";
	private static final String PED_STATUS_SUBMITTED = "Submitted to PED";
	private static final String PED_STATUS_CANCELLED = "CL";
	private static final String PED_STATUS_SOLD = "SD";
	private static final String PED_STATUS_FAILED = "Failed to call PED";
	private static final String SNSD_URL = "SNSD_URL";
	private com.att.sales.nexxus.common.CommonConstants constants;
	
	private ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	private Environment env;

	@Autowired
	private InrFactory inrFactory;

	@Autowired
	private HttpRestClient httpRestClient;

	@Autowired
	private NxDesignAuditRepository nxDesignAuditRepository;

	@Autowired
	private DmaapPublishEventsServiceImpl dmaapPublishEventsServiceImpl;

	@Autowired
	private NxMpDealRepository nxMpDealRepository;

	@Value("${snsd.url1}")
	private String snsdUrl1;

	@Value("${snsd.url2}")
	private String snsdUrl2;

	@Value("${snsd.username}")
	private String snsdUsername;

	@Value("${snsd.password}")
	private String snsdPassword;
	
	@Value("${azure.proxy.enabled}")
	private String isProxyEnabled;
	
	@Value("${ped.azure.proxy.enabled}")
	private String isPedProxyEnabled;
	

	@Transactional
	public void process(RetreiveICBPSPRequest retreiveICBPSPRequest, NxSolutionDetail nxSolutionDetail,
			Map<String, Object> paramMap) {
		JsonNode request = (JsonNode) paramMap.get("dppRequest");//jsonNode
		JsonNode offerss = request.at("/solution/offers");
		//if (request.getNodeType() == JsonNodeType.OBJECT) {
		
		for(int i =0; i< offerss.size(); i++) {
			ArrayNode arrayNode = (ArrayNode) offerss;
			JsonNode value = arrayNode.get(i);
			String offererIdValue= value.get("offerId").asText();
			if("210".equals(offererIdValue)){
				arrayNode.remove(i); 
			}
		} 
	
		log.info("initial RetreiveICBPSPRequest: {}", request);
		AseDppRequestUdfTranslation aseDppRequestUdfTranslation = inrFactory.getAseDppRequestUdfTranslation(request);
		try {
			aseDppRequestUdfTranslation.udfTranslate();
			JacksonUtil.trimJson(request );
			log.info("after udf translation RetreiveICBPSPRequest: {}", request);
		} catch (SalesBusinessException | RuntimeException e) {
			log.error("Exception in retreiveICBPSPRequest udf translation", e);
			saveNxDesignAudit(nxSolutionDetail, FAILED);
			return;
		}
		String status = Optional.ofNullable(retreiveICBPSPRequest.getSolution()).map(Solution::getSolutionStatus)
				.orElse(null);
		JsonNode snsdRequest = null;

		if ("N".equalsIgnoreCase(status) || "U".equalsIgnoreCase(status)) {
			AseDppRequestToSnsdRequest aseDppRequestToSnsdRequest = inrFactory.getAseDppRequestToSnsdRequest(request);
			try {
				snsdRequest = aseDppRequestToSnsdRequest.generate();
				//addIndicatorToPedReq(snsdRequest,retreiveICBPSPRequest);
//
				paramMap.put(SNSD_URL, snsdUrl1);
				log.info("generated snsd request: {}", snsdRequest);
			} catch (SalesBusinessException | RuntimeException e) {
				log.error("Exception in snsd request generation", e);
				saveNxDesignAudit(nxSolutionDetail, FAILED);
				return;
			}
		} else if ("S".equalsIgnoreCase(status) || "C".equalsIgnoreCase(status)) {
			AseDppRequestToSnsdSoldCancelRequest aseDppRequestToSnsdSoldCancelRequest = inrFactory
					.getAseDppRequestToSnsdSoldCancelRequest(request);
			try {
				snsdRequest = aseDppRequestToSnsdSoldCancelRequest.generate();
				//addIndicatorToPedReq(snsdRequest,retreiveICBPSPRequest);

				paramMap.put(SNSD_URL, snsdUrl2);
				log.info("generated snsd request: {}", snsdRequest);
			} catch (SalesBusinessException | RuntimeException e) {
				log.error("Exception in snsd request generation", e);
				saveNxDesignAudit(nxSolutionDetail, FAILED);
				return;
			}

		} else {
			log.error("unknown solutionStatus code {}", status);
			saveNxDesignAudit(nxSolutionDetail, FAILED);
			return;
		}

		List<JsonNode> snsdRequests = splitSnsdRequest(snsdRequest);
		if (snsdRequests == null) {
			log.info("snsd request has no design, omit call to snsd");
			saveNxDesignAudit(nxSolutionDetail, OMITTED);
			return;
		}

		log.info("snsd request list size is: {}", snsdRequests.size());
		log.info("snsd request list is: {}", snsdRequests);
		
		processRequests(retreiveICBPSPRequest, snsdRequests, status, nxSolutionDetail, paramMap);

	}

	
	/*public void addIndicatorToPedReq(JsonNode snsdRequest, RetreiveICBPSPRequest request) {
		if (null != snsdRequest && null != request && null != request.getSolution()) {
			List<Offer> offers = request.getSolution().getOffers();
			if (CollectionUtils.isNotEmpty(offers)) {
				Offer offer = offers.get(0);
				if (null != offer && null != offer.getOfferId()) {
					int id = Integer.parseInt(offer.getOfferId());
					String offerName = salesMsDao.getOfferNameByOfferId(id);
					if (null != offerName && offerName.trim().equalsIgnoreCase("ASENoD")) {
						List<Site> sites = offers.get(0).getSite();
						if (CollectionUtils.isNotEmpty(sites)) {
							List<Site> filteredSites = sites.stream()
									.filter(site -> null != site && null != site.getMacdType()
											&& null != site.getMacdActivity()
											&& site.getMacdType().trim().equalsIgnoreCase("change")
											&& site.getMacdActivity().trim()
													.equalsIgnoreCase("Change Ethernet Payment Plan"))
									.collect(Collectors.toList());
							if (CollectionUtils.isNotEmpty(filteredSites)) {
								((ObjectNode) snsdRequest).put("nssEngagementIndicator", "false");
							}
						}
					}
				}
			}
		}
	}*/
	 
	
	protected void processRequests(RetreiveICBPSPRequest retreiveICBPSPRequest, List<JsonNode> snsdRequests,
			String status, NxSolutionDetail nxSolutionDetail, Map<String, Object> paramMap) {
		String optyId = Optional.ofNullable(retreiveICBPSPRequest.getSolution()).map(Solution::getOptyId).orElse(null);
		Long solutionId = Optional.ofNullable(retreiveICBPSPRequest.getSolution()).map(solution -> {
			if (solution.getPricerDSolutionId() != null) {
				return solution.getPricerDSolutionId();
			} else if (solution.getExternalKey() != null) {
				return solution.getExternalKey();
			}
			return null;
		}).orElse(null);
		String offerId = Optional.ofNullable(retreiveICBPSPRequest.getSolution()).map(Solution::getOffers)
				.map(List::stream).orElse(Stream.empty()).findFirst().map(Offer::getOfferId).orElse(null);
		NxPEDStatusDMaap nxPEDStatusDMaap = new NxPEDStatusDMaap();
		nxPEDStatusDMaap.setOpportunityId(optyId);
		nxPEDStatusDMaap.setSolutionId(solutionId.intValue());
		nxPEDStatusDMaap.setOfferId(offerId);
		nxPEDStatusDMaap.setNxSolutionId(String.valueOf(nxSolutionDetail.getNxSolutionId()));
		nxPEDStatusDMaap.setEventType(DMAAP_EVENT_TYPE);

		snsdRequests.forEach(snsdRequest -> {
			Map<String, Object> inputmap = new HashMap<>();
			inputmap.put(CommonConstants.AUDIT_ID, nxSolutionDetail.getNxSolutionId());
			inputmap.put(CommonConstants.AUDIT_TRANSACTION,
					CommonConstants.AUDIT_TRANSACTION_CONSTANTS.PED_SNSD_PROGRESS_DMAAP.getValue());
			nxPEDStatusDMaap.setNxStatus(PED_STATUS_IN_PROGRESS);
			updateAsrItemId(snsdRequest, nxPEDStatusDMaap);
			// dmaapPublishEventsServiceImpl.triggerDmaapEventForPEDRequest(nxPEDStatusDMaap, inputmap);
			String snsdResponse = callSnsdService(snsdRequest, paramMap);
			updateNxDesignAudit(status, snsdResponse, nxSolutionDetail, snsdRequest, nxPEDStatusDMaap);
		});
	}

	protected void updateAsrItemId(JsonNode snsdRequest, NxPEDStatusDMaap nxPEDStatusDMaap) {
		List<JsonNode> disignNodes = new ArrayList<>();
		if (!snsdRequest.path(NODE_NAME_DESIGN).isMissingNode()) {
			disignNodes = JacksonUtil.arrayNodeToList(snsdRequest.path(NODE_NAME_DESIGN));
		} else if (!snsdRequest.path(NODE_NAME_CIRCUITSDETAILS).isMissingNode()) {
			disignNodes = JacksonUtil.arrayNodeToList(snsdRequest.path(NODE_NAME_CIRCUITSDETAILS));
		}
		List<String> asrItemIds = disignNodes.stream().map(n -> n.path("asrItemId"))
				.filter(n -> !n.isMissingNode() && !n.isNull()).map(JsonNode::asText).collect(Collectors.toList());
		nxPEDStatusDMaap.setAsrItemId(asrItemIds);
	}

	protected List<JsonNode> splitSnsdRequest(JsonNode snsdRequest) {
		List<JsonNode> snsdRequests = new ArrayList<>();
		if (!snsdRequest.path(NODE_NAME_DESIGN).isMissingNode()
				&& snsdRequest.path(NODE_NAME_DESIGN).size() > MAX_DESIGN_LENGTH) {
			splitSnsdRequest(snsdRequest, snsdRequests, MAX_DESIGN_LENGTH, NODE_NAME_DESIGN);
		} else if (!snsdRequest.path(NODE_NAME_CIRCUITSDETAILS).isMissingNode()
				&& snsdRequest.path(NODE_NAME_CIRCUITSDETAILS).size() > MAX_CIRCUITSDETAILS_LENGTH) {
			splitSnsdRequest(snsdRequest, snsdRequests, MAX_CIRCUITSDETAILS_LENGTH, NODE_NAME_CIRCUITSDETAILS);
		} else if (snsdRequest.path(NODE_NAME_CIRCUITSDETAILS).isMissingNode()
				&& snsdRequest.path(NODE_NAME_DESIGN).isMissingNode()) {
			return null;
		} else {
			snsdRequests.add(snsdRequest);
		}
		return snsdRequests;
	}

	protected void splitSnsdRequest(JsonNode snsdRequest, List<JsonNode> snsdRequests, int maxLength,
			String arrayNodeName) {
		JsonNode array = snsdRequest.get(arrayNodeName);
		ObjectNode snsdRequestObj = (ObjectNode) snsdRequest;
		snsdRequestObj.remove(arrayNodeName);
		List<JsonNode> arrayList = JacksonUtil.arrayNodeToList(array);
		NexxusListUtil.batches(arrayList, maxLength).forEach(chunk -> {
			JsonNode arrayChunk = JacksonUtil.listToArrayNode(chunk);
			ObjectNode copy = snsdRequestObj.deepCopy();
			copy.set(arrayNodeName, arrayChunk);
			snsdRequests.add(copy);
		});
	}

	private void updateNxDesignAudit(String status, String snsdResponse, NxSolutionDetail nxSolutionDetail,
			JsonNode snsdRequest, NxPEDStatusDMaap nxPEDStatusDMaap) {
		String flowType = nxSolutionDetail.getFlowType();
		Map<String, Object> inputmap = new HashMap<>();
		inputmap.put(CommonConstants.AUDIT_ID, nxSolutionDetail.getNxSolutionId());
		inputmap.put(CommonConstants.AUDIT_TRANSACTION,
				CommonConstants.AUDIT_TRANSACTION_CONSTANTS.PED_SNSD_STATUS_DMAAP.getValue());
		if (snsdResponse == null) {
			nxPEDStatusDMaap.setNxStatus(PED_STATUS_FAILED);
			dmaapPublishEventsServiceImpl.triggerDmaapEventForPEDRequest(nxPEDStatusDMaap, inputmap);
			if (snsdRequest == null) {
				saveNxDesignAudit(nxSolutionDetail, FAILED);
			} else {
				saveNxDesignAudit(nxSolutionDetail, FAILED, snsdRequest.toString());
				if (null != nxPEDStatusDMaap.getAsrItemId()) {
					updateNxMpDeal(snsdRequest, nxPEDStatusDMaap.getAsrItemId());	
				}
			}
		} else {
			try {

				// dmaap message change starts
				org.codehaus.jackson.JsonNode response = mapper.readTree(snsdResponse);
				// for ASENod and ASE
				if ("6".equals(nxPEDStatusDMaap.getOfferId()) || "103".equals(nxPEDStatusDMaap.getOfferId())) {
					if (response.path("correlation_id").asText().isEmpty()) {
						nxPEDStatusDMaap.setNxStatus(PED_STATUS_FAILED);
						updateNxMpDeal(snsdRequest, nxPEDStatusDMaap.getAsrItemId());
						saveNxDesignAudit(nxSolutionDetail, FAILED, snsdRequest.toString());
						dmaapPublishEventsServiceImpl.triggerDmaapEventForPEDRequest(nxPEDStatusDMaap, inputmap);
					} else {
						updateNxMpDeal(snsdRequest, new ArrayList<>());
						String auditStatus = null;
						if ("N".equalsIgnoreCase(status) || "U".equalsIgnoreCase(status)) {
							auditStatus = status.toUpperCase();
						} else if ("S".equalsIgnoreCase(status)) {
							auditStatus = "SD";
						} else if ("C".equalsIgnoreCase(status)) {
							auditStatus = "CL";
						}
						saveNxDesignAudit(nxSolutionDetail, auditStatus, snsdRequest.toString());
					}
				} else { // for ADE
				 if (response.path("queued").asText().equalsIgnoreCase("false")) {
					if ("C".equalsIgnoreCase(snsdRequest.at("/design/0/designStatus").asText())
							|| "C".equalsIgnoreCase(snsdRequest.at("/statusCode").asText())) {
						nxPEDStatusDMaap.setNxStatus(PED_STATUS_CANCELLED);
					} else if ("S".equalsIgnoreCase(snsdRequest.at("/design/0/designStatus").asText())
							|| "S".equalsIgnoreCase(snsdRequest.at("/statusCode").asText())) {
						nxPEDStatusDMaap.setNxStatus(PED_STATUS_SOLD);
					} else {
						nxPEDStatusDMaap.setNxStatus(PED_STATUS_SUBMITTED);
					}

					// JsonNode description = message.at("/response/description");
					List<String> successAsrs = new ArrayList<String>();
					List<String> failedAsrs = new ArrayList<String>();
					org.codehaus.jackson.JsonNode message = response.path("message");
					if ("S".equalsIgnoreCase(snsdRequest.at("/design/0/designStatus").asText())
							|| "S".equalsIgnoreCase(snsdRequest.at("/statusCode").asText())
							|| "C".equalsIgnoreCase(snsdRequest.at("/statusCode").asText())) {
						if ("0".equalsIgnoreCase(message.path("Errors").asText())) {
							successAsrs.addAll(nxPEDStatusDMaap.getAsrItemId());
						} else {
							org.codehaus.jackson.JsonNode results = message.path("Results");
							for (org.codehaus.jackson.JsonNode result : results) {
								if (!result.path("DynamiteError").asBoolean()) {
									org.codehaus.jackson.JsonNode dynamiteRequest = result.path("DynamiteRequest");
									successAsrs.add(dynamiteRequest.path("COMMON_ID").asText());
								}
							}
						}
					} else {
						org.codehaus.jackson.JsonNode designDetails = message.path("designDetails");
						successAsrs = Optional.ofNullable(designDetails).map(dds -> {
							Iterable<org.codehaus.jackson.JsonNode> iterable = dds::iterator;
							return StreamSupport.stream(iterable.spliterator(), false);
						}).orElse(Stream.empty()).filter(dd -> "SUCCESS".equals(dd.path("result").asText()))
								.map(dd -> dd.path("asrItemId").asText()).collect(Collectors.toList());
						nxPEDStatusDMaap.setMessage(message);
					}
					
					if(("sales_ipne").equalsIgnoreCase(flowType)) {
						nxPEDStatusDMaap.setIpeIndicator("Y");
					}
					nxPEDStatusDMaap.setSuccessAsrItemId(successAsrs);
					failedAsrs = nxPEDStatusDMaap.getAsrItemId().stream()
							.filter(s -> !nxPEDStatusDMaap.getSuccessAsrItemId().contains(s))
							.collect(Collectors.toList());
					nxPEDStatusDMaap.setAsrItemId(failedAsrs);
					updateNxMpDeal(snsdRequest, failedAsrs);
					//send dmap when fail ASR itemId is there and empty the success ASR item ID
					if(CollectionUtils.isNotEmpty(nxPEDStatusDMaap.getAsrItemId())) {
						nxPEDStatusDMaap.setSuccessAsrItemId(new ArrayList<>());
						dmaapPublishEventsServiceImpl.triggerDmaapEventForPEDRequest(nxPEDStatusDMaap, inputmap);
					}
					String auditStatus = null;
					if ("N".equalsIgnoreCase(status) || "U".equalsIgnoreCase(status)) {
						auditStatus = status.toUpperCase();
					} else if ("S".equalsIgnoreCase(status)) {
						auditStatus = "SD";
					} else if ("C".equalsIgnoreCase(status)) {
						auditStatus = "CL";
					}
					if (!nxPEDStatusDMaap.getAsrItemId().isEmpty()) {
						auditStatus = auditStatus + " partially";
					}
					saveNxDesignAudit(nxSolutionDetail, auditStatus, snsdRequest.toString());
				 }else if(response.path("queued").asText().equalsIgnoreCase("true")) {
						updateNxMpDeal(snsdRequest, new ArrayList<>());
						String auditStatus = null;
						if ("N".equalsIgnoreCase(status) || "U".equalsIgnoreCase(status)) {
							auditStatus = status.toUpperCase();
						} else if ("S".equalsIgnoreCase(status)) {
							auditStatus = "SD";
						} else if ("C".equalsIgnoreCase(status)) {
							auditStatus = "CL";
						}
						saveNxDesignAudit(nxSolutionDetail, auditStatus, snsdRequest.toString());
				 }	
			  }
			} catch (IOException e) {
				log.error("error in parsing snsd response", e);
			}
		}
	}

	protected void updateNxMpDeal(JsonNode snsdRequest, List<String> failedAsrs) {
		String nxPedStatus = failedAsrs.size() == 0 ? "Y" : "N";
		JsonNode imsDealNumberNode = snsdRequest.at("/imsDealNumber");
		JsonNode imsVersionNumberNode = snsdRequest.at("/imsVersionNumber");
		if (!imsDealNumberNode.isNull() && !imsDealNumberNode.isMissingNode() && !imsVersionNumberNode.isNull()
				&& !imsVersionNumberNode.isMissingNode()) {
			int imsDealNumber = imsDealNumberNode.asInt();
			int imsVersionNumber = imsVersionNumberNode.asInt();
			List<NxMpDeal> nxMpDeals = nxMpDealRepository.findBydealIDVersnId(String.valueOf(imsDealNumber),
					String.valueOf(imsVersionNumber));
			nxMpDeals.forEach(nxMpDeal -> nxMpDeal.setNxPedStatusInd(nxPedStatus));
			nxMpDealRepository.saveAll(nxMpDeals);
		}
	}

	private void saveNxDesignAudit(NxSolutionDetail nxSolutionDetail, String status, String data) {
		NxDesignAudit nxDesignAudit = new NxDesignAudit();
		nxDesignAudit.setNxRefId(nxSolutionDetail.getNxSolutionId());
		nxDesignAudit.setTransaction("PED_Snsd");
		nxDesignAudit.setStatus(status);
		nxDesignAudit.setData(data);
		nxDesignAuditRepository.save(nxDesignAudit);
	}

	private void saveNxDesignAudit(NxSolutionDetail nxSolutionDetail, String status) {
		saveNxDesignAudit(nxSolutionDetail, status, null);
	}

	public String callSnsdService(JsonNode snsdRequest, Map<String, Object> paramMap) {
		//log.info("calling ped snsd with url: {} request: {}", paramMap.get(SNSD_URL), snsdRequest);
		String snsdResponse = null;
		if (snsdRequest != null) {
			Map<String, String> headers = new HashMap<>();
			headers.put(StringConstants.REQUEST_CONTENT_TYPE, "application/json");
			headers.put("USER-ID", snsdUsername);
			headers.put("PASSWORD", snsdPassword);
			headers.put("Accept", "*/*");
			try {
				String snsdUrl = paramMap.get(SNSD_URL).toString();
				String proxy = null;
				if(StringConstants.CONSTANT_Y.equals(isProxyEnabled) && StringConstants.CONSTANT_Y.equals(isPedProxyEnabled)) {
					proxy = env.getProperty(constants.AZURE_HTTP_PROXY);
				}	
				snsdResponse =  httpRestClient.callHttpRestClient(snsdUrl, HttpMethod.PUT, null, snsdRequest.toString(),
						headers, proxy);
						//restClientUtil.callMPRestClient(snsdRequest.toString(), snsdUrl, "PUT", headers,
						//queryParameters);
			} catch (SalesBusinessException e) {
				log.error("Exception in calling snsd service", e);
			}
		}
		String logSnsdResponse = snsdResponse;
		log.info("snsd response is {}", logSnsdResponse);
		return snsdResponse;
	}	
}
