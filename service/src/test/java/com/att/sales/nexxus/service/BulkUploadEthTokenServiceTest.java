package com.att.sales.nexxus.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Ignore;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.accesspricing.service.AccessPricingServiceImpl;
import com.att.sales.nexxus.admin.model.BulkUploadEthTokenRequest;
import com.att.sales.nexxus.admin.model.UploadEthTokenRequest;
import com.att.sales.nexxus.dao.model.NxAccessPricingData;
import com.att.sales.nexxus.dao.model.NxDataExport;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxAccessPricingDataRepository;
import com.att.sales.nexxus.dao.repository.NxDataExportRepository;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxTeamRepository;
import com.att.sales.nexxus.model.AccessPricingUiRequest;
import com.att.sales.nexxus.model.CircuitSiteDetails;
import com.att.sales.nexxus.model.NexxusOutputRequest;
import com.att.sales.nexxus.util.NxSolutionUserLockUtil;

@ExtendWith(MockitoExtension.class)
public class BulkUploadEthTokenServiceTest {

	@InjectMocks
	private BulkUploadEthTokenService bulkUploadEthTokenService;
	
	@Mock
	private AccessPricingServiceImpl accessPricingService;
	
	@Mock
	private MailService mailService;

	@Mock
	private NxSolutionDetailsRepository solutionRepo;
	
	@Mock
	private NxTeamRepository nxTeamRepository;
	
	@Mock
	private NxDesignAuditRepository nxDesignAuditRepository;
	
	@Mock
	private NxTemplateProcessingService nxTemplateProcessingService;

	@Mock
	private NxAccessPricingDataRepository accessPricingDataRepository;
	
	@Mock
	private NxDataExportRepository nxDataExportRepository;
	
	@Mock
	private Path path;
	
	private String bulkuploadRequestPath="src/main/resources/nexxusTemplate/IGLOO_TOKEN_BULK_UPLOAD_TEMPLATE_V_1.0.xlsx";
	private String downloadBulkuploadRequestPath="src/main/resources/nexxusTemplate/test/testfile.xlsx";

	@Mock
	private NxLookupDataRepository nxLookupDataRepository;

	@Mock
	private  InrQualifyService inrQualifyService;
	
	@Mock
	private NxSolutionUserLockUtil nxSolutionUserLockUtil;

	@Test
	public void testbulkUploadEthTokens() throws SalesBusinessException, FileNotFoundException {
		BulkUploadEthTokenRequest request= new BulkUploadEthTokenRequest();
		request.setNxSolutionId(0L);
		request.setUserId("test");
		InputStream fis = new FileInputStream(bulkuploadRequestPath);
		request.setInputStream(fis);
		NxSolutionDetail nxSolutionDetail= new NxSolutionDetail();
		nxSolutionDetail.setNxSolutionId(1L);
		when(solutionRepo.save(any(NxSolutionDetail.class))).thenReturn(nxSolutionDetail);
		List<NxAccessPricingData> accessPricingDatas=new ArrayList<>();
		NxAccessPricingData nxAccessPricingData = new NxAccessPricingData();
		accessPricingDatas.add(nxAccessPricingData);
		when(accessPricingDataRepository.findByNxSolutionId(anyLong())).thenReturn(accessPricingDatas);
		List<NxLookupData> lookupData= new ArrayList<>();
		NxLookupData nxLookupData = new NxLookupData();
		nxLookupData.setCriteria("Site Ref Id,Circuit ID,Quote ID,New / Existing ?");
		lookupData.add(nxLookupData);
		when(nxLookupDataRepository.findByDatasetNameAndItemId(anyString(),anyString())).thenReturn(lookupData);
		Mockito.doNothing().when(nxSolutionUserLockUtil).updateSolutionLockStatus(anyLong(), anyString());
		bulkUploadEthTokenService.bulkUploadEthTokens(request);
		
		request.setNxSolutionId(1L);
		InputStream fis1 = new FileInputStream(bulkuploadRequestPath);
		request.setInputStream(fis1);
		List<NxSolutionDetail> solutionDetails=new ArrayList<>();
		solutionDetails.add(nxSolutionDetail);
		when(solutionRepo.findByNxSolutionId(anyLong())).thenReturn(solutionDetails.get(0));
		bulkUploadEthTokenService.bulkUploadEthTokens(request);
	}
	
	@Test
	public void testdownloadFailedTokenFile() throws SalesBusinessException {
		NexxusOutputRequest request=new NexxusOutputRequest();
        Path filepath = Paths.get(downloadBulkuploadRequestPath);
		when(nxTemplateProcessingService.getFilePath(anyString())).thenReturn(filepath);
		List<NxDesignAudit> list = new ArrayList<>();
		when(nxDesignAuditRepository.findFailedTokensByTransactionAndNxRefId(anyString(),anyLong())).thenReturn(list);
		NxDesignAudit nxDesignAudit = new NxDesignAudit();
		nxDesignAudit.setData("[\r\n" + 
				"    {\r\n" + 
				"        \"quoteId\": \"66893435.001\",\r\n" + 
				"        \"siteRefId\": \"Cerrito 1136, Retiro, Ciudad de Buenos Aires, Ciudad Autonoma de Buenos Aires, 1010\"\r\n" + 
				"    }\r\n" + 
				"]");
		list.add(nxDesignAudit);
		List<NxAccessPricingData> mpFailList=  new ArrayList<>();
		NxAccessPricingData nxAccessPricingData = new NxAccessPricingData();
		nxAccessPricingData.setNxSolutionId(1L);
		mpFailList.add(nxAccessPricingData);
		when(accessPricingDataRepository.findByNxSolIdAndMpStatus(anyLong(),anyString())).thenReturn(mpFailList);
		bulkUploadEthTokenService.downloadFailedTokenFile(request);

	}
	
