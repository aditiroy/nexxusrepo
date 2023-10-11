package com.att.sales.nexxus.pddm.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dmaap.mr.util.IDmaapMRSubscriber;
@ExtendWith(MockitoExtension.class)
public class MessageConsumptionServiceImplTest {

	@InjectMocks
	MessageConsumptionServiceImpl test;
	
	@Mock
	NexxusUDFDetailsServiceImpl detailsServiceImpl;
	
	@Mock
	private Environment env;
	
	@Mock
	NxRequestDetailsRepository nxRequestDetailsRepository;

	@Mock
	private IDmaapMRSubscriber dmaapMRSubscriberService;
	
	@Test
	public void test() {
		List<String> value = new ArrayList<>();
		value.add("add");
		try {
			Mockito.when(dmaapMRSubscriberService.retrieveMessage("TopicName", "GroupName", "host")).thenReturn(value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		test.getMessage();
		
		
		try {
			Mockito.when(dmaapMRSubscriberService.retrieveMessage("TopicName", "GroupName","host")).thenThrow( new Exception());
			test.getMessage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Test
	public void testIf() throws IOException, InterruptedException, Exception {
		List<String> value = new ArrayList<>();
		value.add("add");
		NxRequestDetails nxRequestDetails =new NxRequestDetails();
		nxRequestDetails.setFileName("fileName");
		nxRequestDetails.setEdfAckId("edfAckId");
		nxRequestDetails.setFlowType("INR");
		nxRequestDetails.setNxReqId(new Long(2l));
		    Mockito.when(nxRequestDetailsRepository.findByEdfAckId(Mockito.anyString())).thenReturn(nxRequestDetails);
			Mockito.when(dmaapMRSubscriberService.retrieveMessage("TopicName", "GroupName","host")).thenReturn(value);
		
		
		test.getMessage();	  
	}

}
