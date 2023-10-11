package com.att.sales.nexxus.myprice.transaction.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyCollection;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxLineItemLookUpDataModel;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxMpPriceDetails;
import com.att.sales.nexxus.dao.model.NxMpSiteDictionary;
import com.att.sales.nexxus.dao.model.NxMpSolutionDetails;
import com.att.sales.nexxus.dao.model.NxRatePlanDetails;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxLineItemLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxMpPriceDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxMpSiteDictionaryRepository;
import com.att.sales.nexxus.dao.repository.NxMpSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxRatePlanDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.myprice.transaction.model.GetTransactionLineItem;
import com.att.sales.nexxus.myprice.transaction.model.GetTransactionLineItemPrice;
import com.att.sales.nexxus.myprice.transaction.model.GetTransactionLineResponse;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.RestClientUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)

public class GetTransactionLineServiceImplTest {
	
	@InjectMocks
	@Spy
	private GetTransactionLineServiceImpl getTransactionLineServiceImpl;
	
	@Mock
	private Environment env;
	
	@Mock
	private NxMpDealRepository nxMpDealRepository;
	
	@Mock
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;
	
	@Mock
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Mock
	private RestClientUtil restClient;
	
	@Mock
	HttpRestClient httpRest;
	
	@Mock
	private NxMpSiteDictionaryRepository nxMpSiteDicRepo;
	
	@Mock 
	private NxDesignAuditRepository nxDesignAuditRepository;
	
	@Mock
	private RestTemplateBuilder restTemplateBuilder;
	
	@Mock
	private RestTemplate restTemplate;
	
	@Mock
	private NxMpPriceDetailsRepository nxMpPriceDetailsRepository;
	
	@Mock
	private NxRatePlanDetailsRepository nxRatePlanDetailsRepository;
	
	@Mock
	private NxLineItemLookupDataRepository nxLineItemLookupDataRepository;
	
	@Mock
	private NxMpSolutionDetailsRepository nxMpSolutionDetailsRepository;
	
	private String transactionId = "1010110";
	
	private List<NxMpDeal> deals;
	
	private String response;
	
	private NxSolutionDetail nxSolutionDetail;
	
	private GetTransactionLineResponse getTransactionLineResponse;
	
	private List<NxLookupData> lookUps;
	
	private NxMpDeal deal;
	
	private List<String> lineBomIdList;
	
	private Map<String, Object> requestMap;
	
