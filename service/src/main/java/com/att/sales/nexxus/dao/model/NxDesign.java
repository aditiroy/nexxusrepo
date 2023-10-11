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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;

/**
 * The Class NxDesign.
 */
@Entity
@Table(name = "NX_DESIGN")
public class NxDesign implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "sequence_nx_design", sequenceName = "SEQ_NX_DESIGN_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_nx_design")
	@Column(name = "NX_DESIGN_ID")
	private Long nxDesignId;
	
	@Column(name = "ASR_ITEM_ID")
	private String asrItemId;
	
	@Column(name = "BUNDLE_CODE")
	private String bundleCd;
	
	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "FIRST_NAME")
	private String fisrtName;
	
	@Column(name = "LAST_NAME")
	private String lastName;
	
	@Column(name = "ATTUID")
	private String attuId;
	
	@Column(name = "CREATED_DATE")
	private Date createdDate = new Date();
	
	@Column(name = "MODIFIED_DATE")
	private Date modifedDate = new Date();
	
	@Column(name = "DESIGN_VERSION")
	private Long designVersion;
	
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "NX_SOLUTION_ID")
	private NxSolutionDetail nxSolutionDetail;
	
	
	@OneToMany(mappedBy = "nxDesign",fetch = FetchType.EAGER,cascade=CascadeType.ALL)
	private List<NxDesignDetails> nxDesignDetails = new ArrayList<>();
	
	@Column(name = "SITE_ID")
	private Long siteId;
	
	@Column(name = "COUNTRY")
	private String country;
	
	@Column(name = "DESIGN_TYPE")
	private String designType;
	
	@Column(name = "CIRCUIT_TD")
	private String circuitId;
	
	@Column(name = "SUBMIT_TO_MP")
	private String submitToMp;


	public Long getNxDesignId() {
		return nxDesignId;
	}

	public void setNxDesignId(Long nxDesignId) {
		this.nxDesignId = nxDesignId;
	}
	public String getAsrItemId() {
		return asrItemId;
	}

	public void setAsrItemId(String asrItemId) {
		this.asrItemId = asrItemId;
	}

	public String getBundleCd() {
		return bundleCd;
	}

	public void setBundleCd(String bundleCd) {
		this.bundleCd = bundleCd;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFisrtName() {
		return fisrtName;
	}

	public void setFisrtName(String fisrtName) {
		this.fisrtName = fisrtName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAttuId() {
		return attuId;
	}

	public void setAttuId(String attuId) {
		this.attuId = attuId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifedDate() {
		return modifedDate;
	}

	public void setModifedDate(Date modifedDate) {
		this.modifedDate = modifedDate;
	}

	public NxSolutionDetail getNxSolutionDetail() {
		return nxSolutionDetail;
	}

	public void setNxSolutionDetail(NxSolutionDetail nxSolutionDetail) {
		this.nxSolutionDetail = nxSolutionDetail;
	}

	public List<NxDesignDetails> getNxDesignDetails() {
		return nxDesignDetails;
	}

	public void setNxDesignDetails(List<NxDesignDetails> nxDesignDetails) {
		this.nxDesignDetails = nxDesignDetails;
	}

	public NxDesignDetails addNxDesignDetails(NxDesignDetails nxDesignDetails) {
		getNxDesignDetails().add(nxDesignDetails);
		nxDesignDetails.setNxDesign(this);
		return nxDesignDetails;
	}
	
	public NxDesignDetails removeNxDesignDetails(NxDesignDetails nxDesignDetails) {
		getNxDesignDetails().remove(nxDesignDetails);
		nxDesignDetails.setNxDesign(null);
		return nxDesignDetails;
	}

	public Long getDesignVersion() {
		return designVersion;
	}

	public void setDesignVersion(Long designVersion) {
		this.designVersion = designVersion;
	}

	/**
	 * @return the siteId
	 */
	public Long getSiteId() {
		return siteId;
	}

	/**
	 * @param siteId the siteId to set
	 */
	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	public String getDesignType() {
		return designType;
	}

	public void setDesignType(String designType) {
		this.designType = designType;
	}

	public String getCircuitId() {
		return circuitId;
	}

	public void setCircuitId(String circuitId) {
		this.circuitId = circuitId;
	}

	public String getSubmitToMp() {
		return submitToMp;
	}

	public void setSubmitToMp(String submitToMp) {
		this.submitToMp = submitToMp;
	}
	
	
}
