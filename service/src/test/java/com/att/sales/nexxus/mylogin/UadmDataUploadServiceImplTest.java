package com.att.sales.nexxus.mylogin;

import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.att.sales.framework.exception.SalesBusinessException;

@ExtendWith(MockitoExtension.class)
public class UadmDataUploadServiceImplTest {
	
	
	@InjectMocks
	private UadmDataUploadServiceImpl uadmDataUploadServiceImpl;
	
	@Mock
	private GetUsersForGroupWSHandler getUsersForGroupWSHandler;
	
	@Mock
	private NexxusMyloginService nexxusMyloginService;
	
	@BeforeEach
	public void init() {
		ReflectionTestUtils.setField(uadmDataUploadServiceImpl, "mypriceDataupload","Y");
		ReflectionTestUtils.setField(uadmDataUploadServiceImpl, "nexxusDataupload","Y");
	}
	
	@Test
	public void testUploadMypriceData() throws SalesBusinessException {
		Mockito.when(getUsersForGroupWSHandler.getUsersWebService(anyMap())).thenReturn(true);
		uadmDataUploadServiceImpl.uploadMypriceData();
	}
	
	@Test
	public void testUploadMypriceDataExc() throws SalesBusinessException {
		Mockito.when(getUsersForGroupWSHandler.getUsersWebService(anyMap())).thenThrow(Exception.class);
		uadmDataUploadServiceImpl.uploadMypriceData();
	}
	
	@Test
	public void testUploadNexxusData() throws SalesBusinessException {
		doNothing().when(nexxusMyloginService).getUserProfile();
		uadmDataUploadServiceImpl.uploadNexxusData();
	}
	
	@Test
	public void testUploadNexxusDataExc() throws SalesBusinessException {
		doThrow(Exception.class).when(nexxusMyloginService).getUserProfile();
		uadmDataUploadServiceImpl.uploadNexxusData();
	}

}
