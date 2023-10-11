package com.att.sales.nexxus.pddm.service;

import java.math.BigDecimal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.dao.repository.NxPddmFileAuditStatusRepository;
import com.att.sales.pddm.dao.NxUDFDetailsFileAuditStatus;

/**
 * The Class UDFAuditDataServiceImpl.
 *
 * @author RudreshWaladaunki
 */
@Service
public class UDFAuditDataServiceImpl extends BaseServiceImpl {

	/** The nx pddm file audit status repository. */
	@Autowired
	private NxPddmFileAuditStatusRepository nxPddmFileAuditStatusRepository;

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(UDFAuditDataServiceImpl.class);

	/**
	 * Sets the audit data.
	 *
	 * @param zipFileName the zip file name
	 * @param status the status
	 * @param statusType the status type
	 * @return the big decimal
	 */
	public BigDecimal setAuditData(String zipFileName, String status, String statusType) {
		logger.info("Entered setAuditData() method");
		NxUDFDetailsFileAuditStatus nxUDFDetailsFileAuditStatus = new NxUDFDetailsFileAuditStatus();
		String transactionId = null;
		String fileVersion = null;// need to check from where will we get fileVersion
		//getting txnId from ServiceMetaData as it will be in header
		if (ServiceMetaData.getRequestMetaData().get("TransactionId")!= null) {
			transactionId = ServiceMetaData.getRequestMetaData().get("TransactionId").toString();
		}
		
		nxUDFDetailsFileAuditStatus.setFileName(zipFileName);
		nxUDFDetailsFileAuditStatus.setFileversion(fileVersion);
		nxUDFDetailsFileAuditStatus.setModifiedDate(new Date());
		nxUDFDetailsFileAuditStatus.setStatus(status);
		nxUDFDetailsFileAuditStatus.setStatusType(statusType);
		nxUDFDetailsFileAuditStatus.setTransactionId(transactionId);
		nxPddmFileAuditStatusRepository.save(nxUDFDetailsFileAuditStatus);
		
		return nxUDFDetailsFileAuditStatus.getFileId();
	}
}
