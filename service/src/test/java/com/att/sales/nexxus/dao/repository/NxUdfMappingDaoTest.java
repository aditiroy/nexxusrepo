package com.att.sales.nexxus.dao.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.dao.model.NxUdfMapping;
@ExtendWith(MockitoExtension.class)
public class NxUdfMappingDaoTest {

	@Mock
	private EntityManager em;

	@InjectMocks
	private NxUdfMappingDao nxUdfMappingDao;
	
	@Mock
	TypedQuery<NxUdfMapping> queryNxUdfMapping;
	
	@Test
	public void TestgetNxUdfMappingMap() {
		when(em.createQuery(any(), eq(NxUdfMapping.class))).thenReturn(queryNxUdfMapping);
		List<NxUdfMapping> resultList= new ArrayList<NxUdfMapping>();
		NxUdfMapping nxUdfMappingObj1 = new NxUdfMapping();
		nxUdfMappingObj1.setUdfId(1L);
		
		NxUdfMapping nxUdfMappingObj2 = new NxUdfMapping();
		nxUdfMappingObj2.setUdfId(2L);
		
		resultList.add(nxUdfMappingObj1);
		resultList.add(nxUdfMappingObj2);
		
		when(queryNxUdfMapping.getResultList()).thenReturn(resultList);

		Map<Long, NxUdfMapping> resultNxUdfMap = nxUdfMappingDao.getNxUdfMappingMap("ruleSet",1L,1L);
		assertSame(nxUdfMappingObj1,resultNxUdfMap.get(1L));
		assertSame(nxUdfMappingObj2,resultNxUdfMap.get(2L));
	}

	@Test
	public void TestgetNxUdfDataByOfferIdAndRule() {
		when(em.createQuery(any(), eq(NxUdfMapping.class))).thenReturn(queryNxUdfMapping);
		List<NxUdfMapping> resultList= new ArrayList<NxUdfMapping>();

		NxUdfMapping nxUdfMappingObj1 = new NxUdfMapping();
		nxUdfMappingObj1.setComponentId(1L);
		nxUdfMappingObj1.setUdfAbbr("udfAbbr");
		
		NxUdfMapping nxUdfMappingObj2 = new NxUdfMapping();
		nxUdfMappingObj2.setComponentId(2L);
		nxUdfMappingObj2.setUdfAbbr("udfAbbr2");
		
		resultList.add(nxUdfMappingObj1);
		resultList.add(nxUdfMappingObj2);
		
		when(queryNxUdfMapping.getResultList()).thenReturn(resultList);
		List<NxUdfMapping> result= nxUdfMappingDao.getNxUdfDataByOfferIdAndRule("ruleSet",1L);

		assertEquals(2, result.size());

	}
	
}
