package com.att.sales.nexxus.myprice.publishValidatedAddresses.service;

import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.myprice.publishValidatedAddresses.model.PublishValidatedAddressesStatusRequest;
import com.att.sales.nexxus.myprice.publishValidatedAddresses.model.PublishValidatedAddressesStatusResponse;

public interface PublishValidatedAddressesStatus {

	public ServiceResponse publishValidatedAddressesStatus(PublishValidatedAddressesStatusRequest request);
	
}
