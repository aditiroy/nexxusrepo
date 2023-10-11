package com.att.sales.pddm.dao;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The Class NxUDFDetailsFileAuditStatus.
 *
 * @author RudreshWaladaunki
 */
@Entity
@Table(name="NEXXUS_UDF_DETAILS_AUDIT_DATA")
public class NxUDFDetailsFileAuditStatus implements Serializable {
	
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The file id. */
	@Column(name="FILE_ID")
	@Id
	@SequenceGenerator(name="udfDetailsSequence",sequenceName="SEQ_NX_UDF_ZIP_FILE_ID", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="udfDetailsSequence")
	private BigDecimal fileId;
	
	/** The file name. */
	@Column(name="FILE_NAME")
	private String fileName;
	
	/** The fileversion. */
	@Column(name="FILE_VERSION")
	private String fileversion;
	
	/** The status type. */
	@Column(name="STATUS_TYPE")
	private String statusType;
	
	/** The status. */
	@Column(name="STATUS")
	private String status;
	
	/** The modified date. */
	@Column(name="MODIFIED_DATE")
	private Date modifiedDate;
		
	/** The transaction id. */
	@Column(name="TRANSACTION_ID")
	private String transactionId;

	/**
	 * Gets the file id.
	 *
	 * @return the fileId
	 */
	public BigDecimal getFileId() {
		return fileId;
	}

	/**
	 * Sets the file id.
	 *
	 * @param fileId the fileId to set
	 */
	public void setFileId(BigDecimal fileId) {
		this.fileId = fileId;
	}

	/**
	 * Gets the file name.
	 *
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets the file name.
	 *
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Gets the fileversion.
	 *
	 * @return the fileversion
	 */
	public String getFileversion() {
		return fileversion;
	}

	/**
	 * Sets the fileversion.
	 *
	 * @param fileversion the fileversion to set
	 */
	public void setFileversion(String fileversion) {
		this.fileversion = fileversion;
	}

	/**
	 * Gets the status type.
	 *
	 * @return the statusType
	 */
	public String getStatusType() {
		return statusType;
	}

	/**
	 * Sets the status type.
	 *
	 * @param statusType the statusType to set
	 */
	public void setStatusType(String statusType) {
		this.statusType = statusType;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Gets the modified date.
	 *
	 * @return the modifiedDate
	 */
	public Date getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * Sets the modified date.
	 *
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	/**
	 * Gets the transaction id.
	 *
	 * @return the transactionId
	 */
	public String getTransactionId() {
		return transactionId;
	}

	/**
	 * Sets the transaction id.
	 *
	 * @param transactionId the transactionId to set
	 */
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	
	
	
}
