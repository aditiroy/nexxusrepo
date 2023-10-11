package com.att.sales.nexxus.myprice.transaction.service;

import static org.mockito.Mockito.any;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.att.sales.nexxus.myprice.transaction.model.Aseod3PACirReqRatesResponse;
import com.att.sales.nexxus.myprice.transaction.model.Aseod3PAMileageReqRatesResponse;
import com.att.sales.nexxus.myprice.transaction.model.Aseod3PAPortReqRatesResponse;



@ExtendWith(MockitoExtension.class)

public class Aseod3PAReqRatesHelperServiceTest {
	
	@InjectMocks
	@Spy
	private Aseod3PAReqRatesHelperService aseod3PAReqRatesHelperService;

	
	@BeforeEach
	public void init() {
		ReflectionTestUtils.setField(aseod3PAReqRatesHelperService, "threadSize", 1);
	}
	
	@Mock
	ExecutorService executor;
	
	@Mock
	List<Callable<Object>> callable;
	
	
	@Test
	public void testGetIncPrices3PA() throws InterruptedException {
		List<Future<Object>> resultLst = new ArrayList<Future<Object>>();
		resultLst.add(CompletableFuture.completedFuture(new Aseod3PACirReqRatesResponse()));
		resultLst.add(CompletableFuture.completedFuture(new Aseod3PAPortReqRatesResponse()));
		resultLst.add(CompletableFuture.completedFuture(new Aseod3PAMileageReqRatesResponse()));
		Mockito.when(executor.invokeAll(any())).thenReturn(resultLst);
		Mockito.when(aseod3PAReqRatesHelperService.getExcutorService()).thenReturn(executor);
		aseod3PAReqRatesHelperService.getIncPrices3PA("111111");
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCancellationException() throws InterruptedException {
		Mockito.when(executor.invokeAll(any())).thenThrow(CancellationException.class);
		Mockito.when(aseod3PAReqRatesHelperService.getExcutorService()).thenReturn(executor);
		aseod3PAReqRatesHelperService.getIncPrices3PA("111111");
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testException() {
		Mockito.when(aseod3PAReqRatesHelperService.getExcutorService()).thenThrow(Exception.class);
		aseod3PAReqRatesHelperService.getIncPrices3PA("111111");
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testExecutionException() throws InterruptedException {
		Mockito.when(executor.invokeAll(any())).thenThrow(ExecutionException.class);
		Mockito.when(aseod3PAReqRatesHelperService.getExcutorService()).thenReturn(executor);
		aseod3PAReqRatesHelperService.getIncPrices3PA("111111");
	}


}
