/**
 * 
 */
package com.att.sales.nexxus.myprice.transaction.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.att.aft.dme2.internal.google.common.base.Strings;
import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.common.MessageConstants;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.constant.TDDConstants;
import com.att.sales.nexxus.dao.model.NxDesign;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxMpDesignDocument;
import com.att.sales.nexxus.dao.model.NxMpSiteDictionary;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxDesignRepository;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxMpDesignDocumentRepository;
import com.att.sales.nexxus.dao.repository.NxMpPriceDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxMpSiteDictionaryRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.SalesMsDao;
import com.att.sales.nexxus.dmaap.mr.util.DmaapPublishEventsService;
import com.att.sales.nexxus.fallout.model.FalloutDetailsRequest;
import com.att.sales.nexxus.handlers.ConfigureDesignWSHandler;
import com.att.sales.nexxus.handlers.ConfigureSolnAndProductWSHandler;
import com.att.sales.nexxus.model.UpdateTransactionOverrideDocument;
import com.att.sales.nexxus.model.UpdateTransactionOverrideRequest;
import com.att.sales.nexxus.myprice.transaction.model.Clone;
import com.att.sales.nexxus.myprice.transaction.model.ConfigureSolnAndProductResponse;
import com.att.sales.nexxus.myprice.transaction.model.CreateTransactionResponse;
import com.att.sales.nexxus.rateletter.model.RateLetterStatusRequest;
import com.att.sales.nexxus.reteriveicb.model.Circuit;
import com.att.sales.nexxus.reteriveicb.model.DesignDetails;
import com.att.sales.nexxus.reteriveicb.model.NexxusMessage;
import com.att.sales.nexxus.reteriveicb.model.NexxusResponse;
import com.att.sales.nexxus.reteriveicb.model.Offer;
import com.att.sales.nexxus.reteriveicb.model.RetreiveICBPSPRequest;
import com.att.sales.nexxus.reteriveicb.model.Site;
import com.att.sales.nexxus.reteriveicb.model.Solution;
import com.att.sales.nexxus.reteriveicb.model.UDFBaseData;
import com.att.sales.nexxus.reteriveicb.model.ValidationIssues;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.att.sales.nexxus.service.PedSnsdServiceUtil;
import com.att.sales.nexxus.service.ProcessPDtoMPRestUtil;
import com.att.sales.nexxus.service.SubmitToMyPriceService;
import com.att.sales.nexxus.service.UpdateTransactionOverrideImpl;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.att.sales.nexxus.util.RestClientUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.TypeRef;
import java.util.concurrent.TimeUnit;
 

/**
 * @author ShruthiCJ
 *
 */
@Component
public class MyPriceTransactionUtil {

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(MyPriceTransactionUtil.class);
	
	@Autowired
	private SubmitToMyPriceService submitToMyPriceService;
	
	@Autowired
	private UpdateTransactionCleanSaveFMO updateTransactionCleanSaveFMO;

	@Autowired
	private CreateTransactionService createTransactionService;

	@Autowired
	private IUpdateTransaction iUpdateTransaction;

	@Autowired
	private ConfigureSolnAndProductWSHandler configureSolnAndProductWSHandler;
	
	@Autowired
	private ConfigureDesignWSHandler configureDesignWSHandler;
	
	@Autowired
	private UpdateTransactionPricingFMOServiceImpl updateTransactionPricingFMOServiceImpl;
	
	@Autowired
	private UpdateTransactionPricingServiceImpl updateTransactionPricingServiceImpl;
	
	@Autowired
	private NxDesignAuditRepository nxDesignAuditRepository;
	
	@Autowired
	private NxMpSiteDictionaryRepository nxMpSiteDictionaryRepository;
	
	@Autowired
	private PedSnsdServiceUtil pedSnsdServiceUtil;
	
	@Value("${myprice.username}")
	private String myPriceUserName;
	
	@Value("${myprice.password}")
	private String myPricePassword;
	
	@Value("${http.proxyHost}")
	private String httpProxyHost;
	
	@Value("${http.proxyPort}")
	private String httpProxyPort;
	
	@Value("${https.proxyHost}")
	private String httpsProxyHost;
	
	@Value("${https.proxyPort}")
	private String httpsProxyPort;
	
	@Value("${http.proxyUser}")
	private String proxyUser;
	
	@Value("${http.proxyPassword}")
	private String proxyPassword;
	
	@Value("${http.proxySet}")
	private String proxySet;
	
	@Autowired
	private JsonPathUtil jsonPathUtil;
	
	@Value("${ase.designModifiedInd.path}")
	private String aseDesignModifiedIndPath;
	
	@Value("${ase.designStatus.path}")
	private String aseDesignStatusPath;
	
	@Value("${ase.priceScenarioId.path}")
	private String asePriceScenarioIdPath;
	
	@Value("${ase.priceModifiedInd.path}")
	private String asePriceModifiedIndPath;
	
	@Value("${ase.priceModifiedInd.priceScenarioId.path}")
	private String asePriceModifiedIndPriceScenarioIdPath;
	
	@Value("${ade.priceScenarioId.path}")
	private String adePriceScenarioIdPath;
	
	@Value("${ade.active.priceScenarioId.path}")
	private String adeActivePriceScenarioIdPath;
	
	@Value("${ade.designModifiedInd.path}")
	private String adeDesignModifiedIndPath;
	
	@Value("${ade.designStatus.path}")
	private String adeDesignStatusPath;
	
	@Value("${ade.priceModifiedInd.path}")
	private String adePriceModifiedIndPath;
	
	@Value("${ade.priceModifiedInd.priceScenarioId.path}")
	private String adePriceModifiedIndPriceScenarioIdPath;
	
	@Value("${ade.new.siteId.path}")
	private String adeNewSiteIdPath;
	
	@Value("${eplswan.cleansave.attibutes.path}")
	private String eplswanCleansaveAttibutesPath;
	
	
	@Autowired
	private CopyTransactionServiceImpl copyTransactionServiceImpl;
	
	@Autowired
	private UpdateTransactionOverrideImpl updateTransactionOverrideImpl;
	
	@Autowired
	private NxMpDealRepository nxMpDealRepository;
	
	@Autowired
	private SalesMsDao salesMsDao;
	
	@Autowired
	private RemoveTransactionServiceLineImpl removeTransactionServiceLineImpl;
	
	@Autowired
	private NxMpDesignDocumentRepository designDocumentRepo;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private NxDesignRepository nxDesignRepository;
	
	@Autowired
	private NxMpSiteDictionaryRepository siteRepo;
	
	@Autowired
	private EntityManager em;
	
	@Autowired
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;
	
	@Autowired
	private DmaapPublishEventsService dmaapPublishEventsService;
	
	@Autowired
	private NxMpPriceDetailsRepository priceDetailsRepo;
	
	@Autowired
	private NexxusJsonUtility nexxusJsonUtility;
	
	@Autowired
	private RestClientUtil restClient;
	
	@Autowired
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	@Autowired
	private NxRequestDetailsRepository nxRequestDetailsRepository;
	
	@Autowired
	private ProcessPDtoMPRestUtil processPDtoMPRestUtil;
	
	@Autowired
	private UpdateTxnSiteUploadServiceImpl updateTxnSiteUploadServiceImpl;
	
	public Long saveNxDesignAudit(Long nxRefId, String nxSubRefId, RetreiveICBPSPRequest retreiveICBPSPRequest, String status, String transaction) {
		NxDesignAudit nxDesignAudit = nxDesignAuditRepository.findByNxRefIdAndNxSubRefIdAndTransaction(nxRefId, nxSubRefId,transaction);
		if(nxDesignAudit == null) {
			nxDesignAudit = new NxDesignAudit();
		}
		nxDesignAudit.setNxRefId(nxRefId);
		nxDesignAudit.setNxSubRefId(nxSubRefId);
		nxDesignAudit.setTransaction(transaction);
		try {
			if(retreiveICBPSPRequest != null) {
				if (ServiceMetaData.getRequestMetaData() != null) {
					Map<String, Object> requestMetaDataMap = new HashMap<>();
					ServiceMetaData.getRequestMetaData().forEach((key, value) -> requestMetaDataMap.put(key, value));
					nxDesignAudit.setData(requestMetaDataMap.toString());
				}
				//nxDesignAudit.setData(mapper.writeValueAsString(retreiveICBPSPRequest));
			}
				
		} catch (Exception e) {
			logger.error("saveNxDesignAudit :: Exception occured while converting retreiveICBPSPRequest to String",e.getCause());
		//	e.printStackTrace();
		}
		nxDesignAudit.setStatus(status);
		nxDesignAuditRepository.saveAndFlush(nxDesignAudit);
		return nxDesignAudit.getNxAuditId();
	}
	
	public void saveNxDesignAudit(Long nxRefId, String transaction) {
		NxDesignAudit nxDesignAudit =  new NxDesignAudit();
		nxDesignAudit.setNxRefId(nxRefId);
		nxDesignAudit.setTransaction(transaction);
		if (ServiceMetaData.getRequestMetaData() != null) {
			Map<String, Object> requestMetaDataMap = new HashMap<>();
			ServiceMetaData.getRequestMetaData().forEach((key, value) -> requestMetaDataMap.put(key, value));
			nxDesignAudit.setData(requestMetaDataMap.toString());
		}
		nxDesignAuditRepository.save(nxDesignAudit);
	}

	public void updateNxDesignAuditStatus(String status, String transType, long nxAuditId) {
		nxDesignAuditRepository.updateStatusByNxAuditId(status, transType, new Date(), nxAuditId);
	}
	
	public void updateNxMpDealMpInd(String nxMpStatusInd, Long nxTxnId) {
		nxMpDealRepository.updateNxMpStatusIndByNxTxnId(nxMpStatusInd, new Date(), nxTxnId);
	}
	public void updateMpDealStatusByNxTxnId(String dealStatus, Long nxTxnId) {
		nxMpDealRepository.updateMpDealStatusByNxTxnId(dealStatus, new Date(), nxTxnId);
		
	}
	
	public Map<String, Object> createAndUpdateTransc(RetreiveICBPSPRequest retreiveICBPSPRequest, NxSolutionDetail nxSolutionDetail,
			Map<String, Object> result, Long nxTxnId) {
		Long nxAuditId = null;
		String flowType = (String) result.get(MyPriceConstants.FLOW_TYPE);
		Map<String, Object> response = createTransactionService
			.createTransaction(retreiveICBPSPRequest, nxSolutionDetail, nxTxnId, flowType);
		CreateTransactionResponse createTransactionResponse = response.get("createTransactionRes") != null ? (CreateTransactionResponse) response.get("createTransactionRes") : null ;
		
		if (null == createTransactionResponse || !createTransactionResponse.getSuccess()) {
			logger.info("Create Transaction is failed for the solution id {}", nxSolutionDetail.getNxSolutionId());
			if(null != nxTxnId) {
				NxDesignAudit nxDesignAudit = nxDesignAuditRepository.findByNxRefIdAndTransaction(nxSolutionDetail.getNxSolutionId(), MyPriceConstants.AUDIT_CREATE);
				nxAuditId = nxDesignAudit.getNxAuditId();
				updateNxDesignAuditStatus(CommonConstants.FAILURE, MyPriceConstants.AUDIT_CREATE, nxAuditId);
			}else {
				nxAuditId = saveNxDesignAudit(nxSolutionDetail.getNxSolutionId(), String.valueOf(retreiveICBPSPRequest.getSolution().getPriceScenarioId()), retreiveICBPSPRequest, CommonConstants.FAILURE, MyPriceConstants.AUDIT_CREATE);
			}
			
			result.put(MyPriceConstants.RESPONSE_STATUS, false);
			result.put(MyPriceConstants.NX_AUDIT_ID, nxAuditId);
			return result;
		}
		if(null != nxTxnId) {
			NxDesignAudit nxDesignAudit = nxDesignAuditRepository.findByNxRefIdAndTransaction(nxSolutionDetail.getNxSolutionId(), MyPriceConstants.AUDIT_CREATE);
			nxAuditId = nxDesignAudit.getNxAuditId();
			updateNxDesignAuditStatus(CommonConstants.SUCCESS, MyPriceConstants.AUDIT_CREATE, nxAuditId);
		}else {
			nxAuditId = saveNxDesignAudit(nxSolutionDetail.getNxSolutionId(), String.valueOf(retreiveICBPSPRequest.getSolution().getPriceScenarioId()), retreiveICBPSPRequest, CommonConstants.SUCCESS, MyPriceConstants.AUDIT_CREATE);
		}
		logger.info("Create Transaction is succeeded for the solution id {}", nxSolutionDetail.getNxSolutionId()  + " and the myprice transaction is {}", createTransactionResponse.getMyPriceTransacId());
		result.put("createTransactionResponse", createTransactionResponse);
		result.put(MyPriceConstants.NX_AUDIT_ID, nxAuditId);
		
		// update transaction
		Map<String, Object> updateResponse = callUpdateCleanSave(retreiveICBPSPRequest, createTransactionResponse, nxAuditId, nxSolutionDetail, result);
		Boolean updateStatus =  updateResponse.containsKey(MyPriceConstants.RESPONSE_STATUS) ? (Boolean) updateResponse.get(MyPriceConstants.RESPONSE_STATUS) : false;
		if (!updateStatus) {
			logger.info("UpdateTransactionCleanSave is failed for the solution id {}", nxSolutionDetail.getNxSolutionId());
			result.put(MyPriceConstants.RESPONSE_STATUS, false);
			return result;
		}
		result.put("retreiveICBPSPRequest", updateResponse != null ? (RetreiveICBPSPRequest) updateResponse.get("retreiveICBPSPRequest") : null);
		logger.info("UpdateTransactionCleanSave is succeeded for the solution id {}", nxSolutionDetail.getNxSolutionId() ,"and the myprice transaction is {}", createTransactionResponse.getMyPriceTransacId());
		result.put(MyPriceConstants.RESPONSE_STATUS, true);
		return result;
	}

