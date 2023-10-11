/**
 * 
 */
package com.att.sales.nexxus.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anySet;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.CommonConstants;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.NxRequestGroup;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxRequestGroupRepository;
import com.att.sales.nexxus.inr.CdirEdit;
import com.att.sales.nexxus.inr.CopyOutputToIntermediateJson;
import com.att.sales.nexxus.inr.InrFactory;
import com.att.sales.nexxus.inr.InrIntermediateJsonUpdate;
import com.att.sales.nexxus.inr.OutputJsonFallOutData;
import com.att.sales.nexxus.model.BulkUploadInrUpdateRequest;
import com.att.sales.nexxus.model.BulkUploadInrUpdateResponse;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author sj0546
 *
 */
@ExtendWith(MockitoExtension.class)
public class BulkUploadInrUpdateServiceTest {
	
	@Spy
	@InjectMocks
	private BulkUploadInrUpdateService bulkUploadInrUpdateService;
	
	@Mock
	private NxRequestDetailsRepository nxRequestDetailsRepository;
	
	@Mock
	private NexxusService nexxusService;
	
	@Mock 
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Mock
	private NexxusJsonUtility nexxusJsonUtility;
	
	@Mock
	private ObjectMapper mapper;
	
	@Mock
	private CopyOutputToIntermediateJson copyOutputToIntermediateJson;
	
	@Mock
	private InrIntermediateJsonUpdate inrIntermediateJsonUpdate;
	
	@Mock
	private InrProcessingService inrProcessingService;
	
	@Mock
	private NxDesignAuditRepository nxDesignAuditRepository;
	
	@Mock
	private CdirEdit cdirEdit;
	
	@Mock
	private InrFactory inrFactory;
	
	@Mock
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	@Mock
	private NxRequestGroupRepository nxRequestGroupRepository;
	
	@Mock
	private InrEditCreateExcelService inrEditCreateExcelService;
	
	@Mock
	private InrQualifyService inrQualifyService;
	
	@Mock
	private JsonPathUtil jsonPathUtil;
	
	private BulkUploadInrUpdateRequest uploadRequest;
	private NxRequestDetails nxReq;
	private String bulkUploadFilePath="src/main/resources/testResources/INR_EDIT_DDA_UPDATE.xlsx";
	private String addressUploadFilePath = "src/main/resources/testResources/INR_EDIT_ADDRESS_UPDATE.xlsx";
	
