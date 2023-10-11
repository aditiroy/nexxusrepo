package com.att.sales.nexxus.service;


import java.util.Map;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.solution.NxTeam;
import com.att.sales.nexxus.model.MailRequest;
import com.att.sales.nexxus.model.MailResponse;
import com.att.sales.nexxus.model.MailServiceRequest;

/**
 * The Interface MailService.
 */
public interface MailService {
	
	/**
	 * Mail notification.
	 *
	 * @param request the request
	 * @return the mail response
	 * @throws SalesBusinessException the sales business exception
	 */
	public ServiceResponse  mailNotification(MailRequest request) throws SalesBusinessException;

	/**
	 * Prepare mail request.
	 *
	 * @param <T> the generic type
	 * @param solnDetails the soln details
	 * @param request the request
	 * @param solnData the soln data
	 * @return the mail service request
	 */
	public <T> MailServiceRequest prepareMailRequest(NxTeam solnDetails,MailRequest request,NxRequestDetails solnData) ;
	
	public  MailServiceRequest prepareMailRequestFMO(NxTeam solnDetails,MailRequest request,NxRequestDetails solnData) ;
	
	/**
	 * Data upload mail notification.
	 *
	 * @param inputmap the inputmap
	 * @param userId the user id
	 * @return the mail response
	 * @throws SalesBusinessException the sales business exception
	 */
	public MailResponse dataUploadMailNotification(Map<String,Object> inputmap,String userId) throws SalesBusinessException;
	
	/**
	 * Prepare data upload mail request.
	 *
	 * @param inputmap the inputmap
	 * @param userId the user id
	 * @return the mail service request
	 */
	public  MailServiceRequest prepareDataUploadMailRequest(Map<String,Object> inputmap,String userId);

	
	/**
	 * Prepare and send mail for PED request.
	 *
	 * @param solutionId the solution id
	 * @param attId the att id
	 * @return the mail response
	 * @throws SalesBusinessException the sales business exception
	 */
	public ServiceResponse prepareAndSendMailForPEDRequest(Long solutionId,String attId)
			throws SalesBusinessException;

	public MailResponse  mailNotificationFMO(MailRequest request) throws SalesBusinessException;

	MailResponse prepareAndSendMailForBulkuploadEthTokensRequest(Long solutionId, String attId, String solutionName,
			int status) throws SalesBusinessException;
	
	MailResponse prepareAndSendMailForServiceAlert(String serviceName, long errorCount, int interval, long nxWebServiceErrorId) throws SalesBusinessException;

}
