package com.att.sales.nexxus.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.CustomJsonConstants;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxDesign;
import com.att.sales.nexxus.dao.model.NxDesignDetails;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxMpDesignDocument;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxDesignRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxMpDesignDocumentRepository;
import com.att.sales.nexxus.dao.repository.NxMpPriceDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.myprice.transaction.model.CreateTransactionResponse;
import com.att.sales.nexxus.myprice.transaction.model.GetTransactionLineItem;
import com.att.sales.nexxus.myprice.transaction.model.GetTransactionLineResponse;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdateRestProcessingService;
import com.att.sales.nexxus.myprice.transaction.service.GetTransactionLineServiceImpl;
import com.att.sales.nexxus.myprice.transaction.service.MyPriceTransactionUtil;
import com.att.sales.nexxus.myprice.transaction.service.RemoveTransactionServiceLineImpl;
import com.att.sales.nexxus.myprice.transaction.service.UpdateTransactionPricingServiceForSolutionImpl;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.TypeRef;

/**
 * @author(ar896d)
 *
 */

@Component
public class ProcessPDtoMPRestUtil {

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(ProcessPDtoMPRestUtil.class);

	@Autowired
	private ConfigAndUpdateRestProcessingService configAndUpdateRestProcessingService;

	@Autowired
	private GetTransactionLineServiceImpl getTransactionLineServiceImpl;

	@Autowired
	private NxMpDesignDocumentRepository nxMpDesignDocumentRepo;

	@Autowired
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;

	@Autowired
	private NxDesignRepository nxDesignRepository;

	@Autowired
	private MyPriceTransactionUtil myPriceTransactionUtil;

	@Value("${myprice.restField.documentId:0l}")
	private String documentId;

	@Autowired
	private UpdateTransactionPricingServiceForSolutionImpl updateTransactionPricingServiceForSolutionImpl;

	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;

	@Autowired
	private JsonPathUtil jsonPathUtil;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private RemoveTransactionServiceLineImpl removeTransactionServiceLineImpl;
	
	@Autowired
	private NxMpPriceDetailsRepository nxMpPriceDetailsRepository;
	
