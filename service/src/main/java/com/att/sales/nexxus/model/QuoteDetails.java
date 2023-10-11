package com.att.sales.nexxus.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.validation.APIFieldProperty;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class QuoteDetails.
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class QuoteDetails {

/** The ap id. */
@APIFieldProperty(required=true)
private Long apId;

/** The location yn. */
@APIFieldProperty(required=true)
private String locationYn;

/** The include yn. */
@APIFieldProperty(required=true)
private String includeYn;

/**
 * Gets the ap id.
 *
 * @return the ap id
 */
@JsonProperty("apId")
public Long getApId() {
	return apId;
}

/**
 * Sets the ap id.
 *
 * @param apId the new ap id
 */
public void setApId(Long apId) {
	this.apId = apId;
}

/**
 * Gets the location yn.
 *
 * @return the location yn
 */
@JsonProperty("locationYn")
public String getLocationYn() {
	return locationYn;
}

/**
 * Sets the location yn.
 *
 * @param locationYn the new location yn
 */
public void setLocationYn(String locationYn) {
	this.locationYn = locationYn;
}

/**
 * Gets the include yn.
 *
 * @return the include yn
 */
@JsonProperty("includeYn")
public String getIncludeYn() {
	return includeYn;
}

/**
 * Sets the include yn.
 *
 * @param includeYn the new include yn
 */
public void setIncludeYn(String includeYn) {
	this.includeYn = includeYn;
}


}
