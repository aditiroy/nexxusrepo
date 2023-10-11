package com.att.sales.nexxus.handlers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxMpConfigMapping;
import com.att.sales.nexxus.dao.model.NxMpDesignDocument;
import com.att.sales.nexxus.helper.MpProductEntity;
import com.att.sales.nexxus.myprice.transaction.dao.service.NxMpRepositoryService;
import com.att.sales.nexxus.myprice.transaction.model.ConfigRespProcessingBean;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdatePricingUtil;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdatePricingUtilIgloo;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdateProcessingInrService;
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
public class ConfigureSolnAndProductWSHandlerIgloo {
	
	private static final Logger log = LoggerFactory.getLogger(ConfigureSolnAndProductWSHandlerIgloo.class);
	

	@Autowired
	@Qualifier("configureSolnAndProductWSClientUtility")
	private SoapWSHandler configureSolnAndProductWSClientUtility;

	@Autowired
	private WSProcessingService wsProcessingService;

	@Autowired
	private NxMpRepositoryService nxMpRepositoryService;
	
	//private Boolean isSuccessful=false;
	
	@Autowired
	private ConfigAndUpdateProcessingInrService configAndUpdateService;
	
	@Autowired
	private ConfigAndUpdatePricingUtil configAndUpdatePricingUtil;
	
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
	
	public Boolean initiateConfigSolnAndProdWebService(JSONObject inputJson,Map<String, Object> requestMap)
			throws SalesBusinessException {
		log.info("Inside execute initiateConfigSolnAndProdWebService for configure solution and product","");
		Boolean isSuccessful=false;
		try {
			if(null!=inputJson) {
				ObjectFactory objectFactory = new ObjectFactory();
				ConfigureResponse configureResponse = null;
				Configure configureRequest = objectFactory.createConfigure();
				configAndUpdatePricingUtil.setProductTypeForInrFmo(requestMap);
				prepareRequestBody(requestMap, configureRequest, inputJson);
				configureSolnAndProductWSClientUtility.setWsName(MyPriceConstants.CONFIG_SOL_PRODUCT_WS);
				// -- Calling to MyPrice to get configuration details.
				configureResponse = wsProcessingService.initiateWebService(configureRequest,
						configureSolnAndProductWSClientUtility, requestMap, ConfigureResponse.class);
				if(null!=configureResponse) {
					CommonStatusType commonstatus=configureResponse.getStatus();
					if(null!=commonstatus && commonstatus.getSuccess()
							.getValue().equals("true")) {
						isSuccessful=true;
						Map<String,Set<String>> dataMap=nxMpRepositoryService.getDataByNxtxnIdInr((Long) requestMap.get("nxTxnId"));
						requestMap.put(MyPriceConstants.SOLUTION_PRODUCT_DATA, dataMap);
						this.processConfigSolAndProductResponse(configureResponse, requestMap);
					}	
					
				}	
			}
		}catch(WSClientException  we) {
			isSuccessful=false;
			log.error("Exception during configure configSolutionAndProduct call: {}", we.getFaultString());
			//this catch block catching exception only for when we trigger request to mp through soap client
			//fautString , we.getFaultString();
			requestMap.put(MyPriceConstants.RESPONSE_DATA, we.getFaultString());
		}catch (Exception e) {
			isSuccessful=false;
			log.error("Exception during configSolutionAndProduct call: {}", e.getMessage());
			//This block is used to catch exception for request preparation and response processing
			requestMap.put(MyPriceConstants.RESPONSE_MSG, e.getMessage());
		}
		
		return isSuccessful;
		
	}
	
	
	protected void prepareRequestBody(Map<String, Object> requestMap, Configure configureRequest, JSONObject inputJson) {
		ObjectFactory objectFactory = new ObjectFactory();
		ConfigurationItemType item = new ConfigurationItemType();
		item.setModel("solution");
		item.setProductLine("solution");
		item.setSegment("wireline");
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
		documents.getDocument().add(configAndUpdatePricingUtil.getSolutionFilterDocument());
				
		returnSpecificAttributesType.setDocuments(documents);
		configTransactionType.setId(requestMap.get("myPriceTransId").toString());
		configTransactionType.setProcessVarName("oraclecpqo_bmClone_2");
		configTransactionType.setDocumentVarName("transaction");
		configTransactionType.setReturnSpecificAttributes(returnSpecificAttributesType);

		JAXBElement<String> price = objectFactory.createResponseIncludesTypePrice("true");
		JAXBElement<String> spare = objectFactory.createResponseIncludesTypeSpare("true");
		JAXBElement<String> bom = objectFactory.createResponseIncludesTypeBom("true");
		JAXBElement<String> bomMapping = objectFactory.createResponseIncludesTypeBomMapping("true");
		JAXBElement<String> attributeLabel = objectFactory.createResponseIncludesTypeAttributeLabel("false");
		JAXBElement<String> previousValue = objectFactory.createResponseIncludesTypePreviousValue("false");
		JAXBElement<String> displayedValue = objectFactory.createResponseIncludesTypeDisplayedValue("false");
		JAXBElement<String> hideInTransactionAttributes = objectFactory
				.createResponseIncludesTypeHideInTransactionAttributes("false");
		JAXBElement<String> ruleDetails = objectFactory.createResponseIncludesTypeRuleDetails("true");
		JAXBElement<ConfigurationTransactionType> transactionType = objectFactory
				.createResponseIncludesTypeTransaction(configTransactionType);

		ResponseIncludesType responseIncludes = new ResponseIncludesType();
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
		configureRequest.setAttributes(this.prepareAttributesForConfigureSolutionReq(requestMap,inputJson));
		
	}
	
