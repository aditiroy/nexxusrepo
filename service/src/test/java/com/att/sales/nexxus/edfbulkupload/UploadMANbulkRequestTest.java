package com.att.sales.nexxus.edfbulkupload;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UploadMANbulkRequestTest {

	@InjectMocks
	UploadMANbulkRequest uploadMANbulkRequest;
	
	@Test
	public void testGetterAndSetter(){
		
		uploadMANbulkRequest.setBeginBillMonth("beginBillMonth");
		assertEquals(new String("beginBillMonth"), uploadMANbulkRequest.getBeginBillMonth());
		
		uploadMANbulkRequest.setBillMonth("billMonth");
		assertEquals(new String("billMonth"), uploadMANbulkRequest.getBillMonth());

		uploadMANbulkRequest.setCpniApprover("cpniApprover");
		assertEquals(new String("cpniApprover"), uploadMANbulkRequest.getCpniApprover());

		uploadMANbulkRequest.setCustomerName("customerName");
		assertEquals(new String("customerName"), uploadMANbulkRequest.getCustomerName());

		uploadMANbulkRequest.setManAccountNumber("manAccountNumber");
		assertEquals(new String("manAccountNumber"), uploadMANbulkRequest.getManAccountNumber());

		uploadMANbulkRequest.setMcnNumber("mcnNumber");
		assertEquals(new String("mcnNumber"), uploadMANbulkRequest.getMcnNumber());

		uploadMANbulkRequest.setProduct("product");
		assertEquals(new String("product"), uploadMANbulkRequest.getProduct());

		uploadMANbulkRequest.setType("type");
		assertEquals(new String("type"), uploadMANbulkRequest.getType());

		uploadMANbulkRequest.setUsageOrNonUsageIndicator("usageOrNonUsageIndicator");
		assertEquals(new String("usageOrNonUsageIndicator"), uploadMANbulkRequest.getUsageOrNonUsageIndicator());

	}
	
}
