/**
 * 
 */
package com.att.sales.nexxus.serviceValidation.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

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
@JsonNaming(value=UpperCamelCaseStrategy.class)
@JsonFilter("avsqLocationFilter")
public class Location {
	
	private LocationOptions locationOptions;
	
	private ValidationOptions validationOptions;
	
	@JsonProperty("GISLocationAttributes")
	private List<GISLocationAttributes> gisLocationAttributes; 
	@JsonProperty("SAGLocationAttributes")
	private List<SAGLocationAttributes> sagLocationAttributes;
	@JsonProperty("LocationNetworkAttributes")
	private LocationNetwokAttributes locationNetworkAttributes;

}
