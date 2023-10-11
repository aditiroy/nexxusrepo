package com.att.sales.nexxus.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import javax.xml.bind.JAXBElement;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.CommonConstants;
import com.att.sales.nexxus.constant.FmoConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxMpConfigMapping;
import com.att.sales.nexxus.dao.model.NxMpDesignDocument;
import com.att.sales.nexxus.dao.repository.NxMpDesignDocumentRepository;
import com.att.sales.nexxus.dao.repository.NxMpPriceDetailsRepository;
import com.att.sales.nexxus.helper.MpProductEntity;
import com.att.sales.nexxus.myprice.transaction.dao.service.NxMpRepositoryService;
import com.att.sales.nexxus.myprice.transaction.model.ConfigRespProcessingBean;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdatePricingUtil;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdatePricingUtilFmo;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.ws.utility.SoapWSHandler;
import com.att.sales.nexxus.ws.utility.WSClientException;
import com.att.sales.nexxus.ws.utility.WSProcessingService;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.AttributesType;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.CommonStatusType;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.ConfigurationItemType;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.ConfigurationTransactionType;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.Configure;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.ConfigureResponse;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.ModelNamePf;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.ObjectFactory;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.ResponseIncludesType;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.ReturnSpecificAttributesType;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.SimpleAttributesType;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.SimpleDocumentType;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.SimpleDocumentsType;

@Component
public class ConfigureDesignWSHandlerFmo {
	
	private static final Logger log = LoggerFactory.getLogger(ConfigureDesignWSHandlerFmo.class);
	@Autowired
	@Qualifier("configureSolnAndProductWSClientUtility")
	private SoapWSHandler configureWSClientUtility;

	@Autowired
	private WSProcessingService wsProcessingService;
	
	@Autowired
	private NxMpDesignDocumentRepository nxMpDesignDocumentRepo;
	
	@Autowired
	private ConfigAndUpdatePricingUtilFmo configAndUpdatePricingUtilFmo;
	
	
	//private Boolean isSuccessful=false;
	
	@Autowired
	private ConfigureSolnAndProductWSHandler configureSolnAndProductWSHandler;
	
	@Autowired
	private ConfigAndUpdatePricingUtil configAndUpdatePricingUtil;
	
	@Autowired
	private NxMpPriceDetailsRepository priceDetailsRepo;
	
	@Autowired
	private NxMpRepositoryService nxMpRepositoryService;
	
	
	/** The thread size. */
	@Value("${fmo.threadPool.size}")
	private Integer threadSize;
	
	/**
	 * Gets the thread size.
	 *
	 * @return the thread size
	 */
	public Integer getThreadSize() {
		return threadSize;
	}
	
	/**
	 * Gets the excutor service.
	 *
	 * @return the excutor service
	 */
	protected ExecutorService getExcutorService() {
        return Executors.newFixedThreadPool(getThreadSize());
    }
	
	/**
	 * Initiate config design web service.
	 *
	 * @param requestMap the request map
	 * @throws SalesBusinessException the sales business exception
	 */
	public Boolean initiateConfigDesignWebService(Map<String, Object> requestMap,String inputDesignJson) throws  SalesBusinessException{
		log.info("Inside execute initiateConfigDesignWebService for configure Design","");
		 Boolean isSuccessful=false;
		try {
			if(StringUtils.isNotEmpty(inputDesignJson)) {
				//requestMap.put(FmoConstants.PORT_ID, nxDesignDetails.getComponentId());
				configAndUpdatePricingUtil.setProductTypeForInrFmo(requestMap);
				ObjectFactory objectFactory = new ObjectFactory();
				ConfigureResponse configureResponse = null;
				Configure configureRequest = objectFactory.createConfigure();
				prepareRequestBody(requestMap, configureRequest, inputDesignJson);
				// -- Calling to MyPrice to get configuration details.
				configureWSClientUtility.setWsName(MyPriceConstants.CONFIG_DESIGN_WS);
				configureResponse = wsProcessingService.initiateWebService(configureRequest,
						configureWSClientUtility, requestMap, ConfigureResponse.class);
				if(null!=configureResponse) {
					CommonStatusType commonstatus=configureResponse.getStatus();
					if(null!=commonstatus && commonstatus.getSuccess()
							.getValue().equals("true")) {
						isSuccessful=true;
						requestMap.put(MyPriceConstants.CONFIG_DESIGN_RESPONSE, configureResponse);
					}
					//this.processConfigResponse(configureResponse, requestMap);
				}
			}
		}catch(WSClientException  we) {
			isSuccessful=false;
			log.error("Exception during configure Design call: {}", we.getFaultString());
			requestMap.put(MyPriceConstants.RESPONSE_DATA, we.getFaultString());
			requestMap.put(MyPriceConstants.MP_API_ERROR, true);
			requestMap.put(MyPriceConstants.CONFIG_DESIGN_FAILED, true);
		} catch(Exception  e) {
			isSuccessful=false;
			log.error("Exception during configure Design call: {}", e);
			requestMap.put(MyPriceConstants.RESPONSE_MSG, e.getMessage());
			requestMap.put(MyPriceConstants.NEXXUS_API_ERROR, true);
			requestMap.put(MyPriceConstants.CONFIG_DESIGN_FAILED, true);
		}
		if(requestMap.containsKey(MyPriceConstants.UNIQUEID_BEID_MAP)){ 
			requestMap.remove(MyPriceConstants.UNIQUEID_BEID_MAP);
		}
		return isSuccessful;
		
	}
	
	
	
