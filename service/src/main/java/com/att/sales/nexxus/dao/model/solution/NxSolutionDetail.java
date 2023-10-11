package com.att.sales.nexxus.dao.model.solution;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.att.sales.nexxus.dao.model.NxDesign;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.NxUser;


/**
 * The persistent class for the NX_SOLUTION_DETAILS database table.
 * 
 */
@Entity
@Table(name="NX_SOLUTION_DETAILS")
@NamedQuery(name="NxSolutionDetail.findAll", query="SELECT n FROM NxSolutionDetail n")
public class NxSolutionDetail implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The nx solution id. */
	@Id
	@SequenceGenerator(name="NX_SOLUTION_DETAILS_NXSOLUTIONID_GENERATOR", sequenceName="SEQ_NX_SOLUTION_ID", allocationSize=1) //Uncomment this line after duplicate entity is deleted
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="NX_SOLUTION_DETAILS_NXSOLUTIONID_GENERATOR") //Uncomment this line after duplicate entity is deleted
	@Column(name="NX_SOLUTION_ID")
	private long nxSolutionId;

	/** The active yn. */
	@Column(name="ACTIVE_YN")
	private String activeYn;

	/** The created date. */
	@Column(name="CREATED_DATE")
	private Date createdDate = new Date();

	/** The created user. */
	@Column(name="CREATED_USER")
	private String createdUser;

	/** The customer name. */
	@Column(name="CUSTOMER_NAME")
	private String customerName;

	/** The duns number. */
	@Column(name="DUNS_NUMBER")
	private String dunsNumber;

	/** The external key. */
	@Column(name="EXTERNAL_KEY")
	private Long externalKey;

	/** The gu duns number. */
	@Column(name="GU_DUNS_NUMBER")
	private String guDunsNumber;

	/** The l 3 value. */
	@Column(name="L3_VALUE")
	private String l3Value;

	/** The l 4 value. */
	@Column(name="L4_VALUE")
	private String l4Value;

	/** The modified date. */
	@Column(name="MODIFIED_DATE")
	private Date modifiedDate = new Date();

	/** The modified user. */
	@Column(name="MODIFIED_USER")
	private String modifiedUser;

	/** The nxs description. */
	@Column(name="NXS_DESCRIPTION")
	private String nxsDescription;

	/** The opty id. */
	@Column(name="OPTY_ID")
	private String optyId;
	
	/** The opty id. */
	@Column(name="OPTY_LINKED_BY")
	private String optyLinkedBy;

	/** The flow type. */
	@Column(name="FLOW_TYPE")
	private String flowType;
	
	/** The Archived Solution ID. **/
	@Column(name="ARCHIVED_SOL_IND")
	private String archivedSolInd;
	
	@OneToMany(mappedBy = "nxSolutionDetail",cascade=CascadeType.ALL)
	private List<NxDesign> nxDesign;
	
	
	@Column(name="AUTOMATION_FLOW_IND") 
	private String automationFlowInd;
	 
	@Column(name="INR_STATUS_IND") 
	private String inrStatusInd;
	
	@Column(name="IGLOO_STATUS_IND") 
	private String iglooStatusInd;
	
	@Column(name="standard_Pricing_Ind") 
	private String standardPricingInd;
	
	@Column(name="REST_VERSION") 
	private String restVersion;
	
	@Column(name="PD_STATUS_IND") 
	private String pdStatusInd;
	
	@Column(name="IS_LOCKED") 
	private String isLocked;
	
	@Column(name="LOCKED_BY_USER") 
	private String lockedByUser;
	
	@Column(name="SLC_IND") 
	private String slcInd;
	

	@Column(name="SOLUTION_VERSION")
	private long solutionVersion;
	
	@Column(name="ATX_CALL") 
	private String atxCall;
	
	
	@ManyToMany
	@JoinTable(name = "NX_USER_SOLUTION", joinColumns = @JoinColumn(name = "SOLUTION_ID"), inverseJoinColumns = @JoinColumn(name = "USER_ID"))
	private Set<NxUser> users = new HashSet<>();
	
	
	public long getSolutionVersion() {
		return solutionVersion;
	}

	public void setSolutionVersion(long solutionVersion) {
		this.solutionVersion = solutionVersion;
	}

	public String getStandardPricingInd() {
		return standardPricingInd;
	}

	public void setStandardPricingInd(String standardPricingInd) {
		this.standardPricingInd = standardPricingInd;
	}

	/**
	 * Gets the flow type.
	 *
	 * @return the flow type
	 */
	public String getFlowType() {
		return flowType;
	}

	/**
	 * Sets the flow type.
	 *
	 * @param flowType the new flow type
	 */
	public void setFlowType(String flowType) {
		this.flowType = flowType;
	}

	/** The nx request details. */
	//bi-directional many-to-one association to nxRequestDetails
	@OneToMany(mappedBy="nxSolutionDetail",fetch = FetchType.EAGER)
	private Set<NxRequestDetails> nxRequestDetails = new HashSet();

	/** The nx teams. */
	//bi-directional many-to-one association to NxTeam
	@OneToMany(mappedBy="nxSolutionDetail")
	private Set<NxTeam> nxTeams;

	/**
	 * Gets the nx solution id.
	 *
	 * @return the nx solution id
	 */
	public long getNxSolutionId() {
		return this.nxSolutionId;
	}

	/**
	 * Sets the nx solution id.
	 *
	 * @param nxSolutionId the new nx solution id
	 */
	public void setNxSolutionId(long nxSolutionId) {
		this.nxSolutionId = nxSolutionId;
	}

	/**
	 * Gets the active yn.
	 *
	 * @return the active yn
	 */
	public String getActiveYn() {
		return this.activeYn;
	}

	/**
	 * Sets the active yn.
	 *
	 * @param activeYn the new active yn
	 */
	public void setActiveYn(String activeYn) {
		this.activeYn = activeYn;
	}

	/**
	 * Gets the created date.
	 *
	 * @return the created date
	 */
	public Date getCreatedDate() {
		return this.createdDate;
	}

	/**
	 * Sets the created date.
	 *
	 * @param createdDate the new created date
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * Gets the created user.
	 *
	 * @return the created user
	 */
	public String getCreatedUser() {
		return this.createdUser;
	}

	/**
	 * Sets the created user.
	 *
	 * @param createdUser the new created user
	 */
	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}

	/**
	 * Gets the customer name.
	 *
	 * @return the customer name
	 */
	public String getCustomerName() {
		return this.customerName;
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
		return this.dunsNumber;
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
	 * Gets the external key.
	 *
	 * @return the external key
	 */
	public Long getExternalKey() {
		return this.externalKey;
	}

	/**
	 * Sets the external key.
	 *
	 * @param externalKey the new external key
	 */
	public void setExternalKey(Long externalKey) {
		this.externalKey = externalKey;
	}


	/**
	 * Gets the gu duns number.
	 *
	 * @return the gu duns number
	 */
	public String getGuDunsNumber() {
		return this.guDunsNumber;
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
	 * Gets the l 3 value.
	 *
	 * @return the l 3 value
	 */
	public String getL3Value() {
		return this.l3Value;
	}

	/**
	 * Sets the l 3 value.
	 *
	 * @param l3Value the new l 3 value
	 */
	public void setL3Value(String l3Value) {
		this.l3Value = l3Value;
	}

	/**
	 * Gets the l 4 value.
	 *
	 * @return the l 4 value
	 */
	public String getL4Value() {
		return this.l4Value;
	}

	/**
	 * Sets the l 4 value.
	 *
	 * @param l4Value the new l 4 value
	 */
	public void setL4Value(String l4Value) {
		this.l4Value = l4Value;
	}


	/**
	 * Gets the modified date.
	 *
	 * @return the modified date
	 */
	public Date getModifiedDate() {
		return this.modifiedDate;
	}

	/**
	 * Sets the modified date.
	 *
	 * @param modifiedDate the new modified date
	 */
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	/**
	 * Gets the modified user.
	 *
	 * @return the modified user
	 */
	public String getModifiedUser() {
		return this.modifiedUser;
	}

	/**
	 * Sets the modified user.
	 *
	 * @param modifiedUser the new modified user
	 */
	public void setModifiedUser(String modifiedUser) {
		this.modifiedUser = modifiedUser;
	}

	/**
	 * Gets the nxs description.
	 *
	 * @return the nxs description
	 */
	public String getNxsDescription() {
		return this.nxsDescription;
	}

	/**
	 * Sets the nxs description.
	 *
	 * @param nxsDescription the new nxs description
	 */
	public void setNxsDescription(String nxsDescription) {
		this.nxsDescription = nxsDescription;
	}

	/**
	 * Gets the opty id.
	 *
	 * @return the opty id
	 */
	public String getOptyId() {
		return this.optyId;
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
	 * Gets the nx teams.
	 *
	 * @return the nx teams
	 */
	public Set<NxTeam> getNxTeams() {
		return this.nxTeams;
	}

	/**
	 * Gets the nx request details.
	 *
	 * @return the nx request details
	 */
	public Set<NxRequestDetails> getNxRequestDetails() {
		return nxRequestDetails;
	}

	/**
	 * Sets the nx request details.
	 *
	 * @param nxRequestDetails the new nx request details
	 */
	public void setNxRequestDetails(Set<NxRequestDetails> nxRequestDetails) {
		this.nxRequestDetails = nxRequestDetails;
	}
	
	public List<NxDesign> getNxDesign() {
		return nxDesign;
	}

	public void setNxDesign(List<NxDesign> nxDesign) {
		this.nxDesign = nxDesign;
	}

	/**
	 * Adds the nx request detail.
	 *
	 * @param nxRequestDetail the nx request detail
	 * @return the nx request details
	 */
	public NxRequestDetails addNxRequestDetail(NxRequestDetails nxRequestDetail) {
		getNxRequestDetails().add(nxRequestDetail);
		nxRequestDetail.setNxSolutionDetail(this);

		return nxRequestDetail;
	}

	/**
	 * Removes the nx request detail.
	 *
	 * @param nxRequestDetail the nx request detail
	 * @return the nx request details
	 */
	public NxRequestDetails removeNxRequestDetail(NxRequestDetails nxRequestDetail) {
		getNxRequestDetails().remove(nxRequestDetail);
		nxRequestDetail.setNxSolutionDetail(null);
		return nxRequestDetail;
	}

	/**
	 * Sets the nx teams.
	 *
	 * @param nxTeams the new nx teams
	 */
	public void setNxTeams(Set<NxTeam> nxTeams) {
		this.nxTeams = nxTeams;
	}

	/**
	 * Adds the nx team.
	 *
	 * @param nxTeam the nx team
	 * @return the nx team
	 */
	public NxTeam addNxTeam(NxTeam nxTeam) {
		getNxTeams().add(nxTeam);
		nxTeam.setNxSolutionDetail(this);

		return nxTeam;
	}

	/**
	 * Removes the nx team.
	 *
	 * @param nxTeam the nx team
	 * @return the nx team
	 */
	public NxTeam removeNxTeam(NxTeam nxTeam) {
		getNxTeams().remove(nxTeam);
		nxTeam.setNxSolutionDetail(null);

		return nxTeam;
	}

	public String getAutomationFlowInd() {
		return automationFlowInd;
	}

	public void setAutomationFlowInd(String automationFlowInd) {
		this.automationFlowInd = automationFlowInd;
	}

	/**
	 * @return the inrStatusInd
	 */
	public String getInrStatusInd() {
		return inrStatusInd;
	}

	/**
	 * @param inrStatusInd the inrStatusInd to set
	 */
	public void setInrStatusInd(String inrStatusInd) {
		this.inrStatusInd = inrStatusInd;
	}

	/**
	 * @return the iglooStatusInd
	 */
	public String getIglooStatusInd() {
		return iglooStatusInd;
	}

	/**
	 * @param iglooStatusInd the iglooStatusInd to set
	 */
	public void setIglooStatusInd(String iglooStatusInd) {
		this.iglooStatusInd = iglooStatusInd;
	}

	public String getRestVersion() {
		return restVersion;
	}

	public void setRestVersion(String restVersion) {
		this.restVersion = restVersion;
	}
	
	public Set<NxUser> getUsers() {
		return users;
	}

	public void setUsers(Set<NxUser> users) {
		this.users = users;
	}
	
	public String getAtxCall() {
		return atxCall;
	}

	public void setAtxCall(String atxCall) {
		this.atxCall = atxCall;
	}

	
	/**
	 * @return the archivedSolInd
	 */
	public String getArchivedSolInd() {
		return archivedSolInd;
	}
	
	/**
	 * @param archivedSolInd the archivedSolInd to set
	 */
	public void setArchivedSolInd(String archivedSolInd) {
		this.archivedSolInd = archivedSolInd;
	}

	public String getPdStatusInd() {
		return pdStatusInd;
	}

	public void setPdStatusInd(String pdStatusInd) {
		this.pdStatusInd = pdStatusInd;
	}
	
	public String getIsLocked() {
		return isLocked;
	}

	public void setIsLocked(String isLocked) {
		this.isLocked = isLocked;
	}
	
	public String getLockedByUser() {
		return lockedByUser;
	}

	public void setLockedByUser(String lockedByUser) {
		this.lockedByUser = lockedByUser;
	}

	public String getSlcInd() {
		return slcInd;
	}

	public void setSlcInd(String slcInd) {
		this.slcInd = slcInd;
	}
	
	public String getOptyLinkedBy() {
		return optyLinkedBy;
	}

	public void setOptyLinkedBy(String optyLinkedBy) {
		this.optyLinkedBy = optyLinkedBy;
	}
	
}