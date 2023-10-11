/*
 * package com.att.sales.nexxus.common;
 * 
 * import java.util.HashMap; import java.util.Map; import java.util.UUID;
 * 
 * import org.apache.camel.Exchange; import org.apache.commons.lang.StringUtils;
 * import org.slf4j.Logger; import org.slf4j.LoggerFactory; import
 * org.springframework.beans.factory.annotation.Autowired;
 * 
 * import com.att.sales.framework.model.ServiceMetaData; import
 * com.att.sales.framework.model.ServiceResponse; import
 * com.att.sales.framework.model.Status; import
 * com.att.sales.framework.model.constants.HttpErrorCodes; import
 * com.att.sales.framework.util.CommonHeaders; import
 * com.att.sales.framework.util.ValidationUtil;
 * 
 *//**
	 * This service acts as a Pre Filter before getting into the Controller. This
	 * will help to put the input parameters in to ThreadLocal classes if required
	 * should not be used without modification.
	 */
/*
*//**
	 * @author lj772s
	 *
	 */
/*
 * 
 * public class AppPreFilter {
 * 
 *//** The log. */
/*
 * private static Logger log = LoggerFactory.getLogger(AppPreFilter.class);
 * 
 * 
 * 
 * 
 *//** The validation util. */
/*
 * private ValidationUtil validationUtil=new ValidationUtil();
 * 
 *//**
	 * Filters out unnecessary headers that are used during transaction but do not
	 * need to be returned to consumer.
	 *
	 * @param exchange the exchange
	 *//*
		 * public void filter(Exchange exchange) {
		 * 
		 * log.info("AppPreFilter...");
		 * 
		 * Map <String, Object> requestMetaDataHash = new HashMap<>();
		 * 
		 * String offer = (String) exchange.getIn().getHeader("OFFER"); String filename
		 * = (String) exchange.getIn().getHeader(CommonConstants.FILENAME); String
		 * transactionId = (String) exchange.getIn().getHeader("TransactionId"); String
		 * method=(String)ServiceMetaData.getRequestMetaData().get(ServiceMetaData.
		 * METHOD); String
		 * uri=(String)ServiceMetaData.getRequestMetaData().get(ServiceMetaData.URI);
		 * String
		 * version=(String)ServiceMetaData.getRequestMetaData().get(ServiceMetaData.
		 * VERSION); String attuid=(String) exchange.getIn().getHeader("attuid"); //
		 * Initialize Thread Local // Pass the variables which you need to add in Thread
		 * Local // Access the Threadlocal variable across the application using //
		 * Map<String,Object> map=ServiceMetaData.getRequestMetaData()
		 * 
		 * 
		 * requestMetaDataHash.put("OFFER", offer);
		 * requestMetaDataHash.put("TransactionId", transactionId);
		 * requestMetaDataHash.put(CommonConstants.FILENAME, filename);
		 * requestMetaDataHash.put("ATTUID", attuid); // Change this to add more filter
		 * parameters. SERVICEID should match the id // attribute of service_definition
		 * xml file // eg: <sales-service
		 * id="GET:/domainobject/servicenamenoun:v1:actionType">
		 * requestMetaDataHash.put(ServiceMetaData.SERVICE_FILTER,
		 * method.concat(":").concat(uri).concat(":").concat(version));
		 * 
		 * ServiceMetaData.add(requestMetaDataHash);
		 * 
		 * //Validate Headers CommonHeaders commonHeaders = new CommonHeaders();
		 * commonHeaders.setOffer(offer); Status status =
		 * validationUtil.validateRequest(commonHeaders,"validateCommonHeaders","C");
		 * 
		 * if (!HttpErrorCodes.STATUS_OK.toString().equalsIgnoreCase(status.getCode()))
		 * {
		 * 
		 * ServiceResponse response = new ServiceResponse();
		 * 
		 * response.setStatus(status);
		 * 
		 * exchange.getOut().setHeader(FrameworkConstants.ERROR_RESPONSE, response);
		 * ServiceMetaData.getThreadLocal().remove();
		 * 
		 * }
		 * 
		 * 
		 * }
		 * 
		 * 
		 * }
		 */