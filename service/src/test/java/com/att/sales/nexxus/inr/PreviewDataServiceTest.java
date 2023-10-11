package com.att.sales.nexxus.inr;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
@ExtendWith(MockitoExtension.class)
public class PreviewDataServiceTest {
	@Spy
	@InjectMocks
	private PreviewDataService previewDataService;
	@Mock
	private InrFactory inrFactory;
	@Mock
	private NxLookupDataRepository nxLookupDataRepository;
	@Mock
	private NxDesignAuditRepository nxDesignAuditRepository;
	@Mock
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	@Mock
	private ObjectMapper mapper;
	@Mock
	private NxOutputFileModel nxOutputFileModel;
	@Mock
	private NxRequestDetails nxRequestDetails;
	@Mock
	private NxSolutionDetail nxSolutionDetail;
	@Mock
	private ObjectNode objectNode;
	@Mock
	private ArrayNode arrayNode;
	@Mock
	private InrInventoryJsonFlatten inrInventoryJsonFlatten;
	private ObjectMapper realMapper = new ObjectMapper();
	
	@Test
	public void generateCdirDataTest() throws IOException {
		ReflectionTestUtils.setField(previewDataService, "mapper", realMapper);
		when(nxOutputFileModel.getNxRequestDetails()).thenReturn(nxRequestDetails);
		when(nxRequestDetails.getNxReqId()).thenReturn(1L);
		when(nxRequestDetails.getNxSolutionDetail()).thenReturn(nxSolutionDetail);
		when(nxSolutionDetail.getNxSolutionId()).thenReturn(1L);
		when(nxRequestDetails.getManageBillingPriceJson()).thenReturn("{}");
		doReturn(objectNode).when(previewDataService).createExcelHeaderNode(any(), anyLong(), anyLong());
		when(nxOutputFileModel.getInventoryJson()).thenReturn("{\"root\":{}}");
		when(inrFactory.getInrInventoryJsonFlatten(any(), any())).thenReturn(inrInventoryJsonFlatten);
		when(inrInventoryJsonFlatten.generate()).thenReturn(arrayNode);
		when(nxOutputFileModel.getFallOutData()).thenReturn("{}");
		doNothing().when(previewDataService).processFalloutXlsm(any(), any(), any());
		
		previewDataService.generateCdirData(nxOutputFileModel);
	}
	
	@Test
	public void updateAuditCheckTest() throws IOException {
		ReflectionTestUtils.setField(previewDataService, "mapper", realMapper);
		when(nxOutputFileModel.getCdirData()).thenReturn("{\"rootTag\": [\"tag1\"]}");
		when(nxOutputFileModel.getInventoryJson()).thenReturn("{\"tag\":1}");
		when(nxOutputFileModel.getNxRequestDetails()).thenReturn(nxRequestDetails);
		Set<String> rootSet = new HashSet<>();
		rootSet.add("tag1");
		rootSet.add("tag2");
		doReturn(null).when(previewDataService).falloutMessageMap(any());
		doReturn(rootSet).when(previewDataService).addAuditCheck(any(), any(), any());
		
		previewDataService.updateAuditCheck(nxOutputFileModel);
	}
	
	@Test
	public void processFalloutXlsmTest() throws JsonParseException, JsonMappingException, IOException {
		ReflectionTestUtils.setField(previewDataService, "mapper", realMapper);
		Set<String> rootSet = new HashSet<>();
		rootSet.add("tag");
		rootSet.add("tag1");
		when(nxOutputFileModel.getFallOutData()).thenReturn("{\r\n" + 
				"    \"inventoryJsonLookups\": [{\r\n" + 
				"            \"longForm\": {\r\n" + 
				"                \"country\": \"US\"\r\n" + 
				"            },\r\n" + 
				"            \"shortForm\": {\r\n" + 
				"                \"country\": \"US\"\r\n" + 
				"            },\r\n" + 
				"            \"fallOutReason\": null\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}");
		doNothing().when(previewDataService).addNewFalloutRow(any(), anyList(), any(), any(), any());
		
		previewDataService.processFalloutXlsm(nxOutputFileModel, objectNode, rootSet);
	}
	
