package com.att.sales.nexxus.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxMpConfigJsonMappingRepository;
import com.att.sales.nexxus.dao.repository.NxMpConfigMappingRepository;

@ExtendWith(MockitoExtension.class)
public class NxMyPriceRepositoryServceTest {
	
	@Spy
	@InjectMocks
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	@Mock
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Mock
	private NxMpConfigMappingRepository nxMpConfigMappingRepository;
	
	@Mock
	private NxMpConfigJsonMappingRepository nxMpConfigJsonMappingRepository;
	
	@Test
	public void getLookupDataByItemIdTes() {
		List<NxLookupData> lookupData=new ArrayList<NxLookupData>();
		NxLookupData n=new NxLookupData();
		n.setItemId("A");
		lookupData.add(n);
		when(nxLookupDataRepository.findByDatasetName(any())).thenReturn(lookupData);
		nxMyPriceRepositoryServce.getLookupDataByItemId("abc");
	}
	
	@Test
	public void findByOfferAndProductTypeAndRuleNameTest() {
		nxMyPriceRepositoryServce.findByOfferAndProductTypeAndRuleName("avpn", "f", "f");
	}
	
	@Test
	public void findByOfferAndRuleNameTest() {
		nxMyPriceRepositoryServce.findByOfferAndRuleName("A", "b");
	}
	
	@Test
	public void findByOfferAndSubOfferAndProductTypeAndRuleNameTest() {
		nxMyPriceRepositoryServce.findByOfferAndSubOfferAndProductTypeAndRuleName("A", "B", "C","D");
	}
	
	@Test 
	public void getDataFromLookupTest() {
		List<NxLookupData> lookupData=new ArrayList<NxLookupData>();
		NxLookupData n=new NxLookupData();
		n.setItemId("A");
		n.setCriteria("g");
		lookupData.add(n);
		when(nxLookupDataRepository.findByDatasetName(any())).thenReturn(lookupData);
		nxMyPriceRepositoryServce.getDataFromLookup("A");
	}
	
	@Test
	public void getDescDataFromLookupTest() {
		List<NxLookupData> lookupData=new ArrayList<NxLookupData>();
		NxLookupData n=new NxLookupData();
		n.setItemId("A");
		n.setDescription("v");
		lookupData.add(n);
		when(nxLookupDataRepository.findByDatasetName(any())).thenReturn(lookupData);
		nxMyPriceRepositoryServce.getDescDataFromLookup("A");
	}
	
	@Test
	public void cacheEnabledTest() {
		nxMyPriceRepositoryServce.cacheEnabled();
	}
	
	@Test
	public void clearCacheTest() {
		nxMyPriceRepositoryServce.clearCache();
	}

}
