package com.att.sales.nexxus.custompricing.model;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Credit {

	private Long creditId;
	
	private Long productId;
	
	private long productNumber;
	
	private List<PriceScenario> PriceScenarios;
	
	
}
