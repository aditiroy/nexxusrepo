package com.att.sales.nexxus.dao.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.repository.NxOutputBeanJsonType;
import com.att.sales.nexxus.output.entity.NxOutputBean;

/**
 * The Class NxOutputFileModel.
 *
 * @author vt393d
 */
@Entity
@Table(name="nx_output_file")
public class NxOutputFileModel implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The id. */
	@Column(name="NX_FILE_ID")
	@Id
	@SequenceGenerator(name="sequence_nx_output_file",sequenceName="SEQ_NX_FILE_ID", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="sequence_nx_output_file")
	private Long id;
	
	/** The intermediate json. */
	@Column(name = "INTERMEDIATE_JSON")
	private String intermediateJson;
	
	/** The output. */
	@Column(name = "OUTPUT_JSON")
	@Convert(converter = NxOutputBeanJsonType.class)
	private  NxOutputBean output = new NxOutputBean();
	
	
	/** The output file. */
	@Lob @Column(name="OUTPUT_FILE",updatable=true)
	private byte[] outputFile;
	
	/** The file name. */
	@Column(name = "FILE_NAME")
	private String fileName;
	
	/** The file type. */
	@Column(name = "FILE_TYPE")
	private String fileType;
	
	/** The created date. */
	@Column(name = "CREATED_DATE")
	private Timestamp createdDate;
	
	/** The modified date. */
	@Column(name = "MODIFIED_DATE")
	private Timestamp modifiedDate;
	
	/** The status. */
	@Column(name = "STATUS")
	private String status;
	
	/** The inventory json. */
	@Column(name = "INVENTORY_JSON")
	private String inventoryJson;
	
	/** The nx request details. */
	//bi-directional many-to-one association to NxRequestDetail
	@ManyToOne
	@JoinColumn(name="NX_REQ_ID")
	private NxRequestDetails nxRequestDetails;
	
	/** The fall out data. */
	@Column(name = "FALLOUT_DATA")
	private String fallOutData;
	
	@Column(name = "MP_OUTPUT_JSON")
	private String mpOutputJson;
	
	@Column(name = "NXSITEID_IND")
	private String nxSiteIdInd;
	
	@Column(name = "CDIR_DATA")
	private String cdirData;
	
	public NxOutputFileModel() {
		
	}
	
	public NxOutputFileModel(NxOutputFileModel copy) {
		intermediateJson = copy.getIntermediateJson();
		output = copy.getOutput();
		outputFile = copy.getOutputFile();
		fileName = copy.getFileName();
		fileType = copy.getFileType();
		createdDate = copy.getCreatedDate();
		modifiedDate = copy.getModifiedDate();
		status = copy.getStatus();
		inventoryJson = copy.getInventoryJson();
		fallOutData = copy.getFallOutData();
		mpOutputJson = copy.getMpOutputJson();
		nxSiteIdInd = StringConstants.CONSTANT_N;
		cdirData = copy.getCdirData();
		dmaapFailureJson = copy.getDmaapFailureJson();
		inventoryFileSize = copy.getInventoryFileSize();
	}


	@Column(name = "DMAAP_FAILURE_JSON")
	private String dmaapFailureJson;
	
	/** The inventory file size */
	@Column(name = "INVENTORY_FILE_SIZE")
	private String inventoryFileSize;
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(Long id) {
		this.id = id;
	}


	/**
	 * Gets the intermediate json.
	 *
	 * @return the intermediate json
	 */
	public String getIntermediateJson() {
		return intermediateJson;
	}

	/**
	 * Sets the intermediate json.
	 *
	 * @param intermediateJson the new intermediate json
	 */
	public void setIntermediateJson(String intermediateJson) {
		this.intermediateJson = intermediateJson;
	}

	/**
	 * Gets the output.
	 *
	 * @return the output
	 */
	public NxOutputBean getOutput() {
		return output;
	}

	/**
	 * Sets the output.
	 *
	 * @param output the new output
	 */
	public void setOutput(NxOutputBean output) {
		this.output = output;
	}

	/**
	 * Gets the output file.
	 *
	 * @return the output file
	 */
	public byte[] getOutputFile() {
		return outputFile;
	}

	/**
	 * Sets the output file.
	 *
	 * @param outputFile the new output file
	 */
	public void setOutputFile(byte[] outputFile) {
		this.outputFile = outputFile;
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
	 * Gets the file type.
	 *
	 * @return the file type
	 */
	public String getFileType() {
		return fileType;
	}

	/**
	 * Sets the file type.
	 *
	 * @param fileType the new file type
	 */
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	/**
	 * Gets the created date.
	 *
	 * @return the created date
	 */
	public Timestamp getCreatedDate() {
		return createdDate;
	}

	/**
	 * Sets the created date.
	 *
	 * @param createdDate the new created date
	 */
	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
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
	 * Gets the nx request details.
	 *
	 * @return the nx request details
	 */
	public NxRequestDetails getNxRequestDetails() {
		return nxRequestDetails;
	}

	/**
	 * Sets the nx request details.
	 *
	 * @param nxRequestDetails the new nx request details
	 */
	public void setNxRequestDetails(NxRequestDetails nxRequestDetails) {
		this.nxRequestDetails = nxRequestDetails;
	}

	/**
	 * Gets the fall out data.
	 *
	 * @return the fall out data
	 */
	public String getFallOutData() {
		return fallOutData;
	}

	/**
	 * Sets the fall out data.
	 *
	 * @param fallOutData the new fall out data
	 */
	public void setFallOutData(String fallOutData) {
		this.fallOutData = fallOutData;
	}

	/**
	 * Gets the modified date.
	 *
	 * @return the modified date
	 */
	public Timestamp getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * Sets the modified date.
	 *
	 * @param modifiedDate the new modified date
	 */
	public void setModifiedDate(Timestamp modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	
	/**
	 * Gets the inventory json.
	 *
	 * @return the inventory json
	 */
	public String getInventoryJson() {
		return inventoryJson;
	}

	/**
	 * Sets the inventory json.
	 *
	 * @param inventoryJson the new inventory json
	 */
	public void setInventoryJson(String inventoryJson) {
		this.inventoryJson = inventoryJson;
	}

	public String getMpOutputJson() {
		return mpOutputJson;
	}

	public void setMpOutputJson(String mpOutputJson) {
		this.mpOutputJson = mpOutputJson;
	}

	public String getDmaapFailureJson() {
		return dmaapFailureJson;
	}

	public void setDmaapFailureJson(String dmaapFailureJson) {
		this.dmaapFailureJson = dmaapFailureJson;
	}

	public String getInventoryFileSize() {
		return inventoryFileSize;
	}

	public void setInventoryFileSize(String inventoryFileSize) {
		this.inventoryFileSize = inventoryFileSize;
	}
	/**
	 * @return the nxSiteIdInd
	 */
	public String getNxSiteIdInd() {
		return nxSiteIdInd;
	}

	/**
	 * @param nxSiteIdInd the nxSiteIdInd to set
	 */
	public void setNxSiteIdInd(String nxSiteIdInd) {
		this.nxSiteIdInd = nxSiteIdInd;
	}
	
	public String getCdirData() {
		return cdirData;
	}

	public void setCdirData(String cdirData) {
		this.cdirData = cdirData;
	}
}
