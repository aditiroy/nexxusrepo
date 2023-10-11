package com.att.sales.nexxus.model;


import com.att.sales.framework.model.ServiceResponse;

/**
 * The Class ExampleBean.
 */
public class ExampleBean extends ServiceResponse {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The field 1. */
	private String field1;
	
	/**
	 * Gets the field 1.
	 *
	 * @return the field 1
	 */
	public String getField1() {
		return field1;
	}

	/**
	 * Sets the field 1.
	 *
	 * @param field1 the new field 1
	 */
	public void setField1(String field1) {
		this.field1 = field1;
	}

	/**
	 * Instantiates a new example bean.
	 */
	public ExampleBean() {
		
	}

	/**
	 * Instantiates a new example bean.
	 *
	 * @param field1 the field 1
	 */
	public ExampleBean(String field1) {
		
		this.field1=field1;
	}

	

}
