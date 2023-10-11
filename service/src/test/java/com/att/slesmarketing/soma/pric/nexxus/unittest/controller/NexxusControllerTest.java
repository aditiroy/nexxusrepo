package com.att.slesmarketing.soma.pric.nexxus.unittest.controller;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.validator.ValidatorResources;
//import org.apache.cxf.jaxrs.ext.multipart.Attachment;
//import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import com.att.sales.framework.controller.BaseController;
import com.att.sales.framework.controller.SpringController;
import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.model.Status;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.framework.util.ValidationUtil;
import com.att.sales.framework.validation.RequestValidator;
import com.att.sales.nexxus.admin.model.FailedEthTokenRequest;
import com.att.sales.nexxus.admin.model.FailedEthTokesResponse;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.MessageConstants;
import com.att.sales.nexxus.controller.NexxusController;
import com.att.sales.nexxus.custompricing.model.CustomPricingRequest;
import com.att.sales.nexxus.edf.model.ManageBillingPriceInvDataRequest;
import com.att.sales.nexxus.fallout.model.FalloutDetailsRequest;
import com.att.sales.nexxus.model.AccessPricingUiRequest;
import com.att.sales.nexxus.model.FetchBillDetails;
import com.att.sales.nexxus.model.FetchBillDetailsResponse;
import com.att.sales.nexxus.model.MailRequest;
import com.att.sales.nexxus.model.MailResponse;
import com.att.sales.nexxus.model.NewEnhancementRequest;
import com.att.sales.nexxus.model.NewEnhancementResponse;
import com.att.sales.nexxus.model.NexxusOutputRequest;
import com.att.sales.nexxus.model.NexxusSolActionRequest;
import com.att.sales.nexxus.model.NexxusSolActionResponse;
import com.att.sales.nexxus.model.ProductDataLoadRequest;
import com.att.sales.nexxus.model.RetrieveAdminDataRequest;
import com.att.sales.nexxus.model.SolutionLockRequest;
import com.att.sales.nexxus.model.UpdateTransactionOverrideRequest;
import com.att.sales.nexxus.model.ZipFileResponse;
import com.att.sales.nexxus.myprice.publishValidatedAddresses.model.PublishValidatedAddressesStatusRequest;
import com.att.sales.nexxus.nxPEDstatus.model.GetNxPEDStatusRequest;
import com.att.sales.nexxus.rateletter.model.RateLetterStatusRequest;
import com.att.sales.nexxus.reteriveicb.model.ActionDeterminants;
import com.att.sales.nexxus.reteriveicb.model.ContractInventoryRequestBean;
import com.att.sales.nexxus.reteriveicb.model.NexxusTestRequest;
import com.att.sales.nexxus.reteriveicb.model.NexxusTestResponse;
import com.att.sales.nexxus.reteriveicb.model.RetreiveICBPSPRequest;
import com.att.sales.nexxus.reteriveicb.model.RetreiveICBPSPResponse;
import com.att.sales.nexxus.rome.model.GetBillingChargesRequest;
import com.att.sales.nexxus.rome.model.GetOptyRequest;
import com.att.sales.nexxus.serviceValidation.model.ConfigurationDetails;
import com.att.sales.nexxus.serviceValidation.model.DesignConfiguration;
import com.att.sales.nexxus.serviceValidation.model.ServiceValidationRequest;
import com.att.sales.nexxus.serviceValidation.model.ServiceValidationResponse;
import com.att.sales.nexxus.serviceValidation.model.SiteDetails;
import com.att.sales.nexxus.transmitdesigndata.model.TransmitDesignDataRequest;
import com.att.sales.nexxus.transmitdesigndata.model.TransmitDesignDataResponse;
import com.att.sales.nexxus.userdetails.model.AddUserRequest;
import com.att.sales.nexxus.userdetails.model.CheckAccessRequest;
import com.att.sales.nexxus.userdetails.model.ConsumerDetailRequest;
import com.att.sales.nexxus.userdetails.model.UserDetailsRequest;
import com.att.sales.nexxus.userdetails.model.UserDetailsResponse;

@ExtendWith(MockitoExtension.class)
public class NexxusControllerTest {

	@InjectMocks
	NexxusController testNexxusController = Mockito.spy(NexxusController.class);
	@Mock
	Status status;
	@Mock
	ValidationUtil validationUtil;
	@Mock
	RequestValidator requestValidator;
	@Mock
	NexxusTestRequest request1;
	@Mock
	Environment env;
	//@Mock
	//MultipartBody multipart;
	@Mock
	ProductDataLoadRequest datataLoadRequest;
	@InjectMocks
	ValidatorResources validatorResources = Mockito.mock(ValidatorResources.class);
	@Spy
	BaseServiceImpl baseServiceImpl;
	@Mock
	AccessPricingUiRequest request3;
	@Mock
	ManageBillingPriceInvDataRequest inventoryrequest;
	@Mock
	RetreiveICBPSPRequest request2;
	@Mock
	GetOptyRequest request;
	@Mock
	Map<String, Object> requestMap;
	@Mock
	NexxusOutputRequest request4;
	@Mock
	MailRequest request5;

