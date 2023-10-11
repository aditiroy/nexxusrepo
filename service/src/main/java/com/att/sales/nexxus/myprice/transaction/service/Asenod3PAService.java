package com.att.sales.nexxus.myprice.transaction.service;

import com.att.sales.framework.exception.SalesBusinessException;

public interface Asenod3PAService<T>{
	
	public T process(String transactionId) throws SalesBusinessException ;

}
