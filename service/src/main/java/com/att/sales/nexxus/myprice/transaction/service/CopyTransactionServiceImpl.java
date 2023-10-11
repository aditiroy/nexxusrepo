package com.att.sales.nexxus.myprice.transaction.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.att.aft.dme2.internal.apache.commons.lang.math.NumberUtils;
import com.att.aft.dme2.internal.google.common.base.Strings;
import com.att.aft.dme2.internal.google.common.collect.Lists;
import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.model.Status;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.accesspricing.service.AccessPricingServiceImpl;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxAccessPricingData;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxMpDesignDocument;
import com.att.sales.nexxus.dao.model.NxMpPriceDetails;
import com.att.sales.nexxus.dao.model.NxMpSiteDictionary;
import com.att.sales.nexxus.dao.model.NxMpSolutionDetails;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxAccessPricingDataRepository;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxMpDesignDocumentRepository;
import com.att.sales.nexxus.dao.repository.NxMpPriceDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxMpSiteDictionaryRepository;
import com.att.sales.nexxus.dao.repository.NxMpSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxOutputFileRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.fallout.model.FalloutDetailsRequest;
import com.att.sales.nexxus.fallout.model.FalloutDetailsResponse;
import com.att.sales.nexxus.fallout.service.FalloutDetailsImpl;
import com.att.sales.nexxus.myprice.transaction.model.Documents;
import com.att.sales.nexxus.myprice.transaction.model.DocumentsWrapper;
import com.att.sales.nexxus.myprice.transaction.model.GetDealLineItem;
import com.att.sales.nexxus.myprice.transaction.model.GetDealVersionRevisionResponse;
import com.att.sales.nexxus.reteriveicb.model.RetreiveICBPSPRequest;
import com.att.sales.nexxus.service.InrBetaGenerateNxsiteId;
import com.att.sales.nexxus.service.InrQualifyService;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.RestClientUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.TypeRef;

@Service("CopyTransactionServiceImpl")
public class CopyTransactionServiceImpl extends BaseServiceImpl{
	
	private static Logger log = LoggerFactory.getLogger(CopyTransactionServiceImpl.class);
	
	@Autowired
	private RestClientUtil restClient;
	
	@Autowired
	private MyPriceTransactionUtil myPriceTransactionUtil;
	
	@Autowired
	private NxMpDealRepository nxMpDealRepository;
	
	@Autowired
	private NxMpSolutionDetailsRepository nxMpSolutionDetailsRepository;
	
	@Autowired
	private NxMpSiteDictionaryRepository nxMpSiteDictionaryRepository;
	
	@Autowired
	private NxMpDesignDocumentRepository nxMpDesignDocumentRepository;
	
	@Autowired
	private NxMpPriceDetailsRepository nxMpPriceDetailsRepository;
	
	@Autowired
	private FalloutDetailsImpl falloutDetailsImpl;
	
	@Autowired
	private Environment env;
	
	@Autowired
	private HttpRestClient httpRestClient;
	
	@Autowired
	private NxRequestDetailsRepository nxRequestDetailsRepository;
	
	@Autowired
	private InrQualifyService inrQualifyService;
	
	@Autowired
	private NxAccessPricingDataRepository nxAccessPricingDataRepository;
	
	@Autowired
	private AccessPricingServiceImpl accessPricingServiceImpl;
	
	@Autowired
	private NxOutputFileRepository  nxOutputFileRepository;
	
	@Autowired
	private JsonPathUtil jsonPathUtil;
	
	@Value("${myprice.proxy.enabled}")
	private String isProxyEnabled;
	
