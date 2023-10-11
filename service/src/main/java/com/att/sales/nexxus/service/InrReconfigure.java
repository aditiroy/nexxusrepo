package com.att.sales.nexxus.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.aft.dme2.internal.google.common.base.Strings;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxRequestGroupRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.fallout.model.FalloutDetailsRequest;
import com.att.sales.nexxus.model.UpdateTransactionOverrideRequest;
import com.att.sales.nexxus.myprice.transaction.model.CreateTransactionResponse;
import com.att.sales.nexxus.myprice.transaction.service.CopyTransactionServiceImpl;
import com.att.sales.nexxus.myprice.transaction.service.MyPriceTransactionUtil;

@Component
public class InrReconfigure {
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(InrReconfigure.class);
	
	@Autowired
	private MyPriceTransactionUtil myPriceTransactionUtil;
	
	@Autowired
	private NxRequestDetailsRepository nxRequestDetailsRepository;
	
	@Autowired 
	private CopyTransactionServiceImpl copyTransactionServiceImpl;
	
	@Autowired
	private NxMpDealRepository nxMpDealRepository;
	
	@Autowired
	private UpdateTransactionOverrideImpl updateTransactionOverrideImpl;
	
	@Autowired
	private ProcessINRtoMP processINRtoMP;
	
	@Autowired
	private MailServiceImpl mailServiceImpl;
	
	@Autowired
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;
	
	@Autowired
	private NxRequestGroupRepository nxRequestGroupRepository;
	
	public boolean isNewLocationAdded(List<Long> nxRequestGrpId, NxSolutionDetail nxSolutionDetail) {
		List<NxRequestDetails> nxRequestDetails = nxRequestDetailsRepository.findbyNSolutionIdAndActiveYnAndNxRequestGrpIds(nxSolutionDetail, StringConstants.CONSTANT_Y, nxRequestGrpId);
		
		List<NxRequestDetails> submittedRequest = nxRequestDetails.stream().filter(req -> MyPriceConstants.REQUEST_SUBMITTED_TO_MP_STATUS.contains(req.getStatus())).collect(Collectors.toList());
		
		List<NxRequestDetails> toBeSubmittedRequest = nxRequestDetails.stream().filter(req -> MyPriceConstants.REQUEST_STATUS.contains(req.getStatus())).collect(Collectors.toList());
		
		if(CollectionUtils.isEmpty(toBeSubmittedRequest)) {
			logger.info("No New request to submit to myprice {}", nxSolutionDetail.getNxSolutionId());
		}
		
		List<String> existingProducts = submittedRequest.stream().map(NxRequestDetails::getProduct).distinct().collect(Collectors.toList());
		List<String> newProducts = toBeSubmittedRequest.stream().map(NxRequestDetails::getProduct).distinct().collect(Collectors.toList());
		
		// if only new product , dont call copy
		
		// if same product , call copy
		
		// if same + new product call copy
		
		boolean result = existingProducts.stream().anyMatch(newProducts::contains); // if anymatch same product or same + new product added else new product 
		return result;
	}
	
