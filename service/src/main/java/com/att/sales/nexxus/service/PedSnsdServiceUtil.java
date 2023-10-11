package com.att.sales.nexxus.service;

import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.constant.CommonConstants;
import com.att.sales.nexxus.constant.CustomJsonConstants;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.constant.TDDConstants;
import com.att.sales.nexxus.dao.model.NxDesign;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxDesignDetails;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxMpConfigJsonMapping;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.SalesMsProdcompUdfAttrVal;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxDesignDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxDesignRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxMpDesignDocumentRepository;
import com.att.sales.nexxus.dao.repository.SalesMsDao;
import com.att.sales.nexxus.dao.repository.SalesMsProdcompUdfAttrValRepository;
import com.att.sales.nexxus.myprice.transaction.dao.service.NxMpRepositoryService;
import com.att.sales.nexxus.myprice.transaction.model.CreateTransactionResponse;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdatePricingUtil;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdateRestUtilPd;
import com.att.sales.nexxus.myprice.transaction.service.MyPriceTransactionUtil;
import com.att.sales.nexxus.myprice.transaction.service.RestCommonUtil;
import com.att.sales.nexxus.reteriveicb.model.ActionDeterminants;
import com.att.sales.nexxus.reteriveicb.model.Offer;
import com.att.sales.nexxus.reteriveicb.model.RetreiveICBPSPRequest;
import com.att.sales.nexxus.reteriveicb.model.Site;
import com.att.sales.nexxus.reteriveicb.model.Solution;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.TypeRef;

@Component
public class PedSnsdServiceUtil {

	private static Logger log = LoggerFactory.getLogger(PedSnsdServiceUtil.class);
	private static final int UDF_A_ENDPOINT = 21033;
	private static final int UDF_Z_ENDPOINT = 21034;
	private static final int UDF_POPCOLLECTOR = 22564;
	private static final int ACCESS_SERVICE_UDFID = 1000371;
	@Autowired
	private NxDesignRepository nxDesignRepository;
	
	@Autowired
	private NxMpRepositoryService nxMpRepositoryService;

	@Autowired
	private SalesMsDao salesMsDao;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private MyPriceTransactionUtil myPriceTransactionUtil;

	@Autowired
	private NxDesignAuditRepository nxDesignAuditRepository;
	
	@Autowired
	private NxDesignDetailsRepository nxDesignDetailsRepository;
	
	@Autowired
	private NxMpDealRepository nxMpDealRepository;
				
	@Autowired
	private ProcessPDtoMPRestUtil processPDtoMPRestUtil;
	
	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;

	@Autowired
	private NexxusJsonUtility nexxusJsonUtility;

	@Autowired
	private RestCommonUtil restCommonUtil;

	@Autowired
	private ConfigAndUpdateRestUtilPd configAndUpdateRestUtilPd;
	
	@Autowired
	private ConfigAndUpdatePricingUtil configAndUpdatePricingUtil;
	
	@Autowired
	private JsonPathUtil jsonPathUtil;
	
	@Autowired
	private NxMpDesignDocumentRepository nxMpDesignDocumentRepository;
	
	@Autowired
	private SalesMsProdcompUdfAttrValRepository salesMsProdcompUdfAttrValRepository;
	
