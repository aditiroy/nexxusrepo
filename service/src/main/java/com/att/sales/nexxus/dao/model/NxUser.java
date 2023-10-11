package com.att.sales.nexxus.dao.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "NX_USER")
@Getter
@Setter
public class NxUser implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "USER_ATT_ID")
	private String userAttId;
	
	@Column(name = "ACTIVE")
	private String active;
	
	@Column(name = "APPLICATION_USER_ID")
	private String applicationUserId;
	
	@Column(name = "FIRST_NAME")
	private String firstName;
	
	@Column(name = "MIDDLE_INITIAL")
	private String middleInitial;
	
	@Column(name = "LAST_NAME")
	private String lastName;
	
	@Column(name = "EMAIL")
	private String email;
	
	@Column(name = "LAST_LOGON_DATE")
	private Date lastLogonDate;
	
	@Column(name = "REQUESTOR_ATTUID")
	private String requestorAttuid;
	
	@ManyToOne
	@JoinColumn(name = "PROFILE_ID")
	private NxProfiles nxProfiles;
	
	@Column(name = "ADDRESS")
	private String address;
	
	@Column(name = "STATE")
	private String state;
	
	@Column(name = "CITY")
	private String city;
	
	@Column(name = "POSTAL_CODE")
	private String postalCode;
	
	@Column(name = "RESOURCE_TYPE")
	private String resourceType;
	
	@Column(name = "PHONE")
	private String phone;
	
	@Column(name = "MANAGER_ATTUID")
	private String managerAttuid;
	
	@Column(name = "APPROVER_ATTUID")
	private String approverAttuid;
	
	@Column(name = "APPROVAL_DATE")
	private Date approvalDate;

}
