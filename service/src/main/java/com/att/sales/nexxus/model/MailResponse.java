package com.att.sales.nexxus.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import com.att.sales.framework.validation.APIFieldProperty;

import com.att.sales.framework.model.ServiceResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class MailResponse.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class MailResponse  extends ServiceResponse{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The coorelation id. */
	@APIFieldProperty(required=true, allowableValues="Coorelation Id Type String")
	private String coorelationId;
	
	/** The mail sent indicator. */
	@APIFieldProperty(required=true, allowableValues="Mail Sent Indicator Type String")
	private String mailSentIndicator;
	
	/**
	 * Gets the coorelation id.
	 *
	 * @return the coorelation id
	 */
	public String getCoorelationId() {
		return coorelationId;
	}
	
	/**
	 * Sets the coorelation id.
	 *
	 * @param coorelationId the new coorelation id
	 */
	public void setCoorelationId(String coorelationId) {
		this.coorelationId = coorelationId;
	}
	
	/**
	 * Gets the mail sent indicator.
	 *
	 * @return the mail sent indicator
	 */
	public String getMailSentIndicator() {
		return mailSentIndicator;
	}
	
	/**
	 * Sets the mail sent indicator.
	 *
	 * @param mailSentIndicator the new mail sent indicator
	 */
	public void setMailSentIndicator(String mailSentIndicator) {
		this.mailSentIndicator = mailSentIndicator;
	}
	
}
