package com.att.sales.nexxus.myprice.transaction.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class MyPriceTransaction {
	
	@JsonProperty("rd_requestID_q")
	private String dealID;
	
	@JsonProperty("bs_id")
	private String myPriceTransacId;
	
	@JsonProperty("rd_revisionNumber_q")
	private String revision;
	
	@JsonProperty("version_t")
	private String version;

}
