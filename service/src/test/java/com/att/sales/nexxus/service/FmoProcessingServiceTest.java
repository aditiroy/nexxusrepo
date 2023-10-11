package com.att.sales.nexxus.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.accesspricing.model.AccessPriceUIdetails;
import com.att.sales.nexxus.accesspricing.model.AccessPricingAQ;
import com.att.sales.nexxus.constant.CommonConstants;
import com.att.sales.nexxus.constant.FmoConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.FmoJsonRulesModel;
import com.att.sales.nexxus.dao.model.FmoOfferJsonRulesMapping;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.handlers.GetOptyInfoWSHandler;
import com.att.sales.nexxus.helper.FmoLookUpDataHelper;
import com.att.sales.nexxus.myprice.transaction.service.MyPriceTransactionUtil;
import com.att.sales.nexxus.output.entity.NxOutputBean;
import com.att.sales.nexxus.reteriveicb.model.ComponentDetails;
import com.att.sales.nexxus.reteriveicb.model.Offer;
import com.att.sales.nexxus.reteriveicb.model.Port;
import com.att.sales.nexxus.reteriveicb.model.PriceAttributes;
import com.att.sales.nexxus.reteriveicb.model.PriceDetails;
import com.att.sales.nexxus.reteriveicb.model.PricePlanDetails;
import com.att.sales.nexxus.reteriveicb.model.RetreiveICBPSPRequest;
import com.att.sales.nexxus.reteriveicb.model.Site;
import com.att.sales.nexxus.reteriveicb.model.Solution;
import com.att.sales.nexxus.rome.model.GetOptyResponse;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;

/**
 * The Class FmoProcessingServiceTest.
 */
/**
 * @author vt393d
 *
 */
@ExtendWith(MockitoExtension.class)
public class FmoProcessingServiceTest {
	
	@Spy
	@InjectMocks
	private FmoProcessingService fmoProcessingService;
	
	@Mock
	private JsonPathUtil jsonPathUtil;
	
	@Mock
	private LineItemProcessingService lineItemService;
	
	@Mock
	private NexxusJsonUtility nexusJsonUtility;
	
	@Mock
	private ReportService reportService;
	
	@Mock 
	private GetOptyInfoWSHandler getOptyInfoWSHandler;
	
	@Mock
	private FmoProcessingRepoService repositoryService;
	
	@Mock
	private MailServiceImpl mailService;
	
	@Mock
	private ExecutorService executors;
	
	@Mock
	private FmoLookUpDataHelper  fmoLookUpDataHelper;
	
	@Mock
	private SubmitToMyPriceService submitToMyPriceService;
	
	@Mock
	private MyPriceTransactionUtil myPriceTransactionUtil;
	
	@Mock
	private NxMpDealRepository nxMpDealRepository;
	
	@Mock
	private NxDesignAuditRepository nxDesignAuditRepository;
	