	public Map<String, Object> restConfigAndDesign(CreateTransactionResponse createTransactionResponse,
			NxDesign nxDesign, Map<String, Object> paramMap, JsonNode siteJson,String asrItemId) {
		Map<String, Object> responseMap = new HashMap<>();
		boolean automationInd = paramMap.get(MyPriceConstants.AUTOMATION_IND) != null
				? (boolean) paramMap.get(MyPriceConstants.AUTOMATION_IND)
				: false;
		String priceUpdate = paramMap.containsKey(MyPriceConstants.PRICE_UPDATE)
				? paramMap.get(MyPriceConstants.PRICE_UPDATE).toString()
				: null;
		String contractTerm = paramMap.containsKey(MyPriceConstants.CONTRACT_TERM)
				? paramMap.get(MyPriceConstants.CONTRACT_TERM).toString()
				: null;

		String subOfferName = paramMap.containsKey(MyPriceConstants.SUB_OFFER_NAME)
				? (String) paramMap.get(MyPriceConstants.SUB_OFFER_NAME)
				: "";
		String offerName = paramMap.containsKey(MyPriceConstants.OFFER_NAME)
				? (String) paramMap.get(MyPriceConstants.OFFER_NAME)
				: "";
		String flowType = offerName;
		Long nxSolutionId = paramMap.containsKey(MyPriceConstants.NX_SOLIUTION_ID)
				? Long.valueOf(paramMap.get(MyPriceConstants.NX_SOLIUTION_ID).toString())
				: 0L;

		String inputDesign = nxDesign.getNxDesignDetails().get(0).getDesignData();
		//logger.info("Design data send to rest API :==>> {} ", inputDesign);
		String transactionType = paramMap.containsKey(StringConstants.TRANSACTION_TYPE)
				? paramMap.get(StringConstants.TRANSACTION_TYPE).toString()
				: null;

		List<Long> nxDesignIdListToUpdate = paramMap.containsKey(MyPriceConstants.DESIGN_ID_FOR_UPDATE_PRICING)?
				(List<Long>)paramMap.get(MyPriceConstants.DESIGN_ID_FOR_UPDATE_PRICING): new ArrayList<Long>();
	
		List<Long> nxDesignIdListUpdateReconfigure = paramMap.containsKey(MyPriceConstants.DESIGN_ID_FOR_UPDATE_PRICING_RECONFIGURE)?
						(List<Long>)paramMap.get(MyPriceConstants.DESIGN_ID_FOR_UPDATE_PRICING_RECONFIGURE): new ArrayList<Long>();
			
		if (StringConstants.TRANSACTION_TYPE_NEW.equalsIgnoreCase(transactionType)) {
			Map<String, Object> requestMap = new HashMap<String, Object>();
			requestMap.put(MyPriceConstants.PRODUCT_TYPE, MyPriceConstants.SOURCE_PD);
			requestMap.put(MyPriceConstants.NX_SOLIUTION_ID, nxSolutionId);
			requestMap.put(CustomJsonConstants.BS_ID,
					createTransactionResponse.getMyPriceTransacId() != null
							? Long.valueOf(createTransactionResponse.getMyPriceTransacId())
							: 0l);
			requestMap.put(MyPriceConstants.MP_TRANSACTION_ID,
					createTransactionResponse.getMyPriceTransacId() != null
							? Long.valueOf(createTransactionResponse.getMyPriceTransacId())
							: 0l);
			requestMap.put(CustomJsonConstants.DOCUMENT_ID, Long.valueOf(documentId));
			requestMap.put(StringConstants.PRICE_SCENARIO_ID,
					createTransactionResponse.getPriceScenarioId().toString());
			if (MyPriceConstants.ASENOD_OFFER_NAME.equalsIgnoreCase(offerName)) {
				requestMap.put(MyPriceConstants.SUB_OFFER_NAME, subOfferName);
			}
			requestMap.put(MyPriceConstants.OFFER_NAME, offerName);

			responseMap = configSolutionDesign(requestMap, MyPriceConstants.AUDIT_CONFIG_SOLN_DESIGN_REST, nxDesign,
					inputDesign);
			if (responseMap.containsKey(MyPriceConstants.RESPONSE_STATUS)
					&& (boolean) responseMap.get(MyPriceConstants.RESPONSE_STATUS)) {
				// design ids for updatePricing
				if(!(MyPriceConstants.ADE_OFFER_NAME.equalsIgnoreCase(offerName)&& MyPriceConstants.CIRCUIT_TYPE_STANDARD.equalsIgnoreCase(nxDesign.getDesignType()))) {
					nxDesignIdListToUpdate.add(nxDesign.getNxDesignId());
				}
				paramMap.put(MyPriceConstants.DESIGN_ID_FOR_UPDATE_PRICING, nxDesignIdListToUpdate);
			}
		} else if (StringConstants.TRANSACTION_TYPE_EXISTING.equalsIgnoreCase(transactionType)) {
			List<String> offerList=new ArrayList<String>();
			offerList.add(offerName.toUpperCase());
			Map<String, Object> designMap = new HashMap<String, Object>();
			designMap.put(MyPriceConstants.MP_TRANSACTION_ID, createTransactionResponse.getMyPriceTransacId());
			designMap.put(MyPriceConstants.NX_TRANSACTION_ID, createTransactionResponse.getNxTransacId());
			designMap.put(MyPriceConstants.OFFER_NAME, createTransactionResponse.getOfferName());
			designMap.put(StringConstants.PRICE_SCENARIO_ID, createTransactionResponse.getPriceScenarioId());
			designMap.put(MyPriceConstants.NX_DESIGN, nxDesign);
			designMap.put(MyPriceConstants.NX_DESIGN_ID, nxDesign.getNxDesignId());
			designMap.put(MyPriceConstants.OFFER_TYPE, flowType);
			designMap.put(MyPriceConstants.PRICE_UPDATE, priceUpdate);
			designMap.put(MyPriceConstants.CONTRACT_TERM, contractTerm);
			designMap.put(MyPriceConstants.AUTOMATION_IND, automationInd);

			Map<String, Object> requestMap = new HashMap<String, Object>();
			requestMap.put(MyPriceConstants.PRODUCT_TYPE, MyPriceConstants.SOURCE_PD);
			requestMap.put(MyPriceConstants.NX_SOLIUTION_ID, nxSolutionId);
			requestMap.put(CustomJsonConstants.BS_ID,
					createTransactionResponse.getMyPriceTransacId() != null
							? Long.valueOf(createTransactionResponse.getMyPriceTransacId())
							: 0l);
			requestMap.put(CustomJsonConstants.DOCUMENT_ID, Long.valueOf(documentId));
			requestMap.put(StringConstants.PRICE_SCENARIO_ID,
					createTransactionResponse.getPriceScenarioId().toString());


			if (MyPriceConstants.ASENOD_OFFER_NAME.equalsIgnoreCase(offerName)) {

				requestMap.put(MyPriceConstants.SUB_OFFER_NAME, subOfferName);
			}
				requestMap.put(MyPriceConstants.OFFER_NAME, offerName);

			String designModifiedInd = siteJson.path("designModifiedInd").asText();
			String designStatus = siteJson.path("designStatus").asText();

			// remove transaction for retrigger
			String isReconfiure = (String) paramMap.get("IS_RECONFIGURE");
			if (StringConstants.CONSTANT_N.equalsIgnoreCase(isReconfiure)) {
				logger.info(
						"Calling remove transaction for deleting solution and product lines for retrigger in rest call {}",
						createTransactionResponse.getNxTransacId());
				myPriceTransactionUtil.removeTransactionServiceLineImpl(designMap);
			}

			// Add site scenario
			if (StringConstants.DESIGN_NEW.equalsIgnoreCase(designStatus)) {
				responseMap = configSolutionDesign(requestMap, MyPriceConstants.AUDIT_CONFIG_SOLN_DESIGN_REST_RC,
						nxDesign, inputDesign);
				boolean result = responseMap.containsKey(MyPriceConstants.RESPONSE_STATUS)
						? (boolean) responseMap.get(MyPriceConstants.RESPONSE_STATUS)
						: false;
				if (result) {
					if(!(MyPriceConstants.ADE_OFFER_NAME.equalsIgnoreCase(offerName)&& MyPriceConstants.CIRCUIT_TYPE_STANDARD.equalsIgnoreCase(nxDesign.getDesignType()))) {
						nxDesignIdListToUpdate.add(nxDesign.getNxDesignId());
					}
					paramMap.put(MyPriceConstants.DESIGN_ID_FOR_UPDATE_PRICING, nxDesignIdListToUpdate);
					// get the part number details
					if ( StringConstants.OFFERNAME_ASENOD.equalsIgnoreCase(offerName) && 
							MyPriceConstants.ASENOD_3PA.equalsIgnoreCase(subOfferName)) {
						String nxSiteId= getNXSiteId(nxDesign);
						processConfigSolDesignResponse(paramMap,createTransactionResponse, offerName,subOfferName,nxSolutionId,null,nxSiteId,offerList,null);

					}else {
						processConfigSolDesignResponse(paramMap,createTransactionResponse, offerName,subOfferName,nxSolutionId,asrItemId,null,offerList,null);
					}				
				}
			}
			// design cancel / delete scenario
			else if (StringConstants.DESIGN_CANCEL.equalsIgnoreCase(designStatus)) {
				boolean result = myPriceTransactionUtil.removeTransactionServiceLineImpl(designMap);
				responseMap.put(MyPriceConstants.RESPONSE_STATUS, result);
				if (result) {
					// removing site details
					List<Long> nxSiteIds = (List<Long>) paramMap.get(MyPriceConstants.CURRENT_NX_SITE_ID);
					Long nxTxnId = createTransactionResponse.getNxTransacId();
					logger.info("Calling deleteSiteFromSiteDic for nxTxnId : {}",
							nxTxnId + " and nxSiteId : " + nxSiteIds.toString());
					myPriceTransactionUtil.deleteSiteFromSiteDic(nxTxnId, nxSiteIds);
				}
			}
			// if designModifiedInd is Y or empty then we need to check designStatus
			else if ((designModifiedInd.isEmpty() || StringConstants.CONSTANT_Y.equalsIgnoreCase(designModifiedInd))
					&& StringConstants.DESIGN_UPDATE.equalsIgnoreCase(designStatus)) {
				// design update scenario. We will remove existing document numbers and call
				// config solution, design and price update
				boolean result = myPriceTransactionUtil.removeTransactionServiceLineImpl(designMap);
				responseMap.put(MyPriceConstants.RESPONSE_STATUS, result);
				if (result) {
					responseMap = configSolutionDesign(requestMap, MyPriceConstants.AUDIT_CONFIG_SOLN_DESIGN_REST_RC,
							nxDesign, inputDesign);
					result = responseMap.containsKey(MyPriceConstants.RESPONSE_STATUS)
							? (boolean) responseMap.get(MyPriceConstants.RESPONSE_STATUS)
							: false;
					if (result) {
						// design ids for updatePricing
						if(!(MyPriceConstants.ADE_OFFER_NAME.equalsIgnoreCase(offerName)&& MyPriceConstants.CIRCUIT_TYPE_STANDARD.equalsIgnoreCase(nxDesign.getDesignType()))) {
							nxDesignIdListToUpdate.add(nxDesign.getNxDesignId());
						}
						paramMap.put(MyPriceConstants.DESIGN_ID_FOR_UPDATE_PRICING, nxDesignIdListToUpdate);
						// get the part number details
						if ( StringConstants.OFFERNAME_ASENOD.equalsIgnoreCase(offerName) && 
								MyPriceConstants.ASENOD_3PA.equalsIgnoreCase(subOfferName)) {
							String nxSiteId= getNXSiteId(nxDesign);
							processConfigSolDesignResponse(paramMap,createTransactionResponse, offerName,subOfferName,nxSolutionId,null,nxSiteId,offerList,null);

						}else {
							processConfigSolDesignResponse(paramMap,createTransactionResponse, offerName,subOfferName,nxSolutionId,asrItemId,null,offerList,null);
						}
					}
				}
			}
			// Price update scenario
			else {
				// Price update scenario with transaction Update reconfigure
				// design ids for updatePricing
				nxDesignIdListUpdateReconfigure.add(nxDesign.getNxDesignId());
				paramMap.put(MyPriceConstants.DESIGN_ID_FOR_UPDATE_PRICING_RECONFIGURE, nxDesignIdListUpdateReconfigure);
				responseMap.put(StringConstants.TRANSACTION_UPDATE, StringConstants.RECONFIGURE);
				responseMap.put(MyPriceConstants.RESPONSE_STATUS,true);
			}
		}
		return responseMap;
	}
	
