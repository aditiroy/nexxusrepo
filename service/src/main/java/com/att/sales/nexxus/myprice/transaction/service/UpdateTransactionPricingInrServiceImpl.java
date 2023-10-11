package com.att.sales.nexxus.myprice.transaction.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxMpConfigMapping;
import com.att.sales.nexxus.dao.model.NxMpDesignDocument;
import com.att.sales.nexxus.dao.model.NxMpPriceDetails;
import com.att.sales.nexxus.dao.repository.NxMpConfigMappingRepository;
import com.att.sales.nexxus.dao.repository.NxMpDesignDocumentRepository;
import com.att.sales.nexxus.dao.repository.NxMpPriceDetailsRepository;
import com.att.sales.nexxus.myprice.transaction.model.GetTransactionLineItemPrice;
import com.att.sales.nexxus.myprice.transaction.model.TransactionPricingReqCriteria;
import com.att.sales.nexxus.myprice.transaction.model.TransactionPricingReqDocuments;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTransactionLineItem;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTransactionLinePricing;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTransactionPricingRequest;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTransactionPricingResponse;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.att.sales.nexxus.util.RestClientUtil;

@Service("updateTransactionPricingInrServiceImpl")
public class UpdateTransactionPricingInrServiceImpl extends BaseServiceImpl {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(UpdateTransactionPricingInrServiceImpl.class);
	
	@Autowired
	private NxMpConfigMappingRepository nxMpConfigMappingRepository;
	
	@Autowired
	private NexxusJsonUtility nexxusJsonUtility;
	
	@Autowired
	private RestClientUtil restClient;
	
	@Autowired
	private UpdateTransactionPricingServiceImpl updateTransactionPricingService;
	
	@Autowired
	private NxMpDesignDocumentRepository nxMpDesignDocumentRepository;
	
	@Autowired
	private NxMpPriceDetailsRepository nxMpPriceDetailsRepository;
	
	
	public UpdateTransactionPricingResponse updateTransactionPricingRequest(Map<String, Object> designMap,JSONObject inputJson) 
			throws SalesBusinessException {
		UpdateTransactionPricingResponse response = null;
		Map<String, Object> apiResponse = new HashMap<String, Object>();
		try {
			String myPriceTxnId = designMap.containsKey(MyPriceConstants.MP_TRANSACTION_ID) ? 
					designMap.get(MyPriceConstants.MP_TRANSACTION_ID).toString() : null;
			Long nxTxnId = designMap.containsKey(MyPriceConstants.NX_TRANSACTION_ID) ? 
					(Long)designMap.get(MyPriceConstants.NX_TRANSACTION_ID) : 0L;
			Long nxDesignId = designMap.containsKey(MyPriceConstants.NX_DESIGN_ID) ?
					(Long)designMap.get(MyPriceConstants.NX_DESIGN_ID) : 0L;
			//myPriceTransactionUtil.setSystemProperties();
			UpdateTransactionPricingRequest request = getRequest(myPriceTxnId,nxTxnId,nxDesignId,designMap,inputJson);
			if(null != request) {
				apiResponse = updateTransactionPricingService.callUpdateTransactionPricingRequestApi(myPriceTxnId,request);
				int code = (apiResponse.containsKey(MyPriceConstants.RESPONSE_CODE) && null != apiResponse.get(MyPriceConstants.RESPONSE_CODE)) ?
						(int) apiResponse.get(MyPriceConstants.RESPONSE_CODE) : 0;
				String transResponse = (apiResponse.containsKey(MyPriceConstants.RESPONSE_DATA) && null != apiResponse.get(MyPriceConstants.RESPONSE_DATA)) ?
						(String) apiResponse.get(MyPriceConstants.RESPONSE_DATA) : null;
				if ((null != transResponse && code == CommonConstants.SUCCESS_CODE) || code == 0) {
					if(code != 0) {
						response = (UpdateTransactionPricingResponse) restClient.processResult(transResponse,
								UpdateTransactionPricingResponse.class);
						setSuccessResponse(response);
					}
					designMap.put(MyPriceConstants.RESPONSE_STATUS, true);
				}else {
					designMap.put(MyPriceConstants.RESPONSE_STATUS, false);
				}
				designMap.put(MyPriceConstants.RESPONSE_CODE, code);
				designMap.put(MyPriceConstants.RESPONSE_MSG, (apiResponse.containsKey(MyPriceConstants.RESPONSE_MSG) && null != apiResponse.get(MyPriceConstants.RESPONSE_MSG)) ?
						(String) apiResponse.get(MyPriceConstants.RESPONSE_MSG) : null);
				designMap.put(MyPriceConstants.RESPONSE_DATA, transResponse);
			}else {
				logger.info("Design / Prices are not matching : updateTransactionPricingRequest myPrice call is not invoked for the transaction id : "+myPriceTxnId);
				designMap.put(MyPriceConstants.RESPONSE_MSG, "Design / Prices are not matching : Update Prcing api is not invoked for transaction id : "+myPriceTxnId);
				designMap.put(MyPriceConstants.RESPONSE_STATUS, false);
			}			
		} catch (Exception e) {
			logger.error("exception occured in Myprice updateTransactionPricingRequest "+e.getMessage());
			designMap.put(MyPriceConstants.RESPONSE_STATUS, false);
			designMap.put(MyPriceConstants.RESPONSE_MSG, "exception occured in Myprice updateTransactionPricingRequest");
		}
		
		return response;
	}

