package com.att.sales.nexxus.dao.model;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NexxusPreviewInrUIModelTest {
	@InjectMocks
	NexxusPreviewInrUIModel test;
	
	@Test
	public void test() {
		String customerName = "";
		String optyId= "";
		String dunsNumber= "";
		String productCd= "";
		String intermediateJson= "";
		String outputJson= "";
		test = new NexxusPreviewInrUIModel(customerName, optyId, dunsNumber, productCd, intermediateJson, outputJson);
	}

}
