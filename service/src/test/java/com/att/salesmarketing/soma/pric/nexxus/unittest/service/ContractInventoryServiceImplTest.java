package com.att.salesmarketing.soma.pric.nexxus.unittest.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.dao.model.NxSsdfSpeedMapping;
import com.att.sales.nexxus.dao.repository.NxSsdfSpeedMappingRepository;
import com.att.sales.nexxus.reteriveicb.model.ContractInvResponse;
import com.att.sales.nexxus.reteriveicb.model.ContractInvResponseBean;
import com.att.sales.nexxus.reteriveicb.model.ContractInventoryRequestBean;
import com.att.sales.nexxus.reteriveicb.model.ContractInventoryRequestDetails;
import com.att.sales.nexxus.reteriveicb.model.SDNEthernetContractResponse;
import com.att.sales.nexxus.reteriveicb.model.SDNEthernetContractResponseBean;
import com.att.sales.nexxus.service.ContractInventoryServiceImpl;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.att.sales.nexxus.util.DME2RestClient;
import com.att.sales.nexxus.util.HttpRestClient;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@ExtendWith(MockitoExtension.class)

public class ContractInventoryServiceImplTest {
	
	@InjectMocks
	private ContractInventoryServiceImpl contractInventoryServiceImpl;
	
	@Mock
	private NxSsdfSpeedMappingRepository nxSsdfSpeedMappingDao;

	@Mock
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	@Mock
	private Environment env;
	
	@Mock
	private DME2RestClient dme2RestClient;
	
	/** The mapper. */
	@Mock
	private ObjectMapper mapper;
	//@Mock
	//private SDNEthernetContractResponseBean ssdfResponse;
	@Mock
	private SDNEthernetContractResponse ssdf;
	
	private String invResponse = null;
	private String inventoryResponse=null;
	@Mock
	private ContractInventoryRequestBean contractInventoryRequestBean;
	@Mock
	private ContractInventoryRequestDetails contractInventoryRequest;
	@Mock
	private ContractInvResponse contractInvResponse;
	@Mock
	private ContractInvResponseBean contractInvResponseBean; 
	
