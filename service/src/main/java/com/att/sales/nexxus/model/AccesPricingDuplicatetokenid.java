package com.att.sales.nexxus.model;

import java.io.Serializable;
import java.util.List;

import com.att.sales.framework.validation.APIFieldProperty;

public class AccesPricingDuplicatetokenid  implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@APIFieldProperty(required=true)
	private List<String> duplicatetokenid;
	
	public List<String> getduplicatetokenid() {
		return duplicatetokenid;
	}
	

	public void setduplicatetokenid(List<String> duplicatetokenid) {
		this.duplicatetokenid = duplicatetokenid;
	}

}
