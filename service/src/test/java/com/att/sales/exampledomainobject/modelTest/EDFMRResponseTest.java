package com.att.sales.exampledomainobject.modelTest;

import static org.junit.Assert.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.exampledomainobject.model.EDFMRResponse;
@ExtendWith(MockitoExtension.class)
public class EDFMRResponseTest {

	@InjectMocks
	EDFMRResponse eDFMRResponse;
	
	@Test
	public void test() {
		//fail("Not yet implemented");
		
		eDFMRResponse.setApplication("application");
		assertEquals(new String("application"),eDFMRResponse.getApplication());
		
		
		eDFMRResponse.setEndRunTime("endRunTime");
		assertEquals(new String("endRunTime"),eDFMRResponse.getEndRunTime());
		
		eDFMRResponse.setMessage("message");
		assertEquals(new String("message"),eDFMRResponse.getMessage());
		
		eDFMRResponse.setOutputfileName("outputfileName");
		assertEquals(new String("outputfileName"),eDFMRResponse.getOutputfileName());
		
		eDFMRResponse.setPgm("pgm");
		assertEquals(new String("pgm"),eDFMRResponse.getPgm());
		
		eDFMRResponse.setRequestId("requestId");
		assertEquals(new String("requestId"),eDFMRResponse.getRequestId());
		
		eDFMRResponse.setStartRunTime("startRunTime");
		assertEquals(new String("startRunTime"),eDFMRResponse.getStartRunTime());
		
		eDFMRResponse.setUserId("userId");
		assertEquals(new String("userId"),eDFMRResponse.getUserId());
		
		
	}

}
