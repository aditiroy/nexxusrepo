package com.att.sales.nexxus.inr;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxOutputFileRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
@ExtendWith(MockitoExtension.class)
public class CdirEditTest {

	@InjectMocks
	private CdirEdit cdirEdit;

	@Mock
	private NexxusJsonUtility nexxusJsonUtility;

	@Mock
	private NxLookupDataRepository nxLookupDataRepository;

	@Mock
	private ObjectMapper mapper;

	@Mock
	private NxOutputFileRepository nxOutputFileRepository;
	
	@Mock
	private NxRequestDetailsRepository nxRequestDetailsRepository;

	@Test
	public void updateCdirDataTest() {
		try {
			NxRequestDetails nxRequestDetails = new NxRequestDetails();
			nxRequestDetails.setProduct("MIS/PNT");
			Mockito.when(nxRequestDetailsRepository.findByNxReqId(anyLong())).thenReturn(nxRequestDetails);
			NxOutputFileModel nxOutputFileModel = new NxOutputFileModel();
			nxOutputFileModel.setCdirData("{ \"mainSheet\": [ { \"rootTag\": \"InrMISPNTResponse\", \"/InrMISPNTResponse/Header/Product\": \"MIS/PNT\", \"/InrMISPNTResponse/Body/AccountDetails/SAPIDDetails/CircuitID\": \"ckt00009\", \"/InrMISPNTResponse/Body/AccountDetails/SAPIDDetails/NXSITEMATCHINGID\": 47, \"/InrMISPNTResponse/Body/AccountDetails/SAPIDDetails/FALLOUTMATCHINGID\": \"0000000009/InrMISPNTResponse/Body/AccountDetails/SAPIDDetails/FALLOUTMATCHINGID\", \"sequence\": 1, \"nxSiteId\": 1, \"NXSITEMATCHINGID\": 47, "
					+ "\"FALLOUTMATCHINGID\": \"0000000009/InrMISPNTResponse/Body/AccountDetails/SAPIDDetails/FALLOUTMATCHINGID\" } ] }");
			List<NxOutputFileModel> nxOutputFileModels = new ArrayList<>();
			nxOutputFileModels.add(nxOutputFileModel);
			nxRequestDetails.setNxOutputFiles(nxOutputFileModels);
			List<NxLookupData> nxLookupDetail = new ArrayList<>();
			NxLookupData nxLookupData = new NxLookupData();
			nxLookupData.setDatasetName("CDIR_DATA_EDIT");
			nxLookupData.setDescription("Data Update");
			nxLookupData.setItemId("MIS/PNT");
			nxLookupData.setCriteria(
					"{ \"/InrMISPNTResponse/Body/AccountDetails/SAPIDDetails/NXSITEMATCHINGID\": { \"rootPath\": \"/InrMISPNTResponse/Body/AccountDetails\", \"nxSiteIdPath\": \"nxSiteId\", \"Circuit ID\": \"/SAPIDDetails/CircuitID\" } }");
			nxLookupDetail.add(nxLookupData);
			Mockito.when(nxLookupDataRepository.findByDatasetNameAndItemIdAndDescription(anyString(), anyString(),
					anyString())).thenReturn(nxLookupDetail);

			NexxusJsonUtility nj = new NexxusJsonUtility();
			LinkedHashMap criteriaMap = (LinkedHashMap<String, Object>) nj
					.convertStringJsonToMap(nxLookupData.getCriteria());
			when(nexxusJsonUtility.convertStringJsonToMap(Mockito.anyString())).thenReturn(criteriaMap);

			ObjectMapper mp = new ObjectMapper();
			JsonNode nodes = mp.readTree(nxOutputFileModel.getCdirData());
			when(mapper.readTree(anyString())).thenReturn(nodes);
			
			Map<String, LinkedHashMap<String, Object>> dataMap = new HashMap<String, LinkedHashMap<String, Object>>();
			LinkedHashMap<String, Object> row11 = new LinkedHashMap<String, Object>();
			row11.put("Circuit ID", "ckt00009");
			row11.put("NX_Site ID", "1");
			dataMap.put("47", row11);

			cdirEdit.updateCdirData(dataMap, 7944L);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Test
	public void updateCdirAddressData() {
		try {
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		nxRequestDetails.setProduct("DOMESTIC PL IOC");
		Mockito.when(nxRequestDetailsRepository.findByNxReqId(anyLong())).thenReturn(nxRequestDetails);
		NxOutputFileModel nxOutputFileModel = new NxOutputFileModel();
		nxOutputFileModel.setCdirData(
				"{\"mainSheet\": [ { \"rootTag\": \"InrDomCktResponse\", \"/InrDomCktResponse/v1:Header/v1:CustomerName\": \"NA\", \"/InrDomCktResponse/v1:Header/v1:Product\": \"DOMESTIC PL IOC\", \"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CLLIZEnd\": \"2clizend\", \"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CLLIAEnd\": \"sitePOPCLLI1\", \"sequence\": 1, \"nxSiteId\": 1, \"nxSiteIdZ\": 2, \"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/NXSITEMATCHINGID\": 1, \"FALLOUTMATCHINGID\": \"0000000010/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" } ] }");
		List<NxOutputFileModel> nxOutputFileModels = new ArrayList<>();
		nxOutputFileModels.add(nxOutputFileModel);
		nxRequestDetails.setNxOutputFiles(nxOutputFileModels);
		List<NxLookupData> nxLookupDetail = new ArrayList<>();
		NxLookupData nxLookupData = new NxLookupData();
		nxLookupData.setDatasetName("CDIR_DATA_EDIT");
		nxLookupData.setDescription("Address Update");
		nxLookupData.setCriteria("{	\"siteLocator\":{\"nxSiteIdPath\":\"nxSiteId || nxSiteIdZ\"}, \"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/NXSITEMATCHINGID\": { \"type\": \"EPLSWAN\", \"rootPath\": \"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo\", \"CLLIZEND\":\"/USOCInfo/CLLIZEnd\", \"POPCLLI\":\"/USOCInfo/CLLIAEnd\" }, \"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/NXSITEMATCHINGID\": { \"type\": \"PrivateLineService\", \"rootPath\": \"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo\", \"CLLIZEND\":\"/USOCInfo/CLLIZEnd\", \"POPCLLI\":\"/USOCInfo/CLLIAEnd\" } }");

		nxLookupDetail.add(nxLookupData);
		Mockito.when(nxLookupDataRepository.findByDatasetNameAndItemIdAndDescription(anyString(), anyString(),
				anyString())).thenReturn(nxLookupDetail);

		NexxusJsonUtility nj = new NexxusJsonUtility();
		LinkedHashMap criteriaMap = (LinkedHashMap<String, Object>) nj
				.convertStringJsonToMap(nxLookupData.getCriteria());
		when(nexxusJsonUtility.convertStringJsonToMap(Mockito.anyString())).thenReturn(criteriaMap);

		ObjectMapper mp = new ObjectMapper();
		JsonNode nodes = mp.readTree(nxOutputFileModel.getCdirData());
		when(mapper.readTree(anyString())).thenReturn(nodes);
		
		Map<Long, List<LinkedHashMap<String, Object>>> datamap = new HashMap<>();
		List<LinkedHashMap<String, Object>> datarow= new ArrayList();
		LinkedHashMap<String, Object> adrow1= new LinkedHashMap<String, Object>();
		adrow1.put("NX_Site_ID", "1");
		adrow1.put("POPCLLI", "sitePOPCLLI3");

		LinkedHashMap<String, Object> adrow2= new LinkedHashMap<String, Object>();
		adrow2.put("NX_Site_ID", "2");
		adrow2.put("POPCLLI", "4clizend");
		
		datarow.add(adrow1);
		datarow.add(adrow2);
		
		datamap.put(7432L, datarow);
		cdirEdit.updateCdirAddressData(datamap);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
