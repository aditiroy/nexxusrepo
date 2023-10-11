/**
 * 
 */
package com.att.sales.nexxus.serviceValidation.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyString;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.dao.model.NxMpSiteDictionary;
import com.att.sales.nexxus.util.DME2RestClient;

/**
 * @author ShruthiCJ
 *
 */
@ExtendWith(MockitoExtension.class)
public class GetQualificationServiceImplTest {
	
	@InjectMocks
	GetQualificationServiceImpl getQualificationServiceImpl;
	
	@Mock
	DME2RestClient dme2RestClient;
	
	@Mock
	AVSQUtil avsqUtil;
	
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
	
	@SuppressWarnings("unchecked")
	@Disabled
	@Test
	public void testGetQualification() throws SalesBusinessException, JSONException {
		String sourceSystem = "myPrice";
		Integer motsId = 22787;
		String uniqueId = "43784986";
		Long nxTxnId = 1L;
		JSONObject objectJson = new JSONObject();
		objectJson.put("statusCodeValue", 200);
		JSONObject body = new JSONObject();
		body.put("status", "Completed");
		objectJson.put("body", body);
		String response = objectJson.toString();
		Mockito.when(dme2RestClient.callIpeGetQualification(anyMap())).thenReturn(response);
		NxMpSiteDictionary nxMpSiteDictionary = new NxMpSiteDictionary();
		Mockito.when(avsqUtil.populateSiteJsonforQualification(anyLong(), anyString(), any())).thenReturn(nxMpSiteDictionary);
		getQualificationServiceImpl.getQualification(sourceSystem, motsId, uniqueId, nxTxnId,null);
	}
	@Disabled
	@Test
	public void testGetQualificationTest() throws SalesBusinessException, JSONException {
		String sourceSystem = "myPrice";
		Integer motsId = 22787;
		String uniqueId = "43784986";
		Long nxTxnId = 1L;
		JSONObject objectJson = new JSONObject();
		objectJson.put("statusCodeValue", 0);
		objectJson.put("body", new JSONObject());
		String response = objectJson.toString();
		Mockito.when(dme2RestClient.callIpeGetQualification(anyMap())).thenReturn(response);
		NxMpSiteDictionary nxMpSiteDictionary = new NxMpSiteDictionary();
		Mockito.when(avsqUtil.populateSiteJsonforQualification(1L, objectJson.get("body").toString(),null)).thenReturn(nxMpSiteDictionary);
		getQualificationServiceImpl.getQualification(sourceSystem, motsId, uniqueId, nxTxnId,null);
	}

}
