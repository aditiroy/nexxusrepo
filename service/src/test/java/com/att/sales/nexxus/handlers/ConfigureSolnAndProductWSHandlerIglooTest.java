package com.att.sales.nexxus.handlers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
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
import com.att.sales.nexxus.dao.repository.NxDesignRepository;
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
public class ConfigureSolnAndProductWSHandlerIglooTest {
	
	@InjectMocks
	@Spy
	ConfigureSolnAndProductWSHandlerIgloo wsHandlerTest;

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
		Mockito.when(nxMpRepositoryService.getDataByNxtxnIdInr(21L)).thenReturn(new HashMap<String,Set<String>>());
		doNothing().when(wsHandlerTest).processConfigSolAndProductResponse(getConfigureResponse(), map);
		wsHandlerTest.initiateConfigSolnAndProdWebService(json, map);
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
		wsHandlerTest.initiateConfigSolnAndProdWebService(json, map);
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
		result.setModelName("LocalAccess");
		result.setModelVariableName("LocalAccess");
		Map<String,List<String>> productInfoMap = new HashMap<String,List<String>>();
		productInfoMap.put("LocalAccess", new ArrayList<String>() {{ add("LocalAccess");}});
		methodParam.put(MyPriceConstants.INR_MP_PRODUCT_INFO_DATA_MAP, productInfoMap);
		Mockito.when(configAndUpdateProcessingInrService.getProductName(productInfoMap, "LocalAccess")).thenReturn("LocalAccess");
		Mockito.when(wsHandlerTest.getResponseProductName("AVPN", "LocalAccess", methodParam)).thenReturn("LocalAccess");
		wsHandlerTest.persistResponse(result, methodParam);
		
		result.setLineBomId("BOM_AVPN");
		result.setParentLineItem("Solution");
		result.setParentDocNumber("2");
		result.setDocumentNumber("3");
		doNothing().when(wsHandlerTest).setResponseProductNo(methodParam, "LocalAccess","3");
		Mockito.when(nxMpRepositoryService.checkProductForUpdate("2",1234L)).thenReturn(true);
		Mockito.when(nxMpRepositoryService.updateSolAndProductResponse("3", new Date(), "2", 1234L)).thenReturn(0);
		methodParam.put("LocalAccess", "productname");
		doNothing().when(wsHandlerTest).setResponseProductNo(methodParam, "LocalAccess", "2");
		wsHandlerTest.persistResponse(result, methodParam);
	}
	
	@Test
	public void testGetThreadSize() {
		wsHandlerTest.getThreadSize();
	}
	
	@Test
	public void testGetConfigMapping() {
		Map<String, Object> requestMap = new HashMap<String, Object>();
		requestMap.put(MyPriceConstants.PRODUCT_NAME, "LocalAccess");
		requestMap.put(MyPriceConstants.IS_GROUP_REQUEST, true);
		Mockito.when(nxMpRepositoryService.findByMultipleOffersAndRuleName(any(), any())).thenReturn(any());
		wsHandlerTest.getConfigMapping(requestMap);
		
		requestMap.put(MyPriceConstants.IS_GROUP_REQUEST, false);
		Mockito.when(nxMpRepositoryService.findByOfferAndRuleName(any(), any())).thenReturn(any());
		wsHandlerTest.getConfigMapping(requestMap);
	}

	@Test
	public void testSetResponseProductNo() {
		wsHandlerTest.setResponseProductNo(new HashMap<String, Object>(), "LocalAccess", "2");
	}

}
