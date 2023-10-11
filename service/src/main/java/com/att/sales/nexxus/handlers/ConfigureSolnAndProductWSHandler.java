package com.att.sales.nexxus.handlers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
import com.att.sales.nexxus.dao.model.NxDesign;
import com.att.sales.nexxus.dao.model.NxMpConfigMapping;
import com.att.sales.nexxus.dao.model.NxMpDesignDocument;
import com.att.sales.nexxus.dao.repository.NxDesignRepository;
import com.att.sales.nexxus.myprice.transaction.dao.service.NxMpRepositoryService;
import com.att.sales.nexxus.myprice.transaction.model.ConfigureSolnAndProductResponse;
import com.att.sales.nexxus.myprice.transaction.model.TransactionLine;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdatePricingUtil;
import com.att.sales.nexxus.ws.utility.SoapWSHandler;
import com.att.sales.nexxus.ws.utility.WSClientException;
import com.att.sales.nexxus.ws.utility.WSProcessingService;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.AttributesType;
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

/**
 * @author KumariMuktta
 *
 */
@Component
public class ConfigureSolnAndProductWSHandler  {

	private static final Logger log = LoggerFactory.getLogger(ConfigureSolnAndProductWSHandler.class);

	@Autowired
	@Qualifier("configureSolnAndProductWSClientUtility")
	private SoapWSHandler configureSolnAndProductWSClientUtility;

	@Autowired
	private WSProcessingService wsProcessingService;

	@Autowired
	private NxMpRepositoryService nxMpRepositoryService;
	
	@Autowired
	private NxDesignRepository nxDesignRepository;

	@Autowired
	private ConfigAndUpdatePricingUtil configAndUpdatePricingUtil;
	
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

	public ConfigureSolnAndProductResponse initiateConfigSolnAndProdWebService(Map<String, Object> requestMap)
			throws SalesBusinessException {
		ConfigureSolnAndProductResponse response = null;
		ObjectFactory objectFactory = new ObjectFactory();
		ConfigureResponse configureResponse = null;
		Configure configureRequest = objectFactory.createConfigure();
		try {
			Long nxDesignId=(Long)requestMap.get(MyPriceConstants.NX_DESIGN_ID);
			NxDesign nxDesign = nxDesignRepository.findByNxDesignId(nxDesignId);

			if (null != nxDesign && CollectionUtils.isNotEmpty(nxDesign.getNxDesignDetails())
					&& null != nxDesign.getNxDesignDetails().get(0)
					&& StringUtils.isNotEmpty(nxDesign.getNxDesignDetails().get(0).getDesignData())) {
				requestMap.put(MyPriceConstants.NX_DESIGN_ID, nxDesign.getNxDesignId());
				String offerName = requestMap.get(MyPriceConstants.OFFER_NAME) != null
						? (String) requestMap.get(MyPriceConstants.OFFER_NAME)
						: "";
				JSONObject designDetails = configAndUpdatePricingUtil.getInputDesignDetails(nxDesign, offerName);
				if (null != designDetails) {
					configAndUpdatePricingUtil.setProductTypeForInrFmo(requestMap);
					prepareRequestBody(requestMap, configureRequest, designDetails);

					// -- Calling to MyPrice to get configuration details.
					configureSolnAndProductWSClientUtility.setWsName(MyPriceConstants.CONFIG_SOL_PRODUCT_WS);
					configureResponse = wsProcessingService.initiateWebService(configureRequest,
							configureSolnAndProductWSClientUtility, requestMap, ConfigureResponse.class);

					if (configureResponse != null) {
						Map<String,String> dataMap=nxMpRepositoryService.getDataByNxtxnId((Long) requestMap.get("nxTxnId"));
						requestMap.put(MyPriceConstants.SOLUTION_PRODUCT_DATA, dataMap);
						response = processAndPersistResponse(configureResponse, requestMap);
					}
				}
			}
		}catch(WSClientException  we) {
			log.error("Exception during configure configSolutionAndProduct call: {}", we.getFaultString());
			//this catch block catching exception only for when we trigger request to mp through soap client
			//fautString , we.getFaultString();
			requestMap.put(MyPriceConstants.RESPONSE_DATA, we.toString());
		}catch (Exception e) {
			log.error("Exception during configSolutionAndProduct call: {}", e.getMessage());
			//This block is used to catch exception for request preparation and response processing
			requestMap.put(MyPriceConstants.RESPONSE_MSG, e.getMessage());
		}
		return response;
	}

	private void prepareRequestBody(Map<String, Object> requestMap, Configure configureRequest, JSONObject site) {

		configureRequest.setItem(prepareItemForConfigureSolutionReq());
		configureRequest.setAttributes(prepareAttributesForConfigureSolutionReq(requestMap, site));
		configureRequest.setResponseIncludes(prepareResponseInculdesForConfigureSolutionReq(requestMap));
	}

