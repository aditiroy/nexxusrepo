/*
 * package com.att.sales.nexxus.controller;
 * 
 * import java.util.Map; import javax.ws.rs.Consumes; import javax.ws.rs.GET;
 * import javax.ws.rs.POST; import javax.ws.rs.PUT; import javax.ws.rs.Path;
 * import javax.ws.rs.Produces; import javax.ws.rs.core.MediaType; import
 * javax.ws.rs.core.Response; import
 * org.apache.cxf.jaxrs.ext.multipart.MultipartBody; import
 * org.springframework.web.bind.annotation.RequestBody; import
 * org.springframework.web.bind.annotation.RequestParam; import
 * com.att.sales.framework.exception.SalesBusinessException; import
 * com.att.sales.nexxus.admin.model.FailedEthTokenRequest; import
 * com.att.sales.nexxus.chatbot.model.ChatBotRequest; import
 * com.att.sales.nexxus.custompricing.model.CustomPricingRequest; import
 * com.att.sales.nexxus.edf.model.ManageBillingPriceInvDataRequest; import
 * com.att.sales.nexxus.fallout.model.FalloutDetailsRequest; import
 * com.att.sales.nexxus.model.APUiResponse; import
 * com.att.sales.nexxus.model.AccessPricingUiRequest; import
 * com.att.sales.nexxus.model.MailRequest; import
 * com.att.sales.nexxus.model.NewEnhancementRequest; import
 * com.att.sales.nexxus.model.NexxusOutputRequest; import
 * com.att.sales.nexxus.model.NexxusSolActionRequest; import
 * com.att.sales.nexxus.model.RetrieveAdminDataRequest; import
 * com.att.sales.nexxus.model.SubmitFeedbackRequest; import
 * com.att.sales.nexxus.model.SyncMyPriceLegacyCoDataRequest; import
 * com.att.sales.nexxus.model.UpdateTransactionOverrideRequest; import
 * com.att.sales.nexxus.myprice.publishValidatedAddresses.model.
 * PublishValidatedAddressesStatusRequest; import
 * com.att.sales.nexxus.nxPEDstatus.model.GetNxPEDStatusRequest; import
 * com.att.sales.nexxus.rateletter.model.RateLetterStatusRequest; import
 * com.att.sales.nexxus.reteriveicb.model.ContractInventoryRequestBean; import
 * com.att.sales.nexxus.reteriveicb.model.NexxusTestRequest; import
 * com.att.sales.nexxus.reteriveicb.model.RetreiveICBPSPRequest; import
 * com.att.sales.nexxus.rome.model.GetOptyRequest; import
 * com.att.sales.nexxus.serviceValidation.model.ServiceValidationRequest; import
 * com.att.sales.nexxus.transmitdesigndata.model.TransmitDesignDataRequest;
 * import com.att.sales.nexxus.userdetails.model.AddUserRequest; import
 * com.att.sales.nexxus.userdetails.model.CheckAccessRequest; import
 * com.att.sales.nexxus.userdetails.model.ConsumerDetailRequest; import
 * com.att.sales.nexxus.userdetails.model.UserDetailsRequest; import
 * io.swagger.annotations.Api; import io.swagger.annotations.ApiOperation;
 * import io.swagger.annotations.ApiResponse; import
 * io.swagger.annotations.ApiResponses;
 * 
 *//**
	 * The Interface INexxusController.
	 */
/*
 * @Api
 * 
 * @Produces({ MediaType.APPLICATION_JSON })
 * 
 * @Path("/nexxus") public interface INexxusController {
 * 
 *//**
package com.att.sales.nexxus.controller;

import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.admin.model.FailedEthTokenRequest;
import com.att.sales.nexxus.chatbot.model.ChatBotRequest;
import com.att.sales.nexxus.custompricing.model.CustomPricingRequest;
import com.att.sales.nexxus.edf.model.ManageBillingPriceInvDataRequest;
import com.att.sales.nexxus.fallout.model.FalloutDetailsRequest;
import com.att.sales.nexxus.inr.InrJsonServiceRequest;
import com.att.sales.nexxus.model.APUiResponse;
import com.att.sales.nexxus.model.AccessPricingUiRequest;
import com.att.sales.nexxus.model.MailRequest;
import com.att.sales.nexxus.model.NewEnhancementRequest;
import com.att.sales.nexxus.model.NexxusOutputRequest;
import com.att.sales.nexxus.model.NexxusSolActionRequest;
import com.att.sales.nexxus.model.RetrieveAdminDataRequest;
import com.att.sales.nexxus.model.SubmitFeedbackRequest;
import com.att.sales.nexxus.model.SolutionLockRequest;
import com.att.sales.nexxus.model.SyncMyPriceLegacyCoDataRequest;
import com.att.sales.nexxus.model.UpdateTransactionOverrideRequest;
import com.att.sales.nexxus.myprice.publishValidatedAddresses.model.PublishValidatedAddressesStatusRequest;
import com.att.sales.nexxus.nxPEDstatus.model.GetNxPEDStatusRequest;
import com.att.sales.nexxus.rateletter.model.RateLetterStatusRequest;
import com.att.sales.nexxus.reteriveicb.model.ContractInventoryRequestBean;
import com.att.sales.nexxus.reteriveicb.model.NexxusTestRequest;
import com.att.sales.nexxus.reteriveicb.model.RetreiveICBPSPRequest;
import com.att.sales.nexxus.rome.model.GetBillingChargesRequest;
import com.att.sales.nexxus.rome.model.GetOptyRequest;
import com.att.sales.nexxus.serviceValidation.model.ServiceValidationRequest;
import com.att.sales.nexxus.transmitdesigndata.model.TransmitDesignDataRequest;
import com.att.sales.nexxus.userdetails.model.AddUserRequest;
import com.att.sales.nexxus.userdetails.model.CheckAccessRequest;
import com.att.sales.nexxus.userdetails.model.ConsumerDetailRequest;
import com.att.sales.nexxus.userdetails.model.UserDetailsRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * The Interface INexxusController.
 */

