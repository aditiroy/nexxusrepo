package com.att.sales.nexxus.serviceValidation.model;

import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author KumariMuktta
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class ServiceValidationRequest implements Serializable {

	private static final long serialVersionUID = 3910219748813599981L;

	private Long transactionId;

	private String optyId;

	private Long dealId;

	private List<SiteDetails> siteDetails;

}
