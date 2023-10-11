package com.att.sales.nexxus.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.NxRequestGroup;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxOutputFileRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxRequestGroupRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxTeamRepository;
import com.att.sales.nexxus.edfbulkupload.EdfManBulkUploadRequest;
import com.att.sales.nexxus.edfbulkupload.UploadMANbulkRequest;
import com.att.sales.nexxus.model.NexxusOutputRequest;
import com.att.sales.nexxus.util.DME2RestClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@ExtendWith(MockitoExtension.class)
public class BulkUploadEdfServiceTest {

	@InjectMocks
	private BulkUploadEdfService bulkUploadEdfService;

	@Mock
	private NxSolutionDetailsRepository nxSolutionDetailsRepo;

	@Mock
	private NxTeamRepository nxTeamRepository;

	@Mock
	private NxRequestGroupRepository nxRequestGroupRepository;

	/** The dme. */
	@Mock
	private DME2RestClient dme;

	@Mock
	private ObjectMapper mapper;

	@Mock
	private NxTemplateProcessingService nxTemplateProcessingService;

	@Mock
	private NxRequestDetailsRepository nxRequestDetailsRepository;

	@Mock
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Mock
	private NxOutputFileRepository nxOutputFileRepository;

	private String downloadEdfBulkuploadRequestPath="src/main/resources/nexxusTemplate/test/testfile.xlsx";
	private String eEdfBulkuploadRequestPath="src/main/resources/nexxusTemplate/test/testEDFBulkUploadTemplate.xlsx";

	ObjectMapper realmapper= new ObjectMapper();

	@BeforeEach
	public void setUp() {
		ReflectionTestUtils.setField(bulkUploadEdfService, "requestsToEdfChunkSize", "1");
		ReflectionTestUtils.setField(bulkUploadEdfService, "failedDmaapMsgRequest", "nx.output.template.path");
	}
	@Test
	public void testSaveNxRequestGroup() {
		NxRequestDetails nxRequestDetails2 = new NxRequestDetails();
		NxLookupData nxLookupData = new NxLookupData();
		nxLookupData.setItemId("1");
		nxLookupData.setDescription("description");
		nxLookupData.setDatasetName("datasetName");
		nxLookupData.setCriteria("criteria");
		Long nxSolutionId = 1L;
		String groupStatus = "Submitted";
		List<NxRequestGroup> nxRequestGroups = new ArrayList<>();
		Mockito.when(
				nxRequestGroupRepository.findByNxSolutionIdAndGroupIdAndActiveYn(anyLong(), anyLong(), anyString()))
				.thenReturn(nxRequestGroups);
		bulkUploadEdfService.saveNxRequestGroup(nxRequestDetails2, nxLookupData, nxSolutionId, groupStatus);

		NxRequestGroup nxRequestGroupObj = new NxRequestGroup();
		nxRequestGroupObj.setGroupId(1L);
		nxRequestGroups.add(nxRequestGroupObj);
		Mockito.when(
				nxRequestGroupRepository.findByNxSolutionIdAndGroupIdAndActiveYn(anyLong(), anyLong(), anyString()))
				.thenReturn(nxRequestGroups);
		bulkUploadEdfService.saveNxRequestGroup(nxRequestDetails2, nxLookupData, nxSolutionId, groupStatus);

	}
	@Disabled
	@Test
	public void testProcessAndVadidateAccountData() throws SalesBusinessException {
		List<UploadMANbulkRequest> bulkRequestsListToUpload = new ArrayList<>();
		UploadMANbulkRequest uploadMANbulkRequest = new UploadMANbulkRequest();
		uploadMANbulkRequest.setBeginBillMonth("062019");
		uploadMANbulkRequest.setBillMonth("092019");
		uploadMANbulkRequest.setCpniApprover("cpniApprover");
		uploadMANbulkRequest.setCustomerName("customerName");
		uploadMANbulkRequest.setManAccountNumber("10012305685");
		uploadMANbulkRequest.setMcnNumber("576585");
		uploadMANbulkRequest.setProduct("ABN");
		uploadMANbulkRequest.setType("MAN");
		uploadMANbulkRequest.setUsageOrNonUsageIndicator("Y");
		bulkRequestsListToUpload.add(uploadMANbulkRequest);
		String userId="asd";
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setNxSolutionId(1L);
		NxLookupData nxLookupData = new NxLookupData();
		nxLookupData.setItemId("1");
		nxLookupData.setDescription("description");
		nxLookupData.setDatasetName("ACCESS_GROUP");
		nxLookupData.setCriteria("criteria");
		Mockito.when(nxLookupDataRepository.findByDescriptionAndDatasetName(anyString(),anyList())).thenReturn(nxLookupData);
		List<NxRequestGroup> nxRequestGroups = new ArrayList<>();
		Mockito.when(
				nxRequestGroupRepository.findByNxSolutionIdAndGroupIdAndActiveYn(anyLong(), anyLong(), anyString()))
				.thenReturn(nxRequestGroups);
		Mockito.when(nxLookupDataRepository.findByDatasetNameAndDescription(anyString(),anyString())).thenReturn(nxLookupData);
		bulkUploadEdfService.processAndVadidateAccountData(bulkRequestsListToUpload,  userId, nxSolutionDetail);
	}
	
