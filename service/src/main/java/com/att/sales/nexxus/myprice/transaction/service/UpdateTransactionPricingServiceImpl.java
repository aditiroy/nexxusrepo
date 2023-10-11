package com.att.sales.nexxus.myprice.transaction.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
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
import org.springframework.transaction.annotation.Transactional;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxMpDesignDocument;
import com.att.sales.nexxus.dao.model.NxMpPriceDetails;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
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
/**
 * 
 * @author Laxman Honawad
 *
 */
@Service("updateTransactionPricingServiceImpl")
public class UpdateTransactionPricingServiceImpl extends BaseServiceImpl {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(UpdateTransactionPricingServiceImpl.class);

	/** The env. */
	@Autowired
	private Environment env;

	@Autowired
	private RestClientUtil restClient;

	@Autowired
	private NxMpDealRepository nxMpDealRepository;

	@Autowired
	private NxMpDesignDocumentRepository nxMpDesignDocumentRepository;

	@Autowired
	private NxMpPriceDetailsRepository nxMpPriceDetailsRepository;
	
	@Autowired
	private ConfigureDesignWSHandler configureDesignWSHandler;
	
	@Autowired
	private HttpRestClient httpRestClient;
	
	@Value("${myprice.proxy.enabled}")
	private String isProxyEnabled;
	
	public ServiceResponse updateTransactionPricingRequest(Map<String, Object> designMap) throws SalesBusinessException {
		logger.info("Entering updateTransactionPricingRequest() method");
		Map<String, Object> apiResponse = new HashMap<String, Object>();
		UpdateTransactionPricingResponse response = null;
		try {
			String myPriceTxnId = designMap.containsKey(MyPriceConstants.MP_TRANSACTION_ID) ? designMap.get(MyPriceConstants.MP_TRANSACTION_ID).toString() : null;
			Long nxDesignId = designMap.containsKey(MyPriceConstants.NX_DESIGN_ID) ? (Long)designMap.get(MyPriceConstants.NX_DESIGN_ID) : 0L;
			Long nxTxnId = designMap.containsKey(MyPriceConstants.NX_TRANSACTION_ID) ? (Long)designMap.get(MyPriceConstants.NX_TRANSACTION_ID) : 0L;
			Long priceScenarioId = designMap.containsKey(StringConstants.PRICE_SCENARIO_ID)?(Long) designMap.get(StringConstants.PRICE_SCENARIO_ID) : 0L;
			String flowType = designMap.containsKey(MyPriceConstants.OFFER_TYPE) ? designMap.get(MyPriceConstants.OFFER_TYPE).toString() : null;		
			String priceUpdate = designMap.containsKey(MyPriceConstants.PRICE_UPDATE) ? designMap.get(MyPriceConstants.PRICE_UPDATE).toString() : null;		
			Long contractTerm = 0L;
			if(flowType.equalsIgnoreCase(MyPriceConstants.ADE_OFFER_NAME)) {
				String term = designMap.containsKey(MyPriceConstants.CONTRACT_TERM) ? (String) designMap.get(MyPriceConstants.CONTRACT_TERM) : null;
				if(StringUtils.isNotEmpty(term)) {
					contractTerm = Long.valueOf(term);
				}
			}
			//myPriceTransactionUtil.setSystemProperties();
			UpdateTransactionPricingRequest request = getRequest(myPriceTxnId, nxDesignId, nxTxnId, priceScenarioId, designMap,flowType,contractTerm);
			if(null != request) {
				
//				if(flowType.equalsIgnoreCase(MyPriceConstants.ASE_OFFER_NAME) && StringUtils.isNotEmpty(priceUpdate) && priceUpdate.equalsIgnoreCase("Y")) {
//					apiResponse = this.callUpdateTransactionPricingRequestApi(myPriceTxnId,request);
//				}
				if (flowType.equalsIgnoreCase(StringConstants.OFFERNAME_ASENOD)
						|| flowType.equalsIgnoreCase(MyPriceConstants.ASE_OFFER_NAME)) {
					apiResponse = this.callUpdateTransactionPricingRequestApi(myPriceTxnId, request);
				}
				if(flowType.equalsIgnoreCase(MyPriceConstants.ADE_OFFER_NAME)) {
					apiResponse = this.callUpdateTransactionPricingRequestApi(myPriceTxnId,request);
				}
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
			} else {
				logger.info("Design / Prices are not matching : updateTransactionPricingRequest myPrice call is not invoked for the transaction id : {}",myPriceTxnId);
				designMap.put(MyPriceConstants.RESPONSE_MSG, "Design / Prices are not matching : Update Prcing api is not invoked for transaction id : "+myPriceTxnId);
				designMap.put(MyPriceConstants.RESPONSE_STATUS, false);
			}			
		} catch (Exception e) {
			logger.error("exception occured in Myprice updateTransactionPricingRequest {}",e.getMessage());
			designMap.put(MyPriceConstants.RESPONSE_STATUS, false);
			designMap.put(MyPriceConstants.RESPONSE_MSG, "exception occured in Myprice updateTransactionPricingRequest");
		}
		logger.info("Existing updateTransactionPricingRequest() method");
		return response;

	}

