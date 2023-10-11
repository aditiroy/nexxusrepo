package com.att.sales.nexxus.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class Report_Key.
 */
public class Report_Key {

	
	/** The Duns number. */
	private String Duns_number;
	
	/** The L 5 master acct id. */
	private String L5_master_acct_id;
	
	/** The L 3 sub acct id. */
	private String L3_sub_acct_id;
	
	/** The L 4 acct id. */
	private String L4_acct_id;
	
	/** The main acct number. */
	private String main_acct_number;
	
	private String mcn;
	
	private String Cust_acct_nbr;
	
	private String Lead_acct_nbr;
	
	private String SVID;
	
	@JsonProperty("SVID")
	public String getSVID() {
		return SVID;
	}

	public void setSVID(String sVID) {
		SVID = sVID;
	}

	/**
	 * Gets the duns number.
	 *
	 * @return the duns number
	 */
	@JsonProperty("Duns_number")
	public String getDuns_number() {
		return Duns_number;
	}
	
	/**
	 * Sets the duns number.
	 *
	 * @param duns_number the new duns number
	 */
	public void setDuns_number(String duns_number) {
		Duns_number = duns_number;
	}
	
	/**
	 * Gets the l 5 master acct id.
	 *
	 * @return the l 5 master acct id
	 */
	@JsonProperty("L5_master_acct_id")
	public String getL5_master_acct_id() {
		return L5_master_acct_id;
	}
	
	/**
	 * Sets the l 5 master acct id.
	 *
	 * @param l5_master_acct_id the new l 5 master acct id
	 */
	public void setL5_master_acct_id(String l5_master_acct_id) {
		L5_master_acct_id = l5_master_acct_id;
	}
	
	/**
	 * Gets the l 3 sub acct id.
	 *
	 * @return the l 3 sub acct id
	 */
	@JsonProperty("L3_sub_acct_id")
	public String getL3_sub_acct_id() {
		return L3_sub_acct_id;
	}
	
	/**
	 * Sets the l 3 sub acct id.
	 *
	 * @param l3_sub_acct_id the new l 3 sub acct id
	 */
	public void setL3_sub_acct_id(String l3_sub_acct_id) {
		L3_sub_acct_id = l3_sub_acct_id;
	}
	
	/**
	 * Gets the l 4 acct id.
	 *
	 * @return the l 4 acct id
	 */
	@JsonProperty("L4_acct_id")
	public String getL4_acct_id() {
		return L4_acct_id;
	}
	
	/**
	 * Sets the l 4 acct id.
	 *
	 * @param l4_acct_id the new l 4 acct id
	 */
	public void setL4_acct_id(String l4_acct_id) {
		L4_acct_id = l4_acct_id;
	}
	
	/**
	 * Gets the main acct number.
	 *
	 * @return the main acct number
	 */
	@JsonProperty("main_acct_number")
	public String getMain_acct_number() {
		return main_acct_number;
	}
	
	/**
	 * Sets the main acct number.
	 *
	 * @param main_acct_number the new main acct number
	 */
	public void setMain_acct_number(String main_acct_number) {
		this.main_acct_number = main_acct_number;
	}

	/**
	 * @return the mcn
	 */
	public String getMcn() {
		return mcn;
	}

	/**
	 * @param mcn the mcn to set
	 */
	public void setMcn(String mcn) {
		this.mcn = mcn;
	}

	@JsonProperty("Cust_acct_nbr")
	public String getCust_acct_nbr() {
		return Cust_acct_nbr;
	}

	public void setCust_acct_nbr(String cust_acct_nbr) {
		Cust_acct_nbr = cust_acct_nbr;
	}

	@JsonProperty("Lead_acct_nbr")
	public String getLead_acct_nbr() {
		return Lead_acct_nbr;
	}

	public void setLead_acct_nbr(String lead_acct_nbr) {
		Lead_acct_nbr = lead_acct_nbr;
	}
	
	
}
