package com.att.sales.nexxus.dao.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SalesMsDaoTest {

	@Mock
	private EntityManager em;

	@InjectMocks
	private SalesMsDao salesMsDao;
	
	@Mock
	Query query;
	
	@Test
	public void testGetOfferNameByOfferId() {
		when(em.createNativeQuery(any())).thenReturn(query);
		List<String> resultList = new ArrayList<>();
		resultList.add("test");
		
		when(query.getResultList()).thenReturn(resultList);

		String result = salesMsDao.getOfferNameByOfferId(1);
		assertEquals("test", result);
	}

	@Test
	public void testGetOfferNameByOfferIdNull() {
		when(em.createNativeQuery(any())).thenReturn(query);
		List<String> resultList = new ArrayList<>();
		
		when(query.getResultList()).thenReturn(resultList);

		String result = salesMsDao.getOfferNameByOfferId(1);
		assertSame(null, result);
	}

	@Test
	public void testGetOfferIdByOfferName() {
		when(em.createNativeQuery(any())).thenReturn(query);
		List<String> resultList = new ArrayList<>();
		resultList.add("test");
		
		when(query.getResultList()).thenReturn(resultList);

		String result = salesMsDao.getOfferIdByOfferName("inr");
		assertEquals("test", result);
	}
	
	@Test
	public void testGetOfferIdByOfferNameNull() {
		when(em.createNativeQuery(any())).thenReturn(query);
		List<String> resultList = new ArrayList<>();
		
		when(query.getResultList()).thenReturn(resultList);

		String result = salesMsDao.getOfferIdByOfferName("inr");
		assertSame(null, result);
	}
}
