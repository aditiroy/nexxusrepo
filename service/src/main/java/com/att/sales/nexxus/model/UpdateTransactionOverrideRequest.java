package com.att.sales.nexxus.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;



import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class UpdateTransactionOverrideRequest implements Serializable {

	private static final long serialVersionUID = -3607873083837824901L;

	private UpdateTransactionOverrideDocument documents;

	private String myPriceTransId;
	
	@JsonIgnore
	private Long nxAuditId;
	
	@JsonIgnore
	private String transType;
}