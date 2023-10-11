package com.att.sales.nexxus.model;

import com.att.sales.nexxus.common.APIFieldProperty;

/**
 * The Class DroolsFileDataRequest.
 *
 * @author 
 */
public class DroolsFileDataRequest {

	/** The activity. */
	private String activity;
	
	/** The type of data. */
	@APIFieldProperty(required=true)
	private String typeOfData;

	
	
	/**
	 * Gets the activity.
	 *
	 * @return the activity
	 */
	
	public String getActivity() {
		return activity;
	}
	
	/**
	 * Sets the activity.
	 *
	 * @param activity the new activity
	 * @return the activity
	 */

	public void setActivity(String activity) {
		this.activity = activity;
	}
	
	/**
	 * Gets the type of data.
	 *
	 * @return the typeofdata
	 */


	public String getTypeOfData() {
		return typeOfData;
	}

	/**
	 * Sets the type of data.
	 *
	 * @param typeOfData the new type of data
	 * @return the typeofdata
	 */
	public void setTypeOfData(String typeOfData) {
		this.typeOfData = typeOfData;
	}
	




	

}
