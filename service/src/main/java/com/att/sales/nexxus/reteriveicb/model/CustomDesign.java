package com.att.sales.nexxus.reteriveicb.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * The Class CustomDesign.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class CustomDesign {
	
	/** The design details. */
	private List<UDFBaseData> designDetails;
	
	/** The custom design element list. */
	private List<CustomDesignElement> customDesignElementList;
	

	/**
	 * Gets the design details.
	 *
	 * @return the design details
	 */
	public List<UDFBaseData> getDesignDetails() {
		return designDetails;
	}



	/**
	 * Sets the design details.
	 *
	 * @param designDetails the new design details
	 */
	public void setDesignDetails(List<UDFBaseData> designDetails) {
		this.designDetails = designDetails;
	}

	/**
	 * Gets the custom design element list.
	 *
	 * @return the custom design element list
	 */
	public List<CustomDesignElement> getCustomDesignElementList() {
		return customDesignElementList;
	}



	/**
	 * Sets the custom design element list.
	 *
	 * @param customDesignElementList the new custom design element list
	 */
	public void setCustomDesignElementList(List<CustomDesignElement> customDesignElementList) {
		this.customDesignElementList = customDesignElementList;
	}

}