	/**
	 * Prepare request body.
	 *
	 * @param requestMap the request map
	 * @param configureRequest the configure request
	 * @param inputDesignDetails the input design details
	 */
	protected void prepareRequestBody(Map<String, Object> requestMap, Configure configureRequest, String inputDesignDetails) {
		String requestProductName=requestMap.get(MyPriceConstants.OFFER_NAME)!=null?
				(String)requestMap.get(MyPriceConstants.OFFER_NAME):"";
		String productNameForConfig=this.getProductNameForConfigRequest(requestProductName);	
		String mpProductLineId=this.getMpProductLineId(requestMap, productNameForConfig);
		requestMap.put(MyPriceConstants.MP_PRODUCT_LINE_ID,mpProductLineId);
		ObjectFactory objectFactory = new ObjectFactory();
		ConfigurationItemType item = new ConfigurationItemType();
		item.setModel(productNameForConfig);
		item.setProductLine("LD");
		item.setSegment("wireline");
		JAXBElement<String> price = objectFactory.createResponseIncludesTypePrice(CommonConstants.TRUE);
		JAXBElement<String> spare = objectFactory.createResponseIncludesTypeSpare(CommonConstants.TRUE);
		JAXBElement<String> bom = objectFactory.createResponseIncludesTypeBom(CommonConstants.TRUE);
		JAXBElement<String> bomMapping = objectFactory.createResponseIncludesTypeBomMapping(CommonConstants.TRUE);
		JAXBElement<String> attributeLabel = objectFactory.createResponseIncludesTypeAttributeLabel(CommonConstants.FALSE);
		JAXBElement<String> previousValue = objectFactory.createResponseIncludesTypePreviousValue(CommonConstants.FALSE);
		JAXBElement<String> displayedValue = objectFactory.createResponseIncludesTypeDisplayedValue(CommonConstants.FALSE);
		JAXBElement<String> hideInTransactionAttributes = objectFactory
				.createResponseIncludesTypeHideInTransactionAttributes(CommonConstants.FALSE);
		JAXBElement<String> ruleDetails = objectFactory.createResponseIncludesTypeRuleDetails(CommonConstants.TRUE);
		ResponseIncludesType responseIncludes = new ResponseIncludesType();
		ConfigurationTransactionType configTransactionType = new ConfigurationTransactionType();
		ReturnSpecificAttributesType returnSpecificAttributesType = new ReturnSpecificAttributesType();
		SimpleDocumentsType documents = new SimpleDocumentsType();
		SimpleDocumentType document = new SimpleDocumentType();
		SimpleAttributesType attributes = new SimpleAttributesType();
		attributes.getAttribute().add("transactionID_t");
		document.setVarName("transaction");
		document.setAttributes(attributes);
		documents.getDocument().add(document);
		
		//Adding filter document to restrict required property in response
		documents.getDocument().add(configAndUpdatePricingUtil.getDesignFilterDocument());
				
		returnSpecificAttributesType.setDocuments(documents);
		configTransactionType.setId(requestMap.get(MyPriceConstants.MP_TRANSACTION_ID).toString());
		configTransactionType.setProcessVarName("oraclecpqo_bmClone_2");
		configTransactionType.setDocumentVarName("transaction");
		configTransactionType.setReturnSpecificAttributes(returnSpecificAttributesType);
		configTransactionType.setDocumentNumber(requestMap.get(MyPriceConstants.MP_PRODUCT_LINE_ID).toString());
		//setting hideInTransactionResponseValue 
		String hideInTransactionResponseValue=configAndUpdatePricingUtil.getHideInTransactionResponseValue(requestMap);
		configTransactionType.setHideTransactionResponse(hideInTransactionResponseValue);
		JAXBElement<ConfigurationTransactionType> transactionType = objectFactory
				.createResponseIncludesTypeTransaction(configTransactionType);
		responseIncludes.setTransaction(transactionType);
		responseIncludes.setPrice(price);
		responseIncludes.setSpare(spare);
		responseIncludes.setBom(bom);
		responseIncludes.setBomMapping(bomMapping);
		responseIncludes.setAttributeLabel(attributeLabel);
		responseIncludes.setPreviousValue(previousValue);
		responseIncludes.setDisplayedValue(displayedValue);
		responseIncludes.setHideInTransactionAttributes(hideInTransactionAttributes);
		responseIncludes.setRuleDetails(ruleDetails);
		configureRequest.setResponseIncludes(responseIncludes);
		configureRequest.setItem(item);
		configureRequest.setAttributes(this.preapareAttributesForConfigDesign(requestMap,inputDesignDetails));
		
	}
	
