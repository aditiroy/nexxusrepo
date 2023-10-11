package com.att.sales.nexxus.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.CommonConstants;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.NxRequestGroup;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxOutputFileRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsDao;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxRequestGroupRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.inr.InrFactory;
import com.att.sales.nexxus.inr.InrXmlToIntermediateJson;
import com.att.sales.nexxus.inr.OutputJsonFallOutData;
import com.att.sales.nexxus.inr.OutputJsonService;
import com.att.sales.nexxus.inr.PreviewDataService;
import com.att.sales.nexxus.output.entity.NxOutputBean;
import com.att.sales.nexxus.util.UnmockableWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

@ExtendWith(MockitoExtension.class)
public class InrProcessingServiceTest {
	private JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
	@Mock
	private NxOutputFileRepository nxOutputFileRepository;
	@Mock
	private NxRequestDetailsRepository nxRequestDetailsRepository;
	@Mock
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;
	@Mock
	private ReportService reportService;
	@Mock
	private NxRequestDetailsDao nxRequestDetailsDao;
	@Mock
	private InrFactory inrFactory;
	@Spy
	private ObjectMapper mapper;
	@Spy
	@InjectMocks
	private InrProcessingService inrProcessingService;
	@Mock
	private PreviewDataService previewDataService;
	@Mock
	private InrQualifyService inrQualifyService;
	@Mock
	private NxDesignAuditRepository nxDesignAuditRepository;
	@Mock
	private JsonNode jsonNode;
	@Mock
	private InrXmlToIntermediateJson inrXmlToIntermediateJson;
	@Mock
	private Blob blob;
	@Mock
	private UnmockableWrapper unmockableWrapper;
	@Mock
	private OutputJsonService outputJsonService;
	@Mock
	private MessageConsumptionServiceImpl messageConsumptionServiceImpl;
	@Mock
	private NxRequestGroupRepository nxRequestGroupRepository;
	@Mock
	private NxLookupDataRepository nxLookupDataRepository;

	@Test
	public void createInrNexusOutputTest() throws SalesBusinessException, IOException {
		ReflectionTestUtils.setField(inrProcessingService, "p8dLocalPath", "/");
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		nxRequestDetails.setFileName("fileName.xml");
		when(unmockableWrapper.readFile(any(), any())).thenReturn("<root></root>");
		// case 1
		doReturn(jsonNode).when(inrProcessingService).generateIntermediateJson(any(), any());
		NxOutputBean nxOutputBean = new NxOutputBean();
		OutputJsonFallOutData outputJsonFallOutData = new OutputJsonFallOutData(nxOutputBean, "", null, false, false);
		doReturn(outputJsonFallOutData).when(inrProcessingService).generateOutput(any(), any());
		doReturn(CommonConstants.STATUS_CONSTANTS.SUCCESS.getValue()).when(inrProcessingService).setSuccessStatus(any(),
				any(), any(), any());
		/*assertEquals(CommonConstants.STATUS_CONSTANTS.SUCCESS.getValue(),
				inrProcessingService.createInrNexusOutput(nxRequestDetails));*/

		// case 2
		doThrow(new SalesBusinessException(InrConstants.XML_FILE_NOT_FOUND_EXCEPTION)).when(inrProcessingService)
				.generateIntermediateJson(any(), any());
		/*assertEquals(CommonConstants.STATUS_CONSTANTS.FILE_NOT_FOUND.getValue(),
				inrProcessingService.createInrNexusOutput(nxRequestDetails));*/
	}

	@Test
	public void regenerateOutputJsonTest() throws IOException, SalesBusinessException {
		NxRequestDetails reqDetails = new NxRequestDetails();
		NxOutputFileModel nxOutputFileModel = new NxOutputFileModel();
		NxDesignAudit nxDesignAudit = new NxDesignAudit();
		Map<String, Object> result = new HashMap<String, Object>();
		String inventoryJsonString = "inventoryJsonString";
		nxOutputFileModel.setIntermediateJson(inventoryJsonString);
		List<NxOutputFileModel> nxOutputFiles = Arrays.asList(nxOutputFileModel);
		reqDetails.setNxOutputFiles(nxOutputFiles);
		doReturn(jsonNode).when(mapper).readTree(anyString());
		NxOutputBean nxOutputBean = new NxOutputBean();
		OutputJsonFallOutData outputJsonFallOutData = new OutputJsonFallOutData(nxOutputBean, "", jsonNode, false, false);
		doReturn(outputJsonFallOutData).when(inrProcessingService).generateOutput(any(), any());
		doReturn(10L).when(inrProcessingService).setSuccessStatus(any(), any(), any(), any());
		doReturn(jsonNode).when(inrProcessingService).generateIntermediateJson(any(), any());
		doReturn(jsonNode).when(previewDataService).generateCdirData(any());
		doReturn(result).when(inrQualifyService).inrQualifyCheck(anyLong(), anyBoolean(), any());
		doReturn(nxDesignAudit).when(nxDesignAuditRepository).findByNxRefIdAndTransaction(any(), any());
		doReturn(reqDetails).when(nxRequestDetailsRepository).findByNxReqId(any());
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setNxSolutionId(2105L);
		nxSolutionDetail.setActiveYn("Y");
		reqDetails.setNxSolutionDetail(nxSolutionDetail);
		inrProcessingService.regenerateOutputJson(reqDetails);
	}

