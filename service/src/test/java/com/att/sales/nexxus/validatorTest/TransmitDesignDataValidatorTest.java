package com.att.sales.nexxus.validatorTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
//import org.mockito.runners.MockitoJUnitRunner;
//package org.mockito.runners;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.TDDConstants;
import com.att.sales.nexxus.transmitdesigndata.model.CircuitDetails;
import com.att.sales.nexxus.transmitdesigndata.model.EndpointDetails;
import com.att.sales.nexxus.transmitdesigndata.model.PortDetails;
import com.att.sales.nexxus.transmitdesigndata.model.SolutionStatus;
import com.att.sales.nexxus.transmitdesigndata.model.TransmitDesignDataRequest;
import com.att.sales.nexxus.validator.TransmitDesignDataValidator;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
public class TransmitDesignDataValidatorTest {
	
	@Spy
	@InjectMocks
	private TransmitDesignDataValidator transmitDesignDataValidator;
	
	
	@Test
	public void transmitDesignDataValidatorTest() throws SalesBusinessException {
		TransmitDesignDataRequest request=new TransmitDesignDataRequest();
		 List<SolutionStatus> solutionStatus=new ArrayList<SolutionStatus>();
		 SolutionStatus s=new SolutionStatus();
		 solutionStatus.add(s);
		 List<CircuitDetails> circuitDetails=new ArrayList<CircuitDetails>();
		 CircuitDetails c=new CircuitDetails();
		 circuitDetails.add(c);
		 List<PortDetails> portDetails=new ArrayList<PortDetails>();
		 PortDetails p=new PortDetails();
		 p.setAsrItemId("112");
		 p.setStatusCode("ytt");
		 portDetails.add(p);
		 s.setCircuitDetails(circuitDetails);
		 s.setPortDetails(portDetails);
		request.setSolutionStatus(solutionStatus);
		//transmitDesignDataValidator.validateUploadRequest(request);
	}
	
	@Test
	public void transmitDesignDataValidatorTest2() throws SalesBusinessException {
		TransmitDesignDataRequest request=new TransmitDesignDataRequest();
		 List<SolutionStatus> solutionStatus=new ArrayList<SolutionStatus>();
		 SolutionStatus s=new SolutionStatus();
		 solutionStatus.add(s);
		 List<CircuitDetails> circuitDetails=new ArrayList<CircuitDetails>();
		 CircuitDetails c=new CircuitDetails();
		 circuitDetails.add(c);
		 List<PortDetails> portDetails=new ArrayList<PortDetails>();
		 PortDetails p=new PortDetails();
		 p.setFailureInd("Y");
		 p.setAsrItemId("112");
		 p.setStatusCode("ytt");
		 portDetails.add(p);
		 s.setCircuitDetails(circuitDetails);
		 s.setPortDetails(portDetails);
		request.setSolutionStatus(solutionStatus);
		//transmitDesignDataValidator.validateUploadRequest(request);
	}
	
	@Test
	public void validateCircuitDetailsTest() {
		 List<CircuitDetails> circuitDetails=new ArrayList<CircuitDetails>();
		 CircuitDetails c=new CircuitDetails();
		 c.setCircuitCancellationReason("abc");
		 List<EndpointDetails> endpointDetails=new ArrayList<EndpointDetails>();
		 EndpointDetails e=new EndpointDetails();
		 endpointDetails.add(e);
		 c.setEndpointDetails(endpointDetails);
		 circuitDetails.add(c);
		 SolutionStatus s=new SolutionStatus();
		 s.setStatusCode("SC");
		 s.setResponseType(TDDConstants.CIRCUIT);
		 List<String> msg=new ArrayList<String>();
		 msg.add("gh");
		transmitDesignDataValidator.validateCircuitDetails(circuitDetails,msg, s);
	}
	
	@Test
	public void validatePortDetailsTest() {
		 List<String> msg=new ArrayList<String>();
		 msg.add("gh");
		 List<PortDetails> portDetails=new ArrayList<PortDetails>();
		 PortDetails p=new PortDetails();
		 p.setFailureInd("Y");
		 portDetails.add(p);
		 SolutionStatus s=new SolutionStatus();
		 s.setStatusCode("SC");
		transmitDesignDataValidator.validatePortDetails(msg, portDetails, s);
	}
	

	@Test
	public void validatePortDetailsTest2() {
		 List<String> msg=new ArrayList<String>();
		 msg.add("gh");
		 List<PortDetails> portDetails=new ArrayList<PortDetails>();
		 PortDetails p=new PortDetails();
		 portDetails.add(p);
		 SolutionStatus s=new SolutionStatus();
		 s.setStatusCode("SC");
		transmitDesignDataValidator.validatePortDetails(msg, portDetails, s);
	}


}