	@Test
	public void testgenerateFailedDmapMsgReport() throws IOException, SalesBusinessException {
		NexxusOutputRequest request= new NexxusOutputRequest();
		request.setRequestId(1L);
		NxRequestDetails nxRequestDetails= new NxRequestDetails();
		nxRequestDetails.setProduct("AVPN");
		when(nxRequestDetailsRepository.findByNxReqId(anyLong())).thenReturn(nxRequestDetails);
		List<NxOutputFileModel> outPutFileDataList = new ArrayList<>();
		NxOutputFileModel nxOutputFileModel = new NxOutputFileModel();
		String failurejsonString = "{\r\n" + 
				"    \"PGM\": \"ACCT_RPT\",\r\n" + 
				"    \"Request_id\": \"240005464\",\r\n" + 
				"    \"User_id\": \"m12568@pricerd.att.com\",\r\n" + 
				"    \"Application\": \"NEXXUS\",\r\n" + 
				"    \"status\": 1,\r\n" + 
				"    \"failedAccountSet\": [\r\n" + 
				"        {\r\n" + 
				"            \"accountNumber\": \"1717919147338\",\r\n" + 
				"            \"reasonCode\": \"001\",\r\n" + 
				"            \"billMonth\": [\r\n" + 
				"                \"012016\",\r\n" + 
				"                \"102016\"\r\n" + 
				"            ]\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"            \"mcn\": \"1717919147338\",\r\n" + 
				"            \"reasonCode\": \"001\",\r\n" + 
				"            \"billMonth\": [\r\n" + 
				"                \"012016\",\r\n" + 
				"                \"102016\"\r\n" + 
				"            ]\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
		nxOutputFileModel.setDmaapFailureJson(failurejsonString);
		outPutFileDataList.add(nxOutputFileModel);
		when(nxOutputFileRepository.findByNxReqId(anyLong())).thenReturn(outPutFileDataList);
		JsonNode dmaapFailureJsonNode = realmapper.createObjectNode();
		dmaapFailureJsonNode=realmapper.readTree(failurejsonString);
		when(mapper.readTree(anyString())).thenReturn(dmaapFailureJsonNode);
		Object object = realmapper.readValue(failurejsonString, Object.class);
		when(mapper.readValue(anyString(),any(TypeReference.class))).thenReturn(object);
		String jsonData = realmapper.writeValueAsString(object);
		when(mapper.writeValueAsString(any())).thenReturn(jsonData);
		List<NxLookupData> nxLookUpdataDetailsList= new ArrayList<>();
		NxLookupData nxLookupData = new NxLookupData();
		nxLookupData.setDescription("Not present");
		nxLookUpdataDetailsList.add(nxLookupData);
		when(nxLookupDataRepository.findByDatasetNameAndItemId(anyString(),anyString())).thenReturn(nxLookUpdataDetailsList);
        Path filepath = Paths.get(downloadEdfBulkuploadRequestPath);
		when(nxTemplateProcessingService.getFilePath(anyString())).thenReturn(filepath);
		bulkUploadEdfService.generateFailedDmapMsgReport(request);
	}

	@Test
	public void testgeneratePreviewEdfBulkuploadSheet() throws IOException, SalesBusinessException {
		NxRequestDetails nxRequestDetails= new NxRequestDetails();
		String manageBillingPriceJson="[\r\n" + 
				"    {\r\n" + 
				"        \"manAccountNumber\": null,\r\n" + 
				"        \"mcnNumber\": \"596960\",\r\n" + 
				"        \"product\": \"DOMESTIC DEDICATED ACCESS\",\r\n" + 
				"        \"usageOrNonUsageIndicator\": \"N\",\r\n" + 
				"        \"beginBillMonth\": null,\r\n" + 
				"        \"billMonth\": \"092019\",\r\n" + 
				"        \"cpniApprover\": \"AK999H\",\r\n" + 
				"        \"customerName\": \"AT&T\",\r\n" + 
				"        \"type\": \"MCN\"\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"        \"manAccountNumber\": null,\r\n" + 
				"        \"mcnNumber\": \"W00189\",\r\n" + 
				"        \"product\": \"DOMESTIC DEDICATED ACCESS\",\r\n" + 
				"        \"usageOrNonUsageIndicator\": \"N\",\r\n" + 
				"        \"beginBillMonth\": null,\r\n" + 
				"        \"billMonth\": \"092019\",\r\n" + 
				"        \"cpniApprover\": \"AK999H\",\r\n" + 
				"        \"customerName\": \"AT&T\",\r\n" + 
				"        \"type\": \"MCN\"\r\n" + 
				"    }\r\n" + 
				"]";
		nxRequestDetails.setManageBillingPriceJson(manageBillingPriceJson);
		when(nxRequestDetailsRepository.findByNxReqId(anyLong())).thenReturn(nxRequestDetails);
		JsonNode manageBillingPriceJsonNode = realmapper.createObjectNode();
		manageBillingPriceJsonNode=realmapper.readTree(manageBillingPriceJson);
		when(mapper.readTree(anyString())).thenReturn(manageBillingPriceJsonNode);
        Path filepath = Paths.get(downloadEdfBulkuploadRequestPath);
		when(nxTemplateProcessingService.getFilePath(anyString())).thenReturn(filepath);
//		bulkUploadEdfService.generatePreviewEdfBulkuploadSheet(1L,1L);
	}
	
