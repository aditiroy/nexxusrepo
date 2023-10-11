package com.att.sales.nexxus.service;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.dao.model.solution.NxUiAudit;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxUiAuditRepository;
import com.att.sales.nexxus.model.NexxusSolActionRequest;

@ExtendWith(MockitoExtension.class)
public class NexxusSolutionActionServiceImplTest {	
	@InjectMocks
	private NexxusSolutionActionServiceImpl nexxusSolutionActionServiceImpl;
	
	@Mock
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;
	@Mock
	private NxUiAuditRepository nxUiAuditRepository;
	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
	} 
	
	@Test
	public void testNexxusSolutionActionTest() {
		NexxusSolActionRequest request = new NexxusSolActionRequest();
		when(nxSolutionDetailsRepository.updateArchivedSolutionId(anyString(),anyLong())).thenReturn(7878);
		List<NxUiAudit> nxUiAuditList=new ArrayList<>();
		when(nxUiAuditRepository.findByNxSolutionIdandActionType(anyLong(),anyString())).thenReturn(nxUiAuditList);
		nexxusSolutionActionServiceImpl.nexxusSolutionAction(request);
	}
}
