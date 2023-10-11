package com.att.sales.nexxus.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class SolutionLockRequest {
	private String attuid;
	private String isLocked;
	private String nxSolutionId;
	
	public String getAttuid() {
		return attuid;
	}
	public void setAttuid(String attuid) {
		this.attuid = attuid;
	}
	public String getIsLocked() {
		return isLocked;
	}
	public void setIsLocked(String isLocked) {
		this.isLocked = isLocked;
	}
	public String getNxSolutionId() {
		return nxSolutionId;
	}
	public void setNxSolutionId(String nxSolutionId) {
		this.nxSolutionId = nxSolutionId;
	}
}
