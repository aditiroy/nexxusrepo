package com.att.sales.nexxus.p8;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class P8ServiceTest {

	@InjectMocks
	P8Service p8Service;
	
	@Test
	public void lookupDocumentInP8Test(){
		setupP8Service();
		p8Service.lookupDocumentInP8("18002320170817073522_315576827.xml");
		p8Service.lookupDocumentInP8("18002320170817073522_315576827.xml11");
	}
	
	@Test
	public void lookupDocumentInP8TestInCorrectURL(){
		p8Service.setP8Url("http://p8ecmcesd.web");
		p8Service.lookupDocumentInP8("18002320170817073522_315576827.xml");
		p8Service.lookupDocumentInP8("18002320170817073522_315576827.xml11");
	}
	
	private void setupP8Service(){
		p8Service.setP8directory("/FPP/inventory");
		p8Service.setP8Url("http://p8ecmcesd.web.att.com/wsi/FNCEWS40MTOM");
		p8Service.setP8User("m12568");
		p8Service.setP8Password("BigDream@IBM20$");
		p8Service.setStrObjStore("SANDBOX");
		p8Service.setP8dLocalPath("C:\\");
	}
}

