package com.att.sales.nexxus.userdetails.model;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.model.ServiceResponse;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class CheckAccessResponse extends ServiceResponse {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	private String applicationProfile;
	private List<FeatureDetails> features;

	public String getApplicationProfile() {
		return applicationProfile;
	}

	public void setApplicationProfile(String applicationProfile) {
		this.applicationProfile = applicationProfile;
	}

	public List<FeatureDetails> getFeatures() {
		return features;
	}

	public void setFeatures(List<FeatureDetails> features) {
		this.features = features;
	}

	
	
	
}