	protected String getProductNameForConfigRequest(String requestProductName) {
		if(MyPriceConstants.FMO_CONFIG_ACCESS_PRODUCT.containsKey(requestProductName) ) {
			return MyPriceConstants.FMO_CONFIG_ACCESS_PRODUCT.get(requestProductName);
		}
		return requestProductName;
	}
	
	
	
	protected AttributesType preapareAttributesForConfigDesign(Map<String, Object> requestMap,
			String inputDesignDetails) {
		String offerName = requestMap.get(MyPriceConstants.OFFER_NAME) != null
				? (String) requestMap.get(MyPriceConstants.OFFER_NAME)
				: "";
		AttributesType attributesType = new AttributesType();
		// This code is use to dynamically set attributes data using
		String country=configAndUpdatePricingUtil.getCountryFromRequest(inputDesignDetails);
		String mappingRulesOfferName=this.getMappingRulesOfferName(offerName,country);
		List<NxMpConfigMapping> configMapping=
				configAndUpdatePricingUtil.getNxConfigMapping(requestMap, mappingRulesOfferName,MyPriceConstants.DESIGN_RULE);
		configAndUpdatePricingUtil.processConfigDataFromCustomeRules(requestMap, JacksonUtil.toJsonObject(inputDesignDetails));
		configAndUpdatePricingUtil.processCustomeFieldsUsingNxLookupData(requestMap,inputDesignDetails);
		String mpProductLineId=(String) requestMap.get(MyPriceConstants.MP_PRODUCT_LINE_ID);
		configAndUpdatePricingUtil.collectDataByProductIdForResponseProcessing(requestMap,mpProductLineId);
		List<Callable<Object>> callable = new ArrayList<Callable<Object>>();
		Optional.ofNullable(configMapping).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull)
				.forEach(data -> {
					ConfigExecutorService<ConfigAndUpdatePricingUtilFmo> executorService = 
							new ConfigExecutorService<ConfigAndUpdatePricingUtilFmo>(
							data);
					executorService.setT(configAndUpdatePricingUtilFmo);
					executorService.setDesignData(inputDesignDetails);
					executorService.setRequestMap(requestMap);
					callable.add(executorService);

				});
		if (CollectionUtils.isNotEmpty(callable)) {
			ExecutorService executor = getExcutorService();
			try {
				List<Future<Object>> resultLst = executor.invokeAll(callable);
				for (Future<Object> modelData : resultLst) {
					if (null != modelData && null != modelData.get()) {
						ModelNamePf site = (ModelNamePf) modelData.get();
						attributesType.getAttribute().add(site);
					}
				}
				executor.shutdown();
				executor.awaitTermination(5, TimeUnit.SECONDS);
			} catch (InterruptedException | ExecutionException e) {
				log.error("Exception from callConfigDesignExucuterService {}",e);
				Thread.currentThread().interrupt();
			} finally {
				if (!executor.isTerminated()) {
					log.error("cancel non-finished tasks");
				}
				executor.shutdownNow();
			}
		}
		return attributesType;
	}
	
	
	@SuppressWarnings("unchecked")
	protected void removeProcessCustomFields(Map<String, Object> requestMap) {
		if(requestMap.containsKey(MyPriceConstants.FMO_PROCESSED_CUSTOM_FIELDS)) {
			Set<String> processedCustomFields=(HashSet<String>)requestMap.get(MyPriceConstants.FMO_PROCESSED_CUSTOM_FIELDS);
			requestMap.keySet().removeAll(processedCustomFields);
		}
		
	}

	protected String getMappingRulesOfferName(String inputOfferName,String country) {
		if(MyPriceConstants.BVoIP.equalsIgnoreCase(inputOfferName) && !"US".equalsIgnoreCase(country)) {
			//for BVoIP International
			return MyPriceConstants.BVoIP_INTERNATIONAL;
		}
		return inputOfferName;
	}
	
	


	
	/**
	 * Process config response.
	 *
	 * @param configureResponse the configure response
	 * @param methodParam the method param
	 * @throws SalesBusinessException the sales business exception
	 */
	public void processConfigResponse(ConfigureResponse configureResponse,Map<String,Object> methodParam) 
			throws SalesBusinessException {
		String flowType=methodParam.get(MyPriceConstants.TRANSACTION_FLOW_TYPE)!=null?
				(String)methodParam.get(MyPriceConstants.TRANSACTION_FLOW_TYPE):null;
		String mpProductLineId=methodParam.get(MyPriceConstants.MP_PRODUCT_LINE_ID)!=null?
				(String)methodParam.get(MyPriceConstants.MP_PRODUCT_LINE_ID):null;
		this.setSolProductData(methodParam,mpProductLineId);			
		if(null!=configureResponse 
				&& null!=configureResponse.getTransaction() 
				&& null!=configureResponse.getTransaction().getValue() 
				&& null!=configureResponse.getTransaction().getValue().getDataXml() 
				&& null!=configureResponse.getTransaction().getValue().getDataXml().getAny()) {
			for (Element element : configureResponse.getTransaction().getValue().getDataXml().getAny()) {
				if(null!=element && null!=element.getElementsByTagName("bmt:transactionLine")) {
					NodeList transactionLineList = element.getElementsByTagName("bmt:transactionLine");
					//delete existing record for reConfigure scenario
					if(StringUtils.isNotEmpty(flowType) && flowType.equals(MyPriceConstants.TRANSACTION_FLOW_TYPE_DESIGN)) {
						this.deleteExistingRecordsByTxnIdAndDesignId(methodParam);
					}
					for (int transLineCount = 0; transLineCount < transactionLineList.getLength(); transLineCount++) {
						Node transactionLineNode = transactionLineList.item(transLineCount);
						ConfigRespProcessingBean obj=this.createResponseBean(transactionLineNode,methodParam);
						this.persistResponse(obj,methodParam);
					}	
				}
			}
		}
		
	}
	
	/**
	 * Creates the response bean.
	 *
	 * @param transactionLineNode the transaction line node
	 * @param methodParam the method param
	 * @return the config resp processing bean
	 */
	@SuppressWarnings("unchecked")
	public ConfigRespProcessingBean createResponseBean(Node transactionLineNode,Map<String,Object> methodParam) {
		Long nxTxnId=methodParam.get(MyPriceConstants.NX_TRANSACTION_ID)!=null?
				(Long)methodParam.get(MyPriceConstants.NX_TRANSACTION_ID):null;
		String mpRequestTxnId=methodParam.get(MyPriceConstants.MP_TRANSACTION_ID)!=null?
				(String)methodParam.get(MyPriceConstants.MP_TRANSACTION_ID):null;				
		ConfigRespProcessingBean result=null;
		if(null!=transactionLineNode) {
			Node mpTxnIdNode=transactionLineNode.getAttributes().getNamedItem("bmt:bs_id");
			String mpRespTxnId=null!=mpTxnIdNode?mpTxnIdNode.getTextContent():null;
			if(StringUtils.isNotEmpty(mpRespTxnId) && mpRespTxnId.equals(mpRequestTxnId)) {
				result=new ConfigRespProcessingBean();
				result.setMpTransactionId(mpRespTxnId);
				result.setNxTransactionId(nxTxnId);
				NodeList transLineChildList = transactionLineNode.getChildNodes();
				if(null!=transLineChildList) {
					for (int transLineChildCount = 0; transLineChildCount < transLineChildList
							.getLength(); transLineChildCount++) {
						Node transLineChild = transLineChildList.item(transLineChildCount);
						if(null!=transLineChild) {
							if (transLineChild.getNodeName().equalsIgnoreCase("bmt:lii_nxSiteId_ql")) {
								result.setNxSiteId(transLineChild.getTextContent());
							}
							if (transLineChild.getNodeName().equalsIgnoreCase("bmt:wl_int_ade_site_reln")) {
								result.setAdeSiteRelation(transLineChild.getTextContent());
							}
							if (transLineChild.getNodeName().equalsIgnoreCase("bmt:_line_bom_id")) {
								result.setLineBomId(transLineChild.getTextContent());
							}
							if (transLineChild.getNodeName().equalsIgnoreCase("bmt:_line_bom_parent_id")) {
								result.setLineBomParentId(transLineChild.getTextContent());
							}
							if (transLineChild.getNodeName().equalsIgnoreCase("bmt:_document_number")) {
								result.setDocumentNumber(transLineChild.getTextContent());
							}
							if (transLineChild.getNodeName().equalsIgnoreCase("bmt:_parent_doc_number")) {
								result.setParentDocNumber(transLineChild.getTextContent());
							}
							if (transLineChild.getNodeName().equalsIgnoreCase("bmt:_parent_line_item")) {
								result.setParentLineItem(transLineChild.getTextContent());
							}
							if (transLineChild.getNodeName().equalsIgnoreCase("bmt:wi_uniqueID_ql")) {
								if(StringUtils.isNotEmpty(transLineChild.getTextContent())) {
									if(methodParam.containsKey(MyPriceConstants.UNIQUEID_BEID_MAP) 
											&& null!=methodParam.get(MyPriceConstants.UNIQUEID_BEID_MAP)) {
										Map<String,String> uniqueIdBeidMap=
												(Map<String, String>) methodParam.get(MyPriceConstants.UNIQUEID_BEID_MAP);
										result.setUsocCode(uniqueIdBeidMap.get(transLineChild.getTextContent()));
									}
								}
							}
							if (transLineChild.getNodeName().equalsIgnoreCase("bmt:_line_bom_part_number")) {
								result.setLineBomPartNumber(transLineChild.getTextContent());
							}
						}
						
					}
				}
				
			}
		}
		return result;
	}
	
	/**
	 * Persist response.
	 *
	 * @param input the input
	 * @param methodParam the method param
	 * @throws SalesBusinessException the sales business exception
	 */
	public void persistResponse(ConfigRespProcessingBean input,Map<String,Object> methodParam) throws SalesBusinessException {
		String flowType=methodParam.get(MyPriceConstants.TRANSACTION_FLOW_TYPE)!=null?
				(String)methodParam.get(MyPriceConstants.TRANSACTION_FLOW_TYPE):null;
		String offerName=methodParam.get(MyPriceConstants.OFFER_NAME)!=null?(String)
				methodParam.get(MyPriceConstants.OFFER_NAME):"";		
		if(StringUtils.isNotEmpty(flowType) && flowType.equals(MyPriceConstants.TRANSACTION_FLOW_TYPE_DESIGN)) {
			this.processSolAndProductBlock(input, methodParam);
		}		
		if(!(input.getLineBomPartNumber().equalsIgnoreCase("Solution") ||
				input.getParentLineItem().equalsIgnoreCase("Solution"))){
			//This flag is use to check Design transactionLine block from configDesign response is belonging to same product id whatever we r getting in request
			//This is use to find child design block for particular product
			Boolean isChildDesign=configAndUpdatePricingUtilFmo.isProductLineIdMatchForConfigDesign(methodParam,
					input.getParentDocNumber(),offerName);
			if(isChildDesign) {
				this.processDesignBlock(input,methodParam);
			}
			
		}
	}
	
	

	protected void processDesignBlock(ConfigRespProcessingBean input,Map<String,Object> methodParam) throws SalesBusinessException {
		try {
			
			Long nxDesignId=methodParam.get(MyPriceConstants.NX_DESIGN_ID)!=null?
					(Long)methodParam.get(MyPriceConstants.NX_DESIGN_ID):null;
			String mpSolutionId=methodParam.get(MyPriceConstants.MP_SOLUTION_ID)!=null?
									(String)methodParam.get(MyPriceConstants.MP_SOLUTION_ID):"";	
			
			if(StringUtils.isNotEmpty(mpSolutionId)) {
				String portId=methodParam.get(FmoConstants.PORT_ID)!=null?
						(String)methodParam.get(FmoConstants.PORT_ID):"";
				List<NxMpDesignDocument> checkDesignForUpdate=nxMpDesignDocumentRepo.checkDesignForUpdate(mpSolutionId,
						input.getParentDocNumber(), input.getNxTransactionId());
				if(CollectionUtils.isEmpty(checkDesignForUpdate)) {
					createNewNxMpDesignDocument(input, nxDesignId, mpSolutionId,portId);
				}else {
					nxMpDesignDocumentRepo.updateDesignBySolIdAndProductIdFmo(Long.valueOf(input.getDocumentNumber()),
							input.getUsocCode(),portId,new Date(),mpSolutionId,
							input.getParentDocNumber(),input.getNxTransactionId());
					methodParam.put(mpSolutionId, true);
				}
			}					
			//isSuccessful=true;	
		}catch(Exception e) {
			log.error("Exception during processDesign", e);
			throw new SalesBusinessException(e.getMessage());
		}
		
	}
	




	private void createNewNxMpDesignDocument(ConfigRespProcessingBean input, Long nxDesignId, 
			String mpSolutionId,String mpPartNumber) {
		NxMpDesignDocument entity=new NxMpDesignDocument();
		entity.setNxDesignId(nxDesignId);
		entity.setMpSolutionId(mpSolutionId);
		entity.setMpProductLineId(input.getParentDocNumber());
		entity.setMpDocumentNumber(Long.valueOf(input.getDocumentNumber()));
		entity.setNxTxnId(input.getNxTransactionId());
		entity.setUsocId(input.getUsocCode());
		entity.setMpPartNumber(mpPartNumber);
		entity.setActiveYN(com.att.sales.nexxus.common.CommonConstants.ACTIVE_Y);
		entity.setCreatedDate(new Date());
		nxMpDesignDocumentRepo.save(entity);
	}
	
	
	
	
	/**
	 * Delete existing records by txn id and design id.
	 *
	 * @param methodParam the method param
	 * @return the long
	 */
	protected Long deleteExistingRecordsByTxnIdAndDesignId(Map<String,Object> methodParam) {
		Long nxDesignId=methodParam.get(MyPriceConstants.NX_DESIGN_ID)!=null?
				(Long)methodParam.get(MyPriceConstants.NX_DESIGN_ID):null;
		Long nxTxnId = methodParam.containsKey(MyPriceConstants.NX_TRANSACTION_ID) ?
				(long) methodParam.get(MyPriceConstants.NX_TRANSACTION_ID) : 0L;
		//delete the existing record
		priceDetailsRepo.deleteByNxTxnIdAndNxDesignId(nxTxnId, nxDesignId);
		return nxMpDesignDocumentRepo.deleteByNxTxnIdAndNxDesignId(nxTxnId, nxDesignId);
	}
	
	/**
	 * Process sol and product response.
	 *
	 * @param input the input
	 * @param methodParam the method param
	 */
	protected void processSolAndProductBlock(ConfigRespProcessingBean input,Map<String,Object> methodParam) {
		//in reconfigure scenario again we r processing solution and product response and save in db
		configureSolnAndProductWSHandler.processSolutionAndProductResponse(methodParam,input.getLineBomId(),
				input.getDocumentNumber(),input.getParentDocNumber());
	}

	public void setSolProductData(Map<String, Object> requestMap,String productId) {
		Long nxTxnId=requestMap.get(MyPriceConstants.NX_TRANSACTION_ID)!=null?
						(Long)requestMap.get(MyPriceConstants.NX_TRANSACTION_ID):null;			
		Map<String,Set<String>> solProductDataMap=nxMpRepositoryService.getDataByNxtxnIdInr(nxTxnId);
						requestMap.put(MyPriceConstants.SOLUTION_PRODUCT_DATA, solProductDataMap);
		String mpSolutionId=Optional.ofNullable(solProductDataMap).orElseGet(Collections::emptyMap).entrySet().stream()
				  .filter(e -> e.getValue().contains(productId))
				  .map(Map.Entry::getKey)
				  .findFirst()
				  .orElse(null);
		requestMap.put(MyPriceConstants.MP_SOLUTION_ID, mpSolutionId);
	}
	
	@SuppressWarnings("unchecked")
	protected String getMpProductLineId(Map<String, Object> requestMap,String productNameForConfig) {
		if(requestMap.containsKey(productNameForConfig)) {
			HashSet<MpProductEntity> productDocId=(HashSet<MpProductEntity>) requestMap.get(productNameForConfig);
			if(CollectionUtils.isNotEmpty(productDocId)) {
				for (Iterator<MpProductEntity> it = productDocId.iterator(); it.hasNext();) {
					MpProductEntity element = it.next();
					 it.remove();
					 return element.getProductDocumentNo();
				}
			}
		}
		return "";
	}
	
	
}
