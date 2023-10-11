package com.att.sales.nexxus.handlers;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.repository.NxDesignRepository;
import com.att.sales.nexxus.myprice.transaction.dao.service.NxMpRepositoryService;
import com.att.sales.nexxus.myprice.transaction.model.ConfigRespProcessingBean;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdatePricingUtil;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdatePricingUtilFmo;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdatePricingUtilInr;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.att.sales.nexxus.ws.utility.ConfigureSolnAndProductWSClientUtility;
import com.att.sales.nexxus.ws.utility.WSProcessingService;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.CommonStatusType;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.Configure;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.ConfigureResponse;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.ObjectFactory;
@ExtendWith(MockitoExtension.class)
public class ConfigureSolnAndProductWSHandlerFmoTest {
	
	@InjectMocks
	@Spy
	ConfigureSolnAndProductWSHandlerFmo wsHandlerTest;

	@Mock
	private NxMpRepositoryService nxMpRepositoryService;

	@Mock
	private WSProcessingService wsProcessingService;

	@Mock
	private NexxusJsonUtility nexxusJsonUtility;

	@Mock
	private ConfigAndUpdatePricingUtil configAndUpdatePricingUtil;
	
	@Mock
	private NxDesignRepository nxDesignRepository;
	
	@Mock
	private ConfigAndUpdatePricingUtilFmo configAndUpdatePricingUtilFmo;
	
	@Mock
	private ConfigureSolnAndProductWSClientUtility configureSolnAndProductWSClientUtility;
	
	@Mock
	private ConfigAndUpdatePricingUtilInr configAndUpdatePricingUtilInr;