	@Test
	public void testvalidationStatus() {
		bulkUploadEthTokenService.validationStatus(new ServiceResponse(),"description", "code","detailedDescription");
	}
	
	@Disabled
	@Test
	public void testSuccessbulkUploadEthTokens() throws SalesBusinessException, FileNotFoundException {
		BulkUploadEthTokenRequest request= new BulkUploadEthTokenRequest();
		request.setNxSolutionId(0L);
		request.setUserId("test");
		InputStream fis = new FileInputStream(bulkuploadRequestPath);
		request.setInputStream(fis);
		NxSolutionDetail nxSolutionDetail= new NxSolutionDetail();
		nxSolutionDetail.setNxSolutionId(1L);
		when(solutionRepo.save(any(NxSolutionDetail.class))).thenReturn(nxSolutionDetail);
		List<NxAccessPricingData> accessPricingDatas=new ArrayList<>();
		NxAccessPricingData nxAccessPricingData = new NxAccessPricingData();
		accessPricingDatas.add(nxAccessPricingData);
		when(accessPricingDataRepository.findByNxSolutionId(anyLong())).thenReturn(accessPricingDatas);
		List<NxLookupData> lookupData= new ArrayList<>();
		NxLookupData nxLookupData = new NxLookupData();
		nxLookupData.setCriteria("Site Ref Id,Circuit ID,Quote ID,New / Existing ?");
		lookupData.add(nxLookupData);
		when(nxLookupDataRepository.findByDatasetNameAndItemId(anyString(),anyString())).thenReturn(lookupData);
	
		bulkUploadEthTokenService.bulkUploadEthTokens(request);
		request.setNxSolutionId(1L);
		InputStream fis1 = new FileInputStream(bulkuploadRequestPath);
		request.setInputStream(fis1);
		List<NxSolutionDetail> solutionDetails=new ArrayList<>();
		solutionDetails.add(nxSolutionDetail);
		when(solutionRepo.findByNxSolutionId(anyLong())).thenReturn(solutionDetails.get(0));
		bulkUploadEthTokenService.bulkUploadEthTokens(request);
		AccessPricingUiRequest req = new AccessPricingUiRequest();
		req.setNxSolutionId(0L);
		req.setAction("saveIglooQuote");	
		request.setUserId("test");
		List<String> Dqids = new ArrayList<>();  
		Dqids.add("ETH200A1TT");       
		Dqids.add("ETH200A1TE"); 
		//List<String> Dudqids = new ArrayList<>();
		Set<String> dupDqIds = new HashSet<>();	
		dupDqIds.add("ETH200NG8F");      
		dupDqIds.add("ETH200NG8F");
		req.setDqId(Dqids);
	//	req.setDupDqId(dupDqIds);
		accessPricingService.getAccessPricing(req);	
		}
	
	@Test
	public void testdownloadIglooTokenFile() throws SalesBusinessException {
		NexxusOutputRequest request=new NexxusOutputRequest();
		List<NxDataExport> nxDataExports = new ArrayList<NxDataExport>();
		NxDataExport nxDataExport = new NxDataExport();
		nxDataExport.setVariableName("ethToken");
		nxDataExport.setDisplayName("TOKEN_ID");
		nxDataExports.add(nxDataExport);
		when(nxDataExportRepository.getNxDataExport(anyString(), anyString())).thenReturn(nxDataExports);
		
		List<NxAccessPricingData> nxAccessPricingDataList = new ArrayList<NxAccessPricingData>();
		NxAccessPricingData nxAccessPricingData = new NxAccessPricingData();
		nxAccessPricingData.setEthToken("ETH10289");
		nxAccessPricingDataList.add(nxAccessPricingData);
		when(accessPricingDataRepository.findByNxSolutionId(anyLong())).thenReturn(nxAccessPricingDataList);
		Path filepath = Paths.get(downloadBulkuploadRequestPath);
		when(nxTemplateProcessingService.getFilePath(anyString())).thenReturn(filepath);
		bulkUploadEthTokenService.downloadIglooTokenFile(request);

	}
	
	@Test
	public void testSetIglooSiteID() {
		Map<String, List<CircuitSiteDetails>> cktSiteMap= new LinkedHashMap<String, List<CircuitSiteDetails>>();
		CircuitSiteDetails csd1= new CircuitSiteDetails();
		csd1.setId(1L);
		CircuitSiteDetails csd2= new CircuitSiteDetails();
		csd2.setId(2L);
		List<CircuitSiteDetails> csdList=new ArrayList<>();
		csdList.add(csd1);
		csdList.add(csd2);
		cktSiteMap.put("ckt1",csdList);
		when(inrQualifyService.prepareInrCktSiteMap(anyLong())).thenReturn(cktSiteMap);
		
		List<UploadEthTokenRequest>	bulktokenListrequest = new ArrayList<>();
		UploadEthTokenRequest uploadEthTokenRequest= new UploadEthTokenRequest();
		uploadEthTokenRequest.setCircuitId("ckt1");
		bulktokenListrequest.add(uploadEthTokenRequest);
		bulkUploadEthTokenService.setIglooSiteID(bulktokenListrequest, 1L);
	}
}
