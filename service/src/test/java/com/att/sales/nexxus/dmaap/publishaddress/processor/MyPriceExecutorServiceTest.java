package com.att.sales.nexxus.dmaap.publishaddress.processor;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyMap;

import static org.mockito.Mockito.anyString;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxMpSiteDictionary;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTxnSiteUploadResponse;
import com.att.sales.nexxus.myprice.transaction.service.MyPriceTransactionUtil;
import com.att.sales.nexxus.myprice.transaction.service.UpdateTxnSiteUploadServiceImpl;
import com.att.sales.nexxus.serviceValidation.service.AVSQUtil;
import com.att.sales.nexxus.serviceValidation.service.GetQualificationServiceImpl;
import com.att.sales.nexxus.util.DME2RestClient;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.RestClientUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@ExtendWith(MockitoExtension.class)
public class MyPriceExecutorServiceTest {
	@InjectMocks 
	MyPriceExecutorService myPriceExecutorService;
	
	@InjectMocks 
	MyPriceInitiatedFlow myPriceInitiatedFlow;
	
	@Mock
	NxMpDealRepository nxMpDealRepository;
	
	@Mock
	ObjectMapper mapper;
	
	@Mock
	MyPriceTransactionUtil myPriceTransactionUtil;
	
	@Mock
	RestClientUtil restClient;
	
	@Mock
	NxSolutionDetailsRepository repository;
	
	@Mock
	DME2RestClient dme2RestClient;
	
	@Mock
	AVSQUtil avsqUtil;
	
	@Mock
	GetQualificationServiceImpl getQualificationServiceImpl;
	
	
	@Mock
	UpdateTxnSiteUploadServiceImpl updateTxnSiteUploadServiceImpl;
	

	@Mock
	java.net.Proxy proxy;
	
	
	@Mock
	Environment env;
	
	@Mock
	HttpRestClient httpRest;
	
	
	
	@BeforeEach
	public void initializeServiceMetaData() {
		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(ServiceMetaData.OFFER, "ASE");
		requestParams.put(ServiceMetaData.REST_REQUEST_START_TIME, System.currentTimeMillis());
		requestParams.put(ServiceMetaData.VERSION, "1.0");
		requestParams.put(ServiceMetaData.METHOD, "TestMethod");
		requestParams.put(ServiceMetaData.URI, "Testuri");
		ServiceMetaData.add(requestParams);
	}
	 