/*
 * @POST
 * 
 * @Path("/transformTestData")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "transform request data", notes =
 * "transform request data")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
 * Response transformTestData(NexxusTestRequest request);
 * 
 *//**
	 * Retreive ICBPSP.
	 *
	 * @param request the request
	 * @return the response
	 */
/*
 * @POST
 * 
 * @Path("/retreiveICBPSP")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "retreiveICBPSP request data", notes =
 * "retreiveICBPSP request data")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
 * Response retreiveICBPSP(@RequestBody RetreiveICBPSPRequest request) throws
 * SalesBusinessException;
 * 
 *//**
	 * Put upload ASE nexxus file.
	 *
	 * @param multipart the multipart
	 * @return the response
	 */
/*
 * @PUT
 * 
 * @Path("/putUploadASENexxusFile")
 * 
 * @Consumes(MediaType.MULTIPART_FORM_DATA) // @Produces({
 * MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "Consumes data load feed file", notes =
 * "Returns a JSON object having Acknowledgement. " +
 * "This API will send either Design Rules Data Management or" +
 * "Network Rules Data Management mS depending on offer", response =
 * Response.class)
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error"),
 * 
 * @ApiResponse(code = 400, message = "Service failed"),
 * 
 * @ApiResponse(code = 01014, message = "Service not defined"),
 * 
 * @ApiResponse(code = 01016, message = "Processing error"),
 * 
 * @ApiResponse(code = 00000, message = "Request completed successfully")
 * 
 * })
 * 
 * Response putUploadASENexxusFile(MultipartBody multipart);
 * 
 * 
 * @GET
 * 
 * @Path("/resources")
 * 
 * @Produces({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "Respond Hello <name>!", notes =
 * "Returns a JSON object with a string to say hello. " +
 * "Uses 'world' if a name is not specified", response = Response.class)
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
 * Response getDroolsFileData();
 * 
 * 
 *//**
	 * Put product data load.
	 *
	 * @param multipart the multipart
	 * @return the response
	 */
/*
 * @PUT
 * 
 * @Path("/productDataLoad")
 * 
 * @Consumes(MediaType.MULTIPART_FORM_DATA) // @Produces({
 * MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "Consumes data load feed file", notes =
 * "Returns a JSON object having Acknowledgement. " +
 * "This API will send either Design Rules Data Management or" +
 * "Network Rules Data Management mS depending on offer", response =
 * Response.class)
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error"),
 * 
 * @ApiResponse(code = 400, message = "Service failed"),
 * 
 * @ApiResponse(code = 01014, message = "Service not defined"),
 * 
 * @ApiResponse(code = 01016, message = "Processing error"),
 * 
 * @ApiResponse(code = 00000, message = "Request completed successfully")
 * 
 * }) public Response putProductDataLoad(MultipartBody multipart);
 * 
 *//**
	 * Retrieve sales order info.
	 *
	 * @param request the request
	 * @return the response
	 */
/*
 * @POST
 * 
 * @Path("/retrieveOptyInfo")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "retrieveSalesOrderInfo operation", notes =
 * "retrieves OptyInfo")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service Not Available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
 * Response retrieveSalesOrderInfo(@RequestBody GetOptyRequest request);
 * 
 *//**
	 * Transform pricing data.
	 *
	 * @param request the request
	 * @return the response
	 */
/*
 * @POST
 * 
 * @Path("/getAccessPrice")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "transform request data", notes =
 * "transform request data", response = APUiResponse.class)
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
 * Response transformPricingData(@RequestBody AccessPricingUiRequest request);
 * 
 *//**
	 * Put product rate plan data load.
	 *
	 * @param multipart the multipart
	 * @return the response
	 */
/*
 * @PUT
 * 
 * @Path("/ratePlanDataLoad")
 * 
 * @Consumes(MediaType.MULTIPART_FORM_DATA) // @Produces({
 * MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "Consumes data load feed file", notes =
 * "Returns a JSON object having Acknowledgement. " +
 * "This API will send either Design Rules Data Management or" +
 * "Network Rules Data Management mS depending on offer", response =
 * Response.class)
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error"),
 * 
 * @ApiResponse(code = 400, message = "Service failed"),
 * 
 * @ApiResponse(code = 01014, message = "Service not defined"),
 * 
 * @ApiResponse(code = 01016, message = "Processing error"),
 * 
 * @ApiResponse(code = 00000, message = "Request completed successfully")
 * 
 * }) public Response putProductRatePlanDataLoad(MultipartBody multipart);
 * 
 *//**
	 * Fetch nexxus solutions by user id.
	 *
	 * @param requestMap the request map
	 * @return the response
	 */