	@Test
	public void testmanBulkUploadToEDF() throws FileNotFoundException, SalesBusinessException {
		EdfManBulkUploadRequest request = new EdfManBulkUploadRequest();
		request.setNxSolutionId(0L);
		InputStream fis = new FileInputStream(eEdfBulkuploadRequestPath);
		request.setInputStream(fis);
		NxSolutionDetail nxSolutionDetails = new NxSolutionDetail();
		nxSolutionDetails.setNxSolutionId(1L);
		when(nxSolutionDetailsRepo.save(any(NxSolutionDetail.class))).thenReturn(nxSolutionDetails);
//		bulkUploadEdfService.manBulkUploadToEDF(request);
	}

	@Test
	public void testmanBulkUploadToEDFCase2() throws SalesBusinessException, FileNotFoundException {
		EdfManBulkUploadRequest request = new EdfManBulkUploadRequest();
		request.setNxSolutionId(21L);
		InputStream fis = new FileInputStream(eEdfBulkuploadRequestPath);
		request.setInputStream(fis);
		List<NxSolutionDetail> nxSolutionDetailList = new ArrayList<>();
		NxSolutionDetail nxSolutionDetails = new NxSolutionDetail();
		nxSolutionDetails.setNxSolutionId(1L);
		nxSolutionDetailList.add(nxSolutionDetails);
		when(nxSolutionDetailsRepo.findByNxSolutionIdAndActiveYn(any(), any()))
				.thenReturn(nxSolutionDetailList);
		when(nxSolutionDetailsRepo.save(any(NxSolutionDetail.class))).thenReturn(nxSolutionDetails);
//		bulkUploadEdfService.manBulkUploadToEDF(request);
	}
	@Disabled
	@Test
	public void testProcessAndVadidateAccountDatacase2() throws SalesBusinessException {
		List<UploadMANbulkRequest> bulkRequestsListToUpload = new ArrayList<>();
		UploadMANbulkRequest uploadMANbulkRequest = new UploadMANbulkRequest();
		uploadMANbulkRequest.setBeginBillMonth("062019");
		uploadMANbulkRequest.setBillMonth("092019");
		uploadMANbulkRequest.setCpniApprover("cpniApprover");
		uploadMANbulkRequest.setCustomerName("customerName");
		uploadMANbulkRequest.setManAccountNumber("10012305685");
		uploadMANbulkRequest.setMcnNumber("576585");
		uploadMANbulkRequest.setProduct("ABN");
		uploadMANbulkRequest.setType("MAN");
		uploadMANbulkRequest.setUsageOrNonUsageIndicator("Y");
		bulkRequestsListToUpload.add(uploadMANbulkRequest);
		String userId="asd";
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setNxSolutionId(1L);
		NxLookupData nxLookupData = new NxLookupData();
		nxLookupData.setItemId("1");
		nxLookupData.setDescription("description");
		nxLookupData.setDatasetName("SERVICE_GROUP");
		nxLookupData.setCriteria("criteria");
		Mockito.when(nxLookupDataRepository.findByDescriptionAndDatasetName(anyString(),anyList())).thenReturn(nxLookupData);
		List<NxRequestGroup> nxRequestGroups = new ArrayList<>();
		Mockito.when(
				nxRequestGroupRepository.findByNxSolutionIdAndGroupIdAndActiveYn(anyLong(), anyLong(), anyString()))
				.thenReturn(nxRequestGroups);
		Mockito.when(nxLookupDataRepository.findByDatasetNameAndDescription(anyString(),anyString())).thenReturn(nxLookupData);
		List<NxLookupData> nxLookupDataList = new ArrayList<>();
		nxLookupDataList.add(nxLookupData);
		NxRequestGroup nxRequestGroup=new NxRequestGroup();
		nxRequestGroup.setNxRequestGroupId(1L);
		Mockito.when(nxRequestGroupRepository.findByNxRequestGroupIdAndActiveYn(anyLong(),anyString())).thenReturn(nxRequestGroup);
		Mockito.when(nxLookupDataRepository.findByItemIdAndDatasetName(anyString(),anyList())).thenReturn(nxLookupDataList);
		bulkUploadEdfService.processAndVadidateAccountData(bulkRequestsListToUpload,  userId, nxSolutionDetail);
	}
}
