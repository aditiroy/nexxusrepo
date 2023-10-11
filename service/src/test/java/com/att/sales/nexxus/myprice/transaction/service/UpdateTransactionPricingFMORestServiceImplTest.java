package com.att.sales.nexxus.myprice.transaction.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxDesign;
import com.att.sales.nexxus.dao.model.NxDesignDetails;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxMpDesignDocument;
import com.att.sales.nexxus.dao.repository.NxDesignDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxMpDesignDocumentRepository;
import com.att.sales.nexxus.dao.repository.NxMpPriceDetailsRepository;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTransactionPricingRequest;
import com.att.sales.nexxus.reteriveicb.model.PriceAttributes;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.RestClientUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)

public class UpdateTransactionPricingFMORestServiceImplTest {

	@Spy
	@InjectMocks
	private UpdateTransactionPricingFMORestServiceImpl updateTransactionPricingFMORestServiceImpl;
	
	@Mock
	private RestClientUtil restClient;

	@Mock
	private NxMpDealRepository nxMpDealRepository;

	@Mock
	private NxMpDesignDocumentRepository nxMpDesignDocumentRepository;

	@Mock
	private NxMpPriceDetailsRepository nxMpPriceDetailsRepository;
	
	@Mock
	private NxDesignDetailsRepository nxDesignDetailsRepository;
	
	@Mock
	private ObjectMapper mapper;
	
	@Mock 
	private ConfigAndUpdatePricingUtil configAndUpdatePricingUtil;
	
	@Mock 
	private RestCommonUtil restCommonUtil;
	
	@Mock 
	private Environment env;
	
	@Mock
	private HttpRestClient httpRest;
	
