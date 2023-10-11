package com.att.sales.nexxus.myprice.transaction.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class TransactionPricingReqCriteria {

	@JsonProperty("limit")
	private Integer limit;

	@JsonProperty("fields")
	private List<String> fields;

}
