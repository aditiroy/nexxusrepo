package com.att.sales.nexxus.dao.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "NX_INR_DESIGN")
public class NxInrDesign implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name = "sequence_nx_inr_design", sequenceName = "SEQ_NX_INR_DESIGN", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_nx_inr_design")
	@Column(name = "NX_INR_DESIGN_ID")
	private Long nxInrDesignId;
	
	@Column(name = "CIRCUIT_ID")
	private String circuitId;
	
	@Column(name = "STATUS")
	private String status;
	
	@CreationTimestamp
	@Column(name = "CREATED_DATE")
	private Date createdDate;
	
	@UpdateTimestamp
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;
	
	@Column(name = "ACTIVE_YN")
	private String activeYN;
	
	@Column(name = "NX_SOLUTION_ID")
	private Long nxSolutionId;
	
	@Lob
	@Column(name = "FAILURE_DATA")
	private String failureData;
	
	@Column(name = "NX_REQUEST_GROUP_ID")
	private Long nxRequestGroupId;
	
	@OneToMany(targetEntity=NxInrDesignDetails.class, mappedBy = "nxInrDesign", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<NxInrDesignDetails> nxInrDesignDetails= new ArrayList<>(0);
	
	

	public NxInrDesignDetails addNxInrDesignDetails(NxInrDesignDetails nxInrDesignDetails) {
		getNxInrDesignDetails().add(nxInrDesignDetails);
		nxInrDesignDetails.setNxInrDesign(this);
		return nxInrDesignDetails;
	}
	
	public NxInrDesignDetails removeNxInrDesignDetails(NxInrDesignDetails nxInrDesignDetails) {
		getNxInrDesignDetails().remove(nxInrDesignDetails);
		nxInrDesignDetails.setNxInrDesign(null);
		return nxInrDesignDetails;
	}
	
	public NxInrDesign() {
		
	}

	/**
	 * @return the nxInrDesignId
	 */
	public Long getNxInrDesignId() {
		return nxInrDesignId;
	}

	/**
	 * @param nxInrDesignId the nxInrDesignId to set
	 */
	public void setNxInrDesignId(Long nxInrDesignId) {
		this.nxInrDesignId = nxInrDesignId;
	}

	/**
	 * @return the circuitId
	 */
	public String getCircuitId() {
		return circuitId;
	}

	/**
	 * @param circuitId the circuitId to set
	 */
	public void setCircuitId(String circuitId) {
		this.circuitId = circuitId;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the modifiedDate
	 */
	public Date getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	/**
	 * @return the activeYN
	 */
	public String getActiveYN() {
		return activeYN;
	}

	/**
	 * @param activeYN the activeYN to set
	 */
	public void setActiveYN(String activeYN) {
		this.activeYN = activeYN;
	}

	/**
	 * @return the nxSolutionId
	 */
	public Long getNxSolutionId() {
		return nxSolutionId;
	}

	/**
	 * @param nxSolutionId the nxSolutionId to set
	 */
	public void setNxSolutionId(Long nxSolutionId) {
		this.nxSolutionId = nxSolutionId;
	}

	/**
	 * @return the failureData
	 */
	public String getFailureData() {
		return failureData;
	}

	/**
	 * @param failureData the failureData to set
	 */
	public void setFailureData(String failureData) {
		this.failureData = failureData;
	}

	/**
	 * @return the nxInrDesignDetails
	 */
	public List<NxInrDesignDetails> getNxInrDesignDetails() {
		return nxInrDesignDetails;
	}

	/**
	 * @param nxInrDesignDetails the nxInrDesignDetails to set
	 */
	public void setNxInrDesignDetails(List<NxInrDesignDetails> nxInrDesignDetails) {
		this.nxInrDesignDetails = nxInrDesignDetails;
	}

	/**
	 * @return the nxRequestGroupId
	 */
	public Long getNxRequestGroupId() {
		return nxRequestGroupId;
	}

	/**
	 * @param nxRequestGroupId the nxRequestGroupId to set
	 */
	public void setNxRequestGroupId(Long nxRequestGroupId) {
		this.nxRequestGroupId = nxRequestGroupId;
	}
	
	
}