	@Mock
	UserDetailsRequest userDetail;
	
	@Mock
	GetBillingChargesRequest getBillingChargesRequest;

	@BeforeEach
	public void setUp() {
		String[] typeOfData = { "Design", "Data" };
		String[] activity = { "aaa", "bbb" };
		ValidationUtil validationUtil = new ValidationUtil();
		ApplicationContext appContext = Mockito.mock(ApplicationContext.class);
		Mockito.when(baseServiceImpl.getValidationUtil()).thenReturn(validationUtil);
		Mockito.when(testNexxusController.getValidationUtil()).thenReturn(validationUtil);
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
		map.put(ServiceMetaData.SERVICE_FILTER, "price");
		map.put("typeOfData", typeOfData);
		map.put("activity", activity);

		ServiceMetaData.add(map);

	}

	/*@Test
	public void putUploadASENexxusFileTestPositive() throws SalesBusinessException {
		Mockito.doReturn(Mockito.mock(ServiceResponse.class)).when((BaseController) testNexxusController)
				.execute(Mockito.anyObject());
		testNexxusController.putUploadASENexxusFile(multipart);
	}

	@Test
	public void putUploadASENexxusFileTestNegative() throws SalesBusinessException {
		SalesBusinessException exception = new SalesBusinessException(MessageConstants.PROCESS_ERROR_CODE);
		Mockito.doThrow(exception).when((BaseController) testNexxusController).execute(Mockito.anyObject());
		testNexxusController.putUploadASENexxusFile(multipart);
	}*/

