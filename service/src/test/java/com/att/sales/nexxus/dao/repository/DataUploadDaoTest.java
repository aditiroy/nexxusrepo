
package com.att.sales.nexxus.dao.repository;

import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.constant.DataUploadConstants;

@ExtendWith(MockitoExtension.class)
public class DataUploadDaoTest {

	@Mock
	private EntityManager em;

	@InjectMocks
	DataUploadDao dataUploadDao;

	@Mock
	StoredProcedureQuery storedProcedure;
	
	@Test
	public void testActiveLineItemDataSuccess() {

		Map<String, Object> inputmap = new HashMap<>();
		inputmap.put(DataUploadConstants.LITTLE_PROD_ID, new Integer(544));
		inputmap.put(DataUploadConstants.TOP_PROD_ID, new Integer(566)); //
	
		when(em.createStoredProcedureQuery(Mockito.anyString())).thenReturn(storedProcedure);
		when(storedProcedure.registerStoredProcedureParameter(Mockito.anyString(),Mockito.any(),
				Mockito.any(ParameterMode.class))).thenReturn(storedProcedure);
		String resultStatus = "SUCCESS";
		String unMatchdata = "Description"; //
		when(storedProcedure.getOutputParameterValue("p_out_status")).thenReturn(resultStatus); //
		when(storedProcedure.getOutputParameterValue("p_out_unmatch_data")).thenReturn(unMatchdata);
		dataUploadDao.activeLineItemData(inputmap);

	}
	
	@Test
	public void testActiveLineItemDataFail() {
		Map<String, Object> inputmap = new HashMap<>();
	
		when(em.createStoredProcedureQuery(Mockito.anyString())).thenReturn(storedProcedure);
		when(storedProcedure.registerStoredProcedureParameter(Mockito.anyString(),Mockito.any(),
				Mockito.any(ParameterMode.class))).thenReturn(storedProcedure);
		String resultStatus = null;
		String unMatchdata = null; //
		when(storedProcedure.getOutputParameterValue("p_out_status")).thenReturn(resultStatus); //
		when(storedProcedure.getOutputParameterValue("p_out_unmatch_data")).thenReturn(unMatchdata);
		dataUploadDao.activeLineItemData(inputmap);

	}

}
