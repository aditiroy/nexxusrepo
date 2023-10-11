package com.att.sales.nexxus.reteriveicb.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * The Class CustomDesignElement.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class CustomDesignElement {
	
	/** The custom design element id. */
	private Long customDesignElementId;
	
	/** The custom feature id. */
	private Long customFeatureId;
	
	/** The custom feature description. */
	private String customFeatureDescription;
	
	/** The custom features notes. */
	private String customFeaturesNotes;
	
	/** The custom item quantity. */
	private Long customItemQuantity;
	
	/**
	 * Gets the custom design element id.
	 *
	 * @return the custom design element id
	 */
	public Long getCustomDesignElementId() {
		return customDesignElementId;
	}
	
	/**
	 * Sets the custom design element id.
	 *
	 * @param customDesignElementId the new custom design element id
	 */
	public void setCustomDesignElementId(Long customDesignElementId) {
		this.customDesignElementId = customDesignElementId;
	}
	
	/**
	 * Gets the custom feature id.
	 *
	 * @return the custom feature id
	 */
	public Long getCustomFeatureId() {
		return customFeatureId;
	}
	
	/**
	 * Sets the custom feature id.
	 *
	 * @param customFeatureId the new custom feature id
	 */
	public void setCustomFeatureId(Long customFeatureId) {
		this.customFeatureId = customFeatureId;
	}
	
	/**
	 * Gets the custom feature description.
	 *
	 * @return the custom feature description
	 */
	public String getCustomFeatureDescription() {
		return customFeatureDescription;
	}
	
	/**
	 * Sets the custom feature description.
	 *
	 * @param customFeatureDescription the new custom feature description
	 */
	public void setCustomFeatureDescription(String customFeatureDescription) {
		this.customFeatureDescription = customFeatureDescription;
	}
	
	/**
	 * Gets the custom features notes.
	 *
	 * @return the custom features notes
	 */
	public String getCustomFeaturesNotes() {
		return customFeaturesNotes;
	}
	
	/**
	 * Sets the custom features notes.
	 *
	 * @param customFeaturesNotes the new custom features notes
	 */
	public void setCustomFeaturesNotes(String customFeaturesNotes) {
		this.customFeaturesNotes = customFeaturesNotes;
	}
	
	/**
	 * Gets the custom item quantity.
	 *
	 * @return the custom item quantity
	 */
	public Long getCustomItemQuantity() {
		return customItemQuantity;
	}
	
	/**
	 * Sets the custom item quantity.
	 *
	 * @param customItemQuantity the new custom item quantity
	 */
	public void setCustomItemQuantity(Long customItemQuantity) {
		this.customItemQuantity = customItemQuantity;
	}
	
}
