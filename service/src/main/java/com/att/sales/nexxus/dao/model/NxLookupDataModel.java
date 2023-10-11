package com.att.sales.nexxus.dao.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;


import lombok.Getter;
import lombok.Setter;
/**
 * The persistent class for the NX_LOOKUP_DATA database table.
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
public class NxLookupDataModel implements Serializable {
	private static final long serialVersionUID = 1L;

	private String datasetName;
	private String itemId;
	private String description;
	private String criteria;
	private String active;
}
