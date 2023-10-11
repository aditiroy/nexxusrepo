package com.att.sales.nexxus.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxLineItemProcessingDao;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxOutputFileRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;

/**
 * The Class FmoProcessingRepoServiceTest.
 */
/**
 * @author vt393d
 *
 */
@ExtendWith(MockitoExtension.class)
public class FmoProcessingRepoServiceTest {

	@Spy
	@InjectMocks
	private FmoProcessingRepoService fmoProcessingRepoService;
	
	@Mock
	private NxLineItemProcessingDao lineItemProcessingDao;
	
	@Mock
	private NxOutputFileRepository nexusOutputFileRepository;
	
	@Mock
	private NxRequestDetailsRepository nxRequestDetailsRepository;
	
	@Mock
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;
	
	@Mock
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	@Mock
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Test
	public void saveNxOutputFileTest() {
		fmoProcessingRepoService.saveNxOutputFile(new NxOutputFileModel());
	}
	
	@Test
	public void saveNxRequestDetailsTest() {
		fmoProcessingRepoService.saveNxRequestDetails(new NxRequestDetails());
	}
	
	@Test
	public void getFmoRulesFromTblTest() {
		fmoProcessingRepoService.getFmoRulesFromTbl(new HashSet<>());
	}
	
	@Test
	public void getDataFromLookUpTblTest() {
		List<Object> result= new ArrayList<Object>();
		result.add("s");
		when(lineItemProcessingDao.getDataFromSalesLookUpTbl(any(),any(),any(),any())).thenReturn(result);
		fmoProcessingRepoService.getDataFromSalesLookUpTbl(new Object(), 1l, 2l, 3l);
	}
	
	@Test
	public void getDataFromLookUpTblTest2() {
		List<Object> result= new ArrayList<Object>();
		when(lineItemProcessingDao.getDataFromSalesLookUpTbl(any(),any(),any(),any())).thenReturn(result);
		fmoProcessingRepoService.getDataFromSalesLookUpTbl(new Object(), 1l, 2l, 3l);
	}
	
	@Test
	public void getNexxusLineItemLookUpItems() {
		fmoProcessingRepoService.getNxLineItemFieldDataByOfferId(1l, "test");
	}
	
	@Test
	public void loadNexxusKeyPathDataTest() {
		fmoProcessingRepoService.loadNexxusKeyPathData();
	}
	
	@Test
	public void getListItemDataByBeIdTest() {
		fmoProcessingRepoService.getLineItemData("a", "b", "c", "d");
	}
	
	@Test
	public void getListItemDataByBeIdTest2() {
		fmoProcessingRepoService.getLineItemData("", "b", "c", "d");
	}
	
	@Test
	public void getLineItemDataTest() {
		fmoProcessingRepoService.getLineItemData("a", "b");
	}
	@Test
	public void testgetNxOutputFileModel() {
		Long requestId=new Long(2l);
		List<NxOutputFileModel> nxOutputFileModels=new ArrayList<>();
		NxOutputFileModel model=new NxOutputFileModel();
		model.setFallOutData("fallOutData");
		model.setFileName("fileName");
		model.setFileType("fileType");
		model.setId(new Long(3l));
		model.setIntermediateJson("intermediateJson");
		nxOutputFileModels.add(model);
		Mockito.when(nexusOutputFileRepository.findByNxReqId(Mockito.anyLong())).thenReturn(nxOutputFileModels);
		fmoProcessingRepoService.getNxOutputFileModel(requestId);
		
	}
	@Test
	public void testgetNxOutputFileModelNull() {
		Long requestId=new Long(2l);
		List<NxOutputFileModel> nxOutputFileModels=null;
		//NxOutputFileModel model=new NxOutputFileModel();
		/*model.setFallOutData("fallOutData");
		model.setFileName("fileName");
		model.setFileType("fileType");
		model.setId(new Long(3l));
		model.setIntermediateJson("intermediateJson");
		nxOutputFileModels.add(model);*/
		Mockito.when(nexusOutputFileRepository.findByNxReqId(Mockito.anyLong())).thenReturn(nxOutputFileModels);
		fmoProcessingRepoService.getNxOutputFileModel(requestId);
		
	}
	
	@Test
	public void getDataFromIms2LookUpTbl() {
		List<Object> result= new ArrayList<Object>();
		result.add("s");
		when(lineItemProcessingDao.getDataFromIms2LookUpTbl(any(),any(),any(),any())).thenReturn(result);
		fmoProcessingRepoService.getDataFromIms2LookUpTbl(new Object(), 1l, 2l, 3l);
	}
	
	@Test
	public void getDataFromIms2LookUpTbl2() {
		List<Object> result= new ArrayList<Object>();
		when(lineItemProcessingDao.getDataFromIms2LookUpTbl(any(),any(),any(),any())).thenReturn(result);
		fmoProcessingRepoService.getDataFromIms2LookUpTbl(new Object(), 1l, 2l, 3l);
	}
	
	@Test
	public void getNxLineItemFieldDataByOfferName() {
		fmoProcessingRepoService.getNxLineItemFieldDataByOfferName("a", "d");
	}
	
	@Test
	public void saveSolutionDetails() {
		fmoProcessingRepoService.saveSolutionDetails(new NxSolutionDetail());
	}
	
	@Test
	public void getLookupDataByIdTest() {
		List<NxLookupData> resultLst=new ArrayList<NxLookupData>();
		NxLookupData v=new NxLookupData();
		resultLst.add(v);
		when(lineItemProcessingDao.getNxLookupDataById(any(),any())).thenReturn(resultLst);
		fmoProcessingRepoService.getLookupDataById("A", "g");
	}
	
	@Test
	public void getLookupDataByIdTest2() {
		List<NxLookupData> resultLst=new ArrayList<NxLookupData>();
		when(lineItemProcessingDao.getNxLookupDataById(any(),any())).thenReturn(resultLst);
		fmoProcessingRepoService.getLookupDataById("A", "g");
	}
	
	@Test
	public void getDataFromNxLookUpTest() {
		List<NxLookupData> resultLst=new ArrayList<NxLookupData>();
		when(lineItemProcessingDao.getNxLookupDataById(any(),any())).thenReturn(resultLst);
		Map<String,NxLookupData> resultMap=new HashMap<String, NxLookupData>();
		resultMap.put("d", new NxLookupData());
		when(nxMyPriceRepositoryServce.getLookupDataByItemId(any())).thenReturn(resultMap);
		fmoProcessingRepoService.getDataFromNxLookUp("d", "h");
	}
	
	@Test
	public void getDataFromNxLookUpTest2() {
		List<NxLookupData> resultLst=new ArrayList<NxLookupData>();
		when(lineItemProcessingDao.getNxLookupDataById(any(),any())).thenReturn(resultLst);
		Map<String,NxLookupData> resultMap=new HashMap<String, NxLookupData>();
		when(nxMyPriceRepositoryServce.getLookupDataByItemId(any())).thenReturn(resultMap);
		fmoProcessingRepoService.getDataFromNxLookUp("d", "h");
	}
	
	@Test
	public void getDataFromNxLooUpByDatasetName() {
		fmoProcessingRepoService.getDataFromNxLooUpByDatasetName("fgfhg");
	}
	
	@Test
	public void getDataFromNxLooUpByDatasetName2() {
		fmoProcessingRepoService.getDataFromNxLooUpByDatasetName(null);
	}
}
