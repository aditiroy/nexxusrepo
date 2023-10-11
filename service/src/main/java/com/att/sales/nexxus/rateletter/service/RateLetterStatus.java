package com.att.sales.nexxus.rateletter.service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.rateletter.model.RateLetterStatusRequest;
import com.att.sales.nexxus.rateletter.model.RateLetterStatusResponse;

public interface RateLetterStatus {
	

	public ServiceResponse rateLetterStatus(RateLetterStatusRequest request) throws SalesBusinessException;

	void triggerDmaapEvent(RateLetterStatusRequest request);

}
