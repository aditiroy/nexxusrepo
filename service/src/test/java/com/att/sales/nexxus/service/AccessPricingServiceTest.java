package com.att.sales.nexxus.service;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.inr.InrFactory;
import com.att.sales.nexxus.inr.InrJsonToIntermediateJson;
import com.att.sales.nexxus.inr.OutputJsonFallOutData;
import com.att.sales.nexxus.inr.OutputJsonService;
import com.att.sales.nexxus.service.AccessPricingService;
import com.fasterxml.jackson.databind.JsonNode;

@ExtendWith(MockitoExtension.class)
public class AccessPricingServiceTest {
	@Mock
	private InrFactory inrFactory;
	@Mock
	private InrJsonToIntermediateJson inrJsonToIntermediateJson;
	@Mock
	private JsonNode rawJson;
	@InjectMocks
	private AccessPricingService test;
	@Mock
	private OutputJsonFallOutData outputJsonFallOutData;
	@Mock
	private OutputJsonService outputJsonService;

	@Test
	public void testgenerateIntermediateJson() throws SalesBusinessException {
		when(inrFactory.getInrJsonToIntermediateJson(any())).thenReturn(inrJsonToIntermediateJson);
		when(inrJsonToIntermediateJson.generate()).thenReturn(rawJson);
		assertSame(rawJson, test.generateIntermediateJson(rawJson));

	}

	@Test
	public void testGenerateOutputJson() throws SalesBusinessException {
		when(outputJsonService.getOutputData(any(), any())).thenReturn(outputJsonFallOutData);
		assertSame(outputJsonFallOutData, test.generateOutputJson(rawJson));
	}
}
