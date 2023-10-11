package com.att.sales.nexxus.edf.model;

import java.util.Date;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;



/**
 * The Class PriceInventoryDataRequest.
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
public class PriceInventoryDataRequest {
	
	
	/** The customer name. */
	private String customerName;
	
	/** The duns number. */
	private String dunsNumber;
	
	/** The mcn. */
	private String mcn;
	
	/** The global utl duns nbr. */
	private String globalUtlDunsNbr;
	
	/** The l 4 acct id. */
	private String l4AcctId;
	
	/** The l 5 master acct id. */
	private String l5MasterAcctId;
	
	/** The l 3 sub acct id. */
	private String l3SubAcctId;
	
	/** The begin bill month. */
	private String beginBillMonth;
	
	/** The bill month. */
	private String billMonth;
	
	/** The main acct number. */
	private String mainAcctNumber;
	
	/** The product. */
	@JsonProperty("product")	
	private String product;
	
	private String productType;
	
	private String custAcctNbr;
	
	private String leadAcctNbr;
	
	@JsonProperty("SVID")	
	private String SVID;
	

	public String getSVID() {
		return SVID;
	}

	public void setSVID(String sVID) {
		SVID = sVID;
	}

	/**
	 * Gets the customer name.
	 *
	 * @return the customer name
	 */
	@JsonProperty("customerName")
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
	@JsonProperty("dunsNumber")
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
	 * Gets the mcn.
	 *
	 * @return the mcn
	 */
	@JsonProperty("mcn")
	public String getMcn() {
		return mcn;
	}
	
	/**
	 * Sets the mcn.
	 *
	 * @param mcn the new mcn
	 */
	public void setMcn(String mcn) {
		this.mcn = mcn;
	}
	
	/**
	 * Gets the global utl duns nbr.
	 *
	 * @return the global utl duns nbr
	 */
	@JsonProperty("globalUtlDunsNbr")
	public String getGlobalUtlDunsNbr() {
		return globalUtlDunsNbr;
	}
	
	/**
	 * Sets the global utl duns nbr.
	 *
	 * @param globalUtlDunsNbr the new global utl duns nbr
	 */
	public void setGlobalUtlDunsNbr(String globalUtlDunsNbr) {
		this.globalUtlDunsNbr = globalUtlDunsNbr;
	}
	
	/**
	 * Gets the l 4 acct id.
	 *
	 * @return the l 4 acct id
	 */
	@JsonProperty("l4AcctId")
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
	 * Gets the l 5 master acct id.
	 *
	 * @return the l 5 master acct id
	 */
	@JsonProperty("l5MasterAcctId")
	public String getL5MasterAcctId() {
		return l5MasterAcctId;
	}
	
	/**
	 * Sets the l 5 master acct id.
	 *
	 * @param l5MasterAcctId the new l 5 master acct id
	 */
	public void setL5MasterAcctId(String l5MasterAcctId) {
		this.l5MasterAcctId = l5MasterAcctId;
	}
	
	/**
	 * Gets the l 3 sub acct id.
	 *
	 * @return the l 3 sub acct id
	 */
	@JsonProperty("l3SubAcctId")
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
	 * Gets the begin bill month.
	 *
	 * @return the begin bill month
	 */
	public String getBeginBillMonth() {
		return beginBillMonth;
	}
	
	/**
	 * Sets the begin bill month.
	 *
	 * @param beginBillMonth the new begin bill month
	 */
	public void setBeginBillMonth(String beginBillMonth) {
		this.beginBillMonth = beginBillMonth;
	}
	
	/**
	 * Gets the bill month.
	 *
	 * @return the bill month
	 */
	@JsonProperty("billMonth")
	public String getBillMonth() {
		return billMonth;
	}
	
	/**
	 * Sets the bill month.
	 *
	 * @param billMonth the new bill month
	 */
	public void setBillMonth(String billMonth) {
		this.billMonth = billMonth;
	}
	
	/**
	 * Gets the product.
	 *
	 * @return the product
	 */
	public String getProduct() {
		return product;
	}
	
	/**
	 * Sets the product.
	 *
	 * @param product the new product
	 */
	public void setProduct(String product) {
		this.product = product;
	}
	
	/**
	 * Gets the main acct number.
	 *
	 * @return the main acct number
	 */
	public String getMainAcctNumber() {
		return mainAcctNumber;
	}
	
	/**
	 * Sets the main acct number.
	 *
	 * @param mainAcctNumber the new main acct number
	 */
	public void setMainAcctNumber(String mainAcctNumber) {
		this.mainAcctNumber = mainAcctNumber;
	}

	/**
	 * @return the productType
	 */
	public String getProductType() {
		return productType;
	}

	/**
	 * @param productType the productType to set
	 */
	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getCustAcctNbr() {
		return custAcctNbr;
	}

	public void setCustAcctNbr(String custAcctNbr) {
		this.custAcctNbr = custAcctNbr;
	}

	public String getLeadAcctNbr() {
		return leadAcctNbr;
	}

	public void setLeadAcctNbr(String leadAcctNbr) {
		this.leadAcctNbr = leadAcctNbr;
	}

}
