package com.att.salesmarketing.soma.pric.nexxus.unittest.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyString;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.service.NexxusAIService;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.RestClientUtil;
/*
 * chandan(ck218y)
 */

@ExtendWith(MockitoExtension.class)
public class NexxusAIServiceTest {
	@Mock
	RestClientUtil restClient;
	
	@InjectMocks
	NexxusAIService service;
	
	@Mock
	private HttpRestClient httpRest;

	@BeforeAll
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
	@Disabled
	@Test
	public void testgetNxPredictions() throws SalesBusinessException {
		Set<String> beids = new HashSet<>();
		String s1 = "beid";
		beids.add(s1);
		String product = "AVPN";
		Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString())).thenReturn(product);
		service.getNxPredictions(beids, product);
	}
	@Disabled
	@Test
	public void testgetNxPredictionsWithRequest() throws SalesBusinessException {
		String product = "AVP";
		Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString())).thenReturn(product);

		service.getNxPredictions("AVPN");
	}
	
}
