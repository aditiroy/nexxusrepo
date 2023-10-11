package com.att.sales.nexxus.myprice.transaction.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
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
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxDesignDetails;
import com.att.sales.nexxus.dao.model.NxMpDesignDocument;
import com.att.sales.nexxus.dao.model.NxMpPriceDetails;
import com.att.sales.nexxus.dao.repository.NxDesignDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxMpDesignDocumentRepository;
import com.att.sales.nexxus.dao.repository.NxMpPriceDetailsRepository;
import com.att.sales.nexxus.handlers.ConfigureDesignWSHandler;
import com.att.sales.nexxus.myprice.transaction.model.GetTransactionLineItemPrice;
import com.att.sales.nexxus.myprice.transaction.model.TransactionPricingReqCriteria;
import com.att.sales.nexxus.myprice.transaction.model.TransactionPricingReqDocuments;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTransactionLineItem;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTransactionLinePricing;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTransactionPricingRequest;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTransactionPricingResponse;
import com.att.sales.nexxus.reteriveicb.model.Circuit;
import com.att.sales.nexxus.reteriveicb.model.ComponentDetails;
import com.att.sales.nexxus.reteriveicb.model.PriceAttributes;
import com.att.sales.nexxus.reteriveicb.model.PriceDetails;
import com.att.sales.nexxus.reteriveicb.model.Site;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.RestClientUtil;

@Service
public class UpdateTransactionPricingServiceForSolutionImpl extends BaseServiceImpl {
	private static Logger logger = LoggerFactory.getLogger(UpdateTransactionPricingServiceForSolutionImpl.class);

	@Autowired
	private Environment env;

	@Autowired
	private RestClientUtil restClient;

	@Autowired
	private NxMpDesignDocumentRepository nxMpDesignDocumentRepository;

	@Autowired
	private ConfigureDesignWSHandler configureDesignWSHandler;

	@Autowired
	private NxMpPriceDetailsRepository nxMpPriceDetailsRepository;

	@Autowired
	private NxDesignDetailsRepository nxDesignDetailsRepository;
	
	@Autowired
	private HttpRestClient httpRestClient;
	
	@Value("${myprice.proxy.enabled}")
	private String isProxyEnabled;
	