/*
 * @POST
 * 
 * @Path("/fetchNexxusSolutionsByUserId")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @Produces({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value =
 * "Fetches the Nexxus solutions for the user id received in the request", notes
 * = "Fetches the Nexxus solutions for the user id received in the request ")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error"),
 * 
 * @ApiResponse(code = 400, message = "Service failed"),
 * 
 * @ApiResponse(code = 01014, message = "Service not defined"),
 * 
 * @ApiResponse(code = 01016, message = "Processing error"),
 * 
 * @ApiResponse(code = 00000, message = "Request completed successfully")
 * 
 * }) public Response fetchNexxusSolutionsByUserId(Map<String, Object>
 * requestMap);
 * 
 *//**
	 * Gets the billing manage price data.
	 *
	 * @param inventoryrequest the inventoryrequest
	 * @return the billing manage price data
	 */
/*
 * @POST
 * 
 * @Path("/getInventory")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "getBillingManagePriceData", notes =
 * "getBillingManagePriceData")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
 * Response getBillingManagePriceData(ManageBillingPriceInvDataRequest
 * inventoryrequest);
 * 
 *//**
	 * Nexxus output download.
	 *
	 * @param request the request
	 * @return the response
	 */
/*
 * @POST
 * 
 * @Path("/downloadNexxusOutput")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @Produces({ MediaType.APPLICATION_JSON }) //@Produces({
 * MediaType.APPLICATION_OCTET_STREAM})
 * 
 * @ApiOperation(value = "Nexxus Output Download api", notes =
 * "Fetches the Nexxus Output for the request ")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error"),
 * 
 * @ApiResponse(code = 400, message = "Service failed"),
 * 
 * @ApiResponse(code = 01014, message = "Service not defined"),
 * 
 * @ApiResponse(code = 01016, message = "Processing error"),
 * 
 * @ApiResponse(code = 00000, message = "Request completed successfully")
 * 
 * }) public Response nexxusOutputDownload(NexxusOutputRequest request);
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 *//**
	 * Mail notification.
	 *
	 * @param request the request
	 * @return the response
	 * @throws SalesBusinessException the sales business exception
	 */
/*
 * @POST
 * 
 * @Path("/mailNotification")
 * 
 * @Produces({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation( value =
 * "Send the mail notification to SalesTransformation team", notes =
 * "Returns a JSON object with Status. ", response = APUiResponse.class )
 * Response mailNotification(@RequestBody MailRequest request) throws
 * SalesBusinessException;
 * 
 * 
 * @POST
 * 
 * @Path("/datafeed")
 * 
 * @Produces({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "Consumes data load feed file", response =
 * Response.class)
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error"),
 * 
 * @ApiResponse(code = 400, message = "Service failed"),
 * 
 * @ApiResponse(code = 01014, message = "Service not defined"),
 * 
 * @ApiResponse(code = 01016, message = "Processing error"),
 * 
 * @ApiResponse(code = 00000, message = "Request completed successfully")
 * 
 * }) Response datafeed(@RequestBody ChatBotRequest request);
 * 
 *//**
	 * Nexxus output zip file download.
	 *
	 * @param request the request
	 * @return the response
	 */
/*
 * @POST
 * 
 * @Path("/downloadZipFile")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @Produces({ MediaType.APPLICATION_OCTET_STREAM})
 * 
 * @ApiOperation(value = "Nexxus Output Download api", notes =
 * "Fetches the Nexxus Output for the request ")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error"),
 * 
 * @ApiResponse(code = 400, message = "Service failed"),
 * 
 * @ApiResponse(code = 01014, message = "Service not defined"),
 * 
 * @ApiResponse(code = 01016, message = "Processing error"),
 * 
 * @ApiResponse(code = 00000, message = "Request completed successfully")
 * 
 * }) public Response nexxusOutputZipFileDownload(NexxusOutputRequest request);
 * 
 *//**
	 * Gets the internal test.
	 *
	 * @return the internal test
	 */
/*
 * @GET
 * 
 * @Path("/internalTest")
 * 
 * @Produces({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "used for internal test", notes =
 * "used for internal test", response = Response.class)
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
 * Response getInternalTest();
 * 
 *//**
	 * Retreive user details.
	 *
	 * @param request the request
	 * @return the response
	 */
/*
 * @POST
 * 
 * @Path("/retreiveUserDetails")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "retreiveICBPSP request data", notes =
 * "retreiveICBPSP request data")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
 * Response retreiveUserDetails(@RequestBody UserDetailsRequest request);
 * 
 * 
 * 
 * 
 *//**
	 * Consumer detail.
	 *
	 * @param request the request
	 * @return the response
	 */