	@Autowired
	private InrBetaGenerateNxsiteId inrBetaGenerateNxsiteId;
	
	
	public Map<String, Object> processMutliPriceScenario(Map<String, Object> requestMap) {
		log.info("Inside processMutliPriceScenario");
		String emptyRequest = "{}";
		Map<String, Object> response = new HashMap<String, Object>();
		requestMap.put(StringConstants.TRANSACTION_UPDATE, StringConstants.TRANSACTION_UPDATE_PRICE);
		String myPriceTransId =  (String) requestMap.get(StringConstants.MY_PRICE_TRANS_ID);
		NxMpDeal nxMpDeal = nxMpDealRepository.findByTransactionId(myPriceTransId);

		Documents copyTransResponse = callCopyTrans(requestMap, emptyRequest,nxMpDeal,new FalloutDetailsRequest());
		int code = (int) requestMap.get(MyPriceConstants.RESPONSE_CODE);
		
		if(null != copyTransResponse && code == CommonConstants.SUCCESS_CODE) {
		
			// save to nxMpdeal
			Long priceScenarioId = null;
			if(null != requestMap.get(StringConstants.PRICE_SCENARIO_ID))
				priceScenarioId = Long.parseLong((String) requestMap.get(StringConstants.PRICE_SCENARIO_ID));
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put(StringConstants.TRANSACTION_UPDATE, StringConstants.TRANSACTION_UPDATE_PRICE);
			int mpLatestrevision=requestMap.containsKey(MyPriceConstants.MP_LATEST_REVISION)?(int) requestMap.get(MyPriceConstants.MP_LATEST_REVISION):0;
			int mpLatestversion=requestMap.containsKey(MyPriceConstants.MP_LATEST_VERSION)?(int) requestMap.get(MyPriceConstants.MP_LATEST_VERSION):0;
			
			paramMap.put(MyPriceConstants.MP_LATEST_REVISION, mpLatestrevision);
			paramMap.put(MyPriceConstants.MP_LATEST_VERSION, mpLatestversion);
			NxMpDeal newNxMPDeal = saveToNxMPDeal(nxMpDeal, copyTransResponse, priceScenarioId, paramMap, new FalloutDetailsRequest());
			
			//	save to NX_MP_SITE_DICTIONARY
			NxMpSiteDictionary nxMpSiteDictionary = nxMpSiteDictionaryRepository.findByNxTxnId(nxMpDeal.getNxTxnId());
			if(null != nxMpSiteDictionary) {
				NxMpSiteDictionary newSiteDictionary = saveToNxSiteDic(newNxMPDeal.getNxTxnId(), nxMpSiteDictionary);
			
				// save to NX_MP_SOLUTION_DETAILS
				NxMpSolutionDetails nxMpSolutionDetails = nxMpSolutionDetailsRepository.findByNxTxnId(nxMpDeal.getNxTxnId());
				if(null != nxMpSolutionDetails) {
					saveToNxSolution(newNxMPDeal.getNxTxnId(), newSiteDictionary.getSiteRefId(), nxMpSolutionDetails);
				}
			}
			// save to nx_mp_design_document
			List<NxMpDesignDocument>  nxMpDesignDocumentList = nxMpDesignDocumentRepository.findByNxTxnId(nxMpDeal.getNxTxnId());
			saveToNxDesignDoc(newNxMPDeal.getNxTxnId(), nxMpDesignDocumentList);
			
			//save to nx_mp_price_details
			List<NxMpPriceDetails> nxMpPriceDetailList = nxMpPriceDetailsRepository.findByNxTxnId(nxMpDeal.getNxTxnId());
			saveToNxPrice(newNxMPDeal.getNxTxnId(), nxMpPriceDetailList);
			
			response.put(StringConstants.STATUS, true);
			response.put(StringConstants.NEW_NX_TXN_ID, newNxMPDeal.getNxTxnId());
			response.put(StringConstants.NEW_NX_TXN_OBJECT, newNxMPDeal);
			
		}else {
			response.put(StringConstants.STATUS, false);
			log.error("exception occured in multiple pricing call");
		}
		myPriceTransactionUtil.prepareResponseMap(requestMap, response);
		return response;
		
	}
	
