package com.att.sales.nexxus.custompricing.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.common.MessageConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.custompricing.model.AllIncPrice;
import com.att.sales.nexxus.custompricing.model.ComponentDetail;
import com.att.sales.nexxus.custompricing.model.CustomPricingRequest;
import com.att.sales.nexxus.custompricing.model.CustomPricingResponse;
import com.att.sales.nexxus.custompricing.model.Offer;
import com.att.sales.nexxus.custompricing.model.Price;
import com.att.sales.nexxus.custompricing.model.PriceAttribute;
import com.att.sales.nexxus.custompricing.model.PriceScenario;
import com.att.sales.nexxus.custompricing.model.Solution;
import com.att.sales.nexxus.dao.model.NxDesign;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxMpPriceDetails;
import com.att.sales.nexxus.dao.model.NxMpSiteDictionary;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxDesignRepository;
import com.att.sales.nexxus.dao.repository.NxLineItemLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxMpPriceDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxMpSiteDictionaryRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.myprice.transaction.dao.service.NxMpRepositoryService;
import com.att.sales.nexxus.myprice.transaction.model.AllIncPrices3PA;
import com.att.sales.nexxus.myprice.transaction.model.AseodReqRatesLineItem;
import com.att.sales.nexxus.myprice.transaction.model.AseodReqRatesResponse;
import com.att.sales.nexxus.myprice.transaction.service.Aseod3PAReqRatesHelperService;
import com.att.sales.nexxus.myprice.transaction.service.AseodReqRatesServiceImpl;
import com.att.sales.nexxus.serviceValidation.model.Locations;
import com.att.sales.nexxus.serviceValidation.model.LocationsWrapper;
import com.att.sales.nexxus.util.JacksonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service("ICustomPricingServiceImpl")
public class ICustomPricingServiceImpl extends BaseServiceImpl implements ICustomPricingService {

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(ICustomPricingServiceImpl.class);

	@Autowired
	private NxMpDealRepository nxMpDealRepository;

	@Autowired
	private NxMpPriceDetailsRepository nxMpPriceDetailsRepository;

	@Autowired
	private NxDesignRepository nxDesignRepository;

	@Autowired
	private NxSolutionDetailsRepository repository;

	@Autowired
	private NxMpSiteDictionaryRepository nxMpSiteDicRepo;

	@Autowired
	private AseodReqRatesServiceImpl aseodReqRatesServiceImpl;
	
	@Autowired
	private Aseod3PAReqRatesHelperService aseod3PAReqRatesHelperService;
	
	@Autowired
	private NxLineItemLookupDataRepository lineItemLookupDataRepository;

	@Autowired
	private NxMpRepositoryService nxMpRepositoryService;
	