/*
 * @POST
 * 
 * @Path("/consumerDetail")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "Nexxus Output UserDetail api", notes =
 * "To Show the Data on UI ")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error"),
 * 
 * @ApiResponse(code = 400, message = "Service failed"),
 * 
 * @ApiResponse(code = 01014, message = "Service not defined"),
 * 
 * @ApiResponse(code = 01016, message = "Processing error"),
 * 
 * @ApiResponse(code = 00000, message = "Request completed successfully")
 * 
 * })
 * 
 * public Response consumerDetail(ConsumerDetailRequest request);
 * 
 *//**
	 * Gets the nx output file id.
	 *
	 * @param request the request
	 * @return the nx output file id
	 */
/*
 * @POST
 * 
 * @Path("/getNxOutputFileId")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @Produces({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "Nexxus Output getNxOutputFileId api", notes =
 * "Fetches the Nexxus Output FileId ")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error"),
 * 
 * @ApiResponse(code = 400, message = "Service failed"),
 * 
 * @ApiResponse(code = 01014, message = "Service not defined"),
 * 
 * @ApiResponse(code = 01016, message = "Processing error"),
 * 
 * @ApiResponse(code = 00000, message = "Request completed successfully")
 * 
 * }) public Response getNxOutputFileId(NexxusOutputRequest request);
 * 
 *//**
	 * Upload nx template file.
	 *
	 * @param multipart the multipart
	 * @return the response
	 * @throws SalesBusinessException the sales business exception
	 */
/*
 * @POST
 * 
 * @Path("/uploadNxTemplateFile")
 * 
 * @Consumes({ MediaType.MULTIPART_FORM_DATA})
 * 
 * @Produces({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "upload template file", notes =
 * "Returns a JSON object with file load results. " +
 * "file must be unique on the server and successfully compiled", response =
 * Response.class)
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
 * Response uploadNxTemplateFile(MultipartBody multipart) throws
 * SalesBusinessException;
 * 
 *//**
	 * Retrieve admin data.
	 *
	 * @param request the request
	 * @return the response
	 */
/*
 * @POST
 * 
 * @Path("/retrieveAdminData")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @Produces({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "retrieveAdminData", notes = "Fetches admin data ")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error"),
 * 
 * @ApiResponse(code = 400, message = "Service failed"),
 * 
 * @ApiResponse(code = 01014, message = "Service not defined"),
 * 
 * @ApiResponse(code = 01016, message = "Processing error"),
 * 
 * @ApiResponse(code = 00000, message = "Request completed successfully")
 * 
 * }) public Response retrieveAdminData(RetrieveAdminDataRequest request);
 * 
 *//**
	 * Nexxus request actions.
	 *
	 * @param request the request
	 * @return the response
	 */
/*
 * @POST
 * 
 * @Path("/nexxusRequestActions")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "retrieveFalloutDetails request data", notes =
 * "retrieveFalloutDetails request data")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
 * Response nexxusRequestActions(@RequestBody FalloutDetailsRequest request);
 * 
 * 
 *//**
	 * Fetch all top products.
	 *
	 * @param requestMap the request map
	 * @return the response
	 */
/*
 * @POST
 * 
 * @Path("/fetchAllTopProducts")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @Produces({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "fetchAllTopProducts", notes =
 * "Fetches all top products data ")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error"),
 * 
 * @ApiResponse(code = 400, message = "Service failed"),
 * 
 * @ApiResponse(code = 01014, message = "Service not defined"),
 * 
 * @ApiResponse(code = 01016, message = "Processing error"),
 * 
 * @ApiResponse(code = 00000, message = "Request completed successfully")
 * 
 * }) public Response fetchAllTopProducts(Map<String, Object> requestMap);
 * 
 *//**
	 * Admin update product info.
	 *
	 * @param requestMap the request map
	 * @return the response
	 */
/*
 * @POST
 * 
 * @Path("/adminUpdateProductInfo")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @Produces({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "adminUpdateProductInfo", notes =
 * "Fetches all top products data ")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error"),
 * 
 * @ApiResponse(code = 400, message = "Service failed"),
 * 
 * @ApiResponse(code = 01014, message = "Service not defined"),
 * 
 * @ApiResponse(code = 01016, message = "Processing error"),
 * 
 * @ApiResponse(code = 00000, message = "Request completed successfully")
 * 
 * }) public Response adminUpdateProductInfo(Map<String, Object> requestMap);
 * 
 *//**
	 * Upload nexxus data file.
	 *
	 * @param multipart the multipart
	 * @return the response
	 * @throws SalesBusinessException the sales business exception
	 */
/*
 * @POST
 * 
 * @Path("/uploadNexxusDataFile")
 * 
 * @Consumes({ MediaType.MULTIPART_FORM_DATA})
 * 
 * @Produces({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "upload data file", notes =
 * "Returns a JSON object with file load results. " +
 * "file must be unique on the server and successfully compiled", response =
 * Response.class)
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
 * Response uploadNexxusDataFile(@RequestParam("file")MultipartBody multipart)
 * throws SalesBusinessException;
 * 
 * 
 *//**
	 * To upload the eth tokens
	 *
	 * @param multipart the multipart
	 * @return the response
	 * @throws SalesBusinessException the sales business exception
	 */
