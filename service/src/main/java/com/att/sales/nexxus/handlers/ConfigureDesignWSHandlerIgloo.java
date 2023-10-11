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
import org.json.simple.JSONObject;
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
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxMpConfigMapping;
import com.att.sales.nexxus.dao.model.NxMpDesignDocument;
import com.att.sales.nexxus.dao.repository.NxMpDesignDocumentRepository;
import com.att.sales.nexxus.helper.MpProductEntity;
import com.att.sales.nexxus.myprice.transaction.dao.service.NxMpRepositoryService;
import com.att.sales.nexxus.myprice.transaction.model.ConfigRespProcessingBean;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdatePricingUtil;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdatePricingUtilIgloo;
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
public class ConfigureDesignWSHandlerIgloo {

	private static final Logger log = LoggerFactory.getLogger(ConfigureDesignWSHandlerIgloo.class);
	@Autowired
	@Qualifier("configureSolnAndProductWSClientUtility")
	private SoapWSHandler configureWSClientUtility;

	@Autowired
	private WSProcessingService wsProcessingService;
	
	//private Boolean isSuccessful=false;
	
	@Autowired
	private ConfigAndUpdatePricingUtil configAndUpdatePricingUtil;
	
	@Autowired
	private NxMpDesignDocumentRepository nxMpDesignDocumentRepo;
	
	@Autowired
	private NxMpRepositoryService nxMpRepositoryService;
	
