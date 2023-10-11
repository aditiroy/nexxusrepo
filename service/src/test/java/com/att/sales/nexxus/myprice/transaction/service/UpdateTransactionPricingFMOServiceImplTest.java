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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.FmoConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxDesign;
import com.att.sales.nexxus.dao.model.NxDesignDetails;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxMpDesignDocument;
import com.att.sales.nexxus.dao.model.NxMpPriceDetails;
import com.att.sales.nexxus.dao.repository.NxDesignDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxMpDesignDocumentRepository;
import com.att.sales.nexxus.dao.repository.NxMpPriceDetailsRepository;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTransactionPricingResponse;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.RestClientUtil;

@ExtendWith(MockitoExtension.class)

public class UpdateTransactionPricingFMOServiceImplTest {
	
	@InjectMocks
	@Spy
	private UpdateTransactionPricingFMOServiceImpl updateTransactionPricingFMOServiceImpl;
	
	@Mock
	private NxMpDealRepository nxMpDealRepository;
	
	@Mock
	private NxDesignDetailsRepository nxDesignDetailsRepository;
	
	@Mock
	private NxMpDesignDocumentRepository nxMpDesignDocumentRepository;
	
	@Mock
	private NxMpPriceDetailsRepository nxMpPriceDetailsRepository;
	
	@Mock
	private Environment env;
	
	@Mock
	private RestClientUtil restClient;
	
	@Mock
	private HttpRestClient httpRest;
	
	private Map<String, Object> designMap;
	private List<NxMpDeal> deals;
	private Map<String, Object> response;
	
	@BeforeEach
	public void init() {
		designMap = new HashMap<String, Object>();
		designMap.put(FmoConstants.PORT_ID, "10101");
		deals = new ArrayList<>();
		NxMpDeal deal = new NxMpDeal();
		deal.setTransactionId("1010110");
		deals.add(deal);
		response = new HashMap<String, Object>();
		response.put(MyPriceConstants.RESPONSE_CODE, 200);
		response.put(MyPriceConstants.RESPONSE_DATA, "{\"id\": \"1234\"}");
		response.put(MyPriceConstants.RESPONSE_MSG, "Success");
	}
	