	public Map<String, Object> restConfigAndDesignConsolidation(CreateTransactionResponse createTransactionResponse,
			List<NxDesign> nxDesignList, Map<String, Object> paramMap, List<JsonNode> siteJsonList,List<String> asrItemIdList) {
		Map<String, Object> responseMap = new HashMap<>();
		boolean automationInd = paramMap.get(MyPriceConstants.AUTOMATION_IND) != null
				? (boolean) paramMap.get(MyPriceConstants.AUTOMATION_IND)
				: false;
		String priceUpdate = paramMap.containsKey(MyPriceConstants.PRICE_UPDATE)
				? paramMap.get(MyPriceConstants.PRICE_UPDATE).toString()
				: null;
		String contractTerm = paramMap.containsKey(MyPriceConstants.CONTRACT_TERM)
				? paramMap.get(MyPriceConstants.CONTRACT_TERM).toString()
				: null;

		String subOfferName = paramMap.containsKey(MyPriceConstants.SUB_OFFER_NAME)
				? (String) paramMap.get(MyPriceConstants.SUB_OFFER_NAME)
				: "";
		String offerName = paramMap.containsKey(MyPriceConstants.OFFER_NAME)
				? (String) paramMap.get(MyPriceConstants.OFFER_NAME)
				: "";
		String flowType = offerName;
		Long nxSolutionId = paramMap.containsKey(MyPriceConstants.NX_SOLIUTION_ID)
				? Long.valueOf(paramMap.get(MyPriceConstants.NX_SOLIUTION_ID).toString())
				: 0L;
		String restVersion = (String) paramMap.get(StringConstants.REST_VERSION);
				
		List<NxDesignDetails> nxDesignDetailsList = nxDesignList.stream()
				.flatMap(nxDesign -> nxDesign.getNxDesignDetails().stream()).collect(Collectors.toList());
		List<String> consolidatedDataList = new ArrayList<>();
		List<Long> nxDesignDetailId = new ArrayList<>();
		for (NxDesignDetails row : nxDesignDetailsList) {
			String consolidationCriteriaData=row.getConsolidationCriteriaData();
			String designData=row.getDesignData();
			String mergedata=this.appendDesignAndConsolidationData(designData,consolidationCriteriaData);
			consolidatedDataList.add(mergedata);
			nxDesignDetailId.add(row.getNxDesignId());
		}
		JsonNode designDataArray = mapper.createArrayNode();
		try {
			designDataArray = mapper.readTree(consolidatedDataList.toString());
		} catch (IOException e) {
			logger.error("Exception", e);
		}
		ObjectNode inputDesignJson = mapper.createObjectNode();
		inputDesignJson.set("site", designDataArray);
		logger.info("consolidate design data: {}\nconsolidation key: {}\nnxDesignDetailId are: {}", inputDesignJson,
				nxDesignDetailsList.get(0).getConsolidationCriteria(), nxDesignDetailId);
		String inputDesign = inputDesignJson.toString();
		//logger.info("Design data send to rest API :==>> {} ", inputDesign);
		String transactionType = paramMap.containsKey(StringConstants.TRANSACTION_TYPE)
				? paramMap.get(StringConstants.TRANSACTION_TYPE).toString()
				: null;

		List<Long> nxDesignIdListToUpdate = paramMap.containsKey(MyPriceConstants.DESIGN_ID_FOR_UPDATE_PRICING)?
				(List<Long>)paramMap.get(MyPriceConstants.DESIGN_ID_FOR_UPDATE_PRICING): new ArrayList<Long>();
	
		List<Long> nxDesignIdListUpdateReconfigure = paramMap.containsKey(MyPriceConstants.DESIGN_ID_FOR_UPDATE_PRICING_RECONFIGURE)?
						(List<Long>)paramMap.get(MyPriceConstants.DESIGN_ID_FOR_UPDATE_PRICING_RECONFIGURE): new ArrayList<Long>();
			
		//if (StringConstants.TRANSACTION_TYPE_NEW.equalsIgnoreCase(transactionType)) {
			Map<String, Object> requestMap = new HashMap<String, Object>();
			requestMap.put(StringConstants.REST_VERSION, restVersion);
			requestMap.put(MyPriceConstants.PRODUCT_TYPE, MyPriceConstants.SOURCE_PD);
			requestMap.put(MyPriceConstants.NX_SOLIUTION_ID, nxSolutionId);
			requestMap.put(CustomJsonConstants.BS_ID,
					createTransactionResponse.getMyPriceTransacId() != null
							? Long.valueOf(createTransactionResponse.getMyPriceTransacId())
							: 0l);
			requestMap.put(MyPriceConstants.MP_TRANSACTION_ID,
					createTransactionResponse.getMyPriceTransacId() != null
							? Long.valueOf(createTransactionResponse.getMyPriceTransacId())
							: 0l);
			requestMap.put(CustomJsonConstants.DOCUMENT_ID, Long.valueOf(documentId));
			requestMap.put(StringConstants.PRICE_SCENARIO_ID,
					createTransactionResponse.getPriceScenarioId().toString());

			if (MyPriceConstants.ASE_OFFER_NAME.equalsIgnoreCase(offerName)) {
				requestMap.put(MyPriceConstants.OFFER_NAME, offerName);
			}
			if (MyPriceConstants.ASENOD_OFFER_NAME.equalsIgnoreCase(offerName)) {
				requestMap.put(MyPriceConstants.OFFER_NAME, offerName);
				requestMap.put(MyPriceConstants.SUB_OFFER_NAME, subOfferName);
			}
			if (MyPriceConstants.ADE_OFFER_NAME.equalsIgnoreCase(offerName)) {
				requestMap.put(MyPriceConstants.OFFER_NAME, offerName);
			}
			if (MyPriceConstants.EPLSWAN_OFFER_NAME.equalsIgnoreCase(offerName)||MyPriceConstants.ETHERNET_OFFER_NAME.equalsIgnoreCase(offerName)) {
				requestMap.put(MyPriceConstants.OFFER_NAME, offerName);
				requestMap.put(MyPriceConstants.SUB_OFFER_NAME, subOfferName);
			}
			responseMap = configSolutionDesignConsolidation(requestMap, MyPriceConstants.AUDIT_CONFIG_SOLN_DESIGN_REST, nxDesignList,
					inputDesign);
			if (responseMap.containsKey(MyPriceConstants.RESPONSE_STATUS)
					&& (boolean) responseMap.get(MyPriceConstants.RESPONSE_STATUS)) {
				// design ids for updatePricing
				nxDesignList.forEach(nxDesign -> {
					if(!(MyPriceConstants.ETHERNET_OFFER_NAME.equalsIgnoreCase(nxDesign.getBundleCd())||MyPriceConstants.EPLSWAN_OFFER_NAME.equalsIgnoreCase(nxDesign.getBundleCd()))) {
						nxDesignIdListToUpdate.add(nxDesign.getNxDesignId());
					}
				});
				paramMap.put(MyPriceConstants.DESIGN_ID_FOR_UPDATE_PRICING, nxDesignIdListToUpdate);
			}
		//} 
		 if (StringConstants.TRANSACTION_TYPE_EXISTING.equalsIgnoreCase(transactionType) && (responseMap.containsKey(MyPriceConstants.RESPONSE_STATUS)
					&& (boolean) responseMap.get(MyPriceConstants.RESPONSE_STATUS))) {
			// xy3208 keep the TRANSACTION_TYPE_EXISTING logic branch here to be in line with the existing flow, in case we need to move logic here
			 //get Txn line api
			 List<String> offerList=new ArrayList<String>();
			 offerList.add(offerName.toUpperCase());
			 if ((StringConstants.OFFERNAME_ASENOD.equalsIgnoreCase(offerName) && 
						MyPriceConstants.ASENOD_3PA.equalsIgnoreCase(subOfferName))||(StringConstants.OFFERNAME_EPLSWAN.equalsIgnoreCase(offerName))||(StringConstants.OFFERNAME_ETHERNET.equalsIgnoreCase(offerName))) {// add eplswan/ethernet offer
					String circuitId= nxDesignList.stream().filter(item-> item.getCircuitId() != null).map(n -> n.getCircuitId().toString()).collect(Collectors.joining(","));
					String nxSiteId= nxDesignList.stream().map(n -> n.getSiteId().toString()).collect(Collectors.joining(","));
					paramMap.put(MyPriceConstants.PRODUCT_TYPE, nxDesignList.get(0).getBundleCd());
					processConfigSolDesignResponse(paramMap,createTransactionResponse, offerName,subOfferName,nxSolutionId,null,nxSiteId,offerList,circuitId);

				}else {
					String asrItemId = nxDesignList.stream().map(n -> n.getAsrItemId()).collect(Collectors.joining(","));
					processConfigSolDesignResponse(paramMap,createTransactionResponse, offerName,subOfferName,nxSolutionId,asrItemId,null,offerList,null);
				}	
		 }
		return responseMap;
	}
	
	protected String appendDesignAndConsolidationData(String designData, String consolidationCriteriaData) {
		StringBuilder mergedJson = new StringBuilder();
		mergedJson.append(designData.substring(0, designData.length() - 1));
		mergedJson.append(",");
		mergedJson.append(consolidationCriteriaData.substring(1));
		return mergedJson.toString();
	}

