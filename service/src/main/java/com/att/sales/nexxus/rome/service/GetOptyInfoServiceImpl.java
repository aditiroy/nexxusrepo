/**
 * rw161p
 */
package com.att.sales.nexxus.rome.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.att.aft.dme2.internal.google.common.base.Strings;
import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.constant.AuditTrailConstants;
import com.att.sales.nexxus.fallout.service.FalloutDetailsImpl;
import com.att.sales.nexxus.handlers.GetOptyInfoWSHandler;
import com.att.sales.nexxus.rome.model.GetOptyRequest;
import com.att.sales.nexxus.rome.model.GetOptyResponse;
import com.att.sales.nexxus.util.AuditUtil;
import com.att.sales.nexxus.util.NxSolutionUserLockUtil;


/**
 * The Class GetOptyInfoServiceImpl.
 */
@Service
public class GetOptyInfoServiceImpl extends BaseServiceImpl {
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(GetOptyInfoServiceImpl.class);
	
	/** The get opty info WS handler. */
	@Autowired
	private GetOptyInfoWSHandler getOptyInfoWSHandler;
	
	@Autowired
	private AuditUtil auditUtil;
	
	@Autowired
	private NxSolutionUserLockUtil nxSolutionUserLockUtil;
	
	@Autowired
	private FalloutDetailsImpl falloutDetailsImpl;
	
	/**
	 * Perform get opty info.
	 *
	 * @param request the request
	 * @return the gets the opty response
	 * @throws SalesBusinessException the sales business exception
	 */
	public ServiceResponse performGetOptyInfo(GetOptyRequest request) throws SalesBusinessException {
		Long currentTime = System.currentTimeMillis();
        Long startTime = System.currentTimeMillis() - currentTime;

		GetOptyResponse optyResp = null;
		logger.info("Entered performGetOptyInfo() method");
		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put("optyId", request.getOptyId());
		requestMap.put("attuid",   request.getAttuid());
		requestMap.put("action", request.getAction());
		requestMap.put("nxSolutionId", request.getNxSolutionId());
		requestMap.put("solutionDescription", request.getSolutionDescription());	
		requestMap.put("actionPerformedBy", request.getActionPerformedBy());
		 try {
			 	if(!Strings.isNullOrEmpty(request.getSolutionDescription())&& (request.getNxSolutionId()!=null || request.getNxSolutionId()!=0)) {
			 		Long endTime = System.currentTimeMillis() - currentTime;
			        Long executionTime=endTime-startTime;
			 		auditUtil.addActionToNxUiAudit(request.getNxSolutionId(), AuditTrailConstants.SOLUTION_NAME_EDIT, request.getActionPerformedBy(), AuditTrailConstants.SUCCESS, null, null,executionTime,null);
				}
			 	
			 	optyResp = 	(GetOptyResponse) getOptyInfoWSHandler.initiateGetOptyInfoWebService(requestMap);	
			 	
			 	if(request.getNxSolutionId() != null) {
			 		nxSolutionUserLockUtil.updateSolutionLockStatus(request.getNxSolutionId(), request.getActionPerformedBy());
				}
				else if(optyResp.getNxSolutionId() != null) {
					nxSolutionUserLockUtil.updateSolutionLockStatus(optyResp.getNxSolutionId(), request.getActionPerformedBy());
				}
			 	
				setSuccessResponse(optyResp);
				logger.info("ended performGetOptyInfo() method");
				
				if(!Strings.isNullOrEmpty(request.getOptyId())) {
					if(request.getNxSolutionId() != null) {
						Long endTime = System.currentTimeMillis() - currentTime;
				        Long executionTime=endTime-startTime;
						auditUtil.addActionToNxUiAudit(request.getNxSolutionId(), AuditTrailConstants.LINK_WITH_EXISITING_OPPORTUNITYID, request.getActionPerformedBy(), AuditTrailConstants.SUCCESS, null, null,executionTime,null);
					}
					else if(optyResp.getNxSolutionId() != null) {
						Long endTime = System.currentTimeMillis() - currentTime;
				        Long executionTime=endTime-startTime;
						auditUtil.addActionToNxUiAudit(optyResp.getNxSolutionId(), AuditTrailConstants.LINK_WITH_EXISITING_OPPORTUNITYID, request.getActionPerformedBy(), AuditTrailConstants.SUCCESS, null, null,executionTime,null);
					}
				}
		 }catch(Exception e) {
			    optyResp = new GetOptyResponse();
				if(!Strings.isNullOrEmpty(request.getOptyId())) {
					if(request.getNxSolutionId() != null) {
						Long endTime = System.currentTimeMillis() - currentTime;
				        Long executionTime=endTime-startTime;
						auditUtil.addActionToNxUiAudit(request.getNxSolutionId(), AuditTrailConstants.LINK_WITH_EXISITING_OPPORTUNITYID, request.getActionPerformedBy(), AuditTrailConstants.FAIL, null, null,executionTime,null);
						nxSolutionUserLockUtil.updateSolutionLockStatus(request.getNxSolutionId(), request.getActionPerformedBy());
					}
					else if(optyResp.getNxSolutionId() != null) {
						Long endTime = System.currentTimeMillis() - currentTime;
				        Long executionTime=endTime-startTime;
						auditUtil.addActionToNxUiAudit(optyResp.getNxSolutionId(), AuditTrailConstants.LINK_WITH_EXISITING_OPPORTUNITYID, request.getActionPerformedBy(), AuditTrailConstants.FAIL, null, null,executionTime,null);
						nxSolutionUserLockUtil.updateSolutionLockStatus(optyResp.getNxSolutionId(), request.getActionPerformedBy());
					}
				}
			    falloutDetailsImpl.setErrorResponse(optyResp, "M00077");
		 }
		return optyResp;
	}

}
