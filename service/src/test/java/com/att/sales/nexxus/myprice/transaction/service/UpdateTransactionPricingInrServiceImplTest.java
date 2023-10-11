package com.att.sales.nexxus.myprice.transaction.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyString;

import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxMpConfigMapping;
import com.att.sales.nexxus.dao.model.NxMpDesignDocument;
import com.att.sales.nexxus.dao.model.NxMpPriceDetails;
import com.att.sales.nexxus.dao.repository.NxMpConfigMappingRepository;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxMpDesignDocumentRepository;
import com.att.sales.nexxus.dao.repository.NxMpPriceDetailsRepository;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTransactionPricingResponse;
import com.att.sales.nexxus.reteriveicb.model.ComponentDetails;
import com.att.sales.nexxus.reteriveicb.model.PriceAttributes;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.att.sales.nexxus.util.RestClientUtil;

@ExtendWith(MockitoExtension.class)

public class UpdateTransactionPricingInrServiceImplTest {

	@InjectMocks
	@Spy
	private UpdateTransactionPricingInrServiceImpl updateTransactionPricingInrServiceImpl;

	@Mock
	private NexxusJsonUtility nexxusJsonUtility;

	@Mock
	private NxMpConfigMappingRepository nxMpConfigMappingRepository;

	@Mock
	private UpdateTransactionPricingServiceImpl updateTransactionPricingServiceImpl;

	@Mock
	private NxMpDealRepository nxMpDealRepository;

	@Mock
	private RestClientUtil restClient;

	@Mock
	private NxMpDesignDocumentRepository nxMpDesignDocumentRepository;

	@Mock
	private NxMpPriceDetailsRepository nxMpPriceDetailsRepository;

	@Mock
	private Environment env;

	@Mock
	private MyPriceTransactionUtil myPriceTransactionUtil;
	
	@Mock
	private Map<String, String> headers;
	
	@Mock
	private Proxy proxy;
	
	@Mock
	private UpdateTransactionPricingServiceImpl updateTransactionPricingService;

	private JSONObject inputJson = JacksonUtil.toJsonObject("{\"CktId\":\"  BFEC546043   ATI \",\"nxSiteId\":10000,\"Quality\":\"Standard\",\"USOCInfo\":[{\"USOC\":\"1LNVX\",\"CLLIAEnd\":\"NWORLAMA\",\"CLLIZEnd\":\"DLLSTXTL\",\"AccessSpeed\":\"10 GBPS BASIC EPL-WAN\",\"DisplaySpeed\":\"10 Gig\",\"Quantity\":\"1\",\"NetRate\":\"8913.40\"}]}");
	
	@BeforeAll 
	public static void init() {
		Map<String, Object> map = new HashMap<>();
		map.put(ServiceMetaData.REST_REQUEST_START_TIME, System.currentTimeMillis());
		ServiceMetaData.add(map);
	}

	@AfterAll 
	public static void afterClass() {
		ServiceMetaData.getThreadLocal().remove();
	}

	@Test
	public void updateTransactionPricingRequestTest() throws SalesBusinessException  {
		Map<String, Object> apiResponse = new HashMap<String, Object>();
		
		Map<String, Object> designMap = new HashMap<>();
		designMap.put(MyPriceConstants.MP_TRANSACTION_ID, "12345");
		designMap.put(MyPriceConstants.NX_TRANSACTION_ID, 1L);
		designMap.put(MyPriceConstants.NX_DESIGN_ID, 1L);
		String uri = "https://custompricingdev2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/actions/updateRequestedRates";
		Mockito.when(env.getProperty("myprice.updateTransactionPricingRequest")).thenReturn(uri);
		apiResponse.put(MyPriceConstants.RESPONSE_STATUS, true);
		apiResponse.put(MyPriceConstants.RESPONSE_CODE, 200);
		apiResponse.put(MyPriceConstants.RESPONSE_DATA, new Object().toString());
		Mockito.when(updateTransactionPricingService.callUpdateTransactionPricingRequestApi(anyString(), any())).thenReturn(apiResponse);
		Mockito.when(restClient.processResult(anyString(), any())).thenReturn(new UpdateTransactionPricingResponse());
		NxMpDesignDocument design = new NxMpDesignDocument();
		design.setMpDocumentNumber(2l);
		Mockito.when(nxMpDesignDocumentRepository.findByTxnDesignUsocIds(anyLong(), anyLong(), anyString())).thenReturn(design);
		Map<String,NxMpConfigMapping> rulesMap = new HashMap<String,NxMpConfigMapping>();
		Mockito.when(updateTransactionPricingInrServiceImpl.getData(anyString(), anyMap(), any())).thenReturn("12345");
		updateTransactionPricingInrServiceImpl.updateTransactionPricingRequest(designMap, inputJson);
	}
	
