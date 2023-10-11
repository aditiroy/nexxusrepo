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
public class UpdateTxnSiteUploadDocuments implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	@JsonProperty("integrationSiteDict1")
	private String integrationSiteDict;
}
