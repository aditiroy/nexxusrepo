package com.att.sales.nexxus.service;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import com.att.sales.exampledomainobject.model.EDFMRResponse;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.dao.model.NxInrActivePods;
import com.att.sales.nexxus.dao.model.NxInrDmaapAudit;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxInrActivePodsRepository;
import com.att.sales.nexxus.dao.repository.NxInrDmaapAuditRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dmaap.mr.util.DmaapMRSubscriberImpl;
import com.att.sales.nexxus.p8.P8Service;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class MessageConsumptionServiceImplTest {
	@InjectMocks
	MessageConsumptionServiceImpl messageConsumptionServiceImpl;
	
	@Mock
	Environment env;
	
	@Mock
	private ObjectMapper mapper;
	

	@Mock
	DmaapMRSubscriberImpl dmaapMRSubscriberService;

	@Mock
	private NxRequestDetailsRepository nxRequestDetailsRepository;

	@Mock
	private P8Service p8Service;

	@Mock
	private InrProcessingService inrProcessingService;

	@Mock
	private NxInrDmaapAuditRepository nxInrDmaapAuditRepository;
	
	@Mock
	private InrQualifyService inrQualifyService;
	
	private NxRequestDetails nxRequets = null;
	
	@Mock
	private NxInrActivePodsRepository nxInrActivePodsRepository;
	
	private List<NxInrDmaapAudit> nxInrDmaapAudits= new ArrayList<>();
	
	private String message = null;
	
	@BeforeEach
	public void setUp() {
		ReflectionTestUtils.setField(messageConsumptionServiceImpl, "edfDmaapEnabled","Y");
		ReflectionTestUtils.setField(messageConsumptionServiceImpl, "inrDmaapProcessEnabled","Y");
		ReflectionTestUtils.setField(messageConsumptionServiceImpl, "inrActivePodsEnabled","Y");
		ReflectionTestUtils.setField(messageConsumptionServiceImpl, "inrBulkActivePodsEnabled","N");
		ReflectionTestUtils.setField(messageConsumptionServiceImpl, "updatePodnameEnabled","Y");
		ReflectionTestUtils.setField(messageConsumptionServiceImpl, "podHeartbeatTimeInMin",2);
		nxRequets = new NxRequestDetails();
		NxSolutionDetail sol = new NxSolutionDetail();
		sol.setNxSolutionId(101l);
		nxRequets.setNxSolutionDetail(sol);
		message = "{\r\n" + 
				"    \"Outputfile_name\": \"37540220200504163143_20200504_113250.xml\",\r\n" + 
				"    \"Request_id\": \"62386220200507173406\",\r\n" + 
				"    \"End_run_time\": null,\r\n" + 
				"    \"PGM\": \"INVNTRY\",\r\n" + 
				"    \"User_id\": \"NexxusId\",\r\n" + 
				"    \"Application\": \"Nexxus\",\r\n" + 
				"    \"Start_run_time\": \"2019-10-17 00:47:59\"\r\n" +
				"    \"Message\": \"Successfully rtrieved\"\r\n" + 
				"}";
		NxInrDmaapAudit nxInrDmaapAudit = new NxInrDmaapAudit();
		nxInrDmaapAudit.setId(2L);
		nxInrDmaapAudit.setNxCorrelationId("62386220200507173406");
		nxInrDmaapAudit.setNxMessage(message);
		nxInrDmaapAudit.setNxProcessStatus("N");
		nxInrDmaapAudit.setNxTransactionType("EDF_INR_DMAAP");
		nxInrDmaapAudit.setNxSolutionId(1l);
		nxInrDmaapAudits.add(nxInrDmaapAudit);
	}

	@Test
	public void testGetMessage() throws IOException, InterruptedException, Exception {
		List<String> messages = new ArrayList<>();
		messages.add("{\r\n" + 
				"    \"Outputfile_name\": \"37540220200504163143_20200504_113250.xml\",\r\n" + 
				"    \"Request_id\": \"62386220200507173406\",\r\n" + 
				"    \"End_run_time\": null,\r\n" + 
				"    \"PGM\": \"INVNTRY\",\r\n" + 
				"    \"User_id\": \"NexxusId\",\r\n" + 
				"    \"Application\": \"Nexxus\",\r\n" + 
				"    \"Start_run_time\": \"2019-10-17 00:47:59\"\r\n" + 
				"}");
		messages.add("edfTest");
		String message = "{\r\n" + 
				"    \"Outputfile_name\": \"37540220200504163143_20200504_113250.xml\",\r\n" + 
				"    \"Request_id\": \"62386220200507173406\",\r\n" + 
				"    \"End_run_time\": null,\r\n" + 
				"    \"PGM\": \"INVNTRY\",\r\n" + 
				"    \"User_id\": \"NexxusId\",\r\n" + 
				"    \"Application\": \"Nexxus\",\r\n" + 
				"    \"Start_run_time\": \"2019-10-17 00:47:59\"\r\n" + 
				"}";
		EDFMRResponse edfMRResponse = new EDFMRResponse();
		edfMRResponse.setRequestId("62386220200507173406");
		edfMRResponse.setMessage("Successfully rtrieved ");
		Mockito.when(env.getProperty(Mockito.anyString())).thenReturn("topicname");
		Mockito.when(dmaapMRSubscriberService.retrieveMessage(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(messages);
		Mockito.when(mapper.readValue(message, EDFMRResponse.class)).thenReturn(edfMRResponse);
		Mockito.when(nxRequestDetailsRepository.findNxSolutionIdByEdfAckIdAndActiveYn(anyString(), anyString())).thenReturn(nxRequets);
		Mockito.when(nxInrDmaapAuditRepository.save(any(NxInrDmaapAudit.class))).thenReturn(any());
		messageConsumptionServiceImpl.getMessage();
	}

	@Test
	public void testProcessInrDmaap() throws IOException, InterruptedException, Exception {
		EDFMRResponse edfMRResponse = new EDFMRResponse();
		edfMRResponse.setRequestId("62386220200507173406");
		edfMRResponse.setMessage("Successfully rtrieved ");
		edfMRResponse.setUserId("NexxusId");
		edfMRResponse.setApplication("Nexxus");
		edfMRResponse.setEndRunTime("null");
		edfMRResponse.setOutputfileName("37540220200504163143_20200504_113250.xml");
		edfMRResponse.setPgm("INVNTRY");
		edfMRResponse.setStartRunTime("2019-10-17 00:47:59");
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		nxRequestDetails.setEdfAckId("62386220200507173406");
		nxRequestDetails.setNxReqId(1L);
		NxOutputFileModel nxOutputFileModel = new NxOutputFileModel();
		nxOutputFileModel.setCdirData("cdirData");
		nxOutputFileModel.setDmaapFailureJson("dmaapFailureJson");
		nxOutputFileModel.setFallOutData("fallOutData");
		nxOutputFileModel.setFileName("fileName");
		nxOutputFileModel.setFileType("fileType");
		nxOutputFileModel.setIntermediateJson("intermediateJson");
		nxOutputFileModel.setInventoryFileSize("23kb");
		nxOutputFileModel.setInventoryJson("inventoryJson");
		List<NxOutputFileModel> nxOutputFiles= new ArrayList<NxOutputFileModel>();
		nxOutputFiles.add(nxOutputFileModel);
		
		NxRequestDetails nxRequestDetails2 = new NxRequestDetails();
		nxRequestDetails2.setFileName("fileName");
		nxRequestDetails2.setDmaapMsg("dmaapMsg");
		nxRequestDetails2.setStatus(30L);
		nxRequestDetails2.setNxOutputFiles(nxOutputFiles);
		nxRequestDetails2.setNxReqId(2L);
		
		List<NxRequestDetails> nxRequestDetail = new ArrayList<>();
		nxRequestDetail.add(nxRequestDetails);
		nxRequestDetail.add(nxRequestDetails2);
		//when(nxInrDmaapAuditRepository.getNewDmaapDetails(any(),any())).thenReturn(nxInrDmaapAudits);
		when(nxInrDmaapAuditRepository.getNewDmaapDetailsByPodName(any(),any(), any())).thenReturn(nxInrDmaapAudits);
		Mockito.when(mapper.readValue(message, EDFMRResponse.class)).thenReturn(edfMRResponse);

		Mockito.when(nxRequestDetailsRepository.findByEdfAckIdAndActiveYn(Mockito.anyString(),Mockito.anyString())).thenReturn(nxRequestDetail);
    	doNothing().when(p8Service).lookupDocumentInP8(Mockito.anyString());
		when(inrProcessingService.createInrNexusOutput(Mockito.any(), Mockito.any())).thenReturn(null);
		doNothing().when(p8Service).lookupDocumentInP8(Mockito.anyString());
		when(inrProcessingService.createInrNexusOutput(Mockito.any(), Mockito.any())).thenReturn(nxOutputFileModel);
		when(inrQualifyService.inrQualifyCheck(Mockito.anyLong(),Mockito.anyBoolean(), Mockito.any())).thenReturn(null);
		messageConsumptionServiceImpl.processInrDmaap();

	}
	
	@Test
	public void testCleanUpPodName() {
		Mockito.when(nxInrDmaapAuditRepository.updatePodName(anyString())).thenReturn(1);
		Mockito.when(nxInrActivePodsRepository.findByPodName(anyString())).thenReturn(null);
		messageConsumptionServiceImpl.cleanUpPodName();
	}
	
	@Test
	public void testCreateNxInrActivePod() {
		Mockito.when(nxInrActivePodsRepository.findByPodName(anyString())).thenReturn(null);
		Mockito.when(nxInrActivePodsRepository.save(any(NxInrActivePods.class))).thenReturn(any());
		messageConsumptionServiceImpl.createNxInrActivePod();
	}
	
	@Test
	public void testUpdatePodName() {
		List<Object[]> data = new ArrayList<>();
		Object[] req = new Object[2];
		req[0] = "podName";
		BigDecimal d = new BigDecimal(0);
		req[1] = d;
		data.add(req);
		NxInrDmaapAudit nxInrDmaapAudit = new NxInrDmaapAudit();
		nxInrDmaapAudit.setId(2L);
		nxInrDmaapAudit.setNxCorrelationId("62386220200507173406");
		nxInrDmaapAudit.setNxMessage(message);
		nxInrDmaapAudit.setNxProcessStatus("N");
		nxInrDmaapAudit.setNxTransactionType("EDF_INR_DMAAP");
		nxInrDmaapAudit.setNxSolutionId(1l);
		Mockito.when(nxInrActivePodsRepository.getPods(any(), any())).thenReturn(data);
		Mockito.when(nxInrDmaapAuditRepository.findByTransactionType(anyString())).thenReturn(nxInrDmaapAudits);
		Mockito.when(nxInrDmaapAuditRepository.save(any())).thenReturn(nxInrDmaapAudits);
		Mockito.when(nxInrDmaapAuditRepository.findByTransactionTypeAndProcessStatusAndPodName(anyString(), any(), any(), any())).thenReturn(nxInrDmaapAudits);
		messageConsumptionServiceImpl.updatePodName(InrConstants.EDF_INR_DMAAP, nxInrDmaapAudit);
	}
}