	protected AttributesType  prepareAttributesForConfigureSolutionReq(Map<String, Object> requestMap,
			JSONObject inputDesignDetails) {
		String designDetails=JacksonUtil.toString(inputDesignDetails);
		String offerName = Optional.ofNullable(requestMap.get(MyPriceConstants.SOURCE)).map(Object::toString).orElse("");
		List<NxMpConfigMapping> configMapping=configAndUpdatePricingUtil.
				getNxConfigMapping(requestMap,offerName,MyPriceConstants.SOLUTION_RULE);
		AttributesType attributesType = new AttributesType();
		List<Callable<Object>> callable = new ArrayList<Callable<Object>>();
		Optional.ofNullable(configMapping).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull).
		forEach( data -> {
			ConfigExecutorService<ConfigAndUpdatePricingUtilIgloo> executorService=
					new ConfigExecutorService<ConfigAndUpdatePricingUtilIgloo>(data);
			executorService.setT(configAndUpdatePricingUtilIgloo);
			executorService.setDesignData(designDetails);
			executorService.setRequestMap(requestMap);
			callable.add(executorService);
			
		});
		if(CollectionUtils.isNotEmpty(callable)) {
			ExecutorService executor=getExcutorService();
			try {
				List<Future<Object>> resultLst = executor.invokeAll(callable);
				for (Future<Object> modelData : resultLst) {
					if(null!=modelData && null!=modelData.get()){
						ModelNamePf data = (ModelNamePf)modelData.get();
						attributesType.getAttribute().add(data);
					} 
				}
				executor.shutdown();
				executor.awaitTermination(5, TimeUnit.SECONDS);
			} catch (InterruptedException |ExecutionException e) {
				log.error("Exception from callConfigSolutionProductExucuterService {}",e);
			    Thread.currentThread().interrupt();
			}finally {
				if (!executor.isTerminated()) {
					log.error("cancel non-finished tasks");
			    }
			    executor.shutdownNow();
			}
		}
		return attributesType;
	}
	
	protected List<NxMpConfigMapping> getConfigMapping(Map<String, Object> requestMap ){
		String productName = Optional.ofNullable(requestMap.get(MyPriceConstants.PRODUCT_NAME)).map(Object::toString)
				.orElse("");
		Boolean isGroupRequest= requestMap.get(MyPriceConstants.IS_GROUP_REQUEST)!=null?
				(Boolean)requestMap.get(MyPriceConstants.IS_GROUP_REQUEST):false;
		if(isGroupRequest) {
			Set<String> products=new HashSet<String>();
			products.add(MyPriceConstants.GROUP_PRODUCT);
			products.add(productName);
			return nxMpRepositoryService.findByMultipleOffersAndRuleName(products, MyPriceConstants.SOLUTION_RULE);
		}else {
			return nxMpRepositoryService.findByOfferAndRuleName(productName,
					MyPriceConstants.SOLUTION_RULE);
		}
	}
	
	


	
	protected void processConfigSolAndProductResponse(ConfigureResponse configureResponse,
			Map<String, Object> requestMap) throws SalesBusinessException {if(null!=configureResponse 
			&& null!=configureResponse.getTransaction() 
			&& null!=configureResponse.getTransaction().getValue() 
			&& null!=configureResponse.getTransaction().getValue().getDataXml() 
			&& null!=configureResponse.getTransaction().getValue().getDataXml().getAny()) {
		for (Element element : configureResponse.getTransaction().getValue().getDataXml().getAny()) {
			if(null!=element && null!=element.getElementsByTagName("bmt:transactionLine")) {
				NodeList transactionLineList = element.getElementsByTagName("bmt:transactionLine");
				for (int transLineCount = 0; transLineCount < transactionLineList.getLength(); transLineCount++) {
					Node transactionLineNode = transactionLineList.item(transLineCount);
					ConfigRespProcessingBean obj=this.createResponseBean(transactionLineNode,requestMap);
					this.persistResponse(obj,requestMap);
				}	
			}
		}
	}}
	
	/**
	 * Creates the response bean.
	 *
	 * @param transactionLineNode the transaction line node
	 * @param methodParam the method param
	 * @return the config resp processing bean
	 */
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
							if (transLineChild.getNodeName().equalsIgnoreCase("bmt:_model_name")) {
								result.setModelName(transLineChild.getTextContent());
							}
							if (transLineChild.getNodeName().equalsIgnoreCase("bmt:_model_variable_name")) {
								result.setModelVariableName(transLineChild.getTextContent());
							}
						}
						
					}
				}
				
			}
		}
		return result;
	}
	
	
	

	@SuppressWarnings("unchecked")
	public void persistResponse(ConfigRespProcessingBean input,Map<String,Object> requestMap) throws SalesBusinessException {
		try {
			//log.info("Solution Igloo Flow :: persistResponse() :: requestMap :==>> {} ",requestMap.toString());
		    String responseProductName=requestMap.get(MyPriceConstants.PRODUCT_NAME)!=null?(String)requestMap.get(MyPriceConstants.PRODUCT_NAME):"";
			//String responseProductName=this.getResponseProductName(input.getModelVariableName(),input.getModelName(),requestMap);
			Long nxTxnId=(Long) requestMap.get("nxTxnId");
			String documentNumber=input.getDocumentNumber();
			String parentDocNum=input.getParentDocNumber();
			Map<String,Set<String>> solProductIdDataMap=null!=requestMap.get(MyPriceConstants.SOLUTION_PRODUCT_DATA)?
					(Map<String, Set<String>>) requestMap.get(MyPriceConstants.SOLUTION_PRODUCT_DATA):new HashMap<String, Set<String>>();
			if (input.getLineBomId().equalsIgnoreCase("BOM_Solution")) {
				if(!solProductIdDataMap.containsKey(documentNumber)) {
					createNewEntry(documentNumber,null,requestMap);
					solProductIdDataMap.put(documentNumber, new HashSet<String>());
				}
			}else if((StringUtils.isNotEmpty(input.getParentLineItem()) && input.getParentLineItem().equalsIgnoreCase("Solution")) 
					|| (StringUtils.isNotEmpty(input.getLineBomParentId()) && input.getLineBomParentId().equalsIgnoreCase("BOM_Solution"))) {
				if(solProductIdDataMap.containsKey(parentDocNum)) {
					if(solProductIdDataMap.get(parentDocNum).add(documentNumber)){
						this.setResponseProductNo(requestMap, responseProductName,documentNumber);
						boolean isProductUpdate=nxMpRepositoryService.checkProductForUpdate(parentDocNum,nxTxnId);
						if(isProductUpdate) {
							nxMpRepositoryService.updateSolAndProductResponse(documentNumber,new Date(), parentDocNum, nxTxnId);
						}else {
							createNewEntry(parentDocNum,documentNumber,requestMap);
						}
					}
				}
			}
			requestMap.replace(MyPriceConstants.SOLUTION_PRODUCT_DATA,solProductIdDataMap);
			//isSuccessful=true;	
		}catch(Exception e) {
			log.error("Exception during processDesign", e);
			throw new SalesBusinessException(e.getMessage());
		}
		
	}
	
	@SuppressWarnings({ "unchecked" })
	protected void setResponseProductNo(Map<String, Object> requestMap,String productName,String documentNumber) {
		if(StringUtils.isNotEmpty(productName)) {
			//map holding key as productName and value as product no.
			if (!requestMap.containsKey(productName)) {
				requestMap.put(productName, new HashSet<MpProductEntity>());
			}
			MpProductEntity obj=new MpProductEntity();
			obj.setProductName(productName);
			obj.setProductDocumentNo(documentNumber);
			((HashSet<MpProductEntity>) requestMap.get(productName)).add(obj);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	protected String getResponseProductName(String modelVaribaleName,String modelName,Map<String,Object> requestMap) {
		Map<String,List<String>> productInfoMap=requestMap.get(MyPriceConstants.INR_MP_PRODUCT_INFO_DATA_MAP)!=null?
				(Map<String,List<String>>)requestMap.get(MyPriceConstants.INR_MP_PRODUCT_INFO_DATA_MAP):null;
		//deriving product name from myPrice response using	'modelVaribaleName' field	
		String productName=configAndUpdateService.getProductName(productInfoMap,modelVaribaleName);
		if(StringUtils.isNotEmpty(productName)) {
			return productName;
		}else {
			//deriving product name from myPrice response using	'modelName' field
			return configAndUpdateService.getProductName(productInfoMap,modelName);
		}
	}
	
	private void createNewEntry(String mpSolutionId,String mpProductLineId, Map<String, Object> requestMap) {
		log.info("createNewEntry() : solution : mpSolutionId : "+mpSolutionId);
		NxMpDesignDocument nxMpDesignDocument = new NxMpDesignDocument();
		nxMpDesignDocument.setNxTxnId((Long) requestMap.get(MyPriceConstants.NX_TRANSACTION_ID));
		nxMpDesignDocument.setMpSolutionId(mpSolutionId);
		nxMpDesignDocument.setMpProductLineId(mpProductLineId);
		nxMpDesignDocument.setActiveYN(com.att.sales.nexxus.common.CommonConstants.ACTIVE_Y);
		nxMpDesignDocument.setCreatedDate(new Date());
		nxMpDesignDocument.setNxDesignId((Long) requestMap.get(MyPriceConstants.NX_ACCESS_PRICE_ID));

		nxMpRepositoryService.setNxMpDesignDocument(nxMpDesignDocument);
	}

}