	@Test
	public void putPutProductDataLoad() throws SalesBusinessException {
		((SpringController) Mockito.doReturn(Mockito.mock(ServiceResponse.class)).when((BaseController) testNexxusController))
				.execute(Mockito.any());
		try {

			//testNexxusController.putProductRatePlanDataLoad(multipart);
			//testNexxusController.putProductDataLoad(multipart);
			testNexxusController.retrieveSalesOrderInfo(request);
			testNexxusController.transformPricingData(request3);
			//testNexxusController.putUploadASENexxusFile(multipart);
			testNexxusController.fetchNexxusSolutionsByUserId(requestMap);
			testNexxusController.getBillingManagePriceData(inventoryrequest);
			testNexxusController.retrieveBillingCharges(getBillingChargesRequest);
			testNexxusController.getInternalTest();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			testNexxusController.retreiveICBPSP(request2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			testNexxusController.transformTestData(request1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			testNexxusController.retreiveUserDetails(userDetail);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			testNexxusController.nexxusOutputDownload(request4);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			testNexxusController.nexxusOutputZipFileDownload(request4);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			testNexxusController.retreiveUserDetails(userDetail);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			testNexxusController.mailNotification(request5);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void putPutProductDataLoadException() throws SalesBusinessException {
		SalesBusinessException exception = new SalesBusinessException(MessageConstants.PROCESS_ERROR_CODE);
		((SpringController) Mockito.doThrow(exception).when((BaseController) testNexxusController)).execute(Mockito.any());
		//testNexxusController.putProductDataLoad(multipart);
		testNexxusController.retrieveSalesOrderInfo(request);
		testNexxusController.retreiveICBPSP(request2);
		testNexxusController.transformPricingData(request3);
		testNexxusController.transformTestData(request1);
		//testNexxusController.putProductRatePlanDataLoad(multipart);
		testNexxusController.getBillingManagePriceData(inventoryrequest);
		//testNexxusController.putUploadASENexxusFile(multipart);
		testNexxusController.fetchNexxusSolutionsByUserId(requestMap);
		testNexxusController.mailNotification(request5);
		testNexxusController.nexxusOutputZipFileDownload(request4);
		testNexxusController.nexxusOutputDownload(request4);
		testNexxusController.retreiveUserDetails(userDetail);
		testNexxusController.retrieveBillingCharges(getBillingChargesRequest);
	}

	@Test
	public void testRetreiveUserDetails() throws SalesBusinessException {
		UserDetailsRequest detailsRequest = new UserDetailsRequest();
		detailsRequest.setAttuid("attuid");
		UserDetailsResponse response = new UserDetailsResponse();
		response.setLeadDesignId(12345L);
		response.setStatus(status);
		// Mockito.when(testNexxusController.execute(detailsRequest)).thenReturn(response);
		doReturn(response).when(testNexxusController).execute(detailsRequest);
		testNexxusController.retreiveUserDetails(detailsRequest);
	}

	@Test
	public void testgetInternalTest() throws SalesBusinessException {
		ServiceResponse response = new ServiceResponse();
		Status status = new Status();
		status.setCode("code");
		response.setStatus(status);

		// Mockito.when(testNexxusController.execute(detailsRequest)).thenReturn(response);
		doReturn(response).when(testNexxusController).execute();
		testNexxusController.getInternalTest();
	}

	@Test
	public void testMailNotification() throws SalesBusinessException {
		MailRequest request = new MailRequest();
		request.setNxRequestId(new Long(2l));
		MailResponse response = new MailResponse();
		response.setCoorelationId("cooreltaionId");
		response.setStatus(status);
		response.setStatus(status);

		// Mockito.when(testNexxusController.execute(detailsRequest)).thenReturn(response);
		doReturn(response).when(testNexxusController).execute(request);
		testNexxusController.mailNotification(request);
	}

	@Test
	public void testgetInternalTestException() throws SalesBusinessException {
		ServiceResponse response = new ServiceResponse();
		Status status = new Status();
		status.setCode("code");
		response.setStatus(status);
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute();
		testNexxusController.getInternalTest();
	}

	@Test
	public void testConsumerDetail() throws SalesBusinessException {

		ServiceResponse response = new ServiceResponse();
		Status status = new Status();
		status.setCode("code");
		response.setStatus(status);
		ConsumerDetailRequest consumerDetailRequest = new ConsumerDetailRequest();
		consumerDetailRequest.setActionType("actionType");
		doReturn(response).when(testNexxusController).execute(consumerDetailRequest);
		testNexxusController.consumerDetail(consumerDetailRequest);

	}

	@Test
	public void testConsumerDetailException() throws SalesBusinessException {

		ServiceResponse response = new ServiceResponse();
		Status status = new Status();
		status.setCode("code");
		response.setStatus(status);
		ConsumerDetailRequest consumerDetailRequest = new ConsumerDetailRequest();
		consumerDetailRequest.setActionType("actionType");
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute(consumerDetailRequest);
		testNexxusController.consumerDetail(consumerDetailRequest);

	}

	@Test
	public void testGetNxOutputFileId() throws SalesBusinessException {
		NexxusOutputRequest nexxusOutputRequest = new NexxusOutputRequest();
		nexxusOutputRequest.setNxOutputFileId(12345L);
		nexxusOutputRequest.setNxSolutionId(12345L);
		ServiceResponse response = new ServiceResponse();
		Status status = new Status();
		status.setCode("code");
		response.setStatus(status);
		doReturn(response).when(testNexxusController).execute(nexxusOutputRequest);
		testNexxusController.getNxOutputFileId(nexxusOutputRequest);
	}

	@Test
	public void testGetNxOutputFileIdException() throws SalesBusinessException {

		ServiceResponse response = new ServiceResponse();
		Status status = new Status();
		status.setCode("code");
		response.setStatus(status);
		NexxusOutputRequest nexxusOutputRequest = new NexxusOutputRequest();
		nexxusOutputRequest.setNxOutputFileId(12345L);
		nexxusOutputRequest.setNxSolutionId(12345L);
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute(nexxusOutputRequest);
		testNexxusController.getNxOutputFileId(nexxusOutputRequest);

	}

	@Test
	public void testTransformTestData() throws SalesBusinessException {
		NexxusTestRequest request = new NexxusTestRequest();
		request.setSolutionId(new Long(2l));
		NexxusTestResponse response = new NexxusTestResponse();
		response.setStatus(status);
		doReturn(response).when(testNexxusController).execute(request);
		testNexxusController.transformTestData(request);
	}

	@Test
	public void testRetreiveICBPSP() throws SalesBusinessException {
		RetreiveICBPSPRequest request = new RetreiveICBPSPRequest();
		ActionDeterminants action = new ActionDeterminants();
		request.setActionDeterminants(Arrays.asList(action));
		RetreiveICBPSPResponse response = new RetreiveICBPSPResponse();
		response.setStatus(status);
		doReturn(response).when(testNexxusController).execute(request);
		testNexxusController.retreiveICBPSP(request);
	}

	/*@Test
	public void testuploadNxTemplateFile() throws SalesBusinessException {
		Attachment att = new Attachment("1", "e", new Object());
		MultipartBody multipart = new MultipartBody(att);
		testNexxusController.uploadNxTemplateFile(multipart);
	}*/

	@Test
	public void testNexxusOutputDownload() throws SalesBusinessException {
		ZipFileResponse response = new ZipFileResponse();
		response.setStatus(status);
		NexxusOutputRequest request = new NexxusOutputRequest();
		request.setNxOutputAction("nxOutputAction");
		doReturn(response).when(testNexxusController).execute(request);
		testNexxusController.nexxusOutputDownload(request);
	}

	@Test
	public void testNexxusOutputZipFileDownload() throws SalesBusinessException {
		ZipFileResponse response = new ZipFileResponse();
		response.setStatus(status);
		NexxusOutputRequest request = new NexxusOutputRequest();
		request.setNxOutputAction("nxOutputAction");
		doReturn(response).when(testNexxusController).execute(request);
		testNexxusController.nexxusOutputZipFileDownload(request);
	}

	@Test
	public void testRetrieveAdminData() throws SalesBusinessException {
		RetrieveAdminDataRequest request = new RetrieveAdminDataRequest();
		request.setAction("action");
		ServiceResponse response = new ServiceResponse();
		Status status = new Status();
		status.setCode("code");
		response.setStatus(status);
		doReturn(response).when(testNexxusController).execute(request);
		testNexxusController.retrieveAdminData(request);
	}

	@Test
	public void testRetrieveAdminDataException() throws SalesBusinessException {
		RetrieveAdminDataRequest request = new RetrieveAdminDataRequest();
		request.setAction("action");
		ServiceResponse response = new ServiceResponse();
		Status status = new Status();
		status.setCode("code");
		response.setStatus(status);
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute(request);
		testNexxusController.retrieveAdminData(request);
	}

	@Test
	public void testRetrieveFalloutDetails() throws SalesBusinessException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("action");
		ServiceResponse response = new ServiceResponse();
		Status status = new Status();
		status.setCode("code");
		response.setStatus(status);
		doReturn(response).when(testNexxusController).execute(request);
		testNexxusController.nexxusRequestActions(request);
	}

	@Test
	public void testRetrieveFalloutDetailsException() throws SalesBusinessException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("action");
		ServiceResponse response = new ServiceResponse();
		Status status = new Status();
		status.setCode("code");
		response.setStatus(status);
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute(request);
		testNexxusController.nexxusRequestActions(request);
	}

	@Test
	public void testServicevalidation() throws SalesBusinessException {

		List<DesignConfiguration> designConfigurations = new ArrayList<>();
		DesignConfiguration designConfig = new DesignConfiguration();
		designConfig.setName("N1");
		designConfig.setValue("V1");
		designConfigurations.add(designConfig);

		List<ConfigurationDetails> configurationDetails = new ArrayList<>();
		ConfigurationDetails configDetails = new ConfigurationDetails();
		configDetails.setDocumentNumber("11");
		configDetails.setModelName("ASE");
		configDetails.setDesignConfiguration(designConfigurations);
		configurationDetails.add(configDetails);

		List<SiteDetails> siteDetailList = new ArrayList<>();
		SiteDetails siteDetails = new SiteDetails();
		siteDetails.setNxSiteId("1");
		siteDetails.setConfigurationDetails(configurationDetails);
		siteDetailList.add(siteDetails);

		ServiceValidationRequest request = new ServiceValidationRequest();
		request.setTransactionId(1L);
		request.setOptyId("100");
		request.setDealId(10L);
		request.setSiteDetails(siteDetailList);

		ServiceValidationResponse response = new ServiceValidationResponse();
		Status status = new Status();
		status.setCode("code");
		response.setStatus(status);

		doReturn(response).when(testNexxusController).execute(request);
		testNexxusController.serviceValidation(request);
	}

	@Test
	public void testServiceValidationException() throws SalesBusinessException {
		ServiceValidationRequest request = new ServiceValidationRequest();
		request.setTransactionId(1L);
		ServiceValidationResponse response = new ServiceValidationResponse();
		Status status = new Status();
		status.setCode("code");
		response.setStatus(status);
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute(request);
		testNexxusController.serviceValidation(request);
	}
	
	@Test
	public void testFetchAllTopProducts() throws SalesBusinessException {
		((SpringController) Mockito.doReturn(Mockito.mock(ServiceResponse.class)).when((BaseController) testNexxusController))
				.execute(Mockito.any());
		Map<String, Object> requestMap = new HashMap<String, Object>();
		testNexxusController.fetchAllTopProducts(requestMap);
	}
	
	@Test
	public void testFetchAllTopProductsException() throws SalesBusinessException {
		Map<String, Object> requestMap = new HashMap<String, Object>();
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute(requestMap);
		testNexxusController.fetchAllTopProducts(requestMap);
	}
	
	@Test
	public void testAdminUpdateProductInfo() throws SalesBusinessException {
		((SpringController) Mockito.doReturn(Mockito.mock(ServiceResponse.class)).when((BaseController) testNexxusController))
				.execute(Mockito.any());
		Map<String, Object> requestMap = new HashMap<String, Object>();
		testNexxusController.adminUpdateProductInfo(requestMap);
	}
	
	@Test
	public void testAdminUpdateProductInfoException() throws SalesBusinessException {
		Map<String, Object> requestMap = new HashMap<String, Object>();
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute(requestMap);
		testNexxusController.adminUpdateProductInfo(requestMap);
	}
	
	@Test
	public void testTransmitDesignData() throws SalesBusinessException {
		((SpringController) Mockito.doReturn(Mockito.mock(TransmitDesignDataResponse.class)).when((BaseController) testNexxusController))
				.execute(Mockito.any());
		TransmitDesignDataRequest request = new TransmitDesignDataRequest();
		testNexxusController.transmitDesignData(request);
	}
	
	@Test
	public void testTransmitDesignDataException() throws SalesBusinessException {
		TransmitDesignDataRequest request = new TransmitDesignDataRequest();
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute(request);
		testNexxusController.transmitDesignData(request);
	}
	
	@Test
	public void testPrepareAndSendMailForPEDRequest() throws SalesBusinessException {
		((SpringController) Mockito.doReturn(Mockito.mock(ServiceResponse.class)).when((BaseController) testNexxusController))
				.execute(Mockito.any());
		com.att.sales.nexxus.accesspricing.model.TransmitDesignDataResponse request = new com.att.sales.nexxus.accesspricing.model.TransmitDesignDataResponse();
		testNexxusController.prepareAndSendMailForPEDRequest(request);
	}
	
	@Test
	public void testPrepareAndSendMailForPEDRequestException() throws SalesBusinessException {
		com.att.sales.nexxus.accesspricing.model.TransmitDesignDataResponse request = new com.att.sales.nexxus.accesspricing.model.TransmitDesignDataResponse();
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute(request);
		testNexxusController.prepareAndSendMailForPEDRequest(request);
	}
	
	@Test
	public void testGetnXPEDStatus() throws SalesBusinessException {
		((SpringController) Mockito.doReturn(Mockito.mock(ServiceResponse.class)).when((BaseController) testNexxusController))
				.execute(Mockito.any());
		GetNxPEDStatusRequest request = new GetNxPEDStatusRequest();
		testNexxusController.getnXPEDStatus(request);
	}
	
	@Test
	public void testGetnXPEDStatusException() throws SalesBusinessException {
		GetNxPEDStatusRequest request = new GetNxPEDStatusRequest();
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute(request);
		testNexxusController.getnXPEDStatus(request);
	}
	
	@Test
	public void testRateLetterStatus() throws SalesBusinessException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		RateLetterStatusRequest request = new RateLetterStatusRequest();
		((SpringController) Mockito.doReturn(Mockito.mock(ServiceResponse.class)).when((BaseController) testNexxusController))
				.execute(Mockito.any());
		Status status = new Status();
		Mockito.when(requestValidator.validatePojo(request)).thenReturn(status);
		testNexxusController.rateLetterStatus(request);
	}
	
	@Test
	public void testRateLetterStatusException() throws SalesBusinessException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		RateLetterStatusRequest request = new RateLetterStatusRequest();
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute(request);
		Status status = new Status();
		Mockito.when(requestValidator.validatePojo(request)).thenReturn(status);
		testNexxusController.rateLetterStatus(request);
	}
	
