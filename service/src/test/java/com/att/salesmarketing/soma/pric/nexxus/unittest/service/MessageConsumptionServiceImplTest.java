package com.att.salesmarketing.soma.pric.nexxus.unittest.service;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import com.att.sales.exampledomainobject.model.EDFMRResponse;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dmaap.mr.util.DmaapMRSubscriberImpl;
import com.att.sales.nexxus.p8.P8Service;
import com.att.sales.nexxus.service.InrProcessingService;
import com.att.sales.nexxus.service.MailServiceImpl;
import com.att.sales.nexxus.service.MessageConsumptionServiceImpl;
import com.att.sales.nexxus.service.MessageCosumptionService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class MessageConsumptionServiceImplTest {

	@InjectMocks
	MessageConsumptionServiceImpl messageConsumptionServiceImpl;
	@Mock
	MessageCosumptionService service;
	@Mock
	Environment env;
    @Mock
    ServiceMetaData metadeta;
	@Mock
	private ObjectMapper mapper;
	@Mock
	MailServiceImpl mailService;

	@Mock
	DmaapMRSubscriberImpl dmaapMRSubscriberService;

	@Mock
	private NxRequestDetailsRepository nxRequestDetailsRepository;

	@Mock
	private P8Service p8Service;

	@Mock
	private InrProcessingService inrProcessingService;

	@BeforeEach
	public void setUp() {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put(CommonConstants.OFFER, "AVPN");
		map.put(ServiceMetaData.VERSION, "v1");
		map.put(ServiceMetaData.METHOD, "POST");
		map.put(ServiceMetaData.URI, "/domain/productrules");
		map.put(CommonConstants.TYPEOFRULE, null);
		map.put(CommonConstants.CLIENT, "001");
		map.put(CommonConstants.REGION, "US");
		// map.put(CommonConstants.FULLDUMPYN, "Y");
		map.put(CommonConstants.DELTAID, "002");
		map.put(CommonConstants.CORRELATIONID, "003");
		map.put(CommonConstants.CALLBACKURL, "/url");
		map.put(CommonConstants.FILENAME, "PSOCandCascaded.xml");
		map.put(ServiceMetaData.REST_REQUEST_START_TIME, System.currentTimeMillis());

		ServiceMetaData.add(map);

	}

	@Test
	public void testMessageConsumptionServiceImpl() throws IOException, InterruptedException, Exception {
		List<String> messages = new ArrayList<>();
		messages.add("{ \r\n" + "	\"User_id\": \"FPPUser\", \r\n" + "	\"Application\": \"FPP\", \r\n"
				+ "	\"PGM\": \"INVNTRY\", \r\n" + "	\"Request_id\": \"48420820180213171508\", \r\n"
				+ "	\"Outputfile_name\": \"Rohit_Testing_61386020170905195223_006913404.xml\", \r\n"
				+ "	\"Start_run_time\": \"05-SEP-17 12.53.48.000000000 PM\", \r\n"
				+ "	\"End_run_time\": \"05-SEP-17 12.59.48.000000000 PM\", \r\n" + "	\"Message\": \"SUCCESS\" \r\n"
				+ "}");
		messages.add("edfTest");
		String message = "{ \r\n" + "	\"User_id\": \"FPPUser\", \r\n" + "	\"Application\": \"FPP\", \r\n"
				+ "	\"PGM\": \"INVNTRY\", \r\n" + "	\"Request_id\": \"48420820180213171508\", \r\n"
				+ "	\"Outputfile_name\": \"Rohit_Testing_61386020170905195223_006913404.xml\", \r\n"
				+ "	\"Start_run_time\": \"05-SEP-17 12.53.48.000000000 PM\", \r\n"
				+ "	\"End_run_time\": \"05-SEP-17 12.59.48.000000000 PM\", \r\n" + "	\"Message\": \"SUCCESS\" \r\n"
				+ "}";
		EDFMRResponse edfMRResponse = new EDFMRResponse();
		edfMRResponse.setRequestId("48420820180213171508");
		edfMRResponse.setMessage("Please check old file : ");
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		nxRequestDetails.setEdfAckId("48420820180213171508");
		Mockito.when(dmaapMRSubscriberService.retrieveMessage(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(messages);
		Mockito.when(mapper.readValue(message, EDFMRResponse.class)).thenReturn(edfMRResponse);
		Mockito.when(nxRequestDetailsRepository.findByEdfAckId(Mockito.anyString())).thenReturn(nxRequestDetails);
		doNothing().when(p8Service).lookupDocumentInP8(Mockito.anyString());
		when(inrProcessingService.createInrNexusOutput(Mockito.any(), Mockito.any())).thenReturn(null);
		messageConsumptionServiceImpl.getMessage();

	}

	@Test
	public void testMessageConsumptionServiceImplIf() throws IOException, InterruptedException, Exception {
		List<String> messages = new ArrayList<>();
		messages.add("{ \r\n" + "	\"User_id\": \"FPPUser\", \r\n" + "	\"Application\": \"FPP\", \r\n"
				+ "	\"PGM\": \"INVNTRY\", \r\n" + "	\"Request_id\": \"48420820180213171508\", \r\n"
				+ "	\"Outputfile_name\": \"Rohit_Testing_61386020170905195223_006913404.xml\", \r\n"
				+ "	\"Start_run_time\": \"05-SEP-17 12.53.48.000000000 PM\", \r\n"
				+ "	\"End_run_time\": \"05-SEP-17 12.59.48.000000000 PM\", \r\n" + "	\"Message\": \"SUCCESS\" \r\n"
				+ "}");
		messages.add("edfTest");
		String message = "{ \r\n" + "	\"User_id\": \"FPPUser\", \r\n" + "	\"Application\": \"FPP\", \r\n"
				+ "	\"PGM\": \"INVNTRY\", \r\n" + "	\"Request_id\": \"48420820180213171508\", \r\n"
				+ "	\"Outputfile_name\": \"Rohit_Testing_61386020170905195223_006913404.xml\", \r\n"
				+ "	\"Start_run_time\": \"05-SEP-17 12.53.48.000000000 PM\", \r\n"
				+ "	\"End_run_time\": \"05-SEP-17 12.59.48.000000000 PM\", \r\n" + "	\"Message\": \"SUCCESS\" \r\n"
				+ "}";
		EDFMRResponse edfMRResponse = new EDFMRResponse();
		edfMRResponse.setRequestId("48420820180213171508");
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		nxRequestDetails.setEdfAckId("48420820180213171508");
		nxRequestDetails.setNxReqId(new Long(2));
		Mockito.when(dmaapMRSubscriberService.retrieveMessage(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(messages);
		Mockito.when(mapper.readValue(message, EDFMRResponse.class)).thenReturn(edfMRResponse);
		Mockito.when(nxRequestDetailsRepository.findByEdfAckId(Mockito.anyString())).thenReturn(nxRequestDetails);
		doNothing().when(p8Service).lookupDocumentInP8(Mockito.anyString());
		when(inrProcessingService.createInrNexusOutput(Mockito.any(), Mockito.any())).thenReturn(null);
		messageConsumptionServiceImpl.getMessage();

	}
	
	@Test
	public void testgetFileName() throws IOException, InterruptedException, Exception {
		List<String> messages = new ArrayList<>();
		messages.add("{ \r\n" + "	\"User_id\": \"FPPUser\", \r\n" + "	\"Application\": \"FPP\", \r\n"
				+ "	\"PGM\": \"INVNTRY\", \r\n" + "	\"Request_id\": \"48420820180213171508\", \r\n"
				+ "	\"Outputfile_name\": \"Rohit_Testing_61386020170905195223_006913404.xml\", \r\n"
				+ "	\"Start_run_time\": \"05-SEP-17 12.53.48.000000000 PM\", \r\n"
				+ "	\"End_run_time\": \"05-SEP-17 12.59.48.000000000 PM\", \r\n" + "	\"Message\": \"SUCCESS\" \r\n"
				+ "}");
		messages.add("edfTest");
		String message = "{ \r\n" + "	\"User_id\": \"FPPUser\", \r\n" + "	\"Application\": \"FPP\", \r\n"
				+ "	\"PGM\": \"INVNTRY\", \r\n" + "	\"Request_id\": \"48420820180213171508\", \r\n"
				+ "	\"Outputfile_name\": \"Rohit_Testing_61386020170905195223_006913404.xml\", \r\n"
				+ "	\"Start_run_time\": \"05-SEP-17 12.53.48.000000000 PM\", \r\n"
				+ "	\"End_run_time\": \"05-SEP-17 12.59.48.000000000 PM\", \r\n" + "	\"Message\": \"SUCCESS\" \r\n"
				+ "}";
		EDFMRResponse edfMRResponse = new EDFMRResponse();
		edfMRResponse.setMessage("message");
		edfMRResponse.setRequestId("48420820180213171508");
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		nxRequestDetails.setEdfAckId("48420820180213171508");
		nxRequestDetails.setNxReqId(new Long(2));
		Mockito.when(dmaapMRSubscriberService.retrieveMessage(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(messages);
		Mockito.when(mapper.readValue(message, EDFMRResponse.class)).thenReturn(edfMRResponse);
		Mockito.when(nxRequestDetailsRepository.findByEdfAckId(Mockito.anyString())).thenReturn(nxRequestDetails);
		doNothing().when(p8Service).lookupDocumentInP8(Mockito.anyString());
		when(inrProcessingService.createInrNexusOutput(Mockito.any(), Mockito.any())).thenReturn(null);
		messageConsumptionServiceImpl.getMessage();

	}
	
	
}
