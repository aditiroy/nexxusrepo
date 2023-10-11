package com.att.sales.nexxus.prdm.service;

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

import com.att.sales.nexxus.dmaap.mr.util.IDmaapMRSubscriber;
import com.att.sales.nexxus.service.RatePlanDataLoadSeviceImpl;

@ExtendWith(MockitoExtension.class)
public class PrdmMessageConsumptionServiceImplTest {

	@InjectMocks
	 PrdmMessageConsumptionServiceImpl  prdmMessageConsumptionServiceImpl;
	
	@Mock
	private Environment env;

	@Mock
	private IDmaapMRSubscriber dmaapMRSubscriberService;
	
	@Mock
	RatePlanDataLoadSeviceImpl ratePlanDataLoadSeviceImpl;

	@Test
	public void test() {
		List<String> value = new ArrayList<>();
		value.add("add");
		try {
			Mockito.when(dmaapMRSubscriberService.retrieveMessage("TopicName", "GroupName","host")).thenReturn(value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		prdmMessageConsumptionServiceImpl.getMessage();
		
		
		try {
			Mockito.when(dmaapMRSubscriberService.retrieveMessage("TopicName", "GroupName", "host")).thenThrow( new Exception());
			prdmMessageConsumptionServiceImpl.getMessage();
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


}