	@BeforeEach
	public void init() throws FileNotFoundException {
		uploadRequest = new BulkUploadInrUpdateRequest();
		uploadRequest.setProduct("DOMESTIC DEDICATED ACCESS");
		InputStream fis = new FileInputStream(bulkUploadFilePath);
		uploadRequest.setInputStream(fis);
		uploadRequest.setAction("dataUpdate");
		nxReq = new NxRequestDetails();
		NxOutputFileModel fileModel = new NxOutputFileModel();
		fileModel.setIntermediateJson("{\"beginBillMonth\":\"September 2019\",\"service\":\"DOMESTIC DEDICATED ACCESS\",\"customerName\":\"AETNA\",\"DomesticEthernetAccessInventory\":{\"CustomerAccountInfo\":[{\"CustomerSubAccountInfo\":{\"CustomerCircuitInfo\":[{\"ServiceIndicator\":\"ADI\",\"CktId\":\"BFEC558376ATI\",\"CustomerLocationInfo\":[{\"ServiceState\":\"VA\",\"USOCInfo\":[{\"DisplaySpeed\":\"10000\",\"USOC\":\"1LNET\",\"NetRate\":\"0\",\"AccessSpeed\":\"10 GBPS BASIC EPL-WAN\"}],\"PhysicalInterface\":\"10GBASELR\",\"ServiceZip\":\"20147-6207\",\"ServiceCity\":\"ASHBURN\",\"ServiceProvider\":\"SBC*LNS\",\"AttCtrOffcCLLICd\":\"WASHDCDT\",\"ServiceAddress2\":\"FLR 2 RM CAGE 2113\",\"ServiceAddress1\":\"21721  FILIGREE CT\",\"AccessArchitecture\":\"DEDICATED\",\"CustSrvgWireCtrCLLICd\":\"WASHDCDN\",\"NXSITEMATCHINGID\":4,\"nxSiteId\":2,\"endPointType\":\"A\",\"nxKeyId\":\"10 GBPS BASIC EPL-WAN$DEDICATED$10GBASELR\"}],\"dataUpdated\":\"Y\"}]}},{\"CustomerSubAccountInfo\":{\"CustomerCircuitInfo\":[{\"ServiceIndicator\":\"AVPN\",\"CktId\":\"IZEC579038ATI\",\"CustomerLocationInfo\":[{\"ServiceState\":\"CT\",\"USOCInfo\":[{\"DisplaySpeed\":\"1000\",\"USOC\":\"1LNET\",\"NetRate\":\"0\",\"AccessSpeed\":\"1 GBPS BASIC EPL-WAN\"}],\"PhysicalInterface\":\"1000BASELX\",\"ServiceZip\":\"06457-0000\",\"ServiceCity\":\"MIDDLETOWN\",\"ServiceProvider\":\"SBC*LNS\",\"AttCtrOffcCLLICd\":\"CHSHCT02\",\"ServiceAddress2\":\"BLDG DATA CTR FLR 3RD RM COMPUTER\",\"ServiceAddress1\":\"1000  MIDDLE ST\",\"AccessArchitecture\":\"SWITCHED\",\"CustSrvgWireCtrCLLICd\":\"CRWLCT00\",\"NXSITEMATCHINGID\":2,\"nxSiteId\":3,\"endPointType\":\"A\",\"nxKeyId\":\"1 GBPS BASIC EPL-WAN$SWITCHED$1000BASELX\"},{\"ServiceState\":\"NY\",\"USOCInfo\":[{\"DisplaySpeed\":\"1000\",\"USOC\":\"1LNET\",\"NetRate\":\"0\",\"AccessSpeed\":\"1 GBPS BASIC EPL-WAN\"}],\"PhysicalInterface\":\"1000BASELX\",\"ServiceZip\":\"10013\",\"ServiceCity\":\"NEW YORK\",\"ServiceProvider\":\"SBC*LNS\",\"AttCtrOffcCLLICd\":\"NYCMNY54\",\"ServiceAddress2\":\"BLDG LNS FLR 10TH RM LNS CAGE\",\"ServiceAddress1\":\"32  AVENUE OF THE AMERICAS\",\"AccessArchitecture\":\"DEDICATED\",\"CustSrvgWireCtrCLLICd\":\"NYCMNYVS\",\"NXSITEMATCHINGID\":1,\"nxSiteId\":4,\"endPointType\":\"Z\",\"nxKeyId\":\"1 GBPS BASIC EPL-WAN$DEDICATED$1000BASELX\"}],\"dataUpdated\":\"Y\"}]}}]},\"DomesticDS3OCXAccessInventory\":{\"CustomerAccountInfo\":[{\"CustomerSubAccountInfo\":{\"CustomerCircuitInfo\":[{\"PrimClsOfSvcCd\":\"AVAU5\",\"ServiceIndicator\":\"POS\",\"CktId\":\"AGEC572447ATI\",\"CustomerLocationInfo\":[{\"AttCtrOffcCLLICd\":\"PTLDMEFO\",\"SiteNPANXX\":\"207774\",\"USOCInfo\":[{\"USOC\":\"1LNM2\",\"NetRate\":\"3780.00\",\"AccessSpeed\":\"OC3\",\"secondaryKey\":\"#FCC#OC3 Local Channel Service#OC3#Unprotected\",\"nxItemId\":1788208}],\"CustSrvgWireCtrCLLICd\":\"PTLDMEFO\",\"NXSITEMATCHINGID\":15,\"nxSiteId\":5,\"endPointType\":\"A\",\"TypeLocalAccess\":\"Unprotected\",\"LittleProductName\":\"LD DS3 OCx Access\",\"ProductTypeLocalAccess\":\"OC3 Local Channel Service\",\"nxKeyId\":\"1788208\"}],\"FALLOUTMATCHINGID\":\"0000000016/DDAResponse/Body/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\"},{\"PrimClsOfSvcCd\":\"AVAU5\",\"ServiceIndicator\":\"POS\",\"CktId\":\"AGEC648238ATI\",\"CustomerLocationInfo\":[{\"AttCtrOffcCLLICd\":\"FTLDFLOV\",\"SiteNPANXX\":\"954693\",\"USOCInfo\":[{\"USOC\":\"1LNM1\",\"NetRate\":\"1925.00\",\"AccessSpeed\":\"OC3\",\"secondaryKey\":\"#FCC#OC3 Local Channel Service#OC3#Unprotected\",\"nxItemId\":1788208}],\"CustSrvgWireCtrCLLICd\":\"FTLDFLJA\",\"NXSITEMATCHINGID\":13,\"nxSiteId\":6,\"endPointType\":\"A\",\"TypeLocalAccess\":\"Unprotected\",\"LittleProductName\":\"LD DS3 OCx Access\",\"ProductTypeLocalAccess\":\"OC3 Local Channel Service\",\"nxKeyId\":\"1788208\"}],\"FALLOUTMATCHINGID\":\"0000000014/DDAResponse/Body/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\"},{\"PrimClsOfSvcCd\":\"AVAU5\",\"ServiceIndicator\":\"POS\",\"CktId\":\"AGEC880883ATI\",\"CustomerLocationInfo\":[{\"AttCtrOffcCLLICd\":\"BSMRNDJC\",\"SiteNPANXX\":\"701221\",\"USOCInfo\":[{\"USOC\":\"1LNM1\",\"NetRate\":\"3250.00\",\"AccessSpeed\":\"OC3\",\"secondaryKey\":\"#FCC#OC3 Local Channel Service#OC3#Unprotected\",\"nxItemId\":1788208}],\"CustSrvgWireCtrCLLICd\":\"BSMRNDBC\",\"NXSITEMATCHINGID\":11,\"nxSiteId\":7,\"endPointType\":\"A\",\"TypeLocalAccess\":\"Unprotected\",\"LittleProductName\":\"LD DS3 OCx Access\",\"ProductTypeLocalAccess\":\"OC3 Local Channel Service\",\"nxKeyId\":\"1788208\"}],\"FALLOUTMATCHINGID\":\"0000000012/DDAResponse/Body/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\"},{\"PrimClsOfSvcCd\":\"AVFV5\",\"ServiceIndicator\":\"POS\",\"CktId\":\"IWEC723444ATI\",\"CustomerLocationInfo\":[{\"AttCtrOffcCLLICd\":\"CHSHCT02\",\"SiteNPANXX\":\"860613\",\"USOCInfo\":[{\"USOC\":\"1LNVE\",\"NetRate\":\"7102.00\",\"AccessSpeed\":\"OC48\",\"secondaryKey\":\"#FCC#OC48 Local Channel Service#OC48#Unprotected\",\"nxItemId\":1788237}],\"CustSrvgWireCtrCLLICd\":\"CRWLCT00\",\"NXSITEMATCHINGID\":9,\"nxSiteId\":8,\"endPointType\":\"A\",\"TypeLocalAccess\":\"Unprotected\",\"LittleProductName\":\"LD DS3 OCx Access\",\"ProductTypeLocalAccess\":\"OC48 Local Channel Service\",\"nxKeyId\":\"1788237\"}],\"FALLOUTMATCHINGID\":\"0000000010/DDAResponse/Body/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\"}]}},{\"CustomerSubAccountInfo\":{\"CustomerCircuitInfo\":[{\"PrimClsOfSvcCd\":\"HPPKE\",\"ServiceIndicator\":\"PL\",\"CktId\":\"IVEC990793ATI\",\"CustomerLocationInfo\":[{\"AttCtrOffcCLLICd\":\"HRFRCT03\",\"SiteNPANXX\":\"860624\",\"USOCInfo\":[{\"USOC\":\"ZDRMK\",\"NetRate\":\"1927.00\",\"AccessSpeed\":\"OC12\",\"secondaryKey\":\"#FCC#OC12 Local Channel Service#OC12#Unprotected\",\"nxItemId\":1788234}],\"CustSrvgWireCtrCLLICd\":\"WNDSCT00\",\"NXSITEMATCHINGID\":7,\"nxSiteId\":9,\"endPointType\":\"A\",\"TypeLocalAccess\":\"Unprotected\",\"LittleProductName\":\"LD DS3 OCx Access\",\"ProductTypeLocalAccess\":\"OC12 Local Channel Service\",\"nxKeyId\":\"1788234\"},{\"AttCtrOffcCLLICd\":\"NWRKNJ02\",\"SiteNPANXX\":\"973275\",\"USOCInfo\":[{\"USOC\":\"ZDRML\",\"NetRate\":\"2508.00\",\"AccessSpeed\":\"OC12\",\"secondaryKey\":\"#FCC#OC12 Local Channel Service#OC12#Unprotected\",\"nxItemId\":1788234}],\"CustSrvgWireCtrCLLICd\":\"SORGNJSO\",\"NXSITEMATCHINGID\":6,\"nxSiteId\":10,\"endPointType\":\"Z\",\"TypeLocalAccess\":\"Unprotected\",\"LittleProductName\":\"LD DS3 OCx Access\",\"ProductTypeLocalAccess\":\"OC12 Local Channel Service\",\"nxKeyId\":\"1788234\"}],\"FALLOUTMATCHINGID\":\"0000000008/DDAResponse/Body/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\"}]}}]},\"flowType\":\"INR\"}");
		fileModel.setMpOutputJson("{\"beginBillMonth\":\"September 2019\",\"service\":\"DOMESTIC DEDICATED ACCESS\",\"customerName\":\"AETNA\",\"DomesticEthernetAccessInventory\":{\"CustomerAccountInfo\":[{\"CustomerSubAccountInfo\":{\"CustomerCircuitInfo\":[{\"ServiceIndicator\":\"ADI\",\"CktId\":\"BFEC558376ATI\",\"CustomerLocationInfo\":[{\"ServiceState\":\"VA\",\"USOCInfo\":[{\"DisplaySpeed\":\"10000\",\"USOC\":\"1LNET\",\"NetRate\":\"0\",\"AccessSpeed\":\"10 GBPS BASIC EPL-WAN\"}],\"PhysicalInterface\":\"10GBASELR\",\"ServiceZip\":\"20147-6207\",\"ServiceCity\":\"ASHBURN\",\"ServiceProvider\":\"SBC*LNS\",\"AttCtrOffcCLLICd\":\"WASHDCDT\",\"ServiceAddress2\":\"FLR 2 RM CAGE 2113\",\"ServiceAddress1\":\"21721  FILIGREE CT\",\"AccessArchitecture\":\"DEDICATED\",\"CustSrvgWireCtrCLLICd\":\"WASHDCDN\",\"NXSITEMATCHINGID\":4,\"nxSiteId\":2,\"endPointType\":\"A\",\"nxKeyId\":\"10 GBPS BASIC EPL-WAN$DEDICATED$10GBASELR\"}],\"dataUpdated\":\"Y\"}]}},{\"CustomerSubAccountInfo\":{\"CustomerCircuitInfo\":[{\"ServiceIndicator\":\"AVPN\",\"CktId\":\"IZEC579038ATI\",\"CustomerLocationInfo\":[{\"ServiceState\":\"CT\",\"USOCInfo\":[{\"DisplaySpeed\":\"1000\",\"USOC\":\"1LNET\",\"NetRate\":\"0\",\"AccessSpeed\":\"1 GBPS BASIC EPL-WAN\"}],\"PhysicalInterface\":\"1000BASELX\",\"ServiceZip\":\"06457-0000\",\"ServiceCity\":\"MIDDLETOWN\",\"ServiceProvider\":\"SBC*LNS\",\"AttCtrOffcCLLICd\":\"CHSHCT02\",\"ServiceAddress2\":\"BLDG DATA CTR FLR 3RD RM COMPUTER\",\"ServiceAddress1\":\"1000  MIDDLE ST\",\"AccessArchitecture\":\"SWITCHED\",\"CustSrvgWireCtrCLLICd\":\"CRWLCT00\",\"NXSITEMATCHINGID\":2,\"nxSiteId\":3,\"endPointType\":\"A\",\"nxKeyId\":\"1 GBPS BASIC EPL-WAN$SWITCHED$1000BASELX\"},{\"ServiceState\":\"NY\",\"USOCInfo\":[{\"DisplaySpeed\":\"1000\",\"USOC\":\"1LNET\",\"NetRate\":\"0\",\"AccessSpeed\":\"1 GBPS BASIC EPL-WAN\"}],\"PhysicalInterface\":\"1000BASELX\",\"ServiceZip\":\"10013\",\"ServiceCity\":\"NEW YORK\",\"ServiceProvider\":\"SBC*LNS\",\"AttCtrOffcCLLICd\":\"NYCMNY54\",\"ServiceAddress2\":\"BLDG LNS FLR 10TH RM LNS CAGE\",\"ServiceAddress1\":\"32  AVENUE OF THE AMERICAS\",\"AccessArchitecture\":\"DEDICATED\",\"CustSrvgWireCtrCLLICd\":\"NYCMNYVS\",\"NXSITEMATCHINGID\":1,\"nxSiteId\":4,\"endPointType\":\"Z\",\"nxKeyId\":\"1 GBPS BASIC EPL-WAN$DEDICATED$1000BASELX\"}],\"dataUpdated\":\"Y\"}]}}]},\"DomesticDS3OCXAccessInventory\":{\"CustomerAccountInfo\":[{\"CustomerSubAccountInfo\":{\"CustomerCircuitInfo\":[{\"PrimClsOfSvcCd\":\"AVAU5\",\"ServiceIndicator\":\"POS\",\"CktId\":\"AGEC572447ATI\",\"CustomerLocationInfo\":[{\"AttCtrOffcCLLICd\":\"PTLDMEFO\",\"SiteNPANXX\":\"207774\",\"USOCInfo\":[{\"USOC\":\"1LNM2\",\"NetRate\":\"3780.00\",\"AccessSpeed\":\"OC3\",\"secondaryKey\":\"#FCC#OC3 Local Channel Service#OC3#Unprotected\",\"nxItemId\":1788208}],\"CustSrvgWireCtrCLLICd\":\"PTLDMEFO\",\"NXSITEMATCHINGID\":15,\"nxSiteId\":5,\"endPointType\":\"A\",\"TypeLocalAccess\":\"Unprotected\",\"LittleProductName\":\"LD DS3 OCx Access\",\"ProductTypeLocalAccess\":\"OC3 Local Channel Service\",\"nxKeyId\":\"1788208\"}],\"FALLOUTMATCHINGID\":\"0000000016/DDAResponse/Body/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\"},{\"PrimClsOfSvcCd\":\"AVAU5\",\"ServiceIndicator\":\"POS\",\"CktId\":\"AGEC648238ATI\",\"CustomerLocationInfo\":[{\"AttCtrOffcCLLICd\":\"FTLDFLOV\",\"SiteNPANXX\":\"954693\",\"USOCInfo\":[{\"USOC\":\"1LNM1\",\"NetRate\":\"1925.00\",\"AccessSpeed\":\"OC3\",\"secondaryKey\":\"#FCC#OC3 Local Channel Service#OC3#Unprotected\",\"nxItemId\":1788208}],\"CustSrvgWireCtrCLLICd\":\"FTLDFLJA\",\"NXSITEMATCHINGID\":13,\"nxSiteId\":6,\"endPointType\":\"A\",\"TypeLocalAccess\":\"Unprotected\",\"LittleProductName\":\"LD DS3 OCx Access\",\"ProductTypeLocalAccess\":\"OC3 Local Channel Service\",\"nxKeyId\":\"1788208\"}],\"FALLOUTMATCHINGID\":\"0000000014/DDAResponse/Body/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\"},{\"PrimClsOfSvcCd\":\"AVAU5\",\"ServiceIndicator\":\"POS\",\"CktId\":\"AGEC880883ATI\",\"CustomerLocationInfo\":[{\"AttCtrOffcCLLICd\":\"BSMRNDJC\",\"SiteNPANXX\":\"701221\",\"USOCInfo\":[{\"USOC\":\"1LNM1\",\"NetRate\":\"3250.00\",\"AccessSpeed\":\"OC3\",\"secondaryKey\":\"#FCC#OC3 Local Channel Service#OC3#Unprotected\",\"nxItemId\":1788208}],\"CustSrvgWireCtrCLLICd\":\"BSMRNDBC\",\"NXSITEMATCHINGID\":11,\"nxSiteId\":7,\"endPointType\":\"A\",\"TypeLocalAccess\":\"Unprotected\",\"LittleProductName\":\"LD DS3 OCx Access\",\"ProductTypeLocalAccess\":\"OC3 Local Channel Service\",\"nxKeyId\":\"1788208\"}],\"FALLOUTMATCHINGID\":\"0000000012/DDAResponse/Body/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\"},{\"PrimClsOfSvcCd\":\"AVFV5\",\"ServiceIndicator\":\"POS\",\"CktId\":\"IWEC723444ATI\",\"CustomerLocationInfo\":[{\"AttCtrOffcCLLICd\":\"CHSHCT02\",\"SiteNPANXX\":\"860613\",\"USOCInfo\":[{\"USOC\":\"1LNVE\",\"NetRate\":\"7102.00\",\"AccessSpeed\":\"OC48\",\"secondaryKey\":\"#FCC#OC48 Local Channel Service#OC48#Unprotected\",\"nxItemId\":1788237}],\"CustSrvgWireCtrCLLICd\":\"CRWLCT00\",\"NXSITEMATCHINGID\":9,\"nxSiteId\":8,\"endPointType\":\"A\",\"TypeLocalAccess\":\"Unprotected\",\"LittleProductName\":\"LD DS3 OCx Access\",\"ProductTypeLocalAccess\":\"OC48 Local Channel Service\",\"nxKeyId\":\"1788237\"}],\"FALLOUTMATCHINGID\":\"0000000010/DDAResponse/Body/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\"}]}},{\"CustomerSubAccountInfo\":{\"CustomerCircuitInfo\":[{\"PrimClsOfSvcCd\":\"HPPKE\",\"ServiceIndicator\":\"PL\",\"CktId\":\"IVEC990793ATI\",\"CustomerLocationInfo\":[{\"AttCtrOffcCLLICd\":\"HRFRCT03\",\"SiteNPANXX\":\"860624\",\"USOCInfo\":[{\"USOC\":\"ZDRMK\",\"NetRate\":\"1927.00\",\"AccessSpeed\":\"OC12\",\"secondaryKey\":\"#FCC#OC12 Local Channel Service#OC12#Unprotected\",\"nxItemId\":1788234}],\"CustSrvgWireCtrCLLICd\":\"WNDSCT00\",\"NXSITEMATCHINGID\":7,\"nxSiteId\":9,\"endPointType\":\"A\",\"TypeLocalAccess\":\"Unprotected\",\"LittleProductName\":\"LD DS3 OCx Access\",\"ProductTypeLocalAccess\":\"OC12 Local Channel Service\",\"nxKeyId\":\"1788234\"},{\"AttCtrOffcCLLICd\":\"NWRKNJ02\",\"SiteNPANXX\":\"973275\",\"USOCInfo\":[{\"USOC\":\"ZDRML\",\"NetRate\":\"2508.00\",\"AccessSpeed\":\"OC12\",\"secondaryKey\":\"#FCC#OC12 Local Channel Service#OC12#Unprotected\",\"nxItemId\":1788234}],\"CustSrvgWireCtrCLLICd\":\"SORGNJSO\",\"NXSITEMATCHINGID\":6,\"nxSiteId\":10,\"endPointType\":\"Z\",\"TypeLocalAccess\":\"Unprotected\",\"LittleProductName\":\"LD DS3 OCx Access\",\"ProductTypeLocalAccess\":\"OC12 Local Channel Service\",\"nxKeyId\":\"1788234\"}],\"FALLOUTMATCHINGID\":\"0000000008/DDAResponse/Body/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\"}]}}]},\"flowType\":\"INR\"}");
		fileModel.setNxRequestDetails(nxReq);
		List<NxOutputFileModel> fileList = new ArrayList<NxOutputFileModel>();
		fileList.add(fileModel);
		nxReq.setNxOutputFiles(fileList);
		nxReq.setNxReqId(1l);
		nxReq.setStatus(30L);
		NxSolutionDetail sol = new NxSolutionDetail();
		sol.setNxSolutionId(101l);
		nxReq.setNxSolutionDetail(sol);
	}
	
