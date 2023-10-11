package com.att.salesmarketing.soma.pric.nexxus.unittest.service;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.sales.nexxus.common.StringConstants;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.model.FetchBillDetailsResponse;
import com.att.sales.nexxus.service.BillDetailsServiceImpl;
import com.att.sales.nexxus.service.ContractInventoryServiceImpl;

@ExtendWith(MockitoExtension.class)
public class BillDetailsServiceImplTest {

	@InjectMocks
	private BillDetailsServiceImpl billDetailsServiceImpl; 
	
	@Mock
	private NxLookupDataRepository nxLookupDataRepository;
	
	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	private static Logger logger = LoggerFactory.getLogger(ContractInventoryServiceImpl.class);
	
	@Test
	public void testfetchBillDetailsTest() {
		FetchBillDetailsResponse response = new FetchBillDetailsResponse();
		List<NxLookupData> nxLookupData=new ArrayList<NxLookupData>();
		NxLookupData data = new NxLookupData();
		logger.info("nxLookupData:"+nxLookupData);
		data.setDescription("January 2019");
		data.setDatasetName(StringConstants.BEGIN_BILL_MONTH);
		nxLookupData.add(data);
		Mockito.when(nxLookupDataRepository.fetchByDatasetNameAndActive(anyList(), anyString())).thenReturn(nxLookupData);
		billDetailsServiceImpl.fetchBillDetails();
	}
}
