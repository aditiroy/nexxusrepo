package com.att.sales.nexxus.myprice.transaction.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxMpDesignDocument;
import com.att.sales.nexxus.dao.model.NxMpPriceDetails;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxMpDesignDocumentRepository;
import com.att.sales.nexxus.dao.repository.NxMpPriceDetailsRepository;
import com.att.sales.nexxus.handlers.ConfigureDesignWSHandler;
import com.att.sales.nexxus.myprice.transaction.model.GetTransactionLineItemPrice;
import com.att.sales.nexxus.myprice.transaction.model.TransactionPricingReqDocuments;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTransactionLineItem;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTransactionLinePricing;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTransactionPricingRequest;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTransactionPricingResponse;
import com.att.sales.nexxus.reteriveicb.model.Circuit;
import com.att.sales.nexxus.reteriveicb.model.ComponentDetails;
import com.att.sales.nexxus.reteriveicb.model.PriceAttributes;
import com.att.sales.nexxus.reteriveicb.model.PriceDetails;
import com.att.sales.nexxus.reteriveicb.model.Site;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.RestClientUtil;

@ExtendWith(MockitoExtension.class)

public class UpdateTransactionPricingServiceImplTest {

	@InjectMocks
	private UpdateTransactionPricingServiceImpl updateTransactionPricingServiceImpl;
	
	@Mock
	private NxMpDealRepository nxMpDealRepository;
	
	@Mock
	private RestClientUtil restClient;
	
	@Mock
	private Environment env;
	
	@Mock
	private MyPriceTransactionUtil myPriceTransactionUtil;
	
	@Mock
	private NxMpPriceDetailsRepository nxMpPriceDetailsRepository;
	
	@Mock
	private ConfigureDesignWSHandler configureDesignWSHandler;
	
	@Mock
	private NxMpDesignDocumentRepository nxMpDesignDocumentRepository;
	
