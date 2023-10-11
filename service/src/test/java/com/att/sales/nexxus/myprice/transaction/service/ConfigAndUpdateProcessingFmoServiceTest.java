package com.att.sales.nexxus.myprice.transaction.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxDesign;
import com.att.sales.nexxus.dao.model.NxDesignDetails;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.handlers.ConfigureDesignWSHandlerFmo;
import com.att.sales.nexxus.handlers.ConfigureSolnAndProductWSHandlerFmo;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
@ExtendWith(MockitoExtension.class)

public class ConfigAndUpdateProcessingFmoServiceTest {
	
	@InjectMocks
	private ConfigAndUpdateProcessingFmoService configAndUpdateProcessingFmoService;
	
	@Mock
	private MyPriceTransactionUtil myPriceTransactionUtil;
	
	@Mock
	private NxMpDealRepository nxMpDealRepository;
	
	@Mock
	private ConfigureSolnAndProductWSHandlerFmo configSolutionHandler;
	
	@Mock
	private ConfigureDesignWSHandlerFmo configureDesignHandler;
	
	
	@Mock
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Mock
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	private Map<String, Object> paramMap;
	
	private NxDesign nxDesign;
	@BeforeEach
	public void init() {
		paramMap = new HashMap<String, Object>();
		nxDesign = new NxDesign();
		nxDesign.setNxDesignId(101l);
		nxDesign.setBundleCd("AVPN");
		List<NxDesignDetails> designs = new ArrayList<>();
		NxDesignDetails design = new NxDesignDetails();
		design.setType("AVPN/TDM");
		design.setProductName("AVPN");
		design.setDesignData("{\"country\":\"US\",\"nxSiteId\":5185,\"zipCode\":\"35111\",\"address\":null,\"referenceOfferId\":4,\"city\":\"MC CALLA\",\"address2\":null,\"address1\":\"12583 Thompson Blvd\",\"postalCode\":null,\"siteName\":\"MCCALLA AREA\",\"siteNpanxx\":null,\"regionCode\":null,\"swcClli\":\"BSMRALBU\",\"siteId\":8369220,\"state\":\"AL\",\"customerLocationClli\":null,\"design\":[{\"country\":\"US\",\"currencyCd\":\"USD\",\"portProtocol\":\"PPP\",\"accessSpeedUdfAttrId\":30375,\"accessDetails\":{\"supplierName\":null,\"npanxx\":null,\"nrcListRate\":null,\"serialNumber\":null,\"tokenId\":null,\"mrcListRate\":null,\"respAccessInterconnect\":null,\"dqid\":null,\"portId\":null,\"speed\":null,\"quoteId\":null,\"iglooMaxMrcDiscount\":null,\"respSpeed\":null,\"popClli\":null,\"respSupplierName\":null,\"currencyCode\":null,\"respPopClli\":null},\"portId\":8925266,\"interface\":null,\"referenceSiteId\":8369220,\"lac\":null,\"accessSpeed\":\"1.544 mb\",\"accessType\":\"Private Line\",\"accessArchitecture\":null,\"accessTypeUdfAttrId\":30155,\"categoryLocalAccess\":null,\"portSpeed\":\"1.544M\",\"priceDetails\":[{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999945449,\"quantity\":1.0,\"priceType\":\"portNRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":0,\"frequency\":\"NRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18030\",\"componentParentId\":8369220,\"productRateId\":930,\"reqPriceType\":\"Fixed\",\"term\":36,\"localListPrice\":1000.0,\"referencePortId\":8925266},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999945449,\"secondaryKeys\":\"#FCC#MPLS Port#Flat Rate#1.544M/1.536M#T1, E1#FR, ATM, IP#VPN Transport Connection#per port#17986#18030#United States#US#USA\",\"quantity\":1.0,\"lineItemId\":6418989,\"priceType\":\"portRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":77,\"frequency\":\"MRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"17986\",\"nrcBeid\":\"18030\",\"componentParentId\":8369220,\"productRateId\":884,\"reqPriceType\":null,\"term\":36,\"elementType\":\"Port\",\"uniqueId\":\"#MPLS Port#MPLS Port - 1.544M/1.536M#ATM#1.544Mbps/1.536Mbps#T1/E1#VPN Transport#Connection#Each\",\"localListPrice\":221.0,\"referencePortId\":8925266},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999945449,\"quantity\":1.0,\"priceType\":null,\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":99,\"frequency\":\"NRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18425\",\"componentParentId\":8369220,\"productRateId\":1498,\"reqPriceType\":null,\"term\":36,\"localListPrice\":0.0,\"referencePortId\":8925266},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999945449,\"secondaryKeys\":\"#FCC#CoS Package#Multimedia Standard Svc#1.544M/1.536M#N/A#N/A#VPN Transport COS Package#per port#18265#18425#United States#US#USA\",\"quantity\":1.0,\"lineItemId\":6418808,\"priceType\":null,\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":77,\"frequency\":\"MRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18265\",\"nrcBeid\":\"18425\",\"componentParentId\":8369220,\"productRateId\":1297,\"reqPriceType\":null,\"term\":36,\"elementType\":\"PortFeature\",\"uniqueId\":\"#CoS Package Multimedia Standard#CoS Package Multimedia Standard Svc - 1.544M/1.536M#ATM#1.544Mbps/1.536Mbps#T1/E1#VPN Transport#COS Package#Port\",\"localListPrice\":0.0,\"referencePortId\":8925266}],\"accessTailTechnology\":null,\"mileage\":null,\"siteType\":null}]}");
		designs.add(design);
		nxDesign.setNxDesignDetails(designs);
	}

