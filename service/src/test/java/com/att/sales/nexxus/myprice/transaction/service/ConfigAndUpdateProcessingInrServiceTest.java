/**
 * 
 */
package com.att.sales.nexxus.myprice.transaction.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxInrDesign;
import com.att.sales.nexxus.dao.model.NxInrDesignDetails;
import com.att.sales.nexxus.handlers.ConfigureDesignWSHandlerInr;
import com.att.sales.nexxus.handlers.ConfigureSolnAndProductWSHandlerInr;
import com.att.sales.nexxus.myprice.transaction.model.CreateTransactionResponse;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTransactionPricingResponse;
import com.att.sales.nexxus.util.JacksonUtil;

/**
 * @author ShruthiCJ
 *
 */
@ExtendWith(MockitoExtension.class)

public class ConfigAndUpdateProcessingInrServiceTest {
	
	@InjectMocks
	private ConfigAndUpdateProcessingInrService configAndUpdateProcessingInrService;
	
	private CreateTransactionResponse response;
	
	private NxInrDesign nxInrDesign;
	
	private Map<String, Object> paramMap;
	
	@Mock
	private ConfigAndUpdatePricingUtilInr configAndUpdatePricingUtilInr;
	
	@Mock
	private ConfigureSolnAndProductWSHandlerInr configureSolnAndProductWSHandlerInr;
	
	@Mock
	private MyPriceTransactionUtil myPriceTransactionUtil;
	
	@Mock
	private ConfigureDesignWSHandlerInr configureDesignWSHandlerInr;
	
	@Mock
	private UpdateTransactionPricingInrServiceImpl updateTransactionPricingInrService;
	
