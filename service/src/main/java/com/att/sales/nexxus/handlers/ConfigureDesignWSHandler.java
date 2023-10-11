package com.att.sales.nexxus.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.JAXBElement;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
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
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxDesign;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxMpConfigMapping;
import com.att.sales.nexxus.dao.model.NxMpDesignDocument;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxDesignRepository;
import com.att.sales.nexxus.dao.repository.NxLineItemProcessingDao;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxMpDesignDocumentRepository;
import com.att.sales.nexxus.dao.repository.NxMpPriceDetailsRepository;
import com.att.sales.nexxus.myprice.transaction.dao.service.NxMpRepositoryService;
import com.att.sales.nexxus.myprice.transaction.model.ConfigRespProcessingBean;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdatePricingUtil;
import com.att.sales.nexxus.myprice.transaction.service.RestCommonUtil;
import com.att.sales.nexxus.reteriveicb.model.ComponentDetails;
import com.att.sales.nexxus.reteriveicb.model.PriceAttributes;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.att.sales.nexxus.ws.utility.SoapWSHandler;
import com.att.sales.nexxus.ws.utility.WSClientException;
import com.att.sales.nexxus.ws.utility.WSProcessingService;
import com.jayway.jsonpath.TypeRef;
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
 * The Class ConfigureDesignWSHandler.
 */
@Component
public class ConfigureDesignWSHandler {
	
	private static final Logger log = LoggerFactory.getLogger(ConfigureDesignWSHandler.class);
	@Autowired
	@Qualifier("configureSolnAndProductWSClientUtility")
	private SoapWSHandler configureWSClientUtility;

	@Autowired
	private WSProcessingService wsProcessingService;
	
	@Autowired
	private NxMpDesignDocumentRepository nxMpDesignDocumentRepo;
	
	@Autowired
	private NexxusJsonUtility nexxusJsonUtility;
	
	/** The line item processing dao. */
	@Autowired
	private NxLineItemProcessingDao lineItemProcessingDao;
	
	@Autowired
	private JsonPathUtil jsonPathUtil;
	
	@Autowired
	private NxDesignAuditRepository nxDesignAuditRepository;
	
	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;
	private Boolean isSuccessful=false;
	
	@Autowired
	private ConfigureSolnAndProductWSHandler configureSolnAndProductWSHandler;
	
	@Autowired
	private ConfigAndUpdatePricingUtil configAndUpdatePricingUtil;
	
	@Autowired
	private NxMpPriceDetailsRepository priceDetailsRepo;
	
	@Autowired
	private NxDesignRepository nxDesignRepository;
	
	@Autowired
	private NxMpRepositoryService nxMpRepositoryService;
	
	@Autowired
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	@Autowired
	private RestCommonUtil restCommonUtil;
	
	private static final String STATE_PATH="$..state";
	
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
	public Boolean initiateConfigDesignWebService(Map<String, Object> requestMap) throws  SalesBusinessException{
		log.info("Inside execute initiateConfigDesignWebService for configure Design","");
		isSuccessful=false;
		try {
			
			Long nxDesignId=(Long)requestMap.get(MyPriceConstants.NX_DESIGN_ID);
			NxDesign nxDesign = nxDesignRepository.findByNxDesignId(nxDesignId);
			
			if(null!=nxDesign && CollectionUtils.isNotEmpty(nxDesign.getNxDesignDetails()) 
					&& null!=nxDesign.getNxDesignDetails().get(0) 
					&& StringUtils.isNotEmpty(nxDesign.getNxDesignDetails().get(0).getDesignData())) {
				String mpProductLineId=getMpProductLineIdByNxTxnIdAndNxDesignId(requestMap);
				requestMap.put(MyPriceConstants.MP_PRODUCT_LINE_ID,mpProductLineId);
				String offerName=requestMap.get(MyPriceConstants.OFFER_NAME)!=null?(String)
						requestMap.get(MyPriceConstants.OFFER_NAME):"";
				JSONObject designDetails=this.getInputDesignDetails(nxDesign, offerName,requestMap);
				if(null!=designDetails) {
					this.setProductType(offerName, designDetails, requestMap);
					configAndUpdatePricingUtil.collectCircuitDetailsForAde(designDetails, offerName, requestMap);
					ObjectFactory objectFactory = new ObjectFactory();
					ConfigureResponse configureResponse = null;
					Configure configureRequest = objectFactory.createConfigure();
					prepareRequestBody(requestMap, configureRequest, designDetails);
					// -- Calling to MyPrice to get configuration details.
					configureWSClientUtility.setWsName(MyPriceConstants.CONFIG_DESIGN_WS);
					configureResponse = wsProcessingService.initiateWebService(configureRequest,
							configureWSClientUtility, requestMap, ConfigureResponse.class);
					if(null!=configureResponse) {
						requestMap.put(MyPriceConstants.INPUT_JSON,designDetails);
						this.processConfigResponse(configureResponse, requestMap);
					}
				}
			}
		}catch(WSClientException  we) {
			log.error("Exception during configure Design call: {}", we.getFaultString());
			//this catch block catching exception only for when we trigger request to mp through soap client
			//fautString , we.getFaultString();
			requestMap.put(MyPriceConstants.RESPONSE_DATA, we.toString());
		} catch(Exception  e) {
			log.error("Exception during configure Design call: {}", e);
			//This block is used to catch exception for request preparation and response processing
			requestMap.put(MyPriceConstants.RESPONSE_MSG, e.getMessage());
		}
		
		return isSuccessful;
		
	}
	
	
	protected void setProductType(String offerName,JSONObject designDetails,Map<String, Object> requestMap) {
		if(StringConstants.OFFERNAME_ASENOD.equalsIgnoreCase(offerName)) {
			String thirdPartyInd=configAndUpdatePricingUtil.getDataInString(designDetails,MyPriceConstants.THIRD_PARTY_IND_PATH);
			if(StringUtils.isNotEmpty(thirdPartyInd) && thirdPartyInd.equalsIgnoreCase("Y")) {
				requestMap.put(MyPriceConstants.PRODUCT_TYPE, MyPriceConstants.ASENOD_3PA);
			}else {
				requestMap.put(MyPriceConstants.PRODUCT_TYPE, MyPriceConstants.ASENOD_IR);
			}
		}
	}
	
	
	
	
	
