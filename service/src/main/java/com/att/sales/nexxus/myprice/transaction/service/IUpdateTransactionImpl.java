package com.att.sales.nexxus.myprice.transaction.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.att.aft.dme2.internal.google.common.base.Strings;
import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxMpSiteDictionary;
import com.att.sales.nexxus.dao.model.NxMpSolutionDetails;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.model.solution.NxTeam;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxMpSiteDictionaryRepository;
import com.att.sales.nexxus.dao.repository.NxMpSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxTeamRepository;
import com.att.sales.nexxus.handlers.GetOptyInfoWSHandler;
import com.att.sales.nexxus.myprice.transaction.model.CreateTransactionResponse;
import com.att.sales.nexxus.myprice.transaction.model.Document;
import com.att.sales.nexxus.reteriveicb.model.Offer;
import com.att.sales.nexxus.reteriveicb.model.RetreiveICBPSPRequest;
import com.att.sales.nexxus.reteriveicb.model.Site;
import com.att.sales.nexxus.reteriveicb.model.UDFBaseData;
import com.att.sales.nexxus.rome.model.GetOptyResponse;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.HttpRestClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jayway.jsonpath.TypeRef;

import lombok.extern.slf4j.Slf4j;

/**
 * @author IndraSingh
 */

@Service("IUpdateTransactionImpl")
@Slf4j
public class IUpdateTransactionImpl extends BaseServiceImpl implements IUpdateTransaction {

	@Autowired
	private Environment env;
	
	@PersistenceContext
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
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Autowired
	private UpdateTxnSiteUploadServiceImpl updateTxnSiteUploadServiceImpl;
	
	@Autowired
	private NxTeamRepository nxTeamRepository;
	
	@Autowired
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;
	
	
	@Autowired
	private JsonPathUtil jsonPathUtil;
	
	@Value("${eplswan.cleansave.attibutes.path}")
	private String eplswanCleansaveAttibutesPath;
	
	@Autowired
	private HttpRestClient httpRestClient;
	
	@Value("${myprice.proxy.enabled}")
	private String isProxyEnabled;
	
	@Override
	public Map<String, Object> updateTransactionCleanSave(RetreiveICBPSPRequest retreiveICBPSPRequest,
			CreateTransactionResponse createTransactionResponse,Map<String,Object> parammap) throws SalesBusinessException {

		
		log.info("Service Log :: [Nexxus [MyPrice - Info] :: Inside updateTransactionCleanSave");
		Map<String, Object> updateResult = new HashMap<String, Object>();

		String myPricetransactionId = createTransactionResponse.getMyPriceTransacId();

		if (null != myPricetransactionId) {
			try {
				updateResult = updateCleanSaveTProcessor(retreiveICBPSPRequest, createTransactionResponse, myPricetransactionId,parammap);
			} catch (IOException | RestClientException | URISyntaxException e) {
				updateResult.put("status", false);
				log.error(e.toString());
			}
		}else {
			log.info("Service Log :: [Nexxus [MyPrice - Info] :: Exited updateTransactionCleanSave ");
		}
	
		//setSuccessResponse(response);
		return updateResult;
	}

