package com.att.sales.nexxus.myprice.transaction.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class TransLineItems {
	
	@JsonProperty("_line_bom_id")
	private String lineBomId;
	
	@JsonProperty("_document_number")
	private String documentNumber;
	
	

}