	/**
	 * Gets the input design details.
	 *
	 * @param nxDesign the nx design
	 * @param offerName the offer name
	 * @return the input design details
	 */
	protected JSONObject getInputDesignDetails(NxDesign nxDesign,String offerName,Map<String, Object> requestMap) {
		JSONObject designDetails=JacksonUtil.toJsonObject(nxDesign.getNxDesignDetails().get(0).getDesignData());
		if(null!=designDetails) {
			this.setNexxusProductSubType(designDetails, offerName, requestMap);
			if(offerName.equals(MyPriceConstants.ASE_OFFER_NAME) ||StringConstants.OFFERNAME_ASENOD.equalsIgnoreCase(offerName)) {
				return this.mergeSolutionAndDesignDataASE(nxDesign, designDetails);
			}else if(offerName.equals(MyPriceConstants.ADE_OFFER_NAME)) {
				return this.mergeSolutionAndDesignDataADE(nxDesign, designDetails);
			}else {
				return designDetails;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	protected void setNexxusProductSubType(JSONObject designDetails,String offerName,Map<String, Object> requestMap) {
		boolean automationInd=requestMap.get(MyPriceConstants.AUTOMATION_IND)!=null?
				(boolean)requestMap.get(MyPriceConstants.AUTOMATION_IND):false;
		String nxProdSubTypeVal=null;		
		if(automationInd) {
			//setting nxProductSubType for automation flow indicator
			NxLookupData prodSubTypeLookupData=nxLookupDataRepository.findTopByDatasetNameAndItemId
					(MyPriceConstants.AUTOMATION_PRODUCT_SUB_TYPE,offerName);
			if(null!=prodSubTypeLookupData && StringUtils.isNotEmpty(prodSubTypeLookupData.getDescription()));{
				nxProdSubTypeVal=prodSubTypeLookupData.getDescription();
			}
			designDetails.put("nxProductSubType",nxProdSubTypeVal);
		}
		
	}
	
	/**
	 * Merge solution and design data ASE.
	 *
	 * @param nxDesign the nx design
	 * @param designDetails the design details
	 * @return the JSON object
	 */
	protected JSONObject mergeSolutionAndDesignDataASE(NxDesign nxDesign,JSONObject designDetails) {
		JSONObject solutionData=getSolutionData(nxDesign);
		if(null!=solutionData) {
			String path1 = "$..solution.offers.*.site[0]";
			String json = jsonPathUtil.set(solutionData, path1,designDetails, true);
			return JacksonUtil.toJsonObject(json);
		}
		return designDetails;
	}
	
	/**
	 * Merge solution and design data ADE.
	 *
	 * @param nxDesign the nx design
	 * @param designDetails the design details
	 * @return the JSON object
	 */
	protected JSONObject mergeSolutionAndDesignDataADE(NxDesign nxDesign,JSONObject designDetails) {
		JSONObject solutionData=getSolutionData(nxDesign);
		if(null!=solutionData) {
			//Replace Site array with Circuit array
			String modifiedSolutionData = jsonPathUtil.delete(solutionData, "$..solution.offers.*.site",true);
			modifiedSolutionData = jsonPathUtil.put(JacksonUtil.toJsonObject(modifiedSolutionData), 
					"$..solution.offers.*","circuit",new JSONArray(), true);
			//Merge Design data with solution data
			modifiedSolutionData = jsonPathUtil.set(JacksonUtil.toJsonObject(modifiedSolutionData), 
					"$..solution.offers.*.circuit[0]",designDetails, true);
			return JacksonUtil.toJsonObject(modifiedSolutionData);
		}
		return designDetails;
	}
	
	
	/**
	 * Gets the solution data.
	 *
	 * @param nxDesign the nx design
	 * @return the solution data
	 */
	protected JSONObject getSolutionData(NxDesign nxDesign) {
		JSONObject solutionData=null;
		NxDesignAudit nxDesignAudit=nxDesignAuditRepository.findByNxRefIdAndTransaction(
				nxDesign.getNxSolutionDetail().getNxSolutionId(),CommonConstants.SOLUTION_DATA);
		if(null!=nxDesignAudit) {
			solutionData=JacksonUtil.toJsonObject(nxDesignAudit.getData());
			if(null!=solutionData) {
				//convert dpp marketStrata value to Nexxus format
				solutionData=this.handleMarketStrataValue(solutionData);
			}
		}
		return solutionData;
	}
	
	
	/**
	 * Handle market strata value.
	 *
	 * @param input the input
	 * @return the JSON object
	 */
	protected JSONObject handleMarketStrataValue(JSONObject input) {
		Object erateInd=nexxusJsonUtility.getValue(input, "$..erateInd");
		if(null!=erateInd && "Y".equals(String.valueOf(erateInd))){
			//code for setting productSubType_pf value in configDesign request for ASE and ASENOD in Erate scenario
			String value="SLED";
			String json = jsonPathUtil.set(input, "$..marketStrata",value, true);
			return JacksonUtil.toJsonObject(json);
		}else {
			Object marketStrataObj=nexxusJsonUtility.getValue(input, "$..marketStrata");
			if(null!=marketStrataObj) {
				String dppValue=String.valueOf(marketStrataObj);
				if(StringUtils.isNotEmpty(dppValue)) {
					NxLookupData nxLookup=nxLookupDataRepository.findTopByDatasetNameAndItemId("mp_marketStrata", dppValue);
					if(null!=nxLookup && StringUtils.isNotEmpty(nxLookup.getDescription())) {
						String json = jsonPathUtil.set(input, "$..marketStrata",nxLookup.getDescription(), true);
						return JacksonUtil.toJsonObject(json);
					}
				}
			}
		}
		
		return input;
	}
	
	
	/**
	 * Prepare request body.
	 *
	 * @param requestMap the request map
	 * @param configureRequest the configure request
	 * @param inputDesignDetails the input design details
	 */
	protected void prepareRequestBody(Map<String, Object> requestMap, Configure configureRequest, JSONObject inputDesignDetails) {
		String offerName=requestMap.get(MyPriceConstants.OFFER_NAME)!=null?
				(String)requestMap.get(MyPriceConstants.OFFER_NAME):"";
		if(StringConstants.OFFERNAME_ASENOD.equalsIgnoreCase(offerName)) {
			offerName = StringConstants.OFFERNAME_ASE;
		}
		ObjectFactory objectFactory = new ObjectFactory();
		ConfigurationItemType item = new ConfigurationItemType();
		item.setModel(offerName);
		item.setProductLine("telco");
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
	
	
	
	protected AttributesType preapareAttributesForConfigDesign(Map<String, Object> requestMap,
			JSONObject inputDesignDetails) {
		String offerName = requestMap.get(MyPriceConstants.OFFER_NAME) != null
				? (String) requestMap.get(MyPriceConstants.OFFER_NAME)
				: "";
		AttributesType attributesType = new AttributesType();
		// This code is use to dynamically set attributes data using
		List<NxMpConfigMapping> configMapping=
				configAndUpdatePricingUtil.getNxConfigMapping(requestMap, offerName,MyPriceConstants.DESIGN_RULE);
		this.derivedDataForRequestUsocFields(inputDesignDetails, requestMap);
		
		List<Callable<Object>> callable = new ArrayList<Callable<Object>>();
		Optional.ofNullable(configMapping).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull)
				.forEach(data -> {
					ConfigExecutorService<ConfigureDesignWSHandler> executorService = new ConfigExecutorService<ConfigureDesignWSHandler>(
							data);
					executorService.setT(this);
					executorService.setInputDesignDetails(inputDesignDetails);
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected String getData(NxMpConfigMapping mappingData,JSONObject inputDesignDetails,Map<String, Object> requestMap) {
		Long offerId=MyPriceConstants.OFFER_NAME_OFFER_ID_MAP.get(mappingData.getOffer());
		String result=null;
		if(StringUtils.isNotEmpty(mappingData.getType()) && 
				mappingData.getType().equals(MyPriceConstants.IS_DEFAULT)) {
			result= mappingData.getDefaultValue();
		}else if(StringUtils.isNotEmpty(mappingData.getType()) && 
				mappingData.getType().equals("Custome")) {
			result= this.customeCodeProcessing(mappingData, inputDesignDetails,requestMap);
		}else if(StringUtils.isNotEmpty(mappingData.getPath())){
			if(StringUtils.isNotEmpty(mappingData.getType()) && mappingData.getType().equals("List")) {
				result= this.processListResult(mappingData, inputDesignDetails, offerId);
			}else if(StringUtils.isNotEmpty(mappingData.getType()) && mappingData.getType().equals("Count")) {
				result= this.getResultCount(mappingData, inputDesignDetails);
			}else if(mappingData.getPath().contains(MyPriceConstants.OR_CONDITION_SEPERATOR)){
				List<String> pathList= new ArrayList(Arrays.asList(mappingData.getPath().split(
						Pattern.quote(MyPriceConstants.OR_CONDITION_SEPERATOR))));
				result= processOrCondition(mappingData, inputDesignDetails, offerId, pathList);
			}else if(mappingData.getPath().contains(MyPriceConstants.MULTI_JSON_PATH_SEPERATOR)){
				List<String> pathList= new ArrayList(Arrays.asList(mappingData.getPath().split(MyPriceConstants.MULTI_JSON_PATH_SEPERATOR)));
				result= processMultipleJsonPath(mappingData,inputDesignDetails,offerId,pathList,mappingData.getDelimiter());
			}else {
				result=this.getItemValueUsingJsonPath(mappingData.getPath(), inputDesignDetails);
				if(StringUtils.isNotEmpty(mappingData.getDataSetName())) {
					result= this.processDataSetName(result, mappingData, offerId);
				}
			}
		}
		if(StringUtils.isEmpty(result)) {
			result=mappingData.getDefaultValue();
		}
		return result;
	}
	
	protected String getResultCount(NxMpConfigMapping mappingData,JSONObject inputDesignDetails) {
		TypeRef<List<Object>> ref = new TypeRef<List<Object>>() {};
		if(mappingData.getPath().contains(MyPriceConstants.MULTI_JSON_PATH_SEPERATOR)){
			int i=0;
			List<String> pathList= new ArrayList<String>(Arrays.asList(mappingData.getPath().split(
					MyPriceConstants.MULTI_JSON_PATH_SEPERATOR)));
			for(String path:pathList) {
				List<Object> dataLst=jsonPathUtil.search(inputDesignDetails,path,ref);
				if(CollectionUtils.isNotEmpty(dataLst)) {
					i=dataLst.size();
				}else {
					continue;
				}
			}
			return i>0?String.valueOf(i):null;
		}else {
			List<Object> dataLst=jsonPathUtil.search(inputDesignDetails, mappingData.getPath(), ref);
			if(CollectionUtils.isNotEmpty(dataLst)) {
				return String.valueOf(dataLst.size());
			}
		}
		
		
		return null;
	}
	
	
	/**
	 * Handle code manually.
	 *
	 * @param mappingData the mapping data
	 * @param inputDesignDetails the input design details
	 * @return the string
	 */
	protected String customeCodeProcessing(NxMpConfigMapping mappingData,JSONObject inputDesignDetails,
			Map<String, Object> requestMap) {
		String result=null;
		if(StringUtils.isNotEmpty(mappingData.getVariableName())){
			if(mappingData.getVariableName().equalsIgnoreCase(MyPriceConstants.ASE_USOC_PF) ||
					mappingData.getVariableName().equalsIgnoreCase(MyPriceConstants.ADE_USOC_PF) ||
					mappingData.getVariableName().equalsIgnoreCase(MyPriceConstants.ASENOD_USOC_PF)) {
				result = requestMap.get(MyPriceConstants.USOC_VALUE)!=null?requestMap.get(MyPriceConstants.USOC_VALUE).toString():null;
			}else if(mappingData.getVariableName().equalsIgnoreCase(MyPriceConstants.ASE_TELCO_BILLING_ARRAY) ||
					mappingData.getVariableName().equalsIgnoreCase(MyPriceConstants.ADE_TELCO_BILLING_ARRAY) || 
					mappingData.getVariableName().equalsIgnoreCase(MyPriceConstants.ASENOD_TELCO_BILLING_ARRAY)) {
				result = requestMap.get(MyPriceConstants.TELCO_BILLING_ARRAY)!=null?
						requestMap.get(MyPriceConstants.TELCO_BILLING_ARRAY).toString():null;
			}else if(mappingData.getVariableName().equalsIgnoreCase(MyPriceConstants.ASE_NEW_PF) ||
					mappingData.getVariableName().equalsIgnoreCase(MyPriceConstants.ADE_NEW_PF)) {
				result = requestMap.get(MyPriceConstants.NEW)!=null?
						requestMap.get(MyPriceConstants.NEW).toString():null;
			}else if(mappingData.getVariableName().equalsIgnoreCase(MyPriceConstants.ASE_EXISTING_PF) ||
					mappingData.getVariableName().equalsIgnoreCase(MyPriceConstants.ADE_EXISTING_PF)) {
				result = requestMap.get(MyPriceConstants.EXISTING)!=null?
						requestMap.get(MyPriceConstants.EXISTING).toString():null;
			}else if(mappingData.getVariableName().equalsIgnoreCase(MyPriceConstants.ASE_MIGRATION_PF) ||
					mappingData.getVariableName().equalsIgnoreCase(MyPriceConstants.ADE_MIGRATION_PF)) {
				result = requestMap.get(MyPriceConstants.MIGRATION)!=null?
						requestMap.get(MyPriceConstants.MIGRATION).toString():null;
			}else if(mappingData.getVariableName().equalsIgnoreCase(MyPriceConstants.DIVERSITY_SERVICE)) {
				result = getDiversityValue(mappingData, inputDesignDetails);
			}else if(mappingData.getVariableName().equalsIgnoreCase(MyPriceConstants.JURISDICTION) &&
					(mappingData.getOffer().equals(MyPriceConstants.ASE_OFFER_NAME) || 
							StringConstants.OFFERNAME_ASENOD.equalsIgnoreCase(mappingData.getOffer())) ) {
				result = processJurisdictionASE(mappingData, inputDesignDetails);
			}else if(mappingData.getVariableName().equalsIgnoreCase(MyPriceConstants.JURISDICTION) &&
					mappingData.getOffer().equals(MyPriceConstants.ADE_OFFER_NAME)) {
				result = processJurisdictionADE(mappingData, inputDesignDetails);
			}else if(mappingData.getVariableName().equalsIgnoreCase(MyPriceConstants.CIR_QUANTITY) &&
					mappingData.getOffer().equals(MyPriceConstants.ASE_OFFER_NAME)) {
				result = requestMap.get(MyPriceConstants.CIR_QUANTITY)!=null?
						requestMap.get(MyPriceConstants.CIR_QUANTITY).toString():null;
			}else if(mappingData.getVariableName().equalsIgnoreCase(MyPriceConstants.PORT_QUANTITY) &&
					mappingData.getOffer().equals(MyPriceConstants.ASE_OFFER_NAME)) {
				result = requestMap.get(MyPriceConstants.PORT_QUANTITY)!=null?
						requestMap.get(MyPriceConstants.PORT_QUANTITY).toString():null;
			}
		}
		return result;
	}
	
	
	/**
	 * Gets the diversity value.
	 *
	 * @param mappingData the mapping data
	 * @param inputDesignDetails the input design details
	 * @return the diversity value
	 */
	protected String getDiversityValue(NxMpConfigMapping mappingData, JSONObject inputDesignDetails) {
		String result=MyPriceConstants.NO;
		if(StringUtils.isNotEmpty(mappingData.getOffer()) &&
				mappingData.getOffer().equals(MyPriceConstants.ADE_OFFER_NAME)){
			result=MyPriceConstants.NO.concat(mappingData.getDelimiter()).concat(MyPriceConstants.NO);
		}
		boolean isExists=nexxusJsonUtility.isExists(inputDesignDetails, mappingData.getPath());
		if(isExists) {
			result=MyPriceConstants.YES;
			if(StringUtils.isNotEmpty(mappingData.getOffer()) &&
					mappingData.getOffer().equals(MyPriceConstants.ADE_OFFER_NAME)){
				result=MyPriceConstants.YES.concat(mappingData.getDelimiter()).concat(MyPriceConstants.YES);
			}
		}
		return result;
	}

	

	/**
	 * Creates the path.
	 *
	 * @param key the key
	 * @param datasetName the dataset name
	 * @param operator the operator
	 * @param condition the condition
	 * @return the string
	 */
	protected String createPathFromNxLookUp(String key,String datasetName,String operator,String condition) {
		List<NxLookupData> rateGroupObj=nxLookupDataRepository.findByDatasetName(datasetName);
		List<String> dataLst=Optional.ofNullable(rateGroupObj).map(List::stream).orElse(Stream.empty()).
		filter(Objects::nonNull).map(NxLookupData::getItemId).collect(Collectors.toList());
		StringBuilder sb = new StringBuilder();
		if(CollectionUtils.isNotEmpty(dataLst)) {
			for(String itemValue:dataLst) {
				if(sb.length()>0 && StringUtils.isNotEmpty(condition)) {
					 sb.append(condition);
				}
				if(StringUtils.isNotEmpty(itemValue)) {
					sb.append("@."+key+operator+"'"+itemValue+"'");
				}
			}
		}
		return sb.toString();
	}
	

	/**
	 * Process jurisdiction ASE.
	 *
	 * @param mappingData        the mapping data
	 * @param inputDesignDetails the input design details
	 * @param result             the result
	 * @return the string
	 */
	protected String processJurisdictionASE(NxMpConfigMapping mappingData, JSONObject inputDesignDetails) { 
		String regionIdItemValue=this.geValueByUdfIdAndCompId(inputDesignDetails, 
				MyPriceConstants.REGION_ID_UDF_ID, MyPriceConstants.PORT_COMPONENT_CD_ID,MyPriceConstants.ASE_OFFER_ID); 
		String ifOofIndItemValue=this.geValueByUdfIdAndCompId(inputDesignDetails, 
				MyPriceConstants.IF_OOF_IND_UDF_ID_ASE, MyPriceConstants.PORT_COMPONENT_CD_ID,MyPriceConstants.ASE_OFFER_ID); 
		String inRegionIndicator=this.geValueByUdfIdAndCompId(inputDesignDetails, 
				200059l, MyPriceConstants.PORT_COMPONENT_CD_ID,MyPriceConstants.ASE_OFFER_ID); 
		String jurisdictionItemValue=this.getItemValueUsingJsonPath(mappingData.getPath(),inputDesignDetails); 
		if(StringUtils.isNotEmpty(jurisdictionItemValue)) { 
			if((jurisdictionItemValue.equals("FCC") || 
					jurisdictionItemValue.equals("Interstate (FCC) Access (Interstate)")) && 
					"13STATES".equals(regionIdItemValue) && 
					!"N".equalsIgnoreCase(inRegionIndicator)) { 
				//If Jurisdiction is "FCC" or "Inerstate (FCC) Access (Interstate)" AND IF/OOF = IF AND 
				return "FCC - 12 States"; 
			}else if((jurisdictionItemValue.equals("FCC") || 
					jurisdictionItemValue.equals("Interstate (FCC) Access (Interstate)")) && 
					"9STATES".equals(regionIdItemValue) && 
					!"N".equalsIgnoreCase(inRegionIndicator)) { 
				//If Jurisdiction is "FCC" or "Inerstate (FCC) Access (Interstate)" AND IF/OOF = IF AND 
				return "FCC - 9 States"; 
			}else if((jurisdictionItemValue.equals("FCC") || 
					jurisdictionItemValue.equals("Interstate (FCC) Access (Interstate)")) && 
					("N".equalsIgnoreCase(inRegionIndicator))) { 
				//If Jurisdiction is "FCC" or "Inerstate (FCC) Access (Interstate)" AND IF/OOF = OOF 
				return "FCC - LNS-OOR"; 
			}else if(jurisdictionItemValue.equals("State Access")) { 
				//If Jurisdiction is "State Access" 
				String state=this.getItemValueUsingJsonPath(STATE_PATH,inputDesignDetails); 
				if(StringUtils.isNotEmpty(state)) { 
					if(!state.equals("FL")) { 
						return state; 
					}else if(("IF").equals(ifOofIndItemValue) || ("OOF").equals(ifOofIndItemValue)) { 
						return state; 
					}else { 
						return "FL - LNS-OOR"; 
					} 
				} 
			} 
		} 
		return null; 
	}
	
	
	/**
	 * Process jurisdiction ADE.
	 *
	 * @param mappingData the mapping data
	 * @param inputDesignDetails the input design details
	 * @param result the result
	 * @return the string
	 */
	protected String processJurisdictionADE(NxMpConfigMapping mappingData, JSONObject inputDesignDetails) {
		String ifOofIndItemValue=this.geValueByUdfIdAndCompId(inputDesignDetails, 
				MyPriceConstants.IF_OOF_IND_UDF_ID_ADE, MyPriceConstants.END_POINT_COMPONENT_CD_ID,MyPriceConstants.ADE_OFFER_ID);
		String jurisdictionAttr=this.getItemValueUsingJsonPath(mappingData.getPath(),inputDesignDetails);
		String jurisdictionItemValue=this.getDataFromSalesLookUp(jurisdictionAttr,MyPriceConstants.ADE_OFFER_ID,
				mappingData.getUdfId(),mappingData.getComponentId());
		if(StringUtils.isNotEmpty(jurisdictionItemValue) && StringUtils.isNotEmpty(ifOofIndItemValue)) {
			if(jurisdictionItemValue.equals("Interstate (FCC) Access (Interstate)")&& 
					ifOofIndItemValue.equals("IF")) {
				//If "Jurisdiction" is 'Interstate (FCC) Access (Interstate)' AND  "IF/OOF Indicator" is 'IF'
				return "FCC - Interstate 21 states";
			}else if(jurisdictionItemValue.equals("Interstate (FCC) Access (Interstate)") && 
					ifOofIndItemValue.equals("OOF")) {
				//If "Jurisdiction" is 'Interstate (FCC) Access (Interstate)' AND "IF/OOF indicator" is 'OOF'
				return "FCC - Interstate LNS-OOR";
			}else if(jurisdictionItemValue.equals("Intrastate Access (Interlata/Intrastate)") && 
					ifOofIndItemValue.equals("IF")) {
				//If "Jurisdiction" is 'Intrastate Access (Interlata/Intrastate)' AND  "IF/OOF Indicator" is 'IF'
				return "Intrastate Access - 21 states";
			}else if(jurisdictionItemValue.equals("Intrastate Access (Interlata/Intrastate)") && 
					ifOofIndItemValue.equals("OOF")) {
				//If "Jurisdiction" is 'Intrastate Access (Interlata/Intrastate)' AND "IF/OOF indicator" is 'OOF'
				return "Intrastate Access - LNS-OOR";
			}else {
				String state=this.getItemValueUsingJsonPath(STATE_PATH,inputDesignDetails);
				if(StringUtils.isNotEmpty(state)) {
					return this.getDataFromNxLookUp(state, "mp_ade_jurisdiction_state");
				}
			}
		}
		return null;
	}
	

	/**
	 * Ge value by udf id and comp id.
	 *
	 * @param inputDesignDetails the input design details
	 * @param udfId the udf id
	 * @param componenetId the componenet id
	 * @param offerId the offer id
	 * @return the string
	 */
	protected String geValueByUdfIdAndCompId(JSONObject inputDesignDetails,Long udfId,Long componenetId,Long offerId) {
		String path="$..component.[?(@.componentCodeId=="+componenetId+")]."
				+ "designDetails.[?(@.udfId=="+udfId+")].udfAttributeId.[*]";
		return this.getDataFromSalesLookUp(this.getItemValueUsingJsonPath(path,inputDesignDetails),
				offerId,udfId,componenetId);
	}
	


	/**
	 * Process multiple json path.
	 *
	 * @param mappingData the mapping data
	 * @param inputDesignDetails the input design details
	 * @param offerId the offer id
	 * @param pathList the path list
	 * @param delim the delim
	 * @return the string
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected String processMultipleJsonPath(NxMpConfigMapping mappingData, JSONObject inputDesignDetails, Long offerId,
			List<String> pathList,String delim) {
		StringBuilder sb = new StringBuilder();
		for(String path:pathList) {
			if(path.contains(",")) {
				List<String> subConditionPathLst= new ArrayList(Arrays.asList(path.split(MyPriceConstants.COMMA_SEPERATOR)));
				if(sb.length()>0 && StringUtils.isNotEmpty(delim)) {
					 sb.append(delim);
				}
				String result=processMultipleJsonPath(mappingData, inputDesignDetails, offerId, 
						subConditionPathLst, MyPriceConstants.COMMA_SEPERATOR);
				if(StringUtils.isNotEmpty(result)) {
					sb.append(result);
				}
			}else if (path.contains(MyPriceConstants.OR_CONDITION_SEPERATOR)){
				List<String> subConditionPathLst= new ArrayList(Arrays.asList(path.split(Pattern.quote
						(MyPriceConstants.OR_CONDITION_SEPERATOR))));
				if(sb.length()>0 && StringUtils.isNotEmpty(delim)) {
					 sb.append(delim);
				}
				String result=this.processOrCondition(mappingData, inputDesignDetails, offerId, subConditionPathLst);
				if(StringUtils.isNotEmpty(result)) {
					sb.append(result);
				}
			}else {
				String itemValue=this.getItemValueUsingJsonPath(path,inputDesignDetails);
				if(StringUtils.isNotEmpty(itemValue)) {
					if(StringUtils.isNotEmpty(mappingData.getDataSetName())) {
						itemValue=this.processDataSetName(itemValue, mappingData, offerId);
					}
					if(sb.length()>0 && StringUtils.isNotEmpty(delim)) {
						sb.append(delim);
					}
					if(StringUtils.isNotEmpty(itemValue)) {
						sb.append(itemValue);
					}
					
				}
			}
		}
		return sb.toString();
	}
	
	
	/**
	 * Process or condition.
	 *
	 * @param mappingData the mapping data
	 * @param inputDesignDetails the input design details
	 * @param offerId the offer id
	 * @param pathList the path list
	 * @return the string
	 */
	protected String processOrCondition(NxMpConfigMapping mappingData,JSONObject inputDesignDetails,Long offerId,
			List<String> pathList) {
		for(String path:pathList) {
			String itemValue=null;
			if(path.contains(MyPriceConstants.MULTI_JSON_PATH_SEPERATOR)){
				List<String> subPathList= new ArrayList<String>(Arrays.asList(path.split(MyPriceConstants.MULTI_JSON_PATH_SEPERATOR)));
				itemValue= processMultipleJsonPath(mappingData,inputDesignDetails,offerId,subPathList,mappingData.getDelimiter());
			}else {
				itemValue=this.getItemValueUsingJsonPath(path,inputDesignDetails);
			}
			if(StringUtils.isNotEmpty(itemValue)) {
				if(StringUtils.isNotEmpty(mappingData.getDataSetName())) {
					itemValue=this.processDataSetName(itemValue,mappingData,offerId);
				}
				if(StringUtils.isNotEmpty(itemValue)) {
					return itemValue;
				}
			}
		}
		return null;
	}
	/**
	 * Process list result.
	 *
	 * @param mappingData the mapping data
	 * @param inputDesignDetails the input design details
	 * @param offerId the offer id
	 * @return the string
	 */
	protected String processListResult(NxMpConfigMapping mappingData,JSONObject inputDesignDetails, Long offerId) {
		TypeRef<List<String>> ref = new TypeRef<List<String>>() {};
		List<String> dataLst=jsonPathUtil.search(inputDesignDetails, mappingData.getPath(), ref);
		StringBuilder sb = new StringBuilder();
		if(CollectionUtils.isNotEmpty(dataLst)) {
			for(String itemValue:dataLst) {
				if(StringUtils.isNotEmpty(mappingData.getDataSetName())) {
					itemValue=this.processDataSetName(itemValue,mappingData,offerId);
				}
				
				if(sb.length()>0 && StringUtils.isNotEmpty(mappingData.getDelimiter())) {
					 sb.append(mappingData.getDelimiter());
				}
				if(StringUtils.isNotEmpty(itemValue)) {
					sb.append(itemValue);
				}
			}
		}
		return sb.toString();
	}
	
	
	
	/**
	 * Process data set name.
	 *
	 * @param input the input
	 * @param mappingData the mapping data
	 * @param offerId the offer id
	 * @return the string
	 */
	protected String processDataSetName(String input,NxMpConfigMapping mappingData,Long offerId) {
		String dataSourceName=mappingData.getDataSetName();
		if(dataSourceName.equals(MyPriceConstants.SALES_LOOKUP_SOURCE)) {
			input = getDataFromSalesLookUp(input,offerId,mappingData.getUdfId(),mappingData.getComponentId());
		}else if(dataSourceName.contains(MyPriceConstants.NX_LOOKUP_SOURCE)) {
			String looupDataSet = dataSourceName.substring(dataSourceName.indexOf('|')+1, dataSourceName.length()); 
			input = getDataFromNxLookUp(input, looupDataSet);
		}
		return input;
	}


	/**
	 * Gets the data from nx look up.
	 *
	 * @param input the input
	 * @param looupDataSet the looup data set
	 * @return the data from nx look up
	 */
	/*protected String getDataFromNxLookUp(String input, String looupDataSet) {
		if(StringUtils.isNotEmpty(looupDataSet)) {
			NxLookupData nxLookup=nxLookupDataRepository.findTopByDatasetNameAndItemId(looupDataSet, input);
			if(null!=nxLookup) {
				input=nxLookup.getDescription();
			}
		}
		return input;
	}*/
	
	protected String getDataFromNxLookUp(String input, String looupDataSet) {
		if(StringUtils.isNotEmpty(looupDataSet) && StringUtils.isNotEmpty(input)) {
			Map<String,NxLookupData> resultMap=nxMyPriceRepositoryServce.getLookupDataByItemId(looupDataSet);
			if(null!=resultMap && resultMap.containsKey(input) && null!= resultMap.get(input) ) {
				NxLookupData data=resultMap.get(input);
				return data.getDescription();
			}
		}
		return input;
	}


	/**
	 * Gets the data from sales look up.
	 *
	 * @param input the input
	 * @param mappingData the mapping data
	 * @param offerId the offer id
	 * @return the data from sales look up
	 */
	protected String getDataFromSalesLookUp(String input, Long offerId,Long udfId,Long componentId) {
		if(StringUtils.isNotEmpty(input) && null!=offerId && null!=udfId && null!=componentId){
			List<Object> result= lineItemProcessingDao.getDataFromSalesLookUpTbl(input,offerId,udfId,
					componentId);
			if(CollectionUtils.isNotEmpty(result) && result.get(0)!=null) {
				input= String.valueOf(result.get(0));
			}
		}
		
		return input;
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
									result.setUsocCode(transLineChild.getTextContent().substring(
											transLineChild.getTextContent().lastIndexOf("#")+1));
								}
							}
							if (transLineChild.getNodeName().equalsIgnoreCase("bmt:lii_uSOC_ql")) {
								result.setUsocCode(transLineChild.getTextContent());
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
			Boolean isChildDesign=configAndUpdatePricingUtil.isProductLineIdMatchForConfigDesign(methodParam,
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
			String offerName=methodParam.get(MyPriceConstants.OFFER_NAME)!=null?
							(String)methodParam.get(MyPriceConstants.OFFER_NAME):"";	
			String mpSolutionId=methodParam.get(MyPriceConstants.MP_SOLUTION_ID)!=null?
									(String)methodParam.get(MyPriceConstants.MP_SOLUTION_ID):"";					
			
			if(offerName.equals(MyPriceConstants.ADE_OFFER_NAME)) {
				String derivedComponentid=this.derivedComponentIdForAde(methodParam, input);
				if(StringUtils.isNotEmpty(derivedComponentid)) {
					if(StringUtils.isNotEmpty(mpSolutionId)) {
						boolean newSolutionProdRow=methodParam.get(mpSolutionId)!=null?(Boolean)methodParam.get(mpSolutionId):false;
						if(newSolutionProdRow) {
							createNewNxMpDesignDocument(input, nxDesignId, mpSolutionId,derivedComponentid);
						}else {
							nxMpDesignDocumentRepo.updateDesignBySolIdAndProductIdForAde(Long.valueOf(input.getDocumentNumber()),
									input.getUsocCode(),derivedComponentid,new Date(),mpSolutionId,
									input.getParentDocNumber(),input.getNxTransactionId());
							methodParam.put(mpSolutionId, true);
						}
					}
				}
			}else {
				if(StringUtils.isNotEmpty(mpSolutionId)) {
					boolean newSolutionProdRow=methodParam.get(mpSolutionId)!=null?(Boolean)methodParam.get(mpSolutionId):false;
					if(newSolutionProdRow) {
						createNewNxMpDesignDocument(input, nxDesignId, mpSolutionId,null);
					}else {
						nxMpDesignDocumentRepo.updateDesignBySolIdAndProductId(Long.valueOf(input.getDocumentNumber()),
								input.getUsocCode(),new Date(),mpSolutionId,
								input.getParentDocNumber(),input.getNxTransactionId());
						methodParam.put(mpSolutionId, true);
					}
				}
			}
			isSuccessful=true;	
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
		//setting only for ADE
		entity.setMpPartNumber(mpPartNumber);
		entity.setActiveYN(com.att.sales.nexxus.common.CommonConstants.ACTIVE_Y);
		entity.setCreatedDate(new Date());
		nxMpDesignDocumentRepo.save(entity);
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected String derivedComponentIdForAde(Map<String,Object> methodParam,ConfigRespProcessingBean input) {
		String requestEndPointANxSiteId=methodParam.get(MyPriceConstants.ENDPOINT_A_NX_SITE_ID)!=null?
				(String)methodParam.get(MyPriceConstants.ENDPOINT_A_NX_SITE_ID):"";
		String requestEndPointZNxSiteId=methodParam.get(MyPriceConstants.ENDPOINT_Z_NX_SITE_ID)!=null?
				(String)methodParam.get(MyPriceConstants.ENDPOINT_Z_NX_SITE_ID):"";
		String responseRefSiteId=StringUtils.isNotEmpty(input.getNxSiteId())?input.getNxSiteId():"";	
		String responseBeId=StringUtils.isNotEmpty(input.getUsocCode())?input.getUsocCode():"";
		
		if(responseRefSiteId.equals(requestEndPointANxSiteId)) {
			
			List<String> requestCircuitBeId=methodParam.get(MyPriceConstants.CIRCUIT_BEID)!=null?
					(List)methodParam.get(MyPriceConstants.CIRCUIT_BEID):new ArrayList<>();
			List<String> requestEndPointABeId=methodParam.get(MyPriceConstants.ENDPOINT_A_BEID)!=null?
					(List)methodParam.get(MyPriceConstants.ENDPOINT_A_BEID):new ArrayList<>();
			List<String> requestEndPointZBeId=methodParam.get(MyPriceConstants.ENDPOINT_Z_BEID)!=null?
					(List)methodParam.get(MyPriceConstants.ENDPOINT_Z_BEID):new ArrayList<>();
					
			if(CollectionUtils.isNotEmpty(requestCircuitBeId) && requestCircuitBeId.contains(responseBeId)) {
				return methodParam.get(MyPriceConstants.CIRCUIT_COMPONENT_ID).toString();
			}else if(CollectionUtils.isNotEmpty(requestEndPointABeId) && requestEndPointABeId.contains(responseBeId)) {
				return methodParam.get(MyPriceConstants.ENDPOINT_A_COMPONENT_ID).toString();
			}else if(CollectionUtils.isNotEmpty(requestEndPointZBeId) && requestEndPointZBeId.contains(responseBeId)) {
				return methodParam.get(MyPriceConstants.ENDPOINT_Z_COMPONENT_ID).toString();
			}	
			
		}else if(responseRefSiteId.equals(requestEndPointZNxSiteId)) {
			
			List<String> requestEndPointZBeId=methodParam.get(MyPriceConstants.ENDPOINT_Z_BEID)!=null?
					(List)methodParam.get(MyPriceConstants.ENDPOINT_Z_BEID):new ArrayList<>();
			if(CollectionUtils.isNotEmpty(requestEndPointZBeId) && requestEndPointZBeId.contains(responseBeId)) {
				return methodParam.get(MyPriceConstants.ENDPOINT_Z_COMPONENT_ID).toString();
			}		
							
		}
		return null;
	}
	
	
	
	
	/**
	 * Gets the mp product line id by nx txn id and nx design id.
	 *
	 * @param paramMap the param map
	 * @return the mp product line id by nx txn id and nx design id
	 */
	private String getMpProductLineIdByNxTxnIdAndNxDesignId(Map<String,Object> paramMap) {
		Long nxTxnId = paramMap.containsKey(MyPriceConstants.NX_TRANSACTION_ID) ? 
				(long) paramMap.get(MyPriceConstants.NX_TRANSACTION_ID) : 0L;
		Long nxDesignId = paramMap.containsKey(MyPriceConstants.NX_DESIGN_ID) ? 
				(long) paramMap.get(MyPriceConstants.NX_DESIGN_ID) : 0L;
		
		List<NxMpDesignDocument> productLineIds = nxMpDesignDocumentRepo.
				getMpProductLineIdByNxTxnIdAndNxDesignId(nxTxnId, nxDesignId);
		if(CollectionUtils.isNotEmpty(productLineIds)) {
			return productLineIds.get(0).getMpProductLineId();
		}
	
		return "";
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
	
	
	
	/**
	 * Derived data for request usoc fields.
	 *
	 * @param inputDesignDetails the input design details
	 * @param requestMap the request map
	 */
	protected void derivedDataForRequestUsocFields(JSONObject inputDesignDetails,Map<String, Object> requestMap) {
		StringBuilder usocPfSb = new StringBuilder();
		StringBuilder newSb = new StringBuilder();
		StringBuilder existingSb = new StringBuilder();
		StringBuilder migrationSb = new StringBuilder();
		String offerName=requestMap.get(MyPriceConstants.OFFER_NAME)!=null?
				(String)requestMap.get(MyPriceConstants.OFFER_NAME):"";	
		if(offerName.equals(MyPriceConstants.ADE_OFFER_NAME)) {
			//map holding key as beid and value as list of field map from circuit,and Endpoint level
			Map<String,List<Map<String,String>>> usocDataMap=this.createDataMapByUsocIdForAde(inputDesignDetails, requestMap);
			if(MapUtils.isNotEmpty(usocDataMap)) {
				int telcoBillingElementArray=0;
				for (Map.Entry<String,List<Map<String,String>>>  usocEntry : usocDataMap.entrySet()) {
					String usocId=usocEntry.getKey();
					List<Map<String,String>> data=usocEntry.getValue();
					int newCount=0;
					int existingCount=0;
					int migrationCount=0;
					boolean isAvailable=false;
					for(Map<String,String> usocFieldsDataMap:data) {
						//derived the New/Existing/Migration category for usoc id using rule from nx_look_up table
						String usocIdCategory=configAndUpdatePricingUtil.getUsocIdCategory(inputDesignDetails, offerName, usocFieldsDataMap,requestMap);
						isAvailable=false;
						if(StringUtils.isNotEmpty(usocIdCategory)) {
							isAvailable=true;
							telcoBillingElementArray++;
							if(usocIdCategory.equals(MyPriceConstants.NEW)) {
								newCount++;
							}
							if(usocIdCategory.equals(MyPriceConstants.EXISTING)) {
								existingCount++;
							}
							if(usocIdCategory.equals(MyPriceConstants.MIGRATION)) {
								migrationCount++;
							}
						}
					}
					if(isAvailable) {
						//usoc String
						createDataForADE(usocPfSb, usocId);
						//new Usoc Quantity
						createDataForADE(newSb, newCount);
						//Existing Usoc Quantity
						createDataForADE(existingSb, existingCount);
						//Migration Usoc Quantity
						createDataForADE(migrationSb, migrationCount);
					}
				}
				requestMap.put(MyPriceConstants.USOC_VALUE, usocPfSb.toString());
				requestMap.put(MyPriceConstants.TELCO_BILLING_ARRAY, telcoBillingElementArray>0?telcoBillingElementArray:null);
				requestMap.put(MyPriceConstants.NEW,newSb.toString());
				requestMap.put(MyPriceConstants.EXISTING,existingSb.toString());
				requestMap.put(MyPriceConstants.MIGRATION,migrationSb.toString());
			}
			
		}else {
			this.processPortQtyAndCIRQtyASE(inputDesignDetails, requestMap,offerName);
			Map<String,Map<String,String>> usocDataMap=this.createDataMapByUsocId(inputDesignDetails,requestMap);
			
			if(MapUtils.isNotEmpty(usocDataMap)) {
				int telcoBillingElementArray=0;
				for (Map.Entry<String,Map<String,String>>  usocEntry : usocDataMap.entrySet()) {
					String usocId=usocEntry.getKey();
					Map<String,String> usocFieldsDataMap=usocEntry.getValue();
					//derived the New/Existing/Migration category for usoc id using rule from nx_look_up table
					String usocIdCategory=configAndUpdatePricingUtil.getUsocIdCategory(inputDesignDetails, offerName, usocFieldsDataMap,requestMap);
					if(StringUtils.isNotEmpty(usocIdCategory)) {
						telcoBillingElementArray++;
						//create String for usoc_pf
						this.createDataForUsocPf(usocPfSb, usocId,MyPriceConstants.MP_DELIM);
						//create String for New category
						this.createDataForNewCategory(newSb, usocIdCategory,MyPriceConstants.MP_DELIM);
						//create String for Existing category
						this.createDataForExistingCategory(existingSb, usocIdCategory,MyPriceConstants.MP_DELIM);
						//create String for Migration category
						this.createDataForMigrationCategory(migrationSb, usocIdCategory,MyPriceConstants.MP_DELIM);
					}
				}
				requestMap.put(MyPriceConstants.USOC_VALUE, usocPfSb.toString());
				requestMap.put(MyPriceConstants.TELCO_BILLING_ARRAY, telcoBillingElementArray>0?telcoBillingElementArray:null);
				requestMap.put(MyPriceConstants.NEW,newSb.toString());
				requestMap.put(MyPriceConstants.EXISTING,existingSb.toString());
				requestMap.put(MyPriceConstants.MIGRATION,migrationSb.toString());
			}
			//custom code for ASENOD 3PA
			this.processAsenod3PAUsocPF(requestMap);
		}	
		
	}
	
	
	@SuppressWarnings("unchecked")
	protected void processAsenod3PAUsocPF(Map<String, Object> requestMap) {
		StringBuilder mergeUsocPfSb = new StringBuilder();
		//all usoc From request other than newExistingMigration rulebase Usoc	
		Set<String> allUsocFor3PA=requestMap.get("all3PAUsoc")!=null?
				(HashSet<String>) requestMap.get("all3PAUsoc"):new HashSet<String>();
		//newExistingMigration rulebase Usoc		
		String ruleBaseUsoc=requestMap.get(MyPriceConstants.USOC_VALUE)!=null?requestMap.get(MyPriceConstants.USOC_VALUE).toString():null;
		//merge rule base usocId to all usoc list
		if(StringUtils.isNotEmpty(ruleBaseUsoc)) {
			List<String> ruleBaseUsocLst = new ArrayList<String>(Arrays.asList(ruleBaseUsoc.split(Pattern.quote(MyPriceConstants.MP_DELIM))));
			allUsocFor3PA.addAll(ruleBaseUsocLst);
		}
		Set<String> checkDuplicates=new HashSet<String>();
		//convert  usocId in myPrice format and append in string
		if(CollectionUtils.isNotEmpty(allUsocFor3PA)) {
			Map<String,List<String>> usocCriteriaMap=this.getAsenod3PaUsocIdMap();		
			for(String usocId:allUsocFor3PA) {
				String updatedUsocId=getConvertedUsocIdFor3PA(usocCriteriaMap, usocId);
				if(mergeUsocPfSb.length()>0) {
					mergeUsocPfSb.append(MyPriceConstants.MP_DELIM);
				}
				if(checkDuplicates.add(updatedUsocId)) {
					mergeUsocPfSb.append(updatedUsocId);
				}
				
			}
			if(null!=mergeUsocPfSb) {
				requestMap.put(MyPriceConstants.USOC_VALUE, mergeUsocPfSb.toString());
				requestMap.put(MyPriceConstants.TELCO_BILLING_ARRAY, allUsocFor3PA.size());
			}
		}
		
	}
	
	public String getConvertedUsocIdFor3PA(Map<String,List<String>> usocCriteriaMap,String inputUsocId) {
		//convert  usocId in myPrice format and append in string
		if(MapUtils.isNotEmpty(usocCriteriaMap)) {
			for (Map.Entry<String,List<String>>  usocCriteria : usocCriteriaMap.entrySet()) {
				List<String> criteria=usocCriteria.getValue();
				if(criteria.contains(inputUsocId)) {
					return usocCriteria.getKey();
				}
			}
		}
		return inputUsocId;
		
	}
	
	
	public Map<String,List<String>> getAsenod3PaUsocIdMap(){
		//get rule data from lookup data table for converting usocId to myPrice format
		Map<String,List<String>> result=new HashMap<String, List<String>>();
		List<NxLookupData> nxLookupLst=nxLookupDataRepository.findByDatasetName(MyPriceConstants.ASENOD_3PA_USOC_RANGE_DATASET);
		 Optional.ofNullable(nxLookupLst).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull).
		    forEach( data -> {
		    	if(StringUtils.isNotEmpty(data.getCriteria()) && StringUtils.isNotEmpty(data.getItemId())) {
		    		List<String> range=new ArrayList<String>(Arrays.asList(data.getCriteria().split(Pattern.quote(","))));
		    		result.put(data.getItemId(), range);
		    	}
		    	
		 });
		return result;
	}
	
	public Map<String, String> getAsenod3PaUsocIdMap1() {
		Map<String, String> result = new HashMap<>();
		Map<String, NxLookupData> lookupData = nxMyPriceRepositoryServce.getLookupDataByItemId(MyPriceConstants.ASENOD_3PA_USOC_RANGE_DATASET);
		for (NxLookupData data : lookupData.values()) {
			if(StringUtils.isNotEmpty(data.getCriteria()) && StringUtils.isNotEmpty(data.getItemId())) {
				for (String key : data.getCriteria().split("\\s*,\\s*")) {
					result.put(key, data.getItemId());
				}
			}
		}
		return result;
	}
	
	/**
	 * Creates the data map by usoc id.
	 *
	 * @param inputDesignDetails the input design details
	 * @param requestMap the request map
	 * @return the map
	 */
	protected Map<String,Map<String,String>> createDataMapByUsocId(JSONObject inputDesignDetails,Map<String, Object> requestMap){
		Map<String,Map<String,String>> result=new TreeMap<>();	
		String priceScenarioId=requestMap.get(StringConstants.PRICE_SCENARIO_ID)!=null?
				String.valueOf(requestMap.get(StringConstants.PRICE_SCENARIO_ID)):null;
		String offerName=requestMap.get(MyPriceConstants.OFFER_NAME)!=null?
				(String)requestMap.get(MyPriceConstants.OFFER_NAME):"";	
		String updatedPath="";
		if(offerName.equals(MyPriceConstants.ASE_OFFER_NAME)) {
			String rateGroupsPath=this.createPathFromNxLookUp("rateGroup", MyPriceConstants.ASE_USOC_DATASET, "==", "||");
			updatedPath=MyPriceConstants.ASE_USOC_PATH.replaceAll("%rateGroups%", rateGroupsPath).
							replaceAll("%priceScenarioId%", priceScenarioId);
		}if(StringConstants.OFFERNAME_ASENOD.equalsIgnoreCase(offerName)) {
			String dataSetName=MyPriceConstants.ASENOD_USOC_DATASET;
			String productType=requestMap.get(MyPriceConstants.PRODUCT_TYPE)!=null?requestMap.get(MyPriceConstants.PRODUCT_TYPE).toString():"";
			if(StringUtils.isNotEmpty(productType) && productType.equalsIgnoreCase(MyPriceConstants.ASENOD_3PA)) {
				dataSetName=MyPriceConstants.ASENOD__3PA_USOC_DATASET;
			}	
			String rateGroupsPath=this.createPathFromNxLookUp("rateGroup", dataSetName, "==", "||");
			updatedPath=MyPriceConstants.ASE_USOC_PATH.replaceAll("%rateGroups%", rateGroupsPath).
							replaceAll("%priceScenarioId%", priceScenarioId);
		}/*else if(offerName.equals(MyPriceConstants.ADE_OFFER_NAME)){
			String priceUnitsPath=this.createPathFromNxLookUp("priceUnit", MyPriceConstants.ADE_USOC_DATASET, "==", "||");
			updatedPath=MyPriceConstants.ADE_USOC_PATH.replaceAll("%priceUnits%", priceUnitsPath).
							replaceAll("%priceScenarioId%", priceScenarioId);
		}*/
		//priceAttribute filter on base rateGroup for ASE and priceUnit for ADE
		List<PriceAttributes> priceAttributesLst=configAndUpdatePricingUtil.getPriceAttributes(inputDesignDetails,updatedPath);
		for(PriceAttributes obj:priceAttributesLst) {
			Map<String,String> filedDataMap=new HashMap<>();
			filedDataMap.put("rateGroup", obj.getRateGroup());
			filedDataMap.put("priceGroup", obj.getPriceGroup());
			filedDataMap.put("priceType", obj.getPriceType());
			result.put(obj.getBeid(), filedDataMap);
		}
		
		//filter the  Uscoc(picked based on rategroup) from all Uscoc
		this.filter3PAUsoc(requestMap, inputDesignDetails, priceAttributesLst);
		return result;
		
	}
	
	protected Map<String,List<Map<String,String>>>  createDataMapByUsocIdForAde(JSONObject inputDesignDetails,Map<String, Object> requestMap){
		String priceScenarioId=requestMap.get(StringConstants.PRICE_SCENARIO_ID)!=null?
				String.valueOf(requestMap.get(StringConstants.PRICE_SCENARIO_ID)):null;
		List<ComponentDetails> componentData=configAndUpdatePricingUtil.getComponentList(inputDesignDetails, "$..priceDetails.componentDetails.*");		
		Map<String,List<Map<String,String>>> dataMap=new HashMap<String, List<Map<String,String>>>();
		if(CollectionUtils.isNotEmpty(componentData)) {
			for(ComponentDetails c:componentData) {
				String priceUnitsPath=this.createPathFromNxLookUp("priceUnit", MyPriceConstants.ADE_USOC_DATASET, "==", "||");
				String updatedPath=MyPriceConstants.ADE_USOC_PATH.replaceAll("%priceUnits%", priceUnitsPath).
								replaceAll("%priceScenarioId%", priceScenarioId);
				List<PriceAttributes> priceAttributesLst= configAndUpdatePricingUtil.getPriceAttributes(c,updatedPath);
				 Map<String,Map<String,String>> map=new HashMap<>();
				 if(CollectionUtils.isNotEmpty(priceAttributesLst)) {
					 for(PriceAttributes obj:priceAttributesLst) {
						 Map<String,String> filedDataMap=new HashMap<>();
						 filedDataMap.put("usocId", obj.getBeid());
						 filedDataMap.put("rateGroup", obj.getRateGroup());
						 filedDataMap.put("priceGroup", obj.getPriceGroup());
						 filedDataMap.put("priceType", obj.getPriceType());	
						 map.put(obj.getBeid(),filedDataMap);
					 }
					 for (Map.Entry<String,Map<String,String>>  usocEntry : map.entrySet()) {
						 String uscoId=usocEntry.getKey();
						 if(!dataMap.containsKey(uscoId)) {
							 dataMap.put(uscoId, new ArrayList<Map<String,String>>());
						 }
						 dataMap.get(uscoId).add(usocEntry.getValue());
					 }
				 }
			}
		}
		
		return dataMap;
		
	}
	
	protected void filter3PAUsoc(Map<String, Object> requestMap,JSONObject inputDesignDetails,List<PriceAttributes> beidByRateGroup) {
		String productType=requestMap.get(MyPriceConstants.PRODUCT_TYPE)!=null?requestMap.get(MyPriceConstants.PRODUCT_TYPE).toString():"";
		if(StringUtils.isNotEmpty(productType) && productType.equalsIgnoreCase(MyPriceConstants.ASENOD_3PA)) {
			List<String> all3PAUsoc=configAndUpdatePricingUtil.getDataListInString(inputDesignDetails, MyPriceConstants.ASENOD_3PA_ALL_USOC_PATH);
			if(CollectionUtils.isNotEmpty(all3PAUsoc)) {
				for(PriceAttributes obj:beidByRateGroup) {
					if(StringUtils.isNotEmpty(obj.getBeid()) && all3PAUsoc.contains(obj.getBeid())) {
						all3PAUsoc.remove(obj.getBeid());
					}
					
				}
				requestMap.put("all3PAUsoc", new HashSet<String>(all3PAUsoc));
			}
		
		}
		
	}
	
	/**
	 * Creates the data for usoc pf.
	 *
	 * @param sb the sb
	 * @param usocId the usoc id
	 * @param delim the delim
	 */
	protected void createDataForUsocPf(StringBuilder sb,String usocId,String delim) {
		if(sb.length()>0) {
			sb.append(delim);
		}
		sb.append(usocId);
	}
	protected void createDataForNewCategory(StringBuilder sb,String usocIdCatgory,String delim) {
		String data=usocIdCatgory.equals(MyPriceConstants.NEW)?"1":"0";
		if(sb.length()>0) {
			sb.append(delim);
		}
		sb.append(data);
	}
	
	protected void createDataForExistingCategory(StringBuilder sb,String usocIdCatgory,String delim) {
		String data=usocIdCatgory.equals(MyPriceConstants.EXISTING)?"1":"0";
		if(sb.length()>0) {
			sb.append(delim);
		}
		sb.append(data);
	}
	
	protected void createDataForMigrationCategory(StringBuilder sb,String usocIdCatgory,String delim) {
		String data=usocIdCatgory.equals(MyPriceConstants.MIGRATION)?"1":"0";
		if(sb.length()>0) {
			sb.append(delim);
		}
		sb.append(data);
	}
	
	protected void createDataForADE(StringBuilder sb,Object data) {
		if(sb.length()>0) {
			sb.append(MyPriceConstants.MP_DELIM);
		}
		sb.append(data);
	}
	
	
	
	protected void setSolProductData(Map<String, Object> requestMap,String productId) {
		if(StringUtils.isNotEmpty(productId)) {
			Long nxTxnId=requestMap.get(MyPriceConstants.NX_TRANSACTION_ID)!=null?
					(Long)requestMap.get(MyPriceConstants.NX_TRANSACTION_ID):null;			
					Map<String,String> solProductDataMap=nxMpRepositoryService.getDataByNxtxnId(nxTxnId);
					requestMap.put(MyPriceConstants.SOLUTION_PRODUCT_DATA, solProductDataMap);
					String mpSolutionId=Optional.ofNullable(solProductDataMap).orElseGet(Collections::emptyMap).entrySet().stream()
							  .filter(e -> e.getValue().equals(productId))
							  .map(Map.Entry::getKey)
							  .findFirst()
							  .orElse(null);
					requestMap.put(MyPriceConstants.MP_SOLUTION_ID, mpSolutionId);
		}
		
	}
	
	protected  void processPortQtyAndCIRQtyASE(JSONObject inputDesignDetails, Map<String, Object> requestMap,String offerName) {
		if(offerName.equals(MyPriceConstants.ASE_OFFER_NAME)) {
			List<String> reqMacdActivityUdfAttr=restCommonUtil.geDataInListString(inputDesignDetails, MyPriceConstants.ASE_MCAD_ACTIVITY_PATH);
			requestMap.put(MyPriceConstants.REQ_MCAD_ACTIVITY_UDFATTR, reqMacdActivityUdfAttr);
			String reqMcadType=configAndUpdatePricingUtil.getRequestMacdTypeForASE(inputDesignDetails, requestMap);
			requestMap.put(MyPriceConstants.REQ_MCAD_TYPE_VALUE, reqMcadType);
			List<NxLookupData> macdOrder=nxLookupDataRepository.getOrderForMacd("ASE_MCAD_ORDER");
			requestMap.put(MyPriceConstants.ASE_MCAD_ORDER_DATA, macdOrder);
			String portQtyPf=configAndUpdatePricingUtil.getPortQtyPf(requestMap);
			requestMap.put(MyPriceConstants.PORT_QUANTITY, portQtyPf);
			String cirQtyPf=configAndUpdatePricingUtil.getCIRQtyPf(requestMap);
			requestMap.put(MyPriceConstants.CIR_QUANTITY, cirQtyPf);
		}
	}
	
	
}
