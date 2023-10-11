package com.att.sales.nexxus.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.att.sales.framework.exception.SalesBusinessException;
//import com.att.sales.framework.tracing.ZipkinWrapper;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.reteriveicb.model.Solution;
import com.att.sales.nexxus.service.WebServiceErrorAlertService;
@ExtendWith(MockitoExtension.class)
public class RestClientUtilTest {
	@Spy
	@InjectMocks
	private RestClientUtil restClientUtil;
	/*@Mock
	private ZipkinWrapper zipkinWrapper;*/
	@Mock
	private HttpURLConnection conn;
	@Mock
	private BufferedReader br;
	@Mock
	private OutputStream os;
	@Mock
	private Proxy proxy;
	@Mock
	private WebServiceErrorAlertService webServiceErrorAlertService;
	
	@Test
	public void processResultTest() {
		Object res = restClientUtil.processResult("{}", Solution.class);
		assertNotNull(res);
		
		res = restClientUtil.processResult("{", Solution.class);
		assertNull(res);
	}
	
	@Test
	public void getProxyTest() {
		Proxy res = restClientUtil.getProxy("", "80", "", "");
		assertNotNull(res);
	}
	
	
	
	
	@Test
	public void callMPRestClientTest() throws IOException, SalesBusinessException {
		String ingressUrl = "https://cmlp-portal.prod.sci.att.com/com-att-cmlp-designtime-prod/predictors/rk967c-20190403202126/v2/syncPredictions";
		String request = "request";
		Map<String, Object> queryParameters = new HashMap<>();
		queryParameters.put("key", "value");
		Map<String, String> headers = new HashMap<>();
		headers.put("key", "value");
		doReturn(conn).when(restClientUtil).openConnection(any());
		when(conn.getOutputStream()).thenReturn(os);
		doReturn(br).when(restClientUtil).getBufferedReaderFromInputStream(any());
		when(br.readLine()).thenReturn("line", "1", null);
		
		restClientUtil.callRestApi(request, ingressUrl, "", headers, queryParameters, null);
	}
	
	@Test
	public void callMPRestClientFailedResponseMessageTest() throws IOException, SalesBusinessException {
		String ingressUrl = "https://cmlp-portal.prod.sci.att.com/com-att-cmlp-designtime-prod/predictors/rk967c-20190403202126/v2/syncPredictions";
		String request = "request";
		Map<String, Object> queryParameters = new HashMap<>();
		queryParameters.put("key", "value");
		Map<String, String> headers = new HashMap<>();
		headers.put("key", "value");
		doReturn(conn).when(restClientUtil).openConnection(any());
		when(conn.getOutputStream()).thenReturn(os);
		doReturn(br).when(restClientUtil).getBufferedReaderFromInputStream(any());
		when(br.readLine()).thenReturn("line", "1", null);
		queryParameters.put(MyPriceConstants.PROXY, proxy);
		doReturn(conn).when(restClientUtil).openConnection(any(), any());
		when(conn.getResponseMessage()).thenReturn("not ok");
		
		restClientUtil.callRestApi(request, ingressUrl, "", headers, queryParameters, null);
	}
	
	@Test
	public void callMPRestClientHttpURLConnectionIOExceptionTest() throws IOException, SalesBusinessException {
		String ingressUrl = "https://cmlp-portal.prod.sci.att.com/com-att-cmlp-designtime-prod/predictors/rk967c-20190403202126/v2/syncPredictions";
		String request = "request";
		Map<String, Object> queryParameters = new HashMap<>();
		queryParameters.put("key", "value");
		Map<String, String> headers = new HashMap<>();
		headers.put("key", "value");
		doThrow(IOException.class).when(restClientUtil).openConnection(any());
		
		restClientUtil.callRestApi(request, ingressUrl, "", headers, queryParameters, null);
	}
	
	@Test
	public void callMPRestClientOutputStreamIOExceptionIOExceptionTest() throws IOException, SalesBusinessException {
		String ingressUrl = "https://cmlp-portal.prod.sci.att.com/com-att-cmlp-designtime-prod/predictors/rk967c-20190403202126/v2/syncPredictions";
		String request = "request";
		Map<String, Object> queryParameters = new HashMap<>();
		queryParameters.put("key", "value");
		Map<String, String> headers = new HashMap<>();
		headers.put("key", "value");
		doReturn(conn).when(restClientUtil).openConnection(any());
		when(conn.getOutputStream()).thenReturn(os);
		doReturn(br).when(restClientUtil).getBufferedReaderFromInputStream(any());
		when(br.readLine()).thenReturn("line", "1", null);
		doThrow(IOException.class).when(os).flush();
		
		restClientUtil.callRestApi(request, ingressUrl, "", headers, queryParameters, null);
	}
	
	@Test
	public void sendToZipkinTest() {
		ReflectionTestUtils.setField(restClientUtil, "zipkinTraceEnabled", "Y");
		restClientUtil.sendToZipkin(null, null, null, true, true);
		restClientUtil.sendToZipkin(null, null, null, true, false);
	}
}
