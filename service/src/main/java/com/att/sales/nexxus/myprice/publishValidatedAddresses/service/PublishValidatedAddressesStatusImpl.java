package com.att.sales.nexxus.myprice.publishValidatedAddresses.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.myprice.publishValidatedAddresses.model.PublishValidatedAddressesStatusRequest;
import com.att.sales.nexxus.myprice.publishValidatedAddresses.model.PublishValidatedAddressesStatusResponse;

/**
 * @author IndraSingh
 * */

@Service("PublishValidatedAddressesStatusImpl")
public class PublishValidatedAddressesStatusImpl extends BaseServiceImpl implements PublishValidatedAddressesStatus {

	private static Logger log = LoggerFactory.getLogger(PublishValidatedAddressesStatusImpl.class);
	
	@Override
	public ServiceResponse publishValidatedAddressesStatus(
			PublishValidatedAddressesStatusRequest request) {
		
		log.info("Entering publishValidatedAddressesStatus() method");
		
		PublishValidatedAddressesStatusResponse response = new PublishValidatedAddressesStatusResponse();
		
		log.info("Exiting publishValidatedAddressesStatus() method");
		
		setSuccessResponse(response);
		
		return response;
	}

}
