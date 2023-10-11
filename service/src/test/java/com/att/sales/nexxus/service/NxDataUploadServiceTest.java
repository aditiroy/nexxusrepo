package com.att.sales.nexxus.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.admin.model.DataUploadRequest;

@ExtendWith(MockitoExtension.class)
public class NxDataUploadServiceTest {

	@InjectMocks
	private NxDataUploadService nxDataUploadService;

	@Mock
	private NxDataUploadHelperService nxDataUploadHelperService;

	@Test
	public void testuploadNexxusDataFile() {
		DataUploadRequest request = new DataUploadRequest();
		nxDataUploadService.uploadNexxusDataFile(request);
	}

}
