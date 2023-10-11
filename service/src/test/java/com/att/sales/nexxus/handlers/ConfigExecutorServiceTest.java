package com.att.sales.nexxus.handlers;
import static org.junit.Assert.assertNull;

import java.util.HashMap;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.dao.model.NxMpConfigMapping;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdatePricingUtil;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdatePricingUtilFmo;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdatePricingUtilIgloo;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdatePricingUtilInr;

@ExtendWith(MockitoExtension.class)
public class ConfigExecutorServiceTest {
	
	@InjectMocks
	ConfigExecutorService<?> configExecutorServiceTest;
	
	@Mock
	ConfigAndUpdatePricingUtilFmo configAndUpdatePricingUtilFmo;
	
	@Mock
	ConfigAndUpdatePricingUtilIgloo configAndUpdatePricingUtilIgloo;
	
	@Mock
	ConfigAndUpdatePricingUtilInr configAndUpdatePricingUtilInr;
	
	@Mock
	ConfigAndUpdatePricingUtil configAndUpdatePricingUtil;
	
	@Mock
	ConfigureDesignWSHandler configureDesignWSHandler;
	
	@Test
	public void testCall() throws Exception {
		NxMpConfigMapping data = new NxMpConfigMapping();
		String design = "design";
		ConfigExecutorService<ConfigAndUpdatePricingUtilFmo> executorService = 
				new ConfigExecutorService<ConfigAndUpdatePricingUtilFmo>(
				data);
		executorService.setT(configAndUpdatePricingUtilFmo);
		executorService.setDesignData(design);
		executorService.setRequestMap(new HashMap<String, Object>());
		executorService.setInputDesignDetails(new JSONObject());
		assertNull(executorService.call());
		
		ConfigExecutorService<ConfigAndUpdatePricingUtilIgloo> executorService1 = 
				new ConfigExecutorService<ConfigAndUpdatePricingUtilIgloo>(
				data);
		executorService1.setT(configAndUpdatePricingUtilIgloo);
		executorService1.setDesignData(design);
		executorService1.setRequestMap(new HashMap<String, Object>());
		executorService1.setInputDesignDetails(new JSONObject());
		assertNull(executorService1.call());
		
		ConfigExecutorService<ConfigAndUpdatePricingUtilInr> executorService2 = 
				new ConfigExecutorService<ConfigAndUpdatePricingUtilInr>(
				data);
		executorService2.setT(configAndUpdatePricingUtilInr);
		executorService2.setDesignData(design);
		executorService2.setRequestMap(new HashMap<String, Object>());
		executorService2.setInputDesignDetails(new JSONObject());
		assertNull(executorService2.call());
		
		ConfigExecutorService<ConfigAndUpdatePricingUtil> executorService3 = 
				new ConfigExecutorService<ConfigAndUpdatePricingUtil>(
				data);
		executorService3.setT(configAndUpdatePricingUtil);
		executorService3.setDesignData(design);
		executorService3.setRequestMap(new HashMap<String, Object>());
		executorService3.setInputDesignDetails(new JSONObject());
		assertNull(executorService3.call());
		
		ConfigExecutorService<ConfigureDesignWSHandler> executorService4 = 
				new ConfigExecutorService<ConfigureDesignWSHandler>(
				data);
		executorService4.setT(configureDesignWSHandler);
		executorService4.setDesignData(design);
		executorService4.setRequestMap(new HashMap<String, Object>());
		executorService4.setInputDesignDetails(new JSONObject());
		assertNull(executorService4.call());
		
	}
}
