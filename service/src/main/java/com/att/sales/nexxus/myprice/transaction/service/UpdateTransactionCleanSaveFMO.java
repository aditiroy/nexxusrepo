package com.att.sales.nexxus.myprice.transaction.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxMpSiteDictionary;
import com.att.sales.nexxus.dao.model.NxMpSolutionDetails;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.model.solution.NxTeam;
import com.att.sales.nexxus.dao.repository.NxMpSiteDictionaryRepository;
import com.att.sales.nexxus.dao.repository.NxMpSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxTeamRepository;
import com.att.sales.nexxus.handlers.GetOptyInfoWSHandler;
import com.att.sales.nexxus.myprice.transaction.model.CreateTransactionResponse;
import com.att.sales.nexxus.myprice.transaction.model.Document;
import com.att.sales.nexxus.reteriveicb.model.RetreiveICBPSPRequest;
import com.att.sales.nexxus.reteriveicb.model.Site;
import com.att.sales.nexxus.rome.model.GetOptyResponse;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;

@org.springframework.stereotype.Component
@Slf4j
public class UpdateTransactionCleanSaveFMO {

	@Autowired
	private Environment env;
	
	@Autowired
	private EntityManager em;

	@Autowired
	private NxMpSolutionDetailsRepository nxMpSolutionDetailRepository;

	@Autowired
	private NxMpSiteDictionaryRepository nxMpSiteDictionaryRepository;

	@Autowired
	private MyPriceTransactionUtil myPriceTransactionUtil;

	@Autowired
	private GetOptyInfoWSHandler getOptyInfoWSHandler;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private NexxusJsonUtility nexxusJsonUtility;
	
	@Autowired
	private UpdateTxnSiteUploadServiceImpl updateTxnSiteUploadServiceImpl;
	
	@Autowired
	private NxTeamRepository nxTeamRepository;
	
	@Autowired
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;
	
	@Autowired
	private HttpRestClient httpRestClient;
	
	@Value("${myprice.proxy.enabled}")
	private String isProxyEnabled;
	
	public Map<String, Object> updateTransactionCleanSave(RetreiveICBPSPRequest retreiveICBPSPRequest,
			CreateTransactionResponse createTransactionResponse,Map<String,Object> parammap) throws SalesBusinessException {

		
		log.info("Service Log :: [Nexxus [MyPrice - Info] :: Inside updateTransactionCleanSaveFMO");
		Map<String, Object> updateResult = new HashMap<String, Object>();

		String myPricetransactionId = createTransactionResponse.getMyPriceTransacId();

		if (null != myPricetransactionId) {
			try {
				updateResult = updateCleanSaveTProcessor(retreiveICBPSPRequest, createTransactionResponse, myPricetransactionId,parammap);
			} catch (IOException | RestClientException | URISyntaxException |SalesBusinessException e) {
				updateResult.put("status", false);
				log.error(e.toString());
				throw new SalesBusinessException(e.getMessage());
			}
		}else {
			log.info("Service Log :: [Nexxus [MyPrice - Info] :: Exited updateTransactionCleanSaveFMO ");
		}
	
		return updateResult;
	}