	@Mock
	private HttpRestClient httpRest;
	
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
	public void updateTransactionPricingRequestAseTest() throws SalesBusinessException, JSONException {
		Map<String, Object> designMap = new HashMap<>();
		//designMap.put(MyPriceConstants.CONTRACT_TERM, "1");
		designMap.put(MyPriceConstants.MP_TRANSACTION_ID, "12345");
		designMap.put(MyPriceConstants.NX_DESIGN_ID, 1L);
		designMap.put(MyPriceConstants.NX_TRANSACTION_ID, 1L);
		designMap.put(StringConstants.PRICE_SCENARIO_ID, 1L);
		designMap.put(MyPriceConstants.OFFER_TYPE, "ASE");
		designMap.put(MyPriceConstants.PRICE_UPDATE, "Y");
		
		String myPriceTxnId = designMap.containsKey(MyPriceConstants.MP_TRANSACTION_ID) ? designMap.get(MyPriceConstants.MP_TRANSACTION_ID).toString() : null;
		Long nxDesignId = designMap.containsKey(MyPriceConstants.NX_DESIGN_ID) ? (Long)designMap.get(MyPriceConstants.NX_DESIGN_ID) : 0L;
		Long nxTxnId = designMap.containsKey(MyPriceConstants.NX_TRANSACTION_ID) ? (Long)designMap.get(MyPriceConstants.NX_TRANSACTION_ID) : 0L;
		Long priceScenarioId = designMap.containsKey(StringConstants.PRICE_SCENARIO_ID)?(Long) designMap.get(StringConstants.PRICE_SCENARIO_ID) : 0L;
		String flowType = designMap.containsKey(MyPriceConstants.OFFER_TYPE) ? designMap.get(MyPriceConstants.OFFER_TYPE).toString() : null;		
		String priceUpdate = designMap.containsKey(MyPriceConstants.PRICE_UPDATE) ? designMap.get(MyPriceConstants.PRICE_UPDATE).toString() : null;		
		
		Long contractTerm = 0L;
		UpdateTransactionPricingResponse response = null;
		Map<String, Object> apiResponse = new HashMap<String, Object>();
		UpdateTransactionPricingRequest request = new UpdateTransactionPricingRequest();
		//Mockito.when(updateTransactionPricingServiceImpl.getRequest(myPriceTxnId, nxDesignId, nxTxnId, priceScenarioId, designMap,flowType,contractTerm)).thenReturn(request);
		request = updateTransactionPricingServiceImpl.getRequest(myPriceTxnId, nxDesignId, nxTxnId, priceScenarioId, designMap,flowType,contractTerm);
		String uri = "https://custompricingdev2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/actions/updateRequestedRates";
		Mockito.when(env.getProperty("myprice.updateTransactionPricingRequest")).thenReturn(uri);
		String res = new Object().toString();
		Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString()))
		.thenReturn(res);
		apiResponse = updateTransactionPricingServiceImpl.callUpdateTransactionPricingRequestApi(myPriceTxnId,request);
		String transResponse = (apiResponse.containsKey(MyPriceConstants.RESPONSE_DATA) && null != apiResponse.get(MyPriceConstants.RESPONSE_DATA)) ?(String) apiResponse.get(MyPriceConstants.RESPONSE_DATA) : null;
		Mockito.when(restClient.processResult(transResponse,UpdateTransactionPricingResponse.class)).thenReturn(response);
		designMap.put(MyPriceConstants.RESPONSE_MSG, (apiResponse.containsKey(MyPriceConstants.RESPONSE_MSG) && null != apiResponse.get(MyPriceConstants.RESPONSE_MSG)) ?(String) apiResponse.get(MyPriceConstants.RESPONSE_MSG) : null);
		designMap.put(MyPriceConstants.RESPONSE_DATA, transResponse);
		designMap.put(MyPriceConstants.RESPONSE_MSG, "Design / Prices are not matching : Update Prcing api is not invoked for transaction id : "+myPriceTxnId);
		designMap.put(MyPriceConstants.RESPONSE_STATUS, false);			
		updateTransactionPricingServiceImpl.updateTransactionPricingRequest(designMap);
	}

	@Test
	public void updateTransactionPricingRequestAdeTest() throws SalesBusinessException, JSONException {
		Map<String, Object> designMap = new HashMap<>();
		designMap.put(MyPriceConstants.CONTRACT_TERM, "1");
		designMap.put(MyPriceConstants.MP_TRANSACTION_ID, "12345");
		designMap.put(MyPriceConstants.NX_DESIGN_ID, 1L);
		designMap.put(MyPriceConstants.NX_TRANSACTION_ID, 1L);
		designMap.put(StringConstants.PRICE_SCENARIO_ID, 1L);
		designMap.put(MyPriceConstants.OFFER_TYPE, "ADE");
		designMap.put(MyPriceConstants.PRICE_UPDATE, "N");
		
		String myPriceTxnId = designMap.containsKey(MyPriceConstants.MP_TRANSACTION_ID) ? designMap.get(MyPriceConstants.MP_TRANSACTION_ID).toString() : null;
		Long nxDesignId = designMap.containsKey(MyPriceConstants.NX_DESIGN_ID) ? (Long)designMap.get(MyPriceConstants.NX_DESIGN_ID) : 0L;
		Long nxTxnId = designMap.containsKey(MyPriceConstants.NX_TRANSACTION_ID) ? (Long)designMap.get(MyPriceConstants.NX_TRANSACTION_ID) : 0L;
		Long priceScenarioId = designMap.containsKey(StringConstants.PRICE_SCENARIO_ID)?(Long) designMap.get(StringConstants.PRICE_SCENARIO_ID) : 0L;
		String flowType = designMap.containsKey(MyPriceConstants.OFFER_TYPE) ? designMap.get(MyPriceConstants.OFFER_TYPE).toString() : null;		
		//String priceUpdate = designMap.containsKey(MyPriceConstants.PRICE_UPDATE) ? designMap.get(MyPriceConstants.PRICE_UPDATE).toString() : null;		

		Long contractTerm = 0L;
		UpdateTransactionPricingResponse response = new UpdateTransactionPricingResponse();
		Map<String, Object> apiResponse = new HashMap<String, Object>();
		UpdateTransactionPricingRequest request = updateTransactionPricingServiceImpl.getRequest(myPriceTxnId, nxDesignId, nxTxnId, priceScenarioId, designMap,flowType,contractTerm);
		String uri = "https://custompricingdev2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/actions/updateRequestedRates";
		Mockito.when(env.getProperty("myprice.updateTransactionPricingRequest")).thenReturn(uri);
		String res = new Object().toString();
		Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString()))
		.thenReturn(res);
		apiResponse = updateTransactionPricingServiceImpl.callUpdateTransactionPricingRequestApi(myPriceTxnId,request);
		String transResponse = (apiResponse.containsKey(MyPriceConstants.RESPONSE_DATA) && null != apiResponse.get(MyPriceConstants.RESPONSE_DATA)) ?(String) apiResponse.get(MyPriceConstants.RESPONSE_DATA) : null;
		Mockito.when(restClient.processResult(transResponse,UpdateTransactionPricingResponse.class)).thenReturn(response);
		designMap.put(MyPriceConstants.RESPONSE_STATUS, true);
		designMap.put(MyPriceConstants.RESPONSE_MSG, (apiResponse.containsKey(MyPriceConstants.RESPONSE_MSG) && null != apiResponse.get(MyPriceConstants.RESPONSE_MSG)) ?(String) apiResponse.get(MyPriceConstants.RESPONSE_MSG) : null);
		designMap.put(MyPriceConstants.RESPONSE_DATA, transResponse);
		designMap.put(MyPriceConstants.RESPONSE_MSG, "Design / Prices are not matching : Update Prcing api is not invoked for transaction id : "+myPriceTxnId);
		designMap.put(MyPriceConstants.RESPONSE_STATUS, false);
		updateTransactionPricingServiceImpl.updateTransactionPricingRequest(designMap);
	}
	
	@Test
	public void getRequestTest() throws JSONException {
		List<UpdateTransactionLineItem> items = null;
/*		UpdateTransactionLineItem item = new UpdateTransactionLineItem();
		items.add(item);*/
		
		UpdateTransactionLinePricing transactionLine = new UpdateTransactionLinePricing();
		transactionLine.setItems(items);
		TransactionPricingReqDocuments documents = new TransactionPricingReqDocuments();
		documents.setTransactionLine(transactionLine);
		UpdateTransactionPricingRequest request = new UpdateTransactionPricingRequest();
		request.setDocuments(documents);
		
		String myPriceTxnId = "12345";
		Long nxDesignId = 1L; 
		Long nxTxnId = 1L;
		Long priceScenarioId = 1L;
		String flowType = "ASE";
		Long contractTerm = null;
		String designData = "{}";
		Map<String, Object> designMap = new HashMap<>();
		Mockito.when(nxMpDealRepository.findDesignDataByDesignId(nxDesignId)).thenReturn(designData);
		Site site = JacksonUtil.fromString(designData, Site.class);
		if (Optional.ofNullable(site).isPresent()) {
			PriceDetails priceDetails = site.getPriceDetails();
			items = updateTransactionPricingServiceImpl.processPriceDetails(priceDetails,priceScenarioId,nxDesignId, nxTxnId,designMap,flowType,0L, site.getThirdPartyInd());
		}
		//if (CollectionUtils.isNotEmpty(items)) {
			

		//}
		updateTransactionPricingServiceImpl.getRequest(myPriceTxnId, nxDesignId, nxTxnId, priceScenarioId,designMap, flowType, contractTerm);		
	}

	@Test
	public void getRequestTestAde() {
		List<UpdateTransactionLineItem> items = null;
		//UpdateTransactionLineItem item = new UpdateTransactionLineItem();
		//items.add(item);
		String myPriceTxnId = "12345";
		Long nxDesignId = 1L; 
		Long nxTxnId = 1L;
		Long priceScenarioId = 1L;
		String flowType = "ADE";
		Long contractTerm = null;
		String designData = "{}";
		Mockito.when(nxMpDealRepository.findDesignDataByDesignId(nxDesignId)).thenReturn(designData);
		Map<String, Object> designMap = new HashMap<>();
		try {
			JSONObject jsonObject = new JSONObject(designData);
			jsonObject.remove("site");
			String modifiedDesignData = jsonObject.toString();
			Circuit circuit = JacksonUtil.fromString(modifiedDesignData, Circuit.class);
			PriceDetails priceDetails = circuit.getPriceDetails();
			String thirdPartyInd = null;
			items = updateTransactionPricingServiceImpl.processPriceDetails(priceDetails, priceScenarioId, nxDesignId, nxTxnId, designMap, flowType, contractTerm, thirdPartyInd);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		//items = processPriceDetails(priceDetails,priceScenarioId,nxDesignId, nxTxnId,designMap,flowType,contractTerm, null);
		//if (CollectionUtils.isNotEmpty(items)) {
			UpdateTransactionLinePricing transactionLine = new UpdateTransactionLinePricing();
			transactionLine.setItems(items);
			TransactionPricingReqDocuments documents = new TransactionPricingReqDocuments();
			documents.setTransactionLine(transactionLine);
			UpdateTransactionPricingRequest request = new UpdateTransactionPricingRequest();
			request.setDocuments(documents);
		//}
		try {
			updateTransactionPricingServiceImpl.getRequest(myPriceTxnId, nxDesignId, nxTxnId, priceScenarioId,designMap, flowType, contractTerm);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	@Test
	public void processPriceDetailsTest() {
		List<UpdateTransactionLineItem> items = new ArrayList<UpdateTransactionLineItem>(); 
		UpdateTransactionLineItem updateTransactionLineItem = new UpdateTransactionLineItem();
		updateTransactionLineItem.setDocumentNumber("1");
		items.add(updateTransactionLineItem);
		PriceDetails priceDetails = new PriceDetails();
		List<PriceAttributes> priceAttributes = new ArrayList<>();
		PriceAttributes pricAttribute = new PriceAttributes();
		pricAttribute.setFrequency("MRC");
		pricAttribute.setRequestedRate(2F);
		pricAttribute.setLocalListPrice(22.20);
		pricAttribute.setPriceScenarioId(1L);
		pricAttribute.setLocalCurrency("US");
		priceAttributes.add(pricAttribute);
		
		PriceAttributes pricAttribute1 = new PriceAttributes();
		pricAttribute1.setFrequency("MRC");
		pricAttribute1.setRequestedDiscount(22F);
		pricAttribute1.setLocalListPrice(22.20);
		pricAttribute1.setPriceScenarioId(1L);
		pricAttribute1.setLocalCurrency("US");
		priceAttributes.add(pricAttribute1);
		
		PriceAttributes pricAttribute2 = new PriceAttributes();
		pricAttribute2.setFrequency("MRC");
		pricAttribute2.setLocalListPrice(22.20);
		pricAttribute2.setPriceScenarioId(1L);
		pricAttribute2.setLocalCurrency("US");
		priceAttributes.add(pricAttribute2);

		
		ComponentDetails componentDetail = new ComponentDetails();
		componentDetail.setPriceAttributes(priceAttributes);
		List<ComponentDetails> componentDetails = new ArrayList<>();
		componentDetails.add(componentDetail);
		priceDetails.setComponentDetails(componentDetails);
		Long priceScenarioId = 1L;
		Long nxDesignId = 1L;
		Long nxTxnId = 1L;
		Map<String, Object> designMap = new HashMap<>();
		String flowType = "ASE";
		Long contractTerm = null;
		String thirdPartyInd = "Y";
		Map<String,List<String>> asenod3PaUsocIdMap = new HashMap<String,List<String>>();
		asenod3PaUsocIdMap.put("123", new ArrayList<>());
		Mockito.when(configureDesignWSHandler.getAsenod3PaUsocIdMap()).thenReturn(asenod3PaUsocIdMap);
		String tempBeid = "123";
		Map<String,List<String>> asenod3PaUsocId = asenod3PaUsocIdMap;
		asenod3PaUsocIdMap.put("123", new ArrayList<>());
		priceDetails.getComponentDetails().stream().forEach(compontDetails->{
			compontDetails.getPriceAttributes().stream().forEach(prizeAttribute -> {
				Mockito.when(configureDesignWSHandler.getConvertedUsocIdFor3PA(asenod3PaUsocId, prizeAttribute.getBeid())).thenReturn(tempBeid);
				NxMpDesignDocument nxMpDesignDocument = new NxMpDesignDocument();
				nxMpDesignDocument.setMpDocumentNumber(1L);
				Optional<?> beid = Optional.ofNullable(tempBeid);
				Mockito.when(nxMpDesignDocumentRepository.findByTxnDesignUsocIds(nxTxnId, nxDesignId, beid.get().toString())).thenReturn(nxMpDesignDocument);
				UpdateTransactionLineItem transactionLineItem = new UpdateTransactionLineItem();
				GetTransactionLineItemPrice getTransactionLineItemPrice = new GetTransactionLineItemPrice();
				getTransactionLineItemPrice.setValue(prizeAttribute.getRequestedRate());
				for(UpdateTransactionLineItem item : items) {
					item.setRequestedEffectivePriceMRC(getTransactionLineItemPrice);
				}
				//Float value = (float) (prizeAttribute.getLocalListPrice() - ((prizeAttribute.getLocalListPrice() * prizeAttribute.getRequestedDiscount())/100));
				//getTransactionLineItemPrice.setValue(value);
				//getTransactionLineItemPrice.setValue(prizeAttribute.getLocalListPrice().floatValue());
				/*				for(UpdateTransactionLineItem item : items) {
					if(item.getDocumentNumber().equalsIgnoreCase(nxMpDesignDocument.getMpDocumentNumber().toString())) {
						if("MRC".equalsIgnoreCase(prizeAttribute.getFrequency())) {
							item.setRequestedEffectivePriceMRC(getTransactionLineItemPrice);
						} else {
							item.setRequestedEffectivePriceNRC(getTransactionLineItemPrice);
						}
					}
				}
				transactionLineItem = new UpdateTransactionLineItem();
				transactionLineItem.setDocumentNumber(nxMpDesignDocument.getMpDocumentNumber().toString());
				if("MRC".equalsIgnoreCase(prizeAttribute.getFrequency())) {
					transactionLineItem.setRequestedEffectivePriceMRC(getTransactionLineItemPrice);
				} else {
					transactionLineItem.setRequestedEffectivePriceNRC(getTransactionLineItemPrice);
				}
*/				items.add(transactionLineItem);
			});
		});
		updateTransactionPricingServiceImpl.processPriceDetails(priceDetails, priceScenarioId, nxDesignId, nxTxnId, designMap, flowType, contractTerm, 
				thirdPartyInd);
	}
	
	@Test
	public void processPriceDetailsAdeTest() {
		List<UpdateTransactionLineItem> items = new ArrayList<UpdateTransactionLineItem>();
		UpdateTransactionLineItem updateTransactionLineItem = new UpdateTransactionLineItem();
		updateTransactionLineItem.setDocumentNumber("1");
		items.add(updateTransactionLineItem);
		PriceDetails priceDetails = new PriceDetails();
		List<PriceAttributes> priceAttributes = new ArrayList<>();
		PriceAttributes pricAttribute = new PriceAttributes();
		pricAttribute.setFrequency("NRC");
		//pricAttribute.setRequestedRate(2F);
		pricAttribute.setRequestedDiscount(2.2F);
		pricAttribute.setLocalListPrice(22.20);
		pricAttribute.setPriceScenarioId(1L);
		pricAttribute.setTerm(2L);
		pricAttribute.setLocalCurrency("US");
		priceAttributes.add(pricAttribute);
		
		PriceAttributes pricAttribute1 = new PriceAttributes();
		pricAttribute1.setFrequency("NRC");
		pricAttribute1.setLocalListPrice(22.20);
		pricAttribute1.setPriceScenarioId(1L);
		pricAttribute1.setLocalCurrency("US");
		priceAttributes.add(pricAttribute1);
		
		PriceAttributes pricAttribute2 = new PriceAttributes();
		pricAttribute2.setFrequency("NRC");
		pricAttribute2.setRequestedRate(2F);
		pricAttribute2.setPriceScenarioId(1L);
		pricAttribute2.setLocalCurrency("US");
		priceAttributes.add(pricAttribute2);
		
		ComponentDetails componentDetail = new ComponentDetails();
		componentDetail.setPriceAttributes(priceAttributes);
		List<ComponentDetails> componentDetails = new ArrayList<>();
		componentDetails.add(componentDetail);
		priceDetails.setComponentDetails(componentDetails);
		Long priceScenarioId = 1L;
		Long nxDesignId = 1L;
		Long nxTxnId = 1L;
		Map<String, Object> designMap = new HashMap<>();
		String flowType = "ADE";
		Long contractTerm = 1L;
		String thirdPartyInd = "Y";
		Map<String,List<String>> asenod3PaUsocIdMap = new HashMap<String,List<String>>();
		Mockito.when(configureDesignWSHandler.getAsenod3PaUsocIdMap()).thenReturn(asenod3PaUsocIdMap);
		String tempBeid = "123";
		Map<String,List<String>> asenod3PaUsocId = asenod3PaUsocIdMap;
		asenod3PaUsocIdMap.put("123", new ArrayList<>());
		priceDetails.getComponentDetails().stream().forEach(compontDetails->{
			String componentId = compontDetails.getComponentId()!=null?compontDetails.getComponentId().toString():"";
			compontDetails.getPriceAttributes().stream().forEach(prizeAttribute -> {
				Mockito.when(configureDesignWSHandler.getConvertedUsocIdFor3PA(asenod3PaUsocId, prizeAttribute.getBeid())).thenReturn(tempBeid);
				Optional<?> beid = Optional.ofNullable(tempBeid);
				NxMpDesignDocument nxMpDesignDocument = new NxMpDesignDocument();
				nxMpDesignDocument.setMpDocumentNumber(1L);
				Mockito.when(nxMpDesignDocumentRepository.findByTxnDesignUsocComponentIds(nxTxnId, nxDesignId, beid.get().toString(),componentId)).thenReturn(nxMpDesignDocument);
			
				UpdateTransactionLineItem transactionLineItem = new UpdateTransactionLineItem();
				GetTransactionLineItemPrice getTransactionLineItemPrice = new GetTransactionLineItemPrice();
				getTransactionLineItemPrice.setValue(prizeAttribute.getRequestedRate());
				for(UpdateTransactionLineItem item : items) {
					item.setRequestedEffectivePriceNRC(getTransactionLineItemPrice);
				}
/*				Float value = (float) (prizeAttribute.getLocalListPrice() - ((prizeAttribute.getLocalListPrice() * prizeAttribute.getRequestedDiscount())/100));
				getTransactionLineItemPrice.setValue(value);
				getTransactionLineItemPrice.setValue(prizeAttribute.getLocalListPrice().floatValue());
				for(UpdateTransactionLineItem item : items) {
					if(item.getDocumentNumber().equalsIgnoreCase(nxMpDesignDocument.getMpDocumentNumber().toString())) {
						if("MRC".equalsIgnoreCase(prizeAttribute.getFrequency())) {
							item.setRequestedEffectivePriceMRC(getTransactionLineItemPrice);
						} else {
							item.setRequestedEffectivePriceNRC(getTransactionLineItemPrice);
						}
					}
				}
				transactionLineItem = new UpdateTransactionLineItem();
				transactionLineItem.setDocumentNumber(nxMpDesignDocument.getMpDocumentNumber().toString());
				if("MRC".equalsIgnoreCase(prizeAttribute.getFrequency())) {
					transactionLineItem.setRequestedEffectivePriceMRC(getTransactionLineItemPrice);
				} else {
					transactionLineItem.setRequestedEffectivePriceNRC(getTransactionLineItemPrice);
				}
*/				items.add(transactionLineItem);


			});
		});
		updateTransactionPricingServiceImpl.processPriceDetails(priceDetails, priceScenarioId, nxDesignId, nxTxnId, designMap, flowType, contractTerm, 
				thirdPartyInd);
	}

	
	@Test
	public void savePriceDetialsTest() {
		Long nxTxnId = 1L;
		NxMpDesignDocument nxMpDesignDocument = new NxMpDesignDocument();
		PriceAttributes priceAttribute = new PriceAttributes();
		priceAttribute.setFrequency("NRC");
		ComponentDetails componentDetails = new ComponentDetails();
		Map<String, Object> paramMap = new HashMap<>();
		String flowType = "ASE";
		NxMpPriceDetails nxMpPriceDetails = new NxMpPriceDetails();
		nxMpPriceDetails.setRequestedNRCEffectivePrice(priceAttribute.getRequestedRate());
		Mockito.when(nxMpPriceDetailsRepository.save(nxMpPriceDetails)).thenReturn(nxMpPriceDetails);
		updateTransactionPricingServiceImpl.savePriceDetials(nxTxnId, nxMpDesignDocument, priceAttribute, componentDetails, paramMap, flowType);
	}
	
	@Test
	public void savePriceDetialsTestReconfigOrUpdate() {
		Long nxTxnId = 1L;
		NxMpDesignDocument nxMpDesignDocument = new NxMpDesignDocument();
		PriceAttributes priceAttribute = new PriceAttributes();
		priceAttribute.setFrequency("MRC");
		ComponentDetails componentDetails = new ComponentDetails();
		Map<String, Object> paramMap = new HashMap<>();
		String flowType = "ADE";
		paramMap.put(StringConstants.TRANSACTION_UPDATE, StringConstants.RECONFIGURE);
		NxMpPriceDetails nxMpPriceDetails = new NxMpPriceDetails();
		Mockito.when(nxMpPriceDetailsRepository.findByMpDocumentNumberAndNxTxnIdAndFrequency(nxMpDesignDocument.getMpDocumentNumber(), nxTxnId, priceAttribute.getFrequency())).thenReturn(nxMpPriceDetails);
		nxMpPriceDetails.setRequestedMRCDiscPercentage(
				priceAttribute.getRequestedMRCDiscPercentage());
		nxMpPriceDetails.setRequestedNRCDiscPercentage(
				priceAttribute.getRequestedNRCDiscPercentage());	
		nxMpPriceDetails.setFrequency(priceAttribute.getFrequency());
		nxMpPriceDetails.setBeid(priceAttribute.getBeid());
		nxMpPriceDetails.setComponentType(componentDetails.getComponentType());
		nxMpPriceDetails.setComponentId(componentDetails.getComponentId());
		nxMpPriceDetails.setComponentParentId(componentDetails.getComponentParentId());
		nxMpPriceDetails.setRequestedMRCEffectivePrice(priceAttribute.getRequestedRate());
		Mockito.when(nxMpPriceDetailsRepository.save(nxMpPriceDetails)).thenReturn(nxMpPriceDetails);
		updateTransactionPricingServiceImpl.savePriceDetials(nxTxnId, nxMpDesignDocument, priceAttribute, componentDetails, paramMap, flowType);
	}
	
	@Test
	public void callUpdateTransactionPricingRequestApiTest() throws SalesBusinessException {
		String myPriceTxnId = "12345";
		UpdateTransactionPricingRequest request = new UpdateTransactionPricingRequest();
		String uri = "https://custompricingdev2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/actions/updateRequestedRates";
		Mockito.when(env.getProperty("myprice.updateTransactionPricingRequest")).thenReturn(uri);
		String res = new Object().toString();
		Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString()))
		.thenReturn(res);
		updateTransactionPricingServiceImpl.callUpdateTransactionPricingRequestApi(myPriceTxnId, request);
	}
	
}
