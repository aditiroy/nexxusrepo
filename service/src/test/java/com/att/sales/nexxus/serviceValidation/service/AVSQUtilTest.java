/**
 * 
 */
package com.att.sales.nexxus.serviceValidation.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxMpSiteDictionary;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxMpSiteDictionaryRepository;
import com.att.sales.nexxus.serviceValidation.model.ASE;
import com.att.sales.nexxus.serviceValidation.model.ConfigurationDetails;
import com.att.sales.nexxus.serviceValidation.model.DesignConfiguration;
import com.att.sales.nexxus.serviceValidation.model.Location;
import com.att.sales.nexxus.serviceValidation.model.ServiceValidationRequest;
import com.att.sales.nexxus.serviceValidation.model.SiteDetails;
import com.att.sales.nexxus.serviceValidation.model.UpdateTransSitesServiceUpdateRequest;
import com.att.sales.nexxus.serviceValidation.model.UpdateTransSitesServiceUpdateResponse;
import com.att.sales.nexxus.serviceValidation.model.ValidationOptions;
import com.att.sales.nexxus.util.RestClientUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;

/**
 * @author ShruthiCJ
 *
 */
@ExtendWith(MockitoExtension.class)
public class AVSQUtilTest {

	@InjectMocks
	AVSQUtil avsqUtil;

	@Autowired
	RestClientUtil restClient;

	@Mock
	NxMpDealRepository nxMpDealRepository;

	@Mock
	NxMpSiteDictionaryRepository nxMpSiteDictionaryRepository;

	@Mock
	UpdateTransactionSitesServiceUpdate updateTransSitesServiceUpdate;

	@Mock
	private EntityManager em;

	@Mock
	Query q;
	
	@Mock
	private ExecutorService executorService;
	
	@InjectMocks
	AVSQExecutorService aVSQExecutorService;
	
	@Mock
	private Environment env;
	
	@Mock
	private NxLookupDataRepository nxLookupDataRepository;
	
	private ObjectMapper realMapper = new ObjectMapper();

	private Map<String, Object> paramMap = new HashMap<String, Object>();
	
	
	
	@Test
	public void validatePopulateASEDefaultValue() {
		ASE ase = new ASE();
		avsqUtil.populateASEDefaultValue(ase);
	}

