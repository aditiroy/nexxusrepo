package com.att.sales.nexxus.output.entity;

/**
 * The Class NxDs3Access.
 *
 * @author vt393d
 */
public class NxDsAccessBean extends NxBaseOutputBean{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6933553493264482098L;
	
	/** The npa nxx. */
	private String npaNxx;
	
	/** The cust srvg wire ctr CLLI cd. */
	private String custSrvgWireCtrCLLICd;
	
	/** The speed. */
	private String speed;
	
	/** The mileage. */
	private String mileage;

	/**
	 * Gets the npa nxx.
	 *
	 * @return the npa nxx
	 */
	public String getNpaNxx() {
		return npaNxx;
	}

	/**
	 * Sets the npa nxx.
	 *
	 * @param npaNxx the new npa nxx
	 */
	public void setNpaNxx(String npaNxx) {
		this.npaNxx = npaNxx;
	}

	/**
	 * Gets the cust srvg wire ctr CLLI cd.
	 *
	 * @return the cust srvg wire ctr CLLI cd
	 */
	public String getCustSrvgWireCtrCLLICd() {
		return custSrvgWireCtrCLLICd;
	}

	/**
	 * Sets the cust srvg wire ctr CLLI cd.
	 *
	 * @param custSrvgWireCtrCLLICd the new cust srvg wire ctr CLLI cd
	 */
	public void setCustSrvgWireCtrCLLICd(String custSrvgWireCtrCLLICd) {
		this.custSrvgWireCtrCLLICd = custSrvgWireCtrCLLICd;
	}

	/**
	 * Gets the speed.
	 *
	 * @return the speed
	 */
	public String getSpeed() {
		return speed;
	}

	/**
	 * Sets the speed.
	 *
	 * @param speed the new speed
	 */
	public void setSpeed(String speed) {
		this.speed = speed;
	}

	/**
	 * Gets the mileage.
	 *
	 * @return the mileage
	 */
	public String getMileage() {
		return mileage;
	}

	/**
	 * Sets the mileage.
	 *
	 * @param mileage the new mileage
	 */
	public void setMileage(String mileage) {
		this.mileage = mileage;
	}
	
	

}