	@Test
	public void testPublishValidatedAddressesStatus() throws SalesBusinessException {
		PublishValidatedAddressesStatusRequest request = new PublishValidatedAddressesStatusRequest();
		((SpringController) Mockito.doReturn(Mockito.mock(ServiceResponse.class)).when((BaseController) testNexxusController))
				.execute(Mockito.any());
		
		testNexxusController.publishValidatedAddressesStatus(request);
	}
	
	@Test
	public void testPublishValidatedAddressesStatusException() throws SalesBusinessException {
		PublishValidatedAddressesStatusRequest request = new PublishValidatedAddressesStatusRequest();
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute(request);
		testNexxusController.publishValidatedAddressesStatus(request);
	}
	
	@Test
	public void testGetTransaction() throws SalesBusinessException {
		((SpringController) Mockito.doReturn(Mockito.mock(ServiceResponse.class)).when((BaseController) testNexxusController))
				.execute(Mockito.any());
		testNexxusController.getTransaction("10101010");
	}
	
	@Test
	public void testGetTransactionException() throws SalesBusinessException {
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute("10101010");
		testNexxusController.getTransaction("10101010");
	}
	
	@Test
	public void testGetTransactionLine() throws SalesBusinessException {
		((SpringController) Mockito.doReturn(Mockito.mock(ServiceResponse.class)).when((BaseController) testNexxusController))
				.execute(Mockito.any());
		testNexxusController.getTransactionLine("10101010");
	}
	
