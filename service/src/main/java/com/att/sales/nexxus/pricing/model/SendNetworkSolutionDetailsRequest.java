package com.att.sales.nexxus.pricing.model;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class SendNetworkSolutionDetailsRequest {
	private String submittingUser;
	private String solutionStatus;
	private String opportunityId;
	private String solutionId;
	private Long asrNum;
	private String salesChannel;
	private String erateIndicator;
	private String caseType;
	private String presaleExpediteIndicator;
	private String presalesExpediteDate;
	private String presalesExpediteComments;
	private String customerRequestedDueDate;
	private Long imsDealNumber;
	private Long imsVersionNumber;
	private String customerName;
	private User postsaleUser;
	private User salesUser;
	private String specialConstructionContractUrl;
	private String specialConstructionPaymentUrl;
	private List<Design> design;

	public String getSubmittingUser() {
		return submittingUser;
	}

	public void setSubmittingUser(String submittingUser) {
		this.submittingUser = submittingUser;
	}

	public String getSolutionStatus() {
		return solutionStatus;
	}

	public void setSolutionStatus(String solutionStatus) {
		this.solutionStatus = solutionStatus;
	}

	public String getOpportunityId() {
		return opportunityId;
	}

	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
	}

	public String getSolutionId() {
		return solutionId;
	}

	public void setSolutionId(String solutionId) {
		this.solutionId = solutionId;
	}

	public Long getAsrNum() {
		return asrNum;
	}

	public void setAsrNum(Long asrNum) {
		this.asrNum = asrNum;
	}

	public String getSalesChannel() {
		return salesChannel;
	}

	public void setSalesChannel(String salesChannel) {
		this.salesChannel = salesChannel;
	}

	public String getErateIndicator() {
		return erateIndicator;
	}

	public void setErateIndicator(String erateIndicator) {
		this.erateIndicator = erateIndicator;
	}

	public String getCaseType() {
		return caseType;
	}

	public void setCaseType(String caseType) {
		this.caseType = caseType;
	}

	public String getPresaleExpediteIndicator() {
		return presaleExpediteIndicator;
	}

	public void setPresaleExpediteIndicator(String presaleExpediteIndicator) {
		this.presaleExpediteIndicator = presaleExpediteIndicator;
	}

	public String getPresalesExpediteDate() {
		return presalesExpediteDate;
	}

	public void setPresalesExpediteDate(String presalesExpediteDate) {
		this.presalesExpediteDate = presalesExpediteDate;
	}

	public String getPresalesExpediteComments() {
		return presalesExpediteComments;
	}

	public void setPresalesExpediteComments(String presalesExpediteComments) {
		this.presalesExpediteComments = presalesExpediteComments;
	}

	public String getCustomerRequestedDueDate() {
		return customerRequestedDueDate;
	}

	public void setCustomerRequestedDueDate(String customerRequestedDueDate) {
		this.customerRequestedDueDate = customerRequestedDueDate;
	}

	public Long getImsDealNumber() {
		return imsDealNumber;
	}

	public void setImsDealNumber(Long imsDealNumber) {
		this.imsDealNumber = imsDealNumber;
	}

	public Long getImsVersionNumber() {
		return imsVersionNumber;
	}

	public void setImsVersionNumber(Long imsVersionNumber) {
		this.imsVersionNumber = imsVersionNumber;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public User getPostsaleUser() {
		return postsaleUser;
	}

	public void setPostsaleUser(User postsaleUser) {
		this.postsaleUser = postsaleUser;
	}

	public User getSalesUser() {
		return salesUser;
	}

	public void setSalesUser(User salesUser) {
		this.salesUser = salesUser;
	}

	public String getSpecialConstructionContractUrl() {
		return specialConstructionContractUrl;
	}

	public void setSpecialConstructionContractUrl(String specialConstructionContractUrl) {
		this.specialConstructionContractUrl = specialConstructionContractUrl;
	}

	public String getSpecialConstructionPaymentUrl() {
		return specialConstructionPaymentUrl;
	}

	public void setSpecialConstructionPaymentUrl(String specialConstructionPaymentUrl) {
		this.specialConstructionPaymentUrl = specialConstructionPaymentUrl;
	}

	public List<Design> getDesign() {
		return design;
	}

	public void setDesign(List<Design> design) {
		this.design = design;
	}

}