	@Autowired
	private ConfigAndUpdatePricingUtilIgloo configAndUpdatePricingUtilIgloo;
	
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
	
	
	public Boolean initiateConfigDesignWebService(JSONObject inputJson,Map<String, Object> requestMap) throws  SalesBusinessException{
		log.info("Inside execute initiateConfigDesignWebService for configure Design","");
		Boolean isSuccessful=false;
		try {
			if(null!=inputJson) {
				ObjectFactory objectFactory = new ObjectFactory();
				ConfigureResponse configureResponse = null;
				Configure configureRequest = objectFactory.createConfigure();
				requestMap.put(MyPriceConstants.PRODUCT_LINE, "LD");
				requestMap.put(MyPriceConstants.SEGMENT, "wireline");
				configAndUpdatePricingUtil.setProductTypeForInrFmo(requestMap);
				this.prepareRequestBody(requestMap, configureRequest, inputJson);
				configureWSClientUtility.setWsName(MyPriceConstants.CONFIG_DESIGN_WS);
				// -- Calling to MyPrice to get configuration details.
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
			//this catch block catching exception only for when we trigger request to mp through soap client
			//fautString , we.getFaultString();
			requestMap.put(MyPriceConstants.RESPONSE_DATA, we.getFaultString());
		} catch(Exception  e) {
			isSuccessful=false;
			log.error("Exception during configure Design call: {}", e);
			//This block is used to catch exception for request preparation and response processing
			requestMap.put(MyPriceConstants.RESPONSE_MSG, e.getMessage());
		}
		
		return isSuccessful;
		
	}
	
	protected void prepareRequestBody(Map<String, Object> requestMap, Configure configureRequest, JSONObject inputJson) {
		//String requestProductName=requestMap.get(MyPriceConstants.PRODUCT_NAME)!=null?(String)requestMap.get(MyPriceConstants.PRODUCT_NAME):"";
		//String productNameForConfig=this.getProductNameForConfigRequest(requestProductName);
		//String mpProductLineId=requestMap.get(this.getProductNameForConfigRequest(requestProductName))!=null?requestMap.get(requestProductName).toString():"";
		//String mpProductLineId=this.getMpProductLineId(requestMap, productNameForConfig);
		String mpProductLineId = getMpProductLineIdByNxTxnId(requestMap);
		requestMap.put(MyPriceConstants.MP_PRODUCT_LINE_ID,mpProductLineId);
		ObjectFactory objectFactory = new ObjectFactory();
		ConfigurationItemType item = new ConfigurationItemType();
		item.setModel("LocalAccess");
		item.setProductLine(String.valueOf(requestMap.get(MyPriceConstants.PRODUCT_LINE)));
		item.setSegment(String.valueOf(requestMap.get(MyPriceConstants.SEGMENT)));
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
		configureRequest.setAttributes(this.preapareAttributesForConfigDesign(requestMap,inputJson));
		
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
	
	protected String getProductNameForConfigRequest(String requestProductName) {
		if(MyPriceConstants.DDA_PRODUCT_MAP.containsKey(requestProductName) ) {
			return MyPriceConstants.DDA_PRODUCT_MAP.get(requestProductName);
		}
		return requestProductName;
	}
	
	protected AttributesType preapareAttributesForConfigDesign(Map<String, Object> requestMap,
			JSONObject inputJson) {
		String designDetails=JacksonUtil.toString(inputJson);
		String offerName = requestMap.get(MyPriceConstants.SOURCE) != null
				? (String) requestMap.get(MyPriceConstants.SOURCE)
				: "";
		AttributesType attributesType = new AttributesType();
		// This code is use to dynamically set attributes data using
		List<NxMpConfigMapping> configMapping=configAndUpdatePricingUtil.
				getNxConfigMapping(requestMap,offerName,MyPriceConstants.DESIGN_RULE);
		
		//configAndUpdatePricingUtil.processConfigDataFromCustomeRules(requestMap, inputJson);
		String mpProductLineId=(String) requestMap.get(MyPriceConstants.MP_PRODUCT_LINE_ID);
		configAndUpdatePricingUtil.collectDataByProductIdForResponseProcessing(requestMap,mpProductLineId);
		
		List<Callable<Object>> callable = new ArrayList<Callable<Object>>();
		Optional.ofNullable(configMapping).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull)
				.forEach(data -> {
					ConfigExecutorService<ConfigAndUpdatePricingUtilIgloo> executorService =
							new ConfigExecutorService<ConfigAndUpdatePricingUtilIgloo>(data);
					executorService.setT(configAndUpdatePricingUtilIgloo);
					executorService.setDesignData(designDetails);
					executorService.setRequestMap(requestMap);
					callable.add(executorService);

				});
		if (CollectionUtils.isNotEmpty(callable)) {
			ExecutorService executor = getExcutorService();
			try {
				List<Future<Object>> resultLst = executor.invokeAll(callable);
				for (Future<Object> modelData : resultLst) {
					if (null != modelData && null != modelData.get()) {
						ModelNamePf data = (ModelNamePf) modelData.get();
						attributesType.getAttribute().add(data);
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
	

	

	
	
	
	
	
	/**
	 * Process config response.
	 *
	 * @param configureResponse the configure response
	 * @param methodParam the method param
	 * @throws SalesBusinessException the sales business exception
	 */
	public void processConfigResponse(ConfigureResponse configureResponse,Map<String,Object> methodParam) 
			throws SalesBusinessException {
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
		String sourceName=configAndUpdatePricingUtil.getSourceName(methodParam);		
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
									if(StringUtils.isNotEmpty(sourceName) && (MyPriceConstants.SOURCE_IGLOO.equals(sourceName) || MyPriceConstants.SOURCE_INTERNATIONAL_ACCESS.equals(sourceName))) {
										if(methodParam.containsKey(MyPriceConstants.UNIQUEID_BEID_MAP) 
												&& null!=methodParam.get(MyPriceConstants.UNIQUEID_BEID_MAP)) {
											Map<String,String> uniqueIdBeidMap=
													(Map<String, String>) methodParam.get(MyPriceConstants.UNIQUEID_BEID_MAP);
											result.setUsocCode(uniqueIdBeidMap.get(transLineChild.getTextContent()));
										}
									}else {
										result.setUsocCode(transLineChild.getTextContent().substring(
												transLineChild.getTextContent().lastIndexOf("#")+1));
									}
								}
							}
							if (transLineChild.getNodeName().equalsIgnoreCase("bmt:lii_uSOC_ql")) {
								if(!StringUtils.isNotEmpty(sourceName) && (MyPriceConstants.SOURCE_IGLOO.equals(sourceName) || MyPriceConstants.SOURCE_INTERNATIONAL_ACCESS.equals(sourceName))) {
									result.setUsocCode(transLineChild.getTextContent());
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
		//log.info("Design Igloo Flow :: persistResponse() :: methodParam :==>> {}",methodParam.toString());
		String productName=methodParam.get(MyPriceConstants.PRODUCT_NAME)!=null?(String)
				methodParam.get(MyPriceConstants.PRODUCT_NAME):"";
		if(!(input.getLineBomPartNumber().equalsIgnoreCase("Solution") ||
				input.getParentLineItem().equalsIgnoreCase("Solution"))){
			//This flag is use to check Design transactionLine block from configDesign response is belonging to same product id whatever we r getting in request
			//This is use to find child design block for particular product
			Boolean isChildDesign=this.isProductLineIdMatchForConfigDesign(methodParam,
					input.getParentDocNumber(),productName);
			if(isChildDesign) {
				log.info("Design Igloo Flow :: persistResponse() :: isChildDesign :==>> "+isChildDesign);
				this.processDesignBlock(input,methodParam);
			}
			
		}
	}
	
	
	/**
	 * Checks if is product line itd match for config design.
	 *
	 * @param methodParam the method param
	 * @param respParentId the resp parent id
	 * @param offerName the offer name
	 * @return the boolean
	 */
	public Boolean isProductLineIdMatchForConfigDesign(Map<String,Object> methodParam,String respParentId,String productName) {
		
		String reqProductLineId=methodParam.get(MyPriceConstants.MP_PRODUCT_LINE_ID)!=null?
				(String)methodParam.get(MyPriceConstants.MP_PRODUCT_LINE_ID):"";
		
		if(productName.equalsIgnoreCase(MyPriceConstants.ETHERNET)|| productName.equalsIgnoreCase(MyPriceConstants.SOURCE_INTERNATIONAL_ACCESS)) {
			if(StringUtils.isNotEmpty(respParentId) && 
					reqProductLineId.equals(respParentId)) {
				return true;		
			}	
			return false;
		}
		return true;
	}	
	
	
	
	protected void processDesignBlock(ConfigRespProcessingBean input,Map<String,Object> methodParam) throws SalesBusinessException {
		try {
			
			Long nxDesignId=methodParam.get(MyPriceConstants.NX_ACCESS_PRICE_ID)!=null?
					(Long)methodParam.get(MyPriceConstants.NX_ACCESS_PRICE_ID):null;
			String mpSolutionId=methodParam.get(MyPriceConstants.MP_SOLUTION_ID)!=null?
									(String)methodParam.get(MyPriceConstants.MP_SOLUTION_ID):"";
			Long nxTxnId = methodParam.get(MyPriceConstants.NX_TRANSACTION_ID) != null
					? (Long) methodParam.get(MyPriceConstants.NX_TRANSACTION_ID)
					: null;
									
			if(StringUtils.isNotEmpty(mpSolutionId)) {
				List<NxMpDesignDocument>  designDocuments = nxMpDesignDocumentRepo.findByNxTxnIdAndNxDesignId(nxTxnId,nxDesignId);
				boolean newSolutionProdRow=methodParam.get(mpSolutionId)!=null?(Boolean)methodParam.get(mpSolutionId):false;
				if(CollectionUtils.isEmpty(designDocuments)) {
					StringBuffer printLog = new StringBuffer( mpSolutionId+" : if newSolutionProdRow:==>>"+newSolutionProdRow);
					log.info("logger {} ", org.apache.commons.lang3.StringUtils.normalizeSpace(printLog.toString()));
					createNewNxMpDesignDocument(input, nxDesignId, mpSolutionId,null);
				}else {
					StringBuffer printLog = new StringBuffer(mpSolutionId+" : else newSolutionProdRow:==>>"+newSolutionProdRow);
					log.info("logger {} ", org.apache.commons.lang3.StringUtils.normalizeSpace(printLog.toString()));
					nxMpDesignDocumentRepo.updateDesignBySolIdAndProductId(Long.valueOf(input.getDocumentNumber()),
							input.getUsocCode(),new Date(),mpSolutionId,
							input.getParentDocNumber(),input.getNxTransactionId());
					methodParam.put(mpSolutionId, true);
				}
				
			}
			//isSuccessful=true;	
		} catch(Exception e) {
			log.error("Exception during processDesign", e);
			throw new SalesBusinessException(e.getMessage());
		}
		
	}
	
	
	private void createNewNxMpDesignDocument(ConfigRespProcessingBean input, Long nxDesignId, 
			String mpSolutionId,String mpPartNumber) {
		//log.info("createNewEntry() : design : mpSolutionId : {} ",mpSolutionId);
		NxMpDesignDocument entity=new NxMpDesignDocument();
		entity.setNxDesignId(nxDesignId);
		entity.setMpSolutionId(mpSolutionId);
		entity.setMpProductLineId(input.getParentDocNumber());
		entity.setMpDocumentNumber(Long.valueOf(input.getDocumentNumber()));
		entity.setNxTxnId(input.getNxTransactionId());
		entity.setUsocId(input.getUsocCode());
		entity.setActiveYN(com.att.sales.nexxus.common.CommonConstants.ACTIVE_Y);
		entity.setCreatedDate(new Date());
		nxMpDesignDocumentRepo.save(entity);
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
		
	/**
	 * Gets the mp product line id by nx txn id and nx design id.
	 *
	 * @param paramMap the param map
	 * @return the mp product line id by nx txn id and nx design id
	 */
	private String getMpProductLineIdByNxTxnId(Map<String,Object> paramMap) {
		Long nxTxnId = paramMap.containsKey(MyPriceConstants.NX_TRANSACTION_ID) ? 
				(long) paramMap.get(MyPriceConstants.NX_TRANSACTION_ID) : 0L; 
		Long nxDesignId = paramMap.containsKey(MyPriceConstants.NX_ACCESS_PRICE_ID) ? 
				(long) paramMap.get(MyPriceConstants.NX_ACCESS_PRICE_ID) : 0L; 
 
		List<NxMpDesignDocument> productLineIds = nxMpDesignDocumentRepo. 
				getMpProductLineIdByNxTxnIdAndNxDesignId(nxTxnId, nxDesignId); 
		if(CollectionUtils.isNotEmpty(productLineIds)) { 
			return productLineIds.get(0).getMpProductLineId(); 
		} 
 
		return "";
	}
}
