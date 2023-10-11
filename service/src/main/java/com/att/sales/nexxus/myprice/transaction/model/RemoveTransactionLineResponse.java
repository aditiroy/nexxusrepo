package com.att.sales.nexxus.myprice.transaction.model;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.model.ServiceResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @author Laxman Honawad
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class RemoveTransactionLineResponse extends ServiceResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("quoteURL_q")
	private String quoteUrl;
	
}
