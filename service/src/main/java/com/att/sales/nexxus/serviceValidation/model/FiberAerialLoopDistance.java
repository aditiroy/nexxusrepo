/**
 * 
 */
package com.att.sales.nexxus.serviceValidation.model;

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
public class FiberAerialLoopDistance {
	
	private Double quantity;
	private String unit;

	@Override
	public String toString() {
		return "FiberAerialLoopDistance [quantity=" + quantity + ", unit=" + unit + "]";
	}
	
	

}