	@Value("${ade.designStatus.path}")
	private String adeDesignStatusPath;

	
	@SuppressWarnings("unchecked")
	public Boolean saveDesignData(RetreiveICBPSPRequest retreiveICBPSPRequest, NxSolutionDetail nxSolutionDetail,
			CreateTransactionResponse createTransactionResponse, Map<String, Object> paramMap) {
		log.info("Start PedSnsdServiceUtil :saveDesignData");
		boolean skipDmaapForReopenOrSold = false;
		boolean skipMypriceCall= false;
		int callDmappCount = 0;
		boolean invokeMyprice = paramMap.containsKey(StringConstants.INVOKE_MYPRICE) ? (boolean) paramMap.get(StringConstants.INVOKE_MYPRICE) : true;
		Boolean result = false;
		//boolean isCancelDesignStatus = paramMap.containsKey("designStatus") ? (boolean) paramMap.get("designStatus") : false;
		//map holding all the asrItemId and nxsiteId that are processed for config solution design , update pricing 
		Map<String, Object> designResult = new HashMap<String, Object>();
		JsonNode request = mapper.valueToTree(retreiveICBPSPRequest);
		// save DB
		String userId = request.at("/solution/userId").asText();
		String userFirstName = request.at("/solution/userFirstName").asText();
		String userLastName = request.at("/solution/userLastName").asText();
		String contractTerm = request.at("/solution/contractTerm").asText();
		String mpsIndicator = request.at("/solution/mpsIndicator").asText();
		log.info("contractTerm at solution :==>> {} "+contractTerm);
		String solutionStatus = request.at("/solution/solutionStatus").asText();
		paramMap.put(StringConstants.SOLUTION_STATUS,solutionStatus);
		String sourceName=retreiveICBPSPRequest.getSolution().getSourceName();
		paramMap.put(StringConstants.SOURCE_NAME,sourceName );
		JsonNode offers = request.at("/solution/offers");
		boolean isRestCall=false;
		List<String> updatePricingUnmatchingAsrIdsList=null;
		List<String> updatePricingUnmatchingNxDesignIdsList =null;
		paramMap.put(MyPriceConstants.DESIGN_ID_FOR_UPDATE_PRICING, new ArrayList<Long>());
		paramMap.put(MyPriceConstants.DESIGN_ID_FOR_UPDATE_PRICING_RECONFIGURE, new ArrayList<Long>());
		if(createTransactionResponse != null && createTransactionResponse.getPriceScenarioId() != null) {
			paramMap.put(StringConstants.PRICE_SCENARIO_ID,createTransactionResponse.getPriceScenarioId().toString());
		}
		//map for update pricing
		Map<String, Object> designMap = new HashMap<String, Object>();
		long nxSolutionId=nxSolutionDetail.getNxSolutionId();
		String offerName = null;
		String subOfferName=null;
		String offer=null;
		List<String> offerList=new ArrayList<String>();
		String solutionRestVersion=nxSolutionDetail.getRestVersion();
		boolean isSolutionRestVer2Call=StringConstants.VERSION_2.equalsIgnoreCase(solutionRestVersion)?true:false;
		List<Object[]> consolidationCriteria = new ArrayList<Object[]>();
		List<Object> asrIds = new ArrayList<Object>();
		List <Long> nxSiteIdList = new ArrayList<Long>();
		Boolean sourceIpne=StringConstants.IPNE.equalsIgnoreCase(retreiveICBPSPRequest.getSolution().getSourceName());
		Boolean isSubmitToMp=sourceIpne && StringConstants.CONSTANT_Y.equalsIgnoreCase(retreiveICBPSPRequest.getSolution().getSubmitToMyprice());
		String pedStatusInd=null;
		if(paramMap.containsKey("IS_PED_SUCCESS")) {
			pedStatusInd=paramMap.get("IS_PED_SUCCESS") != null ? (String) paramMap.get("IS_PED_SUCCESS") : null;
		}
		boolean callGetTxnLine=false;
		boolean isMpcallFailed=true;
		
		 if (isSubmitToMp && !StringConstants.SOLUTION_CANCEL.equalsIgnoreCase(solutionStatus) 
		 		&& (paramMap.containsKey(StringConstants.TRANSACTION_TYPE) && StringConstants.TRANSACTION_TYPE_EXISTING.equalsIgnoreCase(paramMap.get(StringConstants.TRANSACTION_TYPE).toString()))) {
			
		 	result = removeCancelledLineItemsForIpne( retreiveICBPSPRequest, nxSolutionId, createTransactionResponse, paramMap);
		 	designResult.put(MyPriceConstants.TRANSACTION_TYPE, "REMOVE_TRANSACTION");
		 	if(!result) {
		 	return false;
		 	}	
		 }
		for (JsonNode offerElement : offers) {
			String offerId = offerElement.path("offerId").asText();
			String priceUpdate = offerElement.path("priceUpdate").asText();
			log.info("priceUpdate at offerlevel :==>> {} "+priceUpdate);
			if (StringUtils.isNotEmpty(offerId)) {
				int id = Integer.parseInt(offerId);
				offerName = salesMsDao.getOfferNameByOfferId(id);
				offer=offerName;
				offerList.add(offerName.toUpperCase());
			}
			solutionRestVersion =getRestVersion(offerName);
			paramMap.put(StringConstants.REST_VERSION, solutionRestVersion);
			isSolutionRestVer2Call=StringConstants.VERSION_2.equalsIgnoreCase(solutionRestVersion)?true:false;
			isRestCall=processPDtoMPRestUtil.isRESTEnabled(offerName,MyPriceConstants.SOURCE_PD);
			JsonNode site = offerElement.path("site");
			
			if (StringConstants.OFFERNAME_ASE.equals(offerName) || StringConstants.OFFERNAME_ASENOD.equalsIgnoreCase(offerName)) {
				if (StringConstants.VERSION_2.equalsIgnoreCase(solutionRestVersion) 
						&& (paramMap.containsKey(StringConstants.TRANSACTION_TYPE) && StringConstants.TRANSACTION_TYPE_EXISTING.equalsIgnoreCase(paramMap.get(StringConstants.TRANSACTION_TYPE).toString()))) {
					TypeRef<List<Object>> arrayType = new TypeRef<List<Object>>() {
					};
					asrIds = jsonPathUtil.search(retreiveICBPSPRequest, MyPriceConstants.ASE_ASR_ITEM_ID_PATH, arrayType);
					// to get old consolidation criteria on update scenario
					consolidationCriteria = nxDesignDetailsRepository.findConsolidationCriteriaByNxSolutionIdAndAsr(nxSolutionId, asrIds);
				}
				for (JsonNode siteElement : site) {
					// Changes for MACD 
					JsonNode designSiteOfferPort = siteElement.path("designSiteOfferPort");
					for(int i =0; i< designSiteOfferPort.size(); i++) {
						String typeOfInventory = designSiteOfferPort.get(i).path("typeOfInventory").asText();
						if(StringConstants.CONSTANT_FROM.equalsIgnoreCase(typeOfInventory)) {
							((ArrayNode)designSiteOfferPort).remove(i);
						}
					}
					// end of MACD change
					String asrItemId = findAseAsrItemId(siteElement);
					String designStatus = siteElement.path("designStatus").asText();
					String designModifiedInd = siteElement.path("designModifiedInd").asText();
					//JsonNode componentCodeIdNode = siteElement.path("componentCodeId");
					Long popCollectorudfAttId = findPopCollectorUdfAttributeId(siteElement, UDF_POPCOLLECTOR);
					String popCollectorAsr=getDataInString(site,asrItemId);
					if (null!=popCollectorAsr) {
						((ObjectNode)siteElement).put("popCollectorAsr", popCollectorAsr);
					}
					String popCollectorInd=null;
					if(popCollectorudfAttId !=null){
						popCollectorInd=translateUdfAttributeId(UDF_POPCOLLECTOR, popCollectorudfAttId,Long.parseLong(offerId),30);
					}
					NxDesign nxDesign = saveDB(nxSolutionDetail, userId, userFirstName, userLastName, designStatus, designModifiedInd,
							offerName, siteElement, asrItemId, paramMap, (ObjectNode) siteElement,isSolutionRestVer2Call,null,null,null,null, pedStatusInd,null);
					if (StringConstants.DESIGN_REOPEN.equalsIgnoreCase(designStatus) || StringConstants.SOLUTION_SOLD.equalsIgnoreCase(solutionStatus) || !invokeMyprice) {
						result = true;
						skipDmaapForReopenOrSold = true;
						skipMypriceCall=true;
					}else if(null != nxDesign){
						callDmappCount++;
						paramMap.put(MyPriceConstants.PRICE_UPDATE, priceUpdate);
						log.info("priceUpdate ASE offerlevel :==>> {} ",priceUpdate);
						if(isSolutionRestVer2Call) {
							/*solution is rest version2 
							 * */	
							continue;
						}else if (!isRestCall) {
							//soap call
							designResult = myPriceTransactionUtil.configAndUpdatePricing(createTransactionResponse, siteElement,
									nxDesign, paramMap, offerName);
							result = designResult.containsKey(MyPriceConstants.RESPONSE_STATUS) ? (boolean) designResult.get(MyPriceConstants.RESPONSE_STATUS) : false;
						} else{
							//rest v1 call
							if(!StringConstants.CONSTANT_Y.equals(popCollectorInd)) {
								log.info("Processing ASE solution with rest api {}", nxSolutionId);
								String thirdPartyInd=siteElement.path("thirdPartyInd").asText(); 
								if(MyPriceConstants.ASENOD_OFFER_NAME.equalsIgnoreCase(offerName)){
									if(StringUtils.isNotEmpty(thirdPartyInd) && thirdPartyInd.equalsIgnoreCase("Y")) {
										paramMap.put(MyPriceConstants.SUB_OFFER_NAME, MyPriceConstants.ASENOD_3PA);
										subOfferName=MyPriceConstants.ASENOD_3PA;
									}else {
										paramMap.put(MyPriceConstants.SUB_OFFER_NAME, MyPriceConstants.ASENOD_IR);
										subOfferName=MyPriceConstants.ASENOD_IR;
									}
								}
								paramMap.put(MyPriceConstants.NX_SOLIUTION_ID, nxSolutionId);
								paramMap.put(MyPriceConstants.OFFER_NAME,offerName);
								Map<String,Object> restResponseMap=callRestConfigSolAndDeign(createTransactionResponse,nxDesign, 
										paramMap,siteElement,asrItemId);
								boolean responseStatus=restResponseMap.containsKey(MyPriceConstants.RESPONSE_STATUS)?
										(boolean) restResponseMap.get(MyPriceConstants.RESPONSE_STATUS):false;
								result=responseStatus;
								isMpcallFailed=responseStatus;
								if (result && !callGetTxnLine) {
									callGetTxnLine = true;
								}
								if(restResponseMap.containsKey(MyPriceConstants.TRANSACTION_TYPE)) {
									designResult.put(MyPriceConstants.TRANSACTION_TYPE, restResponseMap.get(MyPriceConstants.TRANSACTION_TYPE));
								}
								if(restResponseMap.containsKey(MyPriceConstants.RESPONSE_MSG)) {
									designResult.put(MyPriceConstants.RESPONSE_MSG, restResponseMap.get(MyPriceConstants.RESPONSE_MSG));
								}
							}
							else {
								continue;
							}
						}
					}
					if(!result) {
						designResult.put(MyPriceConstants.ASR_ITEM_ID, asrItemId);
						designResult.put(MyPriceConstants.NX_SITE_ID, siteElement.path("siteId").asLong());
						// break the loop if design fails for any site
						break;
					}
				}
			} else if (StringConstants.OFFERNAME_ADE.equals(offerName)) {
				Map<String, JsonNode> siteMap = new HashMap<>();
				for (JsonNode siteElement : site) {
					JsonNode siteId = siteElement.path("siteId");
					if (!siteId.isMissingNode() && !siteId.isNull()) {
						siteMap.put(siteId.asText(), siteElement);
					}
				}
				JsonNode circuit = offerElement.path("circuit");
				Long circuitId = null;
				for (JsonNode circuitElement : circuit) {
					JsonNode circuitElementCopy = circuitElement.deepCopy();
					String asrItemId = findAdeAsrItemId(circuitElementCopy);
					String designStatus = circuitElement.path("designStatus").asText();
					String designModifiedInd = circuitElement.path("designModifiedInd").asText();
					JsonNode component = circuitElementCopy.path("component");
					String circuitType="";
					for (JsonNode componentElement : component) {
						JsonNode componentCodeIdNode = componentElement.path("componentCodeId");
						JsonNode componentCodeTypeNode = componentElement.path("componentCodeType");
						if("Circuit".equalsIgnoreCase(componentCodeTypeNode.asText())) {
							circuitId = componentElement.path("componentId").asLong();
							circuitType = componentElement.path("circuitType").asText();
						}
						if (StringConstants.COMPONENTID_ENDPOINT.equals(componentCodeIdNode.asText())) {
							JsonNode referenceId = componentElement.at("/references/0/referenceId");
							if (!referenceId.isMissingNode() && !referenceId.isNull()) {
								JsonNode siteNode = siteMap.get(referenceId.asText());
								if (siteNode != null) {
									//setting site on endpointLevel 
									((ObjectNode) componentElement).set("siteObj", siteNode);
									ObjectNode siteNodeObj = (ObjectNode) siteNode;
									String aEndpoint = JacksonUtil.findUdfAttributeTextFromComponentNode(componentElement, UDF_A_ENDPOINT);
									if (aEndpoint != null) {
										siteNodeObj.put("_endPointRef", UDF_A_ENDPOINT);
									} else {
										String zEndpoint = JacksonUtil.findUdfAttributeTextFromComponentNode(componentElement, UDF_Z_ENDPOINT);
										if (zEndpoint != null) {
											siteNodeObj.put("_endPointRef", UDF_Z_ENDPOINT);
										}
									}
									((ObjectNode) circuitElementCopy).withArray("site").add(siteNode);
								}
							}
						}
					}
					
					NxDesign nxDesign = saveDB(nxSolutionDetail, userId, userFirstName, userLastName, designStatus, designModifiedInd, offerName,
							circuitElementCopy, asrItemId, paramMap, (ObjectNode) circuitElement,isSolutionRestVer2Call,circuitType,null,null,isSubmitToMp, pedStatusInd,null);
					if (StringConstants.DESIGN_REOPEN.equalsIgnoreCase(designStatus) || StringConstants.SOLUTION_SOLD.equalsIgnoreCase(solutionStatus) || !invokeMyprice) {
						skipDmaapForReopenOrSold = true;
						result = true;
						skipMypriceCall=true;
					}else if(null != nxDesign) {
						callDmappCount++;
						paramMap.put(MyPriceConstants.PRICE_UPDATE, priceUpdate);
						paramMap.put(MyPriceConstants.CONTRACT_TERM, contractTerm);
						log.info("priceUpdate ADE offerlevel :==>> {} ", priceUpdate);
						log.info("contractTerm ADE solution :==>> {} ", contractTerm);
						String standardPricingInd=null != retreiveICBPSPRequest.getSolution().getStandardPricingInd() ? retreiveICBPSPRequest.getSolution().getStandardPricingInd() : null;
						paramMap.put(MyPriceConstants.STANDART_PRICING_IND, standardPricingInd);
						if(!isRestCall) {
							//soap call
						  designResult = myPriceTransactionUtil.configAndUpdatePricing(createTransactionResponse, circuitElement,
								nxDesign, paramMap,MyPriceConstants.ADE_OFFER_NAME);
						  result = designResult.containsKey(MyPriceConstants.RESPONSE_STATUS) ? (boolean) designResult.get(MyPriceConstants.RESPONSE_STATUS) : false;
						}else {
							//Rest call
							String isReconfiure = paramMap.containsKey("IS_RECONFIGURE")? (String) paramMap.get("IS_RECONFIGURE") : null;
							boolean isConfigureCkt = !StringConstants.SOLUTION_CANCEL.equalsIgnoreCase(solutionStatus) 
									&&  "Y".equalsIgnoreCase(nxDesign.getSubmitToMp()) && !StringConstants.CONSTANT_N.equalsIgnoreCase(isReconfiure);
							if(sourceName!=null &&
									!(sourceIpne && (MyPriceConstants.CIRCUIT_TYPE_ATX.equalsIgnoreCase(nxDesign.getDesignType()) 
									|| (StringConstants.TRANSACTION_TYPE_EXISTING.equalsIgnoreCase(paramMap.get(StringConstants.TRANSACTION_TYPE).toString())
											&& isConfigureCkt )))) {
								
								log.info("Processing ADE solution with rest api {}", nxSolutionId);
								paramMap.put(MyPriceConstants.NX_SOLIUTION_ID, nxSolutionId);
								paramMap.put(MyPriceConstants.OFFER_NAME,offerName);
								
								Map<String,Object> restResponseMap=callRestConfigSolAndDeign(createTransactionResponse,nxDesign, 
										paramMap,circuitElement,asrItemId);
								boolean responseStatus=restResponseMap.containsKey(MyPriceConstants.RESPONSE_STATUS)?
										(boolean) restResponseMap.get(MyPriceConstants.RESPONSE_STATUS):false;
								result=responseStatus;
								isMpcallFailed=responseStatus;
								if(responseStatus) {
									nxDesign.setSubmitToMp("Y");
									nxMpRepositoryService.saveNxDesignDatas(nxDesign);
								}
								if (result && !callGetTxnLine) {
									callGetTxnLine = true;
								}
								if(restResponseMap.containsKey(MyPriceConstants.TRANSACTION_TYPE)) {
									designResult.put(MyPriceConstants.TRANSACTION_TYPE, restResponseMap.get(MyPriceConstants.TRANSACTION_TYPE));
								}
								if(restResponseMap.containsKey(MyPriceConstants.RESPONSE_MSG)) {
									designResult.put(MyPriceConstants.RESPONSE_MSG, restResponseMap.get(MyPriceConstants.RESPONSE_MSG));
								}
							}
							else {
								continue;
							}
						}
					}
					if(!result) {
						designResult.put(MyPriceConstants.ASR_ITEM_ID, asrItemId);
						designResult.put(MyPriceConstants.NX_SITE_ID, circuitId);
						// break the loop if design fails for any circuit
						break;
					}
				}
			} else if (StringConstants.OFFERNAME_EPLSWAN.equals(offerName)) {
				
				if(!isSubmitToMp) {
					skipMypriceCall=true;
					continue;
				}
				if (StringConstants.VERSION_2.equalsIgnoreCase(solutionRestVersion) 
						&& (paramMap.containsKey(StringConstants.TRANSACTION_TYPE) && StringConstants.TRANSACTION_TYPE_EXISTING.equalsIgnoreCase(paramMap.get(StringConstants.TRANSACTION_TYPE).toString()))) {
					TypeRef<List<Long>> arrayType = new TypeRef<List<Long>>() {
					};
					List<Long> nxSiteId = jsonPathUtil.search(retreiveICBPSPRequest, MyPriceConstants.EPLSWAN_NX_SITE_ID_PATH, arrayType);
					// to get old consolidation criteria on update scenario
					nxSiteIdList = nxSiteId.stream().filter(Objects::nonNull).collect(Collectors.toList());
					consolidationCriteria = nxDesignDetailsRepository.findConsolidationCriteriaByNxSolutionIdAndSiteId(nxSolutionId,nxSiteIdList);
				}
				Map<String, JsonNode> siteMap = new HashMap<>();
				for (JsonNode siteElement : site) {
					JsonNode siteId = siteElement.path("siteId");
					if (!siteId.isMissingNode() && !siteId.isNull()) {
						siteMap.put(siteId.asText(), siteElement);
					}
				}
				JsonNode circuit = offerElement.path("circuit");
				Long circuitId = null;
				for (JsonNode circuitElement : circuit) {
					JsonNode circuitElementCopy = circuitElement.deepCopy();
//					String asrItemId = findAdeAsrItemId(circuitElementCopy);
					String designStatus = circuitElement.path("designStatus").asText();
					String designModifiedInd = circuitElement.path("designModifiedInd").asText();
					JsonNode component = circuitElementCopy.path("component");
					String circuitType=null;
					String circuitNumber=null;
					Long circuitCertification=null; 
					Map<String, Site> asrAndNxsiteidList = new HashMap<String, Site>();
					for (JsonNode componentElement : component) {
						JsonNode componentCodeIdNode = componentElement.path("componentCodeId");
						JsonNode componentCodeTypeNode = componentElement.path("componentCodeType");
						if("Circuit".equalsIgnoreCase(componentCodeTypeNode.asText())) {
							circuitId = componentElement.path("componentId").asLong();
							circuitType = componentElement.path("circuitType").asText();
							circuitNumber = JacksonUtil.findUdfAttributeTextFromComponentNode(componentElement, 200158);
							circuitCertification = JacksonUtil.findUdfAttributeIdFromComponentNode(componentElement, 200193);
						}
						if (StringConstants.COMPONENTID_ENDPOINT.equals(componentCodeIdNode.asText())) {
							JsonNode referenceId = componentElement.at("/references/0/referenceId");
							Long udfAttributeId=JacksonUtil.findUdfAttributeIdFromComponentNode(componentElement, ACCESS_SERVICE_UDFID);
							String accessService=translateUdfAttributeId(ACCESS_SERVICE_UDFID, udfAttributeId,Long.parseLong(offerId),componentCodeIdNode.asLong());
							
								if (!referenceId.isMissingNode() && !referenceId.isNull()) {
									String aEPoint = JacksonUtil.findUdfAttributeTextFromComponentNode(componentElement, UDF_A_ENDPOINT);
									String zEpoint = JacksonUtil.findUdfAttributeTextFromComponentNode(componentElement, UDF_Z_ENDPOINT);
									
									JsonNode siteNode = siteMap.get(referenceId.asText());
									if (siteNode != null) {
										Long nxSiteId= siteNode.path("nxSiteId").asLong();
										if (aEPoint != null) {
											Site siteObjCircuit= new Site();
											String EPAasrItemId = JacksonUtil.findUdfAttributeTextFromComponentNode(componentElement, 200162);
											EPAasrItemId=EPAasrItemId!=null?EPAasrItemId:"0";
											siteObjCircuit.setAsrItemId(EPAasrItemId);
											siteObjCircuit.setNxSiteId(nxSiteId);
											asrAndNxsiteidList.put(MyPriceConstants.CIRCUIT, siteObjCircuit);
											if(accessService!= null&&accessService.equalsIgnoreCase(StringConstants.TOTAL_SERVICE)) {
												Site siteObjEPA= new Site();
												siteObjEPA.setAsrItemId(EPAasrItemId);
												siteObjEPA.setNxSiteId(nxSiteId);
												siteObjEPA.setEndPointSiteIdentifier(MyPriceConstants.LOCA);
												asrAndNxsiteidList.put(MyPriceConstants.ENDPOINT_A, siteObjEPA);
												
											}
										}
										if (zEpoint != null) {
											Site siteObjEPZ= new Site();
											String EPZasrItemId = JacksonUtil.findUdfAttributeTextFromComponentNode(componentElement, 200162);
											EPZasrItemId=EPZasrItemId!=null?EPZasrItemId:"0";
											siteObjEPZ.setAsrItemId(EPZasrItemId);
											siteObjEPZ.setNxSiteId(nxSiteId);	
											siteObjEPZ.setEndPointSiteIdentifier(MyPriceConstants.LOCZ);
											if(accessService!= null&&accessService.equalsIgnoreCase(StringConstants.TOTAL_SERVICE)) {
												asrAndNxsiteidList.put(MyPriceConstants.ENDPOINT_Z, siteObjEPZ);
											}
										}
										//setting site on endpointLevel 
//										((ObjectNode) componentElement).set("siteObj", siteNode);
										ObjectNode siteNodeObj = (ObjectNode) siteNode;
										String aEndpoint = JacksonUtil.findUdfAttributeTextFromComponentNode(componentElement, UDF_A_ENDPOINT);
										if (aEndpoint != null) {
											siteNodeObj.put("_endPointRef", UDF_A_ENDPOINT);
											siteNodeObj.put("circuitCertification", circuitCertification);
										} else {
											String zEndpoint = JacksonUtil.findUdfAttributeTextFromComponentNode(componentElement, UDF_Z_ENDPOINT);
											if (zEndpoint != null) {
												siteNodeObj.put("_endPointRef", UDF_Z_ENDPOINT);
												siteNodeObj.put("circuitCertification", circuitCertification);
											}
										}
										((ObjectNode) componentElement).set("siteObj", siteNodeObj);
//										if(accessService!= null&&accessService.equalsIgnoreCase(StringConstants.TOTAL_SERVICE)) {
//											((ObjectNode) circuitElementCopy).withArray("site").add(siteNodeObj);
//										}	
									}
								}
						}
					}
				    for(Map.Entry<String,Site> item : asrAndNxsiteidList.entrySet()){ 
				    	if((item.getKey()==MyPriceConstants.ENDPOINT_A)||(item.getKey()==MyPriceConstants.ENDPOINT_Z)){ 
				    		offerName=StringConstants.OFFERNAME_ETHERNET;
				    	}
				    	else {
				    		offerName=StringConstants.OFFERNAME_EPLSWAN;
				    	}
				    	Site siteobject =item.getValue();
				    	String asrItemId = siteobject.getAsrItemId();
						NxDesign nxDesign = saveDB(nxSolutionDetail, userId, userFirstName, userLastName, designStatus, designModifiedInd, offerName,
							circuitElementCopy, asrItemId, paramMap, (ObjectNode) circuitElement,isSolutionRestVer2Call,circuitType,siteobject.getNxSiteId(),siteobject.getEndPointSiteIdentifier(),null, pedStatusInd,circuitNumber);

						if (StringConstants.DESIGN_REOPEN.equalsIgnoreCase(designStatus) || StringConstants.SOLUTION_SOLD.equalsIgnoreCase(solutionStatus) || !invokeMyprice) {
							skipDmaapForReopenOrSold = true;
							result = true;
							skipMypriceCall=true;
						}else if(null != nxDesign) {
							callDmappCount++;
							paramMap.put(MyPriceConstants.PRICE_UPDATE, priceUpdate);
							paramMap.put(MyPriceConstants.CONTRACT_TERM, contractTerm);
							log.info("priceUpdate EPLSWAN offerlevel :==>> {} ", priceUpdate);
							log.info("contractTerm EPLSWAN solution :==>> {} ", contractTerm);
							String standardPricingInd=null != retreiveICBPSPRequest.getSolution().getStandardPricingInd() ? retreiveICBPSPRequest.getSolution().getStandardPricingInd() : null;
							paramMap.put(MyPriceConstants.STANDART_PRICING_IND, standardPricingInd);
							if(isSolutionRestVer2Call) {
								/*solution is rest version2 
								 * */	
								continue;
							}
							else if(!isRestCall) {
								//soap call
//							  designResult = myPriceTransactionUtil.configAndUpdatePricing(createTransactionResponse, circuitElement,
//									nxDesign, paramMap,MyPriceConstants.ADE_OFFER_NAME);
//							  result = designResult.containsKey(MyPriceConstants.RESPONSE_STATUS) ? (boolean) designResult.get(MyPriceConstants.RESPONSE_STATUS) : false;
							}else {
									log.info("Processing EPLSWAN/Ethernt solution with rest api {}", nxSolutionId);
									paramMap.put(MyPriceConstants.NX_SOLIUTION_ID, nxSolutionId);
									paramMap.put(MyPriceConstants.OFFER_NAME,offerName);
									
									Map<String,Object> restResponseMap=callRestConfigSolAndDeign(createTransactionResponse,nxDesign, 
											paramMap,circuitElement,asrItemId);
									boolean responseStatus=restResponseMap.containsKey(MyPriceConstants.RESPONSE_STATUS)?
											(boolean) restResponseMap.get(MyPriceConstants.RESPONSE_STATUS):false;
									result=responseStatus;
									isMpcallFailed=responseStatus;
									if (result && !callGetTxnLine) {
										callGetTxnLine = true;
									}
									if(restResponseMap.containsKey(MyPriceConstants.TRANSACTION_TYPE)) {
										designResult.put(MyPriceConstants.TRANSACTION_TYPE, restResponseMap.get(MyPriceConstants.TRANSACTION_TYPE));
									}
									if(restResponseMap.containsKey(MyPriceConstants.RESPONSE_MSG)) {
										designResult.put(MyPriceConstants.RESPONSE_MSG, restResponseMap.get(MyPriceConstants.RESPONSE_MSG));
									}
								
							}
						}
						if(!result) {
							designResult.put(MyPriceConstants.ASR_ITEM_ID, asrItemId);
							designResult.put(MyPriceConstants.NX_SITE_ID, circuitId);
							// break the loop if design fails for any circuit
							break;
						}
				}
				}
			}
			paramMap.remove(StringConstants.SITE_TYPE);
			paramMap.remove(MyPriceConstants.CURRENT_NX_SITE_ID);
			/*String standardPricingInd=(String) paramMap.get(MyPriceConstants.STANDART_PRICING_IND);
			if(standardPricingInd.equals("Y")) {
				designMap.put(StringConstants.PRICE_SCENARIO_ID, retreiveICBPSPRequest.getSolution().getPriceScenarioId());
					
			}else {
				designMap.put(MyPriceConstants.MP_TRANSACTION_ID,createTransactionResponse.getMyPriceTransacId() );
				designMap.put(MyPriceConstants.NX_TRANSACTION_ID,createTransactionResponse.getNxTransacId());
				designMap.put(StringConstants.PRICE_SCENARIO_ID, createTransactionResponse.getPriceScenarioId());
			}*/
			if(createTransactionResponse != null) {
			
				designMap.put(MyPriceConstants.MP_TRANSACTION_ID,createTransactionResponse.getMyPriceTransacId() );
				designMap.put(MyPriceConstants.NX_TRANSACTION_ID,createTransactionResponse.getNxTransacId());
				designMap.put(StringConstants.PRICE_SCENARIO_ID, createTransactionResponse.getPriceScenarioId());
			}
			designMap.put(MyPriceConstants.OFFER_TYPE,offerName );
			designMap.put(MyPriceConstants.PRICE_UPDATE,priceUpdate);
			designMap.put(MyPriceConstants.NX_SOLIUTION_ID, nxSolutionId);
			designMap.put(MyPriceConstants.CONTRACT_TERM,contractTerm);
	        
	        if(paramMap.containsKey(InrConstants.REQUEST_META_DATA_KEY)) {
				Map<String, Object> requestMetaDataMap = (Map<String, Object>) paramMap.get(InrConstants.REQUEST_META_DATA_KEY);
				designMap.put(InrConstants.REQUEST_META_DATA_KEY, requestMetaDataMap);
			}
	        
	        if (StringConstants.VERSION_2.equalsIgnoreCase(solutionRestVersion) && !skipMypriceCall) {
				// Rest v2 call with consolidation
				String transactionType = paramMap.containsKey(StringConstants.TRANSACTION_TYPE)
						? paramMap.get(StringConstants.TRANSACTION_TYPE).toString()
						: null;
						
				if (StringConstants.TRANSACTION_TYPE_NEW.equalsIgnoreCase(transactionType)) {
					List<Object[]> consolidationCriteriaList = nxDesignDetailsRepository
							.findConsolidationCriteriaByNxSolutionIdAndType(nxSolutionId);
					for(Object[] ck : consolidationCriteriaList){
						if(null != ck[0]) {
							List<NxDesignDetails> designDetail=new ArrayList<NxDesignDetails>();
							if(null != ck[1]) {
							 designDetail = nxDesignDetailsRepository
									.findDesignDetailsByConsolidationCriteriaAndTypeAndBundleCd(nxSolutionId, String.valueOf(ck[0]),String.valueOf(ck[1]),String.valueOf(ck[2]));
							}
							else {
							 designDetail = nxDesignDetailsRepository
										.findDesignDetailsByConsolidationCriteria(nxSolutionId,String.valueOf(ck[0]));
							}
							List<NxDesign> nxDesignList = designDetail.stream().map(dd -> dd.getNxDesign())
									.collect(Collectors.toList());
							offerName = designDetail.get(0).getNxDesign().getBundleCd();
							subOfferName = designDetail.get(0).getProductName();
							if(offerName.equalsIgnoreCase(StringConstants.OFFERNAME_ETHERNET)) {
								subOfferName = designDetail.get(0).getType();
							}
							paramMap.put(MyPriceConstants.NX_SOLIUTION_ID, nxSolutionId);
							paramMap.put(MyPriceConstants.OFFER_NAME, offerName);
							paramMap.put(MyPriceConstants.SUB_OFFER_NAME, subOfferName);
							Map<String, Object> restResponseMap = callRestConfigSolAndDeignConsolidation(
									createTransactionResponse, nxDesignList, paramMap, null, null);
							boolean responseStatus = restResponseMap.containsKey(MyPriceConstants.RESPONSE_STATUS)
									? (boolean) restResponseMap.get(MyPriceConstants.RESPONSE_STATUS)
									: false;
							result = responseStatus;
							isMpcallFailed=responseStatus;
							if (result && !callGetTxnLine) {
								callGetTxnLine = true;
							}
							if (restResponseMap.containsKey(MyPriceConstants.TRANSACTION_TYPE)) {
								designResult.put(MyPriceConstants.TRANSACTION_TYPE,
										restResponseMap.get(MyPriceConstants.TRANSACTION_TYPE));
							}
							if (restResponseMap.containsKey(MyPriceConstants.RESPONSE_MSG)) {
								designResult.put(MyPriceConstants.RESPONSE_MSG,
										restResponseMap.get(MyPriceConstants.RESPONSE_MSG));
							}
							if(!result) {
								// both key MyPriceConstants.ASR_ITEM_ID and MyPriceConstants.NX_SITE_ID are no longer read from designResult map
								// designResult.put(MyPriceConstants.ASR_ITEM_ID, asrItemId);
								// designResult.put(MyPriceConstants.NX_SITE_ID, siteElement.path("siteId").asLong());
								// break the loop if design fails for any consolidationCriteriaKey
								break;
							}
						}
					}
				} else if (StringConstants.TRANSACTION_TYPE_EXISTING.equalsIgnoreCase(transactionType)) {
					/**
					 * reconfigure scenario
					 * */
					List<Object[]> mpSolutionIdAndDesignIds= new ArrayList<Object[]>();
					if(offerName.equalsIgnoreCase(MyPriceConstants.EPLSWAN_OFFER_NAME)||offerName.equalsIgnoreCase(MyPriceConstants.ETHERNET_OFFER_NAME)) {
						mpSolutionIdAndDesignIds = nxDesignRepository.fetchMpsolutionIdForSolutionBySiteId(nxSolutionId, nxSiteIdList, createTransactionResponse.getNxTransacId());
					}
					else {
						mpSolutionIdAndDesignIds = nxDesignRepository.fetchMpsolutionIdForSolution(nxSolutionId, asrIds, createTransactionResponse.getNxTransacId());
					}
					
					boolean status = false;
					List<String> mpSolutionIdAndDesignIdsList = mpSolutionIdAndDesignIds.stream()
				            .map(str -> String.valueOf(str[0]))
				            .collect(Collectors.toList());
					Map<String, Object> removeInputMap = new HashMap<String, Object>();
					removeInputMap.put(MyPriceConstants.NX_TRANSACTION_ID, createTransactionResponse.getNxTransacId());
					removeInputMap.put(MyPriceConstants.MP_TRANSACTION_ID, createTransactionResponse.getMyPriceTransacId());
					removeInputMap.put(MyPriceConstants.MP_SOLUTION_ID_LIST,  mpSolutionIdAndDesignIdsList.stream().map(n -> Long.parseLong(n.toString())).collect(Collectors.toSet()));
					if(CollectionUtils.isNotEmpty(mpSolutionIdAndDesignIdsList)) {
						
						List<Long> nxDesignIds = nxMpDesignDocumentRepository.findNxDesignIdByNxTxnIdAndMpSolutionId(createTransactionResponse.getNxTransacId(), mpSolutionIdAndDesignIdsList);
						removeInputMap.put(MyPriceConstants.NX_DESIGN_ID_LIST, nxDesignIds);
					}
					if(paramMap.containsKey(InrConstants.REQUEST_META_DATA_KEY)) {
						Map<String, Object> requestMetaDataMap = (Map<String, Object>) paramMap.get(InrConstants.REQUEST_META_DATA_KEY);
						removeInputMap.put(InrConstants.REQUEST_META_DATA_KEY, requestMetaDataMap);
					}
					status = processPDtoMPRestUtil.removeTransactionByMpsolutionid(removeInputMap);
					designResult.put(MyPriceConstants.TRANSACTION_TYPE, "REMOVE_TRANSACTION");
					result = status;
					if(status) {
						if(offerName.equalsIgnoreCase(MyPriceConstants.EPLSWAN_OFFER_NAME)||offerName.equalsIgnoreCase(MyPriceConstants.ETHERNET_OFFER_NAME))
						{
							consolidationCriteria.addAll(nxDesignDetailsRepository.findConsolidationCriteriaByNxSolutionIdAndSiteId(nxSolutionId,nxSiteIdList));
						}
						else {
							consolidationCriteria.addAll(nxDesignDetailsRepository.findConsolidationCriteriaByNxSolutionIdAndAsr(nxSolutionId, asrIds));
						}
						Set<String> nameSet = new HashSet<>();
						List<Object[]> consolidationCriteriaList = consolidationCriteria.stream()
						            .filter(e -> nameSet.add(String.valueOf(e[0])+String.valueOf(e[1])+String.valueOf(e[2])))
						            .collect(Collectors.toList());
						for(Object[] ck: consolidationCriteriaList){
							List<NxDesign> nxDesignList=new ArrayList<NxDesign>();
							if(null != ck[1]) {
								
								nxDesignList = nxDesignRepository
										.fetchNxDesignForSolutionByTypeAndBundleCd(nxSolutionId, MyPriceConstants.ASR_STATUS_FOR_RECONFIGURE, String.valueOf(ck[0]),String.valueOf(ck[1]),String.valueOf(ck[2]));
							}
							else {

								nxDesignList = nxDesignRepository
									.fetchNxDesignForSolution(nxSolutionId, MyPriceConstants.ASR_STATUS_FOR_RECONFIGURE, String.valueOf(ck[0]));
							
							}
							if(CollectionUtils.isNotEmpty(nxDesignList)) {
								offerName = nxDesignList.get(0).getBundleCd();
								subOfferName = nxDesignList.stream().findFirst().get().getNxDesignDetails().get(0).getProductName();
								if(offerName.equalsIgnoreCase(StringConstants.OFFERNAME_ETHERNET)) {
									subOfferName = nxDesignList.stream().findFirst().get().getNxDesignDetails().get(0).getType();
								}
								paramMap.put(MyPriceConstants.NX_SOLIUTION_ID, nxSolutionId);
								paramMap.put(MyPriceConstants.OFFER_NAME, offerName);
								paramMap.put(MyPriceConstants.SUB_OFFER_NAME, subOfferName);
								Map<String, Object> restResponseMap = callRestConfigSolAndDeignConsolidation(
										createTransactionResponse, nxDesignList, paramMap, null, null);
								boolean responseStatus = restResponseMap.containsKey(MyPriceConstants.RESPONSE_STATUS)
										? (boolean) restResponseMap.get(MyPriceConstants.RESPONSE_STATUS)
										: false;
								result = responseStatus;
								isMpcallFailed=responseStatus;
								if (restResponseMap.containsKey(MyPriceConstants.TRANSACTION_TYPE)) {
									designResult.put(MyPriceConstants.TRANSACTION_TYPE,
											restResponseMap.get(MyPriceConstants.TRANSACTION_TYPE));
								}
								if (restResponseMap.containsKey(MyPriceConstants.RESPONSE_MSG)) {
									designResult.put(MyPriceConstants.RESPONSE_MSG,
											restResponseMap.get(MyPriceConstants.RESPONSE_MSG));
								}
								if(!result) {
									// both key MyPriceConstants.ASR_ITEM_ID and MyPriceConstants.NX_SITE_ID are no longer read from designResult map
									// designResult.put(MyPriceConstants.ASR_ITEM_ID, asrItemId);
									// designResult.put(MyPriceConstants.NX_SITE_ID, siteElement.path("siteId").asLong());
									// break the loop if design fails for any consolidationCriteriaKey
									break;
								}
							}
							
						}
					}
					
				}
			
			}
	        if(!isMpcallFailed) {
	        	break;
	        }
		}
		Map<String,Long> allAsrItemIdNxSiteIdMap = getAllAsrItemIdSiteId(offers);
		offerName=offer;

		//xy3208 rest version 2 logic finished upto here
		// call update pricing
		Map<String,Object> updatePricRestResponseMap=null;
		offerName=offer;
		designMap.put(MyPriceConstants.OFFER_TYPE,offerName );
		designMap.put(MyPriceConstants.MPS_INDICATOR, mpsIndicator);
		String transactionType = paramMap.containsKey(StringConstants.TRANSACTION_TYPE) ? paramMap.get(StringConstants.TRANSACTION_TYPE).toString() : null;
		if ((isRestCall || StringConstants.VERSION_2.equalsIgnoreCase(solutionRestVersion)) && StringConstants.TRANSACTION_TYPE_NEW.equalsIgnoreCase(transactionType) && callGetTxnLine 
				&& invokeMyprice && !skipMypriceCall) {
			// for new transaction process the getTransaction line for all asritemId , to get part number
			// details and then update pricing
			processPDtoMPRestUtil.processConfigSolDesignResponse(paramMap,createTransactionResponse, offer,subOfferName, nxSolutionId,null,null,offerList,null);
			List<Long> designIdForUpDatePricing=(List<Long>) paramMap.get(MyPriceConstants.DESIGN_ID_FOR_UPDATE_PRICING);
			log.info("List of designIdForUpDatePricing "+designIdForUpDatePricing);
			if(designIdForUpDatePricing!=null && !designIdForUpDatePricing.isEmpty() && result) {
				designMap.put(MyPriceConstants.NX_DESIGN_ID,designIdForUpDatePricing);
				updatePricRestResponseMap=processPDtoMPRestUtil.callUpdatePricing(designMap, MyPriceConstants.AUDIT_UPDATE_PR_REST);
			    updatePricingUnmatchingAsrIdsList = (updatePricRestResponseMap!=null && updatePricRestResponseMap.containsKey(MyPriceConstants.UPDATE_PRICING_UNMATCHING_ASR_IDS))
							? (List<String>) updatePricRestResponseMap.get(MyPriceConstants.UPDATE_PRICING_UNMATCHING_ASR_IDS): null;
			    updatePricingUnmatchingNxDesignIdsList = (updatePricRestResponseMap!=null && updatePricRestResponseMap.containsKey(MyPriceConstants.UPDATE_PRICING_UNMATCHING_NX_DESIGN_IDS))
							? (List<String>) updatePricRestResponseMap.get(MyPriceConstants.UPDATE_PRICING_UNMATCHING_NX_DESIGN_IDS): null;
			    result =(updatePricRestResponseMap!=null && updatePricRestResponseMap.containsKey(MyPriceConstants.RESPONSE_STATUS))?
			    		(boolean) updatePricRestResponseMap.get(MyPriceConstants.RESPONSE_STATUS):false;
			    
			    if((updatePricingUnmatchingAsrIdsList!=null && !updatePricingUnmatchingAsrIdsList.isEmpty()) 
					|| (updatePricingUnmatchingNxDesignIdsList!=null && !updatePricingUnmatchingNxDesignIdsList.isEmpty())) {
			    	result=false;
			    }
				if(updatePricRestResponseMap.containsKey(MyPriceConstants.TRANSACTION_TYPE)) {
					designResult.put(MyPriceConstants.TRANSACTION_TYPE, updatePricRestResponseMap.get(MyPriceConstants.TRANSACTION_TYPE));
				}
				if(updatePricRestResponseMap.containsKey(MyPriceConstants.RESPONSE_MSG)) {
					designResult.put(MyPriceConstants.RESPONSE_MSG, updatePricRestResponseMap.get(MyPriceConstants.RESPONSE_MSG));
				}
				saveUpdatePricingResponseDesignDetails(result, designIdForUpDatePricing, updatePricRestResponseMap);
			}
		} else if ((isRestCall || StringConstants.VERSION_2.equalsIgnoreCase(solutionRestVersion)) && StringConstants.TRANSACTION_TYPE_EXISTING.equalsIgnoreCase(transactionType) && result
				&& invokeMyprice && !skipMypriceCall) {
			List<Long> designIdForUpDatePricing= (List<Long>) paramMap.get(MyPriceConstants.DESIGN_ID_FOR_UPDATE_PRICING);
			log.info("List of designIdForUpDatePricing in case of reconfigure "+designIdForUpDatePricing);
			if(designIdForUpDatePricing!=null && !designIdForUpDatePricing.isEmpty()) {
				// after all config solution and design in case of existing transaction , update pricing to be done
				designMap.put(MyPriceConstants.NX_DESIGN_ID,designIdForUpDatePricing);
				updatePricRestResponseMap=processPDtoMPRestUtil.callUpdatePricing(designMap, MyPriceConstants.AUDIT_UPDATE_PR_REST_RC);
				updatePricingUnmatchingAsrIdsList = (updatePricRestResponseMap!=null && updatePricRestResponseMap.containsKey(MyPriceConstants.UPDATE_PRICING_UNMATCHING_ASR_IDS))
						? (List<String>) updatePricRestResponseMap.get(MyPriceConstants.UPDATE_PRICING_UNMATCHING_ASR_IDS): null;
				updatePricingUnmatchingNxDesignIdsList = (updatePricRestResponseMap!=null && updatePricRestResponseMap.containsKey(MyPriceConstants.UPDATE_PRICING_UNMATCHING_NX_DESIGN_IDS))
						? (List<String>) updatePricRestResponseMap.get(MyPriceConstants.UPDATE_PRICING_UNMATCHING_NX_DESIGN_IDS): null;
				result =(updatePricRestResponseMap!=null && updatePricRestResponseMap.containsKey(MyPriceConstants.RESPONSE_STATUS))?(boolean) updatePricRestResponseMap.get(MyPriceConstants.RESPONSE_STATUS):false;
				if((updatePricingUnmatchingAsrIdsList!=null && !updatePricingUnmatchingAsrIdsList.isEmpty()) 
						|| (updatePricingUnmatchingNxDesignIdsList!=null && !updatePricingUnmatchingNxDesignIdsList.isEmpty())) {
					result=false;
				}
				
				if(updatePricRestResponseMap.containsKey(MyPriceConstants.TRANSACTION_TYPE)) {
					designResult.put(MyPriceConstants.TRANSACTION_TYPE, updatePricRestResponseMap.get(MyPriceConstants.TRANSACTION_TYPE));
				}
				if(updatePricRestResponseMap.containsKey(MyPriceConstants.RESPONSE_MSG)) {
					designResult.put(MyPriceConstants.RESPONSE_MSG, updatePricRestResponseMap.get(MyPriceConstants.RESPONSE_MSG));
				}
				saveUpdatePricingResponseDesignDetails(result, designIdForUpDatePricing, updatePricRestResponseMap);
			}
			List<Long> designIdForUpDatePricingReconfigure=(List<Long>) paramMap.get(MyPriceConstants.DESIGN_ID_FOR_UPDATE_PRICING_RECONFIGURE);
			log.info("List of designIdForUpDatePricingReconfigure in case of reconfigure "+designIdForUpDatePricingReconfigure);
			if(designIdForUpDatePricingReconfigure!=null && !designIdForUpDatePricingReconfigure.isEmpty()) {
				//if update pricing has to be done for reconfigure
				designMap.put(MyPriceConstants.NX_DESIGN_ID,designIdForUpDatePricingReconfigure);
				designMap.put(StringConstants.TRANSACTION_UPDATE,StringConstants.RECONFIGURE);
				updatePricRestResponseMap=processPDtoMPRestUtil.callUpdatePricing(designMap, MyPriceConstants.AUDIT_UPDATE_PR_REST_RC);
				designMap.remove(StringConstants.TRANSACTION_UPDATE);
				updatePricingUnmatchingAsrIdsList = (updatePricRestResponseMap!=null && updatePricRestResponseMap.containsKey(MyPriceConstants.UPDATE_PRICING_UNMATCHING_ASR_IDS))
						? (List<String>) updatePricRestResponseMap.get(MyPriceConstants.UPDATE_PRICING_UNMATCHING_ASR_IDS): null;
				updatePricingUnmatchingNxDesignIdsList = (updatePricRestResponseMap!=null && updatePricRestResponseMap.containsKey(MyPriceConstants.UPDATE_PRICING_UNMATCHING_NX_DESIGN_IDS))
						? (List<String>) updatePricRestResponseMap.get(MyPriceConstants.UPDATE_PRICING_UNMATCHING_NX_DESIGN_IDS): null;
				result =(updatePricRestResponseMap!=null && updatePricRestResponseMap.containsKey(MyPriceConstants.RESPONSE_STATUS))?(boolean) updatePricRestResponseMap.get(MyPriceConstants.RESPONSE_STATUS):false;
				if((updatePricingUnmatchingAsrIdsList!=null && !updatePricingUnmatchingAsrIdsList.isEmpty()) 
						|| (updatePricingUnmatchingNxDesignIdsList!=null && !updatePricingUnmatchingNxDesignIdsList.isEmpty())) {
					result=false;
				}
				if(updatePricRestResponseMap.containsKey(MyPriceConstants.TRANSACTION_TYPE)) {
					designResult.put(MyPriceConstants.TRANSACTION_TYPE, updatePricRestResponseMap.get(MyPriceConstants.TRANSACTION_TYPE));
				}
				if(updatePricRestResponseMap.containsKey(MyPriceConstants.RESPONSE_MSG)) {
					designResult.put(MyPriceConstants.RESPONSE_MSG, updatePricRestResponseMap.get(MyPriceConstants.RESPONSE_MSG));
				}
				saveUpdatePricingResponseDesignDetails(result, designIdForUpDatePricingReconfigure,
						updatePricRestResponseMap);
			}
		}
		if(!skipDmaapForReopenOrSold || callDmappCount > 0) {
			designResult.put(MyPriceConstants.NX_AUDIT_ID, paramMap.get(MyPriceConstants.NX_AUDIT_ID));
			designResult.put(MyPriceConstants.MYPRICE_DESIGN, "design");
			if(result) {
				myPriceTransactionUtil.sendDmaapEvents(myPriceTransactionUtil.getNxMpDeal(createTransactionResponse.getNxTransacId()), nxSolutionDetail, com.att.sales.nexxus.common.CommonConstants.SUBMITTED, designResult);
			} else {
				designResult.put(MyPriceConstants.NX_SITE_ID_ASR_ITEM_ID_MAP,allAsrItemIdNxSiteIdMap);
				myPriceTransactionUtil.sendDmaapEvents(myPriceTransactionUtil.getNxMpDeal(createTransactionResponse.getNxTransacId()), nxSolutionDetail, com.att.sales.nexxus.common.CommonConstants.FAILED, designResult);
			}
		}
		if(createTransactionResponse != null) {
			NxMpDeal nxMpDeal = nxMpDealRepository.findByNxTxnId(createTransactionResponse.getNxTransacId());
			if (Optional.ofNullable(nxMpDeal).isPresent() && !com.att.sales.nexxus.common.CommonConstants.APPROVED.equalsIgnoreCase(nxMpDeal.getDealStatus())) {
				if (result) {
					nxMpDeal.setDealStatus(com.att.sales.nexxus.common.CommonConstants.SUBMITTED);
				} else {
					nxMpDeal.setDealStatus(com.att.sales.nexxus.common.CommonConstants.FAILED);
				}
				nxMpDeal.setModifiedDate(new Date());
				nxMpDealRepository.save(nxMpDeal);
			}
		}
	
		//Log.info("result : "+result);
		log.info("End PedSnsdServiceUtil :saveDesignData");
		paramMap.put("dppRequest", request);
		return result;
	}

