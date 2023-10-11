package com.att.sales.nexxus.reteriveicb.model;


import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * @author Ruchi
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class Products {
	
	private Long productNumber;
	private String jurisdiction;
	private String region;
	private String state;

	private List<PriceScenarios> priceScenarios;

}
