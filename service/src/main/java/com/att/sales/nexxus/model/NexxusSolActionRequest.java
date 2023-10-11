package com.att.sales.nexxus.model;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
/**
 * @author sx623g
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
public class NexxusSolActionRequest  {
	// Request body fields for nexxusSolutionAction API 
	private String action;
	private Long solutionId;
	private String lockedByUser;
	private String actionPerformedBy;
}
