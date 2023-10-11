package com.att.sales.nexxus.myprice.transaction.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyString;

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
import com.att.sales.nexxus.dao.model.NxAccessPricingData;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.repository.NxAccessPricingDataRepository;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.handlers.ConfigureDesignWSHandlerIgloo;
import com.att.sales.nexxus.handlers.ConfigureSolnAndProductWSHandlerIgloo;
import com.att.sales.nexxus.myprice.transaction.model.CreateTransactionResponse;

@ExtendWith(MockitoExtension.class)

public class ConfigAndUpdateProcessingIglooServiceTest {
	
	@InjectMocks
	private ConfigAndUpdateProcessingIglooService configAndUpdateProcessingIglooService;
	
	@Mock
	private NxMpDealRepository nxMpDealRepository;
	
	@Mock
	private ConfigureSolnAndProductWSHandlerIgloo configureSolnAndProductWSHandlerIgloo;
	
	@Mock
	private NxAccessPricingDataRepository accessPricingDataRepository;
	
	@Mock
	private ConfigureDesignWSHandlerIgloo configureDesignWSHandlerIgloo;
	
	private CreateTransactionResponse response;
	
	private NxAccessPricingData accessPricingData;
	
	private Map<String, Object> paramMap;
	
	@BeforeEach
	public void init() {
		response = new CreateTransactionResponse();
		response.setMyPriceTransacId("1111111");
		response.setNxTransacId(123l);
		accessPricingData = new NxAccessPricingData();
		accessPricingData.setLocationYn("New");
		accessPricingData.setNxAccessPriceId(111l);
		accessPricingData.setIntermediateJson("{\"address\":\"8665 CYPRESS WATERS BLVD\",\"city\":\"IRVING\",\"country\":\"United States\",\"reqVendor\":\"SBC\",\"attEthPop\":\"DLLSTXTL\",\"service\":\"AVPN-ETH\",\"bandwidth\":\"20\",\"accessArch\":\"Switched\",\"physicalInterface\":\"100BaseTX Electrical\",\"baseMonthlyRecurringPrice\":\"906.00\",\"flowType\":\"IGL\"}");
		paramMap = new HashMap<String, Object>();
	}

	@Test
	public void testCallConfigSolutionAndDesignUs() throws SalesBusinessException {
		
		Mockito.when(nxMpDealRepository.getCountryCodeByCountryIsoCode(anyString())).thenReturn("US");
		Mockito.when(configureSolnAndProductWSHandlerIgloo.initiateConfigSolnAndProdWebService(any(), anyMap())).thenReturn(true);
		Mockito.when(accessPricingDataRepository.findByNxAccessPriceId(anyLong())).thenReturn(accessPricingData);
		Mockito.when(accessPricingDataRepository.saveAndFlush(any(NxAccessPricingData.class))).thenReturn(accessPricingData);
		Mockito.when(configureDesignWSHandlerIgloo.initiateConfigDesignWebService(any(), anyMap())).thenReturn(true);
		configAndUpdateProcessingIglooService.callConfigSolutionAndDesign(response, accessPricingData, paramMap);
		
	}
	
	@Test
	public void testCallConfigSolutionAndDesignMow() throws SalesBusinessException {
		accessPricingData.setLocationYn("Existing");
		accessPricingData.setIntermediateJson("{\"city\":\" FRANKFURT \",\"custPostalcode\":\"65936\",\"country\":\"GERMANY\",\"clli\":\"FRNKGEFF\",\"currency\":\"EUR\",\"accessBandwidth\":\"10000\",\"technology\":\"Ethernet (Gateway Interconnect/ESP ETH Shared)\",\"serialNumber\":\"627309_DE_AVPN-ETH_2012475_3596343\",\"service\":\"AVPN-ETH\",\"flowType\":\"IGL\"}");
		Mockito.when(nxMpDealRepository.getCountryCodeByCountryIsoCode(anyString())).thenReturn("GM");
		Mockito.when(configureSolnAndProductWSHandlerIgloo.initiateConfigSolnAndProdWebService(any(), anyMap())).thenReturn(true);
		Mockito.when(accessPricingDataRepository.findByNxAccessPriceId(anyLong())).thenReturn(accessPricingData);
		Mockito.when(accessPricingDataRepository.saveAndFlush(any(NxAccessPricingData.class))).thenReturn(accessPricingData);
		Mockito.when(configureDesignWSHandlerIgloo.initiateConfigDesignWebService(any(), anyMap())).thenReturn(true);
		configAndUpdateProcessingIglooService.callConfigSolutionAndDesign(response, accessPricingData, paramMap);
	}
	
	@Test
	public void testCallConfigureSolutionProductExc() throws SalesBusinessException {
		Mockito.when(configureSolnAndProductWSHandlerIgloo.initiateConfigSolnAndProdWebService(any(), anyMap())).thenThrow(new SalesBusinessException());
		configAndUpdateProcessingIglooService.callConfigureSolutionProduct(null , paramMap);
	}
	
	@Test
	public void testCallConfigureDesignExc() throws SalesBusinessException {
		Mockito.when(configureDesignWSHandlerIgloo.initiateConfigDesignWebService(any(), anyMap())).thenThrow(new SalesBusinessException());
		configAndUpdateProcessingIglooService.callConfigureDesign(null , paramMap);
	}
	
	@Test
	public void testGgetProductName() {
		Map<String,List<String>> configProductInfoMap = new HashMap<String,List<String>>();
		List<String> list = new ArrayList<>();
		list.add("IGLOO");
		configProductInfoMap.put("product", list);
		configAndUpdateProcessingIglooService.getProductName(configProductInfoMap, "IGLOO");
	}
	
	@Test
	public void testGetDealBySolutionId() {
		List<NxMpDeal> deals = new ArrayList<>();
		deals.add(new NxMpDeal());
		Mockito.when(nxMpDealRepository.findBySolutionIdAndActiveYN(anyLong(), anyString())).thenReturn(deals);
		configAndUpdateProcessingIglooService.getDealBySolutionId(10101l);
		
	}
}
