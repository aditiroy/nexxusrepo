package com.att.sales.nexxus.dmaap.publishaddress.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.constant.CommonConstants;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxMpSiteDictionary;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dmaap.publishaddress.model.Document;
import com.att.sales.nexxus.myprice.transaction.service.UpdateTxnSiteUploadServiceImpl;
import com.att.sales.nexxus.serviceValidation.service.GetQualificationServiceImpl;
import com.att.sales.nexxus.util.ThreadMetaDataUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

public class MyPriceExecutorService implements Callable<Object>{

	private static Logger log = LoggerFactory.getLogger(MyPriceExecutorService.class);
	
	private JSONObject inputObject;
	
	MyPriceExecutorService(JSONObject inputObject){
		this.inputObject=inputObject;
	}
	
	private NxMpDealRepository nxMpDealRepository;
	
	private MyPriceInitiatedFlow myPriceInitiatedFlow;
	
	private GetQualificationServiceImpl getQualificationServiceImpl;
	
	private UpdateTxnSiteUploadServiceImpl updateTxnSiteUploadServiceImpl;
	
	public void setNxMpDealRepository(NxMpDealRepository nxMpDealRepository) {
		this.nxMpDealRepository = nxMpDealRepository;
	}
	public void setMyPriceInitiatedFlow(MyPriceInitiatedFlow myPriceInitiatedFlow) {
		this.myPriceInitiatedFlow = myPriceInitiatedFlow;
	}
	public void setGetQualificationServiceImpl(GetQualificationServiceImpl getQualificationServiceImpl) {
		this.getQualificationServiceImpl = getQualificationServiceImpl;
	}
	public void setUpdateTxnSiteUploadServiceImpl(UpdateTxnSiteUploadServiceImpl updateTxnSiteUploadServiceImpl) {
		this.updateTxnSiteUploadServiceImpl = updateTxnSiteUploadServiceImpl;
	}


	@Override
	public Object call() throws Exception {
		try {
			this.processMessagesForDmapp(inputObject);
		} finally {
			ThreadMetaDataUtil.destroyThreadMetaData();
		}
		return null;
	}

	
	public void processMessagesForDmapp(JSONObject messageObject) throws JSONException, SalesBusinessException, JsonProcessingException {
		String uniqueId = (String) messageObject.get("uniqueId");
		log.info("Inside processMessagesForDmapp for transactionId:{}",uniqueId);
		// Get TransactionId as UniqueId from Dmaap
		String transactionId = uniqueId;
		NxMpDeal nxMpDealData = nxMpDealRepository.findByTransactionId(transactionId);
		NxMpDeal nxMpDeal = null;
		Long nxMpTxnId = null;
		if(nxMpDealData == null) {
			// Creating Entry with NX_Transaction_ID in NX_MP_DEAL
			 nxMpDeal = myPriceInitiatedFlow.createEntry(transactionId);
		}
		if(nxMpDealData != null) {
		 nxMpTxnId = nxMpDealData.getNxTxnId();
		}else {
			nxMpTxnId = nxMpDeal.getNxTxnId();
		}
		// creating DataMap to request to calling service
		Map<Object, Object> triggerRequestMap = new HashMap<>();
		Document document = new Document();
		document.setWiStatusUpdateQ(CommonConstants.TSI);

		triggerRequestMap.put("documents", document);

		String tRequestJson = myPriceInitiatedFlow.getupdateTransactionJSON(triggerRequestMap);
		
		Map<String, Object> requestParams = new HashMap<>();
		String conversationId = String.format("NEXXUSGETQUALREQUEST%s", uniqueId);
		String traceId = String.format("%d%d", nxMpTxnId, System.currentTimeMillis());
		requestParams.put(ServiceMetaData.XCONVERSATIONID, conversationId);
		requestParams.put(ServiceMetaData.REST_REQUEST_START_TIME, System.currentTimeMillis());
//		requestParams.put(ServiceMetaData.XTraceId, traceId);
//		requestParams.put(ServiceMetaData.XSpanId, traceId);
		
		if (ServiceMetaData.getRequestMetaData() != null) {
			log.info("ipne dmaap servicemetadata not null");
			ServiceMetaData.getRequestMetaData().forEach((key, value) -> log.info("ipne before add key {} {}", key, value) );
			ServiceMetaData.add(requestParams);
			ServiceMetaData.getRequestMetaData().forEach((key, value) -> log.info("ipne after add key {} {}", key, value) );
		}else {
			log.info("ipne dmaap servicemetadata null");
		}
		
		ThreadMetaDataUtil.initThreadMetaDataIfNull(requestParams);
		// calling service to initiate Transfer Sites
		String serviceResponse = myPriceInitiatedFlow.processSiteStatusUpdate(transactionId, tRequestJson);

		log.info("Service Response ===> \n" + serviceResponse);
				
		// update NX_MP_DEAL with createTransactionType Response Data for this nXMpTxnId
		if (null != serviceResponse && !serviceResponse.isEmpty()) {

			JSONObject jsonObject = new JSONObject(serviceResponse);
			JSONObject getDocuments = jsonObject.getJSONObject("documents");
			Object dealID = getDocuments.get("rd_requestID_q");
			Object revision = getDocuments.get("rd_revisionNumber_q");
			Object version = getDocuments.get("version_t");

			// update NX_MP_DEAL with createTransactionType Response Data for this nXMpTxnId
			NxMpDeal nxMpDealObj = nxMpDealRepository.findByNxTxnId(nxMpTxnId);
			nxMpDealObj.setDealID(dealID.toString());
			nxMpDealObj.setVersion(version.toString());
			nxMpDealObj.setRevision(revision.toString());
			
			if (!getDocuments.isNull("wi_OriginalClonedTxId")) {
				JSONObject clonedTxId = new JSONObject(getDocuments.get("wi_OriginalClonedTxId").toString());
				log.info("current cloned details is {}", clonedTxId);
				if(!clonedTxId.isNull("sourceId") && !transactionId.equalsIgnoreCase(clonedTxId.get("sourceId").toString()) && !clonedTxId.isNull("Action") && ("Clone".equalsIgnoreCase(clonedTxId.get("Action").toString()) || "CreateAdditionalRequest".equalsIgnoreCase(clonedTxId.get("Action").toString()))) {
					nxMpDealObj.setSourceId(clonedTxId.get("sourceId").toString());
					nxMpDealObj.setAction(clonedTxId.get("Action").toString());
				}
			}
			nxMpDealRepository.save(nxMpDealObj);

			// call IP&E getQualification
			
			String sourceSystem = (String) messageObject.get("sourceSystem");
			Integer motsId = (Integer) messageObject.get("motsId");  
			log.info("Calling getQualification for transaction :: {}", nxMpTxnId);
			Map<String, Object> resultMap = getQualificationServiceImpl.
					getQualification(sourceSystem, motsId, uniqueId, nxMpTxnId,nxMpDealObj);
			Boolean status = (Boolean) resultMap.get("status");
			NxMpSiteDictionary nxMpSiteDictionary = (resultMap.containsKey("siteData") 
					&& null != resultMap.get("siteData"))? (NxMpSiteDictionary) resultMap.get("siteData") : null;
			if(status && null != nxMpSiteDictionary) {
				log.info("Calling updateTransactionSiteUpload for transaction :: "+nxMpTxnId);
				updateTxnSiteUploadServiceImpl.updateTransactionSiteUpload(nxMpDealObj.getTransactionId(), 
						nxMpSiteDictionary.getSiteJson(), requestParams);
			}
		}
	
	}
}
