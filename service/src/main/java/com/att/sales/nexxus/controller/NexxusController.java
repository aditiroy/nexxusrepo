package com.att.sales.nexxus.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.att.sales.framework.controller.SpringController;
import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.model.Status;
import com.att.sales.framework.model.constants.HttpErrorCodes;
import com.att.sales.framework.validation.RequestValidator;
import com.att.sales.nexxus.admin.model.BulkUploadEthTokenRequest;
import com.att.sales.nexxus.admin.model.FailedEthTokenRequest;
import com.att.sales.nexxus.chatbot.model.ChatBotRequest;
import com.att.sales.nexxus.common.StringConstants;
import com.att.sales.nexxus.custompricing.model.CustomPricingRequest;
import com.att.sales.nexxus.datarouter.model.DataRouterRequest;
import com.att.sales.nexxus.edf.model.ManageBillingPriceInvDataRequest;
import com.att.sales.nexxus.edfbulkupload.EdfManBulkUploadRequest;
import com.att.sales.nexxus.fallout.model.FalloutDetailsRequest;
import com.att.sales.nexxus.inr.InrJsonServiceRequest;
import com.att.sales.nexxus.inr.InvPriceJsonRequest;
import com.att.sales.nexxus.model.APUiResponse;
import com.att.sales.nexxus.model.AccessPricingUiRequest;
import com.att.sales.nexxus.model.BulkUploadInrUpdateRequest;
import com.att.sales.nexxus.model.FileUploadRequest;
import com.att.sales.nexxus.model.MailRequest;
import com.att.sales.nexxus.model.NewEnhancementRequest;
import com.att.sales.nexxus.model.NexxusOutputRequest;
import com.att.sales.nexxus.model.NexxusSolActionRequest;
import com.att.sales.nexxus.model.RetrieveAdminDataRequest;
import com.att.sales.nexxus.model.SendMailRequest;
import com.att.sales.nexxus.model.SolutionLockRequest;
import com.att.sales.nexxus.model.SubmitFeedbackRequest;
import com.att.sales.nexxus.model.SyncMyPriceLegacyCoDataRequest;
import com.att.sales.nexxus.model.UpdateTransactionOverrideRequest;
import com.att.sales.nexxus.model.ZipFileResponse;
import com.att.sales.nexxus.myprice.publishValidatedAddresses.model.PublishValidatedAddressesStatusRequest;
import com.att.sales.nexxus.nxPEDstatus.model.GetNxPEDStatusRequest;
import com.att.sales.nexxus.rateletter.model.RateLetterStatusRequest;
import com.att.sales.nexxus.reteriveicb.model.ContractInventoryRequestBean;
import com.att.sales.nexxus.reteriveicb.model.NexxusTestRequest;
import com.att.sales.nexxus.reteriveicb.model.RetreiveICBPSPRequest;
import com.att.sales.nexxus.rome.model.GetBillingChargesRequest;
import com.att.sales.nexxus.rome.model.GetOptyRequest;
import com.att.sales.nexxus.serviceValidation.model.ServiceValidationRequest;
import com.att.sales.nexxus.template.model.NxTemplateUploadRequest;
import com.att.sales.nexxus.transmitdesigndata.model.SolutionCostRequest;
import com.att.sales.nexxus.transmitdesigndata.model.TransmitDesignDataRequest;
import com.att.sales.nexxus.userdetails.model.AddUserRequest;
import com.att.sales.nexxus.userdetails.model.CheckAccessRequest;
import com.att.sales.nexxus.userdetails.model.ConsumerDetailRequest;
import com.att.sales.nexxus.userdetails.model.UserDetailsRequest;
import com.att.sales.nexxus.util.NumUtil;
import com.att.sales.nexxus.validator.TransmitDesignDataValidator;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * The Class NexxusController.
 */
@Api
@Path("/nexxus")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({MediaType.APPLICATION_JSON})
public class NexxusController extends SpringController {

	
	@Autowired
	private RequestValidator requestValidator;

	/** The status. */
	private Status status;
	
	/** The log. */
	private static Logger log = LoggerFactory.getLogger(NexxusController.class);
	