	public Map<String, Object> updateCleanSaveTProcessor(RetreiveICBPSPRequest retreiveICBPSPRequest,
			CreateTransactionResponse createTransactionResponse, String myPricetransactionId,Map<String,Object> parammap)
			throws IOException, URISyntaxException, SalesBusinessException {

		log.info("Service Log :: [Nexxus [MyPrice - Info] ::  updateCleanSaveTProcessor Invoked");

		Map<Object, Object> updateCleanTransactionCleanSaveRequestMap = new HashMap<>();
		Document document = new Document();
	//	boolean automatedFlowInd=parammap.get(MyPriceConstants.AUTOMATION_IND)!=null?
			//	(boolean)parammap.get(MyPriceConstants.AUTOMATION_IND):false;
		/** Added the below code by M V C VAMSI KRISHNA (vk553x) in develop/1906 branch.
		 * If marketStrata missing in ICBPricingRequest, call getOpty and set the fields necessary (missing ones)
		   for UT_cleanSave back to the same ICBPricingRequest using response of getOpty.
		   Nexxus shall call getOpty for the following MyPrice fields, if the field values don't come in the NexxusPricing API:
		   Starts*/
		if(null == retreiveICBPSPRequest.getSolution().getMarketStrata()){
			GetOptyResponse optyResp = null; 
			String attUid = retreiveICBPSPRequest.getSolution().getUserId(); 
			String optyId = retreiveICBPSPRequest.getSolution().getOptyId(); 	 
			if(!attUid.isEmpty() && !optyId.isEmpty()) { 
				Map<String, Object> requestMap = new HashMap<>();				
				requestMap.put("attuid", attUid);	
				requestMap.put("optyId", optyId);
				requestMap.put("nxSolutionId", retreiveICBPSPRequest.getSolution().getSolutionId());
				requestMap.put("action", "myPriceFlow");
				optyResp = 	(GetOptyResponse) getOptyInfoWSHandler.initiateGetOptyInfoWebService(requestMap);		
				log.info("Service Log :: [Nexxus [MyPrice - Info] ::  GetOptyInfo Webservice Invoked");
				retreiveICBPSPRequest.getSolution().setOpportunitySalesTeam(optyResp.getSalesRep());
				retreiveICBPSPRequest.getSolution().setOpportunityName(optyResp.getName());	
				retreiveICBPSPRequest.getSolution().setAccountId(optyResp.getAccountId());
				retreiveICBPSPRequest.getSolution().setsAARTAccountNumber(optyResp.getSubAccountID());
				retreiveICBPSPRequest.getSolution().setOptyCurrency(optyResp.getCurrencyCode());
				retreiveICBPSPRequest.getSolution().setOfferType(optyResp.getOffer());
				retreiveICBPSPRequest.getSolution().setOpportunityType(optyResp.getType() != null ? myPriceTransactionUtil.getDataFromNxLookUp(optyResp.getType(), "MP_OPTY_TYPE") : "");
				retreiveICBPSPRequest.getSolution().setMarketSegment(optyResp.getMarketStrataValue());
			}
		}//Ends
		if( StringConstants.IPNE.equalsIgnoreCase( retreiveICBPSPRequest.getSolution().getSourceName())) {
			String optyType = retreiveICBPSPRequest.getSolution().getOpportunityType();
			retreiveICBPSPRequest.getSolution().setOpportunityType(optyType != null ? myPriceTransactionUtil.getDataFromNxLookUp(optyType, "MP_OPTY_TYPE") : "");

		}
		if (!retreiveICBPSPRequest.getSolution().getContact().isEmpty()) {
			for (int i = 0; i < retreiveICBPSPRequest.getSolution().getContact().size(); i++) {
				String contactType = retreiveICBPSPRequest.getSolution().getContact().get(i).getContactType();
				if ("Customer".equalsIgnoreCase(contactType)) {
					document.setCustomerTFirstName(
							retreiveICBPSPRequest.getSolution().getContact().get(i).getFirstName());
					document.setCustomerTLastName(
							retreiveICBPSPRequest.getSolution().getContact().get(i).getLastName());
					document.setCustomerTAddress(
							retreiveICBPSPRequest.getSolution().getContact().get(i).getAddress1());
					document.setCustomerTAddress2(
							retreiveICBPSPRequest.getSolution().getContact().get(i).getAddress2());
					document.setCustomerTCity(retreiveICBPSPRequest.getSolution().getContact().get(i).getCity());
					document.setCustomerTState(retreiveICBPSPRequest.getSolution().getContact().get(i).getState());
					document.setCustomerTZip(retreiveICBPSPRequest.getSolution().getContact().get(i).getZipCode());
					document.setCustomerTCountry(
							retreiveICBPSPRequest.getSolution().getContact().get(i).getCountry());
					document.setCustomerTFax(retreiveICBPSPRequest.getSolution().getContact().get(i).getFaxNumber());
					document.setCustomerTEmail(retreiveICBPSPRequest.getSolution().getContact().get(i).getEmail());
				}
			}
		}
		document.setCustomerTCompanyName(retreiveICBPSPRequest.getSolution().getCustomerName());
		document.setOpportunityIDT(retreiveICBPSPRequest.getSolution().getOptyId());
		document.setOpportunityNameT(retreiveICBPSPRequest.getSolution().getOpportunityName());
		document.setSaartAccountNumber(retreiveICBPSPRequest.getSolution().getsAARTAccountNumber());
		document.setRdDescriptionQ(retreiveICBPSPRequest.getSolution().getSolutionName());		
		document.setRomeMarketSegmentQ(retreiveICBPSPRequest.getSolution().getMarketSegment());
        document.setSourceFromNexxusQ(retreiveICBPSPRequest.getSolution().getSourceName());		
		document.setContractTerm(retreiveICBPSPRequest.getSolution().getContractTerm());
		document.setExternalSolutionId(retreiveICBPSPRequest.getSolution().getExternalKey());
		document.setWiLayerQ(retreiveICBPSPRequest.getSolution().getLayer());	
		document.setWiWireline470Q(retreiveICBPSPRequest.getSolution().getErateFormNumber());
		document.setWiOpportunityTypeQ(retreiveICBPSPRequest.getSolution().getOpportunityType());
		document.setWiBudgetaryFirmQ("firm");
		
		List<Object> siteAddress = new ArrayList<>();
		Map<Object, Object> siteIdmap =new HashMap<>();
        String sourceName = null != retreiveICBPSPRequest.getSolution() ? retreiveICBPSPRequest.getSolution().getSourceName() : null;
		for (int i = 0; i < retreiveICBPSPRequest.getSolution().getOffers().size(); i++) {
			for (int j = 0; j < retreiveICBPSPRequest.getSolution().getOffers().get(i).getSite().size(); j++) {
				Offer currentOffer = retreiveICBPSPRequest.getSolution().getOffers().get(i);
				Site currentSite = retreiveICBPSPRequest.getSolution().getOffers().get(i).getSite().get(j);
				Long nxSiteId=null;
				if(StringConstants.IPNE.equalsIgnoreCase(sourceName)&& siteIdmap != null && siteIdmap.containsKey(currentSite.getSiteId())) {
					Map<Object, Object>  existingSite =(Map<Object, Object>) siteIdmap.get(currentSite.getSiteId());
					nxSiteId=(Long) existingSite.get("nxSiteId");
					if("210".equalsIgnoreCase(currentOffer.getOfferId())) {
						existingSite.put("swcCLLI", currentSite.getSwcClli());
						existingSite.put("buildingClli",currentSite.getCustomerLocationClli());
						existingSite.put("npanxx",currentSite.getNpanxx());
						List<String> popclli=getDataInString(currentOffer,MyPriceConstants.EPLSWAN_POPCLLI_UDFID,currentSite.getSiteId().toString());
						if (!CollectionUtils.isEmpty(popclli)) {
							existingSite.put("popClli",popclli.get(0));
						}	
						List<String> tokenIdEthernet=getDataInString(currentOffer,MyPriceConstants.EPLSWAN_TOKEN_ID_ETHERNET_UDFID,currentSite.getSiteId().toString());
						if (!CollectionUtils.isEmpty(tokenIdEthernet)) {
							existingSite.put("tokenIdEthernet",tokenIdEthernet.get(0));
						}
						List<String> vendorZone=getDataInString(currentOffer,MyPriceConstants.EPLSWAN_VENDOR_ZONE_UDFID,currentSite.getSiteId().toString());
						if (!CollectionUtils.isEmpty(vendorZone)) {
							existingSite.put("vendorZone",vendorZone.get(0));
						}
						
					}
					siteIdmap.put(currentSite.getSiteId(),existingSite);
					retreiveICBPSPRequest.getSolution().getOffers().get(i).getSite().get(j).setNxSiteId(nxSiteId); 
					continue;
				}
				else {
					nxSiteId = getNxSiteId();	 
				}
				retreiveICBPSPRequest.getSolution().getOffers().get(i).getSite().get(j).setNxSiteId(nxSiteId);
				Map<Object, Object> map = new HashMap<>();
				String address2=Strings.isNullOrEmpty(currentSite.getAddress2()) ? "" : currentSite.getAddress2();
				siteIdmap.put(currentSite.getSiteId(),nxSiteId);
				map.put("pricerdSiteId", currentSite.getSiteId());
				map.put("nxSiteId", nxSiteId);
				map.put("name", currentSite.getSiteName());
				map.put("addressLine", currentSite.getAddress1() + " " + address2);
				map.put("city", currentSite.getCity());
				map.put("state", currentSite.getState());
				map.put("postalCode", currentSite.getZipCode());
				map.put("country", currentSite.getCountry());
				map.put("address",
						currentSite.getAddress1() + " " + address2 + " " + currentSite.getCity() + " "
								+ currentSite.getState() + " " + currentSite.getZipCode() + " "
								+ currentSite.getCountry());
				map.put("validationStatus", CommonConstants.VALID);
				Map<String, String> cLLiMap = null;
				if("103".equalsIgnoreCase(currentOffer.getOfferId())) {
					for (int k = 0; k < currentSite.getDesignSiteOfferPort().size(); k++) {
						for (int l = 0; l < currentSite.getDesignSiteOfferPort().get(k).getComponent().size(); l++) {
							String componentCodeType = currentSite.getDesignSiteOfferPort().get(k).getComponent().get(l)
									.getComponentCodeType();
							if (null != componentCodeType && "Port".equalsIgnoreCase(componentCodeType)) {
								List<UDFBaseData> udfList = currentSite.getDesignSiteOfferPort().get(k).getComponent().get(l)
										.getDesignDetails();
								cLLiMap = getCLLiValues(udfList);
							}
						}
					}
				} /*else if ("120".equalsIgnoreCase(currentOffer.getOfferId())) {
					cLLiMap = adeClliMap.containsKey(currentSite.getSiteId()) ? adeClliMap.get(currentSite.getSiteId()) : null;
				}*/
				
				//for ADE swcCLLI and buildingClli(CustomerLocationClli) pick from only siteLevel
				if("120".equalsIgnoreCase(currentOffer.getOfferId())){
					map.put("swcCLLI", currentSite.getSwcClli());
					map.put("buildingClli",currentSite.getCustomerLocationClli());
				}else if("210".equalsIgnoreCase(currentOffer.getOfferId())){
					map.put("swcCLLI", currentSite.getSwcClli());
					map.put("buildingClli",currentSite.getCustomerLocationClli());
					map.put("npanxx",currentSite.getNpanxx());
					List<String> popclli=getDataInString(currentOffer,MyPriceConstants.EPLSWAN_POPCLLI_UDFID,currentSite.getSiteId().toString());
					if (!CollectionUtils.isEmpty(popclli)) {
						map.put("popClli",popclli.get(0));
					}	
					List<String> tokenIdEthernet=getDataInString(currentOffer,MyPriceConstants.EPLSWAN_TOKEN_ID_ETHERNET_UDFID,currentSite.getSiteId().toString());
					if (!CollectionUtils.isEmpty(tokenIdEthernet)) {
						map.put("tokenIdEthernet",tokenIdEthernet.get(0));
					}
					List<String> vendorZone=getDataInString(currentOffer,MyPriceConstants.EPLSWAN_VENDOR_ZONE_UDFID,currentSite.getSiteId().toString());
					if (!CollectionUtils.isEmpty(vendorZone)) {
						map.put("vendorZone",vendorZone.get(0));
					}
				}
				else {
					if (null != cLLiMap && cLLiMap.containsKey("swcLLi")) {
						map.put("swcCLLI", cLLiMap.get("swcLLi"));
					} else {
						map.put("swcCLLI", currentSite.getSwcClli());
					}
					if (null != cLLiMap && cLLiMap.containsKey("buildingClli")) {
						map.put("buildingClli", cLLiMap.get("buildingClli"));
					} else {
						map.put("buildingClli", currentSite.getBuildingClli());
					}
				}
				
				map.put("regionFranchiseStatus", currentSite.getRegionCode());
				map.put("globalLocationId", currentSite.getGlobalLocationId());
				if(StringConstants.IPNE.equalsIgnoreCase(sourceName)) {
					siteIdmap.put(currentSite.getSiteId(),map);
				}
				else {
					siteAddress.add(map);
				}
			}
		}
		if(StringConstants.IPNE.equalsIgnoreCase(sourceName)) {
			ArrayList<Object> siteAddressList= siteIdmap.values().stream().collect(Collectors.toCollection(ArrayList::new));
			siteAddress.addAll(siteAddressList);
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
				String rdOpportunityTeamQ="";
				int i = 1;
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
		document.setRdNameQ(retreiveICBPSPRequest.getSolution().getUserFirstName() + " "
				+ retreiveICBPSPRequest.getSolution().getUserLastName());
		document.setRdTitleQ(retreiveICBPSPRequest.getSolution().getUserTitle());
		document.setRdOfficeQ(retreiveICBPSPRequest.getSolution().getUserWorkPhone());
		document.setRdMobileQ(retreiveICBPSPRequest.getSolution().getUserMobile());
		document.setRdEmailQ(retreiveICBPSPRequest.getSolution().getUserEmail());
		document.setWiSolutionVersionQ(retreiveICBPSPRequest.getSolution().getSolutionVersion());
		//changes for automationFlow
		if(StringUtils.isNotEmpty(retreiveICBPSPRequest.getSolution().getAutomationInd()) 
				&& retreiveICBPSPRequest.getSolution().getAutomationInd().equalsIgnoreCase("Y") ) {
		
			if("103".equalsIgnoreCase(retreiveICBPSPRequest.getSolution().getOffers().get(0).getOfferId())) {
				document.setWiSubLayerQ("Guidebook");
			}
       if(!sourceName.equalsIgnoreCase(StringConstants.IPNE)) {
			document.setRdSegmentQ("Wholesale");
			document.setRdSalesChannelsQ("Wholesale");
         	document.setWiLayerQ("Wholesale");
         	document.setRdBUQ("Affiliate");
			document.setRdAVPQ("wallaceMatt");
			document.setRiCompetitorsQ("level3");
		}
		
		}
		
		if("6".equalsIgnoreCase(retreiveICBPSPRequest.getSolution().getOffers().get(0).getOfferId())) {
			String contractType=retreiveICBPSPRequest.getSolution().getContractType();
			if(StringUtils.isNotEmpty(contractType)){
				NxLookupData nxLookup=nxLookupDataRepository.findTopByDatasetNameAndItemId("MP_CONTRACT_TYPE", contractType);
				if(null!=nxLookup && StringUtils.isNotEmpty(nxLookup.getDescription())) {
					document.setContractPricingScopeQ(nxLookup.getDescription());
				}
				
			}
			if(StringUtils.isNotEmpty(retreiveICBPSPRequest.getSolution().getContractNumber())) {
				document.setWiContractNumber(retreiveICBPSPRequest.getSolution().getContractNumber());
			}
			
			if(StringConstants.CONSTANT_Y.equalsIgnoreCase(retreiveICBPSPRequest.getSolution().getPpcosUser())) {
				document.setPpcosUserQ("true");
			}else if(StringConstants.CONSTANT_N.equalsIgnoreCase(retreiveICBPSPRequest.getSolution().getPpcosUser())) {
				document.setPpcosUserQ("false");
			}
			if(StringConstants.CONSTANT_Y.equalsIgnoreCase(retreiveICBPSPRequest.getSolution().getDiverseAccessInd())) {
				document.setWlDAIndicatorQ("true");
			}else if(StringConstants.CONSTANT_N.equalsIgnoreCase(retreiveICBPSPRequest.getSolution().getDiverseAccessInd())) {
				document.setWlDAIndicatorQ("false");
			}
		}
		
		

		// Persist to NX_MP_SITE_DICTIONARY
		NxMpSiteDictionary nxMpTRow = persistToNXMPSITEDICTIONARYTABLE(retreiveICBPSPRequest, createTransactionResponse,
				siteAddressJson);

		if(null != retreiveICBPSPRequest.getSolution().getOpportunitySalesTeam() && !retreiveICBPSPRequest.getSolution().getOpportunitySalesTeam().isEmpty()) {
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
			log.info("call for updateTransactionCleanSave_t success!");
			msResponse.put("status", true);
		}else {
			log.info("Call for updateTransactionCleanSave_t failed");
			msResponse.put("status", false);
		}

		log.info("Service Log :: [Nexxus [MyPrice - Info] ::  updateCleanSaveTProcessor Destroyed");

		return msResponse;

	}
	
	public Map<String, String> getCLLiValues(List<UDFBaseData> udfList) {
		Map<String, String> cLLiMap = null;
		if (CollectionUtils.isNotEmpty(udfList)) {
			cLLiMap = new HashMap<String, String>();
			Predicate<UDFBaseData> p = (obj) -> (obj.getUdfId() == 200045 || obj.getUdfId() == 20184 || obj.getUdfId() == 200160);
			List<UDFBaseData> udfdata = (List<UDFBaseData>) udfList.stream().filter(p).collect(Collectors.toList());
			// String swcLLi
			AtomicReference<String> swcLLiRef = new AtomicReference<String>();
			AtomicReference<String> buildingClliRef = new AtomicReference<String>();
			udfdata.stream().forEach(o -> {
				if (o.getUdfId() == 200045) {
					if (CollectionUtils.isNotEmpty(o.getUdfAttributeText())) {
						String swcLLi = o.getUdfAttributeText().get(0);
						swcLLiRef.set(swcLLi);
					}
				}
				if (o.getUdfId() == 20184 || o.getUdfId() == 200160) {
					if (CollectionUtils.isNotEmpty(o.getUdfAttributeText())) {
						String buildingClli = o.getUdfAttributeText().get(0);
						buildingClliRef.set(buildingClli);
					}
				}
			});
			cLLiMap.put("swcLLi", swcLLiRef.get());
			cLLiMap.put("buildingClli", buildingClliRef.get());
		}
		return cLLiMap;
	}

	public String getOptyDetails(RetreiveICBPSPRequest retreiveICBPSPRequest) throws SalesBusinessException { 
		 
		String sAARTAccountNumber = null; 
 
		String attUid = retreiveICBPSPRequest.getSolution().getUserId(); 
		String optyId = retreiveICBPSPRequest.getSolution().getOptyId(); 
 
		if(!attUid.isEmpty() && !optyId.isEmpty()) { 
			Map<String, Object> getOptyInfoRequest = new HashMap<>(); 
			getOptyInfoRequest.put("attuid", attUid); 
			getOptyInfoRequest.put("optyId", optyId);
			getOptyInfoRequest.put("myPriceIntiated", "Y");
 
			GetOptyResponse getOptyResponse = null; 
 
			getOptyResponse = (GetOptyResponse) getOptyInfoWSHandler.initiateGetOptyInfoWebService(getOptyInfoRequest); 
 
			sAARTAccountNumber = getOptyResponse.getOptyId(); 
 
		}else { 
			log.info("Service Log :: [Nexxus [MyPrice - Error]] :: Enter a valid attuid or optyId"); 
		} 
 
		return sAARTAccountNumber; 
	}

	public Map<String, Object> callMsForCleanSaveTransaction(String myPricetransactionId, String solutionDataJsonBlock)
			throws SalesBusinessException {
		log.info("Service Log :: [Nexxus [MyPrice - Info]] :: Invoked callMsForCleanSaveTransaction() method");
		String updateTransactionCleanSaveRequestURL = env.getProperty("myPrice.updateTransactionCleanSaveRequest");
		String transactionIdString = String.valueOf(myPricetransactionId);
		String urlWithTransactionId = updateTransactionCleanSaveRequestURL.replace("{TransactionId}",
				transactionIdString);
		
		String updateRequest = solutionDataJsonBlock;
		Map<String, String> headers  = new HashMap<>();
		headers.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.MYPRICE_AUTHORIZATION));
		headers.put(StringConstants.REQUEST_CONTENT_TYPE, "application/json");		
//		Map<String, Object> updateResponse = restClient.initiateWebService(updateRequest, urlWithTransactionId , "POST", headers, queryParameters);
		Map<String, Object> updateResponse = new HashMap<String, Object>();
		String proxy = null;
		if(StringConstants.CONSTANT_Y.equalsIgnoreCase(isProxyEnabled)) {
			proxy = env.getProperty(CommonConstants.CDP_HTTP_PROXY);
		}
		String response = httpRestClient.callHttpRestClient(urlWithTransactionId, HttpMethod.POST, null, updateRequest,
				headers, proxy);
		updateResponse.put(MyPriceConstants.RESPONSE_DATA, response);
		updateResponse.put(MyPriceConstants.RESPONSE_CODE, 200);
		updateResponse.put(MyPriceConstants.RESPONSE_MSG,"OK");