	@Test
	public void testGetTransactionLineException() throws SalesBusinessException {
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute("10101010");
		testNexxusController.getTransactionLine("10101010");
	}
	
	@Test
	public void testUpdateTransactionPricingRequest() throws SalesBusinessException {
		Map<String, Object> designMap = new HashMap<String, Object>();
		((SpringController) Mockito.doReturn(Mockito.mock(ServiceResponse.class)).when((BaseController) testNexxusController))
				.execute(Mockito.any());
		testNexxusController.updateTransactionPricingRequest(designMap);
	}
	
	@Test
	public void testUpdateTransactionPricingRequestException() throws SalesBusinessException {
		Map<String, Object> designMap = new HashMap<String, Object>();
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute(designMap);
		testNexxusController.updateTransactionPricingRequest(designMap);
	}
	
	@Test
	public void testUpdateTransactionSiteUpload() throws SalesBusinessException {
		((SpringController) Mockito.doReturn(Mockito.mock(ServiceResponse.class)).when((BaseController) testNexxusController))
				.execute(Mockito.any());
		testNexxusController.updateTransactionSiteUpload("10101010");
	}
	
	@Test
	public void testUpdateTransactionSiteUploadException() throws SalesBusinessException {
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute("10101010");
		testNexxusController.updateTransactionSiteUpload("10101010");
	}
	
	@Test
	public void testRemoveTransactionLine() throws SalesBusinessException {
		Map<String, Object> designMap = new HashMap<String, Object>();
		((SpringController) Mockito.doReturn(Mockito.mock(ServiceResponse.class)).when((BaseController) testNexxusController))
				.execute(Mockito.any());
		testNexxusController.removeTransactionLine(designMap);
	}
	
