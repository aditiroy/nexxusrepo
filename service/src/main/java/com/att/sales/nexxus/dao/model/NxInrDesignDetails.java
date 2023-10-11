package com.att.sales.nexxus.dao.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "NX_INR_DESIGN_DETAILS")
@Getter
@Setter
public class NxInrDesignDetails implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name = "sequence_nx_inr_design_details", sequenceName = "SEQ_NX_INR_DESIGN_DETAILS", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_nx_inr_design_details")
	@Column(name = "NX_INR_DESIGN_DETAILS_ID")
	private Long nxInrDesignDetailsId;
	
	@Column(name = "DESIGN_DATA")
	private String designData;
	
	@Column(name = "PRODUCT")
	private String product;
	
	@Column(name = "SUB_PRODUCT")
	private String subProduct;
	
	@CreationTimestamp
	@Column(name = "CREATED_DATE")
	private Date createdDate;
	
	@UpdateTimestamp
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;
	
	@Column(name = "ACTIVE_YN")
	private String activeYN;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NX_INR_DESIGN_ID")
	@NotFound ( action = NotFoundAction.IGNORE )
	private NxInrDesign nxInrDesign;
	
	@Lob
	@Column(name = "FAILURE_DATA")
	private String failureData;
	
	@Column(name = "NX_REQ_ID")
	private Long nxReqId;
	
	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "NX_COUNT_THRESHOLD")
	private String nxCountThreshold;
	
	@Column(name = "USAGE_CATEGORY")
	private String usageCategory;

	public NxInrDesignDetails() {
		
	}

}