	@Test
	public void validatePopulateValidOptnsDefaultValue() {
		ValidationOptions validationOptions = new ValidationOptions();
		avsqUtil.populateValidOptnsDefaultValue(validationOptions);
	}
	@Test
	public void validateCallAVSQ() throws InterruptedException {
		ReflectionTestUtils.setField(avsqUtil, "mapper", realMapper);
		Mockito.when(env.getProperty("avsq.threadPool.size=12")).thenReturn("");
		ServiceValidationRequest svRequest = new ServiceValidationRequest();
		List<DesignConfiguration> designConfigurations = new ArrayList<>();
		DesignConfiguration designConfig = new DesignConfiguration();
		designConfig.setName("N1");
		designConfig.setValue("V1");
		designConfigurations.add(designConfig);
	
		List<ConfigurationDetails> configurationDetails = new ArrayList<>();
		ConfigurationDetails configDetails = new ConfigurationDetails();
		configDetails.setDocumentNumber("11");
		configDetails.setModelName("ASE");
		configDetails.setDesignConfiguration(designConfigurations);
		configurationDetails.add(configDetails);
	
		List<SiteDetails> siteDetailList = new ArrayList<>();
		SiteDetails siteDetails = new SiteDetails();
		siteDetails.setNxSiteId("1");
		siteDetails.setAddressLine("1919 McKinney Ave");
		siteDetails.setCity("Dallas");
		siteDetails.setState("TX");
		siteDetails.setPostalCode("75201");
		siteDetails.setConfigurationDetails(configurationDetails);
		siteDetailList.add(siteDetails);
	
		svRequest.setTransactionId(1L);
		svRequest.setOptyId("100");
		svRequest.setDealId(10L);
		svRequest.setSiteDetails(siteDetailList);
		NxMpDeal nxMpDeal = new NxMpDeal();
		nxMpDeal.setNxTxnId(1L);
		when(nxMpDealRepository.findByTransactionId(String.valueOf(svRequest.getTransactionId()))).thenReturn(nxMpDeal);
		NxMpSiteDictionary nxMpSiteDictionary = new NxMpSiteDictionary();
		nxMpSiteDictionary.setNxTxnId(nxMpDeal.getNxTxnId());
		String siteJson = "{ \"status\": \"COMPLETED\", \"locations\": [ { \"id\": 212, \"nxSiteId\": 238, \"locName\": \"Test 1\", \"street\": \"2111 Dickson Dr\", \"city\": \"Austin\", \"state\": \"TX\", \"zip\": \"78704\", \"validationStatus\": \"VALID\", \"avsqResponse\": { \"Location\": { \"GISLocationAttributes\": [ { \"globalLocationId\": \"00000837A5\", \"FieldedAddress\": { \"city\": \"AUSTIN\", \"state\": \"TX\", \"postalCode\": \"78704\", \"singleLineStandardizedAddress\": \"2111 DICKSON DR,AUSTIN,TX,78704-4796\", \"country\": \"USA\", \"postalCodePlus4\": \"4796\" }, \"LocationProperties\": { \"matchStatus\": \"M\", \"buildingClli\": \"AUSJTXIY\", \"regionFranchiseStatus\": \"Y\", \"addressMatchCode\": \"S80\", \"swcCLLI\": \"AUSTTXHI\" } } ] } } } ] }";
		nxMpSiteDictionary.setSiteJson(siteJson);
		when(nxMpSiteDictionaryRepository.findByNxTxnId(nxMpDeal.getNxTxnId())).thenReturn(nxMpSiteDictionary);
		List<Future<Object>> resultLst =new ArrayList<>();
		Future<Object> fo=null;
		resultLst.add(fo);
		when(executorService.invokeAll(any())).thenReturn(resultLst);
		NxLookupData nxLookupData = new NxLookupData();
		nxLookupData.setCriteria("A");
		List<NxLookupData> filter = new ArrayList<>();
		filter.add(nxLookupData);
		when(nxLookupDataRepository.findByDatasetNameAndActive(any(), any())).thenReturn(filter);
		//when(executorService.invokeAll(siteDetailList));
		avsqUtil.callAVSQ(svRequest, 1L, paramMap);
	}
	@Test
	public void validateADSLCallAVSQ() throws InterruptedException {
		Mockito.when(env.getProperty("avsq.threadPool.size=12")).thenReturn("");
		ServiceValidationRequest svRequest = new ServiceValidationRequest();
		List<DesignConfiguration> designConfigurations = new ArrayList<>();
		DesignConfiguration designConfig = new DesignConfiguration();
		designConfig.setName("N1");
		designConfig.setValue("V1");
		designConfigurations.add(designConfig);
	
		List<ConfigurationDetails> configurationDetails = new ArrayList<>();
		ConfigurationDetails configDetails = new ConfigurationDetails();
		configDetails.setDocumentNumber("11");
		configDetails.setModelName("ADSL");
		configDetails.setDesignConfiguration(designConfigurations);
		configurationDetails.add(configDetails);
	
		List<SiteDetails> siteDetailList = new ArrayList<>();
		SiteDetails siteDetails = new SiteDetails();
		siteDetails.setNxSiteId("1");
		siteDetails.setAddressLine("1919 McKinney Ave");
		siteDetails.setCity("Dallas");
		siteDetails.setState("TX");
		siteDetails.setPostalCode("75201");
		siteDetails.setConfigurationDetails(configurationDetails);
		siteDetailList.add(siteDetails);
	
		svRequest.setTransactionId(1L);
		svRequest.setOptyId("100");
		svRequest.setDealId(10L);
		svRequest.setSiteDetails(siteDetailList);
		NxMpDeal nxMpDeal = new NxMpDeal();
		nxMpDeal.setNxTxnId(1L);
		when(nxMpDealRepository.findByTransactionId(String.valueOf(svRequest.getTransactionId()))).thenReturn(nxMpDeal);
		NxMpSiteDictionary nxMpSiteDictionary = new NxMpSiteDictionary();
		nxMpSiteDictionary.setNxTxnId(nxMpDeal.getNxTxnId());
		String siteJson = "{ \"status\": \"COMPLETED\", \"locations\": [ { \"id\": 212, \"nxSiteId\": 238, \"locName\": \"Test 1\", \"street\": \"2111 Dickson Dr\", \"city\": \"Austin\", \"state\": \"TX\", \"zip\": \"78704\", \"validationStatus\": \"VALID\", \"avsqResponse\": { \"Location\": { \"GISLocationAttributes\": [ { \"globalLocationId\": \"00000837A5\", \"FieldedAddress\": { \"city\": \"AUSTIN\", \"state\": \"TX\", \"postalCode\": \"78704\", \"singleLineStandardizedAddress\": \"2111 DICKSON DR,AUSTIN,TX,78704-4796\", \"country\": \"USA\", \"postalCodePlus4\": \"4796\" }, \"LocationProperties\": { \"matchStatus\": \"M\", \"buildingClli\": \"AUSJTXIY\", \"regionFranchiseStatus\": \"Y\", \"addressMatchCode\": \"S80\", \"swcCLLI\": \"AUSTTXHI\" } } ] } } } ] }";
		nxMpSiteDictionary.setSiteJson(siteJson);
		when(nxMpSiteDictionaryRepository.findByNxTxnId(nxMpDeal.getNxTxnId())).thenReturn(nxMpSiteDictionary);
		List<Future<Object>> resultLst =new ArrayList<>();
		Future<Object> fo=null;
		resultLst.add(fo);
		when(executorService.invokeAll(any())).thenReturn(resultLst);
		//when(executorService.invokeAll(siteDetailList));
		avsqUtil.callAVSQ(svRequest, 1L, paramMap);
	}
    @Test
	public void testPopulateSiteJsonforQualification() {
		Long nxTxnId = 0L;
		String ipeResponse = "{\r\n" + 
				"    \"status\": \"COMPLETED\",\r\n" + 
				"    \"locations\": [\r\n" + 
				"        {\r\n" + 
				"            \"id\": 212,\r\n" + 
				"            \"nxSiteId\": 238,\r\n" + 
				"            \"locName\": \"Test 1\",\r\n" + 
				"            \"street\": \"2111 Dickson Dr\",\r\n" + 
				"            \"city\": \"Austin\",\r\n" + 
				"            \"state\": \"TX\",\r\n" + 
				"            \"zip\": \"78704\",\r\n" + 
				"            \"validationStatus\": \"VALID\",\r\n" + 
				"            \"addressValidationServiceQualificationResponse\":\"{\\\"ResultResponse\\\":[{\\\"code\\\":\\\"1008\\\",\\\"description\\\":\\\"Location Data Found Successfully.\\\",\\\"responseApplicationName\\\":\\\"PED\\\"}],\\\"Response\\\":{\\\"code\\\":\\\"0\\\",\\\"description\\\":\\\"Success\\\"},\\\"Location\\\":{\\\"LocationNetworkAttributes\\\":{\\\"FiberSummary\\\":{\\\"FTTBData\\\":{\\\"emtFlag\\\":\\\"false\\\",\\\"servingWireCenterCLLI\\\":\\\"MDTWNJMD\\\",\\\"fttpProjectCode\\\":\\\"FTB0ENJ\\\",\\\"customerPremiseLocation\\\":\\\"NJ0076695\\\",\\\"insideWireRecommendedCode\\\":\\\"A\\\",\\\"distributedAntennaSystemIndicator\\\":\\\"Y\\\",\\\"buildingId\\\":\\\"00001QEVTP\\\",\\\"smallCellAvailableIndicator\\\":\\\"Y\\\",\\\"buildingFiberStatus\\\":{\\\"code\\\":\\\"E\\\",\\\"color\\\":\\\"GREEN\\\"},\\\"buildingCLLI\\\":\\\"MDTWNJAS\\\",\\\"wifiAvailableIndicator\\\":\\\"Y\\\",\\\"planOfExecutionIndicator\\\":true,\\\"nteFlag\\\":\\\"false\\\"},\\\"tier\\\":\\\"Tier 4\\\",\\\"pricingTier\\\":\\\"\\\",\\\"gponLitIndicator\\\":\\\"N\\\"}},\\\"GISLocationAttributes\\\":[{\\\"FieldedAddress\\\":{\\\"singleLineStandardizedAddress\\\":\\\"200 S LAUREL AVE,MIDDLETOWN,NJ,07748-1914\\\",\\\"country\\\":\\\"USA\\\",\\\"streetName\\\":\\\"LAUREL\\\",\\\"city\\\":\\\"MIDDLETOWN\\\",\\\"postalCode\\\":\\\"07748\\\",\\\"postalCodePlus4\\\":\\\"1914\\\",\\\"houseNumber\\\":\\\"200\\\",\\\"county\\\":\\\"MONMOUTH COUNTY\\\",\\\"state\\\":\\\"NJ\\\",\\\"streetThoroughfare\\\":\\\"AVE\\\",\\\"streetDirection\\\":\\\"S\\\"},\\\"LocationProperties\\\":{\\\"localProviderExchangeCode\\\":\\\"732671\\\",\\\"taxGeoCode\\\":\\\"0131025143000\\\",\\\"rateZone\\\":\\\"New York NY\\\",\\\"centralOfficeCode\\\":\\\"MDTWNJMD\\\",\\\"localProviderName\\\":\\\"VERIZON NEW JERSEY, INC.\\\",\\\"centralOfficeType\\\":\\\"AA\\\",\\\"gisMatchCode\\\":\\\"S80\\\",\\\"primaryNpaNxx\\\":{\\\"npa\\\":\\\"732\\\",\\\"nxx\\\":\\\"671\\\"},\\\"icoServingWireCenterCLLI\\\":\\\"MDTWNJMD\\\",\\\"lataCode\\\":\\\"224\\\",\\\"rateCenterCode\\\":\\\"1001694\\\",\\\"legalEntity\\\":\\\"Q90A\\\",\\\"icoCompanyName\\\":\\\"VERIZON NEW JERSEY, INC.\\\",\\\"localProviderNumber\\\":\\\"9206\\\",\\\"matchStatus\\\":\\\"M\\\",\\\"buildingClli\\\":\\\"MDTTNJGO\\\",\\\"gisLocationCode\\\":\\\"AS0\\\",\\\"rateZoneBandCode\\\":\\\"501\\\",\\\"RateCenter\\\":{\\\"name\\\":\\\"MIDDLETOWN\\\",\\\"state\\\":\\\"NJ\\\",\\\"abbreviatedName\\\":\\\"MIDDLETOWN\\\"},\\\"exchangeCode\\\":\\\"732671\\\",\\\"cityStatePostalCodeValidFlag\\\":\\\"true\\\",\\\"lataName\\\":\\\"NORTH JERSEY NJ\\\",\\\"matchLevel\\\":\\\"Street\\\",\\\"Coordinates\\\":{\\\"latitude\\\":40.395839,\\\"longitude\\\":-74.143834},\\\"regionFranchiseStatus\\\":\\\"N\\\",\\\"exco\\\":\\\"732AA\\\",\\\"affiliateName\\\":\\\"New Jersey - Teleport Communications America, LLC (TCAL) \\\",\\\"horizontalCoordinate\\\":\\\"01376\\\",\\\"superScore\\\":770,\\\"verticalCoordinate\\\":\\\"05069\\\",\\\"countyCode\\\":\\\"025\\\",\\\"LocalNetworkServices\\\":[{\\\"clli\\\":\\\"FRHDNJ02H11\\\",\\\"functionType\\\":\\\"TCAL\\\",\\\"responseMessage\\\":\\\"Address found\\\",\\\"localServingOffice\\\":\\\"732275\\\"}],\\\"addressMatchCode\\\":\\\"S80\\\",\\\"convergedBillingAvailabilityFlag\\\":\\\"true\\\",\\\"swcCLLI\\\":\\\"MDTWNJMD\\\",\\\"splitFlag\\\":\\\"false\\\",\\\"coreBasedStatisticalAreaCode\\\":\\\"35620\\\",\\\"localProviderAbbreviatedName\\\":\\\"BANJ\\\",\\\"localProviderServingOfficeCode\\\":\\\"MDTWNJMDDS5\\\",\\\"locator\\\":\\\"EGM\\\"},\\\"globalLocationId\\\":\\\"00001QEVTP\\\",\\\"addressType\\\":\\\"FieldedLandbase\\\",\\\"buildingAddressGLID\\\":\\\"00001QEVTP\\\"}],\\\"HostResponse\\\":[{\\\"Status\\\":{\\\"code\\\":\\\"BP0000\\\",\\\"description\\\":\\\"Address is not valid in-region address\\\"},\\\"hostName\\\":\\\"AttSag\\\",\\\"MatchStatus\\\":\\\"NO_MATCH\\\"},{\\\"Status\\\":{\\\"code\\\":\\\"S1000\\\",\\\"description\\\":\\\"Single Match\\\"},\\\"hostName\\\":\\\"GIS\\\",\\\"enterpriseGeocodingModuleStatus\\\":{\\\"code\\\":\\\"00000\\\",\\\"description\\\":\\\"No change in address line\\\"},\\\"MatchStatus\\\":\\\"MATCH\\\"},{\\\"Status\\\":{\\\"code\\\":\\\"001\\\",\\\"description\\\":\\\"Existing GLID returned\\\"},\\\"hostName\\\":\\\"Glid\\\",\\\"MatchStatus\\\":\\\"MATCH\\\"}]}}\"\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
				
		NxMpDeal cloneNxMpDeal = new NxMpDeal();
		cloneNxMpDeal.setSourceId("1");
		List<NxMpDeal> nxMpDealList = new ArrayList<>();
		nxMpDealList.add(cloneNxMpDeal);
		Mockito.when(nxMpDealRepository.findAllByTransactionId(cloneNxMpDeal.getSourceId())).thenReturn(nxMpDealList);
		NxMpSiteDictionary siteDictionary = new NxMpSiteDictionary();
		String siteJson = "{\"status\":\"COMPLETED\",\"locations\":[{\"id\":212,\"locName\":\"Test 1\",\"street\":\"2111 Dickson Dr\",\"city\":\"Austin\",\"state\":\"TX\",\"zip\":\"78704\",\"validationStatus\":\"VALID\",\"avsqResponse\":{\"QualifiedProducts\":{\"ASE\":{\"aseAvailabilityFlag\":false,\"FiberAvailabilityResults\":{\"fiberAvailabilityFlag\":false,\"ProductFTTB\":{\"FTTBData\":{\"buildingFiberStatus\":{\"color\":\"GREEN\",\"code\":\"E\"}}},\"ProductASE\":[{\"aggregatorType\":\"SEMUX\",\"aseColorCode\":\"GREEN\"},{\"aggregatorType\":\"IPAG2X\",\"aseColorCode\":\"GREEN\"}],\"FiberDataSummary\":{\"fiberLitIndicator\":true,\"pricingTier\":\"Tier 1\",\"FiberAerialLoopDistance\":{\"quantity\":0.35,\"unit\":\"MILES\"}}}}},\"Location\":{\"GISLocationAttributes\":[{\"globalLocationId\":\"00000837A5\",\"FieldedAddress\":{\"city\":\"AUSTIN\",\"state\":\"TX\",\"postalCode\":\"78704\",\"singleLineStandardizedAddress\":\"2111 DICKSON DR,AUSTIN,TX,78704-4796\",\"country\":\"USA\",\"postalCodePlus4\":\"4796\"},\"LocationProperties\":{\"matchStatus\":\"M\",\"buildingClli\":\"AUSJTXIY\",\"regionFranchiseStatus\":\"Y\",\"addressMatchCode\":\"S80\",\"swcCLLI\":\"AUSTTXHI\"}}]}}}]}";
		siteDictionary.setSiteJson(siteJson);
		siteDictionary.setNxTxnId(nxTxnId);
		Mockito.when(nxMpSiteDictionaryRepository.findByNxTxnId(cloneNxMpDeal.getNxTxnId())).thenReturn(siteDictionary);
		avsqUtil.populateSiteJsonforQualification(nxTxnId, ipeResponse,cloneNxMpDeal);
	}

	
	@Test
	public void testPopulateSiteJsonforQualifications() {
		Long nxTxnId = 0L;
		String ipeResponse = "{\r\n" + 
				"    \"status\": \"COMPLETED\",\r\n" + 
				"    \"locations\": [\r\n" + 
				"        {\r\n" + 
				"            \"id\": 212,\r\n" + 
				"            \"nxSiteId\": 238,\r\n" + 
				"            \"locName\": \"Test 1\",\r\n" + 
				"            \"street\": \"2111 Dickson Dr\",\r\n" + 
				"            \"city\": \"Austin\",\r\n" + 
				"            \"state\": \"TX\",\r\n" + 
				"            \"zip\": \"78704\",\r\n" + 
				"            \"validationStatus\": \"VALID\",\r\n" + 
				"            \"addressValidationServiceQualificationResponse\":\"{\\\"ResultResponse\\\":[{\\\"code\\\":\\\"1008\\\",\\\"description\\\":\\\"Location Data Found Successfully.\\\",\\\"responseApplicationName\\\":\\\"PED\\\"}],\\\"Response\\\":{\\\"code\\\":\\\"0\\\",\\\"description\\\":\\\"Success\\\"},\\\"Location\\\":{\\\"LocationNetworkAttributes\\\":{\\\"FiberSummary\\\":{\\\"FTTBData\\\":{\\\"emtFlag\\\":\\\"false\\\",\\\"servingWireCenterCLLI\\\":\\\"MDTWNJMD\\\",\\\"fttpProjectCode\\\":\\\"FTB0ENJ\\\",\\\"customerPremiseLocation\\\":\\\"NJ0076695\\\",\\\"insideWireRecommendedCode\\\":\\\"A\\\",\\\"distributedAntennaSystemIndicator\\\":\\\"Y\\\",\\\"buildingId\\\":\\\"00001QEVTP\\\",\\\"smallCellAvailableIndicator\\\":\\\"Y\\\",\\\"buildingFiberStatus\\\":{\\\"code\\\":\\\"E\\\",\\\"color\\\":\\\"GREEN\\\"},\\\"buildingCLLI\\\":\\\"MDTWNJAS\\\",\\\"wifiAvailableIndicator\\\":\\\"Y\\\",\\\"planOfExecutionIndicator\\\":true,\\\"nteFlag\\\":\\\"false\\\"},\\\"tier\\\":\\\"Tier 4\\\",\\\"pricingTier\\\":\\\"\\\",\\\"gponLitIndicator\\\":\\\"N\\\"}},\\\"GISLocationAttributes\\\":[{\\\"FieldedAddress\\\":{\\\"singleLineStandardizedAddress\\\":\\\"200 S LAUREL AVE,MIDDLETOWN,NJ,07748-1914\\\",\\\"country\\\":\\\"USA\\\",\\\"streetName\\\":\\\"LAUREL\\\",\\\"city\\\":\\\"MIDDLETOWN\\\",\\\"postalCode\\\":\\\"07748\\\",\\\"postalCodePlus4\\\":\\\"1914\\\",\\\"houseNumber\\\":\\\"200\\\",\\\"county\\\":\\\"MONMOUTH COUNTY\\\",\\\"state\\\":\\\"NJ\\\",\\\"streetThoroughfare\\\":\\\"AVE\\\",\\\"streetDirection\\\":\\\"S\\\"},\\\"LocationProperties\\\":{\\\"localProviderExchangeCode\\\":\\\"732671\\\",\\\"taxGeoCode\\\":\\\"0131025143000\\\",\\\"rateZone\\\":\\\"New York NY\\\",\\\"centralOfficeCode\\\":\\\"MDTWNJMD\\\",\\\"localProviderName\\\":\\\"VERIZON NEW JERSEY, INC.\\\",\\\"centralOfficeType\\\":\\\"AA\\\",\\\"gisMatchCode\\\":\\\"S80\\\",\\\"primaryNpaNxx\\\":{\\\"npa\\\":\\\"732\\\",\\\"nxx\\\":\\\"671\\\"},\\\"icoServingWireCenterCLLI\\\":\\\"MDTWNJMD\\\",\\\"lataCode\\\":\\\"224\\\",\\\"rateCenterCode\\\":\\\"1001694\\\",\\\"legalEntity\\\":\\\"Q90A\\\",\\\"icoCompanyName\\\":\\\"VERIZON NEW JERSEY, INC.\\\",\\\"localProviderNumber\\\":\\\"9206\\\",\\\"matchStatus\\\":\\\"M\\\",\\\"buildingClli\\\":\\\"MDTTNJGO\\\",\\\"gisLocationCode\\\":\\\"AS0\\\",\\\"rateZoneBandCode\\\":\\\"501\\\",\\\"RateCenter\\\":{\\\"name\\\":\\\"MIDDLETOWN\\\",\\\"state\\\":\\\"NJ\\\",\\\"abbreviatedName\\\":\\\"MIDDLETOWN\\\"},\\\"exchangeCode\\\":\\\"732671\\\",\\\"cityStatePostalCodeValidFlag\\\":\\\"true\\\",\\\"lataName\\\":\\\"NORTH JERSEY NJ\\\",\\\"matchLevel\\\":\\\"Street\\\",\\\"Coordinates\\\":{\\\"latitude\\\":40.395839,\\\"longitude\\\":-74.143834},\\\"regionFranchiseStatus\\\":\\\"N\\\",\\\"exco\\\":\\\"732AA\\\",\\\"affiliateName\\\":\\\"New Jersey - Teleport Communications America, LLC (TCAL) \\\",\\\"horizontalCoordinate\\\":\\\"01376\\\",\\\"superScore\\\":770,\\\"verticalCoordinate\\\":\\\"05069\\\",\\\"countyCode\\\":\\\"025\\\",\\\"LocalNetworkServices\\\":[{\\\"clli\\\":\\\"FRHDNJ02H11\\\",\\\"functionType\\\":\\\"TCAL\\\",\\\"responseMessage\\\":\\\"Address found\\\",\\\"localServingOffice\\\":\\\"732275\\\"}],\\\"addressMatchCode\\\":\\\"S80\\\",\\\"convergedBillingAvailabilityFlag\\\":\\\"true\\\",\\\"swcCLLI\\\":\\\"MDTWNJMD\\\",\\\"splitFlag\\\":\\\"false\\\",\\\"coreBasedStatisticalAreaCode\\\":\\\"35620\\\",\\\"localProviderAbbreviatedName\\\":\\\"BANJ\\\",\\\"localProviderServingOfficeCode\\\":\\\"MDTWNJMDDS5\\\",\\\"locator\\\":\\\"EGM\\\"},\\\"globalLocationId\\\":\\\"00001QEVTP\\\",\\\"addressType\\\":\\\"FieldedLandbase\\\",\\\"buildingAddressGLID\\\":\\\"00001QEVTP\\\"}],\\\"HostResponse\\\":[{\\\"Status\\\":{\\\"code\\\":\\\"BP0000\\\",\\\"description\\\":\\\"Address is not valid in-region address\\\"},\\\"hostName\\\":\\\"AttSag\\\",\\\"MatchStatus\\\":\\\"NO_MATCH\\\"},{\\\"Status\\\":{\\\"code\\\":\\\"S1000\\\",\\\"description\\\":\\\"Single Match\\\"},\\\"hostName\\\":\\\"GIS\\\",\\\"enterpriseGeocodingModuleStatus\\\":{\\\"code\\\":\\\"00000\\\",\\\"description\\\":\\\"No change in address line\\\"},\\\"MatchStatus\\\":\\\"MATCH\\\"},{\\\"Status\\\":{\\\"code\\\":\\\"001\\\",\\\"description\\\":\\\"Existing GLID returned\\\"},\\\"hostName\\\":\\\"Glid\\\",\\\"MatchStatus\\\":\\\"MATCH\\\"}]}}\"\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
				
		NxMpDeal cloneNxMpDeal = new NxMpDeal();
		cloneNxMpDeal.setSourceId("1");
		List<NxMpDeal> nxMpDealList = new ArrayList<>();
		nxMpDealList.add(cloneNxMpDeal);
		Mockito.when(nxMpDealRepository.findAllByTransactionId(cloneNxMpDeal.getSourceId())).thenReturn(nxMpDealList);
		NxMpSiteDictionary siteDictionary = new NxMpSiteDictionary();
		String siteJson = "{\"status\":\"COMPLETED\",\"locations\":[{\"id\":212,\"nxSiteId\":238,\"locName\":\"Test 1\",\"street\":\"2111 Dickson Dr\",\"city\":\"Austin\",\"state\":\"TX\",\"zip\":\"78704\",\"validationStatus\":\"VALID\",\"avsqResponse\":{\"QualifiedProducts\":{\"ASE\":{\"aseAvailabilityFlag\":false,\"FiberAvailabilityResults\":{\"fiberAvailabilityFlag\":false,\"ProductFTTB\":{\"FTTBData\":{\"buildingFiberStatus\":{\"color\":\"GREEN\",\"code\":\"E\"}}},\"ProductASE\":[{\"aggregatorType\":\"SEMUX\",\"aseColorCode\":\"GREEN\"},{\"aggregatorType\":\"IPAG2X\",\"aseColorCode\":\"GREEN\"}],\"FiberDataSummary\":{\"fiberLitIndicator\":true,\"pricingTier\":\"Tier 1\",\"FiberAerialLoopDistance\":{\"quantity\":0.35,\"unit\":\"MILES\"}}}}},\"Location\":{\"GISLocationAttributes\":[{\"globalLocationId\":\"00000837A5\",\"FieldedAddress\":{\"city\":\"AUSTIN\",\"state\":\"TX\",\"postalCode\":\"78704\",\"singleLineStandardizedAddress\":\"2111 DICKSON DR,AUSTIN,TX,78704-4796\",\"country\":\"USA\",\"postalCodePlus4\":\"4796\"},\"LocationProperties\":{\"matchStatus\":\"M\",\"buildingClli\":\"AUSJTXIY\",\"regionFranchiseStatus\":\"Y\",\"addressMatchCode\":\"S80\",\"swcCLLI\":\"AUSTTXHI\"}}]}}}]}";
		siteDictionary.setSiteJson(siteJson);
		siteDictionary.setNxTxnId(nxTxnId);
		Mockito.when(nxMpSiteDictionaryRepository.findByNxTxnId(cloneNxMpDeal.getNxTxnId())).thenReturn(siteDictionary);
		Mockito.when(nxMpSiteDictionaryRepository.findByNxTxnId(nxTxnId)).thenReturn(siteDictionary);
		avsqUtil.populateSiteJsonforQualification(nxTxnId, ipeResponse,cloneNxMpDeal);
	}

