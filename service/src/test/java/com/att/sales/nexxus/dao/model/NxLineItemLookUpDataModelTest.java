package com.att.sales.nexxus.dao.model;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NxLineItemLookUpDataModelTest {
	
	@InjectMocks
	NxLineItemLookUpDataModel test;

	@Test
	public void test() {
		Long lineItemId = new Long(21);
		String secondaryKey="";
		Long littleProdId= new Long(21);
		Long topProdId= new Long(21);
		Long offerId= new Long(1);
		String idetityField1="";
		String idetityField2="";
		String tabName="";
		String field19="";
		String field20="";
		test = new NxLineItemLookUpDataModel(lineItemId, secondaryKey,
				littleProdId, topProdId,offerId,field19,field20, idetityField1, idetityField2, tabName);
	}

}
