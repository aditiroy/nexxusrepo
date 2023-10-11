package com.att.sales.nexxus.nxPEDstatus.service;

import static org.mockito.Mockito.anyLong;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.dao.repository.NxDesignRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.nxPEDstatus.model.GetNxPEDStatusRequest;

@ExtendWith(MockitoExtension.class)
public class GetNxPEDStatusServiceImplTest {
	
	@InjectMocks
	GetNxPEDStatusServiceImpl getNxPEDStatusServiceImpl;
	
	@Mock
	NxSolutionDetailsRepository nxSolutionDetailsRepository;
	
	@Mock
	NxDesignRepository nxDesignRepository;
	
	@Test
	public void testGetnXPEDStatus() {
		GetNxPEDStatusRequest request = new GetNxPEDStatusRequest();
		request.setSolutionId("1010");
		Mockito.when(nxSolutionDetailsRepository.findSolutionByExternalKey(anyLong())).thenReturn(1010L);
		List<Object[]> obj = new ArrayList<Object[]>();
		Object[] o = new Object[2];
		o[0] ="100";
		o[1] ="101";
		obj.add(o);
		Mockito.when(nxDesignRepository.findDesignIdsNbundleCdByNxSolutionId(anyLong())).thenReturn(obj);
		getNxPEDStatusServiceImpl.getnXPEDStatus(request);
		
	}
}