	public Map<String, Object> callUpdateCleanSave(RetreiveICBPSPRequest retreiveICBPSPRequest, CreateTransactionResponse createTransactionResponse,
			Long nxAuditId, NxSolutionDetail nxSolutionDetail, Map<String, Object> result){
		Map<String, Object> updateResponse = new HashMap<String, Object>();
		String flowType = (String) result.get(MyPriceConstants.FLOW_TYPE);
		try {
			if(MyPriceConstants.SOURCE_FMO.equalsIgnoreCase(flowType)) {
				updateResponse = updateTransactionCleanSaveFMO
						.updateTransactionCleanSave(retreiveICBPSPRequest, createTransactionResponse,result);
			}else {
				updateResponse = iUpdateTransaction
						.updateTransactionCleanSave(retreiveICBPSPRequest, createTransactionResponse,result);
			}
		} catch (SalesBusinessException e) {
			logger.error(e.toString());
			logger.info("UpdateTransactionCleanSave is failed for the MP transaction id {}", createTransactionResponse.getMyPriceTransacId());
			updateResponse.put(MyPriceConstants.RESPONSE_CODE, e.getHttpErrorCode());
			updateResponse.put(MyPriceConstants.RESPONSE_MSG, e.getMessage());
			updateResponse.put(MyPriceConstants.RESPONSE_STATUS, false);
			if (e.getMessage() != null && (e.getMessage().equalsIgnoreCase(MessageConstants.INVALID_OPTYID_OR_HRID) 
					|| e.getMessage().equalsIgnoreCase(MessageConstants.OPTYID_CALL_FAILLED))) {
				logger.error("OptyId call failled ", e);
				updateResponse.put(MyPriceConstants.RESPONSE_CODE,com.att.sales.nexxus.constant.MessageConstants.OPTY_CALL_FAILED);
				updateResponse.put(MyPriceConstants.RESPONSE_MSG, com.att.sales.nexxus.constant.MessageConstants.OPTY_CALL_FAILED_MSG);
			} 
			
		}
		Boolean updateStatus =  updateResponse.containsKey(MyPriceConstants.RESPONSE_STATUS) ? (Boolean) updateResponse.get(MyPriceConstants.RESPONSE_STATUS) : false;
		if (!updateStatus) {
			updateNxMpDealMpInd(StringConstants.CONSTANT_N, createTransactionResponse.getNxTransacId());
			updateNxDesignAuditStatus(CommonConstants.FAILURE, MyPriceConstants.AUDIT_UPDATE_CS, nxAuditId);
			NxMpDeal deal = nxMpDealRepository.findByNxTxnId(createTransactionResponse.getNxTransacId());
			sendDmaapEvents(deal, nxSolutionDetail, CommonConstants.FAILED, updateResponse);
			
		}else {
			updateNxDesignAuditStatus(CommonConstants.SUCCESS, MyPriceConstants.AUDIT_UPDATE_CS, nxAuditId);
		}
		return updateResponse;
	}
	public Map<String, Object> configAndUpdatePricing(CreateTransactionResponse response, JsonNode siteJson, NxDesign nxDesign,
			Map<String, Object> paramMap,String flowType) {
		String transactionType = paramMap.containsKey(StringConstants.TRANSACTION_TYPE) ? paramMap.get(StringConstants.TRANSACTION_TYPE).toString() : null;
		Map<String, Object> designMap = new HashMap<String, Object>();
		Map<String, Object> responseMap = new HashMap<String, Object>();
		boolean automationInd=paramMap.get(MyPriceConstants.AUTOMATION_IND)!=null?(boolean)paramMap.get(MyPriceConstants.AUTOMATION_IND):false;
		boolean result = false;
		String priceUpdate = paramMap.containsKey(MyPriceConstants.PRICE_UPDATE) ? paramMap.get(MyPriceConstants.PRICE_UPDATE).toString() : null;
		logger.info("configAndUpdatePricing() priceUpdate :==>> {} ", priceUpdate);
		String contractTerm = paramMap.containsKey(MyPriceConstants.CONTRACT_TERM) ? paramMap.get(MyPriceConstants.CONTRACT_TERM).toString() : null;
		if(StringConstants.TRANSACTION_TYPE_NEW.equalsIgnoreCase(transactionType)) {
			designMap.clear();
			designMap.put(MyPriceConstants.MP_TRANSACTION_ID, response.getMyPriceTransacId());
			designMap.put(MyPriceConstants.NX_TRANSACTION_ID, response.getNxTransacId());
			designMap.put(MyPriceConstants.OFFER_NAME, response.getOfferName());
			designMap.put(MyPriceConstants.NX_DESIGN, nxDesign);
			designMap.put(MyPriceConstants.NX_DESIGN_ID, nxDesign.getNxDesignId());
			responseMap = callConfigureSolutionProduct(designMap);
			result = responseMap.containsKey(MyPriceConstants.RESPONSE_STATUS) ? (boolean) responseMap.get(MyPriceConstants.RESPONSE_STATUS) : false;
			responseMap.put(MyPriceConstants.TRANSACTION_TYPE, MyPriceConstants.AUDIT_CONFIG_SOLN);
			if(result) {
				designMap.clear();
				designMap.put(MyPriceConstants.MP_TRANSACTION_ID, response.getMyPriceTransacId());
				designMap.put(MyPriceConstants.NX_TRANSACTION_ID, response.getNxTransacId());
				designMap.put(MyPriceConstants.OFFER_NAME, response.getOfferName());
				designMap.put(StringConstants.PRICE_SCENARIO_ID, response.getPriceScenarioId());
				designMap.put(MyPriceConstants.NX_DESIGN, nxDesign);
				designMap.put(MyPriceConstants.NX_DESIGN_ID, nxDesign.getNxDesignId());
				designMap.put(MyPriceConstants.AUTOMATION_IND, automationInd);
				responseMap = callConfigureDesign(designMap);
				responseMap.put(MyPriceConstants.TRANSACTION_TYPE, MyPriceConstants.AUDIT_CONFIG_DESIGN);

			}
			result = responseMap.containsKey(MyPriceConstants.RESPONSE_STATUS) ? (boolean) responseMap.get(MyPriceConstants.RESPONSE_STATUS) : false;
			if(result) {
				designMap.clear();
				designMap.put(MyPriceConstants.MP_TRANSACTION_ID, response.getMyPriceTransacId());
				designMap.put(MyPriceConstants.NX_TRANSACTION_ID, response.getNxTransacId());
				designMap.put(StringConstants.PRICE_SCENARIO_ID, response.getPriceScenarioId());
				designMap.put(MyPriceConstants.NX_DESIGN_ID, nxDesign.getNxDesignId());
				designMap.put(MyPriceConstants.OFFER_TYPE, flowType);
				designMap.put(MyPriceConstants.PRICE_UPDATE, priceUpdate);
				designMap.put(MyPriceConstants.CONTRACT_TERM, contractTerm);
				responseMap = callUpdatePricing(designMap);
				responseMap.put(MyPriceConstants.TRANSACTION_TYPE, MyPriceConstants.AUDIT_UPDATE_PR);
			}
		} else if(StringConstants.TRANSACTION_TYPE_EXISTING.equalsIgnoreCase(transactionType)) {
			String designModifiedInd = siteJson.path("designModifiedInd").asText();
			String designStatus = siteJson.path("designStatus").asText();
			
			designMap.put(MyPriceConstants.MP_TRANSACTION_ID, response.getMyPriceTransacId());
			designMap.put(MyPriceConstants.NX_TRANSACTION_ID, response.getNxTransacId());
			designMap.put(MyPriceConstants.OFFER_NAME, response.getOfferName());
			designMap.put(StringConstants.PRICE_SCENARIO_ID, response.getPriceScenarioId());
			designMap.put(MyPriceConstants.NX_DESIGN, nxDesign);
			designMap.put(MyPriceConstants.NX_DESIGN_ID, nxDesign.getNxDesignId());
			designMap.put(MyPriceConstants.OFFER_TYPE, flowType);
			designMap.put(MyPriceConstants.PRICE_UPDATE, priceUpdate);
			designMap.put(MyPriceConstants.CONTRACT_TERM, contractTerm);
			designMap.put(MyPriceConstants.AUTOMATION_IND, automationInd);
			
			// remove transaction for retrigger
			String isReconfiure = (String) paramMap.get("IS_RECONFIGURE");
			if(StringConstants.CONSTANT_N.equalsIgnoreCase(isReconfiure)) {
				logger.info("Calling remove transaction for deleting solution and product lines for retrigger {}", response.getNxTransacId());
				removeTransactionServiceLineImpl(designMap);
			}
			
			
			// Add site scenario
			if (StringConstants.DESIGN_NEW.equalsIgnoreCase(designStatus)) {
				responseMap = callConfigureSolutionProduct(designMap);
				responseMap.put(MyPriceConstants.TRANSACTION_TYPE, MyPriceConstants.AUDIT_CONFIG_SOLN_RC);
				result = responseMap.containsKey(MyPriceConstants.RESPONSE_STATUS) ? (boolean) responseMap.get(MyPriceConstants.RESPONSE_STATUS) : false;
				 if (result) {
					 responseMap = callConfigureDesign(designMap);
					 responseMap.put(MyPriceConstants.TRANSACTION_TYPE, MyPriceConstants.AUDIT_CONFIG_DESIGN_RC);
				 }
				 result = responseMap.containsKey(MyPriceConstants.RESPONSE_STATUS) ? (boolean) responseMap.get(MyPriceConstants.RESPONSE_STATUS) : false;
				 if (result) {
					 responseMap = callUpdatePricing(designMap);
					 responseMap.put(MyPriceConstants.TRANSACTION_TYPE, MyPriceConstants.AUDIT_UPDATE_PR_RC);
				 }
			}
			//design cancel / delete scenario
			else if (StringConstants.DESIGN_CANCEL.equalsIgnoreCase(designStatus)) {
				result = removeTransactionServiceLineImpl(designMap);
				responseMap.put(MyPriceConstants.RESPONSE_STATUS, result);
				if(result) {
					// removing site details
					List<Long> nxSiteIds = (List<Long>) paramMap.get(MyPriceConstants.CURRENT_NX_SITE_ID);
					Long nxTxnId = response.getNxTransacId();
					logger.info("Calling deleteSiteFromSiteDic for nxTxnId : {}", nxTxnId + " and nxSiteId : " + nxSiteIds.toString());
					deleteSiteFromSiteDic(nxTxnId, nxSiteIds);
				}
			} 
			//if designModifiedInd is Y or empty then we need to check designStatus<br>
			else if ((designModifiedInd.isEmpty() || StringConstants.CONSTANT_Y.equalsIgnoreCase(designModifiedInd)) && StringConstants.DESIGN_UPDATE.equalsIgnoreCase(designStatus)) {
				//design update scenario. We will remove existing document numbers and call config solution, design and price update
				result = removeTransactionServiceLineImpl(designMap);
				responseMap.put(MyPriceConstants.RESPONSE_STATUS, result);
				if(result) {
					responseMap = callConfigureSolutionProduct(designMap);
					responseMap.put(MyPriceConstants.TRANSACTION_TYPE, MyPriceConstants.AUDIT_CONFIG_SOLN_RC);
					result = responseMap.containsKey(MyPriceConstants.RESPONSE_STATUS) ? (boolean) responseMap.get(MyPriceConstants.RESPONSE_STATUS) : false;
					if (result) {
						responseMap = callConfigureDesign(designMap);
						responseMap.put(MyPriceConstants.TRANSACTION_TYPE, MyPriceConstants.AUDIT_CONFIG_DESIGN_RC);
						result = responseMap.containsKey(MyPriceConstants.RESPONSE_STATUS) ? (boolean) responseMap.get(MyPriceConstants.RESPONSE_STATUS) : false;
						if (result) {
							responseMap = callUpdatePricing(designMap);
							responseMap.put(MyPriceConstants.TRANSACTION_TYPE, MyPriceConstants.AUDIT_UPDATE_PR_RC);
						}
						result = responseMap.containsKey(MyPriceConstants.RESPONSE_STATUS) ? (boolean) responseMap.get(MyPriceConstants.RESPONSE_STATUS) : false;
						designMap.remove(MyPriceConstants.TRANSACTION_FLOW_TYPE);
					}
				}
			}
			// Price update scenario
			else {
				designMap.put(StringConstants.TRANSACTION_UPDATE, StringConstants.RECONFIGURE);
				responseMap = callUpdatePricing(designMap);
				designMap.remove(StringConstants.TRANSACTION_UPDATE);
				responseMap.put(MyPriceConstants.TRANSACTION_TYPE, MyPriceConstants.AUDIT_UPDATE_PR_RC);
			} 
		}
		return responseMap;
	}
	
	/**
	 * @param designMap
	 * designMap.put(MyPriceConstants.MP_TRANSACTION_ID, response.getMyPriceTransacId());
	 * designMap.put(MyPriceConstants.NX_TRANSACTION_ID, response.getNxTransacId());
	 * designMap.put(MyPriceConstants.OFFER_NAME, response.getOfferName());
	 * designMap.put(MyPriceConstants.NX_DESIGN_ID, nxDesign.getNxDesignId());
	 * @return
	 */
	public Map<String, Object> callConfigureSolutionProduct(Map<String, Object> designMap) {
		logger.info("Start -- callConfigureSolutionProduct");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		ConfigureSolnAndProductResponse cspResponse;
		try {
			cspResponse = configureSolnAndProductWSHandler.initiateConfigSolnAndProdWebService(designMap);
			responseMap.put("cspResponse", cspResponse);
			responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
			copyTime(designMap, responseMap);
		} catch (SalesBusinessException e) {
			logger.info("Error during callConfigureSolutionProduct {}", e.getMessage());
			responseMap.put(MyPriceConstants.RESPONSE_STATUS, false);
			responseMap.put(MyPriceConstants.RESPONSE_MSG, e.getMessage());
			responseMap.put(MyPriceConstants.RESPONSE_CODE, e.getHttpErrorCode());
			copyTime(designMap, responseMap);
			return responseMap;
		}
		if (null == cspResponse) {
			prepareResponseMap(designMap, responseMap);
			responseMap.put(MyPriceConstants.RESPONSE_STATUS, false);
			return responseMap;
		}

		logger.info("End -- callConfigureSolutionProduct");
		return responseMap;
	}
	
