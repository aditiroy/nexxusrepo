package com.att.sales.exampledomainobject.modelTest;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.model.QueoteRequestList;
import com.att.sales.nexxus.model.QuoteRequest;

/*
 * @Author chandan
 */
@ExtendWith(MockitoExtension.class)
public class QuoteRequestTest {
	@Test
public void testGetterAndSetter() {
		QuoteRequest request=new QuoteRequest();
		QueoteRequestList queoteRequest=new QueoteRequestList();
		List<QueoteRequestList> list=new ArrayList<>();
		request.setQuoteRequest(list);
		assertEquals(list, request.getQuoteRequest());
			
		
}
}