/*
 * @POST
 * 
 * @Path("/bulkUploadEthTokens")
 * 
 * @Consumes({ MediaType.MULTIPART_FORM_DATA})
 * 
 * @Produces({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "upload ethernet token file", notes =
 * "Returns a JSON object with file load results. " +
 * "file must be unique on the server and successfully compiled", response =
 * Response.class)
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
 * Response bulkUploadEthTokens(@RequestParam("file")MultipartBody multipart)
 * throws SalesBusinessException;
 * 
 * 
 *//**
	 * To download the eth tokens
	 *
	 * @param multipart the multipart
	 * @return the response
	 * @throws SalesBusinessException the sales business exception
	 */
/*
 * @POST
 * 
 * @Path("/downloadFailedTokenFile")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @Produces({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "download failed ethernet tokens file", notes =
 * "Returns a JSON object with file load results. " +
 * "file must be unique on the server and successfully compiled", response =
 * Response.class)
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
 * Response downloadFailedTokenFile(@RequestBody FailedEthTokenRequest request)
 * throws SalesBusinessException;
 * 
 *//**
	 * To upload the bulk requests to edf.
	 * 
	 * @param multipart
	 * @return
	 * @throws SalesBusinessException
	 */
/*
 * @POST
 * 
 * @Path("/manBulkUploadToEDF")
 * 
 * @Consumes({ MediaType.MULTIPART_FORM_DATA })
 * 
 * @Produces({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "download imported man and man file", notes =
 * "Returns a JSON object with file load results. " +
 * "file must be unique on the server and successfully compiled", response =
 * Response.class)
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
 * Response manBulkUploadToEDF(@RequestParam("file")MultipartBody multipart)
 * throws SalesBusinessException;
 * 
 * 
 * 
 *//**
	 * Transmit design data.
	 *
	 * @param request the request
	 * @return the response
	 * @throws SalesBusinessException the sales business exception
	 */
/*
 * @POST
 * 
 * @Path("/transmitDesignData")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @Produces({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "Nexxus transmitDesignData api", notes =
 * "get the update DesignData from PED ")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error"),
 * 
 * @ApiResponse(code = 400, message = "Service failed"),
 * 
 * @ApiResponse(code = 01014, message = "Service not defined"),
 * 
 * @ApiResponse(code = 01016, message = "Processing error"),
 * 
 * @ApiResponse(code = 00000, message = "Request completed successfully")
 * 
 * }) public Response transmitDesignData(@RequestBody TransmitDesignDataRequest
 * request)throws SalesBusinessException;
 * 
 *//**
	 * Prepare and send mail for PED request.
	 *
	 * @param response the response
	 * @return the response
	 */
/*
 * @POST
 * 
 * @Path("/prepareAndSendMailForPEDRequest")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @Produces({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "Email/Dmaap event notification", notes =
 * "Email/Dmaap event notification")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error"),
 * 
 * @ApiResponse(code = 400, message = "Service failed"),
 * 
 * @ApiResponse(code = 01014, message = "Service not defined"),
 * 
 * @ApiResponse(code = 01016, message = "Processing error"),
 * 
 * @ApiResponse(code = 00000, message = "Request completed successfully")
 * 
 * }) public Response
 * prepareAndSendMailForPEDRequest(com.att.sales.nexxus.accesspricing.model.
 * TransmitDesignDataResponse response);
 * 
 * @POST
 * 
 * @Path("/getnXPEDStatus")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @Produces({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "get NX PED Status", notes = "get NX PED Status")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error"),
 * 
 * @ApiResponse(code = 400, message = "Service failed"),
 * 
 * @ApiResponse(code = 01014, message = "Service not defined"),
 * 
 * @ApiResponse(code = 01016, message = "Processing error"),
 * 
 * @ApiResponse(code = 00000, message = "Request completed successfully")
 * 
 * }) public Response getnXPEDStatus(GetNxPEDStatusRequest request);
 * 
 *//**
	 * Rate Letter Status.
	 *
	 * @param request the request
	 * @return the response
	 */
/*
 * @POST
 * 
 * @Path("/rateLetterStatus")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "Rate Letter Status Request", notes =
 * "Rate Letter Status Data")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
 * Response rateLetterStatus(@RequestBody RateLetterStatusRequest request)throws
 * SalesBusinessException;
 * 
 *//**
	 * Publish Validated Addresses Status.
	 *
	 * @param request the request
	 * @return the response
	 */
