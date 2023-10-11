package com.att.sales.nexxus.myprice.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.text.DecimalFormat;
import java.math.*;


import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.FmoConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.constant.TDDConstants;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxLineItemLookUpDataModel;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxMpPriceDetails;
import com.att.sales.nexxus.dao.model.NxMpSiteDictionary;
import com.att.sales.nexxus.dao.model.NxMpSolutionDetails;
import com.att.sales.nexxus.dao.model.NxRatePlanDetails;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxLineItemLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxMpPriceDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxMpSiteDictionaryRepository;
import com.att.sales.nexxus.dao.repository.NxMpSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxRatePlanDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.myprice.transaction.model.GetTransactionLineItem;
import com.att.sales.nexxus.myprice.transaction.model.GetTransactionLineItemPrice;
import com.att.sales.nexxus.myprice.transaction.model.GetTransactionLineResponse;
import com.att.sales.nexxus.myprice.transaction.model.GetTransactionLineItemPriceNew;
import com.att.sales.nexxus.rateletter.model.RateLetterStatusRequest;
import com.att.sales.nexxus.rateletter.service.RateLetterStatus;
import com.att.sales.nexxus.rateletter.service.RateLetterStatusImpl;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.RestClientUtil;

import edu.emory.mathcs.backport.java.util.Arrays;

@Service("getTransactionLineServiceImpl")
public class GetTransactionLineServiceImpl extends BaseServiceImpl {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(GetTransactionLineServiceImpl.class);

	/** The env. */
	@Autowired
	private Environment env;
	
	@Autowired
	private HttpRestClient httpRestClient;

	@Autowired
	private RestClientUtil restClient;

	@Autowired
	private NxMpPriceDetailsRepository nxMpPriceDetailsRepository;

	@Autowired
	private NxMpDealRepository nxMpDealRepository;

	@Autowired
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;

	@Autowired
	private NxMpSolutionDetailsRepository nxMpSolutionDetailsRepository;
	
	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;

	@Autowired
	private NxLineItemLookupDataRepository nxLineItemLookupDataRepository;

	@Autowired
	private NxRatePlanDetailsRepository nxRatePlanDetailsRepository;
	
	@Autowired
	private RateLetterStatus rateLetterStatusService;
	
	@Autowired
	private NxMpSiteDictionaryRepository nxMpSiteDicRepo;
	
	@Autowired
	private NxDesignAuditRepository nxDesignAuditRepository;
	
	@Value("${myprice.proxy.enabled}")
	private String isProxyEnabled;
	
	private static HashMap<String, String> jurisdictionMap = new HashMap<>();
	
	public ServiceResponse getTransactionLine(String transactionId, Map<String, Boolean> isErateHoldingMap) throws SalesBusinessException {
		logger.info("Entering getTransactionLine() method");
		boolean isErate = isErateHoldingMap.getOrDefault(RateLetterStatusImpl.IS_ERATE, false);
		GetTransactionLineResponse response = null;
		try {
			//myPriceTransactionUtil.setSystemProperties();
			String uri = env.getProperty("myprice.getTransactionLine");
			uri = uri.replace("{transactionId}", transactionId);
			String fields = env.getProperty("myprice.transactionLine.fields");
			List<NxMpDeal> nxmpdeals = nxMpDealRepository.getByTransactionIdAndNxTxnOrder(transactionId);
			NxMpDeal nxmpdeal = null;
			if(CollectionUtils.isNotEmpty(nxmpdeals)) {
				nxmpdeal = nxmpdeals.get(0);
			}
			NxSolutionDetail nxSolutionDetail = null;
			if (Optional.ofNullable(nxmpdeal).isPresent()
					&& Optional.ofNullable(nxmpdeal.getSolutionId()).isPresent()) {
				nxSolutionDetail = nxSolutionDetailsRepository.findByNxSolutionId(nxmpdeal.getSolutionId());
			}
			List<String> lineBomIdList = nxLookupDataRepository.findByDatasetName("LINE_BOM_ID_LIST").stream().map(NxLookupData::getDescription).collect(Collectors.toList());
			logger.info("..... lineBomIdList ......"+lineBomIdList);
			
			boolean hasMore = false;
			Long offset = 0L;
			List<CompletableFuture<Void>> completableFutures = new ArrayList<>();
			do {
				String url = uri+fields+offset;
				Map<String, String> headers = new HashMap<String, String>();
				headers.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.MYPRICE_AUTHORIZATION));
				headers.put(StringConstants.REQUEST_CONTENT_TYPE, "application/json");
				String proxy = null;
				if(StringConstants.CONSTANT_Y.equalsIgnoreCase(isProxyEnabled)) {
					proxy = env.getProperty(CommonConstants.CDP_HTTP_PROXY);
				}
				String transResponse = httpRestClient.callHttpRestClient(url, HttpMethod.GET, null, null, 
						headers, proxy);
				
				if (null != transResponse && !transResponse.isEmpty()) {
					response = (GetTransactionLineResponse) restClient.processResult(transResponse,
							GetTransactionLineResponse.class);
					hasMore = response.isHasMore();
					StringBuffer printLog = new StringBuffer("GetTransactionLine :: transactionId: " + transactionId +" totalResults: "+response.getTotalResults()+" offset: "+response.getOffset()+" hasMore: "+hasMore);
					logger.info("printLog {}", org.apache.commons.lang3.StringUtils.normalizeSpace(printLog.toString()));
					offset = offset + response.getLimit();
					GetTransactionLineResponse tempResponse = response;
					NxMpDeal deal = nxmpdeal;
					NxSolutionDetail nxSolution = nxSolutionDetail;
					CompletableFuture<Void> task=CompletableFuture.runAsync(() -> {
						if(Optional.ofNullable(deal).isPresent() && Optional.ofNullable(nxSolution).isPresent() && CollectionUtils.isNotEmpty(lineBomIdList)) {
							updatePriceDetails(tempResponse, transactionId, deal, nxSolution,lineBomIdList, isErate);
						}
					});
					completableFutures.add(task);
					if(!hasMore) {
						setSuccessResponse(response);
					}
				}
			}while(hasMore);
			
