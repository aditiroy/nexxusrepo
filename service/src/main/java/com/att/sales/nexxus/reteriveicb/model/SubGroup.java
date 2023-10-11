package com.att.sales.nexxus.reteriveicb.model;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * The Class SubGroup.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class SubGroup {

	/** The sub group name. */
	private String subGroupName;
	
	/** The sub grp id. */
	private String subGrpId;
	
	/** The cat id. */
	private String catId;
	
	/** The sub catid. */
	private String subCatid;
	
	/** The sys dfnd. */
	private String sysDfnd;
	
	/** The usr id. */
	private String usrId;
	
	/** The crtd date. */
	private Date crtdDate;
	
	/** The version num. */
	private String versionNum;
	
	/** The actv YN. */
	private String actvYN;
	
	/**
	 * Gets the sub group name.
	 *
	 * @return the sub group name
	 */
	public String getSubGroupName() {
		return subGroupName;
	}
	
	/**
	 * Sets the sub group name.
	 *
	 * @param subGroupName the new sub group name
	 */
	public void setSubGroupName(String subGroupName) {
		this.subGroupName = subGroupName;
	}
	
	/**
	 * Gets the sub grp id.
	 *
	 * @return the sub grp id
	 */
	public String getSubGrpId() {
		return subGrpId;
	}
	
	/**
	 * Sets the sub grp id.
	 *
	 * @param subGrpId the new sub grp id
	 */
	public void setSubGrpId(String subGrpId) {
		this.subGrpId = subGrpId;
	}
	
	/**
	 * Gets the cat id.
	 *
	 * @return the cat id
	 */
	public String getCatId() {
		return catId;
	}
	
	/**
	 * Sets the cat id.
	 *
	 * @param catId the new cat id
	 */
	public void setCatId(String catId) {
		this.catId = catId;
	}
	
	/**
	 * Gets the sub catid.
	 *
	 * @return the sub catid
	 */
	public String getSubCatid() {
		return subCatid;
	}
	
	/**
	 * Sets the sub catid.
	 *
	 * @param subCatid the new sub catid
	 */
	public void setSubCatid(String subCatid) {
		this.subCatid = subCatid;
	}
	
	/**
	 * Gets the sys dfnd.
	 *
	 * @return the sys dfnd
	 */
	public String getSysDfnd() {
		return sysDfnd;
	}
	
	/**
	 * Sets the sys dfnd.
	 *
	 * @param sysDfnd the new sys dfnd
	 */
	public void setSysDfnd(String sysDfnd) {
		this.sysDfnd = sysDfnd;
	}
	
	/**
	 * Gets the usr id.
	 *
	 * @return the usr id
	 */
	public String getUsrId() {
		return usrId;
	}
	
	/**
	 * Sets the usr id.
	 *
	 * @param usrId the new usr id
	 */
	public void setUsrId(String usrId) {
		this.usrId = usrId;
	}
	
	/**
	 * Gets the crtd date.
	 *
	 * @return the crtd date
	 */
	public Date getCrtdDate() {
		return crtdDate;
	}
	
	/**
	 * Sets the crtd date.
	 *
	 * @param crtdDate the new crtd date
	 */
	public void setCrtdDate(Date crtdDate) {
		this.crtdDate = crtdDate;
	}
	
	/**
	 * Gets the version num.
	 *
	 * @return the version num
	 */
	public String getVersionNum() {
		return versionNum;
	}
	
	/**
	 * Sets the version num.
	 *
	 * @param versionNum the new version num
	 */
	public void setVersionNum(String versionNum) {
		this.versionNum = versionNum;
	}
	
	/**
	 * Gets the actv YN.
	 *
	 * @return the actv YN
	 */
	public String getActvYN() {
		return actvYN;
	}
	
	/**
	 * Sets the actv YN.
	 *
	 * @param actvYN the new actv YN
	 */
	public void setActvYN(String actvYN) {
		this.actvYN = actvYN;
	}
	
}
