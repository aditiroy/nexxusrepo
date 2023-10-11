package com.att.sales.nexxus.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.dao.model.NxInrDmaapAudit;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxInrDmaapAuditRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxOutputFileRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxRequestGroupRepository;
import com.att.sales.nexxus.dmaap.mr.util.IDmaapMRSubscriber;
import com.att.sales.nexxus.fallout.service.FalloutDetailsImpl;
import com.att.sales.nexxus.model.EDFMRBulkFailResponse;
import com.att.sales.nexxus.model.EDFMRBulkSuccessResponse;
import com.att.sales.nexxus.model.Inventoryfiles;
import com.att.sales.nexxus.p8.P8Service;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.att.sales.nexxus.util.xmlMerge.XmlMergeUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class BulkMessageConsumptionServiceImplTest {
	@InjectMocks
	private BulkMessageConsumptionServiceImpl bulkMessageConsumptionServiceImpl;

	@Mock
	private Environment env;

	@Mock
	private IDmaapMRSubscriber dmaapMRSubscriberService;

	@Mock
	private NxRequestDetailsRepository nxRequestDetailsRepository;

	@Mock
	private ObjectMapper mapper;

	@Mock
	private P8Service p8Service;

	@Mock
	private InrProcessingService inrProcessingService;

	@Mock
	private NexxusJsonUtility nexxusJsonUtility;

	@Mock
	private XmlMergeUtil xmlMergeUtil;

	@Mock
	private NxLookupDataRepository nxLookupDataRepository;

	@Mock
	private NxOutputFileRepository nxOutputFileRepository;

	@Mock
	private MailServiceImpl mailService;

	@Mock
	private NxInrDmaapAuditRepository nxInrDmaapAuditRepository;

	@Mock
	private NxRequestGroupRepository nxRequestGroupRepository;

	@Mock
	private FalloutDetailsImpl falloutDetailsImpl;
	
	@Mock
	private MessageConsumptionServiceImpl messageConsumptionServiceImpl;

	String failMsg;
	String successMsg;
	
	private NxRequestDetails nxRequets = null;

	@BeforeEach
	public void setUp() {
		ReflectionTestUtils.setField(bulkMessageConsumptionServiceImpl, "inrBulkDmaapProcessEnabled", "Y");
		ReflectionTestUtils.setField(bulkMessageConsumptionServiceImpl, "p8dLocalPath", "/");
		failMsg = "{\r\n" + "    \"Request_id\": \"353596655\",\r\n" + "    \"PGM\": \"ACCT_RPT\",\r\n"
				+ "    \"failedAccountSet\": [\r\n" + "        {\r\n" + "            \"billMonth\": [\r\n"
				+ "                \"062019\",\r\n" + "                \"092018\"\r\n" + "            ],\r\n"
				+ "            \"reasonCode\": \"001\",\r\n" + "            \"accountNumber\": \"10012153044\"\r\n"
				+ "        }\r\n" + "    ],\r\n" + "    \"User_id\": \"m12568@pricerd.att.com\",\r\n"
				+ "    \"Application\": \"NEXXUS\",\r\n" + "    \"status\": 1\r\n" + "}";
		successMsg = "{\r\n" + "    \"Request_id\": \"860921673\",\r\n" + "    \"PGM\": \"BULK_INV\",\r\n"
				+ "    \"inventoryFiles\": {\r\n" + "        \"fileCount\": 1,\r\n" + "        \"fileNames\": [\r\n"
				+ "            \"file1.xml\"\r\n" + "        ]\r\n" + "    },\r\n"
				+ "    \"User_id\": \"NexxusId\",\r\n" + "    \"Application\": \"Nexxus\",\r\n"
				+ "    \"status\": 3\r\n" + "}";
		
		nxRequets = new NxRequestDetails();
		NxSolutionDetail sol = new NxSolutionDetail();
		sol.setNxSolutionId(101l);
		nxRequets.setNxSolutionDetail(sol);
	}

	@Test
	public void testGetFailMessage() throws Exception {
		ReflectionTestUtils.setField(bulkMessageConsumptionServiceImpl, "edfDmaapFailEnabled", "Y");
		List<String> messages = new ArrayList<>();
		messages.add(failMsg);
		messages.add("edfTest");
		String msg = messages.get(0);
		Mockito.when(env.getProperty(Mockito.anyString())).thenReturn("topicname");
		Mockito.when(dmaapMRSubscriberService.retrieveMessage(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(messages);
		Map dmaapmsgMap = new HashMap<>();
		dmaapmsgMap.put("Request_id", "353596655");
		dmaapmsgMap.put("PGM", "ACCT_RPT");
		Mockito.when(nexxusJsonUtility.convertStringJsonToMap(Mockito.anyString())).thenReturn(dmaapmsgMap);

		EDFMRBulkFailResponse eDFMRBulkFailResponse = new EDFMRBulkFailResponse();
		eDFMRBulkFailResponse.setRequestId("353596655");
		eDFMRBulkFailResponse.setPgm("ACCT_RPT");

		Mockito.when(mapper.readValue(msg, EDFMRBulkFailResponse.class)).thenReturn(eDFMRBulkFailResponse);
		Mockito.when(nxRequestDetailsRepository.findNxSolutionIdByEdfAckIdAndActiveYn(anyString(), anyString())).thenReturn(nxRequets);
		Mockito.when(nxInrDmaapAuditRepository.save(any(NxInrDmaapAudit.class))).thenReturn(any());

		bulkMessageConsumptionServiceImpl.getMessage();
	}

	@Test
	public void testGetSuccessMessage() throws Exception {
		ReflectionTestUtils.setField(bulkMessageConsumptionServiceImpl, "edfDmaapSuccessEnabled", "Y");
		List<String> messages = new ArrayList<>();
		messages.add(successMsg);
		messages.add("edfTest");
		String msg = messages.get(0);
		Mockito.when(env.getProperty(Mockito.anyString())).thenReturn("topicname");
		Mockito.when(dmaapMRSubscriberService.retrieveMessage(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(messages);
		Map dmaapmsgMap = new HashMap<>();
		dmaapmsgMap.put("Request_id", "860921673");
		dmaapmsgMap.put("PGM", "BULK_INV");
		Mockito.when(nexxusJsonUtility.convertStringJsonToMap(Mockito.anyString())).thenReturn(dmaapmsgMap);

		EDFMRBulkSuccessResponse eDFMRBulkSuccessResponse = new EDFMRBulkSuccessResponse();
		eDFMRBulkSuccessResponse.setRequestId("860921673");
		eDFMRBulkSuccessResponse.setPgm("BULK_INV");

		Mockito.when(mapper.readValue(msg, EDFMRBulkSuccessResponse.class)).thenReturn(eDFMRBulkSuccessResponse);
		Mockito.when(nxRequestDetailsRepository.findNxSolutionIdByEdfAckIdAndActiveYn(anyString(), anyString())).thenReturn(nxRequets);
		Mockito.when(nxInrDmaapAuditRepository.save(any(NxInrDmaapAudit.class))).thenReturn(any());

		bulkMessageConsumptionServiceImpl.getMessage();
	}

	@Test
	public void testProcessInrDmaapFailMessage() throws JsonParseException, JsonMappingException, IOException {
		List<NxInrDmaapAudit> nxInrDmaapAudits= new ArrayList<>();
		NxInrDmaapAudit nxInrDmaapAudit = new NxInrDmaapAudit();
		nxInrDmaapAudit.setId(2L);
		nxInrDmaapAudit.setNxCorrelationId("353596655");
		nxInrDmaapAudit.setNxMessage(failMsg);
		nxInrDmaapAudit.setNxProcessStatus("N");
		nxInrDmaapAudit.setNxTransactionType("EDF_INR_DMAAP");
		nxInrDmaapAudits.add(nxInrDmaapAudit);
		
	//	Mockito.when(nxInrDmaapAuditRepository.getNewDmaapDetails(Mockito.anyString(),Mockito.anyString())).thenReturn(nxInrDmaapAudits);
		Mockito.when(nxInrDmaapAuditRepository.getNewDmaapDetailsByPodName(any(), any(), any())).thenReturn(nxInrDmaapAudits);
		Map dmaapmsgMap = new HashMap();
		dmaapmsgMap.put("PGM", "ACCT_RPT");
		Mockito.when(nexxusJsonUtility.convertStringJsonToMap(Mockito.anyString())).thenReturn(dmaapmsgMap);
		
		EDFMRBulkFailResponse eDFMRBulkFailResponse = new EDFMRBulkFailResponse();
		eDFMRBulkFailResponse.setRequestId("353596655");
		eDFMRBulkFailResponse.setPgm("ACCT_RPT");
		eDFMRBulkFailResponse.setStatus(1);
		Mockito.when(mapper.readValue(failMsg, EDFMRBulkFailResponse.class)).thenReturn(eDFMRBulkFailResponse);
		List<NxRequestDetails> nxRequestDetails = new ArrayList<>();
		NxRequestDetails nxRequestDetail = new NxRequestDetails();
		nxRequestDetail.setNxReqId(1L);
		nxRequestDetail.setDmaapMsg(failMsg);
		nxRequestDetails.add(nxRequestDetail);

		List<NxOutputFileModel> nxOutputFileModels= new ArrayList();
		Mockito.when(nxOutputFileRepository.findByNxReqId(Mockito.anyLong())).thenReturn(nxOutputFileModels);
		
		Mockito.when(nxRequestDetailsRepository.findByEdfAckIdAndActiveYnAndBulkReqYn(Mockito.anyString(),Mockito.anyString(),
				Mockito.anyString())).thenReturn(nxRequestDetails);
    	doNothing().when(p8Service).lookupDocumentInP8(Mockito.any());
		doNothing().when(falloutDetailsImpl).setNxGroupStatus(Mockito.any(),Mockito.any());
		bulkMessageConsumptionServiceImpl.processInrDmaap();
	}
	
	
	@Test
	public void testProcessInrDmaapSuccessMessage() throws JsonParseException, JsonMappingException, IOException {
		List<NxInrDmaapAudit> nxInrDmaapAudits= new ArrayList<>();
		NxInrDmaapAudit nxInrDmaapAudit = new NxInrDmaapAudit();
		nxInrDmaapAudit.setId(2L);
		nxInrDmaapAudit.setNxCorrelationId("860921673");
		nxInrDmaapAudit.setNxMessage(successMsg);
		nxInrDmaapAudit.setNxProcessStatus("N");
		nxInrDmaapAudit.setNxTransactionType("EDF_INR_DMAAP");
		nxInrDmaapAudits.add(nxInrDmaapAudit);
		
		//Mockito.when(nxInrDmaapAuditRepository.getNewDmaapDetails(Mockito.anyString(), Mockito.anyString())).thenReturn(nxInrDmaapAudits);
		Mockito.when(nxInrDmaapAuditRepository.getNewDmaapDetailsByPodName(any(), any(), any())).thenReturn(nxInrDmaapAudits);
		Map dmaapmsgMap = new HashMap();
		dmaapmsgMap.put("PGM", "BULK_INV");
		Mockito.when(nexxusJsonUtility.convertStringJsonToMap(Mockito.anyString())).thenReturn(dmaapmsgMap);
		
		EDFMRBulkSuccessResponse eDFMRBulkSuccessResponse = new EDFMRBulkSuccessResponse();
		eDFMRBulkSuccessResponse.setRequestId("860921673");
		eDFMRBulkSuccessResponse.setPgm("BULK_INV");
		Inventoryfiles inventoryFiles = new Inventoryfiles();
		inventoryFiles.setFileCount(1);
		List<String> fileNames= new ArrayList();
		fileNames.add("file1.xml");
		inventoryFiles.setFileNames(fileNames);
		eDFMRBulkSuccessResponse.setInventoryFiles(inventoryFiles);
		eDFMRBulkSuccessResponse.setStatus(0);

		Mockito.when(mapper.readValue(successMsg, EDFMRBulkSuccessResponse.class)).thenReturn(eDFMRBulkSuccessResponse);
		List<NxRequestDetails> nxRequestDetails = new ArrayList<>();
		NxRequestDetails nxRequestDetail = new NxRequestDetails();
		nxRequestDetail.setNxReqId(1L);
		nxRequestDetail.setDmaapMsg(successMsg);
		nxRequestDetails.add(nxRequestDetail);

		List<NxOutputFileModel> nxOutputFileModels= new ArrayList();
		Mockito.when(nxOutputFileRepository.findByNxReqId(Mockito.any())).thenReturn(nxOutputFileModels);
		
		Mockito.when(nxRequestDetailsRepository.findByEdfAckIdAndActiveYnAndBulkReqYn(Mockito.anyString(),Mockito.anyString(),
				Mockito.anyString())).thenReturn(nxRequestDetails);
    	doNothing().when(p8Service).lookupDocumentInP8(Mockito.anyString());
		doNothing().when(falloutDetailsImpl).setNxGroupStatus(Mockito.any(),Mockito.any());

		
		
		bulkMessageConsumptionServiceImpl.processInrDmaap();
	}
	
	@Test
	public void testProcessInrDmaapSuccessMessageElse() throws JsonParseException, JsonMappingException, IOException {
		List<NxInrDmaapAudit> nxInrDmaapAudits= new ArrayList<>();
		NxInrDmaapAudit nxInrDmaapAudit = new NxInrDmaapAudit();
		nxInrDmaapAudit.setId(2L);
		nxInrDmaapAudit.setNxCorrelationId("860921673");
		nxInrDmaapAudit.setNxMessage(successMsg);
		nxInrDmaapAudit.setNxProcessStatus("N");
		nxInrDmaapAudit.setNxTransactionType("EDF_INR_DMAAP");
		nxInrDmaapAudits.add(nxInrDmaapAudit);
		
		//Mockito.when(nxInrDmaapAuditRepository.getNewDmaapDetails(Mockito.anyString(), Mockito.anyString())).thenReturn(nxInrDmaapAudits);
		Mockito.when(nxInrDmaapAuditRepository.getNewDmaapDetailsByPodName(any(), any(), any())).thenReturn(nxInrDmaapAudits);
		Map dmaapmsgMap = new HashMap();
		dmaapmsgMap.put("PGM", "BULK_INV");
		Mockito.when(nexxusJsonUtility.convertStringJsonToMap(Mockito.anyString())).thenReturn(dmaapmsgMap);
		
		EDFMRBulkSuccessResponse eDFMRBulkSuccessResponse = new EDFMRBulkSuccessResponse();
		eDFMRBulkSuccessResponse.setRequestId("860921673");
		eDFMRBulkSuccessResponse.setPgm("BULK_INV");
		Inventoryfiles inventoryFiles = new Inventoryfiles();
		inventoryFiles.setFileCount(1);
		List<String> fileNames= new ArrayList();
		fileNames.add("file1.xml");
		fileNames.add("file2.xml");

		inventoryFiles.setFileNames(fileNames);
		eDFMRBulkSuccessResponse.setInventoryFiles(inventoryFiles);
		eDFMRBulkSuccessResponse.setStatus(2);

		Mockito.when(mapper.readValue(successMsg, EDFMRBulkSuccessResponse.class)).thenReturn(eDFMRBulkSuccessResponse);
		List<NxRequestDetails> nxRequestDetails = new ArrayList<>();
		NxRequestDetails nxRequestDetail = new NxRequestDetails();
		nxRequestDetail.setNxReqId(1L);
		nxRequestDetail.setDmaapMsg(successMsg);
		nxRequestDetails.add(nxRequestDetail);

		List<NxOutputFileModel> nxOutputFileModels= new ArrayList();
		Mockito.when(nxOutputFileRepository.findByNxReqId(Mockito.any())).thenReturn(nxOutputFileModels);
		
		Mockito.when(nxRequestDetailsRepository.findByEdfAckIdAndActiveYnAndBulkReqYn(Mockito.anyString(),Mockito.anyString(),
				Mockito.anyString())).thenReturn(nxRequestDetails);
    	doNothing().when(p8Service).lookupDocumentInP8(Mockito.anyString());
		doNothing().when(falloutDetailsImpl).setNxGroupStatus(Mockito.any(),Mockito.any());
		doNothing().when(messageConsumptionServiceImpl).inrProcess(Mockito.any());
		
		
		bulkMessageConsumptionServiceImpl.processInrDmaap();
	}


}
