package com.att.sales.nexxus.myprice.transaction.service;

import java.util.LinkedHashMap;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;

public interface GenerateRateLetter {

	public ServiceResponse generateRateLetter(LinkedHashMap<String, Object> request) throws SalesBusinessException;

}
