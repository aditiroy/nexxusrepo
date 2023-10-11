package com.att.sales.nexxus.handlers;
import static org.junit.Assert.assertTrue;
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

import org.json.simple.JSONObject;
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
import com.att.sales.nexxus.helper.MpProductEntity;
import com.att.sales.nexxus.myprice.transaction.dao.service.NxMpRepositoryService;
import com.att.sales.nexxus.myprice.transaction.model.ConfigRespProcessingBean;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdatePricingUtil;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdateProcessingInrService;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.att.sales.nexxus.ws.utility.ConfigureSolnAndProductWSClientUtility;
import com.att.sales.nexxus.ws.utility.WSProcessingService;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.CommonStatusType;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.Configure;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.ConfigureResponse;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.ObjectFactory;

@ExtendWith(MockitoExtension.class)
public class ConfigureDesignWSHandlerIglooTest {

	@InjectMocks
	@Spy
	ConfigureDesignWSHandlerIgloo wsHandlerTest;

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

	@BeforeEach
	public void initializeServiceMetaData() {
		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(ServiceMetaData.OFFER, "LocalAccess");
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
		map.put("offerName", "LocalAccess");
		map.put(MyPriceConstants.SOURCE, "IGLOO");
		JSONObject json = JacksonUtil.toJsonObject("{\"custAddr1\":\"246 Bromham Road\",\"city\":\"Biddenham\",\"custPostalcode\":\"MK40 4AA\",\"geoAddr\":\"246 Bromham Road, Biddenham, Bedford, Bedfordshire, MK40 4AA\",\"country\":\"UNITED KINGDOM\",\"alternateCurrency\":\"USD\",\"supplierName\":\"SAREA\",\"clli\":\"LONDENEH\",\"monthlyCostLocal\":\"0.0\",\"oneTimeCostLocal\":\"0.0\",\"currency\":\"GBP\",\"accessBandwidth\":\"10000\",\"technology\":\"Ethernet (Gateway Interconnect/ESP ETH Shared)\",\"serialNumber\":\"1026665_GB_AVPN-ETH_2031551_3654493\",\"nodeName\":\"London 2\",\"speed\":\"10000\",\"service\":\"LocalAccess-ETH\",\"mrc\":\"110.0\",\"nrc\":\"10.0\",\"monthlyPriceUSD\":\"110.0\",\"oneTimePriceUSD\":\"10.0\",\"flowType\":\"IGL\"}");
		doNothing().when(configAndUpdatePricingUtil).setProductTypeForInrFmo(map);
		doNothing().when(configureSolnAndProductWSClientUtility).setWsName(MyPriceConstants.CONFIG_SOL_PRODUCT_WS);
		Mockito.when(wsProcessingService.initiateWebService(any(Configure.class),
				any(ConfigureSolnAndProductWSClientUtility.class), anyMap(), any(Class.class)))
				.thenReturn(getConfigureResponse());
		doNothing().when(wsHandlerTest).prepareRequestBody(map, configureRequest, json);
		wsHandlerTest.initiateConfigDesignWebService(json, map);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWithExceptionScenario() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("myPriceTransId", "42373160");
		map.put("nxTxnId", 21L);
		map.put("offerName", "LocalAccess");
		map.put(MyPriceConstants.SOURCE, "IGLOO");
		JSONObject json = JacksonUtil.toJsonObject("{\"custAddr1\":\"246 Bromham Road\",\"city\":\"Biddenham\",\"custPostalcode\":\"MK40 4AA\",\"geoAddr\":\"246 Bromham Road, Biddenham, Bedford, Bedfordshire, MK40 4AA\",\"country\":\"UNITED KINGDOM\",\"alternateCurrency\":\"USD\",\"supplierName\":\"SAREA\",\"clli\":\"LONDENEH\",\"monthlyCostLocal\":\"0.0\",\"oneTimeCostLocal\":\"0.0\",\"currency\":\"GBP\",\"accessBandwidth\":\"10000\",\"technology\":\"Ethernet (Gateway Interconnect/ESP ETH Shared)\",\"serialNumber\":\"1026665_GB_AVPN-ETH_2031551_3654493\",\"nodeName\":\"London 2\",\"speed\":\"10000\",\"service\":\"LocalAccess-ETH\",\"mrc\":\"110.0\",\"nrc\":\"10.0\",\"monthlyPriceUSD\":\"110.0\",\"oneTimePriceUSD\":\"10.0\",\"flowType\":\"IGL\"}");
		Mockito.when(wsProcessingService.initiateWebService(any(Configure.class),
				any(ConfigureSolnAndProductWSClientUtility.class), anyMap(), any(Class.class)))
				.thenThrow(SoapFaultClientException.class);
		wsHandlerTest.initiateConfigDesignWebService(json, map);
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
		
		transaction.appendChild(lineBomId1);
		transaction.appendChild(lineBomParentId1);
		transaction.appendChild(documentNumber1);
		transaction.appendChild(parentLineItem);
		transaction.appendChild(parentDocNumber1);
		transaction.appendChild(partNumber);
		transaction.appendChild(usoc);
		transaction.appendChild(uniqueId);
		transaction.setAttribute("bmt:bs_id", "123456");
		Mockito.when(configAndUpdatePricingUtil.getSourceName(methodParam)).thenReturn("IGLOO");
		wsHandlerTest.createResponseBean(transaction, methodParam);
	}
	
