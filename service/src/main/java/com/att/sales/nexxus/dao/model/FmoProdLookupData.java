package com.att.sales.nexxus.dao.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The Class FmoProdLookupData.
 *
 * @author vt393d
 */
@Entity
@Table(name="FMO_PROD_LOOKUP_DATA")
public class FmoProdLookupData {
	
	/** The product rate id. */
	@Id
	@Column(name="PRODUCTRATEID")
	private String productRateId;
	
	/** The rate description. */
	@Column(name="RATEDESCRIPTION")
	private String rateDescription;
	
	/** The rate group. */
	@Column(name="RATEGROUP")
	private String rateGroup;
	
	/** The rate type. */
	@Column(name="RATETYPE")
	private String rateType;
	
	/** The rate. */
	@Column(name="RATE")
	private String rate;
	
	/** The ims 2 code. */
	@Column(name="IMS2CODE")
	private String ims2Code;
	
	/** The speed. */
	@Column(name="SPEED")
	private String speed;
	
	/** The port type. */
	@Column(name="PORTTYPE")
	private String portType;
	
	/** The unit of speed. */
	@Column(name="UNITOFSPEED")
	private String unitOfSpeed;
	
	/** The active. */
	@Column(name="ACTIVE")
	private String active;

	/**
	 * Gets the product rate id.
	 *
	 * @return the product rate id
	 */
	public String getProductRateId() {
		return productRateId;
	}

	/**
	 * Sets the product rate id.
	 *
	 * @param productRateId the new product rate id
	 */
	public void setProductRateId(String productRateId) {
		this.productRateId = productRateId;
	}

	/**
	 * Gets the rate description.
	 *
	 * @return the rate description
	 */
	public String getRateDescription() {
		return rateDescription;
	}

	/**
	 * Sets the rate description.
	 *
	 * @param rateDescription the new rate description
	 */
	public void setRateDescription(String rateDescription) {
		this.rateDescription = rateDescription;
	}

	/**
	 * Gets the rate group.
	 *
	 * @return the rate group
	 */
	public String getRateGroup() {
		return rateGroup;
	}

	/**
	 * Sets the rate group.
	 *
	 * @param rateGroup the new rate group
	 */
	public void setRateGroup(String rateGroup) {
		this.rateGroup = rateGroup;
	}

	/**
	 * Gets the rate type.
	 *
	 * @return the rate type
	 */
	public String getRateType() {
		return rateType;
	}

	/**
	 * Sets the rate type.
	 *
	 * @param rateType the new rate type
	 */
	public void setRateType(String rateType) {
		this.rateType = rateType;
	}

	/**
	 * Gets the rate.
	 *
	 * @return the rate
	 */
	public String getRate() {
		return rate;
	}

	/**
	 * Sets the rate.
	 *
	 * @param rate the new rate
	 */
	public void setRate(String rate) {
		this.rate = rate;
	}

	/**
	 * Gets the ims 2 code.
	 *
	 * @return the ims 2 code
	 */
	public String getIms2Code() {
		return ims2Code;
	}

	/**
	 * Sets the ims 2 code.
	 *
	 * @param ims2Code the new ims 2 code
	 */
	public void setIms2Code(String ims2Code) {
		this.ims2Code = ims2Code;
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
	 * Gets the port type.
	 *
	 * @return the port type
	 */
	public String getPortType() {
		return portType;
	}

	/**
	 * Sets the port type.
	 *
	 * @param portType the new port type
	 */
	public void setPortType(String portType) {
		this.portType = portType;
	}

	/**
	 * Gets the unit of speed.
	 *
	 * @return the unit of speed
	 */
	public String getUnitOfSpeed() {
		return unitOfSpeed;
	}

	/**
	 * Sets the unit of speed.
	 *
	 * @param unitOfSpeed the new unit of speed
	 */
	public void setUnitOfSpeed(String unitOfSpeed) {
		this.unitOfSpeed = unitOfSpeed;
	}

	/**
	 * Gets the active.
	 *
	 * @return the active
	 */
	public String getActive() {
		return active;
	}

	/**
	 * Sets the active.
	 *
	 * @param active the new active
	 */
	public void setActive(String active) {
		this.active = active;
	}
	
	
}