	private Map<String, Object> updateCleanSaveTProcessor(RetreiveICBPSPRequest retreiveICBPSPRequest,
			CreateTransactionResponse createTransactionResponse, String myPricetransactionId,Map<String,Object> parammap)
			throws IOException, URISyntaxException, SalesBusinessException {

		log.info("Service Log :: [Nexxus [MyPrice - Info] ::  updateCleanSaveTProcessor for FMO Invoked");

		Map<Object, Object> updateCleanTransactionCleanSaveRequestMap = new HashMap<>();
		Document document = new Document();
		String form470 = null;
		GetOptyResponse optyResp = null;
		if(retreiveICBPSPRequest.getSolution().getOptyId() != null && !retreiveICBPSPRequest.getSolution().getOptyId().isEmpty()) {
			try {
				Map<String, Object> requestMap = new HashMap<>();				
				requestMap.put("attuid", retreiveICBPSPRequest.getSolution().getUserId());	
				requestMap.put("optyId", retreiveICBPSPRequest.getSolution().getOptyId());
				requestMap.put("nxSolutionId", parammap.get("currentNxSolutionId"));
				requestMap.put("action", "myPriceFlow");
				requestMap.put("subAction", "updateSolutionMyPriceFlow");
				requestMap.put("flowType", "FMO");
				optyResp = 	(GetOptyResponse) getOptyInfoWSHandler.initiateGetOptyInfoWebService(requestMap);		
				log.info("Service Log :: [Nexxus [MyPrice - Info] ::  GetOptyInfo Webservice Invoked");
				document.setCustomerTFirstName(optyResp.getAbsCreatedByName());
				document.setCustomerTLastName(optyResp.getAbsCreatedByName());
				document.setCustomerTAddress(optyResp.getAddress1());
				document.setCustomerTAddress2(optyResp.getAddress2());
				document.setCustomerTCity(optyResp.getCity());
				document.setCustomerTState(optyResp.getState());
				document.setCustomerTCountry(optyResp.getCountry());
				document.setCustomerTZip(optyResp.getPostalCode());
				form470 = optyResp.getForm470();
			} catch (Exception e) {
				log.error("Error : While getting opty info");
				throw new SalesBusinessException(e.getMessage());
			}
			
		/**	document.setRomeMarketSegmentQ(optyResp.getMarketStrataValue());
			document.setWiOpportunityTypeQ(optyResp.getType());
			document.setRdAttuidQ(optyResp.getPrimaryATTUID());
			document.setRdOpportunitySalesTeamQ(String.join(",", optyResp.getPrimaryATTUID(), optyResp.getPrimaryManager(), optyResp.getPrimaryManagersManager()));**/
		}
		
		document.setCustomerTCompanyName(retreiveICBPSPRequest.getSolution().getCustomerName());
		document.setOpportunityIDT(retreiveICBPSPRequest.getSolution().getOptyId());
		document.setOpportunityNameT(retreiveICBPSPRequest.getSolution().getOpportunityName());
		document.setSaartAccountNumber(retreiveICBPSPRequest.getSolution().getsAARTAccountNumber());
		document.setRdDescriptionQ(retreiveICBPSPRequest.getSolution().getSolutionName());		
		document.setRomeMarketSegmentQ(retreiveICBPSPRequest.getSolution().getMarketSegment());
		document.setSourceFromNexxusQ("adopt");
		document.setContractTerm(retreiveICBPSPRequest.getSolution().getContractTerm());
		
		document.setWiLayerQ(retreiveICBPSPRequest.getSolution().getLayer());
		
		document.setWiWireline470Q(retreiveICBPSPRequest.getSolution().getErateFormNumber());
		document.setWiOpportunityTypeQ(getLowerCaseString(retreiveICBPSPRequest.getSolution().getOpportunityType()));
		document.setWiBudgetaryFirmQ("firm");
		if(retreiveICBPSPRequest.getSolution().getOpportunitySalesTeam() != null && !retreiveICBPSPRequest.getSolution().getOpportunitySalesTeam().isEmpty()) {
			String opportunitySalesTeam="";
			int i = 1;
			for(String uid:retreiveICBPSPRequest.getSolution().getOpportunitySalesTeam()) {
				if(i > 35) {
					break;
				}
				opportunitySalesTeam=opportunitySalesTeam.concat(uid+",");
				i++;
			} 
			opportunitySalesTeam= opportunitySalesTeam.substring(0, opportunitySalesTeam.length() - 1);
			document.setRdOpportunitySalesTeamQ(opportunitySalesTeam);
		}
			
		document.setDealType("wireline");
		document.setPriceScenarioId(retreiveICBPSPRequest.getSolution().getPriceScenarioId());
		String flowType = (String) parammap.get(MyPriceConstants.FLOW_TYPE);
		if(Optional.ofNullable(flowType).isPresent() && MyPriceConstants.SOURCE_FMO.equalsIgnoreCase(flowType)) {
			if(Optional.ofNullable(optyResp).isPresent()) {
				document.setWiOpportunityTypeQ(getLowerCaseString(optyResp.getType() != null ? myPriceTransactionUtil.getDataFromNxLookUp(optyResp.getType(), "MP_OPTY_TYPE") : ""));
			}
			String authVal1 = null;
			if(CollectionUtils.isNotEmpty(retreiveICBPSPRequest.getSolution().getPricePlanDetails())) {
			 authVal1 = retreiveICBPSPRequest.getSolution().getPricePlanDetails().get(0).getAuthVal();
			}
			log.info("authVal value in cleanSave : "+authVal1);
			BiPredicate<String,String> erateIndPredicate = (erateInd,authVal) ->  null != erateInd && null != authVal && erateInd.trim().equalsIgnoreCase("Y") && authVal.trim().equalsIgnoreCase("FMR");
			if(erateIndPredicate.test(retreiveICBPSPRequest.getSolution().getErateInd(), authVal1)) {
				document.setWiCustomPriceListQ("eRateFMR");
			}
			if("Y".equalsIgnoreCase(retreiveICBPSPRequest.getSolution().getErateInd())) {
				log.info("erateQ is true in cleanSave");
				document.setWiIsErateQ(true);
				document.setWiLayerQ("SLED");
				document.setWiWireline470Q(form470);	
			} else {
				document.setWiIsErateQ(false);
			}
		document.setExternalSolutionId(retreiveICBPSPRequest.getSolution().getExternalKey());//ambika	
		}
		Map<Long, Long> nxSiteIdMap = new HashMap<Long, Long>();
		List<Object> siteAddress = new ArrayList<>();
		for (int i = 0; i < retreiveICBPSPRequest.getSolution().getOffers().size(); i++) {
			for (int j = 0; j < retreiveICBPSPRequest.getSolution().getOffers().get(i).getSite().size(); j++) {
				Site currentSite = retreiveICBPSPRequest.getSolution().getOffers().get(i).getSite().get(j);
				Long nxSiteId = nxSiteIdMap.get(currentSite.getSiteId());
				if(nxSiteId == null) {
					nxSiteId = getNxSiteId();
					nxSiteIdMap.put(currentSite.getSiteId(), nxSiteId);
					Map<Object, Object> map = new HashMap<>();
					map.put("nxSiteId", nxSiteId);
					map.put("name", currentSite.getSiteName());
					map.put("addressLine", String.join(" ",currentSite.getAddress1(), currentSite.getAddress2()));
					map.put("city", currentSite.getCity());
					map.put("state", currentSite.getState());
					map.put("postalCode", currentSite.getZipCode());
					map.put("country", currentSite.getCountry());
					map.put("address", String.join(" ", currentSite.getAddress1(), currentSite.getAddress2(), currentSite.getCity(), currentSite.getState(),
							currentSite.getZipCode(), currentSite.getCountry()));
					map.put("validationStatus", CommonConstants.VALID);
					map.put("swcCLLI", currentSite.getSwcClli());
					map.put("buildingClli", currentSite.getBuildingClli());
					if(CollectionUtils.isNotEmpty(currentSite.getDesignSiteOfferPort())) {
						JsonNode portData = mapper.valueToTree(currentSite);
						ObjectNode portObj = (ObjectNode) portData;
						Object obj = nexxusJsonUtility.getValue(portObj, "$.designSiteOfferPort..accessPricingAQ.accessPriceUIDetails..respPopClli");
						if(obj != null)
							map.put("popCLLI", String.valueOf(obj));
					}
					
					map.put("siteInfoSource", "adopt");
					siteAddress.add(map);
				}
				retreiveICBPSPRequest.getSolution().getOffers().get(i).getSite().get(j).setNxSiteId(nxSiteId);
			}
		}

		String siteAddressJson = getSiteAddressJson(siteAddress);
	//	document.setSiteAddress(siteAddressJson);
		document.setRdAttuidQ(retreiveICBPSPRequest.getSolution().getUserId());
		//added rd_opportunityTeam_q tag
		List<NxSolutionDetail> nxSolutionDetails=nxSolutionDetailsRepository.findByExternalKey(retreiveICBPSPRequest.getSolution().getExternalKey());
		if (!CollectionUtils.isEmpty(nxSolutionDetails)) {
			List<NxTeam> nxTeamList=nxTeamRepository.findByNxSolutionId(nxSolutionDetails.get(0).getNxSolutionId());
			log.info("rd_opportunityTeam_q solution id is"+nxSolutionDetails.get(0).getNxSolutionId());
			if(null!=nxTeamList) {
				int i = 1;
				String rdOpportunityTeamQ="";
				for(NxTeam nxTeam:nxTeamList) {
					if(i > 35) {
						break;
					}
					rdOpportunityTeamQ=rdOpportunityTeamQ.concat(nxTeam.getAttuid()+",");
					i++;
				} 
				rdOpportunityTeamQ= rdOpportunityTeamQ.substring(0, rdOpportunityTeamQ.length() - 1);
				document.setRdOpportunityTeamQ(rdOpportunityTeamQ);
			}
		}
		document.setRdNameQ(String.join(" ", retreiveICBPSPRequest.getSolution().getUserFirstName(),
				 retreiveICBPSPRequest.getSolution().getUserLastName()));
		document.setRdTitleQ(retreiveICBPSPRequest.getSolution().getUserTitle());
		document.setRdOfficeQ(retreiveICBPSPRequest.getSolution().getUserWorkPhone());
		document.setRdMobileQ(retreiveICBPSPRequest.getSolution().getUserMobile());
		document.setRdEmailQ(retreiveICBPSPRequest.getSolution().getUserEmail());
	

		// Persist to NX_MP_SITE_DICTIONARY
		NxMpSiteDictionary nxMpTRow = persistToNXMPSITEDICTIONARYTABLE(retreiveICBPSPRequest, createTransactionResponse,
				siteAddressJson);

		if(null != retreiveICBPSPRequest.getSolution().getOpportunitySalesTeam() && !retreiveICBPSPRequest.getSolution().getOpportunitySalesTeam().isEmpty()) {
			document.setRdOpportunitySalesTeamQ(String.join(",", retreiveICBPSPRequest.getSolution().getOpportunitySalesTeam()));
		}
		updateCleanTransactionCleanSaveRequestMap.put("documents", document);

		// Mapping site block as json String
		String solutionDataJsonBlock = getupdateTransactionCSRTJSON(updateCleanTransactionCleanSaveRequestMap);
		String replaceStringsolutionDataJsonBlock = "\\r\\n";
		solutionDataJsonBlock = solutionDataJsonBlock.replace(replaceStringsolutionDataJsonBlock, "");
		log.info(solutionDataJsonBlock);

		// Persisting solutionDataJsonBlock To NX_MP_SOLUTION_DETAILS
		persistTONXMPSOLUTIONDETAILSTABLE(retreiveICBPSPRequest, createTransactionResponse, nxMpTRow,
				solutionDataJsonBlock);
		
		// read integrated site details
		document.setSiteAddressJson(siteAddressJson); // This holds site which are not from ip&e
		document.setSiteAddress(updateTxnSiteUploadServiceImpl.translateSiteJsonRemoveDuplicatedNxSiteId(nxMpTRow.getSiteJson())); // This holds ip&e site
		if(nxMpTRow.getSiteJson() != null) {
			document.setWiUpdateOverrideQ(true);
		}
		updateCleanTransactionCleanSaveRequestMap.put("documents", document);
		solutionDataJsonBlock = getupdateTransactionCSRTJSON(updateCleanTransactionCleanSaveRequestMap);
		solutionDataJsonBlock = solutionDataJsonBlock.replace(replaceStringsolutionDataJsonBlock, "");

		Map<String, Object> msResponse = callMsForCleanSaveTransaction(myPricetransactionId, solutionDataJsonBlock);
		msResponse.put("retreiveICBPSPRequest", retreiveICBPSPRequest);
		int code =  (int) msResponse.get("code");
		if(code == CommonConstants.SUCCESS_CODE) {
			log.info("call for updateTransactionCleanSave_t FMO success!");
			msResponse.put("status", true);
		}else {
			log.info("Call for updateTransactionCleanSave_t FMO failed");
			msResponse.put("status", false);
		}

		log.info("Service Log :: [Nexxus [MyPrice - Info] ::  updateCleanSaveTProcessor for FMO Destroyed");

		return msResponse;

	}

