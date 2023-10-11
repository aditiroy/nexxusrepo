package com.att.sales.prdm.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The Class NxRatePlanFileAuditData.
 *
 * @author DevChouhan
 */
@Entity
@Table(name="NEXXUS_RATE_PLAN_AUDIT_DATA")
public class NxRatePlanFileAuditData {
	
	/** The file id. */
	@Column(name="FILE_ID")
	@Id
	@SequenceGenerator(name="ratePlanSequence",sequenceName="SEQ_NX_RATE_PLAN_FILE_ID", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ratePlanSequence")
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
	 * @return the file id
	 */
	public BigDecimal getFileId() {
		return fileId;
	}

	/**
	 * Sets the file id.
	 *
	 * @param fileId the new file id
	 */
	public void setFileId(BigDecimal fileId) {
		this.fileId = fileId;
	}

	/**
	 * Gets the file name.
	 *
	 * @return the file name
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets the file name.
	 *
	 * @param fileName the new file name
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
	 * @param fileversion the new fileversion
	 */
	public void setFileversion(String fileversion) {
		this.fileversion = fileversion;
	}

	/**
	 * Gets the status type.
	 *
	 * @return the status type
	 */
	public String getStatusType() {
		return statusType;
	}

	/**
	 * Sets the status type.
	 *
	 * @param statusType the new status type
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
	 * @param status the new status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Gets the modified date.
	 *
	 * @return the modified date
	 */
	public Date getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * Sets the modified date.
	 *
	 * @param modifiedDate the new modified date
	 */
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	/**
	 * Gets the transaction id.
	 *
	 * @return the transaction id
	 */
	public String getTransactionId() {
		return transactionId;
	}

	/**
	 * Sets the transaction id.
	 *
	 * @param transactionId the new transaction id
	 */
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	

	 @Override 
		public int hashCode() { 
			return Objects.hash(fileId); 
		}
	  
	 @Override 
		public boolean equals(Object obj) { 
			if (this == obj) 
				return true; 
			if (obj == null) 
				return false; 
			if (getClass() != obj.getClass()) 
				return false; 
			NxRatePlanFileAuditData other = (NxRatePlanFileAuditData) obj; 
			return Objects.equals(fileId, fileId); 
		}  
	
}