	@Test
	public void testUpdateTransactionPricingRequest() throws SalesBusinessException {
		Mockito.when(nxMpDealRepository.findBySolutionId(anyLong())).thenReturn(deals);
		List<NxDesignDetails> designDetails = new ArrayList<>();
		NxDesign nxd = new NxDesign();
		nxd.setNxDesignId(101l);
		NxDesignDetails design = new NxDesignDetails();
		design.setDesignData("{\"country\":\"US\",\"nxSiteId\":5795,\"zipCode\":\"30005\",\"address\":null,\"referenceOfferId\":4,\"city\":\"Alpharetta\",\"address2\":null,\"address1\":\"300 Point Parkway\",\"postalCode\":null,\"siteName\":\"ALPHARETTA 1\",\"siteNpanxx\":null,\"regionCode\":null,\"swcClli\":\"ALPRGAMA\",\"siteId\":9870753,\"state\":\"GA\",\"customerLocationClli\":null,\"design\":[{\"portProtocol\":\"PPP\",\"accessSpeedUdfAttrId\":30375,\"accessDetails\":{\"supplierName\":null,\"npanxx\":null,\"nrcListRate\":null,\"serialNumber\":null,\"tokenId\":null,\"mrcListRate\":null,\"respAccessInterconnect\":null,\"dqid\":null,\"portId\":null,\"speed\":null,\"quoteId\":null,\"iglooMaxMrcDiscount\":null,\"respSpeed\":null,\"popClli\":null,\"respSupplierName\":null,\"currencyCode\":null,\"respPopClli\":null},\"sitePopCilli\":null,\"portId\":9252692,\"interface\":null,\"referenceSiteId\":9870753,\"lac\":null,\"accessSpeed\":\"1.544 mb\",\"accessType\":\"Private Line\",\"accessArchitecture\":null,\"accessTypeUdfAttrId\":30155,\"categoryLocalAccess\":null,\"portSpeed\":\"1.544M\",\"priceDetails\":[{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999928642,\"secondaryKeys\":\"#Intl#MPLS Port#Flat Rate#56K#DS0#FR#VPN Transport Connection#per port#17972#18030\",\"quantity\":1.0,\"priceType\":\"portNRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":85,\"frequency\":\"NRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18030\",\"componentParentId\":9870753,\"productRateId\":930,\"reqPriceType\":\"Fixed\",\"term\":48,\"elementType\":\"Port\",\"uniqueId\":\"#One Time Charge#MPLS Port Activation Charge#ATM#N/A#N/A#Non Recurring Charges#N/A#Port\",\"localListPrice\":1000.0,\"referencePortId\":9252692},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999928642,\"secondaryKeys\":\"#FCC#MPLS Port#Flat Rate#1.544M/1.536M#T1, E1#FR, ATM, IP#VPN Transport Connection#per port#17986#18030#United States#US#USA\",\"quantity\":1.0,\"priceType\":\"portRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":78,\"frequency\":\"MRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"17986\",\"componentParentId\":9870753,\"productRateId\":884,\"reqPriceType\":null,\"term\":48,\"elementType\":\"Port\",\"uniqueId\":\"#MPLS Port#MPLS Port - 1.544M/1.536M#ATM#1.544Mbps/1.536Mbps#T1/E1#VPN Transport#Connection#Each\",\"localListPrice\":221.0,\"referencePortId\":9252692},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999928642,\"secondaryKeys\":\"#FCC#CoS Package#Multimedia Standard Svc#1.544M/1.536M#N/A#N/A#VPN Transport COS Package#per port#18265#18425#United States#US#USA\",\"quantity\":1.0,\"priceType\":null,\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":78,\"frequency\":\"MRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18265\",\"componentParentId\":9870753,\"productRateId\":1297,\"reqPriceType\":null,\"term\":48,\"elementType\":\"PortFeature\",\"uniqueId\":\"#CoS Package Multimedia Standard#CoS Package Multimedia Standard Svc - 1.544M/1.536M#ATM#1.544Mbps/1.536Mbps#T1/E1#VPN Transport#COS Package#Port\",\"localListPrice\":0.0,\"referencePortId\":9252692},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999928642,\"secondaryKeys\":\"#FCC#AT&T Owned and Managed Features#MPLS Managed CSU#Internal#T1/E1#Managed CPE Features#per circuit#18488#18520#United States#US#USA\",\"quantity\":1.0,\"priceType\":null,\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":87,\"frequency\":\"MRC\",\"rdsPriceType\":\"AVPN_CPE_ROUTER\",\"beid\":\"18488\",\"componentParentId\":9870753,\"productRateId\":1575,\"reqPriceType\":null,\"term\":48,\"elementType\":\"RouterFeatures\",\"uniqueId\":\"#Managed Router - CPE Features#MPLS Managed CSU Internal T1/E1#FR#N/A#T1/E1#Managed CPE#CPE features#Per T1/E1\",\"localListPrice\":40.0,\"referencePortId\":9252692},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999928642,\"secondaryKeys\":\"#FCC#MPLS Managed Lite Router#AT&T Owned and Managed#On-Site Install#Basic#Managed CPE Managed Router#per router#30000#18501#United States#US#USA\",\"quantity\":1.0,\"priceType\":null,\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":0,\"frequency\":\"NRC\",\"rdsPriceType\":\"AVPN_CPE_ROUTER\",\"beid\":\"18501\",\"componentParentId\":9870753,\"productRateId\":1591,\"reqPriceType\":null,\"term\":48,\"elementType\":\"RouterFeatures\",\"uniqueId\":\"#One Time Charges - Managed Router#MPLS MANAGED ROUTER ACTIVATION - AT&T INSTALL#All#N/A#N/A#Not Discountable#Not Discountable OTC#Router\",\"localListPrice\":1000.0,\"referencePortId\":9252692},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999928642,\"secondaryKeys\":\"#Intl#CoS Package#Multimedia High Svc#56K#VPN Transport COS Package#per port#18193#18425\",\"quantity\":1.0,\"priceType\":null,\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":85,\"frequency\":\"NRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18425\",\"componentParentId\":9870753,\"productRateId\":1498,\"reqPriceType\":null,\"term\":48,\"elementType\":\"PortFeature\",\"uniqueId\":\"#One Time Charge#MPLS CoS Activation Charge#ATM#N/A#N/A#Non Recurring Charges#COS Package#Port\",\"localListPrice\":0.0,\"referencePortId\":9252692},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999928642,\"secondaryKeys\":\"#FCC#MPLS Managed Router#AT&T Owned and Managed#N/A#Basic#Managed CPE Managed Router#per router#18495#18501#United States#US#USA\",\"quantity\":1.0,\"priceType\":null,\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":87,\"frequency\":\"MRC\",\"rdsPriceType\":\"AVPN_CPE_ROUTER\",\"beid\":\"18495\",\"componentParentId\":9870753,\"productRateId\":1585,\"reqPriceType\":null,\"term\":48,\"elementType\":\"StandardRouters\",\"uniqueId\":\"#Managed Routers - AT&T Managed#MPLS Managed Router - Basic#All#N/A#N/A#Managed CPE#Managed Router#Router\",\"localListPrice\":230.0,\"referencePortId\":9252692}],\"accessTailTechnology\":null,\"mileage\":null,\"siteType\":null}]}");
		design.setNxDesign(nxd);
		design.setComponentId("9252692");
		design.setProductName(MyPriceConstants.ADI);
		designDetails.add(design);
		Mockito.when(nxDesignDetailsRepository.findDesignDetailsaByNxSolutionId(anyLong())).thenReturn(designDetails);
		List<NxMpDesignDocument> designDocuments = new ArrayList<>();
		NxMpDesignDocument mpDesign = new NxMpDesignDocument();
		mpDesign.setUsocId("18030");
		mpDesign.setMpDocumentNumber(3l);
		designDocuments.add(mpDesign);
		NxMpDesignDocument mpDesign1 = new NxMpDesignDocument();
		mpDesign1.setUsocId("17986");
		mpDesign1.setMpDocumentNumber(4l);
		designDocuments.add(mpDesign1);
		Mockito.when(nxMpDesignDocumentRepository.findByNxTxnIdAndNxDesignIdAndMpPartNumber(anyLong(), anyLong(), anyString())).thenReturn(designDocuments);
		Mockito.when(nxMpPriceDetailsRepository.save(any(NxMpPriceDetails.class))).thenReturn(new NxMpPriceDetails());

		Mockito.when(env.getProperty("myprice.updateTransactionPricingRequest")).thenReturn("https://custompricingst.att.com/rest/v8/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/actions/updateRequestedRates");
		String res = "{\"id\": \"1234\"}";
		Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString()))
		.thenReturn(res);
		Mockito.when(restClient.processResult(anyString(), any())).thenReturn( new UpdateTransactionPricingResponse());
		
		updateTransactionPricingFMOServiceImpl.updateTransactionPricingRequest(designMap);
	}
	
	@Test
	public void testUpdateTransactionPricingRequestNoUsoc() throws SalesBusinessException {
		designMap.put(StringConstants.TRANSACTION_UPDATE, StringConstants.RECONFIGURE);
		Mockito.when(nxMpDealRepository.findBySolutionId(anyLong())).thenReturn(deals);
		List<NxDesignDetails> designDetails = new ArrayList<>();
		NxDesign nxd = new NxDesign();
		nxd.setNxDesignId(101l);
		NxDesignDetails design = new NxDesignDetails();
		design.setDesignData("{\"country\":\"US\",\"nxSiteId\":5795,\"zipCode\":\"30005\",\"address\":null,\"referenceOfferId\":4,\"city\":\"Alpharetta\",\"address2\":null,\"address1\":\"300 Point Parkway\",\"postalCode\":null,\"siteName\":\"ALPHARETTA 1\",\"siteNpanxx\":null,\"regionCode\":null,\"swcClli\":\"ALPRGAMA\",\"siteId\":9870753,\"state\":\"GA\",\"customerLocationClli\":null,\"design\":[{\"portProtocol\":\"PPP\",\"accessSpeedUdfAttrId\":30375,\"accessDetails\":{\"supplierName\":null,\"npanxx\":null,\"nrcListRate\":null,\"serialNumber\":null,\"tokenId\":null,\"mrcListRate\":null,\"respAccessInterconnect\":null,\"dqid\":null,\"portId\":null,\"speed\":null,\"quoteId\":null,\"iglooMaxMrcDiscount\":null,\"respSpeed\":null,\"popClli\":null,\"respSupplierName\":null,\"currencyCode\":null,\"respPopClli\":null},\"sitePopCilli\":null,\"portId\":9252692,\"interface\":null,\"referenceSiteId\":9870753,\"lac\":null,\"accessSpeed\":\"1.544 mb\",\"accessType\":\"Private Line\",\"accessArchitecture\":null,\"accessTypeUdfAttrId\":30155,\"categoryLocalAccess\":null,\"portSpeed\":\"1.544M\",\"priceDetails\":[{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999928642,\"secondaryKeys\":\"#Intl#MPLS Port#Flat Rate#56K#DS0#FR#VPN Transport Connection#per port#17972#18030\",\"quantity\":1.0,\"priceType\":\"portNRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":85,\"frequency\":\"NRC\",\"rdsPriceType\":\"Access\",\"beid\":\"18030\",\"componentParentId\":9870753,\"productRateId\":930,\"reqPriceType\":\"Fixed\",\"term\":48,\"elementType\":\"Port\",\"uniqueId\":\"#One Time Charge#MPLS Port Activation Charge#ATM#N/A#N/A#Non Recurring Charges#N/A#Port\",\"localListPrice\":1000.0,\"referencePortId\":9252692},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999928642,\"secondaryKeys\":\"#FCC#MPLS Port#Flat Rate#1.544M/1.536M#T1, E1#FR, ATM, IP#VPN Transport Connection#per port#17986#18030#United States#US#USA\",\"quantity\":1.0,\"priceType\":\"portRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":78,\"frequency\":\"MRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"17986\",\"componentParentId\":9870753,\"productRateId\":884,\"reqPriceType\":null,\"term\":48,\"elementType\":\"Port\",\"uniqueId\":\"#MPLS Port#MPLS Port - 1.544M/1.536M#ATM#1.544Mbps/1.536Mbps#T1/E1#VPN Transport#Connection#Each\",\"localListPrice\":221.0,\"referencePortId\":9252692},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999928642,\"secondaryKeys\":\"#FCC#CoS Package#Multimedia Standard Svc#1.544M/1.536M#N/A#N/A#VPN Transport COS Package#per port#18265#18425#United States#US#USA\",\"quantity\":1.0,\"priceType\":null,\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":78,\"frequency\":\"MRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18265\",\"componentParentId\":9870753,\"productRateId\":1297,\"reqPriceType\":null,\"term\":48,\"elementType\":\"PortFeature\",\"uniqueId\":\"#CoS Package Multimedia Standard#CoS Package Multimedia Standard Svc - 1.544M/1.536M#ATM#1.544Mbps/1.536Mbps#T1/E1#VPN Transport#COS Package#Port\",\"localListPrice\":0.0,\"referencePortId\":9252692},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999928642,\"secondaryKeys\":\"#FCC#AT&T Owned and Managed Features#MPLS Managed CSU#Internal#T1/E1#Managed CPE Features#per circuit#18488#18520#United States#US#USA\",\"quantity\":1.0,\"priceType\":null,\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":87,\"frequency\":\"MRC\",\"rdsPriceType\":\"AVPN_CPE_ROUTER\",\"beid\":\"18488\",\"componentParentId\":9870753,\"productRateId\":1575,\"reqPriceType\":null,\"term\":48,\"elementType\":\"RouterFeatures\",\"uniqueId\":\"#Managed Router - CPE Features#MPLS Managed CSU Internal T1/E1#FR#N/A#T1/E1#Managed CPE#CPE features#Per T1/E1\",\"localListPrice\":40.0,\"referencePortId\":9252692},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999928642,\"secondaryKeys\":\"#FCC#MPLS Managed Lite Router#AT&T Owned and Managed#On-Site Install#Basic#Managed CPE Managed Router#per router#30000#18501#United States#US#USA\",\"quantity\":1.0,\"priceType\":null,\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":0,\"frequency\":\"NRC\",\"rdsPriceType\":\"AVPN_CPE_ROUTER\",\"beid\":\"18501\",\"componentParentId\":9870753,\"productRateId\":1591,\"reqPriceType\":null,\"term\":48,\"elementType\":\"RouterFeatures\",\"uniqueId\":\"#One Time Charges - Managed Router#MPLS MANAGED ROUTER ACTIVATION - AT&T INSTALL#All#N/A#N/A#Not Discountable#Not Discountable OTC#Router\",\"localListPrice\":1000.0,\"referencePortId\":9252692},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999928642,\"secondaryKeys\":\"#Intl#CoS Package#Multimedia High Svc#56K#VPN Transport COS Package#per port#18193#18425\",\"quantity\":1.0,\"priceType\":null,\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":85,\"frequency\":\"NRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18425\",\"componentParentId\":9870753,\"productRateId\":1498,\"reqPriceType\":null,\"term\":48,\"elementType\":\"PortFeature\",\"uniqueId\":\"#One Time Charge#MPLS CoS Activation Charge#ATM#N/A#N/A#Non Recurring Charges#COS Package#Port\",\"localListPrice\":0.0,\"referencePortId\":9252692},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999928642,\"secondaryKeys\":\"#FCC#MPLS Managed Router#AT&T Owned and Managed#N/A#Basic#Managed CPE Managed Router#per router#18495#18501#United States#US#USA\",\"quantity\":1.0,\"priceType\":null,\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":87,\"frequency\":\"MRC\",\"rdsPriceType\":\"AVPN_CPE_ROUTER\",\"beid\":\"18495\",\"componentParentId\":9870753,\"productRateId\":1585,\"reqPriceType\":null,\"term\":48,\"elementType\":\"StandardRouters\",\"uniqueId\":\"#Managed Routers - AT&T Managed#MPLS Managed Router - Basic#All#N/A#N/A#Managed CPE#Managed Router#Router\",\"localListPrice\":230.0,\"referencePortId\":9252692}],\"accessTailTechnology\":null,\"mileage\":null,\"siteType\":null}]}");
		design.setNxDesign(nxd);
		design.setComponentId("9252692");
		design.setProductName(MyPriceConstants.ADI);
		designDetails.add(design);
		Mockito.when(nxDesignDetailsRepository.findDesignDetailsaByNxSolutionId(anyLong())).thenReturn(designDetails);
		List<NxMpDesignDocument> designDocuments = new ArrayList<>();
		NxMpDesignDocument mpDesign = new NxMpDesignDocument();
		mpDesign.setMpDocumentNumber(3l);
		designDocuments.add(mpDesign);
		Mockito.when(nxMpDesignDocumentRepository.findByNxTxnIdAndNxDesignIdAndMpPartNumber(anyLong(), anyLong(), anyString())).thenReturn(designDocuments);
		Mockito.when(nxMpPriceDetailsRepository.save(any(NxMpPriceDetails.class))).thenReturn(new NxMpPriceDetails());
	
		Mockito.when(env.getProperty("myprice.updateTransactionPricingRequest")).thenReturn("https://custompricingst.att.com/rest/v8/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/actions/updateRequestedRates");
		String res = "{\"id\": \"1234\"}";
		Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString()))
		.thenReturn(res);
		Mockito.when(restClient.processResult(anyString(), any())).thenReturn( new UpdateTransactionPricingResponse());
		Mockito.when(nxMpPriceDetailsRepository.findByMpDocumentNumberAndNxTxnIdAndFrequency(anyLong(), anyLong(), anyString())).thenReturn(new NxMpPriceDetails());
		updateTransactionPricingFMOServiceImpl.updateTransactionPricingRequest(designMap);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUpdateTransactionPricingRequestExce() throws SalesBusinessException {
		Map<String, Object> designMap = new HashMap<String, Object>();
		designMap.put(FmoConstants.PORT_ID, "10101");
		Mockito.when(nxMpDealRepository.findBySolutionId(anyLong())).thenThrow(Exception.class);
		updateTransactionPricingFMOServiceImpl.updateTransactionPricingRequest(designMap);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testUpdateTransactionPricingRequestElse() throws SalesBusinessException {
		Map<String, Object> designMap = new HashMap<String, Object>();
		designMap.put(FmoConstants.PORT_ID, "10101");
		Mockito.when(nxMpDealRepository.findBySolutionId(anyLong())).thenReturn(deals);
		Mockito.when(updateTransactionPricingFMOServiceImpl.getRequest(anyMap(), anyLong(), any())).thenReturn(null);
		updateTransactionPricingFMOServiceImpl.updateTransactionPricingRequest(designMap);
	}
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCallUpdateTransactionPricingRequestApi() throws SalesBusinessException {
		Mockito.when(env.getProperty("myprice.updateTransactionPricingRequest")).thenReturn("https://custompricingst.att.com/rest/v8/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/actions/updateRequestedRates");
		Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString()))
		.thenThrow(SalesBusinessException.class);
		updateTransactionPricingFMOServiceImpl.callUpdateTransactionPricingRequestApi("121212", null);
		
	}
	
}