	protected NxDesign saveDB(NxSolutionDetail nxSolutionDetail, String userId, String userFirstName,
			String userLastName, String designStatus, String designModifiedInd, String offerName, JsonNode design, String asrItemId, 
			Map<String, Object> paramMap, ObjectNode originalDesign,boolean isSolutionRestVer2Call,String circuitType,Long siteID, String endpointIdentifier, 
			Boolean isSubmitToMp, String pedStatusInd,String circuitNumber) {
		
		boolean isEplsOffer=(offerName.equalsIgnoreCase(StringConstants.OFFERNAME_EPLSWAN)||offerName.equalsIgnoreCase(StringConstants.OFFERNAME_ETHERNET))?true:false;
		if ((StringUtils.isNotEmpty(asrItemId) && null != asrItemId)||(isEplsOffer&&null != siteID)) {
			NxDesign nxDesign=null;
			if(isEplsOffer) {
				nxDesign = nxDesignRepository.findBySiteIdAndNxSolutionDetailAndBundleCdAndCircuitId(siteID, nxSolutionDetail,offerName,circuitNumber);
			}
			else {
				nxDesign = nxDesignRepository.findByAsrItemIdAndNxSolutionDetailAndBundleCd(asrItemId, nxSolutionDetail,offerName);
			}
			
		//	List<NxDesignDetails> nxDesignDetailsList = null;
			long designVersion = 1L;
			boolean completeAsrDesignUpdate = false;
			if (null != nxDesign) {
				paramMap.put(StringConstants.SITE_TYPE, StringConstants.SITE_TYPE_NEW);
				//nxDesignDetailsList = designDetailsRepo.findByNxDesign(nxDesign);
				Long oldDesignVersion = nxDesign.getDesignVersion();
				if (oldDesignVersion != null) {
					designVersion = oldDesignVersion;
				}
				completeAsrDesignUpdate = "D".equalsIgnoreCase(nxDesign.getStatus())
						&& StringConstants.DESIGN_UPDATE.equalsIgnoreCase(designStatus)
						&& "Y".equalsIgnoreCase(designModifiedInd);
				if ((StringConstants.DESIGN_UPDATE.equalsIgnoreCase(designStatus)
					//	|| StringConstants.DESIGN_REOPEN.equalsIgnoreCase(designStatus) -- commented this line to avoid inc designversion for reopen scenario 2/25/2021
						) && "Y".equalsIgnoreCase(designModifiedInd) && !completeAsrDesignUpdate && !StringConstants.CONSTANT_N.equalsIgnoreCase(pedStatusInd)) {
						nxDesign.setDesignVersion(++designVersion);
				}
			} else {
				paramMap.put(StringConstants.SITE_TYPE, StringConstants.SITE_TYPE_EXISTING);
				nxDesign = new NxDesign();
				nxDesign.setAsrItemId(asrItemId);
				nxDesign.setNxSolutionDetail(nxSolutionDetail);
				nxDesign.setDesignVersion(designVersion);
			}
			originalDesign.put("designVersion", designVersion);
			((ObjectNode) design).put("designVersion", designVersion);

			nxDesign.setAttuId(filterOutEmpty(userId));
			nxDesign.setBundleCd(filterOutEmpty(offerName));
			nxDesign.setFisrtName(filterOutEmpty(userFirstName));
			nxDesign.setLastName(filterOutEmpty(userLastName));
			nxDesign.setDesignType(circuitType); 
			nxDesign.setSiteId(siteID);
			nxDesign.setCircuitId(circuitNumber);
			if (!completeAsrDesignUpdate) {
				nxDesign.setStatus(filterOutEmpty(designStatus));
			}
			nxDesign.setModifedDate(new Date());
			List<Long> nxSiteIds = new ArrayList<Long>();
			NxDesignDetails nxDesignDetails;
			if (nxDesign.getNxDesignDetails() == null || nxDesign.getNxDesignDetails().isEmpty()) {
				nxDesignDetails = new NxDesignDetails();
				nxDesignDetails.setDesignData(design.toString());
				nxDesignDetails.setType(endpointIdentifier);
				nxDesign.addNxDesignDetails(nxDesignDetails);
				if (StringConstants.OFFERNAME_ASE.equals(offerName) || StringConstants.OFFERNAME_ASENOD.equalsIgnoreCase(offerName)) {
					Long nxSiteId = design.path("nxSiteId").asLong();
					//save the nxSiteId details
					nxDesign.setSiteId(nxSiteId);
				}
			} else {
				nxDesignDetails = nxDesign.getNxDesignDetails().get(0);
				if(filterOutEmpty(nxDesignDetails.getDesignData()) != null) {
					JsonNode currentDesignDetails = JacksonUtil.toJsonNode(nxDesignDetails.getDesignData());
					if (StringConstants.OFFERNAME_ASE.equals(offerName) || StringConstants.OFFERNAME_ASENOD.equalsIgnoreCase(offerName)) {
						Long nxSiteId = currentDesignDetails.path("nxSiteId").asLong();
						ObjectNode siteNodeObj = (ObjectNode) design;
						siteNodeObj.put("nxSiteId", nxSiteId);
						nxSiteIds.add(nxSiteId);
						//save the nxSiteId details
						nxDesign.setSiteId(nxSiteId);
//					} else if (StringConstants.OFFERNAME_ADE.equals(offerName)) {
					}else {	
						JsonNode oldSite = currentDesignDetails.path("site");
						JsonNode newSite = design.path("site");
						
						for(JsonNode siteElement : oldSite) {
							Long siteId = siteElement.path("siteId").asLong();
							Long nxSiteId = siteElement.path("nxSiteId").asLong();
							int endPointRef = siteElement.path("_endPointRef").asInt();
							log.info("nxSiteId is"+nxSiteId);
							
							for(JsonNode newSiteElement : newSite) {
								Long newSiteId = newSiteElement.path("siteId").asLong();
								if(Long.compare(siteId, newSiteId) == 0 && nxSiteId!=null && nxSiteId != 0L) {
									ObjectNode siteNodeObj = (ObjectNode) newSiteElement;
									siteNodeObj.put("nxSiteId", nxSiteId);
									siteNodeObj.put("_endPointRef", endPointRef);
									nxSiteIds.add(nxSiteId);
								}
							}
						}
					}
				}
				nxDesign.getNxDesignDetails().get(0).setDesignData(design.toString());
				nxDesign.getNxDesignDetails().get(0).setRestResponseError(null);
				nxDesign.getNxDesignDetails().get(0).setStatus(null);
			}
			
			String solutionStatus=paramMap.containsKey(StringConstants.SOLUTION_STATUS) ?(String) paramMap.get(StringConstants.SOLUTION_STATUS ):"";
			String sourcName=paramMap.containsKey(StringConstants.SOURCE_NAME) ?(String) paramMap.get(StringConstants.SOURCE_NAME ):"";
			
			if(sourcName.equalsIgnoreCase(StringConstants.IPNE) && null!= isSubmitToMp && !isSubmitToMp &!solutionStatus.equalsIgnoreCase("C") 
					&& "Y".equalsIgnoreCase(designModifiedInd)){
				
				nxDesign.setSubmitToMp("N");
			} 
			if(CollectionUtils.isNotEmpty(nxSiteIds)) {
				paramMap.put(MyPriceConstants.CURRENT_NX_SITE_ID, nxSiteIds);
			}
			nxDesignDetails.setModifedDate(new Date());
			if (isSolutionRestVer2Call && ("N".equalsIgnoreCase(designStatus) || "U".equalsIgnoreCase(designStatus))) {
				/**
				 * to generate the consolidation key
				 */
				if (StringConstants.OFFERNAME_ASENOD.equalsIgnoreCase(offerName)) {
					String subOfferName = null;
					String thirdPartyInd = design.path("thirdPartyInd").asText();
					if (StringUtils.isNotEmpty(thirdPartyInd) && thirdPartyInd.equalsIgnoreCase("Y")) {
						subOfferName = MyPriceConstants.ASENOD_3PA;
					} else {
						subOfferName = MyPriceConstants.ASENOD_IR;
					}
					nxDesign.getNxDesignDetails().get(0).setProductName(subOfferName);
				}
				String pricescenarioId=paramMap.containsKey(StringConstants.PRICE_SCENARIO_ID)
						?(String) paramMap.get(StringConstants.PRICE_SCENARIO_ID):"";
				String consolidationKey = generateConsolidationKey(offerName, design, nxDesignDetails,pricescenarioId);			
				log.info("consolidationKey :::::::" + consolidationKey);
				nxDesignDetails.setConsolidationCriteria(consolidationKey);
				/**
				 * to append the consolidated data with the design details
				 */
			}
			nxMpRepositoryService.saveNxDesignDatas(nxDesign);
			return nxDesign;
		}
		return null;
	}

