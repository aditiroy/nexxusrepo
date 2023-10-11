package com.att.sales.nexxus.validatorTest;


import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.template.model.NxTemplateUploadRequest;
import com.att.sales.nexxus.validator.NxTemplateValidator;


@ExtendWith(MockitoExtension.class)
public class NxTemplateValidatorTest {

	@InjectMocks
	NxTemplateValidator nxTemplateValidator;
	
	@Mock
	InputStream inputStream;
	
	@Test
	public void testValidateUploadRequest() throws SalesBusinessException {
		NxTemplateUploadRequest request = new NxTemplateUploadRequest();
		request.setInputStream(inputStream);
		request.setFileName("fileName.zip");
		request.setFileType("outputFile");
		NxTemplateValidator.validateUploadRequest(request);
	}
	
	@Test
	public void testValidateUploadRequestIf() throws SalesBusinessException {
		NxTemplateUploadRequest request = new NxTemplateUploadRequest();
		request.setInputStream(inputStream);
		request.setFileName("fileName.zip");
		request.setFileType("cdtFile");
		NxTemplateValidator.validateUploadRequest(request);
	}
	
	@Test
	public void validateUploadRequestTest1() throws SalesBusinessException {
		NxTemplateUploadRequest request=new NxTemplateUploadRequest();
		NxTemplateValidator.validateUploadRequest(request);
	}
	
	@Test
	public void validateUploadRequestTest2() throws SalesBusinessException {
		NxTemplateUploadRequest request=new NxTemplateUploadRequest();
		request.setInputStream(inputStream);
		NxTemplateValidator.validateUploadRequest(request);
	}
	
	@Test
	public void validateUploadRequestTest3() throws SalesBusinessException {
		NxTemplateUploadRequest request=new NxTemplateUploadRequest();
		request.setInputStream(inputStream);
		request.setFileName("a.xlsx");
		NxTemplateValidator.validateUploadRequest(request);
	}
	
	@Test
	public void validateUploadRequestTest4() throws SalesBusinessException {
		NxTemplateUploadRequest request=new NxTemplateUploadRequest();
		request.setInputStream(inputStream);
		request.setFileName("a.xlsx");
		request.setFileType("temp");
		NxTemplateValidator.validateUploadRequest(request);
	}

}
