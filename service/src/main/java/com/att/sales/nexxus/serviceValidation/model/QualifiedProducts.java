/**
 * 
 */
package com.att.sales.nexxus.serviceValidation.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;

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
public class QualifiedProducts {
	
	@JsonProperty("ASE")
	private ASE ase;
	@JsonProperty("DataProducts")
	private DataProducts dataProducts;
	
	@Override
	public String toString() {
		return "QualifiedProducts [ase=" + ase + "]" +"[dataProducts=" +dataProducts+"]";
	}
}
