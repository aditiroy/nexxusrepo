package com.att.sales.nexxus.myprice.transaction.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 
 * @author Laxman Honawad
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Data
@NoArgsConstructor
@ToString
public class UpdateTxnSiteUploadFieldedAddress implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	private String singleLineStandardizedAddress;
	
	private String country;
	
	private String city;
	
	private String postalCode;
	
	@JsonProperty("postalCodePlus4")
	private String postalCodePlus;
	
	private String state;
	
	private String unitValue;
	
	private String structureValue;
	
	private String levelValue;
	
}
