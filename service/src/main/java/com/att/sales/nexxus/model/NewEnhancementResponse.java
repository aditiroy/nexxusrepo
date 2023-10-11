package com.att.sales.nexxus.model;
import com.att.sales.framework.model.ServiceResponse;

public class NewEnhancementResponse extends ServiceResponse{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String enhancements;
	
	public String getEnhancements() {
		return enhancements;
	}
	public void setEnhancements(String enhancements) {
		this.enhancements = enhancements;
	}
}
