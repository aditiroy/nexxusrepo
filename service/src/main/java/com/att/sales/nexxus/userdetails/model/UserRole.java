package com.att.sales.nexxus.userdetails.model;

import java.io.Serializable;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class UserRole.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class UserRole implements Serializable {


	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The role id. */
	private Long roleId;
	
	/** The role name. */
	private String roleName;
	
	/** The priority. */
	private Long priority;
	
	/**
	 * Gets the role id.
	 *
	 * @return the role id
	 */
	public Long getRoleId() {
		return roleId;
	}
	
	/**
	 * Sets the role id.
	 *
	 * @param roleId the new role id
	 */
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
	
	/**
	 * Gets the role name.
	 *
	 * @return the role name
	 */
	public String getRoleName() {
		return roleName;
	}
	
	/**
	 * Sets the role name.
	 *
	 * @param roleName the new role name
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	
	/**
	 * Gets the priority.
	 *
	 * @return the priority
	 */
	public Long getPriority() {
		return priority;
	}
	
	/**
	 * Sets the priority.
	 *
	 * @param priority the new priority
	 */
	public void setPriority(Long priority) {
		this.priority = priority;
	}
	 


}
