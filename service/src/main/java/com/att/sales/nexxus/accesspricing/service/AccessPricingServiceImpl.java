/**
 * 
 */
package com.att.sales.nexxus.accesspricing.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.att.aft.dme2.internal.google.common.base.Optional;
import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.Message;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.model.Status;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.accesspricing.model.AccessPricingResponse;
import com.att.sales.nexxus.accesspricing.model.AccessPricingResponseWrapper;
import com.att.sales.nexxus.admin.model.UploadEthTokenRequest;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.AuditTrailConstants;
import com.att.sales.nexxus.constant.MessageConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxAccessPricingData;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.NxUser;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.model.solution.NxTeam;
import com.att.sales.nexxus.dao.repository.NxAccessPricingDataRepository;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxTeamRepository;
import com.att.sales.nexxus.dao.repository.NxUserRepository;
import com.att.sales.nexxus.model.APUiResponse;
import com.att.sales.nexxus.model.AccesPricingDuplicatetokenid;
import com.att.sales.nexxus.model.AccesPricingFailedDqId;
import com.att.sales.nexxus.model.AccessPricingUiRequest;
import com.att.sales.nexxus.model.AccessPricingUiResponse;
import com.att.sales.nexxus.model.QueoteRequestList;
import com.att.sales.nexxus.model.QuoteDetails;
import com.att.sales.nexxus.model.QuoteRequest;
import com.att.sales.nexxus.model.QuoteResponse;
import com.att.sales.nexxus.service.AccessPricingService;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.att.sales.nexxus.userdetails.service.UserServiceImpl;
import com.att.sales.nexxus.util.AuditUtil;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.NxSolutionUserLockUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.commons.lang3.StringUtils;

/**
 * The Class AccessPricingServiceImpl.
 *
 * @author RudreshWaladaunki
 */
@Service
public class AccessPricingServiceImpl extends BaseServiceImpl implements IglooAccessPricingService {
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(AccessPricingServiceImpl.class);

	/** The repository. */
	@Autowired
	private NxAccessPricingDataRepository repository;

	/** The access pricing service. */
	@Autowired
	private AccessPricingService accessPricingService;

	/** The solution repo. */
	@Autowired
	private NxSolutionDetailsRepository solutionRepo;
	
	@Autowired
	private NxTeamRepository nxTeamRepository;
	
	@Autowired
	private NxDesignAuditRepository nxDesignAuditRepository;
	
	@Autowired
	private NxMpDealRepository nxMpDealRepository;
	
	@Autowired
	private NxRequestDetailsRepository nxRequestDetailsRepository;
	
	@Autowired
	private UserServiceImpl userServiceImpl;

	@Autowired
	private NxUserRepository nxUserRepository;
	
	/** The env. */
	@Autowired
	private Environment env;
	
	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private HttpRestClient httpRestClient;
		
	@Autowired
	private AuditUtil auditUtil;
	
	@Autowired
	private NxSolutionUserLockUtil nxSolutionUserLockUtil;

	@Value("${azure.proxy.enabled}")
	private String isProxyEnabled;
	
