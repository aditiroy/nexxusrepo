package com.att.sales.nexxus.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.inr.InrFactory;
import com.att.sales.nexxus.inr.InrPreviewGeneratorV1;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@ExtendWith(MockitoExtension.class)
public class InrEditCreateExcelServiceTest {
	@Spy
	@InjectMocks
	private InrEditCreateExcelService inrEditCreateExcelService;

	@Mock
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;

	@Mock
	private InrFactory inrFactory;

	@Mock
	private NxRequestDetailsRepository nxRequestDetailsRepository;
	
	@Mock
	private InrPreviewGeneratorV1 inrAddressEditGenerator;

	private ObjectMapper realMapper = new ObjectMapper();

	@BeforeEach
	public void init() {
		ReflectionTestUtils.setField(inrEditCreateExcelService, "mapper", realMapper);
	}

	@Test
	public void generateInrAddressEditSheetTest() throws IOException {
		List<NxRequestDetails> nxRequestDetailsList = new ArrayList<>();
		when(nxRequestDetailsRepository.findByNxSolutionId(any())).thenReturn(nxRequestDetailsList);
		LinkedHashMap<String, String> dataConvertRuleMap = new LinkedHashMap<>();
		when(nxMyPriceRepositoryServce.getDataFromLookup(any())).thenReturn(dataConvertRuleMap);
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		nxRequestDetailsList.add(nxRequestDetails);
		NxOutputFileModel nxOutputFileModel = new NxOutputFileModel();
		List<NxOutputFileModel> nxOutputFileModelList = new ArrayList<>();
		nxOutputFileModelList.add(nxOutputFileModel);
		nxRequestDetails.setNxOutputFiles(nxOutputFileModelList);
		doNothing().when(inrEditCreateExcelService).saveAddressToMap(any(), any(), any(), any());
		ArrayNode excelData = realMapper.createArrayNode();
		doReturn(excelData).when(inrEditCreateExcelService).generateExcelData(any());
		when(inrFactory.getInrAddressEditGenerator(any(), anyInt())).thenReturn(inrAddressEditGenerator);
//		inrEditCreateExcelService.generateInrAddressEditSheet(1L);
	}
	
	@Test
	public void generateExcelDataTest() {
		ObjectNode objNode = realMapper.createObjectNode();
		Map<Integer, ObjectNode> rowMap = new LinkedHashMap<>();
		rowMap.put(1, objNode);
		inrEditCreateExcelService.generateExcelData(rowMap);
	}
	