			//call dmap after all threads complete
			CompletableFuture<Void> allFutures = CompletableFuture
					.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()]));
			allFutures.thenAccept(future -> {
				isApprovedThenNotify(transactionId);
			}).exceptionally(exception -> {
				logger.info("GetTransactionLine thread completed exceptionally........");
				return null;
			});
		} catch (SalesBusinessException e) {
			logger.error("exception occured in Myprice getTransactionLine call : "+ e);
		}
		logger.info("Existing getTransactionLine() method");
		return response;
	}

	private String getOfferId(String offerId) {
		if(MyPriceConstants.OFFER_NAME_MAP.containsKey(offerId)) {
			return MyPriceConstants.OFFER_NAME_MAP.get(offerId);
		}
		return offerId;
	}
	public void updatePriceDetails(GetTransactionLineResponse response, String transactionId,NxMpDeal nxmpdeal,NxSolutionDetail nxSolutionDetail, List<String> lineBomIdList, boolean isErate) {
		logger.info("GetTransactionLine Response :==>> {}", JacksonUtil.toString(response));
		logger.info("myprice-transactionId : {}", org.apache.commons.lang3.StringUtils.normalizeSpace(nxmpdeal.getTransactionId()));
		if (Optional.ofNullable(response).isPresent()) {
			if (CollectionUtils.isNotEmpty(response.getItems())) {
				Map<String, Long> nxTxnIdMap = new HashMap<>();
				Map<Long, NxMpDeal> txnIdOfferMap = new HashMap<>();
				txnIdOfferMap.put(nxmpdeal.getNxTxnId(), nxmpdeal);
				if (StringConstants.FLOW_TYPE_IPnE.equalsIgnoreCase(nxSolutionDetail.getFlowType()) || StringConstants.FLOW_TYPE_MP_FIRM.equalsIgnoreCase(nxSolutionDetail.getFlowType()) 
						|| StringConstants.FLOW_TYPE_INR.equalsIgnoreCase(nxSolutionDetail.getFlowType()) || StringConstants.FLOW_TYPE_IGLOO_QUOTE.equalsIgnoreCase(nxSolutionDetail.getFlowType())
						|| StringConstants.FLOW_TYPE_FMO.equalsIgnoreCase(nxSolutionDetail.getFlowType())) { 
					Map<String, Long> offerTxnIdMap = new HashMap<>();
					offerTxnIdMap.put(getOfferId(nxmpdeal.getOfferId()), nxmpdeal.getNxTxnId());
					txnIdOfferMap.put(nxmpdeal.getNxTxnId(), nxmpdeal);
					
					/*
					 * In case of ASENoD, we will get "productType_l": "ASEoD"
					 * In case of ASE, we will get "productType_l": "ASE Classic",
					 * In case of ADE, we will get "productType_l": "ADE",
					 */
					boolean asenod = false, ase = false, ade = false;
					for (GetTransactionLineItem item : response.getItems()) {
						if (null == item.getModelVariableName() || item.getModelVariableName().isEmpty()) {
							if(MyPriceConstants.ASEoD_OFFER_NAME.equalsIgnoreCase(item.getProductType())) {
								asenod = true;
							} else if(MyPriceConstants.ASE_CLASSIC_OFFER_NAME.equalsIgnoreCase(item.getProductType())) {
								ase = true;
							} else if(MyPriceConstants.ADE_OFFER_NAME.equalsIgnoreCase(item.getProductType())) {
								ade = true;
							}
						}
					}
					
					if(asenod && !ase && !ade) {
						offerTxnIdMap.remove(MyPriceConstants.ASE_OFFER_NAME);
						nxmpdeal.setOfferId(MyPriceConstants.ASENOD_OFFER_NAME);
						nxMpDealRepository.save(nxmpdeal);
						offerTxnIdMap.put(nxmpdeal.getOfferId(), nxmpdeal.getNxTxnId());
						txnIdOfferMap.put(nxmpdeal.getNxTxnId(), nxmpdeal);
					} else if(asenod && (ase || ade)) {
						NxMpDeal newDeal =  createNxMpDeal(nxmpdeal, MyPriceConstants.ASENOD_OFFER_NAME);
						offerTxnIdMap.put(newDeal.getOfferId(), newDeal.getNxTxnId());
						txnIdOfferMap.put(newDeal.getNxTxnId(), newDeal);
					}
					
					Map<String, String> fmoLocalAccessdocuments = new HashMap<String, String>();
					Map<String, Long> parentTransportTxn = new HashMap<String, Long>();
					Map<String, Long> childTransportTxn = new HashMap<String, Long>();
					Map<String, List<String>> fmoTransportKeys = new HashMap<String, List<String>>();
					for (GetTransactionLineItem item : response.getItems()) { 
						//if(null != item.getBomId() && (item.getBomId().equalsIgnoreCase("BOM_ASE") || item.getBomId().equalsIgnoreCase("BOM_ADE"))) { // check _line_bom_id as BOM_ASE or BOM_ADE
						if(null != item.getBomId() && lineBomIdList.contains(item.getBomId())) {
							if("BOM_ASE".equalsIgnoreCase(item.getBomId())) {
								if(checkASEoDProductType(response, item.getDocumentNumber())) {
									item.setBomPartNumber(MyPriceConstants.ASENOD_OFFER_NAME);
								}
							}
							if(null != offerTxnIdMap && null != item.getBomPartNumber() && offerTxnIdMap.containsKey(item.getBomPartNumber())) { // if offerTxnIdMap contains _line_bom_part_number then put to nxTxnIdMap with key as _document_number and value as respective value from offerTxnIdMap 							
								nxTxnIdMap.put(item.getDocumentNumber(), offerTxnIdMap.get(item.getBomPartNumber()));
								parentTransportTxn.put(item.getBomId() + "_" + item.getParentDocNumber(), offerTxnIdMap.get(item.getBomPartNumber()));
								childTransportTxn.put(item.getBomId() + "_" + item.getDocumentNumber(), offerTxnIdMap.get(item.getBomPartNumber()));
							} else {
								if(StringConstants.FLOW_TYPE_FMO.equalsIgnoreCase(nxSolutionDetail.getFlowType()) && MyPriceConstants.LocalAccess.equalsIgnoreCase(item.getBomPartNumber())) {
									//if _line_bom_part_number is LocalAccess then for FMO flow we will have reference to corresponding transport - AVPN or ADI
									fmoLocalAccessdocuments.put(item.getDocumentNumber(), item.getParentDocNumber());
								} else {
									// create new deal using nxmpdeal for new _line_bom_part_number as offer and put it in offerTxnIdMap. Also insert in nxTxnIdMap with key as _document_number and value as new nxTxnId 								
									NxMpDeal newDeal =  createNxMpDeal(nxmpdeal, item.getBomPartNumber());
									offerTxnIdMap.put(newDeal.getOfferId(), newDeal.getNxTxnId());
									nxTxnIdMap.put(item.getDocumentNumber(), newDeal.getNxTxnId());
									txnIdOfferMap.put(newDeal.getNxTxnId(), newDeal);
									parentTransportTxn.put(item.getBomId() + "_" + item.getParentDocNumber(), newDeal.getNxTxnId());
									childTransportTxn.put(item.getBomId() + "_" + item.getDocumentNumber(), newDeal.getNxTxnId());
								}
							} 
						} else {
							if(StringConstants.FLOW_TYPE_FMO.equalsIgnoreCase(nxSolutionDetail.getFlowType()) && (StringUtils.isEmpty(item.getModelVariableName()))) {
								if(null!=item.getNxSiteId()) {
									if(!fmoTransportKeys.containsKey(item.getNxSiteId())) {
										fmoTransportKeys.put(item.getNxSiteId(), new ArrayList<String>());
									}
									fmoTransportKeys.get(item.getNxSiteId()).add(item.getParentDocNumber());
								}
								
							}
						} 
					}
					
					if(!fmoLocalAccessdocuments.isEmpty() && !parentTransportTxn.isEmpty()) {
						fmoLocalAccessdocuments.forEach((productDocument, parentDocument) -> {
							if(parentTransportTxn.containsKey("BOM_AVPN_" + parentDocument)) {
								nxTxnIdMap.put(productDocument, parentTransportTxn.get("BOM_AVPN_" + parentDocument));
							}
							if(parentTransportTxn.containsKey("BOM_ADI_" + parentDocument)) {
								nxTxnIdMap.put(productDocument, parentTransportTxn.get("BOM_ADI_" + parentDocument));
							}
							
							if(!nxTxnIdMap.containsKey(productDocument)) {
								for(String key : fmoTransportKeys.keySet()) {
									if(fmoTransportKeys.get(key).contains(productDocument)) {
										for(String value : fmoTransportKeys.get(key)) {
											if(childTransportTxn.containsKey("BOM_AVPN_" + value)) {
												nxTxnIdMap.put(productDocument, childTransportTxn.get("BOM_AVPN_" + value));
												break;
											}
											if(childTransportTxn.containsKey("BOM_ADI_" + value)) {
												nxTxnIdMap.put(productDocument, childTransportTxn.get("BOM_ADI_" + value));
												break;
											}
										}
									}
								}
							}
						});
					}
				}
				
				if (Optional.ofNullable(nxmpdeal).isPresent() && Optional.ofNullable(nxSolutionDetail).isPresent()) {
					//updateLocationBlock(response.getItems(),nxmpdeal.getNxTxnId(),nxSolutionDetail.getFlowType(),nxSolutionDetail.getNxSolutionId());
					logger.info("nxTxnId is {}", nxmpdeal.getNxTxnId());
					logger.info("Flow Type in nxSolutionDetail > {}", nxSolutionDetail.getFlowType());
					Map<String, Object> methodMap = new HashMap<String, Object>();
					List<NxMpPriceDetails> mpPriceDetailsList = new ArrayList<>();
					for (GetTransactionLineItem item : response.getItems()) {
						List<NxMpPriceDetails> priceDetailsList = null;
						logger.info("ModelVariableName : {}", item.getModelVariableName());
						Long newNxTxnId = nxmpdeal.getNxTxnId();
						if (null == item.getModelVariableName() || item.getModelVariableName().isEmpty()) {
							// Sales Initiated Flow
							if (StringConstants.FLOW_TYPE_AUTO.equalsIgnoreCase(nxSolutionDetail.getFlowType()) || StringConstants.FLOW_TYPE_FMO.equalsIgnoreCase(nxSolutionDetail.getFlowType())) {
								logger.info("Sales Initiated Flow : document number > {}", item.getDocumentNumber());
								priceDetailsList = nxMpPriceDetailsRepository.findAllMpDocumentNumberAndNxTxnId(
										Long.parseLong(item.getDocumentNumber()), nxmpdeal.getNxTxnId());
								if (StringConstants.FLOW_TYPE_FMO.equalsIgnoreCase(nxSolutionDetail.getFlowType())
										&& null != nxTxnIdMap && !nxTxnIdMap.isEmpty() && nxTxnIdMap.containsKey(item.getParentDocNumber())) {
									newNxTxnId = nxTxnIdMap.get(item.getParentDocNumber());
									if(CollectionUtils.isEmpty(priceDetailsList)) {
										priceDetailsList = nxMpPriceDetailsRepository.findAllMpDocumentNumberAndNxTxnId(
											Long.parseLong(item.getDocumentNumber()), newNxTxnId);
									}
								}
							}
							// My-Price Initiated Flow
							else if (StringConstants.FLOW_TYPE_IPnE.equalsIgnoreCase(nxSolutionDetail.getFlowType()) || StringConstants.FLOW_TYPE_MP_FIRM.equalsIgnoreCase(nxSolutionDetail.getFlowType()) || StringConstants.FLOW_TYPE_INR.equalsIgnoreCase(nxSolutionDetail.getFlowType()) || StringConstants.FLOW_TYPE_IGLOO_QUOTE.equalsIgnoreCase(nxSolutionDetail.getFlowType())) {
								HashMap<String, String> nxSiteIdMap = updateLocationBlock(response.getItems(),nxmpdeal.getNxTxnId(),nxSolutionDetail.getFlowType(),nxSolutionDetail.getNxSolutionId());
								if(null != nxTxnIdMap && !nxTxnIdMap.isEmpty() && nxTxnIdMap.containsKey(item.getParentDocNumber())) {
									newNxTxnId = nxTxnIdMap.get(item.getParentDocNumber());
								} else {
									newNxTxnId = nxmpdeal.getNxTxnId();
								}
								
								methodMap.put("currentDeal", txnIdOfferMap.get(newNxTxnId));
								priceDetailsList = new ArrayList<>();
								logger.info("My-Price Initiated Flow : nxSiteId as nxDesignId :=======>>>>> {}",
										item.getNxSiteId());
								boolean isMrc = checkMrcOrNrc(item.getApprovedEffectivePriceMRC(),
										item.getApprovedNetEffectivePriceMRC(), item.getApprovedDiscountPctgMRC());
								if (isMrc) {
									NxMpPriceDetails priceDetails = this.createPriceDetails(newNxTxnId, "MRC", item, methodMap,nxSiteIdMap);									
									priceDetailsList.add(priceDetails);									
								}
								boolean isNrc = checkMrcOrNrc(item.getApprovedEffectivePriceNRC(),
										item.getApprovedNetEffectivePriceNRC(), item.getApprovedDiscountPctgNRC());
								if (isNrc) {
									NxMpPriceDetails priceDetails = this.createPriceDetails(newNxTxnId, "NRC", item, methodMap,nxSiteIdMap);
									priceDetailsList.add(priceDetails);
								}
							}
						}
						if (CollectionUtils.isNotEmpty(priceDetailsList)) {
							String flowType = nxSolutionDetail.getFlowType();
							logger.info("......FLOW TYPE ......... >> {}",flowType);
							if (null != item.getSpecialConstructionAppNRC()) {
								priceDetailsList.forEach(
										s -> s.setSpecialConstructionAppNRC(null != item.getSpecialConstructionAppNRC().getValue() ? item.getSpecialConstructionAppNRC().getValue().toString() : ""));
							}
							logger.info("GetTransactionLine Response Item :==>> {}", JacksonUtil.toString(item));
							NxMpDeal currentDeal = txnIdOfferMap.containsKey(newNxTxnId) ? txnIdOfferMap.get(newNxTxnId) : nxmpdeal;
							List<NxMpPriceDetails> nxMpPriceDetailsList = new ArrayList<>();
							priceDetailsList.stream().forEach(priceDetails -> {
								if (item.getDocumentNumber()
										.equals(String.valueOf(priceDetails.getMpDocumentNumber()))) {
									priceDetails = setPriceDetails(priceDetails, item, currentDeal, flowType, isErate);
									if(null != priceDetails) {
										nxMpPriceDetailsList.add(priceDetails);
									}
								}
							});
							if(CollectionUtils.isNotEmpty(nxMpPriceDetailsList)) {
								mpPriceDetailsList.addAll(nxMpPriceDetailsList);
							}
						}
					}
					if(CollectionUtils.isNotEmpty(mpPriceDetailsList)) {
						nxMpPriceDetailsRepository.saveAll(mpPriceDetailsList);
					}
				}
				//updateLocationBlock(response.getItems(),nxmpdeal.getNxTxnId(),nxSolutionDetail.getFlowType(),nxSolutionDetail.getNxSolutionId());
			}
		}
	}

	private boolean checkASEoDProductType(GetTransactionLineResponse response, String parentDocNumber) {
		boolean aseodFlag = false;
		for (GetTransactionLineItem item : response.getItems()) {
			if ((null == item.getModelVariableName() || item.getModelVariableName().isEmpty()) && null != parentDocNumber && parentDocNumber.equalsIgnoreCase(item.getParentDocNumber())) {
				if(MyPriceConstants.ASEoD_OFFER_NAME.equalsIgnoreCase(item.getProductType())) {
					aseodFlag = true;
					break;
				}
			}
		}
		return aseodFlag;
	}
	
	private NxMpDeal createNxMpDeal(NxMpDeal nxmpdeal, String offerId) {
		if (null == nxmpdeal.getOfferId() || nxmpdeal.getOfferId().isEmpty()) {
			nxmpdeal.setOfferId(offerId);
			nxMpDealRepository.save(nxmpdeal);
			return nxmpdeal;
		} else {
			NxMpDeal newDeal = new NxMpDeal();
			try {
				BeanUtils.copyProperties(newDeal, nxmpdeal);
				newDeal.setOfferId(offerId);
				newDeal.setNxTxnId(null);
				newDeal = nxMpDealRepository.save(newDeal);
			} catch (IllegalAccessException e) {
				logger.info("Caught IllegalAccessException {}", e.getMessage());
			} catch (InvocationTargetException e) {
				logger.info("Caught InvocationTargetException {}", e.getMessage());
			}
			return newDeal;
		}
	}

	
	public double limitPrecisionWithDecimalFormat(Double dblAsString, int maxDigitsAfterDecimal) {
		Double roundValue =null;
		if(maxDigitsAfterDecimal>0) {
			BigDecimal bd=new BigDecimal(Double.toString(dblAsString));
			bd=bd.setScale(maxDigitsAfterDecimal, RoundingMode.HALF_UP);
			roundValue=bd.doubleValue();
		}else {
			return dblAsString;
		}
        
        return roundValue;
    }
	
	public NxMpPriceDetails setPriceDetails(NxMpPriceDetails priceDetails, GetTransactionLineItem item, NxMpDeal nxMpDeal, String flowType, boolean isErate) {
		try {
			GetTransactionLineItemPriceNew approvedEffectivePriceNRC = item.getApprovedEffectivePriceNRC();
			GetTransactionLineItemPriceNew requestedEffectivePriceNRC = item.getRequestedEffectivePriceNRC();
			GetTransactionLineItemPriceNew approvedEffectivePriceMRC = item.getApprovedEffectivePriceMRC();
			GetTransactionLineItemPriceNew requestedEffectivePriceMRC = item.getRequestedEffectivePriceMRC();
			
			int priceP = item.getPricePrecision();
			
			Double approvedEffPriceMRC = limitPrecisionWithDecimalFormat(approvedEffectivePriceMRC.getValue(),priceP);
			Double requestedEffPriceMRC = limitPrecisionWithDecimalFormat(requestedEffectivePriceMRC.getValue(),priceP);
			Double approvedEffPriceNRC = limitPrecisionWithDecimalFormat(approvedEffectivePriceNRC.getValue(),priceP);
			Double requestedEffPriceNRC = limitPrecisionWithDecimalFormat(requestedEffectivePriceNRC.getValue(),priceP);
			 
			 
			/*  Double gy = null;
	          Double gyc = null;
				 
				 if(priceP == 2) {
		             DecimalFormat df_obj = new DecimalFormat("#.##");        
		             gy = Double.valueOf(df_obj.format(approvedEffectivePriceMRC.getValue()));
		             gyc = Double.valueOf(df_obj.format(requestedEffectivePriceMRC.getValue()));
		             
		        }else if(priceP == 3) {
		             DecimalFormat df_obj = new DecimalFormat("#.###");
		             gy =  Double.valueOf(df_obj.format(approvedEffectivePriceMRC.getValue()));
		        }else if (priceP == 5) {
		             DecimalFormat df_obj = new DecimalFormat("#.#####");
		             gy =  Double.valueOf(df_obj.format(approvedEffectivePriceMRC.getValue()));
		             
		       }else if (priceP == 8) {
		             DecimalFormat df_obj = new DecimalFormat("#.########");
		             gy =  Double.valueOf(df_obj.format(approvedEffectivePriceMRC.getValue()));
		       }else if(priceP == 9) {
		             DecimalFormat df_obj = new DecimalFormat("#.#########");
		             gy =  Double.valueOf(df_obj.format(approvedEffectivePriceMRC.getValue()));
		       }*/
			 
			 
			 
			
			if ("MRC".equals(priceDetails.getFrequency())) {
				if (null != item.getApprovedDiscountPctgMRC() && !item.getApprovedDiscountPctgMRC().isEmpty()) {
					priceDetails.setApprovedMRCDisc(Float.parseFloat(item.getApprovedDiscountPctgMRC()));
				}
				if (Optional.ofNullable(approvedEffectivePriceMRC).isPresent()) {
					priceDetails.setApprovedMRCEffectivePrice(approvedEffPriceMRC);
					priceDetails.setCurrency(approvedEffectivePriceMRC.getCurrency());
				}
				if("Approved Online".equalsIgnoreCase(nxMpDeal.getAutoApproval())) {
					if (Optional.ofNullable(requestedEffectivePriceMRC).isPresent()) {
						priceDetails.setApprovedMRCNetEffectivePrice(requestedEffPriceMRC);
						priceDetails.setCurrency(requestedEffectivePriceMRC.getCurrency());
					}
				} else {
					if (Optional.ofNullable(approvedEffectivePriceMRC).isPresent()) {
						priceDetails.setApprovedMRCNetEffectivePrice(approvedEffPriceMRC);
						priceDetails.setCurrency(approvedEffectivePriceMRC.getCurrency());
					}
				}
	
			} else {
				if (null != item.getApprovedDiscountPctgNRC() && !item.getApprovedDiscountPctgNRC().isEmpty()) {
					priceDetails.setApprovedNRCDisc(Float.parseFloat(item.getApprovedDiscountPctgNRC()));
				}
				if (Optional.ofNullable(approvedEffectivePriceNRC).isPresent()) {
					priceDetails.setApprovedNRCEffectivePrice(approvedEffPriceNRC);
					priceDetails.setCurrency(approvedEffectivePriceNRC.getCurrency());
				}
				if("Approved Online".equalsIgnoreCase(nxMpDeal.getAutoApproval())) {
					if (Optional.ofNullable(requestedEffectivePriceNRC).isPresent()) {
						priceDetails.setApprovedNRCNetEffectivePrice(requestedEffPriceNRC);
						priceDetails.setCurrency(requestedEffectivePriceNRC.getCurrency());
					}
				} else {
					if (Optional.ofNullable(approvedEffectivePriceNRC).isPresent()) {
						priceDetails.setApprovedNRCNetEffectivePrice(approvedEffPriceNRC);
						priceDetails.setCurrency(approvedEffectivePriceNRC.getCurrency());
					}
				}
			}
			
			if(StringConstants.FLOW_TYPE_AUTO.equalsIgnoreCase(flowType) && ("ADE".equalsIgnoreCase(nxMpDeal.getOfferId()) 
					|| "ASE".equalsIgnoreCase(nxMpDeal.getOfferId()) || "ASENoD".equalsIgnoreCase(nxMpDeal.getOfferId()))) { // Author: Seema
					priceDetails.setTerm(item.getTerm());
			}
	
			if(StringConstants.FLOW_TYPE_FMO.equalsIgnoreCase(flowType)) {
				priceDetails.setNxTxnId(nxMpDeal.getNxTxnId());
			}
			
			if (!(StringConstants.FLOW_TYPE_AUTO.equalsIgnoreCase(flowType))) {
				// AVPN and ADI product's ratePlanId tag set logic
				if (item.getSocVersion() != null && "AVPN".equalsIgnoreCase(nxMpDeal.getOfferId())) {
		
					List<NxRatePlanDetails> nxRatePlanDetailList = nxRatePlanDetailsRepository.findBySocDateAndProduct(item.getSocVersion(), nxMpDeal.getOfferId());
					if (nxRatePlanDetailList != null && nxRatePlanDetailList.size() > 0) {
						NxRatePlanDetails nxRatePlanDetails = nxRatePlanDetailList.get(0);
						if (Optional.ofNullable(nxRatePlanDetails.getRatePlanId()).isPresent()) {
							priceDetails.setRatePlanId(nxRatePlanDetails.getRatePlanId());
						}
					}
				}
				
				if ("ADI".equalsIgnoreCase(nxMpDeal.getOfferId()) || "MIS/PNT".equalsIgnoreCase(nxMpDeal.getOfferId())) {
					
					List<String> adiLittleProductList = Arrays.asList(new String[] {"5380","5381","5382","5598"});
					List<String> adiLittleProductOtherList = Arrays.asList(new String[] {"6005","6006","6007","6171"});
		
					List<NxLineItemLookUpDataModel> nxLookUpLineItemList = nxLineItemLookupDataRepository.findByUniqueIdAndFlowType(item.getUniqueIds(), MyPriceConstants.SOURCE_FMO);
					if (nxLookUpLineItemList != null && nxLookUpLineItemList.size() > 0) {
						NxLineItemLookUpDataModel nxLookUpLineItem = nxLookUpLineItemList.get(0);
						if (Optional.ofNullable(nxLookUpLineItem).isPresent()) {
							if (Optional.ofNullable(nxLookUpLineItem.getLittleProdId()).isPresent()
									&& adiLittleProductList.contains(nxLookUpLineItem.getLittleProdId().toString())) {
								priceDetails.setRatePlanId(Long.valueOf("1"));
							} else if(Optional.ofNullable(nxLookUpLineItem.getLittleProdId()).isPresent()
									&& adiLittleProductOtherList.contains(nxLookUpLineItem.getLittleProdId().toString())) {
								priceDetails.setRatePlanId(Long.valueOf("531"));
							}
						}
					}
				}
	
				// AVPN and ADI product's IS_ACCESS tag set logic
				if("AVPN".equalsIgnoreCase(nxMpDeal.getOfferId()) || "ADI".equalsIgnoreCase(nxMpDeal.getOfferId()) || "MIS/PNT".equalsIgnoreCase(nxMpDeal.getOfferId())) {
					priceDetails.setIsAccess(item.getProductVariation());
				}
				
				// set siteCountry and product rate id for BVoIP product
				if(MyPriceConstants.BVOIP_PRODUCT_MAP.containsKey(nxMpDeal.getOfferId())) {
					List<NxLineItemLookUpDataModel> nxLookUpLineItemList = nxLineItemLookupDataRepository.findByUniqueIdAndFlowType(item.getUniqueIds(), FmoConstants.MYPRICE_FMO);
					if (nxLookUpLineItemList != null && nxLookUpLineItemList.size() > 0) {
						NxLineItemLookUpDataModel nxLookUpLineItem = nxLookUpLineItemList.get(0);
						if (Optional.ofNullable(nxLookUpLineItem).isPresent()) {
							priceDetails.setNxSiteCountry(nxLookUpLineItem.getField6Value());
							//Skip BVoIP Usage line items
							if("Usage".equalsIgnoreCase(nxLookUpLineItem.getField18Value())) {
								return null;
							}
							/*
							 * if ("MRC".equals(priceDetails.getFrequency())) { String sourceId =
							 * nxLookUpLineItem.getField1Value(); if
							 * (Optional.ofNullable(sourceId).isPresent()) {
							 * priceDetails.setProdRateId(Long.parseLong(sourceId)); } }
							 */
							if (StringUtils.isNotBlank(nxLookUpLineItem.getField1Value())) {
								String sourceId = nxLookUpLineItem.getField1Value();
								List<Object> rateTypes = nxLineItemLookupDataRepository.findRateTypeByProdRateId(sourceId);
								for(Object rateType : rateTypes) {
									if(StringUtils.isNotBlank(rateType.toString()) && "NRC".equalsIgnoreCase(rateType.toString()) && rateType.toString().equalsIgnoreCase(priceDetails.getFrequency())) {
										priceDetails.setProdRateId(Long.parseLong(sourceId)); 
										return priceDetails;
									} else if(StringUtils.isNotBlank(rateType.toString()) && "RC".equalsIgnoreCase(rateType.toString()) && "MRC".equalsIgnoreCase(priceDetails.getFrequency())) {
										priceDetails.setProdRateId(Long.parseLong(sourceId)); 
										return priceDetails;
									} else {
										return null;
									}
								}
							} else {
								return null;
							}
						}
					} else {
						return null;
					}
				}
				
				// product rate id and beid logic from unique id
				if ("ADI".equalsIgnoreCase(nxMpDeal.getOfferId()) || "MIS/PNT".equalsIgnoreCase(nxMpDeal.getOfferId())) {
					List<NxLineItemLookUpDataModel> nxLookUpLineItemList = nxLineItemLookupDataRepository.findByUniqueIdAndFlowType(item.getUniqueIds(), MyPriceConstants.SOURCE_FMO);
					if (nxLookUpLineItemList != null && nxLookUpLineItemList.size() > 0) {
						NxLineItemLookUpDataModel nxLookUpLineItem = nxLookUpLineItemList.get(0);
						if (Optional.ofNullable(nxLookUpLineItem).isPresent()) {
							String sourceId = null;
							if ("MRC".equals(priceDetails.getFrequency())) {
								sourceId = nxLookUpLineItem.getField1Value();
							} else {
								sourceId = nxLookUpLineItem.getField2Value();
							}
							if (Optional.ofNullable(sourceId).isPresent()) {
								priceDetails.setUniqueId(nxLookUpLineItem.getField20Value());
								priceDetails.setProdRateId(Long.parseLong(sourceId));
							}
						}
					}
				} else if ("AVPN".equalsIgnoreCase(nxMpDeal.getOfferId())) {
					List<NxLineItemLookUpDataModel> nxLookUpLineItemList = nxLineItemLookupDataRepository.findByUniqueIdAndFlowType(item.getUniqueIds(), FmoConstants.MYPRICE_FMO);
					if (nxLookUpLineItemList != null && nxLookUpLineItemList.size() > 0) {
						NxLineItemLookUpDataModel nxLookUpLineItem = nxLookUpLineItemList.get(0);
						if (Optional.ofNullable(nxLookUpLineItem).isPresent()) {
							
							/*
							 * we will have below 3 scenario's in case of AVPn
							 * 1. MRC Unique id
							 * 2. NRC Unique id
							 * 3. MRC and NRC Unique id
							 */
							String beId = null;
							if ("MRC".equalsIgnoreCase(priceDetails.getFrequency())) {
								if(StringUtils.isNotBlank(nxLookUpLineItem.getField1Value())) {
									beId = nxLookUpLineItem.getField1Value();
								} else {
									return null;
								}
							} 
							
							if ("NRC".equalsIgnoreCase(priceDetails.getFrequency())) {
								if(StringUtils.isNotBlank(nxLookUpLineItem.getField2Value())) {
									beId = nxLookUpLineItem.getField2Value();
								} else {
									return null;
								}
							}
	
							priceDetails.setBeid(beId);
							String prodRateId = nxLineItemLookupDataRepository.findSourceIdByBid(beId);
							if (Optional.ofNullable(prodRateId).isPresent()) {
								priceDetails.setProdRateId(Long.parseLong(prodRateId));
							}
							
		
						} // if found lookup data
					}
				}
			}
			
			// RLA automation logic starts
			if ((StringConstants.FLOW_TYPE_MP_FIRM.equalsIgnoreCase(flowType)
					|| StringConstants.FLOW_TYPE_IPnE.equalsIgnoreCase(flowType)
					|| StringConstants.FLOW_TYPE_INR.equalsIgnoreCase(flowType)
					|| StringConstants.FLOW_TYPE_IGLOO_QUOTE.equalsIgnoreCase(flowType))
					&& ("ADI".equalsIgnoreCase(nxMpDeal.getOfferId()) || "AVPN".equalsIgnoreCase(nxMpDeal.getOfferId())
							|| "MIS/PNT".equalsIgnoreCase(nxMpDeal.getOfferId()))) {
				String socVersion = item.getSocVersion();
				NxRatePlanDetails nxRatePlanDetails = null;
				if (isErate) {
					nxRatePlanDetails = nxRatePlanDetailsRepository.findTopByProductAndErateIndicatorAndActiveYnOrderBySocDateDesc(nxMpDeal.getOfferId(), "Y", "Y");
				} else {
					if ("ADI".equalsIgnoreCase(nxMpDeal.getOfferId()) || "MIS/PNT".equalsIgnoreCase(nxMpDeal.getOfferId())) {
						if (item.getUniqueIds() != null && item.getUniqueIds().toLowerCase().contains("schedule 2")) {
							nxRatePlanDetails = nxRatePlanDetailsRepository.findTopBySocDateContainingAndProductAndActiveYnOrderBySocDateDesc("2006", "ADI", "Y");
						} else if (item.getUniqueIds() != null && item.getUniqueIds().toLowerCase().contains("schedule 3")) {
							nxRatePlanDetails = nxRatePlanDetailsRepository.findTopBySocDateContainingAndProductAndActiveYnOrderBySocDateDesc("2014", "ADI", "Y");
						}
					} else if ("AVPN".equalsIgnoreCase(nxMpDeal.getOfferId())) {
						nxRatePlanDetails = nxRatePlanDetailsRepository.findTopBySocDateAndProductAndActiveYn(socVersion, nxMpDeal.getOfferId(), "Y");
					}
				}
				if (nxRatePlanDetails != null) {
					priceDetails.setRatePlanId(nxRatePlanDetails.getRatePlanId());
					priceDetails.setRatePlanIdExternal(nxRatePlanDetails.getRatePlanIdExternal());
					priceDetails.setSocVersion(nxRatePlanDetails.getSocDate());
				}
			}
			// RLA automation logic ends
		
		}catch(Exception e) {
			e.printStackTrace();
		}
		return priceDetails;
	}
	
	/*public GetTransactionLineResponse callGetTransactionLineApi() throws SalesBusinessException {
		logger.info("Calling MyPrice for callGetTransactionLineApi .");
		GetTransactionLineResponse response = null;
		try {
			RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
			RestTemplate restTemplate = restTemplateBuilder
					.basicAuthorization(env.getProperty("myprice.username"), env.getProperty("myprice.password"))
					.build();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
			ResponseEntity<String> respString = restTemplate.exchange(
					new URI(env.getProperty("myprice.getTransactionLine")), HttpMethod.GET, entity, String.class);

			com.fasterxml.jackson.databind.ObjectMapper thisMapper = new com.fasterxml.jackson.databind.ObjectMapper();
			thisMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
			thisMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			if (null != respString.getBody()) {
				response = thisMapper.readValue(respString.getBody(), GetTransactionLineResponse.class);
			}

			logger.info("The response is {}", response);
		} catch (Exception e) {
			logger.error("Exception : While processing rest client call {}", e.getMessage());
			throw new SalesBusinessException(MessageConstants.ADDRESS_EXCEPTION);
		}

		return response;

	}*/

	public void isApprovedThenNotify(String myPriceTxnId) {
		List<NxMpDeal> nxMpDealList = nxMpDealRepository.findAllByTransactionId(myPriceTxnId);
		if (nxMpDealList != null && nxMpDealList.size() > 0) {
			NxMpDeal nxMpDeal = nxMpDealList.get(0);
			if (Optional.ofNullable(nxMpDeal).isPresent() && Optional.ofNullable(nxMpDeal.getDealStatus()).isPresent()) {
				if (nxMpDeal.getDealStatus().equalsIgnoreCase(CommonConstants.APPROVED_PRICING) || nxMpDeal.getDealStatus().equalsIgnoreCase(CommonConstants.APPROVED)) {
					CompletableFuture.runAsync(() -> {
						RateLetterStatusRequest request = new RateLetterStatusRequest();
						try {
							request.setDealId(nxMpDeal.getDealID());
							request.setDealVersion(nxMpDeal.getVersion());
							request.setDealRevisionNumber(nxMpDeal.getRevision());
							request.setDealStatus(nxMpDeal.getDealStatus());
							request.setCpqId(nxMpDeal.getTransactionId());
							request.setPriceScenarioId(nxMpDeal.getPriceScenarioId());
							request.setRlQuoteUrl(nxMpDeal.getQuoteUrl());
							request.setRlExpirationDate(nxMpDeal.getRateLetterExpiresOn());
							request.setQuoteType(nxMpDeal.getQuoteType());
							NxSolutionDetail nxSolutionDetail = nxSolutionDetailsRepository.findByNxSolutionId(nxMpDeal.getSolutionId());
							if (Optional.ofNullable(nxSolutionDetail).isPresent()) {
								request.setOptyId(nxSolutionDetail.getOptyId());
								request.setCustomerName(nxSolutionDetail.getCustomerName());
								if (Optional.ofNullable(nxSolutionDetail.getExternalKey()).isPresent()) {
									request.setExternalSystemKey(String.valueOf(nxSolutionDetail.getExternalKey()));
								}
								if(StringConstants.DEAL_ACTION_PD_CLONE.equalsIgnoreCase(nxMpDeal.getAction())) {
									nxSolutionDetail = nxSolutionDetailsRepository.getSolutionByDealId(nxMpDeal.getDealID());
									if (Optional.ofNullable(nxSolutionDetail).isPresent()) {
										request.setOptyId(nxSolutionDetail.getOptyId());
										request.setCustomerName(nxSolutionDetail.getCustomerName());
										if (Optional.ofNullable(nxSolutionDetail.getExternalKey()).isPresent()) {
											request.setExternalSystemKey(String.valueOf(nxSolutionDetail.getExternalKey()));
										}
									}
								}
							}
							NxMpSolutionDetails nxMpSolutionDetails = nxMpSolutionDetailsRepository.findByNxTxnId(nxMpDeal.getNxTxnId());
							if (Optional.ofNullable(nxMpSolutionDetails).isPresent()) {
								request.setExternalSystemName(nxMpSolutionDetails.getSourceSystem());
							}
							request.setOptyId(nxMpDeal.getOptyId());
							request.setOffer(nxMpDeal.getOffer());
							request.setPricingManager(nxMpDeal.getPricingManager());
							request.setDealDescription(nxMpDeal.getDealDescription());
							request.setSvId(nxMpDeal.getSvId());
							CompletableFuture.runAsync(()-> {
								rateLetterStatusService.triggerDmaapEvent(request);
							});
						} catch (Exception e) {
							logger.error("Error : While performing isApprovedThenNotify() {}", e.getMessage());
						}
					});
				}
			}
		}
	}	
	
	protected boolean checkMrcOrNrc(GetTransactionLineItemPriceNew effectivePrice,
			GetTransactionLineItemPriceNew netEffectivePrice, String discountPctg) {
		/*Optional<?> optEffectivePrice = Optional.ofNullable(effectivePrice);
		Optional<?> optNetEffectivePrice = Optional.ofNullable(netEffectivePrice);

		if (optEffectivePrice.isPresent() || optNetEffectivePrice.isPresent()
				|| Optional.ofNullable(discountPctg).isPresent()) {
			if (Optional.ofNullable(effectivePrice).isPresent() || Optional.ofNullable(netEffectivePrice).isPresent()
					|| Optional.ofNullable(discountPctg).isPresent()) {
				return true;
			}
		}*/
		
		if ((Optional.ofNullable(effectivePrice).isPresent() && effectivePrice.getValue().floatValue() != 0.0) 
				|| (Optional.ofNullable(netEffectivePrice).isPresent() && netEffectivePrice.getValue().floatValue() != 0.0)
				|| (Optional.ofNullable(discountPctg).isPresent() && !"0.0".equals(discountPctg))) {
				return true;
		}
		return false;
	}
	
	protected NxMpPriceDetails createPriceDetails(Long nxTxnId,String frequency,GetTransactionLineItem item, Map<String, Object> methodMap,HashMap<String, String> nxSiteIdMap) {
		NxMpPriceDetails priceDetails = new NxMpPriceDetails();
		
		if(null != nxSiteIdMap && nxSiteIdMap.containsKey(item.getNxSiteId())) {
			String nxSiteId = nxSiteIdMap.get(item.getNxSiteId());
			priceDetails.setNxDesignId(Long.valueOf(nxSiteId));
		} else if (null != item.getNxSiteId()){
			priceDetails.setNxDesignId(Long.valueOf(item.getNxSiteId()));
		}
		if(getCustomNxDesignId(methodMap, priceDetails.getNxSiteCountry())) {
			priceDetails.setNxDesignId((Long)methodMap.get("currentNxDesignId"));
		}
		if(Optional.ofNullable(item.getNxSiteCountry()).isPresent()) {
			if("USA".equalsIgnoreCase(item.getNxSiteCountry())) {
				priceDetails.setNxSiteCountry("US");
			} else {
				priceDetails.setNxSiteCountry(item.getNxSiteCountry());
			}
		}
		priceDetails.setMpDocumentNumber(Long.parseLong(item.getDocumentNumber()));
		priceDetails.setNxTxnId(nxTxnId);
		priceDetails.setBeid(item.getUsocId());
		priceDetails.setFrequency(frequency);
		priceDetails.setAsrItemId(item.getAsrItemId());
		priceDetails.setTerm(item.getTerm());
		logger.info("Before reverse jurisdiction :==>> "+ item.getJurisdiction());
		String reverseJurisdiction  = getItemIdFromMap(item.getJurisdiction());
		logger.info("After reverse jurisdiction :==>> "+ reverseJurisdiction);
		priceDetails.setJurisdiction(reverseJurisdiction);
		priceDetails.setUniqueId(item.getUniqueIds());
		priceDetails.setSocVersion(item.getSocVersion());
		priceDetails.setEthTokenId(item.getTokenId());
		return priceDetails;
	}
	
	private boolean getCustomNxDesignId(Map<String, Object> methodMap, String country) {
		if(methodMap.containsKey("currentDeal") && null != methodMap.get("currentDeal")) {
			NxMpDeal currentDeal = (NxMpDeal) methodMap.get("currentDeal");
			if(null != currentDeal && (MyPriceConstants.BVOIP_PRODUCT_MAP.containsKey(currentDeal.getOfferId()))) {
				Long value = 0L;
				if(methodMap.containsKey("countryKeys")) {
					Map<String, Long> countryMap = (Map<String, Long>) methodMap.get("countryKeys");
					if(countryMap.containsKey(country)) {
						value = countryMap.get(country);
					} else {
						value = getNewId(methodMap);
						countryMap.put(country, value);
					}
				} else {
					value = getNewId(methodMap);
					Map<String, Long> countryMap = new HashMap<String, Long>();
					countryMap.put(country, value);
					methodMap.put("countryKeys", countryMap);
				}
				methodMap.put("currentNxDesignId", value);
				return true;
			}
		}
		return false;
	}
	
	private Long getNewId(Map<String, Object> methodMap) {
		Long id = 1L;
		if(methodMap.containsKey("maxId") && null != methodMap.get("maxId")) {
			id = (Long) methodMap.get("maxId");
			id++;
			methodMap.put("maxId", id);
		} else {
			methodMap.put("maxId", id);
		}
		return id;
	}
	
	private String getItemIdFromMap(String key) {
		if(jurisdictionMap.containsKey(key)){
			logger.info("key found in jurisdiction map :==>> "+key);
			return jurisdictionMap.get(key);
		} else {
			logger.info("key not found in jurisdiction map :==>> "+key);
			String value = getItemIdFromDb(key);
			if(Optional.ofNullable(value).isPresent()) {
				jurisdictionMap.put(key, value);
				return jurisdictionMap.get(key);
			} else {
				return null;
			}
		}
	}

	static {
		jurisdictionMap.put("MOW", "MOW");
		jurisdictionMap.put("US", "US");
	}
	
	private String getItemIdFromDb(String key) {
		NxLookupData data = nxLookupDataRepository.findByDatasetNameAndDescription("MP_ADE_REVERSE_JURISDICTION", key);
		if(Optional.ofNullable(data).isPresent()) {
			return data.getItemId();
		}
		return null;
	}
	
	
	public GetTransactionLineResponse getTransactionLineSalesOne(String transactionId) throws SalesBusinessException {
		logger.info("Entering getTransactionLineSalesOne() method");
		
		GetTransactionLineResponse response = null;
		try {
			String uri = env.getProperty("myprice.getTransactionLine");
			uri = uri.replace("{transactionId}", transactionId);
			Map<String, String> headers = new HashMap<String, String>();
			headers.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.MYPRICE_AUTHORIZATION));
			headers.put(StringConstants.REQUEST_CONTENT_TYPE, "application/json");
			String proxy = null;
			if(StringConstants.CONSTANT_Y.equalsIgnoreCase(isProxyEnabled)) {
				proxy = env.getProperty(CommonConstants.CDP_HTTP_PROXY);
			}
			String transResponse = httpRestClient.callHttpRestClient(uri, HttpMethod.GET, null, null, 
					headers, proxy);

			if (null != transResponse && !transResponse.isEmpty()) {
				response = (GetTransactionLineResponse) restClient.processResult(transResponse,
						GetTransactionLineResponse.class);
	
				logger.info("GetTransactionLine response for transaction " + transactionId + "is: " + response);
			}
		} catch (SalesBusinessException e) {
			logger.error("Exception occured in Myprice getTransactionLine call");
		}
		logger.info("Existing getTransactionLine() method");
		return response;

	}
	public void publishDmaapForAutomationFlow(NxMpDeal  nxMpDeal,String status) {
		RateLetterStatusRequest request = new RateLetterStatusRequest();
		try {
			request.setDealId(nxMpDeal.getDealID());
			request.setDealVersion(nxMpDeal.getVersion());
			request.setDealRevisionNumber(nxMpDeal.getRevision());
			request.setDealStatus(status);
			request.setCpqId(nxMpDeal.getTransactionId());
			request.setPriceScenarioId(nxMpDeal.getPriceScenarioId());
			request.setRlQuoteUrl(nxMpDeal.getQuoteUrl());
			request.setRlExpirationDate(nxMpDeal.getRateLetterExpiresOn());
			NxSolutionDetail nxSolutionDetail = nxSolutionDetailsRepository.findByNxSolutionId(nxMpDeal.getSolutionId());
			if (Optional.ofNullable(nxSolutionDetail).isPresent()) {
				request.setOptyId(nxSolutionDetail.getOptyId());
				request.setCustomerName(nxSolutionDetail.getCustomerName());
				if (Optional.ofNullable(nxSolutionDetail.getExternalKey()).isPresent()) {
					request.setExternalSystemKey(String.valueOf(nxSolutionDetail.getExternalKey()));
				}
			}
			NxMpSolutionDetails nxMpSolutionDetails = nxMpSolutionDetailsRepository.findByNxTxnId(nxMpDeal.getNxTxnId());
			if (Optional.ofNullable(nxMpSolutionDetails).isPresent()) {
				request.setExternalSystemName(nxMpSolutionDetails.getSourceSystem());
			}
			
			request.setOptyId(nxMpDeal.getOptyId());
			request.setOffer(nxMpDeal.getOffer());
			request.setPricingManager(nxMpDeal.getPricingManager());
			request.setDealDescription(nxMpDeal.getDealDescription());
			request.setSvId(nxMpDeal.getSvId());
			CompletableFuture.runAsync(()->{
				rateLetterStatusService.triggerDmaapEvent(request);
			});
		} catch (Exception e) {
			logger.error("Error : While performing publishDmaap Message For Automation Flow() {}", e.getMessage());
		}
	}

	private HashMap<String, String> updateLocationBlock(List<GetTransactionLineItem> items,Long nTxnId,String flowType,Long nxSolutionId){
		NxMpSiteDictionary nxMpSiteDictionary = nxMpSiteDicRepo.findByNxTxnId(nTxnId);
		//nxMpSiteDictionary.setSiteAddress("{ \"siteAddress\" :  \"[ {\\n  \\\"country\\\" : \\\"US\\\",\\n  \\\"nxSiteId\\\" : \\\"1\\\",\\n  \\\"siteInfoSource\\\" : \\\"inr\\\",\\n  \\\"address\\\" : \\\"701 BROOKS AVE,THIEF RIVER FAL,MN,56701,US\\\",\\n  \\\"city\\\" : \\\"THIEF RIVER FAL\\\",\\n  \\\"postalCode\\\" : \\\"56701\\\",\\n  \\\"regionFranchiseStatus\\\" : \\\"\\\",\\n  \\\"addressLine\\\" : \\\"701 BROOKS AVE\\\",\\n  \\\"grsSiteId\\\" : \\\"90970726\\\",\\n  \\\"icoreSiteId\\\" : \\\"3090214\\\",\\n  \\\"buildingClli\\\" : \\\"\\\",\\n  \\\"swcCLLI\\\" : \\\"\\\",\\n  \\\"popClli\\\" : \\\"\\\",\\n  \\\"state\\\" : \\\"MN\\\",\\n  \\\"validationStatus\\\" : \\\"VALID\\\"\\n}, {\\n  \\\"country\\\" : \\\"US\\\",\\n  \\\"nxSiteId\\\" : \\\"2\\\",\\n  \\\"siteInfoSource\\\" : \\\"inr\\\",\\n  \\\"address\\\" : \\\"701 BROOKS AVE S,THIEF RIVER FALLS,MN,56701,US\\\",\\n  \\\"city\\\" : \\\"THIEF RIVER FALLS\\\",\\n  \\\"postalCode\\\" : \\\"56701\\\",\\n  \\\"regionFranchiseStatus\\\" : \\\"\\\",\\n  \\\"addressLine\\\" : \\\"701 BROOKS AVE S\\\",\\n  \\\"grsSiteId\\\" : \\\"90970729\\\",\\n  \\\"icoreSiteId\\\" : \\\"3090222\\\",\\n  \\\"buildingClli\\\" : \\\"\\\",\\n  \\\"swcCLLI\\\" : \\\"\\\",\\n  \\\"popClli\\\" : \\\"\\\",\\n  \\\"state\\\" : \\\"MN\\\",\\n  \\\"validationStatus\\\" : \\\"VALID\\\"\\n}, {\\n  \\\"country\\\" : \\\"US\\\",\\n  \\\"nxSiteId\\\" : \\\"3\\\",\\n  \\\"siteInfoSource\\\" : \\\"inr\\\",\\n  \\\"address\\\" : \\\"4300 MARKET POINTE DR,BLMGTN,MN,55435,US\\\",\\n  \\\"city\\\" : \\\"BLMGTN\\\",\\n  \\\"postalCode\\\" : \\\"55435\\\",\\n  \\\"regionFranchiseStatus\\\" : \\\"\\\",\\n  \\\"addressLine\\\" : \\\"4300 MARKET POINTE DR\\\",\\n  \\\"grsSiteId\\\" : \\\"91072758\\\",\\n  \\\"icoreSiteId\\\" : \\\"3198052\\\",\\n  \\\"buildingClli\\\" : \\\"\\\",\\n  \\\"swcCLLI\\\" : \\\"\\\",\\n  \\\"popClli\\\" : \\\"\\\",\\n  \\\"state\\\" : \\\"MN\\\",\\n  \\\"validationStatus\\\" : \\\"VALID\\\"\\n}, {\\n  \\\"country\\\" : \\\"\\\",\\n  \\\"nxSiteId\\\" : \\\"\\\",\\n  \\\"siteInfoSource\\\" : \\\"inr\\\",\\n  \\\"address\\\" : \\\"FLR MAIN RM CLOSET N,THIEF RIVER FAL,MN,56701\\\",\\n  \\\"city\\\" : \\\"THIEF RIVER FAL\\\",\\n  \\\"postalCode\\\" : \\\"56701\\\",\\n  \\\"regionFranchiseStatus\\\" : \\\"\\\",\\n  \\\"addressLine\\\" : \\\"FLR MAIN RM CLOSET N\\\",\\n  \\\"circuitId\\\" : \\\"IUEC870761ATI\\\",\\n  \\\"accessSpeed\\\" : \\\"100 MBPS BASIC SVC\\\",\\n  \\\"buildingClli\\\" : \\\"\\\",\\n  \\\"swcCLLI\\\" : \\\"TRFLMNTH\\\",\\n  \\\"name\\\" : \\\"\\\",\\n  \\\"popClli\\\" : \\\"FARGNDBC\\\",\\n  \\\"state\\\" : \\\"MN\\\",\\n  \\\"validationStatus\\\" : \\\"VALID\\\"\\n} ]\"}");
		//nxMpSiteDicRepo.saveAndFlush(nxMpSiteDictionary);
		HashMap<String, String> nxSiteIdMap = null;
		try {
		if(Optional.ofNullable(nxMpSiteDictionary).isPresent()) {
			if(StringConstants.FLOW_TYPE_IPnE.equalsIgnoreCase(flowType) || StringConstants.FLOW_TYPE_MP_FIRM.equalsIgnoreCase(flowType)){		
				JSONArray locationsAddress;
				JSONObject siteJsonObject;
				HashMap<String, String> siteIdMap;
				if(Optional.ofNullable(nxMpSiteDictionary.getSiteJson()).isPresent()) {
					siteJsonObject = new JSONObject(nxMpSiteDictionary.getSiteJson());
					JSONArray locationsJsonArray = siteJsonObject.getJSONArray("locations");
					siteIdMap = new HashMap<>();
					//locationsJsonArray.forEach(object->{
					for(int i = 0; i > locationsJsonArray.length() ; i++) {
						String nxSiteId = null;
						try {
							String nxMpSiteId = locationsJsonArray.getJSONObject(i).get("mpNxSiteId").toString();
							if(Optional.ofNullable(nxMpSiteId).isPresent()) {
								nxSiteId = locationsJsonArray.getJSONObject(i).get("nxSiteId").toString();
								siteIdMap.put(nxMpSiteId, nxSiteId);
							}
						} catch (JSONException e) {
							nxSiteId = locationsJsonArray.getJSONObject(i).get("nxSiteId").toString();
							siteIdMap.put(nxSiteId, nxSiteId);
						}
					}
					locationsAddress = getNewLocationArray(items,nxSolutionId,siteIdMap);
					if(null != locationsAddress && locationsAddress.length() > 0) {
						//locationsAddress.forEach(item ->{
						for(int i = 0; i > locationsAddress.length() ; i++) {
							locationsJsonArray.put(locationsAddress.get(i));
						}
					}
					siteJsonObject.put("locations", locationsJsonArray);
				} else {
					siteIdMap = new HashMap<>();
					locationsAddress=getNewLocationArray(items,nxSolutionId,siteIdMap);
					siteJsonObject = new JSONObject();
					siteJsonObject.put("locations", locationsAddress);
				}
				nxMpSiteDictionary.setSiteJson(siteJsonObject.toString());
				nxSiteIdMap = siteIdMap;
			} else {
				JSONArray siteAddress;
				JSONObject siteAddressObject;
				HashMap<String, String> siteIdMap;
				if(Optional.ofNullable(nxMpSiteDictionary.getSiteAddress()).isPresent()) {
					siteAddressObject = new JSONObject(nxMpSiteDictionary.getSiteAddress());
					String siteAdrress = siteAddressObject.get("siteAddress").toString();
					JSONArray siteAddressArray = new JSONArray(siteAdrress);
					siteIdMap = new HashMap<>();
				//	siteAddressArray.forEach(object->{
					for(int i = 0; i > siteAddressArray.length() ; i++) {
						
						String nxSiteId = null;
						try {
							String nxMpSiteId = siteAddressArray.getJSONObject(i).get("mpNxSiteId").toString();
							if(Optional.ofNullable(nxMpSiteId).isPresent()) {
								nxSiteId = siteAddressArray.getJSONObject(i).get("nxSiteId").toString();
								siteIdMap.put(nxMpSiteId, nxSiteId);
							}
						} catch (JSONException e) {
							nxSiteId = siteAddressArray.getJSONObject(i).get("nxSiteId").toString();
							siteIdMap.put(nxSiteId, nxSiteId);
						}
					}
					siteAddress = getNewLocationArray(items,nxSolutionId,siteIdMap);
					if(null != siteAddress && siteAddress.length() > 0) {
						for(int i = 0; i > siteAddress.length() ; i++) {
							siteAddressArray.put(siteAddress.get(i));
						}
						/*
						 * siteAddress.forEach(item ->{ siteAddressArray.put(item); });
						 */
					}
					// siteAddressObject.put("siteAddress", siteAddressArray.toString());
					nxMpSiteDictionary.setSiteAddress("{ \"siteAddress\" :  " + siteAddressArray.toString() + "}");
				} else {
					siteIdMap = new HashMap<>();
					siteAddress = getNewLocationArray(items,nxSolutionId,siteIdMap);
					// siteAddressObject = new JSONObject();
					// siteAddressObject.put("siteAddress", siteAddress.toString());
					nxMpSiteDictionary.setSiteAddress("{ \"siteAddress\" :  " + siteAddress.toString() + "}");
				}
				// nxMpSiteDictionary.setSiteAddress(siteAddressObject.toString());
				nxSiteIdMap = siteIdMap;
			}
			nxMpSiteDicRepo.saveAndFlush(nxMpSiteDictionary);
		}else {
			HashMap<String, String> siteIdMap = new HashMap<>();
			JSONArray locationsAddress=getNewLocationArray(items,nxSolutionId,siteIdMap);
			JSONObject siteJsonObject = new JSONObject();
			siteJsonObject.put("locations", locationsAddress);
			nxMpSiteDictionary = new NxMpSiteDictionary();
			nxMpSiteDictionary.setSiteJson(siteJsonObject.toString());
			nxMpSiteDictionary.setNxTxnId(nTxnId);
			nxMpSiteDictionary.setActiveYN(StringConstants.CONSTANT_Y);
			nxMpSiteDictionary.setSourceSystem(MyPriceConstants.SOURCE_SYSTEM);
			nxMpSiteDicRepo.saveAndFlush(nxMpSiteDictionary);
			nxSiteIdMap = siteIdMap;
		}
		
	}catch(JSONException e) {
		e.printStackTrace();
	}
		return nxSiteIdMap;
	}
	
	public JSONArray getNewLocationArray(List<GetTransactionLineItem> items,Long nxSolutionId,HashMap<String, String> siteIdMap) throws JSONException  {
		if(CollectionUtils.isNotEmpty(items))  {
			AtomicInteger nxSiteIdCounter = getNxSiteIdCounter(nxSolutionId);
			JSONArray locationArray = new JSONArray();
			items.forEach(item->{
				if(Optional.ofNullable(item.getNxSiteId()).isPresent() && !siteIdMap.containsKey(item.getNxSiteId())) { 
					JSONObject object = new JSONObject();
					try {
						object.put("locName", item.getNxSiteName());
						
						object.put("mpNxSiteId", item.getNxSiteId());
						if(item.getNxSiteId().matches("[0-9]+")) {
							object.put("nxSiteId", item.getNxSiteId());
							siteIdMap.put(item.getNxSiteId(), item.getNxSiteId());
						} else {
							int counter = nxSiteIdCounter.getAndIncrement();
							object.put("nxSiteId", counter);
							siteIdMap.put(item.getNxSiteId(), String.valueOf(counter));
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					locationArray.put(object);
				}
			});
			updateNxSiteIdCounter(nxSolutionId,nxSiteIdCounter);
			return locationArray;
		}
		return null;
	}
	
	private AtomicInteger getNxSiteIdCounter(Long nxSolutionId) {
		NxDesignAudit nxDesignAudit = nxDesignAuditRepository.findByNxRefIdAndTransaction(nxSolutionId, MyPriceConstants.REQUEST_SITE_ID_REF);
		AtomicInteger nxSiteIdCounter = null;
		if(nxDesignAudit != null) {
			nxSiteIdCounter = new AtomicInteger(Integer.parseInt(nxDesignAudit.getData()));
		}else {
			nxSiteIdCounter = new AtomicInteger(1);
		}
		return nxSiteIdCounter;
	}
	
	private void updateNxSiteIdCounter(Long nxSolutionId,AtomicInteger nxSiteIdCounter) {
		NxDesignAudit nxDesignAudit = nxDesignAuditRepository.findByNxRefIdAndTransaction(nxSolutionId, MyPriceConstants.REQUEST_SITE_ID_REF);
		if(nxDesignAudit != null) {
			nxDesignAudit.setData(String.valueOf(nxSiteIdCounter));
			nxDesignAudit.setModifedDate(new Date());
		}else {
			nxDesignAudit = new NxDesignAudit();
			nxDesignAudit.setData(String.valueOf(nxSiteIdCounter));
			nxDesignAudit.setTransaction(MyPriceConstants.REQUEST_SITE_ID_REF);
			nxDesignAudit.setNxRefId(nxSolutionId);
			nxDesignAudit.setCreatedDate(new Date());
		}
		nxDesignAuditRepository.saveAndFlush(nxDesignAudit);
	}

	public GetTransactionLineResponse getTransactionLineConfigDesignSolution(String transactionId, String offername,Long offset, Map<String, Object> requestMap) throws SalesBusinessException {
		logger.info("Entering getTransactionLine() method to get the config solution and design details");
		
		GetTransactionLineResponse response = null;
		try {
			String uri = env.getProperty("myprice.getTransactionLine.rest.url");
			uri = uri.replace("{transactionId}", transactionId);
			String fields ="";
			if (offername == null) {
				fields=	env.getProperty("myprice.gettransactionLine.fmo.fields");
			} else if (offername.equalsIgnoreCase(MyPriceConstants.ASE_OFFER_NAME) || offername.equalsIgnoreCase(MyPriceConstants.ASENOD_OFFER_NAME) 
					|| offername.equalsIgnoreCase(MyPriceConstants.ADE_OFFER_NAME)|| offername.equalsIgnoreCase(MyPriceConstants.EPLSWAN_OFFER_NAME)||offername.equalsIgnoreCase(MyPriceConstants.ETHERNET_OFFER_NAME)) {				
				String restVersion = (requestMap != null && requestMap.containsKey(StringConstants.REST_VERSION)) ? (String) requestMap.get(StringConstants.REST_VERSION) : null;
				if(StringConstants.VERSION_2.equalsIgnoreCase(restVersion)) {
					fields = env.getProperty("myprice.restv2.gettransactionLine.ase.fields");
				}else {
					fields=	env.getProperty("myprice.gettransactionLine.ase.fields");
				}
			}
			String url = uri+fields+offset;
			//logger.info("GetTransactionLine for cofigDesignAndSolution request url: {}",url);
			Map<String, String> headers = new HashMap<String, String>();
			headers.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.MYPRICE_AUTHORIZATION));
			headers.put(StringConstants.REQUEST_CONTENT_TYPE, "application/json");
			String proxy = null;
			if(StringConstants.CONSTANT_Y.equalsIgnoreCase(isProxyEnabled)) {
				proxy = env.getProperty(CommonConstants.CDP_HTTP_PROXY);
			}
			String transResponse = httpRestClient.callHttpRestClient(url, HttpMethod.GET, null, null, 
					headers, proxy);
			if (null != transResponse && !transResponse.isEmpty()) {
				response = (GetTransactionLineResponse) restClient.processResult(transResponse,
						GetTransactionLineResponse.class);
				StringBuffer printLog = new StringBuffer("GetTransactionLine  for cofigDesignAndSolution :: transactionId: " + transactionId +
						" totalResults: "+response.getTotalResults()+" offset: "+response.getOffset()+" hasMore: "+response.isHasMore());
				logger.info("printlog {}", org.apache.commons.lang3.StringUtils.normalizeSpace(printLog.toString()));
			}
			
		} catch (SalesBusinessException e) {
			logger.error("exception occured in Myprice getTransactionLine call : "+ e);
		}
		logger.info("Existing getTransactionLine() method");
		return response;
	}
	
	public GetTransactionLineResponse getTransactionLineConfigDesignBasedOnField(String transactionId, String offerName,String subOfferName,Long offset,String field, Map<String, Object> requestMap,String circuitId) throws SalesBusinessException {
		logger.info("Entering getTransactionLine() method to get the config solution and design details");
		
		GetTransactionLineResponse response = null;
		try {
			String uri = env.getProperty("myprice.getTransactionLine.rest.url");
			uri = uri.replace("{transactionId}", transactionId);
			String fields ="";
			String restVersion = (requestMap != null && requestMap.containsKey(StringConstants.REST_VERSION)) ? (String) requestMap.get(StringConstants.REST_VERSION) : null;
			String sourcName = requestMap.containsKey(StringConstants.SOURCE_NAME) ?(String) requestMap.get(StringConstants.SOURCE_NAME ):"";
			if((MyPriceConstants.ASE_OFFER_NAME.equalsIgnoreCase(offerName) || 
					(MyPriceConstants.ASENOD_OFFER_NAME.equalsIgnoreCase(offerName) &&	MyPriceConstants.ASENOD_IR.equalsIgnoreCase(subOfferName))
					||	MyPriceConstants.ADE_OFFER_NAME.equalsIgnoreCase(offerName)||MyPriceConstants.EPLSWAN_OFFER_NAME.equalsIgnoreCase(offerName)||MyPriceConstants.ETHERNET_OFFER_NAME.equalsIgnoreCase(offerName))&& field!=null ) {

				if(StringConstants.VERSION_2.equalsIgnoreCase(restVersion) && MyPriceConstants.ASENOD_IR.equalsIgnoreCase(subOfferName)) {
					fields=	env.getProperty("myprice.restv2.gettransactionLine.ase.asrItemId.fields");
					String asrItemIdField = env.getProperty("myprice.restv2.astItemIds");
					String[] asrItemIds = field.split(",");
					StringBuilder asrFilter = new StringBuilder(); 
					for(String asr : asrItemIds) {
						if(asrFilter.length() != 0) {
							asrFilter.append(",");
						}
						asrFilter.append(asrItemIdField.replace("{asrItemID}", asr));
					}
					fields = fields.replace("{lii_asrID_ql}", asrFilter);
				}
				else if(MyPriceConstants.ADE_OFFER_NAME.equalsIgnoreCase(offerName)) {
					if(StringConstants.IPNE.equalsIgnoreCase(sourcName)) {
						
						fields=	env.getProperty("myprice.gettransactionLine.ipne.ade.asrItemId.fields");
						fields = fields.replace("{asrItemID}", field);
					}
					else {
						fields=	env.getProperty("myprice.gettransactionLine.ase.asrItemId.fields");
						fields = fields.replace("{asrItemID}", field);
					}
					
				}else if(StringConstants.VERSION_2.equalsIgnoreCase(restVersion) && (MyPriceConstants.EPLSWAN_OFFER_NAME.equalsIgnoreCase(offerName)||(MyPriceConstants.ETHERNET_OFFER_NAME.equalsIgnoreCase(offerName)))) {
					
					String productType = (requestMap != null && requestMap.containsKey(MyPriceConstants.PRODUCT_TYPE)) ? (String) requestMap.get(MyPriceConstants.PRODUCT_TYPE) : null;
					if(MyPriceConstants.ETHERNET_OFFER_NAME.equalsIgnoreCase(productType)) {
						productType=MyPriceConstants.LOCALACCESS_PRODUCT_NAME;
						fields=	env.getProperty("myprice.restv2.gettransactionLine.ethernet.nxSiteId.fields");
					}
					else {
						productType=MyPriceConstants.EPLSWAN_PRODUCT_NAME;
						fields=	env.getProperty("myprice.restv2.gettransactionLine.eplswan.nxSiteId.fields");
					}
					String nxSiteIdField = env.getProperty("myprice.restv2.nxSiteIds");
					String[] nxSiteIds = field.split(",");
					StringBuilder nxSiteIdFilter = new StringBuilder();
					for(String siteId : nxSiteIds) {
						if(nxSiteIdFilter.length() != 0) {
							nxSiteIdFilter.append(",");
						}
						nxSiteIdFilter.append(nxSiteIdField.replace("{nxSiteId}", siteId));
					
					}
					String circuitIdField = env.getProperty("myprice.restv2.circuitIds");
					String[] circuitIds = circuitId.split(",");
					StringBuilder circuitIdFilter = new StringBuilder();
					for(String circuitid : circuitIds) {
						if(circuitIdFilter.length() != 0) {
							circuitIdFilter.append(",");
						}
						circuitIdFilter.append(circuitIdField.replace("{circuitId}", circuitid));
					
					}
					fields = fields.replace("{nxSiteId_List}", nxSiteIdFilter);
					fields = fields.replace("{circuitId_List}", circuitIdFilter);
					fields = fields.replace("{product}", productType);
					System.out.println("Fields are "+fields);
				}
				else {
					fields=	env.getProperty("myprice.gettransactionLine.ase.v1.asrItemId.fields");
					fields = fields.replace("{asrItemID}", field);
				}
			}
			if((MyPriceConstants.ASENOD_OFFER_NAME.equalsIgnoreCase(offerName) && 
					MyPriceConstants.ASENOD_3PA.equalsIgnoreCase(subOfferName)) &&  field!=null) {
				if(StringConstants.VERSION_2.equalsIgnoreCase(restVersion) && MyPriceConstants.ASENOD_3PA.equalsIgnoreCase(subOfferName)) {
					fields=	env.getProperty("myprice.restv2.gettransactionLine.asenod.nxSiteId.fields");
					String nxSiteIdField = env.getProperty("myprice.restv2.nxSiteIds");
					String[] nxSiteIds = field.split(",");
					StringBuilder nxSiteIdFilter = new StringBuilder();
					for(String siteId : nxSiteIds) {
						if(nxSiteIdFilter.length() != 0) {
							nxSiteIdFilter.append(",");
						}
						nxSiteIdFilter.append(nxSiteIdField.replace("{nxSiteId}", siteId));
					}
					fields = fields.replace("{lii_nxSiteId_ql}", nxSiteIdFilter);
				}else {
					fields=	env.getProperty("myprice.gettransactionLine.asenod.nxSiteId.fields");
					fields = fields.replace("{nxSiteId}", field);
				}
			}
			Map<String, String> requestHeaders = new HashMap<String, String>();
			requestHeaders.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.MYPRICE_AUTHORIZATION));
			requestHeaders.put(StringConstants.REQUEST_CONTENT_TYPE, "application/json");
			
			String proxy = null;
			if(StringConstants.CONSTANT_Y.equalsIgnoreCase(isProxyEnabled)) {
				proxy = env.getProperty(CommonConstants.CDP_HTTP_PROXY);
			}
			String url = uri+fields+offset;
			String transResponse = restClient.callRestApi(null, url, "GET", requestHeaders, null, proxy);
			
			if (null != transResponse && !transResponse.isEmpty()) {
				response = (GetTransactionLineResponse) restClient.processResult(transResponse,
						GetTransactionLineResponse.class);
			StringBuffer printLog = new StringBuffer("GetTransactionLine  for cofigDesignAndSolution :: transactionId: " + transactionId +
					" totalResults: "+response.getTotalResults()+" offset: "+response.getOffset()+" hasMore: "+response.isHasMore()); 	
			logger.info("printLog {}", org.apache.commons.lang3.StringUtils.normalizeSpace(printLog.toString()));
			}
			
		} catch (SalesBusinessException e) {
			logger.error("exception occured in Myprice getTransactionLine call : "+ e);
		}
		logger.info("Existing getTransactionLine() method");
		return response;
	}

	
}