	public void processConfigSolDesignResponse(Map<String, Object> requestMap,CreateTransactionResponse createTransactionResponse, String offerName,
		String subOfferName,long nxSolutionId, String siteAsrItemId,String siteNxSiteId,List<String> offerList,String circuitId) {
		String mpTransactionId = createTransactionResponse.getMyPriceTransacId() != null
				? createTransactionResponse.getMyPriceTransacId()
				: "";
		Long nxTransactionId = createTransactionResponse.getNxTransacId();
		boolean hasMore = false;
		Long offset = 0L;
		GetTransactionLineResponse response = null;
		
		Long currentTime = System.currentTimeMillis();
		Long getTransactionRestStartTime = System.currentTimeMillis() - currentTime;
		Map<String, Object> asrItemIdDesignMap = getDesignAsrItemIdDetail(nxSolutionId);
		Map<String, Object> nxsiteIDDesignMap = getDesignNxSiteIdDetail(nxSolutionId);
		Map<String, Map<String,Object>> asrItemIdCircuitdetailADEMap=new HashMap<>();
		Map<String, Map<String,Object>> asrItemIdCircuitdetailEplsMap=new HashMap<>();
		if (offerList.contains(StringConstants.OFFERNAME_ADE.toUpperCase() )) {
			collectADECircuitDetails(createTransactionResponse,asrItemIdCircuitdetailADEMap,nxSolutionId,offerList);
		}
		if (offerList.contains(MyPriceConstants.EPLSWAN_OFFER_NAME.toUpperCase())||offerList.contains(MyPriceConstants.ETHERNET_OFFER_NAME.toUpperCase())) {
			collectEplsCircuitDetails(createTransactionResponse,asrItemIdCircuitdetailEplsMap,nxSolutionId,offerList);
		}
		Map<String, Map<String, List<NxMpDesignDocument>>> productLineDetailMap = new HashMap<>();
		Map<String, String> mpSolutionIdMap = new HashMap<String, String>();
		try {
			String restVersion = (requestMap != null && requestMap.containsKey(StringConstants.REST_VERSION)) ? (String) requestMap.get(StringConstants.REST_VERSION) : null;
			do {
				if (siteAsrItemId == null && siteNxSiteId == null) {
					// get details of all productline corresponding to that transaction
					response = getTransactionLineServiceImpl.getTransactionLineConfigDesignSolution(mpTransactionId,
							offerName, offset, requestMap);
				}
				else if(siteAsrItemId!=null || siteNxSiteId!=null ){
					//for ASE , ASEod IR and ADE design Id based on asrItemId
					if(MyPriceConstants.ASE_OFFER_NAME.equalsIgnoreCase(offerName) || 
							(MyPriceConstants.ASENOD_OFFER_NAME.equalsIgnoreCase(offerName) &&	MyPriceConstants.ASENOD_IR.equalsIgnoreCase(subOfferName))
							||	MyPriceConstants.ADE_OFFER_NAME.equalsIgnoreCase(offerName)) {
						// get details of all productline corresponding to that asrItemId for ASE,ASENoD IR,ADE
						response = getTransactionLineServiceImpl.getTransactionLineConfigDesignBasedOnField(mpTransactionId,
								offerName,subOfferName, offset, siteAsrItemId, requestMap,circuitId);
					}
					//for  ASEod 3PA design Id based on nxsiteId
					if((MyPriceConstants.ASENOD_OFFER_NAME.equalsIgnoreCase(offerName) && 
							MyPriceConstants.ASENOD_3PA.equalsIgnoreCase(subOfferName))||MyPriceConstants.EPLSWAN_OFFER_NAME.equalsIgnoreCase(offerName)
							||MyPriceConstants.ETHERNET_OFFER_NAME.equalsIgnoreCase(offerName) ) {
						// get details of all productline corresponding to that nxSiteId for ASENoD 3PA
						response = getTransactionLineServiceImpl.getTransactionLineConfigDesignBasedOnField(mpTransactionId,
								offerName,subOfferName, offset, siteNxSiteId, requestMap,circuitId);
					}
				}
				hasMore = response.isHasMore();
				if(response.getLimit() != null) {
					offset = offset + response.getLimit();
				}
				Map<String, List<NxMpDesignDocument>> mpProductLineIdMap = null;
				List<NxMpDesignDocument> documentNumberdetailList = null;
				if (response != null && CollectionUtils.isNotEmpty(response.getItems())) {
					List<GetTransactionLineItem> itemList = response.getItems();
					for (GetTransactionLineItem transLineItem : itemList) {
						String documentNumber = transLineItem.getDocumentNumber();
						String parentDocumentNumber = transLineItem.getParentDocNumber();
						String usocId = transLineItem.getUsocId();
						String lineBomId = transLineItem.getBomId();
						String lineBomPartNumber = transLineItem.getBomPartNumber();
						String lineBomParentId = transLineItem.getBomParentId();
						String parentlineItem = transLineItem.getParentLineitem();
						String modelAsr = transLineItem.getModelAsr();
						String asrItemVal = transLineItem.getAsrItemId();
						String cicuitNumber = transLineItem.getCircuitNumber();
						String asrItemId = asrItemVal != null ? asrItemVal : (modelAsr != null ? modelAsr : null);

						String nxSiteIdval = transLineItem.getNxSiteId();
						String modelSites=transLineItem.getModelSites();
						String nxsiteId = modelSites != null ? modelSites : (nxSiteIdval != null ? nxSiteIdval : null);
						
						String designDetailIdentifier=null;
						Long nxDesignId = null;
						//for ASE ,ADE design Id based on asrItemId
						if(offerList.contains(MyPriceConstants.ASE_OFFER_NAME.toUpperCase()) || 
								offerList.contains(MyPriceConstants.ADE_OFFER_NAME.toUpperCase())||offerList.contains(MyPriceConstants.EPLSWAN_OFFER_NAME.toUpperCase()) || 
								offerList.contains(MyPriceConstants.ETHERNET_OFFER_NAME.toUpperCase())) {
							if(null!=parentlineItem) {
								parentlineItem=parentlineItem.replaceAll("\\s+","");
								if(parentlineItem.equalsIgnoreCase(MyPriceConstants.ASE_OFFER_NAME)||parentlineItem.equalsIgnoreCase(MyPriceConstants.ADE_OFFER_NAME)) {
									if(asrItemId!=null) {
										designDetailIdentifier=asrItemId;
										nxDesignId = (asrItemIdDesignMap != null && asrItemIdDesignMap.containsKey(asrItemId))
														? (Long) asrItemIdDesignMap.get(asrItemId) : null;
									}
												
								}
								else if(parentlineItem.equalsIgnoreCase(MyPriceConstants.EPLSWAN_OFFER_NAME)||parentlineItem.equalsIgnoreCase(MyPriceConstants.LocalAccess)) {
									if(nxsiteId!=null) {
										designDetailIdentifier=nxsiteId+"#"+parentlineItem+"#"+cicuitNumber;
										if (nxsiteIDDesignMap != null && nxsiteIDDesignMap.containsKey(nxsiteId+"#"+parentlineItem+"#"+cicuitNumber)) {//question same circuit number i have to add which we are getting from myprice
											nxDesignId = (Long) nxsiteIDDesignMap.get(nxsiteId+"#"+parentlineItem+"#"+cicuitNumber);
										}
									}
								}
							}
						}

						//for  ASEod 3PA design Id based on nxsiteId and ASEod IR  based on asrItemId
						if(MyPriceConstants.ASENOD_OFFER_NAME.equalsIgnoreCase(offerName)) {
							// myPrice may return string "null", to avoid setting nxDesignId back to null
							// put the assignment in if
							if(nxsiteId!=null) {
								designDetailIdentifier=nxsiteId;
								if (nxsiteIDDesignMap != null && nxsiteIDDesignMap.containsKey(nxsiteId)) {
									nxDesignId = (Long) nxsiteIDDesignMap.get(nxsiteId);
								}
//								nxDesignId = (nxsiteIDDesignMap != null && nxsiteIDDesignMap.containsKey(nxsiteId))
//										? (Long) nxsiteIDDesignMap.get(nxsiteId) : null;
							}
							if(asrItemId!=null) {
								designDetailIdentifier=asrItemId;
								if (asrItemIdDesignMap != null && asrItemIdDesignMap.containsKey(asrItemId)) {
									nxDesignId = (Long) asrItemIdDesignMap.get(asrItemId);
								}
//								nxDesignId = (asrItemIdDesignMap != null && asrItemIdDesignMap.containsKey(asrItemId))
//										? (Long) asrItemIdDesignMap.get(asrItemId) : null;
							}
						}
						NxMpDesignDocument nxMpDesignDocument=null;
						// for solution
						if ("BOM_Solution".equalsIgnoreCase(lineBomId)) {
							if (!productLineDetailMap.containsKey(documentNumber)) {
								productLineDetailMap.put(documentNumber,
										new HashMap<String, List<NxMpDesignDocument>>());
							}
						} else if ("Solution".equalsIgnoreCase(parentlineItem)
								|| "BOM_Solution".equalsIgnoreCase(lineBomParentId)) {
							mpProductLineIdMap = productLineDetailMap.get(parentDocumentNumber);
							/**removing designDetailIdentifier null check as both the v1 and v2 has same response structure 
							 * if (!mpProductLineIdMap.containsKey(documentNumber) && StringConstants.VERSION_2.equalsIgnoreCase(restVersion)) {
								documentNumberdetailList = new ArrayList<>();
								mpProductLineIdMap.put(documentNumber, documentNumberdetailList);
								mpSolutionIdMap.put(parentDocumentNumber, documentNumber);
							}else if (!mpProductLineIdMap.containsKey(documentNumber) && designDetailIdentifier != null) {
								documentNumberdetailList = new ArrayList<>();
								mpProductLineIdMap.put(documentNumber, documentNumberdetailList);
								mpSolutionIdMap.put(parentDocumentNumber, documentNumber);
							}*/
							if (!mpProductLineIdMap.containsKey(documentNumber)) {
								documentNumberdetailList = new ArrayList<>();
								mpProductLineIdMap.put(documentNumber, documentNumberdetailList);
								mpSolutionIdMap.put(parentDocumentNumber, documentNumber);
							}
						}

						// for design
						if (!("Solution".equalsIgnoreCase(lineBomPartNumber)
								|| "Solution".equalsIgnoreCase(parentlineItem)) && designDetailIdentifier != null) {
							String mpSolutionIdVal = getKey(mpSolutionIdMap, parentDocumentNumber);
							mpProductLineIdMap=productLineDetailMap.get(mpSolutionIdVal);
							documentNumberdetailList = mpProductLineIdMap.get(parentDocumentNumber);
							String derivedComponentid=null;
							if(MyPriceConstants.ADE_OFFER_NAME.equalsIgnoreCase(parentlineItem)) {
								Map<String,Object> circuitdetailForADE =asrItemIdCircuitdetailADEMap.get(designDetailIdentifier);
								derivedComponentid=this.derivedComponentIdForAde(circuitdetailForADE,transLineItem);
							}
							if(MyPriceConstants.EPLSWAN_OFFER_NAME.equalsIgnoreCase(parentlineItem) 
									|| MyPriceConstants.LocalAccess.equalsIgnoreCase(parentlineItem)) {
								Map<String,Object> circuitdetailForEpls =asrItemIdCircuitdetailEplsMap.get(designDetailIdentifier);
								derivedComponentid=this.derivedComponentIdForEpls(circuitdetailForEpls,transLineItem);
							}
								nxMpDesignDocument = new NxMpDesignDocument();
								nxMpDesignDocument.setMpDocumentNumber(Long.valueOf(documentNumber));
								nxMpDesignDocument.setMpProductLineId(parentDocumentNumber);
								nxMpDesignDocument.setMpSolutionId(mpSolutionIdVal);
								nxMpDesignDocument.setUsocId(usocId);
								nxMpDesignDocument.setActiveYN(com.att.sales.nexxus.common.CommonConstants.ACTIVE_Y);
								nxMpDesignDocument.setCreatedDate(new Date());
								nxMpDesignDocument.setNxTxnId(nxTransactionId);
								nxMpDesignDocument.setNxDesignId(nxDesignId);
								nxMpDesignDocument.setMpPartNumber(derivedComponentid);
								documentNumberdetailList.add(nxMpDesignDocument);
						}
					}
				}
				// save the product line details in nx_mpDesignDocument
				if (MapUtils.isNotEmpty(productLineDetailMap)) {
					for (Map.Entry<String, Map<String, List<NxMpDesignDocument>>> mpsolutionId : productLineDetailMap
							.entrySet()) {
						Map<String, List<NxMpDesignDocument>> mpProductLineIdMapValue = mpsolutionId.getValue();
						if (MapUtils.isNotEmpty(mpProductLineIdMapValue)) {
							for (Map.Entry<String, List<NxMpDesignDocument>> mpProuctLineEntry : mpProductLineIdMapValue
									.entrySet()) {
								//documentListToSave = mpProuctLineEntry.getValue();
								if (CollectionUtils.isNotEmpty( mpProuctLineEntry.getValue())) {
									nxMpDesignDocumentRepo.saveAll( mpProuctLineEntry.getValue());
								}
							}
						}
					}
				}
				
			} while (hasMore);
			printTotalDuration(currentTime, getTransactionRestStartTime, StringConstants.GET_TRANSACTION_LINE_REST,nxSolutionId);
		} catch (SalesBusinessException e) {
			logger.info("Error during getTransactionLineServiceImpl.getTransactionLineConfigDesignSolution {}",
					e.getMessage());
		}
	}

