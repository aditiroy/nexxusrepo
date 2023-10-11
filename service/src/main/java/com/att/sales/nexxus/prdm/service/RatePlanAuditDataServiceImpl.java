package com.att.sales.nexxus.prdm.service;

import java.math.BigDecimal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.dao.repository.NxPrdmFileAuditStatusRepository;
import com.att.sales.prdm.dao.NxRatePlanFileAuditData;

/**
 * The Class RatePlanAuditDataServiceImpl.
 *
 * @author DevChouhan
 */
@Service
public class RatePlanAuditDataServiceImpl extends BaseServiceImpl {

	/** The nx prdm file audit status repository. */
	@Autowired
	private NxPrdmFileAuditStatusRepository nxPrdmFileAuditStatusRepository;

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(RatePlanAuditDataServiceImpl.class);

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
		NxRatePlanFileAuditData nxRatePlanFileAuditData = new NxRatePlanFileAuditData();
		String transactionId = null;
		String fileVersion = null;// need to check from where will we get fileVersion
		//getting txnId from ServiceMetaData as it will be in header
		if (ServiceMetaData.getRequestMetaData().get("TransactionId")!= null) {
			transactionId = ServiceMetaData.getRequestMetaData().get("TransactionId").toString();
		}
		
		nxRatePlanFileAuditData.setFileName(zipFileName);
		nxRatePlanFileAuditData.setFileversion(fileVersion);
		nxRatePlanFileAuditData.setModifiedDate(new Date());
		nxRatePlanFileAuditData.setStatus(status);
		nxRatePlanFileAuditData.setStatusType(statusType);
		nxRatePlanFileAuditData.setTransactionId(transactionId);
		nxPrdmFileAuditStatusRepository.save(nxRatePlanFileAuditData);
		
		return nxRatePlanFileAuditData.getFileId();
	}
}
