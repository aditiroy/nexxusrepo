
package com.att.salesmarketing.soma.pric.nexxus.unittest.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.model.Status;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxPdRequestValidation;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.HybridRepositoryService;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxPdRequestValidationRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.myprice.transaction.service.MyPriceTransactionUtil;
import com.att.sales.nexxus.reteriveicb.model.ActionDeterminants;
import com.att.sales.nexxus.reteriveicb.model.Circuit;
import com.att.sales.nexxus.reteriveicb.model.Component;
import com.att.sales.nexxus.reteriveicb.model.ComponentDetails;
import com.att.sales.nexxus.reteriveicb.model.Offer;
import com.att.sales.nexxus.reteriveicb.model.PriceAttributes;
import com.att.sales.nexxus.reteriveicb.model.PriceDetails;
import com.att.sales.nexxus.reteriveicb.model.RetreiveICBPSPRequest;
import com.att.sales.nexxus.reteriveicb.model.RetreiveICBPSPResponse;
import com.att.sales.nexxus.reteriveicb.model.Solution;
import com.att.sales.nexxus.reteriveicb.model.UDFBaseData;
import com.att.sales.nexxus.service.FmoProcessingService;
import com.att.sales.nexxus.service.PedSnsdServiceUtil;
import com.att.sales.nexxus.service.RetreiveICBPSPServiceImpl;
@ExtendWith(MockitoExtension.class)

public class RetreiveICBPSPServiceImplTest extends PedSnsdServiceUtil {

	@InjectMocks
	RetreiveICBPSPServiceImpl service;

	@Mock
	NxSolutionDetailsRepository repository;

	@Mock
	NxRequestDetailsRepository repo;

	@Mock
	private FmoProcessingService fmoProcessingService;

	@Mock
	private NxMpDealRepository nxMpDealRepository;

	@Mock
	NxDesignAuditRepository nxDesignAuditRepository;

	@Mock
	private PedSnsdServiceUtil pedSnsdServiceUtil;

	@Mock
	HybridRepositoryService hybridRepositoryService;

	@Mock
	MyPriceTransactionUtil myPriceTransactionUtil;

	@Mock
	Map<String, Object> createAndUpdateStatusMap;
	
	
	@Mock
	private NxPdRequestValidationRepository nxPdReqValidationRepository;
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(RetreiveICBPSPServiceImplTest.class);