	@Test
	public void testProcessSiteServiceUpdate() throws SalesBusinessException {
		ReflectionTestUtils.setField(avsqUtil, "mapper", realMapper);
		Long nxTxnId = 1L;
		Long myPriceTransId = 2L;
		String siteJson = "{\"status\":\"COMPLETED\",\"locations\":[{\"id\":212,\"nxSiteId\":238,\"locName\":\"Test 1\",\"street\":\"2111 Dickson Dr\",\"city\":\"Austin\",\"state\":\"TX\",\"zip\":\"78704\",\"validationStatus\":\"VALID\",\"documentNumber\":\"2\",\"avsqResponse\":{\"QualifiedProducts\":{\"ASE\":{\"aseAvailabilityFlag\":false,\"FiberAvailabilityResults\":{\"fiberAvailabilityFlag\":false,\"ProductFTTB\":{\"FTTBData\":{\"buildingFiberStatus\":{\"color\":\"GREEN\",\"code\":\"E\"}}},\"ProductASE\":[{\"aggregatorType\":\"SEMUX\",\"aseColorCode\":\"GREEN\"},{\"aggregatorType\":\"IPAG2X\",\"aseColorCode\":\"GREEN\"}],\"FiberDataSummary\":{\"fiberLitIndicator\":true,\"pricingTier\":\"Tier 1\",\"FiberAerialLoopDistance\":{\"quantity\":0.35,\"unit\":\"MILES\"}}}}},\"Location\":{\"GISLocationAttributes\":[{\"globalLocationId\":\"00000837A5\",\"FieldedAddress\":{\"city\":\"AUSTIN\",\"state\":\"TX\",\"postalCode\":\"78704\",\"singleLineStandardizedAddress\":\"2111 DICKSON DR,AUSTIN,TX,78704-4796\",\"country\":\"USA\",\"postalCodePlus4\":\"4796\"},\"LocationProperties\":{\"matchStatus\":\"M\",\"buildingClli\":\"AUSJTXIY\",\"regionFranchiseStatus\":\"Y\",\"addressMatchCode\":\"S80\",\"swcCLLI\":\"AUSTTXHI\"}}]}}}]}";
		NxMpSiteDictionary siteDictionary = new NxMpSiteDictionary();
		siteDictionary.setSiteJson(siteJson);
		siteDictionary.setNxTxnId(nxTxnId);
		ConfigurationDetails configurationDetails = new ConfigurationDetails();
		configurationDetails.setDocumentNumber("2");
		SiteDetails siteDetails = new SiteDetails();
		siteDetails.setNxSiteId("238");
		siteDetails.setConfigurationDetails(Arrays.asList(configurationDetails));
		ServiceValidationRequest request = new ServiceValidationRequest();
		request.setSiteDetails(Arrays.asList(siteDetails));
		UpdateTransSitesServiceUpdateResponse res = new UpdateTransSitesServiceUpdateResponse();
		when(nxMpSiteDictionaryRepository.findByNxTxnId(nxTxnId)).thenReturn(siteDictionary);
		when(updateTransSitesServiceUpdate.sitesServiceUpdate(any(UpdateTransSitesServiceUpdateRequest.class),
				anyLong(), anyMap())).thenReturn(res);
		NxLookupData nxLookupData = new NxLookupData();
		nxLookupData.setCriteria("A");
		List<NxLookupData> filter = new ArrayList<>();
		filter.add(nxLookupData);
		when(nxLookupDataRepository.findByDatasetNameAndActive(any(), any())).thenReturn(filter);
		avsqUtil.processSiteServiceUpdate(nxTxnId, myPriceTransId, request, paramMap);
	}
	
	
	@Disabled
	@Test
	public void testpopulateLocation() {
		SiteDetails siteDetails = new SiteDetails();

		Location location  = new Location();
		
		avsqUtil.populateLocation(siteDetails, location);
	}
	@Test
	public void validateADEAVSQCALL() {
		Mockito.when(env.getProperty("avsq.threadPool.size=12")).thenReturn("");
		ServiceValidationRequest svRequest = new ServiceValidationRequest();
		List<DesignConfiguration> designConfigurations = new ArrayList<>();
		DesignConfiguration designConfig = new DesignConfiguration();
		designConfig.setName("N1");
		designConfig.setValue("V1");
		designConfigurations.add(designConfig);

		List<ConfigurationDetails> configurationDetails = new ArrayList<>();
		ConfigurationDetails configDetails = new ConfigurationDetails();
		configDetails.setDocumentNumber("11");
		configDetails.setModelName("ADE");
		configDetails.setDesignConfiguration(designConfigurations);
		configurationDetails.add(configDetails);

		List<SiteDetails> siteDetailList = new ArrayList<>();
		SiteDetails siteDetails = new SiteDetails();
		siteDetails.setNxSiteId("1");
		siteDetails.setAddressLine("1919 McKinney Ave");
		siteDetails.setCity("Dallas");
		siteDetails.setState("TX");
		siteDetails.setPostalCode("75201");
		siteDetails.setConfigurationDetails(configurationDetails);
		siteDetailList.add(siteDetails);

		svRequest.setTransactionId(1L);
		svRequest.setOptyId("100");
		svRequest.setDealId(10L);
		svRequest.setSiteDetails(siteDetailList);
		NxMpDeal nxMpDeal = new NxMpDeal();
		nxMpDeal.setNxTxnId(1L);
		when(nxMpDealRepository.findByTransactionId(String.valueOf(svRequest.getTransactionId()))).thenReturn(nxMpDeal);
		NxMpSiteDictionary nxMpSiteDictionary = new NxMpSiteDictionary();
		nxMpSiteDictionary.setNxTxnId(nxMpDeal.getNxTxnId());
		String siteJson = "{ \"status\": \"COMPLETED\", \"locations\": [ { \"id\": 212, \"nxSiteId\": 238, \"locName\": \"Test 1\", \"street\": \"2111 Dickson Dr\", \"city\": \"Austin\", \"state\": \"TX\", \"zip\": \"78704\", \"validationStatus\": \"VALID\", \"avsqResponse\": { \"Location\": { \"GISLocationAttributes\": [ { \"globalLocationId\": \"00000837A5\", \"FieldedAddress\": { \"city\": \"AUSTIN\", \"state\": \"TX\", \"postalCode\": \"78704\", \"singleLineStandardizedAddress\": \"2111 DICKSON DR,AUSTIN,TX,78704-4796\", \"country\": \"USA\", \"postalCodePlus4\": \"4796\" }, \"LocationProperties\": { \"matchStatus\": \"M\", \"buildingClli\": \"AUSJTXIY\", \"regionFranchiseStatus\": \"Y\", \"addressMatchCode\": \"S80\", \"swcCLLI\": \"AUSTTXHI\" } } ] } } } ] }";
		nxMpSiteDictionary.setSiteJson(siteJson);
		when(nxMpSiteDictionaryRepository.findByNxTxnId(nxMpDeal.getNxTxnId())).thenReturn(nxMpSiteDictionary);
		avsqUtil.callAVSQ(svRequest, 1L, paramMap);
	}
	
