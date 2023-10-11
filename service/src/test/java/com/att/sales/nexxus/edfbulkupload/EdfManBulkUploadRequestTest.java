package com.att.sales.nexxus.edfbulkupload;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
@ExtendWith(MockitoExtension.class)
public class EdfManBulkUploadRequestTest {
	@InjectMocks
	EdfManBulkUploadRequest edfManBulkUploadRequest;
	
	@Test
	public void testGetterAndSetter(){
	
		edfManBulkUploadRequest.setFileName("fileName");
		assertEquals(new String("fileName"), edfManBulkUploadRequest.getFileName());
		
		InputStream stubInputstream = new ByteArrayInputStream("testdata".getBytes());
		edfManBulkUploadRequest.setInputStream(stubInputstream);
		assertSame(stubInputstream, edfManBulkUploadRequest.getInputStream());
		
		edfManBulkUploadRequest.setNxSolutionId(1L);
		assertEquals(new Long(1L), edfManBulkUploadRequest.getNxSolutionId());
		
		edfManBulkUploadRequest.setOptyId("optyId");
		assertEquals(new String("optyId"), edfManBulkUploadRequest.getOptyId());
		
		edfManBulkUploadRequest.setUserId("userId");
		assertEquals(new String("userId"), edfManBulkUploadRequest.getUserId());
	}
}