	@Transactional
	public UpdateTransactionPricingRequest getRequest(String myPriceTxnId, Long nxDesignId, Long nxTxnId,
			Long priceScenarioId, Map<String, Object> designMap,String flowType,Long contractTerm) throws JSONException  {
		String logMyPriceTxnId = myPriceTxnId;
		logger.info("UTP-Request myPriceTxnId : {} ", org.apache.commons.lang3.StringUtils.normalizeSpace(logMyPriceTxnId));
		logger.info("UTP-Request nxDesignId : {}",nxDesignId);
		logger.info("UTP-Request nxTxnId : {}",nxTxnId);
		logger.info("UTP-Request priceScenarioId : {}",priceScenarioId);
		String logFlowType = flowType;
		logger.info("UTP-Request flowType : {}",logFlowType);
		
		List<UpdateTransactionLineItem> items = null;
		String designData = nxMpDealRepository.findDesignDataByDesignId(nxDesignId);
		if(Optional.ofNullable(designData).isPresent()) {
			
			if(flowType.equalsIgnoreCase(MyPriceConstants.ASE_OFFER_NAME) || flowType.equalsIgnoreCase(StringConstants.OFFERNAME_ASENOD)) {
					Site site = null;
					if (Optional.ofNullable(designData).isPresent()) {
						site = JacksonUtil.fromString(designData, Site.class);
						if (Optional.ofNullable(site).isPresent()) {
							PriceDetails priceDetails = site.getPriceDetails();
							items = processPriceDetails(priceDetails,priceScenarioId,nxDesignId, nxTxnId,designMap,flowType,0L, site.getThirdPartyInd());
						}
					}
			}
		
			if(flowType.equalsIgnoreCase(MyPriceConstants.ADE_OFFER_NAME)) {
					Circuit circuit = null;
					if (Optional.ofNullable(designData).isPresent()) {
						JSONObject jsonObject = new JSONObject(designData);
						jsonObject.remove("site");
						String modifiedDesignData = jsonObject.toString();
						circuit = JacksonUtil.fromString(modifiedDesignData, Circuit.class);
						if (Optional.ofNullable(circuit).isPresent()) {
							StringBuffer printLog = new StringBuffer("logFlowType   "+logFlowType+" UTP-Request purchaseOrderNumber : "+circuit.getPurchaseOrderNumber());
							logger.info("printLog {}", org.apache.commons.lang3.StringUtils.normalizeSpace(printLog.toString()));
							PriceDetails priceDetails = circuit.getPriceDetails();
							items = processPriceDetails(priceDetails,priceScenarioId,nxDesignId, nxTxnId,designMap,flowType,contractTerm, null);
						}
					}
			}
		}
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
	
	public List<UpdateTransactionLineItem> processPriceDetails(PriceDetails priceDetails,Long priceScenarioId,Long nxDesignId, Long nxTxnId, Map<String, Object> designMap,String flowType,Long contractTerm, String thirdPartyInd) {
		
		// get usoc id only for aseod 3pa
		Map<String,List<String>> asenod3PaUsocIdMap = new HashMap<String,List<String>>();
		if(StringConstants.CONSTANT_Y.equalsIgnoreCase(thirdPartyInd)) {
			asenod3PaUsocIdMap = configureDesignWSHandler.getAsenod3PaUsocIdMap();
		}
		
		List<UpdateTransactionLineItem> items = new ArrayList<UpdateTransactionLineItem>(); 
		Optional<?> optionalPriceDetails = Optional.ofNullable(priceDetails);
		if (optionalPriceDetails.isPresent()) {
			if (CollectionUtils.isNotEmpty(priceDetails.getComponentDetails())) {
				StringBuffer printLog = new StringBuffer("flowType.. "+flowType+" UTP-Request No of component details : =======>>>> "+priceDetails.getComponentDetails().size());
				logger.info("printLog {}", org.apache.commons.lang3.StringUtils.normalizeSpace(printLog.toString()));
				Map<String,List<String>> asenod3PaUsocId = asenod3PaUsocIdMap;
				//for(ComponentDetails componentDetails : priceDetails.getComponentDetails()) {
				priceDetails.getComponentDetails().stream().forEach(componentDetails -> {
					String componentId = componentDetails.getComponentId()!=null?componentDetails.getComponentId().toString():"";
					if (CollectionUtils.isNotEmpty(componentDetails.getPriceAttributes())) {
						StringBuffer printLogMessage = new StringBuffer( "flow type... "+flowType+" UTP-Request No of price attributes of component details : =======>>>> "+componentDetails.getPriceAttributes().size());
						logger.info("printLogMessage {}", org.apache.commons.lang3.StringUtils.normalizeSpace(printLogMessage.toString()));
						//for(PriceAttributes priceAttribute : componentDetails.getPriceAttributes()) {
						componentDetails.getPriceAttributes().stream().forEach(priceAttribute -> {
							Optional<?> beid;
							if(!asenod3PaUsocId.isEmpty()) {
								beid = Optional.ofNullable(configureDesignWSHandler.getConvertedUsocIdFor3PA(asenod3PaUsocId, priceAttribute.getBeid()));
							}else {
								beid = Optional.ofNullable(priceAttribute.getBeid());
							}
							if (beid.isPresent() && null != priceScenarioId && null != priceAttribute.getPriceScenarioId() && Long.compare(priceScenarioId, priceAttribute.getPriceScenarioId()) == 0) {
								NxMpDesignDocument nxMpDesignDocument = null;
								if(flowType.equalsIgnoreCase(MyPriceConstants.ASE_OFFER_NAME) || flowType.equalsIgnoreCase(StringConstants.OFFERNAME_ASENOD)) {
									nxMpDesignDocument = nxMpDesignDocumentRepository
										.findByTxnDesignUsocIds(nxTxnId, nxDesignId, beid.get().toString());
								} 
								if(flowType.equalsIgnoreCase(MyPriceConstants.ADE_OFFER_NAME)) {
									nxMpDesignDocument = nxMpDesignDocumentRepository
											.findByTxnDesignUsocComponentIds(nxTxnId, nxDesignId, beid.get().toString(),componentId);
								}
								if (Optional.ofNullable(nxMpDesignDocument).isPresent()) {
									UpdateTransactionLineItem transactionLineItem = null;
									GetTransactionLineItemPrice getTransactionLineItemPrice = null;
									if(null != priceAttribute.getFrequency()) {
										if("MRC".equalsIgnoreCase(priceAttribute.getFrequency())) {
											getTransactionLineItemPrice = new GetTransactionLineItemPrice();
											if(Optional.ofNullable(priceAttribute.getRequestedRate()).isPresent()) {
												getTransactionLineItemPrice.setValue(priceAttribute.getRequestedRate());
											}
											else if(Optional.ofNullable(priceAttribute.getLocalListPrice()).isPresent()){
												if(Optional.ofNullable(priceAttribute.getRequestedDiscount()).isPresent()){
													Float value = (float) (priceAttribute.getLocalListPrice() - ((priceAttribute.getLocalListPrice() * priceAttribute.getRequestedDiscount())/100));
													getTransactionLineItemPrice.setValue(value);
												}
												else{
													getTransactionLineItemPrice.setValue(priceAttribute.getLocalListPrice().floatValue());
												}
											}
										}	
										if("NRC".equalsIgnoreCase(priceAttribute.getFrequency())) {
											getTransactionLineItemPrice = new GetTransactionLineItemPrice();
											if(Optional.ofNullable(priceAttribute.getRequestedRate()).isPresent()) {
												getTransactionLineItemPrice.setValue(priceAttribute.getRequestedRate());
											}
											
											else if(Optional.ofNullable(priceAttribute.getLocalListPrice()).isPresent()){
												if(Optional.ofNullable(priceAttribute.getRequestedDiscount()).isPresent()){
													Float value = (float) (priceAttribute.getLocalListPrice() - ((priceAttribute.getLocalListPrice() * priceAttribute.getRequestedDiscount())/100));
													getTransactionLineItemPrice.setValue(value);
												}
												else{
													getTransactionLineItemPrice.setValue(priceAttribute.getLocalListPrice().floatValue());
												}
											}
										}
									}
									
									if(null !=  getTransactionLineItemPrice && Optional.ofNullable(getTransactionLineItemPrice.getValue()).isPresent()) {
										boolean exists = false;
										getTransactionLineItemPrice.setCurrency(priceAttribute.getLocalCurrency());
										for(UpdateTransactionLineItem item : items) {
											if(item.getDocumentNumber().equalsIgnoreCase(nxMpDesignDocument.getMpDocumentNumber().toString())) {
												exists = true;
												if("MRC".equalsIgnoreCase(priceAttribute.getFrequency())) {
													item.setRequestedEffectivePriceMRC(getTransactionLineItemPrice);
												} else {
													item.setRequestedEffectivePriceNRC(getTransactionLineItemPrice);
												}
											}
										}
										if(!exists) {
											transactionLineItem = new UpdateTransactionLineItem();
											transactionLineItem.setDocumentNumber(nxMpDesignDocument.getMpDocumentNumber().toString());
											if("MRC".equalsIgnoreCase(priceAttribute.getFrequency())) {
												transactionLineItem.setRequestedEffectivePriceMRC(getTransactionLineItemPrice);
											} else {
												transactionLineItem.setRequestedEffectivePriceNRC(getTransactionLineItemPrice);
											}
											if(flowType.equalsIgnoreCase(MyPriceConstants.ADE_OFFER_NAME)) {
												if(Optional.ofNullable(priceAttribute.getTerm()).isPresent()) {
													transactionLineItem.setTerm(priceAttribute.getTerm());
												} else {
													transactionLineItem.setTerm(contractTerm);
												}
											}
											items.add(transactionLineItem);
										}
										savePriceDetials(nxTxnId, nxMpDesignDocument, priceAttribute, componentDetails, designMap,flowType);
									}			
									
								}
							}

						});
					}
				});

			}
		}
		return items;
	}

	public void savePriceDetials(Long nxTxnId, NxMpDesignDocument nxMpDesignDocument,
			PriceAttributes priceAttribute,	ComponentDetails componentDetails, Map<String, Object> paramMap,String flowType) {
		NxMpPriceDetails nxMpPriceDetails = new NxMpPriceDetails();
		if(paramMap.containsKey(StringConstants.TRANSACTION_UPDATE) 
				&& paramMap.get(StringConstants.TRANSACTION_UPDATE).toString().equalsIgnoreCase(StringConstants.RECONFIGURE)){
			nxMpPriceDetails = nxMpPriceDetailsRepository.findByMpDocumentNumberAndNxTxnIdAndFrequency(nxMpDesignDocument.getMpDocumentNumber(), nxTxnId, priceAttribute.getFrequency());
			if(null != nxMpPriceDetails) {
				nxMpPriceDetails.setRequestedMRCDiscPercentage(
						priceAttribute.getRequestedMRCDiscPercentage());
				nxMpPriceDetails.setRequestedNRCDiscPercentage(
						priceAttribute.getRequestedNRCDiscPercentage());	
				nxMpPriceDetails.setFrequency(priceAttribute.getFrequency());
				nxMpPriceDetails.setBeid(priceAttribute.getBeid());
				nxMpPriceDetails.setComponentType(componentDetails.getComponentType());
				nxMpPriceDetails.setComponentId(componentDetails.getComponentId());
				nxMpPriceDetails.setComponentParentId(componentDetails.getComponentParentId());
				if("MRC".equalsIgnoreCase(priceAttribute.getFrequency())) {
					nxMpPriceDetails.setRequestedMRCEffectivePrice(priceAttribute.getRequestedRate());
				}else {
					nxMpPriceDetails.setRequestedNRCEffectivePrice(priceAttribute.getRequestedRate());
				}
				nxMpPriceDetailsRepository.save(nxMpPriceDetails);
			}
		}else {
			nxMpPriceDetails.setNxTxnId(nxMpDesignDocument.getNxTxnId());
			nxMpPriceDetails.setNxDesignId(nxMpDesignDocument.getNxDesignId());
			nxMpPriceDetails
					.setMpDocumentNumber(nxMpDesignDocument.getMpDocumentNumber());
			nxMpPriceDetails.setRequestedMRCDiscPercentage(
					priceAttribute.getRequestedMRCDiscPercentage());
			nxMpPriceDetails.setRequestedNRCDiscPercentage(
					priceAttribute.getRequestedNRCDiscPercentage());
			nxMpPriceDetails.setFrequency(priceAttribute.getFrequency());
			nxMpPriceDetails.setBeid(priceAttribute.getBeid());
			nxMpPriceDetails.setComponentType(componentDetails.getComponentType());
			nxMpPriceDetails.setComponentId(componentDetails.getComponentId());
			nxMpPriceDetails.setComponentParentId(componentDetails.getComponentParentId());
			if("MRC".equalsIgnoreCase(priceAttribute.getFrequency())) {
				nxMpPriceDetails.setRequestedMRCEffectivePrice(priceAttribute.getRequestedRate());
			}else {
				nxMpPriceDetails.setRequestedNRCEffectivePrice(priceAttribute.getRequestedRate());
			}
			nxMpPriceDetailsRepository.save(nxMpPriceDetails);
		}
	}
	
	protected Map<String, Object> callUpdateTransactionPricingRequestApi(String myPriceTxnId,UpdateTransactionPricingRequest request) throws SalesBusinessException {
		Map<String, Object> response = new HashMap<String, Object>();
		String uri = env.getProperty("myprice.updateTransactionPricingRequest");
		uri = uri.replace("{transactionId}", myPriceTxnId);
		String requestString = JacksonUtil.toString(request);
		logger.info("Json of UT Pricing request :========>>>>>"+requestString);
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
			logger.error("exception occured in Myprice updateTransactionPricingRequest call");
			e.printStackTrace();
			response.put(MyPriceConstants.RESPONSE_CODE, e.getHttpErrorCode());
			response.put(MyPriceConstants.RESPONSE_MSG, e.getMessage());
		}
		return response;
	}
	
/**	public UpdateTransactionPricingResponse callUpdateTransactionPricingRequestApi(
			UpdateTransactionPricingRequest request) throws SalesBusinessException {

		logger.info("Calling MyPrice for callupdateTransactionPricingRequestApi .");
		UpdateTransactionPricingResponse response = null;

		try {
			RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
			RestTemplate restTemplate = restTemplateBuilder
					.basicAuthorization(env.getProperty("myprice.username"), env.getProperty("myprice.password"))
					.build();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			HttpEntity<Object> entity = new HttpEntity<Object>(request, headers);
			ResponseEntity<String> respString = restTemplate.exchange(
					new URI(env.getProperty("myprice.updateTransactionPricingRequest")), HttpMethod.GET, entity,
					String.class);

			com.fasterxml.jackson.databind.ObjectMapper thisMapper = new com.fasterxml.jackson.databind.ObjectMapper();
			thisMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
			thisMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			if (null != respString.getBody()) {
				response = thisMapper.readValue(respString.getBody(), UpdateTransactionPricingResponse.class);
			}

			logger.info("The response is {}", response);
		} catch (Exception e) {
			logger.error("Exception : While processing rest client call" + e);
			logger.error("SERVICE LOG :: Error while calling mS " + e.getMessage(), e);
			throw new SalesBusinessException(MessageConstants.ADDRESS_EXCEPTION);
		}

		return response;

	}**/
}
