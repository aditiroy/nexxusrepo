package com.att.sales.nexxus.model;


import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The Class DesignDetails.
 *
 * @author km017g
 */
@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DesignDetails implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The udf id. */
	private String udfId;
	
	/** The udf value. */
	private String udfValue;
	
	/** The udf attributes. */
	private transient List<UdfAttributes> udfAttributes;

	/**
	 * Gets the udf id.
	 *
	 * @return the udf id
	 */
	public String getUdfId() {
		return udfId;
	}

	/**
	 * Sets the udf id.
	 *
	 * @param udfId the new udf id
	 */
	public void setUdfId(String udfId) {
		this.udfId = udfId;
	}

	/**
	 * Gets the udf value.
	 *
	 * @return the udf value
	 */
	public String getUdfValue() {
		return udfValue;
	}

	/**
	 * Sets the udf value.
	 *
	 * @param udfValue the new udf value
	 */
	public void setUdfValue(String udfValue) {
		this.udfValue = udfValue;
	}

	/**
	 * Gets the udf attributes.
	 *
	 * @return the udf attributes
	 */
	public List<UdfAttributes> getUdfAttributes() {
		return udfAttributes;
	}

	/**
	 * Sets the udf attributes.
	 *
	 * @param udfAttributes the new udf attributes
	 */
	public void setUdfAttributes(List<UdfAttributes> udfAttributes) {
		this.udfAttributes = udfAttributes;
	}

	/**
	 * Gets the serialversionuid.
	 *
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UDFDetails [udfId=" + udfId + ", udfValue=" + udfValue + ", udfAttributes=" + udfAttributes + "]";
	}

}