	public UpdateTransactionPricingRequest getRequest(String myPriceTxnId,Long nxTxnId,Long nxDesignId, Map<String, Object> paramMap,JSONObject inputJson) {
		UpdateTransactionLinePricing transactionLine = new UpdateTransactionLinePricing();
		transactionLine.setItems(this.processPriceDetails(paramMap,nxTxnId,nxDesignId,inputJson));
		TransactionPricingReqDocuments documents = new TransactionPricingReqDocuments();
		documents.setTransactionLine(transactionLine);
		UpdateTransactionPricingRequest request = new UpdateTransactionPricingRequest();
		request.setDocuments(documents);
		TransactionPricingReqCriteria criteria= new TransactionPricingReqCriteria();
		criteria.setLimit(1);
		List<String> fieldList=new ArrayList<>();
		fieldList.add(MyPriceConstants.DOCUMENT_NUMBER);
		criteria.setFields(fieldList);
		request.setCriteria(criteria);
		return request;
	}
	
	@SuppressWarnings("unchecked")
	protected List<UpdateTransactionLineItem> processPriceDetails(Map<String, Object> paramMap,Long nxTxnId,Long nxDesignId,JSONObject inputJson){
		List<UpdateTransactionLineItem> items = new ArrayList<UpdateTransactionLineItem>(); 
		String productName=paramMap.get(MyPriceConstants.PRODUCT_NAME)!=null?
				paramMap.get(MyPriceConstants.PRODUCT_NAME).toString():null;
		Map<String,NxMpConfigMapping> rulesMap=this.getRuleData(productName);
		if(null!=inputJson) {
			JSONArray usocObjectLst=(JSONArray)inputJson.get("USOCInfo");
			Optional.ofNullable(usocObjectLst).ifPresent(i -> i.forEach(data -> {
				JSONObject usocObj = (JSONObject) data;
				String beid=this.getData(MyPriceConstants.USOC_ID, rulesMap, usocObj);
				String netRate=this.getData(MyPriceConstants.NET_RATE, rulesMap, usocObj);
				if(StringUtils.isNotEmpty(beid)) {
					NxMpDesignDocument nxMpDesignDocument = nxMpDesignDocumentRepository
							.findByTxnDesignUsocIds(nxTxnId, nxDesignId, beid);
					if(null!=nxMpDesignDocument) {
						UpdateTransactionLineItem transactionLineItem = null;
						GetTransactionLineItemPrice getTransactionLineItemPrice = null;
						if(StringUtils.isNotEmpty(netRate)) {
							getTransactionLineItemPrice = new GetTransactionLineItemPrice();
							getTransactionLineItemPrice.setValue(Float.valueOf(netRate));
							boolean exists = false;
							for(UpdateTransactionLineItem item : items) {
								if(item.getDocumentNumber().equalsIgnoreCase(nxMpDesignDocument.getMpDocumentNumber().toString())) {
									exists = true;
									item.setRequestedEffectivePriceMRC(getTransactionLineItemPrice);
								}
							}
							if(!exists) {
								transactionLineItem = new UpdateTransactionLineItem();
								transactionLineItem.setDocumentNumber(nxMpDesignDocument.getMpDocumentNumber().toString());
								transactionLineItem.setRequestedEffectivePriceMRC(getTransactionLineItemPrice);
								items.add(transactionLineItem);
							}
							paramMap.put(MyPriceConstants.USOC_ID, beid);
							paramMap.put(MyPriceConstants.NET_RATE, netRate);
							savePriceDetails(paramMap,nxMpDesignDocument,nxTxnId);
						
						}
					}
				}
			}));
		}
		return items;
	}
	