	@SuppressWarnings("unchecked")
	@Test
	public void testDmaapSubscribe() throws SalesBusinessException, JsonProcessingException, JSONException  {
		myPriceExecutorService.setGetQualificationServiceImpl(getQualificationServiceImpl);
		myPriceExecutorService.setMyPriceInitiatedFlow(myPriceInitiatedFlow);
		myPriceExecutorService.setNxMpDealRepository(nxMpDealRepository);
		myPriceExecutorService.setUpdateTxnSiteUploadServiceImpl(updateTxnSiteUploadServiceImpl);
		String messageString = "{\r\n" + 
				"	\"sourceSystem\":\"myPrice\",\r\n" + 
				"	\"motsId\":27226,\r\n" + 
				"	\"uniqueId\":\"69758139\",\r\n" + 
				"	\"status\":\"COMPLETED\"\r\n" + 
				"}\r\n" + 
				"";
		org.json.JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(messageString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		NxMpDeal nxMpDeal = new NxMpDeal();
		Mockito.when(nxMpDealRepository.findByTransactionId("1")).thenReturn(nxMpDeal);
		Mockito.when(mapper.enable(SerializationFeature.INDENT_OUTPUT)).thenReturn(any());
		Mockito.when(env.getProperty("myPrice.siteStatusUpdate")).thenReturn("https://custompricingdev2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{TransactionId}/actions/siteStatusUpdate");
		String transResponse ="{ \"documents\": { \"rd_requestID_q\": 123, \"rd_revisionNumber_q\": 1, \"version_t\": 0, \"rd_salesChannels_q\": { \"displayValue\": \"System Integrators\", \"value\": \"SystemIntegrators\" }, \"wi_OriginalClonedTxId\":{ \"sourceId\":\"ipne\", \"Action\":\"action\" }, \"mi_applyNetworkCostMBFactor_q\": null, \"d_sub_cUSTOMFIRSTNETPRIMARYUSERSENHANCEDPUSHTO_q\": null, \"ASEoD3PA_swiG34mbps_q\": null, \"ASEoD3PA_swiG350mbps_q\": null, \"myUploadedSites_q\": { \"fileName\": null, \"fileType\": null, \"fileID\": null, \"isFileSaved\": false, \"fileLocation\": \"https://custompricingst2.att.com/rest/v7/commerceProcesses/oraclecpqo_bmClone_2/documents/36712497/attachmentAttributes/myUploadedSites_q/transactions/72935413/documentNumbers/1\" }, \"mi_includeMobileSelectDiscount_q\": \"Include Mobile Select Discount\", \"totalCustomerCost_sitefinancials\": null, \"mi_cRUUpgradesWaived_q\": true, \"requestedDiscounts\": \"Requested Discount %\", \"wLProforma_TimeSeriesTotalHTML_q\": null, \"_customer_t_address\": null, \"_soldTo_t_state\": null, \"wi_creditOption1PricingInput_q\": null, \"devices\": null, \"wireLineAdhocString_q\": null, \"ASEoD3PA_7500mbpsRt_q\": null, \"wl_int_ASEoD3PA_errorLog\": null, \"d_firstNetFederal_q\": false, \"requiredApproval\": null, \"rd_segment_q\": { \"displayValue\": \"Global Business\", \"value\": \"GlobalBusiness\" }, \"ASEoD3PA_swiG114mbps_q\": null, \"ASEoD3PA_100mbpsInt_q\": null, \"d_mACAttainmentChange_q\": false, \"ise_committedGrowthCRUV_q\": null, \"ASEoD3PA_isCirTableRan_q\": false, \"proForma_adHocRevenue_bu\": null, \"tsc_dateRateLetterLastModified_q\": null, \"hurdleGuidelines\": \" \", \"rd_mobile_q\": null, \"_submitted_by_submit_t\": \"\", \"ASEoD3PA_250mbpsBCM_q\": null, \"a_visualWorkflow_q\": \"\", \"d_additionalTCCUSTOM_q\": false, \"ASEoD3PA_150mbpsBCH_q\": null, \"d_aa_retailASE_q\": false, \"ASEoD3PA_2mbpsInt_q\": null, \"wi_creditOptionssalesheader_q\": \"Sales Request\", \"ASEoD3PA_dedG810000mbps_q\": null, \"priceWithinPolicy_t\": true, \"totalOneTimeCostAmount_t\": { \"value\": 0, \"currency\": \"USD\" }, \"fsd_UpfrontCost3\": null, \"ASEoD3PA_swiG650mbps_q\": null, \"integrationSiteDict1\": null, \"ASEoD3PA_swiG10150mbps_q\": null, \"nPVRevenue_t\": { \"value\": 0, \"currency\": \"USD\" }, \"ASEoD3PA_2000mbpsInt_q\": null, \"ASEoD3PA_20mbpsNCH_q\": null, \"wi_siteStatusUpdate_q\": \"Transfer Sites Initiated\", \"_shipTo_t_zip\": null, \"wi_CostView_q\": { \"displayValue\": \"True Cost\", \"value\": \"truecost\" } } }";
			Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString()))
					.thenReturn(transResponse);
			Long nxMpTxnId = null;
			NxMpDeal nxMpDealObj = new NxMpDeal();
			nxMpDealObj.setAction("action");
			nxMpDealObj.setActiveYN("activeYN");
			nxMpDealObj.setNxTxnId(1L);
			Mockito.when(nxMpDealRepository.findByNxTxnId(nxMpTxnId)).thenReturn(nxMpDealObj);
			JSONObject objectJson = new JSONObject();
			try {
				objectJson.put("statusCodeValue", 200);
				objectJson.put("body", new JSONObject());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String response = objectJson.toString();
			Mockito.when(dme2RestClient.callIpeGetQualification(anyMap())).thenReturn(response);
			NxMpSiteDictionary nxMpSiteDictionary = new NxMpSiteDictionary();
			nxMpSiteDictionary.setNxTxnId(1L);
			nxMpSiteDictionary.setActiveYN("activeYN");
			Map<String, Object> resultMap = new HashMap<String, Object>();
			Boolean status = true;
			resultMap.put("status", status);
			resultMap.put("siteData", nxMpSiteDictionary);
			Mockito.when(avsqUtil.populateSiteJsonforQualification(1L, objectJson.get("body").toString(),null)).thenReturn(nxMpSiteDictionary);
			Mockito.when(getQualificationServiceImpl.getQualification(anyString(), any(), anyString(), anyLong(),any())).thenReturn(resultMap);
			UpdateTxnSiteUploadResponse updateTxnSiteUploadResponse = new UpdateTxnSiteUploadResponse();
			Mockito.when(updateTxnSiteUploadServiceImpl.updateTransactionSiteUpload(nxMpDealObj.getTransactionId(), nxMpSiteDictionary.getSiteJson(), new HashMap<String, Object>()))
			.thenReturn(updateTxnSiteUploadResponse);
			myPriceExecutorService.processMessagesForDmapp(jsonObject);
	
	
	
	}
	
}


	