	String design="{\"site\":[{\"country\":\"US\",\"nxSiteId\":10204,\"zipCode\":\"95136\",\"address\":null,\"referenceOfferId\":4,\"isLineItemPicked\":\"Y\",\"city\":\"san jose\",\"address2\":null,\"address1\":"
			+ "\"20 shenado pl\",\"postalCode\":null,\"siteName\":\"test\",\"siteNpanxx\":\"408225\",\"regionCode\":null,\"swcClli\":\"SNJSCA13\",\"design\":[{\"portProtocol\":\"PPP\","
			+ "\"accessSpeedUdfAttrId\":30549,\"accessDetails\":{\"supplierName\":null,\"npanxx\":null,\"nrcListRate\":null,\"serialNumber\":null,\"tokenId\":null,\"mrcListRate\":null,"
			+ "\"respAccessInterconnect\":null,\"dqid\":null,\"physicalInterface\":null,\"portId\":null,\"speed\":null,\"quoteId\":null,\"iglooMaxMrcDiscount\":null,\"respSpeed\":null,\"popClli\":null,"
			+ "\"respSupplierName\":null,\"currencyCode\":null,\"respPopClli\":null},\"sitePopCilli\":null,\"portId\":9257029,\"interface\":null,\"referenceSiteId\":9958922,\"lac\":null,\"accessSpeed\":"
			+ "\"DS3\",\"accessType\":\"Private Line\",\"accessArchitecture\":null,\"accessTypeUdfAttrId\":30155,\"categoryLocalAccess\":null,\"portSpeed\":\"45M\",\"priceDetails\":[{\"country\":\"US\","
			+ "\"componentType\":\"Port\",\"priceScenarioId\":99999925473,\"quantity\":\"1\",\"priceType\":\"accessRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":0.0,\"frequency\":\"MRC\","
			+ "\"rdsPriceType\":\"ACCESS\",\"beid\":null,\"componentParentId\":9958922,\"productRateId\":2929,\"reqPriceType\":null,\"term\":60,\"localListPrice\":16852.78,\"referencePortId\":9257029},"
			+ "{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999925473,\"secondaryKeys\":\"#FCC#MPLS Port#Flat Rate#45M#T3#FR, ATM, IP#VPN Transport Connection#per port#18013#18030#United States#US#USA\","
			+ "\"quantity\":\"1\",\"priceType\":\"portRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":55.56,\"frequency\":\"MRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18013\",\"nrcBeid\":\"18030\",\"componentParentId"
			+ "\":9958922,\"productRateId\":906,\"reqPriceType\":null,\"term\":60,\"elementType\":\"Port\",\"uniqueId\":\"#MPLS Port#MPLS Port - 45M#FR, ATM, IP#45 Mbps#T3#VPN Transport#Connection#Each\",\"localListPrice\":1990.0,"
			+ "\"referencePortId\":9257029},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999925473,\"quantity\":\"1\",\"priceType\":\"portNRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":88.88,"
			+ "\"frequency\":\"NRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18030\",\"componentParentId\":9958922,\"productRateId\":930,\"reqPriceType\":\"Fixed\",\"term\":60,\"localListPrice\":1000.0,\"referencePortId\":9257029},"
			+ "{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999925473,\"secondaryKeys\":\"#FCC#CoS Package#Multimedia Standard Svc#45M#N\\/A#N\\/A#VPN Transport COS Package#per port#18292#18425#United States#US#USA\","
			+ "\"quantity\":\"1\",\"priceType\":\"cosRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":55.56,\"frequency\":\"MRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18292\",\"nrcBeid\":\"18425\",\"componentParentId"
			+ "\":9958922,\"productRateId\":1330,\"reqPriceType\":null,\"term\":60,\"elementType\":\"PortFeature\",\"uniqueId\":\"#CoS Package Multimedia Standard#CoS Package Multimedia Standard Svc - 45M#FR,"
			+ " ATM, IP#45 Mbps#T3#VPN Transport#COS Package#Port\",\"localListPrice\":0.0,\"referencePortId\":9257029},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999925473,\"quantity"
			+ "\":\"1\",\"priceType\":\"cosNRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":88.88,\"frequency\":\"NRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18425\",\"componentParentId\":9958922,\"productRateId\":"
			+ "1498,\"reqPriceType\":null,\"term\":60,\"localListPrice\":0.0,\"referencePortId\":9257029}],\"accessTailTechnology\":null,\"mileage\":null,\"siteType\":null}],\"siteId\":9958922,\"state\":\"CA\","
			+ "\"customerLocationClli\":null},{\"country\":\"US\",\"nxSiteId\":10204,\"zipCode\":\"95136\",\"address\":null,\"referenceOfferId\":4,\"isLineItemPicked\":\"Y\",\"city\":\"san jose\",\"address2\":null,"
			+ "\"address1\":\"20 shenado pl\",\"postalCode\":null,\"siteName\":\"test\",\"siteNpanxx\":\"408225\",\"regionCode\":null,\"swcClli\":\"SNJSCA13\",\"design\":[{\"portProtocol\":\"PPP\","
			+ "\"accessSpeedUdfAttrId\":30549,\"accessDetails\":{\"supplierName\":null,\"npanxx\":null,\"nrcListRate\":null,\"serialNumber\":null,\"tokenId\":null,\"mrcListRate\":null,"
			+ "\"respAccessInterconnect\":null,\"dqid\":null,\"physicalInterface\":null,\"portId\":null,\"speed\":null,\"quoteId\":null,\"iglooMaxMrcDiscount\":null,\"respSpeed\""
			+ ":null,\"popClli\":null,\"respSupplierName\":null,\"currencyCode\":null,\"respPopClli\":null},\"sitePopCilli\":null,\"portId\":9257029,\"interface\":null,\"referenceSiteId\":9958922,"
			+ "\"lac\":null,\"accessSpeed\":\"DS3\",\"accessType\":\"Private Line\",\"accessArchitecture\":null,\"accessTypeUdfAttrId\":30155,\"categoryLocalAccess\":null,\"portSpeed\":\"45M\",\"priceDetails\":[{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999925473,\"quantity\":\"1\",\"priceType\":\"accessRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":0.0,\"frequency\":\"MRC\",\"rdsPriceType\":\"ACCESS\",\"beid\":null,\"componentParentId\":9958922,\"productRateId\":2929,\"reqPriceType\":null,\"term\":60,\"localListPrice\":16852.78,\"referencePortId\":9257029},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999925473,\"secondaryKeys\":\"#FCC#MPLS Port#Flat Rate#45M#T3#FR, ATM, IP#VPN Transport Connection#per port#18013#18030#United States#US#USA\",\"quantity\":\"1\",\"priceType\":\"portRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":50.56,\"frequency\":\"MRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18013\",\"nrcBeid\":\"18030\",\"componentParentId\":9958922,\"productRateId\":906,\"reqPriceType\":null,\"term\":60,\"elementType\":\"Port\",\"uniqueId\":\"#MPLS Port#MPLS Port - 45M#FR, ATM, IP#45 Mbps#T3#VPN Transport#Connection#Each\",\"localListPrice\":2000.0,\"referencePortId\":9257029},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999925473,\"quantity\":\"1\",\"priceType\":\"portNRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":88.88,\"frequency\":\"NRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18030\",\"componentParentId\":9958922,\"productRateId\":930,\"reqPriceType\":\"Fixed\",\"term\":60,\"localListPrice\":1000.0,\"referencePortId\":9257029},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999925473,\"secondaryKeys\":\"#FCC#CoS Package#Multimedia Standard Svc#45M#N\\/A#N\\/A#VPN Transport COS Package#per port#18292#18425#United States#US#USA\",\"quantity\":\"1\",\"priceType\":\"cosRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":50.56,\"frequency\":\"MRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18292\",\"nrcBeid\":\"18425\",\"componentParentId\":9958922,\"productRateId\":1330,\"reqPriceType\":null,\"term\":60,\"elementType\":\"PortFeature\",\"uniqueId\":\"#CoS Package Multimedia Standard#CoS Package Multimedia Standard Svc - 45M#FR, ATM, IP#45 Mbps#T3#VPN Transport#COS Package#Port\",\"localListPrice\":20.0,\"referencePortId\":9257029},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999925473,\"quantity\":\"1\",\"priceType\":\"cosNRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":88.88,\"frequency\":\"NRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18425\",\"componentParentId\":9958922,\"productRateId\":1498,\"reqPriceType\":null,\"term\":60,\"localListPrice\":0.0,\"referencePortId\":9257029}],\"accessTailTechnology\":null,\"mileage\":null,\"siteType\":null}],\"siteId\":9958923,\"state\":\"CA\",\"customerLocationClli\":null}]}";
	
	
	@Test
	public void updateTransactionPricingRequestTest() throws IOException, SalesBusinessException {
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		inputParamMap.put(MyPriceConstants.NX_SOLIUTION_ID,4765l);
		List<NxMpDeal> deals=new ArrayList<NxMpDeal>();
		NxMpDeal nd=new NxMpDeal();
		nd.setDealID("123");
		nd.setTransactionId("123");
		deals.add(nd);
		when(nxMpDealRepository.findBySolutionId(any())).thenReturn(deals);
		NxDesign nxDesign=new NxDesign();
		nxDesign.setNxDesignId(9l);
		List<NxDesignDetails> designDetails =new ArrayList<NxDesignDetails>();
		NxDesignDetails ndl=new NxDesignDetails();
		ndl.setNxDesign(nxDesign);
		ndl.setDesignData(design);
		ndl.setComponentId("1");
		designDetails.add(ndl);
		when(nxDesignDetailsRepository.findDesignDetailsaByNxSolutionId(any())).thenReturn(designDetails);
		JsonNode input=JacksonUtil.toJsonNode(design);
		when(mapper.readTree(anyString())).thenReturn(input);
		List<PriceAttributes> priceAttributes = new ArrayList<PriceAttributes>();
		PriceAttributes p=new PriceAttributes();
		p.setBeid("123");
		p.setReferencePortId(9257029l);
		p.setFrequency("MRC");
		p.setLocalListPrice(67d);
		p.setNrcBeid("67");
		p.setIcbDesiredDiscPerc(1d);
		priceAttributes.add(p);
		
		PriceAttributes p2=new PriceAttributes();
		p2.setBeid("67");
		p2.setReferencePortId(9257029l);
		p2.setFrequency("NRC");
		
		p2.setLocalListPrice(67d);
		p2.setIcbDesiredDiscPerc(1d);
		priceAttributes.add(p);
		priceAttributes.add(p2);
		when(configAndUpdatePricingUtil.getPriceAttributes(any(),anyString())).thenReturn(priceAttributes);
		
		List<NxMpDesignDocument> designDocuments = new ArrayList<NxMpDesignDocument>();
		NxMpDesignDocument nx=new NxMpDesignDocument();
		nx.setUsocId("123");
		nx.setMpDocumentNumber(8l);
		nx.setNxDocumentId(2l);
		designDocuments.add(nx);
		
		NxMpDesignDocument nx2=new NxMpDesignDocument();
		nx2.setUsocId("456");
		nx2.setNxDocumentId(21l);
		nx2.setMpDocumentNumber(8l);
		designDocuments.add(nx2);
		when(nxMpDesignDocumentRepository.findByNxTxnIdAndNxDesignIdAndMpPartNumber(any(),any(),any())).thenReturn(designDocuments);
		String uri = "https://custompricingdev2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/actions/updateRequestedRates";
		when(env.getProperty(any())).thenReturn(uri);
		String res = "ddg";
		Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString()))
		.thenReturn(res);
		updateTransactionPricingFMORestServiceImpl.updateTransactionPricingRequest(inputParamMap);
	}
	
	@Test
	public void updateTransactionPricingRequestTest1() throws IOException, SalesBusinessException {
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		inputParamMap.put(MyPriceConstants.NX_SOLIUTION_ID,4765l);
		List<NxMpDeal> deals=new ArrayList<NxMpDeal>();
		NxMpDeal nd=new NxMpDeal();
		nd.setDealID("123");
		nd.setTransactionId("123");
		deals.add(nd);
		when(nxMpDealRepository.findBySolutionId(any())).thenReturn(deals);
		NxDesign nxDesign=new NxDesign();
		nxDesign.setNxDesignId(9l);
		List<NxDesignDetails> designDetails =new ArrayList<NxDesignDetails>();
		NxDesignDetails ndl=new NxDesignDetails();
		ndl.setNxDesign(nxDesign);
		ndl.setDesignData(design);
		ndl.setComponentId("1");
		designDetails.add(ndl);
		when(nxDesignDetailsRepository.findDesignDetailsaByNxSolutionId(any())).thenReturn(designDetails);
		JsonNode input=JacksonUtil.toJsonNode(design);
		when(mapper.readTree(anyString())).thenReturn(input);
		List<PriceAttributes> priceAttributes = new ArrayList<PriceAttributes>();
		PriceAttributes p=new PriceAttributes();
		p.setBeid("123");
		p.setReferencePortId(9257029l);
		p.setFrequency("MRC");
		p.setLocalListPrice(67d);
		p.setNrcBeid("67");
		p.setIcbDesiredDiscPerc(1d);
		priceAttributes.add(p);
		
		PriceAttributes p2=new PriceAttributes();
		p2.setBeid("67");
		p2.setReferencePortId(9257029l);
		p2.setFrequency("NRC");
		
		p2.setLocalListPrice(67d);
		p2.setIcbDesiredDiscPerc(1d);
		priceAttributes.add(p);
		priceAttributes.add(p2);
		when(configAndUpdatePricingUtil.getPriceAttributes(any(),anyString())).thenReturn(priceAttributes);
		
		List<NxMpDesignDocument> designDocuments = new ArrayList<NxMpDesignDocument>();
		NxMpDesignDocument nx=new NxMpDesignDocument();
		nx.setUsocId("123");
		nx.setMpDocumentNumber(8l);
		nx.setNxDocumentId(2l);
		designDocuments.add(nx);
		
		NxMpDesignDocument nx2=new NxMpDesignDocument();
		nx2.setUsocId("456");
		nx2.setNxDocumentId(21l);
		nx2.setMpDocumentNumber(8l);
		designDocuments.add(nx2);
		when(nxMpDesignDocumentRepository.findByNxTxnIdAndNxDesignIdAndMpPartNumber(any(),any(),any())).thenReturn(designDocuments);
		String uri = "https://custompricingdev2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/actions/updateRequestedRates";
		when(env.getProperty(any())).thenReturn(uri);
		String res = "ddg";
		Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString()))
		.thenReturn(res);
		updateTransactionPricingFMORestServiceImpl.updateTransactionPricingRequest(inputParamMap);
	}
	
	
	
	
	@Test
	public void updateTransactionPricingRequestTest2() throws IOException {
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		inputParamMap.put(MyPriceConstants.NX_SOLIUTION_ID,4765l);
		List<NxMpDeal> deals=new ArrayList<NxMpDeal>();
		NxMpDeal nd=new NxMpDeal();
		nd.setDealID("123");
		nd.setTransactionId("123");
		deals.add(nd);
		when(nxMpDealRepository.findBySolutionId(any())).thenReturn(deals);
		NxDesign nxDesign=new NxDesign();
		nxDesign.setNxDesignId(9l);
		List<NxDesignDetails> designDetails =new ArrayList<NxDesignDetails>();
		NxDesignDetails ndl=new NxDesignDetails();
		ndl.setNxDesign(nxDesign);
		ndl.setProductName("ADI");
		ndl.setDesignData(design);
		ndl.setComponentId("1");
		designDetails.add(ndl);
		when(nxDesignDetailsRepository.findDesignDetailsaByNxSolutionId(any())).thenReturn(designDetails);
		JsonNode input=JacksonUtil.toJsonNode(design);
		when(mapper.readTree(anyString())).thenReturn(input);
		List<PriceAttributes> priceAttributes = new ArrayList<PriceAttributes>();
		PriceAttributes p=new PriceAttributes();
		p.setProductRateId(123l);
		p.setReferencePortId(9257029l);
		p.setFrequency("MRC");
		p.setRequestedRate(3f);
		p.setNrcBeid("67");
		p.setIcbDesiredDiscPerc(1d);
		p.setTerm(1l);
		priceAttributes.add(p);
		
		PriceAttributes p2=new PriceAttributes();
		p2.setProductRateId(67l);
		p2.setReferencePortId(9257029l);
		p2.setFrequency("NRC");
		p2.setRequestedRate(3f);
		p2.setIcbDesiredDiscPerc(1d);
		p2.setTerm(1l);
		priceAttributes.add(p);
		priceAttributes.add(p2);
		when(configAndUpdatePricingUtil.getPriceAttributes(any(),anyString())).thenReturn(priceAttributes);
		
		List<NxMpDesignDocument> designDocuments = new ArrayList<NxMpDesignDocument>();
		NxMpDesignDocument nx=new NxMpDesignDocument();
		nx.setUsocId("123");
		nx.setMpDocumentNumber(8l);
		nx.setNxDocumentId(2l);
		designDocuments.add(nx);
		
		NxMpDesignDocument nx2=new NxMpDesignDocument();
		nx2.setUsocId("456");
		nx2.setNxDocumentId(21l);
		nx2.setMpDocumentNumber(8l);
		designDocuments.add(nx2);
		when(nxMpDesignDocumentRepository.findByNxTxnIdAndNxDesignIdAndMpPartNumber(any(),any(),any())).thenReturn(designDocuments);
		updateTransactionPricingFMORestServiceImpl.updateTransactionPricingRequest(inputParamMap);
	}
	
	@Test
	public void updateTransactionPricingRequestTest3() throws IOException {
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		inputParamMap.put(MyPriceConstants.NX_SOLIUTION_ID,4765l);
		List<NxMpDeal> deals=new ArrayList<NxMpDeal>();
		NxMpDeal nd=new NxMpDeal();
		nd.setDealID("123");
		nd.setTransactionId("123");
		deals.add(nd);
		when(nxMpDealRepository.findBySolutionId(any())).thenReturn(deals);
		NxDesign nxDesign=new NxDesign();
		nxDesign.setNxDesignId(9l);
		List<NxDesignDetails> designDetails =new ArrayList<NxDesignDetails>();
		NxDesignDetails ndl=new NxDesignDetails();
		ndl.setNxDesign(nxDesign);
		ndl.setProductName("ADI");
		ndl.setDesignData(design);
		ndl.setComponentId("1");
		designDetails.add(ndl);
		when(nxDesignDetailsRepository.findDesignDetailsaByNxSolutionId(any())).thenReturn(designDetails);
		JsonNode input=JacksonUtil.toJsonNode(design);
		when(mapper.readTree(anyString())).thenReturn(input);
		List<PriceAttributes> priceAttributes = new ArrayList<PriceAttributes>();
		PriceAttributes p=new PriceAttributes();
		p.setProductRateId(123l);
		p.setReferencePortId(9257029l);
		
		p.setRequestedRate(3f);
		p.setNrcBeid("67");
		p.setRdsPriceType("Access");
		p.setIcbDesiredDiscPerc(1d);
		p.setTerm(1l);
		priceAttributes.add(p);
		
		PriceAttributes p2=new PriceAttributes();
		p2.setProductRateId(67l);
		p2.setReferencePortId(9257029l);
		p2.setFrequency("NRC");
		p2.setRequestedRate(3f);
		p2.setIcbDesiredDiscPerc(1d);
		p2.setTerm(1l);
		priceAttributes.add(p);
		priceAttributes.add(p2);
		when(configAndUpdatePricingUtil.getPriceAttributes(any(),anyString())).thenReturn(priceAttributes);
		
		List<NxMpDesignDocument> designDocuments = new ArrayList<NxMpDesignDocument>();
		NxMpDesignDocument nx=new NxMpDesignDocument();
		nx.setUsocId("123");
		nx.setMpDocumentNumber(8l);
		nx.setNxDocumentId(2l);
		designDocuments.add(nx);
		
		NxMpDesignDocument nx2=new NxMpDesignDocument();
		
		nx2.setNxDocumentId(21l);
		nx2.setMpDocumentNumber(8l);
		designDocuments.add(nx2);
		when(nxMpDesignDocumentRepository.findByNxTxnIdAndNxDesignIdAndMpPartNumber(any(),any(),any())).thenReturn(designDocuments);
		updateTransactionPricingFMORestServiceImpl.updateTransactionPricingRequest(inputParamMap);
	}
	
	@Test
	public void updateTransactionPricingRequestTest4() throws IOException {
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		inputParamMap.put(MyPriceConstants.NX_SOLIUTION_ID,4765l);
		List<NxMpDeal> deals=new ArrayList<NxMpDeal>();
		NxMpDeal nd=new NxMpDeal();
		nd.setDealID("123");
		nd.setTransactionId("123");
		deals.add(nd);
		when(nxMpDealRepository.findBySolutionId(any())).thenReturn(deals);
		NxDesign nxDesign=new NxDesign();
		nxDesign.setNxDesignId(9l);
		List<NxDesignDetails> designDetails =new ArrayList<NxDesignDetails>();
		NxDesignDetails ndl=new NxDesignDetails();
		ndl.setNxDesign(nxDesign);
		ndl.setProductName("ADI");
		ndl.setDesignData(design);
		ndl.setComponentId("1");
		designDetails.add(ndl);
		when(nxDesignDetailsRepository.findDesignDetailsaByNxSolutionId(any())).thenReturn(designDetails);
		JsonNode input=JacksonUtil.toJsonNode(design);
		when(mapper.readTree(anyString())).thenReturn(input);
		
		updateTransactionPricingFMORestServiceImpl.updateTransactionPricingRequest(inputParamMap);
	}
	

	@Test
	public void callUpdateTransactionPricingRequestApiException() throws SalesBusinessException {
		
		UpdateTransactionPricingRequest request=new UpdateTransactionPricingRequest();
		String uri = "https://custompricingdev2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/actions/updateRequestedRates";
		when(env.getProperty(any())).thenReturn(uri);
		doThrow(new SalesBusinessException("")).when(httpRest).callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString());
		updateTransactionPricingFMORestServiceImpl.callUpdateTransactionPricingRequestApi("1", request);
	}
	
	@Disabled
	@Test
	public void getSitesException() throws JsonMappingException, JsonProcessingException {
	//	doThrow(new JsonMappingException("")).when(mapper).readTree(anyString());
		updateTransactionPricingFMORestServiceImpl.getSites("");
	}
	
	
	@Test
	public void updateTransactionPricingRequestTest7() throws IOException {
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		inputParamMap.put(MyPriceConstants.NX_SOLIUTION_ID,4765l);
		List<NxMpDeal> deals=new ArrayList<NxMpDeal>();
		NxMpDeal nd=new NxMpDeal();
		nd.setDealID("123");
		nd.setTransactionId("123");
		deals.add(nd);
		when(nxMpDealRepository.findBySolutionId(any())).thenReturn(deals);
		NxDesign nxDesign=new NxDesign();
		nxDesign.setNxDesignId(9l);
		List<NxDesignDetails> designDetails =new ArrayList<NxDesignDetails>();
		NxDesignDetails ndl=new NxDesignDetails();
		ndl.setNxDesign(nxDesign);
		ndl.setProductName("BVoIP");
		ndl.setDesignData(design);
		ndl.setComponentId("1");
		designDetails.add(ndl);
		when(nxDesignDetailsRepository.findDesignDetailsaByNxSolutionId(any())).thenReturn(designDetails);
		JsonNode input=JacksonUtil.toJsonNode(design);
		when(mapper.readTree(anyString())).thenReturn(input);
		List<PriceAttributes> priceAttributes = new ArrayList<PriceAttributes>();
		PriceAttributes p=new PriceAttributes();
		p.setProductRateId(123l);
		p.setReferencePortId(9257029l);
		
		p.setRequestedRate(3f);
		p.setNrcBeid("67");
		p.setRdsPriceType("Access");
		p.setIcbDesiredDiscPerc(1d);
		p.setTerm(1l);
		priceAttributes.add(p);
		
		PriceAttributes p2=new PriceAttributes();
		p2.setProductRateId(67l);
		p2.setReferencePortId(9257029l);
		p2.setFrequency("NRC");
		p2.setRequestedRate(3f);
		p2.setIcbDesiredDiscPerc(1d);
		p2.setTerm(1l);
		priceAttributes.add(p);
		priceAttributes.add(p2);
		when(configAndUpdatePricingUtil.getPriceAttributes(any(),anyString())).thenReturn(priceAttributes);
		
		List<NxMpDesignDocument> designDocuments = new ArrayList<NxMpDesignDocument>();
		NxMpDesignDocument nx=new NxMpDesignDocument();
		nx.setUsocId("123");
		nx.setMpDocumentNumber(8l);
		nx.setNxDocumentId(2l);
		designDocuments.add(nx);
		
		NxMpDesignDocument nx2=new NxMpDesignDocument();
		
		nx2.setNxDocumentId(21l);
		nx2.setMpDocumentNumber(8l);
		designDocuments.add(nx2);
		when(nxMpDesignDocumentRepository.findByNxTxnIdAndNxDesignIdAndUsocIds(any(),any(),any())).thenReturn(designDocuments);
		updateTransactionPricingFMORestServiceImpl.updateTransactionPricingRequest(inputParamMap);
	}
	
}