	@Test
	public void testBulkUploadInrData() throws SalesBusinessException, IOException {
		
		Mockito.when(nxRequestDetailsRepository.findByNxReqIdAndActiveYn(anyLong(), anyString())).thenReturn(nxReq);
		doNothing().when(nexxusService).updateNxSolution(anyLong());
		List<NxLookupData> nxLookupLst = new ArrayList<NxLookupData>();
		NxLookupData lookup = new NxLookupData();
		lookup.setCriteria("{ \"ethernet\": { \"whereClausePath\": \"/DomesticEthernetAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo\", \"whereClauseAttriName\": \"nxSiteId\", \"whereClauseExcelColName\": \"NX_Site ID\", \"whereClauseDatatype\": \"Integer\", \"cdirKey\": \"NXSITEMATCHINGID\", \"data\": { \"Transport Type\": { \"path\": \"/DomesticEthernetAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo\", \"jsonAttriName\": \"ServiceIndicator\" }, \"Access Architecture\": { \"path\": \"CustomerLocationInfo\", \"jsonAttriName\": \"AccessArchitecture\" } } }, \"ds3\": { \"whereClausePath\": \"/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo\", \"whereClauseAttriName\": \"nxSiteId\", \"whereClauseExcelColName\": \"NX_Site ID\", \"whereClauseDatatype\": \"Integer\", \"cdirKey\": \"NXSITEMATCHINGID\", \"data\": { \"Transport Type\": { \"path\": \"/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo\", \"jsonAttriName\": \"ServiceIndicator\" } } }, \"ds0ds1\": { \"whereClausePath\": \"/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo\", \"whereClauseAttriName\": \"nxSiteId\", \"whereClauseExcelColName\": \"NX_Site ID\", \"whereClauseDatatype\": \"Integer\", \"cdirKey\": \"NXSITEMATCHINGID\", \"data\": { \"Transport Type\": { \"path\": \"/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo\", \"jsonAttriName\": \"ServiceIndicator\" } } } }");
		lookup.setDescription("Data Update");
		nxLookupLst.add(lookup);
		Mockito.when(nxLookupDataRepository.findByDatasetNameAndItemIdAndActive(anyString(), anyString(), anyString())).thenReturn(nxLookupLst);
		NexxusJsonUtility nexxusJsonUtility1 = new NexxusJsonUtility();
		LinkedHashMap rules = (LinkedHashMap<String, Object>) nexxusJsonUtility1.convertStringJsonToMap(lookup.getCriteria());
		Mockito.when(nexxusJsonUtility.convertStringJsonToMap(anyString())).thenReturn(rules);
		ObjectMapper objMapper = new ObjectMapper();
		JsonNode jsonNode = objMapper.readTree(nxReq.getNxOutputFiles().get(0).getMpOutputJson());
		Mockito.when(mapper.readTree(anyString())).thenReturn(jsonNode);
		Mockito.when(inrFactory.getCopyOutputToIntermediateJson(any(), any())).thenReturn(copyOutputToIntermediateJson);
		doNothing().when(copyOutputToIntermediateJson).copyNxSiteId();
		Mockito.when(inrFactory.getInrIntermediateJsonUpdate(any(), any(), any(), anyString(), anyString(), any(), any())).thenReturn(inrIntermediateJsonUpdate);
		doNothing().when(inrIntermediateJsonUpdate).inredits(anySet(), anyMap(), anySet(),anySet());
		OutputJsonFallOutData outputJsonFallOutData = new OutputJsonFallOutData(null, null, jsonNode, false, false); 
		Mockito.when(inrProcessingService.generateOutput(any(), any())).thenReturn(outputJsonFallOutData);
		Mockito.when(nxDesignAuditRepository.findByNxRefIdAndTransaction(anyLong(), anyString())).thenReturn(null);
		Mockito.when(nxDesignAuditRepository.saveAndFlush(any(NxDesignAudit.class))).thenReturn(new NxDesignAudit());
		Mockito.when(nxRequestDetailsRepository.save(any(NxRequestDetails.class))).thenReturn(nxReq);
		doNothing().when(cdirEdit).updateCdirData(anyMap(), anyLong());
		List<NxLookupData> nxLookupstatus = new ArrayList<NxLookupData>();
		NxLookupData lookupStatus = new NxLookupData();
		lookupStatus.setCriteria("30");
		lookupStatus.setDescription("30");
		lookupStatus.setItemId("220");
		nxLookupstatus.add(lookupStatus);
		Mockito.when(nxMyPriceRepositoryServce.getItemDescFromLookup(anyString(), anyString())).thenReturn(nxLookupstatus);
		NxRequestGroup nxRequestGroup = new NxRequestGroup();
		nxRequestGroup.setStatus("11");
		Mockito.when(nxRequestGroupRepository.findByNxRequestGroupIdAndActiveYn(anyLong(), anyString())).thenReturn(nxRequestGroup);
		bulkUploadInrUpdateService.bulkUploadInrData(uploadRequest);
	}
	
