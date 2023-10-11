package com.att.sales.nexxus.dao.repository;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.dao.model.NexxusPreviewInrUIModel;

@ExtendWith(MockitoExtension.class)
public class NxPreviewInrJsonDaoTest {

	@Mock
	private EntityManager em;

	@InjectMocks
	NxPreviewInrJsonDao nxPreviewInrJsonDao;
	
	@Mock
	TypedQuery<NexxusPreviewInrUIModel> query;
	

	@Test
	public void testGetIntermediateJson() {
		when(em.createNamedQuery(any(), eq(NexxusPreviewInrUIModel.class))).thenReturn(query);
		List<NexxusPreviewInrUIModel> resultlist =new ArrayList<>();
		NexxusPreviewInrUIModel nexxusPreviewInrUIModelObj1 = new NexxusPreviewInrUIModel();
		nexxusPreviewInrUIModelObj1.setCustomerName("customerName1");
		nexxusPreviewInrUIModelObj1.setDunsNumber("46546");
		
		NexxusPreviewInrUIModel nexxusPreviewInrUIModelObj2 = new NexxusPreviewInrUIModel();
		nexxusPreviewInrUIModelObj2.setCustomerName("customerName2");
		nexxusPreviewInrUIModelObj2.setDunsNumber("465656546");
		
		resultlist.add(nexxusPreviewInrUIModelObj1);
		resultlist.add(nexxusPreviewInrUIModelObj2);
		
		when(query.getResultList()).thenReturn(resultlist);

		List<NexxusPreviewInrUIModel> result=nxPreviewInrJsonDao.getIntermediateJson(56L);
		assertSame(nexxusPreviewInrUIModelObj1,result.get(0));
		assertSame(nexxusPreviewInrUIModelObj2,result.get(1));

	}
	
}
