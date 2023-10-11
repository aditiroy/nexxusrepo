package com.att.sales.nexxus.handlers;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
import com.att.sales.nexxus.dao.model.NxMpDesignDocument;
import com.att.sales.nexxus.dao.repository.NxDesignRepository;
import com.att.sales.nexxus.dao.repository.NxMpDesignDocumentRepository;
import com.att.sales.nexxus.dao.repository.NxMpPriceDetailsRepository;
import com.att.sales.nexxus.helper.MpProductEntity;
import com.att.sales.nexxus.myprice.transaction.dao.service.NxMpRepositoryService;
import com.att.sales.nexxus.myprice.transaction.model.ConfigRespProcessingBean;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdatePricingUtil;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdatePricingUtilFmo;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdateProcessingInrService;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.att.sales.nexxus.ws.utility.ConfigureSolnAndProductWSClientUtility;
import com.att.sales.nexxus.ws.utility.WSProcessingService;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.CommonStatusType;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.Configure;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.ConfigureResponse;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.ObjectFactory;

@ExtendWith(MockitoExtension.class)
public class ConfigureDesignWSHandlerFmoTest {

	@InjectMocks
	@Spy
	ConfigureDesignWSHandlerFmo wsHandlerTest;

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
	private ConfigAndUpdateProcessingInrService configAndUpdateProcessingInrService;
	
	@Mock
	private ConfigureSolnAndProductWSClientUtility configureSolnAndProductWSClientUtility;
	
	@Mock
	private NxMpDesignDocumentRepository nxMpDesignDocumentRepo;
	
	@Mock
	Configure configureRequest;
	
	@Mock
	ConfigAndUpdatePricingUtilFmo configAndUpdatePricingUtilFmo;
	
	@Mock
	NxMpPriceDetailsRepository nxMpPriceDetailsRepository;
	
	@Mock
	ConfigureSolnAndProductWSHandler configureSolnAndProductWSHandler;

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
		doNothing().when(wsHandlerTest).prepareRequestBody(map, configureRequest, json);
		wsHandlerTest.initiateConfigDesignWebService(map, json);
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
		wsHandlerTest.initiateConfigDesignWebService(map, json);
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
		Element partNumber = document.createElement("bmt:_line_bom_part_number");
		Element usoc = document.createElement("bmt:lii_uSOC_ql");
		Element uniqueId = document.createElement("bmt:wi_uniqueID_ql");
		Element siteId = document.createElement("bmt:lii_nxSiteId_ql");
		Element intAdeSite = document.createElement("bmt:wl_int_ade_site_reln");
		
