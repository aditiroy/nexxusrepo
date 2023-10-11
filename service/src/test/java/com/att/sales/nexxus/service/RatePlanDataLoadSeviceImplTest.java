package com.att.sales.nexxus.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

//import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.helper.FileReaderHelper;
import com.att.sales.nexxus.prdm.service.PrdmSubscriptionStatusImpl;
import com.att.sales.nexxus.prdm.service.RatePlanAuditDataServiceImpl;


@ExtendWith(MockitoExtension.class)
public class RatePlanDataLoadSeviceImplTest {

	@InjectMocks
	RatePlanDataLoadSeviceImpl ratePlanDataLoadSeviceImpl;

	@Mock
	private Environment env;
	@Mock
	Connection connection;
	@Mock
	CallableStatement callableStatement;
	
	//@Mock
	//MultipartBody multipartBody;
	
	@Mock
	private FileReaderHelper fileReaderHelper;

	@Mock
	public PrdmSubscriptionStatusImpl prdmSubscriptionStatusImpl;

	@Mock
	public RatePlanAuditDataServiceImpl planAuditDataServiceImpl;
	
	   @BeforeEach
		 	public void initializeServiceMetaData() {
		 		Map<String, Object> requestParams = new HashMap<>();
		 		requestParams.put(ServiceMetaData.OFFER, "AVPN");
		 		requestParams.put(ServiceMetaData.REST_REQUEST_START_TIME, System.currentTimeMillis());
		 		requestParams.put(ServiceMetaData.VERSION, "1.0");
		 		requestParams.put(ServiceMetaData.METHOD, "TestMethod");
		 		requestParams.put(ServiceMetaData.URI, "Testuri");
		 		ServiceMetaData.add(requestParams);
		 	}
	
	/*@Test
	public void test() throws SalesBusinessException {
		ProductDataLoadRequest productDataLoadRequest = new ProductDataLoadRequest();
		productDataLoadRequest.setMultipartBody(multipartBody);
		try {
			ratePlanDataLoadSeviceImpl.putRatePlanDataLoad(productDataLoadRequest);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testElse() {
		ProductDataLoadRequest productDataLoadRequest = new ProductDataLoadRequest();
		productDataLoadRequest.setMultipartBody(multipartBody);

		try {
			ratePlanDataLoadSeviceImpl.putRatePlanDataLoad(productDataLoadRequest);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ratePlanDataLoadSeviceImpl.loadDataFromArchToWrkTablesForPrdm();
		String message ="ROLLBACK";
		try {
			ratePlanDataLoadSeviceImpl.loadPrdmDataFromWorkingToActualTables(message);
		} catch (SalesBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		message ="ACTIVE";
		try {
			ratePlanDataLoadSeviceImpl.loadPrdmDataFromWorkingToActualTables(message);
		} catch (SalesBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		@Test
		public void testCatch() {
			ProductDataLoadRequest productDataLoadRequest = new ProductDataLoadRequest();
			productDataLoadRequest.setMultipartBody(multipartBody);
			String reasonCode = null;
			try {
				ratePlanDataLoadSeviceImpl.putRatePlanDataLoad(productDataLoadRequest);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ratePlanDataLoadSeviceImpl.loadDataFromArchToWrkTablesForPrdm();
			String message ="ROLLBACK";
			try {
				ratePlanDataLoadSeviceImpl.loadPrdmDataFromWorkingToActualTables(message);
			} catch (SalesBusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			message ="ACTIVE";
			try {
				ratePlanDataLoadSeviceImpl.loadPrdmDataFromWorkingToActualTables(message);
			} catch (SalesBusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}*/
		
		
	@Test
	public void testSetDesignDataLoadData() throws SalesBusinessException {
	String fileName=null;
	try {
	ratePlanDataLoadSeviceImpl.setDesignDataLoadData(fileName);
	}catch(Exception e){
		}
	}
}