	@Test
	public void validatePopulateADEDefaultValue() {
		ValidationOptions ade = new ValidationOptions();
		avsqUtil.populateValidOptnsDefaultValue(ade);
	}

	@Test
	public void validateAVPNAvsaCall() {
		Mockito.when(env.getProperty("avsq.threadPool.size=12")).thenReturn("");
		ServiceValidationRequest svRequest = new ServiceValidationRequest();
		List<DesignConfiguration> designConfigurations = new ArrayList<>();
		DesignConfiguration designConfig = new DesignConfiguration();
		designConfig.setName("N1");
		designConfig.setValue("V1");
		designConfigurations.add(designConfig);

		List<ConfigurationDetails> configurationDetails = new ArrayList<>();
		ConfigurationDetails configDetails = new ConfigurationDetails();
		configDetails.setDocumentNumber("11");
		configDetails.setModelName("AVPN");
		configDetails.setDesignConfiguration(designConfigurations);
		configurationDetails.add(configDetails);

		List<SiteDetails> siteDetailList = new ArrayList<>();
		SiteDetails siteDetails = new SiteDetails();
		siteDetails.setNxSiteId("1");
		siteDetails.setAddressLine("1919 McKinney Ave");
		siteDetails.setCity("Dallas");
		siteDetails.setState("TX");
		siteDetails.setPostalCode("75201");
		siteDetails.setConfigurationDetails(configurationDetails);
		siteDetailList.add(siteDetails);

		svRequest.setTransactionId(1L);
		svRequest.setOptyId("100");
		svRequest.setDealId(10L);
		svRequest.setSiteDetails(siteDetailList);
		NxMpDeal nxMpDeal = new NxMpDeal();
		nxMpDeal.setNxTxnId(1L);
		when(nxMpDealRepository.findByTransactionId(String.valueOf(svRequest.getTransactionId()))).thenReturn(nxMpDeal);
		NxMpSiteDictionary nxMpSiteDictionary = new NxMpSiteDictionary();
		nxMpSiteDictionary.setNxTxnId(nxMpDeal.getNxTxnId());
		String siteJson = "{ \"status\": \"COMPLETED\", \"locations\": [ { \"id\": 212, \"nxSiteId\": 238, \"locName\": \"Test 1\", \"street\": \"2111 Dickson Dr\", \"city\": \"Austin\", \"state\": \"TX\", \"zip\": \"78704\", \"validationStatus\": \"VALID\", \"avsqResponse\": { \"Location\": { \"GISLocationAttributes\": [ { \"globalLocationId\": \"00000837A5\", \"FieldedAddress\": { \"city\": \"AUSTIN\", \"state\": \"TX\", \"postalCode\": \"78704\", \"singleLineStandardizedAddress\": \"2111 DICKSON DR,AUSTIN,TX,78704-4796\", \"country\": \"USA\", \"postalCodePlus4\": \"4796\" }, \"LocationProperties\": { \"matchStatus\": \"M\", \"buildingClli\": \"AUSJTXIY\", \"regionFranchiseStatus\": \"Y\", \"addressMatchCode\": \"S80\", \"swcCLLI\": \"AUSTTXHI\" } } ] } } } ] }";
		nxMpSiteDictionary.setSiteJson(siteJson);
		when(nxMpSiteDictionaryRepository.findByNxTxnId(nxMpDeal.getNxTxnId())).thenReturn(nxMpSiteDictionary);
		avsqUtil.callAVSQ(svRequest, 1L, paramMap);
	}
	
