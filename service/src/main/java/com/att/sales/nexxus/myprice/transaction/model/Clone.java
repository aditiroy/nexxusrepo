/**
 * 
 */
package com.att.sales.nexxus.myprice.transaction.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * @author sj0546
 *
 */
@Getter
@Setter
public class Clone {
	
	@JsonProperty("Action")
	private String action;
	private String sourceId;

}
