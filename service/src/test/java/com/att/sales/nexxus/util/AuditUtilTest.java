package com.att.sales.nexxus.util;

import static org.mockito.Mockito.any;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.AuditTrailConstants;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.solution.NxUiAudit;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxUiAuditRepository;

@ExtendWith(MockitoExtension.class)
	public class AuditUtilTest {
		@InjectMocks
		private AuditUtil util;
		@Mock
		NxLookupData nxLookupData;
		@Mock
		private NxLookupDataRepository nxLookupDataRepository;
		@Mock
		private NxUiAuditRepository nxUiAuditRepository;
		
		@Test
		public void testAddActionToNxUiAudit() throws SalesBusinessException {
			when(nxLookupDataRepository.findByItemIdAndDatasetNameAndCriteria(any(),any(),any())).thenReturn(nxLookupData);
			NxUiAudit nxUiAudit=new NxUiAudit();
			nxUiAudit.setNxSolutionId(1l);
			when(nxUiAuditRepository.saveAndFlush(nxUiAudit)).thenReturn(nxUiAudit);
//			util.addActionToNxUiAudit(any(),any(),any(),any(),any(),any());
			Long nxSolutioId = 1l;
			String action = AuditTrailConstants.USER_ADD;
			String actionPerformedBy = null;
			String status=AuditTrailConstants.SUCCESS;
			Long sourceSolId=null;
			String attid=null;
			Long executionTime = 1l;
			String additionalMessage=null;
			util.addActionToNxUiAudit(nxSolutioId, action, actionPerformedBy, status, sourceSolId, attid, executionTime, additionalMessage);
			
		}

}
