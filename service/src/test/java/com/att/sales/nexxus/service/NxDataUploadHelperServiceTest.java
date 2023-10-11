package com.att.sales.nexxus.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.admin.model.DataUploadRequest;
import com.att.sales.nexxus.admin.model.ExcelReader;
import com.att.sales.nexxus.admin.model.ExcelValueConfig;
import com.att.sales.nexxus.constant.DataUploadConstants;
import com.att.sales.nexxus.dao.model.LittleProductDataEntity;
import com.att.sales.nexxus.dao.model.LookupDataMapping;
import com.att.sales.nexxus.dao.model.NxLineItemLookUpDataModel;
import com.att.sales.nexxus.dao.model.NxLineItemLookUpStagingModel;
import com.att.sales.nexxus.dao.model.TopProductDataEntity;
import com.att.sales.nexxus.dao.repository.DataUploadLookupRepository;
import com.att.sales.nexxus.dao.repository.LittleProductRepo;
import com.att.sales.nexxus.dao.repository.NxAdminAuditDataRepository;
import com.att.sales.nexxus.dao.repository.NxLineItemLookUpStagingRepository;

@ExtendWith(MockitoExtension.class)
public class NxDataUploadHelperServiceTest {

	@InjectMocks
	private NxDataUploadHelperService nxDataUploadHelperService;
	
	@Mock
	private DataUploadLookupRepository dataUploadLookupRepo;
	
	@Mock
	private ExcelReader excelReader;
	
	@Mock
	private NxLineItemLookUpStagingRepository nxLineItemLookUpStagingRepo;
	
	@Mock
	private LittleProductRepo littleProductRepo;
	
	@Mock
	private NxAdminAuditDataRepository nxAdminAuditDataRepository;
		
	@Mock
	private MailService mailService;

	private String eEdfBulkuploadRequestPath="src/main/resources/nexxusTemplate/test/testEDFBulkUploadTemplate.xlsx";

	@Test
	public void testuploadDataInStaggingTable() {
		Map<String,Object> inputmap= new HashMap<>();
		List<NxLineItemLookUpStagingModel> inputData = new ArrayList<>();
		List<NxLineItemLookUpStagingModel> savedEntities= new ArrayList<>();
		NxLineItemLookUpStagingModel nxLineItemLookUpStagingModel = new NxLineItemLookUpStagingModel();
		nxLineItemLookUpStagingModel.setNxItemId(1L);
		savedEntities.add(nxLineItemLookUpStagingModel);
		when(nxLineItemLookUpStagingRepo.bulkSave(anyList())).thenReturn(savedEntities);
		nxDataUploadHelperService.uploadDataInStaggingTable(inputmap, inputData);
		inputmap.put(DataUploadConstants.LITTLE_PROD_ID, 1L);
		inputmap.put(DataUploadConstants.TOP_PROD_ID, 1L);
		inputmap.put(DataUploadConstants.TOP_PROD_ID, 1L);
		inputmap.put(DataUploadConstants.USER_ID, "userId");
		inputmap.put(DataUploadConstants.ID_RANGE, "idRange");
		nxDataUploadHelperService.uploadDataInStaggingTable(inputmap, inputData);
	}
	
	@Test
	public void testdeleteStaggingData() {
		Map<String, Object> inputmap = new HashMap<>();
		List<Long> deleteDataIds = new ArrayList<>();
		deleteDataIds.add(1L);
		when(nxLineItemLookUpStagingRepo.getIdByLittleAndTopProdId(anyLong(), anyLong())).thenReturn(deleteDataIds);
		doNothing().when(nxLineItemLookUpStagingRepo).deleteRecordByIds(anyLong(),anyLong());
		nxDataUploadHelperService.deleteStaggingData(inputmap);
		inputmap.put(DataUploadConstants.LITTLE_PROD_ID,1L);
		inputmap.put(DataUploadConstants.TOP_PROD_ID, 1L);
		nxDataUploadHelperService.deleteStaggingData(inputmap);
	}
	