	@BeforeEach
	public void initializeServiceMetaData() {
		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(ServiceMetaData.OFFER, "AVPN");
		requestParams.put(ServiceMetaData.REST_REQUEST_START_TIME, System.currentTimeMillis());
		requestParams.put(ServiceMetaData.VERSION, "1.0");
		requestParams.put(ServiceMetaData.METHOD, "TestMethod");
		requestParams.put(ServiceMetaData.URI, "Testuri");
		ServiceMetaData.add(requestParams);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void test() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("myPriceTransId", "42373160");
		map.put("nxTxnId", 21L);
		map.put("offerName", "DOMESTIC PLIOC");
		map.put(MyPriceConstants.SOURCE, "INR");
		String json = "{\"country\":\"US\",\"nxSiteId\":10204,\"zipCode\":\"95136\",\"address\":null,\"referenceOfferId\":4,\"isLineItemPicked\":\"Y\",\"city\":\"san jose\",\"address2\":null,\"address1\":\"20 shenado pl\",\"postalCode\":null,\"siteName\":\"test\",\"siteNpanxx\":\"408225\",\"regionCode\":null,\"swcClli\":\"SNJSCA13\",\"siteId\":9958922,\"state\":\"CA\",\"customerLocationClli\":null,\"design\":[{\"portProtocol\":\"PPP\",\"accessSpeedUdfAttrId\":30549,\"accessDetails\":{\"supplierName\":null,\"npanxx\":null,\"nrcListRate\":null,\"serialNumber\":null,\"tokenId\":null,\"mrcListRate\":null,\"respAccessInterconnect\":null,\"dqid\":null,\"physicalInterface\":null,\"portId\":null,\"speed\":null,\"quoteId\":null,\"iglooMaxMrcDiscount\":null,\"respSpeed\":null,\"popClli\":null,\"respSupplierName\":null,\"currencyCode\":null,\"respPopClli\":null},\"sitePopCilli\":null,\"portId\":9257029,\"interface\":null,\"referenceSiteId\":9958922,\"lac\":null,\"accessSpeed\":\"DS3\",\"accessType\":\"Private Line\",\"accessArchitecture\":null,\"accessTypeUdfAttrId\":30155,\"categoryLocalAccess\":null,\"portSpeed\":\"45M\",\"priceDetails\":[{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999925473,\"quantity\":\"1\",\"priceType\":\"accessRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":0.0,\"frequency\":\"MRC\",\"rdsPriceType\":\"ACCESS\",\"beid\":null,\"componentParentId\":9958922,\"productRateId\":2929,\"reqPriceType\":null,\"term\":60,\"localListPrice\":16852.78,\"referencePortId\":9257029},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999925473,\"secondaryKeys\":\"#FCC#MPLS Port#Flat Rate#45M#T3#FR, ATM, IP#VPN Transport Connection#per port#18013#18030#United States#US#USA\",\"quantity\":\"1\",\"priceType\":\"portRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":55.56,\"frequency\":\"MRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18013\",\"nrcBeid\":\"18030\",\"componentParentId\":9958922,\"productRateId\":906,\"reqPriceType\":null,\"term\":60,\"elementType\":\"Port\",\"uniqueId\":\"#MPLS Port#MPLS Port - 45M#FR, ATM, IP#45 Mbps#T3#VPN Transport#Connection#Each\",\"localListPrice\":1990.0,\"referencePortId\":9257029},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999925473,\"quantity\":\"1\",\"priceType\":\"portNRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":88.88,\"frequency\":\"NRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18030\",\"componentParentId\":9958922,\"productRateId\":930,\"reqPriceType\":\"Fixed\",\"term\":60,\"localListPrice\":1000.0,\"referencePortId\":9257029},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999925473,\"secondaryKeys\":\"#FCC#CoS Package#Multimedia Standard Svc#45M#N/A#N/A#VPN Transport COS Package#per port#18292#18425#United States#US#USA\",\"quantity\":\"1\",\"priceType\":\"cosRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":55.56,\"frequency\":\"MRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18292\",\"nrcBeid\":\"18425\",\"componentParentId\":9958922,\"productRateId\":1330,\"reqPriceType\":null,\"term\":60,\"elementType\":\"PortFeature\",\"uniqueId\":\"#CoS Package Multimedia Standard#CoS Package Multimedia Standard Svc - 45M#FR, ATM, IP#45 Mbps#T3#VPN Transport#COS Package#Port\",\"localListPrice\":0.0,\"referencePortId\":9257029},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999925473,\"quantity\":\"1\",\"priceType\":\"cosNRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":88.88,\"frequency\":\"NRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18425\",\"componentParentId\":9958922,\"productRateId\":1498,\"reqPriceType\":null,\"term\":60,\"localListPrice\":0.0,\"referencePortId\":9257029}],\"accessTailTechnology\":null,\"mileage\":null,\"siteType\":null}]}";
		doNothing().when(configAndUpdatePricingUtil).setProductTypeForInrFmo(map);
		doNothing().when(configureSolnAndProductWSClientUtility).setWsName(MyPriceConstants.CONFIG_SOL_PRODUCT_WS);
		Mockito.when(wsProcessingService.initiateWebService(any(Configure.class),
				any(ConfigureSolnAndProductWSClientUtility.class), anyMap(), any(Class.class)))
				.thenReturn(getConfigureResponse());
		Mockito.when(nxMpRepositoryService.getDataByNxtxnIdInr(21L)).thenReturn(new HashMap<String,Set<String>>());
		
		Mockito.when(configAndUpdatePricingUtilInr.getConfigProdutMapFromLookup(anyString())).thenReturn(new HashMap<String,List<String>>());
		
		doNothing().when(wsHandlerTest).processConfigSolAndProductResponse(getConfigureResponse(), map);
		wsHandlerTest.initiateConfigSolnAndProdWebService(map, json);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWithExceptionScenario() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("myPriceTransId", "42373160");
		map.put("nxTxnId", 21L);
		map.put("offerName", "DOMESTIC PLIOC");
		map.put(MyPriceConstants.SOURCE, "INR");
		String json = "{\"country\":\"US\",\"nxSiteId\":10204,\"zipCode\":\"95136\",\"address\":null,\"referenceOfferId\":4,\"isLineItemPicked\":\"Y\",\"city\":\"san jose\",\"address2\":null,\"address1\":\"20 shenado pl\",\"postalCode\":null,\"siteName\":\"test\",\"siteNpanxx\":\"408225\",\"regionCode\":null,\"swcClli\":\"SNJSCA13\",\"siteId\":9958922,\"state\":\"CA\",\"customerLocationClli\":null,\"design\":[{\"portProtocol\":\"PPP\",\"accessSpeedUdfAttrId\":30549,\"accessDetails\":{\"supplierName\":null,\"npanxx\":null,\"nrcListRate\":null,\"serialNumber\":null,\"tokenId\":null,\"mrcListRate\":null,\"respAccessInterconnect\":null,\"dqid\":null,\"physicalInterface\":null,\"portId\":null,\"speed\":null,\"quoteId\":null,\"iglooMaxMrcDiscount\":null,\"respSpeed\":null,\"popClli\":null,\"respSupplierName\":null,\"currencyCode\":null,\"respPopClli\":null},\"sitePopCilli\":null,\"portId\":9257029,\"interface\":null,\"referenceSiteId\":9958922,\"lac\":null,\"accessSpeed\":\"DS3\",\"accessType\":\"Private Line\",\"accessArchitecture\":null,\"accessTypeUdfAttrId\":30155,\"categoryLocalAccess\":null,\"portSpeed\":\"45M\",\"priceDetails\":[{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999925473,\"quantity\":\"1\",\"priceType\":\"accessRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":0.0,\"frequency\":\"MRC\",\"rdsPriceType\":\"ACCESS\",\"beid\":null,\"componentParentId\":9958922,\"productRateId\":2929,\"reqPriceType\":null,\"term\":60,\"localListPrice\":16852.78,\"referencePortId\":9257029},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999925473,\"secondaryKeys\":\"#FCC#MPLS Port#Flat Rate#45M#T3#FR, ATM, IP#VPN Transport Connection#per port#18013#18030#United States#US#USA\",\"quantity\":\"1\",\"priceType\":\"portRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":55.56,\"frequency\":\"MRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18013\",\"nrcBeid\":\"18030\",\"componentParentId\":9958922,\"productRateId\":906,\"reqPriceType\":null,\"term\":60,\"elementType\":\"Port\",\"uniqueId\":\"#MPLS Port#MPLS Port - 45M#FR, ATM, IP#45 Mbps#T3#VPN Transport#Connection#Each\",\"localListPrice\":1990.0,\"referencePortId\":9257029},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999925473,\"quantity\":\"1\",\"priceType\":\"portNRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":88.88,\"frequency\":\"NRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18030\",\"componentParentId\":9958922,\"productRateId\":930,\"reqPriceType\":\"Fixed\",\"term\":60,\"localListPrice\":1000.0,\"referencePortId\":9257029},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999925473,\"secondaryKeys\":\"#FCC#CoS Package#Multimedia Standard Svc#45M#N/A#N/A#VPN Transport COS Package#per port#18292#18425#United States#US#USA\",\"quantity\":\"1\",\"priceType\":\"cosRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":55.56,\"frequency\":\"MRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18292\",\"nrcBeid\":\"18425\",\"componentParentId\":9958922,\"productRateId\":1330,\"reqPriceType\":null,\"term\":60,\"elementType\":\"PortFeature\",\"uniqueId\":\"#CoS Package Multimedia Standard#CoS Package Multimedia Standard Svc - 45M#FR, ATM, IP#45 Mbps#T3#VPN Transport#COS Package#Port\",\"localListPrice\":0.0,\"referencePortId\":9257029},{\"country\":\"US\",\"componentType\":\"Port\",\"priceScenarioId\":99999925473,\"quantity\":\"1\",\"priceType\":\"cosNRCRateId\",\"localCurrency\":\"USD\",\"icbDesiredDiscPerc\":88.88,\"frequency\":\"NRC\",\"rdsPriceType\":\"PORT\",\"beid\":\"18425\",\"componentParentId\":9958922,\"productRateId\":1498,\"reqPriceType\":null,\"term\":60,\"localListPrice\":0.0,\"referencePortId\":9257029}],\"accessTailTechnology\":null,\"mileage\":null,\"siteType\":null}]}";
		Mockito.when(wsProcessingService.initiateWebService(any(Configure.class),
				any(ConfigureSolnAndProductWSClientUtility.class), anyMap(), any(Class.class)))
				.thenThrow(SoapFaultClientException.class);
		wsHandlerTest.initiateConfigSolnAndProdWebService(map, json);
	}
	