	@Test
	public void processInventoryTest() throws IOException {
		ReflectionTestUtils.setField(previewDataService, "mapper", realMapper);
		ObjectNode excelHeaderNode = realMapper.createObjectNode();
		doNothing().when(previewDataService).addSingleSheet(any(), any(), any(), any(), any());
		when(objectNode.withArray(any())).thenReturn(arrayNode);
		Set<String> rootSet = new HashSet<>();
		when(nxOutputFileModel.getInventoryJson()).thenReturn("{\r\n" + 
				"    \"InrDomCktResponse\": {\r\n" + 
				"        \"Body\": {\r\n" + 
				"            \"DomesticIOCInventory\": {}\r\n" + 
				"        }\r\n" + 
				"    }\r\n" + 
				"}");
		
		previewDataService.processInventory(nxOutputFileModel, objectNode, rootSet, excelHeaderNode);
		
		when(nxOutputFileModel.getInventoryJson()).thenReturn("{\r\n" + 
				"    \"InrDomCktResponse\": {\r\n" + 
				"        \"Body\": {\r\n" + 
				"            \"DomesticIOCInventory\": {},\r\n" + 
				"			\"DomesticEthernetIOCInventory\":{}\r\n" + 
				"        }\r\n" + 
				"    }\r\n" + 
				"}");
		when(inrFactory.getInrInventoryJsonFlatten(any(), any())).thenReturn(inrInventoryJsonFlatten);
		ArrayNode arrayNode2 = realMapper.createArrayNode();
		arrayNode2.add(objectNode);
		when(inrInventoryJsonFlatten.generate()).thenReturn(arrayNode2);
		previewDataService.processInventory(nxOutputFileModel, objectNode, rootSet, excelHeaderNode);
		
		when(nxOutputFileModel.getInventoryJson()).thenReturn("{\r\n" + 
				"    \"InrDomCktResponse\": {\r\n" + 
				"        \"Body\": {\r\n" + 
				"			\"DomesticEthernetIOCInventory\":{}\r\n" + 
				"        }\r\n" + 
				"    }\r\n" + 
				"}");
		previewDataService.processInventory(nxOutputFileModel, objectNode, rootSet, excelHeaderNode);
		
		when(nxOutputFileModel.getInventoryJson()).thenReturn("{\r\n" + 
				"    \"DDAResponse\": {\r\n" + 
				"        \"Body\": {\r\n" + 
				"			\"DomesticEthernetAccessInventory\":{},\r\n" + 
				"			\"DomesticDSODS1AccessInventory\":{},\r\n" + 
				"			\"DomesticDS3OCXAccessInventory\":{}\r\n" + 
				"        }\r\n" + 
				"    }\r\n" + 
				"}");
		ObjectNode objectNode2 = realMapper.createObjectNode();
		objectNode2.put("sequence", 1);
		when(objectNode.get(any())).thenReturn(objectNode2.get("sequence"));
		previewDataService.processInventory(nxOutputFileModel, objectNode, rootSet, excelHeaderNode);
	}
	
	@Test
	public void createExcelHeaderNodeTest() {
		ReflectionTestUtils.setField(previewDataService, "mapper", realMapper);
		ObjectNode manageBillingPriceJsonNode = realMapper.createObjectNode();
		manageBillingPriceJsonNode.put("searchCriteria", "duns");
		
		previewDataService.createExcelHeaderNode(manageBillingPriceJsonNode, 1L, 1L);
		
		manageBillingPriceJsonNode.put("searchCriteria", "mcn");
		manageBillingPriceJsonNode.put("billMonth", "011999");
		manageBillingPriceJsonNode.put("beginBillMonth", "011999");
		previewDataService.createExcelHeaderNode(manageBillingPriceJsonNode, 1L, 1L);
	}
	