	@Test
	public void validateBVoIPAvsaCall() {
		Mockito.when(env.getProperty("avsq.threadPool.size=12")).thenReturn("");
		ServiceValidationRequest svRequest = new ServiceValidationRequest();
		List<DesignConfiguration> designConfigurations = new ArrayList<>();
		DesignConfiguration designConfig = new DesignConfiguration();
		designConfig.setName("N1");
		designConfig.setValue("V1");
		designConfigurations.add(designConfig);

		List<ConfigurationDetails> configurationDetails = new ArrayList<>();
		ConfigurationDetails configDetails = new ConfigurationDetails();
		configDetails.setDocumentNumber("11");
		configDetails.setModelName("BVoIP");
		configDetails.setDesignConfiguration(designConfigurations);
		configurationDetails.add(configDetails);

		List<SiteDetails> siteDetailList = new ArrayList<>();
		SiteDetails siteDetails = new SiteDetails();
		siteDetails.setNxSiteId("1");
		siteDetails.setAddressLine("1919 McKinney Ave");
		siteDetails.setCity("Dallas");
		siteDetails.setState("TX");
		siteDetails.setPostalCode("75201");
		siteDetails.setConfigurationDetails(configurationDetails);
		siteDetailList.add(siteDetails);

		svRequest.setTransactionId(1L);
		svRequest.setOptyId("100");
		svRequest.setDealId(10L);
		svRequest.setSiteDetails(siteDetailList);
		NxMpDeal nxMpDeal = new NxMpDeal();
		nxMpDeal.setNxTxnId(1L);
		when(nxMpDealRepository.findByTransactionId(String.valueOf(svRequest.getTransactionId()))).thenReturn(nxMpDeal);
		NxMpSiteDictionary nxMpSiteDictionary = new NxMpSiteDictionary();
		nxMpSiteDictionary.setNxTxnId(nxMpDeal.getNxTxnId());
		String siteJson = "{ \"status\": \"COMPLETED\", \"locations\": [ { \"id\": 212, \"nxSiteId\": 238, \"locName\": \"Test 1\", \"street\": \"2111 Dickson Dr\", \"city\": \"Austin\", \"state\": \"TX\", \"zip\": \"78704\", \"validationStatus\": \"VALID\", \"avsqResponse\": { \"Location\": { \"GISLocationAttributes\": [ { \"globalLocationId\": \"00000837A5\", \"FieldedAddress\": { \"city\": \"AUSTIN\", \"state\": \"TX\", \"postalCode\": \"78704\", \"singleLineStandardizedAddress\": \"2111 DICKSON DR,AUSTIN,TX,78704-4796\", \"country\": \"USA\", \"postalCodePlus4\": \"4796\" }, \"LocationProperties\": { \"matchStatus\": \"M\", \"buildingClli\": \"AUSJTXIY\", \"regionFranchiseStatus\": \"Y\", \"addressMatchCode\": \"S80\", \"swcCLLI\": \"AUSTTXHI\" } } ] } } } ] }";
		nxMpSiteDictionary.setSiteJson(siteJson);
		when(nxMpSiteDictionaryRepository.findByNxTxnId(nxMpDeal.getNxTxnId())).thenReturn(nxMpSiteDictionary);
		avsqUtil.callAVSQ(svRequest, 1L, paramMap);
	}
	@Test
	public void validateATTCollaborateAvsaCall() {
		Mockito.when(env.getProperty("avsq.threadPool.size=12")).thenReturn("");
		ServiceValidationRequest svRequest = new ServiceValidationRequest();
		List<DesignConfiguration> designConfigurations = new ArrayList<>();
		DesignConfiguration designConfig = new DesignConfiguration();
		designConfig.setName("N1");
		designConfig.setValue("V1");
		designConfigurations.add(designConfig);

		List<ConfigurationDetails> configurationDetails = new ArrayList<>();
		ConfigurationDetails configDetails = new ConfigurationDetails();
		configDetails.setDocumentNumber("11");
		configDetails.setModelName("ATTCollaborate");
		configDetails.setDesignConfiguration(designConfigurations);
		configurationDetails.add(configDetails);

		List<SiteDetails> siteDetailList = new ArrayList<>();
		SiteDetails siteDetails = new SiteDetails();
		siteDetails.setNxSiteId("1");
		siteDetails.setAddressLine("1919 McKinney Ave");
		siteDetails.setCity("Dallas");
		siteDetails.setState("TX");
		siteDetails.setPostalCode("75201");
		siteDetails.setConfigurationDetails(configurationDetails);
		siteDetailList.add(siteDetails);

		svRequest.setTransactionId(1L);
		svRequest.setOptyId("100");
		svRequest.setDealId(10L);
		svRequest.setSiteDetails(siteDetailList);
		NxMpDeal nxMpDeal = new NxMpDeal();
		nxMpDeal.setNxTxnId(1L);
		when(nxMpDealRepository.findByTransactionId(String.valueOf(svRequest.getTransactionId()))).thenReturn(nxMpDeal);
		NxMpSiteDictionary nxMpSiteDictionary = new NxMpSiteDictionary();
		nxMpSiteDictionary.setNxTxnId(nxMpDeal.getNxTxnId());
		String siteJson = "{ \"status\": \"COMPLETED\", \"locations\": [ { \"id\": 212, \"nxSiteId\": 238, \"locName\": \"Test 1\", \"street\": \"2111 Dickson Dr\", \"city\": \"Austin\", \"state\": \"TX\", \"zip\": \"78704\", \"validationStatus\": \"VALID\", \"avsqResponse\": { \"Location\": { \"GISLocationAttributes\": [ { \"globalLocationId\": \"00000837A5\", \"FieldedAddress\": { \"city\": \"AUSTIN\", \"state\": \"TX\", \"postalCode\": \"78704\", \"singleLineStandardizedAddress\": \"2111 DICKSON DR,AUSTIN,TX,78704-4796\", \"country\": \"USA\", \"postalCodePlus4\": \"4796\" }, \"LocationProperties\": { \"matchStatus\": \"M\", \"buildingClli\": \"AUSJTXIY\", \"regionFranchiseStatus\": \"Y\", \"addressMatchCode\": \"S80\", \"swcCLLI\": \"AUSTTXHI\" } } ] } } } ] }";
		nxMpSiteDictionary.setSiteJson(siteJson);
		when(nxMpSiteDictionaryRepository.findByNxTxnId(nxMpDeal.getNxTxnId())).thenReturn(nxMpSiteDictionary);
		avsqUtil.callAVSQ(svRequest, 1L, paramMap);
	}
	
	
	@Test
	public void validateEPLSWANAvsaCall() {
		Mockito.when(env.getProperty("avsq.threadPool.size=12")).thenReturn("");
		ServiceValidationRequest svRequest = new ServiceValidationRequest();
		List<DesignConfiguration> designConfigurations = new ArrayList<>();
		DesignConfiguration designConfig = new DesignConfiguration();
		designConfig.setName("N1");
		designConfig.setValue("V1");
		designConfigurations.add(designConfig);

		List<ConfigurationDetails> configurationDetails = new ArrayList<>();
		ConfigurationDetails configDetails = new ConfigurationDetails();
		configDetails.setDocumentNumber("11");
		configDetails.setModelName("EPLS-WAN");
		configDetails.setDesignConfiguration(designConfigurations);
		configurationDetails.add(configDetails);

		List<SiteDetails> siteDetailList = new ArrayList<>();
		SiteDetails siteDetails = new SiteDetails();
		siteDetails.setNxSiteId("1");
		siteDetails.setAddressLine("1919 McKinney Ave");
		siteDetails.setCity("Dallas");
		siteDetails.setState("TX");
		siteDetails.setPostalCode("75201");
		siteDetails.setConfigurationDetails(configurationDetails);
		siteDetailList.add(siteDetails);

		svRequest.setTransactionId(1L);
		svRequest.setOptyId("100");
		svRequest.setDealId(10L);
		svRequest.setSiteDetails(siteDetailList);
		NxMpDeal nxMpDeal = new NxMpDeal();
		nxMpDeal.setNxTxnId(1L);
		when(nxMpDealRepository.findByTransactionId(String.valueOf(svRequest.getTransactionId()))).thenReturn(nxMpDeal);
		NxMpSiteDictionary nxMpSiteDictionary = new NxMpSiteDictionary();
		nxMpSiteDictionary.setNxTxnId(nxMpDeal.getNxTxnId());
		String siteJson = "{ \"status\": \"COMPLETED\", \"locations\": [ { \"id\": 212, \"nxSiteId\": 238, \"locName\": \"Test 1\", \"street\": \"2111 Dickson Dr\", \"city\": \"Austin\", \"state\": \"TX\", \"zip\": \"78704\", \"validationStatus\": \"VALID\", \"avsqResponse\": { \"Location\": { \"GISLocationAttributes\": [ { \"globalLocationId\": \"00000837A5\", \"FieldedAddress\": { \"city\": \"AUSTIN\", \"state\": \"TX\", \"postalCode\": \"78704\", \"singleLineStandardizedAddress\": \"2111 DICKSON DR,AUSTIN,TX,78704-4796\", \"country\": \"USA\", \"postalCodePlus4\": \"4796\" }, \"LocationProperties\": { \"matchStatus\": \"M\", \"buildingClli\": \"AUSJTXIY\", \"regionFranchiseStatus\": \"Y\", \"addressMatchCode\": \"S80\", \"swcCLLI\": \"AUSTTXHI\" } } ] } } } ] }";
		nxMpSiteDictionary.setSiteJson(siteJson);
		when(nxMpSiteDictionaryRepository.findByNxTxnId(nxMpDeal.getNxTxnId())).thenReturn(nxMpSiteDictionary);
		avsqUtil.callAVSQ(svRequest, 1L, paramMap);
	}
	
