/**
 * Added by Lijo Manickathan John
  */
package com.att.sales.framework.controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.Message;
import com.att.sales.framework.model.ServiceBean;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.model.Status;
import com.att.sales.framework.model.constants.FrameworkConstants;
import com.att.sales.framework.model.constants.HttpErrorCodes;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.framework.tracing.RestTracingAspect;
import com.att.sales.framework.util.MessageResourcesUtil;
import com.att.sales.framework.util.ServiceLoaderUtil;
import com.att.sales.framework.util.ValidationUtil;
import com.att.sales.nexxus.admin.model.BulkUploadEthTokenRequest;
import com.att.sales.nexxus.edfbulkupload.EdfManBulkUploadRequest;
import com.att.sales.nexxus.model.BulkUploadInrUpdateRequest;
import com.att.sales.nexxus.template.model.NxTemplateUploadRequest;


/**
 * @author Lijo Manickathan John
 *
 */

public class BaseController extends BaseServiceImpl {

	private static final Logger LOG = LoggerFactory.getLogger(BaseController.class);

	/** The message mappings. */
	private java.util.Map<String, Message> messageMappings;

	/** The service loader util. */

	protected ServiceLoaderUtil serviceLoaderUtil = new ServiceLoaderUtil();
	

	@Autowired
	private ApplicationContext appContext;

	@Autowired
	private RestTracingAspect restTracing;

	ValidationUtil validationUtil = new ValidationUtil();
	
	@Autowired
	private ObjectMapper mapper;

	/**
	 * Gets the message mappings.
	 * 
	 * @return the messageMappings.
	 */
	public java.util.Map<String, Message> getMessageMappings() {
		return this.messageMappings;
	}

	/**
	 * Sets the message mappings.
	 * 
	 * @param messageMappings the messageMappings to set.
	 */
	public void setMessageMappings(java.util.Map<String, Message> messageMappings) {
		this.messageMappings = messageMappings;
	}

	public ValidationUtil getValidationUtil() {

		return this.validationUtil;
	}

	/**
	 * Gets the error status.
	 * 
	 * @param msgCode  the msg code
	 * @param category the category
	 * @return the error status
	 * @inheritDoc Returns the error status matching the parameters.
	 */
	public Status getErrorStatusForMessages(List<Message> messageCodes, Integer httpErrorCode) {

		Status status = new Status();

		this.messageMappings = MessageResourcesUtil.getMessageMapping();
		if (httpErrorCode != null) {

			status.setCode(httpErrorCode.toString());
		}

		status.setMessages(messageCodes);

		return status;
	}

	/**
	 * Gets the error status.
	 * 
	 * @param msgCode  the msg code
	 * @param category the category
	 * @return the error status
	 * @inheritDoc Returns the error status matching the parameters.
	 */
	public Status getErrorStatus(String msgCode, int category) {

		Status status = new Status();

		List<Message> messageList = new ArrayList<Message>();
		this.messageMappings = MessageResourcesUtil.getMessageMapping();

		Message message = this.messageMappings.get(msgCode);

		if (category == HttpErrorCodes.ERROR) {

			status.setCode(HttpErrorCodes.ERROR.toString());
		}

		else if (category == HttpErrorCodes.SERVER_ERROR) {

			status.setCode(HttpErrorCodes.SERVER_ERROR.toString());
		}

		messageList.add(message);
		status.setMessages(messageList);

		return status;
	}

	/**
	 * Factory Method : Gets the error service response.
	 * 
	 * @param msgCode  the msg code
	 * @param category the category
	 * @return ServiceResponse
	 * @inheritDoc Returns the error Service matching the parameters.
	 */
	public ServiceResponse getErrorServiceResponse(String msgCode, int category) {

		return new ServiceResponse(this.getErrorStatus(msgCode, category));

	}

	/**
	 * Factory Method : Look up the Service
	 * 
	 * @return IBaseService
	 * @throws SalesBusinessException
	 * @throws ServiceFailureException
	 * @inheritDoc Returns the error Service matching the parameters.
	 */
	private Object lookupService() throws SalesBusinessException {

		Object baseService = null;

		ServiceBean serviceHandle = null;

		String provider = "C";

		String serviceId = (String) ServiceMetaData.getRequestMetaData().get(ServiceMetaData.SERVICE_FILTER);

		LOG.info("Service ID  is" + serviceId);

		Map<String, Map<String, ServiceBean>> serviceExecutionLoader = serviceLoaderUtil.getServiceExecutionLoader();

		Map<String, ServiceBean> serviceHandleMap = serviceExecutionLoader.get(provider);

		if (serviceHandleMap == null) {

			Map<String, ServiceBean> serviceHandleCommonMap = serviceExecutionLoader
					.get(FrameworkConstants.COMMON_PROVIDER);
			serviceHandle = serviceHandleCommonMap.get(serviceId);
		} else {

			serviceHandle = serviceHandleMap.get(serviceId);

		}

		Map<String, Object> requestMetaDataHash = new HashMap<String, Object>();

		requestMetaDataHash.put("serviceHandle", serviceHandle);

		ServiceMetaData.add(requestMetaDataHash);

		if (serviceHandle == null) {

			return null;
		}
		String impl = serviceHandle.getServiceImpl();

		LOG.info("Service Implementation File Name is" + impl);
		String methodName = serviceHandle.getServiceMethod();

		LOG.info("Implementation Method Name  is" + methodName);

		if (impl != null) {
			baseService = (Object) this.appContext.getBean(impl);
		}

		if (null == baseService || StringUtils.isBlank(methodName)) {
			LOG.error("Method getService Service not defined");

			setValidationMessages("Service Implementation does not exist in services.xml.");
		}

		return baseService;
	}