	@Test
	public void testRemoveTransactionLineException() throws SalesBusinessException {
		Map<String, Object> designMap = new HashMap<String, Object>();
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute(designMap);
		testNexxusController.removeTransactionLine(designMap);
	}
	
	@Test
	public void testUpdateTransactionOverride() throws SalesBusinessException {
		UpdateTransactionOverrideRequest request = new UpdateTransactionOverrideRequest();
		((SpringController) Mockito.doReturn(Mockito.mock(ServiceResponse.class)).when((BaseController) testNexxusController))
				.execute(Mockito.any());
		testNexxusController.updateTransactionOverride(request);
	}
	
	@Test
	public void testUpdateTransactionOverrideException() throws SalesBusinessException {
		UpdateTransactionOverrideRequest request = new UpdateTransactionOverrideRequest();
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute(request);
		testNexxusController.updateTransactionOverride(request);
	}
	
	@Test
	public void testCopyTransaction() throws SalesBusinessException {
		Map<String, Object> designMap = new HashMap<String, Object>();
		((SpringController) Mockito.doReturn(Mockito.mock(ServiceResponse.class)).when((BaseController) testNexxusController))
				.execute(Mockito.anyChar());
		testNexxusController.copyTransaction(designMap);
	}
	
	@Test
	public void testCopyTransactionException() throws SalesBusinessException {
		Map<String, Object> designMap = new HashMap<String, Object>();
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute(designMap);
		testNexxusController.copyTransaction(designMap);
	}
	
	@Test
	public void testGetCustomPricing() throws SalesBusinessException {
		CustomPricingRequest request = new CustomPricingRequest();
		((SpringController) Mockito.doReturn(Mockito.mock(ServiceResponse.class)).when((BaseController) testNexxusController))
				.execute(Mockito.any());
		testNexxusController.getCustomPricing(request);
	}
	
	@Test
	public void testGetCustomPricingException() throws SalesBusinessException {
		CustomPricingRequest request = new CustomPricingRequest();
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute(request);
		testNexxusController.getCustomPricing(request);
	}
	
	@Test
	public void testGetCustomPricingSalesOne() throws SalesBusinessException {
		CustomPricingRequest request = new CustomPricingRequest();
		((SpringController) Mockito.doReturn(Mockito.mock(ServiceResponse.class)).when((BaseController) testNexxusController))
				.execute(Mockito.any());
		testNexxusController.getCustomPricingSalesOne(request);
	}
	
	@Test
	public void testGetCustomPricingSalesOneException() throws SalesBusinessException {
		CustomPricingRequest request = new CustomPricingRequest();
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute(request);
		testNexxusController.getCustomPricingSalesOne(request);
	}
	
	@Test
	public void testUpdateTransactionQualifyService() throws SalesBusinessException {
		Map<String, Object> designMap = new HashMap<String, Object>();
		((SpringController) Mockito.doReturn(Mockito.mock(ServiceResponse.class)).when((BaseController) testNexxusController))
				.execute(Mockito.any());
		testNexxusController.updateTransactionQualifyService(designMap);
	}
	
	@Test
	public void testUpdateTransactionQSException() throws SalesBusinessException {
		Map<String, Object> designMap = new HashMap<String, Object>();
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute(designMap);
		testNexxusController.updateTransactionQualifyService(designMap);
	}
	
	@Test
	public void testUpdateTransactionPriceScore() throws SalesBusinessException {
		Map<String, Object> designMap = new HashMap<String, Object>();
		((SpringController) Mockito.doReturn(Mockito.mock(ServiceResponse.class)).when((BaseController) testNexxusController))
				.execute(Mockito.any());
		testNexxusController.updateTransactionPriceScore(designMap);
	}
	
	@Test
	public void testUpdateTransactionPSException() throws SalesBusinessException {
		Map<String, Object> designMap = new HashMap<String, Object>();
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute(designMap);
		testNexxusController.updateTransactionPriceScore(designMap);
	}
	
	@Test
	public void testUpdateTransactionSubmitToApproval() throws SalesBusinessException {
		Map<String, Object> designMap = new HashMap<String, Object>();
		((SpringController) Mockito.doReturn(Mockito.mock(ServiceResponse.class)).when((BaseController) testNexxusController))
				.execute(Mockito.any());
		testNexxusController.updateTransactionSubmitToApproval(designMap);
	}
	
	@Test
	public void testUpdateTransactionSAException() throws SalesBusinessException {
		Map<String, Object> designMap = new HashMap<String, Object>();
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute(designMap);
		testNexxusController.updateTransactionSubmitToApproval(designMap);
	}
	
	@Test
	public void testGenerateRateLetter() throws SalesBusinessException {
		Map<String, Object> designMap = new HashMap<String, Object>();
		((SpringController) Mockito.doReturn(Mockito.mock(ServiceResponse.class)).when((BaseController) testNexxusController))
				.execute(Mockito.any());
		testNexxusController.generateRateLetter(designMap);
	}
	
