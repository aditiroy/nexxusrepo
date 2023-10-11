package com.att.sales.nexxus.rateletter.model;

import java.io.Serializable;

/*
 * @author Ruchi Yadav
 * 
 */

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.validation.APIFieldProperty;
import com.att.sales.nexxus.reteriveicb.model.NexxusMessage;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class RateLetterStatusRequest implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String optyId;

	@APIFieldProperty(required=true)
	private String dealId;

	@APIFieldProperty(required=true)
	private String cpqId;

	private String customerName;

	private String dealVersion;

	private String dealRevisionNumber;

	private String dealStatus;

	private String externalSystemKey;

	private String externalSystemName;
	
	private Long priceScenarioId;
	
	private String quoteType;

	private String rlExpirationDate;
	
	private String rlQuoteUrl;
	
	@APIFieldProperty(allowableValues="mobility,wireline,integrated")
	private String offer;
	
	private String pricingManager;
	
	private String dealDescription;
	
	private String svId;
	
	private String ipeIndicator;
	
	private String costCompletedStatus;
	
	/** The nexxus message. */
	private NexxusMessage message;
	
}
