package com.att.sales.nexxus.myprice.transaction.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxDesignDetails;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxMpDesignDocument;
import com.att.sales.nexxus.dao.model.NxMpPriceDetails;
import com.att.sales.nexxus.dao.repository.NxDesignDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxMpDesignDocumentRepository;
import com.att.sales.nexxus.dao.repository.NxMpPriceDetailsRepository;
import com.att.sales.nexxus.myprice.transaction.model.GetTransactionLineItemPrice;
import com.att.sales.nexxus.myprice.transaction.model.TransactionPricingReqCriteria;
import com.att.sales.nexxus.myprice.transaction.model.TransactionPricingReqDocuments;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTransactionLineItem;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTransactionLinePricing;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTransactionPricingRequest;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTransactionPricingResponse;
import com.att.sales.nexxus.reteriveicb.model.Design;
import com.att.sales.nexxus.reteriveicb.model.PriceAttributes;
import com.att.sales.nexxus.reteriveicb.model.Site;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.RestClientUtil;

/**
 * 
 * @author Laxman Honawad
 *
 */
@Service("updateTransactionPricingFMOServiceImpl")
public class UpdateTransactionPricingFMOServiceImpl extends BaseServiceImpl {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(UpdateTransactionPricingFMOServiceImpl.class);

	/** The env. */
	@Autowired
	private Environment env;

	@Autowired
	private RestClientUtil restClient;

	@Autowired
	private MyPriceTransactionUtil myPriceTransactionUtil;

	@Autowired
	private NxMpDealRepository nxMpDealRepository;

	@Autowired
	private NxMpDesignDocumentRepository nxMpDesignDocumentRepository;

	@Autowired
	private NxMpPriceDetailsRepository nxMpPriceDetailsRepository;
	
	@Autowired
	private NxDesignDetailsRepository nxDesignDetailsRepository;
	
	@Autowired
	private HttpRestClient httpRestClient;
	
	@Value("${myprice.proxy.enabled}")
	private String isProxyEnabled;
	
	//private static final String ERATE_CUSTOM_PRICE_LIST = "E-Rate FMR";