	/**
	 * Factory Method : executes the method
	 * 
	 * @param parameters The run time parameters
	 * 
	 * @return ServiceResponse
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws Throwable
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @inheritDoc Returns the error Service matching the parameters.
	 */

	public ServiceResponse process(Object... parameters) throws SalesBusinessException {

		Class[] paramString = {};
		if (parameters.length != 0) {
			paramString = new Class[parameters.length];
			for (int i = 0; i < parameters.length; i++) {
				if (parameters[i] != null) {

					paramString[i] = parameters[i].getClass();

				}
			}
		}

		Object baseService = this.lookupService();

		if (baseService == null) {
			Status status = setValidationMessages("Service Implementation does not exist in services.xml.");
			return new ServiceResponse(status);
		}

		Map<String, Object> requestMap = new HashMap<String, Object>();
		requestMap.put(ServiceMetaData.ALL, parameters);

		if (parameters.length > 0) {

			ServiceMetaData.add(requestMap);
		}

		ServiceBean svcBean = (ServiceBean) ServiceMetaData.getRequestMetaData().get("serviceHandle");
		Method method = null;
		try {
			method = baseService.getClass().getDeclaredMethod(svcBean.getServiceMethod(), paramString);

			try {
//				Object[] parametersArray = (Object[]) parameters;
				Object[] parametersArray = Arrays.copyOf(parameters, parameters.length);
				if(!(parametersArray instanceof Byte[])) {
					removeInputStream(parametersArray);
				
				  LOG.info("#######################INCOMING REQUEST PAYLOAD########################");	
				  LOG.info(mapper.writeValueAsString(parametersArray));		

				  restTracing.traceServiceRequest(parametersArray[0], null, null, true);
				}

			} catch (Exception e) {
				LOG.error("Could not send trace to zipkin/jeager");
			}

			
			Object resp = (Object) method.invoke(baseService, parameters);
			
			
			ServiceResponse response = null;
			if (resp instanceof ServiceResponse) {

				LOG.info("The model has extended the ServiceResponse");

				response = (ServiceResponse) resp;	
								
				

			} else if (resp instanceof String) {
				LOG.info("The model has extended the class String");

				ServiceResponse res = new ServiceResponse();
				res = setSuccessResponse(res);
				res.setResponse((String) resp);
				
				return res;
			} else {

				ServiceResponse res = new ServiceResponse();
				Status status = setValidationMessages("Model class needs to extend the class ServiceResponse");
				res.setStatus(status);
				if (ServiceMetaData.getElapsedTime() != null) {
					res.setResponseTime(Long.toString(ServiceMetaData.getElapsedTime()));
				}
				
				return res;

			}

			return response;

		} catch (InvocationTargetException e) {
			
			

			LOG.error("InvocationTargetException#####", e);
			
			Map<String, Object> requestMetaDataHash = new HashMap();
			
			requestMetaDataHash.put("MSEXCEPTION", e);
			ServiceMetaData.add(requestMetaDataHash);

			
			try {

				if (e.getCause() instanceof SalesBusinessException) {

					LOG.error("Exception is BusinessException");

					SalesBusinessException exception = (SalesBusinessException) e.getTargetException();

					throwBusinessException(exception.getHttpErrorCode(), exception.getMessageCodes(), true);
				} else if (e.getCause() instanceof Exception) {
					
					LOG.error("Exception",e);

					throwBusinessException();
				}

			}

			catch (SalesBusinessException exception) {

				LOG.error("BusinessException ....", exception);

				throwBusinessException(exception.getHttpErrorCode(), exception.getMessageCodes(), true);

			}

		} catch (Exception e) {

			LOG.error("Exception  ....", e);

			if (e instanceof NoSuchMethodException) {

				Status status = setValidationMessages(
						"The type of the parameter which got passed in execute method  does not match with any method signature in the service file. It could be because, the type interface is being used in the service file as argument. Replace the interface with class For eg: Replace Map with HashMap..");

				return new ServiceResponse(status);
			} else {

				throwBusinessException();
			}
		}
		return null;

	}