	protected String findAseAsrItemId(JsonNode site) {
		/*String asrItemId = site.path("asrItemId").isNull() ? null : site.path("asrItemId").asText();
		if (StringUtils.isNotEmpty(asrItemId) && null != asrItemId) {
			return asrItemId;
		}*/ // commented this code to read asrItermid at udf at all places
		JsonNode port = site.at("/designSiteOfferPort/0");
		return JacksonUtil.findUdfAttributeTextFromComponentNode(JacksonUtil.findComponentNode(port, 30), 20169);
	}

	public String findAdeAsrItemId(JsonNode circuit) {
	/*	String asrItemId = circuit.path("asrItemId").isNull() ? null : circuit.path("asrItemId").asText();
		if (StringUtils.isNotEmpty(asrItemId) && null != asrItemId) {
			return asrItemId;
		}*/ // commented this code to read asrItermid at udf at all places
		return JacksonUtil.findUdfAttributeTextFromComponentNode(JacksonUtil.findComponentNode(circuit, 1210), 200162);
	}
	
	protected Long findPopCollectorUdfAttributeId(JsonNode site, int udfId) {
		JsonNode port = site.at("/designSiteOfferPort/0");
		return JacksonUtil.findUdfAttributeIdFromComponentNode(JacksonUtil.findComponentNode(port, 30), udfId);
	}