/*
 * @POST
 * 
 * @Path("/publishValidatedAddressesStatus")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "Publish Validated Addresses Request", notes =
 * "Publish Validated Addresses Status Data")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
 * Response publishValidatedAddressesStatus(@RequestBody
 * PublishValidatedAddressesStatusRequest request)throws SalesBusinessException;
 * 
 * @POST
 * 
 * @Path("/serviceValidation")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "Service Validation", notes = "Service Validation")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
 * Response serviceValidation(@RequestBody ServiceValidationRequest request)
 * throws SalesBusinessException;
 * 
 * @POST
 * 
 * @Path("/createTransaction")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "Create Transaction", notes = "Create Transaction")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
 * Response createTransaction() throws SalesBusinessException;
 * 
 * @POST
 * 
 * @Path("/getTransaction")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "Get Transaction", notes = "Get Transaction")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
 * Response getTransaction(@RequestBody String transactionId) throws
 * SalesBusinessException;
 * 
 * @POST
 * 
 * @Path("/getTransactionLine")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "Get Transaction Line", notes = "Get Transaction Line")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
 * Response getTransactionLine(@RequestBody String transactionId) throws
 * SalesBusinessException;
 * 
 * @POST
 * 
 * @Path("/updateTransactionPricingRequest")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "Update Transaction Pricing Request", notes =
 * "Update Transaction Pricing Request")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
 * Response updateTransactionPricingRequest(@RequestBody Map<String, Object>
 * designMap) throws SalesBusinessException;
 * 
 * @POST
 * 
 * @Path("/updateTransactionSiteUpload")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "Update Transaction Site Upload", notes =
 * "Update Transaction Site Upload")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
 * Response updateTransactionSiteUpload(@RequestBody String transactionId)
 * throws SalesBusinessException;
 * 
 * @POST
 * 
 * @Path("/removeTransactionLine")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "Remove Transaction Line", notes =
 * "Remove Transaction Line")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
 * Response removeTransactionLine(@RequestBody Map<String, Object> designMap)
 * throws SalesBusinessException;
 * 
 * @POST
 * 
 * @Path("/updateTransactionOverride")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "Update Transaction Override", notes =
 * "Update Transaction Override")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
 * Response updateTransactionOverride(@RequestBody
 * UpdateTransactionOverrideRequest request) throws SalesBusinessException;
 * 
 * @POST
 * 
 * @Path("/copyTransaction")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "Copy Transaction", notes = "Copy Transaction")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
 * Response copyTransaction(@RequestBody Map<String, Object>
 * copyTransactionRequest) throws SalesBusinessException;
 * 
 * @POST
 * 
 * @Path("/getCustomPricing")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "get custom pricing", notes =
 * "get custom pricing to get the rateLetter")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
 * Response getCustomPricing(@RequestBody CustomPricingRequest
 * customPricingRequest) throws SalesBusinessException;
 * 
 * @POST
 * 
 * @Path("/getCustomPricingSalesOne")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "get custom pricing", notes =
 * "get custom pricing to get the rateLetter")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
 * Response getCustomPricingSalesOne(@RequestBody CustomPricingRequest
 * customPricingRequest) throws SalesBusinessException;
 * 
 * 
 * @POST
 * 
 * @Path("/updateTransactionQualifyService")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "Update Transaction Qualify Service", notes =
 * "Update Transaction Qualify Service")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
 * Response updateTransactionQualifyService(@RequestBody Map<String, Object>
 * updateTransQualifyServiceRequest) throws SalesBusinessException;
 * 
 * @POST
 * 
 * @Path("/updateTransactionPriceScore")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "Update Transaction Price Score", notes =
 * "Update Transaction Price Score")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
 * Response updateTransactionPriceScore(@RequestBody Map<String, Object>
 * updateTransPriceScoreRequest) throws SalesBusinessException;
 * 
 * @POST
 * 
 * @Path("/updateTransactionSubmitToApproval")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "Update Transaction Submit to Approval", notes =
 * "Update Transaction Submit to Approval")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
 * Response updateTransactionSubmitToApproval(@RequestBody Map<String, Object>
 * updateTransSubmitToApprovalRequest) throws SalesBusinessException;
 * 
 * @POST
 * 
 * @Path("/generateRateLetter")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "Generate Rate Letter", notes = "Generate Rate Letter")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
 * Response generateRateLetter(@RequestBody Map<String, Object>
 * generateRateLetterRequest) throws SalesBusinessException;
 * 
 * 
 * @POST
 * 
 * @Path("/aseodReqRates")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "ASEoD req rates", notes = "ASEoD req rates")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
 * Response aseodReqRates(@RequestBody String transactionId) throws
 * SalesBusinessException;
 * 
 *//**
	 * Download encoded Binary file
	 *
	 * @param request the request
	 * @return the response
	 */