	private void removeInputStream(Object[] parametersArray) {
		if (parametersArray.length == 1) {
			Object reqOb = parametersArray[0];
			if (reqOb instanceof BulkUploadEthTokenRequest) {
				BulkUploadEthTokenRequest requestWithoutInputStream = new BulkUploadEthTokenRequest();
				BulkUploadEthTokenRequest request = (BulkUploadEthTokenRequest) reqOb;
				requestWithoutInputStream.setAction(request.getAction());
				requestWithoutInputStream.setFileName(request.getFileName());
				requestWithoutInputStream.setNxSolutionId(request.getNxSolutionId());
				requestWithoutInputStream.setUserId(request.getUserId());
				parametersArray[0] = requestWithoutInputStream;
			} else if (reqOb instanceof EdfManBulkUploadRequest) {
				EdfManBulkUploadRequest requestWithoutInputStream = new EdfManBulkUploadRequest();
				EdfManBulkUploadRequest request = (EdfManBulkUploadRequest) reqOb;
				requestWithoutInputStream.setFileName(request.getFileName());
				requestWithoutInputStream.setNxSolutionId(request.getNxSolutionId());
				requestWithoutInputStream.setOptyId(request.getOptyId());
				requestWithoutInputStream.setUserId(request.getUserId());
				parametersArray[0] = requestWithoutInputStream;
			} else if (reqOb instanceof BulkUploadInrUpdateRequest) {
				BulkUploadInrUpdateRequest requestWithoutInputStream = new BulkUploadInrUpdateRequest();
				BulkUploadInrUpdateRequest request = (BulkUploadInrUpdateRequest) reqOb;
				requestWithoutInputStream.setAction(request.getAction());
				requestWithoutInputStream.setActionPerformedBy(request.getActionPerformedBy());
				requestWithoutInputStream.setFileName(request.getFileName());
				requestWithoutInputStream.setNxSolutionId(request.getNxSolutionId());
				requestWithoutInputStream.setProduct(request.getProduct());
				parametersArray[0] = requestWithoutInputStream;
			} else if (reqOb instanceof NxTemplateUploadRequest) {
				NxTemplateUploadRequest requestWithoutInputStream = new NxTemplateUploadRequest();
				NxTemplateUploadRequest request = (NxTemplateUploadRequest) reqOb;
				requestWithoutInputStream.setExtension(request.getExtension());
				requestWithoutInputStream.setFileName(request.getFileName());
				requestWithoutInputStream.setFileType(request.getFileType());
				parametersArray[0] = requestWithoutInputStream;
			}
		}
		
	}

	private void throwBusinessException(Integer httpErrorCode, List<Message> messages, boolean flag)
			throws SalesBusinessException {

		if (messages != null && flag == true) {

			throw new SalesBusinessException(httpErrorCode, messages, true);
		}

	}

	private void throwBusinessException() throws SalesBusinessException {

		throw new SalesBusinessException();

	}

	private Status setValidationMessages(String detailedDescription) {

		Status status = new Status();
		Message errorMessage = new Message("M400", detailedDescription, detailedDescription);
		List<Message> errorMessages = new ArrayList<Message>();
		errorMessages.add(errorMessage);
		status.setMessages(errorMessages);
		status.setCode(HttpErrorCodes.ERROR.toString());
		return status;
	}
	public Response handleException(ServiceResponse response, SalesBusinessException exception) {

		LOG.error("Exception ...Handling Exception..");
		Status status = new Status();

		status = getErrorStatusForMessages(exception.getMessageCodes(), exception.getHttpErrorCode());

		response.setStatus(status);
		if (exception.getHttpErrorCode() == 200) {

			return Response.status(javax.ws.rs.core.Response.Status.OK).entity(response).build();
		} else if (exception.getHttpErrorCode() == 400) {

			return Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST).entity(response).build();
		} else if (exception.getHttpErrorCode() == 500) {

			return Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST).entity(response).build();
		} else {

			return Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST).entity(response).build();
		}

	}

	public Response handleException(String response, SalesBusinessException exception) {

		LOG.error("Exception in SalesBusinessException");
		Status status = new Status();

		ServiceResponse serviceResponse = new ServiceResponse();

		LOG.error("Exception in SalesBusinessException where bad request is");

		status = getErrorStatusForMessages(exception.getMessageCodes(), exception.getHttpErrorCode());
		serviceResponse.setStatus(status);

		if (exception.getHttpErrorCode() == 200) {

			return Response.status(javax.ws.rs.core.Response.Status.OK).entity(serviceResponse).build();
		} else if (exception.getHttpErrorCode() == 400) {

			return Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST).entity(serviceResponse).build();
		} else if (exception.getHttpErrorCode() == 500) {

			return Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST).entity(serviceResponse)
					.build();
		} else {

			return Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST).entity(serviceResponse)
					.build();
		}

	}
}
