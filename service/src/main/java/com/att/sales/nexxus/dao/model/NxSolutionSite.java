package com.att.sales.nexxus.dao.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "NX_SOLUTION_SITE")
@Getter
@Setter
@NoArgsConstructor
public class NxSolutionSite implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name = "sequence_nx_solution_site", sequenceName = "SEQ_NX_SOLUTION_SITE", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_nx_solution_site")
	@Column(name = "ID")
	private Long id;

	@Column(name = "NX_SOLUTION_ID")
	private Long nxSolutionId;
	
	@Lob
	@Column(name = "SITE_ADDRESS")
	private String siteAddress;
	
	@CreationTimestamp
	@Column(name = "CREATED_DATE")
	private Date createdDate;
	
	@UpdateTimestamp
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;
	
	@Column(name = "ACTIVE_YN")
	private String activeYN;
	
	@Column(name = "NX_REQUEST_GROUP_ID")
	private Long nxRequestGroupId;
	
	@Column(name = "NX_REQ_ID")
	private Long nxReqId;

}
