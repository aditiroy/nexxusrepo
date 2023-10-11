package com.att.sales.nexxus.myprice.transaction.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service("updateTransactionPricingFMORestServiceImpl")
public class UpdateTransactionPricingFMORestServiceImpl  extends BaseServiceImpl {
	
	private static final Logger logger = LoggerFactory.getLogger(UpdateTransactionPricingFMORestServiceImpl.class);
	
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
	private ObjectMapper mapper;
	
	@Autowired 
	private ConfigAndUpdatePricingUtil configAndUpdatePricingUtil;
	
	@Autowired
	private RestCommonUtil restCommonUtil;
	
	@Autowired
	private HttpRestClient httpRestClient;
	
	@Value("${myprice.proxy.enabled}")
	private String isProxyEnabled;
	
	@SuppressWarnings("unchecked")
	public UpdateTransactionPricingResponse updateTransactionPricingRequest(Map<String, Object> designMap){
		logger.info("Entering FMO Rest updateTransactionPricingRequest() method");
		Map<String, Object> apiResponse = new HashMap<String, Object>();
		UpdateTransactionPricingResponse response = null;
		try {
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
					logger.info("Design / Prices are not matching : updateTransactionPricingRequest myPrice call is "
							+ "not invoked for the transaction id : {}", deal.getTransactionId());
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
	protected UpdateTransactionPricingRequest getRequest(Map<String, Object> designMap,Long nxSolutionId,NxMpDeal deal) {
		List<UpdateTransactionLineItem> items = new ArrayList<>();
		List<NxDesignDetails> designDetails = nxDesignDetailsRepository.findDesignDetailsaByNxSolutionId(nxSolutionId);
		if(CollectionUtils.isNotEmpty(designDetails)) {
		for(NxDesignDetails designDetail : designDetails ) {
			logger.info("UTP-Request myPriceTxnId : {}",deal.getTransactionId());
			String designData=designDetail.getDesignData();
			if (StringUtils.isNotEmpty(designData)) {
				List<Site> sites=this.getSites(designData);
				 Optional.ofNullable(sites).map(List::stream).orElse(Stream.empty()).
				    forEach( site -> {
						String siteId=site.getSiteId()!=null?site.getSiteId().toString():null;
						 Optional.ofNullable(site.getDesign()).map(List::stream).orElse(Stream.empty()).
						    forEach( design -> {
								items.addAll(processFMOPriceDetails(designMap,design,deal,designDetail,siteId));
						  });
						
				    });
			}
		}}
		
		
		
		if (CollectionUtils.isNotEmpty(items)) {
			UpdateTransactionLinePricing transactionLine = new UpdateTransactionLinePricing();
			transactionLine.setItems(items);
			TransactionPricingReqDocuments documents = new TransactionPricingReqDocuments();
			documents.setTransactionLine(transactionLine);
			UpdateTransactionPricingRequest request = new UpdateTransactionPricingRequest();
			request.setDocuments(documents);
			return request;
		}
		return null;

	}
	
	protected List<Site> getSites(String inputJson){
		List<Site> siteList=new ArrayList<Site>();
		JsonNode input = null;
		try {
			input = mapper.readTree(inputJson);
			JsonNode sites = input.path("site");
			for (JsonNode siteElement : sites) {
				Site site = JacksonUtil.fromStringForCodeHaus(siteElement.toString(), Site.class);
				siteList.add(site);
			}
		} catch (IOException e) {
			logger.info("Exception in submitFMOToMyPrice while converting to json {}");
		}
		return siteList;
	}
	
	protected List<UpdateTransactionLineItem> processFMOPriceDetails(Map<String, Object> designMap,Design design,
			NxMpDeal deal,NxDesignDetails designDetail,String siteId) {
		List<UpdateTransactionLineItem> items = new ArrayList<UpdateTransactionLineItem>();
		logger.info("FMO processFMOPriceDetails() entered");
		List<PriceAttributes> priceAttributes = configAndUpdatePricingUtil.getPriceAttributes(design,"$..priceDetails.*");
		if (CollectionUtils.isNotEmpty(priceAttributes)) {
			String portId=design.getPortId()!=null?design.getPortId().toString():null;
			String productName=designDetail.getProductName();
			Long nxTxnId=deal.getNxTxnId();
			Long nxDesignId= designDetail.getNxDesign().getNxDesignId();
			//String portId = designMap.containsKey(FmoConstants.PORT_ID) ? designMap.get(FmoConstants.PORT_ID).toString() : null;
			logger.info("FMO priceAttributes size : {}",priceAttributes.size()," portId : {}",portId);
			List<NxMpDesignDocument> designDocuments=null;
			if(MyPriceConstants.BVoIP.equalsIgnoreCase(productName)) {
				Set<String> productRateIds=restCommonUtil.geDataInSetString(design, "$..priceDetails.*.productRateId");
				designDocuments = nxMpDesignDocumentRepository.findByNxTxnIdAndNxDesignIdAndUsocIds(nxTxnId, nxDesignId, productRateIds);
			}else {
				designDocuments = nxMpDesignDocumentRepository.findByNxTxnIdAndNxDesignIdAndMpPartNumber(nxTxnId, nxDesignId, portId);
			}
			
			if(CollectionUtils.isNotEmpty(designDocuments)) {
				logger.info("FMO designDocuments size : {}",designDocuments.size());
				for(NxMpDesignDocument designDocument : designDocuments) {
					if(StringUtils.isNotEmpty(designDocument.getUsocId())) {
						if(MyPriceConstants.BVoIP.equalsIgnoreCase(productName)) {
							for(PriceAttributes priceAttribute : priceAttributes) {
								if(null != priceAttribute.getProductRateId() && designDocument.getUsocId().equalsIgnoreCase(priceAttribute.getProductRateId().toString())) {
									GetTransactionLineItemPrice getTransactionLineItemPrice = getTransactionLineItemPrice(priceAttribute);
									getUpdateTransactionLineItem(items, getTransactionLineItemPrice, priceAttribute, designDocument, nxTxnId, designMap,siteId);
								}
							}
						}else {
							//We need get respective priceAttributes wrt referencePortId then create record in price details and send to MyPrice
							for(PriceAttributes priceAttribute : priceAttributes) {
								if(null != priceAttribute.getReferencePortId() && portId.equalsIgnoreCase(priceAttribute.getReferencePortId().toString())
										&& (((MyPriceConstants.ADI.equalsIgnoreCase(productName)) &&
												null != priceAttribute.getProductRateId() && designDocument.getUsocId().equalsIgnoreCase(priceAttribute.getProductRateId().toString()))
										|| designDocument.getUsocId().equalsIgnoreCase(priceAttribute.getBeid()))) {
									logger.info("portId & referencePortId and usocId & beid : Equal : items size : "+items.size());
									GetTransactionLineItemPrice getTransactionLineItemPrice = getTransactionLineItemPrice(priceAttribute);
									getUpdateTransactionLineItem(items, getTransactionLineItemPrice, priceAttribute, designDocument, nxTxnId, designMap,siteId);
									
									//Also if priceAttributes contains nrcBeid then need to get respective priceAttributes block by matching beid 
									//then create new record in price details and send to MyPrice
									if(StringUtils.isNotEmpty(priceAttribute.getNrcBeid())) {
										for(PriceAttributes nrcPriceAttribute : priceAttributes) {
											if("NRC".equalsIgnoreCase(nrcPriceAttribute.getFrequency()) 
													&& (((MyPriceConstants.ADI.equalsIgnoreCase(productName)) && 
															null != nrcPriceAttribute.getProductRateId() && priceAttribute.getNrcBeid().equalsIgnoreCase(nrcPriceAttribute.getProductRateId().toString()))
													|| priceAttribute.getNrcBeid().equalsIgnoreCase(nrcPriceAttribute.getBeid()))) {
												GetTransactionLineItemPrice getNrcTransactionLineItemPrice = getTransactionLineItemPrice(nrcPriceAttribute);
												getUpdateTransactionLineItem(items, getNrcTransactionLineItemPrice, nrcPriceAttribute, designDocument, nxTxnId, designMap,siteId);
											}
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
								
								if("Access".equalsIgnoreCase(priceAttribute.getRdsPriceType())  && MyPriceConstants.ADI.equalsIgnoreCase(productName)) {
									GetTransactionLineItemPrice getTransactionLineItemPrice = getTransactionLineItemPrice(priceAttribute);
									getUpdateTransactionLineItem(items, getTransactionLineItemPrice, priceAttribute, designDocument, nxTxnId, designMap,siteId);
								}else {
									savePriceDetials(nxTxnId, designDocument, priceAttribute, designMap,siteId);
								}
							}
						}
					}
				}
			}
		}
		return items;
	}

	
	protected GetTransactionLineItemPrice getTransactionLineItemPrice(PriceAttributes priceAttribute) {
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
	
	protected void getUpdateTransactionLineItem(List<UpdateTransactionLineItem> items, GetTransactionLineItemPrice getTransactionLineItemPrice, 
			PriceAttributes priceAttribute, NxMpDesignDocument designDocument,Long nxTxnId, Map<String, Object> designMap,String siteId) {
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
			savePriceDetials(nxTxnId, designDocument, priceAttribute, designMap,siteId);
		}
	}
	
	protected void savePriceDetials(Long nxTxnId, NxMpDesignDocument nxMpDesignDocument, PriceAttributes priceAttribute,
			Map<String, Object> paramMap,String siteId) {
		logger.info("FMO savePriceDetials() entered");
		NxMpPriceDetails nxMpPriceDetails = new NxMpPriceDetails();
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
		nxMpPriceDetails.setAsrItemId(siteId);
		nxMpPriceDetails.setNxSiteCountry(priceAttribute.getCountry());
		nxMpPriceDetailsRepository.save(nxMpPriceDetails);
	
	}


	protected Map<String, Object> callUpdateTransactionPricingRequestApi(String myPriceTxnId,
			UpdateTransactionPricingRequest request) throws SalesBusinessException {
		Map<String, Object> response = new HashMap<String, Object>();
		String uri = env.getProperty("myprice.updateTransactionPricingRequest");
		uri = uri.replace("{transactionId}", myPriceTxnId);
		String requestString = JacksonUtil.toString(request);
		logger.info("FMO Json of UT Pricing request :========>>>>>{}", myPriceTxnId + "===>>" +requestString);
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
			response.put(MyPriceConstants.RESPONSE_CODE, 200);
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