	Integer threadSize=5;
	
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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void createFmoNexusOutputTest() throws SalesBusinessException, NoSuchMethodException, SecurityException,
	IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, SerialException, SQLException {
		NxRequestDetails reqDetails=new NxRequestDetails();
		reqDetails.setNxReqId(1l);
		RetreiveICBPSPRequest inputJson=createRequest();
		Map<String, Object> requestMap=new HashMap<>();
		requestMap.put(FmoConstants.CALL_OPTYINFO, FmoConstants.YES);
		GetOptyResponse getOptyResp = new GetOptyResponse();
		when(getOptyInfoWSHandler.initiateGetOptyInfoWebService(any())).thenReturn(getOptyResp);
		Map<Long,Map<String,List<FmoJsonRulesModel>>> dataMap=new HashMap<>();
		Map<String,List<FmoJsonRulesModel>> dd=new HashMap<>();
		dataMap.put(2l, dd);
		List<Long> allOfferId=new ArrayList<>();
		allOfferId.add(12l);
		allOfferId.add(17l);
		when(jsonPathUtil.search(any(),any(),any())).thenReturn(allOfferId);
		Set<Long> allOfferIdSet=new HashSet<>();
		allOfferIdSet.add(12l);
		allOfferIdSet.add(17l);
		Solution solution=new Solution();
		List<Offer> offerLst=new ArrayList<>();
		Offer o=new Offer();
		o.setOfferId("11");
		offerLst.add(o);
		solution.setOffers(offerLst);
		List<FmoOfferJsonRulesMapping> resultLs=new ArrayList<>();
		FmoOfferJsonRulesMapping fo=new FmoOfferJsonRulesMapping();
		FmoJsonRulesModel fmo=new FmoJsonRulesModel();
		fmo.setItemName("sol");
		fo.setFmoRules(fmo);
		resultLs.add(fo);
		when(repositoryService.getFmoRulesFromTbl(any())).thenReturn(resultLs);
		Method method = fmoProcessingService.getClass().getDeclaredMethod("getFmoRules", Solution.class);
		method.setAccessible(true);
		dataMap=(Map) method.invoke(fmoProcessingService,inputJson.getSolution());
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		Blob blob=new SerialBlob(byteStream.toByteArray());
		when(reportService.generateReport(any())).thenReturn(blob);
		fmoProcessingService.createFmoNexxusOutput(reqDetails, inputJson, requestMap);
	}
	
	
	@Test
	public void createFmoNexusOutputException2() throws SalesBusinessException {
		Map<String, Object> requestMap=new HashMap<>();
		NxRequestDetails reqDetails=new NxRequestDetails();
		reqDetails.setNxReqId(1l);
		reqDetails.setStatus(1l);
		RetreiveICBPSPRequest inputJson=createRequest();
		requestMap.put(FmoConstants.CALL_OPTYINFO, FmoConstants.YES);
		doThrow(new SalesBusinessException("")).when(getOptyInfoWSHandler).initiateGetOptyInfoWebService(any());
		fmoProcessingService.createFmoNexxusOutput(reqDetails, inputJson,requestMap);
	}
	
	@Test
	public void createOffersTest() {
		List<Offer> offerLst=new ArrayList<>();
		Offer o=new Offer();
		o.setOfferId("0");
		offerLst.add(o);
		Map<Long,Map<String,List<FmoJsonRulesModel>>> dataMap=new HashMap<>();
		Map<String,List<FmoJsonRulesModel>> dd=new HashMap<>();
		List<FmoJsonRulesModel> resultLs=new ArrayList<>();
		FmoJsonRulesModel fmo=new FmoJsonRulesModel();
		fmo.setItemName("sol");
		fmo.setUdfQuery("$ ...id");
		resultLs.add(fmo);
		dd.put(FmoConstants.OFFER_TAG, resultLs);
		dataMap.put(0l, dd);
		List<PricePlanDetails> pricePlanDetails=new ArrayList<>();
		Long externalKey=5l;
		fmoProcessingService.createOffers(offerLst, dataMap, pricePlanDetails, externalKey);
	}
	
	
	@Test
	public void setStatusForNxRequestDetailsTest() {
		NxRequestDetails reqDetails=new NxRequestDetails();
		reqDetails.setNxReqId(1l);
		fmoProcessingService.setStatusForNxRequestDetails(reqDetails, CommonConstants.SUCCESS);
	}
	@Test
	public void setStatusForNxRequestDetailsTest1() {
		NxRequestDetails reqDetails=new NxRequestDetails();
		reqDetails.setNxReqId(1l);
		fmoProcessingService.setStatusForNxRequestDetails(reqDetails, CommonConstants.FALLOUT);
	}
	
	@Test
	public void setStatusForNxRequestDetailsTest2() {
		NxRequestDetails reqDetails=new NxRequestDetails();
		reqDetails.setNxReqId(1l);
		fmoProcessingService.setStatusForNxRequestDetails(reqDetails, CommonConstants.SUCCESS_WITH_FALLOUT);
	}
	
	
	@Test
	public void createSolutionTest() {
		Solution solution=new Solution();
		Map<Long,Map<String,List<FmoJsonRulesModel>>> dataMap=new HashMap<>();
		Map<String,List<FmoJsonRulesModel>> dd=new HashMap<>();
		List<FmoJsonRulesModel> resultLs=new ArrayList<>();
		FmoJsonRulesModel fmo=new FmoJsonRulesModel();
		fmo.setItemName("sol");
		fmo.setUdfQuery("$ ...id");
		resultLs.add(fmo);
		dd.put(FmoConstants.SOLUTION_TAG, resultLs);
		dataMap.put(0l, dd);
		fmoProcessingService.createSolution(solution, dataMap);
	}
	