	@Autowired
	private NxMyPriceRepositoryServce  nxMyPriceRepositoryServce;
	/**
	 * Gets the access pricing.
	 *
	 * @param request the request
	 * @return the access pricing
	 * @throws SalesBusinessException the sales business exception
	 * @see com.att.sales.nexxus.accesspricing.service.IglooAccessPricingService#getAccessPricing(com.att.sales.nexxus.model.AccessPricingUiRequest)
	 * @Param getAccessPricing()
	 * @Param throws
	 */
	@Override
	public ServiceResponse getAccessPricing(AccessPricingUiRequest request) throws SalesBusinessException {
		Long currentTime = System.currentTimeMillis();
        Long startTime = System.currentTimeMillis() - currentTime;
		logger.info("Entered getAccessPricing() method");
		AccessPricingResponse resp;
		AccessPricingResponseWrapper resposeWrapper;
		APUiResponse apResp = new APUiResponse();
		List<AccessPricingUiResponse> responseList = new ArrayList<>();
		QuoteRequest quoteRequest = new QuoteRequest();
		List<QueoteRequestList> listquote = new ArrayList<>();
		QueoteRequestList listquoteRequest;
		Long nxSolutionId = request.getNxSolutionId();
		String description = null;
		if(!request.isBulkupload() && StringConstants.ADD_ACTION_TYPE.equalsIgnoreCase(request.getAction())) {
			Set<String> duplicatetokenSet = new HashSet<>();
			HashSet<String> quoteSet=new HashSet<String>();
				if(CollectionUtils.isNotEmpty(request.getDqId())) {
					for(String quote: request.getDqId()) {
						if(quoteSet.contains(quote)) {
							duplicatetokenSet.add(quote);
						}else {
							quoteSet.add(quote);
						}
					}
				String duplicateTokens = duplicatetokenSet.stream().collect(Collectors.joining(", "));
				if(duplicateTokens.isEmpty() || request.getNxSolutionId() != 0L ) {
					//to check db duplicate token
					Set<String> tokensDbSet = new HashSet<>();
					List<NxAccessPricingData> accessPricingdatas = repository
							.findByTokensAndQuotes(new HashSet(request.getDqId()), request.getNxSolutionId());
					java.util.Optional.ofNullable(accessPricingdatas).map(List::stream).orElse(Stream.empty()).forEach(item -> {
						String token=item.getEthToken()!=null?item.getEthToken():item.getIglooQuoteId();
						tokensDbSet.add(token);
					});
					
					duplicateTokens = tokensDbSet.stream().collect(Collectors.joining(", "));
					logger.info("DB duplicateTokens:==>>" + duplicateTokens);
				}
				//to check db duplicate token
				if(!duplicateTokens.isEmpty()) {
					description="Duplicate token exist for "+duplicateTokens;
					validationStatus(apResp, com.att.sales.nexxus.common.MessageConstants.INVALID_DATA_MSG, 
							com.att.sales.nexxus.common.MessageConstants.INVALID_DATA_CODE,description);
					return apResp;
				}
			}
		}
			//if nxSolutionId is null, create solution for quick igloo quote
		if(Optional.fromNullable(request.getNxSolutionId()).isPresent() && request.getNxSolutionId().equals(0L) ) {
			try {
				NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
				description = StringConstants.FLOW_TYPE_IGLOO_QUOTE +"_"+LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				nxSolutionDetail.setFlowType(StringConstants.FLOW_TYPE_IGLOO_QUOTE);
				nxSolutionDetail.setCreatedUser(request.getUserId());
				nxSolutionDetail.setActiveYn("Y");
				nxSolutionDetail.setNxsDescription(description);
				nxSolutionDetail.setArchivedSolInd("N");
				nxSolutionDetail = solutionRepo.save(nxSolutionDetail);
				createNxTeamEntry(nxSolutionDetail, request.getUserId());
				nxSolutionId = nxSolutionDetail.getNxSolutionId();
				request.setNxSolutionId(nxSolutionId);
				Long endTime = System.currentTimeMillis() - currentTime;
		        Long executionTime = endTime-startTime;
				//for capturing audit trail
				auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.IGLOO_SOLUTION_CREATE,request.getUserId(),AuditTrailConstants.SUCCESS,null,null,executionTime,null);
			}catch(Exception e) {
				Long endTime = System.currentTimeMillis() - currentTime;
		        Long executionTime = endTime-startTime;
				auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.IGLOO_SOLUTION_CREATE,request.getUserId(),AuditTrailConstants.FAIL,null,null,executionTime,null);
			}
		}
		if (request.getDqId() != null && CollectionUtils.isNotEmpty(request.getDqId())) {
			for (String req : request.getDqId()) {
				listquoteRequest = new QueoteRequestList();
				listquoteRequest.setRequestType("13");
				listquoteRequest.setDqId(req);
				listquoteRequest.setUserId(request.getUserId());
				/*if(java.util.Optional.ofNullable(request.getCountry()).isPresent()) {
				listquoteRequest.setCountry(request.getCountry());
				} else {
					listquoteRequest.setCountry("US");
				}*/
				listquoteRequest.setPortId(request.getPortId());
				listquoteRequest.setSiteId(request.getSiteId());
				if(!(java.util.Optional.ofNullable(request.getPortId()).isPresent() && java.util.Optional.ofNullable(request.getPortId()).isPresent())) {
					listquoteRequest.setQueryType("GET");
				}
				listquote.add(listquoteRequest);
			}

		}
		quoteRequest.setQuoteRequest(listquote);
		try {
			if (StringConstants.ADD_ACTION_TYPE.equalsIgnoreCase(request.getAction())) {
				//resposeWrapper = dme.getPricingAccess(quoteRequest);
				String uri = env.getProperty("pricing.ms.uri");		
				Map<String, String> requestHeaders = new HashMap<>();
				requestHeaders.put("Offer", "AVPN");
				requestHeaders.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.AZURE_MS_AUTHORIZATION));
				com.fasterxml.jackson.databind.ObjectMapper thisMapper = new com.fasterxml.jackson.databind.ObjectMapper();
				thisMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
				String requestPayload = mapper.writeValueAsString(quoteRequest);
				String proxy = null;
				if(StringConstants.CONSTANT_Y.equals(isProxyEnabled)) {
					proxy = env.getProperty(CommonConstants.AZURE_HTTP_PROXY);
				}
				String response = httpRestClient.callHttpRestClient(uri,  HttpMethod.POST, null, requestPayload, 
						requestHeaders, proxy);
				
				AccessPricingResponse responseObj = thisMapper.readValue(response, AccessPricingResponse.class);
				
				JsonNode tree = mapper.readTree(response);
				resposeWrapper = new AccessPricingResponseWrapper(responseObj, tree);
				
				if(null != resposeWrapper) {
					resp = resposeWrapper.getAccessPricingResponse();
					apResp = saveData(resp, request, responseList, apResp, resposeWrapper.getTree());
					apResp.setNxSolutionId(nxSolutionId);
					apResp.setDescription(description);
					//for capturing audit trail	
					if(!request.isBulkupload()) {
						Long endTime = System.currentTimeMillis() - currentTime;
		                Long executionTime=endTime-startTime;	
						auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.ADD_IGLOO_QUOTES,request.getUserId(),AuditTrailConstants.SUCCESS,null,null,executionTime, null);
						nxSolutionUserLockUtil.updateSolutionLockStatus(request.getNxSolutionId(), request.getActionPerformedBy());
					}
				}
			}
		} catch (Exception e) {
			logger.error("execution failed for getPricingAccess()", e);
			storeFailedTokens(request,null,true);
			nxSolutionUserLockUtil.updateSolutionLockStatus(request.getNxSolutionId(), request.getActionPerformedBy());
			Long endTime = System.currentTimeMillis() - currentTime;
			Long executionTime = endTime-startTime;
			auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.ADD_IGLOO_QUOTES,request.getUserId(),AuditTrailConstants.FAIL,null,null,executionTime,null);
			throw new SalesBusinessException(MessageConstants.DQID_INVALID);
		}
		if (StringConstants.RETREIVE_ACTION_TYPE.equalsIgnoreCase(request.getAction())) {
			apResp = retrieveQuoteId(request, responseList, apResp);
		} else if (StringConstants.UPDATE_ACTION_TYPE.equalsIgnoreCase(request.getAction())) {
			try {
				apResp = updateQuoteId(request, responseList, apResp);
				Long endTime = System.currentTimeMillis() - currentTime;
                Long executionTime=endTime-startTime;	
				auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.UPDATE_IGLOO_QUOTES,request.getActionPerformedBy(),AuditTrailConstants.SUCCESS,null,null,executionTime,null);
			}catch(Exception e){
				Long endTime = System.currentTimeMillis() - currentTime;
                Long executionTime=endTime-startTime;
				auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.UPDATE_IGLOO_QUOTES,request.getActionPerformedBy(),AuditTrailConstants.FAIL,null,null,executionTime,null);
			}
		}
		else if (StringConstants.DELETE_ACTION_TYPE.equalsIgnoreCase(request.getAction())) {
			try {
				apResp = deleteQuoteId(request, responseList, apResp);
				apResp.setNxSolutionId(nxSolutionId);
				Long endTime = System.currentTimeMillis() - currentTime;
				Long executionTime = endTime-startTime;
				//for capturing audit trail	
				auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.DELETE_IGLOO_QUOTES,request.getActionPerformedBy(),AuditTrailConstants.SUCCESS,null,null,executionTime,null);
			}catch(Exception e){
				Long endTime = System.currentTimeMillis() - currentTime;
				Long executionTime = endTime-startTime;
				auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.DELETE_IGLOO_QUOTES,request.getActionPerformedBy(),AuditTrailConstants.FAIL,null,null,executionTime,null);
			}
		}
		if (apResp.getStatus() == null) {
			setSuccessResponse(apResp);
		}
		//To delete empty solution of the normal quote process if all the tokens has failed
		if(!request.isBulkupload()) {
			List<NxAccessPricingData> accessPricingDatas = repository.findByNxSolutionId(nxSolutionId);
			List<NxRequestDetails> nxRequestDetails = nxRequestDetailsRepository.findByNxSolutionId(nxSolutionId);
			if(CollectionUtils.isEmpty(accessPricingDatas) && CollectionUtils.isEmpty(nxRequestDetails)) {
				try {
					solutionRepo.deleteById(nxSolutionId);
					apResp.setNxSolutionId(0L);
					apResp.setDescription("Empty Solution Deleted");
				} catch (Exception e) {
					apResp.setNxSolutionId(nxSolutionId);
					apResp.setDescription(description);
					logger.error("Error : While deleting empty solution : "+e);
				}
			}
		}
		logger.info("Exiting getAccessPricing() method");
    		return apResp;
	}

	/**
	 * saving data in db.
	 *
	 * @param iglooResp    the igloo resp
	 * @param request      the request
	 * @param responseList the response list
	 * @param response     the response
	 * @param tree         the tree
	 * @return the AP ui response
	 * @throws SalesBusinessException the sales business exception
	 */
	public APUiResponse saveData(AccessPricingResponse iglooResp, AccessPricingUiRequest request,
			List<AccessPricingUiResponse> responseList, APUiResponse response, JsonNode tree)
			throws SalesBusinessException {
		Map<String, NxLookupData> nxLookupDataMap = nxMyPriceRepositoryServce.getLookupDataByItemId("IGLOO_MP_SERVICE_MAPPING");
		List<NxAccessPricingData> pricingDataList = new ArrayList<>();
		List<QuoteResponse> quoteResponseList = iglooResp.getQuoteResponse();
		JsonNode quoteResponseArray = tree.path("quoteResponse");
		//List<String> allDqIdS = request.getDqId().stream().map(quote-> new String(quote)).collect(Collectors.toList());
		Map<String, String> currencyCodes = new HashMap<String, String>();
		List<String> reqDqId = request.getDqId();
		Set<String> processedEthTokens = new HashSet<String>();
		Set<String> failedEthTokens = new HashSet<String>();
		//List<String> tokenMissingField = new ArrayList<String>();
		 Set<String> tokenMissingField = new HashSet<String>();
		if(java.util.Optional.ofNullable(quoteResponseList).isPresent()) {
			for (int i = 0; i < quoteResponseList.size(); i++) {
				logger.info("quote Response of index ["+i+"] :{}",quoteResponseList.get(i).getStatus().getCode());
				if (CollectionUtils.isNotEmpty(quoteResponseList) && "200".equalsIgnoreCase(quoteResponseList.get(i).getStatus().getCode())) {
					String quoteId = null;
					String dqId = null;
					boolean isRemoved=false;
					boolean isDuplicateEthToken = false;
					if (quoteResponseList.get(i).getQuoteDetails() != null) {
						logger.info("inside ethernet {}");
						quoteId = quoteResponseList.get(i).getQuoteDetails().getEthToken();
						
						
						if(processedEthTokens.contains(quoteResponseList.get(i).getQuoteDetails().getEthToken())) {
							isDuplicateEthToken = true;
							failedEthTokens.add(quoteResponseList.get(i).getQuoteDetails().getIglooQuoteID());
						} else {
							processedEthTokens.add(quoteResponseList.get(i).getQuoteDetails().getEthToken());
							isDuplicateEthToken = false;
							isRemoved=reqDqId.remove(quoteId);
							dqId=isRemoved?quoteId:dqId;
						}
						
						if(quoteResponseList.get(i).getQuoteDetails().getIglooQuoteID() != null && !quoteResponseList.get(i).getQuoteDetails().getIglooQuoteID().isEmpty()) {
							isRemoved=reqDqId.remove(quoteResponseList.get(i).getQuoteDetails().getIglooQuoteID());
							dqId=isRemoved?quoteResponseList.get(i).getQuoteDetails().getIglooQuoteID():dqId;
						}
						NxAccessPricingData nxAccessPricingData = new NxAccessPricingData();
						nxAccessPricingData.setLocationYn("New");
						if(request.isBulkupload()) {
							UploadEthTokenRequest uploadedToken = getUploadedToken(request.getBulkUploadTokens(),quoteId, quoteResponseList.get(i).getQuoteDetails().getIglooQuoteID());
							if(null != uploadedToken && java.util.Optional.ofNullable(uploadedToken.getSiteRefId()).isPresent()) {
								nxAccessPricingData.setSiteRefId(String.valueOf(uploadedToken.getSiteRefId()));
							}
							if(null != uploadedToken && java.util.Optional.ofNullable(uploadedToken.getCircuitId()).isPresent()) {
								nxAccessPricingData.setCircuitId(String.valueOf(uploadedToken.getCircuitId()));
							}
							if(null != uploadedToken && null != uploadedToken.getPortStatus() && !uploadedToken.getPortStatus().isEmpty()) {
								nxAccessPricingData.setLocationYn(uploadedToken.getPortStatus().trim());
							} 
							if(null != uploadedToken && java.util.Optional.ofNullable(uploadedToken.getNxsiteId()).isPresent()) {
								nxAccessPricingData.setNxSiteId(uploadedToken.getNxsiteId());
							}
						}
						nxAccessPricingData.setAttEthPop(quoteResponseList.get(i).getQuoteDetails().getAttEthPop());
						nxAccessPricingData.setIglooQuoteId(quoteResponseList.get(i).getQuoteDetails().getIglooQuoteID());
						nxAccessPricingData
								.setReqContractTerm(quoteResponseList.get(i).getQuoteDetails().getContractTerm());
						String accessArch = quoteResponseList.get(i).getQuoteDetails().getAccessArch();
						if(null != accessArch) {
							quoteResponseList.get(i).getQuoteDetails().setAccessArch(accessArch.toUpperCase());
							nxAccessPricingData.setAccessType(accessArch.toUpperCase());
						}
						nxAccessPricingData.setReqPhysicalInterface(quoteResponseList.get(i).getQuoteDetails().getReqPhysicalInterface());
						nxAccessPricingData.setIcbApprMrcDsc(quoteResponseList.get(i).getQuoteDetails().getDiscountMonthlyRecurringPrice());
						nxAccessPricingData.setReqFloor(quoteResponseList.get(i).getQuoteDetails().getReqFloor());
						nxAccessPricingData.setReqRoom(quoteResponseList.get(i).getQuoteDetails().getReqRoom());
						nxAccessPricingData.setReqBuilding(quoteResponseList.get(i).getQuoteDetails().getReqBuilding());
						nxAccessPricingData.setReqStreetAddress(quoteResponseList.get(i).getQuoteDetails().getReqStreetAddress());
						nxAccessPricingData.setReqCity(quoteResponseList.get(i).getQuoteDetails().getReqCity());
						nxAccessPricingData.setReqState(quoteResponseList.get(i).getQuoteDetails().getReqState());
						nxAccessPricingData.setReqCountry(quoteResponseList.get(i).getQuoteDetails().getReqCountry());
						nxAccessPricingData.setReqZipCode(quoteResponseList.get(i).getQuoteDetails().getReqZipCode());

						BigDecimal mrcbigDecimal =  new BigDecimal(quoteResponseList.get(i).getQuoteDetails().getBaseMonthlyRecurringPrice());
						nxAccessPricingData.setMrc(String.valueOf(mrcbigDecimal));
						BigDecimal nrcbigDecimal =  new BigDecimal(quoteResponseList.get(i).getQuoteDetails().getNonRecurringCharge());
						nxAccessPricingData.setNrc(String.valueOf(nrcbigDecimal));
						nxAccessPricingData.setStreetAddress(quoteResponseList.get(i).getQuoteDetails().getStreetAddress());
						nxAccessPricingData.setCustAddr1(quoteResponseList.get(i).getQuoteDetails().getStreetAddress());
						nxAccessPricingData.setCustCity(quoteResponseList.get(i).getQuoteDetails().getCity());
						nxAccessPricingData.setCustState(quoteResponseList.get(i).getQuoteDetails().getState());						
                        nxAccessPricingData.setCustCountry("US");
						nxAccessPricingData.setCustPostalcode(quoteResponseList.get(i).getQuoteDetails().getZipCode());
						nxAccessPricingData.setVendorZoneCode(quoteResponseList.get(i).getQuoteDetails().getZone());
						nxAccessPricingData.setSplConstructionCharges(quoteResponseList.get(i).getQuoteDetails().getSplConstructionCharge());
						nxAccessPricingData.setQuoteType(quoteResponseList.get(i).getQuoteDetails().getQuoteType());
						nxAccessPricingData.setEthernetLcMonthlyRecurringCost(quoteResponseList.get(i).getQuoteDetails().getEthernetLcMonthlyRecurringCost());
						nxAccessPricingData.setEthernetLcPopMonthlyRecurringCost(quoteResponseList.get(i).getQuoteDetails().getEthernetLcPopMonthlyRecurringCost());
						nxAccessPricingData.setSplConstructionCostMRC(quoteResponseList.get(i).getQuoteDetails().getSplConstructionCostMRC());
						nxAccessPricingData.setEthernetLcNonRecurringCost(quoteResponseList.get(i).getQuoteDetails().getEthernetLcNonRecurringCost());
						nxAccessPricingData.setEthernetLcPopNonRecurringCost(quoteResponseList.get(i).getQuoteDetails().getEthernetLcPopNonRecurringCost());
						nxAccessPricingData.setSplConstructionCostNRC(quoteResponseList.get(i).getQuoteDetails().getSplConstructionCostNRC());
						if(CollectionUtils.isNotEmpty(quoteResponseList.get(i).getAccessSupplierList()) && java.util.Optional.ofNullable(quoteResponseList.get(i).getAccessSupplierList().get(0).getAccessSupplier()).isPresent()) {
							nxAccessPricingData.setSupplierName(quoteResponseList.get(i).getAccessSupplierList().get(0).getAccessSupplier().getSupplierName());
						}
						if(CollectionUtils.isNotEmpty(quoteResponseList.get(i).getAccessSupplierList()) && java.util.Optional.ofNullable(quoteResponseList.get(i).getAccessSupplierList().get(0).getAccessSupplier()).isPresent() && CollectionUtils.isNotEmpty(quoteResponseList.get(i).getAccessSupplierList().get(0).getAccessSupplier().getNodeList()) && java.util.Optional.ofNullable(quoteResponseList.get(i).getAccessSupplierList().get(0).getAccessSupplier().getNodeList().get(0).getNodeObj()).isPresent()) {
							nxAccessPricingData.setClli(quoteResponseList.get(i).getAccessSupplierList().get(0).getAccessSupplier().getNodeList().get(0).getNodeObj().getClli());
							nxAccessPricingData.setNodeName(quoteResponseList.get(i).getAccessSupplierList().get(0).getAccessSupplier().getNodeList().get(0).getNodeObj().getNodeName());
						}
						if(java.util.Optional.ofNullable(quoteResponseList.get(i).getQuoteDetails().getCurrency()).isPresent()) {
							nxAccessPricingData.setCurrency(quoteResponseList.get(i).getQuoteDetails().getCurrency());
						} else {
							nxAccessPricingData.setCurrency("USD");
						}
						nxAccessPricingData.setService(quoteResponseList.get(i).getQuoteDetails().getService());
						if(java.util.Optional.ofNullable(quoteResponseList.get(i).getQuoteDetails().getBandwidth()).isPresent() 
								&& !quoteResponseList.get(i).getQuoteDetails().getBandwidth().trim().isEmpty() ) {
							nxAccessPricingData.setAccessBandwidth(Integer.valueOf(quoteResponseList.get(i).getQuoteDetails().getBandwidth()));
						}
//						if(java.util.Optional.ofNullable(quoteResponseList.get(i).getBandwidth()).isPresent()) {
//							nxAccessPricingData.setAccessBandwidth(Integer.valueOf(quoteResponseList.get(i).getBandwidth()));
//						}
						nxAccessPricingData.setNxSolutionId(request.getNxSolutionId());
						nxAccessPricingData.setIncludeYn("Y");
						nxAccessPricingData.setEthToken(quoteResponseList.get(i).getQuoteDetails().getEthToken());
						nxAccessPricingData.setSpeed(quoteResponseList.get(i).getQuoteDetails().getReqAccessBandwidth());
						JsonNode quote = quoteResponseArray.path(i);
						JsonNode intermediateJson = accessPricingService.generateIntermediateJson(quote);
						nxAccessPricingData.setIntermediateJson(intermediateJson.toString());
/*						OutputJsonFallOutData outputJsonFallOutData = accessPricingService
								.generateOutputJson(intermediateJson);
						nxAccessPricingData.setOutputJson(outputJsonFallOutData.getNxOutputBean());
						String fallOutData = outputJsonFallOutData.getFallOutData();
						if (fallOutData != null) {
							logger.info("lineItem lookup fallout: {}", fallOutData);
						}*/
						nxAccessPricingData.setRequiredFieldError("");
						nxAccessPricingData.setCreatedDate(new Date());
						if(isDuplicateEthToken) {
							nxAccessPricingData.setHasRequiredFields("N");
							tokenMissingField.add(dqId);
							nxAccessPricingData.setRequiredFieldError("Duplicate Token Found:");
							if(StringUtils.isEmpty(nxAccessPricingData.getService())) {
								nxAccessPricingData.setRequiredFieldError(nxAccessPricingData.getRequiredFieldError()+dqId+" : ");
							}
						}
						String consolidationCriteriaVal=getConsolidationCriteriaValue(intermediateJson,nxAccessPricingData.getLocationYn(), nxLookupDataMap);
						nxAccessPricingData.setConsolidation_criteria(consolidationCriteriaVal);
						if(StringUtils.isEmpty(nxAccessPricingData.getService()) 
								|| ((StringUtils.isEmpty(nxAccessPricingData.getCustCountry())) &&(StringUtils.isEmpty(nxAccessPricingData.getReqCountry())) )
								|| ((StringUtils.isEmpty(nxAccessPricingData.getSpeed())) &&((nxAccessPricingData.getAccessBandwidth()==null)) )
								|| StringUtils.isEmpty(nxAccessPricingData.getVendorZoneCode())
								|| StringUtils.isEmpty(nxAccessPricingData.getMrc())
								|| StringUtils.isEmpty(nxAccessPricingData.getNrc())){
							nxAccessPricingData.setHasRequiredFields("N");
							tokenMissingField.add(dqId);
							nxAccessPricingData.setRequiredFieldError(nxAccessPricingData.getRequiredFieldError() + " Missing Required Fields:");
							if(StringUtils.isEmpty(nxAccessPricingData.getService())) {
								nxAccessPricingData.setRequiredFieldError(nxAccessPricingData.getRequiredFieldError()+",Service");
							}
							
							if((StringUtils.isEmpty(nxAccessPricingData.getCustCountry())) &&(StringUtils.isEmpty(nxAccessPricingData.getReqCountry())) ){
								nxAccessPricingData.setRequiredFieldError(nxAccessPricingData.getRequiredFieldError()+",CustCountry");
							}
							
							if((StringUtils.isEmpty(nxAccessPricingData.getSpeed())) &&((nxAccessPricingData.getAccessBandwidth()==null)) ) {
								nxAccessPricingData.setRequiredFieldError(nxAccessPricingData.getRequiredFieldError()+",Speed");
							}
							
			                if(StringUtils.isEmpty(nxAccessPricingData.getVendorZoneCode())) {
								nxAccessPricingData.setRequiredFieldError(nxAccessPricingData.getRequiredFieldError()+",VendorZoneCode");
							}
							
							if(StringUtils.isEmpty(nxAccessPricingData.getMrc())) {
								nxAccessPricingData.setRequiredFieldError(nxAccessPricingData.getRequiredFieldError()+",Mrcg");
							}
						
							if(StringUtils.isEmpty(nxAccessPricingData.getNrc())) {
								nxAccessPricingData.setRequiredFieldError(nxAccessPricingData.getRequiredFieldError()+",Nrc");
							}
						
						  	// remove , at the beginning
							nxAccessPricingData.setRequiredFieldError(nxAccessPricingData.getRequiredFieldError().replaceFirst(",", ""));
							
						} else {
							if( !StringUtils.isEmpty(nxAccessPricingData.getCustCountry()) || !StringUtils.isEmpty(nxAccessPricingData.getReqCountry())) {
								String countryCode = (!StringUtils.isEmpty(nxAccessPricingData.getCustCountry())) ? nxAccessPricingData.getCustCountry() : nxAccessPricingData.getReqCountry(); 
								//String isoCountryCode = nxMpDealRepository.getCountryCodeByCountryIsoCode(countryCode);
								if(StringUtils.isEmpty(countryCode)) {
									nxAccessPricingData.setRequiredFieldError(nxAccessPricingData.getRequiredFieldError()+",Custcountry/Reqcountry has no ISO country code mapping");
									nxAccessPricingData.setRequiredFieldError(nxAccessPricingData.getRequiredFieldError().replaceFirst(",", ""));
									nxAccessPricingData.setHasRequiredFields("N");
									tokenMissingField.add(dqId);
								}
							} else {
								nxAccessPricingData.setHasRequiredFields("Y");
							}
							
						}
                      
                    if (!"Found-Firm".equalsIgnoreCase(quoteResponseList.get(i).getQuoteDetails().getQuoteStatus())){
                    	
                    	    tokenMissingField.add(dqId);
							nxAccessPricingData.setHasRequiredFields("N");
							
							nxAccessPricingData.setRequiredFieldError(nxAccessPricingData.getRequiredFieldError()+",Invalid QuoteStatus " + quoteResponseList.get(i).getQuoteDetails().getQuoteStatus());
						}
						
						pricingDataList.add(nxAccessPricingData);
					}
					else if (quoteResponseList.get(i).getQuoteDetails() == null) {
						logger.info("inside mow {}");
						quoteId = quoteResponseList.get(i).getSerialNumber();
						isRemoved=reqDqId.remove(quoteId);
						dqId=isRemoved?quoteId:dqId;
						NxAccessPricingData nxAccessPricingData = new NxAccessPricingData();
						nxAccessPricingData.setLocationYn("New");
						if(request.isBulkupload()) {
							UploadEthTokenRequest uploadedToken = getUploadedToken(request.getBulkUploadTokens(),quoteId, null);
							if(null != uploadedToken && java.util.Optional.ofNullable(uploadedToken.getSiteRefId()).isPresent()) {
								nxAccessPricingData.setSiteRefId(String.valueOf(uploadedToken.getSiteRefId()));
							}
							if(null != uploadedToken && null != uploadedToken.getPortStatus() && !uploadedToken.getPortStatus().isEmpty()) {
								nxAccessPricingData.setLocationYn(uploadedToken.getPortStatus().trim());
							}
							if(null != uploadedToken && java.util.Optional.ofNullable(uploadedToken.getCircuitId()).isPresent()) { 
								nxAccessPricingData.setCircuitId(String.valueOf(uploadedToken.getCircuitId())); 
							}
							if(null != uploadedToken && java.util.Optional.ofNullable(uploadedToken.getNxsiteId()).isPresent()) {
								nxAccessPricingData.setNxSiteId(uploadedToken.getNxsiteId());
							}
						}
						String accessArch = quoteResponseList.get(i).getAccessArch();
						if(null != accessArch) {
							nxAccessPricingData.setAccessType(accessArch.toUpperCase());
						}
						logger.info(" mow pop clli {}",quoteResponseList.get(i).getClli());
						nxAccessPricingData.setMowPop(quoteResponseList.get(i).getClli());
						nxAccessPricingData.setReqPhysicalInterface(quoteResponseList.get(i).getPhysicalInterface());
//						nxAccessPricingData.setTailTechnology(quoteResponseList.get(i).getTailTechnology().toString());
//						nxAccessPricingData.setReqAccessArch(quoteResponseList.get(i).getAccessArch());
						nxAccessPricingData.setReqCity(quoteResponseList.get(i).getCustCity());
						
						String isoCountryCode = nxMpDealRepository.getCountryCodeByCountryIsoCode(quoteResponseList.get(i).getCustCountry());
						
						
						if(!StringUtils.isEmpty(isoCountryCode)) {
						
						nxAccessPricingData.setReqCountry(isoCountryCode);
						
						}else {
							
						nxAccessPricingData.setReqCountry(quoteResponseList.get(i).getCustCountry());
						
						}
						nxAccessPricingData.setNxSolutionId(request.getNxSolutionId());
						nxAccessPricingData.setIncludeYn("Y");
				//		nxAccessPricingData.setEthToken(quoteResponseList.get(i).getSerialNumber());
				//		nxAccessPricingData.setIglooQuoteId(quoteResponseList.get(i).getTokenId());
						nxAccessPricingData.setEthToken(quoteResponseList.get(i).getTokenId());
						nxAccessPricingData.setIglooQuoteId(quoteResponseList.get(i).getSerialNumber());

						nxAccessPricingData.setSpeed(quoteResponseList.get(i).getSpeed());
						nxAccessPricingData.setSupplierName(quoteResponseList.get(i).getSupplierName());
						BigDecimal mrcbigDecimal =  new BigDecimal(quoteResponseList.get(i).getMonthlyPriceLocal());
				        nxAccessPricingData.setMrc(String.valueOf(mrcbigDecimal));
				        
						BigDecimal nrcbigDecimal =  new BigDecimal(quoteResponseList.get(i).getOneTimePriceLocal());
						nxAccessPricingData.setNrc(String.valueOf(nrcbigDecimal));
						nxAccessPricingData.setCustAddr1(quoteResponseList.get(i).getCustAddr1());
						nxAccessPricingData.setCustCity(quoteResponseList.get(i).getCustCity());
						nxAccessPricingData.setCustState(quoteResponseList.get(i).getCustState());
						nxAccessPricingData.setCustCountry(quoteResponseList.get(i).getCustCountry());
						nxAccessPricingData.setCustPostalcode(quoteResponseList.get(i).getCustPostalcode());
						nxAccessPricingData.setSupplierName(quoteResponseList.get(i).getSupplierName());
						nxAccessPricingData.setClli(quoteResponseList.get(i).getClli());
						nxAccessPricingData.setNodeName(quoteResponseList.get(i).getNodeName());
						nxAccessPricingData.setCurrency(quoteResponseList.get(i).getLocalCurrency());
						if (java.util.Optional.ofNullable(nxAccessPricingData.getCustCountry()).isPresent() && !java.util.Optional.ofNullable(nxAccessPricingData.getCurrency()).isPresent()) {
							if(currencyCodes.containsKey(nxAccessPricingData.getCustCountry())) {
								nxAccessPricingData.setCurrency(currencyCodes.get(nxAccessPricingData.getCustCountry()));
							} else {
								String currency = nxMpDealRepository.getCurrencyCodeByCountryIsoCode(nxAccessPricingData.getCustCountry());
								nxAccessPricingData.setCurrency(currency);
								currencyCodes.put(nxAccessPricingData.getCustCountry(), currency);
							}
						}
						nxAccessPricingData.setAlternateCurrency(quoteResponseList.get(i).getAlternateCurrency());
						nxAccessPricingData.setService(quoteResponseList.get(i).getService());
						if(java.util.Optional.ofNullable(quoteResponseList.get(i).getBandwidth()).isPresent()) {
							nxAccessPricingData.setAccessBandwidth(quoteResponseList.get(i).getBandwidth());
						} else {
							nxAccessPricingData.setAccessBandwidth(quoteResponseList.get(i).getAccessBandwidth());
						}
						
						JsonNode quote = quoteResponseArray.path(i);
						JsonNode intermediateJson = accessPricingService.generateIntermediateJson(quote);
						nxAccessPricingData.setIntermediateJson(intermediateJson.toString());
						String consolidationCriteriaVal=getConsolidationCriteriaValue(intermediateJson,nxAccessPricingData.getLocationYn(), nxLookupDataMap);
						nxAccessPricingData.setConsolidation_criteria(consolidationCriteriaVal);
						nxAccessPricingData.setRequiredFieldError("");
						nxAccessPricingData.setConsolidation_criteria(consolidationCriteriaVal);
						nxAccessPricingData.setCreatedDate(new Date());
						if(isDuplicateEthToken) {
//							nxAccessPricingData.setHasRequiredFields("N");
//							tokenMissingField.add(dqId);
//							nxAccessPricingData.setRequiredFieldError("Duplicate Token Found:");
							if(StringUtils.isEmpty(nxAccessPricingData.getService())) {
								nxAccessPricingData.setRequiredFieldError(nxAccessPricingData.getRequiredFieldError()+dqId+" : ");
							}
						}
						
						if(StringUtils.isEmpty(nxAccessPricingData.getService()) 
								|| StringUtils.isEmpty(nxAccessPricingData.getCustCountry())
								|| StringUtils.isEmpty(nxAccessPricingData.getSpeed())
								|| StringUtils.isEmpty(nxAccessPricingData.getClli())
								//|| StringUtils.isEmpty(nxAccessPricingData.getVendorZoneCode())
								|| StringUtils.isEmpty(nxAccessPricingData.getMrc())
								|| StringUtils.isEmpty(nxAccessPricingData.getNrc())){
							
							//|| StringUtils.isEmpty(nxAccessPricingData.getSplConstructionCostNRC())
					         //||StringUtils.isEmpty(nxAccessPricingData.getEthernetLcPopMonthlyRecurringCost())
							nxAccessPricingData.setHasRequiredFields("N");
							tokenMissingField.add(dqId);
							nxAccessPricingData.setRequiredFieldError("Missing Required Fields:");
							if(StringUtils.isEmpty(nxAccessPricingData.getService())) {
								nxAccessPricingData.setRequiredFieldError(nxAccessPricingData.getRequiredFieldError()+",Service");
							}
							
							if((StringUtils.isEmpty(nxAccessPricingData.getCustCountry())) &&(StringUtils.isEmpty(nxAccessPricingData.getReqCountry())) ){
								nxAccessPricingData.setRequiredFieldError(nxAccessPricingData.getRequiredFieldError()+",CustCountry");
							} else if( !StringUtils.isEmpty(nxAccessPricingData.getCustCountry()) || !StringUtils.isEmpty(nxAccessPricingData.getReqCountry())) {
								String countryCode = (!StringUtils.isEmpty(nxAccessPricingData.getCustCountry())) ? nxAccessPricingData.getCustCountry() : nxAccessPricingData.getReqCountry(); 
								//String isoCountryCode = nxMpDealRepository.getCountryCodeByCountryIsoCode(countryCode);
								if(StringUtils.isEmpty(countryCode)) {
									nxAccessPricingData.setRequiredFieldError(nxAccessPricingData.getRequiredFieldError()+",Custcountry/Reqcountry has no ISO country code mapping");
									// remove , at the beginning
									if(StringUtils.isEmpty(nxAccessPricingData.getRequiredFieldError()))
										nxAccessPricingData.setRequiredFieldError(nxAccessPricingData.getRequiredFieldError().replaceFirst(",", ""));
									nxAccessPricingData.setHasRequiredFields("N");
								}
							}
							
							if(StringUtils.isEmpty(nxAccessPricingData.getSpeed())) {
								nxAccessPricingData.setRequiredFieldError(nxAccessPricingData.getRequiredFieldError()+",Speed ");
							}
							
							if(StringUtils.isEmpty(nxAccessPricingData.getClli())) {
								nxAccessPricingData.setRequiredFieldError(nxAccessPricingData.getRequiredFieldError()+",Attethpop");
							}
//														if(StringUtils.isEmpty(nxAccessPricingData.getVendorZoneCode())) {
//								nxAccessPricingData.setRequiredFieldError(nxAccessPricingData.getRequiredFieldError()+",VendorZoneCode field is missing");
//						}
							
							if(StringUtils.isEmpty(nxAccessPricingData.getMrc())) {
								nxAccessPricingData.setRequiredFieldError(nxAccessPricingData.getRequiredFieldError()+",Mrc");
							}
						
							if(StringUtils.isEmpty(nxAccessPricingData.getNrc())) {
								nxAccessPricingData.setRequiredFieldError(nxAccessPricingData.getRequiredFieldError()+",Nrc");
							}
						
						  	// remove , at the beginning
							nxAccessPricingData.setRequiredFieldError(nxAccessPricingData.getRequiredFieldError().replaceFirst(",", ""));
							
						} else {
							if( !StringUtils.isEmpty(nxAccessPricingData.getCustCountry()) || !StringUtils.isEmpty(nxAccessPricingData.getReqCountry())) {
								String countryCode = (!StringUtils.isEmpty(nxAccessPricingData.getCustCountry())) ? nxAccessPricingData.getCustCountry() : nxAccessPricingData.getReqCountry(); 
								//String isoCountryCode = nxMpDealRepository.getCountryCodeByCountryIsoCode(countryCode);
								if(StringUtils.isEmpty(countryCode)) {
									nxAccessPricingData.setRequiredFieldError(nxAccessPricingData.getRequiredFieldError()+",Custcountry/Reqcountry has no ISO country code mapping");
									// remove , at the beginning
//									if(StringUtils.isEmpty(nxAccessPricingData.getRequiredFieldError()))
									nxAccessPricingData.setRequiredFieldError(nxAccessPricingData.getRequiredFieldError().replaceFirst(",", ""));
									nxAccessPricingData.setHasRequiredFields("N");
									tokenMissingField.add(dqId);
								}
							} else {
								nxAccessPricingData.setHasRequiredFields("Y");
							}
							
						}
                      	if(!quoteResponseList.get(i).getCoverageIndicator().startsWith("Found")) {
                      		
                      		tokenMissingField.add(dqId);
							nxAccessPricingData.setHasRequiredFields("N");
							nxAccessPricingData.setRequiredFieldError(nxAccessPricingData.getRequiredFieldError()+ " Invalid CoverageIndicator " + quoteResponseList.get(i).getCoverageIndicator());// need to change the messgage dev 2205
						}
						pricingDataList.add(nxAccessPricingData);
					}
				}
				
				/*else {
	
					String failedDqId = reqDqId.toString();
					Status status = new Status();
					List<Message> messageList = new ArrayList<>();
					Message msg = new Message();
					msg.setDescription("Failed DQID : " + failedDqId);
					msg.setCode("M2300");
					msg.setDetailedDescription("DQID FAILED");
					messageList.add(msg);
					status.setCode(HttpErrorCodes.ERROR.toString());
					status.setMessages(messageList);
					response.setStatus(status);
	
				}*/
			}
		}
/*		logger.info("all tokens:==>>"+allDqIdS.toString());
		logger.info("failed tokens:==>>"+reqDqId.toString());
		List<String> successTokens = allDqIdS.stream().filter(quoteId -> !reqDqId.contains(quoteId)).collect(Collectors.toList());
		logger.info("success tokens:==>>"+successTokens.toString());*/

		storeNxSiteID(pricingDataList,request.getNxSolutionId(), null);
		storeFailedTokens(request,reqDqId,false);
		List<String> totalFailToken=new ArrayList<>();
		if(CollectionUtils.isNotEmpty(reqDqId)){
			totalFailToken.addAll(reqDqId);
		}
		List<String> totalDuplicatetoken=new ArrayList<>();
		if(CollectionUtils.isNotEmpty(failedEthTokens)){
			totalDuplicatetoken.addAll(failedEthTokens);
		}
		

		if(CollectionUtils.isNotEmpty(totalDuplicatetoken)) {
			AccesPricingDuplicatetokenid failedPriceResp = new AccesPricingDuplicatetokenid(); 
			failedPriceResp.setduplicatetokenid(totalDuplicatetoken); 
			response.setAccesPricingDuplicatetokenid(failedPriceResp);	
		}
		

		if(CollectionUtils.isNotEmpty(totalFailToken)) {
			AccesPricingFailedDqId failedPriceResp = new AccesPricingFailedDqId(); 
			failedPriceResp.setFailedDqId(totalFailToken); 
			response.setAccesPricingFailedDqId(failedPriceResp);	
		}
		repository.saveAll(pricingDataList);
	
		NxSolutionDetail details = solutionRepo.findByNxSolutionId(request.getNxSolutionId());
		if(details != null) {
			details.setModifiedDate(new Date());
			solutionRepo.save(details);
		}
	
		//For bulkupload shoudn't call
		if(!request.isBulkupload()) {
			response = getDetailsData(request, responseList, response);
		}
		currencyCodes.clear();
		return response;
	}

	/**
	 * Retrieve quote id.
	 *
	 * @param request      the request
	 * @param responseList the response list
	 * @param response     the response
	 * @return retrieve data
	 */
	public APUiResponse retrieveQuoteId(AccessPricingUiRequest request, List<AccessPricingUiResponse> responseList,
			APUiResponse response) {
		response = getDetailsData(request, responseList, response);
		return response;
	}

	/**
	 * updating data in db.
	 *
	 * @param request      the request
	 * @param responseList the response list
	 * @param response     the response
	 * @return the AP ui response
	 * @Param response
	 */
	public APUiResponse updateQuoteId(AccessPricingUiRequest request, List<AccessPricingUiResponse> responseList,
			APUiResponse response) {
		Map<String, NxLookupData> nxLookupDataMap = nxMyPriceRepositoryServce.getLookupDataByItemId("IGLOO_MP_SERVICE_MAPPING");
		List<QuoteDetails> quoteDetailsList = request.getQuoteDetails();
		for (int i = 0; i < quoteDetailsList.size(); i++) {
			NxAccessPricingData nxAccessPricingData = repository
					.getNxAccessPricingData(quoteDetailsList.get(i).getApId());
			if (null != quoteDetailsList.get(i).getLocationYn()) {
				nxAccessPricingData.setLocationYn(quoteDetailsList.get(i).getLocationYn());
			}
			if (null != quoteDetailsList.get(i).getIncludeYn()) {
				nxAccessPricingData.setIncludeYn(quoteDetailsList.get(i).getIncludeYn());
			}
			nxAccessPricingData.setNxAccessPriceId(quoteDetailsList.get(i).getApId());
			
			JsonNode intermediateJsonNode= JacksonUtil.toJsonNode(nxAccessPricingData.getIntermediateJson());
			nxAccessPricingData.setConsolidation_criteria(getConsolidationCriteriaValue
					(intermediateJsonNode,nxAccessPricingData.getLocationYn(), nxLookupDataMap));
			repository.save(nxAccessPricingData);
		}
		NxSolutionDetail details = solutionRepo
				.findByNxSolutionId(request.getNxSolutionId());
		details.setModifiedDate(new Date());
		solutionRepo.save(details);
		response = getDetailsData(request, responseList, response);
		return response;
	}

	/**
	 * delete quoteId from db.
	 *
	 * @param request      the request
	 * @param responseList the response list
	 * @param response     the response
	 * @return the AP ui response
	 */
	public APUiResponse deleteQuoteId(AccessPricingUiRequest request, List<AccessPricingUiResponse> responseList,
			APUiResponse response) {
		if(request.getNxSolutionId() != null) {
		  if(java.util.Optional.ofNullable(request.getDqId()).isPresent()) {
			for (String req : request.getDqId()) {
				if(null!=req) {
					List<NxAccessPricingData> nxAccessPricingList = repository.findByEthTokenAndNxSolutionId(req,
							request.getNxSolutionId());
					if(!nxAccessPricingList.isEmpty()) {
						repository.deleteAll(nxAccessPricingList);
						NxSolutionDetail details  = solutionRepo.findByNxSolutionId(request.getNxSolutionId());
						details.setModifiedDate(new Date());
						solutionRepo.save(details);
					}
				}
		  }
		} 
		if(java.util.Optional.ofNullable(request.getIglooQuoteId()).isPresent()) {
			for (String req : request.getIglooQuoteId()) {
				if(null!=req) {
					List<NxAccessPricingData> nxAccessPricingList = repository.findByIglooQuoteIdAndNxSolutionId(req,
							request.getNxSolutionId());
					if(!nxAccessPricingList.isEmpty()) {
						repository.deleteAll(nxAccessPricingList);
						NxSolutionDetail details =  solutionRepo.findByNxSolutionId(request.getNxSolutionId());
						details.setModifiedDate(new Date());
						solutionRepo.save(details);
					}
				}
		  }				
		}
		response = getDetailsData(request, responseList, response);
 	   }
		return response;
	}

	/**
	 * getting data and return to Ui.
	 *
	 * @param request      the request
	 * @param responseList the response list
	 * @param response     the response
	 * @return ui response
	 */
	public APUiResponse getDetailsData(AccessPricingUiRequest request, List<AccessPricingUiResponse> responseList,
			APUiResponse response) {
		List<NxAccessPricingData> nxAccessPricingDataList = repository.findByHasrequiredfeilds(request.getNxSolutionId());
		for (int i = 0; i < nxAccessPricingDataList.size(); i++) {
			AccessPricingUiResponse accessPriceResp = new AccessPricingUiResponse();
			accessPriceResp.setCity(nxAccessPricingDataList.get(i).getReqCity());
			accessPriceResp.setState(nxAccessPricingDataList.get(i).getReqState());
			
			String isoCountryCode = nxMpDealRepository.getCountryCodeByCountryIsoCode(nxAccessPricingDataList.get(i).getCustCountry());
			
			if(!StringUtils.isEmpty(isoCountryCode)) {
				
				accessPriceResp.setCustCountry(isoCountryCode);
				
			}else {
			
				accessPriceResp.setCustCountry(nxAccessPricingDataList.get(i).getCustCountry());
			
			}
			
			accessPriceResp.setCountry(nxAccessPricingDataList.get(i).getReqCountry());
			accessPriceResp.setReqCountry(nxAccessPricingDataList.get(i).getReqCountry());
			accessPriceResp.setSupplierName(nxAccessPricingDataList.get(i).getSupplierName());
			accessPriceResp.setReqCity(nxAccessPricingDataList.get(i).getReqCity());
			accessPriceResp.setReqState(nxAccessPricingDataList.get(i).getReqState());
			accessPriceResp.setReqStreetAddress(nxAccessPricingDataList.get(i).getReqStreetAddress());
			accessPriceResp.setReqZipCode(nxAccessPricingDataList.get(i).getReqZipCode());
			accessPriceResp.setCustAddr1(nxAccessPricingDataList.get(i).getCustAddr1());
			accessPriceResp.setCustCity(nxAccessPricingDataList.get(i).getCustCity());
			accessPriceResp.setCustState(nxAccessPricingDataList.get(i).getCustState());
			accessPriceResp.setCustPostalcode(nxAccessPricingDataList.get(i).getCustPostalcode());
			accessPriceResp.setSupplierName(nxAccessPricingDataList.get(i).getSupplierName());
			accessPriceResp.setClli(nxAccessPricingDataList.get(i).getClli());
			accessPriceResp.setNodeName(nxAccessPricingDataList.get(i).getNodeName());
			accessPriceResp.setService(nxAccessPricingDataList.get(i).getService());
			accessPriceResp.setAccessBandwidth(nxAccessPricingDataList.get(i).getAccessBandwidth());
			accessPriceResp.setCurrency(nxAccessPricingDataList.get(i).getCurrency());
			accessPriceResp.setAlternateCurrency(nxAccessPricingDataList.get(i).getAlternateCurrency());
			accessPriceResp.setSiteName(nxAccessPricingDataList.get(i).getSiteRefId());
			accessPriceResp.setIglooQuoteId(nxAccessPricingDataList.get(i).getIglooQuoteId());
			accessPriceResp.setMrc(roundOff(nxAccessPricingDataList.get(i).getMrc()));
			accessPriceResp.setNrc(roundOff(nxAccessPricingDataList.get(i).getNrc()));
			accessPriceResp.setDqId(nxAccessPricingDataList.get(i).getEthToken());
			accessPriceResp.setApId(nxAccessPricingDataList.get(i).getNxAccessPriceId());
			accessPriceResp.setLocationYn(nxAccessPricingDataList.get(i).getLocationYn());
			accessPriceResp.setIncludeYn(nxAccessPricingDataList.get(i).getIncludeYn());
			accessPriceResp.setSpeed(nxAccessPricingDataList.get(i).getSpeed());
			accessPriceResp.setEthernetLcMonthlyRecurringCost(nxAccessPricingDataList.get(i).getEthernetLcMonthlyRecurringCost());
			accessPriceResp.setEthernetLcPopMonthlyRecurringCost(nxAccessPricingDataList.get(i).getEthernetLcPopMonthlyRecurringCost());
			accessPriceResp.setSplConstructionCostMRC(nxAccessPricingDataList.get(i).getSplConstructionCostMRC());
			accessPriceResp.setEthernetLcNonRecurringCost(nxAccessPricingDataList.get(i).getEthernetLcNonRecurringCost());
			accessPriceResp.setEthernetLcPopNonRecurringCost(nxAccessPricingDataList.get(i).getEthernetLcPopNonRecurringCost());
			accessPriceResp.setSplConstructionCostNRC(nxAccessPricingDataList.get(i).getSplConstructionCostNRC());
			accessPriceResp.setHasRequiredFields(nxAccessPricingDataList.get(i).getHasRequiredFields());
			String zone = nxAccessPricingDataList.get(i).getVendorZoneCode();
			accessPriceResp.setZone((zone != null) ? zone : "");
			String mpStatus = nxAccessPricingDataList.get(i).getMpStatus();
			accessPriceResp.setMpStatus((mpStatus != null) ? mpStatus : "");
			responseList.add(accessPriceResp);
		}
		response.setAccessPricingUiResponse(responseList);  
		//response.setFailedToken(failedToken(request.getNxSolutionId()));
		return response;
	}

	
	public UploadEthTokenRequest getUploadedToken(List<UploadEthTokenRequest> bulkUploadTokens,String quoteId, String uiQuoteId) {
		UploadEthTokenRequest uploadEthTokenRequest = null;
		if(CollectionUtils.isNotEmpty(bulkUploadTokens) && java.util.Optional.ofNullable(quoteId).isPresent()) {
			uploadEthTokenRequest = bulkUploadTokens.stream().filter(token->token.getQuoteId().equalsIgnoreCase(quoteId)).findAny().orElse(null);
		} 
		if(uploadEthTokenRequest == null && CollectionUtils.isNotEmpty(bulkUploadTokens) && java.util.Optional.ofNullable(uiQuoteId).isPresent()) {
			uploadEthTokenRequest = bulkUploadTokens.stream().filter(token->token.getQuoteId().equalsIgnoreCase(uiQuoteId)).findAny().orElse(null);
		} 
		return uploadEthTokenRequest;
	}
	
	public void createNxTeamEntry(NxSolutionDetail nxSolutionDetail,String attuid) throws SalesBusinessException {
		try {
			// NxUser table to get user details
			nxSolutionDetail.setNxSolutionId(nxSolutionDetail.getNxSolutionId());
			String userProfileName = userServiceImpl.getUserProfileName(attuid);
			NxTeam nxTeam = new NxTeam();
			if (!UserServiceImpl.NONE.equals(userProfileName)) {
				NxUser nxUser = nxUserRepository.findByUserAttId(attuid);
				nxTeam.setAttuid(nxUser.getUserAttId());
				nxTeam.setEmail(nxUser.getEmail());
				nxTeam.setfName(nxUser.getFirstName());
				nxTeam.setlName(nxUser.getLastName());
				nxTeam.setIsPryMVG("y");
				nxTeam.setNxSolutionDetail(nxSolutionDetail);
			}else {
				nxTeam.setAttuid(attuid);
				nxTeam.setIsPryMVG("y");
				nxTeam.setNxSolutionDetail(nxSolutionDetail);
			}
			nxTeamRepository.save(nxTeam);
		} catch (Exception e) {
			logger.error("Exception:", e);
			throw new SalesBusinessException(MessageConstants.DATA_NOT_FOUND);
		}

	}
	
	public String roundOff(String value) {
		try {
			if(java.util.Optional.ofNullable(value).isPresent() && !value.isEmpty()) {
				BigDecimal bd = new BigDecimal(value);
				bd = bd.setScale(2, BigDecimal.ROUND_HALF_EVEN);
				return bd.toString();
			}
		} catch (Exception e) {
			logger.error("Error : While rounding off value : ",e);
		}
		return null;
	}
	
	public void storeFailedTokens(AccessPricingUiRequest request,List<String> reqDqId,boolean isError) {
		if(isError) {
			reqDqId = request.getBulkUploadTokens().stream().map(obj->obj.getQuoteId()).collect(Collectors.toList());
		}
		if(request.isBulkupload()  && CollectionUtils.isNotEmpty(reqDqId)) {
			NxDesignAudit audit = nxDesignAuditRepository.findByNxRefIdAndTransaction(request.getNxSolutionId(),"Ethernet Token Bulkupload");
			JSONArray array = null;
			//NxDesignAudit audit;
			if(java.util.Optional.ofNullable(audit).isPresent()) {
				String data = audit.getData();
				JSONParser parser = new JSONParser();
				try {
					array = (JSONArray) parser.parse(data);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}else {
				audit = new NxDesignAudit();
				audit.setNxRefId(request.getNxSolutionId());
				audit.setTransaction("Ethernet Token Bulkupload");
				array = new JSONArray();
			}
			for(String quoteId : reqDqId) {
				JSONObject jsonObject = new JSONObject();
				UploadEthTokenRequest uploadedToken = getUploadedToken(request.getBulkUploadTokens(),quoteId, null);
				if(quoteId!=null) {
					jsonObject.put("quoteId", quoteId);
				}
				if(java.util.Optional.ofNullable(uploadedToken).isPresent()) {
					jsonObject.put("siteRefId", uploadedToken.getSiteRefId());
				}
				array.add(jsonObject);
			}
			audit.setData(array.toJSONString());
			nxDesignAuditRepository.save(audit);
		}
	}
	//adding the consolidationCriteria Value
	protected String getConsolidationCriteriaValue(JsonNode intermediateJson, String locationYN, Map<String, NxLookupData> nxLookupDataMap) {
		StringBuilder result = new StringBuilder();
		///if(intermediateJson.get("country")!=null) {
			//String countryIsoCode = nxMpDealRepository.getCountryCodeByCountryIsoCode(intermediateJson.get("country").asText());
			
		if ((intermediateJson.get("country")!=null && ("United States".equalsIgnoreCase(intermediateJson.get("country").asText())||"US".equalsIgnoreCase(intermediateJson.get("country").asText()))) ){
			String accessArch =intermediateJson.get("accessArch")==null?"null" : intermediateJson.get("accessArch").asText();
				String reqAccessBandwidth = intermediateJson.get("bandwidth") == null
						? (intermediateJson.get("reqAccessBandwidth") == null ? "null"
								: intermediateJson.get("reqAccessBandwidth").asText())
						: intermediateJson.get("bandwidth").asText();
				String physicalInterface =intermediateJson.get("physicalInterface")==null?"null":intermediateJson.get("physicalInterface").asText();
				String service=intermediateJson.get("service")==null?"null":intermediateJson.get("service").asText();
				NxLookupData lookupData=nxLookupDataMap.get(service);
				if(lookupData!=null) {
					service=lookupData.getDescription();
				}
				result = result.append(accessArch).append("$").append(reqAccessBandwidth).append("$")
						.append(physicalInterface).append("$").append(service).append("$").append(locationYN);
			} else {
				String technology =intermediateJson.get("technology")==null?"null":intermediateJson.get("technology").asText();
				String speed =intermediateJson.get("speed")==null?"null":intermediateJson.get("speed").asText();
				String service = intermediateJson.get("service")==null?"null":intermediateJson.get("service").asText();
				NxLookupData lookupData=nxLookupDataMap.get(service);
				if(lookupData!=null) {
					service=lookupData.getDescription();
				}
				//	String currency =intermediateJson.get("currency")==null?"null":intermediateJson.get("currency").asText();
				result = result.append(technology).append("$").append(speed).append("$").append(service).append("$").append(locationYN);
			}
		//}
		return result.toString();
	}
	
	public void validationStatus(ServiceResponse objResponse, String description, String code,
			String detailedDescription) {
		Status status = new Status();
		List<Message> msgList = new ArrayList<>();
		Message successMessage = new Message(code, description, detailedDescription);
		msgList.add(successMessage);
		status.setMessages(msgList);
		status.setCode(code);
		objResponse.setStatus(status);
	}
	
	public void storeNxSiteID(List<NxAccessPricingData> pricingDataList,Long solutionId, Map<String, Object> map) {
		if(!CollectionUtils.isEmpty(pricingDataList)) {
			NxDesignAudit nxDesignAudit = nxDesignAuditRepository.findByNxRefIdAndTransaction(solutionId, MyPriceConstants.REQUEST_SITE_ID_REF);
			AtomicInteger nxSiteIdCounter = new AtomicInteger(1);
			if (map != null && map.containsKey("maxNxsiteid")) {
				nxSiteIdCounter = new AtomicInteger(Integer.parseInt(String.valueOf(map.get("maxNxsiteid"))));
			} else if(nxDesignAudit != null) {
					nxSiteIdCounter.set(Integer.parseInt(nxDesignAudit.getData()));
			}
			for(NxAccessPricingData nxAccessPricingData: pricingDataList) {
				long nxSiteId = 0;
				String intermediateJson = nxAccessPricingData.getIntermediateJson();
				if(null != intermediateJson) {						
					if(null != nxAccessPricingData && java.util.Optional.ofNullable(nxAccessPricingData.getNxSiteId()).isPresent()
							&& map == null) {
						nxSiteId=nxAccessPricingData.getNxSiteId();
					}else {
						nxSiteId = nxSiteIdCounter.incrementAndGet();
						nxAccessPricingData.setNxSiteId(nxSiteId);
					}
					JsonNode design = JacksonUtil.toJsonNode(intermediateJson);
					ObjectNode obj = (ObjectNode) design;
					obj.put("nxSiteId", nxSiteId);
					nxAccessPricingData.setIntermediateJson(design.toString());
				}
			}
		
			if (nxDesignAudit != null) {
				nxDesignAudit.setData(String.valueOf(nxSiteIdCounter));
				nxDesignAudit.setModifedDate(new Date());
			} else {
				nxDesignAudit = new NxDesignAudit();
				nxDesignAudit.setData(String.valueOf(nxSiteIdCounter));
				nxDesignAudit.setTransaction(MyPriceConstants.REQUEST_SITE_ID_REF);
				nxDesignAudit.setNxRefId(solutionId);
				nxDesignAudit.setCreatedDate(new Date());
			}
			nxDesignAuditRepository.saveAndFlush(nxDesignAudit);
		}
	}
}
