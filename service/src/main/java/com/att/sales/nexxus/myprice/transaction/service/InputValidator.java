package com.att.sales.nexxus.myprice.transaction.service;

import java.util.ArrayList;
import java.util.List;

import com.att.sales.framework.model.Message;
import com.att.sales.framework.model.Status;
import com.att.sales.nexxus.serviceValidation.model.ConfigurationDetails;
import com.att.sales.nexxus.serviceValidation.model.DesignConfiguration;
import com.att.sales.nexxus.serviceValidation.model.ServiceValidationRequest;
import com.att.sales.nexxus.serviceValidation.model.ServiceValidationResponse;
import com.att.sales.nexxus.serviceValidation.model.SiteDetails;

/**
 * @author KumariMuktta
 *
 */
public class InputValidator {

	public ServiceValidationResponse validateRequest(ServiceValidationRequest request) {
		ServiceValidationResponse response;
		response = checkNull(request.getTransactionId());
		if (response == null) {
			response = checkSiteNull(request.getSiteDetails());

		}
		return response;
	}

	public ServiceValidationResponse checkNull(ServiceValidationRequest request) {
		ServiceValidationResponse response = null;
		if (request == null) {
			response = getBadRequestResponse();
		}
		return response;
	}

	public ServiceValidationResponse checkNull(Long transactionId) {
		ServiceValidationResponse response = null;
		if (transactionId == null) {
			response = getBadRequestResponse();
		}
		return response;
	}

	public ServiceValidationResponse checkSiteNull(List<SiteDetails> siteDetails) {
		ServiceValidationResponse response = null;
		if (siteDetails == null || siteDetails.isEmpty()) {
			response = getBadRequestResponse();
		} else {
			for (SiteDetails siteDetail : siteDetails) {
				response = checkConfigDetailsNull(siteDetail.getConfigurationDetails());
				if (response != null) {
					return response;
				}
			}
		}

		return response;
	}

	public ServiceValidationResponse checkConfigDetailsNull(List<ConfigurationDetails> configurationDetails) {
		ServiceValidationResponse response = null;
		if (configurationDetails == null || configurationDetails.isEmpty()) {
			response = getBadRequestResponse();
		} else {
			for (ConfigurationDetails configDetail : configurationDetails) {
				response = checkNull(configDetail.getDocumentNumber());
				if (response == null) {
					response = checkNull(configDetail.getModelName());
					if (response == null) {
						response = checkDesignConfigNull(configDetail.getDesignConfiguration());
					}
				}
				if (response != null) {
					return response;
				}
			}
		}
		return response;
	}

	private ServiceValidationResponse checkDesignConfigNull(List<DesignConfiguration> designConfiguration) {
		ServiceValidationResponse response = null;
		if (designConfiguration != null && !designConfiguration.isEmpty()) {
			for (DesignConfiguration designConfig : designConfiguration) {
				response = checkNull(designConfig.getName());
				if (response == null) {
					response = checkNull(designConfig.getValue());
				}
				if (response != null) {
					return response;
				}
			}
		}
		return response;
	}

	public ServiceValidationResponse checkNull(String value) {
		ServiceValidationResponse response = null;
		if (value == null) {
			response = getBadRequestResponse();
		}
		return response;
	}

	private ServiceValidationResponse getBadRequestResponse() {
		ServiceValidationResponse response = new ServiceValidationResponse();
		Status status = new Status();
		Message msg = new Message("M00002", "BAD_REQUEST", "Request body is Invalid and cannot be parsed");
		response.setStatus(status);
		response.getStatus().setCode("400");
		response.getStatus().setMessages(new ArrayList<Message>());
		response.getStatus().getMessages().add(msg);
		return response;

	}
}
