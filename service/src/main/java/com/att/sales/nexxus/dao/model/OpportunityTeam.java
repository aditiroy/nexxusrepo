package com.att.sales.nexxus.dao.model;

/**
*
*
* @author aa316k
*         
*/

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The Class OpportunityTeam.
 */
@Entity
@Table(name="NX_SOLUTION_DETAILS")
@NamedQueries({
		@NamedQuery(name = "OpportunityTeam.getSolutionData", query = "select s from OpportunityTeam s where s.solutionId= :solutionId") })


public class OpportunityTeam implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The solution id. */
	@Column(name="NX_SOLUTION_ID")
	@Id
	/*@SequenceGenerator(name="sequence",sequenceName="SEQ_NX_SOLUTION_ID", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="sequence")*/
	private Long solutionId;
	
	/** The opty id. */
	@Column(name="OPTY_ID")
	private	String optyId;
	
	
	/** The customer name. */
	@Column(name="CUSTOMER_NAME")
	private String customerName;
	
	/** The d UNS number. */
	@Column(name="DUNS_NUMBER")
	private String dUNSNumber;
	
	/** The global ultimate DUNS. */
	@Column(name="GU_DUNS_NUMBER")
	private String globalUltimateDUNS;
	
	/** The sub account ID. */
	@Column(name="L3_VALUE")
	private String subAccountID;
	
	/** The description. */
	@Column(name="NXS_DESCRIPTION")
	private String description;
	
	/** The created user. */
	@Column(name="CREATED_USER")
	private String createdUser;
	
	/** The created date. */
	@Column(name="CREATED_DATE")
	private Date createdDate;
	
	/** The modified user. */
	@Column(name="MODIFIED_USER")
	private String modifiedUser;
	
	/** The modified date. */
	@Column(name="MODIFIED_DATE")
	private Date modifiedDate;
	
	/** The active yn. */
	@Column(name="ACTIVE_YN")
	private String activeYn;
	
	/**
	 * Gets the opty id.
	 *
	 * @return the optyId
	 */
	public String getOptyId() {
		return optyId;
	}
	
	/**
	 * Sets the opty id.
	 *
	 * @param optyId the optyId to set
	 */
	public void setOptyId(String optyId) {
		this.optyId = optyId;
	}
	
	/**
	 * Gets the customer name.
	 *
	 * @return the customerName
	 */
	public String getCustomerName() {
		return customerName;
	}
	
	/**
	 * Sets the customer name.
	 *
	 * @param customerName the customerName to set
	 */
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	
	/**
	 * Gets the d UNS number.
	 *
	 * @return the dUNSNumber
	 */
	public String getdUNSNumber() {
		return dUNSNumber;
	}
	
	/**
	 * Sets the d UNS number.
	 *
	 * @param dUNSNumber the dUNSNumber to set
	 */
	public void setdUNSNumber(String dUNSNumber) {
		this.dUNSNumber = dUNSNumber;
	}
	
	/**
	 * Gets the global ultimate DUNS.
	 *
	 * @return the globalUltimateDUNS
	 */
	public String getGlobalUltimateDUNS() {
		return globalUltimateDUNS;
	}
	
	/**
	 * Sets the global ultimate DUNS.
	 *
	 * @param globalUltimateDUNS the globalUltimateDUNS to set
	 */
	public void setGlobalUltimateDUNS(String globalUltimateDUNS) {
		this.globalUltimateDUNS = globalUltimateDUNS;
	}
	
	/**
	 * Gets the sub account ID.
	 *
	 * @return the subAccountID
	 */
	public String getSubAccountID() {
		return subAccountID;
	}
	
	/**
	 * Sets the sub account ID.
	 *
	 * @param subAccountID the subAccountID to set
	 */
	public void setSubAccountID(String subAccountID) {
		this.subAccountID = subAccountID;
	}
	
	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Sets the description.
	 *
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Gets the created user.
	 *
	 * @return the createdUser
	 */
	public String getCreatedUser() {
		return createdUser;
	}
	
	/**
	 * Sets the created user.
	 *
	 * @param createdUser the createdUser to set
	 */
	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}
	
	/**
	 * Gets the created date.
	 *
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}
	
	/**
	 * Sets the created date.
	 *
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	/**
	 * Gets the modified user.
	 *
	 * @return the modifiedUser
	 */
	public String getModifiedUser() {
		return modifiedUser;
	}
	
	/**
	 * Sets the modified user.
	 *
	 * @param modifiedUser the modifiedUser to set
	 */
	public void setModifiedUser(String modifiedUser) {
		this.modifiedUser = modifiedUser;
	}
	
	/**
	 * Gets the modified date.
	 *
	 * @return the modifiedDate
	 */
	public Date getModifiedDate() {
		return modifiedDate;
	}
	
	/**
	 * Sets the modified date.
	 *
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	
	/**
	 * Gets the active yn.
	 *
	 * @return the activeYn
	 */
	public String getActiveYn() {
		return activeYn;
	}
	
	/**
	 * Sets the active yn.
	 *
	 * @param activeYn the activeYn to set
	 */
	public void setActiveYn(String activeYn) {
		this.activeYn = activeYn;
	}
	

	
	/**
	 * Gets the solution id.
	 *
	 * @return the solution id
	 */
	public Long getSolutionId() {
		return solutionId;
	}
	
	/**
	 * Sets the solution id.
	 *
	 * @param solutionId the new solution id
	 */
	public void setSolutionId(Long solutionId) {
		this.solutionId = solutionId;
	}
	
	

}