	@Test
	public void testSetErrorResponse() {
		BulkUploadInrUpdateResponse response = new BulkUploadInrUpdateResponse();
		bulkUploadInrUpdateService.setErrorResponse(response, "IU0001");
	}
	
	@Test
	public void testBulkUploadInrDataAddressUpdate() throws FileNotFoundException, SalesBusinessException {
		ObjectMapper realMapper = new ObjectMapper();
		ReflectionTestUtils.setField(bulkUploadInrUpdateService, "mapper", realMapper);
		uploadRequest.setAction(CommonConstants.BULK_INR_ADDRESS_UPDATE);
		List<NxRequestDetails> nxRequestDetailList = new ArrayList<>();
		nxRequestDetailList.add(nxReq);
		Mockito.when(nxRequestDetailsRepository.findByNxSolutionId(any())).thenReturn(nxRequestDetailList);
		LinkedHashMap<String, String> dataConvertRuleMap = new LinkedHashMap<>();
		nxReq.setProduct("AVPN");
		dataConvertRuleMap.put("AVPN", "rules...");
		Mockito.when(nxMyPriceRepositoryServce.getDataFromLookup(any())).thenReturn(dataConvertRuleMap);
		InputStream fis = new FileInputStream(addressUploadFilePath);
		uploadRequest.setInputStream(fis);
//		bulkUploadInrUpdateService.bulkUploadInrData(uploadRequest);
	}
	