	@BeforeEach
	public void init() {
		response = new CreateTransactionResponse();
		response.setMyPriceTransacId("1010101");
		nxInrDesign = new NxInrDesign();
		nxInrDesign.setNxInrDesignId(1010l);
		List<NxInrDesignDetails> designs = new ArrayList<NxInrDesignDetails>();
		NxInrDesignDetails design = new NxInrDesignDetails();
		design.setNxInrDesign(nxInrDesign);
		design.setProduct("AVPN");
		design.setSubProduct("LocalAccess");
		design.setDesignData("{\"siteId\":\"90923699\",\"address\":\"4 HEATHROW BOULEVARD 280 BATH ROAD\",\"city\":\"WEST DRAYTON\",\"country\":\"GB\",\"custPostalcode\":\"UB70DQ\",\"design\":[{\"siteName\":\"3038829\",\"portSpeed\":\"20000\",\"priceDetails\":[{\"priceType\":\"PORTBEID\",\"beid\":\"18007\",\"quantity\":\"1\",\"localListPrice\":\"910\",\"actualPrice\":\"136.5\",\"secondaryKey\":\"#Intl#MPLS Port#Flat Rate#20M#Enet, T3, E3#Enet, FR, ATM, IP#VPN Transport Connection#per port#18007#18030\",\"elementType\":\"Port\",\"uniqueId\":\"#MPLS Port#MPLS Port - 20M#ATM#20Mbps#ENET#VPN Transport#Connection#Each\"},{\"priceType\":\"DIVERSITYBEID\",\"beid\":\"18128\",\"quantity\":\"1\",\"localListPrice\":\"30\",\"actualPrice\":\"21\",\"secondaryKey\":\"#Intl#Port Option#POP Diversity#20M#Enet, T3, E3#Enet, ATM, IP, FR#VPN Transport Connection#per port#18128#18151\",\"elementType\":\"PortFeature\",\"uniqueId\":\"#POP Diversity#MPLS Port POP Diversity - 20M#ATM#20Mbps#ENET#VPN Transport#Connection#Each\"},{\"priceType\":\"ACCESSBEID\",\"beid\":\"19770\",\"quantity\":\"1\",\"localListPrice\":\"385.24\",\"actualPrice\":\"385.24\"}],\"circuitId\":\"MMYF106085ATI\",\"accessBandwidth\":\"20000 Kbps\",\"clli\":\"WKNGEN13\",\"technology\":\"Ethernet (Gateway Interconnect/ESP ETH Shared)\",\"accessCarrier\":\"GB-SAREAQ\",\"accessSecondaryKey\":\"#Intl#United Kingdom#GB#AVPN#N/A#N/A\",\"nxSiteId\":35501},{\"siteName\":\"3038829\",\"portSpeed\":\"20000\",\"priceDetails\":[{\"priceType\":\"PORTBEID\",\"beid\":\"18007\",\"quantity\":\"1\",\"localListPrice\":\"910\",\"actualPrice\":\"136.5\",\"secondaryKey\":\"#Intl#MPLS Port#Flat Rate#20M#Enet, T3, E3#Enet, FR, ATM, IP#VPN Transport Connection#per port#18007#18030\",\"elementType\":\"Port\",\"uniqueId\":\"#MPLS Port#MPLS Port - 20M#ATM#20Mbps#ENET#VPN Transport#Connection#Each\"},{\"priceType\":\"DIVERSITYBEID\",\"beid\":\"18128\",\"quantity\":\"1\",\"localListPrice\":\"30\",\"actualPrice\":\"21\",\"secondaryKey\":\"#Intl#Port Option#POP Diversity#20M#Enet, T3, E3#Enet, ATM, IP, FR#VPN Transport Connection#per port#18128#18151\",\"elementType\":\"PortFeature\",\"uniqueId\":\"#POP Diversity#MPLS Port POP Diversity - 20M#ATM#20Mbps#ENET#VPN Transport#Connection#Each\"},{\"priceType\":\"ACCESSBEID\",\"beid\":\"19770\",\"quantity\":\"1\",\"localListPrice\":\"385.24\",\"actualPrice\":\"385.24\"},{\"priceType\":\"COSBEID\",\"beid\":\"18286\",\"quantity\":\"1\",\"localListPrice\":\"0\",\"actualPrice\":\"0\",\"secondaryKey\":\"#Intl#CoS Package#Multimedia Standard Svc#20M#VPN Transport COS Package#per port#18286#18425\",\"elementType\":\"PortFeature\",\"uniqueId\":\"#CoS Package Multimedia Standard#CoS Package Multimedia Standard Svc - 20M#ENET#20Mbps#ENET#VPN Transport#COS Package#Port\"}],\"circuitId\":\"MMYF106085ATI\",\"accessBandwidth\":\"20000 Kbps\",\"clli\":\"WKNGEN13\",\"technology\":\"Ethernet (Gateway Interconnect/ESP ETH Shared)\",\"accessCarrier\":\"GB-SAREAQ\",\"accessSecondaryKey\":\"#Intl#United Kingdom#GB#AVPN#N/A#N/A\",\"nxSiteId\":35501},{\"siteName\":\"3038829\",\"portSpeed\":\"20000\",\"priceDetails\":[{\"priceType\":\"PORTBEID\",\"beid\":\"18007\",\"quantity\":\"1\",\"localListPrice\":\"910\",\"actualPrice\":\"136.5\",\"secondaryKey\":\"#Intl#MPLS Port#Flat Rate#20M#Enet, T3, E3#Enet, FR, ATM, IP#VPN Transport Connection#per port#18007#18030\",\"elementType\":\"Port\",\"uniqueId\":\"#MPLS Port#MPLS Port - 20M#ATM#20Mbps#ENET#VPN Transport#Connection#Each\"},{\"priceType\":\"DIVERSITYBEID\",\"beid\":\"18128\",\"quantity\":\"1\",\"localListPrice\":\"30\",\"actualPrice\":\"21\",\"secondaryKey\":\"#Intl#Port Option#POP Diversity#20M#Enet, T3, E3#Enet, ATM, IP, FR#VPN Transport Connection#per port#18128#18151\",\"elementType\":\"PortFeature\",\"uniqueId\":\"#POP Diversity#MPLS Port POP Diversity - 20M#ATM#20Mbps#ENET#VPN Transport#Connection#Each\"},{\"priceType\":\"ACCESSBEID\",\"beid\":\"19770\",\"quantity\":\"1\",\"localListPrice\":\"385.24\",\"actualPrice\":\"385.24\"}],\"circuitId\":\"MMYF106085ATI\",\"accessBandwidth\":\"20000 Kbps\",\"clli\":\"WKNGEN13\",\"technology\":\"Ethernet (Gateway Interconnect/ESP ETH Shared)\",\"accessCarrier\":\"GB-SAREAQ\",\"accessSecondaryKey\":\"#Intl#United Kingdom#GB#AVPN#N/A#N/A\",\"nxSiteId\":35501},{\"siteName\":\"3038829\",\"portSpeed\":\"20000\",\"priceDetails\":[{\"priceType\":\"PORTBEID\",\"beid\":\"18007\",\"quantity\":\"1\",\"localListPrice\":\"910\",\"actualPrice\":\"136.5\",\"secondaryKey\":\"#Intl#MPLS Port#Flat Rate#20M#Enet, T3, E3#Enet, FR, ATM, IP#VPN Transport Connection#per port#18007#18030\",\"elementType\":\"Port\",\"uniqueId\":\"#MPLS Port#MPLS Port - 20M#ATM#20Mbps#ENET#VPN Transport#Connection#Each\"},{\"priceType\":\"DIVERSITYBEID\",\"beid\":\"18128\",\"quantity\":\"1\",\"localListPrice\":\"30\",\"actualPrice\":\"21\",\"secondaryKey\":\"#Intl#Port Option#POP Diversity#20M#Enet, T3, E3#Enet, ATM, IP, FR#VPN Transport Connection#per port#18128#18151\",\"elementType\":\"PortFeature\",\"uniqueId\":\"#POP Diversity#MPLS Port POP Diversity - 20M#ATM#20Mbps#ENET#VPN Transport#Connection#Each\"},{\"priceType\":\"ACCESSBEID\",\"beid\":\"19770\",\"quantity\":\"1\",\"localListPrice\":\"385.24\",\"actualPrice\":\"385.24\"}],\"circuitId\":\"MMYF106085ATI\",\"accessBandwidth\":\"20000 Kbps\",\"clli\":\"WKNGEN13\",\"technology\":\"Ethernet (Gateway Interconnect/ESP ETH Shared)\",\"accessCarrier\":\"GB-SAREAQ\",\"accessSecondaryKey\":\"#Intl#United Kingdom#GB#AVPN#N/A#N/A\",\"nxSiteId\":35501},{\"siteName\":\"3038829\",\"portSpeed\":\"20000\",\"priceDetails\":[{\"priceType\":\"PORTBEID\",\"beid\":\"18007\",\"quantity\":\"1\",\"localListPrice\":\"910\",\"actualPrice\":\"136.5\",\"secondaryKey\":\"#Intl#MPLS Port#Flat Rate#20M#Enet, T3, E3#Enet, FR, ATM, IP#VPN Transport Connection#per port#18007#18030\",\"elementType\":\"Port\",\"uniqueId\":\"#MPLS Port#MPLS Port - 20M#ATM#20Mbps#ENET#VPN Transport#Connection#Each\"},{\"priceType\":\"DIVERSITYBEID\",\"beid\":\"18128\",\"quantity\":\"1\",\"localListPrice\":\"30\",\"actualPrice\":\"21\",\"secondaryKey\":\"#Intl#Port Option#POP Diversity#20M#Enet, T3, E3#Enet, ATM, IP, FR#VPN Transport Connection#per port#18128#18151\",\"elementType\":\"PortFeature\",\"uniqueId\":\"#POP Diversity#MPLS Port POP Diversity - 20M#ATM#20Mbps#ENET#VPN Transport#Connection#Each\"},{\"priceType\":\"ACCESSBEID\",\"beid\":\"19770\",\"quantity\":\"1\",\"localListPrice\":\"385.24\",\"actualPrice\":\"385.24\"}],\"circuitId\":\"MMYF106085ATI\",\"accessBandwidth\":\"20000 Kbps\",\"clli\":\"WKNGEN13\",\"technology\":\"Ethernet (Gateway Interconnect/ESP ETH Shared)\",\"accessCarrier\":\"GB-SAREAQ\",\"accessSecondaryKey\":\"#Intl#United Kingdom#GB#AVPN#N/A#N/A\",\"nxSiteId\":35501},{\"siteName\":\"3038829\",\"portSpeed\":\"20000\",\"priceDetails\":[{\"priceType\":\"PORTBEID\",\"beid\":\"18007\",\"quantity\":\"1\",\"localListPrice\":\"910\",\"actualPrice\":\"136.5\",\"secondaryKey\":\"#Intl#MPLS Port#Flat Rate#20M#Enet, T3, E3#Enet, FR, ATM, IP#VPN Transport Connection#per port#18007#18030\",\"elementType\":\"Port\",\"uniqueId\":\"#MPLS Port#MPLS Port - 20M#ATM#20Mbps#ENET#VPN Transport#Connection#Each\"},{\"priceType\":\"DIVERSITYBEID\",\"beid\":\"18128\",\"quantity\":\"1\",\"localListPrice\":\"30\",\"actualPrice\":\"21\",\"secondaryKey\":\"#Intl#Port Option#POP Diversity#20M#Enet, T3, E3#Enet, ATM, IP, FR#VPN Transport Connection#per port#18128#18151\",\"elementType\":\"PortFeature\",\"uniqueId\":\"#POP Diversity#MPLS Port POP Diversity - 20M#ATM#20Mbps#ENET#VPN Transport#Connection#Each\"},{\"priceType\":\"ACCESSBEID\",\"beid\":\"19770\",\"quantity\":\"1\",\"localListPrice\":\"385.24\",\"actualPrice\":\"385.24\"},{\"priceType\":\"COSBEID\",\"beid\":\"18286\",\"quantity\":\"1\",\"localListPrice\":\"0\",\"actualPrice\":\"0\",\"secondaryKey\":\"#Intl#CoS Package#Multimedia Standard Svc#20M#VPN Transport COS Package#per port#18286#18425\",\"elementType\":\"PortFeature\",\"uniqueId\":\"#CoS Package Multimedia Standard#CoS Package Multimedia Standard Svc - 20M#ENET#20Mbps#ENET#VPN Transport#COS Package#Port\"}],\"circuitId\":\"MMYF106085ATI\",\"accessBandwidth\":\"20000 Kbps\",\"clli\":\"WKNGEN13\",\"technology\":\"Ethernet (Gateway Interconnect/ESP ETH Shared)\",\"accessCarrier\":\"GB-SAREAQ\",\"accessSecondaryKey\":\"#Intl#United Kingdom#GB#AVPN#N/A#N/A\",\"nxSiteId\":35501},{\"siteName\":\"3038829\",\"portSpeed\":\"20000\",\"priceDetails\":[{\"priceType\":\"PORTBEID\",\"beid\":\"18007\",\"quantity\":\"1\",\"localListPrice\":\"910\",\"actualPrice\":\"136.5\",\"secondaryKey\":\"#Intl#MPLS Port#Flat Rate#20M#Enet, T3, E3#Enet, FR, ATM, IP#VPN Transport Connection#per port#18007#18030\",\"elementType\":\"Port\",\"uniqueId\":\"#MPLS Port#MPLS Port - 20M#ATM#20Mbps#ENET#VPN Transport#Connection#Each\"},{\"priceType\":\"DIVERSITYBEID\",\"beid\":\"18128\",\"quantity\":\"1\",\"localListPrice\":\"30\",\"actualPrice\":\"21\",\"secondaryKey\":\"#Intl#Port Option#POP Diversity#20M#Enet, T3, E3#Enet, ATM, IP, FR#VPN Transport Connection#per port#18128#18151\",\"elementType\":\"PortFeature\",\"uniqueId\":\"#POP Diversity#MPLS Port POP Diversity - 20M#ATM#20Mbps#ENET#VPN Transport#Connection#Each\"},{\"priceType\":\"ACCESSBEID\",\"beid\":\"19770\",\"quantity\":\"1\",\"localListPrice\":\"385.24\",\"actualPrice\":\"385.24\"}],\"circuitId\":\"MMYF106085ATI\",\"accessBandwidth\":\"20000 Kbps\",\"clli\":\"WKNGEN13\",\"technology\":\"Ethernet (Gateway Interconnect/ESP ETH Shared)\",\"accessCarrier\":\"GB-SAREAQ\",\"accessSecondaryKey\":\"#Intl#United Kingdom#GB#AVPN#N/A#N/A\",\"nxSiteId\":35501},{\"siteName\":\"3038829\",\"portSpeed\":\"20000\",\"priceDetails\":[{\"priceType\":\"PORTBEID\",\"beid\":\"18007\",\"quantity\":\"1\",\"localListPrice\":\"910\",\"actualPrice\":\"136.5\",\"secondaryKey\":\"#Intl#MPLS Port#Flat Rate#20M#Enet, T3, E3#Enet, FR, ATM, IP#VPN Transport Connection#per port#18007#18030\",\"elementType\":\"Port\",\"uniqueId\":\"#MPLS Port#MPLS Port - 20M#ATM#20Mbps#ENET#VPN Transport#Connection#Each\"},{\"priceType\":\"DIVERSITYBEID\",\"beid\":\"18128\",\"quantity\":\"1\",\"localListPrice\":\"30\",\"actualPrice\":\"21\",\"secondaryKey\":\"#Intl#Port Option#POP Diversity#20M#Enet, T3, E3#Enet, ATM, IP, FR#VPN Transport Connection#per port#18128#18151\",\"elementType\":\"PortFeature\",\"uniqueId\":\"#POP Diversity#MPLS Port POP Diversity - 20M#ATM#20Mbps#ENET#VPN Transport#Connection#Each\"},{\"priceType\":\"ACCESSBEID\",\"beid\":\"19770\",\"quantity\":\"1\",\"localListPrice\":\"385.24\",\"actualPrice\":\"385.24\"}],\"circuitId\":\"MMYF106085ATI\",\"accessBandwidth\":\"20000 Kbps\",\"clli\":\"WKNGEN13\",\"technology\":\"Ethernet (Gateway Interconnect/ESP ETH Shared)\",\"accessCarrier\":\"GB-SAREAQ\",\"accessSecondaryKey\":\"#Intl#United Kingdom#GB#AVPN#N/A#N/A\",\"nxSiteId\":35501}],\"secondaryKey\":\"#Intl#AVPN#United Kingdom#GB#EMEA\",\"product\":\"AVPN\"}");
		designs.add(design);
		nxInrDesign.setNxInrDesignDetails(designs);
		paramMap = new HashMap<String, Object>();
		paramMap.put(MyPriceConstants.MP_TRANSACTION_ID, "1010101");
		paramMap.put(MyPriceConstants.NX_TRANSACTION_ID, 101l);
		
		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(ServiceMetaData.OFFER, "AVPN");
		requestParams.put(ServiceMetaData.REST_REQUEST_START_TIME, System.currentTimeMillis());
		requestParams.put(ServiceMetaData.VERSION, "1.0");
		requestParams.put(ServiceMetaData.METHOD, "TestMethod");
		requestParams.put(ServiceMetaData.URI, "Testuri");
		
		paramMap.put(InrConstants.REQUEST_META_DATA_KEY, requestParams);
	}
	@Test
	public void testConfigAndUpdatePricing() throws SalesBusinessException {
		Map<String,List<String>> productInfoMap = new HashMap<String,List<String>>();
		productInfoMap.put("AVPN", new ArrayList<String>() {{add("AVPN");}});
		Mockito.when(configAndUpdatePricingUtilInr.getConfigProdutMapFromLookup(anyString())).thenReturn(productInfoMap);
		Mockito.when(configureSolnAndProductWSHandlerInr.initiateConfigSolnAndProdWebService(any(), anyMap())).thenReturn(true);
		doNothing().when(myPriceTransactionUtil).prepareResponseMap(anyMap(), anyMap());
		Mockito.when(configureDesignWSHandlerInr.initiateConfigDesignWebService(any(), anyMap())).thenReturn(true);
		configAndUpdateProcessingInrService.configAndUpdatePricing(response, nxInrDesign, paramMap);
	}

