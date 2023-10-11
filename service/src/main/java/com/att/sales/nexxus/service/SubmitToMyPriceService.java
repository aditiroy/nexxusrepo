package com.att.sales.nexxus.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.att.aft.dme2.internal.google.common.base.Strings;
import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.CommonConstants.STATUS_CONSTANTS;
import com.att.sales.nexxus.constant.CustomJsonConstants;
import com.att.sales.nexxus.constant.FmoConstants;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxAccessPricingData;
import com.att.sales.nexxus.dao.model.NxDesign;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxDesignDetails;
import com.att.sales.nexxus.dao.model.NxInrDesign;
import com.att.sales.nexxus.dao.model.NxInrDesignDetails;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxMpDesignDocument;
import com.att.sales.nexxus.dao.model.NxMpSiteDictionary;
import com.att.sales.nexxus.dao.model.NxMpSolutionDetails;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.NxSolutionSite;
import com.att.sales.nexxus.dao.model.NxValidationRules;
import com.att.sales.nexxus.dao.model.SubmitToAnotherDeal;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.model.solution.NxTeam;
import com.att.sales.nexxus.dao.repository.NxAccessPricingDataRepository;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxDesignDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxDesignRepository;
import com.att.sales.nexxus.dao.repository.NxInrDesignDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxInrDesignRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxMpDesignDocumentRepository;
import com.att.sales.nexxus.dao.repository.NxMpSiteDictionaryRepository;
import com.att.sales.nexxus.dao.repository.NxMpSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxOutputFileRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionSiteRepository;
import com.att.sales.nexxus.dao.repository.NxTeamRepository;
import com.att.sales.nexxus.dao.repository.SubmitToAnotherDealRepository;
import com.att.sales.nexxus.fallout.model.FalloutDetailsRequest;
import com.att.sales.nexxus.handlers.GetOptyInfoWSHandler;
import com.att.sales.nexxus.model.UsageRule;
import com.att.sales.nexxus.model.UsageRuleObj;
import com.att.sales.nexxus.myprice.transaction.dao.service.NxMpRepositoryService;
import com.att.sales.nexxus.myprice.transaction.model.CreateTransactionResponse;
import com.att.sales.nexxus.myprice.transaction.model.Document;
import com.att.sales.nexxus.myprice.transaction.model.GetTransactionLineItem;
import com.att.sales.nexxus.myprice.transaction.model.GetTransactionLineResponse;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdatePricingUtilInr;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdateProcessingFmoService;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdateRestProcessingService;
import com.att.sales.nexxus.myprice.transaction.service.ConfigDesignHelperService;
import com.att.sales.nexxus.myprice.transaction.service.CopyTransactionServiceImpl;
import com.att.sales.nexxus.myprice.transaction.service.CreateTransactionService;
import com.att.sales.nexxus.myprice.transaction.service.GetTransactionLineServiceImpl;
import com.att.sales.nexxus.myprice.transaction.service.IUpdateTransactionImpl;
import com.att.sales.nexxus.myprice.transaction.service.MyPriceTransactionUtil;
import com.att.sales.nexxus.myprice.transaction.service.UpdateTransactionPricingFMORestServiceImpl;
import com.att.sales.nexxus.myprice.transaction.service.UpdateTransactionPricingFMOServiceImpl;
import com.att.sales.nexxus.myprice.transaction.service.UpdateTxnSiteUploadServiceImpl;
import com.att.sales.nexxus.rome.model.GetOptyResponse;
import com.att.sales.nexxus.userdetails.model.UserDetails;
import com.att.sales.nexxus.userdetails.model.UserDetailsRequest;
import com.att.sales.nexxus.userdetails.model.UserDetailsResponse;
import com.att.sales.nexxus.userdetails.model.UserRole;
import com.att.sales.nexxus.userdetails.service.UserDetailsServiceImpl;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.att.sales.nexxus.util.RestClientUtil;
import com.att.sales.nexxus.util.ThreadMetaDataUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.TypeRef;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.ConfigureResponse;

@Component
public class SubmitToMyPriceService {

	private static Logger logger = LoggerFactory.getLogger(SubmitToMyPriceService.class);

	@Autowired
	private ProcessINRtoMP processINRtoMP;

	@Autowired
	private NxMpRepositoryService nxMpRepositoryService;

	@Autowired
	private NxSolutionSiteRepository nxSolutionSiteRepository;

	@Autowired
	private NexxusJsonUtility nexxusJsonUtility;

	@Autowired
	private NxMpDealRepository nxMpDealRepository;

	@Autowired
	private NxRequestDetailsRepository nxRequestDetailsRepository;

	@Autowired
	private CreateTransactionService createTransactionService;

	@Autowired
	private RestClientUtil restClient;

	@Autowired
	private NxDesignAuditRepository nxDesignAuditRepository;

	@Autowired
	private MyPriceTransactionUtil myPriceTransactionUtil;

	@Autowired
	private IUpdateTransactionImpl iUpdateTransactionImpl;

	@Autowired
	private GetOptyInfoWSHandler getOptyInfoWSHandler;

	@Autowired
	private NxMpSolutionDetailsRepository nxMpSolutionDetailsRepository;

	@Autowired
	private NxMpSiteDictionaryRepository nxMpSiteDictionaryRepository;

	@Autowired
	private UserDetailsServiceImpl userDetailsServiceImpl;

	@Autowired
	private JsonPathUtil jsonPathUtil;

	@Autowired
	private NxOutputFileRepository nxOutputFileRepository;

	@Autowired
	private UpdateTxnSiteUploadServiceImpl updateTxnSiteUploadServiceImpl;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private NxDesignRepository nxDesignRepository;

	@Autowired
	private NxInrDesignRepository nxInrDesignRepository;

	@Autowired
	private NxAccessPricingDataRepository nxAccessPricingDataRepository;

	@Autowired
	private InrQualifyService inrQualifyService;
	
	@Autowired
	private ConfigAndUpdateProcessingFmoService configAndUpdateProcessingFmoService;

	@Autowired
	private ConfigAndUpdatePricingUtilInr configAndUpdatePricingUtilInr;

	@Autowired
	private UpdateTransactionPricingFMOServiceImpl updateTransactionPricingFMOServiceImpl;

	@Autowired
	private InrReconfigure inrReconfigure;

	@Autowired
	private ConfigDesignHelperService configDesignHelperService;
	
	@Autowired
	private MailServiceImpl mailServiceImpl;
	
	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Autowired
	private ConfigAndUpdateRestProcessingService configAndUpdateRestProcessingService;
	
	@Autowired
	private GetTransactionLineServiceImpl getTransactionLineServiceImpl;
	
	@Autowired
	private NxMpDesignDocumentRepository nxMpDesignDocumentRepo;
	
	@Autowired
	private UpdateTransactionPricingFMORestServiceImpl updatePricingRestFmo;
	
	@Autowired
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	@Autowired
	private NxDesignDetailsRepository nxDesignDetailsRepository;
	
	@Autowired
	private CopyTransactionServiceImpl copyTransactionServiceImpl;
	
	@Autowired
	private NxTeamRepository nxTeamRepository;
	
	@Value("${myprice.restField.documentId:0l}")
	private String documentId;
	
	@Value("${myprice.getTransactionLine.retry.times:3}")
	private int mypriceGetTranSactionLineRetryTimes;
	
	@Autowired
	private NxInrDesignDetailsRepository nxInrDesignDetailsRepository;
	