	@Test
	public void addAuditCheckTest() throws IOException {
		ReflectionTestUtils.setField(previewDataService, "mapper", realMapper);
		when(nxRequestDetails.getNxReqId()).thenReturn(1L);
		Map<String, NxLookupData> cdirDesignAuditMap = new HashMap<>();
		NxLookupData nxLookupData = new NxLookupData();
		nxLookupData.setItemId("MYPRICE_CONFIG_FAILURE");
		nxLookupData.setDescription("Component(s) have not been configured in MyPrice");
		cdirDesignAuditMap.put("MYPRICE_CONFIG_FAILURE", nxLookupData);
		when(nxMyPriceRepositoryServce.getLookupDataByItemId("CDIR_DESIGN_AUDIT")).thenReturn(cdirDesignAuditMap);
		NxDesignAudit nxDesignAudit = new NxDesignAudit();
		nxDesignAudit.setTransaction("MYPRICE_CONFIG_FAILURE");
		when(nxDesignAuditRepository.findByNxRefIdAndTransaction(any(), any())).thenReturn(nxDesignAudit);
		nxDesignAudit.setData("[id1]");
		when(nxRequestDetails.getStatus()).thenReturn(90L);
		List<NxDesignAudit> audits = new ArrayList<>();
		NxDesignAudit nxDesignAudit2 = new NxDesignAudit();
		audits.add(nxDesignAudit2);
		when(nxRequestDetails.getNxSolutionDetail()).thenReturn(nxSolutionDetail);
		when(nxDesignAuditRepository.findByNxRefIdAndTransactionAndStatus(any(), any(), any())).thenReturn(audits);
		List<NxLookupData> nxLookupdataList=new ArrayList<>();
		NxLookupData nxLookupDataObj = new NxLookupData();
		nxLookupDataObj.setDescription("BVoIP,BVoIP Non-Usage");
		nxLookupdataList.add(nxLookupDataObj);
		when(nxLookupDataRepository.findByDatasetName(anyString())).thenReturn(nxLookupdataList);
		nxDesignAudit2.setData("{\r\n" + 
				"    \"restErrors\": [{\r\n" + 
				"            \"circuitId\": \"id2\",\r\n" + 
				"            \"messages\": \"Message1. Message2. Message3. Message4.\"\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}");
		List<String> idPaths = Arrays.asList("id");
		doReturn(idPaths).when(previewDataService).retrieveIdPaths();
		doNothing().when(previewDataService).addNewFalloutRow(any(), any(JsonNode.class), any(), any(), any());
		String resJson = "{\r\n" + 
				"    \"mainSheet\": [{\r\n" + 
				"            \"id\": \"id1\"\r\n" + 
				"        },\r\n" + 
				"		{\r\n" + 
				"            \"id\": \"id2\"\r\n" + 
				"        },\r\n" + 
				"		{\r\n" + 
				"            \"id\": \"id3\"\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}\r\n" + 
				"";
		ObjectNode res = (ObjectNode) realMapper.readTree(resJson);
		Map<String, List<String>> falloutMessageMap = previewDataService.falloutMessageMap(nxRequestDetails);
		previewDataService.addAuditCheck(falloutMessageMap, res, null);
		
		//case 2: Exception in parsing NX_DESIGN_AUDIT DATA column to json node
		nxDesignAudit2.setData("{\r\n" + 
				"    \"restErrors\": [{\r\n" + 
				"            \"circuitId\": \"id2\"\r\n" + 
				"            \"messages\": \"Message1. Message2. Message3. Message4.\"\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}");
		previewDataService.addAuditCheck(falloutMessageMap, res, null);
	}
	
	@Test
	public void addNewFalloutRowTest() {
		ReflectionTestUtils.setField(previewDataService, "mapper", realMapper);
		List<String> rootSetList = Arrays.asList("tag", "tag1");
		Map<String, String> longForm = new HashMap<>();
		Map<String, String> shortForm = new HashMap<>();
		Set<String> rootSet = new HashSet<>();
		InventoryJsonLookup inventoryJsonLookup = new InventoryJsonLookup();
		shortForm.put("1", "1");
		longForm.put("/InrDomCktResponse/Body/DomesticIOCInventory", "2");
		inventoryJsonLookup.setShortForm(shortForm);
		inventoryJsonLookup.setLongForm(longForm);
		inventoryJsonLookup.setFallOutReason("fallOutReason");
		when(objectNode.withArray(any())).thenReturn(arrayNode);
		
		previewDataService.addNewFalloutRow(objectNode, rootSetList, inventoryJsonLookup, "", rootSet);
		
		ObjectNode objectNode2 = realMapper.createObjectNode();
		objectNode2.put("rootTag", "tag");
		previewDataService.addNewFalloutRow(objectNode, objectNode2, inventoryJsonLookup, "", rootSet);
	}
	
	@Test
	public void retrieveIdPathsTest() {
		NxLookupData nxLookupData = new NxLookupData();
		nxLookupData.setDescription("description");
		List<NxLookupData> nxLookUpData = Arrays.asList(nxLookupData);
		when(nxLookupDataRepository.findByDatasetName("INR_CDIR_IDS")).thenReturn(nxLookUpData);
		
		List<String> idPaths = previewDataService.retrieveIdPaths();
		List<String> expected = Arrays.asList("description");
		assertEquals(expected, idPaths);
	}
}