	@Test
	public void saveAddressToMapTest() {
		String mpOutputJson = "{\"beginBillMonth\":\"January 2017\",\"service\":\"AVPN\",\"customerName\":\"KRATOS DEFENSE & SECURITY SOLUTIONS INC\",\"searchCriteria\":\"DunsNumber\",\"accountDetails\":[{\"currency\":\"USD\",\"custName\":\"KRATOS DEFENSE & SECURITY SOLUTIONS INC\",\"site\":[{\"country\":\"US\",\"state\":\"NJ\",\"city\":\"SAN DIEGO\",\"address\":\"new Borkley\",\"siteId\":\"90816966\",\"custPostalcode\":\"54321\",\"design\":[{\"siteName\":\"2322190\",\"accessCarrier\":\"AT&T\",\"circuitId\":\"BMEC959625ATI\",\"portSpeed\":\"500000\",\"clli\":\"GRDNCA02\",\"accessBandwidth\":\"500000\",\"NXSITEMATCHINGID\":47,\"priceDetails\":[{\"priceType\":\"PORTOVERAGEBEID\",\"beid\":\"96936\",\"quantity\":\"1\",\"localListPrice\":\"9224\",\"actualPrice\":\"1844.8\",\"secondaryKey\":\"#FCC#TACACS#TACACS Level 2 Employee Enablement#N/A#N/A#Not Discountable#per event#N/A#22867#United States#US#USA\",\"elementType\":\"RouterFeatures\",\"uniqueId\":\"#FCC#Managed Router Features#Per ID#TACACS#Managed CPE#Custom Features\",\"nxItemId\":7643292}],\"nxKeyId\":\"7643292_1844.8$US\",\"nxSiteId\":190},{\"siteName\":\"change\",\"accessCarrier\":\"AT&T\",\"technology\":\"Ethernet (Gateway Interconnect/ESP ETH Shared)\",\"accessBandwidth\":\"20000 Kbps\",\"NXSITEMATCHINGID\":46,\"priceDetails\":[{\"priceType\":\"PORTOVERAGEBEID\",\"beid\":\"96936\",\"quantity\":\"1\",\"localListPrice\":\"2036\",\"actualPrice\":\"509\",\"secondaryKey\":\"#FCC#TACACS#TACACS Level 2 Employee Enablement#N/A#N/A#Not Discountable#per event#N/A#22867#United States#US#USA\",\"elementType\":\"RouterFeatures\",\"uniqueId\":\"#FCC#Managed Router Features#Per ID#TACACS#Managed CPE#Custom Features\",\"nxItemId\":7643292}],\"nxKeyId\":\"7643292_509$US\",\"nxSiteId\":191}],\"FALLOUTMATCHINGID\":\"0000000048/AVPNPricingInventory/Body/AccountDetails/AVPNService/AVPNInventoryDetails/SiteDetails/FALLOUTMATCHINGID\"}]}],\"flowType\":\"INR\"}";
		String dataConvertRule = "[[{\"objName\":\"design\",\"excelTag\":\"NX_Site_ID\",\"treeTag\":\"nxSiteId\"},{\"objName\":\"site\",\"excelTag\":\"Address1\",\"treeTag\":\"address\"},{\"objName\":\"site\",\"excelTag\":\"State\",\"treeTag\":\"state\"},{\"objName\":\"site\",\"excelTag\":\"City\",\"treeTag\":\"city\"},{\"objName\":\"site\",\"excelTag\":\"Zip\",\"treeTag\":\"custPostalcode\"},{\"objName\":\"design\",\"excelTag\":\"ICORESITEID\",\"treeTag\":\"siteName\"},{\"objName\":\"site\",\"excelTag\":\"GRSSITEID\",\"treeTag\":\"siteId\"},{\"objName\":\"site\",\"excelTag\":\"NPA-NXX\",\"treeTag\":\"npanxx\"},{\"objName\":\"site\",\"excelTag\":\"SWCCLI\",\"treeTag\":\"swccli\"},{\"objName\":\"site\",\"excelTag\":\"POPCLLI\",\"treeTag\":\"popclli\"}]]";
		String offer = "AVPN";
		Map<Integer, ObjectNode> rowMap = new LinkedHashMap<>();
		inrEditCreateExcelService.saveAddressToMap(rowMap, dataConvertRule, mpOutputJson, offer);
		
		inrEditCreateExcelService.saveAddressToMap(rowMap, dataConvertRule, mpOutputJson, "");
		
		inrEditCreateExcelService.saveAddressToMap(rowMap, dataConvertRule, null, "");
	}
	
