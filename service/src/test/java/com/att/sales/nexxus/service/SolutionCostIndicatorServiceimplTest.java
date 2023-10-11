/**
 * 
 */
package com.att.sales.nexxus.service;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.model.constants.HttpErrorCodes;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.dmaap.mr.util.DmaapPublishEventsServiceImpl;
import com.att.sales.nexxus.fallout.service.FalloutDetailsImpl;
import com.att.sales.nexxus.myprice.transaction.service.AutomationFlowHelperService;
import com.att.sales.nexxus.reteriveicb.model.SolutionCostIndicatorResponse;
import com.att.sales.nexxus.transmitdesigndata.model.SolutionCostRequest;

/**
 * @author aa316k
 *
 */
@ExtendWith(MockitoExtension.class)
public class SolutionCostIndicatorServiceimplTest {
	
	
	@InjectMocks
	private SolutionCostIndicatorServiceimpl solutionCostIndicatorServiceimpl;
	
	
	@Mock
	private AutomationFlowHelperService automationFlowHelperService;
	
	@Mock
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;
	
	@Mock
	private DmaapPublishEventsServiceImpl dmaapPublishEventsServiceImpl;

	/**
	 * Test method for {@link com.att.sales.nexxus.service.SolutionCostIndicatorServiceimpl#solutionCostIndicator(com.att.sales.nexxus.transmitdesigndata.model.SolutionCostRequest)}.
	 */
	@Test
	public void testSolutionCostIndicatorNull() {
		SolutionCostRequest request = new SolutionCostRequest();
		SolutionCostIndicatorResponse solutionCostIndicator = solutionCostIndicatorServiceimpl.solutionCostIndicator(request);
		assertNotNull(solutionCostIndicator);
		assertEquals(solutionCostIndicator.getStatus().getCode(),HttpErrorCodes.ERROR.toString());

	}
	
	@Test
	public void testSolutionCostIndicator() {
		SolutionCostRequest request = new SolutionCostRequest();
		request.setSolutionId(12345L);
		request.setSlcIndicator("C");
		
		List<NxSolutionDetail> nxSolnList = new ArrayList<>();
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setFlowType("Auto");
		nxSolutionDetail.setOptyId("12345");
		nxSolutionDetail.setAutomationFlowInd("Y");
		nxSolnList.add(nxSolutionDetail);
		
		Mockito.when(
				nxSolutionDetailsRepository.findByExternalKey(request.getSolutionId())).thenReturn(nxSolnList);
		
		SolutionCostIndicatorResponse solutionCostIndicator = solutionCostIndicatorServiceimpl.solutionCostIndicator(request);
		assertNotNull(solutionCostIndicator);
		assertEquals(solutionCostIndicator.getStatus().getCode(), "200");
		
	}
	
	@Test
	public void testSolutionCostIndicatorY() {
		SolutionCostRequest request = new SolutionCostRequest();
		request.setSolutionId(12345L);
		request.setSlcIndicator("Y");
		
		
		  List<NxSolutionDetail> nxSolnList = new ArrayList<>(); NxSolutionDetail
		  nxSolutionDetail = new NxSolutionDetail();
		  nxSolutionDetail.setFlowType("Auto"); nxSolutionDetail.setOptyId("12345");
		  nxSolutionDetail.setAutomationFlowInd("Y"); nxSolnList.add(nxSolutionDetail);
		  
		  Mockito.when(
		  nxSolutionDetailsRepository.findByExternalKey(request.getSolutionId())).
		  thenReturn(nxSolnList);
		 
		
		SolutionCostIndicatorResponse solutionCostIndicator = solutionCostIndicatorServiceimpl.solutionCostIndicator(request);
		assertNotNull(solutionCostIndicator);
		assertEquals(solutionCostIndicator.getStatus().getCode(), "200");
		
	}

	
	@Test
	public void testSolutionCostIndicatorN() {
		SolutionCostRequest request = new SolutionCostRequest();
		request.setSolutionId(12345L);
		request.setSlcIndicator("N");
		
		
		  List<NxSolutionDetail> nxSolnList = new ArrayList<>(); NxSolutionDetail
		  nxSolutionDetail = new NxSolutionDetail();
		  nxSolutionDetail.setFlowType("Auto"); nxSolutionDetail.setOptyId("12345");
		  nxSolutionDetail.setAutomationFlowInd("Y"); nxSolnList.add(nxSolutionDetail);
		  
		  Mockito.when(
		  nxSolutionDetailsRepository.findByExternalKey(request.getSolutionId())).
		  thenReturn(nxSolnList);
		 
		
		SolutionCostIndicatorResponse solutionCostIndicator = solutionCostIndicatorServiceimpl.solutionCostIndicator(request);
		assertNotNull(solutionCostIndicator);
		assertEquals(solutionCostIndicator.getStatus().getCode(), "200");
		
	}

	/**
	 * Test method for {@link com.att.sales.nexxus.service.SolutionCostIndicatorServiceimpl#setErrorResponse(com.att.sales.framework.model.ServiceResponse, java.lang.String)}.
	 */
	
	@Test
	public void testSetErrorResponseErrCode() {
		ServiceResponse response = new ServiceResponse();
		solutionCostIndicatorServiceimpl.setErrorResponse(response, "M00021");
	}
	

}
