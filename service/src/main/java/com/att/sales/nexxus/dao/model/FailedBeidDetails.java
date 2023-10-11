/**
 * 
 */
package com.att.sales.nexxus.dao.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The Class FailedBeidDetails.
 *
 * @author RudreshWaladaunki
 * FALLOUT_DATA_ID NUMBER(11),
 * NX_SOLUTION_ID NUMBER(11),
 * NX_REQ_ID NUMBER(11),
 * FALLOUT_DATA CLOB
 */
@Entity
@Table(name="NX_FAILED_BEID_DETAILS")
public class FailedBeidDetails implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The fallout data id. */
	@SequenceGenerator(name="sequence_nx_failed_beid_details",sequenceName="SEQ_NX_FALLOUT_DATA_ID", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="sequence_nx_failed_beid_details")
	@Id
	@Column(name="FALLOUT_DATA_ID")
	private long falloutDataId;
	
	/** The nx solution id. */
	@Column(name="NX_SOLUTION_ID")
	private long nxSolutionId;
	
	/** The nx req id. */
	@Column(name="NX_REQ_ID")
	private long nxReqId;
	
	/** The fallout data. */
	@Column(name="FALLOUT_DATA")
	private String falloutData;

	/**
	 * Gets the fallout data id.
	 *
	 * @return the falloutDataId
	 */
	public long getFalloutDataId() {
		return falloutDataId;
	}

	/**
	 * Sets the fallout data id.
	 *
	 * @param falloutDataId the falloutDataId to set
	 */
	public void setFalloutDataId(long falloutDataId) {
		this.falloutDataId = falloutDataId;
	}

	/**
	 * Gets the nx solution id.
	 *
	 * @return the nxSolutionId
	 */
	public long getNxSolutionId() {
		return nxSolutionId;
	}

	/**
	 * Sets the nx solution id.
	 *
	 * @param nxSolutionId the nxSolutionId to set
	 */
	public void setNxSolutionId(long nxSolutionId) {
		this.nxSolutionId = nxSolutionId;
	}

	/**
	 * Gets the nx req id.
	 *
	 * @return the nxReqId
	 */
	public long getNxReqId() {
		return nxReqId;
	}

	/**
	 * Sets the nx req id.
	 *
	 * @param nxReqId the nxReqId to set
	 */
	public void setNxReqId(long nxReqId) {
		this.nxReqId = nxReqId;
	}

	/**
	 * Gets the fallout data.
	 *
	 * @return the falloutData
	 */
	public String getFalloutData() {
		return falloutData;
	}

	/**
	 * Sets the fallout data.
	 *
	 * @param falloutData the falloutData to set
	 */
	public void setFalloutData(String falloutData) {
		this.falloutData = falloutData;
	}
	
}