	public UpdateTransactionPricingResponse updateTransactionPricingRequest(Map<String, Object> designMap){
		logger.info("Entering FMO updateTransactionPricingRequest() method");
		Map<String, Object> apiResponse = new HashMap<String, Object>();
		UpdateTransactionPricingResponse response = null;
		try {
/*			String myPriceTxnId = designMap.containsKey(MyPriceConstants.MP_TRANSACTION_ID)
					? designMap.get(MyPriceConstants.MP_TRANSACTION_ID).toString()
					: null;
			Long nxDesignId = designMap.containsKey(MyPriceConstants.NX_DESIGN_ID)
					? (Long) designMap.get(MyPriceConstants.NX_DESIGN_ID)
					: 0L;
			Long nxTxnId = designMap.containsKey(MyPriceConstants.NX_TRANSACTION_ID)
					? (Long) designMap.get(MyPriceConstants.NX_TRANSACTION_ID)
					: 0L;
			Long priceScenarioId = designMap.containsKey(StringConstants.PRICE_SCENARIO_ID)
					? (Long) designMap.get(StringConstants.PRICE_SCENARIO_ID)
					: 0L;
			String offerType = designMap.containsKey(MyPriceConstants.OFFER_TYPE)
					? designMap.get(MyPriceConstants.OFFER_TYPE).toString()
					: null;*/
			
			Long nxSolutionId = designMap.containsKey(MyPriceConstants.NX_SOLIUTION_ID)
					? (Long) designMap.get(MyPriceConstants.NX_SOLIUTION_ID)
					: null;
					
			List<NxMpDeal> deals = nxMpDealRepository.findBySolutionId(nxSolutionId);
			NxMpDeal deal = null;
			if(CollectionUtils.isNotEmpty(deals)) {
				deal = deals.get(0);
				UpdateTransactionPricingRequest request = getRequest(designMap,nxSolutionId,deal);
				if (null != request) {
					apiResponse = this.callUpdateTransactionPricingRequestApi(deal.getTransactionId(), request);
					int code = (apiResponse.containsKey(MyPriceConstants.RESPONSE_CODE)
							&& null != apiResponse.get(MyPriceConstants.RESPONSE_CODE))
									? (int) apiResponse.get(MyPriceConstants.RESPONSE_CODE)
									: 0;
					String transResponse = (apiResponse.containsKey(MyPriceConstants.RESPONSE_DATA)
							&& null != apiResponse.get(MyPriceConstants.RESPONSE_DATA))
									? (String) apiResponse.get(MyPriceConstants.RESPONSE_DATA)
									: null;
					if ((null != transResponse && code == CommonConstants.SUCCESS_CODE) || code == 0) {
						if (code != 0) {
							response = (UpdateTransactionPricingResponse) restClient.processResult(transResponse,
									UpdateTransactionPricingResponse.class);
							setSuccessResponse(response);
						}
						designMap.put(MyPriceConstants.RESPONSE_STATUS, true);
					} else {
						designMap.put(MyPriceConstants.RESPONSE_STATUS, false);
						designMap.put(MyPriceConstants.MP_API_ERROR, true);
						designMap.put(MyPriceConstants.UPDATE_PRICING_FAILED, true);
					}
					designMap.put(MyPriceConstants.RESPONSE_CODE, code);
					designMap.put(MyPriceConstants.RESPONSE_MSG,
							(apiResponse.containsKey(MyPriceConstants.RESPONSE_MSG)
									&& null != apiResponse.get(MyPriceConstants.RESPONSE_MSG))
											? (String) apiResponse.get(MyPriceConstants.RESPONSE_MSG)
											: null);
					designMap.put(MyPriceConstants.RESPONSE_DATA, transResponse);
				} else {
					logger.info("Design / Prices are not matching : updateTransactionPricingRequest myPrice call is not invoked for the transaction id : {}", deal.getTransactionId());
					designMap.put(MyPriceConstants.RESPONSE_MSG,
							"Design / Prices are not matching : Update Prcing api is not invoked for transaction id : "
									+ deal.getTransactionId());
					designMap.put(MyPriceConstants.RESPONSE_STATUS, false);
				}

			}
			
		} catch (Exception e) {
			logger.error("exception occured in Myprice updateTransactionPricingRequest " + e.getMessage());
			designMap.put(MyPriceConstants.RESPONSE_STATUS, false);
			designMap.put(MyPriceConstants.RESPONSE_MSG, "exception occured in Myprice updateTransactionPricingRequest");
			designMap.put(MyPriceConstants.NEXXUS_API_ERROR, true);
			designMap.put(MyPriceConstants.UPDATE_PRICING_FAILED, true);
		}
		logger.info("Existing FMO updateTransactionPricingRequest() method");
		return response;

	}