	protected <K, V> K getKey(Map<K, V> map, V value) {
		if (MapUtils.isNotEmpty(map) && value != null) {
			return map.entrySet().stream().filter(entry -> value.equals(entry.getValue())).map(Map.Entry::getKey)
					.findFirst().get();
		}
		return null;
	}

	protected Map<String, Object> getDesignAsrItemIdDetail(long nxSolutionId) {
		// Map of asrItemId corresponding to design
		Map<String, Object> nxAsrItemIdDesignMap = new HashMap<>();
		NxSolutionDetail nxSolutionDetail = nxSolutionDetailsRepository.findByNxSolutionId(nxSolutionId);
		if (nxSolutionDetail != null) {
			List<NxDesign> nxDesignList = nxDesignRepository.findByNxSolutionDetail(nxSolutionDetail);
			if (CollectionUtils.isNotEmpty(nxDesignList)) {
				for (NxDesign nxDesignDetails : nxDesignList) {
						if(!(MyPriceConstants.CIRCUIT_TYPE_ATX.equalsIgnoreCase(nxDesignDetails.getDesignType())
								||nxDesignDetails.getBundleCd().equalsIgnoreCase(MyPriceConstants.EPLSWAN_OFFER_NAME)
								||nxDesignDetails.getBundleCd().equalsIgnoreCase(MyPriceConstants.ETHERNET_OFFER_NAME))){
							nxAsrItemIdDesignMap.put(nxDesignDetails.getAsrItemId(), nxDesignDetails.getNxDesignId());
						
						}
				}
			}
		}
		return nxAsrItemIdDesignMap;
	}
	
	protected Map<String, Object> getDesignNxSiteIdDetail(long nxSolutionId) {
		// Map of asrItemId corresponding to design
		Map<String, Object> nxSiteIdDesignMap = new HashMap<>();
		NxSolutionDetail nxSolutionDetail = nxSolutionDetailsRepository.findByNxSolutionId(nxSolutionId);
		if (nxSolutionDetail != null) {
			List<NxDesign> nxDesignList = nxDesignRepository.findByNxSolutionDetail(nxSolutionDetail);
			if (CollectionUtils.isNotEmpty(nxDesignList)) {
				for (NxDesign nxDesignDetails : nxDesignList) {
					if(nxDesignDetails.getSiteId()!=null) {
						if(nxDesignDetails.getBundleCd().equalsIgnoreCase(MyPriceConstants.EPLSWAN_OFFER_NAME)) {
							nxSiteIdDesignMap.put(String.valueOf(nxDesignDetails.getSiteId())+"#"+nxDesignDetails.getBundleCd()+"#"+nxDesignDetails.getCircuitId(), nxDesignDetails.getNxDesignId());
						}
						else if(nxDesignDetails.getBundleCd().equalsIgnoreCase(MyPriceConstants.ETHERNET_OFFER_NAME)) {
							nxSiteIdDesignMap.put(String.valueOf(nxDesignDetails.getSiteId())+"#"+MyPriceConstants.LocalAccess+"#"+nxDesignDetails.getCircuitId(), nxDesignDetails.getNxDesignId());
						}
						else {
							nxSiteIdDesignMap.put(String.valueOf(nxDesignDetails.getSiteId()), nxDesignDetails.getNxDesignId());
						}
					}
				}
			}
		}
		return nxSiteIdDesignMap;
	}
	protected Map<String, Object> getErrorDetails(Map<String, Object> requestMap) {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		responseMap.put(MyPriceConstants.RESPONSE_STATUS,
				(requestMap.containsKey(MyPriceConstants.RESPONSE_STATUS)
						&& requestMap.get(MyPriceConstants.RESPONSE_STATUS) != null)
								? (boolean) requestMap.get(MyPriceConstants.RESPONSE_STATUS)
								: false);
		responseMap.put(CustomJsonConstants.ERROR_WS_NAME,
				(requestMap.containsKey(CustomJsonConstants.ERROR_WS_NAME)
						&& requestMap.get(CustomJsonConstants.ERROR_WS_NAME) != null)
								? (String) requestMap.get(CustomJsonConstants.ERROR_WS_NAME)
								: null);
		if (requestMap.containsKey(CustomJsonConstants.REST_RESPONSE_ERROR)) {
			responseMap.put(CustomJsonConstants.REST_RESPONSE_ERROR,
					requestMap.get(CustomJsonConstants.REST_RESPONSE_ERROR));
		}
		if (requestMap.containsKey(CustomJsonConstants.CONFIG_BOM_ERROR_DATA)) {
			responseMap.put(CustomJsonConstants.CONFIG_BOM_ERROR_DATA,
					requestMap.get(CustomJsonConstants.CONFIG_BOM_ERROR_DATA));
		}
		if (requestMap.containsKey(CustomJsonConstants.SITE_CONFIG_ERROR_MAP)) {
			responseMap.put(CustomJsonConstants.SITE_CONFIG_ERROR_MAP,
					requestMap.get(CustomJsonConstants.SITE_CONFIG_ERROR_MAP));
		}
		return responseMap;
	}

