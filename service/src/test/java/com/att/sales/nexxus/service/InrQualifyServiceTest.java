package com.att.sales.nexxus.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.matches;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxAccessPricingData;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.NxRequestGroup;
import com.att.sales.nexxus.dao.model.NxValidationRules;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxAccessPricingDataRepository;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxOutputFileRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxRequestGroupRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionSiteRepository;
import com.att.sales.nexxus.dao.repository.NxValidationRulesRepository;
import com.att.sales.nexxus.fallout.service.FalloutDetailsImpl;
import com.att.sales.nexxus.model.CircuitSiteDetails;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class InrQualifyServiceTest {

	@InjectMocks
	private InrQualifyService inrQualifyService;
	
	@Mock
	private NxDesignAuditRepository nxDesignAuditRepository;
	
	@Mock
	private NxSolutionSiteRepository nxSolutionSiteRepository;
	
	@Mock
	private NexxusJsonUtility nexxusJsonUtility;
	
	@Mock
	private NxRequestDetailsRepository nxRequestDetailsRepository;
	
	@Mock
	private NxOutputFileRepository nxOutputFileRepository;
	
	@Mock
	private NxRequestGroupRepository nxRequestGroupRepository;
	
	@Mock
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Mock
	private NxValidationRulesRepository nxValidationRulesRepository;
	
	@Mock
	private JsonPathUtil jsonPathUtil;
	
	@Mock
	private ObjectMapper mapper;
	
	@Mock
	private FalloutDetailsImpl falloutDetailsImpl;

	private ObjectMapper realMapper;
	String mpOutPutJson;
	private String accessJsonwithSiteId = null, serviceJson = null, serviceJsonwithSiteId = null;
	
	private List<NxLookupData> copyStatusLookup;
	
	@Mock
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	@Mock
	private NxAccessPricingDataRepository nxaccessPricingDataRepo;

	@BeforeEach

	public void setUp() {
		accessJsonwithSiteId ="{ \"beginBillMonth\": \"December 2020\", \"service\": \"DOMESTIC DEDICATED ACCESS\", \"customerName\": \"CAPITAL ONE\", \"DomesticDSODS1AccessInventory\": { \"CustomerAccountInfo\": [ { \"CustomerSubAccountInfo\": { \"CustomerCircuitInfo\": [ { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC336948811ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"OKTNVAEA\", \"SiteNPANXX\": \"703263\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"CNVIVACT\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1307, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000095/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC336948812ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"OKTNVAEA\", \"SiteNPANXX\": \"703263\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"CNVIVACT\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1307, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000094/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" } ] } }, { \"CustomerSubAccountInfo\": { \"CustomerCircuitInfo\": [ { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC496885811ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"LSVGNV02\", \"SiteNPANXX\": \"702228\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"LSVGNVXW\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1308, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000093/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC496885812ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"LSVGNV02\", \"SiteNPANXX\": \"702228\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"LSVGNVXW\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1308, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000092/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" } ] } }, { \"CustomerSubAccountInfo\": { \"CustomerCircuitInfo\": [ { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC148720811ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"HSTXTXFY\", \"SiteNPANXX\": \"281980\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"SGLDTXXD\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1309, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000091/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC148720812ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"HSTXTXFY\", \"SiteNPANXX\": \"281980\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"SGLDTXXD\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1309, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000090/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC199401811ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"AUSTTXGR\", \"SiteNPANXX\": \"512708\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"AUSTTXGR\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1310, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000089/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC199401812ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"AUSTTXGR\", \"SiteNPANXX\": \"512708\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"AUSTTXGR\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1310, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000088/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC221012ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"DLLNTX27\", \"SiteNPANXX\": \"214219\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"DLLSTXLA\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1311, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000087/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC268242ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"WHPLNY02\", \"SiteNPANXX\": \"845351\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"TUXDNYTX\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1312, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000086/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"FR\", \"CktId\": \"DHEC270358ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"ADSNTXAU\", \"SiteNPANXX\": \"214479\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"DLLSTXRN\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1313, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000085/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC285891811ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"NWORLAMA\", \"SiteNPANXX\": \"504833\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"NWORLAMT\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1314, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000084/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC285891812ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"NWORLAMA\", \"SiteNPANXX\": \"504833\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"NWORLAMT\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1314, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000083/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC285891813ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"NWORLAMA\", \"SiteNPANXX\": \"504833\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"NWORLAMT\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1314, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000082/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC285891814ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"NWORLAMA\", \"SiteNPANXX\": \"504833\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"NWORLAMT\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1314, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000081/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC303015ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"LFYTLAAT\", \"SiteNPANXX\": \"337262\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"LFYTLAMA\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1315, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000080/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC316756811ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"IRNGTX27\", \"SiteNPANXX\": \"972556\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"DLLSTXNO\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1316, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000079/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC316756812ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"IRNGTX27\", \"SiteNPANXX\": \"972556\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"DLLSTXNO\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1316, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000078/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC316756813ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"IRNGTX27\", \"SiteNPANXX\": \"972556\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"DLLSTXNO\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1316, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000077/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC379656811ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"NWORLAMA\", \"SiteNPANXX\": \"504280\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"NWORLALK\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1317, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000076/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC379656812ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"NWORLAMA\", \"SiteNPANXX\": \"504280\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"NWORLALK\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1317, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000075/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC479577811ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"LGVWTXTL\", \"SiteNPANXX\": \"903223\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"TXRKTXXB\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1318, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000074/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC479577812ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"LGVWTXTL\", \"SiteNPANXX\": \"903223\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"TXRKTXXB\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1318, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000073/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC501218811ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"HSTNTX01\", \"SiteNPANXX\": \"713284\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"HSTNTXJA\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1319, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000072/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC501218812ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"HSTNTX01\", \"SiteNPANXX\": \"713284\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"HSTNTXJA\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1319, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000071/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC505432811ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"NWORLAMA\", \"SiteNPANXX\": \"985542\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"HMNDLAMA\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1320, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000070/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC505432812ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"NWORLAMA\", \"SiteNPANXX\": \"985542\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"HMNDLAMA\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1320, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000069/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"FR\", \"CktId\": \"DHEC521808ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"HSTNTX01\", \"SiteNPANXX\": \"281876\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"HSTNTXGP\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1321, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000068/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC680872811ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"BTRGLAMA\", \"SiteNPANXX\": \"225647\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"GNZLLAXA\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1322, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000067/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC680872812ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"BTRGLAMA\", \"SiteNPANXX\": \"225647\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"GNZLLAXA\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1322, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000066/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC681295811ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"HSTNTX01\", \"SiteNPANXX\": \"936539\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"CONRTXXA\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1323, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000065/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC681295812ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"HSTNTX01\", \"SiteNPANXX\": \"936539\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"CONRTXXA\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1323, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000064/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC695026811ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"PHNXAZMA\", \"SiteNPANXX\": \"602431\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"PHNXAZSE\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1324, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000063/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC695026812ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"PHNXAZMA\", \"SiteNPANXX\": \"602431\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"PHNXAZSE\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1324, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000062/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC732800811ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"LSANCA02\", \"SiteNPANXX\": \"213217\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"LSANCA03\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1325, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000061/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC732800812ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"LSANCA02\", \"SiteNPANXX\": \"213217\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"LSANCA03\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1325, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000060/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC732800813ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"LSANCA02\", \"SiteNPANXX\": \"213217\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"LSANCA03\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1325, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000059/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC732800814ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"LSANCA02\", \"SiteNPANXX\": \"213217\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"LSANCA03\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1325, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000058/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC742674811ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"MILWWIHE\", \"SiteNPANXX\": \"414357\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"MILWWI16\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1326, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000057/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC742674812ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"MILWWIHE\", \"SiteNPANXX\": \"414357\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"MILWWI16\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1326, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000056/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC751127ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"DLLNTX27\", \"SiteNPANXX\": \"469335\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"DLLSTXFL\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1327, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000055/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC766367ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"ADSNTXAU\", \"SiteNPANXX\": \"972966\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"LWVLTXXF\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1328, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000054/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC790185ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"HSTXTXFY\", \"SiteNPANXX\": \"713340\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"HSTNTXID\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1329, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000053/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC824441811ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"HSTNTX01\", \"SiteNPANXX\": \"713233\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"HSTNTXHU\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1330, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000052/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC824441812ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"HSTNTX01\", \"SiteNPANXX\": \"713233\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"HSTNTXHU\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1330, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000051/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC849001ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"KATYTXQA\", \"SiteNPANXX\": \"281232\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"RSBGTXRR\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1331, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000050/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC888342811ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"HSTNTX01\", \"SiteNPANXX\": \"281554\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"LGCYTXXA\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1332, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000049/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC888342812ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"HSTNTX01\", \"SiteNPANXX\": \"281554\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"LGCYTXXA\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1332, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000048/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC901342ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"ADSNTXAU\", \"SiteNPANXX\": \"214265\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"DLLSTXEM\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1333, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000047/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC953750ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"DLLNTX27\", \"SiteNPANXX\": \"214219\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"DLLSTXLA\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1334, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000046/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DHEC988454ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"IRNGTX27\", \"SiteNPANXX\": \"817481\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"160.00\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"GRPVTXXA\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\", \"nxSiteId\": 1335, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000045/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" } ] } } ] }, \"DomesticDS3OCXAccessInventory\": { \"CustomerAccountInfo\": [ { \"CustomerSubAccountInfo\": { \"CustomerCircuitInfo\": [ { \"PrimClsOfSvcCd\": \"AVAU5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"AGEC883415ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"MNRVMDMO\", \"SiteNPANXX\": \"301271\", \"USOCInfo\": [ { \"USOC\": \"1LNM2\", \"NetRate\": \"6364.06\", \"AccessSpeed\": \"OC3\", \"secondaryKey\": \"#FCC#OC3 Local Channel Service#OC3#Unprotected\", \"nxItemId\": 1788208 } ], \"CustSrvgWireCtrCLLICd\": \"THRMMDTH\", \"TypeLocalAccess\": \"Unprotected\", \"LittleProductName\": \"LD DS3 OCx Access\", \"ProductTypeLocalAccess\": \"OC3 Local Channel Service\", \"nxKeyId\": \"1788208\", \"nxSiteId\": 1183, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000394/DDAResponse/Body/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVAU5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"AGEC883733ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"SHPTLATL\", \"SiteNPANXX\": \"318221\", \"USOCInfo\": [ { \"USOC\": \"1LNM2\", \"NetRate\": \"2728.90\", \"AccessSpeed\": \"OC3\", \"secondaryKey\": \"#FCC#OC3 Local Channel Service#OC3#Unprotected\", \"nxItemId\": 1788208 } ], \"CustSrvgWireCtrCLLICd\": \"SHPTLAMA\", \"TypeLocalAccess\": \"Unprotected\", \"LittleProductName\": \"LD DS3 OCx Access\", \"ProductTypeLocalAccess\": \"OC3 Local Channel Service\", \"nxKeyId\": \"1788208\", \"nxSiteId\": 1184, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000393/DDAResponse/Body/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVAU5\", \"ServiceIndicator\": \"POS\", \"CktId\": \"AGEC886610ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"NYCMNY54\", \"SiteNPANXX\": \"212243\", \"USOCInfo\": [ { \"USOC\": \"1LNM2\", \"NetRate\": \"4279.82\", \"AccessSpeed\": \"OC3\", \"secondaryKey\": \"#FCC#OC3 Local Channel Service#OC3#Unprotected\", \"nxItemId\": 1788208 } ], \"CustSrvgWireCtrCLLICd\": \"NYCMNY18\", \"TypeLocalAccess\": \"Unprotected\", \"LittleProductName\": \"LD DS3 OCx Access\", \"ProductTypeLocalAccess\": \"OC3 Local Channel Service\", \"nxKeyId\": \"1788208\", \"nxSiteId\": 1185, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000392/DDAResponse/Body/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVA45\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DNEC513117ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"LKCHLAMA\", \"SiteNPANXX\": \"337494\", \"USOCInfo\": [ { \"USOC\": \"1LN44\", \"NetRate\": \"1700.00\", \"AccessSpeed\": \"DS3\", \"secondaryKey\": \"#FCC#DS3 Schedule B/Regional#DS3#Unprotected\", \"nxItemId\": 1788177 } ], \"CustSrvgWireCtrCLLICd\": \"LKCHLADT\", \"TypeLocalAccess\": \"Unprotected\", \"LittleProductName\": \"LD DS3 OCx Access\", \"ProductTypeLocalAccess\": \"DS3 Schedule B/Regional\", \"nxKeyId\": \"1788177\", \"nxSiteId\": 1186, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000391/DDAResponse/Body/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVA45\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DNEC550741ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"SHPTLATL\", \"SiteNPANXX\": \"318549\", \"USOCInfo\": [ { \"USOC\": \"1LN44\", \"NetRate\": \"1700.00\", \"AccessSpeed\": \"DS3\", \"secondaryKey\": \"#FCC#DS3 Schedule B/Regional#DS3#Unprotected\", \"nxItemId\": 1788177 } ], \"CustSrvgWireCtrCLLICd\": \"SHPTLABS\", \"TypeLocalAccess\": \"Unprotected\", \"LittleProductName\": \"LD DS3 OCx Access\", \"ProductTypeLocalAccess\": \"DS3 Schedule B/Regional\", \"nxKeyId\": \"1788177\", \"nxSiteId\": 1187, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000390/DDAResponse/Body/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVA45\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DNEC655201ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"SHPTLATL\", \"SiteNPANXX\": \"318221\", \"USOCInfo\": [ { \"USOC\": \"1LN44\", \"NetRate\": \"1700.00\", \"AccessSpeed\": \"DS3\", \"secondaryKey\": \"#FCC#DS3 Schedule B/Regional#DS3#Unprotected\", \"nxItemId\": 1788177 } ], \"CustSrvgWireCtrCLLICd\": \"SHPTLAMA\", \"TypeLocalAccess\": \"Unprotected\", \"LittleProductName\": \"LD DS3 OCx Access\", \"ProductTypeLocalAccess\": \"DS3 Schedule B/Regional\", \"nxKeyId\": \"1788177\", \"nxSiteId\": 1188, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000389/DDAResponse/Body/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVA45\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DNEC763662ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"LFYTLAAT\", \"SiteNPANXX\": \"337237\", \"USOCInfo\": [ { \"USOC\": \"1LN44\", \"NetRate\": \"1700.00\", \"AccessSpeed\": \"DS3\", \"secondaryKey\": \"#FCC#DS3 Schedule B/Regional#DS3#Unprotected\", \"nxItemId\": 1788177 } ], \"CustSrvgWireCtrCLLICd\": \"LFYTLAMA\", \"TypeLocalAccess\": \"Unprotected\", \"LittleProductName\": \"LD DS3 OCx Access\", \"ProductTypeLocalAccess\": \"DS3 Schedule B/Regional\", \"nxKeyId\": \"1788177\", \"nxSiteId\": 1189, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000388/DDAResponse/Body/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"HCA45\", \"ServiceIndicator\": \"PL\", \"CktId\": \"DNEC912298ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"SHPTLATL\", \"SiteNPANXX\": \"318674\", \"USOCInfo\": [ { \"USOC\": \"1LN44\", \"NetRate\": \"1700.00\", \"AccessSpeed\": \"DS3\", \"secondaryKey\": \"#FCC#DS3 Schedule B/Regional#DS3#Unprotected\", \"nxItemId\": 1788188 } ], \"CustSrvgWireCtrCLLICd\": \"SHPTLAMA\", \"TypeLocalAccess\": \"Unprotected\", \"LittleProductName\": \"LD DS3 OCx Access\", \"ProductTypeLocalAccess\": \"DS3 Schedule B/Regional\", \"nxKeyId\": \"1788188\", \"nxSiteId\": 1190, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000387/DDAResponse/Body/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVA45\", \"ServiceIndicator\": \"POS\", \"CktId\": \"DNEC971273ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"RDCYCA02\", \"SiteNPANXX\": \"650216\", \"USOCInfo\": [ { \"USOC\": \"1LN44\", \"NetRate\": \"1700.00\", \"AccessSpeed\": \"DS3\", \"secondaryKey\": \"#FCC#DS3 Schedule B/Regional#DS3#Unprotected\", \"nxItemId\": 1788177 } ], \"CustSrvgWireCtrCLLICd\": \"RDCYCA01\", \"TypeLocalAccess\": \"Unprotected\", \"LittleProductName\": \"LD DS3 OCx Access\", \"ProductTypeLocalAccess\": \"DS3 Schedule B/Regional\", \"nxKeyId\": \"1788177\", \"nxSiteId\": 1191, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000386/DDAResponse/Body/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVA45\", \"ServiceIndicator\": \"FR\", \"CktId\": \"DNEC986386ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"NWORLAMA\", \"SiteNPANXX\": \"504441\", \"USOCInfo\": [ { \"USOC\": \"1LN44\", \"NetRate\": \"1700.00\", \"AccessSpeed\": \"DS3\", \"secondaryKey\": \"#FCC#DS3 Schedule B/Regional#DS3#Unprotected\", \"nxItemId\": 1788177 } ], \"CustSrvgWireCtrCLLICd\": \"KNNRLABR\", \"TypeLocalAccess\": \"Unprotected\", \"LittleProductName\": \"LD DS3 OCx Access\", \"ProductTypeLocalAccess\": \"DS3 Schedule B/Regional\", \"nxKeyId\": \"1788177\", \"nxSiteId\": 1192, \"endPointType\": \"A\" } ], \"FALLOUTMATCHINGID\": \"0000000385/DDAResponse/Body/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" } ] } }    ]}, \"flowType\": \"INR\" }";
		//accessJson ="{ \"beginBillMonth\": \"September 2019\", \"service\": \"DOMESTIC DEDICATED ACCESS\", \"customerName\": \"NA\", \"DomesticDSODS1AccessInventory\": { \"CustomerAccountInfo\": [ { \"CustomerSubAccountInfo\": { \"CustomerCircuitInfo\": [ { \"PrimClsOfSvcCd\": \"MLAX5\", \"ServiceIndicator\": \"MIS Promo\", \"CktId\": \"DHEC404368ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"DLLNTX27\", \"SiteNPANXX\": \"214220\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"265\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273072 } ], \"CustSrvgWireCtrCLLICd\": \"DLLSTXRO\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273072\" } ], \"FALLOUTMATCHINGID\": \"0000000011/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"MLAX5\", \"ServiceIndicator\": \"MIS Promo\", \"CktId\": \"DHEC410363ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"NYCMNY54\", \"SiteNPANXX\": \"212351\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"265\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273072 } ], \"CustSrvgWireCtrCLLICd\": \"NYCMNY37\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273072\" } ], \"FALLOUTMATCHINGID\": \"0000000010/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"CktId\": \"DHEC434208811ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"CLMAMORS\", \"SiteNPANXX\": \"573636\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"422.40\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"JFCYMOXA\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\" } ], \"FALLOUTMATCHINGID\": \"0000000009/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" }, { \"PrimClsOfSvcCd\": \"AVCX5\", \"CktId\": \"DHEC434208812ATI\", \"CustomerLocationInfo\": [ { \"AttCtrOffcCLLICd\": \"CLMAMORS\", \"SiteNPANXX\": \"573636\", \"USOCInfo\": [ { \"USOC\": \"1LNV9\", \"NetRate\": \"422.40\", \"AccessSpeed\": \"1.544 mb\", \"secondaryKey\": \"FCC#1.544 mb#DS1#Inter\", \"nxItemId\": 273031 } ], \"CustSrvgWireCtrCLLICd\": \"JFCYMOXA\", \"TypeLocalAccess\": \"Inter\", \"LittleProductName\": \"LD DS0/DS1 Access Unbundled\", \"nxKeyId\": \"273031\" } ], \"FALLOUTMATCHINGID\": \"0000000008/DDAResponse/Body/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\" },  ] } } ] }, \"DomesticEthernetAccessInventory\": { \"CustomerAccountInfo\": [ { \"CustomerSubAccountInfo\": { \"CustomerCircuitInfo\": [ { \"ServiceIndicator\": \"ETH\", \"CktId\": \"BBEC502680ATI\", \"CustomerLocationInfo\": [ { \"ServiceState\": \"DC\", \"USOCInfo\": [ { \"DisplaySpeed\": \"50\", \"USOC\": \"1LNET\", \"NetRate\": \"1108.25\", \"AccessSpeed\": \"50 MBPS BASIC SVC\" } ], \"PhysicalInterface\": \"1000BASESX\", \"ServiceZip\": \"20005\", \"ServiceCity\": \"WASHINGTON\", \"ServiceProvider\": \"VERIZON SOUTH\", \"AttCtrOffcCLLICd\": \"WASHDCSW\", \"ServiceAddress2\": \"BLDG MAIN FLR 1 RM SVR\", \"ServiceAddress1\": \"1401  I ST NW\", \"AccessArchitecture\": \"SWITCHED\", \"CustSrvgWireCtrCLLICd\": \"WASHDCMO\", \"nxKeyId\": \"50 MBPS BASIC SVC$SWITCHED$1000BASESX\" } ] }, { \"ServiceIndicator\": \"AVPN\", \"CktId\": \"BBEC517428ATI\", \"CustomerLocationInfo\": [ { \"ServiceState\": \"CA\", \"USOCInfo\": [ { \"DisplaySpeed\": \"50\", \"USOC\": \"1LNET\", \"NetRate\": \"510.30\", \"AccessSpeed\": \"50 MBPS BASIC SVC\" } ], \"PhysicalInterface\": \"1000BASESX\", \"ServiceZip\": \"90067\", \"ServiceCity\": \"LA\", \"ServiceProvider\": \"ATT/SBC CAL-SOUTH\", \"AttCtrOffcCLLICd\": \"LSANCA03\", \"ServiceAddress2\": \"FLR 23 SUIT 2300\", \"ServiceAddress1\": \"2049  CENTURY PARK EAST\", \"AccessArchitecture\": \"SWITCHED\", \"CustSrvgWireCtrCLLICd\": \"WLANCA01\", \"nxKeyId\": \"50 MBPS BASIC SVC$SWITCHED$1000BASESX\" } ] }, { \"ServiceIndicator\": \"AVPN\", \"CktId\": \"BBEC522676ATI\", \"CustomerLocationInfo\": [ { \"ServiceState\": \"TN\", \"USOCInfo\": [ { \"DisplaySpeed\": \"50\", \"USOC\": \"1LNET\", \"NetRate\": \"510.30\", \"AccessSpeed\": \"50 MBPS BASIC SVC\" } ], \"PhysicalInterface\": \"1000BASESX\", \"ServiceZip\": \"37219\", \"ServiceCity\": \"NASHVILLE\", \"ServiceProvider\": \"ATT/BELL SOUTH (TN)\", \"AttCtrOffcCLLICd\": \"NSVLTNMT\", \"ServiceAddress2\": \"BLDG MAIN FLR 9 RM MAIN SERV\", \"ServiceAddress1\": \"401  COMMERCE ST\", \"AccessArchitecture\": \"SWITCHED\", \"CustSrvgWireCtrCLLICd\": \"NSVLTNMT\", \"nxKeyId\": \"50 MBPS BASIC SVC$SWITCHED$1000BASESX\" } ] }, { \"ServiceIndicator\": \"AVPN\", \"CktId\": \"MMEC898461ATI\", \"CustomerLocationInfo\": [ { \"ServiceState\": \"MO\", \"USOCInfo\": [ { \"DisplaySpeed\": \"20\", \"USOC\": \"1LNET\", \"NetRate\": \"1136.00\", \"AccessSpeed\": \"20 MBPS BASIC SVC\" } ], \"PhysicalInterface\": \"1000BASESX\", \"ServiceZip\": \"65101\", \"ServiceCity\": \"JEFFERSON CITY\", \"ServiceProvider\": \"CENTURYLINK (EMBARQ)\", \"AttCtrOffcCLLICd\": \"OKGVMOXA\", \"ServiceAddress2\": \"FLR 3RD RM SUITE 300\", \"ServiceAddress1\": \"221  BOLIVAR ST\", \"AccessArchitecture\": \"DEDICATED\", \"CustSrvgWireCtrCLLICd\": \"JFCYMOXA\", \"nxKeyId\": \"20 MBPS BASIC SVC$DEDICATED$1000BASESX\" } ] } ] } } ] }, \"flowType\": \"INR\" }";

		serviceJsonwithSiteId = "{ \"customerName\": \"POLSINELLI\", \"beginBillMonth\": \"September 2019\", \"service\": \"DOMESTIC PL IOC\", \"DomesticIOCInventory\": { \"CustomerAccountInfo\": [ { \"CustomerName\": \"POLSINELLI PC\", \"CustomerSubAccountInfo\": { \"CustomerCircuitInfo\": [ { \"PrimClsOfSvcCd\": \"MLAXE\", \"CktId\": \"DHEC492765801ATI\", \"USOCInfo\": [ { \"CLLIZEnd\": \"STLSMO09\", \"USOC\": \"1LNVX\", \"NetRate\": \"0.00\", \"AccessSpeed\": \"ACC T1.5MBPS MIS ACC\", \"Quantity\": \"1\", \"CLLIAEnd\": \"CLMAMORS\", \"lineItemId\": 59981, \"nxItemId\": 59981 } ], \"FALLOUTMATCHINGID\": \"0000000006/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\", \"Quality\": \"Standard\", \"nxAccessSpeed\": \"1.544 Mbps\", \"nxKeyId\": \"59981$Standard\", \"nxSiteId\": 1, \"nxSiteIdZ\": 2 }, { \"PrimClsOfSvcCd\": \"MLAXE\", \"CktId\": \"DHEC492765802ATI\", \"USOCInfo\": [ { \"CLLIZEnd\": \"STLSMO09\", \"USOC\": \"1LNVX\", \"NetRate\": \"0.00\", \"AccessSpeed\": \"ACC T1.5MBPS MIS ACC\", \"Quantity\": \"1\", \"CLLIAEnd\": \"CLMAMORS\", \"lineItemId\": 59981, \"nxItemId\": 59981 } ], \"FALLOUTMATCHINGID\": \"0000000005/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\", \"Quality\": \"Standard\", \"nxAccessSpeed\": \"1.544 Mbps\", \"nxKeyId\": \"59981$Standard\", \"nxSiteId\": 3, \"nxSiteIdZ\": 4 }, { \"PrimClsOfSvcCd\": \"MLAXE\", \"CktId\": \"DHEC492765803ATI\", \"USOCInfo\": [ { \"CLLIZEnd\": \"STLSMO09\", \"USOC\": \"1LNVX\", \"NetRate\": \"0.00\", \"AccessSpeed\": \"ACC T1.5MBPS MIS ACC\", \"Quantity\": \"1\", \"CLLIAEnd\": \"CLMAMORS\", \"lineItemId\": 59981, \"nxItemId\": 59981 } ], \"FALLOUTMATCHINGID\": \"0000000004/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\", \"Quality\": \"Standard\", \"nxAccessSpeed\": \"1.544 Mbps\", \"nxKeyId\": \"59981$Standard\", \"nxSiteId\": 5, \"nxSiteIdZ\": 6 }, { \"PrimClsOfSvcCd\": \"MLAXE\", \"CktId\": \"DHEC492765804ATI\", \"USOCInfo\": [ { \"CLLIZEnd\": \"STLSMO09\", \"USOC\": \"1LNVX\", \"NetRate\": \"0.00\", \"AccessSpeed\": \"ACC T1.5MBPS MIS ACC\", \"Quantity\": \"1\", \"CLLIAEnd\": \"CLMAMORS\", \"lineItemId\": 59981, \"nxItemId\": 59981 } ], \"FALLOUTMATCHINGID\": \"0000000003/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\", \"Quality\": \"Standard\", \"nxAccessSpeed\": \"1.544 Mbps\", \"nxKeyId\": \"59981$Standard\", \"nxSiteId\": 7, \"nxSiteIdZ\": 8 } ] } } ] }, \"flowType\": \"INR\" }";
		serviceJson = "{ \"customerName\": \"POLSINELLI\", \"beginBillMonth\": \"September 2019\", \"service\": \"DOMESTIC PL IOC\", \"DomesticIOCInventory\": { \"CustomerAccountInfo\": [ { \"CustomerName\": \"POLSINELLI PC\", \"CustomerSubAccountInfo\": { \"CustomerCircuitInfo\": [ { \"PrimClsOfSvcCd\": \"MLAXE\", \"CktId\": \"DHEC492765801ATI\", \"USOCInfo\": [ { \"CLLIZEnd\": \"STLSMO09\", \"USOC\": \"1LNVX\", \"NetRate\": \"0.00\", \"AccessSpeed\": \"ACC T1.5MBPS MIS ACC\", \"Quantity\": \"1\", \"CLLIAEnd\": \"CLMAMORS\", \"lineItemId\": 59981, \"nxItemId\": 59981 } ], \"FALLOUTMATCHINGID\": \"0000000006/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\", \"Quality\": \"Standard\", \"nxAccessSpeed\": \"1.544 Mbps\", \"nxKeyId\": \"59981$Standard\" }, { \"PrimClsOfSvcCd\": \"MLAXE\", \"CktId\": \"DHEC492765802ATI\", \"USOCInfo\": [ { \"CLLIZEnd\": \"STLSMO09\", \"USOC\": \"1LNVX\", \"NetRate\": \"0.00\", \"AccessSpeed\": \"ACC T1.5MBPS MIS ACC\", \"Quantity\": \"1\", \"CLLIAEnd\": \"CLMAMORS\", \"lineItemId\": 59981, \"nxItemId\": 59981 } ], \"FALLOUTMATCHINGID\": \"0000000005/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\", \"Quality\": \"Standard\", \"nxAccessSpeed\": \"1.544 Mbps\", \"nxKeyId\": \"59981$Standard\" }, { \"PrimClsOfSvcCd\": \"MLAXE\", \"CktId\": \"DHEC492765803ATI\", \"USOCInfo\": [ { \"CLLIZEnd\": \"STLSMO09\", \"USOC\": \"1LNVX\", \"NetRate\": \"0.00\", \"AccessSpeed\": \"ACC T1.5MBPS MIS ACC\", \"Quantity\": \"1\", \"CLLIAEnd\": \"CLMAMORS\", \"lineItemId\": 59981, \"nxItemId\": 59981 } ], \"FALLOUTMATCHINGID\": \"0000000004/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\", \"Quality\": \"Standard\", \"nxAccessSpeed\": \"1.544 Mbps\", \"nxKeyId\": \"59981$Standard\" }, { \"PrimClsOfSvcCd\": \"MLAXE\", \"CktId\": \"DHEC492765804ATI\", \"USOCInfo\": [ { \"CLLIZEnd\": \"STLSMO09\", \"USOC\": \"1LNVX\", \"NetRate\": \"0.00\", \"AccessSpeed\": \"ACC T1.5MBPS MIS ACC\", \"Quantity\": \"1\", \"CLLIAEnd\": \"CLMAMORS\", \"lineItemId\": 59981, \"nxItemId\": 59981 } ], \"FALLOUTMATCHINGID\": \"0000000003/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\", \"Quality\": \"Standard\", \"nxAccessSpeed\": \"1.544 Mbps\", \"nxKeyId\": \"59981$Standard\" } ] } }, { \"CustomerName\": \"POLSINELLI\", \"CustomerSubAccountInfo\": { \"CustomerCircuitInfo\": [ { \"PrimClsOfSvcCd\": \"MLAX5\", \"CktId\": \"DHEC767301ATI\", \"USOCInfo\": [ { \"CLLIZEnd\": \"CLMAMD01\", \"Quantity\": \"1\", \"USOC\": \"1LNVX\", \"NetRate\": \"0.00\", \"AccessSpeed\": \"ACC T1.5MBPS MIS ACC\", \"CLLIAEnd\": \"BLTMMDCH\", \"lineItemId\": 59978, \"nxItemId\": 59978 } ], \"FALLOUTMATCHINGID\": \"0000000002/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\", \"Quality\": \"Standard\", \"nxAccessSpeed\": \"1.544 Mbps\", \"nxKeyId\": \"59978$Standard\" }, { \"PrimClsOfSvcCd\": \"MLAX5\", \"CktId\": \"DHEC912295ATI\", \"USOCInfo\": [ { \"CLLIZEnd\": \"CMBRMA01\", \"Quantity\": \"1\", \"USOC\": \"1LNVX\", \"NetRate\": \"0.00\", \"AccessSpeed\": \"ACC T1.5MBPS MIS ACC\", \"CLLIAEnd\": \"BSTNMACO\", \"lineItemId\": 59978, \"nxItemId\": 59978 } ], \"FALLOUTMATCHINGID\": \"0000000001/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\", \"Quality\": \"Standard\", \"nxAccessSpeed\": \"1.544 Mbps\", \"nxKeyId\": \"59978$Standard\" } ] } } ] }, \"flowType\": \"INR\" }";
		realMapper = new ObjectMapper();
		 mpOutPutJson="{\r\n" + 
				"    \"beginBillMonth\": \"July 2019\",\r\n" + 
				"    \"service\": \"AVPN\",\r\n" + 
				"    \"accountDetails\": [\r\n" + 
				"        {\r\n" + 
				"            \"custName\": \"CGI TECHNOLOGIES AND SOLUTIONS INC.\",\r\n" + 
				"            \"site\": [\r\n" + 
				"                {\r\n" + 
				"                    \"siteId\": \"90770133\",\r\n" + 
				"                    \"design\": [\r\n" + 
				"                        {\r\n" + 
				"                            \"circuitId\": \"MLEC968863ATI\",\r\n" + 
				"                            \"clli\": \"NYCMNY54\",\r\n" + 
				"                            \"priceDetails\": [\r\n" + 
				"                                {\r\n" + 
				"                                    \"beid\": \"17995\",\r\n" + 
				"                                    \"quantity\": \"1\",\r\n" + 
				"                                    \"nxItemId\": 6419040\r\n" + 
				"                                }\r\n" + 
				"                            ],\r\n" + 
				"                            \"nxKeyId\": \"6419040_273$US\",\r\n" + 
				"                            \"nxSiteId\": 1\r\n" + 
				"                        }\r\n" + 
				"                    ]\r\n" + 
				"                }\r\n" + 
				"            ]\r\n" + 
				"        }\r\n" + 
				"    ],\r\n" + 
				"    \"flowType\": \"INR\"\r\n" + 
				"}";
		 
		copyStatusLookup = new ArrayList<>();
		NxLookupData nx = new NxLookupData();
		nx.setItemId("200");
		nx.setDescription("30");
		nx.setCriteria("30,90");
		copyStatusLookup.add(nx);

	}
	
	@Test
	public void testinrQualifyCheckAccess() throws IOException {
		
		List<NxRequestDetails> nxRequestDetails = new ArrayList<>();
		NxRequestDetails nxReqGrpId= new NxRequestDetails();
		nxReqGrpId.setNxRequestGroupName("ACCESS_GROUP");
		nxReqGrpId.setNxReqId(1L);
		nxReqGrpId.setNxRequestGroupId(1L);
		nxReqGrpId.setStatus(30L);
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setNxSolutionId(1);
		nxReqGrpId.setNxSolutionDetail(nxSolutionDetail);
		List<NxOutputFileModel> nxOutputFiles= new ArrayList<>();
		NxOutputFileModel nxOutputFileModel = new NxOutputFileModel();
		nxOutputFileModel.setStatus("success");
		nxOutputFileModel.setMpOutputJson(accessJsonwithSiteId);
		nxOutputFileModel.setNxSiteIdInd(StringConstants.CONSTANT_Y);
		nxOutputFiles.add(nxOutputFileModel);
		nxReqGrpId.setNxOutputFiles(nxOutputFiles);
		nxRequestDetails.add(nxReqGrpId);
	
		NxRequestGroup nxRequestGroup = new NxRequestGroup();
		nxRequestGroup.setNxRequestGroupId(1L);
		
		when(nxRequestGroupRepository.findByNxRequestGroupIdAndActiveYn(anyLong(),anyString())).thenReturn(nxRequestGroup);
		when(nxRequestDetailsRepository.findByNxRequestGroupIdAndActiveYn(anyLong(),anyString())).thenReturn(nxRequestDetails);
		when(nxRequestDetailsRepository.findByNxReqIdAndActiveYn(anyLong(),anyString())).thenReturn(nxReqGrpId);
		NxDesignAudit nxDesignAudit= new NxDesignAudit();
		nxDesignAudit.setData("1");
		when(nxDesignAuditRepository.findByNxRefIdAndTransaction(anyLong(),anyString())).thenReturn(nxDesignAudit);
		
		List<NxOutputFileModel> nxOutputFileModelList= new ArrayList<>();
		nxOutputFileModelList.add(nxOutputFileModel);
		when(nxOutputFileRepository.findByNxReqId(any())).thenReturn(nxOutputFileModelList);
		List<NxValidationRules> rules = new ArrayList<>();
		NxValidationRules rule = new NxValidationRules();
		rule.setName("nxSiteId,nxSiteIdZ");
		rule.setValue("CktId");
		rule.setDataPath("/DomesticEthernetAccessInventory/CustomerAccountInfo,/CustomerSubAccountInfo/CustomerCircuitInfo");
		rule.setSubDataPath("/CustomerLocationInfo");
		rule.setSubData("AttCtrOffcCLLICd");
		rules.add(rule);
		NxValidationRules rule1 = new NxValidationRules();
		rule1.setName("nxSiteId,nxSiteIdZ");
		rule1.setValue("CktId");
		rule1.setDataPath("/DomesticDSODS1AccessInventory/CustomerAccountInfo,/CustomerSubAccountInfo/CustomerCircuitInfo");
		rule1.setSubDataPath("/CustomerLocationInfo");
		rule1.setSubData("AttCtrOffcCLLICd");
		rule1.setSubOffer("TDM");
		rules.add(rule1);
		when(nxValidationRulesRepository.findByValidationGroupAndOfferAndActiveAndFlowType(anyString(), anyString(), anyString(), anyString())).thenReturn(rules);
		JsonNode request= realMapper.readTree(accessJsonwithSiteId);
		when(mapper.readTree(anyString())).thenReturn(request);
		when(nexxusJsonUtility.getValue(any(), anyString())).thenReturn("DHEC404368ATI");
		inrQualifyService.inrQualifyCheck(1l, true, null);
		
	}
	
	@Test
	public void testinrQualifyCheckAccess1() throws IOException {
		
		List<NxRequestDetails> nxRequestDetails = new ArrayList<>();
		NxRequestDetails nxReqGrpId= new NxRequestDetails();
		nxReqGrpId.setNxRequestGroupName("ACCESS_GROUP");
		nxReqGrpId.setNxReqId(1L);
		nxReqGrpId.setNxRequestGroupId(1L);
		nxReqGrpId.setStatus(30L);
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setNxSolutionId(1);
		nxReqGrpId.setNxSolutionDetail(nxSolutionDetail);
		List<NxOutputFileModel> nxOutputFiles= new ArrayList<>();
		NxOutputFileModel nxOutputFileModel = new NxOutputFileModel();
		nxOutputFileModel.setStatus("success");
		nxOutputFileModel.setMpOutputJson(accessJsonwithSiteId);
		nxOutputFileModel.setNxSiteIdInd(StringConstants.CONSTANT_Y);
		nxOutputFiles.add(nxOutputFileModel);
		nxReqGrpId.setNxOutputFiles(nxOutputFiles);
		nxRequestDetails.add(nxReqGrpId);
	
		NxRequestGroup nxRequestGroup = new NxRequestGroup();
		nxRequestGroup.setNxRequestGroupId(1L);
		
		when(nxRequestGroupRepository.findByNxRequestGroupIdAndActiveYn(anyLong(),anyString())).thenReturn(nxRequestGroup);
		when(nxRequestDetailsRepository.findByNxRequestGroupIdAndActiveYn(anyLong(),anyString())).thenReturn(nxRequestDetails);
		when(nxRequestDetailsRepository.findByNxReqIdAndActiveYn(anyLong(),anyString())).thenReturn(nxReqGrpId);
		NxDesignAudit nxDesignAudit= new NxDesignAudit();
		nxDesignAudit.setData("1");
		when(nxDesignAuditRepository.findByNxRefIdAndTransaction(anyLong(),anyString())).thenReturn(nxDesignAudit);
		
		List<NxOutputFileModel> nxOutputFileModelList= new ArrayList<>();
		nxOutputFileModelList.add(nxOutputFileModel);
		when(nxOutputFileRepository.findByNxReqId(any())).thenReturn(nxOutputFileModelList);
		List<NxValidationRules> rules = new ArrayList<>();
		NxValidationRules rule = new NxValidationRules();
		rule.setName("nxSiteId,nxSiteIdZ");
		rule.setValue("CktId");
		rule.setDataPath("/DomesticEthernetAccessInventory/CustomerAccountInfo,/CustomerSubAccountInfo/CustomerCircuitInfo");
		rule.setSubDataPath("/CustomerLocationInfo");
		rule.setSubData("AttCtrOffcCLLICd");
		rules.add(rule);
		NxValidationRules rule1 = new NxValidationRules();
		rule1.setName("nxSiteId,nxSiteIdZ");
		rule1.setValue("CktId");
		rule1.setDataPath("/DomesticDSODS1AccessInventory/CustomerAccountInfo,/CustomerSubAccountInfo/CustomerCircuitInfo");
		rule1.setSubDataPath("/CustomerLocationInfo");
		rule1.setSubData("AttCtrOffcCLLICd");
		rule1.setSubOffer("TDM");
		rules.add(rule1);
		when(nxValidationRulesRepository.findByValidationGroupAndOfferAndActiveAndFlowType(anyString(), anyString(), anyString(), anyString())).thenReturn(rules);
		JsonNode request= realMapper.readTree(accessJsonwithSiteId);
		when(mapper.readTree(anyString())).thenReturn(request);
		inrQualifyService.inrQualifyCheck(1l, true, null);
		
	}
	
	@Test
	public void testinrQualifyCheckService() throws IOException {
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setNxSolutionId(1);
		List<NxRequestDetails> nxRequestDetails = new ArrayList<>();
		
		NxRequestDetails nxReqdetail3= new NxRequestDetails();
		nxReqdetail3.setNxRequestGroupName("SERVICE_ACCESS_GROUP");
		nxReqdetail3.setNxReqId(3L);
		nxReqdetail3.setNxRequestGroupId(1L);
		nxReqdetail3.setStatus(30L);
		nxReqdetail3.setNxSolutionDetail(nxSolutionDetail);
		List<NxOutputFileModel> nxOutputFiles3= new ArrayList<>();
		NxOutputFileModel nxOutputFileModel3 = new NxOutputFileModel();
		nxOutputFileModel3.setStatus("success");
		nxOutputFileModel3.setMpOutputJson(serviceJsonwithSiteId);
		nxOutputFileModel3.setNxSiteIdInd(StringConstants.CONSTANT_Y);
		nxOutputFiles3.add(nxOutputFileModel3);
		nxReqdetail3.setNxOutputFiles(nxOutputFiles3);
		nxRequestDetails.add(nxReqdetail3);
		
		NxRequestDetails nxReqdetail4= new NxRequestDetails();
		nxReqdetail4.setNxRequestGroupName("SERVICE_ACCESS_GROUP");
		nxReqdetail4.setNxReqId(4L);
		nxReqdetail4.setNxRequestGroupId(1L);
		nxReqdetail4.setStatus(30L);
		nxReqdetail4.setNxSolutionDetail(nxSolutionDetail);
		List<NxOutputFileModel> nxOutputFiles4= new ArrayList<>();
		NxOutputFileModel nxOutputFileModel4 = new NxOutputFileModel();
		nxOutputFileModel4.setStatus("success");
		nxOutputFileModel4.setMpOutputJson(serviceJson);
		nxOutputFileModel4.setNxSiteIdInd(StringConstants.CONSTANT_N);
		nxOutputFiles4.add(nxOutputFileModel4);
		nxReqdetail4.setNxOutputFiles(nxOutputFiles4);
		nxRequestDetails.add(nxReqdetail4);

		NxRequestGroup nxRequestGroup = new NxRequestGroup();
		nxRequestGroup.setNxRequestGroupId(1L);
		
		when(nxRequestGroupRepository.findByNxRequestGroupIdAndActiveYn(anyLong(),anyString())).thenReturn(nxRequestGroup);
		when(nxRequestDetailsRepository.findByNxRequestGroupIdAndActiveYn(anyLong(),anyString())).thenReturn(nxRequestDetails);
		when(nxRequestDetailsRepository.findByNxReqIdAndActiveYn(anyLong(),anyString())).thenReturn(nxReqdetail3);
		NxDesignAudit nxDesignAudit= new NxDesignAudit();
		nxDesignAudit.setData("1");
		when(nxDesignAuditRepository.findByNxRefIdAndTransaction(anyLong(),anyString())).thenReturn(nxDesignAudit);
		
		List<NxOutputFileModel> nxOutputFileModelList= new ArrayList<>();
		nxOutputFileModelList.add(nxOutputFileModel3);
		when(nxOutputFileRepository.findByNxReqId(any())).thenReturn(nxOutputFileModelList);
		List<NxValidationRules> rules = new ArrayList<>();
		NxValidationRules rule = new NxValidationRules();
		rule.setName("nxSiteId,nxSiteIdZ");
		rule.setValue("CktId");
		rule.setDataPath("/DomesticIOCInventory/CustomerAccountInfo,/CustomerSubAccountInfo/CustomerCircuitInfo");
		rule.setSubDataPath("/USOCInfo");
		rule.setSubData("CLLIAEnd,CLLIZEnd");
		rule.setDescription("CustomerLocationInfo");
		rules.add(rule);
		when(nxValidationRulesRepository.findByValidationGroupAndOfferAndActiveAndFlowType(anyString(), anyString(), anyString(), anyString())).thenReturn(rules);
		JsonNode request= realMapper.readTree(serviceJsonwithSiteId);
		when(mapper.readTree(anyString())).thenReturn(request);
		when(nexxusJsonUtility.getValue(any(), anyString())).thenReturn("DHEC492765801ATI");
		inrQualifyService.inrQualifyCheck(1l, true, null);
	}
	
	@Test
	public void testinrQualifyCheckServiceWithSite() throws IOException {
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setNxSolutionId(1);
		List<NxRequestDetails> nxRequestDetails = new ArrayList<>();
		
		NxRequestDetails nxReqdetail4= new NxRequestDetails();
		nxReqdetail4.setNxRequestGroupName("SERVICE_ACCESS_GROUP");
		nxReqdetail4.setNxReqId(4L);
		nxReqdetail4.setNxRequestGroupId(1L);
		nxReqdetail4.setStatus(30L);
		nxReqdetail4.setNxSolutionDetail(nxSolutionDetail);
		List<NxOutputFileModel> nxOutputFiles4= new ArrayList<>();
		NxOutputFileModel nxOutputFileModel4 = new NxOutputFileModel();
		nxOutputFileModel4.setStatus("success");
		nxOutputFileModel4.setMpOutputJson(serviceJson);
		nxOutputFileModel4.setNxSiteIdInd(StringConstants.CONSTANT_N);
		nxOutputFiles4.add(nxOutputFileModel4);
		nxReqdetail4.setNxOutputFiles(nxOutputFiles4);
		nxRequestDetails.add(nxReqdetail4);

		NxRequestGroup nxRequestGroup = new NxRequestGroup();
		nxRequestGroup.setNxRequestGroupId(1L);
		
		when(nxRequestGroupRepository.findByNxRequestGroupIdAndActiveYn(anyLong(),anyString())).thenReturn(nxRequestGroup);
		when(nxRequestDetailsRepository.findByNxRequestGroupIdAndActiveYn(anyLong(),anyString())).thenReturn(nxRequestDetails);
		when(nxRequestDetailsRepository.findByNxReqIdAndActiveYn(anyLong(),anyString())).thenReturn(nxReqdetail4);
		NxDesignAudit nxDesignAudit= new NxDesignAudit();
		nxDesignAudit.setData("1");
		when(nxDesignAuditRepository.findByNxRefIdAndTransaction(anyLong(),anyString())).thenReturn(nxDesignAudit);
		
		List<NxOutputFileModel> nxOutputFileModelList= new ArrayList<>();
		nxOutputFileModelList.add(nxOutputFileModel4);
		when(nxOutputFileRepository.findByNxReqId(any())).thenReturn(nxOutputFileModelList);
		List<NxValidationRules> rules = new ArrayList<>();
		NxValidationRules rule = new NxValidationRules();
		rule.setName("nxSiteId,nxSiteIdZ");
		rule.setValue("CktId");
		rule.setDataPath("/DomesticIOCInventory/CustomerAccountInfo,/CustomerSubAccountInfo/CustomerCircuitInfo");
		rule.setSubDataPath("/USOCInfo");
		rule.setSubData("CLLIAEnd,CLLIZEnd");
		rule.setDescription("CustomerLocationInfo");
		rules.add(rule);
		when(nxValidationRulesRepository.findByValidationGroupAndOfferAndActiveAndFlowType(anyString(), anyString(), anyString(), anyString())).thenReturn(rules);
		JsonNode request= realMapper.readTree(serviceJsonwithSiteId);
		when(mapper.readTree(anyString())).thenReturn(request);
		inrQualifyService.inrQualifyCheck(1l, true, null);
	}
	
	
	@Test
	public void testinrQualifyCheck() throws IOException {
		List<NxRequestDetails> nxRequestDetails = new ArrayList<>();
		NxRequestDetails nxReqGrpId= new NxRequestDetails();
		nxReqGrpId.setNxRequestGroupName("ACCESS_GROUP");
		nxReqGrpId.setNxReqId(1L);
		nxReqGrpId.setNxRequestGroupId(1L);
		nxReqGrpId.setStatus(30L);
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setNxSolutionId(1);
		nxReqGrpId.setNxSolutionDetail(nxSolutionDetail);
		List<NxOutputFileModel> nxOutputFiles= new ArrayList<>();
		NxOutputFileModel nxOutputFileModel = new NxOutputFileModel();
		nxOutputFileModel.setStatus("success");
		nxOutputFileModel.setMpOutputJson(mpOutPutJson);
		nxOutputFileModel.setNxSiteIdInd(StringConstants.CONSTANT_N);
		nxOutputFiles.add(nxOutputFileModel);
		nxRequestDetails.add(nxReqGrpId);
		nxReqGrpId.setNxOutputFiles(nxOutputFiles);

		
		NxRequestDetails nxReqdetail2= new NxRequestDetails();
		nxReqdetail2.setNxRequestGroupName(MyPriceConstants.SERVICE_GROUP);
		nxReqdetail2.setNxRequestGroupId(1L);
		nxReqdetail2.setNxReqId(1L);
		nxReqdetail2.setStatus(80L);
		List<NxOutputFileModel> nxOutputFiles2= new ArrayList<>();
		NxOutputFileModel nxOutputFileModel2 = new NxOutputFileModel();
		nxOutputFileModel2.setStatus("success");
		nxOutputFileModel2.setMpOutputJson("{\"test\":\"testdata\"}");
		nxOutputFileModel2.setNxSiteIdInd(StringConstants.CONSTANT_Y);
		nxOutputFiles2.add(nxOutputFileModel2);
		nxReqdetail2.setNxOutputFiles(nxOutputFiles2);
		nxRequestDetails.add(nxReqdetail2);
		
		NxRequestDetails nxReqdetail3= new NxRequestDetails();
		nxReqdetail3.setNxRequestGroupName(MyPriceConstants.SERVICE_ACCESS_GROUP);
		nxReqdetail3.setNxRequestGroupId(1L);
		nxReqdetail3.setStatus(20L);
		nxReqdetail3.setNxReqId(1L);
		nxReqdetail3.setNxOutputFiles(nxOutputFiles);
		nxRequestDetails.add(nxReqdetail3);

		NxRequestGroup nxRequestGroup = new NxRequestGroup();
		nxRequestGroup.setNxRequestGroupId(1L);
		when(nxRequestGroupRepository.findByNxRequestGroupIdAndActiveYn(anyLong(),anyString())).thenReturn(nxRequestGroup);
		when(nxRequestDetailsRepository.findByNxRequestGroupIdAndActiveYn(anyLong(),anyString())).thenReturn(nxRequestDetails);
		when(nxRequestDetailsRepository.findByNxReqIdAndActiveYn(anyLong(),anyString())).thenReturn(nxReqGrpId);
		NxDesignAudit nxDesignAudit= new NxDesignAudit();
		nxDesignAudit.setData("1");
		when(nxDesignAuditRepository.findByNxRefIdAndTransaction(anyLong(),anyString())).thenReturn(nxDesignAudit);
		List<NxOutputFileModel> nxOutputFileModelList= new ArrayList<>();
		nxOutputFileModelList.add(nxOutputFileModel);
		when(nxOutputFileRepository.findByNxReqId(any())).thenReturn(nxOutputFileModelList);
		JsonNode request= realMapper.readTree(mpOutPutJson);
		when(mapper.readTree(anyString())).thenReturn(request);
		//case1
		when(nxLookupDataRepository.findDatasetNameByItemIdAndDatasetName(anyString(),anyList())).thenReturn(MyPriceConstants.ACCESS_GROUP);
		inrQualifyService.inrQualifyCheck(1l,true, null);
		
		//case2
		when(nxLookupDataRepository.findDatasetNameByItemIdAndDatasetName(anyString(),anyList())).thenReturn(MyPriceConstants.SERVICE_GROUP);
		inrQualifyService.inrQualifyCheck(1l,true, null);

		//case3
		when(nxDesignAuditRepository.findByNxRefIdAndTransaction(anyLong(),anyString())).thenReturn(null);
		when(nxLookupDataRepository.findDatasetNameByItemIdAndDatasetName(anyString(),anyList())).thenReturn(MyPriceConstants.SERVICE_ACCESS_GROUP);
		inrQualifyService.inrQualifyCheck(1l,true, null);

	}

	@Test
	public void testinrQualifyCheckStatus10() {
		List<NxRequestDetails> nxRequestDetails = new ArrayList<>();
		List<NxOutputFileModel> nxOutputFiles= new ArrayList<>();
		NxOutputFileModel nxOutputFileModel = new NxOutputFileModel();
		nxOutputFileModel.setStatus(com.att.sales.nexxus.constant.CommonConstants.FAIL);
		nxOutputFiles.add(nxOutputFileModel);
		NxRequestDetails nxReqdetail= new NxRequestDetails();
		nxReqdetail.setNxRequestGroupName(MyPriceConstants.SERVICE_GROUP);
		nxReqdetail.setNxRequestGroupId(1L);
		nxReqdetail.setStatus(10L);
		nxReqdetail.setNxOutputFiles(nxOutputFiles);
		nxRequestDetails.add(nxReqdetail);
		NxRequestGroup nxRequestGroup = new NxRequestGroup();
		nxRequestGroup.setNxRequestGroupId(1L);

		when(nxRequestDetailsRepository.findByNxReqIdAndActiveYn(anyLong(),anyString())).thenReturn(nxReqdetail);
		when(nxRequestDetailsRepository.findByNxRequestGroupIdAndActiveYn(anyLong(),anyString())).thenReturn(nxRequestDetails);
		when(nxRequestGroupRepository.findByNxRequestGroupIdAndActiveYn(anyLong(),anyString())).thenReturn(nxRequestGroup);
		NxDesignAudit nxDesignAudit= new NxDesignAudit();
		nxDesignAudit.setData("1");
		when(nxDesignAuditRepository.findByNxRefIdAndTransaction(anyLong(),anyString())).thenReturn(nxDesignAudit);
		inrQualifyService.inrQualifyCheck(1l,true, null);
	}

	@Test
	public void testgetData() {
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		nxRequestDetails.setProduct("AVPN");
		List<NxValidationRules> nxValidationRuleList= new ArrayList<>();
		NxValidationRules nxValidationRules = new NxValidationRules();
		nxValidationRules.setDataPath("$..path");
		nxValidationRules.setDescription("description");
		nxValidationRuleList.add(nxValidationRules);
		when(nxValidationRulesRepository.findByValidationGroupAndOfferAndActiveAndFlowType(anyString(),anyString(),anyString(),
				anyString())).thenReturn(nxValidationRuleList);
		
		List<Object> results = new ArrayList<>();
		results.add("1");
		when(jsonPathUtil.search(any(), any(), any())).thenReturn(results);
		String mpOutputJson="{\"testdata\":\"testdata\"}" ; 
		Set<String> data =new HashSet();
		inrQualifyService.getData(mpOutputJson,data,data,nxRequestDetails);
		
	}
	
	@Test
	public void testgetCircuits() {
		List<Object> results = new ArrayList<>();
		results.add("1");
		when(jsonPathUtil.search(any(), any(), any())).thenReturn(results);
		inrQualifyService.getCircuits("request", "path");
		inrQualifyService.getCircuits(null, null);
	}

	@Test
	public void testProcess() throws IOException {
		List<NxOutputFileModel> nxOutputFileModelList=new ArrayList<>();
		NxOutputFileModel nxOutputFileModel = new NxOutputFileModel();
		Map<String, String> ddaMrc = new HashMap<>();
		nxOutputFileModel.setMpOutputJson("{\"testdata\":\"testdata\"}");
		nxOutputFileModelList.add(nxOutputFileModel);
		when(nxOutputFileRepository.findByNxReqId(anyLong())).thenReturn(nxOutputFileModelList);
		JsonNode request = realMapper.createObjectNode();
		when(mapper.readTree(anyString())).thenReturn(request);
		
		List<NxValidationRules> validationRuleList= new ArrayList<>();
		NxValidationRules nxValidationRules = new NxValidationRules();
		nxValidationRules.setDataPath("/accountDetails,/design");
		validationRuleList.add(nxValidationRules);
		
		when(nxValidationRulesRepository.findByValidationGroupAndOfferAndActiveAndFlowType(anyString(),
				anyString(),anyString(),anyString())).thenReturn(validationRuleList);
		when(nexxusJsonUtility.getValue(any(),anyString())).thenReturn("5765");
		boolean prepareSiteMap=true;
		String productType="SERVICE";
		Map<String, List<CircuitSiteDetails>> cktSiteMap= new HashMap<>();
		List<CircuitSiteDetails> circuitSiteDetailsList= new ArrayList<CircuitSiteDetails>();
		CircuitSiteDetails circuitSiteDetails = new CircuitSiteDetails();
		circuitSiteDetails.setClli("clii");
		circuitSiteDetails.setEndType("endtype");
		circuitSiteDetails.setId(1L);
		circuitSiteDetailsList.add(circuitSiteDetails);
		cktSiteMap.put("5765",circuitSiteDetailsList);
		AtomicInteger nxSiteIdCounter=new AtomicInteger(1);
		Map<String, List<Object>> cktLocations = new HashMap<>();
		String value = "{\r\n" + 
				"    \"beginBillMonth\": \"February 2020\",\r\n" + 
				"    \"service\": \"AVPN\",\r\n" + 
				"    \"searchCriteriaValue\": \"072918402\",\r\n" + 
				"    \"accountDetails\": [\r\n" + 
				"        {\r\n" + 
				"            \"currency\": \"USD\",\r\n" + 
				"            \"custName\": \"PHYSICIANS MUTUAL INSURANCE CO\",\r\n" + 
				"            \"site\": [\r\n" + 
				"                {\r\n" + 
				"                    \"country\": \"US\",\r\n" + 
				"                    \"state\": \"NE\",\r\n" + 
				"                    \"city\": \"OMAHA\",\r\n" + 
				"                    \"address\": \"2600 DODGE ST\",\r\n" + 
				"                    \"siteId\": \"90888109\",\r\n" + 
				"                    \"design\": [\r\n" + 
				"                        {\r\n" + 
				"                            \"siteName\": \"2973294\",\r\n" + 
				"                            \"circuitId\": \"IUEC985743ATI\",\r\n" + 
				"                            \"portSpeed\": \"100000\",\r\n" + 
				"                            \"clli\": \"OMAHNENW\",\r\n" + 
				"                            \"accessBandwidth\": \"100000\",\r\n" + 
				"                            \"priceDetails\": [\r\n" + 
				"                                {\r\n" + 
				"                                    \"priceType\": \"PORTBEID\",\r\n" + 
				"                                    \"beid\": \"18014\",\r\n" + 
				"                                    \"quantity\": \"1\",\r\n" + 
				"                                    \"localListPrice\": \"1355\",\r\n" + 
				"                                    \"actualPrice\": \"542\",\r\n" + 
				"                                    \"secondaryKey\": \"#FCC#MPLS Port#Flat Rate#100M#Enet, OC3#Enet, ATM, IP#VPN Transport Connection#per port#18014#18030#United States#US#USA\",\r\n" + 
				"                                    \"uniqueId\": \"#MPLS Port#MPLS Port - 100M#Enet, ATM, IP#100 Mbps#ENET#VPN Transport#Connection#Each\",\r\n" + 
				"                                    \"nxItemId\": 6418993\r\n" + 
				"                                }\r\n" + 
				"                            ],\r\n" + 
				"                            \"nxKeyId\": \"6418993_542$US\",\r\n" + 
				"                            \"nxSiteId\": 2\r\n" + 
				"                        }\r\n" + 
				"                    ],\r\n" + 
				"                    \"FALLOUTMATCHINGID\": \"0000000001/AVPNPricingInventory/Body/AccountDetails/AVPNService/AVPNInventoryDetails/SiteDetails/FALLOUTMATCHINGID\"\r\n" + 
				"                }\r\n" + 
				"            ]\r\n" + 
				"        }\r\n" + 
				"    ],\r\n" + 
				"    \"flowType\": \"INR\"\r\n" + 
				"}";
		JsonNode requestNode = JacksonUtil.toJsonNode(value);
		
		inrQualifyService.process(requestNode,nxValidationRules, prepareSiteMap, productType, cktSiteMap, cktLocations, nxSiteIdCounter, null,null,ddaMrc);
	}
	
	@Test
	public void testsaveNxSolutionSite() {
		when(nxSolutionSiteRepository.findByNxSolutionIdAndNxRequestGroupIdAndActiveYNAndNxReqId(anyLong(),anyLong(),anyString(),anyLong())).thenReturn(null);
		List<Object> siteAddress=new ArrayList<>();
		siteAddress.add("address");
		NxRequestDetails nxReqGrpId=new NxRequestDetails();
		NxSolutionDetail nxSolutionDetail= new NxSolutionDetail();
		nxSolutionDetail.setNxSolutionId(1);
		nxReqGrpId.setNxSolutionDetail(nxSolutionDetail);
		nxReqGrpId.setNxRequestGroupId(1L);
		nxReqGrpId.setNxReqId(1L);
		inrQualifyService.saveNxSolutionSite(siteAddress, nxReqGrpId);
	}
	
	@Test
	public void testdeleteDisQualifiedCkts() {
		List<NxDesignAudit> nxDesignAuditList= new ArrayList<>();
		NxDesignAudit nxDesignAudit = new NxDesignAudit();
		nxDesignAuditList.add(nxDesignAudit);
		when(nxDesignAuditRepository.findByNxRefIdAndTransactions(anyList(),anyList())).thenReturn(nxDesignAuditList);
		List reqid = new ArrayList();
		reqid.add(1L);
		inrQualifyService.deleteDisQualifiedCkts(reqid);
	}
	
	@Test
	public void testpopulateLocationsInService() throws IOException {
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		nxRequestDetails.setProduct("AVPN");
		nxRequestDetails.setNxReqId(1L);
		nxRequestDetails.setStatus(30l);
		List<NxRequestDetails> accessRequest= new ArrayList<>();
		accessRequest.add(nxRequestDetails);
		List<NxRequestDetails> serviceRequest=new ArrayList<>();
		serviceRequest.add(nxRequestDetails);
		boolean isGenerateNxSiteId=true;
		Mockito.when(nxMyPriceRepositoryServce.getItemDescFromLookup(anyString(), anyString())).thenReturn(copyStatusLookup);
		List<NxValidationRules> nxValidationRuleList= new ArrayList<>();
		NxValidationRules nxValidationRules = new NxValidationRules();
		nxValidationRules.setSubDataPath("..$");
		nxValidationRules.setDataPath("/accountdetail");
		nxValidationRuleList.add(nxValidationRules);
		when(nxValidationRulesRepository.findByValidationGroupAndOfferAndActiveAndFlowType(anyString(),anyString(),
				anyString(),anyString())).thenReturn(nxValidationRuleList);
		
		List<Object> results=new ArrayList<>();
		results.add("test");
		when(jsonPathUtil.search(any(), anyString(), any())).thenReturn(results);

		List<NxOutputFileModel> nxOutputFileModelList = new ArrayList<>();
		NxOutputFileModel nxOutputFileModel = new NxOutputFileModel();
		nxOutputFileModel.setMpOutputJson(mpOutPutJson);
		nxOutputFileModelList.add(nxOutputFileModel);
		when(nxOutputFileRepository.findByNxReqId(anyLong())).thenReturn(nxOutputFileModelList);
		when(nexxusJsonUtility.getValue(any(),any())).thenReturn("value");
		JsonNode request=realMapper.readTree(mpOutPutJson);
		when(mapper.readTree(anyString())).thenReturn(request);

		inrQualifyService.populateLocationsInService(accessRequest,serviceRequest,isGenerateNxSiteId);
	}
	
	@Test
	public void testgenerateNxSiteId() throws IOException {
		List<NxRequestDetails> nxRequestDetailList=new ArrayList<>();
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		Map<String, String> ddaMrc = new HashMap<>();
		nxRequestDetails.setNxReqId(1L);
		nxRequestDetails.setProduct("AVPN");
		nxRequestDetails.setStatus(30l);
		nxRequestDetailList.add(nxRequestDetails);
		Mockito.when(nxMyPriceRepositoryServce.getItemDescFromLookup(anyString(), anyString())).thenReturn(copyStatusLookup);
		Map<String, List<CircuitSiteDetails>> cktSiteMap=new HashMap<>();
		List<CircuitSiteDetails> circuitSiteDetailsList= new ArrayList<CircuitSiteDetails>();
		CircuitSiteDetails circuitSiteDetails = new CircuitSiteDetails();
		circuitSiteDetails.setClli("clii");
		circuitSiteDetails.setEndType("endtype");
		circuitSiteDetails.setId(1L);
		circuitSiteDetailsList.add(circuitSiteDetails);
		cktSiteMap.put("5765",circuitSiteDetailsList);
		boolean prepareSiteMap=false;
		String productType="ACCESS";
		boolean isGenerateNxSiteId=true;
		Map<String, List<Object>> cktLocations = null;
		String flowType="INR";
		AtomicInteger nxSiteIdCounter=new AtomicInteger(1);
		List<NxOutputFileModel> nxOutputFileModelsList= new ArrayList<>();
		NxOutputFileModel nxOutputFileModel = new NxOutputFileModel();
		nxOutputFileModel.setMpOutputJson(mpOutPutJson);
		nxOutputFileModelsList.add(nxOutputFileModel);
		JsonNode request=realMapper.readTree(mpOutPutJson);
		when(mapper.readTree(anyString())).thenReturn(request);
		when(nxOutputFileRepository.findByNxReqId(anyLong())).thenReturn(nxOutputFileModelsList);

		List<NxValidationRules> nxValidationRuleList= new ArrayList<>();
		NxValidationRules nxValidationRules = new NxValidationRules();
		nxValidationRules.setDataPath("/accountDetails,/design");
		nxValidationRuleList.add(nxValidationRules);
		
		NxValidationRules nxValidationRules2 = new NxValidationRules();
		nxValidationRules2.setDataPath("/accountDetails");
		nxValidationRuleList.add(nxValidationRules2);

		when(nxValidationRulesRepository.findByValidationGroupAndOfferAndActiveAndFlowType(anyString(),anyString(),anyString(),
				anyString())).thenReturn(nxValidationRuleList);
		inrQualifyService.generateNxSiteId(nxRequestDetailList, cktSiteMap, prepareSiteMap, productType, cktLocations, flowType, isGenerateNxSiteId, nxSiteIdCounter, null,null,ddaMrc);
	}
	
	@Test
	public void testqualifyCheck() {
		List<NxRequestDetails> accessRequestList = new  ArrayList<NxRequestDetails>();
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		nxRequestDetails.setProduct("AVPN");
		nxRequestDetails.setNxRequestGroupName("ACCESS");
		accessRequestList.add(nxRequestDetails);
		List<NxRequestDetails> serviceRequestList = new ArrayList<NxRequestDetails>();
		NxRequestDetails nxRequestDetails2= new NxRequestDetails();
		nxRequestDetails2.setNxRequestGroupName("SERVICE");
		nxRequestDetails2.setProduct("BVoIP");
		serviceRequestList.add(nxRequestDetails2);
		Long solutionId=1l;
		NxRequestGroup nxRequestGroup=new NxRequestGroup();
		nxRequestGroup.setNxRequestGroupId(1L);
		List<NxOutputFileModel> nxOutputFileModelList= new ArrayList<>();
		NxOutputFileModel nxOutputFileModel = new NxOutputFileModel();
		nxOutputFileModel.setMpOutputJson(mpOutPutJson);
		nxOutputFileModelList.add(nxOutputFileModel);
		when(nxOutputFileRepository.findByNxReqId(anyLong())).thenReturn(nxOutputFileModelList);
		when(nxDesignAuditRepository.findByTransactionAndStatusAndNxRefId(anyString(),anyString(),anyLong())).thenReturn(null);
		List<NxValidationRules> nxValidationRuleList= new ArrayList<>();
		NxValidationRules nxValidationRules = new NxValidationRules();
		nxValidationRules.setDataPath("$..path");
		nxValidationRules.setDescription("description");
		nxValidationRuleList.add(nxValidationRules);
		when(nxValidationRulesRepository.findByValidationGroupAndOfferAndActiveAndFlowType(matches("QUALIFY"),anyString(),anyString(),
				anyString())).thenReturn(nxValidationRuleList);
		List<Object> results = new ArrayList<>();
		results.add("1");
		when(jsonPathUtil.search(any(), any(), any())).thenReturn(results);
		inrQualifyService.qualifyCheck(accessRequestList,serviceRequestList,solutionId, nxRequestGroup);
	
		when(nxValidationRulesRepository.findByValidationGroupAndOfferAndActiveAndFlowType(matches("QUALIFY"),anyString(),anyString(),
				anyString())).thenReturn(new ArrayList<>());
		inrQualifyService.qualifyCheck(accessRequestList,serviceRequestList,solutionId, nxRequestGroup);

	}
	
	@Test
	public void testprepareSiteData() throws JsonProcessingException {
		String productName="AVPN";
		List<Object> siteAddress=new ArrayList<>();
		Object outputJson="{ \"country\": \"US\", \"state\": \"MO\", \"city\": \"ST LOUIS\", \"address\": \"100 S 4\", \"siteId\": \"90090930\", \"custPostalcode\": \"63012\", \"design\": [ { \"siteName\": \"1504483\", \"accessCarrier\": \"AT&T\", \"circuitId\": \"IUEC961778ATI\", \"technology\": \"Ethernet (Gateway Interconnect/ESP ETH Shared)\", \"portSpeed\": \"100000\", \"clli\": \"STLSMO09\", \"accessBandwidth\": \"100000\", \"priceDetails\": [ { \"priceType\": \"PORTBEID\", \"beid\": \"18014\", \"quantity\": \"1\", \"localListPrice\": \"886\", \"actualPrice\": \"443\", \"secondaryKey\": \"#FCC#MPLS Port#Flat Rate#100M#Enet, OC3#Enet, ATM, IP#VPN Transport Connection#per port#18014#18030#United States#US#USA\", \"elementType\": \"Port\", \"uniqueId\": \"#MPLS Port#MPLS Port - 100M#Enet, ATM, IP#100 Mbps#ENET#VPN Transport#Connection#Each\", \"nxItemId\": 6418993 } ], \"nxKeyId\": \"6418993_443$US\", \"nxSiteId\": 1 } ], \"FALLOUTMATCHINGID\": \"0000000029/AVPNPricingInventory/Body/AccountDetails/AVPNService/AVPNInventoryDetails/SiteDetails/FALLOUTMATCHINGID\" }";
		Set<String> processedSites=new HashSet<>(); 
		String flowType="INR";
		List<NxValidationRules> nxValidationRulesList = new ArrayList<>();
		NxValidationRules nxValidationRules = new NxValidationRules();
		nxValidationRules.setDataPath("$.accountDetails..site[*]");
		nxValidationRules.setSubDataPath("$.design[*]");
		nxValidationRules.setSubData("nxSiteId:nxSiteId;iCoreSiteID:siteName;CircuitID:circuitId;name:NO_TAG:TBD-nxSiteId");
		nxValidationRules.setFlowType("INR");
		nxValidationRules.setValue("city:city:PLACEHOLDER;postalCode:custPostalcode:PLACEHOLDER;addressLine:address:PLACEHOLDER;state:state:PLACEHOLDER;country:country:PLACEHOLDER;regionFranchiseStatus:NO_TAG;buildingClli:NO_TAG;swcCLLI:NO_TAG;popClli:NO_TAG;GRSSiteID:siteId;address:address#city#state#custPostalcode#country:PLACEHOLDER:,");
		nxValidationRulesList.add(nxValidationRules);
		when(nxValidationRulesRepository.findByValidationGroupAndOfferAndActiveAndFlowType(anyString(),anyString(),
				anyString(),anyString())).thenReturn(nxValidationRulesList);
		List<Object> results = new ArrayList<>();
		results.add(outputJson);
		when(jsonPathUtil.search(any(), any(), any())).thenReturn(results);
		JsonNode cktData = realMapper.valueToTree(outputJson);
		when(mapper.valueToTree(any())).thenReturn(cktData);
		when(mapper.writeValueAsString(any())).thenReturn("test");
		inrQualifyService.prepareSiteData(productName,siteAddress,outputJson,processedSites,flowType);
	}
	
	@Test
	public void testgenerateNxsiteId() throws IOException {
		JsonNode dataNode = realMapper.readTree("{\r\n" + 
				"    \"beginBillMonth\": \"July 2019\",\r\n" + 
				"    \"service\": \"AVPN\",\r\n" + 
				"    \"accountDetails\": [\r\n" + 
				"        {\r\n" + 
				"            \"currency\": \"USD\",\r\n" + 
				"            \"custName\": \"CGI TECHNOLOGIES AND SOLUTIONS INC.\",\r\n" + 
				"            \"site\": [\r\n" + 
				"                {\r\n" + 
				"                    \"state\": \"NY\",\r\n" + 
				"                    \"siteId\": \"90770133\",\r\n" + 
				"                    \"design\": [\r\n" + 
				"                        {\r\n" + 
				"                            \"siteName\": \"2792573\",\r\n" + 
				"                            \"circuitId\": \"MLEC968863ATI\",\r\n" + 
				"                            \"priceDetails\": [\r\n" + 
				"                                {\r\n" + 
				"                                    \"priceType\": \"PORTBEID\",\r\n" + 
				"                                    \"nxItemId\": 6419040\r\n" + 
				"                                }\r\n" + 
				"                            ],\r\n" + 
				"                            \"nxKeyId\": \"6419040_273$US\",\r\n" + 
				"                            \"nxSiteId\": 1\r\n" + 
				"                        }\r\n" + 
				"                    ],\r\n" + 
				"                    \"FALLOUTMATCHINGID\": \"0000000060/AVPNPricingInventory/Body/AccountDetails/AVPNService/AVPNInventoryDetails/SiteDetails/FALLOUTMATCHINGID\"\r\n" + 
				"                }\r\n" + 
				"            ]\r\n" + 
				"        }\r\n" + 
				"    ],\r\n" + 
				"    \"flowType\": \"INR\"\r\n" + 
				"}");

		Iterator<JsonNode>  datas = dataNode.at("/accountDetails").elements();
		NxValidationRules nvalidationRule=new NxValidationRules() ;
		nvalidationRule.setName("nxSiteId");
		nvalidationRule.setSubData("CLLIAEnd,CLLIZEnd");
		nvalidationRule.setSubDataPath("/site/design");
		Map<String, String> ddaMrc = new HashMap<>();
		String productType="ACCESS";
		Map<String ,List<CircuitSiteDetails>> cktSiteMap = null;
		Map<String, List<Object>> cktLocations = null;
		AtomicInteger nxSiteIdCounter=new AtomicInteger(1);
		inrQualifyService.generateNxsiteId(datas,nvalidationRule,true,productType,cktSiteMap,cktLocations,nxSiteIdCounter, null,null,ddaMrc);
	}
	
	@Test
	public void testprepareIglooCktSiteMap() {
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		nxRequestDetails.setProduct("AVPN");
		NxSolutionDetail nxSolutionDetail=new NxSolutionDetail();
		nxSolutionDetail.setNxSolutionId(1L);
		nxRequestDetails.setNxSolutionDetail(nxSolutionDetail);
		when(nxRequestDetailsRepository.findByNxReqId(anyLong())).thenReturn(nxRequestDetails);
		List<NxRequestDetails> nxRequestDetailList = new ArrayList<>();
		nxRequestDetailList.add(nxRequestDetails);
		when(nxRequestDetailsRepository.findByNxSolutionId(anyLong())).thenReturn(nxRequestDetailList);
		NxLookupData nxLookupData = new NxLookupData();
		nxLookupData.setCriteria("AVPN");
		List<NxLookupData> nxLookupdataList = new ArrayList<>();
		nxLookupdataList.add(nxLookupData);
		when(nxLookupDataRepository.findByDatasetNameAndActive(anyString(),anyString())).thenReturn(nxLookupdataList);
		List<NxAccessPricingData> nxAccessPricingDataList = new ArrayList<>();
		NxAccessPricingData nxAccessPricingData = new NxAccessPricingData();
		nxAccessPricingData.setCircuitId("cktid");
		nxAccessPricingData.setNxSiteId(1L);
		nxAccessPricingDataList.add(nxAccessPricingData);
		when(nxaccessPricingDataRepo.findByNxSolutionId(anyLong())).thenReturn(nxAccessPricingDataList);
		Map<String, List<CircuitSiteDetails>> cktSiteMap = new LinkedHashMap<>();
		inrQualifyService.prepareIglooCktSiteMap(cktSiteMap,1L);
	}
	
	@Test
	public void testprepareInrCktSiteMap() {
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		nxRequestDetails.setNxRequestGroupName( MyPriceConstants.SERVICE_ACCESS_GROUP);
		nxRequestDetails.setProduct("AVPN");
		NxSolutionDetail nxSolutionDetail=new NxSolutionDetail();
		nxSolutionDetail.setNxSolutionId(1L);
		nxRequestDetails.setNxSolutionDetail(nxSolutionDetail);
		List<NxRequestDetails> nxRequestDetailList = new ArrayList<>();
		nxRequestDetailList.add(nxRequestDetails);
		when(nxRequestDetailsRepository.findByNxSolutionId(anyLong())).thenReturn(nxRequestDetailList);
		NxLookupData nxLookupData = new NxLookupData();
		nxLookupData.setCriteria("AVPN");
		List<NxLookupData> nxLookupdataList = new ArrayList<>();
		nxLookupdataList.add(nxLookupData);
		when(nxLookupDataRepository.findByDatasetNameAndActive(anyString(),anyString())).thenReturn(nxLookupdataList);
		NxDesignAudit nxDesignAudit = new NxDesignAudit();
		nxDesignAudit.setData("1");
		when(nxDesignAuditRepository.findByNxRefIdAndTransaction(anyLong(),anyString())).thenReturn(nxDesignAudit);
		inrQualifyService.prepareInrCktSiteMap(1L);
		
	}
}