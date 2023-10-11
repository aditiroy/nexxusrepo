package com.att.sales.nexxus.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.dao.model.NxFeedback;
import com.att.sales.nexxus.dao.model.NxUser;
import com.att.sales.nexxus.dao.repository.NxFeedbackRepository;
import com.att.sales.nexxus.dao.repository.NxUserRepository;
import com.att.sales.nexxus.model.SubmitFeedbackRequest;
import com.att.sales.nexxus.userdetails.service.UserServiceImpl;

@Service
public class SubmitFeedbackServiceImpl extends BaseServiceImpl{
	
	private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	private static final String ERROR_MISSING_ATTUID = "FD0001";
	private static final String ERROR_MISSING_FEEDBACK = "FD0002";
	private static final String ERROR_ATTUID_NOT_FOUND = "FD0003";
	public static final String NONE = "NONE";

	@Autowired
	private NxFeedbackRepository nxFeedbackRepository;

	@Autowired
	private NxUserRepository nxUserRepository;
	
	public ServiceResponse submitFeedback(SubmitFeedbackRequest request) throws SalesBusinessException {
		logger.info("enter submitFeedback method");
		validateFeedbackRequest(request);
		ServiceResponse response = new ServiceResponse();

		NxUser nxUser = nxUserRepository.findByUserAttId(request.getAttuid());
		if (nxUser == null) {
			throw new SalesBusinessException(ERROR_ATTUID_NOT_FOUND);
		}
		NxFeedback nxFeedback = new NxFeedback();
		nxFeedback.setAttId(request.getAttuid());
		nxFeedback.setEmail(nxUser.getEmail());
		nxFeedback.setFeedback(request.getFeedback());
		nxFeedback.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		nxFeedbackRepository.save(nxFeedback);
		logger.info("exit submitFeedback method");
		return setSuccessResponse(response);
	}
	
	protected void validateFeedbackRequest(SubmitFeedbackRequest request) throws SalesBusinessException {
		List<String> messageCodes = new ArrayList<>();
		if (request.getAttuid() == null || request.getAttuid().isEmpty()) {
			messageCodes.add(ERROR_MISSING_ATTUID);
		}
		if (request.getFeedback() == null || request.getFeedback().isEmpty()) {
			messageCodes.add(ERROR_MISSING_FEEDBACK);
		}
		if (!messageCodes.isEmpty()) {
			throw new SalesBusinessException(messageCodes);
		}
	}

}