	@BeforeEach
	public void setUp() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ServiceMetaData.VERSION, "1.0");
		map.put(ServiceMetaData.METHOD, "GET");
		map.put(ServiceMetaData.URI, "/services/hello1");
		map.put(ServiceMetaData.REST_REQUEST_START_TIME, System.currentTimeMillis());
		map.put(ServiceMetaData.MS_REQUEST_START_TIME, System.currentTimeMillis());
		ServiceMetaData.add(map);
	}

	
	 @Test 	
	public void testretreiveICBPSPWithoutSolution()  {

		RetreiveICBPSPRequest request = createRequestObject();
		Solution solution = new Solution();
		solution.setAutomationInd("Y");
		solution.setStandardPricingInd("N");
		NxSolutionDetail solnData = new NxSolutionDetail();
		solnData.setExternalKey(11111L);
		List<NxSolutionDetail> solnList = new ArrayList<>();
		solnList.add(solnData);
		Mockito.when(repository.findByExternalKey(Mockito.anyLong())).thenReturn(solnList);
		List<NxMpDeal> nxMpDeals = new ArrayList<>();
		NxMpDeal nxMpDeal = new NxMpDeal();
		nxMpDeal.setNxTxnId(1L);
		nxMpDeal.setAction("action");
		nxMpDeals.add(nxMpDeal);
		Mockito.when(
				nxMpDealRepository.findBySolutionIdAndActiveYN(solnData.getNxSolutionId(), CommonConstants.ACTIVE_Y))
				.thenReturn(nxMpDeals);
		NxDesignAudit nxDesignAudit = new NxDesignAudit();
		nxDesignAudit.setTransaction("MYPRICE_CREATE");
		nxDesignAudit.setStatus("FAILURE");
		Mockito.when(nxDesignAuditRepository.findByNxRefIdAndTransaction(solnData.getNxSolutionId(),
				MyPriceConstants.AUDIT_CREATE)).thenReturn(nxDesignAudit);
		Boolean statusValue = true;
		Map<String, Object> createAndUpdateStatus = new HashMap<String, Object>();
		createAndUpdateStatus.put(MyPriceConstants.AUTOMATION_IND, "Y");
		Map<String, Object> createAndUpdateMap = new HashMap<String, Object>();
		createAndUpdateMap.put("status", statusValue);
		Long nxTxnId = 1L;
		Mockito.when(myPriceTransactionUtil.createAndUpdateTransc(any(), any(), any(), anyLong()))
				.thenReturn(createAndUpdateMap);
		Mockito.when(pedSnsdServiceUtil.saveDesignData(any(), any(), any(), anyMap())).thenReturn(statusValue);
		service.retreiveICBPSP(request);
		RetreiveICBPSPResponse response = new RetreiveICBPSPResponse();
		Status status = new Status();
		response.setStatus(status);
		assertNotNull(response);

	}

	@Test
	public void testretreiveICBPSPWithAutomationtSolution()  {

		RetreiveICBPSPRequest request = createRequestObject();
		List<ActionDeterminants> actionDeterminantsList = new ArrayList<>();
		ActionDeterminants actionDeterminants = new ActionDeterminants();
		actionDeterminants.setActivity("SubmitDesigntoNSS");
		List<String> component = new ArrayList<>();
		component.add("Design");
		component.add("Price");
		component.add("ASEoD");
		actionDeterminants.setComponent(component);
		actionDeterminantsList.add(actionDeterminants);
		Solution solution = new Solution();
		solution.setAutomationInd("Y");
		solution.setPricerDSolutionId(1L);
		solution.setStandardPricingInd("N");
		fetchAllValidation("ASEoD");
		List<Offer> offers = new ArrayList<>();
		Offer offer = new Offer();
		offer.setOfferId("6");
		offers.add(offer);
		solution.setOffers(offers);
		request.setActionDeterminants(actionDeterminantsList);
		request.setSolution(solution);
		NxSolutionDetail solnData = new NxSolutionDetail();
		solnData.setExternalKey(11111L);
		List<NxSolutionDetail> solnList = new ArrayList<>();
		solnList.add(solnData);
		Mockito.when(repository.findByExternalKey(Mockito.anyLong())).thenReturn(solnList);
		List<NxMpDeal> nxMpDeals = new ArrayList<>();
		NxMpDeal nxMpDeal = new NxMpDeal();
		nxMpDeal.setNxTxnId(1L);
		nxMpDeal.setAction("action");
		nxMpDeals.add(nxMpDeal);
		Mockito.when(
				nxMpDealRepository.findBySolutionIdAndActiveYN(solnData.getNxSolutionId(), CommonConstants.ACTIVE_Y))
				.thenReturn(nxMpDeals);
		NxDesignAudit nxDesignAudit = new NxDesignAudit();
		nxDesignAudit.setTransaction("MYPRICE_CREATE");
		nxDesignAudit.setStatus("FAILURE");
		Mockito.when(nxDesignAuditRepository.findByNxRefIdAndTransaction(solnData.getNxSolutionId(),
				MyPriceConstants.AUDIT_CREATE)).thenReturn(nxDesignAudit);
		Mockito.when(nxDesignAuditRepository.findByNxRefIdAndTransaction(solnData.getNxSolutionId(),
				com.att.sales.nexxus.constant.CommonConstants.SOLUTION_DATA)).thenReturn(nxDesignAudit);
		Boolean statusValue = true;
		Map<String, Object> createAndUpdateStatus = new HashMap<String, Object>();
		createAndUpdateStatus.put(MyPriceConstants.AUTOMATION_IND, "Y");
		Map<String, Object> createAndUpdateMap = new HashMap<String, Object>();
		createAndUpdateMap.put("status", statusValue);
		Long nxTxnId = 1L;
		Mockito.when(myPriceTransactionUtil.createAndUpdateTransc(any(), any(), any(), anyLong()))
				.thenReturn(createAndUpdateMap);
		Mockito.when(pedSnsdServiceUtil.saveDesignData(any(), any(), any(), anyMap())).thenReturn(statusValue);
			service.retreiveICBPSP(request);
		RetreiveICBPSPResponse response = new RetreiveICBPSPResponse();
		Status status = new Status();
		response.setStatus(status);
		assertNotNull(response);

	}
	
	@Test
	public void testretreiveICBPSPWithoutAutomationtSolution()  {

		RetreiveICBPSPRequest request = createRequestObject();
		List<ActionDeterminants> actionDeterminantsList = new ArrayList<>();
		ActionDeterminants actionDeterminants = new ActionDeterminants();
		actionDeterminants.setActivity("SubmitDesigntoNSS");
		List<String> component = new ArrayList<>();
		component.add("Design");
		component.add("Price");
		component.add("ASEoD");
		actionDeterminants.setComponent(component);
		actionDeterminantsList.add(actionDeterminants);
		Solution solution = new Solution();
		solution.setAutomationInd("N");
		solution.setPricerDSolutionId(1L);
		solution.setStandardPricingInd("N");
		fetchAllValidation("ASEoD");
		List<Offer> offers = new ArrayList<>();
		Offer offer = new Offer();
		offer.setOfferId("6");
		offers.add(offer);
		solution.setOffers(offers);
		request.setActionDeterminants(actionDeterminantsList);
		request.setSolution(solution);
		NxSolutionDetail solnData = new NxSolutionDetail();
		solnData.setExternalKey(11111L);
		List<NxSolutionDetail> solnList = new ArrayList<>();
		solnList.add(solnData);
		Mockito.when(repository.findByExternalKey(Mockito.anyLong())).thenReturn(solnList);
		List<NxMpDeal> nxMpDeals = new ArrayList<>();
		NxMpDeal nxMpDeal = new NxMpDeal();
		nxMpDeal.setNxTxnId(1L);
		nxMpDeal.setAction("action");
		nxMpDeals.add(nxMpDeal);
		Mockito.when(
				nxMpDealRepository.findBySolutionIdAndActiveYN(solnData.getNxSolutionId(), CommonConstants.ACTIVE_Y))
				.thenReturn(nxMpDeals);
		NxDesignAudit nxDesignAudit = new NxDesignAudit();
		nxDesignAudit.setTransaction("MYPRICE_CREATE");
		nxDesignAudit.setStatus("FAILURE");
		Mockito.when(nxDesignAuditRepository.findByNxRefIdAndTransaction(solnData.getNxSolutionId(),
				MyPriceConstants.AUDIT_CREATE)).thenReturn(nxDesignAudit);
		Mockito.when(nxDesignAuditRepository.findByNxRefIdAndTransaction(solnData.getNxSolutionId(),
				com.att.sales.nexxus.constant.CommonConstants.SOLUTION_DATA)).thenReturn(nxDesignAudit);
		Boolean statusValue = true;
		Map<String, Object> createAndUpdateStatus = new HashMap<String, Object>();
		createAndUpdateStatus.put(MyPriceConstants.AUTOMATION_IND, "Y");
		Map<String, Object> createAndUpdateMap = new HashMap<String, Object>();
		createAndUpdateMap.put("status", statusValue);
		Long nxTxnId = 1L;
		Mockito.when(myPriceTransactionUtil.createAndUpdateTransc(any(), any(), any(), anyLong()))
				.thenReturn(createAndUpdateMap);
		Mockito.when(pedSnsdServiceUtil.saveDesignData(any(), any(), any(), anyMap())).thenReturn(statusValue);
			service.retreiveICBPSP(request);
		RetreiveICBPSPResponse response = new RetreiveICBPSPResponse();
		Status status = new Status();
		response.setStatus(status);
		assertNotNull(response);

	}
	@Test
	public void testretreiveICBPSPWithSolution()  {
		RetreiveICBPSPRequest request = createRequestObject();
		List<ActionDeterminants> actionDeterminantsList = new ArrayList<>();
		ActionDeterminants actionDeterminants = new ActionDeterminants();
		actionDeterminants.setActivity("SubmitDesigntoNSS");
		List<String> component = new ArrayList<>();
		component.add("Design");
		component.add("Price");
		component.add("ASEoD");
		actionDeterminants.setComponent(component);
		actionDeterminantsList.add(actionDeterminants);
		Solution solution = new Solution();
		solution.setAutomationInd("Y");
		solution.setPricerDSolutionId(1L);
		solution.setStandardPricingInd("N");
		solution.setSolutionId(1l);
		fetchAllValidation("ASEoD");
		List<Offer> offers = new ArrayList<>();
		Offer offer = new Offer();
		offer.setOfferId("6");
		offers.add(offer);
		solution.setOffers(offers);
		request.setActionDeterminants(actionDeterminantsList);
		request.setSolution(solution);
		NxSolutionDetail solnData = new NxSolutionDetail();
		solnData.setExternalKey(11111L);
		List<NxSolutionDetail> solnList = new ArrayList<>();
		solnList.add(solnData);
		Mockito.when(repository.findByExternalKey(Mockito.anyLong())).thenReturn(solnList);
		List<NxMpDeal> nxMpDeals = new ArrayList<>();
		NxMpDeal nxMpDeal = new NxMpDeal();
		nxMpDeal.setNxTxnId(1L);
		nxMpDeal.setAction("action");
		nxMpDeal.setNxMpStatusInd("N");
		nxMpDeals.add(nxMpDeal);
		Mockito.when(nxMpDealRepository.getActivePricerDDeals(solnData.getNxSolutionId(), CommonConstants.ACTIVE_Y))
				.thenReturn(nxMpDeals);
		NxDesignAudit nxDesignAudit = new NxDesignAudit();
		nxDesignAudit.setTransaction("MYPRICE_CREATE");
		nxDesignAudit.setStatus("Reconfigure");
		Mockito.when(nxDesignAuditRepository.findByNxRefIdAndTransaction(solnData.getNxSolutionId(),
				MyPriceConstants.AUDIT_CREATE)).thenReturn(nxDesignAudit);
		Mockito.when(nxDesignAuditRepository.findByNxRefIdAndTransaction(solnData.getNxSolutionId(), MyPriceConstants.AUDIT_UPDATE_CS)).thenReturn(nxDesignAudit);
		Mockito.when(nxDesignAuditRepository.findByNxRefIdAndTransaction(solnData.getNxSolutionId(),
				com.att.sales.nexxus.constant.CommonConstants.SOLUTION_DATA)).thenReturn(nxDesignAudit);
		Boolean statusValue = true;
		Map<String, Object> createAndUpdateStatus = new HashMap<String, Object>();
		createAndUpdateStatus.put(MyPriceConstants.AUTOMATION_IND, "Y");
		Map<String, Object> createAndUpdateMap = new HashMap<String, Object>();
		createAndUpdateMap.put("status", statusValue);
		Long nxTxnId = 1L;
		Mockito.when(myPriceTransactionUtil.createAndUpdateTransc(any(), any(), any(), anyLong()))
				.thenReturn(createAndUpdateMap);
		Mockito.when(pedSnsdServiceUtil.saveDesignData(any(), any(), any(), anyMap())).thenReturn(statusValue);
		service.retreiveICBPSP(request);
		RetreiveICBPSPResponse response = new RetreiveICBPSPResponse();
		Status status = new Status();
		response.setStatus(status);
		assertNotNull(response);

	}

	
	@Test
	public void testretreiveICBPSPReconfigure() {
		try {
		RetreiveICBPSPRequest request = createRequestObject();
		List<ActionDeterminants> actionDeterminantsList = new ArrayList<>();
		ActionDeterminants actionDeterminants = new ActionDeterminants();
		actionDeterminants.setActivity("SubmitDesigntoNSS");
		List<String> component = new ArrayList<>();
		component.add("Design");
		component.add("Price");
		component.add("ASEoD");
		actionDeterminants.setComponent(component);
		actionDeterminantsList.add(actionDeterminants);
		Solution solution = new Solution();
		solution.setAutomationInd("Y");
		solution.setPricerDSolutionId(1L);
		solution.setStandardPricingInd("N");
		fetchAllValidation("ASEoD");
		List<Offer> offers = new ArrayList<>();
		Offer offer = new Offer();
		offer.setOfferId("6");
		offers.add(offer);
		solution.setOffers(offers);
		request.setActionDeterminants(actionDeterminantsList);
		request.setSolution(solution);
		NxSolutionDetail solnData = new NxSolutionDetail();
		solnData.setExternalKey(11111L);
		List<NxSolutionDetail> solnList = new ArrayList<>();
		solnList.add(solnData);
		Mockito.when(repository.findByExternalKey(Mockito.anyLong())).thenReturn(solnList);
		List<NxMpDeal> nxMpDeals = new ArrayList<>();
		NxMpDeal nxMpDeal = new NxMpDeal();
		nxMpDeal.setNxTxnId(1L);
		nxMpDeal.setAction("action");
		nxMpDeal.setNxMpStatusInd("N");
		nxMpDeals.add(nxMpDeal);
		Mockito.when(nxMpDealRepository.getActivePricerDDeals(solnData.getNxSolutionId(), CommonConstants.ACTIVE_Y))
				.thenReturn(nxMpDeals);
		NxDesignAudit nxDesignAudit = new NxDesignAudit();
		nxDesignAudit.setTransaction("MYPRICE_CREATE");
		nxDesignAudit.setStatus("Reconfigure");
		Mockito.when(nxDesignAuditRepository.findByNxRefIdAndTransaction(solnData.getNxSolutionId(),
				MyPriceConstants.AUDIT_CREATE)).thenReturn(nxDesignAudit);
		Mockito.when(nxDesignAuditRepository.findByNxRefIdAndTransaction(solnData.getNxSolutionId(), MyPriceConstants.AUDIT_UPDATE_CS)).thenReturn(nxDesignAudit);
		Mockito.when(nxDesignAuditRepository.findByNxRefIdAndTransaction(solnData.getNxSolutionId(),
				com.att.sales.nexxus.constant.CommonConstants.SOLUTION_DATA)).thenReturn(nxDesignAudit);
		Boolean statusValue = true;
		Map<String, Object> createAndUpdateStatus = new HashMap<String, Object>();
		createAndUpdateStatus.put(MyPriceConstants.AUTOMATION_IND, "Y");
		Map<String, Object> createAndUpdateMap = new HashMap<String, Object>();
		createAndUpdateMap.put("status", statusValue);
		Long nxTxnId = 1L;
		Mockito.when(myPriceTransactionUtil.createAndUpdateTransc(any(), any(), any(), anyLong()))
				.thenReturn(createAndUpdateMap);
		Mockito.when(pedSnsdServiceUtil.saveDesignData(any(), any(), any(), anyMap())).thenReturn(statusValue);
		service.retreiveICBPSP(request);
		RetreiveICBPSPResponse response = new RetreiveICBPSPResponse();
		Status status = new Status();
		response.setStatus(status);
		assertNotNull(response);
		}catch(Exception e) {
			e.printStackTrace();
		}

	}

	 	@Test 	
		public void testretreiveICBPSPWithoffer()  {

			RetreiveICBPSPRequest request = createRequestObject();
			Solution solution = new Solution();
			List<Offer> offers = new ArrayList<>();
			Offer offer = new Offer();
			offer.setOfferId("6");
			offers.add(offer);
			solution.setOffers(offers);
			solution.setStandardPricingInd("N");
			request.setSolution(solution);
			NxSolutionDetail solnData = new NxSolutionDetail();
			solnData.setExternalKey(11111L);
			List<NxSolutionDetail> solnList = new ArrayList<>();
			solnList.add(solnData);
			Mockito.when(repository.findByExternalKey(Mockito.anyLong())).thenReturn(solnList);
			fetchAllValidation("ASEoD");
			List<NxMpDeal> nxMpDeals = new ArrayList<>();
			NxMpDeal nxMpDeal = new NxMpDeal();
			nxMpDeal.setNxTxnId(1L);
			nxMpDeal.setAction("action");
			nxMpDeals.add(nxMpDeal);
			Mockito.when(
					nxMpDealRepository.findBySolutionIdAndActiveYN(solnData.getNxSolutionId(), CommonConstants.ACTIVE_Y))
					.thenReturn(nxMpDeals);
			NxDesignAudit nxDesignAudit = new NxDesignAudit();
			nxDesignAudit.setTransaction("MYPRICE_CREATE");
			nxDesignAudit.setStatus("FAILURE");
			Mockito.when(nxDesignAuditRepository.findByNxRefIdAndTransaction(solnData.getNxSolutionId(),
					MyPriceConstants.AUDIT_CREATE)).thenReturn(nxDesignAudit);
			Boolean statusValue = true;
			Map<String, Object> createAndUpdateStatus = new HashMap<String, Object>();
			createAndUpdateStatus.put(MyPriceConstants.AUTOMATION_IND, "Y");
			Map<String, Object> createAndUpdateMap = new HashMap<String, Object>();
			createAndUpdateMap.put("status", statusValue);
			Long nxTxnId = 1L;
			Mockito.when(myPriceTransactionUtil.createAndUpdateTransc(any(), any(), any(), anyLong()))
					.thenReturn(createAndUpdateMap);
			Mockito.when(pedSnsdServiceUtil.saveDesignData(any(), any(), any(), anyMap())).thenReturn(statusValue);
				service.retreiveICBPSP(request);
			RetreiveICBPSPResponse response = new RetreiveICBPSPResponse();
			Status status = new Status();
			response.setStatus(status);
			assertNotNull(response);

		}
	
	
	@Test
	public void testretreiveICBPSPWithoutLocalKey()  {

		RetreiveICBPSPRequest request = createRequestObject();
		List<ActionDeterminants> actionDeterminantsList = new ArrayList<>();
		ActionDeterminants actionDeterminants = new ActionDeterminants();
		actionDeterminants.setActivity("SubmitDesigntoNSS");
		List<String> component = new ArrayList<>();
		component.add("Design");
		component.add("Price");
		component.add("ASEoD");
		actionDeterminants.setComponent(component);
		actionDeterminantsList.add(actionDeterminants);
		Solution solution = new Solution();
		solution.setAutomationInd("Y");
		solution.setPricerDSolutionId(1L);
		solution.setStandardPricingInd("Y");
		solution.setBundleCode("ADE");
		fetchAllValidation("ASEoD");
		List<Offer> offers = new ArrayList<>();
		Offer offer = new Offer();
		offer.setOfferId("6");
		offers.add(offer);
		solution.setOffers(offers);
		request.setActionDeterminants(actionDeterminantsList);
		request.setSolution(solution);
		NxSolutionDetail solnData = new NxSolutionDetail();
		solnData.setExternalKey(11111L);
		List<NxSolutionDetail> solnList = new ArrayList<>();
		solnList.add(solnData);
		Mockito.when(repository.findByExternalKey(Mockito.anyLong())).thenReturn(solnList);
					service.retreiveICBPSP(request);
		RetreiveICBPSPResponse response = new RetreiveICBPSPResponse();
		Status status = new Status();
		response.setStatus(status);
		assertNotNull(response);	}

	@Disabled

	@Test
	public void testretreiveICBPSPWithoutExtKey()  {
		RetreiveICBPSPRequest request = createRequestObject();
		NxSolutionDetail solnData = new NxSolutionDetail();
		request.getSolution().setExternalKey(null);
		Mockito.when(repository.findByExternalKey(Mockito.anyLong())).thenReturn(null);
			service.retreiveICBPSP(request);
		RetreiveICBPSPResponse response = new RetreiveICBPSPResponse();
		Status status = new Status();
		response.setStatus(status);
		assertNotNull(response);
	}

	private RetreiveICBPSPRequest createRequestObject() {
		RetreiveICBPSPRequest request = new RetreiveICBPSPRequest();
		Solution soln = new Solution();
		soln.setOptyId("1=TEST");
		soln.setBulkInd("Y");
		soln.setExternalKey(12345L);
		soln.setUserId("aa316k");
		soln.setStandardPricingInd("N");
		request.setSolution(soln);
		return request;
	}
	@Disabled
	@Test
	public void testretreiveICBPSPWithoutSolutionStatus() {
		RetreiveICBPSPRequest request = new RetreiveICBPSPRequest();
		Solution soln = new Solution();
		soln.setOptyId("1=TEST");
		soln.setBulkInd("Y");
		soln.setExternalKey(12345L);
		soln.setUserId("aa316k");
		soln.setStandardPricingInd("N");
		request.setSolution(soln);
		ServiceResponse response = service.retreiveICBPSP(request);
		assertNotNull(response);
	}
	@Test 	
	public void testRetreiveICBPSPStandardPricingIndicatorASY()  {

		RetreiveICBPSPRequest request = createRequestObject();
		List<ActionDeterminants> actionDeterminantsList = new ArrayList<>();
		ActionDeterminants actionDeterminants = new ActionDeterminants();
		actionDeterminants.setActivity("SubmitDesigntoNSS");
		List<String> component = new ArrayList<>();
		component.add("Design");
		component.add("Price");
		component.add("ASEoD");
		actionDeterminants.setComponent(component);
		actionDeterminantsList.add(actionDeterminants);
		Solution solution = new Solution();
		solution.setAutomationInd("Y");
		solution.setPricerDSolutionId(1L);
		solution.setStandardPricingInd("Y");
		solution.setBundleCode("ADE");
		fetchAllValidation("ASEoD");
		List<Offer> offers = new ArrayList<>();
		Offer offer = new Offer();
		offer.setOfferId("6");
		offers.add(offer);
		solution.setOffers(offers);
		request.setActionDeterminants(actionDeterminantsList);
		request.setSolution(solution);
		NxSolutionDetail solnData = new NxSolutionDetail();
		solnData.setExternalKey(11111L);
		List<NxSolutionDetail> solnList = new ArrayList<>();
		solnList.add(solnData);
		Mockito.when(repository.findByExternalKey(Mockito.anyLong())).thenReturn(solnList);
					service.retreiveICBPSP(request);
		RetreiveICBPSPResponse response = new RetreiveICBPSPResponse();
		Status status = new Status();
		response.setStatus(status);
		assertNotNull(response);

	}
 	@Test 	
	public void testRetreiveICBPSPStandardPricingIndicatorASN()  {
		RetreiveICBPSPRequest request = createRequestObject();
		Solution solution = new Solution();
		List<Offer> offers = new ArrayList<>();
		Offer offer = new Offer();
		offer.setOfferId("6");
		offers.add(offer);
		solution.setOffers(offers);
		solution.setStandardPricingInd("N");
		solution.setSolutionStatus("U");
	 	request.setSolution(solution);
		NxSolutionDetail solnData = new NxSolutionDetail();
		solnData.setExternalKey(11111L);
		List<NxSolutionDetail> solnList = new ArrayList<>();
		solnList.add(solnData);
		List<NxPdRequestValidation> validations=new ArrayList<NxPdRequestValidation>();
		NxPdRequestValidation validation=new NxPdRequestValidation();
		validation.setActive("Y");
		validation.setDataType("String");
		validation.setErrorMsg("solutionStatus is missing. Please verify the request");
		validation.setField("Solution Status");
		validation.setJsonPath("$.solution.solutionStatus");
		validation.setPdReqValidationId(1);
		validation.setProduct("All");
		validation.setSubJsonPath("");
		validation.setValidationOrder(1);
		validations.add(validation);
		NxPdRequestValidation validation1=new NxPdRequestValidation();
		validation1.setActive("Y");
		validation1.setDataType("Array");
		validation1.setErrorMsg("Atleast one offer is required");
		validation1.setField("Offer");
		validation1.setJsonPath("$.solution.offers[*]");
		validation1.setPdReqValidationId(2);
		validation1.setProduct("All");
		validation1.setSubJsonPath("");
		validation1.setValidationOrder(2);
		validations.add(validation1);
		String product = request.getSolution().getBundleCode();
		Mockito.when(nxPdReqValidationRepository.fetchAllValidation(product)).thenReturn(validations);

		Mockito.when(repository.findByExternalKey(Mockito.anyLong())).thenReturn(solnList);
		List<NxMpDeal> nxMpDeals = new ArrayList<>();
		fetchAllValidation("ASEoD");
		NxMpDeal nxMpDeal = new NxMpDeal();
		nxMpDeal.setNxTxnId(1L);
		nxMpDeal.setAction("action");
		nxMpDeals.add(nxMpDeal);
		Mockito.when(
				nxMpDealRepository.findBySolutionIdAndActiveYN(solnData.getNxSolutionId(), CommonConstants.ACTIVE_Y))
				.thenReturn(nxMpDeals);
		NxDesignAudit nxDesignAudit = new NxDesignAudit();
		nxDesignAudit.setTransaction("MYPRICE_CREATE");
		nxDesignAudit.setStatus("FAILURE");
		Mockito.when(nxDesignAuditRepository.findByNxRefIdAndTransaction(solnData.getNxSolutionId(),
				MyPriceConstants.AUDIT_CREATE)).thenReturn(nxDesignAudit);
		Boolean statusValue = true;
		Map<String, Object> createAndUpdateStatus = new HashMap<String, Object>();
		createAndUpdateStatus.put(MyPriceConstants.AUTOMATION_IND, "Y");
		Map<String, Object> createAndUpdateMap = new HashMap<String, Object>();
		createAndUpdateMap.put("status", statusValue);
		Long nxTxnId = 1L;
		Mockito.when(myPriceTransactionUtil.createAndUpdateTransc(any(), any(), any(), anyLong()))
				.thenReturn(createAndUpdateMap);
		Mockito.when(pedSnsdServiceUtil.saveDesignData(any(), any(), any(), anyMap())).thenReturn(statusValue);
			service.retreiveICBPSP(request);
		RetreiveICBPSPResponse response = new RetreiveICBPSPResponse();
		Status status = new Status();
		response.setStatus(status);
		assertNotNull(response);

	}
 	@Disabled
 	@Test 	
	public void testRetreiveICBPSPAutomationIndAsN()  {
		RetreiveICBPSPRequest request = createRequestObject();
		Solution solution = new Solution();
		List<Offer> offers = new ArrayList<>();
		Offer offer = new Offer();
		offer.setOfferId("6");
		List<Circuit> circuit=new ArrayList<>();
		Circuit circuit1=new Circuit();
		circuit1.setDesignStatus("N");
		List<Component> component=new ArrayList<Component>();
		Component component1=new Component();
		component1.setComponentCodeId(1210l);
		List<UDFBaseData> designDetails=new ArrayList<UDFBaseData>();
		UDFBaseData designDetail=new UDFBaseData();
		designDetail.setUdfId(200182);
		List<String> attributes=new ArrayList<>();
		attributes.add("N/A");
		designDetail.setUdfAttributeText(attributes);
		designDetails.add(designDetail);
		component1.setDesignDetails(designDetails);
		component.add(component1);
		circuit1.setComponent(component);
		PriceDetails priceDetails=new PriceDetails();
		List<ComponentDetails> componentDetails=new ArrayList<>();
		ComponentDetails componentDetail=new ComponentDetails();
		List<PriceAttributes> priceAttributes=new ArrayList<PriceAttributes>();
		PriceAttributes priceAttribute=new PriceAttributes();
		priceAttribute.setProductRateId(01l);
		priceAttribute.setChargeCodeId("");
		priceAttribute.setLocalListPrice(600d);
		priceAttributes.add(priceAttribute);
		componentDetail.setPriceAttributes(priceAttributes);
		componentDetails.add(componentDetail);
		priceDetails.setComponentDetails(componentDetails);
		circuit1.setPriceDetails(priceDetails);
		circuit.add(circuit1);
		offer.setCircuit(circuit);
		offers.add(offer);
		solution.setOffers(offers);
		solution.setStandardPricingInd("Y");
		solution.setAutomationInd("N");
	 	request.setSolution(solution);
		NxSolutionDetail solnData = new NxSolutionDetail();
		solnData.setExternalKey(null);
		List<NxSolutionDetail> solnList = new ArrayList<>();
		solnList.add(solnData);
		List<NxPdRequestValidation> validations=new ArrayList<NxPdRequestValidation>();
		NxPdRequestValidation validation2=new NxPdRequestValidation();
		validation2.setActive("Y");
		validation2.setDataType("Array");
		validation2.setErrorMsg("Circuit or designStatus, asrItemId or priceAttributes for each site might be missing. Please verify the request");
		validation2.setField("Circuit");
		validation2.setJsonPath("$.solution.offers[*].circuit[*]");
		validation2.setPdReqValidationId(4);
		validation2.setProduct("ADE	");
		validation2.setSubJsonPath("{\"designStatus\":{\"path\": \"$.solution.offers[*].circuit[*].[?(@.designStatus != null &&@.designStatus != '')].designStatus},\"udfasrItemId\": {\"path\": \"$..component.[?(@.componentCodeId==1210)].designDetails.[?(@.udfId==200162)].udfAttributeText.[*]}, \"priceAttributes\": { \"path\": \"$.priceDetails.componentDetails[*].priceAttributes[*]\",\"dataType\": \"Array\"} }");
		validation2.setValidationOrder(3);
		validations.add(validation2);
		String product = request.getSolution().getBundleCode();
		Mockito.when(nxPdReqValidationRepository.fetchAllValidation(product)).thenReturn(validations);
		
		Mockito.when(repository.findByExternalKey(Mockito.anyLong())).thenReturn(solnList);
		List<NxMpDeal> nxMpDeals = new ArrayList<>();
		fetchAllValidation("ASEoD");
		NxMpDeal nxMpDeal = new NxMpDeal();
		nxMpDeal.setNxTxnId(1L);
		nxMpDeal.setAction("action");
		nxMpDeals.add(nxMpDeal);
		Mockito.when(
				nxMpDealRepository.findBySolutionIdAndActiveYN(solnData.getNxSolutionId(), CommonConstants.ACTIVE_Y))
				.thenReturn(nxMpDeals);
		NxDesignAudit nxDesignAudit = new NxDesignAudit();
		nxDesignAudit.setTransaction("MYPRICE_CREATE");
		nxDesignAudit.setStatus("FAILURE");
		Mockito.when(nxDesignAuditRepository.findByNxRefIdAndTransaction(solnData.getNxSolutionId(),
				MyPriceConstants.AUDIT_CREATE)).thenReturn(nxDesignAudit);
		Boolean statusValue = true;
		Map<String, Object> createAndUpdateStatus = new HashMap<String, Object>();
		createAndUpdateStatus.put(MyPriceConstants.AUTOMATION_IND, "N");
		Map<String, Object> createAndUpdateMap = new HashMap<String, Object>();
		createAndUpdateMap.put("status", statusValue);
		Long nxTxnId = 1L;
		Mockito.when(myPriceTransactionUtil.createAndUpdateTransc(any(), any(), any(), anyLong()))
				.thenReturn(createAndUpdateMap);
		Mockito.when(pedSnsdServiceUtil.saveDesignData(any(), any(), any(), anyMap())).thenReturn(statusValue);
		service.retreiveICBPSP(request);
		RetreiveICBPSPResponse response = new RetreiveICBPSPResponse();
		Status status = new Status();
		response.setStatus(status);
		assertNotNull(response);

	}
	public void fetchAllValidation(String product)  {
		List<NxPdRequestValidation> response = nxPdReqValidationRepository.fetchAllValidation(product);
		assertNotNull(response);
	}
	
	
}
