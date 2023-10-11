package com.att.sales.nexxus.userdetails.service;

/**
*
*
* @author aa316k
*         
*/

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.userdetails.model.ConsumerDetailRequest;
import com.att.sales.nexxus.userdetails.model.ConsumerDetailResponse;



/**
 * Process the Add,delete and retrieve functionality for Nexxus UI .
 */

public interface ConsumerDetailService {
	

	/**
	 * Consumer detail data.
	 *
	 * @param request the request
	 * @return the consumer detail response
	 * @throws SalesBusinessException the sales business exception
	 */
	public ServiceResponse consumerDetailData(ConsumerDetailRequest request) throws SalesBusinessException;
	

}