		transaction.appendChild(lineBomId1);
		transaction.appendChild(lineBomParentId1);
		transaction.appendChild(documentNumber1);
		transaction.appendChild(parentLineItem);
		transaction.appendChild(parentDocNumber1);
		transaction.appendChild(partNumber);
		transaction.appendChild(usoc);
		transaction.appendChild(uniqueId);
		transaction.appendChild(siteId);
		transaction.appendChild(intAdeSite);
		transaction.setAttribute("bmt:bs_id", "123456");
		Mockito.when(configAndUpdatePricingUtil.getSourceName(methodParam)).thenReturn("INR");
		wsHandlerTest.createResponseBean(transaction, methodParam);
	}
	
	@Test
	public void testPersistResponse() throws SalesBusinessException {
		Map<String,Object> methodParam = new HashMap<String,Object>();
		methodParam.put(MyPriceConstants.NX_TRANSACTION_ID, 1234L);
		methodParam.put(MyPriceConstants.MP_TRANSACTION_ID, "123456");
		methodParam.put(MyPriceConstants.PRODUCT_NAME, "AVPN");
		ConfigRespProcessingBean result = new ConfigRespProcessingBean();
		result.setMpTransactionId("123456");
		result.setNxTransactionId(1234L);
		result.setParentLineItem("BOM_AVPN");
		result.setLineBomPartNumber("BOM");
		result.setModelName("AVPN");
		result.setModelVariableName("AVPN");
		result.setParentDocNumber("2");
		Mockito.when(configAndUpdatePricingUtilFmo.isProductLineIdMatchForConfigDesign(anyMap(),
				anyString(), anyString())).thenReturn(true);
		doNothing().when(wsHandlerTest).processDesignBlock(result, methodParam);
		wsHandlerTest.persistResponse(result, methodParam);
		
		methodParam.put(MyPriceConstants.TRANSACTION_FLOW_TYPE, "designUpdate");
		doNothing().when(configureSolnAndProductWSHandler).processSolutionAndProductResponse(anyMap(), anyString(), anyString(), anyString());
		wsHandlerTest.persistResponse(result, methodParam);
	}
	
	@Test
	public void testGetThreadSize() {
		wsHandlerTest.getThreadSize();
	}
	
	@Test
	public void testSetSolProductData() {
		wsHandlerTest.setSolProductData(anyMap(), anyString());
	}
	
	@Test
	public void testProcessConfigResponse() throws SalesBusinessException {
		Map<String,Object> methodParam = new HashMap<String,Object>();
		methodParam.put(MyPriceConstants.MP_PRODUCT_LINE_ID, "3");
		doNothing().when(wsHandlerTest).persistResponse(any(),anyMap());
		wsHandlerTest.processConfigResponse(getConfigureResponse(), methodParam);
	}
	
	@Test
	public void testProcessDesignBlock() throws SalesBusinessException {
		ConfigRespProcessingBean input = new ConfigRespProcessingBean();
		Map<String,Object> methodParam = new HashMap<String,Object>();
		methodParam.put(MyPriceConstants.NX_DESIGN_ID, 1010L);
		methodParam.put(MyPriceConstants.MP_SOLUTION_ID, "1920");
		methodParam.put("1920", true);
		input.setDocumentNumber("2");
		wsHandlerTest.processDesignBlock(input, methodParam);
		
		methodParam.put("1920", true);
		input.setDocumentNumber("2");
		NxMpDesignDocument  design = new NxMpDesignDocument();
		List<NxMpDesignDocument> designDocuments = new ArrayList<NxMpDesignDocument>();
		designDocuments.add(design);
		Mockito.when(nxMpDesignDocumentRepo.checkDesignForUpdate(anyString(),
				anyString(), anyLong())).thenReturn(designDocuments);
		Mockito.when(nxMpDesignDocumentRepo.updateDesignBySolIdAndProductId(1010L, "USOC", new Date(), "1920",
				"2", 10010L)).thenReturn(1);
		wsHandlerTest.processDesignBlock(input, methodParam);
	}
	
	@Test
	public void testGetMpProductLineId() {
		Map<String, Object> requestMap = new HashMap<String, Object>();
		HashSet<MpProductEntity> set = new HashSet<MpProductEntity>();
		set.add(new MpProductEntity());
		requestMap.put("AVPN", set);
		wsHandlerTest.getMpProductLineId(requestMap, "AVPN");
	}
	
	@Test
	public void testDeleteExistingRecordsByTxnIdAndDesignId() {
		Map<String,Object> methodParam = new HashMap<String,Object>();
		methodParam.put(MyPriceConstants.NX_DESIGN_ID, 10101L);
		methodParam.put(MyPriceConstants.NX_TRANSACTION_ID, 101L);
		Mockito.when(nxMpPriceDetailsRepository.deleteByNxTxnIdAndNxDesignId(anyLong(), anyLong())).thenReturn(1L);
		Mockito.when(nxMpDesignDocumentRepo.deleteByNxTxnIdAndNxDesignId(anyLong(), anyLong())).thenReturn(1L);
		wsHandlerTest.deleteExistingRecordsByTxnIdAndDesignId(methodParam);
	}
}