	private ConfigureResponse getConfigureResponse() {
		ConfigureResponse response = new ConfigureResponse();
		ObjectFactory objectFactory = new ObjectFactory();
		response.setTransaction(
				objectFactory.createConfigureResponseTransaction(objectFactory.createTransactionType()));
		response.getTransaction().getValue().setDataXml(objectFactory.createAnyType());
		response.getTransaction().getValue().getDataXml().getAny().add(createTransactionElement());
		CommonStatusType commonstatus = new CommonStatusType();
		commonstatus.setSuccess(new JAXBElement<String>(new QName("success"), String.class, "true"));
		response.setStatus(commonstatus);
		return response;
	}

	private Element createTransactionElement() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Document document = db.newDocument();

		Element transaction = document.createElement("bmt:transaction");
		Element subDocuments = document.createElement("bmt:sub_documents");

		Element transactionLine1 = document.createElement("bmt:transactionLine");
		Element lineBomId1 = document.createElement("bmt:_line_bom_id");
		Element lineBomParentId1 = document.createElement("bmt:_line_bom_parent_id");
		Element documentNumber1 = document.createElement("bmt:_document_number");
		Element prodLineId1 = document.createElement("bmt:_model_product_line_id");
		Element parentDocNumber1 = document.createElement("bmt:_parent_doc_number");

