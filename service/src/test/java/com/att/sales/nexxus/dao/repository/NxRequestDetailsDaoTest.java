package com.att.sales.nexxus.dao.repository;


import java.sql.Date;

import javax.persistence.EntityManager;

import javax.persistence.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class NxRequestDetailsDaoTest {

	@InjectMocks
	NxRequestDetailsDao nxRequestDetailsDao;
	
	@Mock
	private EntityManager em;
	
	@Mock
	private Query query;
	
	@Test
	public void testUpdateNoDmaapNotificationStatus() {
		Long noDmaapStatus = 12345L;
		Long inProgressStatus = 12345L;
		Date dateThreshold = new Date(inProgressStatus);
		Mockito.when(em.createQuery("UPDATE NxRequestDetails SET status = :noDmaapStatus "
				+ "WHERE status = :inProgressStatus AND createdDate < :dateThreshold")).thenReturn(query);
		nxRequestDetailsDao.updateNoDmaapNotificationStatus(noDmaapStatus, inProgressStatus, dateThreshold);
		
	}

}
