package com.att.sales.nexxus.serviceValidation.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxMpSiteDictionary;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxMpSiteDictionaryRepository;
import com.att.sales.nexxus.serviceValidation.model.ConfigurationDetails;
import com.att.sales.nexxus.serviceValidation.model.DesignConfiguration;
import com.att.sales.nexxus.serviceValidation.model.ServiceValidationRequest;
import com.att.sales.nexxus.serviceValidation.model.SiteDetails;
import com.att.sales.nexxus.serviceValidation.model.UpdateTransSitesServiceUpdateRequest;
import com.att.sales.nexxus.serviceValidation.model.UpdateTransSitesServiceUpdateResponse;

/**
 * @author KumariMuktta
 *
 */
@ExtendWith(MockitoExtension.class)
public class ServiceValidationImplTest {

	@Spy
	@InjectMocks
	ServiceValidationImpl serviceValidationImpl;

	@Mock
	AVSQUtil aVSQUtil;

	@Mock
	NxMpDealRepository nxMpDealRepository;

	@Mock
	NxMpSiteDictionaryRepository nxMpSiteDictionaryRepository;

	@Mock
	UpdateTransactionSitesServiceUpdate siteServiceUpdate;
	
	@BeforeEach
	public void initializeServiceMetaData() {
		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(ServiceMetaData.OFFER, "AVPN");
		requestParams.put(ServiceMetaData.REST_REQUEST_START_TIME, System.currentTimeMillis());
		requestParams.put(ServiceMetaData.VERSION, "1.0");
		requestParams.put(ServiceMetaData.METHOD, "TestMethod");
		requestParams.put(ServiceMetaData.URI, "TestUri");
		ServiceMetaData.add(requestParams);
	}

	@Disabled
	@Test
	public void validateServiceTest() throws SalesBusinessException {

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
		siteDetails.setNxSiteId("238");
		siteDetails.setAddressLine("");
		siteDetails.setCity("Dallas");
		siteDetails.setState("TX");
		siteDetails.setPostalCode("75201");
		siteDetails.setConfigurationDetails(configurationDetails);
		siteDetailList.add(siteDetails);

		ServiceValidationRequest request = new ServiceValidationRequest();
		request.setTransactionId(1L);
		request.setOptyId("100");
		request.setDealId(10L);
		request.setSiteDetails(siteDetailList);

		UpdateTransSitesServiceUpdateResponse response = new UpdateTransSitesServiceUpdateResponse();
		NxMpSiteDictionary nxMpSiteDictionary = new NxMpSiteDictionary();
		String siteJson = "{\"status\":\"COMPLETED\",\"locations\":[{\"id\":212,\"nxSiteId\":238,\"locName\":\"Test 1\",\"street\":\"2111 Dickson Dr\",\"city\":\"Austin\",\"state\":\"TX\",\"zip\":\"78704\",\"validationStatus\":\"VALID\",\"avsqResponse\":{\"QualifiedProducts\":{\"ASE\":{\"aseAvailabilityFlag\":false,\"FiberAvailabilityResults\":{\"fiberAvailabilityFlag\":false,\"ProductFTTB\":{\"FTTBData\":{\"buildingFiberStatus\":{\"color\":\"GREEN\",\"code\":\"E\"}}},\"ProductASE\":[{\"aggregatorType\":\"SEMUX\",\"aseColorCode\":\"GREEN\"},{\"aggregatorType\":\"IPAG2X\",\"aseColorCode\":\"GREEN\"}],\"FiberDataSummary\":{\"fiberLitIndicator\":true,\"pricingTier\":\"Tier 1\",\"FiberAerialLoopDistance\":{\"quantity\":0.35,\"unit\":\"MILES\"}}}}},\"Location\":{\"GISLocationAttributes\":[{\"globalLocationId\":\"00000837A5\",\"FieldedAddress\":{\"city\":\"AUSTIN\",\"state\":\"TX\",\"postalCode\":\"78704\",\"singleLineStandardizedAddress\":\"2111 DICKSON DR,AUSTIN,TX,78704-4796\",\"country\":\"USA\",\"postalCodePlus4\":\"4796\"},\"LocationProperties\":{\"matchStatus\":\"M\",\"buildingClli\":\"AUSJTXIY\",\"regionFranchiseStatus\":\"Y\",\"addressMatchCode\":\"S80\",\"swcCLLI\":\"AUSTTXHI\"}}]}}}]}";
		nxMpSiteDictionary.setNxTxnId(1L);
		nxMpSiteDictionary.setSiteJson(siteJson);
		NxMpDeal nxMpDeal = new NxMpDeal();
		nxMpDeal.setNxTxnId(1L);

		doNothing().when(aVSQUtil).callAVSQ(Mockito.any(), eq(nxMpSiteDictionary.getNxTxnId()), anyMap());
		Mockito.when(nxMpDealRepository.findByTransactionId("1")).thenReturn(nxMpDeal);
		Mockito.when(nxMpSiteDictionaryRepository.findByNxTxnId(1L)).thenReturn(nxMpSiteDictionary);
		Mockito.when(siteServiceUpdate.sitesServiceUpdate(any(UpdateTransSitesServiceUpdateRequest.class), eq(1L), anyMap()))
				.thenReturn(response);
		serviceValidationImpl.validateService(request);
	}
	
}
