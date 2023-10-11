package com.att.sales.nexxus.dao.repository;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.dao.model.NxLineItemLookUpDataModel;
import com.att.sales.nexxus.dao.model.NxLineItemLookUpFieldModel;
import com.att.sales.nexxus.dao.model.NxLookupData;

/**
 * The Class NxLineItemProcessingDaoTest.
 * @author vt393d
 */
@ExtendWith(MockitoExtension.class)
public class NxLineItemProcessingDaoTest {

	@Spy
	@InjectMocks
	private NxLineItemProcessingDao nxLineItemProcessingDao;
	
	@PersistenceContext
	@Mock
	private EntityManager entityManager;
	
	@Mock
	private static Query query;
	
	@Test
	public void getFmoRulesTest() {
		Query query = mock(Query.class);
		when(entityManager.createQuery(anyString())).thenReturn(query);
		Set<Long> offerId=new HashSet<>();
		nxLineItemProcessingDao.getFmoRules(offerId);
	}
	
	@Test
	public void getNexxusLineItemLookUpItemsTest() {
		Query query = mock(Query.class);
		when(entityManager.createQuery(anyString())).thenReturn(query);
		nxLineItemProcessingDao.getNxLineItemFieldDataByOfferId(1l, "AVPN");
	}
	
	@Test
	public void getListItemDataTest() {
		Query query = mock(Query.class);
		when(entityManager.createNativeQuery(anyString(),eq(NxLineItemLookUpDataModel.class))).
		thenReturn(query);
		nxLineItemProcessingDao.getLineItemData("x", "fmo");
	}
	
	@Test
	public void getListItemDataByBeIdTest() {
		Query query = mock(Query.class);
		when(entityManager.createNativeQuery(anyString(),anyString())).
		thenReturn(query);
		nxLineItemProcessingDao.getLineItemData("x", "y", "z", "fmo");
	}
	
	@Test
	public void loadNexxusKeyPathDataTest() {
		Query query = mock(Query.class);
		when(entityManager.createQuery(anyString())).thenReturn(query);
		nxLineItemProcessingDao.loadNexxusKeyPathData();
	}
	
	@Test
	public void getDataFromLookUpTblTest() {
		Query query = mock(Query.class);
		when(entityManager.createNativeQuery(anyString())).
		thenReturn(query);
		nxLineItemProcessingDao.getDataFromSalesLookUpTbl(1l, 2l, 3l, 4l);
	}
	
	@Test
	public void testGetNxLineItemFieldDataByOfferName() {
		Query query = mock(Query.class);
		when(entityManager.createQuery(anyString())).
		thenReturn(query);
		List<NxLineItemLookUpFieldModel> nxLineItemLookupFieldModelList = new ArrayList<>();
		
		NxLineItemLookUpFieldModel nxLineItemLookUpFieldModelObj1 = new NxLineItemLookUpFieldModel();
		nxLineItemLookUpFieldModelObj1.setActive("Y");
		nxLineItemLookUpFieldModelObj1.setCountryCd("GE");
		nxLineItemLookupFieldModelList.add(nxLineItemLookUpFieldModelObj1);
		
		when(query.getResultList()).thenReturn(nxLineItemLookupFieldModelList);
		
		List<NxLineItemLookUpFieldModel>  result=nxLineItemProcessingDao.getNxLineItemFieldDataByOfferName("AVPN", "INR");
		assertSame(nxLineItemLookUpFieldModelObj1, result.get(0));
	}

	@Test
	public void testGetDataFromIms2LookUpTbl() {
		Query query = mock(Query.class);
		when(entityManager.createNativeQuery(anyString())).
		thenReturn(query);
		List<Object> displayValueList = new ArrayList<>();
		displayValueList.add("display1");
		
		when(query.getResultList()).thenReturn(displayValueList);
		List<Object> result=nxLineItemProcessingDao.getDataFromIms2LookUpTbl("Item", 1L, 2L, 3L);
		assertSame(displayValueList,result);
	}
	
	@Test
	public void testGetNxLookupDataById() {
		Query query = mock(Query.class);
		when(entityManager.createQuery(anyString())).
		thenReturn(query);
		
		List<NxLookupData> nxLookupDataList = new ArrayList<>();
		NxLookupData nxLookupDataObj= new NxLookupData();
		nxLookupDataObj.setActive("Y");
		nxLookupDataObj.setDatasetName("CUSTOM_CONFIG_RULE");
		nxLookupDataObj.setDescription("test description");
		nxLookupDataList.add(nxLookupDataObj);
		
		when(query.getResultList()).thenReturn(nxLookupDataList);
		List<NxLookupData> result = nxLineItemProcessingDao.getNxLookupDataById("CUSTOM_CONFIG_RULE", "34");
		assertSame(nxLookupDataObj,result.get(0));
	}
}