		lineBomId1.appendChild(document.createTextNode("BOM_Solution"));
		lineBomParentId1.appendChild(document.createTextNode("BOM_Solution"));
		documentNumber1.appendChild(document.createTextNode("1"));
		prodLineId1.appendChild(document.createTextNode("37903795"));
		parentDocNumber1.appendChild(document.createTextNode(null));

		Element transactionLine2 = document.createElement("bmt:transactionLine");
		Element lineBomId2 = document.createElement("bmt:_line_bom_id");
		Element lineBomParentId2 = document.createElement("bmt:_line_bom_parent_id");
		Element documentNumber2 = document.createElement("bmt:_document_number");
		Element prodLineId2 = document.createElement("bmt:_model_product_line_id");
		Element parentDocNumber2 = document.createElement("bmt:_parent_doc_number");

		lineBomId2.appendChild(document.createTextNode("BOM_SolutionASE"));
		lineBomParentId2.appendChild(document.createTextNode("BOM_Solution"));
		documentNumber2.appendChild(document.createTextNode("2"));
		prodLineId2.appendChild(document.createTextNode("37903795"));
		parentDocNumber2.appendChild(document.createTextNode("1"));

		transactionLine1.appendChild(lineBomId1);
		transactionLine1.appendChild(lineBomParentId1);
		transactionLine1.appendChild(documentNumber1);
		transactionLine1.appendChild(prodLineId1);
		transactionLine1.appendChild(parentDocNumber1);

		transactionLine2.appendChild(lineBomId2);
		transactionLine2.appendChild(lineBomParentId2);
		transactionLine2.appendChild(documentNumber2);
		transactionLine2.appendChild(prodLineId2);
		transactionLine2.appendChild(parentDocNumber2);