	public ServiceResponse copyTransaction(Map<String, Object> requestMap, FalloutDetailsRequest request) {
		log.info("Inside copyTransaction");
		String emptyRequest = "{}";
		ServiceResponse response = new ServiceResponse();
		String nexxusMyPriceTransId =  (String) requestMap.get(StringConstants.MY_PRICE_TRANS_ID);
		List<NxMpDeal> nxMpDeals = nxMpDealRepository.findByMpTransactionId(nexxusMyPriceTransId);
		Documents copyTransResponse = null;
		
		NxMpDeal mpDeal = new NxMpDeal();
		if (StringConstants.APPEND_TO_OTHER_DEAL
				.equalsIgnoreCase((String) requestMap.get("ACTION_IND"))) {
			mpDeal.setDealID((String)requestMap.get("dealId"));
			mpDeal.setVersion((String)requestMap.get("version"));
			mpDeal.setRevision((String)requestMap.get("revision"));
			mpDeal.setTransactionId((String)requestMap.get(StringConstants.MY_PRICE_TRANS_ID));
			copyTransResponse = callCopyTrans(requestMap, emptyRequest,mpDeal,request);
		}else if (CollectionUtils.isNotEmpty(nxMpDeals)) {
			copyTransResponse = callCopyTrans(requestMap, emptyRequest,nxMpDeals.get(0),request);
		}
	    NxSolutionDetail nxSolutionDetail = (requestMap.containsKey(StringConstants.SOLUTIONDETAILS) && null != requestMap.get(StringConstants.SOLUTIONDETAILS))? (NxSolutionDetail) requestMap.get(StringConstants.SOLUTIONDETAILS) : null;
	    String flowType = (requestMap.containsKey(StringConstants.FLOW_TYPE) && null != requestMap.get(StringConstants.FLOW_TYPE))? (String) requestMap.get(StringConstants.FLOW_TYPE) : null;
	    
	    Long priceScenarioId = null;
		if(null != requestMap.get(StringConstants.PRICE_SCENARIO_ID))
			priceScenarioId = (Long) requestMap.get(StringConstants.PRICE_SCENARIO_ID);
	    
	    int code = (int) requestMap.get(MyPriceConstants.RESPONSE_CODE);

		if(null != copyTransResponse && code == CommonConstants.SUCCESS_CODE) {
			if (StringConstants.APPEND_TO_OTHER_DEAL.equalsIgnoreCase((String) requestMap.get("ACTION_IND"))) {
				NxMpDeal newNxMPDeal = saveToNxMPDeal(mpDeal, copyTransResponse, priceScenarioId, requestMap,
						request);
				requestMap.put(StringConstants.NEW_MY_PRICE_DEAL, newNxMPDeal);
				
				NxMpSiteDictionary nxMpSiteDictionary = null;
				if (CollectionUtils.isNotEmpty(nxMpDeals)) {
					nxMpDeals.sort((NxMpDeal d1, NxMpDeal d2)->d2.getNxTxnId().compareTo(d1.getNxTxnId())); 
					for(NxMpDeal d : nxMpDeals) {
						nxMpSiteDictionary = nxMpSiteDictionaryRepository.findByNxTxnId(d.getNxTxnId());
					}
				}
				if(nxMpSiteDictionary == null){
					 nxMpSiteDictionary=new NxMpSiteDictionary();
					 nxMpSiteDictionary.setSourceSystem(MyPriceConstants.SOURCE_INR);
					 nxMpSiteDictionary.setActiveYN("Y");
				}
				
				if (copyTransResponse.getSiteAddress() != null) {
					nxMpSiteDictionary.setSiteAddress("{ \"siteAddress\" : " + copyTransResponse.getSiteAddress() + "}");
				}
				requestMap.put("appendtoDealTxnId", newNxMPDeal.getNxTxnId());
				saveToNxSiteDic(newNxMPDeal.getNxTxnId(), nxMpSiteDictionary);
				setSuccessResponse(response);
			}else if(CollectionUtils.isNotEmpty(nxMpDeals)) {
				NxMpDeal nxMpDeal = nxMpDeals.get(0);	
				// save to nxMpdeal
				NxMpDeal newNxMPDeal = saveToNxMPDeal(nxMpDeal, copyTransResponse, priceScenarioId, requestMap, request);
				requestMap.put(StringConstants.NEW_MY_PRICE_DEAL, newNxMPDeal);
				
				//	save to NX_MP_SITE_DICTIONARY
				NxMpSiteDictionary nxMpSiteDictionary = nxMpSiteDictionaryRepository.findByNxTxnId(nxMpDeal.getNxTxnId());
				if(null != nxMpSiteDictionary) {
					/*if (StringConstants.APPEND_TO_OTHER_DEAL
							.equalsIgnoreCase((String) requestMap.get("ACTION_IND"))) {
						nxMpSiteDictionary.setSiteAddress(copyTransResponse.getSiteAddress());
					}*/
					NxMpSiteDictionary newSiteDictionary = saveToNxSiteDic(newNxMPDeal.getNxTxnId(), nxMpSiteDictionary);
				
					// save to NX_MP_SOLUTION_DETAILS
					NxMpSolutionDetails nxMpSolutionDetails = nxMpSolutionDetailsRepository.findByNxTxnId(nxMpDeal.getNxTxnId());
					if(null != nxMpSolutionDetails) {
						saveToNxSolution(newNxMPDeal.getNxTxnId(), newSiteDictionary.getSiteRefId(), nxMpSolutionDetails);
					}
				}
				// save to nx_mp_design_document
				List<NxMpDesignDocument>  nxMpDesignDocumentList = nxMpDesignDocumentRepository.findByNxTxnId(nxMpDeal.getNxTxnId());
				saveToNxDesignDoc(newNxMPDeal.getNxTxnId(), nxMpDesignDocumentList);
				
				//save to nx_mp_price_details
				List<NxMpPriceDetails> nxMpPriceDetailList = nxMpPriceDetailsRepository.findByNxTxnId(nxMpDeal.getNxTxnId());
				saveToNxPrice(newNxMPDeal.getNxTxnId(), nxMpPriceDetailList);
				RetreiveICBPSPRequest retreiveICBPSPRequest = (requestMap.containsKey("retreiveICBPSPRequest") && null != requestMap.get("retreiveICBPSPRequest")) ?
						(RetreiveICBPSPRequest) requestMap.get("retreiveICBPSPRequest") : null;
				// audit for reconfigure
				Long nxAuditId = myPriceTransactionUtil.saveNxDesignAudit(nxSolutionDetail.getNxSolutionId(), null != priceScenarioId ? String.valueOf(priceScenarioId) : null,
						retreiveICBPSPRequest, CommonConstants.SUCCESS, MyPriceConstants.AUDIT_COPY_RECONFIGURE);
				requestMap.put(MyPriceConstants.NX_AUDIT_ID, nxAuditId);
				if(!StringConstants.FLOW_TYPE_INR.equalsIgnoreCase(flowType)) {
					myPriceTransactionUtil.sendDmaapEvents(newNxMPDeal, nxSolutionDetail , CommonConstants.CREATED, requestMap);
				}
				log.info("Dmapp event created for copy transaction for transactionId: "+ newNxMPDeal.getNxTxnId()); 
	
				setSuccessResponse(response);
			}
		} else {
			response = new ServiceResponse();
			Status status = new Status();
			status.setCode("M00003");
			response.setStatus(status);
			NxMpDeal deal = new NxMpDeal();
			deal.setPriceScenarioId(priceScenarioId);
			if (!StringConstants.FLOW_TYPE_INR.equalsIgnoreCase(flowType)) {
				myPriceTransactionUtil.sendDmaapEvents(deal, nxSolutionDetail, CommonConstants.FAILED, requestMap);
			}
			log.error("exception occured in multiple pricing call");
		}
		requestMap.remove(MyPriceConstants.RESPONSE_CODE);
		requestMap.remove(MyPriceConstants.RESPONSE_DATA);
		requestMap.remove(MyPriceConstants.RESPONSE_MSG);
		return response;
		
	}
	