	protected String filterOutEmpty(String s) {
		if (StringUtils.isEmpty(s)) {
			return null;
		}
		return s;
	}

	@SuppressWarnings("unchecked")
	public String prepareUpdateOrchBaseRequest(RetreiveICBPSPRequest inputrequest) {
		JSONObject updateOrchJson = new JSONObject();
		List<ActionDeterminants> actionDeterminants = inputrequest.getActionDeterminants();
		updateOrchJson.put("actionDeterminants", getActionDeterminants(actionDeterminants));
		updateOrchJson.put("solution", getSolutionObject(inputrequest.getSolution()));
		return updateOrchJson.toJSONString();
	}

	@SuppressWarnings("unchecked")
	protected JSONArray getActionDeterminants(List<ActionDeterminants> actionDeterminants) {
		JSONArray actionDts = new JSONArray();
		if (CollectionUtils.isNotEmpty(actionDeterminants) && null != actionDeterminants.get(0)) {
			ActionDeterminants newInputActionDtr=new ActionDeterminants();
			newInputActionDtr.setActivity(TDDConstants.UPDATE_DESIGN_ACTIVITY);
			newInputActionDtr.setComponent(actionDeterminants.get(0).getComponent());
			actionDts.add(JacksonUtil.convertObjectToJsonObject(newInputActionDtr));
		}
		return actionDts;
	}