	@Test
	public void validateADIAvsaCall() {
		Mockito.when(env.getProperty("avsq.threadPool.size=12")).thenReturn("");
		ServiceValidationRequest svRequest = new ServiceValidationRequest();
		List<DesignConfiguration> designConfigurations = new ArrayList<>();
		DesignConfiguration designConfig = new DesignConfiguration();
		designConfig.setName("N1");
		designConfig.setValue("V1");
		designConfigurations.add(designConfig);

		List<ConfigurationDetails> configurationDetails = new ArrayList<>();
		ConfigurationDetails configDetails = new ConfigurationDetails();
		configDetails.setDocumentNumber("11");
		configDetails.setModelName("ADI");
		configDetails.setDesignConfiguration(designConfigurations);
		configurationDetails.add(configDetails);

		List<SiteDetails> siteDetailList = new ArrayList<>();
		SiteDetails siteDetails = new SiteDetails();
		siteDetails.setNxSiteId("1");
		siteDetails.setAddressLine("1919 McKinney Ave");
		siteDetails.setCity("Dallas");
		siteDetails.setState("TX");
		siteDetails.setPostalCode("75201");
		siteDetails.setConfigurationDetails(configurationDetails);
		siteDetailList.add(siteDetails);

		svRequest.setTransactionId(1L);
		svRequest.setOptyId("100");
		svRequest.setDealId(10L);
		svRequest.setSiteDetails(siteDetailList);
		NxMpDeal nxMpDeal = new NxMpDeal();
		nxMpDeal.setNxTxnId(1L);
		when(nxMpDealRepository.findByTransactionId(String.valueOf(svRequest.getTransactionId()))).thenReturn(nxMpDeal);
		NxMpSiteDictionary nxMpSiteDictionary = new NxMpSiteDictionary();
		nxMpSiteDictionary.setNxTxnId(nxMpDeal.getNxTxnId());
		String siteJson = "{ \"status\": \"COMPLETED\", \"locations\": [ { \"id\": 212, \"nxSiteId\": 238, \"locName\": \"Test 1\", \"street\": \"2111 Dickson Dr\", \"city\": \"Austin\", \"state\": \"TX\", \"zip\": \"78704\", \"validationStatus\": \"VALID\", \"avsqResponse\": { \"Location\": { \"GISLocationAttributes\": [ { \"globalLocationId\": \"00000837A5\", \"FieldedAddress\": { \"city\": \"AUSTIN\", \"state\": \"TX\", \"postalCode\": \"78704\", \"singleLineStandardizedAddress\": \"2111 DICKSON DR,AUSTIN,TX,78704-4796\", \"country\": \"USA\", \"postalCodePlus4\": \"4796\" }, \"LocationProperties\": { \"matchStatus\": \"M\", \"buildingClli\": \"AUSJTXIY\", \"regionFranchiseStatus\": \"Y\", \"addressMatchCode\": \"S80\", \"swcCLLI\": \"AUSTTXHI\" } } ] } } } ] }";
		nxMpSiteDictionary.setSiteJson(siteJson);
		when(nxMpSiteDictionaryRepository.findByNxTxnId(nxMpDeal.getNxTxnId())).thenReturn(nxMpSiteDictionary);
		avsqUtil.callAVSQ(svRequest, 1L, paramMap);
	}
	