/*
 * @POST
 * 
 * @Path("/getEncodedBinaryFile")
 * 
 * @Consumes({ MediaType.APPLICATION_JSON })
 * 
 * @Produces({ MediaType.APPLICATION_JSON }) //@Produces({
 * MediaType.APPLICATION_OCTET_STREAM})
 * 
 * @ApiOperation(value = "Nexxus encode Binary file Download api", notes =
 * "Fetches the Nexxus Output for the request")
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error"),
 * 
 * @ApiResponse(code = 400, message = "Service failed"),
 * 
 * @ApiResponse(code = 01014, message = "Service not defined"),
 * 
 * @ApiResponse(code = 01016, message = "Processing error"),
 * 
 * @ApiResponse(code = 00000, message = "Request completed successfully")
 * 
 * }) public Response getEncodedBinaryFile(NexxusOutputRequest request);
 * 
 * @GET
 * 
 * @Path("/refreshCache")
 * 
 * @Produces({ MediaType.APPLICATION_JSON })
 * 
 * @ApiOperation(value = "used for refreshCache test", notes =
 * "used for refreshCache test", response = Response.class)
 * 
 * @ApiResponses(value = { @ApiResponse(code = 404, message =
 * "Service not available"),
 * 
 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
 * Response refreshCache();
 *//**
	 * Download encoded Binary file
	 *
	 * @param request the request
	 * @return the response
<<<<<<< HEAD
	 *//*
		 * @POST
		 * 
		 * @Path("/getContractInventory")
		 * 
		 * @Consumes({ MediaType.APPLICATION_JSON })
		 * 
		 * @Produces({ MediaType.APPLICATION_JSON })
		 * 
		 * @ApiOperation(value = "Nexxus to call getSDNContractInventory", notes =
		 * "calling getSDNContractInvnetory to SSDF")
		 * 
		 * @ApiResponses(value = { @ApiResponse(code = 404, message =
		 * "Service not available"),
		 * 
		 * @ApiResponse(code = 500, message = "Unexpected Runtime error"),
		 * 
		 * @ApiResponse(code = 400, message = "Service failed"),
		 * 
		 * @ApiResponse(code = 01014, message = "Service not defined"),
		 * 
		 * @ApiResponse(code = 01016, message = "Processing error"),
		 * 
		 * @ApiResponse(code = 00000, message = "Request completed successfully")
		 * 
		 * }) public Response getContractInventory(@RequestBody
		 * ContractInventoryRequestBean request);
		 * 
		 * 
		 * @POST
		 * 
		 * @Path("/addUser")
		 * 
		 * @Consumes({ MediaType.APPLICATION_JSON })
		 * 
		 * @Produces({ MediaType.APPLICATION_JSON })
		 * 
		 * @ApiOperation(value = "Adding New User to Nexxus", notes =
		 * "Adding New User to Nexxus")
		 * 
		 * @ApiResponses(value = { @ApiResponse(code = 404, message =
		 * "Service not available"),
		 * 
		 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
		 * Response addUser(AddUserRequest request) throws SalesBusinessException;
		 * 
		 * @POST
		 * 
		 * @Path("/checkAccess")
		 * 
		 * @Consumes({ MediaType.APPLICATION_JSON })
		 * 
		 * @Produces({ MediaType.APPLICATION_JSON })
		 * 
		 * @ApiOperation(value = "Checking User Access to Nexxus", notes =
		 * "Checking User Access to Nexxus")
		 * 
		 * @ApiResponses(value = { @ApiResponse(code = 404, message =
		 * "Service not available"),
		 * 
		 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
		 * Response checkAccess(CheckAccessRequest request) throws
		 * SalesBusinessException;
		 * 
		 * @PUT
		 * 
		 * @Path("/nexxusSolutionAction")
		 * 
		 * @Consumes({ MediaType.APPLICATION_JSON })
		 * 
		 * @Produces({ MediaType.APPLICATION_JSON })
		 * 
		 * @ApiOperation(value = "Archive Solution to Nexxus", notes =
		 * "Archive Solution to Nexxus")
		 * 
		 * @ApiResponses(value = { @ApiResponse(code = 404, message =
		 * "Service not available"),
		 * 
		 * @ApiResponse(code = 500, message = "Unexpected Runtime Error") }) public
		 * Response nexxusSolutionAction(NexxusSolActionRequest request) throws
		 * SalesBusinessException;
		 * 
		 * @POST
		 * 
		 * @Path("/submitFeedback")
		 * 
		 * @Consumes({ MediaType.APPLICATION_JSON })
		 * 
		 * @Produces({ MediaType.APPLICATION_JSON })
		 * 
		 * @ApiOperation(value = "submitFeedback to Nexxus", notes =
		 * "submitFeedback to Nexxus")
		 * 
		 * @ApiResponses(value = { @ApiResponse(code = 404, message =
		 * "Service not available"),
		 * 
		 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
		 * Response submitFeedback(SubmitFeedbackRequest request) throws
		 * SalesBusinessException;
		 * 
		 * @POST
		 * 
		 * @Path("/bulkUploadInrData")
		 * 
		 * @Consumes({ MediaType.MULTIPART_FORM_DATA})
		 * 
		 * @Produces({ MediaType.APPLICATION_JSON })
		 * 
		 * @ApiOperation(value = "upload inr data file", notes =
		 * "Returns a JSON object with file load results. " +
		 * "file must be unique on the server and successfully compiled", response =
		 * Response.class)
		 * 
		 * @ApiResponses(value = { @ApiResponse(code = 404, message =
		 * "Service not available"),
		 * 
		 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
		 * Response bulkUploadInrData(@RequestParam("file")MultipartBody multipart)
		 * throws SalesBusinessException;
		 * 
		 * @POST
		 * 
		 * @Path("/fetchNewEnhancements")
		 * 
		 * @Consumes({ MediaType.APPLICATION_JSON })
		 * 
		 * @Produces({ MediaType.APPLICATION_JSON })
		 * 
		 * @ApiOperation(value = "Fetching new enhancements", notes =
		 * "Fetching new enhancements")
		 * 
		 * @ApiResponses(value = { @ApiResponse(code = 404, message =
		 * "Service not available"),
		 * 
		 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
		 * Response fetchNewEnhancements(NewEnhancementRequest request);
		 * 
		 * @GET
		 * 
		 * @Path("/fetchBillDetails")
		 * 
		 * @Produces({ MediaType.APPLICATION_JSON })
		 * 
		 * @ApiOperation(value = "Fetch new bill details", notes =
		 * "Fetch new bill details")
		 * 
		 * @ApiResponses(value = { @ApiResponse(code = 404, message =
		 * "Service not available"),
		 * 
		 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
		 * Response fetchBillDetails();
		 * 
		 * @POST
		 * 
		 * @Path("/uploadMyPriceLegacyCoData")
		 * 
		 * @Consumes({ MediaType.MULTIPART_FORM_DATA })
		 * 
		 * @Produces({ MediaType.APPLICATION_JSON })
		 * 
		 * @ApiOperation(value = "Upload myPrice data for legacy_co table", notes =
		 * "Returns a JSON object with file load results", response = Response.class)
		 * 
		 * @ApiResponses(value = { @ApiResponse(code = 404, message =
		 * "Service not available"),
		 * 
		 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
		 * Response uploadMyPriceLegacyCoData(MultipartBody multipart) throws
		 * SalesBusinessException;
		 * 
		 * @POST
		 * 
		 * @Path("/syncMyPriceLegacyCoData")
		 * 
		 * @Consumes({ MediaType.APPLICATION_JSON })
		 * 
		 * @Produces({ MediaType.APPLICATION_JSON })
		 * 
		 * @ApiOperation(value = "sync nexxus and myPrice legacy_co table", notes =
		 * "Returns a JSON object with table sync results", response = Response.class)
		 * 
		 * @ApiResponses(value = { @ApiResponse(code = 404, message =
		 * "Service not available"),
		 * 
		 * @ApiResponse(code = 500, message = "Unexpected Runtime error") }) public
		 * Response syncMyPriceLegacyCoData(@RequestBody SyncMyPriceLegacyCoDataRequest
		 * syncMyPriceLegacyCoDataRequest) throws SalesBusinessException; }
		 
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
	public Response getContractInventory(@RequestBody ContractInventoryRequestBean request);

	
	@POST
	@Path("/addUser")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Adding New User to Nexxus", notes = "Adding New User to Nexxus")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response addUser(AddUserRequest request) throws SalesBusinessException;
	
	@POST
	@Path("/checkAccess")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Checking User Access to Nexxus", notes = "Checking User Access to Nexxus")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response checkAccess(CheckAccessRequest request) throws SalesBusinessException;
	
	@PUT
	@Path("/nexxusSolutionAction")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Archive Solution to Nexxus", notes = "Archive Solution to Nexxus")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime Error") })
	public Response nexxusSolutionAction(NexxusSolActionRequest request) throws SalesBusinessException;
	
	@POST
	@Path("/submitFeedback")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "submitFeedback to Nexxus", notes = "submitFeedback to Nexxus")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response submitFeedback(SubmitFeedbackRequest request) throws SalesBusinessException;
	
	@POST
	@Path("/bulkUploadInrData")
	@Consumes({ MediaType.MULTIPART_FORM_DATA})
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "upload inr data file", notes = "Returns a JSON object with file load results. "
			+ "file must be unique on the server and successfully compiled", response = Response.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response bulkUploadInrData(@RequestParam("file")MultipartBody multipart) throws SalesBusinessException;

	@POST
	@Path("/fetchNewEnhancements")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Fetching new enhancements", notes = "Fetching new enhancements")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response fetchNewEnhancements(NewEnhancementRequest request);

	@GET
	@Path("/fetchBillDetails")
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Fetch new bill details", notes = "Fetch new bill details")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response fetchBillDetails();
	
	@POST
	@Path("/uploadMyPriceLegacyCoData")
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Upload myPrice data for legacy_co table", notes = "Returns a JSON object with file load results", response = Response.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response uploadMyPriceLegacyCoData(MultipartBody multipart) throws SalesBusinessException;
	
	@POST
	@Path("/syncMyPriceLegacyCoData")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "sync nexxus and myPrice legacy_co table", notes = "Returns a JSON object with table sync results", response = Response.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response syncMyPriceLegacyCoData(@RequestBody SyncMyPriceLegacyCoDataRequest syncMyPriceLegacyCoDataRequest) throws SalesBusinessException;
	
	@POST
	@Path("/solutionLockCheck")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Solution Locking API", notes = "Lock the NX Solution")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response solutionLockCheck(SolutionLockRequest request) throws SalesBusinessException;

} 
	
	
	 * Retrieve billing charges info.
	 *
	 * @param request the request
	 * @return the response
	 
	@POST
	@Path("/retrieveBillingCharges")
	@Consumes({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "retrieveBillingCharges operation", notes = "retrieves BillingCharges")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service Not Available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response retrieveBillingCharges(@RequestBody GetBillingChargesRequest request);
	@POST
	@Path("/usrpDesign")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "query usrp design data", notes = "mereg with price json to form new inventory json")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response usrpDesign(InrJsonServiceRequest request) throws SalesBusinessException;
}
*/