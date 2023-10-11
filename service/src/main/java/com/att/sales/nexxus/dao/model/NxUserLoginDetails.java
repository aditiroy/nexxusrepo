package com.att.sales.nexxus.dao.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;

import com.att.sales.nexxus.userdetails.model.AddUserRequest;
import com.att.sales.nexxus.userdetails.model.CheckAccessRequest;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "NX_USER_LOGIN_DETAILS")
@Getter
@Setter
public class NxUserLoginDetails implements Serializable {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	private static final String LOGIN = "Login";
	private static final String ADD_USER = "Add User";
	private static final String MODIFI_USER = "Modify User";

	@Id
	@SequenceGenerator(name = "SEQUENCE_NX_USER_LOGIN_DETAILS", sequenceName = "SEQ_NX_USER_LOGIN_DETAILS_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQUENCE_NX_USER_LOGIN_DETAILS")
	@Column(name = "USER_LOGIN_DETAIL_ID")
	private Long userLoginDetailId;

	@Column(name = "DATE_TIME")
	private Date dateTime = new Date();

	@Column(name = "EVENT_TYPE")
	private String eventType;

	@Column(name = "PROTOCOL")
	private String protocol = "HTTP";

	@Column(name = "STATUS")
	private String status;

	@Column(name = "EVENT_SOURCE")
	private String eventSource = "Web GUI";

	@Column(name = "SRC")
	private String src = "NONE";

	@Column(name = "USER_NAME")
	private String userName;

	@Column(name = "ATTUID")
	private String attuid;

	protected NxUserLoginDetails() {

	}

	public NxUserLoginDetails(CheckAccessRequest checkAccessRequest) {
		eventType = LOGIN;
		userName = checkAccessRequest.getAttuid();
		attuid = checkAccessRequest.getAttuid();
		if (StringUtils.isNotEmpty(checkAccessRequest.getProtocol())) {
			protocol = checkAccessRequest.getProtocol();
		}
		if (StringUtils.isNotEmpty(checkAccessRequest.getEventSource())) {
			eventSource = checkAccessRequest.getEventSource();
		}
		if (StringUtils.isNotEmpty(checkAccessRequest.getSrc())) {
			src = checkAccessRequest.getSrc();
		}
		if (StringUtils.isNotEmpty(checkAccessRequest.getUserName())) {
			userName = checkAccessRequest.getUserName();
		}
	}

	public NxUserLoginDetails(AddUserRequest addUserRequest) {
		String userFullName = ((	
			addUserRequest.getFirstName() == null ? ""
				: addUserRequest.getFirstName()) + " " + (addUserRequest.getLastName() == null ? ""
						: addUserRequest.getLastName())).trim();
		attuid = addUserRequest.getAttuid();
		eventType = addUserRequest.getRequestType();
		if (StringUtils.isNotEmpty(addUserRequest.getProtocol())) {
			protocol = addUserRequest.getProtocol();
		}
		if (StringUtils.isNotEmpty(addUserRequest.getEventSource())) {
			eventSource = addUserRequest.getEventSource();
		}
		if (StringUtils.isNotEmpty(addUserRequest.getSrc())) {
			src = addUserRequest.getSrc();
		}
		if (StringUtils.isNotEmpty(userFullName)) {
			userName = userFullName;
		}
	}
}