	private ConfigureSolnAndProductResponse processAndPersistResponse(ConfigureResponse configureResponse,
			Map<String, Object> requestMap) throws SalesBusinessException {

		ConfigureSolnAndProductResponse response = new ConfigureSolnAndProductResponse();
		for (Element element : configureResponse.getTransaction().getValue().getDataXml().getAny()) {
			NodeList transactionList = element.getChildNodes();
			for (int transCount = 0; transCount < transactionList.getLength(); transCount++) {
				Node transactionNode = transactionList.item(transCount);
				if (transactionNode.getNodeName().equals("bmt:sub_documents")) {
					NodeList subDocumentsChildList = transactionNode.getChildNodes();
					for (int subDocumentsChildCount = 0; subDocumentsChildCount < subDocumentsChildList
							.getLength(); subDocumentsChildCount++) {
						Node subDocumentsChildNode = subDocumentsChildList.item(subDocumentsChildCount);
						if (subDocumentsChildNode.getNodeName().equals("bmt:transactionLine")) {
							TransactionLine transactionLine = new TransactionLine();
							String lineBomIdValue = null;
							String documentNumber = null;
							String prodLineId = null;
							String parentDocNum = null;
							NodeList transLineChildList = subDocumentsChildNode.getChildNodes();
							for (int transLineCount = 0; transLineCount < transLineChildList
									.getLength(); transLineCount++) {
								Node transLineChild = transLineChildList.item(transLineCount);
								if (transLineChild.getNodeName().equalsIgnoreCase("bmt:_line_bom_id")) {
									lineBomIdValue = transLineChild.getTextContent();
									transactionLine.setMpSolutionId(lineBomIdValue);
								}
								if (transLineChild.getNodeName().equalsIgnoreCase("bmt:_document_number")) {
									documentNumber = transLineChild.getTextContent();
									transactionLine.setDocumentNumber(Long.parseLong(documentNumber));
								}
								if (transLineChild.getNodeName().equalsIgnoreCase("bmt:_model_product_line_id")) {
									prodLineId = transLineChild.getTextContent();
									transactionLine.setMpProductLineId(prodLineId);
								}
								if (transLineChild.getNodeName().equalsIgnoreCase("bmt:_parent_doc_number")) {
									parentDocNum = transLineChild.getTextContent();
								}
							}
							response.addTransactionLines(transactionLine);

							this.processSolutionAndProductResponse(requestMap, lineBomIdValue, documentNumber,
									parentDocNum);

						}
					}
				}
			}
		}
		return response;
	}
	
	/*public void processSolutionAndProductResponseOld(Map<String, Object> requestMap, String lineBomIdValue,
			String documentNumber, String parentDocNum) {
		if (lineBomIdValue.equalsIgnoreCase("BOM_Solution")) {
			Long recordCount = nxMpRepositoryService.getCountNxMpDesignDocument((Long) requestMap.get("nxTxnId"),
					Long.parseLong(documentNumber));
			if (recordCount == 0) {
				saveConfigureResponse(documentNumber, requestMap);
			}
		} else if (lineBomIdValue.equalsIgnoreCase("BOM_SolutionASE") || lineBomIdValue.equalsIgnoreCase("BOM_ASE")
				|| lineBomIdValue.equalsIgnoreCase("BOM_SolutionADE") || lineBomIdValue.equalsIgnoreCase("BOM_ADE")
				|| lineBomIdValue.equalsIgnoreCase("BOM_SolutionASENOD") || lineBomIdValue.equalsIgnoreCase("BOM_ASENOD")) {

			List<NxMpDesignDocument> recordList = nxMpRepositoryService.getNxMpDesignDocument((Long) requestMap.get("nxTxnId"),
					Long.parseLong(documentNumber), parentDocNum);
			if (recordList == null || recordList.isEmpty()) {
				NxMpDesignDocument nxMpDesignDocument = nxMpRepositoryService
						.findByNxTxnIdAndMpSolutionId((Long) requestMap.get("nxTxnId"), parentDocNum);
				updateConfigureResponse(nxMpDesignDocument, documentNumber);
			}
		}
	}*/