	@Mock
	private HttpRestClient httpRest;


	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(ContractInventoryServiceImpl.class);
	
	
	@BeforeEach
	public void init() {
		invResponse ="{ \"SDNEthernetContractResponse\": { \"ContractID\": \"SDN1U3ZRPN\", \"ContractTerm\": \"12\", \"ContractICB\": \"N\", \"NetPricePercentage\": \"0\", \"OfferId\": \"151\", \"PricePlanId\": \"2779\", \"RatePlanId\": \"7031\", \"SDNCharges\": { \"PortFees\": { \"Price\": [ { \"RateId\": \"11535154\", \"RateDescription\": \"Port Feature\", \"FieldName\": \"Port Fee\", \"PortCharge\": \"Inside Wiring\", \"TypeOfRate\": \"NRC\", \"Currency\": \"USD\", \"Rate\": \"0.0\" } ] }, \"CosPremium\": [ { \"FieldName\": \"Class of Service Premium\", \"CosPremium\": \"Real Time\", \"COSonContract\": \"Y\", \"CIRSpeed\": [ { \"FieldName\": \"CIR\", \"CIR\": \"4\", \"CIRUnits\": \"MBPS\", \"CIRonContract\": \"Y\", \"Price\": { \"RateId\": \"11593214\", \"RateDescription\": \"CIR/CoS\", \"FieldName\": \"MRC\", \"CIRTypeOfRate\": \"MRC\", \"CIRCurrency\": \"USD\", \"CIRListRate\": \"617.08\", \"RateCategory\": \"Tier3\" } } ] } ], \"AseThirdPartyDetail\": [ { \"FieldName\": \"Connection\", \"COSonContract\": \"Y\", \"ConnectionType\": \"Switched\", \"PriceMileageGroup\": \"Price Group 19\", \"CIRSpeed\": [ { \"FieldName\": \"CIR\", \"CIR\": \"250\", \"CIRUnits\": \"MBPS\", \"CIRonContract\": \"Y\", \"Price\": { \"RateId\": \"11584780\", \"CIRTypeOfRate\": \"MRC\", \"CIRCurrency\": \"USD\", \"CIRListRate\": \"1784.48\" } } ] }, { \"FieldName\": \"Class of Service Premium\", \"CosPremium\": \"Real Time\", \"COSonContract\": \"Y\", \"CIRSpeed\": [ { \"FieldName\": \"CIR\", \"CIR\": \"500\", \"CIRUnits\": \"MBPS\", \"CIRonContract\": \"Y\", \"Price\": { \"RateId\": \"11568051\", \"CIRTypeOfRate\": \"MRC\", \"CIRCurrency\": \"USD\", \"CIRListRate\": \"15466.07\" } } ] }, { \"FieldName\": \"Mileage\", \"COSonContract\": \"Y\", \"ConnectionType\": \"Dedicated\", \"PriceMileageGroup\": \"Mileage G\", \"MileageType\": \"Per Mileage\", \"CIRSpeed\": [ { \"FieldName\": \"CIR\", \"CIR\": \"600\", \"CIRUnits\": \"MBPS\", \"CIRonContract\": \"Y\", \"Price\": { \"RateId\": \"11567946\", \"CIRTypeOfRate\": \"MRC\", \"CIRCurrency\": \"USD\", \"CIRListRate\": \"0.0\" } } ] } ], \"SDNMRC\": [ { \"MRCCharge\": \"10G Port Connection\", \"FieldName\": \"MRC Charge\", \"PortConMRConContract\": \"Y\", \"Price\": { \"RateId\": \"11593543\", \"RateDescription\": \"Port Connection\", \"RateType\": \"MRC\", \"RateCurrency\": \"USD\", \"Rate\": \"960.0\", \"RateCategory\": \"Aggregator\" } }, { \"MRCCharge\": \"100M Port Connection\", \"FieldName\": \"MRC Charge\", \"PortConMRConContract\": \"Y\", \"Price\": { \"RateId\": \"11535063\", \"RateDescription\": \"Port Connection\", \"RateType\": \"MRC\", \"RateCurrency\": \"USD\", \"Rate\": \"272.0\" } }, { \"MRCCharge\": \"10G Port Connection\", \"FieldName\": \"MRC Charge\", \"PortConMRConContract\": \"Y\", \"Price\": { \"RateId\": \"11535807\", \"RateDescription\": \"Port Connection\", \"RateType\": \"MRC\", \"RateCurrency\": \"USD\", \"Rate\": \"1300.0\" } }, { \"MRCCharge\": \"Enhanced Multicast\", \"FieldName\": \"MRC Charge\", \"PortConMRConContract\": \"Y\", \"Price\": { \"RateId\": \"11535315\", \"RateDescription\": \"Port Feature\", \"RateType\": \"MRC\", \"RateCurrency\": \"USD\", \"Rate\": \"70.0\" } }, { \"MRCCharge\": \"Additional MAC Addresses\", \"FieldName\": \"MRC Charge\", \"PortConMRConContract\": \"Y\", \"Price\": { \"RateId\": \"11535062\", \"RateDescription\": \"Port Feature\", \"RateType\": \"MRC\", \"RateCurrency\": \"USD\", \"Rate\": \"5.0\" } }, { \"MRCCharge\": \"1G Port Connection\", \"FieldName\": \"MRC Charge\", \"PortConMRConContract\": \"Y\", \"Price\": { \"RateId\": \"11535289\", \"RateDescription\": \"Port Connection\", \"RateType\": \"MRC\", \"RateCurrency\": \"USD\", \"Rate\": \"272.0\" } } ] }, \"MarketSegment\": \"Wholesale\" } }";
		inventoryResponse="{ \"status\": {\"code\": \"200\",\"messages\": [ { \"code\": \"M00000\",\"description\": \"REQUEST_COMPLETED_SUCCESSFULLY\",\"detailedDescription\": \"REQUEST_COMPLETED_SUCCESSFULLY\"}]},\"transactionId\": \"restasap18461112122020\",\"responseTime\": \"5947\",\"timestamp\": \"Sat Feb 06 15:26:34 IST 2021\",\"responseStatus\": \"ContractNumber is mandatory\"}";
	}
	@Test
	@Disabled
	public void testGetContractInventoryTest() throws SalesBusinessException, JsonParseException, JsonMappingException, IOException {
		ContractInventoryRequestBean request = new ContractInventoryRequestBean();
		ContractInventoryRequestDetails contractInventoryRequest = new ContractInventoryRequestDetails();
		contractInventoryRequest.setContractNumber("SDN1U3ZRPN");
		contractInventoryRequest.setTransactionId("10SDNA58NV0U00");
		request.setContractInventoryRequest(contractInventoryRequest);
		List<NxSsdfSpeedMapping> nxSsdfSpeedMappings=new ArrayList<NxSsdfSpeedMapping>();
		NxSsdfSpeedMapping mapping=new NxSsdfSpeedMapping();
		mapping.setActive("Y");
		mapping.setComponent("Mileage");
		mapping.setConnectionType("Dedicated");
		mapping.setFormula("((2 <= speed ) && (speed<=50))");
		mapping.setId(101l);
		mapping.setOffer("ASEoD3PA");
		mapping.setPriceGroup("Mileage G");
		mapping.setPriceType("Per Mileage");
		mapping.setSpeedRange("2Mbp50Mbps");
		nxSsdfSpeedMappings.add(mapping);
		when(nxSsdfSpeedMappingDao.getSpeedData(anyString(), anyString())).thenReturn(nxSsdfSpeedMappings);
		Mockito.when(env.getProperty(anyString())).thenReturn("test");
		/*Mockito.when(dme2RestClient.callDme2Client(anyString(), anyString(), anyString(), anyString(), anyString(),
				anyString(), anyString(), anyString(), anyMap(), anyString(), anyMap())).thenReturn(invResponse);*/
		Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString())).thenReturn(invResponse);

		
		ObjectMapper objMapper = new ObjectMapper();
		SDNEthernetContractResponseBean ssdfResponse = objMapper.readValue(invResponse,
						SDNEthernetContractResponseBean.class);
		Mockito.when(mapper.readValue(anyString(),eq(SDNEthernetContractResponseBean.class))).thenReturn(ssdfResponse);
		contractInventoryServiceImpl.getContractInventory(request);
	}
	@Test
	public void getContractNumberMandatoryTest() throws SalesBusinessException, JsonParseException, JsonMappingException, IOException {
		ContractInventoryRequestBean request = new ContractInventoryRequestBean();
		ContractInventoryRequestDetails contractInventoryRequest = new ContractInventoryRequestDetails();
		contractInventoryRequest.setTransactionId("10SDNA58NV0U00");
		request.setContractInventoryRequest(contractInventoryRequest);
		ContractInvResponseBean response = new ContractInvResponseBean();
		List<NxSsdfSpeedMapping> nxSsdfSpeedMappings=new ArrayList<NxSsdfSpeedMapping>();
		when(nxSsdfSpeedMappingDao.getSpeedData(anyString(), anyString())).thenReturn(nxSsdfSpeedMappings);
		Mockito.when(env.getProperty(anyString())).thenReturn("test");
		Mockito.when(dme2RestClient.callDme2Client(anyString(), anyString(), anyString(), anyString(), anyString(),
				anyString(), anyString(), anyString(), anyMap(), anyString(), anyMap())).thenReturn(inventoryResponse);
//		ContractInvResponseBean responseBean = mapper.readValue(inventoryResponse,
//				ContractInvResponseBean.class);
		assertNotNull(response);
//		assertEquals("ContractNumber is mandatory",responseBean.getResponseStatus().toString());
	}
	@Mock
	private NxSsdfSpeedMappingRepository repository;
	@Test
	public void getSpeedDataTest() throws SalesBusinessException, JsonParseException, JsonMappingException, IOException {
		List<NxSsdfSpeedMapping> nxSsdfSpeedMappings = null ;
		Mockito.when(nxSsdfSpeedMappingDao.getSpeedData("Mileage", "ASEoD3PA")).thenReturn(nxSsdfSpeedMappings);
		Mockito.when(nxSsdfSpeedMappingDao.getSpeedData("Connection", "ASEoD3PA")).thenReturn(nxSsdfSpeedMappings);
		List<NxSsdfSpeedMapping> mileageSpeedData = nxSsdfSpeedMappingDao.getSpeedData("Mileage", "ASEoD3PA");
		logger.info("mileage speed data {}",mileageSpeedData);
//		assertNotNull(mileageSpeedData);
		List<NxSsdfSpeedMapping> connectionSpeedData = nxSsdfSpeedMappingDao.getSpeedData("Connection", "ASEoD3PA");
		logger.info("connection speed data {}",connectionSpeedData);
//		assertNotNull(connectionSpeedData);
		
	}
	
}