	@Test
	public void uploadExcelDataTest() throws IOException {
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		nxRequestDetails.setProduct("AVPN");
		NxOutputFileModel nxOutputFileModel = new NxOutputFileModel();
		List<NxOutputFileModel> nxOutputFileModelList = new ArrayList<>();
		nxOutputFileModelList.add(nxOutputFileModel);
		nxRequestDetails.setNxOutputFiles(nxOutputFileModelList);
		nxOutputFileModel.setIntermediateJson("{\"beginBillMonth\":\"January 2017\",\"service\":\"AVPN\",\"customerName\":\"KRATOS DEFENSE & SECURITY SOLUTIONS INC\",\"searchCriteria\":\"DunsNumber\",\"accountDetails\":[{\"currency\":\"USD\",\"custName\":\"KRATOS DEFENSE & SECURITY SOLUTIONS INC\",\"site\":[{\"country\":\"US\",\"state\":\"NJ\",\"city\":\"SAN DIEGO\",\"address\":\"new Borkley\",\"siteId\":\"90816966\",\"custPostalcode\":\"54321\",\"design\":[{\"siteName\":\"2322190\",\"accessCarrier\":\"AT&T\",\"circuitId\":\"BMEC959625ATI\",\"portSpeed\":\"500000\",\"clli\":\"GRDNCA02\",\"accessBandwidth\":\"500000\",\"NXSITEMATCHINGID\":47,\"priceDetails\":[{\"priceType\":\"PORTOVERAGEBEID\",\"beid\":\"96936\",\"quantity\":\"1\",\"localListPrice\":\"9224\",\"actualPrice\":\"1844.8\"}]},{\"siteName\":\"change\",\"accessCarrier\":\"AT&T\",\"technology\":\"Ethernet (Gateway Interconnect/ESP ETH Shared)\",\"accessBandwidth\":\"20000 Kbps\",\"NXSITEMATCHINGID\":46,\"priceDetails\":[{\"priceType\":\"PORTOVERAGEBEID\",\"beid\":\"96936\",\"quantity\":\"1\",\"localListPrice\":\"2036\",\"actualPrice\":\"509\"}]}],\"FALLOUTMATCHINGID\":\"0000000048/AVPNPricingInventory/Body/AccountDetails/AVPNService/AVPNInventoryDetails/SiteDetails/FALLOUTMATCHINGID\"}]}],\"flowType\":\"INR\"}");
		nxOutputFileModel.setMpOutputJson("{\"beginBillMonth\":\"January 2017\",\"service\":\"AVPN\",\"customerName\":\"KRATOS DEFENSE & SECURITY SOLUTIONS INC\",\"searchCriteria\":\"DunsNumber\",\"accountDetails\":[{\"currency\":\"USD\",\"custName\":\"KRATOS DEFENSE & SECURITY SOLUTIONS INC\",\"site\":[{\"country\":\"US\",\"state\":\"NJ\",\"city\":\"SAN DIEGO\",\"address\":\"new Borkley\",\"siteId\":\"90816966\",\"custPostalcode\":\"54321\",\"design\":[{\"siteName\":\"2322190\",\"accessCarrier\":\"AT&T\",\"circuitId\":\"BMEC959625ATI\",\"portSpeed\":\"500000\",\"clli\":\"GRDNCA02\",\"accessBandwidth\":\"500000\",\"NXSITEMATCHINGID\":47,\"priceDetails\":[{\"priceType\":\"PORTOVERAGEBEID\",\"beid\":\"96936\",\"quantity\":\"1\",\"localListPrice\":\"9224\",\"actualPrice\":\"1844.8\",\"secondaryKey\":\"#FCC#TACACS#TACACS Level 2 Employee Enablement#N/A#N/A#Not Discountable#per event#N/A#22867#United States#US#USA\",\"elementType\":\"RouterFeatures\",\"uniqueId\":\"#FCC#Managed Router Features#Per ID#TACACS#Managed CPE#Custom Features\",\"nxItemId\":7643292}],\"nxKeyId\":\"7643292_1844.8$US\",\"nxSiteId\":190},{\"siteName\":\"change\",\"accessCarrier\":\"AT&T\",\"technology\":\"Ethernet (Gateway Interconnect/ESP ETH Shared)\",\"accessBandwidth\":\"20000 Kbps\",\"NXSITEMATCHINGID\":46,\"priceDetails\":[{\"priceType\":\"PORTOVERAGEBEID\",\"beid\":\"96936\",\"quantity\":\"1\",\"localListPrice\":\"2036\",\"actualPrice\":\"509\",\"secondaryKey\":\"#FCC#TACACS#TACACS Level 2 Employee Enablement#N/A#N/A#Not Discountable#per event#N/A#22867#United States#US#USA\",\"elementType\":\"RouterFeatures\",\"uniqueId\":\"#FCC#Managed Router Features#Per ID#TACACS#Managed CPE#Custom Features\",\"nxItemId\":7643292}],\"nxKeyId\":\"7643292_509$US\",\"nxSiteId\":191}],\"FALLOUTMATCHINGID\":\"0000000048/AVPNPricingInventory/Body/AccountDetails/AVPNService/AVPNInventoryDetails/SiteDetails/FALLOUTMATCHINGID\"}]}],\"flowType\":\"INR\"}");
		String dataConvertRule = "[[{\"objName\":\"design\",\"excelTag\":\"NX_Site_ID\",\"treeTag\":\"nxSiteId\"},{\"objName\":\"site\",\"excelTag\":\"Address1\",\"treeTag\":\"address\"},{\"objName\":\"site\",\"excelTag\":\"State\",\"treeTag\":\"state\"},{\"objName\":\"site\",\"excelTag\":\"City\",\"treeTag\":\"city\"},{\"objName\":\"site\",\"excelTag\":\"Zip\",\"treeTag\":\"custPostalcode\"},{\"objName\":\"design\",\"excelTag\":\"ICORESITEID\",\"treeTag\":\"siteName\"},{\"objName\":\"site\",\"excelTag\":\"GRSSITEID\",\"treeTag\":\"siteId\"},{\"objName\":\"site\",\"excelTag\":\"NPA-NXX\",\"treeTag\":\"npanxx\"},{\"objName\":\"site\",\"excelTag\":\"SWCCLI\",\"treeTag\":\"swccli\"},{\"objName\":\"site\",\"excelTag\":\"POPCLLI\",\"treeTag\":\"popclli\"}]]";
		List<LinkedHashMap<String, Object>> cdirData = new ArrayList<>();
		LinkedHashMap<String, Object> row = new LinkedHashMap<>();
		row.put("NX_Site_ID", "190");
		row.put("State", "NJ");
		LinkedHashMap<String, Object> row2 = new LinkedHashMap<>();
		row2.put("NX_Site_ID", "191");
		row2.put("State", "NJ");
		cdirData.add(row);
		cdirData.add(row2);
		inrEditCreateExcelService.uploadExcelData(nxRequestDetails, dataConvertRule, cdirData);
	}
}