	@Test
	public void createSiteTest() {
		when(fmoProcessingService.getThreadSize()).thenReturn(threadSize);
		when(fmoProcessingService.getExcutorService()).thenReturn(executors);
		List<Site> sites=new ArrayList<>();
		Site s=new Site();
		s.setSiteId(12l);
		sites.add(s);
		Map<String,List<FmoJsonRulesModel>> itemMap=new HashMap<>();
		List<FmoJsonRulesModel> resultLs=new ArrayList<>();
		FmoJsonRulesModel fmo=new FmoJsonRulesModel();
		fmo.setItemName("sol");
		fmo.setUdfQuery("$ ..id");
		resultLs.add(fmo);
		itemMap.put(FmoConstants.SITE_TAG, resultLs);
		List<PricePlanDetails> pricePlanDetails=new ArrayList<>();
		PricePlanDetails p=new PricePlanDetails();
		p.setOfferId("11");
		p.setCountryCd("US");
		pricePlanDetails.add(p);
		Map<String,Object> methodInputMap=new HashMap<>();
		methodInputMap.put(FmoConstants.OFFER_ID, 11l);
		methodInputMap.put(FmoConstants.COUNTRY_CD, "US");
		Long offerId=9l;
		fmoProcessingService.createSite(sites, itemMap, pricePlanDetails,
				methodInputMap, offerId);
	}
	
	@Test
	public void createDesignTest() {
		Map<String,List<FmoJsonRulesModel>> itemMap=new HashMap<>();
		List<FmoJsonRulesModel> resultLs=new ArrayList<>();
		FmoJsonRulesModel fmo=new FmoJsonRulesModel();
		fmo.setItemName("sol");
		fmo.setUdfQuery("$ ..id");
		resultLs.add(fmo);
		itemMap.put(FmoConstants.DESIGN_TAG, resultLs);
		List<Port> designSiteOfferPort=new ArrayList<>();
		Port p=new Port();
		designSiteOfferPort.add(p);
		PriceDetails priceDetails=new PriceDetails();
		PricePlanDetails pricePlanDetails=new PricePlanDetails();
		pricePlanDetails.setOfferId("11");
		pricePlanDetails.setCountryCd("US");
		fmoProcessingService.createDesign(designSiteOfferPort, priceDetails, itemMap,pricePlanDetails, 5l, 3l);
	}
	
	@Test
	public void createAccessDetailsTest() {
		Map<String,List<FmoJsonRulesModel>> itemMap=new HashMap<>();
		List<FmoJsonRulesModel> resultLs=new ArrayList<>();
		FmoJsonRulesModel fmo=new FmoJsonRulesModel();
		fmo.setItemName("sol");
		fmo.setUdfQuery("$ ..id");
		resultLs.add(fmo);
		itemMap.put(FmoConstants.ACCESS_DETAILS_TAG, resultLs);
		AccessPriceUIdetails accessPricing=new AccessPriceUIdetails();
		fmoProcessingService.createAccessDetails(accessPricing, itemMap, 5l);
	}
	
