package com.att.sales.nexxus.userdetails.model;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
//import org.codehaus.jackson.annotate.JsonTypeInfo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("userProvisionRequest")                                                                                         
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT ,use = JsonTypeInfo.Id.NAME)
public class AddUserRequest {
	private String myloginRequestNo;
	private String requestorAttuid;
	private String applicationName;
	private String attuid;
	private String address1;
	private String state;
	private String city;
	private String firstName;
	private String lastName;
	private String email;
	private String postalCode;
	private String resourceType;
	private String phone;
	private String costCenter;
	private String applicationProfile;
	private String requestType;
	private String managerAttuid;
	private String approverAttuid;
	private String approvalDate;
	private Date approvalDate_1;
	private String protocol;
	private String eventSource;
	private String src;

	public String getMyloginRequestNo() {
		return myloginRequestNo;
	}

	public void setMyloginRequestNo(String myloginRequestNo) {
		this.myloginRequestNo = myloginRequestNo;
	}

	public String getRequestorAttuid() {
		return requestorAttuid;
	}

	public void setRequestorAttuid(String requestorAttuid) {
		this.requestorAttuid = requestorAttuid;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getAttuid() {
		return attuid;
	}

	public void setAttuid(String attuid) {
		this.attuid = attuid;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCostCenter() {
		return costCenter;
	}

	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}

	public String getApplicationProfile() {
		return applicationProfile;
	}

	public void setApplicationProfile(String applicationProfile) {
		this.applicationProfile = applicationProfile;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getManagerAttuid() {
		return managerAttuid;
	}

	public void setManagerAttuid(String managerAttuid) {
		this.managerAttuid = managerAttuid;
	}

	public String getApproverAttuid() {
		return approverAttuid;
	}

	public void setApproverAttuid(String approverAttuid) {
		this.approverAttuid = approverAttuid;
	}

	public String getApprovalDate() {
		return approvalDate;
	}

	public void setApprovalDate(String approvalDate) {
		this.approvalDate = approvalDate;
	}

	public Date getApprovalDate_1() {
		return approvalDate_1;
	}

	public void setApprovalDate_1(Date approvalDate_1) {
		this.approvalDate_1 = approvalDate_1;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getEventSource() {
		return eventSource;
	}

	public void setEventSource(String eventSource) {
		this.eventSource = eventSource;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}
}
