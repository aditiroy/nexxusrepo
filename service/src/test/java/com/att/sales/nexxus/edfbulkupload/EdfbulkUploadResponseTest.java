package com.att.sales.nexxus.edfbulkupload;

import static org.junit.Assert.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EdfbulkUploadResponseTest {
	@InjectMocks
	EdfbulkUploadResponse edfbulkUploadResponse;
	
	@Test
	public void testGetterAndSetter() {
		edfbulkUploadResponse.setNxSolutionDesc("nxSolutionDesc");
		assertEquals(new String("nxSolutionDesc"),edfbulkUploadResponse.getNxSolutionDesc());

		edfbulkUploadResponse.setNxSolutionId(1L);
		assertEquals(new Long(1L),edfbulkUploadResponse.getNxSolutionId());
	}
	
}