	public void reconfigure(Map<String, Object> paramMap, CreateTransactionResponse createTransactionResponse, 
			NxMpDeal deal,NxSolutionDetail nxSolutionDetail, List<Long> nxRequestGrpId,Map<String, Object> requestMetaDataMap,
			FalloutDetailsRequest request){
		String source = paramMap.containsKey(MyPriceConstants.SOURCE) ? paramMap.get(MyPriceConstants.SOURCE).toString() : null;		
		String prodName = paramMap.containsKey(MyPriceConstants.PRODUCT_NAME) ? paramMap.get(MyPriceConstants.PRODUCT_NAME).toString() : null;		
		copyTransaction(paramMap, createTransactionResponse, deal,nxRequestGrpId, request);
		boolean status = paramMap.containsKey(MyPriceConstants.RESPONSE_STATUS) ? (boolean) paramMap.get(MyPriceConstants.RESPONSE_STATUS) : true;
		paramMap.remove(MyPriceConstants.RESPONSE_STATUS);
		Long nxAuditId = (Long) paramMap.get(MyPriceConstants.NX_AUDIT_ID);
		CreateTransactionResponse createTxnResponse = (CreateTransactionResponse) paramMap.get("createTransactionResponse");
		if (!Strings.isNullOrEmpty(request.getActionInd())
				&& request.getActionInd().equalsIgnoreCase(StringConstants.APPEND_TO_OTHER_DEAL)) {
			paramMap.put("CURRENT_DEALSTATUS", CommonConstants.CREATED);
		} else {
			paramMap.put("CURRENT_DEALSTATUS", deal.getDealStatus());
		}
		if(status) {
/*			Map<String, Object> configUpdateResMap = new HashMap<String, Object>();
			Map<String, Object> requestMetaDataMap = new HashMap<>();
			logger.info("inr reconfigure to MP nxTxnId {}", createTxnResponse.getNxTransacId()+" myPriceTxnId : "+createTxnResponse.getMyPriceTransacId());
			//requestMetaDataMap.put(MyPriceConstants.NX_TRANSACTION_ID, createTxnResponse.getNxTransacId());
			CompletableFuture.runAsync(() -> {
				processINRtoMP.process(nxSolutionDetail, nxRequestGrpId, requestMetaDataMap, configUpdateResMap, source, createTxnResponse,  nxAccessPricingDatas,prodName,true);
			});
			paramMap.put(MyPriceConstants.RESPONSE_STATUS, true);
			myPriceTransactionUtil.updateNxDesignAuditStatus(CommonConstants.SUCCESS, MyPriceConstants.AUDIT_UPDATE_CS,
					nxAuditId);*/
			if (!Strings.isNullOrEmpty(request.getActionInd())
					&& request.getActionInd().equalsIgnoreCase(StringConstants.APPEND_TO_OTHER_DEAL)) {
				String nxSolId=paramMap.get(StringConstants.NEW_NX_SOL_ID).toString();
				Long solId=Long.parseLong(nxSolId);
				logger.info("newSolutionId :::: {}", org.apache.commons.lang3.StringUtils.normalizeSpace(paramMap.get(StringConstants.NEW_NX_SOL_ID).toString()));
				nxSolutionDetail=nxSolutionDetailsRepository.findByNxSolutionId(solId);
				if(paramMap.containsKey(StringConstants.NEW_SOL_IND)) {
					nxRequestGrpId = nxRequestGroupRepository.findNxGroupIdByNxSolutionIdAndActiveYn(solId, StringConstants.CONSTANT_Y);
				}
			}
			processINR(nxSolutionDetail, nxRequestGrpId, source, createTxnResponse, prodName,true,nxAuditId,paramMap);
		}else {
			logger.info("inr reconfigure failed : While processing cleanSave : nxTxnId {} ",createTxnResponse.getNxTransacId());
			paramMap.put(MyPriceConstants.RESPONSE_STATUS, false);
			myPriceTransactionUtil.updateNxMpDealMpInd(StringConstants.CONSTANT_N,
					createTxnResponse.getNxTransacId());
			myPriceTransactionUtil.updateNxDesignAuditStatus(CommonConstants.FAILURE, MyPriceConstants.AUDIT_UPDATE_CS,
					nxAuditId);
			myPriceTransactionUtil.updateMpDealStatusByNxTxnId(CommonConstants.FAILED,createTxnResponse.getNxTransacId());
			NxMpDeal nxMpDeal = nxMpDealRepository.findByNxTxnId(createTransactionResponse.getNxTransacId());
			if(Optional.ofNullable(nxMpDeal).isPresent()) {
				nxMpDeal.setDealStatus(CommonConstants.FAILED);
				nxMpDeal.setModifiedDate(new Date());
				nxMpDealRepository.saveAndFlush(nxMpDeal);
			}
			mailServiceImpl.prepareMyPriceDealSubmissionRequest(nxMpDeal);

		}
	}
	
