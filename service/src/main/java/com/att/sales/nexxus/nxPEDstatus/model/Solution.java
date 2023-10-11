package com.att.sales.nexxus.nxPEDstatus.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @author Laxman Honawad
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class Solution {

	private String solutionId;

	private String offerId;

	@Autowired
	private List<SolutionDetails> solutionDetails;
}