	/**
	 * 
	 * @param designMap
	 * key: MyPriceConstants.MP_TRANSACTION_ID
	 * value type: String
	 * 
	 * key: MyPriceConstants.OFFER_TYPE
	 * value type: String
	 * 
	 * key: StringConstants.TRANSACTION_UPDATE
	 * value type: String
	 * 
	 * key: MyPriceConstants.PRICE_UPDATE
	 * value type: String
	 * 
	 * key: MyPriceConstants.NX_SOLIUTION_ID
	 * value type: Long
	 * 
	 * key: MyPriceConstants.NX_TRANSACTION_ID
	 * value type: Long
	 * 
	 * key: MyPriceConstants.NX_DESIGN_ID
	 * value type: List<Long>
	 * present: optional
	 * 
	 * key: MyPriceConstants.CONTRACT_TERM
	 * value type: String
	 * present: ADE case
	 * 
	 * @return
	 * 
	 * After return the below key values are added back to designMap
	 * key: MyPriceConstants.RESPONSE_STATUS
	 * value type: Boolean
	 * 
	 * key: MyPriceConstants.RESPONSE_CODE
	 * value type: Integer
	 * 
	 * key: MyPriceConstants.RESPONSE_MSG
	 * value type: String
	 * 
	 * key: MyPriceConstants.RESPONSE_DATA
	 * value type: String
	 * 
	 * key: MyPriceConstants.UPDATE_PRICING_UNMATCHING_ASR_IDS
	 * value type: List<String>
	 * 
	 * key: MyPriceConstants.UPDATE_PRICING_UNMATCHING_NX_DESIGN_IDS
	 * value type: List<Long>
	 * 
	 * 
	 * @throws SalesBusinessException
	 */
	public UpdateTransactionPricingResponse updateTransactionPricingRequest(Map<String, Object> designMap)
			throws SalesBusinessException {
		Map<String, Object> apiResponse = new HashMap<String, Object>();
		UpdateTransactionPricingResponse response = null;

		try {
			String myPriceTxnId = designMap.get(MyPriceConstants.MP_TRANSACTION_ID) != null
					? designMap.get(MyPriceConstants.MP_TRANSACTION_ID).toString()
					: null;
			String flowType = designMap.get(MyPriceConstants.OFFER_TYPE) != null
					? designMap.get(MyPriceConstants.OFFER_TYPE).toString()
					: null;
			String transactionUpdate = (String) designMap.get(StringConstants.TRANSACTION_UPDATE);
			String priceUpdate = (String) designMap.get(MyPriceConstants.PRICE_UPDATE);
			Long nxSolutionId = designMap.get(MyPriceConstants.NX_SOLIUTION_ID) != null
					? (Long) designMap.get(MyPriceConstants.NX_SOLIUTION_ID)
					: 0L;
			Long nxTxnId = designMap.get(MyPriceConstants.NX_TRANSACTION_ID) != null
					? (Long) designMap.get(MyPriceConstants.NX_TRANSACTION_ID)
					: 0L;
			Long priceScenarioId = designMap.containsKey(StringConstants.PRICE_SCENARIO_ID)
					? (Long) designMap.get(StringConstants.PRICE_SCENARIO_ID)
					: 0L;
			String mpsIndicator = (String) designMap.get(MyPriceConstants.MPS_INDICATOR);
			@SuppressWarnings("unchecked")
			List<Long> nxDesignIdList = (List<Long>) designMap.get(MyPriceConstants.NX_DESIGN_ID);
			Long contractTerm = 0L;
			String term = (String) designMap.get(MyPriceConstants.CONTRACT_TERM);
			if (StringUtils.isNotEmpty(term)) {
				contractTerm = Long.valueOf(term);
			}
			PassingParam passingParam = getPassingParam();
			passingParam.setFlowType(flowType);
			passingParam.setTransactionUpdate(transactionUpdate);
			passingParam.setNxSolutionId(nxSolutionId);
			passingParam.setNxTxnId(nxTxnId);
			passingParam.setPriceScenarioId(priceScenarioId);
			passingParam.setNxDesignIdList(nxDesignIdList);
			passingParam.setContractTerm(contractTerm);
			StringBuffer errorMsg= new StringBuffer();
			Map<String, Object> errorResponseMap = new HashMap<>();
			UpdateTransactionPricingRequest request = getRequest(passingParam);
			if (request != null) {
//				if ((MyPriceConstants.ASE_OFFER_NAME.equalsIgnoreCase(flowType)
//						|| StringConstants.OFFERNAME_ASENOD.equalsIgnoreCase(flowType))
//						&& "Y".equalsIgnoreCase(priceUpdate)) {
//					apiResponse = this.callUpdateTransactionPricingRequestApi(myPriceTxnId, request);
//
//				}
//				if (MyPriceConstants.ASE_OFFER_NAME.equalsIgnoreCase(flowType)
//						&& "Y".equalsIgnoreCase(priceUpdate)) {
//					apiResponse = this.callUpdateTransactionPricingRequestApi(myPriceTxnId, request);
//				}
				if ((StringConstants.OFFERNAME_ASENOD.equalsIgnoreCase(flowType)
						|| MyPriceConstants.ASE_OFFER_NAME.equalsIgnoreCase(flowType))
						&& !"N".equalsIgnoreCase(mpsIndicator)) {
					apiResponse = this.callUpdateTransactionPricingRequestApi(myPriceTxnId, request);
				}
				if (MyPriceConstants.ADE_OFFER_NAME.equalsIgnoreCase(flowType)||MyPriceConstants.EPLSWAN_OFFER_NAME.equalsIgnoreCase(flowType)) {
					apiResponse = this.callUpdateTransactionPricingRequestApi(myPriceTxnId, request);
				}

				int code = apiResponse.get(MyPriceConstants.RESPONSE_CODE) != null
						? (int) apiResponse.get(MyPriceConstants.RESPONSE_CODE)
						: 0;
				String transResponse = (String) apiResponse.get(MyPriceConstants.RESPONSE_DATA);
				if ((null != transResponse && code == CommonConstants.SUCCESS_CODE) || code == 0) {
					if (code != 0) {
						response = (UpdateTransactionPricingResponse) restClient.processResult(transResponse,
								UpdateTransactionPricingResponse.class);
						setSuccessResponse(response);
					}
					designMap.put(MyPriceConstants.RESPONSE_STATUS, true);
				} else {
					designMap.put(MyPriceConstants.RESPONSE_STATUS, false);
				}
				designMap.put(MyPriceConstants.RESPONSE_CODE, code);
				designMap.put(MyPriceConstants.RESPONSE_MSG, (String) apiResponse.get(MyPriceConstants.RESPONSE_MSG));
				designMap.put(MyPriceConstants.RESPONSE_DATA, transResponse);

			} else {
				logger.info(
						"Design / Prices are not matching : updateTransactionPricingRequest myPrice call is not invoked for the transaction id : {}",
						myPriceTxnId);
				errorMsg.append("Design / Prices are not matching : Update Prcing api is not invoked for transaction id : ");
				errorMsg.append(myPriceTxnId);
				errorResponseMap.put(MyPriceConstants.UPDATE_PRICING_ERROR_MAP, errorMsg.toString());
				designMap.put(MyPriceConstants.RESPONSE_MSG,errorResponseMap.toString());
				designMap.put(MyPriceConstants.RESPONSE_STATUS, false);
			}
			if (!passingParam.getUnmatchingAsr().isEmpty()) {
				logger.info("Cannot find matching nx_mp_design_document for asrItemId: {}", passingParam.getUnmatchingAsr());
				logger.info("Cannot find matching nx_mp_design_document for nxDesignId: {}", passingParam.getUnmatchingDesignId());
				designMap.put(MyPriceConstants.UPDATE_PRICING_UNMATCHING_ASR_IDS, new ArrayList<String>(passingParam.getUnmatchingAsr()));
				designMap.put(MyPriceConstants.UPDATE_PRICING_UNMATCHING_NX_DESIGN_IDS, new ArrayList<Long>(passingParam.getUnmatchingDesignId()));
				if(!passingParam.getUnmatchingAsr().isEmpty()) {
					errorMsg.append(" Cannot find matching nx_mp_design_document for asrItemId: ");
					errorMsg.append(passingParam.getUnmatchingAsr());
				}
				if(!passingParam.getUnmatchingDesignId().isEmpty()) {
					errorMsg.append(" Cannot find matching nx_mp_design_document for nxDesignId: ");
					errorMsg.append(passingParam.getUnmatchingDesignId());
				}
				errorResponseMap.put(MyPriceConstants.UPDATE_PRICING_ERROR_MAP, errorMsg.toString());
				designMap.put(MyPriceConstants.RESPONSE_MSG,errorResponseMap.toString());
			}
		} catch (Exception e) {
			logger.error("exception occured in Myprice updateTransactionPricingRequest", e);
			designMap.put(MyPriceConstants.RESPONSE_STATUS, false);
			designMap.put(MyPriceConstants.RESPONSE_MSG, e.getMessage());
		}
		
		logger.info("Existing updateTransactionPricingRequest() method");
		return response;
	}