	public Documents callCopyTrans(Map<String, Object> requestMap, String emptyRequest,NxMpDeal nxMpDeal, FalloutDetailsRequest request) {
		log.info("Inside callCopyTrans");
		//myPriceTransactionUtil.setSystemProperties();
		String flowType = (requestMap.containsKey(StringConstants.FLOW_TYPE) && null != requestMap.get(StringConstants.FLOW_TYPE))? (String) requestMap.get(StringConstants.FLOW_TYPE) : null;
		Map<String, Object> result = new HashMap<String, Object>();
		Documents documents = null;
		try {
			Map<String, String> headers  = new HashMap<>();
			headers.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.MYPRICE_AUTHORIZATION));
			String myPriceTransId =  (String) requestMap.get(StringConstants.MY_PRICE_TRANS_ID);
			/**
			 * get the transaction id value from myprice deal revision version call
			 * */
			
		     //if(!StringConstants.FLOW_TYPE_INR.equalsIgnoreCase(flowType)) {
				
				if (requestMap.containsKey(StringConstants.TRANSACTION_UPDATE)&& requestMap.get(StringConstants.TRANSACTION_UPDATE).toString()
								.equalsIgnoreCase(StringConstants.TRANSACTION_UPDATE_DESIGN)) {
					
					Map<String,Object> resultMap=getDealVersionDetail(nxMpDeal.getDealID());
					requestMap.put(MyPriceConstants.MP_LATEST_VERSION,
							resultMap.containsKey(MyPriceConstants.VERSION) ? resultMap.get(MyPriceConstants.VERSION) : 0);
				}
				
		   //}
			if (requestMap.containsKey(StringConstants.TRANSACTION_UPDATE)
					&& requestMap.get(StringConstants.TRANSACTION_UPDATE).toString()
							.equalsIgnoreCase(StringConstants.TRANSACTION_UPDATE_PRICE)) {
				int version = nxMpDealRepository.findMaxVersionBySolutoinId(nxMpDeal.getSolutionId(), CommonConstants.ACTIVE_Y);
				
				
				Map<String,Object> resultMap=getDealRevisionDetail(nxMpDeal.getDealID(),version);
				//myPriceTransId=resultMap.containsKey(MyPriceConstants.BSID)?(String) resultMap.get(MyPriceConstants.BSID):"";
				requestMap.put(MyPriceConstants.MP_LATEST_REVISION,
						resultMap.containsKey(MyPriceConstants.REVISION) ? resultMap.get(MyPriceConstants.REVISION)
								: 0);
				requestMap.put(MyPriceConstants.MP_LATEST_VERSION, version);
			}

			if (!Strings.isNullOrEmpty(request.getActionInd()) && requestMap.containsKey("ACTION_IND")
					&& requestMap.get("ACTION_IND").toString().equalsIgnoreCase(StringConstants.APPEND_TO_OTHER_DEAL)) {
				String version = String.valueOf(request.getVersion());
				String revision = String.valueOf(request.getRevision());
				String dealId = String.valueOf(request.getDealId());
				myPriceTransId = nxMpDeal.getTransactionId();
			}

			String proxy = null;
			if(StringConstants.CONSTANT_Y.equalsIgnoreCase(isProxyEnabled)) {
				proxy = env.getProperty(CommonConstants.CDP_HTTP_PROXY);
			}
			log.info("****** 	CopyTransaction url ********** {}", org.apache.commons.lang3.StringUtils.normalizeSpace(env.getProperty("myprice.copyTransactionRequest").replace("{transactionId}", myPriceTransId)));
			String requestBody = "{ \"criteria\": {\"fields\": [\"bs_id\",\"rd_requestID_q\", \"rd_revisionNumber_q\",\"version_t\",\"siteAddress\"]}}";
			String response = httpRestClient.callHttpRestClient(env.getProperty("myprice.copyTransactionRequest").replace("{transactionId}", myPriceTransId),  HttpMethod.POST, null, requestBody, 
					headers,proxy);
			result.put(MyPriceConstants.RESPONSE_DATA, response);
			result.put(MyPriceConstants.RESPONSE_CODE, 200);
			result.put(MyPriceConstants.RESPONSE_MSG,"OK");

