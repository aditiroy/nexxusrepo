package com.att.sales.nexxus.dao.model;

import java.io.Serializable;
import java.sql.Blob;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;

/**
 * The Class NxOutputFileAuditModel.
 */
@Entity
@Table(name="NX_OUTPUT_FILE_AUDIT")
public class NxOutputFileAuditModel implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The nx output file id. */
	@SequenceGenerator(name="sequence_nx_output_file_audit",sequenceName="SEQ_NX_OUTPUT_FILE_ID", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="sequence_nx_output_file_audit")
	@Id
	@Column(name="NX_OUTPUT_FILE_ID")
	private Long nxOutputFileId;
	
	/** The nx solution detail. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="NX_SOLUTION_ID")
	private NxSolutionDetail nxSolutionDetail;
	
	/** The file name. */
	@Column(name="FILE_NAME")
	private String fileName;
	
	/** The output file. */
	@Column(name="OUTPUT_FILE")
	private Blob outputFile;
	
	/** The created date. */
	@Temporal(TemporalType.DATE)
	@Column(name="CREATED_DATE")
	private Date createdDate = new Date();

	/**
	 * Gets the nx output file id.
	 *
	 * @return the nxOutputFileId
	 */
	public Long getNxOutputFileId() {
		return nxOutputFileId;
	}

	/**
	 * Sets the nx output file id.
	 *
	 * @param nxOutputFileId the nxOutputFileId to set
	 */
	public void setNxOutputFileId(Long nxOutputFileId) {
		this.nxOutputFileId = nxOutputFileId;
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
	 * Gets the output file.
	 *
	 * @return the outputFile
	 */
	public Blob getOutputFile() {
		return outputFile;
	}

	/**
	 * Sets the output file.
	 *
	 * @param outputFile the outputFile to set
	 */
	public void setOutputFile(Blob outputFile) {
		this.outputFile = outputFile;
	}

	/**
	 * Gets the created date.
	 *
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * Sets the created date.
	 *
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * Gets the nx solution detail.
	 *
	 * @return the nxSolutionDetail
	 */
	public NxSolutionDetail getNxSolutionDetail() {
		return nxSolutionDetail;
	}

	/**
	 * Sets the nx solution detail.
	 *
	 * @param nxSolutionDetail the nxSolutionDetail to set
	 */
	public void setNxSolutionDetail(NxSolutionDetail nxSolutionDetail) {
		this.nxSolutionDetail = nxSolutionDetail;
	}
		
}
