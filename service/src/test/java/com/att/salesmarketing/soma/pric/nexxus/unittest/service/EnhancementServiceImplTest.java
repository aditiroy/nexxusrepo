package com.att.salesmarketing.soma.pric.nexxus.unittest.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.model.NewEnhancementRequest;
import com.att.sales.nexxus.service.EnhancementServiceImpl;

@ExtendWith(MockitoExtension.class)
public class EnhancementServiceImplTest {

	@InjectMocks
	private EnhancementServiceImpl enhancementServiceImpl; 
	
	@Mock
	private NxLookupDataRepository nxLookupDataRepository;
	
	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testfetchNewEnhancementsTest() {
		NewEnhancementRequest request = new NewEnhancementRequest();
		List<NxLookupData> nxLookupData=new ArrayList<NxLookupData>();
		NxLookupData data = new NxLookupData();
		data.setDatasetName("WHATS_NEW");
		data.setItemId("WN");
		data.setDescription("Enhancement Response");
		data.setCriteria("test");
		data.setSortOrder(null);
		data.setActive("Y");
		nxLookupData.add(data);
		when(nxLookupDataRepository.findByDatasetNameAndActive(anyString(),anyString())).thenReturn(nxLookupData);
		enhancementServiceImpl.fetchNewEnhancements(request);
	}
}
