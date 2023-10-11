/**
 * 
 */
package com.att.sales.nexxus.model;

import java.io.Serializable;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class RetrieveAdminDataRequest.
 *
 * @author RudreshWaladaunki
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class RetrieveAdminDataRequest implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The action. */
	@JsonProperty("action")
	private String action;

	/**
	 * Gets the action.
	 *
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * Sets the action.
	 *
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

}