	public void validateANIRAAvsaCall() {
		Mockito.when(env.getProperty("avsq.threadPool.size=12")).thenReturn("");
		ServiceValidationRequest svRequest = new ServiceValidationRequest();
		List<DesignConfiguration> designConfigurations = new ArrayList<>();
		DesignConfiguration designConfig = new DesignConfiguration();
		designConfig.setName("N1");
		designConfig.setValue("V1");
		designConfigurations.add(designConfig);

		List<ConfigurationDetails> configurationDetails = new ArrayList<>();
		ConfigurationDetails configDetails = new ConfigurationDetails();
		configDetails.setDocumentNumber("11");
		configDetails.setModelName("ANIRA");
		configDetails.setDesignConfiguration(designConfigurations);
		configurationDetails.add(configDetails);

		List<SiteDetails> siteDetailList = new ArrayList<>();
		SiteDetails siteDetails = new SiteDetails();
		siteDetails.setNxSiteId("1");
		siteDetails.setAddressLine("1919 McKinney Ave");
		siteDetails.setCity("Dallas");
		siteDetails.setState("TX");
		siteDetails.setPostalCode("75201");
		siteDetails.setConfigurationDetails(configurationDetails);
		siteDetailList.add(siteDetails);

		svRequest.setTransactionId(1L);
		svRequest.setOptyId("100");
		svRequest.setDealId(10L);
		svRequest.setSiteDetails(siteDetailList);
		NxMpDeal nxMpDeal = new NxMpDeal();
		nxMpDeal.setNxTxnId(1L);
		when(nxMpDealRepository.findByTransactionId(String.valueOf(svRequest.getTransactionId()))).thenReturn(nxMpDeal);
		NxMpSiteDictionary nxMpSiteDictionary = new NxMpSiteDictionary();
		nxMpSiteDictionary.setNxTxnId(nxMpDeal.getNxTxnId());
		String siteJson = "{ \"status\": \"COMPLETED\", \"locations\": [ { \"id\": 212, \"nxSiteId\": 238, \"locName\": \"Test 1\", \"street\": \"2111 Dickson Dr\", \"city\": \"Austin\", \"state\": \"TX\", \"zip\": \"78704\", \"validationStatus\": \"VALID\", \"avsqResponse\": { \"Location\": { \"GISLocationAttributes\": [ { \"globalLocationId\": \"00000837A5\", \"FieldedAddress\": { \"city\": \"AUSTIN\", \"state\": \"TX\", \"postalCode\": \"78704\", \"singleLineStandardizedAddress\": \"2111 DICKSON DR,AUSTIN,TX,78704-4796\", \"country\": \"USA\", \"postalCodePlus4\": \"4796\" }, \"LocationProperties\": { \"matchStatus\": \"M\", \"buildingClli\": \"AUSJTXIY\", \"regionFranchiseStatus\": \"Y\", \"addressMatchCode\": \"S80\", \"swcCLLI\": \"AUSTTXHI\" } } ] } } } ] }";
		nxMpSiteDictionary.setSiteJson(siteJson);
		when(nxMpSiteDictionaryRepository.findByNxTxnId(nxMpDeal.getNxTxnId())).thenReturn(nxMpSiteDictionary);
		avsqUtil.callAVSQ(svRequest, 1L, paramMap);
	}
	
	
	@Test
	public void validateOPTEWANAvsaCall() {
		Mockito.when(env.getProperty("avsq.threadPool.size=12")).thenReturn("");
		ServiceValidationRequest svRequest = new ServiceValidationRequest();
		List<DesignConfiguration> designConfigurations = new ArrayList<>();
		DesignConfiguration designConfig = new DesignConfiguration();
		designConfig.setName("N1");
		designConfig.setValue("V1");
		designConfigurations.add(designConfig);

		List<ConfigurationDetails> configurationDetails = new ArrayList<>();
		ConfigurationDetails configDetails = new ConfigurationDetails();
		configDetails.setDocumentNumber("11");
		configDetails.setModelName("OPT-E-WAN");
		configDetails.setDesignConfiguration(designConfigurations);
		configurationDetails.add(configDetails);

		List<SiteDetails> siteDetailList = new ArrayList<>();
		SiteDetails siteDetails = new SiteDetails();
		siteDetails.setNxSiteId("1");
		siteDetails.setAddressLine("1919 McKinney Ave");
		siteDetails.setCity("Dallas");
		siteDetails.setState("TX");
		siteDetails.setPostalCode("75201");
		siteDetails.setConfigurationDetails(configurationDetails);
		siteDetailList.add(siteDetails);

		svRequest.setTransactionId(1L);
		svRequest.setOptyId("100");
		svRequest.setDealId(10L);
		svRequest.setSiteDetails(siteDetailList);
		NxMpDeal nxMpDeal = new NxMpDeal();
		nxMpDeal.setNxTxnId(1L);
		when(nxMpDealRepository.findByTransactionId(String.valueOf(svRequest.getTransactionId()))).thenReturn(nxMpDeal);
		NxMpSiteDictionary nxMpSiteDictionary = new NxMpSiteDictionary();
		nxMpSiteDictionary.setNxTxnId(nxMpDeal.getNxTxnId());
		String siteJson = "{ \"status\": \"COMPLETED\", \"locations\": [ { \"id\": 212, \"nxSiteId\": 238, \"locName\": \"Test 1\", \"street\": \"2111 Dickson Dr\", \"city\": \"Austin\", \"state\": \"TX\", \"zip\": \"78704\", \"validationStatus\": \"VALID\", \"avsqResponse\": { \"Location\": { \"GISLocationAttributes\": [ { \"globalLocationId\": \"00000837A5\", \"FieldedAddress\": { \"city\": \"AUSTIN\", \"state\": \"TX\", \"postalCode\": \"78704\", \"singleLineStandardizedAddress\": \"2111 DICKSON DR,AUSTIN,TX,78704-4796\", \"country\": \"USA\", \"postalCodePlus4\": \"4796\" }, \"LocationProperties\": { \"matchStatus\": \"M\", \"buildingClli\": \"AUSJTXIY\", \"regionFranchiseStatus\": \"Y\", \"addressMatchCode\": \"S80\", \"swcCLLI\": \"AUSTTXHI\" } } ] } } } ] }";
		nxMpSiteDictionary.setSiteJson(siteJson);
		when(nxMpSiteDictionaryRepository.findByNxTxnId(nxMpDeal.getNxTxnId())).thenReturn(nxMpSiteDictionary);
		avsqUtil.callAVSQ(svRequest, 1L, paramMap);
	}
	
	
	
	
	
	
	
	
}
