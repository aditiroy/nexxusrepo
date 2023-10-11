package com.att.sales.nexxus.dmaap.publishaddress.service;

import java.io.IOException;

import com.att.sales.framework.exception.SalesBusinessException;

/**
 * @author IndraSingh
 * 
 * */
public interface IDMapSubscribePAddressService {
	
	void dMapPublishAddressEvent() throws SalesBusinessException, IOException, InterruptedException, Exception;

}