	@Test
	public void testreadExcelFile() throws FileNotFoundException, SalesBusinessException {
		InputStream inputFile = new FileInputStream(eEdfBulkuploadRequestPath);
		LittleProductDataEntity data = new LittleProductDataEntity();
		data.setLittleId(1L);
		List<LookupDataMapping> mappingDataLst=new ArrayList<>();
		LookupDataMapping lookupDataMapping =  new LookupDataMapping();
		lookupDataMapping.setFlowType("INR");
		mappingDataLst.add(lookupDataMapping);
		when(dataUploadLookupRepo.findByLittleId(anyLong())).thenReturn(mappingDataLst);
		Map<String, List<ExcelValueConfig[]>> excelRowValuesMap = new HashMap<>();
		List<ExcelValueConfig[]> excelValConfigList= new ArrayList<>();
		ExcelValueConfig[] excelValueConfigArr = new ExcelValueConfig[1];
		ExcelValueConfig excelValueConfig = new ExcelValueConfig();
		excelValueConfigArr[0]=excelValueConfig;
		excelValConfigList.add(excelValueConfigArr);
		excelRowValuesMap.put(DataUploadConstants.FILE_DATA, excelValConfigList);
		when(excelReader.getInputExcelRowValues(any(),any(),any())).thenReturn(excelRowValuesMap);
		Map<String,Object> inputmap=new HashMap<>();
		nxDataUploadHelperService.readExcelFile(inputFile, data,NxLineItemLookUpStagingModel.class,inputmap);
	}
	
	@Test
	public void testsaveDataInAdminAuditTable() {
		Map<String,Object> inputmap=new HashMap<>();
		String description="description";
		nxDataUploadHelperService.saveDataInAdminAuditTable(inputmap,description);
		inputmap.put(DataUploadConstants.LITTLE_PROD_ID, 1L);
		inputmap.put(DataUploadConstants.TOP_PROD_ID, 1L);
		inputmap.put(DataUploadConstants.USER_ID, "userId");
		inputmap.put(DataUploadConstants.ID_RANGE, "idRange");
		nxDataUploadHelperService.saveDataInAdminAuditTable(inputmap,description);
	}
	
	@Test
	public void testuploadDataFile() throws FileNotFoundException {
		DataUploadRequest request=new DataUploadRequest();
		request.setId("1");
		request.setAction("action");
		request.setActivity(DataUploadConstants.ACTIVE);
		request.setUserId("userId");
		LittleProductDataEntity littleProductDataEntity=new LittleProductDataEntity();
		littleProductDataEntity.setLittleProductId(1L);
		TopProductDataEntity topProductData  = new TopProductDataEntity();
		topProductData.setTopProductId(1L);
		littleProductDataEntity.setTopProductData(topProductData);
		when(littleProductRepo.findByLittleId(anyLong())).thenReturn(littleProductDataEntity);
		List<NxLineItemLookUpDataModel> savedEntities=new ArrayList<>();
		NxLineItemLookUpDataModel nxLineItemLookUpDataModel = new NxLineItemLookUpDataModel();
		nxLineItemLookUpDataModel.setNxItemId(1L);
		savedEntities.add(nxLineItemLookUpDataModel);
		when(nxLineItemLookUpStagingRepo.activateLineItemData(anyMap())).thenReturn(savedEntities);
		nxDataUploadHelperService.uploadDataFile(request);
		
		DataUploadRequest request1=new DataUploadRequest();
		request.setId("1");
		request.setAction("action");
		request.setUserId("userId");
		InputStream inputFile = new FileInputStream(eEdfBulkuploadRequestPath);
		request.setInputStream(inputFile);
		when(excelReader.getInputExcelRowValues(any(),any(),any())).thenReturn(new HashMap<>());
		nxDataUploadHelperService.uploadDataFile(request1);

	}
}