	public UpdateTransactionPricingRequest getRequest(PassingParam passingParam) {
		if (passingParam.getNxDesignIdList() == null || passingParam.getNxDesignIdList().isEmpty()) {
			List<NxDesignDetails> nxDesignDetailsList = nxDesignDetailsRepository
					.findDesignDetailsaByNxSolutionId(passingParam.getNxSolutionId());
			List<Long> nxDesignIdList = nxDesignDetailsList.stream().map(n -> n.getNxDesign().getNxDesignId())
					.distinct().collect(Collectors.toList());
			passingParam.setNxDesignDetailsList(nxDesignDetailsList);
			passingParam.setNxDesignIdList(nxDesignIdList);
		} else {
			List<NxDesignDetails> nxDesignDetailsList = nxDesignDetailsRepository
					.findByNxDesignIdIn(passingParam.getNxDesignIdList());
			passingParam.setNxDesignDetailsList(nxDesignDetailsList);
		}
		Map<String, String> asenod3PaUsocIdMap = configureDesignWSHandler.getAsenod3PaUsocIdMap1();
		List<NxMpDesignDocument> nxMpDesignDocumentList = nxMpDesignDocumentRepository
				.findByNxTxnIdAndNxDesignIdIn(passingParam.getNxTxnId(), passingParam.getNxDesignIdList());
		List<NxMpPriceDetails> nxMpPriceDetailsList = nxMpPriceDetailsRepository
				.findByNxTxnId(passingParam.getNxTxnId());
		Map<String, NxMpDesignDocument> nxMpDesignDocumentAseMap = new HashMap<>();
		Map<String, NxMpDesignDocument> nxMpDesignDocumentAdeMap = new HashMap<>();
		Map<String, NxMpDesignDocument> nxMpDesignDocumentEplsMap = new HashMap<>();
		for (NxMpDesignDocument nxMpDesignDocument : nxMpDesignDocumentList) {
			nxMpDesignDocumentAseMap.put(String.join("_", String.valueOf(nxMpDesignDocument.getNxDesignId()),
					nxMpDesignDocument.getUsocId()), nxMpDesignDocument);
			nxMpDesignDocumentAdeMap.put(String.join("_", String.valueOf(nxMpDesignDocument.getNxDesignId()),
					nxMpDesignDocument.getUsocId(), nxMpDesignDocument.getMpPartNumber()), nxMpDesignDocument);
			nxMpDesignDocumentEplsMap.put(String.join("_", String.valueOf(nxMpDesignDocument.getNxDesignId()),
					nxMpDesignDocument.getMpPartNumber()), nxMpDesignDocument);
		}
		Map<String, NxMpPriceDetails> nxMpPriceDetailsMap = new HashMap<>();
		for (NxMpPriceDetails nxMpPriceDetails : nxMpPriceDetailsList) {
			nxMpPriceDetailsMap.put(String.join("_", String.valueOf(nxMpPriceDetails.getMpDocumentNumber()),
					nxMpPriceDetails.getFrequency()), nxMpPriceDetails);
		}

		passingParam.setAsenod3PaUsocIdMap(asenod3PaUsocIdMap);
		passingParam.setNxMpDesignDocumentAseMap(nxMpDesignDocumentAseMap);
		passingParam.setNxMpDesignDocumentAdeMap(nxMpDesignDocumentAdeMap);
		passingParam.setNxMpDesignDocumentEplsMap(nxMpDesignDocumentEplsMap);
		passingParam.setNxMpPriceDetailsMap(nxMpPriceDetailsMap);

		passingParam = processSite(passingParam);
		nxMpPriceDetailsRepository.saveAll(passingParam.getToBeSavedNxMpPriceDetails());

		if (!passingParam.getItemMap().isEmpty()) {
			UpdateTransactionLinePricing transactionLine = new UpdateTransactionLinePricing();
			transactionLine.setItems(new ArrayList<UpdateTransactionLineItem>(passingParam.getItemMap().values()));
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

	protected PassingParam processSite(PassingParam passingParam) {
		passingParam.getNxDesignDetailsList().stream()
				.filter(nxDesignDetails -> nxDesignDetails != null && nxDesignDetails.getDesignData() != null)
				.forEach(nxDesignDetails -> {
					Map<String, String> usocIdMap = new HashMap<>();
					if (MyPriceConstants.ASE_OFFER_NAME.equalsIgnoreCase(passingParam.getFlowType())
							|| StringConstants.OFFERNAME_ASENOD.equalsIgnoreCase(passingParam.getFlowType())) {
						Site site = JacksonUtil.fromString(nxDesignDetails.getDesignData(), Site.class);
						PriceDetails priceDetails = site.getPriceDetails();
						if (StringConstants.CONSTANT_Y.equalsIgnoreCase(site.getThirdPartyInd())) {
							usocIdMap = passingParam.getAsenod3PaUsocIdMap();
						}
						processPriceDetails(passingParam, nxDesignDetails, priceDetails, usocIdMap);
					}  else if (MyPriceConstants.ADE_OFFER_NAME.equalsIgnoreCase(passingParam.getFlowType())||MyPriceConstants.EPLSWAN_OFFER_NAME.equalsIgnoreCase(passingParam.getFlowType())) {
						JSONObject jsonObject = null;
						try {
							jsonObject = new JSONObject(nxDesignDetails.getDesignData());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						jsonObject.remove("site");
						String modifiedDesignData = jsonObject.toString();
						Circuit circuit = JacksonUtil.fromString(modifiedDesignData, Circuit.class);
						PriceDetails priceDetails = circuit.getPriceDetails();
						processPriceDetails(passingParam, nxDesignDetails, priceDetails, usocIdMap);
					}
				});
		return passingParam;
	}

	protected void processPriceDetails(PassingParam passingParam, NxDesignDetails nxDesignDetails,
			PriceDetails priceDetails, Map<String, String> usocIdMap) {
		passingParam.setDesignDetailsMatchDesignDoc(false);
		if (priceDetails == null) {
			passingParam.getUnmatchingAsr().add(nxDesignDetails.getNxDesign().getAsrItemId());
			passingParam.getUnmatchingDesignId().add(nxDesignDetails.getNxDesign().getNxDesignId());
			return;
		}
		Optional.ofNullable(priceDetails.getComponentDetails()).map(List::stream).orElse(Stream.empty())
				.filter(Objects::nonNull).forEach(componentDetails -> {
					String componentId = componentDetails.getComponentId() != null
							? componentDetails.getComponentId().toString()
							: "";
					Optional.ofNullable(componentDetails.getPriceAttributes()).map(List::stream).orElse(
							Stream.empty()).filter(
									priceAttributes -> priceAttributes != null && priceAttributes.getBeid() != null
											&& Long.compare(passingParam.getPriceScenarioId(),
													priceAttributes.getPriceScenarioId()) == 0)
							.forEach(priceAttributes -> {
								String beid = priceAttributes.getBeid();
								if (usocIdMap.containsKey(beid)) {
									beid = usocIdMap.get(beid);
								}
								NxMpDesignDocument nxMpDesignDocument = null;
								if (MyPriceConstants.ASE_OFFER_NAME.equalsIgnoreCase(nxDesignDetails.getNxDesign().getBundleCd())
										|| StringConstants.OFFERNAME_ASENOD
												.equalsIgnoreCase(nxDesignDetails.getNxDesign().getBundleCd())) {
									nxMpDesignDocument = passingParam.getNxMpDesignDocumentAseMap().get(
											String.join("_", String.valueOf(nxDesignDetails.getNxDesign().getNxDesignId()), beid));
								} else if (MyPriceConstants.ADE_OFFER_NAME
										.equalsIgnoreCase(nxDesignDetails.getNxDesign().getBundleCd())) {
									nxMpDesignDocument = passingParam.getNxMpDesignDocumentAdeMap().get(String.join("_",
											String.valueOf(nxDesignDetails.getNxDesign().getNxDesignId()), beid, componentId));
								} else if (MyPriceConstants.EPLSWAN_OFFER_NAME.equalsIgnoreCase(nxDesignDetails.getNxDesign().getBundleCd())){
										nxMpDesignDocument = passingParam.getNxMpDesignDocumentEplsMap().get(String.join("_",
												String.valueOf(nxDesignDetails.getNxDesign().getNxDesignId()), componentId));
								}
								if (nxMpDesignDocument != null) {
									passingParam.setDesignDetailsMatchDesignDoc(true);
									GetTransactionLineItemPrice getTransactionLineItemPrice = null;
									if (null != priceAttributes.getFrequency()) {
										getTransactionLineItemPrice = new GetTransactionLineItemPrice();
										if (priceAttributes.getRequestedRate() != null) {
											getTransactionLineItemPrice.setValue(priceAttributes.getRequestedRate());
										} else if (priceAttributes.getLocalListPrice() != null) {
											if (priceAttributes.getRequestedDiscount() != null) {
												Float value = (float) (priceAttributes.getLocalListPrice()
														- ((priceAttributes.getLocalListPrice()
																* priceAttributes.getRequestedDiscount()) / 100));
												getTransactionLineItemPrice.setValue(value);
											} else {
												getTransactionLineItemPrice
														.setValue(priceAttributes.getLocalListPrice().floatValue());
											}
										}

									}
									if (getTransactionLineItemPrice != null
											&& getTransactionLineItemPrice.getValue() != null) {
										getTransactionLineItemPrice.setCurrency(priceAttributes.getLocalCurrency());
										if (passingParam.getItemMap()
												.containsKey(nxMpDesignDocument.getMpDocumentNumber().toString())) {
											UpdateTransactionLineItem transactionLineItem = passingParam.getItemMap()
													.get(nxMpDesignDocument.getMpDocumentNumber().toString());
											if ("MRC".equalsIgnoreCase(priceAttributes.getFrequency())) {
												transactionLineItem
														.setRequestedEffectivePriceMRC(getTransactionLineItemPrice);
											} else {
												transactionLineItem
														.setRequestedEffectivePriceNRC(getTransactionLineItemPrice);
											}
											if (priceAttributes.getTerm() != null) {
												transactionLineItem.setTerm(priceAttributes.getTerm());
											} else {
												transactionLineItem.setTerm(passingParam.getContractTerm());
											}
										} else {
											UpdateTransactionLineItem transactionLineItem = new UpdateTransactionLineItem();
											transactionLineItem.setDocumentNumber(
													nxMpDesignDocument.getMpDocumentNumber().toString());
											passingParam.getItemMap().put(transactionLineItem.getDocumentNumber(),
													transactionLineItem);
											if ("MRC".equalsIgnoreCase(priceAttributes.getFrequency())) {
												transactionLineItem
														.setRequestedEffectivePriceMRC(getTransactionLineItemPrice);
											} else {
												transactionLineItem
														.setRequestedEffectivePriceNRC(getTransactionLineItemPrice);
											}
											if (priceAttributes.getTerm() != null) {
												transactionLineItem.setTerm(priceAttributes.getTerm());
											} else {
												transactionLineItem.setTerm(passingParam.getContractTerm());
											}
										}
										updateToBeSavedNxMpPriceDetails(passingParam, nxMpDesignDocument,
												priceAttributes, componentDetails);
									}
								}
							});
				});
		if (!passingParam.isDesignDetailsMatchDesignDoc()) {
			passingParam.getUnmatchingAsr().add(nxDesignDetails.getNxDesign().getAsrItemId());
			passingParam.getUnmatchingDesignId().add(nxDesignDetails.getNxDesign().getNxDesignId());
		}
	}

	protected void updateToBeSavedNxMpPriceDetails(PassingParam passingParam, NxMpDesignDocument nxMpDesignDocument,
			PriceAttributes priceAttributes, ComponentDetails componentDetails) {
		NxMpPriceDetails nxMpPriceDetails = null;
		if (StringConstants.RECONFIGURE.equalsIgnoreCase(passingParam.getTransactionUpdate())) {
			nxMpPriceDetails = passingParam.getNxMpPriceDetailsMap().get(String.join("_",
					String.valueOf(nxMpDesignDocument.getMpDocumentNumber()), priceAttributes.getFrequency()));
			if (nxMpPriceDetails == null) {
				return;
			}
		} else {
			nxMpPriceDetails = new NxMpPriceDetails();
			nxMpPriceDetails.setNxTxnId(nxMpDesignDocument.getNxTxnId());
			nxMpPriceDetails.setNxDesignId(nxMpDesignDocument.getNxDesignId());
			nxMpPriceDetails.setMpDocumentNumber(nxMpDesignDocument.getMpDocumentNumber());
		}
		nxMpPriceDetails.setRequestedMRCDiscPercentage(priceAttributes.getRequestedMRCDiscPercentage());
		nxMpPriceDetails.setRequestedNRCDiscPercentage(priceAttributes.getRequestedNRCDiscPercentage());
		nxMpPriceDetails.setFrequency(priceAttributes.getFrequency());
		nxMpPriceDetails.setBeid(priceAttributes.getBeid());
		nxMpPriceDetails.setComponentType(componentDetails.getComponentType());
		nxMpPriceDetails.setComponentId(componentDetails.getComponentId());
		nxMpPriceDetails.setComponentParentId(componentDetails.getComponentParentId());
		if ("MRC".equalsIgnoreCase(priceAttributes.getFrequency())) {
			nxMpPriceDetails.setRequestedMRCEffectivePrice(priceAttributes.getRequestedRate());
		} else {
			nxMpPriceDetails.setRequestedNRCEffectivePrice(priceAttributes.getRequestedRate());
		}
		passingParam.getToBeSavedNxMpPriceDetails().add(nxMpPriceDetails);
	}

	protected Map<String, Object> callUpdateTransactionPricingRequestApi(String myPriceTxnId,
			UpdateTransactionPricingRequest request) throws SalesBusinessException {
		Map<String, Object> response = new HashMap<String, Object>();
		String uri = env.getProperty("myprice.updateTransactionPricingRequest");
		uri = uri.replace("{transactionId}", myPriceTxnId);
		String requestString = JacksonUtil.toString(request);
		logger.info("Json of UT Pricing request :========>>>>>" + requestString);
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
			logger.error("exception occured in Myprice updateTransactionPricingRequest call");
			e.printStackTrace();
			response.put(MyPriceConstants.RESPONSE_CODE, e.getHttpErrorCode());
			response.put(MyPriceConstants.RESPONSE_MSG, e.getMessage());
		}
		return response;
	}
	
	protected PassingParam getPassingParam() {
		return new PassingParam();
	}

	class PassingParam {
		private String flowType;
		private String transactionUpdate;
		private Long nxSolutionId;
		private Long nxTxnId;
		private Long contractTerm;
		private List<Long> nxDesignIdList;
		private List<NxDesignDetails> nxDesignDetailsList;
		private Map<String, String> asenod3PaUsocIdMap;
		private Map<String, NxMpDesignDocument> nxMpDesignDocumentAseMap;
		private Map<String, NxMpDesignDocument> nxMpDesignDocumentAdeMap;
		private Map<String, NxMpDesignDocument> nxMpDesignDocumentEplsMap;
		private Map<String, UpdateTransactionLineItem> itemMap = new HashMap<>();
		private Map<String, NxMpPriceDetails> nxMpPriceDetailsMap;
		private List<NxMpPriceDetails> toBeSavedNxMpPriceDetails = new ArrayList<>();
		private Set<String> unmatchingAsr = new HashSet<>();
		private Set<Long> unmatchingDesignId = new HashSet<>();
		private boolean isDesignDetailsMatchDesignDoc = false;
		private Long priceScenarioId;

		public String getFlowType() {
			return flowType;
		}

		public void setFlowType(String flowType) {
			this.flowType = flowType;
		}

		public String getTransactionUpdate() {
			return transactionUpdate;
		}

		public void setTransactionUpdate(String transactionUpdate) {
			this.transactionUpdate = transactionUpdate;
		}

		public Long getNxSolutionId() {
			return nxSolutionId;
		}

		public void setNxSolutionId(Long nxSolutionId) {
			this.nxSolutionId = nxSolutionId;
		}

		public Long getNxTxnId() {
			return nxTxnId;
		}

		public void setNxTxnId(Long nxTxnId) {
			this.nxTxnId = nxTxnId;
		}

		public Long getContractTerm() {
			return contractTerm;
		}

		public void setContractTerm(Long contractTerm) {
			this.contractTerm = contractTerm;
		}

		public List<Long> getNxDesignIdList() {
			return nxDesignIdList;
		}

		public void setNxDesignIdList(List<Long> nxDesignIdList) {
			this.nxDesignIdList = nxDesignIdList;
		}

		public List<NxDesignDetails> getNxDesignDetailsList() {
			return nxDesignDetailsList;
		}

		public void setNxDesignDetailsList(List<NxDesignDetails> nxDesignDetailsList) {
			this.nxDesignDetailsList = nxDesignDetailsList;
		}

		public Map<String, String> getAsenod3PaUsocIdMap() {
			return asenod3PaUsocIdMap;
		}

		public void setAsenod3PaUsocIdMap(Map<String, String> asenod3PaUsocIdMap) {
			this.asenod3PaUsocIdMap = asenod3PaUsocIdMap;
		}

		public Map<String, NxMpDesignDocument> getNxMpDesignDocumentAseMap() {
			return nxMpDesignDocumentAseMap;
		}

		public void setNxMpDesignDocumentAseMap(Map<String, NxMpDesignDocument> nxMpDesignDocumentAseMap) {
			this.nxMpDesignDocumentAseMap = nxMpDesignDocumentAseMap;
		}

		public Map<String, NxMpDesignDocument> getNxMpDesignDocumentAdeMap() {
			return nxMpDesignDocumentAdeMap;
		}

		public void setNxMpDesignDocumentAdeMap(Map<String, NxMpDesignDocument> nxMpDesignDocumentAdeMap) {
			this.nxMpDesignDocumentAdeMap = nxMpDesignDocumentAdeMap;
		}

		public Map<String, UpdateTransactionLineItem> getItemMap() {
			return itemMap;
		}

		public Map<String, NxMpPriceDetails> getNxMpPriceDetailsMap() {
			return nxMpPriceDetailsMap;
		}

		public void setNxMpPriceDetailsMap(Map<String, NxMpPriceDetails> nxMpPriceDetailsMap) {
			this.nxMpPriceDetailsMap = nxMpPriceDetailsMap;
		}

		public List<NxMpPriceDetails> getToBeSavedNxMpPriceDetails() {
			return toBeSavedNxMpPriceDetails;
		}

		public Set<String> getUnmatchingAsr() {
			return unmatchingAsr;
		}
		
		public Set<Long> getUnmatchingDesignId() {
			return unmatchingDesignId;
		}

		public boolean isDesignDetailsMatchDesignDoc() {
			return isDesignDetailsMatchDesignDoc;
		}

		public void setDesignDetailsMatchDesignDoc(boolean isDesignDetailsMatchDesignDoc) {
			this.isDesignDetailsMatchDesignDoc = isDesignDetailsMatchDesignDoc;
		}

		public Long getPriceScenarioId() {
			return priceScenarioId;
		}

		public void setPriceScenarioId(Long priceScenarioId) {
			this.priceScenarioId = priceScenarioId;
		}

		public Map<String, NxMpDesignDocument> getNxMpDesignDocumentEplsMap() {
			return nxMpDesignDocumentEplsMap;
		}

		public void setNxMpDesignDocumentEplsMap(Map<String, NxMpDesignDocument> nxMpDesignDocumentEplsMap) {
			this.nxMpDesignDocumentEplsMap = nxMpDesignDocumentEplsMap;
		}
	}
}