	public Map<String, Object> configSolutionDesign(Map<String, Object> requestMap, String transactionType,
			NxDesign nxDesign, String inputDesign) {
		logger.info("Start -- configSolutionDesign for rest");
		Map<String, Object> responseMap = new HashMap<>();
		configAndUpdateRestProcessingService.callMpConfigAndUpdate(requestMap, inputDesign);
		responseMap.put(MyPriceConstants.REST_MP_CONFIG, true);
		responseMap.put(MyPriceConstants.TRANSACTION_TYPE, transactionType);
		if ((requestMap.containsKey(MyPriceConstants.RESPONSE_STATUS)
				&& !(Boolean) requestMap.get(MyPriceConstants.RESPONSE_STATUS))
				|| (requestMap.containsKey(CustomJsonConstants.CONFIG_BOM_ERROR)
						&& requestMap.get(CustomJsonConstants.CONFIG_BOM_ERROR) != null
						&& (Boolean) requestMap.get(CustomJsonConstants.CONFIG_BOM_ERROR))
				|| (requestMap.containsKey(CustomJsonConstants.SITE_CONFIG_ERROR)
						&& requestMap.get(CustomJsonConstants.SITE_CONFIG_ERROR) != null
						&& (Boolean) requestMap.get(CustomJsonConstants.SITE_CONFIG_ERROR))) {
			responseMap.put(MyPriceConstants.RESPONSE_STATUS, false);
			responseMap.put(MyPriceConstants.REST_ERROR_MSG, getErrorDetails(requestMap).toString());
		} else {
			// successfully configured
			responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
			responseMap.put(MyPriceConstants.NX_DESIGN_ID_SUCCESSFULLY_CONFIGURED, nxDesign.getNxDesignId());
		}
		return responseMap;
	}
	
	public Map<String, Object> configSolutionDesignConsolidation(Map<String, Object> requestMap, String transactionType,
			List<NxDesign> nxDesignList, String inputDesign) {
		logger.info("Start -- configSolutionDesignConsolidation for rest");
		Map<String, Object> responseMap = new HashMap<>();
		configAndUpdateRestProcessingService.callMpConfigAndUpdate(requestMap, inputDesign);
		responseMap.put(MyPriceConstants.REST_MP_CONFIG, true);
		responseMap.put(MyPriceConstants.TRANSACTION_TYPE, transactionType);
		if ((requestMap.containsKey(MyPriceConstants.RESPONSE_STATUS)
				&& !(Boolean) requestMap.get(MyPriceConstants.RESPONSE_STATUS))
				|| (requestMap.containsKey(CustomJsonConstants.CONFIG_BOM_ERROR)
						&& requestMap.get(CustomJsonConstants.CONFIG_BOM_ERROR) != null
						&& (Boolean) requestMap.get(CustomJsonConstants.CONFIG_BOM_ERROR))
				|| (requestMap.containsKey(CustomJsonConstants.SITE_CONFIG_ERROR)
						&& requestMap.get(CustomJsonConstants.SITE_CONFIG_ERROR) != null
						&& (Boolean) requestMap.get(CustomJsonConstants.SITE_CONFIG_ERROR))) {
			responseMap.put(MyPriceConstants.RESPONSE_STATUS, false);
			responseMap.put(MyPriceConstants.REST_ERROR_MSG, getErrorDetails(requestMap).toString());
		} else {
			// successfully configured
			responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
			//xy3208 this map key NX_DESIGN_ID_SUCCESSFULLY_CONFIGURED only used to check if not equals 0, then set NxDesignDetails status to success
			//this key is read in PedSnsdServiceUtil.callRestConfigSolAndDeign and PedSnsdServiceUtil.callRestConfigSolAndDeignConsolidation
			responseMap.put(MyPriceConstants.NX_DESIGN_ID_SUCCESSFULLY_CONFIGURED, nxDesignList.get(0).getNxDesignId()); 
		}
		return responseMap;
	}

	public Map<String, Object> callUpdatePricing(Map<String, Object> designMap, String transactionType) {
		logger.info("Start -- callUpdatePricing for rest");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {

			updateTransactionPricingServiceForSolutionImpl.updateTransactionPricingRequest(designMap);
			boolean status = (designMap.containsKey(MyPriceConstants.RESPONSE_STATUS)
					&& designMap.get(MyPriceConstants.RESPONSE_STATUS) != null)
							? (boolean) designMap.get(MyPriceConstants.RESPONSE_STATUS)
							: false;
			if (status) {
				responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
			} else {
				responseMap.put(MyPriceConstants.RESPONSE_STATUS, false);
			}
			responseMap.put(MyPriceConstants.TRANSACTION_TYPE, transactionType);
			List<String> updatePricingUnmatchingAsrIdsList = designMap
					.containsKey(MyPriceConstants.UPDATE_PRICING_UNMATCHING_ASR_IDS)
							? (List<String>) designMap.get(MyPriceConstants.UPDATE_PRICING_UNMATCHING_ASR_IDS)
							: null;
			List<String> updatePricingUnmatchingNxDesignIdsList = designMap
					.containsKey(MyPriceConstants.UPDATE_PRICING_UNMATCHING_NX_DESIGN_IDS)
							? (List<String>) designMap.get(MyPriceConstants.UPDATE_PRICING_UNMATCHING_NX_DESIGN_IDS)
							: null;
			responseMap.put(MyPriceConstants.UPDATE_PRICING_UNMATCHING_ASR_IDS, updatePricingUnmatchingAsrIdsList);
			responseMap.put(MyPriceConstants.UPDATE_PRICING_UNMATCHING_NX_DESIGN_IDS,
					updatePricingUnmatchingNxDesignIdsList);
			if ((designMap.containsKey(MyPriceConstants.RESPONSE_MSG)
					&& designMap.get(MyPriceConstants.RESPONSE_MSG) != null)) {
				responseMap.put(MyPriceConstants.RESPONSE_MSG, designMap.get(MyPriceConstants.RESPONSE_MSG));
			}

		} catch (SalesBusinessException e) {
			logger.info("Error during callUpdatePricing {}", e.getMessage());
		}
		logger.info("End -- callUpdatePricing");
		return responseMap;
	}
	