	@SuppressWarnings("unchecked")
	protected JSONObject getSolutionObject(Solution solution) {
		JSONObject obj = new JSONObject();
		obj.put("solutionDeterminants", JacksonUtil.convertObjectToJsonObject(solution.getSolutionDeterminants()));
		obj.put("pricerDSolutionId", solution.getExternalKey());
		obj.put("leadDesignID", solution.getExternalKey());
		obj.put("automationInd", solution.getAutomationInd());
		obj.put("erateInd", solution.getErateInd());
		obj.put("bulkInd", solution.getBulkInd());
		obj.put("marketStrata", solution.getMarketStrata());
		obj.put("layer", solution.getLayer());
		obj.put("solutionStatus", solution.getSolutionStatus());
		obj.put("bundleCode", solution.getBundleCode());
		obj.put("cancellationReason", solution.getCancellationReason());
		obj.put("offers", getOfferObj(solution.getOffers()));
		obj.put("contractType", solution.getContractType());
		return obj;
	}

	@SuppressWarnings("unchecked")
	protected JSONArray getOfferObj(List<Offer> offers) {
		JSONArray array = new JSONArray();
		JSONObject obj = new JSONObject();
		obj.put("offerId", offers.get(0).getOfferId());
		obj.put("site", new JSONArray());
		array.add(obj);
		return array;
	}

	protected void saveSolutionData(RetreiveICBPSPRequest request, Long solutionId) {
		NxDesignAudit nxDesignAudit = nxDesignAuditRepository.findByNxRefIdAndTransaction(solutionId,
				CommonConstants.SOLUTION_DATA);
		if (nxDesignAudit != null) {
			nxDesignAudit.setModifedDate(new Date());
			nxDesignAudit.setStatus("Completed");
			nxDesignAudit.setData(this.prepareUpdateOrchBaseRequest(request));
		} else {
			nxDesignAudit = new NxDesignAudit();
			nxDesignAudit.setNxRefId(solutionId);
			nxDesignAudit.setStatus("Completed");
			nxDesignAudit.setTransaction(CommonConstants.SOLUTION_DATA);
			nxDesignAudit.setCreatedDate(new Date());
			nxDesignAudit.setData(this.prepareUpdateOrchBaseRequest(request));
		}
		nxDesignAuditRepository.save(nxDesignAudit);
	}
	
	protected void saveUpdatePricingResponseDesignDetails(Boolean result, List<Long> designIdForUpDatePricing,
			Map<String, Object> updatePricRestResponseMap) {
		List<NxDesignDetails> nxDesinDetailList = nxDesignDetailsRepository.findByNxDesignIdIn(designIdForUpDatePricing);
		if(result) {
			List<NxDesignDetails> nxDesinDetailListToSave= new ArrayList<>(); 
			for(NxDesignDetails nxDesignDetails:nxDesinDetailList) {
				nxDesignDetails.setStatus(MyPriceConstants.REST_API_SUCCEED);
				nxDesinDetailListToSave.add(nxDesignDetails);
			}
			nxDesignDetailsRepository.saveAll(nxDesinDetailListToSave);
		}else {
			List<NxDesignDetails> nxDesinDetailListToSave= new ArrayList<>(); 
			String respponseError=updatePricRestResponseMap.containsKey(MyPriceConstants.RESPONSE_MSG)?
					(String) updatePricRestResponseMap.get(MyPriceConstants.RESPONSE_MSG):"Error while update Pricing";
			for(NxDesignDetails nxDesignDetails:nxDesinDetailList) {
				nxDesignDetails.setStatus(MyPriceConstants.REST_API_FAILED);
				nxDesignDetails.setRestResponseError(respponseError);
				nxDesinDetailListToSave.add(nxDesignDetails);
			}
			nxDesignDetailsRepository.saveAll(nxDesinDetailListToSave);
		}
	}

	protected Map<String,Object> callRestConfigSolAndDeign(CreateTransactionResponse createTransactionResponse,NxDesign nxDesign,
			Map<String, Object> paramMap,JsonNode siteElement,String asritemId){
		Map<String,Object> restResponseMap=processPDtoMPRestUtil.restConfigAndDesign(createTransactionResponse,nxDesign, 
				paramMap,siteElement,asritemId);
		boolean responseStatus=restResponseMap.containsKey(MyPriceConstants.RESPONSE_STATUS)?
				(boolean) restResponseMap.get(MyPriceConstants.RESPONSE_STATUS):false;
		NxDesignDetails nxDesignDetails=null;
		if (null != nxDesign && CollectionUtils.isNotEmpty(nxDesign.getNxDesignDetails())){
			nxDesignDetails=nxDesign.getNxDesignDetails().get(0);
		}
		long nxDesignIdConfigured=restResponseMap.containsKey(MyPriceConstants.NX_DESIGN_ID_SUCCESSFULLY_CONFIGURED)?
				(long)restResponseMap.get(MyPriceConstants.NX_DESIGN_ID_SUCCESSFULLY_CONFIGURED):0;
		boolean restMpConfigRequest=restResponseMap.containsKey(MyPriceConstants.REST_MP_CONFIG)?
				(boolean)restResponseMap.get(MyPriceConstants.REST_MP_CONFIG):false;
		if(restMpConfigRequest) {		
			if (!responseStatus) {
				String errorDetails=restResponseMap.containsKey(MyPriceConstants.REST_ERROR_MSG)
						?(String) restResponseMap.get(MyPriceConstants.REST_ERROR_MSG):"";
				nxDesignDetails.setRestResponseError(errorDetails);
				nxDesignDetails.setStatus(MyPriceConstants.REST_API_FAILED);
				restResponseMap.put(MyPriceConstants.RESPONSE_MSG, errorDetails);
			} else if (responseStatus && nxDesignIdConfigured != 0) {
				nxDesignDetails.setStatus(MyPriceConstants.REST_API_SUCCEED);
			}
			nxDesignDetailsRepository.saveAndFlush(nxDesignDetails);
		}
		return restResponseMap;
	}
	
