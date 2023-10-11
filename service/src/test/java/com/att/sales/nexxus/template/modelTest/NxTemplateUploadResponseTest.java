package com.att.sales.nexxus.template.modelTest;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.template.model.NxTemplateUploadResponse;

@ExtendWith(MockitoExtension.class)
public class NxTemplateUploadResponseTest {
	
	
	@InjectMocks
	NxTemplateUploadResponse nxTemplateUploadResponse;
	
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
		ServiceMetaData.add(map);

	}
	
	
	
	

	@Test
	public void test() {
		
		nxTemplateUploadResponse.setFileName("fileName");
		assertEquals(new String("fileName"), nxTemplateUploadResponse.getFileName());
	}

}
