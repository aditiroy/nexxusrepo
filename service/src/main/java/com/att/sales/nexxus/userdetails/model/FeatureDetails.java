package com.att.sales.nexxus.userdetails.model;

import java.io.Serializable;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * The Class FeatureDetails.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class FeatureDetails implements Serializable {


	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	private String featureId;
	private String featureName;
	
	
	
	public FeatureDetails( String featureId,String featureName) {
		super();
		this.featureName = featureName;
		this.featureId = featureId;
	}
	public String getFeatureName() {
		return featureName;
	}
	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}
	public String getFeatureId() {
		return featureId;
	}
	public void setFeatureId(String featureId) {
		this.featureId = featureId;
	}
	
	
	
}
