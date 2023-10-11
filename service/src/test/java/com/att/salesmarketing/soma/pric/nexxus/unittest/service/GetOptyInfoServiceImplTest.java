package com.att.salesmarketing.soma.pric.nexxus.unittest.service;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.dao.model.OpportunityTeam;
import com.att.sales.nexxus.handlers.GetOptyInfoWSHandler;
import com.att.sales.nexxus.rome.model.GetOptyRequest;
import com.att.sales.nexxus.rome.model.GetOptyResponse;
import com.att.sales.nexxus.rome.service.GetOptyInfoServiceImpl;


@ExtendWith(MockitoExtension.class)
public class GetOptyInfoServiceImplTest {
	
	@InjectMocks
	GetOptyInfoServiceImpl service = new GetOptyInfoServiceImpl();
	
	@Mock
	GetOptyInfoWSHandler getOptyInfoWSHandler;
		
	@Mock
	OpportunityTeam opportunityTeam; 
	
	@Mock
	GetOptyRequest request;
	
	@Test
	public void test() {
		try {
			GetOptyRequest optyRequest = new GetOptyRequest();
			optyRequest.setAction("createSolution");
			optyRequest.setAttuid("ec006e");
			optyRequest.setOptyId("675589");
			optyRequest.setNxSolutionId(new Long(2));
			optyRequest.setSolutionDescription("description");
			GetOptyResponse optyResp = new GetOptyResponse();			
			Mockito.when(getOptyInfoWSHandler.initiateGetOptyInfoWebService(Mockito.any())).thenReturn(optyResp);
			service.performGetOptyInfo(optyRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