	protected Map<String,NxMpConfigMapping> getRuleData(String offerName){
		List<NxMpConfigMapping> rules=nxMpConfigMappingRepository.findByOfferAndRuleName(offerName,"PRICE");
		Map<String,NxMpConfigMapping> dataMap=new HashMap<String, NxMpConfigMapping>();
		for(NxMpConfigMapping x:rules) {
			dataMap.put(x.getVariableName(), x);
		}
		return dataMap;
	}
	
	protected String getData(String fieldName,Map<String,NxMpConfigMapping> rulesMap,JSONObject input) {
		NxMpConfigMapping mappingData=rulesMap.get(fieldName);
		String result=null;
		if(null!=mappingData) {
			if(StringUtils.isNotEmpty(mappingData.getType()) && 
					mappingData.getType().equals(MyPriceConstants.IS_DEFAULT)) {
				result= mappingData.getDefaultValue();
			}else if(StringUtils.isNotEmpty(mappingData.getPath())){
				result=this.getItemValueUsingJsonPath(mappingData.getPath(),input);
			}
			if(StringUtils.isEmpty(result)) {
				result=mappingData.getDefaultValue();
			}
		}
		return result;
	}
	

	/**
	 * Gets the item value using json path.
	 *
	 * @param jsonPath the json path
	 * @param inputDesignDetails the input design details
	 * @return the item value using json path
	 */
	protected String getItemValueUsingJsonPath(String jsonPath,JSONObject inputDesignDetails) {
		Object result=nexxusJsonUtility.getValue(inputDesignDetails, jsonPath);
		if(null!=result) {
			return String.valueOf(result);
		}
		return null;
	}
	
	protected void savePriceDetails(Map<String, Object> paramMap,NxMpDesignDocument nxMpDesignDocument,Long nxTxnId) {
		String beId=paramMap.get(MyPriceConstants.USOC_ID)!=null?paramMap.get(MyPriceConstants.USOC_ID).toString():null;
		String netRate=paramMap.get(MyPriceConstants.NET_RATE)!=null?paramMap.get(MyPriceConstants.NET_RATE).toString():null;
		NxMpPriceDetails nxMpPriceDetails = new NxMpPriceDetails();
		if(paramMap.containsKey(StringConstants.TRANSACTION_UPDATE) 
				&& paramMap.get(StringConstants.TRANSACTION_UPDATE).toString().equalsIgnoreCase(StringConstants.RECONFIGURE)){
			nxMpPriceDetails = nxMpPriceDetailsRepository.findByMpDocumentNumberAndNxTxnIdAndFrequency(
					nxMpDesignDocument.getMpDocumentNumber(),nxTxnId,MyPriceConstants.MRC);
			if(null != nxMpPriceDetails) {
				nxMpPriceDetails.setBeid(beId);
				nxMpPriceDetails.setRequestedMRCEffectivePrice(Float.valueOf(netRate));
				nxMpPriceDetailsRepository.save(nxMpPriceDetails);
			}
		}else {
			nxMpPriceDetails.setNxTxnId(nxMpDesignDocument.getNxTxnId());
			nxMpPriceDetails.setNxDesignId(nxMpDesignDocument.getNxDesignId());
			nxMpPriceDetails
					.setMpDocumentNumber(nxMpDesignDocument.getMpDocumentNumber());
			nxMpPriceDetails.setFrequency(MyPriceConstants.MRC);
			nxMpPriceDetails.setBeid(beId);
			nxMpPriceDetails.setRequestedMRCEffectivePrice(Float.valueOf(netRate));
			nxMpPriceDetailsRepository.save(nxMpPriceDetails);
		}
		
	}
	
}