	@Test
	public void testCallConfigSolutionAndDesign() throws SalesBusinessException {
		Mockito.when(configSolutionHandler.initiateConfigSolnAndProdWebService(anyMap(), anyString())).thenReturn(true);
		Mockito.when(configureDesignHandler.initiateConfigDesignWebService(anyMap(), anyString())).thenReturn(true);
		configAndUpdateProcessingFmoService.callConfigSolutionAndDesign(nxDesign, paramMap);
	}
	
	@Test
	public void testCallConfigureSolutionProductExc() throws SalesBusinessException {
		Mockito.when(configSolutionHandler.initiateConfigSolnAndProdWebService(anyMap(), anyString())).thenThrow(new SalesBusinessException());
		configAndUpdateProcessingFmoService.callConfigureSolutionProduct(null , paramMap);
	}
	
	@Test
	public void testCallConfigureDesignExc() throws SalesBusinessException {
		Mockito.when(configureDesignHandler.initiateConfigDesignWebService(anyMap(), anyString())).thenThrow(new SalesBusinessException());
		configAndUpdateProcessingFmoService.callConfigureDesign(null , paramMap);
	}
	
	@Test
	public void testGetDealBySolutionId() {
		List<NxMpDeal> deals = new ArrayList<>();
		deals.add(new NxMpDeal());
		Mockito.when(nxMpDealRepository.findBySolutionIdAndActiveYN(anyLong(), anyString())).thenReturn(deals);
		configAndUpdateProcessingFmoService.getDealBySolutionId(10101l);
		
	}
	
