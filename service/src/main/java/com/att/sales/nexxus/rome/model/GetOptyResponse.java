package com.att.sales.nexxus.rome.model;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.model.ServiceResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;



/**
 * The Class GetOptyResponse.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class GetOptyResponse  extends ServiceResponse{
	
	/** The opty id. */
	private String optyId;	
		
	/** The customer name. */
	private String customerName;
	
	private String mainPhone;
	

		/** The duns number. */
	private String dunsNumber;
	
	/** The gu duns number. */
	private String guDunsNumber;
	
	/** The l 4 acct id. */
	private String l4AcctId;
	
	/** The l 3 sub acct id. */
	private String l3SubAcctId;
	
	/** The nx solution id. */
	private Long nxSolutionId;
	
	/** The solution description. */
	private String solutionDescription;
	
	/** Body.OptyInfoResponse.ResponseParams.ListOfOpportunityPosition.OpportunityPosition.SalesRep where IsPrimaryMVG is 'N' */
	private List<String> salesRep;
	
	/** Body.OptyInfoResponse.ResponseParams.Name */
	private String name;
	
	/** Body.OptyInfoResponse.ResponseParams.AccountId */
	private String accountId;
	
	/** Body.OptyInfoResponse.ResponseParams.ListOfAbsAccount.AbsAccount.SubAccountID */
	private String subAccountID;
	
	/** Body.OptyInfoResponse.ResponseParams.CurrencyCode */
	private String currencyCode;
	
	/** Body.OptyInfoResponse.ResponseParams.Offer */
	private String offer;
	
	/** Body.OptyInfoResponse.ResponseParams.Type */
	private String type;
	
	/** Body.OptyInfoResponse.ResponseParams.MarketStrataValue */
	private String marketStrataValue;
	
	private String absCreatedByName;
	
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String postalCode;
	private String country;
	private String primaryATTUID;
	private String primaryManager;
	private String primaryManagersManager;
	private String form470;
	
	/**
	 * Gets the opty id.
	 *
	 * @return the opty id
	 */
	public String getOptyId() {
		return optyId;
	}
	
	/**
	 * Sets the opty id.
	 *
	 * @param optyId the new opty id
	 */
	public void setOptyId(String optyId) {
		this.optyId = optyId;
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
	 * Gets the duns number.
	 *
	 * @return the duns number
	 */
	public String getDunsNumber() {
		return dunsNumber;
	}
	
	/**
	 * Sets the duns number.
	 *
	 * @param dunsNumber the new duns number
	 */
	public void setDunsNumber(String dunsNumber) {
		this.dunsNumber = dunsNumber;
	}
	
	/**
	 * Gets the gu duns number.
	 *
	 * @return the gu duns number
	 */
	public String getGuDunsNumber() {
		return guDunsNumber;
	}
	
	/**
	 * Sets the gu duns number.
	 *
	 * @param guDunsNumber the new gu duns number
	 */
	public void setGuDunsNumber(String guDunsNumber) {
		this.guDunsNumber = guDunsNumber;
	}
	
	/**
	 * Gets the l 4 acct id.
	 *
	 * @return the l 4 acct id
	 */
	public String getL4AcctId() {
		return l4AcctId;
	}
	
	/**
	 * Sets the l 4 acct id.
	 *
	 * @param l4AcctId the new l 4 acct id
	 */
	public void setL4AcctId(String l4AcctId) {
		this.l4AcctId = l4AcctId;
	}
	
	/**
	 * Gets the l 3 sub acct id.
	 *
	 * @return the l 3 sub acct id
	 */
	public String getL3SubAcctId() {
		return l3SubAcctId;
	}
	
	/**
	 * Sets the l 3 sub acct id.
	 *
	 * @param l3SubAcctId the new l 3 sub acct id
	 */
	public void setL3SubAcctId(String l3SubAcctId) {
		this.l3SubAcctId = l3SubAcctId;
	}
	
	/**
	 * Gets the nx solution id.
	 *
	 * @return the nx solution id
	 */
	public Long getNxSolutionId() {
		return nxSolutionId;
	}
	
	/**
	 * Sets the nx solution id.
	 *
	 * @param nxSolutionId the new nx solution id
	 */
	public void setNxSolutionId(Long nxSolutionId) {
		this.nxSolutionId = nxSolutionId;
	}
	
	/**
	 * Gets the solution description.
	 *
	 * @return the solution description
	 */
	public String getSolutionDescription() {
		return solutionDescription;
	}
	
	/**
	 * Sets the solution description.
	 *
	 * @param solutionDescription the new solution description
	 */
	public void setSolutionDescription(String solutionDescription) {
		this.solutionDescription = solutionDescription;
	}
	
	/**
	 * @return the salesRep
	 */
	public List<String> getSalesRep() {
		return salesRep;
	}
	
	/**
	 * @param salesRep the salesRep to set
	 */
	public void setSalesRep(List<String> salesRep) {
		this.salesRep = salesRep;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the accountId
	 */
	public String getAccountId() {
		return accountId;
	}
	
	/**
	 * @param accountId the accountId to set
	 */
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	
	/**
	 * @return the subAccountID
	 */
	public String getSubAccountID() {
		return subAccountID;
	}
	
	/**
	 * @param subAccountID the subAccountID to set
	 */
	public void setSubAccountID(String subAccountID) {
		this.subAccountID = subAccountID;
	}
	
	/**
	 * @return the currencyCode
	 */
	public String getCurrencyCode() {
		return currencyCode;
	}
	
	/**
	 * @param currencyCode the currencyCode to set
	 */
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	
	/**
	 * @return the offer
	 */
	public String getOffer() {
		return offer;
	}
	
	/**
	 * @param offer the offer to set
	 */
	public void setOffer(String offer) {
		this.offer = offer;
	}
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * @return the marketStrataValue
	 */
	public String getMarketStrataValue() {
		return marketStrataValue;
	}
	
	/**
	 * @param marketStrataValue the marketStrataValue to set
	 */
	public void setMarketStrataValue(String marketStrataValue) {
		this.marketStrataValue = marketStrataValue;
	}

	/**
	 * @return the absCreatedByName
	 */
	public String getAbsCreatedByName() {
		return absCreatedByName;
	}

	/**
	 * @param absCreatedByName the absCreatedByName to set
	 */
	public void setAbsCreatedByName(String absCreatedByName) {
		this.absCreatedByName = absCreatedByName;
	}

	/**
	 * @return the address1
	 */
	public String getAddress1() {
		return address1;
	}

	/**
	 * @param address1 the address1 to set
	 */
	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	/**
	 * @return the address2
	 */
	public String getAddress2() {
		return address2;
	}

	/**
	 * @param address2 the address2 to set
	 */
	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the postalCode
	 */
	public String getPostalCode() {
		return postalCode;
	}

	/**
	 * @param postalCode the postalCode to set
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
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

	/**
	 * @return the primaryATTUID
	 */
	public String getPrimaryATTUID() {
		return primaryATTUID;
	}

	/**
	 * @param primaryATTUID the primaryATTUID to set
	 */
	public void setPrimaryATTUID(String primaryATTUID) {
		this.primaryATTUID = primaryATTUID;
	}

	/**
	 * @return the primaryManager
	 */
	public String getPrimaryManager() {
		return primaryManager;
	}

	/**
	 * @param primaryManager the primaryManager to set
	 */
	public void setPrimaryManager(String primaryManager) {
		this.primaryManager = primaryManager;
	}

	/**
	 * @return the primaryManagersManager
	 */
	public String getPrimaryManagersManager() {
		return primaryManagersManager;
	}

	/**
	 * @param primaryManagersManager the primaryManagersManager to set
	 */
	public void setPrimaryManagersManager(String primaryManagersManager) {
		this.primaryManagersManager = primaryManagersManager;
	}

	public String getMainPhone() {
		return mainPhone;
	}

	public void setMainPhone(String mainPhone) {
		this.mainPhone = mainPhone;
	}

	public String getForm470() {
		return form470;
	}

	public void setForm470(String form470) {
		this.form470 = form470;
	}
}