	protected Map<String,Object> callRestConfigSolAndDeignConsolidation(CreateTransactionResponse createTransactionResponse,List<NxDesign> nxDesignList,
			Map<String, Object> paramMap, List<JsonNode> siteElementList, List<String> asritemIdList) {
		Map<String, Object> restResponseMap = processPDtoMPRestUtil.restConfigAndDesignConsolidation(
				createTransactionResponse, nxDesignList, paramMap, siteElementList, asritemIdList);
		boolean responseStatus = restResponseMap.containsKey(MyPriceConstants.RESPONSE_STATUS)
				? (boolean) restResponseMap.get(MyPriceConstants.RESPONSE_STATUS)
				: false;
		List<NxDesignDetails> nxDesignDetailsList = nxDesignList.stream()
				.flatMap(nxDesign -> nxDesign.getNxDesignDetails().stream()).collect(Collectors.toList());
		long nxDesignIdConfigured = restResponseMap.containsKey(MyPriceConstants.NX_DESIGN_ID_SUCCESSFULLY_CONFIGURED)
				? (long) restResponseMap.get(MyPriceConstants.NX_DESIGN_ID_SUCCESSFULLY_CONFIGURED)
				: 0;
		boolean restMpConfigRequest = restResponseMap.containsKey(MyPriceConstants.REST_MP_CONFIG)
				? (boolean) restResponseMap.get(MyPriceConstants.REST_MP_CONFIG)
				: false;
		if (restMpConfigRequest) {
			if (!responseStatus) {
				String errorDetails = restResponseMap.containsKey(MyPriceConstants.REST_ERROR_MSG)
						? (String) restResponseMap.get(MyPriceConstants.REST_ERROR_MSG)
						: "";
				nxDesignDetailsList.forEach(nxDesignDetails -> {
					nxDesignDetails.setRestResponseError(errorDetails);
					nxDesignDetails.setStatus(MyPriceConstants.REST_API_FAILED);
					nxDesignDetailsRepository.saveAndFlush(nxDesignDetails);
				});
				restResponseMap.put(MyPriceConstants.RESPONSE_MSG, errorDetails);
			} else if (responseStatus && nxDesignIdConfigured != 0) {
				nxDesignDetailsList.forEach(nxDesignDetails -> {
					nxDesignDetails.setStatus(MyPriceConstants.REST_API_SUCCEED);
					nxDesignDetailsRepository.saveAndFlush(nxDesignDetails);
				});

			}
		}
		return restResponseMap;
	}
	
	protected Map<String,Long> getAllAsrItemIdSiteId(JsonNode offers) {
		Map<String,Long> allAsrItemIdNxSiteIdMap = new HashMap<>();
		for (JsonNode offerElement : offers) {
			String offerId = offerElement.path("offerId").asText();
			String offerName=null;
			if (StringUtils.isNotEmpty(offerId)) {
				int id = Integer.parseInt(offerId);
				offerName = salesMsDao.getOfferNameByOfferId(id);
			}
			JsonNode site = offerElement.path("site");
			if (StringConstants.OFFERNAME_ASE.equals(offerName) || StringConstants.OFFERNAME_ASENOD.equalsIgnoreCase(offerName)) {
				for (JsonNode siteElement : site) {
					String asrItemId = findAseAsrItemId(siteElement);
					allAsrItemIdNxSiteIdMap.put(asrItemId, siteElement.path("siteId").asLong());
				}
			}
			 else if (StringConstants.OFFERNAME_ADE.equals(offerName)) {
					JsonNode circuit = offerElement.path("circuit");
					Long circuitId = null;
				     for (JsonNode circuitElement : circuit) {
						JsonNode circuitElementCopy = circuitElement.deepCopy();
						String asrItemId = findAdeAsrItemId(circuitElementCopy);
						JsonNode component = circuitElementCopy.path("component");
						for (JsonNode componentElement : component) {
							JsonNode componentCodeIdNode = componentElement.path("componentCodeId");
							JsonNode componentCodeTypeNode = componentElement.path("componentCodeType");
							if("Circuit".equalsIgnoreCase(componentCodeTypeNode.asText())) {
								circuitId = componentElement.path("componentId").asLong();
							}
						}
						allAsrItemIdNxSiteIdMap.put(asrItemId,circuitId);
				 }
			 }
		}
		return allAsrItemIdNxSiteIdMap;
	}
	
	protected String generateConsolidationKey(String offerName, JsonNode design, NxDesignDetails nxDesignDetails,String pricescenarioId) {
		Map<String, Object> consolidationCriteriaValueMap = new LinkedHashMap<String, Object>();
		String subOfferName = null;
		String productType = "PD";
		String itemID = null;
		long nxSolutionId = nxDesignDetails.getNxDesign().getNxSolutionDetail().getNxSolutionId();
		NxDesignAudit nxDesignAudit=nxDesignAuditRepository.findByNxRefIdAndTransaction(
				nxSolutionId,CommonConstants.SOLUTION_DATA);
		String thirdPartyInd = design.path("thirdPartyInd").asText();
		if(null!=nxDesignAudit && nxDesignAudit.getData() != null) {
			try {
				JsonNode solutionData = mapper.readTree(nxDesignAudit.getData());
				JsonNode siteArray = solutionData.at("/solution/offers/0/site");
				((ArrayNode) siteArray).add(design);
				design = solutionData;
			} catch (IOException e) {
				log.error("Exception", e);
			}
		}
		Map<String, Object> requestMap= new HashMap<>();
	    JSONObject consolidationDataObj = new JSONObject();
		requestMap.put(MyPriceConstants.OFFER_NAME, offerName);
//		if (MyPriceConstants.ASENOD_OFFER_NAME.equalsIgnoreCase(offerName)) {
			if (StringUtils.isNotEmpty(thirdPartyInd) && thirdPartyInd.equalsIgnoreCase("Y")) {
				subOfferName = MyPriceConstants.ASENOD_3PA;
				itemID = offerName.concat("/").concat(subOfferName);
			} else {
				subOfferName = MyPriceConstants.ASENOD_IR;
				itemID = offerName.concat("/").concat(subOfferName);
			}
			if((MyPriceConstants.EPLSWAN_OFFER_NAME.equalsIgnoreCase(offerName)||MyPriceConstants.ETHERNET_OFFER_NAME.equalsIgnoreCase(offerName))) {
				subOfferName = nxDesignDetails.getType();
				itemID = subOfferName!=null?offerName.concat("/").concat(subOfferName):offerName;
			}
			requestMap.put(MyPriceConstants.SUB_OFFER_NAME, subOfferName);
			requestMap.put(StringConstants.PRICE_SCENARIO_ID,pricescenarioId);
			List<NxLookupData> rulesData = nxLookupDataRepository.findByDatasetNameAndItemIdAndDescription(
					CustomJsonConstants.REST_V2_CONSOLIDATION_CRITERIA, itemID, productType);
			if (CollectionUtils.isNotEmpty(rulesData)) {
				for (NxLookupData ruleObj : rulesData) {
					LinkedHashMap<String, Object> criteriaMap = (LinkedHashMap<String, Object>) nexxusJsonUtility
							.convertStringJsonToMap(ruleObj.getCriteria());
					for (Map.Entry<String, Object> x : criteriaMap.entrySet()) {
						String derivedvalue = null;
						if (x.getValue() instanceof Map<?, ?>) {
							Map<String, String> mapData = (Map<String, String>) x.getValue();
							if (mapData.containsKey("type")) {
								String mapDataValue = mapData.get("type");
								if ("default".equalsIgnoreCase(mapDataValue)) {
									derivedvalue = mapData.get("value");
								} else if ("custom".equalsIgnoreCase(mapDataValue)) {
									NxMpConfigJsonMapping nxMpConfigjsonMapping = new NxMpConfigJsonMapping();
									nxMpConfigjsonMapping.setKey(x.getKey());
									nxMpConfigjsonMapping.setOffer(MyPriceConstants.ASENOD_OFFER_NAME);
									nxMpConfigjsonMapping.setSubOffer(subOfferName);
									String inputPath="";
									if (mapData.containsKey("inputPathValue")) {
										inputPath=mapData.get("inputPathValue");
									}
									nxMpConfigjsonMapping.setInputPath(inputPath);
									derivedvalue = configAndUpdateRestUtilPd.processCustomFields(nxMpConfigjsonMapping,
											design, consolidationCriteriaValueMap, String.class);
								} else if ("inputPath".equalsIgnoreCase(mapDataValue)) {
									String jsonPath = mapData.get("value");
									if (jsonPath.contains(MyPriceConstants.MULTI_JSON_PATH_SEPERATOR)) {
										List<String> pathList = new ArrayList<String>(Arrays.asList(jsonPath
												.split(Pattern.quote(MyPriceConstants.MULTI_JSON_PATH_SEPERATOR))));
										derivedvalue = processMultipleJsonPath(design, pathList, mapData);
									} else if(jsonPath.contains(MyPriceConstants.OR_CONDITION_SEPERATOR)){
										List<String> pathList= new ArrayList<String>(Arrays.asList(jsonPath.split(
												Pattern.quote(MyPriceConstants.OR_CONDITION_SEPERATOR))));
										derivedvalue = processOrCondition(design, pathList, mapData);
										mapData.remove("dataSetName");
									}else {
										derivedvalue = restCommonUtil.getItemValueUsingJsonPath(jsonPath, design,
												String.class);
									}
									if (mapData.containsKey("dataSetName")) {
										derivedvalue = restCommonUtil.processDataSetName(derivedvalue,
												mapData.get("dataSetName"), String.class);
									}
								}else if("customConfigRule".equalsIgnoreCase(mapDataValue)) {
									derivedvalue=null;
									configAndUpdateRestUtilPd.process3PAUcoIdFromLookup(requestMap);
									String jsonPath= mapData.get("value");
									List<String> derived3PAUsocList=null;
									if((offerName.equals(MyPriceConstants.ASENOD_OFFER_NAME) && MyPriceConstants.ASENOD_IR.equals(subOfferName))) {
										Map<String,Map<String,String>> usocIdCategoryMap=this.processConfigDataASE(requestMap,design,jsonPath,offerName);
										if(MapUtils.isNotEmpty(usocIdCategoryMap)) {
											org.json.JSONArray usocArray= new org.json.JSONArray();
											List<String> usocCategoryKeyList = new ArrayList<>();
											for (Map.Entry<String,Map<String,String>>  usocEntry : usocIdCategoryMap.entrySet()) {
												usocArray.put(usocEntry.getValue());
												String usocIdCategoryKey=usocEntry.getValue().entrySet()
														.stream().map(e -> e.getValue())
														.collect(joining("&"));	
												usocCategoryKeyList.add(usocIdCategoryKey);
											}
											consolidationCriteriaValueMap.put(x.getKey(), String.join("$", usocCategoryKeyList));
											JSONObject jsonOB= new JSONObject();
											jsonOB.put("item", usocArray);
											consolidationDataObj.put("usocDetails", jsonOB);
										}
									}else if(offerName.equals(MyPriceConstants.ASENOD_OFFER_NAME) && MyPriceConstants.ASENOD_3PA.equals(subOfferName)) {
										derived3PAUsocList=this.processConfigDataASENoD3PA(requestMap, design, jsonPath);	
										if(derived3PAUsocList!=null) {
											derivedvalue=String.join("$", derived3PAUsocList);
										}
									}
								}
							}
						}
						if(derivedvalue!=null) {
							consolidationCriteriaValueMap.put(x.getKey(), derivedvalue);
							consolidationDataObj.put(x.getKey(), derivedvalue);
						}
					}
				}
			}
//		}
		String consolidationKey = "";
		log.info("consolidationCriteriaValueMap ::" + consolidationCriteriaValueMap);
		if (MapUtils.isNotEmpty(consolidationCriteriaValueMap)) {
			String consolidationCriteriadata=consolidationDataObj.toString().replace("\\/","/");
			nxDesignDetails.setConsolidationCriteriaData(consolidationCriteriadata);
			List<String> consolidationKeyValues = consolidationCriteriaValueMap.entrySet().stream()
					.map(Map.Entry::getValue).filter(n->n!=null)
					.map(n -> n.toString()).filter(n -> !n.isEmpty())
					.collect(Collectors.toList());
			consolidationKey = String.join("$", consolidationKeyValues);

			//log.info("consolidationKey for "+offerName+"_"+subOfferName+" is ...." + consolidationKey);
		}
		return consolidationKey.toString();
	}
	