	/* (non-Javadoc)
	 * @see com.att.sales.nexxus.controller.INexxusController#transformTestData(com.att.sales.nexxus.reteriveicb.model.NexxusTestRequest)
	 */
	@POST
	@Path("/transformTestData")
	@Consumes({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "transform request data", notes = "transform request data")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })

	public Response transformTestData(NexxusTestRequest request) {
		// response = new NexxusTestResponse();
		
			log.info("Entered saveLogicalChannel() method");
			ResponseEntity<ServiceResponse> response = this.execute(request);
			log.info("Successfully completed saveLogicalChannel() method");
			return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}

	
	@POST
	@Path("/retreiveICBPSP")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "retreiveICBPSP request data", notes = "retreiveICBPSP request data")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response retreiveICBPSP(RetreiveICBPSPRequest request) throws SalesBusinessException {
		// RetreiveICBPSPResponse response = new RetreiveICBPSPResponse();// what is response

		log.info("Entered retreiveICBPSP() method");
		ResponseEntity<ServiceResponse> response = this.execute(request);
		log.info("Successfully completed retreiveICBPSP() method");

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}

		
	@PUT
	@Path("/putUploadASENexxusFile")
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Consumes data load feed file", notes = "Returns a JSON object having Acknowledgement. "
			+ "This API will send either Design Rules Data Management or"
			+ "Network Rules Data Management mS depending on offer", response = Response.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error"),
			@ApiResponse(code = 400, message = "Service failed"),
			@ApiResponse(code = 01014, message = "Service not defined"),
			@ApiResponse(code = 01016, message = "Processing error"),
			@ApiResponse(code = 00000, message = "Request completed successfully")

	})
	public Response putUploadASENexxusFile(String  request) {
		log.info("Inside fileupload method in NexxusController");
		//ServiceResponse response = new ServiceResponse();
		ResponseEntity<ServiceResponse> response = this.execute(request);
		
		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
		
	}

	
	@PUT
	@Path("/productDataLoad")
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Consumes data load feed file", notes = "Returns a JSON object having Acknowledgement. "
			+ "This API will send either Design Rules Data Management or"
			+ "Network Rules Data Management mS depending on offer", response = Response.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error"),
			@ApiResponse(code = 400, message = "Service failed"),
			@ApiResponse(code = 01014, message = "Service not defined"),
			@ApiResponse(code = 01016, message = "Processing error"),
			@ApiResponse(code = 00000, message = "Request completed successfully")

	})
	public Response putProductDataLoad(String request) {
		log.info("Inside fileupload method in NexxusController");
		// ServiceResponse response = new ServiceResponse();
		ResponseEntity<ServiceResponse> response = this.execute(request);

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();

	}

	
	@POST
	@Path("/getAccessPrice")
	@Consumes({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "transform request data", notes = "transform request data", response = APUiResponse.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
	@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response transformPricingData(AccessPricingUiRequest request) {
		// ServiceResponse response = new ServiceResponse();
		log.info("Entered transformPricingData() method");
		ResponseEntity<ServiceResponse> response = this.execute(request);
		log.info("Successfully completed transformPricingData() method");

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}

	
	@POST
	@Path("/getInventory")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "getBillingManagePriceData", notes = "getBillingManagePriceData")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response getBillingManagePriceData(ManageBillingPriceInvDataRequest inventoryrequest) {
		// ServiceResponse response = new ServiceResponse();

		log.info("Entered getBillingManagePriceData() method");
		if (ServiceMetaData.getRequestMetaData().get(ServiceMetaData.XTraceId) == null) {
			String key = null;
			if(inventoryrequest.getNxSolutionId() != null) {
				key = inventoryrequest.getNxSolutionId();
			}else {
				key = inventoryrequest.getCpniApprover();
			}
			Map<String, Object> requestParams = new HashMap<>();
			String conversationId = String.format("NEXXUSBILLINGMANAGEPRICEDATA%s", key);
			requestParams.put(ServiceMetaData.XCONVERSATIONID, conversationId);
			ServiceMetaData.add(requestParams);
		}
		ResponseEntity<ServiceResponse> response = this.execute(inventoryrequest);
		log.info("Successfully completed getBillingManagePriceData() method");

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}

	
	@POST
	@Path("/retrieveOptyInfo")
	@Consumes({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "retrieveSalesOrderInfo operation", notes = "retrieves OptyInfo")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service Not Available"),
	@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response retrieveSalesOrderInfo(GetOptyRequest request) {
		// ServiceResponse response = new ServiceResponse();
		ResponseEntity<ServiceResponse> response = this.execute(request);

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}

	
	@PUT
	@Path("/ratePlanDataLoad")
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Consumes data load feed file", notes = "Returns a JSON object having Acknowledgement. "
			+ "This API will send either Design Rules Data Management or"
			+ "Network Rules Data Management mS depending on offer", response = Response.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error"),
			@ApiResponse(code = 400, message = "Service failed"),
			@ApiResponse(code = 01014, message = "Service not defined"),
			@ApiResponse(code = 01016, message = "Processing error"),
			@ApiResponse(code = 00000, message = "Request completed successfully")

	})
	public Response putProductRatePlanDataLoad(String request) {
		log.info("inside putProductDataLoad() method");
		log.info("Inside fileupload method in NexxusController");
		// ServiceResponse response = new ServiceResponse();
		ResponseEntity<ServiceResponse> response = this.execute(request);

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	@POST
	@Path("/fetchNexxusSolutionsByUserId")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Fetches the Nexxus solutions for the user id received in the request", notes = "Fetches the Nexxus solutions for the user id received in the request ")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error"),
			@ApiResponse(code = 400, message = "Service failed"),
			@ApiResponse(code = 01014, message = "Service not defined"),
			@ApiResponse(code = 01016, message = "Processing error"),
			@ApiResponse(code = 00000, message = "Request completed successfully")

	})
	public  Response fetchNexxusSolutionsByUserId(Map<String, Object> requestMap) {
			log.info("Entered retrieveNexxusSolutionsByUserId() method");
			ResponseEntity<ServiceResponse> res = this.execute(requestMap);
			return Response.status(res.getStatusCode().value()).entity(res.getBody()).build();
	}
	
	@POST
	@Path("/getContractInventory")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Nexxus to call getSDNContractInventory", notes = "calling getSDNContractInvnetory to SSDF")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error"),
			@ApiResponse(code = 400, message = "Service failed"),
			@ApiResponse(code = 01014, message = "Service not defined"),
			@ApiResponse(code = 01016, message = "Processing error"),
			@ApiResponse(code = 00000, message = "Request completed successfully")

	})
	public Response getContractInventory(ContractInventoryRequestBean request) {
		//ServiceResponse response = new ServiceResponse();
		
			log.info("Entered contractInventory() method");
			if (ServiceMetaData.getRequestMetaData().get(ServiceMetaData.XTraceId) == null) {
				Map<String, Object> requestParams = new HashMap<>();
				String conversationId = String.format("NEXXUSGETCONTRACTINVENTORY%s",
						request.getContractInventoryRequest().getTransactionId());
				String traceId = String.format("%d%d", NumUtil.parseLong(request.getContractInventoryRequest().getTransactionId(), 0L),
						System.currentTimeMillis());
				requestParams.put(ServiceMetaData.XCONVERSATIONID, conversationId);
//				requestParams.put(ServiceMetaData.XTraceId, traceId);
//				requestParams.put(ServiceMetaData.XSpanId, traceId);
				ServiceMetaData.add(requestParams);
			}
			ResponseEntity<ServiceResponse> response = this.execute(request);
			log.info("Successfully completed contractInventory() method");
		
		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}

	/*
	@POST
	@Path("/fetchNexxusSolutionsByUserId")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Fetches the Nexxus solutions for the user id received in the request", notes = "Fetches the Nexxus solutions for the user id received in the request ")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error"),
			@ApiResponse(code = 400, message = "Service failed"),
			@ApiResponse(code = 01014, message = "Service not defined"),
			@ApiResponse(code = 01016, message = "Processing error"),
			@ApiResponse(code = 00000, message = "Request completed successfully")

	})
	public Response fetchNexxusSolutionsByUserId(Map<String, Object> requestMap) {
		ServiceResponse response = new ServiceResponse();
		try {
			log.info("Entered retrieveNexxusSolutionsByUserId() method");
			response = this.execute(requestMap);
			log.info("Successfully completed retrieveNexxusSolutionsByUserId() method");
		} catch (SalesBusinessException exception) {
			return handleException(response, exception);
		}
		return Response.status(response.getStatusCode().value()).entity(response).build();
	}*/


	@POST
	@Path("/downloadNexxusOutput")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Nexxus Output Download api", notes = "Fetches the Nexxus Output for the request ")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error"),
			@ApiResponse(code = 400, message = "Service failed"),
			@ApiResponse(code = 01014, message = "Service not defined"),
			@ApiResponse(code = 01016, message = "Processing error"),
			@ApiResponse(code = 00000, message = "Request completed successfully")

	})
	public Response nexxusOutputDownload(NexxusOutputRequest request) {
		// ServiceResponse response = new ServiceResponse();
		//ZipFileResponse zipFileResponse = null;

		ResponseEntity<ServiceResponse> response = this.execute(request);
		// zipFileResponse = (ZipFileResponse) response;

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	
	@POST
	@Path("/mailNotification")
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(
			value = "Send the mail notification to SalesTransformation team",
			notes = "Returns a JSON object with Status. ",
			response = APUiResponse.class
	)
	public Response mailNotification(MailRequest request) {
		// MailResponse response = new MailResponse();
		ResponseEntity<ServiceResponse> response = this.execute(request);
		// String callResponseString = this.execute(request).toString();
		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	@POST
	@Path("/datafeed")
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Consumes data load feed file", response = Response.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error"),
			@ApiResponse(code = 400, message = "Service failed"),
			@ApiResponse(code = 01014, message = "Service not defined"),
			@ApiResponse(code = 01016, message = "Processing error"),
			@ApiResponse(code = 00000, message = "Request completed successfully")

	})
	public Response datafeed(ChatBotRequest request) {
		// ElizaResponse response = new ElizaResponse();
		ResponseEntity<ServiceResponse> response = this.execute(request);

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	

	
	@POST
	@Path("/downloadZipFile")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON})
	@ApiOperation(value = "Nexxus Output Download api", notes = "Fetches the Nexxus Output for the request ")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error"),
			@ApiResponse(code = 400, message = "Service failed"),
			@ApiResponse(code = 01014, message = "Service not defined"),
			@ApiResponse(code = 01016, message = "Processing error"),
			@ApiResponse(code = 00000, message = "Request completed successfully")

	})
	public Response nexxusOutputZipFileDownload(NexxusOutputRequest request) {
		ResponseEntity<ServiceResponse> response = this.execute(request);
		ZipFileResponse zipFileResponse = null;
		try {
			response = this.execute(request);
			zipFileResponse = (ZipFileResponse) response.getBody();
		}  catch (Exception e) {
			log.error("Main exception occured during nexxusOutputZipFileDownload process", e);
		}
		return Response.ok(zipFileResponse.getFileZip(), MediaType.APPLICATION_OCTET_STREAM)
				.header(StringConstants.CONTENT_DISPOSITION,
						StringConstants.ATTACHMENT + "\"" + zipFileResponse.getZipFileName() + "\"")
				.build();
	}

	
	@GET
	@Path("/internalTest")
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "used for internal test", notes = "used for internal test", response = Response.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response getInternalTest() {
		// ServiceResponse response = new ServiceResponse();
		ResponseEntity<ServiceResponse> response = this.execute();

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}

	
	@POST
	@Path("/retreiveUserDetails")
	@Consumes({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "retreiveICBPSP request data", notes = "retreiveICBPSP request data")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })

	public Response retreiveUserDetails(UserDetailsRequest request) {
		// UserDetailsResponse response = new UserDetailsResponse();
		log.info("inside mETHOD");
		ResponseEntity<ServiceResponse> response = this.execute(request);

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}

	
	@POST
	@Path("/consumerDetail")
	@Consumes({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Nexxus Output UserDetail api", notes = "To Show the Data on UI ")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error"),
			@ApiResponse(code = 400, message = "Service failed"),
			@ApiResponse(code = 01014, message = "Service not defined"),
			@ApiResponse(code = 01016, message = "Processing error"),
			@ApiResponse(code = 00000, message = "Request completed successfully")

	})
	public Response consumerDetail(ConsumerDetailRequest request) {
		// ServiceResponse response = new ServiceResponse();
		ResponseEntity<ServiceResponse> response = this.execute(request);

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}

	
	@POST
	@Path("/getNxOutputFileId")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Nexxus Output getNxOutputFileId api", notes = "Fetches the Nexxus Output FileId ")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error"),
			@ApiResponse(code = 400, message = "Service failed"),
			@ApiResponse(code = 01014, message = "Service not defined"),
			@ApiResponse(code = 01016, message = "Processing error"),
			@ApiResponse(code = 00000, message = "Request completed successfully")

	})
	public Response getNxOutputFileId(NexxusOutputRequest request) {
		// ServiceResponse response = new ServiceResponse();
		ResponseEntity<ServiceResponse> response = this.execute(request);

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	private static final String NAME = "name";

	
	/*@POST
	@Path("/uploadNxTemplateFile")
	@Consumes({ MediaType.MULTIPART_FORM_DATA})
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "upload template file", notes = "Returns a JSON object with file load results. "
			+ "file must be unique on the server and successfully compiled", response = Response.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response uploadNxTemplateFile(MultipartBody multipart) throws SalesBusinessException {
		NxTemplateUploadRequest request = new NxTemplateUploadRequest();
		NxTemplateUploadResponse response = new NxTemplateUploadResponse();
		Status status;
		try {
			if (multipart != null) {
				for (Attachment a : multipart.getAllAttachments()) {
					ContentDisposition cd = a.getContentDisposition();
					if (cd != null && TemplateFileConstants.FILE.equalsIgnoreCase(cd.getParameter(NAME))
							&& ServiceMetaData.getRequestMetaData().get(cd.getFilename()) != null) {
						request.setInputStream(
								(InputStream) ServiceMetaData.getRequestMetaData().get(cd.getFilename()));
						request.setFileName(cd.getFilename());
					}
				}
			}
			if (ServiceMetaData.getRequestMetaData().get(TemplateFileConstants.FILE_TYPE) != null) {
				request.setFileType(
						((String[]) ServiceMetaData.getRequestMetaData().get(TemplateFileConstants.FILE_TYPE))[0]);

			}
			log.info("In uploadNxTemplateFile, Template upload request is {}", request);
			NxTemplateValidator.validateUploadRequest(request);
			response = (NxTemplateUploadResponse) this.execute(request);
		} catch (SalesBusinessException exception) {
			status = getErrorStatus(exception.getMsgCode(), HttpErrorCodes.SERVER_ERROR);
			NxTemplateUploadResponse fresponse = new NxTemplateUploadResponse();
			fresponse.setStatus(status);
			fresponse.setFileName(request.getFileName());
			return handleException(fresponse, exception);
		}
		return Response.status(response.getStatusCode().value()).entity(response).build();
	}
	***/
	

	@POST
	@Path("/retrieveAdminData")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "retrieveAdminData", notes = "Fetches admin data ")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error"),
			@ApiResponse(code = 400, message = "Service failed"),
			@ApiResponse(code = 01014, message = "Service not defined"),
			@ApiResponse(code = 01016, message = "Processing error"),
			@ApiResponse(code = 00000, message = "Request completed successfully")

	})
	public Response retrieveAdminData(RetrieveAdminDataRequest request) {
		//ServiceResponse response = new ServiceResponse();
		ResponseEntity<ServiceResponse> response = this.execute(request);
		
		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	

	@POST
	@Path("/nexxusRequestActions")
	@Consumes({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "retrieveFalloutDetails request data", notes = "retrieveFalloutDetails request data")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response nexxusRequestActions(FalloutDetailsRequest request) {
		// ServiceResponse response = new ServiceResponse();

		log.info("Entered retrieveFalloutDetails() method");
		if (ServiceMetaData.getRequestMetaData().get(ServiceMetaData.XTraceId) == null) {
			Map<String, Object> requestParams = new HashMap<>();
			String conversationId = String.format("NexxusAction_%s_%d", request.getAction(), request.getNxSolutionId());
			String traceId = String.format("%d%d%d", NumUtil.parseLong(request.getNxSolutionId(), 0L),
					NumUtil.parseLong(request.getNxReqId(), 0L), System.currentTimeMillis());
			log.info("traceId in retrieveFalloutDetails is updated to {}", traceId);
			requestParams.put(ServiceMetaData.XCONVERSATIONID, conversationId);
//			requestParams.put(ServiceMetaData.XTraceId, traceId);
//			requestParams.put(ServiceMetaData.XSpanId, traceId);
			ServiceMetaData.add(requestParams);
		}
		ResponseEntity<ServiceResponse> response = this.execute(request);
		log.info("Successfully completed retrieveFalloutDetails() method");

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	/***

	
	@POST
	@Path("/uploadNexxusDataFile")
	@Consumes({ MediaType.MULTIPART_FORM_DATA})
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "upload data file", notes = "Returns a JSON object with file load results. "
			+ "file must be unique on the server and successfully compiled", response = Response.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })

	public Response uploadNexxusDataFile(MultipartBody multipart)
			throws SalesBusinessException {
		DataUploadRequest request=new DataUploadRequest();
		DataUploadResponse response=new DataUploadResponse();
		Status status;
		try {
				if (multipart != null) {
					for (Attachment a : multipart.getAllAttachments()) {
						ContentDisposition cd = a.getContentDisposition();
						if (cd != null && TemplateFileConstants.FILE.equalsIgnoreCase(cd.getParameter(NAME))
								&& ServiceMetaData.getRequestMetaData().get(cd.getFilename()) != null) {
							request.setInputStream(
									(InputStream) ServiceMetaData.getRequestMetaData().get(cd.getFilename()));
						}
					}
				}
				
				if (ServiceMetaData.getRequestMetaData().get(DataUploadConstants.ID) != null) {
					request.setId(
							((String[]) ServiceMetaData.getRequestMetaData().get(DataUploadConstants.ID))[0]);
	
				}
				if (ServiceMetaData.getRequestMetaData().get(DataUploadConstants.USER_ID) != null) {
					request.setUserId(
							((String[]) ServiceMetaData.getRequestMetaData().get(DataUploadConstants.USER_ID))[0]);
	
				}
				if (ServiceMetaData.getRequestMetaData().get(DataUploadConstants.ACTION) != null) {
					request.setAction(
							((String[]) ServiceMetaData.getRequestMetaData().get(DataUploadConstants.ACTION))[0]);
	
				}
				if (ServiceMetaData.getRequestMetaData().get(DataUploadConstants.ACTIVITY) != null) {
					request.setActivity(
							((String[]) ServiceMetaData.getRequestMetaData().get(DataUploadConstants.ACTIVITY))[0]);
	
				}
				log.info("In uploadNexxusDataFile, Template upload request is {}", request);
				response = (DataUploadResponse) this.execute(request);
				
				
			} catch (SalesBusinessException exception) {
				status = getErrorStatus(exception.getMsgCode(), HttpErrorCodes.SERVER_ERROR);
				NxTemplateUploadResponse fresponse = new NxTemplateUploadResponse();
				fresponse.setStatus(status);
				return handleException(fresponse, exception);
			}
			return Response.status(response.getStatusCode().value()).entity(response).build();
			
	}
	***/

	@POST
	@Path("/bulkUploadEthTokens")
	@Consumes({ MediaType.APPLICATION_JSON})
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "upload ethernet token file", notes = "Returns a JSON object with file load results. "
			+ "file must be unique on the server and successfully compiled", response = Response.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response bulkUploadEthTokens(FileUploadRequest req) throws SalesBusinessException {
		BulkUploadEthTokenRequest request = new BulkUploadEthTokenRequest();
		// BulkUploadEthTokenResponse response = new BulkUploadEthTokenResponse();
		Status status;

		byte[] b = Base64.getDecoder().decode(req.getFileContent());
		InputStream is = new ByteArrayInputStream(b);
		request.setInputStream(is);
		request.setUserId(req.getUserId());
		request.setAction(req.getAction());
		request.setNxSolutionId(req.getNxSolutionId());
		log.info("In bulkUploadEthTokens, Template upload request is {}", request);
		ResponseEntity<ServiceResponse> response = this.execute(request);

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();

	}

	@POST
	@Path("/downloadFailedTokenFile")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "download failed ethernet tokens file", notes = "Returns a JSON object with file load results. "
			+ "file must be unique on the server and successfully compiled", response = Response.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })

	public Response downloadFailedTokenFile(FailedEthTokenRequest request) throws SalesBusinessException {
		// FailedEthTokesResponse response = new FailedEthTokesResponse();
		log.info("In downloadFailedTokenFile, request is {}", request);
//		ResponseEntity<ServiceResponse> response = this.execute(request);
//
//		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
		ResponseEntity<ServiceResponse> response = this.execute(request);
		ZipFileResponse zipFileResponse = null;
		try {
			response = this.execute(request);
			zipFileResponse = (ZipFileResponse) response.getBody();
		}  catch (Exception e) {
			log.error("Main exception occured during nexxusOutputZipFileDownload process", e);
		}
		return Response.ok(zipFileResponse.getFileZip(), MediaType.APPLICATION_OCTET_STREAM)
				.header(StringConstants.CONTENT_DISPOSITION,
						StringConstants.ATTACHMENT + "\"" + zipFileResponse.getZipFileName() + "\"")
				.build();

	}


	@POST
	@Path("/manBulkUploadToEDF")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "download imported man and man file", notes = "Returns a JSON object with file load results. "
			+ "file must be unique on the server and successfully compiled", response = Response.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response manBulkUploadToEDF(FileUploadRequest req) throws SalesBusinessException {
		EdfManBulkUploadRequest request = new EdfManBulkUploadRequest();
		// EdfbulkUploadResponse response = new EdfbulkUploadResponse();

		byte[] b = Base64.getDecoder().decode(req.getFileContent());
		InputStream is = new ByteArrayInputStream(b);
		request.setInputStream(is);
		request.setUserId(req.getUserId());
		request.setNxSolutionId(req.getNxSolutionId());
		log.info("In manBulkupload, Template upload request is {}", request);
		ResponseEntity<ServiceResponse> response = this.execute(request);

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	
	
	@POST
	@Path("/fetchAllTopProducts")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "fetchAllTopProducts", notes = "Fetches all top products data ")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error"),
			@ApiResponse(code = 400, message = "Service failed"),
			@ApiResponse(code = 01014, message = "Service not defined"),
			@ApiResponse(code = 01016, message = "Processing error"),
			@ApiResponse(code = 00000, message = "Request completed successfully")

	})
	public Response fetchAllTopProducts(Map<String, Object> requestMap) {
		// ServiceResponse response = new ServiceResponse();
		log.info("Entered fetchAllTopProducts() method");
		ResponseEntity<ServiceResponse> response = this.execute(requestMap);
		log.info("Successfully completed fetchAllTopProducts() method");

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}

	
	@POST
	@Path("/adminUpdateProductInfo")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "adminUpdateProductInfo", notes = "Fetches all top products data ")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error"),
			@ApiResponse(code = 400, message = "Service failed"),
			@ApiResponse(code = 01014, message = "Service not defined"),
			@ApiResponse(code = 01016, message = "Processing error"),
			@ApiResponse(code = 00000, message = "Request completed successfully")

	})
	public Response adminUpdateProductInfo(Map<String, Object> requestMap) {
		// ServiceResponse response = new ServiceResponse();
		log.info("Entered adminUpdateProductInfo() method");
		ResponseEntity<ServiceResponse> response = this.execute(requestMap);
		log.info("Successfully completed adminUpdateProductInfo() method");

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	
	@POST
	@Path("/transmitDesignData")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Nexxus transmitDesignData api", notes = "get the update DesignData from PED ")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error"),
			@ApiResponse(code = 400, message = "Service failed"),
			@ApiResponse(code = 01014, message = "Service not defined"),
			@ApiResponse(code = 01016, message = "Processing error"),
			@ApiResponse(code = 00000, message = "Request completed successfully")

	})

	public Response transmitDesignData(TransmitDesignDataRequest request) throws SalesBusinessException {
		Status status = new Status();
		// TransmitDesignDataResponse response=new TransmitDesignDataResponse();
		TransmitDesignDataValidator.validateUploadRequest(request);
		ResponseEntity<ServiceResponse> response = this.execute(request);

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}


	
	@POST
	@Path("/prepareAndSendMailForPEDRequest")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Email/Dmaap event notification", notes = "Email/Dmaap event notification")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error"),
			@ApiResponse(code = 400, message = "Service failed"),
			@ApiResponse(code = 01014, message = "Service not defined"),
			@ApiResponse(code = 01016, message = "Processing error"),
			@ApiResponse(code = 00000, message = "Request completed successfully")

	})

	public Response prepareAndSendMailForPEDRequest(
			com.att.sales.nexxus.accesspricing.model.TransmitDesignDataResponse transmitDesignDataResponse) {
		// ServiceResponse response = new ServiceResponse();
		ResponseEntity<ServiceResponse> response = this.execute(transmitDesignDataResponse);

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	@POST
	@Path("/getnXPEDStatus")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "get NX PED Status", notes = "get NX PED Status")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error"),
			@ApiResponse(code = 400, message = "Service failed"),
			@ApiResponse(code = 01014, message = "Service not defined"),
			@ApiResponse(code = 01016, message = "Processing error"),
			@ApiResponse(code = 00000, message = "Request completed successfully")

	})
	public Response getnXPEDStatus(GetNxPEDStatusRequest request) {
		// ServiceResponse response = new ServiceResponse();
		ResponseEntity<ServiceResponse> response = this.execute(request);
		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	@POST
	@Path("/rateLetterStatus")
	@Consumes({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Rate Letter Status Request", notes = "Rate Letter Status Data")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response rateLetterStatus(RateLetterStatusRequest request) {
		//RateLetterStatusResponse response = new RateLetterStatusResponse();
		ServiceResponse response = new ServiceResponse();
		ResponseEntity<ServiceResponse> response1=null;
			log.info("Entered rateLetterStatus() method");
			try {
				status = requestValidator.validatePojo(request);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				log.error("Error Message While Request Validation at rateLetterStatus :: {}", e);
			}
		
		if (!HttpErrorCodes.STATUS_OK.toString().equalsIgnoreCase(status.getCode())) {
			log.info("Validation Check Failed");
			response.setStatus(status);
			return Response.status(javax.ws.rs.core.Response.Status.OK).entity(response).build();
		}
		
			String cpqId = request.getCpqId();
			if (ServiceMetaData.getRequestMetaData().get(ServiceMetaData.XTraceId) == null) {
				Map<String, Object> requestParams = new HashMap<>();
				String conversationId = String.format("NEXXUSRATELETTER%s", cpqId);
				Long cpq = NumUtil.parseLong(cpqId, 0L);
				String traceId = String.format("%d%d", cpq, System.currentTimeMillis());
				requestParams.put(ServiceMetaData.XCONVERSATIONID, conversationId);
//				requestParams.put(ServiceMetaData.XTraceId, traceId);
//				requestParams.put(ServiceMetaData.XSpanId, traceId);
				ServiceMetaData.add(requestParams);
			}
			response1 =  this.execute(request);
			//response1=response;
			log.info("Successfully completed rateLetterStatus() method");
		
		return Response.status(response1.getStatusCode().value()).entity(response1.getBody()).build();
	}
	
	@POST
	@Path("/publishValidatedAddressesStatus")
	@Consumes({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Publish Validated Addresses Request", notes = "Publish Validated Addresses Status Data")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response publishValidatedAddressesStatus(PublishValidatedAddressesStatusRequest request) {

		// ServiceResponse response = new ServiceResponse();
		log.info("Entered publishValidatedAddressesStatus() method");
		ResponseEntity<ServiceResponse> response = this.execute(request);
		log.info("Exiting publishValidatedAddressesStatus() method");

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	@POST
	@Path("/serviceValidation")
	@Consumes({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Service Validation", notes = "Service Validation")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response serviceValidation(ServiceValidationRequest request) throws SalesBusinessException{
		//ServiceResponse response = new ServiceResponse();
		
			log.info("Entered serviceValidation() method");
			Long transactionId = request.getTransactionId();
			if (ServiceMetaData.getRequestMetaData().get(ServiceMetaData.XTraceId) == null) {
				Map<String, Object> requestParams = new HashMap<>();
				String conversationId = String.format("NEXXUSSERVICEVALIDATION%d", transactionId);
				String traceId = String.format("%d%d", transactionId, System.currentTimeMillis());
				requestParams.put(ServiceMetaData.XCONVERSATIONID, conversationId);
//				requestParams.put(ServiceMetaData.XTraceId, traceId);
//				requestParams.put(ServiceMetaData.XSpanId, traceId);
				ServiceMetaData.add(requestParams);
			}
			ResponseEntity<ServiceResponse> response = this.execute(request);
			log.info("Successfully completed serviceValidation() method");
		
		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	@POST
	@Path("/createTransaction")
	@Consumes({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Create Transaction", notes = "Create Transaction")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })

	public Response createTransaction() throws SalesBusinessException {
		// ServiceResponse response = new ServiceResponse();
		log.info("Entered createTransaction() method");
		ResponseEntity<ServiceResponse> response = this.execute();
		log.info("Successfully completed createTransaction() method");

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	@POST
	@Path("/getTransaction")
	@Consumes({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Get Transaction", notes = "Get Transaction")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response getTransaction(String transactionId) throws SalesBusinessException {
		// ServiceResponse response = new ServiceResponse();
		log.info("Entered getTransaction() method");
		ResponseEntity<ServiceResponse> response = this.execute(transactionId);
		log.info("Successfully completed getTransaction() method");

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	@POST
	@Path("/getTransactionLine")
	@Consumes({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Get Transaction Line", notes = "Get Transaction Line")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })

	public Response getTransactionLine(String transactionId) throws SalesBusinessException {
		// ServiceResponse response = new ServiceResponse();
		log.info("Entered getTransactionLine() method");
		ResponseEntity<ServiceResponse> response = this.execute(transactionId);
		log.info("Successfully completed getTransactionLine() method");

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	@POST
	@Path("/updateTransactionPricingRequest")
	@Consumes({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Update Transaction Pricing Request", notes = "Update Transaction Pricing Request")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response updateTransactionPricingRequest(Map<String, Object> designMap) throws SalesBusinessException {
		// ServiceResponse response = new ServiceResponse();
		log.info("Entered updateTransactionPricingRequest() method");
		ResponseEntity<ServiceResponse> response = this.execute(designMap);
		log.info("Successfully completed updateTransactionPricingRequest() method");

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	@POST
	@Path("/updateTransactionSiteUpload")
	@Consumes({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Update Transaction Site Upload", notes = "Update Transaction Site Upload")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })

	public Response updateTransactionSiteUpload(String transactionId) throws SalesBusinessException {
		// ServiceResponse response = new ServiceResponse();
		log.info("Entered updateTransactionSiteUpload() method");
		ResponseEntity<ServiceResponse> response = this.execute(transactionId);
		log.info("Successfully completed updateTransactionSiteUpload() method");

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	@POST
	@Path("/removeTransactionLine")
	@Consumes({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Remove Transaction Line", notes = "Remove Transaction Line")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response removeTransactionLine(Map<String, Object> designMap) throws SalesBusinessException {
		// ServiceResponse response = new ServiceResponse();
		log.info("Entered removeTransactionLine() method");
		ResponseEntity<ServiceResponse> response = this.execute(designMap);
		log.info("Successfully completed removeTransactionLine() method");

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	@POST
	@Path("/updateTransactionOverride")
	@Consumes({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Update Transaction Override", notes = "Update Transaction Override")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })

	public Response updateTransactionOverride(UpdateTransactionOverrideRequest request) throws SalesBusinessException {
		// ServiceResponse response = new ServiceResponse();
		log.info("Entered updateTransactionOverride() method");
		ResponseEntity<ServiceResponse> response = this.execute(request);
		log.info("Successfully completed updateTransactionOverride() method");

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	@POST
	@Path("/copyTransaction")
	@Consumes({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Copy Transaction", notes = "Copy Transaction")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response copyTransaction(Map<String, Object> copyTransactionRequest) throws SalesBusinessException {
		// ServiceResponse response = new ServiceResponse();
		log.info("Entered copyTransaction() method");
		ResponseEntity<ServiceResponse> response = this.execute(copyTransactionRequest);
		log.info("Successfully completed copyTransaction() method");

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	@POST
	@Path("/getCustomPricing")
	@Consumes({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "get custom pricing", notes = "get custom pricing to get the rateLetter")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response getCustomPricing(CustomPricingRequest request) throws SalesBusinessException {
		// ServiceResponse response = new ServiceResponse();
		log.info("Entered getCustomPricing() method");
		ResponseEntity<ServiceResponse> response = this.execute(request);
		log.info("Successfully completed getCustomPricing() method");

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	@POST
	@Path("/getCustomPricingSalesOne")
	@Consumes({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "get custom pricing", notes = "get custom pricing to get the rateLetter")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response getCustomPricingSalesOne(CustomPricingRequest request) throws SalesBusinessException {
		// ServiceResponse response = new ServiceResponse();
		log.info("Entered getCustomPricingSalesOne() method");
		ResponseEntity<ServiceResponse> response = this.execute(request);
		log.info("Successfully completed getCustomPricingSalesOne() method");

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	@POST
	@Path("/updateTransactionQualifyService")
	@Consumes({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Update Transaction Qualify Service", notes = "Update Transaction Qualify Service")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response updateTransactionQualifyService(Map<String, Object> updateTransQualifyServiceRequest)
			throws SalesBusinessException {
		// ServiceResponse response = new ServiceResponse();
		log.info("Entered updateTransactionQualifyService() method");
		ResponseEntity<ServiceResponse> response = this.execute(updateTransQualifyServiceRequest);
		log.info("Successfully completed updateTransactionQualifyService() method");

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	@POST
	@Path("/updateTransactionPriceScore")
	@Consumes({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Update Transaction Price Score", notes = "Update Transaction Price Score")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response updateTransactionPriceScore(Map<String, Object> updateTransPriceScoreRequest)
			throws SalesBusinessException {
		// ServiceResponse response = new ServiceResponse();

		log.info("Entered updateTransactionPriceScore() method");
		ResponseEntity<ServiceResponse> response = this.execute(updateTransPriceScoreRequest);
		log.info("Successfully completed updateTransactionPriceScore() method");

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	@POST
	@Path("/updateTransactionSubmitToApproval")
	@Consumes({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Update Transaction Submit to Approval", notes = "Update Transaction Submit to Approval")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })

	public Response updateTransactionSubmitToApproval(Map<String, Object> updateTransSubmitToApprovalRequest)
			throws SalesBusinessException {
		// ServiceResponse response = new ServiceResponse();

		log.info("Entered updateTransactionSubmitToApproval() method");
		ResponseEntity<ServiceResponse> response = this.execute(updateTransSubmitToApprovalRequest);
		log.info("Successfully completed updateTransactionSubmitToApproval() method");

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}

	@POST
	@Path("/generateRateLetter")
	@Consumes({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Generate Rate Letter", notes = "Generate Rate Letter")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })

	public Response generateRateLetter(Map<String, Object> generateRateLetterRequest) throws SalesBusinessException {
		// ServiceResponse response = new ServiceResponse();
		log.info("Entered generateRateLetter() method");
		ResponseEntity<ServiceResponse> response = this.execute(generateRateLetterRequest);
		log.info("Successfully completed generateRateLetter() method");

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	@POST
	@Path("/aseodReqRates")
	@Consumes({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "ASEoD req rates", notes = "ASEoD req rates")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response aseodReqRates(String transactionId) throws SalesBusinessException {
		// ServiceResponse response = new ServiceResponse();
		log.info("Entered aseodReqRates() method");
		ResponseEntity<ServiceResponse> response = this.execute(transactionId);
		log.info("Successfully completed aseodReqRates() method");

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	@POST
	@Path("/getEncodedBinaryFile")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Nexxus encode Binary file Download api", notes = "Fetches the Nexxus Output for the request")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error"),
			@ApiResponse(code = 400, message = "Service failed"),
			@ApiResponse(code = 01014, message = "Service not defined"),
			@ApiResponse(code = 01016, message = "Processing error"),
			@ApiResponse(code = 00000, message = "Request completed successfully")

	})
	public Response getEncodedBinaryFile(NexxusOutputRequest request) {
//		 ServiceResponse response = new ServiceResponse();
		ResponseEntity<ServiceResponse> response = this.execute(request);

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	
	}
	
	@GET
	@Path("/refreshCache")
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "used for refreshCache test", notes = "used for refreshCache test", response = Response.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response refreshCache() {
		// ServiceResponse response = new ServiceResponse();
		ResponseEntity<ServiceResponse> response = this.execute();

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	

	

	@POST
	@Path("/addUser")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Adding New User to Nexxus", notes = "Adding New User to Nexxus")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response addUser(AddUserRequest addUserRequest) throws SalesBusinessException {
		// ServiceResponse response = new ServiceResponse();
		log.info("Entered addUser() method");
		ResponseEntity<ServiceResponse> response = this.execute(addUserRequest);
		log.info("Successfully completed addUser() method");

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	@POST
	@Path("/checkAccess")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Checking User Access to Nexxus", notes = "Checking User Access to Nexxus")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response checkAccess(CheckAccessRequest checkAccessRequest) throws SalesBusinessException {
		// ServiceResponse response = new ServiceResponse();

		log.info("Entered checkAccess() method");
		ResponseEntity<ServiceResponse> response = this.execute(checkAccessRequest);
		log.info("Successfully completed checkAccess() method");

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	@PUT
	@Path("/nexxusSolutionAction")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Archive Solution to Nexxus", notes = "Archive Solution to Nexxus")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime Error") })
	public Response nexxusSolutionAction(NexxusSolActionRequest request) throws SalesBusinessException {
		// NexxusSolActionResponse response = new NexxusSolActionResponse();
		log.info("Entered nexxusSolutionAction() method");
		ResponseEntity<ServiceResponse> response = this.execute(request);
		log.info("Successfully completed nexxusSolutionAction() method");

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
		
	@POST
	@Path("/submitFeedback")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "submitFeedback to Nexxus", notes = "submitFeedback to Nexxus")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response submitFeedback(SubmitFeedbackRequest request) throws SalesBusinessException {
		// ServiceResponse response = new ServiceResponse();
		log.info("Entered submitFeedback() method");
		ResponseEntity<ServiceResponse> response = this.execute(request);
		log.info("Successfully completed submitFeedback() method");

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	@POST
	@Path("/bulkUploadInrData")
	@Consumes({ MediaType.APPLICATION_JSON})
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "upload inr data file", notes = "Returns a JSON object with file load results. "
			+ "file must be unique on the server and successfully compiled", response = Response.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response bulkUploadInrData(FileUploadRequest req) throws SalesBusinessException {
		BulkUploadInrUpdateRequest request = new BulkUploadInrUpdateRequest();
		// BulkUploadInrUpdateResponse response = new BulkUploadInrUpdateResponse();
		Status status;
		byte[] b = Base64.getDecoder().decode(req.getFileContent());
		InputStream is = new ByteArrayInputStream(b);
		request.setInputStream(is);
		request.setActionPerformedBy(req.getUserId());
		request.setAction(req.getAction());
		request.setNxSolutionId(req.getNxSolutionId());
		request.setProduct(req.getProduct());
		ResponseEntity<ServiceResponse> response = this.execute(request);

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	
	@POST
	@Path("/fetchNewEnhancements")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Fetching new enhancements", notes = "Fetching new enhancements")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response fetchNewEnhancements(NewEnhancementRequest request) {
		// NewEnhancementResponse response = new NewEnhancementResponse();
		ResponseEntity<ServiceResponse> response = this.execute(request);

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	@GET
	@Path("/fetchBillDetails")
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Fetch new bill details", notes = "Fetch new bill details")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response fetchBillDetails() {
		// FetchBillDetailsResponse response = new FetchBillDetailsResponse();
		ResponseEntity<ServiceResponse> response = this.execute();

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	@POST
	@Path("/uploadMyPriceLegacyCoData")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Upload myPrice data for legacy_co table", notes = "Returns a JSON object with file load results", response = Response.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response uploadMyPriceLegacyCoData(FileUploadRequest req) throws SalesBusinessException {
		log.info("Entered uploadMyPriceLegacyCoData() method");
		NxTemplateUploadRequest request = new NxTemplateUploadRequest();
		// ServiceResponse response = new ServiceResponse();
		byte[] b = Base64.getDecoder().decode(req.getFileContent());
		InputStream is = new ByteArrayInputStream(b);
		request.setInputStream(is);
		log.info("uploadMyPriceLegacyCoData receiving file: {}",
				org.apache.commons.lang3.StringUtils.normalizeSpace(req.getFileName()));
		request.setFileName(req.getFileName());
		ResponseEntity<ServiceResponse> response = this.execute(request);
		log.info("Successfully completed uploadMyPriceLegacyCoData method");

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}

	@POST
	@Path("/syncMyPriceLegacyCoData")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "sync nexxus and myPrice legacy_co table", notes = "Returns a JSON object with table sync results", response = Response.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })

	public Response syncMyPriceLegacyCoData(SyncMyPriceLegacyCoDataRequest request) throws SalesBusinessException {
		// ServiceResponse response = new ServiceResponse();
		log.info("Entered syncMyPriceLegacyCoData() method");
		ResponseEntity<ServiceResponse> response = this.execute(request);
		log.info("Successfully completed syncMyPriceLegacyCoData() method");

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	@POST
	@Path("/solutionLockCheck")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Solution Locking API", notes = "Lock the NX Solution")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response solutionLockCheck(SolutionLockRequest request) throws SalesBusinessException {
		// ServiceResponse response = new ServiceResponse();
		log.info("Entered solutionLockCheck() method");
		ResponseEntity<ServiceResponse> response = this.execute(request);
		log.info("Successfully completed solutionLockCheck() method");

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	@POST
	@Path("/retrieveBillingCharges")
	@Consumes({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "retrieveBillingCharges operation", notes = "retrieves BillingCharg.getBody()es")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service Not Available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response retrieveBillingCharges(GetBillingChargesRequest request) {
		// ServiceResponse response = new ServiceResponse();
		log.info("Entered retrieveBillingCharges() method");
		ResponseEntity<ServiceResponse> response = this.execute(request);
		log.info("Successfully completed retrieveBillingCharges() method");

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}

	@POST
	@Path("/usrpDesign")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "query usrp design data", notes = "mereg with price json to form new inventory json")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response usrpDesign(InrJsonServiceRequest request) throws SalesBusinessException {
		// ServiceResponse response = new ServiceResponse();
		log.info("Entered usrpDesign() method");
		ResponseEntity<ServiceResponse> response = this.execute(request);
		log.info("Successfully completed usrpDesign() method");

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}

	@POST
	@Path("/sendMail")
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(
			value = "Send the mail notification for Datalake process",
			notes = "Returns a JSON object with Status. ",
			response = APUiResponse.class
	)
	public Response sendMail(SendMailRequest request) {
		// MailResponse response = new MailResponse();
		ResponseEntity<ServiceResponse> response = this.execute(request);
		// String callResponseString = this.execute(request).toString();

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	//Ruchita
		@POST
		@Path("/copyAction")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
	public Response copyAction(FalloutDetailsRequest request) {
		// FalloutDetailsResponse response = new FalloutDetailsResponse();
		ResponseEntity<ServiceResponse> response = this.execute(request);
		// String callResponseString = this.execute(request).toString();

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
		
		@POST
		@Path("/submitToMyprice")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
	public Response submitToMyprice(FalloutDetailsRequest request) {
		// ServiceResponse response = new ServiceResponse();
		log.info("Entered retrieveFalloutDetails() method");
		if (ServiceMetaData.getRequestMetaData().get(ServiceMetaData.XTraceId) == null) {
			Map<String, Object> requestParams = new HashMap<>();
			String conversationId = String.format("NexxusAction_%s_%d", request.getAction(), request.getNxSolutionId());
			String traceId = String.format("%d%d%d", NumUtil.parseLong(request.getNxSolutionId(), 0L),
					NumUtil.parseLong(request.getNxReqId(), 0L), System.currentTimeMillis());
			log.info("traceId in retrieveFalloutDetails is updated to {}", traceId);
			requestParams.put(ServiceMetaData.XCONVERSATIONID, conversationId);
//			requestParams.put(ServiceMetaData.XTraceId, traceId);
//			requestParams.put(ServiceMetaData.XSpanId, traceId);
			ServiceMetaData.add(requestParams);
		}
		ResponseEntity<ServiceResponse> response = this.execute(request);
		log.info("Successfully completed retrieveFalloutDetails() method");

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
		
		@POST
		@Path("/retriggerRequest")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public Response retriggerRequest(FalloutDetailsRequest request) {
		// FalloutDetailsResponse response = new FalloutDetailsResponse();
		ResponseEntity<ServiceResponse> response = this.execute(request);
		// String callResponseString = this.execute(request).toString();
		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
		
		@POST
		@Path("/retrieveBillingInventory")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		@ApiOperation(value = "retrieveBillingInventory operation", notes = "retrieves retrieveBillingInventory")
		@ApiResponses(value = { @ApiResponse(code = 404, message = "Service Not Available"),
				@ApiResponse(code = 500, message = "Unexpected Runtime error") })
		public Response retrieveBillingInventory(GetBillingChargesRequest request) {
		// ServiceResponse response = new ServiceResponse();
		log.info("Entered retrieveBillingInventory() method");
		ResponseEntity<ServiceResponse> response = this.execute(request);
		log.info("Successfully completed retrieveBillingInventory() method");

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}

		
		@POST
		@Path("/invPriceJson")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		@ApiOperation(value = "query inventory data to create price json", notes = "query inventory data to create price json")
		@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
				@ApiResponse(code = 500, message = "Unexpected Runtime error") })
		public Response invPriceJson(InvPriceJsonRequest request) throws SalesBusinessException {
		// ServiceResponse response = new ServiceResponse();
		log.info("Entered invPriceJson() method");
		ResponseEntity<ServiceResponse> response = this.execute(request);
		log.info("Successfully completed invPriceJson() method");

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}

		@POST
		@Path("/retrieveBillingProductDetails")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		@ApiOperation(value = "query usrp to to retrieve service type", notes = "query usrp to to retrieve service type")
		@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
				@ApiResponse(code = 500, message = "Unexpected Runtime error") })
		public Response getProductdetails(InvPriceJsonRequest request) throws SalesBusinessException {
		// ServiceResponse response = new ServiceResponse();
		log.info("Entered retrieveBillingProductDetails() method");
		ResponseEntity<ServiceResponse> response = this.execute(request);
		log.info("Successfully completed retrieveBillingProductDetails from USRP () method");

		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
			
	
	@POST
	@Path("/solutionCostIndicator")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Nexxus solutionCostIndicator api", notes = "get the solutionIndicator from PED ")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error"),
			@ApiResponse(code = 400, message = "Service failed"),
			@ApiResponse(code = 01014, message = "Service not defined"),
			@ApiResponse(code = 01016, message = "Processing error"),
			@ApiResponse(code = 00000, message = "Request completed successfully")})
	public Response solutionCostIndicator(SolutionCostRequest request) throws SalesBusinessException{
		log.info("solutionCostIndicator() method");
		if (ServiceMetaData.getRequestMetaData().get(ServiceMetaData.XTraceId) == null) {
			Map<String, Object> requestParams = new HashMap<>();
			String conversationId = String.format("NEXXUSSOLNCOSTIND%d", request.getSolutionId());
			String traceId = String.format("%d%d", NumUtil.parseLong(request.getSolutionId(), 0L),
					System.currentTimeMillis());
			log.info("traceId in solutionCostIndicator is updated to {}", traceId);
			requestParams.put(ServiceMetaData.XCONVERSATIONID, conversationId);
			ServiceMetaData.add(requestParams);
		}
		ResponseEntity<ServiceResponse> response = this.execute(request);
		log.info("Successfully completed solutionCostIndicator() method");
		return Response.status(response.getStatusCode().value()).entity(response.getBody()).build();
	}
	
	@POST
	@Path("/publishDataRouter")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Nexxus DataRouter Api", notes = "publishing the Data Router ")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error"),
			@ApiResponse(code = 400, message = "Service failed"),
			@ApiResponse(code = 01014, message = "Service not defined"),
			@ApiResponse(code = 01016, message = "Processing error"),
			@ApiResponse(code = 00000, message = "Request completed successfully")})
	public Response publishDataRouter(DataRouterRequest request) throws SalesBusinessException{
		log.info("publishDataRouter() method");
		ResponseEntity<ServiceResponse> response = this.execute(request);
		log.info("Successfully published DataRouter() method");
		return Response.status(javax.ws.rs.core.Response.Status.OK).entity(response.getBody()).build();
	}
}