		subDocuments.appendChild(transactionLine1);
		subDocuments.appendChild(transactionLine2);
		transaction.appendChild(subDocuments);
		document.appendChild(transaction);
		return transaction;
	}
	
	@Test
	public void testCreateResponseBean() {
		Map<String,Object> methodParam = new HashMap<String,Object>();
		methodParam.put(MyPriceConstants.NX_TRANSACTION_ID, 1234L);
		methodParam.put(MyPriceConstants.MP_TRANSACTION_ID, "123456");
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Document document = db.newDocument();

		Element transaction = document.createElement("bmt:transaction");
		Element lineBomId1 = document.createElement("bmt:_line_bom_id");
		Element lineBomParentId1 = document.createElement("bmt:_line_bom_parent_id");
		Element documentNumber1 = document.createElement("bmt:_document_number");
		Element parentDocNumber1 = document.createElement("bmt:_parent_doc_number");
		Element parentLineItem = document.createElement("bmt:_parent_line_item");
		Element modelName = document.createElement("bmt:_model_name");
		Element modelVarName = document.createElement("bmt:_model_variable_name");
		
		transaction.appendChild(lineBomId1);
		transaction.appendChild(lineBomParentId1);
		transaction.appendChild(documentNumber1);
		transaction.appendChild(parentLineItem);
		transaction.appendChild(parentDocNumber1);
		transaction.appendChild(modelName);
		transaction.appendChild(modelVarName);
		transaction.setAttribute("bmt:bs_id", "123456");
		wsHandlerTest.createResponseBean(transaction, methodParam);
	}
	
	@Test
	public void testPersistResponse() throws SalesBusinessException {
		Map<String,Object> methodParam = new HashMap<String,Object>();
		methodParam.put(MyPriceConstants.NX_TRANSACTION_ID, 1234L);
		methodParam.put(MyPriceConstants.MP_TRANSACTION_ID, "123456");
		Map<String,Set<String>> solProductIdDataMap = new HashMap<String,Set<String>>();
		Set<String> sets = new HashSet<String>();
		solProductIdDataMap.put("2", sets);
		methodParam.put("solutionProdData", solProductIdDataMap);
		ConfigRespProcessingBean result = new ConfigRespProcessingBean();
		result.setMpTransactionId("123456");
		result.setNxTransactionId(1234L);
		result.setLineBomId("BOM_Solution");
		result.setModelName("AVPN");
		result.setModelVariableName("AVPN");
		Map<String,List<String>> productInfoMap = new HashMap<String,List<String>>();
		productInfoMap.put("AVPN", new ArrayList<String>() {{ add("AVPN");}});
		methodParam.put(MyPriceConstants.INR_MP_PRODUCT_INFO_DATA_MAP, productInfoMap);
		Mockito.when(configAndUpdatePricingUtilFmo.getProductName(productInfoMap, "AVPN")).thenReturn("AVPN");
		Mockito.when(wsHandlerTest.getResponseProductName("AVPN", "AVPN", methodParam)).thenReturn("AVPN");
		wsHandlerTest.persistResponse(result, methodParam);
		
		result.setLineBomId("BOM_AVPN");
		result.setParentLineItem("Solution");
		result.setParentDocNumber("2");
		result.setDocumentNumber("3");
		doNothing().when(wsHandlerTest).setResponseProductNo(methodParam, "AVPN","3");
		Mockito.when(nxMpRepositoryService.checkProductForUpdate("2",1234L)).thenReturn(true);
		Mockito.when(nxMpRepositoryService.updateSolAndProductResponse("3", new Date(), "2", 1234L)).thenReturn(0);
		methodParam.put("AVPN", "productname");
		doNothing().when(wsHandlerTest).setResponseProductNo(methodParam, "AVPN", "2");
		wsHandlerTest.persistResponse(result, methodParam);
	}
	
	@Test
	public void testGetThreadSize() {
		wsHandlerTest.getThreadSize();
	}
	
	@Test
	public void testSetResponseProductNo() {
		wsHandlerTest.setResponseProductNo(new HashMap<String, Object>(), "AVPN", "2");
	}

}