	@Test
	public void generateOutputTest() throws SalesBusinessException {
		OutputJsonFallOutData outputJsonFallOutData = new OutputJsonFallOutData(null, null, null, true, true);
		when(outputJsonService.getOutputData(any(), any())).thenReturn(outputJsonFallOutData);
		assertSame(outputJsonFallOutData, inrProcessingService.generateOutput(jsonNode, "INR"));
	}

	@Test
	public void setSuccessStatusTest() {
		NxOutputFileModel model = new NxOutputFileModel();

		// case 1
		ObjectNode intermediateJson = jsonNodeFactory.objectNode();
		assertEquals(CommonConstants.STATUS_CONSTANTS.ERROR.getValue(),
				inrProcessingService.setSuccessStatus(model, intermediateJson, null, null));

		// case 2
		intermediateJson.put("field", "value");
		NxOutputBean nxOutputBean = new NxOutputBean();
		OutputJsonFallOutData outputJsonFallOutData = new OutputJsonFallOutData(nxOutputBean, null, null, false, false);
		model.setFallOutData("fallOutData");
		assertEquals(CommonConstants.STATUS_CONSTANTS.FALLOUT.getValue(),
				inrProcessingService.setSuccessStatus(model, intermediateJson, outputJsonFallOutData, blob));

		// case 3
		outputJsonFallOutData = new OutputJsonFallOutData(nxOutputBean, null, null, false, true);
		assertEquals(CommonConstants.STATUS_CONSTANTS.SUCCESS_WITH_FALLOUT.getValue(),
				inrProcessingService.setSuccessStatus(model, intermediateJson, outputJsonFallOutData, blob));
		
		// case 4
		model.setFallOutData(null);
		assertEquals(CommonConstants.STATUS_CONSTANTS.SUCCESS.getValue(),
				inrProcessingService.setSuccessStatus(model, intermediateJson, outputJsonFallOutData, blob));
		
		// case 5
		outputJsonFallOutData = new OutputJsonFallOutData(nxOutputBean, null, null, false, false);
		intermediateJson.put("service", "DOMESTIC DEDICATED ACCESS");
		intermediateJson.put("DomesticEthernetAccessInventory","data");
		assertEquals(CommonConstants.STATUS_CONSTANTS.SUCCESS.getValue(),
				inrProcessingService.setSuccessStatus(model, intermediateJson, outputJsonFallOutData, null));

		// case 6
		outputJsonFallOutData = new OutputJsonFallOutData(nxOutputBean, null, null, true, true);
		intermediateJson.put("service", "DOMESTIC DEDICATED ACCESS");
		intermediateJson.put("DomesticEthernetAccessInventory","data");
		assertEquals(CommonConstants.STATUS_CONSTANTS.SUCCESS.getValue(),
				inrProcessingService.setSuccessStatus(model, intermediateJson, outputJsonFallOutData, null));

		// case 7
		outputJsonFallOutData = new OutputJsonFallOutData(nxOutputBean, null, null, false, false);
		model.setFallOutData("fallOutData");
		intermediateJson.put("service", "DOMESTIC DEDICATED ACCESS");
		intermediateJson.put("DomesticEthernetAccessInventory","data");
		assertEquals(CommonConstants.STATUS_CONSTANTS.SUCCESS_WITH_FALLOUT.getValue(),
				inrProcessingService.setSuccessStatus(model, intermediateJson, outputJsonFallOutData, null));

	}

	@Test
	public void updateNoDmaapNotificationStatusTest() {
		ReflectionTestUtils.setField(inrProcessingService, "enableScheduler", "Y");
		ReflectionTestUtils.setField(inrProcessingService, "dmaapFailTimeInHours", "24");
		when(nxRequestDetailsDao.updateNoDmaapNotificationStatus(anyLong(), anyLong(), any())).thenReturn(1);
		List<NxRequestDetails> nxRequestDetailsList = new ArrayList<NxRequestDetails>();
		NxRequestDetails nxRequestDetails1 = new NxRequestDetails();
		nxRequestDetails1.setNxRequestGroupId(1L);
		nxRequestDetails1.setNxSolutionDetail(new NxSolutionDetail());
		nxRequestDetails1.setNxRequestGroupName("SERVICE_GROUP");
		nxRequestDetailsList.add(nxRequestDetails1);

		NxRequestDetails nxRequestDetails2 = new NxRequestDetails();
		nxRequestDetails2.setNxRequestGroupId(1L);
		nxRequestDetails2.setNxSolutionDetail(new NxSolutionDetail());
		nxRequestDetails2.setNxRequestGroupName("SERVICE_ACCESS_GROUP");
		nxRequestDetails2.setProduct("DOMESTIC DEDICATED ACCESS");
		nxRequestDetailsList.add(nxRequestDetails2);

		when(nxRequestDetailsRepository.getNxRequests(anyLong(),any())).thenReturn(nxRequestDetailsList);
		when(nxRequestGroupRepository.findByNxRequestGroupIdAndActiveYn(anyLong(),anyString()))
		.thenReturn(new NxRequestGroup());
		when(nxRequestDetailsRepository.findRequestsByGroupId(anyLong(),anyString())).thenReturn(nxRequestDetailsList);
		
		List<NxLookupData> nxLookupDataList = new ArrayList<>();
		NxLookupData nxLookupData = new NxLookupData();
		nxLookupData.setDescription("DOMESTIC DEDICATED ACCESS");
		nxLookupDataList.add(nxLookupData);
		when(nxLookupDataRepository.findByDatasetName(anyString())).thenReturn(nxLookupDataList);
		inrProcessingService.updateNoDmaapNotificationStatus();
	}
}
