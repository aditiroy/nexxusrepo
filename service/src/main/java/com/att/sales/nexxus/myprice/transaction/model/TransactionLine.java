package com.att.sales.nexxus.myprice.transaction.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.model.ServiceResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class TransactionLine extends ServiceResponse {

	private static final long serialVersionUID = -4887912688832034402L;

	@JsonProperty("_line_bom_id")
	private String mpSolutionId;

	@JsonProperty("_model_product_line_id")
	private String mpProductLineId;

	@JsonProperty("_document_number")
	private Long documentNumber;
}