package com.att.sales.nexxus.dao.repository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NexxusPricingDaoTest {

	@Mock
	private EntityManager em;

	@InjectMocks
	private NexxusPricingDao nexxusPricingDao;
	
	@Mock
	Query query;

	@Test
	public void testGetUdfValue() {
		when(em.createNativeQuery(any())).thenReturn(query);
		when(query.getSingleResult()).thenReturn("result");
		String result=nexxusPricingDao.getUdfValue(1L);
		assertEquals("result",result);
	}
	
	@Test
	public void testGetAttributeValue() {
		when(em.createNativeQuery(any())).thenReturn(query);
		when(query.getSingleResult()).thenReturn("result");
		String result=nexxusPricingDao.getAttributeValue(1L);
		assertEquals("result",result);
		
	}
	
}
