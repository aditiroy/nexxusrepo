/**
 * 
 */
package com.att.sales.nexxus.serviceValidation.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxMpSiteDictionary;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxMpSiteDictionaryRepository;
import com.att.sales.nexxus.serviceValidation.model.ASE;
import com.att.sales.nexxus.serviceValidation.model.ATTCollaborate;
import com.att.sales.nexxus.serviceValidation.model.AdditionalRequestData;
import com.att.sales.nexxus.serviceValidation.model.AddressValidationServiceQualificationRequest;
import com.att.sales.nexxus.serviceValidation.model.AddressValidationServiceQualificationResponse;
import com.att.sales.nexxus.serviceValidation.model.ConfigurationDetails;
import com.att.sales.nexxus.serviceValidation.model.DesignConfiguration;
import com.att.sales.nexxus.serviceValidation.model.Location;
import com.att.sales.nexxus.serviceValidation.model.LocationOptions;
import com.att.sales.nexxus.serviceValidation.model.Locations;
import com.att.sales.nexxus.serviceValidation.model.LocationsWrapper;
import com.att.sales.nexxus.serviceValidation.model.RequestedProducts;
import com.att.sales.nexxus.serviceValidation.model.ServiceValidationRequest;
import com.att.sales.nexxus.serviceValidation.model.SiteDetails;
import com.att.sales.nexxus.serviceValidation.model.SitesServiceUpdateDocuments;
import com.att.sales.nexxus.serviceValidation.model.UnfieldedAddress;
import com.att.sales.nexxus.serviceValidation.model.UpdateTransSitesServiceUpdateAVSQResponse;
import com.att.sales.nexxus.serviceValidation.model.UpdateTransSitesServiceUpdateLocations;
import com.att.sales.nexxus.serviceValidation.model.UpdateTransSitesServiceUpdateLocationsWrapper;
import com.att.sales.nexxus.serviceValidation.model.UpdateTransSitesServiceUpdateRequest;
import com.att.sales.nexxus.serviceValidation.model.UpdateTransSitesServiceUpdateResponse;
import com.att.sales.nexxus.serviceValidation.model.ValidationOptions;
import com.att.sales.nexxus.serviceValidation.model.VoiceOverIPServiceAvailability;
import com.att.sales.nexxus.util.DME2RestClient;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.ThreadMetaDataUtil;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.jayway.jsonpath.TypeRef;


/**
 * @author ShruthiCJ
 *
 */