	@Test
	public void createPriceDetailsTest() {
		PricePlanDetails pricePlanDetails=new PricePlanDetails();
		pricePlanDetails.setOfferId("11");
		pricePlanDetails.setCountryCd("US");
		Map<String,List<FmoJsonRulesModel>> itemMap=new HashMap<>();
		List<FmoJsonRulesModel> resultLs=new ArrayList<>();
		FmoJsonRulesModel fmo=new FmoJsonRulesModel();
		fmo.setItemName("sol");
		fmo.setUdfQuery("$ ..id");
		resultLs.add(fmo);
		itemMap.put(FmoConstants.PRICE_DETAIL_TAGS, resultLs);
		ComponentDetails componentDetails=new ComponentDetails();
		List<PriceAttributes> priceDetailsLst=new ArrayList<>();
		PriceAttributes pa=new PriceAttributes();
		pa.setBeid("123");
		priceDetailsLst.add(pa);
		componentDetails.setPriceAttributes(priceDetailsLst);
		fmoProcessingService.createPriceDetails(componentDetails, itemMap, pricePlanDetails, 3l, 5l);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getIcbDesiredDiscountTest() {
		PricePlanDetails pricePlanDetailsObj=new PricePlanDetails();
		JSONObject jsonObject=new JSONObject();
		jsonObject.put(FmoConstants.PRODUCT_RATE_ID, "4");
		List<Double> dataLst=new ArrayList<>();
		dataLst.add(12.0);
		dataLst.add(17.0);
		when(jsonPathUtil.search(any(),any(),any())).thenReturn(dataLst);
		fmoProcessingService.getIcbDesiredDiscount(jsonObject, pricePlanDetailsObj);
	}
	
	@Test
	public void getPriceAttributesTest() {
		JSONObject jsonObject=new JSONObject();
		fmoProcessingService.getPriceAttributes(jsonObject, "$..id");
	}
	
	@Test
	public void getDataTest1() {
		fmoProcessingService.getData(new Object(), 12l, 2l, 4l,
				FmoConstants.SALES_UDF_LOOKUP_SOURCE,null);
	}
	
	@Test
	public void getDataTest2() {
		ConcurrentMap<String,String> priceTypeDataMap=new ConcurrentHashMap<>();
		priceTypeDataMap.put("A", "B");
		when(fmoProcessingService.getPriceTypeDataMap()).thenReturn(priceTypeDataMap);
		fmoProcessingService.getData("A", 12l, 2l, 4l,
				FmoConstants.PRICE_TYPE_SOURCE,null);
	}
	
	@Test
	public void getDataTest3() {
		ConcurrentMap<String,String> priceTypeDataMap=new ConcurrentHashMap<>();
		priceTypeDataMap.put("A", "B");
		when(fmoProcessingService.getPriceTypeDataMap()).thenReturn(priceTypeDataMap);
		fmoProcessingService.getData("D", 12l, 2l, 4l,
				FmoConstants.PRICE_TYPE_SOURCE,null);
	}
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void getCountryCodeTest() {
		JSONObject jsonObject=new JSONObject();
		jsonObject.put(FmoConstants.COUNTRY_CD, "US");
		fmoProcessingService.getCountryCode(jsonObject);
	}
	
	@Test
	public void getCompoenetIdTest() {
		List<Long> dataLst=new ArrayList<>();
		dataLst.add(12l);
		dataLst.add(17l);
		when(jsonPathUtil.search(any(),any(),any())).thenReturn(dataLst);
		JSONObject jsonObject=new JSONObject();
		fmoProcessingService.getCompoenetId(jsonObject, "$..id");
	}
	
	@Test
	public void getSiteDataThroughExecutorTest() throws InterruptedException {
		when(fmoProcessingService.getThreadSize()).thenReturn(threadSize);
		when(fmoProcessingService.getExcutorService()).thenReturn(executors);
		List<Callable<Object>> taskList=new ArrayList<>();
		Site site=new Site();
		FmoExecutorService executorObj=new FmoExecutorService(site);
		List<Future<Object>> resultLst =new ArrayList<>();
		Future<Object> fo=null;
		resultLst.add(fo);
		when(executors.invokeAll(any())).thenReturn(resultLst);
		taskList.add(executorObj);
		fmoProcessingService.getSiteDataThroughExecutor(taskList);
	}
	

	@Test
	public void getPricePlanDetailsTest1() {
		List<PricePlanDetails> pricePlanDetails=new ArrayList<>();
		PricePlanDetails p=new PricePlanDetails();
		p.setOfferId("11");
		p.setCountryCd("US");
		pricePlanDetails.add(p);
		Map<String,Object> methodInputMap=new HashMap<>();
		methodInputMap.put(FmoConstants.OFFER_ID, 11l);
		methodInputMap.put(FmoConstants.COUNTRY_CD, "US");
		fmoProcessingService.getPricePlanDetails(pricePlanDetails, methodInputMap);
	}
	
	@Test
	public void getPricePlanDetailsTest2() {
		List<PricePlanDetails> pricePlanDetails=new ArrayList<>();
		Map<String,Object> methodInputMap=new HashMap<>();
		methodInputMap.put(FmoConstants.OFFER_ID, 11l);
		methodInputMap.put(FmoConstants.COUNTRY_CD, "US");
		fmoProcessingService.getPricePlanDetails(pricePlanDetails, methodInputMap);
	}
	
	@Test
	public void getCountryCodeTest2() {
		JSONObject jsonObject=new JSONObject();
		fmoProcessingService.getCountryCode(jsonObject);
	}
	
	
	private RetreiveICBPSPRequest createRequest() {
		RetreiveICBPSPRequest inputJson=new RetreiveICBPSPRequest();
		Solution solution=new Solution();
		List<Offer> offerLst=new ArrayList<>();
		Offer o=new Offer();
		o.setOfferId("11");
		offerLst.add(o);
		solution.setOffers(offerLst);
		inputJson.setSolution(solution);  
		return inputJson;
	}
	
	@Test
	public void testsendDmaapForFailed() {
		Map<String, Object> requestMap= new HashMap<>();
		requestMap.put(MyPriceConstants.NX_TRANSACTION_ID,686L);
		NxRequestDetails reqDetails=new NxRequestDetails();
		NxMpDeal deal =new NxMpDeal();
		when(nxMpDealRepository.findByNxTxnId(anyLong())).thenReturn(deal);
		doNothing().when(myPriceTransactionUtil).sendDmaapEvents(any(),any(),anyString());
		reqDetails.setStatus(30L);
		fmoProcessingService.sendDmaapForFailed(requestMap, reqDetails);
		
		requestMap.put(MyPriceConstants.NX_TRANSACTION_ID, 46546L);
		reqDetails.setStatus(20L);
		fmoProcessingService.sendDmaapForFailed(requestMap, reqDetails);
		
		reqDetails.setStatus(50L);
		fmoProcessingService.sendDmaapForFailed(requestMap, reqDetails);
		
		reqDetails.setStatus(70L);
		fmoProcessingService.sendDmaapForFailed(requestMap, reqDetails);
	}
	
	@Test
	public void testsetStausinNxAuditTbl() {
		Map<String, Object> requestMap= new HashMap<>();
		requestMap.put(MyPriceConstants.NX_AUDIT_ID, 567L);
		NxRequestDetails reqDetails=new NxRequestDetails() ;
		reqDetails.setStatus(70L);
		NxDesignAudit nxDesignAudit= new NxDesignAudit();
		when(nxDesignAuditRepository.findByNxAuditId(anyLong())).thenReturn(nxDesignAudit);
		fmoProcessingService.setStausinNxAuditTbl(requestMap, reqDetails);
	}

	@Test
	public void testcreateFileName() {
		String result=fmoProcessingService.createFileName(56L);
		assertTrue(result.contains("_NexusDataSet_Sheet_"));
	}
	
	@Test
	public void testSetStatus() throws SerialException, SQLException {
		NxOutputFileModel model = new NxOutputFileModel() ;
		JSONObject intermediateJson = new JSONObject() ;
		NxOutputBean nxOutputBean= new NxOutputBean();
	    byte byteArray[] = {20};
		Blob outputFile= new SerialBlob(byteArray) ;
		Map<String, Object> paramMap= new HashMap<>();
		
		//first case
		fmoProcessingService.setStatus(model, null, nxOutputBean, outputFile, paramMap);
		fmoProcessingService.setStatus(model, intermediateJson, nxOutputBean, outputFile, paramMap);

		//second case
		intermediateJson.put("name", "testName");
		model.setMpOutputJson("{\"test\":\"data\"}");
		paramMap.put(FmoConstants.IS_COMPLETE_FALLOUT,true);
		fmoProcessingService.setStatus(model, intermediateJson, nxOutputBean, outputFile, paramMap);
		paramMap.put(FmoConstants.IS_COMPLETE_FALLOUT,false);
		fmoProcessingService.setStatus(model, intermediateJson, nxOutputBean, outputFile, paramMap);
		
		//third case
		model.setFallOutData("fallout happened");
		model.setStatus(CommonConstants.SUCCESS);
		paramMap.remove(FmoConstants.IS_COMPLETE_FALLOUT);
		fmoProcessingService.setStatus(model, intermediateJson, nxOutputBean, outputFile, paramMap);
		model.setStatus(CommonConstants.FAIL);
		fmoProcessingService.setStatus(model, intermediateJson, nxOutputBean, outputFile, paramMap);
	}
	
	@Test
	public void testaccessPricingByPortId() {
		AccessPricingAQ accessPricingAQ= new AccessPricingAQ();
		String path="\"$..id\"";
		fmoProcessingService.accessPricingByPortId(accessPricingAQ, path);
		List<AccessPriceUIdetails> data = new ArrayList<AccessPriceUIdetails>();
		AccessPriceUIdetails accessPriceUIdetails = new AccessPriceUIdetails();
		data.add(accessPriceUIdetails);
		when(jsonPathUtil.search(any(),anyString(),any())).thenReturn(data);
		fmoProcessingService.accessPricingByPortId(accessPricingAQ, path);
	}
	
	@Test
	public void testupdateNexxusOutput() throws SalesBusinessException, SerialException, SQLException {
		NxRequestDetails reqDetails = new NxRequestDetails();
		reqDetails.setNxReqId(1L);
		reqDetails.setNxSolutionDetail(new NxSolutionDetail());
		NxOutputFileModel model= new NxOutputFileModel();
		model.setIntermediateJson("{\"test\":\"data\"}");
		when(repositoryService.getNxOutputFileModel(anyLong())).thenReturn(model);
		NxOutputBean nxOutputJson= new NxOutputBean();
		when(lineItemService.getLineItemData(any(),anyMap())).thenReturn(nxOutputJson);
		when(nexusJsonUtility.convertMapToJson(anyMap())).thenReturn("fallout data");
	    byte byteArray[] = {20};
		Blob outputFile= new SerialBlob(byteArray) ;
		when(reportService.generateReport(any())).thenReturn(outputFile);
		doNothing().when(repositoryService).saveNxOutputFile(any());
		doNothing().when(repositoryService).saveSolutionDetails(any());
		fmoProcessingService.updateNexxusOutput(reqDetails);
	}
	
	@Test
	public void testconvertConcurrentCallType() {
		String result1=fmoProcessingService.convertConcurrentCallType("11");
		assertEquals("VoMIS12", result1);
		
		String result2=fmoProcessingService.convertConcurrentCallType("14");
		assertEquals("VoMIS24", result2);

		String result3=fmoProcessingService.convertConcurrentCallType("28");
		assertEquals("VoMIS48", result3);

		String result4=fmoProcessingService.convertConcurrentCallType("88");
		assertEquals("T3", result4);

	}
	
	@Test
	public void testconvertStringToNumber() {
		Long result=fmoProcessingService.convertStringToNumber("67");
		assertEquals(Long.parseLong("67"), 67);
	}
	
	@Test
	public void getDataTest4() {
		fmoProcessingService.getData(new Object(), 12l, 2l, 4l,
				FmoConstants.IMS2_LOOKUP_SOURCE,null);
	}
		
	@Test
	public void getDataTest5() {
		fmoProcessingService.getData("23", 12l, 2l, 4l,
				FmoConstants.CONCURRENT_CALL_TYPE,null);
	}

	@Test
	public void getDataTest6() {
		fmoProcessingService.getData("23", 12l, 2l, 4l,
				MyPriceConstants.NX_LOOKUP_SOURCE,"test");
	}
}