	protected void collectADECircuitDetails(CreateTransactionResponse createTransactionResponse,Map<String, Map<String,Object>> asrItemIdCircuitdetailADEMap,
			long nxSolutionId, List<String> offerList) {
		NxSolutionDetail nxSolutionDetail = nxSolutionDetailsRepository.findByNxSolutionId(nxSolutionId);
		if (nxSolutionDetail != null) {
			List<NxDesign> nxDesignList = nxDesignRepository.findByNxSolutionDetail(nxSolutionDetail);
			if(offerList.contains(MyPriceConstants.ADE_OFFER_NAME.toUpperCase())) {
				if (CollectionUtils.isNotEmpty(nxDesignList)) {
					for (NxDesign nxDesign : nxDesignList) {
						 Map<String,Object> requestMap = new HashMap<>();
						 JSONObject designDetails=JacksonUtil.toJsonObject(nxDesign.getNxDesignDetails().get(0).getDesignData());
						 requestMap.put(StringConstants.PRICE_SCENARIO_ID,
									createTransactionResponse.getPriceScenarioId().toString());
						 this.collectCircuitDetailsForAde(designDetails, offerList, requestMap);
						 asrItemIdCircuitdetailADEMap.put(nxDesign.getAsrItemId(), requestMap);	
					}
				}
			}
			
		}
	}
	protected void collectEplsCircuitDetails(CreateTransactionResponse createTransactionResponse,Map<String, Map<String,Object>> asrItemIdCircuitdetailEplsMap,
			long nxSolutionId,List<String> offerName ) {
		NxSolutionDetail nxSolutionDetail = nxSolutionDetailsRepository.findByNxSolutionId(nxSolutionId);
		if (nxSolutionDetail != null) {
			List<NxDesign> nxDesignList = nxDesignRepository.findByNxSolutionDetail(nxSolutionDetail);
			if(offerName.contains(MyPriceConstants.EPLSWAN_OFFER_NAME.toUpperCase())||offerName.contains(MyPriceConstants.ETHERNET_OFFER_NAME.toUpperCase()) ) {
				if (CollectionUtils.isNotEmpty(nxDesignList)) {
					for (NxDesign nxDesign : nxDesignList) {
						 Map<String,Object> requestMap = new HashMap<>();
						 JSONObject designDetails=JacksonUtil.toJsonObject(nxDesign.getNxDesignDetails().get(0).getDesignData());
						 requestMap.put(StringConstants.PRICE_SCENARIO_ID,
									createTransactionResponse.getPriceScenarioId().toString());
							 this.collectCircuitDetailsForEpls(designDetails, offerName, requestMap);
							 String product=MyPriceConstants.ETHERNET_OFFER_NAME.equals(nxDesign.getBundleCd())?MyPriceConstants.LocalAccess:nxDesign.getBundleCd();
							 asrItemIdCircuitdetailEplsMap.put(nxDesign.getSiteId()+"#"+product+"#"+nxDesign.getCircuitId(), requestMap);
					}
				}
			}
			
		}
	}
	protected String getNXSiteId(NxDesign nxDesign) {
		//to get the detail
		NxDesignDetails nxDesignDetails = nxDesign.getNxDesignDetails().get(0);
		JsonNode currentDesignDetails = JacksonUtil.toJsonNode(nxDesignDetails.getDesignData());
		return currentDesignDetails.path("nxSiteId").asText();
	}


	 public boolean isRESTEnabled(String itemId, String description) {
		boolean isRestEnabled=false;
			List<NxLookupData> restProductDetails=nxLookupDataRepository.
					findByDatasetNameAndItemIdAndDescription(MyPriceConstants.REST_PRODUCTS,
							itemId,description);
			if(CollectionUtils.isNotEmpty(restProductDetails)) {
				String criteria =restProductDetails.get(0).getCriteria();
				if(StringConstants.CONSTANT_Y.equalsIgnoreCase(criteria)) {
					isRestEnabled=true;
				}else if(StringConstants.CONSTANT_N.equalsIgnoreCase(criteria)) {
					isRestEnabled=false;
				}
			}
		
		return isRestEnabled;
	}
	