	protected Map<String,Map<String,String>> processConfigDataASE(Map<String, Object> requestMap, Object inputDesign, String jsonPath,
			String offerName) {
		Map<String,Map<String,String>> usocCategoryMap=new TreeMap<>();	
		Map<String,Map<String,String>> usocDataMap=configAndUpdateRestUtilPd.createDataMapByUsocId(JacksonUtil.toJsonObject(inputDesign.toString()),requestMap,jsonPath);
		if(MapUtils.isNotEmpty(usocDataMap)) {
			for (Map.Entry<String,Map<String,String>>  usocEntry : usocDataMap.entrySet()) {
				String usocId=usocEntry.getKey();
				Map<String,String> usocFieldsDataMap=usocEntry.getValue();
				//derived the New/Existing/Migration category for usoc id using rule from nx_look_up table
				String usocIdCategory=configAndUpdatePricingUtil.getUsocIdCategory(JacksonUtil.toJsonObject(inputDesign.toString()), offerName, usocFieldsDataMap,requestMap);
				if(StringUtils.isNotEmpty(usocIdCategory)) {
					Map<String,String> categoryDataMap=new LinkedHashMap<>();
					categoryDataMap.put("usocId", usocId);
					categoryDataMap.put("category", usocIdCategory);
					usocCategoryMap.put(usocId, categoryDataMap);
				}
			}
		}
		return usocCategoryMap;
	}

	protected List<String>  processConfigDataASENoD3PA(Map<String, Object> requestMap, Object inputDesign, String jsonPath) {
		List<String> usocIdList = new ArrayList<>(); 
		Map<String,Map<String,String>> usocDataMap=configAndUpdateRestUtilPd.createDataMapByUsocId(JacksonUtil.toJsonObject(inputDesign.toString()),requestMap,jsonPath);
		if(MapUtils.isNotEmpty(usocDataMap)) {
			for (Map.Entry<String,Map<String,String>>  usocEntry : usocDataMap.entrySet()) {
				String usocId=usocEntry.getKey();
				//convert request usocId using lookupdata range	
				String updatedUsocId=configAndUpdateRestUtilPd.getConvertedUsocIdFor3PA(requestMap, usocId);
				usocIdList.add(updatedUsocId);
			}
		}
		Collections.sort(usocIdList);
		return usocIdList;
	}
	
	protected String processMultipleJsonPath(Object inputDesignJson, List<String> pathList,
			Map<String, String> criteriaMapData) {
		StringBuilder sb = new StringBuilder();
		for (String path : pathList) {
			String itemValue = restCommonUtil.getItemValueUsingJsonPath(path, inputDesignJson, String.class);
			if (StringUtils.isNotEmpty(itemValue)) {
				if (criteriaMapData.containsKey("dataSetName")) {
					itemValue = restCommonUtil.processDataSetName(itemValue, criteriaMapData.get("dataSetName"),
							String.class);
				}
				if (StringUtils.isNotEmpty(itemValue)) {
					sb.append(itemValue);
				}
			}
		}
		return sb.toString();
	}
	
	protected String processOrCondition(Object inputDesignJson, List<String> pathList,
			Map<String, String> criteriaMapData) {
		for(String path:pathList) {
			String itemValue=restCommonUtil.getItemValueUsingJsonPath(path, inputDesignJson, String.class);
			if(null != itemValue) {
				if (criteriaMapData.containsKey("dataSetName")) {
					itemValue = restCommonUtil.processDataSetName(itemValue, criteriaMapData.get("dataSetName"),
							String.class);
				}
				if(null != itemValue) {
					return itemValue;
				}
			}
		}
		return null;
	}
	
	protected String appendDesignAndConsolidationData(String designData,String consolidationCriteriaData ) {
		StringBuilder mergedJson= new StringBuilder();
		mergedJson.append(designData.substring(0, designData.length() - 1));
		mergedJson.append(",");
		mergedJson.append(consolidationCriteriaData.substring(1));
		if(mergedJson!=null) {
			return mergedJson.toString();
		}
		return null;
	}
	protected String translateUdfAttributeId(long udfId, long udfAttributeId,long offerId,long componentCodeId) {
		SalesMsProdcompUdfAttrVal salesMsProdcompUdfAttrVal = salesMsProdcompUdfAttrValRepository
				.findTopByOfferIdAndComponentIdAndUdfIdAndUdfAttributeIdAndActive(offerId, componentCodeId, udfId,
						udfAttributeId, "Active");
		if (salesMsProdcompUdfAttrVal != null) {
			return salesMsProdcompUdfAttrVal.getUdfAttributeValue();
		}

		return null;
	}
	protected String getRestVersion(String offerName) {
		
			if(processPDtoMPRestUtil.isRESTVersionEnabled(offerName,MyPriceConstants.REST_PRODUCTS_V2, MyPriceConstants.SOURCE_PD)) {
				return StringConstants.VERSION_2;
			}else if(processPDtoMPRestUtil.isRESTVersionEnabled(offerName,MyPriceConstants.REST_PRODUCTS, MyPriceConstants.SOURCE_PD)) {
				return null;
			}
		return null;
	}
	public String getDataInString(Object request,String asrId) {
		if(request != null) { 
			String Path=MyPriceConstants.POPCOLLECTOR_ASR_PATH;
			Path= String.format(Path,asrId );
			TypeRef<List<String>> ref = new TypeRef<List<String>>() {};
			List<String>  results = jsonPathUtil.search(request, Path, ref);
			if(CollectionUtils.isNotEmpty(results)) {
				return results.get(0);
			}
			return null;
		}
		return null;
	}
	
	 public boolean removeCancelledLineItemsForIpne( RetreiveICBPSPRequest retreiveICBPSPRequest, long nxSolutionId, 
	 		CreateTransactionResponse createTransactionResponse, Map<String, Object> paramMap){
		 
	 	TypeRef<List<Long>> arrayType1 = new TypeRef<List<Long>>() {};		
	 	List<Long> nxSiteId = jsonPathUtil.search(retreiveICBPSPRequest, MyPriceConstants.EPLSWAN_NX_SITE_ID_PATH, arrayType1);
	 	

	 	TypeRef<List<Object>> arrayType2 = new TypeRef<List<Object>>() {};
	 	List<Object> asrIds = jsonPathUtil.search(retreiveICBPSPRequest, MyPriceConstants.ADE_ASR_ITEM_ID_PATH, arrayType2);
	 	
	 	TypeRef<List<String>> arrayType3 = new TypeRef<List<String>>() {};		
	 	List<String> circuitId = jsonPathUtil.search(retreiveICBPSPRequest, MyPriceConstants.EPLSWAN_CIRCUIT_ID_PATH, arrayType3);
	 	
	 	List<Long> nxSiteIdList = nxSiteId.stream().filter(Objects::nonNull).collect(Collectors.toList());
	 	List<Object[]> mpSolutionIdAndDesignIds= new ArrayList<Object[]>();
	 	List<String> productList= Arrays.asList(MyPriceConstants.EPLSWAN_OFFER_NAME,MyPriceConstants.ETHERNET_OFFER_NAME);
	 	
 		if(CollectionUtils.isNotEmpty(nxSiteIdList)) {
 		 	List<Long> newDesignIds= nxDesignRepository.fetchDesignIdBySiteIdAndProductAndCircuitId(nxSolutionId, nxSiteIdList,productList,circuitId);
 			if(CollectionUtils.isNotEmpty(newDesignIds))
 				mpSolutionIdAndDesignIds.addAll(nxDesignRepository.fetchMpsolutionIdByDesignId(nxSolutionId, newDesignIds, createTransactionResponse.getNxTransacId(),productList));
 			else
 				mpSolutionIdAndDesignIds.addAll(nxDesignRepository.fetchMpsolutionIdBySolutionId(nxSolutionId, createTransactionResponse.getNxTransacId(),productList));
 		}else
 			mpSolutionIdAndDesignIds.addAll(nxDesignRepository.fetchMpsolutionIdBySolutionId(nxSolutionId, createTransactionResponse.getNxTransacId(),productList));
 		
 		List<String> DesignIdsList = mpSolutionIdAndDesignIds.stream()
	             .map(str -> String.valueOf(str[2]))
	             .collect(Collectors.toList());
 		if(CollectionUtils.isNotEmpty(DesignIdsList))
 			nxDesignRepository.updateDesignBySolIdAndDesignId(MyPriceConstants.DESIGN_STATUS_CANCEL,new Date(),nxSolutionId,DesignIdsList);
 	
 		if(CollectionUtils.isNotEmpty(asrIds))
 			mpSolutionIdAndDesignIds.addAll(nxDesignRepository.fetchMpsolutionIdByAsrAndProduct(nxSolutionId, asrIds, createTransactionResponse.getNxTransacId(),MyPriceConstants.ADE_OFFER_NAME,StringConstants.CONSTANT_N));
 		else
 			mpSolutionIdAndDesignIds.addAll(nxDesignRepository.fetchMpsolutionIdBySolutionIdAndSubmitToMp(nxSolutionId, createTransactionResponse.getNxTransacId(),MyPriceConstants.ADE_OFFER_NAME,StringConstants.CONSTANT_N));

	 	boolean status = false;
	 	List<String> mpSolutionIdAndDesignIdsList = mpSolutionIdAndDesignIds.stream()
	             .map(str -> String.valueOf(str[0]))
	             .collect(Collectors.toList());
	 	Map<String, Object> removeInputMap = new HashMap<String, Object>();
	 	removeInputMap.put(MyPriceConstants.NX_TRANSACTION_ID, createTransactionResponse.getNxTransacId());
	 	removeInputMap.put(MyPriceConstants.MP_TRANSACTION_ID, createTransactionResponse.getMyPriceTransacId());
	 	removeInputMap.put(MyPriceConstants.MP_SOLUTION_ID_LIST,  mpSolutionIdAndDesignIdsList.stream().map(n -> Long.parseLong(n.toString())).collect(Collectors.toSet()));
	 	if(CollectionUtils.isNotEmpty(mpSolutionIdAndDesignIdsList)) {
			
	 		List<Long> nxDesignIds = nxMpDesignDocumentRepository.findNxDesignIdByNxTxnIdAndMpSolutionId(createTransactionResponse.getNxTransacId(), mpSolutionIdAndDesignIdsList);
	 		removeInputMap.put(MyPriceConstants.NX_DESIGN_ID_LIST, nxDesignIds);
	 	}
	 	if(paramMap.containsKey(InrConstants.REQUEST_META_DATA_KEY)) {
	 		Map<String, Object> requestMetaDataMap = (Map<String, Object>) paramMap.get(InrConstants.REQUEST_META_DATA_KEY);
	 		removeInputMap.put(InrConstants.REQUEST_META_DATA_KEY, requestMetaDataMap);
	 	}
	 	status = processPDtoMPRestUtil.removeTransactionByMpsolutionid(removeInputMap);
	 	return status;
	 }
}