	@Test
	public void testPersistResponse() throws SalesBusinessException {
		Map<String,Object> methodParam = new HashMap<String,Object>();
		methodParam.put(MyPriceConstants.NX_TRANSACTION_ID, 1234L);
		methodParam.put(MyPriceConstants.MP_TRANSACTION_ID, "123456");
		methodParam.put(MyPriceConstants.PRODUCT_NAME, "LocalAccess");
		ConfigRespProcessingBean result = new ConfigRespProcessingBean();
		result.setMpTransactionId("123456");
		result.setNxTransactionId(1234L);
		result.setParentLineItem("BOM_AVPN");
		result.setLineBomPartNumber("BOM");
		result.setModelName("LocalAccess");
		result.setModelVariableName("LocalAccess");
		result.setParentDocNumber("2");
		Mockito.when(wsHandlerTest.isProductLineIdMatchForConfigDesign(methodParam,	result.getParentDocNumber(), "LocalAccess")).thenReturn(true);
		doNothing().when(wsHandlerTest).processDesignBlock(result, methodParam);
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
		methodParam.put(MyPriceConstants.NX_ACCESS_PRICE_ID, 1010L);
		methodParam.put(MyPriceConstants.MP_SOLUTION_ID, "1920");
		methodParam.put(MyPriceConstants.NX_TRANSACTION_ID, 19201L);
		methodParam.put("1920", true);
		input.setDocumentNumber("2");
		wsHandlerTest.processDesignBlock(input, methodParam);
		
		methodParam.put("1920", false);
		input.setDocumentNumber("2");
		NxMpDesignDocument  design = new NxMpDesignDocument();
		List<NxMpDesignDocument> designDocuments = new ArrayList<NxMpDesignDocument>();
		designDocuments.add(design);
		Mockito.when(nxMpDesignDocumentRepo.findByNxTxnIdAndNxDesignId(anyLong(), anyLong())).thenReturn(designDocuments);
		Mockito.when(nxMpDesignDocumentRepo.updateDesignBySolIdAndProductId(1010L, "USOC", new Date(), "1920",
				"2", 10010L)).thenReturn(1);
		wsHandlerTest.processDesignBlock(input, methodParam);
	}
	
	@Test
	public void testGetMpProductLineId() {
		Map<String, Object> requestMap = new HashMap<String, Object>();
		HashSet<MpProductEntity> set = new HashSet<MpProductEntity>();
		set.add(new MpProductEntity());
		requestMap.put("LocalAccess", set);
		wsHandlerTest.getMpProductLineId(requestMap, "LocalAccess");
	}
	
	@Test
	public void testIsProductLineIdMatchForConfigDesign() {
		Map<String,Object> methodParam = new HashMap<String,Object>();
		methodParam.put(MyPriceConstants.MP_PRODUCT_LINE_ID, "1");
		assertTrue(wsHandlerTest.isProductLineIdMatchForConfigDesign(methodParam, "1", MyPriceConstants.ETHERNET));
		assertTrue(wsHandlerTest.isProductLineIdMatchForConfigDesign(methodParam, "1", MyPriceConstants.SOURCE_INTERNATIONAL_ACCESS));
	}
	
	@Test
	public void testGetProductNameForConfigRequest() {
		wsHandlerTest.getProductNameForConfigRequest("test");
	}
	
}