	protected String derivedComponentIdForAde(Map<String,Object> methodParam,GetTransactionLineItem input) {
		String requestEndPointANxSiteId=methodParam.get(MyPriceConstants.ENDPOINT_A_NX_SITE_ID)!=null?
				(String)methodParam.get(MyPriceConstants.ENDPOINT_A_NX_SITE_ID):"";
		String requestEndPointZNxSiteId=methodParam.get(MyPriceConstants.ENDPOINT_Z_NX_SITE_ID)!=null?
				(String)methodParam.get(MyPriceConstants.ENDPOINT_Z_NX_SITE_ID):"";
		String responseRefSiteId=StringUtils.isNotEmpty(input.getNxSiteId())?input.getNxSiteId():"";	
		String responseBeId=StringUtils.isNotEmpty(input.getUsocId())?input.getUsocId():"";
		
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
			List<String> requestCircuitBeId=methodParam.get(MyPriceConstants.CIRCUIT_BEID)!=null?
					(List)methodParam.get(MyPriceConstants.CIRCUIT_BEID):new ArrayList<>();
			if(CollectionUtils.isNotEmpty(requestEndPointZBeId) && requestEndPointZBeId.contains(responseBeId)) {
				return methodParam.get(MyPriceConstants.ENDPOINT_Z_COMPONENT_ID).toString();
			}else if(CollectionUtils.isNotEmpty(requestCircuitBeId) && requestCircuitBeId.contains(responseBeId)) {
				return methodParam.get(MyPriceConstants.CIRCUIT_COMPONENT_ID).toString();
			}	
							
		}
		return null;
	}
	
	protected String derivedComponentIdForEpls(Map<String,Object> methodParam,GetTransactionLineItem input) {
		String requestEndPointANxSiteId=methodParam.get(MyPriceConstants.ENDPOINT_A_NX_SITE_ID)!=null?
				(String)methodParam.get(MyPriceConstants.ENDPOINT_A_NX_SITE_ID):"";
		String requestEndPointZNxSiteId=methodParam.get(MyPriceConstants.ENDPOINT_Z_NX_SITE_ID)!=null?
				(String)methodParam.get(MyPriceConstants.ENDPOINT_Z_NX_SITE_ID):"";
		String responseRefSiteId=StringUtils.isNotEmpty(input.getNxSiteId())?input.getNxSiteId():"";	
		String responseProduct=StringUtils.isNotEmpty(input.getParentLineitem())?input.getParentLineitem().replaceAll("\\s+",""):"";
		
		if(responseRefSiteId.equals(requestEndPointANxSiteId)) {
			
			String requestCircuitProduct=methodParam.get(MyPriceConstants.CIRCUIT)!=null? 
					methodParam.get(MyPriceConstants.CIRCUIT).toString():null;
			String requestEndPointProduct=methodParam.get(MyPriceConstants.ENDPOINT_A)!=null?
					methodParam.get(MyPriceConstants.ENDPOINT_A).toString():null;
			String requestEndPointZProduct=methodParam.get(MyPriceConstants.ENDPOINT_Z)!=null?
					methodParam.get(MyPriceConstants.ENDPOINT_Z).toString():null;
					
			if(null!= requestCircuitProduct&& requestCircuitProduct.equalsIgnoreCase(responseProduct)) {
				return methodParam.get(MyPriceConstants.CIRCUIT_COMPONENT_ID).toString();
			}else if(null!=requestEndPointProduct && requestEndPointProduct.equalsIgnoreCase(responseProduct)) {
				return methodParam.get(MyPriceConstants.ENDPOINT_A_COMPONENT_ID).toString();
			}else if(null!=requestEndPointZProduct && requestEndPointZProduct.equalsIgnoreCase(responseProduct)) {
				return methodParam.get(MyPriceConstants.ENDPOINT_Z_COMPONENT_ID).toString();
			}	
			
		}else if(responseRefSiteId.equals(requestEndPointZNxSiteId)) {
			String requestEndPointZProduct=methodParam.get(MyPriceConstants.ENDPOINT_Z)!=null?
					methodParam.get(MyPriceConstants.ENDPOINT_Z).toString():null;
			if(null!=requestEndPointZProduct && requestEndPointZProduct.equalsIgnoreCase(responseProduct)) {
				return methodParam.get(MyPriceConstants.ENDPOINT_Z_COMPONENT_ID).toString();
			}		
							
		}
		return null;
	}
	/**
	 * Collect circuit details for ade.
	 *
	 * @param inputDesignDetails the input design details
	 * @param offerName the offer name
	 * @param methodParam the method param
	 */
	public void collectCircuitDetailsForAde(JSONObject inputDesignDetails,List<String> offerName,Map<String,Object> methodParam) {
		if(offerName.contains(MyPriceConstants.ADE_OFFER_NAME.toUpperCase())) {
			String priceScenarioId=methodParam.get(StringConstants.PRICE_SCENARIO_ID)!=null?
					String.valueOf(methodParam.get(StringConstants.PRICE_SCENARIO_ID)):null;
			//collect all ADE mandatory data from DPP for response processing
			methodParam.put(MyPriceConstants.ENDPOINT_A_NX_SITE_ID, 
					this.getDataInString(inputDesignDetails,MyPriceConstants.ENDPOINT_A_NX_SITE_ID_PATH));
			methodParam.put(MyPriceConstants.ENDPOINT_Z_NX_SITE_ID, 
					this.getDataInString(inputDesignDetails,MyPriceConstants.ENDPOINT_Z_NX_SITE_ID_PATH));
			methodParam.put(MyPriceConstants.ENDPOINT_A_COMPONENT_ID,
					this.getDataInString(inputDesignDetails, MyPriceConstants.ENDPOINT_A_COMPONENT_ID_PATH));
			methodParam.put(MyPriceConstants.ENDPOINT_Z_COMPONENT_ID, 
					this.getDataInString(inputDesignDetails,MyPriceConstants.ENDPOINT_Z_COMPONENT_ID_PATH));
			methodParam.put(MyPriceConstants.CIRCUIT_COMPONENT_ID,
					this.getDataInString(inputDesignDetails,MyPriceConstants.CIRCUIT_COMPONENT_ID_PATH));
			methodParam.put(MyPriceConstants.ENDPOINT_A_BEID, this.getBeIdListByComponentId(inputDesignDetails,
					methodParam.get(MyPriceConstants.ENDPOINT_A_COMPONENT_ID),priceScenarioId));
			methodParam.put(MyPriceConstants.ENDPOINT_Z_BEID, this.getBeIdListByComponentId(inputDesignDetails,
					methodParam.get(MyPriceConstants.ENDPOINT_Z_COMPONENT_ID),priceScenarioId));
			methodParam.put(MyPriceConstants.CIRCUIT_BEID, this.getBeIdListByComponentId(inputDesignDetails,
					methodParam.get(MyPriceConstants.CIRCUIT_COMPONENT_ID),priceScenarioId));
		}
	}

	public void collectCircuitDetailsForEpls(JSONObject inputDesignDetails,List<String> offerName,Map<String,Object> methodParam) {
		if(offerName.contains(MyPriceConstants.EPLSWAN_OFFER_NAME.toUpperCase())||offerName.contains(MyPriceConstants.EPLSWAN_OFFER_NAME.toUpperCase())) {
			String priceScenarioId=methodParam.get(StringConstants.PRICE_SCENARIO_ID)!=null?
					String.valueOf(methodParam.get(StringConstants.PRICE_SCENARIO_ID)):null;
			//collect all ADE mandatory data from DPP for response processing
			methodParam.put(MyPriceConstants.ENDPOINT_A_NX_SITE_ID, 
					this.getDataInString(inputDesignDetails,MyPriceConstants.ENDPOINT_A_NX_SITE_ID_PATH));
			methodParam.put(MyPriceConstants.ENDPOINT_Z_NX_SITE_ID, 
					this.getDataInString(inputDesignDetails,MyPriceConstants.ENDPOINT_Z_NX_SITE_ID_PATH));
			methodParam.put(MyPriceConstants.ENDPOINT_A_COMPONENT_ID,
					this.getDataInString(inputDesignDetails, MyPriceConstants.ENDPOINT_A_COMPONENT_ID_PATH));
			methodParam.put(MyPriceConstants.ENDPOINT_Z_COMPONENT_ID, 
					this.getDataInString(inputDesignDetails,MyPriceConstants.ENDPOINT_Z_COMPONENT_ID_PATH));
			methodParam.put(MyPriceConstants.CIRCUIT_COMPONENT_ID,
					this.getDataInString(inputDesignDetails,MyPriceConstants.CIRCUIT_COMPONENT_ID_PATH));
			methodParam.put(MyPriceConstants.ENDPOINT_A, MyPriceConstants.LocalAccess); 
			methodParam.put(MyPriceConstants.ENDPOINT_Z,MyPriceConstants.LocalAccess);
			methodParam.put(MyPriceConstants.CIRCUIT,MyPriceConstants.EPLSWAN );
		}
	}
	protected String getDataInString(JSONObject inputDesignDetails,String path) {
		TypeRef<List<String>> ref = new TypeRef<List<String>>() {};
		List<String> dataLst=jsonPathUtil.search(inputDesignDetails,path, ref);
		if(CollectionUtils.isNotEmpty(dataLst)) {
			return dataLst.get(0);
		}
		return null;
	}
	

	/**
	 * Gets the be id list by component id.
	 *
	 * @param inputDesignDetails the input design details
	 * @param componentId the component id
	 * @return the be id list by component id
	 */
	protected List<String> getBeIdListByComponentId(JSONObject inputDesignDetails,Object componentId,String priceScenarioId){
		String path="$..priceDetails.componentDetails.[?(@.componentId=="+componentId+")]"
				+ ".priceAttributes[?(@.priceScenarioId=="+priceScenarioId+")].beid";
		return this.getDataListInString(inputDesignDetails, path);
	}
	
	
	protected List<String> getDataListInString(JSONObject inputDesignDetails,String path) {
		TypeRef<List<String>> ref = new TypeRef<List<String>>() {};
		List<String> dataLst=jsonPathUtil.search(inputDesignDetails,path, ref);
		if(CollectionUtils.isNotEmpty(dataLst)) {
			return dataLst;
		}
		return new ArrayList<>();
	}
	
	
	protected void printTotalDuration(Long currentTime,Long startTime,String operation,Long nxSolutionId) {
		Long endTime=System.currentTimeMillis() - currentTime;
		String totalDuration=new StringBuilder().append(operation +" for solution id :: ").append(nxSolutionId).append(" took :: ").append((endTime - startTime)).append(" ").append(MyPriceConstants.MILLISEC).toString();
		logger.info(totalDuration);
	}
	
	public boolean isRESTVersionEnabled(String itemId,String datasetName, String description) {
		boolean isRestVerEnabled=false;
		List<NxLookupData> restProductDetails=nxLookupDataRepository.findByItemIdAndDatasetAndCriteriaAndDesc(itemId,datasetName,StringConstants.CONSTANT_Y,description);
		if(CollectionUtils.isNotEmpty(restProductDetails)) {
			isRestVerEnabled=true;
		}
		return isRestVerEnabled;
	}
	
	public boolean removeTransactionByMpsolutionid(Map<String, Object> designMap) {
		logger.info("Start -- removeTransactionByMpsolutionid");
		try {
			Set<Long> mpSolutionIds = designMap.containsKey(MyPriceConstants.MP_SOLUTION_ID_LIST) ? (HashSet<Long>) designMap.get(MyPriceConstants.MP_SOLUTION_ID_LIST) : null;
			if(CollectionUtils.isNotEmpty(mpSolutionIds)) {
				Long nxTxnId = designMap.containsKey(MyPriceConstants.NX_TRANSACTION_ID) ? (long) designMap.get(MyPriceConstants.NX_TRANSACTION_ID) : 0L;
				List<Long> nxDesignId = designMap.containsKey(MyPriceConstants.NX_DESIGN_ID_LIST) ? (List<Long>) designMap.get(MyPriceConstants.NX_DESIGN_ID_LIST) : null;
				designMap.put(MyPriceConstants.DOCUMENT_ID, mpSolutionIds);
				removeTransactionServiceLineImpl.removeTransactionLine(designMap);
				//need to add delete logic for design document and price details
				nxMpPriceDetailsRepository.deleteDataByNxTxnIdAndNxDesignId(nxTxnId, nxDesignId);
				nxMpDesignDocumentRepo.deleteDataByNxTxnIdAndNxDesignId(nxTxnId, nxDesignId);
				designMap.remove(MyPriceConstants.DOCUMENT_ID);
			}
		} catch (SalesBusinessException e) {
			logger.info("Error during removeTransactionByMpsolutionid {}", e.getMessage());
			return false;
		}
		logger.info("End -- removeTransactionByMpsolutionid");
		if (designMap.containsKey(InrConstants.REQUEST_META_DATA_KEY)) {
			designMap.remove(InrConstants.REQUEST_META_DATA_KEY);
		}
		return true;
	}
}