		log.info("Service Log :: [Nexxus [MyPrice - Info]] :: Destroyed callMsForCleanSaveTransaction() method");	
		return updateResponse;
	}

	public void persistTONXMPSOLUTIONDETAILSTABLE(RetreiveICBPSPRequest retreiveICBPSPRequest,
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

	public String getupdateTransactionCSRTJSON(Map<Object, Object> updateCleanTransactionCleanSaveRequestMap)
			throws JsonProcessingException {

		ObjectMapper obj = new ObjectMapper();
		obj.enable(SerializationFeature.INDENT_OUTPUT);
		String solutionDataJsonBlock;
		solutionDataJsonBlock = obj.writeValueAsString(updateCleanTransactionCleanSaveRequestMap);
		log.info("UT-CleanSave : getupdateTransactionCSRTJSON() : documents request :==>> {} "+solutionDataJsonBlock);
		return solutionDataJsonBlock;
	}

	public String getSiteAddressJson(List<Object> siteAddress) throws JsonProcessingException {
		ObjectMapper obj = new ObjectMapper();
		String siteAddressBlock;
		siteAddressBlock = obj.writerWithDefaultPrettyPrinter().writeValueAsString(siteAddress);
		return siteAddressBlock;
	}

	public NxMpSiteDictionary persistToNXMPSITEDICTIONARYTABLE(RetreiveICBPSPRequest retreiveICBPSPRequest,
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
	
	public long getNxSiteId() {		 
 		Query q = em.createNativeQuery("SELECT SEQ_NX_SITE_ID.NEXTVAL FROM DUAL");
 		BigDecimal result = (BigDecimal) q.getSingleResult();
 		return result.longValue();
 	}
	
	public String getLowerCaseString(String data) {
		if(null != data && data.length() != 0) {
			return data.toLowerCase();
		}
		return null;
	}
	
	public List<String> getDataInString(Object request,String udfId,String referenceId) {
		if(request != null) { 
			String Path=eplswanCleansaveAttibutesPath;
			Path= String.format(Path,referenceId,udfId );
			TypeRef<List<Object>> mapType = new TypeRef<List<Object>>() {};
			List<Object> results = jsonPathUtil.search(request, Path, mapType);
			if(CollectionUtils.isEmpty(results)) {
				return null;
			} else {
				System.out.print( results.get(0));
				return (List<String>) results.get(0);
			}
		}
		return null;
	}
}
