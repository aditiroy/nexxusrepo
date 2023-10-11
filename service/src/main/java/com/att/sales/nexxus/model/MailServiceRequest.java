package com.att.sales.nexxus.model;

import java.io.Serializable;

import com.att.sales.framework.validation.APIFieldProperty;

/**
 * The Class MailServiceRequest.
 */
public class MailServiceRequest implements Serializable {

	/** aa316k. */
	private static final long serialVersionUID = 1L;
	
	/** The mailsource. */
	@APIFieldProperty(required=true)
	private String mailsource;
	
	/** The from mail list. */
	@APIFieldProperty(required=true)	
	private String fromMailList;
	
	/** The to mail list. */
	@APIFieldProperty(required=true)
	private String toMailList;
	
	/** The email sub. */
	@APIFieldProperty(required=true)
	private String emailSub;
	
	/** The email body. */
	@APIFieldProperty(required=true)
	private String emailBody;
	
	/** The coorelation id. */
	@APIFieldProperty(required=true)
	private Long coorelationId;
	
	

	/**
	 * Gets the mailsource.
	 *
	 * @return the mailsource
	 */
	public String getMailsource() {
		return mailsource;
	}

	/**
	 * Sets the mailsource.
	 *
	 * @param mailsource the new mailsource
	 */
	public void setMailsource(String mailsource) {
		this.mailsource = mailsource;
	}


	/**
	 * Gets the from mail list.
	 *
	 * @return the from mail list
	 */
	public String getFromMailList() {
		return fromMailList;
	}

	/**
	 * Sets the from mail list.
	 *
	 * @param fromMailList the new from mail list
	 */
	public void setFromMailList(String fromMailList) {
		this.fromMailList = fromMailList;
	}

	
	/**
	 * Gets the to mail list.
	 *
	 * @return the to mail list
	 */
	public String getToMailList() {
		return toMailList;
	}

	/**
	 * Sets the to mail list.
	 *
	 * @param toMailList the new to mail list
	 */
	public void setToMailList(String toMailList) {
		this.toMailList = toMailList;
	}

	/**
	 * Gets the email sub.
	 *
	 * @return the email sub
	 */
	public String getEmailSub() {
		return emailSub;
	}

	/**
	 * Sets the email sub.
	 *
	 * @param emailSub the new email sub
	 */
	public void setEmailSub(String emailSub) {
		this.emailSub = emailSub;
	}

	/**
	 * Gets the email body.
	 *
	 * @return the email body
	 */
	public String getEmailBody() {
		return emailBody;
	}

	/**
	 * Sets the email body.
	 *
	 * @param emailBody the new email body
	 */
	public void setEmailBody(String emailBody) {
		this.emailBody = emailBody;
	}


	/**
	 * Gets the coorelation id.
	 *
	 * @return the coorelation id
	 */
	public Long getCoorelationId() {
		return coorelationId;
	}

	/**
	 * Sets the coorelation id.
	 *
	 * @param coorelationId the new coorelation id
	 */
	public void setCoorelationId(Long coorelationId) {
		this.coorelationId = coorelationId;
	}
	
	
}
