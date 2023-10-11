package com.att.sales.nexxus.dao.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.att.sales.nexxus.dao.repository.NxDesignStatusJsonType;
import com.att.sales.nexxus.nxPEDstatus.model.SolutionDetails;

/**
 * The Class NxDesignDetails.
 */
@Entity
@Table(name = "NX_DESIGN_DETAILS")
public class NxDesignDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "sequence_nx_design_details", sequenceName = "SEQ_NX_DESIGN_DTLS_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_nx_design_details")
	@Column(name = "NX_DESIGN_DETAILS_ID")
	private Long nxDesignId;

	@Column(name = "DESIGN_DATA")
	private String designData;

	@Column(name = "DESIGN_STATUS")
	@Convert(converter = NxDesignStatusJsonType.class)
	private SolutionDetails designStatus = new SolutionDetails();

	@ManyToOne
	@JoinColumn(name = "NX_DESIGN_ID")
	private NxDesign nxDesign;

	@Column(name = "CREATED_DATE")
	private Date createdDate = new Date();

	@Column(name = "MODIFIED_DATE")
	private Date modifedDate = new Date();
	
	@Column(name = "component_id")
	private String componentId;

	@Column(name = "type")
	private String type;
	
	@Column(name = "PRODUCT_NAME")
	private String productName;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "REST_RESPONSE_ERROR")
	private String restResponseError;

	/** The consolidation criteria */
	@Column(name = "CONSOLIDATION_CRITERIA")
	private String consolidationCriteria;

	/** The consolidation criteria */
	@Column(name = "CONSOLIDATION_CRITERIA_DATA")
	private String consolidationCriteriaData;
	
	public Long getNxDesignId() {
		return nxDesignId;
	}

	public void setNxDesignId(Long nxDesignId) {
		this.nxDesignId = nxDesignId;
	}
	
	public String getDesignData() {
		return designData;
	}

	public void setDesignData(String designData) {
		this.designData = designData;
	}

	public NxDesign getNxDesign() {
		return nxDesign;
	}

	public void setNxDesign(NxDesign nxDesign) {
		this.nxDesign = nxDesign;
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

	public SolutionDetails getDesignStatus() {
		return designStatus;
	}

	public void setDesignStatus(SolutionDetails designStatus) {
		this.designStatus = designStatus;
	}

	public String getComponentId() {
		return componentId;
	}

	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRestResponseError() {
		return restResponseError;
	}

	public void setRestResponseError(String restResponseError) {
		this.restResponseError = restResponseError;
	}

	public String getConsolidationCriteria() {
		return consolidationCriteria;
	}

	public void setConsolidationCriteria(String consolidationCriteria) {
		this.consolidationCriteria = consolidationCriteria;
	}

	public String getConsolidationCriteriaData() {
		return consolidationCriteriaData;
	}

	public void setConsolidationCriteriaData(String consolidationCriteriaData) {
		this.consolidationCriteriaData = consolidationCriteriaData;
	}
}