	@Autowired
	private SubmitToAnotherDealRepository submitToAnotherDealRepository;
	
	
	public Map<String, Object> submitToMyPrice(NxSolutionDetail nxSolutionDetail, FalloutDetailsRequest request) {
		logger.info("Start :: submitToMyPrice");
		List<Long> nxRequestGrpId = request.getDataIds(); 
		String source = request.getReqDesc();
		Map<String, Object> result = new HashMap<String, Object>();
		Long nxSolutionId = nxSolutionDetail.getNxSolutionId();
		String productName = null;
		List<NxRequestDetails> nxRequestDetails = new ArrayList<NxRequestDetails>();
		if (nxRequestGrpId != null && nxRequestGrpId.size() > 0) {
			nxRequestDetails = nxRequestDetailsRepository.findbyNSolutionIdAndActiveYnAndNxRequestGrpIds(
					nxSolutionDetail, StringConstants.CONSTANT_Y, nxRequestGrpId);
		}

		Long iglooCount = nxAccessPricingDataRepository.getCountByNxSolIdAndIncludeIndAndMpStatus(nxSolutionDetail.getNxSolutionId());
		if (CollectionUtils.isEmpty(nxRequestDetails) && iglooCount.longValue() == StringConstants.INT_ZERO) {
			logger.info("MyPrice is not invoked as nxRequest and igloo quotes are not associated to the solution {}",
					nxSolutionId);
			result.put(MyPriceConstants.RESPONSE_STATUS, false);
			result.put("messageCode", "M00032");
			return result;
		}
		Map<String, Object> requestMetaDataMap = new HashMap<>();
		if (ServiceMetaData.getRequestMetaData() != null) {
			ServiceMetaData.getRequestMetaData().forEach((key, value) -> requestMetaDataMap.put(key, value));
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<NxMpDeal> deal = nxMpDealRepository.findBySolutionIdAndActiveYN(nxSolutionId, StringConstants.CONSTANT_Y);
		if (!Strings.isNullOrEmpty(request.getActionInd())
				&& StringConstants.APPEND_TO_OTHER_DEAL.equals(request.getActionInd())) {
			Map<String, Object> resultMap = copyTransactionServiceImpl.getDealVersionRevisionDetail(request.getDealId(),
					request.getVersion(), request.getRevision());
			
			 String ItemEmpty  = (String)resultMap.get(MyPriceConstants.ITEM_EMPTY);
	          
	          if("Y".equalsIgnoreCase(ItemEmpty)) {
	        		System.out.println("Entered integer is B: "+ 1);
	          
		          paramMap.put(MyPriceConstants.RESPONSE_STATUS, false);
		          
		          paramMap.put("messageCode", "M00420");
	          
	          return paramMap ;
	         }
			SubmitToAnotherDeal submitToAnotherDealId = submitToAnotherDealRepository.findBydealId(request.getDealId());
			if (Optional.ofNullable(submitToAnotherDealId).isPresent()) {
				logger.info("Submission to other deal id is in progress please wait till it completes to do the subsmission");
				result.put(MyPriceConstants.RESPONSE_STATUS, false);
				result.put("messageCode", "D0003");
				return result;
			} else {
				SubmitToAnotherDeal newSubmitToAnotherDealId = new SubmitToAnotherDeal();
				newSubmitToAnotherDealId.setDealId(request.getDealId());
				submitToAnotherDealRepository.save(newSubmitToAnotherDealId);
			}
	          
			NxMpDeal nxMpDeal = new NxMpDeal();
			nxMpDeal.setDealID(request.getDealId());
			
			String myPriceTransId = resultMap.containsKey(MyPriceConstants.BSID)
					? (String) resultMap.get(MyPriceConstants.BSID)
					: "";
			nxMpDeal.setTransactionId(myPriceTransId);
			int mpLatestVersion = resultMap.containsKey(MyPriceConstants.VERSION) ? (int) resultMap.get(MyPriceConstants.VERSION) : 0;
			if (CollectionUtils.isNotEmpty(deal)) {
				deal.clear();
			}
			deal.add(nxMpDeal);
			paramMap.put(MyPriceConstants.MP_LATEST_VERSION, mpLatestVersion);
		}
		if (CollectionUtils.isNotEmpty(nxRequestDetails)) {
			productName = nxRequestDetails.get(0).getProduct();
		} else if (iglooCount.longValue() != 0) {
			productName = MyPriceConstants.IGLOO_PRODUCT_NAME;
		}
		// check if nxmptransaction already exists
		if ((CollectionUtils.isNotEmpty(deal) && null != deal.get(0).getTransactionId())
				|| (!Strings.isNullOrEmpty(request.getActionInd())
						&& StringConstants.APPEND_TO_OTHER_DEAL.equals(request.getActionInd()))) {
			//Reconfigure as deal is exist
			NxMpDeal existingDeal = deal.get(0);
			if (request.getDealId() == null || request.getDealId().isEmpty()) {
				String dealId = deal.get(0).getDealID();
				SubmitToAnotherDeal submitToAnotherDealId = submitToAnotherDealRepository.findBydealId(dealId);
				if (Optional.ofNullable(submitToAnotherDealId).isPresent()) {
					logger.info("Submission to same deal id is in progress please wait till it completes to do the subsmission");
					result.put(MyPriceConstants.RESPONSE_STATUS, false);
					result.put("messageCode", "D0002");
					return result;
				} else {
					SubmitToAnotherDeal newSubmitToAnotherDealId = new SubmitToAnotherDeal();
					newSubmitToAnotherDealId.setDealId(dealId);
					submitToAnotherDealRepository.save(newSubmitToAnotherDealId);
				}
			}
			
			String nxMpStatusInd = StringUtils.isEmpty(existingDeal.getNxMpStatusInd())? "Y" : existingDeal.getNxMpStatusInd() ;
			paramMap.put(StringConstants.MY_PRICE_TRANS_ID, existingDeal.getTransactionId());
			paramMap.put(StringConstants.MY_PRICE_DEAL_ID, existingDeal.getDealID());
			paramMap.put(StringConstants.SUBMITTED_USER, request.getActionPerformedBy());
			paramMap.put(MyPriceConstants.PRODUCT_NAME, productName);

			logger.info("INR reconfigure scenario nxSolutionId : "+nxSolutionId);
			CreateTransactionResponse createTransactionResponse = new CreateTransactionResponse();
			paramMap.put(StringConstants.FLOW_TYPE, StringConstants.FLOW_TYPE_INR);
			paramMap.put(StringConstants.SOLUTIONDETAILS, nxSolutionDetail);
			paramMap.put("iglooCount", iglooCount);
			paramMap.put("IS_RECONFIGURE", "inrReconfigure");
			paramMap.put("NX_MP_STATUS_IND", nxMpStatusInd);
			paramMap.put(MyPriceConstants.SOURCE, source);
			paramMap.put(StringConstants.TRANSACTION_UPDATE, StringConstants.TRANSACTION_UPDATE_DESIGN);
			if (!Strings.isNullOrEmpty(request.getActionInd())
					&& StringConstants.APPEND_TO_OTHER_DEAL.equals(request.getActionInd())) {
				paramMap.remove(StringConstants.TRANSACTION_UPDATE);
			}
			List<NxDesignAudit> nxDesignAudit = nxDesignAuditRepository.findByNxRefId(nxSolutionId);
			paramMap.put(MyPriceConstants.NX_AUDIT_ID, nxDesignAudit.get(0).getNxAuditId());
			paramMap.put("ACTION_IND", request.getActionInd());
			paramMap.put("dealId", request.getDealId());
			paramMap.put("version", request.getVersion());
			paramMap.put("revision", request.getRevision());
			
			if(StringConstants.CONSTANT_N.equalsIgnoreCase(nxMpStatusInd)) {
				List<NxDesignAudit> designAudit = nxDesignAuditRepository.findByNxRefIdAndTransactionAndStatus(nxSolutionId, MyPriceConstants.AUDIT_UPDATE_CS,CommonConstants.FAILURE);
				if(CollectionUtils.isNotEmpty(designAudit)) {
					paramMap.put("CALL_CLEAN_SAVE", StringConstants.CONSTANT_Y);
				}
			}
			final List<Long> requestGroupIdData = nxRequestGrpId;
			
			CompletableFuture.runAsync(() -> {
				try {
					
					ThreadMetaDataUtil.initThreadMetaData(requestMetaDataMap);
					inrReconfigure.reconfigure(paramMap, createTransactionResponse, existingDeal,nxSolutionDetail,requestGroupIdData,requestMetaDataMap,request);
						} catch (Exception e) {
					logger.info("Execption", e);
				} finally {
					String dealId = null;
					if (request.getDealId() == null || request.getDealId().isEmpty()) {
						dealId = deal.get(0).getDealID();
					} else {
						dealId = request.getDealId();
					}
					
					SubmitToAnotherDeal submitToAnotherDealId = submitToAnotherDealRepository.findBydealId(dealId);
					if (submitToAnotherDealId != null) {
						submitToAnotherDealRepository.delete(submitToAnotherDealId);
					}
					ThreadMetaDataUtil.destroyThreadMetaData();
				}
			});
			paramMap.put(MyPriceConstants.SOURCE, source);
			paramMap.put(MyPriceConstants.RESPONSE_STATUS, true);
			
			return paramMap;
		}else {
				result = createTransaction(nxSolutionId, null, productName, request);
				boolean status = (boolean) result.get(MyPriceConstants.RESPONSE_STATUS);
				if (!status) {
					return result;
				}
		}
			
		/*
		 * if (!Strings.isNullOrEmpty(request.getActionInd()) &&
		 * request.getActionInd().equalsIgnoreCase(StringConstants.APPEND_TO_OTHER_DEAL)
		 * && CollectionUtils.isNotEmpty(deal)) { String
		 * nxSolId=paramMap.get(StringConstants.NEW_NX_SOL_ID).toString(); Long
		 * solId=Long.parseLong(nxSolId);
		 * logger.info("newSolutionId :::: {}",paramMap.get(StringConstants.
		 * NEW_NX_SOL_ID).toString()); List<NxSolutionDetail>
		 * nxSolutionDetails=nxSolutionDetailsRepository.findByNxSolutionId(solId);
		 * nxSolutionDetail=nxSolutionDetails.get(0); }
		 */
		//if(noReconfigure) {
		// call update clean save
		result.put(MyPriceConstants.PRODUCT_NAME, productName);
		final String prodName = productName;
		result.put(MyPriceConstants.SOURCE, source);
		if (CollectionUtils.isNotEmpty(nxRequestDetails)) {
			List<NxOutputFileModel> nxOutputFileModel = nxOutputFileRepository
					.findByNxReqId(nxRequestDetails.get(0).getNxReqId());
			if (CollectionUtils.isNotEmpty(nxOutputFileModel)) {
				result.put(MyPriceConstants.OUTPUT_JSON, nxOutputFileModel.get(0).getMpOutputJson());
			}
		}
		Map<String, Object> resultdata = new HashMap<String, Object>();
		resultdata.put(StringConstants.SUBMITTED_USER, request.getActionPerformedBy());
		resultdata.putAll(result);
		CompletableFuture.runAsync(() -> {
			try {
				ThreadMetaDataUtil.initThreadMetaData(requestMetaDataMap);
				long startTime = System.nanoTime();
				long endTime = System.nanoTime();
				Map<String, Object> updateResponse = callUpdateCleanSave(nxSolutionDetail, resultdata, nxRequestGrpId,requestMetaDataMap);
				endTime = System.nanoTime();
				
				Boolean updateStatus = updateResponse.containsKey(MyPriceConstants.RESPONSE_STATUS)
						? (Boolean) updateResponse.get(MyPriceConstants.RESPONSE_STATUS)
						: false;
						
				logger.info("callUpdateCleanSave response : {}", updateStatus);
				CreateTransactionResponse createTransactionResponse = (CreateTransactionResponse) resultdata
						.get("createTransactionRes");
						startTime = System.nanoTime();
				NxMpDeal nxMpDeal = nxMpDealRepository.findByNxTxnId(createTransactionResponse.getNxTransacId());
			
				if (!updateStatus) {
					/* send the fail mail */
					if(Optional.ofNullable(nxMpDeal).isPresent()) {
						nxMpDeal.setDealStatus(CommonConstants.FAILED);
						nxMpDeal.setModifiedDate(new Date());
						nxMpDealRepository.saveAndFlush(nxMpDeal);
					}
					mailServiceImpl.prepareMyPriceDealSubmissionRequest(nxMpDeal);
				}else {
					Map<String, Object> configUpdateResMap = new HashMap<String, Object>();
					configUpdateResMap.put("iglooCount", iglooCount);
					startTime = System.nanoTime();
					processINRtoMP.process(nxSolutionDetail, nxRequestGrpId, requestMetaDataMap, configUpdateResMap, source,
							createTransactionResponse, prodName, false);
				
				}
			} catch (Exception e) {
				logger.info("Exception", e);
			} finally {
				ThreadMetaDataUtil.destroyThreadMetaData();
			}
		});
		logger.info("End :: submitToMyPrice");
		return result;
	}

	public boolean skipCktProcessing(List<NxValidationRules> skipCktRules, JsonNode cktData,
			Map<String, Object> paramMap) {
		boolean designDataMissing = false;
		boolean isBlockHasMandatorydata=false;
		JsonNodeFactory nf = JsonNodeFactory.instance;
		if (CollectionUtils.isNotEmpty(skipCktRules)) {
			String flowtype=paramMap.containsKey(MyPriceConstants.FLOWTYPE)?
					(String)paramMap.get(MyPriceConstants.FLOWTYPE):null;
			NxValidationRules skipRule = skipCktRules.get(0);
			if(skipRule.getSubDataPath()!=null && MyPriceConstants.SOURCE_USRP.equalsIgnoreCase(flowtype) &&
					MyPriceConstants.USRP_PORT_OFFER.contains(skipRule.getName())){
				/**to capture the mandatory fallout reason at price level for port block in  usrp
				**
				*/
				List<Object> subDataNodes = inrQualifyService.getCircuits(cktData,
						skipRule.getSubDataPath());
				Set<String> falloutIdWithNoMandateData= new HashSet<>();
				if(subDataNodes!=null){
					for (int i = 0; i < subDataNodes.size(); i++) {
					String designData[] = skipRule.getValue().split(",");
					int mandatdesigncount=0;
					for (String design : designData) {
						Object obj = nexxusJsonUtility.getValue(subDataNodes.get(i), design);
						if (obj == null) {
							String identifierId = skipRule.getDescription();
							Object identifierObj =null;
							if(identifierId != null) {
								identifierObj = nexxusJsonUtility.getValue(subDataNodes.get(i), identifierId);
							}
							if(identifierObj != null) {
								String identifierObjVal = identifierObj != null ? String.valueOf(identifierObj) : null;
								if(identifierObjVal != null) {
									Set<String> identifierObjValset = (paramMap.containsKey("skipCircuits") && paramMap.get("skipCircuits") != null) ?  (Set<String>)paramMap.get("skipCircuits") : null;
									if(identifierObjValset != null) {
										identifierObjValset.add(identifierObjVal); 
										paramMap.put("skipCircuits", identifierObjValset); 
									}else {
										identifierObjValset = new HashSet<String>(); 
										identifierObjValset.add(identifierObjVal); 
										paramMap.put("skipCircuits",  identifierObjValset); 
									}
								}
								falloutIdWithNoMandateData.add(identifierObjVal);
							}
							break;
						}else {
							mandatdesigncount++;
						}
					}
					if(mandatdesigncount==designData.length) {
						isBlockHasMandatorydata=true;
					}
				}
			}
				if(!isBlockHasMandatorydata) {
					//no pricedetail has mandatory design data
					designDataMissing=true;
				}
				if(CollectionUtils.isNotEmpty(falloutIdWithNoMandateData)) {
					removeJsonObjectFromPricedetails(cktData,falloutIdWithNoMandateData);
				}
			}else {
			String designData[] = skipRule.getValue().split(",");
			for (String design : designData) {
				Object obj = nexxusJsonUtility.getValue(cktData, design);
				if (obj == null) {
					designDataMissing = true;
					String cktIdPath = skipRule.getDescription();
					if(cktIdPath != null) {
						if(MyPriceConstants.SOURCE_USRP.equalsIgnoreCase(flowtype) && MyPriceConstants.USRP_ACCESS_OFFER.contains(skipRule.getName())) {
							List<Object> f = inrQualifyService.getCircuits(cktData,
									cktIdPath);
							if(CollectionUtils.isNotEmpty(f)) {
								List<String> falloutMatchingIds=f.stream().map(Object::toString).collect(Collectors.toList());
								Set<String> falloutIds = (paramMap.containsKey("skipCircuits") && paramMap.get("skipCircuits") != null) ? 
										(Set<String>)paramMap.get("skipCircuits") : null;
								if(falloutIds != null) {
									falloutIds.addAll(falloutMatchingIds); 
									paramMap.put("skipCircuits", falloutIds); 
								}else {
									falloutIds = new HashSet<String>(); 
									falloutIds.addAll(falloutMatchingIds); 
									paramMap.put("skipCircuits",  falloutIds); 
								}

							}
						}else {
						Object cktObj = nexxusJsonUtility.getValue(cktData, cktIdPath);
						String circuitId = cktObj != null ? String.valueOf(cktObj) : null;
						if(circuitId != null) {
							Set<String> cktIds = (paramMap.containsKey("skipCircuits") && paramMap.get("skipCircuits") != null) ?  (Set<String>)paramMap.get("skipCircuits") : null;
							if(cktIds != null) {
								cktIds.add(circuitId); 
								paramMap.put("skipCircuits", cktIds); 
							}else {
								cktIds = new HashSet<String>(); 
								cktIds.add(circuitId); 
								paramMap.put("skipCircuits",  cktIds); 
							}
						  }
						}
					}
					break;
				}
			 }
			}
			/*if(skipRule.getSubData() != null && !designDataMissing) {
				try {
					List<LinkedHashMap<String, String>> items = mapper.readValue(skipRule.getSubData(), new TypeReference<List<LinkedHashMap<String, String>>>() {});
					for (LinkedHashMap<String, String> item : items) {
						if("eq".equalsIgnoreCase(item.get("check"))) {
							Object obj = nexxusJsonUtility.getValue(cktData, item.get("key"));
							if (obj != null && item.get("value").equalsIgnoreCase(obj.toString())) {
								designDataMissing = true;
								break;
							}
						}
					}
				}catch(Exception e) {
					logger.info("Error in json conversion at skip rules for inr");
				}
			}*/
		}
		return designDataMissing;
	}

	@Transactional
	protected NxInrDesign saveNxInrDesign(NxSolutionDetail nxSolutionDetail, JsonNode design,
			Map<String, Object> paramMap, Map<String, NxInrDesign> designDataMap) {
		Long nxReqId = paramMap.containsKey(MyPriceConstants.NX_REQ_ID)
				? (Long) paramMap.get(MyPriceConstants.NX_REQ_ID)
				: 0L;
		String productName = (String) paramMap.get(MyPriceConstants.PRODUCT_NAME);
		String offerName = (String) paramMap.get(MyPriceConstants.OFFER_NAME);
		String path = paramMap.get(MyPriceConstants.SUB_DATA) != null ? (String) paramMap.get(MyPriceConstants.SUB_DATA): null;
		String circuitId = paramMap.get(MyPriceConstants.CIRCUIT_ID) != null ? ((String) paramMap.get(MyPriceConstants.CIRCUIT_ID)).trim(): null;
		Long groupId = paramMap.get(MyPriceConstants.NX_GROUP_ID) != null ? (Long) paramMap.get(MyPriceConstants.NX_GROUP_ID): null;
		NxInrDesign nxInrDesign = null;
		if(!designDataMap.isEmpty() && designDataMap.containsKey(circuitId)) {
			nxInrDesign = designDataMap.get(circuitId);
		}else {
			Set<Long> groupIds = paramMap.get(MyPriceConstants.NX_GROUP_IDS) != null ? (Set<Long>) paramMap.get(MyPriceConstants.NX_GROUP_IDS): new HashSet<Long>(0);
			nxInrDesign = nxInrDesignRepository.findByNxSolutionIdAndCircuitIdAndActiveYNAndNxRequestGroupId(
						nxSolutionDetail.getNxSolutionId(), circuitId, StringConstants.CONSTANT_Y, groupIds);
			groupIds = null;
			
		}
		
		String isReconfiure = paramMap.get("IS_RECONFIGURE") != null ?  (String) paramMap.get("IS_RECONFIGURE") : null;
		String nxMpStatusInd = paramMap.containsKey("NX_MP_STATUS_IND") ? paramMap.get("NX_MP_STATUS_IND").toString() : null;
		if(null != isReconfiure && isReconfiure.equalsIgnoreCase("inrReconfigure") && null != nxMpStatusInd && nxMpStatusInd.trim().equalsIgnoreCase("Y") && Optional.ofNullable(nxInrDesign).isPresent()) {
			Long nxTxnId = paramMap.containsKey(MyPriceConstants.NX_TRANSACTION_ID) ? (Long) paramMap.get(MyPriceConstants.NX_TRANSACTION_ID) : 0L;
			logger.info("nxInrDesignId"+nxInrDesign.getNxInrDesignId()+"Calling remove transaction for deleting solution and product lines for retrigger {}", nxTxnId);
			Map<String, Object> designMap = new HashMap<>();
			designMap.put(MyPriceConstants.NX_TRANSACTION_ID, nxTxnId);
			designMap.put(MyPriceConstants.NX_DESIGN_ID, nxInrDesign.getNxInrDesignId());
			myPriceTransactionUtil.removeTransactionServiceLineImpl(designMap);
			designMap = null;
			nxInrDesign.setStatus(MyPriceConstants.DESIGN_STATUS_UPDATE);
		}
		else if (null == nxInrDesign) {
			nxInrDesign = new NxInrDesign();
			nxInrDesign.setStatus(MyPriceConstants.API_NOT_INVOKED);
		}
		nxInrDesign.setNxSolutionId(nxSolutionDetail.getNxSolutionId());
		nxInrDesign.setCircuitId(circuitId);
		nxInrDesign.setActiveYN(StringConstants.CONSTANT_Y);
		nxInrDesign.setNxRequestGroupId(groupId);

		if (nxInrDesign.getNxInrDesignDetails() != null && !nxInrDesign.getNxInrDesignDetails().isEmpty()) {
			List<NxInrDesignDetails> details = nxInrDesign.getNxInrDesignDetails().stream()
					.filter(req -> req.getProduct().equalsIgnoreCase(productName)).collect(Collectors.toList());
			List<NxInrDesignDetails> nxInrList = new ArrayList<NxInrDesignDetails>();
			if(CollectionUtils.isNotEmpty(details)) {
				if (MyPriceConstants.DDA_OFFER_NAME.equalsIgnoreCase(offerName)) {
					boolean sitefound = false;
					for (NxInrDesignDetails nxInrDesignDetail : details) {
						if (nxInrDesignDetail.getProduct().equalsIgnoreCase(productName)) {
							JsonNode currentDesignDetails = JacksonUtil.toJsonNode(nxInrDesignDetail.getDesignData());
							nxInrDesignDetail.setNxReqId(nxReqId);
							if (!currentDesignDetails.path(path).isMissingNode()) {
								JsonNode oldNode = currentDesignDetails.at("/" + path);
								Long nxSiteId = oldNode.path("nxSiteId").asLong();
								JsonNode newNode = design.at("/" + path);
								Long newNxSiteId = newNode.path("nxSiteId").asLong();
								if (nxSiteId.longValue() == newNxSiteId.longValue()) {
									ObjectNode cktNodeObj = (ObjectNode) newNode;
									cktNodeObj.put("nxSiteId", nxSiteId);
									nxInrDesignDetail.setDesignData(design.toString());
									sitefound = true;
									break;
								}else if(!oldNode.path("endPointType").isNull() && oldNode.path("endPointType").asText().equalsIgnoreCase(newNode.path("endPointType").asText())) {
									sitefound = true;
									break;
								}
							}
						}
					}
					if (!sitefound) {
						NxInrDesignDetails designDetail = new NxInrDesignDetails();
						designDetail.setDesignData(design.toString());
						designDetail.setProduct(productName);
						designDetail.setActiveYN(StringConstants.CONSTANT_Y);
						designDetail.setNxReqId(nxReqId);
						nxInrList.add(designDetail);
					}
				}else {
					for (NxInrDesignDetails nxInrDesignDetail : details) {
						if (nxInrDesignDetail.getProduct().equalsIgnoreCase(productName)) {
							JsonNode currentDesignDetails = JacksonUtil.toJsonNode(nxInrDesignDetail.getDesignData());
							nxInrDesignDetail.setNxReqId(nxReqId);
							if (!currentDesignDetails.path("nxSiteId").isMissingNode()) {
								Long nxSiteId = currentDesignDetails.path("nxSiteId").asLong();
								ObjectNode cktNodeObj = (ObjectNode) design;
								Long newNxSiteId = cktNodeObj.get("nxSiteId").asLong();
								if (nxSiteId.longValue() == newNxSiteId.longValue()) {
									cktNodeObj.put("nxSiteId", nxSiteId);
									nxInrDesignDetail.setDesignData(design.toString());
								} else {
									NxInrDesignDetails designDetail = new NxInrDesignDetails();
									designDetail.setDesignData(design.toString());
									designDetail.setProduct(productName);
									designDetail.setActiveYN(StringConstants.CONSTANT_Y);
									designDetail.setNxReqId(nxReqId);
									nxInrList.add(designDetail);
								}
							} else if (!currentDesignDetails.path(path).isMissingNode()) {
								JsonNode oldNode = currentDesignDetails.at("/" + path);
								Long nxSiteId = oldNode.path("nxSiteId").asLong();
		
								JsonNode newNode = design.at("/" + path);
								Long newNxSiteId = newNode.path("nxSiteId").asLong();
								if (nxSiteId.longValue() == newNxSiteId.longValue()) {
									if (MyPriceConstants.AVPN_OFFER_NAME.equalsIgnoreCase(productName)) {
										String subProduct = paramMap.containsKey(MyPriceConstants.SUB_PRODUCT)
												&& paramMap.get(MyPriceConstants.SUB_PRODUCT) != null
														? (String) paramMap.get(MyPriceConstants.SUB_PRODUCT)
														: null;
										if (nxInrDesignDetail.getSubProduct() == null) {
											nxInrDesignDetail.setSubProduct(subProduct);
										}
										if (oldNode.isArray()) {
											ArrayNode newArray = (ArrayNode) newNode;
											ArrayNode oldArray = (ArrayNode) oldNode;
											oldArray.add(newArray.get(0));
										}
										nxInrDesignDetail.setDesignData(currentDesignDetails.toString());
									} else {
										ObjectNode cktNodeObj = (ObjectNode) newNode;
										cktNodeObj.put("nxSiteId", nxSiteId);
										nxInrDesignDetail.setDesignData(design.toString());
									}
		
								} else {
									NxInrDesignDetails designDetail = new NxInrDesignDetails();
									designDetail.setDesignData(design.toString());
									designDetail.setProduct(productName);
									designDetail.setActiveYN(StringConstants.CONSTANT_Y);
									designDetail.setNxReqId(nxReqId);
									nxInrList.add(designDetail);
								}
							}
							if (nxInrDesignDetail.getSubProduct() == null) {
								nxInrDesignDetail.setSubProduct(paramMap.containsKey(MyPriceConstants.SUB_PRODUCT)
										&& paramMap.get(MyPriceConstants.SUB_PRODUCT) != null
										? (String) paramMap.get(MyPriceConstants.SUB_PRODUCT)
										: null);
							}
						}
					}
				}
			}else{
				NxInrDesignDetails designDetail = new NxInrDesignDetails();
				designDetail.setDesignData(design.toString());
				designDetail.setProduct(productName);
				designDetail.setActiveYN(StringConstants.CONSTANT_Y);
				designDetail.setNxReqId(nxReqId);
				designDetail.setSubProduct(paramMap.containsKey(MyPriceConstants.SUB_PRODUCT)
						&& paramMap.get(MyPriceConstants.SUB_PRODUCT) != null
						? (String) paramMap.get(MyPriceConstants.SUB_PRODUCT)
						: null);
				nxInrList.add(designDetail);
			}
			for (NxInrDesignDetails ninr : nxInrList) {
				nxInrDesign.addNxInrDesignDetails(ninr);
			}
		} else {
			NxInrDesignDetails nxInrDesignDetail = new NxInrDesignDetails();
			nxInrDesignDetail.setDesignData(design.toString());
			nxInrDesignDetail.setProduct(productName);
			nxInrDesignDetail.setActiveYN(StringConstants.CONSTANT_Y);
			nxInrDesignDetail.setNxReqId(nxReqId);
			nxInrDesignDetail.setSubProduct(paramMap.containsKey(MyPriceConstants.SUB_PRODUCT)
					&& paramMap.get(MyPriceConstants.SUB_PRODUCT) != null
					? (String) paramMap.get(MyPriceConstants.SUB_PRODUCT)
					: null);
			nxInrDesign.addNxInrDesignDetails(nxInrDesignDetail);
			
		}
		//nxInrDesignRepository.saveAndFlush(nxInrDesign);
		designDataMap.put(circuitId, nxInrDesign);
		return nxInrDesign;

	}

	public Map<String, Object> callUpdateCleanSave(NxSolutionDetail nxSolutionDetail, Map<String, Object> result,
			List<Long> nxRequestGrpId,Map<String, Object> requestMetaDataMap) {
		Long nxAuditId = (Long) result.get(MyPriceConstants.NX_AUDIT_ID);
		String source = result.get(MyPriceConstants.SOURCE) != null ? (String) result.get(MyPriceConstants.SOURCE)
				: null;
		Map<Object, Object> updateCleanTransactionCleanSaveRequestMap = new HashMap<>();
		CreateTransactionResponse createTransactionResponse = (CreateTransactionResponse) result
				.get("createTransactionRes");
		Map<String, Object> updateResponse = new HashMap<String, Object>();
		Document document = new Document();
		try {
			
			String siteAddressJson = getSiteAddress(nxRequestGrpId,nxSolutionDetail, null);
			document.setRdAttuidQ(nxSolutionDetail.getCreatedUser());
			//added rd_opportunityTeam_q tag
			logger.info("rd_opportunityTeam_q solution id is"+nxSolutionDetail.getNxSolutionId());
			List<NxTeam> nxTeamList=nxTeamRepository.findByNxSolutionId(nxSolutionDetail.getNxSolutionId());
			if(null!=nxTeamList) {
				String rdOpportunityTeamQ="";
				int i = 1;
				for(NxTeam nxTeam:nxTeamList) {
					if(i > 35) {
						break;
					}
					rdOpportunityTeamQ=rdOpportunityTeamQ.concat(nxTeam.getAttuid()+",");
					i++;
				} 
				rdOpportunityTeamQ= rdOpportunityTeamQ.substring(0, rdOpportunityTeamQ.length() - 1);
				document.setRdOpportunityTeamQ(rdOpportunityTeamQ);
			}
			String solutionDataJsonBlock = null;
			if (nxSolutionDetail.getOptyId() != null && !nxSolutionDetail.getOptyId().isEmpty()) {
				GetOptyResponse optyResp = null;
				Map<String, Object> requestMap = new HashMap<>();
//				requestMap.put("attuid", nxSolutionDetail.getCreatedUser());
				requestMap.put("attuid", result.get(StringConstants.SUBMITTED_USER));
				requestMap.put("optyId", nxSolutionDetail.getOptyId());
				requestMap.put("nxSolutionId", nxSolutionDetail.getNxSolutionId());
				requestMap.put("action", "myPriceFlow");
				try {
					optyResp = (GetOptyResponse) getOptyInfoWSHandler.initiateGetOptyInfoWebService(requestMap);
				}catch(SalesBusinessException e) {
					try {
						requestMap.put("attuid", nxSolutionDetail.getCreatedUser());
						optyResp = (GetOptyResponse) getOptyInfoWSHandler.initiateGetOptyInfoWebService(requestMap);
					}catch(SalesBusinessException e1) {
						requestMap.put("attuid", nxSolutionDetail.getOptyLinkedBy());
						optyResp = (GetOptyResponse) getOptyInfoWSHandler.initiateGetOptyInfoWebService(requestMap);
					}
				}
				
				logger.info("optyResp :: ", optyResp);
			
				if(optyResp.getOptyId()==null) {
					logger.info("Service Log :: optyResp is null ::  GetOptyInfo Webservice Invoked");
					return updateResponse;
				}

				logger.info("Service Log :: [Nexxus [MyPrice - Info] ::  GetOptyInfo Webservice Invoked");
				document.setCustomerTFirstName(optyResp.getAbsCreatedByName());
				document.setCustomerTLastName(optyResp.getAbsCreatedByName());
				document.setCustomerTCompanyName(optyResp.getCustomerName());
				document.setCustomerTAddress(optyResp.getAddress1());
				document.setCustomerTAddress2(optyResp.getAddress2());
				document.setCustomerTCity(optyResp.getCity());
				document.setCustomerTState(optyResp.getState());
				document.setCustomerTCountry(optyResp.getCountry());
				document.setCustomerTZip(optyResp.getPostalCode());
				document.setRomeMarketSegmentQ(null != optyResp.getMarketStrataValue() ? myPriceTransactionUtil.getDataFromNxLookUp(optyResp.getMarketStrataValue(), "INR_MARKET_SEGMENT") : "");
				document.setWiOpportunityTypeQ(optyResp.getType() != null ? myPriceTransactionUtil.getDataFromNxLookUp(optyResp.getType(), "MP_OPTY_TYPE") : "");
				document.setRdOpportunitySalesTeamQ(String.join(",", optyResp.getPrimaryATTUID(),
						optyResp.getPrimaryManager(), optyResp.getPrimaryManagersManager()));
			}

			// Call ADOPT to get user details like name, email
		if (MyPriceConstants.SOURCE_FMO.equalsIgnoreCase(source)) {
			UserDetailsRequest userDetailsRequest = new UserDetailsRequest();
			userDetailsRequest.setAttuid(nxSolutionDetail.getCreatedUser());
			UserDetailsResponse resp = (UserDetailsResponse) userDetailsServiceImpl.retreiveUserDetails(userDetailsRequest);
			List<UserDetails> userDetails = resp.getUserDetails();
			if (CollectionUtils.isNotEmpty(userDetails) && null != userDetails.get(0)) {
				document.setRdEmailQ(userDetails.get(0).getEmailId());
				document.setRdMobileQ(userDetails.get(0).getCellular());
				document.setRdOfficeQ(userDetails.get(0).getPhone());
				List<UserRole> userRole = userDetails.get(0).getUserRole();
				if (CollectionUtils.isNotEmpty(userRole) && null != userRole.get(0)) {
					document.setRdTitleQ(userRole.get(0).getRoleName());
				}
			}
		}
			document.setWiBudgetaryFirmQ("firm");
			document.setDealType("wireline");
			if (MyPriceConstants.SOURCE_FMO.equalsIgnoreCase(source)) {
				document.setSourceFromNexxusQ(MyPriceConstants.SOURCE_ADOPT);
                document.setExternalSolutionId(nxSolutionDetail.getExternalKey());//ambika
			} else {
				document.setSourceFromNexxusQ(source);
                document.setExternalSolutionId(nxSolutionDetail.getNxSolutionId());//ambika
			}
			document.setRdDescriptionQ(nxSolutionDetail.getNxsDescription());
			document.setOpportunityIDT(nxSolutionDetail.getOptyId());
			if(nxSolutionDetail.getCustomerName()==null) { 
				document.setCustomerTCompanyName("N/A"); 
			}else { 
				document.setCustomerTCompanyName(nxSolutionDetail.getCustomerName()); 
			}

			try {
				NxMpSiteDictionary nxMpSiteDictionary = persistNxMpSiteDictionary(
						createTransactionResponse.getNxTransacId(), siteAddressJson, MyPriceConstants.SOURCE_INR);
				updateCleanTransactionCleanSaveRequestMap.put("documents", document);
				solutionDataJsonBlock = getupdateTransactionCSRTJSON(updateCleanTransactionCleanSaveRequestMap);
				String replaceStringsolutionDataJsonBlock = "\\r\\n";
				solutionDataJsonBlock = solutionDataJsonBlock.replace(replaceStringsolutionDataJsonBlock, "");
				persistNxSolutoinDetail(solutionDataJsonBlock, createTransactionResponse.getNxTransacId(),
						MyPriceConstants.SOURCE_INR, nxMpSiteDictionary.getSiteRefId());

				// read integrated site details
				document.setSiteAddressJson(siteAddressJson); // This holds site which are not from ip&e
				document.setSiteAddress(updateTxnSiteUploadServiceImpl.translateSiteJsonRemoveDuplicatedNxSiteId(nxMpSiteDictionary.getSiteJson())); // This holds ip&e site
				if (nxMpSiteDictionary.getSiteJson() != null) {
					document.setWiUpdateOverrideQ(true);
				}
				updateCleanTransactionCleanSaveRequestMap.put("documents", document);
				solutionDataJsonBlock = getupdateTransactionCSRTJSON(updateCleanTransactionCleanSaveRequestMap);
				solutionDataJsonBlock = solutionDataJsonBlock.replace(replaceStringsolutionDataJsonBlock, "");

			} catch (JsonProcessingException e) {
				throw new SalesBusinessException();
			}
			updateResponse = iUpdateTransactionImpl.callMsForCleanSaveTransaction(
					createTransactionResponse.getMyPriceTransacId(), solutionDataJsonBlock);
			int code = (int) updateResponse.get("code");
			if (code == CommonConstants.SUCCESS_CODE) {
				logger.info("call for updateTransactionCleanSave_t success!");
				updateResponse.put(MyPriceConstants.RESPONSE_STATUS, true);
			} else {
				logger.info("Call for updateTransactionCleanSave_t failed");
				updateResponse.put(MyPriceConstants.RESPONSE_STATUS, false);
			}

		} catch (SalesBusinessException e) {
			logger.error(e.toString());
			logger.info("UpdateTransactionCleanSave is failed for the solution id {}",
					nxSolutionDetail.getNxSolutionId());
			updateResponse.put(MyPriceConstants.RESPONSE_CODE, e.getHttpErrorCode());
			updateResponse.put(MyPriceConstants.RESPONSE_MSG, e.getMessage());
			updateResponse.put(MyPriceConstants.RESPONSE_STATUS, false);
		}
		int code = (int) updateResponse.get(MyPriceConstants.RESPONSE_CODE);
		if (code == CommonConstants.SUCCESS_CODE) {
			updateResponse.put(MyPriceConstants.RESPONSE_STATUS, true);
			myPriceTransactionUtil.updateNxDesignAuditStatus(CommonConstants.SUCCESS, MyPriceConstants.AUDIT_UPDATE_CS,
					nxAuditId);
		} else {
			updateResponse.put(MyPriceConstants.RESPONSE_STATUS, false);
			myPriceTransactionUtil.updateNxMpDealMpInd(StringConstants.CONSTANT_N,
					createTransactionResponse.getNxTransacId());
			myPriceTransactionUtil.updateNxDesignAuditStatus(CommonConstants.FAILURE, MyPriceConstants.AUDIT_UPDATE_CS,
					nxAuditId);
			myPriceTransactionUtil.updateMpDealStatusByNxTxnId(CommonConstants.FAILED,createTransactionResponse.getNxTransacId());
		}
		return updateResponse;
	}
	
	public String getSiteAddress(List<Long> nxRequestGrpId,NxSolutionDetail nxSolutionDetail, Map<String, Object> map) {
		String siteAddressJson = null;
		List<Object> sites = new ArrayList<Object>();
		/**
		 * populate igloo site address first as in circuit consolidation igloo sites address have preference
		 * 
		 * */
		Map<String, Map<String, Object>> processedSites = new LinkedHashMap<String, Map<String, Object>>();
		if (map != null && map.containsKey("appendtoDealTxnId")) {
			NxMpSiteDictionary siteDictionary = nxMpSiteDictionaryRepository
					.findByNxTxnId(Long.parseLong(String.valueOf(map.get("appendtoDealTxnId"))));
			if(siteDictionary != null && siteDictionary.getSiteAddress() != null) {
				try {
					Map<String, Object> siteMap = mapper.readValue(siteDictionary.getSiteAddress(), Map.class);
					List<Map<String, Object>> siteArray = (List<Map<String, Object>>) siteMap.get("siteAddress");
					for(Map<String, Object> site : siteArray) {
						String nxSiteId = site.get("nxSiteId") != null ? String.valueOf(site.get("nxSiteId")) : null;
						processedSites.put(nxSiteId, site);
					}
				} catch (JsonMappingException e) {
					logger.info("Execption", e);
				} catch (JsonProcessingException e) {
					logger.info("Execption", e);
				}
			}
		}
				
		getIglooSiteAddress(nxSolutionDetail.getNxSolutionId(),processedSites);

		if(nxRequestGrpId != null) {
			//Set<String> processedSites = new HashSet<String>();
			for(Long nxGrpId : processINRtoMP.sortGrpsOnPrecedence(nxRequestGrpId)) {
				List<NxSolutionSite> nxSolutionSite = nxSolutionSiteRepository.findByNxSolutionIdAndNxRequestGroupIdAndActiveYN(nxSolutionDetail.getNxSolutionId(), nxGrpId, StringConstants.CONSTANT_Y);
				if(CollectionUtils.isNotEmpty(nxSolutionSite)) {
					for(NxSolutionSite siteJson : nxSolutionSite) {
						try {
							List<Map<String, Object>> siteMap = mapper.readValue(siteJson.getSiteAddress(), List.class);
							for(Map<String, Object> site : siteMap) {
								String nxSiteId = site.get("nxSiteId") != null ? String.valueOf(site.get("nxSiteId")) : null;
								String nxSiteIdZ = site.get("nxSiteIdZ") != null ? String.valueOf(site.get("nxSiteIdZ")) : null;
								if(nxSiteId != null) {
									if(processedSites.containsKey(nxSiteId)) {
										Map<String, Object> newMap = new HashMap<>(processedSites.get(nxSiteId));
										site.forEach(
												  (key, value) -> newMap.merge(key, value, (v1, v2) -> (v1 != null && v1 != "" && v1 != "PLACEHOLDER") ? v1 : v2));
										processedSites.put(nxSiteId, newMap);
									}else {
										processedSites.put(nxSiteId, site);
									}
								}
								if(nxSiteIdZ != null) {
									if(processedSites.containsKey(nxSiteIdZ)) {
										Map<String, Object> newMap = new HashMap<>(processedSites.get(nxSiteIdZ));
										site.forEach(
												  (key, value) -> newMap.merge(key, value, (v1, v2) -> (v1 != null && v1 != "" && v1 != "PLACEHOLDER") ? v1 : v2));
										processedSites.put(nxSiteIdZ, newMap);
									}else {
										processedSites.put(nxSiteIdZ, site);
									}
								}
								/*Set<String> siteIds = new HashSet<String>(){{
									if(site.get("nxSiteId") != null)
										add(String.valueOf(site.get("nxSiteId")));
									if(site.get("nxSiteIdZ") != null)
										add(String.valueOf(site.get("nxSiteIdZ")));
								}};
								if(!processedSites.containsAll(siteIds)){
									sites.add(site);
									processedSites.addAll(siteIds);
								}*/
								
							}
							
						} catch (IOException e) {
							e.getMessage();
						}
					}
				}
			}
		}
		sites.addAll(processedSites.values());
		processedSites = null;
		siteAddressJson = inrQualifyService.getSiteAddressJson(sites);
		siteAddressJson = siteAddressJson.replaceAll("\\|", "").replaceAll("~", "");
		return siteAddressJson;
	}
	
	public List<Object> getIglooSiteAddress(Long solutionId, Map<String, Map<String, Object>> processedSites) {
		logger.info("getIglooSiteAddress");
		List<NxAccessPricingData> nxAccessPricingDatas = nxAccessPricingDataRepository.findByNxSolIdAndMpStatusAndIncludeInd(solutionId);
		List<Object> iglooSites = new ArrayList<Object>();
		Map<String, String> countryMap = new HashMap<String, String>();
		if(!CollectionUtils.isEmpty(nxAccessPricingDatas)) {
			nxAccessPricingDatas.stream().forEach(nxAccessPricingData -> {
				long nxSiteId = 0;
				String siteRefID = "";
				String reqIlecSwc = null;
				String iglooIdIA = "";
				String tokenIdEthernet = "";
				String intermediateJson = nxAccessPricingData.getIntermediateJson();
				if(null != intermediateJson) {						
					JsonNode design = JacksonUtil.toJsonNode(intermediateJson);
					if(null != nxAccessPricingData.getNxSiteId()) {
						nxSiteId = nxAccessPricingData.getNxSiteId();
					} else {
						nxSiteId = design.at("/nxSiteId").isNull() || design.at("/nxSiteId").isMissingNode() ? 0 : design.at("/nxSiteId").asLong();
					}
					nxAccessPricingData.setIntermediateJson(design.toString());
					try {
						reqIlecSwc = design.at("/reqIlecSwc").isNull() || design.at("/reqIlecSwc").isMissingNode() ? "" : design.at("/reqIlecSwc").asText();
						if(null != nxAccessPricingData.getSiteRefId()) {
							siteRefID = nxAccessPricingData.getSiteRefId();
						} else {
							siteRefID = design.at("/siteRefID").isNull() || design.at("/siteRefID").isMissingNode() ? "" : design.at("/siteRefID").asText();
						}
					} catch (Exception e) {
						siteRefID = "";
						logger.error("Error : While getting siteRefID : {} - {}", e.getMessage(), nxAccessPricingData.getNxAccessPriceId());
					}
					
					iglooIdIA = (!design.at("/serialNumber").isMissingNode() && !design.at("/serialNumber").isNull()) ? design.at("/serialNumber").asText() : "";
					tokenIdEthernet = (!design.at("/ethToken").isMissingNode() && !design.at("/ethToken").isNull()) ? design.at("/ethToken").asText() : "";
				}
				if(!processedSites.containsKey(String.valueOf(nxSiteId))) {
					Map<String, Object> map = new HashMap<>();
					String postalCode = (nxAccessPricingData.getCustPostalcode() != null) ? nxAccessPricingData.getCustPostalcode() : ((nxAccessPricingData.getReqZipCode() != null) ? nxAccessPricingData.getReqZipCode() : "");
					String country = (nxAccessPricingData.getCustCountry() != null) ? nxAccessPricingData.getCustCountry() : ((nxAccessPricingData.getReqCountry() != null) ? nxAccessPricingData.getReqCountry() : "");
					String state = (nxAccessPricingData.getCustState() != null) ? nxAccessPricingData.getCustState() : ((nxAccessPricingData.getReqState() != null) ? nxAccessPricingData.getReqState() : "");
					String city = (nxAccessPricingData.getCustCity() != null) ? nxAccessPricingData.getCustCity() : ((nxAccessPricingData.getReqCity() != null) ? nxAccessPricingData.getReqCity() : "");
					String address = (nxAccessPricingData.getCustAddr1() != null) ? nxAccessPricingData.getCustAddr1() : ((nxAccessPricingData.getReqStreetAddress() != null) ? nxAccessPricingData.getReqStreetAddress() : "");
					String countryIsoCode =nxMpDealRepository.getCountryCodeByCountryIsoCode(country); 
					map.put("nxSiteId", nxSiteId);
					map.put("name", ((null != siteRefID && !siteRefID.isEmpty()) ? siteRefID : "nx-"+nxSiteId));
					map.put("addressLine", address);
					map.put("city", city);
					map.put("state", state);
					map.put("postalCode", postalCode);
					map.put("country",country);
						
					if(!StringUtils.isEmpty(countryIsoCode)) {
						
						map.put("address", String.join(" ", address, city, state, postalCode, countryIsoCode));
						
					}else {
					
						map.put("address", String.join(" ", address, city, state, postalCode, country));
					
					}
				
					map.put("validationStatus", CommonConstants.VALID);
					map.put("siteInfoSource", StringConstants.FLOW_TYPE_IGLOO);
					String quoteType=nxAccessPricingData.getQuoteType();
					logger.info("Custom Quote :::: ", quoteType);
					if(null != countryIsoCode && (countryIsoCode.trim().equalsIgnoreCase("US") || countryIsoCode.trim().equalsIgnoreCase("United states"))  ) {
						if(quoteType.equals("Custom Quote")) {
							map.put("swcCLLI", "No CLLI (OAPI)");
						}else {
							map.put("swcCLLI", (reqIlecSwc != null) ? reqIlecSwc : "");
						}
						map.put("popClli", (nxAccessPricingData.getAttEthPop() != null) ? nxAccessPricingData.getAttEthPop() : "");
						map.put("accessSpeed", (nxAccessPricingData.getAccessBandwidth() != null) ? nxAccessPricingData.getAccessBandwidth()+" Mbps" :"");
						map.put("tokenIdEthernet", tokenIdEthernet);
					} else {
						map.put("popClli", (nxAccessPricingData.getClli() != null) ? nxAccessPricingData.getClli() : "");
						try {
							Float accessSpeed = (nxAccessPricingData.getAccessBandwidth() != null) ? (float) (nxAccessPricingData.getAccessBandwidth() / 1000f) : null;
							map.put("accessSpeed",(nxAccessPricingData.getAccessBandwidth() != null) ? accessSpeed + " Mbps": "");
						} catch (Exception e) {
							map.put("accessSpeed", "");
						}
						map.put("iglooIdIA", iglooIdIA);
					}
					map.put("supplierName", (nxAccessPricingData.getSupplierName() != null) ? nxAccessPricingData.getSupplierName() : "");
					map.put("vendorZone", (nxAccessPricingData.getVendorZoneCode() != null) ? nxAccessPricingData.getVendorZoneCode() : "");
					map.put("CircuitID",(nxAccessPricingData.getCircuitId() != null)?getCircuitIdWithoutSpace(nxAccessPricingData.getCircuitId()) : "");
					iglooSites.add(map);
					String nxSiteIdval = map.get("nxSiteId") != null ? String.valueOf(map.get("nxSiteId")) : null;
					if(nxSiteIdval!=null) {
						processedSites.put(nxSiteIdval, map);
					}
				}

			});
			logger.info("save getIglooSiteAddress");
			nxAccessPricingDataRepository.saveAll(nxAccessPricingDatas);
			logger.info("save getIglooSiteAddress");
			countryMap.clear();
		}
		logger.info("end getIglooSiteAddress");
		return iglooSites;
	}
	
	protected String getupdateTransactionCSRTJSON(Map<Object, Object> updateCleanTransactionCleanSaveRequestMap)
			throws JsonProcessingException {

		ObjectMapper obj = new ObjectMapper();
		obj.enable(SerializationFeature.INDENT_OUTPUT);
		String solutionDataJsonBlock;
		solutionDataJsonBlock = obj.writeValueAsString(updateCleanTransactionCleanSaveRequestMap);
		return solutionDataJsonBlock;
	}

	public void persistNxSolutoinDetail(String solutionDataJsonBlock, Long nxTxnId, String sourceSystem,
			Long siteRefId) {

		logger.info("Service Log :: [Nexxus [MyPrice - Info] ::  persistTONXMPSOLUTIONDETAILSTABLE Invoked");
		NxMpSolutionDetails nxMpSolutionDetails = nxMpSolutionDetailsRepository.findByNxTxnId(nxTxnId);
		if (null == nxMpSolutionDetails)
			nxMpSolutionDetails = new NxMpSolutionDetails();
		nxMpSolutionDetails.setNxTxnId(nxTxnId);
		nxMpSolutionDetails.setSourceSystem(sourceSystem);
		nxMpSolutionDetails.setSolutionData(solutionDataJsonBlock);
		nxMpSolutionDetails.setActiveYN(CommonConstants.ACTIVE_Y);
		nxMpSolutionDetails.setSiteRefId(siteRefId);
		nxMpSolutionDetailsRepository.save(nxMpSolutionDetails);

		logger.info("Service Log :: [Nexxus [MyPrice - Info] ::  persistTONXMPSOLUTIONDETAILSTABLE Destroyed");
	}

	public NxMpSiteDictionary persistNxMpSiteDictionary(Long nxTxnId, String siteBlockJson, String sourceSystem)
			throws JsonProcessingException {

		logger.info("Service Log :: [Nexxus [MyPrice - Info] ::  persistToNXMPSITEDICTIONARYTABLE Invoked");

		NxMpSiteDictionary nxMpSiteDictionary = nxMpSiteDictionaryRepository.findByNxTxnId(nxTxnId);
		if (null == nxMpSiteDictionary)
			nxMpSiteDictionary = new NxMpSiteDictionary();
		nxMpSiteDictionary.setNxTxnId(nxTxnId);
		nxMpSiteDictionary.setSourceSystem(sourceSystem);
		if (siteBlockJson != null) {
			nxMpSiteDictionary.setSiteAddress("{ \"siteAddress\" :  "
					+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(siteBlockJson) + "}");
		}
		nxMpSiteDictionary.setActiveYN(CommonConstants.ACTIVE_Y);
		nxMpSiteDictionaryRepository.saveAndFlush(nxMpSiteDictionary);

		logger.info("Service Log :: [Nexxus [MyPrice - Info] ::  persistToNXMPSITEDICTIONARYTABLE Destroyed");

		return nxMpSiteDictionary;
	}

	public Map<String, Object> createTransaction(Long nxSolutionId, Long nxTxnId, String offer, FalloutDetailsRequest request) {
		logger.info("Entering createTransaction() method");
		CreateTransactionResponse createTransactionRes = null;
		Long nxAuditId = null;
		Map<String, Object> response = new HashMap<String, Object>();
		NxMpDeal nxMpDeal = new NxMpDeal();
		if (nxTxnId != null) {
			nxMpDeal.setNxTxnId(nxTxnId);
			nxMpDeal.setModifiedDate(new Date());
		} else {
			nxMpDeal.setCreatedDate(new Date());
		}

		try {
			nxMpDeal.setOfferId(offer);
			nxMpDeal.setSolutionId(nxSolutionId);
			nxMpDeal.setActiveYN(CommonConstants.ACTIVE_Y);
			response = createTransactionService.callCreateTrans();
			String createResponse = response.get(MyPriceConstants.RESPONSE_DATA) != null
					? (String) response.get(MyPriceConstants.RESPONSE_DATA)
					: null;
			int code = response.get(MyPriceConstants.RESPONSE_CODE) != null
					? (int) response.get(MyPriceConstants.RESPONSE_CODE)
					: null;
			if (code == CommonConstants.SUCCESS_CODE) {
				if (null != createResponse && !createResponse.isEmpty()) {
					createTransactionRes = (CreateTransactionResponse) restClient.processResult(createResponse,
							CreateTransactionResponse.class);
				}
				if (null != createTransactionRes) {
					logger.info("Create Transaction is succeeded for the solution id {}", nxSolutionId,
							" and the myprice transaction is {}", createTransactionRes.getMyPriceTransacId());
					nxMpDeal.setDealID(createTransactionRes.getDealID());
					nxMpDeal.setRevision(createTransactionRes.getRevision());
					nxMpDeal.setTransactionId(createTransactionRes.getMyPriceTransacId());
					nxMpDeal.setVersion(createTransactionRes.getVersion());
					nxMpDeal.setDealStatus(CommonConstants.CREATED);
					nxMpDeal.setNxMpStatusInd(StringConstants.CONSTANT_Y);
					nxMpDeal = nxMpDealRepository.saveAndFlush(nxMpDeal);
					createTransactionRes.setNxTransacId(nxMpDeal.getNxTxnId());
					createTransactionRes.setOfferName(nxMpDeal.getOfferId());
					createTransactionRes.setPriceScenarioId(0L);
					if (null != nxMpDeal.getPriceScenarioId()) {
						createTransactionRes.setPriceScenarioId(nxMpDeal.getPriceScenarioId());
					}
					createTransactionRes.setSuccess(true);
					// audit
					if (null != nxTxnId) {
						NxDesignAudit nxDesignAudit = nxDesignAuditRepository.findByNxRefIdAndTransaction(nxSolutionId,
								MyPriceConstants.AUDIT_CREATE);
						nxAuditId = nxDesignAudit.getNxAuditId();
						myPriceTransactionUtil.updateNxDesignAuditStatus(CommonConstants.SUCCESS,
								MyPriceConstants.AUDIT_CREATE, nxAuditId);
					} else {
						nxAuditId = myPriceTransactionUtil.saveNxDesignAudit(nxSolutionId, null, null,
								CommonConstants.SUCCESS, MyPriceConstants.AUDIT_CREATE);
					}
					response.put(MyPriceConstants.RESPONSE_STATUS, true);
				}
			} else {
				logger.info("Create Transaction is failed for the solution id {}", nxSolutionId);
				nxMpDeal.setNxMpStatusInd(StringConstants.CONSTANT_N);
				nxMpDeal.setDealStatus(CommonConstants.FAILED);
				nxMpDeal = nxMpDealRepository.save(nxMpDeal);
				createTransactionRes = new CreateTransactionResponse();
				createTransactionRes.setNxTransacId(nxMpDeal.getNxTxnId());
				createTransactionRes.setSuccess(false);
				// audit
				if (null != nxTxnId) {
					NxDesignAudit nxDesignAudit = nxDesignAuditRepository.findByNxRefIdAndTransaction(nxSolutionId,
							MyPriceConstants.AUDIT_CREATE);
					nxAuditId = nxDesignAudit.getNxAuditId();
					myPriceTransactionUtil.updateNxDesignAuditStatus(CommonConstants.FAILURE,
							MyPriceConstants.AUDIT_CREATE, nxAuditId);
				} else {
					nxAuditId = myPriceTransactionUtil.saveNxDesignAudit(nxSolutionId, null, null,
							CommonConstants.FAILURE, MyPriceConstants.AUDIT_CREATE);
				}
				response.put(MyPriceConstants.RESPONSE_STATUS, false);
			}
		} catch (SalesBusinessException e) {
			logger.info("Create Transaction is failed for the solution id {}", nxSolutionId);
			nxMpDeal.setNxMpStatusInd(StringConstants.CONSTANT_N);
			nxMpDeal = nxMpDealRepository.save(nxMpDeal);
			createTransactionRes = new CreateTransactionResponse();
			createTransactionRes.setNxTransacId(nxMpDeal.getNxTxnId());
			createTransactionRes.setSuccess(false);
			response.put(MyPriceConstants.RESPONSE_CODE, e.getHttpErrorCode());
			response.put(MyPriceConstants.RESPONSE_MSG, e.getMessage());
			response.put(MyPriceConstants.RESPONSE_STATUS, false);
			logger.error("exception occured in Myprice createTraction call {}", e.getMessage());

			// audit
			if (null != nxTxnId) {
				NxDesignAudit nxDesignAudit = nxDesignAuditRepository.findByNxRefIdAndTransaction(nxSolutionId,
						MyPriceConstants.AUDIT_CREATE);
				nxAuditId = nxDesignAudit.getNxAuditId();
				myPriceTransactionUtil.updateNxDesignAuditStatus(CommonConstants.FAILURE, MyPriceConstants.AUDIT_CREATE,
						nxAuditId);
			} else {
				nxAuditId = myPriceTransactionUtil.saveNxDesignAudit(nxSolutionId, null, null, CommonConstants.FAILURE,
						MyPriceConstants.AUDIT_CREATE);
			}
		}

		response.put("createTransactionRes", createTransactionRes);
		response.put(MyPriceConstants.NX_AUDIT_ID, nxAuditId);
		logger.info("Existing createTransaction() method");
		return response;
	}

	public Set<Object> getValuesFromRequest(Object request, String path) {
		if (request != null) {
			TypeRef<List<Object>> mapType = new TypeRef<List<Object>>() {
			};
			List<Object> results = jsonPathUtil.search(request, path, mapType);
			if (CollectionUtils.isEmpty(results)) {
				return null;
			} else {
				return new HashSet<Object>(results);
			}
		}
		return null;
	}

	public NxDesign saveNxDesign(NxSolutionDetail nxSolutionDetail, JsonNode design, Map<String, Object> paramMap) {
		String productName = (String) paramMap.get(MyPriceConstants.PRODUCT_NAME);
		String portId = paramMap.get(FmoConstants.PORT_ID) != null
				? ((String) paramMap.get(FmoConstants.PORT_ID)).trim()
				: null;

		if (!StringUtils.isEmpty(portId) && null != portId) {
			NxDesign nxDesign = nxDesignRepository.findByAsrItemIdAndNxSolutionDetail(portId, nxSolutionDetail);
			if (null == nxDesign) {
				nxDesign = new NxDesign();
				nxDesign.setAsrItemId(portId);
				nxDesign.setNxSolutionDetail(nxSolutionDetail);
				nxDesign.setBundleCd(productName);
				nxDesign.setCountry(design.path("country").asText());
				nxDesign.setSiteId(Long.valueOf(design.path("siteId").asText()));
				nxDesign.setStatus("N"); // May need to change once reconfiguration changes comes in
			} else {
				nxDesign.setModifedDate(new Date());
			}

			NxDesignDetails nxDesignDetails;
			if (nxDesign.getNxDesignDetails() == null || nxDesign.getNxDesignDetails().isEmpty()) {
				nxDesignDetails = new NxDesignDetails();
				nxDesignDetails.setDesignData(design.toString());
				nxDesign.addNxDesignDetails(nxDesignDetails);
			} else {
				// changes will be required in case of resiliency.
				nxDesignDetails = nxDesign.getNxDesignDetails().get(0);

				nxDesign.getNxDesignDetails().get(0).setDesignData(design.toString());
			}
			nxDesignDetails.setModifedDate(new Date());
			nxMpRepositoryService.saveNxDesignDatas(nxDesign);
			return nxDesign;
		}
		return null;
	}

	public void submitFMOToMyPrice(NxSolutionDetail nxSolutionDetail, NxRequestDetails requestDetails,
			NxOutputFileModel model, Map<String, Object> requestMap) throws JSONException, JsonProcessingException  {
		if (null != requestDetails && (STATUS_CONSTANTS.SUCCESS.getValue() == requestDetails.getStatus()
				|| STATUS_CONSTANTS.FALLOUT_IGNORED.getValue() == requestDetails.getStatus())) {
			if (null != model && null != model.getMpOutputJson()) {
				String outputData = model.getMpOutputJson();

				JsonNode request = null;
				try {
					request = mapper.readTree(outputData);
				} catch (IOException e) {
					logger.info("Exception in submitFMOToMyPrice while converting to json {}",
							requestDetails.getNxReqId());
				}

				Map<String, Object> saveDesignMap = new HashMap<String, Object>();

				String erateInd = requestMap.containsKey(FmoConstants.ERATE_IND)
						? requestMap.get(FmoConstants.ERATE_IND).toString()
						: null;
				logger.info("submitFMOToMyPrice() erateInd : {}", erateInd);
				JsonNode offers = request.at("/solution/offers");
				List<String> restProducts = new ArrayList<String>();
				Map<String, String> nxSiteIdUniqueIdToUsocIdMap = new HashMap<>();
				Map<String, String> nxSiteIdUniqueIdToPortIdMap = new HashMap<>();
				Map<String, NxDesignDetails> nxSiteIdUniqueIdToNxDesignDetailsMap = new HashMap<>();
				Map<String, List<NxDesignDetails>> nxSiteIdToNxDesignDetailsListMap = new HashMap<>();
				for (JsonNode offerElement : offers) {
					// map holding key as accessTye and value as criteria
					Map<String, List<String>> accessTypeDataMap = configAndUpdateProcessingFmoService
							.getDataMapFromLookup(MyPriceConstants.FMO_ACCESS_PRODUCT_NAME_DATASET);
					saveDesignMap.put(MyPriceConstants.FMO_ACCESS_PRODUCT_NAME_DATA_MAP, accessTypeDataMap);
					String offerId = offerElement.path("offerId").asText();

					String offerName = null;
					if (!StringUtils.isEmpty(offerId)) {
						offerName = nxMpRepositoryService.getOfferNameByOfferId(Integer.parseInt(offerId));
					}
					offerName = configAndUpdateProcessingFmoService.getBvoipOfferName(offerName);
					saveDesignMap.put(MyPriceConstants.OFFER_NAME, offerName);
					JsonNode site = offerElement.path("site");
					
					if (!isRESTEnabled(offerName, MyPriceConstants.SOURCE_FMO)) {
						for (JsonNode siteElement : site) {
							String isLineItemPicked = siteElement.at("/isLineItemPicked").asText();
							if (org.apache.commons.lang.StringUtils.isNotEmpty(isLineItemPicked)
									&& "Y".equals(isLineItemPicked)) {
								String siteId = siteElement.at("/siteId").asText();
								saveDesignMap.put(FmoConstants.SITE_ID, siteId);
								JsonNode siteDesign = siteElement.path("design");
								for (JsonNode siteDesignElement : siteDesign) {
									ObjectNode newSite = (ObjectNode) siteElement.deepCopy();
									newSite.remove("design");
									newSite.withArray("design").add(siteDesignElement);
									saveNxDesignFMO(nxSolutionDetail, newSite, saveDesignMap);
								}
							}

						} 
					} else {
						restProducts.add(offerName);
						String usocFieldName = nxMyPriceRepositoryServce
								.getDescDataFromLookup(FmoConstants.FMO_DESIGN_USOC_FIELDS).get(offerName);
						Map<String, ObjectNode> nxKeyIdToData = new HashMap<>();
						for (JsonNode siteElement : site) {
							String isLineItemPicked = siteElement.at("/isLineItemPicked").asText();
							if ("Y".equals(isLineItemPicked)) {
								boolean siteLevelNxKeyId = siteElement.has("nxKeyId");
								updateUsocIdMap(siteElement, nxSiteIdUniqueIdToUsocIdMap, usocFieldName);
								String nxKeyId = null;
								String nxSiteId = siteElement.path("nxSiteId").asText();
								if (siteLevelNxKeyId) {
									nxKeyId = siteElement.path("nxKeyId").asText();
									if (!nxKeyIdToData.containsKey(nxKeyId)) {
										ObjectNode objectNode = mapper.createObjectNode();
										objectNode.put("nxKeyId", nxKeyId);
										nxKeyIdToData.put(nxKeyId, objectNode);
									}
									nxKeyIdToData.get(nxKeyId).withArray("site").add(siteElement);
								} else {
									ObjectNode siteCopy = (ObjectNode) siteElement.deepCopy();
									siteCopy.remove("design");
									JsonNode siteDesign = siteElement.path("design");
									Map<String, ObjectNode> nxKeyIdToSite = new HashMap<>();
									for (JsonNode siteDesignElement : siteDesign) {
										nxKeyId = siteDesignElement.path("nxKeyId").asText();
										String portId = siteDesignElement.path("portId").asText();
										updatePortIdMap(nxSiteId, portId, siteDesignElement,
												nxSiteIdUniqueIdToPortIdMap);
										if (!nxKeyIdToSite.containsKey(nxKeyId)) {
											nxKeyIdToSite.put(nxKeyId, siteCopy.deepCopy());
										}
										nxKeyIdToSite.get(nxKeyId).withArray("design").add(siteDesignElement);
									}
									nxKeyIdToSite.forEach((k, v) -> {
										if (!nxKeyIdToData.containsKey(k)) {
											ObjectNode objectNode = mapper.createObjectNode();
											objectNode.put("nxKeyId", k);
											nxKeyIdToData.put(k, objectNode);
										}
										nxKeyIdToData.get(k).withArray("site").add(v);
									});
								}
							}
						}
						saveNxDesignFMO(nxSolutionDetail, nxKeyIdToData, saveDesignMap,
								nxSiteIdUniqueIdToNxDesignDetailsMap, nxSiteIdToNxDesignDetailsListMap);
					}
					saveDesignMap.clear();
				}
				// Add Logic to call my price configure and price update api's
				List<NxDesign> nxDesigns = nxDesignRepository.findByNxSolutionDetail(nxSolutionDetail);
				List<NxDesign> restDesigns = nxDesigns.stream().filter(n -> restProducts.contains(n.getBundleCd())).collect(Collectors.toList());
				List<NxDesign> soapDesigns = nxDesigns.stream().filter(n -> !restProducts.contains(n.getBundleCd())).collect(Collectors.toList());
				restProducts.clear();
				NxMpDeal nxMpDeal = configAndUpdateProcessingFmoService
						.getDealBySolutionId(nxSolutionDetail.getNxSolutionId());
				boolean result=false;
				Map<String, Object> responseMap = new HashMap<String, Object>();
				if(CollectionUtils.isNotEmpty(soapDesigns)) {
					Map<String, Object> dataMap = new HashMap<String, Object>();
					dataMap.put(MyPriceConstants.MP_TRANSACTION_ID, nxMpDeal.getTransactionId());
					dataMap.put(MyPriceConstants.NX_TRANSACTION_ID, nxMpDeal.getNxTxnId());
					dataMap.put(MyPriceConstants.CONFIG_DESIGN_RESPONSE, new ConfigureResponse());
					dataMap.put(MyPriceConstants.CONFIG_RESPONSE_PROCESSING_DATA,
							new HashMap<String, Map<String, Object>>());
					dataMap.put(StringConstants.PRICE_SCENARIO_ID, nxMpDeal.getPriceScenarioId());
					dataMap.put(MyPriceConstants.FLOW_TYPE, MyPriceConstants.SOURCE_FMO);
					int i=0;
					
					for (NxDesign design : soapDesigns) {
						dataMap.put(FmoConstants.ERATE_IND, erateInd);
						if (requestMap.containsKey(InrConstants.REQUEST_META_DATA_KEY)) {
							dataMap.put(InrConstants.REQUEST_META_DATA_KEY,
									requestMap.get(InrConstants.REQUEST_META_DATA_KEY));
						}
						Boolean isLastDesign=myPriceTransactionUtil.isLastDesign(i++, nxDesigns);
						dataMap.put(MyPriceConstants.IS_LAST_DESIGN, isLastDesign);
						responseMap = configAndUpdateProcessingFmoService.callConfigSolutionAndDesign(design, dataMap);
						result = responseMap.containsKey(MyPriceConstants.RESPONSE_STATUS)
								? (boolean) responseMap.get(MyPriceConstants.RESPONSE_STATUS)
								: false;
						//Terminate the calling MpConfig API process if any site got failed		
						if(!result) {
							break;
						}
					}
					// saving configDesign response for all sites
					Map<String, Object> configResponseMap = configDesignHelperService.processConfigDesignResponse(dataMap);
					responseMap.putAll(configResponseMap);

					result = responseMap.containsKey(MyPriceConstants.RESPONSE_STATUS)
							? (boolean) responseMap.get(MyPriceConstants.RESPONSE_STATUS)
							: false;

					if (result) {
						Map<String, Object> requestPriceMap = new HashMap<String, Object>();
						if (requestMap.containsKey(InrConstants.REQUEST_META_DATA_KEY)) {
							requestPriceMap.put(InrConstants.REQUEST_META_DATA_KEY,
									requestMap.get(InrConstants.REQUEST_META_DATA_KEY));
						}
						requestPriceMap.put(MyPriceConstants.SOURCE, MyPriceConstants.SOURCE_FMO);
						requestPriceMap.put(MyPriceConstants.NX_SOLIUTION_ID, nxSolutionDetail.getNxSolutionId());
						updateTransactionPricingFMOServiceImpl.updateTransactionPricingRequest(requestPriceMap);
						responseMap.put(MyPriceConstants.TRANSACTION_TYPE, MyPriceConstants.AUDIT_UPDATE_PR);
						result = responseMap.containsKey(MyPriceConstants.RESPONSE_STATUS)
								? (boolean) responseMap.get(MyPriceConstants.RESPONSE_STATUS)
								: false;
					}
				}
				if(CollectionUtils.isNotEmpty(restDesigns)) {
					Map<String, Object> restRequest = new HashMap<String, Object>();
					design:
					for (NxDesign design : restDesigns) {
						for(NxDesignDetails nxDesignDetail : design.getNxDesignDetails()) {
							restRequest.put(MyPriceConstants.OFFER_NAME, design.getBundleCd());
							if (requestMap.containsKey(InrConstants.REQUEST_META_DATA_KEY)) {
								restRequest.put(InrConstants.REQUEST_META_DATA_KEY,
										requestMap.get(InrConstants.REQUEST_META_DATA_KEY));
							}
							restRequest.put(MyPriceConstants.PRODUCT_TYPE, MyPriceConstants.SOURCE_FMO);
							restRequest.put(CustomJsonConstants.BS_ID, nxMpDeal.getTransactionId()!=null?
									Long.valueOf(nxMpDeal.getTransactionId()):0l);
							restRequest.put(CustomJsonConstants.DOCUMENT_ID, Long.valueOf(documentId));
							restRequest.put(MyPriceConstants.MP_TRANSACTION_ID, nxMpDeal.getTransactionId()!=null?
									Long.valueOf(nxMpDeal.getTransactionId()):0l); // to print in logs
							JSONObject designData = new JSONObject(nxDesignDetail.getDesignData());
							
							if(MyPriceConstants.BVoIP.equalsIgnoreCase(design.getBundleCd())) {
								Object country=nexxusJsonUtility.getValue(nxDesignDetail.getDesignData(), "$..site.*.country");
								if(null!=country && !"US".equalsIgnoreCase(country.toString())) {
									//for BVoIP International
									restRequest.put(MyPriceConstants.OFFER_NAME, MyPriceConstants.BVoIP_INTERNATIONAL);
								}
							}
							configAndUpdateRestProcessingService.callMpConfigAndUpdate(restRequest, designData.get("site").toString());
							result = restRequest.containsKey(MyPriceConstants.RESPONSE_STATUS) ? (boolean)restRequest.get(MyPriceConstants.RESPONSE_STATUS) : true;
							if(nxDesignDetail.getType() != null && result) {
								restRequest = new HashMap<String, Object>();
								restRequest.put(MyPriceConstants.OFFER_NAME, nxDesignDetail.getType());
								if (requestMap.containsKey(InrConstants.REQUEST_META_DATA_KEY)) {
									restRequest.put(InrConstants.REQUEST_META_DATA_KEY,
											requestMap.get(InrConstants.REQUEST_META_DATA_KEY));
								}
								restRequest.put(MyPriceConstants.PRODUCT_TYPE, MyPriceConstants.SOURCE_FMO);
								restRequest.put(CustomJsonConstants.BS_ID, nxMpDeal.getTransactionId()!=null?
										Long.valueOf(nxMpDeal.getTransactionId()):0l);
								restRequest.put(CustomJsonConstants.DOCUMENT_ID, Long.valueOf(documentId));
								restRequest.put(MyPriceConstants.MP_TRANSACTION_ID, nxMpDeal.getTransactionId()!=null?
										Long.valueOf(nxMpDeal.getTransactionId()):0l); 
								configAndUpdateRestProcessingService.callMpConfigAndUpdate(restRequest, designData.get("site").toString());
							}
							designData = null;
							if((restRequest.containsKey(MyPriceConstants.RESPONSE_STATUS) && !(Boolean)restRequest.get(MyPriceConstants.RESPONSE_STATUS))
									|| (restRequest.containsKey(CustomJsonConstants.CONFIG_BOM_ERROR) && restRequest.get(CustomJsonConstants.CONFIG_BOM_ERROR) != null && (Boolean)restRequest.get(CustomJsonConstants.CONFIG_BOM_ERROR))
									|| (restRequest.containsKey(CustomJsonConstants.SITE_CONFIG_ERROR) && restRequest.get(CustomJsonConstants.SITE_CONFIG_ERROR) != null && (Boolean)restRequest.get(CustomJsonConstants.SITE_CONFIG_ERROR))) {
								result = false;	
								nxDesignDetail.setStatus(MyPriceConstants.REST_API_FAILED);
								nxDesignDetail.setRestResponseError(processINRtoMP.getErrorDetails(restRequest).toString());
								break design;
							}else {
								nxDesignDetail.setStatus(MyPriceConstants.REST_API_SUCCEED);
								result = true;
							}
						}
					}
					
					this.processConfigSolDesignResponse(requestMap, nxMpDeal.getNxTxnId(),
							nxMpDeal.getTransactionId(), nxSiteIdUniqueIdToPortIdMap, nxSiteIdUniqueIdToUsocIdMap,
							nxSiteIdUniqueIdToNxDesignDetailsMap, nxSiteIdToNxDesignDetailsListMap);
					if(result) {
						Map<String, Object> requestPriceMap = new HashMap<String, Object>();
						if (requestMap.containsKey(InrConstants.REQUEST_META_DATA_KEY)) {
							requestPriceMap.put(InrConstants.REQUEST_META_DATA_KEY,
									requestMap.get(InrConstants.REQUEST_META_DATA_KEY));
						}
						requestPriceMap.put(MyPriceConstants.SOURCE, MyPriceConstants.SOURCE_FMO);
						requestPriceMap.put(MyPriceConstants.NX_SOLIUTION_ID, nxSolutionDetail.getNxSolutionId());
						updatePricingRestFmo.updateTransactionPricingRequest(requestPriceMap);
						responseMap.put(MyPriceConstants.TRANSACTION_TYPE, MyPriceConstants.AUDIT_UPDATE_PR);
						result = requestPriceMap.containsKey(MyPriceConstants.RESPONSE_STATUS)
								? (boolean) requestPriceMap.get(MyPriceConstants.RESPONSE_STATUS)
										: false;
					}
					
					nxDesignRepository.saveAll(restDesigns);
				}
				
				if (null!=nxMpDeal) {
					if (result) {
						nxMpDeal.setDealStatus(CommonConstants.SUBMITTED);
						myPriceTransactionUtil.sendDmaapEvents(nxMpDeal, nxSolutionDetail,
								com.att.sales.nexxus.common.CommonConstants.SUBMITTED);
					} else {
						nxMpDeal.setNxMpStatusInd(StringConstants.CONSTANT_N);
						nxMpDeal.setDealStatus(CommonConstants.FAILED);
						if (responseMap.containsKey(MyPriceConstants.MP_API_ERROR)
								&& (Boolean) responseMap.get(MyPriceConstants.MP_API_ERROR)) {
							responseMap.remove(MyPriceConstants.RESPONSE_DATA);
							responseMap.put(MyPriceConstants.RESPONSE_CODE,
									com.att.sales.nexxus.constant.MessageConstants.UPLOAD_MP_FAILED);
							responseMap.put(MyPriceConstants.RESPONSE_MSG,
									com.att.sales.nexxus.constant.MessageConstants.UPLOAD_MP_FAILED_MSG);
							myPriceTransactionUtil.sendDmaapEvents(nxMpDeal, nxSolutionDetail,
									com.att.sales.nexxus.common.CommonConstants.FAILED, responseMap);
						} else {
							myPriceTransactionUtil.sendDmaapEvents(nxMpDeal, nxSolutionDetail,
									com.att.sales.nexxus.common.CommonConstants.FAILED);
						}

					}
					nxMpDeal.setModifiedDate(new Date());
					nxMpDeal = nxMpDealRepository.saveAndFlush(nxMpDeal);
					mailServiceImpl.prepareMyPriceDealSubmissionRequest(nxMpDeal);
				}

			}
		}
	}

	protected void updatePortIdMap(String nxSiteId, String portId, JsonNode designElement,
			Map<String, String> nxSiteIdUniqueIdToPortIdMap) {
		List<JsonNode> uniqueIds = designElement.findValues("uniqueId");
		for (JsonNode n : uniqueIds) {
			String uniqueId = n.asText();
			nxSiteIdUniqueIdToPortIdMap.put(nxSiteId + uniqueId, portId);
		}
	}

	protected void updateUsocIdMap(JsonNode siteElement, Map<String, String> nxSiteIdUniqueIdToUsocIdMap, String usocFieldName) {
		String nxSiteId = siteElement.path("nxSiteId").asText();
		updateUsocIdMap(nxSiteId, siteElement, nxSiteIdUniqueIdToUsocIdMap, usocFieldName);
	}

	protected void updateUsocIdMap(String nxSiteId, JsonNode node,
			Map<String, String> nxSiteIdUniqueIdToUsocIdMap, String usocFieldName) {
		switch (node.getNodeType()) {
		case ARRAY:
			for (JsonNode element : node) {
				updateUsocIdMap(nxSiteId, element, nxSiteIdUniqueIdToUsocIdMap, usocFieldName);
			}
			break;
		case OBJECT:
			if (node.has("uniqueId")) {
				String key = nxSiteId + node.path("uniqueId").asText();
				String value = node.path(usocFieldName).asText();
				nxSiteIdUniqueIdToUsocIdMap.put(key, value);
			}
			for (JsonNode element : node) {
				updateUsocIdMap(nxSiteId, element, nxSiteIdUniqueIdToUsocIdMap, usocFieldName);
			}
			break;
		default:
			break;
		}
	}
	
	protected boolean isRESTEnabled(String offerName, String flowType) {
		List<NxLookupData> restProductDetails = nxLookupDataRepository
				.findByItemIdAndDatasetAndCriteriaAndDesc(offerName, MyPriceConstants.REST_PRODUCTS, StringConstants.CONSTANT_Y, flowType);
		if (CollectionUtils.isNotEmpty(restProductDetails)) {
			return true;
		}
		return false;
	}
	
	protected void saveNxDesignFMO(NxSolutionDetail nxSolutionDetail, Map<String, ObjectNode> nxKeyIdToData,
			Map<String, Object> paramMap, Map<String, NxDesignDetails> nxSiteIdUniqueIdToNxDesignDetailsMap,
			Map<String, List<NxDesignDetails>> nxSiteIdToNxDesignDetailsListMap) {
		String offerName = (String) paramMap.get(MyPriceConstants.OFFER_NAME);
		nxKeyIdToData.forEach((k, v) -> {
			Map<String, String> nxSiteIdUniqueIdToUsocIdMap = new HashMap<>();
			Set<String> nxSiteIdSet = new HashSet<>();
			JsonNode siteArray = v.path("site");
			for (JsonNode siteElement : siteArray) {
				updateUsocIdMap(siteElement, nxSiteIdUniqueIdToUsocIdMap, "dummy");
				nxSiteIdSet.add(siteElement.path("nxSiteId").asText());
			}
			NxDesign nxDesign = nxDesignRepository.findByAsrItemIdAndNxSolutionDetail(k, nxSolutionDetail);
			if (null == nxDesign) {
				nxDesign = new NxDesign();
				nxDesign.setAsrItemId(k);
				nxDesign.setNxSolutionDetail(nxSolutionDetail);
				nxDesign.setBundleCd(
						configAndUpdateProcessingFmoService.getOfferNameForNxDesign(Arrays.asList(offerName)));
				nxDesign.setCountry(
						v.at("/site/0/country").asText().isEmpty() ? null : v.at("/site/0/country").asText());
				nxDesign.setStatus("N"); // May need to change once reconfiguration changes comes in
			} else {
				nxDesign.setBundleCd(configAndUpdateProcessingFmoService
						.getOfferNameForNxDesign(Arrays.asList(offerName, nxDesign.getBundleCd())));
				nxDesign.setModifedDate(new Date());
			}
			if (nxDesign.getNxDesignDetails() == null || nxDesign.getNxDesignDetails().isEmpty()) {
				NxDesignDetails nxDesignDetails = new NxDesignDetails();
				nxDesignDetails.setType(this.findTypeFromNxKeyId(k));
				nxDesignDetails.setDesignData(v.toString());
				nxDesignDetails.setProductName(offerName);
				nxDesignDetails.setCreatedDate(new Date());
				nxDesign.addNxDesignDetails(nxDesignDetails);
				nxSiteIdUniqueIdToUsocIdMap.keySet()
						.forEach(key -> nxSiteIdUniqueIdToNxDesignDetailsMap.put(key, nxDesignDetails));
				nxSiteIdSet.forEach(key -> {
					if (!nxSiteIdToNxDesignDetailsListMap.containsKey(key)) {
						nxSiteIdToNxDesignDetailsListMap.put(key, new ArrayList<>());
					}
					nxSiteIdToNxDesignDetailsListMap.get(key).add(nxDesignDetails);
				});
			} else {
				NxDesignDetails nxDesignDetails = nxDesign.getNxDesignDetails().get(0);
				nxDesignDetails.setModifedDate(new Date());
				nxDesignDetails.setDesignData(v.toString());
//				nxDesign.addNxDesignDetails(nxDesignDetails); // xy3208 test if this line causes issue
				nxSiteIdUniqueIdToUsocIdMap.keySet()
						.forEach(key -> nxSiteIdUniqueIdToNxDesignDetailsMap.put(key, nxDesignDetails));
				nxSiteIdSet.forEach(key -> {
					if (!nxSiteIdToNxDesignDetailsListMap.containsKey(key)) {
						nxSiteIdToNxDesignDetailsListMap.put(key, new ArrayList<>());
					}
					nxSiteIdToNxDesignDetailsListMap.get(key).add(nxDesignDetails);
				});
			}
			nxMpRepositoryService.saveNxDesignDatas(nxDesign);
		});
	}
	
	protected String findTypeFromNxKeyId(String nxKeyId) {
		String[] elements = nxKeyId.split("\\$");
		for (String e : elements) {
			if (e.contains("/")) {
				return e;
			}
		}
		return null;
	}

	public NxDesign saveNxDesignFMO(NxSolutionDetail nxSolutionDetail, JsonNode design, Map<String, Object> paramMap) {
		String offerName = (String) paramMap.get(MyPriceConstants.OFFER_NAME);
		String siteId = paramMap.get(FmoConstants.SITE_ID) != null
				? ((String) paramMap.get(FmoConstants.SITE_ID)).trim()
				: null;
		List<String> designOfferList = new ArrayList<String>();
		;

		if (!StringUtils.isEmpty(siteId) && null != siteId) {
			NxDesign nxDesign = nxDesignRepository.findBySiteIdAndNxSolutionDetail(Long.valueOf(siteId),
					nxSolutionDetail);
			if (null == nxDesign) {
				designOfferList.clear();
				designOfferList.add(offerName);
				nxDesign = new NxDesign();
				nxDesign.setAsrItemId(siteId);
				nxDesign.setNxSolutionDetail(nxSolutionDetail);
				nxDesign.setBundleCd(configAndUpdateProcessingFmoService.getOfferNameForNxDesign(designOfferList));
				nxDesign.setCountry(design.path("country").asText());
				nxDesign.setSiteId(Long.valueOf(siteId));
				nxDesign.setStatus("N"); // May need to change once reconfiguration changes comes in
			} else {
				designOfferList.add(offerName);
				designOfferList.add(nxDesign.getBundleCd());
				nxDesign.setBundleCd(configAndUpdateProcessingFmoService.getOfferNameForNxDesign(designOfferList));
				nxDesign.setModifedDate(new Date());
			}
			String portId = design.at("/design/0/portId").asText();
			NxDesignDetails nxDesignDetails = null;
			if (nxDesign.getNxDesignDetails() == null || nxDesign.getNxDesignDetails().isEmpty()) {
				nxDesignDetails = new NxDesignDetails();
				nxDesignDetails.setComponentId(portId);
				nxDesignDetails.setType(configAndUpdateProcessingFmoService.getAccessType(design, offerName, paramMap));
				nxDesignDetails.setDesignData(design.toString());
				nxDesignDetails.setProductName(offerName);
				nxDesignDetails.setCreatedDate(new Date());
			} else {
				nxDesignDetails = this.getNxDesignDtllsByPortIdAndProductName(portId, offerName,
						nxDesign.getNxDesignDetails());
				if (nxDesignDetails == null) {
					nxDesignDetails = new NxDesignDetails();
					nxDesignDetails.setComponentId(portId);
					nxDesignDetails
							.setType(configAndUpdateProcessingFmoService.getAccessType(design, offerName, paramMap));
					nxDesignDetails.setDesignData(design.toString());
					nxDesignDetails.setProductName(offerName);
					nxDesignDetails.setCreatedDate(new Date());
				} else {
					nxDesignDetails.setModifedDate(new Date());
					nxDesignDetails.setDesignData(design.toString());
				}

			}
			nxDesign.addNxDesignDetails(nxDesignDetails);
			nxMpRepositoryService.saveNxDesignDatas(nxDesign);
			return nxDesign;
		}
		return null;
	}

	protected NxDesignDetails getNxDesignDtllsByPortIdAndProductName(String portId, String offerName,
			List<NxDesignDetails> nxDesignDetailslst) {
		if (org.apache.commons.lang3.StringUtils.isNotEmpty(portId)) {
			return nxDesignDetailslst.stream()
					.filter(x -> org.apache.commons.lang3.StringUtils.isNotEmpty(x.getComponentId())
							&& portId.equals(x.getComponentId()) && offerName.equals(x.getProductName()))
					.findAny().orElse(null);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected void persistDesignBasedSolutionCategory(NxValidationRules nxValidationRule, Map<String, Object> paramMap,
			List<NxOutputFileModel> nxOutputFileModels, NxSolutionDetail nxSolutionDetail,
			List<NxValidationRules> skipCktRules) {
		Long nxReqId = paramMap.containsKey(MyPriceConstants.NX_REQ_ID)
				? (Long) paramMap.get(MyPriceConstants.NX_REQ_ID)
				: 0L;
		logger.info("saveNxInrDesign() nxReqId :: " + nxReqId);
		String subDataPath = nxValidationRule.getSubDataPath();
		paramMap.put(MyPriceConstants.PRODUCT_NAME, nxValidationRule.getName());
		paramMap.put(MyPriceConstants.SUB_DATA, nxValidationRule.getSubData());
		paramMap.put(MyPriceConstants.SUB_DATA_PATH, nxValidationRule.getSubDataPath());
		paramMap.put(MyPriceConstants.JSON_DATA, nxOutputFileModels.get(0).getMpOutputJson());
		Object obj = nexxusJsonUtility.getValue(nxOutputFileModels.get(0).getMpOutputJson(),
				nxValidationRule.getValue());
		String circuitId = null;
		if (null != obj) {
			circuitId = String.valueOf(obj);
		}
		paramMap.put(MyPriceConstants.CIRCUIT_ID, circuitId);
		paramMap.put(MyPriceConstants.DESCRIPTION, nxValidationRule.getDescription());
		
		List<Long> restRequestIds=null;
		if(null!=paramMap.get("restMap")) {
			Map<Long, String> restRequests=(Map<Long, String>) paramMap.get("restMap");
			restRequestIds = restRequests.entrySet().stream().filter(n -> n.getValue().equalsIgnoreCase(StringConstants.CONSTANT_Y)).map(n -> n.getKey()).collect(Collectors.toList());
		}
		boolean isRestRequest=false;
		if(CollectionUtils.isNotEmpty(restRequestIds)) {
			isRestRequest=true;
		}
		try {
			if (subDataPath != null) {
				ObjectMapper mapper = new ObjectMapper();
				UsageRuleObj usageRuleObj = mapper.readValue(subDataPath, UsageRuleObj.class);
				List<UsageRule> usageRuleList = usageRuleObj.getUsageRule();
				String data = paramMap.containsKey(MyPriceConstants.JSON_DATA)
						&& paramMap.get(MyPriceConstants.JSON_DATA) != null
								? (String) paramMap.get(MyPriceConstants.JSON_DATA)
								: "";
				String productNameValue = (String) paramMap.get(MyPriceConstants.PRODUCT_NAME);
				String productName = "";
				Map<String, List<String>> productInfoMap = configAndUpdatePricingUtilInr
						.getConfigProdutMapFromLookup(MyPriceConstants.INR_MP_PRODUCT_DATASET);

				if (null != productInfoMap) {
					for (Map.Entry<String, List<String>> criteriaMap : productInfoMap.entrySet()) {
						List<String> criteria = criteriaMap.getValue();
						if (criteria != null && criteria.contains(productNameValue)) {
							productName = criteriaMap.getKey();
						}
					}
				}

				for (UsageRule usageRule : usageRuleList) {
					String jsonArray = usageRule.getJsonarray();
					String jsonDataPath = usageRule.getJsonDataPath();
					String jsonDataPathRule[] = jsonDataPath.split("###");
					String rulename=usageRule.getRuleName();
					for (String rule : jsonDataPathRule) {
						/*
						 * execute the validation path , and substitute the json data path for the value
						 */
						if (usageRule.getJsonValidationPath() != null) {
							List<Object> validationPathList = inrQualifyService.getCircuits(data,
									usageRule.getJsonValidationPath());
							if (null != validationPathList) {
								List<Object> distinctValidationPathList = validationPathList.stream().distinct()
										.collect(Collectors.toList());
								for (Object ob : distinctValidationPathList) {
									String newRule = rule.replace(usageRule.getJsonValidationKey(), ob.toString());
									List<Object> designDetailData = inrQualifyService.getCircuits(data, newRule);
									if(rule.contains("VTNS_ONENETINTL") && designDetailData!= null && !designDetailData.isEmpty() )
									{
										productName = MyPriceConstants.ONENET;
									}
									if (null != designDetailData) {
										JsonNode usageData = mapper.valueToTree(designDetailData);
										boolean designDataMissing = skipCktProcessing(skipCktRules, usageData,
												paramMap);
										if (!designDataMissing) {
											String usageCategory=ob.toString().concat("_").concat(nxValidationRule.getFlowType());
											List<NxInrDesignDetails> resultList=
													nxInrDesignDetailsRepository.findByNxSolutionIdAndUsagecategory(nxSolutionDetail.getNxSolutionId(),usageCategory,productName);
											NxInrDesign nxInrDesign=null;
											if(CollectionUtils.isNotEmpty(resultList)) {
												NxInrDesignDetails nxInrDesignDetails =  resultList.get(0);
												JSONObject jsonobject = new JSONObject(nxInrDesignDetails.getDesignData());
												JSONArray usageDetailarray = jsonobject.getJSONArray(jsonArray);
												//append the value
											    List<Object> appendedListdata = new ArrayList<Object>();
										        if(usageDetailarray!=null) {
											        for (int i=0;i<usageDetailarray.length();i++){
											        	appendedListdata.add(usageDetailarray.get(i));
											        }  
										        } 
										        appendedListdata.addAll(designDetailData);
												JSONObject designData = new JSONObject();
												designData.put(jsonArray, appendedListdata);
												nxInrDesignDetails.setDesignData(designData.toString());
												nxInrDesignDetails.setNxReqId(nxReqId);
												nxInrDesignDetailsRepository.saveAndFlush(nxInrDesignDetails);
											}else {
												nxInrDesign = new NxInrDesign();
												nxInrDesign.setNxSolutionId(nxSolutionDetail.getNxSolutionId());
												nxInrDesign.setCircuitId(circuitId);
												nxInrDesign.setActiveYN(StringConstants.CONSTANT_Y);
												nxInrDesign.setStatus(MyPriceConstants.API_NOT_INVOKED);
												NxInrDesignDetails designDetail = new NxInrDesignDetails();
												JSONObject designData = new JSONObject();
												designData.put(jsonArray, designDetailData);
												designDetail.setDesignData(designData.toString());
												designDetail.setProduct(productName);
												designDetail.setActiveYN(StringConstants.CONSTANT_Y);
												designDetail.setNxReqId(nxReqId);
												designDetail.setUsageCategory(usageCategory);
												if(isRestRequest) {
													nxInrDesign.setStatus(MyPriceConstants.REST_API_NOT_INVOKED);
													designDetail.setStatus(MyPriceConstants.REST_API_NOT_INVOKED);
												}
												nxInrDesign.addNxInrDesignDetails(designDetail);
												nxInrDesignRepository.saveAndFlush(nxInrDesign);
											}	
										}
									}
								}
							}
						} else {
							List<Object> designDetailData = inrQualifyService.getCircuits(data, rule);
							if(rule.contains("VTNS_ONENETINTL") && designDetailData!= null && !designDetailData.isEmpty() )
							{
								productName = MyPriceConstants.ONENET;
							}
							if (null != designDetailData) {
								JsonNode usageData = mapper.valueToTree(designDetailData);
								boolean designDataMissing = skipCktProcessing(skipCktRules, usageData, paramMap);
								if (!designDataMissing) {
									String usageCategory=rulename.concat("_").concat(nxValidationRule.getFlowType());
									List<NxInrDesignDetails> resultList=
											nxInrDesignDetailsRepository.findByNxSolutionIdAndUsagecategory(nxSolutionDetail.getNxSolutionId(),usageCategory,productName);
									NxInrDesign nxInrDesign=null;
									if(CollectionUtils.isNotEmpty(resultList)) {
										NxInrDesignDetails nxInrDesignDetails =  resultList.get(0);
										JSONObject jsonobject = new JSONObject(nxInrDesignDetails.getDesignData());
										JSONArray usageDetailarray = jsonobject.getJSONArray(jsonArray);
										//append the value
									    List<Object> appendedListdata = new ArrayList<Object>();  
								        if(usageDetailarray!=null) {
										    for (int i=0;i<usageDetailarray.length();i++){
									        	appendedListdata.add(usageDetailarray.get(i));
									        } 
									    }
								        appendedListdata.addAll(designDetailData);
										JSONObject designData = new JSONObject();
										designData.put(jsonArray, appendedListdata);
										nxInrDesignDetails.setDesignData(designData.toString());
										nxInrDesignDetails.setNxReqId(nxReqId);
										nxInrDesignDetailsRepository.saveAndFlush(nxInrDesignDetails);
									}else {
										nxInrDesign = new NxInrDesign();
										nxInrDesign.setNxSolutionId(nxSolutionDetail.getNxSolutionId());
										nxInrDesign.setCircuitId(circuitId);
										nxInrDesign.setActiveYN(StringConstants.CONSTANT_Y);
										nxInrDesign.setStatus(MyPriceConstants.API_NOT_INVOKED);
										NxInrDesignDetails nxInrDesignDetail = new NxInrDesignDetails();
										JSONObject designData = new JSONObject();
										designData.put(jsonArray, designDetailData);
										nxInrDesignDetail.setDesignData(designData.toString());
										nxInrDesignDetail.setProduct(productName);
										nxInrDesignDetail.setActiveYN(StringConstants.CONSTANT_Y);
										nxInrDesignDetail.setNxReqId(nxReqId);
										nxInrDesignDetail.setUsageCategory(usageCategory);
										if(isRestRequest) {
											nxInrDesign.setStatus(MyPriceConstants.REST_API_NOT_INVOKED);
											nxInrDesignDetail.setStatus(MyPriceConstants.REST_API_NOT_INVOKED);
										}
										nxInrDesign.addNxInrDesignDetails(nxInrDesignDetail);
										nxInrDesignRepository.saveAndFlush(nxInrDesign);
									}
									
								}
							}
						}
					}
				}
			}
		} catch (IOException | JSONException e) {
			logger.error("Exception occured during saving NxInrDesignDetails", e);

		}
	}
	
	/**
	 * mirror to ProcessPDtoMPRestUtil.processConfigSolDesignResponse
	 * @param nxSiteIdUniqueIdToNxDesignDetailsMap 
	 * @param nxSiteIdToNxDesignDetailsListMap 
	 * @param restRequest 
	 */
	public void processConfigSolDesignResponse(Map<String, Object> requestMap, Long nxTransactionId,
			String mpTransactionId, Map<String, String> nxSiteIdUniqueIdToPortIdMap,
			Map<String, String> nxSiteIdUniqueIdToUsocIdMap,
			Map<String, NxDesignDetails> nxSiteIdUniqueIdToNxDesignDetailsMap,
			Map<String, List<NxDesignDetails>> nxSiteIdToNxDesignDetailsListMap) {
		final String seperator = "\\s*,\\s*";
		Map<NxDesignDetails, Set<String>> nxDesignDetailsToNxSiteIdUniqueIdMap = new HashMap<>();
		nxSiteIdUniqueIdToNxDesignDetailsMap.forEach((k, v) -> {
			if (!nxDesignDetailsToNxSiteIdUniqueIdMap.containsKey(v)) {
				nxDesignDetailsToNxSiteIdUniqueIdMap.put(v, new HashSet<>());
			}
			nxDesignDetailsToNxSiteIdUniqueIdMap.get(v).add(k);
		});
		Set<String> nxSiteIdUniqueIdProcessed = new HashSet<>();
		boolean hasMore = false;
		Long offset = 0L;
		boolean firstDesignadded = false;
		GetTransactionLineResponse response = null;
		try {
			do {
				for (int i = 0; i < mypriceGetTranSactionLineRetryTimes && response == null; i++) {
					response = getTransactionLineServiceImpl.getTransactionLineConfigDesignSolution(mpTransactionId,
							null, offset, null);
				}
				if (response == null) {
					break;
				}
				hasMore = response.isHasMore();
				offset = offset + response.getLimit();

				Map<String, Map<String, List<NxMpDesignDocument>>> productLineDetailMap = new HashMap<>();
				Map<String, List<NxMpDesignDocument>> mpProductLineIdMap = null;
				List<NxMpDesignDocument> documentNumberdetailList = null;
				Map<String, String> mpSolutionIdMap = new HashMap<String, String>();
				if (response != null && CollectionUtils.isNotEmpty(response.getItems())) {
					List<GetTransactionLineItem> itemList = response.getItems();
					for (GetTransactionLineItem transLineItem : itemList) {
						String documentNumber = transLineItem.getDocumentNumber(); // _document_number
						String parentDocumentNumber = transLineItem.getParentDocNumber(); // _parent_doc_number
						String usocId = transLineItem.getUsocId(); // lii_uSOC_ql
						String lineBomId = transLineItem.getBomId(); // _line_bom_id
						String lineBomPartNumber = transLineItem.getBomPartNumber(); // _line_bom_part_number
						String lineBomParentId = transLineItem.getBomParentId(); // _line_bom_parent_id
						String parentlineItem = transLineItem.getParentLineitem(); // _parent_line_item
						String modelAsr = transLineItem.getModelAsr(); // wl_int_model_asr
						String asrItemVal = transLineItem.getAsrItemId(); // lii_asrID_ql
						String asrItemId = asrItemVal != null ? asrItemVal : (modelAsr != null ? modelAsr : null);

						String nxSiteIdval = transLineItem.getNxSiteId(); // lii_nxSiteId_ql
						String modelSites = transLineItem.getModelSites(); // wl_int_model_sites
						String externalKey = transLineItem.getExternalKey(); // lii_externalKey_ql
						String nxsiteId = modelSites != null ? modelSites
								: (nxSiteIdval != null ? nxSiteIdval : externalKey);

						String uniqueIds = transLineItem.getUniqueIds(); // wi_uniqueID_ql

						// for solution
						if ("BOM_Solution".equalsIgnoreCase(lineBomId)) {
							if (!productLineDetailMap.containsKey(documentNumber)) {
								productLineDetailMap.put(documentNumber,
										new HashMap<String, List<NxMpDesignDocument>>());
							}
						} else if ("Solution".equalsIgnoreCase(parentlineItem)
								|| "BOM_Solution".equalsIgnoreCase(lineBomParentId)) {
							mpProductLineIdMap = productLineDetailMap.get(parentDocumentNumber);
							if (!mpProductLineIdMap.containsKey(documentNumber)) {
								documentNumberdetailList = new ArrayList<>();
								NxMpDesignDocument nxMpDesignDocument = new NxMpDesignDocument();
								nxMpDesignDocument.setMpSolutionId(parentDocumentNumber);
								nxMpDesignDocument.setMpProductLineId(documentNumber);
								nxMpDesignDocument.setCreatedDate(new Date());
								nxMpDesignDocument.setActiveYN(com.att.sales.nexxus.common.CommonConstants.ACTIVE_Y);
								nxMpDesignDocument.setNxDesignId(null);
								nxMpDesignDocument.setNxTxnId(nxTransactionId);
								documentNumberdetailList.add(nxMpDesignDocument);
								mpProductLineIdMap.put(documentNumber, documentNumberdetailList);
								mpSolutionIdMap.put(parentDocumentNumber, documentNumber);

								firstDesignadded = false;
							}
						}

						// for design
						if (!("Solution".equalsIgnoreCase(lineBomPartNumber)
								|| "Solution".equalsIgnoreCase(parentlineItem)) && nxsiteId != null) {
							String[] nxSiteIds = nxsiteId.split(seperator);
							documentNumberdetailList = mpProductLineIdMap.get(parentDocumentNumber);
							for (String siteId : nxSiteIds) {
								String nxSiteIdUniqueId = siteId + uniqueIds;
								NxMpDesignDocument nxMpDesignDocument = null;
								if (!firstDesignadded) {
									nxMpDesignDocument = documentNumberdetailList.get(0);
									documentNumberdetailList.remove(0);
									firstDesignadded = true;
								} else {
									// entry of one document number assosciated with productline is there
									nxMpDesignDocument = new NxMpDesignDocument();
								}
								if (nxMpDesignDocument != null) {
									nxMpDesignDocument.setMpDocumentNumber(Long.valueOf(documentNumber));
									nxMpDesignDocument.setMpProductLineId(parentDocumentNumber);
									String mpSolutionIdVal = getKey(mpSolutionIdMap, parentDocumentNumber);
									nxMpDesignDocument.setMpSolutionId(mpSolutionIdVal);
									nxMpDesignDocument.setUsocId(nxSiteIdUniqueIdToUsocIdMap.get(nxSiteIdUniqueId));
									nxMpDesignDocument
											.setActiveYN(com.att.sales.nexxus.common.CommonConstants.ACTIVE_Y);
									nxMpDesignDocument.setCreatedDate(new Date());
									nxMpDesignDocument.setNxTxnId(nxTransactionId);
									if (nxSiteIdUniqueIdToNxDesignDetailsMap.containsKey(nxSiteIdUniqueId)) {
										nxMpDesignDocument.setNxDesignId(nxSiteIdUniqueIdToNxDesignDetailsMap
												.get(nxSiteIdUniqueId).getNxDesign().getNxDesignId());
										nxMpDesignDocument
												.setMpPartNumber(nxSiteIdUniqueIdToPortIdMap.get(nxSiteIdUniqueId));
									} else {
										NxDesignDetails nxDesignDetails = nxSiteIdToNxDesignDetailsListMap.get(siteId)
												.stream().filter(ndd -> ndd.getType() != null).findAny().orElse(null);
										if (nxDesignDetails != null) {
											nxMpDesignDocument
													.setNxDesignId(nxDesignDetails.getNxDesign().getNxDesignId());
											JsonNode designData = mapper.readTree(nxDesignDetails.getDesignData());
											Iterable<JsonNode> iterable = () -> designData.path("site").iterator();
											Stream<JsonNode> siteStream = StreamSupport.stream(iterable.spliterator(),
													false);
											String portId = siteStream
													.filter(node -> node.path("nxSiteId").asText().equals(siteId))
													.findAny().map(node -> node.at("/design/0/portId").asText())
													.orElse(null);
											nxMpDesignDocument.setMpPartNumber(portId);
										}
									}
									documentNumberdetailList.add(nxMpDesignDocument);
									nxSiteIdUniqueIdProcessed.add(nxSiteIdUniqueId);
									nxSiteIdUniqueIdToNxDesignDetailsMap.remove(nxSiteIdUniqueId);
								}
							}
						}
					}
				}
				// save the product line details in nx_mpDesignDocument
				List<NxMpDesignDocument> documentListToSave = null;
				if (MapUtils.isNotEmpty(productLineDetailMap)) {
					for (Map.Entry<String, Map<String, List<NxMpDesignDocument>>> mpsolutionId : productLineDetailMap
							.entrySet()) {
						Map<String, List<NxMpDesignDocument>> mpProductLineId = mpsolutionId.getValue();
						if (MapUtils.isNotEmpty(mpProductLineId)) {
							for (Map.Entry<String, List<NxMpDesignDocument>> mpProuctLineEntry : mpProductLineId
									.entrySet()) {
								documentListToSave = mpProuctLineEntry.getValue();
								if (CollectionUtils.isNotEmpty(documentListToSave)) {
									nxMpDesignDocumentRepo.saveAll(documentListToSave);
								}
							}
						}
					}
				}
			} while (hasMore);
		} catch (SalesBusinessException e) {
			logger.info("Error during getTransactionLineServiceImpl.getTransactionLineConfigDesignSolution {}", e);
		} catch (Exception e) {
			logger.info("Error during getTransactionLineServiceImpl.getTransactionLineConfigDesignSolution {}", e);
		}finally {
			nxSiteIdUniqueIdToNxDesignDetailsMap.forEach((k, v) -> {
				Set<String> all = nxDesignDetailsToNxSiteIdUniqueIdMap.get(v);
				if (nxSiteIdUniqueIdProcessed.stream().anyMatch(all::contains)) {
					v.setStatus(MyPriceConstants.REST_API_PARTIAL_SUCCEED);
				} else {
					v.setStatus(MyPriceConstants.REST_API_FAILED);
				}
				nxDesignDetailsRepository.saveAndFlush(v);
			});
		}
	}
	
	protected <K, V> K getKey(Map<K, V> map, V value) {
		if (MapUtils.isNotEmpty(map) && value != null) {
			return map.entrySet().stream().filter(entry -> value.equals(entry.getValue())).map(Map.Entry::getKey)
					.findFirst().get();
		}
		return null;
	}
	
	protected String getCircuitIdWithoutSpace(String circuitId) {
		return circuitId.replaceAll("\\s", "").replaceAll("\\.", "");
	}
	
	protected void removeJsonObjectFromPricedetails(JsonNode node, Set<String> filter) {
		List<JsonNode> priceDetails = node.findValues("priceDetails");
		if (CollectionUtils.isNotEmpty(priceDetails)) {
			for (JsonNode priceDetailsArray : priceDetails) {
				ArrayNode priceDetailsArrayNode = (ArrayNode) priceDetailsArray;
				for (int i = priceDetailsArrayNode.size() - 1; i >= 0; i--) {
					if (filter.contains(priceDetailsArrayNode.get(i).path("FALLOUTMATCHINGID").asText())) {
						priceDetailsArrayNode.remove(i);
					}
				}
			}
		}
	}
	 
}