	@Test
	public void testprocessTdmckts() throws IOException {
		NxOutputFileModel nxOutputFile = new NxOutputFileModel();
		nxOutputFile.setMpOutputJson("{ \"beginBillMonth\": \"September 2019\", \"service\": \"DOMESTIC DEDICATED ACCESS\", \"customerName\": \"AETNA LIFE INSURANCE\", \"DomesticDSODS1AccessInventory\": { \"CustomerAccountInfo\": [ { \"PrimClsOfSvcCd\": \"AGAV5\", \"ServiceIndicator\": \"NODAL\", \"CktId\": \"DHEC549715100ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"SLKCUTMAC\", \"SiteNPANXX\": \"8014534\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"140.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273013 } ], \"CustSrvgWireCtrCLLICd\": \"CTWDUTMAC\", \"NXSITEMATCHINGID\": 101, \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273013$NODAL\", \"nxSiteId\": 117, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000102/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" } ] },  \"flowType\": \"INR\" }");
		NxDesignAudit designAudit = new NxDesignAudit();
		designAudit.setData("117$DHEC549715$8014534$CTWDUTMAC");
		List<String> nxSiteIdForTdmChk = new ArrayList<String>();
		nxSiteIdForTdmChk.add("117");
		JsonNode node = mapper.createObjectNode();
		Mockito.when(mapper.readTree(anyString())).thenReturn(node);
		List<Object> results = new ArrayList<Object>();
		Map<String, Object> n = new LinkedHashMap<>();
		n.put("SiteNPANXX", "81222");
		n.put("CustSrvgWireCtrCLLICd", "clli");
		results.add(n);
		Mockito.when(jsonPathUtil.search(any(), any(),any())).thenReturn(results);
		bulkUploadInrUpdateService.processTdmckts(nxOutputFile, designAudit, nxSiteIdForTdmChk);
	}

}