	@SuppressWarnings("unchecked")
	public void processSolutionAndProductResponse(Map<String, Object> requestMap, String lineBomIdValue,
			String documentNumber, String parentDocNum) {
		Long nxTxnId=(Long) requestMap.get("nxTxnId");
		Map<String,String> solProductIdDataMap=null!=requestMap.get(MyPriceConstants.SOLUTION_PRODUCT_DATA)?
				(Map<String, String>) requestMap.get(MyPriceConstants.SOLUTION_PRODUCT_DATA):new HashMap<String, String>();
				if (lineBomIdValue.equalsIgnoreCase("BOM_Solution")) {
					if(!solProductIdDataMap.containsKey(documentNumber)) {
						saveConfigureResponse(documentNumber, requestMap);
					}
				} else if (lineBomIdValue.equalsIgnoreCase("BOM_SolutionASE") || lineBomIdValue.equalsIgnoreCase("BOM_ASE")
						|| lineBomIdValue.equalsIgnoreCase("BOM_SolutionADE") || lineBomIdValue.equalsIgnoreCase("BOM_ADE")
						|| lineBomIdValue.equalsIgnoreCase("BOM_SolutionASENOD") || lineBomIdValue.equalsIgnoreCase("BOM_ASENOD")) {

					String productId=solProductIdDataMap.get(parentDocNum)!=null?solProductIdDataMap.get(parentDocNum):
						null;
					if(StringUtils.isEmpty(productId)) {
						nxMpRepositoryService.updateSolAndProductResponse(documentNumber, new Date(), parentDocNum, nxTxnId);
					}
				}

	}
	

	private void saveConfigureResponse(String documentNumber, Map<String, Object> requestMap) {
		NxMpDesignDocument nxMpDesignDocument = new NxMpDesignDocument();
		nxMpDesignDocument.setNxTxnId((Long) requestMap.get(MyPriceConstants.NX_TRANSACTION_ID));
		nxMpDesignDocument.setMpSolutionId(documentNumber);
		nxMpDesignDocument.setActiveYN(com.att.sales.nexxus.common.CommonConstants.ACTIVE_Y);
		nxMpDesignDocument.setCreatedDate(new Date());
		nxMpDesignDocument.setNxDesignId((Long) requestMap.get(MyPriceConstants.NX_DESIGN_ID));

		nxMpRepositoryService.setNxMpDesignDocument(nxMpDesignDocument);
	}

	/*private void updateConfigureResponse(NxMpDesignDocument nxMpDesignDocument, String documentNumber) {
		nxMpDesignDocument.setMpProductLineId(documentNumber);
		nxMpDesignDocument.setModifiedDate(new Date());
		nxMpRepositoryService.updateNxMpDesignDocument(nxMpDesignDocument);
	}*/

	private ConfigurationItemType prepareItemForConfigureSolutionReq() {
		ConfigurationItemType item = new ConfigurationItemType();
		item.setModel("solution");
		item.setProductLine("solution");
		item.setSegment("wireline");
		return item;
	}

	/*private AttributesType prepareAttributesForConfigureSolutionReq(Map<String, Object> requestMap, JSONObject site) {

		String offerName = Optional.ofNullable(requestMap.get(MyPriceConstants.OFFER_NAME)).map(Object::toString)
				.orElse("");
		List<NxMpConfigMapping> configMapping = nxMpRepositoryService.findByOfferAndRuleName(offerName,
				MyPriceConstants.SOLUTION_RULE);
		AttributesType attributesType = new AttributesType();
		Optional.ofNullable(configMapping).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull)
				.forEach(data -> {
					JAXBElement<String> value = new ObjectFactory()
							.createModelPathPmValue(configAndUpdatePricingUtil.getData(data, site));
					ModelPathPm obj = new ModelPathPm();
					obj.setVariableName(data.getVariableName());
					obj.setValue(value);
					attributesType.getAttribute().add(obj);

				});
		return attributesType;
	}*/
	
	
	protected AttributesType  prepareAttributesForConfigureSolutionReq(Map<String, Object> requestMap,
			JSONObject inputDesignDetails) {
		String offerName = Optional.ofNullable(requestMap.get(MyPriceConstants.OFFER_NAME)).map(Object::toString)
				.orElse("");
		List<NxMpConfigMapping> configMapping=configAndUpdatePricingUtil.
				getNxConfigMapping(requestMap,offerName,MyPriceConstants.SOLUTION_RULE);
		AttributesType attributesType = new AttributesType();
		List<Callable<Object>> callable = new ArrayList<Callable<Object>>();
		Optional.ofNullable(configMapping).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull).
		forEach( data -> {
			ConfigExecutorService<ConfigAndUpdatePricingUtil> executorService=new ConfigExecutorService<ConfigAndUpdatePricingUtil>(data);
			executorService.setT(configAndUpdatePricingUtil);
			executorService.setInputDesignDetails(inputDesignDetails);
			executorService.setRequestMap(requestMap);
			callable.add(executorService);
			
		});
		if(CollectionUtils.isNotEmpty(callable)) {
			ExecutorService executor=getExcutorService();
			try {
				List<Future<Object>> resultLst = executor.invokeAll(callable);
				for (Future<Object> modelData : resultLst) {
					if(null!=modelData && null!=modelData.get()){
						ModelNamePf site = (ModelNamePf)modelData.get();
						attributesType.getAttribute().add(site);
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

	private ResponseIncludesType prepareResponseInculdesForConfigureSolutionReq(Map<String, Object> requestMap) {
		ObjectFactory objectFactory = new ObjectFactory();
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
		return responseIncludes;
	}
}