	public Map<String, Object> callMsForCleanSaveTransaction(String myPricetransactionId, String solutionDataJsonBlock)
			throws SalesBusinessException {
			
		//myPriceTransactionUtil.setSystemProperties();
		
		log.info("Service Log :: [Nexxus [MyPrice - Info]] :: Invoked callMsForCleanSaveTransaction() FMO method");
	
		String updateTransactionCleanSaveRequestURL = env.getProperty("myPrice.updateTransactionCleanSaveRequest");
		String transactionIdString = String.valueOf(myPricetransactionId);
		String urlWithTransactionId = updateTransactionCleanSaveRequestURL.replace("{TransactionId}",
				transactionIdString);
		
		String updateRequest = solutionDataJsonBlock;
		
		Map<String, String> headers  = new HashMap<>();
		headers.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.MYPRICE_AUTHORIZATION));
		headers.put(StringConstants.REQUEST_CONTENT_TYPE, "application/json");
		log.info("Created Request For Update: " + "\n" + updateRequest);
//		Map<String, Object> updateResponse = restClient.initiateWebService(updateRequest, urlWithTransactionId , "POST", headers, queryParameters);
		Map<String, Object> updateResponse = new HashMap<String, Object>();
		String proxy = null;
		if(StringConstants.CONSTANT_Y.equalsIgnoreCase(isProxyEnabled)) {
			proxy = env.getProperty(CommonConstants.CDP_HTTP_PROXY);
		}
		String response = httpRestClient.callHttpRestClient(urlWithTransactionId, HttpMethod.POST, null,
				updateRequest,headers, proxy);
		updateResponse.put(MyPriceConstants.RESPONSE_DATA, response);
		updateResponse.put(MyPriceConstants.RESPONSE_CODE, 200);
		updateResponse.put(MyPriceConstants.RESPONSE_MSG,"OK");

		log.info("Service Log :: [Nexxus [MyPrice - Info]] :: Destroyed callMsForCleanSaveTransaction() FMO method");	
		return updateResponse;
	}

	private void persistTONXMPSOLUTIONDETAILSTABLE(RetreiveICBPSPRequest retreiveICBPSPRequest,
			CreateTransactionResponse createTransactionResponse, Object nxMpTRow, String solutionDataJsonBlock) {

		log.info("Service Log :: [Nexxus [MyPrice - Info] ::  persistTONXMPSOLUTIONDETAILSTABLE Invoked");
		Long nxTxnId = createTransactionResponse.getNxTransacId();
		NxMpSolutionDetails nxMpSolutionDetails = nxMpSolutionDetailRepository.findByNxTxnId(nxTxnId);
		if(null == nxMpSolutionDetails)
			nxMpSolutionDetails = new NxMpSolutionDetails();
		nxMpSolutionDetails.setNxTxnId(nxTxnId);
		nxMpSolutionDetails.setSourceSystem(retreiveICBPSPRequest.getSolution().getSourceName());
		nxMpSolutionDetails.setSolutionData(solutionDataJsonBlock);
		nxMpSolutionDetails.setActiveYN(CommonConstants.ACTIVE_Y);
		nxMpSolutionDetails.setSiteRefId(((NxMpSiteDictionary) nxMpTRow).getSiteRefId());
		nxMpSolutionDetailRepository.save(nxMpSolutionDetails);

		log.info("Service Log :: [Nexxus [MyPrice - Info] ::  persistTONXMPSOLUTIONDETAILSTABLE Destroyed");
	}

	private String getupdateTransactionCSRTJSON(Map<Object, Object> updateCleanTransactionCleanSaveRequestMap)
			throws JsonProcessingException {

		ObjectMapper obj = new ObjectMapper();
		obj.enable(SerializationFeature.INDENT_OUTPUT);
		String solutionDataJsonBlock;
		solutionDataJsonBlock = obj.writeValueAsString(updateCleanTransactionCleanSaveRequestMap);
		log.info("UT-CleanSave : getupdateTransactionCSRTJSON() : documents request :==>> {} "+solutionDataJsonBlock);
		return solutionDataJsonBlock;
	}

	private String getSiteAddressJson(List<Object> siteAddress) throws JsonProcessingException {
		ObjectMapper obj = new ObjectMapper();
		String siteAddressBlock;
		siteAddressBlock = obj.writerWithDefaultPrettyPrinter().writeValueAsString(siteAddress);
		return siteAddressBlock;
	}

	private NxMpSiteDictionary persistToNXMPSITEDICTIONARYTABLE(RetreiveICBPSPRequest retreiveICBPSPRequest,
			CreateTransactionResponse createTransactionResponse, String siteBlockJson) {

		log.info("Service Log :: [Nexxus [MyPrice - Info] ::  persistToNXMPSITEDICTIONARYTABLE Invoked");

		Long nxTxnId = createTransactionResponse.getNxTransacId();
		NxMpSiteDictionary nxMpSiteDictionary = nxMpSiteDictionaryRepository.findByNxTxnId(nxTxnId);
		if(null == nxMpSiteDictionary)
			nxMpSiteDictionary = new NxMpSiteDictionary();
		nxMpSiteDictionary.setNxTxnId(nxTxnId);
		nxMpSiteDictionary.setSourceSystem(retreiveICBPSPRequest.getSolution().getSourceName());
		nxMpSiteDictionary.setSiteAddress("{ \"siteAddress\" :  " + siteBlockJson + "}");
		nxMpSiteDictionary.setActiveYN(CommonConstants.ACTIVE_Y);
		nxMpSiteDictionaryRepository.save(nxMpSiteDictionary);

		log.info("Service Log :: [Nexxus [MyPrice - Info] ::  persistToNXMPSITEDICTIONARYTABLE Destroyed");

		return nxMpSiteDictionary;
	}
	
	private long getNxSiteId() {		 
 		Query q = em.createNativeQuery("SELECT SEQ_NX_SITE_ID.NEXTVAL FROM DUAL");
 		BigDecimal result = (BigDecimal) q.getSingleResult();
 		return result.longValue();
 	}
	
	private String getLowerCaseString(String data) {
		if(null != data && data.length() != 0) {
			return data.toLowerCase();
		}
		return null;
	}

}

