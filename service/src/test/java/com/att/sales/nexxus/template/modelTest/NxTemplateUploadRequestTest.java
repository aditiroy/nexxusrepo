package com.att.sales.nexxus.template.modelTest;


import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.template.model.NxTemplateUploadRequest;

@ExtendWith(MockitoExtension.class)
public class NxTemplateUploadRequestTest {

	@InjectMocks 
	NxTemplateUploadRequest nxTemplateUploadRequest;
	
	@Mock
InputStream inputStream;
	
	@Test
	public void test() {
		nxTemplateUploadRequest.setExtension("extension");
		assertEquals(new String("extension"), nxTemplateUploadRequest.getExtension());
		
		nxTemplateUploadRequest.setFileName("fileName");
		assertEquals(new String("fileName"), nxTemplateUploadRequest.getFileName());
		
		nxTemplateUploadRequest.setFileType("fileType");
		assertEquals(new String("fileType"), nxTemplateUploadRequest.getFileType());
		
	}

}