	@Override
	public ServiceResponse getCutomPricing(CustomPricingRequest request) throws JSONException, SalesBusinessException{

		CustomPricingResponse response = new CustomPricingResponse();
		Solution soln = new Solution();
		soln.setSolutionDeterminants(request.getSolution().getSolutionDeterminants());
		soln.setUserId(request.getSolution().getUserId());
		soln.setExternalKey(request.getSolution().getExternalKey());
		soln.setDealId(request.getSolution().getDealId());
		soln.setVersionNumber(request.getSolution().getVersionNumber());
		soln.setProductNumber(request.getSolution().getProductNumber());
		
		//Add logic to retrieve wrt to deal id, version and revision
		List<NxMpDeal> nxMpDeals;
		
		if(request.getSolution().getRevisionNumber() != null) {
			nxMpDeals =	nxMpDealRepository.findBydealIDVersnIdRevId(request.getSolution().getDealId(),request.getSolution().getVersionNumber(),request.getSolution().getRevisionNumber(), new HashSet<String>(Arrays.asList(MyPriceConstants.DEAL_STATUS_APPROVED, CommonConstants.APPROVED)));
		} else {
			nxMpDeals = nxMpDealRepository.findBydealIDVersnId(request.getSolution().getDealId(),request.getSolution().getVersionNumber(), new HashSet<String>(Arrays.asList(MyPriceConstants.DEAL_STATUS_APPROVED, CommonConstants.APPROVED)));
		}
		if(CollectionUtils.isNotEmpty(nxMpDeals)) {
			soln.setDealStatus(nxMpDeals.get(0).getDealStatus());
			soln.setRlQuoteUrl(nxMpDeals.get(0).getQuoteUrl());
			soln.setRlExpirationDate(nxMpDeals.get(0).getRateLetterExpiresOn());
			soln.setRlDiscountApprovalType(nxMpDeals.get(0).getAutoApproval());
			soln.setRlType(nxMpDeals.get(0).getRlType());
			
			NxSolutionDetail nxSolndetail = repository.findByNxSolutionId(nxMpDeals.get(0).getSolutionId());
			if(null != nxSolndetail && StringConstants.FLOW_TYPE_AUTO.equalsIgnoreCase(nxSolndetail.getFlowType())) {
				Offer offer = createOfferBlock(nxMpDeals.get(0));
				offer.setPrices(createSalesPriceBlock(nxMpDeals, nxSolndetail,StringConstants.FLOW_TYPE_AUTO));
				soln.getOffers().add(offer);
				response.setSolution(soln);
				setSuccessResponse(response);
			}
			else if (null != nxSolndetail && (StringConstants.FLOW_TYPE_FMO.equalsIgnoreCase(nxSolndetail.getFlowType()))) {
				for(NxMpDeal nxMpDeal : nxMpDeals) {
					Offer offer = createOfferBlock(nxMpDeal);
					offer.setPrices(createSalesPriceBlock(new ArrayList<NxMpDeal>(Arrays.asList(nxMpDeal)), nxSolndetail,StringConstants.FLOW_TYPE_FMO));
					soln.getOffers().add(offer);
				}
				response.setSolution(soln);
				setSuccessResponse(response);
			}
			else if (null != nxSolndetail
					&& (StringConstants.FLOW_TYPE_MP_FIRM.equalsIgnoreCase(nxSolndetail.getFlowType())
							|| StringConstants.FLOW_TYPE_IPnE.equalsIgnoreCase(nxSolndetail.getFlowType())
							|| StringConstants.FLOW_TYPE_INR.equalsIgnoreCase(nxSolndetail.getFlowType())
							|| StringConstants.FLOW_TYPE_IGLOO_QUOTE.equalsIgnoreCase(nxSolndetail.getFlowType()))) {
				List<Locations> locations;
				try {
					locations = createLocationsBlock(nxMpDeals, nxSolndetail.getFlowType());
				
				for(NxMpDeal nxMpDeal : nxMpDeals) {
					Offer offer = createOfferBlock(nxMpDeal);
					if(CollectionUtils.isNotEmpty(locations)) {
						offer.setLocations(locations);
					}
					offer.setPrices(createMyPricePriceBlock(nxMpDeal, nxSolndetail,nxSolndetail.getFlowType()));
					soln.getOffers().add(offer);
				}
				soln.setCustomerName(nxSolndetail.getCustomerName());
				soln.setSaartAccountNumber(nxSolndetail.getL3Value());
			
			response.setSolution(soln);
			setSuccessResponse(response);
			} catch (JsonProcessingException | JSONException | SalesBusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			response.setSolution(soln);
			response.setStatus(getErrorStatus(MessageConstants.NO_DATA_FOUND));
		}
		}

		return response;
	}
	
	private Price createSalesPriceBlock(List<NxMpDeal> nxMpDeals, NxSolutionDetail nxSolndetail,String flowType) {
		Price prices = new Price();
		for (NxMpDeal nxMpDeal : nxMpDeals) {
			Long nxTxnId = nxMpDeal.getNxTxnId();
			String transactionId=nxMpDeal.getTransactionId();
			PriceScenario priceScenario = createPriceScenario(nxMpDeal);
			List<AllIncPrice> allIncPrices = null;
			AllIncPrices3PA allIncpricesTpa=null;
			if(Optional.ofNullable(nxMpDeal.getOfferId()).isPresent() && Optional.ofNullable(nxMpDeal.getTransactionId()).isPresent()
					&& nxMpDeal.getOfferId().equalsIgnoreCase(StringConstants.OFFERNAME_ASENOD)) {
				//contractPricingScope_q = specialConstruction then allincblock should be excluded in getcustomePricing response
				String contractPricingScope=nxMpDeal.getContractPricingScope();
				boolean isSpecialConstruction=StringUtils.isNotEmpty(contractPricingScope) 
						&& "specialConstruction".equals(contractPricingScope)?true:false;
				if(!isSpecialConstruction) {
					allIncPrices = asenodReqRates(nxMpDeal.getOfferId(),nxMpDeal.getTransactionId());
					allIncpricesTpa=aseod3PAReqRatesHelperService.getIncPrices3PA(transactionId);
				}
			
			}
			List<NxDesign> nxDesigns = nxDesignRepository.findByNxSolutionDetail(nxSolndetail);
			List<String> currentComponentTypes = new ArrayList<>();
			List<Long> currentComponentIds = new ArrayList<>();
			List<ComponentDetail> finalCompDetails = new ArrayList<>();
			List<ComponentDetail> componentDetails = null;
			for (NxDesign nxDesign : nxDesigns) {
				List<NxMpPriceDetails> nxMpPriceDetails = nxMpPriceDetailsRepository
						.findByNxDesignIdAndNxTxnId(nxDesign.getNxDesignId(), nxTxnId);
				if (CollectionUtils.isNotEmpty(nxMpPriceDetails)) {
					List<String> usocPrasent=new ArrayList<>();
					priceLoop:
					for (NxMpPriceDetails nxMpPriceDetail : nxMpPriceDetails) {
						//extract BEID
						
						if(usocPrasent.contains(nxMpPriceDetail.getBeid().concat(nxMpPriceDetail.getFrequency())) && "ASENoD".equalsIgnoreCase(nxMpDeal.getOfferId())) {
							continue priceLoop;
						}	
							if (currentComponentTypes.contains(nxMpPriceDetail.getComponentType()) && currentComponentIds.contains(nxMpPriceDetail.getComponentId())) {
								for (ComponentDetail existingComponentDetail : finalCompDetails) {
									if ((nxDesign.getAsrItemId().equalsIgnoreCase(existingComponentDetail.getAsrItemID()) || (StringConstants.FLOW_TYPE_FMO.equalsIgnoreCase(flowType) && nxMpPriceDetail.getAsrItemId().equalsIgnoreCase(existingComponentDetail.getAsrItemID())))
										&& nxMpPriceDetail.getComponentType()
												.equalsIgnoreCase(existingComponentDetail.getComponentType()) && Long.compare(nxMpPriceDetail.getComponentId(), existingComponentDetail.getComponentId()) == 0) {
											existingComponentDetail.getPriceAttributes()
											.add(createPriceAttribute(nxMpPriceDetail,flowType));
										if(null == existingComponentDetail.getSpecialConstructionNRCCharge() || "0.0".equalsIgnoreCase(existingComponentDetail.getSpecialConstructionNRCCharge())) {
											existingComponentDetail.setSpecialConstructionNRCCharge(nxMpPriceDetail.getSpecialConstructionAppNRC());
										}
								//Added Now
								if(null != flowType && flowType.equalsIgnoreCase(StringConstants.FLOW_TYPE_FMO)) {
									//existingComponentDetail.setSiteCountry(nxDesign.getCountry());
									//existingComponentDetail.setSiteId(String.valueOf(nxDesign.getSiteId()));
									existingComponentDetail.setSiteCountry(nxMpPriceDetail.getNxSiteCountry());
									existingComponentDetail.setSiteId(nxMpPriceDetail.getAsrItemId());
									//existingComponentDetail.setSocVersion(nxMpPriceDetail.getSocVersion());
									existingComponentDetail.setRatePlanId(nxMpPriceDetail.getRatePlanId());
									existingComponentDetail.setEthTokenId(nxMpPriceDetail.getEthTokenId());
								}
								}
							}
						} else {
							currentComponentTypes.add(nxMpPriceDetail.getComponentType());
							currentComponentIds.add(nxMpPriceDetail.getComponentId());
							ComponentDetail componentDetail = new ComponentDetail();
							componentDetail.setAsrItemID(nxDesign.getAsrItemId());
							componentDetail.setComponentType(nxMpPriceDetail.getComponentType());
							componentDetail.setComponentId(nxMpPriceDetail.getComponentId());
							
							componentDetail.setSpecialConstructionNRCCharge(nxMpPriceDetail.getSpecialConstructionAppNRC());
							componentDetail.setComponentParentId(nxMpPriceDetail.getComponentParentId());
							if(StringConstants.FLOW_TYPE_AUTO.equalsIgnoreCase(flowType) && ("ADE".equalsIgnoreCase(nxMpDeal.getOfferId()) || "ASENoD".equalsIgnoreCase(nxMpDeal.getOfferId()) || "ASE".equalsIgnoreCase(nxMpDeal.getOfferId()))) {
								componentDetail.setTerm(nxMpPriceDetail.getTerm());
							}
							//Added Now
							if(null != flowType && flowType.equalsIgnoreCase(StringConstants.FLOW_TYPE_FMO)) {
								//componentDetail.setSiteCountry(nxDesign.getCountry());
								//componentDetail.setSiteId(String.valueOf(nxDesign.getSiteId()));
								componentDetail.setAsrItemID(nxMpPriceDetail.getAsrItemId());
								componentDetail.setSiteCountry(nxMpPriceDetail.getNxSiteCountry());
								componentDetail.setSiteId(nxMpPriceDetail.getAsrItemId());
								//componentDetail.setSocVersion(nxMpPriceDetail.getSocVersion());
								componentDetail.setRatePlanId(nxMpPriceDetail.getRatePlanId());
								componentDetail.setEthTokenId(nxMpPriceDetail.getEthTokenId());
							}
							componentDetail.getPriceAttributes().add(createPriceAttribute(nxMpPriceDetail,flowType));
							componentDetails = new ArrayList<>();
							componentDetails.add(componentDetail);
							finalCompDetails.add(componentDetail);
						}
						usocPrasent.add(nxMpPriceDetail.getBeid().concat(nxMpPriceDetail.getFrequency()));
						//}
					}
					currentComponentTypes.clear();
					currentComponentIds.clear();
				}
			}
			if(CollectionUtils.isNotEmpty(allIncPrices)) {
				priceScenario.setAllIncPrices(allIncPrices);
				logger.info("transaction-id : "+nxMpDeal.getTransactionId() +" no of prices :==>> "+priceScenario.getAllIncPrices().size());
			}
			
			if(null!=allIncpricesTpa) {
				priceScenario.setAllIncPricesTpa(allIncpricesTpa);
			}
			priceScenario.setComponentDetails(finalCompDetails);
			prices.getPriceScenarios().add(priceScenario);
		}
		return prices;
	}
	
	private List<Locations> createLocationsBlock(List<NxMpDeal> nxMpDeals, String flowType) throws JSONException, SalesBusinessException, JsonProcessingException {
		List<Locations> locationsList = new ArrayList<>();
		for(NxMpDeal nxMpDeal : nxMpDeals) {
			NxMpSiteDictionary nxMpSiteDic = nxMpSiteDicRepo.findByNxTxnId(nxMpDeal.getNxTxnId());
			if(null != nxMpSiteDic && Optional.ofNullable(nxMpSiteDic.getSiteAddress()).isPresent() && 
					(StringConstants.FLOW_TYPE_INR.equalsIgnoreCase(flowType) 
							|| StringConstants.FLOW_TYPE_IGLOO_QUOTE.equalsIgnoreCase(flowType))) {
				
				JSONObject siteDictAddress = new JSONObject(nxMpSiteDic.getSiteAddress());
				JSONArray siteDictArray = new JSONArray(siteDictAddress.get("siteAddress").toString());
				for (int i = 0; i < siteDictArray.length(); i++) {
					Locations location = new Locations();
					JSONObject siteDict = siteDictArray.getJSONObject(i);
					String siteName = (siteDict.has("name") && !JSONObject.NULL.equals(siteDict.get("name")))? siteDict.getString("name") : null;
					String city = (siteDict.has("city") && !JSONObject.NULL.equals(siteDict.get("city")))? siteDict.getString("city") : null;
					String state = (siteDict.has("state") && !JSONObject.NULL.equals(siteDict.get("state")))? siteDict.getString("state") : null;
					String zipCode = (siteDict.has("postalCode") && !JSONObject.NULL.equals(siteDict.get("postalCode")))? siteDict.getString("postalCode") : null;
					String country = (siteDict.has("country") && !JSONObject.NULL.equals(siteDict.get("country")))? siteDict.getString("country") : null;
					String Street = (siteDict.has("addressLine") && !JSONObject.NULL.equals(siteDict.get("addressLine")))? siteDict.getString("addressLine") : null;
					String validationStatus = (siteDict.has("validationStatus") && !JSONObject.NULL.equals(siteDict.get("validationStatus")))? siteDict.getString("validationStatus") : null;
					String siteInfoSource = (siteDict.has("siteInfoSource") && !JSONObject.NULL.equals(siteDict.get("siteInfoSource")))? siteDict.getString("siteInfoSource") : null;
					String nxSiteId = (siteDict.has("nxSiteId") && !JSONObject.NULL.equals(siteDict.get("nxSiteId")) && !siteDict.get("nxSiteId").toString().isEmpty())? siteDict.get("nxSiteId").toString() : null;

					location.setCity(city);
					location.setState(state);
					location.setZip(zipCode);
					location.setCountry(country);
					location.setStreet(Street);
					location.setName(siteName);
					location.setValidationStatus(validationStatus);
					location.setSiteInfoSource(siteInfoSource);
					if(nxSiteId != null && StringUtils.isNotEmpty(nxSiteId)) {
						location.setNxSiteId(nxSiteId);
					}
					locationsList.add(location);
				}
				
			} else if(null != nxMpSiteDic && Optional.ofNullable(nxMpSiteDic.getSiteJson()).isPresent()) {
				ObjectMapper thisMapper = new ObjectMapper();
				String siteJson = nxMpSiteDic.getSiteJson();
				LocationsWrapper locations;
				try {
					locations = thisMapper.readValue(siteJson, LocationsWrapper.class);
					locationsList.addAll(locations.getLocations());
					if(locationsList != null && locationsList.size() > 0) {
						locationsList.stream().forEach(location -> location.setName(location.getLocName()));
					}
				} catch (IOException e) {
					logger.info("exception stack trace {}",e);
				}
				break;
			}// ipne flow
		}
		return locationsList;
	}
	
	private Price createMyPricePriceBlock(NxMpDeal nxMpDeal, NxSolutionDetail nxSolndetail,String flowType) {
		Price prices = new Price();
		
		//for(NxMpDeal nxMpDeal : nxMpDeals) {
			PriceScenario priceScenario = createPriceScenario(nxMpDeal);
			List<AllIncPrice> allIncPrices = null;
			AllIncPrices3PA allIncpricesTpa=null;
			if(Optional.ofNullable(nxMpDeal.getOfferId()).isPresent() && Optional.ofNullable(nxMpDeal.getTransactionId()).isPresent() && nxMpDeal.getOfferId().equalsIgnoreCase(StringConstants.OFFERNAME_ASENOD)) {
				//contractPricingScope_q = specialConstruction then allincblock should be excluded in getcustomePricing response
				String contractPricingScope=nxMpDeal.getContractPricingScope();
				boolean isSpecialConstruction=StringUtils.isNotEmpty(contractPricingScope) 
						&& "specialConstruction".equals(contractPricingScope)?true:false;
				if(!isSpecialConstruction) {
					allIncPrices = asenodReqRates(nxMpDeal.getOfferId(),nxMpDeal.getTransactionId());
					allIncpricesTpa=aseod3PAReqRatesHelperService.getIncPrices3PA(nxMpDeal.getTransactionId());
				}
				
			}
			List<ComponentDetail> componentDetails = new ArrayList<>();
			//List<Long> currentNxDesignIds = new ArrayList<>();
			
			Map<Long, List<Long>> currentNxDesignIds = new HashMap<Long, List<Long>>();
			
			List<NxMpPriceDetails> nxMpPriceDetails = nxMpPriceDetailsRepository.findByNxTxnId(nxMpDeal.getNxTxnId());
			if (CollectionUtils.isNotEmpty(nxMpPriceDetails)) {
				List<String> usocPrasent=new ArrayList<>();
				priceLoop:
				for (NxMpPriceDetails nxMpPriceDetail : nxMpPriceDetails) {
					//NxDesign nxDesign = nxDesignRepository.findByNxDesignId(nxMpPriceDetail.getNxDesignId());
				//	if(nxMpPriceDetail.getProdRateId() != null || StringConstants.FLOW_TYPE_INR.equalsIgnoreCase(flowType) || MyPriceConstants.LocalAccess.equalsIgnoreCase(nxMpDeal.getOfferId())) {
					
					if("ASENoD".equalsIgnoreCase(nxMpDeal.getOfferId()) && usocPrasent.contains(nxMpPriceDetail.getBeid().concat(nxMpPriceDetail.getFrequency()))) {
						continue priceLoop;
					}
					if(MyPriceConstants.PRODUCT_RATE_ID_MAP.contains(nxMpDeal.getOfferId()) && nxMpPriceDetail.getProdRateId() == null 
							&& !(StringConstants.FLOW_TYPE_AUTO.equalsIgnoreCase(flowType) || StringConstants.FLOW_TYPE_FMO.equalsIgnoreCase(flowType))) {
						logger.info("Skipping price blocks for product rate id is empty scenario");
					}else {
						if (currentNxDesignIds.containsKey(nxMpPriceDetail.getNxDesignId()) && currentNxDesignIds.get(nxMpPriceDetail.getNxDesignId()).contains(nxMpPriceDetail.getTerm())) {
							for (ComponentDetail existingComponentDetail : componentDetails) {
								if (((null != nxMpPriceDetail.getAsrItemId() && nxMpPriceDetail.getAsrItemId().equalsIgnoreCase(existingComponentDetail.getAsrItemID())) ||
										(null != nxMpPriceDetail.getNxDesignId() && Long.compare(nxMpPriceDetail.getNxDesignId(),existingComponentDetail.getComponentId()) == 0)) &&
										(null != nxMpPriceDetail.getTerm() && Long.compare(nxMpPriceDetail.getTerm(),existingComponentDetail.getTerm()) == 0)){
										existingComponentDetail.getPriceAttributes().add(createPriceAttribute(nxMpPriceDetail,flowType));
									if(null == existingComponentDetail.getSpecialConstructionNRCCharge() || "0.0".equalsIgnoreCase(existingComponentDetail.getSpecialConstructionNRCCharge())) {
										existingComponentDetail.setSpecialConstructionNRCCharge(nxMpPriceDetail.getSpecialConstructionAppNRC());
									}
									//Added Now
									if(checkFlowtypeEitherIpneOrInr(flowType)) {
										existingComponentDetail.setSiteCountry(nxMpPriceDetail.getNxSiteCountry());
										if(checkForNonBvoipProducts(nxMpDeal)) {
											existingComponentDetail.setSiteId(String.valueOf(nxMpPriceDetail.getNxDesignId()));
										}
										//existingComponentDetail.setSocVersion(nxMpPriceDetail.getSocVersion());
										existingComponentDetail.setEthTokenId(nxMpPriceDetail.getEthTokenId());
										existingComponentDetail.setSocDate(nxMpPriceDetail.getSocVersion());
										existingComponentDetail.setRatePlanId(nxMpPriceDetail.getRatePlanId());
										existingComponentDetail.setExternalRatePlanId(nxMpPriceDetail.getRatePlanIdExternal());
									}
								}
							}
						} else {
						//	currentNxDesignIds.add(nxMpPriceDetail.getNxDesignId());
							if(currentNxDesignIds.containsKey(nxMpPriceDetail.getNxDesignId())) {
								currentNxDesignIds.get(nxMpPriceDetail.getNxDesignId()).add(nxMpPriceDetail.getTerm());
							}else {
								currentNxDesignIds.put(nxMpPriceDetail.getNxDesignId(), new ArrayList<Long>() {{add(nxMpPriceDetail.getTerm());}});
							}
							
							ComponentDetail componentDetail = new ComponentDetail();
							componentDetail.setAsrItemID(nxMpPriceDetail.getAsrItemId());
							componentDetail.setComponentId(nxMpPriceDetail.getNxDesignId());
							componentDetail.setSpecialConstructionNRCCharge(nxMpPriceDetail.getSpecialConstructionAppNRC());
							componentDetail.setTerm(nxMpPriceDetail.getTerm());
							componentDetail.setJurisdiction(nxMpPriceDetail.getJurisdiction());
							//Added Now
							if(checkFlowtypeEitherIpneOrInr(flowType)) {
								componentDetail.setSiteCountry(nxMpPriceDetail.getNxSiteCountry());
								if(checkForNonBvoipProducts(nxMpDeal)) {
									componentDetail.setSiteId(String.valueOf(nxMpPriceDetail.getNxDesignId()));
								}
								//componentDetail.setSocVersion(nxMpPriceDetail.getSocVersion());
								componentDetail.setEthTokenId(nxMpPriceDetail.getEthTokenId());
								componentDetail.setSocDate(nxMpPriceDetail.getSocVersion());
								componentDetail.setRatePlanId(nxMpPriceDetail.getRatePlanId());
								componentDetail.setExternalRatePlanId(nxMpPriceDetail.getRatePlanIdExternal());
							}
							componentDetail.getPriceAttributes().add(createPriceAttribute(nxMpPriceDetail,flowType));
							componentDetails.add(componentDetail);
						}
						if("ASENoD".equalsIgnoreCase(nxMpDeal.getOfferId()))
							usocPrasent.add(nxMpPriceDetail.getBeid().concat(nxMpPriceDetail.getFrequency()));
					//}
	
					}
					
				}
				currentNxDesignIds.clear();
			//}
			if(CollectionUtils.isNotEmpty(allIncPrices)) {
				priceScenario.setAllIncPrices(allIncPrices);
				logger.info("transaction-id : "+nxMpDeal.getTransactionId() +" no of prices :==>> "+priceScenario.getAllIncPrices().size());
			}
			if(null!=allIncpricesTpa) {
				priceScenario.setAllIncPricesTpa(allIncpricesTpa);
			}
			if(CollectionUtils.isNotEmpty(componentDetails)) {
				priceScenario.setComponentDetails(componentDetails);
			}
		//	prices.getPriceScenarios().add(priceScenario);
		}
		prices.getPriceScenarios().add(priceScenario);
		return prices;
	}

	/**
	 * @param nxMpDeal
	 * @return
	 */
	private boolean checkForNonBvoipProducts(NxMpDeal nxMpDeal) {
		return !(Optional.ofNullable(nxMpDeal.getOfferId()).isPresent()
				&& MyPriceConstants.BVOIP_PRODUCT_MAP.containsKey(nxMpDeal.getOfferId()));
	}
	/**
	 * @param flowType
	 * @return
	 */
	private boolean checkFlowtypeEitherIpneOrInr(String flowType) {
		return null != flowType && (flowType.equalsIgnoreCase(StringConstants.FLOW_TYPE_IPnE) ||
						flowType.equalsIgnoreCase(StringConstants.FLOW_TYPE_MP_FIRM) || 
						flowType.equalsIgnoreCase(StringConstants.FLOW_TYPE_INR) ||
						flowType.equalsIgnoreCase(StringConstants.FLOW_TYPE_IGLOO_QUOTE));
	}
	
	private Offer createOfferBlock(NxMpDeal deal) {
		Offer offer = new Offer();
		if ("ASE".equalsIgnoreCase(deal.getOfferId())) {
			offer.setBundleCode("ASE");
			offer.setOfferId(103L);
		}  else if ("ASENoD".equalsIgnoreCase(deal.getOfferId())) {
			offer.setBundleCode("ASENoD");
			offer.setOfferId(6L);
		} else if ("ADE".equalsIgnoreCase(deal.getOfferId())) {
			offer.setBundleCode("ADE");
			offer.setOfferId(120L);
		} else {
			String offerId = getOfferId(deal.getOfferId());
			offer.setBundleCode(offerId);
			String offerIdFromSalesMsTable = nxMpRepositoryService.getOfferIdByOfferName(offerId);
			logger.info("<<<<<<<<<<<<<<<<<<<<<< offer_id >>>>>>>>>>>>>>>>>>>>>> {}",offerIdFromSalesMsTable);
			if(Optional.ofNullable(offerIdFromSalesMsTable).isPresent()) {
				offer.setOfferId(Long.parseLong(offerIdFromSalesMsTable));
			}
		}
		return offer;
	}
	
	private String getOfferId(String offerId) {
		if(MyPriceConstants.OFFER_NAME_MAP.containsKey(offerId)) {
			return MyPriceConstants.OFFER_NAME_MAP.get(offerId);
		}
		return offerId;
	}
	
	private PriceScenario createPriceScenario(NxMpDeal deal) {
		PriceScenario priceScenario = new PriceScenario();
		priceScenario.setPriceScenarioId(deal.getPriceScenarioId());
		priceScenario.setRevisionNumber(Integer.parseInt(deal.getRevision()));
		priceScenario.setDealStatus(deal.getDealStatus());
		priceScenario.setRlDiscountApprovalType(deal.getAutoApproval());
		priceScenario.setRlExpirationDate(deal.getRateLetterExpiresOn());
		priceScenario.setRlQuoteUrl(deal.getQuoteUrl());
		priceScenario.setTransactionId(Long.parseLong(deal.getTransactionId()));
		priceScenario.setRlType(deal.getRlType());
		return priceScenario;
	}
	
	private PriceAttribute createPriceAttribute(NxMpPriceDetails pricDetails, String flowType) {
		PriceAttribute pricAttr = new PriceAttribute();
				
		pricAttr.setBeid(pricDetails.getBeid());
		pricAttr.setFrequency(pricDetails.getFrequency());
		//Added New
		if (null != flowType && (flowType.equalsIgnoreCase(StringConstants.FLOW_TYPE_FMO)
				|| flowType.equalsIgnoreCase(StringConstants.FLOW_TYPE_IPnE)
				|| flowType.equalsIgnoreCase(StringConstants.FLOW_TYPE_MP_FIRM) 
				|| flowType.equalsIgnoreCase(StringConstants.FLOW_TYPE_INR)
				|| flowType.equalsIgnoreCase(StringConstants.FLOW_TYPE_IGLOO_QUOTE))) {
			pricAttr.setIsAccess(pricDetails.getIsAccess());
			pricAttr.setProductRateId(pricDetails.getProdRateId());
			pricAttr.setRdsPriceType(pricDetails.getRdsPriceType());
		}
		if ("MRC".equalsIgnoreCase(pricDetails.getFrequency())) {
			pricAttr.setApprovedDiscount(pricDetails.getApprovedMRCDisc());
			pricAttr.setRequestedDiscount(pricDetails.getRequestedMRCDiscPercentage());
			pricAttr.setApprovedNetRate(pricDetails.getApprovedMRCNetEffectivePrice());
		} else if ("NRC".equalsIgnoreCase(pricDetails.getFrequency())) {
			pricAttr.setApprovedDiscount(pricDetails.getApprovedNRCDisc());
			pricAttr.setRequestedDiscount(pricDetails.getRequestedNRCDiscPercentage());
			pricAttr.setApprovedNetRate(pricDetails.getApprovedNRCNetEffectivePrice());
		}
		return pricAttr;
	}
	
	private List<AllIncPrice> asenodReqRates(String offerId,String transactionId) {
		List<AllIncPrice> allIncPrices = null;
			try {
				AseodReqRatesResponse aseodReqRatesResponse = (AseodReqRatesResponse) aseodReqRatesServiceImpl.aseodReqRates(transactionId);
				if(Optional.ofNullable(aseodReqRatesResponse).isPresent() && CollectionUtils.isNotEmpty(aseodReqRatesResponse.getItems())) {
					logger.info("ASENoD req response for transactionId : "+transactionId+" :==>> "+JacksonUtil.toString(aseodReqRatesResponse.getItems()));
					allIncPrices = new ArrayList<>();
					for(AseodReqRatesLineItem item : aseodReqRatesResponse.getItems()){
						AllIncPrice allIncPrice = new AllIncPrice();
						allIncPrice.setNetRate(item.getPrice());
						allIncPrice.setCosType(item.getCosType());
						allIncPrice.setUsocId(item.getUsoc());
						allIncPrice.setPricingTier(item.getPricingTier());
						allIncPrice.setPortInd(item.getPortInd());
						allIncPrices.add(allIncPrice);
					}
				}
			} catch (SalesBusinessException e) {
				e.printStackTrace();
			}
		return allIncPrices;
	}
	
	
}
