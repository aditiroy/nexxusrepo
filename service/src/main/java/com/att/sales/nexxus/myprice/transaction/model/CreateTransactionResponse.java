/**
 * 
 */
package com.att.sales.nexxus.myprice.transaction.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import lombok.Getter;
import lombok.Setter;

/**
 * @author ShruthiCJ
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
public class CreateTransactionResponse extends MyPriceTransaction implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Boolean success;
	private Long nxTransacId;
	private String offerName;
	private Long priceScenarioId;

}
