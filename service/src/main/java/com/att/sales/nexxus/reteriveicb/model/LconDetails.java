package com.att.sales.nexxus.reteriveicb.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class LconDetails {
	
	private String lconType;
	private String lconFirstName;
	private String lconLastName;
	private String lconPhone;
	private String lconEmail;
	public String getLconType() {
		return lconType;
	}
	public void setLconType(String lconType) {
		this.lconType = lconType;
	}
	public String getLconFirstName() {
		return lconFirstName;
	}
	public void setLconFirstName(String lconFirstName) {
		this.lconFirstName = lconFirstName;
	}
	public String getLconLastName() {
		return lconLastName;
	}
	public void setLconLastName(String lconLastName) {
		this.lconLastName = lconLastName;
	}
	public String getLconPhone() {
		return lconPhone;
	}
	public void setLconPhone(String lconPhone) {
		this.lconPhone = lconPhone;
	}
	public String getLconEmail() {
		return lconEmail;
	}
	public void setLconEmail(String lconEmail) {
		this.lconEmail = lconEmail;
	}
	
	


}