	@Transactional
	public UpdateTransactionPricingRequest getRequest(Map<String, Object> designMap,Long nxSolutionId,NxMpDeal deal) {
		List<UpdateTransactionLineItem> items = new ArrayList<>();
		List<NxDesignDetails> designDetails = nxDesignDetailsRepository.findDesignDetailsaByNxSolutionId(nxSolutionId);
		if(CollectionUtils.isNotEmpty(designDetails)) {
		for(NxDesignDetails designDetail : designDetails ) {
			logger.info("UTP-Request myPriceTxnId : {}",deal.getTransactionId());
			//String designData = nxMpDealRepository.findDesignDataByDesignIdAndComponentId(nxDesignId, designMap.get(FmoConstants.PORT_ID).toString(), offerType);
			Site site = null;
			if (Optional.ofNullable(designDetail).isPresent() && Optional.ofNullable(designDetail.getDesignData()).isPresent()) {
				site = JacksonUtil.fromStringForCodeHaus(designDetail.getDesignData(), Site.class);
				if (Optional.ofNullable(site).isPresent()) {
					if (CollectionUtils.isNotEmpty(site.getDesign())) {
						List<PriceAttributes> priceAttributes = new ArrayList<>();
						for (Design design : site.getDesign()) {
							if (CollectionUtils.isNotEmpty(design.getPriceDetails())) {
								for (PriceAttributes priceDetails : design.getPriceDetails()) {
									priceAttributes.add(priceDetails);
								}
							}
						}
						items.addAll(processFMOPriceDetails(priceAttributes, deal.getPriceScenarioId(), designDetail.getNxDesign().getNxDesignId(), deal.getNxTxnId(), designMap,
								designDetail.getProductName(), site.getThirdPartyInd(),designDetail.getComponentId()));
					}
				}
			}
		}}
		if (CollectionUtils.isNotEmpty(items)) {
			UpdateTransactionLinePricing transactionLine = new UpdateTransactionLinePricing();
			transactionLine.setItems(items);
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
		return null;

	}

	public List<UpdateTransactionLineItem> processFMOPriceDetails(List<PriceAttributes> priceAttributes,Long priceScenarioId, Long nxDesignId, Long nxTxnId, Map<String, Object> designMap, String flowType,
			String thirdPartyInd,String portId) {
		List<UpdateTransactionLineItem> items = new ArrayList<UpdateTransactionLineItem>();
		logger.info("FMO processFMOPriceDetails() entered");
		if (CollectionUtils.isNotEmpty(priceAttributes)) {
			//String portId = designMap.containsKey(FmoConstants.PORT_ID) ? designMap.get(FmoConstants.PORT_ID).toString() : null;
			logger.info("FMO priceAttributes size : {}",priceAttributes.size()," portId : {}",portId);
			List<NxMpDesignDocument> designDocuments = nxMpDesignDocumentRepository.findByNxTxnIdAndNxDesignIdAndMpPartNumber(nxTxnId, nxDesignId, portId);
			if(CollectionUtils.isNotEmpty(designDocuments)) {
				logger.info("FMO designDocuments size : {}",designDocuments.size());
				for(NxMpDesignDocument designDocument : designDocuments) {
					if(StringUtils.isNotEmpty(designDocument.getUsocId())) {
						//We need get respective priceAttributes wrt referencePortId then create record in price details and send to MyPrice
						for(PriceAttributes priceAttribute : priceAttributes) {
							if(null != priceAttribute.getReferencePortId() && portId.equalsIgnoreCase(priceAttribute.getReferencePortId().toString())
									&& (((MyPriceConstants.ADI.equalsIgnoreCase(flowType) || MyPriceConstants.BVoIP.equalsIgnoreCase(flowType)) && null != priceAttribute.getProductRateId() && designDocument.getUsocId().equalsIgnoreCase(priceAttribute.getProductRateId().toString()))
									|| designDocument.getUsocId().equalsIgnoreCase(priceAttribute.getBeid()))) {
								logger.info("portId & referencePortId and usocId & beid : Equal : items size : "+items.size());
								GetTransactionLineItemPrice getTransactionLineItemPrice = getTransactionLineItemPrice(priceAttribute);
								getUpdateTransactionLineItem(items, getTransactionLineItemPrice, priceAttribute, designDocument, nxTxnId, designMap, flowType);
								
								//Also if priceAttributes contains nrcBeid then need to get respective priceAttributes block by matching beid 
								//then create new record in price details and send to MyPrice
								if(StringUtils.isNotEmpty(priceAttribute.getNrcBeid())) {
									for(PriceAttributes nrcPriceAttribute : priceAttributes) {
										if("NRC".equalsIgnoreCase(nrcPriceAttribute.getFrequency()) 
												&& (((MyPriceConstants.ADI.equalsIgnoreCase(flowType) || MyPriceConstants.BVoIP.equalsIgnoreCase(flowType)) && null != nrcPriceAttribute.getProductRateId() && priceAttribute.getNrcBeid().equalsIgnoreCase(nrcPriceAttribute.getProductRateId().toString()))
												|| priceAttribute.getNrcBeid().equalsIgnoreCase(nrcPriceAttribute.getBeid()))) {
											GetTransactionLineItemPrice getNrcTransactionLineItemPrice = getTransactionLineItemPrice(nrcPriceAttribute);
											getUpdateTransactionLineItem(items, getNrcTransactionLineItemPrice, nrcPriceAttribute, designDocument, nxTxnId, designMap, flowType);
										}
									}
								}
							}
						}
					} else {
						//if usoc id is empty, we need to create record in price details for access rates, so during getTransactionLine we can update approved prices.
						for(PriceAttributes priceAttribute : priceAttributes) {
							if(null != priceAttribute.getReferencePortId() && portId.equalsIgnoreCase(priceAttribute.getReferencePortId().toString())
									&& ("Access".equalsIgnoreCase(priceAttribute.getRdsPriceType()) || "ETHERNET_ACCESS".equalsIgnoreCase(priceAttribute.getRdsPriceType()))) {
								
								if("Access".equalsIgnoreCase(priceAttribute.getRdsPriceType())  && MyPriceConstants.ADI.equalsIgnoreCase(flowType)) {
									GetTransactionLineItemPrice getTransactionLineItemPrice = getTransactionLineItemPrice(priceAttribute);
									getUpdateTransactionLineItem(items, getTransactionLineItemPrice, priceAttribute, designDocument, nxTxnId, designMap, flowType);
								}else {
									savePriceDetials(nxTxnId, designDocument, priceAttribute, designMap, flowType);
								}
							}
						}
					}
				}
			}
		}
		return items;
	}
	
	public GetTransactionLineItemPrice getTransactionLineItemPrice(PriceAttributes priceAttribute) {
		GetTransactionLineItemPrice getTransactionLineItemPrice = null;
		
		if (null != priceAttribute.getFrequency()) {
			if ("MRC".equalsIgnoreCase(priceAttribute.getFrequency())) {
				getTransactionLineItemPrice = new GetTransactionLineItemPrice();
				if (Optional.ofNullable(priceAttribute.getLocalListPrice()).isPresent()
						&& Optional.ofNullable(priceAttribute.getIcbDesiredDiscPerc()).isPresent()) {
					Float value = (float) ((100 - priceAttribute.getIcbDesiredDiscPerc())
							* priceAttribute.getLocalListPrice() / 100);
					getTransactionLineItemPrice.setValue(value);
				} else if (Optional.ofNullable(priceAttribute.getRequestedRate()).isPresent()) {
					getTransactionLineItemPrice.setValue(priceAttribute.getRequestedRate());
				}
			}
			if ("NRC".equalsIgnoreCase(priceAttribute.getFrequency())) {
				getTransactionLineItemPrice = new GetTransactionLineItemPrice();
				if (Optional.ofNullable(priceAttribute.getLocalListPrice()).isPresent()
						&& Optional.ofNullable(priceAttribute.getIcbDesiredDiscPerc()).isPresent()) {
					Float value = (float) ((100 - priceAttribute.getIcbDesiredDiscPerc())
							* priceAttribute.getLocalListPrice() / 100);
					getTransactionLineItemPrice.setValue(value);
				} else if (Optional.ofNullable(priceAttribute.getRequestedRate()).isPresent()) {
					getTransactionLineItemPrice.setValue(priceAttribute.getRequestedRate());
				}
			}
			
		}
		return getTransactionLineItemPrice;
	}
	
	public void getUpdateTransactionLineItem(List<UpdateTransactionLineItem> items, GetTransactionLineItemPrice getTransactionLineItemPrice, 
			PriceAttributes priceAttribute, NxMpDesignDocument designDocument, 
			Long nxTxnId, Map<String, Object> designMap, String flowType) {
		UpdateTransactionLineItem transactionLineItem = null;
		if (null != getTransactionLineItemPrice
				&& Optional.ofNullable(getTransactionLineItemPrice.getValue()).isPresent()) {
			logger.info("FMO getUpdateTransactionLineItem() :: price value : "+getTransactionLineItemPrice.getValue());
			boolean exists = false;
			getTransactionLineItemPrice.setCurrency(priceAttribute.getLocalCurrency());
			for (UpdateTransactionLineItem item : items) {
				if (item.getDocumentNumber()
						.equalsIgnoreCase(designDocument.getMpDocumentNumber().toString())) {
					exists = true;
					if ("MRC".equalsIgnoreCase(priceAttribute.getFrequency())) {
						item.setRequestedEffectivePriceMRC(getTransactionLineItemPrice);
					} else {
						item.setRequestedEffectivePriceNRC(getTransactionLineItemPrice);
					}
				}
			}
			if (!exists) {
				transactionLineItem = new UpdateTransactionLineItem();
				transactionLineItem
						.setDocumentNumber(designDocument.getMpDocumentNumber().toString());
				if ("MRC".equalsIgnoreCase(priceAttribute.getFrequency())) {
					transactionLineItem.setRequestedEffectivePriceMRC(getTransactionLineItemPrice);
				} else {
					transactionLineItem.setRequestedEffectivePriceNRC(getTransactionLineItemPrice);
				}
				if(null != priceAttribute.getTerm()) {
					transactionLineItem.setTerm(priceAttribute.getTerm());
				}
				items.add(transactionLineItem);
			}
			savePriceDetials(nxTxnId, designDocument, priceAttribute, designMap, flowType);
		}
	}
	
	public void savePriceDetials(Long nxTxnId, NxMpDesignDocument nxMpDesignDocument, PriceAttributes priceAttribute,
			Map<String, Object> paramMap, String flowType) {
		logger.info("FMO savePriceDetials() entered");
		NxMpPriceDetails nxMpPriceDetails = new NxMpPriceDetails();
		if (paramMap.containsKey(StringConstants.TRANSACTION_UPDATE) && paramMap.get(StringConstants.TRANSACTION_UPDATE)
				.toString().equalsIgnoreCase(StringConstants.RECONFIGURE)) {
			nxMpPriceDetails = nxMpPriceDetailsRepository.findByMpDocumentNumberAndNxTxnIdAndFrequency(
					nxMpDesignDocument.getMpDocumentNumber(), nxTxnId, priceAttribute.getFrequency());
			if (null != nxMpPriceDetails) {
				logger.info("FMO savePriceDetials reconfigure : "+nxMpPriceDetails.getNxPriceDetailsId());
				nxMpPriceDetails.setRequestedMRCDiscPercentage(priceAttribute.getRequestedMRCDiscPercentage());
				nxMpPriceDetails.setRequestedNRCDiscPercentage(priceAttribute.getRequestedNRCDiscPercentage());
				nxMpPriceDetails.setFrequency(priceAttribute.getFrequency());
				nxMpPriceDetails.setBeid(priceAttribute.getBeid());
				nxMpPriceDetails.setComponentType(priceAttribute.getComponentType());
				/** componentId as referencePortId for FMO */
				nxMpPriceDetails.setComponentId(priceAttribute.getReferencePortId());
				nxMpPriceDetails.setComponentParentId(priceAttribute.getComponentParentId());
				if ("MRC".equalsIgnoreCase(priceAttribute.getFrequency())) {
					nxMpPriceDetails.setRequestedMRCEffectivePrice(priceAttribute.getRequestedRate());
				} else {
					nxMpPriceDetails.setRequestedNRCEffectivePrice(priceAttribute.getRequestedRate());
				}
				nxMpPriceDetails.setRdsPriceType(priceAttribute.getRdsPriceType());
				nxMpPriceDetails.setProdRateId(priceAttribute.getProductRateId());
				nxMpPriceDetails.setAsrItemId(null!=priceAttribute.getComponentParentId()?priceAttribute.getComponentParentId().toString():null);
				nxMpPriceDetails.setNxSiteCountry(priceAttribute.getCountry());
				nxMpPriceDetailsRepository.save(nxMpPriceDetails);
			}
		} else {
			logger.info("FMO savePriceDetials configure : "+nxMpPriceDetails.getNxPriceDetailsId());
			nxMpPriceDetails.setNxTxnId(nxMpDesignDocument.getNxTxnId());
			nxMpPriceDetails.setNxDesignId(nxMpDesignDocument.getNxDesignId());
			nxMpPriceDetails.setMpDocumentNumber(nxMpDesignDocument.getMpDocumentNumber());
			nxMpPriceDetails.setRequestedMRCDiscPercentage(priceAttribute.getRequestedMRCDiscPercentage());
			nxMpPriceDetails.setRequestedNRCDiscPercentage(priceAttribute.getRequestedNRCDiscPercentage());
			nxMpPriceDetails.setFrequency(priceAttribute.getFrequency());
			nxMpPriceDetails.setBeid(priceAttribute.getBeid());
			nxMpPriceDetails.setComponentType(priceAttribute.getComponentType());
			/** componentId as referencePortId for FMO */
			nxMpPriceDetails.setComponentId(priceAttribute.getReferencePortId());
			nxMpPriceDetails.setComponentParentId(priceAttribute.getComponentParentId());
			if ("MRC".equalsIgnoreCase(priceAttribute.getFrequency())) {
				nxMpPriceDetails.setRequestedMRCEffectivePrice(priceAttribute.getRequestedRate());
			} else {
				nxMpPriceDetails.setRequestedNRCEffectivePrice(priceAttribute.getRequestedRate());
			}
			nxMpPriceDetails.setRdsPriceType(priceAttribute.getRdsPriceType());
			nxMpPriceDetails.setProdRateId(priceAttribute.getProductRateId());
			nxMpPriceDetails.setAsrItemId(null!=priceAttribute.getComponentParentId()?priceAttribute.getComponentParentId().toString():null);
			nxMpPriceDetails.setNxSiteCountry(priceAttribute.getCountry());
			nxMpPriceDetailsRepository.save(nxMpPriceDetails);
		}
	}

	protected Map<String, Object> callUpdateTransactionPricingRequestApi(String myPriceTxnId,
			UpdateTransactionPricingRequest request) throws SalesBusinessException {
		Map<String, Object> response = new HashMap<String, Object>();
		String uri = env.getProperty("myprice.updateTransactionPricingRequest");
		uri = uri.replace("{transactionId}", myPriceTxnId);
		String requestString = JacksonUtil.toString(request);
		logger.info("FMO Json of UT Pricing request :========>>>>>{}", requestString);
		try {
			Map<String, String> headers  = new HashMap<>();
			headers.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.MYPRICE_AUTHORIZATION));
			headers.put(StringConstants.REQUEST_CONTENT_TYPE, "application/json");
			String proxy = null;
			if(StringConstants.CONSTANT_Y.equalsIgnoreCase(isProxyEnabled)) {
				proxy = env.getProperty(CommonConstants.CDP_HTTP_PROXY);
			}
			String res = httpRestClient.callHttpRestClient(uri,  HttpMethod.POST, null, requestString, 
					headers, proxy);
			response.put(MyPriceConstants.RESPONSE_CODE, "200");
			response.put(MyPriceConstants.RESPONSE_DATA, res);
		} catch (SalesBusinessException e) {
			logger.error("exception occured in Myprice FMO updateTransactionPricingRequest call");
			response.put(MyPriceConstants.RESPONSE_CODE, e.getHttpErrorCode());
			response.put(MyPriceConstants.RESPONSE_MSG, e.getMessage());
			response.put(MyPriceConstants.NEXXUS_API_ERROR, true);
			response.put(MyPriceConstants.UPDATE_PRICING_FAILED, true);
		}
		return response;
	}

}