	@Test
	public void testGenerateRateLetterException() throws SalesBusinessException {
		Map<String, Object> designMap = new HashMap<String, Object>();
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute(designMap);
		testNexxusController.generateRateLetter(designMap);
	}
	
	@Test
	public void testAseodReqRates() throws SalesBusinessException {
		((SpringController) Mockito.doReturn(Mockito.mock(ServiceResponse.class)).when((BaseController) testNexxusController))
				.execute(Mockito.any());
		testNexxusController.aseodReqRates("10101010");
	}
	
	@Test
	public void testAseodReqRatesException() throws SalesBusinessException {
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute("10101010");
		testNexxusController.aseodReqRates("10101010");
	}
	
	@Test
	public void testGetEncodedBinaryFile() throws SalesBusinessException {
		NexxusOutputRequest request = new NexxusOutputRequest();
		((SpringController) Mockito.doReturn(Mockito.mock(ServiceResponse.class)).when((BaseController) testNexxusController))
				.execute(Mockito.any());
		testNexxusController.getEncodedBinaryFile(request);
	}
	
	@Test
	public void testGetEncodedBinaryFileException() throws SalesBusinessException {
		NexxusOutputRequest request = new NexxusOutputRequest();
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute(request);
		testNexxusController.getEncodedBinaryFile(request);
	}
	
	@Test
	public void testRefreshCache() throws SalesBusinessException {
		ServiceResponse response = new ServiceResponse();
		Status status = new Status();
		status.setCode("code");
		response.setStatus(status);
		doReturn(response).when(testNexxusController).execute();
		testNexxusController.refreshCache();
	}
	
	@Test
	public void testRefreshCacheException() throws SalesBusinessException {
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute();
		testNexxusController.refreshCache();
	}
	
	@Test
	public void testCreateTransaction() throws SalesBusinessException {
		ServiceResponse response = new ServiceResponse();
		Status status = new Status();
		status.setCode("code");
		response.setStatus(status);
		doReturn(response).when(testNexxusController).execute();
		testNexxusController.createTransaction();
	}
	
	@Test
	public void testCreateTransactionException() throws SalesBusinessException {
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute();
		testNexxusController.createTransaction();
	}
	
	/*@Test
	public void testUploadNexxusDataFile() throws SalesBusinessException {
		Mockito.doReturn(Mockito.mock(DataUploadResponse.class)).when(testNexxusController)
				.execute(Mockito.anyObject());
		testNexxusController.uploadNexxusDataFile(multipart);
	}

	@Test
	public void testUploadNexxusDataFileException() throws SalesBusinessException {
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute(Mockito.anyObject());
		testNexxusController.uploadNexxusDataFile(multipart);
	}
	
	@Test
	public void testBulkUploadEthTokens() throws SalesBusinessException {
		Mockito.doReturn(Mockito.mock(BulkUploadEthTokenResponse.class)).when(testNexxusController)
				.execute(Mockito.anyObject());
		testNexxusController.bulkUploadEthTokens(multipart);
	}

	@Test
	public void testBulkUploadEthTokensException() throws SalesBusinessException {
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute(Mockito.anyObject());
		testNexxusController.uploadNexxusDataFile(multipart);
	}*/
	
	@Test
	public void testDownloadFailedTokenFile() throws SalesBusinessException {
		FailedEthTokenRequest request = new FailedEthTokenRequest();
		Mockito.doReturn(Mockito.mock(FailedEthTokesResponse.class)).when(testNexxusController)
				.execute(Mockito.any());
		testNexxusController.downloadFailedTokenFile(request);
	}

	@Test
	public void testDownloadFailedTokenFileException() throws SalesBusinessException {
		FailedEthTokenRequest request = new FailedEthTokenRequest();
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute(request);
		testNexxusController.downloadFailedTokenFile(request);
	}
	