	public void copyTransaction(Map<String, Object> paramMap, CreateTransactionResponse createTransactionResponse, NxMpDeal deal,List<Long> nxRequestGrpId, FalloutDetailsRequest request){
		paramMap.put(StringConstants.MY_PRICE_TRANS_ID, deal.getTransactionId());
		paramMap.put(StringConstants.MY_PRICE_DEAL_ID, deal.getDealID());
		copyTransactionServiceImpl.copyTransaction(paramMap, request);
		
		if(paramMap.containsKey(StringConstants.NEW_MY_PRICE_DEAL)) {
			if (!Strings.isNullOrEmpty(request.getActionInd()) && paramMap.containsKey("ACTION_IND")
					&& paramMap.get("ACTION_IND").toString().equalsIgnoreCase(request.getActionInd())) {
			} else {
				deal.setActiveYN("N");
				nxMpDealRepository.save(deal);
			}
			NxMpDeal newDeal = (NxMpDeal) paramMap.get(StringConstants.NEW_MY_PRICE_DEAL);
			myPriceTransactionUtil.createTransactionResponse(createTransactionResponse, newDeal, deal.getOfferId());
			paramMap.put("createTransactionResponse", createTransactionResponse);
			logger.info("copyTransaction to MP nxSolutionId {}", newDeal.getSolutionId()+" nxTxnId : "+createTransactionResponse.getNxTransacId()+" myPriceTxnId : "+createTransactionResponse.getMyPriceTransacId());
			//call clean save if it is failed
			String callCleanSave = paramMap.containsKey("CALL_CLEAN_SAVE") ? (String) paramMap.get("CALL_CLEAN_SAVE") : null;
			if(StringConstants.CONSTANT_Y.equalsIgnoreCase(callCleanSave)) {
				paramMap.put("createTransactionRes", createTransactionResponse);
			    NxSolutionDetail nxSolutionDetail = (paramMap.containsKey(StringConstants.SOLUTIONDETAILS) && null != paramMap.get(StringConstants.SOLUTIONDETAILS))? (NxSolutionDetail) paramMap.get(StringConstants.SOLUTIONDETAILS) : null;
			    if (!Strings.isNullOrEmpty(request.getActionInd())
						&& request.getActionInd().equalsIgnoreCase(StringConstants.APPEND_TO_OTHER_DEAL)) {
					String nxSolId=paramMap.get(StringConstants.NEW_NX_SOL_ID).toString();
					Long solId=Long.parseLong(nxSolId);
					logger.info("newSolutionId :::: {}",paramMap.get(StringConstants.NEW_NX_SOL_ID).toString());
					nxSolutionDetail=nxSolutionDetailsRepository.findByNxSolutionId(solId);
				}
			    Map<String, Object> updateResponse = myPriceTransactionUtil.callUpdateCleanSave(nxSolutionDetail, paramMap, nxRequestGrpId);
				Boolean updateStatus =  updateResponse.containsKey(MyPriceConstants.RESPONSE_STATUS) ? (Boolean) updateResponse.get(MyPriceConstants.RESPONSE_STATUS) : false;
				if (!updateStatus) {
					paramMap.put(MyPriceConstants.RESPONSE_STATUS, false);
					return;
				} /*else {
					Long nxAuditId = (Long) paramMap.get(MyPriceConstants.NX_AUDIT_ID);
					String source = (String) paramMap.get(MyPriceConstants.SOURCE);
				    List<NxAccessPricingData> nxAccessPricingDatas = (paramMap.containsKey(MyPriceConstants.NX_ACCESS_PRICING_DATA) && null != paramMap.get(MyPriceConstants.NX_ACCESS_PRICING_DATA))? (List<NxAccessPricingData> ) paramMap.get(MyPriceConstants.NX_ACCESS_PRICING_DATA) : null;
					processINR(nxSolutionDetail, nxRequestGrpId, source, createTransactionResponse, nxAccessPricingDatas, createTransactionResponse.getOfferName(), true, nxAuditId, paramMap);
				}*/
			}
			paramMap.put(MyPriceConstants.TRANSACTION_TYPE, MyPriceConstants.AUDIT_UPDATE_OR_RECONFIGURE);
			UpdateTransactionOverrideRequest overrideRequest = myPriceTransactionUtil.prepareUpdateTransactionOverrideRequest(newDeal, paramMap, null, null, newDeal.getOfferId());
			ServiceResponse overrideRes = updateTransactionOverrideImpl.updateTransactionOverride(overrideRequest);
			if(null != overrideRes && CommonConstants.SUCCESS_STATUS.equals(overrideRes.getStatus().getCode())){
				paramMap.put(MyPriceConstants.RESPONSE_STATUS, true);
				return;
			}else {
				paramMap.put(MyPriceConstants.RESPONSE_STATUS, false);
				return;
			}
		}else {
			paramMap.put(MyPriceConstants.RESPONSE_STATUS, false);
			return;
		}
	}

	
	private void processINR(NxSolutionDetail nxSolutionDetail, List<Long> nxRequestGrpId, String source,
			CreateTransactionResponse createTxnResponse, String prodName, boolean isReconfigure,Long nxAuditId,Map<String, Object> paramMap) {
		Map<String, Object> configUpdateResMap = new HashMap<String, Object>();
		configUpdateResMap.put("iglooCount", paramMap.get("iglooCount"));
		Map<String, Object> requestMetaDataMap = new HashMap<>();
		String nxMpStatusInd = paramMap.containsKey("NX_MP_STATUS_IND") ? paramMap.get("NX_MP_STATUS_IND").toString() : null;
		logger.info("inr reconfigure to MP nxTxnId {}", createTxnResponse.getNxTransacId()+" myPriceTxnId : "+createTxnResponse.getMyPriceTransacId() +" nxMpStatusInd : "+nxMpStatusInd);
		requestMetaDataMap.put("NX_MP_STATUS_IND", nxMpStatusInd);
		requestMetaDataMap.put("CURRENT_DEALSTATUS", paramMap.containsKey("CURRENT_DEALSTATUS") ? paramMap.get("CURRENT_DEALSTATUS").toString() : null);
	//	CompletableFuture.runAsync(() -> {
			processINRtoMP.process(nxSolutionDetail, nxRequestGrpId, requestMetaDataMap, configUpdateResMap, source, createTxnResponse, prodName,isReconfigure);
	//	});
		paramMap.put(MyPriceConstants.RESPONSE_STATUS, true);
		myPriceTransactionUtil.updateNxDesignAuditStatus(CommonConstants.SUCCESS, MyPriceConstants.AUDIT_UPDATE_CS,
				nxAuditId);	
	}
}
