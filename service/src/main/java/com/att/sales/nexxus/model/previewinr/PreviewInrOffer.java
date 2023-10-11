package com.att.sales.nexxus.model.previewinr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.att.sales.nexxus.output.entity.NxAvpnOutputBean;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The Class PreviewInrOffer.
 *
 * @author sn973r
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PreviewInrOffer implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The service. */
	private String service;
	
	/** The customer name. */
	private String customerName;
	
	/** The search criteria. */
	private String searchCriteria;
	
	/** The search criteria value. */
	private String searchCriteriaValue;
	
	/**
	 * Gets the service.
	 *
	 * @return the service
	 */
	public String getService() {
		return service;
	}

	/**
	 * Sets the service.
	 *
	 * @param service the new service
	 */
	public void setService(String service) {
		this.service = service;
	}

	/**
	 * Gets the customer name.
	 *
	 * @return the customer name
	 */
	public String getCustomerName() {
		return customerName;
	}

	/**
	 * Sets the customer name.
	 *
	 * @param customerName the new customer name
	 */
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	/**
	 * Gets the search criteria.
	 *
	 * @return the search criteria
	 */
	public String getSearchCriteria() {
		return searchCriteria;
	}

	/**
	 * Sets the search criteria.
	 *
	 * @param searchCriteria the new search criteria
	 */
	public void setSearchCriteria(String searchCriteria) {
		this.searchCriteria = searchCriteria;
	}

	/**
	 * Gets the search criteria value.
	 *
	 * @return the search criteria value
	 */
	public String getSearchCriteriaValue() {
		return searchCriteriaValue;
	}

	/**
	 * Sets the search criteria value.
	 *
	 * @param searchCriteriaValue the new search criteria value
	 */
	public void setSearchCriteriaValue(String searchCriteriaValue) {
		this.searchCriteriaValue = searchCriteriaValue;
	}

	
	/** The account details. */
	@JsonInclude(Include.NON_EMPTY)	
	private List<PreviewInraccountDetails> accountDetails;	
	
	
	/**
	 * Instantiates a new preview inr offer.
	 */
	public PreviewInrOffer() {
		
		accountDetails=new ArrayList<>();

	}
	
	/**
	 * Gets the account details.
	 *
	 * @return the account details
	 */
	public List<PreviewInraccountDetails> getAccountDetails() {
		return accountDetails;
	}

	/**
	 * Sets the account details.
	 *
	 * @param accountDetails the new account details
	 */
	public void setAccountDetails(List<PreviewInraccountDetails> accountDetails) {
		this.accountDetails = accountDetails;
	}
		
	
}
