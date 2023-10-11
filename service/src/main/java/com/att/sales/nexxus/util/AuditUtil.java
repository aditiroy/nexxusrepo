package com.att.sales.nexxus.util;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.constant.AuditTrailConstants;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.solution.NxUiAudit;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxUiAuditRepository;

@Component
public class AuditUtil {

	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;
	@Autowired
	private NxUiAuditRepository nxUiAuditRepository;
	
	public  void addActionToNxUiAudit(Long nxSolutioId, String action,String actionPerformedBy,String status, Long sourceSolId,String attid,Long executionTime,String additionalMessage){
		NxLookupData nxLookupData;
		if(status.equals("S")) {
			nxLookupData=nxLookupDataRepository.findByItemIdAndDatasetNameAndCriteria(action, AuditTrailConstants.AUDIT_DATA_MESSAGE, "Success Message");
		}else {
			nxLookupData=nxLookupDataRepository.findByItemIdAndDatasetNameAndCriteria(action, AuditTrailConstants.AUDIT_DATA_MESSAGE, "Failure Message");
		}
		
		nxSolutioId=nxSolutioId!=null?nxSolutioId:0L;
		String message="";
		String actionType = "";
		if(AuditTrailConstants.COPY_SOLUTION.equalsIgnoreCase(action) && null!=sourceSolId) {
			message=message.concat(nxLookupData.getDescription()+":"+sourceSolId);
		}
		else if((AuditTrailConstants.USER_ADD.equalsIgnoreCase(action)|| AuditTrailConstants.USER_DELETE.equalsIgnoreCase(action)) && null!=attid) {
			message=message.concat(attid+" "+nxLookupData.getDescription());
		}
		else {
			message=nxLookupData.getDescription();
		}
		if (additionalMessage!=null) {
			message=message.concat(","+additionalMessage);
		}
		if (nxLookupData.getSortOrder()==1){
			actionType="Update";
		}
		else{
			actionType ="Fetch";
		}
		if(nxLookupData!=null) {
		NxUiAudit nxUiAudit=new NxUiAudit();
		nxUiAudit.setAction(nxLookupData.getItemId());
		nxUiAudit.setAttId(actionPerformedBy);
		nxUiAudit.setMessage(message);
		nxUiAudit.setNxSolutionId(nxSolutioId);
		nxUiAudit.setCreatedDate(new Date());
		nxUiAudit.setStatus(status);
		nxUiAudit.setExecutionTime(executionTime);
		nxUiAudit.setActionType(actionType);
		nxUiAuditRepository.saveAndFlush(nxUiAudit);		
			
	}
	
	}
}
