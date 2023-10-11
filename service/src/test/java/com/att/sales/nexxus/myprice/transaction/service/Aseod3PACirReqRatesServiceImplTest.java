package com.att.sales.nexxus.myprice.transaction.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyString;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.myprice.transaction.model.Aseod3PACirReqRatesResponse;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.RestClientUtil;

@ExtendWith(MockitoExtension.class)
public class Aseod3PACirReqRatesServiceImplTest {
	
	@InjectMocks
	private Aseod3PACirReqRatesServiceImpl aseod3PACirReqRatesServiceImpl;
	
	@Mock
	private Environment env;
	
	@Mock
	private RestClientUtil restClient;
	
	@Mock
	private HttpRestClient httpRest;
	
	@Test
	public void testProcess() {
		try {
			Mockito.when(env.getProperty("myprice.cirReqRatesArrContainer")).thenReturn("https://custompricingdev2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/wl_int_ASEoD3PA_cirReqRatesArrContainer_q");
			Mockito.when(env.getProperty("myprice.username")).thenReturn("");
			Mockito.when(env.getProperty("myprice.password")).thenReturn("");
			String transResponse = new Object().toString();
			Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString()))
			.thenReturn(transResponse);
			Mockito.when(restClient.processResult(anyString(), any()))
			.thenReturn(new Aseod3PACirReqRatesResponse());
			aseod3PACirReqRatesServiceImpl.process("111111");
		} catch (SalesBusinessException e) {
			// TODO Auto-generated catch block
			e.getMessage();
		}
	}
	
	@Test
	public void testProcessException() {
		try {
			Mockito.when(env.getProperty("myprice.cirReqRatesArrContainer")).thenReturn("https://custompricingdev2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/wl_int_ASEoD3PA_cirReqRatesArrContainer_q");
			Mockito.when(env.getProperty("myprice.username")).thenReturn("");
			Mockito.when(env.getProperty("myprice.password")).thenReturn("");
			Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString()))
			.thenThrow(SalesBusinessException.class);
			aseod3PACirReqRatesServiceImpl.process("111111");
		} catch (SalesBusinessException e) {
			// TODO Auto-generated catch block
			e.getMessage();
		}
	}

}