@Component
public class AVSQUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(AVSQUtil.class);
	
	private static String IPNE = "ipne";
	
	private static final String MP_SITE_SERVICE_UPDATE_FILTER = "MP_SITE_SERVICE_UPDATE_FILTER";

	@Autowired
	private DME2RestClient dme2RestClient;

	@Autowired
	private NxMpSiteDictionaryRepository nxMpSiteDictionaryRepository;

	@Autowired
	private NxMpDealRepository nxMpDealRepository;

	@Autowired
	private EntityManager em;
	
	@Autowired
	private Environment env;

	@Autowired
	private UpdateTransactionSitesServiceUpdate siteServiceUpdate;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;
	
	
	@Autowired
	private JsonPathUtil jsonPathUtil;
	
	private static final String swccliPath="$.AddressValidationServiceQualificationResponse.Location.GISLocationAttributes[0].LocationProperties.swcCLLI";

	
	public void populateASEDefaultValue(ASE ase) {
		ase.setAseIndicator(true);
		AdditionalRequestData aseAdditionalRequestData = new AdditionalRequestData();
		aseAdditionalRequestData.setProductType("SDN");
		aseAdditionalRequestData.setFiberCheckIndicator(true);
		aseAdditionalRequestData.setFiberLitCheckIndicator(true);
		ase.setAdditionalRequestData(aseAdditionalRequestData);
	}

	public void populateValidOptnsDefaultValue(ValidationOptions validationOptions) {
		validationOptions.setLecValidationIndicator(true);
		validationOptions.setMaxAlternativeReturn("5");
		validationOptions.setGlidLookup("3");
		validationOptions.setMaxSupplementalReturn("25");
		validationOptions.setReturnSupplementalsIndicator(true);
	}
	
	public void populateLocation(SiteDetails siteDetails, Location location) {
		LocationOptions locationOptions = new LocationOptions();
		UnfieldedAddress unfieldedAddress = new UnfieldedAddress();
		unfieldedAddress.setAddressLine(siteDetails.getAddressLine());
		unfieldedAddress.setCity(siteDetails.getCity());
		unfieldedAddress.setState(siteDetails.getState());
		unfieldedAddress.setPostalCode(siteDetails.getPostalCode());
		unfieldedAddress.setUnitType(siteDetails.getUnitType());
		unfieldedAddress.setUnitValue(siteDetails.getUnitValue());
		unfieldedAddress.setLevelType(siteDetails.getLevelType());
		unfieldedAddress.setLevelValue(siteDetails.getLevelValue());
		unfieldedAddress.setStructureType(siteDetails.getStructureType());
		unfieldedAddress.setStructureValue(siteDetails.getStructureValue());
		unfieldedAddress.setNxSiteId(siteDetails.getNxSiteId());
		locationOptions.setUnfieldedAddress(unfieldedAddress);
		location.setLocationOptions(locationOptions);
	}

	public void callAVSQ(ServiceValidationRequest serviceValidationRequest, Long nxTxnId, Map<String, Object> paramMap) {
		logger.info("Inside callAVSQ for txnId: " +nxTxnId);
		List<Callable<Object>> callable = new ArrayList<Callable<Object>>();
			List<SiteDetails> siteDetailList = serviceValidationRequest.getSiteDetails();
		String offerName = null;
		boolean ignoreDuplicateCall = true;
		Integer threadSize=12;
		String x = env.getProperty("avsq.threadPool.size");
		if(StringUtils.isNotEmpty(x)) {
			threadSize=Integer.parseInt(x);
		}
		for(SiteDetails siteDetails: siteDetailList) {
			AVSQExecutorService avsqExecutorService = new AVSQExecutorService();
			AddressValidationServiceQualificationRequest avsqRequest = new AddressValidationServiceQualificationRequest();
			RequestedProducts aseProducts = new RequestedProducts();
			boolean aseFound = false;
			List<ConfigurationDetails> configList = siteDetails.getConfigurationDetails();
			
			if (CollectionUtils.isNotEmpty(configList)) {
				ignoreDuplicateCall = true;
				for (ConfigurationDetails config : configList) {
					offerName = config.getModelName();
					if ((ignoreDuplicateCall && null != config.getModelName()
							&& (config.getModelName().equalsIgnoreCase(MyPriceConstants.ASE_OFFER_NAME)
									|| config.getModelName().equalsIgnoreCase(MyPriceConstants.ASEoD_OFFER_NAME)))
							&& (IPNE.equalsIgnoreCase(siteDetails.getSiteInfoSource())
									|| null == siteDetails.getSiteInfoSource())) {
						ignoreDuplicateCall = false;
						Location location = new Location();
						populateLocation(siteDetails, location);
						ValidationOptions validationOptions = new ValidationOptions();
						populateValidOptnsDefaultValue(validationOptions);
						location.setValidationOptions(validationOptions);
						avsqRequest.setLocation(location);
						ASE ase = new ASE();
						populateASEDefaultValue(ase);
						ase.getAdditionalRequestData().setInterOfficeFacilitySWCCLLI(siteDetails.getClli());
						aseProducts.setAse(ase);
						aseFound = true;
						avsqRequest.getLocation().getLocationOptions().getUnfieldedAddress().setDocumentNumber(config.getDocumentNumber());
						avsqRequest.setQualConversationId("nexxusQual-"+siteDetails.getNxSiteId()+"-"+config.getModelName()+"-"+config.getDocumentNumber()+"-"+ new Date().getTime());
						avsqRequest.setModelName(offerName);
					} else if (null != config.getModelName()
							&& (config.getModelName().equalsIgnoreCase(MyPriceConstants.ADE_OFFER_NAME))
							&& (IPNE.equalsIgnoreCase(siteDetails.getSiteInfoSource())
									|| null == siteDetails.getSiteInfoSource())) {
						List<DesignConfiguration> designList = config.getDesignConfiguration();
						
							AddressValidationServiceQualificationRequest adeRequest = new AddressValidationServiceQualificationRequest();
							Location location = new Location();
							populateLocation(siteDetails, location);
							ValidationOptions validationOptions = new ValidationOptions();
							populateValidOptnsDefaultValue(validationOptions);
							location.setValidationOptions(validationOptions);
							adeRequest.setLocation(location);
							AVSQExecutorService adeExecutorService = new AVSQExecutorService();
							RequestedProducts adeProducts = new RequestedProducts();
							ASE ase = new ASE();
							populateASEDefaultValue(ase);
							if (CollectionUtils.isNotEmpty(designList)) {
								config.getDesignConfiguration().forEach(design -> {
									if (design.getName().equalsIgnoreCase(MyPriceConstants.ADE_CLLI_VALUE)) {
										ase.getAdditionalRequestData().setInterOfficeFacilitySWCCLLI(design.getValue());
									}
								});
							}							
							adeProducts.setAse(ase);
							adeRequest.setRequestedProducts(adeProducts);
							adeRequest.getLocation().getLocationOptions().getUnfieldedAddress().setDocumentNumber(config.getDocumentNumber());
							adeRequest.setQualConversationId("nexxusQual-"+siteDetails.getNxSiteId()+"-"+config.getModelName()+"-"+config.getDocumentNumber()+"-"+ new Date().getTime());
							adeRequest.setModelName(offerName);
							adeExecutorService.setAddressValidationServiceQualificationRequest(adeRequest);
							adeExecutorService.setDme2RestClient(dme2RestClient);
							adeExecutorService.setParamMap(paramMap);
							callable.add(adeExecutorService);
						
					}else if(null != config.getModelName() && (config.getModelName().equalsIgnoreCase(MyPriceConstants.EPLS_WAN_OFFER_NAME)
							|| null != config.getModelName() && (config.getModelName().equalsIgnoreCase(MyPriceConstants.ADSL)
							|| null != config.getModelName() && (config.getModelName().equalsIgnoreCase(MyPriceConstants.AVPN)))
							|| null != config.getModelName() && (config.getModelName().equalsIgnoreCase(MyPriceConstants.ANIRA))
							|| null != config.getModelName() && (config.getModelName().equalsIgnoreCase(MyPriceConstants.ADI))
							|| null != config.getModelName() && config.getModelName().equalsIgnoreCase(MyPriceConstants.OPTEWAN))
							&& (IPNE.equalsIgnoreCase(siteDetails.getSiteInfoSource()) || null == siteDetails.getSiteInfoSource())){
						logger.info("Inside ESLPS request Preaparation");
						AddressValidationServiceQualificationRequest addressvalRequest = new AddressValidationServiceQualificationRequest();
						Location location = new Location();
						populateLocation(siteDetails, location);
						addressvalRequest.setLocation(location);
						AVSQExecutorService adeExecutorService = new AVSQExecutorService();
						RequestedProducts requestedProducts = new RequestedProducts();
						if(config.getModelName().equalsIgnoreCase(MyPriceConstants.EPLS_WAN_OFFER_NAME)) 
						{
							requestedProducts.setEplsWANIndicator(true);
						}else if(config.getModelName().equalsIgnoreCase(MyPriceConstants.ADSL)){
							requestedProducts.setAttDSLIndicator(true);
						}else if(config.getModelName().equalsIgnoreCase(MyPriceConstants.AVPN))
						{
							requestedProducts.setAvpnIndicator(true);
						}else if(config.getModelName().equalsIgnoreCase(MyPriceConstants.ANIRA))
						{
							requestedProducts.setAniraIndicator(true);
						}
						else if(config.getModelName().equalsIgnoreCase(MyPriceConstants.ADI))
						{
							requestedProducts.setAdiIndicator(true);
						} 
						else if(config.getModelName().equalsIgnoreCase(MyPriceConstants.OPTEWAN))
						{
							requestedProducts.setOpticalEthernetWANIndicator(true);
						}
						addressvalRequest.setRequestedProducts(requestedProducts);
						addressvalRequest.getLocation().getLocationOptions().getUnfieldedAddress().setDocumentNumber(config.getDocumentNumber());
						addressvalRequest.setQualConversationId("nexxusQual-"+siteDetails.getNxSiteId()+"-"+config.getModelName()+"-"+config.getDocumentNumber()+"-"+ new Date().getTime());
						addressvalRequest.setModelName(offerName);
						adeExecutorService.setAddressValidationServiceQualificationRequest(addressvalRequest);
						adeExecutorService.setDme2RestClient(dme2RestClient);
						adeExecutorService.setParamMap(paramMap);
						callable.add(adeExecutorService);
					
					}
					else if(null != config.getModelName() && (config.getModelName().equalsIgnoreCase(MyPriceConstants.BVoIP))
							&& (IPNE.equalsIgnoreCase(siteDetails.getSiteInfoSource()) || null == siteDetails.getSiteInfoSource()))
							{
						AddressValidationServiceQualificationRequest bVoipRequest = new AddressValidationServiceQualificationRequest();
						Location location = new Location();
						populateLocation(siteDetails, location);
						bVoipRequest.setLocation(location);
						AVSQExecutorService adeExecutorService = new AVSQExecutorService();
						RequestedProducts requestedProducts = new RequestedProducts();
						requestedProducts.setBvoipIndicator(true);
						requestedProducts.setPFlexLocalIndicator(false);
						requestedProducts.setIpFlexLongDistanceIndicator(true);
						requestedProducts.setIpFlexTollFreeIndicator(true);
						requestedProducts.setVdnaIndicator(false);
						VoiceOverIPServiceAvailability ipServiceAvailibilty = new VoiceOverIPServiceAvailability();
						populateipServiceAvailibiltyValues(ipServiceAvailibilty);
						requestedProducts.setVoiceOverIPServiceAvailability(ipServiceAvailibilty);
						bVoipRequest.setRequestedProducts(requestedProducts);
						bVoipRequest.getLocation().getLocationOptions().getUnfieldedAddress().setDocumentNumber(config.getDocumentNumber());
						bVoipRequest.setQualConversationId("nexxusQual-"+siteDetails.getNxSiteId()+"-"+config.getModelName()+"-"+config.getDocumentNumber()+"-"+ new Date().getTime());
						bVoipRequest.setModelName(offerName);
						adeExecutorService.setAddressValidationServiceQualificationRequest(bVoipRequest);
						adeExecutorService.setDme2RestClient(dme2RestClient);
						adeExecutorService.setParamMap(paramMap);
						callable.add(adeExecutorService);
							}
					else if(null != config.getModelName() && (config.getModelName().equalsIgnoreCase(MyPriceConstants.ATTCollaborate))
							&& (IPNE.equalsIgnoreCase(siteDetails.getSiteInfoSource()) || null == siteDetails.getSiteInfoSource()))
					{
						AddressValidationServiceQualificationRequest attCollaborateRequest = new AddressValidationServiceQualificationRequest();
						Location location = new Location();
						populateLocation(siteDetails, location);
						attCollaborateRequest.setLocation(location);
						AVSQExecutorService adeExecutorService = new AVSQExecutorService();
						RequestedProducts requestedProducts = new RequestedProducts();
						ATTCollaborate attCollaborate = new ATTCollaborate();
						attCollaborate.setAttCollaborateIndicator(true);
						requestedProducts.setAttCollaborate(attCollaborate);
						attCollaborateRequest.setRequestedProducts(requestedProducts);
						attCollaborateRequest.getLocation().getLocationOptions().getUnfieldedAddress().setDocumentNumber(config.getDocumentNumber());
						attCollaborateRequest.setQualConversationId("nexxusQual-"+siteDetails.getNxSiteId()+"-"+config.getModelName()+"-"+config.getDocumentNumber()+"-"+ new Date().getTime());
						attCollaborateRequest.setModelName(offerName);
						adeExecutorService.setAddressValidationServiceQualificationRequest(attCollaborateRequest);
						adeExecutorService.setDme2RestClient(dme2RestClient);
						adeExecutorService.setParamMap(paramMap);
						callable.add(adeExecutorService);
					} else if (ignoreDuplicateCall) {
						AddressValidationServiceQualificationRequest request = new AddressValidationServiceQualificationRequest();
						Location location = new Location();
						populateLocation(siteDetails, location);
						request.setLocation(location);
						request.getLocation().getLocationOptions().getUnfieldedAddress().setDocumentNumber(config.getDocumentNumber());
						request.setQualConversationId("nexxusQual-"+siteDetails.getNxSiteId()+"-"+config.getModelName()+"-"+config.getDocumentNumber()+"-"+ new Date().getTime());
						AVSQExecutorService ipneExecutorService = new AVSQExecutorService();
						ipneExecutorService.setAddressValidationServiceQualificationRequest(request);
						ipneExecutorService.setDme2RestClient(dme2RestClient);
						ipneExecutorService.setParamMap(paramMap);
						ipneExecutorService.setSiteDetails(siteDetails);
						callable.add(ipneExecutorService);
					}
				}
			}
			if (aseFound) {
				avsqRequest.setRequestedProducts(aseProducts);
				avsqExecutorService.setAddressValidationServiceQualificationRequest(avsqRequest);
				avsqExecutorService.setDme2RestClient(dme2RestClient);
				avsqExecutorService.setParamMap(paramMap);
				callable.add(avsqExecutorService);
			}

		}
		// call executor
		ExecutorService executor = Executors.newFixedThreadPool(threadSize);
		try {
			List<Future<Object>> response = executor.invokeAll(callable);
			

			ObjectMapper thisMapper = new ObjectMapper();
			thisMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
			thisMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			ObjectMapper prodMapper = new ObjectMapper();
			prodMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
			prodMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			prodMapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
			prodMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
			String siteJson = null;
			try {
				if (null != response && null != nxTxnId) {
					NxMpDeal nxMpDeal = nxMpDealRepository
							.findByTransactionId(String.valueOf(serviceValidationRequest.getTransactionId()));
					NxMpSiteDictionary nxMpSiteDictionary = nxMpSiteDictionaryRepository
							.findByNxTxnId(nxMpDeal.getNxTxnId());
					siteJson = nxMpSiteDictionary.getSiteJson();
					if (null != siteJson) {
							LocationsWrapper locations = thisMapper.readValue(siteJson, LocationsWrapper.class);
							
							Map<String, List<String>> currentLocationDatas = new HashMap<>();
							for(Locations location : locations.getLocations()) {
								if (currentLocationDatas.containsKey(location.getNxSiteId())) {
									currentLocationDatas.get(location.getNxSiteId()).add(location.getDocumentNumber());
								} else {
									currentLocationDatas.put(location.getNxSiteId(), new ArrayList<String>(Arrays.asList(location.getDocumentNumber())));
								}
							}
							
							ArrayList<Locations> adeLocations = new ArrayList<Locations>();
							nextResult:
								for (int i = 0; i < response.size(); i++) {
									Future<Object> res = response.get(i);
									AVSQExecutorService avsqExecutorService = (AVSQExecutorService) callable.get(i);
									if (avsqExecutorService.getSiteDetails() != null) {
										continue;
									}
									String nxSiteId = avsqExecutorService.getAddressValidationServiceQualificationRequest().getLocation().getLocationOptions().getUnfieldedAddress().getNxSiteId();
									String docmentNumber = avsqExecutorService.getAddressValidationServiceQualificationRequest().getLocation().getLocationOptions().getUnfieldedAddress().getDocumentNumber();
									String qualConversationId = avsqExecutorService.getAddressValidationServiceQualificationRequest().getQualConversationId();
									String respString = (null != res && null != res.get()) ? (String) res.get() : "";
									String modelName = avsqExecutorService.getAddressValidationServiceQualificationRequest().getModelName();
									if (null != respString && respString.contains(("QualifiedProducts"))) {
										AddressValidationServiceQualificationResponse avsqResponse = prodMapper
												.readValue(respString, AddressValidationServiceQualificationResponse.class);
										logger.info("Service validation : Multiple design configuration per site {}, {}, {}, {}", nxTxnId, modelName, docmentNumber, nxSiteId);
										for(Locations location : locations.getLocations()) {
											//logger.info("Service validation : current location {}, {}", location.getDocumentNumber(), location.getNxSiteId());
											if (location.getNxSiteId().equals(nxSiteId)) {
												if ((location.getDocumentNumber() == null || (docmentNumber.equals(location.getDocumentNumber())))) {									
													logger.info("ReSPONSE Processesing  for EPLS_WAN_OFFER_NAME OR ADSL :" + modelName);
													location.setDocumentNumber(docmentNumber);
													location.setQualConversationId(qualConversationId);
													location.setModelName(modelName);
													location.getAvsqResponse().setQualifiedProducts(avsqResponse.getQualifiedProducts());
													location.getAvsqResponse().setLocation(avsqResponse.getLocation());
													continue nextResult;
												} else if ((location.getDocumentNumber() != null && !(location.getDocumentNumber().equals(docmentNumber)))){
													if(currentLocationDatas.isEmpty() ? true : currentLocationDatas.containsKey(nxSiteId) ? !currentLocationDatas.get(nxSiteId).contains(docmentNumber) : true) {
														Locations adeLocation = new Locations();					 
														adeLocation.setId(location.getId()); 
														adeLocation.setGlobalLocationId(location.getGlobalLocationId()); 
														adeLocation.setNxSiteId(location.getNxSiteId()); 
														adeLocation.setLocName(location.getLocName()); 
														adeLocation.setStreet(location.getStreet()); 
														adeLocation.setCity(location.getCity()); 
														adeLocation.setState(location.getState()); 
														adeLocation.setZip(location.getZip()); 
														adeLocation.setCountry(location.getCountry()); 
														adeLocation.setDocumentNumber(docmentNumber); 
														adeLocation.setValidationStatus(location.getValidationStatus()); 
														adeLocation.setQualConversationId(qualConversationId); 
														adeLocation.setModelName(modelName); 
														AddressValidationServiceQualificationResponse adeAvsqResponse = new AddressValidationServiceQualificationResponse(); 
														adeAvsqResponse.setLocation(location.getAvsqResponse().getLocation()); 
														adeAvsqResponse.setQualifiedProducts(avsqResponse.getQualifiedProducts()); 
														adeLocation.setAvsqResponse(adeAvsqResponse); 
														adeLocations.add(adeLocation);
														continue nextResult;
														
														
													}
												}
											}
										}
									} else {
										logger.info("callAVSQ : QualifiedProducts Got error in AVSQ response");
										for(Locations location : locations.getLocations()) {
											if (location.getNxSiteId().equals(nxSiteId)) {
												if ((location.getDocumentNumber() == null || (docmentNumber.equals(location.getDocumentNumber())))) {
													location.setDocumentNumber(docmentNumber);
													location.setQualConversationId(qualConversationId);
													location.setModelName(modelName);
													continue nextResult;
												} else if ((location.getDocumentNumber() != null && !(location.getDocumentNumber().equals(docmentNumber)))){
													if(currentLocationDatas.isEmpty() ? true : currentLocationDatas.containsKey(nxSiteId) ? !currentLocationDatas.get(nxSiteId).contains(docmentNumber) : true) {
														Locations adeLocation = new Locations();
														BeanUtils.copyProperties(location, adeLocation);
														adeLocation.setDocumentNumber(docmentNumber);
														adeLocation.setQualConversationId(qualConversationId);
														location.setModelName(modelName);
														adeLocations.add(adeLocation);
														continue nextResult;
													}
												}
											}
										}
									}
								}
							if(CollectionUtils.isNotEmpty(adeLocations)) {
								locations.getLocations().addAll(adeLocations);
							}
							
							// changes in AVSQ call after we receive response for ASE and ASEoD to store multiple location block if configurationDetails is having multiple documentNumber.
							ArrayList<Locations> aseLocations = new ArrayList<Locations>();
							for(SiteDetails siteDetails: siteDetailList) {
								List<ConfigurationDetails> configList = siteDetails.getConfigurationDetails();
								if (CollectionUtils.isNotEmpty(configList)) {
									for (ConfigurationDetails config : configList) {
										if (null != config.getModelName() && (MyPriceConstants.ASE_OFFER_NAME.equalsIgnoreCase(config.getModelName())
												|| MyPriceConstants.ASEoD_OFFER_NAME.equalsIgnoreCase(config.getModelName()))) {
											boolean isDocNumExist = false;
											for(Locations location : locations.getLocations()) {
												if (location.getNxSiteId().equals(siteDetails.getNxSiteId())
														&& (location.getDocumentNumber() != null && (location.getDocumentNumber().equals(config.getDocumentNumber())))
														&& location.getAvsqResponse() != null){
													isDocNumExist = true;
													break;
												}
											}
											if(!isDocNumExist) {
												Locations existingLocation = locations.getLocations().stream().filter(loc -> (loc.getNxSiteId().equals(siteDetails.getNxSiteId()) && config.getModelName().equalsIgnoreCase(loc.getModelName()))).findFirst().orElse(null);
												if(null != existingLocation) {
													Locations aseLocation = new Locations();
													BeanUtils.copyProperties(existingLocation, aseLocation);
													aseLocation.setDocumentNumber(config.getDocumentNumber());
													aseLocations.add(aseLocation);
												}
											}
										}
									}
								}
							}
							if(CollectionUtils.isNotEmpty(aseLocations)) {
								locations.getLocations().addAll(aseLocations);
							}
							SimpleFilterProvider filterProvider = new SimpleFilterProvider();
							filterProvider.addFilter("avsqFilter",
									SimpleBeanPropertyFilter.filterOutAllExcept("Location", "QualifiedProducts"));
							filterProvider.addFilter("avsqLocationFilter",
									SimpleBeanPropertyFilter.filterOutAllExcept("GISLocationAttributes", "LocationNetworkAttributes"));
							thisMapper.setFilterProvider(filterProvider);
							thisMapper.setSerializationInclusion(Include.NON_NULL);
							thisMapper.setSerializationInclusion(Include.NON_EMPTY);
							
							nxMpSiteDictionary.setSiteJson(thisMapper.writeValueAsString(locations));
							nxMpSiteDictionaryRepository.save(nxMpSiteDictionary);
							String requestPayLoad = thisMapper.writeValueAsString(locations);
							logger.info("The request is  {}", requestPayLoad);
					}
				}
				for (int i = 0; i < response.size(); i++) {
					Future<Object> res = response.get(i);
					AVSQExecutorService avsqExecutorService = (AVSQExecutorService) callable.get(i);
					if (avsqExecutorService.getSiteDetails() == null) {
						continue;
					}
					String nxSiteId = avsqExecutorService.getAddressValidationServiceQualificationRequest().getLocation().getLocationOptions().getUnfieldedAddress().getNxSiteId();
					String docmentNumber = avsqExecutorService.getAddressValidationServiceQualificationRequest().getLocation().getLocationOptions().getUnfieldedAddress().getDocumentNumber();
					String qualConversationId = avsqExecutorService.getAddressValidationServiceQualificationRequest().getQualConversationId();
					String respString = (null != res && null != res.get()) ? (String) res.get() : "{\"AddressValidationServiceQualificationResponse\": {}}";
					String modelName = avsqExecutorService.getAddressValidationServiceQualificationRequest().getModelName();
					AddressValidationServiceQualificationResponse avsqResponse = prodMapper.readValue(respString,
							AddressValidationServiceQualificationResponse.class);
					logger.info("Service validation : Multiple design configuration per site {}, {}, {}, {}", nxTxnId,
							modelName, docmentNumber, nxSiteId);
					for (ConfigurationDetails cfg : avsqExecutorService.getSiteDetails().getConfigurationDetails()) {
						if (cfg.getDocumentNumber() == null || docmentNumber.equals(cfg.getDocumentNumber())) {
							cfg.setDocumentNumber(docmentNumber);
							cfg.setQualConversationId(qualConversationId);
							cfg.setModelName(modelName);
							cfg.setAvsqResponse(avsqResponse);
						}
					}
				}
			} catch (IOException | ExecutionException e) {
				logger.error("Exception from callAVSQ while processing json {}", e);
				Thread.currentThread().interrupt();
			} catch (InterruptedException e) {
				logger.error("Exception from callAVSQ {}", e);
				Thread.currentThread().interrupt();
			} finally {
				executor.shutdown();
				logger.error("shutdown finished");
			}
			logger.info("callAVSQ : Thread shutdown");

			try {
				logger.info("Start : processSiteServiceUpdate");
				processSiteServiceUpdate(nxTxnId, serviceValidationRequest.getTransactionId(),
						serviceValidationRequest, paramMap);
			} catch (SalesBusinessException e) {
				logger.error("Exception from processSiteServiceUpdate call {}", e);
			}

		} catch (InterruptedException e) {
			logger.error("Exception from callAVSQ {}", e);
			Thread.currentThread().interrupt();
		}
	}

	private void populateipServiceAvailibiltyValues(VoiceOverIPServiceAvailability ipServiceAvailibilty) {
		ipServiceAvailibilty.setRateCenter("OKLD MN-PD");
		ipServiceAvailibilty.setRateCenterState("CA");
		ipServiceAvailibilty.setNumberAvailabilityIndicator(true);
		ipServiceAvailibilty.setLnsSwitchCLLI("SNFCCA21GT5");
		ipServiceAvailibilty.setNumberAvailabilityIndicator(true);
		ipServiceAvailibilty.setVoipAvailabilityFlag("N");
		ipServiceAvailibilty.setVoipE911AvailabilityFlag("N");
		ipServiceAvailibilty.setVoipLocalAvailabilityFlag("N");
		ipServiceAvailibilty.setE911AvailabilityFlag("N");
		ipServiceAvailibilty.setTaxGeoCode("0105001235000");
		ipServiceAvailibilty.setIpTollFreeIndicator(true);
	}
	
	public NxMpSiteDictionary populateSiteJsonforQualificationUsingJsonNode(Long nxTxnId, String ipeResponse,NxMpDeal cloneNxMpDeal) {
		logger.info("Start populateSiteJsonforQualificationUsingJsonNode");
		ObjectMapper thisMapper = new ObjectMapper();
		NxMpSiteDictionary nxMpSiteDictionary = null;
		try {
			HashMap<String, String> locationMap = null;
			if (Optional.ofNullable(cloneNxMpDeal).isPresent() && Optional.ofNullable(cloneNxMpDeal.getSourceId()).isPresent()) {
				locationMap = new HashMap<>();
				List<NxMpDeal> nxMpDeals = nxMpDealRepository.findAllByTransactionId(cloneNxMpDeal.getSourceId());
				if(CollectionUtils.isNotEmpty(nxMpDeals)) {
					//logger.info("Clone transaction-id :==>> {} ", org.apache.commons.lang3.StringUtils.normalizeSpace(cloneNxMpDeal.getSourceId()));
					for(NxMpDeal nxMpDeal : nxMpDeals) {
						NxMpSiteDictionary siteDictionary = nxMpSiteDictionaryRepository.findByNxTxnId(nxMpDeal.getNxTxnId());
						if(Optional.ofNullable(siteDictionary).isPresent()) {
							logger.info("Clone transaction siteJson :==>> {} ", siteDictionary.getSiteJson());
							if(siteDictionary.getSiteJson() != null) {
								JSONObject jsonObject = new JSONObject(siteDictionary.getSiteJson());
								JSONArray locationsJsonArray = jsonObject.getJSONArray("locations");
								if (null != locationsJsonArray && locationsJsonArray.length() > 0) {
									logger.info("Clone transaction site locations size :==>> {} ", locationsJsonArray.length());
									for (int index = 0; index < locationsJsonArray.length(); index++) {
										JSONObject locationsJsonObject = (JSONObject) locationsJsonArray.get(index);
										if (null != locationsJsonObject) {
											String nxSiteId = null;
											if (!locationsJsonObject.isNull("nxSiteId")) {
												nxSiteId = locationsJsonObject.optString("nxSiteId");
											}
											String locName = null;
											if (!locationsJsonObject.isNull("locName")) {
												locName = locationsJsonObject.getString("locName");
											}
											if(Optional.ofNullable(nxSiteId).isPresent() && Optional.ofNullable(locName).isPresent()) {
												locationMap.put(locName, nxSiteId);
											}
										}
									}
								}
							}
						}
					}
				}
			}
			JsonNode ipeLocations = thisMapper.readTree(ipeResponse);
			for (JsonNode location : ipeLocations.path("locations")) {
				String nxSiteId = null;
				if(null != locationMap && locationMap.containsKey(location.path("locName").asText())) {
					nxSiteId = locationMap.get(location.path("locName").asText());
					logger.info("Key exist in map :: site id :==>> {} ", nxSiteId);
				} else {
					nxSiteId = String.valueOf(getNxSiteId());
					logger.info("Key not exist in map :: Location name :==>> {} ", location.path("locName").asText());
				}
				((ObjectNode) location).put("nxSiteId", nxSiteId);
				((ObjectNode) location).put("siteInfoSource", "ipne");
				String avsq = location.path("addressValidationServiceQualificationResponse").asText();
				JsonNode avsqNode = thisMapper.readTree(avsq);
				((ObjectNode) location).set("avsqResponse", avsqNode);
				((ObjectNode) location).remove("addressValidationServiceQualificationResponse");
			}
			String locationJson = ipeLocations.toString();
			logger.info("Location json from IP&E MS ::{}", locationJson);
			
			// save to site dictionary table
			nxMpSiteDictionary = nxMpSiteDictionaryRepository.findByNxTxnId(nxTxnId);
			if (null != nxMpSiteDictionary) {
				logger.info("Updating the existing site json for the nxtxnId ::{}",  nxTxnId);
				if(nxMpSiteDictionary.getSiteJson() != null) {
					JsonNode locationList = thisMapper.readTree(nxMpSiteDictionary.getSiteJson());
					Map<Long, List<JsonNode>> locationsMap = new HashMap<>();
					if (null != locationList) {
						for (JsonNode location : locationList.path("locations")) {
							if (locationsMap.containsKey(location.path("id").asLong())) {
								locationsMap.get(location.path("id").asLong()).add(location);
							} else {
								locationsMap.put(location.path("id").asLong(), new ArrayList<>(Arrays.asList(location)));
							}
						}
					}
					ObjectNode newLocations = thisMapper.createObjectNode();
					List<JsonNode> locationsModifiedList = new ArrayList<>();
					if (null != locationsMap && !locationsMap.isEmpty()) {
						if (!ipeLocations.path("locations").isMissingNode() && !ipeLocations.path("locations").isNull()) {
							newLocations.set("status", ipeLocations.get("status"));
							for (JsonNode location : ipeLocations.path("locations")) {
								if (locationsMap.containsKey(location.path("id").asLong())) {
									List<JsonNode> existingLocations = locationsMap.get(location.path("id").asLong());
									for (JsonNode eLocation : existingLocations) {
										((ObjectNode) location).set("nxSiteId", eLocation.get("nxSiteId"));
										if (null != eLocation.get("documentNumber")) {
											((ObjectNode) location).set("documentNumber", eLocation.get("documentNumber"));
										}
										if (!eLocation.path("avsqResponse").path("QualifiedProducts").isMissingNode() && !eLocation.path("avsqResponse").path("QualifiedProducts").isNull()) {
											((ObjectNode) location).set("avsqResponse", eLocation.get("avsqResponse"));
										}
										locationsModifiedList.add(location);
									}
									locationsMap.remove(location.path("id").asLong());
								} else {
									locationsModifiedList.add(location);
								}
							}
						}
					} else if (!ipeLocations.path("locations").isMissingNode() && !ipeLocations.path("locations").isNull()) {
						for (JsonNode location : ipeLocations.path("locations")) {
							locationsModifiedList.add(location);
						}
					}
					if (null != locationsMap && !locationsMap.isEmpty()) {
						locationsMap.values().forEach(nodeList -> locationsModifiedList.addAll(nodeList));
					}
					if (null != locationsModifiedList) {
						locationsModifiedList.forEach(node -> newLocations.withArray("locations").add(node));
					}
					nxMpSiteDictionary.setSiteJson(newLocations.toString());
					nxMpSiteDictionaryRepository.save(nxMpSiteDictionary);
				} else {
					nxMpSiteDictionary.setSiteJson(locationJson);
					nxMpSiteDictionaryRepository.save(nxMpSiteDictionary);
				}
			} else {
				logger.info("Creating new site json for the nxtxnId ::{}" + nxTxnId);
				nxMpSiteDictionary = new NxMpSiteDictionary();
				nxMpSiteDictionary.setNxTxnId(nxTxnId);
				nxMpSiteDictionary.setSourceSystem(MyPriceConstants.SOURCE_SYSTEM);
				nxMpSiteDictionary.setSiteJson(locationJson);
				nxMpSiteDictionary.setActiveYN(CommonConstants.ACTIVE_Y);
				nxMpSiteDictionaryRepository.save(nxMpSiteDictionary);
			}
		} catch (Exception e) {
			logger.info("Location Json processing for IP&E response is failed for the nxTxnId {}", nxTxnId);
			logger.info("Exception", e);
		}
		return nxMpSiteDictionary;
	}

	public NxMpSiteDictionary populateSiteJsonforQualification(Long nxTxnId, String ipeResponse,NxMpDeal cloneNxMpDeal) {
		logger.info("Start populateSiteJsonforQualification");
		ObjectMapper thisMapper = new ObjectMapper();
		thisMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
		thisMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		SimpleFilterProvider filterProvider = new SimpleFilterProvider();
		filterProvider.addFilter("avsqFilter", SimpleBeanPropertyFilter.filterOutAllExcept("Location"));
		filterProvider.addFilter("avsqLocationFilter",
				SimpleBeanPropertyFilter.filterOutAllExcept("GISLocationAttributes", "SAGLocationAttributes"));
		thisMapper.setFilterProvider(filterProvider);
		NxMpSiteDictionary nxMpSiteDictionary = null;
		try {
			HashMap<String, String> locationMap = null;
			if(Optional.ofNullable(cloneNxMpDeal).isPresent() && Optional.ofNullable(cloneNxMpDeal.getSourceId()).isPresent()) {
				locationMap = new HashMap<>();
				List<NxMpDeal> nxMpDeals = nxMpDealRepository.findAllByTransactionId(cloneNxMpDeal.getSourceId());
				if(CollectionUtils.isNotEmpty(nxMpDeals)) {
					logger.info("Clone transaction-id :==>> {} ", cloneNxMpDeal.getSourceId());
					for(NxMpDeal nxMpDeal : nxMpDeals) {
						NxMpSiteDictionary siteDictionary = nxMpSiteDictionaryRepository.findByNxTxnId(nxMpDeal.getNxTxnId());
						if(Optional.ofNullable(siteDictionary).isPresent()) {
							logger.info("Clone transaction siteJson :==>> {} ", siteDictionary.getSiteJson());
							if(siteDictionary.getSiteJson() != null) {
								JSONObject jsonObject = new JSONObject(siteDictionary.getSiteJson());
								JSONArray locationsJsonArray = jsonObject.getJSONArray("locations");
								if (null != locationsJsonArray && locationsJsonArray.length() > 0) {
									logger.info("Clone transaction site locations size :==>> {} ", locationsJsonArray.length());
									for (int index = 0; index < locationsJsonArray.length(); index++) {
										JSONObject locationsJsonObject = (JSONObject) locationsJsonArray.get(index);
										if (null != locationsJsonObject) {
											String nxSiteId = null;
											if (!locationsJsonObject.isNull("nxSiteId")) {
												nxSiteId = locationsJsonObject.optString("nxSiteId");
											}
											String locName = null;
											if (!locationsJsonObject.isNull("locName")) {
												locName = locationsJsonObject.getString("locName");
											}
											if(Optional.ofNullable(nxSiteId).isPresent() && Optional.ofNullable(locName).isPresent()) {
												locationMap.put(locName, nxSiteId);
											}
										}
									}
								}
							}
						}
					}
				}
				
			}
			LocationsWrapper ipeLocations = thisMapper.readValue(ipeResponse, LocationsWrapper.class);
			for(Locations location : ipeLocations.getLocations()) {
				String nxSiteId = null;
				if(null != locationMap && locationMap.containsKey(location.getLocName())) {
					nxSiteId = locationMap.get(location.getLocName());
					logger.info("Key exist in map :: site id :==>> {} ", nxSiteId);
				}else {
					nxSiteId = String.valueOf(getNxSiteId());
					logger.info("Key not exist in map :: Location name :==>> {} ", location.getLocName());
				}
				location.setNxSiteId(nxSiteId);
				location.setSiteInfoSource("ipne");
				String avsq = location.getAddressValidationServiceQualificationResponse();
				try {
					location.setAvsqResponse(
							thisMapper.readValue(avsq, AddressValidationServiceQualificationResponse.class));
					location.setAddressValidationServiceQualificationResponse(null);
				} catch (IOException e) {
					logger.info("AVSQ Json processing for IP&E response is failed for the location id {}",
							location.getId());
					logger.info("Exception", e);
				}
			}
			thisMapper.setSerializationInclusion(Include.NON_NULL);
			thisMapper.setSerializationInclusion(Include.NON_EMPTY);
			String locationJson = thisMapper.writeValueAsString(ipeLocations);
			logger.info("Location json from IP&E MS ::{}", locationJson);

			// save to site dictionary table
			nxMpSiteDictionary = nxMpSiteDictionaryRepository.findByNxTxnId(nxTxnId);
			if (null != nxMpSiteDictionary) {
				logger.info("Updating the existing site json for the nxtxnId ::{}",  nxTxnId);
				if(nxMpSiteDictionary.getSiteJson() != null) {
					
					ObjectMapper mapper = new ObjectMapper();
					SimpleFilterProvider qualiFilter = new SimpleFilterProvider();
					qualiFilter.addFilter("avsqFilter",
							SimpleBeanPropertyFilter.filterOutAllExcept("Location", "QualifiedProducts"));
					qualiFilter.addFilter("avsqLocationFilter",
							SimpleBeanPropertyFilter.filterOutAllExcept("GISLocationAttributes"));
					mapper.setFilterProvider(qualiFilter);

					LocationsWrapper locationList = mapper.readValue(nxMpSiteDictionary.getSiteJson(),
							LocationsWrapper.class);
					Map<Long, List<Locations>> locationsMap = new HashMap<>();
					if (null != locationList) {
						for (Locations location : locationList.getLocations()) {
							if (locationsMap.containsKey(location.getId())) {
								locationsMap.get(location.getId()).add(location);
							} else {
								locationsMap.put(location.getId(), new ArrayList<Locations>(Arrays.asList(location)));
							}
						}
					}
					LocationsWrapper newLocations = new LocationsWrapper();
					List<Locations> locationsModifiedList = new ArrayList<>();
					if (null != locationsMap && !locationsMap.isEmpty()) {
						if (null != ipeLocations && Optional.ofNullable(ipeLocations.getLocations()).isPresent()) {
							newLocations.setStatus(ipeLocations.getStatus());
							for (Locations location : ipeLocations.getLocations()) {
								if (locationsMap.containsKey(location.getId())) {
									List<Locations> existingLocations = locationsMap.get(location.getId());
									for (Locations eLocation : existingLocations) {
										location.setNxSiteId(eLocation.getNxSiteId());
										if (null != eLocation.getDocumentNumber()) {
											location.setDocumentNumber(eLocation.getDocumentNumber());
										}
										if (null != eLocation.getAvsqResponse()
												&& null != eLocation.getAvsqResponse().getQualifiedProducts()) {
											AddressValidationServiceQualificationResponse avsqResponse = new AddressValidationServiceQualificationResponse();
											avsqResponse.setQualifiedProducts(
													eLocation.getAvsqResponse().getQualifiedProducts());
											avsqResponse.setLocation(eLocation.getAvsqResponse().getLocation());
											location.setAvsqResponse(avsqResponse);
										}
										locationsModifiedList.add(location);
									}
									locationsMap.remove(location.getId());
								} else {
									locationsModifiedList.add(location);
								}
							}
						}
					} else if (null != ipeLocations && Optional.ofNullable(ipeLocations.getLocations()).isPresent()) {
						locationsModifiedList.addAll(ipeLocations.getLocations());
					}
					if (null != locationsMap && !locationsMap.isEmpty()) {
						locationsModifiedList.addAll(
								locationsMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList()));
					}
					if (null != locationsModifiedList) {
						newLocations.setLocations(locationsModifiedList);
					}
					
					mapper.setSerializationInclusion(Include.NON_NULL);
					mapper.setSerializationInclusion(Include.NON_EMPTY);
					nxMpSiteDictionary.setSiteJson(mapper.writeValueAsString(newLocations));
					nxMpSiteDictionaryRepository.save(nxMpSiteDictionary);
				}else {
					nxMpSiteDictionary.setSiteJson(locationJson);
					nxMpSiteDictionaryRepository.save(nxMpSiteDictionary);
				}
				
			} else {
				logger.info("Creating new site json for the nxtxnId ::{}" + nxTxnId);
				nxMpSiteDictionary = new NxMpSiteDictionary();
				nxMpSiteDictionary.setNxTxnId(nxTxnId);
				nxMpSiteDictionary.setSourceSystem(MyPriceConstants.SOURCE_SYSTEM);
				nxMpSiteDictionary.setSiteJson(locationJson);
				nxMpSiteDictionary.setActiveYN(CommonConstants.ACTIVE_Y);
				nxMpSiteDictionaryRepository.save(nxMpSiteDictionary);
			}

		} catch (Exception e) {
			logger.info("Location Json processing for IP&E response is failed for the nxTxnId {}", nxTxnId);
			logger.info("Exception", e);
		}
		return nxMpSiteDictionary;
	}

	public UpdateTransSitesServiceUpdateResponse processSiteServiceUpdate(Long nxTxnId, Long myPriceTransId,
			ServiceValidationRequest serviceValidationRequest, Map<String, Object> paramMap) throws SalesBusinessException {

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		SimpleFilterProvider filterProvider = new SimpleFilterProvider();
		filterProvider.addFilter("avsqLocationFilter",
				SimpleBeanPropertyFilter.filterOutAllExcept("GISLocationAttributes", "LocationNetworkAttributes"));
		objectMapper.setFilterProvider(filterProvider);
		LocationsWrapper locationsWrapper = new LocationsWrapper();
		List<UpdateTransSitesServiceUpdateLocations> sitesStatusUpdateLocationList = new ArrayList<>();

		NxMpSiteDictionary siteDictionary = nxTxnId == null ? null : nxMpSiteDictionaryRepository.findByNxTxnId(nxTxnId);

		UpdateTransSitesServiceUpdateResponse siteServiceUpdateResponse = null;
		UpdateTransSitesServiceUpdateLocationsWrapper sitesStatusUpdateLocWrapper = new UpdateTransSitesServiceUpdateLocationsWrapper();

		try {
			Map<String, List<Locations>> locationMap = new HashMap<>();
			if (siteDictionary != null && siteDictionary.getSiteJson() != null) {
				locationsWrapper = objectMapper.readValue(siteDictionary.getSiteJson(), LocationsWrapper.class);

				for (Locations location : locationsWrapper.getLocations()) {
					if (locationMap.containsKey(location.getNxSiteId())) {
						locationMap.get(location.getNxSiteId()).add(location);
					} else {
						locationMap.put(location.getNxSiteId(), new ArrayList<Locations>(Arrays.asList(location)));
					}
				}
			}
			for (SiteDetails site : serviceValidationRequest.getSiteDetails()) {
				if (CollectionUtils.isNotEmpty(site.getConfigurationDetails())) {
					for (ConfigurationDetails config : site.getConfigurationDetails()) {
						if (config.getAvsqResponse() != null) {
							UpdateTransSitesServiceUpdateAVSQResponse sitesStatusUpdateAVSQResp = new UpdateTransSitesServiceUpdateAVSQResponse();
							UpdateTransSitesServiceUpdateLocations sitesStatusUpdateLocations = new UpdateTransSitesServiceUpdateLocations();
							sitesStatusUpdateAVSQResp
									.setQualifiedProducts(config.getAvsqResponse().getQualifiedProducts());
							sitesStatusUpdateAVSQResp.setLocation(config.getAvsqResponse().getLocation());
							sitesStatusUpdateLocations.setNxSiteId(site.getNxSiteId());
							sitesStatusUpdateLocations.setName(site.getName());
							sitesStatusUpdateLocations
									.setDocumentNumber((config.getDocumentNumber() != null) ? config.getDocumentNumber()
											: getDocNumFromServiceValidationRequest(serviceValidationRequest,
													site.getNxSiteId()));
							sitesStatusUpdateLocations
									.setAddressValidationServiceQualificationResponse(sitesStatusUpdateAVSQResp);
							sitesStatusUpdateLocations.setQualConversationId(config.getQualConversationId());
							sitesStatusUpdateLocationList.add(sitesStatusUpdateLocations);
						} else if (locationMap.get(site.getNxSiteId()) != null) {
							List<Locations> locations = locationMap.get(site.getNxSiteId());
							for (Locations location : locations) {
								if (StringUtils.equals(config.getDocumentNumber(), location.getDocumentNumber())) {
									UpdateTransSitesServiceUpdateAVSQResponse sitesStatusUpdateAVSQResp = new UpdateTransSitesServiceUpdateAVSQResponse();
									UpdateTransSitesServiceUpdateLocations sitesStatusUpdateLocations = new UpdateTransSitesServiceUpdateLocations();
									sitesStatusUpdateAVSQResp
											.setQualifiedProducts(location.getAvsqResponse().getQualifiedProducts());
									sitesStatusUpdateAVSQResp.setLocation(location.getAvsqResponse().getLocation());
									sitesStatusUpdateLocations.setNxSiteId(location.getNxSiteId());
									sitesStatusUpdateLocations.setName(location.getLocName());
									sitesStatusUpdateLocations.setDocumentNumber(
											(location.getDocumentNumber() != null) ? location.getDocumentNumber()
													: getDocNumFromServiceValidationRequest(serviceValidationRequest,
															location.getNxSiteId()));
									sitesStatusUpdateLocations.setAddressValidationServiceQualificationResponse(
											sitesStatusUpdateAVSQResp);
									sitesStatusUpdateLocations.setQualConversationId(location.getQualConversationId());
									sitesStatusUpdateLocationList.add(sitesStatusUpdateLocations);
								}
							}
						}
					}
				}
			}
			UpdateTransSitesServiceUpdateRequest request = new UpdateTransSitesServiceUpdateRequest();
			SitesServiceUpdateDocuments document = new SitesServiceUpdateDocuments();
			sitesStatusUpdateLocWrapper.setLocations(sitesStatusUpdateLocationList);
			String sitesStatusUpdateLocString = objectMapper.writeValueAsString(sitesStatusUpdateLocWrapper);
			JsonNode sitesStatusUpdateNode = mapper.readTree(sitesStatusUpdateLocString);
			logger.info("original sitesStatusUpdateLocWrapper Json for nxTxnId {} : {}", nxTxnId, sitesStatusUpdateNode);
			List<NxLookupData> filter = nxLookupDataRepository.findByDatasetNameAndActive(MP_SITE_SERVICE_UPDATE_FILTER, "Y");
			String[] filterWords = filter.get(0).getCriteria().split("\\s*,\\s*");
			JacksonUtil.trimJsonWithTagFilter(sitesStatusUpdateNode, new HashSet<>(Arrays.asList(filterWords)));
			logger.info("trimed sitesStatusUpdateLocWrapper Json for nxTxnId {} : {}", nxTxnId, sitesStatusUpdateNode);
			document.setIntegrationServiceSiteDict1(sitesStatusUpdateNode.toString());
			request.setDocuments(document);
			siteServiceUpdateResponse = siteServiceUpdate.sitesServiceUpdate(request, myPriceTransId, paramMap);
		} catch (JsonProcessingException e1) {
			logger.info(
					"sitesStatusUpdateLocWrapper Json processing for processSiteServiceUpdate is failed for the nxTxnId {}",
					nxTxnId);
			logger.info("Exception", e1);
		} catch (IOException e) {
			logger.info("Location Json processing for processSiteServiceUpdate is failed for the nxTxnId {}", nxTxnId);
			logger.info("Exception", e);
		}
		return siteServiceUpdateResponse;
	}

	private long getNxSiteId() {
		Query q = em.createNativeQuery("SELECT SEQ_NX_SITE_ID.NEXTVAL FROM DUAL");
		BigDecimal result = q != null ? (BigDecimal) q.getSingleResult() : new BigDecimal(0);
		return result.longValue();
	}

	private String getDocNumFromServiceValidationRequest(ServiceValidationRequest request, String nxSiteId) {
		String docNum = null;
		for (SiteDetails siteDetail : request.getSiteDetails()) {
			if (siteDetail.getNxSiteId().equals(nxSiteId)) {
				docNum = siteDetail.getConfigurationDetails().get(0).getDocumentNumber();
			}
		}
		return docNum;
	}
	
	public String getSwccliFromAVSQ(SiteDetails siteDetails,Map<String, Object> paramMap){
		String result=null;
		Location location = new Location();
		populateLocation(siteDetails, location);
		AddressValidationServiceQualificationRequest avsqRequest = new AddressValidationServiceQualificationRequest();
		avsqRequest.setLocation(location);
		try {
			paramMap.put(ServiceMetaData.REST_REQUEST_START_TIME, System.currentTimeMillis());
			ThreadMetaDataUtil.initThreadMetaData(paramMap);
			String response=dme2RestClient.callAVSQRequest(avsqRequest, paramMap);
			if(response!=null) {
				JsonNode addressValidationServiceQualificationResponse = mapper.readTree(response);
				String swccli=getDataInString(addressValidationServiceQualificationResponse, swccliPath);
				if(swccli!=null) {
					result=swccli;
				}
			}
		}
		catch (SalesBusinessException | JsonProcessingException e) {
			logger.info("Exception:", e);
		} finally {
			ThreadMetaDataUtil.destroyThreadMetaData();
		}
	
		return result;
	}
	
	public String getDataInString(Object request,String path) {
		TypeRef<List<String>> ref = new TypeRef<List<String>>() {};
		List<String> dataLst=jsonPathUtil.search(request,path, ref);
		if(CollectionUtils.isNotEmpty(dataLst)) {
			return dataLst.get(0);
		}
		return null;
	}
}