	@Test
	public void testCallConfigureSolutionProduct() throws SalesBusinessException {
		Mockito.when(configureSolnAndProductWSHandlerInr.initiateConfigSolnAndProdWebService(any(), anyMap())).thenReturn(false);
		configAndUpdateProcessingInrService.callConfigureSolutionProduct(any(), anyMap());
		
		Mockito.when(configureSolnAndProductWSHandlerInr.initiateConfigSolnAndProdWebService(any(), anyMap())).thenThrow(SalesBusinessException.class);
		configAndUpdateProcessingInrService.callConfigureSolutionProduct(any(), anyMap());
	}
	
	@Test
	public void testCallConfigureDesign() throws SalesBusinessException {
		Mockito.when(configureDesignWSHandlerInr.initiateConfigDesignWebService(any(), anyMap())).thenReturn(false);
		configAndUpdateProcessingInrService.callConfigureDesign(any(), anyMap());
		
		Mockito.when(configureDesignWSHandlerInr.initiateConfigDesignWebService(any(), anyMap())).thenThrow(SalesBusinessException.class);
		configAndUpdateProcessingInrService.callConfigureDesign(any(), anyMap());
	}
	
	@Test
	public void testCallUpdatePricing() throws SalesBusinessException {
		JSONObject json = JacksonUtil.toJsonObject("{\"siteId\":\"90923699\",\"address\":\"4 HEATHROW BOULEVARD 280 BATH ROAD\",\"city\":\"WEST DRAYTON\",\"country\":\"GB\",\"custPostalcode\":\"UB70DQ\",\"design\":[{\"siteName\":\"3038829\",\"portSpeed\":\"20000\",\"priceDetails\":[{\"priceType\":\"PORTBEID\",\"beid\":\"18007\",\"quantity\":\"1\",\"localListPrice\":\"910\",\"actualPrice\":\"136.5\",\"secondaryKey\":\"#Intl#MPLS Port#Flat Rate#20M#Enet, T3, E3#Enet, FR, ATM, IP#VPN Transport Connection#per port#18007#18030\",\"elementType\":\"Port\",\"uniqueId\":\"#MPLS Port#MPLS Port - 20M#ATM#20Mbps#ENET#VPN Transport#Connection#Each\"},{\"priceType\":\"DIVERSITYBEID\",\"beid\":\"18128\",\"quantity\":\"1\",\"localListPrice\":\"30\",\"actualPrice\":\"21\",\"secondaryKey\":\"#Intl#Port Option#POP Diversity#20M#Enet, T3, E3#Enet, ATM, IP, FR#VPN Transport Connection#per port#18128#18151\",\"elementType\":\"PortFeature\",\"uniqueId\":\"#POP Diversity#MPLS Port POP Diversity - 20M#ATM#20Mbps#ENET#VPN Transport#Connection#Each\"},{\"priceType\":\"ACCESSBEID\",\"beid\":\"19770\",\"quantity\":\"1\",\"localListPrice\":\"385.24\",\"actualPrice\":\"385.24\"}],\"circuitId\":\"MMYF106085ATI\",\"accessBandwidth\":\"20000 Kbps\",\"clli\":\"WKNGEN13\",\"technology\":\"Ethernet (Gateway Interconnect/ESP ETH Shared)\",\"accessCarrier\":\"GB-SAREAQ\",\"accessSecondaryKey\":\"#Intl#United Kingdom#GB#AVPN#N/A#N/A\",\"nxSiteId\":35501},{\"siteName\":\"3038829\",\"portSpeed\":\"20000\",\"priceDetails\":[{\"priceType\":\"PORTBEID\",\"beid\":\"18007\",\"quantity\":\"1\",\"localListPrice\":\"910\",\"actualPrice\":\"136.5\",\"secondaryKey\":\"#Intl#MPLS Port#Flat Rate#20M#Enet, T3, E3#Enet, FR, ATM, IP#VPN Transport Connection#per port#18007#18030\",\"elementType\":\"Port\",\"uniqueId\":\"#MPLS Port#MPLS Port - 20M#ATM#20Mbps#ENET#VPN Transport#Connection#Each\"},{\"priceType\":\"DIVERSITYBEID\",\"beid\":\"18128\",\"quantity\":\"1\",\"localListPrice\":\"30\",\"actualPrice\":\"21\",\"secondaryKey\":\"#Intl#Port Option#POP Diversity#20M#Enet, T3, E3#Enet, ATM, IP, FR#VPN Transport Connection#per port#18128#18151\",\"elementType\":\"PortFeature\",\"uniqueId\":\"#POP Diversity#MPLS Port POP Diversity - 20M#ATM#20Mbps#ENET#VPN Transport#Connection#Each\"},{\"priceType\":\"ACCESSBEID\",\"beid\":\"19770\",\"quantity\":\"1\",\"localListPrice\":\"385.24\",\"actualPrice\":\"385.24\"},{\"priceType\":\"COSBEID\",\"beid\":\"18286\",\"quantity\":\"1\",\"localListPrice\":\"0\",\"actualPrice\":\"0\",\"secondaryKey\":\"#Intl#CoS Package#Multimedia Standard Svc#20M#VPN Transport COS Package#per port#18286#18425\",\"elementType\":\"PortFeature\",\"uniqueId\":\"#CoS Package Multimedia Standard#CoS Package Multimedia Standard Svc - 20M#ENET#20Mbps#ENET#VPN Transport#COS Package#Port\"}],\"circuitId\":\"MMYF106085ATI\",\"accessBandwidth\":\"20000 Kbps\",\"clli\":\"WKNGEN13\",\"technology\":\"Ethernet (Gateway Interconnect/ESP ETH Shared)\",\"accessCarrier\":\"GB-SAREAQ\",\"accessSecondaryKey\":\"#Intl#United Kingdom#GB#AVPN#N/A#N/A\",\"nxSiteId\":35501},{\"siteName\":\"3038829\",\"portSpeed\":\"20000\",\"priceDetails\":[{\"priceType\":\"PORTBEID\",\"beid\":\"18007\",\"quantity\":\"1\",\"localListPrice\":\"910\",\"actualPrice\":\"136.5\",\"secondaryKey\":\"#Intl#MPLS Port#Flat Rate#20M#Enet, T3, E3#Enet, FR, ATM, IP#VPN Transport Connection#per port#18007#18030\",\"elementType\":\"Port\",\"uniqueId\":\"#MPLS Port#MPLS Port - 20M#ATM#20Mbps#ENET#VPN Transport#Connection#Each\"},{\"priceType\":\"DIVERSITYBEID\",\"beid\":\"18128\",\"quantity\":\"1\",\"localListPrice\":\"30\",\"actualPrice\":\"21\",\"secondaryKey\":\"#Intl#Port Option#POP Diversity#20M#Enet, T3, E3#Enet, ATM, IP, FR#VPN Transport Connection#per port#18128#18151\",\"elementType\":\"PortFeature\",\"uniqueId\":\"#POP Diversity#MPLS Port POP Diversity - 20M#ATM#20Mbps#ENET#VPN Transport#Connection#Each\"},{\"priceType\":\"ACCESSBEID\",\"beid\":\"19770\",\"quantity\":\"1\",\"localListPrice\":\"385.24\",\"actualPrice\":\"385.24\"}],\"circuitId\":\"MMYF106085ATI\",\"accessBandwidth\":\"20000 Kbps\",\"clli\":\"WKNGEN13\",\"technology\":\"Ethernet (Gateway Interconnect/ESP ETH Shared)\",\"accessCarrier\":\"GB-SAREAQ\",\"accessSecondaryKey\":\"#Intl#United Kingdom#GB#AVPN#N/A#N/A\",\"nxSiteId\":35501},{\"siteName\":\"3038829\",\"portSpeed\":\"20000\",\"priceDetails\":[{\"priceType\":\"PORTBEID\",\"beid\":\"18007\",\"quantity\":\"1\",\"localListPrice\":\"910\",\"actualPrice\":\"136.5\",\"secondaryKey\":\"#Intl#MPLS Port#Flat Rate#20M#Enet, T3, E3#Enet, FR, ATM, IP#VPN Transport Connection#per port#18007#18030\",\"elementType\":\"Port\",\"uniqueId\":\"#MPLS Port#MPLS Port - 20M#ATM#20Mbps#ENET#VPN Transport#Connection#Each\"},{\"priceType\":\"DIVERSITYBEID\",\"beid\":\"18128\",\"quantity\":\"1\",\"localListPrice\":\"30\",\"actualPrice\":\"21\",\"secondaryKey\":\"#Intl#Port Option#POP Diversity#20M#Enet, T3, E3#Enet, ATM, IP, FR#VPN Transport Connection#per port#18128#18151\",\"elementType\":\"PortFeature\",\"uniqueId\":\"#POP Diversity#MPLS Port POP Diversity - 20M#ATM#20Mbps#ENET#VPN Transport#Connection#Each\"},{\"priceType\":\"ACCESSBEID\",\"beid\":\"19770\",\"quantity\":\"1\",\"localListPrice\":\"385.24\",\"actualPrice\":\"385.24\"}],\"circuitId\":\"MMYF106085ATI\",\"accessBandwidth\":\"20000 Kbps\",\"clli\":\"WKNGEN13\",\"technology\":\"Ethernet (Gateway Interconnect/ESP ETH Shared)\",\"accessCarrier\":\"GB-SAREAQ\",\"accessSecondaryKey\":\"#Intl#United Kingdom#GB#AVPN#N/A#N/A\",\"nxSiteId\":35501},{\"siteName\":\"3038829\",\"portSpeed\":\"20000\",\"priceDetails\":[{\"priceType\":\"PORTBEID\",\"beid\":\"18007\",\"quantity\":\"1\",\"localListPrice\":\"910\",\"actualPrice\":\"136.5\",\"secondaryKey\":\"#Intl#MPLS Port#Flat Rate#20M#Enet, T3, E3#Enet, FR, ATM, IP#VPN Transport Connection#per port#18007#18030\",\"elementType\":\"Port\",\"uniqueId\":\"#MPLS Port#MPLS Port - 20M#ATM#20Mbps#ENET#VPN Transport#Connection#Each\"},{\"priceType\":\"DIVERSITYBEID\",\"beid\":\"18128\",\"quantity\":\"1\",\"localListPrice\":\"30\",\"actualPrice\":\"21\",\"secondaryKey\":\"#Intl#Port Option#POP Diversity#20M#Enet, T3, E3#Enet, ATM, IP, FR#VPN Transport Connection#per port#18128#18151\",\"elementType\":\"PortFeature\",\"uniqueId\":\"#POP Diversity#MPLS Port POP Diversity - 20M#ATM#20Mbps#ENET#VPN Transport#Connection#Each\"},{\"priceType\":\"ACCESSBEID\",\"beid\":\"19770\",\"quantity\":\"1\",\"localListPrice\":\"385.24\",\"actualPrice\":\"385.24\"}],\"circuitId\":\"MMYF106085ATI\",\"accessBandwidth\":\"20000 Kbps\",\"clli\":\"WKNGEN13\",\"technology\":\"Ethernet (Gateway Interconnect/ESP ETH Shared)\",\"accessCarrier\":\"GB-SAREAQ\",\"accessSecondaryKey\":\"#Intl#United Kingdom#GB#AVPN#N/A#N/A\",\"nxSiteId\":35501},{\"siteName\":\"3038829\",\"portSpeed\":\"20000\",\"priceDetails\":[{\"priceType\":\"PORTBEID\",\"beid\":\"18007\",\"quantity\":\"1\",\"localListPrice\":\"910\",\"actualPrice\":\"136.5\",\"secondaryKey\":\"#Intl#MPLS Port#Flat Rate#20M#Enet, T3, E3#Enet, FR, ATM, IP#VPN Transport Connection#per port#18007#18030\",\"elementType\":\"Port\",\"uniqueId\":\"#MPLS Port#MPLS Port - 20M#ATM#20Mbps#ENET#VPN Transport#Connection#Each\"},{\"priceType\":\"DIVERSITYBEID\",\"beid\":\"18128\",\"quantity\":\"1\",\"localListPrice\":\"30\",\"actualPrice\":\"21\",\"secondaryKey\":\"#Intl#Port Option#POP Diversity#20M#Enet, T3, E3#Enet, ATM, IP, FR#VPN Transport Connection#per port#18128#18151\",\"elementType\":\"PortFeature\",\"uniqueId\":\"#POP Diversity#MPLS Port POP Diversity - 20M#ATM#20Mbps#ENET#VPN Transport#Connection#Each\"},{\"priceType\":\"ACCESSBEID\",\"beid\":\"19770\",\"quantity\":\"1\",\"localListPrice\":\"385.24\",\"actualPrice\":\"385.24\"},{\"priceType\":\"COSBEID\",\"beid\":\"18286\",\"quantity\":\"1\",\"localListPrice\":\"0\",\"actualPrice\":\"0\",\"secondaryKey\":\"#Intl#CoS Package#Multimedia Standard Svc#20M#VPN Transport COS Package#per port#18286#18425\",\"elementType\":\"PortFeature\",\"uniqueId\":\"#CoS Package Multimedia Standard#CoS Package Multimedia Standard Svc - 20M#ENET#20Mbps#ENET#VPN Transport#COS Package#Port\"}],\"circuitId\":\"MMYF106085ATI\",\"accessBandwidth\":\"20000 Kbps\",\"clli\":\"WKNGEN13\",\"technology\":\"Ethernet (Gateway Interconnect/ESP ETH Shared)\",\"accessCarrier\":\"GB-SAREAQ\",\"accessSecondaryKey\":\"#Intl#United Kingdom#GB#AVPN#N/A#N/A\",\"nxSiteId\":35501},{\"siteName\":\"3038829\",\"portSpeed\":\"20000\",\"priceDetails\":[{\"priceType\":\"PORTBEID\",\"beid\":\"18007\",\"quantity\":\"1\",\"localListPrice\":\"910\",\"actualPrice\":\"136.5\",\"secondaryKey\":\"#Intl#MPLS Port#Flat Rate#20M#Enet, T3, E3#Enet, FR, ATM, IP#VPN Transport Connection#per port#18007#18030\",\"elementType\":\"Port\",\"uniqueId\":\"#MPLS Port#MPLS Port - 20M#ATM#20Mbps#ENET#VPN Transport#Connection#Each\"},{\"priceType\":\"DIVERSITYBEID\",\"beid\":\"18128\",\"quantity\":\"1\",\"localListPrice\":\"30\",\"actualPrice\":\"21\",\"secondaryKey\":\"#Intl#Port Option#POP Diversity#20M#Enet, T3, E3#Enet, ATM, IP, FR#VPN Transport Connection#per port#18128#18151\",\"elementType\":\"PortFeature\",\"uniqueId\":\"#POP Diversity#MPLS Port POP Diversity - 20M#ATM#20Mbps#ENET#VPN Transport#Connection#Each\"},{\"priceType\":\"ACCESSBEID\",\"beid\":\"19770\",\"quantity\":\"1\",\"localListPrice\":\"385.24\",\"actualPrice\":\"385.24\"}],\"circuitId\":\"MMYF106085ATI\",\"accessBandwidth\":\"20000 Kbps\",\"clli\":\"WKNGEN13\",\"technology\":\"Ethernet (Gateway Interconnect/ESP ETH Shared)\",\"accessCarrier\":\"GB-SAREAQ\",\"accessSecondaryKey\":\"#Intl#United Kingdom#GB#AVPN#N/A#N/A\",\"nxSiteId\":35501},{\"siteName\":\"3038829\",\"portSpeed\":\"20000\",\"priceDetails\":[{\"priceType\":\"PORTBEID\",\"beid\":\"18007\",\"quantity\":\"1\",\"localListPrice\":\"910\",\"actualPrice\":\"136.5\",\"secondaryKey\":\"#Intl#MPLS Port#Flat Rate#20M#Enet, T3, E3#Enet, FR, ATM, IP#VPN Transport Connection#per port#18007#18030\",\"elementType\":\"Port\",\"uniqueId\":\"#MPLS Port#MPLS Port - 20M#ATM#20Mbps#ENET#VPN Transport#Connection#Each\"},{\"priceType\":\"DIVERSITYBEID\",\"beid\":\"18128\",\"quantity\":\"1\",\"localListPrice\":\"30\",\"actualPrice\":\"21\",\"secondaryKey\":\"#Intl#Port Option#POP Diversity#20M#Enet, T3, E3#Enet, ATM, IP, FR#VPN Transport Connection#per port#18128#18151\",\"elementType\":\"PortFeature\",\"uniqueId\":\"#POP Diversity#MPLS Port POP Diversity - 20M#ATM#20Mbps#ENET#VPN Transport#Connection#Each\"},{\"priceType\":\"ACCESSBEID\",\"beid\":\"19770\",\"quantity\":\"1\",\"localListPrice\":\"385.24\",\"actualPrice\":\"385.24\"}],\"circuitId\":\"MMYF106085ATI\",\"accessBandwidth\":\"20000 Kbps\",\"clli\":\"WKNGEN13\",\"technology\":\"Ethernet (Gateway Interconnect/ESP ETH Shared)\",\"accessCarrier\":\"GB-SAREAQ\",\"accessSecondaryKey\":\"#Intl#United Kingdom#GB#AVPN#N/A#N/A\",\"nxSiteId\":35501}],\"secondaryKey\":\"#Intl#AVPN#United Kingdom#GB#EMEA\",\"product\":\"AVPN\"}");
		paramMap.put(MyPriceConstants.RESPONSE_STATUS, true);
		Mockito.when(updateTransactionPricingInrService.updateTransactionPricingRequest(anyMap(), any())).thenReturn(new UpdateTransactionPricingResponse());;
		configAndUpdateProcessingInrService.callUpdatePricing(json, paramMap);
		
		paramMap.put(MyPriceConstants.RESPONSE_STATUS, false);
		configAndUpdateProcessingInrService.callUpdatePricing(json, paramMap);
		
		Mockito.when(updateTransactionPricingInrService.updateTransactionPricingRequest(anyMap(), any())).thenThrow(SalesBusinessException.class);
		configAndUpdateProcessingInrService.callUpdatePricing(json, paramMap);
	}
	
	@Test
	public void testGetProductName() {
		Map<String,List<String>> configProductInfoMap = new HashMap<String,List<String>>();
		configProductInfoMap.put("DDA", new ArrayList<String>() {{add("TDM");}});
		configAndUpdateProcessingInrService.getProductName(configProductInfoMap, "TDM");
	}
	
	@Test
	public void testListEqualsIgnoreOrder() {
		List<String> lsit1 = new ArrayList<String>() {{add("TDM");}};
		List<String> list2 = new ArrayList<String>() {{add("TDM"); add("Ethernet");}};
		configAndUpdateProcessingInrService.listEqualsIgnoreOrder(lsit1, list2);
		
		list2.remove(1);
		configAndUpdateProcessingInrService.listEqualsIgnoreOrder(lsit1, list2);
	}
}
