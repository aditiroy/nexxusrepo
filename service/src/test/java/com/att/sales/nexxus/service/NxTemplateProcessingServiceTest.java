package com.att.sales.nexxus.service;

import static org.mockito.Mockito.doReturn;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.TemplateFileConstants;
import com.att.sales.nexxus.template.model.NxTemplateUploadRequest;
import com.att.sales.nexxus.template.model.NxTemplateUploadResponse;

@ExtendWith(MockitoExtension.class)
public class NxTemplateProcessingServiceTest {

	@InjectMocks
	NxTemplateProcessingService test;
	@Mock
	Environment env;
	@Mock
	InputStream uploadedFile;
	@Mock
	Path uploadFilepath;
	@Mock
	File file;
	
	@BeforeEach
	public static void init() {
		Map<String, Object> map = new HashMap<>();
		map.put(ServiceMetaData.OFFER, "2");
		map.put(ServiceMetaData.VERSION, "v2");
		map.put(ServiceMetaData.METHOD, "post");
		map.put(ServiceMetaData.URI, "hghg");
		map.put(ServiceMetaData.REST_REQUEST_START_TIME, System.currentTimeMillis());
		map.put(ServiceMetaData.SERVICE_FILTER, "AVPN");
		map.put(ServiceMetaData.SERVICEID, "SERVICEID");
		map.put(CommonConstants.FILENAME, "FILE_1");
		
		ServiceMetaData.add(map);

	}
	
	@Test
	public void testUploadTemplateFile() throws SalesBusinessException {
		NxTemplateUploadResponse response=new NxTemplateUploadResponse();
		NxTemplateUploadRequest request=new NxTemplateUploadRequest();
		request.setFileType(TemplateFileConstants.OUTPUT_FILE);
		request.setInputStream(uploadedFile);
		Path uploadFilepath =null;
		String filePath ="nx.output.template.path";
		//Mockito.when(test.getFilePath(Mockito.anyString())).thenReturn(uploadFilepath);
		//doReturn(uploadFilepath.getFileName()).doNothing();
		test.uploadTemplateFile(request);
		
	}
	/*@Test
	public void testReplaceFile() throws IOException {
	test.replaceFile(uploadedFile, uploadFilepath);
	}*/
	@Test
	public void testGetFilePath() throws SalesBusinessException {
		String filePath="nx.output.template.path";
		File file = new File(filePath);
		String fileName="file";
		String dir="";
		String baseFolder="base";
		Path validateFilepath=null;
		test.getFilePath(filePath);
		
	
	}
	@Test
	public void testGetFilePathAndCreateFolderIfNotExists() throws IOException {
		String fileName="file";
		String baseFolder="base";
		test.getFilePathAndCreateFolderIfNotExists(baseFolder, fileName);
	}

}
