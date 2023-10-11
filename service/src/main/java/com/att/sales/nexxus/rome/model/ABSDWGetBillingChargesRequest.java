package com.att.sales.nexxus.rome.model;

public class ABSDWGetBillingChargesRequest {

	/** The requestType. */
	private String requestType;

	/** The keyFieldID. */
	private String keyFieldID;

	/** The billDate. */
	private String billDate;

	/** The refNB. */
	private String refNB;

	/** The mcnNB. */
	private String mcnNB;
	
	/** The svID. */
	private String svID;

	/**
	 * @return the requestType
	 */
	public String getRequestType() {
		return requestType;
	}

	/**
	 * @param requestType the requestType to set
	 */
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	/**
	 * @return the keyFieldID
	 */
	public String getKeyFieldID() {
		return keyFieldID;
	}

	/**
	 * @param keyFieldID the keyFieldID to set
	 */
	public void setKeyFieldID(String keyFieldID) {
		this.keyFieldID = keyFieldID;
	}

	/**
	 * @return the billDate
	 */
	public String getBillDate() {
		return billDate;
	}

	/**
	 * @param billDate the billDate to set
	 */
	public void setBillDate(String billDate) {
		this.billDate = billDate;
	}

	/**
	 * @return the refNB
	 */
	public String getRefNB() {
		return refNB;
	}

	/**
	 * @param refNB the refNB to set
	 */
	public void setRefNB(String refNB) {
		this.refNB = refNB;
	}

	/**
	 * @return the mcnNB
	 */
	public String getMcnNB() {
		return mcnNB;
	}

	/**
	 * @param mcnNB the mcnNB to set
	 */
	public void setMcnNB(String mcnNB) {
		this.mcnNB = mcnNB;
	}

	public String getSvID() {
		return svID;
	}

	public void setSvID(String svID) {
		this.svID = svID;
	}
}