	/**
	 * @param designMap
	 * designMap.put(MyPriceConstants.MP_TRANSACTION_ID, response.getMyPriceTransacId());
	 * designMap.put(MyPriceConstants.OFFER_NAME, response.getOfferName());
	 * designMap.put(MyPriceConstants.NX_TRANSACTION_ID, response.getNxTransacId());
	 * designMap.put(MyPriceConstants.NX_DESIGN, nxDesign);
	 * @return
	 */
	public Map<String, Object> callConfigureDesign(Map<String, Object> designMap) {
		logger.info("Start -- callConfigureDesign");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			Boolean isSuccessful = configureDesignWSHandler.initiateConfigDesignWebService(designMap);
			if(!isSuccessful) {
				prepareResponseMap(designMap, responseMap);
				return responseMap;
			}
		} catch (SalesBusinessException e) {
			logger.info("Error during callConfigureDesign {}", e.getMessage());
			responseMap.put(MyPriceConstants.RESPONSE_STATUS, false);
			responseMap.put(MyPriceConstants.RESPONSE_MSG, e.getMessage());
			responseMap.put(MyPriceConstants.RESPONSE_CODE, e.getHttpErrorCode());
			copyTime(designMap, responseMap);
			return responseMap;
		}
		responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
		copyTime(designMap, responseMap);
		logger.info("End -- callConfigureDesign");
		return responseMap;
	}
	public void copyTime(Map<String, Object> sourceMap, Map<String, Object> destinationMap){
		String timeKey = sourceMap.keySet().stream().filter(key -> key.startsWith("soapWs_ProcessTime_")).collect(Collectors.joining());
		if(timeKey != null) {
			destinationMap.put(timeKey, (sourceMap.containsKey(timeKey) && sourceMap.get(timeKey) != null) ? (Long) sourceMap.get(timeKey) : null);
			sourceMap.remove(timeKey);
		}
	}
	/**
	 * @param designMap
	 * designMap.put(StringConstants.MY_PRICE_TRANS_ID, response.getMyPriceTransacId());
	 * designMap.put(MyPriceConstants.NX_DESIGN_ID, nxDesign.getNxDesignId());
	 * designMap.put(MyPriceConstants.NX_TRANSACTION_ID, response.getNxTransacId());
	 * designMap.put(StringConstants.PRICE_SCENARIO_ID, response.getPriceScenarioId());
	 * @return
	 */
	public Map<String, Object> callUpdatePricing(Map<String, Object> designMap) {
		logger.info("Start -- callUpdatePricing");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			
			String source = designMap.containsKey(MyPriceConstants.SOURCE) ? designMap.get(MyPriceConstants.SOURCE).toString() : null;
			
			if(Optional.ofNullable(source).isPresent() && source.equalsIgnoreCase(MyPriceConstants.SOURCE_FMO)) {
				updateTransactionPricingFMOServiceImpl.updateTransactionPricingRequest(designMap);
			} else {
				updateTransactionPricingServiceImpl.updateTransactionPricingRequest(designMap);
			}
			boolean status = (designMap.containsKey(MyPriceConstants.RESPONSE_STATUS) && designMap.get(MyPriceConstants.RESPONSE_STATUS) != null) ? (boolean) designMap.get(MyPriceConstants.RESPONSE_STATUS) : false;
			prepareResponseMap(designMap, responseMap);
			if(status) {
				responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
			}else {
				responseMap.put(MyPriceConstants.RESPONSE_STATUS, false);
			}
		} catch (SalesBusinessException e) {
			logger.info("Error during callUpdatePricing {}", e.getMessage());
			responseMap.put(MyPriceConstants.RESPONSE_STATUS, false);
			responseMap.put(MyPriceConstants.RESPONSE_MSG, e.getMessage());
			responseMap.put(MyPriceConstants.RESPONSE_CODE, e.getHttpErrorCode());
			responseMap.put(MyPriceConstants.NEXXUS_API_ERROR, true);
			responseMap.put(MyPriceConstants.UPDATE_PRICING_FAILED, true);
			return responseMap;
		}
		logger.info("End -- callUpdatePricing");
		return responseMap;
	}
	
	public void prepareResponseMap(Map<String, Object> sourceMap, Map<String, Object> destinationMap) {
		destinationMap.put(MyPriceConstants.RESPONSE_STATUS, (sourceMap.containsKey(MyPriceConstants.RESPONSE_STATUS) && sourceMap.get(MyPriceConstants.RESPONSE_STATUS) != null) ? (boolean) sourceMap.get(MyPriceConstants.RESPONSE_STATUS) : false);
		sourceMap.remove(MyPriceConstants.RESPONSE_STATUS);
		destinationMap.put(MyPriceConstants.RESPONSE_DATA, (sourceMap.containsKey(MyPriceConstants.RESPONSE_DATA) && sourceMap.get(MyPriceConstants.RESPONSE_DATA) != null) ? (String) sourceMap.get(MyPriceConstants.RESPONSE_DATA) : null);
		sourceMap.remove(MyPriceConstants.RESPONSE_DATA);
		destinationMap.put(MyPriceConstants.RESPONSE_MSG, (sourceMap.containsKey(MyPriceConstants.RESPONSE_MSG) && sourceMap.get(MyPriceConstants.RESPONSE_MSG) != null) ? (String) sourceMap.get(MyPriceConstants.RESPONSE_MSG) : null);
		sourceMap.remove(MyPriceConstants.RESPONSE_MSG);
		destinationMap.put(MyPriceConstants.RESPONSE_CODE, (sourceMap.containsKey(MyPriceConstants.RESPONSE_CODE) && sourceMap.get(MyPriceConstants.RESPONSE_CODE) != null) ? (int) sourceMap.get(MyPriceConstants.RESPONSE_CODE) : 0);
		sourceMap.remove(MyPriceConstants.RESPONSE_CODE);
		destinationMap.put(MyPriceConstants.MP_API_ERROR, (sourceMap.containsKey(MyPriceConstants.MP_API_ERROR) && sourceMap.get(MyPriceConstants.MP_API_ERROR) != null) ? (Boolean) sourceMap.get(MyPriceConstants.MP_API_ERROR) : false);
		sourceMap.remove(MyPriceConstants.MP_API_ERROR);
		destinationMap.put(MyPriceConstants.NEXXUS_API_ERROR, (sourceMap.containsKey(MyPriceConstants.NEXXUS_API_ERROR) && sourceMap.get(MyPriceConstants.NEXXUS_API_ERROR) != null) ? (Boolean) sourceMap.get(MyPriceConstants.NEXXUS_API_ERROR) : false);
		sourceMap.remove(MyPriceConstants.NEXXUS_API_ERROR);
		copyTime(sourceMap, destinationMap);
	}
	
	public boolean removeTransactionServiceLineImpl(Map<String, Object> designMap) {
		logger.info("Start -- removeTransactionServiceLineImpl");
		try {
			Long nxTxnId = designMap.containsKey(MyPriceConstants.NX_TRANSACTION_ID) ? (long) designMap.get(MyPriceConstants.NX_TRANSACTION_ID) : 0L;
			Long nxDesignId = designMap.containsKey(MyPriceConstants.NX_DESIGN_ID) ? (long) designMap.get(MyPriceConstants.NX_DESIGN_ID) : 0L;
			
			List<NxMpDesignDocument> productLineIds = designDocumentRepo.getMpProductLineIdByNxTxnIdAndNxDesignId(nxTxnId, nxDesignId);
			if(CollectionUtils.isNotEmpty(productLineIds)) {
				Set<Long> documentIds = new HashSet<Long>();
				for(NxMpDesignDocument designDoc : productLineIds) {
					documentIds.add(Long.valueOf(designDoc.getMpSolutionId()));
					documentIds.add(designDoc.getMpDocumentNumber());
					documentIds.add(Long.valueOf(designDoc.getMpProductLineId()));
				}
				designMap.put(MyPriceConstants.DOCUMENT_ID, documentIds);
				removeTransactionServiceLineImpl.removeTransactionLine(designMap);
				
				//need to add delete logic for design document and price details
				priceDetailsRepo.deleteByNxTxnIdAndNxDesignId(nxTxnId, nxDesignId);
				designDocumentRepo.deleteByNxTxnIdAndNxDesignId(nxTxnId, nxDesignId);
				designMap.remove(MyPriceConstants.DOCUMENT_ID);
			}
		} catch (SalesBusinessException e) {
			logger.info("Error during removeTransactionServiceLineImpl {}", e.getMessage());
			return false;
		}
		logger.info("End -- removeTransactionServiceLineImpl");
		return true;
	}
	
	/*public void setSystemProperties() {
		System.setProperty("http.proxyHost", httpProxyHost);
		System.setProperty("http.proxyPort", httpProxyPort);
		System.setProperty("https.proxyHost", httpsProxyHost);
		System.setProperty("https.proxyPort", httpsProxyPort);
		System.setProperty("http.proxyUser", proxyUser);
		System.setProperty("http.proxyPassword", proxyPassword);
		System.setProperty("http.proxySet", proxySet);
	}*/
	
	public Map<String, String> myPriceHeaders() {
		Map<String, String> headers  = new HashMap<String, String>();
		String encoded= Base64.getEncoder().encodeToString((myPriceUserName + ":" + myPricePassword).getBytes());
		headers.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+encoded);
		headers.put(StringConstants.REQUEST_CONTENT_TYPE, "application/json");
		return headers;
		
	}
	
	public Set<Object> getValuesFromRequest(Object request, String path) {
		if(request != null) {
			TypeRef<List<Object>> mapType = new TypeRef<List<Object>>() {};
			List<Object> results = jsonPathUtil.search(request, path, mapType);
			if(CollectionUtils.isEmpty(results)) {
				return null;
			} else {
				return new HashSet<Object>(results);
			}
		}
		return null;
	}
	
	public void reconfigureScenarios(RetreiveICBPSPRequest retreiveICBPSPRequest, List<NxMpDeal> nxMpDeal, 
			Map<String, Object> paramMap) {
		CreateTransactionResponse createTransactionResponse = new CreateTransactionResponse();
		paramMap.put("retreiveICBPSPRequest", retreiveICBPSPRequest);
		String sourceName=retreiveICBPSPRequest.getSolution().getSourceName();
		paramMap.put("sourceName", sourceName);
		if(null != retreiveICBPSPRequest) {
			if(null != retreiveICBPSPRequest.getSolution().getOffers()) {
				for(Offer offer : retreiveICBPSPRequest.getSolution().getOffers()) {
					String offerId = offer.getOfferId();
					String offerName = null;
					if (StringUtils.isNotEmpty(offerId)) {
						int id = Integer.parseInt(offerId);
						offerName = salesMsDao.getOfferNameByOfferId(id);
					}
					
					if (StringUtils.isNotEmpty(offerName) && (StringConstants.OFFERNAME_ASE.equalsIgnoreCase(offerName) || StringConstants.OFFERNAME_ASENOD.equalsIgnoreCase(offerName))) {
						Set<Object> designModifiedIndicators = getValuesFromRequest(offer, aseDesignModifiedIndPath);
						Set<Object> designStatusIndicators = getValuesFromRequest(offer, aseDesignStatusPath);
                        Set<Object> asePriceScenarioIds = new HashSet<Object>();
						if((StringConstants.OFFERNAME_ASE.equalsIgnoreCase(offerName) ||
								StringConstants.OFFERNAME_ASENOD.equalsIgnoreCase(offerName)) 
										&& "N".equalsIgnoreCase(retreiveICBPSPRequest.getSolution().getMpsIndicator())){
							asePriceScenarioIds = getValuesFromRequest(offer, asePriceScenarioIdPath);
						}else {
							asePriceScenarioIds = getValuesFromRequest(offer, "Y".equalsIgnoreCase(retreiveICBPSPRequest.getSolution().getAutomationInd()) ? asePriceScenarioIdPath : asePriceModifiedIndPriceScenarioIdPath);
						}
						Set<Object> asePriceModifiedIds = getValuesFromRequest(offer, asePriceModifiedIndPath);
						Set<Object> asePriceModifiedIndPriceScenarioIds = getValuesFromRequest(offer, asePriceModifiedIndPriceScenarioIdPath);
						
						processReconfigure(designStatusIndicators, designModifiedIndicators, asePriceModifiedIds, 
								asePriceScenarioIds, asePriceModifiedIndPriceScenarioIds, nxMpDeal, 
								paramMap, offerName, offer, createTransactionResponse, retreiveICBPSPRequest.getSolution().getSolutionStatus(),retreiveICBPSPRequest);
						
					}else if(StringUtils.isNotEmpty(offerName) &&( StringConstants.OFFERNAME_ADE.equalsIgnoreCase(offerName)&&!StringConstants.IPNE.equalsIgnoreCase(sourceName))) {
//							if(retreiveICBPSPRequest.getSolution().getSourceName().equalsIgnoreCase(StringConstants.IPNE)&&(retreiveICBPSPRequest.getSolution().getOffers().indexOf(offer)!=0)) {
//								break;
//							}
							Set<Object> designModifiedIndicators = getValuesFromRequest(offer, adeDesignModifiedIndPath);
							Set<Object> designStatusIndicators = getValuesFromRequest(offer, adeDesignStatusPath);
							Set<Object> adePriceScenarioIds = getValuesFromRequest(offer, "Y".equalsIgnoreCase(retreiveICBPSPRequest.getSolution().getAutomationInd()) ? adePriceScenarioIdPath : adeActivePriceScenarioIdPath);
							Set<Object> adePriceModifiedIds = getValuesFromRequest(offer, adePriceModifiedIndPath);
							Set<Object> adePriceModifiedIndPriceScenarioIds = getValuesFromRequest(offer, adePriceModifiedIndPriceScenarioIdPath);
							
							processReconfigure(designStatusIndicators, designModifiedIndicators, adePriceModifiedIds, 
									adePriceScenarioIds, adePriceModifiedIndPriceScenarioIds, nxMpDeal, 
									paramMap, offerName, offer, createTransactionResponse, retreiveICBPSPRequest.getSolution().getSolutionStatus(),retreiveICBPSPRequest);
						
					}else if(StringConstants.IPNE.equalsIgnoreCase(sourceName)) {
						if(retreiveICBPSPRequest.getSolution().getOffers().size()>1) {
							List<Offer> offers=  retreiveICBPSPRequest.getSolution().getOffers().stream().filter(u -> u.getOfferId().equals("120")).collect(Collectors.toList());
							
							Set<Object> designModifiedIndicators = getValuesFromRequest(offers.get(0), adeDesignModifiedIndPath);
							offerName="ADE";
							Set<Object> designStatusIndicators = getValuesFromRequest(offers.get(0), adeDesignStatusPath);
							Set<Object> adePriceScenarioIds = getValuesFromRequest(offers.get(0),  adePriceScenarioIdPath);
							Set<Object> adePriceModifiedIds = getValuesFromRequest(offers.get(0), adePriceModifiedIndPath);
							Set<Object> adePriceModifiedIndPriceScenarioIds = getValuesFromRequest(offers.get(0), adePriceModifiedIndPriceScenarioIdPath);
							
							processReconfigure(designStatusIndicators, designModifiedIndicators, adePriceModifiedIds, 
									adePriceScenarioIds, adePriceModifiedIndPriceScenarioIds, nxMpDeal, 
									paramMap, offerName, offers.get(0), createTransactionResponse, retreiveICBPSPRequest.getSolution().getSolutionStatus(),retreiveICBPSPRequest);
						
						}
						else {
							Set<Object> designModifiedIndicators = getValuesFromRequest(offer, adeDesignModifiedIndPath);
							Set<Object> designStatusIndicators = getValuesFromRequest(offer, adeDesignStatusPath);
							Set<Object> adePriceScenarioIds = getValuesFromRequest(offer, adePriceScenarioIdPath);
							Set<Object> adePriceModifiedIds = getValuesFromRequest(offer, adePriceModifiedIndPath);
							Set<Object> adePriceModifiedIndPriceScenarioIds = getValuesFromRequest(offer, adePriceModifiedIndPriceScenarioIdPath);
							
							processReconfigure(designStatusIndicators, designModifiedIndicators, adePriceModifiedIds, 
									adePriceScenarioIds, adePriceModifiedIndPriceScenarioIds, nxMpDeal, 
									paramMap, offerName, offer, createTransactionResponse, retreiveICBPSPRequest.getSolution().getSolutionStatus(), retreiveICBPSPRequest);
						}
						break;
					}
				}
			}
			
		}
	}
	
	public void processReconfigure(Set<Object> designStatusIndicators, Set<Object> designModifiedIndicators, Set<Object> priceModifiedIds, 
			Set<Object> priceScenarioIds, Set<Object> priceModifiedIndPriceScenarioIds, List<NxMpDeal> nxMpDeal, 
			Map<String, Object> paramMap, String offerName, Offer offer, CreateTransactionResponse createTransactionResponse, String solutionStatus, RetreiveICBPSPRequest retreiveICBPSPRequestObj) {
		NxMpDeal deal = null;
		boolean designUpdate = false;
		boolean priceUpdate = false;
		boolean designUpdateForReopen = false;
		boolean callPedForDesignUpdate = false;
		boolean invokeMyprice = true;
		String isPedSuccess = paramMap.get("IS_PED_SUCCESS") != null ? (String) paramMap.get("IS_PED_SUCCESS") : null;
		designUpdateForReopen = checkDesignStatusForR(designStatusIndicators);
		designUpdate = checkDesignStatus(designModifiedIndicators, designStatusIndicators);
		priceUpdate = checkPriceStatus(priceModifiedIds);
		
		//get NX_SOLUTION_DETAILS
		NxSolutionDetail nxSolutionDetail = nxSolutionDetailsRepository.findByNxSolutionId(nxMpDeal.get(0).getSolutionId());
		paramMap.put(StringConstants.SOLUTIONDETAILS, nxSolutionDetail);
		if(designUpdate || designUpdateForReopen || StringConstants.SOLUTION_SOLD.equalsIgnoreCase(solutionStatus) || StringConstants.CONSTANT_N.equalsIgnoreCase(isPedSuccess)) {
			paramMap.put(StringConstants.TRANSACTION_UPDATE, StringConstants.TRANSACTION_UPDATE_DESIGN);
			if(!(designUpdateForReopen || StringConstants.SOLUTION_SOLD.equalsIgnoreCase(solutionStatus))) {
//				deal = getNxMpDeal(nxMpDeal, priceScenarioIds, solPriceScenarioId);
				deal = getNxMpDeal(nxMpDeal, priceScenarioIds);
				
			}
			callPedForDesignUpdate = true;
		} else if(priceUpdate) {
			paramMap.put(StringConstants.TRANSACTION_UPDATE, StringConstants.TRANSACTION_UPDATE_PRICE);
//			deal = getNxMpDeal(nxMpDeal, priceModifiedIndPriceScenarioIds, solPriceScenarioId);
			deal = getNxMpDeal(nxMpDeal, priceModifiedIndPriceScenarioIds);
		}
	
		if(designUpdateForReopen || StringConstants.SOLUTION_SOLD.equalsIgnoreCase(solutionStatus) || StringConstants.CONSTANT_N.equalsIgnoreCase(isPedSuccess)){
			if(deal != null) {
				createTransactionResponse(createTransactionResponse, deal, offerName);
			}
			invokeMyprice = false;
		}else {
			paramMap.put(StringConstants.PRICE_SCENARIO_ID, deal.getPriceScenarioId());
			paramMap.put(StringConstants.MY_PRICE_TRANS_ID, deal.getTransactionId());
			paramMap.put(StringConstants.MY_PRICE_DEAL_ID, deal.getDealID());
			copyTransactionServiceImpl.copyTransaction(paramMap, new FalloutDetailsRequest());
			if(paramMap.containsKey(StringConstants.NEW_MY_PRICE_DEAL)) {
				NxMpDeal newDeal = (NxMpDeal) paramMap.get(StringConstants.NEW_MY_PRICE_DEAL);
				createTransactionResponse(createTransactionResponse, newDeal, offerName);
				//call clean save if it is failed
				String callCleanSave = paramMap.containsKey("CALL_CLEAN_SAVE") ? (String) paramMap.get("CALL_CLEAN_SAVE") : null;
				if(StringConstants.CONSTANT_Y.equalsIgnoreCase(callCleanSave)) {
					Long nxAuditId = (Long) paramMap.get(MyPriceConstants.NX_AUDIT_ID);
					RetreiveICBPSPRequest retreiveICBPSPRequest = (RetreiveICBPSPRequest) paramMap.get("retreiveICBPSPRequest");
					Map<String, Object> updateResponse = callUpdateCleanSave(retreiveICBPSPRequest, createTransactionResponse, nxAuditId, nxSolutionDetail, paramMap);
					Boolean updateStatus =  updateResponse.containsKey(MyPriceConstants.RESPONSE_STATUS) ? (Boolean) updateResponse.get(MyPriceConstants.RESPONSE_STATUS) : false;
					if (!updateStatus) {
						paramMap.put(MyPriceConstants.RESPONSE_STATUS, false);
						return;
					}
				}
				paramMap.put(MyPriceConstants.TRANSACTION_TYPE, MyPriceConstants.AUDIT_UPDATE_OR_RECONFIGURE);
				
				String sourceName = (String) paramMap.get("sourceName");
				UpdateTransactionOverrideRequest overrideRequest=null;
				if(StringConstants.IPNE.equalsIgnoreCase(sourceName)) {
					 overrideRequest = prepareUpdateTransactionOverrideRequestForIpne(newDeal, paramMap, designStatusIndicators, offer, offerName,retreiveICBPSPRequestObj);
				}
				else {
					 paramMap.put(MyPriceConstants.SOLUTION_VERSION, retreiveICBPSPRequestObj.getSolution().getSolutionVersion());
					 overrideRequest = prepareUpdateTransactionOverrideRequest(newDeal, paramMap, designStatusIndicators, offer, offerName);
				}
				ServiceResponse overrideRes= updateTransactionOverrideImpl.updateTransactionOverride(overrideRequest);
				if(null != overrideRes && !CommonConstants.SUCCESS_STATUS.equals(overrideRes.getStatus().getCode())){
					paramMap.put(MyPriceConstants.RESPONSE_STATUS, false);
					return;
				}
				
			}else {
				paramMap.put(MyPriceConstants.RESPONSE_STATUS, false);
				return;
			}
			deal.setActiveYN("N");
			nxMpDealRepository.save(deal);
		}
		paramMap.put("createTransactionResponse", createTransactionResponse);
		paramMap.put(StringConstants.DESIGN_UPDATE_FOR_REOPEN, designUpdateForReopen);
		paramMap.put(StringConstants.CALLPEDFORDESIGNUPDATE, callPedForDesignUpdate);
		paramMap.put(StringConstants.INVOKE_MYPRICE, invokeMyprice);
		paramMap.put("priceScenarioIds", priceScenarioIds);
		paramMap.put("priceModifiedIndPriceScenarioIds", priceModifiedIndPriceScenarioIds);
	}
	
	public void createTransactionResponse(CreateTransactionResponse createTransactionResponse, NxMpDeal nxMpDeal, String offerName) {
		createTransactionResponse.setNxTransacId(nxMpDeal.getNxTxnId());
		createTransactionResponse.setDealID(nxMpDeal.getDealID());
		createTransactionResponse.setPriceScenarioId(nxMpDeal.getPriceScenarioId());
		if(offerName != null) {
			createTransactionResponse.setOfferName(offerName);
		}else {
			createTransactionResponse.setOfferName(nxMpDeal.getOfferId());
		}
		createTransactionResponse.setMyPriceTransacId(nxMpDeal.getTransactionId());
		createTransactionResponse.setDealID(nxMpDeal.getDealID());
		createTransactionResponse.setVersion(nxMpDeal.getVersion());
		createTransactionResponse.setRevision(nxMpDeal.getRevision());
		createTransactionResponse.setSuccess(true);
	}
	
	/*
	 * if designModifiedIndicators contains Y 
	 * or
	 * if designStatusIndicators contains U or C or N
	 * then its design update.
	 */
	public boolean checkDesignStatus(Set<Object> designModifiedIndicators, Set<Object> designStatusIndicators) {
		if((CollectionUtils.isNotEmpty(designModifiedIndicators) && designModifiedIndicators.contains(StringConstants.CONSTANT_Y)) || 
				(CollectionUtils.isNotEmpty(designStatusIndicators) && (designStatusIndicators.contains(StringConstants.DESIGN_UPDATE) || designStatusIndicators.contains(StringConstants.DESIGN_CANCEL) || designStatusIndicators.contains(StringConstants.DESIGN_NEW)))) {
			return true;
		}
		return false;
	}
	
	public boolean checkDesignStatusForR(Set<Object> designStatusIndicators) {
		if(CollectionUtils.isNotEmpty(designStatusIndicators) && designStatusIndicators.contains(StringConstants.DESIGN_REOPEN)) {
			return true;
		}
		return false;
	}
	
	public boolean checkPriceStatus(Set<Object> asePriceModifiedIds) {
		if(CollectionUtils.isNotEmpty(asePriceModifiedIds) && asePriceModifiedIds.contains(StringConstants.CONSTANT_Y)) {
			return true;
		}
		return false;
	}

	public String getSiteDictionary(NxMpDeal deal, Set<Object> designStatusIndicators, Offer offer, String offerName) throws JSONException { // {
		String siteAddressJson = null;
		if(CollectionUtils.isNotEmpty(designStatusIndicators) && designStatusIndicators.contains(StringConstants.DESIGN_NEW)) {
			NxMpSiteDictionary siteData = siteRepo.findByNxTxnId(deal.getNxTxnId());
			List<JsonNode> listNode = new ArrayList<>();
			
			Set<Object> adeNewSiteIds = new HashSet<>();
			if(StringConstants.OFFERNAME_ADE.equalsIgnoreCase(offerName)) {
				adeNewSiteIds = getValuesFromRequest(offer, adeNewSiteIdPath);
			}
			for(Site site : offer.getSite()) { 
				if ((StringConstants.OFFERNAME_ADE.equalsIgnoreCase(offerName) && StringConstants.DESIGN_NEW.equalsIgnoreCase(site.getDesignStatus())) 
						|| (StringConstants.OFFERNAME_ADE.equalsIgnoreCase(offerName) && CollectionUtils.isNotEmpty(adeNewSiteIds) && adeNewSiteIds.contains(site.getSiteId()))
						|| ((StringConstants.OFFERNAME_ASE.equalsIgnoreCase(offerName) || StringConstants.OFFERNAME_ASENOD.equalsIgnoreCase(offerName)) && StringConstants.DESIGN_NEW.equalsIgnoreCase(site.getDesignStatus()))) { 
					ObjectNode map = mapper.createObjectNode(); 
					Long nxSiteId = getNxSiteId(); 
					site.setNxSiteId(nxSiteId);
					String address2 = Strings.isNullOrEmpty(site.getAddress2()) ? "" : site.getAddress2();
					map.put("nxSiteId", nxSiteId); 
					map.put("name", site.getSiteName()); 
					map.put("addressLine", site.getAddress1() + " " + address2); 
					map.put("city", site.getCity()); 
					map.put("state", site.getState()); 
					map.put("postalCode", site.getZipCode()); 
					map.put("country", site.getCountry()); 
					map.put("address", site.getAddress1() + " " + address2 + " " + site.getCity() + " " 
							+ site.getState() + " " + site.getZipCode() + " " + site.getCountry()); 
					map.put("validationStatus", CommonConstants.VALID); 
					if(StringConstants.OFFERNAME_ADE.equalsIgnoreCase(offerName)) {
						map.put("buildingClli", site.getCustomerLocationClli()); 
					}else {
						map.put("buildingClli", site.getBuildingClli()); 
					}
					map.put("regionFranchiseStatus", site.getRegionCode()); 
					map.put("swcCLLI", site.getSwcClli()); 
					map.put("globalLocationId", site.getGlobalLocationId());
					listNode.add(map); 
				} 
			}
			if(CollectionUtils.isNotEmpty(listNode) && null != siteData && null != siteData.getSiteAddress()) {
				ObjectNode node = (ObjectNode) JacksonUtil.toJsonNode(siteData.getSiteAddress());
				JsonNode referenceId = node.at("/siteAddress");
				if (!referenceId.isMissingNode() && !referenceId.isNull()) {
					if (referenceId.isTextual()) {
						try {
							String referenceIdString = referenceId.toString();
							referenceIdString = referenceIdString.substring(1, referenceIdString.length() - 1);
							String referenceIdStringEscaped = StringEscapeUtils.unescapeJava(referenceIdString);
							JsonNode readTree = mapper.readTree(referenceIdStringEscaped);
							node.set("siteAddress", readTree);
						} catch (IOException e) {
							logger.info(
									"NxMpSiteDictionary.getSiteAddress String expression for siteAddress is not an ArrayNode. Json parsing error");
						}
					}
					((ObjectNode) node).withArray("siteAddress").addAll(listNode);
				} else {
					((ObjectNode) node).putArray("siteAddress").addAll(listNode);
				}
				
				try {
					siteAddressJson = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(node.at("/siteAddress"));
					siteData.setSiteAddress(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(node));
					siteData.setModifiedDate(new Date());
					siteRepo.save(siteData);
				} catch (JsonProcessingException e) {
					logger.info("Exception happened in getSiteDictionary during conversion for {}", deal.getNxTxnId());
				}
			}
		}
		else if(CollectionUtils.isNotEmpty(designStatusIndicators) && designStatusIndicators.contains(StringConstants.DESIGN_CANCEL)) {
			NxMpSiteDictionary siteData = siteRepo.findByNxTxnId(deal.getNxTxnId());
			if (StringUtils.isNotEmpty(offerName) && (StringConstants.OFFERNAME_ASE.equalsIgnoreCase(offerName) || StringConstants.OFFERNAME_ASENOD.equalsIgnoreCase(offerName))) {
				for(Site site : offer.getSite()) {
					if (StringConstants.DESIGN_CANCEL.equalsIgnoreCase(site.getDesignStatus())) {
						JSONObject siteDictAddress = new JSONObject(siteData.getSiteAddress());
						JSONArray siteDictArray = new JSONArray(siteDictAddress.get("siteAddress").toString());
						for (int i = 0; i < siteDictArray.length(); i++) {
							JSONObject siteDict = siteDictArray.getJSONObject(i);
							String siteName = (siteDict.has("name") && !JSONObject.NULL.equals(siteDict.get("name"))) ? siteDict.getString("name") : null;
							String address = (siteDict.has("addressLine") && !JSONObject.NULL.equals(siteDict.get("addressLine"))) ? siteDict.getString("addressLine") : null;
							String city = (siteDict.has("city") && !JSONObject.NULL.equals(siteDict.get("city"))) ? siteDict.getString("city") : null;
							String state = (siteDict.has("state") && !JSONObject.NULL.equals(siteDict.get("state"))) ? siteDict.getString("state") : null;
							String zipCode = (siteDict.has("postalCode") && !JSONObject.NULL.equals(siteDict.get("postalCode"))) ? siteDict.getString("postalCode") : null;
							String country = (siteDict.has("country") && !JSONObject.NULL.equals(siteDict.get("country"))) ? siteDict.getString("country") : null;
							String address2 = Strings.isNullOrEmpty(site.getAddress2()) ? "" : site.getAddress2();
							String addressLine = site.getAddress1() + " " + address2;
							if(StringUtils.equals(siteName, site.getSiteName()) && StringUtils.equals(address, addressLine)
									&& StringUtils.equals(city, site.getCity()) && StringUtils.equals(state, site.getState())
									&& StringUtils.equals(zipCode, site.getZipCode()) && StringUtils.equals(country, site.getCountry())) {
								siteDictArray.remove(i);
							}
						}
						siteAddressJson = siteDictArray.toString();
					}
				}
			}
			else if (StringUtils.isNotEmpty(offerName) && (StringConstants.OFFERNAME_ADE.equalsIgnoreCase(offerName)||StringConstants.OFFERNAME_EPLSWAN.equalsIgnoreCase(offerName))) {
				Map<Long, Site> siteMap = new HashMap<>();
				for(Site site : offer.getSite()) {
					if (site.getSiteId() != null) {
						siteMap.put(site.getSiteId(), site);
					}
				}
				for(Circuit circuit : offer.getCircuit()) {
					if (StringConstants.DESIGN_CANCEL.equalsIgnoreCase(circuit.getDesignStatus())) {
						List<com.att.sales.nexxus.reteriveicb.model.Component> components = circuit.getComponent().stream().filter(comp -> comp.getComponentCodeId().equals(Long.parseLong(StringConstants.COMPONENTID_ENDPOINT))).collect(Collectors.toList());
						for(com.att.sales.nexxus.reteriveicb.model.Component component : components) {
							Long referenceId = component.getReferences().get(0).getReferenceId();
							Site site = null;
							if (referenceId != null) {
								site = siteMap.get(referenceId);
							}
							JSONObject siteDictAddress = new JSONObject(siteData.getSiteAddress());
							JSONArray siteDictArray = new JSONArray(siteDictAddress.get("siteAddress").toString());
							for (int i = 0; i < siteDictArray.length(); i++) {
								JSONObject siteDict = siteDictArray.getJSONObject(i);
								String siteName = (siteDict.has("name") && !JSONObject.NULL.equals(siteDict.get("name"))) ? siteDict.getString("name") : null;
								String address = (siteDict.has("addressLine") && !JSONObject.NULL.equals(siteDict.get("addressLine"))) ? siteDict.getString("addressLine") : null;
								String city = (siteDict.has("city") && !JSONObject.NULL.equals(siteDict.get("city"))) ? siteDict.getString("city") : null;
								String state = (siteDict.has("state") && !JSONObject.NULL.equals(siteDict.get("state"))) ? siteDict.getString("state") : null;
								String zipCode = (siteDict.has("postalCode") && !JSONObject.NULL.equals(siteDict.get("postalCode"))) ? siteDict.getString("postalCode") : null;
								String country = (siteDict.has("country") && !JSONObject.NULL.equals(siteDict.get("country"))) ? siteDict.getString("country") : null;
								String address2=Strings.isNullOrEmpty(site.getAddress2()) ? "" : site.getAddress2();
								String addressLine = site.getAddress1() + " " +address2;
								if(StringUtils.equalsIgnoreCase(siteName, site.getSiteName()) && StringUtils.equalsIgnoreCase(address, addressLine)
										&& StringUtils.equalsIgnoreCase(city, site.getCity()) && StringUtils.equalsIgnoreCase(state, site.getState())
										&& StringUtils.equalsIgnoreCase(zipCode, site.getZipCode()) && StringUtils.equalsIgnoreCase(country, site.getCountry())) {
									site.setNxSiteId((siteDict.has("nxSiteId") && !JSONObject.NULL.equals(siteDict.get("nxSiteId")))? siteDict.getLong("nxSiteId") : 0l);
									siteDictArray.remove(i);
									for(Site siteObj : offer.getSite()) {
										if (siteObj.getSiteId() != null &&siteObj.getSiteId().equals(site.getSiteId())) {
											siteObj.setNxSiteId(site.getNxSiteId());
										}
									}
								}
							}
							siteAddressJson = siteDictArray.toString();
						}
					}
				}
			}
		}
		else if(CollectionUtils.isNotEmpty(designStatusIndicators) && designStatusIndicators.contains(StringConstants.DESIGN_UPDATE)) {
			Map<Long, Map<String, String>> clliMap = getClliValuesforAllSites(offer);
			NxMpSiteDictionary siteData = siteRepo.findByNxTxnId(deal.getNxTxnId());
			JSONObject siteDictAddress = new JSONObject(siteData.getSiteAddress());
			JSONArray siteDictArray = new JSONArray(siteDictAddress.get("siteAddress").toString());
			if (StringUtils.isNotEmpty(offerName) && (StringConstants.OFFERNAME_ASE.equalsIgnoreCase(offerName) || StringConstants.OFFERNAME_ASENOD.equalsIgnoreCase(offerName))) {
				for(Site site : offer.getSite()) {
					if (StringConstants.DESIGN_UPDATE.equalsIgnoreCase(site.getDesignStatus())) {
						for (int i = 0; i < siteDictArray.length(); i++) {
							JSONObject siteDict = siteDictArray.getJSONObject(i);
							getUpdatedSiteJson(site, siteDict, clliMap,offerName);
						}
					}
				}
			}
			else if (StringUtils.isNotEmpty(offerName) && StringConstants.OFFERNAME_ADE.equalsIgnoreCase(offerName)) {
				Map<Long, Site> siteMap = new HashMap<>();
				for(Site site : offer.getSite()) {
					if (site.getSiteId() != null) {
						siteMap.put(site.getSiteId(), site);
					}
				}
				for(Circuit circuit : offer.getCircuit()) {
					if (StringConstants.DESIGN_UPDATE.equalsIgnoreCase(circuit.getDesignStatus())) {
						List<com.att.sales.nexxus.reteriveicb.model.Component> components = circuit.getComponent().stream().filter(comp -> comp.getComponentCodeId().equals(Long.parseLong(StringConstants.COMPONENTID_ENDPOINT))).collect(Collectors.toList());
						for(com.att.sales.nexxus.reteriveicb.model.Component component : components) {
							Long referenceId = component.getReferences().get(0).getReferenceId();
							Site site = null;
							if (referenceId != null) {
								site = siteMap.get(referenceId);
							}
							for (int i = 0; i < siteDictArray.length(); i++) {
								JSONObject siteDict = siteDictArray.getJSONObject(i);
								getUpdatedSiteJson(site, siteDict, clliMap,offerName);
							}
						}
					}
				}
			}
			siteAddressJson = siteDictArray.toString();
			try {
				siteData.setSiteAddress("{ \"siteAddress\" :  " + siteDictArray + "}");
				siteData.setModifiedDate(new Date());
				siteRepo.save(siteData);
			} catch (Exception e) {
				logger.info("Exception happened in getSiteDictionary during design update conversion for {}", deal.getNxTxnId());
			}
		}
		return siteAddressJson;
	}
	
	
	public String getSiteDictionaryForIpne(NxMpDeal deal, Set<Object> designStatusIndicators, Offer offer, String offerName,RetreiveICBPSPRequest retreiveICBPSPRequest, List<Long> nxSiteIdList) {
		String siteAddressJson = null;
		if(CollectionUtils.isNotEmpty(designStatusIndicators) && designStatusIndicators.contains(StringConstants.DESIGN_NEW)) {
			NxMpSiteDictionary siteData = siteRepo.findByNxTxnId(deal.getNxTxnId());
			List<JsonNode> listNode = new ArrayList<>();
			JSONObject siteDictAddress = new JSONObject(siteData.getSiteAddress());
			JSONArray siteDictArray = new JSONArray(siteDictAddress.get("siteAddress").toString());
			Map<Object, Object> siteIdmap =new HashMap<>();
			
			for(Offer offerObj : retreiveICBPSPRequest.getSolution().getOffers()) {
				Set<Object> adeNewSiteIds = new HashSet<>();
				if(StringConstants.OFFERNAME_ADE.equalsIgnoreCase(offerName)|| StringConstants.OFFERNAME_EPLSWAN.equalsIgnoreCase(offerName)) {
					adeNewSiteIds = getValuesFromRequest(offerObj, adeNewSiteIdPath);
				}
				for(Site site : offerObj.getSite()) { 
					boolean isExistingSiteId=false;
					if ((StringConstants.OFFERNAME_ADE.equalsIgnoreCase(offerName) && StringConstants.DESIGN_NEW.equalsIgnoreCase(site.getDesignStatus())) 
							|| (StringConstants.OFFERNAME_ADE.equalsIgnoreCase(offerName) && CollectionUtils.isNotEmpty(adeNewSiteIds) && adeNewSiteIds.contains(site.getSiteId().intValue()))
							|| (StringConstants.OFFERNAME_EPLSWAN.equalsIgnoreCase(offerName) && CollectionUtils.isNotEmpty(adeNewSiteIds) && adeNewSiteIds.contains(site.getSiteId().intValue()))) { 
						
						
						for (int i = 0; i < siteDictArray.length(); i++) {
							
							JSONObject siteDict = siteDictArray.getJSONObject(i);
							Long pricerdSiteId = (siteDict.has("pricerdSiteId") && !JSONObject.NULL.equals(siteDict.get("pricerdSiteId"))) ? siteDict.getLong("pricerdSiteId") : 0L;							
							if(Long.compare(pricerdSiteId, site.getSiteId()) == 0) {						
								if("210".equalsIgnoreCase(offerObj.getOfferId())){
									siteDict.put("swcCLLI", site.getSwcClli());
									siteDict.put("buildingClli",site.getCustomerLocationClli());
									siteDict.put("npanxx",site.getNpanxx());
									List<String> popclli=getDataInString(offerObj,MyPriceConstants.EPLSWAN_POPCLLI_UDFID,site.getSiteId().toString());
									if (!CollectionUtils.isEmpty(popclli)) {
										siteDict.put("popClli",popclli.get(0));
									}	
									List<String> tokenIdEthernet=getDataInString(offerObj,MyPriceConstants.EPLSWAN_TOKEN_ID_ETHERNET_UDFID,site.getSiteId().toString());
									if (!CollectionUtils.isEmpty(tokenIdEthernet)) {
										siteDict.put("tokenIdEthernet",tokenIdEthernet.get(0));
									}
									List<String> vendorZone=getDataInString(offerObj,MyPriceConstants.EPLSWAN_VENDOR_ZONE_UDFID,site.getSiteId().toString());
									if (!CollectionUtils.isEmpty(vendorZone)) {
										siteDict.put("vendorZone",vendorZone.get(0));
									}
								}
								site.setNxSiteId((siteDict.has("nxSiteId") && !JSONObject.NULL.equals(siteDict.get("nxSiteId")))? siteDict.getLong("nxSiteId") : 0l);
								nxSiteIdList.add((siteDict.has("nxSiteId") && !JSONObject.NULL.equals(siteDict.get("nxSiteId")))? siteDict.getLong("nxSiteId") : 0l);
								isExistingSiteId=true;
							    break;
							}
						}
						
						if(!isExistingSiteId) {
							ObjectNode map = mapper.createObjectNode(); 	
							Long nxSiteId=null;
							if(siteIdmap != null && siteIdmap.containsKey(site.getSiteId())) {
								ObjectNode  existingSite =(ObjectNode) siteIdmap.get(site.getSiteId());
								JsonNode existingnxSiteId= existingSite.get("nxSiteId");
								if("210".equalsIgnoreCase(offerObj.getOfferId())) {
									existingSite.put("swcCLLI", site.getSwcClli());
									existingSite.put("buildingClli",site.getCustomerLocationClli());
									existingSite.put("npanxx",site.getNpanxx());
									List<String> popclli=getDataInString(offerObj,MyPriceConstants.EPLSWAN_POPCLLI_UDFID,site.getSiteId().toString());
									if (!CollectionUtils.isEmpty(popclli)) {
										existingSite.put("popClli",popclli.get(0));
									}	
									List<String> tokenIdEthernet=getDataInString(offerObj,MyPriceConstants.EPLSWAN_TOKEN_ID_ETHERNET_UDFID,site.getSiteId().toString());
									if (!CollectionUtils.isEmpty(tokenIdEthernet)) {
										existingSite.put("tokenIdEthernet",tokenIdEthernet.get(0));
									}
									List<String> vendorZone=getDataInString(offerObj,MyPriceConstants.EPLSWAN_VENDOR_ZONE_UDFID,site.getSiteId().toString());
									if (!CollectionUtils.isEmpty(vendorZone)) {
										existingSite.put("vendorZone",vendorZone.get(0));
									}
									
								}
								siteIdmap.put(site.getSiteId(),existingSite);
								site.setNxSiteId(existingnxSiteId.asLong());
								nxSiteIdList.add(existingnxSiteId.asLong());
								continue;
							}
							else {
								nxSiteId = getNxSiteId();	 
							}	
							
							site.setNxSiteId(nxSiteId);
							nxSiteIdList.add(nxSiteId);
							String address2 = Strings.isNullOrEmpty(site.getAddress2()) ? "" : site.getAddress2();
							map.put("pricerdSiteId", site.getSiteId());
							map.put("nxSiteId", nxSiteId); 
							map.put("name", site.getSiteName()); 
							map.put("addressLine", site.getAddress1() + " " + address2); 
							map.put("city", site.getCity()); 
							map.put("state", site.getState()); 
							map.put("postalCode", site.getZipCode()); 
							map.put("country", site.getCountry()); 
							map.put("address", site.getAddress1() + " " + address2 + " " + site.getCity() + " " 
									+ site.getState() + " " + site.getZipCode() + " " + site.getCountry()); 
							map.put("validationStatus", CommonConstants.VALID); 
							if(StringConstants.OFFERNAME_ADE.equalsIgnoreCase(offerName)) {
								map.put("buildingClli", site.getCustomerLocationClli()); 
							}else {
								map.put("buildingClli", site.getBuildingClli()); 
							}
							if("210".equalsIgnoreCase(offerObj.getOfferId())){
								map.put("swcCLLI", site.getSwcClli());
								map.put("buildingClli",site.getCustomerLocationClli());
								map.put("npanxx",site.getNpanxx());
								List<String> popclli=getDataInString(offerObj,MyPriceConstants.EPLSWAN_POPCLLI_UDFID,site.getSiteId().toString());
								if (!CollectionUtils.isEmpty(popclli)) {
									map.put("popClli",popclli.get(0));
								}	
								List<String> tokenIdEthernet=getDataInString(offerObj,MyPriceConstants.EPLSWAN_TOKEN_ID_ETHERNET_UDFID,site.getSiteId().toString());
								if (!CollectionUtils.isEmpty(tokenIdEthernet)) {
									map.put("tokenIdEthernet",tokenIdEthernet.get(0));
								}
								List<String> vendorZone=getDataInString(offerObj,MyPriceConstants.EPLSWAN_VENDOR_ZONE_UDFID,site.getSiteId().toString());
								if (!CollectionUtils.isEmpty(vendorZone)) {
									map.put("vendorZone",vendorZone.get(0));
								}
							}
							map.put("regionFranchiseStatus", site.getRegionCode()); 
							map.put("swcCLLI", site.getSwcClli()); 
							map.put("globalLocationId", site.getGlobalLocationId());
//							listNode.add(map);
							siteIdmap.put(site.getSiteId(),map);
							
						}
						else {
							continue;	
						}
					}
				}
			}
			if(!siteIdmap.isEmpty()) {
				siteIdmap.forEach((k, map)-> listNode.add((JsonNode) map));
			}
			siteData.setSiteAddress("{ \"siteAddress\" :  " + siteDictArray.toString() + "}");
			if(CollectionUtils.isNotEmpty(listNode) && null != siteData && null != siteData.getSiteAddress()) {
				ObjectNode node = (ObjectNode) JacksonUtil.toJsonNode(siteData.getSiteAddress());
				JsonNode referenceId = node.at("/siteAddress");
				if (!referenceId.isMissingNode() && !referenceId.isNull()) {
					if (referenceId.isTextual()) {
						try {
							String referenceIdString = referenceId.toString();
							referenceIdString = referenceIdString.substring(1, referenceIdString.length() - 1);
							String referenceIdStringEscaped = StringEscapeUtils.unescapeJava(referenceIdString);
							JsonNode readTree = mapper.readTree(referenceIdStringEscaped);
							node.set("siteAddress", readTree);
						} catch (IOException e) {
							logger.info(
									"NxMpSiteDictionary.getSiteAddress String expression for siteAddress is not an ArrayNode. Json parsing error");
						}
					}
					((ObjectNode) node).withArray("siteAddress").addAll(listNode);
				} else {
					((ObjectNode) node).putArray("siteAddress").addAll(listNode);
				}
				
				try {
					siteAddressJson = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(node.at("/siteAddress"));
					siteData.setSiteAddress(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(node));
					siteData.setModifiedDate(new Date());
					siteRepo.save(siteData);
				} catch (JsonProcessingException e) {
					logger.info("Exception happened in getSiteDictionary during conversion for {}", deal.getNxTxnId());
				}
			}
		}
		if(CollectionUtils.isNotEmpty(designStatusIndicators) && designStatusIndicators.contains(StringConstants.DESIGN_CANCEL)) {
			NxMpSiteDictionary siteData = siteRepo.findByNxTxnId(deal.getNxTxnId());
			
			if (StringUtils.isNotEmpty(offerName) && (StringConstants.OFFERNAME_ADE.equalsIgnoreCase(offerName)||StringConstants.OFFERNAME_EPLSWAN.equalsIgnoreCase(offerName))) {
//				Map<Long, Site> siteMap = new HashMap<>();
//				for(Site site : offer.getSite()) {
//					if (site.getSiteId() != null) {
//						siteMap.put(site.getSiteId(), site);
//					}
//				}
				JSONObject siteDictAddress = new JSONObject(siteData.getSiteAddress());
				JSONArray siteDictArray = new JSONArray(siteDictAddress.get("siteAddress").toString());
				for(Offer offerObj : retreiveICBPSPRequest.getSolution().getOffers()) {
					Map<Long, Site> siteMap = new HashMap<>();
					for(Site site : offerObj.getSite()) {
						if (site.getSiteId() != null) {
							siteMap.put(site.getSiteId(), site);
						}
					}
					for(Circuit circuit : offerObj.getCircuit()) {
						if (StringConstants.DESIGN_CANCEL.equalsIgnoreCase(circuit.getDesignStatus())) {
							List<com.att.sales.nexxus.reteriveicb.model.Component> components = circuit.getComponent().stream().filter(comp -> comp.getComponentCodeId().equals(Long.parseLong(StringConstants.COMPONENTID_ENDPOINT))).collect(Collectors.toList());
							for(com.att.sales.nexxus.reteriveicb.model.Component component : components) {
								Long referenceId = component.getReferences().get(0).getReferenceId();
								Site site = null;
								if (referenceId != null) {
									site = siteMap.get(referenceId);
								}
//								JSONObject siteDictAddress = new JSONObject(siteData.getSiteAddress());
//								JSONArray siteDictArray = new JSONArray(siteDictAddress.get("siteAddress").toString());
								for (int i = 0; i < siteDictArray.length(); i++) {
									JSONObject siteDict = siteDictArray.getJSONObject(i);
									String siteName = (siteDict.has("name") && !JSONObject.NULL.equals(siteDict.get("name"))) ? siteDict.getString("name") : null;
									String address = (siteDict.has("addressLine") && !JSONObject.NULL.equals(siteDict.get("addressLine"))) ? siteDict.getString("addressLine") : null;
									String city = (siteDict.has("city") && !JSONObject.NULL.equals(siteDict.get("city"))) ? siteDict.getString("city") : null;
									String state = (siteDict.has("state") && !JSONObject.NULL.equals(siteDict.get("state"))) ? siteDict.getString("state") : null;
									String zipCode = (siteDict.has("postalCode") && !JSONObject.NULL.equals(siteDict.get("postalCode"))) ? siteDict.getString("postalCode") : null;
									String country = (siteDict.has("country") && !JSONObject.NULL.equals(siteDict.get("country"))) ? siteDict.getString("country") : null;
									String address2=Strings.isNullOrEmpty(site.getAddress2()) ? "" : site.getAddress2();
									String addressLine = site.getAddress1() + " " +address2;
									if(StringUtils.equalsIgnoreCase(siteName, site.getSiteName()) && StringUtils.equalsIgnoreCase(address, addressLine)
											&& StringUtils.equalsIgnoreCase(city, site.getCity()) && StringUtils.equalsIgnoreCase(state, site.getState())
											&& StringUtils.equalsIgnoreCase(zipCode, site.getZipCode()) && StringUtils.equalsIgnoreCase(country, site.getCountry())) {
										site.setNxSiteId((siteDict.has("nxSiteId") && !JSONObject.NULL.equals(siteDict.get("nxSiteId")))? siteDict.getLong("nxSiteId") : 0l);
//										siteDictArray.remove(i);
										for(Site siteObj : offerObj.getSite()) {
											if (siteObj.getSiteId() != null &&siteObj.getSiteId().equals(site.getSiteId())) {
												siteObj.setNxSiteId(site.getNxSiteId());
												nxSiteIdList.add(site.getNxSiteId());
											}
										}
									}
								}
//								siteAddressJson = siteDictArray.toString();
							}
						}
						
					}
				}
				siteAddressJson = siteDictArray.toString();
			}
		}
		if(CollectionUtils.isNotEmpty(designStatusIndicators) && designStatusIndicators.contains(StringConstants.DESIGN_UPDATE)) {
//			Map<Long, Map<String, String>> clliMap = getClliValuesforAllSites(offer);
			NxMpSiteDictionary siteData = siteRepo.findByNxTxnId(deal.getNxTxnId());
			JSONObject siteDictAddress = new JSONObject(siteData.getSiteAddress());
			JSONArray siteDictArray = new JSONArray(siteDictAddress.get("siteAddress").toString());
			if (StringUtils.isNotEmpty(offerName) && (StringConstants.OFFERNAME_ADE.equalsIgnoreCase(offerName)||StringConstants.OFFERNAME_EPLSWAN.equalsIgnoreCase(offerName))) {
				for(Offer offerObj : retreiveICBPSPRequest.getSolution().getOffers()) {
					Map<Long, Map<String, String>> clliMap = getClliValuesforAllSites(offerObj);
					Map<Long, Site> siteMap = new HashMap<>();
					for(Site site : offerObj.getSite()) {
						if (site.getSiteId() != null) {
							siteMap.put(site.getSiteId(), site);
						}
					}
					for(Circuit circuit : offerObj.getCircuit()) {
						if (StringConstants.DESIGN_UPDATE.equalsIgnoreCase(circuit.getDesignStatus())) {
							List<com.att.sales.nexxus.reteriveicb.model.Component> components = circuit.getComponent().stream().filter(comp -> comp.getComponentCodeId().equals(Long.parseLong(StringConstants.COMPONENTID_ENDPOINT))).collect(Collectors.toList());
							for(com.att.sales.nexxus.reteriveicb.model.Component component : components) {
								Long referenceId = component.getReferences().get(0).getReferenceId();
								Site site = null;
								if (referenceId != null) {
									site = siteMap.get(referenceId);
								}
								for (int i = 0; i < siteDictArray.length(); i++) {
									
									JSONObject siteDict = siteDictArray.getJSONObject(i);
									getUpdatedSiteJson(site, siteDict, clliMap,offerName);
									
									String siteName = (siteDict.has("name") && !JSONObject.NULL.equals(siteDict.get("name"))) ? siteDict.getString("name") : null;
									String address = (siteDict.has("addressLine") && !JSONObject.NULL.equals(siteDict.get("addressLine"))) ? siteDict.getString("addressLine") : null;
									String city = (siteDict.has("city") && !JSONObject.NULL.equals(siteDict.get("city"))) ? siteDict.getString("city") : null;
									String state = (siteDict.has("state") && !JSONObject.NULL.equals(siteDict.get("state"))) ? siteDict.getString("state") : null;
									String zipCode = (siteDict.has("postalCode") && !JSONObject.NULL.equals(siteDict.get("postalCode"))) ? siteDict.getString("postalCode") : null;
									String country = (siteDict.has("country") && !JSONObject.NULL.equals(siteDict.get("country"))) ? siteDict.getString("country") : null;
									String address2=Strings.isNullOrEmpty(site.getAddress2()) ? "" : site.getAddress2();
									String addressLine = site.getAddress1() + " " +address2;
									if(StringUtils.equalsIgnoreCase(siteName, site.getSiteName()) && StringUtils.equalsIgnoreCase(address, addressLine)
											&& StringUtils.equalsIgnoreCase(city, site.getCity()) && StringUtils.equalsIgnoreCase(state, site.getState())
											&& StringUtils.equalsIgnoreCase(zipCode, site.getZipCode()) && StringUtils.equalsIgnoreCase(country, site.getCountry())) {
										site.setNxSiteId((siteDict.has("nxSiteId") && !JSONObject.NULL.equals(siteDict.get("nxSiteId")))? siteDict.getLong("nxSiteId") : 0l);
//											siteDictArray.remove(i);
										for(Site siteObj : offerObj.getSite()) {
											if (siteObj.getSiteId() != null &&siteObj.getSiteId().equals(site.getSiteId())) {
												siteObj.setNxSiteId(site.getNxSiteId());
												nxSiteIdList.add(site.getNxSiteId());
											}
										}
									}
								}
							}
						}
					}
				}
			}
			siteAddressJson = siteDictArray.toString();
			try {
				siteData.setSiteAddress("{ \"siteAddress\" :  " + siteDictArray + "}");
				siteData.setModifiedDate(new Date());
				siteRepo.save(siteData);
			} catch (Exception e) {
				logger.info("Exception happened in getSiteDictionary during design update conversion for {}", deal.getNxTxnId());
			}
		}
		return siteAddressJson;
	}
	public void getUpdatedSiteJson(Site site, JSONObject siteDict, Map<Long, Map<String, String>> clliMap,String offerName) {
		Long pricerdSiteId = (siteDict.has("pricerdSiteId") && !JSONObject.NULL.equals(siteDict.get("pricerdSiteId"))) ? siteDict.getLong("pricerdSiteId") : 0L;
		if(Long.compare(pricerdSiteId, site.getSiteId()) == 0) {
			String address2 = Strings.isNullOrEmpty(site.getAddress2()) ? "" : site.getAddress2();
			siteDict.put("name", site.getSiteName());
			siteDict.put("addressLine", site.getAddress1() + " " + address2);
			siteDict.put("city", site.getCity());
			siteDict.put("state", site.getState());
			siteDict.put("postalCode", site.getZipCode());
			siteDict.put("country", site.getCountry());
			siteDict.put("address",
					site.getAddress1() + " " + address2 + " " + site.getCity() + " "
							+ site.getState() + " " + site.getZipCode() + " "
							+ site.getCountry());
			siteDict.put("validationStatus", CommonConstants.VALID);
			siteDict.put("regionFranchiseStatus", site.getRegionCode());
			siteDict.put("globalLocationId", site.getGlobalLocationId());
			
			//for ADE swcCLLI and buildingClli(CustomerLocationClli) pick from only siteLevel
			if (StringUtils.isNotEmpty(offerName) && StringConstants.OFFERNAME_ADE.equalsIgnoreCase(offerName)) {
				siteDict.put("swcCLLI", site.getSwcClli());
				siteDict.put("buildingClli", site.getCustomerLocationClli());
			}else {
				Map<String, String> currentClliMap = clliMap.get(site.getSiteId());
				if (null != currentClliMap && currentClliMap.containsKey("swcLLi")) {
					siteDict.put("swcCLLI", currentClliMap.get("swcLLi"));
				} else {
					siteDict.put("swcCLLI", site.getSwcClli());
				}
				if (null != currentClliMap && currentClliMap.containsKey("buildingClli")) {
					siteDict.put("buildingClli", currentClliMap.get("buildingClli"));
				} else {
					siteDict.put("buildingClli", site.getBuildingClli());
				}
			}
		}
	}
	
	public Map<Long, Map<String, String>> getClliValuesforAllSites(Offer offer) {
		Map<Long, Map<String, String>> clliMap = new HashMap<Long, Map<String, String>>();
		
		for(Site currentSite : offer.getSite()) {
			if("103".equalsIgnoreCase(offer.getOfferId())) {
				for (int k = 0; k < currentSite.getDesignSiteOfferPort().size(); k++) {
					for (int l = 0; l < currentSite.getDesignSiteOfferPort().get(k).getComponent().size(); l++) {
						String componentCodeType = currentSite.getDesignSiteOfferPort().get(k).getComponent().get(l)
								.getComponentCodeType();
						if (null != componentCodeType && "Port".equalsIgnoreCase(componentCodeType)) {
							List<UDFBaseData> udfList = currentSite.getDesignSiteOfferPort().get(k).getComponent().get(l)
									.getDesignDetails();
							clliMap.put(currentSite.getSiteId(), getCLLiValues(udfList));
						}
					}
				}
			}
			/*else if ("120".equalsIgnoreCase(offer.getOfferId())) {
				if(CollectionUtils.isNotEmpty(offer.getCircuit())) {
					List<Circuit> circuits = offer.getCircuit();
					for(Circuit circuit : circuits) {
						List<com.att.sales.nexxus.reteriveicb.model.Component> components =circuit.getComponent(); 
						for(com.att.sales.nexxus.reteriveicb.model.Component component : components) {
							if(Long.compare(1220L, component.getComponentCodeId()) == 0) {
								List<References> references = component.getReferences();
								for(References reference : references) {
									if("Site".equalsIgnoreCase(reference.getReferenceType()) && Long.compare(currentSite.getSiteId(), reference.getReferenceId()) == 0) {
										clliMap.put(currentSite.getSiteId(), getCLLiValues(component.getDesignDetails()));
									}
								}
							}
						}
					}
				}
			}*/
		}
		return clliMap;
	}
	
	protected Map<String, String> getCLLiValues(List<UDFBaseData> udfList) {
		Map<String, String> cLLiMap = null;
		if (CollectionUtils.isNotEmpty(udfList)) {
			cLLiMap = new HashMap<String, String>();
			Predicate<UDFBaseData> p = (obj) -> (obj.getUdfId() == 200045 || obj.getUdfId() == 20184 || obj.getUdfId() == 200160);
			List<UDFBaseData> udfdata = (List<UDFBaseData>) udfList.stream().filter(p).collect(Collectors.toList());
			// String swcLLi
			AtomicReference<String> swcLLiRef = new AtomicReference<String>();
			AtomicReference<String> buildingClliRef = new AtomicReference<String>();
			udfdata.stream().forEach(o -> {
				if (o.getUdfId() == 200045) {
					if (CollectionUtils.isNotEmpty(o.getUdfAttributeText())) {
						String swcLLi = o.getUdfAttributeText().get(0);
						swcLLiRef.set(swcLLi);
					}
				}
				if (o.getUdfId() == 20184 || o.getUdfId() == 200160) {
					if (CollectionUtils.isNotEmpty(o.getUdfAttributeText())) {
						String buildingClli = o.getUdfAttributeText().get(0);
						buildingClliRef.set(buildingClli);
					}
				}
			});
			cLLiMap.put("swcLLi", swcLLiRef.get());
			cLLiMap.put("buildingClli", buildingClliRef.get());
		}
		return cLLiMap;
	}
	public long getNxSiteId() { 
 		return ((BigDecimal) em.createNativeQuery("SELECT SEQ_NX_SITE_ID.NEXTVAL FROM DUAL").getSingleResult()).longValue(); 
 	}
	
	public NxMpDeal getNxMpDeal(List<NxMpDeal> nxMpDeal, Set<Object> asePrcieScenarioIds) {
		
		Set<Object> asePrcieIds = new HashSet<Object>();
		for(Object pricescenarios: asePrcieScenarioIds) {
			if(null != pricescenarios) {
				asePrcieIds.add(Long.parseLong(pricescenarios.toString()));
			}	
		 }
		asePrcieScenarioIds.clear();
		asePrcieScenarioIds.addAll(asePrcieIds);
//		for(NxMpDeal deal : nxMpDeal) {
//			if(deal.getPriceScenarioId().equals(solPriceScenarioId) && deal.getActiveYN().equalsIgnoreCase("Y")) {
//			  return deal;	
//			}
//		}
      	return nxMpDeal.stream().filter(deal -> CollectionUtils.isNotEmpty(asePrcieScenarioIds)
				&& asePrcieScenarioIds.contains(deal.getPriceScenarioId())).findAny().orElse(null);
	}
	
	public UpdateTransactionOverrideRequest prepareUpdateTransactionOverrideRequest(NxMpDeal newDeal, Map<String, Object> paramMap, Set<Object> designStatusIndicators, Offer offer, String offerName) {
		UpdateTransactionOverrideRequest request = new UpdateTransactionOverrideRequest();
		request.setMyPriceTransId(newDeal.getTransactionId());
		
		UpdateTransactionOverrideDocument document = new UpdateTransactionOverrideDocument();
		document.setVersion(newDeal.getVersion());
		document.setRevision(newDeal.getRevision());
		document.setCreateNewAdditionalRequest(true);
		document.setRequestId(newDeal.getDealID());
		long solutionVersion=paramMap.get(MyPriceConstants.SOLUTION_VERSION) != null ? (long) (paramMap.get(MyPriceConstants.SOLUTION_VERSION)):0;
		document.setWiSolutionVersionQ(solutionVersion);
		String isReconfiure = (String) paramMap.get("IS_RECONFIGURE");
		if(StringConstants.CONSTANT_N.equalsIgnoreCase(isReconfiure)) {
			try {
			String siteJson = getUpdatedSiteForRC(offerName, offer, newDeal);
			if(StringUtils.isNotEmpty(siteJson)) {
				document.setSiteAddress(siteJson);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		}
		//Need to send site address in case of add site scenario
		else if(null != designStatusIndicators) {
			try {
				String siteJson = getSiteDictionary(newDeal, designStatusIndicators, offer, offerName);
				if(StringUtils.isNotEmpty(siteJson)) {
					document.setSiteAddress(siteJson);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String source=(String) paramMap.get(MyPriceConstants.SOURCE);
		if (MyPriceConstants.SOURCE_PD.equalsIgnoreCase(source)) { 
			String path = "$..priceAttributes.[?(@.priceScenarioId=='priceScenarioIdValue')].term"; 
			path = path.replaceAll("priceScenarioIdValue", newDeal.getPriceScenarioId().toString()); 
			List<Object> terms = getTermValueFromRequest(paramMap.get("RequestSolution"), path); 
			if (CollectionUtils.isNotEmpty(terms)) { 
				if(terms.get(0) != null) {
					Long term = new Long(terms.get(0).toString()); 
					document.setWiContractTermQ(term);
					document.setWiContractTermUpdateQ(true);
				}
				terms = null; 
			} 
		}
		if(null != isReconfiure && isReconfiure.equalsIgnoreCase("inrReconfigure")) {
			Long iglooCount = (Long) paramMap.get("iglooCount");
		    NxSolutionDetail nxSolutionDetail = (paramMap.containsKey(StringConstants.SOLUTIONDETAILS) && null != paramMap.get(StringConstants.SOLUTIONDETAILS))? (NxSolutionDetail) paramMap.get(StringConstants.SOLUTIONDETAILS) : null;
			//if(paramMap.containsKey("ACTION_IND") && paramMap.get("ACTION_IND").toString().equalsIgnoreCase(StringConstants.APPEND_TO_OTHER_DEAL)) {
			if (paramMap.containsKey("ACTION_IND") && paramMap.get("ACTION_IND") != null
					&& StringConstants.APPEND_TO_OTHER_DEAL.equalsIgnoreCase(paramMap.get("ACTION_IND").toString())) {
				if (paramMap.containsKey(StringConstants.NEW_NX_SOL_ID)) {
					String nxSolId = paramMap.get(StringConstants.NEW_NX_SOL_ID).toString();
					Long solId = Long.parseLong(nxSolId);
					logger.info("newSolutionId::::{}",org.apache.commons.lang3.StringUtils.normalizeSpace(paramMap.get(StringConstants.NEW_NX_SOL_ID).toString()));
					nxSolutionDetail = nxSolutionDetailsRepository.findByNxSolutionId(solId);
				}
			}
			document.setExternalSolutionId(nxSolutionDetail.getNxSolutionId());
			List<NxRequestDetails> nxRequestDetails = nxRequestDetailsRepository.findbyNxSolutionIdAndActiveYnAndStatus(nxSolutionDetail, StringConstants.CONSTANT_Y, MyPriceConstants.INR_RC_REQUEST_STATUS);
			List<Long> requestGroupIds = null;
			if(CollectionUtils.isNotEmpty(nxRequestDetails)) {
				Set<Long> groupIds = nxRequestDetails.stream().map(nxRequestDetail-> nxRequestDetail.getNxRequestGroupId()).collect(Collectors.toSet());
				requestGroupIds = new ArrayList<>(groupIds);
			}
			if(CollectionUtils.isNotEmpty(requestGroupIds) || iglooCount.longValue() != 0) {
				logger.info("cleansave inrReconfigure for solution id : {}", newDeal.getSolutionId());
				long startTime = System.nanoTime();
				long endTime = System.nanoTime();
				String siteAddressJson = submitToMyPriceService.getSiteAddress(requestGroupIds, nxSolutionDetail, paramMap);
				long seconds = TimeUnit.NANOSECONDS.toSeconds(endTime-startTime);
				logger.debug("SRK:::submitToMyPriceService.getSiteAddress total run time = "+seconds);
				logger.info("SRK:::submitToMyPriceService.getSiteAddress total run time = "+seconds);
				if(StringUtils.isNotEmpty(siteAddressJson)) {
					
					document.setSiteAddress(siteAddressJson.replaceAll("[\\n\\t\\r]", ""));
					try {
						submitToMyPriceService.persistNxMpSiteDictionary(newDeal.getNxTxnId(), siteAddressJson, MyPriceConstants.SOURCE_INR);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}

				}
			}
			
			Clone clone = new Clone();
			clone.setAction(MyPriceConstants.NX_CLONE);
			clone.setSourceId(paramMap.get(StringConstants.MY_PRICE_TRANS_ID).toString());
			try {
				document.setWiOriginalClonedTxId(mapper.writeValueAsString(clone));
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// check if ip&e site exists
		if (paramMap.get("ACTION_IND") == null
				|| !paramMap.get("ACTION_IND").equals(StringConstants.APPEND_TO_OTHER_DEAL)) {
			NxMpSiteDictionary siteData = siteRepo.findByNxTxnId(newDeal.getNxTxnId());
			if (siteData.getSiteJson() != null) {
				logger.info(siteData.getSiteRefId() + " : setting site data for txnId : " + newDeal.getNxTxnId());
				try {
					document.setIntegrationSiteDict(updateTxnSiteUploadServiceImpl
							.translateSiteJsonRemoveDuplicatedNxSiteId(siteData.getSiteJson()));
				} catch (SalesBusinessException e) {
					e.printStackTrace();
				} // This holds ip&e site
				document.setWiUpdateOverrideQ(true);
			}
		}
		
		request.setDocuments(document);
		request.setNxAuditId((Long) paramMap.get(MyPriceConstants.NX_AUDIT_ID));
		request.setTransType((String) paramMap.get(MyPriceConstants.TRANSACTION_TYPE));
		return request;
	}
	public List<Object> getTermValueFromRequest(Object request, String path) { 
		TypeRef<List<Object>> mapType = new TypeRef<List<Object>>() { 
		}; 
		List<Object> results = jsonPathUtil.search(request, path, mapType); 
		if (CollectionUtils.isEmpty(results)) { 
			return null; 
		} else { 
			return new ArrayList<Object>(results); 
		} 
	}
	public UpdateTransactionOverrideRequest prepareUpdateTransactionOverrideRequestForIpne(NxMpDeal newDeal, Map<String, Object> paramMap, Set<Object> designStatusIndicators, Offer offer, String offerName,RetreiveICBPSPRequest retreiveICBPSPRequest) {
		UpdateTransactionOverrideRequest request = new UpdateTransactionOverrideRequest();
		request.setMyPriceTransId(newDeal.getTransactionId());
		UpdateTransactionOverrideDocument document = new UpdateTransactionOverrideDocument();
		document.setVersion(newDeal.getVersion());
		document.setRevision(newDeal.getRevision());
		document.setCreateNewAdditionalRequest(true);
		document.setRequestId(newDeal.getDealID());
		document.setWiSolutionVersionQ(retreiveICBPSPRequest.getSolution().getSolutionVersion());
		String isReconfiure = (String) paramMap.get("IS_RECONFIGURE");
		String siteDictString = null;
		List<Long> nxSiteIdList=new ArrayList<Long>();
		if(StringConstants.CONSTANT_N.equalsIgnoreCase(isReconfiure)) {
			String siteJson = getUpdatedSiteForRCForIpne(offerName, offer, newDeal,retreiveICBPSPRequest,paramMap,nxSiteIdList);
			siteDictString=siteJson;
			if(StringUtils.isNotEmpty(siteJson)) {
				document.setSiteAddress(siteJson);
			}
		}
		//Need to send site address in case of add site scenario
		else if(null != designStatusIndicators) {
			String siteJson = getSiteDictionaryForIpne(newDeal, designStatusIndicators, offer, offerName, retreiveICBPSPRequest,nxSiteIdList);
			siteDictString=siteJson;
			if(StringUtils.isNotEmpty(siteJson)) {
				document.setSiteAddress(siteJson);
			}
		}
		
		JSONArray list = new JSONArray(); 
		JSONArray siteDictArray = new JSONArray(siteDictString);
		if (siteDictArray != null) { 
			   for (int i = 0; i < siteDictArray.length(); i++)
			   { 
				   JSONObject siteDict = siteDictArray.getJSONObject(i);
				   Long nxSiteId=	(siteDict.has("nxSiteId") && !JSONObject.NULL.equals(siteDict.get("nxSiteId")))? siteDict.getLong("nxSiteId") : 0l;
				   if(CollectionUtils.isNotEmpty(nxSiteIdList) && nxSiteIdList.contains(nxSiteId)) {
					   list.put(siteDict);
				   }
			       
			   } 
		}
		document.setSiteAddress(list.toString());
		NxMpSiteDictionary nxMpSiteDictionary = siteRepo.findByNxTxnId(newDeal.getNxTxnId());
		nxMpSiteDictionary.setSiteAddress("{ \"siteAddress\" :  " + list.toString() + "}");
		nxMpSiteDictionaryRepository.save(nxMpSiteDictionary);
		
		// check if ip&e site exists
		NxMpSiteDictionary siteData = siteRepo.findByNxTxnId(newDeal.getNxTxnId());
		if(siteData.getSiteJson() != null) {
			logger.info(siteData.getSiteRefId()+" : setting site data for txnId : "+newDeal.getNxTxnId());
			try {
				document.setIntegrationSiteDict(updateTxnSiteUploadServiceImpl.translateSiteJsonRemoveDuplicatedNxSiteId(siteData.getSiteJson()));
			} catch (SalesBusinessException e) {
				e.printStackTrace();
			} // This holds ip&e site
			document.setWiUpdateOverrideQ(true);
		}
		
		request.setDocuments(document);
		request.setNxAuditId((Long) paramMap.get(MyPriceConstants.NX_AUDIT_ID));
		request.setTransType((String) paramMap.get(MyPriceConstants.TRANSACTION_TYPE));
		return request;
	}
	
	public void processMutliPriceScenario(RetreiveICBPSPRequest retreiveICBPSPRequest, NxSolutionDetail solutionData,
			Map < String, Object > paramMap) {
		logger.info("Start ::processMutliPriceScenario for nx_solution_id{} " +solutionData.getNxSolutionId());
	    String offerName = null;
	    paramMap.put(MyPriceConstants.RESPONSE_STATUS, true);
	    String myPriceTransId =  (String) paramMap.get(StringConstants.MY_PRICE_TRANS_ID);
	    String source =  (String) paramMap.get(MyPriceConstants.SOURCE);
        String transactionType = paramMap.containsKey(StringConstants.TRANSACTION_TYPE) ? paramMap.get(StringConstants.TRANSACTION_TYPE).toString() : null;
	    NxMpDeal newDeal = nxMpDealRepository.findByTransactionId(myPriceTransId);
	    Set <Object> priceScenarioIds = new LinkedHashSet <Object>();
	    List <Long> newPricesScenarioIds = new ArrayList<Long>();
		boolean isRestCall=false;
	    if (null != retreiveICBPSPRequest) {
	    	Long contractTerm = retreiveICBPSPRequest.getSolution().getContractTerm();
	    	String mpsIndicator = retreiveICBPSPRequest.getSolution().getMpsIndicator();
	    	logger.info("contractTerm Multi-price-scenario at solution level:==>> {} ", contractTerm);
	        if (null != retreiveICBPSPRequest.getSolution().getOffers()) {
	            for (Offer offer: retreiveICBPSPRequest.getSolution().getOffers()) {
	                String offerId = offer.getOfferId();
	                if (StringUtils.isNotEmpty(offerId)) {
	                    int id = Integer.parseInt(offerId);
	                    offerName = salesMsDao.getOfferNameByOfferId(id);
	                }
	                
	                if(StringConstants.TRANSACTION_TYPE_NEW.equalsIgnoreCase(transactionType)) {
			    		  if (StringUtils.isNotEmpty(offerName) && (StringConstants.OFFERNAME_ASE.equalsIgnoreCase(offerName) 
			    				  || StringConstants.OFFERNAME_ASENOD.equalsIgnoreCase(offerName))) {
			    			  if((StringConstants.OFFERNAME_ASE.equalsIgnoreCase(offerName) ||
										StringConstants.OFFERNAME_ASENOD.equalsIgnoreCase(offerName)) 
												&& "N".equalsIgnoreCase(retreiveICBPSPRequest.getSolution().getMpsIndicator())){
			    				  priceScenarioIds = getValuesFromRequest(offer, asePriceScenarioIdPath);
			    			  }else {
			    				  priceScenarioIds = getValuesFromRequest(offer, "Y".equalsIgnoreCase(retreiveICBPSPRequest.getSolution().getAutomationInd()) ? asePriceScenarioIdPath : asePriceModifiedIndPriceScenarioIdPath);
							  }
			    			  
			              }else if (StringUtils.isNotEmpty(offerName) && StringConstants.OFFERNAME_ADE.equalsIgnoreCase(offerName)) {
			                  priceScenarioIds = getValuesFromRequest(offer, "Y".equalsIgnoreCase(retreiveICBPSPRequest.getSolution().getAutomationInd()) ? adePriceScenarioIdPath : adeActivePriceScenarioIdPath);
			              }
		    		}else {
		    			boolean isDesignUpdate = (boolean) paramMap.get(StringConstants.CALLPEDFORDESIGNUPDATE);
			            if(isDesignUpdate) {
			            	 priceScenarioIds = paramMap.containsKey("priceScenarioIds") ? (HashSet<Object>) paramMap.get("priceScenarioIds") : new HashSet <Object>();
			            }else {
			            	priceScenarioIds = paramMap.containsKey("priceModifiedIndPriceScenarioIds") ? (HashSet<Object>) paramMap.get("priceModifiedIndPriceScenarioIds") : new HashSet <Object>();
			            }
		    		}
	            }
	            /*if(CollectionUtils.isNotEmpty(priceScenarioIds)) {
		            for(Object pricescenarios : priceScenarioIds) {
		    			if(null != pricescenarios) {
		    				priceScenarioIds.remove(pricescenarios);
		    				priceScenarioIds.add(Long.parseLong(pricescenarios.toString()));
		    			}
		    		}
	            }*/
	            
	            Map<String, Object> result = new HashMap<String, Object>();
	            List <Long> newNxTxnIds = new ArrayList<Long>();
	            Map<String, Long> nxAuditIds = new HashMap<String, Long>();
	            if (CollectionUtils.isNotEmpty(priceScenarioIds)) {
	            	Set<Long> newPSIds = priceScenarioIds.stream().map(ps -> Long.parseLong(ps.toString())).collect(Collectors.toSet());
	            	newPSIds.remove(newDeal.getPriceScenarioId());
	    	        //ordering of pricescenarioId in desc order for ADE    
	            	if (StringUtils.isNotEmpty(offerName) && !newPSIds.isEmpty()) {
	                	List<Long> newPSIdslist = newPSIds.stream().collect(Collectors.toList());
	                	Collections.sort(newPSIdslist, (o1, o2) -> Long.compare(o2,o1));
	                	Set<Long> sortedresultSet = new LinkedHashSet<Long>(newPSIdslist);
	                	newPSIds=sortedresultSet;
	                	logger.info("newPSIds in desc order for ADE: {}", newPSIds);
	                }
	                for (Long priceScenarioId: newPSIds) { 
                    	paramMap.put(StringConstants.PRICE_SCENARIO_ID, priceScenarioId.toString());
//                    	List<NxMpDeal> deals=nxMpDealRepository.findBySolutionIdAndActiveYNAndPriceScenarioId(solutionData.getNxSolutionId(), "Y", priceScenarioId);
//                    	if(CollectionUtils.isNotEmpty(deals)) {
//                    		paramMap.put(StringConstants.MY_PRICE_TRANS_ID,deals.get(0).getTransactionId());
//                    	}
                    	result = copyTransactionServiceImpl.processMutliPriceScenario(paramMap);
                    	boolean status = (boolean) result.get(StringConstants.STATUS);
                    	if(status) {
                    		NxMpDeal newNxMpDeal = (NxMpDeal) result.get(StringConstants.NEW_NX_TXN_OBJECT);
                    		sendDmaapEvents(newNxMpDeal,  solutionData , CommonConstants.CREATED, result);
                    		logger.info("Dmaap event created for pricescenarioId: " + priceScenarioId.toString());
                    		newPricesScenarioIds.add(priceScenarioId);
                    		newNxTxnIds.add((Long) result.get(StringConstants.NEW_NX_TXN_ID));
                    		
                			// audit for price scenario
                			Long nxAuditId = saveNxDesignAudit(solutionData.getNxSolutionId(), null != priceScenarioId ? String.valueOf(priceScenarioId) : null,
                					retreiveICBPSPRequest, CommonConstants.SUCCESS, MyPriceConstants.AUDIT_COPY_PRICE_SCENARIO);
                			
                			nxAuditIds.put(priceScenarioId.toString(), nxAuditId);
                			// call updatetransaction override
                			Map<String, Object> overrideReq = new HashMap<String, Object>();
                			overrideReq.put(StringConstants.TRANSACTION_UPDATE, StringConstants.TRANSACTION_UPDATE_PRICE);
                			overrideReq.put("RequestSolution", paramMap.get("RequestSolution")); 
							overrideReq.put(MyPriceConstants.SOURCE, paramMap.get(MyPriceConstants.SOURCE));
							overrideReq.put(MyPriceConstants.SOLUTION_VERSION, retreiveICBPSPRequest.getSolution().getSolutionVersion());
                			UpdateTransactionOverrideRequest utOverrideReq = prepareUpdateTransactionOverrideRequest(newNxMpDeal, overrideReq, null, null, null);
                			utOverrideReq.setNxAuditId(nxAuditId);
                			utOverrideReq.setTransType(MyPriceConstants.AUDIT_UPDATE_OR_PRICE_SCENARIO);
                			ServiceResponse overrideRes = updateTransactionOverrideImpl.updateTransactionOverride(utOverrideReq);
                			logger.info("Update Transaction override response : {} ", overrideRes.toString());
                			// if override fails stops
                			if(null != overrideRes && !CommonConstants.SUCCESS_STATUS.equals(overrideRes.getStatus().getCode())){
                				paramMap.put(MyPriceConstants.RESPONSE_STATUS, false);
                				return;
                			}
                			
                    	}else {
                    		NxMpDeal deal = new NxMpDeal();
                			deal.setPriceScenarioId(priceScenarioId);
                    		sendDmaapEvents(deal,  solutionData , CommonConstants.FAILED, result);
                    		logger.info("Dmaap event failed for pricescenarioId: " + priceScenarioId.toString());
                    		paramMap.put(MyPriceConstants.RESPONSE_STATUS, false);
                    		return;
                    	}
                    	result.remove(MyPriceConstants.RESPONSE_CODE);
                    	result.remove(MyPriceConstants.RESPONSE_DATA);
                    	result.remove(MyPriceConstants.RESPONSE_MSG);
	                }
	            }
	            for(Long nxTxnId :  newNxTxnIds) {
	            	for (Long priceScenarioId: newPricesScenarioIds) {
	            		NxMpDeal nxMpDeal = nxMpDealRepository.findByNxTxnIdAndPriceScenarioIdAndActiveYN(nxTxnId, priceScenarioId, CommonConstants.ACTIVE_Y);
	            		if(null != nxMpDeal) {
	            			for (Offer offer: retreiveICBPSPRequest.getSolution().getOffers()) {
	            				 String offerId = offer.getOfferId();
	         	                if (StringUtils.isNotEmpty(offerId)) {
	         	                    int id = Integer.parseInt(offerId);
	         	                    offerName = salesMsDao.getOfferNameByOfferId(id);
	         	                }
	         	               isRestCall=processPDtoMPRestUtil.isRESTEnabled(offerName,MyPriceConstants.SOURCE_PD);
	            				Map < String, Object > designMap = new HashMap < String, Object > ();
	            				if(paramMap.containsKey(InrConstants.REQUEST_META_DATA_KEY)) {
	            						Map<String, Object> requestMetaDataMap = (Map<String, Object>) paramMap.get(InrConstants.REQUEST_META_DATA_KEY);
	            						designMap.put(InrConstants.REQUEST_META_DATA_KEY, requestMetaDataMap);
	            				}
     	                        designMap.put(StringConstants.MY_PRICE_TRANS_ID, nxMpDeal.getTransactionId());
     	                        designMap.put(MyPriceConstants.NX_TRANSACTION_ID, nxTxnId); 
     	                        designMap.put(StringConstants.PRICE_SCENARIO_ID, priceScenarioId);
     	                        designMap.put(StringConstants.TRANSACTION_UPDATE, StringConstants.RECONFIGURE);
    	                        designMap.put(MyPriceConstants.OFFER_TYPE, offerName);
    	                        designMap.put(MyPriceConstants.SOURCE, source);
    	                        designMap.put(MyPriceConstants.MPS_INDICATOR, mpsIndicator);
    	                        if(offer.getPriceUpdate() != null)
    	                        	designMap.put(MyPriceConstants.PRICE_UPDATE, offer.getPriceUpdate());
    	                        if(contractTerm != null)
    	                        	designMap.put(MyPriceConstants.CONTRACT_TERM, String.valueOf(contractTerm));
    	                        logger.info("priceUpdate Multi-price-scenario :==>> {} ", offer.getPriceUpdate());
    	                        logger.info("contractTerm Multi-price-scenario :==>> {} ", contractTerm);
    	                        boolean status = false;
    	                        Map<String, Object> pricingStatus = new HashMap<String, Object>();
    	                        if (StringUtils.isNotEmpty(offerName) && (StringConstants.OFFERNAME_ASE.equalsIgnoreCase(offerName) || StringConstants.OFFERNAME_ASENOD.equalsIgnoreCase(offerName))) {
		     	                   /* for (Site site: offer.getSite()) {
		     	                    	Long nxDesignId = nxDesignRepository.findByAsrItemIdAndNxSolutionId(site.getAsrItemId(), nxMpDeal.getSolutionId());
		     	                    	if(!isRestCall) {
		     	                    		//soap call
			     	                    	designMap.put(MyPriceConstants.NX_DESIGN_ID, nxDesignId); 
			     	                    	pricingStatus = callUpdatePricing(designMap);
			     	                    }else {
			     	                    	//rest call
			     	                    	List<Long> designIdForUpDatePricing= new ArrayList<>();
			     	                    	designIdForUpDatePricing.add(nxDesignId);
			     	                    	designMap.put(MyPriceConstants.NX_DESIGN_ID,designIdForUpDatePricing);
			     	                    	pricingStatus=processPDtoMPRestUtil.callUpdatePricing(designMap, MyPriceConstants.AUDIT_UPDATE_PR_REST_RC);
				     	                }
		     	                    	pricingStatus.put(MyPriceConstants.ASR_ITEM_ID, site.getAsrItemId());
		     	                    	pricingStatus.put(MyPriceConstants.NX_SITE_ID, site.getSiteId());
		     	                    	status = (boolean) pricingStatus.get(MyPriceConstants.RESPONSE_STATUS);
		     	                    	if(!status)
		     	                    		break;
		     	                    }*/
    	                        	if (!isRestCall) {
    	                        		//soap call
										for (Site site : offer.getSite()) {
											Long nxDesignId = nxDesignRepository.findByAsrItemIdAndNxSolutionId(
													site.getAsrItemId(), nxMpDeal.getSolutionId());
											designMap.put(MyPriceConstants.NX_DESIGN_ID, nxDesignId);
											if(!"C".equalsIgnoreCase(site.getDesignStatus())) {
												pricingStatus = callUpdatePricing(designMap);
												status = (boolean) pricingStatus.get(MyPriceConstants.RESPONSE_STATUS);
											}else {
												status = true;
											}
											pricingStatus.put(MyPriceConstants.ASR_ITEM_ID, site.getAsrItemId());
											pricingStatus.put(MyPriceConstants.NX_SITE_ID, site.getSiteId());											
											if (!status)
												break;
										}
									} else {
										// rest call
										List<String> asrItemIds = offer.getSite().stream().filter(s-> !"C".equalsIgnoreCase(s.getDesignStatus())).map(Site::getAsrItemId)
												.collect(Collectors.toList());
										if (CollectionUtils.isNotEmpty(asrItemIds)) {
											logger.info("updatePricng calling for :==>> {} ", asrItemIds);
											List<Long> designIdForUpDatePricing = nxDesignRepository
													.findByNxSolutionIdAndMultipleAsrItemId(nxMpDeal.getSolutionId(),
															asrItemIds);
											designMap.put(MyPriceConstants.NX_DESIGN_ID, designIdForUpDatePricing);
											pricingStatus = processPDtoMPRestUtil.callUpdatePricing(designMap,
													MyPriceConstants.AUDIT_UPDATE_PR_REST_RC);
											status = (boolean) pricingStatus.get(MyPriceConstants.RESPONSE_STATUS);
										}else {
											status = true;
										}

									}
	     	                    }else {
	     	                    	/*Long circuitId = null;
		     	                    for(Circuit circuit : offer.getCircuit()) {
		     	                    	JsonNode circuitJson = mapper.valueToTree(circuit);
		     	                    	String asrItemId = pedSnsdServiceUtil.findAdeAsrItemId(circuitJson);
		     	                    	Long nxDesignId = nxDesignRepository.findByAsrItemIdAndNxSolutionId(asrItemId, nxMpDeal.getSolutionId());
		     	                    	if(!"C".equalsIgnoreCase(circuit.getDesignStatus())) {
		     	                    		if(!isRestCall) {
		     	                    			//soap call
		     	                    			designMap.put(MyPriceConstants.NX_DESIGN_ID, nxDesignId); 
			     	                    		pricingStatus = callUpdatePricing(designMap);
		     	                    		}else {
		     	                    			//rest call
		     	                    			List<Long> designIdForUpDatePricing= new ArrayList<>();
				     	                    	designIdForUpDatePricing.add(nxDesignId);
				     	                    	designMap.put(MyPriceConstants.NX_DESIGN_ID,designIdForUpDatePricing);
				     	                    	pricingStatus=processPDtoMPRestUtil.callUpdatePricing(designMap, MyPriceConstants.AUDIT_UPDATE_PR_REST_RC);
					     	                }
		     	                    	
		     	                    	
		     	                    	} else {
		     	                    		pricingStatus.put(MyPriceConstants.RESPONSE_STATUS, true);
		     	                    	}
		     	                    	pricingStatus.put(MyPriceConstants.ASR_ITEM_ID, asrItemId);
		     	                    	JsonNode componentCodeTypeNode = circuitJson.path("componentCodeType");
		     							if("Circuit".equalsIgnoreCase(componentCodeTypeNode.asText())) {
		     								circuitId = circuitJson.path("componentId").asLong();
		     							}
		     	                    	pricingStatus.put(MyPriceConstants.NX_SITE_ID, circuitId);
		     	                    	status = (boolean) pricingStatus.get(MyPriceConstants.RESPONSE_STATUS);
		     	                    	if(!status)
		     	                    		break;
		     	                    }*/
	     	                    	if (!isRestCall) {
	     	                    		//soap call
										Long circuitId = null;
										for (Circuit circuit : offer.getCircuit()) {
											JsonNode circuitJson = mapper.valueToTree(circuit);
											String asrItemId = pedSnsdServiceUtil.findAdeAsrItemId(circuitJson);
											Long nxDesignId = nxDesignRepository.findByAsrItemIdAndNxSolutionId(
													asrItemId, nxMpDeal.getSolutionId());
											if (!"C".equalsIgnoreCase(circuit.getDesignStatus())) {
												designMap.put(MyPriceConstants.NX_DESIGN_ID, nxDesignId);
												pricingStatus = callUpdatePricing(designMap);

											} else {
												pricingStatus.put(MyPriceConstants.RESPONSE_STATUS, true);
											}
											pricingStatus.put(MyPriceConstants.ASR_ITEM_ID, asrItemId);
											JsonNode componentCodeTypeNode = circuitJson.path("componentCodeType");
											if ("Circuit".equalsIgnoreCase(componentCodeTypeNode.asText())) {
												circuitId = circuitJson.path("componentId").asLong();
											}
											pricingStatus.put(MyPriceConstants.NX_SITE_ID, circuitId);
											status = (boolean) pricingStatus.get(MyPriceConstants.RESPONSE_STATUS);
											if (!status)
												break;
										}
									} else {
										// rest call
										List<String> asrItemIds = new ArrayList<String>();
										List<Circuit> circuitLst = offer.getCircuit().stream()
												.filter(i -> !"C".equalsIgnoreCase(i.getDesignStatus()))
												.collect(Collectors.toList());
										if (CollectionUtils.isNotEmpty(circuitLst)) {
											JsonNode allCircuitJson = mapper.valueToTree(circuitLst);
											for (JsonNode circuitJson : allCircuitJson) {
												String asrItemId = pedSnsdServiceUtil.findAdeAsrItemId(circuitJson);
												asrItemIds.add(asrItemId);
											}
											logger.info("updatePricng calling for :==>> {} ", asrItemIds);
											List<Long> designIdForUpDatePricing = nxDesignRepository
													.findByNxSolutionIdAndMultipleAsrItemId(nxMpDeal.getSolutionId(),
															asrItemIds);
											designMap.put(MyPriceConstants.NX_DESIGN_ID, designIdForUpDatePricing);
											pricingStatus = processPDtoMPRestUtil.callUpdatePricing(designMap,
													MyPriceConstants.AUDIT_UPDATE_PR_REST_RC);
										}else {
											pricingStatus.put(MyPriceConstants.RESPONSE_STATUS, true);
										}
										status = (boolean) pricingStatus.get(MyPriceConstants.RESPONSE_STATUS);
									}
	     	                    	
		     	                }
	     	                    pricingStatus.put(MyPriceConstants.NX_AUDIT_ID, nxAuditIds.get(priceScenarioId.toString()));
    	                    	pricingStatus.put(MyPriceConstants.MYPRICE_DESIGN, "design");
    	                    	pricingStatus.put(MyPriceConstants.TRANSACTION_TYPE, MyPriceConstants.AUDIT_UPDATE_PR_PRICE_SCENARIO);
	     	                    if(status) {
	     	                        sendDmaapEvents(getNxMpDeal((Long) designMap.get(MyPriceConstants.NX_TRANSACTION_ID)), getNxSolutionDetails((Long) designMap.get(MyPriceConstants.NX_TRANSACTION_ID)), CommonConstants.SUBMITTED, pricingStatus);
	     	                    } else {
	     	                    	sendDmaapEvents(getNxMpDeal((Long) designMap.get(MyPriceConstants.NX_TRANSACTION_ID)), getNxSolutionDetails((Long) designMap.get(MyPriceConstants.NX_TRANSACTION_ID)), CommonConstants.FAILED, pricingStatus);
	     	                    	paramMap.put(MyPriceConstants.RESPONSE_STATUS, false);
	     	                    	return;
	     	                    }
	     	                }
	            		}
	            	}
	            }
	        }
	    }
	    logger.info("End processMutliPriceScenario");
	    
	}
	
	public void deleteSiteFromSiteDic(Long nxTxnId, List<Long> nxSiteIds) {
		try {
			NxMpSiteDictionary nxMpSiteDictionary = nxMpSiteDictionaryRepository.findByNxTxnId(nxTxnId);
			JSONObject siteAddress = new JSONObject(nxMpSiteDictionary.getSiteAddress());
			JSONArray siteArray = new JSONArray(siteAddress.get("siteAddress").toString());
			for (int i = 0; i < siteArray.length(); i++) {
				JSONObject site = siteArray.getJSONObject(i);
				Long siteId = Long.valueOf((Integer) site.get("nxSiteId"));
				if (nxSiteIds.contains(siteId)) {
					siteArray.remove(i);
				}
			}
			String updatedSiteJson = siteArray.toString();
			logger.info("Remove Transaction for design cancle scenario : updated site json {}", updatedSiteJson);
			nxMpSiteDictionary.setSiteAddress("{ \"siteAddress\" :  " + updatedSiteJson + "}");
			nxMpSiteDictionaryRepository.save(nxMpSiteDictionary);
		} catch (Exception e) {
			logger.info("Exception", e);
		}
	}
	
	public NxMpDeal getNxMpDeal(Long nxTxnId) {
		return nxMpDealRepository.findByNxTxnId(nxTxnId);
	}
	
	public NxSolutionDetail getNxSolutionDetails(Long nxTxnId) {
		NxMpDeal nxMpDeal = getNxMpDeal(nxTxnId);
		NxSolutionDetail nxSolutionDetail = nxSolutionDetailsRepository.findByNxSolutionId(nxMpDeal.getSolutionId());
		return nxSolutionDetail;
	}
	
	public void sendDmaapEvents(NxMpDeal deal, NxSolutionDetail solution, String status) {
		
		Map<String,Object> inputmap=new HashMap<>();
		if(null != deal) {
			inputmap.put(com.att.sales.nexxus.constant.CommonConstants.AUDIT_ID,Long.valueOf(deal.getTransactionId()));
		} else {
			inputmap.put(com.att.sales.nexxus.constant.CommonConstants.AUDIT_ID,Long.valueOf(solution.getNxSolutionId()));
		}
		inputmap.put(com.att.sales.nexxus.constant.CommonConstants.AUDIT_TRANSACTION,
				com.att.sales.nexxus.constant.CommonConstants.AUDIT_TRANSACTION_CONSTANTS.valueOf(status).getValue());
		RateLetterStatusRequest request = new RateLetterStatusRequest();
		if(null != deal) {
			request.setDealId(deal.getDealID());
			request.setDealVersion(deal.getVersion());
			request.setDealRevisionNumber(deal.getRevision());
			request.setPriceScenarioId(deal.getPriceScenarioId());
			request.setCpqId(deal.getTransactionId());

				
		}
		request.setDealStatus(status);
		request.setExternalSystemKey(solution.getExternalKey().toString());
		request.setOptyId(solution.getOptyId());
	//	request.setExternalSystemName(solution.getExternalKeyName());
	//	request.setPriceScenarioId(solution.getPriceScenarioId());
		request.setCustomerName(solution.getCustomerName());
		
		dmaapPublishEventsService.triggerDmaapEventForMyprice(request, inputmap);
	}
	

	public void sendDmaapEvents(NxMpDeal deal, NxSolutionDetail solution, String status, Map<String, Object> apiResponse) {
		Map<String,Object> inputmap=new HashMap<>();
		if(null != deal) {
			inputmap.put(com.att.sales.nexxus.constant.CommonConstants.AUDIT_ID,Long.valueOf(deal.getTransactionId()));
		} else {
			inputmap.put(com.att.sales.nexxus.constant.CommonConstants.AUDIT_ID,Long.valueOf(solution.getNxSolutionId()));
		}
		inputmap.put(com.att.sales.nexxus.constant.CommonConstants.AUDIT_TRANSACTION,
				com.att.sales.nexxus.constant.CommonConstants.AUDIT_TRANSACTION_CONSTANTS.valueOf(status).getValue());
		RateLetterStatusRequest request = new RateLetterStatusRequest();
		if(null != deal) {
			request.setDealId(deal.getDealID());
			request.setDealVersion(deal.getVersion());
			request.setDealRevisionNumber(deal.getRevision());
			request.setPriceScenarioId(deal.getPriceScenarioId());
			request.setCpqId(deal.getTransactionId());
		}
		if(StringConstants.SALES_IPNE.equalsIgnoreCase(solution.getFlowType())) {
			request.setIpeIndicator(StringConstants.CONSTANT_Y);
		}
		request.setDealStatus(status);
		if(Optional.ofNullable(solution.getExternalKey()).isPresent()) {
			request.setExternalSystemKey(solution.getExternalKey().toString());
		}
		request.setOptyId(solution.getOptyId());
	//	request.setExternalSystemName(solution.getExternalKeyName());
		request.setCustomerName(solution.getCustomerName());
		String type = apiResponse.containsKey(MyPriceConstants.MYPRICE_DESIGN) ? (String) apiResponse.get(MyPriceConstants.MYPRICE_DESIGN) : null;
		if(CommonConstants.FAILED.equalsIgnoreCase(status)) {
			NexxusMessage message = new NexxusMessage();
			NexxusResponse response = new NexxusResponse();
			
			/** if type is design then populate designDetails
			 * else response
			 */
			if(MyPriceConstants.MYPRICE_DESIGN.equalsIgnoreCase(type)) {
				long nxAuditId = apiResponse.containsKey(MyPriceConstants.NX_AUDIT_ID) ? (long) apiResponse.get(MyPriceConstants.NX_AUDIT_ID) : 0;
				String transType = (apiResponse.containsKey(MyPriceConstants.TRANSACTION_TYPE) && null != apiResponse.get(MyPriceConstants.TRANSACTION_TYPE)) ? 
						(String) apiResponse.get(MyPriceConstants.TRANSACTION_TYPE) : "MP_CONFIG";
				
				updateNxMpDealMpInd(StringConstants.CONSTANT_N, deal.getNxTxnId());
				if(nxAuditId != 0)
					updateNxDesignAuditStatus(CommonConstants.FAILURE, transType, nxAuditId);
			/*	String asrItemId =apiResponse.containsKey(MyPriceConstants.ASR_ITEM_ID)?(String) apiResponse.get(MyPriceConstants.ASR_ITEM_ID):"";
				Long siteOrCircuitId = (apiResponse.containsKey(MyPriceConstants.NX_SITE_ID) && null != apiResponse.get(MyPriceConstants.NX_SITE_ID)) ? (Long) apiResponse.get(MyPriceConstants.NX_SITE_ID) : 0;
				
				List<DesignDetails> designDetails = new ArrayList<DesignDetails>();
				DesignDetails designDetail = new DesignDetails();
				designDetail.setAsrItemId(asrItemId);
				designDetail.setId(String.valueOf(siteOrCircuitId));
				List<ValidationIssues> validationIssues = new ArrayList<ValidationIssues>();
				ValidationIssues validationIssue = new ValidationIssues();
				if(apiResponse.containsKey(MyPriceConstants.RESPONSE_DATA) && null != apiResponse.get(MyPriceConstants.RESPONSE_DATA)) {
					validationIssue = JacksonUtil.fromString((String) apiResponse.get(MyPriceConstants.RESPONSE_DATA), ValidationIssues.class);
				}
				validationIssues.add(validationIssue);
				designDetail.setValidationIssues(validationIssues);
				designDetails.add(designDetail);
				message.setDesignDetails(designDetails);*/
				
				List<DesignDetails> designDetails = new ArrayList<DesignDetails>();
				Map<String,String> allAsrSiteIdMap =
						apiResponse.containsKey(MyPriceConstants.NX_SITE_ID_ASR_ITEM_ID_MAP)?(Map<String,String>)apiResponse.get(MyPriceConstants.NX_SITE_ID_ASR_ITEM_ID_MAP):null;
				if(allAsrSiteIdMap!=null && MapUtils.isNotEmpty(allAsrSiteIdMap)) {
					for(Map.Entry<String,String> asrSiteIdMap: allAsrSiteIdMap.entrySet()) {
						String nxSiteId=String.valueOf(asrSiteIdMap.getValue());
						String asrItemId =asrSiteIdMap.getKey();
						DesignDetails designDetail = new DesignDetails();
						designDetail.setAsrItemId(asrItemId);
						designDetail.setId(nxSiteId);
						List<ValidationIssues> validationIssues = new ArrayList<ValidationIssues>();
						ValidationIssues validationIssue = new ValidationIssues();
						if(apiResponse.containsKey(MyPriceConstants.RESPONSE_DATA) && null != apiResponse.get(MyPriceConstants.RESPONSE_DATA)) {
							validationIssue = JacksonUtil.fromString((String) apiResponse.get(MyPriceConstants.RESPONSE_DATA), ValidationIssues.class);
						}
						validationIssues.add(validationIssue);
						designDetail.setValidationIssues(validationIssues);
						designDetails.add(designDetail);
					}
				}
				message.setDesignDetails(designDetails);
			}else {
				if(apiResponse.containsKey(MyPriceConstants.RESPONSE_DATA) && null != apiResponse.get(MyPriceConstants.RESPONSE_DATA))
					response = JacksonUtil.fromString((String) apiResponse.get(MyPriceConstants.RESPONSE_DATA), NexxusResponse.class);
			}
			if(apiResponse.containsKey(MyPriceConstants.RESPONSE_CODE)) {
				int code = (int) apiResponse.get(MyPriceConstants.RESPONSE_CODE);
				if(code != 0)
					response.setCode(code);
			}
			if(apiResponse.containsKey(MyPriceConstants.RESPONSE_MSG)) {
				response.setDescription((String) apiResponse.get(MyPriceConstants.RESPONSE_MSG));
			}
			message.setResponse(response);
			request.setMessage(message);
		}else {
			if(MyPriceConstants.MYPRICE_DESIGN.equalsIgnoreCase(type)) {
				long nxAuditId = apiResponse.containsKey(MyPriceConstants.NX_AUDIT_ID) ? (long) apiResponse.get(MyPriceConstants.NX_AUDIT_ID) : 0;
				String transType = (apiResponse.containsKey(MyPriceConstants.TRANSACTION_TYPE) && null != apiResponse.get(MyPriceConstants.TRANSACTION_TYPE)) ? 
						(String) apiResponse.get(MyPriceConstants.TRANSACTION_TYPE) : "MP_CONFIG";
				if(nxAuditId != 0)
					updateNxDesignAuditStatus(CommonConstants.SUCCESS, transType, nxAuditId);
			}
		}
		dmaapPublishEventsService.triggerDmaapEventForMyprice(request, inputmap);
	}
	
	public String getUpdatedSiteForRC (String offerName, Offer offer, NxMpDeal deal) throws JSONException {
		String siteAddressJson = null;
		NxMpSiteDictionary siteData = siteRepo.findByNxTxnId(deal.getNxTxnId());
		if (StringUtils.isNotEmpty(offerName) && (StringConstants.OFFERNAME_ASE.equalsIgnoreCase(offerName) || StringConstants.OFFERNAME_ASENOD.equalsIgnoreCase(offerName))) {
			JSONObject siteDictAddress = new JSONObject(siteData.getSiteAddress());
			JSONArray siteDictArray = new JSONArray(siteDictAddress.get("siteAddress").toString());
			for(Site site : offer.getSite()) {
				for (int i = 0; i < siteDictArray.length(); i++) {
					JSONObject siteDict = siteDictArray.getJSONObject(i);
					String siteName = (siteDict.has("name") && !JSONObject.NULL.equals(siteDict.get("name")))? siteDict.getString("name") : null;
					String address = (siteDict.has("addressLine") && !JSONObject.NULL.equals(siteDict.get("addressLine")))? siteDict.getString("addressLine") : null;
					String city = (siteDict.has("city") && !JSONObject.NULL.equals(siteDict.get("city")))? siteDict.getString("city") : null;
					String state = (siteDict.has("state") && !JSONObject.NULL.equals(siteDict.get("state")))? siteDict.getString("state") : null;
					String zipCode = (siteDict.has("postalCode") && !JSONObject.NULL.equals(siteDict.get("postalCode")))? siteDict.getString("postalCode") : null;
					String country = (siteDict.has("country") && !JSONObject.NULL.equals(siteDict.get("country")))? siteDict.getString("country") : null;
					String address2 = Strings.isNullOrEmpty(site.getAddress2()) ? "" : site.getAddress2();
					String addressLine = site.getAddress1() + " " + address2;
					if(StringUtils.equals(siteName, site.getSiteName()) && StringUtils.equals(address, addressLine)
							&& StringUtils.equals(city, site.getCity()) && StringUtils.equals(state, site.getState())
							&& StringUtils.equals(zipCode, site.getZipCode()) && StringUtils.equals(country, site.getCountry())) {
						site.setNxSiteId((siteDict.has("nxSiteId") && !JSONObject.NULL.equals(siteDict.get("nxSiteId")))? siteDict.getLong("nxSiteId") : 0l);
						siteDictArray.remove(i);
					}
				}
			}
			if(siteDictArray.length() > 0)
				siteAddressJson = siteDictArray.toString();
		}
		else if (StringUtils.isNotEmpty(offerName) && StringConstants.OFFERNAME_ADE.equalsIgnoreCase(offerName)) {
			Map<Long, Site> siteMap = new HashMap<>();
			for(Site site : offer.getSite()) {
				if (site.getSiteId() != null) {
					siteMap.put(site.getSiteId(), site);
				}
			}
			JSONObject siteDictAddress = new JSONObject(siteData.getSiteAddress());
			JSONArray siteDictArray = new JSONArray(siteDictAddress.get("siteAddress").toString());
			for(Circuit circuit : offer.getCircuit()) {
				List<com.att.sales.nexxus.reteriveicb.model.Component> components = circuit.getComponent().stream().filter(comp -> comp.getComponentCodeId().equals(Long.parseLong(StringConstants.COMPONENTID_ENDPOINT))).collect(Collectors.toList());
				for(com.att.sales.nexxus.reteriveicb.model.Component component : components) {
					Long referenceId = component.getReferences().get(0).getReferenceId();
					Site site = null;
					if (referenceId != null) {
						site = siteMap.get(referenceId);
					}
					
					for (int i = 0; i < siteDictArray.length(); i++) {
						JSONObject siteDict = siteDictArray.getJSONObject(i);
						String siteName = (siteDict.has("name") && !JSONObject.NULL.equals(siteDict.get("name")))? siteDict.getString("name") : null;
						String address = (siteDict.has("addressLine") && !JSONObject.NULL.equals(siteDict.get("addressLine")))? siteDict.getString("addressLine") : null;
						String city = (siteDict.has("city") && !JSONObject.NULL.equals(siteDict.get("city")))? siteDict.getString("city") : null;
						String state = (siteDict.has("state") && !JSONObject.NULL.equals(siteDict.get("state")))? siteDict.getString("state") : null;
						String zipCode = (siteDict.has("postalCode") && !JSONObject.NULL.equals(siteDict.get("postalCode")))? siteDict.getString("postalCode") : null;
						String country = (siteDict.has("country") && !JSONObject.NULL.equals(siteDict.get("country")))? siteDict.getString("country") : null;
						String address2=Strings.isNullOrEmpty(site.getAddress2()) ? "" : site.getAddress2();
						String addressLine = site.getAddress1() + " " +address2;
						if(StringUtils.equals(siteName, site.getSiteName()) && StringUtils.equals(address, addressLine)
								&& StringUtils.equals(city, site.getCity()) && StringUtils.equals(state, site.getState())
								&& StringUtils.equals(zipCode, site.getZipCode()) && StringUtils.equals(country, site.getCountry())) {
							site.setNxSiteId((siteDict.has("nxSiteId") && !JSONObject.NULL.equals(siteDict.get("nxSiteId")))? siteDict.getLong("nxSiteId") : 0l);
							siteDictArray.remove(i);
							for(Site siteObj : offer.getSite()) {
								if (siteObj.getSiteId() != null &&siteObj.getSiteId().equals(site.getSiteId())) {
									siteObj.setNxSiteId(site.getNxSiteId());
								}
							}
						}
					}
				}
			}
			if(siteDictArray.length() > 0)
				siteAddressJson = siteDictArray.toString();
		}
		else if (StringUtils.isNotEmpty(offerName) && StringConstants.OFFERNAME_EPLSWAN.equalsIgnoreCase(offerName)) {
			Map<Long, Site> siteMap = new HashMap<>();
			for(Site site : offer.getSite()) {
				if (site.getSiteId() != null) {
					siteMap.put(site.getSiteId(), site);
				}
			}
			JSONObject siteDictAddress = new JSONObject(siteData.getSiteAddress());
			JSONArray siteDictArray = new JSONArray(siteDictAddress.get("siteAddress").toString());
			for(Circuit circuit : offer.getCircuit()) {
				List<com.att.sales.nexxus.reteriveicb.model.Component> components = circuit.getComponent().stream().filter(comp -> comp.getComponentCodeId().equals(Long.parseLong(StringConstants.COMPONENTID_ENDPOINT))).collect(Collectors.toList());
				for(com.att.sales.nexxus.reteriveicb.model.Component component : components) {
					Long referenceId = component.getReferences().get(0).getReferenceId();
					Site site = null;
					if (referenceId != null) {
						site = siteMap.get(referenceId);
					}
					
					for (int i = 0; i < siteDictArray.length(); i++) {
						JSONObject siteDict = siteDictArray.getJSONObject(i);
						String siteName = (siteDict.has("name") && !JSONObject.NULL.equals(siteDict.get("name")))? siteDict.getString("name") : null;
						String address = (siteDict.has("addressLine") && !JSONObject.NULL.equals(siteDict.get("addressLine")))? siteDict.getString("addressLine") : null;
						String city = (siteDict.has("city") && !JSONObject.NULL.equals(siteDict.get("city")))? siteDict.getString("city") : null;
						String state = (siteDict.has("state") && !JSONObject.NULL.equals(siteDict.get("state")))? siteDict.getString("state") : null;
						String zipCode = (siteDict.has("postalCode") && !JSONObject.NULL.equals(siteDict.get("postalCode")))? siteDict.getString("postalCode") : null;
						String country = (siteDict.has("country") && !JSONObject.NULL.equals(siteDict.get("country")))? siteDict.getString("country") : null;
						String address2=Strings.isNullOrEmpty(site.getAddress2()) ? "" : site.getAddress2();
						String addressLine = site.getAddress1() + " " +address2;
						if(StringUtils.equalsIgnoreCase(siteName, site.getSiteName()) && StringUtils.equalsIgnoreCase(address, addressLine)
								&& StringUtils.equalsIgnoreCase(city, site.getCity()) && StringUtils.equalsIgnoreCase(state, site.getState())
								&& StringUtils.equalsIgnoreCase(zipCode, site.getZipCode()) && StringUtils.equalsIgnoreCase(country, site.getCountry())) {
							site.setNxSiteId((siteDict.has("nxSiteId") && !JSONObject.NULL.equals(siteDict.get("nxSiteId")))? siteDict.getLong("nxSiteId") : 0l);
//							siteDictArray.remove(i);
							for(Site siteObj : offer.getSite()) {
								if (siteObj.getSiteId() != null &&siteObj.getSiteId().equals(site.getSiteId())) {
									siteObj.setNxSiteId(site.getNxSiteId());
								}
							}
						}
					}
				}
			}
			if(siteDictArray.length() > 0)
				siteAddressJson = siteDictArray.toString();
		}
		return siteAddressJson;
	}
	public String getUpdatedSiteForRCForIpne(String offerName, Offer offer, NxMpDeal deal,RetreiveICBPSPRequest retreiveICBPSPRequest,Map<String, Object> paramMap,List<Long> nxSiteIdList) {
		String siteAddressJson = null;
		String sourceName= (String) paramMap.get("sourceName");
		NxMpSiteDictionary siteData = siteRepo.findByNxTxnId(deal.getNxTxnId());
		if(StringUtils.isNotEmpty(offerName) && (StringConstants.OFFERNAME_ADE.equalsIgnoreCase(offerName)||StringConstants.OFFERNAME_EPLSWAN.equalsIgnoreCase(offerName))) {
			JSONObject siteDictAddress = new JSONObject(siteData.getSiteAddress());
			JSONArray siteDictArray = new JSONArray(siteDictAddress.get("siteAddress").toString());
			for(Offer offerObj : retreiveICBPSPRequest.getSolution().getOffers()) {
				Map<Long, Site> siteMap = new HashMap<>();
				for(Site site : offerObj.getSite()) {
					if (site.getSiteId() != null) {
						siteMap.put(site.getSiteId(), site);
					}
				}
				for(Circuit circuit : offerObj.getCircuit()) {
					List<com.att.sales.nexxus.reteriveicb.model.Component> components = circuit.getComponent().stream().filter(comp -> comp.getComponentCodeId().equals(Long.parseLong(StringConstants.COMPONENTID_ENDPOINT))).collect(Collectors.toList());
					for(com.att.sales.nexxus.reteriveicb.model.Component component : components) {
						Long referenceId = component.getReferences().get(0).getReferenceId();
						Site site = null;
						if (referenceId != null) {
							site = siteMap.get(referenceId);
						}
						
						for (int i = 0; i < siteDictArray.length(); i++) {
							JSONObject siteDict = siteDictArray.getJSONObject(i);
							String siteName = (siteDict.has("name") && !JSONObject.NULL.equals(siteDict.get("name")))? siteDict.getString("name") : null;
							String address = (siteDict.has("addressLine") && !JSONObject.NULL.equals(siteDict.get("addressLine")))? siteDict.getString("addressLine") : null;
							String city = (siteDict.has("city") && !JSONObject.NULL.equals(siteDict.get("city")))? siteDict.getString("city") : null;
							String state = (siteDict.has("state") && !JSONObject.NULL.equals(siteDict.get("state")))? siteDict.getString("state") : null;
							String zipCode = (siteDict.has("postalCode") && !JSONObject.NULL.equals(siteDict.get("postalCode")))? siteDict.getString("postalCode") : null;
							String country = (siteDict.has("country") && !JSONObject.NULL.equals(siteDict.get("country")))? siteDict.getString("country") : null;
							String address2=Strings.isNullOrEmpty(site.getAddress2()) ? "" : site.getAddress2();
							String addressLine = site.getAddress1() + " " +address2;
							if(StringUtils.equalsIgnoreCase(siteName, site.getSiteName()) && StringUtils.equalsIgnoreCase(address, addressLine)
									&& StringUtils.equalsIgnoreCase(city, site.getCity()) && StringUtils.equalsIgnoreCase(state, site.getState())
									&& StringUtils.equalsIgnoreCase(zipCode, site.getZipCode()) && StringUtils.equalsIgnoreCase(country, site.getCountry())) {
								site.setNxSiteId((siteDict.has("nxSiteId") && !JSONObject.NULL.equals(siteDict.get("nxSiteId")))? siteDict.getLong("nxSiteId") : 0l);
//								siteDictArray.remove(i);
								for(Site siteObj : offerObj.getSite()) {
									if (siteObj.getSiteId() != null &&siteObj.getSiteId().equals(site.getSiteId())) {
										siteObj.setNxSiteId(site.getNxSiteId());
										nxSiteIdList.add(site.getNxSiteId());
									}
								}
							}
						}
					}
				}
			}
			if(siteDictArray.length() > 0)
				siteAddressJson = siteDictArray.toString();
		}
		return siteAddressJson;
	}


	public boolean  getAutomationFlowInd(Solution solution) {
		boolean automationInd=false;
		try {
			if(StringUtils.isNotEmpty(solution.getAutomationInd()) && 
					solution.getAutomationInd().equalsIgnoreCase("Y")) {
				outerloop:
					for(Offer offer:solution.getOffers()) {
						JsonNode inputData=null;
						String datasetName=MyPriceConstants.NSS_ENGAGEMENT_DATASET;
						String jsonPath=null;
						if(offer.getOfferId().equals(TDDConstants.ASE_OFFER_ID)) {
							inputData=JacksonUtil.jsonNodeFromObj(offer.getSite());
							jsonPath=MyPriceConstants.NSS_ENGAGEMENT_JSON_PATH_ASE;
						}else if(offer.getOfferId().equals(TDDConstants.ADE_OFFER_ID)) {
							inputData=JacksonUtil.jsonNodeFromObj(offer.getCircuit());
							jsonPath=MyPriceConstants.NSS_ENGAGEMENT_JSON_PATH_ADE;
						}
						if(null!=inputData) {
							for (JsonNode element : inputData) {
								String nssEngagement=this.getNssEngagementValue(element,jsonPath,datasetName);
								if(StringUtils.isNotEmpty(nssEngagement) && nssEngagement.equalsIgnoreCase("Y") ) {
									automationInd= true;
								}else {
									automationInd=false;
									break outerloop;
								}
							}
						}
					}
			}
		}catch (Exception e) {
			logger.error("Exception during getting automationFlow indicator : {}", e);
		}
		
		return automationInd;
		
	}
	
	protected <T> String getNssEngagementValue(T inputData,String jsonPath,String datasetname) {
		String result=null;
		if(jsonPath.contains(MyPriceConstants.OR_CONDITION_SEPERATOR)){
			List<String> pathList= new ArrayList<String>(Arrays.asList(jsonPath.split(
					Pattern.quote(MyPriceConstants.OR_CONDITION_SEPERATOR))));
			result= processOrCondition(inputData, pathList, datasetname);
		}else {
			result=this.getItemValueUsingJsonPath(inputData,jsonPath,datasetname);
		}
		return result;
	}
	
	protected <T>String processOrCondition(T inputData,List<String> pathList,String datasetName) {
		for(String path:pathList) {
			String itemValue=this.getItemValueUsingJsonPath(inputData,path,datasetName);
			if(StringUtils.isNotEmpty(itemValue)) {
				return itemValue;
			}
		}
		return null;
	}
	
	
	
	protected <T>String getItemValueUsingJsonPath(T input,String jsonPath,String datasetname) {
		Object result=nexxusJsonUtility.getValue(input, jsonPath);
		if(null!=result) {
			return getDataFromNxLookUp(String.valueOf(result),datasetname);
		}
		return null;
	}
	
	/*protected String getDataFromNxLookUp(String input, String looupDataSet) {
		if(StringUtils.isNotEmpty(looupDataSet)) {
			NxLookupData nxLookup=nxLookupDataRepository.findTopByDatasetNameAndItemId(looupDataSet, input);
			if(null!=nxLookup) {
				input=nxLookup.getDescription();
			}
		}
		return input;
	}*/
	
	public String getDataFromNxLookUp(String input, String looupDataSet) {
		if(StringUtils.isNotEmpty(looupDataSet) && StringUtils.isNotEmpty(input)) {
			Map<String,NxLookupData> resultMap=nxMyPriceRepositoryServce.getLookupDataByItemId(looupDataSet);
			if(null!=resultMap && resultMap.containsKey(input) && null!= resultMap.get(input) ) {
				NxLookupData data=resultMap.get(input);
				return data.getDescription();
			}
		}
		return input;
	}
	
	
	/**
	 * This method is to persist data for FMO flow
	 * 
	 * @param retreiveICBPSPRequest
	 * @param nxSolutionDetail
	 * @param createTransactionResponse
	 * @param paramMap
	 * @return
	 */
	public Boolean saveDesignData(RetreiveICBPSPRequest retreiveICBPSPRequest, NxSolutionDetail nxSolutionDetail,
			CreateTransactionResponse createTransactionResponse, Map<String, Object> paramMap) {
		logger.info("Start saveDesignData for FMO :saveDesignData");
	//	boolean skipDmaapForReopenOrSold = false;
	//	int callDmappCount = 0;
		Boolean result = false;
		Map<String, Object> designResult = new HashMap<String, Object>();
		JsonNode request = mapper.valueToTree(retreiveICBPSPRequest);
		// save DB
		String userId = request.at("/solution/userId").asText();
		String userFirstName = request.at("/solution/userFirstName").asText();
		String userLastName = request.at("/solution/userLastName").asText();
		paramMap.put("userId", userId);
		paramMap.put("userFirstName", userFirstName);
		paramMap.put("userLastName", userLastName);
		
		JsonNode offers = request.at("/solution/offers");
		for (JsonNode offerElement : offers) {
			String offerId = offerElement.path("offerId").asText();
			String offerName = null;
			if (StringUtils.isNotEmpty(offerId)) {
				int id = Integer.parseInt(offerId);
				offerName = salesMsDao.getOfferNameByOfferId(id);
			}
			paramMap.put(MyPriceConstants.PRODUCT_NAME, offerName);
			JsonNode site = offerElement.path("site");
			for (JsonNode siteElement : site) {
				Object obj = nexxusJsonUtility.getValue(siteElement, "$..component.[?(@.componentCodeId==10)].componentId");
				String portId = String.valueOf(obj);
				paramMap.put(MyPriceConstants.CIRCUIT_ID, portId);
				NxDesign nxDesign = submitToMyPriceService.saveNxDesign(nxSolutionDetail, siteElement, paramMap);
				if(null != nxDesign){
					/**callDmappCount++;
					
					 * TODO: Replace this with config design and pricing call
					 * 
					 * designResult = myPriceTransactionUtil.configAndUpdatePricing(createTransactionResponse, siteElement,
							nxDesign, paramMap, offerName);
					result = designResult.containsKey(MyPriceConstants.RESPONSE_STATUS) ? (boolean) designResult.get(MyPriceConstants.RESPONSE_STATUS) : false;**/
				}
				if(!result) {
					designResult.put(MyPriceConstants.ASR_ITEM_ID, portId);
					designResult.put(MyPriceConstants.NX_SITE_ID, siteElement.path("siteId").asLong());
					// break the loop if design fails for any site
					break;
				}
			}
			paramMap.remove(StringConstants.SITE_TYPE);
			paramMap.remove(MyPriceConstants.CURRENT_NX_SITE_ID);
		}
		/*
		 * TODO : replace code sending dmapp if any

		 * if(!skipDmaapForReopenOrSold || callDmappCount > 0) {
			designResult.put(MyPriceConstants.NX_AUDIT_ID, paramMap.get(MyPriceConstants.NX_AUDIT_ID));
			designResult.put(MyPriceConstants.MYPRICE_DESIGN, "design");
			if(result) {
				myPriceTransactionUtil.sendDmaapEvents(myPriceTransactionUtil.getNxMpDeal(createTransactionResponse.getNxTransacId()), nxSolutionDetail, com.att.sales.nexxus.common.CommonConstants.SUBMITTED, designResult);
			} else {
				myPriceTransactionUtil.sendDmaapEvents(myPriceTransactionUtil.getNxMpDeal(createTransactionResponse.getNxTransacId()), nxSolutionDetail, com.att.sales.nexxus.common.CommonConstants.FAILED, designResult);
			}
		}*/
		logger.info("End saveDesignData for FMO :saveDesignData");
		paramMap.put("dppRequest", request);
		return result;
	}
	
	public <T> Boolean isLastDesign(int counter, List<T> inputLst) {
		if (counter == inputLst.size() - 1) {
			return true;
		}
		return false;
	}
	public  Proxy getProxy() {
		return restClient.getProxy(httpProxyHost, httpProxyPort, proxyUser, proxyPassword);
	}

	public Map<String, Object> callUpdateCleanSave(NxSolutionDetail nxSolutionDetail, Map<String, Object> paramMap,
			List<Long> nxRequestGrpId) {
		Map<String, Object> requestMetaDataMap = new HashMap<>();
		return submitToMyPriceService.callUpdateCleanSave(nxSolutionDetail, paramMap, nxRequestGrpId,requestMetaDataMap);
	}
	
	public List<String> getDataInString(Object request,String udfId,String referenceId) {
		if(request != null) { 
			String Path=eplswanCleansaveAttibutesPath;
			Path= String.format(Path,referenceId,udfId );
			TypeRef<List<Object>> mapType = new TypeRef<List<Object>>() {};
			List<Object> results = jsonPathUtil.search(request, Path, mapType);
			if(CollectionUtils.isEmpty(results)) {
				return null;
			} else {
				System.out.print( results.get(0));
				return (List<String>) results.get(0);
			}
		}
		return null;
	}
}
