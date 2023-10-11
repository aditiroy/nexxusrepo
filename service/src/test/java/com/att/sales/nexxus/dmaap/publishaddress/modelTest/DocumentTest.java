package com.att.sales.nexxus.dmaap.publishaddress.modelTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.dmaap.publishaddress.model.Document;

@ExtendWith(MockitoExtension.class)
public class DocumentTest {
	@InjectMocks
	Document document;
	@Test
	public void test() {
		document.setWiStatusUpdateQ("wiStatusUpdateQ");
	}

}