	@Test
	public void updateTransactionPricingRequestTestExc() throws SalesBusinessException  {
		Map<String, Object> designMap = new HashMap<>();
		designMap.put(MyPriceConstants.MP_TRANSACTION_ID, "12345");
		designMap.put(MyPriceConstants.NX_TRANSACTION_ID, 1L);
		designMap.put(MyPriceConstants.NX_DESIGN_ID, 1L);
		String uri = "https://custompricingdev2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/actions/updateRequestedRates";
		Mockito.when(env.getProperty("myprice.updateTransactionPricingRequest")).thenReturn(uri);
		Mockito.when(updateTransactionPricingService.callUpdateTransactionPricingRequestApi(anyString(), any())).thenThrow(Exception.class);
		updateTransactionPricingInrServiceImpl.updateTransactionPricingRequest(designMap, inputJson);
	}


	@Test
	public void getData() {
		Map<String, NxMpConfigMapping> rulesMap = new HashMap<>();
		NxMpConfigMapping nxMpConfigMapping = new NxMpConfigMapping();
		rulesMap.put("INR", nxMpConfigMapping);
		updateTransactionPricingInrServiceImpl.getData("INR", rulesMap, inputJson);
	}

	@Test
	public void getItemValueUsingJsonPath() {
		String jsonPath = "";
		JSONObject inputDesignDetails = new JSONObject();
		Object result = new Object();
		Mockito.when(nexxusJsonUtility.getValue(inputDesignDetails, jsonPath)).thenReturn(result);
		updateTransactionPricingInrServiceImpl.getItemValueUsingJsonPath(jsonPath, inputDesignDetails);
	}

	@Test
	public void savePriceDetialsTest() {
		Long nxTxnId = 1L;
		NxMpDesignDocument nxMpDesignDocument = new NxMpDesignDocument();
		nxMpDesignDocument.setMpDocumentNumber(1L);
		PriceAttributes priceAttribute = new PriceAttributes();
		priceAttribute.setFrequency("NRC");
		// ComponentDetails componentDetails = new ComponentDetails();
		Map<String, Object> paramMap = new HashMap<>();
		// String flowType = "ASE";
		paramMap.put(MyPriceConstants.USOC_ID, "123AB");
		paramMap.put(MyPriceConstants.NET_RATE, 123.05F);
		NxMpPriceDetails nxMpPriceDetails = new NxMpPriceDetails();
		nxMpPriceDetails.setRequestedNRCEffectivePrice(priceAttribute.getRequestedRate());
		Mockito.when(nxMpPriceDetailsRepository.findByMpDocumentNumberAndNxTxnIdAndFrequency(
				nxMpDesignDocument.getMpDocumentNumber(), nxTxnId, MyPriceConstants.MRC)).thenReturn(nxMpPriceDetails);
		Mockito.when(nxMpPriceDetailsRepository.save(nxMpPriceDetails)).thenReturn(nxMpPriceDetails);
		updateTransactionPricingInrServiceImpl.savePriceDetails(paramMap, nxMpDesignDocument, nxTxnId);

	}

	@Test
	public void savePriceDetialsTestReconfigOrUpdate() {
		Long nxTxnId = 1L;
		NxMpDesignDocument nxMpDesignDocument = new NxMpDesignDocument();
		PriceAttributes priceAttribute = new PriceAttributes();
		priceAttribute.setFrequency("MRC");
		ComponentDetails componentDetails = new ComponentDetails();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(MyPriceConstants.USOC_ID, "123AB");
		paramMap.put(MyPriceConstants.NET_RATE, 123.05F);
		// String flowType = "ADE";
		paramMap.put(StringConstants.TRANSACTION_UPDATE, StringConstants.RECONFIGURE);
		NxMpPriceDetails nxMpPriceDetails = new NxMpPriceDetails();
		Mockito.when(nxMpPriceDetailsRepository.findByMpDocumentNumberAndNxTxnIdAndFrequency(
				nxMpDesignDocument.getMpDocumentNumber(), nxTxnId, priceAttribute.getFrequency()))
				.thenReturn(nxMpPriceDetails);
		nxMpPriceDetails.setRequestedMRCDiscPercentage(priceAttribute.getRequestedMRCDiscPercentage());
		nxMpPriceDetails.setRequestedNRCDiscPercentage(priceAttribute.getRequestedNRCDiscPercentage());
		nxMpPriceDetails.setFrequency(priceAttribute.getFrequency());
		nxMpPriceDetails.setBeid(priceAttribute.getBeid());
		nxMpPriceDetails.setComponentType(componentDetails.getComponentType());
		nxMpPriceDetails.setComponentId(componentDetails.getComponentId());
		nxMpPriceDetails.setComponentParentId(componentDetails.getComponentParentId());
		nxMpPriceDetails.setRequestedMRCEffectivePrice(priceAttribute.getRequestedRate());
		Mockito.when(nxMpPriceDetailsRepository.save(nxMpPriceDetails)).thenReturn(nxMpPriceDetails);
		updateTransactionPricingInrServiceImpl.savePriceDetails(paramMap, nxMpDesignDocument, nxTxnId);
	}

}
