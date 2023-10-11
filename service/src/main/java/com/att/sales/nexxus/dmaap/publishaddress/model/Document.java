package com.att.sales.nexxus.dmaap.publishaddress.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

/**
 * @author IndraSingh
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class Document implements Serializable{

	private static final long serialVersionUID = 1L;

	@JsonProperty("wi_siteStatusUpdate_q")
	private String wiStatusUpdateQ;
	
	
}