			String copyResponse = (result.containsKey(MyPriceConstants.RESPONSE_DATA) && result.get(MyPriceConstants.RESPONSE_DATA) != null) ? (String) result.get(MyPriceConstants.RESPONSE_DATA) : null;
			int code = (int) result.get(MyPriceConstants.RESPONSE_CODE);
//			int code=200;
			myPriceTransactionUtil.prepareResponseMap(result, requestMap);
			log.info("Copy Transaction response code :: {} ", code);
            log.info("Copy Transaction response :: {} ", org.apache.commons.lang3.StringUtils.normalizeSpace(copyResponse));
			if (code == CommonConstants.SUCCESS_CODE && null != copyResponse && !copyResponse.isEmpty()) {
				requestMap.put(MyPriceConstants.RESPONSE_STATUS, true);
				DocumentsWrapper documentsWrapper = (DocumentsWrapper) restClient.processResult(copyResponse,
						DocumentsWrapper.class);
				return (documentsWrapper != null ? documentsWrapper.getDocuments() : null);
			}else {
				requestMap.put(MyPriceConstants.RESPONSE_STATUS, false);
			}
		} catch (SalesBusinessException e) {
			log.info("Exception occured while calling copy transaction");
			e.getMessage();
			requestMap.put(MyPriceConstants.RESPONSE_CODE, e.getHttpErrorCode());
			requestMap.put(MyPriceConstants.RESPONSE_MSG, e.getMessage());
			requestMap.put(MyPriceConstants.RESPONSE_STATUS, false);
		}
		log.info("End callCopyTrans");
		return documents;
	}
	public NxMpDeal saveToNxMPDeal(NxMpDeal nxMpDeal, Documents copyTransResponse, Long priceScenarioId, Map<String, Object> paramMap, FalloutDetailsRequest request) {
		log.info("Inside saveToNxMPDeal");
		NxMpDeal newNxMPDeal = new NxMpDeal();
		BeanUtils.copyProperties(nxMpDeal, newNxMPDeal, "nxTxnId", "modifiedDate", "priceScenarioId", "nxPedStatusInd");
		newNxMPDeal.setDealID(nxMpDeal.getDealID());
		
		if(paramMap.containsKey(StringConstants.ADD_PRODDUCT_IN_EXSTING_TXN) && paramMap.get(StringConstants.ADD_PRODDUCT_IN_EXSTING_TXN).toString().equalsIgnoreCase(StringConstants.ADD_PRODDUCT_IN_EXSTING_TXN)) {
			newNxMPDeal.setRevision(nxMpDeal.getRevision());
			newNxMPDeal.setVersion(nxMpDeal.getVersion());
		}
		if(paramMap.containsKey(StringConstants.TRANSACTION_UPDATE) && paramMap.get(StringConstants.TRANSACTION_UPDATE).toString().equalsIgnoreCase(StringConstants.TRANSACTION_UPDATE_DESIGN)) {
			newNxMPDeal.setRevision(StringConstants.STR_ZERO);
			//int version = nxMpDealRepository.findMaxVersionBySolutoinId(nxMpDeal.getSolutionId(), CommonConstants.ACTIVE_Y);
			//newNxMPDeal.setVersion(String.valueOf(++version));
			//here new value has to be retieved from mp api call, i.e called before  copy transaction
			int version = (int) paramMap.get(MyPriceConstants.MP_LATEST_VERSION);
			newNxMPDeal.setVersion(String.valueOf(++version));
		}
		if(paramMap.containsKey(StringConstants.TRANSACTION_UPDATE) && paramMap.get(StringConstants.TRANSACTION_UPDATE).toString().equalsIgnoreCase(StringConstants.TRANSACTION_UPDATE_PRICE)) {
			//int revision = nxMpDealRepository.findMaxRevisionBySolutoinId(nxMpDeal.getSolutionId(), nxMpDeal.getDealID(), CommonConstants.ACTIVE_Y, nxMpDeal.getVersion());
			//newNxMPDeal.setRevision(String.valueOf(++revision));
			//here new value has to be retieved from mp api call, i.e called before  copy transaction
			int revision = (int) paramMap.get(MyPriceConstants.MP_LATEST_REVISION);
			newNxMPDeal.setRevision(String.valueOf(++revision));
			int version = (int) paramMap.get(MyPriceConstants.MP_LATEST_VERSION);
			newNxMPDeal.setVersion(String.valueOf(version));
			newNxMPDeal.setNxPedStatusInd(nxMpDeal.getNxPedStatusInd());
		}
		if (!Strings.isNullOrEmpty(request.getActionInd()) && paramMap.containsKey("ACTION_IND")
				&& paramMap.get("ACTION_IND").toString().equalsIgnoreCase(request.getActionInd())) {
			FalloutDetailsResponse falloutResponse = null;
			List<NxMpDeal> deals=nxMpDealRepository.findBySolutionIdAndActiveYN(request.getNxSolutionId(), StringConstants.CONSTANT_Y);
			Long newNxSolutionId = 0L;
			TypeRef<List<Object>> mapType = new TypeRef<List<Object>>() {
			};
			String  siteAddress= copyTransResponse.getSiteAddress();
			List<Object> results = jsonPathUtil.search(siteAddress, "$..nxSiteId", mapType);
			Map<String, Object> map=new HashMap<String, Object>();
			List<String> nxSiteIds=new ArrayList<>();
			if (CollectionUtils.isNotEmpty(results)) {
				for(Object result:results) {
					if(NumberUtils.isDigits(String.valueOf(result))) {
						nxSiteIds.add(String.valueOf(result));
					}
				}
			}
			if (CollectionUtils.isNotEmpty(nxSiteIds)) {
				Long maxNxSiteId = nxSiteIds.stream().map(n -> Long.parseLong(String.valueOf(n))).max(Comparator.naturalOrder()).get();
				map.put("maxNxsiteid", maxNxSiteId);
			}
			if(CollectionUtils.isNotEmpty(deals)) {
				request.setAction("copySolution");
				try {
					request.setMap(map);
					falloutResponse = (FalloutDetailsResponse) falloutDetailsImpl.nexxusRequestActions(request);
				} catch (SalesBusinessException e) {
					log.info("Exeception caugth while copying the solution :{} ", e);
				}
				newNxSolutionId = falloutResponse.getNxSolutionId();
				paramMap.put(StringConstants.NEW_SOL_IND, StringConstants.CONSTANT_Y);
				paramMap.put(StringConstants.NEW_NX_SOL_ID, newNxSolutionId);
			} else {
				newNxSolutionId = request.getNxSolutionId();
				if(map.containsKey("maxNxsiteid")) {
					long startTime = System.nanoTime();
					long endTime = System.nanoTime();
					
					
					List<NxRequestDetails> details= nxRequestDetailsRepository.findByNxSolutionId(request.getNxSolutionId());
					for(NxRequestDetails requestDetails: details) {
							List<NxOutputFileModel> outputFiles = requestDetails.getNxOutputFiles();
							for(NxOutputFileModel outputFile: outputFiles) {
								outputFile.setNxSiteIdInd("N");
								nxOutputFileRepository.saveAndFlush(outputFile);
							}
							if(StringConstants.FLOW_TYPE_USRP.equalsIgnoreCase(requestDetails.getFlowType())) {
								inrBetaGenerateNxsiteId.generateNxsiteidInrBeta(requestDetails.getNxReqId(), true, map);
							}else {
								inrQualifyService.inrQualifyCheck(requestDetails.getNxReqId(), true, map);
							}
					}
					
					List<NxAccessPricingData> nxAccessPricingDatas = nxAccessPricingDataRepository.findByNxSolIdAndMpStatusAndIncludeInd(request.getNxSolutionId());
					if(CollectionUtils.isNotEmpty(nxAccessPricingDatas)) {
						accessPricingServiceImpl.storeNxSiteID(nxAccessPricingDatas, newNxSolutionId, map);
						List<List<NxAccessPricingData>> smallerTokensList = Lists.partition(nxAccessPricingDatas, 900);
						for(List<NxAccessPricingData> tokens : smallerTokensList) {
							nxAccessPricingDataRepository.saveAll(tokens);
						}
					}
					nxAccessPricingDatas=null;
				}
				paramMap.put(StringConstants.NEW_SOL_IND, StringConstants.CONSTANT_Y);
				paramMap.put(StringConstants.NEW_NX_SOL_ID, newNxSolutionId);
			}
			
			newNxMPDeal.setRevision(request.getRevision());
			newNxMPDeal.setDealID(request.getDealId());
			newNxMPDeal.setSolutionId(newNxSolutionId);
			newNxMPDeal.setActiveYN(CommonConstants.ACTIVE_Y);
			int version = (int) paramMap.get(MyPriceConstants.MP_LATEST_VERSION);
			newNxMPDeal.setVersion(String.valueOf(++version));
            log.info("copy transaction : appendToOtherDeal deal verison and revision {} {} {}", org.apache.commons.lang3.StringUtils.normalizeSpace(newNxMPDeal.getDealID()),
            		org.apache.commons.lang3.StringUtils.normalizeSpace(newNxMPDeal.getVersion()),
            		org.apache.commons.lang3.StringUtils.normalizeSpace(newNxMPDeal.getRevision()));
		}
		newNxMPDeal.setTransactionId(copyTransResponse.getMyPriceTransacId());
		newNxMPDeal.setPriceScenarioId(priceScenarioId);
		newNxMPDeal.setDealStatus(CommonConstants.CREATED);
		newNxMPDeal.setCreatedDate(new Date());
		newNxMPDeal.setNxMpStatusInd(StringConstants.CONSTANT_Y);
			
		// set actv= N when there is change in version or revision for PS 
		if(Optional.ofNullable(priceScenarioId).isPresent()) {
		     List <NxMpDeal> deal = nxMpDealRepository.findBySolutionIdAndActiveYNAndPriceScenarioId(nxMpDeal.getSolutionId(), CommonConstants.ACTIVE_Y, Long.parseLong(priceScenarioId.toString()));
	         if (CollectionUtils.isNotEmpty(deal)) {
	         	NxMpDeal currentDeal = deal.get(0);
	         	if(!newNxMPDeal.getVersion().equalsIgnoreCase(currentDeal.getVersion()) || !newNxMPDeal.getRevision().equalsIgnoreCase(currentDeal.getRevision())) {
	         		currentDeal.setActiveYN(StringConstants.CONSTANT_N);
	         		currentDeal.setModifiedDate(new Date());
	         		nxMpDealRepository.saveAndFlush(currentDeal);
	         	}
	            
	         }
         }
         //save new deal
        newNxMPDeal = nxMpDealRepository.saveAndFlush(newNxMPDeal);
       
 		log.info("Copy Transaction : New transction : {} ", (newNxMPDeal != null ? newNxMPDeal.getNxTxnId() : 0));
		return newNxMPDeal;
	}
	
	public NxMpSiteDictionary saveToNxSiteDic(Long nxTxnId, NxMpSiteDictionary nxMpSiteDictionary) {
		log.info("Inside saveToNxSiteDic");
		NxMpSiteDictionary newSiteDictionary = new NxMpSiteDictionary();
		BeanUtils.copyProperties(nxMpSiteDictionary, newSiteDictionary, "siteRefId", "nxTxnId", "createdDate", "modifiedDate");
		newSiteDictionary.setNxTxnId(nxTxnId);
		nxMpSiteDictionaryRepository.saveAndFlush(newSiteDictionary);
		log.info("End saveToNxSiteDic");
		return newSiteDictionary;
	}
	
	public NxMpSolutionDetails saveToNxSolution(Long nxTxnId, Long siteRefId, NxMpSolutionDetails nxMpSolutionDetails) {
		log.info("Inside saveToNxSolution");
		NxMpSolutionDetails newSolutionDetails = new NxMpSolutionDetails();
		BeanUtils.copyProperties(nxMpSolutionDetails, newSolutionDetails, "nxMpSolutionId", "nxTxnId", "siteRefId", "createdDate", "modifiedDate");
		newSolutionDetails.setNxTxnId(nxTxnId);
		newSolutionDetails.setSiteRefId(siteRefId);
		nxMpSolutionDetailsRepository.save(newSolutionDetails);
		log.info("End saveToNxSolution");
		return newSolutionDetails;
	}
	
	public List<NxMpDesignDocument> saveToNxDesignDoc(Long nxTxnId, List<NxMpDesignDocument> nxMpDesignDocumentList) {
		log.info("Inside saveToNxDesign");
		List<NxMpDesignDocument>  copyDesignDocumentList = new ArrayList<NxMpDesignDocument>();
		for(NxMpDesignDocument nxMpDesign : nxMpDesignDocumentList) {
			NxMpDesignDocument newDesignDoc = new NxMpDesignDocument();
			BeanUtils.copyProperties(nxMpDesign, newDesignDoc, "nxDocumentId", "nxTxnId", "createdDate", "modifiedDate");
			newDesignDoc.setNxTxnId(nxTxnId);
			newDesignDoc.setCreatedDate(new Date());
			copyDesignDocumentList.add(newDesignDoc);
		}			
		if(!copyDesignDocumentList.isEmpty())
			nxMpDesignDocumentRepository.saveAll(copyDesignDocumentList);
		log.info("End saveToNxDesign");
		return copyDesignDocumentList;
	}
	
	public List<NxMpPriceDetails> saveToNxPrice(Long nxTxnId, List<NxMpPriceDetails> nxMpPriceDetailList) {
		log.info("Inside saveToNxPrice");
		List<NxMpPriceDetails>  copyMpPriceDetailList = new ArrayList<NxMpPriceDetails>();
		for(NxMpPriceDetails nxMpPriceDetails : nxMpPriceDetailList) {
			NxMpPriceDetails newPriceDetails = new NxMpPriceDetails();
			BeanUtils.copyProperties(nxMpPriceDetails, newPriceDetails, "nxPriceDetailsId", "nxTxnId");
			newPriceDetails.setNxTxnId(nxTxnId);
			copyMpPriceDetailList.add(newPriceDetails);
		}
		if(!copyMpPriceDetailList.isEmpty())
			nxMpPriceDetailsRepository.saveAll(copyMpPriceDetailList);
		log.info("End saveToNxPrice");
		return copyMpPriceDetailList;
	}
	
	public Map<String,Object> getDealVersionDetail(String dealId) {
		
		System.out.println("Entered integer is F: "+ 1);
		Map<String,Object> resultMap = new HashMap<>();
		try {
			String uri = env.getProperty("myprice.getDealLine.rest.url");
			Map<String, String> headers = new HashMap<String, String>();
			headers.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.MYPRICE_AUTHORIZATION));
			headers.put(StringConstants.REQUEST_CONTENT_TYPE, "application/json");
			String fields = env.getProperty("myprice.getDeal.version.fields");
			fields = fields.replace("{dealId}", dealId);
			String url = uri + fields;
			log.info("Get deal version details request url: {}", org.apache.commons.lang3.StringUtils.normalizeSpace(url));
			String proxy = null;
			if(StringConstants.CONSTANT_Y.equalsIgnoreCase(isProxyEnabled)) {
				proxy = env.getProperty(CommonConstants.CDP_HTTP_PROXY);
			}
			String transResponse = restClient.callRestApi(null, url, "GET", headers, null, proxy);
			if (null != transResponse && !transResponse.isEmpty()) {
				GetDealVersionRevisionResponse response = (GetDealVersionRevisionResponse) restClient
						.processResult(transResponse, GetDealVersionRevisionResponse.class);			
				
				if (response!=null && CollectionUtils.isNotEmpty(response.getItems())) {
					Map<String,Object> itemMap= new HashMap<>();
					Set<Integer> versionData= new HashSet<Integer>();
					List<GetDealLineItem> itemList = response.getItems();
					
					for (GetDealLineItem dealLineItem : itemList) {
						versionData.add(dealLineItem.getVersion());
						itemMap.put(String.valueOf(dealLineItem.getVersion()), dealLineItem.getBsId());
					}
					int maxversion=Collections.max(versionData);
					resultMap.put(MyPriceConstants.VERSION, maxversion);
					resultMap.put(MyPriceConstants.BSID, itemMap.get(String.valueOf(maxversion)));
				}
			}
		}catch (SalesBusinessException e) {
			log.error("exception occured in Myprice get deal version detail call : "+ e);
		}
		return resultMap;
	}
	
	public Map<String,Object> getDealRevisionDetail(String dealId,int version) {
		Map<String,Object> resultMap = new HashMap<>();
		try {
			String uri = env.getProperty("myprice.getDealLine.rest.url");
			System.out.println("Entered integer is arya: "+ 1);
			Map<String, String> headers = new HashMap<String, String>();
			headers.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.MYPRICE_AUTHORIZATION));
			headers.put(StringConstants.REQUEST_CONTENT_TYPE, "application/json");
			String fields = env.getProperty("myprice.getDeal.revision.fields");
			fields = fields.replace("{dealId}", dealId);
			fields = fields.replace("{versionNumber}", String.valueOf(version));
			String url = uri + fields;
			log.info("Get deal revision details request url: {}", org.apache.commons.lang3.StringUtils.normalizeSpace(url));
			String proxy = null;
			if(StringConstants.CONSTANT_Y.equalsIgnoreCase(isProxyEnabled)) {
				proxy = env.getProperty(CommonConstants.CDP_HTTP_PROXY);
			}
			String transResponse = restClient.callRestApi(null, url, "GET", headers, null, proxy);
			if (null != transResponse && !transResponse.isEmpty()) {
				GetDealVersionRevisionResponse response = (GetDealVersionRevisionResponse) restClient
						.processResult(transResponse, GetDealVersionRevisionResponse.class);
				if (response!=null && CollectionUtils.isNotEmpty(response.getItems())) {
					Set<Integer> revisiondata= new HashSet<Integer>();
					List<GetDealLineItem> itemList = response.getItems();
					Map<String,Object> itemMap= new HashMap<>();
					for (GetDealLineItem dealLineItem : itemList) {
						if(dealLineItem.getRevisionNumber()!=null && !dealLineItem.getRevisionNumber().isEmpty()) {
							revisiondata.add(Integer.parseInt(dealLineItem.getRevisionNumber()));
							itemMap.put(dealLineItem.getRevisionNumber(), dealLineItem.getBsId());
						}
					}
					int maxrevision=Collections.max(revisiondata);
					resultMap.put(MyPriceConstants.REVISION, maxrevision);
					resultMap.put(MyPriceConstants.BSID, itemMap.get(String.valueOf(maxrevision)));
					
				}
			}
		}catch (SalesBusinessException e) {
			log.error("exception occured in Myprice get deal revision detail call : "+ e);
		}
		
		return resultMap;
	}
	
	public Map<String,Object> getDealVersionRevisionDetail(String dealId, String version, String revision) {
		Map<String,Object> resultMap = new HashMap<>();
		try {
			String uri = env.getProperty("myprice.getDealLine.rest.url");
			Map<String, Object> queryParameters = new HashMap<String, Object>();
			Map<String, String> headers = new HashMap<String, String>();

			String encoded = Base64.getEncoder().encodeToString(
					(env.getProperty("myprice.username") + ":" + env.getProperty("myprice.password")).getBytes());
			headers.put(StringConstants.REQUEST_AUTHORIZATION, "Basic " + encoded);
			headers.put(StringConstants.REQUEST_CONTENT_TYPE, "application/json");
			String fields = env.getProperty("myprice.getTxn.fields");
			fields = fields.replace("{dealId}", dealId);
			//fields = fields.replace("{revisionNumber}", String.valueOf(revision));
			queryParameters.put(MyPriceConstants.PROXY, myPriceTransactionUtil.getProxy());
			String url = uri + fields;
			log.info("Get deal version details request url: {}", org.apache.commons.lang3.StringUtils.normalizeSpace(url));
			String proxy = null;
			if(StringConstants.CONSTANT_Y.equalsIgnoreCase(isProxyEnabled)) {
				proxy = env.getProperty(CommonConstants.CDP_HTTP_PROXY);
			}
			String transResponse = restClient.callRestApi(null, url, "GET", headers, null, proxy);

			if (null != transResponse && !transResponse.isEmpty()) {
				GetDealVersionRevisionResponse response = (GetDealVersionRevisionResponse) restClient.processResult(transResponse, GetDealVersionRevisionResponse.class);

				resultMap.put(MyPriceConstants.ITEM_EMPTY, "N");
				
				if (response!=null && CollectionUtils.isNotEmpty(response.getItems())) {
					Map<String,Object> itemMap= new HashMap<>();
					Set<Integer> versionData= new HashSet<Integer>();
					List<GetDealLineItem> itemList = response.getItems();
					for (GetDealLineItem dealLineItem : itemList) {
						versionData.add(dealLineItem.getVersion());
						if (String.valueOf(dealLineItem.getVersion()).equalsIgnoreCase(version) 
								&& dealLineItem.getRevisionNumber().equalsIgnoreCase(revision)) {
							itemMap.put(String.valueOf(dealLineItem.getVersion()), dealLineItem.getBsId());
							resultMap.put(MyPriceConstants.BSID,dealLineItem.getBsId());
						}
					}
					int maxversion=Collections.max(versionData);
					resultMap.put(MyPriceConstants.VERSION, maxversion);
				}else {
					
						  resultMap.put(MyPriceConstants.ITEM_EMPTY, "Y"); 
						
					}	
			}
		}catch (SalesBusinessException e) {
			log.error("exception occured in Myprice get deal version detail call : "+ e);
		}
		return resultMap;
	}
}
