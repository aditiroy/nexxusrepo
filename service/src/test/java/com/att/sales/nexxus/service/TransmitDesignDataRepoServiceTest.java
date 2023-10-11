package com.att.sales.nexxus.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxUdfMapping;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxUdfMappingDao;
import com.att.sales.nexxus.dao.repository.SalesMsProdcompRepository;

@ExtendWith(MockitoExtension.class)
public class TransmitDesignDataRepoServiceTest {

	@InjectMocks
	private TransmitDesignDataRepoService transmitDesignDataRepoService;
	
	@Mock
	private NxSolutionDetailsRepository nxSolutionDetailsRepo;
	
	@Mock
	private NxDesignAuditRepository nxDesignAuditRepo;
	
	@Mock
	private NxUdfMappingDao nxUdfMappingDao;
	
	@Mock
	private NxLookupDataRepository nxLookupDataRepo;
	
	@Mock
	private SalesMsProdcompRepository salesMsProdcompRepo;
	
	@Test
	public void testSaveSolutionDetails() {
		NxSolutionDetail entity = new NxSolutionDetail();
		transmitDesignDataRepoService.saveSolutionDetails(entity);
	}

	@Test
	public void testFindByExternalKey() {
		List<NxSolutionDetail> nxSolutionDetailList= new ArrayList<>();
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setNxSolutionId(1L);
		nxSolutionDetailList.add(nxSolutionDetail);
		when(nxSolutionDetailsRepo.findByExternalKey(anyLong())).thenReturn(nxSolutionDetailList);
		List<NxSolutionDetail> result=transmitDesignDataRepoService.findByExternalKey(1L);
		assertSame(nxSolutionDetailList, result);
	}
	
	@Test
	public void testGetUdfDataMap() {
		List<NxUdfMapping> resultList=new ArrayList<>();
		NxUdfMapping nxUdfMapping = new NxUdfMapping();
		nxUdfMapping.setComponentId(1L);
		nxUdfMapping.setNxUdfMappingId(1L);
		nxUdfMapping.setOfferId(120L);
		nxUdfMapping.setOfferName("ADE");
		nxUdfMapping.setRuleSet("aseDppRequest");
		nxUdfMapping.setUdfAbbr("asrItemId");
		nxUdfMapping.setUdfAttributeDatasetName("aseDppRequest");
		nxUdfMapping.setUdfId(22211L);
		resultList.add(nxUdfMapping);
		when(nxUdfMappingDao.getNxUdfDataByOfferIdAndRule(anyString(),anyLong())).thenReturn(resultList);
		Map<Long,Map<String,NxUdfMapping>> expectedResult= new HashMap<>();
		Map<String,NxUdfMapping> nxUdfMappingVal= new HashMap<>();
		nxUdfMappingVal.put("asrItemId", nxUdfMapping);
		expectedResult.put(1L, nxUdfMappingVal);
		
		Map<Long,Map<String,NxUdfMapping>> result=transmitDesignDataRepoService.getUdfDataMap("aseDppRequest", 120L);
		assertEquals(expectedResult.get(1L).get("asrItemId").getOfferId(),
				result.get(1L).get("asrItemId").getOfferId());
		
	}
	
	@Test
	public void testsaveAuditData() {
		NxDesignAudit entity = new NxDesignAudit();
		transmitDesignDataRepoService.saveAuditData(entity);
	}
	
	@Test
	public void testFindTopByDatasetNameAndDescription() {
		NxLookupData nxLookupData = new NxLookupData();
		nxLookupData.setDatasetName("STATUS");
		when(nxLookupDataRepo.findTopByDatasetNameAndDescription(anyString(),anyString())).thenReturn(nxLookupData);
		NxLookupData result=transmitDesignDataRepoService.findTopByDatasetNameAndDescription("STATUS","status");
		assertSame(nxLookupData,result);
	}
	
	@Test
	public void testgetUdfAttrIdFromSalesTbl() {
		List<Long> udfAttrIDList = new ArrayList<>();
		udfAttrIDList.add(22211L);
		when(salesMsProdcompRepo.getUdfAttrIdFromSalesTbl(anyLong(),anyLong(),anyLong(),anyString())).
		thenReturn(udfAttrIDList);
		Long result=transmitDesignDataRepoService.getUdfAttrIdFromSalesTbl(120L, 1210L, 22211L, "kmzMapRequestInd");
		assertEquals(22211,result.longValue());
	}
	
	@Test
	public void testgetPedStatusMap() {
		List<NxLookupData> lookUpDataList= new ArrayList<NxLookupData>();
		NxLookupData nxLookupData = new NxLookupData();
		nxLookupData.setItemId("S");
		nxLookupData.setDatasetName("STATUS");
		nxLookupData.setDescription("Submitted");
		lookUpDataList.add(nxLookupData);
		when(nxLookupDataRepo.findByDatasetName(anyString())).thenReturn(lookUpDataList);
		Map<String,NxLookupData> result=transmitDesignDataRepoService.getPedStatusMap();
		assertEquals(result.get("S").getDatasetName(), "STATUS");
	}
	
	
}

