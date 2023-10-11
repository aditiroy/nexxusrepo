package com.att.sales.nexxus.dao.model.solution;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the NX_TEAM database table.
 * 
 * 
 */
@Entity
@Table(name="NX_TEAM")
@NamedQueries({
@NamedQuery(name="NxTeam.findAll", query="SELECT n FROM NxTeam n")
})
public class NxTeam implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The nx team id. */
	@SequenceGenerator(name="sequence_nx_team",sequenceName="SEQ_NX_TEAM_ID", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="sequence_nx_team")
	@Id
	@Column(name="NX_TEAM_ID")
	private long nxTeamId;
	
	/** The attuid. */
	@Column(name="ATTUID")
	private String attuid;
	
	
	/** The nx solution detail. */
	//bi-directional many-to-one association to NxSolutionDetail
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="NX_SOLUTION_ID")
	private NxSolutionDetail nxSolutionDetail;
	
	/** The name. */
	@Column(name="F_NAME")
	private String fName;
	
	/** The l name. */
	@Column(name="L_NAME")
	private String lName;
	
	/** The sales rep full name. */
	@Column(name="SALES_REP_FULL_NAME")
	private String salesRepFullName;
	
	/** The email. */
	@Column(name="EMAIL")
	private String email;
	
	/** The manager name. */
	@Column(name="MANAGER_NAME")
	private String managerName;
	
	/** The manager hrid. */
	@Column(name="MANAGER_HRID")
	private String managerHrid;
	
	/** The is pry MVG. */
	@Column(name="IS_PRY_MVG")
	private String isPryMVG;

	/**
	 * Instantiates a new nx team.
	 */
	public NxTeam() {
		//
	}
	

	/**
	 * Gets the f name.
	 *
	 * @return the fName
	 */
	public String getfName() {
		return fName;
	}



	/**
	 * Sets the f name.
	 *
	 * @param fName the fName to set
	 */
	public void setfName(String fName) {
		this.fName = fName;
	}



	/**
	 * Gets the l name.
	 *
	 * @return the lName
	 */
	public String getlName() {
		return lName;
	}



	/**
	 * Sets the l name.
	 *
	 * @param lName the lName to set
	 */
	public void setlName(String lName) {
		this.lName = lName;
	}



	/**
	 * Gets the sales rep full name.
	 *
	 * @return the salesRepFullName
	 */
	public String getSalesRepFullName() {
		return salesRepFullName;
	}



	/**
	 * Sets the sales rep full name.
	 *
	 * @param salesRepFullName the salesRepFullName to set
	 */
	public void setSalesRepFullName(String salesRepFullName) {
		this.salesRepFullName = salesRepFullName;
	}



	/**
	 * Gets the email.
	 *
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}



	/**
	 * Sets the email.
	 *
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}



	/**
	 * Gets the manager name.
	 *
	 * @return the salesRep
	 */



	/**
	 * @return the managerName
	 */
	public String getManagerName() {
		return managerName;
	}



	/**
	 * Sets the manager name.
	 *
	 * @param managerName the managerName to set
	 */
	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}



	/**
	 * Gets the manager hrid.
	 *
	 * @return the managerHrid
	 */
	public String getManagerHrid() {
		return managerHrid;
	}



	/**
	 * Sets the manager hrid.
	 *
	 * @param managerHrid the managerHrid to set
	 */
	public void setManagerHrid(String managerHrid) {
		this.managerHrid = managerHrid;
	}



	/**
	 * Gets the checks if is pry MVG.
	 *
	 * @return the isPryMVG
	 */
	public String getIsPryMVG() {
		return isPryMVG;
	}



	/**
	 * Sets the checks if is pry MVG.
	 *
	 * @param isPryMVG the isPryMVG to set
	 */
	public void setIsPryMVG(String isPryMVG) {
		this.isPryMVG = isPryMVG;
	}



	/**
	 * Gets the nx team id.
	 *
	 * @return the nx team id
	 */
	public long getNxTeamId() {
		return this.nxTeamId;
	}

	/**
	 * Sets the nx team id.
	 *
	 * @param nxTeamId the new nx team id
	 */
	public void setNxTeamId(long nxTeamId) {
		this.nxTeamId = nxTeamId;
	}

	/**
	 * Gets the attuid.
	 *
	 * @return the attuid
	 */
	public String getAttuid() {
		return this.attuid;
	}

	/**
	 * Sets the attuid.
	 *
	 * @param attuid the new attuid
	 */
	public void setAttuid(String attuid) {
		this.attuid = attuid;
	}

	/**
	 * Gets the nx solution detail.
	 *
	 * @return the nx solution detail
	 */
	public NxSolutionDetail getNxSolutionDetail() {
		return this.nxSolutionDetail;
	}

	/**
	 * Sets the nx solution detail.
	 *
	 * @param nxSolutionDetail the new nx solution detail
	 */
	public void setNxSolutionDetail(NxSolutionDetail nxSolutionDetail) {
		this.nxSolutionDetail = nxSolutionDetail;
	}

}