	/*@Test
	public void testManBulkUploadToEDF() throws SalesBusinessException {
		Mockito.doReturn(Mockito.mock(EdfbulkUploadResponse.class)).when(testNexxusController)
				.execute(Mockito.anyObject());
		Attachment att = new Attachment("1", "e", new Object());
		MultipartBody multipart = new MultipartBody(att);
		testNexxusController.manBulkUploadToEDF(multipart);
	}

	@Test
	public void testManBulkUploadToEDFException() throws SalesBusinessException {
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute(Mockito.anyObject());
		testNexxusController.manBulkUploadToEDF(multipart);
	}*/
	@Test
	public void getContractInventoryControllerTest() throws SalesBusinessException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		ContractInventoryRequestBean request = new ContractInventoryRequestBean();
		Mockito.mock(ServiceMetaData.class);
		((SpringController) Mockito.doReturn(Mockito.mock(ServiceResponse.class)).when((BaseController) testNexxusController))
				.execute(Mockito.any());
		Status status = new Status();
		Mockito.when(requestValidator.validatePojo(request)).thenReturn(status);
		testNexxusController.getContractInventory(request);
	}	
	@Test
	public void getContractInventoryControllerTestException() throws SalesBusinessException {
		ContractInventoryRequestBean request = new ContractInventoryRequestBean();
			SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute(Mockito.any());
		testNexxusController.getContractInventory(request);
	}
	@Test
	public void testAddUser() throws SalesBusinessException {
		AddUserRequest addUserRequest=new AddUserRequest();
		((SpringController) Mockito.doReturn(Mockito.mock(ServiceResponse.class)).when((BaseController) testNexxusController))
		.execute(Mockito.any());
		testNexxusController.addUser(addUserRequest);
	}
	@Test
	public void testAddUserException() throws SalesBusinessException {
		AddUserRequest addUserRequest=new AddUserRequest();
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute(Mockito.any());
		testNexxusController.addUser(addUserRequest);
	}
	@Test
	public void testCheckAccess() throws SalesBusinessException {
		CheckAccessRequest checkAccessRequest=new CheckAccessRequest();
		((SpringController) Mockito.doReturn(Mockito.mock(ServiceResponse.class)).when((BaseController) testNexxusController))
		.execute(Mockito.any());
		testNexxusController.checkAccess(checkAccessRequest);
	}
	@Test
	public void testCheckAccessException() throws SalesBusinessException {
		CheckAccessRequest checkAccessRequest=new CheckAccessRequest();
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute(Mockito.any());
		testNexxusController.checkAccess(checkAccessRequest);
	}
	
	/*@Test
	public void testBulkUploadInrData() throws SalesBusinessException {
		Mockito.doReturn(Mockito.mock(BulkUploadInrUpdateResponse.class)).when(testNexxusController)
				.execute(Mockito.anyObject());
		testNexxusController.bulkUploadInrData(multipart);
	}

	@Test
	public void testBulkUploadInrDataException() throws SalesBusinessException {
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute(Mockito.anyObject());
		testNexxusController.bulkUploadInrData(multipart);
	}*/
	public void testfetchNewEnhancements() throws SalesBusinessException {
		NewEnhancementResponse response = new NewEnhancementResponse();
		NewEnhancementRequest request = new NewEnhancementRequest();
		response.setEnhancements("{ \"name\":\"John\", \"age\":30, \"car\":null }");
		((SpringController) Mockito.doReturn(response).when((BaseController) testNexxusController)) 
		.execute(Mockito.any());
		testNexxusController.fetchNewEnhancements(request);
	}
	@Test
	public void testfetchNewEnhancementsException() throws SalesBusinessException {
		NewEnhancementRequest request = new NewEnhancementRequest();
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute(Mockito.any());
		testNexxusController.fetchNewEnhancements(request);
	}

	@Test
	public void testnexxusSolutionAction() throws SalesBusinessException {
		NexxusSolActionResponse response = new NexxusSolActionResponse();
		NexxusSolActionRequest request = new NexxusSolActionRequest();
		response.setNxSolutionId(7878L);
		((SpringController) Mockito.doReturn(response).when((BaseController) testNexxusController)) 
		.execute(Mockito.any());
		testNexxusController.nexxusSolutionAction(request);
	}
	@Test
	public void testnexxusSolutionActionException() throws SalesBusinessException {
		NexxusSolActionRequest request = new NexxusSolActionRequest();
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute(Mockito.any());
		testNexxusController.nexxusSolutionAction(request);
	}
	@Test
	public void testfetchBillDetails() throws SalesBusinessException {
		FetchBillDetailsResponse response = new FetchBillDetailsResponse();
		FetchBillDetails list = new FetchBillDetails();
		List<FetchBillDetails> fetchBillDetailsList = new ArrayList<>();
		list.setEdfInput("December 2021");
		list.setEdfInput("November 2021");
		fetchBillDetailsList.add(list);
		response.setBeginBillMonths(fetchBillDetailsList);
		response.setBillMonths(fetchBillDetailsList);
		doReturn(response).when(testNexxusController).execute();
		testNexxusController.fetchBillDetails();
	}
	
	@Test
	public void testfetchBillDetailsException() throws SalesBusinessException {
		SalesBusinessException exception = new SalesBusinessException();
		doThrow(exception).when(testNexxusController).execute();
		testNexxusController.fetchBillDetails();
	}
	
	@Test
	public void testSolutionLockCheck() throws SalesBusinessException {
		ServiceResponse response = new ServiceResponse();
		SolutionLockRequest request = new SolutionLockRequest();
		request.setAttuid("user1");
		request.setIsLocked("Y");
		request.setNxSolutionId("12345");
		Status status = new Status();
		status.setCode("code");
		response.setStatus(status);
		((SpringController) Mockito.doReturn(response).when((BaseController) testNexxusController)) 
		.execute(Mockito.any());
		testNexxusController.solutionLockCheck(request);
	}
	
	@Test
	public void testSolutionLockCheckException() throws SalesBusinessException {
		SalesBusinessException exception = new SalesBusinessException();
		SolutionLockRequest request = new SolutionLockRequest();
		request.setAttuid("user1");
		request.setIsLocked("Y");
		request.setNxSolutionId("12345");
		((SpringController) doThrow(exception).when((BaseController) testNexxusController)).execute(Mockito.any());
		testNexxusController.solutionLockCheck(request);
	}

}
