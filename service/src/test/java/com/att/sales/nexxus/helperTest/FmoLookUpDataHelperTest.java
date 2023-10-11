package com.att.sales.nexxus.helperTest;


import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.constant.FmoConstants;
import com.att.sales.nexxus.dao.model.FmoProdLookupData;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.repository.FmoProdLookupDataRepo;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.helper.FmoLookUpDataHelper;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

/**
 * @author vt393d
 *
 */
@ExtendWith(MockitoExtension.class)
public class FmoLookUpDataHelperTest {

	@Spy
	@InjectMocks
	FmoLookUpDataHelper fmoLookUpDataHelper;
	
	@Mock
	private FmoProdLookupDataRepo fmoProdLookupDataRepo;
	
	@Mock
	private HazelcastInstance  hazelcastInstance;
	
	@Mock
	private IMap<Object, Object> imap;
	
	@Mock
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Test
	public void createPriceTypeDataMapTest1() {
		List<FmoProdLookupData> fmoProdLookupDataLst=new ArrayList<>();
		FmoProdLookupData obj=new FmoProdLookupData();
		obj.setActive("Y");
		obj.setIms2Code("PORT");
		obj.setRateType("RC");
		obj.setProductRateId("123");
		fmoProdLookupDataLst.add(obj);
		when(fmoProdLookupDataRepo.findDataRateType(any())).thenReturn(fmoProdLookupDataLst);
		List<NxLookupData> nxLookupLst=new ArrayList<NxLookupData>();
		NxLookupData f=new NxLookupData();
		f.setItemId("PORTRC");
		f.setDescription("b");
		nxLookupLst.add(f);
		when(nxLookupDataRepository.findByDatasetName(any())).thenReturn(nxLookupLst);
		fmoLookUpDataHelper.createPriceTypeDataMap();
	}
	
	@Test
	public void createPriceTypeDataMapTest2() {
		List<FmoProdLookupData> fmoProdLookupDataLst=new ArrayList<>();
		FmoProdLookupData obj=new FmoProdLookupData();
		obj.setActive("Y");
		obj.setIms2Code("FS");
		obj.setRateType("GH");
		obj.setProductRateId("123");
		fmoProdLookupDataLst.add(obj);
		when(fmoProdLookupDataRepo.findDataRateType(any())).thenReturn(fmoProdLookupDataLst);
		fmoLookUpDataHelper.createPriceTypeDataMap();
	}
	
	@Test
	public void getFmoLookDataFromCacheTest1() {
		ConcurrentMap<String,String> dataMap=new ConcurrentHashMap<>();
		dataMap.put("123", "PORTNRC");
		imap.put(FmoConstants.PRICE_TYPE_DATA, dataMap);
		when(hazelcastInstance.getMap(any())).thenReturn(imap);
		fmoLookUpDataHelper.getFmoLookDataFromCache();
	}
	
	@Test
	public void getFmoLookDataFromCacheTest2() {
		fmoLookUpDataHelper.getFmoLookDataFromCache();
	}
	
	
}