	@BeforeEach
	public void init() {
		deals = new ArrayList<>();
		deal = new NxMpDeal();
		deal.setSolutionId(101l);
		deal.setOfferId("ASE Classic");
		deal.setNxTxnId(1010l);
		deal.setDealStatus("APPROVED");
		deal.setCreatedDate(new Date());
		deal.setModifiedDate(new Date());
		deals.add(deal);
		
		nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setFlowType("MP_FIRM");
		nxSolutionDetail.setExternalKey(1111l);
		response = "{ \"totalResults\": 8, \"offset\": 0, \"limit\": 1000, \"count\": 8, \"hasMore\": false, \"links\": [ { \"rel\": \"canonical\", \"href\": \"https://custompricingst.att.com/rest/v9/commerceDocumentsOraclecpqo_bmClone_2Transaction/225982343/transactionLine\" }, { \"rel\": \"self\", \"href\": \"https://custompricingst.att.com/rest/v9/commerceDocumentsOraclecpqo_bmClone_2Transaction/225982343/transactionLine?fields=_line_bom_id,_line_bom_parent_id,_parent_line_item,_document_number,_parent_doc_number,_model_name,_model_variable_name,lii_nxSiteId_ql,lii_asrID_ql,wl_int_model_asr,lii_uSOC_ql,wi_uniqueID_ql,wl_int_ade_site_reln,_line_bom_part_number&totalResults=true&offset=0&limit=1000\" } ], \"items\": [ { \"_document_number\": 2, \"_model_variable_name\": \"solution\", \"_line_bom_parent_id\": null, \"_model_name\": \"Solution\", \"lii_nxSiteId_ql\": null, \"_line_bom_id\": \"BOM_Solution\", \"wl_int_ade_site_reln\": null, \"_parent_line_item\": null, \"wi_uniqueID_ql\": null, \"lii_uSOC_ql\": null, \"_line_bom_part_number\": \"Solution\", \"_parent_doc_number\": null, \"lii_asrID_ql\": null, \"wl_int_model_asr\": null, \"links\": [ { \"rel\": \"self\", \"href\": \"https://custompricingst.att.com/rest/v9/commerceDocumentsOraclecpqo_bmClone_2Transaction/225982343/transactionLine/2\" }, { \"rel\": \"parent\", \"href\": \"https://custompricingst.att.com/rest/v9/commerceDocumentsOraclecpqo_bmClone_2Transaction/225982343\" } ] }, { \"_document_number\": 3, \"_model_variable_name\": \"aSE\", \"_line_bom_parent_id\": \"BOM_Solution\", \"_model_name\": \"ASE\", \"lii_nxSiteId_ql\": null, \"_line_bom_id\": \"BOM_ASE\", \"wl_int_ade_site_reln\": null, \"_parent_line_item\": \"Solution\", \"wi_uniqueID_ql\": null, \"lii_uSOC_ql\": null, \"_line_bom_part_number\": \"ASE\", \"_parent_doc_number\": \"2\", \"lii_asrID_ql\": null, \"wl_int_model_asr\": \"R00002035897\", \"links\": [ { \"rel\": \"self\", \"href\": \"https://custompricingst.att.com/rest/v9/commerceDocumentsOraclecpqo_bmClone_2Transaction/225982343/transactionLine/3\" }, { \"rel\": \"parent\", \"href\": \"https://custompricingst.att.com/rest/v9/commerceDocumentsOraclecpqo_bmClone_2Transaction/225982343\" } ] }, { \"_document_number\": 4, \"_model_variable_name\": null, \"productType_l\": \"ASE Classic\", \"_line_bom_parent_id\": \"BOM_ASE\", \"_model_name\": null, \"lii_nxSiteId_ql\": \"451439\", \"_line_bom_id\": \"BOM_ASEASE_CRT_5000\", \"wl_int_ade_site_reln\": null, \"_parent_line_item\": \"ASE\", \"wi_uniqueID_ql\": \"#FCC - 9 States#5000 Mbps CIR#RealTime - Basic Only#OEM5T\", \"lii_uSOC_ql\": \"OEM5T\", \"_line_bom_part_number\": \"ASE_CRT_5000\", \"_parent_doc_number\": \"3\", \"lii_asrID_ql\": \"R00002035897\", \"wl_int_model_asr\": null, \"lii_approvedDiscountPctgMRC_ql\":\"100\", \"links\": [ { \"rel\": \"self\", \"href\": \"https://custompricingst.att.com/rest/v9/commerceDocumentsOraclecpqo_bmClone_2Transaction/225982343/transactionLine/4\" }, { \"rel\": \"parent\", \"href\": \"https://custompricingst.att.com/rest/v9/commerceDocumentsOraclecpqo_bmClone_2Transaction/225982343\" } ] }, { \"_document_number\": 5, \"_model_variable_name\": null, \"_line_bom_parent_id\": \"BOM_ASE\", \"_model_name\": null, \"lii_nxSiteId_ql\": \"451439\", \"_line_bom_id\": \"BOM_ASEASE_CPCB_10000\", \"wl_int_ade_site_reln\": null, \"_parent_line_item\": \"ASE\", \"wi_uniqueID_ql\": \"#FCC - 9 States#Customer Port Connection - 10 Gbps#Basic#OEMXG\", \"lii_uSOC_ql\": \"OEMXG\", \"_line_bom_part_number\": \"ASE_CPCB_10000\", \"_parent_doc_number\": \"3\", \"lii_asrID_ql\": \"R00002035897\", \"wl_int_model_asr\": null, \"lii_approvedDiscountPctgNRC_ql\":\"100\", \"links\": [ { \"rel\": \"self\", \"href\": \"https://custompricingst.att.com/rest/v9/commerceDocumentsOraclecpqo_bmClone_2Transaction/225982343/transactionLine/5\" }, { \"rel\": \"parent\", \"href\": \"https://custompricingst.att.com/rest/v9/commerceDocumentsOraclecpqo_bmClone_2Transaction/225982343\" } ] }, { \"_document_number\": 6, \"_model_variable_name\": \"solution\", \"_line_bom_parent_id\": null, \"_model_name\": \"Solution\", \"lii_nxSiteId_ql\": null, \"_line_bom_id\": \"BOM_Solution\", \"wl_int_ade_site_reln\": null, \"_parent_line_item\": null, \"wi_uniqueID_ql\": null, \"lii_uSOC_ql\": null, \"_line_bom_part_number\": \"Solution\", \"_parent_doc_number\": null, \"lii_asrID_ql\": null, \"wl_int_model_asr\": null, \"links\": [ { \"rel\": \"self\", \"href\": \"https://custompricingst.att.com/rest/v9/commerceDocumentsOraclecpqo_bmClone_2Transaction/225982343/transactionLine/6\" }, { \"rel\": \"parent\", \"href\": \"https://custompricingst.att.com/rest/v9/commerceDocumentsOraclecpqo_bmClone_2Transaction/225982343\" } ] }, { \"_document_number\": 7, \"_model_variable_name\": \"aSE\", \"_line_bom_parent_id\": \"BOM_Solution\", \"_model_name\": \"ASE\", \"lii_nxSiteId_ql\": null, \"_line_bom_id\": \"BOM_ASE\", \"wl_int_ade_site_reln\": null, \"_parent_line_item\": \"Solution\", \"wi_uniqueID_ql\": null, \"lii_uSOC_ql\": null, \"_line_bom_part_number\": \"ASE\", \"_parent_doc_number\": \"6\", \"lii_asrID_ql\": null, \"wl_int_model_asr\": \"100002035898\", \"links\": [ { \"rel\": \"self\", \"href\": \"https://custompricingst.att.com/rest/v9/commerceDocumentsOraclecpqo_bmClone_2Transaction/225982343/transactionLine/7\" }, { \"rel\": \"parent\", \"href\": \"https://custompricingst.att.com/rest/v9/commerceDocumentsOraclecpqo_bmClone_2Transaction/225982343\" } ] }, { \"_document_number\": 8, \"_model_variable_name\": null, \"productType_l\": \"ASEoD\", \"_line_bom_parent_id\": \"BOM_ASE\", \"_model_name\": null, \"lii_nxSiteId_ql\": \"451440\", \"_line_bom_id\": \"BOM_ASEASE_CI_1000\", \"wl_int_ade_site_reln\": null, \"_parent_line_item\": \"ASE\", \"wi_uniqueID_ql\": \"#FCC - 9 States#1000 Mbps CIR#Interactive - Basic Only#OEM1T\", \"lii_uSOC_ql\": \"OEM1T\", \"_line_bom_part_number\": \"ASE_CI_1000\", \"_parent_doc_number\": \"7\", \"lii_asrID_ql\": \"100002035898\", \"wl_int_model_asr\": null, \"links\": [ { \"rel\": \"self\", \"href\": \"https://custompricingst.att.com/rest/v9/commerceDocumentsOraclecpqo_bmClone_2Transaction/225982343/transactionLine/8\" }, { \"rel\": \"parent\", \"href\": \"https://custompricingst.att.com/rest/v9/commerceDocumentsOraclecpqo_bmClone_2Transaction/225982343\" } ] }, { \"_document_number\": 9, \"_model_variable_name\": null, \"_line_bom_parent_id\": \"BOM_ASE\", \"_model_name\": null, \"lii_nxSiteId_ql\": \"451440\", \"_line_bom_id\": \"BOM_ASEASE_CPCB_1000\", \"wl_int_ade_site_reln\": null, \"_parent_line_item\": \"ASE\", \"wi_uniqueID_ql\": \"#FCC - 9 States#Customer Port Connection - 1 Gbps#Basic#OEM1G\", \"lii_uSOC_ql\": \"OEM1G\", \"_line_bom_part_number\": \"ASE_CPCB_1000\", \"_parent_doc_number\": \"7\", \"lii_asrID_ql\": \"100002035898\", \"wl_int_model_asr\": null, \"links\": [ { \"rel\": \"self\", \"href\": \"https://custompricingst.att.com/rest/v9/commerceDocumentsOraclecpqo_bmClone_2Transaction/225982343/transactionLine/9\" }, { \"rel\": \"parent\", \"href\": \"https://custompricingst.att.com/rest/v9/commerceDocumentsOraclecpqo_bmClone_2Transaction/225982343\" } ] } ] }";
		
		lookUps = new ArrayList<>();
		NxLookupData lookUp = new NxLookupData();
		lookUp.setDescription("BOM_BVoIP");
		lookUp.setDescription("BOM_AVPN");
		lookUp.setDescription("BOM_ASE");
		lookUps.add(lookUp);
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			getTransactionLineResponse = mapper.readValue(response,
					GetTransactionLineResponse.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		lineBomIdList = new ArrayList<>();
		lineBomIdList.add("BOM_BVoIP");
		lineBomIdList.add("BOM_AVPN");
		
		requestMap = new HashMap<String, Object>();
	}
	
	@Test
	public void testGetTransactionLine() throws SalesBusinessException {
		Mockito.when(env.getProperty("myprice.getTransactionLine")).thenReturn("https://custompricingst.att.com/rest/v8/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/transactionLine");
		Mockito.when(env.getProperty("myprice.username")).thenReturn("");
		Mockito.when(env.getProperty("myprice.password")).thenReturn("");
		Mockito.when(env.getProperty("myprice.transactionLine.fields")).thenReturn("?fields=lii_uSOC_ql,_document_number,_line_bom_id,rl_extPriceMRC_ql,rl_extPriceNRC_ql,_line_bom_part_number,_parent_doc_number,lii_approvedDiscountPctgNRC_ql,lii_approvedEffectivePriceNRC_ql,lii_approvedNetEffectivePriceNRC_ql,lii_approvedDiscountPctgMRC_ql,lii_approvedEffectivePriceMRC_ql,lii_approvedNetEffectivePriceMRC_ql,_model_product_line_name,_model_variable_name,lii_nxSiteId_ql,lii_countryAbbrev_ql,lii_country_ql,lii_specialConstructionEffectiveCharge_ql,lii_asrID_ql,lii_contractTerm_ql,lii_jurisdiction_ql,productType_l,lii_requestedEffectivePriceMRC_ql,lii_requestedEffectivePriceNRC_ql,wi_uniqueID_ql,lii_Token_ql,lii_productVariation_ql,lii_sOCDate_ql,rl_isProductRow_ql,rl_product_ql,lii_siteName_ql&totalResults=true&offset=");
		Mockito.when(nxMpDealRepository.getByTransactionIdAndNxTxnOrder(anyString())).thenReturn(deals);
		Mockito.when(nxSolutionDetailsRepository.findByNxSolutionId(anyLong())).thenReturn(nxSolutionDetail);
		Mockito.when(nxLookupDataRepository.findByDatasetName(anyString())).thenReturn(lookUps);
		Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString())).thenReturn(response);
		Mockito.when(restClient.processResult(anyString(), any())).thenReturn(getTransactionLineResponse);
		Mockito.when(nxMpDealRepository.findAllByTransactionId(anyString())).thenReturn(deals);
		getTransactionLineServiceImpl.getTransactionLine(transactionId, new HashMap<>());
	}
	
	@Test
	public void testUpdatePriceDetails() {
		Mockito.when(nxMpDealRepository.save(any(NxMpDeal.class))).thenReturn(deal);
		Mockito.when(nxMpSiteDicRepo.findByNxTxnId(anyLong())).thenReturn(new NxMpSiteDictionary());
		Mockito.when(nxDesignAuditRepository.findByNxRefIdAndTransaction(anyLong(), anyString())).thenReturn(null);
		Mockito.when(nxDesignAuditRepository.saveAndFlush(any(NxDesignAudit.class))).thenReturn(null);
		Mockito.when(nxMpPriceDetailsRepository.saveAll(anyCollection())).thenReturn(null);
		getTransactionLineServiceImpl.updatePriceDetails(getTransactionLineResponse, transactionId, deal, nxSolutionDetail, lineBomIdList, false);
	}
	
	@Test
	public void testSetPriceDetails() {
		NxMpPriceDetails priceDetails = new NxMpPriceDetails();
		priceDetails.setFrequency("MRC");
		GetTransactionLineItem item = new GetTransactionLineItem();
		GetTransactionLineItemPrice price = new GetTransactionLineItemPrice();
		price.setValue(100.0f);
		item.setSocVersion("1.0");
		deal.setOfferId("AVPN");
		List<NxRatePlanDetails> nxRatePlanDetailList = new ArrayList<>();
		NxRatePlanDetails ratePlan = new NxRatePlanDetails();
		ratePlan.setRatePlanId(1l);
		nxRatePlanDetailList.add(ratePlan);
		Mockito.when(nxRatePlanDetailsRepository.findBySocDateAndProduct(anyString(), anyString())).thenReturn(nxRatePlanDetailList);
		List<NxLineItemLookUpDataModel> nxLookUpLineItemList = new ArrayList<NxLineItemLookUpDataModel>();
		NxLineItemLookUpDataModel lookup = new NxLineItemLookUpDataModel();
		lookup.setField1Value("1233");
		lookup.setField2Value("123");
		lookup.setLittleProdId(6005l);
		nxLookUpLineItemList.add(lookup);
		Mockito.when(nxLineItemLookupDataRepository.findByUniqueIdAndFlowType(anyString(), anyString())).thenReturn(nxLookUpLineItemList);
		Mockito.when(nxLineItemLookupDataRepository.findSourceIdByBid(anyString())).thenReturn("11");
		getTransactionLineServiceImpl.setPriceDetails(priceDetails, item, deal, "FMO", false);
		
		deal.setOfferId("ADI");
		lookup.setLittleProdId(5380l);
		Mockito.when(nxRatePlanDetailsRepository.findTopBySocDateContainingAndProductAndActiveYnOrderBySocDateDesc(any(), any(), any())).thenReturn(ratePlan);
		item.setUniqueIds("xxx Schedule 2 xxx");
		getTransactionLineServiceImpl.setPriceDetails(priceDetails, item, deal, "MP_FIRM", false);
		
		item.setUniqueIds("xxx Schedule 3 xxx");
		getTransactionLineServiceImpl.setPriceDetails(priceDetails, item, deal, "MP_FIRM", false);
		
		Mockito.when(nxRatePlanDetailsRepository.findTopByProductAndErateIndicatorAndActiveYnOrderBySocDateDesc(any(), any(), any())).thenReturn(ratePlan);
		getTransactionLineServiceImpl.setPriceDetails(priceDetails, item, deal, "MP_FIRM", true);
		
		deal.setOfferId("AVPN");
		NxMpPriceDetails priceDetails1 = new NxMpPriceDetails();
		priceDetails1.setFrequency("NRC");
		Mockito.when(nxRatePlanDetailsRepository.findTopBySocDateAndProductAndActiveYn(any(), any(), any())).thenReturn(ratePlan);
		getTransactionLineServiceImpl.setPriceDetails(priceDetails1, item, deal, "MP_FIRM", false);
		
		
		deal.setOfferId("ADI");
		getTransactionLineServiceImpl.setPriceDetails(priceDetails1, item, deal, "MP_FIRM", false);
		
		deal.setOfferId("BVoIP");
		List<Object> rates = new ArrayList<>();
		rates.add("NRC");
		rates.add("MRC");
		Mockito.when(nxLineItemLookupDataRepository.findRateTypeByProdRateId(anyString())).thenReturn(rates);
		getTransactionLineServiceImpl.setPriceDetails(priceDetails1, item, deal, "MP_FIRM", false);
	}

	@Test
	public void testGetTransactionLineSalesOne() throws SalesBusinessException {
		Mockito.when(env.getProperty("myprice.getTransactionLine")).thenReturn("https://custompricingst.att.com/rest/v8/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/transactionLine");
		Mockito.when(env.getProperty("myprice.username")).thenReturn("");
		Mockito.when(env.getProperty("myprice.password")).thenReturn("");
		Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString())).thenReturn(response);
		Mockito.when(restClient.processResult(anyString(), any())).thenReturn(getTransactionLineResponse);
		getTransactionLineServiceImpl.getTransactionLineSalesOne(transactionId);
	}
	
	@Test
	public void testPublishDmaapForAutomationFlow() {
		Mockito.when(nxMpSolutionDetailsRepository.findByNxTxnId(anyLong())).thenReturn(new NxMpSolutionDetails());
		getTransactionLineServiceImpl.publishDmaapForAutomationFlow(deal, "Submitted");
	}
	
	@Test
	public void getTransactionLineConfigDesignSolution() throws SalesBusinessException {
		Mockito.when(env.getProperty("myprice.getTransactionLine.rest.url")).thenReturn("https://custompricingst.att.com/rest/v8/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/transactionLine");
		Mockito.when(env.getProperty("myprice.username")).thenReturn("");
		Mockito.when(env.getProperty("myprice.password")).thenReturn("");
		Mockito.when(env.getProperty("myprice.gettransactionLine.fmo.fields")).thenReturn("?fields=lii_uSOC_ql,_document_number,_line_bom_id,rl_extPriceMRC_ql,rl_extPriceNRC_ql,_line_bom_part_number,_parent_doc_number,lii_approvedDiscountPctgNRC_ql,lii_approvedEffectivePriceNRC_ql,lii_approvedNetEffectivePriceNRC_ql,lii_approvedDiscountPctgMRC_ql,lii_approvedEffectivePriceMRC_ql,lii_approvedNetEffectivePriceMRC_ql,_model_product_line_name,_model_variable_name,lii_nxSiteId_ql,lii_countryAbbrev_ql,lii_country_ql,lii_specialConstructionEffectiveCharge_ql,lii_asrID_ql,lii_contractTerm_ql,lii_jurisdiction_ql,productType_l,lii_requestedEffectivePriceMRC_ql,lii_requestedEffectivePriceNRC_ql,wi_uniqueID_ql,lii_Token_ql,lii_productVariation_ql,lii_sOCDate_ql,rl_isProductRow_ql,rl_product_ql,lii_siteName_ql&totalResults=true&offset=");
		Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString())).thenReturn(response);
		Mockito.when(restClient.processResult(anyString(), any())).thenReturn(getTransactionLineResponse);
		getTransactionLineServiceImpl.getTransactionLineConfigDesignSolution(transactionId, "AVPN", 1l, requestMap);
	}
	
	@Test
	public void getTransactionLineConfigDesignBasedOnField() throws SalesBusinessException {
		Mockito.when(env.getProperty("myprice.getTransactionLine.rest.url")).thenReturn("https://custompricingst.att.com/rest/v8/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/transactionLine");
		Mockito.when(env.getProperty("myprice.username")).thenReturn("");
		Mockito.when(env.getProperty("myprice.password")).thenReturn("");
		Mockito.when(env.getProperty("myprice.gettransactionLine.ase.asrItemId.fields")).thenReturn("?fields=lii_uSOC_ql,_document_number,_line_bom_id,rl_extPriceMRC_ql,rl_extPriceNRC_ql,_line_bom_part_number,_parent_doc_number,lii_approvedDiscountPctgNRC_ql,lii_approvedEffectivePriceNRC_ql,lii_approvedNetEffectivePriceNRC_ql,lii_approvedDiscountPctgMRC_ql,lii_approvedEffectivePriceMRC_ql,lii_approvedNetEffectivePriceMRC_ql,_model_product_line_name,_model_variable_name,lii_nxSiteId_ql,lii_countryAbbrev_ql,lii_country_ql,lii_specialConstructionEffectiveCharge_ql,lii_asrID_ql,lii_contractTerm_ql,lii_jurisdiction_ql,productType_l,lii_requestedEffectivePriceMRC_ql,lii_requestedEffectivePriceNRC_ql,wi_uniqueID_ql,lii_Token_ql,lii_productVariation_ql,lii_sOCDate_ql,rl_isProductRow_ql,rl_product_ql,lii_siteName_ql&totalResults=true&offset=");
		Mockito.when(env.getProperty("myprice.gettransactionLine.asenod.nxSiteId.fields")).thenReturn("?fields=lii_uSOC_ql,_document_number,_line_bom_id,rl_extPriceMRC_ql,rl_extPriceNRC_ql,_line_bom_part_number,_parent_doc_number,lii_approvedDiscountPctgNRC_ql,lii_approvedEffectivePriceNRC_ql,lii_approvedNetEffectivePriceNRC_ql,lii_approvedDiscountPctgMRC_ql,lii_approvedEffectivePriceMRC_ql,lii_approvedNetEffectivePriceMRC_ql,_model_product_line_name,_model_variable_name,lii_nxSiteId_ql,lii_countryAbbrev_ql,lii_country_ql,lii_specialConstructionEffectiveCharge_ql,lii_asrID_ql,lii_contractTerm_ql,lii_jurisdiction_ql,productType_l,lii_requestedEffectivePriceMRC_ql,lii_requestedEffectivePriceNRC_ql,wi_uniqueID_ql,lii_Token_ql,lii_productVariation_ql,lii_sOCDate_ql,rl_isProductRow_ql,rl_product_ql,lii_siteName_ql&totalResults=true&offset=");
		Mockito.when(env.getProperty("myprice.gettransactionLine.ase.v1.asrItemId.fields")).thenReturn("?fields=lii_uSOC_ql,_document_number,_line_bom_id,rl_extPriceMRC_ql,rl_extPriceNRC_ql,_line_bom_part_number,_parent_doc_number,lii_approvedDiscountPctgNRC_ql,lii_approvedEffectivePriceNRC_ql,lii_approvedNetEffectivePriceNRC_ql,lii_approvedDiscountPctgMRC_ql,lii_approvedEffectivePriceMRC_ql,lii_approvedNetEffectivePriceMRC_ql,_model_product_line_name,_model_variable_name,lii_nxSiteId_ql,lii_countryAbbrev_ql,lii_country_ql,lii_specialConstructionEffectiveCharge_ql,lii_asrID_ql,lii_contractTerm_ql,lii_jurisdiction_ql,productType_l,lii_requestedEffectivePriceMRC_ql,lii_requestedEffectivePriceNRC_ql,wi_uniqueID_ql,lii_Token_ql,lii_productVariation_ql,lii_sOCDate_ql,rl_isProductRow_ql,rl_product_ql,lii_siteName_ql&totalResults=true&offset=");
		Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString())).thenReturn(response);
		Mockito.when(restClient.processResult(anyString(), any())).thenReturn(getTransactionLineResponse);
		getTransactionLineServiceImpl.getTransactionLineConfigDesignBasedOnField(transactionId, "ASENoD", "asenod_IR", 1l, "111", requestMap,null);
		
		getTransactionLineServiceImpl.getTransactionLineConfigDesignBasedOnField(transactionId, "ASENoD", "asenod_3PA", 1l, "111", requestMap,null);
	}
	
	@Test
	public void getTransactionLineConfigDesignBasedOnFieldRestV2() throws SalesBusinessException {
		requestMap.put(StringConstants.REST_VERSION, StringConstants.VERSION_2);
		Mockito.when(env.getProperty("myprice.getTransactionLine.rest.url")).thenReturn("https://custompricingst.att.com/rest/v8/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/transactionLine");
		Mockito.when(env.getProperty("myprice.username")).thenReturn("");
		Mockito.when(env.getProperty("myprice.password")).thenReturn("");
		Mockito.when(env.getProperty("myprice.restv2.gettransactionLine.ase.asrItemId.fields")).thenReturn("?fields=_line_bom_id,_line_bom_parent_id,_parent_line_item,_document_number,_parent_doc_number,_model_name,_model_variable_name,lii_nxSiteId_ql,wl_int_model_sites,lii_asrID_ql,wl_int_model_asr,lii_uSOC_ql,wi_uniqueID_ql,wl_int_ade_site_reln,_line_bom_part_number&q={$or:[{lii_asrID_ql},{_line_bom_id:{$regex:'BOM_Solution'}},{$and:[{_line_bom_id:{$regex:'BOM_ASE'}},{_line_bom_parent_id:{$regex:'BOM_Solution'}}]}]}&totalResults=true&offset=");
		Mockito.when(env.getProperty("myprice.restv2.gettransactionLine.asenod.nxSiteId.fields")).thenReturn("?fields=_line_bom_id,_line_bom_parent_id,_parent_line_item,_document_number,_parent_doc_number,_model_name,_model_variable_name,lii_nxSiteId_ql,lii_asrID_ql,wl_int_model_asr,wl_int_model_sites,lii_uSOC_ql,wi_uniqueID_ql,wl_int_ade_site_reln,_line_bom_part_number&q={$or:[{lii_nxSiteId_ql},{_line_bom_id:{$regex:'BOM_Solution'}},{$and:[{_line_bom_id:{$regex:'BOM_ASE'}},{_line_bom_parent_id:{$regex:'BOM_Solution'}}]}]}&totalResults=true&offset=");
		Mockito.when(env.getProperty("myprice.restv2.astItemIds")).thenReturn("{lii_asrID_ql:{$regex:'{asrItemID}'}}");
		Mockito.when(env.getProperty("myprice.restv2.nxSiteIds")).thenReturn("{lii_nxSiteId_ql:{$regex:'{nxSiteId}'}}");
		Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString())).thenReturn(response);
		Mockito.when(restClient.processResult(anyString(), any())).thenReturn(getTransactionLineResponse);
		getTransactionLineServiceImpl.getTransactionLineConfigDesignBasedOnField(transactionId, "ASENoD", "asenod_IR", 1l, "111,123", requestMap,null);
		
		getTransactionLineServiceImpl.getTransactionLineConfigDesignBasedOnField(transactionId, "ASENoD", "asenod_3PA", 1l, "111,123", requestMap,null);
	}
	
	@Test
	public void testIsApprovedThenNotify() {
		List<NxMpDeal> nxMpDealList = new ArrayList<NxMpDeal>();
		NxMpDeal deal = new NxMpDeal();
		deal.setDealStatus(CommonConstants.APPROVED);
		deal.setAction(StringConstants.DEAL_ACTION_PD_CLONE);
		nxMpDealList.add(deal);
		Mockito.when(nxMpDealRepository.findAllByTransactionId(anyString())).thenReturn(nxMpDealList);
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setExternalKey(1019l);
		Mockito.when(nxSolutionDetailsRepository.findByNxSolutionId(anyLong())).thenReturn(nxSolutionDetail);
		Mockito.when(nxSolutionDetailsRepository.getSolutionByDealId(anyString())).thenReturn(nxSolutionDetail);
		Mockito.when(nxMpSolutionDetailsRepository.findByNxTxnId(anyLong())).thenReturn(new NxMpSolutionDetails());
		getTransactionLineServiceImpl.isApprovedThenNotify("12345");
	}
}