	@Test
	public void testGetAccessType() {
		Map<String,List<String>> productInfoMap = new HashMap<String,List<String>>();
		List<String> list = new ArrayList<>();
		list.add("ADI");
		list.add("ADI/TDM");
		productInfoMap.put("product", list);
		paramMap.put(MyPriceConstants.FMO_ACCESS_PRODUCT_NAME_DATA_MAP, productInfoMap);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode design = null;
		try {
			design = mapper.readTree(nxDesign.getNxDesignDetails().get(0).getDesignData());
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String,NxLookupData> resultMap = new HashMap<>();
		NxLookupData lookup = new NxLookupData();
		lookup.setDescription("TDM");
		resultMap.put("30155", lookup);
		Mockito.when(nxMyPriceRepositoryServce.getLookupDataByItemId(anyString())).thenReturn(resultMap);
		configAndUpdateProcessingFmoService.getAccessType(design, "ADI", paramMap);
	}
	
	@Test
	public void testGetDataMapFromLookup() {
		List<NxLookupData> lookUps = new ArrayList<>();
		NxLookupData lookup = new NxLookupData();
		lookup.setDescription("176477S2403-S2404");
		lookup.setCriteria("176477S2403,176477S2404");
		lookup.setItemId("176477S2403-S2404");
		lookUps.add(lookup);
		Mockito.when(nxLookupDataRepository.findByDatasetName(anyString())).thenReturn(lookUps);
		configAndUpdateProcessingFmoService.getDataMapFromLookup("FMO_DOMESTIC_ACCESS_TYPE");
	}
	
	@Test
	public void testGetOfferNameForNxDesign() {
		List<String> requestProduts = new ArrayList<>();
		requestProduts.add("ADI");
		requestProduts.add("ADI/TDM");
		configAndUpdateProcessingFmoService.getOfferNameForNxDesign(requestProduts);
	}
	
	@Test
	public void testGetBvoipOfferName() {
		configAndUpdateProcessingFmoService.getBvoipOfferName("BVOIP");
		configAndUpdateProcessingFmoService.getBvoipOfferName("AVPN");
	}
	
	@Test
	public void testGetAccessTypeNonUs() {
		Map<String,List<String>> productInfoMap = new HashMap<String,List<String>>();
		List<String> list = new ArrayList<>();
		list.add("AVPN");
		list.add("AVPN/TDM");
		productInfoMap.put("product", list);
		paramMap.put(MyPriceConstants.FMO_ACCESS_PRODUCT_NAME_DATA_MAP, productInfoMap);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode design = null;
		try {
			design = mapper.readTree("{\"country\":\"GM\",\"nxSiteId\":5185,\"zipCode\":\"35111\",\"address\":null,\"referenceOfferId\":4,\"city\":\"MC CALLA\",\"address2\":null,\"address1\":\"12583 Thompson Blvd\",\"postalCode\":null,\"siteName\":\"MCCALLA AREA\",\"siteNpanxx\":null,\"regionCode\":null,\"swcClli\":\"BSMRALBU\",\"siteId\":8369220,\"state\":\"AL\",\"customerLocationClli\":null,\"design\":[{\"country\":\"US\",\"currencyCd\":\"USD\",\"portProtocol\":\"PPP\",\"accessSpeedUdfAttrId\":30375,\"accessDetails\":{\"supplierName\":null,\"npanxx\":null,\"nrcListRate\":null,\"serialNumber\":null,\"tokenId\":null,\"mrcListRate\":null,\"respAccessInterconnect\":null,\"dqid\":null,\"portId\":null,\"speed\":null,\"quoteId\":null,\"iglooMaxMrcDiscount\":null,\"respSpeed\":null,\"popClli\":null,\"respSupplierName\":null,\"currencyCode\":null,\"respPopClli\":null},\"portId\":8925266,\"interface\":null,\"referenceSiteId\":8369220,\"lac\":null,\"accessSpeed\":\"1.544 mb\",\"accessType\":\"Private Line\",\"accessArchitecture\":null,\"accessTypeUdfAttrId\":30155,\"categoryLocalAccess\":null,\"portSpeed\":\"1.544M\",\"priceDetails\":[{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999945449,\"quantity\":1.0,\"priceType\":\"portNRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":0,\"frequency\":\"NRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18030\",\"componentParentId\":8369220,\"productRateId\":930,\"reqPriceType\":\"Fixed\",\"term\":36,\"localListPrice\":1000.0,\"referencePortId\":8925266},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999945449,\"secondaryKeys\":\"#FCC#MPLS Port#Flat Rate#1.544M/1.536M#T1, E1#FR, ATM, IP#VPN Transport Connection#per port#17986#18030#United States#US#USA\",\"quantity\":1.0,\"lineItemId\":6418989,\"priceType\":\"portRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":77,\"frequency\":\"MRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"17986\",\"nrcBeid\":\"18030\",\"componentParentId\":8369220,\"productRateId\":884,\"reqPriceType\":null,\"term\":36,\"elementType\":\"Port\",\"uniqueId\":\"#MPLS Port#MPLS Port - 1.544M/1.536M#ATM#1.544Mbps/1.536Mbps#T1/E1#VPN Transport#Connection#Each\",\"localListPrice\":221.0,\"referencePortId\":8925266},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999945449,\"quantity\":1.0,\"priceType\":null,\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":99,\"frequency\":\"NRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18425\",\"componentParentId\":8369220,\"productRateId\":1498,\"reqPriceType\":null,\"term\":36,\"localListPrice\":0.0,\"referencePortId\":8925266},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999945449,\"secondaryKeys\":\"#FCC#CoS Package#Multimedia Standard Svc#1.544M/1.536M#N/A#N/A#VPN Transport COS Package#per port#18265#18425#United States#US#USA\",\"quantity\":1.0,\"lineItemId\":6418808,\"priceType\":null,\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":77,\"frequency\":\"MRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18265\",\"nrcBeid\":\"18425\",\"componentParentId\":8369220,\"productRateId\":1297,\"reqPriceType\":null,\"term\":36,\"elementType\":\"PortFeature\",\"uniqueId\":\"#CoS Package Multimedia Standard#CoS Package Multimedia Standard Svc - 1.544M/1.536M#ATM#1.544Mbps/1.536Mbps#T1/E1#VPN Transport#COS Package#Port\",\"localListPrice\":0.0,\"referencePortId\":8925266}],\"accessTailTechnology\":null,\"mileage\":null,\"siteType\":null}]}");
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String,NxLookupData> resultMap = new HashMap<>();
		NxLookupData lookup = new NxLookupData();
		lookup.setDescription("TDM");
		resultMap.put("30155", lookup);
		Mockito.when(nxMyPriceRepositoryServce.getLookupDataByItemId(anyString())).thenReturn(resultMap);
		configAndUpdateProcessingFmoService.getAccessType(design, "AVPN", paramMap);
	}
}
